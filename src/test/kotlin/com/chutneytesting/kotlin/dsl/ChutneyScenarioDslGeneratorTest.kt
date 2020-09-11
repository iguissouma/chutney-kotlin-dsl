package com.chutneytesting.kotlin.dsl

import org.junit.Test
import kotlin.test.assertEquals

class ChutneyScenarioDslGeneratorTest {
    @Test
    fun `abe to create kotlin chutney scenario from json`() {

        val generateDsl = ChutneyScenarioDslGenerator().generateDsl(
            """
            {
              "title": "swapi GET people record",
              "description": "swapi GET people record",
              "givens": [
                {
                  "description": "I set get people service api endpoint",
                  "implementation": {
                    "type": "context-put",
                    "inputs": {
                      "entries": {
                        "uri": "api/people/1"
                      }
                    }
                  }
                }
              ],
              "when": {
                "description": "I send GET HTTP request",
                "implementation": {
                  "type": "http-get",
                  "target": "swapi.dev",
                  "inputs": {
                    "uri": "${'$'}{#uri}"
                  }
                }
              },
              "thens": [
                {
                  "description": "I receive valid HTTP response",
                  "implementation": {
                    "type": "json-assert",
                    "inputs": {
                      "document": "${'$'}{#body}",
                      "expected": {
                        "${'$'}.name": "Luke Skywalker"
                      }
                    }
                  }
                }
              ]
            }
        """.trimIndent()
        )

        assertEquals(generateDsl,
            """
            >val `swapi GET people record` = Scenario(title = "swapi GET people record") {
            >    Given("I set get people service api endpoint") {
            >        ContextPutTask(entries = mapOf("uri" to "api/people/1"))
            >    }
            >    When("I send GET HTTP request") {
            >        HttpGetTask(target = "swapi.dev", uri = "\${'$'}{#uri}")
            >    }
            >    Then("I receive valid HTTP response") {
            >        JsonAssertTask(document = "\${'$'}{#body}", expected = mapOf("${'$'}.name" to "Luke Skywalker"))
            >    }
            >}
            """.trimMargin('>'.toString())
        )

    }
}
