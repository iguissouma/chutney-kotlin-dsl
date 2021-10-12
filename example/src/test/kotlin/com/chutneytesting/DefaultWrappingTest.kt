package com.chutneytesting

import com.chutneytesting.engine.api.execution.StatusDto
import com.chutneytesting.kotlin.dsl.ChutneyEnvironment
import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.launcher.Launcher
import com.chutneytesting.scenario.call_a_website
import com.chutneytesting.scenario.call_google
import com.chutneytesting.scenario.should_fail
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class DefaultWrappingTest : ParametrizedMethods() {

    private val launcher = Launcher()

    companion object {
        fun campaign_scenarios() = ParametrizedMethods.campaign_scenarios()
        fun environments() = ParametrizedMethods.environments()
        fun environments_names() = ParametrizedMethods.environments_names()
    }

    @ParameterizedTest
    @MethodSource("campaign_scenarios")
    fun `is able to emulate a campaign on one environment`(scenario: ChutneyScenario) {
        launcher.run(scenario, GOOGLE)
    }


    @ParameterizedTest
    @MethodSource("environments")
    fun `is able to run a scenario on different environments`(environment: ChutneyEnvironment) {
        launcher.run(call_a_website, environment)
    }

    private val campaign = listOf(
        call_google,
        call_a_website
    )

    @ParameterizedTest
    @MethodSource("environments_names")
    fun `is able to run a campaign on different environments by names`(environmentName: String) {
        launcher.run(campaign, environmentName)
    }

    @Test
    fun `is able to fail`() {
        launcher.run(should_fail, GOOGLE, StatusDto.FAILURE)
    }
}
