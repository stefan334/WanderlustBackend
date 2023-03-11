package com.travel.Wanderlust.Controllers;


import com.travel.Wanderlust.Services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
        private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

        private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping
    public String token(Authentication authentication){
        LOG.debug("Token requewsted for user: '{}'", authentication.getName());
        String token = tokenService.generateToken(authentication);
        LOG.debug("Token granted '{}'", token);
        return token;
    }
}
