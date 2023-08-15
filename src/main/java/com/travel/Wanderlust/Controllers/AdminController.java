package com.travel.Wanderlust.Controllers;

import com.travel.Wanderlust.Entities.Image;
import com.travel.Wanderlust.Entities.Post;
import com.travel.Wanderlust.Entities.User;
import com.travel.Wanderlust.Repositories.ImageRepository;
import com.travel.Wanderlust.Repositories.PostRepository;
import com.travel.Wanderlust.Repositories.UserRepository;
import com.travel.Wanderlust.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;


    // Update user information (Admin-only)
    @PostMapping("/admin/updateUser/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User updatedUser) {
        System.out.println("Called update user");
        try {
            User user = userService.findById(userId);
            if (user != null) {
                // Update user information
                user.setName(updatedUser.getName());
                user.setEmail(updatedUser.getEmail());
                user.setUsername(updatedUser.getUsername());
                user.setPassword(updatedUser.getPassword());
                // Update other user properties as needed

                userService.save(user);
                return ResponseEntity.ok().build();
            }
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    // Delete a post (Admin-only)
    @DeleteMapping("/admin/deletePost/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        try {
            Optional<Post> postOptional = postRepository.findById(postId);
            if (postOptional.isPresent()) {
                Post post = postOptional.get();

                // Delete associated image
                Image image = post.getImage();
                if (image != null) {
                    // Delete image file from storage if needed
                    imageRepository.delete(image);
                }

                // Delete post
                postRepository.delete(post);

                return ResponseEntity.ok().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    // Delete an image (Admin-only)
    @DeleteMapping("/admin/deleteImage/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
        try {
            Image image = imageRepository.findById(imageId).orElse(null);
            if (image != null) {
                // Delete image file from storage if needed
                // Delete image entity
                imageRepository.delete(image);
                return ResponseEntity.ok().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
    @GetMapping("/admin/users/{email}")
    public User getUsers(@PathVariable String email) {
        System.out.println(email);
        if(email.contains("@")){
            System.out.println("Getting user by email");
            return userService.findByEmail(email);
        }else{
            System.out.println("Getting user by username");
            return userService.findByUsername(email);
        }
    }

    @GetMapping("/admin-statistics/total-posts")
    public ResponseEntity<Long> getTotalPosts() {
        Long totalPosts = postRepository.count();
        return ResponseEntity.ok(totalPosts);
    }

    // Endpoint to get total number of users
    @GetMapping("/admin-statistics/total-users")
    public ResponseEntity<Long> getTotalUsers() {
        Long totalUsers = userRepository.count();
        return ResponseEntity.ok(totalUsers);
    }

    // Other statistics endpoints...

    // Example: Endpoint to get average number of likes per post
    @GetMapping("/admin-statistics/average-likes-per-post")
    public ResponseEntity<Double> getAverageLikesPerPost() {
        Double averageLikes = postRepository.calculateAverageLikesPerPost();
        return ResponseEntity.ok(averageLikes);
    }

    // Example: Endpoint to get user with the most posts
    @GetMapping("/admin-statistics/user-with-most-posts")
    public ResponseEntity<User> getUserWithMostPosts() {
        User userWithMostPosts = userRepository.findUserWithMostPosts();
        return ResponseEntity.ok(userWithMostPosts);
    }
    // Other admin-only endpoints for various administrative tasks

}
