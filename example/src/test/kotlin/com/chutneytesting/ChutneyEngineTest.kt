package com.chutneytesting

import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.junit.api.ChutneyTest
import com.chutneytesting.scenario.alwaysSuccessWithParam
import com.chutneytesting.scenario.call_a_website

class ChutneyEngineTest {

    companion object {
        @JvmField
        val ENV_AS_FIELD = CHUTNEY
    }

    @ChutneyTest(environment = "CHUTNEY")
    fun testMethod(): ChutneyScenario {
        return call_a_website;
    }

    @ChutneyTest(environment = "ENV_AS_FIELD")
    fun testMethodWithEnvField(): ChutneyScenario {
        return call_a_website;
    }

    @ChutneyTest(environment = "CHUTNEY")
    fun testMethodWithScenarioList(): List<ChutneyScenario> {
        return listOf("first", "second", "third").map {
            alwaysSuccessWithParam(it)
        }
    }
}
