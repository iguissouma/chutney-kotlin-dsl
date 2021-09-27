package com.chutneytesting.kotlin.util

import com.chutneytesting.kotlin.transformation.from_component_to_kotlin.RawImplementationMapper
import com.chutneytesting.kotlin.transformation.from_component_to_kotlin.StepImplementation
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedInputStream
import java.io.InputStreamReader
import java.io.Reader
import java.net.HttpURLConnection
import java.net.Proxy
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

object HttpClient {

    private val LOGGER: Logger = LoggerFactory.getLogger(HttpClient::class.java)

    inline fun <reified T> post(serverInfo: ChutneyServerInfo, query: String, body: String): T {
        return execute(serverInfo, query, "POST", body)
    }

    inline fun <reified T> get(serverInfo: ChutneyServerInfo, query: String): T {
        return execute(serverInfo, query, "GET", "")
    }

    inline fun <reified T> execute(
        serverInfo: ChutneyServerInfo,
        query: String,
        requestMethod: String,
        body: String
    ): T {
        val connection = createConnection(serverInfo, query, requestMethod)

        if (connection is HttpsURLConnection) {
            configureSslConnection(query, connection)
        }

        if (body.isNotBlank()) {
            val os = connection.outputStream
            os.write(body.toByteArray())
            os.close()
        }
        try {
            val inputStream = BufferedInputStream(connection.inputStream)
            val reader: Reader = InputStreamReader(inputStream, "UTF-8")
            val mapper = configureObjectMapper()
            return mapper.readValue(reader, object : TypeReference<T>() {})
        } catch (e: Exception) {
            throw e
        }
    }

    fun configureObjectMapper(): ObjectMapper {
        val stepImplModule = SimpleModule()
            .addDeserializer(StepImplementation::class.java, RawImplementationMapper(null))
        return jacksonObjectMapper()
            .registerModule(stepImplModule)
            .registerModule(KotlinModule())
            .registerModule(JavaTimeModule())
    }

    fun createConnection(
        serverInfo: ChutneyServerInfo,
        query: String,
        requestMethod: String
    ): HttpURLConnection {
        val url = URL(serverInfo.remoteServerUrl + query)
        val remoteUser = serverInfo.remoteUserName
        val remotePassword = serverInfo.remoteUserPassword
        val encodedAuth = Base64.getEncoder().encodeToString("$remoteUser:$remotePassword".toByteArray())
        val authHeaderValue = "Basic $encodedAuth"

        val connection = url.openConnection(Proxy.NO_PROXY) as HttpURLConnection
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        connection.setRequestProperty("Authorization", authHeaderValue)
        connection.doOutput = true
        connection.doInput = true
        connection.requestMethod = requestMethod
        return connection
    }

    fun configureSslConnection(url: String, connection: HttpsURLConnection) {
        try {
            val sc: SSLContext = SSLContext.getInstance("TLS")
            sc.init(null, arrayOf<X509TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {}
                override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return emptyArray()
                }
            }), SecureRandom())
            val factory = sc.socketFactory

            connection.sslSocketFactory = factory
            connection.setHostnameVerifier { _, _ -> true }
        } catch (e: Throwable) {
            LOGGER.error("Problems configuring SSL connection to $url", e)
        }
    }
}




