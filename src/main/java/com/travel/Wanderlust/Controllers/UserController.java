package com.travel.Wanderlust.Controllers;

import com.travel.Wanderlust.Entities.*;
import com.travel.Wanderlust.Repositories.*;
import com.travel.Wanderlust.Services.NotificationService;
import com.travel.Wanderlust.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> saveUser(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();

        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            response.put("error", "Email is already taken.");
            return ResponseEntity.badRequest().body(response);
        }

        // Validate the password
        String password = user.getPassword();
        if (!isValidPassword(password)) {
            response.put("error", "Password must contain both letters and digits and be at least 6 characters long.");
            return ResponseEntity.badRequest().body(response);
        }
        userService.save(user);
        response.put("success", "User Added");
        return ResponseEntity.ok().body(response);
    }

    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[a-zA-Z])(?=.*\\d).{6,}$");
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        System.out.println("We are getting all users");
        return userService.findAll();
    }

    @GetMapping("/users/{email}")
    public User getUserByEmailOrUsername(@PathVariable String email) {
        System.out.println(email);
        if(email.contains("@")){
            System.out.println("Getting user by email");
            return userService.findByEmail(email);
        }else{
            System.out.println("Getting user by username");
            return userService.findByUsername(email);
        }
    }

    @GetMapping("/users/search")
    public List<User> searchUsers(@RequestParam("username") String username) {
        return userService.searchUsersByUsername(username);
    }

    @PostMapping("/follow")
    public ResponseEntity<?> followUser(@RequestParam String userEmailToFollow, @RequestParam String currentUserEmail) {
        try {
            User userToFollow = userRepository.findByEmail(userEmailToFollow);
            User currentUser = userRepository.findByEmail(currentUserEmail);

            if (currentUser != null && userToFollow != null) {
                currentUser.addFollowing(userToFollow);
                userToFollow.addFollower(currentUser);
                userRepository.save(currentUser);
                userRepository.save(userToFollow);
                notificationService.createFollowNotification(userToFollow, currentUser);

                return ResponseEntity.ok().build();
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/getFollowing/{currentUserUsername}")
    public ResponseEntity<?> getFollowing(@PathVariable String currentUserUsername) {
        User currentUser = userRepository.findByUsername(currentUserUsername);
        System.out.println("This user is following: " + currentUser.getFollowing());
        return ResponseEntity.ok().body(currentUser.getFollowing());

    }

    @GetMapping("/getFollowers/{currentUserUsername}")
    public ResponseEntity<?> getFollowers(@PathVariable String currentUserUsername) {
        User currentUser = userRepository.findByUsername(currentUserUsername);
        System.out.println("This user is following: " + currentUser.getFollowing());
        return ResponseEntity.ok().body(currentUser.getFollowers());
    }

    @GetMapping("getNotifications/{username}/unread")
    public List<Notification> getUnreadNotifications(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return Collections.emptyList();
        }
        return notificationService.getUnreadNotifications(user);
    }

    @PostMapping("/unfollow")
    public ResponseEntity<?> unfollowUser(@RequestParam String userEmailToUnfollow, @RequestParam String currentUserEmail) {
        User currentUser = userRepository.findByEmail(currentUserEmail);
        User userToUnfollow = userRepository.findByEmail(userEmailToUnfollow);

        if (currentUser != null && userToUnfollow != null) {
            currentUser.unfollow(userToUnfollow);
            userRepository.save(currentUser);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

}
