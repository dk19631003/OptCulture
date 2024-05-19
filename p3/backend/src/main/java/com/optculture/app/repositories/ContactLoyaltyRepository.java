package com.optculture.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.optculture.shared.entities.contact.ContactLoyalty;

@Repository
public interface ContactLoyaltyRepository extends JpaRepository<ContactLoyalty, Long> {

	ContactLoyalty findFirstByUserIdAndContactContactIdAndMembershipStatusOrderByLoyaltyIdDesc(Long userId, Long contactId, String membershipStatus);
	ContactLoyalty findFirstByUserIdAndCardNumber(Long userId, String cardNumber);
	ContactLoyalty findFirstByUserIdAndMobilePhone(Long userId, String mobile);
	@Query("SELECT COUNT(c) FROM ContactLoyalty c WHERE c.userId = :userId AND c.cardNumber = :cardNumber")
	long findCountByUserIdAndCardNumber(Long userId, String cardNumber);
}
