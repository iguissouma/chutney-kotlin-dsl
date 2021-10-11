package com.chutneytesting.kotlin.engine

import com.chutneytesting.kotlin.ChutneyTest
import com.chutneytesting.kotlin.ChutneyTestClass
import com.chutneytesting.kotlin.dsl.*
import org.junit.jupiter.api.Test
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage
import org.junit.platform.testkit.engine.EngineTestKit

internal class ChutneyEngineTest {
    @Test
    fun `it should discover tests and execute`() {
        EngineTestKit
            .engine(ChutneyEngine())
            .selectors(
                selectPackage("com.chutneytesting.kotlin.engine"),
                selectClass(TestEngineScenarios::class.java)
            )
            .execute()
            .testEvents().debug()
            .assertStatistics { stats ->
                stats.started(3)
                    .finished(3)
                    .succeeded(3)
            }
    }
}

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
}
