package com.objectcomputing.training

import groovy.util.logging.Slf4j
import io.micronaut.context.ApplicationContext
import io.micronaut.core.util.CollectionUtils
import io.micronaut.runtime.server.EmbeddedServer
import org.testcontainers.containers.KafkaContainer
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

@Slf4j
abstract class AbstractTestContainersSpec extends Specification {

    PollingConditions conditions = new PollingConditions(timeout: 60, delay: 1)

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer
    @Shared
    @AutoCleanup
    ApplicationContext context
    @Shared
    static KafkaContainer kafkaContainer = KafkaSetup.init()

    void setupSpec() {
        List<Object> config = ["kafka.bootstrap.servers", kafkaContainer.bootstrapServers]
        config.addAll(getConfiguration())

        embeddedServer = ApplicationContext.run(EmbeddedServer, CollectionUtils.mapOf(config as Object[]))

        context = embeddedServer.applicationContext
    }

    protected List<Object> getConfiguration() {
        [
                'micronaut.application.name', "metrics",
                'kafka.streams.default.num.stream.threads', 1,
                'kafka.streams.word-count.num.stream.threads', 1]
    }


    void cleanupSpec() {
        try {
            embeddedServer.stop()
            log.warn("Stopped containers!")
        } catch (Exception ignored) {
            log.error("Could not stop containers")
        }
        embeddedServer?.close()
    }
}
