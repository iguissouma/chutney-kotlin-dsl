package com.chutneytesting.kotlin.engine

import com.chutneytesting.engine.api.execution.StatusDto
import com.chutneytesting.kotlin.dsl.ChutneyEnvironment
import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.launcher.Launcher
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestExecutionResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
            if (testDescriptor is ChutneyTestDescriptor) {
                var result: TestExecutionResult = TestExecutionResult.successful()
                try {
                    request.engineExecutionListener.executionStarted(testDescriptor)
                    result = executeTest(testDescriptor)
                    /*testDescriptor.children.forEach { c ->
                        request.engineExecutionListener.executionStarted(c)
                        request.engineExecutionListener.executionFinished(c, TestExecutionResult.successful())
                    }*/
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

    private fun executeTest(testDescriptor: ChutneyTestDescriptor): TestExecutionResult {

        val testClassInstance =
            testDescriptor.classInfo.loadClass().getDeclaredConstructor().newInstance()
        val getScenario = testDescriptor.methodInfo.loadClassAndGetMethod()

        if (getScenario.returnType != ChutneyScenario::class.java) {
            throw IllegalStateException("Method ${getScenario.declaringClass.name}#${getScenario.name} has invalid return type, should return Scenario.")
        }

        val scenario = getScenario.invoke(testClassInstance) as ChutneyScenario

        println(scenario.toString())

        val statusDto = Launcher().run(scenario, envA)

        scenario.title?.let {
            log.info("Loaded scenario $it.")
        } ?: log.info("Loaded scenario.")

        return if (statusDto == StatusDto.SUCCESS) TestExecutionResult.successful() else TestExecutionResult.failed(Throwable("Failed..."))
    }
}
