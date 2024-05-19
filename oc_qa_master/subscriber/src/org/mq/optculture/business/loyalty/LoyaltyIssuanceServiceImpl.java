package org.mq.optculture.business.loyalty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.SparkBaseCard;
import org.mq.marketer.campaign.beans.SparkBaseLocationDetails;
import org.mq.marketer.campaign.beans.SparkBaseTransactions;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
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
import org.mq.marketer.sparkbase.transactionWsdl.LoyaltyIssuanceResponse;
import org.mq.marketer.sparkbase.transactionWsdl.ResponseStandardHeaderComponent;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.loyalty.Balances;
import org.mq.optculture.model.loyalty.HeaderInfo;
import org.mq.optculture.model.loyalty.IssuanceInfo;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceJsonRequest;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceJsonResponse;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceResponseObject;
import org.mq.optculture.model.loyalty.StatusInfo;
import org.mq.optculture.model.loyalty.UserDetails;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceRequest;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.SBToOCJSONTranslator;
import org.mq.optculture.utils.ServiceLocator;


import com.google.gson.Gson;
/**
 * Performs loyalty issuance on a given card number with the specified amount and value code. 
 * If value code matches 'Points' then it raises a sparkbase call LoyaltyIssuance(points).
 * If value code matches 'USD' then it raises a sparkbase call GiftIssuance(USD).
 * On successful response from sparkbase call, balances(points, amount) data are updated in OptCulture database for reference. 
 *
 * @author Venkata Rathnam D
 *
 */
public class LoyaltyIssuanceServiceImpl implements LoyaltyIssuanceService{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
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
		if(serviceRequest == null || !serviceRequest.equals(OCConstants.LOYALTY_SERVICE_ACTION_ISSUANCE)){
			LoyaltyIssuanceResponseObject issuanceResponse = new LoyaltyIssuanceResponseObject();
			LoyaltyIssuanceJsonResponse jsonResponseObject = new LoyaltyIssuanceJsonResponse();

			StatusInfo statusInfo = new StatusInfo("101201", PropertyUtil.getErrorMessage(101201, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			issuanceResponse.setSTATUS(statusInfo);
			jsonResponseObject.setLOYALTYISSUANCERESPONSE(issuanceResponse);
			//Convert Object to JSON string
			String responseJson = gson.toJson(jsonResponseObject);
			BaseResponseObject responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			return responseObject;
		}

		LoyaltyIssuanceRequestObject issuanceRequest = null;
		LoyaltyTransaction transaction = null;
		
		//if(serviceRequest.equals(OCConstants.LOYALTY_SERVICE_ACTION_ISSUANCE)){
		logger.info("POS issuance request...online mode ...");
		LoyaltyIssuanceJsonRequest jsonRequestObject = null;
		try{
			jsonRequestObject = gson.fromJson(requestJson, LoyaltyIssuanceJsonRequest.class);
		}catch(Exception e){
			LoyaltyIssuanceResponseObject issuanceResponse = new LoyaltyIssuanceResponseObject();
			LoyaltyIssuanceJsonResponse jsonResponseObject = new LoyaltyIssuanceJsonResponse();

			StatusInfo statusInfo = new StatusInfo("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			issuanceResponse.setSTATUS(statusInfo);
			jsonResponseObject.setLOYALTYISSUANCERESPONSE(issuanceResponse);
			//Convert Object to JSON string
			String responseJson = gson.toJson(jsonResponseObject);
			BaseResponseObject responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			return responseObject;
		}
		issuanceRequest = jsonRequestObject.getLOYALTYISSUANCEREQ();
		
		LoyaltyIssuanceService loyaltyIssuanceService = (LoyaltyIssuanceService) ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_ISSUANCE_BUSINESS_SERVICE);
		LoyaltyIssuanceResponseObject issuanceResponse = loyaltyIssuanceService.processIssuanceRequest(issuanceRequest, 
				OCConstants.LOYALTY_ONLINE_MODE,baseRequestObject.getTransactionId(), baseRequestObject.getTransactionDate() );
		
		LoyaltyIssuanceJsonResponse jsonResponseObject = new LoyaltyIssuanceJsonResponse();
		jsonResponseObject.setLOYALTYISSUANCERESPONSE(issuanceResponse);

		//Convert Object to JSON string
		String responseJson = gson.toJson(jsonResponseObject);
		BaseResponseObject responseObject = new BaseResponseObject();
		responseObject.setAction(serviceRequest);
		responseObject.setJsonValue(responseJson);
		
		return responseObject;
	}
	/**
	 * Handles the complete process of Loyalty Issuance for either points or amount(USD).
	 * 
	 * @param issuanceRequest
	 * @return issuanceResponse
	 * @throws BaseServiceException
	 */
	@Override
	public LoyaltyIssuanceResponseObject processIssuanceRequest(
			LoyaltyIssuanceRequestObject issuanceRequest, String mode, String trxId, String trxDate) throws BaseServiceException {

		logger.info(">>>Started processIssuanceRequest method>>>");
		
		LoyaltyIssuanceResponseObject issuanceResponse = null;
		StatusInfo statusInfo = null;
		Users user = null;
		ContactsLoyalty contactsLoyalty = null;
		SparkBaseLocationDetails sparkbaseLocation = null;
		logger.info("issuanceREquest: "+issuanceRequest);
		
		IssuanceInfo issuanceInfoRequest = issuanceRequest.getISSUANCEINFO();
		issuanceInfoRequest.setTIERNAME("");
		try{
			statusInfo = validateIssuanceJsonData(issuanceRequest, mode);
			if(statusInfo != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
				issuanceResponse = prepareIssuanceResponse(issuanceRequest.getHEADERINFO(), issuanceRequest.getUSERDETAILS(), issuanceInfoRequest, null, statusInfo);
				return issuanceResponse;
			}
			
			user = getUser(issuanceRequest.getUSERDETAILS().getUSERNAME(), issuanceRequest.getUSERDETAILS().getORGANISATION(),
					issuanceRequest.getUSERDETAILS().getTOKEN());
			if(user == null){
				statusInfo = new StatusInfo("101013", PropertyUtil.getErrorMessage(101013, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				issuanceResponse = prepareIssuanceResponse(issuanceRequest.getHEADERINFO(), issuanceRequest.getUSERDETAILS(), issuanceInfoRequest, null, statusInfo);
				return issuanceResponse;
			}
			
			//need to redirect the request to OC loyalty service based on the user flag
			if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equals(user.getloyaltyServicetype())){
				
				if(issuanceRequest.getISSUANCEINFO().getCARDNUMBER() == null || 
						issuanceRequest.getISSUANCEINFO().getCARDNUMBER().isEmpty() && 
						(issuanceRequest.getISSUANCEINFO().getCUSTOMERID() != null && 
						!issuanceRequest.getISSUANCEINFO().getCUSTOMERID().isEmpty())) {
					
					
						contactsLoyalty = findLoyaltyCardInOCByCustId(issuanceRequest.getISSUANCEINFO().getCUSTOMERID(), user.getUserId());
					
						if(contactsLoyalty == null || contactsLoyalty.getCardNumber() == null){
							
							statusInfo = new StatusInfo("111595", PropertyUtil.getErrorMessage(111595, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							issuanceResponse = prepareIssuanceResponse(issuanceRequest.getHEADERINFO(), issuanceRequest.getUSERDETAILS(), issuanceInfoRequest, null, statusInfo);
							return issuanceResponse;
						}
						
						
						issuanceRequest.getISSUANCEINFO().setCARDNUMBER(contactsLoyalty.getCardNumber());
						
				}
				
				
				SBToOCJSONTranslator translator = new SBToOCJSONTranslator();
				Object requestObject = translator.convertSbReqToOC(issuanceRequest, OCConstants.LOYALTY_TRANSACTION_ISSUANCE);
				LoyaltyIssuanceRequest loyaltyIssuanceRequest = (LoyaltyIssuanceRequest)requestObject;
				LoyaltyIssuanceOCService loyaltyIssuanceService = (LoyaltyIssuanceOCService)ServiceLocator.getInstance().getServiceById(OCConstants.LOYALTY_ISSUANCE_OC_BUSINESS_SERVICE);
				org.mq.optculture.model.ocloyalty.LoyaltyIssuanceResponse responseObject = loyaltyIssuanceService.processIssuanceRequest(loyaltyIssuanceRequest, mode, trxId, trxDate,null);
				
				statusInfo = translator.convertStatus(responseObject.getStatus());
				
				issuanceInfoRequest.setTIERNAME(responseObject.getMembership().getTierName());

				if(OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
					issuanceResponse = prepareIssuanceResponse(issuanceRequest.getHEADERINFO(), issuanceRequest.getUSERDETAILS(), issuanceInfoRequest, null, statusInfo);
					//issuanceResponse.setResponseObject(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC);
					return issuanceResponse;
				}else{
					issuanceResponse = translator.convertIssuanceResponse(responseObject);
					/*issuanceResponse.setHEADERINFO(issuanceRequest.getHEADERINFO());
					issuanceResponse.setSTATUS(statusInfo);*/
					List<Balances> balances = prepareBalancesObject(responseObject);
					issuanceResponse = prepareIssuanceResponse(issuanceRequest.getHEADERINFO(), issuanceRequest.getUSERDETAILS(),
							issuanceInfoRequest, balances, statusInfo);
					//issuanceResponse.setResponseObject(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC);
					return issuanceResponse;
					
				}
				
			}
			
			statusInfo = validateSbSettings(issuanceRequest, mode, user);
			if(statusInfo != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
				issuanceResponse = prepareIssuanceResponse(issuanceRequest.getHEADERINFO(), issuanceRequest.getUSERDETAILS(), issuanceInfoRequest, null, statusInfo);
				return issuanceResponse;
			}
			
			String cardNumber = null;
			Long cardLong = null;
			if(issuanceRequest.getISSUANCEINFO().getCARDNUMBER() != null && 
					!issuanceRequest.getISSUANCEINFO().getCARDNUMBER().trim().isEmpty()){
				cardLong = OptCultureUtils.validateCardNumber(issuanceRequest.getISSUANCEINFO().getCARDNUMBER());
				if(cardLong == null){
					statusInfo = new StatusInfo("100107", PropertyUtil.getErrorMessage(100107, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					issuanceResponse = prepareIssuanceResponse(issuanceRequest.getHEADERINFO(), issuanceRequest.getUSERDETAILS(),
							issuanceInfoRequest, null, statusInfo);
					return issuanceResponse;
				}
				cardNumber = ""+cardLong;
			}
			if(cardNumber != null){
				issuanceRequest.getISSUANCEINFO().setCARDNUMBER(cardNumber);
			}
			//String cardNumber = issuanceRequest.getISSUANCEINFO().getCARDNUMBER();
			String customerId = issuanceRequest.getISSUANCEINFO().getCUSTOMERID();
			
			if(cardNumber == null || cardNumber.trim().isEmpty()){
				contactsLoyalty = findLoyaltyCardInOCByCustId(customerId, user.getUserId());
			}
			else{
				contactsLoyalty = findLoyaltyCardInOC(cardNumber, user.getUserId());
			}
			
			sparkbaseLocation = getSparkbaseLocation(user.getUserOrganization().getUserOrgId());
			
			Object object = null;
			boolean loyaltyNotFound = false;
			
			if(contactsLoyalty == null && (cardNumber == null || cardNumber.trim().isEmpty())){
				
				logger.info("contactsloyalty not found in OC and card is not given in request...");
				
				statusInfo = new StatusInfo("200013", PropertyUtil.getErrorMessage(200013, OCConstants.ERROR_LOYALTY_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				issuanceResponse = prepareIssuanceResponse(issuanceRequest.getHEADERINFO(), issuanceRequest.getUSERDETAILS(), issuanceInfoRequest, null, statusInfo);
				return issuanceResponse;
			}
			
			if(contactsLoyalty == null && cardNumber != null && cardNumber.trim().length() > 0){ //loyalty not found in OC
				logger.info("contactsloyalty not found in OC and card is given in request..."+cardNumber);
				contactsLoyalty = new ContactsLoyalty();
				contactsLoyalty.setCardNumber(cardNumber);
				contactsLoyalty.setCardPin(issuanceRequest.getISSUANCEINFO().getCARDPIN());
				loyaltyNotFound = true;
			}		
			double purchaseAmount = Double.parseDouble(issuanceRequest.getISSUANCEINFO().getENTEREDAMOUNT());
			double pointsBalBefore = contactsLoyalty.getLoyaltyBalance() == null ? 0.0 : contactsLoyalty.getLoyaltyBalance();
			double amountBalBefore = contactsLoyalty.getGiftcardBalance() == null ? 0.0 : contactsLoyalty.getGiftcardBalance();
			
				
			ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("contactsLoyaltyDaoForDML");
			object = callToSparkbaseApi(issuanceRequest.getISSUANCEINFO().getVALUECODE(), issuanceRequest.getISSUANCEINFO().getENTEREDAMOUNT(), sparkbaseLocation, contactsLoyalty);
			statusInfo = getIssuanceResponseStatus(object, issuanceRequest.getISSUANCEINFO().getVALUECODE());
			
			
			if(statusInfo != null && statusInfo.getERRORCODE().equals("0")){
				logger.info("loyalty issuance was successful... and updating...");
				List<Balances> balances = prepareBalancesObject(object);
				issuanceRequest.getISSUANCEINFO().setCARDNUMBER(contactsLoyalty.getCardNumber()+"");
				issuanceRequest.getISSUANCEINFO().setCARDPIN(contactsLoyalty.getCardPin() == null ? "" : contactsLoyalty.getCardPin());
				issuanceResponse = prepareIssuanceResponse(issuanceRequest.getHEADERINFO(), issuanceRequest.getUSERDETAILS(),
						issuanceInfoRequest, balances, statusInfo);
				//contactsLoyalty.setLifeTimePurchaseValue(contactsLoyalty.getLifeTimePurchaseValue() == null ? purchaseAmount : contactsLoyalty.getLifeTimePurchaseValue()+purchaseAmount);
				contactsLoyalty.setCummulativePurchaseValue(contactsLoyalty.getCummulativePurchaseValue() == null ? purchaseAmount : contactsLoyalty.getCummulativePurchaseValue()+purchaseAmount);
				contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
				
				if(loyaltyNotFound){
					
					logger.info(">>> Creating new loyalty card in sparkbase cards and contactloyalty....");
					contactsLoyalty.setCreatedDate(Calendar.getInstance());
					//contactsLoyalty.setCardPin(inquiryRequest.getINQUIRYINFO().getCARDPIN());
					contactsLoyalty.setUserId(user.getUserId());
					//contactsLoyalty.setOptinMedium(Constants.CONTACT_LOYALTY_TYPE_POS);
					contactsLoyalty.setContactLoyaltyType(Constants.CONTACT_LOYALTY_TYPE_POS);
					contactsLoyalty.setSourceType(issuanceRequest.getHEADERINFO().getSOURCETYPE());
					contactsLoyalty.setLocationId(sparkbaseLocation.getLocationId());
					contactsLoyalty.setPosStoreLocationId(issuanceRequest.getHEADERINFO().getSTORENUMBER());
					contactsLoyalty.setUserId(user.getUserId());
					
					//ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACT_LOYALITY_DAO);
					contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
					logger.info(">>>Created new contactsloyalty successfully");
					SparkBaseCardDao cardDao = (SparkBaseCardDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASE_CARD_DAO);
					SparkBaseCardDaoForDML cardDaoForDML = (SparkBaseCardDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SPARKBASE_CARD_DAO_FOR_DML);
					SparkBaseCard card = findSBCardInOC(cardNumber, sparkbaseLocation.getSparkBaseLocationDetails_id());
					
					if(card == null){
						logger.info(">>> Sparkbase card not found in OC...");
						card = new SparkBaseCard();
						card.setSparkBaseLocationId(sparkbaseLocation);
						card.setCardId(contactsLoyalty.getCardNumber());
						card.setCardPin(contactsLoyalty.getCardPin());
						card.setCardType(Constants.SPARKBASE_CARD_TYPE_PHYSICAL);
						card.setStatus(Constants.SPARKBASE_CARD_STATUS_ACTIVATED);
						
						//cardDao.saveOrUpdate(card);
						cardDaoForDML.saveOrUpdate(card);
						logger.info(">>>Created new sparkbasecards successfully");
					}
					else{
						if(card.getStatus().equalsIgnoreCase(Constants.SPARKBASE_CARD_STATUS_INVENTORY)){
							
							logger.info(">>> Sparkbase card found in OC... with INVENTORY STATUS...");
							card.setStatus(Constants.SPARKBASE_CARD_STATUS_ACTIVATED);
							//cardDao.saveOrUpdate(card);
							cardDaoForDML.saveOrUpdate(card);
							logger.info(">>>Updated sparkbasecard status to ACTIVATED successfully");
							
						}
					}
					
				}
				
				
				double pointsBal = contactsLoyalty.getLoyaltyBalance() == null ? 0.0 : contactsLoyalty.getLoyaltyBalance();
				double amountBal = contactsLoyalty.getGiftcardBalance() == null ? 0.0 : contactsLoyalty.getGiftcardBalance();
				
				
				String earnType = sparkbaseLocation.getEarnType();
				//String earnedValue = sparkbaseLocation.getEa
				String enteredAmtType = "";
				//statusInfo = findCardInOCRepository(inquiryRequest.getINQUIRYINFO().getCARDNUMBER(),sparkbaseLocation.getSparkBaseLocationDetails_id());
				String transactionId = null;
				String diff = null;
				
				double pointsDiffDbl = pointsBal-pointsBalBefore ;
				double amountDiffDbl = amountBal-amountBalBefore  ;
				//long earnedPoints = (long)pointsDiffDbl;
				double earnedAmount = amountDiffDbl;
				
				//calculate earnedpoints
				long earnedPoints = 0;
				purchaseAmount = Double.parseDouble(Utility.truncateUptoTwoDecimal(purchaseAmount));
				if(sparkbaseLocation.getEarnType() != null && 
						sparkbaseLocation.getEarnType().equalsIgnoreCase(OCConstants.LOYALTY_POINTS)) {
					
					if(sparkbaseLocation.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_VALUE)){
						
						Double multipleFactordbl = purchaseAmount/sparkbaseLocation.getEarnOnSpentAmount();
						long multipleFactor = multipleFactordbl.intValue();
						//double multipleFactor = multipleFactordbl;
						earnedPoints = sparkbaseLocation.getEarnValue() * multipleFactor;
					}
					else if(sparkbaseLocation.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_PERCENTAGE)){
						
						earnedPoints = (long)(sparkbaseLocation.getEarnValue() * purchaseAmount)/100;
					}
					
				}
				
				//calculate converted points
				String convertionRate = null;
				double subPoints = 0.0;
				if(sparkbaseLocation.getEarnType() != null && 
						sparkbaseLocation.getEarnType().equalsIgnoreCase(OCConstants.LOYALTY_POINTS) && 
						amountDiffDbl>0 && 
						sparkbaseLocation.getConversionType() != null && 
						sparkbaseLocation.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO) ) {
					
					double multipledouble = pointsBalBefore/sparkbaseLocation.getConvertFromPoints();
					int multiple = (int)multipledouble;
					//double convertedAmount = tier.getConvertToAmount() * multiple;
					double convertedAmount = 0.0;
					String result = Utility.truncateUptoTwoDecimal(sparkbaseLocation.getConvertToAmount() * multiple);
					if(result != null)
						convertedAmount  = Double.parseDouble(result);
					//double convertedAmount = new BigDecimal(tier.getConvertToAmount() * multiple).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
					subPoints = multiple * sparkbaseLocation.getConvertFromPoints();
					convertionRate = sparkbaseLocation.getConvertFromPoints().intValue()+" Points -> "+sparkbaseLocation.getConvertToAmount().doubleValue();
					pointsDiffDbl = earnedPoints-subPoints;
				}
				
				
				if(issuanceRequest.getISSUANCEINFO().getVALUECODE().equals(OCConstants.LOYALTY_POINTS)){
					enteredAmtType = OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE;
					transactionId = ((org.mq.marketer.sparkbase.transactionWsdl.LoyaltyIssuanceResponse) object).getIdentification().getTransactionId();
					//diff = ((org.mq.marketer.sparkbase.transactionWsdl.LoyaltyIssuanceResponse) object).getAmountRemaining();
					//earnedPoints = (long)Math.floor(Double.parseDouble(diff));
				}
				
				else {//this is gift issuance so create gift transaction
					
					transactionId = ((GiftIssuanceResponse) object).getIdentification().getTransactionId();
					enteredAmtType = OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE;
					
				}
				
				
				logger.info("  transaction id is   " + transactionId);
				if(transactionId != null && !transactionId.isEmpty()){
					
						SparkBaseTransactions sbTransc = new SparkBaseTransactions();
					
						
						
						// store number
						sbTransc.setStoreNumber(issuanceRequest.getHEADERINFO().getSTORENUMBER());
						
						// transaction id
						sbTransc.setTransactionId(transactionId);
						
						//location details
						sbTransc.setLocationId(sparkbaseLocation.getLocationId());
						
						// location name and user_id  from users & user_organization
						sbTransc.setLocationName(user.getUserOrganization().getOrganizationName());
						
						sbTransc.setUserId(user.getUserId());
						
						// card number from request json
						sbTransc.setCardId(cardNumber);
						
						sbTransc.setType(OCConstants.LOYALTY_TYPE_ISSUANCE);
						
						//entered amount from request json
						sbTransc.setAmountEntered(Double.parseDouble(issuanceRequest.getISSUANCEINFO().getENTEREDAMOUNT()));
						
						//amount remaining from response
						
						if(diff!=null && !diff.isEmpty()) {
							sbTransc.setDifference(Double.parseDouble(diff));
						}
						
						
						sbTransc.setLoyaltyBalance((double) 0);
						sbTransc.setGiftcardBalance((double) 0);
						
						for (Balances balance : balances) {
								String valueCode = balance.getVALUECODE();
								String amount = balance.getAMOUNT();
			
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
						
						
						sbTransc.setProcessedTime(Calendar.getInstance());
						
						Calendar cal1 = Calendar.getInstance();
						
						cal1.add(Calendar.MINUTE, 690);
						
						sbTransc.setServerTime(cal1);
						
						
						sbTransc.setCreatedDate(Calendar.getInstance());
						sbTransc.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
						
						SparkBaseTransactionsDao sparkbaseTransactionsDao = (SparkBaseTransactionsDao)ServiceLocator.getInstance().getDAOByName("sparkBaseTransactionsDao");
						SparkBaseTransactionsDaoForDML sparkbaseTransactionsDaoForDML = (SparkBaseTransactionsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("sparkBaseTransactionsDaoForDML");
						//sparkbaseTransactionsDao.save(sbTransc);
						sparkbaseTransactionsDaoForDML.save(sbTransc);
			
						logger.info("=================== Exit saving transaction ====================");
					}
				
				String pointsDiff = (long)pointsDiffDbl+Constants.STRING_NILL;
				String amtDiff = amountDiffDbl+Constants.STRING_NILL;
				
				createPurchaseTransaction(issuanceRequest,purchaseAmount, contactsLoyalty, earnedAmount,  earnedPoints,
						earnType,enteredAmtType, sparkbaseLocation.getUserOrganization().getUserOrgId(), pointsDiff, amtDiff , trxId+"", convertionRate, subPoints+"" );
			
			}
			else{
				
				issuanceResponse = prepareIssuanceResponse(issuanceRequest.getHEADERINFO(), issuanceRequest.getUSERDETAILS(),
						issuanceInfoRequest, null, statusInfo);
				return issuanceResponse;
			}
			

			/*if(contactsLoyalty == null){
				statusInfo = findCardInOCRepository(issuanceRequest.getISSUANCEINFO().getCARDNUMBER(),sparkbaseLocation.getSparkBaseLocationDetails_id());
				if(statusInfo != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
					issuanceResponse = prepareIssuanceResponse(issuanceRequest.getHEADERINFO(), issuanceRequest.getUSERDETAILS(), issuanceRequest.getISSUANCEINFO(), null, statusInfo);
					return issuanceResponse;
				}
				else {
					//TODO: card activated in repository without contacts loyalty.Is issuance allowed ?  
				}
			}
			//TODO:
			//check whether card is activated in sparkbase or not ??
			
			Object object = callToSparkbaseApi(issuanceRequest.getISSUANCEINFO().getVALUECODE(), issuanceRequest.getISSUANCEINFO().getENTEREDAMOUNT(), sparkbaseLocation, contactsLoyalty);
			statusInfo = getIssuanceResponseStatus(object, issuanceRequest.getISSUANCEINFO().getVALUECODE());
			List<Balances> balances = prepareBalancesObject(object);
			issuanceResponse = prepareIssuanceResponse(issuanceRequest.getHEADERINFO(), issuanceRequest.getUSERDETAILS(), issuanceRequest.getISSUANCEINFO(), balances, statusInfo);
			
			saveContactsLoyalty(contactsLoyalty, statusInfo.getSTATUS());*/
			
		}catch(Exception e){
			logger.error("Exception in loyalty issuance service", e);
			statusInfo = new StatusInfo("101000", PropertyUtil.getErrorMessage(101000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			issuanceResponse = new LoyaltyIssuanceResponseObject();
			issuanceResponse.setHEADERINFO(issuanceRequest.getHEADERINFO());
			issuanceResponse.setSTATUS(statusInfo);
			issuanceResponse.setUSERDETAILS(issuanceRequest.getUSERDETAILS());
			issuanceResponse.setISSUANCEINFO(issuanceInfoRequest);
			return issuanceResponse;
			//throw new BaseServiceException("Loyalty Issuance Request Failed");
		}
		logger.info(">>>Completed processIssuanceRequest method>>>");
		return issuanceResponse;
	}
	
	
	
	private LoyaltyTransactionChild createPurchaseTransaction(LoyaltyIssuanceRequestObject issuanceRequest, Double purchaseAmount, 
			ContactsLoyalty loyalty, double earnedAmount, double earnedPoints, 
			String earnType,	 String entAmountType, Long orgId, String ptsDiff, String amtDiff, 	String transactionId, String conversionRate, String convertedPoints){
		
		LoyaltyTransactionChild transaction = null;
		try{
			
			transaction = new LoyaltyTransactionChild();
			transaction.setTransactionId(Long.valueOf(transactionId));
			transaction.setMembershipNumber(""+loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCardSetId(loyalty.getCardSetId());
			
			//transaction.setCreatedDate(Calendar.getInstance());
			
			transaction.setCreatedDate(Calendar.getInstance());
			transaction.setAmountDifference(amtDiff);
			transaction.setPointsDifference(ptsDiff);
			transaction.setEarnType(earnType);
			if(earnType != null && earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)){
				//transaction.setEarnedPoints((double)earnedValue);
				transaction.setEarnedPoints((Double)earnedPoints);
				//transaction.setNetEarnedPoints((double)earnedValue);
			}
			else if(earnType != null && earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
				//transaction.setEarnedAmount((double)earnedValue);
				transaction.setEarnedAmount((Double)earnedAmount);
				//transaction.setNetEarnedAmount((double)earnedValue);
			}
			
			//transaction.setEarnStatus(earnStatus);
			transaction.setEnteredAmount((Double)purchaseAmount);
			//transaction.setExcludedAmount(itemExcludedAmt);
			transaction.setEnteredAmountType(entAmountType);
			transaction.setOrgId(orgId);
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setUserId(loyalty.getUserId());
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE);
			logger.debug("storeNumber is===>"+issuanceRequest.getHEADERINFO().getSTORENUMBER());               
			transaction.setStoreNumber(issuanceRequest.getHEADERINFO().getSTORENUMBER() != null && !issuanceRequest.getHEADERINFO().getSTORENUMBER().trim().isEmpty() ? issuanceRequest.getHEADERINFO().getSTORENUMBER() : null);
			transaction.setSubsidiaryNumber(issuanceRequest.getHEADERINFO().getSUBSIDIARYNUMBER() != null && !issuanceRequest.getHEADERINFO().getSUBSIDIARYNUMBER().trim().isEmpty() ? issuanceRequest.getHEADERINFO().getSUBSIDIARYNUMBER().trim() : null);
			transaction.setReceiptNumber(issuanceRequest.getISSUANCEINFO().getRECEIPTNUMBER() != null && !issuanceRequest.getISSUANCEINFO().getRECEIPTNUMBER().trim().isEmpty() ? issuanceRequest.getISSUANCEINFO().getRECEIPTNUMBER() : null);
			transaction.setEmployeeId(issuanceRequest.getISSUANCEINFO().getEMPID()!=null && !issuanceRequest.getISSUANCEINFO().getEMPID().trim().isEmpty() ? issuanceRequest.getISSUANCEINFO().getEMPID().trim():null);
			transaction.setDocSID(issuanceRequest.getISSUANCEINFO().getDOCSID());
			transaction.setDescription(conversionRate);
			transaction.setSourceType(issuanceRequest.getHEADERINFO().getSOURCETYPE());
			transaction.setContactId(loyalty.getContact() == null ? null : loyalty.getContact().getContactId());
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
			logger.debug("Issuance Transaction Id:::"+transaction.getTransChildId());
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
	private StatusInfo validateIssuanceJsonData(LoyaltyIssuanceRequestObject issuanceRequest, String mode) throws Exception{

		StatusInfo statusInfo = null;

		if(issuanceRequest == null ){
			statusInfo = new StatusInfo(
					"200001", PropertyUtil.getErrorMessage(200001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(issuanceRequest.getUSERDETAILS() == null){
			statusInfo = new StatusInfo(
					"101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(issuanceRequest.getISSUANCEINFO() == null){
			statusInfo = new StatusInfo(
					"200002", PropertyUtil.getErrorMessage(200002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(issuanceRequest.getUSERDETAILS().getUSERNAME() == null || issuanceRequest.getUSERDETAILS().getUSERNAME().trim().length() <=0 || 
				issuanceRequest.getUSERDETAILS().getORGANISATION() == null || issuanceRequest.getUSERDETAILS().getORGANISATION().trim().length() <=0 || 
				issuanceRequest.getUSERDETAILS().getTOKEN() == null || issuanceRequest.getUSERDETAILS().getTOKEN().trim().length() <=0) {
			statusInfo = new StatusInfo("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		
		/*SparkBaseLocationDetails sparkbaseLocation = getSparkbaseLocation(user.getUserOrganization().getUserOrgId());
		if(sparkbaseLocation == null) {
			statusInfo = new StatusInfo("101006", PropertyUtil.getErrorMessage(101006, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(issuanceRequest.getISSUANCEINFO().getSTORELOCATIONID() == null || issuanceRequest.getISSUANCEINFO().getSTORELOCATIONID().trim().length() <= 0
				|| !sparkbaseLocation.getLocationId().equals(issuanceRequest.getISSUANCEINFO().getSTORELOCATIONID().trim())){
			statusInfo = new StatusInfo("101404", PropertyUtil.getErrorMessage(101404, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}*/
		if((issuanceRequest.getISSUANCEINFO().getCARDNUMBER() == null || issuanceRequest.getISSUANCEINFO().getCARDNUMBER().trim().isEmpty())
				&& OCConstants.LOYALTY_ONLINE_MODE.equals(mode)){
			statusInfo = new StatusInfo("200003", PropertyUtil.getErrorMessage(200003, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if((issuanceRequest.getISSUANCEINFO().getCARDNUMBER() == null || issuanceRequest.getISSUANCEINFO().getCARDNUMBER().trim().isEmpty())
				&& (issuanceRequest.getISSUANCEINFO().getCUSTOMERID() == null || issuanceRequest.getISSUANCEINFO().getCUSTOMERID().trim().isEmpty())){
			statusInfo = new StatusInfo("200003", PropertyUtil.getErrorMessage(200003, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(issuanceRequest.getISSUANCEINFO().getVALUECODE() == null || issuanceRequest.getISSUANCEINFO().getVALUECODE().trim().isEmpty() || 
				issuanceRequest.getISSUANCEINFO().getENTEREDAMOUNT() == null || issuanceRequest.getISSUANCEINFO().getENTEREDAMOUNT().isEmpty() || 
				issuanceRequest.getISSUANCEINFO().getENTEREDAMOUNT().equals("0")){
			statusInfo = new StatusInfo("200004", PropertyUtil.getErrorMessage(200004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(!(issuanceRequest.getISSUANCEINFO().getVALUECODE().equals(OCConstants.LOYALTY_POINTS) || 
				issuanceRequest.getISSUANCEINFO().getVALUECODE().equals(OCConstants.LOYALTY_USD))){
			statusInfo = new StatusInfo("200011", PropertyUtil.getErrorMessage(200011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		return statusInfo;
	}
	
	public StatusInfo validateSbSettings(LoyaltyIssuanceRequestObject issuanceRequest, String mode, Users user) throws Exception{
		
		StatusInfo statusInfo = null;

		SparkBaseLocationDetails sparkbaseLocation = getSparkbaseLocation(user.getUserOrganization().getUserOrgId());
		if(sparkbaseLocation == null) {
			statusInfo = new StatusInfo("101006", PropertyUtil.getErrorMessage(101006, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(issuanceRequest.getISSUANCEINFO().getSTORELOCATIONID() == null || issuanceRequest.getISSUANCEINFO().getSTORELOCATIONID().trim().length() <= 0
				|| !sparkbaseLocation.getLocationId().equals(issuanceRequest.getISSUANCEINFO().getSTORELOCATIONID().trim())){
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
	private ContactsLoyalty findLoyaltyCardInOCByCustId(String customerId, Long userId) throws Exception {
		
		ContactsLoyalty contactLoyalty = null;
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		List<ContactsLoyalty> loyaltyList = contactsLoyaltyDao.getLoyaltyListByCustId(customerId, userId);
		
		if(loyaltyList != null && loyaltyList.size() > 0){
			Iterator<ContactsLoyalty> iterList = loyaltyList.iterator();
			ContactsLoyalty latestLoyalty = null;
			ContactsLoyalty iterLoyalty = null;
			while(iterList.hasNext()){
				iterLoyalty = iterList.next();
				if(latestLoyalty != null && latestLoyalty.getCreatedDate().after(iterLoyalty.getCreatedDate())){
					continue;
				}
				latestLoyalty = iterLoyalty;
			}
			
			contactLoyalty = latestLoyalty;
		}
		
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
	/**
	 * Raises issuance request call to sparkbase and gets response of success or failure.
	 * if request successful in sparkbase, it returns transaction details including balances.
	 * 
	 * @param valueCode
	 * @param enteredAmount
	 * @param sparkbaseLocation
	 * @param contactLoyalty
	 * @return Object(LoyaltyIssuanceResponse or GiftIssuanceResponse)
	 * @throws Exception
	 */
	private Object callToSparkbaseApi(String valueCode, String enteredAmount, 
			SparkBaseLocationDetails sparkbaseLocation, ContactsLoyalty contactLoyalty) throws Exception {

		logger.info(">>>Started callToSparkbaseApi method>>>");
		
		Object responseObject = null;
		String[] amount = new String[2];
		amount[0] = valueCode;
		amount[1] = enteredAmount;

		if(valueCode.equals(OCConstants.LOYALTY_POINTS)){
			responseObject = SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.LOYALTYISSUANCE, sparkbaseLocation, contactLoyalty, null, amount, true);
		}
		else if(valueCode.equals(OCConstants.LOYALTY_USD)){
			responseObject = SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.GIFTISSUANCE, sparkbaseLocation, contactLoyalty, null, amount, true);
		}
		logger.info(">>>Completed callToSparkbaseApi method>>>");
		return responseObject;
	}
	/**
	 * Saves contacts loyalty object in OptCulture database.
	 * 
	 * @param contactsLoyalty
	 * @param issuanceStatus
	 * @throws Exception
	 */
	private void saveContactsLoyalty(ContactsLoyalty contactsLoyalty, String issuanceStatus) throws Exception {
		ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
		if(OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(issuanceStatus)){
			contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
		}
	}
	/**
	 * Prepares Balances Object for JSON response.
	 * 
	 * @param sbIssuanceResponse(response returned from sparkbase)
	 * @return list of balances(Points, USD)
	 * @throws Exception
	 */
	private List<Balances> prepareBalancesObject(Object sbIssuanceResponse) throws Exception{

		List<Balances> balancesList = null;

		Balances balances = null;
		ArrayOfBalance aBalances = null;
		if(sbIssuanceResponse instanceof LoyaltyIssuanceResponse){
			aBalances = ((LoyaltyIssuanceResponse) sbIssuanceResponse).getBalances();
		}
		else if(sbIssuanceResponse instanceof GiftIssuanceResponse){
			aBalances = ((GiftIssuanceResponse) sbIssuanceResponse).getBalances();
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
	 * Prepares StatusInfo object from sparkbase issuance response object for JSON.
	 * The status contains either issuance successful or failure.
	 * This method is common for both points and gift(amount) issuance.
	 * 
	 * @param sbIssuanceResponse
	 * @param valueCode
	 * @return StatusInfo
	 * @throws Exception
	 */
	private StatusInfo getIssuanceResponseStatus(Object sbIssuanceResponse, String valueCode) throws Exception{

		LoyaltyIssuanceResponse loyaltyIssuanceResponse = null;
		GiftIssuanceResponse giftIssuanceResponse = null;
		ErrorMessageComponent errorMsg = null;
		StatusInfo statusInfo = null;

		if(sbIssuanceResponse instanceof ErrorMessageComponent) {
			errorMsg = (ErrorMessageComponent)sbIssuanceResponse;
		} else if (sbIssuanceResponse instanceof LoyaltyIssuanceResponse){
			loyaltyIssuanceResponse = (LoyaltyIssuanceResponse)sbIssuanceResponse;
			ResponseStandardHeaderComponent standardHeader = loyaltyIssuanceResponse.getStandardHeader();
			if (standardHeader.getStatus().equals("E")) {
				logger.info("Printing Error...");
				errorMsg = loyaltyIssuanceResponse.getErrorMessage();
			}
		}
		else if (sbIssuanceResponse instanceof GiftIssuanceResponse){
			giftIssuanceResponse = (GiftIssuanceResponse)sbIssuanceResponse;
			ResponseStandardHeaderComponent standardHeader = giftIssuanceResponse.getStandardHeader();
			if (standardHeader.getStatus().equals("E")) {
				logger.info("Printing Error...");
				errorMsg = giftIssuanceResponse.getErrorMessage();
			}
		}

		if(errorMsg != null){
			statusInfo = new StatusInfo("200012", PropertyUtil.getErrorMessage(200012, OCConstants.ERROR_LOYALTY_FLAG)+", "+errorMsg.getBriefMessage(),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		else{
			//if(valueCode.equals("Points")){
				statusInfo = new StatusInfo("0", "Issuance was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			//}
			/*else if(valueCode.equals("USD")){
				statusInfo = new StatusInfo("0", "Gift Issuance Successful.",
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
	
	private SparkBaseCard findSBCardInOC(String cardNumber, Long ocLocationId) throws Exception {

		SparkBaseCardDao sparkBaseCardDao = (SparkBaseCardDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASECARD_DAO);
		List<SparkBaseCard> cardList = sparkBaseCardDao.findByCardId(ocLocationId, cardNumber);

		if(cardList != null && cardList.size() > 0){
			return cardList.get(0);
		}
		return null;
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
	 * @param issuanceInfo
	 * @param balances
	 * @param statusInfo
	 * @return LoyaltyIssuanceResponseObject
	 * @throws Exception
	 */
	private LoyaltyIssuanceResponseObject prepareIssuanceResponse(HeaderInfo headerInfo, UserDetails userDetails, IssuanceInfo issuanceInfo,
			List<Balances> balances, StatusInfo statusInfo) throws Exception {
		LoyaltyIssuanceResponseObject issuanceResponse = new LoyaltyIssuanceResponseObject();
		issuanceResponse.setHEADERINFO(headerInfo);
		issuanceResponse.setISSUANCEINFO(issuanceInfo);
		issuanceResponse.setUSERDETAILS(userDetails);
		issuanceResponse.setBALANCES(balances);
		issuanceResponse.setSTATUS(statusInfo);
		return issuanceResponse;
	}
	
	/*public LoyaltyTransaction logTransactionRequest(LoyaltyIssuanceRequestObject requestObject, String jsonRequest, String mode){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransaction transaction = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			LoyaltyTransaction trans = loyaltyTransactionDao.findByRequestId(requestObject.getHEADERINFO().getREQUESTID());
			if(trans != null){
				return null;
			}
			
			transaction = new LoyaltyTransaction();
			transaction.setJsonRequest(jsonRequest);
			transaction.setRequestId(requestObject.getHEADERINFO().getREQUESTID());
			transaction.setPcFlag(Boolean.valueOf(requestObject.getHEADERINFO().getPCFLAG()));
			transaction.setMode(mode);//online or offline
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_ISSUANCE);
			transaction.setUserDetail(requestObject.getUSERDETAILS().getUSERNAME()+"__"+requestObject.getUSERDETAILS().getORGANISATION());
			loyaltyTransactionDao.saveOrUpdate(transaction);
			
		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
		}
		return transaction;
	}
	
	public void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyIssuanceResponseObject response){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			transaction.setJsonResponse(responseJson);
			transaction.setCardNumber(response.getISSUANCEINFO().getCARDNUMBER());
			loyaltyTransactionDao.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}*/
	
	
}
