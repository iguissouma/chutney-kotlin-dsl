import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.ajoberstar.reckon") version "0.13.0"

    kotlin("jvm") version "1.4.32" apply false
}

reckon {
    scopeFromProp()
    snapshotFromProp()
}

subprojects {
    extra["chutneyTestingVersion"] = "1.3.12"
    extra["junitJupiterVersion"] = "5.6.3"

    repositories {
        mavenCentral()
    }

    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        val testImplementation by configurations
        val testRuntimeOnly by configurations

        testImplementation("org.junit.jupiter:junit-jupiter-api:${project.extra["junitJupiterVersion"]}")
        testImplementation("org.junit.jupiter:junit-jupiter-params:${project.extra["junitJupiterVersion"]}")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${project.extra["junitJupiterVersion"]}")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "failed", "skipped")
            showStandardStreams = true
        }
    }
}
