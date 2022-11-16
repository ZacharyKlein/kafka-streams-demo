package com.objectcomputing.training

import com.objectcomputing.training.streams.WordCountStream
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.testcontainers.containers.KafkaContainer

class KafkaSetup {

    static KafkaContainer kafkaContainer

    static KafkaContainer init() {
        if (kafkaContainer == null) {
            kafkaContainer = new KafkaContainer("5.4.2")
            kafkaContainer.start()
        }

        createTopics()

        return kafkaContainer
    }

    static void destroy() {
        if (kafkaContainer) {
            kafkaContainer.stop()
            kafkaContainer = null
        }
    }

    //Override to create different topics on startup
    private static List<String> getTopics() {
        return ["streams-plaintext-input",
                "streams-wordcount-output",
                "word-count-store"]
    }

    static void createTopics() {
        println "creating topics..."
        def newTopics = topics.collect { topic -> new NewTopic(topic, 1, (short) 1) }
        def admin = AdminClient.create(["bootstrap.servers": kafkaContainer.getBootstrapServers()])

        println "topics: ${newTopics*.name()}"

        admin.createTopics(newTopics)

        println "...finished"

        println admin.listTopics().names().get()
    }
}
