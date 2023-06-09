package com.chutneytesting.kotlin.util

import com.chutneytesting.kotlin.transformation.from_component_to_kotlin.RawImplementationMapper
import com.chutneytesting.kotlin.transformation.from_component_to_kotlin.StepImplementation
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.paranamer.ParanamerModule
import org.apache.http.HttpHost
import org.apache.http.HttpRequest
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.auth.BasicScheme
import org.apache.http.impl.client.BasicAuthCache
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.HttpClients
import java.io.BufferedInputStream
import java.io.InputStreamReader
import java.io.Reader
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

object HttpClient {

    enum class HttpMethod { POST, GET }

    inline fun <reified T> post(serverInfo: ChutneyServerInfo, query: String, body: String): T {
        return execute(serverInfo, query, HttpMethod.POST, body)
    }

    inline fun <reified T> get(serverInfo: ChutneyServerInfo, query: String): T {
        return execute(serverInfo, query, HttpMethod.GET, "")
    }

    inline fun <reified T> execute(
        serverInfo: ChutneyServerInfo,
        query: String,
        requestMethod: HttpMethod,
        body: String
    ): T {

        val proxyHost = serverInfo.proxyUri?.let { HttpHost(it.host, it.port, it.protocol) }
        val targetHost = HttpHost(serverInfo.uri.host, serverInfo.uri.port, serverInfo.uri.protocol)

        val httpClientContext = buildHttpClientContext(targetHost)
        val httpRequest = buildHttpRequest(requestMethod, query, body)
        val httpClient = buildHttpClient(serverInfo, targetHost, proxyHost)

        val httpResponse = httpClient.execute(targetHost, httpRequest, httpClientContext)

        if (httpResponse.statusLine.statusCode >= 300) {
            throw HttpClientException("Call to server returned status ${httpResponse.statusLine}")
        }

        try {
            val inputStream = BufferedInputStream(httpResponse.entity.content)
            val reader: Reader = InputStreamReader(inputStream, Charsets.UTF_8)
            val mapper = configureObjectMapper()
            return mapper.readValue(reader, object : TypeReference<T>() {})
        } catch (e: Exception) {
            throw HttpClientException(e)
        }
    }

    fun configureObjectMapper(): ObjectMapper {
        val stepImplModule = SimpleModule()
            .addDeserializer(StepImplementation::class.java, RawImplementationMapper(null))
        return jacksonObjectMapper()
            .registerModule(stepImplModule)
            .registerModule(JavaTimeModule())
            .registerModule(ParanamerModule())
    }

    private fun dumbSSLContext(): SSLContext {
        val sc: SSLContext = SSLContext.getInstance("TLS")
        sc.init(null, arrayOf<X509TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {}
            override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return emptyArray()
            }
        }), SecureRandom())
        return sc
    }

    fun buildHttpClientContext(targetHost: HttpHost): HttpClientContext {
        val authCache = BasicAuthCache()
        authCache.put(targetHost, BasicScheme())

        val httpClientContext = HttpClientContext.create()
        httpClientContext.authCache = authCache

        return httpClientContext
    }

    fun buildHttpRequest(
        requestMethod: HttpMethod,
        uri: String,
        body: String
    ): HttpRequest {
        val httpRequest: HttpRequest
        when (requestMethod) {
            HttpMethod.POST -> {
                httpRequest = HttpPost(uri)
                httpRequest.entity = StringEntity(body, ContentType.APPLICATION_JSON)
            }

            HttpMethod.GET -> httpRequest = HttpGet(uri)
        }
        return httpRequest
    }

    fun buildHttpClient(
        serverInfo: ChutneyServerInfo,
        targetHost: HttpHost,
        proxyHost: HttpHost?
    ): HttpClient {
        val credentialsProvider = buildCredentialProvider(serverInfo, targetHost, proxyHost)

        val httpClient = HttpClients.custom()
            .setSSLContext(dumbSSLContext())
            .setDefaultCredentialsProvider(credentialsProvider)

        proxyHost?.let { httpClient.setProxy(proxyHost) }

        return httpClient.build()
    }

    private fun buildCredentialProvider(
        serverInfo: ChutneyServerInfo,
        targetHost: HttpHost,
        proxyHost: HttpHost?
    ): CredentialsProvider {
        val credentialsProvider = BasicCredentialsProvider()
        serverInfo.proxyUser?.let {
            credentialsProvider.setCredentials(
                AuthScope(proxyHost),
                UsernamePasswordCredentials(serverInfo.proxyUser, serverInfo.proxyPassword)
            )
        }
        credentialsProvider.setCredentials(
            AuthScope(targetHost),
            UsernamePasswordCredentials(serverInfo.user, serverInfo.password)
        )
        return credentialsProvider
    }
}

class HttpClientException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(cause: Throwable?) : super(cause)
}
