package com.chutneytesting.kotlin.synchronize

import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.util.ChutneyServerInfo
import java.io.File
import java.io.IOException
import java.nio.file.Files

/**
 * Synchronise scenario locally and/or remotely and returns elapsed time in milliseconds.
 */
fun ChutneyScenario.synchronise(
    serverInfo: ChutneyServerInfo? = null,
    updateRemote: Boolean = false,
    path: String = "src/main/resources/chutney/",
    pathCreated: String = "$path/in_progress"
) {
    var id = this.id
    if (updateRemote && serverInfo != null) {
        id = createOrUpdateRemoteScenario(serverInfo, this)
    }
    val scenario = this
    getJsonFile(path, scenario)?.apply {
        updateJsonFile(this, id, scenario)
    } ?: createJsonFile(pathCreated, id, scenario)
}

private fun updateJsonFile(file: File, id: Int?, scenario: ChutneyScenario) {
    file.writeText(scenario.toString())
    val fileNewName = getFileName(id, scenario.title)
    if (file.name != fileNewName) {
        renameJsonFile(file, fileNewName)
    }
    println("| AT json synchronized:: ${file.parentFile.absolutePath.plus(fileNewName)}")
}

private fun createJsonFile(pathCreated: String, id: Int?, scenario: ChutneyScenario) {
    File(pathCreated).mkdirs()
    File("$pathCreated/${getFileName(id, scenario.title)}")
        .apply { writeText(scenario.toString()) }
        .also { println("| AT json created:: ${it.absolutePath}") }
}

private fun getFileName(id: Int?, title: String) =
    (id?.let { id.toString() + "-" } ?: "") + title + ".chutney.json"

private fun getJsonFile(path: String, scenario: ChutneyScenario): File? {
    return File(path).walkTopDown().filter { it.isFile }
        .firstOrNull {
            val chutneyScenarioIdFromFileName = getChutneyScenarioIdFromFileName(it.name)
            val sameId = scenario.id != null && scenario.id == chutneyScenarioIdFromFileName
            val sameName = it.name.equals(getFileName(scenario.id, scenario.title), ignoreCase = true)
            sameId || sameName
        }
}

private fun createOrUpdateRemoteScenario(serverInfo: ChutneyServerInfo, scenario: ChutneyScenario): Int {
    var id = scenario.id
    if (id != null) {
        updateJsonRemoteScenario(serverInfo = serverInfo, scenario)
    } else {
        id = createRemoteScenario(serverInfo = serverInfo, scenario)
    }
    return id
}


private fun renameJsonFile(
    jsonFile: File,
    fileName: String
) {
    try {
        Files.move(jsonFile.toPath(), jsonFile.toPath().resolveSibling(fileName))
    } catch (e: IOException) {
        println("| AT json file at ${jsonFile.name} cannot be renamed to: $fileName. ${e.message}")
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

private fun updateJsonRemoteScenario(
    serverInfo: ChutneyServerInfo,
    scenario: ChutneyScenario
) {
    try {
        ChutneyServerServiceImpl.updateJsonScenario(serverInfo, scenario)
        println("| remote AT json synchronized:: ${serverInfo.remoteServerUrl}/#/scenario/${scenario.id}/execution/last")
    } catch (e: Exception) {
        println("| remote AT with id: ${scenario.id} cannot be synchronized:: ${e.message}")
        throw e
    }
}

private fun createRemoteScenario(
    serverInfo: ChutneyServerInfo,
    scenario: ChutneyScenario
): Int {
    try {
        val id = ChutneyServerServiceImpl.createJsonScenario(serverInfo, scenario)
        println("| remote AT json created:: ${serverInfo.remoteServerUrl}/#/scenario/$id/execution/last")
        return id
    } catch (e: Exception) {
        println("| remote AT cannot be created:: ${e.message}")
        throw e
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
