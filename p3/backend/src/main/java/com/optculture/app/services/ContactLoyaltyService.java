package com.optculture.app.services;

import org.springframework.stereotype.Service;
import com.optculture.app.repositories.ContactLoyaltyRepository;
import com.optculture.shared.entities.contact.ContactLoyalty;

@Service
public class ContactLoyaltyService {


	public ContactLoyaltyService(ContactLoyaltyRepository contactsLoyaltyRepository) {
		this.contactsLoyaltyRepository = contactsLoyaltyRepository;
	}

	ContactLoyaltyRepository contactsLoyaltyRepository;

	public ContactLoyalty getContactsLoyaltyByUserIdAndContactId(Long userId, Long contactId) {
		return contactsLoyaltyRepository.findFirstByUserIdAndContactContactIdAndMembershipStatusOrderByLoyaltyIdDesc(userId, contactId, "Active");
	}

}
