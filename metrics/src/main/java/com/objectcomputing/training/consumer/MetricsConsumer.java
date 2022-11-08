package com.objectcomputing.training.consumer;

import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@KafkaListener
public class MetricsConsumer {

  private static final Logger LOG = LoggerFactory.getLogger(MetricsConsumer.class);

  @Topic("metrics-order-size")
  void receiveMetricsOrder(Long size) {
    if (LOG.isInfoEnabled()) {
      LOG.info("Recording metrics for order with size: {}", size);
    }
  }

  @Topic("metrics-book-pages")
  void receiveMetricsBook(Long pages) {
    if (LOG.isInfoEnabled()) {
      LOG.info("Recording metrics for book with pages: {}", pages);
    }
  }


}
