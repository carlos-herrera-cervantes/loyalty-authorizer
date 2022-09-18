package com.loyaltyauthorizer.configs;

public class Jwt {

    private Jwt() {}

    public static final String SECRET_KEY = System.getenv("JWT_SECRET_KEY");

    public static final long EXPIRATION = 18000;
    
}
