package com.chutneytesting.kotlin

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junitpioneer.jupiter.ClearSystemProperty
import org.mockserver.client.MockServerClient
import org.mockserver.configuration.Configuration
import org.mockserver.integration.ClientAndServer
import util.SocketUtil
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ClearSystemProperty.ClearSystemProperties(
    ClearSystemProperty(key = "http.proxyHost"),
    ClearSystemProperty(key = "http.proxyPort"),
    ClearSystemProperty(key = "http.proxyUser"),
    ClearSystemProperty(key = "http.proxyPassword"),
    ClearSystemProperty(key = "https.proxyHost"),
    ClearSystemProperty(key = "https.proxyPort"),
    ClearSystemProperty(key = "https.proxyUser"),
    ClearSystemProperty(key = "https.proxyPassword")
)
abstract class HttpTestBase(
    mockServerPort: Int? = SocketUtil.freePort(),
    mockServerConfiguration: Configuration? = Configuration.configuration()
) {
    val mockServer: MockServerClient
    val url: String

    init {
        mockServer = ClientAndServer.startClientAndServer(mockServerConfiguration, mockServerPort)
        url = "http://localhost:${mockServer.port}"
    }

    @BeforeEach
    fun reset() {
        mockServer.reset()
    }

    @AfterAll
    fun tearDown() {
        mockServer.close()
    }
}
