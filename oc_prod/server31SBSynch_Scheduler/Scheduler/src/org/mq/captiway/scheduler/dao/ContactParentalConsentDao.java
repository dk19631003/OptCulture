package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.mq.captiway.scheduler.beans.ContactParentalConsent;

public class ContactParentalConsentDao extends AbstractSpringDao{

	public ContactParentalConsentDao() {}
	/*
	public void saveOrUpdate(ContactParentalConsent contactConsent){
        super.saveOrUpdate(contactConsent);
    }*/
	
	public List<ContactParentalConsent> findAllByUserId(Long currentUserId, String status, String srchKey, int firstResult, int maxResult) {
		String query = "";
		
		String subquery ="";
		if(srchKey != null) {
			subquery = " AND contactEmail like '%"+srchKey+"%' ";
		}
		if(status.equals("All")) {
			query="FROM ContactParentalConsent WHERE userId="+currentUserId+ subquery +" order by sentDate desc";
		  }else {	
		
			query="FROM ContactParentalConsent WHERE userId="+currentUserId+" AND status like '" + status + "'"+subquery+" order by sentDate desc" ;
		  }
		
		
		
		
		
		return executeQuery(query, firstResult, maxResult);
		
	}
	
	public int findCountByUserId(Long currentUserId, String status, String srchKey) {
		
		String query = "";
		String subquery ="";
		if(srchKey != null) {
			subquery = " AND contactEmail like '%"+srchKey+"%' ";
		}
		
		
		if(status.equals("All")) {
			query="SELECT COUNT(*) FROM ContactParentalConsent WHERE userId="+currentUserId+subquery;
		  }else {	
		
			query="SELECT COUNT(*) FROM ContactParentalConsent WHERE userId="+currentUserId+" AND status like '" + status+"'"+subquery;
		  }
		
		List list = getHibernateTemplate().find(query);
		if(list.size()>0)
			return ((Long)list.get(0)).intValue();
		else
			return 0;
		
		
		
	}
	
	
	public ContactParentalConsent findByContactId(Long cid) {
		
		String qry = "FROM ContactParentalConsent WHERE contactId="+cid;
		
		List<ContactParentalConsent> list = getHibernateTemplate().find(qry);
		if(list.size()>0)
			return (list.get(0));
		else
			return null;
		
	}//findByContactId
	
	
	
}
