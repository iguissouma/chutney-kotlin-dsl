package com.chutneytesting.kotlin.util

import com.chutneytesting.kotlin.HttpTestBase
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockserver.matchers.Times
import org.mockserver.model.Header
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.MediaType

class HttpClientTest : HttpTestBase() {

    @Test
    fun use_explicit_http_proxy_with_authentication() {
        // Given
        val serverInfo = ChutneyServerInfo(
            "http://chutney.server:666",
            "user",
            "password",
            "http://${mockServer.remoteAddress().hostString}:${mockServer.remoteAddress().port}",
            "proxyUer",
            "proxyPassword"
        )

        val proxyAuthRequestExpectation = mockServer
            .`when`(
                HttpRequest.request(".*"),
                Times.exactly(1)
            )
            .respond(
                HttpResponse.response()
                    .withStatusCode(407)
                    .withHeader("proxy-authenticate", "Basic realm=\"proxyRealm\"")
            )[0]
        val proxyAuthExpectation = mockServer
            .`when`(
                HttpRequest.request(".*")
                    .withHeader(Header.header("Proxy-Authorization")),
                Times.exactly(1)
            )
            .respond(
                HttpResponse.response()
                    .withStatusCode(200)
                    .withBody("666", MediaType.JSON_UTF_8)
            )[0]

        // When
        HttpClient.get<Any>(serverInfo, "/path")

        // Then
        mockServer.verify(proxyAuthRequestExpectation.id, proxyAuthExpectation.id)
    }

    @Test
    fun use_implicit_http_proxy_with_authentication() {
        // Given
        System.setProperty("http.proxyHost", mockServer.remoteAddress().hostString)
        System.setProperty("http.proxyPort", mockServer.remoteAddress().port.toString())
        System.setProperty("http.proxyUser", "proxyUer")
        System.setProperty("http.proxyPassword", "proxyPassword")

        val serverInfo = ChutneyServerInfo("http://chutney.server:666", "user", "password")

        val proxyAuthRequestExpectation = mockServer
            .`when`(
                HttpRequest.request(".*"),
                Times.exactly(1)
            )
            .respond(
                HttpResponse.response()
                    .withStatusCode(407)
                    .withHeader("proxy-authenticate", "Basic realm=\"proxyRealm\"")
            )[0]
        val proxyAuthExpectation = mockServer
            .`when`(
                HttpRequest.request(".*")
                    .withHeader(Header.header("Proxy-Authorization")),
                Times.exactly(1)
            )
            .respond(
                HttpResponse.response()
                    .withStatusCode(200)
                    .withBody("666", MediaType.JSON_UTF_8)
            )[0]

        // When
        HttpClient.get<Any>(serverInfo, "/path")

        // Then
        mockServer.verify(proxyAuthRequestExpectation.id, proxyAuthExpectation.id)
    }

    @ParameterizedTest
    @ValueSource(ints = [300, 310, 401, 403, 500])
    fun throw_when_http_status_ko(statusCode: Int) {
        // Given
        val serverInfo = ChutneyServerInfo(url, "user", "password")
        val requestKOExpectation = mockServer
            .`when`(
                HttpRequest.request(".*"),
                Times.exactly(1)
            )
            .respond(
                HttpResponse.response()
                    .withStatusCode(statusCode)
            )[0]

        // When / Then
        org.junit.jupiter.api.assertThrows<HttpClientException> {
            HttpClient.get<Any>(serverInfo, "/path")
        }

        mockServer.verify(requestKOExpectation.id)
    }
}
