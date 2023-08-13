package com.travel.Wanderlust.Repositories;


import com.travel.Wanderlust.Entities.Image;
import com.travel.Wanderlust.Entities.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByCreationDateBetween(Date startDate, Date endDate, Pageable pageable);

    Post findByImage(Image image);
}
