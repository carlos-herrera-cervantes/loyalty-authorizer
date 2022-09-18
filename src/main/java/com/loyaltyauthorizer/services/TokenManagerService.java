package com.loyaltyauthorizer.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.loyaltyauthorizer.configs.Jwt;
import com.loyaltyauthorizer.models.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenManagerService {

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        String[] roles = user.getRoles();

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(user.getEmail())
            .setAudience(String.join(",", roles))
            .setExpiration(new Date(System.currentTimeMillis() + Jwt.EXPIRATION * 1000))
            .signWith(SignatureAlgorithm.HS512, Jwt.SECRET_KEY)
            .compact();
    }

}
