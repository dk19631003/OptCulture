package org.mq.captiway.scheduler.services;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.mq.captiway.scheduler.dao.ContactParentalConsentDao;
import org.mq.captiway.scheduler.dao.MailingListDao;
import org.mq.captiway.scheduler.dao.PurgeDao;
import org.mq.captiway.scheduler.dao.PurgeDaoForDML;
import org.mq.captiway.scheduler.dao.SuppressedContactsDao;
import org.mq.captiway.scheduler.dao.UnsubscribesDao;
import org.mq.captiway.scheduler.dao.UnsubscribesDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.utils.OCConstants;
import org.springframework.beans.factory.InitializingBean;


public class PurgeList implements InitializingBean {
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private MailingListDao mailingListDao;
	private PurgeDao purgeDao;
	private PurgeDaoForDML purgeDaoForDML;
	public PurgeDaoForDML getPurgeDaoForDML() {
		return purgeDaoForDML;
	}

	public void setPurgeDaoForDML(PurgeDaoForDML purgeDaoForDML) {
		this.purgeDaoForDML = purgeDaoForDML;
	}

	private UnsubscribesDao unsubscribesDao;
	private UnsubscribesDaoForDML unsubscribesDaoForDML;
	public UnsubscribesDaoForDML getUnsubscribesDaoForDML() {
		return unsubscribesDaoForDML;
	}

	public void setUnsubscribesDaoForDML(UnsubscribesDaoForDML unsubscribesDaoForDML) {
		this.unsubscribesDaoForDML = unsubscribesDaoForDML;
	}

	private SuppressedContactsDao suppressedContactsDao;
	private ContactParentalConsentDao contactParentalConsentDao;
	private static DirContext ictx = null;
	public static LRUCache<String, String> domainCache = new LRUCache<String, String>(100);
	public MailingListDao getMailingListDao() {
		return mailingListDao;
	}

	public PurgeDao getPurgeDao() {
		return purgeDao;
	}

	public UnsubscribesDao getUnsubscribesDao() {
		return unsubscribesDao;
	}

	public SuppressedContactsDao getSuppressedContactsDao() {
		return suppressedContactsDao;
	}

	public ContactParentalConsentDao getContactParentalConsentDao() {
		return contactParentalConsentDao;
	}

	public static DirContext getIctx() {
		return ictx;
	}

	public static LRUCache<String, String> getDomainCache() {
		return domainCache;
	}

	public void setMailingListDao(MailingListDao mailingListDao) {
		this.mailingListDao = mailingListDao;
	}

	public void setPurgeDao(PurgeDao purgeDao) {
		this.purgeDao = purgeDao;
	}

	public void setUnsubscribesDao(UnsubscribesDao unsubscribesDao) {
		this.unsubscribesDao = unsubscribesDao;
	}

	public void setSuppressedContactsDao(SuppressedContactsDao suppressedContactsDao) {
		this.suppressedContactsDao = suppressedContactsDao;
	}

	public void setContactParentalConsentDao(
			ContactParentalConsentDao contactParentalConsentDao) {
		this.contactParentalConsentDao = contactParentalConsentDao;
	}

	public static void setIctx(DirContext ictx) {
		PurgeList.ictx = ictx;
	}

	public static void setDomainCache(LRUCache<String, String> domainCache) {
		PurgeList.domainCache = domainCache;
	}

	
	public void addForPurge(Long userId, Long listId) {
		try{
		//MailingList mailingList = mailingListDao.findById(list);
		purgeDaoForDML.initiatePurge(userId, listId);
		}catch (Exception e) {
			logger.info("Exception :: ",e);
		}

	}

	public void checkForValidDomainByEmailId(Contacts contact) {

		if (contact == null) {

			logger.debug("No contact..returning ");
			return;

		}

		String emailId = contact.getEmailId();
		if (emailId == null || emailId.trim().isEmpty()) {

			logger.debug("No Email id..returning ");

			contact.setEmailStatus(Constants.CONT_STATUS_INVALID_EMAIL);
			// contactsDao.saveOrUpdate(contact);

			return;

		}

		String retMsg = checkForValidDomainByEmailId(emailId);
		if (retMsg == null||retMsg.equalsIgnoreCase(
				Constants.CONT_STATUS_ACTIVE)) {

			// need to set actual status for the contact
			contact.setPurged(true);
			// observe the contcat's emailstatus first
			// if it is active no need to change its emailStatus
			// if not try to modify the status based on the optin medium and and
			// all such...
			if (contact.getEmailStatus() != null
					&& contact.getEmailStatus().equalsIgnoreCase(
							Constants.CONT_STATUS_ACTIVE)) {
				contact = validateSuprresed(contact);
				contact = validateUnsubscribe(contact);
				return;
			}

			// contact.setEmailStatus(Constants.CONT_STATUS_ACTIVE);
			// String optinMedium = contact.getOptinMedium();

			List<MailingList> mlList = mailingListDao.findByContactBit(contact
					.getUsers().getUserId(), contact.getMlBits());
			Iterator<MailingList> mlItr = mlList.iterator();
			// Set<MailingList> mlset = contact.getMlSet();
			// Iterator<MailingList> mlItr = mlset.iterator();
			MailingList mailingList = null;
			while (mlItr.hasNext()) {
				mailingList = mlItr.next();

				/*
				 * if(mailingList.isCheckParentalConsent()) {
				 * 
				 * contact.setEmailStatus(getContactEmailStatus(contact,
				 * mailingList.getCheckDoubleOptin(),
				 * mailingList.isCheckParentalConsent())); continue; }
				 */
				if (contact.getEmailStatus().equalsIgnoreCase(
						Constants.CONT_STATUS_PARENTAL_PENDING)) {

					contact.setEmailStatus(getContactEmailStatus(contact,
							mailingList.getCheckDoubleOptin(), true));
					continue;

				}
				if (mailingList.getCheckDoubleOptin()) {

					if (contact.getEmailStatus().equalsIgnoreCase(
							Constants.CONT_STATUS_PARENTAL_PENDING))
						continue;

					contact.setEmailStatus(Constants.CONT_STATUS_OPTIN_PENDING);

				}
			}// while

			if (contact.getEmailStatus() == null
					|| contact.getEmailStatus().equals(
							Constants.CONT_STATUS_PURGE_PENDING)) {

				validateSuprresed(contact);
				validateUnsubscribe(contact);
				if (contact.getEmailStatus() == null
						|| contact.getEmailStatus().equals(
								Constants.CONT_STATUS_PURGE_PENDING)) {
					contact.setEmailStatus(Constants.CONT_STATUS_ACTIVE);
				}

			}

		} else {

			// purging done but not a valid domain or mail server
			contact.setPurged(true);
			contact.setEmailStatus(retMsg);

		}

		// contactsDao.saveOrUpdate(contact);

	}// checkForValidDomainByEmailId

	public static String checkForValidDomainByEmailId(String emailId) {

		if (emailId == null || emailId.trim().length() == 0) {

			logger.debug("got no email Id");
			return "got no email Id";

		}

		int pos = emailId.indexOf('@');

		if (pos == -1) {
			logger.debug("got invalid email Id : no '@' found.");
			return "got invalid email Id : no '@' found.";

		}

		String cacheMsgStr = null;
		String domain = emailId.substring(++pos).trim().toUpperCase();
		cacheMsgStr = domainCache.get(domain);
		
		if(cacheMsgStr==null){
		try {
			int mxCount=-1;
			for (int i = 0; i < 1; i++) {
				try {
					mxCount = doLookup(domain);
					cacheMsgStr = (mxCount > 0) ? "Active" : "Not a Mail Server";
					break;
				} 
				catch (NamingException ex) {
					cacheMsgStr = ex.getMessage().trim().toUpperCase();
					if(cacheMsgStr.contains("DNS ERROR")) {
						// try { Thread.sleep(100); } catch (InterruptedException e) {	}
						// logger.warn(" Once again trying for domain :" + domain);
						cacheMsgStr = Constants.CONT_STATUS_PURGE_PENDING;
						 continue;
					} // if
					else if(cacheMsgStr.contains("DNS NAME NOT FOUND")) {
						cacheMsgStr="Invalid Domain";
					}
					break;
				}// catch
			}// for
		}finally{
			/*if(domainCache.get(domain)==null && (cacheMsgStr.equalsIgnoreCase("Active")||cacheMsgStr.equalsIgnoreCase("Not a Mail Server")||cacheMsgStr.equalsIgnoreCase("Invalid Domain"))){
				domainCache.put(domain.toLowerCase(), cacheMsgStr);
				try{
					purgeDao.saveOrUpdate(new DomainStatus(domain.toLowerCase(), cacheMsgStr));
					}catch (Exception e) {
						logger.info("Exception :: ",e);
					}
				if(!newDomains.contains(new DomainStatus(domain.toLowerCase(),cacheMsgStr))){
					newDomains.add(new DomainStatus(domain.toLowerCase(),cacheMsgStr));
				}
			}*/
		}
		}
		return cacheMsgStr;

	}

	private Contacts validateSuprresed(Contacts contact) {

		try {
			logger.debug("single contact  supp entr ");

			Map<String, String> suppcategoryMap = new HashMap<String, String>();

			suppcategoryMap.put(Constants.SUPP_TYPE_BOUNCED,
					Constants.CONT_STATUS_BOUNCED);
			suppcategoryMap.put(Constants.CS_STATUS_SPAMMED,
					Constants.CONT_STATUS_REPORT_AS_SPAM);

			String suppressType = suppressedContactsDao
					.isAlreadySuppressedContact(contact.getUsers().getUserId(),
							contact.getEmailId());
			logger.debug("single contact setting status supp " + suppressType);
			if (suppressType != null) {

				contact.setEmailStatus(suppcategoryMap.get(suppressType));
			}
			return contact;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return contact;
		}
	}

	private Contacts validateUnsubscribe(Contacts contact) {

		try {
			logger.debug("single contact  unsub entr ");
			String status = null;

			status = unsubscribesDao.isAlreadyUnsubscribedContact(contact
					.getUsers().getUserId(), contact.getEmailId());

			logger.debug("single contact setting status unsub  " + status);
			if (status != null)
				contact.setEmailStatus(status);

			logger.debug("single contact  unsub exit ");

			return contact;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return contact;
		}
	}

	public String getContactEmailStatus(Contacts contact, boolean optInFlag,
			boolean consentFlag) {
		String status = Constants.CON_STATUS_ACTIVE;

		/*
		 * boolean isPurgeStatus =
		 * (status.equalsIgnoreCase(Constants.CONT_STATUS_INVALID_DOMAIN) ||
		 * status.equalsIgnoreCase(Constants.CONT_STATUS_NOT_A_MAIL_SERVER) ||
		 * status.equalsIgnoreCase(Constants.CONT_STATUS_INVALID_EMAIL));
		 * 
		 * 
		 * if(isPurgeStatus) {
		 * 
		 * return status; }
		 */

		// boolean optInFlag = ml.getCheckDoubleOptin();
		// boolean consentFlag = ml.isCheckParentalConsent();

		String optinMedium = contact.getOptinMedium();

		if (optInFlag) {

			status = Constants.CONT_STATUS_OPTIN_PENDING;

			if (consentFlag
					&& optinMedium != null
					&& optinMedium
							.startsWith(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM)) {

				Calendar bdCal = contact.getBirthDay();

				if (bdCal != null
						&&

						(Calendar.getInstance().getTimeInMillis() - bdCal
								.getTimeInMillis()) / (1000 * 60 * 60 * 24) <= (365 * 13)) {

					ContactParentalConsent contactParentalConsent = contactParentalConsentDao
							.findByContactId(contact.getContactId());
					if (contactParentalConsent != null
							&& contactParentalConsent
									.getStatus()
									.equalsIgnoreCase(
											Constants.CONT_PARENTAL_STATUS_PENDING_APPROVAL)) {

						status = Constants.CONT_STATUS_PARENTAL_PENDING;

					}// if

				}// if

			} // else

		}

		if (consentFlag
				&& optinMedium != null
				&& optinMedium
						.startsWith(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM)) {

			Calendar bdCal = contact.getBirthDay();

			/*
			 * if(optInFlag) status = Constants.CONT_STATUS_OPTIN_PENDING; else
			 */

			if (bdCal != null
					&&

					(Calendar.getInstance().getTimeInMillis() - bdCal
							.getTimeInMillis()) / (1000 * 60 * 60 * 24) <= (365 * 13)) {

				ContactParentalConsent contactParentalConsent = contactParentalConsentDao
						.findByContactId(contact.getContactId());
				if (contactParentalConsent != null
						&& contactParentalConsent
								.getStatus()
								.equalsIgnoreCase(
										Constants.CONT_PARENTAL_STATUS_PENDING_APPROVAL)) {
					status = Constants.CONT_STATUS_PARENTAL_PENDING;

				}

			} // else
			else {

				if (optInFlag) {

					status = Constants.CONT_STATUS_OPTIN_PENDING;

				} else {

					status = Constants.CONT_STATUS_ACTIVE;
				}

			}

		}

		return status;

	}

	/**
	 * Checks for the MX record of the given Host
	 * 
	 * @param hostName
	 * @return count of available MX servers
	 * @throws NamingException
	 */
	public static int doLookup(String hostName) throws NamingException {

		Attributes attrs = ictx.getAttributes(hostName, new String[] { "MX" });
		Attribute attr = attrs.get("MX");
		if (attr == null)
			return (0);
		return (attr.size());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			final Hashtable<Object, Object> env = new Hashtable<Object, Object>();
			env.put("java.naming.factory.initial",
					"com.sun.jndi.dns.DnsContextFactory");
			ictx = new InitialDirContext(env);
		} catch (NamingException e) {
			logger.error("Exception ::::", e);
		}
			List<DomainStatus> allDomains  = purgeDao.getAllDomainswithStatus();
			for(DomainStatus domain:allDomains){
			domainCache.put(domain.getDomain(), domain.getStatus());
			}
			logger.info("domain cache size :: in.com "+domainCache.get("in.com"));
		}
	
		
}
