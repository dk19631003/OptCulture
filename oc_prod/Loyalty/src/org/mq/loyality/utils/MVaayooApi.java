package org.mq.loyality.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MVaayooApi {

	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);
	public void prepareData(String userID, String pwd, String messageContent, String msgType,
			String fromMobNum, String toMobNum, String msgSeq, String senderId) throws Exception {
		
		
	
		
		//replace Barcode placeholders with dummy barcode
		
		messageContent = messageContent.replace("|^", "[").replace("^|", "]");
		
		
		logger.info("Before test messageContent: "+messageContent);
		//if(msgType.equals(Constants.SMS_TYPE_TRANSACTIONAL)) {
			
			MVaayooHTTPSample mVaayooHTTPSample  = new MVaayooHTTPSample(userID, pwd);
			mVaayooHTTPSample.test(messageContent, toMobNum, senderId);
			
		
		//messageContent = replaceBarcodePhWithDummyCode(messageContent);
		
		
		/*if(toMobNum.contains(",")){
			//******need to split the mobile numbers and send to each individual contact seperately********
			mobile = toMobNum.split(",");
			for (int i = 0; i < mobile.length; i++) {
				
				
			}//for
			
		} 
		
		sms = "";*/
		
		
		
		
		//msgToBeSent = msgReqStructure.replace("<MESSAGESTRUCTURE>", sms);
		
		
	/*	if(toMobNum.contains(",")) {
			
			multiple = true;
			
		}
		if(msgType.equals("1")) {
			
			if(multiple) {
				
				msgToBeSent = multiple_requestStructure.replace("<VAL_TO>", toMobNum ).replace("<VAL_TXT>", messageContent);
			}else{
				
				msgToBeSent = requestStructure.replace("<VAL_TO>", toMobNum ).replace("<VAL_TXT>", messageContent);
			}
			
		}else if(msgType.equals("2")) {
			
			if(multiple) {
				
				msgToBeSent = multiple_requestStructure2_way.replace("<VAL_TO>", toMobNum ).replace("<VAL_TXT>", messageContent);
			}else{
				
				msgToBeSent = requestStructure2_way.replace("<VAL_TO>", toMobNum ).replace("<VAL_TXT>", messageContent);
			}
			
			
		}*/
		
		//String msgToBeSent = requestStructure.replace("<VAL_TO>", toMobNum ).replace("<VAL_TXT>", messageContent);
		
		
	}
	public String sendSMSOverHTTP(String content, String mobilenumber, String senderID, String userID, String pwd) {
		
		MVaayooHTTPSample httpSampleApp = new MVaayooHTTPSample(userID, pwd);
		logger.debug("=====started sending====");
		
		String response = httpSampleApp.test(content, mobilenumber, senderID);
		logger.debug("=====ended here====");
		return response;
	}
/*private String replaceBarcodePhWithDummyCode(String smsMsg){
		
		try{
		
		Set<String> coupPhSet = findCoupPlaceholders(smsMsg);
		
		if(coupPhSet != null  && coupPhSet.size() > 0) {
			
			if(couponsDao == null){
				couponsDao = (CouponsDao)applicationContext.getBean("couponsDao");
			}
			
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
					}
				}
				
			}
		}
		}catch(Exception e){
			logger.error("Exception in replacing barcode placeholder", e);
		}
		return smsMsg;
	}
	*/

	
	
}
