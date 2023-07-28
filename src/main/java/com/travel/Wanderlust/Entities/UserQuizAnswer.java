package com.travel.Wanderlust.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_quiz_answer")
@NoArgsConstructor
public class UserQuizAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuizQuestion question;

    @Column(name = "answer_value")
    private String answerValues;

    public UserQuizAnswer(User user, QuizQuestion question, String selectedAnswer) {
        this.user  = user;
        this.question = question;
        this.answerValues = selectedAnswer;
    }

    // getters and setters
}