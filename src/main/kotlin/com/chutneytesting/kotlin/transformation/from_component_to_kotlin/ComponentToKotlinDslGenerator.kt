package com.chutneytesting.kotlin.transformation.from_component_to_kotlin

import com.chutneytesting.kotlin.dsl.SSH_CLIENT_CHANNEL
import com.chutneytesting.kotlin.transformation.ChutneyServerService
import com.chutneytesting.kotlin.transformation.ChutneyServerServiceImpl
import com.chutneytesting.kotlin.util.ChutneyServerInfo
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class ComponentToKotlinDslGenerator {
    fun generateDsl(serverInfo: ChutneyServerInfo, chutneyServerService: ChutneyServerService = ChutneyServerServiceImpl): String {
        val allComponents: List<ComposableStepDto> = chutneyServerService.getAllComponent(serverInfo)
        var result = ""
        allComponents.forEach { component ->
            if (component.steps?.size!! == 0) {
                // Leaf component
                result += (generateComponent(component) + "\n")
            } else {
                // Parent component
                result += (generateParentComponent(component) + "\n")
            }
        }

        val allScenarios: List<LinkedHashMap<String, Any>> = chutneyServerService.getAllScenarios(serverInfo)
        allScenarios.forEach { s ->
            val id = (s["metadata"] as LinkedHashMap<String, Any>).get("id") as String
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
    result += component.steps?.map { step ->
"""
${kotlinHeaderLight(step)}
${kotlinCallFunction(step)}
"""
    }?.joinToString(separator = "\n") ?: run {
        ""
    }
    result += "}"
    return result
}


private fun createStep(
    implementation: ComposableStepDto,
    args: String,
    stepName: String
) = """
${kotlinHeader(implementation)}
${kotlinFunctionName(implementation)}
    Step("${implementation.name}") {
        $stepName($args)
    }
}
     """

private fun kotlinFunctionName(implementation: ComposableStepDto) : String {
    val functionName = formatFunctionName(implementation.name)
    return "public fun ChutneyStepBuilder.`${functionName}`() {"
}

private fun kotlinCallFunction(implementation: ComposableStepDto): String {
    val functionName = formatFunctionName(implementation.name)
    return "`${functionName}`()"
}

private fun kotlinHeader(implementation: ComposableStepDto):String {
    var result = "/**\n"
    result += "* id : ${implementation.id}\n"
    if(implementation.strategy?.type != "Default") result += "* strategy: ${implementation.strategy}\n"
    if(implementation.computedParameters?.size!! > 0)result += "* computed parameters ${implementation.computedParameters.joinToString(",") { it.key + " = " + it.value }}\n"
    if(implementation.parameters?.size!! > 0)result += "* parameters ${implementation.parameters.joinToString(",") { it.key + " = " + it.value }}\n"
    if(implementation.tags?.size!! > 0)result += "* tags: ${implementation.tags}\n"
    result += "**/"
    return result
}

private fun kotlinHeaderLight(implementation: ComposableStepDto):String {
    var result = "/**\n"
    if(implementation.computedParameters?.size!! > 0)result += "* computed parameters ${implementation.computedParameters.joinToString(",") { it.key + " = " + it.value }}\n"
    if(implementation.parameters?.size!! > 0)result += "* parameters ${implementation.parameters.joinToString(",") { it.key + " = " + it.value }}\n"
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
    return when (component.task?.type) {
        "context-put" -> mapContexPutTask(component)
        "http-get" -> mapHttpGetTask(component)
        "http-post" -> mapHttpPostTask(component)
        "http-put" -> mapHttpPutTask(component)
        "amqp-clean-queues" -> mapAmqpCleanQueuesTask(component)
        "amqp-basic-consume" -> mapAmqpBasicConsumeTask(component)
        "json-assert" -> mapJsonAssertTask(component)
        "json-compare" -> mapJsonCompareTask(component)
        "string-assert" -> mapStringAssertTask(component)
        "sql" -> mapSqlTask(component)
        "sleep" -> mapSleepTask(component)
        "assert" -> mapAssertsTask(component)
        "debug" -> mapDebugTask(component)
        "groovy" -> mapGroovyTask(component)
        "ssh-client" -> mapSshClientTask(component)
        "compare" -> mapCompareTask(component)
        else -> mapTODO(component)
    }
}

private fun mapTODO(component: ComposableStepDto): String {
    return """{
       TODO("Not yet implemented") ${component.task?.type}
    }"""
}

private fun mapDebugTask(implementation: ComposableStepDto): String {
    return createStep(implementation, "", "DebugTask")
}

private fun mapCompareTask(implementation: ComposableStepDto): String {
    val inputs = implementation.task?.inputs
    val actual = inputAsString(inputs, "actual")
    val expected = inputAsString(inputs, "expected")
    val mode = inputAsString(inputs, "mode")
    val listOfArgs = listOf(
        "actual" to actual,
        "expected" to expected,
        "mode" to mode
    )
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "CompareTask")
}

private fun mapGroovyTask(implementation: ComposableStepDto): String {
    val inputs = implementation.task?.inputs
    val script = inputAsString(inputs, "script")
    val parameters = inputAsMap(inputs, "parameters")
    val outputs = outputsAsMap(implementation.task)
    val listOfArgs = listOf(
        "script" to "\"\"" + script + "\"\"",
        "parameters" to parameters,
        "outputs" to outputs
    )
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "GroovyTask")
}

private fun mapSshClientTask(implementation: ComposableStepDto): String {
    val target = target(implementation.task)
    val inputs = implementation.task?.inputs
    val outputs = outputsAsMap(implementation.task)
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
    return createStep(implementation, args, "SshClientTask")
}

private fun mapSleepTask(implementation: ComposableStepDto): String {
    val inputs = implementation.task?.inputs
    val duration = inputAsString(inputs, "duration")
    val listOfArgs = listOf(
        "duration" to duration
    )
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "SleepTask")
}

fun mapAssertsTask(implementation: ComposableStepDto): String {
    val input = implementation.task?.inputs
    val asserts = input?.let { inputAsList(it, "asserts") }
    val listOfArgs = listOf(
        "asserts" to asserts
    )
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "AssertTask")
}


fun mapAmqpBasicConsumeTask(implementation: ComposableStepDto): String {
    val inputs = implementation.task?.inputs
    val selector = inputAsString(inputs, "selector")
    val queueName = inputAsString(inputs, "queue-name")
    val timeout = inputAsString(inputs, "timeout")
    val nbMessages = inputs?.get("nb-messages") as Int? ?: 1
    val outputs = outputsAsMap(implementation.task)
    val target = target(implementation.task)
    val listOfArgs = listOf(
        "target" to target,
        "queueName" to queueName,
        "nbMessages" to nbMessages,
        "timeout" to timeout,
        "selector" to selector,
        "outputs" to outputs
    )
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "AmqpBasicConsumeTask")
}

fun mapJsonAssertTask(implementation: ComposableStepDto): String {
    val inputs = implementation.task?.inputs
    val document = inputAsString(inputs, "document")
    val expected = inputAsMap(inputs, "expected")
    val listOfArgs = listOf("document" to document, "expected" to expected)
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "JsonAssertTask")
}

fun mapJsonCompareTask(implementation: ComposableStepDto): String {
    val inputs = implementation.task?.inputs
    val document1 = inputAsString(inputs, "document1")
    val document2 = inputAsString(inputs, "document2")
    val comparingPaths = inputAsMap(inputs, "comparingPaths")
    val listOfArgs = listOf("document1" to document1, "document2" to document2, "comparingPaths" to comparingPaths)
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "JsonCompareTask")
}

fun mapSqlTask(implementation: ComposableStepDto): String {
    val inputs = implementation.task?.inputs
    val statements = inputs?.let { inputAsList(it, "statements") }
    val outputs = outputsAsMap(implementation.task)
    val target = target(implementation.task)
    val listOfArgs = listOf(
        "statements" to statements,
        "outputs" to outputs,
        "target" to target
    )
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "SqlTask")
}

fun mapStringAssertTask(implementation: ComposableStepDto): String {
    val inputs = implementation.task?.inputs
    val document = inputAsString(inputs, "document")
    val expected = inputAsString(inputs, "expected")
    val listOfArgs = listOf("document" to document, "expected" to expected)
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "StringAssertTask")
}

fun mapAmqpCleanQueuesTask(implementation: ComposableStepDto): String {
    val inputs = implementation.task?.inputs
    val queueNames = inputAsString(inputs, "queueNames")
    val target = target(implementation.task)
    val listOfArgs = listOf("target" to target, "queueNames" to queueNames)
    val args = mapArgs(listOfArgs)
    return createStep(implementation, args, "AmqpCleanQueuesTask")
}

fun mapHttpGetTask(implementation: ComposableStepDto): String {
    val inputs = implementation.task?.inputs
    val headers = inputAsMap(inputs, "headers")
    val outputs = outputsAsMap(implementation.task)
    val target = target(implementation.task)
    val uri = uri(implementation.task)
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
    return createStep(implementation, args, "HttpGetTask")
}

fun mapHttpPostTask(implementation: ComposableStepDto): String {
    val inputs = implementation.task?.inputs
    val headers = inputAsMap(inputs, "headers")
    val body = if (inputs?.get("body") is Map<*, *>) inputAsMap(
        inputs,
        "body"
    ) else inputAsString(inputs, "body")
    val outputs = outputsAsMap(implementation.task)
    val target = target(implementation.task)
    val uri = uri(implementation.task)
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
    return createStep(implementation, args, "HttpPostTask")
}

fun mapHttpPutTask(implementation: ComposableStepDto): String {
    val inputs = implementation.task?.inputs
    val outputs = outputsAsMap(implementation.task)

    val target = implementation.task?.let { target(it) }
    val headers = inputAsMap(inputs, "headers")
    val body = inputAsMap(inputs, "body")
    val uri = implementation.task?.let { uri(it) }
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
    return createStep(implementation, args, "HttpPutTask")
}

fun mapContexPutTask(implementation: ComposableStepDto): String {
    val input = implementation.task?.inputs
    val outputs = outputsAsMap(implementation.task)
    val entries = inputAsMap(input, "entries")
    val listOfArgs = listOf(
        "entries" to entries,
        "outputs" to outputs
    )
    val args = mapArgs(listOfArgs)

    return createStep(implementation, args, "ContextPutTask")
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

fun inputAsList(inputs: Map<String, Any?>, key: String) =
    listOfConstructor(inputs[key] as List<String>?)

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
        list.joinToString(",\n") {
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
    if (inputs != null) {
        return escapeKotlin(
            (inputs["uri"] as String? ?: "")
        ).wrapWithQuotes()
    } else {
        return ""
    }
}

fun target(implementation: StepImplementation?): String = (implementation?.target as String).wrapWithQuotes()
