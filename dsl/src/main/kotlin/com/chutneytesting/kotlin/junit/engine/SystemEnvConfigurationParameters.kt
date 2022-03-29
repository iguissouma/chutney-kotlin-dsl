package com.chutneytesting.kotlin.junit.engine

import org.junit.platform.engine.ConfigurationParameters
import java.util.*
import java.util.Optional.ofNullable

class SystemEnvConfigurationParameters(private val delegate: ConfigurationParameters) : ConfigurationParameters {

    private val env = System.getenv()

    override fun get(key: String?): Optional<String> {
        val delegateValue = delegate.get(key)

        return if (delegateValue.isEmpty) {
            ofNullable(env[key])
        } else {
            delegateValue
        }
    }

    override fun getBoolean(key: String?): Optional<Boolean> {
        val delegateValue = delegate.getBoolean(key)

        return if (delegateValue.isEmpty) {
            ofNullable(env[key]).map { it.toBoolean() }
        } else {
            delegateValue
        }
    }

    override fun size(): Int {
        return delegate.size() + env.size
    }
}
