package com.chutneytesting.kotlin.engine

import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.dsl.ChutneyStep
import io.github.classgraph.ClassInfo
import io.github.classgraph.MethodInfo
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor
import java.lang.IllegalStateException
import java.util.*


class ChutneyScenarioTestDescriptor(
    val classInfo: ClassInfo,
    val methodInfo: MethodInfo,
    engineId: UniqueId,
    displayName: String
) : AbstractTestDescriptor(engineId.append("chutney-scenario-${UUID.randomUUID()}", displayName), displayName) {

    val scenario: ChutneyScenario

    init {

        val testClassInstance =
            this.classInfo.loadClass().getDeclaredConstructor().newInstance()
        val getScenario = this.methodInfo.loadClassAndGetMethod()

        if (getScenario.returnType != ChutneyScenario::class.java) {
            throw IllegalStateException("Method ${getScenario.declaringClass.name}#${getScenario.name} has invalid return type, should return Scenario.")
        }

        scenario = getScenario.invoke(testClassInstance) as ChutneyScenario
        (scenario.givens + scenario.`when` + scenario.thens).forEach {
            addChild(
                ChutneyScenarioStepTestDescriptor(
                    it,
                    UniqueId.forEngine("chutney-step-${UUID.randomUUID()}"),
                    it?.description ?: "no-description"
                )
            )
        }
    }

    override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER
}

class ChutneyScenarioStepTestDescriptor(
    val step: ChutneyStep?,
    engineId: UniqueId,
    displayName: String,
) : AbstractTestDescriptor(engineId, displayName) {

    init {
        step?.subSteps?.forEach {
            addChild(
                ChutneyScenarioStepTestDescriptor(
                    it,
                    UniqueId.forEngine("chutney-step-${UUID.randomUUID()}"),
                    it.description ?: "no-description"
                )
            )
        }

    }

    override fun getType(): TestDescriptor.Type = TestDescriptor.Type.TEST
}

