package com.chutneytesting.scenario

import com.chutneytesting.kotlin.dsl.AssertTask
import com.chutneytesting.kotlin.dsl.ContextPutTask
import com.chutneytesting.kotlin.dsl.HttpGetTask
import com.chutneytesting.kotlin.dsl.Scenario
import com.chutneytesting.kotlin.dsl.StringAssertTask
import com.chutneytesting.kotlin.dsl.SuccessTask
import com.chutneytesting.kotlin.dsl.spEL

val call_google = Scenario(title = "Call google") {
    Given("Create variable") {
        ContextPutTask(
            entries = mapOf(
                "name" to "Chutney"
            )
        )
    }
    And("Some http call") {
        HttpGetTask(
            target = "google",
            uri = "/"
        )
    }
    When("Hello world") {
        SuccessTask()
    }
    Then("Stupid assert") {
        StringAssertTask(
            document = "name".spEL,
            expected = "Chutney"
        )
    }
}

val call_a_website = Scenario(title = "Call a website") {
    When("Hello website") {
        HttpGetTask(
            target = "website",
            uri = "/",
            validations = mapOf("http 200" to "status == 200".spEL())
        )
    }
    Then("Stupid assert") {
        SuccessTask()
    }
}

val should_fail = Scenario(title = "Call unknown and fail") {
    When("Hello unknown") {
        HttpGetTask(
            target = "unknown",
            uri = "/"
        )
    }
    Then("Fail") {
        AssertTask(listOf("status == 200".spEL))
    }
    And("Should not be executed") {
        SuccessTask()
    }
}
