package com.ecommerce.user.dto;

public class AuthResponse {

    private final String token;
    private final String token_type = "Bearer";
    private final long expires_in_ms;
    private final UserResponse user;

    public AuthResponse(String token, long expires_in_ms, UserResponse user) {
        this.token         = token;
        this.expires_in_ms = expires_in_ms;
        this.user          = user;
    }

    public String getToken()       { return token; }
    public String getTokenType()   { return token_type; }
    public long getExpiresInMs()   { return expires_in_ms; }
    public UserResponse getUser()  { return user; }
}