package com.app.chatserver.services;

import io.jsonwebtoken.Claims;

public interface JwtTokenService {
    String DEFAULT_ISSUER = "HAY!";
    String DEFAULT_SUBJECT = "AuthToken";
    String ERROR_INVALID_JWT_SIGNATURE = "INVALID_JWT_SIGNATURE";
    String ERROR_INVALID_JWT_TOKEN = "INVALID_JWT_TOKEN";
    String ERROR_EXPIRED_TOKEN = "EXPIRED_TOKEN";
    String ERROR_UNSUPPORTED_JWT_TOKEN = "UNSUPPORTED_JWT_TOKEN";
    String ERROR_CLAIMS_IS_EMPTY = "CLAIMS_IS_EMPTY";
    String createJwtAuthTokenByUserId(Integer id);
    Claims getAllClaimsFromToken(String token);
    boolean validateToken(String authToken);
    String getSignature(String token);
}
