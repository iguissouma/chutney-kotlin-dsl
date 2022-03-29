package com.chutneytesting.kotlin.launcher

import com.chutneytesting.engine.api.execution.StatusDto
import com.chutneytesting.engine.api.execution.StatusDto.SUCCESS
import com.chutneytesting.kotlin.dsl.ChutneyEnvironment
import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.execution.CHUTNEY_ROOT_PATH
import com.chutneytesting.kotlin.execution.ExecutionService
import com.chutneytesting.kotlin.execution.report.CHUTNEY_REPORT_ROOT_PATH
import com.chutneytesting.kotlin.execution.report.AnsiReportWriter
import com.chutneytesting.kotlin.execution.report.JsonReportWriter
import org.assertj.core.api.Assertions
import org.assertj.core.api.SoftAssertions

class Launcher(
    private val reportRootPath: String = CHUTNEY_REPORT_ROOT_PATH,
    environmentJsonRootPath: String = CHUTNEY_ROOT_PATH
) {

    private val executionService = ExecutionService(environmentJsonRootPath)

    fun run(
        scenario: ChutneyScenario,
        environmentName: String,
        expected: StatusDto = SUCCESS
    ) {
        run(scenario, executionService.getEnvironment(environmentName), expected)
    }

    fun run(
        scenarios: List<ChutneyScenario>,
        environmentName: String,
        expected: StatusDto = SUCCESS
    ) {
        run(scenarios, executionService.getEnvironment(environmentName), expected)
    }

    fun run(
        scenario: ChutneyScenario,
        environment: ChutneyEnvironment,
        expected: StatusDto = SUCCESS
    ) {
        Assertions.assertThat(run(scenario, environment)).isEqualTo(expected)
    }

    fun run(
        scenarios: List<ChutneyScenario>,
        environment: ChutneyEnvironment,
        expected: StatusDto = SUCCESS
    ) {
        val softly = SoftAssertions()
        scenarios.map { runSoftly(it, environment, expected, softly) }
        softly.assertAll()
    }

    private fun runSoftly(
        scenario: ChutneyScenario,
        environment: ChutneyEnvironment,
        expected: StatusDto,
        softly: SoftAssertions
    ) {
        val status = run(scenario, environment)
        softly.assertThat(status).isEqualTo(expected)
    }

    private fun run(
        scenario: ChutneyScenario,
        environment: ChutneyEnvironment
    ): StatusDto? {
        val report = executionService.waitLastReport(executionService.execute(scenario, environment))
        AnsiReportWriter().printReport(report)
        JsonReportWriter.writeReport(report, reportRootPath)
        return report.status
    }
}
