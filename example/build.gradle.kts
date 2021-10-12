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

    mainClass.set("com.chutneytesting.kotlin.launcher.SiteGeneratorMain")
    args("build/reports/chutney")
}

task("chutneyDefaultReportSite", JavaExec::class) {
    classpath(configurations.runtimeClasspath)

    mainClass.set("com.chutneytesting.kotlin.launcher.SiteGeneratorMain")
}

tasks {
    test {
        exclude("**/*Maven*Test.class")
        finalizedBy("chutneyGradleReportSite")
        finalizedBy("chutneyDefaultReportSite")
    }
}
