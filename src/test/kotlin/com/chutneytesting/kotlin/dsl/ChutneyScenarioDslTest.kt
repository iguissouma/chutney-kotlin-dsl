package com.chutneytesting.kotlin.dsl

import com.gregwoodfill.assert.`should equal json`
import org.junit.Test

class ChutneyScenarioDslTest {

    @Test
    fun `abe to create chutney scenario using kotlin dsl`() {

        val scenario = Scenario(title = "SWAPI GET people record") {
            Given("I set get people service api endpoint") {
                ContextPutTask(entries = mapOf("uri" to "api/people/1"))
            }
            When("I send GET HTTP request") {
                HttpGetTask(target = "swapi.dev", uri = "uri".spEL())
            }
            Then("I receive valid HTTP response") {
                JsonAssertTask(document = "body".spEL(), expected = mapOf("$.name" to "Luke Skywalker"))
            }
        }

        "$scenario" `should equal json` """
            {
              "title": "SWAPI GET people record",
              "description": "SWAPI GET people record",
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

    }

}
