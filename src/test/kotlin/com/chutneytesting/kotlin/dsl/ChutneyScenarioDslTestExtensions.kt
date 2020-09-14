package com.chutneytesting.kotlin.dsl

fun String.asResource(): String = ChutneyScenarioDslTest::class.java.getResource(this).readText()
