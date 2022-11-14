package com.objectcomputing.training.listeners;

import io.micronaut.configuration.kafka.streams.event.BeforeKafkaStreamStart;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;

@Singleton
public class BeforeStartKafkaStreamsListenerImp {

    private boolean executed = false;

    @EventListener
    public void execute(BeforeKafkaStreamStart event) {
        executed = true;
    }

    public boolean isExecuted() {
        return executed;
    }
}
