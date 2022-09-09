package com.chutneytesting.kotlin.util

import org.junit.platform.engine.ConfigurationParameters
import java.util.*
import java.util.Optional.empty
import java.util.Optional.ofNullable

class SystemEnvConfigurationParameters(private val delegate: ConfigurationParameters?) : ConfigurationParameters {

    private val env = System.getenv()
    private val properties = System.getProperties()

    override fun get(key: String?): Optional<String> {
        val delegateValue = delegate?.get(key) ?: empty()

        return if (delegateValue.isEmpty) {
            if (ofNullable(env[key]).isEmpty) ofNullable(properties.getProperty(key))
            else ofNullable(env[key])
        } else {
            delegateValue
        }
    }

    override fun getBoolean(key: String?): Optional<Boolean> {
        val delegateValue = delegate?.getBoolean(key) ?: empty()

        return if (delegateValue.isEmpty) {
            if (ofNullable(env[key]).isEmpty) ofNullable(properties.getProperty(key)).map { it.toBoolean() }
            else ofNullable(env[key]).map { it.toBoolean() }
        } else {
            delegateValue
        }
    }

    override fun size(): Int {
        return env.size + (delegate?.size() ?: 0);
    }
}
