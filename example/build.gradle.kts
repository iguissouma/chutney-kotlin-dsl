dependencies {
    implementation(project(":chutney-kotlin-dsl"))
}

task("cleanChutneyDefaultReportSite", Delete::class) {
    doLast {
        val chutneyDefaultReportSiteDir = project.file(".chutney/reports")
        delete(chutneyDefaultReportSiteDir)
        logger.info("Default Chutney report site deleted : ${chutneyDefaultReportSiteDir.absolutePath}")
    }
}

tasks {
    clean {
        finalizedBy("cleanChutneyDefaultReportSite")
    }
}

task("chutneyGradleReportSite", JavaExec::class) {
    classpath(configurations.runtimeClasspath)

    mainClass.set("com.chutneytesting.kotlin.execution.report.SiteGeneratorMain")
    args("build/reports/chutney")
}

task("chutneyDefaultReportSite", JavaExec::class) {
    classpath(configurations.runtimeClasspath)

    mainClass.set("com.chutneytesting.kotlin.execution.report.SiteGeneratorMain")
}

tasks {
    test {
        systemProperty("junit.chutney.engine.stepAsTest", false)
        systemProperty("junit.chutney.log.color.enabled", false)

        exclude("**/*Maven*Test.class")
        finalizedBy("chutneyGradleReportSite")
        finalizedBy("chutneyDefaultReportSite")
    }
}
