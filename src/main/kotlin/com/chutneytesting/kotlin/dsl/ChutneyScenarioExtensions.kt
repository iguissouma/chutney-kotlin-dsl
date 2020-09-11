package com.chutneytesting.kotlin.dsl

const val JSON_PATH_ROOT = "\$"
val String.spELVar: String
    get() = "#$this"
val String.spEL: String
    get() = "\${#$this}"

fun String.spELString(): String = "\${#$this}"
fun String.spEL(): String = "\${#$this}"
fun String.spELVar(): String = "#$this"
fun Map<String, Any>.toEntries(): Map<String, Map<String, Any>> = mapOf("entries" to this)
fun String.toSpelPair(): Pair<String, String> = this to this.spEL
fun json(variable: String, path: String = JSON_PATH_ROOT): String = "json(${variable.spELVar}, '$path')".spEL()
fun retryTimeOutStrategy(timeout: String = "30 sec", retryDelay: String = "5 sec") =
    RetryTimeOutStrategy(timeout, retryDelay)
