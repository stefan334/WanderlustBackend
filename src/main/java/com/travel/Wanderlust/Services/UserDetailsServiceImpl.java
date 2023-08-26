package com.travel.Wanderlust.Services;

import com.travel.Wanderlust.Controllers.AuthController;
import com.travel.Wanderlust.Entities.MyUserPrincipal;
import com.travel.Wanderlust.Entities.User;
import com.travel.Wanderlust.Repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private static final Logger LOG = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email);
        System.out.println(user);
        if (user == null) {
            LOG.info("Not found");
            throw new UsernameNotFoundException(email);
        }
        LOG.info("Found");
        return new MyUserPrincipal(user);
    }


}
