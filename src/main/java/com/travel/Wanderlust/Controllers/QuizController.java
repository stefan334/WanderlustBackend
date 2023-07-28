package com.travel.Wanderlust.Controllers;

import com.travel.Wanderlust.Entities.QuizQuestion;
import com.travel.Wanderlust.Entities.User;
import com.travel.Wanderlust.Entities.UserQuizAnswer;
import com.travel.Wanderlust.Repositories.UserQuizAnswerRepository;
import com.travel.Wanderlust.Repositories.UserRepository;
import com.travel.Wanderlust.Services.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class QuizController {
    @Autowired
    private QuizService quizService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserQuizAnswerRepository userQuizAnswerRepository;

    @GetMapping("/questions")
    public List<QuizQuestion> getAllQuestions() {
        try {
            return quizService.getAllQuestions();
        }catch (Exception e){
            System.out.println(e);
            return List.of();
        }
    }

    @GetMapping("/questions/{id}")
    public QuizQuestion getQuestionById(@PathVariable Long id) {
        return quizService.getQuestionById(id);
    }

    @PostMapping("/questions")
    public QuizQuestion addQuestion(@RequestBody QuizQuestion question) {
        return quizService.addQuestion(question);
    }

    @PostMapping("/answers")
    public UserQuizAnswer addUserAnswers(@RequestBody Map<Long, String> answers, @RequestParam("userEmail") String userEmail) {
        // Retrieve the user by email
        User user = userRepository.findByEmail(userEmail);

        // Process each question-answer entry in the map
        for (Map.Entry<Long, String> entry : answers.entrySet()) {
            Long questionId = entry.getKey();
            String answer = entry.getValue();

            // Retrieve the question by ID
            QuizQuestion question = quizService.getQuestionById(questionId);

            // Check if an existing UserQuizAnswer exists for the user and question
            UserQuizAnswer existingAnswer = userQuizAnswerRepository.findByUserAndQuestion(user, question);
            if (existingAnswer != null) {
                // Update the existing answer
                existingAnswer.setAnswerValues(answer);
                userQuizAnswerRepository.save(existingAnswer);
            } else {
                // Create and save a new UserQuizAnswer entity
                UserQuizAnswer userQuizAnswer = new UserQuizAnswer(user, question, answer);
                userQuizAnswerRepository.save(userQuizAnswer);
            }
        }

        // Return the response as needed
        return new UserQuizAnswer();
    }


}