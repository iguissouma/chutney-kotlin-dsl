package com.chutneytesting.kotlin.launcher

import com.chutneytesting.engine.api.execution.StatusDto
import com.chutneytesting.engine.api.execution.StatusDto.*
import com.chutneytesting.engine.api.execution.StepExecutionReportDto
import com.chutneytesting.kotlin.launcher.AnsiColor.*

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

    fun printReport(report: StepExecutionReportDto, environmentName: String) {
        println()
        reportHeader(report, environmentName)

        report.steps?.forEach {
            step(it)
        }
    }

    private fun reportHeader(report: StepExecutionReportDto, environmentName: String) {
        println(
            color(
                "[" + report.status + "] " + "scenario: \"" + report.name + "\"" + " on environment " + environmentName + "\n",
                report.status
            )
        )
    }

    public fun step(step: StepExecutionReportDto, indent: String = "  ") {
        stepHeader(step, indent)

        errors(step, indent)
        information(step, indent)

        if (step.steps.isNotEmpty() && step.type.isBlank()) {
            step.steps.forEach { step(it, "$indent  ") }
        }

        if (step.type.isNotBlank()) {
            println("$indent  " + step.type + " " + mapAsString(step.context.evaluatedInputs))
            if (step.targetName.isNotBlank()) {
                println("$indent  on { " + step.targetName + ": " + step.targetUrl + " }")
            }
        }
    }

    private fun stepHeader(step: StepExecutionReportDto, indent: String) {
        println(indent + color("[" + step.status + "] " + step.name + " [" + step.strategy.ifBlank { "default" } + "]",
            step.status))
    }

    private fun mapAsString(map: Map<String, Any>): String {
        return map.entries.joinToString(separator = ",", prefix = "{ ", postfix = "}") {
            it.key + ": " + "\"" + it.value + "\""
        }
    }

    private fun color(s: String, status: StatusDto): String {
        return when (status) {
            SUCCESS -> GREEN.bright()
            WARN -> YELLOW.bright()
            FAILURE -> RED.bright()
            STOPPED -> YELLOW.bright()
            NOT_EXECUTED -> MAGENTA.bright()
            else -> ""
        }.plus(s + RESET.color)
    }

    private fun errors(step: StepExecutionReportDto, indent: String) {
        step.errors.forEach {
            println(color("$indent  >> $it", step.status))
        }
    }

    private fun information(step: StepExecutionReportDto, indent: String) {
        step.information.forEach {
            println(BLUE.bright() + "$indent  >> $it" + RESET.color)
        }
    }
}
