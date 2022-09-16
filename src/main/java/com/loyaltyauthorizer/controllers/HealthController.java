package com.loyaltyauthorizer.controllers;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loyaltyauthorizer.configs.App;
import com.loyaltyauthorizer.types.HealthCheck;

@RestController
@RequestMapping("/api/v1/authorizer/health-check")
public class HealthController {

    @GetMapping
    public ResponseEntity<HealthCheck> check() {
        HealthCheck healthCheck = new HealthCheck();
        healthCheck.setService(App.SERVICE_NAME);
        healthCheck.setDate(LocalDateTime.now());

        return new ResponseEntity<HealthCheck>(healthCheck, HttpStatus.OK);
    }
    
}
