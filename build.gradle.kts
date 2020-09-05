import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.0"
}
group = "me.iguissouma"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}
dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.5")
    testImplementation(kotlin("test-junit"))
    testImplementation("com.gregwoodfill.assert:kotlin-json-assert:0.1.0")
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
