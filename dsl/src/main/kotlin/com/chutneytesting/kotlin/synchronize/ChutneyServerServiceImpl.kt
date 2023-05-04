package com.chutneytesting.kotlin.synchronize

import com.chutneytesting.environment.api.dto.EnvironmentDto
import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.dsl.Mapper
import com.chutneytesting.kotlin.transformation.from_component_to_kotlin.ComposableStepDto
import com.chutneytesting.kotlin.transformation.from_component_to_kotlin.ComposableTestCaseDto
import com.chutneytesting.kotlin.util.ChutneyServerInfo
import com.chutneytesting.kotlin.util.HttpClient
import org.apache.commons.text.StringEscapeUtils


interface ChutneyServerService {
    fun getAllComponent(serverInfo: ChutneyServerInfo): List<ComposableStepDto>
    fun getAllScenarios(serverInfo: ChutneyServerInfo): List<LinkedHashMap<String, Any>>
    fun getComposedScenario(serverInfo: ChutneyServerInfo, scenarioId: String): ComposableTestCaseDto
    fun updateJsonScenario(serverInfo: ChutneyServerInfo, scenario: ChutneyScenario)

    fun createJsonScenario(serverInfo: ChutneyServerInfo, scenario: ChutneyScenario): Int
    fun getEnvironments(serverInfo: ChutneyServerInfo): Set<EnvironmentDto>
}

object ChutneyServerServiceImpl : ChutneyServerService {

    override fun getAllComponent(serverInfo: ChutneyServerInfo): List<ComposableStepDto> {
        return HttpClient.get(serverInfo, "/api/steps/v1/all")
    }

    override fun getAllScenarios(serverInfo: ChutneyServerInfo): List<LinkedHashMap<String, Any>> {
        return HttpClient.get(serverInfo, "/api/scenario/v2")
    }

    override fun getComposedScenario(serverInfo: ChutneyServerInfo, scenarioId: String): ComposableTestCaseDto {
        return HttpClient.get(serverInfo, "/api/scenario/component-edition/$scenarioId")
    }

    override fun updateJsonScenario(serverInfo: ChutneyServerInfo, scenario: ChutneyScenario) {
        val remoteScenario: LinkedHashMap<String, Any> = getRemoteScenario(serverInfo, scenario.id!!)
        val generatedTag = "KOTLIN"
        var tags = remoteScenario["tags"] as List<String>
        if (!tags.contains(generatedTag)) {
            tags = tags.plus(generatedTag)
        }
        val body = """
            {
                "id": ${scenario.id} ,
                "content":"${StringEscapeUtils.escapeJson(scenario.toString())}",
                "title": "${scenario.title}",
                "description":"${scenario.description}" ,
                "tags": ${Mapper.toJson(tags)},
                "version": ${remoteScenario["version"]}
            }
        """.trimIndent()
        HttpClient.post<Any>(serverInfo, "/api/scenario/v2/raw", body)
    }

    private fun getRemoteScenario(
        serverInfo: ChutneyServerInfo,
        id: Int
    ): LinkedHashMap<String, Any> {
        return HttpClient.get(
            serverInfo, "/api/scenario/v2/raw/$id"
        )
    }

    override fun createJsonScenario(serverInfo: ChutneyServerInfo, scenario: ChutneyScenario): Int {
        val generatedTag = "KOTLIN"
        val body = """
            {
                "content": "${StringEscapeUtils.escapeJson(scenario.toString())}",
                "title": "${scenario.title}",
                "description": "${scenario.description}",
                "tags": ["$generatedTag"]
            }
        """.trimIndent()
        return HttpClient.post(serverInfo, "/api/scenario/v2/raw", body)

    }

    override fun getEnvironments(serverInfo: ChutneyServerInfo): Set<EnvironmentDto> {
        return HttpClient.get(serverInfo, "/api/v2/environment")
    }

    private fun escapeSql(str: String?): String? = str?.replace("'", "''")

}

