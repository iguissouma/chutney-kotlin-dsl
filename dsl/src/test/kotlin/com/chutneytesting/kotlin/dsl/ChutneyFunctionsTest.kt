package com.chutneytesting.kotlin.dsl

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.expression.spel.standard.SpelExpressionParser

class ChutneyFunctionsTest {

    @Test
    fun `use json function`() {
        assertELWrap(json("ctxVar"))

        assertThrows<IllegalArgumentException> { jsonPath("") }

        val unWrappedELExpr = json("ctxVar", "$.key[?(@.name='nn')]")
            .removePrefix("\${").removeSuffix("}")
        assertExpressionNotNullWhenParsed(unWrappedELExpr)
    }

    @Test
    fun `should wrap expression for evaluation by default`() {
        assertELWrap(jsonPath("json"))
        assertELWrap(jsonSerialize("obj"))

        assertELWrap(xpath("xml"))
        assertELWrap(xpathNs("xml", prefixes = mapOf("prefix" to "ns")))

        assertELWrap(getSoapBody("login", "pass", "body"))

        assertELWrap(date("date", "format"))

        assertELWrap(str_replace("text", "regexp", "replace"))

        assertELWrap(generate())
        assertELWrap(generate_uuid())
        assertELWrap(generate_randomLong())
        assertELWrap(generate_randomInt(9))

        assertELWrap(wiremock_extractHeadersAsMap("var"))
        assertELWrap(wiremock_extractParameters("var"))

        assertELWrap(nullable("var"))

        assertELWrap(micrometerRegistry("className"))

        assertELWrap(tcpPort())
        assertELWrap(tcpPorts(2))
        assertELWrap(tcpPortMin(2))
        assertELWrap(tcpPortMinMax(2, 5))
        assertELWrap(tcpPortsMinMax(2, 2, 5))
        assertELWrap(tcpPortRandomRange(100))
        assertELWrap(tcpPortsRandomRange(2, 100))
        assertELWrap(udpPort())
        assertELWrap(udpPorts(2))
        assertELWrap(udpPortMin(2))
        assertELWrap(udpPortMinMax(2, 5))
        assertELWrap(udpPortsMinMax(2, 2, 5))
        assertELWrap(udpPortRandomRange(100))
        assertELWrap(udpPortsRandomRange(2, 100))

        assertELWrap(resourcePath("name"))
        assertELWrap(resourcesPath("name"))
        assertELWrap(resourceContent("name"))

        assertELWrap(escapeJson("text"))
        assertELWrap(unescapeJson("text"))
        assertELWrap(escapeXml10("text"))
        assertELWrap(escapeXml11("text"))
        assertELWrap(unescapeXml("text"))
        assertELWrap(escapeHtml3("text"))
        assertELWrap(unescapeHtml3("text"))
        assertELWrap(escapeHtml4("text"))
        assertELWrap(unescapeHtml4("text"))
        assertELWrap(escapeSql("text"))
    }

    private fun assertELWrap(jsonPath: String) {
        assert(jsonPath.startsWith("\${") && jsonPath.endsWith("}"))
    }

    @Test
    fun `use jsonPath function`() {
        assertThrows<IllegalArgumentException> { jsonPath("") }

        assertExpressionNotNullWhenParsed(
            jsonPath("{\"key\":\"val\"}".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            jsonPath("ctxVar".spELVar, "$.key[?(@.name='nn')]", elEval = false)
        )
    }

    @Test
    fun `use jsonSerialize function`() {
        assertThrows<IllegalArgumentException> { jsonSerialize("") }

        assertExpressionNotNullWhenParsed(
            jsonSerialize("new Object()", elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            jsonSerialize("ctxVar".spELVar, elEval = false)
        )
    }

    @Test
    fun `use xpath function`() {
        assertThrows<IllegalArgumentException> { xpath("") }

        assertExpressionNotNullWhenParsed(
            xpath("<a><b attr=\"val\">inner text</b></a>".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            xpath("ctxVar".spELVar, "//b/text()", elEval = false)
        )
    }

    @Test
    fun `use xpathNs function`() {
        val prefixes = mapOf("prefix" to "ns")
        assertThrows<IllegalArgumentException> { xpathNs("", prefixes = prefixes) }
        assertThrows<IllegalArgumentException> { xpathNs("xml", prefixes = mapOf()) }

        assertExpressionNotNullWhenParsed(
            xpathNs("<a><b attr=\"val\">inner text</b></a>".elString(), prefixes = prefixes, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            xpathNs("ctxVar".spELVar, "//b/text()".elString(), prefixes, elEval = false)
        )
    }

    @Test
    fun `use getSoapBody function`() {
        assertThrows<IllegalArgumentException> { getSoapBody("", "pass", "body") }
        assertThrows<IllegalArgumentException> { getSoapBody("user", "", "body") }
        assertThrows<IllegalArgumentException> { getSoapBody("user", "pass", "") }

        assertExpressionNotNullWhenParsed(
            getSoapBody("user", "login", "<a>body</a>".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            getSoapBody("user", "login", "ctxVar".spELVar, elEval = false)
        )
    }

    @Test
    fun `use date function`() {
        assertThrows<IllegalArgumentException> { date("") }

        assertExpressionNotNullWhenParsed(
            date("2011-12-03T10:15:30Z".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            date("ctxVar".spELVar, "YYYYDDMM", elEval = false)
        )
    }

    @Test
    fun `use str_replace function`() {
        assertThrows<IllegalArgumentException> { str_replace("", "regexp") }
        assertThrows<IllegalArgumentException> { str_replace("text", "") }

        assertExpressionNotNullWhenParsed(
            str_replace("text".elString(), ".*", elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            str_replace("ctxVar".spELVar, ".*", "void", elEval = false)
        )
    }

    @Test
    fun `use generate functions`() {
        assertThrows<IllegalArgumentException> { generate_randomInt(0) }

        assertExpressionNotNullWhenParsed(
            generate(elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            generate_uuid(elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            generate_randomLong(elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            generate_randomInt(100, elEval = false)
        )
    }

    @Test
    fun `use wiremock functions`() {
        assertThrows<IllegalArgumentException> { wiremock_extractHeadersAsMap("") }
        assertThrows<IllegalArgumentException> { wiremock_extractParameters("") }

        assertExpressionNotNullWhenParsed(
            wiremock_extractHeadersAsMap("ctxVar", elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            wiremock_extractParameters("ctxVar", elEval = false)
        )
    }

    @Test
    fun `use nullable function`() {
        assertThrows<IllegalArgumentException> { nullable("") }

        assertExpressionNotNullWhenParsed(
            nullable("ctxVar", elEval = false)
        )
    }

    @Test
    fun `use micrometerRegistry function`() {
        assertThrows<IllegalArgumentException> { micrometerRegistry("") }

        assertExpressionNotNullWhenParsed(
            micrometerRegistry("org.registry", elEval = false)
        )
    }

    @Test
    fun `use tcpPort and udpPort functions`() {
        assertThrows<IllegalArgumentException> { tcpPorts(0) }
        assertThrows<IllegalArgumentException> { tcpPortMin(0) }
        assertThrows<IllegalArgumentException> { tcpPortMinMax(0, 1) }
        assertThrows<IllegalArgumentException> { tcpPortMinMax(1, 0) }
        assertThrows<IllegalArgumentException> { tcpPortMinMax(2, 1) }
        assertThrows<IllegalArgumentException> { tcpPortsMinMax(0, 1, 3) }
        assertThrows<IllegalArgumentException> { tcpPortsMinMax(5, 0, 1) }
        assertThrows<IllegalArgumentException> { tcpPortsMinMax(5, 1, 0) }
        assertThrows<IllegalArgumentException> { tcpPortsMinMax(5, 2, 1) }
        assertThrows<IllegalArgumentException> { tcpPortRandomRange(0) }
        assertThrows<IllegalArgumentException> { tcpPortsRandomRange(0, 100) }
        assertThrows<IllegalArgumentException> { tcpPortsRandomRange(5, 0) }
        assertThrows<IllegalArgumentException> { udpPorts(0) }
        assertThrows<IllegalArgumentException> { udpPortMin(0) }
        assertThrows<IllegalArgumentException> { udpPortMinMax(0, 1) }
        assertThrows<IllegalArgumentException> { udpPortMinMax(1, 0) }
        assertThrows<IllegalArgumentException> { udpPortMinMax(2, 1) }
        assertThrows<IllegalArgumentException> { udpPortsMinMax(0, 1, 3) }
        assertThrows<IllegalArgumentException> { udpPortsMinMax(5, 0, 1) }
        assertThrows<IllegalArgumentException> { udpPortsMinMax(5, 1, 0) }
        assertThrows<IllegalArgumentException> { udpPortsMinMax(5, 2, 1) }
        assertThrows<IllegalArgumentException> { udpPortRandomRange(0) }
        assertThrows<IllegalArgumentException> { udpPortsRandomRange(0, 100) }
        assertThrows<IllegalArgumentException> { udpPortsRandomRange(5, 0) }

        assertExpressionNotNullWhenParsed(
            tcpPort(elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            tcpPorts(5, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            tcpPortMin(80000, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            tcpPortMinMax(80000, 81000, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            tcpPortsMinMax(5, 80000, 81000, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            tcpPortRandomRange(100, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            tcpPortsRandomRange(5, 100, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            udpPort(elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            udpPorts(5, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            udpPortMin(80000, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            udpPortMinMax(80000, 81000, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            udpPortsMinMax(5, 80000, 81000, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            udpPortRandomRange(100, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            udpPortsRandomRange(5, 100, elEval = false)
        )
    }

    @Test
    fun `use resource functions`() {
        assertThrows<IllegalArgumentException> { resourcePath("") }
        assertThrows<IllegalArgumentException> { resourcesPath("") }
        assertThrows<IllegalArgumentException> { resourceContent("") }
        assertThrows<IllegalArgumentException> { resourceContent("path", "") }

        assertExpressionNotNullWhenParsed(
            resourcePath("org.package.resource", elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            resourcesPath("org.package.resource", elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            resourceContent("org.package.resource", elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            resourceContent("org.package.resource", Charsets.ISO_8859_1.name(), elEval = false)
        )
    }

    @Test
    fun `use escape and unescape functions`() {
        assertThrows<IllegalArgumentException> { escapeJson("") }
        assertThrows<IllegalArgumentException> { unescapeJson("") }
        assertThrows<IllegalArgumentException> { escapeXml10("") }
        assertThrows<IllegalArgumentException> { escapeXml11("") }
        assertThrows<IllegalArgumentException> { unescapeXml("") }
        assertThrows<IllegalArgumentException> { escapeHtml3("") }
        assertThrows<IllegalArgumentException> { unescapeHtml3("") }
        assertThrows<IllegalArgumentException> { escapeHtml4("") }
        assertThrows<IllegalArgumentException> { unescapeHtml4("") }

        assertExpressionNotNullWhenParsed(
            escapeJson("json".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            escapeJson("ctxVar".spELVar, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            unescapeJson("json".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            unescapeJson("ctxVar".spELVar, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            escapeXml10("xml".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            escapeXml10("ctxVar".spELVar, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            escapeXml11("xml".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            escapeXml11("ctxVar".spELVar, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            unescapeXml("xml".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            unescapeXml("ctxVar".spELVar, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            escapeHtml3("html".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            escapeHtml3("ctxVar".spELVar, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            unescapeHtml3("ctxVar".spELVar, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            unescapeHtml4("html".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            escapeHtml4("html".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            escapeHtml4("ctxVar".spELVar, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            unescapeHtml4("html".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            unescapeHtml4("ctxVar".spELVar, elEval = false)
        )
    }

    private val parser = SpelExpressionParser()
    private fun assertExpressionNotNullWhenParsed(elExpr: String) {
        assertNotNull(parser.parseExpression(elExpr))
    }
}

