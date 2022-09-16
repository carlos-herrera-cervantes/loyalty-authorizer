package com.loyaltyauthorizer.types;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class HealthCheck {

    String service;

    LocalDateTime date;
    
}
