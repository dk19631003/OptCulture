package org.mq.optculture.business.loyalty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.SparkBaseCard;
import org.mq.marketer.campaign.beans.SparkBaseLocationDetails;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.SparkBaseCardDao;
import org.mq.marketer.campaign.dao.SparkBaseCardDaoForDML;
import org.mq.marketer.campaign.dao.SparkBaseLocationDetailsDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.sparkbase.SparkBaseServiceAsync;
import org.mq.marketer.sparkbase.transactionWsdl.ArrayOfBalance;
import org.mq.marketer.sparkbase.transactionWsdl.CustomerInfoComponent;
import org.mq.marketer.sparkbase.transactionWsdl.ErrorMessageComponent;
import org.mq.marketer.sparkbase.transactionWsdl.InquiryResponse;
import org.mq.marketer.sparkbase.transactionWsdl.ResponseStandardHeaderComponent;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.loyalty.Balances;
import org.mq.optculture.model.loyalty.CustomerInfo;
import org.mq.optculture.model.loyalty.HeaderInfo;
import org.mq.optculture.model.loyalty.InquiryInfo;
import org.mq.optculture.model.loyalty.LoyaltyInquiryJsonRequest;
import org.mq.optculture.model.loyalty.LoyaltyInquiryJsonResponse;
import org.mq.optculture.model.loyalty.LoyaltyInquiryRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyInquiryResponseObject;
import org.mq.optculture.model.loyalty.StatusInfo;
import org.mq.optculture.model.loyalty.UserDetails;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;


import com.google.gson.Gson;

/**
 * It finds given card in OptCulture and Sparkbase to know whether card is activated or inventory.
 *
 * @author Venkata Rathnam D
 *
 */
public class LoyaltyInquiryServiceImpl implements LoyaltyInquiryService {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	/**
	 * BaseService Request called by rest service controller.
	 * @return BaseResponseObject
	 */
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		Gson gson = new Gson();
		String serviceRequest = baseRequestObject.getAction();
		String requestJson = baseRequestObject.getJsonValue();
		BaseResponseObject responseObject = null;
		if(serviceRequest == null || !OCConstants.LOYALTY_SERVICE_ACTION_INQUIRY.equals(serviceRequest)){
			StatusInfo statusInfo = new StatusInfo("101201", PropertyUtil.getErrorMessage(101201, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			LoyaltyInquiryResponseObject inquiryResponse = new LoyaltyInquiryResponseObject();
			inquiryResponse.setSTATUS(statusInfo);
			LoyaltyInquiryJsonResponse jsonResponseObject = new LoyaltyInquiryJsonResponse();
			jsonResponseObject.setLOYALTYINQUIRYRESPONSE(inquiryResponse);
			String responseJson = gson.toJson(jsonResponseObject);
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			return responseObject;
		}
		
		//Convert JSON string to Object
		//Gson gson = new Gson();
		LoyaltyInquiryJsonRequest jsonRequestObject = null;
		try{
			jsonRequestObject = gson.fromJson(requestJson, LoyaltyInquiryJsonRequest.class);
		}catch(Exception e){
			StatusInfo statusInfo = new StatusInfo("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			LoyaltyInquiryResponseObject inquiryResponse = new LoyaltyInquiryResponseObject();
			inquiryResponse.setSTATUS(statusInfo);
			LoyaltyInquiryJsonResponse jsonResponseObject = new LoyaltyInquiryJsonResponse();
			jsonResponseObject.setLOYALTYINQUIRYRESPONSE(inquiryResponse);
			String responseJson = gson.toJson(jsonResponseObject);
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			return responseObject;
		}
		LoyaltyInquiryRequestObject inquiryRequest = jsonRequestObject.getLOYALTYINQUIRYREQ();
		LoyaltyInquiryService loyaltyInquiryService = (LoyaltyInquiryService) ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_INQUIRY_BUSINESS_SERVICE);
		LoyaltyInquiryResponseObject inquiryResponse = loyaltyInquiryService.processInquiryRequest(inquiryRequest);
		LoyaltyInquiryJsonResponse jsonResponseObject = new LoyaltyInquiryJsonResponse();
		jsonResponseObject.setLOYALTYINQUIRYRESPONSE(inquiryResponse);
		
		//Convert Object to JSON string
		String responseJson = gson.toJson(jsonResponseObject);
		responseObject = new BaseResponseObject();
		responseObject.setAction(serviceRequest);
		responseObject.setJsonValue(responseJson);
		return responseObject;
	}
	/**
	 * Handles the complete process of Loyalty Inquiry.
	 * 
	 * @param inquiryRequest
	 * @return inquiryResponse
	 * @throws BaseServiceException
	 */
	@Override
	public LoyaltyInquiryResponseObject processInquiryRequest(LoyaltyInquiryRequestObject inquiryRequest) throws BaseServiceException {
		
		logger.info("started loyalty inquiry with requestid : "+inquiryRequest.getHEADERINFO().getREQUESTID());
		LoyaltyInquiryResponseObject inquiryResponse = null;
		StatusInfo statusInfo = null;
		CustomerInfo customerInfo= null;
		Users user = null;
		ContactsLoyalty contactsLoyalty = null;
		SparkBaseLocationDetails sparkbaseLocation = null;
		try{
			
			statusInfo = validateInquiryJsonData(inquiryRequest);
			if(statusInfo != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
				inquiryResponse = prepareInquiryResponse(inquiryRequest.getHEADERINFO(), inquiryRequest.getUSERDETAILS(),
						inquiryRequest.getINQUIRYINFO(), null, statusInfo, null);
				return inquiryResponse;
			}
			
			user = getUser(inquiryRequest.getUSERDETAILS().getUSERNAME(), inquiryRequest.getUSERDETAILS().getORGANISATION(), inquiryRequest.getUSERDETAILS().getTOKEN());
			statusInfo = validateUserSparkbaseSettings(user, inquiryRequest.getUSERDETAILS().getTOKEN(), inquiryRequest.getINQUIRYINFO().getSTORELOCATIONID());
			if(statusInfo != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
				inquiryResponse = prepareInquiryResponse(inquiryRequest.getHEADERINFO(), inquiryRequest.getUSERDETAILS(),
						inquiryRequest.getINQUIRYINFO(), null, statusInfo, null);
				return inquiryResponse;
			}
			
			
			String cardNumber = null;
			Long cardLong = null;
			if(inquiryRequest.getINQUIRYINFO().getCARDNUMBER() != null && !inquiryRequest.getINQUIRYINFO().getCARDNUMBER().trim().isEmpty()){
				cardLong = OptCultureUtils.validateCardNumber(inquiryRequest.getINQUIRYINFO().getCARDNUMBER());
				if(cardLong == null){
					statusInfo = new StatusInfo("100107", PropertyUtil.getErrorMessage(100107, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					inquiryResponse = prepareInquiryResponse(inquiryRequest.getHEADERINFO(), inquiryRequest.getUSERDETAILS(),
							inquiryRequest.getINQUIRYINFO(), null, statusInfo, null);
					return inquiryResponse;
				}
				cardNumber = ""+cardLong;
			}
			if(cardNumber != null){
				inquiryRequest.getINQUIRYINFO().setCARDNUMBER(cardNumber);
			}
			//String cardNumber = inquiryRequest.getINQUIRYINFO().getCARDNUMBER();
			String customerId = inquiryRequest.getINQUIRYINFO().getCUSTOMERID();
			
			if(cardNumber == null || cardNumber.trim().isEmpty()){
				contactsLoyalty = findLoyaltyCardInOCByCustId(customerId, user.getUserId());
			}
			else{
				contactsLoyalty = findLoyaltyCardInOC(cardNumber, user.getUserId());
			}
			
			//contactsLoyalty = findLoyaltyCardInOC(inquiryRequest.getINQUIRYINFO().getCARDNUMBER(), user.getUserId());
			sparkbaseLocation = getSparkbaseLocation(user.getUserOrganization().getUserOrgId());
			
			//inquiry loyalty card in sparkbase
			Object object = null;
			boolean loyaltyNotFound = false;
			
			if(contactsLoyalty == null && (cardNumber == null || cardNumber.trim().isEmpty())){
				statusInfo = new StatusInfo("200013", PropertyUtil.getErrorMessage(200013, OCConstants.ERROR_LOYALTY_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				inquiryResponse = prepareInquiryResponse(inquiryRequest.getHEADERINFO(), inquiryRequest.getUSERDETAILS(),
						inquiryRequest.getINQUIRYINFO(), null, statusInfo, null);
				return inquiryResponse;
			}
			
			if(contactsLoyalty == null && cardNumber != null && cardNumber.trim().length() > 0){ //loyalty not found in OC
				
				contactsLoyalty = new ContactsLoyalty();
				contactsLoyalty.setCardNumber(cardNumber);
				contactsLoyalty.setCardPin(inquiryRequest.getINQUIRYINFO().getCARDPIN());
				loyaltyNotFound = true;
			}		
				
			ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("contactsLoyaltyDaoForDML");
			object = callToSparkbaseApi(sparkbaseLocation, contactsLoyalty);
			statusInfo = getInquiryResponseStatus(object);
				
			if(statusInfo != null && statusInfo.getERRORCODE().equals("0")){
				
				customerInfo = getInquiryResponseCustomerData(object);
				customerInfo.setCUSTOMERID(contactsLoyalty.getCustomerId() == null ? "" : contactsLoyalty.getCustomerId());
				List<Balances> balances = prepareBalancesObject(object);
				inquiryRequest.getINQUIRYINFO().setCARDNUMBER(contactsLoyalty.getCardNumber()+"");
				inquiryRequest.getINQUIRYINFO().setCARDPIN(contactsLoyalty.getCardPin() == null ? "" : contactsLoyalty.getCardPin());
				inquiryResponse = prepareInquiryResponse(inquiryRequest.getHEADERINFO(), inquiryRequest.getUSERDETAILS(),
						inquiryRequest.getINQUIRYINFO(), balances, statusInfo, customerInfo);
				
				contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
				
				if(loyaltyNotFound){
					contactsLoyalty.setCreatedDate(Calendar.getInstance());
					//contactsLoyalty.setCardPin(inquiryRequest.getINQUIRYINFO().getCARDPIN());
					contactsLoyalty.setUserId(user.getUserId());
//					contactsLoyalty.setOptinMedium(Constants.CONTACT_LOYALTY_TYPE_POS);
					contactsLoyalty.setContactLoyaltyType(Constants.CONTACT_LOYALTY_TYPE_POS);
					contactsLoyalty.setSourceType(inquiryRequest.getHEADERINFO().getSOURCETYPE());
					contactsLoyalty.setLocationId(sparkbaseLocation.getLocationId());
					contactsLoyalty.setPosStoreLocationId(inquiryRequest.getHEADERINFO().getSTORENUMBER());
					contactsLoyalty.setUserId(user.getUserId());
					
					//ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACT_LOYALITY_DAO);
					contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
					
					SparkBaseCardDao cardDao = (SparkBaseCardDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASE_CARD_DAO);
					SparkBaseCardDaoForDML cardDaoForDML = (SparkBaseCardDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SPARKBASE_CARD_DAO_FOR_DML);
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
			}
			else{
				
				inquiryResponse = prepareInquiryResponse(inquiryRequest.getHEADERINFO(), inquiryRequest.getUSERDETAILS(),
						inquiryRequest.getINQUIRYINFO(), null, statusInfo, null);
				return inquiryResponse;
			}
			
			/*if(contactsLoyalty == null && cardNumber != null && cardNumber.trim().length() > 0){
				statusInfo = findCardInOCRepository(inquiryRequest.getINQUIRYINFO().getCARDNUMBER(),sparkbaseLocation.getSparkBaseLocationDetails_id());
				if(statusInfo != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
					inquiryResponse = prepareInquiryResponse(inquiryRequest.getHEADERINFO(), inquiryRequest.getUSERDETAILS(),
							inquiryRequest.getINQUIRYINFO(), null, statusInfo, null);
					return inquiryResponse;
				}
				else {
					//TODO: card activated in repository without contacts loyalty.Is issuance allowed ?  
				}
			}
			else if(contactsLoyalty == null && customerId != null && customerId.length() > 0){
				statusInfo = new StatusInfo("200009", PropertyUtil.getErrorMessage(200009, OCConstants.ERROR_LOYALTY_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				inquiryResponse = prepareInquiryResponse(inquiryRequest.getHEADERINFO(), inquiryRequest.getUSERDETAILS(),
						inquiryRequest.getINQUIRYINFO(), null, statusInfo, null);
				return inquiryResponse;
			}*/
			
			//Object object = callToSparkbaseApi(sparkbaseLocation, contactsLoyalty);
			//statusInfo = getInquiryResponseStatus(object);
			//customerInfo = getInquiryResponseCustomerData(object);
			//List<Balances> balances = prepareBalancesObject(object);
			//inquiryRequest.getINQUIRYINFO().setCARDNUMBER(contactsLoyalty.getCardNumber()+"");
			//inquiryRequest.getINQUIRYINFO().setCARDPIN(contactsLoyalty.getCardPin() == null ? "" : contactsLoyalty.getCardPin());
			//inquiryResponse = prepareInquiryResponse(inquiryRequest.getHEADERINFO(), inquiryRequest.getUSERDETAILS(),
			//		inquiryRequest.getINQUIRYINFO(), balances, statusInfo, customerInfo);
		}catch(Exception e){
			logger.error("Exception in loyalty inquiry service", e);
			statusInfo = new StatusInfo("200201", PropertyUtil.getErrorMessage(200201, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			inquiryResponse = new LoyaltyInquiryResponseObject();
			inquiryResponse.setHEADERINFO(inquiryRequest.getHEADERINFO());
			inquiryResponse.setSTATUS(statusInfo);
			inquiryResponse.setUSERDETAILS(inquiryRequest.getUSERDETAILS());
			inquiryResponse.setINQUIRYINFO(inquiryRequest.getINQUIRYINFO());
			return inquiryResponse;
			//throw new BaseServiceException("Loyalty Inquiry Request Failed");
		}
		logger.info("Completed loyalty inquiry with requestid : "+inquiryRequest.getHEADERINFO().getREQUESTID());
		return inquiryResponse;
	}
	/**
	 * Prepares StatusInfo object from sparkbase inquiry response object for JSON.
	 * The status contains either inquiry successful or failure.
	 * 
	 * @param sbInquiryResponse
	 * @return StatusInfo
	 * @throws Exception
	 */
	private StatusInfo getInquiryResponseStatus(Object sbInquiryResponse) throws Exception{

		InquiryResponse inquiryResponse = null;
		ErrorMessageComponent errorMsg = null;
		StatusInfo statusInfo = null;

		if(sbInquiryResponse instanceof ErrorMessageComponent) {
			errorMsg = (ErrorMessageComponent)sbInquiryResponse;
		} else if (sbInquiryResponse instanceof InquiryResponse){
			inquiryResponse = (InquiryResponse)sbInquiryResponse;
			ResponseStandardHeaderComponent standardHeader = inquiryResponse.getStandardHeader();
			if (standardHeader.getStatus().equals("E")) {
				logger.info("Printing Error...");
				errorMsg = inquiryResponse.getErrorMessage();
			}
		}
		if(errorMsg != null){
			statusInfo = new StatusInfo("200201", PropertyUtil.getErrorMessage(200201, OCConstants.ERROR_LOYALTY_FLAG)+", "+errorMsg.getBriefMessage(),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		else{
			statusInfo = new StatusInfo("0", "Inquiry was successful.",
						OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			return statusInfo;
		}
	}
	/**
	 * Validates existence of JSON objects in the request, such as userdetails, inquiryinfo. 
	 * 
	 * @param inquiryRequest
	 * @return StatusInfo
	 * @throws Exception
	 */
	private StatusInfo validateInquiryJsonData(LoyaltyInquiryRequestObject inquiryRequest) throws Exception{
		StatusInfo statusInfo = null;
		if(inquiryRequest == null ){
			statusInfo = new StatusInfo(
					"200301", PropertyUtil.getErrorMessage(200301, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(inquiryRequest.getUSERDETAILS() == null){
			statusInfo = new StatusInfo(
					"101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(inquiryRequest.getINQUIRYINFO() == null){
			statusInfo = new StatusInfo(
					"200302", PropertyUtil.getErrorMessage(200302, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(inquiryRequest.getUSERDETAILS().getUSERNAME() == null || inquiryRequest.getUSERDETAILS().getUSERNAME().trim().length() <=0 || 
				inquiryRequest.getUSERDETAILS().getORGANISATION() == null || inquiryRequest.getUSERDETAILS().getORGANISATION().trim().length() <=0 || 
				inquiryRequest.getUSERDETAILS().getTOKEN() == null || inquiryRequest.getUSERDETAILS().getTOKEN().trim().length() <=0) {
			statusInfo = new StatusInfo("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if((inquiryRequest.getINQUIRYINFO().getCARDNUMBER() == null || inquiryRequest.getINQUIRYINFO().getCARDNUMBER().trim().isEmpty())
				&& (inquiryRequest.getINQUIRYINFO().getCUSTOMERID() == null || inquiryRequest.getINQUIRYINFO().getCUSTOMERID().trim().isEmpty())){
			statusInfo = new StatusInfo("200003", PropertyUtil.getErrorMessage(200003, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		return statusInfo;
	}
	/**
	 * Validates JSON request user parameters to check whether sparkbase location settings are created in OptCulture or not.
	 * 
	 * @return StatusInfo
	 * @throws Exception
	 */
	private StatusInfo validateUserSparkbaseSettings(Users user, String userToken, String storeLocationId) throws Exception {
		
		StatusInfo statusInfo = null;
		if(user == null){
			statusInfo = new StatusInfo("101013", PropertyUtil.getErrorMessage(101013, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		SparkBaseLocationDetails sparkbaseLocation = getSparkbaseLocation(user.getUserOrganization().getUserOrgId());
		if(sparkbaseLocation == null) {
			statusInfo = new StatusInfo("101006", PropertyUtil.getErrorMessage(101006, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(storeLocationId == null || storeLocationId.trim().length() <= 0 || !sparkbaseLocation.getLocationId().equals(storeLocationId.trim())){
			statusInfo = new StatusInfo("101404", PropertyUtil.getErrorMessage(101404, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		return statusInfo;
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
	 * Raises inquiry request call to sparkbase and gets InquiryResponse of success or failure.
	 * if request successful in sparkbase, it returns transaction details including balances.
	 * 
	 * @param valueCode
	 * @param enteredAmount
	 * @param sparkbaseLocation
	 * @param contactLoyalty
	 * @return Object(InquiryResponse)
	 * @throws Exception
	 */
	private Object callToSparkbaseApi(SparkBaseLocationDetails sparkbaseLocation, ContactsLoyalty contactLoyalty) throws Exception {
		Object responseObject = null;
		responseObject = SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.INQUIRY, sparkbaseLocation, contactLoyalty, null, null, true);
		return responseObject;
	}
	/**
	 * Saves contacts loyalty object in OptCulture database.
	 * 
	 * @param contactsLoyalty
	 * @param inquiryStatus
	 * @throws Exception
	 */
	private void saveContactsLoyalty(ContactsLoyalty contactsLoyalty, String inquiryStatus) throws Exception {
		ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
		if(OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(inquiryStatus)){
			contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
		}
	}
	/**
	 * Prepares Balances Object for JSON response.
	 * 
	 * @param sbInquiryResponse(response returned from sparkbase)
	 * @return list of balances(Points, USD)
	 * @throws Exception
	 */
	private List<Balances> prepareBalancesObject(Object sbInquiryResponse) throws Exception{
		List<Balances> balancesList = null;
		Balances balances = null;
		ArrayOfBalance aBalances = null;
		aBalances = ((InquiryResponse)sbInquiryResponse).getBalances();
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
	 * Prepares final JSON Response Object
	 * 
	 * @param headerInfo
	 * @param userDetails
	 * @param inquiryInfo
	 * @param balances
	 * @param statusInfo
	 * @param customerInfo
	 * @return LoyaltyInquiryResponseObject
	 * @throws Exception
	 */
	private LoyaltyInquiryResponseObject prepareInquiryResponse(HeaderInfo headerInfo, UserDetails userDetails, InquiryInfo inquiryInfo,
			List<Balances> balances, StatusInfo statusInfo, CustomerInfo customerInfo) throws Exception {
		LoyaltyInquiryResponseObject inquiryResponse = new LoyaltyInquiryResponseObject();
		inquiryResponse.setHEADERINFO(headerInfo);
		inquiryResponse.setINQUIRYINFO(inquiryInfo);
		inquiryResponse.setUSERDETAILS(userDetails);
		inquiryResponse.setBALANCES(balances);
		inquiryResponse.setSTATUS(statusInfo);
		inquiryResponse.setCUSTOMERINFO(customerInfo);
		return inquiryResponse;
	}
	/**
	 * Fetches customer info from sparkbase inquiryResponse object, and prepares customerInfo object
	 * 
	 * @param contact
	 * @return customerInfo
	 */
	public CustomerInfo getInquiryResponseCustomerData(Object object){
		InquiryResponse inquiryResponse = (InquiryResponse)object;
		CustomerInfoComponent custInfoComponent = inquiryResponse.getCustomerInfo();
		CustomerInfo customerInfo = new CustomerInfo();
		customerInfo.setADDRESS1(custInfoComponent.getAddress1() == null ? "" : custInfoComponent.getAddress1());
		customerInfo.setADDRESS2(custInfoComponent.getAddress2() == null ? "" : custInfoComponent.getAddress2());
		customerInfo.setANNIVERSARY(custInfoComponent.getAnniversary() == null? "" : custInfoComponent.getAnniversary());
		customerInfo.setBIRTHDAY(custInfoComponent.getBirthday() == null ? "" : custInfoComponent.getBirthday());
		customerInfo.setCITY(custInfoComponent.getCity() == null ? "" : custInfoComponent.getCity());
		customerInfo.setCOUNTRY(custInfoComponent.getCountry() == null ? "" : custInfoComponent.getCountry());
		customerInfo.setCUSTOMERID("");
		customerInfo.setCUSTOMERTYPE("");
		customerInfo.setEMAIL(custInfoComponent.getEmail() == null ? "" : custInfoComponent.getEmail());
		customerInfo.setEMAILPREF(custInfoComponent.getEmailPref() == null ? "" : custInfoComponent.getEmailPref());
		customerInfo.setFIRSTNAME(custInfoComponent.getFirstName() == null ? "" : custInfoComponent.getFirstName());
		customerInfo.setGENDER(custInfoComponent.getGender() == null ? "" : custInfoComponent.getGender());
		customerInfo.setLASTNAME(custInfoComponent.getLastName() == null ? "" : custInfoComponent.getLastName());
		customerInfo.setMAILPREF(custInfoComponent.getMailPref() == null ? "" : custInfoComponent.getMailPref());
		customerInfo.setMIDDLENAME(custInfoComponent.getMiddleName() == null ? "" : custInfoComponent.getMiddleName());
		customerInfo.setPHONE(custInfoComponent.getPhone() == null ? "" : custInfoComponent.getPhone());
		customerInfo.setPHONEPREF(custInfoComponent.getPhonePref() == null ? "" : custInfoComponent.getPhonePref());
		customerInfo.setPOSTAL(custInfoComponent.getPostal() == null ? "" : custInfoComponent.getPostal());
		customerInfo.setSTATE(custInfoComponent.getState() == null ? "" : custInfoComponent.getState());
		return customerInfo;
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
}