package org.mq.optculture.business.loyalty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.SparkBaseCard;
import org.mq.marketer.campaign.beans.SparkBaseLocationDetails;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.dao.SparkBaseCardDao;
import org.mq.marketer.campaign.dao.SparkBaseLocationDetailsDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.marketer.sparkbase.SparkBaseServiceAsync;
import org.mq.marketer.sparkbase.transactionWsdl.ArrayOfBalance;
import org.mq.marketer.sparkbase.transactionWsdl.CustomerInfoComponent;
import org.mq.marketer.sparkbase.transactionWsdl.ErrorMessageComponent;
import org.mq.marketer.sparkbase.transactionWsdl.GiftIssuanceResponse;
import org.mq.marketer.sparkbase.transactionWsdl.InquiryResponse;
import org.mq.marketer.sparkbase.transactionWsdl.ResponseStandardHeaderComponent;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.loyalty.Balances;
import org.mq.optculture.model.loyalty.CustomerInfo;
import org.mq.optculture.model.loyalty.HeaderInfo;
import org.mq.optculture.model.loyalty.InquiryInfo;
import org.mq.optculture.model.loyalty.IssuanceInfo;
import org.mq.optculture.model.loyalty.LoyaltyInquiryJsonRequest;
import org.mq.optculture.model.loyalty.LoyaltyInquiryJsonResponse;
import org.mq.optculture.model.loyalty.LoyaltyInquiryRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyInquiryResponseObject;
import org.mq.optculture.model.loyalty.OTPRedeemLimit;
import org.mq.optculture.model.loyalty.StatusInfo;
import org.mq.optculture.model.loyalty.UserDetails;
import org.mq.optculture.model.ocloyalty.AdditionalInfo;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.model.ocloyalty.LoyaltyInquiryRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyInquiryResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceResponse;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.SBToOCJSONTranslator;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;

public class AsyncLoyaltyInquiryServiceImpl implements AsyncLoyaltyInquiryService {
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
		if(serviceRequest == null || !OCConstants.ASYNC_LOYALTY_SERVICE_ACTION_INQUIRY.equals(serviceRequest)){
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
		AsyncLoyaltyInquiryService loyaltyInquiryService = (AsyncLoyaltyInquiryService) ServiceLocator.getInstance().getServiceByName(OCConstants.ASYNC_LOYALTY_INQUIRY_BUSINESS_SERVICE);
		//LoyaltyInquiryResponseObject inquiryResponse = loyaltyInquiryService.processInquiryRequest(inquiryRequest);
		LoyaltyInquiryResponseObject inquiryResponse = loyaltyInquiryService.processInquiryRequest(inquiryRequest, baseRequestObject.getTransactionId(), baseRequestObject.getTransactionDate());		
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
	//public LoyaltyInquiryResponseObject processInquiryRequest(LoyaltyInquiryRequestObject inquiryRequest) throws BaseServiceException {
	public LoyaltyInquiryResponseObject processInquiryRequest(LoyaltyInquiryRequestObject inquiryRequest, String trxID, String trxDate) throws BaseServiceException {
		
		logger.info("started loyalty inquiry with requestid : "+inquiryRequest.getHEADERINFO().getREQUESTID());
		LoyaltyInquiryResponseObject inquiryResponse = null;
		StatusInfo statusInfo = null;
		CustomerInfo customerInfo= null;
		Users user = null;
		ContactsLoyalty contactsLoyalty = null;
		SparkBaseLocationDetails sparkbaseLocation = null;
		InquiryInfo inquiryInfo = inquiryRequest.getINQUIRYINFO();
		String tierName = "";
		inquiryInfo.setTIERNAME(tierName);
		
		try{
			
			statusInfo = validateInquiryJsonData(inquiryRequest);
			if(statusInfo != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
				inquiryResponse = prepareInquiryResponse(inquiryRequest.getHEADERINFO(), inquiryRequest.getUSERDETAILS(),
						inquiryInfo, null, statusInfo, null);
				return inquiryResponse;
			}
			
			user = getUser(inquiryRequest.getUSERDETAILS().getUSERNAME(), inquiryRequest.getUSERDETAILS().getORGANISATION(), inquiryRequest.getUSERDETAILS().getTOKEN());
			//by pravendra 
			if(user==null){
				statusInfo = new StatusInfo(
						"101013", PropertyUtil.getErrorMessage(101013, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				if(statusInfo != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
					inquiryResponse = prepareInquiryResponse(inquiryRequest.getHEADERINFO(), inquiryRequest.getUSERDETAILS(),
							inquiryInfo, null, statusInfo, null);
					return inquiryResponse;
				}
			}
			//need to redirect the request to OC loyalty service based on the user flag
			if(user != null && OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equals(user.getloyaltyServicetype())){
				
				LoyaltyTransaction loyaltyTrx = findTransaction(trxID);
				if(loyaltyTrx != null) {
					LoyaltyTransactionDao loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
					LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			    
					loyaltyTrx.setLoyaltyServiceType(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC);
				   // loyaltyTransactionDao.saveOrUpdate(loyaltyTrx);
				    loyaltyTransactionDaoForDML.saveOrUpdate(loyaltyTrx);
					
				}
				SBToOCJSONTranslator translator = new SBToOCJSONTranslator();
				Object requestObject = translator.convertSbReqToOC(inquiryRequest, OCConstants.LOYALTY_TRANSACTION_INQUIRY);
				LoyaltyInquiryRequest loyaltyinquiryRequest = (LoyaltyInquiryRequest)requestObject;
				LoyaltyInquiryOCService loyaltyInquiryService = (LoyaltyInquiryOCService)ServiceLocator.getInstance().getServiceById(OCConstants.LOYALTY_INQUIRY_OC_BUSINESS_SERVICE);
				LoyaltyInquiryResponse responseObject = loyaltyInquiryService.processInquiryRequest(loyaltyinquiryRequest, trxID, trxDate);
				
				
				statusInfo = translator.convertStatus(responseObject.getStatus());
				if(OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
					inquiryResponse = prepareInquiryResponse(inquiryRequest.getHEADERINFO(), inquiryRequest.getUSERDETAILS(), inquiryInfo, null, statusInfo, null);
					return inquiryResponse;
				}else{
					customerInfo = new CustomerInfo();
					customerInfo.setCUSTOMERID(responseObject.getMatchedCustomers().get(0).getCustomerId());
					customerInfo.setCUSTOMERTYPE("");
					customerInfo.setFIRSTNAME(responseObject.getMatchedCustomers().get(0).getFirstName());
					customerInfo.setMIDDLENAME("");
					customerInfo.setLASTNAME(responseObject.getMatchedCustomers().get(0).getLastName());
					customerInfo.setPHONE(responseObject.getMatchedCustomers().get(0).getPhone());//APP-2857
					customerInfo.setPHONEPREF("");
					customerInfo.setEMAIL(responseObject.getMatchedCustomers().get(0).getEmailAddress());
					customerInfo.setEMAILPREF("");			
					customerInfo.setADDRESS1("");
					customerInfo.setADDRESS2("");
					customerInfo.setCITY("");
					customerInfo.setSTATE("");
					customerInfo.setPOSTAL("");
					customerInfo.setCOUNTRY("");
					customerInfo.setMAILPREF("");
					customerInfo.setBIRTHDAY("");
					customerInfo.setANNIVERSARY("");
					customerInfo.setGENDER("");
					
					
					inquiryInfo.setTIERNAME(responseObject.getMembership().getTierName());
					if(responseObject.getAdditionalInfo().getOtpEnabled() != null && 
							!responseObject.getAdditionalInfo().getOtpEnabled().isEmpty())inquiryInfo.setOTPENABLED(responseObject.getAdditionalInfo().getOtpEnabled());
					if(responseObject.getAdditionalInfo().getOtpRedeemLimit() != null && responseObject.getAdditionalInfo().getOtpRedeemLimit().size() > 0) {
						
						List<OTPRedeemLimit> OTPRedeemLimit = prepareOTPRedeemLimit(responseObject.getAdditionalInfo().getOtpRedeemLimit());
						inquiryInfo.setOTPREDEEMLIMIT(OTPRedeemLimit);
					}
					
					
					List<Balances> balances = prepareSBToOCBalancesObject(responseObject);
					inquiryResponse = prepareInquiryResponse(inquiryRequest.getHEADERINFO(), inquiryRequest.getUSERDETAILS(),
							inquiryInfo, balances, statusInfo, customerInfo);
					return inquiryResponse;				
				
				}
				
			}
			
			if(OCConstants.LOYALTY_SERVICE_TYPE_SB.equals(user.getloyaltyServicetype())) {
				statusInfo = validateUserSparkbaseSettings(user, inquiryRequest.getUSERDETAILS().getTOKEN(), inquiryRequest.getINQUIRYINFO().getSTORELOCATIONID());
				if(statusInfo != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
					inquiryResponse = prepareInquiryResponse(inquiryRequest.getHEADERINFO(), inquiryRequest.getUSERDETAILS(),
							inquiryInfo, null, statusInfo, null);
					return inquiryResponse;
				}
			}
			
			//removing card validation in inquiry w.r.t Dynamic program and SBTOOC users
			/*if(inquiryRequest.getINQUIRYINFO().getCARDNUMBER() != null && !inquiryRequest.getINQUIRYINFO().getCARDNUMBER().trim().isEmpty()){
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
			}*/
			boolean isMobile = false;
			String cardNumber = inquiryRequest.getINQUIRYINFO().getCARDNUMBER();
			String customerId = inquiryRequest.getINQUIRYINFO().getCUSTOMERID();
			
			if(cardNumber == null || cardNumber.trim().isEmpty()){
				contactsLoyalty = findLoyaltyCardInOCByCustId(customerId, user.getUserId());
			}
			else if(cardNumber != null && !cardNumber.trim().isEmpty()){
				
				contactsLoyalty = findLoyaltyCardInOC(cardNumber, user.getUserId());
				if(contactsLoyalty == null) {
					String parsedCardNumber = OptCultureUtils.parseCardNumber(cardNumber);
					if(parsedCardNumber.length() != 0 && parsedCardNumber.length() < 15) {
						
						contactsLoyalty = findLoyaltyCardInOCByMobile(parsedCardNumber, user.getUserId());
					}
					
				}
				
			}
			
			if(contactsLoyalty != null){
				//by pravendra
				if(contactsLoyalty.getMembershipStatus() != null) {
				if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED)){
					String message = PropertyUtil.getErrorMessage(111517, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
					statusInfo = new StatusInfo("111517", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					inquiryResponse = prepareInquiryResponse(inquiryRequest.getHEADERINFO(), inquiryRequest.getUSERDETAILS(),
							inquiryInfo, null, statusInfo, null);
					return inquiryResponse;
				}
				if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED)){
					String message = PropertyUtil.getErrorMessage(111539, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
					statusInfo = new StatusInfo("111539", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					inquiryResponse = prepareInquiryResponse(inquiryRequest.getHEADERINFO(), inquiryRequest.getUSERDETAILS(),
							inquiryInfo, null, statusInfo, null);
					return inquiryResponse;
				}
				if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
					String message = PropertyUtil.getErrorMessage(111578, OCConstants.ERROR_LOYALTY_FLAG)+contactsLoyalty.getCardNumber()+".";
					statusInfo = new StatusInfo("111578", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					inquiryResponse = prepareInquiryResponse(inquiryRequest.getHEADERINFO(), inquiryRequest.getUSERDETAILS(),
							inquiryInfo, null, statusInfo, null);
					return inquiryResponse;
				}
				}
				inquiryRequest.getINQUIRYINFO().setCARDNUMBER(contactsLoyalty.getCardNumber().toString());
				inquiryRequest.getINQUIRYINFO().setCARDPIN(contactsLoyalty.getCardPin().toString());

				Balances bal = null;
				List<Balances> balances = new ArrayList<Balances>();
				try{
				statusInfo = new StatusInfo("0", "Inquiry was successful.",
						OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				if(contactsLoyalty.getLoyaltyBalance()!=null){
					bal = new Balances();
					double value = new BigDecimal(contactsLoyalty.getLoyaltyBalance().intValue()).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
					//bal.setAMOUNT(contactsLoyalty.getLoyaltyBalance().intValue()+"");
					bal.setAMOUNT(value+"");
					bal.setVALUECODE("Points");
					bal.setEXCHANGERATE("");
					bal.setDIFFERENCE("0");
					balances.add(bal);
				}else{
					bal = new Balances();
					bal.setAMOUNT("0");
					bal.setVALUECODE("Points");
					bal.setEXCHANGERATE("");
					bal.setDIFFERENCE("0");
					balances.add(bal);
				}
				if(contactsLoyalty.getGiftcardBalance()!=null){
					bal = new Balances();
					//double value = new BigDecimal(contactsLoyalty.getGiftcardBalance()).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
					double value = Double.parseDouble(Utility.truncateUptoTwoDecimal(contactsLoyalty.getGiftcardBalance()));
					//bal.setAMOUNT(contactsLoyalty.getGiftcardBalance()+"");
					bal.setAMOUNT(value+"");
					bal.setVALUECODE("USD");
					bal.setEXCHANGERATE("");
					bal.setDIFFERENCE("0");
					balances.add(bal);
					
				}else{
					bal = new Balances();
					bal.setAMOUNT("0.00");
					bal.setVALUECODE("USD");
					bal.setEXCHANGERATE("");
					bal.setDIFFERENCE("0");
					balances.add(bal);
				}
				customerInfo = new CustomerInfo();
				Contacts contact = contactsLoyalty.getContact();
				if(contact != null){
				customerInfo.setADDRESS1(contact.getAddressOne() == null || contact.getAddressOne().trim().isEmpty() ? "" : contact.getAddressOne());
				customerInfo.setADDRESS2(contact.getAddressTwo() == null || contact.getAddressTwo().trim().isEmpty() ? "" : contact.getAddressTwo());
				customerInfo.setANNIVERSARY("");
				if(contact.getAnniversary() != null) {
					String retStr = MyCalendar.calendarToString(contact.getAnniversary(), MyCalendar.FORMAT_SB_DATEONLY);
					if(!retStr.equals("--")) customerInfo.setANNIVERSARY(retStr);
				}
				customerInfo.setBIRTHDAY("");
				
				if(contact.getBirthDay() != null) {
					String retStr = MyCalendar.calendarToString(contact.getBirthDay(), MyCalendar.FORMAT_SB_DATEONLY);
					if(!retStr.equals("--")) customerInfo.setBIRTHDAY(retStr);
				}
				
				
				customerInfo.setCITY(contact.getCity() == null || contact.getCity().trim().isEmpty() ? "" : contact.getCity());
				customerInfo.setCOUNTRY(contact.getCountry() == null || contact.getCountry().trim().isEmpty() ? "" : contact.getCountry());
				customerInfo.setCUSTOMERID("");
				customerInfo.setCUSTOMERTYPE("");
				customerInfo.setEMAIL(contact.getEmailId()==null || contact.getEmailId().trim().isEmpty() ? "" : contact.getEmailId());
				customerInfo.setEMAILPREF("");
				customerInfo.setFIRSTNAME(contact.getFirstName()==null || contact.getFirstName().trim().isEmpty() ? "" : contact.getFirstName());
				customerInfo.setGENDER("");
				if(contact.getGender() != null && contact.getGender().trim().length()>0) {
					char ch = contact.getGender().toUpperCase().charAt(0);
					if(ch=='M' || ch=='F') customerInfo.setGENDER(""+ch); 
				}
				customerInfo.setLASTNAME(contact.getLastName() == null || contact.getLastName().trim().isEmpty() ? "" : contact.getLastName());
				customerInfo.setMAILPREF("");
				customerInfo.setMIDDLENAME("");
				customerInfo.setPHONE(contact.getMobilePhone() == null || contact.getMobilePhone().trim().length() ==0 ? "" : contact.getMobilePhone() + "");      
				customerInfo.setPHONEPREF("");
				customerInfo.setPOSTAL("");
				customerInfo.setSTATE(contact.getState() == null || contact.getState().trim().isEmpty() ? "" : contact.getState());
				}else{
					customerInfo.setADDRESS1("");
					customerInfo.setADDRESS2("");
					customerInfo.setANNIVERSARY("");
					 customerInfo.setANNIVERSARY("");
					customerInfo.setBIRTHDAY("");
					 customerInfo.setBIRTHDAY("");
					customerInfo.setCITY("");
					customerInfo.setCOUNTRY("");
					customerInfo.setCUSTOMERID("");
					customerInfo.setCUSTOMERTYPE("");
					customerInfo.setEMAIL("");
					customerInfo.setEMAILPREF("");
					customerInfo.setFIRSTNAME("");
					customerInfo.setGENDER("");
					customerInfo.setGENDER(""); 
					customerInfo.setLASTNAME("");
					customerInfo.setMAILPREF("");
					customerInfo.setMIDDLENAME("");
					customerInfo.setPHONE("");      
					customerInfo.setPHONEPREF("");
					customerInfo.setPOSTAL("");
					customerInfo.setSTATE("");
				}
				if(contactsLoyalty != null && contactsLoyalty.getProgramTierId() != null){
					
					tierName = getLoyaltyTierName(contactsLoyalty.getProgramTierId());
					inquiryInfo.setTIERNAME(tierName);

				}
				inquiryResponse = prepareInquiryResponse(inquiryRequest.getHEADERINFO(), inquiryRequest.getUSERDETAILS(),
						inquiryInfo, balances, statusInfo, customerInfo );
				}catch (Exception e) {
					logger.info(" Exception occured while preparing response ::: ",e);
					inquiryResponse = prepareInquiryResponse(inquiryRequest.getHEADERINFO(), inquiryRequest.getUSERDETAILS(),
							inquiryInfo, balances, statusInfo, new CustomerInfo() );
				}
				return inquiryResponse;
				
			}
			
			if(contactsLoyalty == null && (cardNumber == null || cardNumber.trim().isEmpty())){
				statusInfo = new StatusInfo("200013", PropertyUtil.getErrorMessage(200013, OCConstants.ERROR_LOYALTY_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				inquiryResponse = prepareInquiryResponse(inquiryRequest.getHEADERINFO(), inquiryRequest.getUSERDETAILS(),
						inquiryInfo, null, statusInfo, null);
				return inquiryResponse;
			}
			
			
			//contactsLoyalty = findLoyaltyCardInOC(inquiryRequest.getINQUIRYINFO().getCARDNUMBER(), user.getUserId());
			
			//inquiry loyalty card in sparkbase
			Object object = null;
			boolean loyaltyNotFound = false;
			//for only SB users it will wrk
			if(OCConstants.LOYALTY_SERVICE_TYPE_SB.equals(user.getloyaltyServicetype())){
				
				if(contactsLoyalty == null && cardNumber != null && cardNumber.trim().length() > 0){ //loyalty not found in OC
					
					contactsLoyalty = new ContactsLoyalty();
					contactsLoyalty.setCardNumber(cardNumber);
					contactsLoyalty.setCardPin(inquiryRequest.getINQUIRYINFO().getCARDPIN());
					loyaltyNotFound = true;
				}		
					
				sparkbaseLocation = getSparkbaseLocation(user.getUserOrganization().getUserOrgId());
				//ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
				object = callToSparkbaseApi(sparkbaseLocation, contactsLoyalty);
				statusInfo = getInquiryResponseStatus(object);
					
				if(statusInfo != null && statusInfo.getERRORCODE().equals("0")){
					
					customerInfo = getInquiryResponseCustomerData(object);
					customerInfo.setCUSTOMERID(contactsLoyalty.getCustomerId() == null ? "" : contactsLoyalty.getCustomerId());
					List<Balances> balances = prepareBalancesObject(object);
					inquiryRequest.getINQUIRYINFO().setCARDNUMBER(contactsLoyalty.getCardNumber()+"");
					inquiryRequest.getINQUIRYINFO().setCARDPIN(contactsLoyalty.getCardPin() == null ? "" : contactsLoyalty.getCardPin());
					
					if(contactsLoyalty != null && contactsLoyalty.getProgramTierId() != null){
						
						tierName = getLoyaltyTierName(contactsLoyalty.getProgramTierId());
						inquiryInfo.setTIERNAME(tierName);

					}
					inquiryResponse = prepareInquiryResponse(inquiryRequest.getHEADERINFO(), inquiryRequest.getUSERDETAILS(),
							inquiryInfo, balances, statusInfo, customerInfo);
					//To avoid duplicate cards creation the below portion commented
					
					/*loyaltyDao.saveOrUpdate(contactsLoyalty);
					
					if(loyaltyNotFound && !isMobile){
						
						SparkBaseCardDao cardDao = (SparkBaseCardDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASE_CARD_DAO);
						SparkBaseCard card = findSBCardInOC(cardNumber, sparkbaseLocation.getSparkBaseLocationDetails_id());
						
						
						ContactsDao contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
						Contacts contact =  contactsDao.findContactByValues(null, null, inquiryRequest.getINQUIRYINFO().getCUSTOMERID(), user.getUserId());
						contactsLoyalty.setContact(contact);
						contactsLoyalty.setCustomerId(inquiryRequest.getINQUIRYINFO().getCUSTOMERID());
						if(card!=null)contactsLoyalty.setCardPin(card.getCardPin());
						contactsLoyalty.setEmpId(inquiryRequest.getINQUIRYINFO().getEMPID());
						contactsLoyalty.setPosStoreLocationId(inquiryRequest.getHEADERINFO().getSTORENUMBER());
						contactsLoyalty.setMode(OCConstants.LOYALTY_ONLINE_MODE);
						contactsLoyalty.setServiceType(OCConstants.LOYALTY_SERVICE_TYPE_SB);
						contactsLoyalty.setMembershipStatus(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE);
						contactsLoyalty.setMobilePhone(contact.getMobilePhone());
						contactsLoyalty.setRewardFlag(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);
						contactsLoyalty.setCreatedDate(Calendar.getInstance());
						//contactsLoyalty.setCardPin(inquiryRequest.getINQUIRYINFO().getCARDPIN());
						contactsLoyalty.setUserId(user.getUserId());
//						contactsLoyalty.setOptinMedium(Constants.CONTACT_LOYALTY_TYPE_POS);
						contactsLoyalty.setContactLoyaltyType(Constants.CONTACT_LOYALTY_TYPE_POS);
						contactsLoyalty.setLocationId(sparkbaseLocation.getLocationId());
						contactsLoyalty.setPosStoreLocationId(sparkbaseLocation.getLocationId());
						contactsLoyalty.setUserId(user.getUserId());
						
						//ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACT_LOYALITY_DAO);
						loyaltyDao.saveOrUpdate(contactsLoyalty);
						
						if(card == null){
							card = new SparkBaseCard();
							card.setSparkBaseLocationId(sparkbaseLocation);
							card.setCardId(contactsLoyalty.getCardNumber());
							card.setCardPin(contactsLoyalty.getCardPin());
							card.setCardType(Constants.SPARKBASE_CARD_TYPE_PHYSICAL);
							card.setStatus(Constants.SPARKBASE_CARD_STATUS_ACTIVATED);
							
							cardDao.saveOrUpdate(card);
						}
						else{
							if(card.getStatus().equalsIgnoreCase(Constants.SPARKBASE_CARD_STATUS_INVENTORY)){
								card.setStatus(Constants.SPARKBASE_CARD_STATUS_ACTIVATED);
								cardDao.saveOrUpdate(card);
							}
						}
						
					}*/
					
					//statusInfo = findCardInOCRepository(inquiryRequest.getINQUIRYINFO().getCARDNUMBER(),sparkbaseLocation.getSparkBaseLocationDetails_id());
				}
				else{
					
					inquiryResponse = prepareInquiryResponse(inquiryRequest.getHEADERINFO(), inquiryRequest.getUSERDETAILS(),
							inquiryInfo, null, statusInfo, null);
					return inquiryResponse;
				}
				
			}else {
				if(contactsLoyalty == null && cardNumber != null && cardNumber.trim().length() > 0){
					
					statusInfo = new StatusInfo("200013", PropertyUtil.getErrorMessage(200013, OCConstants.ERROR_LOYALTY_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					inquiryResponse = prepareInquiryResponse(inquiryRequest.getHEADERINFO(), inquiryRequest.getUSERDETAILS(),
							inquiryInfo, null, statusInfo, null);
					return inquiryResponse;
					
				}
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
			inquiryResponse.setINQUIRYINFO(inquiryInfo);
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
		//APP-1981
		if(errorMsg != null){
			statusInfo = new StatusInfo("200201", PropertyUtil.getErrorMessage(200201, OCConstants.ERROR_LOYALTY_FLAG)+", "+errorMsg.getBriefMessage()!=null&&errorMsg.getBriefMessage().trim().equalsIgnoreCase("Activation Required")?
					"Card is getting activated. Please proceed with the transaction.":errorMsg.getBriefMessage(),
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
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		contactLoyalty = contactsLoyaltyDao.getContactsLoyaltyByCardId(cardNumber, userId);
		return contactLoyalty;
	}
	
	
	
	private ContactsLoyalty findLoyaltyCardInOCByMobile(String mobile, Long userId) throws Exception {
		ContactsLoyalty contactLoyalty = null;
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		contactLoyalty = contactsLoyaltyDao.getContactsLoyaltyByMobile(Long.valueOf(mobile), userId);
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
		List<SparkBaseCard> cardList = sparkBaseCardDao.findByCardId(ocLocationId,cardNumber);

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
				//double value = new BigDecimal(aBalances.getBalance().get(i).getAmount()).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
				double value = Double.parseDouble(Utility.truncateUptoTwoDecimal(Double.parseDouble(aBalances.getBalance().get(i).getAmount())));
				//balances.setAMOUNT(aBalances.getBalance().get(i).getAmount());
				balances.setAMOUNT(""+value);
				balances.setDIFFERENCE(aBalances.getBalance().get(i).getDifference());
				balances.setEXCHANGERATE(aBalances.getBalance().get(i).getExchangeRate());
				balancesList.add(balances);
			}
		}
		return balancesList;
	}
	
	/**
	 * Prepares Balances Object for JSON response.
	 * 
	 * @param sbtoocInquiryResponse(response returned from OC)
	 * @return list of balances(Points, USD)
	 * @throws Exception
	 */
	private List<Balances> prepareSBToOCBalancesObject(Object inquiryResponse) throws Exception{

		List<Balances> balancesList = null;

		Balances balances = null;
		String valueCode = null;
		//ArrayOfBalance aBalances = null;
		AdditionalInfo additionalInfo =((LoyaltyInquiryResponse)inquiryResponse).getAdditionalInfo();
		List<Balance> ocGivenBals = ((LoyaltyInquiryResponse)inquiryResponse).getBalances();
		balancesList = new ArrayList<Balances>();
		for(int i = 0; i < ocGivenBals.size(); i++) {
			if(ocGivenBals.get(i).getType() != OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT){
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
				//APP-1967
				if(i==1 && additionalInfo.getTotalRedeemableCurrency()!= null && !additionalInfo.getTotalRedeemableCurrency().isEmpty())
					balances.setAMOUNT(additionalInfo.getTotalRedeemableCurrency());
				
				balances.setDIFFERENCE(ocGivenBals.get(i).getDifference());
				balances.setEXCHANGERATE("");
				balancesList.add(balances);
			}
		}
		/*if(aBalances != null && aBalances.getBalance() != null){
			balancesList = new ArrayList<Balances>();
			for(int i = 0; i < aBalances.getBalance().size(); i++) {
				balances = new Balances();
				balances.setVALUECODE(aBalances.getBalance().get(i).getValueCode());
				balances.setAMOUNT(aBalances.getBalance().get(i).getAmount());
				balances.setDIFFERENCE(aBalances.getBalance().get(i).getDifference());
				balances.setEXCHANGERATE(aBalances.getBalance().get(i).getExchangeRate());
				balancesList.add(balances);
			}
		}*/
		return balancesList;
	}
	
	public List<OTPRedeemLimit> prepareOTPRedeemLimit(List<org.mq.optculture.model.ocloyalty.OTPRedeemLimit> OCOTPRedeemLimit) {
		List<OTPRedeemLimit> OTPRedeemLimit = new ArrayList<OTPRedeemLimit>();
		
		for (org.mq.optculture.model.ocloyalty.OTPRedeemLimit OCOtpRedeemLimitObj : OCOTPRedeemLimit) {
			
			OTPRedeemLimit otpRedeemLimit = new OTPRedeemLimit();
			otpRedeemLimit.setAMOUNT(OCOtpRedeemLimitObj.getAmount());
			otpRedeemLimit.setVALUECODE(OCOtpRedeemLimitObj.getValueCode());
			OTPRedeemLimit.add(otpRedeemLimit);
		}
		
		return OTPRedeemLimit;
		
	}
	public String getLoyaltyTierName(Long tierID) throws Exception{
			
		LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		LoyaltyProgramTier retTier = loyaltyProgramTierDao.getTierById(tierID);
		String tierName = retTier != null ? retTier.getTierName() : "" ;
		return tierName;
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
	public LoyaltyTransaction findTransaction(String trxID){
		LoyaltyTransaction transaction = null;
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			transaction = loyaltyTransactionDao.findById(Long.parseLong(trxID));
		}catch(Exception e){
			logger.error("Exception in find transaction by requestid", e);
		}
		return transaction;
	}
}