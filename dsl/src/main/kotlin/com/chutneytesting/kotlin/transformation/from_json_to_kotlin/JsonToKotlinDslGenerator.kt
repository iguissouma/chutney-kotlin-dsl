package com.chutneytesting.kotlin.transformation.from_json_to_kotlin

import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.dsl.ChutneyStep
import com.chutneytesting.kotlin.dsl.ChutneyStepImpl
import com.chutneytesting.kotlin.dsl.Strategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import java.util.regex.Pattern

class ChutneyScenarioDslGenerator {

    fun generateDsl(content: String): String {
        val chutneyScenario = jacksonObjectMapper().readValue<ChutneyScenario>(content)

        return """
                >val `${chutneyScenario.title}` = Scenario(title = "${chutneyScenario.title}") {
                >    ${mapGivens(chutneyScenario)}
                >    ${mapWhen(chutneyScenario)}
                >    ${mapThens(chutneyScenario)}
                >}
            """.trimMargin(">")

    }

    fun generateDsl(file: File) = generateDsl(file.readText())

    private fun mapGivens(chutneyScenario: ChutneyScenario): String {
        return chutneyScenario.givens.mapIndexed { index: Int, step: ChutneyStep ->
            if (step.strategy != null) {
                gwta(index, "Given") + "(\"${step.description}\", ${step.strategy.toDsl()}) ${step.toDsl()}"
            } else {
                gwta(index, "Given") + "(\"${step.description}\") ${step.toDsl()}"
            }
        }.joinToString(separator = "\n")
    }

    private fun mapWhen(chutneyScenario: ChutneyScenario): String {
        if (chutneyScenario.`when`?.strategy != null) {
            return """When("${chutneyScenario.`when`.description}", ${chutneyScenario.`when`.strategy.toDsl()}) ${chutneyScenario.`when`.toDsl()}"""
        } else {
            return """When("${chutneyScenario.`when`?.description}") ${chutneyScenario.`when`?.toDsl()}"""
        }
    }


    private fun mapThens(chutneyScenario: ChutneyScenario): String {
        return chutneyScenario.thens.mapIndexed { index: Int, step: ChutneyStep ->
            if (step.strategy != null) {
                gwta(index, "Then") + "(\"${step.description}\", ${step.strategy.toDsl()}) ${step.toDsl()}"
            } else {
                gwta(index, "Then") + "(\"${step.description}\") ${step.toDsl()}"
            }
        }.joinToString(separator = "\n")
    }

    private fun gwta(index: Int, gt: String) = if (index == 0) gt else "And"
}

private fun Strategy.toDsl(): String? {
    return when (this.type) {
        "retry-with-timeout" -> mapRetryTimeoutAction(this)
        else -> return null
    }
}

private fun ChutneyStep.toDsl(): String {
    if (this.subSteps.isEmpty()) {
        val implementation = this.implementation ?: return mapTODO()
        return when (implementation.type) {
            "context-put" -> mapContexPutAction(implementation)
            "http-get" -> mapHttpGetAction(implementation)
            "http-post" -> mapHttpPostAction(implementation)
            "http-put" -> mapHttpPutAction(implementation)
            "amqp-clean-queues" -> mapAmqpCleanQueuesAction(implementation)
            "amqp-basic-consume" -> mapAmqpBasicConsumeAction(implementation)
            "json-assert" -> mapJsonAssertAction(implementation)
            "json-compare" -> mapJsonCompareAction(implementation)
            "string-assert" -> mapStringAssertAction(implementation)
            "sql" -> mapSqlAction(implementation)
            "sleep" -> mapSleepAction(implementation)
            "assert" -> mapAssertsAction(implementation)
            "debug" -> mapDebugAction()
            else -> mapTODO()
        }
    } else {
        return this.subSteps
            .joinToString(
                separator = "\n",
                prefix = " {\n",
                postfix = "}\n"
            ) { "Step(\"${it.description}\") ${it.toDsl()}" }

    }
}

private fun mapTODO(): String {
    return """{
       TODO("Not yet implemented")
    }"""
}

private fun mapRetryTimeoutAction(strategy: Strategy): String {
    val parameter = strategy.parameters
    val timeout = inputAsString(parameter, "timeOut")
    val retryDelay = inputAsString(parameter, "retryDelay")
    return "RetryTimeOutStrategy($timeout, $retryDelay)"
}

private fun mapDebugAction(): String {
    return """{
       DebugAction()
    }"""
}

private fun mapSleepAction(implementation: ChutneyStepImpl): String {
    val inputs = implementation.inputs
    val duration = inputAsString(inputs, "duration")
    val listOfArgs = listOf(
        "duration" to duration
    )
    val args = mapArgs(listOfArgs)
    return """{
       SleepAction($args)
    }"""
}

fun mapAssertsAction(implementation: ChutneyStepImpl): String {
    val input = implementation.inputs
    val asserts = inputAsMapList(input, "asserts")
    val listOfArgs = listOf(
        "asserts" to asserts
    )
    val args = mapArgs(listOfArgs)
    return """{
        AssertTrueAction($args)
    }"""
}


fun mapAmqpBasicConsumeAction(implementation: ChutneyStepImpl): String {
    val inputs = implementation.inputs
    val selector = inputAsString(inputs, "selector")
    val queueName = inputAsString(inputs, "queue-name")
    val timeout = inputAsString(inputs, "timeout")
    val nbMessages = inputs["nb-messages"] as Int? ?: 1
    val outputs = outputsAsMap(implementation)
    val target = target(implementation)
    val listOfArgs = listOf(
        "target" to target,
        "queueName" to queueName,
        "nbMessages" to nbMessages,
        "timeout" to timeout,
        "selector" to selector,
        "outputs" to outputs
    )
    val args = mapArgs(listOfArgs)
    return """{
        AmqpBasicConsumeAction($args)
    }"""
}

fun mapJsonAssertAction(implementation: ChutneyStepImpl): String {
    val inputs = implementation.inputs
    val document = inputAsString(inputs, "document")
    val expected = inputAsMap(inputs, "expected")
    val listOfArgs = listOf("document" to document, "expected" to expected)
    val args = mapArgs(listOfArgs)
    return """{
        JsonAssertAction($args)
    }"""
}

fun mapJsonCompareAction(implementation: ChutneyStepImpl): String {
    val inputs = implementation.inputs
    val document1 = inputAsString(inputs, "document1")
    val document2 = inputAsString(inputs, "document2")
    val comparingPaths = inputAsMap(inputs, "comparingPaths")
    val listOfArgs = listOf("document1" to document1, "document2" to document2, "comparingPaths" to comparingPaths)
    val args = mapArgs(listOfArgs)
    return """{
        JsonCompareAction($args)
    }"""
}

fun mapSqlAction(implementation: ChutneyStepImpl): String {
    val inputs = implementation.inputs
    val statements = inputAsList(inputs, "statements")
    val outputs = outputsAsMap(implementation)
    val target = target(implementation)
    val listOfArgs = listOf(
        "statements" to statements,
        "outputs" to outputs,
        "target" to target
    )
    val args = mapArgs(listOfArgs)
    return """{
        SqlAction($args)
    }"""
}

fun mapStringAssertAction(implementation: ChutneyStepImpl): String {
    val inputs = implementation.inputs
    val document = inputAsString(inputs, "document")
    val expected = inputAsString(inputs, "expected")
    val listOfArgs = listOf("document" to document, "expected" to expected)
    val args = mapArgs(listOfArgs)
    return """{
        StringAssertAction($args)
    }"""
}

fun mapAmqpCleanQueuesAction(implementation: ChutneyStepImpl): String {
    val inputs = implementation.inputs
    val queueNames = inputAsString(inputs, "queueNames")
    val target = target(implementation)
    val listOfArgs = listOf("target" to target, "queueNames" to queueNames)
    val args = mapArgs(listOfArgs)
    return """{
        AmqpCleanQueuesAction($args)
    }"""
}

fun mapJmsSenderAction(implementation: ChutneyStepImpl): String {
    val inputs = implementation.inputs
    val target = target(implementation)
    val headers = inputAsString(inputs, "dateDemande")
    val queueName = inputAsString(inputs, "destination")
    val payload = inputAsString(inputs, "body")
    val listOfArgs = listOf(
        "target" to target,
        "headers" to headers,
        "queueName" to queueName,
        "payload" to payload
    )
    val args = mapArgs(listOfArgs)
    return """{
        JmsSenderAction($args)
    }"""
}

fun mapHttpGetAction(implementation: ChutneyStepImpl): String {
    val inputs = implementation.inputs
    val headers = inputAsMap(inputs, "headers")
    val outputs = outputsAsMap(implementation)
    val target = target(implementation)
    val uri = uri(implementation)
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
    return """{
        HttpGetAction($args)
    }"""
}

fun mapHttpPutAction(implementation: ChutneyStepImpl): String {
    val inputs = implementation.inputs
    val target = target(implementation)
    val headers = inputAsMap(inputs, "headers")
    val body = inputAsMap(inputs, "body")
    val uri = uri(implementation)
    val timeout = inputAsString(inputs, "timeout")
    val listOfArgs = listOf(
        "target" to target,
        "uri" to uri,
        "headers" to headers,
        "timeout" to timeout,
        "body" to body,
        "strategy" to null
    )
    val args = mapArgs(listOfArgs)
    return """{
        HttpPutAction($args)
    }"""
}

fun mapContexPutAction(implementation: ChutneyStepImpl): String {
    val input = implementation.inputs
    val entries = inputAsMap(input, "entries")
    val listOfArgs = listOf(
        "entries" to entries
    )
    val args = mapArgs(listOfArgs)
    return """{
        ContextPutAction($args)
    }"""
}

private fun outputsAsMap(implementation: ChutneyStepImpl) =
    mapOfConstructor(implementation.outputs)

private fun inputAsString(inputs: Map<String, Any?>, key: String) =
    escapeKotlin((inputs[key] as String? ?: "")).wrapWithQuotes()

private fun mapArgs(listOfArgs: List<Pair<String, Any?>>): String {
    return listOfArgs
        .filterNot { it.second == null || it.second == "".wrapWithQuotes() || it.second == "mapOf()" || it.second == "listOf()" }
        .joinToString(", ") { it.first + " = " + it.second }
}

@Suppress("UNCHECKED_CAST")
private fun inputAsMapList(inputs: Map<String, Any?>, key: String) =
    listOfMapConstructor(inputs[key] as List<Map<String, Any>>?)

@Suppress("UNCHECKED_CAST")
private fun inputAsList(inputs: Map<String, Any?>, key: String) =
    listOfConstructor(inputs[key] as List<String>?)

@Suppress("UNCHECKED_CAST")
private fun inputAsMap(inputs: Map<String, Any?>, key: String) =
    mapOfConstructor(inputs[key] as Map<String, Any>?)

fun mapHttpPostAction(implementation: ChutneyStepImpl): String {
    val inputs = implementation.inputs
    val headers = inputAsMap(inputs, "headers")
    val body = if (inputs["body"] is Map<*, *>) inputAsMap(inputs, "body") else inputAsString(inputs, "body")
    val outputs = outputsAsMap(implementation)
    val target = target(implementation)
    val uri = uri(implementation)
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

    return """{
        HttpPostAction($args)
    }"""
}

fun uri(implementation: ChutneyStepImpl): String {
    val inputs = implementation.inputs
    return escapeKotlin((inputs["uri"] as String? ?: "")).wrapWithQuotes()
}

private fun target(implementation: ChutneyStepImpl): String = (implementation.target as String).wrapWithQuotes()

private fun String.wrapWithQuotes(): String {
    return "\"$this\""
}

private fun listOfConstructor(
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

private fun listOfMapConstructor(
    list: List<Map<String, Any>>?
): String {
    if (list == null) {
        return "listOf()"
    }
    return "listOf(${
        list.joinToString(",\n") {
            mapOfConstructor(it)
        }
    })"
}

private fun mapOfConstructor(
    entries: Map<String, Any>?
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
    return s//.replace("'$", "'£")
        .replace("\${", "\\\${")
        .replace("\"", "\\\"")
    //.replace("'£", "'$")
}

val spelPattern: Pattern = Pattern.compile("\\$\\{#(.*?)}")

fun toKotlinSpelString(s: String): String {
    val sb = StringBuffer()
    val m = spelPattern.matcher(s)
    while (m.find()) {
        m.appendReplacement(sb, "\\$\\{\"${m.group(1)}\".spEL()}")
    }
    m.appendTail(sb)
    return sb.toString()
}
