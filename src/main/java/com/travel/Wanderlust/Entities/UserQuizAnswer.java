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

    @ElementCollection
    @CollectionTable(name = "user_quiz_answer_values", joinColumns = @JoinColumn(name = "user_quiz_answer_id"))
    @Column(name = "answer_value")
    private List<String> answerValues;

    // getters and setters
}