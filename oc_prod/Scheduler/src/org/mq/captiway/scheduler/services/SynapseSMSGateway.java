package org.mq.captiway.scheduler.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.coyote.ProtocolException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.services.SynapseSMSList.MessageParams;
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
import org.mq.optculture.service.GatewaySessionProvider;
import org.mq.optculture.utils.ServiceLocator;
import org.smpp.Session;
import org.zkoss.json.JSONObject;
import org.zkoss.json.parser.JSONParser;

import com.google.gson.Gson;

public class SynapseSMSGateway {
	
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
		//private final String queryString = "username=<USERNAME>&password=<PWD>&to=<TO>&from=<SENDERID>&text=<MESSAGE>&concat=<CONCAT>";
		private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
		private final String urlStr = "https://api.me.synapselive.com/v1/multichannel/messages/sendsms";
		
		int smsCount=0;
		//List<SynapseSMSList> textlist = null;
		List<MessageParams> textlist = null;
		String messageContent = "";
		private LinkedHashSet<String> sentIdsSet=new LinkedHashSet<String>();
		
		private String userID;
		private String pwd ;
		private String senderId;
		
		
		public SynapseSMSGateway() {}
		
		public SynapseSMSGateway(String userID, String pwd) {
			
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
		
		private  Set<String> dlrReciepts = new HashSet<String>();
		public static Map<String, String> synapseDlrMap ;
		public static Set<String> deliveredStatusSet ;
		public static Set<String> suppressedStatusSet;
		private static Map<Long, String> updateStatusMap ;
		public static synchronized Map<Long, String> getUpdateStatusMap() {
			if(updateStatusMap == null) {
				updateStatusMap = new LinkedHashMap<Long, String>();
			}
			return updateStatusMap;
		}
		public SynapseSMSGateway(OCSMSGateway ocsmsGateway,String userID,String pwd,String senderId) {
			
			this.ocsmsGateway = ocsmsGateway;
			this.userID = userID;
			this.pwd = pwd;
			this.senderId=senderId;
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
		
		public void pingToSendRestOfSMS(Long smsreportId){
		
		try{
			logger.info("synapse pingToSendRestOfSMS---");
			List<String> retList = null;
			if(smsCount > 0 && textlist != null && textlist.size() >0) {
				logger.info("textlist.size-- "+textlist.size());
				PrepareSynapseJsonRequest json = null;
				Gson gson = new Gson();
				PrepareSynapseJsonRequest pj=new PrepareSynapseJsonRequest();
				pj.setMsgType("1");
				pj.setSenderId(senderId);
				pj.setPassword(pwd);
				pj.setMessage(messageContent);
				pj.setUserName(userID);
				//pj.setPriority("0");
				//pj.setReferenceId("1");
				//pj.setDlrUrl(null);
				
				List<MessageParams> smsList = new ArrayList<MessageParams>();
				List<SynapseSMSList> mobileList = new ArrayList<SynapseSMSList>();
				SynapseSMSList syn=new SynapseSMSList();
				syn.setMobileNumbers(textlist);
				pj.setMobileNumbers(syn);
				pj.setPassword(pwd);
				
				
				String request = gson.toJson(pj);
				json = gson.fromJson(request,PrepareSynapseJsonRequest.class);
				logger.info("requestJson--->"+request);
					 DefaultHttpClient httpClient = new DefaultHttpClient();
					 HttpPost postRequest = new HttpPost(urlStr);
					 StringEntity input = new StringEntity(gson.toJson(json));
					 input.setContentType("application/json");
					 postRequest.setEntity(input);

					 HttpResponse response = httpClient.execute(postRequest);

					 BufferedReader br = new BufferedReader(
					                         new InputStreamReader((response.getEntity().getContent())));

					 String output;
					 logger.info("Output from Server .... \n");
					 String decodedString="";
						while ((output = br.readLine()) != null) {
							decodedString += output;
						}
					 logger.info("decodedString--"+decodedString);
					 
					 Gson gson1 = new Gson();
					 Result eqResponse =new Result();
						eqResponse = gson1.fromJson(decodedString,Result.class);
						//responseList=eqResponse.getResult();
						logger.info("=======response ======"+eqResponse);//responseList);
					 httpClient.getConnectionManager().shutdown();
				List<Result> responseList= new ArrayList<Result>();
				responseList.add(eqResponse);
				Iterator<String> iter = sentIdsSet.iterator();
				String actualSentId=Constants.STRING_NILL;
					logger.info(eqResponse.getStatus());
					logger.info(eqResponse.getResult());
					while(iter.hasNext()){
						if(actualSentId.length()>0) actualSentId+=Constants.DELIMETER_COMMA;
						actualSentId+=iter.next();
					}
				if(actualSentId!=null && !actualSentId.isEmpty()) updateInitialStatusFromSynapse(eqResponse.getResult(),smsreportId,actualSentId);
				
				sentIdsSet.clear();
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
		
		public void parseInitialResponse(String response,LinkedHashSet<String> sentIds,Long smsReportId) {
			logger.info("response--"+response);
			try {
				Gson gson1 = new Gson();
				Result synResponse =new Result();
				List<Result> responseList =new ArrayList<Result>();
				synResponse = gson1.fromJson(response,Result.class);
				String uniqueId = synResponse.getResult();
				String status=synResponse.getStatus();
				responseList.add(synResponse);
				logger.debug("=======responseList ======"+responseList);
				if(status.equalsIgnoreCase("success")){
				Iterator< String> iter = sentIds.iterator();
				String actualSentId=Constants.STRING_NILL;
				while(iter.hasNext()){
					if(actualSentId.length()>0) actualSentId+=Constants.DELIMETER_COMMA;
					actualSentId+=iter.next();
				}
				if(actualSentId!=null && !actualSentId.isEmpty()) updateInitialStatusFromSynapse(uniqueId,smsReportId,actualSentId);
			}
			} catch(Exception e){
				logger.error("Exception",e);
			}
			
		}//parseInitialResponse
		
	public String sendSingleSMSOverHTTP(String content, String mobile) {
			

			
		String uniqueId =Constants.STRING_NILL;
			try{
				logger.info("sendSingleSMSOverHTTP-synapse--");
				List<MessageParams> textLst = new ArrayList<MessageParams>();
				SynapseSMSList synapseSMSList = new SynapseSMSList();
				MessageParams messageParams = synapseSMSList.new MessageParams();
				messageParams.setMobileNumber(mobile);
				synapseSMSList.setMobileNumbers(textLst);
				textLst.add(messageParams);
					Gson gson = new Gson();
					PrepareSynapseJsonRequest pj=new PrepareSynapseJsonRequest();
					pj.setUserName(userID);
					pj.setPassword(pwd);
					pj.setMsgType("1");
					pj.setSenderId(senderId);
					pj.setMessage(content);
					pj.setMobileNumbers(synapseSMSList);
					
					String request = gson.toJson(pj);
					logger.info("requestJson--->"+request);
					
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
					
					/*String output;
					logger.info("Output from Server .... \n");
					while ((output = br.readLine()) != null) {
						logger.info(output);
					}*/
					String output;
					String resp="";
					logger.info("Output from Server .... \n");
					while ((output = br.readLine()) != null) {
						if(output!=null)resp=resp+output;
						logger.info("output---"+resp);
					}
					
					httpClient.getConnectionManager().shutdown();
					
					Gson gson1 = new Gson();
					Result synResponse =new Result();
					synResponse = gson1.fromJson(resp,Result.class);
					uniqueId = synResponse.getResult();
				
			} catch (MalformedURLException e) {
				logger.error("MalformedURLException",e);
			} catch (IOException e) {
				logger.error("IOException",e);
			}catch(Exception e){
				logger.error("Exception",e);
			}

			return uniqueId;
		}
		
		
		
		
		public void sendOverHTTP(String content, String mobile,String sentId,Long smsCampRepId,boolean isPersonalized) {
			
		try{
			logger.info("entered synapse sendOverHTTP--");
			if(textlist == null ){
				textlist = new ArrayList<MessageParams>(); 
			}
			if(!isPersonalized) {
			
			SynapseSMSList synapseSMSList = new SynapseSMSList();
			MessageParams messageParams = synapseSMSList.new MessageParams();
			logger.info("mobile "+mobile);
			messageParams.setMobileNumber(mobile);
			textlist.add(messageParams);
			logger.info("textlist "+textlist.size());
			messageContent=content;
			synapseSMSList.setMobileNumbers(textlist);
			smsCount ++;
			sentIdsSet.add(sentId);
			logger.info("sentIdsSet--"+sentIdsSet.size());
			if(smsCount == 100){
				Gson gson = new Gson();
				PrepareSynapseJsonRequest pj=new PrepareSynapseJsonRequest();
				pj.setUserName(userID);
				pj.setPassword(pwd);
				pj.setMsgType("1");
				pj.setSenderId(senderId);
				pj.setMessage(content);
				pj.setMobileNumbers(synapseSMSList);
				
				String request = gson.toJson(pj);
				logger.info("requestJson--->"+request);
				
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost postRequest = new HttpPost(urlStr);
				StringEntity input = new StringEntity(request);
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
					logger.info("output---"+resp);
					}
				
				httpClient.getConnectionManager().shutdown();
				
				textlist = new ArrayList<MessageParams>();
				
				parseInitialResponse(resp,sentIdsSet,smsCampRepId);
				smsCount = 0;
				sentIdsSet.clear();
			
			}
			}else {
				SynapseSMSList synapseSMSList = new SynapseSMSList();
				MessageParams messageParams = synapseSMSList.new MessageParams();
				logger.info("mobile "+mobile);
				sentIdsSet.add(sentId);
				logger.info("sentIdsSet--"+sentIdsSet.size());
				messageParams.setMobileNumber(mobile);
				textlist.add(messageParams);
				logger.info("textlist "+textlist.size());
				synapseSMSList.setMobileNumbers(textlist);
				Gson gson = new Gson();
				PrepareSynapseJsonRequest pj=new PrepareSynapseJsonRequest();
				pj.setUserName(userID);
				pj.setPassword(pwd);
				pj.setMsgType("1");
				pj.setSenderId(senderId);
				pj.setMessage(content);
				pj.setMobileNumbers(synapseSMSList);
				
				String request = gson.toJson(pj);
				logger.info("requestJson--->"+request);
				
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost postRequest = new HttpPost(urlStr);
				StringEntity input = new StringEntity(request);
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
					logger.info("output---"+resp);
					}
				
				httpClient.getConnectionManager().shutdown();
				
				textlist = new ArrayList<MessageParams>();
				parseInitialResponse(resp,sentIdsSet,smsCampRepId);
				sentIdsSet.clear();
				
			}
			
		} catch (MalformedURLException e) {
			logger.error("MalformedURLException",e);
		} catch (IOException e) {
			logger.error("IOException",e);
		}catch(Exception e){
			logger.error("Exception",e);
		}
		}	
		
		public boolean checkConnectivity(OCSMSGateway ocsmsGateway){
			try {

								Gson gson = new Gson();
								PrepareSynapseJsonRequest pj=new PrepareSynapseJsonRequest();
								pj.setUserName(ocsmsGateway.getUserId());
								pj.setPassword(ocsmsGateway.getPwd());
								pj.setSenderId(ocsmsGateway.getSenderId());
								
								String request = gson.toJson(pj);
								logger.info("requestJson--->"+request);
								
								DefaultHttpClient httpClient = new DefaultHttpClient();
								HttpPost postRequest = new HttpPost(urlStr);
								StringEntity input = new StringEntity(request);
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
									logger.info("output---"+resp);
									}
								
								httpClient.getConnectionManager().shutdown();
								
								if(resp==null ||resp.isEmpty()){
									logger.info("got response null");
									return false;
								}
								
								JSONParser parser =new JSONParser();
								JSONObject json = (JSONObject) parser.parse(resp);
								logger.info("json "+json);
								logger.info("json.result() "+json.get("result"));
								//String msg=json.get("result").toString();
								if( json.get("result")!=null  && json.get("result").toString().equalsIgnoreCase("Invalid Credentials.")){
									logger.info("returning false");
									return false;
								}
								
								return true;
								
								}catch (MalformedURLException e) {
									logger.error("MalformedURLException",e);
									return false;
							   } catch (IOException e) {
								   logger.error("IOException",e);
								   return false;
							   } catch (Exception e) {
								   logger.error("Exception",e);
								   return false;
							   }
	}
		
		public void updateInitialStatusFromSynapse(String refId, Long smsCampRepId,String sentId) {
			// TODO Auto-generated method stub
				try {
					ServiceLocator serviceLocator = ServiceLocator.getInstance();
					SMSCampaignSentDaoForDML smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML)serviceLocator.getDAOForDMLByName("smsCampaignSentDaoForDML");
						if(logger.isDebugEnabled()) logger.debug("refId in updateSMSCampSentStatus is===>"+refId+" smsCampRepId "+smsCampRepId.longValue());
						smsCampaignSentDaoForDML.updateSynapseRefId(sentId.trim(),smsCampRepId.longValue(),refId);
				}catch (Exception e) {
					// TODO: handle exception
					logger.error("Exception ::::" , e);
				}
			
		}
		
	/*public static void main(String[] args) throws Exception{
		
		
		try {*/
			
			/*//String urlStr = "http://api.me.synapselive.com/v1/multichannel/messages/sendsms";
			//String urlString = "http://api.synapselive.vectramind.in/push.aspx?user=demo&pass=demo&senderid=3554&mobile=9550808880&lang=0&message=Test msg&dlr=1";
			String urlString = "http://api.synapselive.vectramind.in/push.aspx?user=<USERNAME>&pass=<PWD>&senderid=<SENDERID>&mobile=<TO>&lang=0&message=<MESSAGE>&dlr=1";
			//http://api.synapselive.vectramind.in/push.aspx?user=Inditechme&pass=Indi@951&type=1X&message=hi&lang=0&mobile=9550808880&senderid=3554 
			 
			//String queryString = new String("{\"user\":\"<USERNAME>\",\"pass\":\"<PWD>\",\"mobile\":\"<TO>\",\"senderid\":\"<SENDERID>\",\"message\":\"<MESSAGE>\",\"concat\":\"<CONCAT>\"}");
			//String postData = "";
			//String data = URLEncoder.encode("Hello", "UTF-8"); 
			
			
			urlString = urlString.replace("<USERNAME>", "user" ).replace("<PWD>", URLEncoder.encode("pwd", "UTF-8"))
				     .replace("<SENDERID>", "3554").replace("<TO>", "")
				     .replace("<MESSAGE>", "");

			
			System.out.println("postData======>"+urlString);
			
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();

			int code = connection.getResponseCode();
			System.out.println("code="+code);
			System.out.println(connection.getResponseMessage());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}*/
			/*
			List<String> retList = null;
			SynapseSMSList synapseSMSList = new SynapseSMSList();
			MessageParams messageParams = synapseSMSList.new MessageParams();
			//String urlStr = "https://api.me.synapselive.com/v1/multichannel/messages";
			String urlStr = "https://api.me.synapselive.com/v1/multichannel/messages/sendsms";
				Gson gson = new Gson();
				PrepareSynapseJsonRequest pj=new PrepareSynapseJsonRequest();
				pj.setUserName("Inditechmee");
				pj.setPassword("Indi@951one");
				//pj.setMsgType("1");
				pj.setSenderId("3554");
				
				String request = gson.toJson(pj);
				System.out.println("requestJson--->"+request);
				
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost postRequest = new HttpPost(urlStr);
				StringEntity input = new StringEntity(request);
				input.setContentType("application/json");
				postRequest.setEntity(input);
				
				HttpResponse response = httpClient.execute(postRequest);
				BufferedReader br = new BufferedReader(
						new InputStreamReader((response.getEntity().getContent())));
				
				String output;
				String resp="";
				System.out.println("Output from Server .... \n");
				while ((output = br.readLine()) != null) {
					if(output!=null)resp=resp+output;
					//System.out.println("output---"+resp);
					}
				System.out.println("output---"+resp);
				httpClient.getConnectionManager().shutdown();
				if(resp.contains("Not Found")){
					System.out.println("return true");
				}else{
				JSONParser parser =new JSONParser();
				JSONObject json = (JSONObject) parser.parse(resp);
				System.out.println("json "+json);
				System.out.println("json.result() "+json.get("result"));
				//if username/username,pwd wrng -- invalid
				//if only pwd wrong -- json.getResult null
				//if credentials correct and url worng -- json.getResult null
				//String msg=json.get("result").toString();
				String resultString=(String)json.get("result");
				System.out.println("resultString "+resultString);
				if(resultString.equalsIgnoreCase("Invalid Credentials.")){
					System.out.println("return false");
				}
				if( json.get("result")!=null  && json.get("result").toString().equalsIgnoreCase("Invalid Credentials.")){
					System.out.println("return false");
				}
				}*/
		/*PrepareSynapseJsonRequest json = null;
			Gson gson = new Gson();
			//SynapseSMSGateway sy=new SynapseSMSGateway();
			PrepareSynapseJsonRequest pj=new PrepareSynapseJsonRequest();
			String msgType="1";
			String senderId="3554";
			String message="Hai@Harshi";
			pj.setUserName("Inditechme");
			pj.setMsgType(msgType);
			pj.setSenderId(senderId);
			pj.setMessage(message);
			//pj.setPriority("0");
			//pj.setReferenceId("1");
			//pj.setDlrUrl(null);
			
			//String[] mobileNumber={"971558803090","971526412966"};
			String[] mobileNumber={"8121835559","9848022338"};
			//logger.info("mobilenumb length "+mobilenumb.length);
			
			List<MessageParams> smsList = new ArrayList<MessageParams>();
			List<SynapseSMSList> mobileList = new ArrayList<SynapseSMSList>();
			SynapseSMSList syn=new SynapseSMSList();
			//MessageParams msgParams=syn.new MessageParams();
			for(int i=0;i<mobileNumber.length;i++){
				MessageParams sl = syn.new MessageParams();
				logger.info("mobile num "+mobileNumber[i]);
				sl.setMobileNumber(mobileNumber[i]);
				smsList.add(sl);
				syn.setMobileNumbers(smsList);
				mobileList.add(syn);
			}
			pj.setMobileNumbers(syn);
			pj.setPassword("Indi@951");
			
			
			String request = gson.toJson(pj);
			json = gson.fromJson(request,PrepareSynapseJsonRequest.class);
			logger.info("requestJson--->"+request);
			//logger.info("responseJson--->"+responseJson);
			 try{
				 DefaultHttpClient httpClient = new DefaultHttpClient();
				 HttpPost postRequest = new HttpPost("https://api.me.synapselive.com/v1/multichannel/messages/sendsms");
				 StringEntity input = new StringEntity(gson.toJson(json));
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
				 logger.info("Output from Server .... \n");
				 String decodedString="";
					while ((output = br.readLine()) != null) {
						decodedString += output;
					}
					//br.close();
				 logger.info("decodedString--"+decodedString);
				 
				 Gson gson1 = new Gson();
				 Result synResponse =new Result();
					//List<SynapseResponseList> responseList =null;
					synResponse = gson1.fromJson(decodedString,Result.class);
					//responseList=eqResponse.getResult();
					logger.info("=======response ======"+synResponse);//responseList);
				 httpClient.getConnectionManager().shutdown();
				 
				 //logger.info("Entering parse response");
				 //retList = pj.parseInitialResponse(decodedString,sentIdsSet);
				 
				    catch (MalformedURLException e) {

				 e.printStackTrace();
				   } catch (IOException e) {

				 e.printStackTrace();

				   }*/
				 /*  System.out.println("return true");
		}catch (MalformedURLException e) {

			 e.printStackTrace();
			   } catch (IOException e) {

			 e.printStackTrace();
			   }
	}*/

}