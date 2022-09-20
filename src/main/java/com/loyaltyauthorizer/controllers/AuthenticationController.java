package com.loyaltyauthorizer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loyaltyauthorizer.configs.Redis;
import com.loyaltyauthorizer.models.User;
import com.loyaltyauthorizer.repositories.UserRepository;
import com.loyaltyauthorizer.repositories.UserSessionRepository;
import com.loyaltyauthorizer.services.TokenManagerService;
import com.loyaltyauthorizer.types.Credentials;
import com.loyaltyauthorizer.types.MessageResponse;

@RestController
@RequestMapping("/api/v1/authorizer/authentication")
public class AuthenticationController {

    @Autowired
    private TokenManagerService tokenManagerService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @PostMapping("sign-in")
    public ResponseEntity<MessageResponse> signIn(@RequestBody Credentials credentials) {
        User user = userRepository.findByEmail(credentials.getEmail());

        if (user == null) {
            return new ResponseEntity<MessageResponse>(HttpStatus.NOT_FOUND);
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (!(passwordEncoder.matches(credentials.getPassword(), user.getPassword())) || user.isDeactivated()) {
            return new ResponseEntity<MessageResponse>(HttpStatus.UNAUTHORIZED);
        }

        String jwt = tokenManagerService.generateToken(user);
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessage(jwt);

        String key = String.format(Redis.JWT_KEY, user.getEmail());
        userSessionRepository.setSession(key, jwt);

        return new ResponseEntity<MessageResponse>(messageResponse, HttpStatus.OK);
    }

    @PostMapping("sign-out")
    public ResponseEntity<HttpStatus> signOut(
        @RequestHeader("user-email") String userEmail
    ) {
        String key = String.format(Redis.JWT_KEY, userEmail);
        userSessionRepository.destroySession(key);
        return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
    }
    
}
