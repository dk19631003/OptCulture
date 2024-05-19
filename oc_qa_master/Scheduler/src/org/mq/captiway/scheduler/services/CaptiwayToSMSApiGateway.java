package org.mq.captiway.scheduler.services;

import java.io.IOException;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.CharacterCodes;
import org.mq.captiway.scheduler.beans.ClickaTellSMSInbound;
import org.mq.captiway.scheduler.beans.OCSMSGateway;
import org.mq.captiway.scheduler.beans.SMSBounces;
import org.mq.captiway.scheduler.beans.SMSCampReportLists;
import org.mq.captiway.scheduler.beans.SMSCampaignReport;
import org.mq.captiway.scheduler.beans.SMSCampaignSent;
import org.mq.captiway.scheduler.beans.SMSDeliveryReport;
import org.mq.captiway.scheduler.beans.SMSSettings;
import org.mq.captiway.scheduler.beans.SMSSuppressedContacts;
import org.mq.captiway.scheduler.beans.UserOrganization;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.CharacterCodesDao;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.SMSBouncesDao;
import org.mq.captiway.scheduler.dao.SMSBouncesDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampReportListsDao;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDao;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDao;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDaoForDML;
import org.mq.captiway.scheduler.dao.SMSDeliveryReportDao;
import org.mq.captiway.scheduler.dao.SMSDeliveryReportDaoForDML;
import org.mq.captiway.scheduler.dao.SMSSuppressedContactsDao;
import org.mq.captiway.scheduler.dao.SMSSuppressedContactsDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.ReplacePlaceHolders;
import org.mq.captiway.scheduler.utility.SMSStatusCodes;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.springframework.context.ApplicationContext;

public class CaptiwayToSMSApiGateway  {
	
	
	
	
	//private String userName,password;
	//private String userSMSTool;
	//private int smsCount;
	
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER); 

	
	
	private ClickaTellApi clickaTellApi;
	private CellNextApi cellNextApi;
	private NetCoreApi netCoreApi;
	private MVaayooApi mVaayooApi;
	private List<CharacterCodes> charCodes;
	private UnicelSMSGateway unicelSMSGatewayObj ;
	private CMComSMSGateway CMComSMSGatewayObj ;
	private PakistanGateway pakistanGatewayObj;
	private OutreachPakistanGateway outreachPakistanGatewayObj;
	private BsmsItsPakistanGateway bsmsItsPakistanGateway;
	private InfobipSMSGateway infobipSMSGateway;
	private EquenceSMSGateway equenceSMSGateway;
	private SynapseSMSGateway synapseSMSGateway;
	private InfocommSMSGateway infocommSMSGateway;
	//ops-433
	private MessagebirdSMSgateway messagebirdSMSGateway;
	
	private LinkedHashMap<String, String> mobileSentIdMap;
	private volatile Long startSentId, endSentId;
	private volatile Long startSentIdOutreach, endSentIdOutreach;
	private volatile Set<Long> outreachSentIdsSet = new HashSet<Long>();//1--Polo rl issue
	private volatile Set<Long> bsmsItsSentIdsSet = new HashSet<Long>();
	

	

	public synchronized Long getStartSentIdOutreach() {
		return startSentIdOutreach;
	}

	public synchronized void setStartSentIdOutreach(Long startSentIdOutreach) {
		this.startSentIdOutreach = startSentIdOutreach;
	}

	public synchronized Long getStartSentId() {
		return startSentId;
	}

	public synchronized void setStartSentId(Long startSentId) {
		this.startSentId = startSentId;
	}




	private ApplicationContext context;
	private SMSCampaignSentDao smsCampaignSentDao;
	private SMSCampaignSentDaoForDML smsCampaignSentDaoForDML;
	private SMSDeliveryReportDao smsDeliveryReportDao;
	private SMSDeliveryReportDaoForDML smsDeliveryReportDaoForDML;
	private SMSSuppressedContactsDao smsSuppressedContactsDao;
	private SMSSuppressedContactsDaoForDML smsSuppressedContactsDaoForDML;
	private SMSCampaignReportDao smsCampaignReportDao;
	private SMSCampaignReportDaoForDML smsCampaignReportDaoForDML;
	private SMSBouncesDao smsBouncesDao;
	private SMSBouncesDaoForDML smsBouncesDaoForDML;
	private ContactsDao contactsDao;
	private ContactsDaoForDML contactsDaoForDML;
	public CaptiwayToSMSApiGateway() {
		//this.smsCount = 0;
	}
	
	/*public CaptiwayToSMSApiGateway(String userSMSTool, ApplicationContext context) {
		//this.smsCount = 0;
		
		if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CLICKATELL)) {
			
			this.clickaTellApi = new ClickaTellApi();
			
			
			
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CELLNEXT)) {
			
			this.cel = new ClickaTellApi();
			
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_NETCORE)) {
			
			
			
		}
		
		
	}*/
	
	
	public CaptiwayToSMSApiGateway(ApplicationContext context) {
		//this.smsCount = 0;
		this.context = context;
		 contactsDao = (ContactsDao)context.getBean("contactsDao");
		 contactsDaoForDML = (ContactsDaoForDML)context.getBean("contactsDaoForDML");
		 smsCampaignSentDao = (SMSCampaignSentDao)context.getBean("smsCampaignSentDao");
		 smsCampaignSentDaoForDML  = (SMSCampaignSentDaoForDML )context.getBean("smsCampaignSentDaoForDML");
		 smsCampaignReportDao = (SMSCampaignReportDao)context.getBean("smsCampaignReportDao");
		 smsCampaignReportDaoForDML = (SMSCampaignReportDaoForDML)context.getBean("smsCampaignReportDaoForDML");
		 smsDeliveryReportDao = (SMSDeliveryReportDao)context.getBean("smsDeliveryReportDao");
		 smsDeliveryReportDaoForDML = (SMSDeliveryReportDaoForDML)context.getBean("smsDeliveryReportDaoForDML");
		 smsSuppressedContactsDao = (SMSSuppressedContactsDao)context.getBean("smsSuppressedContactsDao");
		 smsSuppressedContactsDaoForDML = (SMSSuppressedContactsDaoForDML)context.getBean("smsSuppressedContactsDaoForDML");
		 smsBouncesDao = (SMSBouncesDao)context.getBean("smsBouncesDao");
		 smsBouncesDaoForDML = (SMSBouncesDaoForDML)context.getBean("smsBouncesDaoForDML");

		
		
	}
	
	
	
	/*public CaptiwayToSMSApiGateway(NetCoreApi netCoreApi) {
		this.netCoreApi = netCoreApi;
	}
	
	private ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	
	public CellNextApi getCellNextApi() {
		return cellNextApi;
	}

	public void setCellNextApi(CellNextApi cellNextApi) {
		this.cellNextApi = cellNextApi;
	}
	
	private NetCoreApi netCoreApi;
	
	public NetCoreApi getnetCoreApi() {
		return netCoreApi;
	}

	public void setnetCoreApi(NetCoreApi netCoreApi) {
		this.netCoreApi = netCoreApi;
	}
	
	private ClickaTellApi clickaTellApi;
	
	public ClickaTellApi getClickaTellApi() {
		return clickaTellApi;
	}

	public void setClickaTellApi(ClickaTellApi clickaTellApi) {
		this.clickaTellApi = clickaTellApi;
	}

	
	
	public SMSCampaignSentDao getSmsCampaignSentDao() {
		return smsCampaignSentDao;
	}

	public void setSmsCampaignSentDao(SMSCampaignSentDao smsCampaignSentDao) {
		this.smsCampaignSentDao = smsCampaignSentDao;
	}
	
	
	
	public SMSDeliveryReportDao getSmsDeliveryReportDao() {
		return smsDeliveryReportDao;
	}

	public void setSmsDeliveryReportDao(SMSDeliveryReportDao smsDeliveryReportDao) {
		this.smsDeliveryReportDao = smsDeliveryReportDao;
	}
	
	
	
	
	public SMSSuppressedContactsDao getSmsSuppressedContactsDao() {
		return smsSuppressedContactsDao;
	}

	public void setSmsSuppressedContactsDao(SMSSuppressedContactsDao smsSuppressedContactsDao) {
		this.smsSuppressedContactsDao = smsSuppressedContactsDao;
	}


	public SMSCampaignReportDao getSmsCampaignReportDao() {
		return smsCampaignReportDao;
	}

	public void setSmsCampaignReportDao(SMSCampaignReportDao smsCampaignReportDao) {
		this.smsCampaignReportDao = smsCampaignReportDao;
	}
	
	
	
	
	public SMSBouncesDao getSmsBouncesDao() {
		return smsBouncesDao;
	}

	public void setSmsBouncesDao(SMSBouncesDao smsBouncesDao) {
		this.smsBouncesDao = smsBouncesDao;
	}

	
	
	
	
	
	
	public ContactsDao getContactsDao() {
		return contactsDao;
	}

	public void setContactsDao(ContactsDao contactsDao) {
		this.contactsDao = contactsDao;
	}
	*/
	
	public String getGatewaySpecificRequestIdToPUllReports(String userSMSTool) {
		
		if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MVAYOO)) {
			
			return "bcamp_"+MyCalendar.calendarToString(Calendar.getInstance(),
					MyCalendar.FORMAT_DATEONLY_WITHOUT_DELIMETER)+"_89128" ;
			
		}if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_PAKISTAN) || userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_OUTREACH)) {
			
			//TODO this request id is the transaction id that will be generated for each submission(i.e for every chunk)
			//for a single record in this table we can not store those many transaction ids.
			//Hence we have to depend on the report id, i.e no request id will be there for all such gateways.
			return null;
			
		}
		
		return null;
		
	}//getGatewaySpecificRequestIdToPUllReports
	
	
	public void pingGateway( OCSMSGateway ocSMSGateway, Long smsCampRepId, String senderID,String templateRegisteredId) throws BaseServiceException{
		String userSMSTool = ocSMSGateway.getGatewayName();
			
		if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CLICKATELL)) {
			if(clickaTellApi == null) {
				
				clickaTellApi = new ClickaTellApi();
				
			}
			// need to convert the NEtCore understandable format
			List<String> responseList = clickaTellApi.pingClickatellToSendRestOfSMS();
			if(responseList != null && responseList.size()>0){
				
				/*
				 * This hasbeen commented to avoid the insertion of the SMSdelivery 
				 * report which is related to the fetching of the reports as per the old design.
				 * It may useful as the alternative of the fetching process in which captiway only
				 * connects to NetCore and requests the required reports unlike the ping back/push back URL. 
				 */
				
				
				//saveRequest(responseList.get(0).split("\\|")[2], smsCampRepId);
				updateInitialStatusFromClikaTell(responseList,smsCampRepId);
			}
			
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MVAYOO)) {
			if(mVaayooApi == null) {
				
				mVaayooApi = new MVaayooApi(ocSMSGateway );
				
			}
			 mVaayooApi.pingMVaayooToSendRestOfSMS();
			
			
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_UNICEL)) {
			if(unicelSMSGatewayObj == null) {
				
				unicelSMSGatewayObj = new UnicelSMSGateway(ocSMSGateway );
				
			}
			unicelSMSGatewayObj.pingUnicelToSendRestOfSMS();
			
			
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_PAKISTAN)) {
			if(pakistanGatewayObj == null) {
				
				pakistanGatewayObj = new PakistanGateway(ocSMSGateway );
				
			}
			//request comes to here from the first thread of multisubmission when it finds no receipient(contact object) 
			//for the last chunk we have to intimate the gateway to proceed sending the rest of the SMS(must be <=100)
			try {
				
				String[] responseArr = pakistanGatewayObj.submitFinalChunk(senderID);
				if(responseArr != null) {
					UpdateInitialStatusFromSMS4Connect(getStartSentId(), endSentId, responseArr, smsCampRepId);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ", e);
			}
			
			
		}else if(Constants.USER_SMSTOOL_INFOBIP.equalsIgnoreCase(userSMSTool)){
			if(infobipSMSGateway == null){
				infobipSMSGateway  = new InfobipSMSGateway();
			}
			infobipSMSGateway.pingInfobipToSendRestOfSMS();
			
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_OUTREACH)) {
			if(outreachPakistanGatewayObj == null) {
				
				outreachPakistanGatewayObj = new OutreachPakistanGateway(ocSMSGateway );
				
			}
			//request comes to here from the first thread of multisubmission when it finds no receipient(contact object) 
			//for the last chunk we have to intimate the gateway to proceed sending the rest of the SMS(must be <=100)
			try {
				
				String[] responseArr = outreachPakistanGatewayObj.submitFinalChunk(senderID);
				if(responseArr != null) {
					UpdateInitialStatusFromOutreach(getStartSentIdOutreach(), endSentIdOutreach, responseArr, smsCampRepId);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ", e);
			}
			
			
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_BSMS_ITS_PAKISTAN)) {
			if(bsmsItsPakistanGateway == null) {
				
				bsmsItsPakistanGateway = new BsmsItsPakistanGateway(ocSMSGateway );
				
			}
			//request comes to here from the first thread of multisubmission when it finds no receipient(contact object) 
			//for the last chunk we have to intimate the gateway to proceed sending the rest of the SMS(must be <=100)
			try {
				
				Map<String,String> responseMap = bsmsItsPakistanGateway.submitFinalChunk(senderID);
				if(responseMap != null) {
					UpdateInitialStatusFromBsmsItsPakistan(responseMap, smsCampRepId);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ", e);
			}
			
			
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_EQUENCE)) {
			
			if(equenceSMSGateway == null){
				equenceSMSGateway = new EquenceSMSGateway(ocSMSGateway,ocSMSGateway.getUserId(),ocSMSGateway.getPwd(),senderID,ocSMSGateway.getPrincipalEntityId());
			}
			equenceSMSGateway.pingToSendRestOfSMS(smsCampRepId,templateRegisteredId);
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_SYNAPSE)) {
			
			if(synapseSMSGateway == null){
				synapseSMSGateway = new SynapseSMSGateway(ocSMSGateway,ocSMSGateway.getUserId(),ocSMSGateway.getPwd(),senderID);
			}
			synapseSMSGateway.pingToSendRestOfSMS(smsCampRepId);
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_INFOCOMM)) {
			
			if(infocommSMSGateway == null){
				infocommSMSGateway = new InfocommSMSGateway(ocSMSGateway,ocSMSGateway.getUserId(),ocSMSGateway.getPwd(),senderID);
			}
			infocommSMSGateway.pingToSendRestOfSMS(smsCampRepId);
		}
		//OPS-433
		else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MESSAGEBIRD)) {
			
			if(messagebirdSMSGateway == null){
				messagebirdSMSGateway = new MessagebirdSMSgateway(ocSMSGateway,ocSMSGateway.getUserId(),ocSMSGateway.getPwd(),senderID,null);
			}
			messagebirdSMSGateway.pingToSendRestOfSMS(smsCampRepId,templateRegisteredId);
		}
		
		
		
		
		
		
	}
	
	
	
	/**
	 * this method send the sms to user configured SMS gateway
	 * @param user
	 * @param messageContent
	 * @param mobileNumber
	 * @param msgType
	 * @param contactId
	 * @param fromMobNum
	 * @param toMobNum
	 * @param msgSeq
	 */
	/*public  void sendToSMSApi(String userSMSTool, String rawMessageContent, String mobileNumber, 
					int msgType, String sentId, String fromMobNum, String toMobNum, String msgSeq, Long smsCampRepId,  String senderId) {
	*/	
	/*public  void sendToSMSApi(String userSMSGatewayDetails, String rawMessageContent, String mobileNumber, 
			String msgType, String sentId, String fromMobNum, String toMobNum, String msgSeq, Long smsCampRepId,  String senderId) {
*/
	public  void sendToSMSApi(OCSMSGateway ocsmsGateway, boolean isPersonalized,  String rawMessageContent, String mobileNumber, 
			String msgType, String sentId, String fromMobNum, String toMobNum, 
			String msgSeq, Long smsCampRepId,  String senderId,String templateRegisteredId) throws Exception{
	
		
		try {
			//******** need to write code to send the request to appropriate API based on the user configurations ******
			
			//String[] userSMSGatewayDetailsTokenArr = userSMSGatewayDetails.split(Constants.ADDR_COL_DELIMETER);
			
			/*String userSMSTool = userSMSGatewayDetailsTokenArr[0];
			String userID = userSMSGatewayDetailsTokenArr[1];
			String pwd = userSMSGatewayDetailsTokenArr[2];
			*/
			logger.debug("======entered sendToSMSApi===>2");
			String userSMSTool = ocsmsGateway.getGatewayName();
			String userID = ocsmsGateway.getUserId();
			String pwd = ocsmsGateway.getPwd();
			String apiID = ocsmsGateway.getAPIId();
			
			//this.userSMSTool = user.getSMSTool();//<----no need to fetch from DB;need to define it in users bean
			
			if(logger.isDebugEnabled()) {
				logger.debug(">>>>>>> Started CaptiwayToSMSApiGateway :: sendToSMSApi <<<<<<< ");
				logger.debug("got the message content is === >" +rawMessageContent);
			}
			String response = "";
			List<String> responseList = null;
			if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_SMSCOUNTRY)) {
				//TODO need to convert the request into the SMSCountryAPI understandable format
				//and send a requset to send SMS
				
				
			}
			else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CELLNEXT)) {
				//call the method to prepare the data into xml format
				
				// response = cellNextApi.prepareData(messageContent, msgType, sentId, fromMobNum, toMobNum, msgSeq);
				
				
				//update the status in SMSCampaignSent
				//updateSMSCampStatus(response, sentId);
				
				
				}
			
			else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_NETCORE)) {
				
				//***** need tao convert NetCore understandable format*****
				/*responseList = netCoreApi.prepareData(messageContent, msgType, sentId, fromMobNum, toMobNum, 
								msgSeq, smsCampRepId, limit, size, senderId);*/
				if(responseList != null && responseList.size()>0) {
					
					/*
					 * This hasbeen commented to avoid the insertion of the SMSdelivery 
					 * report which is related to the fetching of the reports as per the old design.
					 * It may useful as the alternative of the fetching process in which captiway only
					 * connects to NetCore and requests the required reports unlike the ping back/push back URL. 
					 */
					
					
					//saveRequest(responseList.get(0).split("\\|")[2], smsCampRepId);
					updateSMSCampSentInitialStatus(responseList,smsCampRepId);
				}
				
				
			}
			else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CLICKATELL)) {
				// need to convert the NEtCore understandable format
				if(clickaTellApi == null) {
					
					clickaTellApi = new ClickaTellApi();
					
				}
				
				// 	rawMessageContent = StringEscapeUtils.escapeHtml(rawMessageContent);
					String hasUnicode = "0";
				   for (int i = 0; i < rawMessageContent.length(); i++) {
				        
						int codePoint = rawMessageContent.codePointAt(i);
				        
						if (codePoint > 127) {
							hasUnicode = "1";
						}
						}
				//  String outputString = Constants.STRING_NILL;
					String messageContent =  Constants.STRING_NILL;
					if(hasUnicode.equalsIgnoreCase("1")) {
						
				try {
				byte[] ar = rawMessageContent.getBytes(StandardCharsets.UTF_16BE);
			 /*outputString = new String(ar, StandardCharsets.UTF_16LE);
			 
				System.out.println("utf16 :"+outputString);  
				for (int j = 0; j < ar.length; j++)   
				{  
				System.out.print(ar[j]);  
				}  
*/

			
				StringBuilder sb = new StringBuilder();
			    for (byte b : ar) {
			        sb.append(String.format("%02X", b));
			    }

			   messageContent =  sb.toString();
				}catch(Exception e) {
			
				System.out.println("Exception while changing in byte"+ e);
				}
		//	    messageContent =  StringEscapeUtils.escapeXml(messageContent);
		//let test for all the type of chars if not worked then put escapehtml.escapexml unless not required as the test was being replaced in their portal
				 logger.info("Original message for xml structure Miércoles , Mañana for testing tildes- 3846:"+rawMessageContent);//+"Escaped xml code" +messageContent);
								
				logger.debug("sending the utf 16be format if t : "+messageContent);
				}
					else { // if not unicode : then just do html escape and send.
						
						 messageContent = StringEscapeUtils.escapeHtml(rawMessageContent);
						 logger.info("String donesn't has any unicode.\n"+ messageContent);

					}
				
				responseList =	clickaTellApi.prepareData(messageContent, msgType, sentId,  toMobNum, 
						msgSeq, smsCampRepId, senderId, userID, pwd, apiID,hasUnicode);
				
				
				if(responseList != null && responseList.size()>0){
					
					/*
					 * This hasbeen commented to avoid the insertion of the SMSdelivery 
					 * report which is related to the fetching of the reports as per the old design.
					 * It may useful as the alternative of the fetching process in which captiway only
					 * connects to NetCore and requests the required reports unlike the ping back/push back URL. 
					 */
					
					
					//saveRequest(responseList.get(0).split("\\|")[2], smsCampRepId);
					updateInitialStatusFromClikaTell(responseList,smsCampRepId);
				}
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_PAKISTAN)) {
				// need to convert the NEtCore understandable format
				if(pakistanGatewayObj == null) {
					
					pakistanGatewayObj = new PakistanGateway(ocsmsGateway);
					//if(mobileSentIdMap == null) mobileSentIdMap = new LinkedHashMap<String, String>(); 
				}
				//TODO to test
				Long sentIdL = Long.parseLong(sentId);
				if(getStartSentId() == null) setStartSentId(sentIdL); 
				endSentId = sentIdL;
				String messageContent = StringEscapeUtils.escapeHtml(rawMessageContent);
				
				String[] responseArr = pakistanGatewayObj.prepareData(isPersonalized, messageContent, toMobNum, senderId);
				if(responseArr != null){
					UpdateInitialStatusFromSMS4Connect(getStartSentId(), sentIdL, responseArr, smsCampRepId);	
				}
				
			}
			else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MVAYOO)) {
				// need to convert the NEtCore understandable format
				
				//boolean proceedWithHTTP = false;
				if(mVaayooApi == null) {
					
					mVaayooApi = new MVaayooApi(ocsmsGateway);
					
				}
				//if(msgType == (byte)3) {
				//String messageContent = StringEscapeUtils.escapeHtml(rawMessageContent);
				
				String messageContent =  replaceSpecialCharacters(rawMessageContent );
				
				if(msgType.equals(Constants.SMS_TYPE_TRANSACTIONAL)) {
					
					mVaayooApi.sendTransactionalSMSOverSMPP(messageContent, toMobNum, Long.parseLong(sentId), senderId);
					
				}else{
					
					mVaayooApi.sendSMSOverSMPP(messageContent, toMobNum, Long.parseLong(sentId), senderId);
					
					
					
				}
					
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CM)) {
				// need to convert the mVaayooApi understandable format
				if(CMComSMSGatewayObj == null) {
					
					CMComSMSGatewayObj = new CMComSMSGateway( ocsmsGateway);
				}
				String messageContent =  replaceSpecialCharacters(rawMessageContent );
				
				//String messageContent = StringEscapeUtils.escapeHtml(rawMessageContent);
				
				CMComSMSGatewayObj.sendSMSOverSMPP(messageContent, toMobNum, Long.parseLong(sentId), senderId);
			}//Added For Infobip
			else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_UNICEL)) {
				// need to convert the mVaayooApi understandable format
				if(unicelSMSGatewayObj == null) {
					
					unicelSMSGatewayObj = new UnicelSMSGateway( ocsmsGateway);
				}
				String messageContent =  replaceSpecialCharacters(rawMessageContent );
				
				//String messageContent = StringEscapeUtils.escapeHtml(rawMessageContent);
				
				unicelSMSGatewayObj.sendSMSOverSMPP(messageContent, toMobNum, Long.parseLong(sentId), senderId);
			}//Added For Infobip
			else if(Constants.USER_SMSTOOL_INFOBIP.equalsIgnoreCase(userSMSTool)){
				
				if(infobipSMSGateway == null){
					
					infobipSMSGateway = new InfobipSMSGateway(ocsmsGateway);
					
				}
				
				String messageContent =  replaceSpecialCharacters(rawMessageContent );
			    logger.debug("Infobip trying to send sms over smpp ...messageContent"+messageContent+".... toMobNum"+toMobNum);
				infobipSMSGateway.sendSMSOverSMPP(messageContent, toMobNum,  Long.parseLong(sentId), senderId);
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_OUTREACH)) {
				// need to convert the NEtCore understandable format
				if(outreachPakistanGatewayObj == null) {
					
					outreachPakistanGatewayObj = new OutreachPakistanGateway(ocsmsGateway);
					//if(mobileSentIdMap == null) mobileSentIdMap = new LinkedHashMap<String, String>(); 
				}
				
				
				//TODO to test
				Long sentIdL = Long.parseLong(sentId);
				
				
				outreachSentIdsSet.add(sentIdL);//2--Polo rl issue
				
				/*if(getStartSentIdOutreach() == null) setStartSentIdOutreach(sentIdL); 
				endSentIdOutreach = sentIdL;*/
				
				String messageContent = StringEscapeUtils.escapeHtml(rawMessageContent);
				
				String[] responseArr = outreachPakistanGatewayObj.prepareData(isPersonalized, messageContent, toMobNum, senderId);
				if(responseArr != null){
					UpdateInitialStatusFromOutreach(getStartSentIdOutreach(), sentIdL, responseArr, smsCampRepId);	
				}
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_BSMS_ITS_PAKISTAN)) {
				// need to convert the NEtCore understandable format
				if(bsmsItsPakistanGateway == null) {
					
					bsmsItsPakistanGateway = new BsmsItsPakistanGateway(ocsmsGateway);
					//if(mobileSentIdMap == null) mobileSentIdMap = new LinkedHashMap<String, String>(); 
				}
				
				
				//TODO to test
				Long sentIdL = Long.parseLong(sentId);
				
				
				bsmsItsSentIdsSet.add(sentIdL);
				
				/*if(getStartSentIdOutreach() == null) setStartSentIdOutreach(sentIdL); 
				endSentIdOutreach = sentIdL;*/
				
				String messageContent = StringEscapeUtils.escapeHtml(rawMessageContent);
				
				Map<String,String> responseMap = bsmsItsPakistanGateway.prepareData(isPersonalized, messageContent, toMobNum, senderId);
				if(responseMap != null){
					UpdateInitialStatusFromBsmsItsPakistan(responseMap, smsCampRepId);
				}
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_EQUENCE)) {
				
				if(equenceSMSGateway == null) {
					
					equenceSMSGateway = new EquenceSMSGateway(ocsmsGateway,userID,pwd,senderId,ocsmsGateway.getPrincipalEntityId());
				}
				//String messageContent = StringEscapeUtils.escapeHtml(rawMessageContent);

				List<String> eqResponseList=
				//equenceSMSGateway.sendOverHTTP(replaceSpecialCharacters(rawMessageContent),toMobNum,sentId,smsCampRepId);
				equenceSMSGateway.sendOverHTTP(rawMessageContent,toMobNum,sentId,smsCampRepId,templateRegisteredId);
				/*if(eqResponseList != null && eqResponseList.size()>0){
					
					updateInitialStatusFromEquence(eqResponseList,smsCampRepId,sentId,toMobNum);
				}*/
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_SYNAPSE)) {
				
				if(synapseSMSGateway == null) {
					
					synapseSMSGateway = new SynapseSMSGateway(ocsmsGateway,userID,pwd,senderId);
				}
				//String messageContent = StringEscapeUtils.escapeHtml(rawMessageContent);

				synapseSMSGateway.sendOverHTTP(rawMessageContent,toMobNum,sentId,smsCampRepId,isPersonalized);
			}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_INFOCOMM)) {
				
				if(infocommSMSGateway == null) {
					
					infocommSMSGateway = new InfocommSMSGateway(ocsmsGateway,userID,pwd,senderId);
				}
				responseList =	infocommSMSGateway.prepareData(rawMessageContent, msgType, sentId,  toMobNum, 
						msgSeq, smsCampRepId, senderId, userID, pwd,isPersonalized);
			}
			
			else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MESSAGEBIRD)) {
				
				if(messagebirdSMSGateway == null) {
					
					messagebirdSMSGateway = new MessagebirdSMSgateway(ocsmsGateway,userID,pwd,senderId,ocsmsGateway.getPrincipalEntityId());
				}
				//String messageContent = StringEscapeUtils.escapeHtml(rawMessageContent);

				List<String> eqResponseList=
				//equenceSMSGateway.sendOverHTTP(replaceSpecialCharacters(rawMessageContent),toMobNum,sentId,smsCampRepId);
						messagebirdSMSGateway.sendOverHTTP(rawMessageContent,toMobNum,sentId,smsCampRepId,templateRegisteredId);
				/*if(eqResponseList != null && eqResponseList.size()>0){
					
					updateInitialStatusFromEquence(eqResponseList,smsCampRepId,sentId,toMobNum);
				}*/
			}
			
		} catch (Exception e) {
			if(logger.isDebugEnabled()) logger.debug("Exception while sending the SMS",e);
			throw new Exception();
		}
		
		
		
	}//sendToSMSApi
	
	public void updateInitialStatusFromEquence(String mrId, Long smsCampRepId,String sentId,String mobile) {
		// TODO Auto-generated method stub
			try {
				ServiceLocator serviceLocator = ServiceLocator.getInstance();
				SMSCampaignSentDaoForDML smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML)serviceLocator.getDAOForDMLByName("smsCampaignSentDaoForDML");
					if(logger.isDebugEnabled()) logger.debug("mrId in updateSMSCampSentStatus is===>"+mrId+" smsCampRepId "+smsCampRepId.longValue());
					/*logger.info("smsCampaignSentDaoForDML---"+smsCampaignSentDaoForDML);
					logger.info("responseList---"+responseList);
					for (String mrId : responseList) {						
						smsCampaignSentDaoForDML.updateApiMsgId(sentId.trim(),smsCampRepId.longValue(),mrId);
					}*/
					smsCampaignSentDaoForDML.updateMrId(sentId.trim(),smsCampRepId.longValue(),mrId,mobile);
			}catch (Exception e) {
				// TODO: handle exception
				logger.error("Exception ::::" , e);
			}
		
	}
	
	public void updateInitialStatusFromMsgBird(String mrId, Long smsCampRepId,String sentId,String mobile) {
		// TODO Auto-generated method stub
			try {
				ServiceLocator serviceLocator = ServiceLocator.getInstance();
				SMSCampaignSentDaoForDML smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML)serviceLocator.getDAOForDMLByName("smsCampaignSentDaoForDML");
					if(logger.isDebugEnabled()) logger.debug("mrId of Msgbird Response in updateSMSCampSentStatus is===>"+mrId+" smsCampRepId "+smsCampRepId.longValue());
					/*logger.info("smsCampaignSentDaoForDML---"+smsCampaignSentDaoForDML);
					logger.info("responseList---"+responseList);
					for (String mrId : responseList) {						
						smsCampaignSentDaoForDML.updateApiMsgId(sentId.trim(),smsCampRepId.longValue(),mrId);
					}*/
					smsCampaignSentDaoForDML.updateMrId(sentId.trim(),smsCampRepId.longValue(),mrId,mobile);
			}catch (Exception e) {
				// TODO: handle exception
				logger.error("Exception ::::" , e);
			}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

	public String replaceSpecialCharacters(String messageContent) {
		logger.debug("====entered replace chars====");
		try {
			if(charCodes == null) {
				
				CharacterCodesDao characterCodesDao = null;
				try {
					characterCodesDao = (CharacterCodesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CHARACTERCODESDAO);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error(" Exception while getting the dao");
				}
				
				if(characterCodesDao == null ) return messageContent;
				
				charCodes = characterCodesDao.findAll();
				
				if(charCodes == null || charCodes.size() <= 0) return messageContent; 
				
				
			}
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
	
	
	/**
	 * this method sends the request to fetch the reports
	 * @param userSMSTool
	 * @param smsDeliveryReport
	 */
	public void requestToSMSApi(String userSMSTool, SMSDeliveryReport smsDeliveryReport) {
		//******** need to send the request to appropriate SMS API based on the user SMS tool*****
		
		if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_NETCORE)){
			//netCoreApi.fetchReports(smsDeliveryReport);
			
			updateSMSCampSentStatus(netCoreApi.fetchReports(smsDeliveryReport),smsDeliveryReport);
			
		}
		
		
		
		
	}//requestToSMSApi
	
	/**
	 * this method sends the request to fetch the reports
	 * @param userSMSTool
	 * @param smsDeliveryReport
	 */
	public Map<String, String> requestToSMSApi(OCSMSGateway ocsmsGateway, String msgIdStr) throws Exception{
		//******** need to send the request to appropriate SMS API based on the user SMS tool*****
		String userSMSTool = ocsmsGateway.getGatewayName();
		
		if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MVAYOO)){
			//netCoreApi.fetchReports(smsDeliveryReport);
			
			if(mVaayooApi == null) {
				
				mVaayooApi = new MVaayooApi(ocsmsGateway);
				
			}//if
		
			return mVaayooApi.fetchReports(msgIdStr);
			
		}
		if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_PAKISTAN)){
			//netCoreApi.fetchReports(smsDeliveryReport);
			
			PakistanGateway pakistanGatewayObj =  new PakistanGateway(ocsmsGateway);
		
			return pakistanGatewayObj.fetchReports(msgIdStr);
			
		}
        if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_OUTREACH)){
			//netCoreApi.fetchReports(smsDeliveryReport);
			
			OutreachPakistanGateway outreachPakistanGatewayObj =  new OutreachPakistanGateway(ocsmsGateway);
		
			return outreachPakistanGatewayObj.fetchReports(msgIdStr);
			
		}
        if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_INFOCOMM)){
				
        	InfocommSMSGateway infocommSMSGateway = new InfocommSMSGateway(ocsmsGateway);
				
		
			return infocommSMSGateway.getReportsFromInfocomm(msgIdStr);
			
		}
		
		return null;
		
		
	}//requestToSMSApi
	
	public int getExpiryPeriod(OCSMSGateway ocsmsGateway) throws Exception{
		//******** need to send the request to appropriate SMS API based on the user SMS tool*****
		String userSMSTool = ocsmsGateway.getGatewayName();
		
		if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MVAYOO)){
			
			return 2;
			
		}
		if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_PAKISTAN)){
		
			return 7;
			
		}
		if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_OUTREACH)){
			
			return 7;
			
		}
		
		return 0;
		
		
	}//requestToSMSApi
	
	
	/**
	 * this methos updates the status SMSCampaignSent based on the received rep[orts
	 * @param statusMessage
	 * @param sentId
	 */
	public  void updateSMSCampSentStatus(List<String> updateContentList, SMSDeliveryReport smsDeliveryReport) {
		try {
				String[] respCnt = null;
				String queryStr;
				if(logger.isDebugEnabled()) logger.debug("updateContentList in updateSMSCampSentStatus is===>"+updateContentList);
				if(updateContentList != null && updateContentList.size()>0) {
					for (String response : updateContentList) {
						respCnt = response.split("\\|");
						if(logger.isDebugEnabled()) logger.debug("respCnt[0]"+respCnt[0]+"  respCnt[1] "+respCnt[1]);
						//queryStr="update sms_campaign_sent set status='"+respCnt[1]+"' where sent_id="+respCnt[0];
							if(respCnt[3].equals(smsDeliveryReport.getRequestId())) {//if the reports are fetched only for the scheduler picked smsDeliveryreport object
								//Long mobile = Long.parseLong(respCnt[2]);
								//smsCampaignSentDao.updateStatus(respCnt[1], respCnt[2],respCnt[3]);
								smsCampaignSentDaoForDML.updateStatus(respCnt[1], respCnt[2],respCnt[3]);

							}
					}//for
				}//if
				
		} catch (Exception e) {
			if(logger.isDebugEnabled()) logger.debug("Exception while updating the sent status",e);
		}
		
	}//updateSMSCampSentStatus
	
	
	public synchronized void UpdateInitialStatusFromSMS4Connect(Long startSentId, 
			Long endSentId, String[] responseArr, Long smsCampRepId) throws Exception{
		
		try {
			SMSCampaignSentDao smsCampaignSentDao = (SMSCampaignSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_CAMPAIGNSENT_DAO);
			
			String responseCode = responseArr[0];//TODO we can map our own response code irrespective of what we get from gateway  
			/*if(responseCode.equals("300")){
				
				
			}else*/ 
			if(responseCode.equals("206")){
				//smsCampaignSentDao.updateInitialStatusToMultiple(startSentId, endSentId, SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);
				smsCampaignSentDaoForDML.updateInitialStatusToMultiple(startSentId, endSentId, SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);

				List<SMSCampaignSent> retList = smsCampaignSentDao.findBy(startSentId, endSentId, smsCampRepId);
				
				if(retList == null) return;
				
				List<SMSBounces> smsBounceList = new ArrayList<SMSBounces>();
				List<SMSCampaignSent> listToBeUpdated = new ArrayList<SMSCampaignSent>();
				List<String> contactMobilesList = new ArrayList<String>();
				List<SMSSuppressedContacts> suppressedContactsList = new ArrayList<SMSSuppressedContacts>(); 
				Users user = null;
				for (SMSCampaignSent smsCampaignSent : retList) {
					
					if(user == null){
						
						user = smsCampaignSent.getSmsCampaignReport().getUser();
					}
					smsCampaignSent.setStatus(SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);
					
					SMSBounces newBounce= new SMSBounces();
					newBounce.setCrId(smsCampaignSent.getSmsCampaignReport().getSmsCrId());
					newBounce.setSentId(smsCampaignSent);
					newBounce.setMessage(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER);
					newBounce.setMobile(smsCampaignSent.getMobileNumber());
					newBounce.setCategory(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER);
					newBounce.setBouncedDate(Calendar.getInstance());
					smsBounceList.add(newBounce);
					
					SMSSuppressedContacts suppressedContact = new SMSSuppressedContacts();
					suppressedContact.setUser(user);
					suppressedContact.setMobile(smsCampaignSent.getMobileNumber());
					suppressedContact.setType(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER);
					suppressedContact.setReason(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER);
					suppressedContact.setSuppressedtime(Calendar.getInstance());
					
					suppressedContactsList.add(suppressedContact);
					
					contactMobilesList.add(smsCampaignSent.getMobileNumber());
					
					listToBeUpdated.add(smsCampaignSent);
				}//for
				
				if(listToBeUpdated.size() > 0) {
					
					//smsCampaignSentDao.saveByCollection(listToBeUpdated);
					smsCampaignSentDaoForDML.saveByCollection(listToBeUpdated);

					listToBeUpdated.clear();
					
					if(smsBounceList.size() > 0) {
						
						//smsBouncesDao.saveByCollection(smsBounceList);
						smsBouncesDaoForDML.saveByCollection(smsBounceList);

						smsBounceList.clear();
					}
					
					
					if(suppressedContactsList.size() > 0) {
						SMSCampaignDeliveryReportsHandler handler = new SMSCampaignDeliveryReportsHandler();
						handler.addToSuppressedContacts(user, suppressedContactsList);
						
					}
					
					if(contactMobilesList.size() > 0) {
						
						try {
							String mobileStr = Constants.STRING_NILL;
							for (String mobile : contactMobilesList) {
								
								if(mobile.startsWith(user.getCountryCarrier().toString())) {
									
									mobile = mobile.substring(user.getCountryCarrier().toString().length());
									
								}
								
								if(!mobileStr.isEmpty()) mobileStr += "|";
								
								mobileStr += mobile;
								
								
								
							}
							contactsDaoForDML.updatemobileStatusForMultipleContacts(mobileStr, 
									SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER, user.getUserId());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("Exception while updating contacts ", e);
							
						}
						
						contactMobilesList.clear();
					}
					//TODO need to update bounce count in sms reports
					//int updateCount = smsCampaignReportDao.updateBounceReport(smsCampRepId);
					int updateCount = smsCampaignReportDaoForDML.updateBounceReport(smsCampRepId);
					logger.debug("bounced count  ::"+updateCount);
					//smsCampaignSentDao.saveByCollection(listToBeUpdated);
					
				}//if
				
				
			}
			//int updatedCnt = smsCampaignSentDao.updateReqId( startSentId, endSentId, responseArr[1]);
			int updatedCnt = smsCampaignSentDaoForDML.updateReqId( startSentId, endSentId, responseArr[1]);

			
			
			logger.debug("updatedCnt ==== "+updatedCnt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
			if(startSentId.equals(getStartSentId())){
				
				setStartSentId(null);
			}
		}
		
	}
	
    public synchronized void UpdateInitialStatusFromOutreach(Long startSentId, 
			Long endSentId, String[] responseArr, Long smsCampRepId) throws Exception{
		
		try {
			
			try{
				
				Thread currentThread = Thread.currentThread();
				logger.info("In outreach, currentThread.getName() >>>>>>>>> "+currentThread.getName());
				//logger.info("In outreach, startSentId >>>>>>>>> "+startSentId+"  >>>>>>>>>>>>>> endSentId >>>>>>>>>>>> "+endSentId);
			}catch(Exception e){
				logger.error("Exception in outreach >>>>>>>> ",e);
			}
			
			
			
			SMSCampaignSentDao smsCampaignSentDao = (SMSCampaignSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_CAMPAIGNSENT_DAO);
			
			String responseCode = responseArr[0];//TODO we can map our own response code irrespective of what we get from gateway  
			/*if(responseCode.equals("300")){
				
				
			}else*/ 
			if(responseCode.equals("206")){
				//smsCampaignSentDao.updateInitialStatusToMultiple(startSentId, endSentId, SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);
				//int numberOfBounced = smsCampaignSentDao.updateInitialStatusToMultipleOutreach(outreachSentIdsSet,SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);//3--Polo rl issue
				int numberOfBounced = smsCampaignSentDaoForDML.updateInitialStatusToMultipleOutreach(outreachSentIdsSet,SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);//3--Polo rl issue

				logger.debug("number of bounces updated in very initial = "+numberOfBounced);
				//List<SMSCampaignSent> retList = smsCampaignSentDao.findBy(startSentId, endSentId, smsCampRepId);//4--Polo rl issue
				List<SMSCampaignSent> retList = smsCampaignSentDao.findBy_Outreach(outreachSentIdsSet, smsCampRepId);
				
				if(retList == null) return;
				
				List<SMSBounces> smsBounceList = new ArrayList<SMSBounces>();
				List<SMSCampaignSent> listToBeUpdated = new ArrayList<SMSCampaignSent>();
				List<String> contactMobilesList = new ArrayList<String>();
				List<SMSSuppressedContacts> suppressedContactsList = new ArrayList<SMSSuppressedContacts>(); 
				Users user = null;
				for (SMSCampaignSent smsCampaignSent : retList) {
					
					if(user == null){
						
						user = smsCampaignSent.getSmsCampaignReport().getUser();
					}
					smsCampaignSent.setStatus(SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);
					
					SMSBounces newBounce= new SMSBounces();
					newBounce.setCrId(smsCampaignSent.getSmsCampaignReport().getSmsCrId());
					newBounce.setSentId(smsCampaignSent);
					newBounce.setMessage(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER);
					newBounce.setMobile(smsCampaignSent.getMobileNumber());
					newBounce.setCategory(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER);
					newBounce.setBouncedDate(Calendar.getInstance());
					smsBounceList.add(newBounce);
					
					SMSSuppressedContacts suppressedContact = new SMSSuppressedContacts();
					suppressedContact.setUser(user);
					suppressedContact.setMobile(smsCampaignSent.getMobileNumber());
					suppressedContact.setType(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER);
					suppressedContact.setReason(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER);
					suppressedContact.setSuppressedtime(Calendar.getInstance());
					
					suppressedContactsList.add(suppressedContact);
					
					contactMobilesList.add(smsCampaignSent.getMobileNumber());
					
					listToBeUpdated.add(smsCampaignSent);
				}//for
				
				if(listToBeUpdated.size() > 0) {
					
					//smsCampaignSentDao.saveByCollection(listToBeUpdated);
					smsCampaignSentDaoForDML.saveByCollection(listToBeUpdated);
					listToBeUpdated.clear();
					
					if(smsBounceList.size() > 0) {
						
						//smsBouncesDao.saveByCollection(smsBounceList);
						smsBouncesDaoForDML.saveByCollection(smsBounceList);

						smsBounceList.clear();
					}
					
					
					if(suppressedContactsList.size() > 0) {
						SMSCampaignDeliveryReportsHandler handler = new SMSCampaignDeliveryReportsHandler();
						handler.addToSuppressedContacts(user, suppressedContactsList);
						
					}
					
					if(contactMobilesList.size() > 0) {
						
						try {
							String mobileStr = Constants.STRING_NILL;
							for (String mobile : contactMobilesList) {
								
								if(mobile.startsWith(user.getCountryCarrier().toString())) {
									
									mobile = mobile.substring(user.getCountryCarrier().toString().length());
									
								}
								
								if(!mobileStr.isEmpty()) mobileStr += "|";
								
								mobileStr += mobile;
								
								
								
							}
							contactsDaoForDML.updatemobileStatusForMultipleContacts(mobileStr, 
									SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER, user.getUserId());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("Exception while updating contacts ", e);
							
						}
						
						contactMobilesList.clear();
					}
					//TODO need to update bounce count in sms reports
					//int updateCount = smsCampaignReportDao.updateBounceReport(smsCampRepId);
					int updateCount = smsCampaignReportDaoForDML.updateBounceReport(smsCampRepId);
					logger.debug("bounced count  ::"+updateCount);
					//smsCampaignSentDao.saveByCollection(listToBeUpdated);
					
				}//if
				
				
			}
			//int updatedCnt = smsCampaignSentDao.updateReqId( startSentId, endSentId, responseArr[1]);
			//int updatedCnt = smsCampaignSentDao.updateReqId_Outreach( outreachSentIdsSet, responseArr[1]);//5--Polo rl issue
			int updatedCnt = smsCampaignSentDaoForDML.updateReqId_Outreach( outreachSentIdsSet, responseArr[1]);//5--Polo rl issue

			
			outreachSentIdsSet.clear();//6--Polo rl issue
			logger.debug("updatedCnt ==== "+updatedCnt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
			/*if(startSentId.equals(getStartSentIdOutreach())){
				logger.info("startSentIdOutreach.equals(getStartSentIdOutreach() outreach ========= is true");
				setStartSentIdOutreach(null);
			}*/
		}
		
	}
	
    
    
    
    public synchronized void UpdateInitialStatusFromBsmsItsPakistan(Map<String,String> responseMap, Long smsCampRepId) throws Exception{
		
		try {
			
			try{
				
				Thread currentThread = Thread.currentThread();
				logger.info("In bsms_its_pakistan, currentThread.getName() >>>>>>>>> "+currentThread.getName());
				//logger.info("In outreach, startSentId >>>>>>>>> "+startSentId+"  >>>>>>>>>>>>>> endSentId >>>>>>>>>>>> "+endSentId);
			}catch(Exception e){
				logger.error("Exception in bsms_its_pakistan >>>>>>>> ",e);
			}
			
			
			
			SMSCampaignSentDao smsCampaignSentDao = (SMSCampaignSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_CAMPAIGNSENT_DAO);
			
			Set<Map.Entry<String,String>> set = responseMap.entrySet();  
			Map.Entry<String, String> mapEntry = set.iterator().next();
			
			String key = mapEntry.getKey();
			String value = mapEntry.getValue();
			
			
			int updatedCnt=0;
	 
			if("Error".equals(key)){
				
				int numberOfBounced = smsCampaignSentDaoForDML.updateInitialStatusBsmsItsPak(bsmsItsSentIdsSet,SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);
				logger.debug("number of bounces updated in very initial = "+numberOfBounced);
				
				
				List<SMSCampaignSent> retList = smsCampaignSentDao.findBy_BsmsItsPak(bsmsItsSentIdsSet, smsCampRepId);
				logger.debug("retList >>>>>>>>>>>>> "+retList);
				
				if(retList == null) return;
				
				List<SMSBounces> smsBounceList = new ArrayList<SMSBounces>();
				List<SMSCampaignSent> listToBeUpdated = new ArrayList<SMSCampaignSent>();
				List<String> contactMobilesList = new ArrayList<String>();
				List<SMSSuppressedContacts> suppressedContactsList = new ArrayList<SMSSuppressedContacts>(); 
				Users user = null;
				for (SMSCampaignSent smsCampaignSent : retList) {
					
					if(user == null){
						
						user = smsCampaignSent.getSmsCampaignReport().getUser();
					}
					smsCampaignSent.setStatus(SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);
					
					SMSBounces newBounce= new SMSBounces();
					newBounce.setCrId(smsCampaignSent.getSmsCampaignReport().getSmsCrId());
					newBounce.setSentId(smsCampaignSent);
					newBounce.setMessage(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER);
					newBounce.setMobile(smsCampaignSent.getMobileNumber());
					newBounce.setCategory(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER);
					newBounce.setBouncedDate(Calendar.getInstance());
					smsBounceList.add(newBounce);
					
					SMSSuppressedContacts suppressedContact = new SMSSuppressedContacts();
					suppressedContact.setUser(user);
					suppressedContact.setMobile(smsCampaignSent.getMobileNumber());
					suppressedContact.setType(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER);
					suppressedContact.setReason(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER);
					suppressedContact.setSuppressedtime(Calendar.getInstance());
					
					suppressedContactsList.add(suppressedContact);
					
					contactMobilesList.add(smsCampaignSent.getMobileNumber());
					
					listToBeUpdated.add(smsCampaignSent);
				}//for
				
				if(listToBeUpdated.size() > 0) {
					
					//smsCampaignSentDao.saveByCollection(listToBeUpdated);
					smsCampaignSentDaoForDML.saveByCollection(listToBeUpdated);
					listToBeUpdated.clear();
					
					if(smsBounceList.size() > 0) {
						
						//smsBouncesDao.saveByCollection(smsBounceList);
						smsBouncesDaoForDML.saveByCollection(smsBounceList);

						smsBounceList.clear();
					}
					
					
					if(suppressedContactsList.size() > 0) {
						SMSCampaignDeliveryReportsHandler handler = new SMSCampaignDeliveryReportsHandler();
						handler.addToSuppressedContacts(user, suppressedContactsList);
						
					}
					
					if(contactMobilesList.size() > 0) {
						
						try {
							String mobileStr = Constants.STRING_NILL;
							for (String mobile : contactMobilesList) {
								
								if(mobile.startsWith(user.getCountryCarrier().toString())) {
									
									mobile = mobile.substring(user.getCountryCarrier().toString().length());
									
								}
								
								if(!mobileStr.isEmpty()) mobileStr += "|";
								
								mobileStr += mobile;
								
								
								
							}
							contactsDaoForDML.updatemobileStatusForMultipleContacts(mobileStr, 
									SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER, user.getUserId());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("Exception while updating contacts ", e);
							
						}
						
						contactMobilesList.clear();
					}
					//TODO need to update bounce count in sms reports
					//int updateCount = smsCampaignReportDao.updateBounceReport(smsCampRepId);
					int updateCount = smsCampaignReportDaoForDML.updateBounceReport(smsCampRepId);
					logger.debug("bounced count  ::"+updateCount);
					//smsCampaignSentDao.saveByCollection(listToBeUpdated);
					
				}//if
				
				
			}else{ // i.e. Success
				
				updatedCnt = smsCampaignSentDaoForDML.updateReqId_BsmsIts( bsmsItsSentIdsSet, key);			 	
				
				
			}
			
			
			bsmsItsSentIdsSet.clear();
			logger.debug("updatedCnt ==== "+updatedCnt);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}finally{
			bsmsItsSentIdsSet.clear();
			logger.debug("cleared the set ==== ");
			/*if(startSentId.equals(getStartSentIdOutreach())){
				logger.info("startSentIdOutreach.equals(getStartSentIdOutreach() outreach ========= is true");
				setStartSentIdOutreach(null);
			}*/
		}
		
	}
    
    
	
	/**
	 * this method sets the status in SMSCampaignSent received when after immediate sending
	 * @param updateContentList
	 */
	public  void updateInitialStatusFromClikaTell(List<String> updateContentList, Long smsCampRepId) {

		try {
				String[] respCnt = null;
				//String queryStr;
				SMSCampaignSent smsCampaignSent = null;
				boolean isError = false;
				String status = null;
				String mobileStatus = null;
				if(logger.isDebugEnabled()) logger.debug("updateContentList in updateSMSCampSentStatus is===>"+updateContentList+" smsCampRepId "+smsCampRepId.longValue());
				if(updateContentList != null && updateContentList.size()>0){
					for (String response : updateContentList) {
						respCnt = response.split("\\|");
						//if(logger.isDebugEnabled()) logger.debug("respCnt[0]"+respCnt[0]+"  respCnt[1] "+respCnt[1]);
						//queryStr="update sms_campaign_sent set status='"+respCnt[1]+"' where sent_id="+respCnt[0];
						//smsCampaignSentDao.updateInitialStatus(respCnt[0], respCnt[1]);
						
						String actualSentId = respCnt[0].contains(Constants.ADDR_COL_DELIMETER) ? 
								respCnt[0].split(Constants.ADDR_COL_DELIMETER)[1] : respCnt[0] ;
						
						/*if(respCnt[0].contains("000000000")) {: 
							String actualSentId = respCnt[0];
							actualSentId = actualSentId.substring(0,actualSentId.lastIndexOf("000000000"));
							respCnt[0] = actualSentId;
						}*/
						if(respCnt[1].startsWith(Constants.SMS_SUPP_TYPE_INVALID_MOBILE_NUMBER)) {
							
							//TODO need get the user object from sent-report-user
							status = Constants.SMS_SUPP_TYPE_INVALID_MOBILE_NUMBER;
							mobileStatus = Constants.SMS_SUPP_TYPE_INVALID_MOBILE_NUMBER;
							
							isError = true;
							
							
						}//if
						else if(respCnt[1].startsWith(SMSStatusCodes.CLICKATELL_STATUS_NO_CREDITS)) {
							
							isError = true;
							status = SMSStatusCodes.CLICKATELL_STATUS_NO_CREDITS;
							
							
						}
						else if(respCnt[1].startsWith(SMSStatusCodes.CLICKATELL_STATUS_NUMBER_DELISTED)) {
							
							isError = true;
							status = SMSStatusCodes.CLICKATELL_STATUS_NUMBER_DELISTED;
							mobileStatus = SMSStatusCodes.CLICKATELL_STATUS_NUMBER_DELISTED;
							
						}
						
						else {
							
							try {
								isError = false;
								
								//int updateCount = smsCampaignSentDao.updateApiMsgId((actualSentId).trim(), smsCampRepId, respCnt[1] );
								int updateCount = smsCampaignSentDaoForDML.updateApiMsgId((actualSentId).trim(), smsCampRepId, respCnt[1] );

								mobileStatus = Constants.CON_STATUS_ACTIVE;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								logger.error(e);
							}
						}
						
						if(isError == true) {
							try {
								//smsCampaignSentDao.updateInitialStatus((actualSentId).trim(), SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);
								smsCampaignSentDaoForDML.updateInitialStatus((actualSentId).trim(), SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);

								smsCampaignSent = smsCampaignSentDao.findBySentId((actualSentId),smsCampRepId);
								
								addToBounces(smsCampaignSent.getSmsCampaignReport(), smsCampaignSent.getMobileNumber(), smsCampaignSent, status, respCnt[1].split(Constants.ADDR_COL_DELIMETER)[1]);
								
								
								if(!respCnt[1].startsWith(SMSStatusCodes.CLICKATELL_STATUS_NO_CREDITS)){
									
									Users user = smsCampaignSent.getSmsCampaignReport().getUser();
									contactsDaoForDML.updatemobileStatus(smsCampaignSent.getMobileNumber(), mobileStatus, user);
									addToSuppressedContacts(user, smsCampaignSent.getMobileNumber(), status);
									
								}
							} catch (Exception e) {
								if(logger.isErrorEnabled()) logger.error("error while adding bounce :"+smsCampaignSent.getMobileNumber());
								logger.error("Exception ::::" , e);
							}
						}
						
						//smsCampaignSent = smsCampaignSentDao.findBySentId(""+Long.parseLong(actualSentId),smsCampRepId);
						
						//smsCampaignSent.setApiMsgId(respCnt[1]);
						/*if(respCnt[1].equalsIgnoreCase("Invalid mobile number")) {
							
							//TODO need get the user object from sent-report-user
							
							Users user = smsCampaignSent.getSmsCampaignReport().getUser();
							addToSuppressedContacts(user, smsCampaignSent.getMobileNumber());
							
							
							
						}//if
*/						//smsCampaignSentDao.saveOrUpdate(smsCampaignSent);
					}//for
				}//if
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ::::" , e);
		}
	
		
		
	}//
	
	//public void unbindSession(String userSMSGatewayDetails) {
	public void unbindSession(OCSMSGateway ocsmsGateway) {
		
		/*String[] userSMSGatewayDetailsTokenArr = userSMSGatewayDetails.split(Constants.ADDR_COL_DELIMETER);
		
		String userSMSTool = userSMSGatewayDetailsTokenArr[0];
		*/
		
		String userSMSTool = ocsmsGateway.getGatewayName();
		
		
		
		if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MVAYOO)) {
			//call the method to prepare the data into xml format
			
			if(mVaayooApi == null) {
				
				mVaayooApi = new MVaayooApi(ocsmsGateway);
				
			}
			
			mVaayooApi.unbindSession() ;
			//update the status in SMSCampaignSent
			//updateSMSCampStatus(response, sentId);
			
		}//if
		else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_UNICEL)) {
		//call the method to prepare the data into xml format
		
			 if(unicelSMSGatewayObj == null) {
				
				unicelSMSGatewayObj = new UnicelSMSGateway(ocsmsGateway);
				
			 }
		
			 unicelSMSGatewayObj.unbindSession() ;
			//update the status in SMSCampaignSent
			//updateSMSCampStatus(response, sentId);
		
		}//if
		else if(Constants.USER_SMSTOOL_INFOBIP.equalsIgnoreCase(userSMSTool)){
			if(infobipSMSGateway == null){
				//infobipSMSGateway = new InfobipSMSGateway(); <------- is this a problem
				infobipSMSGateway = new InfobipSMSGateway(ocsmsGateway);
			}
			infobipSMSGateway.unbindSession();
		}
			
	}
//public  boolean getBalance(String userSMSTool, int totalCount, byte msgType) {
//public  boolean getBalance(String userSMSGatewayDetails, int totalCount, String msgType) {
public  boolean getBalance(OCSMSGateway  ocsmsGateway, int totalCount) throws BaseServiceException{
	
	//String[] userSMSGatewayDetailsTokenArr = userSMSGatewayDetails.split(Constants.ADDR_COL_DELIMETER);
	
	/*String userSMSTool = userSMSGatewayDetailsTokenArr[0];
	String userID = userSMSGatewayDetailsTokenArr[1];
	String pwd = userSMSGatewayDetailsTokenArr[2];*/
	
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
			if(clickaTellApi == null) {
				
				clickaTellApi = new ClickaTellApi();
				
			}
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
			// need to convert the NEtCore understandable format
			//TODO need to implement
			return false;
		}
		else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MVAYOO)) {
			//call the method to prepare the data into xml format
			
			//boolean isTransactional = (msgType == 3);
			//boolean isTransactional = (msgType.equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL));
			if(mVaayooApi == null) {
				
				mVaayooApi = new MVaayooApi(ocsmsGateway);
				
			}
			return mVaayooApi.getBalance(totalCount);
			
			
			//update the status in SMSCampaignSent
			//updateSMSCampStatus(response, sentId);
			
			
			
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_PAKISTAN)) {
			
			PakistanGateway pakistanGatewayObj = new PakistanGateway(ocsmsGateway);
			return pakistanGatewayObj.getBalence(totalCount, ocsmsGateway);
			
		}//Added for Infobip
		else if(Constants.USER_SMSTOOL_INFOBIP.equalsIgnoreCase(userSMSTool)){
			InfobipSMSGateway infobipSMSGateway = new InfobipSMSGateway(ocsmsGateway);
			//logger.info("Infobip Checking Credits count.."+totalCount);
			return infobipSMSGateway.getBalance(totalCount,ocsmsGateway);
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_OUTREACH)) {
			
			OutreachPakistanGateway outreachPakistanGatewayObj =  new OutreachPakistanGateway(ocsmsGateway);
			return outreachPakistanGatewayObj.getBalence(totalCount, ocsmsGateway);
			
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_BSMS_ITS_PAKISTAN)) {
			
			BsmsItsPakistanGateway bsmsItsPakistanGateway =  new BsmsItsPakistanGateway(ocsmsGateway);
			return bsmsItsPakistanGateway.getBalence(totalCount, ocsmsGateway);
			
		}
		return true;
		
	}
	public void sendErrorResponse(String userSMSTool, String UserId, String pwd, 
			String content, String mobileNumber, String senderId, String apiID) throws Exception{
		
		if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_UNICEL)) {
			// need to convert the mVaayooApi understandable format
			
			UnicelSMSGateway unicelSMSGatewayObj = new UnicelSMSGateway();
			
			 unicelSMSGatewayObj.sendOverHTTP(UserId, pwd, replaceSpecialCharacters(content), mobileNumber, senderId);
			
			
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CLICKATELL)) {
			
			if(clickaTellApi == null)clickaTellApi = new ClickaTellApi();
			String msgID = clickaTellApi.sendAutoResponse(UserId, pwd, apiID, mobileNumber, content, senderId, null );
			//String msgID = unicelSMSGatewayObj.sendOverHTTP(content, mobileNumber, senderId);
			
		}
		else if(Constants.USER_SMSTOOL_INFOBIP.equalsIgnoreCase(userSMSTool)){
			InfobipSMSGateway infobipSMSGateway = new InfobipSMSGateway();
			infobipSMSGateway.sendOverHTTP(UserId,pwd,replaceSpecialCharacters(content), mobileNumber, senderId);
		}
		
	}
	
	public String sendSingleSms(OCSMSGateway ocsmsGateway, String content, 
			String mobileNumber, String senderId, Long rowId,String templateRegisteredId) throws BaseServiceException {

		String userSMSTool = ocsmsGateway.getGatewayName();
		String userID = ocsmsGateway.getUserId();
		String pwd = ocsmsGateway.getPwd();
		String apiId = ocsmsGateway.getAPIId();
		String msgID = null;

		if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_UNICEL)) {
			// need to convert the mVaayooApi understandable format

			UnicelSMSGateway unicelSMSGatewayObj = new UnicelSMSGateway(ocsmsGateway);
			msgID = unicelSMSGatewayObj.sendOverHTTP(userID, pwd,replaceSpecialCharacters(content), mobileNumber, senderId);

		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CLICKATELL)) {
			
			content = StringEscapeUtils.escapeHtml(content);
			if(clickaTellApi == null)clickaTellApi = new ClickaTellApi();
			msgID = clickaTellApi.sendSingleSms(userID, pwd,apiId, mobileNumber,content, senderId);

		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MVAYOO)) {
			
			if(mVaayooApi == null) {
				
				mVaayooApi = new MVaayooApi(ocsmsGateway);
				
			}
			msgID = mVaayooApi.sendSMSOverHTTP(content, mobileNumber, senderId);
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_PAKISTAN)) {
			
			PakistanGateway pakistanGateway = new PakistanGateway(ocsmsGateway);
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
			msgID = infobipSMSGateway.sendOverHTTP(userID, pwd, replaceSpecialCharacters(content), mobileNumber, senderId);
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_OUTREACH)) {
			
			OutreachPakistanGateway outreachPakistanGateway = new OutreachPakistanGateway(ocsmsGateway);
			try {
				String[] responseArr = outreachPakistanGateway.sendMsg(content, true, mobileNumber, userID, pwd, senderId);
				if(responseArr != null){
					msgID = responseArr[0] + Constants.ADDR_COL_DELIMETER + responseArr[1];
				}
			} catch (Exception e) {
				logger.error("Exception while sending msg ", e);
			}
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_BSMS_ITS_PAKISTAN)) {
			
			BsmsItsPakistanGateway bsmsItsPakistanGateway = new BsmsItsPakistanGateway(ocsmsGateway);
			try {
				Map<String, String> responseMap = bsmsItsPakistanGateway.sendMsg(content, true, mobileNumber, userID, pwd, senderId);
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
			//String messageContent = StringEscapeUtils.escapeHtml(content);
			//equenceSMSGateway.sendSingleSMSOverHTTP(replaceSpecialCharacters(messageContent), mobileNumber);
			msgID = equenceSMSGateway.sendSingleSMSOverHTTP(content, mobileNumber,rowId,templateRegisteredId);

		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MESSAGEBIRD)) {

			MessagebirdSMSgateway messageBirdSMSGateway = new MessagebirdSMSgateway(ocsmsGateway,userID,pwd,senderId,ocsmsGateway.getPrincipalEntityId());
			//String messageContent = StringEscapeUtils.escapeHtml(content);
			//equenceSMSGateway.sendSingleSMSOverHTTP(replaceSpecialCharacters(messageContent), mobileNumber);
			//otp sent 
		//	content=content.replace("&nbsp;"," ");
			msgID = messageBirdSMSGateway.sendSingleSMSOverHTTP(content, mobileNumber,rowId,templateRegisteredId);

		}
		else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CM)) {
			if(CMComSMSGatewayObj == null) {
				
				CMComSMSGatewayObj = new CMComSMSGateway( ocsmsGateway);
			}
			String messageContent =  replaceSpecialCharacters(content );
			
			//String messageContent = StringEscapeUtils.escapeHtml(rawMessageContent);
			
			CMComSMSGatewayObj.sendSMSOverSMPPForSingle(messageContent, mobileNumber, rowId, senderId);
			
			/*EquenceSMSGateway equenceSMSGateway = new EquenceSMSGateway(ocsmsGateway,userID,pwd,senderId,ocsmsGateway.getPrincipalEntityId());
			//String messageContent = StringEscapeUtils.escapeHtml(content);
			//equenceSMSGateway.sendSingleSMSOverHTTP(replaceSpecialCharacters(messageContent), mobileNumber);
			msgID = equenceSMSGateway.sendSingleSMSOverHTTP(content, mobileNumber,rowId,templateRegisteredId);*/

		}
		else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_SYNAPSE)) {

			SynapseSMSGateway synapseSMSGateway = new SynapseSMSGateway(ocsmsGateway,userID,pwd,senderId);
			//String messageContent = StringEscapeUtils.escapeHtml(content);
			//synapseSMSGateway.sendSingleSMSOverHTTP(replaceSpecialCharacters(messageContent), mobileNumber);
			msgID = synapseSMSGateway.sendSingleSMSOverHTTP(content, mobileNumber);

		}
		else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_INFOCOMM)) {

			InfocommSMSGateway infocommSMSGateway = new InfocommSMSGateway(ocsmsGateway,userID,pwd,senderId);
			msgID = infocommSMSGateway.sendSingleSMSOverHTTP(content, mobileNumber);

		}
		logger.debug("---------------msgId::"+msgID);
		return msgID;
	}
	
	
	public void sendAutoResponse(OCSMSGateway ocsmsGateway, String content, 
			String mobileNumber, String senderId, Long contactId, ClickaTellSMSInbound inbounObj) throws BaseServiceException {
		
		String userSMSTool = ocsmsGateway.getGatewayName();
		String userID = ocsmsGateway.getUserId();
		String pwd = ocsmsGateway.getPwd();
		String apiId = ocsmsGateway.getAPIId();
		
		content = content.replace("|^", "[").replace("^|", "]");
		Set<String> coupPhSet = Utility.findCoupPlaceholders(content);
		if(coupPhSet != null  && coupPhSet.size() > 0) {
			
			ReplacePlaceHolders replacePlaceHolders = new ReplacePlaceHolders();
			content = replacePlaceHolders.replaceSMSAutoResponseContent(content, coupPhSet, mobileNumber, contactId);
			
		}
		
		
		if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_UNICEL)) {
			// need to convert the mVaayooApi understandable format
			
			UnicelSMSGateway unicelSMSGatewayObj = new UnicelSMSGateway(ocsmsGateway);
			String msgID = unicelSMSGatewayObj.sendOverHTTP(userID, pwd, replaceSpecialCharacters(content), mobileNumber, senderId);
			if(inbounObj != null) {
				
				inbounObj.setMsgID(msgID);
				inbounObj.setAutoResponse(content);
				
			}
			
		}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CLICKATELL)) {
			content = StringEscapeUtils.escapeHtml(content);
			if(clickaTellApi == null)clickaTellApi = new ClickaTellApi();
			String msgID = clickaTellApi.sendAutoResponse(userID, pwd,apiId, mobileNumber, content, senderId, 
					inbounObj.getInboundMsgId().toString());
			//String msgID = unicelSMSGatewayObj.sendOverHTTP(content, mobileNumber, senderId);
			if(inbounObj != null) {
				
				inbounObj.setMsgID(msgID);
				inbounObj.setAutoResponse(content);
				
			}
			
			
		}else if(Constants.USER_SMSTOOL_INFOBIP.equalsIgnoreCase(userSMSTool)){
			InfobipSMSGateway infobipSMSGateway =  new InfobipSMSGateway();
			String msgID = infobipSMSGateway.sendOverHTTP(userID, pwd, replaceSpecialCharacters(content), mobileNumber, senderId);
			if(inbounObj != null) {
				inbounObj.setMsgID(msgID);
				inbounObj.setAutoResponse(content);
				
			}
		}
		
		
	}

	public String getMobileStr(Set<String> optinMobileSet, Users currUser ) {
		
		String mobileStr = Constants.STRING_NILL;
		for (String toMobNum : optinMobileSet) {
			toMobNum = toMobNum.trim();
			
			if(!toMobNum.startsWith(currUser.getCountryCarrier().toString()) && 
					(toMobNum.length() >= currUser.getUserOrganization().getMinNumberOfDigits()
					&& toMobNum.length() <= currUser.getUserOrganization().getMaxNumberOfDigits())) {
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
		if(clickaTellApi == null) {
			
			clickaTellApi = new ClickaTellApi();
			
		}
		clickaTellApi.sendMultipleMobileDoubleOptin(userID, pwd, apiID, optinMobileSet, smsSettings);
		
		
		//update the status in SMSCampaignSent
		//updateSMSCampStatus(response, sentId);
		
		
		
	}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MVAYOO)) {
		//call the method to prepare the data into xml format
		if(mVaayooApi == null) {
			
			mVaayooApi = new MVaayooApi(ocsmsGateway);
			
		}
		
		
		mVaayooApi.sendSMSOverHTTP(smsSettings.getAutoResponse(), getMobileStr(optinMobileSet,
				smsSettings.getUserId()), smsSettings.getSenderId());
		//clickaTellApi.sendMultipleMobileDoubleOptin(userID, pwd, optinMobileSet, smsSettings);
		
		
		//update the status in SMSCampaignSentMo
		//updateSMSCampStatus(response, sentId);
		
		
		
	}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_UNICEL)) {
		//call the method to prepare the data into xml format
		if(unicelSMSGatewayObj == null) {
			
			unicelSMSGatewayObj = new UnicelSMSGateway(ocsmsGateway);
			
		}
		unicelSMSGatewayObj.sendOverHTTP(userID, pwd, replaceSpecialCharacters(smsSettings.getAutoResponse()), 
				getMobileStr(optinMobileSet, smsSettings.getUserId()), smsSettings.getSenderId());//sendSMSOverHTTP(smsSettings.getAutoResponse(), optinMobileSet, sentId);
		//clickaTellApi.sendMultipleMobileDoubleOptin(userID, pwd, optinMobileSet, smsSettings);
		
		
		//update the status in SMSCampaignSent
		//updateSMSCampStatus(response, sentId);
	}
	else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_NETCORE)) {
		// need to convert the NEtCore understandable format
		//TODO need to implement
		
	}
	
	else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CELLNEXT)) {
		// need to convert the NEtCore understandable format
		//TODO need to implement
	}
	else if(Constants.USER_SMSTOOL_INFOBIP.equalsIgnoreCase(userSMSTool)){

		if(infobipSMSGateway == null){
			infobipSMSGateway =  new InfobipSMSGateway(ocsmsGateway);
		}
		infobipSMSGateway.sendOverHTTP(userID, pwd, replaceSpecialCharacters(smsSettings.getAutoResponse()),
				getMobileStr(optinMobileSet, smsSettings.getUserId()), smsSettings.getSenderId());
	}
	
	
	
	
}
	
	
	/**
	 * this method sets the status in SMSCampaignSent received when after immediate sending
	 * @param updateContentList
	 */
	public  void updateSMSCampSentInitialStatus(List<String> updateContentList, Long smsCampRepId) {
		try {
				String[] respCnt = null;
				//String queryStr;
				SMSCampaignSent smsCampaignSent = null;
				if(logger.isDebugEnabled()) logger.debug("updateContentList in updateSMSCampSentStatus is===>"+updateContentList);
				if(updateContentList != null && updateContentList.size()>0){
					for (String response : updateContentList) {
						respCnt = response.split("\\|");
						//if(logger.isDebugEnabled()) logger.debug("respCnt[0]"+respCnt[0]+"  respCnt[1] "+respCnt[1]);
						//queryStr="update sms_campaign_sent set status='"+respCnt[1]+"' where sent_id="+respCnt[0];
						//smsCampaignSentDao.updateInitialStatus(respCnt[0], respCnt[1]);
						
						String actualSentId = respCnt[0].substring(1);
						
						/*if(respCnt[0].contains("000000000")) {
							String actualSentId = respCnt[0];
							actualSentId = actualSentId.substring(0,actualSentId.lastIndexOf("000000000"));
							respCnt[0] = actualSentId;
						}*/
						
						smsCampaignSent = smsCampaignSentDao.findBySentId(""+Long.parseLong(actualSentId),smsCampRepId);
						
						smsCampaignSent.setRequestId(respCnt[2]);
						smsCampaignSent.setStatus(respCnt[1]);
						if(respCnt[1].equalsIgnoreCase("Invalid mobile number")) {
							
							//TODO need get the user object from sent-report-user
							
							Users user = smsCampaignSent.getSmsCampaignReport().getUser();
							addToSuppressedContacts(user, smsCampaignSent.getMobileNumber(),
									Constants.SMS_SUPP_TYPE_INVALID_MOBILE_NUMBER);
							
							
						}//if
						//smsCampaignSentDao.saveOrUpdate(smsCampaignSent);
						smsCampaignSentDaoForDML.saveOrUpdate(smsCampaignSent);
					}//for
				}//if
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ::::" , e);
		}
	}//updateSMSCampSentInitialStatus
	
	/**
	 * This method is added to make the suppressed contacts for SMS.
	 * (Here if the contact's mobile number is not a valid number then it will be treated as suppressed contact).
	 * @param users
	 * @param mobile
	 */
	public void addToSuppressedContacts(Users users, String mobile, String status) {
		try {
			
			SMSSuppressedContacts suppressedContact = null;
			List<SMSSuppressedContacts> retList =  smsSuppressedContactsDao.searchContactsById(users, mobile);
			if(retList != null && retList.size() == 1) {
				
				suppressedContact = retList.get(0);
				
				
			}
			else {
				
				suppressedContact = new SMSSuppressedContacts();
				suppressedContact.setUser(users);
				suppressedContact.setMobile(mobile);
				suppressedContact.setSuppressedtime(Calendar.getInstance());
				
			}
			
			
			/*SMSSuppressedContacts suppressedContact = new SMSSuppressedContacts();
			//Users users = usersDao.find(userId); 
			suppressedContact.setUser(users);
			suppressedContact.setMobile(mobile);*/
			suppressedContact.setType(status);
			suppressedContact.setReason(status);
			
			//smsSuppressedContactsDao.saveOrUpdate(suppressedContact);
			smsSuppressedContactsDaoForDML.saveOrUpdate(suppressedContact);
			if(logger.isDebugEnabled()) logger.debug("Added successfully to suppress contacts .");
		} catch (Exception e) {
			if(logger.isErrorEnabled()) logger.error("**Exception : while adding contact to suppress Contacts list :", e);
		}
		
	}
	
	
	public void addToBounces(SMSCampaignReport smsCampaignReport, String mobile,
			SMSCampaignSent smsCampaignSent, String status, String statusCode ) {
		
		if(smsCampaignSent == null || smsCampaignReport == null) {
			
			if(logger.isErrorEnabled()) logger.error("** got source objects as null");
			return;
			
		}
		
		//need to find the existing object with the same given mobile if any..
		Long crId = smsCampaignReport.getSmsCrId(); 
		SMSBounces newBounce = smsBouncesDao.findBymobile(crId, mobile);
		
		if(newBounce == null) {
			
			newBounce = new SMSBounces();
			newBounce.setCrId(crId);
			newBounce.setSentId(smsCampaignSent);
			
			
		}
		
		newBounce.setMessage(SMSStatusCodes.clickaTellStatusMessagesMap.get(statusCode));
		newBounce.setMobile(mobile);
		newBounce.setCategory(status);
		newBounce.setBouncedDate(Calendar.getInstance());
		//smsBouncesDao.saveOrUpdate(newBounce);
		smsBouncesDaoForDML.saveOrUpdate(newBounce);

		//smsCampaignReportDao.updateBounceReport(smsCampaignReport.getSmsCrId());
		if(logger.isDebugEnabled()) logger.debug("updateing report bounce count"+smsCampaignReport.getBounces());
		
		
	}//addToBounces
	
	public String getReportListNames(Long smsCampRepId) {
		
		String campRepListNames = "";
		
		SMSCampReportListsDao smsCampReportListsDao = (SMSCampReportListsDao)context.
		getBean("smsCampReportListsDao");


		
		SMSCampReportLists smsCampRepLists = smsCampReportListsDao.findBySmsCampReportId(smsCampRepId);
		
		String listNames = smsCampRepLists.getListsName();
		String[] listNameArr = listNames.split(",");
		for (String listName : listNameArr) {
			
			
			if(campRepListNames.length() > 0) campRepListNames += ",";
			campRepListNames += "'"+listName.trim()+"'";
			
			
			
		}//for
		
		
		return campRepListNames;
		
		
	}
	
	
	/**
	 * this method creates a new SMSDeliveryReport object with the status 'Active'
	 * @param response
	 * @param smsCampRepId
	 */
	public  void saveRequest(String response, Long smsCampRepId) {
		
		if(logger.isDebugEnabled()) logger.debug("----just entered in save requset-----"+smsCampRepId);
		
		//smsDeliveryReportDao.saveOrUpdate(new SMSDeliveryReport(response, smsCampRepId, "Active", Calendar.getInstance()));
		smsDeliveryReportDaoForDML.saveOrUpdate(new SMSDeliveryReport(response, smsCampRepId, "Active", Calendar.getInstance()));
		
		
		
	}//saveRequest
	
	
	
	
	/*public static void main(String args[]) {
	NetCoreApi nobj = new NetCoreApi();
	CaptiwayToSMSApiGateway tempobj = new CaptiwayToSMSApiGateway(nobj);
	tempobj.sendToSMSApi(Constants.USER_SMSTOOL_NETCORE, "hai sampale message1", "919490927928", "N", "60", "9848495956", "919490927928", "1", 120l);
	
}*/
	
	// Added for DR extraction
	
	

public String sendSingleMobileDoubleOptin(OCSMSGateway ocsmsGateway, String to, String text, SMSSettings smsSettings) throws BaseServiceException{
	
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
		
		return clickaTellApi.sendSingleMobileDoubleOptin(userID, pwd, apiID, to, text, smsSettings);
		
		
		//update the status in SMSCampaignSent
		//updateSMSCampStatus(response, sentId);
		
		
		
	}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MVAYOO)) {
		//call the method to prepare the data into xml format
		if(mVaayooApi == null) {
			
			mVaayooApi = new MVaayooApi(ocsmsGateway);
			
		}
		Users currUser = smsSettings.getUserId();
		if(!to.startsWith(currUser.getCountryCarrier().toString()) && 
				(to.length() >= currUser.getUserOrganization().getMinNumberOfDigits()
				&& to.length() <= currUser.getUserOrganization().getMaxNumberOfDigits())	) {
			to = currUser.getCountryCarrier().toString()+to;
		}
		mVaayooApi.sendSMSOverHTTP(smsSettings.getAutoResponse(), to, smsSettings.getSenderId());
		//clickaTellApi.sendMultipleMobileDoubleOptin(userID, pwd, optinMobileSet, smsSettings);
		
		
		//update the status in SMSCampaignSentMo
		//updateSMSCampStatus(response, sentId);
		
		
		
	}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_UNICEL)) {
		//call the method to prepare the data into xml format
		if(unicelSMSGatewayObj == null) {
			
			unicelSMSGatewayObj = new UnicelSMSGateway(ocsmsGateway);
			
		}
		Users currUser = smsSettings.getUserId();
		if(!to.startsWith(currUser.getCountryCarrier().toString()) && 
				(to.length() >= currUser.getUserOrganization().getMinNumberOfDigits()
				&& to.length() <= currUser.getUserOrganization().getMaxNumberOfDigits())) {
			to = currUser.getCountryCarrier().toString()+to;
		}
		unicelSMSGatewayObj.sendOverHTTP(userID, pwd, replaceSpecialCharacters(smsSettings.getAutoResponse()), 
				to, smsSettings.getSenderId());//sendSMSOverHTTP(smsSettings.getAutoResponse(), optinMobileSet, sentId);
		//clickaTellApi.sendMultipleMobileDoubleOptin(userID, pwd, optinMobileSet, smsSettings);
		
		
		//update the status in SMSCampaignSent
		//updateSMSCampStatus(response, sentId);
	}
	else if(Constants.USER_SMSTOOL_INFOBIP.equalsIgnoreCase(userSMSTool)){
		if(infobipSMSGateway == null) {

			infobipSMSGateway = new InfobipSMSGateway(ocsmsGateway);

		}
		Users currUser = smsSettings.getUserId();
		UserOrganization userOrganization = currUser.getUserOrganization();
		if(!to.startsWith(currUser.getCountryCarrier().toString()) && ( (to.length() >= userOrganization.getMinNumberOfDigits()) && (to.length() <= userOrganization.getMaxNumberOfDigits()))) {
			to = currUser.getCountryCarrier().toString()+to;
		}
		infobipSMSGateway.sendOverHTTP(userID, pwd, replaceSpecialCharacters(smsSettings.getAutoResponse()), 
				to, smsSettings.getSenderId());
	}
	return null;
	
	
	
	
	
}

	
	public String findTypeOfAckBy(String userSMSTool){
		
		if(userSMSTool.equals(Constants.USER_SMSTOOL_PAKISTAN) || userSMSTool.equals(Constants.USER_SMSTOOL_OUTREACH)) return Constants.SMS_TYPE_OF_ACK_TRANSACTIONID;
		else return Constants.SMS_TYPE_OF_ACK_MSGID; 
	}
	
	//method to check the connection
	public boolean checkAuthentication(OCSMSGateway ocsmsGateway){
			
			String userSMSTool = ocsmsGateway.getGatewayName();
			/*String userID = ocsmsGateway.getUserId();
			String pwd = ocsmsGateway.getPwd();
			String apiID = ocsmsGateway.getAPIId();*/
				if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_SMSCOUNTRY)) {
					
					return true;
					
				}
				else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CLICKATELL)) {
					logger.info("checking clickatell connection");
					ClickaTellApi clickaTellApi = new ClickaTellApi();
					return clickaTellApi.checkAuthentication(ocsmsGateway);
					
				}
				else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_NETCORE)) {
					// need to convert the NEtCore understandable format
					//TODO need to implement
					return true;
					
				}
				
				else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_CELLNEXT)) {
					// need to convert the NEtCore understandable format
					//TODO need to implement
					return true;
				}
				else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MVAYOO)) {
					//return getConnection(ocsmsGateway);
					
					
				}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_PAKISTAN)) {
					
					return true;
					
				}
				else if(Constants.USER_SMSTOOL_INFOBIP.equalsIgnoreCase(userSMSTool)){
					logger.info("checking infobip connection");
					InfobipSMSGateway infobip=new InfobipSMSGateway();
					return infobip.checkAuthentication(ocsmsGateway);
				}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_OUTREACH)) {
					
					return true;
					
				}else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_BSMS_ITS_PAKISTAN)) {
					return true;					
				}
				else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_EQUENCE)) {
					EquenceSMSGateway eqGateway = new EquenceSMSGateway();
					return eqGateway.checkConnectivity(ocsmsGateway);					
				}
				else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_SYNAPSE)) {
					SynapseSMSGateway synGateway = new SynapseSMSGateway();
					return synGateway.checkConnectivity(ocsmsGateway);					
				}
				else if(userSMSTool.equalsIgnoreCase(Constants.USER_SMSTOOL_MESSAGEBIRD)) {
					MessagebirdSMSgateway msgbirdGateway = new MessagebirdSMSgateway();
					
					return msgbirdGateway.checkConnectivity(ocsmsGateway);	
					//return true;
				}
				
				return true;
	}
	/*public boolean getConnection(OCSMSGateway ocsmsGateway){
		try {
			TCPIPConnection connection = null;
			logger.info("ocsmsGateway.getIp()"+ocsmsGateway.getIp()+"\tocsmsGateway.getPort()"+ocsmsGateway.getPort());
		    connection = new TCPIPConnection(ocsmsGateway.getIp(), Integer.parseInt(ocsmsGateway.getPort()));
		    connection.open();
		    return true;
		} catch (IOException e) {
			logger.error("Exception ", e);
			return false;
		} catch (Exception e) {
			if(e instanceof SocketException) {
				logger.error("Exception ", e);
				return false;
			}
			logger.error("Exception ", e);
		}
	
		return true;
	}*/
	
	
}//CaptiwayTOSMSApiGateway
