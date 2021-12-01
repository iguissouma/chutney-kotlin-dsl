package com.chutneytesting.kotlin.dsl

// Based on chutney.tasks file

fun ChutneyStepBuilder.SuccessTask() {
    Implementation {
        type = "success"
    }
}

fun ChutneyStepBuilder.FailTask() {
    Implementation {
        type = "fail"
    }
}

fun ChutneyStepBuilder.DebugTask(filters: List<String> = listOf()) {
    Implementation {
        type = "debug"
        inputs = listOf(
            "filters" to filters
        ).notEmptyToMap()
    }
}

fun ChutneyStepBuilder.SleepTask(duration: String) {
    Implementation {
        type = "sleep"
        inputs = mapOf("duration" to duration)
    }
}

fun ChutneyStepBuilder.ContextPutTask(entries: Map<String, Any>, outs: Map<String, Any> = mapOf()) {
    Implementation {
        type = "context-put"
        inputs = mapOf("entries" to entries)
        outputs = outs
    }
}

fun ChutneyStepBuilder.FinalTask(
    name: String,
    type: String,
    target: String? = null,
    inputs: Map<String, Any> = emptyMap(),
    strategyType: String? = null,
    strategyProperties: Map<String, Any> = emptyMap(),
    validations: Map<String, Any> = emptyMap(),
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "final",
        inputs = listOf(
            "name" to name,
            "type" to type,
            "target" to target,
            "inputs" to inputs,
            "strategy-type" to strategyType,
            "strategy-properties" to strategyProperties,
            "validations" to validations
        ).notEmptyToMap(),
        outputs = outputs
    )
}


fun ChutneyStepBuilder.GroovyTask(
    script: String,
    parameters: Map<String, Any> = mapOf(),
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "groovy",
        inputs = listOf(
            "script" to script,
            "parameters" to parameters
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}


fun ChutneyStepBuilder.AmqpCreateBoundTemporaryQueueTask(
    target: String,
    exchangeName: String,
    routingKey: String,
    queueName: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "amqp-create-bound-temporary-queue",
        target = target,
        inputs = listOf(
            "exchange-name" to exchangeName,
            "routing-key" to routingKey,
            "queue-name" to queueName
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.AmqpDeleteQueueTask(
    target: String,
    queueName: String,
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "amqp-delete-queue",
        target = target,
        inputs = listOf(
            "queue-name" to queueName
        ).notEmptyToMap()
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.AmqpUnbindQueueTask(
    target: String,
    exchangeName: String,
    routingKey: String,
    queueName: String,
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "amqp-unbind-queue",
        target = target,
        inputs = listOf(
            "exchange-name" to exchangeName,
            "routing-key" to routingKey,
            "queue-name" to queueName
        ).notEmptyToMap()
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.AmqpBasicPublishTask(
    target: String,
    exchangeName: String,
    routingKey: String,
    headers: Map<String, Any>,
    properties: Map<String, String>,
    payload: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "amqp-basic-publish",
        target = target,
        inputs = listOf(
            "exchange-name" to exchangeName,
            "routing-key" to routingKey,
            "headers" to headers,
            "properties" to properties,
            "payload" to payload
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.AmqpBasicConsumeTask(
    target: String,
    queueName: String,
    nbMessages: Int = 1,
    timeout: String = "60 sec",
    selector: String = "",
    ack: Boolean? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "amqp-basic-consume",
        target = target,
        inputs = listOf(
            "queue-name" to queueName,
            "nb-messages" to nbMessages,
            "timeout" to timeout,
            "selector" to selector,
            "ack" to ack
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.AmqpBasicGetTask(
    target: String,
    queueName: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "amqp-basic-consume",
        target = target,
        inputs = listOf(
            "queue-name" to queueName
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.AmqpCleanQueuesTask(
    target: String,
    queueNames: List<String>,
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "amqp-clean-queues",
        target = target,
        inputs = listOf("queue-names" to queueNames).notEmptyToMap()
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.QpidServerStartTask(
    initConfig: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "qpid-server-start",
        inputs = listOf("init-config" to initConfig).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
}
// fun ChutneyStepBuilder.QpidServerStopTask auto registered by start task


fun ChutneyStepBuilder.MongoCountTask(
    target: String,
    collection: String,
    query: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "mongo-count",
        target = target,
        inputs = listOf(
            "collection" to collection,
            "query" to query
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.MongoDeleteTask(
    target: String,
    collection: String,
    query: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "mongo-delete",
        target = target,
        inputs = listOf(
            "collection" to collection,
            "query" to query
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.MongoFindTask(
    target: String,
    collection: String,
    query: String,
    limit: Int = 0,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "mongo-find",
        target = target,
        inputs = listOf(
            "collection" to collection,
            "query" to query,
            ("limit" to limit).takeIfPositive()
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.MongoInsertTask(
    target: String,
    collection: String,
    document: String,
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "mongo-insert",
        target = target,
        inputs = listOf(
            "collection" to collection,
            "document" to document
        ).notEmptyToMap()
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.MongoUpdateTask(
    target: String,
    collection: String,
    filter: String,
    update: String,
    arraysFilter: List<String> = listOf(),
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "mongo-update",
        target = target,
        inputs = listOf(
            "collection" to collection,
            "filter" to filter,
            "update" to update,
            "arraysFilter" to arraysFilter
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.MongoListTask(
    target: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "mongo-list",
        target = target,
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}


fun ChutneyStepBuilder.HttpGetTask(
    target: String,
    uri: String,
    headers: Map<String, Any> = mapOf(),
    timeout: String = "2 sec",
    outputs: Map<String, Any> = mapOf(),
    strategy: Strategy? = null,
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "http-get",
        target = target,
        inputs = listOf(
            "uri" to uri,
            "headers" to headers,
            "timeout" to timeout
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.HttpPostTask(
    target: String,
    uri: String,
    headers: Map<String, Any> = mapOf(),
    body: Any?,
    timeout: String = "2 sec",
    outputs: Map<String, Any> = mapOf(),
    strategy: Strategy? = null,
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "http-post",
        target = target,
        inputs = listOf(
            "uri" to uri,
            "headers" to headers,
            ("body" to body).takeIf {
                when (body) {
                    is String? -> body.isNullOrBlank().not()
                    is Map<*, *>? -> body.isNullOrEmpty().not()
                    else -> false
                }
            },
            "timeout" to timeout
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.HttpPutTask(
    target: String,
    uri: String,
    headers: Map<String, Any> = mapOf(),
    body: Map<String, Any> = mapOf(),
    timeout: String = "2 sec",
    outputs: Map<String, Any> = mapOf(),
    strategy: Strategy? = null,
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "http-put",
        target = target,
        inputs = listOf(
            "uri" to uri,
            "headers" to headers,
            "body" to body,
            "timeout" to timeout
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.HttpDeleteTask(
    target: String,
    uri: String,
    headers: Map<String, Any> = mapOf(),
    timeout: String = "2 sec",
    outputs: Map<String, Any> = mapOf(),
    strategy: Strategy? = null,
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "http-delete",
        target = target,
        inputs = listOf(
            "uri" to uri,
            "headers" to headers,
            "timeout" to timeout
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.HttpSoapTask(
    target: String,
    uri: String,
    body: String,
    headers: Map<String, Any> = mapOf(),
    timeout: String = "",
    username: String,
    password: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "http-soap",
        target = target,
        inputs = listOf(
            "uri" to uri,
            "headers" to headers,
            "body" to body,
            "username" to username,
            "password" to password,
            "timeout" to timeout
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.HttpPatchTask(
    target: String,
    uri: String,
    headers: Map<String, Any> = mapOf(),
    body: Any?,
    timeout: String = "2 sec",
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "http-post",
        target = target,
        inputs = listOf(
            "uri" to uri,
            "headers" to headers,
            ("body" to body).takeIf {
                when (body) {
                    is String? -> body.isNullOrBlank().not()
                    is Map<*, *>? -> body.isNullOrEmpty().not()
                    else -> false
                }
            },
            "timeout" to timeout
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.HttpsServerStartTask(
    port: String?,
    trustStorePath: String?,
    trustStorePassword: String?,
    keyStorePath: String?,
    keyStorePassword: String?,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "https-server-start",
        inputs = listOf(
            "port" to port,
            "truststore-path" to trustStorePath,
            "truststore-password" to trustStorePassword,
            "keystore-path" to keyStorePath,
            "keystore-password" to keyStorePassword
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
}

fun ChutneyStepBuilder.HttpsListenerTask(
    httpServerVarName: String = "httpsServer",
    uri: String,
    verb: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "https-listener",
        inputs = listOf(
            "https-server" to httpServerVarName.spEL(),
            "uri" to uri,
            "verb" to verb
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}
// fun ChutneyStepBuilder.HttpsServerStopTask auto registered by start task


enum class SSH_CLIENT_CHANNEL { COMMAND, SHELL }

fun ChutneyStepBuilder.SshClientTask(
    target: String,
    commands: List<Any>,
    channel: SSH_CLIENT_CHANNEL?,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "ssh-client",
        target = target,
        inputs = listOf(
            "commands" to commands,
            "channel" to channel?.name
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.SshServerStartTask(
    port: String = "",
    host: String = "",
    keyPair: String = "",
    usernames: List<String> = emptyList(),
    passwords: List<String> = emptyList(),
    authorizedKeys: String = "",
    stubs: List<String> = emptyList(),
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "ssh-server-start",
        inputs = listOf(
            "port" to port,
            "bind-address" to host,
            "private-key" to keyPair,
            "usernames" to usernames,
            "passwords" to passwords,
            "authorized-keys" to authorizedKeys,
            "responses" to stubs
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
}
// fun ChutneyStepBuilder.SshServerStopTask auto registered by start task

@Deprecated("Bad naming", ReplaceWith("JmsCleanQueueTask(target, queueName)"), DeprecationLevel.WARNING)
fun ChutneyStepBuilder.JmsCleanQueuesTask(
    target: String,
    queueName: String
) {
    JmsCleanQueueTask(target, queueName)
}

fun ChutneyStepBuilder.JmsCleanQueueTask(
    target: String,
    destination: String,
    selector: String = "",
    bodySelector: String = "",
    browserMaxDepth: Int = 0,
    timeOut: String = "",
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "jms-clean-queue",
        target = target,
        inputs = listOf(
            "destination" to destination,
            "selector" to selector,
            "bodySelector" to bodySelector,
            "browserMaxDepth" to browserMaxDepth,
            "timeOut" to timeOut
        ).notEmptyToMap()
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.JmsListenerTask(
    target: String,
    destination: String,
    selector: String = "",
    bodySelector: String = "",
    browserMaxDepth: Int = 0,
    timeOut: String = "",
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "jms-listener",
        target = target,
        inputs = listOf(
            "destination" to destination,
            "selector" to selector,
            "bodySelector" to bodySelector,
            ("browserMaxDepth" to browserMaxDepth).takeIfPositive(),
            "timeOut" to timeOut
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.JmsSenderTask(
    target: String,
    queueName: String,
    headers: Map<String, Any> = mapOf(),
    payload: String,
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "jms-sender",
        target = target,
        inputs = listOf(
            "destination" to queueName,
            "body" to payload,
            "headers" to headers
        ).notEmptyToMap()
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.JmsBrokerStartTask(
    configUri: String = "",
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "jms-broker-start",
        inputs = listOf(
            "config-uri" to configUri,
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
}
// fun ChutneyStepBuilder.JmsBrokerStopTask auto registered by start task


fun ChutneyStepBuilder.SqlTask(
    target: String,
    statements: List<String>,
    outputs: Map<String, Any> = mapOf(),
    nbLoggedRow: Int = 0,
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "sql",
        target = target,
        inputs = listOf(
            "statements" to statements,
            ("nbLoggedRow" to nbLoggedRow).takeIfPositive()
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}


fun ChutneyStepBuilder.SeleniumDriverInitTask(
    browser: String = "",
    driverPath: String,
    browserPath: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-driver-init",
        inputs = listOf(
            "browser" to browser,
            "driverPath" to driverPath,
            "browserPath" to browserPath
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

val defaultWebDriverSpel = "webDriver".spEL()

enum class SELENIUM_BY {
    id, NAME, className, cssSelector, xpath, zk;

    companion object {
        fun name(v: SELENIUM_BY): String {
            if (v == NAME) return "name"
            return v.name
        }
    }
}

fun ChutneyStepBuilder.SeleniumClickTask(
    webDriver: String = defaultWebDriverSpel,
    selector: String,
    by: SELENIUM_BY,
    wait: Int = 0,
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-click",
        inputs = listOf(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            ("wait" to wait).takeIfPositive()
        ).notEmptyToMap()
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.SeleniumCloseTask(
    webDriver: String = defaultWebDriverSpel,
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-close",
        inputs = mapOf(
            "web-driver" to webDriver
        )
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.SeleniumGetTask(
    webDriver: String = defaultWebDriverSpel,
    newTab: Boolean = false,
    url: String,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-get",
        inputs = listOf(
            "web-driver" to webDriver,
            ("selector" to "tab").takeIf { newTab },
            "value" to url
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.SeleniumGetAttributeTask(
    webDriver: String = defaultWebDriverSpel,
    selector: String,
    by: SELENIUM_BY,
    wait: Int = 0,
    attribute: String = "",
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-get-attribute",
        inputs = listOf(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            ("wait" to wait).takeIfPositive(),
            "attribute" to attribute
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.SeleniumGetTextTask(
    webDriver: String = defaultWebDriverSpel,
    selector: String,
    by: SELENIUM_BY,
    wait: Int = 0,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-get-text",
        inputs = listOf(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            ("wait" to wait).takeIfPositive(),
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

// fun ChutneyStepBuilder.SeleniumQuitTask auto registered by init taskS

fun ChutneyStepBuilder.SeleniumScreenShotTask(
    webDriver: String = defaultWebDriverSpel,
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-screenshot",
        inputs = mapOf(
            "web-driver" to webDriver
        )
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.SeleniumSendKeysTask(
    webDriver: String = defaultWebDriverSpel,
    selector: String,
    by: SELENIUM_BY,
    wait: Int = 0,
    value: String = "",
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-send-keys",
        inputs = listOf(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            ("wait" to wait).takeIfPositive(),
            "value" to value
        ).notEmptyToMap()
    )
    if (strategy != null) this.strategy = strategy
}

enum class SELENIUM_SWITCH { Frame, Window, Popup, AlertOk, AlertCancel }

fun ChutneyStepBuilder.SeleniumSwitchToTask(
    webDriver: String = defaultWebDriverSpel,
    selector: String,
    by: SELENIUM_BY,
    wait: Int = 0,
    switchType: SELENIUM_SWITCH? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-switch-to",
        inputs = listOf(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            ("wait" to wait).takeIfPositive(),
            "switchType" to switchType?.name
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.SeleniumWaitTask(
    webDriver: String = defaultWebDriverSpel,
    selector: String,
    by: SELENIUM_BY,
    wait: Int = 0,
    value: String = "",
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-wait",
        inputs = listOf(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            ("wait" to wait).takeIfPositive(),
            "value" to value
        ).notEmptyToMap()
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.SeleniumHoverThenClickTask(
    webDriver: String = defaultWebDriverSpel,
    selector: String,
    by: SELENIUM_BY,
    wait: Int = 0,
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-hover-then-click",
        inputs = listOf(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            ("wait" to wait).takeIfPositive()
        ).notEmptyToMap()
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.SeleniumScrollToTask(
    webDriver: String = defaultWebDriverSpel,
    selector: String,
    by: SELENIUM_BY,
    wait: Int = 0,
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-scroll-to",
        inputs = listOf(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            ("wait" to wait).takeIfPositive()
        ).notEmptyToMap()
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.SeleniumRemoteDriverInitTask(
    hub: String,
    browser: String = "",
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-remote-driver-init",
        inputs = listOf(
            "hub" to hub,
            "browser" to browser
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.SeleniumSetBrowserSizeTask(
    webDriver: String = defaultWebDriverSpel,
    width: Int,
    height: Int,
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "selenium-set-browser-size",
        inputs = listOf(
            "web-driver" to webDriver,
            "width" to width,
            "height" to height
        ).notEmptyToMap()
    )
    if (strategy != null) this.strategy = strategy
}


fun ChutneyStepBuilder.JsonAssertTask(
    document: String,
    expected: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "json-assert",
        inputs = listOf(
            "document" to document,
            "expected" to expected
        ).notEmptyToMap()
    )
}

fun ChutneyStepBuilder.JsonAssertTask(
    documentVariable: String,
    expectationsVariable: String
) {
    implementation = ChutneyStepImpl(
        type = "json-assert",
        inputs = listOf(
            "document" to documentVariable.spEL,
            "expected" to expectationsVariable.spEL
        ).notEmptyToMap()
    )
}

fun ChutneyStepBuilder.JsonCompareTask(
    document1: String,
    document2: String,
    comparingPaths: Map<String, String> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "json-compare",
        inputs = listOf(
            "document1" to document1,
            "document2" to document2,
            "comparingPaths" to comparingPaths
        ).notEmptyToMap()
    )
}

fun ChutneyStepBuilder.JsonValidationTask(
    schema: String,
    json: String
) {
    implementation = ChutneyStepImpl(
        type = "json-validation",
        inputs = listOf(
            "schema" to schema,
            "json" to json
        ).notEmptyToMap()
    )
}

fun ChutneyStepBuilder.XmlAssertTask(
    document: String,
    expected: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "xml-assert",
        inputs = listOf(
            "document" to document,
            "expected" to expected
        ).notEmptyToMap()
    )
}

fun ChutneyStepBuilder.StringAssertTask(
    document: String,
    expected: String
) {
    implementation = ChutneyStepImpl(
        type = "string-assert",
        inputs = listOf(
            "document" to document,
            "expected" to expected
        ).notEmptyToMap()
    )
}

@Deprecated("Bad naming", ReplaceWith("AssertTask(List<String>)"), DeprecationLevel.WARNING)
fun ChutneyStepBuilder.AssertTrueTask(asserts: List<Map<String, Any>>) {
    implementation = ChutneyStepImpl(
        type = "assert",
        inputs = listOf("asserts" to asserts).notEmptyToMap()
    )
}

fun ChutneyStepBuilder.AssertTask(
    asserts: List<String>
) {
    implementation = ChutneyStepImpl(
        type = "assert",
        inputs = listOf(
            "asserts" to asserts.map { s -> mapOf("assert-true" to s) }
        ).notEmptyToMap()
    )
}

fun ChutneyStepBuilder.XsdValidationTask(
    xml: String,
    xsdPath: String
) {
    implementation = ChutneyStepImpl(
        type = "xsd-validation",
        inputs = listOf(
            "xml" to xml,
            "xsd" to xsdPath
        ).notEmptyToMap()
    )
}

fun ChutneyStepBuilder.CompareTask(
    mode: String,
    actual: String,
    expected: String
) {
    implementation = ChutneyStepImpl(
        type = "compare",
        inputs = listOf(
            "mode" to mode,
            "actual" to actual,
            "expected" to expected
        ).notEmptyToMap()
    )
}


fun ChutneyStepBuilder.KafkaBasicPublishTask(
    target: String,
    topic: String,
    headers: Map<String, Any> = mapOf(),
    payload: Any,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "kafka-basic-publish",
        target = target,
        inputs = listOf(
            "topic" to topic,
            "headers" to headers,
            "payload" to payload
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.KafkaBasicConsumeTask(
    target: String,
    topic: String,
    group: String,
    properties: Map<String, String> = mapOf("auto.offset.reset" to "earliest"),
    timeout: String = "60 sec",
    selector: String = "",
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "kafka-basic-consume",
        target = target,
        inputs = listOf(
            "topic" to topic,
            "group" to group,
            "timeout" to timeout,
            "selector" to selector,
            "properties" to properties
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.KafkaBrokerStartTask(
    port: String? = null,
    topics: List<String>? = null,
    properties: Map<String, Any>? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "kafka-broker-start-consume",
        inputs = listOf(
            "port" to port,
            "topics" to topics,
            "properties" to properties
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
}
// fun ChutneyStepBuilder.KafkaBrokerStopTask auto registered by start task


fun ChutneyStepBuilder.MicrometerCounterTask(
    name: String,
    description: String? = null,
    unit: String? = null,
    tags: List<String> = emptyList(),
    counter: String? = null,
    increment: String? = null,
    registry: String? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "micrometer-counter",
        inputs = listOf(
            "name" to name,
            "description" to description,
            "unit" to unit,
            "tags" to tags,
            "counter" to counter,
            "increment" to increment,
            "registry" to registry
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.MicrometerGaugeTask(
    name: String,
    description: String? = null,
    unit: String? = null,
    strongReference: Boolean = false,
    tags: List<String> = emptyList(),
    gaugeObject: Any? = null, //Number or Object or List or Map
    gaugeFunction: String? = null,
    registry: String? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "micrometer-gauge",
        inputs = listOf(
            "name" to name,
            "description" to description,
            "unit" to unit,
            "strongReference" to strongReference,
            "tags" to tags,
            "gaugeObject" to gaugeObject,
            "gaugeFunction" to gaugeFunction,
            "registry" to registry
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.MicrometerTimerTask(
    name: String,
    description: String? = null,
    tags: List<String> = emptyList(),
    bufferLength: String? = null,
    expiry: String? = null,
    maxValue: String? = null,
    minValue: String? = null,
    percentilePrecision: String? = null,
    publishPercentilesHistogram: Boolean = false,
    percentiles: String? = null,
    sla: String? = null,
    timer: String? = null,
    registry: String? = null,
    timeunit: String? = null,
    record: String? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "micrometer-timer",
        inputs = listOf(
            "name" to name,
            "description" to description,
            "tags" to tags,
            "bufferLength" to bufferLength,
            "expiry" to expiry,
            "maxValue" to maxValue,
            "minValue" to minValue,
            "percentilePrecision" to percentilePrecision,
            "publishPercentilesHistogram" to publishPercentilesHistogram,
            "percentiles" to percentiles,
            "sla" to sla,
            "timer" to timer,
            "registry" to registry,
            "timeunit" to timeunit,
            "record" to record
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.MicrometerTimerStartTask(
    registry: String? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "micrometer-timer-start",
        inputs = listOf(
            "registry" to registry,
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.MicrometerTimerStopTask(
    registry: String? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "micrometer-timer-stop",
        inputs = listOf(
            "registry" to registry,
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.MicrometerSummaryTask(
    name: String,
    description: String? = null,
    unit: String? = null,
    tags: List<String> = emptyList(),
    bufferLength: String? = null,
    expiry: String? = null,
    maxValue: String? = null,
    minValue: String? = null,
    percentilePrecision: String? = null,
    publishPercentilesHistogram: Boolean = false,
    percentiles: String? = null,
    scale: String? = null,
    sla: String? = null,
    distributionSummary: String? = null,
    registry: String? = null,
    record: String? = null,
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "micrometer-summary",
        inputs = listOf(
            "name" to name,
            "description" to description,
            "unit" to unit,
            "tags" to tags,
            "bufferLength" to bufferLength,
            "expiry" to expiry,
            "maxValue" to maxValue,
            "minValue" to minValue,
            "percentilePrecision" to percentilePrecision,
            "publishPercentilesHistogram" to publishPercentilesHistogram,
            "percentiles" to percentiles,
            "scale" to scale,
            "sla" to sla,
            "distributionSummary" to distributionSummary,
            "registry" to registry,
            "record" to record
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.RadiusAuthenticateTask(
    target: String,
    userName: String,
    userPassword: String,
    protocol: String? = "chap",
    attributes: Map<String, String>? = mapOf(),
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "radius-authenticate",
        target = target,
        inputs = listOf(
            "userName" to userName,
            "userPassword" to userPassword,
            "protocol" to protocol,
            "attributes" to attributes
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

fun ChutneyStepBuilder.RadiusAccountingTask(
    target: String,
    userName: String,
    accountingType: Int,
    attributes: Map<String, String>? = mapOf(),
    outputs: Map<String, Any> = mapOf(),
    validations: Map<String, Any> = mapOf(),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "radius-accounting",
        target = target,
        inputs = listOf(
            "userName" to userName,
            "accountingType" to accountingType,
            "attributes" to attributes
        ).notEmptyToMap(),
        outputs = outputs,
        validations = validations
    )
    if (strategy != null) this.strategy = strategy
}

// Helpers
private fun <T> List<Pair<String, T?>?>.notEmptyToMap(): Map<String, T> {
    return (this
        .filterNotNull()
        .filter { it.second != null }
        .filter {
            when (it.second) {
                is Collection<*> -> (it.second as Collection<*>).isNotEmpty()
                is Map<*, *> -> (it.second as Map<*, *>).isNotEmpty()
                else -> true
            }
        } as List<Pair<String, T>>).toMap()
}

private fun Pair<String, Int>.takeIfPositive(): Pair<String, Int>? {
    return this.takeIf { this.second > 0 }
}
