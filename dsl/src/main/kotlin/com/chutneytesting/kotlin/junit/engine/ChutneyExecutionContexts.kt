package com.chutneytesting.kotlin.junit.engine

import com.chutneytesting.engine.domain.execution.RxBus
import com.chutneytesting.engine.domain.execution.engine.step.Step
import com.chutneytesting.engine.domain.execution.event.BeginStepExecutionEvent
import com.chutneytesting.engine.domain.execution.event.EndScenarioExecutionEvent
import com.chutneytesting.engine.domain.execution.event.EndStepExecutionEvent
import com.chutneytesting.engine.domain.execution.event.StartScenarioExecutionEvent
import com.chutneytesting.engine.domain.execution.report.Status
import com.chutneytesting.environment.domain.exception.EnvironmentNotFoundException
import com.chutneytesting.kotlin.dsl.ChutneyStep
import com.chutneytesting.kotlin.dsl.ChutneyStepImpl
import com.chutneytesting.kotlin.dsl.Strategy
import com.chutneytesting.kotlin.execution.CannotResolveDefaultEnvironmentException
import com.chutneytesting.kotlin.execution.ExecutionService
import com.chutneytesting.kotlin.execution.report.JsonReportWriter
import com.chutneytesting.kotlin.junit.engine.ChutneyConfigurationParameters.CONFIG_ENGINE_STEP_AS_TEST
import com.chutneytesting.kotlin.junit.engine.ChutneyConfigurationParameters.CONFIG_ENVIRONMENT
import com.chutneytesting.kotlin.junit.engine.ChutneyEngineExecutionContext.ListenerEvent.*
import com.chutneytesting.kotlin.junit.engine.ChutneyJUnitReportingKeys.REPORT_JSON_STRING
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.junit.platform.engine.*
import org.junit.platform.engine.TestExecutionResult.successful
import org.junit.platform.engine.reporting.ReportEntry
import org.junit.platform.engine.support.descriptor.MethodSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

enum class ChutneyConfigurationParameters(val parameter: String) {
    CONFIG_ENVIRONMENT("junit.chutney.environment"),
    CONFIG_ENGINE_STEP_AS_TEST("junit.chutney.engine.stepAsTest"),
    CONFIG_REPORT_ROOT_PATH("junit.chutney.report.rootPath"),
    CONFIG_REPORT_FILE("junit.chutney.report.file.enabled"),
    CONFIG_REPORT_SITE("junit.chutney.report.site.enabled"),
    CONFIG_CONSOLE_LOG_COLOR("junit.chutney.log.color.enabled"),
    CONFIG_SCENARIO_LOG("junit.chutney.log.scenario.enabled"),
    CONFIG_STEP_LOG("junit.chutney.log.step.enabled")
}

enum class ChutneyJUnitReportingKeys(val value: String) {
    REPORT_JSON_STRING("chutney.report"),
    REPORT_STEP_JSON_STRING("chutney.report.step")
}

class ChutneyEngineExecutionContext(val request: ExecutionRequest) {
    val executionService = ExecutionService()
    val configurationParameters: ConfigurationParameters = SystemEnvConfigurationParameters(request.configurationParameters)

    private val scenarioExecutions = HashMap<Long, ChutneyScenarioExecutionContext>()
    private val syncExecutionSemaphore: Semaphore = Semaphore(1)

    private val endExecutionLatch: CountDownLatch = CountDownLatch(request.rootTestDescriptor.children.size)
    private val stepAsTest: Boolean = configurationParameters.getBoolean(CONFIG_ENGINE_STEP_AS_TEST.parameter).orElse(true)

    private val startScenarioDisposable: Disposable
    private val beginStepDisposable: Disposable
    private val endStepDisposable: Disposable
    private val endScenarioDisposable: Disposable

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    init {
        val chutneyBus = RxBus.getInstance()
        startScenarioDisposable =
            chutneyBus.register(StartScenarioExecutionEvent::class.java, this::startScenarioExecution)

        if (stepAsTest) {
            beginStepDisposable = chutneyBus.register(BeginStepExecutionEvent::class.java, this::beginStepExecution)
            endStepDisposable = chutneyBus.register(EndStepExecutionEvent::class.java, this::endStepExecution)
        } else {
            beginStepDisposable = Observable.empty<BeginStepExecutionEvent>().subscribe()
            endStepDisposable = Observable.empty<BeginStepExecutionEvent>().subscribe()
        }

        endScenarioDisposable = chutneyBus.register(EndScenarioExecutionEvent::class.java, this::endScenarioExecution)
    }

    fun execute() {
        startExecution()
        try {
            request.rootTestDescriptor.children
                .filterIsInstance<ChutneyClassDescriptor>()
                .forEach {
                    executeClass(it)
                }
        } finally {
            endExecution()
        }
    }

    fun addScenarioExecution(executionId: Long, chutneyScenarioExecutionContext: ChutneyScenarioExecutionContext) {
        scenarioExecutions[executionId] = chutneyScenarioExecutionContext
    }

    fun endExecutionLatch() {
        syncExecutionSemaphore.release()
        endExecutionLatch.countDown()
    }

    enum class ListenerEvent { STARTED, SKIPPED, FINISHED, REPORT, DYNAMIC }

    fun notifyJUnitListener(
        event: ListenerEvent,
        testDescriptor: TestDescriptor,
        testResult: TestExecutionResult? = null,
        reportEntry: ReportEntry? = null,
        reason: String? = null
    ) {
        try {
            when (event) {
                STARTED -> request.engineExecutionListener.executionStarted(testDescriptor)
                SKIPPED -> request.engineExecutionListener.executionSkipped(testDescriptor, reason)
                FINISHED -> request.engineExecutionListener.executionFinished(testDescriptor, testResult)
                REPORT -> request.engineExecutionListener.reportingEntryPublished(testDescriptor, reportEntry)
                DYNAMIC -> request.engineExecutionListener.dynamicTestRegistered(testDescriptor)
            }
        } catch (e: Exception) {
            logger.warn("Notification failed", e)
        }
    }

    private fun executeClass(classDescriptor: ChutneyClassDescriptor) {
        syncExecutionSemaphore.acquire()

        val chutneyClassExecutionContext = ChutneyClassExecutionContext(this, classDescriptor)
        chutneyClassExecutionContext.execute()
    }

    private fun startExecution() {
        notifyJUnitListener(STARTED, request.rootTestDescriptor)
    }

    private fun endExecution() {
        endExecutionLatch.await()
        unregisterRxBus()

        notifyJUnitListener(FINISHED, request.rootTestDescriptor, successful())
    }

    private fun startScenarioExecution(event: StartScenarioExecutionEvent) {
        awaitForScenarioExecutionIdContextMapping(event)

        checkExecutionThen(event.executionId()) {
            it.startExecution(event.step)
        }
    }

    private fun beginStepExecution(event: BeginStepExecutionEvent) {
        checkExecutionThen(event.executionId()) {
            it.beginStepExecution(event.step)
        }
    }

    private fun endStepExecution(event: EndStepExecutionEvent) {
        checkExecutionThen(event.executionId()) {
            it.endStepExecution(event.step)
        }
    }

    private fun endScenarioExecution(event: EndScenarioExecutionEvent) {
        checkExecutionThen(event.executionId()) {
            it.endExecution(event.step)
        }
    }

    private fun checkExecutionThen(executionId: Long, block: (ChutneyScenarioExecutionContext) -> Unit) {
        scenarioExecutions[executionId]?.apply(block)
            ?: throw IllegalStateException("Cannot find execution [${executionId}]")
    }

    private fun awaitForScenarioExecutionIdContextMapping(event: StartScenarioExecutionEvent) {
        while (true) {
            if (scenarioExecutions[event.executionId()] != null) break
            TimeUnit.MILLISECONDS.sleep(50)
        }
    }

    private fun unregisterRxBus() {
        startScenarioDisposable.dispose()
        beginStepDisposable.dispose()
        endStepDisposable.dispose()
        endScenarioDisposable.dispose()
    }
}

class ChutneyClassExecutionContext(
    val engineExecutionContext: ChutneyEngineExecutionContext,
    private val chutneyClassDescriptor: ChutneyClassDescriptor
) {
    private val scenarioExecutionContexts: MutableSet<ChutneyScenarioExecutionContext> = HashSet()
    private val syncExecutionSemaphore: Semaphore = Semaphore(1)
    private val endExecutionLatch: CountDownLatch = CountDownLatch(chutneyClassDescriptor.children.size)

    fun execute() {
        startExecution()
        try {
            chutneyClassDescriptor.children
                .filterIsInstance<ChutneyScenarioDescriptor>()
                .forEach {
                    executeScenario(it)
                }
        } finally {
            endExecution()
        }
    }

    fun endExecutionLatch() {
        syncExecutionSemaphore.release()
        endExecutionLatch.countDown()
    }

    private fun startExecution() {
        engineExecutionContext.notifyJUnitListener(STARTED, chutneyClassDescriptor)
    }

    private fun executeScenario(scenarioDescriptor: ChutneyScenarioDescriptor) {
        syncExecutionSemaphore.acquire()

        val chutneyScenarioExecutionContext = ChutneyScenarioExecutionContext(this, scenarioDescriptor)
        scenarioExecutionContexts.add(chutneyScenarioExecutionContext)
        chutneyScenarioExecutionContext.execute()
    }

    private fun endExecution() {
        endExecutionLatch.await()

        try {
            engineExecutionContext.notifyJUnitListener(FINISHED, chutneyClassDescriptor, successful())
        } finally {
            engineExecutionContext.endExecutionLatch()
        }
    }
}

class ChutneyScenarioExecutionContext(
    private val chutneyClassExecutionContext: ChutneyClassExecutionContext,
    private val chutneyScenarioDescriptor: ChutneyScenarioDescriptor
) {
    private var executionId: Long? = null
    private var rootStep: Step? = null
    private var executionStatus: Status = Status.NOT_EXECUTED
    private val uniqueIds = mutableMapOf<Step, TestDescriptor>()
    private val retryParents = mutableSetOf<Step>()
    private val retry = mutableMapOf<Step, Pair<TestDescriptor, TestExecutionResult?>>()
    private val engineExecutionContext = chutneyClassExecutionContext.engineExecutionContext

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    fun execute() {
        logger.info("Running scenario {} from {}", chutneyScenarioDescriptor.chutneyScenario.title, (chutneyScenarioDescriptor.source.get() as MethodSource).methodName)

        try {
            executionId = delegateExecution()
            engineExecutionContext.addScenarioExecution(executionId!!, this)
        } catch (t: Throwable) {
            try {
                notifyFailedLaunchExecution(t)
            } finally {
                chutneyClassExecutionContext.endExecutionLatch()
            }
        }
    }

    private fun delegateExecution() =
        if (chutneyScenarioDescriptor.environment != null) {
            engineExecutionContext.executionService.execute(
                chutneyScenarioDescriptor.chutneyScenario,
                chutneyScenarioDescriptor.environment
            )
        } else {
            engineExecutionContext.executionService.execute(
                chutneyScenarioDescriptor.chutneyScenario,
                resolveEnvironmentName(chutneyScenarioDescriptor.environmentName)
            )
        }

    fun startExecution(rootStep: Step) {
        this.rootStep = rootStep

        engineExecutionContext.notifyJUnitListener(STARTED, chutneyScenarioDescriptor)
    }

    fun beginStepExecution(step: Step) {
        val rootStep = rootStep!!

        if (rootStep != step) {
            val uniqueId = buildStepUniqueId(chutneyScenarioDescriptor.uniqueId, rootStep, step)
            chutneyScenarioDescriptor.findByUniqueId(uniqueId).ifPresentOrElse({
                val chutneyStepDescriptor = it as ChutneyStepDescriptor
                if (chutneyStepDescriptor.hasRetryStrategy()) {
                    retryParents.remove(step)
                    retryParents.add(step)
                }
                if (hasParentRetryStep(step)) {
                    retry[step] = Pair(chutneyStepDescriptor, null)
                }
                if (!uniqueIds.containsKey(step)) {
                    uniqueIds[step] = chutneyStepDescriptor
                    engineExecutionContext.notifyJUnitListener(STARTED, chutneyStepDescriptor)
                }
            }, {
                val stepDescriptor = mapStepToChutneyDescriptor(uniqueId, step)
                uniqueIds[step] = stepDescriptor

                chutneyScenarioDescriptor.addChild(stepDescriptor)
                stepDescriptor.setParent(chutneyScenarioDescriptor)
                engineExecutionContext.notifyJUnitListener(DYNAMIC, stepDescriptor)
                stepDescriptor.children.forEach { engineExecutionContext.notifyJUnitListener(DYNAMIC, it) }
                engineExecutionContext.notifyJUnitListener(STARTED, stepDescriptor)
            })
        }
    }

    fun endStepExecution(step: Step) {
        val rootStep = rootStep!!

        if (rootStep != step) {
            val testDescriptor = uniqueIds[step]!! as ChutneyStepDescriptor

            engineExecutionContext.notifyJUnitListener(
                REPORT,
                testDescriptor,
                reportEntry = ReportEntry.from(
                    mapOf(
                        ChutneyJUnitReportingKeys.REPORT_STEP_JSON_STRING.value to JsonReportWriter.reportAsJson(ReportUtil.generateReportDto(step))
                    )
                )
            )

            val testExecutionResult = testExecutionResultFromStatus(
                throwable = stepFailureException(step),
                status = arrayOf(step.status())
            )

            if (hasParentRetryStep(step)) {
                retry[step] = Pair(testDescriptor, testExecutionResult)
            } else if (testDescriptor.hasRetryStrategy()) {
                if (testExecutionResult.status.equals(TestExecutionResult.Status.SUCCESSFUL)) {
                    endRetryStep(step, testDescriptor, testExecutionResult)
                } else {
                    retry[step] = Pair(testDescriptor, testExecutionResult)
                }
            } else {
                engineExecutionContext.notifyJUnitListener(FINISHED, testDescriptor, testExecutionResult)
            }
        }
    }

    fun endExecution(rootStep: Step) {
        executionStatus = rootStep.status()

        try {
            endPendingFailedRetryStep()

            engineExecutionContext.notifyJUnitListener(
                REPORT,
                chutneyScenarioDescriptor,
                reportEntry = ReportEntry.from(
                    mapOf(
                        REPORT_JSON_STRING.value to JsonReportWriter.reportAsJson(ReportUtil.generateReportDto(rootStep))
                    ))
            )

            engineExecutionContext.notifyJUnitListener(
                FINISHED,
                chutneyScenarioDescriptor,
                testExecutionResultFromStatus(
                    throwable = stepFailureException(rootStep),
                    status = arrayOf(executionStatus)
                )
            )
        } finally {
            chutneyClassExecutionContext.endExecutionLatch()
        }
    }

    private fun resolveEnvironmentName(environmentName: String): String? {
        if (environmentName.isBlank()) {
            return engineExecutionContext.configurationParameters.get(CONFIG_ENVIRONMENT.parameter).orElse(null)
        }
        return environmentName
    }

    private fun convertExecuteException(t: Throwable, scenarioDescriptor: ChutneyScenarioDescriptor): Throwable {
        return when (t) {
            is CannotResolveDefaultEnvironmentException -> UnresolvedScenarioEnvironmentException(t.message)
            is EnvironmentNotFoundException -> UnresolvedScenarioEnvironmentException("Environment [${scenarioDescriptor.environmentName}] not found")
            else -> AssertionError(t)
        }
    }

    private fun buildStepUniqueId(
        fromUniqueId: UniqueId,
        rootStep: Step,
        subStep: Step
    ): UniqueId {
        val list = rootStep.findSubStepPath(subStep).filterIndexed { index, _ -> index > 0 }
        var uniqueId = fromUniqueId
        list.forEachIndexed { i, s ->
            val stepIndex = if (i == 0) {
                rootStep.subSteps().indexOf(s)
            } else {
                list[i - 1].subSteps().indexOf(s)
            }
            uniqueId = uniqueId.addStep(stepIndex)
        }
        return uniqueId
    }

    private fun stepFailureException(step: Step): Throwable? {
        if (step.isExecutionFailed()) {
            return StepExecutionFailedException(step)
        }
        return null
    }

    private fun notifyFailedLaunchExecution(t: Throwable) {
        engineExecutionContext.notifyJUnitListener(STARTED, chutneyScenarioDescriptor)
        chutneyScenarioDescriptor.children.forEach {
            engineExecutionContext.notifyJUnitListener(SKIPPED, it, reason = "Could not launch parent scenario execution")
        }
        engineExecutionContext.notifyJUnitListener(
            FINISHED,
            chutneyScenarioDescriptor,
            testExecutionResultFromStatus(
                throwable = convertExecuteException(t, chutneyScenarioDescriptor),
                status = arrayOf(Status.FAILURE)
            )
        )
    }

    private fun mapStepToChutneyDescriptor(uniqueId: UniqueId, step: Step): TestDescriptor {
        val chutneyStepDescriptor = ChutneyStepDescriptor(uniqueId, step.definition().name, chutneyScenarioDescriptor.source.get(), mapStepToChutneyStep(step))
        step.subSteps().forEach {
            val childId = buildStepUniqueId(uniqueId, step, it)
            val childDescriptor = mapStepToChutneyDescriptor(childId, it)
            chutneyStepDescriptor.addChild(childDescriptor)
        }
        return chutneyStepDescriptor
    }

    private fun mapStepToChutneyStep(step: Step): ChutneyStep {
        return ChutneyStep(
            step.definition().name,
            if (step.definition().type.isBlank()) {
                null
            } else {
                ChutneyStepImpl(step.definition().type)
            },
            step.definition().strategy.map { Strategy(it.type, it.strategyProperties as Map<String, String>) }.orElse(null)
        )
    }

    private fun hasParentRetryStep(step: Step): Boolean {
        return retryParents.flatMap { flatSubSteps(it) }.distinct().contains(step)
    }

    private fun flatSubSteps(step: Step, withSelf: Boolean = false): List<Step> {
        val stepList = step.subSteps().takeIf { it.isNotEmpty() }?.flatMap { flatSubSteps(it, true) } ?: emptyList()
        return if (withSelf) {
            stepList.plus(step)
        } else {
            stepList
        }
    }

    private fun endRetryStep(step: Step, testDescriptor: TestDescriptor, testExecutionResult: TestExecutionResult?) {
        step.subSteps().forEach {
            retry.remove(it)?.let { pair ->
                endRetryStep(it, pair.first, pair.second)
            }
        }

        retryParents.remove(step)
        engineExecutionContext.notifyJUnitListener(FINISHED, testDescriptor, testExecutionResult)
    }

    private fun endPendingFailedRetryStep() {
        retryParents.toList().forEach {
            retry.remove(it)?.let { pair ->
                endRetryStep(it, pair.first, pair.second)
            }
        }
    }
}
