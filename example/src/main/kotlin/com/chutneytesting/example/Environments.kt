package com.chutneytesting.example

import com.chutneytesting.kotlin.dsl.ChutneyEnvironment
import com.chutneytesting.kotlin.dsl.ChutneyTarget

val google_fr = ChutneyTarget(name = "search_engine", url = "https://www.google.fr")

val environment_fr = ChutneyEnvironment(
    name = "The French World Wide Web",
    description = "The World Wide Web, for strange but beautiful French people",
    targets = listOf(
        google_fr
    )
)

val google_en = ChutneyTarget(name = "search_engine", url = "https://www.google.com")

val environment_en = ChutneyEnvironment(
    name = "The English World Wide Web",
    description = "The World Wide Web, mostly",
    targets = listOf(
        google_en
    )
)

object ChutneyEnvironmentBuilder {
    private var name: String = "Global"
    private var description: String = "Default environment"
    private var targets: List<ChutneyTarget> = emptyList()

    fun name(name: String): ChutneyEnvironmentBuilder {
        this.name = name
        return this
    }

    fun description(description: String): ChutneyEnvironmentBuilder {
        this.description = description
        return this
    }

    fun targets(targets: List<ChutneyTarget>): ChutneyEnvironmentBuilder {
        this.targets = targets
        return this
    }

    fun build(): ChutneyEnvironment {
        return ChutneyEnvironment(name, description, targets)
    }

}

object ChutneyTargetBuilder {
    private var name: String = "target"
    private var url: String = ""
    private var properties: Map<String, String> = emptyMap()

    fun name(name: String): ChutneyTargetBuilder {
        this.name = name
        return this
    }

    fun url(url: String): ChutneyTargetBuilder {
        this.url = url
        return this
    }

    fun properties(properties: Map<String, String>): ChutneyTargetBuilder {
        this.properties = properties
        return this
    }

    fun build(): ChutneyTarget {
        return ChutneyTarget(name, url, properties)
    }
}
