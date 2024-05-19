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
public class OutreachPakistanGateway {

	private final String URL_PH_ID = "{USERID}";
	private final String URL_PH_PWD = "{PASSWORD}";
	private final String URL_PH_MASK = "{MASK}";
	private final String URL_PH_TO = "{TO}";
	private final String URL_PH_LANG = "{LANG}";
	private final String URL_PH_MSG = "{MSG}";
	private final String URL_PH_TYPE = "{TYPE}";
	//http://sms4connect.com/api/sendsms.php/balance/status?id={USERID}&pwd={PASSWORD}
	private final String BAL_URL_GET_DATA = "id="+URL_PH_ID+"&pass="+URL_PH_PWD;//"//"http://www.sms4connect.com/api/sendsms.php/balance/status?id="+URL_PH_ID+"&pass="+URL_PH_PWD;//TODO need to configure at DB
	
	//private final String MSG_SUBMIT_GET_URL = "http://www.sms4connect.com/api/sendsms.php/sendsms/url";
	private final String MSG_SUBMIT_GET_URL = "http://outreach.pk/api/sendsms.php/sendsms/url";
	
	
	private final String POST_DATA = "id="+URL_PH_ID+"&pass="+URL_PH_PWD+"&mask="+URL_PH_MASK+"&to="+URL_PH_TO+"&lang="+URL_PH_LANG+"&msg="+URL_PH_MSG+"&type="+URL_PH_TYPE;

	
	
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
	
	public OutreachPakistanGateway() {}
	
	/*public PakistanGateway(String userId, String pwd) {
		
		
	}*/
	
	public boolean getBalence(int totalCount, OCSMSGateway gatewayObj) {
				
				
		try {
			
			String targetUrl = gatewayObj.getPostpaidBalURL()+"?"+(BAL_URL_GET_DATA);
					
			URL url = new URL(targetUrl.replace(URL_PH_ID, gatewayObj.getUserId()).replace(URL_PH_PWD, gatewayObj.getPwd()));
			
			logger.debug("url "+url.toString());
			
			HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
			
			
			urlconnection.setRequestMethod("GET");
			urlconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			urlconnection.setDoOutput(true);

							
			BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
			
			String decodedString;
			String response = "";
			while ((decodedString = in.readLine()) != null) {
				response += decodedString;
			}
			in.close();
			logger.debug("balence response is======>"+response);
			//if(true) return true;
			if(response == null) {
				
				//logger.error("exception while fetching balence");
				return false;
				
			}//if
			
			String balResp = parseBalResponse(response);
			
			if(balResp == null) {
				
				//logger.error("exception while fetching balence");
				return false;
				
			}//if
			double balence = 0;
			try{
				 balence = Double.parseDouble(balResp);
			}
			catch (NumberFormatException e) {
				logger.error("Exception", e);
				return false;
			} 
			boolean canProceed = true;
			
			if(balence <= totalCount){
				
				canProceed = false;
				
				
			}//if
			
			if( !canProceed ) {
				
				try {
					if(emailQueueDao == null) {
						emailQueueDao = (EmailQueueDao)applicationContext.getBean("emailQueueDao");
						
					}//if
					
					String message = PropertyUtil.getPropertyValueFromDB(Constants.SMS_LOW_CREDITS_WARN_TEXT);
					String emailId = PropertyUtil.getPropertyValueFromDB("SupportEmailId");
					
				 	EmailQueue emailQueue = new EmailQueue("Ran out of SMS Credits-Outreach", message, 
				 			Constants.EQ_TYPE_LOW_SMS_CREDITS, "Active", emailId, Calendar.getInstance());
				 	
				 	emailQueueDao.saveOrUpdate(emailQueue);
			 	} catch(Exception e) {
			 		logger.error("Exception ::" , e);
			 	}
				
			}
			
			return canProceed;
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}
			
			
		return false;
		 
		 
		 
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
	
	
	
	public String[] sendMsg(String msg, boolean isSingle, String toMobile, String userId, String pwd, String senderID) throws BaseServiceException{
		
		
		//here no need to prepare a msg fro each mobile hence send it as a post request when multiple mobiles are there
		//assuming that usually from subscriber user will not enter more than 100 numbers at a time
		String submitResponse = isSingle ? sendGet(msg, isSingle, toMobile, userId, pwd, senderID) :
			sendPost(msg, isSingle, toMobile, userId, pwd, senderID);
		
		if(submitResponse == null){
			
			throw new BaseServiceException("Exception in submitting the msg, not received the response");
		}
		
		String[] responseArr = parseSubmitResponse(submitResponse);
		
		if(responseArr == null) {
			
			throw new BaseServiceException("not received the transactionId");
		}
		
		logger.debug("transactionId = "+responseArr[1]);
		
		//TODO need to store the Tid against to this submission
		return responseArr;
		
	}
	
	private String sendGet(String msg, boolean isSingle, String toMobile, String userId, String pwd, String mask) {
		
		try {
			String targetUrl = MSG_SUBMIT_GET_URL+"?"+(POST_DATA.replace(URL_PH_ID, userId)
								.replace(URL_PH_PWD, pwd)
								.replace(URL_PH_TO, toMobile)
								.replace(URL_PH_LANG, "English")
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
			logger.debug("balence response is======>"+response);
			
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
		
		try {
			String response = Constants.STRING_NILL;
			String targetUrl = MSG_SUBMIT_GET_URL;
						
			URL url = new URL(targetUrl);
			
			String postData = (POST_DATA.replace(URL_PH_ID, userId)
					.replace(URL_PH_PWD, pwd)
					.replace(URL_PH_TO, toMobile)
					.replace(URL_PH_LANG, "English")
					.replace(URL_PH_MASK, URLEncoder.encode(mask, "UTF-8"))
					.replace(URL_PH_MSG, URLEncoder.encode(msg, "UTF-8")) // TODO check
					.replace(URL_PH_TYPE, "Xml"));
			
			logger.info("Data to be sent is=====>"+postData);
			
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
				response += decodedString;
			}
			in.close();
			logger.info("response is======>"+response);
	
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
	
	public String[] parseSubmitResponse(String submitResponse) {

		String[] responseArr = new String[2];

		try {

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(submitResponse.getBytes()));
			doc.getDocumentElement().normalize();

			Element docElement = doc.getDocumentElement();
			//<?xml version="1.0" encoding="UTF-8" ?>
			//<corpsms>	<code>206</code>	<type>Error</type>	<response>Invalid Destination Number, Please Check Format i-e 92345xxxxxxx</response>	<transactionID>321919605</transactionID></corpsms>
			//<type>Error</type>
			NodeList codeNodeLst = doc.getElementsByTagName("code");
			if(codeNodeLst == null) return null;
			logger.info("status message is====>"+codeNodeLst.getLength());
			String responseCode = null;
			boolean isSuccess = false;
			for(int i=0; i<codeNodeLst.getLength(); i++) {

				Node node = codeNodeLst.item(i);
				Element element = (Element)node;
				//Attr idAttr =  element.getAttributeNode("id");
				responseCode = element.getTextContent().trim();
				if(!responseCode.equals("300")){
					logger.debug("there is an error some where");
				}//if
			}//for
			if(!isSuccess){

				NodeList responseNodeLst = doc.getElementsByTagName("response");
				if(responseNodeLst == null) return null;
				logger.info("status message is====>"+responseNodeLst.getLength());
				String errorResponse = null;
				for(int i=0; i<responseNodeLst.getLength(); i++) {

					Node node = responseNodeLst.item(i);
					Element element = (Element)node;
					//Attr idAttr =  element.getAttributeNode("id");
					errorResponse = element.getTextContent().trim();
					logger.debug("errorResponse is====>"+errorResponse);
					//return null;
				}
			}

			NodeList TIDNodeLst = doc.getElementsByTagName("transactionID");
			if(TIDNodeLst == null) return null;
			String transactionId = null;
			logger.info("status message is====>"+TIDNodeLst.getLength());
			for(int i=0; i<TIDNodeLst.getLength(); i++) {

				Node node = TIDNodeLst.item(i);
				Element element = (Element)node;
				//Attr idAttr =  element.getAttributeNode("id");
				transactionId = element.getTextContent().trim();
				responseArr[0] = responseCode;
				responseArr[1] = transactionId;
				return responseArr;
				//return transactionId;

			}


		} catch (ParserConfigurationException e) {
			logger.error("Exception ::" , e);
		} catch (SAXException e) {
			logger.error("Exception ::" , e);
		} catch (IOException e) {
			logger.error("Exception ::" , e);
		}

		return null;
		/*String transactionId = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(submitResponse.getBytes()));
			doc.getDocumentElement().normalize();

			Element docElement = doc.getDocumentElement();

			NodeList nodeLst = doc.getElementsByTagName("transactionID");
			if(nodeLst == null) return null;
			logger.info("status message is====>"+nodeLst.getLength());
			for(int i=0; i<nodeLst.getLength(); i++) {

				Node node = nodeLst.item(i);
				Element element = (Element)node;
				//Attr idAttr =  element.getAttributeNode("id");
				transactionId = element.getTextContent().trim();

				return transactionId;

			}
		} catch (ParserConfigurationException e) {
			logger.error("Exception ::" , e);
		} catch (SAXException e) {
			logger.error("Exception ::" , e);
		} catch (IOException e) {
			logger.error("Exception ::" , e);
		}

		return transactionId;*/
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
		OutreachPakistanGateway obj = new OutreachPakistanGateway();
		
		//String retStr = obj.parseBalResponse(balResponse);
		//String retStr = obj.parseSubmitResponse(submitResponse);
		obj.sendGet("test", true, "929123423232", "92test11", "cloud2233", "Outreach");//(1, "92test11", "cloud2233");
		//logger.debug(retStr);
		
	}
}
