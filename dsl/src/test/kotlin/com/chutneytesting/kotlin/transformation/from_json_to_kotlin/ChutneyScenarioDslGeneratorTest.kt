package com.chutneytesting.kotlin.transformation.from_json_to_kotlin

import com.chutneytesting.kotlin.asResourceContent
import com.chutneytesting.kotlin.dsl.ChutneyScenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

class ChutneyScenarioDslGeneratorTest {

    @Test
    fun `abe to create kotlin chutney scenario from json`() {

        val generateDsl = ChutneyScenarioDslGenerator().generateDsl("dsl/get-people.chutney.json".asResourceContent())

        assertEquals(
            generateDsl,
            """
            >val `swapi GET people record` = Scenario(title = "swapi GET people record") {
            >    Given("I set get people service api endpoint") {
            >        ContextPutAction(entries = mapOf("uri" to "api/people/1"))
            >    }
            >    When("I send GET HTTP request", RetryTimeOutStrategy("5 s", "1 s")) {
            >        HttpGetAction(target = "swapi.dev", uri = "\${'$'}{#uri}")
            >    }
            >    Then("I receive valid HTTP response") {
            >        JsonAssertAction(document = "\${'$'}{#body}", expected = mapOf("${'$'}.name" to "Luke Skywalker"))
            >    }
            >}
            """.trimMargin('>'.toString())
        )

    }

    @Test
    fun `abe to compile kotlin dsl chutney scenario to json`() {
        val scriptContent = "dsl/get-people.chutney.kts".asResourceContent()
        val fromScript: ChutneyScenario = load(scriptContent)

        JSONAssert.assertEquals(
            "dsl/get-people.chutney.json".asResourceContent(),
            "$fromScript",
            false
        )
    }

    inline fun <reified T> load(script: String): T {
        val engine: ScriptEngine = ScriptEngineManager().getEngineByExtension("kts")
        return engine.eval(script) as T
    }
}
