package com.chutneytesting.kotlin.dsl

data class ChutneyEnvironment(
    val name: String = "Undefined environment",
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
}

data class ChutneyTarget(
    val name: String = "",
    val url: String = "",
    val configuration: ChutneyConfiguration = ChutneyConfiguration()
)

data class ChutneyConfiguration(
    val properties: Map<String, String> = emptyMap(),
    val security: ChutneySecurityProperties = ChutneySecurityProperties()
)

data class ChutneySecurityProperties(
    val credential: Credential = Credential(),
    val trustStore: String = "",
    val trustStorePassword: String = "",
    val keyStore: String = "",
    val keyStorePassword: String = "",
    val privateKey: String = ""
) {

    data class Credential(
        val username: String = "",
        val password: String = ""
    )
}
