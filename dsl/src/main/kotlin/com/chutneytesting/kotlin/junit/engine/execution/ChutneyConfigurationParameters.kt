package com.chutneytesting.kotlin.junit.engine.execution

import com.chutneytesting.kotlin.execution.CHUTNEY_ENV_ROOT_PATH
import com.chutneytesting.kotlin.execution.report.CHUTNEY_REPORT_ROOT_PATH

enum class ChutneyConfigurationParameters(val parameter: String, private val default: Any? = null) {
    CONFIG_ENVIRONMENT("junit.chutney.environment.default", null),
    CONFIG_ENVIRONMENT_ROOT_PATH("junit.chutney.environment.rootPath", CHUTNEY_ENV_ROOT_PATH),
    CONFIG_ENGINE_STEP_AS_TEST("junit.chutney.engine.stepAsTest", true),
    CONFIG_REPORT_ROOT_PATH("junit.chutney.report.rootPath", CHUTNEY_REPORT_ROOT_PATH),
    CONFIG_REPORT_FILE("junit.chutney.report.file.enabled", true),
    CONFIG_REPORT_SITE("junit.chutney.report.site.enabled", true),
    CONFIG_CONSOLE_LOG_COLOR("junit.chutney.log.color.enabled", true),
    CONFIG_SCENARIO_LOG("junit.chutney.log.scenario.enabled", true),
    CONFIG_STEP_LOG("junit.chutney.log.step.enabled", true);

    fun defaultBoolean(): Boolean {
        return when (this.default) {
            is Boolean -> this.default
            is String -> this.default.toBoolean()
            else -> false
        }
    }

    fun defaultNumber(): Number {
        return when (this.default) {
            is Number -> this.default
            else -> 0
        }
    }

    fun defaultString(): String? {
        return when (this.default) {
            is String -> this.default
            else -> this.default?.toString()
        }
    }
}
