package com.chutneytesting.kotlin

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.mockserver.client.MockServerClient
import org.mockserver.integration.ClientAndServer
import java.util.*

val random = Random()
internal fun randomFrom(from: Int = 1024, to: Int = 65535): Int {
    return random.nextInt(to - from) + from
}

abstract class HttpTestBase {
    private val port = randomFrom()
    var mockServer: MockServerClient = MockServerClient("localhost", port)
    val url = "http://localhost:$port"


    @BeforeAll
    fun prepare() {
        mockServer = ClientAndServer.startClientAndServer(port)
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
