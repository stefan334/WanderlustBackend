package com.travel.Wanderlust.Repositories;


import com.travel.Wanderlust.Entities.Image;
import com.travel.Wanderlust.Entities.Post;
import com.travel.Wanderlust.Entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByCreationDateBetween(Date startDate, Date endDate, Pageable pageable);

    Post findByImage(Image image);


    Page<Post> findByUserIn(Set<User> following, PageRequest of);

    List<Post> findByUserId(Long id);

    @Query("SELECT AVG(subq.likeCount) FROM (SELECT SIZE(p.likes) AS likeCount FROM Post p) AS subq")
    Double calculateAverageLikesPerPost();
}
