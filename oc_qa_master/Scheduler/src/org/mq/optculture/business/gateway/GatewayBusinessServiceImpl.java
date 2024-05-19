package org.mq.optculture.business.gateway;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.ClickaTellSMSInbound;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.OCSMSGateway;
import org.mq.captiway.scheduler.beans.OrgSMSkeywords;
import org.mq.captiway.scheduler.beans.SMSSettings;
import org.mq.captiway.scheduler.beans.UserSMSGateway;
import org.mq.captiway.scheduler.beans.UserSMSSenderId;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.ClickaTellSMSInboundDao;
import org.mq.captiway.scheduler.dao.ClickaTellSMSInboundDaoForDML;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.MailingListDao;
import org.mq.captiway.scheduler.dao.OCSMSGatewayDao;
import org.mq.captiway.scheduler.dao.OrgSMSkeywordsDao;
import org.mq.captiway.scheduler.dao.SMSSettingsDao;
import org.mq.captiway.scheduler.dao.SMSSuppressedContactsDao;
import org.mq.captiway.scheduler.dao.UserSMSGatewayDao;
import org.mq.captiway.scheduler.dao.UserSMSSenderIdDao;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.dao.UsersDaoForDML;
import org.mq.captiway.scheduler.services.CaptiwayToSMSApiGateway;
import org.mq.captiway.scheduler.services.UnicelSMSGateway;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.SMSStatusCodes;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.business.helper.GatewayRequestProcessHelper;
import org.mq.optculture.business.helper.SmsQueueHelper;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.SMSHTTPDLRRequestObject;
import org.mq.optculture.model.SMSInBoundRequestObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class GatewayBusinessServiceImpl implements GatewayBusinessService{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		
		String action = baseRequestObject.getAction();
		logger.debug("=======calling the service ======"+action);
		
		
		if(action == null){
			throw new BaseServiceException("No action/type found");
		}
		
		GatewayBusinessService gatewayBusinessService = (GatewayBusinessService)ServiceLocator.getInstance().getServiceByName(OCConstants.GATEWAY_BUSINESS_SERVICE);
		BaseResponseObject baseResponseObject = null;//response to return 
				
		if(action.equals(OCConstants.REQUEST_PARAM_TYPE_VALUE_SMS)) {
			
			SMSInBoundRequestObject smsInBoundRequestObject = (SMSInBoundRequestObject)baseRequestObject;
			if(smsInBoundRequestObject.getMsgContent() == null) throw new BaseServiceException("No message content found");
			baseResponseObject = gatewayBusinessService.processSMSInBoundRequest(smsInBoundRequestObject);
			
		}else if(action.equals(OCConstants.REQUEST_PARAM_TYPE_VALUE_MISSEDCALL)) {
			SMSInBoundRequestObject smsInBoundRequestObject = (SMSInBoundRequestObject)baseRequestObject;

			baseResponseObject = gatewayBusinessService.processSMSMissedCallOptinRequest(smsInBoundRequestObject);
			
		}else if(action.equals(OCConstants.REQUEST_PARAM_TYPE_VALUE_DLR)) {
			SMSHTTPDLRRequestObject SMSHTTPDLRRequestObj = (SMSHTTPDLRRequestObject)baseRequestObject;

			baseResponseObject = gatewayBusinessService.processDLRRequest(SMSHTTPDLRRequestObj);
			
		}
		
		
		return baseResponseObject;
	}
	
	@Override
	public BaseResponseObject processSMSInBoundRequest(SMSInBoundRequestObject smsInBoundRequestObject)
			throws BaseServiceException {
		
		BaseResponseObject baseResponseObject = new BaseResponseObject();
		
		logger.debug("=======calling the service ======");
		String content = smsInBoundRequestObject.getMsgContent(); 
		//need to parse and get the word(s) 
		//String timeStamp = smsInBoundRequestObject.getReceivedTimeStr();
		String receivedNumber = smsInBoundRequestObject.getDestination();
		String sourceNumber = smsInBoundRequestObject.getSource();
		
		if(receivedNumber == null || content == null || sourceNumber == null ) 
			throw new BaseServiceException("type is "+smsInBoundRequestObject.getAction()+" but no required paramters found");
		
		Calendar receivedTime = Calendar.getInstance();
		/*try {
			SimpleDateFormat format = new SimpleDateFormat(smsInBoundRequestObject.getTimeFormat());
			
			receivedTime.setTime(format.parse(timeStamp));
			
		} catch (Exception e) {
			
			logger.error(" Unable to convert the time value into time "+timeStamp);
		}*/
		
		
		GatewayRequestProcessHelper gatewayRequestProcessHelper =  new GatewayRequestProcessHelper();
		
		//get the keyword from content
		logger.debug("Content ..."+content+"\t..Received Number.."+receivedNumber);
		Object keyWord = gatewayRequestProcessHelper.getKeyWordFromMsgContent(content, receivedNumber);//getFinalKeywordFrom(keywordsList);
		logger.debug("keyWordkeyWord ............"+keyWord);
		if(keyWord == null ) {
			//logger.debug("====smsInBoundRequestObject.getFromCountry()==="+smsInBoundRequestObject.getFromCountry());
			boolean isCheckErrorResponse = true;
			if( SMSStatusCodes.checkGlobalOptOutMap.get(smsInBoundRequestObject.getFromCountry())){
				String keywordStr = gatewayRequestProcessHelper.isAKeyword(content);
				
				if(keywordStr != null ) {
					isCheckErrorResponse = false;
					boolean isHelp = gatewayRequestProcessHelper.checkIsHelp(keywordStr);
					
					String autoResponse = isHelp ? PropertyUtil.getPropertyValueFromDB(OCConstants.SMS_KEYWORD_MSG_HELP) 
								: PropertyUtil.getPropertyValueFromDB(OCConstants.SMS_KEYWORD_MSG_STOP);
					
					ClickaTellSMSInbound inbounObj = gatewayRequestProcessHelper.createInBoundObj(sourceNumber, 
							receivedNumber, null, content, receivedTime, keywordStr, autoResponse);
					
					if(!isHelp){
						gatewayRequestProcessHelper.addToSuppressedContacts(null, sourceNumber, receivedTime, 
							Constants.CON_MOBILE_STATUS_OPTED_OUT, "Opted-out through a Global Opt-out keyword");
					}
					String account = SMSStatusCodes.defaultAccountMap.get(smsInBoundRequestObject.getFromCountry());
					String[] accountArr = account.split(Constants.ADDR_COL_DELIMETER);
					CaptiwayToSMSApiGateway captiwayToSMSApiGateway = new CaptiwayToSMSApiGateway();
					try {
						logger.debug("1......"+accountArr[0], accountArr[1], accountArr[2], 
								autoResponse, sourceNumber, accountArr[3], (accountArr.length==5 ? accountArr[4] : null));
						captiwayToSMSApiGateway.sendErrorResponse(accountArr[0], accountArr[1], accountArr[2], 
								autoResponse, sourceNumber, accountArr[3], (accountArr.length==5 ? accountArr[4] : null));//(ocgateway, autoResponse, sourceNumber, senderId);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("exception whole sending error response ", e);
					}
				
					
				}
				
			}
			if(isCheckErrorResponse){
				//send an error auto response
				logger.debug("send an error auto response");
				String wrongKeywordResponse = PropertyUtil.getPropertyValueFromDB(Constants.SMS_WRONG_KEYWORD_RESPONSE);
				if(wrongKeywordResponse != null && !wrongKeywordResponse.trim().isEmpty()) {
					
					try {
						String account = SMSStatusCodes.defaultAccountMap.get(smsInBoundRequestObject.getFromCountry());
						String[] accountArr = account.split(Constants.ADDR_COL_DELIMETER);
						
						ClickaTellSMSInbound inbounObj = gatewayRequestProcessHelper.createInBoundObj(sourceNumber, 
								receivedNumber, null, content, receivedTime, "not found", wrongKeywordResponse);
						logger.info("2"+accountArr[0] +" accountArr[1]"+ accountArr[1]+"accountArr[2]"+ accountArr[2] 
								+"wrongKeywordResponse"+wrongKeywordResponse+"sourceNumber"+ sourceNumber+"accountArr[3]"+ accountArr[3]+"accountArr.length==5 ? accountArr[4] : null"+ (accountArr.length==5 ? accountArr[4] : null));
						CaptiwayToSMSApiGateway captiwayToSMSApiGateway = new CaptiwayToSMSApiGateway();
						captiwayToSMSApiGateway.sendErrorResponse(accountArr[0], accountArr[1], accountArr[2], 
								wrongKeywordResponse, sourceNumber, accountArr[3], (accountArr.length==5 ? accountArr[4] : null));//(ocgateway, autoResponse, sourceNumber, senderId);
					
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("exception whole sending error response ", e);
					}
					
				}
			}
			
		}else{
			Users user = null;
			String usedKeyword = null;
			String autoResponse = null;
			String senderId = null;
			Long contactId = null;
			CaptiwayToSMSApiGateway captiwayToSMSApiGateway = null;
			if(keyWord instanceof SMSSettings) {//optin/optout
				logger.debug("keyWord is an instance of SMSSettings");
				
				SMSSettings SMSSettingsObj = (SMSSettings)keyWord;
				user = SMSSettingsObj.getUserId();
				usedKeyword = SMSSettingsObj.getKeyword();//TODO on SMS settings
				
				autoResponse = (!SMSSettingsObj.getType().equals(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN)) ?
								SMSSettingsObj.getAutoResponse() : null;
								
				
				senderId = SMSSettingsObj.getSenderId();
				
				//find this keyword i optin or optout
				if(SMSSettingsObj.getType().equals(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN)){
					
					gatewayRequestProcessHelper.processMobileOptin(SMSSettingsObj, sourceNumber, OCConstants.MOBILE_OPTIN_SOURCE_KEYWORD);
					autoResponse =  SMSSettingsObj.isEnableWelcomeMessage() ? SMSSettingsObj.getWelcomeMessage() : null;	
					
				}else if(SMSSettingsObj.getType().equals(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT)) {
					
					gatewayRequestProcessHelper.processOptOut(sourceNumber, SMSSettingsObj, receivedTime);
					
				}else if(SMSSettingsObj.getType().equals(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP)) {
					
					
				}
				if(SMSStatusCodes.optOutFooterMap.get(user.getCountryType())){
					
					if(SMSSettingsObj.getMessageHeader() != null && autoResponse != null) autoResponse = SMSSettingsObj.getMessageHeader()+" "+autoResponse;
					
				}
				
			}else if(keyWord instanceof OrgSMSkeywords) {//promotional keyword

				OrgSMSkeywords orgKeyWord = (OrgSMSkeywords)keyWord;
				user = orgKeyWord.getUser();
				usedKeyword = orgKeyWord.getKeyword();
				logger.debug("keyWord is an instance of SMSSettings");
				autoResponse = orgKeyWord.getAutoResponse();
				
				
				
				if(orgKeyWord.getStatus().equalsIgnoreCase(Constants.KEYWORD_STATUS_EXPIRED)) {
					
					autoResponse = PropertyUtil.getPropertyValueFromDB(Constants.SMS_EXPIRED_KEYWORD_RESPONSE);
				}
				else if(Calendar.getInstance().before(orgKeyWord.getStartFrom())) {
					
					autoResponse = PropertyUtil.getPropertyValueFromDB(Constants.SMS_WRONG_KEYWORD_RESPONSE);
					
				}
				senderId = orgKeyWord.getSenderId();
				
				if(SMSStatusCodes.optOutFooterMap.get(user.getCountryType())){
					SMSSettingsDao smsSettingsDao = null;
					try {
						smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName("smsSettingsDao");
					
						SMSSettings smsSettings = smsSettingsDao.findByUser(user.getUserId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
						if(smsSettings != null && smsSettings.getMessageHeader() != null && autoResponse != null) autoResponse = smsSettings.getMessageHeader()+" "+autoResponse;
					} catch (Exception e) {
						logger.error("Exception ");
					}
				}
				
			}
			
			if(!user.isEnableSMS()) {
				throw new BaseServiceException("user is not enabled with SMS ");
				
			}
			
			OCSMSGateway ocgateway = GatewayRequestProcessHelper.getOcSMSGateway(user, SMSStatusCodes.defaultSMSTypeMap.get(user.getCountryType()));
			if(ocgateway == null) {
				
				throw new BaseServiceException("no gateway is available ");
				
			}
			//make an entry in inbound table
			ClickaTellSMSInbound inbounObj = gatewayRequestProcessHelper.createInBoundObj(sourceNumber, 
					receivedNumber, user, content, receivedTime, usedKeyword, autoResponse);
			//send auto response
			if(autoResponse != null && !autoResponse.trim().isEmpty()) {
				
				
				if(SMSStatusCodes.optInMap.get(user.getCountryType())) {//check if the optin concept is there for this user  
					ContactsDao contactsDao = null;
					UserSMSSenderIdDao userSMSSenderIdDao = null;
					try {
						contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
						userSMSSenderIdDao = (UserSMSSenderIdDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSSENDERID_DAO);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						throw new BaseServiceException("no contactdao / userSMSSenderIdDao bean found");
					}
					String mobilePhone = sourceNumber;
					if(sourceNumber.startsWith(""+user.getCountryCarrier())) {
						
						mobilePhone = sourceNumber.substring((""+user.getCountryCarrier()).length());
					}
					
					//find opt-in contact or not
					List<Contacts> mobileOptedContacts = contactsDao.findByQuery("FROM Contacts WHERE users="+user.getUserId()+" AND mobilePhone like '%"+mobilePhone+"'");
					
					
					if(mobileOptedContacts != null){
						
						Contacts mobileOptinContact = null;
						for (Contacts contact : mobileOptedContacts) {
							
							if(contact.isMobileOptin()) {
								mobileOptinContact = contact;
								break;
							}
							
							
						}
						if(mobileOptinContact != null) {
							ocgateway = GatewayRequestProcessHelper.getOcSMSGateway(user, SMSStatusCodes.optInTypeMap.get(user.getCountryType()));
							if(ocgateway == null) {
								
								throw new BaseServiceException("no gateway is available ");
								
							}
							List<UserSMSSenderId> retList = userSMSSenderIdDao.findSenderIdBySMSType(user.getUserId(), 
									SMSStatusCodes.optInTypeMap.get(user.getCountryType()));
							if(retList != null) senderId = retList.get(0).getSenderId();
							
						}
						//TODO send it thru the opt-in account
						
					}
					
					
				}
				
				captiwayToSMSApiGateway = new CaptiwayToSMSApiGateway();
				if(!ocgateway.isPostPaid()) {
					
					if(!captiwayToSMSApiGateway.getBalance(ocgateway, 1)){
						
						throw new BaseServiceException("lowcredits with "+ocgateway.getGatewayName());
					}
				}
				
				if( (  (user.getSmsCount() == null ? 0 : user.getSmsCount()) - (user.getUsedSmsCount() == null ? 0 : user.getUsedSmsCount() ) ) >=  1) {
					logger.info("3"+ocgateway, autoResponse, sourceNumber, senderId, contactId, inbounObj);
					captiwayToSMSApiGateway.sendAutoResponse(ocgateway, autoResponse, sourceNumber, senderId, contactId, inbounObj);
					//user.setUsedSmsCount( (user.getUsedSmsCount() != null ? user.getUsedSmsCount() : 0 )+1);
					UsersDao usersDao = null;
					UsersDaoForDML usersDaoForDML = null;
					try {
						
						usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);//ServiceByName(OCConstants.CAPTIWAYTOSMSAPIGATEWAY_SERVICE);
						usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
						//captiwayToSMSApiGateway = new CaptiwayToSMSApiGateway();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						throw new BaseServiceException("No bean found with name "+OCConstants.USERS_DAO);
					}
					//usersDao.saveOrUpdate(user);
					//usersDao.updateUsedSMSCount(user.getUserId(), 1);
					//usersDaoForDML.updateUsedSMSCount(user.getUserId(), 1);
					int charCount = autoResponse.length();
				    int usedCount=1;
				     if(charCount>160) usedCount = (charCount/160) + 1;
				     usersDaoForDML.updateUsedSMSCount(user.getUserId(), usedCount);
					/**
					 * Update SMS Queue.
					 */
					SmsQueueHelper smsQueueHelper = new SmsQueueHelper();
					smsQueueHelper.updateSMSQueue(sourceNumber, autoResponse, Constants.SMS_MSG_TYPE_AUTORESPONSE,  user, senderId);
					
				}
				
				if(inbounObj != null) {
					try{
						ClickaTellSMSInboundDao clickaTellSMSInboundDao = (ClickaTellSMSInboundDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CLICKATELLSMSINBOUND_DAO);
						ClickaTellSMSInboundDaoForDML clickaTellSMSInboundDaoForDML = (ClickaTellSMSInboundDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CLICKATELLSMSINBOUND_DAO_FOR_DML);
						//clickaTellSMSInboundDao.saveOrUpdate(inbounObj);
						clickaTellSMSInboundDaoForDML.saveOrUpdate(inbounObj);
					}catch (Exception e) {
							// TODO Auto-generated catch block
						throw new BaseServiceException("Problem while creating the inbound entry");
							
					}
				}
				
			}//if
			
		
			
		}
		
		return baseResponseObject;
	}
	
	
	
	
	@Override
	public BaseResponseObject processSMSMissedCallOptinRequest(SMSInBoundRequestObject smsInBoundRequestObject)
			throws BaseServiceException {

		BaseResponseObject baseResponseObject = new BaseResponseObject();//TODO set response
		//need to parse and get the word(s) 
		logger.debug("=======calling the service ======");
		String receivedNumber = smsInBoundRequestObject.getDestination();
		SMSSettingsDao smsSettigsDao = null;
		
		try {
			
			smsSettigsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("No dao found in the context with id "+OCConstants.SMSSETTINGS_DAO);
		}
		
		if(smsSettigsDao == null) throw new BaseServiceException("No dao found in the context with id "+OCConstants.SMSSETTINGS_DAO);
		
		SMSSettings smsSettings = smsSettigsDao.findByMissedCallNumber(receivedNumber);
		
		if(smsSettings == null) throw new BaseServiceException("No record found in sms settings optin setup for this missedcall number "+receivedNumber);
		
		
		GatewayRequestProcessHelper gatewayRequestProcessHelper =  new GatewayRequestProcessHelper();
		gatewayRequestProcessHelper.processMobileOptin(smsSettings, smsInBoundRequestObject.getSource(), OCConstants.MOBILE_OPTIN_SOURCE_MISSEDCALL);
	
		
		return baseResponseObject;
	}
	
	@Override
	public BaseResponseObject processDLRRequest(SMSHTTPDLRRequestObject SMSHTTPDLRRequestObj) throws BaseServiceException {
		BaseResponseObject baseResponseObject = new BaseResponseObject();//TODO set response
		
		
		String msgID = SMSHTTPDLRRequestObj.getMessageID();
		String mobileNumber = SMSHTTPDLRRequestObj.getMobileNumber();
		String deliveredTimeStr = SMSHTTPDLRRequestObj.getDeliveredTimeStr();
		String status = SMSHTTPDLRRequestObj.getStatus();
		String reason = SMSHTTPDLRRequestObj.getReason();
		String clientMsgId = SMSHTTPDLRRequestObj.getClientMsgId();
		
		if(msgID == null || mobileNumber == null || deliveredTimeStr == null
				|| status == null ) {
			
			throw new BaseServiceException("one of the required parametes are not received");
		}
		Long minutes = null;
		Calendar deliveredTime = Calendar.getInstance();
		try {
			minutes = Long.parseLong(deliveredTimeStr);
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			try {
				//SimpleDateFormat format = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
				if(SMSHTTPDLRRequestObj.getTimeFormat() != null) {
					
					SimpleDateFormat format = new SimpleDateFormat(SMSHTTPDLRRequestObj.getTimeFormat());
					deliveredTime.setTime(format.parse(deliveredTimeStr));
				}
				
			} catch (Exception e) {
				
				logger.error(" Unable to convert the time value into time "+deliveredTimeStr);
			}
			
		}
		
		//no NFE means it is long minuts
		if(minutes != null) {
						
			long timeinMillis = minutes*1000;
			deliveredTime.setTimeInMillis(timeinMillis);
		}
		SMSHTTPDLRRequestObj.setDeliveredTime(deliveredTime);
		
		GatewayRequestProcessHelper gatewayRequestProcessHelper =  new GatewayRequestProcessHelper();
		
		if(clientMsgId != null) {
			if(clientMsgId.startsWith(Constants.SMS_TYPE_AUTORESPONSE+Constants.ADDR_COL_DELIMETER)) gatewayRequestProcessHelper.processInboundDLR(SMSHTTPDLRRequestObj);
			else gatewayRequestProcessHelper.processCampaignDLR(SMSHTTPDLRRequestObj);
		}
		else gatewayRequestProcessHelper.processInboundDLR(SMSHTTPDLRRequestObj);
		
		
		
		
		
		return baseResponseObject;
	}
}
