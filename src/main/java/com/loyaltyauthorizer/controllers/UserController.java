package com.loyaltyauthorizer.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loyaltyauthorizer.configs.Redis;
import com.loyaltyauthorizer.models.User;
import com.loyaltyauthorizer.repositories.UserRepository;
import com.loyaltyauthorizer.repositories.UserSessionRepository;

@RestController
@RequestMapping("/api/v1/authorizer/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        User created = userRepository.save(user);
        return new ResponseEntity<User>(created, HttpStatus.CREATED);
    }

    @DeleteMapping("disable/{id}")
    public ResponseEntity<HttpStatus> disableById(
        @PathVariable("id") String id
    ) {
        Optional<User> optional = userRepository.findById(id);

        if (!optional.isPresent()) {
            return new ResponseEntity<HttpStatus>(HttpStatus.NOT_FOUND);
        }

        User user = optional.get();
        user.setDeactivated(true);
        userRepository.save(user);

        String key = String.format(Redis.JWT_KEY, user.getEmail());
        userSessionRepository.destroySession(key);

        return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
    }
    
}
