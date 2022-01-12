package com.chutneytesting.kotlin.dsl

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.skyscreamer.jsonassert.*

class ChutneyScenarioDslTest {

    @Test
    fun `is able to create chutney scenario using kotlin dsl`() {

        val `swapi GET people record` = Scenario(title = "swapi GET people record") {
            Given("I set get people service api endpoint") {
                ContextPutTask(entries = mapOf("uri" to "api/people/1"))
            }
            When("I send GET HTTP request", RetryTimeOutStrategy("5 s", "1 s")) {
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
            When("I send GET HTTP request", RetryTimeOutStrategy("5 s", "1 s")) {
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
                    validations = mapOf("always true" to "true".elEval()),
                    strategy = RetryTimeOutStrategy("5 s", "1 s")
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

    @Test
    fun `should not have null or empty object in json final inputs`() {

        val chutneyScenario = Scenario(title = "No NULL in final") {
            When("final") {
                FinalTask("success", "success")
            }
        }

        val json = "$chutneyScenario"
        assertThat(json)
            .doesNotContain("null", "{}");
    }

    @Test
    fun `should generate json scenario with kafka tasks`() {

        val chutneyScenario = Scenario(title = "Kafka tasks") {
            When("nothing") {  }
            Then("Publish") {
                KafkaBasicPublishTask(
                    target = "target", topic = "topic", payload = "payload",
                    properties = mapOf("bootstrap.servers" to "a.host:666,b.host:999")
                )
            }
            And("Consume") {
                KafkaBasicConsumeTask(
                    target = "target", topic = "topic", group = "group",
                    nbMessages = 2,
                    headerSelector = "$[json/path]",
                    contentType = "application/json",
                    ackMode = KafkaSpringOffsetCommitBehavior.MANUAL
                )
            }
        }

        JSONAssert.assertEquals(
            "/kafka-tasks.chutney.json".asResource(),
            "$chutneyScenario",
            true
        )
    }
}

