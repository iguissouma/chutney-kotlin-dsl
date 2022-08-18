package com.chutneytesting.kotlin.synchronize

import EnvironmentSynchronizeService
import com.chutneytesting.environment.domain.Target
import com.chutneytesting.environment.infra.JsonFilesEnvironmentRepository
import com.chutneytesting.kotlin.HttpTestBase
import com.chutneytesting.kotlin.util.ChutneyServerInfo
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import java.nio.file.Path

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EnvironmentServiceSynchronizationTest : HttpTestBase() {


    @Test
    fun should_synchronize_local_environments_from_remote(@TempDir tempDir: Path) {
        // Given
        val environmentsFilesPath = tempDir.toAbsolutePath().toString()
        val envsResponse = "[{\n" +
            "    \"name\": \"CHUTNEY\",\n" +
            "    \"description\": \"desc\",\n" +
            "    \"targets\": [\n" +
            "        {\n" +
            "            \"name\": \"db\",\n" +
            "            \"url\": \"dbUrl\",\n" +
            "            \"properties\": [\n" +
            "                {\n" +
            "                    \"key\": \"driverClassName\",\n" +
            "                    \"value\": \"oracle.jdbc.OracleDriver\"\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    ]\n" +
            "}\n" +
            "]\n"
        mockServer
            .`when`(
                request()
                    .withPath("/api/v2/environment")
            )
            .respond(
                response()
                    .withBody(envsResponse)
                    .withHeader("Content-Type", "application/json")
            )
        val chutneyServerInfo = ChutneyServerInfo(
            url,
            "aUser",
            "aPassword"
        )

        val environmentSynchronizeService = EnvironmentSynchronizeService()

        // When
        environmentSynchronizeService.synchroniseLocal(
            serverInfo = chutneyServerInfo,
            environmentsPath = environmentsFilesPath,
            force = true
        )

        // Then
        val jsonFilesEnvironmentRepository = JsonFilesEnvironmentRepository(environmentsFilesPath)
        val actualEnvironment = jsonFilesEnvironmentRepository.findByName("CHUTNEY")
        Assertions.assertThat(actualEnvironment.name).isEqualTo("CHUTNEY")
        Assertions.assertThat(actualEnvironment.description).isEqualTo("desc")
        Assertions.assertThat(actualEnvironment.targets).hasSize(1)
        Assertions.assertThat(actualEnvironment.targets.iterator().next()).isEqualTo(
            Target.builder().withName("db")
                .withUrl("dbUrl")
                .withEnvironment("CHUTNEY")
                .withProperty("driverClassName", "oracle.jdbc.OracleDriver")
                .build()
        )
    }
}
