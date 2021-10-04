package com.chutneytesting.kotlin.tests

import com.chutneytesting.kotlin.ChutneyTest
import com.chutneytesting.kotlin.ChutneyTestClass
import com.chutneytesting.kotlin.dsl.*

@ChutneyTestClass
class TestEngineScenariosTwo {

    @ChutneyTest
    fun `it should work 1`() = Scenario(title = "scenario success") {
        Given("Create variable") {
            ContextPutTask(entries = mapOf("name" to "Chutney"))
        }
        When("Hello world") {
            SuccessTask()
        }
        Then("Stupid assert") {
            StringAssertTask(document = "name".spEL, expected = "Chutney")
        }
    }

    @ChutneyTest
    fun `it should work 2`() = Scenario(title = "scenario success") {
        Given("Create variable") {
            ContextPutTask(entries = mapOf("name" to "Chutney"))
        }
        When("Hello world") {
            SuccessTask()
        }
        Then("Stupid assert") {
            StringAssertTask(document = "name".spEL, expected = "Chutney")
        }
    }
}
