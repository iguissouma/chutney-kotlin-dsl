package com.chutneytesting.kotlin.synchronize

import com.chutneytesting.kotlin.HttpTestBase
import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.dsl.Scenario
import com.chutneytesting.kotlin.dsl.SuccessTask
import com.chutneytesting.kotlin.util.ChutneyServerInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import org.mockserver.model.Format
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import java.io.File
import java.net.MalformedURLException
import java.nio.file.Path
import kotlin.io.path.absolutePathString

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ScenarioSynchronizeServiceTest : HttpTestBase() {

    private val chutneyServerInfo = ChutneyServerInfo(
        url,
        "aUser",
        "aPassword"
    )

    private val localScenario = Scenario(title = "A scenario") {
        When("Something happens") {
            SuccessTask()
        }
    }

    @Test
    fun should_create_new_scenario_local_file(@TempDir tempDir: Path) {

        // When & then
        assertScenarioSynchronization(tempDir = tempDir, scenario = localScenario)
        mockServer.verifyZeroInteractions()
    }

    @Test
    fun should_update_scenario_local_file(@TempDir tempDir: Path) {

        // Given
        assertScenarioSynchronization(tempDir = tempDir, scenario = localScenario)

        val modifiedScenario = Scenario(title = "A scenario") {
            When("Something happens with success") {
                SuccessTask()
            }
        }

        // When & Then
        assertScenarioSynchronization(tempDir = tempDir, scenario = modifiedScenario)
        mockServer.verifyZeroInteractions()
    }

    @Test
    fun should_create_remote_scenario_and_rename_scenario_local_file(@TempDir tempDir: Path) {

        // Given
        assertScenarioSynchronization(tempDir = tempDir, scenario = localScenario)

        val modifiedScenario = Scenario(title = "A scenario") {
            When("Something happens with success") {
                SuccessTask()
            }
        }

        val createdScenarioId = 1
        mockServer
            .`when`(
                request()
                    .withPath("/api/scenario/v2/raw")
                    .withMethod("POST")
            )
            .respond(
                response()
                    .withBody(createdScenarioId.toString())
            )

        // When & Then
        assertScenarioSynchronization(
            tempDir = tempDir,
            scenario = modifiedScenario,
            id = createdScenarioId,
            updateRemote = true
        )
        val createScenarioRequest = HttpRequest().withPath("/api/scenario/v2/raw").withMethod("POST")
        mockServer.verify(createScenarioRequest)
        val requestJson = mockServer.retrieveRecordedRequests(createScenarioRequest, Format.JSON)
        assertThat(requestJson).contains(localScenario.title)
        assertThat(requestJson).contains("Something happens with success")
        assertThat(requestJson).containsIgnoringWhitespaces("GENERATED")
    }

    @Test
    fun should_update_scenario_remotely_and_locally(@TempDir tempDir: Path) {

        // Given
        var existingScenario = Scenario(id = 1, title = "A scenario") {
            When("Something happens with success") {
                SuccessTask()
            }
        }
        assertScenarioSynchronization(
            tempDir = tempDir,
            scenario = existingScenario,
            id = existingScenario.id,
            updateRemote = false
        )

        existingScenario = Scenario(id = 1, title = "An other scenario title") {
            When("Something happens with success") {
                SuccessTask()
            }
        }

        mockServer
            .`when`(
                request()
                    .withPath("/api/scenario/v2/raw/1")
                    .withMethod("GET")
            )
            .respond(
                response()
                    .withBody(
                        "{" +
                            "\"version\": 1," +
                            "\"tags\": [\"TEST\"]" +
                            "}"
                    )
            )
        mockServer
            .`when`(
                request()
                    .withPath("/api/scenario/v2/raw")
                    .withMethod("POST")
            )
            .respond(
                response()
                    .withBody("{}")
            )

        // When & Then
        assertScenarioSynchronization(
            tempDir = tempDir,
            scenario = existingScenario,
            id = existingScenario.id,
            updateRemote = true
        )
        val updateScenarioHttpRequest = HttpRequest().withPath("/api/scenario/v2/raw").withMethod("POST")
        mockServer.verify(
            HttpRequest().withPath("/api/scenario/v2/raw/1").withMethod("GET"),
            updateScenarioHttpRequest
        )

        val requestJson = mockServer.retrieveRecordedRequests(updateScenarioHttpRequest, Format.JSON)
        assertThat(requestJson).contains(existingScenario.title)
        assertThat(requestJson).contains(existingScenario.id.toString())
        assertThat(requestJson).contains("Something happens with success")
        assertThat(requestJson).containsIgnoringWhitespaces("TEST")
        assertThat(requestJson).containsIgnoringWhitespaces("GENERATED")
    }

    @Test
    fun should_throw_exception_when_updating_scenario_on_unknown_remote_instance(@TempDir tempDir: Path) {
        // Given
        var existingScenario = Scenario(id = 1, title = "A scenario") {
            When("Something happens with success") {
                SuccessTask()
            }
        }
        val malFormattedHost = ChutneyServerInfo("unknown host", "", "")

        val exception = assertThrows<MalformedURLException> {
            existingScenario.synchronise(
                serverInfo = malFormattedHost,
                path = tempDir.absolutePathString(),
                pathCreated = tempDir.absolutePathString(),
                updateRemote = true
            )
        }
        assertThat(exception.message).isEqualTo("no protocol: unknown host/api/scenario/v2/raw/1")
    }

    private fun assertScenarioSynchronization(
        tempDir: Path,
        scenario: ChutneyScenario,
        id: Int? = null,
        updateRemote: Boolean = false
    ) {
        // When
        scenario.synchronise(
            serverInfo = chutneyServerInfo,
            path = tempDir.absolutePathString(),
            pathCreated = tempDir.absolutePathString(),
            updateRemote = updateRemote
        )

        // Then
        var tmpDirFiles = File(tempDir.absolutePathString()).walkTopDown().filter { it.isFile }
        assertThat(tmpDirFiles.count()).isEqualTo(1)
        var jsonFile = tmpDirFiles.first()
        val fileName = (id?.let { "$id-" } ?: "") + scenario.title + ".chutney.json"
        assertThat(jsonFile.name).isEqualTo(fileName)
        assertThat(jsonFile.readText()).isEqualTo(scenario.toString())
    }
}
