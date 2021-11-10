import com.chutneytesting.kotlin.dsl.*

Scenario(title = "swapi GET people record") {
    Given("I set get people service api endpoint") {
        ContextPutTask(entries = mapOf("uri" to "api/people/1"))
    }
    When("I send GET HTTP request", RetryTimeOutStrategy("5 s", "1 s")) {
        HttpGetTask(target = "swapi.dev", uri = "\${#uri}", validations = mapOf("always true" to "true".elEval()))
    }
    Then("I receive valid HTTP response") {
        JsonAssertTask(document = "\${#body}", expected = mapOf("$.name" to "Luke Skywalker"))
    }
}
