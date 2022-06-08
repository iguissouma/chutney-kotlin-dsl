package com.chutneytesting.kotlin.dsl

data class ChutneyEnvironment(
    val name: String,
    val description: String = "",
    val targets: List<ChutneyTarget> = emptyList()
) {
    fun findTarget(targetName: String?): ChutneyTarget? {
        return try {
            targets.first { it.name == targetName }
        } catch (nsee: NoSuchElementException) {
            null
        }
    }

    override fun toString(): String {
        return "ChutneyEnvironment(name='$name', description='$description')"
    }
}

data class ChutneyTarget(
    val name: String,
    val url: String,
    val properties: Map<String, String> = emptyMap()
)
