package org.mq.marketer.campaign.controller.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.DRSMSSent;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.SMSCampaignSentDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.EquenceAutoSMSResponseList;
import org.mq.optculture.model.EquenceDLRResponseObject;
import org.mq.optculture.model.EquenceSingleSMSResponse;
import org.mq.optculture.model.MessagebirdResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollRequest;
import org.mq.optculture.model.ocloyalty.RequestHeader;
import org.mq.optculture.model.ocloyalty.SkuDetails;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.google.gson.Gson;


public class MessagebirdSMSGateway {

		private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
		private String userID ;
		private String pwd ;
		private String senderId;
		private String peId;
		private OCSMSGateway ocsmsGateway;
		private final String urlStr = "https://rest.messagebird.com/messages";
		List<MessageBirdTextList> messages = null;

		
		public MessagebirdSMSGateway() {}
		
		public MessagebirdSMSGateway(OCSMSGateway ocsmsGateway,String userID,String pwd,String senderId,String peId) {
			
			this.ocsmsGateway = ocsmsGateway;
			this.userID = userID;
			this.pwd = pwd;
			this.senderId=senderId;
			this.peId=peId;
			try {
				ServiceLocator serviceLocator = ServiceLocator.getInstance();
				/*smsCampaignSentDao = (SMSCampaignSentDao)serviceLocator.getDAOByName("smsCampaignSentDao");
				smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML )serviceLocator.getDAOForDMLByName("smsCampaignSentDaoForDML");
				smsCampaignReportDao = (SMSCampaignReportDao)serviceLocator.getDAOByName("smsCampaignReportDao");
				smsCampaignReportDaoForDML = (SMSCampaignReportDaoForDML)serviceLocator.getDAOForDMLByName("smsCampaignReportDaoForDML");
				contactsDao = (ContactsDao)serviceLocator.getDAOByName("contactsDao");
				contactsDaoForDML  = (ContactsDaoForDML)serviceLocator.getDAOForDMLByName("contactsDaoForDML");*/
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception while getting the dao ", e);
			}
		}
		
		public MessagebirdSMSGateway(String userID, String pwd, String senderId,String peId) {
			
			this.userID = userID;
			this.pwd = pwd;
			this.senderId=senderId;
			this.peId = peId;
			
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
		
		public void sendTestSMS(String content, String mobile) {
			

			
			try{
				if(messages == null ){
					messages = new ArrayList<MessageBirdTextList>(); 
				}
				MessageBirdTextList msgbirdTextList = new MessageBirdTextList();
				
				msgbirdTextList.setRecipients(mobile);
				msgbirdTextList.setOriginator(senderId);
				msgbirdTextList.setBody(content);
				msgbirdTextList.setReference("SenderId");
				msgbirdTextList.setReportUrl("https://enb3jx9ynld1.x.pipedream.net/");
				messages.add(msgbirdTextList);

					Gson gson = new Gson();
					PrepareMessageBirdJsonRequest pj=new PrepareMessageBirdJsonRequest(); ///same changes as  scheduler
					pj.setMessages(messages);
					
					
					List<PrepareMessageBirdSingleJsonRequest> msgbirdist=	PrepareMessageBirdSingleJsonRequest.convert(pj);
				
					logger.info("new requestlist is "+msgbirdist);

					
					DefaultHttpClient httpClient = new DefaultHttpClient();
					HttpPost postRequest = new HttpPost(urlStr);
					
					for (PrepareMessageBirdSingleJsonRequest msg:msgbirdist) {
						
					logger.info("entering for loop");

					String request = gson.toJson(msg);
					
					logger.info("Msgbird requestJson for SendTestsms  --->"+request);
					
				//	DefaultHttpClient httpClient = new DefaultHttpClient();
				//	HttpPost postRequest = new HttpPost("https://api.equence.in/pushsms");
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
					
					String output;
					//String resp="";
					
					while ((output = br.readLine()) != null) {
						//if(output!=null)resp=resp+output;
						logger.info("Output from Server .... \n"+output);
					}
					/*logger.info("resp .... \n"+resp);
					resp.substring(resp.indexOf('['),resp.indexOf(']'));
					resp= resp.substring((resp.indexOf('[')+1),resp.indexOf(']'));
					logger.info("resp .... \n"+resp);
					parseInitialResponse(resp);*/
					}
					httpClient.getConnectionManager().shutdown();
				
			} catch (MalformedURLException e) {
				
				logger.error("Exception",e);
			} catch (IOException e) {
				
				logger.error("Exception",e);
				
			}
		}	
		
		
		
		
		public class MessageBirdTextList {

			
			
			private String recipients;
			private String originator;
			private String body;
			private String reference;
			private String reportUrl;
			
			
			public String getRecipients() {
				return recipients;
			}
			public void setRecipients(String recipients) {
				this.recipients = recipients;
			}
			
			public String getOriginator() {
				return originator;
			}
			public void setOriginator(String originator) {
				this.originator = originator;
			}
			public String getBody() {
				return body;
			}
			public void setBody(String body) {
				this.body = body;
			}
			public String getReference() {
				return reference;
			}
			public void setReference(String reference) {
				this.reference = reference;
			}
			public String getReportUrl() {
				return reportUrl;
			}
			public void setReportUrl(String reportUrl) {
				this.reportUrl = reportUrl;
			}
			
	}

		
		public String sendDRSMS(String content, String mobile,String templateRegisteredId,DRSMSSent drSmsSent) {
			

			
			try{
				
				
				content = content.replace("|^", "[").replace("^|", "]");
				
				logger.info("Before content: "+content);
				
				content = replaceBarcodePhWithDummyCode(content);
				if(messages == null ){
					messages = new ArrayList<MessageBirdTextList>(); 
				}
				MessageBirdTextList msgbirdTextList = new MessageBirdTextList();
				
				msgbirdTextList.setRecipients(mobile);
				msgbirdTextList.setOriginator(senderId);
				msgbirdTextList.setBody(content);
				msgbirdTextList.setReference("SenderId");
				msgbirdTextList.setReportUrl("https://enb3jx9ynld1.x.pipedream.net/");
				messages.add(msgbirdTextList);

					Gson gson = new Gson();
					PrepareMessageBirdJsonRequest pj=new PrepareMessageBirdJsonRequest(); ///same changes as  scheduler
					pj.setMessages(messages);
					
					//pj.setMSGID(drSmsSent!=null ? OCConstants.DRSMSPrefix+drSmsSent.getId() :"");
					List<PrepareMessageBirdSingleJsonRequest> msgbirdist=	PrepareMessageBirdSingleJsonRequest.convert(pj);
					
					logger.info("new requestlist is "+msgbirdist);

					
					DefaultHttpClient httpClient = new DefaultHttpClient();
					HttpPost postRequest = new HttpPost(urlStr);
					String resp="";
					for (PrepareMessageBirdSingleJsonRequest msg:msgbirdist) {
						
					
					String request = gson.toJson(msg);
					logger.info("Msgbird requestJson for SendDRsms--->"+request);
					
					//DefaultHttpClient httpClient = new DefaultHttpClient();
					//HttpPost postRequest = new HttpPost(urlStr);
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
					
				
					String output="";
					logger.info("Output from Server .... \n");
					while ((output = br.readLine()) != null) {
						if(output!=null)resp=resp+output;
						logger.info("output---"+resp);
						}
					/*logger.info("resp .... \n"+resp);
					resp.substring(resp.indexOf('['),resp.indexOf(']'));
					resp= resp.substring((resp.indexOf('[')+1),resp.indexOf(']'));
					logger.info("resp .... \n"+resp);
					parseInitialResponse(resp);*/
					}
					httpClient.getConnectionManager().shutdown();
					
					String mrId = parseInitialAutoSMSResponse(resp);
					return mrId;
				
			} catch (MalformedURLException e) {
				
				logger.error("Exception",e);
			} catch (IOException e) {
				
				logger.error("Exception",e);
				
			}
			return null;
		}
		
		private String replaceBarcodePhWithDummyCode(String smsMsg) {
			// TODO Auto-generated method stub
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
		}

		public String parseInitialAutoSMSResponse(String response) {
			logger.info("auto sms response msgbird--"+response);
			try {
				Gson gson1 = new Gson();
				MessagebirdResponse msgResponse =new MessagebirdResponse();
				msgResponse = gson1.fromJson(response,MessagebirdResponse.class);
				
				String mrId="";
				String msgId="";
					mrId=msgResponse.getId();
					if(msgId.equalsIgnoreCase(Constants.STRING_NILL)) msgId=mrId;
					logger.info("mrId== "+msgResponse.getId());
				
				
				return msgId;
			} catch(Exception e){
				logger.error("Exception",e);
				return null;
			}
			
		}//parseInitialAutoSMSResponse
		
			
		
		public class PrepareMessageBirdJsonRequest {

			private List<MessageBirdTextList> messages;

			public List<MessageBirdTextList> getMessages() {
				return messages;
			}

			public void setMessages(List<MessageBirdTextList> messages) {
				this.messages = messages;
			}


			public  PrepareMessageBirdJsonRequest() {
				
			}
			
}
		
		public void prepareData(String userID, String pwd, String senderId, String messageContent, String toMobNum) {
				messageContent = messageContent.replace("|^", "[").replace("^|", "]");
			
			logger.info("Before test messageContent: "+messageContent);
			
			messageContent = replaceBarcodePhWithDummyCode(messageContent);//we can shift this method to other place
			
				String[] mobileArray =  toMobNum.split(",");
				for(String oneMobile : mobileArray){
					oneMobile = oneMobile.trim();
					sendTestSMS(messageContent,oneMobile);
				}			
		}
}
