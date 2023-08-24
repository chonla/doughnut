package com.odde.doughnut.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.odde.doughnut.entities.json.ChatRequest;
import com.odde.doughnut.entities.json.ChatResponse;
import com.odde.doughnut.testability.MakeMeWithoutDB;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import io.reactivex.Single;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:repository.xml"})
@Transactional
public class RestChatControllerTests {
  RestChatController target;

  @Mock private OpenAiApi openAiApi;
  MakeMeWithoutDB makeMe = new MakeMeWithoutDB();

  @BeforeEach
  void setUp() {
    target = new RestChatController();
  }

  @Test
  void chatWithAI() {
    // Arrange
    String expected = "I'm ChatGPT";
    Single<ChatCompletionResult> completionResultSingle =
        Single.just(makeMe.openAiCompletionResult().choice(expected).please());
    Mockito.when(openAiApi.createChatCompletion(Mockito.any())).thenReturn(completionResultSingle);

    // Act
    ChatRequest request = new ChatRequest("What's your name?");
    ChatResponse res = target.chat(request);

    // Assert
    assertEquals(new ChatResponse(expected).getAnswer(), res.getAnswer());
  }
}
