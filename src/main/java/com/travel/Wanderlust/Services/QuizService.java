package com.travel.Wanderlust.Services;

import com.travel.Wanderlust.Entities.QuizQuestion;
import com.travel.Wanderlust.Entities.User;
import com.travel.Wanderlust.Entities.UserQuizAnswer;
import com.travel.Wanderlust.Repositories.QuizQuestionRepository;
import com.travel.Wanderlust.Repositories.UserQuizAnswerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizService {
    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private UserQuizAnswerRepository userQuizAnswerRepository;

    public QuizQuestion addQuestion(QuizQuestion question) {
        return quizQuestionRepository.save(question);
    }

    public List<QuizQuestion> getAllQuestions() {
        return quizQuestionRepository.findAll();
    }

    public QuizQuestion getQuestionById(Long id) {
        return quizQuestionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Question not found"));
    }

    public UserQuizAnswer addAnswer(UserQuizAnswer answer) {
        return userQuizAnswerRepository.save(answer);
    }

    public List<UserQuizAnswer> getAnswersByUser(User user) {
        return userQuizAnswerRepository.findByUser(user);
    }
}
