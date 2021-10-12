# Chutney Testing Kotlin DSL examples

This module contains examples showing how to use Chutney kotlin DSL.

A single project with two possibles build configurations (Gradle and Maven).

Three Tests exists, written in JUnit5, demonstrating the different use of the launcher.

To run, just launch the tests :
* ```gradlew clean test```
* ```mvn clean verify```
* Execute tests from your favorite IDE

Note that in order to run the maven command, the kotlin dsl java archive must be present in repository. To deploy locally this archive, run ```gradlew publishToMavenLocal -x :chutney-koltin-dsl:signChutneyKotlinDSLPublication ```
