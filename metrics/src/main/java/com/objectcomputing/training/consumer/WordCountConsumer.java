package com.objectcomputing.training.consumer;

import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.micronaut.configuration.kafka.annotation.OffsetReset.EARLIEST;

@KafkaListener(offsetReset = EARLIEST, groupId = "WordCountListener")
public class WordCountConsumer {

  private final Map<String, Long> wordCounts = new ConcurrentHashMap<>();

  @Topic("streams-wordcount-output")
  void count(@KafkaKey String word, long count) {
    wordCounts.put(word, count);
  }

  public long getCount(String word) {
    Long num = wordCounts.get(word);
    if (num != null) {
      return num;
    }
    return 0;
  }

  public Map<String, Long> getWordCounts() {
    return Collections.unmodifiableMap(wordCounts);
  }
}
