package com.chutneytesting

import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.junit.api.ChutneyTest
import com.chutneytesting.scenario.call_a_website

class ChutneyEngineTest {

    companion object {
        @JvmField
        val CHUTNEY_AS_FIELD = CHUTNEY
    }

    @ChutneyTest(environment = "CHUTNEY")
    fun testMethod(): ChutneyScenario {
        return call_a_website;
    }

    @ChutneyTest(environment = "CHUTNEY_AS_FIELD")
    fun testMethodWithEnvField(): ChutneyScenario {
        return call_a_website;
    }
}
