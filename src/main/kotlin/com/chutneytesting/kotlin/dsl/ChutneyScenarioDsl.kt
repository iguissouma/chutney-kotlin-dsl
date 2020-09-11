package com.chutneytesting.kotlin.dsl

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.core.util.Separators
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature


@DslMarker
annotation class ChutneyScenarioDsl

fun Scenario(id: Int? = null, title: String, block: ChutneyScenarioBuilder.() -> Unit): ChutneyScenario {
    return ChutneyScenarioBuilder(id, title).apply(block).build()
}

@ChutneyScenarioDsl
class ChutneyScenarioBuilder(val id: Int? = null, val title: String = "") {
    var description = title
    private val givens = mutableListOf<ChutneyStep>()
    private var `when`: ChutneyStep? = null
    private val thens = mutableListOf<ChutneyStep>()

    fun Given(description: String = "", block: ChutneyStepBuilder.() -> Unit) {
        givens.add(ChutneyStepBuilder(description).apply(block).build())
    }

    fun When(description: String = "", block: ChutneyStepBuilder.() -> Unit) {
        `when` = ChutneyStepBuilder(description).apply(block).build()
    }

    fun Then(description: String = "", block: ChutneyStepBuilder.() -> Unit) {
        thens.add(ChutneyStepBuilder(description).apply(block).build())
    }

    fun And(description: String = "", block: ChutneyStepBuilder.() -> Unit) {
        when {
            `when` != null -> thens.add(ChutneyStepBuilder(description).apply(block).build())
            else -> givens.add(ChutneyStepBuilder(description).apply(block).build())
        }
    }

    fun build(): ChutneyScenario = ChutneyScenario(id, title, description, givens, `when`, thens)

}

open class Strategy(val type: String, val parameters: Map<String, String>)
class RetryTimeOutStrategy(timeout: String, retryDelay: String) :
    Strategy(type = "retry-with-timeout", parameters = mapOf("timeOut" to timeout, "retryDelay" to retryDelay))

@ChutneyScenarioDsl
class ChutneyStepBuilder(var description: String = "") {

    var subSteps = mutableListOf<ChutneyStep>()
    var implementation: ChutneyStepImpl? = null
    var strategy: Strategy? = null

    fun Implementation(block: ChutneyStepImplBuilder.() -> Unit) {
        implementation = ChutneyStepImplBuilder().apply(block).build()
    }

    fun Step(description: String = "", block: ChutneyStepBuilder.() -> Unit) {
        subSteps.add(ChutneyStepBuilder(description).apply(block).build())
    }

    fun ContextPutTask(entries: Map<String, Any>, outputs: Map<String, Any> = mapOf()) {
        implementation =
            ChutneyStepImpl(type = "context-put", target = null, inputs = entries.toEntries(), outputs = outputs)
    }

    fun DebugTask() {
        implementation = ChutneyStepImpl(type = "debug", target = null, inputs = mapOf(), outputs = mapOf())
    }

    fun HttpGetTask(
        target: String,
        uri: String,
        headers: Map<String, Any> = mapOf(),
        timeout: String = "2 sec",
        outputs: Map<String, Any> = mapOf(),
        strategy: Strategy? = null
    ) {
        implementation = ChutneyStepImpl(
            type = "http-get",
            target = target,
            inputs = mapOf("uri" to uri, "headers" to headers, "timeout" to timeout),
            outputs = outputs
        )
        this.strategy = strategy

    }

    fun HttpDeleteTask(
        target: String,
        uri: String,
        headers: Map<String, Any> = mapOf(),
        timeout: String = "2 sec",
        outputs: Map<String, Any> = mapOf(),
        strategy: Strategy? = null
    ) {
        implementation = ChutneyStepImpl(
            type = "http-delete",
            target = target,
            inputs = mapOf("uri" to uri, "headers" to headers, "timeout" to timeout),
            outputs = outputs
        )
        this.strategy = strategy

    }

    fun HttpPostTask(
        target: String,
        uri: String,
        headers: Map<String, Any> = mapOf(),
        body: Map<String, Any> = mapOf(),
        timeout: String = "2 sec",
        outputs: Map<String, Any> = mapOf("body".toSpelPair()),
        strategy: Strategy? = null
    ) {
        implementation = ChutneyStepImpl(
            type = "http-post",
            target = target,
            inputs = mapOf("uri" to uri, "headers" to headers, "body" to body, "timeout" to timeout),
            outputs = outputs
        )
        this.strategy = strategy
    }

    fun KafkaBasicPublishTask(
        target: String,
        topic: String,
        headers: Map<String, Any> = mapOf(),
        payload: Map<String, Any> = mapOf()
    ) {
        implementation = ChutneyStepImpl(
            type = "kafka-basic-publish",
            target = target,
            inputs = mapOf("topic" to topic, "headers" to headers, "payload" to payload),
            outputs = mapOf()
        )
    }

    fun KafkaBasicConsumeTask(
        target: String,
        topic: String,
        group: String,
        properties: Map<String, String> = mapOf("auto.offset.reset" to "earliest"),
        timeout: String = "60 sec",
        selector: String,
        outputs: Map<String, Any> = mapOf()
    ) {
        implementation = ChutneyStepImpl(
            type = "kafka-basic-consume",
            target = target,
            inputs = mapOf(
                "topic" to topic,
                "group" to group,
                "timeout" to timeout,
                "selector" to selector,
                "properties" to properties
            ),
            outputs = outputs
        )
    }

    fun AmqpBasicConsumeTask(
        target: String,
        queueName: String,
        nbMessages: Int = 1,
        timeout: String = "60 sec",
        selector: String,
        outputs: Map<String, Any> = mapOf()
    ) {
        implementation = ChutneyStepImpl(
            type = "amqp-basic-consume",
            target = target,
            inputs = mapOf(
                "queue-name" to queueName,
                "nb-messages" to nbMessages,
                "timeout" to timeout,
                "selector" to selector
            ),
            outputs = outputs
        )
    }

    fun AmqpCleanQueuesTask(target: String, queueNames: String) {
        implementation = ChutneyStepImpl(
            type = "amqp-clean-queues",
            target = target,
            inputs = mapOf("queue-names" to queueNames),
            outputs = mapOf()
        )
    }

    fun JmsCleanQueuesTask(target: String, queueName: String) {
        implementation = ChutneyStepImpl(
            type = "jms-clean-queue",
            target = target,
            inputs = mapOf("destination" to queueName),
            outputs = mapOf()
        )
    }

    fun JmsSenderTask(target: String, queueName: String, payload: String) {
        implementation = ChutneyStepImpl(
            type = "jms-sender",
            target = target,
            inputs = mapOf(
                "destination" to queueName,
                "body" to payload,
                "headers" to mapOf("X--JMS-VERSION" to "1.0", "X--HEADER-1" to "42")
            ),
            outputs = mapOf()
        )
    }

    fun JsonAssertTask(document: String, expected: Map<String, Any> = mapOf()) {
        implementation = ChutneyStepImpl(
            type = "json-assert",
            target = null,
            inputs = mapOf("document" to document, "expected" to expected),
            outputs = mapOf()
        )
    }

    fun JsonAssertTask(documentVariable: String, expectationsVariable: String) {
        implementation = ChutneyStepImpl(
            type = "json-assert",
            target = null,
            inputs = mapOf("document" to documentVariable.spEL, "expected" to expectationsVariable.spEL),
            outputs = mapOf()
        )
    }

    fun JsonCompareTask(document1: String, document2: String, comparingPaths: Map<String, String> = mapOf()) {
        implementation = ChutneyStepImpl(
            type = "json-compare",
            target = null,
            inputs = mapOf("document1" to document1, "document2" to document2, "comparingPaths" to comparingPaths),
            outputs = mapOf()
        )
    }

    fun StringAssertTask(document: String, expected: String) {
        implementation = ChutneyStepImpl(
            type = "string-assert",
            target = null,
            inputs = mapOf("document" to document, "expected" to expected),
            outputs = mapOf()
        )
    }

    fun AssertTrueTask(asserts: List<String>) {
        implementation = ChutneyStepImpl(
            type = "assert",
            target = null,
            inputs = mapOf("asserts" to asserts.map { mapOf("assert-true" to it) }.toList()),
            outputs = mapOf()
        )
    }

    fun XmlAssertTask(document: String, expected: Map<String, Any> = mapOf()) {
        implementation = ChutneyStepImpl(
            type = "xml-assert",
            target = null,
            inputs = mapOf("document" to document, "expected" to expected),
            outputs = mapOf()
        )
    }

    fun JsonValidationTask(schema: String, json: String) {
        implementation = ChutneyStepImpl(
            type = "json-validation",
            target = null,
            inputs = mapOf("schema" to schema, "json" to json),
            outputs = mapOf()
        )
    }

    fun SqlTask(target: String, statements: List<String>, outputs: Map<String, Any> = mapOf()) {
        implementation = ChutneyStepImpl(
            type = "sql",
            target = target,
            inputs = mapOf("statements" to statements),
            outputs = outputs
        )
    }

    fun SleepTask(duration: String) {
        implementation = ChutneyStepImpl(
            type = "sleep",
            target = null,
            inputs = mapOf("duration" to String),
            outputs = mapOf()
        )
    }

    fun build(): ChutneyStep = ChutneyStep(description, implementation, strategy, subSteps)

}

@ChutneyScenarioDsl
class ChutneyStepImplBuilder {

    var type: String = ""
    var target: String = ""
    var inputs: Map<String, Any> = mapOf()
    var outputs: Map<String, Any> = mapOf()

    fun build(): ChutneyStepImpl = ChutneyStepImpl(type, target, inputs, outputs)

}

object Mapper {

    val pp = object : DefaultPrettyPrinter() {
        init {
            val indenter: Indenter = DefaultIndenter()
            indentObjectsWith(indenter) // Indent JSON objects
            indentArraysWith(indenter) // Indent JSON arrays
        }

        override fun withSeparators(separators: Separators?): DefaultPrettyPrinter {
            _separators = separators
            _objectFieldValueSeparatorWithSpaces = "" + separators!!.objectFieldValueSeparator + " "
            return this
        }
    }

    val mapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        .setDefaultPrettyPrinter(pp)
        .enable(SerializationFeature.INDENT_OUTPUT)

}

class ChutneyStep(
    val description: String,
    val implementation: ChutneyStepImpl? = null,
    val strategy: Strategy? = null,
    val subSteps: List<ChutneyStep>? = null
)

class ChutneyStepImpl(
    val type: String,
    val target: String?,
    val inputs: Map<String, Any>,
    val outputs: Map<String, Any>?
)

class ChutneyScenario(
    @JsonIgnore val id: Int?,
    val title: String = "",
    val description: String = "",
    val givens: List<ChutneyStep> = mutableListOf<ChutneyStep>(),
    val `when`: ChutneyStep? = null,
    val thens: List<ChutneyStep> = mutableListOf<ChutneyStep>()
) {

    override fun toString(): String {
        return Mapper.mapper.writeValueAsString(this) + System.getProperty("line.separator")
    }
}
