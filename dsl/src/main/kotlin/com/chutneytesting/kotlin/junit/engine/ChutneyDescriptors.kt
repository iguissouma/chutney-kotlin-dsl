package com.chutneytesting.kotlin.junit.engine

import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.dsl.ChutneyStep
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import org.junit.platform.engine.support.descriptor.EngineDescriptor

class ChutneyEngineDescriptor(uniqueId: UniqueId, displayName: String) : EngineDescriptor(uniqueId, displayName)

class ChutneyClassDescriptor(
    uniqueId: UniqueId,
    displayName: String,
    source: TestSource
) : AbstractTestDescriptor(uniqueId, displayName, source) {

    override fun getType(): TestDescriptor.Type {
        return TestDescriptor.Type.CONTAINER
    }
}

class ChutneyScenarioDescriptor(
    uniqueId: UniqueId,
    displayName: String,
    source: TestSource,
    val chutneyScenario: ChutneyScenario,
    val environmentName: String,
    private val stepAsTest: Boolean
) : AbstractTestDescriptor(uniqueId, displayName, source) {

    override fun getType(): TestDescriptor.Type {
        return if (stepAsTest) TestDescriptor.Type.CONTAINER_AND_TEST else TestDescriptor.Type.TEST
    }
}

class ChutneyStepDescriptor(
    uniqueId: UniqueId,
    displayName: String,
    source: TestSource,
    private val chutneyStep: ChutneyStep
) : AbstractTestDescriptor(uniqueId, displayName, source) {

    override fun getType(): TestDescriptor.Type {
        return chutneyStep.implementation?.let { TestDescriptor.Type.TEST }
            ?: TestDescriptor.Type.CONTAINER_AND_TEST
    }
}
