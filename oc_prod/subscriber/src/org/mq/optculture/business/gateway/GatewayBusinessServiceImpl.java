package org.mq.optculture.business.gateway;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.mq.marketer.campaign.beans.ClickaTellSMSInbound;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.OrgSMSkeywords;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.UserSMSGateway;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.service.CaptiwayToSMSApiGateway;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ClickaTellSMSInboundDao;
import org.mq.marketer.campaign.dao.ClickaTellSMSInboundDaoForDML;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MailingListDaoForDML;
import org.mq.marketer.campaign.dao.OCSMSGatewayDao;
import org.mq.marketer.campaign.dao.OrgSMSkeywordsDao;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDao;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDaoForDML;
import org.mq.marketer.campaign.dao.UserSMSGatewayDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.GatewayRequestProcessHelper;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.SMSInBoundRequestObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zkplus.spring.SpringUtil;
/**
 * 
 * @author proumyaa
 *incomplete class must not use
 */
public class GatewayBusinessServiceImpl implements GatewayBusinessService{

	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		
		String action = baseRequestObject.getAction();
		
		if(action == null){
			throw new BaseServiceException("No action/type found");
		}
		
		
		SMSInBoundRequestObject smsInBoundRequestObject = (SMSInBoundRequestObject)baseRequestObject;
		BaseResponseObject baseResponseObject = null;//response to return 
				
		if(action.equals(OCConstants.REQUEST_PARAM_TYPE_VALUE_SMS)) {
			
			if(smsInBoundRequestObject.getMsgContent() == null) throw new BaseServiceException("No message content found");
			baseResponseObject = processSMSInBoundRequest(smsInBoundRequestObject);
			
			
			
		}else if(action.equals(OCConstants.REQUEST_PARAM_TYPE_VALUE_MISSEDCALL)) {
			
			baseResponseObject = processSMSMissedCallOptinRequest(smsInBoundRequestObject);
			
		}
		
		
		return baseResponseObject;
	}
	
	@Override
	public BaseResponseObject processSMSInBoundRequest(SMSInBoundRequestObject smsInBoundRequestObject)
			throws BaseServiceException {
		
		BaseResponseObject baseResponseObject = new BaseResponseObject();
		
		
		String content = smsInBoundRequestObject.getMsgContent(); 
		//need to parse and get the word(s) 
		String timeStamp = smsInBoundRequestObject.getReceivedTimeStr();
		String receivedNumber = smsInBoundRequestObject.getDestination();
		String sourceNumber = smsInBoundRequestObject.getSource();
		
		if(receivedNumber == null || content == null || sourceNumber == null || timeStamp == null) 
			throw new BaseServiceException("type is "+smsInBoundRequestObject.getAction()+" but no required paramters found");
		
		Calendar receivedTime = null;
		try {
			SimpleDateFormat format = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
			
			receivedTime = Calendar.getInstance();
			receivedTime.setTime(format.parse(timeStamp));
			
		} catch (Exception e) {
			
			throw new BaseServiceException(" Unable to convert the time value into time "+timeStamp);
		}
		
		
		GatewayRequestProcessHelper gatewayRequestProcessHelper =  new GatewayRequestProcessHelper();
		
		Object keyWord = gatewayRequestProcessHelper.getKeyWordFromMsgContent(smsInBoundRequestObject.getMsgContent(), receivedNumber);//getFinalKeywordFrom(keywordsList);
		
		
		
		
		if(keyWord == null) {
			
			//send an error auto response
			String wrongKeywordResponse = PropertyUtil.getPropertyValueFromDB(Constants.SMS_WRONG_KEYWORD_RESPONSE);
			if(wrongKeywordResponse != null && !wrongKeywordResponse.trim().isEmpty()) {
				
				//TODO
			}
			
		}else{
			Users user = null;
			String usedKeyword = null;
			String autoResponse = null;
			UserSMSGatewayDao userSMSGatewayDao;
			OCSMSGatewayDao OCSMSGatewayDao;
			try {
				userSMSGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
				OCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				throw new BaseServiceException("No id Found");
			}
			
			UserSMSGateway userSmsGateway = null;
			OCSMSGateway ocgateway = null;
			String senderId = null;
			
			if(keyWord instanceof SMSSettings) {
				
				SMSSettings SMSSettingsObj = (SMSSettings)keyWord;
				user = SMSSettingsObj.getUserId();
				usedKeyword = SMSSettingsObj.getKeyword();//TODO on SMS settings
				//find this keyword i optin or optout
				autoResponse = SMSSettingsObj.getAutoResponse();
				senderId = SMSSettingsObj.getSenderId();
				
			}else if(keyWord instanceof OrgSMSkeywords) {

				OrgSMSkeywords orgKeyWord = (OrgSMSkeywords)keyWord;
				user = orgKeyWord.getUser();
				usedKeyword = orgKeyWord.getKeyword();
				
				autoResponse = orgKeyWord.getAutoResponse();
				senderId = orgKeyWord.getSenderId();
				
				
			}
			 userSmsGateway = userSMSGatewayDao.findByUserId(user.getUserId(), SMSStatusCodes.defaultSMSTypeMap.get(user.getCountryType()));
				
			if(userSmsGateway == null) {
				
				throw new BaseServiceException("no default smstype available for user");
				
			}
			
			 ocgateway = OCSMSGatewayDao.findById(userSmsGateway.getGatewayId());
			if(ocgateway == null) {
				
				throw new BaseServiceException("no gateway is available ");
				
			}
			
			try {
				
				ClickaTellSMSInboundDao clickaTellSMSInboundDao = (ClickaTellSMSInboundDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CLICKATELLSMSINBOUND_DAO);
				ClickaTellSMSInboundDaoForDML clickaTellSMSInboundDaoForDML = (ClickaTellSMSInboundDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CLICKATELLSMSINBOUND_DAO_FOR_DML);
				
				ClickaTellSMSInbound clickaTellSMSInbound =  new ClickaTellSMSInbound();
				clickaTellSMSInbound.setMoFrom(sourceNumber);
				clickaTellSMSInbound.setMoTo(receivedNumber);
				clickaTellSMSInbound.setOrgId( user.getUserOrganization().getUserOrgId());
				clickaTellSMSInbound.setText(content);
				clickaTellSMSInbound.setTimeStamp(receivedTime);
				clickaTellSMSInbound.setUsedKeyWords(usedKeyword);
				//clickaTellSMSInboundDao.saveOrUpdate(clickaTellSMSInbound);
				clickaTellSMSInboundDaoForDML.saveOrUpdate(clickaTellSMSInbound);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new BaseServiceException("Problem while creating the inbound entry");
				
			}
			
			
			
			
			if(autoResponse != null && !autoResponse.trim().isEmpty()) {
				
				autoResponse = StringEscapeUtils.escapeHtml(autoResponse);
				ContactsDao contactsDao = null;
				
				if(SMSStatusCodes.optInMap.get(user.getCountryType())) {
					
					try {
						contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						throw new BaseServiceException("No id found");
					}
					String mobilePhone = sourceNumber;
					if(sourceNumber.startsWith(""+user.getCountryCarrier())) {
						
						mobilePhone = sourceNumber.substring((""+user.getCountryCarrier()).length());
					}
					boolean isIndia = user!=null ? user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA) : false;
					List<Contacts> mobileOptedContacts = contactsDao.findByQuery("FROM Contacts WHERE users="+user.getUserId()+
							" AND mobilePhone " + (!isIndia ? " LIKE '%":"='")+mobilePhone+"'");
					
					
					if(mobileOptedContacts != null){
						
						Contacts mobileOptinContact = null;
						for (Contacts contact : mobileOptedContacts) {
							
							if(contact.isMobileOptin()) mobileOptinContact = contact;
							
							
						}
						if(mobileOptinContact != null) {
							
							userSmsGateway = userSMSGatewayDao.findByUserId(user.getUserId(), SMSStatusCodes.optInTypeMap.get(user.getCountryType()));
								
							if(userSmsGateway == null) {
								
								throw new BaseServiceException("no optin smstype available for user");
								
							}
							
							 ocgateway = OCSMSGatewayDao.findById(userSmsGateway.getGatewayId());
							if(ocgateway == null) {
								
								throw new BaseServiceException("no gateway is available ");
								
							}
							
						}
						//TODO send it thru the opt-in account
						
					}
					
					
				}
				//CaptiwayToSMSApiGateway captiwayToSMSApiGateway = (CaptiwayToSMSApiGateway)ServiceLocator.getInstance().getDAOByName(OCConstants.CLICKATELLSMSINBOUND_DAO);
				
			}//if
			
		
			
		}
		
		return baseResponseObject;
	}
	
	
	
	
	@Override
	public BaseResponseObject processSMSMissedCallOptinRequest(SMSInBoundRequestObject smsInBoundRequestObject)
			throws BaseServiceException {

		BaseResponseObject baseResponseObject = new BaseResponseObject();
		//need to parse and get the word(s) 
		
		String receivedNumber = smsInBoundRequestObject.getDestination();
		SMSSettingsDao smsSettigsDao = null;
		ContactsDaoForDML contactsDaoForDML = null;
		ContactsDao contactsDao = null;
		MailingListDao mailingListDao = null;
		MailingListDaoForDML mailingListDaoForDML = null;
		SMSSuppressedContactsDao smsSuppressedContactsDao = null;
		SMSSuppressedContactsDaoForDML smsSuppressedContactsDaoForDML = null;
		
		try {
			
			smsSettigsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("No dao found in the context with id "+OCConstants.SMSSETTINGS_DAO);
		}
		
		if(smsSettigsDao == null) new BaseServiceException("No dao found in the context with id "+OCConstants.SMSSETTINGS_DAO);
		
		SMSSettings smsSettings = smsSettigsDao.findByMissedCallNumber(receivedNumber);
		
		if(smsSettings == null) new BaseServiceException("No record found in sms settings optin setup for this missedcall number "+receivedNumber);
		
		if(smsSettings.getListId() == null) new BaseServiceException("No List is configured sms settings optin"
				+ " setup for smssettings record "+smsSettings.getSetupId().longValue());
		
		
		try {
			
			//smsSuppressedContactsDao = (SMSSuppressedContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			smsSuppressedContactsDao = (SMSSuppressedContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_SUPPRESSED_CONTACTS_DAO);
			smsSuppressedContactsDaoForDML = (SMSSuppressedContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_SUPPRESSED_CONTACTS_DAO_FOR_DML);
			contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
			mailingListDao = (MailingListDao)ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
			mailingListDaoForDML = (MailingListDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.MAILINGLIST_DAO_FOR_DML);
			contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
			mailingListDao = (MailingListDao)ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("No dao(s) found in the context with id ");
		}
		
		
		Users user = smsSettings.getUserId();
		Set<MailingList> mlSet = null;
		String mobileNumber = smsInBoundRequestObject.getSource();
		//Users user = smsSettings.getUserId();
		Contacts contact = contactsDao.findMobileContactByUser(mobileNumber, user);
		MailingList mailingList = mailingListDao.findById(smsSettings.getListId());
		Calendar now = Calendar.getInstance();
		
		if(contact == null) {
			
			contact = new Contacts();
			contact.setMobilePhone(mobileNumber);
			contact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
			contact.setEmailStatus(Constants.CONT_STATUS_INVALID_EMAIL);
			contact.setUsers(user);
			contact.setCreatedDate(now);
			contact.setModifiedDate(now);
			contact.setOptin(new Byte((byte) 0));
			mlSet = new HashSet<MailingList>();
			
			if(mailingList != null) {
				mlSet.add(mailingList);
			}
			
			long mlsBit = Utility.getMlsBit(mlSet);
			contact.setMlBits(mlsBit);
			
			
			
		}
		else {
			
			if( (contact.getMlBits().longValue() & mailingList.getMlBit().longValue() ) <= 0) {
				
				contact.setMlBits(contact.getMlBits().longValue()|mailingList.getMlBit().longValue());
				mailingList.setLastModifiedDate(now);
				mailingListDaoForDML.saveOrUpdate(mailingList);
			}
			
			
		}//else
		
		contact.setLastSMSDate(null);
		contact.setMobileOptin(true);
		contactsDaoForDML.saveOrUpdate(contact);
		
		contactsDaoForDML.updatemobileStatus(mobileNumber, Constants.CON_MOBILE_STATUS_ACTIVE, user );
		//smsSuppressedContactsDao.deleteMblByUserId(user, mobileNumber);
		smsSuppressedContactsDaoForDML.deleteMblByUserId(user, mobileNumber);
		
		
		
			
		
	
		
		return baseResponseObject;
	}
	
}
