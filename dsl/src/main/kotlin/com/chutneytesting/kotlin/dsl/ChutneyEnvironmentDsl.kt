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
    val configuration: ChutneyConfiguration = ChutneyConfiguration()
)

data class ChutneyConfiguration(
    val properties: Map<String, String> = emptyMap(),
    val security: ChutneySecurityProperties = ChutneySecurityProperties()
)

data class ChutneySecurityProperties(
    val credential: Credential? = null,
    val trustStore: String? = null,
    val trustStorePassword: String? = null,
    val keyStore: String? = null,
    val keyStorePassword: String? = null,
    val privateKey: String? = null
) {

    data class Credential(
        val username: String? = null,
        val password: String? = null
    )
}
