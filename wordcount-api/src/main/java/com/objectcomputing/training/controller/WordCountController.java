package com.objectcomputing.training.controller;

import com.objectcomputing.training.producer.WordCountProducer;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;

@Controller("word-count")
public class WordCountController {
  WordCountProducer producer;

  public WordCountController(WordCountProducer producer) {
    this.producer = producer;
  }

  @Post
  HttpResponse<?> postWordCount(@Body String text) {
    producer.sendWords(text);

    return HttpResponse.ok();
  }


}
