package com.chutneytesting.kotlin.transformation.from_component_to_kotlin

import java.time.Instant

data class ComposableTestCaseDto(
    val id: String?,
    val title: String?,
    val description: String?,
    val creationDate: Instant?,
    val tags: List<String>?,
    val datasetId: String?,
    val author: String?,
    val updateDate: Instant?,
    val version: Integer?,
    val computedParameters: List<KeyValue>?,
    val scenario: ComposableScenarioDto?
)

data class ComposableScenarioDto(
    val parameters: List<KeyValue>?,
    val componentSteps: List<ComposableStepDto>
)

