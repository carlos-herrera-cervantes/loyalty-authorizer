package com.loyaltyauthorizer.controllers;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.loyaltyauthorizer.configs.Redis;
import com.loyaltyauthorizer.models.Consumer;
import com.loyaltyauthorizer.repositories.ConsumerRepository;
import com.loyaltyauthorizer.repositories.UserSessionRepository;
import com.loyaltyauthorizer.services.ApiKeyService;

@RestController
@RequestMapping("/api/v1/authorizer/consumers")
public class ConsumerController {

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private ApiKeyService apiKeyService;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @GetMapping
    public ResponseEntity<Page<Consumer>> findAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Consumer> consumers = consumerRepository.findAll(pageable);
        return new ResponseEntity<Page<Consumer>>(consumers, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Consumer> create(@RequestBody Consumer consumer) {
        try {
            String apiKey = apiKeyService.generateApiKey(128);
            consumer.setApiKey(apiKey);
            Consumer created = consumerRepository.save(consumer);
            
            String key = String.format(Redis.API_KEY, apiKey);
            userSessionRepository.setSession(key, apiKey);

            return new ResponseEntity<Consumer>(created, HttpStatus.CREATED);
        } catch (NoSuchAlgorithmException e) {
            return new ResponseEntity<Consumer>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("disable/{id}")
    public ResponseEntity<HttpStatus> deleteById(
        @RequestHeader("api-key") String apiKey,
        @PathVariable("id") String id
    ) {
        Optional<Consumer> optional = consumerRepository.findById(id);

        if (!optional.isPresent()) {
            return new ResponseEntity<HttpStatus>(HttpStatus.NOT_FOUND);
        }

        Consumer consumer = optional.get();
        consumer.setActive(false);
        consumerRepository.save(consumer);

        String key = String.format(Redis.API_KEY, apiKey);
        userSessionRepository.destroySession(key);

        return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
    }
    
}
