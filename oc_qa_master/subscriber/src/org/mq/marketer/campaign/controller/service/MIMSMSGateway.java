package org.mq.marketer.campaign.controller.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Set;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.DRSMSSent;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class MIMSMSGateway {

		private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
		private String userID ;
		private String pwd ;
		private String senderId;
		private String peId;
		private OCSMSGateway ocsmsGateway;
		private final String urlStr = "https://myinboxmedia.in/api/sms/SendSMS";

		
		public MIMSMSGateway() {}
		
		public MIMSMSGateway(OCSMSGateway ocsmsGateway,String userID,String pwd,String senderId,String peId) {
			
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
		
		public MIMSMSGateway(String userID, String pwd, String senderId,String peId) {
			
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
				Gson gson = new Gson();
				PrepareMIMJsonRequest pj=new PrepareMIMJsonRequest();				
				
				pj.setUserid(userID);
				pj.setPwd(pwd);
				pj.setMobile(mobile);
				pj.setSender(senderId);
				pj.setMsg(content);
				pj.setMsgtype("16");
				
				String request = gson.toJson(pj);
				logger.info("requestJson for MIMSMSGateway is--->"+request);

				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost postRequest = new HttpPost(urlStr);
				StringEntity input = new StringEntity(request);
				input.setContentType("application/json");
				postRequest.setEntity(input);
				
				HttpResponse response = httpClient.execute(postRequest);
					
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
			
			} catch (MalformedURLException e) {
				
				logger.error("Exception",e);
			} catch (IOException e) {
				
				logger.error("Exception",e);
				
			}
		}	
		
	

		
		public String sendDRSMS(String content, String mobile,String templateRegisteredId,DRSMSSent drSmsSent) {
			

			
			try{
				
				
				content = content.replace("|^", "[").replace("^|", "]");
				
				logger.info("Before content: "+content);
				
				content = replaceBarcodePhWithDummyCode(content);
				
				Gson gson = new Gson();
				PrepareMIMJsonRequest pj=new PrepareMIMJsonRequest();				
				
				pj.setUserid(userID);
				pj.setPwd(pwd);
				pj.setMobile(mobile);
				pj.setSender(senderId);
				pj.setMsg(content);
				pj.setMsgtype("16");
				
				String request = gson.toJson(pj);
				logger.info("requestJson for MIMSMSGateway is--->"+request);

				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost postRequest = new HttpPost(urlStr);
				StringEntity input = new StringEntity(request);
				input.setContentType("application/json");
				postRequest.setEntity(input);
				
				HttpResponse response = httpClient.execute(postRequest);
					
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
				
				logger.error("Exception",e);
			} catch (IOException e) {
				
				logger.error("Exception",e);
				
			}
			return null;
		}
		
		
		public String SendDLR(String mobile ,DRSMSSent drSmsSent,String msgId) {
			

			logger.info("calling SendDLR method of MIM gateway");

			try{
				
				Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				PrepareMIMDLRRequest pj=new PrepareMIMDLRRequest();				
				
				pj.setUserId(userID);
				pj.setPwd(pwd);
				pj.setMsgId(msgId);
				
				String request = gson.toJson(pj);
				logger.info("DLRrequest for MIMSMSGateway is--->"+request);

				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost postRequest = new HttpPost("https://myinboxmedia.in/api/sms/GetDelivery");
				StringEntity input = new StringEntity(request);
				input.setContentType("application/json");
				postRequest.setEntity(input);
				
				HttpResponse response = httpClient.execute(postRequest);
					
				BufferedReader br = new BufferedReader(
						new InputStreamReader((response.getEntity().getContent())));
				
				String resp="";
				String output="";
				logger.info("Output from Server .... \n");
				while ((output = br.readLine()) != null) {
						if(output!=null)resp=resp+output;
						logger.info("output fromDLR MIM---"+resp);
					}
				
				httpClient.getConnectionManager().shutdown();
				String DLRStatus = getStatus(resp);
				return DLRStatus;
				
			} catch (MalformedURLException e) {
				
				logger.error("Exception",e);
			} catch (IOException e) {
				
				logger.error("Exception",e);
				
			}
			return null;
		}
		
		
	public class PrepareMIMJsonRequest {
			
			private String userid; 
			private String pwd;
			private String mobile;
			private String sender;
			private String msg;
			private String msgtype;
			
			public PrepareMIMJsonRequest(){
				//Default Constructor
			}
			
			
			
			public String getUserid() {
				return userid;
			}

			public void setUserid(String userid) {
				this.userid = userid;
			}

			public String getPwd() {
				return pwd;
			}

			public void setPwd(String pwd) {
				this.pwd = pwd;
			}

			public String getMobile() {
				return mobile;
			}

			public void setMobile(String mobile) {
				this.mobile = mobile;
			}

			public String getSender() {
				return sender;
			}

			public void setSender(String sender) {
				this.sender = sender;
			}

			public String getMsg() {
				return msg;
			}

			public void setMsg(String msg) {
				this.msg = msg;
			}

			public String getMsgtype() {
				return msgtype;
			}

			public void setMsgtype(String msgtype) {
				this.msgtype = msgtype;
			}

			
			
			
		}
		
		
		public class PrepareMIMDLRRequest{
			
			private String userId;
			private String pwd;
			private String msgId;
			
			public String getUserId() {
				return userId;
			}


			public void setUserId(String userId) {
				this.userId = userId;
			}


			public String getPwd() {
				return pwd;
			}


			public void setPwd(String pwd) {
				this.pwd = pwd;
			}


			public String getMsgId() {
				return msgId;
			}


			public void setMsgId(String msgId) {
				this.msgId = msgId;
			}
			
			public  PrepareMIMDLRRequest() {
			
			}
			
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
			logger.info("auto sms response mim--"+response);
			try {
				Gson gson1 = new Gson();
				MIMResponse[] mimResponse = gson1.fromJson(response,MIMResponse[].class);
				String Response="";
			       for(MIMResponse mimResponses : mimResponse) {
			    	   Response = mimResponses.getResponse();
			       }
				 String[] parts = Response.split("Message ID: ");
			     System.out.println("parts 1: " + parts[1]);
			     String messageId = parts[1].substring(0, parts[1].length());
			     if(messageId!=null) {
			     	logger.info("messageId== "+messageId);
			     	return messageId;
			    }else {
			    	return null; 
			    }
			} catch(Exception e){
				logger.error("Exception",e);
				return null;
			}
			
		}//parseInitialAutoSMSResponse
		
		public String getStatus(String response) {
			logger.info(" sms response mim--"+response);
			try {
				Gson gson1 = new Gson();
				MIMResponse[] mimResponse = gson1.fromJson(response,MIMResponse[].class);
				String Response="";
			       for(MIMResponse mimResponses : mimResponse) {
			    	   Response = mimResponses.getResponse();
			       }
			       String[] parts = Response.split(",");
			       String status = parts[0].split(":")[1].trim();
				 if(status!=null) {
			     	logger.info("DLR status== "+status);
			     	return status;
			    }else {
			    	return null; 
			    }
			} catch(Exception e){
				logger.error("Exception",e);
				return null;
			}
			
		}//getStatus 
		
		
			
		
		public class MIMResponse {

		    private String Response;
		    
		    public String getResponse() {
				return Response;
			}

		    public void setResponse(String response) {
				Response = response;
			}

		    public String toString() {
		        return "SmsResponseList{" +
		                "response='" + Response + '\'' +
		                '}';
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
}
