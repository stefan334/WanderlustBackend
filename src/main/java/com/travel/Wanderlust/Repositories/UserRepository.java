package com.travel.Wanderlust.Repositories;
import com.travel.Wanderlust.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long>{
    User findByEmail(String email);
    User findByUsername(String username);
    List<User> findByUsernameContainingIgnoreCase(String username);
}
