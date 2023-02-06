package kafka

import com.chutneytesting.example.ChutneyEnvironmentBuilder
import com.chutneytesting.example.ChutneyTargetBuilder
import com.chutneytesting.example.scenario.kafka_scenario
import com.chutneytesting.kotlin.launcher.Launcher
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName


class KafkaTest {
    private val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))

    @BeforeEach
    fun setUp() {
        kafkaContainer.start()
    }

    @AfterEach
    fun tearDown() {
        kafkaContainer.stop()
    }

    @Test
    fun `publish & consume kafka message`() {
        val environment = ChutneyEnvironmentBuilder
            .targets(
                listOf(
                    ChutneyTargetBuilder
                        .url(kafkaContainer.bootstrapServers)
                        .properties(mapOf("auto.offset.reset" to "earliest")).build()
                )
            ).build()

        Launcher().run(kafka_scenario, environment)
    }
}
