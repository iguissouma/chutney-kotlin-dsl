package com.chutneytesting.kotlin.execution

import com.chutneytesting.engine.api.execution.StepExecutionReportDto

fun StepExecutionReportDto.findFirstExecutableStep(): StepExecutionReportDto {
    if (steps.isEmpty()) {
        return this
    }
    return steps[0].findFirstExecutableStep()
}

fun StepExecutionReportDto.extractExecutionEnvironment(): String {
    return findFirstExecutableStep().context.scenarioContext["environment"] as String? ?: "no env"
}
