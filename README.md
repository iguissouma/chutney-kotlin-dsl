# Chutney Testing Kotlin DSL

![CI](https://github.com/chutney-testing/chutney-kotlin-dsl/workflows/CI/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.chutneytesting/chutney-kotlin-dsl/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.chutneytesting/chutney-kotlin-dsl)

## DO IT IN CODE {"NOT": "JSON"}

This repository aims to add a kotlin flavor for writing chutney scenarios.

## Why?

- Avoid text copy pasting
- Provide better code assistance using IDE 
- Allow customization for teams

A chutney json scenario:
```json
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
        "uri": "${#uri}"
      }
    }
  },
  "thens": [
    {
      "description": "I receive valid HTTP response",
      "implementation": {
        "type": "json-assert",
        "inputs": {
          "document": "${#body}",
          "expected": {
            "$.name": "Luke Skywalker"
          }
        }
      }
    }
  ]
}
```

Writing the same scenario with a kotlin DSL:
```kotlin
Scenario(title = "SWAPI GET people record") {
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
```

How (k)ool is Kotlin? super (k)ool! 

# How to run Kotlin scenarios

You want to run Chutney scenarios from your local environment or on your CI/CD ?

You want to use the DSL in your tests without the hassle of installing a Chutney server ?

Here we go !

## 1. Define your targets

Here, you can see that ```systemA``` and ```systemB``` share the same name ```mySystem```.
This is usefull for writing scenarios without coupling them to specific URLs or configuration. 

By using the same name and overriding only specific properties, you can run the same scenario on different environment (see next snippet).

```kotlin
val systemA = ChutneyTarget(
    name = "mySystem",
    url = "tcp://my.system.com:4242",
    configuration = ChutneyConfiguration(
        properties = mapOf("some" to "properties"),
        security = ChutneySecurityProperties(
            credential = ChutneySecurityProperties.Credential(
                username = "kakarot",
                password = "uruchim41"
            )
        )
    )
)
val systemB = systemA.copy(url = "tcp://another.url.com:1313")
val systemBprime = systemB.copy(name = "prime", url = "http://yet.another.url")
```



## 2. Define your environments

Take care while adding your targets to an environment. In the previous snippet, ```systemA``` and ```systemB``` share the same name ```mySystem```.
Since the target name is used as an identifier, you should not put targets with the same name in the same environment !

```kotlin
val envA = ChutneyEnvironment(
    name = "envA",
    description = "fake environment for test",
    targets = listOf(
        systemA
    )
)

val envB = ChutneyEnvironment(
    name = "envB",
    description = "fake environment for test",
    targets = listOf(
        systemB,
        systemBprime
    )
)

```

## 3. Define your scenarios

As seen in the two previous snippets, note how the scenario refers only to the target name ```mySystem```.
So this scenario can run on environment ```envA``` and ```envB``` without modifying it.

```kotlin
val say_hi = Scenario(title = "Say hi!") {
    When("Hello world") {
        HttpGetTask(
            target = "mySystem"
        )
    }
    Then("Succeed") {
        SuccessTask()
    }
}
```

## 4. Run your scenarios ! 

For example, you can wrap Chutney execution with JUnit.

```kotlin
class CrispyIntegrationTest {
    @Test
    fun `say hi`() {
        Launcher().run(say_hi, envA)
    }
}
```

### Change default reports folder

By default, reports are in ".chutney/reports". But you can override it using ```Launcher("target/chutney-reports")```

### Expecting a failure

You can change the expecting status of your scenario. For example, the Chutney scenario will fail, 
but not the running JUnit test.
 
```kotlin
@Test
fun `is able to fail`() {
    launcher.run(failing_scenario, environment, StatusDto.FAILURE)
}
 ```

### Running many scenarios

You can simply pass a list of scenarios.

```kotlin
val my_campaign = listOf(
    a_scenario,
    another_scenario
)

@Test
fun `is able to run many scenarios`() {
    launcher.run(my_campaign, environment)
}
```

### Running many scenarios, again

You can create campaigns by using ```@ParameterizedTest```
This is nice because JUnit will wrap each scenario execution into its own.

```kotlin
private companion object {
    @JvmStatic
    fun campaign_scenarios() = Stream.of(
        Arguments.of(a_scenario),
        Arguments.of(another_scenario)
    )
}

@ParameterizedTest
@MethodSource("campaign_scenarios")
fun `is able to emulate a campaign on one environment`(scenario: ChutneyScenario) {
    launcher.run(scenario, environment)
}
```

### Running a campaign, on different environment

To keep it simple, we will combine the two previous snippets, 
but this time we will parameterize the environment. 

```kotlin
private companion object {
    @JvmStatic
    fun environments() = Stream.of(
        Arguments.of(envA),
        Arguments.of(envB)
    )
}

val my_campaign = listOf(
    a_scenario,
    another_scenario
)

@ParameterizedTest
@MethodSource("environments")
fun `is able to run a campaign on different environments`(environment: ChutneyEnvironment) {
    launcher.run(my_campaign, environment)
}
```
