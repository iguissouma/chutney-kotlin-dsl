package com.chutneytesting.kotlin.dsl

import com.gregwoodfill.assert.`should equal json`
import de.swirtz.ktsrunner.objectloader.KtsObjectLoader
import loadResource
import org.junit.Test
import kotlin.test.assertEquals

class ChutneyScenarioDslGeneratorTest {
    @Test
    fun `abe to create kotlin chutney scenario from json`() {

        val generateDsl = ChutneyScenarioDslGenerator().generateDsl(loadResource("/get-people.chutney.json"))

        assertEquals(generateDsl,
            """
            >val `swapi GET people record` = Scenario(title = "swapi GET people record") {
            >    Given("I set get people service api endpoint") {
            >        ContextPutTask(entries = mapOf("uri" to "api/people/1"))
            >    }
            >    When("I send GET HTTP request") {
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
        val scriptContent = loadResource("/get-people.chutney.kts")
        val fromScript: ChutneyScenario = KtsObjectLoader().load(scriptContent)
        "$fromScript" `should equal json` loadResource("/get-people.chutney.json")
    }
}
