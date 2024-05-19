package com.optculture.launchpad.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.optculture.shared.entities.communication.email.SuppressedContacts;

public interface SuppressedContactsRepository extends JpaRepository<SuppressedContacts,Long>{
	

}
