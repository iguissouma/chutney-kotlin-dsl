package com.chutneytesting.kotlin.launcher

import com.chutneytesting.ExecutionConfiguration
import com.chutneytesting.engine.api.execution.*
import com.chutneytesting.kotlin.dsl.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.function.Executable
import java.io.File

const val CHUTNEY_REPORT_ROOT_PATH = ".chutney/reports"

class Launcher(private val reportRootPath: String = CHUTNEY_REPORT_ROOT_PATH) {

    private val executionConfiguration = ExecutionConfiguration()
    private val om = ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)

    fun run(scenario: ChutneyScenario, environment: ChutneyEnvironment, expected: StatusDto = StatusDto.SUCCESS) {
        val executable = execute(scenario, environment, expected)
        executable.execute()
    }

    fun run(
        scenarios: List<ChutneyScenario>,
        environment: ChutneyEnvironment,
        expected: StatusDto = StatusDto.SUCCESS
    ) {
        val reports = scenarios
            .map { execute(it, environment, expected) }
        Assertions.assertAll(reports)
    }

    private fun execute(scenario: ChutneyScenario, environment: ChutneyEnvironment, expected: StatusDto): Executable {
        val executionRequestDto = ExecutionRequestDto(mapScenarioToExecutionRequest(scenario, environment))
        val report = executionConfiguration.embeddedTestEngine().execute(executionRequestDto)
        println("\n")
        ConsolePrinter().consolePrint(report, environment)
        writeReports(scenario, environment, report)
        return Executable {
            assertEquals(expected, report.status)
        }
    }

    private fun writeReports(
        scenario: ChutneyScenario,
        environment: ChutneyEnvironment,
        report: StepExecutionReportDto
    ) {
        val reportRootPath = File(reportRootPath, environment.name)
        reportRootPath.mkdirs()
        File(
            reportRootPath,
            scenario.title.split(" ")
                .joinToString("", postfix = ".json") { it.capitalize() })
            .bufferedWriter()
            .use { out -> out.write(om.writerWithDefaultPrettyPrinter().writeValueAsString(report)) }
    }

    private fun mapScenarioToExecutionRequest(
        scenario: ChutneyScenario,
        environment: ChutneyEnvironment
    ): ExecutionRequestDto.StepDefinitionRequestDto {

        val steps = (scenario.givens + scenario.`when` + scenario.thens).filterNotNull()
        return ExecutionRequestDto.StepDefinitionRequestDto(
            scenario.title,
            null,
            null,
            "",
            mapOf(), //inputs
            mapStepDefinition(steps, environment), // steps
            mapOf(), //outputs
            mapOf(), // validations
            environment.name
        )
    }

    private fun mapStepDefinition(
        steps: List<ChutneyStep>,
        environment: ChutneyEnvironment
    ): List<ExecutionRequestDto.StepDefinitionRequestDto> {
        return steps.map { step ->
            ExecutionRequestDto.StepDefinitionRequestDto(
                step.description,
                mapTargetToTargetExecutionDto(environment, step.implementation?.target),
                mapStrategyToStrategyExecutionDto(step.strategy),
                step.implementation?.type,
                step.implementation?.inputs,
                mapStepDefinition(step.subSteps, environment), // steps
                step.implementation?.outputs,  //outputs
                mapOf(),  // validations
                environment.name
            )
        }
    }

    private fun mapStrategyToStrategyExecutionDto(strategy: Strategy?): ExecutionRequestDto.StepStrategyDefinitionRequestDto {
        return ExecutionRequestDto.StepStrategyDefinitionRequestDto(
            strategy?.type ?: "",
            strategy?.parameters ?: emptyMap()
        )
    }

    private fun mapTargetToTargetExecutionDto(environment: ChutneyEnvironment, target: String?): TargetExecutionDto? {
        return environment.findTarget(target)?.let {
            toTargetExecutionDto(it)
        }
    }

    private fun toTargetExecutionDto(target: ChutneyTarget): TargetExecutionDto {
        return TargetExecutionDto(
            target.name,
            target.url,
            target.configuration.properties,
            toSecurityDto(target.configuration.security),
            listOf()
        )
    }

    private fun toSecurityDto(securityProperties: ChutneySecurityProperties): SecurityInfoExecutionDto {
        return SecurityInfoExecutionDto(
            CredentialExecutionDto(securityProperties.credential.username, securityProperties.credential.password),
            securityProperties.trustStore.ifEmpty { null },
            securityProperties.trustStorePassword.ifEmpty { null },
            securityProperties.keyStore.ifEmpty { null },
            securityProperties.keyStorePassword.ifEmpty { null },
            securityProperties.privateKey.ifEmpty { null }
        )
    }
}

