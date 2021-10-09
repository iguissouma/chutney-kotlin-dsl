package com.chutneytesting.kotlin.engine

import com.chutneytesting.engine.api.execution.StatusDto.*
import com.chutneytesting.engine.api.execution.StepExecutionReportDto
import com.chutneytesting.kotlin.dsl.ChutneyEnvironment
import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.launcher.ConsolePrinter
import com.chutneytesting.kotlin.launcher.Launcher
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException
import kotlin.AssertionError

val envA = ChutneyEnvironment(
    name = "envA",
    description = "fake environment for test",
    targets = emptyList())

class ScenarioExecutor : Executor {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ScenarioExecutor::class.java)
        private val printer = ConsolePrinter()
    }

    override fun execute(request: ExecutionRequest) {
        var totalResult: TestExecutionResult? = null
        val rootTestDescriptor = request.rootTestDescriptor
        request.engineExecutionListener.executionStarted(rootTestDescriptor)
        log.info("Execution started: ${rootTestDescriptor.uniqueId}")
        request.rootTestDescriptor.children.forEach { testDescriptor ->
            if (testDescriptor is ChutneyScenarioTestDescriptor) {
                var result: TestExecutionResult = TestExecutionResult.successful()
                try {
                    request.engineExecutionListener.executionStarted(testDescriptor)
                    val report = executeTest(testDescriptor) ?: error("should have report")
                    testDescriptor.children.forEachIndexed { index, stepTestDescriptor ->
                        val stepExecutionReportDto = report.steps?.get(index) ?: error("should have report")
                        recordStepExecution(request, stepTestDescriptor, stepExecutionReportDto)
                    }
                    result = report.testExecutionResult()
                } catch (ex: Throwable) {
                    log.error("Failure: $ex")
                    result = TestExecutionResult.failed(ex)
                } finally {
                    request.engineExecutionListener.executionFinished(testDescriptor, result)
                }
                totalResult = aggregateResult(totalResult, result)
            }
        }
        request.engineExecutionListener.executionFinished(
            rootTestDescriptor,
            totalResult
        )
        log.info("Execution finished: ${rootTestDescriptor.uniqueId}")
    }

    private fun recordStepExecution(
        request: ExecutionRequest,
        stepTestDescriptor: TestDescriptor,
        stepExecutionReportDto: StepExecutionReportDto
    ) {
        request.engineExecutionListener.executionStarted(stepTestDescriptor)
        stepTestDescriptor.children?.forEachIndexed { index, sub ->
            recordStepExecution(request, sub, stepExecutionReportDto.steps[index])
        }
        printer.step(step = stepExecutionReportDto)
        request.engineExecutionListener.executionFinished(
            stepTestDescriptor,
            stepExecutionReportDto.testExecutionResult()
        )
    }

    fun StepExecutionReportDto.testExecutionResult(): TestExecutionResult {
        return when (status) {
            SUCCESS -> TestExecutionResult.successful()
            WARN -> TestExecutionResult.failed(AssertionError())
            FAILURE -> TestExecutionResult.failed(AssertionError())
            NOT_EXECUTED -> TestExecutionResult.aborted(null)
            STOPPED -> error("A stooped test cannot reach this state")
            PAUSED -> error("A paused test cannot reach this state")
            RUNNING -> error("A running test cannot reach this state")
            EXECUTED -> error("An executed test cannot reach this state")
        }
    }


    private fun aggregateResult(
        current: TestExecutionResult?,
        new: TestExecutionResult
    ): TestExecutionResult {
        if (current == null) return new
        return when (current.status) {
            TestExecutionResult.Status.ABORTED, TestExecutionResult.Status.FAILED -> current
            else -> {
                if (current.status != new.status) {
                    log.debug("Update aggregate state ${current.status} -> ${new.status}")
                }
                new
            }
        }
    }

    private fun executeTest(testDescriptor: ChutneyScenarioTestDescriptor): StepExecutionReportDto? {

        testDescriptor.scenario.title.let {
            log.info("Loaded scenario $it.")
        }
        return Launcher().runAndGetReport(testDescriptor.scenario, envA)

    }
}
