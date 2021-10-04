package com.chutneytesting.kotlin.engine

import com.chutneytesting.kotlin.ChutneyTestClass
import io.github.classgraph.ClassGraph
import org.junit.platform.commons.support.AnnotationSupport
import org.junit.platform.engine.*
import org.junit.platform.engine.discovery.PackageSelector
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import java.util.function.Predicate

class ChutneyEngine() : TestEngine {
    companion object {
        const val engineId = "chutney-engine"
        const val testScenarioAnnotationName = "com.chutneytesting.kotlin.ChutneyTest"
    }

    override fun getId(): String = engineId

    val executor: Executor = ScenarioExecutor()

    override fun discover(discoveryRequest: EngineDiscoveryRequest, uniqueId: UniqueId): TestDescriptor {
        val packageSelector = discoveryRequest.getSelectorsByType(PackageSelector::class.java)

        val classGraphScan = ClassGraph().run {
            enableAllInfo()
            packageSelector.forEach {
                acceptPackages(it.packageName)
            }
            scan()
        }

        val engineDescriptor = EngineDescriptor(uniqueId, "Chutney")

        val testClasses =
            classGraphScan.getClassesWithMethodAnnotation(testScenarioAnnotationName)
        testClasses.forEach { testClass ->
            testClass.methodInfo.forEach { testMethod ->
                if (testMethod.annotationInfo.containsName(testScenarioAnnotationName)) {
                    engineDescriptor.addChild(
                        ChutneyTestDescriptor(
                            testClass,
                            testMethod,
                            UniqueId.forEngine(id),
                            "${testMethod.className}#${testMethod.name}"
                        )
                    )
                }
            }
        }

        return engineDescriptor


    }


    override fun execute(request: ExecutionRequest?) {
        request?.let { req ->
            executor.execute(req)
        }
    }
}

interface Executor {
    fun execute(request: ExecutionRequest)
}
