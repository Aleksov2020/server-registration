package com.app.chatserver.repository;

import com.app.chatserver.model.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarRepository extends JpaRepository<Avatar, Integer> {
}
