package com.chutneytesting.kotlin.dsl

import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

class ChutneyScenarioDslTest {

    @Test
    fun `is able to create chutney scenario using kotlin dsl`() {

        val `swapi GET people record` = Scenario(title = "swapi GET people record") {
            Given("I set get people service api endpoint") {
                ContextPutTask(entries = mapOf("uri" to "api/people/1"))
            }
            When("I send GET HTTP request") {
                HttpGetTask(
                    target = "swapi.dev",
                    uri = "uri".spEL(),
                    validations = mapOf("always true" to "true".elEval())
                )
            }
            Then("I receive valid HTTP response") {
                JsonAssertTask(document = "body".spEL(), expected = mapOf("$.name" to "Luke Skywalker"))
            }
        }

        JSONAssert.assertEquals(
            "/get-people.chutney.json".asResource(),
            "$`swapi GET people record`",
            true
        )
    }

    @Test
    fun `is able to create chutney scenario with substeps using kotlin dsl`() {

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

        JSONAssert.assertEquals(
            "/get-people-with-substeps.chutney.json".asResource(),
            "$`swapi GET people record`",
            true
        )

    }

    @Test
    fun `is able to create chutney scenario using kotlin dsl with functions`() {

        fun declareUri(): ChutneyStepBuilder.() -> Unit = { ContextPutTask(entries = mapOf("uri" to "api/people/1")) }

        val `swapi GET people record` = Scenario(title = "swapi GET people record") {
            Given("I set get people service api endpoint", declareUri())
            When("I send GET HTTP request") {
                HttpGetTask(
                    target = "swapi.dev",
                    uri = "uri".spEL(),
                    validations = mapOf("always true" to "true".elEval())
                )
            }
            Then("I receive valid HTTP response") {
                JsonAssertTask(document = "body".spEL(), expected = mapOf("$.name" to "Luke Skywalker"))
            }
        }

        JSONAssert.assertEquals(
            "/get-people.chutney.json".asResource(),
            "$`swapi GET people record`",
            true
        )
    }

    @Test
    fun `is able to create chutney scenario using kotlin dsl with functions and multiple assertions`() {

        fun declareUri(): ChutneyStepBuilder.() -> Unit = { ContextPutTask(entries = mapOf("uri" to "api/people/1")) }

        val `swapi GET people record` = Scenario(title = "swapi GET people record") {
            Given("I set get people service api endpoint", declareUri())
            When("I send GET HTTP request") {
                HttpGetTask(target = "swapi.dev", uri = "uri".spEL())
            }
            Then("I receive valid HTTP response") {
                JsonAssertTask(
                    document = "body".spEL(),
                    expected = mapOf("$.name" to "Luke Skywalker", "$.species" to emptyArray<String>())
                )
            }
        }

        JSONAssert.assertEquals(
            "/get-people-multiple-assertions.chutney.json".asResource(),
            "$`swapi GET people record`",
            true
        )
    }

    @Test
    fun `is able to create chutney scenario using kotlin dsl with extension functions`() {

        fun ChutneyStepBuilder.declareUri() = ContextPutTask(entries = mapOf("uri" to "api/people/1"))

        val `swapi GET people record` = Scenario(title = "swapi GET people record") {
            Given("I set get people service api endpoint") {
                declareUri()
            }
            When("I send GET HTTP request") {
                HttpGetTask(
                    target = "swapi.dev",
                    uri = "uri".spEL(),
                    validations = mapOf("always true" to "true".elEval())
                )
            }
            Then("I receive valid HTTP response") {
                JsonAssertTask(document = "body".spEL(), expected = mapOf("$.name" to "Luke Skywalker"))
            }
        }

        JSONAssert.assertEquals(
            "/get-people.chutney.json".asResource(),
            "$`swapi GET people record`",
            true
        )
    }

    @Test
    fun `is able to create chutney scenario using kotlin dsl with softAssertions`() {

        fun declareUri(): ChutneyStepBuilder.() -> Unit = { ContextPutTask(entries = mapOf("uri" to "api/people/1")) }

        val `swapi GET people record` = Scenario(title = "swapi GET people record") {
            Given("I set get people service api endpoint", declareUri())
            When("I send GET HTTP request") {
                HttpGetTask(target = "swapi.dev", uri = "uri".spEL())
            }
            Then("I receive valid HTTP response", strategy = SoftAssertStrategy()) {
                JsonAssertTask(
                    document = "body".spEL(),
                    expected = mapOf("$.name" to "Luke Skywalker", "$.species" to emptyArray<String>())
                )
            }
        }

        JSONAssert.assertEquals(
            "/get-people-multiple-assertions-soft-strategy.chutney.json".asResource(),
            "$`swapi GET people record`",
            true
        )
    }
}