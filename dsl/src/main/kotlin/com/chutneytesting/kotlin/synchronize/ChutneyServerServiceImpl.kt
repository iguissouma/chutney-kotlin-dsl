package com.chutneytesting.kotlin.synchronize

import com.chutneytesting.kotlin.transformation.from_component_to_kotlin.ComposableStepDto
import com.chutneytesting.kotlin.transformation.from_component_to_kotlin.ComposableTestCaseDto
import com.chutneytesting.kotlin.util.ChutneyServerInfo
import com.chutneytesting.kotlin.util.HttpClient


interface ChutneyServerService {
    fun getAllComponent(serverInfo: ChutneyServerInfo): List<ComposableStepDto>
    fun getAllScenarios(serverInfo: ChutneyServerInfo): List<LinkedHashMap<String, Any>>
    fun getComposedScenario(serverInfo: ChutneyServerInfo, scenarioId: String): ComposableTestCaseDto
    fun updateJsonScenario(serverInfo: ChutneyServerInfo, scenarioContent: String, scenarioId: String)
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

    override fun updateJsonScenario(serverInfo: ChutneyServerInfo, scenarioContent: String, scenarioId: String) {
        val generatedTag = "GENERATED"
        val escapeSql = escapeSql(scenarioContent)
        val body = "update scenario set content='$escapeSql', version=version+1, tags=CASE WHEN tags like '%$generatedTag%' THEN tags ELSE CONCAT_WS(',',tags,'$generatedTag') end where id = '$scenarioId'"

        HttpClient.post<Any>(serverInfo, "/api/v1/admin/database/execute/jdbc", body)
    }

    private fun escapeSql(str: String?): String? = str?.replace("'", "''")

}

