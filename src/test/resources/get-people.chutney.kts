import com.chutneytesting.kotlin.dsl.Scenario

Scenario(title = "swapi GET people record") {
    Given("I set get people service api endpoint") {
        ContextPutTask(entries = mapOf("uri" to "api/people/1"))
    }
    When("I send GET HTTP request") {
        HttpGetTask(target = "swapi.dev", uri = "\${#uri}")
    }
    Then("I receive valid HTTP response") {
        JsonAssertTask(document = "\${#body}", expected = mapOf("$.name" to "Luke Skywalker"))
    }
}
