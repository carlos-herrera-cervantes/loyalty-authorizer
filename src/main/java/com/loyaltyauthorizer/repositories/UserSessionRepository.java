package com.loyaltyauthorizer.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserSessionRepository {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void setSession(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void destroySession(String key) {
        redisTemplate.opsForValue().getAndDelete(key);
    }
    
}
