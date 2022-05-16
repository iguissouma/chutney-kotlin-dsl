package com.chutneytesting.kotlin.execution

import com.chutneytesting.environment.domain.exception.EnvironmentNotFoundException
import com.chutneytesting.kotlin.asResource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

class ExecutionServiceTest {

    @Test
    fun `should retrieve empty environment when no json env defined and none asked for`(@TempDir tempDir: Path) {
        val sut = ExecutionService(tempDir.toAbsolutePath().toString())

        val noneEnvCall = sut.getEnvironment()
        assertThat(noneEnvCall.targets).isEmpty()

        val nullEnvCall = sut.getEnvironment(null)
        assertThat(nullEnvCall.targets).isEmpty()
    }

    @Test
    fun `should retrieve the environment when only one env defined and none asked for`() {
        val sut = ExecutionService(File("execution/oneEnv".asResource().path).path)

        val noneEnvCall = sut.getEnvironment()
        assertThat(noneEnvCall.name).isEqualTo("ALONE")

        val nullEnvCall = sut.getEnvironment(null)
        assertThat(nullEnvCall.name).isEqualTo("ALONE")
    }

    @Test
    fun `should throw exception when multi env defined and none asked for`() {
        val sut = ExecutionService(File("execution/multiEnv".asResource().path).path)

        assertThrows<CannotResolveDefaultEnvironmentException> {
            sut.getEnvironment()
        }

        assertThrows<CannotResolveDefaultEnvironmentException> {
            sut.getEnvironment(null)
        }
    }

    @Test
    fun `should throw exception when env not found`() {
        val sut = ExecutionService(File("execution/multiEnv".asResource().path).path)

        assertThrows<EnvironmentNotFoundException> {
            sut.getEnvironment("UNDEFINED")
        }
    }

    @Test
    fun `should retrieve an environment when env defined`() {
        val sut = ExecutionService(File("execution/oneEnv".asResource().path).path)

        val environment = sut.getEnvironment("ALONE")

        assertThat(environment.name).isEqualTo("ALONE")
        assertThat(environment.description).isEqualTo("alone environment for test")

        assertThat(environment.targets).hasSize(1)
        val target = environment.targets[0]
        val targetPropertiesAssert = SoftAssertions()
        targetPropertiesAssert.assertThat(target.name).isEqualTo("target")
        targetPropertiesAssert.assertThat(target.url).isEqualTo("url")
        targetPropertiesAssert.assertThat(target.configuration.properties).containsExactly(entry("key", "value"))
        targetPropertiesAssert.assertThat(target.configuration.security.credential?.username).isEqualTo("username")
        targetPropertiesAssert.assertThat(target.configuration.security.credential?.password).isEqualTo("password")
// Not present in dto !!
//        targetPropertiesAssert.assertThat(target.configuration.security.trustStore).isEqualTo("path")
//        targetPropertiesAssert.assertThat(target.configuration.security.trustStorePassword).isEqualTo("password")
        targetPropertiesAssert.assertThat(target.configuration.security.keyStore).isEqualTo("path")
        targetPropertiesAssert.assertThat(target.configuration.security.keyStorePassword).isEqualTo("password")
        targetPropertiesAssert.assertThat(target.configuration.security.keyPassword).isEqualTo("password")
        targetPropertiesAssert.assertThat(target.configuration.security.privateKey).isEqualTo("path")
        targetPropertiesAssert.assertAll()
    }
}
