package org.mq.marketer.campaign.controller.service;

	
	import java.io.BufferedReader;
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
	import java.io.ByteArrayInputStream;
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
	import org.mq.marketer.campaign.beans.OCSMSGateway;
	import org.mq.marketer.campaign.controller.service.SynapseSMSGateway.SynapseSMSList.MessageParams;
	import org.mq.marketer.campaign.dao.CouponsDao;
	import org.mq.marketer.campaign.dao.SMSCampaignSentDao;
	import org.mq.marketer.campaign.general.Constants;
	import org.mq.marketer.campaign.general.PropertyUtil;
	import org.mq.marketer.campaign.general.Utility;
	import org.mq.optculture.model.BaseRequestObject;
	import org.mq.optculture.model.EquenceDLRResponseObject;
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

	public class SynapseSMSGateway {

		private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
		private String userID ;
		private String pwd ;
		private String senderId;
		private OCSMSGateway ocsmsGateway;
		private final String urlStr = "https://api.me.synapselive.com/v1/multichannel/messages/sendsms";
		
		
		public SynapseSMSGateway() {}
		
		public SynapseSMSGateway(OCSMSGateway ocsmsGateway,String userID,String pwd,String senderId) {
			
			this.ocsmsGateway = ocsmsGateway;
			this.userID = userID;
			this.pwd = pwd;
			this.senderId=senderId;
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
		
		public SynapseSMSGateway(String userID, String pwd, String senderId) {
			
			this.userID = userID;
			this.pwd = pwd;
			this.senderId=senderId;
			
			
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
					logger.info("content "+content);
					pj.setMessage(content);
					pj.setMobileNumbers(synapseSMSList);
					
					String request = gson.toJson(pj);
					logger.info("requestJson--->"+request);
					
					DefaultHttpClient httpClient = new DefaultHttpClient();
					HttpPost postRequest = new HttpPost("https://api.me.synapselive.com/v1/multichannel/messages/sendsms");
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
					logger.info("Output from Server .... \n");
					while ((output = br.readLine()) != null) {
						logger.info(output);
					}
					
					httpClient.getConnectionManager().shutdown();
				
			} catch (MalformedURLException e) {
				
				logger.error("",e);
			} catch (IOException e) {
				
				logger.error("",e);
				
			}
		}	
		
		public void prepareData(String userID, String pwd, String senderID, String messageContent, String toMobNum) throws Exception {
		messageContent = messageContent.replace("|^", "[").replace("^|", "]");
		
		logger.info("Before test messageContent: "+messageContent);
		
		messageContent = replaceBarcodePhWithDummyCode(messageContent);//we can shift this method to other place
		
			String[] mobileArray =  toMobNum.split(",");
			for(String oneMobile : mobileArray){
				oneMobile = oneMobile.trim();
				sendTestSMS(messageContent,oneMobile);
			}
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
		}
		
		public class SynapseSMSList {
			
			//private List<MessageParams> mobileNumbers;
			private List<MessageParams> messageParams;
			
			public List<MessageParams> getMobileNumbers() {
				return messageParams ;
			}

			public void setMobileNumbers(List<MessageParams> messageParams) {
				this.messageParams = messageParams;
			}

			public class MessageParams{
				
				private String mobileNumber;
				
				public String getMobileNumber() {
					return mobileNumber;
				}
				public void setMobileNumber(String mobileNumber) {
					this.mobileNumber = mobileNumber;
				}
			}
		}
		
		public class PrepareSynapseJsonRequest {
			
			private String userName;
			private String priority;
			private String referenceId;
			private String dlrUrl;
			private String msgType;
			private String senderId;
			private String message;
			private SynapseSMSList mobileNumbers;
			private String password;
			
			
			public PrepareSynapseJsonRequest(){
			//Default Constructor
			}
			
			public String getUserName() {
			return userName;
			}
			
			public void setUserName(String username) {
			this.userName = username;
			}
			
			public String getPriority() {
				return priority;
			}

			public void setPriority(String priority) {
				this.priority = priority;
			}

			public String getPassword() {
			return password;
			}
			
			public void setPassword(String password) {
			this.password = password;
			}
			public String getReferenceId() {
				return referenceId;
			}
			
			public void setReferenceId(String referenceId) {
				this.referenceId = referenceId;
			}
			
			public String getMsgType() {
				return msgType;
			}
			public void setMsgType(String msgType) {
				this.msgType = msgType;
			}
			public String getSenderId() {
				return senderId;
			}
			public void setSenderId(String senderId) {
				this.senderId = senderId;
			}
			public String getMessage() {
				return message;
			}
			public void setMessage(String message) {
				this.message = message;
			}
			
			public SynapseSMSList getMobileNumbers() {
				return mobileNumbers;
			}

			public void setMobileNumbers(SynapseSMSList mobileNumbers) {
				this.mobileNumbers = mobileNumbers;
			}

			public String getDlrUrl() {
				return dlrUrl;
			}

			public void setDlrUrl(String dlrUrl) {
				this.dlrUrl = dlrUrl;
			}

		}
	}

