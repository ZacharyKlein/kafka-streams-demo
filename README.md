# Exercise 4

This lab consists of a Gradle multi-project build with two subprojects, `wordcount-api` and `metrics`. `word-count-api` is a Micronaut Application with the Kafka featured applied, and includes a controller that accepts a POST request containing a string of text.

The `metrics` project is a Micronaut Messaging application with the Kafka Streams feature applied. It is the application that will initialize a Kafka Streams topolgy to process data from a Kafka topic.

The `docker-compose.yml` file in the project will start up Kafka and initialize needed topics for the Kafka Streams application.

Before beginning the steps in the exercise, start up Kafka and initialize the environment with docker-compose:

```
docker-compose up
```
Leave that terminal window/tab open while working through the exercise.

## Steps

### word-count-api
1. Implement the `WordCountProducer` as a @KafkaClient - define a producer method that sends a String to the "streams-plaintext-input" topic (no key is needed)
2. Inject the `WordCountProducer` into the `WordCountController`, and update the controller method to send the data on to Kafka
3. Run the word-count-api application with gradlew: `./gradlew run -t`

### metrics
1. Update the `WordCountStream` to complete the Kafka Streams implementation:
   - a. Using the provided `ConfiguredStreamBuilder`, create a KStream from the ""streams-plaintext-input" topic (Hint: See code listing 1.1)
   - b. From the KStream, create a KTable maps over each message, splits the text into words, performs a `groupBy` operation, and finally a `count` of the number of occurrences of each word in the KStream  (Hint: See code listing 1.2)
   - b. Convert the KTable back into a KStream with the `stream()` method, and publish it to the "streams-wordcount-output" topic, with the String (word) as the key and the Long (count) as the value.   (Hint: See code listing 1.2)
2. Update the `WordCountConsumer` to complete the @KafkaListener:
   - a. Annotate the class with `@KafkaListener(offsetReset = EARLIEST, groupId = "WordCountListener")`
   - b. Annotate the `count()` method with `@Topic`, and consume from the "streams-wordcount-output" topic
   - c. Invoke the `printWordTable()` method to print out the results of the in-memory table of wordcounts.
3. Run the metrics application with gradlew: `./gradlew run -t`
4. Send text to the API with CURL:
```
curl -X POST --location "http://localhost:8080/word-count" \
    -H "Content-Type: application/json" \
    -d "{\"text\":  \"count the words name them one by one\"}"
```
5. You should see the word counts logged out from the consumer in the metrics application.

Notes: While not part of the exercise, the metrics application also contains a `WordCountQueryService` which demonstrates the use of the `InteractiveQueryService` from Kafka Streams. It is utilized in the `KafkaStreamsSpec` test class.

## Code Listings

1.1
```
KStream<String, String> source = builder.stream("streams-plaintext-input");
```

1.2
```
KTable<String, Long> groupedByWord = source
                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
                .groupBy((key, word) -> word, Grouped.with(Serdes.String(), Serdes.String()))
                .count(Materialized.as("word-count-store"));
```

1.3
```
groupedByWord
                .toStream()
                .to("streams-wordcount-output", Produced.with(Serdes.String(), Serdes.Long()));
```
