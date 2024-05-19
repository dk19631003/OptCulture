package org.mq.marketer.campaign.controller.service;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CharacterCodes;
import org.mq.marketer.campaign.beans.DRSMSSent;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.CharacterCodesDao;
import org.mq.marketer.campaign.dao.OCSMSGatewayDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.beans.BeansException;

/**
 * 
 * @author proumya
 *
 */
public class CaptiwayToSMSApiGateway {
	
	/*private String userName,password;
	private String userSMSTool,response;*/
	
	
	private ClickaTellApi clickaTellApi;

	private CellNextApi cellNextApi;
	private NetCoreApi netCoreApi;
	private SMSCountryApi smsCountryApi;
	public MVaayooApi getmVaayooApi() {
		return mVaayooApi;
	}

	public void setmVaayooApi(MVaayooApi mVaayooApi) {
		this.mVaayooApi = mVaayooApi;
	}

	private MVaayooApi mVaayooApi;
	
	public ClickaTellApi getClickaTellApi() {
		return clickaTellApi;
	}

	public void setClickaTellApi(ClickaTellApi clickaTellApi) {
		this.clickaTellApi = clickaTellApi;
	}

	public CellNextApi getCellNextApi() {
		return cellNextApi;
	}

	public void setCellNextApi(CellNextApi cellNextApi) {
		this.cellNextApi = cellNextApi;
	}

	public NetCoreApi getNetCoreApi() {
		return netCoreApi;
	}

	public void setNetCoreApi(NetCoreApi netCoreApi) {
		this.netCoreApi = netCoreApi;
	}

	public SMSCountryApi getSmsCountryApi() {
		return smsCountryApi;
	}

	public void setSmsCountryApi(SMSCountryApi smsCountryApi) {
		this.smsCountryApi = smsCountryApi;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		CaptiwayToSMSApiGateway.logger = logger;
	}
	
	private static Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public CaptiwayToSMSApiGateway() {
		
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
		}/// add here for outreach---------------1
		else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_OUTREACH)) {
			
			OutreachPakistanGateway outreachPakistanGatewayObj = new OutreachPakistanGateway();
			return outreachPakistanGatewayObj.getBalence(totalCount, ocsmsGateway);
			
		}if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_BSMS_ITS_PAKISTAN)) {
			
			BsmsItsPakistanGateway bsmsItsPakistanGateway = new BsmsItsPakistanGateway();
			return bsmsItsPakistanGateway.getBalence(totalCount, ocsmsGateway);
			
		}
		return true;
		
	}
	public String getMobileStr(Set<String> optinMobileSet, Users currUser ) {
		
		String mobileStr = Constants.STRING_NILL;
		for (String toMobNum : optinMobileSet) {
			toMobNum = toMobNum.trim();
			
			if(!toMobNum.startsWith(currUser.getCountryCarrier().toString()) && toMobNum.length() == 10) {
				toMobNum = currUser.getCountryCarrier().toString()+toMobNum;
			}
			
			if(mobileStr.length()>0)	mobileStr += ",";
			
			mobileStr +=  toMobNum;
			
			/*if(isFillCoup) {
				
				try {
					text = replacePlaceHolders.replaceSMSAutoResponseContent(text, coupPhSet, toMobNum, null);
					text = StringEscapeUtils.escapeHtml(text);
				} catch (BaseServiceException e) {
					// TODO Auto-generated catch block
					logger.error("exception while getting the coupon placehlders", e);
				}
			}*/
			
			
		}//for
		return mobileStr;
		
	}
	public void sendMultipleMobileDoubleOptin(OCSMSGateway ocsmsGateway, Set<String> optinMobileSet, SMSSettings smsSettings) {
		
		String userSMSTool = ocsmsGateway.getGatewayName();
		String userID = ocsmsGateway.getUserId();
		String pwd = ocsmsGateway.getPwd();
		String apiID = ocsmsGateway.getAPIId();
		
		if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_SMSCOUNTRY)) {
			//*************** need to convert the request into the SMSCountryAPI understandable format******
			//and send a requset to send SMS
			//TODO need to implement
			
		}
		else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CLICKATELL)) {
			//call the method to prepare the data into xml format
			
			clickaTellApi.sendMultipleMobileDoubleOptin(userID, pwd, apiID, optinMobileSet, smsSettings);
			
			
			//update the status in SMSCampaignSent
			//updateSMSCampStatus(response, sentId);
			
			
			
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MVAYOO)) {
			//call the method to prepare the data into xml format
			
			
			mVaayooApi.sendSMSOverHTTP(smsSettings.getAutoResponse(), getMobileStr(optinMobileSet,
					smsSettings.getUserId()), smsSettings.getSenderId(), userID, pwd);
			//clickaTellApi.sendMultipleMobileDoubleOptin(userID, pwd, optinMobileSet, smsSettings);
			
			
			//update the status in SMSCampaignSentMo
			//updateSMSCampStatus(response, sentId);
			
			
			
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_UNICEL)) {
			//call the method to prepare the data into xml format
			UnicelSMSGateway unicelSMSGatewayObj = new UnicelSMSGateway(userID, pwd);
			
			unicelSMSGatewayObj.test(replaceSpecialCharacters(smsSettings.getAutoResponse()), 
					getMobileStr(optinMobileSet, smsSettings.getUserId()), smsSettings.getSenderId());
			
			//clickaTellApi.sendMultipleMobileDoubleOptin(userID, pwd, optinMobileSet, smsSettings);
			
			
			//update the status in SMSCampaignSent
			//updateSMSCampStatus(response, sentId);
			
			
			
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CM)) {
			// need to convert the mVaayooApi understandable format
			 CMComSMSGateway CMComSMSGatewayObj = null;
			if(CMComSMSGatewayObj == null) {
				
				CMComSMSGatewayObj = new CMComSMSGateway( ocsmsGateway);
			}
			//String messageContent =  replaceSpecialCharacters(rawMessageContent );
			
			//String messageContent = StringEscapeUtils.escapeHtml(rawMessageContent);
			
			//CMComSMSGatewayObj.sendSMSOverSMPP(messageContent, toMobNum, Long.parseLong(sentId), senderId);
		}
		else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_PAKISTAN)) {
			PakistanGateway pakistanGateway = new PakistanGateway();
			pakistanGateway.sendMultipleMobileDoubleOptin(userID, pwd, optinMobileSet, smsSettings);
			
			
		}
		else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_NETCORE)) {
			// need to convert the NEtCore understandable format
			//TODO need to implement
			
		}
		
		else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CELLNEXT)) {
			// need to convert the NEtCore understandable format
			//TODO need to implement
		}
		else if (Constants.USER_SMSTOOL_INFOBIP.equalsIgnoreCase(userSMSTool)){
			//TODO need to implement 
		}
		///--- outreach-------2
		else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_OUTREACH)) {
			OutreachPakistanGateway outreachPakistanGateway = new OutreachPakistanGateway();
			outreachPakistanGateway.sendMultipleMobileDoubleOptin(userID, pwd, optinMobileSet, smsSettings);
			
			
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_BSMS_ITS_PAKISTAN)) {
			BsmsItsPakistanGateway bsmsItsPakistanGateway = new BsmsItsPakistanGateway();
			bsmsItsPakistanGateway.sendMultipleMobileDoubleOptin(userID, apiID, optinMobileSet, smsSettings);
			
			
		}
		
		
		
	}
	
	
	
	
	/*
	public String sendSingleMobileDoubleOptin(String userSMSTool, String from, String to, String text) {
		
		if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_SMSCOUNTRY)) {
			//*************** need to convert the request into the SMSCountryAPI understandable format******
			//and send a requset to send SMS
			//TODO need to implement
			
		}
		else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CLICKATELL)) {
			//call the method to prepare the data into xml format
			
			text = StringEscapeUtils.escapeHtml(text);
			return clickaTellApi.sendSingleMobileDoubleOptin(from, to, text);
			
			
			//update the status in SMSCampaignSent
			//updateSMSCampStatus(response, sentId);
			
			
			
		}
		else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_NETCORE)) {
			// need to convert the NEtCore understandable format
			//TODO need to implement
			return null;
			
		}
		
		else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CELLNEXT)) {
			// need to convert the NEtCore understandable format
			//TODO need to implement
			return null;
		}
		return null;
		
		
		
		
		
	}*/
	
	public String sendSingleMobileDoubleOptin(OCSMSGateway ocsmsGateway, 
			String senderID, String to, String text, Users currUser) throws BaseServiceException{
		
		String userSMSTool = ocsmsGateway.getGatewayName();
		String userID = ocsmsGateway.getUserId();
		String pwd = ocsmsGateway.getPwd();
		String apiID = ocsmsGateway.getAPIId();
		
		text = text.replace("|^", "[").replace("^|", "]");
		Set<String> coupPhSet = Utility.findCoupPlaceholders(text);
		
		if(coupPhSet != null  && coupPhSet.size() > 0) {
			
			
			text = replaceSMSAutoResponseContent(text, coupPhSet, to);
			
		}
		if(text == null) {
			
			return Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
			
		}
		
		if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_SMSCOUNTRY)) {
			//*************** need to convert the request into the SMSCountryAPI understandable format******
			//and send a requset to send SMS
			//TODO need to implement
			
		}
		else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CLICKATELL)) {
			//call the method to prepare the data into xml format
			text = StringEscapeUtils.escapeHtml(text);
			return clickaTellApi.sendSingleMobileDoubleOptin(userID, pwd, apiID, senderID, to, text);
			
			
			//update the status in SMSCampaignSent
			//updateSMSCampStatus(response, sentId);
			
			
			
		}
		else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MVAYOO)) {
			//call the method to prepare the data into xml format
			
			if(!to.startsWith(currUser.getCountryCarrier().toString()) && to.length() == 10) {
				to = currUser.getCountryCarrier().toString()+to;
			}
			mVaayooApi.sendSMSOverHTTP(text, to, senderID, userID, pwd);
			//clickaTellApi.sendMultipleMobileDoubleOptin(userID, pwd, optinMobileSet, smsSettings);
			
			
			//update the status in SMSCampaignSentMo
			//updateSMSCampStatus(response, sentId);
			
			
			
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_UNICEL)) {
			
			if(!to.startsWith(currUser.getCountryCarrier().toString()) && to.length() == 10) {
				to = currUser.getCountryCarrier().toString()+to;
			}
			UnicelSMSGateway unicelSMSGatewayObj = new UnicelSMSGateway(userID, pwd);
			unicelSMSGatewayObj.test(replaceSpecialCharacters(text), to, senderID);
			
			
			
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_PAKISTAN)) {
			
			/*if(!to.startsWith(currUser.getCountryCarrier().toString()) && to.length() == 10) {
				to = currUser.getCountryCarrier().toString()+to;
			}*/
			PakistanGateway pakistanGateway = new PakistanGateway();
			try {
				pakistanGateway.prepareData(userID, pwd, senderID,text, to);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception while sending msg ", e);
			}
			
			
			
		}else if(Constants.USER_SMSTOOL_INFOBIP.equalsIgnoreCase(userSMSTool)){
			InfobipSMSGateway infobipSMSGateway  = new InfobipSMSGateway();
			infobipSMSGateway.sendTestSMS(userID,pwd,senderID,replaceSpecialCharacters(text), to );
			
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_OUTREACH)) {
			
			/*if(!to.startsWith(currUser.getCountryCarrier().toString()) && to.length() == 10) {
				to = currUser.getCountryCarrier().toString()+to;
			}*/
			OutreachPakistanGateway outreachPakistanGateway = new OutreachPakistanGateway();
			try {
				outreachPakistanGateway.prepareData(userID, pwd, senderID,text, to);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception while sending msg ", e);
			}
			
			
			
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_BSMS_ITS_PAKISTAN)) {
			
			/*if(!to.startsWith(currUser.getCountryCarrier().toString()) && to.length() == 10) {
				to = currUser.getCountryCarrier().toString()+to;
			}*/
			BsmsItsPakistanGateway bsmsItsPakistanGateway = new BsmsItsPakistanGateway();
			try {
				bsmsItsPakistanGateway.prepareData(userID, apiID, senderID,text, to);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception while sending msg ", e);
			}
			
			
			
		}
		
		///-------outreach  3
		return null;
		
	}
	
	
	/**used to redirect the send sms request to appropriate(user configured SMSApi) by 
	 * preparing data into the specific API understandable format 
	 * 
	 * @param user
	 * @param messageContent
	 * @param mobileNumber
	 * @param msgType
	 * @param contactId
	 * @param fromMobNum
	 * @param toMobNum
	 * @param msgSeq
	 */
	/*public  void sendToSMSApi(String userSMSDetails, String messageContent, String mobileNumber, 
					String msgType, String fromMobNum, String toMobNum, String msgSeq, String senderId) {*/
		public  void sendToSMSApi(OCSMSGateway ocsmsGateway, String messageContent, String mobileNumber, 
				String msgType, String fromMobNum, String toMobNum, String msgSeq, String senderId,String templateRegisteredId) {
		
		try {
			logger.debug("----just entered----");
			//******* need to write code to send the request to appropriate API based on the user configurations******** 
			
			/*String[] userSMSDetailsArr = userSMSDetails.split(Constants.ADDR_COL_DELIMETER);
			
			String userSMSTool = userSMSDetailsArr[0];
			String userID = userSMSDetailsArr[1];
			String pwd = userSMSDetailsArr[2];*/
			
			
			
			String userSMSTool = ocsmsGateway.getGatewayName();
			if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CM)) {
				OCSMSGatewayDao OCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName("OCSMSGatewayDao");

				ocsmsGateway = OCSMSGatewayDao.findForSubscriber(ocsmsGateway.getGatewayName(),ocsmsGateway.getAccountType() ); 
					
			}
			//this.userSMSTool = user.getSMSTool();//<----no need to fetch from DB;need to define it in users bean
			String userID = ocsmsGateway.getUserId();
			String pwd = ocsmsGateway.getPwd();
			String apiId = ocsmsGateway.getAPIId();
			
			logger.debug("===User SMS tool == "+userSMSTool +" == account == "+userID);
			
			if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_SMSCOUNTRY)) {
				//*************** need to convert the request into the SMSCountryAPI understandable format******
				//and send a requset to send SMS
				
				
			}
			else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CELLNEXT)) {
				//call the method to prepare the data into xml format
				
				//((CellNextApi)SpringUtil.getBean("cellNextApi")).prepareData(messageContent, msgType, fromMobNum, toMobNum, msgSeq);//prepareData(messageContent, msgType,  fromMobNum, toMobNum, msgSeq);
				
				
				//update the status in SMSCampaignSent
				//updateSMSCampStatus(response, sentId);
				
				
				
			}
			else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_NETCORE)) {
				// need to convert the NEtCore understandable format
				NetCoreApi netCoreApiObj = new NetCoreApi();
				netCoreApiObj.prepareData(messageContent, msgType, fromMobNum, toMobNum, msgSeq, senderId);
			}
			
			else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CLICKATELL)) {
				// need to convert the NEtCore understandable format
		  //  messageContent =  StringEscapeUtils.escapeHtml(messageContent);
				String hasUnicode = "0"; //App - 3846
				 for (int i = 0; i < messageContent.length(); i++) {
				        
						int codePoint = messageContent.codePointAt(i);
				        
						if (codePoint > 127) {
							hasUnicode = "1";
						}
						}
				//  String outputString = Constants.STRING_NILL;
				if(hasUnicode.equalsIgnoreCase("1")) {
				try {
				byte[] ar = messageContent.getBytes(StandardCharsets.UTF_16BE);
		
			
				StringBuilder sb = new StringBuilder();
			    for (byte b : ar) {
			        sb.append(String.format("%02X", b));
			    }

			   messageContent =  sb.toString();
			   logger.debug("Printing the message content in utf16:"+messageContent);
				}catch(Exception e) {
			
				System.out.println("Exception while changing in byte"+ e);
				}
			}else {
				    messageContent =  StringEscapeUtils.escapeHtml(messageContent);
				    logger.info("has  not unicode : "+messageContent);

			}
			    
				ClickaTellApi clickaTellApiObj = new ClickaTellApi();
				clickaTellApiObj.prepareData(userID, pwd,  apiId,
						messageContent, msgType, fromMobNum, toMobNum, msgSeq, senderId,hasUnicode); 
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MVAYOO)) {
				// need to convert the mVaayooApi understandable format
				MVaayooApi mVaayooApiObj = new MVaayooApi();
				mVaayooApiObj.prepareData(userID, pwd,
						messageContent, msgType, fromMobNum, toMobNum, msgSeq, senderId);
				
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_UNICEL)) {
				
				UnicelSMSGateway unicelSMSGatewayObj = new UnicelSMSGateway(userID, pwd);
				unicelSMSGatewayObj.test(replaceSpecialCharacters(messageContent), toMobNum, senderId);
			}
			else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CM)) {
				logger.debug("==CM==");
				String message = replaceSpecialCharacters(messageContent);
				CMComSMSGateway CMComSMSGateway = new CMComSMSGateway(ocsmsGateway);
				CMComSMSGateway.sendSMSOverSMPP(message, toMobNum, null, senderId);//(replaceSpecialCharacters(messageContent), toMobNum, senderId);
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_PAKISTAN)) {
				//TODO store the responses
				PakistanGateway pakisthGatewayObj = new PakistanGateway();
				pakisthGatewayObj.prepareData(userID, pwd, senderId,messageContent, toMobNum);
				
				
			}//Added for Infobip
			else if(Constants.USER_SMSTOOL_INFOBIP.equalsIgnoreCase(userSMSTool)){
				InfobipSMSGateway infobipSMSGateway = new InfobipSMSGateway();
				//Replace Special Characters 
				String message = replaceSpecialCharacters(messageContent);
				infobipSMSGateway.sendTestSMS(userID,pwd,senderId,message,toMobNum);
				
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_OUTREACH)) {
				//TODO store the responses
				OutreachPakistanGateway outreachPakisthGatewayObj = new OutreachPakistanGateway();
				outreachPakisthGatewayObj.prepareData(userID, pwd, senderId,messageContent, toMobNum);
				
				
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_BSMS_ITS_PAKISTAN)) {
				//TODO store the responses
				BsmsItsPakistanGateway bsmsItsPakistanGateway = new BsmsItsPakistanGateway();
				bsmsItsPakistanGateway.prepareData(userID, apiId, senderId,messageContent, toMobNum);
				
				
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_EQUENCE)) {
				
				EquenceSMSGateway equenceSMSGateway = new EquenceSMSGateway(userID, pwd,senderId,ocsmsGateway.getPrincipalEntityId());
				//String content = StringEscapeUtils.escapeHtml(messageContent);
				equenceSMSGateway.prepareData(userID, pwd, senderId, messageContent, toMobNum,templateRegisteredId);
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_SYNAPSE)) {
				
				SynapseSMSGateway synapseSMSGateway = new SynapseSMSGateway(userID, pwd,senderId);
				String content = StringEscapeUtils.escapeHtml(messageContent);
				synapseSMSGateway.prepareData(userID, pwd, senderId, messageContent, toMobNum);
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_INFOCOMM)) {
				
				InfocommSMSGateway infocommSMSGateway = new InfocommSMSGateway(userID, pwd,senderId);
				String content = StringEscapeUtils.escapeHtml(messageContent);
				infocommSMSGateway.prepareData(userID, pwd, senderId, messageContent, toMobNum);
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MESSAGEBIRD)) {
				
				MessagebirdSMSGateway messagebirdSMSGateway = new MessagebirdSMSGateway(ocsmsGateway,userID,pwd,senderId,ocsmsGateway.getPrincipalEntityId());
				//String content = StringEscapeUtils.escapeHtml(messageContent);
				messagebirdSMSGateway.prepareData(userID, pwd, senderId, messageContent, toMobNum);
			}
			///--------outreach 4
		} catch (Exception e) {
			logger.error("Exception while sending to SMS Api",e);
		}
		
		
		
	}//sendToSMSApi
		public  String sendDRSMS(OCSMSGateway ocsmsGateway, String messageContent, String mobileNumber, 
				String msgType, String fromMobNum, String toMobNum, String msgSeq, String senderId,String templateRegisteredId,DRSMSSent drSmsSent) {
		
		try {
			logger.debug("----just entered----");
			
			String userSMSTool = ocsmsGateway.getGatewayName();
			
			if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CM)) {
				OCSMSGatewayDao OCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName("OCSMSGatewayDao");

				ocsmsGateway = OCSMSGatewayDao.findForSubscriber(ocsmsGateway.getGatewayName(),ocsmsGateway.getAccountType() ); 
					
			}
			String userID = ocsmsGateway.getUserId();
			String pwd = ocsmsGateway.getPwd();
			String apiId = ocsmsGateway.getAPIId();
			String msgID = null;
			if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_SMSCOUNTRY)) {
				//*************** need to convert the request into the SMSCountryAPI understandable format******
				//and send a requset to send SMS
				
				
			}
			else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CELLNEXT)) {
				//call the method to prepare the data into xml format
				
				//((CellNextApi)SpringUtil.getBean("cellNextApi")).prepareData(messageContent, msgType, fromMobNum, toMobNum, msgSeq);//prepareData(messageContent, msgType,  fromMobNum, toMobNum, msgSeq);
				
				
				//update the status in SMSCampaignSent
				//updateSMSCampStatus(response, sentId);
				
				
				
			}
			else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_NETCORE)) {
				// need to convert the NEtCore understandable format
				NetCoreApi netCoreApiObj = new NetCoreApi();
				netCoreApiObj.prepareData(messageContent, msgType, fromMobNum, toMobNum, msgSeq, senderId);
			}
			
			else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CLICKATELL)) {
				// need to convert the NEtCore understandable format
				messageContent = StringEscapeUtils.escapeHtml(messageContent);
				String hasUnicode = "0";//APP- 3846
				ClickaTellApi clickaTellApiObj = new ClickaTellApi();
				clickaTellApiObj.prepareData(userID, pwd,  apiId,
						messageContent, msgType, fromMobNum, toMobNum, msgSeq, senderId,hasUnicode);
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MVAYOO)) {
				// need to convert the mVaayooApi understandable format
				MVaayooApi mVaayooApiObj = new MVaayooApi();
				mVaayooApiObj.prepareData(userID, pwd,
						messageContent, msgType, fromMobNum, toMobNum, msgSeq, senderId);
				
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_UNICEL)) {
				
				UnicelSMSGateway unicelSMSGatewayObj = new UnicelSMSGateway(userID, pwd);
				unicelSMSGatewayObj.test(replaceSpecialCharacters(messageContent), toMobNum, senderId);
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CM)) {
				String message = replaceSpecialCharacters(messageContent);
				CMComSMSGateway CMComSMSGateway = new CMComSMSGateway(ocsmsGateway);
				CMComSMSGateway.sendSMSOverSMPP(message, toMobNum, drSmsSent.getId(), senderId);//(replaceSpecialCharacters(messageContent), toMobNum, senderId);
			}
			else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_PAKISTAN)) {
				//TODO store the responses
				PakistanGateway pakisthGatewayObj = new PakistanGateway();
				pakisthGatewayObj.prepareData(userID, pwd, senderId,messageContent, toMobNum);
				
				
			}//Added for Infobip
			else if(Constants.USER_SMSTOOL_INFOBIP.equalsIgnoreCase(userSMSTool)){
				InfobipSMSGateway infobipSMSGateway = new InfobipSMSGateway();
				//Replace Special Characters 
				String message = replaceSpecialCharacters(messageContent);
				infobipSMSGateway.sendTestSMS(userID,pwd,senderId,message,toMobNum);
				
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_OUTREACH)) {
				//TODO store the responses
				OutreachPakistanGateway outreachPakisthGatewayObj = new OutreachPakistanGateway();
				outreachPakisthGatewayObj.prepareData(userID, pwd, senderId,messageContent, toMobNum);
				
				
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_BSMS_ITS_PAKISTAN)) {
				//TODO store the responses
				BsmsItsPakistanGateway bsmsItsPakistanGateway = new BsmsItsPakistanGateway();
				bsmsItsPakistanGateway.prepareData(userID, apiId, senderId,messageContent, toMobNum);
				
				
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_EQUENCE)) {
				
				EquenceSMSGateway equenceSMSGateway = new EquenceSMSGateway(userID, pwd,senderId,ocsmsGateway.getPrincipalEntityId());
				msgID = equenceSMSGateway.sendDRSMS(messageContent, toMobNum,templateRegisteredId,drSmsSent);
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_SYNAPSE)) {
				
				SynapseSMSGateway synapseSMSGateway = new SynapseSMSGateway(userID, pwd,senderId);
				String content = StringEscapeUtils.escapeHtml(messageContent);
				synapseSMSGateway.prepareData(userID, pwd, senderId, messageContent, toMobNum);
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_INFOCOMM)) {
				
				InfocommSMSGateway infocommSMSGateway = new InfocommSMSGateway(userID, pwd,senderId);
				String content = StringEscapeUtils.escapeHtml(messageContent);
				infocommSMSGateway.prepareData(userID, pwd, senderId, messageContent, toMobNum);
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MESSAGEBIRD)) {
				
				MessagebirdSMSGateway messagebirdSMSGateway = new MessagebirdSMSGateway(ocsmsGateway,userID,pwd,senderId,ocsmsGateway.getPrincipalEntityId());
				msgID = messagebirdSMSGateway.sendDRSMS(messageContent, toMobNum,templateRegisteredId,drSmsSent);

			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MIM)) {
				
				MIMSMSGateway mimSMSGateway = new MIMSMSGateway(ocsmsGateway,userID,pwd,senderId,ocsmsGateway.getPrincipalEntityId());
				msgID = mimSMSGateway.sendDRSMS(messageContent, toMobNum,templateRegisteredId,drSmsSent);
				/*if(msgID!= null) {
					logger.info("entering mgid not null");
					String Status="";
					Thread.sleep(50000);
					Status = mimSMSGateway.SendDLR(mobileNumber, drSmsSent,msgID);
					if(Status.equalsIgnoreCase("Delivered")) {
						logger.info("entering Delivered condition");
						drSmsSent.setStatus("Delivered"); 
					}else {
						drSmsSent.setStatus(Status);
					}
				}*/
				
			}

			///--------outreach 4
			return msgID;
		} catch (Exception e) {
			logger.error("Exception while sending to SMS Api",e);
		}
		
		return null;
		
	}//sendDRSMS
	
	
	
	
	
	
	/**
	 * used to update the SMSCampaignSent status based on the response 
	 * @param statusMessage
	 * @param sentId
	 */
	/*public void updateSMSCampStatus(String statusMessage,String sentId) {
		try {
			logger.info("----just entered in updateSMSCampStatus ");
			String queryStr="update sms_campaign_sent set status='"+statusMessage+"'where sent_id="+sentId;
			((SMSCampaignSentDao)SpringUtil.getBean("smsCampaignSentDao")).updateSmsCampSentStatus(queryStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		
		
		
	}*/
	
	/*public static void main(String args[]) {
		CaptiwayToSMSApiGateway tempobj = new CaptiwayToSMSApiGateway();
		tempobj.sendToSMSApi(Constants.USER_SMSTOOL_CELLNEXT, "hai sampale message1", "919490918537", "1", "43", "9848495956", "919490918537", "1");
		
	}*/
		public void sendMessageFromOC(String userSMSTool, String UserId, String pwd, 
				String content, String mobileNumber, String senderId, String apiID) throws Exception{
			
			if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_UNICEL)) {
				// need to convert the mVaayooApi understandable format
				
				UnicelSMSGateway unicelSMSGatewayObj = new UnicelSMSGateway(UserId, pwd);
				
				 unicelSMSGatewayObj.test(replaceSpecialCharacters(content), mobileNumber, senderId);
				
				
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CLICKATELL)) {
				
				if(clickaTellApi == null)clickaTellApi = new ClickaTellApi();
//				String msgID = clickaTellApi.sendAutoResponse(UserId, pwd, apiID, mobileNumber, content, senderId, null );
				//String msgID = unicelSMSGatewayObj.sendOverHTTP(content, mobileNumber, senderId);
				
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_PAKISTAN)) {
				
				PakistanGateway pakisthGatewayObj = new PakistanGateway();
				pakisthGatewayObj.prepareData(UserId, pwd, senderId, content, mobileNumber );
				
			}else if(Constants.USER_SMSTOOL_INFOBIP.equalsIgnoreCase(userSMSTool)){
				InfobipSMSGateway infobipSMSGateway = new InfobipSMSGateway();
				infobipSMSGateway.sendTestSMS(UserId,pwd,senderId,replaceSpecialCharacters(content), mobileNumber );
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_OUTREACH)) {
				
				OutreachPakistanGateway outreachPakisthGatewayObj = new OutreachPakistanGateway();
				outreachPakisthGatewayObj.prepareData(UserId, pwd, senderId, content, mobileNumber );
				
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_BSMS_ITS_PAKISTAN)) {
				
				BsmsItsPakistanGateway bsmsItsPakistanGateway = new BsmsItsPakistanGateway();
				bsmsItsPakistanGateway.prepareData(UserId, pwd, senderId, content, mobileNumber );
				
			}
			///--------------outreach 5
			
		}
	
		public String replaceSpecialCharacters(String messageContent) {
			
				
			try {
				CharacterCodesDao characterCodesDao = null;
				try {
					characterCodesDao = (CharacterCodesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CHARACTERCODESDAO);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error(" Exception while getting the dao");
				}
				
				if(characterCodesDao == null ) return messageContent;
				
				List<CharacterCodes> charCodes = characterCodesDao.findAll();
				
				if(charCodes == null || charCodes.size() <= 0) return messageContent; 
				
					
				
				String retMessage = messageContent;
				for (CharacterCodes characterCodes : charCodes) {
					logger.debug("===entered for==="+characterCodes.getCharcater() + " replaced ===="+characterCodes.getCode());
					retMessage = messageContent.replace(characterCodes.getCharcater(), characterCodes.getCode());
					
				}//for
				
				return retMessage;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ", e);
			}
			return messageContent;
		}
	
		
		public  static String replaceSMSAutoResponseContent(String content, Set<String> coupSet, String to) throws BaseServiceException{
			
			if(coupSet == null) {
				
				coupSet = Utility.findCoupPlaceholders(content);
				
			}
			
			try {
				if(coupSet.size() > 0) {
					
					
					for (String cfStr : coupSet) {
						
						 if(cfStr.startsWith("CC_")) {
							 
							 String value="";
							
							 //to make coupon providing logic to be sync ,let the scheduler
							 //only offer a coupon code
							String postData = "cfStr="+cfStr+"&issuedTo="+to;
							URL url = new URL(PropertyUtil.getPropertyValue(Constants.COUP_PROVIDER_FOR_SUBSCRIBER_URL));
							
							HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
							
							urlconnection.setRequestMethod("POST");
							urlconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
							urlconnection.setDoOutput(true);

							OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
							out.write(postData);
							out.flush();
							out.close();
							
							
							BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
							
							String decodedString;
							while ((decodedString = in.readLine()) != null) {
								value += decodedString;
							}
							in.close();
							logger.info("response is======>"+value);

							
							if(value == null) value = "";
							
							content = content.replace("[" + cfStr + "]", value);
							
						}//if
						
						
						
					}//for
					
					
				}
				
				return content;
			} catch (BeansException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return null;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return null;
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return null;
			} /*catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return null;
			}*/
			
			
		}//replaceSMS
		public String sendSingleSms(OCSMSGateway ocsmsGateway, String content, 
				String mobileNumber, String senderId,String templateRegisteredId) throws Exception{
			return sendSingleSms( ocsmsGateway,  content,  mobileNumber,  senderId, templateRegisteredId,null);
		}
		
		public String sendSingleSms(OCSMSGateway ocsmsGateway, String content, 
				String mobileNumber, String senderId,String templateRegisteredId, Long rowId) throws Exception {

			String userSMSTool = ocsmsGateway.getGatewayName();
			
			
			
			if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CM)) {
				OCSMSGatewayDao OCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName("OCSMSGatewayDao");

				ocsmsGateway = OCSMSGatewayDao.findForSubscriber(ocsmsGateway.getGatewayName(),ocsmsGateway.getAccountType() ); 
					
			}
			String userID = ocsmsGateway.getUserId();
			String pwd = ocsmsGateway.getPwd();
			String apiId = ocsmsGateway.getAPIId();
			String msgID = null;

			if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_UNICEL)) {
				// need to convert the mVaayooApi understandable format

				UnicelSMSGateway unicelSMSGatewayObj = new UnicelSMSGateway(userID, pwd);
				msgID = unicelSMSGatewayObj.sendOverHTTP(replaceSpecialCharacters(content), mobileNumber, senderId);

			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CM)) {
				String message = replaceSpecialCharacters(content);
				CMComSMSGateway CMComSMSGateway = new CMComSMSGateway(ocsmsGateway);
				CMComSMSGateway.sendSMSOverSMPP(message, mobileNumber, null, senderId);//(replaceSpecialCharacters(messageContent), toMobNum, senderId);
			}
			else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CLICKATELL)) {
				
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
					logger.error("Exception while sending msg ", e);
				}
			}else if(Constants.USER_SMSTOOL_INFOBIP.equalsIgnoreCase(userSMSTool)){
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
					logger.error("Exception while sending msg ", e);
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

				EquenceSMSGateway equenceSMSGateway = new EquenceSMSGateway(ocsmsGateway,userID,pwd,senderId,ocsmsGateway.getPrincipalEntityId());
				logger.info("sms Content before replaing : "+content);
				String messageContent = content;//StringEscapeUtils.escapeHtml(content); 
				msgID = equenceSMSGateway.sendTestSMS(replaceSpecialCharacters(messageContent), mobileNumber,templateRegisteredId, rowId);

			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_SYNAPSE)) {

				SynapseSMSGateway synapseSMSGateway = new SynapseSMSGateway(ocsmsGateway,userID,pwd,senderId);
				String messageContent = StringEscapeUtils.escapeHtml(content);
				synapseSMSGateway.sendTestSMS(replaceSpecialCharacters(messageContent), mobileNumber);

			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_INFOCOMM)) {

				InfocommSMSGateway infocommSMSGateway = new InfocommSMSGateway(ocsmsGateway,userID,pwd,senderId);
				String messageContent = StringEscapeUtils.escapeHtml(content);
				infocommSMSGateway.sendTestSMS(replaceSpecialCharacters(messageContent), mobileNumber);

			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MESSAGEBIRD)) {

				MessagebirdSMSGateway messagebirdSMSGateway = new MessagebirdSMSGateway(ocsmsGateway,userID,pwd,senderId,ocsmsGateway.getPrincipalEntityId());
				String messageContent = StringEscapeUtils.escapeHtml(content);
				messagebirdSMSGateway.sendTestSMS(replaceSpecialCharacters(messageContent), mobileNumber);

			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MIM)) {
				
				MIMSMSGateway mimSMSGateway = new MIMSMSGateway(ocsmsGateway,userID,pwd,senderId,ocsmsGateway.getPrincipalEntityId());
				String messageContent = StringEscapeUtils.escapeHtml(content);
				mimSMSGateway.sendTestSMS(replaceSpecialCharacters(messageContent), mobileNumber);

			}

			
			/// outreach -----------6
			logger.debug("---------------msgId::"+msgID);
			return msgID;
		}
		
}//CaptiwayTOSMSApiGateway
