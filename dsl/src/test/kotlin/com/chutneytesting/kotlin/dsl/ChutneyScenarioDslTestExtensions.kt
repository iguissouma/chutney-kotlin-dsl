package com.chutneytesting.kotlin.dsl

fun String.asResource(): String = ChutneyScenarioDslTest::class.java.getResource(this)?.readText()
    ?: throw RuntimeException("Resource not found [$this]")
