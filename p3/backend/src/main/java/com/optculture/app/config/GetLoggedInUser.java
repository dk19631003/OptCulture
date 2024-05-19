package com.optculture.app.config;

import com.optculture.app.dto.login.MyUserPrincipal;
import com.optculture.shared.entities.org.User;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class GetLoggedInUser {
    //@Bean
    public User getLoggedInUser() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof MyUserPrincipal userPrincipal) {
            return userPrincipal.getUser();
        } else {
            return null;
        }
    }
}
