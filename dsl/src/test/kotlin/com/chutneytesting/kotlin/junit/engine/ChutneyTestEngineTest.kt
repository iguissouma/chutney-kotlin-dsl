package com.chutneytesting.kotlin.junit.engine

import com.chutneytesting.kotlin.junit.engine.ChutneyTestEngine.Companion.CHUTNEY_JUNIT_ENGINE_ID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.platform.engine.DiscoverySelector
import org.junit.platform.engine.FilterResult
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClasspathResource
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClasspathRoots
import org.junit.platform.launcher.PostDiscoveryFilter
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.testkit.engine.EngineExecutionResults
import org.junit.platform.testkit.engine.EngineTestKit
import org.junit.platform.testkit.engine.EventStatistics
import org.mockito.Mockito
import org.mockito.Mockito.any
import org.mockito.Mockito.times
import java.nio.file.Path
import java.util.*


private class ChutneyTestEngineTest {

    val sut = ChutneyTestEngine()

    companion object {
        @JvmStatic
        fun emptySelectors(): Array<Any> {
            return arrayOf(
                selectClass("UnknownClass"),
                selectClasspathRoots(setOf(Path.of("../.github")))[0],
                selectClasspathResource("unknownResource")
            )
        }

        @JvmStatic
        fun containerChutneyTestSelectors(): Array<Any> {
            return arrayOf(
                selectClass("com.chutneytesting.kotlin.junit.engine.ChutneyTest"),
                selectClasspathRoots(setOf(Path.of(".")))[0],
                selectClasspathResource("com/chutneytesting/kotlin/junit/engine/ChutneyTest.class")
            )
        }
    }

    @Test
    fun should_get_engineId() {
        assertThat(sut.id).isEqualTo(CHUTNEY_JUNIT_ENGINE_ID)
    }

    @Test
    fun should_get_groupId() {
        assertThat(sut.groupId).hasValue("com.chutneytesting")
    }

    @Test
    fun should_get_artifactId() {
        assertThat(sut.artifactId).hasValue("chutney-kotlin-dsl")
    }

    @ParameterizedTest
    @MethodSource("emptySelectors")
    fun should_do_nothing_when_select_nothing(selector: DiscoverySelector) {
        val result: EngineExecutionResults = EngineTestKit.engine(CHUTNEY_JUNIT_ENGINE_ID)
            .selectors(selector)
            .execute()

        result
            .allEvents()
            //.debug(System.out)
            .assertStatistics { stats: EventStatistics ->
                stats.started(1).finished(1).succeeded(1)
            }
    }

    @ParameterizedTest
    @MethodSource("containerChutneyTestSelectors")
    fun should_execute_scenario_when_select_containerChutneyTest(selector: DiscoverySelector) {
        val result: EngineExecutionResults = EngineTestKit.engine(CHUTNEY_JUNIT_ENGINE_ID)
            .selectors(selector)
            .execute()

        result
            .allEvents()
            //.debug(System.out)
            .assertStatistics { stats: EventStatistics ->
                stats
                    .started(19)
                    .finished(19)
                    .succeeded(19)
                    .reportingEntryPublished(17)
            }
    }

    @Test
    fun should_filter_on_method_name() {
        val filter = MyPostDiscoveryFilter();
        val filterMock = Mockito.spy(filter);
        val discoveryRequest = LauncherDiscoveryRequestBuilder.request()
            .selectors(
                selectClasspathRoots(Collections.singleton(Path.of("/com/chutneytesting/kotlin/junit/engine"))),
            )
            .filters(
                filterMock
            )
            .build()

        val testEngine = ChutneyTestEngine()

        testEngine.discover(discoveryRequest, UniqueId.forEngine(testEngine.id))

        // 2 times for 2 tests in ChutneyTest
        Mockito.verify(filterMock, times(2)).apply(any());
    }

    open class MyPostDiscoveryFilter : PostDiscoveryFilter {
        override fun apply(`object`: TestDescriptor?): FilterResult {
            return FilterResult.includedIf(true);
        }
    }
}
