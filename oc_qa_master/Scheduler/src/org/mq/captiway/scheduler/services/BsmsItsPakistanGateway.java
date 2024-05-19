package org.mq.captiway.scheduler.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.beans.OCSMSGateway;
import org.mq.captiway.scheduler.beans.SMSSettings;
import org.mq.captiway.scheduler.dao.EmailQueueDao;
import org.mq.captiway.scheduler.dao.EmailQueueDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.ReplacePlaceHolders;
import org.mq.captiway.scheduler.utility.SMSStatusCodes;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.ServiceLocator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class BsmsItsPakistanGateway {
	

	
	private static final String SMS4CONNECT_STATUS_SUCCESS = "Successful";
	private static final String SMS4CONNECT_STATUS_UNSUCCESS = "UnSuccessful";
	private static final String SMS4CONNECT_STATUS_PENDING = "Message Sent to Telecom";
	
	private final String URL_PH_ID = "{USERID}";
	private final String URL_PH_PWD = "{PASSWORD}";
	private final String URL_PH_MASK = "{MASK}";
	private final String URL_PH_TO = "{TO}";
	private final String URL_PH_LANG = "{LANG}";
	private final String URL_PH_MSG = "{MSG}";
	private final String URL_PH_TYPE = "{TYPE}";
	private final String URL_PH_TID = "{TID}";
	private final String URL_PH_API_KEY = "{APIKEY}"; // specific to this provider (http://bsms.its.com.pk)
	
	//http://sms4connect.com/api/sendsms.php/balance/status?id={USERID}&pwd={PASSWORD}
	//private final String BAL_URL_GETDATA = "id="+URL_PH_ID+"&pass="+URL_PH_PWD;//"http://www.sms4connect.com/api/sendsms.php/balance/status?id="+URL_PH_ID+"&pass="+URL_PH_PWD;//TODO need to configure at DB
	
	//private final String MSG_SUBMIT_GET_URL = "http://sms4connect.com/api/sendsms.php/sendsms/url";
	
	private final String MSG_SUBMIT_GET_URL = "http://bsms.its.com.pk/api.php";
	//private final String POST_DATA = "id="+URL_PH_ID+"&pass="+URL_PH_PWD+"&mask="+URL_PH_MASK+"&to="+URL_PH_TO+"&lang="+URL_PH_LANG+"&msg="+URL_PH_MSG+"&type="+URL_PH_TYPE;
	private final String POST_DATA = "key="+URL_PH_API_KEY+"&receiver="+URL_PH_TO+"&sender="+URL_PH_MASK+"&msgdata="+URL_PH_MSG+"&response_type="+URL_PH_TYPE;
	//private final String REPORT_URL = "http://sms4connect.com/api/sendsms.php/delivery/status";
	//private final String REPORT_URL = "http://outreach.pk/api/sendsms.php/delivery/status";
	
	//private final String REPORT_POST_DATA = "id="+URL_PH_ID+"&pass="+URL_PH_PWD+"&transaction="+URL_PH_TID; //passed -rajeev

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	private OCSMSGateway ocSMSGateway;
	private volatile LinkedHashSet<String> mobilesSet;//to preserve the insertion order
	private boolean isPersonalized;
	private String msgContent;
	private AtomicInteger counter;
	private Map<String, String > statusMap ;
	
	private void setStatusMap() {
		if(statusMap == null){
			statusMap = new HashMap<String, String>();
			statusMap.put("Successful", SMSStatusCodes.CLICKATELL_STATUS_RECEIVED);
			statusMap.put("UnSuccessful", SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);
			statusMap.put("Message Sent to Telecom", SMSStatusCodes.CLICKATELL_STATUS_RECEIVED);//TODO
			
			
		}
		
	}
	public BsmsItsPakistanGateway() {
		setStatusMap();
	}
	
	public BsmsItsPakistanGateway(OCSMSGateway ocSMSGateway) {
		this.ocSMSGateway = ocSMSGateway;
		mobilesSet = new LinkedHashSet<String>();
		counter = new AtomicInteger(0);
		setStatusMap();
		
	}
	
	private void updateCounter(boolean isIncrement){
		
		if(isIncrement) counter.incrementAndGet();
		else counter.set(0);
	}
	
	private int getCounter(){
		
		return counter.get();
	}
	private synchronized LinkedHashSet<String> getMobileSet(){
		
		return this.mobilesSet;
	}
	private synchronized void setMobileSet(String mobile){
		
		getMobileSet().add(mobile);
	}
	
	
	public boolean getBalence(int totalCount, OCSMSGateway gatewayObj) {
		
		return true;
	}
	
	
	public void sendMultipleMobileDoubleOptin(String userID, String pwd,
			Set<String> optinMobileSet, SMSSettings smsSettings) { //not modified as per new integration, as in even very initial PakistanGateway, its not being called.

		PakistanGatewaySubmission multiSubMission = new PakistanGatewaySubmission(userID, pwd, optinMobileSet, smsSettings);
		multiSubMission.start();
	}
	
	public Map<String,String> submitFinalChunk(String senderID) throws Exception{
		//this method is skeleton only for future bulk sending(if any)
		return null;
	} 
	
	
	public Map<String,String> prepareData(boolean isPersonalized, String messageContent,  String toMobNum, String senderID) throws Exception {
			return sendMsg(messageContent, true, toMobNum, ocSMSGateway.getUserId(), ocSMSGateway.getPwd(), senderID);
	}
	
	public Map<String,String> sendMsg(String msg, boolean isSingle, String toMobile, String userId, String pwd, String senderID) throws BaseServiceException{
		
		
		String submitResponse = isSingle ? sendGet(msg, toMobile, ocSMSGateway.getUserId(), ocSMSGateway.getAPIId(), senderID) :
			sendPost(msg, toMobile, ocSMSGateway.getUserId(), ocSMSGateway.getAPIId(), senderID);
		
		if(submitResponse == null){
			
			throw new BaseServiceException("Exception in submitting the msg, not received the response");
		}
		
		Map<String,String> responseMap = parseSubmitResponse(submitResponse);
		
		if(responseMap == null) {
			
			throw new BaseServiceException("not received the transactionId");
		}
		
		logger.debug("response map in case of BsmsIts provider = "+responseMap);
		
		//TODO need to store the Tid against to this submission
		return responseMap;
		
	}
	
	private String sendGet(String msg, String toMobile, String userId, String pwd, String mask) {
		
		try {
			String targetUrl = MSG_SUBMIT_GET_URL+"?"+(POST_DATA.replace(URL_PH_API_KEY, pwd)
								//.replace(URL_PH_PWD, pwd)
								.replace(URL_PH_TO, toMobile)
								//.replace(URL_PH_LANG, "English")
								.replace(URL_PH_MASK, URLEncoder.encode(mask, "UTF-8"))
								.replace(URL_PH_MSG, URLEncoder.encode(msg, "UTF-8"))
								.replace(URL_PH_TYPE, "xml"));
			logger.debug("targetUrl>>>>>>>"+targetUrl);
			//System.out.println("targetUrl>>>>>>>"+targetUrl);
		//	logger.debug(" Current Thread Name "+Thread.currentThread().getName()+"______ targetUrl ==>"+targetUrl);	
			
			/*if(true){
				logger.debug("______ Returning from Send GEt here .....................<<<<<<<<<<<<<<<<<<<<<<");	
				return null;
			}*/
			
			URL url = new URL(targetUrl);
			HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
			
			
			urlconnection.setRequestMethod("GET");
			urlconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			urlconnection.setDoOutput(true);

							
			BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
			
			String decodedString;
			String response = Constants.STRING_NILL;
			
			while ((decodedString = in.readLine()) != null) {
				response += decodedString;
			}
			in.close();
			logger.debug("submit response BsmsIts, is======>"+response);
			//System.out.println("submit response BsmsIts, is======>"+response);
			return response;
			
		} catch (MalformedURLException e) {
			logger.error("Exception", e);
		} catch (ProtocolException e) {
			logger.error("Exception", e);
		} catch (IOException e) {
			logger.error("Exception", e);
		}catch (Exception e) {
			logger.error("Exception", e);
		}
						
		return null;		
		
	}
	
	private String sendPost(String msg, String toMobile, String userId, String pwd, String mask) {
		
		return sendGet(msg, toMobile, ocSMSGateway.getUserId(), ocSMSGateway.getAPIId(), mask);
		
	}
	
	public Map<String, String> fetchReports(String TIDStr) throws Exception{
		return null;
	}
	
	
	
	private Map<String, String> parseReportResponse(StringBuilder response) throws Exception{
		
		/**
		 * <?xml version="1.0" encoding="UTF-8" ?>
			<corpsms>
			<transaction id="1">
			<status num="92300xxxxxxx" date="07-11-2012 13:25:02">Successful</status>
			</transaction>
			</corpsms>

		 */
		String responseStr = response.toString();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(responseStr.getBytes()));
		doc.getDocumentElement().normalize();
		
		
		
		NodeList nodeLst = doc.getElementsByTagName("transaction");
		if(nodeLst == null) return null;
		logger.info("status message is====>"+nodeLst.getLength());
		
		Map<String, String> retStatusMap = new LinkedHashMap<String, String>();
		
		for(int i=0; i<nodeLst.getLength(); i++) {
			Node node = nodeLst.item(i);
			Element element = (Element)node;
			String TID = element.getAttribute("id");
			NodeList childNodeLst = null;
			if(node.hasChildNodes()){
				
				childNodeLst = element.getElementsByTagName("status");
				for (int j=0; j<childNodeLst.getLength(); j++) {
					
					Node statusNode = childNodeLst.item(j);
					Element statusElement = (Element)statusNode;
					String mobileNumber = statusElement.getAttribute("num");
					String status = statusElement.getTextContent();
					
					//Set<String> mobileStatus = statusMap.get(TID);
					//if(mobileStatus == null) mobileStatus = new LinkedHashSet<String>();
					//mobileStatus.add(mobileNumber+Constants.ADDR_COL_DELIMETER+status);
					retStatusMap.put(mobileNumber, TID+Constants.ADDR_COL_DELIMETER+this.statusMap.get(status));
					
				}//for
				
			}//if
				
		}//for
		
		return retStatusMap;
	}
	
	public String parseBalResponse(String balenceResponse){
		
		
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(balenceResponse.getBytes()));
			doc.getDocumentElement().normalize();
			
			
			//Element docElement = doc.getDocumentElement();
			
			NodeList nodeLst = doc.getElementsByTagName("response");
			if(nodeLst == null) return null;
			logger.info("status message is====>"+nodeLst.getLength());
			for(int i=0; i<nodeLst.getLength(); i++) {
				
				Node node = nodeLst.item(i);
				Element element = (Element)node;
				return element.getTextContent().trim();
					
			}
		} catch (ParserConfigurationException e) {
			logger.error("Exception ::" , e);
		} catch (SAXException e) {
			logger.error("Exception ::" , e);
		} catch (IOException e) {
			logger.error("Exception ::" , e);
		}
		
		return null;
	}
	
	//tested on june 6th 2017
	public  Map<String,String> parseSubmitResponse(String submitResponse) {
		
		HashMap<String, String> responseMap;
		
		try {
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(submitResponse.getBytes()));
			doc.getDocumentElement().normalize();
			
			
			NodeList errorNumberNodeList = doc.getElementsByTagName("errorno");				
			Node errorNumberNode = errorNumberNodeList.item(0);
			Element errorNumberElement = (Element)errorNumberNode;
			String errorNumber = errorNumberElement.getTextContent().trim();
			
			if(errorNumber.equals("0")){ // i.e. Success
				NodeList validNumbersNodeList = doc.getElementsByTagName("valid_numbers");				
				Node validNumbersNode = validNumbersNodeList.item(0);
				Element validNumbersElement = (Element)validNumbersNode;
				String validNumbers = validNumbersElement.getTextContent().trim();
				
				responseMap = new HashMap<String, String>(Integer.parseInt(validNumbers));
				
				
				//now fill the map with key as msgid and phonenumber as value.
				NodeList numberListNodeList = doc.getElementsByTagName("numberlist");
				
				logger.info("length of numberListNodeList is ====>"+numberListNodeList.getLength());
				for(int i=0; i<numberListNodeList.getLength(); i++) {
					
					Node node = numberListNodeList.item(i);
					NodeList childNodeList = node.getChildNodes();
					
					//collecting msgid
					Node msgIdNode = childNodeList.item(0);					
					Element msgIdNodeElement = (Element)msgIdNode;
					String msgid = msgIdNodeElement.getTextContent().trim();
					logger.info("msgid >>>>>>>> "+msgid);
					
					//collecting number
					Node numberNode = childNodeList.item(1);					
					Element numberNodeElement = (Element)numberNode;
					String number = numberNodeElement.getTextContent().trim();
					logger.info("number >>>>>>>> "+number);
					
					responseMap.put(msgid, number);
						
				}
				
				
			}else{
				responseMap = new HashMap<String, String>(1);
				NodeList statusNodeList = doc.getElementsByTagName("status");
				NodeList descNodeList = doc.getElementsByTagName("description");
				
				Node statusNode = statusNodeList.item(0);
				Node descNode = descNodeList.item(0);
				
				Element statusNodeElement = (Element)statusNode;
				Element descNodeElement = (Element)descNode;
				
				String status = statusNodeElement.getTextContent().trim();
				String description = descNodeElement.getTextContent().trim();
				
				responseMap.put(status, description);
				
				
				
				
			}
			
			logger.info("responseMap>>>>>>>>>"+responseMap);
			return responseMap;
					
			
			
		} catch (ParserConfigurationException e) {
			logger.error("Exception ::" , e);
		} catch (SAXException e) {
			logger.error("Exception ::" , e);
		} catch (IOException e) {
			logger.error("Exception ::" , e);
		}
		
		return null;
	
		
		
		
	}
	
	
	
	
	
	class PakistanGatewaySubmission extends Thread {
		
		
		private String userID;
		private String pwd;
		private Set<String> optinMobileSet;
		private SMSSettings smsSettings;
		
		public PakistanGatewaySubmission(String userID, String pwd, Set<String> optinMobileSet, SMSSettings smsSettings) {
			
			this.userID = userID;
			this.pwd = pwd;
			this.optinMobileSet = optinMobileSet;
			this.smsSettings = smsSettings;
			
		}
		
		
		@Override
		public void run() {
			
			try {
				sendMultipleMobileDoubleOptin( userID,  pwd, optinMobileSet,  smsSettings);
			}catch (BaseServiceException e) {
				logger.error("Exception while sending multiple double optin msgs", e);
			}catch (Exception e) {
				logger.error("Exception while sending multiple double optin msgs", e);
			}
		}
		
		
		public void sendMultipleMobileDoubleOptin(String userID, String pwd, Set<String> optinMobileSet, SMSSettings smsSettings) throws BaseServiceException {
			
		if(logger.isDebugEnabled()) logger.debug("performing mobile optin here....");
			String messageContent = smsSettings.getAutoResponse();
			
			String senderID = smsSettings.getSenderId();
			
			if(senderID == null) {
				if(logger.isErrorEnabled()) logger.error("NO senderID found to be send");
				return;
				
			}
			if(messageContent == null) {
				
				if(logger.isErrorEnabled()) logger.error("NO message found to be send");
				return;
				
			}
			
			if(SMSStatusCodes.optOutFooterMap.get(smsSettings.getUserId().getCountryType())){
				
				messageContent = smsSettings.getMessageHeader() != null ? (smsSettings.getMessageHeader()+" "+ messageContent) : (""  + messageContent);
			}
			
			//messageContent = StringEscapeUtils.escapeHtml(messageContent);
			
			long userId = smsSettings.getUserId().getUserId().longValue();
			
			messageContent = messageContent.replace("|^", "[").replace("^|", "]");
			
			Set<String> coupPhSet = Utility.findCoupPlaceholders(messageContent);
			
			boolean isFillCoup = false;
			ReplacePlaceHolders replacePlaceHolders = null;
			if(coupPhSet != null  && coupPhSet.size() > 0) {
				isFillCoup = true;
				replacePlaceHolders = new ReplacePlaceHolders();
			}
			String mobileStr = Constants.STRING_NILL;
			
			for (String toMobNum : optinMobileSet) {
				
				String text = messageContent;
				toMobNum = toMobNum.trim();
				
				//TODO need to think on country carrier
				
				if(isFillCoup) {
					
					try {
						text = text = replacePlaceHolders.replaceSMSAutoResponseContent(text, coupPhSet, toMobNum, null);
						
					} catch (BaseServiceException e) {
						logger.error("exception while getting the coupon placehlders", e);
					}
					text = StringEscapeUtils.escapeHtml(text);
				}
									
				if(!isFillCoup) {
					
					if(!mobileStr.isEmpty()) mobileStr += Constants.DELIMETER_COMMA;
					mobileStr += toMobNum;
					
				}else{
					//TODO store the Tid or not
					sendGet(text, toMobNum, userID, pwd, senderID);
				}
				
			}//for
			
			if(!isFillCoup) {
				
				String text = StringEscapeUtils.escapeHtml(messageContent);
				sendPost(text, mobileStr, userID, pwd, senderID);
				
			}
			
			if(logger.isDebugEnabled()) logger.debug("completed mobile optin here....");
			
		}//sendMultipleMobileDoubleOptin
			
	
	}
	public static void main(String[] args) throws Exception{
		String submitResponse="<response>"+
				"<errorno>0</errorno>"+
				"<status>Success</status>"+
				"<description>Messages(s) accepted for delivery.</description>"+
				"<numberlist>"+
					"<msgid>1470890</msgid>"+
					"<number>923444042792</number>"+
				"</numberlist>"+
				"<numberlist>"+
					"<msgid>1470891</msgid>"+
					"<number>923444459689</number>"+
				"</numberlist>"+
				"<valid_numbers>2</valid_numbers>"+
			"</response>";
		//parseSubmitResponse(submitResponse); made static and tested
		
		
		String errormsg = "<response>"+
				"<errorno>41</errorno>"+
				"<status>Error</status>"+
				"<description>Invalid mask Provided.</description>"+
				"</response>";
		//parseSubmitResponse(errormsg); made static and tested
		
		
		BsmsItsPakistanGateway bgt = new BsmsItsPakistanGateway();
		String response = bgt.sendGet("Please Ignore It's a Test sms from OC", "923444042792", "", "8970a62a0dac54ae2151a23bd522c562", "Entertainer");
		Map<String,String> map = bgt.parseSubmitResponse(response);
		logger.info("map >>>>>>>>>>> "+map);
	}


}
