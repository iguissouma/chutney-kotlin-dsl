package com.chutneytesting.kotlin.junit.engine

import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.dsl.FinalTask
import com.chutneytesting.kotlin.dsl.Scenario
import com.chutneytesting.kotlin.dsl.SuccessTask
import com.chutneytesting.kotlin.junit.api.ChutneyTest

class ChutneyTest {

    @ChutneyTest
    fun withFinalTask(): ChutneyScenario {
        return Scenario(title = "A scenario") {
            When("Action is triggered") {
                Step("Register final task") {
                    FinalTask("final task name", "success")
                }
            }
        }
    }

    @ChutneyTest
    fun testMethod(): ChutneyScenario {
        return Scenario(title = "A scenario") {
            Given("A initial state") {
                Step("A sub step for setting the state") {
                    SuccessTask()
                }
                Step("Another sub step for setting the state") {
                    SuccessTask()
                }
            }
            When("Action is triggered") {
                Step("A sub step for action") {
                    SuccessTask()
                }
                Step("Another sub step for action") {
                    SuccessTask()
                }
            }
            Then("A new state is there") {
                Step("A sub step for validating the new state") {
                    SuccessTask()
                }
                Step("Another sub step for validating the new state") {
                    SuccessTask()
                }
            }
        }
    }

    @ChutneyTest
    fun anotherTestMethod(): ChutneyScenario {
        return Scenario(title = "Another scenario") {
            Given("A initial state") {
                Step("A sub step for setting the state") {
                    SuccessTask()
                }
            }
            When("Action is triggered") {
                Step("A sub step for action") {
                    SuccessTask()
                }
            }
            Then("A new state is there") {
                Step("A sub step for validating the new state") {
                    SuccessTask()
                }
            }
        }
    }
}
