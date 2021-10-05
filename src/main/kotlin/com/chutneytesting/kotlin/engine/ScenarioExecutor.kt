package com.chutneytesting.kotlin.engine

import com.chutneytesting.engine.api.execution.StatusDto
import com.chutneytesting.engine.api.execution.StepExecutionReportDto
import com.chutneytesting.kotlin.dsl.ChutneyEnvironment
import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.launcher.Launcher
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.AssertionError
import java.lang.IllegalStateException

val envA = ChutneyEnvironment(
    name = "envA",
    description = "fake environment for test",
    targets = emptyList())

class ScenarioExecutor : Executor {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ScenarioExecutor::class.java)
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
                    val report = executeTest(testDescriptor)
                    result = if (report?.status == StatusDto.SUCCESS) TestExecutionResult.successful() else TestExecutionResult.failed(Throwable(report?.errors?.joinToString(separator = System.lineSeparator())))
                    testDescriptor.children.forEachIndexed { index, stepTestDescriptor ->
                        val stepExecutionReportDto = report?.steps?.get(index)
                        recordStepExecution(request, stepTestDescriptor, stepExecutionReportDto)
                    }
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
        stepTestDescriptor: TestDescriptor?,
        stepExecutionReportDto: StepExecutionReportDto?
    ) {
        stepTestDescriptor?.children?.forEachIndexed {index, sub ->
            recordStepExecution(request, sub, stepExecutionReportDto?.steps?.get(index))
        }
        request.engineExecutionListener.executionStarted(stepTestDescriptor)
        request.engineExecutionListener.executionFinished(
            stepTestDescriptor,
            if (stepExecutionReportDto?.status == StatusDto.SUCCESS) TestExecutionResult.successful() else TestExecutionResult.failed(
                AssertionError(stepExecutionReportDto?.errors?.joinToString(separator = System.lineSeparator()))
            )
        )
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

        val testClassInstance =
            testDescriptor.classInfo.loadClass().getDeclaredConstructor().newInstance()
        val getScenario = testDescriptor.methodInfo.loadClassAndGetMethod()

        if (getScenario.returnType != ChutneyScenario::class.java) {
            throw IllegalStateException("Method ${getScenario.declaringClass.name}#${getScenario.name} has invalid return type, should return Scenario.")
        }

        // val scenario = getScenario.invoke(testClassInstance) as ChutneyScenario

        // println(scenario.toString())


        testDescriptor.scenario.title.let {
            log.info("Loaded scenario $it.")
        }
        return Launcher().runAndGetReport(testDescriptor.scenario, envA)

        // return if (statusDto == StatusDto.SUCCESS) TestExecutionResult.successful() else TestExecutionResult.failed(Throwable("Failed..."))
    }
}
