package com.chutneytesting.kotlin.transformation.from_component_to_kotlin

import com.chutneytesting.kotlin.dsl.SSH_CLIENT_CHANNEL
import com.chutneytesting.kotlin.synchronize.ChutneyServerService
import com.chutneytesting.kotlin.synchronize.ChutneyServerServiceImpl
import com.chutneytesting.kotlin.util.ChutneyServerInfo
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class ComponentToKotlinDslGenerator {
    fun generateDsl(
        serverInfo: ChutneyServerInfo,
        chutneyServerService: ChutneyServerService = ChutneyServerServiceImpl
    ): String {
        val allComponents: List<ComposableStepDto> = chutneyServerService.getAllComponent(serverInfo)
        var result = ""
        allComponents.forEach { component ->
            result += if (component.steps?.size!! == 0) {
                // Leaf component
                (generateComponent(component) + "\n")
            } else {
                // Parent component
                (generateParentComponent(component) + "\n")
            }
        }

        val allScenarios: List<LinkedHashMap<String, Any>> = chutneyServerService.getAllScenarios(serverInfo)
        allScenarios.forEach { s ->
            val id = (s["metadata"] as LinkedHashMap<*, *>)["id"] as String
            if (id.contains("-")) {
                val scenario: ComposableTestCaseDto = chutneyServerService.getComposedScenario(serverInfo, id)
                result += (scenariokotlinHeader(scenario))
                result +=
"""
val `${scenario.title}` = Scenario(title = "${scenario.title}") {
    Given("le scenario") {
"""
                result += (scenariokotlin(scenario))
                result +=
"""
    }
    When("TODO Déclencheur"){}
    Then("TODO Assert"){}
}
"""

            }

        }
        return result
    }
}

private fun scenariokotlin(testCase: ComposableTestCaseDto): String {
    var result = ""
    testCase.scenario?.componentSteps?.forEach { c ->
        result +=
"""
        /**
        * computedParameters : ${c.computedParameters?.joinToString(",") { it.key + " = " + it.value }}
        * parameters : ${c.parameters?.joinToString(",") { it.key + " = " + it.value }}
        **/
"""
        result += kotlinCallFunction(c)
    }
    return result
}


private fun scenariokotlinHeader(testCase: ComposableTestCaseDto) =
"""
/**
* id : ${testCase.id}
* title : ${testCase.title}
* description : ${testCase.description}
* creationDate : ${testCase.creationDate}
* tags : ${testCase.tags}
* datasetId : ${testCase.datasetId}
* author : ${testCase.author}
* updateDate : ${testCase.updateDate}
* version : ${testCase.version}
* computedParameters : ${testCase.computedParameters?.joinToString(",") { it.key + " = " + it.value }}
* parameters : ${testCase.scenario?.parameters?.joinToString(",") { it.key + " = " + it.value }}
**/
"""

private fun generateParentComponent(component: ComposableStepDto): String {
    var result =
"""
${kotlinHeader(component)}
${kotlinFunctionName(component)}
"""
    result += component.steps?.joinToString(separator = "\n") { step ->
"""
${kotlinHeaderLight(step)}
${kotlinCallFunction(step)}
"""
    } ?: run {
        ""
    }
    result += "}"
    return result
}


private fun createStep(
    implementation: ComposableStepDto,
    args: String,
    stepName: String
) =
"""
${kotlinHeader(implementation)}
${kotlinFunctionName(implementation)}
    Step("${implementation.name}") {
        $stepName($args)
    }
}
"""

private fun kotlinFunctionName(implementation: ComposableStepDto): String {
    val functionName = formatFunctionName(implementation.name)
    return "public fun ChutneyStepBuilder.`${functionName}`() {"
}

private fun kotlinCallFunction(implementation: ComposableStepDto): String {
    val functionName = formatFunctionName(implementation.name)
    return "`${functionName}`()"
}

private fun kotlinHeader(implementation: ComposableStepDto): String {
    var result = "/**\n"
    result += "* id : ${implementation.id}\n"
    if (implementation.strategy?.type != "Default") result += "* strategy: ${implementation.strategy}\n"
    if (implementation.computedParameters?.size!! > 0) result += "* computed parameters ${
        implementation.computedParameters.joinToString(
            ","
        ) { it.key + " = " + it.value }
    }\n"
    if (implementation.parameters?.size!! > 0) result += "* parameters ${implementation.parameters.joinToString(",") { it.key + " = " + it.value }}\n"
    if (implementation.tags?.size!! > 0) result += "* tags: ${implementation.tags}\n"
    result += "**/"
    return result
}

private fun kotlinHeaderLight(implementation: ComposableStepDto): String {
    var result = "/**\n"
    if (implementation.computedParameters?.size!! > 0) result += "* computed parameters ${
        implementation.computedParameters.joinToString(
            ","
        ) { it.key + " = " + it.value }
    }\n"
    if (implementation.parameters?.size!! > 0) result += "* parameters ${implementation.parameters.joinToString(",") { it.key + " = " + it.value }}\n"
    result += "**/"
    return result
}

fun formatFunctionName(toFormat: String?): String {
    if (toFormat != null) {
        return toFormat.replace("*", "").replace("$", "").replace(":", "")
    }
    return ""
}

private fun generateComponent(component: ComposableStepDto): String {
    return when (component.action?.type) {
        "context-put" -> mapContexPutAction(component)
        "http-get" -> mapHttpGetAction(component)
        "http-post" -> mapHttpPostAction(component)
        "http-put" -> mapHttpPutAction(component)
        "amqp-clean-queues" -> mapAmqpCleanQueuesAction(component)
        "amqp-basic-consume" -> mapAmqpBasicConsumeAction(component)
        "json-assert" -> mapJsonAssertAction(component)
        "json-compare" -> mapJsonCompareAction(component)
        "string-assert" -> mapStringAssertAction(component)
        "sql" -> mapSqlAction(component)
        "sleep" -> mapSleepAction(component)
        "assert" -> mapAssertsAction(component)
        "debug" -> mapDebugAction(component)
        "groovy" -> mapGroovyAction(component)
        "ssh-client" -> mapSshClientAction(component)
        "compare" -> mapCompareAction(component)
        else -> mapTODO(component)
    }
}

private fun mapTODO(component: ComposableStepDto): String {
    return """{
       TODO("Not yet implemented") ${component.action?.type}
    }"""
}

private fun mapDebugAction(implementation: ComposableStepDto): String {
    return createStep(implementation, "", "DebugAction")
}

private fun mapCompareAction(implementation: ComposableStepDto): String {
    val inputs = implementation.action?.inputs
    val actual = inputAsString(inputs, "actual")
    val expected = inputAsString(inputs, "expected")
    val mode = inputAsString(inputs, "mode")
    val listOfArgs = listOf(
        "actual" to actual,
        "expected" to expected,
        "mode" to mode
    )
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "CompareAction")
}

private fun mapGroovyAction(implementation: ComposableStepDto): String {
    val inputs = implementation.action?.inputs
    val script = inputAsString(inputs, "script")
    val parameters = inputAsMap(inputs, "parameters")
    val outputs = outputsAsMap(implementation.action)
    val listOfArgs = listOf(
        "script" to "\"\"" + script + "\"\"",
        "parameters" to parameters,
        "outputs" to outputs
    )
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "GroovyAction")
}

private fun mapSshClientAction(implementation: ComposableStepDto): String {
    val target = target(implementation.action)
    val inputs = implementation.action?.inputs
    val outputs = outputsAsMap(implementation.action)
    var channel = SSH_CLIENT_CHANNEL.COMMAND
    try {
        channel = SSH_CLIENT_CHANNEL.valueOf(inputAsString(inputs, "channel"))
    } catch (e: Exception) {
    }

    val commands = inputs?.let { inputAsList(it, "commands") }
    val listOfArgs = listOf(
        "commands" to commands,
        "channel" to "SSH_CLIENT_CHANNEL.$channel",
        "target" to target,
        "outputs" to outputs
    )
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "SshClientAction")
}

private fun mapSleepAction(implementation: ComposableStepDto): String {
    val inputs = implementation.action?.inputs
    val duration = inputAsString(inputs, "duration")
    val listOfArgs = listOf(
        "duration" to duration
    )
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "SleepAction")
}

fun mapAssertsAction(implementation: ComposableStepDto): String {
    val input = implementation.action?.inputs
    val asserts = input?.let { inputAsList(it, "asserts") }
    val listOfArgs = listOf(
        "asserts" to asserts
    )
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "AssertAction")
}


fun mapAmqpBasicConsumeAction(implementation: ComposableStepDto): String {
    val inputs = implementation.action?.inputs
    val selector = inputAsString(inputs, "selector")
    val queueName = inputAsString(inputs, "queue-name")
    val timeout = inputAsString(inputs, "timeout")
    val nbMessages = inputs?.get("nb-messages") as Int? ?: 1
    val outputs = outputsAsMap(implementation.action)
    val target = target(implementation.action)
    val listOfArgs = listOf(
        "target" to target,
        "queueName" to queueName,
        "nbMessages" to nbMessages,
        "timeout" to timeout,
        "selector" to selector,
        "outputs" to outputs
    )
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "AmqpBasicConsumeAction")
}

fun mapJsonAssertAction(implementation: ComposableStepDto): String {
    val inputs = implementation.action?.inputs
    val document = inputAsString(inputs, "document")
    val expected = inputAsMap(inputs, "expected")
    val listOfArgs = listOf("document" to document, "expected" to expected)
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "JsonAssertAction")
}

fun mapJsonCompareAction(implementation: ComposableStepDto): String {
    val inputs = implementation.action?.inputs
    val document1 = inputAsString(inputs, "document1")
    val document2 = inputAsString(inputs, "document2")
    val comparingPaths = inputAsMap(inputs, "comparingPaths")
    val listOfArgs = listOf("document1" to document1, "document2" to document2, "comparingPaths" to comparingPaths)
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "JsonCompareAction")
}

fun mapSqlAction(implementation: ComposableStepDto): String {
    val inputs = implementation.action?.inputs
    val statements = inputs?.let { inputAsList(it, "statements") }
    val outputs = outputsAsMap(implementation.action)
    val target = target(implementation.action)
    val listOfArgs = listOf(
        "statements" to statements,
        "outputs" to outputs,
        "target" to target
    )
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "SqlAction")
}

fun mapStringAssertAction(implementation: ComposableStepDto): String {
    val inputs = implementation.action?.inputs
    val document = inputAsString(inputs, "document")
    val expected = inputAsString(inputs, "expected")
    val listOfArgs = listOf("document" to document, "expected" to expected)
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "StringAssertAction")
}

fun mapAmqpCleanQueuesAction(implementation: ComposableStepDto): String {
    val inputs = implementation.action?.inputs
    val queueNames = inputAsString(inputs, "queueNames")
    val target = target(implementation.action)
    val listOfArgs = listOf("target" to target, "queueNames" to queueNames)
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "AmqpCleanQueuesAction")
}

fun mapHttpGetAction(implementation: ComposableStepDto): String {
    val inputs = implementation.action?.inputs
    val headers = inputAsMap(inputs, "headers")
    val outputs = outputsAsMap(implementation.action)
    val target = target(implementation.action)
    val uri = uri(implementation.action)
    val timeout = inputAsString(inputs, "timeout")
    val listOfArgs = listOf(
        "target" to target,
        "uri" to uri,
        "headers" to headers,
        "timeout" to timeout,
        "outputs" to outputs,
        "strategy" to null
    )
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "HttpGetAction")
}

fun mapHttpPostAction(implementation: ComposableStepDto): String {
    val inputs = implementation.action?.inputs
    val headers = inputAsMap(inputs, "headers")
    val body = if (inputs?.get("body") is Map<*, *>) inputAsMap(
        inputs,
        "body"
    ) else inputAsString(inputs, "body")
    val outputs = outputsAsMap(implementation.action)
    val target = target(implementation.action)
    val uri = uri(implementation.action)
    val timeout = inputAsString(inputs, "timeout")
    val listOfArgs = listOf(
        "target" to target,
        "uri" to uri,
        "headers" to headers,
        "body" to body,
        "timeout" to timeout,
        "outputs" to outputs,
        "strategy" to null
    )
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "HttpPostAction")
}

fun mapHttpPutAction(implementation: ComposableStepDto): String {
    val inputs = implementation.action?.inputs
    val outputs = outputsAsMap(implementation.action)

    val target = implementation.action?.let { target(it) }
    val headers = inputAsMap(inputs, "headers")
    val body = inputAsMap(inputs, "body")
    val uri = implementation.action?.let { uri(it) }
    val timeout = inputAsString(inputs, "timeout")
    val listOfArgs = listOf(
        "target" to target,
        "uri" to uri,
        "headers" to headers,
        "timeout" to timeout,
        "body" to body,
        "strategy" to null,
        "outputs" to outputs,
    )
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "HttpPutAction")
}

fun mapContexPutAction(implementation: ComposableStepDto): String {
    val input = implementation.action?.inputs
    val outputs = outputsAsMap(implementation.action)
    val entries = inputAsMap(input, "entries")
    val listOfArgs = listOf(
        "entries" to entries,
        "outputs" to outputs
    )
    val args = mapArgs(listOfArgs)

    return createStep(implementation, args, "ContextPutAction")
}

fun outputsAsMap(implementation: StepImplementation?) =
    mapOfConstructor(implementation?.outputs)

fun inputAsString(inputs: Map<String, Any?>?, key: String) =
    escapeKotlin((inputs?.get(key) as String? ?: "")).wrapWithQuotes()

fun mapArgs(listOfArgs: List<Pair<String, Any?>>): String {
    return listOfArgs
        .filterNot { it.second == null || it.second == "".wrapWithTripleQuotes() || it.second == "mapOf()" || it.second == "listOf()" }
        .joinToString(", ") { it.first + " = " + it.second }
}

@Suppress("UNCHECKED_CAST")
fun inputAsList(inputs: Map<String, Any?>, key: String) =
    listOfConstructor(inputs[key] as List<String>?)

@Suppress("UNCHECKED_CAST")
fun inputAsMap(inputs: Map<String, Any?>?, key: String) =
    mapOfConstructor(inputs?.get(key) as Map<String, Any>?)

fun String.wrapWithQuotes(): String {
    return "\"$this\""
}

fun String.wrapWithTripleQuotes(): String {
    return "\"\"\"$this\"\"\""
}

fun listOfConstructor(
    list: List<String>?
): String {
    if (list == null) {
        return "listOf()"
    }
    return "listOf(${
        list.joinToString(",\n") { it ->
            it.split("\n").joinToString(" +\n") { (escapeKotlin(it)).wrapWithQuotes() }
        }
    })"
}

fun mapOfConstructor(
    entries: Map<String, Any?>?
): String {
    if (entries == null) {
        return "mapOf()"
    }
    return "mapOf(${
        entries.map {
            "\"${it.key}\" to \"${
                escapeKotlin(
                    if (it.value is Map<*, *>) {
                        escapeKotlin(jacksonObjectMapper().writeValueAsString(it.value as Map<*, *>))
                    } else it.value.toString() //TODO check when is Int
                )
            }\""
        }.joinToString(",\n")
    })"
}

fun escapeKotlin(s: String): String {
    return s
        .replace("\${", "\\\${")
        .replace("\"", "\\\"")
}

fun uri(implementation: StepImplementation?): String {
    val inputs = implementation?.inputs
    return if (inputs != null) {
        escapeKotlin(
            (inputs["uri"] as String? ?: "")
        ).wrapWithQuotes()
    } else {
        ""
    }
}

fun target(implementation: StepImplementation?): String = (implementation?.target as String).wrapWithQuotes()
