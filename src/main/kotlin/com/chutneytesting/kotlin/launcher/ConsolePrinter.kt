package com.chutneytesting.kotlin.launcher

import com.chutneytesting.engine.api.execution.StatusDto
import com.chutneytesting.engine.api.execution.StepExecutionReportDto
import com.chutneytesting.kotlin.dsl.ChutneyEnvironment

enum class AnsiColor(val color: String) {

    BLACK("\u001b[30m"),
    RED("\u001b[31m"),
    GREEN("\u001b[32m"),
    YELLOW("\u001b[33m"),
    BLUE("\u001b[34m"),
    MAGENTA("\u001b[35m"),
    CYAN("\u001b[36m"),
    WHITE("\u001b[37m"),

    RESET("\u001b[0m");

    fun bright(): String {
        return this.color.removeSuffix("m").plus(";1m")
    }

}

class ConsolePrinter {

    fun consolePrint(report: StepExecutionReportDto, environment: ChutneyEnvironment) {
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

    private fun consolePrint(map: Map<String, Any>): String {
        var properties = " "
        map.entries.forEach {
            properties += it.key + ": " + "\"" + it.value + "\", "
        }
        return properties
    }

    private fun color(s: String, status: StatusDto): String {
        return when (status) {
            StatusDto.SUCCESS -> AnsiColor.GREEN.bright()
            StatusDto.WARN -> AnsiColor.YELLOW.bright()
            StatusDto.FAILURE -> AnsiColor.RED.bright()
            StatusDto.STOPPED -> AnsiColor.YELLOW.bright()
            StatusDto.NOT_EXECUTED -> AnsiColor.MAGENTA.bright()
            else -> ""
        }.plus(s + AnsiColor.RESET.color)
    }

    private fun errors(step: StepExecutionReportDto, indent: String) {
        step.errors.forEach {
            println(color("$indent  >> $it", step.status))
        }
    }

    private fun information(step: StepExecutionReportDto, indent: String) {
        step.information.forEach {
            println(AnsiColor.BLUE.bright() + "$indent  >> $it" + AnsiColor.RESET)
        }
    }
}
