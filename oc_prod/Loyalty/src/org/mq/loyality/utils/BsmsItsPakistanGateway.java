package org.mq.loyality.utils;

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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.loyality.common.dao.EmailQueueDao;
import org.mq.loyality.common.hbmbean.EmailQueue;
import org.mq.loyality.common.hbmbean.OCSMSGateway;
import org.mq.loyality.exception.BaseServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author proumyaa
 *
 */
public class BsmsItsPakistanGateway {

	private final String URL_PH_ID = "{USERID}";
	private final String URL_PH_PWD = "{PASSWORD}";
	private final String URL_PH_MASK = "{MASK}";
	private final String URL_PH_TO = "{TO}";
	private final String URL_PH_LANG = "{LANG}";
	private final String URL_PH_MSG = "{MSG}";
	private final String URL_PH_TYPE = "{TYPE}";
	private final String URL_PH_API_KEY = "{APIKEY}"; 
	//http://sms4connect.com/api/sendsms.php/balance/status?id={USERID}&pwd={PASSWORD}
	private final String BAL_URL_GET_DATA = "id="+URL_PH_ID+"&pass="+URL_PH_PWD;//"//"http://www.sms4connect.com/api/sendsms.php/balance/status?id="+URL_PH_ID+"&pass="+URL_PH_PWD;//TODO need to configure at DB
	
	//private final String MSG_SUBMIT_GET_URL = "http://www.sms4connect.com/api/sendsms.php/sendsms/url";
	private final String MSG_SUBMIT_GET_URL = "http://bsms.its.com.pk/api.php";
	
	
	private final String POST_DATA = "key="+URL_PH_API_KEY+"&receiver="+URL_PH_TO+"&sender="+URL_PH_MASK+"&msgdata="+URL_PH_MSG+"&response_type="+URL_PH_TYPE;

	
	
	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);
	@Autowired
	private EmailQueueDao emailQueueDao;
	
	public EmailQueueDao getEmailQueueDao() {
		return emailQueueDao;
	}

	public void setEmailQueueDao(EmailQueueDao emailQueueDao) {
		this.emailQueueDao = emailQueueDao;
	}
	
	private ApplicationContext applicationContext;
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	public BsmsItsPakistanGateway() {}
	
	/*public PakistanGateway(String userId, String pwd) {
		
		
	}*/
	
	public boolean getBalence(int totalCount, OCSMSGateway gatewayObj) {
		return true;
	}
	
	
	/*public void sendMultipleMobileDoubleOptin(String userID, String pwd,
			Set<String> optinMobileSet, SMSSettings smsSettings) {

		PakistanGatewaySubmission multiSubMission = new PakistanGatewaySubmission(userID, pwd, optinMobileSet, smsSettings);
		multiSubMission.start();
	}
	
	public void prepareData(String userID, String pwd, String senderID, String messageContent, String toMobNum) throws Exception {
		
		messageContent = messageContent.replace("|^", "[").replace("^|", "]");
		
		logger.info("Before test messageContent: "+messageContent);
		
		messageContent = replaceBarcodePhWithDummyCode(messageContent);//we can shift this method to other place
		
		boolean isSingle = !(toMobNum.contains(Constants.DELIMETER_COMMA));
		
		sendMsg(messageContent, isSingle, toMobNum, userID, pwd, senderID);
		
	}
	
	private String replaceBarcodePhWithDummyCode(String smsMsg){
		
		try{
			CouponsDao couponsDao = null;
			Set<String> coupPhSet = Utility.findCoupPlaceholders(smsMsg);
		
			if(coupPhSet != null  && coupPhSet.size() > 0) {
			
				couponsDao = (CouponsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
				
				Iterator<String> iter = coupPhSet.iterator();
				String couponPh = null;
				Coupons coupon = null;
				
				String appShortUrl = PropertyUtil.getPropertyValue(Constants.APP_SHORTNER_URL).trim();
				
				while(iter.hasNext()){
					couponPh = iter.next();
					logger.debug("sms cc ph: "+couponPh);
					String searchBarcodePhStr = appShortUrl+"["+couponPh+"]";
					
					if(smsMsg.contains(searchBarcodePhStr)){
						String[] ccPhTokens = couponPh.split("_");
						String testBarcode = "Sample";		
						//coupon = couponsDao.findCouponsByIdAndName(Long.parseLong(ccPhTokens[1].trim()),ccPhTokens[2].trim());
						coupon = couponsDao.findCouponsById(Long.parseLong(ccPhTokens[1].trim()));
						if(coupon == null){
							continue;
						}
						else{
							logger.debug("coupon: "+coupon+" "+coupon.getBarcodeType()+" ");
							String barcodeStr = null;
							if(coupon.getBarcodeType().trim().equals("LN")){
								barcodeStr = "L"+testBarcode;
							}
							else if(coupon.getBarcodeType().trim().equals("QR")){
								barcodeStr = "Q"+testBarcode;
							}
							else if(coupon.getBarcodeType().trim().equals("DM")){
								barcodeStr = "D"+testBarcode;
							}
							else if(coupon.getBarcodeType().trim().equals("AZ")){
								barcodeStr = "A"+testBarcode;
							}
							
							String barcodeLink  = appShortUrl+barcodeStr;
							smsMsg = smsMsg.replace(searchBarcodePhStr, barcodeLink);
							
							logger.info("searchBarcodePhStr :"+ searchBarcodePhStr);
							logger.info("Test sms barcodeLink: "+barcodeLink);
						}//else
					}//if
					
				}//while
			}//if
		}catch(Exception e){
			logger.error("Exception in replacing barcode placeholder", e);
		}
		return smsMsg;
	}*/
	
	
	
	public Map<String, String> sendMsg(String msg, boolean isSingle, String toMobile, String userId, String pwd, String senderID) throws BaseServiceException{
		
		
		//here no need to prepare a msg fro each mobile hence send it as a post request when multiple mobiles are there
		//assuming that usually from subscriber user will not enter more than 100 numbers at a time
		String submitResponse = isSingle ? sendGet(msg, isSingle, toMobile, userId, pwd, senderID) :
			sendPost(msg, isSingle, toMobile, userId, pwd, senderID);
		
		if(submitResponse == null){
			
			throw new BaseServiceException("Exception in submitting the msg, not received the response");
		}
		
		
		Map<String,String> responseMap = parseSubmitResponse(submitResponse);
		
		if(responseMap == null) {
			
			throw new BaseServiceException("not received the transactionId");
		}
		
		logger.debug("responseMap = "+responseMap);
		
		//TODO need to store the Tid against to this submission
		return responseMap;
		
	}
	
	private String sendGet(String msg, boolean isSingle, String toMobile, String userId, String pwd, String mask) {
		
		try {
			String targetUrl = MSG_SUBMIT_GET_URL+"?"+(POST_DATA.replace(URL_PH_API_KEY, pwd)
								//.replace(URL_PH_PWD, pwd)
								.replace(URL_PH_TO, toMobile)
								//.replace(URL_PH_LANG, "English")
								.replace(URL_PH_MASK, URLEncoder.encode(mask, "UTF-8"))
								.replace(URL_PH_MSG, URLEncoder.encode(msg, "UTF-8"))
								.replace(URL_PH_TYPE, "xml"));
			logger.debug("targetUrl======>"+targetUrl);			
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
			logger.debug("submit response from bsmsits pk is======>"+response);
			
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
	
	private String sendPost(String msg, boolean isSingle, String toMobile, String userId, String pwd, String mask) {
		return sendGet(msg, isSingle, toMobile, userId, pwd, mask);
	}
	
	public void getReorts(){
		
		
	}
	
	public String parseBalResponse(String balenceResponse){
		
		
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(balenceResponse.getBytes()));
			doc.getDocumentElement().normalize();
			
			
			Element docElement = doc.getDocumentElement();
			
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
	
	public Map<String, String> parseSubmitResponse(String submitResponse) {
		
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
					//System.out.println("msgid >>>>>>>> "+msgid);
					logger.debug("msgid >>>>>>>> "+msgid);
					
					//collecting number
					Node numberNode = childNodeList.item(1);					
					Element numberNodeElement = (Element)numberNode;
					String number = numberNodeElement.getTextContent().trim();
					//System.out.println("number >>>>>>>> "+number);
					logger.debug("number >>>>>>>> "+number);
					
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
			
			//System.out.println("responseMap>>>>>>>>>"+responseMap);
			logger.debug("responseMap>>>>>>>>>"+responseMap);
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
	
	/*class PakistanGatewaySubmission extends Thread {
		
		
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
		
		
		public void sendMultipleMobileDoubleOptin(String userID, String pwd, Set<String> optinMobileSet, 
				SMSSettings smsSettings) throws BaseServiceException {
			
		if(logger.isDebugEnabled()) logger.debug("performing mobile optin here....");
			String messageContent = smsSettings.getAutoResponse();
			
			String senderID = smsSettings.getSenderId();
			
			if(senderID == null) {
				if(logger.isErrorEnabled()) logger.error("NO senderid found to be send");
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
			
			if(coupPhSet != null  && coupPhSet.size() > 0) {
				isFillCoup = true;
				
			}
			String mobileStr = Constants.STRING_NILL;
			
			for (String toMobNum : optinMobileSet) {
				
				String text = messageContent;
				toMobNum = toMobNum.trim();
				
				//TODO need to think on country carrier
				
				if(isFillCoup) {
					
					try {
						text = CaptiwayToSMSApiGateway.replaceSMSAutoResponseContent(text, coupPhSet, toMobNum);
						
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
					sendGet(text, true, toMobNum, userID, pwd, senderID);
				}
				
			}//for
			
			if(!isFillCoup) {
				
				String text = StringEscapeUtils.escapeHtml(messageContent);
				sendPost(text, false, mobileStr, userID, pwd, senderID);
				
			}
			
			if(logger.isDebugEnabled()) logger.debug("completed mobile optin here....");
			
		}//sendMultipleMobileDoubleOptin
			
	
	}*/
	public static void main(String[] args) {
		
		/*String balResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
				+ "<corpsms>"
				+ "<code>100</code>"
				+ "<type>Success</type>"
				+ "<response>5913</response>"
				+ "</corpsms>";*/
		
		/*String submitResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
							+ "<corpsms>"
							+ "<transaction id=\"1\">"
							+ "<status num=\"92300xxxxxxx\" date=\"07-11-2012 13:25:02\">Successful</status>"
							+ "</transaction>"
							+ "</corpsms>";*/
		
		
		/*String submitResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
								+ "<corpsms><code>300</code><type>Success</type>"
								+ "<response>Message Sent to Telecom.</response>"
								+ "<transactionID>1</transactionID>"
								+ "</corpsms>";
			*/	
		//OutreachPakistanGateway obj = new OutreachPakistanGateway();
		
		//String retStr = obj.parseBalResponse(balResponse);
		//String retStr = obj.parseSubmitResponse(submitResponse);
		//obj.sendGet("test", true, "929123423232", "92test11", "cloud2233", "Outreach");//(1, "92test11", "cloud2233");
		//logger.debug(retStr);
		
	}
}
