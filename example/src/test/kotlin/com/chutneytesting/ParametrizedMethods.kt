package com.chutneytesting

import com.chutneytesting.scenario.call_a_website
import com.chutneytesting.scenario.call_google
import org.junit.jupiter.params.provider.Arguments
import java.util.stream.Stream

open class ParametrizedMethods {

    companion object {
        @JvmStatic
        fun campaign_scenarios(): Stream<Arguments> = Stream.of(
            Arguments.of(call_a_website),
            Arguments.of(call_google)
        )

        @JvmStatic
        fun environments(): Stream<Arguments> = Stream.of(
            Arguments.of(CHUTNEY),
            Arguments.of(GOOGLE)
        )

        @JvmStatic
        fun environments_names(): Stream<Arguments> = Stream.of(
            Arguments.of("CHUTNEY"),
            Arguments.of("GOOGLE")
        )
    }
}
