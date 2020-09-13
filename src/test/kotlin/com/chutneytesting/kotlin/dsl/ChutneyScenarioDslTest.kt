package com.chutneytesting.kotlin.dsl

import com.gregwoodfill.assert.`should equal json`
import org.junit.Test

class ChutneyScenarioDslTest {

    @Test
    fun `abe to create chutney scenario using kotlin dsl`() {

        val `swapi GET people record` = Scenario(title = "swapi GET people record") {
            Given("I set get people service api endpoint") {
                ContextPutTask(entries = mapOf("uri" to "api/people/1"))
            }
            When("I send GET HTTP request") {
                HttpGetTask(target = "swapi.dev", uri = "uri".spEL())
            }
            Then("I receive valid HTTP response") {
                JsonAssertTask(document = "body".spEL(), expected = mapOf("$.name" to "Luke Skywalker"))
            }
        }

        "$`swapi GET people record`" `should equal json` "/get-people.chutney.json".asResource()

    }

    @Test
    fun `able to create chutney scenario with substeps using kotlin dsl`() {

        val `swapi GET people record` = Scenario(title = "swapi GET people record") {
            Given("I set get people service api endpoint") {
                Step("set id") {
                    ContextPutTask(entries = mapOf("id" to "1"))
                }
                Step("set uri") {
                    ContextPutTask(entries = mapOf("uri" to "api/people/${"id".spEL()}"))
                }
            }
            When("I send GET HTTP request") {
                HttpGetTask(target = "swapi.dev", uri = "uri".spEL())
            }
            Then("I receive valid HTTP response") {
                JsonAssertTask(document = "body".spEL(), expected = mapOf("$.name" to "Luke Skywalker"))
            }
        }

        "$`swapi GET people record`" `should equal json` "/get-people-with-substeps.chutney.json".asResource()

    }

}
