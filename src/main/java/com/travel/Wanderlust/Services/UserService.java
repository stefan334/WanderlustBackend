package com.travel.Wanderlust.Services;

import com.travel.Wanderlust.Entities.User;
import com.travel.Wanderlust.Repositories.UserRepository;
import com.travel.Wanderlust.config.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    public static final String USER_NOT_ADDED = "The email you typed is already in use";
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Map<String,String> save(User user) {
        Map<String,String> response = new HashMap<>();

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            user.setRole(Role.USER);
            userRepository.save(user);
            response.put("response", "User Added");
        }catch (Exception e){
            LOG.info(USER_NOT_ADDED);
            response.put("response", USER_NOT_ADDED);
        }
        return response;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public List<User> searchUsersByUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username);
    }

    public String getUsernameByEmail(String email){
        return userRepository.findByEmail(email).getUsername();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
