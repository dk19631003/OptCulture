package com.optculture.launchpad.repositories;


import java.util.Calendar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.optculture.shared.entities.contact.Contact;

public interface ContactRepository extends JpaRepository<Contact, Long> {

	//@Query("SELECT c FROM Contact c WHERE c.userId=:user_id AND c.contactId = :contactId ")
	Contact findByUserIdAndContactId(Long  user_id,Long cid);
	
	Contact findByContactId(Long cid);
	
	//updateEmailStatusByUserId(emailId, userId, conStatus, OCConstants.CONT_STATUS_ACTIVE);
	@Modifying(flushAutomatically=true,clearAutomatically=true)
	@Query("update Contact set emailStatus= :contactStatus,lastStatusChange=:localTime where emailId = :emailId and userId = :userId")
	int updateEmailStatusByUserId(String emailId,Long userId,String contactStatus,Calendar localTime);
	
	/*
	 * @Modifying(flushAutomatically=true,clearAutomatically=true) int
	 * updateEmailStatusUserId(String emailId,Long userId,)
	 */
	

}
