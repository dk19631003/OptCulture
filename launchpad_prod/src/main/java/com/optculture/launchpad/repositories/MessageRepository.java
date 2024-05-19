package com.optculture.launchpad.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.optculture.shared.entities.system.Messages;
@Repository
public interface MessageRepository extends JpaRepository<Messages,Long>{
	
	
}
