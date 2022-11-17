package com.app.chatserver.repository;

import com.app.chatserver.model.RefreshToken;
import com.app.chatserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    RefreshToken findByToken(String token);
    boolean existsByToken(String token);
    boolean deleteByUser(User u);
}
