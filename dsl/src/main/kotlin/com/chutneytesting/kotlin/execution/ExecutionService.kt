package com.chutneytesting.kotlin.execution

import com.chutneytesting.ExecutionConfiguration
import com.chutneytesting.engine.api.execution.ExecutionRequestDto
import com.chutneytesting.engine.api.execution.StepExecutionReportDto
import com.chutneytesting.environment.api.EmbeddedEnvironmentApi
import com.chutneytesting.environment.api.dto.EnvironmentDto
import com.chutneytesting.environment.domain.EnvironmentService
import com.chutneytesting.environment.infra.JsonFilesEnvironmentRepository
import com.chutneytesting.kotlin.dsl.*

const val CHUTNEY_ROOT_PATH = ".chutney"

class CannotResolveDefaultEnvironmentException : Exception("No environment name was given and there is more than one environment. Defaulting is impossible. Please, specify a name or declare only one environment.")

class ExecutionService(
    environmentJsonRootPath: String = CHUTNEY_ROOT_PATH
) {

    private val executionConfiguration = ExecutionConfiguration()
    private val embeddedEnvironmentApi = EmbeddedEnvironmentApi(
        EnvironmentService(
            JsonFilesEnvironmentRepository(environmentJsonRootPath)
        )
    )

    companion object {
        val EMPTY = ChutneyEnvironment("EMPTY")
    }

    fun execute(
        scenario: ChutneyScenario,
        environment: ChutneyEnvironment
    ): Long {
        return executionConfiguration.embeddedTestEngine()
            .executeAsync(
                ExecutionRequestDto(
                    ExecutionRequestMapper.mapScenarioToExecutionRequest(scenario, environment),
                    environment.name
                )
            )
    }

    fun execute(
        scenario: ChutneyScenario,
        environmentName: String? = null
    ): Long {
        return execute(scenario, getEnvironment(environmentName))
    }

    fun waitLastReport(executionId: Long): StepExecutionReportDto {
        return executionConfiguration.embeddedTestEngine()
            .receiveNotification(executionId)
            .blockingLast()
    }

    fun getEnvironment(environmentName: String? = null): ChutneyEnvironment {
        val environmentDto: EnvironmentDto
        val environments = embeddedEnvironmentApi.listEnvironments()

        environmentDto = if (environmentName.isNullOrBlank()) {
            if (environments.isNotEmpty()) {
                if (environments.size > 1) {
                    throw CannotResolveDefaultEnvironmentException()
                } else {
                    environments.first()
                }
            } else {
                return EMPTY
            }
        } else {
            embeddedEnvironmentApi.getEnvironment(environmentName)
        }

        return mapEnvironmentNameToChutneyEnvironment(environmentDto)
    }

    private fun mapEnvironmentNameToChutneyEnvironment(environmentDto: EnvironmentDto): ChutneyEnvironment {
        return ChutneyEnvironment(
            name = environmentDto.name,
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
                            keyPassword = targetDto.keyPassword,
                            privateKey = targetDto.privateKey
                        )
                    )
                )
            }
        )
    }
}
