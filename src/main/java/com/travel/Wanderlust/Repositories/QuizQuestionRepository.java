package com.travel.Wanderlust.Repositories;


import com.travel.Wanderlust.Entities.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion,Long> {
}