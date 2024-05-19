package org.mq.captiway.scheduler.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.OCSMSGateway;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.SMSBouncesDao;
import org.mq.captiway.scheduler.dao.SMSBouncesDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDao;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDao;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDaoForDML;
import org.mq.captiway.scheduler.dao.SMSSuppressedContactsDao;
import org.mq.captiway.scheduler.services.SynapseSMSList.MessageParams;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.SMSStatusCodes;
import org.mq.optculture.service.GatewaySessionProvider;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.json.JSONObject;
import org.zkoss.json.parser.JSONParser;

import com.google.gson.Gson;

public class InfocommSMSGateway {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
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
	private String userID ;
	private String pwd ;
	private int smsCount;
	//List<String> textlist = null;
	String content = "";
	String tempMobileData="";
	private LinkedHashSet<String> sentIdsSet=new LinkedHashSet<String>();
	/*https ://www.ismartsms.net/iBulkSMS/HttpWS/SMSDynamicAPI.aspx?UserId=fbb_webser&Password=Fbb@7890&MobileNo=9550808880,9848022338&Message=TestMessage&
		PushDateTime=10/24/2019 10:39:00&Lang=0& FLashSMS=N*/
	/*https://www.ismartsms.net/iBulkSMS/HttpWS/SMSDynamicRefIntlAPI.aspx?UserId=ismartsms&Password=ismartsms&MobileNo=97199XXXXXX&Message=TestMessage&
	 PushDateTime=03/04/2018 07:30:00&Lang=0&Header=AnyHeader&referenceIds=101,102*/
	
	/*private final static String queryString = "https://www.ismartsms.net/iBulkSMS/HttpWS/SMSDynamicAPI.aspx?"
			+ "UserId=<USER>&Password=<PWD>&MobileNo=<TO>&Message=<MESSAGE>&Lang=0&FLashSMS=N";*/
	private final static String queryString = "https://www.ismartsms.net/iBulkSMS/HttpWS/SMSDynamicRefIntlAPI.aspx?"
			+ "UserId=<USER>&Password=<PWD>&MobileNo=<TO>&Message=<MESSAGE>&Lang=0&FLashSMS=N&referenceIds=<REFERENCEIDS>";
	
	private final static String reportsUrlString = "https://www.ismartsms.net/iBulkSMS/HttpWS/SMSDeliveryReportAPI.aspx?"
			+ "UserId=<USER>&Password=<PWD>&referenceIds=<REFERENCEIDS>";
	
	public InfocommSMSGateway() {}
	
	public InfocommSMSGateway(OCSMSGateway ocsmsGateway) {
		
		this.ocsmsGateway = ocsmsGateway;
		this.smsCount = 0;
	}
	
	public InfocommSMSGateway(String userID, String pwd) {
		
		this.userID = userID;
		this.pwd = pwd;
		
		
	}
	public InfocommSMSGateway(OCSMSGateway ocsmsGateway,String userID,String pwd,String senderId) {
		
		this.ocsmsGateway = ocsmsGateway;
		this.userID = userID;
		this.pwd = pwd;
		//this.senderId=senderId;
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

	
	public List<String> prepareData(String messageContent, String msgType, String sentId,
			 String toMobNum, String msgSeq, Long smsCampRepId,  String senderId, String userId, String pwd,boolean isPersonalized) {
		
		List<String> retList = null;
		try {
			if(!isPersonalized) {
				smsCount += 1;
				sentIdsSet.add(sentId);
				if(logger.isDebugEnabled()) logger.debug("smsCount "+smsCount);
				content=messageContent;
				/*if(textlist == null ){
					textlist = new ArrayList<String>(); 
				}*/
				if(tempMobileData.length() > 0) tempMobileData += Constants.DELIMETER_COMMA;
				tempMobileData += toMobNum; 
				logger.info("tempMobileData "+tempMobileData);
				if((smsCount == 50 )) {
					//**** need to prepare the msgStructure*****
					//textlist.add(toMobNum);
					String referenceIdString=Constants.STRING_NILL;
					for(String eachSentId:sentIdsSet) {
						if(referenceIdString.length() > 0) {
							referenceIdString +=","+Integer.parseInt(eachSentId);
						}else {
							referenceIdString += Integer.parseInt(eachSentId);
						}
					}
					String response = sendSMS(tempMobileData,userId,pwd,content,referenceIdString);
					smsCount = 0;
					parseInitialResponse(sentIdsSet,smsCampRepId);
					sentIdsSet.clear();
					//textlist = new ArrayList<String>();
					tempMobileData="";
				}//if
			}else {
				sentIdsSet.add(sentId);
				String response = sendSMS(toMobNum,userId,pwd,messageContent,String.valueOf(Integer.parseInt(sentId)));
				parseInitialResponse(sentIdsSet,smsCampRepId);
				sentIdsSet.clear();
			}
		
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception :::", e);
		}
		return retList;
			
		}
		public static String sendSMS(String tempMobileData,String userId, String pwd,String content,String referenceIdString) {
			
			String msgID="";
			
			try{
				String urlString = queryString.replace("<USER>", userId ).replace("<PWD>", URLEncoder.encode(pwd, "UTF-8"))
				        .replace("<TO>", tempMobileData).replace("<MESSAGE>", URLEncoder.encode(content, "UTF-8"))
				        .replace("<REFERENCEIDS>", referenceIdString);
				        
				String postData = "";
				String response = "";
				
				logger.info("Data to be sent is=====>"+urlString);
				
				URL url = new URL(urlString);
				
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
				logger.debug("response is======>"+response);
			
			msgID = response;
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::", e);
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::", e);
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception :::", e);
		}
		return msgID;
	}


	public String sendSingleSMSOverHTTP(String content, String mobile) {
		

		
		String uniqueId =Constants.STRING_NILL;

	try {
		 
		String urlString = queryString.replace("<USER>", userID ).replace("<PWD>", URLEncoder.encode(pwd, "UTF-8"))
			        .replace("<TO>", mobile).replace("<MESSAGE>", URLEncoder.encode(content, "UTF-8"))
			        //.replace("<PUSHTIME>", "");
			        ;
		
			String postData = "";
			String response = "";
			
			logger.info("Data to be sent is=====>"+urlString);
			
			URL url = new URL(urlString);
			
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
		logger.debug("response is======>"+response);
		
		uniqueId = response;
		
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
	return uniqueId;
	}

public void pingToSendRestOfSMS(Long smsreportId){
		
		try{
			logger.info("infocomm pingToSendRestOfSMS---");
			List<String> retList = null;
			if(smsCount > 0) {
					String referenceIdString=Constants.STRING_NILL;
					for(String eachSentId:sentIdsSet) {
						if(referenceIdString.length() > 0) {
							referenceIdString +=","+Integer.parseInt(eachSentId);
						}else {
							referenceIdString += Integer.parseInt(eachSentId);
						}
					}
					String urlString = queryString.replace("<USER>", userID ).replace("<PWD>", URLEncoder.encode(pwd, "UTF-8"))
					        .replace("<TO>", tempMobileData).replace("<MESSAGE>", URLEncoder.encode(content, "UTF-8"))
					        .replace("<REFERENCEIDS>", referenceIdString);
						String postData = "";
						String response = "";
						
						logger.info("Data to be sent is=====>"+urlString);
						
						URL url = new URL(urlString);
						
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
					logger.debug("response is======>"+response);
					parseInitialResponse(sentIdsSet,smsreportId);
					sentIdsSet.clear();
			} 
		}catch(Exception e){
			logger.error("Exception",e);
		}
				
	}

	public void parseInitialResponse(LinkedHashSet<String> sentIds,Long smsReportId) {
	try {
		
		Iterator< String> iter = sentIds.iterator();
		String actualSentId=Constants.STRING_NILL;
		while(iter.hasNext()){
			if(actualSentId.length()>0) actualSentId+=Constants.DELIMETER_COMMA;
			actualSentId+=iter.next();
		}
		if(actualSentId!=null && !actualSentId.isEmpty()) updateAPImsgId(smsReportId,actualSentId);
	} catch(Exception e){
		logger.error("Exception",e);
	}
	
}//parseInitialResponse
	public void updateAPImsgId(Long smsCampRepId,String sentId) {
		// TODO Auto-generated method stub
			try {
				ServiceLocator serviceLocator = ServiceLocator.getInstance();
				SMSCampaignSentDaoForDML smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML)serviceLocator.getDAOForDMLByName("smsCampaignSentDaoForDML");
					smsCampaignSentDaoForDML.updateInfocommAPIId(sentId.trim(),smsCampRepId.longValue());
			}catch (Exception e) {
				// TODO: handle exception
				logger.error("Exception ::::" , e);
			}
		
	}

	public Map<String, String> getReportsFromInfocomm(String msgIds) {
		 //if(targetUrl == null || targetUrl.isEmpty()) return null;
			Map<String, String> responseStatusCodeMap = new LinkedHashMap<String, String>();
		 try {
			 
			 /*https://www.ismartsms.net/iBulkSMS/HttpWS/SMSDeliveryReportAPI.aspx?UserId=xxxxxx&Password=xxxxxx&
			  * referenceIds=100001,100002,100003
			  */
				/* ReferenceId=100001,Status=delivered,DeliveryDateTime=8/5/2018 10:03:00AM|ReferenceId=5002,Status=delivered,
				 * DeliveryDateTime=8/5/2018 10:03:00 AM
				 */
/*
				 3  User Id or Password is Incorrect
				 11 Unknown Error
				 31 Delivery Receipt not enabled
				 32 Reference ID can’t be null or blank
				 33 No Record Found
*/

				String postData = "";
				postData += reportsUrlString.replace("<USER>", ocsmsGateway.getUserId() )
						.replace("<PWD>", URLEncoder.encode(ocsmsGateway.getPwd(), "UTF-8"))
				        .replace("<REFERENCEIDS>", msgIds);
				
				//String[] msgIdsArr = msgIdStr.split(Constants.DELIMETER_COMMA);
				
					try{
						logger.debug("before sending post data is===="+postData);
						
						URL url = new URL(postData);
						
						HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
						
						//urlconnection.setConnectTimeout(20000);
						//urlconnection.setReadTimeout(20000);
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
						logger.debug("report response is======>"+response);
						
						if(response != null) {
							
							if(response.startsWith("ReferenceId")) {
								
							String status = Constants.STRING_NILL;;
							String refId = Constants.STRING_NILL;
							
							String[] responseArr = response.trim().toString().split("\\|");
							logger.debug("resp arr size======>"+responseArr.length);
							for(String eachResp : responseArr) {
								
								String[] eachRespArr = eachResp.split(",");
								for (String resp : eachRespArr) {
									
								if(resp.startsWith("ReferenceId") ){
									
									refId = resp.split("=")[1];
								}else if(resp.startsWith("Status")){
										
									status = resp.split("=")[1];
										
									logger.debug("there in map======>"+(refId));
									if(!status.isEmpty() && !refId.isEmpty()){ // && responseStatusCodeMap.containsKey(refId)) {
										//logger.debug("there in map======>"+responseStatusCodeMap.containsKey(refId));
										responseStatusCodeMap.put(refId, status);
										 status = Constants.STRING_NILL;;
										 refId = Constants.STRING_NILL;
										
									}
									
									}
								}
							}
							}else {
								// how to update the other status when no ref id is being given
								responseStatusCodeMap.put(response, SMSStatusCodes.infocommStatusCodesMap.get(response));
							}
							
							
						}//if
						
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						logger.error("Exception",e);
					} catch (ProtocolException e) {
						// TODO Auto-generated catch block
						logger.error("Exception",e);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						logger.error("Exception",e);
					}catch (Exception e) {
						// TODO: handle exception
						logger.error("Exception",e);
					}
					return responseStatusCodeMap;
		 }catch (Exception e) {
				// TODO: handle exception
				logger.error("Exception",e);
			}
				
				
				//59071138631160420375
				//postData += "user=amith.lulla@optculture.com:amithl&tid=59093138622453710607 ";
				//postData += "user=mallika.naidu@optculture.com:mallika&tid=59071138631160420375 ";
				
				
			return responseStatusCodeMap;
	 }
/*	public boolean checkConnectivity(OCSMSGateway ocsmsGateway){
	try {

		String urlString = "https://www.ismartsms.net/iBulkSMS/HttpWS/SMSDynamicAPI.aspx?UserId=<USERNAME>&Password=<PWD>&"
				+ "MobileNo=<TO>&Message=<MESSAGE>&Lang=0&FLashSMS=N";
		 
		urlString = urlString.replace("<USERNAME>", "fbb_adminapi" ).replace("<PWD>", URLEncoder.encode("Fbbadmin@123!", "UTF-8"))
			        .replace("<TO>", "90639247").replace("<MESSAGE>", URLEncoder.encode("Testing sms from OC", "UTF-8"))
			        //.replace("<PUSHTIME>", "");
			        ;
		
			String postData = "";
			String response = "";
			
			System.out.println("Data to be sent is=====>"+urlString);
			
			URL url = new URL(urlString);
			
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
			logger.info("response==="+response);
			in.close();
						
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
}*/

/*	public static void main(String[] args) throws Exception{
	
	
	try {
		
		String urlString = "https://www.ismartsms.net/iBulkSMS/HttpWS/SMSDynamicAPI.aspx?UserId=<USERNAME>&Password=<PWD>&"
				+ "MobileNo=<TO>&Message=<MESSAGE>&Lang=0&FLashSMS=N";
		//String urlString = "https://www.ismartsms.net/iBulkSMS/HttpWS/SMSDynamicRefIntlAPI.aspx?"
			//	+ "UserId=<USER>&Password=<PWD>&MobileNo=<TO>&Message=<MESSAGE>&Lang=0&FLashSMS=N&referenceIds=<REFERENCEIDS>";
		
		//reports
		//https://www.ismartsms.net/iBulkSMS/HttpWS/SMSDeliveryReportAPI.aspx?UserId=xxxxxx&Password=xxxxxx&referenceIds=100001
		String urlString = "https://www.ismartsms.net/iBulkSMS/HttpWS/SMSDeliveryReportAPI.aspx?UserId=<USER>&Password=<PWD>&referenceIds=<REFERENCEIDS>";
		
		String postData = "";
		postData = urlString.replace("<USER>", "fbb_webser" ).replace("<PWD>", URLEncoder.encode("Fbb@7890", "UTF-8"))
			        .replace("<TO>", "95175382").replace("<MESSAGE>", URLEncoder.encode("Test sms from OC", "UTF-8"))
			        .replace("<REFERENCEIDS>", "29217");
		
			String response = "";
			
			System.out.println("Data to be sent is=====>"+postData);
			
			URL url = new URL(urlString);
			
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
			System.out.println("response==="+response);
			in.close();
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	}
	}*/
	

}
