package com.travel.Wanderlust.Controllers;

import com.travel.Wanderlust.Entities.Image;
import com.travel.Wanderlust.Entities.User;
import com.travel.Wanderlust.Repositories.UserRepository;
import com.travel.Wanderlust.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        return userService.findAll();
    }

    @GetMapping("/users/{email}")
    public User getUsers(@PathVariable String email) {
        System.out.println("Getting user by email");
        return userService.findByEmail(email);
    }

    @PostMapping("/uploadImage/{email}/{image}")
    public boolean uploadImage(@PathVariable String email, @PathVariable String image){
        Image image1 = new Image();
        image1.setName(image);
        User user = userService.findByEmail(email);
        image1.setUser_id(user);
        try {
            if(user.getImagesUploaded()==null){
                user.setImagesUploaded(new HashSet<>());
            }
            user.addImage(image1);
            userRepository.save(user);
        }catch(Exception e){
            System.out.println(e);
            return false;
        }
        return true;
    }
}
