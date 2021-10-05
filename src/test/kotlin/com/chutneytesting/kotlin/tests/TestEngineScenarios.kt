package com.chutneytesting.kotlin.tests

import com.chutneytesting.kotlin.ChutneyTest
import com.chutneytesting.kotlin.ChutneyTestClass
import com.chutneytesting.kotlin.dsl.*

@ChutneyTestClass
class TestEngineScenarios {

    @ChutneyTest
    fun `it should work`() = Scenario(title = "scenario success") {
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
    fun `it should work with substeps`() = Scenario(title = "scenario success") {
        Given("Create variable") {
            ContextPutTask(entries = mapOf("name" to "Chutney"))
        }
        When("Hello world") {
            SuccessTask()
        }
        Then("Stupid assert") {
            Step("Stupid assert sub1") {
                StringAssertTask(document = "name".spEL, expected = "Chutney")
            }
            Step("Stupid assert sub2") {
                StringAssertTask(document = "name".spEL, expected = "Chutney")
            }
            Step("Stupid assert sub3") {
                StringAssertTask(document = "name".spEL, expected = "Chutney")
            }
        }
    }
}
