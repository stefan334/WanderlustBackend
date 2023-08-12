package com.travel.Wanderlust.Repositories;

import com.travel.Wanderlust.Entities.Comment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
