package com.chutneytesting.example.http

import com.chutneytesting.example.scenario.FILM
import com.chutneytesting.example.scenario.FILMS_ENDPOINT
import com.chutneytesting.example.scenario.HTTP_TARGET_NAME
import com.chutneytesting.example.scenario.http_scenario
import com.chutneytesting.kotlin.dsl.ChutneyEnvironment
import com.chutneytesting.kotlin.dsl.Environment
import com.chutneytesting.kotlin.launcher.Launcher
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.testcontainers.containers.MockServerContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName


@Testcontainers
class HttpScenarioTest {

    private var httpAddress: String = ""
    private var httpPort: Int = 0
    private var environment: ChutneyEnvironment = ChutneyEnvironment("default value")

    private val MOCKSERVER_IMAGE = DockerImageName
        .parse("mockserver/mockserver")
        .withTag("mockserver-5.15.0") // same version as mockserver client

    @Container
    private var mockServer: MockServerContainer = MockServerContainer(MOCKSERVER_IMAGE)


    @BeforeEach
    fun setUp() {
        httpAddress = "localhost" // mockServer.host doesn't work actually because it can be an ipv6 which is not yet supported(https://trello.com/c/avirYHMr/173-support-ipv6)
        httpPort = mockServer.firstMappedPort
        environment = Environment(name = "local", description = "local environment") {
            Target {
                Name(HTTP_TARGET_NAME)
                Url("http://$httpAddress:$httpPort")
            }
        }
    }

    @Test
    fun `create & update film` () {
        val mockserver = MockServerClient(httpAddress, httpPort)
        val filmId = "1"
        mockserver.`when`(request().withPath(FILMS_ENDPOINT).withMethod("POST"))
                .respond(response().withStatusCode(201).withBody(filmId))
        mockserver.`when`(request().withPath("$FILMS_ENDPOINT/$filmId").withMethod("PATCH"))
            .respond(response().withStatusCode(200))
        mockserver.`when`(request().withPath("$FILMS_ENDPOINT/$filmId").withMethod("GET"))
            .respond(response().withStatusCode(200).withBody(FILM.replaceFirst("%rating%", "79").trimIndent()))


        Launcher().run(http_scenario, environment)
    }

}
