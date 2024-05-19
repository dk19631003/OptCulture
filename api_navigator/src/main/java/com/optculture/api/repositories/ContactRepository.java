package com.optculture.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.optculture.shared.entities.contact.Contact;

import jakarta.transaction.Transactional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
	
	
	@Modifying(flushAutomatically=true,clearAutomatically= true)
	@Transactional
	@Query("update Contact set mobileStatus=:mobileStatus where userId=:userId and mobilePhone=:mobilePhone")
	int updateContactBymobilePhone(String mobileStatus,Long userId,String mobilePhone);
	
}
