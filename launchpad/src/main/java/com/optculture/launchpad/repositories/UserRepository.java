package com.optculture.launchpad.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.optculture.shared.entities.org.User;


public interface UserRepository extends JpaRepository<User, Long> {
	

	User findByuserId(Long userId);
}
