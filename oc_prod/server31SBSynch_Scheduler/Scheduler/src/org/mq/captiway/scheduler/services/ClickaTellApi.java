package org.mq.captiway.scheduler.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.beans.SMSSettings;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.CouponCodesDao;
import org.mq.captiway.scheduler.dao.EmailQueueDao;
import org.mq.captiway.scheduler.dao.EmailQueueDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.ReplacePlaceHolders;
import org.mq.captiway.scheduler.utility.SMSStatusCodes;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ClickaTellApi {
	
	/**<!DOCTYPE REQ SYSTEM http://www.clickatell.com/downloads/xml_dtd.txt>

	 * <clickAPI>
		<sendMsg>
		<api_id>1</api_id>
		<user>demo</user>
		<password>demo</password>
		<to>123456567890123</to>
		<text>Initial text message</text>
		<from>me</from>
		</sendMsg>
		</clickAPI>

	 */
	
	/**
	 * URL             :  https://www.clickatell.com/login/
Username  : optculture10
Password   : optculture10 
Product      : Central Api
Client ID     : HCO503
	 */

//private ApplicationContext applicationContext;


	
	
/*public ApplicationContext getApplicationContext() {
		return applicationContext;
	}



	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}*/
	public ClickaTellApi() {
		// TODO Auto-generated constructor stub
		//smsMsgsList = new ArrayList<String>();
		smsStructureList = new ArrayList<String>();
		smsCount = 0;
		sentIdsSet = new LinkedHashSet<String>();
	}

	/*public ClickaTellApi(EmailQueueDao emailQueueDao) {
		// TODO Auto-generated constructor stub
		//smsMsgsList = new ArrayList<String>();
		this.emailQueueDao = emailQueueDao;
		smsStructureList = new ArrayList<String>();
		smsCount = 0;
		sentIdsSet = new LinkedHashSet<String>();
	}*/

	
	/*public ClickaTellApi() {
		
		this.applicationContext = applicationContext;
		smsStructureList = new ArrayList<String>();
		smsCount = 0;
		sentIdsSet = new LinkedHashSet<String>();
	}*/
	
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
private final String data = "<!DOCTYPE REQ SYSTEM http://www.clickatell.com/downloads/xml_dtd.txt>";


private final static String singleSmsStructure = "<clickAPI>" +
													"<sendMsg>" +
													"<api_id><APIID></api_id>" +
													/*"<user>optculture10</user>" +
													"<password>optculture10</password>"+
													"<from>888555</from>" +
													"<to>18322321356</to>" +
													"<text>test msg</text>" +
													"<climsgid>A;=;12323</climsgid>"+*/
													"<user><USERID></user>" +
													"<password><PWD></password>" +
													"<from><SENDERID></from>" +
													"<mo>1</mo>"+
													"<to><VAL_TO></to>" +
													"<text><VAL_TXT></text>" +
													"<callback>3</callback>"+
													"</sendMsg>" +
													"</clickAPI>";

private final static String autoResponseStructure = "<clickAPI>" +
											"<sendMsg>" +
											"<api_id><APIID></api_id>" +
											/*"<user>optculture10</user>" +
											"<password>optculture10</password>"+
											"<from>888555</from>" +
											"<to>18322321356</to>" +
											"<text>test msg</text>" +
											"<climsgid>A;=;12323</climsgid>"+*/
											"<user><USERID></user>" +
											"<password><PWD></password>" +
											"<from><SENDERID></from>" +
											"<mo>1</mo>"+
											"<to><VAL_TO></to>" +
											"<text><VAL_TXT></text>" +
											"<climsgid><VAL_SENTID></climsgid>"+
											"<callback>3</callback>"+
											"<concat>3</concat>"+
											"</sendMsg>" +
											"</clickAPI>";



private final String authentReqStructure = "<clickAPI>" +
											"<auth>" +
											"<api_id><APIID></api_id>" +
											"<user><USERID></user>" +
											"<password><PWD></password>" +
											"</auth>" +
											"</clickAPI>";/*"<clickAPI>" +
											"<auth>" +
											"<api_id><APIID></api_id>" +
											"<user>optculture10</user>" +
											"<password>optculture10</password>" +
											"</auth>" +
											"</clickAPI>";*/


private final String msgReqStructure = "<clickAPI><MESSAGESTRUCTURE></clickAPI>";
	
private final String requestStructure2_way = "<sendMsg>" +
											"<session_id><VAL_SESSIONID></session_id>" +
											"<from><SENDERID></from>"+
											"<mo>1</mo>"+
											"<to><VAL_TO></to>" +
											"<callback>3</callback>"+
											"<concat>3</concat>"+
											"<text><VAL_TXT></text>" +
											"<climsgid><VAL_SENTID></climsgid>" +
											"<sequence_no><VAL_REPID></sequence_no>"+
											"</sendMsg>" ;
		

private final String requestStructure = "<sendMsg>" +
										"<session_id><VAL_SESSIONID></session_id>" +
										"<from><SENDERID></from>"+
										"<to><VAL_TO></to>" +
										"<callback>3</callback>"+
										"<concat>3</concat>"+
										"<text><VAL_TXT></text>" +
										"<mo>1</mo>"+
										"<climsgid><VAL_SENTID></climsgid>" +
										"<sequence_no><VAL_REPID></sequence_no>"+
										"</sendMsg>" ;/*"<sendMsg>" +
											"<session_id><VAL_SESSIONID></session_id>" +
											"<from>888555</from>"+
											"<to><VAL_TO></to>" +
											"<callback>3</callback>"+
											"<concat>3</concat>"+
											"<text><VAL_TXT></text>" +
											"<mo>1</mo>"+
											"<climsgid><VAL_SENTID></climsgid>" +
											"<sequence_no><VAL_REPID></sequence_no>"+
											"</sendMsg>" ;*/




private final String doubleOptInRequestStructure = "<sendMsg>" +
													"<session_id><VAL_SESSIONID></session_id>" +
													"<from><SENDERID></from>"+
													"<to><VAL_TO></to>" +
													"<callback>3</callback>"+
													"<concat>3</concat>"+
													"<text><VAL_TXT></text>" +
													"<mo>1</mo>"+
													"<climsgid><VAL_TOMOBILE></climsgid>" +
													"<sequence_no><VAL_USERID></sequence_no>"+
													"</sendMsg>" ;/*"<sendMsg>" +
													"<session_id><VAL_SESSIONID></session_id>" +
													"<from>888555</from>"+
													"<to><VAL_TO></to>" +
													"<callback>3</callback>"+
													"<concat>3</concat>"+
													"<text><VAL_TXT></text>" +
													"<mo>1</mo>"+
													"<climsgid><VAL_TOMOBILE></climsgid>" +
													"<sequence_no><VAL_USERID></sequence_no>"+
													"</sendMsg>" ;*/

private final static String singleDoubleOptinStructure = "<clickAPI>" +
													"<sendMsg>" +
													"<api_id><APIID></api_id>" +
													"<user><USERID></user>" +
													"<password><PWD></password>" +
													"<from>888555</from>" +
													"<mo>1</mo>"+
													"<to><VAL_TO></to>" +
													"<text><VAL_TXT></text>" +
													"<concat>3</concat>"+
													"<callback>3</callback>"+
													"</sendMsg>" +
													"</clickAPI>";
	/*private final String messageStructure = "<MESSAGE> \n <TEXT><VAL_TXT></TEXT> \n <TYPE><VAL_TYPE></TYPE> \n <MID><VAL_MID></MID> \n"+
									         "<SMSSTRUCTURE> </MESSAGE> \n";
	
	private final String smsStructure = "<SMS FROM='<VAL_FROM>' TO='<VAL_TO>' INDEX ='<VAL_INDEX>'></SMS> \n";
	*/
	private List<String> smsStructureList;
	private int smsCount;
	private String sessionId;
	private LinkedHashSet<String> sentIdsSet;
	private ContactsDao contactsDao;
	private boolean isRanOutOfContacts;
	
 public List<String>  pingClickatellToSendRestOfSMS() {
	 List<String> retList = null;
	 String tempReq = "";
		String tempData ="";
		//**** need to prepare the msgStructure*****
		if(smsStructureList == null || smsStructureList.isEmpty()) return null;
		tempReq = msgReqStructure;
			String tempMsgStr = "";
			for (String msgStructure : smsStructureList) {
				tempMsgStr += msgStructure;
				
				
			}
			if(logger.isDebugEnabled()) logger.debug("msgToBeSent "+tempReq);
			tempData = tempReq.replace("<MESSAGESTRUCTURE>", tempMsgStr);
			
			if(logger.isDebugEnabled()) logger.debug(" finally the tempdata is=====>"+tempData);
			
		//responseList = sendSMS(tempData); 
			String response = sendSMS(tempData);
			try {
				retList = parseInitialResponse(response, sentIdsSet);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ", e);
			}
			smsStructureList.clear();
			//sentIdsSet.clear();
			smsCount = 0;
			sessionId = null;
			isRanOutOfContacts = false;
			sentIdsSet.clear();
	 
			return retList;
}







 //static Set<String> sentIdsSet = new LinkedHashSet<String>();
 
 
 

	/**
	 * this method prepares XmlDocument to sent the SMS content, and sends it to NETCORE gateway
	 * @param messageContent
	 * @param msgType
	 * @param fromMobNum
	 * @param toMobNum
	 * @param msgSeq
	 * @throws Exception
	 */
	/*public List<String> prepareData(String messageContent, int msgType, String sentId,
		String fromMobNum, String toMobNum, String msgSeq, Long smsCampRepId,  String senderId) throws Exception {
	*/
 
 public List<String> prepareData(String messageContent, String msgType, String sentId,
			 String toMobNum, String msgSeq, Long smsCampRepId,  String senderId, String userId, String pwd, String apiID) throws Exception {
		 
		toMobNum = toMobNum.trim();
		if(!toMobNum.startsWith("1") && toMobNum.length()==10) {
			
			toMobNum = "1"+toMobNum;
			
		}
		
		if(sessionId == null) {
			
			sessionId = getSessionId(userId, pwd, apiID);
			
			
			if(logger.isDebugEnabled()) logger.debug("got sessionid :: "+sessionId);
			if(sessionId == null) {
				if(logger.isDebugEnabled()) logger.debug("got no session cant send SMS "); 
				return null;
			}
			
			
			
		}//if
		
		List<String> retList = null;
		String msgToBeSent = null;
		smsCount++;
		sentIdsSet.add(Constants.SMS_TYPE_CAMPAIGN+Constants.ADDR_COL_DELIMETER+sentId);
		if(logger.isDebugEnabled()) logger.debug("smsCount "+smsCount);
		
		if(msgType.equals(Constants.SMS_TYPE_OUTBOUND)) {//if(msgType == 1) {
			
			msgToBeSent = requestStructure.replace("<VAL_SESSIONID>", sessionId).replace("<SENDERID>", senderId).
							replace("<VAL_TO>", toMobNum ).replace("<VAL_TXT>", messageContent).
							replace("<VAL_SENTID>", Constants.SMS_TYPE_CAMPAIGN+Constants.ADDR_COL_DELIMETER+sentId).replace("<VAL_REPID>", ""+smsCampRepId);
			
		}else if(msgType.equals(Constants.SMS_TYPE_2_WAY)) {
			
			msgToBeSent = requestStructure2_way.replace("<VAL_SESSIONID>", sessionId).replace("<SENDERID>", senderId).
							replace("<VAL_TO>", toMobNum ).replace("<VAL_TXT>", messageContent).
							replace("<VAL_SENTID>", Constants.SMS_TYPE_CAMPAIGN+Constants.ADDR_COL_DELIMETER+sentId).replace("<VAL_REPID>", ""+smsCampRepId);
			
		}
		
		
		//sentIdsSet.add(sentId+"");
		
		if(logger.isDebugEnabled()) logger.debug("msgToBeSent "+msgToBeSent);
		smsStructureList.add(msgToBeSent);
		if(logger.isDebugEnabled()) logger.debug("size "+smsStructureList.size());
		
		String tempReq = "";
		String tempData ="";
		//to open a session for 
		//if((smsCount == 100 && limit) || (smsCount == size && !limit)) {
		if((smsCount == 100 )) {
			
			//**** need to prepare the msgStructure*****
			
		tempReq = msgReqStructure;
			String tempMsgStr = "";
			for (String msgStructure : smsStructureList) {
				tempMsgStr += msgStructure;
				
				
			}
			if(logger.isDebugEnabled()) logger.debug("msgToBeSent "+tempReq);
			tempData = tempReq.replace("<MESSAGESTRUCTURE>", tempMsgStr);
			
			if(logger.isDebugEnabled()) logger.debug(" finally the tempdata is=====>"+tempData);
			
		//responseList = sendSMS(tempData);
			String response = sendSMS(tempData);
			retList = parseInitialResponse(response, sentIdsSet);
			smsStructureList.clear();
			//sentIdsSet.clear();
			smsCount = 0;
			sessionId = null;
			isRanOutOfContacts = false;
			sentIdsSet.clear();
			
		}//if
		
		return retList;
		
	}
	/**
	 * 
	 * <clickAPI>
			<sendMsgResp>
				<apiMsgId>21324684b74e3b283a0ac5103330f4d3</apiMsgId>
				<sequence_no>874</sequence_no>
			</sendMsgResp>
			<sendMsgResp>
				<apiMsgId>a624a159aded2d916a37afe0c478919a</apiMsgId>
				<sequence_no>874</sequence_no>
			</sendMsgResp>
			<sendMsgResp>
				<apiMsgId>16e414c21ccd02143ac177309f66e814</apiMsgId>
				<sequence_no>874</sequence_no>
			</sendMsgResp>
		</clickAPI>
	 * 
	 * 
	 * 
	 */
	
	/**
	 * this method parse the response given by netCore after immediate sending
	 * @param response
	 * @return
	 */
	public static List<String> parseInitialResponse(String response, LinkedHashSet< String> sentIdsSet) {
		List<String> responseContentList = new ArrayList<String>();
		try {
			//response = response.substring(response.indexOf("<sendMsgResp>"));
			Node node = null;
			Node childNode = null;
			Element childElement = null;
			Element element = null;
			String apiMsgId = "";
			String sentId = "";
			//String seq = "";
			String desc = "";
			NodeList childNodeList = null;
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(response.getBytes()));
			doc.getDocumentElement().normalize();
			//reqid = ((Element)doc.getDocumentElement()).getAttribute("sendMsgResp");//request id specific to each sending
			
			
			NodeList nodeLst = doc.getElementsByTagName("sendMsgResp");//given to each <MESSAGE> tag
			if(logger.isInfoEnabled()) logger.info("the number of sendMsgResp tags are===>"+nodeLst.getLength());
			Iterator< String> iter = sentIdsSet.iterator();
			for(int i=0; i<nodeLst.getLength(); i++) {
				
				
				node = nodeLst.item(i);
				element = (Element)node;
				//guid = element.getAttribute("GUID");
				//sentId = element.getAttribute("ID");//sent id in our SMSCampaignSent
				
				//if(logger.isInfoEnabled()) logger.info("sent id is===>"+sentId+"node is====>"+node.toString());
				
				if(element.hasChildNodes()) {
					
					
					childNodeList = element.getChildNodes();
					for(int j=0; j<childNodeList.getLength(); j++) {
						
						
						childNode = childNodeList.item(j);
						//if(logger.isDebugEnabled()) logger.debug("my childNode is=====>"+childNode.getNodeName());
						if(childNode.getNodeName().equals("#text")) continue;
						
						if(childNode.getNodeName().equals("apiMsgId")) {
							
							apiMsgId = childNode.getTextContent();
							responseContentList.add(iter.next()+"|"+apiMsgId);
							
						}else if(childNode.getNodeName().equals("fault")){
							
							apiMsgId = childNode.getTextContent();
							if(apiMsgId.startsWith("114, Cannot route message") ) {
								
								apiMsgId = Constants.SMS_SUPP_TYPE_INVALID_MOBILE_NUMBER +
								Constants.ADDR_COL_DELIMETER + apiMsgId.substring(0,apiMsgId.indexOf(",")) ;
								
							}else if(apiMsgId.startsWith("301, No Credit Left")) {
								
								apiMsgId = SMSStatusCodes.CLICKATELL_STATUS_NO_CREDITS + 
								Constants.ADDR_COL_DELIMETER + apiMsgId.substring(0,apiMsgId.indexOf(",")) ;
								
							}else if(apiMsgId.startsWith("128, Number delisted")) {
								
								apiMsgId = SMSStatusCodes.CLICKATELL_STATUS_NUMBER_DELISTED +
								Constants.ADDR_COL_DELIMETER + apiMsgId.substring(0,apiMsgId.indexOf(",")) ;
							}
							responseContentList.add(iter.next()+"|"+apiMsgId);
							
						}
						
					}//for each sendMsgResp
					
					/*childNode = element.getFirstChild();
					//childElement = (Element)childNode;
					apiMsgId = childNode.getTextContent();
					responseContentList.add(iter.next()+"|"+apiMsgId);*/
					/*if(!element.getTextContent().trim().equalsIgnoreCase("")) {
						desc = element.getTextContent();
						desc = desc.replace(desc.substring(0, 3), "").trim();//to remove 3 digit ERROR codes
						
						if(desc.startsWith("Mobile number") && desc.endsWith("invalid")) {// to remove the invalid mobile number in description
							
							desc = "Invalid mobile number";
						}
						
						responseContentList.add(sentId+"|"+desc+"|"+reqid);
						
						desc = desc.replace(desc.substring(0, 3), "");//to remove 3 digit ERROR codes
						
						if(desc.contains("Invalid mobile number")) {// to remove the invalid mobile number in description
							
							desc = desc.replace(desc.substring(desc.indexOf("-",desc.indexOf("Invalid mobile number")), desc.length()), "");
						}
						
						responseContentList.add(sentId+"|"+desc+"|"+reqid);
						
					}else {// if no error occured
						
						responseContentList.add(sentId+"|"+"Status Pending"+"|"+reqid);
					}*/
				}//if
				
				
			
			}//for
			if(logger.isInfoEnabled()) logger.info("the response list is====>"+responseContentList);
			
			return responseContentList;
			
		}catch (UnsupportedEncodingException e) {
			logger.info("Exception",e);
			return null;
		} catch (MalformedURLException e) {
			logger.info("Exception",e);
			return null;
		} catch (IOException e) {
			logger.info("Exception",e);
			return null;
		} catch(Exception e){
			logger.info("Exception",e);
			return null;
		}
		
	}//parseInitialResponse
	
	
	
	/**
	 * this method helps us to get the session id we requested
	 * @param xmlStr represents the response to auth command
	 * @return session ID
	 */
	public static String parse(String xmlStr) {
		String reqId = "";
		Node node = null;
		Node childNode = null;
		Element element = null;
		
		String code = "";
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(xmlStr.getBytes()));
			doc.getDocumentElement().normalize();
			
			
			Element docElement = doc.getDocumentElement();
			
			NodeList nodeLst = doc.getElementsByTagName("authResp");//given to each <MESSAGE> tag
			if(logger.isDebugEnabled()) logger.debug("status message is====>"+nodeLst.getLength());
			for(int i=0; i<nodeLst.getLength(); i++) {
				
				
				node = nodeLst.item(i);
				element = (Element)node;
				return element.getTextContent().trim();
				
				
					
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
		}
		
		return code;
		
	}
	
	
	public static void main(String args[]) throws Exception {
	//NetCoreApi obj = new NetCoreApi();
	//String response = obj.prepareData("hai", "N", "123", "919848495956", "919490927928", "1");
		
	/*String response ="<?xml version=\"1.0\"?><clickAPI>       <sendMsgResp>           <apiMsgId>4ee7844a1f535d11c310bae23a601ad4</apiMsgId>           <sequence_no>"+
"13</sequence_no>   </sendMsgResp>  <sendMsgResp>           <apiMsgId>5142b079bc4175fdab9d9e860d6d8b19</apiMsgId>           <sequence_no>13</sequence_no>   </"+
"sendMsgResp>  <sendMsgResp>           <apiMsgId>825d23198740aaf98220eab7629a9bc3</apiMsgId>           <sequence_no>13</sequence_no>   </sendMsgResp></clickAP"+
"I>";*/

	/*String response = "<clickAPI><sendMsgResp><apiMsgId>21324684b74e3b283a0ac5103330f4d3</apiMsgId><sequence_no>874</sequence_no></sendMsgResp>"+
			"<sendMsgResp><apiMsgId>a624a159aded2d916a37afe0c478919a</apiMsgId><sequence_no>874</sequence_no></sendMsgResp><sendMsgResp>"+
				"<apiMsgId>16e414c21ccd02143ac177309f66e814</apiMsgId><sequence_no>874</sequence_no></sendMsgResp></clickAPI>";
	//String response = "<!DOCTYPE RESULT SYSTEM 'http://bulkpush.mytoday.com/BulkSms/BulkSmsRespV1.00.dtd'><RESULT REQID ='331177299'>	<MID SUBMITDATE='2011-06-02 13:19:46' ID='123' TAG = 'null' TID = '683648130'>  </MID></RESULT>";
*/	//String reqId = parse(response);
	//logger.debug("status message is====>"+reqId);
	/*LinkedHashSet<String> sentIdsSet = new LinkedHashSet<String>();
	
	sentIdsSet.add("101");
	sentIdsSet.add("102");
	sentIdsSet.add("103");
	parseInitialResponse(response, sentIdsSet);*/
		String autoresp = autoResponseStructure;
		System.out.println(sendSMS(autoresp));
		
		/*String balenceresponse =  "<clickAPI>	<getBalanceResp>		<ok>4885.780</ok>		<sequence_no></sequence_no>	</getBalanceResp></clickAPI>";
		String balence = parseBalenceResponse(balenceresponse);
		if(logger.isDebugEnabled()) logger.debug("balence ::"+balence);*/
	
	}
	
	public static String parseBalenceResponse(String balenceResponse) {
		
		String reqId = "";
		Node node = null;
		Node childNode = null;
		Element element = null;
		
		String code = "";
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(balenceResponse.getBytes()));
			doc.getDocumentElement().normalize();
			
			
			Element docElement = doc.getDocumentElement();
			
			NodeList nodeLst = doc.getElementsByTagName("ok");//given to each <MESSAGE> tag
			if(nodeLst == null) return null;
			if(logger.isDebugEnabled()) logger.debug("status message is====>"+nodeLst.getLength());
			for(int i=0; i<nodeLst.getLength(); i++) {
				
				
				node = nodeLst.item(i);
				element = (Element)node;
				return element.getTextContent().trim();
				
				
					
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
		}
		
		return code;
		
		
		
	}
	
	
	
	//public String getSessionId(String reqData) {
		
	public String getSessionId(String userID, String pwd, String apiID) {	
		try {
			
			String reqData = authentReqStructure.replace("<USERID>", userID).
					replace("<PWD>", pwd).replace("<APIID>", apiID);
			String postData = "";
			String response = "";
			
			postData += "data="+URLEncoder.encode(reqData, "UTF-8");
			if(logger.isDebugEnabled()) logger.debug("Data to be sent is=====>"+postData);
			
			URL url = new URL("http://api.clickatell.com/xml/xml");
			
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
			if(logger.isDebugEnabled()) logger.debug("response is======>"+response);
			return parse(response);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		}
		
		
		
		
	}//getSessionId
	
	
	
	
	/**
	 * this method sends the SMS to NETCORE Gateway 
	 * @param tempData
	 */
	public static String sendSMS(String tempData) {
		String response = "";
		try {
			//TODO need to open a url connection
			//append the  request parameter UserRequest along with the values to the url
			//capture the response as in Xml format which can be parsed further and get the GUID and required things further
			String postData = "";
			
			postData += "data="+URLEncoder.encode(tempData, "UTF-8");
			if(logger.isDebugEnabled()) logger.debug("Data to be sent is=====>"+postData);
			
			URL url = new URL("http://api.clickatell.com/xml/xml");
			
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
			if(logger.isDebugEnabled()) logger.debug("response is======>"+response);
			
			/***parse and update the status based on the initial response****/
			
			//List<String> responseList = parseInitialResponse(response);
	
		}catch(Exception e) {
			
			if(logger.isDebugEnabled()) logger.debug("Exception while sendind the SMS");
			logger.error("Exception ::::" , e);
			
		}
		return response;
	}//sendSMS
	
		
	
	public String sendSingleSms(String userId, String pwd, String apiID, String to,String text, String senderID) throws BaseServiceException{
		
		try {
			
			/*text = text.replace("|^", "[").replace("^|", "]");
			Set<String> coupPhSet = Utility.findCoupPlaceholders(text);
			
			if(coupPhSet != null  && coupPhSet.size() > 0) {
				
				ReplacePlaceHolders replacePlaceHolders = new ReplacePlaceHolders();
				text = replacePlaceHolders.replaceSMSAutoResponseContent(text, coupPhSet, to, null);
				
			}
			
			text = StringEscapeUtils.escapeHtml(text);*/
			if(to != null && to.length()==10 && !to.startsWith("1") ) {
				to = "1"+to;
			}
			
			String sendContent = singleSmsStructure.replace("<USERID>", userId).replace("<PWD>",pwd).replace("<APIID>", apiID).
					replace("<VAL_TO>", to).replace("<VAL_TXT>", text).replace("<SENDERID>", senderID); 
			
				String respoString = sendSMS(sendContent);
				
				respoString = parseAutoResponse(respoString);
				
				return respoString;
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("Exception while sending auto response");
		}
		
	
	}
	public String sendAutoResponse(String userId, String pwd, String apiID, String to, 
			String text, String senderID, String inboundObjId) throws BaseServiceException{
		
		
		try {
			
			/*text = text.replace("|^", "[").replace("^|", "]");
			Set<String> coupPhSet = Utility.findCoupPlaceholders(text);
			
			if(coupPhSet != null  && coupPhSet.size() > 0) {
				//coments
				CouponCodesDao couponCodesDao = null;
				
				try {
					couponCodesDao = (CouponCodesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new BaseServiceException("No Dao found with this id");
				}
				//comments
				ReplacePlaceHolders replacePlaceHolders = new ReplacePlaceHolders();
				text = replacePlaceHolders.replaceSMSAutoResponseContent(text, coupPhSet, to, null);
				
			}
			
			text = StringEscapeUtils.escapeHtml(text);*/
			
			if(to != null && to.length()==10 && !to.startsWith("1") ) {
				
				to = "1"+to;
			}
			String sendContent = autoResponseStructure.replace("<USERID>", userId).replace("<PWD>",pwd).replace("<APIID>", apiID).
					replace("<VAL_TO>", to).replace("<VAL_TXT>", text).replace("<SENDERID>", senderID).
					replace("<VAL_SENTID>", inboundObjId != null ? (Constants.SMS_TYPE_AUTORESPONSE + Constants.ADDR_COL_DELIMETER + inboundObjId) : ""); 
			
			//if(getBalance(1) ) { //check whether balance is sufficient or not?????
				
				String respoString = sendSMS(sendContent);
				
				respoString = parseAutoResponse(respoString);
				
				return respoString;
				
			//}//if
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("Exception while sending auto response");
		}
		
	}
	
	
	
	public void sendMultipleMobileDoubleOptin(String userId, String pwd, String apiID, Set<String> optinMobileSet, SMSSettings smsSettings) {

		ClickaTellSubmission clickaTellSubmission = new ClickaTellSubmission();
		clickaTellSubmission.sendMultipleMobileDoubleOptin(userId, pwd, apiID, optinMobileSet, smsSettings);
		
	}
	
	
	
	private static final String balanceReqStructure = "<clickAPI>" +
														"<getBalance>" +
														"<api_id><APIID></api_id>" +
														"<user><USERID></user>" +
														"<password><PWD></password>" +
														"</getBalance>" +
														"</clickAPI>";
														/*"<clickAPI>" +
														"<getBalance>" +
														"<api_id><APIID></api_id>" +
														"<user>optculture10</user>" +
														"<password>optculture10</password>" +
														"</getBalance>" +
														"</clickAPI>";*/
	
	//private EmailQueueDao emailQueueDao;
	
	/*public EmailQueueDao getEmailQueueDao() {
		return emailQueueDao;
	}



	public void setEmailQueueDao(EmailQueueDao emailQueueDao) {
		this.emailQueueDao = emailQueueDao;
	}*/



	public  boolean getBalance(int totalCount, String userId, String pwd, String APIID) {
		
		String dataToBeSent = balanceReqStructure.replace("<USERID>", userId)
						.replace("<PWD>", pwd).replace("<APIID>", APIID);
		
		String responStr = sendSMS(dataToBeSent);
		
		String balResp = parseBalenceResponse(responStr);
		
		if(balResp == null) {
			
			logger.error("exception while fetching balence");
			return false;
			
		}//if
		
		double balence = Double.parseDouble(balResp);
		
		boolean canProceed = true;
		
		if(balence <= totalCount){
			
			canProceed = false;
			
			
		}//if
		
		if( !canProceed ) {
			
			
			try {
				
				String message = PropertyUtil.getPropertyValueFromDB(Constants.SMS_LOW_CREDITS_WARN_TEXT);
				String emailId = PropertyUtil.getPropertyValueFromDB("SupportEmailId");
				
			 	EmailQueue emailQueue = new EmailQueue("Ran out of SMS Credits-Clickatell", message, 
			 			Constants.EQ_TYPE_LOW_SMS_CREDITS, "Active", emailId, new Date());
			 	EmailQueueDao emailQueueDao = null;
			 	EmailQueueDaoForDML emailQueueDaoForDML = null;
				try {
					emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName("emailQueueDao");
					emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("emailQueueDaoForDML");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					throw new BaseServiceException("Exception while getting emailQueueDao");
				}
			 		
			 	//emailQueueDao.saveOrUpdate(emailQueue);
			 	emailQueueDaoForDML.saveOrUpdate(emailQueue);
		 	} catch(Exception e) {
		 		logger.error("exception while saving email queue object");
		 		return canProceed;
		 	}
			
			
		}
		
		
		
		return canProceed;
	}
	
	
	
	
	
	/**
	 * this method parse the response given by netCore after immediate sending
	 * @param response
	 * @return
	 */
	public static List<String> parseMultipleOptInInitialResponse(String response, LinkedHashSet< String> sentIdsSet) {
		List<String> responseContentList = new ArrayList<String>();
		try {
			//response = response.substring(response.indexOf("<sendMsgResp>"));
			Node node = null;
			Node childNode = null;
			Element childElement = null;
			Element element = null;
			String apiMsgId = "";
			String sentId = "";
			//String seq = "";
			String desc = "";
			NodeList childNodeList = null;
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(response.getBytes()));
			doc.getDocumentElement().normalize();
			//reqid = ((Element)doc.getDocumentElement()).getAttribute("sendMsgResp");//request id specific to each sending
			
			
			NodeList nodeLst = doc.getElementsByTagName("sendMsgResp");//given to each <MESSAGE> tag
			if(logger.isInfoEnabled()) logger.info("the number of sendMsgResp tags are===>"+nodeLst.getLength());
			Iterator< String> iter = sentIdsSet.iterator();
			for(int i=0; i<nodeLst.getLength(); i++) {
				
				
				node = nodeLst.item(i);
				element = (Element)node;
				//guid = element.getAttribute("GUID");
				//sentId = element.getAttribute("ID");//sent id in our SMSCampaignSent
				
				//if(logger.isInfoEnabled()) logger.info("sent id is===>"+sentId+"node is====>"+node.toString());
				
				if(element.hasChildNodes()) {
					
					
					childNodeList = element.getChildNodes();
					for(int j=0; j<childNodeList.getLength(); j++) {
						
						
						childNode = childNodeList.item(j);
						//if(logger.isDebugEnabled()) logger.debug("my childNode is=====>"+childNode.getNodeName());
						if(childNode.getNodeName().equals("#text")) continue;
						
						if(childNode.getNodeName().equals("apiMsgId")) {
							
							apiMsgId = Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
							responseContentList.add(iter.next()+"|"+apiMsgId);
							
						}else if(childNode.getNodeName().equals("fault")){
							
							apiMsgId = childNode.getTextContent();
							if(apiMsgId.startsWith("114, Cannot route message") ) {
								
								apiMsgId = SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER +
								Constants.ADDR_COL_DELIMETER + apiMsgId.substring(0,apiMsgId.indexOf(",")) ;
								
							}else if(apiMsgId.startsWith("301, No Credit Left")) {
								
								apiMsgId = SMSStatusCodes.CLICKATELL_STATUS_NO_CREDITS + 
								Constants.ADDR_COL_DELIMETER + apiMsgId.substring(0,apiMsgId.indexOf(",")) ;
								
							}else if(apiMsgId.startsWith("128, Number delisted")) {
								
								apiMsgId = SMSStatusCodes.CLICKATELL_STATUS_NUMBER_DELISTED +
								Constants.ADDR_COL_DELIMETER + apiMsgId.substring(0,apiMsgId.indexOf(",")) ;
							}
							responseContentList.add(iter.next()+"|"+apiMsgId);
							
						}
						
					}//for each sendMsgResp
					
					/*childNode = element.getFirstChild();
					//childElement = (Element)childNode;
					apiMsgId = childNode.getTextContent();
					responseContentList.add(iter.next()+"|"+apiMsgId);*/
					/*if(!element.getTextContent().trim().equalsIgnoreCase("")) {
						desc = element.getTextContent();
						desc = desc.replace(desc.substring(0, 3), "").trim();//to remove 3 digit ERROR codes
						
						if(desc.startsWith("Mobile number") && desc.endsWith("invalid")) {// to remove the invalid mobile number in description
							
							desc = "Invalid mobile number";
						}
						
						responseContentList.add(sentId+"|"+desc+"|"+reqid);
						
						desc = desc.replace(desc.substring(0, 3), "");//to remove 3 digit ERROR codes
						
						if(desc.contains("Invalid mobile number")) {// to remove the invalid mobile number in description
							
							desc = desc.replace(desc.substring(desc.indexOf("-",desc.indexOf("Invalid mobile number")), desc.length()), "");
						}
						
						responseContentList.add(sentId+"|"+desc+"|"+reqid);
						
					}else {// if no error occured
						
						responseContentList.add(sentId+"|"+"Status Pending"+"|"+reqid);
					}*/
				}//if
				
				
			
			}//for
			if(logger.isInfoEnabled()) logger.info("the response list is====>"+responseContentList);
			
			return responseContentList;
			
		}catch (UnsupportedEncodingException e) {
			logger.info("Exception",e);
			return null;
		} catch (MalformedURLException e) {
			logger.info("Exception",e);
			return null;
		} catch (IOException e) {
			logger.info("Exception",e);
			return null;
		} catch(Exception e){
			logger.info("Exception",e);
			return null;
		}
		
	}//parseInitialResponse

	
	/**
	 * this method sets the status in SMSCampaignSent received when after immediate sending
	 * @param updateContentList
	 */
	public  void updateInitialStatusFromClikaTell(List<String> updateContentList, Users userId) {
		
		

		try {
			ContactsDao contactsDao = null;
			ContactsDaoForDML contactsDaoForDML = null;
			try {
				contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
				contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				throw new BaseServiceException("Exception while getting contactsDao");
			}
				String[] respCnt = null;
				//String queryStr;
				boolean isError = false;
				String status = null;
				String mobileStatus = null;
				if(logger.isDebugEnabled()) logger.debug("updateContentList in updateSMSCampSentStatus is===>"+updateContentList);
				if(updateContentList != null && updateContentList.size()>0){
					for (String response : updateContentList) {
						respCnt = response.split("\\|");
						//logger.debug("respCnt[0]"+respCnt[0]+"  respCnt[1] "+respCnt[1]);
						//queryStr="update sms_campaign_sent set status='"+respCnt[1]+"' where sent_id="+respCnt[0];
						//smsCampaignSentDao.updateInitialStatus(respCnt[0], respCnt[1]);
						
						String mobile = respCnt[0];
						
						/*if(respCnt[0].contains("000000000")) {
							String actualSentId = respCnt[0];
							actualSentId = actualSentId.substring(0,actualSentId.lastIndexOf("000000000"));
							respCnt[0] = actualSentId;
						}*/
						if(respCnt[1].startsWith(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER)) {
							
							//TODO need get the user object from sent-report-user
							status = SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER;
							mobileStatus = SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER;
							
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
							
							isError = false;
							if(logger.isDebugEnabled()) logger.debug("mobile ::"+mobile+" status::"+mobileStatus +" isError ::"+isError);
							/*int updateCount = contactsDao.updatemobileStatus(mobile, mobileStatus, userId);//updateApiMsgId((actualSentId).trim(), smsCampRepId, respCnt[1] );
							mobileStatus = Constants.CON_STATUS_ACTIVE;*/
						}
						
						if(isError == true) {
							try {
								
								if(logger.isDebugEnabled()) logger.debug("mobile ::"+mobile+" status::"+mobileStatus +" isError ::"+isError);
								contactsDaoForDML.updatemobileStatus(mobile, mobileStatus, userId);
							} catch (Exception e) {
								logger.error("Exception ::::" , e);
							}
						}
						
					}//for
				}//if
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ::::" , e);
		}
	
		
		
	}//
	
	
	
	
	
	class ClickaTellSubmission extends Thread {
		
		
		
		public void sendMultipleMobileDoubleOptin(String userID, String pwd, String apiID, Set<String> optinMobileSet, SMSSettings smsSettings) {
				
			if(logger.isDebugEnabled()) logger.debug("performing mobile optin here....");
				String messageContent = smsSettings.getAutoResponse();
				
				if(messageContent == null) {
					
					if(logger.isErrorEnabled()) logger.error("NO message found to be send");
					return;
					
				}
				String sessionId = getSessionId(userID, pwd, apiID);
				
				
				if(sessionId == null) {
					if(logger.isErrorEnabled()) logger.error("got no session cant send SMS "); 
					return ;
				}
				
				if(SMSStatusCodes.optOutFooterMap.get(smsSettings.getUserId().getCountryType())){
					
					messageContent = smsSettings.getMessageHeader() != null ? (smsSettings.getMessageHeader()+" "+ messageContent) : (""  + messageContent);
				}
				
				//messageContent = StringEscapeUtils.escapeHtml(messageContent);
				
				long userId = smsSettings.getUserId().getUserId().longValue();
				
				messageContent = messageContent.replace("|^", "[").replace("^|", "]");
				
				Set<String> coupPhSet = Utility.findCoupPlaceholders(messageContent);
				
				ReplacePlaceHolders replacePlaceHolders = null;
				boolean isFillCoup = false;
				
				if(coupPhSet != null  && coupPhSet.size() > 0) {
					replacePlaceHolders = new ReplacePlaceHolders();
					isFillCoup = true;
					/*CouponCodesDao couponCodesDao = null;
					
					try {
						couponCodesDao = (CouponCodesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						throw new BaseServiceException("No Dao found with this id");
					}*/
					
					
				}
				
				List<String> smsStructureList = new ArrayList<String>();
				LinkedHashSet<String> sentIdsSet = new LinkedHashSet<String>();
				List<String> retList = null;
				for (String toMobNum : optinMobileSet) {
					String text = messageContent;
					toMobNum = toMobNum.trim();
					
					if(!toMobNum.startsWith("1") && toMobNum.length()==10) {
						
						toMobNum = "1"+toMobNum;
						
					}//if
					
					if(isFillCoup) {
						
						try {
							text = replacePlaceHolders.replaceSMSAutoResponseContent(text, coupPhSet, toMobNum, null);
							
						} catch (BaseServiceException e) {
							// TODO Auto-generated catch block
							logger.error("exception while getting the coupon placehlders", e);
						}
					}
					text = StringEscapeUtils.escapeHtml(text);
					String msgToBeSent = doubleOptInRequestStructure.replace("<VAL_SESSIONID>", sessionId).
										replace("<VAL_TO>", toMobNum ).
										replace("<VAL_TXT>", text).
										replace("<VAL_TOMOBILE>", toMobNum).replace("<VAL_USERID>", ""+userId).
										replace("<SENDERID>", smsSettings.getSenderId() != null ? smsSettings.getSenderId() : "888555");
										
					
					smsStructureList.add(msgToBeSent);
					sentIdsSet.add(toMobNum);
					
				}//for
				

				
				//**** need to prepare the msgStructure*****
				String tempReq = "";
				String tempData ="";	
				tempReq = msgReqStructure;
				String tempMsgStr = "";
				for (String msgStructure : smsStructureList) {
					tempMsgStr += msgStructure;
					
					
				}
				if(logger.isDebugEnabled()) logger.debug("msgToBeSent "+tempReq);
				tempData = tempReq.replace("<MESSAGESTRUCTURE>", tempMsgStr);
				
				if(logger.isDebugEnabled()) logger.debug(" finally the tempdata is=====>"+tempData);
				
				
				
				String response = sendSMS(tempData);
				retList = parseMultipleOptInInitialResponse(response, sentIdsSet);
				smsStructureList.clear();
				//sentIdsSet.clear();
				sessionId = null;
				sentIdsSet.clear();
				
				if(retList != null && retList.size()>0){
					
					/*
					 * This hasbeen commented to avoid the insertion of the SMSdelivery 
					 * report which is related to the fetching of the reports as per the old design.
					 * It may useful as the alternative of the fetching process in which captiway only
					 * connects to NetCore and requests the required reports unlike the ping back/push back URL. 
					 */
					
					
					//saveRequest(responseList.get(0).split("\\|")[2], smsCampRepId);
					updateInitialStatusFromClikaTell(retList,smsSettings.getUserId());
				}
				
				if(logger.isDebugEnabled()) logger.debug("completed mobile optin here....");
				
			}//sendMultipleMobileDoubleOptin
			
			
			
			
		}

// Added for DR extraction
	
	public String sendSingleMobileDoubleOptin(String userID, String pwd, String apiID, String to, String text, SMSSettings smsSettings) throws BaseServiceException{
		
		
		
		text = text.replace("|^", "[").replace("^|", "]");
		Set<String> coupPhSet = Utility.findCoupPlaceholders(text);
		
		if(coupPhSet != null  && coupPhSet.size() > 0) {
			
			/*CouponCodesDao couponCodesDao = null;
			
			try {
				couponCodesDao = (CouponCodesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new BaseServiceException("No Dao found with this id");
			}*/
			ReplacePlaceHolders replacePlaceHolders = new ReplacePlaceHolders();
			text = replacePlaceHolders.replaceSMSAutoResponseContent(text, coupPhSet, to, null);
			
		}
		if(text == null) {
			
			return Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
			
		}
		text = StringEscapeUtils.escapeHtml(text);
		
		if(to != null && to.length()==10 && !to.startsWith("1") ) {
			
			to = "1"+to;
		}
		
		String sendContent = singleDoubleOptinStructure.replace("<USERID>", userID).replace("<PWD>",pwd).replace("<APIID>",apiID).
				replace("<VAL_TO>", to).replace("<VAL_TXT>", text).replace("<SENDERID>", smsSettings.getSenderId() != null ? smsSettings.getSenderId() : "888555"); 
		
		
		
		String response = sendSMS(sendContent);
		return parseSingleOptInInitialResponse(response);
		
		
	}//sendSingleMobileDoubleOptin
	
	
	
	
	/**
	 * this method parse the response given by netCore after immediate sending
	 * @param response
	 * @return
	 */
	public static String parseSingleOptInInitialResponse(String response) {
		List<String> responseContentList = new ArrayList<String>();
		try {
			//response = response.substring(response.indexOf("<sendMsgResp>"));
			Node node = null;
			Node childNode = null;
			Element childElement = null;
			Element element = null;
			String apiMsgId = "";
			String sentId = "";
			//String seq = "";
			String desc = "";
			NodeList childNodeList = null;
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(response.getBytes()));
			doc.getDocumentElement().normalize();
			//reqid = ((Element)doc.getDocumentElement()).getAttribute("sendMsgResp");//request id specific to each sending
			
			
			NodeList nodeLst = doc.getElementsByTagName("sendMsgResp");//given to each <MESSAGE> tag
			//logger.info("the number of sendMsgResp tags are===>"+nodeLst.getLength());
			//Iterator< String> iter = sentIdsSet.iterator();
			for(int i=0; i<nodeLst.getLength(); i++) {
				
				
				node = nodeLst.item(i);
				element = (Element)node;
				//guid = element.getAttribute("GUID");
				//sentId = element.getAttribute("ID");//sent id in our SMSCampaignSent
				
				//logger.info("sent id is===>"+sentId+"node is====>"+node.toString());
				
				if(element.hasChildNodes()) {
					
					
					childNodeList = element.getChildNodes();
					for(int j=0; j<childNodeList.getLength(); j++) {
						
						
						childNode = childNodeList.item(j);
						//logger.info("my childNode is=====>"+childNode.getNodeName());
						if(childNode.getNodeName().equals("#text")) continue;
						
						if(childNode.getNodeName().equals("fault")){
							
							apiMsgId = childNode.getTextContent();
							if(apiMsgId.startsWith("114, Cannot route message") ) {
								apiMsgId = SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER;
								return apiMsgId;
							}else if(apiMsgId.startsWith("128, Number delisted")) {
								
								apiMsgId = SMSStatusCodes.CLICKATELL_STATUS_NUMBER_DELISTED ;
								return apiMsgId;
							}
							//responseContentList.add(iter.next()+"|"+apiMsgId);
							
						}
						
					}//for each sendMsgResp
					
					/*childNode = element.getFirstChild();
					//childElement = (Element)childNode;
					apiMsgId = childNode.getTextContent();
					responseContentList.add(iter.next()+"|"+apiMsgId);*/
					/*if(!element.getTextContent().trim().equalsIgnoreCase("")) {
						desc = element.getTextContent();
						desc = desc.replace(desc.substring(0, 3), "").trim();//to remove 3 digit ERROR codes
						
						if(desc.startsWith("Mobile number") && desc.endsWith("invalid")) {// to remove the invalid mobile number in description
							
							desc = "Invalid mobile number";
						}
						
						responseContentList.add(sentId+"|"+desc+"|"+reqid);
						
						desc = desc.replace(desc.substring(0, 3), "");//to remove 3 digit ERROR codes
						
						if(desc.contains("Invalid mobile number")) {// to remove the invalid mobile number in description
							
							desc = desc.replace(desc.substring(desc.indexOf("-",desc.indexOf("Invalid mobile number")), desc.length()), "");
						}
						
						responseContentList.add(sentId+"|"+desc+"|"+reqid);
						
					}else {// if no error occured
						
						responseContentList.add(sentId+"|"+"Status Pending"+"|"+reqid);
					}*/
				}//if
				
				
			
			}//for
			//.info("the response list is====>"+responseContentList);
			
			return Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
			
		}catch (UnsupportedEncodingException e) {
			//logger.info("Exception",e);
			return null;
		} catch (MalformedURLException e) {
			//logger.info("Exception",e);
			return null;
		} catch (IOException e) {
			//logger.info("Exception",e);
			return null;
		} catch(Exception e){
			//logger.info("Exception",e);
			return null;
		}
		
	}//parseInitialResponse

	public static String parseAutoResponse(String response) {
		//List<String> responseContentList = new ArrayList<String>();
		try {
			//response = response.substring(response.indexOf("<sendMsgResp>"));
			Node node = null;
			Node childNode = null;
			Element childElement = null;
			Element element = null;
			String apiMsgId = null;
			String sentId = "";
			//String seq = "";
			String desc = "";
			NodeList childNodeList = null;
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(response.getBytes()));
			doc.getDocumentElement().normalize();
			//reqid = ((Element)doc.getDocumentElement()).getAttribute("sendMsgResp");//request id specific to each sending
			
			
			NodeList nodeLst = doc.getElementsByTagName("sendMsgResp");//given to each <MESSAGE> tag
			if(logger.isInfoEnabled()) logger.info("the number of sendMsgResp tags are===>"+nodeLst.getLength());
			//Iterator< String> iter = sentIdsSet.iterator();
			for(int i=0; i<nodeLst.getLength(); i++) {
				
				
				node = nodeLst.item(i);
				element = (Element)node;
				//guid = element.getAttribute("GUID");
				//sentId = element.getAttribute("ID");//sent id in our SMSCampaignSent
				
				//if(logger.isInfoEnabled()) logger.info("sent id is===>"+sentId+"node is====>"+node.toString());
				
				if(element.hasChildNodes()) {
					
					
					childNodeList = element.getChildNodes();
					for(int j=0; j<childNodeList.getLength(); j++) {
						
						
						childNode = childNodeList.item(j);
						//if(logger.isDebugEnabled()) logger.debug("my childNode is=====>"+childNode.getNodeName());
						if(childNode.getNodeName().equals("#text")) continue;
						
						if(childNode.getNodeName().equals("apiMsgId")) {
							
							apiMsgId = childNode.getTextContent();
							return apiMsgId;
							
						}else if(childNode.getNodeName().equals("fault")){
							
							apiMsgId = childNode.getTextContent();
							if(apiMsgId.startsWith("114, Cannot route message") ) {
								
								apiMsgId = Constants.SMS_SUPP_TYPE_INVALID_MOBILE_NUMBER +
								Constants.ADDR_COL_DELIMETER + apiMsgId.substring(0,apiMsgId.indexOf(",")) ;
								
							}else if(apiMsgId.startsWith("301, No Credit Left")) {
								
								apiMsgId = SMSStatusCodes.CLICKATELL_STATUS_NO_CREDITS + 
								Constants.ADDR_COL_DELIMETER + apiMsgId.substring(0,apiMsgId.indexOf(",")) ;
								
							}else if(apiMsgId.startsWith("128, Number delisted")) {
								
								apiMsgId = SMSStatusCodes.CLICKATELL_STATUS_NUMBER_DELISTED +
								Constants.ADDR_COL_DELIMETER + apiMsgId.substring(0,apiMsgId.indexOf(",")) ;
							}
							
						}
						
					}//for each sendMsgResp
					
					/*childNode = element.getFirstChild();
					//childElement = (Element)childNode;
					apiMsgId = childNode.getTextContent();
					responseContentList.add(iter.next()+"|"+apiMsgId);*/
					/*if(!element.getTextContent().trim().equalsIgnoreCase("")) {
						desc = element.getTextContent();
						desc = desc.replace(desc.substring(0, 3), "").trim();//to remove 3 digit ERROR codes
						
						if(desc.startsWith("Mobile number") && desc.endsWith("invalid")) {// to remove the invalid mobile number in description
							
							desc = "Invalid mobile number";
						}
						
						responseContentList.add(sentId+"|"+desc+"|"+reqid);
						
						desc = desc.replace(desc.substring(0, 3), "");//to remove 3 digit ERROR codes
						
						if(desc.contains("Invalid mobile number")) {// to remove the invalid mobile number in description
							
							desc = desc.replace(desc.substring(desc.indexOf("-",desc.indexOf("Invalid mobile number")), desc.length()), "");
						}
						
						responseContentList.add(sentId+"|"+desc+"|"+reqid);
						
					}else {// if no error occured
						
						responseContentList.add(sentId+"|"+"Status Pending"+"|"+reqid);
					}*/
				}//if
				
				
			
			}//for
			if(logger.isInfoEnabled()) logger.info("the response list is====>"+apiMsgId);
			
			return apiMsgId;
			
		}catch (UnsupportedEncodingException e) {
			logger.info("Exception",e);
			return null;
		} catch (MalformedURLException e) {
			logger.info("Exception",e);
			return null;
		} catch (IOException e) {
			logger.info("Exception",e);
			return null;
		} catch(Exception e){
			logger.info("Exception",e);
			return null;
		}
		
	}//parseInitialResponse
	
	
	
	
}
