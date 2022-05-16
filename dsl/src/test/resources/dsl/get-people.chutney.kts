import com.chutneytesting.kotlin.dsl.ContextPutTask
import com.chutneytesting.kotlin.dsl.HttpGetTask
import com.chutneytesting.kotlin.dsl.JsonAssertTask
import com.chutneytesting.kotlin.dsl.RetryTimeOutStrategy
import com.chutneytesting.kotlin.dsl.Scenario
import com.chutneytesting.kotlin.dsl.elEval

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
