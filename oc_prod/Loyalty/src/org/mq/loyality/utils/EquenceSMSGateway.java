package org.mq.loyality.utils;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.loyality.common.hbmbean.OCSMSGateway;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class EquenceSMSGateway implements ApplicationContextAware{



		private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);
		private String userID ;
		private String pwd ;
		private String senderId;
		private OCSMSGateway ocsmsGateway;
		private final String urlStr = "http://api.equence.in/pushsms";
		
		private ApplicationContext applicationContext;

		public ApplicationContext getApplicationContext() {
				return applicationContext;
			}

		public void setApplicationContext(ApplicationContext applicationContext) {
			this.applicationContext = applicationContext;
		}
		public EquenceSMSGateway() {}
		
		public EquenceSMSGateway(OCSMSGateway ocsmsGateway,String userID,String pwd,String senderId) {
			
			this.ocsmsGateway = ocsmsGateway;
			this.userID = userID;
			this.pwd = pwd;
			this.senderId=senderId;
		}
		
		public EquenceSMSGateway(String userID, String pwd, String senderId) {
			
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
				
				List<EquenceTextList> textLst = new ArrayList<EquenceTextList>();
				EquenceTextList equenceTextList = new EquenceTextList();
				equenceTextList.setTo(mobile);
				equenceTextList.setText(content);
				textLst.add(equenceTextList);
					Gson gson = new Gson();
					PrepareEquenceJsonRequest pj=new PrepareEquenceJsonRequest();
					pj.setUsername(userID);
					pj.setPassword(pwd);
					pj.setFrom(senderId);
					pj.setTextlist(textLst);
					
					String request = gson.toJson(pj);
					logger.info("requestJson--->"+request);
					
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
					httpClient.getConnectionManager().shutdown();
				
			} catch (MalformedURLException e) {
				
				logger.error("Exception",e);
			} catch (IOException e) {
				
				logger.error("Exception",e);
				
			}
		}	
		
		public class PrepareEquenceJsonRequest {
			
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
		
		public class PrepareEquenceJsonResponse {

			/*{
			"username":"****",
			"password":"****",
			"from":"SMSTST",
			"textlist":[                                                                                 
			                   {"to":"9206774674","text":"hi test message1"},
			                   {"to":"8895801942","text":"hi test message2"},
			                   {"to":"7377069728","text":"hi test message3"}]
		}*/
			private String username; 
			private String password;
			private String from;
			private List<EquenceTextList> textlist; 
			
			public PrepareEquenceJsonResponse(){
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
		public class EquenceTextList {

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
		}
				
		/*public void prepareData(String userID, String pwd, String senderID, String messageContent, String toMobNum) throws Exception {
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
		}*/

}
