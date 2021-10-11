package com.chutneytesting.kotlin.tests

import com.chutneytesting.kotlin.ChutneyTest
import com.chutneytesting.kotlin.ChutneyTestClass

@ChutneyTestClass
class TestEngineJsonFileScenarios {

    @ChutneyTest("get-people.chutney.json")
    fun `it should work`() {
    }

    @ChutneyTest(value = "get-people.chutney.json", environment = "DEV")
    fun `it should work on another environment`() {
    }

}