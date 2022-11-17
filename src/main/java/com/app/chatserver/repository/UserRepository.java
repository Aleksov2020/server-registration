package com.app.chatserver.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.chatserver.model.User;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer>{
	User findUserByPhone(String phone);
	boolean existsUserByPhone(String phone);

	User findUserByUserName (String userName);

	boolean existsUserByUserName(String userName);

	List<User> findUserByUserNameStartsWith(String name, Pageable pageable);
}
