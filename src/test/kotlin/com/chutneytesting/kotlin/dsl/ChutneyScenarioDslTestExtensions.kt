package com.chutneytesting.kotlin.dsl

fun String.asResource(): String = this.javaClass::class.java.getResource(this).readText()
