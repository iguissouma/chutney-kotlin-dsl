package com.chutneytesting.kotlin.engine

import io.github.classgraph.ClassInfo
import io.github.classgraph.MethodInfo
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor


class ChutneyTestDescriptor(
    val classInfo: ClassInfo,
    val methodInfo: MethodInfo,
    engineId: UniqueId,
    displayName: String
) : AbstractTestDescriptor(engineId.append("chutney-test", displayName), displayName) {

    init {
        //addChild(ChutneyLeafTestDescriptor(UniqueId.forEngine("chutney"), "display 1"))
        //addChild(ChutneyLeafTestDescriptor(UniqueId.forEngine("chutney"), "display 5"))
    }

    override fun getType(): TestDescriptor.Type = TestDescriptor.Type.TEST
}

class ChutneyLeafTestDescriptor(
    engineId: UniqueId,
    displayName: String
) :  AbstractTestDescriptor(engineId.append("chutney-test", displayName), displayName) {
    override fun getType(): TestDescriptor.Type = TestDescriptor.Type.TEST
}

