package org.mq.loyality.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.loyality.common.hbmbean.OCSMSGateway;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class InfocommSMSGateway implements ApplicationContextAware{

	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);
	private String userID ;
	private String pwd ;
	private String senderId;
	private OCSMSGateway ocsmsGateway;
	private final String queryString = "https://www.ismartsms.net/iBulkSMS/HttpWS/SMSDynamicAPI.aspx?"
			+ "UserId=<USER>&Password=<PWD>&MobileNo=<TO>&Message=<MESSAGE>&Lang=0&FLashSMS=N";
	private ApplicationContext applicationContext;
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
		
		public InfocommSMSGateway() {}
		
		public InfocommSMSGateway(OCSMSGateway ocsmsGateway,String userID,String pwd,String senderId) {
			
			this.ocsmsGateway = ocsmsGateway;
			this.userID = userID;
			this.pwd = pwd;
			this.senderId=senderId;
		}
		
		public InfocommSMSGateway(String userID, String pwd, String senderId) {
			
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
				//"https ://www.ismartsms.net/iBulkSMS/HttpWS/SMSDynamicAPI.aspx?"
						//+ "UserId=<USER>&Password=<PWD>&MobileNo=<TO>&Message=<MESSAGE>&Lang=0& FLashSMS=N";

				String postData = this.queryString.replace("<USER>", getUserID()).replace("<PWD>", URLEncoder.encode(getPwd(), "UTF-8"))
					     .replace("<TO>", mobile)
					     .replace("<MESSAGE>", URLEncoder.encode(content, "UTF-8"));
		
				logger.debug("postData======>"+postData);
				
				URL url = new URL(postData);
				
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
