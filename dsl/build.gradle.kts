import java.time.Instant
import java.time.format.DateTimeFormatter

plugins {
    java
    `maven-publish`
    signing
}

val group = "com.chutneytesting"
val timestamp: String = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
val githubUrl = "https://github.com/chutney-testing/${project.name}"
val publicationName = "chutneyKotlinDSL"

dependencies {

    api("com.chutneytesting:engine:${project.extra["chutneyTestingVersion"]}")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin") {
        version {
            strictly("2.12.1")
        }
    }
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("com.chutneytesting:environment:${project.extra["chutneyTestingVersion"]}")
    implementation("org.assertj:assertj-core:3.23.1")
    implementation("org.springframework:spring-core:5.3.17")

    runtimeOnly("com.chutneytesting:task-impl:${project.extra["chutneyTestingVersion"]}")

    testImplementation("org.skyscreamer:jsonassert:1.5.0")
    testImplementation("org.springframework:spring-expression:5.3.17")
    testImplementation(kotlin("scripting-jsr223"))
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation("org.mock-server:mockserver-netty:5.4.1")
    testImplementation("org.junit-pioneer:junit-pioneer:1.7.1")

    // JUnit5 engine dependencies
    implementation("org.junit.platform:junit-platform-engine:${project.extra["junitPlatformVersion"]}")
    implementation("org.junit.platform:junit-platform-launcher:${project.extra["junitPlatformVersion"]}")
    testImplementation("org.junit.platform:junit-platform-testkit:${project.extra["junitPlatformVersion"]}")
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.javadoc {
    if (JavaVersion.current().isJava11Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

publishing {
    publications {
        create<MavenPublication>(publicationName) {
            groupId = group
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
            pom {
                name.set("Chutney Kotlin DSL")
                description.set("Generates Chutney scenarios using Kotlin.")
                inceptionYear.set("2020")
                url.set(githubUrl)
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }
                scm {
                    url.set("${githubUrl}.git")
                    connection.set("scm:git:git@github.com:chutney-testing/${project.name}.git")
                    developerConnection.set("scm:git:git@github.com:chutney-testing/${project.name}.git")
                    tag.set(project.version.toString().takeUnless { it.endsWith("SNAPSHOT") })
                }
                issueManagement {
                    system.set("github")
                    url.set("${githubUrl}/issues")
                }
                ciManagement {
                    system.set("github-ci")
                    url.set("${githubUrl}/actions")
                }
                developers {
                    developer {
                        id.set("iguissouma ")
                        name.set("Issam Guissouma")
                    }
                    developer {
                        id.set("boddissattva")
                        name.set("Matthieu Gensollen")
                    }
                    developer {
                        id.set("bessonm")
                        name.set("Mael Besson")
                    }
                    developer {
                        id.set("nbrouand")
                        name.set("Nicolas Brouand")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            name = "OSSRH"

            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots"

            val ossrhUsername =
                System.getenv("OSSRH_USERNAME") // Use token ; https://s01.oss.sonatype.org/#profile;User%20Token
            val ossrhPassword = System.getenv("OSSRH_PASSWORD") // Use token

            url = uri(releasesRepoUrl)
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
}

signing {
    //useGpgCmd()

    // Format: "0x12345678" ; gpg --list-keys --keyid-format 0xSHORT
    val signingKeyId: String? = System.getenv("CHUTNEY_GPG_KEY_ID")

    // gpg -a --export-secret-subkeys KEY_ID
    val signingKey: String? = System.getenv("CHUTNEY_GPG_KEY")
    val signingPassword: String? = System.getenv("CHUTNEY_GPG_PASSPHRASE")
    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    sign(publishing.publications[publicationName])
}
