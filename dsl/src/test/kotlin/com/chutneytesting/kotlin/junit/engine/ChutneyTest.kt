package com.chutneytesting.kotlin.junit.engine

import com.chutneytesting.kotlin.dsl.*
import com.chutneytesting.kotlin.junit.api.ChutneyTest

class ChutneyTest {

    @ChutneyTest
    fun withFinalTask(): ChutneyScenario {
        return Scenario(title = "A final task scenario") {
            When("Final task is registered") {
                FinalTask("final task name", "success")
            }
        }
    }

    @ChutneyTest
    fun withRetryStrategyFinalTask(): ChutneyScenario {
        return Scenario(title = "A retry strategy final task scenario") {
            Given("A date to reach") {
                ContextPutTask(
                    mapOf("dateToPass" to "now().plusSeconds(2)".spEL())
                )
            }
            When("Final task is registered to wait for time to pass") {
                FinalTask("Assert time passes...", "success",
                    strategyType = RetryTimeOutStrategy.TYPE,
                    strategyProperties = mapOf("timeOut" to "3 s", "retryDelay" to "1 s"),
                    validations = mapOf("date is past" to "now().isAfter(#dateToPass)".spEL())
                )
            }
        }
    }

    @ChutneyTest
    fun withRetryStrategy(): ChutneyScenario {
        return Scenario(title = "A scenario with retry strategy") {
            Given("A number to reach") {
                ContextPutTask(
                    mapOf("index" to "\${0}", "numToReach" to "\${3}")
                )
            }
            When("Validation is triggered multiple times", RetryTimeOutStrategy("1 s", "100 ms")) {
                Step("Update index") {
                    ContextPutTask(
                        mapOf("index" to "index + 1".spEL())
                    )
                }
                Step("Assert num is reach") {
                    AssertTask(
                        listOf("index == #numToReach".spEL())
                    )
                }
            }
        }
    }

    @ChutneyTest
    fun withInnerRetryStrategy(): ChutneyScenario {
        return Scenario(title = "A scenario with inner retry strategy") {
            Given("A number to reach") {
                ContextPutTask(
                    mapOf("index" to "\${0}", "numToReach" to "\${3}")
                )
            }
            When("Validation is triggered multiple times", RetryTimeOutStrategy("2 s", "100 ms")) {
                Step("Update index") {
                    ContextPutTask(
                        mapOf("index" to "index + 1".spEL())
                    )
                }
                Step("Another number to reach") {
                    ContextPutTask(
                        mapOf("anotherIndex" to "\${0}", "anotherNumToReach" to "\${2}")
                    )
                }
                Step("Another inner validation triggered multiple times", RetryTimeOutStrategy("1 s", "100 ms")) {
                    Step("Update another index") {
                        ContextPutTask(
                            mapOf("anotherIndex" to "anotherIndex + 1".spEL())
                        )
                    }
                    Step("Assert another num is reach") {
                        AssertTask(
                            listOf("anotherIndex == #anotherNumToReach".spEL())
                        )
                    }
                }
                Step("Assert num is reach") {
                    AssertTask(
                        listOf("index == #numToReach".spEL())
                    )
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
