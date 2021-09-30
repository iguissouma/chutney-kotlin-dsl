package com.chutneytesting.kotlin.synchronize

import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.util.ChutneyServerInfo
import java.io.File

/**
 * Synchronise scenario locally and/or remotely and returns elapsed time in milliseconds.
 */
fun ChutneyScenario.synchronise(serverInfo: ChutneyServerInfo? = null, updateRemote: Boolean = false, path: String = "src/main/resources/chutney/") {
    val json = this.toString()
    val fileName = (this.id?.let { this.id.toString() + "-" } ?: "") + title + ".chutney.json"
    File(path).walkTopDown().filter { it.isFile }.firstOrNull {
        val chutneyScenarioIdFromFileName = getChutneyScenarioIdFromFileName(it.name)
        it.name.equals(fileName, ignoreCase = true) || (this.id != null && chutneyScenarioIdFromFileName != null && this.id == chutneyScenarioIdFromFileName)
    }?.apply {
        this.writeText(json)
    }?.also {
        println("| AT json synchronized:: ${it.absolutePath}")
    } ?: File("src/main/resources/chutney/in_progress/$fileName").apply { writeText(json) }
        .also { println("| AT json created:: ${it.absolutePath}") }

    if (updateRemote && this.id != null && serverInfo != null) {
        updateJsonRemoteScenario(serverInfo, this.id, json)
    }
}

/**
 * Cosmetic to create a list of scenarios
 */
class SynchronizeScenariosBuilder {
    var scenarios: List<ChutneyScenario> = mutableListOf()

    operator fun ChutneyScenario.unaryPlus() {
        scenarios = scenarios + this
    }

    operator fun List<ChutneyScenario>.unaryPlus() {
        scenarios = scenarios + this
    }

    operator fun ChutneyScenario.unaryMinus() {
        // scenarios = scenarios - this
        // cosmetic to ignore scenario
    }
}

private fun updateJsonRemoteScenario(serverInfo: ChutneyServerInfo, id: Int, content: String) {
    try {
        ChutneyServerServiceImpl.updateJsonScenario(serverInfo, content, id.toString())
        println("| remote AT json synchronized:: ${serverInfo.remoteServerUrl}/#/scenario/$id/execution/last")
    } catch (e: Exception) {
        println("| remote AT json cannot be synchronized:: $id")
    }
}

private fun getChutneyScenarioIdFromFileName(fileName: String): Int? {
    val dashIndex = fileName.indexOf("-")
    return try {
        if (dashIndex > 0) Integer.valueOf(fileName.substring(0, dashIndex)) else null
    } catch (e: Exception) {
        null
    }
}
