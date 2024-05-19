package org.mq.captiway.scheduler.services;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.ContactParentalConsent;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.DomainStatus;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.Messages;
import org.mq.captiway.scheduler.beans.PurgeQueue;
import org.mq.captiway.scheduler.dao.ContactParentalConsentDao;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.MailingListDao;
import org.mq.captiway.scheduler.dao.MailingListDaoForDML;
import org.mq.captiway.scheduler.dao.MessagesDao;
import org.mq.captiway.scheduler.dao.MessagesDaoForDML;
import org.mq.captiway.scheduler.dao.PurgeDao;
import org.mq.captiway.scheduler.dao.PurgeDaoForDML;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zkplus.spring.SpringUtil;

public class PurgeThread  extends Thread{
private Long userId;
private Hashtable<String, Integer> quesHt;
private static final Logger logger =  LogManager.getLogger(Constants.SCHEDULER_LOGGER);
private static String quesEmailPatterns = PropertyUtil.getPropertyValue("quesEmailPatterns");
private static PurgeDao purgeDao = null;
private static PurgeDaoForDML purgeDaoForDML = null;
private MailingListDao mailingListDao;
private MailingListDaoForDML mailingListDaoForDML;
//private static Set<DomainStatus> newDomains = new LinkedHashSet<DomainStatus>(); 


public PurgeThread(Long userId){
	this.userId=userId;
	
}
	@Override
	public void run() {
	try {
		logger.info("started purgethread for user "+userId+" at "+System.currentTimeMillis());
		UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName("usersDao");
		ContactsDao contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName("contactsDao");
		ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName("contactsDaoForDML");
		mailingListDao = (MailingListDao) ServiceLocator.getInstance().getDAOByName("mailingListDao");
		mailingListDaoForDML = (MailingListDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName("mailingListDaoForDML");
		MessagesDao messagesDao = (MessagesDao) ServiceLocator.getInstance().getDAOByName("messagesDao");
		MessagesDaoForDML messagesDaoForDML = (MessagesDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName("messagesDaoForDML");
		purgeDao = (PurgeDao) ServiceLocator.getInstance().getDAOByName("purgeDao");
		purgeDaoForDML = (PurgeDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName("purgeDaoForDML");
		List<PurgeQueue> purgeRows = purgeDao.getAllPurgePendingByUserId(userId);
		for(PurgeQueue purge : purgeRows){
			//MailingList mailingList = mailingListDao.find(purge.getListId());
			Long purgeMListId = purge.getListId();
			Long totalSizetoPurge = null;
			int size=100;
			int breakInt=100;
			Long contactId = 0l;
			List<Contacts> contactsList;

			List<Map<String, Object>> adminList = usersDao.findAllAdmins();

				try {
					quesHt = new Hashtable<String, Integer>();
					if(logger.isInfoEnabled()) logger.info(" UnpurgedContacts size of List Id="+purgeMListId+" :"+totalSizetoPurge);
					
					MailingList ml = mailingListDao.findById(purgeMListId);
					
					boolean optflag = ml.getCheckDoubleOptin();
						
					do {
						
						//contactsList = contactsDao.getUnpurgedContactsByListId(purgeMListId, contactId, size);
						contactsList = contactsDao.getUnpurgedContactsByListId(ml, contactId, size);
						breakInt = contactsList.size();
						if(breakInt==0) break;
						
						if(contactsList.size()>0) {
							contactId = contactsList.get(contactsList.size()-1).getContactId();
						}
						
						
						try {
							contactsList = validateSMTP(contactsList, optflag);
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("-- Exception -- ",e);
						}
						
						contactsDaoForDML.saveByCollection(contactsList);
						//System.gc();
					} while(breakInt == size); //while startIndex
					
					if(logger.isInfoEnabled()) logger.info(" Purging is finished for the list :"+purgeMListId);
					
					
					
					if(logger.isDebugEnabled()) logger.debug(" Got return back........");
					
				} catch (Exception ex) {
					logger.error("Exception ::::", ex);
					logger.error("** Exception : Root", (Throwable)ex);
				}
				finally {
					mailingListDaoForDML.setMlStatusById(purgeMListId, Constants.MAILINGLIST_STATUS_ACTIVE);
				}
				//Reset the breakInt, and Contact Id.
				breakInt=100;
				contactId = 0l;
				
				//TODO need to send a message to the user describing briefly about the purged contacts

				if(quesHt.size() > 0) {
					StringBuffer msgSb = 
						new StringBuffer("Found the following questionable patterns in the mailing List : "+ mailingListDao.findById(purgeMListId).getListName() +"\r\n");
					
					Enumeration<String> keysEnum= quesHt.keys();
					String keyStr;
					while(keysEnum.hasMoreElements()) {
						keyStr = keysEnum.nextElement();
						
						msgSb.append(keyStr.replace(",", "") +" : "+ quesHt.get(keyStr) +"\r\n");
					} // while
					
					if(adminList!=null && adminList.size() > 0) {
						
						for (Map<String, Object> admin : adminList) {
							if(logger.isInfoEnabled()) logger.info(">>>> ALERTS SENT TO ADMIN ID : "+admin.get("user_id"));
							
							Messages msgs = new Messages("Admin Alerts", "Questionable Email Pattern", msgSb.toString().trim(), Calendar.getInstance(),
											"Inbox", false, "INFO", usersDao.find((Long)admin.get("user_id")));
							//messagesDao.saveOrUpdate(msgs);
							messagesDaoForDML.saveOrUpdate(msgs);
							
							
		/*					(new MessageHandler(messagesDao,null)).sendMessage("Admin Alerts", 
									"Questionable Email Pattern", msgSb.toString().trim(), 
									"Inbox", false, "INFO", usersDao.find((Long)admin.get("user_id")));*/
						} // for
					} // if
					
				} // if(quesHt.size() > 0)
				
				purge.setStatus(Constants.PURGE_STATUS_COMPLETED);
				purge.setPurgedDate(Calendar.getInstance());
				purgeDaoForDML.saveOrUpdate(purge);

			} //while
		logger.info("ended purgethread for user "+userId+" at "+System.currentTimeMillis());
	}catch (Exception e) {
		// TODO Auto-generated catch block
		logger.info("Exception :: ",e);
	}finally{
		/*synchronized (this.getClass()) {
			logger.info("size of new domains..."+newDomains.size());
			if(newDomains.size() > 0){
				try{
				purgeDao.saveOrUpdateAll(newDomains);
				}catch (Exception e) {
					logger.info("Exception :: ",e);
				}
				newDomains.clear();
			}
			
		}*/
		//System.gc();
	}
}
private List<Contacts> validateSMTP(List<Contacts> contactsList, boolean optinFlag) {

	String mlOptStatusStr = Constants.CON_STATUS_ACTIVE;
	
	if(optinFlag) {
		mlOptStatusStr = Constants.CONT_STATUS_OPTIN_PENDING;
	}
	
	for (Contacts contact : contactsList) {
		
		try {
		if(contact.getEmailId() == null || contact.getEmailId().trim().length() == 0) {
		//	logger.debug("contact.getEmailId() is null of contact Id is "+contact.getContactId());
			
			contact.setPurged(true);
			contact.setEmailStatus(Constants.CONT_STATUS_INVALID_EMAIL);
			continue;
		}
		int pos = contact.getEmailId().indexOf('@');
		
		if (pos == -1) {
			continue;
		}

		String emailFirstPart = contact.getEmailId().substring(0,pos).trim().toUpperCase();
		
		emailFirstPart = "," + emailFirstPart + ",";
		if(quesEmailPatterns.indexOf(emailFirstPart) != -1) {
			Integer countVal = quesHt.containsKey(emailFirstPart) ? quesHt.get(emailFirstPart) : 0;
				quesHt.put(emailFirstPart, countVal+1);
		}
		
		String domain = contact.getEmailId().substring(++pos).trim().toUpperCase();			
		String cacheMsgStr = PurgeList.domainCache.get(domain);
		List<MailingList> mlList = mailingListDao.findByContactBit(contact.getUsers().getUserId(), contact.getMlBits());
		if(cacheMsgStr != null) {
			
			if(cacheMsgStr.equals("Active")) { 	// Purged with valid domain name. 
				contact.setPurged(true);
				
				/*contact.setEmailStatus(Constants.CONT_STATUS_ACTIVE);
				Set<MailingList> mlset = contact.getMlSet();
				Iterator<MailingList> mlItr = mlset.iterator();
				//MailingList mailingList = null;
				while(mlItr.hasNext()){
				//mailingList = mlItr.next();
					if(mlItr.next().getCheckDoubleOptin()){
						contact.setEmailStatus(Constants.CONT_STATUS_OPTIN_PENDING);
						break;
					}
				}*/
				if(contact.getEmailStatus() != null && contact.getEmailStatus().equalsIgnoreCase(Constants.CONT_STATUS_ACTIVE) ) continue;
				
				
				//Set<MailingList> mlset = contact.getMlSet();
				Set<MailingList> mlset = new HashSet<MailingList>(mlList);
				Iterator<MailingList> mlItr = mlset.iterator();
				MailingList mailingList = null;
				while(mlItr.hasNext()){
				mailingList = mlItr.next();
					
					
					if(contact.getEmailStatus().equalsIgnoreCase(Constants.CONT_STATUS_PARENTAL_PENDING)) {
						
						contact.setEmailStatus(getContactEmailStatus(contact, mailingList.getCheckDoubleOptin(), true));
						continue;
					}
					
					else if(mailingList.getCheckDoubleOptin()) {
						
						if(contact.getEmailStatus().equalsIgnoreCase(Constants.CONT_STATUS_PARENTAL_PENDING) ) continue;
						
						contact.setEmailStatus(Constants.CONT_STATUS_OPTIN_PENDING);
						
					}
				}//while

				if(contact.getEmailStatus() == null || contact.getEmailStatus().equals(Constants.CONT_STATUS_PURGE_PENDING)) {
					
					contact.setEmailStatus(Constants.CONT_STATUS_ACTIVE);
					
				}
				
				
				
				
			}
			else if(cacheMsgStr.contains("DNS ERROR") || 
					cacheMsgStr.contains("DNS SERVER FAILURE")) { // Communication error 
				continue;
			}
			else {   	//  Purged with invalid domain.					
				contact.setPurged(true);
				contact.setEmailStatus(cacheMsgStr);
			}
			
			continue;
		} // if
		
		cacheMsgStr="";
		try {
			int mxCount=-1;
			for (int i = 0; i < 1; i++) {
				try {
					mxCount = PurgeList.doLookup(domain);
					cacheMsgStr = (mxCount > 0) ? "Active" : "Not a Mail Server";
					break;
				} 
				catch (NamingException ex) {
					cacheMsgStr = ex.getMessage().trim().toUpperCase();
					if(cacheMsgStr.contains("DNS ERROR")) {
						// try { Thread.sleep(100); } catch (InterruptedException e) {	}
						// logger.warn(" Once again trying for domain :" + domain);
						 continue;
					} // if
					else if(cacheMsgStr.contains("DNS NAME NOT FOUND")) {
						cacheMsgStr="Invalid Domain";
					}
					break;
				}// catch
			}// for
		} 
		finally {
			/*if(PurgeList.domainCache.get(domain)==null && (cacheMsgStr.equalsIgnoreCase("Active")||cacheMsgStr.equalsIgnoreCase("Not a Mail Server")||cacheMsgStr.equalsIgnoreCase("Invalid Domain"))){
				PurgeList.domainCache.put(domain.toLowerCase(), cacheMsgStr);
				if(!newDomains.contains(new DomainStatus(domain.toLowerCase(),cacheMsgStr))){
					newDomains.add(new DomainStatus(domain.toLowerCase(),cacheMsgStr));
				}
			}*/
			
			if(cacheMsgStr.equals("Active")) {        // Purged with valid domain name. 
				contact.setPurged(true);
				
				if(contact.getEmailStatus() != null && contact.getEmailStatus().equalsIgnoreCase(Constants.CONT_STATUS_ACTIVE) ) continue;
				
				//Set<MailingList> mlset = contact.getMlSet();
				Set<MailingList> mlset = new HashSet<MailingList>(mlList);
				Iterator<MailingList> mlItr = mlset.iterator();
				MailingList mailingList = null;
				while(mlItr.hasNext()){
				mailingList = mlItr.next();
					
					
					if(mailingList.isCheckParentalConsent()) {
						
						contact.setEmailStatus(getContactEmailStatus(contact, mailingList.getCheckDoubleOptin(), mailingList.isCheckParentalConsent()));
						continue;
					}
					
					else if(mailingList.getCheckDoubleOptin()) {
						
						if(contact.getEmailStatus().equalsIgnoreCase(Constants.CONT_STATUS_PARENTAL_PENDING) ) continue;
						
						contact.setEmailStatus(Constants.CONT_STATUS_OPTIN_PENDING);
						
					}
				}//while

				if(contact.getEmailStatus() == null || contact.getEmailStatus().equals(Constants.CONT_STATUS_PURGE_PENDING)) {
					
					contact.setEmailStatus(Constants.CONT_STATUS_ACTIVE);
					
				}
				
				
				/*//contact.setEmailStatus(Constants.CONT_STATUS_ACTIVE);
				Set<MailingList> mlset = contact.getMlSet();
				Iterator<MailingList> mlItr = mlset.iterator();
				//MailingList mailingList = null;
				while(mlItr.hasNext()){
				//mailingList = mlItr.next();
					if(mlItr.next().getCheckDoubleOptin()){
						contact.setEmailStatus(Constants.CONT_STATUS_OPTIN_PENDING);
					}
				}
				*/
				
				//contact.setEmailStatus(getContactEmailStatus(contact, contact.getMailingList()));
			}
			else if(cacheMsgStr.contains("DNS ERROR") || 
					cacheMsgStr.contains("DNS SERVER FAILURE")) {       // Communication error 
				continue;
			}
			else {        //  Purged with invalid domain.		
				contact.setPurged(true);
				contact.setEmailStatus(cacheMsgStr);
			}
		} // finally
		
	}catch(Exception excep){
		logger.error("Exception occured while validating Purge for contact:::::: " + contact.getContactId() + " :::: ", excep);
	}
	} // for each
	return contactsList;
} //validateSMTP

public  String getContactEmailStatus(Contacts contact, boolean optInFlag, boolean consentFlag) throws Exception {
	String status = Constants.CON_STATUS_ACTIVE;
	ContactParentalConsentDao contactParentalConsentDao = (ContactParentalConsentDao) ServiceLocator.getInstance().getDAOByName("ContactParentalConsentDao");
	
	
	/*boolean isPurgeStatus = (status.equalsIgnoreCase(Constants.CONT_STATUS_INVALID_DOMAIN) ||
							status.equalsIgnoreCase(Constants.CONT_STATUS_NOT_A_MAIL_SERVER) ||
							status.equalsIgnoreCase(Constants.CONT_STATUS_INVALID_EMAIL));
	
	
	if(isPurgeStatus) {
		
		return status;
	}*/
							
	//boolean optInFlag = ml.getCheckDoubleOptin();
	//boolean consentFlag = ml.isCheckParentalConsent();
	
	String optinMedium = contact.getOptinMedium();
	
	
	if(optInFlag) {
		
		 status = Constants.CONT_STATUS_OPTIN_PENDING;
		
		
		if (consentFlag && optinMedium != null && optinMedium.startsWith(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM)) {
			
			Calendar bdCal =contact.getBirthDay();
			
			
			 if(bdCal != null && 
					 
					 (Calendar.getInstance().getTimeInMillis()-bdCal.getTimeInMillis())/(1000 * 60*60*24) <= (365*13) ) {
				 
				 ContactParentalConsent contactParentalConsent = contactParentalConsentDao.findByContactId(contact.getContactId());
				 if(contactParentalConsent != null && contactParentalConsent.getStatus()
						 .equalsIgnoreCase(Constants.CONT_PARENTAL_STATUS_PENDING_APPROVAL)) {
					 
					 status = Constants.CONT_STATUS_PARENTAL_PENDING;
					 
				 }//if
				 
			 }//if
			
		} // else
		
	}
	
	if (consentFlag && optinMedium != null && optinMedium.startsWith(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM)) {
		
		Calendar bdCal =contact.getBirthDay();
		
		/* if(optInFlag) status = Constants.CONT_STATUS_OPTIN_PENDING;
		 else*/  
		
		
		 if(bdCal != null && 
				 
				 (Calendar.getInstance().getTimeInMillis()-bdCal.getTimeInMillis())/(1000 * 60*60*24) <= (365*13) ) {
			 
			 ContactParentalConsent contactParentalConsent = contactParentalConsentDao.findByContactId(contact.getContactId());
			 if(contactParentalConsent != null && contactParentalConsent.getStatus()
					 .equalsIgnoreCase(Constants.CONT_PARENTAL_STATUS_PENDING_APPROVAL)) {
				 status = Constants.CONT_STATUS_PARENTAL_PENDING;
				 
			 }
		 
		 } // else
		 else{
			 
			 if(optInFlag) {
				 
				 status = Constants.CONT_STATUS_OPTIN_PENDING;
				 
			 }else{
				 
				 status = Constants.CONT_STATUS_ACTIVE;
			 }
			 
			 
			 
		 }
		 
	
	}
	
	return status;
	
}


}
