package com.objectcomputing.training.producer;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaClient
public interface WordCountProducer {

  @Topic("streams-plaintext-input")
  void sendWords(String value);

}
