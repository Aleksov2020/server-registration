package com.app.chatserver.services;

import com.app.chatserver.model.RefreshToken;
import com.app.chatserver.model.User;

import java.util.Optional;

public interface RefreshTokenService {
    String ERROR_EXPIRED_TOKEN = "EXPIRED_TOKEN";
    Optional<RefreshToken> findByToken(String token);
    RefreshToken getRefreshToken(User user);
    RefreshToken verifyExpiration(RefreshToken token);
}
