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
        createReportsListFile()
        copySiteFiles()
    }

    private fun createReportsListFile() {
        File(reportRootPath, reportListFileName)
            .bufferedWriter()
            .use { out ->
                out.write("[" + readReportsNames()
                    ?.joinToString(
                        separator = "\"},{\"",
                        prefix = "{\"", "\"}"
                    ) { p -> "env\":\"${p.first}\",\"scenario\":\"${p.second}" } + "]")
            }
    }

    private fun copySiteFiles() {
        pathResolver.getResources("classpath*:chutney-report-website/*").forEach {
            it.inputStream.copyTo(
                out = File(reportRootPath, it.filename).outputStream()
            )
        }
    }

    private fun readReportsNames(): List<Pair<String, String>>? {
        return File(reportRootPath)
            .listFiles { file -> file.isDirectory }
            ?.flatMap { envDir ->
                envDir?.listFiles { _, name ->
                    name.endsWith(".json")
                }?.toList() ?: emptyList()
            }
            ?.map { reportFile ->
                Pair(reportFile.parentFile.name, reportFile.name)
            }
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
