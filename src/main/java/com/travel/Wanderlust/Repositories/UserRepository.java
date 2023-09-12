package com.travel.Wanderlust.Repositories;
import com.travel.Wanderlust.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long>{
    User findByEmail(String email);
    User findByUsername(String username);
    List<User> findByUsernameContainingIgnoreCase(String username);
    @Query("SELECT u FROM User u WHERE u.id IN (SELECT p.user.id FROM Post p GROUP BY p.user.id ORDER BY COUNT(p.user.id) DESC LIMIT 1)")
    User findUserWithMostPosts();

    boolean existsByEmail(String email);
}
