package com.chutneytesting.kotlin.launcher

import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.io.File

object SiteGeneratorMain {
    @JvmStatic
    fun main(args: Array<String>) {
        val reportRootPath: String =
            args.getOrElse(0) { System.getProperty("chutney.reports.rootpath") } ?: CHUTNEY_REPORT_ROOT_PATH
        SiteGenerator(reportRootPath).generateSite()
    }
}

class SiteGenerator(private val reportRootPath: String = CHUTNEY_REPORT_ROOT_PATH) {

    private val reportListFileName = "reports-list.json"
    private val pathResolver = PathMatchingResourcePatternResolver()

    fun generateSite() {
        if (createReportsListFile()) {
            copySiteFiles()
        }
    }

    private fun createReportsListFile(): Boolean {
        if (!File(reportRootPath).exists()) {
            println("Reports' root path does not exist: $reportRootPath")
            return false;
        } else {
            val reportsListFile = File(reportRootPath, reportListFileName)
            reportsListFile
                .bufferedWriter()
                .use { out ->
                    out.write("[" + readReportsNames()
                        .joinToString(
                            separator = "\"},{\"",
                            prefix = "{\"", "\"}"
                        ) { p -> "env\":\"${p.first}\",\"scenario\":\"${p.second}" } + "]")
                }
            println("Reports' list file generated at ${reportsListFile.absolutePath}")
            return true;
        }
    }

    private fun copySiteFiles() {
        pathResolver.getResources("classpath*:chutney-report-website/*").forEach {
            it.inputStream.copyTo(
                out = File(reportRootPath, it.filename).outputStream()
            )
        }
        println("Reports web site copied into ${File(reportRootPath).absolutePath}")
    }

    private fun readReportsNames(): List<Pair<String, String>> {
        return File(reportRootPath)
            .listFiles { file -> file.isDirectory }
            ?.flatMap { envDir ->
                envDir?.listFiles { _, name ->
                    name.endsWith(".json")
                }?.toList() ?: emptyList()
            }
            ?.map { reportFile ->
                Pair(reportFile.parentFile.name, reportFile.name)
            } ?: emptyList()
    }
/*
    private fun readReportsNames(): List<Pair<String, String>> {
        return pathResolver.getResources(reportRootPath.absolutePath).asList()
            .filter { r -> r.file.isDirectory }
            .flatMap {
                it.file.listFiles { _, name ->
                    name.endsWith(".json")
                }?.asList()
                    ?.map { reportFile ->
                        Pair(reportFile.parentFile.name, reportFile.name)
                    } ?: emptyList()
            }
    }
*/
}
