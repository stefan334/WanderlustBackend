package com.travel.Wanderlust.Controllers;

import com.travel.Wanderlust.Services.TokenService;
import com.travel.Wanderlust.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final TokenService tokenService;
    @Autowired
    private UserService userService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/token")
    public Map<String,String> token(Authentication authentication) {
        LOG.info("Token requested for user: '{}'", authentication.getName());
        String token = tokenService.generateToken(authentication);
        LOG.info("Token granted: {}", token);
        Map<String, String> map = new HashMap<>();
        map.put("access_token", token);
        String username = userService.getUsernameByEmail(authentication.getName());
        map.put("username", username);
        return map;
    }

}