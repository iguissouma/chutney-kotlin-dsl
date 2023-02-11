package com.chutneytesting.example.scenario

import com.chutneytesting.kotlin.dsl.*

const val RABBITMQ_EXCHANGE = "my.exchange"
const val RABBITMQ_QUEUE = "my.queue"
val amqp_scenario = Scenario(title = "Films library") {

    When("I publish my favorite film") {
        AmqpBasicPublishAction(
            target = "RABBITMQ_TARGET",
            exchangeName = RABBITMQ_EXCHANGE,
            routingKey = "children.fiction",
            headers = mapOf(
                "season" to "1",
            ),
            properties = mapOf(
                "content_type" to "application/json",
            ),
            payload = """
                {
                "title": "Castle in the Sky",
                "director": "Hayao Miyazaki",
                "rating": 78,
                "category": "fiction"
                }
            """.trimIndent(),
        )
    }

    Then("I consume available films from my queue") {
        AmqpBasicConsumeAction(
            target = "RABBITMQ_TARGET",
            queueName = RABBITMQ_QUEUE,
            nbMessages = 1,
            selector = "\$..[?(\$.headers.season=='1')]",
            timeout = "5 sec",
            ack = true
        )
    }

    And("I check that I got my favorite film") {
        AssertAction(
            asserts = listOf(
                "#headers.get(0).get('season').equals('1')".elEval(),
                "#jsonPath(#payload, '\$.title').equals('Castle in the Sky')".elEval(),
                "#jsonPath(#payload, '\$.director').equals('Hayao Miyazaki')".elEval(),
            )
        )
    }

}
