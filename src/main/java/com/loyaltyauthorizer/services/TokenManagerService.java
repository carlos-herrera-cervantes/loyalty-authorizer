package com.loyaltyauthorizer.services;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.loyaltyauthorizer.configs.Jwt;
import com.loyaltyauthorizer.models.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenManagerService {

    public String generateToken(User user) {
        String[] roles = user.getRoles();
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, Jwt.EXPIRATION_DAYS);

        Date expirationDate = calendar.getTime();

        return Jwts.builder()
            .setSubject(user.getEmail())
            .setAudience(String.join(",", roles))
            .setId(user.getId())
            .setIssuedAt(new Date())
            .setExpiration(expirationDate)
            .signWith(SignatureAlgorithm.HS512, Jwt.SECRET_KEY.getBytes())
            .compact();
    }

}
