package com.odde.doughnut.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.odde.doughnut.testability.MakeMe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:repository.xml"})
@Transactional
class RestCommentControllerTest {
  RestCommentController controller;
  //  UserModel currentUser;
  //  @Mock
  //  OpenAiApi openAiApi;
  @Autowired MakeMe makeMe;

  @BeforeEach
  void Setup() {
    // Set up a CommentRepositoryMock
    // inject into controller
    controller = new RestCommentController(makeMe.modelFactoryService);
  }

  @Nested
  class GetCommentByID {
    @Test
    void givenCommentShouldGetCommentByID() {
      // create some dummy data [ Comment ... ] as expected outcome
      // mock CommentRepository.getCommentsById(1234) to return [ Comment ... ]
      int noteId = 1234;
      assertEquals(1, controller.getComments(noteId).size());
    }
  }
}
