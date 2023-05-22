package com.travel.Wanderlust.Controllers;

import com.travel.Wanderlust.Entities.QuizQuestion;
import com.travel.Wanderlust.Services.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class QuizController {
    @Autowired
    private QuizService quizService;

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
}