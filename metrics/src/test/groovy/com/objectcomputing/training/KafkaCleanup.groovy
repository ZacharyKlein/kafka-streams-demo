package com.objectcomputing.training

import org.spockframework.runtime.extension.AbstractGlobalExtension

class KafkaCleanup extends AbstractGlobalExtension {
    @Override
    void stop() {
        KafkaSetup.destroy()
    }
}
