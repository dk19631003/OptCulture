package com.optculture.app.repositories;

import com.optculture.app.dto.contacts.ContactsDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.optculture.shared.entities.contact.Contact;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

	Contact findOneByContactIdAndUserId(Long contactId, Long userId);
	@Query("SELECT new com.optculture.app.dto.contacts.ContactsDto( c.firstName as firstName,c.lastName as lastName,c.mobilePhone as mobilePhone, c.emailId as emailId, cl.cardNumber as membershipNumber )" +
			" FROM Contact c " +
			" JOIN ContactLoyalty cl ON c.userId = :userId AND c.contactId = cl.contact.contactId AND cl.membershipStatus = 'Active' AND cl.userId = :userId WHERE c.mobilePhone = :mobilePhone ORDER BY c.contactId DESC LIMIT 1 OFFSET 0")
	Optional<ContactsDto> findFirstByMobilePhoneAndUserId(Long userId, String mobilePhone);

	@Query("SELECT new com.optculture.app.dto.contacts.ContactsDto(CAST(c.contactId AS java.lang.String) AS cid,c.createdDate as createdDate,c.modifiedDate as lastInteraction, c.firstName as firstName,c.lastName as lastName,c.mobilePhone as mobilePhone, c.emailId as emailId, cl.cardNumber as membershipNumber, lr.tierName as currentTierName)"+
			" FROM Contact c "+
			" LEFT JOIN ContactLoyalty cl ON c.userId = :userId AND c.contactId = cl.contact.contactId  AND cl.userId = :userId"+
			" LEFT JOIN LoyaltyProgramTier lr ON lr.tierId = cl.programTierId  "+
			" WHERE c.userId = :userId AND ( :#{#contact.firstName} IS NULL  OR  c.firstName LIKE  %:#{#contact.firstName}% )"+
			" AND  ( :#{#contact.lastName} IS NULL  OR  c.lastName LIKE  %:#{#contact.lastName}% ) "+
			" AND (:#{#contact.emailId} IS NULL OR c.emailId LIKE :#{#contact.emailId}% ) "+
			" AND (:#{#contact.mobilePhone} IS NULL OR c.mobilePhone = :#{#contact.mobilePhone} ) AND ( :membershipNumber IS NULL OR cl.cardNumber = :membershipNumber ) ORDER BY c.contactId desc  LIMIT :pageSize OFFSET :startNumber"
			)
	List<ContactsDto> findByContactDetails(Contact contact , String membershipNumber, Long userId, int startNumber, int pageSize);

	Optional<Contact> findFirstByUserIdAndMobilePhone(Long userId, String udf1);
	@Cacheable("totalContacts")
	@Query("SELECT COUNT(c) FROM Contact c  WHERE c.userId = :userId")
	long findTotalContacts(Long userId);
}
