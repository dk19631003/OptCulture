package org.mq.loyality.common.service;
import java.text.ParseException;
import java.util.List;

import org.mq.loyality.common.hbmbean.Contacts;
import org.mq.loyality.common.hbmbean.ContactsLoyalty;
import org.mq.loyality.common.hbmbean.EmailQueue;
import org.mq.loyality.common.hbmbean.LoginDetails;
import org.mq.loyality.common.hbmbean.LoyaltySettings;
import org.mq.loyality.common.hbmbean.UserOrganization;
import org.mq.loyality.common.hbmbean.Users;
import org.mq.loyality.utils.ActivityForm;
import org.mq.loyality.utils.TransDetails;

public interface UserService {
	public LoyaltySettings getSettingDetails(String url);
	List<UserOrganization> getOrgDetails(Long id);
	Contacts findById(Long cid);
	public void saveProfile(Contacts data);
	public List<TransDetails> getTotTransDetails(StringBuffer sb) throws ParseException;
	public List<TransDetails> getPageTransDetails(StringBuffer sb, int page, int pageSize);
	public Users findByUserId(Long userId);
	public List<ActivityForm> getRecieptDetails(String id, Long long1, String storeName, String docSid, Long userId);
	public void savePassword(ContactsLoyalty contactLoyality);
	public List<ContactsLoyalty> getUser(String userName, LoyaltySettings settings);
	void saveOrUpdate(EmailQueue queue);
	public List<ContactsLoyalty> findLoyaltyListByContactId(Long contactId);
	public void saveOrUpdate(ContactsLoyalty obj);
	public Users getLoyaltyByUserId(Long userId);
	public String getPropertyValueFromDB(String string);
	public Character isValidEmail(String cardId);
	public List<ContactsLoyalty> getContactWithEmail(String cardId);
	public void saveOrUpdate(LoginDetails loginDet);
	
    
	
	
	

}
