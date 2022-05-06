package com.chutneytesting.kotlin.junit.engine.execution

enum class ChutneyConfigurationParameters(val parameter: String) {
    CONFIG_ENVIRONMENT("junit.chutney.environment"),
    CONFIG_ENGINE_STEP_AS_TEST("junit.chutney.engine.stepAsTest"),
    CONFIG_REPORT_ROOT_PATH("junit.chutney.report.rootPath"),
    CONFIG_REPORT_FILE("junit.chutney.report.file.enabled"),
    CONFIG_REPORT_SITE("junit.chutney.report.site.enabled"),
    CONFIG_CONSOLE_LOG_COLOR("junit.chutney.log.color.enabled"),
    CONFIG_SCENARIO_LOG("junit.chutney.log.scenario.enabled"),
    CONFIG_STEP_LOG("junit.chutney.log.step.enabled")
}
