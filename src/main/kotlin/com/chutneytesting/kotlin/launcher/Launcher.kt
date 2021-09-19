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
        consolePrint(executionRequestDto)
        val report = executionConfiguration.embeddedTestEngine().execute(executionRequestDto)
        println("\n")
        consolePrint(report, environment)
        writeReports(scenario, environment, report)
        return Executable {
            assertEquals(expected, report.status)
        }
    }

    private fun consolePrint(report: StepExecutionReportDto, environment: ChutneyEnvironment) {
        println(
            color(
                "[" + report.status + "] " + "scenario: \"" + report.name + "\"" + " on environment " + environment.name + "\n",
                report.status
            )
        )

        report.steps?.forEach {
            consolePrint(it, "  ")
        }
    }

    //val success =  "\u001b[32m"
    private val success = "\u001b[34m"
    private val warning = "\u001b[33m"
    private val failure = "\u001b[31m"
    private val stopped = "\u001b[35m"
    private val reset = "\u001b[0m"

    private fun color(s: String, status: StatusDto): String {
        when (status) {
            StatusDto.SUCCESS -> return success + s + reset
            StatusDto.WARN -> return warning + s + reset
            StatusDto.FAILURE -> return failure + s + reset
            StatusDto.STOPPED -> return stopped + s + reset
            StatusDto.NOT_EXECUTED -> return stopped + s + reset
            else -> return s + reset
        }
    }

    private fun consolePrint(step: StepExecutionReportDto, indent: String) {
        println(indent + color("[" + step.status + "] " + step.name + " [" + step.strategy.ifBlank { "default" } + "]",
            step.status))

        errors(step, indent)
        information(step, indent)

        if (!step.steps.isEmpty() && step.type.isBlank()) {
            step.steps.forEach { consolePrint(it, "$indent  ") }
        }

        if (step.type.isNotBlank()) {
            println("$indent  " + step.type + " { " + consolePrint(step.context.evaluatedInputs) + "}")
            if (step.targetName.isNotBlank()) {
                println("$indent  on { " + step.targetName + ": " + step.targetUrl + " }")
            }
        }
    }

    private fun errors(step: StepExecutionReportDto, indent: String) {
        step.errors.forEach {
            println(color("$indent  >> $it", step.status))
        }
    }

    private fun information(step: StepExecutionReportDto, indent: String) {
        step.information.forEach {
            println("$success$indent  >> $it$reset")
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

    private fun consolePrint(executionRequestDto: ExecutionRequestDto) {
        println("scenario: \"" + executionRequestDto.scenario.name + "\" on environment " + executionRequestDto.scenario.environment + "\n")

        executionRequestDto.scenario.definition.steps.forEach {
            consolePrint(it, "  ")
        }
    }

    private fun consolePrint(step: StepDefinitionDto, indent: String) {
        println(indent + step.name + " [" + step.strategy.type.ifBlank { "default" } + "]")

        if (step.steps.isNotEmpty() && step.type.isBlank()) {
            step.steps.forEach { consolePrint(it, "$indent  ") }
        }

        if (step.type.isNotBlank()) {
            println("$indent  " + step.type + " { " + consolePrint(step.inputs) + "}")
            step.target.ifPresent { println("$indent  on { " + it.name + ": " + it.url + " }") }
        }

    }

    private fun consolePrint(map: Map<String, Any>): String {
        var properties = " "
        map.entries.forEach {
            properties += it.key + ": " + "\"" + it.value + "\", "
        }
        return properties
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

