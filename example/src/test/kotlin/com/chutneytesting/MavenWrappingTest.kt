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

class MavenWrappingTest : ParameterizedMethods() {

    private val launcher = Launcher(reportRootPath = "target/chutney-reports", environmentJsonRootPath = "chutney_env")

    companion object {
        fun campaign_scenarios() = ParameterizedMethods.campaign_scenarios()
        fun environments() = ParameterizedMethods.environments()
        fun environments_names() = ParameterizedMethods.environments_names()
    }

    @ParameterizedTest
    @MethodSource("campaign_scenarios")
    fun `is able to emulate a campaign on one environment by name`(scenario: ChutneyScenario) {
        launcher.run(scenario, "GOOGLE")
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
    @MethodSource("environments")
    fun `is able to run a campaign on different environments`(environment: ChutneyEnvironment) {
        launcher.run(campaign, environment)
    }

    @Test
    fun `is able to fail`() {
        launcher.run(should_fail, GOOGLE, StatusDto.FAILURE)
    }
}
