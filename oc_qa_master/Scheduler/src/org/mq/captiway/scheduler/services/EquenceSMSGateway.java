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

import org.apache.http.HttpResponse;
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
import com.google.gson.Gson;

public class EquenceSMSGateway {
	
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
	private final String urlStr = "https://api.equence.in/pushsms";
	
	int smsCount=0;
	List<EquenceTextList> textlist = null;
	private LinkedHashSet<String> sentIdsSet=new LinkedHashSet<String>();
	private LinkedHashMap<String, String> sentIdsMobilesMap = new LinkedHashMap<String, String>();
	private String userID;
	private String pwd ;
	private String senderId;
	private String peId=""; //"1005782244529523125";
	
	
	public EquenceSMSGateway() {}
	
	public EquenceSMSGateway(String userID, String pwd) {
		
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
	public EquenceSMSGateway(OCSMSGateway ocsmsGateway,String userID,String pwd,String senderId,String peId) {
		
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
			logger.info("pingToSendRestOfSMS---");
			List<String> retList = null;
			if(smsCount > 0 && textlist != null && textlist.size() >0) {
				logger.info("textlist.size-- "+textlist.size());
				Gson gson = new Gson();
				PrepareEquenceJsonRequest pj=new PrepareEquenceJsonRequest();
				pj.setUsername(userID);
				pj.setPassword(pwd);
				pj.setFrom(senderId);
				pj.setPeId(peId);
			//	pj.setTmplId(templateRegisteredId!=null?templateRegisteredId:"");
				pj.setTextlist(textlist);
				
				List<PrepareEquenceSingleJsonRequest> Equlist=	PrepareEquenceSingleJsonRequest.convert(pj);
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost postRequest = new HttpPost(urlStr);
				Iterator<String> iter = sentIdsSet.iterator();
				for (PrepareEquenceSingleJsonRequest msg:Equlist) {
				
				logger.info("entering for loop");

					
				String request = gson.toJson(msg);
				logger.info("requestJson for pingToSendRestOFSMS--->"+request);
				
			//	DefaultHttpClient httpClient = new DefaultHttpClient();
			//	HttpPost postRequest = new HttpPost(urlStr);
				StringEntity input = new StringEntity(request);
				logger.info("input in for loop-- "+input);
				input.setContentType("application/json");
				postRequest.setEntity(input);
				
				
				
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
					//logger.info("output---"+output);
				}
				
				
				logger.info("resp---"+resp);
				/*try {
					logger.info("resp .... \n"+resp);
					resp.substring(resp.indexOf('['),resp.indexOf(']'));
					resp= resp.substring((resp.indexOf('[')+1),resp.indexOf(']'));
					logger.info("resp .... \n"+resp);
					//parseInitialResponse(resp,sentId);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ", e);
				}*/
				
				//retList = parseInitialResponse(resp,sentIdsSet);
				Gson gson1 = new Gson();
				Response eqResponse =new Response();
				List<EquenceResponseList> responseList =null;
				eqResponse = gson1.fromJson(resp,Response.class);
				responseList=eqResponse.getResponse();
				//logger.debug("=======responseList ======"+responseList);
				
				
				String actualSentId="";
				//boolean sentIdHasNext=iter.hasNext();
				CaptiwayToSMSApiGateway captiway=new CaptiwayToSMSApiGateway();
				/*for (EquenceResponseList respLst : responseList ){
					logger.info(respLst.getMrId());
					logger.info(respLst.getDestination());
					if(sventIdHasNext){
						logger.info("sentIdHasNext "+sentIdHasNext);
						actualSentId=iter.next();
					}
					captiway.updateInitialStatusFromEquence(respLst.getMrId(),smsreportId,actualSentId,respLst.getDestination());
				}*/
				for (EquenceResponseList respLst : responseList ){
					logger.info(respLst.getMrId());
					logger.info(respLst.getDestination());
					try {
						if(respLst.getSegment() != null && !respLst.getSegment().isEmpty() && (Integer.parseInt(respLst.getSegment())!=0
								&& Integer.parseInt(respLst.getSegment())>1)
								&& respLst.getDestination().endsWith(sentIdsMobilesMap.get(actualSentId))) continue;
					} catch (Exception e) {
						logger.error("Exception",e);
					}
					if(iter.hasNext()){
						actualSentId=iter.next();
						captiway.updateInitialStatusFromEquence(respLst.getMrId()!=null ? respLst.getMrId() : respLst.getMrid(),smsreportId,actualSentId,respLst.getDestination());
					}
				}
				
				/*logger.info("sentIdsSet--"+sentIdsSet.size());
				for (String sentId : sentIdsSet) {
					captiway.updateInitialStatusFromEquence(retList,smsreportId,sentId);				
				}*/
			//	httpClient.getConnectionManager().shutdown();
				logger.info(" end of pingToSendRestOfSMS---");
		
			}// Equlist end for loop
			
			sentIdsSet.clear();
			sentIdsMobilesMap.clear();

				httpClient.getConnectionManager().shutdown();

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
			Response eqResponse =new Response();
			List<EquenceResponseList> responseList =null;
			eqResponse = gson1.fromJson(response,Response.class);
			responseList=eqResponse.getResponse();
			logger.debug("=======responseList ======"+responseList+ " from sendoverHTTP");
			
			Iterator< String> iter = sentIdsSet.iterator();
			String actualSentId="";
			CaptiwayToSMSApiGateway captiway=new CaptiwayToSMSApiGateway();
			/*for (EquenceResponseList respLst : responseList ){
				logger.info(respLst.getMrId());
				actualSentId=iter.next();
				captiway.updateInitialStatusFromEquence(respLst.getMrId(),smsReportId,actualSentId,respLst.getDestination());
			}*/
			for (EquenceResponseList respLst : responseList ){
				try {
					if(respLst.getSegment() != null && !respLst.getSegment().isEmpty() && (Integer.parseInt(respLst.getSegment())!=0
							&& Integer.parseInt(respLst.getSegment())>1)
							&& respLst.getDestination().endsWith(sentIdsMobilesMap.get(actualSentId))) continue;
				} catch (Exception e) {
					logger.error("Exception",e);
				}
				if(iter.hasNext()) {
					
					actualSentId=iter.next();
					captiway.updateInitialStatusFromEquence(respLst.getMrId()!= null ? respLst.getMrId() : respLst.getMrid() ,smsReportId,actualSentId,respLst.getDestination());
				}
			}
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
			EquenceSingleSMSResponse eqResponse =new EquenceSingleSMSResponse();
			List<EquenceAutoSMSResponseList> responseList =null;
			eqResponse = gson1.fromJson(response,EquenceSingleSMSResponse.class);
			responseList=eqResponse.getResponse();
			logger.debug("=======responseList ======"+responseList);
			
			String mrId="";
			String msgId="";
			for (EquenceAutoSMSResponseList respLst : responseList ){
				mrId=respLst.getMrId();
				if(msgId.equalsIgnoreCase(Constants.STRING_NILL)) msgId=mrId;
				logger.info("mrId== "+respLst.getMrId());
			}
			
			return msgId;
		} catch(Exception e){
			logger.error("Exception",e);
			return null;
		}
		
	}//parseInitialAutoSMSResponse
	
/*public class Response extends BaseRequestObject {

	private List<EquenceResponseList> response; 
		public List<EquenceResponseList> getResponse() {
		return response;
	}

	public void setResponse(List<EquenceResponseList> response) {
		this.response = response;
	}

		
		
}*/
/*public class EquenceResponseList{
		private String mrId; 
		private String id;
		private String seg;
		private String destination;
		private String dispatch;
		private String segment;
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getSeg() {
			return seg;
		}

		public void setSeg(String seg) {
			this.seg = seg;
		}

		public String getDestination() {
			return destination;
		}

		public void setDestination(String destination) {
			this.destination = destination;
		}

		public String getDispatch() {
			return dispatch;
		}

		public void setDispatch(String dispatch) {
			this.dispatch = dispatch;
		}

		public String getSegment() {
			return segment;
		}

		public void setSegment(String segment) {
			this.segment = segment;
		}

		public String getMrId() {
			return mrId;
		}

		public void setMrId(String mrId) {
			this.mrId = mrId;
		}
		
				}
	
*/	public String sendSingleSMSOverHTTP(String content, String mobile, Long rowId,String templateRegisteredId) {
		

		
		try{
			logger.info("sendSingleSMSOverHTTP---");
			/*List<EquenceTextList> textLst = new ArrayList<EquenceTextList>();
			EquenceTextList equenceTextList = new EquenceTextList();
			equenceTextList.setTo(mobile);
			equenceTextList.setText(content);
			textLst.add(equenceTextList);*/
				Gson gson = new Gson();
				PrepareEquenceSingleJsonRequest prepareSingleJson =new PrepareEquenceSingleJsonRequest();
				prepareSingleJson.setUsername(userID);
				prepareSingleJson.setPassword(pwd);
				prepareSingleJson.setFrom(senderId);
				prepareSingleJson.setPeId(peId);
				//pj.setTextlist(textLst);
				prepareSingleJson.setTo(mobile);
				prepareSingleJson.setText(content);
				prepareSingleJson.setTmplId(templateRegisteredId!=null?templateRegisteredId:"");
				//rowId comes null from lty prgm helper
				prepareSingleJson.setMSGID(rowId!=null ? OCConstants.AutoSMSPrefix+rowId.toString() :"");
				
				String request = gson.toJson(prepareSingleJson);
				logger.info("requestJson for sendSingleSMSOverHTTP--->"+request);
				
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost postRequest = new HttpPost(urlStr);
				StringEntity input = new StringEntity(request);
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
		if(textlist == null ){
			textlist = new ArrayList<EquenceTextList>(); 
		}
		
		List<String> retList = null;
		EquenceTextList equenceTextList = new EquenceTextList();
		equenceTextList.setTo(mobile);
		equenceTextList.setText(content);
		equenceTextList.setTmplId(templateRegisteredId);
		
		textlist.add(equenceTextList);
		smsCount += 1;
		sentIdsSet.add(sentId);
		logger.info("sentIdsSet--"+sentIdsSet.size());
		sentIdsMobilesMap.put(sentId, mobile);
		//logger.info("sentIdsSet--"+sentIdsSet);
		if(smsCount >= 100){
			Gson gson = new Gson();
			PrepareEquenceJsonRequest pj=new PrepareEquenceJsonRequest();
			pj.setUsername(userID);
			pj.setPassword(pwd);
			pj.setFrom(senderId);
			pj.setPeId(peId);
		//	pj.setTmplId(templateRegisteredId!=null?templateRegisteredId:"");
			pj.setTextlist(textlist);
			
			List<PrepareEquenceSingleJsonRequest> Equlist=	PrepareEquenceSingleJsonRequest.convert(pj);
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(urlStr);
			
			for (PrepareEquenceSingleJsonRequest msg:Equlist) {
			
			
			String request = gson.toJson(pj);
			logger.info("requestJson for sendOverHTTP--->"+request);
			
		//	DefaultHttpClient httpClient = new DefaultHttpClient();
		//	HttpPost postRequest = new HttpPost(urlStr);
			StringEntity input = new StringEntity(request);
			input.setContentType("application/json");
			postRequest.setEntity(input);
			
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
				//logger.info("output---"+resp);
				}
			
			//httpClient.getConnectionManager().shutdown();
			
			textlist = new ArrayList<EquenceTextList>();
			
			retList = parseInitialResponse(resp,sentIdsSet,smsCampRepId);
			smsCount = 0;
			sentIdsSet.clear();//this is not cleared due to the exception hence ==100 is failed
			return retList;
		}
		
			httpClient.getConnectionManager().shutdown();
			
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
	
	public boolean checkConnectivity(OCSMSGateway ocsmsGateway){
		try {
			PrepareEquenceJsonRequest pj=new PrepareEquenceJsonRequest();
			Gson gson = new Gson();
			pj.setUsername(ocsmsGateway.getUserId());
			pj.setPassword(ocsmsGateway.getPwd());
			pj.setFrom(ocsmsGateway.getSenderId());
			pj.setPeId(ocsmsGateway.getPrincipalEntityId());
			pj.setTmplId("");
			
			String request = gson.toJson(pj);
			logger.info("requestJson for checkConnectivity--->"+request);
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost("https://api.equence.in/pushsms");
			StringEntity input = new StringEntity(request);
			input.setContentType("application/json");
			postRequest.setEntity(input);
			
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
			
			
			if(resp==null ||resp.isEmpty()){
				logger.info("got response null");
				return false;
			}
			JSONParser parser=new JSONParser();
			JSONObject json = (JSONObject) parser.parse(resp);
			logger.info("json "+json);
			logger.info("json.message() "+json.get("message"));
			String msg=json.get("message").toString();
			if(msg.contains("User details not found")||msg.contains("Authentication failed")){
				return false;
			}
			else if(msg.equalsIgnoreCase("To parameter not specified or is Null")||(msg.equalsIgnoreCase("Internal Server Error"))){
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
	
	/*public synchronized void processDlrReciepts() throws Exception{
		//id:1114022511523684840 sub:001 dlvrd:001 submit date:1402251152 done date:1402251152 stat:DELIVRD err:001 Text:
		logger.debug("-------in process----"+dlrReciepts.size());
		
		if(dlrReciepts == null|| dlrReciepts.size() == 0) {
			logger.warn("Some error got, as this is called to process but no entries found.");
			return;
		}
		//process the msg_idsmap first
		Map<Long, String> updateStatusMap = getUpdateStatusMap();
		if(updateStatusMap != null && updateStatusMap.size() > 0 ){
			SMSCConnectorObj.processUpdateStatusMap(null, updateStatusMap, true);
			Map<Long, String> tempUpdateStatusMap = new HashMap<Long, String>();
			tempUpdateStatusMap.putAll(updateStatusMap);
			tempUpdateStatusMap.clear();
		}
		
		String[] dlrmsgTokenArr = null;
		String sentIdsStr = Constants.STRING_NILL;
		
		Map<String, String> updateSentDlrMap = new HashMap<String, String>();
		
		for (String recptMessage : dlrReciepts) {
			
			String msgID =  null;
			String seqNum = null;
			String statusCode = null;
			dlrmsgTokenArr = recptMessage.split(Constants.STRING_WHITESPACE);
			for (String token : dlrmsgTokenArr) {
				//logger.debug("token ::"+token);
					String[] tokenArr = token.split(Constants.DELIMETER_COLON);
				if(tokenArr[0].equals(Constants.SMS_DLR_STATUSCODE_TOKEN)) {
					
					statusCode = tokenArr[1];
				}else if(tokenArr[0].equals(Constants.SMS_DLR_MSGID_TOKEN)) {
					
					msgID = tokenArr[1];
					if(sentIdsStr.length() > 0) sentIdsStr += Constants.DELIMETER_COMMA;
					sentIdsStr += "'"+msgID+"'";
				}
				
				
			}//for
			
			updateSentDlrMap.put(msgID, statusCode);
			
		}//for
		
		if(sentIdsStr == null || sentIdsStr.isEmpty()) {
			logger.debug("No sent items found for msgIds== "+sentIdsStr);
			
			//TODO empty the dlrrecptset may be in synchronized block
			return;
		}
		List<SMSCampaignSent> sentList = smsCampaignSentDao.findByMsgIds(sentIdsStr);
		if(sentList == null ){
			//TODO
			logger.debug("No sent items found for msgIds== "+sentIdsStr);
			
			return;
		}
		List<SMSCampaignSent> listToBeUpdated = new ArrayList<SMSCampaignSent>();
		List<SMSBounces> smsBounceList = new ArrayList<SMSBounces>();
		List<String> contactMobilesList = new ArrayList<String>();
		List<SMSSuppressedContacts> suppressedContactsList = new ArrayList<SMSSuppressedContacts>(); 
		
		Long smsCrID = null;
		Users user  =  null;
		SMSCampaignReport smscr = null;
		
		for (SMSCampaignSent smsCampaignSent : sentList) {
			
			try {
				
				if(smsCampaignSent.getApiMsgId()  == null ) continue;
					
				String status = equencelDlrMap.get(updateSentDlrMap.get(smsCampaignSent.getApiMsgId()));
				
				
				logger.debug("status for "+smsCampaignSent.getApiMsgId() + " status  "+status);
				smscr = smsCampaignSent.getSmsCampaignReport();
				
				if(smsCrID == null || !smscr.getSmsCrId().equals(smsCrID)){ 
					
					if(listToBeUpdated.size() > 0 && !smscr.getSmsCrId().equals(smsCrID)){
						updateDlrChunk(listToBeUpdated, smsBounceList, suppressedContactsList, contactMobilesList, user, smsCrID);
					}
					smsCrID = smscr.getSmsCrId();
					user  = smscr.getUser();
				
				}
				
				smsCampaignSent.setStatus(status);
				boolean isNonDelivered = true;
				for (String  dstate : deliveredStatusSet) {
					
					if(status.equalsIgnoreCase(dstate)) {
						isNonDelivered = false;
					}
				}
				if(!deliveredStatusSet.contains(status)) {
					
					smsCampaignSent.setStatus(SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);
					SMSBounces newBounce= new SMSBounces();
					newBounce.setCrId(smsCampaignSent.getSmsCampaignReport().getSmsCrId());
					newBounce.setSentId(smsCampaignSent);
					newBounce.setMessage(status);
					newBounce.setMobile(smsCampaignSent.getMobileNumber());
					newBounce.setCategory(status);
					newBounce.setBouncedDate(Calendar.getInstance());
					smsBounceList.add(newBounce);
					
					if(suppressedStatusSet.contains(status)) {
						
						SMSSuppressedContacts suppressedContact = new SMSSuppressedContacts();
						suppressedContact.setUser(user);
						suppressedContact.setMobile(smsCampaignSent.getMobileNumber());
						suppressedContact.setType(status);
						suppressedContact.setReason(status);
						
						suppressedContact.setSuppressedtime(Calendar.getInstance());
						
						suppressedContactsList.add(suppressedContact);
						if(status.equals(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER)) {
							
							contactMobilesList.add(smsCampaignSent.getMobileNumber());
							
						}//if
						
					}//if
					
				}//if
					
				listToBeUpdated.add(smsCampaignSent);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new BaseServiceException("Exception in process dlrreceipts ");
				
			}
			
		}//for
		
		try {
			updateDlrChunk(listToBeUpdated, smsBounceList, suppressedContactsList, contactMobilesList, user, smsCrID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("Exception in process dlrreceipts ");
		}
		synchronized (dlrReciepts) {
			dlrReciepts.clear();
			
		}
	}
	
	private void updateDlrChunk(List<SMSCampaignSent> listToBeUpdated, List<SMSBounces> smsBounceList,
			List<SMSSuppressedContacts> suppressedContactsList, List<String> contactMobilesList,
			Users user, Long smsCrId) throws Exception{
		logger.debug("=============entered into update chunk===========");
		try {
			ServiceLocator serviceLocator = ServiceLocator.getInstance();
			if(smsCampaignSentDao == null)smsCampaignSentDao = (SMSCampaignSentDao)serviceLocator.getDAOByName("smsCampaignSentDao");
			if(smsBouncesDao == null)smsBouncesDao = (SMSBouncesDao)serviceLocator.getDAOByName("smsBouncesDao");
			if(smsCampaignReportDao == null)smsCampaignReportDao = (SMSCampaignReportDao)serviceLocator.getDAOByName("smsCampaignReportDao");
			if(smsSuppressedContactsDao == null)smsSuppressedContactsDao = (SMSSuppressedContactsDao)serviceLocator.getDAOByName("smsSuppressedContactsDao");
			if(contactsDao == null)contactsDao = (ContactsDao)serviceLocator.getDAOByName("contactsDao");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception while getting the dao ", e);
			throw new Exception("dao not found");
		}
		
		if(listToBeUpdated.size() > 0) {
			logger.debug("=============entered into sentUpdate ==========="+listToBeUpdated.size());
			//smsCampaignSentDao.saveByCollection(listToBeUpdated);
			smsCampaignSentDaoForDML.saveByCollection(listToBeUpdated);

			listToBeUpdated.clear();
			
			if(smsBounceList.size() > 0) {
				logger.debug("=============entered into bounceUpdate ==========="+smsBounceList.size());
				//smsBouncesDao.saveByCollection(smsBounceList);
				smsBouncesDaoForDML.saveByCollection(smsBounceList);
				smsBounceList.clear();
			}
			
			
			if(suppressedContactsList.size() > 0) {
				SMSCampaignDeliveryReportsHandler handler = new SMSCampaignDeliveryReportsHandler();
				logger.debug("=============entered into suppressedUpdate ==========="+suppressedContactsList.size());
				handler.addToSuppressedContacts(user, suppressedContactsList);//;(user, suppressedContactsList);
				
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
					logger.debug("=============entered into contactUpdate ==========="+mobileStr);
					contactsDaoForDML.updatemobileStatusForMultipleContacts(mobileStr, 
							SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER, user.getUserId());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception while updating contacts ", e);
					
				}
				contactMobilesList.clear();
			}
			//TODO need to update bounce count in sms reports
			//int updateCount = smsCampaignReportDao.updateBounceReport(smsCrId);
			int updateCount = smsCampaignReportDaoForDML.updateBounceReport(smsCrId);
			logger.debug("bounced count  ::"+updateCount);
			//smsCampaignSentDao.saveByCollection(listToBeUpdated);
			
		}//if
		
		
	}
*/	
		

	/*public class PrepareEquenceJsonRequest {
			{
					"username":"****",
					"password":"****",
					"from":"SMSTST",
					"textlist":[                                                                                 
					                   {"to":"9206774674","text":"hi test message1"},
					                   {"to":"8895801942","text":"hi test message2"},
					                   {"to":"7377069728","text":"hi test message3"}]
		      }
			private String username; 
			private String password;
			private String from;
			private List<TextList> textlist; 
			
			public PrepareEquenceJsonRequest(){
				//Default Constructor
			}
			
			public String getUsername() {
				return username;
			}
			
			public void setUsername(String username) {
				this.username = username;
			}
			
			public String getPassword() {
				return password;
			}
			
			public void setPassword(String password) {
				this.password = password;
			}
			
			public String getFrom() {
				return from;
			}
			
			public void setFrom(String from) {
				this.from = from;
			}
			
			public List<TextList> getTextlist() {
				return textlist;
			}
		
			public void setTextlist(List<TextList> textlist) {
				this.textlist = textlist;
			}
		}*/
		
		/*public class EquenceTextList {

			private String to;
			private String text;
			
			public String getTo() {
				return to;
			}
			
			public void setTo(String to) {
				this.to = to;
			}
			
			public String getText() {
				return text;
			}
			
			public void setText(String text) {
				this.text = text;
			}
		}*/
	
/*public String sendOverHTTP(String UserID, String pwd, String content, String mobile, String senderId) {
	
	try {
		
		 * http://api.equence.in/pushsms?username=optcult_pr&password=-MP59_lg&to=919*********&from=OPTCLT&text=HelloHarshi 
		 
		
		String postData = "";
		String data = URLEncoder.encode(content, "UTF-8"); 
		
		
		postData += this.queryString.replace("<USERNAME>", UserID ).replace("<PWD>", URLEncoder.encode(pwd, "UTF-8"))
			     .replace("<SENDERID>", senderId).replace("<TO>", mobile)
			     .replace("<MESSAGE>", data).replace("<CONCAT>", content.trim().length() <= 160 ? "0" : "1");

		
		logger.debug("postData======>"+postData);
		
		URL url = new URL(this.urlStr);
		
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
		String response = "";
		while ((decodedString = in.readLine()) != null) {
			response += decodedString;
		}
		in.close();
		logger.debug("response is======>"+response);
		
		String msgID = response;
		return msgID;
		
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		logger.error("Exception :::", e);
		return null;
	} catch (ProtocolException e) {
		// TODO Auto-generated catch block
		logger.error("Exception :::", e);
		return null;
	} catch (IOException e) {
		// TODO Auto-generated catch block
		logger.error("Exception :::", e);
		return null;
	}catch (Exception e) {
		// TODO: handle exception
		logger.error("Exception :::", e);
		return null;
	}
	}*/

	/*public class PrepareEquenceJsonRequest {

		//delete laterrrr
		{
		"username":"****",
		"password":"****",
		"from":"SMSTST",
		"textlist":[                                                                                 
		                   {"to":"9206774674","text":"hi test message1"},
		                   {"to":"8895801942","text":"hi test message2"},
		                   {"to":"7377069728","text":"hi test message3"}]
	}
		private String username; 
		private String password;
		private String from;
		private List<EquenceTextList> textlist; 
		
		public PrepareEquenceJsonRequest(){
		//Default Constructor
		}
		
		public String getUsername() {
		return username;
		}
		
		public void setUsername(String username) {
		this.username = username;
		}
		
		public String getPassword() {
		return password;
		}
		
		public void setPassword(String password) {
		this.password = password;
		}
		
		public String getFrom() {
		return from;
		}
		
		public void setFrom(String from) {
		this.from = from;
		}
		
		public List<EquenceTextList> getTextlist() {
		return textlist;
		}
		
		public void setTextlist(List<EquenceTextList> textlist) {
		this.textlist = textlist;
		}
		}
*/
				
		/*public static void main(String[] args) throws Exception{
			LinkedHashSet<String> sentIdsSet=new LinkedHashSet<String>();
		       sentIdsSet.add("abc");
		       sentIdsSet.add("def");
		       Iterator< String> iter = sentIdsSet.iterator();
		       String actualSentId="";
		       for(int i=0;i<=2;i++){
		    	   if(iter.hasNext()){
		    		   actualSentId=iter.next();
		            System.out.println("" + i);
		            System.out.println("" + actualSentId);
		    	   }
		       }
		}*/
				/*PrepareEquenceJsonRequest responseJson = null;
				List<String> retList = null;
				List<String> sentIdsSet=new ArrayList<String>();
				sentIdsSet.add("1234");
					Gson gson = new Gson();
					EquenceSMSGateway eq=new EquenceSMSGateway();
					PrepareEquenceJsonRequest pj=eq.new PrepareEquenceJsonRequest();
					pj.setUsername("optcult_pr");
					pj.setPassword("-MP59_lg");
					pj.setFrom("OPTCLT");
					
					String[] mobilenumb={"8121835559","682"};
					System.out.println("mobilenumb length "+mobilenumb.length);
					String content="hai";
					List<EquenceTextList> toText = new ArrayList<EquenceTextList>();
					for(int i=0;i<mobilenumb.length;i++){
						EquenceTextList tl = new EquenceTextList();
						System.out.println("mobile num "+mobilenumb[i]);
						tl.setTo(mobilenumb[i]);
						tl.setText(content);
						toText.add(tl);
					}
					pj.setTextlist(toText);
					
					
					String request = gson.toJson(pj);
					responseJson = gson.fromJson(request,PrepareEquenceJsonRequest.class);
					System.out.println("requestJson--->"+request);
					System.out.println("responseJson--->"+responseJson);
					 try{
						 DefaultHttpClient httpClient = new DefaultHttpClient();
						 HttpPost postRequest = new HttpPost("https://api.equence.in/pushsms");
						 StringEntity input = new StringEntity(gson.toJson(responseJson));
						 input.setContentType("application/json");
						 postRequest.setEntity(input);

						 HttpResponse response = httpClient.execute(postRequest);

						 if (response.getStatusLine().getStatusCode() != 201) {
						 throw new RuntimeException("Failed : HTTP error code : "
						 + response.getStatusLine().getStatusCode());
						 }

						 BufferedReader br = new BufferedReader(
						                         new InputStreamReader((response.getEntity().getContent())));

						 String output;
						 System.out.println("Output from Server .... \n");
						 String decodedString="";
							while ((output = br.readLine()) != null) {
								decodedString += output;
							}
							//br.close();
							 System.out.println("decodedString--"+decodedString);
						 httpClient.getConnectionManager().shutdown();
						 
						 System.out.println("Entering parse response");
						 retList = eq.parseInitialResponse(decodedString,sentIdsSet);
						 
						   } catch (MalformedURLException e) {

						 e.printStackTrace();
						   } catch (IOException e) {

						 e.printStackTrace();

						   }
		}
*/
}
