package com.chutneytesting.kotlin.execution

import com.chutneytesting.engine.api.execution.CredentialExecutionDto
import com.chutneytesting.engine.api.execution.ExecutionRequestDto
import com.chutneytesting.engine.api.execution.SecurityInfoExecutionDto
import com.chutneytesting.engine.api.execution.TargetExecutionDto
import com.chutneytesting.kotlin.dsl.ChutneyEnvironment
import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.dsl.ChutneySecurityProperties
import com.chutneytesting.kotlin.dsl.ChutneyStep
import com.chutneytesting.kotlin.dsl.ChutneyTarget
import com.chutneytesting.kotlin.dsl.Strategy

object ExecutionRequestMapper {

    fun mapScenarioToExecutionRequest(
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
            mapOf() // validations
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
                step.implementation?.validations  // validations
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
            securityProperties.credential?.username.let {
                CredentialExecutionDto(
                    it,
                    securityProperties.credential?.password
                )
            },
            securityProperties.trustStore?.ifEmpty { null },
            securityProperties.trustStorePassword?.ifEmpty { null },
            securityProperties.keyStore?.ifEmpty { null },
            securityProperties.keyStorePassword?.ifEmpty { null },
            securityProperties.keyPassword?.ifEmpty { null },
            securityProperties.privateKey?.ifEmpty { null }
        )
    }
}
