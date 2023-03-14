dependencies {
    implementation(project(":chutney-kotlin-dsl"))
    implementation(platform("org.testcontainers:testcontainers-bom:1.17.6"))
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:kafka")
    testImplementation("org.testcontainers:rabbitmq")
    testImplementation("org.testcontainers:mockserver")
    testImplementation("org.mock-server:mockserver-client-java-no-dependencies:5.15.0")
    testImplementation("org.testcontainers:postgresql")
    testImplementation ("org.postgresql:postgresql:42.5.3")




}

task("chutneyGradleReportSite", JavaExec::class) {
    classpath(configurations.runtimeClasspath)

    mainClass.set("com.chutneytesting.kotlin.execution.report.SiteGeneratorMain")
    args("build/reports/chutney")
}

tasks {
    test {
        systemProperty("chutney.report.rootPath", "build/reports/chutney")
        systemProperty("chutney.engine.stepAsTest", false)
        systemProperty("chutney.log.color.enabled", false)

        finalizedBy("chutneyGradleReportSite")
    }
}
