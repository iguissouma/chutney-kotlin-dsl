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
        inputs = listOfNotNull(
            ("filters" to filters).takeIf { filters.isNotEmpty() }
        ).toMap()
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


fun ChutneyStepBuilder.GroovyTask(script: String, parameters: Map<String, Any> = mapOf(), outputs: Map<String, Any> = mapOf()) {
    implementation = ChutneyStepImpl(
        type = "context-put",
        target = null,
        inputs = mapOf("script" to script, "parameters" to parameters),
        outputs = outputs
    )
}


fun ChutneyStepBuilder.AmqpCreateBoundTemporaryQueueTask(
    target: String,
    exchangeName: String,
    routingKey: String,
    queueName: String,
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "amqp-create-bound-temporary-queue",
        target = target,
        inputs = mapOf(
            "exchange-name" to exchangeName,
            "routing-key" to routingKey,
            "queue-name" to queueName
        ),
        outputs = outputs
    )
}

fun ChutneyStepBuilder.AmqpDeleteQueueTask(
    target: String,
    queueName: String
) {
    implementation = ChutneyStepImpl(
        type = "amqp-delete-queue",
        target = target,
        inputs = mapOf(
            "queue-name" to queueName
        ),
        outputs = mapOf()
    )
}

fun ChutneyStepBuilder.AmqpUnbindQueueTask(
    target: String,
    exchangeName: String,
    routingKey: String,
    queueName: String
) {
    implementation = ChutneyStepImpl(
        type = "amqp-unbind-queue",
        target = target,
        inputs = mapOf(
            "exchange-name" to exchangeName,
            "routing-key" to routingKey,
            "queue-name" to queueName
        ),
        outputs = mapOf()
    )
}

fun ChutneyStepBuilder.AmqpBasicPublishTask(
    target: String,
    exchangeName: String,
    routingKey: String,
    headers: Map<String, Any>,
    properties: Map<String, String>,
    payload: String,
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "amqp-basic-publish",
        target = target,
        inputs = listOfNotNull(
            "exchange-name" to exchangeName,
            "routing-key" to routingKey,
            ("headers" to headers).takeIf { headers.isNotEmpty() },
            ("properties" to properties).takeIf { properties.isNotEmpty() },
            "payload" to payload
        ).toMap(),
        outputs = outputs
    )
}

fun ChutneyStepBuilder.AmqpBasicConsumeTask(
    target: String,
    queueName: String,
    nbMessages: Int = 1,
    timeout: String = "60 sec",
    selector: String = "",
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

fun ChutneyStepBuilder.AmqpBasicGetTask(
    target: String,
    queueName: String,
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "amqp-basic-consume",
        target = target,
        inputs = mapOf(
            "queue-name" to queueName
        ),
        outputs = outputs
    )
}

fun ChutneyStepBuilder.AmqpCleanQueuesTask(
    target: String,
    queueNames: List<String>
) {
    implementation = ChutneyStepImpl(
        type = "amqp-clean-queues",
        target = target,
        inputs = mapOf("queue-names" to queueNames),
        outputs = mapOf()
    )
}

fun ChutneyStepBuilder.QpidServerStartTask(
    initConfig: String
) {
    implementation = ChutneyStepImpl(
        type = "qpid-server-start",
        target = null,
        inputs = mapOf("init-config" to initConfig),
        outputs = mapOf()
    )
}


fun ChutneyStepBuilder.MongoCountTask(
    target: String,
    collection: String,
    query: String,
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "mongo-count",
        target = target,
        inputs = mapOf(
            "collection" to collection,
            "query" to query
        ),
        outputs = outputs
    )
}

fun ChutneyStepBuilder.MongoDeleteTask(
    target: String,
    collection: String,
    query: String
) {
    implementation = ChutneyStepImpl(
        type = "mongo-delete",
        target = target,
        inputs = mapOf(
            "collection" to collection,
            "query" to query
        ),
        outputs = mapOf()
    )
}

fun ChutneyStepBuilder.MongoFindTask(
    target: String,
    collection: String,
    query: String,
    limit: Int?,
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "mongo-find",
        target = target,
        inputs = listOfNotNull(
            "collection" to collection,
            "query" to query,
            ("limit" to limit).takeIf { limit != null && limit <= 0 }
        ).toMap(),
        outputs = outputs
    )
}

fun ChutneyStepBuilder.MongoInsertTask(
    target: String,
    collection: String,
    document: String,
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "mongo-insert",
        target = target,
        inputs = mapOf(
            "collection" to collection,
            "document" to document
        ),
        outputs = outputs
    )
}

fun ChutneyStepBuilder.MongoUpdateTask(
    target: String,
    collection: String,
    filter: String,
    update: String,
    arraysFilter: List<String> = listOf(),
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "mongo-update",
        target = target,
        inputs = listOfNotNull(
            "collection" to collection,
            "filter" to filter,
            "update" to update,
            ("arraysFilter" to arraysFilter).takeIf { arraysFilter.isNotEmpty() }
        ).toMap(),
        outputs = outputs
    )
}

fun ChutneyStepBuilder.MongoListTask(
    target: String,
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "mongo-list",
        target = target,
        inputs = mapOf(),
        outputs = outputs
    )
}


fun ChutneyStepBuilder.HttpGetTask(
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
        inputs = listOfNotNull(
            "uri" to uri,
            ("headers" to headers).takeIf { headers.isNotEmpty() },
            "timeout" to timeout
        ).toMap(),
        outputs = outputs
    )
    this.strategy = strategy
}

fun ChutneyStepBuilder.HttpPostTask(
    target: String,
    uri: String,
    headers: Map<String, Any> = mapOf(),
    body: Any?,
    timeout: String = "2 sec",
    outputs: Map<String, Any> = mapOf("body".toSpelPair()),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "http-post",
        target = target,
        inputs = listOfNotNull(
            "uri" to uri,
            ("headers" to headers).takeIf { headers.isNotEmpty() },
            ("body" to body).takeIf {
                when (body) {
                    is String? -> body.isNullOrBlank().not()
                    is Map<*, *>? -> body.isNullOrEmpty().not()
                    else -> false
                }
            },
            "timeout" to timeout
        ).toMap(),
        outputs = outputs
    )
    this.strategy = strategy
}

fun ChutneyStepBuilder.HttpPutTask(
    target: String,
    uri: String,
    headers: Map<String, Any> = mapOf(),
    body: Map<String, Any> = mapOf(),
    timeout: String = "2 sec",
    outputs: Map<String, Any> = mapOf("body".toSpelPair()),
    strategy: Strategy? = null
) {
    implementation = ChutneyStepImpl(
        type = "http-put",
        target = target,
        inputs = listOfNotNull(
            "uri" to uri,
            ("headers" to headers).takeIf { headers.isNotEmpty() },
            "body" to body,
            "timeout" to timeout
        ).toMap(),
        outputs = outputs
    )
    this.strategy = strategy
}

fun ChutneyStepBuilder.HttpDeleteTask(
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
        inputs = listOfNotNull(
            "uri" to uri,
            ("headers" to headers).takeIf { headers.isNotEmpty() },
            "timeout" to timeout
        ).toMap(),
        outputs = outputs
    )
    this.strategy = strategy
}

fun ChutneyStepBuilder.HttpSoapTask(
    target: String,
    uri: String,
    body: String,
    headers: Map<String, Any> = mapOf(),
    timeout: String = "",
    username: String,
    password: String,
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "http-soap",
        target = target,
        inputs = listOfNotNull(
            "uri" to uri,
            ("headers" to headers).takeIf { headers.isNotEmpty() },
            "body" to body,
            "username" to username,
            "password" to password,
            ("timeout" to timeout).takeIf { timeout.isBlank().not() }
        ).toMap(),
        outputs = outputs
    )
}

fun ChutneyStepBuilder.HttpsServerStartTask(
    port: String?,
    trustStorePath: String?,
    trustStorePassword: String?,
    keyStorePath: String?,
    keyStorePassword: String?,
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "https-server-start",
        target = null,
        inputs = listOfNotNull(
            ("port" to port).takeIf { port.isNullOrBlank().not() },
            ("truststore-path" to trustStorePath).takeIf { trustStorePath.isNullOrBlank().not() },
            ("truststore-password" to trustStorePassword).takeIf { trustStorePassword.isNullOrBlank().not() },
            ("keystore-path" to keyStorePath).takeIf { keyStorePath.isNullOrBlank().not() },
            ("keystore-password" to keyStorePassword).takeIf { keyStorePassword.isNullOrBlank().not() }
        ).toMap(),
        outputs = outputs
    )
}

fun ChutneyStepBuilder.HttpsListenerTask(
    httpServerVarName: String = "httpsServer",
    uri: String,
    verb: String,
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "https-listener",
        target = null,
        inputs = mapOf(
            "https-server" to httpServerVarName.spEL(),
            "uri" to uri,
            "verb" to verb
        ),
        outputs = outputs
    )
}


enum class SSH_CLIENT_CHANNEL { COMMAND, SHELL }

fun ChutneyStepBuilder.SshClientTask(
    target: String,
    commands: List<Any>,
    channel: SSH_CLIENT_CHANNEL?,
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "ssh-client",
        target = target,
        inputs = listOfNotNull(
            "commands" to commands,
            "channel" to channel?.name
        ).toMap(),
        outputs = outputs
    )
}

fun ChutneyStepBuilder.SshServerStartTask(
    port: String = "",
    host: String = "",
    keyPair: String = "",
    usernames: List<String> = emptyList(),
    passwords: List<String> = emptyList(),
    authorizedKeys: String = "",
    stubs: List<String> = emptyList(),
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "ssh-server-start",
        target = null,
        inputs = listOfNotNull(
            ("port" to port).takeIf { port.isNotBlank() },
            ("bind-address" to host).takeIf { host.isNotBlank() },
            ("private-key" to keyPair).takeIf { keyPair.isNotBlank() },
            ("usernames" to usernames).takeIf { usernames.isNotEmpty() },
            ("passwords" to passwords).takeIf { passwords.isNotEmpty() },
            ("authorized-keys" to authorizedKeys).takeIf { authorizedKeys.isNotBlank() },
            ("responses" to stubs).takeIf { stubs.isNotEmpty() }
        ).toMap(),
        outputs = outputs
    )
}


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
    timeOut: String = ""
) {
    implementation = ChutneyStepImpl(
        type = "jms-clean-queue",
        target = target,
        inputs = listOfNotNull(
            "destination" to destination,
            ("selector" to selector).takeIf { selector.isNotBlank() },
            ("bodySelector" to selector).takeIf { bodySelector.isNotBlank() },
            ("browserMaxDepth" to selector).takeIf { browserMaxDepth > 0 },
            ("timeOut" to selector).takeIf { timeOut.isNotBlank() }
        ).toMap(),
        outputs = mapOf()
    )
}

fun ChutneyStepBuilder.JmsListenerTask(
    target: String,
    destination: String,
    selector: String = "",
    bodySelector: String = "",
    browserMaxDepth: Int = 0,
    timeOut: String = "",
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "jms-listener",
        target = target,
        inputs = listOfNotNull(
            "destination" to destination,
            ("selector" to selector).takeIf { selector.isNotBlank() },
            ("bodySelector" to selector).takeIf { bodySelector.isNotBlank() },
            ("browserMaxDepth" to selector).takeIf { browserMaxDepth > 0 },
            ("timeOut" to selector).takeIf { timeOut.isNotBlank() }
        ).toMap(),
        outputs = outputs
    )
}

fun ChutneyStepBuilder.JmsSenderTask(
    target: String,
    queueName: String,
    headers: Map<String, Any> = mapOf(),
    payload: String
) {
    implementation = ChutneyStepImpl(
        type = "jms-sender",
        target = target,
        inputs = listOfNotNull(
            "destination" to queueName,
            "body" to payload,
            ("headers" to headers).takeIf { headers.isNotEmpty() },
        ).toMap(),
        outputs = mapOf()
    )
}

fun ChutneyStepBuilder.JmsBrokerStartTask(
    configUri: String = "",
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "jms-broker-start",
        target = null,
        inputs = listOfNotNull(
            ("config-uri" to configUri).takeIf { configUri.isNotEmpty() },
        ).toMap(),
        outputs = outputs
    )
}


fun ChutneyStepBuilder.SqlTask(
    target: String,
    statements: List<String>,
    outputs: Map<String, Any> = mapOf(),
    nbLoggedRow: Int = 0
) {
    implementation = ChutneyStepImpl(
        type = "sql",
        target = target,
        inputs = listOfNotNull(
            "statements" to statements,
            ("nbLoggedRow" to nbLoggedRow).takeIf { nbLoggedRow != 0 }
        ).toMap(),
        outputs = outputs
    )
}


fun ChutneyStepBuilder.SeleniumDriverInitTask(
    browser: String = "",
    driverPath: String,
    browserPath: String,
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "selenium-driver-init",
        target = null,
        inputs = listOfNotNull(
            ("browser" to browser).takeIf { browser.isNotBlank() },
            "driverPath" to driverPath,
            "browserPath" to browserPath
        ).toMap(),
        outputs = outputs
    )
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
    wait: Int = 0
) {
    implementation = ChutneyStepImpl(
        type = "selenium-click",
        target = null,
        inputs = listOfNotNull(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            ("wait" to wait).takeIf { wait > 0 }
        ).toMap(),
        outputs = mapOf()
    )
}

fun ChutneyStepBuilder.SeleniumCloseTask(
    webDriver: String = defaultWebDriverSpel
) {
    implementation = ChutneyStepImpl(
        type = "selenium-close",
        target = null,
        inputs = mapOf(
            "web-driver" to webDriver
        ),
        outputs = mapOf()
    )
}

fun ChutneyStepBuilder.SeleniumGetTask(
    webDriver: String = defaultWebDriverSpel,
    newTab: Boolean = false,
    url: String,
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "selenium-get",
        target = null,
        inputs = listOfNotNull(
            "web-driver" to webDriver,
            ("selector" to "tab").takeIf { newTab },
            "value" to url
        ).toMap(),
        outputs = outputs
    )
}

fun ChutneyStepBuilder.SeleniumGetAttributeTask(
    webDriver: String = defaultWebDriverSpel,
    selector: String,
    by: SELENIUM_BY,
    wait: Int = 0,
    attribute: String = "",
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "selenium-get-attribute",
        target = null,
        inputs = listOfNotNull(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            ("wait" to wait).takeIf { wait > 0 },
            ("attribute" to attribute).takeIf { attribute.isNotBlank() }
        ).toMap(),
        outputs = outputs
    )
}

fun ChutneyStepBuilder.SeleniumGetTextTask(
    webDriver: String = defaultWebDriverSpel,
    selector: String,
    by: SELENIUM_BY,
    wait: Int = 0,
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "selenium-get-text",
        target = null,
        inputs = listOfNotNull(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            ("wait" to wait).takeIf { wait > 0 }
        ).toMap(),
        outputs = outputs
    )
}

fun ChutneyStepBuilder.SeleniumQuitTask(
    webDriver: String = defaultWebDriverSpel
) {
    implementation = ChutneyStepImpl(
        type = "selenium-quit",
        target = null,
        inputs = mapOf(
            "web-driver" to webDriver
        ),
        outputs = mapOf()
    )
}

fun ChutneyStepBuilder.SeleniumScreenShotTask(
    webDriver: String = defaultWebDriverSpel
) {
    implementation = ChutneyStepImpl(
        type = "selenium-screenshot",
        target = null,
        inputs = mapOf(
            "web-driver" to webDriver
        ),
        outputs = mapOf()
    )
}

fun ChutneyStepBuilder.SeleniumSendKeysTask(
    webDriver: String = defaultWebDriverSpel,
    selector: String,
    by: SELENIUM_BY,
    wait: Int = 0,
    value: String = ""
) {
    implementation = ChutneyStepImpl(
        type = "selenium-send-keys",
        target = null,
        inputs = listOfNotNull(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            ("wait" to wait).takeIf { wait > 0 },
            "value" to value
        ).toMap(),
        outputs = mapOf()
    )
}

enum class SELENIUM_SWITCH { Frame, Window, Popup, AlertOk, AlertCancel }

fun ChutneyStepBuilder.SeleniumSwitchToTask(
    webDriver: String = defaultWebDriverSpel,
    selector: String,
    by: SELENIUM_BY,
    wait: Int = 0,
    switchType: SELENIUM_SWITCH? = null,
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "selenium-switch-to",
        target = null,
        inputs = listOfNotNull(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            ("wait" to wait).takeIf { wait > 0 },
            "switchType" to switchType?.name
        ).toMap(),
        outputs = outputs
    )
}

fun ChutneyStepBuilder.SeleniumWaitTask(
    webDriver: String = defaultWebDriverSpel,
    selector: String,
    by: SELENIUM_BY,
    wait: Int = 0,
    value: String = ""
) {
    implementation = ChutneyStepImpl(
        type = "selenium-wait",
        target = null,
        inputs = listOfNotNull(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            ("wait" to wait).takeIf { wait > 0 },
            "value" to value
        ).toMap(),
        outputs = mapOf()
    )
}

fun ChutneyStepBuilder.SeleniumHoverThenClickTask(
    webDriver: String = defaultWebDriverSpel,
    selector: String,
    by: SELENIUM_BY,
    wait: Int = 0
) {
    implementation = ChutneyStepImpl(
        type = "selenium-hover-then-click",
        target = null,
        inputs = listOfNotNull(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            ("wait" to wait).takeIf { wait > 0 }
        ).toMap(),
        outputs = mapOf()
    )
}

fun ChutneyStepBuilder.SeleniumScrollToTask(
    webDriver: String = defaultWebDriverSpel,
    selector: String,
    by: SELENIUM_BY,
    wait: Int = 0
) {
    implementation = ChutneyStepImpl(
        type = "selenium-scroll-to",
        target = null,
        inputs = listOfNotNull(
            "web-driver" to webDriver,
            "selector" to selector,
            "by" to SELENIUM_BY.name(by),
            ("wait" to wait).takeIf { wait > 0 }
        ).toMap(),
        outputs = mapOf()
    )
}

fun ChutneyStepBuilder.SeleniumRemoteDriverInitTask(
    hub: String,
    browser: String = "",
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "selenium-remote-driver-init",
        target = null,
        inputs = listOfNotNull(
            "hub" to hub,
            ("browser" to browser).takeIf { browser.isNotBlank() }
        ).toMap(),
        outputs = outputs
    )
}

fun ChutneyStepBuilder.SeleniumSetBrowserSizeTask(
    webDriver: String = defaultWebDriverSpel,
    width: Int,
    height: Int
) {
    implementation = ChutneyStepImpl(
        type = "selenium-set-browser-size",
        target = null,
        inputs = listOfNotNull(
            "web-driver" to webDriver,
            "width" to width,
            "height" to height
        ).toMap(),
        outputs = mapOf()
    )
}


fun ChutneyStepBuilder.JsonAssertTask(
    document: String,
    expected: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "json-assert",
        target = null,
        inputs = mapOf(
            "document" to document,
            "expected" to expected
        ),
        outputs = mapOf()
    )
}

fun ChutneyStepBuilder.JsonAssertTask(
    documentVariable: String,
    expectationsVariable: String
) {
    implementation = ChutneyStepImpl(
        type = "json-assert",
        target = null,
        inputs = mapOf(
            "document" to documentVariable.spEL,
            "expected" to expectationsVariable.spEL
        ),
        outputs = mapOf()
    )
}

fun ChutneyStepBuilder.JsonCompareTask(
    document1: String,
    document2: String,
    comparingPaths: Map<String, String> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "json-compare",
        target = null,
        inputs = mapOf(
            "document1" to document1,
            "document2" to document2,
            "comparingPaths" to comparingPaths
        ),
        outputs = mapOf()
    )
}

fun ChutneyStepBuilder.JsonValidationTask(
    schema: String,
    json: String
) {
    implementation = ChutneyStepImpl(
        type = "json-validation",
        target = null,
        inputs = mapOf(
            "schema" to schema,
            "json" to json
        ),
        outputs = mapOf()
    )
}

fun ChutneyStepBuilder.XmlAssertTask(
    document: String,
    expected: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "xml-assert",
        target = null,
        inputs = mapOf(
            "document" to document,
            "expected" to expected
        ),
        outputs = mapOf()
    )
}

fun ChutneyStepBuilder.StringAssertTask(
    document: String,
    expected: String
) {
    implementation = ChutneyStepImpl(
        type = "string-assert",
        target = null,
        inputs = mapOf(
            "document" to document,
            "expected" to expected
        ),
        outputs = mapOf()
    )
}

@Deprecated("Bad naming", ReplaceWith("AssertTask(List<String>)"), DeprecationLevel.WARNING)
fun ChutneyStepBuilder.AssertTrueTask(asserts: List<Map<String, Any>>) {
    implementation = ChutneyStepImpl(
        type = "assert",
        target = null,
        inputs = mapOf("asserts" to asserts),
        outputs = mapOf()
    )
}
fun ChutneyStepBuilder.AssertTask(
    asserts: List<String>
) {
    implementation = ChutneyStepImpl(
        type = "assert",
        target = null,
        inputs = mapOf(
            "asserts" to asserts.map { s -> mapOf("assert-true" to s) }
        ),
        outputs = mapOf()
    )
}

fun ChutneyStepBuilder.XsdValidationTask(
    xml: String,
    xsdPath: String
) {
    implementation = ChutneyStepImpl(
        type = "xsd-validation",
        target = null,
        inputs = mapOf(
            "xml" to xml,
            "xsd" to xsdPath
        ),
        outputs = mapOf()
    )
}

fun ChutneyStepBuilder.CompareTask(
    mode: String,
    actual: String,
    expected: String
) {
    implementation = ChutneyStepImpl(
        type = "compare",
        target = null,
        inputs = mapOf(
            "mode" to mode,
            "actual" to actual,
            "expected" to expected
        ),
        outputs = mapOf()
    )
}


fun ChutneyStepBuilder.KafkaBasicPublishTask(
    target: String,
    topic: String,
    headers: Map<String, Any> = mapOf(),
    payload: Any
) {
    implementation = ChutneyStepImpl(
        type = "kafka-basic-publish",
        target = target,
        inputs = listOfNotNull(
            "topic" to topic,
            ("headers" to headers).takeIf { headers.isNotEmpty() },
            "payload" to payload
        ).toMap(),
        outputs = mapOf()
    )
}

fun ChutneyStepBuilder.KafkaBasicConsumeTask(
    target: String,
    topic: String,
    group: String,
    properties: Map<String, String> = mapOf("auto.offset.reset" to "earliest"),
    timeout: String = "60 sec",
    selector: String = "",
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

fun ChutneyStepBuilder.KafkaBrokerStartTask(
    port: String? = null,
    topics: List<String>? = null,
    properties: Map<String, Any>? = null,
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "kafka-broker-start-consume",
        target = null,
        inputs = listOfNotNull(
            "port" to port,
            "topics" to topics,
            "properties" to properties
        ).toMap(),
        outputs = outputs
    )
}


fun ChutneyStepBuilder.MicrometerCounterTask(
    name: String,
    description: String? = null,
    unit: String? = null,
    tags: List<String> = emptyList(),
    counter: String? = null,
    increment: String? = null,
    registry: String? = null,
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "micrometer-counter",
        target = null,
        inputs = mapOf(
            "name" to name,
            "description" to description,
            "unit" to unit,
            "tags" to tags,
            "counter" to counter,
            "increment" to increment,
            "registry" to registry
        ),
        outputs = outputs
    )
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
    outputs: Map<String, Any> = mapOf()
) {
    val inputs = HashMap<String, Any?>()
    inputs.putAll(mapOf("name" to null))
    implementation = ChutneyStepImpl(
        type = "micrometer-gauge",
        target = null,
        inputs = mapOf(
            "name" to name,
            "description" to description,
            "unit" to unit,
            "strongReference" to strongReference,
            "tags" to tags,
            "gaugeObject" to gaugeObject,
            "gaugeFunction" to gaugeFunction,
            "registry" to registry
        ),
        outputs = outputs
    )
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
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "micrometer-timer",
        target = null,
        inputs = mapOf(
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
        ),
        outputs = outputs
    )
}

fun ChutneyStepBuilder.MicrometerTimerStartTask(
    registry: String? = null,
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "micrometer-timer-start",
        target = null,
        inputs = mapOf(
            "registry" to registry,
        ),
        outputs = outputs
    )
}

fun ChutneyStepBuilder.MicrometerTimerStopTask(
    registry: String? = null,
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "micrometer-timer-stop",
        target = null,
        inputs = mapOf(
            "registry" to registry,
        ),
        outputs = outputs
    )
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
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "micrometer-summary",
        target = null,
        inputs = mapOf(
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
        ),
        outputs = outputs
    )
}

fun ChutneyStepBuilder.FinalTask(
    name: String,
    type: String,
    inputs: Map<String, Any> = emptyMap(),
    strategyType: String? = null,
    strategyProperties: Map<String, Any> = emptyMap(),
    validations: Map<String, Any> = emptyMap(),
    outputs: Map<String, Any> = mapOf()
) {
    implementation = ChutneyStepImpl(
        type = "micrometer-summary",
        target = null,
        inputs = mapOf(
            "name" to name,
            "type" to type,
            "inputs" to inputs,
            "strategy-type" to strategyType,
            "strategy-properties" to strategyProperties,
            "validations" to validations
        ),
        outputs = outputs
    )
}

