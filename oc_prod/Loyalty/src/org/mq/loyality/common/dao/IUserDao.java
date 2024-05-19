package org.mq.loyality.common.dao;
import java.util.ArrayList;
import java.util.List;

import org.mq.loyality.common.hbmbean.ContactsLoyalty;
import org.mq.loyality.common.hbmbean.EmailQueue;
import org.mq.loyality.common.hbmbean.LoginDetails;
import org.mq.loyality.common.hbmbean.LoyaltySettings;
import org.mq.loyality.common.hbmbean.Users;

public interface IUserDao {
	List<ContactsLoyalty> getUser(String username, Long orgno,String password);

	List<LoyaltySettings> getSettingDetails(String url);

	Users findByUserId(Long userId);

	void saveOrUpdate(EmailQueue queue);
	
	List<ContactsLoyalty> getContactList(String c, LoyaltySettings settings);

	void saveOrUpdate(LoginDetails loginDet);

	List<LoginDetails> checkLoginDetails(String login, Long orgId);

	List<ContactsLoyalty> findLoyaltyListByContactId(Long contactId);

	void saveOrUpdate(ContactsLoyalty obj);

	Users getLoyaltyByUserId(Long userId);

	String getPropertyValueFromDB(String prop_key);

	List<ContactsLoyalty> getUser(String login, String mobileRangeJava,	Long userOrgId, Short countryCarrier);

	List<ContactsLoyalty> getUserMobile(String username, Long orgno);

	Character isvalidEmail(String cardId);

	ArrayList<Object[]> getContactWithEmail(String cardId);

	List<ContactsLoyalty> getUserCard(String login, Long userOrgId);

	int updateUsedSMSCount(Long userId, int usedCount);
	

	
}
