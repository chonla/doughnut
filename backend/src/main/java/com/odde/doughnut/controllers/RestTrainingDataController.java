package com.odde.doughnut.controllers;

import com.odde.doughnut.entities.MarkedQuestion;
import com.odde.doughnut.entities.json.TrainingData;
import com.odde.doughnut.factoryServices.ModelFactoryService;
import com.odde.doughnut.models.UserModel;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.SessionScope;

@RestController
@SessionScope
@RequestMapping("/api/gettrainingdata")
class RestTrainingDataController {
  private final ModelFactoryService modelFactoryService;
  private UserModel currentUser;

  public RestTrainingDataController(
      ModelFactoryService modelFactoryService, UserModel currentUser) {
    this.modelFactoryService = modelFactoryService;
    this.currentUser = currentUser;
  }

  @GetMapping("/goodtrainingdata")
  public List<TrainingData> getGoodTrainingData() {
    currentUser.assertLoggedIn();
    List<MarkedQuestion> markedQuestions = new ArrayList<>();
    modelFactoryService.markedQuestionRepository.findAll().forEach(markedQuestions::add);
    return markedQuestions.stream().map(MarkedQuestion::getTrainingData).toList();
  }
}
