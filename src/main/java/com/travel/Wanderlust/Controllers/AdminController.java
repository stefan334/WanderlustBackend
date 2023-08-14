package com.travel.Wanderlust.Controllers;

import com.travel.Wanderlust.Entities.Image;
import com.travel.Wanderlust.Entities.User;
import com.travel.Wanderlust.Repositories.ImageRepository;
import com.travel.Wanderlust.Repositories.PostRepository;
import com.travel.Wanderlust.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ImageRepository imageRepository;

    // Update user information (Admin-only)
    @PostMapping("/admin/updateUser/{userUsername}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable String userUsername, @RequestBody User updatedUser) {
        try {
            User user = userService.findByUsername(userUsername);
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    // Delete a post (Admin-only)
    @DeleteMapping("/admin/deletePost/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        try {
            postRepository.deleteById(postId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
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

    // Other admin-only endpoints for various administrative tasks

}
