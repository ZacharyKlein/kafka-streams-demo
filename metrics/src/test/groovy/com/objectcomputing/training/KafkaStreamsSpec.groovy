package com.objectcomputing.training

import com.objectcomputing.training.consumer.WordCountConsumer
import com.objectcomputing.training.producer.WordCountProducer
import com.objectcomputing.training.service.WordCountQueryService
import spock.lang.Retry

@Retry
class KafkaStreamsSpec extends AbstractTestContainersSpec {

    void "test kafka stream application"() {
        given:
        def WORD_COUNT_STORE = "word-count-store"
        WordCountQueryService wordCountQueryService = context.getBean(WordCountQueryService)

        when:
        WordCountProducer wordCountClient = context.getBean(WordCountProducer)
        wordCountClient.sendWords("The quick brown fox jumps over the lazy dog. THE QUICK BROWN FOX JUMPED OVER THE LAZY DOG'S BACK")

        WordCountConsumer countListener = context.getBean(WordCountConsumer)

        then:
        conditions.eventually {
            countListener.getCount("fox") > 0
            countListener.getCount("jumps") > 0
            wordCountQueryService.getWordCount(WORD_COUNT_STORE, "fox") > 0
            wordCountQueryService.getWordCount(WORD_COUNT_STORE, "jumps") > 0
            wordCountQueryService.<String, Long> getGenericKeyValue(WORD_COUNT_STORE, "the") > 0
        }
    }


    void "test BeforeStartKafkaStreamsListener execution"() {
        when:
        def builder = context.getBean(com.objectcomputing.training.listeners.BeforeStartKafkaStreamsListenerImp)

        then:
        builder.executed
    }
}
