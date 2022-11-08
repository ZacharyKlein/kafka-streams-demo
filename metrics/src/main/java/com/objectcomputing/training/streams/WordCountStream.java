package com.objectcomputing.training.streams;

import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.util.Arrays;
import java.util.Properties;

@Factory
public class WordCountStream {
    public static final String STREAM_WORD_COUNT = "word-count";
    public static final String INPUT = "streams-plaintext-input";
    public static final String OUTPUT = "streams-wordcount-output";
    public static final String WORD_COUNT_STORE = "word-count-store";

    @Singleton
    @Named(STREAM_WORD_COUNT)
    KStream<String, String> wordCountStream(ConfiguredStreamBuilder builder) {

        Properties props = builder.getConfiguration();
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KStream<String, String> source = builder.stream(INPUT);

        KTable<String, Long> groupedByWord = source
                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
                .groupBy((key, word) -> word, Grouped.with(Serdes.String(), Serdes.String()))
                .count(Materialized.as(WORD_COUNT_STORE));

        groupedByWord
                //convert to stream
                .toStream()
                //send to output using specific serdes
                .to(OUTPUT, Produced.with(Serdes.String(), Serdes.Long()));

        return source;
    }
}
