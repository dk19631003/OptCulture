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
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.OCSMSGateway;
import org.mq.captiway.scheduler.beans.SMSBounces;
import org.mq.captiway.scheduler.beans.SMSCampaignReport;
import org.mq.captiway.scheduler.beans.SMSCampaignSent;
import org.mq.captiway.scheduler.beans.SMSSuppressedContacts;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.SMSBouncesDao;
import org.mq.captiway.scheduler.dao.SMSBouncesDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDao;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDao;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDaoForDML;
import org.mq.captiway.scheduler.dao.SMSSuppressedContactsDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.SMSStatusCodes;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.service.GatewaySessionProvider;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.smpp.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.zkoss.json.JSONObject;
import org.zkoss.json.parser.JSONParser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class MessagebirdSMSgateway {
	
	private OCSMSGateway ocsmsGateway;
	private SMSCampaignSentDao smsCampaignSentDao;
	private SMSCampaignSentDaoForDML smsCampaignSentDaoForDML ;
	private SMSBouncesDao smsBouncesDao;
	private SMSBouncesDaoForDML smsBouncesDaoForDML;
	private SMSCampaignDeliveryReportsHandler smsDlrHandler;
	private SMSCampaignReportDao smsCampaignReportDao;
	private SMSCampaignReportDaoForDML smsCampaignReportDaoForDML;
	private SMSSuppressedContactsDao smsSuppressedContactsDao;
	private ContactsDao contactsDao;
	private ContactsDaoForDML contactsDaoForDML;
	private SMSCConnector SMSCConnectorObj;
	private GatewaySessionProvider sessionProvider;
	private Session unicellSession;
	private final String queryString = "username=<USERNAME>&password=<PWD>&to=<TO>&from=<SENDERID>&text=<MESSAGE>&concat=<CONCAT>";
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private final String urlStr = "https://rest.messagebird.com/messages";
	private final String BatchurlStr = "https://rest.messagebird.com/messages/batches";
	
	int smsCount=0;
	List<MessageBirdTextList> messages = null;
	private LinkedHashSet<String> sentIdsSet=new LinkedHashSet<String>();
	private LinkedHashMap<String, String> sentIdsMobilesMap = new LinkedHashMap<String, String>();
	private String userID;
	private String pwd ;
	private String senderId;
	private String peId=""; //"1005782244529523125";
	
	
	public MessagebirdSMSgateway() {}
	
	public MessagebirdSMSgateway(String userID, String pwd) {
		
		this.userID = userID;
		this.pwd = pwd;
		
		
	}
	
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		
		this.pwd = pwd;
	}
	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		
		this.senderId = senderId;
	}
	
	public String getPeId() {
		return peId;
	}

	public void setPeId(String peId) {
		this.peId = peId;
	}

	private  Set<String> dlrReciepts = new HashSet<String>();
	public static Map<String, String> equencelDlrMap ;
	public static Set<String> deliveredStatusSet ;
	public static Set<String> suppressedStatusSet;
	private static Map<Long, String> updateStatusMap ;
	public static synchronized Map<Long, String> getUpdateStatusMap() {
		if(updateStatusMap == null) {
			updateStatusMap = new LinkedHashMap<Long, String>();
		}
		return updateStatusMap;
	}
	public MessagebirdSMSgateway(OCSMSGateway ocsmsGateway,String userID,String pwd,String senderId,String peId) {
		
		this.ocsmsGateway = ocsmsGateway;
		this.userID = userID;
		this.pwd = pwd;
		this.senderId=senderId;
		this.peId=peId;
		try {
			ServiceLocator serviceLocator = ServiceLocator.getInstance();
			smsCampaignSentDao = (SMSCampaignSentDao)serviceLocator.getDAOByName("smsCampaignSentDao");
			smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML )serviceLocator.getDAOForDMLByName("smsCampaignSentDaoForDML");
			smsBouncesDao = (SMSBouncesDao)serviceLocator.getDAOByName("smsBouncesDao");
			smsBouncesDaoForDML = (SMSBouncesDaoForDML)serviceLocator.getDAOForDMLByName("smsBouncesDaoForDML");
			smsCampaignReportDao = (SMSCampaignReportDao)serviceLocator.getDAOByName("smsCampaignReportDao");
			smsCampaignReportDaoForDML = (SMSCampaignReportDaoForDML)serviceLocator.getDAOForDMLByName("smsCampaignReportDaoForDML");
			smsSuppressedContactsDao = (SMSSuppressedContactsDao)serviceLocator.getDAOByName("smsSuppressedContactsDao");
			contactsDao = (ContactsDao)serviceLocator.getDAOByName("contactsDao");
			contactsDaoForDML  = (ContactsDaoForDML)serviceLocator.getDAOForDMLByName("contactsDaoForDML");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception while getting the dao ", e);
		}
	}
	
	public void pingToSendRestOfSMS(Long smsreportId,String templateRegisteredId){
	
	try{
		logger.info("pingToSendRestOfSMS in MesaageBird---");
		List<String> retList = null;
		if(smsCount > 0 && messages != null && messages.size() >0) {
			logger.info("textlist.size-- "+messages.size());
			Gson gson = new Gson();
			PrepareMessageBirdJsonRequest pj=new PrepareMessageBirdJsonRequest();
			
			//pj.setAccesskey(ocsmsGateway.getAPIId());
			pj.setMessages(messages);
			
			logger.info("pj after setting value "+pj.getMessages().toString());

			
			List<PrepareMessageBirdSingleJsonRequest> msgbirdist=	PrepareMessageBirdSingleJsonRequest.convert(pj);
			
			logger.info("msgbirdist size--->"+msgbirdist.size());

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(urlStr);
		
			Iterator<String> iter = sentIdsSet.iterator();

			for (PrepareMessageBirdSingleJsonRequest msg:msgbirdist) {
			
			String request = gson.toJson(msg);
			logger.info("requestJson Msgbird--->"+request);
			
			//DefaultHttpClient httpClient = new DefaultHttpClient();
			//HttpPost postRequest = new HttpPost(urlStr);
			StringEntity input = new StringEntity(request);
			logger.info("input-- "+input);
			
			postRequest.setHeader("Authorization",ocsmsGateway.getAPIId());
			input.setContentType("application/json");
			postRequest.setEntity(input);
			HttpResponse response = httpClient.execute(postRequest);
			
			BufferedReader br = new BufferedReader(
					new InputStreamReader((response.getEntity().getContent())));
			
			String output;
			String resp="";
			logger.info("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				if(output!=null)resp=resp+output;
				//logger.info("output---"+output);
			}
			logger.info("resp---"+resp);
		
				Gson gson1 = new Gson();

				MessagebirdResponse msgbirdresponse = new MessagebirdResponse();
		        msgbirdresponse = gson1.fromJson(resp,MessagebirdResponse.class);
		        
		        String actualSentId="";
		        //boolean sentIdHasNext=iter.hasNext();
		        CaptiwayToSMSApiGateway captiway=new CaptiwayToSMSApiGateway();
			
					// Need to discuus regarding updation of reports 
					 List<Item> itemsList = msgbirdresponse.getRecipients().getItems();

				      
					for(Item item: itemsList) {
							
							logger.info("entering inside itemobj for loop");
							logger.info("Receipient value is"+item.getRecipient());
							
							if(iter.hasNext()){
								actualSentId=iter.next();
								captiway.updateInitialStatusFromMsgBird(msgbirdresponse.getId(),smsreportId,actualSentId,item.getRecipient());
							}
						}
					
					
			}
			sentIdsSet.clear();
			sentIdsMobilesMap.clear();
			httpClient.getConnectionManager().shutdown();
			logger.info(" end of pingToSendRestOfSMS---");
		}
		
	} catch (MalformedURLException e) {
		logger.error("MalformedURLException",e);
	} catch (IOException e) {
		logger.error("IOException",e);
	}catch(Exception e){
		logger.error("Exception",e);
	}
}
	
	public List<String> parseInitialResponse(String response,LinkedHashSet<String> sentIds,Long smsReportId) {
		List<String> responseContentList = new ArrayList<String>();
		logger.info("response--"+response);
		try {
			Gson gson1 = new Gson();
			MessagebirdResponse msgbirdResponse =new MessagebirdResponse();
			//List<MessagebirdResponseList> responseList =null;
			msgbirdResponse = gson1.fromJson(response,MessagebirdResponse.class);
			//responseList=msgbirdResponse.getMsgbirdresponse();
			//logger.debug("=======responseList ======"+responseList);
			
			Iterator< String> iter = sentIdsSet.iterator();
			String actualSentId="";
			CaptiwayToSMSApiGateway captiway=new CaptiwayToSMSApiGateway();
			/*for (EquenceResponseList respLst : responseList ){
				logger.info(respLst.getMrId());
				actualSentId=iter.next();
				captiway.updateInitialStatusFromEquence(respLst.getMrId(),smsReportId,actualSentId,respLst.getDestination());
			}
			
			// Need to discuus regarding updation of reports 

			/*for (MessagebirdResponseList respLst : responseList ){
				try {
					
					/*if(respLst.getSegment() != null && !respLst.getSegment().isEmpty() && (Integer.parseInt(respLst.getSegment())!=0
							&& Integer.parseInt(respLst.getSegment())>1)
							&& respLst.getDestination().endsWith(sentIdsMobilesMap.get(actualSentId))) continue;*/
				/*} catch (Exception e) {
					logger.error("Exception",e);
				}
				if(iter.hasNext()) {
					
					actualSentId=iter.next();
					//captiway.updateInitialStatusFromEquence(respLst.getMrId(),smsReportId,actualSentId,respLst.getDestination());
				}
			}*/
			sentIdsSet.clear();
			sentIdsMobilesMap.clear();
			return responseContentList;
		} catch(Exception e){
			logger.error("Exception",e);
			return null;
		}
		
	}//parseInitialResponse
	
	
	public String parseInitialAutoSMSResponse(String response) {
		logger.info("auto sms response--"+response);
		try {
			Gson gson1 = new Gson();
			MessagebirdResponse msgbirdResponse =new MessagebirdResponse();
			//List<MessagebirdAutoSMSResponseList> responseList =null;
			msgbirdResponse = gson1.fromJson(response,MessagebirdResponse.class);
			//responseList=msgbirdResponse.getResponse();
			//logger.debug("=======responseList ======"+responseList);
			
			String mrId="";
			String msgId="";
			mrId=msgbirdResponse.getId();
			if(msgId.equalsIgnoreCase(Constants.STRING_NILL)) msgId=mrId;
			logger.info("mrId== "+msgbirdResponse.getId());
			return msgId;
		
		} catch(Exception e){
			logger.error("Exception",e);
			return null;
		}
		
	}//parseInitialAutoSMSResponse
	


	public String sendSingleSMSOverHTTP(String content, String mobile, Long rowId,String templateRegisteredId) {
		

		
		try{
			logger.info("sendSingleSMSOverHTTP---");
			/*List<EquenceTextList> textLst = new ArrayList<EquenceTextList>();
			EquenceTextList equenceTextList = new EquenceTextList();
			equenceTextList.setTo(mobile);
			equenceTextList.setText(content);
			textLst.add(equenceTextList);*/
				Gson gson = new Gson();
				PrepareMessageBirdSingleJsonRequest prepareSingleJson =new PrepareMessageBirdSingleJsonRequest();
			
				
				//prepareSingleJson.setAccesskey(ocsmsGateway.getAPIId());
				
				prepareSingleJson.setRecipients(mobile);
				prepareSingleJson.setOriginator(senderId);
				prepareSingleJson.setBody(content);
				prepareSingleJson.setReference("SenderId");
				prepareSingleJson.setReportUrl("https://enb3jx9ynld1.x.pipedream.net/"+mobile);
				
				//rowId comes null from lty prgm helper
				//prepareSingleJson.setMSGID(rowId!=null ? OCConstants.AutoSMSPrefix+rowId.toString() :"");
				
				String request = gson.toJson(prepareSingleJson);
				logger.info("requestJson--->"+request);
				
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost postRequest = new HttpPost(urlStr);
				StringEntity input = new StringEntity(request);
				
				postRequest.setHeader("Authorization",ocsmsGateway.getAPIId());
				input.setContentType("application/json");
				postRequest.setEntity(input);
				
				HttpResponse response = httpClient.execute(postRequest);
				
				/*if (response.getStatusLine().getStatusCode() != 201) {
					 throw new RuntimeException("Failed : HTTP error code : "
					 + response.getStatusLine().getStatusCode());
					 }*/
				
				BufferedReader br = new BufferedReader(
						new InputStreamReader((response.getEntity().getContent())));
				
				String resp="";
				String output="";
				logger.info("Output from Server .... \n");
				while ((output = br.readLine()) != null) {
					if(output!=null)resp=resp+output;
					logger.info("output---"+resp);
					}
				
				httpClient.getConnectionManager().shutdown();
				
				String mrId = parseInitialAutoSMSResponse(resp);
				return mrId;
			
		} catch (MalformedURLException e) {
			logger.error("MalformedURLException",e);
		} catch (IOException e) {
			logger.error("IOException",e);
		}catch(Exception e){
			logger.error("Exception",e);
		}
		return null;
	}
	
	
	
	
	public List<String> sendOverHTTP(String content, String mobile,String sentId,Long smsCampRepId,String templateRegisteredId) {
		
	try{
		logger.info("entered sendOverHTTP--");
		if(messages == null ){
			messages = new ArrayList<MessageBirdTextList>(); 
		}
		
		List<String> retList = null;
		MessageBirdTextList msgbirdTextList = new MessageBirdTextList();
		
		msgbirdTextList.setRecipients(mobile);
		msgbirdTextList.setOriginator(senderId);
		msgbirdTextList.setBody(content);
		msgbirdTextList.setReference("SenderId");
		msgbirdTextList.setReportUrl("https://qcapp.optculture.com/Scheduler/updateSMSMBDLR.mqrm?msgid="+sentId);
		
		//equenceTextList.setText(content);
		messages.add(msgbirdTextList);
		smsCount += 1;
		sentIdsSet.add(sentId);
		logger.info("sentIdsSet--"+sentIdsSet.size());
		logger.info("smsCount size adding "+smsCount);
		logger.info("messages size adding "+messages);

		sentIdsMobilesMap.put(sentId, mobile);
		//logger.info("sentIdsSet--"+sentIdsSet);
		
		if(smsCount >= 20){
			
			Gson gson = new Gson();
			
			PrepareMessageBirdJsonRequest pj=new PrepareMessageBirdJsonRequest();
			
			pj.setMessages(messages);
	
		//	List<PrepareMessageBirdSingleJsonRequest> msgbirdist=	PrepareMessageBirdSingleJsonRequest.convert(pj);
			logger.info("messages size--->"+messages.size());

			
		//	for (PrepareMessageBirdSingleJsonRequest msg:msgbirdist) {
			
			String request = gson.toJson(pj);
			
			logger.info("requestJson--->"+request);
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(BatchurlStr);
			
			StringEntity input = new StringEntity(request);
			input.setContentType("application/json");
			postRequest.setEntity(input);
			postRequest.setHeader("Authorization",ocsmsGateway.getAPIId());

			HttpResponse response = httpClient.execute(postRequest);
			
			/*if (response.getStatusLine().getStatusCode() != 201) {
				 throw new RuntimeException("Failed : HTTP error code : "
				 + response.getStatusLine().getStatusCode());
				 }*/
			
			BufferedReader br = new BufferedReader(
					new InputStreamReader((response.getEntity().getContent())));
			
			String output;
			String resp="";
			logger.info("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				if(output!=null)resp=resp+output;
				logger.info("output---"+resp);
				}
			
			httpClient.getConnectionManager().shutdown();
			
			messages = new ArrayList<MessageBirdTextList>();
			
			retList = parseInitialResponse(resp,sentIdsSet,smsCampRepId);
			smsCount = 0;
			sentIdsSet.clear();
			return retList;
			//}
			//httpClient.getConnectionManager().shutdown();

		}
		
	} catch (MalformedURLException e) {
		logger.error("MalformedURLException",e);
	} catch (IOException e) {
		logger.error("IOException",e);
	}catch(Exception e){
		logger.error("Exception",e);
	}
		return null;
	}	
	
	public  boolean checkConnectivity(OCSMSGateway ocsmsGateway){
		try {
			
		//	PrepareMessageBirdJsonRequest pj=new PrepareMessageBirdJsonRequest();
			Gson gson = new Gson();
//			if(messages == null ){
//				messages = new ArrayList<MessageBirdTextList>(); 
//			}
//			MessageBirdTextList msgbirdTextList = new MessageBirdTextList();
//			msgbirdTextList.setRecipients("9398591611");
//			msgbirdTextList.setOriginator(senderId);
//			messages.add(msgbirdTextList);
			
		//	pj.setMessages(messages);
			
		//	String request = gson.toJson(pj);
		//	logger.info("requestJson--->"+request);
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			
		//	GetMethod 
			
			HttpGet getRequest = new HttpGet("https://rest.messagebird.com/balance");
			
			getRequest.setHeader("Authorization",ocsmsGateway.getAPIId());
			
			//StringEntity input = new StringEntity(request);
			
			//input.setContentType("application/json");
			//((HttpResponse) getRequest).setEntity(input);
			
			HttpResponse response = httpClient.execute(getRequest);
			
			/*if (response.getStatusLine().getStatusCode() != 201) {
				 throw new RuntimeException("Failed : HTTP error code : "
				 + response.getStatusLine().getStatusCode());
				 }*/
			
			BufferedReader br = new BufferedReader(
					new InputStreamReader((response.getEntity().getContent())));
			
			String output;
			String resp="";
			logger.info("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				if(output!=null)resp=resp+output;
				logger.info("output---"+resp);
				}
			
			httpClient.getConnectionManager().shutdown();
			
			
			if(resp==null ||resp.isEmpty()){
				logger.info("got response null");
				return false;
			}
			JSONParser parser=new JSONParser();
			JSONObject json = (JSONObject) parser.parse(resp);
			logger.info("json "+json);
			logger.info("json.payment() "+json.get("payment"));
			String msg=json.get("payment").toString();
			
			if(msg.contains("User details not found")||msg.contains("Authentication failed")){
				return false;
			}
			else if(msg.equalsIgnoreCase("To parameter not specified or is Null")||(msg.equalsIgnoreCase("Internal Server Error") || msg.equalsIgnoreCase("postpaid") )){
				return true;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error("MalformedURLException",e);
		return false;
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			logger.error("ProtocolException",e);
			return false;
		}catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("IOException",e);
			return false;
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception",e);
			return false;
		}
		return true;
	}
	


	
}
