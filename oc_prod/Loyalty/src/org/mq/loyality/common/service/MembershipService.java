package org.mq.loyality.common.service;

import org.mq.loyality.common.hbmbean.ContactsLoyalty;
import org.mq.loyality.common.hbmbean.LoyaltyProgramTier;
import org.mq.loyality.exception.LoyaltyProgramException;

public interface MembershipService {
	public org.mq.loyality.common.hbmbean.LoyaltyProgram findById(Long prgmId);
	public LoyaltyProgramTier getTierById(Long tierId);
	ContactsLoyalty getContactsLoyaltyByCardId(Long cardId);
	Object[] fetchExpiryValues(Long loyaltyId, String expDate, String rewardFlag);
	public ContactsLoyalty getLoyaltyByPrgmAndPhone(Long programId, String phone, String countryCarrier) ;
	public ContactsLoyalty getLoyaltyByPrgmAndMembrshp(Long programId,
			String newPhone);
	public Double findPointsToUpgrade(ContactsLoyalty contactsLoyalty, LoyaltyProgramTier loyaltyProgramTier) throws LoyaltyProgramException;
	
	
	
	
	
	
	
	
	
	
	

}
