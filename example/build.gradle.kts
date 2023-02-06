dependencies {
    implementation(project(":chutney-kotlin-dsl"))
    testImplementation("org.testcontainers:testcontainers:1.17.6")
    testImplementation("org.testcontainers:kafka:1.17.6")

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
