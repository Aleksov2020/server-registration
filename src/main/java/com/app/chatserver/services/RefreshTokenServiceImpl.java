package com.app.chatserver.services;

import com.app.chatserver.exceptions.TokenException;
import com.app.chatserver.model.RefreshToken;
import com.app.chatserver.model.User;
import com.app.chatserver.repository.RefreshTokenRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private @Getter @Setter String refreshToken;
    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Value("${hay.jwtRefreshExpirationMs}")
    private Integer expiration;
    @Override
    public Optional<RefreshToken> findByToken(String token) {
        if (refreshTokenRepository.existsByToken(token)) {
            return Optional.of(refreshTokenRepository.findByToken(token));
        } else {
            return Optional.empty();
        }
    }
    @Override
    public RefreshToken getRefreshToken(User user){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(expiration));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }
    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenException(ERROR_EXPIRED_TOKEN);
        }
        return token;
    }
    @Transactional
    public boolean deleteByUserId(User user) {
        return refreshTokenRepository.deleteByUser(user);
    }
}
