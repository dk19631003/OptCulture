package org.mq.marketer.sparkbase.restservice;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.SparkBaseCard;
import org.mq.marketer.campaign.beans.SparkBaseLocationDetails;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.SparkBaseCardDao;
import org.mq.marketer.campaign.dao.SparkBaseLocationDetailsDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.sparkbase.SparkBaseServiceAsync;
import org.mq.marketer.sparkbase.transactionWsdl.ArrayOfBalance;
import org.mq.marketer.sparkbase.transactionWsdl.ErrorMessageComponent;
import org.mq.marketer.sparkbase.transactionWsdl.GiftRedemptionResponse;
import org.mq.marketer.sparkbase.transactionWsdl.LoyaltyRedemptionResponse;
import org.mq.marketer.sparkbase.transactionWsdl.ResponseStandardHeaderComponent;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class LoyaltyRedemptionService extends AbstractController{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private UsersDao usersDao;
	private SparkBaseLocationDetailsDao sparkBaseLocationDetailsDao;
	private SparkBaseCardDao sparkBaseCardDao;
	private ContactsLoyaltyDao contactsLoyaltyDao;
	private ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML;
	
	public ContactsLoyaltyDaoForDML getContactsLoyaltyDaoForDML() {
		return contactsLoyaltyDaoForDML;
	}


	public void setContactsLoyaltyDaoForDML(
			ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML) {
		this.contactsLoyaltyDaoForDML = contactsLoyaltyDaoForDML;
	}
	
	public LoyaltyRedemptionService(){
		
	}
	
	
	public UsersDao getUsersDao() {
		return usersDao;
	}

	public void setUsersDao(UsersDao usersDao) {
		this.usersDao = usersDao;
	}

	public SparkBaseLocationDetailsDao getSparkBaseLocationDetailsDao() {
		return sparkBaseLocationDetailsDao;
	}

	public void setSparkBaseLocationDetailsDao(
			SparkBaseLocationDetailsDao sparkBaseLocationDetailsDao) {
		this.sparkBaseLocationDetailsDao = sparkBaseLocationDetailsDao;
	}

	public SparkBaseCardDao getSparkBaseCardDao() {
		return sparkBaseCardDao;
	}

	public void setSparkBaseCardDao(SparkBaseCardDao sparkBaseCardDao) {
		this.sparkBaseCardDao = sparkBaseCardDao;
	}

	public ContactsLoyaltyDao getContactsLoyaltyDao() {
		return contactsLoyaltyDao;
	}

	public void setContactsLoyaltyDao(ContactsLoyaltyDao contactsLoyaltyDao) {
		this.contactsLoyaltyDao = contactsLoyaltyDao;
	}
	
	

	private final Pattern digitPattern = Pattern.compile("(\\d+)");  // %B974174416697245^?;974174416697245=?
	private Matcher matcher = null;
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String msg = "";
		String emailMsg = "";
		int errorCode = 0;
		
		JSONObject headerInfo = null;
		JSONObject redemptionInfoJsonObj = null;
		
		//Contacts contact = null;
		SparkBaseLocationDetails sparkBaseLoc; 
		
		PrintWriter printWriter = response.getWriter();
		response.setContentType("application/json");
		
		logger.debug("entered into loyalt/Gift Redemption service>>>>>>>");
		
		LoyaltyRedemptionResponse loyaltyRedemptionResponse = null;
		GiftRedemptionResponse giftRedemptionResponse = null; 
		
		try{
		
		
		Enumeration<String> enumerator = request.getParameterNames();
		logger.debug("enumberator size: "+enumerator);
		while(enumerator.hasMoreElements()){
			String reqParam = enumerator.nextElement();
			
			logger.debug("parameter: "+reqParam);
			logger.debug("value: "+request.getParameter(reqParam));
		}
		
		
		JSONObject jsonRootObject = null;
		
		if(request.getParameter("jsonVal") != null && request.getParameter("jsonVal").length() > 0){
			String jsonValStr = request.getParameter("jsonVal");
			jsonRootObject = (JSONObject)JSONValue.parse(jsonValStr);
			
			if(jsonRootObject == null){
				
				logger.debug("Error : Invalid json Object as jsonVal .. Returning. ****");
				msg = "Error : Invalid json Object .. Returning. ****"+emailMsg;
				errorCode = 101201;
				return null;
			}
			
		}
		else{
			
			InputStream is = request.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			char[] chr = new char[1024];
			int bytesRead = 0;
			StringBuilder sb = new StringBuilder();
			
			while((bytesRead = br.read(chr)) > 0 ){
				sb.append(chr, 0, bytesRead);
			}
			
			try {
				
				jsonRootObject = (JSONObject)JSONValue.parse(sb.toString());
			} catch(Exception e) {
				
				logger.debug("Error : Invalid json Object as REST... ****");
				msg = "Error : Invalid json Object ... ****"+emailMsg;
				errorCode = 101001;
				return null;
			}
			
		}
		
		if(jsonRootObject == null) {
			
			logger.debug("Error : Unable to parse the json.. Returning. ****");
			msg = "Error : Unable to parse the json... ****"+emailMsg;
			errorCode = 101002;
			return null;
		}
		
		JSONObject jsonMainObject = (JSONObject)jsonRootObject.get("LOYALTYREDEMPTIONREQ");
		
		if(jsonMainObject == null) {
			
			logger.debug("Error : unable to find the LoyaltyRedemptionReq JSON ****");
			msg = "Error : Unable To Find The LoyaltyRedemptionReq In JSON."+emailMsg;
			errorCode = 200100;
			return null;
		}
		
		logger.debug("json Main object : "+jsonMainObject.toString());
		
		
		headerInfo = (JSONObject)jsonMainObject.get("HEADERINFO");
		//InquiryInfo json object in InquiryRequest
		redemptionInfoJsonObj = (JSONObject)jsonMainObject.get("REDEMPTIONINFO");
		
		if(redemptionInfoJsonObj == null) {
			
			logger.debug("Error : unable to find the RedemptionInfo in JSON ****");
			msg = "Error : unable to find the RedemptionInfo in JSON "+emailMsg;
			errorCode = 200101;
			return null;
		}
		
		//JSONObject customerInfoJson = (JSONObject)jsonMainObject.get("CustomerInfo");
		
		
		//UserDetails json object in InquiryRequest
		JSONObject userJsonObj = (JSONObject)jsonMainObject.get("USERDETAILS");
		
		if(userJsonObj == null) {
			
			logger.debug("Error : unable to find the User Details in JSON ****");
			msg = "Error : unable to find the User Details in JSON "+emailMsg;
			errorCode = 101011;
			return null;
		}
		
		String userToken = userJsonObj.get("TOKEN") == null ? null : userJsonObj.get("TOKEN").toString().trim();
		String userName = userJsonObj.get("USERNAME") == null ? null : userJsonObj.get("USERNAME").toString().trim();
		String userOrg = userJsonObj.get("ORGANISATION") == null ? null : userJsonObj.get("ORGANISATION").toString().trim();
		
		
		if((userToken == null || userToken.trim().length() <=0) || 
				(userName == null || userName.trim().length() <=0) || 
				(userOrg == null || userOrg.trim().length() <=0)) {
			
			logger.debug("Error : User Token,UserName,Organisation cannot be empty.");
			msg = "Error : User Token,UserName,Organisation cannot be empty."+emailMsg;
			errorCode = 101012;
			return null;
		}
		
		userName = userName + Constants.USER_AND_ORG_SEPARATOR + userOrg;
		//logger.debug("userToken: "+userName+"   "+userToken); -- APP-3914
		Users user = usersDao.findByToken(userName, userToken);
		if(user == null) {
			
			logger.debug("Unable to find the user Obj with Token : "+ userToken);
			msg = "Error : Unable to find the User with Token : "+ userToken+emailMsg;
			errorCode = 101013;
			return null;
		}
		
		
		List<SparkBaseLocationDetails> sbDetailsList = sparkBaseLocationDetailsDao.findAllByOrganizationId(user.getUserOrganization().getUserOrgId());
		
		if(sbDetailsList == null) {
			
			logger.debug(" No spark Base Details Found for this org: "+ userOrg);
			msg = "Error : No spark Base Details Found for this org: "+ userOrg+emailMsg;
			errorCode = 101005;
			return null;
			
		}
		sparkBaseLoc = sbDetailsList.get(0);
		
		
		if(sparkBaseLoc == null) {
			
			logger.debug("Error : No SparkBaseDetails Found with the given credentials. ****");
			msg = "Error : No SparkBase Details Found with the given credentials."+emailMsg;
			errorCode = 101006;
			return null;
		}
		
		logger.debug("Got sparkbaseloc object "+ sparkBaseLoc);
		
		//String posStoreLocId = redemptionInfoJsonObj.get("StoreLocationId") == null ? null : redemptionInfoJsonObj.get("StoreLocationId").toString();
		String cardNumber = redemptionInfoJsonObj.get("CARDNUMBER") == null ? null : redemptionInfoJsonObj.get("CARDNUMBER").toString().trim();
		String customerId = redemptionInfoJsonObj.get("CUSTOMERID") == null ? null : redemptionInfoJsonObj.get("CUSTOMERID").toString().trim();
		String cardPin = redemptionInfoJsonObj.get("CARDPIN") == null ? null : redemptionInfoJsonObj.get("CARDPIN").toString().trim();
		String cardType = redemptionInfoJsonObj.get("CARDTYPE") == null ? null : redemptionInfoJsonObj.get("CARDTYPE").toString().trim();
		String valueCode = redemptionInfoJsonObj.get("VALUECODE") == null ? null : redemptionInfoJsonObj.get("VALUECODE").toString().trim();
		String enteredAmount = redemptionInfoJsonObj.get("ENTEREDAMOUNT") == null ? null : redemptionInfoJsonObj.get("ENTEREDAMOUNT").toString().trim();
		
		//boolean success = false;
		if((cardNumber == null || cardNumber.trim().length() <= 0) && 
				(customerId == null || customerId.trim().length() <= 0)){
			logger.debug("Error : Card Number, CustomerId Invalid."+emailMsg);
			msg = "Error : Card Number, CustomerId Invalid."+emailMsg;
			errorCode = 200003;
			return null;
		}
		
		String[] amount = new String[2];
		//String valueCode = issuanceInfoJsonObj.get("ValueCode") == null ? null : issuanceInfoJsonObj.get("ValueCode").toString();
		
		
		if((valueCode == null || valueCode.trim().length() <= 0) || 
				(enteredAmount == null || enteredAmount.trim().length() <= 0 || enteredAmount.equals("0"))){
			logger.debug("Error : Amount Details Invalid."+emailMsg);
			msg = "Error : Amount Details Invalid."+emailMsg;
			errorCode = 200004;
			return null;
		}
		
		amount[0] = valueCode;
		amount[1] = enteredAmount;
		
		if((cardType == null || cardType.trim().length() <= 0 ) || 
				(valueCode == null || valueCode.trim().length() <= 0 )){

			logger.debug("CardType or ValueCode Invalid.");
			msg = "CardType or ValueCode Invalid.";
			errorCode = 200005;
			return null;
		}
		
		ContactsLoyalty contactLoyalty  = null;
		//Contacts tempContact = null;
		String cardLong = null;
		
		
		//get contact loyalty object with cardNumber
		if(cardNumber != null && cardNumber.trim().length() > 0 ){
			
			    try {
			    	
			    	String cardNum = "";
			    	  matcher = digitPattern.matcher(cardNumber);
				          while (matcher.find()) {
				        	  if(cardNum.length() == 15 || cardNum.length() == 16 ) break;
				        	  cardNum += matcher.group(1).trim();
				          } // while
				    cardNumber = cardNum;
			          
			    	logger.debug("Card NUmber After removing Extra char: "+cardNumber);
			    	
			    	cardLong = cardNumber;
			    } catch(NumberFormatException e) {
			    	//logger.error("Exception ::::", e);
			    	logger.debug("card format error");
					msg = "Card format Error, given card is invalid : "+cardNumber;
					errorCode = 100107;
					//retStrArr[0]=msg;
					//retStrArr[1]=""+errorCode;
					return null;
			    }	
			
			contactLoyalty = contactsLoyaltyDao.getContactsLoyaltyByCardId(cardLong, user.getUserId());
			
		}
		else if(customerId != null && customerId.trim().length() > 0) { //get loyalty object with customer Id
			
			contactLoyalty = contactsLoyaltyDao.getContactsLoyaltyByCustId(customerId, user.getUserId());
			
		}
	
		//Loyalty found, contact has been deleted from OC
		Contacts contact = null;
		
		if(contactLoyalty != null){
			contact = contactLoyalty.getContact();
			cardLong = contactLoyalty.getCardNumber();
			if(contact == null){
				logger.debug("Loyalty Found, But Contact Not Found.");
				msg = "Loyalty Found, But Contact Not Found.";
				errorCode = 200006;
				return null;
			}
			
			if(contact != null && contact.getMlBits().longValue() == 0){
				logger.debug("Loyalty Found, But Contact Has Been Deleted.");
				msg = "Loyalty Found, But Contact Has Been Deleted.";
				errorCode = 200007;
				return null;
				
			}
			
		}
		
		
		//TODO handle customer has more than one card
		if(contactLoyalty != null && !contactLoyalty.getCardNumber().equalsIgnoreCase(cardLong)){
			logger.debug("loyalty card number: "+contactLoyalty.getCardNumber()+" cardLong: "+cardLong);
			msg = "Loyalty Found, Given Card Number Not Matched.";
			errorCode = 200008;
			return null;
		}
		
		
		
		
		ErrorMessageComponent errorMsg = null;
		Object responseObject = null;
		//Contacts dbContact = null;
		
		if(contactLoyalty == null){//loyalty not found in OC
			
			List<SparkBaseCard> sblist = sparkBaseCardDao.findByCardId(sparkBaseLoc.getSparkBaseLocationDetails_id(), cardLong);
			
			if(sblist == null){
				
				logger.debug("Card Not Available, Upload Given Card To OptCulture.");
				msg = "Card Not Available, Upload Given Card To OptCulture.";
				errorCode = 200009;
				return null;
			}
			
			
			if(sblist != null){
				
				SparkBaseCard sbCard = sblist.get(0);
				if(sbCard.getStatus().equals(Constants.SPARKBASE_CARD_STATUS_INVENTORY)){
					
					logger.debug("Card Status Inventory, Please Enroll The Given Card.");
					msg = "Card Status Inventory, Please Enroll The Given Card.";
					errorCode = 200010;
				}
				
			}
		}
		
		else if(contactLoyalty != null && contactLoyalty.getCardNumber() != null){ //contact loyalty found in OC , make issue call to SB
			
			if(valueCode.equals("Points")){
				
				responseObject = SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.LOYALTYREDEMPTION, sparkBaseLoc, contactLoyalty, null, amount, true);
			}
			else if(valueCode.equals("USD")){
				
				responseObject = SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.GIFTREDEMPTION, sparkBaseLoc, contactLoyalty, null, amount, true);
			}
			else{
				logger.debug("CardType or ValueCode Invalid.");
				msg = "CardType or ValueCode Invalid.";
				errorCode = 200011;
				return null;
			}
			
			if(responseObject instanceof ErrorMessageComponent) {
				
				errorMsg = (ErrorMessageComponent)responseObject;
			} else if ((responseObject instanceof LoyaltyRedemptionResponse)){
				
				loyaltyRedemptionResponse = (LoyaltyRedemptionResponse)responseObject;
				
				ResponseStandardHeaderComponent standardHeader = loyaltyRedemptionResponse.getStandardHeader();
				
				if (standardHeader.getStatus().equals("E")) {
			          logger.debug("Printing Error...");
			          errorMsg = loyaltyRedemptionResponse.getErrorMessage();
			    }
			}
			
			else if ((responseObject instanceof GiftRedemptionResponse)){
				
				giftRedemptionResponse = (GiftRedemptionResponse)responseObject;
				
				ResponseStandardHeaderComponent standardHeader = giftRedemptionResponse.getStandardHeader();
				
				if (standardHeader.getStatus().equals("E")) {
			          logger.debug("Printing Error...");
			          errorMsg = giftRedemptionResponse.getErrorMessage();
			    }
			}
			
			if(errorMsg != null){
				logger.debug("Loyalty Redemption Failed, Server Error"+", "+errorMsg.getBriefMessage());
				errorCode = 200102;
				msg = "Loyalty Redemption Failed, Server Error"+", "+errorMsg.getBriefMessage();
				return null;
			}
			
			contactsLoyaltyDaoForDML.saveOrUpdate(contactLoyalty);
			
			if(cardType.equals("Loyalty") && valueCode.equals("Points")){
				logger.debug("Loyalty Redemption Successful.");
				msg = "Loyalty Redemption Successful.";
			}
			else if(cardType.equals("GiftCard") && valueCode.equals("USD")){
				logger.debug("Gift Redemption Successful.");
				msg = "Gift Redemption Successful.";
			}
			//success error code
			errorCode = 0;
			
			
		}
		
		}
		catch(Exception e){
			logger.error("Exception ::::", e);
			msg = "Error : Server error occured."+emailMsg;
			errorCode = 101000;
			
		}
		finally{

			try {
				//LinkedHashMap customerInfo = null;
				LinkedHashMap jsonMainObject2 = new LinkedHashMap();
				
				if(errorCode == 0) {
					
					JSONArray balances = new JSONArray();
					JSONObject amountDetails = null;
					ArrayOfBalance aBalances = null;				
					
					if (loyaltyRedemptionResponse != null){
						aBalances = loyaltyRedemptionResponse.getBalances();
					}
					else if (giftRedemptionResponse != null){
						aBalances = giftRedemptionResponse.getBalances();
					}
					
					
					if(aBalances != null && aBalances.getBalance() != null){
					String valueCode = null;
				    String amount = null;
				    for(int i = 0; i < aBalances.getBalance().size(); i++) {
				    	amountDetails = new JSONObject();
				    	  amountDetails.put("VALUECODE", aBalances.getBalance().get(i).getValueCode());
				    	  amountDetails.put("AMOUNT", aBalances.getBalance().get(i).getAmount());
				    	  amountDetails.put("DIFFERENCE", aBalances.getBalance().get(i).getDifference());
				    	  amountDetails.put("EXCHANGERATE", aBalances.getBalance().get(i).getExchangeRate());
				    	  
				    	  balances.add(amountDetails);
				    }
					}
					jsonMainObject2.put("BALANCES",balances);
				}
				
				
				JSONObject statusObject = new JSONObject();
				statusObject.put("STATUS", errorCode == 0 ? "Success" : "Failure");
				statusObject.put("MESSAGE", msg);
				statusObject.put("ERRORCODE", new Integer(errorCode).toString());
				
				jsonMainObject2.put("STATUS",statusObject);
				
				if(headerInfo != null) {
					
					jsonMainObject2.put("HEADERINFO", headerInfo);
				}	
				
				if(redemptionInfoJsonObj != null){
					jsonMainObject2.put("REDEMPTIONINFO", redemptionInfoJsonObj);
				}
				
				JSONObject rootObject = new JSONObject();
				rootObject.put("LOYALTYREDEMPTIONRESPONSE", jsonMainObject2);
				
				logger.debug("Loyalty Redemption Response json object : "+ rootObject);
				printWriter.write(rootObject.toJSONString());
				
				printWriter.flush();
				printWriter.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::::", e);
			}
			
			
		}
		return null;
	}
	
	
}
