package org.mq.optculture.business.loyalty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.EventTrigger;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.SparkBaseCard;
import org.mq.marketer.campaign.beans.SparkBaseLocationDetails;
import org.mq.marketer.campaign.beans.SparkBaseTransactions;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObservable;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObserver;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.EventTriggerDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.dao.SparkBaseCardDao;
import org.mq.marketer.campaign.dao.SparkBaseCardDaoForDML;
import org.mq.marketer.campaign.dao.SparkBaseLocationDetailsDao;
import org.mq.marketer.campaign.dao.SparkBaseTransactionsDao;
import org.mq.marketer.campaign.dao.SparkBaseTransactionsDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.marketer.sparkbase.SparkBaseServiceAsync;
import org.mq.marketer.sparkbase.transactionWsdl.ArrayOfBalance;
import org.mq.marketer.sparkbase.transactionWsdl.ErrorMessageComponent;
import org.mq.marketer.sparkbase.transactionWsdl.GiftIssuanceResponse;
import org.mq.marketer.sparkbase.transactionWsdl.GiftRedemptionResponse;
import org.mq.marketer.sparkbase.transactionWsdl.LoyaltyIssuanceResponse;
import org.mq.marketer.sparkbase.transactionWsdl.LoyaltyRedemptionResponse;
import org.mq.marketer.sparkbase.transactionWsdl.ResponseStandardHeaderComponent;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.loyalty.Balances;
import org.mq.optculture.model.loyalty.HeaderInfo;
import org.mq.optculture.model.loyalty.LoyaltyRedemptionJsonRequest;
import org.mq.optculture.model.loyalty.LoyaltyRedemptionJsonResponse;
import org.mq.optculture.model.loyalty.LoyaltyRedemptionRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyRedemptionResponseObject;
import org.mq.optculture.model.loyalty.RedemptionInfo;
import org.mq.optculture.model.loyalty.StatusInfo;
import org.mq.optculture.model.loyalty.UserDetails;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyRedemptionRequest;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.SBToOCJSONTranslator;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;

/**
 * Performs loyalty redemption on a given card number with the specified amount and value code. 
 * If value code matches 'Points' then it raises a sparkbase call LoyaltyRedemption(points).
 * If value code matches 'USD' then it raises a sparkbase call GiftRedemption(USD).
 * 
 * On successful response from sparkbase call, balances(points, amount) data are updated in OptCulture database for reference. 
 *
 * @author Venkata Rathnam D
 *
 */
public class LoyaltyRedemptionServiceImpl implements LoyaltyRedemptionService{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private String loyaltyExtraction;
	/**
	 * BaseService Request called by rest service controller. 
	 * @return BaseResponseObject
	 */
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		String serviceRequest = baseRequestObject.getAction();
		String requestJson = baseRequestObject.getJsonValue();
		Gson gson = new Gson();
		if(requestJson == null || serviceRequest == null || !serviceRequest.equals(OCConstants.LOYALTY_SERVICE_ACTION_REDEMPTION)){
			LoyaltyRedemptionResponseObject redemptionResponse = new LoyaltyRedemptionResponseObject();
			LoyaltyRedemptionJsonResponse jsonResponseObject = new LoyaltyRedemptionJsonResponse();
			
			StatusInfo statusInfo = new StatusInfo("101201", PropertyUtil.getErrorMessage(101201, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			redemptionResponse.setSTATUS(statusInfo);
			jsonResponseObject.setLOYALTYREDEMPTIONRESPONSE(redemptionResponse);
			
			//Convert Object to JSON string
			String responseJson = gson.toJson(jsonResponseObject);
			BaseResponseObject responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			return responseObject;
		}
		
		//Convert JSON string to Object
		//Gson gson = new Gson();
		LoyaltyRedemptionJsonRequest jsonRequestObject = null;
		try{
			jsonRequestObject = gson.fromJson(requestJson, LoyaltyRedemptionJsonRequest.class);
		}catch(Exception e){
			LoyaltyRedemptionResponseObject redemptionResponse = new LoyaltyRedemptionResponseObject();
			LoyaltyRedemptionJsonResponse jsonResponseObject = new LoyaltyRedemptionJsonResponse();
			
			StatusInfo statusInfo = new StatusInfo("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			redemptionResponse.setSTATUS(statusInfo);
			jsonResponseObject.setLOYALTYREDEMPTIONRESPONSE(redemptionResponse);
			
			//Convert Object to JSON string
			String responseJson = gson.toJson(jsonResponseObject);
			BaseResponseObject responseObject = new BaseResponseObject(); 
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			return responseObject;
		}
		LoyaltyTransaction transaction = null;
		LoyaltyRedemptionRequestObject redemptionRequest = jsonRequestObject.getLOYALTYREDEMPTIONREQ();
		String requestId = redemptionRequest.getHEADERINFO().getREQUESTID();
		String docsid = redemptionRequest.getREDEMPTIONINFO().getDOCSID();
		String userDetail = redemptionRequest.getUSERDETAILS().getUSERNAME()+"__"+redemptionRequest.getUSERDETAILS().getORGANISATION();
		String userName = redemptionRequest.getUSERDETAILS().getUSERNAME()+Constants.USER_AND_ORG_SEPARATOR+redemptionRequest.getUSERDETAILS().getORGANISATION();
		//if DOCSID not there then split the request id(the second token is meant for DOCSID).This is given for v8 plugin
		boolean isInFormat = (requestId != null && !requestId.isEmpty() && requestId.split(OCConstants.TOKEN_UNDERSCORE).length==3);
		
		String CustSID =  redemptionRequest.getREDEMPTIONINFO().getCUSTOMERID();
		
		boolean isCustSID = (isInFormat && CustSID != null && !CustSID.trim().isEmpty() && CustSID.equalsIgnoreCase(requestId.split(OCConstants.TOKEN_UNDERSCORE)[1]));
		
		if(docsid == null ||( docsid != null && docsid.trim().isEmpty()) && isInFormat && !isCustSID) {
			/*String POSVersion = PropertyUtil.getPOSVersion(userName);
			if(POSVersion != null && !POSVersion.equalsIgnoreCase(OCConstants.POSVERSION_V8)){
				logger.info("requestId ==== "+requestId+"  &&&&& "+(requestId.split(OCConstants.TOKEN_UNDERSCORE).toString()));   
				docsid = requestId.split(OCConstants.TOKEN_UNDERSCORE)[1];
				
			}*/
			docsid = requestId.split(OCConstants.TOKEN_UNDERSCORE)[1];
		}
		
		
		
		
		/*if(docsid != null && !docsid.trim().isEmpty()){
			transaction = findTransactionBy(userDetail,docsid,null);
		}*/
		//LoyaltyTransaction transaction = findTransactionByRequestId(requestId);
		logger.debug("Trasaction is ::::: " + transaction);
		if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED)){
			//String responseJson = transaction.getJsonResponse();
			//logger.debug("Transaction found and its processed already. returing the response::::: " + responseJson);
			/*BaseResponseObject responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			return responseObject;*/
			StatusInfo statusInfo = new StatusInfo("111536", PropertyUtil.getErrorMessage(111536, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			logger.debug("The error message is:"+ PropertyUtil.getErrorMessage(111536, OCConstants.ERROR_LOYALTY_FLAG));
			LoyaltyRedemptionResponseObject redemptionResponse = null;
			try{
				redemptionResponse = prepareRedemptionResponse(redemptionRequest.getHEADERINFO(), redemptionRequest.getUSERDETAILS(), redemptionRequest.getREDEMPTIONINFO(), null, statusInfo);
				String responseJson = gson.toJson(redemptionResponse);
				logger.debug("Transaction found and its processed already. returing the response::::: " + responseJson);
				BaseResponseObject responseObject = new BaseResponseObject();responseObject.setAction(serviceRequest);
				responseObject.setJsonValue(responseJson);
				return responseObject;
			}
			catch(Exception e){
				logger.error("Exception in loyalty Redemption service", e);
				statusInfo = new StatusInfo("101000", PropertyUtil.getErrorMessage(101000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				redemptionResponse = new LoyaltyRedemptionResponseObject();
				redemptionResponse.setHEADERINFO(redemptionRequest.getHEADERINFO());
				redemptionResponse.setSTATUS(statusInfo);
				redemptionResponse.setUSERDETAILS(redemptionRequest.getUSERDETAILS());
				redemptionResponse.setREDEMPTIONINFO(redemptionRequest.getREDEMPTIONINFO());
				return redemptionResponse;
				
				//throw new BaseServiceException("Loyalty Redemption Request Failed");
			}		
		}else if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW)){
			String responseJson = "{\"LOYALTYREDEMPTIONRESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101020\",\"MESSAGE\":\"Pending \"101002\": Request is been processed..\",\"STATUS\":\"Pending\"}}}";
			logger.debug("Transaction found and its not yet processed. returing the response::::: " + responseJson);
			BaseResponseObject responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			return responseObject;
		}
		if(transaction == null){
			logger.debug("Transaction not found. So, inserting a transaction::::::::");
			transaction = logTransactionRequest(redemptionRequest, requestJson, "online", docsid);
		}
		Date date = transaction.getRequestDate().getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		String transDate = df.format(date);
		
		LoyaltyRedemptionService loyaltyRedemptionService = (LoyaltyRedemptionService) ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_REDEMPTION_BUSINESS_SERVICE);
		LoyaltyRedemptionResponseObject redemptionResponse = loyaltyRedemptionService.processRedemptionRequest(redemptionRequest, OCConstants.LOYALTY_ONLINE_MODE, transaction.getId()+"", transDate,loyaltyExtraction);
		LoyaltyRedemptionJsonResponse jsonResponseObject = new LoyaltyRedemptionJsonResponse();
		jsonResponseObject.setLOYALTYREDEMPTIONRESPONSE(redemptionResponse);
		
		//Convert Object to JSON string
		String responseJson = gson.toJson(jsonResponseObject);
		BaseResponseObject responseObject = new BaseResponseObject();
		responseObject.setAction(serviceRequest);
		responseObject.setJsonValue(responseJson);
		logger.debug("::::updating the trasanction for trxn id::::::: " + transaction.getRequestId());
		//transaction.setJsonResponse(jsonResponse);
		updateTransactionStatus(transaction, responseObject.getJsonValue(), redemptionRequest);
		return responseObject;
	}
	/**
	 * Handles the complete process of Loyalty Redemption for either points or amount(USD).
	 * @return LoyaltyRedemptionResponseObject
	 */
	@Override
	public LoyaltyRedemptionResponseObject processRedemptionRequest(
			LoyaltyRedemptionRequestObject redemptionRequest, String mode, String trxID, String trxDate,String ltyExtraction)
			throws BaseServiceException {
		LoyaltyRedemptionResponseObject redemptionResponse = null;
		StatusInfo statusInfo = null;
		Users user = null;
		ContactsLoyalty contactsLoyalty = null;
		SparkBaseLocationDetails sparkbaseLocation = null;
		RedemptionInfo redemInfo = redemptionRequest.getREDEMPTIONINFO();
		redemInfo.setTIERNAME("");
		this.loyaltyExtraction=loyaltyExtraction;
		try{
			statusInfo = validateRedemptionJsonData(redemptionRequest);
			if(statusInfo != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
				redemptionResponse = prepareRedemptionResponse(redemptionRequest.getHEADERINFO(), redemptionRequest.getUSERDETAILS(), redemInfo, null, statusInfo);
				return redemptionResponse;
			}
			user = getUser(redemptionRequest.getUSERDETAILS().getUSERNAME(), redemptionRequest.getUSERDETAILS().getORGANISATION(), redemptionRequest.getUSERDETAILS().getTOKEN());
			
			
			if(user == null){
				statusInfo = new StatusInfo("101013", PropertyUtil.getErrorMessage(101013, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				redemptionResponse = prepareRedemptionResponse(redemptionRequest.getHEADERINFO(), redemptionRequest.getUSERDETAILS(), redemInfo, null, statusInfo);
				return redemptionResponse;
				
			}
			if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equals(user.getloyaltyServicetype()) && user.isIgnorePointsRedemption()){
				if(redemptionRequest.getREDEMPTIONINFO().getVALUECODE().equals(OCConstants.LOYALTY_POINTS)){
					statusInfo = new StatusInfo("0", "Redemption will be done shortly.",
							OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
					redemptionResponse = prepareRedemptionResponse(redemptionRequest.getHEADERINFO(), redemptionRequest.getUSERDETAILS(), redemInfo, null, statusInfo);
					return redemptionResponse;
				}
				
				
			}
			if(user.isEnableLoyaltyExtraction() && user.isRedemptionFromDR() && loyaltyExtraction == null) {
				statusInfo = new StatusInfo("0", "Redemption will be done shortly.",OCConstants.JSON_RESPONSE_IGNORED_MESSAGE);
				redemptionResponse = prepareRedemptionResponse(redemptionRequest.getHEADERINFO(), redemptionRequest.getUSERDETAILS(), redemInfo, null, statusInfo);
				return redemptionResponse;
			}
			//need to redirect the request to OC loyalty service based on the user flag
			if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equals(user.getloyaltyServicetype())){
				
				SBToOCJSONTranslator translator = new SBToOCJSONTranslator();
				Object requestObject = translator.convertSbReqToOC(redemptionRequest, OCConstants.LOYALTY_TRANSACTION_REDEMPTION);
				LoyaltyRedemptionRequest loyaltyRedemptionRequest = (LoyaltyRedemptionRequest)requestObject;
				LoyaltyRedemptionOCService loyaltyRedemptionService = (LoyaltyRedemptionOCService)ServiceLocator.getInstance().getServiceById(OCConstants.LOYALTY_REDEMPTION_OC_BUSINESS_SERVICE);
				org.mq.optculture.model.ocloyalty.LoyaltyRedemptionResponse responseObject = loyaltyRedemptionService.processRedemptionRequest(loyaltyRedemptionRequest, mode, trxID, trxDate,null);
				redemInfo.setTIERNAME(responseObject.getMembership().getTierName());
				if(redemptionRequest.getREDEMPTIONINFO().getCARDNUMBER() == null || redemptionRequest.getREDEMPTIONINFO().getCARDNUMBER().trim().isEmpty()) {
					
					if(responseObject.getMembership().getCardNumber()!= null && 
							!responseObject.getMembership().getCardNumber().isEmpty()){
						redemptionRequest.getREDEMPTIONINFO().setCARDNUMBER(responseObject.getMembership().getCardNumber());
					}
				}
				
				statusInfo = translator.convertStatus(responseObject.getStatus());
				List<Balances> balances = prepareBalancesObject(responseObject);
				if(OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
					redemptionResponse = prepareRedemptionResponse(redemptionRequest.getHEADERINFO(), redemptionRequest.getUSERDETAILS(),
							redemInfo, balances, statusInfo);
					return redemptionResponse;
				}else{
					//redemptionResponse = translator.convertRedemptionResponse(responseObject);
					/*issuanceResponse.setHEADERINFO(issuanceRequest.getHEADERINFO());
					issuanceResponse.setSTATUS(statusInfo);*/
					redemptionResponse = prepareRedemptionResponse(redemptionRequest.getHEADERINFO(), redemptionRequest.getUSERDETAILS(),
							redemInfo, balances, statusInfo);
					//issuanceResponse.setResponseObject(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC);
					return redemptionResponse;
					
				}
				
			}
			
			
			
			
			statusInfo = validateSBSettings(redemptionRequest, user);
			
			
			String cardNumber = null;
			Long cardLong = null;
			if(redemptionRequest.getREDEMPTIONINFO().getCARDNUMBER() != null && !redemptionRequest.getREDEMPTIONINFO().getCARDNUMBER().trim().isEmpty()){
				cardLong = OptCultureUtils.validateCardNumber(redemptionRequest.getREDEMPTIONINFO().getCARDNUMBER());
				if(cardLong == null){
					statusInfo = new StatusInfo("100107", PropertyUtil.getErrorMessage(100107, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					redemptionResponse = prepareRedemptionResponse(redemptionRequest.getHEADERINFO(), redemptionRequest.getUSERDETAILS(), redemInfo, null, statusInfo);
					return redemptionResponse;
				}
				cardNumber = ""+cardLong;
			}
			if(cardNumber != null){
				redemptionRequest.getREDEMPTIONINFO().setCARDNUMBER(cardNumber);
			}
			
			contactsLoyalty = findLoyaltyCardInOC(cardNumber, user.getUserId());
			sparkbaseLocation = getSparkbaseLocation(user.getUserOrganization().getUserOrgId());
			
			
			Object object = null;
			boolean loyaltyNotFound = false;
			
			if(contactsLoyalty == null && (cardNumber == null || cardNumber.trim().isEmpty())){
				statusInfo = new StatusInfo("200009", PropertyUtil.getErrorMessage(200009, OCConstants.ERROR_LOYALTY_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				redemptionResponse = prepareRedemptionResponse(redemptionRequest.getHEADERINFO(), redemptionRequest.getUSERDETAILS(), redemInfo, null, statusInfo);
				return redemptionResponse;
			}
			
			if(contactsLoyalty == null && cardNumber != null && cardNumber.trim().length() > 0){ //loyalty not found in OC
				
				contactsLoyalty = new ContactsLoyalty();
				contactsLoyalty.setCardNumber(cardNumber);
				contactsLoyalty.setCardPin(redemptionRequest.getREDEMPTIONINFO().getCARDPIN());
				loyaltyNotFound = true;
			}		
			double pointsBalBefore = contactsLoyalty.getLoyaltyBalance() == null ? 0.0 : contactsLoyalty.getLoyaltyBalance();
			double amountBalBefore = contactsLoyalty.getGiftcardBalance() == null ? 0.0 : contactsLoyalty.getGiftcardBalance();
			double enteredAmount = Double.parseDouble(redemptionRequest.getREDEMPTIONINFO().getENTEREDAMOUNT());
			ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
			object = callToSparkbaseApi(redemptionRequest.getREDEMPTIONINFO().getVALUECODE(), redemptionRequest.getREDEMPTIONINFO().getENTEREDAMOUNT(), sparkbaseLocation, contactsLoyalty);
			statusInfo = getRedemptionResponseStatus(object, redemptionRequest.getREDEMPTIONINFO().getVALUECODE());
				
			if(statusInfo != null && statusInfo.getERRORCODE().equals("0")){
				
				List<Balances> balances = prepareBalancesObject(object);
				redemptionRequest.getREDEMPTIONINFO().setCARDNUMBER(contactsLoyalty.getCardNumber()+"");
				redemptionRequest.getREDEMPTIONINFO().setCARDPIN(contactsLoyalty.getCardPin() == null ? "" : contactsLoyalty.getCardPin());
				
				redemptionResponse = prepareRedemptionResponse(redemptionRequest.getHEADERINFO(), redemptionRequest.getUSERDETAILS(), redemInfo, balances, statusInfo);
				
				contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
				
				if(loyaltyNotFound){
					contactsLoyalty.setCreatedDate(Calendar.getInstance());
					//contactsLoyalty.setCardPin(inquiryRequest.getINQUIRYINFO().getCARDPIN());
					contactsLoyalty.setUserId(user.getUserId());
					//contactsLoyalty.setOptinMedium(Constants.CONTACT_LOYALTY_TYPE_POS);
					contactsLoyalty.setContactLoyaltyType(Constants.CONTACT_LOYALTY_TYPE_POS);
					contactsLoyalty.setSourceType(redemptionRequest.getHEADERINFO().getSOURCETYPE());
					contactsLoyalty.setLocationId(sparkbaseLocation.getLocationId());
					contactsLoyalty.setPosStoreLocationId(redemptionRequest.getHEADERINFO().getSTORENUMBER());
					contactsLoyalty.setUserId(user.getUserId());
					
					//ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACT_LOYALITY_DAO);
					contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
					
					SparkBaseCardDao cardDao = (SparkBaseCardDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASE_CARD_DAO);
					SparkBaseCardDaoForDML cardDaoForDML = (SparkBaseCardDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SPARKBASECARD_DAO_FOR_DML);
					SparkBaseCard card = findSBCardInOC(cardNumber, sparkbaseLocation.getSparkBaseLocationDetails_id());
					
					if(card == null){
						card = new SparkBaseCard();
						card.setSparkBaseLocationId(sparkbaseLocation);
						card.setCardId(contactsLoyalty.getCardNumber());
						card.setCardPin(contactsLoyalty.getCardPin());
						card.setCardType(Constants.SPARKBASE_CARD_TYPE_PHYSICAL);
						card.setStatus(Constants.SPARKBASE_CARD_STATUS_ACTIVATED);
						
						//cardDao.saveOrUpdate(card);
						cardDaoForDML.saveOrUpdate(card);
					}
					else{
						if(card.getStatus().equalsIgnoreCase(Constants.SPARKBASE_CARD_STATUS_INVENTORY)){
							card.setStatus(Constants.SPARKBASE_CARD_STATUS_ACTIVATED);
							//cardDao.saveOrUpdate(card);
							cardDaoForDML.saveOrUpdate(card);
						}
					}
					
				}
				
				//statusInfo = findCardInOCRepository(inquiryRequest.getINQUIRYINFO().getCARDNUMBER(),sparkbaseLocation.getSparkBaseLocationDetails_id());
				
				// add code here
				
				
				String transactionId = null;
				String entAmountType = "";
				
				String giftDiff = null;
				String description = "";
				double conversionAmt = 0;
				String pointsDiff = Constants.STRING_NILL;
				String amountDiff = Constants.STRING_NILL;
				
				
				if(redemptionRequest.getREDEMPTIONINFO().getVALUECODE().equals(OCConstants.LOYALTY_POINTS)){
					
					transactionId = ((LoyaltyRedemptionResponse) object).getIdentification().getTransactionId();
					entAmountType = OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTSREDEEM;
					pointsDiff = "-"+(long)enteredAmount;
					
				}
				
				else {
					
					transactionId = ((GiftRedemptionResponse) object).getIdentification().getTransactionId();
					entAmountType = OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM;
					amountDiff = "-"+enteredAmount;
				}
				
				if(transactionId != null && !transactionId.isEmpty()){
					
					SparkBaseTransactions sbTransc = new SparkBaseTransactions();
					logger.info("=================== enter saving transaction ====================");
					
					// store number
					sbTransc.setStoreNumber(redemptionRequest.getHEADERINFO().getSTORENUMBER());
					
					// transaction id
					sbTransc.setTransactionId(transactionId);
					
					//location details
					sbTransc.setLocationId(sparkbaseLocation.getLocationId());
					
					// location name and user_id  from users & user_organization
					sbTransc.setLocationName(user.getUserOrganization().getOrganizationName());
					
					sbTransc.setUserId(user.getUserId());
					
					// card number from request json
					sbTransc.setCardId(cardNumber);
					
					sbTransc.setType(OCConstants.LOYALTY_TYPE_REDEMPTION);
					
					//entered amount from request json
					sbTransc.setAmountEntered(Double.parseDouble(redemptionRequest.getREDEMPTIONINFO().getENTEREDAMOUNT()));
					
					//amount remaining from response
					/*String diff = ((LoyaltyIssuanceResponse) object).getAmountRemaining();
					if(diff!=null && !diff.isEmpty()) {
						sbTransc.setDifference(Double.parseDouble(diff));
						logger.info("=================== difference ===================="+((LoyaltyIssuanceResponse) object).getAmountRemaining());
					}*/
					
					sbTransc.setLoyaltyBalance((double) 0);
					sbTransc.setGiftcardBalance((double) 0);
					
					for (Balances balance : balances) {
						String valueCode = balance.getVALUECODE();
						String amount = balance.getAMOUNT();

						logger.info("  Value Code = " + valueCode);
						logger.info("  Amount = " + amount);

						if(valueCode.equalsIgnoreCase("Points")) {
							sbTransc.setLoyaltyBalance(Double.parseDouble(amount));
						} 
						else  {
							sbTransc.setGiftcardBalance(Double.parseDouble(amount));
						} 

					}
					
					if(contactsLoyalty.getContact() != null){
						
						sbTransc.setContactId(contactsLoyalty.getContact().getContactId());
					}
					
					sbTransc.setStatus(Constants.SPARKBASE_TRANSACTION_STATUS_NEW);
					
					sbTransc.setProcessedTime(Calendar.getInstance());
					
					Calendar cal1 = Calendar.getInstance();
					
					cal1.add(Calendar.MINUTE, 690);
					
					sbTransc.setServerTime(cal1);
					
					
					sbTransc.setCreatedDate(Calendar.getInstance());
					
					SparkBaseTransactionsDao sparkbaseTransactionsDao = (SparkBaseTransactionsDao)ServiceLocator.getInstance().getDAOByName("sparkBaseTransactionsDao");
					SparkBaseTransactionsDaoForDML sparkbaseTransactionsDaoForDML = (SparkBaseTransactionsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("sparkBaseTransactionsDaoForDML");
					//sparkbaseTransactionsDao.save(sbTransc);
					sparkbaseTransactionsDaoForDML.save(sbTransc);
		
					logger.info("=================== Exit saving transaction ====================");
				}
				
				double pointsBal = contactsLoyalty.getLoyaltyBalance() == null ? 0.0 : contactsLoyalty.getLoyaltyBalance();
				double amountBal = contactsLoyalty.getGiftcardBalance() == null ? 0.0 : contactsLoyalty.getGiftcardBalance();
				
				double pointsDiffDbl = pointsBalBefore - pointsBal;
				double amountDiffDbl = amountBalBefore - amountBal;
				
				
				createSuccessfulTransaction(redemptionRequest, contactsLoyalty, user.getUserOrganization().getUserOrgId(), 
						Long.valueOf(trxID), entAmountType, pointsDiff, amountDiff, giftDiff, false, conversionAmt, description);
				
				
			}
			else{
				
				redemptionResponse = prepareRedemptionResponse(redemptionRequest.getHEADERINFO(), redemptionRequest.getUSERDETAILS(),
						redemInfo, null, statusInfo);
				return redemptionResponse;
			}
			
			/*if(contactsLoyalty == null){
				statusInfo = findCardInOCRepository(cardNumber,sparkbaseLocation.getSparkBaseLocationDetails_id());
				if(statusInfo != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
					redemptionResponse = prepareRedemptionResponse(redemptionRequest.getHEADERINFO(), redemptionRequest.getUSERDETAILS(), redemptionRequest.getREDEMPTIONINFO(), null, statusInfo);
					return redemptionResponse;
				}
				else {
					//TODO: card activated in repository without contacts loyalty.Is issuance allowed ?  
				}
			}
			Object object = callToSparkbaseApi(redemptionRequest.getREDEMPTIONINFO().getVALUECODE(), redemptionRequest.getREDEMPTIONINFO().getENTEREDAMOUNT(), sparkbaseLocation, contactsLoyalty);
			statusInfo = getRedemptionResponseStatus(object, redemptionRequest.getREDEMPTIONINFO().getVALUECODE());
			List<Balances> balances = prepareBalancesObject(object);
			
			redemptionResponse = prepareRedemptionResponse(redemptionRequest.getHEADERINFO(), redemptionRequest.getUSERDETAILS(), redemptionRequest.getREDEMPTIONINFO(), balances, statusInfo);
			saveContactsLoyalty(contactsLoyalty, "");*/
			
		}catch(Exception e){
			logger.error("Exception in loyalty Redemption service", e);
			statusInfo = new StatusInfo("101000", PropertyUtil.getErrorMessage(101000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			redemptionResponse = new LoyaltyRedemptionResponseObject();
			redemptionResponse.setHEADERINFO(redemptionRequest.getHEADERINFO());
			redemptionResponse.setSTATUS(statusInfo);
			redemptionResponse.setUSERDETAILS(redemptionRequest.getUSERDETAILS());
			redemptionResponse.setREDEMPTIONINFO(redemInfo);
			return redemptionResponse;
			
			//throw new BaseServiceException("Loyalty Redemption Request Failed");
		}
		return redemptionResponse;
	}
	
	
	private LoyaltyTransactionChild createSuccessfulTransaction(LoyaltyRedemptionRequestObject redemptionRequest, ContactsLoyalty loyalty,
			Long orgId, Long transactionId, String amountType, String pointsDiff, String amountDiff, String giftDiff,
			boolean withOtpCode, double conversionAmt, String description ){
		
		LoyaltyTransactionChild transaction = null;
		try{
			
			transaction = new LoyaltyTransactionChild();
			transaction.setTransactionId(transactionId);
			
			transaction.setMembershipNumber(""+loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());

			//transaction.setCreatedDate(Calendar.getInstance());
			/*if(redemptionRequest.getREDEMPTIONINFO().Membership().getCreatedDate() != null && !redemptionRequest.getMembership().getCreatedDate().trim().isEmpty()){
				String requestDate = redemptionRequest.getMembership().getCreatedDate();  
				DateFormat formatter;
				Date date;
				formatter = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
				date = (Date) formatter.parse(requestDate);
				Calendar cal = new MyCalendar(Calendar.getInstance(), null,
						MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
				cal.setTime(date);

				String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
				int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
				UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
				Users user = usersDao.findMlUser(loyalty.getUserId());
				String timezoneDiffrenceMinutes = user.getClientTimeZone();
				logger.info(timezoneDiffrenceMinutes);
				int timezoneDiffrenceMinutesInt = 0;
				if(timezoneDiffrenceMinutes != null) 
					timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
				timezoneDiffrenceMinutesInt = serverTimeZoneValInt - timezoneDiffrenceMinutesInt;
				logger.info("Client time to Server Time.."+timezoneDiffrenceMinutesInt);
				cal.add(Calendar.MINUTE,timezoneDiffrenceMinutesInt);
				logger.info("Client time to Server Time Calendar.."+cal);
				transaction.setCreatedDate(cal);
			}
			else{*/
				transaction.setCreatedDate(Calendar.getInstance());
			//}
			transaction.setPointsDifference(pointsDiff);
			transaction.setAmountDifference(amountDiff);
			transaction.setGiftDifference(giftDiff);
			
			if(redemptionRequest.getREDEMPTIONINFO().getENTEREDAMOUNT() != null && !redemptionRequest.getREDEMPTIONINFO().getENTEREDAMOUNT().trim().isEmpty()){
				//transaction.setEnteredAmount(Double.valueOf(redemptionRequest.getAmount().getEnteredValue()));
				String enteredAmount = Utility.truncateUptoTwoDecimal(Double.valueOf(redemptionRequest.getREDEMPTIONINFO().getENTEREDAMOUNT()));
				transaction.setEnteredAmount(Double.parseDouble(enteredAmount));
				transaction.setEnteredAmountType(amountType);
			}
			
			if(redemptionRequest.getREDEMPTIONINFO().getRECEIPTAMOUNT() != null && !redemptionRequest.getREDEMPTIONINFO().getRECEIPTAMOUNT().trim().isEmpty()){
				//transaction.setEnteredAmount(Double.valueOf(redemptionRequest.getAmount().getEnteredValue()));
				String receiptAmount = Utility.truncateUptoTwoDecimal(Double.valueOf(redemptionRequest.getREDEMPTIONINFO().getRECEIPTAMOUNT()));
				
				
				transaction.setReceiptAmount(Double.parseDouble(receiptAmount));
			}
			transaction.setUserId(loyalty.getUserId());
			transaction.setOrgId(orgId);
			
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setCardSetId(loyalty.getCardSetId());
			
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION);
			logger.debug("storeNumber is===>"+redemptionRequest.getHEADERINFO().getSTORENUMBER());           
			transaction.setStoreNumber(redemptionRequest.getHEADERINFO().getSTORENUMBER() != null && !redemptionRequest.getHEADERINFO().getSTORENUMBER().trim().isEmpty() 
					? redemptionRequest.getHEADERINFO().getSTORENUMBER() : null);
			transaction.setSubsidiaryNumber(redemptionRequest.getHEADERINFO().getSUBSIDIARYNUMBER() != null && !redemptionRequest.getHEADERINFO().getSUBSIDIARYNUMBER().trim().isEmpty() 
					? redemptionRequest.getHEADERINFO().getSUBSIDIARYNUMBER() : null);
			transaction.setReceiptNumber(redemptionRequest.getREDEMPTIONINFO().getRECEIPTNUMBER() != null && !redemptionRequest.getREDEMPTIONINFO().getRECEIPTNUMBER().trim().isEmpty() 
					? redemptionRequest.getREDEMPTIONINFO().getRECEIPTNUMBER() : null);
		//	transaction.setStoreNumber(redemptionRequest.getHeader().getStoreNumber());
			transaction.setEmployeeId(redemptionRequest.getREDEMPTIONINFO().getEMPID()!=null && !redemptionRequest.getREDEMPTIONINFO().getEMPID().trim().isEmpty() ? redemptionRequest.getREDEMPTIONINFO().getEMPID().trim():null);
			//transaction.setTerminalId(redemptionRequest.getHEADERINFO().getTerminalId()!=null && !redemptionRequest.getHeader().getTerminalId().trim().isEmpty() ? redemptionRequest.getHeader().getTerminalId().trim():null);
			transaction.setDocSID(redemptionRequest.getREDEMPTIONINFO().getDOCSID());
			transaction.setSourceType(redemptionRequest.getHEADERINFO().getSOURCETYPE());
			/*if(withOtpCode){
				transaction.setDescription2("Redeemed with OTP code : "+redemptionRequest.getOtpCode());
			}*/
			transaction.setDescription(description.isEmpty() ? null : description);
			transaction.setConversionAmt(conversionAmt);
			transaction.setContactId(loyalty.getContact() == null ? null : loyalty.getContact().getContactId());
//			transaction.setEventTriggStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			
			LoyaltyTransactionChildDao loyaltyTransChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			//loyaltyTransChildDao.saveOrUpdate(transaction);
			loyaltyTransChildDaoForDML.saveOrUpdate(transaction);
			
			//Event Trigger sending part
			EventTriggerEventsObservable eventTriggerEventsObservable = (EventTriggerEventsObservable) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVABLE);
			EventTriggerEventsObserver eventTriggerEventsObserver = (EventTriggerEventsObserver) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVER);
			eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
			EventTriggerDao eventTriggerDao  = (EventTriggerDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EVENT_TRIGGER_DAO);
			List<EventTrigger> etList = eventTriggerDao.findAllETByUserAndType(transaction.getUserId(),Constants.ET_TYPE_ON_LOYALTY_REDEMPTION);
			List<EventTrigger> giftEtList = eventTriggerDao.findAllETByUserAndType(transaction.getUserId(),Constants.ET_TYPE_ON_GIFT_REDEMPTION);
			if(etList != null) {
				eventTriggerEventsObservable.notifyToObserver(etList, transaction.getTransChildId(), transaction.getTransChildId(), 
																transaction.getUserId(), OCConstants.LOYALTY_REDEMPTION,Constants.ET_TYPE_ON_LOYALTY_REDEMPTION);
			}
			if(giftEtList != null) {
				eventTriggerEventsObservable.notifyToObserver(giftEtList, transaction.getTransChildId(), transaction.getTransChildId(), 
																transaction.getUserId(), OCConstants.LOYALTY_GIFT_REDEMPTION,Constants.ET_TYPE_ON_GIFT_REDEMPTION);
			}
		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
		}
		return transaction;
	}
	
	
	
	
	
	/**
	 * Validates all JSON Request parameters
	 * @param LoyaltyIssuanceRequestObject
	 * @return StatusInfo
	 * @throws Exception
	 */
	private StatusInfo validateRedemptionJsonData(LoyaltyRedemptionRequestObject redemptionRequest) throws Exception{
		
		StatusInfo statusInfo = null;
		
		if(redemptionRequest == null ){
			statusInfo = new StatusInfo(
					"200100", PropertyUtil.getErrorMessage(200100, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(redemptionRequest.getUSERDETAILS() == null){
			statusInfo = new StatusInfo(
					"101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(redemptionRequest.getREDEMPTIONINFO() == null){
			statusInfo = new StatusInfo(
					"200101", PropertyUtil.getErrorMessage(200101, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(redemptionRequest.getUSERDETAILS().getUSERNAME() == null || redemptionRequest.getUSERDETAILS().getUSERNAME().trim().length() <=0 || 
				redemptionRequest.getUSERDETAILS().getORGANISATION() == null || redemptionRequest.getUSERDETAILS().getORGANISATION().trim().length() <=0 || 
				redemptionRequest.getUSERDETAILS().getTOKEN() == null || redemptionRequest.getUSERDETAILS().getTOKEN().trim().length() <=0) {
			statusInfo = new StatusInfo("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		
		if(redemptionRequest.getREDEMPTIONINFO().getCARDNUMBER() == null || redemptionRequest.getREDEMPTIONINFO().getCARDNUMBER().trim().isEmpty()){
			statusInfo = new StatusInfo("200009", PropertyUtil.getErrorMessage(200009, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(redemptionRequest.getREDEMPTIONINFO().getVALUECODE() == null || redemptionRequest.getREDEMPTIONINFO().getVALUECODE().trim().isEmpty() || 
				redemptionRequest.getREDEMPTIONINFO().getENTEREDAMOUNT() == null || redemptionRequest.getREDEMPTIONINFO().getENTEREDAMOUNT().isEmpty() || 
						redemptionRequest.getREDEMPTIONINFO().getENTEREDAMOUNT().equals("0")){
			statusInfo = new StatusInfo("200004", PropertyUtil.getErrorMessage(200004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(!(redemptionRequest.getREDEMPTIONINFO().getVALUECODE().equals(OCConstants.LOYALTY_POINTS) || 
				redemptionRequest.getREDEMPTIONINFO().getVALUECODE().equals(OCConstants.LOYALTY_USD))){
			statusInfo = new StatusInfo("200011", PropertyUtil.getErrorMessage(200011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		return statusInfo;
	}
	
	
	public StatusInfo validateSBSettings(LoyaltyRedemptionRequestObject redemptionRequest, Users user) throws Exception{
		StatusInfo statusInfo = null;
		
		SparkBaseLocationDetails sparkbaseLocation = getSparkbaseLocation(user.getUserOrganization().getUserOrgId());
		if(sparkbaseLocation == null) {
			statusInfo = new StatusInfo("101006", PropertyUtil.getErrorMessage(101006, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(redemptionRequest.getREDEMPTIONINFO().getSTORELOCATIONID() == null || redemptionRequest.getREDEMPTIONINFO().getSTORELOCATIONID().trim().length() <= 0
				|| !sparkbaseLocation.getLocationId().equals(redemptionRequest.getREDEMPTIONINFO().getSTORELOCATIONID().trim())){
			statusInfo = new StatusInfo("101404", PropertyUtil.getErrorMessage(101404, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		return statusInfo;
	}
	
	/**
	 * Finds whether given cardNumber enrolled in OC or not
	 * 
	 * @param cardNumber
	 * @param userId
	 * @return ContactsLoyalty
	 * @throws Exception
	 */
	private ContactsLoyalty findLoyaltyCardInOC(String cardNumber, Long userId) throws Exception {
		ContactsLoyalty contactLoyalty = null;
		String parsedCardNumber = OptCultureUtils.parseCardNumber(cardNumber);
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		contactLoyalty = contactsLoyaltyDao.getContactsLoyaltyByCardId(parsedCardNumber, userId);
		return contactLoyalty;
	}
	/**
	 * Checks whether given card number exists under OptCulture sparkase location repository
	 * 
	 * @param cardNumber
	 * @param ocLocationId
	 * @return StatusInfo
	 * @throws Exception
	 */
	private StatusInfo findCardInOCRepository(String cardNumber, Long ocLocationId) throws Exception {
		
		StatusInfo statusInfo = null;
		SparkBaseCardDao sparkBaseCardDao = (SparkBaseCardDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASECARD_DAO);
		List<SparkBaseCard> cardList = sparkBaseCardDao.findByCardId(ocLocationId, cardNumber);
		
		if(cardList == null){
			statusInfo = new StatusInfo("200009", PropertyUtil.getErrorMessage(200009, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		
		if(cardList != null){
			SparkBaseCard sbCard = cardList.get(0);
			if(Constants.SPARKBASE_CARD_STATUS_INVENTORY.equals(sbCard.getStatus())){
				statusInfo = new StatusInfo("200010", PropertyUtil.getErrorMessage(200010, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
		}
		return statusInfo;
	}
	
	private SparkBaseCard findSBCardInOC(String cardNumber, Long ocLocationId) throws Exception {

		SparkBaseCardDao sparkBaseCardDao = (SparkBaseCardDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASECARD_DAO);
		List<SparkBaseCard> cardList = sparkBaseCardDao.findByCardId(ocLocationId, cardNumber);

		if(cardList != null && cardList.size() > 0){
			return cardList.get(0);
		}
		return null;
	}
	
	
	/**
	 * Raises redemption request call to sparkbase and gets response of success or failure.
	 * if request successful in sparkbase, it returns transaction details including balances.
	 * 
	 * @param valueCode
	 * @param enteredAmount
	 * @param sparkbaseLocation
	 * @param contactLoyalty
	 * @return Object(LoyaltyRedemptionResponse or GiftRedemptionResponse)
	 * @throws Exception
	 */
	private Object callToSparkbaseApi(String valueCode, String enteredAmount, 
			SparkBaseLocationDetails sparkbaseLocation, ContactsLoyalty contactLoyalty) throws Exception {
		
		Object responseObject = null;
		String[] amount = new String[2];
		amount[0] = valueCode;
		amount[1] = enteredAmount;
		
		if(valueCode.equals(OCConstants.LOYALTY_POINTS)){
			responseObject = SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.LOYALTYREDEMPTION, sparkbaseLocation, contactLoyalty, null, amount, true);
		}
		else if(valueCode.equals(OCConstants.LOYALTY_USD)){
			responseObject = SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.GIFTREDEMPTION, sparkbaseLocation, contactLoyalty, null, amount, true);
		}
		return responseObject;
	}
	/**
	 * Saves contacts loyalty object in OptCulture database.
	 * 
	 * @param contactsLoyalty
	 * @param redemptionStatus
	 * @throws Exception
	 */
	private void saveContactsLoyalty(ContactsLoyalty contactsLoyalty, String redemptionStatus) throws Exception {
		ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
		contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
	}
	/**
	 * Prepares Balances Object for JSON response.
	 * 
	 * @param sbRedemptionResponse(response returned from sparkbase)
	 * @return list of balances(Points, USD)
	 * @throws Exception
	 */
	private List<Balances> prepareBalancesObject(Object redemptionResponse) throws Exception{
		
		List<Balances> balancesList = null;
		
		Balances balances = null;
		ArrayOfBalance aBalances = null;
		String valueCode = null;
		if(redemptionResponse instanceof LoyaltyRedemptionResponse){
			aBalances = ((LoyaltyRedemptionResponse) redemptionResponse).getBalances();
		}
		else if(redemptionResponse instanceof GiftRedemptionResponse){
			aBalances = ((GiftRedemptionResponse) redemptionResponse).getBalances();
		}else if(redemptionResponse instanceof org.mq.optculture.model.ocloyalty.LoyaltyRedemptionResponse) {
			

			List<Balance> ocGivenBals = ((org.mq.optculture.model.ocloyalty.LoyaltyRedemptionResponse)redemptionResponse).getBalances();
			balancesList = new ArrayList<Balances>();
			for(int i = 0; i < ocGivenBals.size(); i++) {
				balances = new Balances();
				valueCode = ocGivenBals.get(i).getValueCode();
				if(valueCode!= null && !valueCode.trim().isEmpty()) {
					if(valueCode.equals(OCConstants.LOYALTY_TYPE_CURRENCY)) {
						balances.setVALUECODE(OCConstants.LOYALTY_USD );
					}else if(valueCode.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {
						
						balances.setVALUECODE(OCConstants.LOYALTY_POINTS );
					}
				}
				balances.setAMOUNT(ocGivenBals.get(i).getAmount());
				balances.setDIFFERENCE(ocGivenBals.get(i).getDifference());
				balances.setEXCHANGERATE("");
				balancesList.add(balances);
			}
		
		}
		
		if(aBalances != null && aBalances.getBalance() != null){
			balancesList = new ArrayList<Balances>();
			for(int i = 0; i < aBalances.getBalance().size(); i++) {
				balances = new Balances();
				balances.setVALUECODE(aBalances.getBalance().get(i).getValueCode());
				balances.setAMOUNT(aBalances.getBalance().get(i).getAmount());
				balances.setDIFFERENCE(aBalances.getBalance().get(i).getDifference());
				balances.setEXCHANGERATE(aBalances.getBalance().get(i).getExchangeRate());
				balancesList.add(balances);
			}
		}
		return balancesList;
	}
	/**
	 * Prepares StatusInfo object from sparkbase redemption response object for JSON.
	 * The status contains either redemption successful or failure.
	 * This method is common for both points and gift(amount) redemption.
	 * 
	 * @param sbRedemptionResponse
	 * @param valueCode
	 * @return StatusInfo
	 * @throws Exception
	 */
	private StatusInfo getRedemptionResponseStatus(Object sbRedemptionResponse, String valueCode) throws Exception{

		LoyaltyRedemptionResponse loyaltyRedemptionResponse = null;
		GiftRedemptionResponse giftRedemptionResponse = null;
		ErrorMessageComponent errorMsg = null;
		StatusInfo statusInfo = null;
		
		if(sbRedemptionResponse instanceof ErrorMessageComponent) {
			errorMsg = (ErrorMessageComponent)sbRedemptionResponse;
		} else if (sbRedemptionResponse instanceof LoyaltyIssuanceResponse){
			loyaltyRedemptionResponse = (LoyaltyRedemptionResponse)sbRedemptionResponse;
			ResponseStandardHeaderComponent standardHeader = loyaltyRedemptionResponse.getStandardHeader();
			if (standardHeader.getStatus().equals("E")) {
				  logger.info("Printing Error...");
		          errorMsg = loyaltyRedemptionResponse.getErrorMessage();
		    }
		}
		else if (sbRedemptionResponse instanceof GiftRedemptionResponse){
			giftRedemptionResponse = (GiftRedemptionResponse)sbRedemptionResponse;
			ResponseStandardHeaderComponent standardHeader = giftRedemptionResponse.getStandardHeader();
			if (standardHeader.getStatus().equals("E")) {
				  logger.info("Printing Error...");
		          errorMsg = giftRedemptionResponse.getErrorMessage();
		    }
		}
		
		if(errorMsg != null){
			statusInfo = new StatusInfo("200102", PropertyUtil.getErrorMessage(200102, OCConstants.ERROR_LOYALTY_FLAG)+", "+errorMsg.getBriefMessage(),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		else{
			//if(valueCode.equals("Points")){
				statusInfo = new StatusInfo("0", "Successfully redeemed.",
						OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			//}
			/*else if(valueCode.equals("USD")){
				statusInfo = new StatusInfo("0", "Gift Redemption Successful.",
						OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			}*/
			return statusInfo;
		}
	}
	/**
	 * Fetches Users object from OC database
	 * 
	 * @param userName
	 * @param orgId
	 * @param userToken
	 * @return Users
	 * @throws Exception
	 */
	private Users getUser(String userName, String orgId, String userToken) throws Exception{
		
		String completeUserName = userName+Constants.USER_AND_ORG_SEPARATOR+orgId;
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Users user = usersDao.findByToken(completeUserName, userToken);
		return user;
	}
	/**
	 * Finds sparkbase location settings object in OptCulture
	 * 
	 * @param userOrgId
	 * @return SparkBaseLocation
	 * @throws Exception
	 */
	private SparkBaseLocationDetails getSparkbaseLocation(Long userOrgId) throws Exception{
		SparkBaseLocationDetailsDao sparkBaseLocationDetailsDao = (SparkBaseLocationDetailsDao)
				ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASE_LOCATIONDETAILS_DAO);
		List<SparkBaseLocationDetails> sbDetailsList = sparkBaseLocationDetailsDao.findActiveSBLocByOrgId(userOrgId);
		SparkBaseLocationDetails sparkbaseLocation = (sbDetailsList == null) ? null : sbDetailsList.get(0);
		return sparkbaseLocation;
		
	}
	/**
	 * Prepares final JSON Response Object
	 * 
	 * @param headerInfo
	 * @param userDetails
	 * @param redemptionInfo
	 * @param balances
	 * @param statusInfo
	 * @return LoyaltyRedemptionResponseObject
	 * @throws Exception
	 */
	private LoyaltyRedemptionResponseObject prepareRedemptionResponse(HeaderInfo headerInfo, UserDetails userDetails, RedemptionInfo redemptionInfo,
								List<Balances> balances, StatusInfo statusInfo) throws Exception {
		LoyaltyRedemptionResponseObject redemptionResponse = new LoyaltyRedemptionResponseObject();
		redemptionResponse.setHEADERINFO(headerInfo);
		redemptionResponse.setREDEMPTIONINFO(redemptionInfo);
		redemptionResponse.setUSERDETAILS(userDetails);
		redemptionResponse.setBALANCES(balances);
		redemptionResponse.setSTATUS(statusInfo);
		return redemptionResponse;
	}
	
	/*public LoyaltyTransaction findTransactionByRequestId(String requestId){
		LoyaltyTransaction transaction = null;
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			transaction = loyaltyTransactionDao.findByRequestIdAndType(requestId, OCConstants.LOYALTY_TRANSACTION_REDEMPTION);
		}catch(Exception e){
			logger.error("Exception in find transaction by requestid", e);
		}
		return transaction;
	}*/
	
	public LoyaltyTransaction findTransactionBy(String userDetail, String docsid, String requestId){
		LoyaltyTransaction transaction = null;
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			transaction = loyaltyTransactionDao.findByDocSIdRequestIdAndType(userDetail, requestId, docsid, OCConstants.LOYALTY_TRANSACTION_REDEMPTION);
		}catch(Exception e){
			logger.error("Exception in find transaction by requestid", e);
		}
		return transaction;
	}
	
	public LoyaltyTransaction logTransactionRequest(LoyaltyRedemptionRequestObject requestObject, String jsonRequest, String mode, String docSid){
		logger.debug("Entered logTransactionRequest ::: ");
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		LoyaltyTransaction transaction = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
						
			transaction = new LoyaltyTransaction();
			transaction.setJsonRequest(jsonRequest);
			transaction.setStoreNumber(requestObject.getHEADERINFO().getSTORENUMBER());
			transaction.setRequestId(requestObject.getHEADERINFO().getREQUESTID());
			transaction.setPcFlag(Boolean.valueOf(requestObject.getHEADERINFO().getPCFLAG()));
			transaction.setMode(mode);//online or offline
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_REDEMPTION);
			transaction.setUserDetail(requestObject.getUSERDETAILS().getUSERNAME()+"__"+requestObject.getUSERDETAILS().getORGANISATION());
			transaction.setDocSID(docSid != null && 
					!docSid.trim().isEmpty() ? docSid.trim() : null);
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
			logger.debug("Exited logTransactionRequest ::: ");
		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
		}
		return transaction;
	}
	
	public void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyRedemptionRequestObject request){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
if(responseJson != null ){
				
				try {
					LoyaltyRedemptionJsonResponse jsonResponseObject = new Gson().fromJson(responseJson, LoyaltyRedemptionJsonResponse.class);
					transaction.setRequestStatus(jsonResponseObject.getLOYALTYREDEMPTIONRESPONSE().getSTATUS().getSTATUS());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ", e);
				}
			}
			 Users user= getUser(request.getUSERDETAILS().getUSERNAME(), request.getUSERDETAILS().getORGANISATION(),
					 request.getUSERDETAILS().getTOKEN());
			 if(Utility.countryCurrencyMap.get(user.getCountryType()) != null) {
				 
				 responseJson = responseJson.replace(Utility.countryCurrencyMap.get(user.getCountryType()), "");
			 }
			transaction.setJsonResponse(responseJson);
			if(request.getREDEMPTIONINFO() != null)
				transaction.setCardNumber(request.getREDEMPTIONINFO().getCARDNUMBER());
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}
}
