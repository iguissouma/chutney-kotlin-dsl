package com.chutneytesting.kotlin.transformation.from_json_to_kotlin

import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.dsl.asResource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

class ChutneyScenarioDslGeneratorTest {

    @Test
    fun `abe to create kotlin chutney scenario from json`() {

        val generateDsl = ChutneyScenarioDslGenerator().generateDsl("/get-people.chutney.json".asResource())

        assertEquals(
            generateDsl,
            """
            >val `swapi GET people record` = Scenario(title = "swapi GET people record") {
            >    Given("I set get people service api endpoint") {
            >        ContextPutTask(entries = mapOf("uri" to "api/people/1"))
            >    }
            >    When("I send GET HTTP request", RetryTimeOutStrategy("5 s", "1 s")) {
            >        HttpGetTask(target = "swapi.dev", uri = "\${'$'}{#uri}")
            >    }
            >    Then("I receive valid HTTP response") {
            >        JsonAssertTask(document = "\${'$'}{#body}", expected = mapOf("${'$'}.name" to "Luke Skywalker"))
            >    }
            >}
            """.trimMargin('>'.toString())
        )

    }

    @Test
    fun `abe to compile kotlin dsl chutney scenario to json`() {
        val scriptContent = "/get-people.chutney.kts".asResource()
        val fromScript: ChutneyScenario = load(scriptContent)

        JSONAssert.assertEquals(
            "/get-people.chutney.json".asResource(),
            "$fromScript",
            false
        )
    }

    inline fun <reified T> load(script: String): T {
        val engine: ScriptEngine = ScriptEngineManager().getEngineByExtension("kts")
        return engine.eval(script) as T
    }
}
