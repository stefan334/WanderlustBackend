package com.travel.Wanderlust.Repositories;

import com.travel.Wanderlust.Entities.Like;
import com.travel.Wanderlust.Entities.Location;
import com.travel.Wanderlust.Entities.Post;
import com.travel.Wanderlust.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Like findByPostAndUser(Post post, User user);

}
