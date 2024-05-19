package com.optculture.launchpad.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.optculture.shared.entities.contact.ContactLoyalty;

@Repository
public interface ContactLoyaltyRepository extends JpaRepository<ContactLoyalty, Long> {
	
	ContactLoyalty findByloyaltyId(Long loyaltyId);
	
	List<ContactLoyalty> findByContactContactIdAndUserId(Long contactId,Long userId);

}
