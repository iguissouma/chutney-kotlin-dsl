package com.chutneytesting.kotlin.transformation.from_component_to_kotlin

import com.chutneytesting.kotlin.synchronize.ChutneyServerService
import com.chutneytesting.kotlin.util.ChutneyServerInfo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import java.time.Instant


class ComponentToKotlinDslGeneratorKtTest {

    private val actionStep = ComposableStepDto(
        id = "1",
        name = "First action",
        parameters = listOf(KeyValue("param1", "valueparam1")),
        computedParameters = listOf(KeyValue("computedParameter1", "valuecomputedParameter1")),
        strategy = null,
        steps = emptyList(),
        usage = "",
        tags = listOf("TAG1"),
        task = StepImplementation(
            type = "http-get",
            target = "A_TARGET",
            inputs = mapOf(
                "uri" to "/my/uri/",
                "timeout" to "5s",
                "headers" to mapOf("myHeader" to "myHeaderValue")
            ),
            outputs = mapOf("myOutput" to "outputValue"),
            validations = mapOf("myValidation" to "validationValue")
        )
    )

    private val parentStep = ComposableStepDto(
        id = "2",
        name = "Parent step",
        parameters = listOf(KeyValue("param11", "valueparam11")),
        computedParameters = listOf(KeyValue("computedParameter11", "valuecomputedParameter11")),
        strategy = null,
        steps = listOf(actionStep),
        usage = "",
        tags = listOf("TAG2"),
        task = null
    )

    @Test
    fun generateDslForAction() {
        val mockServerInfo = ChutneyServerInfo("", "", "")
        val sut = ComponentToKotlinDslGenerator()


        val steps: List<ComposableStepDto> = listOf(actionStep)
        val mockChutneyServerService = mock<ChutneyServerService> {
            on { getAllComponent(eq(mockServerInfo)) }.doReturn(steps)
        }
        val result = sut.generateDsl(mockServerInfo, mockChutneyServerService)

        assertEquals(
            result.trim(),
            """
/**
* id : 1
* strategy: null
* computed parameters computedParameter1 = valuecomputedParameter1
* parameters param1 = valueparam1
* tags: [TAG1]
**/
public fun ChutneyStepBuilder.`First action`() {
    Step("First action") {
        HttpGetTask(target = "A_TARGET", uri = "/my/uri/", headers = mapOf("myHeader" to "myHeaderValue"), timeout = "5s", outputs = mapOf("myOutput" to "outputValue"))
    }
}
            """.trim()
        )
    }

    @Test
    fun generateDslForParent() {
        val mockServerInfo = ChutneyServerInfo("", "", "")
        val sut = ComponentToKotlinDslGenerator()


        val steps: List<ComposableStepDto> = listOf(parentStep)
        val mockChutneyServerService = mock<ChutneyServerService> {
            on { getAllComponent(eq(mockServerInfo)) }.doReturn(steps)
        }
        val result = sut.generateDsl(mockServerInfo, mockChutneyServerService)

        assertEquals(
            result.trim(),
            """
/**
* id : 2
* strategy: null
* computed parameters computedParameter11 = valuecomputedParameter11
* parameters param11 = valueparam11
* tags: [TAG2]
**/
public fun ChutneyStepBuilder.`Parent step`() {

/**
* computed parameters computedParameter1 = valuecomputedParameter1
* parameters param1 = valueparam1
**/
`First action`()
}
            """.trim()
        )
    }

    @Test
    fun generateDslForScenario() {
        val mockServerInfo = ChutneyServerInfo("", "", "")
        val sut = ComponentToKotlinDslGenerator()

        val scenario = LinkedHashMap<String, Any>()
        val metadata = LinkedHashMap<String, Any>()
        metadata["id"] = "123-456"
        scenario["metadata"] = metadata
        val scenarios: List<LinkedHashMap<String, Any>> = listOf(scenario)
        val composableScenario = ComposableScenarioDto(
            parameters = listOf(KeyValue("parameter1", "parameter1")),
            componentSteps = listOf(parentStep, parentStep)
        )
        val testCase = ComposableTestCaseDto(
            id = "123-456",
            title = "scenario title",
            description = "scenario description",
            creationDate = Instant.ofEpochMilli(10000000000),
            tags = listOf("TAG1"),
            datasetId = "12",
            author = "Bob",
            updateDate = Instant.ofEpochMilli(20000000000),
            version = 42,
            computedParameters = listOf(KeyValue("computedParameter1", "valuecomputedParameter1")),
            scenario = composableScenario
        )
        val mockChutneyServerService = mock<ChutneyServerService> {
            on { getAllScenarios(eq(mockServerInfo)) }.doReturn(scenarios)
            on { getComposedScenario(eq(mockServerInfo), eq("123-456")) }.doReturn(testCase)
        }
        val result = sut.generateDsl(mockServerInfo, mockChutneyServerService)

        assertEquals(
            result.trim(),
            """
/**
* id : 123-456
* title : scenario title
* description : scenario description
* creationDate : 1970-04-26T17:46:40Z
* tags : [TAG1]
* datasetId : 12
* author : Bob
* updateDate : 1970-08-20T11:33:20Z
* version : 42
* computedParameters : computedParameter1 = valuecomputedParameter1
* parameters : parameter1 = parameter1
**/

val `scenario title` = Scenario(title = "scenario title") {
    Given("le scenario") {

        /**
        * computedParameters : computedParameter11 = valuecomputedParameter11
        * parameters : param11 = valueparam11
        **/
`Parent step`()
        /**
        * computedParameters : computedParameter11 = valuecomputedParameter11
        * parameters : param11 = valueparam11
        **/
`Parent step`()
    }
    When("TODO Déclencheur"){}
    Then("TODO Assert"){}
}
            """.trim()
        )
    }
}


