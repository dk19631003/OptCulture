package org.mq.captiway.scheduler.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.RecipientProvider;
import org.mq.captiway.scheduler.SMSRecipientProvider;
import org.mq.captiway.scheduler.beans.CampaignReport;
import org.mq.captiway.scheduler.beans.Campaigns;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.CustomTemplates;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.Messages;
import org.mq.captiway.scheduler.beans.OCSMSGateway;
import org.mq.captiway.scheduler.beans.SMSCampaignReport;
import org.mq.captiway.scheduler.beans.SMSCampaignSent;
import org.mq.captiway.scheduler.beans.SMSCampaignUrls;
import org.mq.captiway.scheduler.beans.SMSCampaigns;
import org.mq.captiway.scheduler.beans.UserSMSGateway;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.CampaignSentDao;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsLoyaltyDao;
import org.mq.captiway.scheduler.dao.CouponCodesDao;
import org.mq.captiway.scheduler.dao.MailingListDao;
import org.mq.captiway.scheduler.dao.MessagesDao;
import org.mq.captiway.scheduler.dao.MessagesDaoForDML;
import org.mq.captiway.scheduler.dao.NotSentToContactsDao;
import org.mq.captiway.scheduler.dao.OrganizationStoresDao;
import org.mq.captiway.scheduler.dao.RetailProSalesDao;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.ReplacePlaceHolders;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.exception.BaseServiceException;
import org.springframework.context.ApplicationContext;

public class SMSMultiThreadedSubmission extends Thread {

	
	private SMSRecipientProvider provider;
	private String campaignType;
	private SMSCampaigns smsCampaign;
	private Users user;
	//private CustomTemplates customTemplate;
	//private String htmlContent;
	private String textContent;
	//private Long crId;
	//private MailingList mailingList;
	/*private String supportEmailStr = "support@captiway.com";
	private String campSubject;
	*/private Set<String> totalPhSet;
	private Set<String> urlSet;
	private Set<SMSCampaignUrls> smsCampUrlList;
	private SMSCampaignReport smsCampaignReport;
	private ApplicationContext context;
	
	RetailProSalesDao retailProSalesDao = null;
	OrganizationStoresDao organizationStoresDao = null;
	ContactsLoyaltyDao contactsLoyaltyDao = null;
	CouponCodesDao couponCodesDao=null;
	CampaignSentDao campaignSentDao;
	private ContactsDao contactsDao; 
	private SMSCampaignSentDao smsCampaignSentDao;
	private MessagesDao messagesDao;
	private MessagesDaoForDML messagesDaoForDML;
	
	private boolean limit = true;
	
	private CaptiwayToSMSApiGateway captiwayToSMSApiGateway;
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER); 
	private int tempCount = 0;
	private OCSMSGateway ocSMSGateway = null;
	
	
	/*public int getTempCount() {
		
		return tempCount;
	}*/
	public SMSMultiThreadedSubmission() {
		
		
	}
	// For Sending Campaign .
	/**
	 * SMSMultiThreadedSubmission
	 * @param provider
	 * @param user
	 * @param smsCampaign
	 * @param textContent
	 * @param smsCampaignReport
	 * @param totalPhSet
	 * @param urlSet
	 * @param smsCampUrlList
	 * @param campaignType
	 * @param context
	 * @param captiwayToSMSApiGateway
	 * @param ocSMSGateway
	 */
		public SMSMultiThreadedSubmission(SMSRecipientProvider provider, Users user, SMSCampaigns smsCampaign,
				String textContent, SMSCampaignReport smsCampaignReport, Set<String> totalPhSet,Set<String> urlSet, Set<SMSCampaignUrls> smsCampUrlList,
				String campaignType, ApplicationContext context, CaptiwayToSMSApiGateway captiwayToSMSApiGateway, OCSMSGateway ocSMSGateway ) { 
			
			this.provider = provider;
			this.smsCampaign = smsCampaign;
			this.textContent = textContent;
			this.smsCampaignReport = smsCampaignReport;
			this.campaignType = campaignType;
			this.totalPhSet = totalPhSet;
			this.urlSet = urlSet;
			this.smsCampUrlList = smsCampUrlList;
			this.tempCount = 0;
			this.context = context;
			this.user = user;
			this.captiwayToSMSApiGateway = captiwayToSMSApiGateway;
			this.ocSMSGateway = ocSMSGateway;
			
		}


	public void run()  {
		logger.debug("======entered run===>1");
		
		byte msgOverSizeOption =smsCampaign.getMessageSizeOption();
		String senderId = smsCampaign.getSenderId();
		Users user = smsCampaign.getUsers();
		Long orgId = user.getUserOrganization().getUserOrgId();
		String smsCampaignName = smsCampaign.getSmsCampaignName();
		int stIndex=0;
		int size=100;
		String response = "";
		List<Contacts> retList;
		String sendingMsg = "";
		
		//String userSMSTool = user.getUserSMSTool();
		
		SMSCampaignSent smsCampaignSent = null;
		String sentId = "";
		
		ReplacePlaceHolders replacePlaceHolders  = null;
		
		
		couponCodesDao = (CouponCodesDao)context.getBean("couponCodesDao");
		campaignSentDao = (CampaignSentDao)context.getBean("campaignSentDao");
		retailProSalesDao = (RetailProSalesDao)context.getBean("retailProSalesDao");
		organizationStoresDao = (OrganizationStoresDao)context.getBean("organizationStoresDao");
		contactsLoyaltyDao = (ContactsLoyaltyDao)context.getBean("contactsLoyaltyDao");
		//captiwayToSMSApiGateway = (CaptiwayToSMSApiGateway)context.getBean("captiwayToSMSApiGateway");
		 messagesDao = (MessagesDao)context.getBean("messagesDao");
		 messagesDaoForDML = (MessagesDaoForDML )context.getBean("messagesDaoForDML");

//		 captiwayToSMSApiGateway = new CaptiwayToSMSApiGateway(context);
		
		boolean placeHolders = false;
		ArrayList<String> msgContentLst = null;
		//String msgContent = smsCampaign.getMessageContent();
		if(totalPhSet != null && totalPhSet.size() > 0) {
			
			placeHolders = true;
			replacePlaceHolders = new ReplacePlaceHolders(context, orgId, null, smsCampaignName);
			msgContentLst = splitSMSMessage(textContent, 160);
			
		}else{
			
			placeHolders = false;
			msgContentLst = splitSMSMessage(textContent, 170);
		}
		
		
		//String query = "SELECT * FROM sms_tempcontacts";
		
		
		//int tempMsgType = smsCampaign.getMessageType();// == 1) ? "N" : "L";
		String smsCampaignType = smsCampaign.getMessageType();// == 1) ? "N" : "L"; 
		//String userSMSGatewayAccDetails = userSMSGateway.getAccountDetails();
		Contacts contact = provider.getNext();
		//msgContent = msgContent.replace("|^", "[").replace("^|", "]");
		
		// Check if the contact object exists or not
    	if(contact == null ) {
    		if(logger.isInfoEnabled()) logger.info("--------- No Recipients to send SMS, hence SMS Sender Thread exiting -------"+ Thread.currentThread().getName());
    		
    		//no need to ping
    		/*limit = true;
    		captiwayToSMSApiGateway.pingGateway(userSMSTool, smsCampaignReport.getSmsCrId());*/
    		return;
    	}
    	while(contact != null) { 
		String mobileNumber = contact.getMobilePhone();
		/*if(!mobileNumber.startsWith("1") && mobileNumber.length()==10) {
			
			mobileNumber = "1"+mobileNumber;
			
		}*/
		
		smsCampaignSent = (SMSCampaignSent)contact.getTempObj();
		if(smsCampaignSent == null ) {
    		if(logger.isInfoEnabled()) logger.info("--------- No sent obj is attached to this contact, hence SMS Sender Thread exiting -------"+ Thread.currentThread().getName());
    		return;
    	}
		
		Long smsCampRepId = smsCampaignReport.getSmsCrId();
		sentId = ""+smsCampaignSent.getSentId();
		
		int lengthOfSentId = sentId.length();  
		for(int j=lengthOfSentId; j<8; j++){
			
			sentId = "0"+sentId;
			
		}
		
		if(logger.isInfoEnabled()) logger.info("sent id is=====>"+sentId+" in Thread :: "+Thread.currentThread().getName());
		if(logger.isInfoEnabled()) logger.info(mobileNumber+"  , "+contact.getEmailId());
		
		contact.setTempObj(smsCampaignSent.getSentId());
		
		
		/**
		 * if SMS contains place holders we submit individually
		 */
		if(placeHolders) { // single sending
			
			if(msgOverSizeOption == 1) { // split and send as multiple messages
				for (int i = 0; i < msgContentLst.size(); i++) {
					tempCount++;
					sendingMsg = replacePlaceHolders(msgContentLst.get(i), contact);
					String multiPartSentId = 1+sentId;
					if(msgContentLst.size() > 1)  {

						sendingMsg = "("+ (i+1) +"/"+ msgContentLst.size() +") " +sendingMsg;
						/*for(int j = lengthOfSentId; j<8; j++){
							
						sentId = "0"+sentId;
							
						}*/
						
						
						multiPartSentId = (i+1)+sentId;
					}

					if(logger.isInfoEnabled()) logger.info("msgContent--->"+sendingMsg);

					/*captiwayToSMSApiGateway.sendToSMSApi(userSMSTool, sendingMsg, mobileNumber,
							tempMsgType, multiPartSentId, "9848495956", mobileNumber, "1",
							smsCampRepId,  senderId);*/
					
					try {
						captiwayToSMSApiGateway.sendToSMSApi(ocSMSGateway,placeHolders, sendingMsg, mobileNumber,
								smsCampaignType, multiPartSentId, "9848495956", mobileNumber, "1",
								smsCampRepId,  senderId);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("exception cannot continue ");
						return;
					}

					if(logger.isInfoEnabled()) logger.info("sms sent to-----> "+contact.getFirstName());
					

				} // for each
				
				
				/*for (int i = 0; i < msgContentLst.size(); i++) {
							tempCount++;
							sendingMsg = replacePlaceHolders(msgContentLst.get(i), contact);
							
							
							if(i==0){
								
								captiwayToSMSApiGateway.sendToSMSApi(userSMSTool, sendingMsg, ""+contact.getPhone(),
										tempMsgType, sentId, "9848495956", ""+contact.getPhone(), "1",
										smsCampRepId,limit,retList.size(), senderId);
								
							}
							
							if(msgContentLst.size()>1 && i>0)  {
								
								sendingMsg = "("+ (i+1) +"/"+ msgContentLst.size() +") " +sendingMsg;
								smsCampSentChild = new SmsCampSentChild(smsCampRepId, Constants.CS_STATUS_SUCCESS,
													smsCampaignSent, contact.getContactId(), ""+contact.getPhone(), sendingMsg);
								
								smsChildList.add(smsCampSentChild);
								
							}//if
							
							logger.debug("msgContent--->"+sendingMsg);
							
							response = SMSCountryApi.sendSingleSMS(""+contact.getPhone(), sendingMsg, 
									smsCampaign.getSenderId(), tempMsgType);
							
						} // for each
*/							
				/*smsCampSentChildDao.saveByCollection(smsChildList);
				for (SmsCampSentChild smsCampSentChld : smsChildList) {
					
					captiwayToSMSApiGateway.sendToSMSApi(userSMSTool, smsCampSentChld.getSendingMessage(), ""+contact.getPhone(),
							tempMsgType,""+smsCampSentChld.getId(), "9848495956", ""+contact.getPhone(), "1",
							smsCampRepId,limit,retList.size(), senderId);
					
					
					
					
				}//for
				
				smsChildList.clear();*/
				
			}else if(msgOverSizeOption == 2) { // truncate and send single msg
				tempCount++;
				sendingMsg = replacePlaceHolders(msgContentLst.get(0), contact);
				
					
					/*response = SMSCountryApi.sendSingleSMS(""+contact.getPhone(), 
								sendingMsg, smsCampaign.getSenderId(), tempMsgType);*/
				/*captiwayToSMSApiGateway.sendToSMSApi(userSMSTool, sendingMsg, mobileNumber,
						tempMsgType, 1+sentId, "9848495956", mobileNumber, "1", 
						smsCampRepId, senderId);
				*/
				try {
					captiwayToSMSApiGateway.sendToSMSApi(ocSMSGateway,placeHolders, sendingMsg, mobileNumber,
							smsCampaignType, 1+sentId, "9848495956", mobileNumber, "1", 
							smsCampRepId, senderId);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("exception cannot continue ");
					return;
				}
				
			}else if(msgOverSizeOption == 4) { // do not send the sms if it is too long 
				if(msgContentLst.size()>1){
					Messages messages = new Messages("SMS Campaign","SMS is ignored","SMS size option is-do not " +
							"sending SMS, " +
							"the SMS is ignored from sending.",Calendar.getInstance(), "Inbox", false, "info", smsCampaign.getUsers());
					//messagesDao.saveOrUpdate(messages);
					messagesDaoForDML.saveOrUpdate(messages);
				}else{
					tempCount++;
					sendingMsg = replacePlaceHolders(msgContentLst.get(0), contact);
					
						/*response = SMSCountryApi.sendSingleSMS(""+contact.getPhone(),
									sendingMsg, smsCampaign.getSenderId(), tempMsgType);*/
				/*	captiwayToSMSApiGateway.sendToSMSApi(userSMSTool, sendingMsg, mobileNumber,
							tempMsgType, 1+sentId, "9848495956", mobileNumber, "1",
							smsCampRepId, senderId);*/
					try {
						captiwayToSMSApiGateway.sendToSMSApi(ocSMSGateway, placeHolders, sendingMsg, mobileNumber,
								smsCampaignType, 1+sentId, "9848495956", mobileNumber, "1",
								smsCampRepId, senderId);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("exception cannot continue ");
						return;
					}
					
				}
			}else if(msgOverSizeOption == 8) { // send as a long message
				tempCount += msgContentLst.size() ;
				logger.debug("size of messages "+msgContentLst.size());
				//String msgContentToBeSent = msgContent;
				String msgContentToBeSent = textContent;
				
				if(totalPhSet != null && totalPhSet.size() >0) {
		    		
					if(logger.isInfoEnabled()) logger.info("<<<<<<<<<<<<<<<calling replace place holders >>>>>>>>>>>>>>>>");
					msgContentToBeSent = replacePlaceHolders.getSMSPlaceHolders(contact, totalPhSet, urlSet, smsCampUrlList, Constants.COUP_GENT_CAMPAIGN_TYPE_SMS, 
							mobileNumber,user, smsCampaignSent.getSentId(), provider, msgContentToBeSent);
		    		
		    		
		    	}
				
				//String msgContentToBeSent = replacePlaceHolders(msgContent, contact);
				
					/*response = SMSCountryApi.sendSingleSMS(""+contact.getPhone(),
								msgContent, smsCampaign.getSenderId(), tempMsgType);*/
				/*captiwayToSMSApiGateway.sendToSMSApi(userSMSTool, msgContentToBeSent, mobileNumber,
						tempMsgType, sentId, "9848495956", mobileNumber, "1",
						smsCampRepId, senderId);*/
				
				try {
					captiwayToSMSApiGateway.sendToSMSApi(ocSMSGateway,placeHolders, msgContentToBeSent, mobileNumber,
							smsCampaignType, sentId, "9848495956", mobileNumber, "1",
							smsCampRepId, senderId);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("exception cannot continue ");
					return;
				}
				
			}//else if
		} 
		else { // else follow group submission
			
			if(msgOverSizeOption == 1) {  // split and send as multiple messages
				
				/*
				 * 
				 */
				for (int i = 0; i < msgContentLst.size(); i++) {

					tempCount++;
					sendingMsg = msgContentLst.get(i);
					String multiPartSentId = 1+sentId;

					if(msgContentLst.size()>1)  { 

						sendingMsg = "("+ (i+1) +"/"+ msgContentLst.size() +") " +sendingMsg;
						
						/*for(int j=lengthOfSentId; j<8; j++){
							
							sentId = "0"+sentId;
								
						}*/
						multiPartSentId = (i+1)+sentId;
						//sentId = smsCampaignSent.getSentId()+"000000000"+i;
					}

					
					/*captiwayToSMSApiGateway.sendToSMSApi(userSMSTool, sendingMsg, mobileNumber,
							tempMsgType, multiPartSentId, "9848495956", mobileNumber, "1",
							smsCampRepId, senderId);*/
					
					try {
						captiwayToSMSApiGateway.sendToSMSApi(ocSMSGateway, placeHolders, sendingMsg, mobileNumber,
								smsCampaignType, multiPartSentId, "9848495956", mobileNumber, "1",
								smsCampRepId, senderId);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("exception cannot continue ");
						return;
					}
					
					if(logger.isInfoEnabled()) logger.info("sms sent to-----> "+contact.getFirstName());

				} // for each
			}else if(msgOverSizeOption == 2) { // truncate and send single msg
				
				tempCount++;
				sendingMsg = msgContentLst.get(0);
				
					/*response = SMSCountryApi.sendSingleSMS(""+contact.getPhone(), sendingMsg,
								smsCampaign.getSenderId(), tempMsgType);*/
				/*captiwayToSMSApiGateway.sendToSMSApi(userSMSTool, sendingMsg, mobileNumber,
						tempMsgType, 1+sentId, "9848495956", mobileNumber, "1",
						smsCampRepId, senderId);*/
				
				try {
					captiwayToSMSApiGateway.sendToSMSApi(ocSMSGateway,placeHolders, sendingMsg, mobileNumber,
							smsCampaignType, 1+sentId, "9848495956", mobileNumber, "1",
							smsCampRepId, senderId);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("exception cannot continue ");
					return;
				}
				
				
			}else if(msgOverSizeOption == 4) { // do not send the sms if it is too long 
				if(msgContentLst.size()>1){
					Messages messages = new Messages("SMS Campaign","SMS is ignored","SMS size option is-do not " +
							"sending SMS, " +
							"the SMS is ignored from sending.", Calendar.getInstance(), "Inbox", false, "info", smsCampaign.getUsers());
					//messagesDao.saveOrUpdate(messages);
					messagesDaoForDML.saveOrUpdate(messages);
				}else{
					tempCount++;
					sendingMsg = msgContentLst.get(0);
					
						/*response = SMSCountryApi.sendSingleSMS(""+contact.getPhone(), sendingMsg,
								smsCampaign.getSenderId(), tempMsgType);*/
					/*captiwayToSMSApiGateway.sendToSMSApi(userSMSTool, sendingMsg, mobileNumber,
							tempMsgType, 1+sentId, "9848495956", mobileNumber, "1",
							smsCampRepId, senderId);*/
					
					try {
						captiwayToSMSApiGateway.sendToSMSApi(ocSMSGateway,placeHolders, sendingMsg, mobileNumber,
								smsCampaignType, 1+sentId, "9848495956", mobileNumber, "1",
								smsCampRepId, senderId);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("exception cannot continue ");
						return;
					}
					
				}//else
			}else if(msgOverSizeOption == 8){ // send as a long msg
				
				
				tempCount += msgContentLst.size() ;
				
				
				String msgContentToBeSent = textContent;
				/*
				if(totalPhSet != null && totalPhSet.size() >0) {
		    		
					logger.info("<<<<<<<<<<<<<<<calling replace place holders >>>>>>>>>>>>>>>>");
					
		    		String [] contentsStrArr = replacePlaceHolders.getContactPhValue(contact, msgContent,
		    				"", "", totalPhSet, Constants.COUP_GENT_CAMPAIGN_TYPE_SMS, ""+contact.getPhone());
		    		
		    		
		    		
		    		msgContentToBeSent = contentsStrArr[0];
		    		//tempTextContent = contentsStrArr[1];
		    		//tempCampSubject = contentsStrArr[2];
		    		
		    	}
				*/
				
				
				
					/*response = SMSCountryApi.sendSingleSMS(""+contact.getPhone(),
							msgContent, smsCampaign.getSenderId(), tempMsgType);*/
				/*captiwayToSMSApiGateway.sendToSMSApi(userSMSTool, msgContentToBeSent, mobileNumber,
						tempMsgType, sentId, "9848495956", mobileNumber, "1",
						smsCampRepId, senderId);*/
				try {
					captiwayToSMSApiGateway.sendToSMSApi(ocSMSGateway,placeHolders, msgContentToBeSent, mobileNumber,
							smsCampaignType, sentId, "9848495956", mobileNumber, "1",
							smsCampRepId, senderId);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("exception cannot continue ");
					return;
				}
				
			}//else if
				
		} // else
		
			contact = provider.getNext();
			if(contact == null ) {
	    		if(logger.isInfoEnabled()) logger.info("--------- No Recipients to send SMS, hence SMS Sender Thread exiting -------"+ Thread.currentThread().getName());
	    		
	    		limit = true;
	    		//captiwayToSMSApiGateway.pingGateway(userSMSTool, smsCampaignReport.getSmsCrId(), (smsCampaign.getMessageType() == (byte)3));
	    		try {
					captiwayToSMSApiGateway.pingGateway(ocSMSGateway, smsCampaignReport.getSmsCrId(), senderId);
					
				} catch (BaseServiceException e) {
					// TODO Auto-generated catch block
					logger.error("got exception in final updation", e);
				}
	    		break;
	    	}
			
    	}

		/*if(response.equalsIgnoreCase("Messages Sent Successfully")) {
			
			smsCampSentList.add(new SMSCampaignSent(contact.getPhone(), 
					smsCampaignReport, Constants.CS_STATUS_SUCCESS,contact.getContactId()));
		}
		else {
			smsCampSentList.add(new SMSCampaignSent(contact.getPhone(),
					smsCampaignReport, Constants.CS_STATUS_FAILURE, contact.getContactId()));
		}*/
				
    	provider.addTempCount(tempCount);
		logger.info("thread "+Thread.currentThread().getName()+" given "+tempCount+" credits");
	}
	
	
	private static final String plArr[] ={"|^GEN_firstName^|", "|^GEN_lastName^|", 
		"|^GEN_phone^|", "|^GEN_emailId^|", "|^GEN_addressOne^|",
		"|^GEN_addressTwo^|", "|^GEN_city^|","|^GEN_state^|", 
		"|^GEN_country^|", "|^GEN_pin^|"};

	
	/**
	 * this method replaces the placeholder value with the contact value
	 * @param orgMsg
	 * @param contact
	 * @return
	 */
	private String replacePlaceHolders(String orgMsg, Contacts contact) {
		
		String retMsg = orgMsg;
		
		for (String ph : plArr) {
		
			String cfStr = ph.substring(ph.indexOf('^')+1, ph.lastIndexOf('^'));
			String value;
		
			if(cfStr.startsWith("GEN_")) {
				
				cfStr = cfStr.substring(4);
				if(cfStr.equalsIgnoreCase("emailId"))	value = contact.getEmailId();
				else if(cfStr.equalsIgnoreCase("firstName"))	value = contact.getFirstName();
				else if(cfStr.equalsIgnoreCase("lastName"))	value = contact.getLastName();
				else if(cfStr.equalsIgnoreCase("addressOne"))	value = contact.getAddressOne();
				else if(cfStr.equalsIgnoreCase("addressTwo"))	value = contact.getAddressTwo();
				else if(cfStr.equalsIgnoreCase("city"))	value = contact.getCity();
				else if(cfStr.equalsIgnoreCase("state"))	value = contact.getState();
				else if(cfStr.equalsIgnoreCase("country"))	value = contact.getCountry();
				else if(cfStr.equalsIgnoreCase("pin"))	value = contact.getZip() ;
				else if(cfStr.equalsIgnoreCase("phone"))	value = contact.getMobilePhone();
				else if(cfStr.equalsIgnoreCase("zip"))	value = contact.getZip() ;
				else if(cfStr.equalsIgnoreCase("mobile"))	value = contact.getMobilePhone();
				else value = "";
				
				if(logger.isInfoEnabled()) logger.info("Gen token :" + cfStr + " - Value :" + value);
				try {
					if(value == null || value.trim().length() == 0) {
						retMsg = retMsg.replace(ph, "");
					}
					else if	(value != null && value.trim().length() > 0) {
					
						retMsg = retMsg.replace(ph, value);
					}
					
				} catch (Exception e) {
					logger.error("Exception ::::" , e);
				}
			} // if(cfStr.startsWith("GEN_"))
	
		
		} // for 
		
		return retMsg;
	} // replacePlaceHolders
	
	
	
	
	/**
	 * this method splits the sms if its exceeds the specified length
	 * @param msgContent
	 * @param size
	 * @return
	 */
	private ArrayList<String> splitSMSMessage(String msgContent, int size) {
		
		try {
			ArrayList<String> retList = new ArrayList<String>();//to store the message tokens
			
			int skipCounter=0;//to indicate the max number of message tokens
			
			do {
				skipCounter++;
				
				if(msgContent.length() > size-10) {
					
					int endInd = msgContent.indexOf(' ', size-10);
					
					if(endInd==-1 || endInd > size-5) {
						endInd = msgContent.lastIndexOf(' ', size-10);
					}
					
					if(endInd==-1) {
						if(logger.isInfoEnabled()) logger.info("No Spaces in the Given Token");
						break;
					}

					/*
					 * While splitting, if it is inside the Place holder
					 * then find out for a new space before/after from that position for split
					 */  
					
					
					if(msgContent.lastIndexOf("|^", endInd) != -1 && 
							msgContent.indexOf("^|", endInd) != -1 &&
							(endInd > msgContent.lastIndexOf("|^", endInd)) && 
							(endInd < msgContent.indexOf("^|", endInd)) )  {
						
						
						int phStInd = msgContent.lastIndexOf("|^", endInd);
						int phEnInd = msgContent.indexOf("^|", endInd);
						
						int tempEndInd=endInd;
						
						if(msgContent.substring(phStInd+2, endInd).indexOf("^|")==-1) {
							endInd = msgContent.lastIndexOf(' ', phStInd);
						}
						
						if(endInd==-1 && (msgContent.substring(tempEndInd, phEnInd).indexOf("|^")==-1)) {
							endInd = msgContent.indexOf(' ', phEnInd+1);
						}
						if(endInd==-1)	{
							break;
						}
						
					} // if
					
					String tempStr = msgContent.substring(0,endInd);
					retList.add(tempStr);
					
					msgContent = msgContent.substring(endInd+1);
					
					
				} //if
				
				if(skipCounter>40) {
					break;
				}
			} while(msgContent.length() > size-10);
			
			if(msgContent.length() > 0) {
				retList.add(msgContent);
			}
			
			return retList;
			
		} catch (Exception e) {
			logger.error("** Error occured while submitting the SMS campaigns",e);
			return null;
		}
		
	} // splitSMSMessage
	
	
	
	
	
}
