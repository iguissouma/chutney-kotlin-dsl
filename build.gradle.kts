import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.ajoberstar.reckon") version "0.16.1"

    kotlin("jvm") version "1.6.21" apply false
}

reckon {
    scopeFromProp()
    snapshotFromProp()
}

subprojects {
    extra["chutneyTestingVersion"] = "2.0.0"

    repositories {
        mavenCentral()
    }

    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        val implementation by configurations
        val testImplementation by configurations
        val testRuntimeOnly by configurations

        implementation(enforcedPlatform("com.chutneytesting:chutney-parent:${project.extra["chutneyTestingVersion"]}"))

        testImplementation("org.junit.jupiter:junit-jupiter-api")
        testImplementation("org.junit.jupiter:junit-jupiter-params")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "failed", "skipped")
            showStandardStreams = true
        }
        // Pass the proxy configuration to the gradle test executor
        systemProperty("http.proxyHost", System.getProperty("http.proxyHost"))
        systemProperty("http.proxyPort", System.getProperty("http.proxyPort"))
        systemProperty("http.nonProxyHosts", System.getProperty("http.nonProxyHosts"))
        systemProperty("https.proxyHost", System.getProperty("https.proxyHost"))
        systemProperty("https.proxyPort", System.getProperty("https.proxyPort"))
        systemProperty("https.nonProxyHosts", System.getProperty("https.nonProxyHosts"))
    }
}
