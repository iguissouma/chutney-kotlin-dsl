package com.chutneytesting.kotlin.launcher

import com.chutneytesting.ExecutionConfiguration
import com.chutneytesting.engine.api.execution.*
import com.chutneytesting.engine.api.execution.StatusDto.SUCCESS
import com.chutneytesting.environment.api.EmbeddedEnvironmentApi
import com.chutneytesting.environment.api.dto.EnvironmentDto
import com.chutneytesting.environment.domain.EnvironmentService
import com.chutneytesting.environment.infra.JsonFilesEnvironmentRepository
import com.chutneytesting.kotlin.dsl.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.assertj.core.api.Assertions
import org.assertj.core.api.SoftAssertions
import java.io.File

const val CHUTNEY_ROOT_PATH = ".chutney"
const val CHUTNEY_REPORT_ROOT_PATH = "$CHUTNEY_ROOT_PATH/reports"

class Launcher(
    private val reportRootPath: String = CHUTNEY_REPORT_ROOT_PATH,
    environmentJsonRootPath: String = CHUTNEY_ROOT_PATH
) {

    private val executionConfiguration = ExecutionConfiguration()
    private val embeddedEnvironmentApi = EmbeddedEnvironmentApi(
        EnvironmentService(
            JsonFilesEnvironmentRepository(environmentJsonRootPath)
        )
    )
    private val om = ObjectMapper()
        .findAndRegisterModules()
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)

    fun environment(name: String): ChutneyEnvironment {
        return mapEnvironmentNameToChutneyEnvironment(name)
    }


    fun run(
        scenario: ChutneyScenario,
        environmentName: String,
        expected: StatusDto = SUCCESS
    ) {
        run(scenario, mapEnvironmentNameToChutneyEnvironment(environmentName), expected)
    }

    fun run(
        scenarios: List<ChutneyScenario>,
        environmentName: String,
        expected: StatusDto = SUCCESS
    ) {
        run(scenarios, mapEnvironmentNameToChutneyEnvironment(environmentName), expected)
    }

    fun run(
        scenario: ChutneyScenario,
        environment: ChutneyEnvironment,
        expected: StatusDto = SUCCESS
    ) {
        Assertions.assertThat(run(scenario, environment)).isEqualTo(expected)
    }

    fun run(
        scenarios: List<ChutneyScenario>,
        environment: ChutneyEnvironment,
        expected: StatusDto = SUCCESS
    ) {
        val softly = SoftAssertions()
        scenarios.map { runSoftly(it, environment, expected, softly) }
        softly.assertAll()
    }

    private fun runSoftly(
        scenario: ChutneyScenario,
        environment: ChutneyEnvironment,
        expected: StatusDto,
        softly: SoftAssertions
    ) {
        val status = run(scenario, environment)
        softly.assertThat(expected).isEqualTo(status)
    }

    fun run(
        scenario: ChutneyScenario,
        environment: ChutneyEnvironment
    ): StatusDto? {
        val executionRequestDto = ExecutionRequestDto(mapScenarioToExecutionRequest(scenario, environment))
        val report = executionConfiguration.embeddedTestEngine().execute(executionRequestDto)
        ConsolePrinter().printReport(report, environment.name)
        writeReports(scenario, environment.name, report)
        return report.status
    }

    fun runAndGetReport(
        scenario: ChutneyScenario,
        environment: ChutneyEnvironment
    ): StepExecutionReportDto? {
        val executionRequestDto = ExecutionRequestDto(mapScenarioToExecutionRequest(scenario, environment))
        val report = executionConfiguration.embeddedTestEngine().execute(executionRequestDto)
        return report
    }

    private fun writeReports(
        scenario: ChutneyScenario,
        environmentName: String,
        report: StepExecutionReportDto
    ) {
        val reportRootPath = File(reportRootPath, environmentName)
        reportRootPath.mkdirs()
        File(
            reportRootPath,
            scenario.title.split(" ")
                .joinToString("", postfix = ".json") { it.capitalize() })
            .bufferedWriter()
            .use { out -> out.write(om.writerWithDefaultPrettyPrinter().writeValueAsString(report)) }
    }

    private fun mapEnvironmentNameToChutneyEnvironment(environmentName: String): ChutneyEnvironment {
        val environmentDto = embeddedEnvironmentApi.getEnvironment(environmentName)
        return ChutneyEnvironment(
            name = environmentDto!!.name,
            description = environmentDto.description,
            targets = environmentDto.targets.map { targetDto ->
                ChutneyTarget(
                    name = targetDto.name,
                    url = targetDto.url,
                    configuration = ChutneyConfiguration(
                        properties = targetDto.propertiesToMap(),
                        security = ChutneySecurityProperties(
                            credential = targetDto.username?.let {
                                ChutneySecurityProperties.Credential(
                                    it,
                                    targetDto.password
                                )
                            },
                            keyStore = targetDto.keyStore,
                            keyStorePassword = targetDto.keyStorePassword,
                            privateKey = targetDto.privateKey
                        )
                    )
                )
            }
        )
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

    private fun toSecurityDto(securityProperties: ChutneySecurityProperties): SecurityInfoExecutionDto? {
        return SecurityInfoExecutionDto(
            securityProperties.credential?.username?.let {
                CredentialExecutionDto(
                    it,
                    securityProperties.credential.password
                )
            },
            securityProperties.trustStore?.ifEmpty { null },
            securityProperties.trustStorePassword?.ifEmpty { null },
            securityProperties.keyStore?.ifEmpty { null },
            securityProperties.keyStorePassword?.ifEmpty { null },
            securityProperties.privateKey?.ifEmpty { null }
        )
    }
}

