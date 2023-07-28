package com.travel.Wanderlust.Repositories;


import com.travel.Wanderlust.Entities.QuizQuestion;
import com.travel.Wanderlust.Entities.User;
import com.travel.Wanderlust.Entities.UserQuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserQuizAnswerRepository extends JpaRepository<UserQuizAnswer,Long> {

    List<UserQuizAnswer> findByUser(User user);
    UserQuizAnswer findByUserAndQuestion(User user, QuizQuestion question);
}
