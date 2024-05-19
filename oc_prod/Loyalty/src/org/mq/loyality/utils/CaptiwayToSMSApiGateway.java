package org.mq.loyality.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.loyality.common.dao.CharacterCodesDao;
import org.mq.loyality.common.hbmbean.CharacterCodes;
import org.mq.loyality.common.hbmbean.OCSMSGateway;
import org.springframework.beans.factory.annotation.Autowired;


public class CaptiwayToSMSApiGateway {
	
	@Autowired
	private NetCoreApi netCoreApi;
	@Autowired
	private ClickaTellApi clickaTellApi;
	@Autowired
	private MVaayooApi mVaayooApi;
	@Autowired
	private CharacterCodesDao characterCodesDao;
	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);
	
public String replaceSpecialCharacters(String messageContent) {
		
		try {
		//	CharacterCodesDao characterCodesDao = null;
			try {
			//	characterCodesDao = (CharacterCodesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CHARACTERCODESDAO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
			
			if(characterCodesDao == null ) return messageContent;
			
			List<CharacterCodes> charCodes = characterCodesDao.findAll();
			
			if(charCodes == null || charCodes.size() <= 0) return messageContent; 
			
				
			
			String retMessage = messageContent;
			for (CharacterCodes characterCodes : charCodes) {
				retMessage = messageContent.replace(characterCodes.getCharcater(), characterCodes.getCode());
				
			}//for
			
			return retMessage;
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return messageContent;
	}	
	
public  boolean getBalance(OCSMSGateway ocsmsGateway, int totalCount) {
	
	String userSMSTool = ocsmsGateway.getGatewayName();
	String userID = ocsmsGateway.getUserId();
	String pwd = ocsmsGateway.getPwd();
	String apiID = ocsmsGateway.getAPIId();
	
	if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_SMSCOUNTRY)) {
		//*************** need to convert the request into the SMSCountryAPI understandable format******
		//and send a requset to send SMS
		//TODO need to implement
		return false;
		
	}
	else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CLICKATELL)) {
		//call the method to prepare the data into xml format
		
		return clickaTellApi.getBalance(totalCount, userID, pwd, apiID);
		
		
		//update the status in SMSCampaignSent
		//updateSMSCampStatus(response, sentId);
		
		
		
	}
	else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_NETCORE)) {
		// need to convert the NEtCore understandable format
		//TODO need to implement
		return false;
		
	}
	
	else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CELLNEXT)) {
		
		//TODO need to implement
		return false;
	}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_PAKISTAN)) {
		
		PakistanGateway pakistanGatewayObj = new PakistanGateway();
		return pakistanGatewayObj.getBalence(totalCount, ocsmsGateway);
		
	} else if(Constants.USER_SMSTOOL_INFOBIP.equalsIgnoreCase(userSMSTool)){
		InfobipSMSGateway infobipSMSGateway =  new InfobipSMSGateway();
		return infobipSMSGateway.getBalance(totalCount, ocsmsGateway);
	}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_OUTREACH)) {
		
		OutreachPakistanGateway outreachPakistanGatewayObj = new OutreachPakistanGateway();
		return outreachPakistanGatewayObj.getBalence(totalCount, ocsmsGateway);
		
	}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_BSMS_ITS_PAKISTAN)) {
		
		BsmsItsPakistanGateway bsmsItsPakistanGateway = new BsmsItsPakistanGateway();
		return bsmsItsPakistanGateway.getBalence(totalCount, ocsmsGateway);
		
	}
	return true;
	
}
	
public String sendSingleSms(OCSMSGateway ocsmsGateway, String content, 
				String mobileNumber, String senderId) throws Exception {

	logger.info("IN SMS class=============>"+ocsmsGateway);
			String userSMSTool = ocsmsGateway.getGatewayName();
			String userID = ocsmsGateway.getUserId();
			String pwd = ocsmsGateway.getPwd();
			String apiId = ocsmsGateway.getAPIId();
			String msgID = null;

			if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_UNICEL)) {
				// need to convert the mVaayooApi understandable format

				UnicelSMSGateway unicelSMSGatewayObj = new UnicelSMSGateway(userID, pwd);
				msgID = unicelSMSGatewayObj.sendOverHTTP(replaceSpecialCharacters(content), mobileNumber, senderId);

			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CLICKATELL)) {
				
				content = StringEscapeUtils.escapeHtml(content);
				if(clickaTellApi == null)clickaTellApi = new ClickaTellApi();
				msgID = clickaTellApi.sendSingleSms(userID, pwd,apiId, mobileNumber,content, senderId);

			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MVAYOO)) {
				
				if(mVaayooApi == null) {
					
					mVaayooApi = new MVaayooApi();
					
				}
				msgID = mVaayooApi.sendSMSOverHTTP(content, mobileNumber, senderId, userID, pwd);
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_PAKISTAN)) {
				
				PakistanGateway pakistanGateway = new PakistanGateway();
				try {
					String[] responseArr = pakistanGateway.sendMsg(content, true, mobileNumber, userID, pwd, senderId);
					if(responseArr != null){
						msgID = responseArr[0] + Constants.ADDR_COL_DELIMETER + responseArr[1];
					}
				} catch (Exception e) {
					logger.info("Exception while sending msg ",e);
				}
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_INFOBIP)){
				InfobipSMSGateway infobipSMSGateway = new InfobipSMSGateway();
				msgID = infobipSMSGateway.sendOverHTTP(userID,pwd,replaceSpecialCharacters(content), mobileNumber, senderId);
				
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_OUTREACH)) {
				
				OutreachPakistanGateway outreachPakistanGateway = new OutreachPakistanGateway();
				try {
					String[] responseArr = outreachPakistanGateway.sendMsg(content, true, mobileNumber, userID, pwd, senderId);
					if(responseArr != null){
						msgID = responseArr[0] + Constants.ADDR_COL_DELIMETER + responseArr[1];
					}
				} catch (Exception e) {
					logger.info("Exception while sending msg ",e);
				}
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_BSMS_ITS_PAKISTAN)) {
				BsmsItsPakistanGateway bsmsItsPakistanGateway = new BsmsItsPakistanGateway();
				try {
					Map<String,String> responseMap = bsmsItsPakistanGateway.sendMsg(content, true, mobileNumber, userID, pwd, senderId);
					if(responseMap != null){
						Set<Map.Entry<String,String>> set = responseMap.entrySet();  
						Map.Entry<String, String> mapEntry = set.iterator().next();
						
						String key = mapEntry.getKey();
						String value = mapEntry.getValue();
						msgID = key;
					}
				} catch (Exception e) {
					logger.error("Exception while sending msg ", e);
				}
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_EQUENCE)) {

				EquenceSMSGateway equenceSMSGateway = new EquenceSMSGateway(ocsmsGateway,userID,pwd,senderId);
				String messageContent = StringEscapeUtils.escapeHtml(content);
				equenceSMSGateway.sendTestSMS(replaceSpecialCharacters(messageContent), mobileNumber);

			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_SYNAPSE)) {

				SynapseSMSGateway synapseSMSGateway = new SynapseSMSGateway(ocsmsGateway,userID,pwd,senderId);
				String messageContent = StringEscapeUtils.escapeHtml(content);
				synapseSMSGateway.sendTestSMS(replaceSpecialCharacters(messageContent), mobileNumber);

			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_INFOCOMM)) {

				InfocommSMSGateway infocommSMSGateway = new InfocommSMSGateway(ocsmsGateway,userID,pwd,senderId);
				String messageContent = StringEscapeUtils.escapeHtml(content);
				infocommSMSGateway.sendTestSMS(replaceSpecialCharacters(messageContent), mobileNumber);

			}
			logger.info("msg id in captiway=============>"+msgID);
			
			return msgID;
		}
	
}
