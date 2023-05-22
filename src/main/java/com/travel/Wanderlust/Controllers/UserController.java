package com.travel.Wanderlust.Controllers;

import com.travel.Wanderlust.Entities.Image;
import com.travel.Wanderlust.Entities.User;
import com.travel.Wanderlust.Repositories.UserRepository;
import com.travel.Wanderlust.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public Map<String, String> saveUser(@RequestBody User user) {
        System.out.println("We are creating a new user now");
        return userService.save(user);
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        System.out.println("We are getting all users");
        List<User> users  = userService.findAll();


        return users;

    }

    @GetMapping("/users/{email}")
    public User getUsers(@PathVariable String email) {
        System.out.println("Getting user by email");
        return userService.findByEmail(email);
    }

    @PostMapping("/uploadImage")
    public boolean uploadImage(@RequestParam("email") String email, @RequestParam("image") MultipartFile imageFile) {
        try {
            Image image = new Image();
            image.setName(imageFile.getOriginalFilename());

            User user = userService.findByEmail(email);
            image.setUser_id(user);

            if (user.getImagesUploaded() == null) {
                user.setImagesUploaded(new HashSet<>());
            }

            user.addImage(image);
            userRepository.save(user);

            // Save the image file to your desired location
            // You can use imageFile.getInputStream() to get the input stream of the file
            // and save it to your desired storage (e.g., local disk, cloud storage, etc.)

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }
}
