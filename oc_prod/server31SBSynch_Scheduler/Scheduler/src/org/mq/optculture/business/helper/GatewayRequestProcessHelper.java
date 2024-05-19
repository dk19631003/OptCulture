package org.mq.optculture.business.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.mq.captiway.scheduler.beans.ClickaTellSMSInbound;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.EventTrigger;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.OCSMSGateway;
import org.mq.captiway.scheduler.beans.OrgSMSkeywords;
import org.mq.captiway.scheduler.beans.SMSBounces;
import org.mq.captiway.scheduler.beans.SMSCampaignReport;
import org.mq.captiway.scheduler.beans.SMSCampaignSent;
import org.mq.captiway.scheduler.beans.SMSSettings;
import org.mq.captiway.scheduler.beans.SMSSuppressedContacts;
import org.mq.captiway.scheduler.beans.UserSMSGateway;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.ClickaTellSMSInboundDao;
import org.mq.captiway.scheduler.dao.ClickaTellSMSInboundDaoForDML;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.EventTriggerDao;
import org.mq.captiway.scheduler.dao.MailingListDao;
import org.mq.captiway.scheduler.dao.MailingListDaoForDML;
import org.mq.captiway.scheduler.dao.OCSMSGatewayDao;
import org.mq.captiway.scheduler.dao.OrgSMSkeywordsDao;
import org.mq.captiway.scheduler.dao.SMSBouncesDao;
import org.mq.captiway.scheduler.dao.SMSBouncesDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDao;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDao;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDaoForDML;
import org.mq.captiway.scheduler.dao.SMSSettingsDao;
import org.mq.captiway.scheduler.dao.SMSSuppressedContactsDao;
import org.mq.captiway.scheduler.dao.SMSSuppressedContactsDaoForDML;
import org.mq.captiway.scheduler.dao.UserSMSGatewayDao;
import org.mq.captiway.scheduler.services.CaptiwayToSMSApiGateway;
import org.mq.captiway.scheduler.services.EventTriggerEventsObservable;
import org.mq.captiway.scheduler.services.UnicelSMSGateway;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.SMSStatusCodes;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.SMSHTTPDLRRequestObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GatewayRequestProcessHelper {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	public Object getKeyWordFromMsgContent(String msgContent, String receivedNumber ) throws BaseServiceException{
		
		SMSSettings settingKeyWord = findSettingsKeyWordFromContent(msgContent, receivedNumber);
		if(settingKeyWord != null) return settingKeyWord;
		
		
		OrgSMSkeywords orgSMSkeyWord = findOrgSMSKeyWordFromContent(msgContent, receivedNumber);
		if(orgSMSkeyWord != null) return orgSMSkeyWord;
			
			
		return null;
		
	}
		
	/*public boolean checkAndPerformGlobalOptOut(String content, String moFrom, Calendar cal) throws Exception{
		
		if(isAKeyword(content)) {
			
			addToSuppressedContacts(null, moFrom, cal);
		}
		
		return true;
	}*/
	
	public static OCSMSGateway getOcSMSGateway(Users user, String type) throws BaseServiceException{
		
		UserSMSGatewayDao userSMSGatewayDao = null;
		OCSMSGatewayDao OCSMSGatewayDao = null;
		
		UserSMSGateway userSmsGateway = null;
		OCSMSGateway ocgateway = null;
		
		try {
			userSMSGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
			OCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("No DAO found with given id");
		}
		
		 userSmsGateway = userSMSGatewayDao.findByUserId(user.getUserId(), type);
			
		if(userSmsGateway == null) {
			
			throw new BaseServiceException("no default smstype available for user"+user.getUserId());
			
		}
		
		 ocgateway = OCSMSGatewayDao.findById(userSmsGateway.getGatewayId());
		
		return ocgateway;
	}
	
	public ClickaTellSMSInbound createInBoundObj(String sourceNumber, String receivedNumber, Users user, 
			String content, Calendar receivedTime, String usedKeyword, String autoResponse) throws BaseServiceException{
		
		
		try {
			
			ClickaTellSMSInboundDao clickaTellSMSInboundDao = (ClickaTellSMSInboundDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CLICKATELLSMSINBOUND_DAO);
			ClickaTellSMSInboundDaoForDML clickaTellSMSInboundDaoForDML = (ClickaTellSMSInboundDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CLICKATELLSMSINBOUND_DAO_FOR_DML);
			
			ClickaTellSMSInbound clickaTellSMSInbound =  new ClickaTellSMSInbound();
			clickaTellSMSInbound.setMoFrom(sourceNumber);
			clickaTellSMSInbound.setMoTo(receivedNumber);
			clickaTellSMSInbound.setOrgId( user != null ? user.getUserOrganization().getUserOrgId() : null);
			clickaTellSMSInbound.setText(content);
			clickaTellSMSInbound.setAutoResponse(autoResponse);
			clickaTellSMSInbound.setTimeStamp(receivedTime);
			clickaTellSMSInbound.setUsedKeyWords(usedKeyword);
			clickaTellSMSInbound.setDeliveryStatus(Constants.SMS_SENT_STATUS_STATUS_PENDING);
			//clickaTellSMSInboundDao.saveOrUpdate(clickaTellSMSInbound);
			clickaTellSMSInboundDaoForDML.saveOrUpdate(clickaTellSMSInbound);
			return clickaTellSMSInbound;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("Problem while creating the inbound entry");
			
		}
		
	}
	
	
	public OrgSMSkeywords findOrgSMSKeyWordFromContent(String msgContent, String receivedNumber ) throws BaseServiceException {
		
		OrgSMSkeywordsDao orgSMSkeywordsDao = null;
		logger.debug("=========in smskeyword=====");
		
		try {
			orgSMSkeywordsDao = (OrgSMSkeywordsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGSMSKEYWORDS_DAO);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("No dao(s) found in the context");
		}
		
		List<OrgSMSkeywords> keywordsList =  orgSMSkeywordsDao.findAllByReceivedNumber(receivedNumber);
		
		if(keywordsList == null) {
			logger.debug("=========in keywordsList is NILL=====");
			throw new BaseServiceException("No keywords find for this number "+receivedNumber);
		}
		
		//List<String> newKeywordsList = new ArrayList<String>();
		Map<String, OrgSMSkeywords> keywordsMap = new HashMap<String, OrgSMSkeywords>();
		msgContent = msgContent.toLowerCase();
		for (OrgSMSkeywords orgSMSkeywords : keywordsList) {
			
			String keyword = orgSMSkeywords.getKeyword().toLowerCase();
			if(msgContent.startsWith(keyword) ) {
				//newKeywordsList.add(keyword);
				keywordsMap.put(keyword, orgSMSkeywords);
			}
			
			
		}//
		logger.debug("=========finalKeyword====="+keywordsMap);
		String finalKeyword = null;
		Set<String> keySet = keywordsMap.keySet();
        for (String kWord : keySet) {
        	String[] strArr = msgContent.split(kWord);
        	//logger.debug("=========finalKeyword====="+strArr[0]);
        	if(strArr.length > 0 && strArr[1].startsWith(" ")) {
        		String[] finalKeywordArr = kWord.split(" ");
                if(finalKeywordArr.length == keySet.size()){
                	finalKeyword = kWord;
                    break;
                }
        		
        	}else if(kWord.equalsIgnoreCase(msgContent)) {
        		 //logger.debug("=========finalKeyword====="+msgContent+"  "+kWord);
        		finalKeyword = kWord; 
        	}
        	else continue;
        	
        	
		}
        logger.debug("=========finalKeyword====="+finalKeyword);
		return (OrgSMSkeywords) (finalKeyword == null ? finalKeyword : keywordsMap.get(finalKeyword));
		
	}
	
	
	public SMSSettings findSettingsKeyWordFromContent(String msgContent, String receivedNumber ) throws BaseServiceException	{
		
		SMSSettingsDao smsSettingsDao = null;
		logger.debug("=========in smssettings=====");
		
		try {
			smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("No dao(s) found in the context");
		}
		
		List<SMSSettings> settingsKeywordsList =  smsSettingsDao.findAllByReceivedNumber(receivedNumber);
		
		if(settingsKeywordsList == null) {
			logger.debug("=========in settingsKeywordsList is NILL=====");
			return null;
		}
		
		Map<String, SMSSettings> keywordsMap = new HashMap<String, SMSSettings>();
		msgContent = msgContent.toLowerCase();
		for (SMSSettings settingskeywords : settingsKeywordsList) {
			
			String keyword = settingskeywords.getKeyword().toLowerCase();
			//String optOutKeyWord = settingskeywords.getOptoutKeyword();
			if(msgContent.startsWith(keyword)  ) {
				//newKeywordsList.add(keyword);
				keywordsMap.put(keyword, settingskeywords);
			}
			
			
		}
		String finalKeyword = null;
		Set<String> keySet = keywordsMap.keySet();
        for (String kWord : keySet) {
        	String[] strArr = msgContent.split(kWord);
        	if(strArr.length > 0 && strArr[1].startsWith(" ")) {
        		String[] finalKeywordArr = kWord.split(" ");
                if(finalKeywordArr.length == keySet.size()){
                	finalKeyword = kWord;
                    break;
                }
        		
        	}else if(kWord.equalsIgnoreCase(msgContent)) finalKeyword = kWord; 
        	else continue;
        	
        	
		}
        logger.debug("=========finalKeyword====="+finalKeyword);
        return (SMSSettings) (finalKeyword == null ? finalKeyword : keywordsMap.get(finalKeyword));
		
	}
	
	
	public void sendAutoResponse() {
		
		
		
		
		
	}
	
	public void processMobileOptin(SMSSettings smsSettings, String mobileNumber, String source) throws BaseServiceException{
		
		try {
			ContactsDao contactsDao = null;
			ContactsDaoForDML contactsDaoForDML = null;
			MailingListDao mailingListDao = null;
			MailingListDaoForDML mailingListDaoForDML = null;
			SMSSuppressedContactsDao smsSuppressedContactsDao = null;
			SMSSuppressedContactsDaoForDML smsSuppressedContactsDaoForDML = null;

			try {
				
				smsSuppressedContactsDao = (SMSSuppressedContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_SUPPRESSED_CONTACTS_DAO);
				smsSuppressedContactsDaoForDML = (SMSSuppressedContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_SUPPRESSED_CONTACTS_DAO_FOR_DML);
				contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
				contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
				mailingListDao = (MailingListDao)ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
				mailingListDaoForDML = (MailingListDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.MAILINGLIST_DAO_FOR_DML);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new BaseServiceException("No dao(s) found in the context with id ");
			}
			
			if(smsSettings.getListId() == null) new BaseServiceException("No List is configured sms settings optin"
					+ " setup for smssettings record "+smsSettings.getSetupId().longValue());
			
			
			try {
				
				smsSuppressedContactsDao = (SMSSuppressedContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_SUPPRESSED_CONTACTS_DAO);
				smsSuppressedContactsDaoForDML = (SMSSuppressedContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_SUPPRESSED_CONTACTS_DAO_FOR_DML);
				contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
				mailingListDao = (MailingListDao)ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
				mailingListDaoForDML = (MailingListDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.MAILINGLIST_DAO_FOR_DML);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new BaseServiceException("No dao(s) found in the context with id ");
			}
			
			
			Users user = smsSettings.getUserId();
			Set<MailingList> mlSet = null;
			//Users user = smsSettings.getUserId();
			Contacts contact = contactsDao.findMobileContactByUser(mobileNumber, user);
			MailingList mailingList = mailingListDao.findById(smsSettings.getListId());
			Calendar now = Calendar.getInstance();
			boolean isNewContact = false;
			if(contact == null) {
				
				contact = new Contacts();
				contact.setMobilePhone(mobileNumber);
				contact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
				contact.setEmailStatus(Constants.CONT_STATUS_INVALID_EMAIL);
				contact.setUsers(user);
				contact.setCreatedDate(now);
				contact.setOptin(new Byte((byte) 0));
				contact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_MOBILE);
				mlSet = new HashSet<MailingList>();
				
				if(mailingList != null) {
					mlSet.add(mailingList);
				}
				
				long mlsBit = Utility.getMlsBit(mlSet);
				contact.setMlBits(mlsBit);
				
				mailingList.setListSize(mailingList.getListSize() + 1);
				mailingListDaoForDML.saveOrUpdate(mailingList);
				
				isNewContact = true;
				
			}
			else {
				
				if(user.getUserId().longValue() == contact.getUsers().getUserId().longValue() && (contact.getMlBits().longValue() & mailingList.getMlBit().longValue() ) <= 0) {
					
					contact.setMlBits(contact.getMlBits().longValue()|mailingList.getMlBit().longValue());
					mailingList.setLastModifiedDate(new Date());
					mailingListDaoForDML.saveOrUpdate(mailingList);
				}
				
				
			}//else
			
			contact.setLastSMSDate(null);
			contact.setMobileOptin(true);
			contact.setMobileOptinDate(now);
			contact.setMobileOptinSource(source);
			contactsDaoForDML.saveOrUpdate(contact);
			
			//contactsDao.updatemobileStatus(mobileNumber, Constants.CON_MOBILE_STATUS_ACTIVE, user );
			contactsDaoForDML.updateMobileOptinStatus(mobileNumber, Constants.CON_MOBILE_STATUS_ACTIVE, user, source );
			//smsSuppressedContactsDao.deleteFromSuppressedContacts(user, mobileNumber);
			smsSuppressedContactsDaoForDML.deleteFromSuppressedContacts(user, mobileNumber);
			
			//check for the trigger
			if(isNewContact) {
				EventTriggerDao eventTriggerDao = (EventTriggerDao)ServiceLocator.getInstance().getDAOByName("eventTriggerDao");
				
				List<EventTrigger> contactTypeeventTriggers = eventTriggerDao.findAllUserAutoRespondTriggers(user.getUserId().longValue(),
						(Constants.ET_TYPE_ON_CONTACT_OPTIN_MEDIUM+Constants.DELIMETER_COMMA+Constants.ET_TYPE_ON_CONTACT_ADDED));
				
				boolean isEnableContactEvent = (contactTypeeventTriggers != null && contactTypeeventTriggers.size() > 0);
				Long startId = contact.getContactId();
				Long endId = contact.getContactId();
				if(isEnableContactEvent && startId != null && endId	!= null) {
					EventTriggerEventsObservable eventTriggerEventsObservable = (EventTriggerEventsObservable)ServiceLocator.getInstance().getBeanByName("eventTriggerEventsObservable");
					eventTriggerEventsObservable.notifyToObserver(contactTypeeventTriggers, startId, endId, user.getUserId(), Constants.POS_MAPPING_TYPE_CONTACTS);
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("Exception in perform mobile optin");
		}

		
	}
	
	public void processOptOut(String mobileNUmber, SMSSettings smsSettings, Calendar receivedTime) throws BaseServiceException{
		
		
		ContactsDao contactsDao = null;
		ContactsDaoForDML contactsDaoForDML = null;
		
		Users user = smsSettings.getUserId();
		
		try {
			contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
			contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("No dao(s) found in the context with id ");
		}
		
		Contacts contact = contactsDao.findMobileContactByUser(mobileNUmber, user);
		if(contact != null) {
			
			/*contact.setMobileStatus(SMSStatusCodes.CLICKATELL_STATUS_OPTED_OUT);
			contactsDao.saveOrUpdate(contact);*/
			//contactsDao.updatemobileStatus(mobileNUmber, Constants.CON_MOBILE_STATUS_OPTED_OUT, user );
			addToSuppressedContacts(user, mobileNUmber, receivedTime, Constants.CON_MOBILE_STATUS_OPTED_OUT, "Opted-out through keyword");
			contactsDaoForDML.updateMobileOptoutStatus(mobileNUmber, user );
			
		}//if
		
		
		
	
		
		
	}
	public ClickaTellSMSInbound findByMsgID(String msgID) throws BaseServiceException{
		
		ClickaTellSMSInboundDao clickaTellSMSInboundDao = null;
		try {
			clickaTellSMSInboundDao = (ClickaTellSMSInboundDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CLICKATELLSMSINBOUND_DAO);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("No dao(s) found in the context with id ");
		}
		
		return clickaTellSMSInboundDao.findBy(msgID);
		
		
	}
	
	public void addToSuppressedContacts(Users user, String mobile, 
			Calendar cal, String type, String reason) throws BaseServiceException{
		SMSSuppressedContactsDao smsSuppressedContactsDao = null;
		SMSSuppressedContactsDaoForDML smsSuppressedContactsDaoForDML = null;
		ContactsDao contactsDao = null;
		ContactsDaoForDML contactsDaoForDML = null;
		try {
			ServiceLocator locator = ServiceLocator.getInstance();
			smsSuppressedContactsDao = (SMSSuppressedContactsDao)locator.getDAOByName(OCConstants.SMS_SUPPRESSED_CONTACTS_DAO);
			smsSuppressedContactsDaoForDML = (SMSSuppressedContactsDaoForDML)locator.getDAOForDMLByName(OCConstants.SMS_SUPPRESSED_CONTACTS_DAO_FOR_DML);
			contactsDao = (ContactsDao)locator.getDAOByName(OCConstants.CONTACTS_DAO);
			contactsDaoForDML = (ContactsDaoForDML)locator.getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("No dao(s) found in the context with id ");
		}
		SMSSuppressedContacts suppressedContact = null;
		Long userId = null; 
		
		if(user != null) {
			
			userId = user.getUserId();
		}
		List<SMSSuppressedContacts> retList =  smsSuppressedContactsDao.searchContactsById(userId, mobile);
		if(retList != null && retList.size() == 1) {
			suppressedContact = retList.get(0);
			//suppressedContact.setSuppressedtime(Calendar.getInstance());
			
		}
		else {
			
			suppressedContact = new SMSSuppressedContacts();
			suppressedContact.setUser(user);
			suppressedContact.setMobile(mobile);
			suppressedContact.setSuppressedtime(Calendar.getInstance());
			
		}
		//Users users = usersDao.find(userId); 
		suppressedContact.setType(type);
		suppressedContact.setReason(reason == null ? type :reason);
		try{
			
			//smsSuppressedContactsDao.saveOrUpdate(suppressedContact);
			smsSuppressedContactsDaoForDML.saveOrUpdate(suppressedContact);
		}catch (Exception e) {
			if(logger.isErrorEnabled()) logger.error("**Exception : while adding contact to suppress Contacts list :", e);
		}
			
		if(user != null)contactsDaoForDML.updatemobileStatus(mobile, type, user);
	}
	
	public String isAKeyword(String msg) {
		
		
		Set<String> GLOBAL_OPTOUT_KEYWORDS = new HashSet<String>(); 
		
		String globalKeywordList = PropertyUtil.getPropertyValueFromDB(OCConstants.SMS_GLOBALKEYWAORDS_CLICKATEL);
		String[] keywords = globalKeywordList.split(Constants.ADDR_COL_DELIMETER);
		
		//GLOBAL_OPTOUT_KEYWORDS.addAll(OCConstants.GLOBAL_OPTOUT_KEYWORDS);
		//GLOBAL_OPTOUT_KEYWORDS.addAll(keywords);
        List<String> keywordList = new ArrayList<String>();
        msg = msg.toLowerCase();
        
        for(String keyword : keywords){
        	keyword = keyword.toLowerCase();
            if(msg.startsWith(keyword)){
                keywordList.add(keyword);
                //logger.debug("Keyword exist...u" + strArray[i]);
                String[] splitArray = msg.split(keyword);
                
                
                for(int j = 0; j < splitArray.length; j++){
                    logger.debug(splitArray[j] + " Invalid");
                }
            }
        }
        String srtKeyword = null;
        for (String kywd : keywordList) {
        	String[] strArr = msg.split(kywd);
        	if(strArr.length > 0 && strArr[1].startsWith(" ")) {
        		//logger.debug("fdfdfjkfjdkfjdfjkdl");
        		String[] finalKeyword = kywd.split(" ");
                if(finalKeyword.length == keywordList.size()){
                     srtKeyword = kywd;
                     logger.debug("======="+srtKeyword);		
                   return srtKeyword;
                }
        		
        	}else if(kywd.equalsIgnoreCase(msg)){
        		srtKeyword = kywd;
        		logger.debug("======="+srtKeyword);	
        		return srtKeyword;
        	}
        	else continue;
        	
        	
		}
        logger.debug("======="+srtKeyword);	
        return srtKeyword;
       
		
		
	}
	
	public static void main(String[] args) {
		String strArray[] = {  "START","SAMPLE", "START TRMODE AMERT", "START TRMODE"};
        List<String> keywordList = new ArrayList<String>();
        String msg = "START TRMODE AMERT uy begin";
        for(int i = 0; i < strArray.length; i++){
            if(msg.startsWith(strArray[i])){
                keywordList.add(strArray[i]);
                logger.debug("Keyword exist...u" + strArray[i]);
                String[] splitArray = msg.split(strArray[i]);
                
                
                for(int j = 0; j < splitArray.length; j++){
                    logger.debug(splitArray[j] + " Invalid");
                }
            }
        }
        String srtKeyword = null;
        for (String string : keywordList) {
        	 logger.debug(string);
        	String[] strArr = msg.split(string);
        	if(strArr.length > 0 && strArr[1].startsWith(" ")) {
        		logger.debug("fdfdfjkfjdkfjdfjkdl");
        		String[] finalKeyword = string.split(" ");
                if(finalKeyword.length == keywordList.size()){
                     srtKeyword = string;
                    logger.debug(" sdjdsdjs "+srtKeyword);
                    break;
                }
        		
        	}else if(string.equalsIgnoreCase(msg)) srtKeyword = string; else continue;
        	
        	
		}
       logger.debug(srtKeyword);
        /*
        String srtKeyword = "";

        if(keywordList.size() > 1){
            logger.debug("Matched more than once");

            for(int k = 0; k < keywordList.size(); k++){
                String dsfdKeyword = keywordList.get(k);
                String[] finalKeyword = dsfdKeyword.split(" ");
                if(finalKeyword.length == keywordList.size()){
                    srtKeyword = keywordList.get(k);
                    logger.debug(srtKeyword);
                    break;
                }
            }
        } */
	}
	public void processInboundDLR(SMSHTTPDLRRequestObject SMSHTTPDLRRequestObj)  throws  BaseServiceException{
		
		ClickaTellSMSInbound inboundObj = findByMsgID(SMSHTTPDLRRequestObj.getMessageID());
		
		if(inboundObj == null) throw new BaseServiceException("no record exists with msgid "+SMSHTTPDLRRequestObj.getMessageID());
		
		//String status = getDLRStatus(SMSHTTPDLRRequestObj);
		
		inboundObj.setDeliveryStatus(getDLRStatus(SMSHTTPDLRRequestObj));
		inboundObj.setDeliveredTime(SMSHTTPDLRRequestObj.getDeliveredTime());
		
		ClickaTellSMSInboundDao clickaTellSMSInboundDao = null;
		ClickaTellSMSInboundDaoForDML clickaTellSMSInboundDaoForDML = null;
		try {
			clickaTellSMSInboundDao = (ClickaTellSMSInboundDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CLICKATELLSMSINBOUND_DAO);
			clickaTellSMSInboundDaoForDML = (ClickaTellSMSInboundDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CLICKATELLSMSINBOUND_DAO_FOR_DML);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("No dao(s) found in the context with id ");
		}
		//clickaTellSMSInboundDao.saveOrUpdate(inboundObj);
	
		clickaTellSMSInboundDaoForDML.saveOrUpdate(inboundObj);
		
	}
	
	public String getDLRStatus(SMSHTTPDLRRequestObject SMSHTTPDLRRequestObj) {
		String status = SMSHTTPDLRRequestObj.getStatus();
		String userSMSTool = SMSHTTPDLRRequestObj.getUserSMSTool();
		
		if(userSMSTool.equals(Constants.USER_SMSTOOL_UNICEL)) {
			
			return UnicelSMSGateway.unicellDlrMap.get(status);
			
		}else if(userSMSTool.equals(Constants.USER_SMSTOOL_CLICKATELL)) {
			
			return SMSStatusCodes.clickaTellStatusCodesMap.get(status);
			
		}
		
		return null;
		
		
	}
	
	public String setDLRBounceStatus(SMSHTTPDLRRequestObject SMSHTTPDLRRequestObj, 
			SMSCampaignSent smsCampaignSent) throws BaseServiceException {
		
		String status = SMSHTTPDLRRequestObj.getStatus();
		String userSMSTool = SMSHTTPDLRRequestObj.getUserSMSTool();
		
		if(userSMSTool.equals(Constants.USER_SMSTOOL_CLICKATELL)) {
			
			String type = SMSStatusCodes.clickaTellStatusCodesMap.get(status);
			
			if(SMSStatusCodes.BouncedSet.contains(type)) {
				
				
				status = SMSStatusCodes.CLICKATELL_STATUS_BOUNCED;
				
				addToBounces(smsCampaignSent.getSmsCampaignReport(), 
						smsCampaignSent.getMobileNumber(), smsCampaignSent, type, 
						SMSStatusCodes.clickaTellStatusMessagesMap.get(SMSHTTPDLRRequestObj.getStatus()));
				
				
				if(SMSStatusCodes.SuppressedSet.contains(SMSStatusCodes.clickaTellStatusCodesMap.get(status))){
					
					//Users user = smsCampaignReport.getUser();
					
					addToSuppressedContacts(smsCampaignSent.getSmsCampaignReport().getUser(), 
							smsCampaignSent.getMobileNumber(), SMSHTTPDLRRequestObj.getDeliveredTime(), 
							type, SMSStatusCodes.clickaTellStatusMessagesMap.get(SMSHTTPDLRRequestObj.getStatus()) );
					
					
				}
				/*smsCampaignSent.setStatus(SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);
				
				
				SMSBounces newBounce= new SMSBounces();
				newBounce.setCrId(smsCampaignSent.getSmsCampaignReport().getSmsCrId());
				newBounce.setSentId(smsCampaignSent);
				newBounce.setMessage(status);
				newBounce.setMobile(smsCampaignSent.getMobileNumber());
				newBounce.setCategory(status);
				newBounce.setBouncedDate(Calendar.getInstance());*/
			}else{
				
				
				 status = SMSStatusCodes.clickaTellStatusCodesMap.get(status);
				 
				SMSBouncesDao smsBouncesDao = null;
				SMSBouncesDaoForDML smsBouncesDaoForDML  = null;

				SMSCampaignReportDao smsCampaignReportDao = null;
				SMSCampaignReportDaoForDML smsCampaignReportDaoForDML = null;
				try {
					smsBouncesDao = (SMSBouncesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSBOUNCES_DAO);
					smsBouncesDaoForDML = (SMSBouncesDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMSBOUNCES_DAO_ForDML);

					smsCampaignReportDao = (SMSCampaignReportDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_CAMPAIGNREPORT_DAO);
					smsCampaignReportDaoForDML = (SMSCampaignReportDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_CAMPAIGNREPORT_DAO_FOR_DML);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new BaseServiceException("No dao found smsBouncesDao");
				}
				/*
				smsBouncesDao.deleteByMobile(smsCampaignSent.getSmsCampaignReport().getSmsCrId(),
						smsCampaignSent.getMobileNumber(), smsCampaignSent.getSentId());
						*/
				smsBouncesDaoForDML.deleteByMobile(smsCampaignSent.getSmsCampaignReport().getSmsCrId(),
						smsCampaignSent.getMobileNumber(), smsCampaignSent.getSentId());
				smsCampaignReportDaoForDML.updateBounceReport(smsCampaignSent.getSmsCampaignReport().getSmsCrId());
				
				
			}
			
		}
		
		return status;
	}
	
	public void processCampaignDLR(SMSHTTPDLRRequestObject SMSHTTPDLRRequestObj) throws  BaseServiceException {
		
		String cliMSgId = SMSHTTPDLRRequestObj.getClientMsgId();
		
		String actualSentId = cliMSgId.contains(Constants.ADDR_COL_DELIMETER) ? 
				cliMSgId.split(Constants.ADDR_COL_DELIMETER)[1] : cliMSgId ;
		Long cliMsgIdLong = null;
		String mobile = SMSHTTPDLRRequestObj.getMobileNumber();
		try {
			cliMsgIdLong = Long.parseLong(actualSentId);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("cannot process dlr for this msgID");
		}
		
		SMSCampaignSentDao smsCampaignSentDao = null;
		SMSCampaignSentDaoForDML smsCampaignSentDaoForDML  = null;

		try {
			smsCampaignSentDao = (SMSCampaignSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_CAMPAIGNSENT_DAO);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("Exception while fetching smsCampaignSentDao");
		}
			
		SMSCampaignSent smsCampaignSent = smsCampaignSentDao.findByReqIdAndMobile(cliMsgIdLong, mobile);
		if(smsCampaignSent == null ) throw new BaseServiceException("no smsCampaignSent "
				+ "found with id , mobile"+cliMsgIdLong.longValue()+", "+mobile);
		
		String status = getDLRStatus(SMSHTTPDLRRequestObj);
		
		String sentStatus = status;
		
		sentStatus = setDLRBounceStatus(SMSHTTPDLRRequestObj, smsCampaignSent);
		
		//int updateCount = smsCampaignSentDao.updateStatusByCliMsgId(sentStatus, mobile, cliMsgIdLong, SMSHTTPDLRRequestObj.getMessageID());
		int updateCount = smsCampaignSentDaoForDML.updateStatusByCliMsgId(sentStatus, mobile, cliMsgIdLong, SMSHTTPDLRRequestObj.getMessageID());

		
	}
	
	public void addToBounces(SMSCampaignReport smsCampaignReport, String mobile,
			SMSCampaignSent smsCampaignSent, String status, String msg ) throws BaseServiceException {
		
		if(smsCampaignSent == null || smsCampaignReport == null) {
			
			if(logger.isErrorEnabled()) logger.error("** got source objects as null");
			return;
			
		}
		
		//need to find the existing object with the same given mobile if any..
		Long crId = smsCampaignReport.getSmsCrId(); 
		
		SMSBouncesDao smsBouncesDao = null;
		SMSBouncesDaoForDML smsBouncesDaoForDML = null;

		SMSCampaignReportDao smsCampaignReportDao = null;
		SMSCampaignReportDaoForDML smsCampaignReportDaoForDML = null;
		
		try {
			smsBouncesDao = (SMSBouncesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSBOUNCES_DAO);
			smsBouncesDaoForDML = (SMSBouncesDaoForDML )ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMSBOUNCES_DAO_ForDML);
			smsCampaignReportDao = (SMSCampaignReportDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_CAMPAIGNREPORT_DAO);
			smsCampaignReportDaoForDML = (SMSCampaignReportDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_CAMPAIGNREPORT_DAO_FOR_DML);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("No dao found smsBouncesDao");
		}
		
		SMSBounces newBounce = smsBouncesDao.findBymobile(crId, mobile);
		
		if(newBounce == null) {
			
			newBounce = new SMSBounces();
			newBounce.setCrId(crId);
			newBounce.setSentId(smsCampaignSent);
			
			
		}
		
		newBounce.setMessage(msg);
		newBounce.setMobile(mobile);
		newBounce.setCategory(status);
		newBounce.setBouncedDate(Calendar.getInstance());
		//smsBouncesDao.saveOrUpdate(newBounce);
		smsBouncesDaoForDML.saveOrUpdate(newBounce);

		//smsCampaignReportDao.updateBounceReport(smsCampaignReport.getSmsCrId());
		if(logger.isDebugEnabled()) logger.debug("updateing report bounce count"+smsCampaignReport.getBounces());
		//smsCampaignReportDao.updateBounceReport(smsCampaignReport.getSmsCrId());
		smsCampaignReportDaoForDML.updateBounceReport(smsCampaignReport.getSmsCrId());
		
	}//addToBounces
	
	
	public boolean checkIsHelp(String msg){
		
		String keyword = PropertyUtil.getPropertyValueFromDB(OCConstants.SMS_KEYWORD_HELP_CLICKATEL);
		if(keyword != null && keyword.equalsIgnoreCase(msg)) return true;
		
		return false;
	}
}

