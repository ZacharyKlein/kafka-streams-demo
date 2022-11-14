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
    ApplicationContext context
    @Shared
    static KafkaContainer kafkaContainer = KafkaSetup.init()

    void setupSpec() {
        List<Object> config = ["kafka.bootstrap.servers", kafkaContainer.bootstrapServers]
        config.addAll(getConfiguration())

        context = ApplicationContext.run(CollectionUtils.mapOf(config as Object[]))
    }

    protected List<Object> getConfiguration() {
        ['micronaut.application.name', "metrics"]
    }

    void cleanupSpec() {
        try {
            if (kafkaContainer) {
                kafkaContainer.stop()
                kafkaContainer = null
            }
            context.stop()
            log.warn("Stopped containers!")
        } catch (Exception ignored) {
            log.error("Could not stop containers")
        }
        context?.close()
    }
}
