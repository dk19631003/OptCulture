package org.mq.loyality.common.dao;

import java.util.ArrayList;

import org.mq.loyality.common.hbmbean.Contacts;
import org.mq.loyality.common.hbmbean.ContactsLoyalty;
import org.mq.loyality.common.hbmbean.Users;

public interface ContactsDao {

	Contacts getContacts(long contactId);

	void saveOrUpdate(Contacts data);

	ArrayList<Object[]> getTotTransDetails(StringBuffer sb);

	ArrayList<Object[]> getTotTransDetails(StringBuffer sb, int page,
			int pageSize);

	ArrayList<Object[]> getRecieptDetails(String id,String docSid, Long orgId,
			String storeName, Long userId);

	void saveOrUpdate(ContactsLoyalty contactLoyality);

	public int updatemobileStatus(String mobile, String status, Users user);

	public Double findContactPurchaseDetails(Long userId, Long contactId);

}
