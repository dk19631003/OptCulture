package org.mq.optculture.business.loyalty;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyCards;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramExclusion;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyThresholdBonus;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.LoyaltyTransactionExpiry;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.service.CaptiwayToSMSApiGateway;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObservable;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObserver;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MailingListDaoForDML;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptLtyMembshpPwd;
import org.mq.marketer.campaign.general.POSFieldsEnum;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.GatewayRequestProcessHelper;
import org.mq.optculture.business.helper.LoyaltyCardFinder;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.helper.SmsQueueHelper;
import org.mq.optculture.data.dao.LoyaltyAutoCommDao;
import org.mq.optculture.data.dao.LoyaltyCardSetDao;
import org.mq.optculture.data.dao.LoyaltyCardSetDaoForDML;
import org.mq.optculture.data.dao.LoyaltyCardsDao;
import org.mq.optculture.data.dao.LoyaltyCardsDaoForDML;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramExclusionDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDaoForDML;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.ocloyalty.Customer;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollResponse;
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.ocloyalty.MembershipResponse;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.model.updatecontacts.ContactRequest;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zkplus.spring.SpringUtil;

import com.google.gson.Gson;

/**
 * === OptCulture Loyalty Program ===
 * 
 * Loyalty enrolment service.
 * Input: enrol request JSON 
 * Output: enrol response JSON with status
 * @deprecated
 * @author Venkata Rathnam D
 *
 */
public class LoyaltyEnrollmentOCServiceImpl implements LoyaltyEnrollmentOCService{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	/**
	 * BaseService Request called by rest service controller.
	 * @return BaseResponseObject
	 */
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		logger.info("Started processRequest...");
		
		BaseResponseObject responseObject = null;
		String serviceRequest = baseRequestObject.getAction();
		String requestJson = baseRequestObject.getJsonValue();
		String responseJson = null;
		Gson gson = new Gson();
		LoyaltyEnrollResponse enrollResponse = null;
		LoyaltyEnrollRequest enrollRequest = null;
		
		if(serviceRequest == null || !serviceRequest.equals(OCConstants.LOYALTY_SERVICE_ACTION_ENROLMENT)){
			logger.info("servicerequest is null...");
			
			Status status = new Status("101001", ""+PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			
			enrollResponse = new LoyaltyEnrollResponse();
			enrollResponse.setStatus(status);
			responseJson = gson.toJson(enrollResponse);
			
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			logger.info("Exited baserequest due to invalid service");
			return responseObject;
		}
		
		try{
			enrollRequest = gson.fromJson(requestJson, LoyaltyEnrollRequest.class);
		}catch(Exception e){
			
			Status status = new Status("101001", ""+PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			
			enrollResponse = new LoyaltyEnrollResponse();
			enrollResponse.setStatus(status);
			responseJson = gson.toJson(enrollResponse);
			
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			logger.info("Exited baserequest due to invalid JSON ", e);
			return responseObject;
		}
		
		try{
			LoyaltyEnrollmentOCService loyaltyEnrollService = (LoyaltyEnrollmentOCService) ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_ENROLMENT_OC_BUSINESS_SERVICE);
			enrollResponse = loyaltyEnrollService.processEnrollmentRequest(enrollRequest, OCConstants.LOYALTY_ONLINE_MODE, baseRequestObject.getTransactionId(), baseRequestObject.getTransactionDate());
			responseJson = gson.toJson(enrollResponse);
			
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
		}catch(Exception e){
			logger.error("Exception in loyalty enroll base service.",e);
			throw new BaseServiceException("Server Error.");
		}
		logger.info("Completed processing baserequest... ");
		return responseObject;

	}
	
	/**
	 * Handles the complete process of Loyalty Enrolment.
	 * 
	 * @param enrollRequest
	 * @return enrollResponse
	 * @throws BaseServiceException
	 */
	@Override
	public LoyaltyEnrollResponse processEnrollmentRequest(LoyaltyEnrollRequest enrollRequest, String mode, String transactionId, String transactionDate)
			throws BaseServiceException {

		logger.info("Started processing enrollment request...");
		LoyaltyEnrollResponse enrollResponse = null;
		Status status = null;
		Users user = null;
		
		ResponseHeader responseHeader = new ResponseHeader();
		responseHeader.setRequestDate(enrollRequest.getHeader().getRequestDate());
		responseHeader.setRequestId(enrollRequest.getHeader().getRequestId());
		responseHeader.setTransactionDate(transactionDate);
		responseHeader.setTransactionId(transactionId);
		responseHeader.setSourceType(enrollRequest.getHeader().getSourceType() != null && !enrollRequest.getHeader().getSourceType().trim().isEmpty() ? enrollRequest.getHeader().getSourceType().trim() : Constants.STRING_NILL);
		/*responseHeader.setSubsidiaryNumber(enrollRequest.getHeader().getSubsidiaryNumber() != null && !enrollRequest.getHeader().getSubsidiaryNumber().trim().isEmpty() ? enrollRequest.getHeader().getSubsidiaryNumber().trim() : Constants.STRING_NILL);
		responseHeader.setReceiptNumber( Constants.STRING_NILL);
		responseHeader.setReceiptAmount(Constants.STRING_NILL);*/
		try{
			
			//Basic validations including store number validation
			status = validateEnrollmentJsonData(enrollRequest);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
				return enrollResponse;
			}
		
			user = getUser(enrollRequest.getUser().getUserName(), enrollRequest.getUser().getOrganizationId(),
					enrollRequest.getUser().getToken());
			if(user == null){
				status = new Status("101013", PropertyUtil.getErrorMessage(101013, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
				return enrollResponse;
			}
			
			if(!user.isEnabled()){
				status = new Status("111558", PropertyUtil.getErrorMessage(111558, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
				return enrollResponse;
			}
			if(user.getPackageExpiryDate().before(Calendar.getInstance())){
				status = new Status("111559", PropertyUtil.getErrorMessage(111559, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
				return enrollResponse;
			}
			//if the user has loyalty type oc(changed during migrationsbtooc)
			if(OCConstants.LOYALTY_SERVICE_TYPE_OC.equals(user.getloyaltyServicetype())) {
				
				if(enrollRequest.getHeader().getStoreNumber() == null || enrollRequest.getHeader().getStoreNumber().length() <= 0){
					status = new Status("111501", PropertyUtil.getErrorMessage(111501, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					return enrollResponse;
				}
				
			}
			
			MailingListDao mailingListDao = (MailingListDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
			MailingList mlList = mailingListDao.findPOSMailingList(user);
			
			if(mlList == null){
				status = new Status("101007", PropertyUtil.getErrorMessage(101007, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				enrollResponse = prepareEnrollmentResponse(responseHeader, null, null,status);
				return enrollResponse;
			}
			
			LoyaltyCards loyaltyCard = null;
			LoyaltyProgram loyaltyProgram = null;
			ContactsLoyalty contactLoyalty = null;
			
			if(enrollRequest.getMembership().getCardNumber() != null && enrollRequest.getMembership().getCardNumber().trim().length() > 0){
				
				logger.info("Enrollment with card number : "+enrollRequest.getMembership().getCardNumber());

				//find all active programs of the user
				String programIdStr = null;
				List<LoyaltyProgram> activePrograms = findActiveProgramList(user.getUserId(), OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD);
				if(activePrograms == null || activePrograms.size() <= 0){
					status = new Status("111503", PropertyUtil.getErrorMessage(111503, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					return enrollResponse;
				}
				//find is any dynamic card based program is there for this user, if there then skip the validation as numbertype
				boolean isDCardBasedExists = false;
				boolean isSuspendedProgram=false;
				LoyaltyProgram DCardBasedProgram = null;
				for(LoyaltyProgram program : activePrograms){
					if(programIdStr == null){
						programIdStr = ""+program.getProgramId();
					}
					else{
						programIdStr += ","+program.getProgramId();
					}
					if(OCConstants.LOYALTY_PROGRAM_TYPE_DYNAMIC.equals(program.getProgramType())) {
						isDCardBasedExists = true;
						DCardBasedProgram = program;
					}
				}
				
				String cardNumber = "";
				//if(!isDCardBasedExists){
					
					//parse the card
					String cardLong = null;
					
					/*if(enrollRequest.getMembership().getCardNumber().trim().length() != 16) {
						status = new Status("100107", PropertyUtil.getErrorMessage(100107, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						return enrollResponse;
					}*/
					cardLong = OptCultureUtils.validateOCLtyCardNumber(enrollRequest.getMembership().getCardNumber().trim());
					if(cardLong == null){
						
						if(isDCardBasedExists){
							String validationRule = DCardBasedProgram.getValidationRule();
							if(validationRule != null) {
								cardNumber = OptCultureUtils.validateOCLtyDCardNumber(enrollRequest.getMembership().getCardNumber().trim(), validationRule);
								if(cardNumber == null){
									String val[] = validationRule.split(Constants.ADDR_COL_DELIMETER);
									String msg = PropertyUtil.getErrorMessage(100108, OCConstants.ERROR_LOYALTY_FLAG)+" "+(val[0].equalsIgnoreCase("N")?"Numeric":"Alphanumeric")+
											" and length should be "+val[1]+(val[0].equalsIgnoreCase("N")?" digits":" characters")+".";
									status = new Status("100108", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
									enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
									return enrollResponse;
								}
								enrollRequest.getMembership().setCardNumber(cardNumber);
							}
							
						}else{
							
							String msg = PropertyUtil.getErrorMessage(100107, OCConstants.ERROR_LOYALTY_FLAG)+" "+enrollRequest.getMembership().getCardNumber().trim()+".";
							status = new Status("100107", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
							return enrollResponse;
							
						}
					}
					if(cardLong != null) {
						loyaltyCard = findLoyaltyCardByUserId(enrollRequest.getMembership().getCardNumber().trim(), user.getUserId());
						if(loyaltyCard == null){
							if(!isDCardBasedExists) {
								String msg = PropertyUtil.getErrorMessage(100107, OCConstants.ERROR_LOYALTY_FLAG)+" "+enrollRequest.getMembership().getCardNumber().trim()+".";
								status = new Status("100107", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
								return enrollResponse;
							}
							else{
								String validationRule = DCardBasedProgram.getValidationRule();
								if(validationRule != null) {
									
									cardNumber = OptCultureUtils.validateOCLtyDCardNumber(enrollRequest.getMembership().getCardNumber().trim(), validationRule);
									if(cardNumber == null){
										String val[] = validationRule.split(Constants.ADDR_COL_DELIMETER);
										String msg = PropertyUtil.getErrorMessage(100108, OCConstants.ERROR_LOYALTY_FLAG)+" "+(val[0].equalsIgnoreCase("N")?"Numeric":"Alphanumeric")+
												" and length should be "+val[1]+(val[0].equalsIgnoreCase("N")?" digits":" characters")+".";
										status = new Status("100108", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
										enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
										return enrollResponse;
									}
									enrollRequest.getMembership().setCardNumber(cardNumber);
								}
							}
						}
						//by pravendra
						else{
							logger.info("**********else ");
							List<LoyaltyProgram> suspendPrograms = findSuspendedProgramList(user.getUserId(), OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD);
							if(suspendPrograms!=null){
							for(LoyaltyProgram susprg:suspendPrograms){
								  if(susprg.getProgramId().equals(loyaltyCard.getProgramId())){
									  isSuspendedProgram=true;
									  break;
								  }
							  }
							}
						}
						cardNumber = ""+cardLong;
					}
					logger.info("CARD NUMBER After parsing :"+cardNumber);
					enrollRequest.getMembership().setCardNumber(cardNumber);
			/*	}else{
					
					String validationRule = DCardBasedProgram.getValidationRule();
					if(validationRule != null) {
						 
						cardNumber = OptCultureUtils.validateOCLtyDCardNumber(enrollRequest.getMembership().getCardNumber().trim(), validationRule);
						if(cardNumber == null){
							String msg = PropertyUtil.getErrorMessage(100108, OCConstants.ERROR_LOYALTY_FLAG)+" "+enrollRequest.getMembership().getCardNumber().trim()+".";
							status = new Status("100108", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
							return enrollResponse;
						}
						enrollRequest.getMembership().setCardNumber(cardNumber);
					}
					
				}*/
				logger.info("Active program Ids : "+programIdStr);
				
				//find all active card sets of the user of type physical and virtual
				String cardSetIdStr = null;
				List<LoyaltyCardSet> activeCardSets = findActiveCardSets(programIdStr);
				if(activeCardSets == null){
					status = new Status("111504", PropertyUtil.getErrorMessage(111504, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					return enrollResponse;
				}
				LoyaltyCardSet dCardBasedCardSet = null;
				for(LoyaltyCardSet cardSet : activeCardSets){
					if(cardSetIdStr == null){
						cardSetIdStr = ""+cardSet.getCardSetId();
					}
					else{
						cardSetIdStr += ","+cardSet.getCardSetId();
					}
					if(OCConstants.LOYALTY_CARD_GENERATION_TYPE_DYNAMIC.equals(cardSet.getCardGenerationType())) {
						dCardBasedCardSet = cardSet;
					}
				}
				logger.info("Active cardSet Ids : "+cardSetIdStr);
				
				loyaltyCard = LoyaltyCardFinder.findInventoryCard(programIdStr, cardSetIdStr, cardNumber, user.getUserId());
				boolean isDCard = false;
				//by pravendra
				if(loyaltyCard == null && isSuspendedProgram){ 
					logger.info("=================Loyalty card is null=============");
					String msg = PropertyUtil.getErrorMessage(111585, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".";
					status = new Status("111585", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					logger.info("Loyalty card not found");
					return enrollResponse;
				}
				if(loyaltyCard == null){
					if(!isDCardBasedExists) {
						String msg = PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".";
						status = new Status("111505", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						logger.info("Loyalty card not found");
						return enrollResponse;
					}
					else{
						LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
						LoyaltyCards loyaltyCardSus = loyaltyCardsDao.getLoyaltyCard(cardSetIdStr, cardNumber, user.getUserId());
						
						if(loyaltyCardSus!=null){
							String msg = PropertyUtil.getErrorMessage(111591, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".";
							status = new Status("111591", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
							logger.info("Loyalty card not found2");
							return enrollResponse;
						
						}
						

						if(dCardBasedCardSet != null) {
							loyaltyCard = insertDCard(cardNumber, enrollRequest.getMembership().getCardPin(), user, DCardBasedProgram, dCardBasedCardSet);
							if(loyaltyCard == null) {
								String msg = PropertyUtil.getErrorMessage(111584, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".";
								status = new Status("111584", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
								logger.info("Another enrollment request is being processed on the entered card-number-"+cardNumber);
								return enrollResponse;
								
							}
							isDCard = true;
							
						}
					}
				}
				
				for(LoyaltyProgram program : activePrograms){
					if(program.getProgramId().equals(loyaltyCard.getProgramId())){
						loyaltyProgram = program;
					}
				}
				LoyaltyCardSet cardSetObj = null;
				for(LoyaltyCardSet cardSet : activeCardSets){
					if(cardSet.getCardSetId().equals(loyaltyCard.getCardSetId())){
						cardSetObj = cardSet;
					}
				}
				LoyaltyProgramTier linkedTierObj = null;
				LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
				if(loyaltyCard.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_CARD_STATUS_ACTIVATED)){ // convert from G type card to GL type card
					
					ContactsLoyalty giftMembership = findMembershpByCard(loyaltyCard.getCardNumber(), loyaltyCard.getProgramId(), user.getUserId());
					if(giftMembership.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED)){
						String message = PropertyUtil.getErrorMessage(111517, OCConstants.ERROR_LOYALTY_FLAG)+" "+giftMembership.getCardNumber()+".";
						status = new Status("111517", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						return enrollResponse;
					}
					else if(giftMembership.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED)){
						String message = PropertyUtil.getErrorMessage(111539, OCConstants.ERROR_LOYALTY_FLAG)+" "+giftMembership.getCardNumber()+".";
						status = new Status("111539", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						return enrollResponse;
					}
					else if(giftMembership.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
						ContactsLoyalty destLoyalty = getDestMembershipIfAny(giftMembership);
						String maskedNum = Constants.STRING_NILL;
						if(destLoyalty != null) {
							maskedNum = Utility.maskNumber(destLoyalty.getCardNumber()+Constants.STRING_NILL);
						}
						String message = PropertyUtil.getErrorMessage(111578, OCConstants.ERROR_LOYALTY_FLAG)+maskedNum+".";
						status = new Status("111578", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						return enrollResponse;
					}
					
					String failedRequisites = validateProgramRequisites(enrollRequest, loyaltyProgram);
					logger.info("Mandatory failed fields:"+failedRequisites);
					if(failedRequisites != null){
						status = new Status("111509", PropertyUtil.getErrorMessage(111509, OCConstants.ERROR_LOYALTY_FLAG)+" "+failedRequisites+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						return enrollResponse;
					}
					
					if(loyaltyProgram.getUniqueMobileFlag() == 'Y'){
						
						contactLoyalty = findMembershpByPhone(enrollRequest.getCustomer().getPhone(), loyaltyProgram.getProgramId(), user.getUserId(), user.getCountryCarrier(),user.getUserOrganization().getMaxNumberOfDigits());
						if(contactLoyalty != null){
							status = new Status("111535", PropertyUtil.getErrorMessage(111535, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactLoyalty.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							Customer customer = findCustomerByCid(contactLoyalty.getContact(), user.getUserId());
							List<MatchedCustomer> customers = prepareMatchedCustomers(customer, ""+contactLoyalty.getCardNumber());
							enrollResponse = prepareEnrollmentResponse(responseHeader, null, customers, status);
							return enrollResponse;
						}
					}
					
					LoyaltyProgramExclusion loyaltyExclusion = getLoyaltyExclusion(loyaltyProgram.getProgramId());
					if(loyaltyExclusion != null){
						status = validateStoreNumberExclusion(enrollRequest, loyaltyProgram, loyaltyExclusion);
						if(status != null){
							enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
							//updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
							return enrollResponse;
						}
					}
					
					if(cardSetObj.getLinkedTierLevel() > 0){
						String type = "Tier "+cardSetObj.getLinkedTierLevel();
						linkedTierObj = loyaltyProgramTierDao.getTierByPrgmAndType(loyaltyProgram.getProgramId(), type);
						if(linkedTierObj == null){
							//active incomplete status response...
							if(isDCard) {
								deleteDCard(loyaltyCard);
							}
							else{
								
								updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
							}
							status = new Status("111555", PropertyUtil.getErrorMessage(111555, OCConstants.ERROR_LOYALTY_FLAG),
									OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
							return enrollResponse;
						}
					}
					
					logger.info("Loyalty card is already activated ");
					return convertGiftToLoyalty(enrollRequest, responseHeader, loyaltyProgram, loyaltyCard, mode, user, mlList,linkedTierObj, loyaltyProgram.getMembershipType());
					
				}
				
				if(loyaltyCard.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_CARD_STATUS_ENROLLED)){
					String msg = PropertyUtil.getErrorMessage(111506, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".";
					status = new Status("111506", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					Customer customer = findCustomer(loyaltyCard.getCardNumber(), loyaltyCard.getProgramId(), user.getUserId());
					List<MatchedCustomer> customers = prepareMatchedCustomers(customer, loyaltyCard.getCardNumber());
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, customers, status);
					logger.info("Loyalty card is already enrolled ");
					return enrollResponse;
				}
				
				String failedRequisites = validateProgramRequisites(enrollRequest, loyaltyProgram);
				logger.info("Mandatory failed fields:"+failedRequisites);
				if(failedRequisites != null){
					status = new Status("111509", PropertyUtil.getErrorMessage(111509, OCConstants.ERROR_LOYALTY_FLAG)+" "+failedRequisites+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					if(isDCard) {
						deleteDCard(loyaltyCard);
					}
					else{
						
						updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
					}
					return enrollResponse;
				}
				
				if(loyaltyProgram.getUniqueMobileFlag() == 'Y'){
					contactLoyalty = findMembershpByPhone(enrollRequest.getCustomer().getPhone(), loyaltyProgram.getProgramId(), user.getUserId(), user.getCountryCarrier(),user.getUserOrganization().getMaxNumberOfDigits());
					if(contactLoyalty != null){
						status = new Status("111535", PropertyUtil.getErrorMessage(111535, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactLoyalty.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						Customer customer = findCustomerByCid(contactLoyalty.getContact(), user.getUserId());
						List<MatchedCustomer> customers = prepareMatchedCustomers(customer, ""+contactLoyalty.getCardNumber());
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, customers, status);
						if(isDCard) {
							deleteDCard(loyaltyCard);
						}
						else{
							
							updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
						}
						return enrollResponse;
					}
				}
				
				LoyaltyProgramExclusion loyaltyExclusion = getLoyaltyExclusion(loyaltyProgram.getProgramId());
				if(loyaltyExclusion != null){
					status = validateStoreNumberExclusion(enrollRequest, loyaltyProgram, loyaltyExclusion);
					if(status != null){
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						if(isDCard) {
							deleteDCard(loyaltyCard);
						}
						else{
							
							updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
						}
						return enrollResponse;
					}
				}
				
				if(cardSetObj.getLinkedTierLevel() > 0){
					String type = "Tier "+cardSetObj.getLinkedTierLevel();
					linkedTierObj = loyaltyProgramTierDao.getTierByPrgmAndType(loyaltyProgram.getProgramId(), type);
					if(linkedTierObj == null){
						//active incomplete status response...
						if(isDCard) {
							deleteDCard(loyaltyCard);
						}
						else{
							
							updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
						}
						status = new Status("111555", PropertyUtil.getErrorMessage(111555, OCConstants.ERROR_LOYALTY_FLAG),
								OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						return enrollResponse;
					}
				}
				
				return createMembership(enrollRequest, responseHeader, loyaltyProgram, loyaltyCard, mode, user, mlList, 
						OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD, linkedTierObj);
				
			}
			else if(enrollRequest.getMembership().getIssueCardFlag() != null && enrollRequest.getMembership().getIssueCardFlag().length() > 0
					&& enrollRequest.getMembership().getIssueCardFlag().equalsIgnoreCase("Y")){
				
				logger.info("Enrolling with auto issue card ..."+enrollRequest.getMembership().getIssueCardFlag());
				
				loyaltyProgram = findDefaultProgram(user.getUserId());
				if(loyaltyProgram == null || !loyaltyProgram.getStatus().equals(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)){
					status = new Status("111503", PropertyUtil.getErrorMessage(111503, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					return enrollResponse;
				}
				
				List<LoyaltyCardSet> activeCardSets = findLoyaltyActiveCardSet(loyaltyProgram.getProgramId());
				if(activeCardSets == null){
					status = new Status("100102", PropertyUtil.getErrorMessage(100102, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					return enrollResponse;
				}
				
				String cardSetIdStr = null;
				for(LoyaltyCardSet cardSet : activeCardSets){
					logger.info("cardSetIdStr = "+cardSetIdStr);
					if(cardSetIdStr == null){
						cardSetIdStr = ""+cardSet.getCardSetId();
					}
					else{
						cardSetIdStr += ","+cardSet.getCardSetId();
					}
				}
				
				loyaltyCard = LoyaltyCardFinder.findInventoryCard(""+loyaltyProgram.getProgramId(), cardSetIdStr, null, user.getUserId());
				//loyaltyCard = findInventoryCard(loyaltyProgram.getProgramId(), user.getUserOrganization().getUserOrgId(), activeCardSets);
				if(loyaltyCard == null){
					status = new Status("100102", PropertyUtil.getErrorMessage(100102, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					return enrollResponse;
				}
				logger.info("card = "+loyaltyCard.getCardNumber()+" status = "+loyaltyCard.getStatus());
				
				String failedRequisites = validateProgramRequisites(enrollRequest, loyaltyProgram);
				if(failedRequisites != null){
					status = new Status("111509", PropertyUtil.getErrorMessage(111509, OCConstants.ERROR_LOYALTY_FLAG)+" "+failedRequisites+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
					return enrollResponse;
				}
				logger.info("failedRequisites == "+failedRequisites);
				if(loyaltyProgram.getUniqueMobileFlag() == 'Y'){
					contactLoyalty = findMembershpByPhone(enrollRequest.getCustomer().getPhone(), loyaltyProgram.getProgramId(), user.getUserId(), user.getCountryCarrier(),user.getUserOrganization().getMaxNumberOfDigits());
					if(contactLoyalty != null){
						status = new Status("111535", PropertyUtil.getErrorMessage(111535, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactLoyalty.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						Customer customer = findCustomerByCid(contactLoyalty.getContact(), user.getUserId());
						List<MatchedCustomer> customers = prepareMatchedCustomers(customer, ""+contactLoyalty.getCardNumber());
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, customers, status);
						updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
						return enrollResponse;
					}
				}
				
				LoyaltyProgramExclusion loyaltyExclusion = getLoyaltyExclusion(loyaltyProgram.getProgramId());
				if(loyaltyExclusion != null){
					status = validateStoreNumberExclusion(enrollRequest, loyaltyProgram, loyaltyExclusion);
					if(status != null){
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
						return enrollResponse;
					}
				}
				
				return createMembership(enrollRequest, responseHeader, loyaltyProgram, loyaltyCard, mode, user, mlList, OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD, null);
				
			}
			else if(enrollRequest.getMembership().getPhoneNumber() != null && enrollRequest.getMembership().getPhoneNumber().trim().length() > 0){
				
				logger.info(">>>Enrollment through mobile phone number : "+enrollRequest.getMembership().getPhoneNumber());
				String validStatus = LoyaltyProgramHelper.validateMembershipMobile(enrollRequest.getMembership().getPhoneNumber().trim());
				if(OCConstants.LOYALTY_MEMBERSHIP_MOBILE_INVALID.equals(validStatus)){
					status = new Status("111554", PropertyUtil.getErrorMessage(111554, OCConstants.ERROR_LOYALTY_FLAG)+" "+enrollRequest.getMembership().getPhoneNumber().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null,status);
					return enrollResponse;
				}
				String phoneParse = Utility.phoneParse(enrollRequest.getMembership().getPhoneNumber().trim(),user!=null ? user.getUserOrganization() : null );
				if(phoneParse == null){
					status = new Status("111554", PropertyUtil.getErrorMessage(111554, OCConstants.ERROR_LOYALTY_FLAG)+" "+enrollRequest.getMembership().getPhoneNumber().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null,status);
					return enrollResponse;
				}
				enrollRequest.getMembership().setPhoneNumber(phoneParse);
				
				loyaltyProgram = findMobileBasedProgram(user.getUserId());
				if(loyaltyProgram == null){
					status = new Status("111507", PropertyUtil.getErrorMessage(111507, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null,status);
					return enrollResponse;
				}
				
				contactLoyalty = findContactLoyaltyByPhone(enrollRequest.getMembership().getPhoneNumber().trim(), loyaltyProgram.getProgramId(), user.getUserId(),user.getCountryCarrier(),user.getUserOrganization().getMaxNumberOfDigits());
				if(contactLoyalty != null){
					String msg = PropertyUtil.getErrorMessage(111508, OCConstants.ERROR_LOYALTY_FLAG)+" "+enrollRequest.getMembership().getPhoneNumber().trim()+".";
					status = new Status("111508", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					Customer customer = findCustomerByCid(contactLoyalty.getContact(), user.getUserId());
					List<MatchedCustomer> customers = prepareMatchedCustomers(customer, ""+contactLoyalty.getCardNumber());
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, customers, status);
					return enrollResponse;
				}
				
				String failedRequisites = validateProgramRequisites(enrollRequest, loyaltyProgram);
				if(failedRequisites != null){
					status = new Status("111509", PropertyUtil.getErrorMessage(111509, OCConstants.ERROR_LOYALTY_FLAG)+" "+failedRequisites+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					return enrollResponse;
				}
				
				//if(loyaltyProgram.getUniqueMobileFlag() == 'Y'){
					
					if(enrollRequest.getCustomer().getPhone() != null && !enrollRequest.getCustomer().getPhone().trim().isEmpty()
							&& !enrollRequest.getMembership().getPhoneNumber().trim().equals(enrollRequest.getCustomer().getPhone().trim())){
						status = new Status("111541", PropertyUtil.getErrorMessage(111541, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						return enrollResponse;
					}
					
					/*contactLoyalty = findMembershpByPhone(enrollRequest.getMembership().getPhoneNumber(), loyaltyProgram.getProgramId(), user.getUserId());
					if(contactLoyalty != null){
						status = new Status("111535", PropertyUtil.getErrorMessage(111535, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						return enrollResponse;
					}*/
				//}
					
					LoyaltyProgramExclusion loyaltyExclusion = getLoyaltyExclusion(loyaltyProgram.getProgramId());
					if(loyaltyExclusion != null){
						status = validateStoreNumberExclusion(enrollRequest, loyaltyProgram, loyaltyExclusion);
						if(status != null){
							enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
							return enrollResponse;
						}
					}	
				
				
				return createMembership(enrollRequest, responseHeader, loyaltyProgram, null, mode, user, mlList, OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE, null);
				
			}
			else{
				status = new Status("111502", PropertyUtil.getErrorMessage(111502, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				enrollResponse = prepareEnrollmentResponse(responseHeader, null, null,status);
				return enrollResponse;
			}
			
		}catch(Exception e){
			logger.error("Exception in processing enrollment request...", e);
			status = new Status("101000", PropertyUtil.getErrorMessage(101000, OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
			return enrollResponse;
		}
	}
	
	private LoyaltyCards insertDCard(String cardNumber, String cardPin, Users user, 
								LoyaltyProgram dCardBasedProgram, LoyaltyCardSet dCardBasedCardSet) throws Exception {
		
		LoyaltyCards loyaltyCard = findCardByCardNumber(dCardBasedProgram.getProgramId().toString(), dCardBasedCardSet.getCardSetId().toString(), cardNumber, dCardBasedProgram.getUserId());
		
		
		if(loyaltyCard != null && loyaltyCard.getStatus().equals(OCConstants.LOYALTY_CARD_STATUS_SELECTED)){
			logger.info("Usergenerated card with selected status");
			return null;
		}else if (loyaltyCard != null){
			return loyaltyCard;
		}else{ 
			logger.info("inserting user generated card");
			updateCardSetQuantity(dCardBasedCardSet);
			loyaltyCard = new LoyaltyCards();
			loyaltyCard.setProgramId(dCardBasedProgram.getProgramId());
			loyaltyCard.setCardSetId(dCardBasedCardSet.getCardSetId());
			loyaltyCard.setCardNumber(cardNumber);
			loyaltyCard.setCardPin(cardPin);
			loyaltyCard.setOrgId(user.getUserOrganization().getUserOrgId());
			loyaltyCard.setUserId(user.getUserId());
			loyaltyCard.setRegisteredFlag('N');
			loyaltyCard.setStatus(OCConstants.LOYALTY_CARD_STATUS_SELECTED);
			updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_SELECTED);
			loyaltyCard.setStatus(OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
			return loyaltyCard;
		}
		
	}

	private LoyaltyCards findCardByCardNumber(String programIdStr, String cardSetIdStr, String cardNumber, Long userId) throws Exception {
		LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		return loyaltyCardsDao.findCardByProgram(programIdStr, cardSetIdStr, cardNumber, userId);
	}
	private void updateCardSetQuantity(LoyaltyCardSet cardSet) throws Exception{
		LoyaltyCardSetDao loyaltyCardSetDao = (LoyaltyCardSetDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
		LoyaltyCardSetDaoForDML loyaltyCardSetDaoForDML = (LoyaltyCardSetDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARD_SET_DAO_FOR_DML);
		//loyaltyCardSetDao.updateCardSetQuantity(cardSet.getCardSetId(), 1l);
		loyaltyCardSetDaoForDML.updateCardSetQuantity(cardSet.getCardSetId(), 1l);
	}
	private Status validateEnrollmentJsonData(LoyaltyEnrollRequest enrollRequest) throws Exception{
		logger.info("Entered validateEnrollmentJsonData method >>>>");
		
		Status status = null;
		if(enrollRequest == null ){
			status = new Status(
					"101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(enrollRequest.getUser() == null){
			status = new Status(
					"101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(enrollRequest.getMembership() == null){
			status = new Status(
					"101004", PropertyUtil.getErrorMessage(101004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(enrollRequest.getUser().getUserName() == null || enrollRequest.getUser().getUserName().trim().length() <=0 || 
				enrollRequest.getUser().getOrganizationId() == null || enrollRequest.getUser().getOrganizationId().trim().length() <=0 || 
				enrollRequest.getUser().getToken() == null || enrollRequest.getUser().getToken().trim().length() <=0) {
			status = new Status("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
				
		return status;
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
		Users user = usersDao.findUserByToken(completeUserName, userToken);
		return user;
	}
	
	private LoyaltyEnrollResponse prepareEnrollmentResponse(ResponseHeader header, MembershipResponse membershipResponse,
			List<MatchedCustomer> matchedCustomers, Status status) throws BaseServiceException {
		LoyaltyEnrollResponse enrollResponse = new LoyaltyEnrollResponse();
		enrollResponse.setHeader(header);
		
		if(membershipResponse == null){
			membershipResponse = new MembershipResponse();
			membershipResponse.setCardNumber("");
			membershipResponse.setCardPin("");
			membershipResponse.setExpiry("");
			membershipResponse.setPhoneNumber("");
			membershipResponse.setTierLevel("");
			membershipResponse.setTierName("");
		}
		if(matchedCustomers == null){
			matchedCustomers = new ArrayList<MatchedCustomer>();
		}
		enrollResponse.setMembership(membershipResponse);
		enrollResponse.setMatchedCustomers(matchedCustomers);
		enrollResponse.setStatus(status);
		return enrollResponse;
	}
	
	/**
	 * prepares contact object with the enroll request customer info data
	 * 
	 * @param customerInfo
	 * @return
	 * @throws Exception
	 */
	private Contacts prepareContactFromJsonData(LoyaltyEnrollRequest enrollRequest, Long userId, LoyaltyProgram program) throws Exception {
		
		logger.info("Entered prepareContactFromJsonData method >>>>>");
		
		Customer customerInfo = enrollRequest.getCustomer();
		Contacts inputContact = new Contacts();
		if(customerInfo.getCustomerId() != null && customerInfo.getCustomerId().trim().length() > 0) {
			inputContact.setExternalId(customerInfo.getCustomerId().trim());
			logger.info("customer id: "+customerInfo.getCustomerId());
		}
		if(customerInfo.getEmailAddress() != null && customerInfo.getEmailAddress().trim().length() > 0) {
			inputContact.setEmailId(customerInfo.getEmailAddress().trim());
			logger.info("email id: "+customerInfo.getEmailAddress());
		}
		if(customerInfo.getFirstName() != null && customerInfo.getFirstName().trim().length() > 0) {
			inputContact.setFirstName(customerInfo.getFirstName().trim());
		}
		if(customerInfo.getLastName() != null && customerInfo.getLastName().trim().length() > 0) {
			inputContact.setLastName(customerInfo.getLastName().trim());
		}
		if(customerInfo.getAddressLine1() != null && customerInfo.getAddressLine1().trim().length() > 0) {
			inputContact.setAddressOne(customerInfo.getAddressLine1().trim());
		}
		if(customerInfo.getAddressLine2() != null && customerInfo.getAddressLine2().trim().length() > 0) {
			inputContact.setAddressTwo(customerInfo.getAddressLine2().trim());
		}
		if(customerInfo.getCity() != null && customerInfo.getCity().trim().length() > 0) {
			inputContact.setCity(customerInfo.getCity().trim());
		}
		if(customerInfo.getState() != null && customerInfo.getState().trim().length() > 0) {
			inputContact.setState(customerInfo.getState().trim());
		}
		if(customerInfo.getCountry() != null && customerInfo.getCountry().trim().length() > 0) {
			inputContact.setCountry(customerInfo.getCountry().trim());
		}
		if(customerInfo.getPostal() != null && customerInfo.getPostal().trim().length() > 0) {
			inputContact.setZip(customerInfo.getPostal().trim());
		}
		if(customerInfo.getBirthday() != null && customerInfo.getBirthday().trim().length() > 0) {
			Calendar cal = MyCalendar.dateString2Calendar(customerInfo.getBirthday().trim());
			inputContact.setBirthDay(cal);
		}
		if(customerInfo.getAnniversary() != null && customerInfo.getAnniversary().trim().length() > 0) {
			Calendar cal = MyCalendar.dateString2Calendar(customerInfo.getAnniversary().trim());
			inputContact.setAnniversary(cal);
		}
		if(customerInfo.getGender() != null && customerInfo.getGender().trim().length() > 0) {
			inputContact.setGender(customerInfo.getGender().trim());
		}	
		
		if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE.equals(program.getMembershipType()) && 
				enrollRequest.getMembership().getPhoneNumber() != null && enrollRequest.getMembership().getPhoneNumber().trim().length() > 0) {
			inputContact.setMobilePhone(enrollRequest.getMembership().getPhoneNumber().trim());
			logger.info("phone= "+customerInfo.getPhone());
		}
		else if(customerInfo.getPhone() != null && customerInfo.getPhone().trim().length() > 0) {
			inputContact.setMobilePhone(customerInfo.getPhone());
			logger.info("phone= "+customerInfo.getPhone());
		}
		
		logger.info("Exited prepareContactFromJsonData method >>>>>");
		return inputContact;
	}
	
	

	private String performMobileOptin(Contacts contact, Users currentUser) throws Exception {
		SMSSettingsDao smsSettingsDao  = null;
		//UsersDao usersDao = null;
		UsersDaoForDML usersDaoForDML = null;
		ContactsDaoForDML contactsDaoForDML = null;
		try{
			smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			//usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
			contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
		}catch(Exception e){
			logger.error("Exception in getting smssettingsdao or usersdao", e);
		}
			
			SMSSettings smsSettings = null;
			if(SMSStatusCodes.smsProgramlookupOverUserMap.get(currentUser.getCountryType())) smsSettings = smsSettingsDao.findByUser(currentUser.getUserId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
			else  smsSettings = smsSettingsDao.findByOrg(currentUser.getUserOrganization().getUserOrgId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
		
			//SMSSettings smsSettings = smsSettingsDao.findByUser(currentUser.getUserId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN );
			
			if(smsSettings == null) {
				contact.setMobileOptin(false);
				return Constants.CON_MOBILE_STATUS_ACTIVE;
				
			}
			Users user = smsSettings.getUserId();
			OCSMSGateway ocsmsGateway = GatewayRequestProcessHelper.getOcSMSGateway(user, 
					SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
			if(ocsmsGateway == null) {
				
				return Constants.CON_MOBILE_STATUS_ACTIVE;
			}
			
			currentUser = smsSettings.getUserId();//to avoid lazy=false from contacts
			//do only when the existing phone number is not same with the entered
			byte optin = 0;
			String phone = contact.getMobilePhone();
			String mobileStatus = Constants.CON_MOBILE_STATUS_ACTIVE;
			if(contact.getOptinMedium() != null) {
				
				if(contact.getOptinMedium().equalsIgnoreCase(Constants.CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY) ) {
					optin = 1;
				}
				else if(contact.getOptinMedium().startsWith(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM) ) {
					optin = 2;
				}
				else if(contact.getOptinMedium().equalsIgnoreCase(Constants.CONTACT_OPTIN_MEDIUM_POS) ) {
					optin = 4;
				}
			}
			
			Users contactOwner = contact.getUsers();
			Byte userOptinVal =	smsSettings.getOptInMedium();
			
			userOptinVal = ( SMSStatusCodes.userOptinMediumMap.get(contactOwner.getCountryType()) && contactOwner.getOptInMedium() != null) ? 
					contactOwner.getOptInMedium() : userOptinVal;
					CaptiwayToSMSApiGateway captiwayToSMSApiGateway = (CaptiwayToSMSApiGateway)ServiceLocator.getInstance().getServiceById(OCConstants.CAPTIWAYTOSMSAPIGATEWAY);
					
			if(smsSettings.isEnable() && 
					userOptinVal != null && 
					(userOptinVal.byteValue() & optin ) > 0 ) {	
				//TODO after the above todo done consider only one among these two conditions on contact
				if(contact.getLastSMSDate() == null && contact.isMobileOptin() != true) {
					
					mobileStatus = Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
					contact.setMobileStatus(mobileStatus);
					contact.setLastSMSDate(Calendar.getInstance());
					if(!ocsmsGateway.isPostPaid() && !captiwayToSMSApiGateway.getBalance(ocsmsGateway, 1)) {
						
						logger.debug("low credits with clickatell");
						return mobileStatus;
					}
					
					if( (  (currentUser.getSmsCount() == null ? 0 : currentUser.getSmsCount()) - (currentUser.getUsedSmsCount() == null ? 0 : currentUser.getUsedSmsCount() ) ) >=  1) {
						
						String msgContent = smsSettings.getAutoResponse();
						if(msgContent != null) {
							if(SMSStatusCodes.optOutFooterMap.get(currentUser.getCountryType())){
								
								msgContent = smsSettings.getMessageHeader() + " "+ msgContent;
							}
							//msgContent = smsSettings.getMessageHeader() == null ? Constants.STRING_NILL : smsSettings.getMessageHeader() + " "+ msgContent;
						}
						
						mobileStatus = captiwayToSMSApiGateway.sendSingleMobileDoubleOptin(ocsmsGateway, 
								smsSettings.getSenderId(), phone, msgContent, smsSettings.getUserId());
	
						if(mobileStatus == null) {
							
							mobileStatus = Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
						}
						
						if(!mobileStatus.equals(Constants.CON_MOBILE_STATUS_OPTIN_PENDING)) {
							contactsDaoForDML.updatemobileStatus(phone, mobileStatus, currentUser);
						}
						
						/*currentUser.setUsedSmsCount( (currentUser.getUsedSmsCount() == null ? 0 : currentUser.getUsedSmsCount()) +1);
						usersDao.saveOrUpdate(currentUser);*/
						//usersDao.updateUsedSMSCount(currentUser.getUserId(), 1);
						usersDaoForDML.updateUsedSMSCount(currentUser.getUserId(), 1);
						
						/**
						 * Update Sms Queue
						 */
						SmsQueueHelper smsQueueHelper = new SmsQueueHelper();
						smsQueueHelper.updateSMSQueue(phone,msgContent,Constants.SMS_MSG_TYPE_OPTIN, user, smsSettings.getSenderId());
					}else {
						logger.debug("low credits with user...");
						return mobileStatus;
					}
				}//if
			}//if
			
			else{
				
				if(contact.getMobilePhone() != null) {
					mobileStatus = Constants.CON_MOBILE_STATUS_ACTIVE;
					contact.setMobileStatus(mobileStatus);
				}
			}
			
			return mobileStatus;
	}
	
	
	private void sendWelcomeEmail(Contacts contact, MailingList mailingList, Users user) {
		logger.info("Entered sendWelcomeEmail method >>>>>");
		//to send the loyalty related email
		EmailQueueDao emailQueueDao = null;
		EmailQueueDaoForDML emailQueueDaoForDML = null;
		CustomTemplatesDao customTemplatesDao = null;
		try{
		emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName("emailQueueDao");
		emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("emailQueueDaoForDML");
		
		customTemplatesDao = (CustomTemplatesDao)ServiceLocator.getInstance().getDAOByName("customTemplatesDao");
		}catch(Exception e){
			logger.error("Exception in sending welcome email", e);
			return ;
		}
		CustomTemplates custTemplate = null;
		  String message = PropertyUtil.getPropertyValueFromDB("welcomeMsgTemplate");
		  
		  if(mailingList.getWelcomeCustTempId() != null) {
			   
			  custTemplate = customTemplatesDao.findCustTemplateById(mailingList.getWelcomeCustTempId());
			  if(custTemplate != null) {
				  		if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
						  message = custTemplate.getHtmlText();
						}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
						  try {
						  MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
						  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
						  if(myTemplates != null) message = myTemplates.getContent();
						  }catch (Exception e) {
							logger.info(e);
						}
				  }
			  }
		  }
		  
		  message = message.replace("[OrganisationName]", user.getUserOrganization().getOrganizationName())
				  .replace("[senderReplyToEmailID]", user.getEmailId());
		  
		  EmailQueue testEmailQueue = new EmailQueue(mailingList.getWelcomeCustTempId(),Constants.EQ_TYPE_WELCOME_MAIL, message, "Active",
				  				contact.getEmailId(), user, MyCalendar.getNewCalendar(), "Welcome Mail",
				  				null, contact.getFirstName(), null, contact.getContactId());
				
			//testEmailQueue.setChildEmail(childEmail);
			logger.info("testEmailQueue"+testEmailQueue.getChildEmail());
			
			//emailQueueDao.saveOrUpdate(testEmailQueue);
			emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
		logger.info("Exited sendWelcomeEmail method >>>>>");
		
	}//sendWelcomeEmail
	
	private LoyaltyProgram findDefaultProgram(Long userId) throws Exception {
		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		return loyaltyProgramDao.findDefaultProgramByUserId(userId);
	}
	
	private List<LoyaltyCardSet> findLoyaltyActiveCardSet(Long programId) throws Exception {
		LoyaltyCardSetDao loyaltyCardSetDao = (LoyaltyCardSetDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
		return loyaltyCardSetDao.findActiveByProgramId(programId);
	}
	
	private LoyaltyProgram findMobileBasedProgram(Long userId) throws Exception {
		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		return loyaltyProgramDao.findProgramByUserId(userId, OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE, OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE);
	}
	
	private ContactsLoyalty findContactLoyaltyByPhone(String mobilePhone, Long programId, Long userId,short countryCarrier,int maxDigits ) throws Exception{
		if(mobilePhone!=null && mobilePhone.trim().length() !=0){
			mobilePhone = mobilePhone.trim();
			//UserOrganization organization=  user!=null ? user.getUserOrganization() : null ;
			//phone = phoneParse(phone, organization);
			if(mobilePhone != null && mobilePhone.startsWith(countryCarrier+"")
					 && mobilePhone.length() >maxDigits) {
				mobilePhone =  mobilePhone.replaceFirst(countryCarrier+"", "");
				//logger.info("phone is============>"+phone);
			}
			try {
				mobilePhone= Long.parseLong(mobilePhone)+"";
				
			} catch (Exception e) {
				logger.info("OOPs error ");
			}
			}
		String mobileWithCarrier= countryCarrier+mobilePhone;
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		return contactsLoyaltyDao.findByMembershipNoAndUserId(Long.valueOf(mobilePhone),Long.valueOf(mobileWithCarrier), programId, userId);
	}
	
	
	private ContactsLoyalty findMembershpByPhone(String mobilePhone, Long programId, Long userId,short countryCarrier,int maxDigits) throws Exception{
		
		mobilePhone=mobilePhone.replaceAll("[- ()]", "");//APP-117
		if(mobilePhone!=null && mobilePhone.trim().length() !=0){
			mobilePhone = mobilePhone.trim();
			//UserOrganization organization=  user!=null ? user.getUserOrganization() : null ;
			//phone = phoneParse(phone, organization);
			if(mobilePhone != null && mobilePhone.startsWith(countryCarrier+"")
					 && mobilePhone.length() >maxDigits) {
				mobilePhone =  mobilePhone.replaceFirst(countryCarrier+"", "");
				//logger.info("phone is============>"+phone);
			}
			try {
				mobilePhone= Long.parseLong(mobilePhone)+"";
				
			} catch (Exception e) {
				logger.info("OOPs error ");
			}
			}
			
		String mobileWithCarrier= countryCarrier+mobilePhone;
		ContactsLoyalty loyalty = null;
		ContactsLoyaltyDao contactLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		loyalty = contactLoyaltyDao.findByMobilePhone(mobilePhone,mobileWithCarrier, programId, userId);
		return loyalty;
	}
	
	private ContactsLoyalty findMembershpByCard(String cardNumber, Long programId, Long userId) throws Exception{
		
		ContactsLoyalty loyalty = null;
		ContactsLoyaltyDao contactLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		loyalty = contactLoyaltyDao.findByProgram(cardNumber, programId, userId);
		return loyalty;
	}
	
	private ContactsLoyalty prepareLoyaltyMembership(String mbershipNumber, String mbershipType, String cardpin, String phone, 
			String optInMedium, String subsidiary, String storeNumber, String mode, LoyaltyProgram program, 
			LoyaltyProgramTier linkedTierObj, Long cardSetId,String empId,String termId, String sourceType) throws Exception {
		//logger.info("Entered prepareContactsLoyaltyObject >>>>>");
		ContactsLoyalty contactLoyalty = new ContactsLoyalty();
		contactLoyalty.setCardNumber(mbershipNumber);
		contactLoyalty.setMembershipType(mbershipType);
		contactLoyalty.setMobilePhone(phone);
		contactLoyalty.setCardPin(cardpin == null ? "" : cardpin);
		contactLoyalty.setCreatedDate(Calendar.getInstance());
//		contactLoyalty.setOptinMedium(optInMedium);
		contactLoyalty.setContactLoyaltyType(optInMedium);
		contactLoyalty.setSourceType(sourceType);
		contactLoyalty.setSubsidiaryNumber(subsidiary);
		contactLoyalty.setPosStoreLocationId(storeNumber == null || storeNumber.trim().isEmpty() ? null : storeNumber);
		contactLoyalty.setMembershipStatus(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE);
		contactLoyalty.setMode(mode);
		contactLoyalty.setOptinDate(Calendar.getInstance());
		contactLoyalty.setProgramId(program.getProgramId());
		contactLoyalty.setCardSetId(cardSetId);
		contactLoyalty.setEmpId(empId!=null && !empId.trim().isEmpty() ? empId.trim():null);
		contactLoyalty.setTerminalId(termId!=null && !termId.trim().isEmpty() ? termId.trim():null);
		if(linkedTierObj != null){
			contactLoyalty.setProgramTierId(linkedTierObj.getTierId());
		}
		
		contactLoyalty.setServiceType(OCConstants.LOYALTY_SERVICE_TYPE_OC);
		return contactLoyalty;
	}
	
	
	private LoyaltyProgramTier findTier(Long contactId, Long userId, Long loyaltyId, List<LoyaltyProgramTier> tiersList, Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap) throws Exception {


		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		ContactsLoyalty contactsLoyalty = null;
		contactsLoyalty = contactsLoyaltyDao.findByContactId(userId, contactId);
		/*LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);

		List<LoyaltyProgramTier> tiersList = loyaltyProgramTierDao.fetchTiersByProgramId(programId);
		if (tiersList == null || tiersList.size() <= 0) {
			logger.info("Tiers list is empty...");
			return null;
		}
		else if (tiersList.size() >= 1) {//sort tiers by tiertype i.e Tier 1, Tier 2, and so on.
			Collections.sort(tiersList, new Comparator<LoyaltyProgramTier>() {
				@Override
				public int compare(LoyaltyProgramTier o1, LoyaltyProgramTier o2) {

					int num1 = Integer.valueOf(o1.getTierType().substring(5)).intValue();
					int num2 = Integer.valueOf(o2.getTierType().substring(5)).intValue();
					if(num1 < num2){
						return -1;
					}
					else if(num1 == num2){
						return 0;
					}
					else{
						return 1;
					}
				}
			});
		}

		for(LoyaltyProgramTier tier : tiersList) {//testing purpose
			logger.info("tier level : "+tier.getTierType());
		}

		if(!OCConstants.LOYALTY_PROGRAM_TIER1.equals(tiersList.get(0).getTierType())){// if tier 1 not exist return null
			logger.info("selected tier...null...tier1 not found");
			return null;
		}*/

		/*//Prepare eligible tiers map
		Iterator<LoyaltyProgramTier> iterTier = tiersList.iterator();
		Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap = new LinkedHashMap<LoyaltyProgramTier, LoyaltyProgramTier>();
		LoyaltyProgramTier prevtier = null;
		LoyaltyProgramTier nexttier = null;

		while(iterTier.hasNext()){
			nexttier = iterTier.next();
			if(OCConstants.LOYALTY_PROGRAM_TIER1.equals(nexttier.getTierType())){
				eligibleMap.put(nexttier, null);
			}
			else{
				if((Integer.valueOf(prevtier.getTierType().substring(5))+1) 
						== Integer.valueOf(nexttier.getTierType().substring(5)) && prevtier.getTierUpgdConstraintValue() != null){
					eligibleMap.put(nexttier, prevtier);
					logger.info("eligible tier ="+nexttier.getTierType()+" upgdconstrant value = "+prevtier.getTierUpgdConstraintValue());
				}
			}
			prevtier = nexttier;
		}*/
		
		if(OCConstants.LOYALTY_LIFETIME_POINTS.equals(tiersList.get(0).getTierUpgdConstraint())){
			logger.info("tier condition on :"+OCConstants.LOYALTY_LIFETIME_POINTS);
			return tiersList.get(0);
		}
		else if(contactId == null){
			logger.info("contactId is null and selected tier..."+tiersList.get(0).getTierType());
			return tiersList.get(0);
		}
		else if(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE.equals(tiersList.get(0).getTierUpgdConstraint())){
			logger.info("tier condition on :"+OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE);
			
			ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);				

			//List<Map<String, Object>> contactPurcahseList = contactsDao.findContactPurchaseDetails(userId, contactId);
			Double totPurchaseValue = null;
			/*if(contactPurcahseList != null && contactPurcahseList.size() == 1) {
				for (Map<String, Object> eachMap : contactPurcahseList) {
					if(eachMap.containsKey("tot_purchase_amt")){
						totPurchaseValue = Double.valueOf(eachMap.get("tot_purchase_amt") != null ? eachMap.get("tot_purchase_amt").toString() : "0.00");
						logger.info("purchase value = "+totPurchaseValue);
					}
				}
			}*/
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			//totPurchaseValue = Double.valueOf(loyaltyTransactionChildDao.getLifeTimeLoyaltyPurchaseValue(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId()));
			totPurchaseValue=LoyaltyProgramHelper.getLPV(contactsLoyalty);//contactsLoyalty.getLifeTimePurchaseValue();

			logger.info("purchase value = "+totPurchaseValue);

			
			//if(contactPurcahseList == null || totPurchaseValue == null || totPurchaseValue <= 0){
			if(totPurchaseValue == null || totPurchaseValue <= 0){
				logger.info("purchase value is empty...");
				return tiersList.get(0);
			}
			else{

				Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
				LoyaltyProgramTier prevKeyTier = null;
				LoyaltyProgramTier nextKeyTier = null;
				while(it.hasNext()){
					nextKeyTier = it.next();
					logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
					logger.info("-------------tiersList.get(0)::"+tiersList.get(0).getTierType());
					if(OCConstants.LOYALTY_PROGRAM_TIER1.equalsIgnoreCase(nextKeyTier.getTierType())){
						prevKeyTier = nextKeyTier;
						continue;
					}
					if(totPurchaseValue > 0 && totPurchaseValue < eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
						if(prevKeyTier == null){
							logger.info("selected tier is currTier..."+tiersList.get(0).getTierType());
							return tiersList.get(0);
						}
						logger.info("selected tier..."+prevKeyTier.getTierType());
						return prevKeyTier;
					}
					else if (totPurchaseValue > 0 && totPurchaseValue >= eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue() && !it.hasNext()) {
						logger.info("selected tier..."+nextKeyTier.getTierType());
						return nextKeyTier;
					}
					prevKeyTier = nextKeyTier;
				}
				return tiersList.get(0);
			}
		}
		else if(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE.equals(tiersList.get(0).getTierUpgdConstraint())){
			
			//create a temp object and return it to caller. caller should handle this temp object.
			LoyaltyProgramTier tempTier = new LoyaltyProgramTier();
			tempTier.setTierType("Pending");
			return tempTier;
			
		}
		else{
			return null;
		}

	}
	private void deleteDCard(LoyaltyCards loyaltyCard) throws Exception{
		
		LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		LoyaltyCardsDaoForDML loyaltyCardsDaoForDML = (LoyaltyCardsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);
		//loyaltyCardsDao.deleteBy(loyaltyCard.getCardId());
		loyaltyCardsDaoForDML.deleteBy(loyaltyCard.getCardId());
		
	}
	/*private void updateLoyaltyCardStatus(LoyaltyCards loyaltyCard, boolean isDard) throws Exception {
		
		LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		if(isDard) {
			loyaltyCardsDao.deleteBy(loyaltyCard.getCardId());
		}else {
			
			loyaltyCard.setStatus(OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
			loyaltyCardsDao.saveOrUpdate(loyaltyCard);
		}
		
	}*/
	private void updateLoyaltyCardStatus(LoyaltyCards loyaltyCard, String status) throws Exception {
		
		LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		LoyaltyCardsDaoForDML loyaltyCardsDaoForDML = (LoyaltyCardsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);
		
			
			loyaltyCard.setStatus(status);
			//loyaltyCardsDao.saveOrUpdate(loyaltyCard);
			loyaltyCardsDaoForDML.saveOrUpdate(loyaltyCard);
		
	}
	private void saveContactLoyalty(ContactsLoyalty loyalty) throws Exception{
		
		ContactsLoyaltyDaoForDML loyaltyDao = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
		loyaltyDao.saveOrUpdate(loyalty);
	}
	
	private LoyaltyTransactionChild createSuccessfulTransaction(LoyaltyEnrollRequest enrollRequest, ContactsLoyalty contactLoyalty, ResponseHeader responseHeader, Long userId, Long orgId, 
			Long programId, String membershipType, String docSID, String desc, String storeNumber, String empId, String termId){
		
		LoyaltyTransactionChild transaction = null;
		try{
		
			transaction = new LoyaltyTransactionChild();
			transaction.setTransactionId(Long.valueOf(responseHeader.getTransactionId()));
			transaction.setMembershipNumber(""+contactLoyalty.getCardNumber());
			transaction.setMembershipType(membershipType);
			//transaction.setCreatedDate(Calendar.getInstance());
			
			if(enrollRequest.getCustomer().getCreatedDate() != null && !enrollRequest.getCustomer().getCreatedDate().trim().isEmpty()){
				String requestDate = enrollRequest.getCustomer().getCreatedDate();  
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
				Users user = usersDao.findMlUser(userId);
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
			else{				
				transaction.setCreatedDate(Calendar.getInstance());
			}
			transaction.setOrgId(orgId);
			transaction.setAmountBalance(contactLoyalty.getGiftcardBalance());
			transaction.setPointsBalance(contactLoyalty.getLoyaltyBalance());
			transaction.setGiftBalance(contactLoyalty.getGiftBalance());
			transaction.setProgramId(programId);
			transaction.setTierId(contactLoyalty.getProgramTierId());
			transaction.setUserId(userId);
			transaction.setOrgId(contactLoyalty.getOrgId());
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);
			logger.debug("storeNumber is===>"+storeNumber);     
			transaction.setSubsidiaryNumber(enrollRequest.getHeader().getSubsidiaryNumber() != null && !enrollRequest.getHeader().getSubsidiaryNumber().trim().isEmpty() ? enrollRequest.getHeader().getSubsidiaryNumber().trim() : null);
			transaction.setStoreNumber(storeNumber != null && !storeNumber.trim().isEmpty() ? storeNumber : null);
			transaction.setEmployeeId(empId!=null && !empId.trim().isEmpty() ? empId.trim():null);
			transaction.setTerminalId(termId!=null && !termId.trim().isEmpty() ? termId.trim():null);
			transaction.setDocSID(docSID);
			transaction.setCardSetId(contactLoyalty.getCardSetId());
			transaction.setSourceType(enrollRequest.getHeader().getSourceType());
			transaction.setDescription(desc);
			transaction.setLoyaltyId(contactLoyalty.getLoyaltyId());
			
			LoyaltyTransactionChildDao childDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML childDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			//childDao.saveOrUpdate(transaction);
			childDaoForDML.saveOrUpdate(transaction);
		
		}catch(Exception e){
			logger.error("Exception while creating transaction in child table...", e);
		}
		
		return transaction;
	}
	
	private List<LoyaltyProgram> findActiveProgramList(Long userId, String membershipType) throws Exception {
		
		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		return loyaltyProgramDao.findProgramsBy(userId, OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE, membershipType);
	}
	//by pravendra
	private List<LoyaltyProgram> findSuspendedProgramList(Long userId, String membershipType) throws Exception {
		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		return loyaltyProgramDao.findProgramsBy(userId, OCConstants.LOYALTY_PROGRAM_STATUS_SUSPENDED, membershipType);
	}
	private List<LoyaltyCardSet> findActiveCardSets(String programIdStr) throws Exception {
		
		LoyaltyCardSetDao loyaltyCardSetDao = (LoyaltyCardSetDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
		return loyaltyCardSetDao.findByProgramIdStr(programIdStr, OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE);
	}
	
	
	private String validateProgramRequisites(LoyaltyEnrollRequest enrollRequest, LoyaltyProgram program) throws Exception {
		
			Customer customer = enrollRequest.getCustomer();
		
			String mandatoryStr = null;
			String requisites = program.getRegRequisites();
			if(requisites != null && requisites.trim().length() > 0){
				String[] requisitesArr = requisites.split(";=;");
				for(String requisite : requisitesArr){
					
					if(requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_FIRSTNAME) && (customer.getFirstName() == null || customer.getFirstName().trim().isEmpty())){
						if(mandatoryStr == null){
							mandatoryStr = "firstName";  
						}
						else{
							mandatoryStr += ","+"firstName";
						}
					}
					else if(requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_LASTNAME) && (customer.getLastName() == null || customer.getLastName().trim().isEmpty())){
						if(mandatoryStr == null){
							mandatoryStr = "lastName";  
						}
						else{
							mandatoryStr += ","+"lastName";
						}
					}
					else if(requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_EMAILID) && (customer.getEmailAddress() == null || customer.getEmailAddress().trim().isEmpty())){
						if(mandatoryStr == null){
							mandatoryStr = "emailAddress";  
						}
						else{
							mandatoryStr += ","+"emailAddress";
						}
					}
					else if((OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(program.getMembershipType())  ) && 
							requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_MOBILEPHONE) && (customer.getPhone() == null || customer.getPhone().trim().isEmpty())){
						if(mandatoryStr == null){
							mandatoryStr = "phone";  
						}
						else{
							mandatoryStr += ","+"phone";
						}
					}
					else if(requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_ADDRESSONE) && (customer.getAddressLine1() == null || customer.getAddressLine1().trim().isEmpty())){
						if(mandatoryStr == null){
							mandatoryStr = "addressLine1";  
						}
						else{
							mandatoryStr += ","+"addressLine1";
						}
					}
					else if(requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_CITY) && (customer.getCity() == null || customer.getCity().trim().isEmpty())){
						if(mandatoryStr == null){
							mandatoryStr = "city";  
						}
						else{
							mandatoryStr += ","+"city";
						}
					}
					else if(requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_STATE) && (customer.getState() == null || customer.getState().trim().isEmpty())){
						if(mandatoryStr == null){
							mandatoryStr = "state";  
						}
						else{
							mandatoryStr += ","+"state";
						}
					}
					else if(requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_ZIP) && (customer.getPostal() == null || customer.getPostal().trim().isEmpty())){
						if(mandatoryStr == null){
							mandatoryStr = "postal";  
						}
						else{
							mandatoryStr += ","+"postal";
						}
					}
					else if(requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_COUNTRY) && (customer.getCountry() == null || customer.getCountry().trim().isEmpty())){
						if(mandatoryStr == null){
							mandatoryStr = "country";  
						}
						else{
							mandatoryStr += ","+"country";
						}
					}
					else if(requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_BIRTHDAY) && (customer.getBirthday() == null || customer.getBirthday().trim().isEmpty())){
						if(mandatoryStr == null){
							mandatoryStr = "birthday";  
						}
						else{
							mandatoryStr += ","+"birthday";
						}
					}
					else if(requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_ANNIVERSARY) && (customer.getAnniversary() == null || customer.getAnniversary().trim().isEmpty())){
						if(mandatoryStr == null){
							mandatoryStr = "anniversary";  
						}
						else{
							mandatoryStr += ","+"anniversary";
						}
					}
					else if(requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_GENDER) && (customer.getGender() == null || customer.getGender().trim().isEmpty())){
						if(mandatoryStr == null){
							mandatoryStr = "gender";  
						}
						else{
							mandatoryStr += ","+"gender";
						}
					}
				}
			}
			return mandatoryStr;
			
	}
	
	private LoyaltyEnrollResponse createMembership(LoyaltyEnrollRequest enrollRequest, ResponseHeader responseHeader, 
			LoyaltyProgram program, LoyaltyCards card,
			String mode, Users user, MailingList mlList, String memberShipType, LoyaltyProgramTier linkedTierObj) throws Exception {
		logger.info("Entered createMembership method >>>");
		boolean isDCard = OCConstants.LOYALTY_PROGRAM_TYPE_DYNAMIC.equals(program.getProgramType());
		LoyaltyEnrollResponse enrollResponse = null;
		//LoyaltyMembership loyaltyMembership = null;
		ContactsLoyalty contactLoyalty = null;
		
		List<POSMapping> contactPOSMap = null;
		POSMappingDao posMappingDao = (POSMappingDao) ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
		contactPOSMap = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", user.getUserId());
		
		Contacts jsonContact = new Contacts();
		jsonContact.setUsers(user);
		String subsidiary = enrollRequest.getHeader().getSubsidiaryNumber() != null && !enrollRequest.getHeader().getSubsidiaryNumber().trim().isEmpty() ? enrollRequest.getHeader().getSubsidiaryNumber().trim() : null; 
		String store = enrollRequest.getHeader().getStoreNumber() != null ? enrollRequest.getHeader().getStoreNumber().trim() : enrollRequest.getHeader().getStoreNumber();
		/*if(enrollRequest.getCustomer().getPhone() != null && !enrollRequest.getCustomer().getPhone().isEmpty()){
			String validStatus = LoyaltyProgramHelper.validateMembershipMobile(enrollRequest.getCustomer().getPhone().trim());
			if(OCConstants.LOYALTY_MEMBERSHIP_MOBILE_INVALID.equals(validStatus)){
				Status status = new Status("111554", PropertyUtil.getErrorMessage(111554, OCConstants.ERROR_LOYALTY_FLAG)+" "+enrollRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				enrollResponse = prepareEnrollmentResponse(responseHeader, null, null,status);
				if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(memberShipType)) {
					
					if(isDCard){
						deleteDCard(card);
					}else{
						
						updateLoyaltyCardStatus(card, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
					}
				}
				return enrollResponse;
			}
		}*/
		if(contactPOSMap != null){
			jsonContact = setContactFields(jsonContact, contactPOSMap, enrollRequest,memberShipType);
		}
		logger.info("----enrollRequest.getCustomer().getPhone()=="+enrollRequest.getCustomer().getPhone()+"----jsonContact.getMobilePhone()=="+jsonContact.getMobilePhone());
		/*if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(memberShipType) && 
				//program.getRegRequisites().contains(OCConstants.LOYALTY_REG_REQUISITE_MOBILEPHONE) &&
				enrollRequest.getCustomer().getPhone() != null && !enrollRequest.getCustomer().getPhone().trim().isEmpty() &&
				(jsonContact.getMobilePhone() == null || jsonContact.getMobilePhone().trim().isEmpty())){
			Status status = new Status("111554", PropertyUtil.getErrorMessage(111554, OCConstants.ERROR_LOYALTY_FLAG)+" "+enrollRequest.getCustomer().getPhone()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
			if(isDCard){
				deleteDCard(card);
			}else{
				
				updateLoyaltyCardStatus(card, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
			}
			return enrollResponse;
		}*/
		if(jsonContact.getMobilePhone() != null && !jsonContact.getMobilePhone().isEmpty())
			enrollRequest.getCustomer().setPhone(jsonContact.getMobilePhone());

//				prepareContactFromJsonData(enrollRequest, user.getUserId(), program);
		Map<String, Object> contactAndDataFlags = validateAndSavedbContact(jsonContact, mlList, user);
		Contacts dbContact = (Contacts) contactAndDataFlags.get("dbContact");
		boolean isExists = (Boolean) contactAndDataFlags.get("isExists");
		
		if(isExists){
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			contactLoyalty = contactsLoyaltyDao.findByContactIdStrAndPrgmId(user.getUserId(), dbContact.getContactId()+"",program.getProgramId());
			if(contactLoyalty != null) {
				if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(memberShipType.trim())){
					if(isDCard){
						deleteDCard(card);
					}else{
						
						updateLoyaltyCardStatus(card, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
					}
				}
				
				Customer customer = prepareCustomer(dbContact);
				List<MatchedCustomer> customers = prepareMatchedCustomers(customer, ""+contactLoyalty.getCardNumber());
				
				Status status = new Status("111563", PropertyUtil.getErrorMessage(111563, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactLoyalty.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return prepareEnrollmentResponse(responseHeader, null, customers, status);
			}
		}
		
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
//		dbContact.setLoyaltyCustomer((byte)1);
		
		List<LoyaltyProgramTier> tierList = null;
		if(linkedTierObj == null){//added condition
			tierList = validateTierList(program.getProgramId(), user.getUserId());
			
			if(tierList == null || tierList.size() == 0 || !OCConstants.LOYALTY_PROGRAM_TIER1.equals(tierList.get(0).getTierType())){
				if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(memberShipType.trim())){
					if(isDCard){
						deleteDCard(card);
					}else{
						
						updateLoyaltyCardStatus(card, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
					}
				}
				Status status = new Status("111555", PropertyUtil.getErrorMessage(111555, OCConstants.ERROR_LOYALTY_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return prepareEnrollmentResponse(responseHeader, null, null, status);
			}
		}
		contactsDaoForDML.saveOrUpdate(dbContact);
		
		String phone = null;
		if(jsonContact.getMobilePhone() != null){
			phone = jsonContact.getMobilePhone();
		}
		else{
			phone = dbContact.getMobilePhone();
		}
		if(memberShipType.equals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD)){
			/*contactLoyalty = prepareLoyaltyMembership(card.getCardNumber(), OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD, card.getCardPin(), phone,
					Constants.CONTACT_LOYALTY_TYPE_POS,	store, mode, program, linkedTierObj, card.getCardSetId(),
					enrollRequest.getHeader().getEmployeeId(),enrollRequest.getHeader().getTerminalId(), enrollRequest.getHeader().getSourceType());*/
			contactLoyalty = prepareLoyaltyMembership(card.getCardNumber(), OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD, card.getCardPin(), phone,
					Constants.CONTACT_LOYALTY_TYPE_POS, subsidiary, store, mode, program, linkedTierObj, card.getCardSetId(),
					enrollRequest.getHeader().getEmployeeId(),enrollRequest.getHeader().getTerminalId(), enrollRequest.getHeader().getSourceType());
			
		}
		else if(memberShipType.equals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)){
			/*contactLoyalty = prepareLoyaltyMembership(enrollRequest.getMembership().getPhoneNumber(), OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE, 
					null, phone, Constants.CONTACT_LOYALTY_TYPE_POS, store, mode, program, linkedTierObj,
					null,enrollRequest.getHeader().getEmployeeId(),enrollRequest.getHeader().getTerminalId(), enrollRequest.getHeader().getSourceType());*/
			contactLoyalty = prepareLoyaltyMembership(enrollRequest.getMembership().getPhoneNumber(), OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE, 
					null, phone, Constants.CONTACT_LOYALTY_TYPE_POS, subsidiary, store, mode, program, linkedTierObj,
					null,enrollRequest.getHeader().getEmployeeId(),enrollRequest.getHeader().getTerminalId(), enrollRequest.getHeader().getSourceType());
		}
		contactLoyalty.setUserId(user.getUserId());
		contactLoyalty.setOrgId(user.getUserOrganization().getUserOrgId());
		contactLoyalty.setContact(dbContact);
		contactLoyalty.setCustomerId(dbContact.getExternalId());
		contactLoyalty.setRewardFlag(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);

		//generate a pwd and encrypt it and save it...
		String encPwd = generateMembrshpPwd();
		contactLoyalty.setMembershipPwd(encPwd);
		
		dbContact.setLoyaltyCustomer((byte)1);
		contactsDaoForDML.saveOrUpdate(dbContact);
		
		saveContactLoyalty(contactLoyalty);
		
		if(memberShipType.equals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD)){
			card.setMembershipId(contactLoyalty.getLoyaltyId());
			card.setActivationDate(Calendar.getInstance());
			card.setStatus(OCConstants.LOYALTY_CARD_STATUS_ENROLLED);
			//updateCardStatus(OCConstants.LOYALTY_CARD_STATUS_ENROLLED, card);
			saveLoyaltyCard(card);
		}

		LoyaltyProgramTier tier = null;
		Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap = null;
		if(linkedTierObj == null){//added condition
			//Prepare eligible tiers map
			Iterator<LoyaltyProgramTier> iterTier = tierList.iterator();
			eligibleMap = new LinkedHashMap<LoyaltyProgramTier, LoyaltyProgramTier>();
			LoyaltyProgramTier prevtier = null;
			LoyaltyProgramTier nexttier = null;
			
			while(iterTier.hasNext()){
				nexttier = iterTier.next();
				if(OCConstants.LOYALTY_PROGRAM_TIER1.equals(nexttier.getTierType())){
					eligibleMap.put(nexttier, null);
				}
				else{
					if((Integer.valueOf(prevtier.getTierType().substring(5))+1) 
							== Integer.valueOf(nexttier.getTierType().substring(5)) && prevtier.getTierUpgdConstraintValue() != null){
						eligibleMap.put(nexttier, prevtier);
						logger.info("eligible tier ="+nexttier.getTierType()+" upgdconstrant value = "+prevtier.getTierUpgdConstraintValue());
					}
				}
				prevtier = nexttier;
			}
			
			
			tier = findTier(dbContact.getContactId(), user.getUserId(), contactLoyalty.getLoyaltyId(), tierList, eligibleMap);
			if(!"Pending".equalsIgnoreCase(tier.getTierType())){
				contactLoyalty.setProgramTierId(tier.getTierId());
				saveContactLoyalty(contactLoyalty);
			}
		}
		else {
			tier = linkedTierObj;
		}
		
		MembershipResponse accountResponse = new MembershipResponse();
		
		if(memberShipType.equals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD)){
			accountResponse.setCardNumber(String.valueOf(contactLoyalty.getCardNumber()));
			accountResponse.setCardPin(card.getCardPin());
			accountResponse.setPhoneNumber("");
		}
		else if(memberShipType.equals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)){
			accountResponse.setPhoneNumber(String.valueOf(contactLoyalty.getCardNumber()));
			accountResponse.setCardNumber("");
			accountResponse.setCardPin("");
		}
		if( tier != null && !"Pending".equalsIgnoreCase(tier.getTierType())){
			
			if(program.getTierEnableFlag() == OCConstants.FLAG_YES) {
				accountResponse.setTierLevel(tier.getTierType());
				accountResponse.setTierName(tier.getTierName());
			}
			else {
				accountResponse.setTierLevel("");
				accountResponse.setTierName("");
			}
			
			if(program.getMembershipExpiryFlag() == 'Y' && tier.getMembershipExpiryDateType() != null 
					&& tier.getMembershipExpiryDateValue() != null){
				accountResponse.setExpiry(LoyaltyProgramHelper.getMbrshipExpiryDate(contactLoyalty.getCreatedDate(), contactLoyalty.getTierUpgradedDate(), 
						false, tier.getMembershipExpiryDateType(), tier.getMembershipExpiryDateValue()));
			}
			else{
				accountResponse.setExpiry("");
			}
			
		}
		else{
			accountResponse.setTierLevel("");
			accountResponse.setTierName("");
			accountResponse.setExpiry("");
		}
		
		Customer customer = prepareCustomer(dbContact);
		List<MatchedCustomer> customers = prepareMatchedCustomers(customer, ""+contactLoyalty.getCardNumber());
		
		Status status = new Status("0", "Enrollment was successful", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		enrollResponse = prepareEnrollmentResponse(responseHeader, accountResponse, customers, status);
		
		
		LoyaltyTransactionChild transChild = createSuccessfulTransaction(enrollRequest, contactLoyalty, responseHeader, user.getUserId(), user.getUserOrganization().getUserOrgId()
				, program.getProgramId(), memberShipType, enrollRequest.getHeader().getDocSID(), "loyaltyEnroll", store,enrollRequest.getHeader().getEmployeeId(),enrollRequest.getHeader().getTerminalId());
		
		//send loyalty threshold alerts...
		if(program.getProgramType()
				.equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_TYPE_CARD)){
			if(enrollRequest.getMembership().getCardNumber() != null && enrollRequest.getMembership().getCardNumber().trim().length() > 0){
				LoyaltyProgramHelper.sendLowCardsThresholdAlerts(user, program, false);
			}
			else{
				LoyaltyProgramHelper.sendLowCardsThresholdAlerts(user, program, true);
			}
		}
		
		if("Pending".equalsIgnoreCase(tier.getTierType())) {
			LoyaltyEnrollCPVThread cpvThread = new LoyaltyEnrollCPVThread(eligibleMap, user, contactLoyalty, tierList, transChild, program, enrollRequest);
			Thread th = new Thread(cpvThread);
			th.start();
			
			return enrollResponse;
		}
		
		LoyaltyAutoComm loyaltyAutoComm = getLoyaltyAutoComm(program.getProgramId());
		LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
		//Send Loyalty Registration Email
		if(status.getErrorCode().equals("0") && dbContact.getEmailId() != null && loyaltyAutoComm != null && loyaltyAutoComm.getRegEmailTmpltId() != null){
			//email queue
			autoCommGen.sendEnrollTemplate(loyaltyAutoComm.getRegEmailTmpltId(), ""+contactLoyalty.getCardNumber(), 
					contactLoyalty.getCardPin(), user, dbContact.getEmailId(), dbContact.getFirstName(),
					dbContact.getContactId(), contactLoyalty.getLoyaltyId(),"");
		}
		if (status.getErrorCode().equals("0") && user.isEnableSMS() && loyaltyAutoComm != null && loyaltyAutoComm.getRegSmsTmpltId() != null 
				&& contactLoyalty.getMobilePhone() != null) {
			//sms queue
			Long cid = null;
			if (contactLoyalty.getContact() != null && contactLoyalty.getContact().getContactId() != null) {
				cid = contactLoyalty.getContact().getContactId();
			}
			autoCommGen.sendEnrollSMSTemplate(loyaltyAutoComm.getRegSmsTmpltId(), user, cid, contactLoyalty.getLoyaltyId(),
					contactLoyalty.getMobilePhone(), "");
		}
		
		updateMembershipBalances(enrollRequest, contactLoyalty, program, loyaltyAutoComm,
				dbContact.getEmailId(), dbContact.getUsers(), dbContact.getFirstName(), dbContact.getContactId(), tier);
		
		saveContactLoyalty(contactLoyalty);
		
		logger.info("Exited createMembership method <<<");      
		return enrollResponse; 
	}
	
	
	private String generateMembrshpPwd() throws Exception {
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		String memPwd = "";  
		String encPwd = "";
		//do {
			memPwd = RandomStringUtils.randomAlphanumeric(6);
			encPwd = EncryptDecryptLtyMembshpPwd.encrypt(memPwd);
	 	//} while(loyaltyDao.findByMembrshpPwd(encPwd) != null);
		return encPwd;
	}

	private List<LoyaltyProgramTier> validateTierList(Long programId, Long userId) {
		try{
			LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);

			List<LoyaltyProgramTier> tiersList = loyaltyProgramTierDao.fetchTiersByProgramId(programId);
			if (tiersList == null || tiersList.size() <= 0) {
				logger.info("Tiers list is empty...");
				return null;
			}
			else if (tiersList.size() >= 1) {//sort tiers by tiertype i.e Tier 1, Tier 2, and so on.
				Collections.sort(tiersList, new Comparator<LoyaltyProgramTier>() {
					@Override
					public int compare(LoyaltyProgramTier o1, LoyaltyProgramTier o2) {

						int num1 = Integer.valueOf(o1.getTierType().substring(5)).intValue();
						int num2 = Integer.valueOf(o2.getTierType().substring(5)).intValue();
						if(num1 < num2){
							return -1;
						}
						else if(num1 == num2){
							return 0;
						}
						else{
							return 1;
						}
					}
				});
			}

			for(LoyaltyProgramTier tier : tiersList) {//testing purpose
				logger.info("tier level : "+tier.getTierType());
			}
			return tiersList;
		}catch(Exception e){
			logger.error("Exception in validating tiersList::", e);
			return null;
		}

	}

	private LoyaltyEnrollResponse convertGiftToLoyalty(LoyaltyEnrollRequest enrollRequest, ResponseHeader responseHeader, 
			LoyaltyProgram program, LoyaltyCards card,
			String mode, Users user, MailingList mlList, LoyaltyProgramTier linkedTierObj, String memberShipType) throws Exception {
		
		logger.info("Convert gift to loyalty membership..");
		
		LoyaltyEnrollResponse enrollResponse = null;
		ContactsLoyalty contactLoyalty = null;
		
		List<POSMapping> contactPOSMap = null;
		POSMappingDao posMappingDao = (POSMappingDao) ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
		contactPOSMap = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", user.getUserId());
		
		Contacts jsonContact = new Contacts();
		jsonContact.setUsers(user);
		
		if(contactPOSMap != null){
			jsonContact = setContactFields(jsonContact, contactPOSMap, enrollRequest,memberShipType);
		}
//				prepareContactFromJsonData(enrollRequest, user.getUserId(), program);
		Map<String, Object> contactAndDataFlags = validateAndSavedbContact(jsonContact, mlList, user);
		Contacts dbContact = (Contacts) contactAndDataFlags.get("dbContact");
		boolean isExists = (Boolean) contactAndDataFlags.get("isExists");
		
		if(isExists){
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			contactLoyalty = contactsLoyaltyDao.findByContactIdStrAndPrgmId(user.getUserId(), dbContact.getContactId()+"",program.getProgramId());
			if(contactLoyalty != null) {
				Customer customer = prepareCustomer(dbContact);
				List<MatchedCustomer> customers = prepareMatchedCustomers(customer, ""+contactLoyalty.getCardNumber());
				
				Status status = new Status("111563", PropertyUtil.getErrorMessage(111563, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return prepareEnrollmentResponse(responseHeader, null, customers, status);
			}
		}
		
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
		dbContact.setLoyaltyCustomer((byte)1);
		
		List<LoyaltyProgramTier> tierList = null;
		if(linkedTierObj == null){//added condition
			tierList = validateTierList(program.getProgramId(), user.getUserId());
			
			if(tierList == null || tierList.size() == 0 || !OCConstants.LOYALTY_PROGRAM_TIER1.equals(tierList.get(0).getTierType())){
				Status status = new Status("111555", PropertyUtil.getErrorMessage(111555, OCConstants.ERROR_LOYALTY_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return prepareEnrollmentResponse(responseHeader, null, null, status);
			}
		}
		contactsDaoForDML.saveOrUpdate(dbContact);
		
		contactLoyalty = findMembershpByCard(card.getCardNumber(), card.getProgramId(), user.getUserId());
		contactLoyalty.setContact(dbContact);
		contactLoyalty.setCreatedDate(Calendar.getInstance());
		contactLoyalty.setCustomerId(dbContact.getExternalId());
		if(jsonContact.getMobilePhone() != null){
			contactLoyalty.setMobilePhone(jsonContact.getMobilePhone());
		}
		else{
			contactLoyalty.setMobilePhone(dbContact.getMobilePhone());
		}
//		contactLoyalty.setMobilePhone(dbContact.getMobilePhone());
		contactLoyalty.setRewardFlag(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL);
		
		//generate a pwd and encrypt it and save it...
		String encPwd = generateMembrshpPwd();
		contactLoyalty.setMembershipPwd(encPwd);

		saveContactLoyalty(contactLoyalty);

		card.setStatus(OCConstants.LOYALTY_CARD_STATUS_ENROLLED);
		saveLoyaltyCard(card);
		
		LoyaltyProgramTier tier = null;
		Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap = null;
		if(linkedTierObj == null){//added condition
			//Prepare eligible tiers map
			Iterator<LoyaltyProgramTier> iterTier = tierList.iterator();
			eligibleMap = new LinkedHashMap<LoyaltyProgramTier, LoyaltyProgramTier>();
			LoyaltyProgramTier prevtier = null;
			LoyaltyProgramTier nexttier = null;

			while(iterTier.hasNext()){
				nexttier = iterTier.next();
				if(OCConstants.LOYALTY_PROGRAM_TIER1.equals(nexttier.getTierType())){
					eligibleMap.put(nexttier, null);
				}
				else{
					if((Integer.valueOf(prevtier.getTierType().substring(5))+1) 
							== Integer.valueOf(nexttier.getTierType().substring(5)) && prevtier.getTierUpgdConstraintValue() != null){
						eligibleMap.put(nexttier, prevtier);
						logger.info("eligible tier ="+nexttier.getTierType()+" upgdconstrant value = "+prevtier.getTierUpgdConstraintValue());
					}
				}
				prevtier = nexttier;
			}

			tier = findTier(dbContact.getContactId(), user.getUserId(), contactLoyalty.getLoyaltyId(), tierList, eligibleMap);
			if(!"Pending".equalsIgnoreCase(tier.getTierType())){
				contactLoyalty.setProgramTierId(tier.getTierId());
				saveContactLoyalty(contactLoyalty);
			}
		}
		else{
			tier = linkedTierObj;
			contactLoyalty.setProgramTierId(tier.getTierId());
			saveContactLoyalty(contactLoyalty);
		}
		MembershipResponse accountResponse = new MembershipResponse();
		
		accountResponse.setCardNumber(String.valueOf(contactLoyalty.getCardNumber()));
		accountResponse.setCardPin(card.getCardPin());
		accountResponse.setPhoneNumber("");
		
		if(tier != null && !"Pending".equalsIgnoreCase(tier.getTierType())){
			if(program.getTierEnableFlag() == OCConstants.FLAG_YES) {
				accountResponse.setTierLevel(tier.getTierType());
				accountResponse.setTierName(tier.getTierName());
			}
			else {
				accountResponse.setTierLevel("");
				accountResponse.setTierName("");
			}
			if(program.getMembershipExpiryFlag() == 'Y' && tier.getMembershipExpiryDateType() != null 
					&& tier.getMembershipExpiryDateValue() != null){
				accountResponse.setExpiry(LoyaltyProgramHelper.getMbrshipExpiryDate(contactLoyalty.getCreatedDate(), contactLoyalty.getTierUpgradedDate(), 
						false, tier.getMembershipExpiryDateType(), tier.getMembershipExpiryDateValue()));
			}
			else{
				accountResponse.setExpiry("");
			}
			
		}
		else {
			accountResponse.setTierLevel("");
			accountResponse.setTierName("");
			accountResponse.setExpiry("");
		}
		
		Customer customer = prepareCustomer(dbContact);
		List<MatchedCustomer> customers = prepareMatchedCustomers(customer, ""+contactLoyalty.getCardNumber());
		
		Status status = new Status("0", "Enrollment was successful", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		enrollResponse = prepareEnrollmentResponse(responseHeader, accountResponse, customers, status);
		
		String store = enrollRequest.getHeader().getStoreNumber() != null ?  enrollRequest.getHeader().getStoreNumber().trim() : enrollRequest.getHeader().getStoreNumber(); 
		LoyaltyTransactionChild transChild = createSuccessfulTransaction(enrollRequest, contactLoyalty, responseHeader, user.getUserId(), user.getUserOrganization().getUserOrgId()
				, program.getProgramId(), OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD, enrollRequest.getHeader().getDocSID(), "GiftToLoyalty", store,enrollRequest.getHeader().getEmployeeId(),enrollRequest.getHeader().getTerminalId());

		if("Pending".equalsIgnoreCase(tier.getTierType())) {
			LoyaltyEnrollCPVThread cpvThread = new LoyaltyEnrollCPVThread(eligibleMap, user, contactLoyalty, tierList, transChild, program, enrollRequest);
			Thread th = new Thread(cpvThread);
			th.start();

			return enrollResponse;
		}

		LoyaltyAutoComm loyaltyAutoComm = getLoyaltyAutoComm(program.getProgramId());
		LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
		//Send Loyalty Registration Email
		if(status.getErrorCode().equals("0") && dbContact.getEmailId() != null && loyaltyAutoComm != null && loyaltyAutoComm.getRegEmailTmpltId() != null){
			//email queue
			/*autoCommGen.sendEnrollTemplate(loyaltyAutoComm.getRegEmailTmpltId(), ""+contactLoyalty.getCardNumber(),
					contactLoyalty.getCardPin(), user, dbContact.getEmailId(), dbContact.getFirstName(), dbContact.getContactId());*/
			
			autoCommGen.sendEnrollTemplate(loyaltyAutoComm.getRegEmailTmpltId(), ""+contactLoyalty.getCardNumber(), 
					contactLoyalty.getCardPin(), user, dbContact.getEmailId(), dbContact.getFirstName(), dbContact.getContactId(),
					contactLoyalty.getLoyaltyId(),"");
		}
		if (status.getErrorCode().equals("0") && user.isEnableSMS() && loyaltyAutoComm != null && loyaltyAutoComm.getRegSmsTmpltId() != null && contactLoyalty.getMobilePhone() != null) {
			//sms queue
			Long cid = null;
			if (contactLoyalty.getContact() != null && contactLoyalty.getContact().getContactId() != null) {
				cid = contactLoyalty.getContact().getContactId();
			}
			/*autoCommGen.sendEnrollSMSTemplate(loyaltyAutoComm.getRegSmsTmpltId(), user, 
					cid, contactLoyalty.getLoyaltyId(), enrollRequest.getCustomer().getPhone());*/
			
			autoCommGen.sendEnrollSMSTemplate(loyaltyAutoComm.getRegSmsTmpltId(), user, cid, contactLoyalty.getLoyaltyId(),
					contactLoyalty.getMobilePhone(), "");
		}
		
		updateMembershipBalances(enrollRequest, contactLoyalty, program, loyaltyAutoComm,
				dbContact.getEmailId(), dbContact.getUsers(), dbContact.getFirstName(), dbContact.getContactId(), tier);
		
		saveContactLoyalty(contactLoyalty);
		
		return enrollResponse;
	}
	
	
	
	private void saveLoyaltyCard(LoyaltyCards loyaltyCard) throws Exception {
		
		LoyaltyCardsDao loyaltyCardDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		LoyaltyCardsDaoForDML loyaltyCardDaoForDML = (LoyaltyCardsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);
		//loyaltyCardDao.saveOrUpdate(loyaltyCard);
		loyaltyCardDaoForDML.saveOrUpdate(loyaltyCard);

	}
	
	private void updateMembershipBalances(LoyaltyEnrollRequest enrollRequest, ContactsLoyalty loyalty, LoyaltyProgram program, 
			LoyaltyAutoComm autoComm, String emailId, Users user, String firstName, Long contactId, LoyaltyProgramTier tier) throws Exception {
		
		Double fromLtyBalance = loyalty.getTotalLoyaltyEarned();
		Double fromAmtBalance = loyalty.getTotalGiftcardAmount();
		LoyaltyThresholdBonusDao bonusDao = (LoyaltyThresholdBonusDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
		List<LoyaltyThresholdBonus> bonusList = bonusDao.getBonusListByPrgmId(program.getProgramId());
		if(bonusList == null || bonusList.size() == 0) return;
		for(LoyaltyThresholdBonus bonus : bonusList){
			if(bonus.getRegistrationFlag() == 'Y'){
				String earnType = null;
				double earnedValue = 0;
				double earnedAmount = 0.0;
				boolean bonusPointsFlag = false;
				
				if(bonus.getExtraBonusType().equals(OCConstants.LOYALTY_TYPE_POINTS)){
					bonusPointsFlag = true;
					earnType = OCConstants.LOYALTY_TYPE_POINTS;
					earnedValue = bonus.getExtraBonusValue();
					
					if(loyalty.getTotalLoyaltyEarned() == null){
						loyalty.setTotalLoyaltyEarned(bonus.getExtraBonusValue());
					}
					else{
						loyalty.setTotalLoyaltyEarned(loyalty.getTotalLoyaltyEarned()+bonus.getExtraBonusValue());
					}
					
					if(loyalty.getLoyaltyBalance() == null){
						loyalty.setLoyaltyBalance(bonus.getExtraBonusValue());
					}
					else{
						loyalty.setLoyaltyBalance(loyalty.getLoyaltyBalance()+bonus.getExtraBonusValue());
					}
					
				}
				else if(bonus.getExtraBonusType().equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
					
					earnType = OCConstants.LOYALTY_TYPE_AMOUNT;
					//earnedValue = bonus.getExtraBonusValue();
					String result = Utility.truncateUptoTwoDecimal(bonus.getExtraBonusValue());
					if(result != null)
						earnedAmount = Double.parseDouble(result);
					
					if(loyalty.getTotalGiftcardAmount() == null){
						//loyalty.setTotalGiftcardAmount(bonus.getExtraBonusValue());
						loyalty.setTotalGiftcardAmount(earnedAmount);
					}
					else{
						//loyalty.setTotalGiftcardAmount(loyalty.getTotalGiftcardAmount()+bonus.getExtraBonusValue());
						loyalty.setTotalGiftcardAmount(new BigDecimal(loyalty.getTotalGiftcardAmount()+earnedAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					}
					
					if(loyalty.getGiftcardBalance() == null){
						//loyalty.setGiftcardBalance(bonus.getExtraBonusValue());
						loyalty.setGiftcardBalance(earnedAmount);
					}
					else{
						//loyalty.setGiftcardBalance(loyalty.getGiftcardBalance()+bonus.getExtraBonusValue());
						loyalty.setGiftcardBalance(new BigDecimal(loyalty.getGiftcardBalance()+earnedAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					}
				}
				
				LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
				// Send auto communication email
				if(autoComm != null && autoComm.getThreshBonusEmailTmpltId() != null && emailId != null){
						/*autoCommGen.sendEarnBonusTemplate(autoComm.getThreshBonusEmailTmpltId(), ""+loyalty.getCardNumber(),
								loyalty.getCardPin(), user, emailId, firstName, contactId);*/
						autoCommGen.sendEarnBonusTemplate(autoComm.getThreshBonusEmailTmpltId(), ""+loyalty.getCardNumber(), 
								loyalty.getCardPin(), user, emailId, firstName, contactId, loyalty.getLoyaltyId());
				}
				if(autoComm != null && autoComm.getThreshBonusSmsTmpltId() != null && loyalty.getMobilePhone() != null){
					autoCommGen.sendEarnBonusSMSTemplate(autoComm.getThreshBonusSmsTmpltId(), user, contactId, 
							loyalty.getLoyaltyId(), loyalty.getMobilePhone());
				}
				
				// create a child transaction
				//LoyaltyTransactionChild childTxbonus = createBonusTransaction(loyalty, earnedValue, earnType, "Registration Bonus");
				LoyaltyTransactionChild childTxbonus = createBonusTransaction(enrollRequest, loyalty, earnedAmount, earnedValue, earnType, "Registration Bonus");
				
					double pointsbonus = 0.0;
					double amountbonus = 0.0;
					if(bonusPointsFlag){
						pointsbonus = bonus.getExtraBonusValue();
					}
					else{
						amountbonus = bonus.getExtraBonusValue();
					}
					
				createExpiryTransaction(loyalty, (long)pointsbonus, amountbonus, loyalty.getOrgId(), 
						childTxbonus.getTransChildId());
				
				//applyConversionRules(loyalty, childTxbonus, program, tier);
				String[] diffBonArr = applyConversionRules(loyalty, tier); 
				logger.info("balances After conversion rules updatation --  points = "+loyalty.getLoyaltyBalance()+" currency = "+loyalty.getGiftcardBalance());
				
				String conversionBonRate = null;
				long convertBonPoints = 0;
				double convertBonAmount = 0;
				if(diffBonArr != null){
					convertBonAmount = Double.valueOf(diffBonArr[0].trim());
					convertBonPoints = Double.valueOf(diffBonArr[1].trim()).longValue();
					conversionBonRate = diffBonArr[2];
				}
				Long pointsDifference = (long)pointsbonus - convertBonPoints;
				double amountDifference = (double)amountbonus +  (diffBonArr != null ? Double.parseDouble(diffBonArr[0].trim()) : 0.0);
			
				tier = applyTierUpgradeRule(loyalty, program, childTxbonus, tier);
				updateBonusTransaction(childTxbonus, loyalty, ""+pointsDifference, ""+amountDifference, convertBonAmount, tier);
				
				break;
			}//if bonus flag
		}
		updateThresholdBonus(enrollRequest, loyalty, program, fromLtyBalance, fromAmtBalance, tier);
		
	}
	
	private LoyaltyProgramTier applyTierUpgradeRule(ContactsLoyalty contactsLoyalty, LoyaltyProgram program, LoyaltyTransactionChild transactionChild, LoyaltyProgramTier currTier){
		logger.debug(">>>>>>>>>>>>> entered in applyTierUpgradeRule");
		try{
			boolean tierUpgd = false;

			LoyaltyProgramTier newTier = LoyaltyProgramHelper.applyTierUpgdRules(contactsLoyalty.getContact().getContactId(), contactsLoyalty, currTier);
			if(!newTier.getTierType().equalsIgnoreCase(currTier.getTierType())){
				currTier = newTier;
				tierUpgd = true;
			}
			
			if(tierUpgd){
				contactsLoyalty.setProgramTierId(currTier.getTierId());
				contactsLoyalty.setTierUpgradedDate(Calendar.getInstance());
				contactsLoyalty.setTierUpgradeReason(currTier.getTierUpgdConstraint());
				ContactsLoyaltyDaoForDML contactsLoyaltyDao = (ContactsLoyaltyDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
				contactsLoyaltyDao.saveOrUpdate(contactsLoyalty);

				transactionChild.setTierId(currTier.getTierId());
				LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
				//loyaltyTransactionChildDao.saveOrUpdate(transactionChild);
				loyaltyTransactionChildDaoForDML.saveOrUpdate(transactionChild);
			}
			
			Contacts contact = null;
			LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
			LoyaltyAutoComm autoComm = getLoyaltyAutoComm(program.getProgramId());
			if(tierUpgd && autoComm != null && autoComm.getTierUpgdEmailTmpltId() != null && contactsLoyalty.getContact() != null &&
					contactsLoyalty.getContact().getContactId() != null){
				contact = findContactById(contactsLoyalty.getContact().getContactId());
				if(contact != null && contact.getEmailId() != null){
					autoCommGen.sendTierUpgdTemplate(autoComm.getTierUpgdEmailTmpltId(), ""+contactsLoyalty.getCardNumber(),
							contactsLoyalty.getCardPin(), contact.getUsers(), contact.getEmailId(),
							contact.getFirstName(), contact.getContactId(), contactsLoyalty.getLoyaltyId());
				}

			}
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = usersDao.findByUserId(contactsLoyalty.getUserId());
			if(user.isEnableSMS() && tierUpgd && autoComm != null && autoComm.getTierUpgdSmsTmpltId() != null) {
				Long contactId = null;
				if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null){
					contactId = contactsLoyalty.getContact().getContactId();
				}
				autoCommGen.sendTierUpgdSMSTemplate(autoComm.getTierUpgdSmsTmpltId(), user, contactId,
						contactsLoyalty.getLoyaltyId(), null);
			}

			//contactsLoyaltyDao.saveOrUpdate(contactsLoyalty);
		}catch(Exception e){
			logger.error("Exception while upgrading tier...", e);
		}
		logger.debug("<<<<<<<<<<<<< completed applyTierUpgradeRule");
		return currTier;
	}//applyTierUpgradeRule
	
	private void updateThresholdBonus(LoyaltyEnrollRequest enrollRequest, ContactsLoyalty contactsLoyalty, LoyaltyProgram program, Double fromLtyBalance, Double fromAmtBalance, LoyaltyProgramTier tier) throws Exception {
		logger.info(" Entered into updateThresholdBonus method >>>");
		
		try{
			LoyaltyThresholdBonusDao loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
			List<LoyaltyThresholdBonus> threshBonusList = loyaltyThresholdBonusDao.getBonusListByPrgmId(program.getProgramId(), 'N' );
			List<LoyaltyThresholdBonus> pointsBonusList = new ArrayList<LoyaltyThresholdBonus>();
			List<LoyaltyThresholdBonus> amountBonusList = new ArrayList<LoyaltyThresholdBonus>();
			
			fromAmtBalance = fromAmtBalance == null ? 0.0 : fromAmtBalance;
			fromLtyBalance = fromLtyBalance == null ? 0.0 : fromLtyBalance;
			
			if(threshBonusList == null) return;
			
			for(LoyaltyThresholdBonus bonus : threshBonusList){
				if(bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_POINTS)){
					pointsBonusList.add(bonus);
				}
				else if (bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
					amountBonusList.add(bonus);
				}
			}
			
			if(pointsBonusList.isEmpty() && amountBonusList.isEmpty()) return;
			
			List<LoyaltyThresholdBonus> matchedBonusList = new ArrayList<LoyaltyThresholdBonus>();
			
			if(pointsBonusList.size() > 0){
				Collections.sort(pointsBonusList, new Comparator<LoyaltyThresholdBonus>(){
					@Override
					public int compare(LoyaltyThresholdBonus ltb1, LoyaltyThresholdBonus ltb2) {
						return ltb1.getEarnedLevelValue().compareTo(ltb2.getEarnedLevelValue());
					}
				});
			}
			if(amountBonusList.size() > 0){
				Collections.sort(amountBonusList, new Comparator<LoyaltyThresholdBonus>(){
					@Override
					public int compare(LoyaltyThresholdBonus ltb1, LoyaltyThresholdBonus ltb2) {
						return ltb1.getEarnedLevelValue().compareTo(ltb2.getEarnedLevelValue());
					}
				});
			}
			if(contactsLoyalty.getTotalLoyaltyEarned() != null && contactsLoyalty.getTotalLoyaltyEarned() > 0){
				for(LoyaltyThresholdBonus bonus : pointsBonusList){
					if(contactsLoyalty.getTotalLoyaltyEarned() >= bonus.getEarnedLevelValue() && 
							(fromLtyBalance == null || fromLtyBalance.doubleValue() < bonus.getEarnedLevelValue())){
						matchedBonusList.add(bonus);
					}
				}
			}
			if(contactsLoyalty.getTotalGiftcardAmount() != null && contactsLoyalty.getTotalGiftcardAmount() > 0){
				for(LoyaltyThresholdBonus bonus : amountBonusList){
					if(contactsLoyalty.getTotalGiftcardAmount() >= bonus.getEarnedLevelValue() && 
							(fromAmtBalance == null || fromAmtBalance.doubleValue() < bonus.getEarnedLevelValue())){
						matchedBonusList.add(bonus);
					}
				}
			}
			 
			long bonusPoints = 0;
			double bonusAmount = 0.0;
			String bonusRate = null;
			boolean bonusflag =false;
			
			if(matchedBonusList != null && matchedBonusList.size() > 0){
				for (LoyaltyThresholdBonus matchedBonus : matchedBonusList) {
					if(OCConstants.LOYALTY_TYPE_POINTS.equals(matchedBonus.getExtraBonusType())){
						bonusflag = true;
						logger.info("loyalty bonus type :Points:");
						bonusPoints = matchedBonus.getExtraBonusValue().longValue();
						bonusRate = ""+matchedBonus.getEarnedLevelValue()+" "+matchedBonus.getEarnedLevelType()+
								" --> "+matchedBonus.getExtraBonusValue()+" "+OCConstants.LOYALTY_TYPE_POINTS;
						if(contactsLoyalty.getLoyaltyBalance() == null ) {
							contactsLoyalty.setLoyaltyBalance(matchedBonus.getExtraBonusValue());
						}
						else{
							contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() + matchedBonus.getExtraBonusValue());
						}
						if(contactsLoyalty.getTotalLoyaltyEarned() == null){
							contactsLoyalty.setTotalLoyaltyEarned(matchedBonus.getExtraBonusValue());
						}
						else{
							contactsLoyalty.setTotalLoyaltyEarned(contactsLoyalty.getTotalLoyaltyEarned() + matchedBonus.getExtraBonusValue());
						}
						//LoyaltyTransactionChild childTxbonus = createBonusTransaction(contactsLoyalty, Double.valueOf(matchedBonus.getExtraBonusValue()), OCConstants.LOYALTY_TYPE_POINTS, bonusRate);
						LoyaltyTransactionChild childTxbonus = createBonusTransaction(enrollRequest, contactsLoyalty, bonusAmount, Double.valueOf(matchedBonus.getExtraBonusValue()), OCConstants.LOYALTY_TYPE_POINTS, bonusRate);
								
						logger.info("balances before balance object = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
						createExpiryTransaction(contactsLoyalty, bonusPoints, bonusAmount, contactsLoyalty.getOrgId(), 
								childTxbonus.getTransChildId());
						
						String[] diffBonArr = applyConversionRules(contactsLoyalty, tier); 
						logger.info("balances After conversion rules updatation --  points = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
						
						String conversionBonRate = null;
						long convertBonPoints = 0;
						double convertBonAmount = 0;
						if(diffBonArr != null){
							convertBonAmount = Double.valueOf(diffBonArr[0].trim());
							convertBonPoints = Double.valueOf(diffBonArr[1].trim()).longValue();
							conversionBonRate = diffBonArr[2];
						}
						Long pointsDifference = bonusPoints - convertBonPoints;
						double amountDifference = (double)bonusAmount + (diffBonArr != null ? Double.parseDouble(diffBonArr[0].trim()) : 0.0);
					
						tier = applyTierUpgradeRule(contactsLoyalty, program, childTxbonus, tier);
						updateBonusTransaction(childTxbonus, contactsLoyalty, ""+pointsDifference, ""+amountDifference, convertBonAmount, tier);

						
					}
					else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(matchedBonus.getExtraBonusType())){
						bonusflag = true;
						logger.info("loyalty bonus type :Amount:");
						//bonusAmount = matchedBonus.getExtraBonusValue();
						String result = Utility.truncateUptoTwoDecimal(matchedBonus.getExtraBonusValue());
						if(result != null)
							bonusAmount = Double.parseDouble(result);
						bonusRate = ""+matchedBonus.getEarnedLevelValue()+" "+matchedBonus.getEarnedLevelType()+
								" --> "+matchedBonus.getExtraBonusValue()+" "+OCConstants.LOYALTY_TYPE_AMOUNT;
						if(contactsLoyalty.getGiftcardBalance() == null ) {
							//contactsLoyalty.setGiftcardBalance(matchedBonus.getExtraBonusValue());
							contactsLoyalty.setGiftcardBalance(bonusAmount);
						}
						else{
							//contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() + matchedBonus.getExtraBonusValue());
							contactsLoyalty.setGiftcardBalance(new BigDecimal(contactsLoyalty.getGiftcardBalance() + bonusAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						}
						if(contactsLoyalty.getTotalGiftcardAmount() == null){
							//contactsLoyalty.setTotalGiftcardAmount(matchedBonus.getExtraBonusValue());
							contactsLoyalty.setTotalGiftcardAmount(bonusAmount);
						}
						else{
							//contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount() + matchedBonus.getExtraBonusValue());
							contactsLoyalty.setTotalGiftcardAmount(new BigDecimal(contactsLoyalty.getTotalGiftcardAmount() + bonusAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						}
						//LoyaltyTransactionChild childTxbonus = createBonusTransaction(contactsLoyalty, Double.valueOf(matchedBonus.getExtraBonusValue()), OCConstants.LOYALTY_TYPE_AMOUNT, bonusRate);
						//LoyaltyTransactionChild childTxbonus = createBonusTransaction(enrollRequest, contactsLoyalty, Double.valueOf(matchedBonus.getExtraBonusValue()), OCConstants.LOYALTY_TYPE_AMOUNT, bonusRate);
						LoyaltyTransactionChild childTxbonus = createBonusTransaction(enrollRequest, contactsLoyalty, bonusAmount, bonusPoints, OCConstants.LOYALTY_TYPE_AMOUNT, bonusRate);
								
						logger.info("balances before balance object = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
						createExpiryTransaction(contactsLoyalty, bonusPoints, bonusAmount, contactsLoyalty.getOrgId(), 
								childTxbonus.getTransChildId());
						
						if(tier != null){
							// CALL CONVERSION
							//applyConversionRules(contactsLoyaltyObj, childTxbonus, program, loyaltyProgramTier);
							// CALL TIER UPGD
							//loyaltyProgramTier = applyTierUpgradeRule(contactsLoyaltyObj, program, childTxbonus, loyaltyProgramTier);
							Long pointsDifference = 0l;
							Double amountDifference = 0.0;
							String[] diffBonArr = applyConversionRules(contactsLoyalty, tier); 
							logger.info("balances After conversion rules updatation --  points = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
							
							String conversionBonRate = null;
							long convertBonPoints = 0;
							double convertBonAmount = 0;
							if(diffBonArr != null){
								convertBonAmount = Double.valueOf(diffBonArr[0].trim());
								convertBonPoints = Double.valueOf(diffBonArr[1].trim()).longValue();
								conversionBonRate = diffBonArr[2];
							}
							pointsDifference = bonusPoints - convertBonPoints;
							amountDifference = (double)bonusAmount +  (diffBonArr != null ? Double.parseDouble(diffBonArr[0].trim()) : 0.0);
							tier = applyTierUpgradeRule(contactsLoyalty, program, childTxbonus, tier);
							updateBonusTransaction(childTxbonus, contactsLoyalty, ""+pointsDifference, ""+amountDifference, convertBonAmount, tier);						
						
						}
					}
				}
			}
			
			LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
			LoyaltyAutoComm autoComm = getLoyaltyAutoComm(program.getProgramId());
			if(bonusflag && autoComm != null && autoComm.getThreshBonusEmailTmpltId() != null && contactsLoyalty.getContact() != null &&
					contactsLoyalty.getContact().getContactId() != null){
				Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
				if(contact != null && contact.getEmailId() != null){
					autoCommGen.sendEarnBonusTemplate(autoComm.getThreshBonusEmailTmpltId(), ""+contactsLoyalty.getCardNumber(),
							contactsLoyalty.getCardPin(), contact.getUsers(), contact.getEmailId(), contact.getFirstName(),
							contact.getContactId(), contactsLoyalty.getLoyaltyId());
				}
			}
			UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = userDao.findByUserId(contactsLoyalty.getUserId());
			if (user.isEnableSMS() && bonusflag && autoComm != null && autoComm.getThreshBonusSmsTmpltId() != null){
				Long contactId = null;
				if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
					contactId = contactsLoyalty.getContact().getContactId();
				}
				autoCommGen.sendEarnBonusSMSTemplate(autoComm.getThreshBonusSmsTmpltId(), user, contactId,
							contactsLoyalty.getLoyaltyId(), null);
			}
			logger.info("bonus ...points = "+bonusPoints+" amount = "+bonusAmount);
			logger.info("Completed updateThresholdBonus method <<<");
			return;
		}catch(Exception e){
			logger.error("Exception in update threshold bonus...", e);
			throw new LoyaltyProgramException("Exception in threshold bonus...");
		}
	}
	
	private Contacts findContactById(Long cid) throws Exception {
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		return contactsDao.findById(cid);
	}
	
	/*private void applyConversionRules(ContactsLoyalty contactsLoyalty, LoyaltyTransactionChild transaction, LoyaltyProgram program, LoyaltyProgramTier tier){
		logger.info(" Entered into applyConversionRules method >>>");
		String[] differenceArr = null;

		try{
			if(tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO)){
				if(tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0 
						&& contactsLoyalty.getLoyaltyBalance() != null && contactsLoyalty.getLoyaltyBalance() > 0 
						&& contactsLoyalty.getLoyaltyBalance() >= tier.getConvertFromPoints()){
				
					differenceArr = new String[3];
					
					double multipledouble = contactsLoyalty.getLoyaltyBalance()/tier.getConvertFromPoints();
					//int multiple = (int)multipledouble;
					double multiple = (double)multipledouble;
					//double convertedAmount = tier.getConvertToAmount() * multiple;
					double convertedAmount = 0.0;
					String result = Utility.truncateUptoTwoDecimal(tier.getConvertToAmount() * multiple);
					if(result != null)
						convertedAmount  = Double.parseDouble(result);
					double subPoints = multiple * tier.getConvertFromPoints();
					
					differenceArr[0] = ""+convertedAmount;
					differenceArr[1] = ""+subPoints;
					differenceArr[2] = tier.getConvertFromPoints()+" Points -> "+tier.getConvertToAmount();
					
					logger.info("multiple factor = "+multiple);
					logger.info("Conversion amount ="+convertedAmount);
					logger.info("subtract points = "+subPoints);
					
					//update giftcard balance
					if(contactsLoyalty.getGiftcardBalance() == null ) {
						contactsLoyalty.setGiftcardBalance(convertedAmount);
					}
					else{
						contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() + convertedAmount);
					}
					if(contactsLoyalty.getTotalGiftcardAmount() == null){
						contactsLoyalty.setTotalGiftcardAmount(convertedAmount);
					}
					else{
						contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount() + convertedAmount);
					}
					
					transaction.setConversionAmt((long)convertedAmount);
					//deduct loyalty points
					contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() - subPoints);
					contactsLoyalty.setTotalLoyaltyRedemption(contactsLoyalty.getTotalLoyaltyRedemption() == null ? subPoints :
						contactsLoyalty.getTotalLoyaltyRedemption() + subPoints);
					
					logger.info("contactsLoyalty.getGiftcardBalance() = "+contactsLoyalty.getGiftcardBalance());
					transaction.setAmountBalance(contactsLoyalty.getLoyaltyBalance());
					transaction.setPointsBalance(contactsLoyalty.getGiftcardBalance());
					transaction.setGiftBalance(contactsLoyalty.getGiftBalance());
					
					// Deduct points or amount from expiry table
					deductPointsFromExpiryTable(contactsLoyalty.getLoyaltyId(), contactsLoyalty.getUserId(), subPoints, convertedAmount);
					logger.info("After conversion rules...subPoints = "+subPoints+" and convertedAmount = "+convertedAmount);
				}
			}
		
		}catch(Exception e){
			logger.error("Exception while applying auto conversion rules...", e);
		}
		logger.info("Completed applyConversionRules method <<<");
	}*/
private String[] applyConversionRules(ContactsLoyalty contactsLoyalty, LoyaltyProgramTier tier){
		
		String[] differenceArr = null;

		try{
			
			if(tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO)){
				
				if(tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0 
						&& contactsLoyalty.getLoyaltyBalance() != null && contactsLoyalty.getLoyaltyBalance() > 0 
						&& contactsLoyalty.getLoyaltyBalance() >= tier.getConvertFromPoints()){
				
					differenceArr = new String[3];
										
					double multipledouble = contactsLoyalty.getLoyaltyBalance()/tier.getConvertFromPoints();
					int multiple = (int)multipledouble;
					//double multiple = multipledouble;//Changes App-724,Release 2.5.6.0
					//double convertedAmount = tier.getConvertToAmount() * multiple;
					double convertedAmount = 0.0;
					String result = Utility.truncateUptoTwoDecimal(tier.getConvertToAmount() * multiple);
					if(result != null)
						convertedAmount = Double.parseDouble(result);
					//double subPoints = multiple * tier.getConvertFromPoints();
					String res = Utility.truncateUptoTwoDecimal(multiple * tier.getConvertFromPoints());
					double subPoints = 0.0;
					if(res != null)
						subPoints = Double.parseDouble(res);
					
					differenceArr[0] = ""+convertedAmount;
					differenceArr[1] = ""+subPoints;
					differenceArr[2] = tier.getConvertFromPoints().intValue()+" Points -> "+tier.getConvertToAmount().intValue();
					
					logger.info("multiple factor = "+multiple);
					logger.info("Conversion amount ="+convertedAmount);
					logger.info("subtract points = "+subPoints);
					
					
					//update giftcard balance
					if(contactsLoyalty.getGiftcardBalance() == null ) {
						contactsLoyalty.setGiftcardBalance(convertedAmount);
					}
					else{
						contactsLoyalty.setGiftcardBalance(new BigDecimal(contactsLoyalty.getGiftcardBalance() + convertedAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					}
					if(contactsLoyalty.getTotalGiftcardAmount() == null){
						contactsLoyalty.setTotalGiftcardAmount(convertedAmount);
					}
					else{
						contactsLoyalty.setTotalGiftcardAmount(new BigDecimal(contactsLoyalty.getTotalGiftcardAmount() + convertedAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					}
					
					//deduct loyalty points
					contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() - subPoints);
					contactsLoyalty.setTotalLoyaltyRedemption(contactsLoyalty.getTotalLoyaltyRedemption() == null ? subPoints :
						contactsLoyalty.getTotalLoyaltyRedemption() + subPoints);
					
					logger.info("contactsLoyalty.getGiftcardBalance() = "+contactsLoyalty.getGiftcardBalance());
					deductPointsFromExpiryTable(contactsLoyalty.getLoyaltyId(), contactsLoyalty.getUserId(), subPoints, convertedAmount);
				}
			}
		
		}catch(Exception e){
			logger.error("Exception while applying auto conversion rules...", e);
			return null;
		}
		return differenceArr;
	}
	
	private void deductPointsFromExpiryTable(Long loyaltyId, Long userId, double subPoints, double earnedAmt) throws Exception{
		logger.info(" Entered into deductPointsFromExpiryTable method >>>");
		
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
		List<LoyaltyTransactionExpiry> expiryList = null; //expiryDao.fetchExpPointsTrans(""+membershipNumber, 100, userId);
		Iterator<LoyaltyTransactionExpiry> iterList = null; //expiryList.iterator();
		LoyaltyTransactionExpiry expiry = null;
		long remainingPoints = (long)subPoints;
		
		do{
			expiryList = expiryDao.fetchExpLoyaltyPtsTrans(loyaltyId, 100, userId);
			if(expiryList == null) break;
			iterList = expiryList.iterator();
			
			while(iterList.hasNext()){
				
				logger.info("remainingPoints = "+remainingPoints +" earnedAmt = "+earnedAmt);
				expiry = iterList.next();
				
				if((expiry.getExpiryPoints() == null || expiry.getExpiryPoints() <= 0) && 
						(expiry.getExpiryAmount() == null || expiry.getExpiryAmount() <= 0)){
					logger.info("Wrong entry condition...");
				}
				else if(expiry.getExpiryPoints() < remainingPoints){
					logger.info("subtracted points = "+expiry.getExpiryPoints());
					remainingPoints = remainingPoints - expiry.getExpiryPoints().longValue();
					expiry.setExpiryPoints(0l);
					//expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					continue;
				}
				else if(expiry.getExpiryPoints() >= remainingPoints){
					logger.info("subtracted points = "+expiry.getExpiryPoints());
					expiry.setExpiryPoints(expiry.getExpiryPoints() - remainingPoints);
					remainingPoints = 0;
					if(expiry.getExpiryAmount() == null){
						expiry.setExpiryAmount(earnedAmt);
					}
					else{
						expiry.setExpiryAmount(expiry.getExpiryAmount() + earnedAmt);
					}
					//logger.info("expiry.getExpiryAmount() = "+expiry.getExpiryAmount()+ " earnedAmt = "+earnedAmt);
					//expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					//logger.info("expiry.getExpiryAmount() = "+expiry.getExpiryAmount()+ " earnedAmt = "+earnedAmt);
					break;
				}
			}
		
		}while(remainingPoints > 0 && expiryList != null);
		logger.info("Completed deductPointsFromExpiryTable method <<<");
	}
	
	private LoyaltyAutoComm getLoyaltyAutoComm(Long programId){
		try{
			LoyaltyAutoCommDao autoCommDao = (LoyaltyAutoCommDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
			return autoCommDao.findById(programId);
		}catch(Exception e){
			logger.error("Exception in getting auto comm object...", e);
			return null;
		}
	}
	
	private Map<String,Object> validateAndSavedbContact(Contacts jsonContact, MailingList mlList, Users user) throws Exception {
		
		Contacts dbContact = findOCContact(jsonContact, user);
		boolean isExists = false;
		
		PurgeList purgeList = (PurgeList)ServiceLocator.getInstance().getServiceById(OCConstants.PURGELIST);
		boolean updateMLFlag = false;
		boolean isEnableEvent = false;
		boolean isNewContact = false;
		
		if(dbContact == null) {
			logger.info("New Contact...");
			dbContact = jsonContact;
			logger.info("In Validate Contact method dbContact = "+dbContact);
			//dbContact.setEmailId(jsonContact.getEmailId());
			dbContact.setEmailId(validateEmailId(dbContact.getEmailId()));
			dbContact.setMlBits(mlList.getMlBit());
			dbContact.setUsers(user);
			dbContact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_POS);
			dbContact.setEmailStatus(Constants.CONT_STATUS_PURGE_PENDING);
			dbContact.setCreatedDate(Calendar.getInstance());
			
			isNewContact = true;
			updateMLFlag = true;
			isEnableEvent = true;
			
			dbContact.setPurged(false);
			dbContact.setMobilePhone(dbContact.getMobilePhone() == null ? null : Utility.phoneParse(dbContact.getMobilePhone(),user!=null ? user.getUserOrganization() : null ));
			purgeList.checkForValidDomainByEmailId(dbContact);
			validateMobilePhoneStatus(dbContact);
		}
		else {
			logger.info("Existing contact.");
			if(jsonContact.getExternalId() != null){
				isExists = true;
				/*if(dbContact.getExternalId() == null || dbContact.getExternalId().trim().isEmpty())
					dbContact.setExternalId(jsonContact.getExternalId());*/
			}
			
			String emailStatus = dbContact.getEmailStatus();
			boolean emailFlag = dbContact.getPurged();
			boolean purgeFlag = false;
			long contactBit = dbContact.getMlBits().longValue();
			if((dbContact.getEmailId() != null && jsonContact.getEmailId() != null &&	! dbContact.getEmailId().equalsIgnoreCase(
					jsonContact.getEmailId())) || (dbContact.getEmailId() == null && jsonContact.getEmailId() != null) ) {
				emailStatus = Constants.CONT_STATUS_PURGE_PENDING;
				emailFlag = false;
				purgeFlag = true;
			}
			if( contactBit == 0l ) {//deleted contact ,need to be triggered action
				dbContact.setMlBits(mlList.getMlBit());
				dbContact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_POS);
				emailStatus = Constants.CONT_STATUS_PURGE_PENDING;
				emailFlag = false;
				purgeFlag = true;
				isEnableEvent = true;
				updateMLFlag = true;
				dbContact.setUsers(user);
			}
			else{
				if(mlList != null && ( (contactBit & mlList.getMlBit() ) <= 0) ) { //add existing contact to POS if it is not there in it
					dbContact.setMlBits(contactBit | mlList.getMlBit());
					updateMLFlag = true;
				}
			}
			dbContact = Utility.mergeContacts(jsonContact, dbContact);
			//perform mobile optin
			validateMobilePhoneStatus(dbContact);

			dbContact.setEmailStatus(emailStatus);
			dbContact.setPurged(emailFlag);
			if(purgeFlag) {
				purgeList.checkForValidDomainByEmailId(dbContact);
			}
			
			/*if(dbContact.getMlBits().longValue() == 0l ) {//marked as deleted
				
				dbContact.setAddressOne(jsonContact.getAddressOne());
				dbContact.setAddressTwo(jsonContact.getAddressTwo());
				dbContact.setAnniversary(jsonContact.getAnniversary());
				dbContact.setBirthDay(jsonContact.getBirthDay());
				dbContact.setCity(jsonContact.getCity());
				dbContact.setCountry(jsonContact.getCountry());
				dbContact.setEmailId(validateEmailId(dbContact.getEmailId()));
				dbContact.setExternalId(jsonContact.getExternalId());
				dbContact.setFirstName(jsonContact.getFirstName());
				dbContact.setGender(jsonContact.getGender());
				dbContact.setLastName(jsonContact.getLastName());
				dbContact.setMobilePhone(jsonContact.getMobilePhone());
				validateMobilePhoneStatus(dbContact);
				
				isEnableEvent = true;
				updateMLFlag = true;
				
				dbContact.setPurged(false);
				dbContact.setMlBits(mlList.getMlBit());
				dbContact.setUsers(user);
				dbContact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_POS);
				dbContact.setEmailStatus(Constants.CONT_STATUS_PURGE_PENDING);
				purgeList.checkForValidDomainByEmailId(dbContact);
			}
			else{
				long conMlBits = dbContact.getMlBits();
				if(mlList != null && ( (conMlBits & mlList.getMlBit() ) <= 0) ) { //add existing contact to POS if it is not there in it
					dbContact.setMlBits(conMlBits | mlList.getMlBit());
					updateMLFlag = true;
				}
			}*/
		}
		
		ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
		contactsDaoForDML.saveOrUpdate(dbContact);
		
		MailingListDao mailingListDao = (MailingListDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
		MailingListDaoForDML mailingListDaoForDML = (MailingListDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.MAILINGLIST_DAO_FOR_DML);
		if(updateMLFlag) {
			
			mlList.setListSize(mlList.getListSize() + 1);
			mlList.setLastModifiedDate(Calendar.getInstance());
			mailingListDaoForDML.saveOrUpdate(mlList);
		}
		
		if(isEnableEvent){
			EventTriggerEventsObservable eventTriggerEventsObservable=(EventTriggerEventsObservable) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVABLE);
			EventTriggerEventsObserver eventTriggerEventsObserver=(EventTriggerEventsObserver) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVER);
			
			eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
			eventTriggerEventsObservable.notifyForWebEvents(user.getUserId().longValue(),
					dbContact.getContactId().longValue(), dbContact.getContactId().longValue() );
			
		}
		
		if(isNewContact  && !mlList.getCheckDoubleOptin() && mlList.isCheckWelcomeMsg()) {
			sendWelcomeEmail(dbContact, mlList, user);
		}
		//logger.info("Exited validatedbContact method >>>>>");
		Map<String, Object> returnValues = new HashMap<String, Object>();
		returnValues.put("dbContact", dbContact);
		returnValues.put("isExists", isExists);
		return returnValues;
	}
	
	private String validateEmailId(String emailId){
		//logger.info("Entered validateEmailId method >>>>");
		if(emailId != null) {
			if(Utility.validateEmail(emailId)) {
				//logger.info("Exited validateEmailId method >>>>");
				return emailId;
			}
		}
		//logger.info("Exited validateEmailId method >>>>");
		return null;
	}
	/**
	 * Validates mobilephone and sets appropriate message
	 * 
	 * @param dbContact
	 */
	private void validateMobilePhoneStatus(Contacts dbContact) throws Exception{
		//logger.info("Entered validateMobilePhoneStatus method >>>>");
		if(dbContact.getMobilePhone() != null && dbContact.getMobilePhone().trim().length() > 0) {
			//dbContact.setMobileStatus(performMobileOptin(dbContact, dbContact.getUsers()));
			try {
				Users user = dbContact.getUsers();
				String phoneStr = Utility.phoneParse(dbContact.getMobilePhone().toString().trim(), user!=null ? user.getUserOrganization() : null );
				if(phoneStr != null ) {
					dbContact.setMobilePhone(phoneStr);
					if(dbContact.getUsers().isEnableSMS() && dbContact.getUsers().isConsiderSMSSettings()){
						dbContact.setMobileStatus(performMobileOptin(dbContact, dbContact.getUsers()));
					}else{
						dbContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
					}
				}else {
					dbContact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
				}
			} catch(Exception e) {
				logger.error("Exception in phone parse", e);
			}
		}	
		else{
			dbContact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
			dbContact.setMobileOptin(false);
		}
		//logger.info("Exited validateMobilePhoneStatus method >>>>");
	}
	
	/**
	 * Checks whether given contact is exist in oc. It searches by external id, email id and mobile phone.
	 * If given contact is found in db, it returns db contact object.
	 * 
	 * @param jsonContact
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	private Contacts findOCContact(Contacts jsonContact, Users user) throws Exception {
		//logger.info("Entered findOCContact method >>>>");
		POSMappingDao posMappingDao = (POSMappingDao)ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		TreeMap<String, List<String>> priorMap =  Utility.getPriorityMap(user.getUserId(), Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
		Contacts dbContact = contactsDao.findContactByUniqPriority(priorMap, jsonContact, user.getUserId(), user);
		//logger.info("Exited findOCContact method >>>>");
		return dbContact;
	}
	
	private Customer findCustomer(String cardNumber, Long programId, Long userId) throws Exception {
		
		ContactsLoyalty loyalty  = findMembershpByCard(cardNumber, programId, userId);
		if(loyalty == null || loyalty.getContact() == null || loyalty.getContact().getContactId() == null) return null;
		
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		Contacts contact = contactsDao.findById(loyalty.getContact().getContactId());
		if(contact == null) return null;
		
		return prepareCustomerFromContact(contact);
	}
	
	private Customer prepareCustomerFromContact(Contacts contact){
		
		Customer customer = new Customer();
		
		customer.setAddressLine1(contact.getAddressOne() == null ? "" : contact.getAddressOne());
		customer.setAddressLine2(contact.getAddressTwo() == null ? "" : contact.getAddressTwo());
		if(contact.getAnniversary() == null){
			customer.setAnniversary("");
		}else{
			String anniversaryDate = MyCalendar.calendarToString(contact.getAnniversary(), MyCalendar.FORMAT_DATETIME_STYEAR);
			customer.setAnniversary(anniversaryDate);
		}
		if(contact.getBirthDay() == null){
			customer.setBirthday("");
		}else{
			String birthDate = MyCalendar.calendarToString(contact.getBirthDay(), MyCalendar.FORMAT_DATETIME_STYEAR);
			customer.setBirthday(birthDate);
		}
		
		customer.setCity(contact.getCity() == null ? "" : contact.getCity());
		customer.setCountry(contact.getCountry() == null ? "" : contact.getCountry());
		customer.setCustomerId(contact.getExternalId() == null ? "" : contact.getExternalId());
		customer.setEmailAddress(contact.getEmailId() == null ? "" : contact.getEmailId());
		customer.setFirstName(contact.getFirstName() == null ? "" : contact.getFirstName());
		customer.setGender(contact.getGender() == null ? "" : contact.getGender());
		customer.setLastName(contact.getLastName() == null ? "" : contact.getLastName());
		customer.setPhone(contact.getPhone() == null ? "" : ""+contact.getPhone());
		customer.setPostal(contact.getZip() == null ? "" : contact.getZip());
		customer.setState(contact.getState() == null ? "" : contact.getState());
		
		return customer;
	}
	
	
	private Customer findCustomerByCid(Contacts contact, Long userId) throws Exception {
		
		if(contact == null || contact.getContactId() == null) return null;
		
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		Contacts contact1 = contactsDao.findById(contact.getContactId());
		if(contact1 == null) return null;
		
		return prepareCustomerFromContact(contact);
	}
	
	
	private List<MatchedCustomer> prepareMatchedCustomers(Customer customer, String membershipNumber){
		
		List<MatchedCustomer> matchedCustomers = new ArrayList<MatchedCustomer>();
		MatchedCustomer matchedCustomer = new MatchedCustomer();
		matchedCustomer.setCustomerId(customer.getCustomerId() == null ? "" : customer.getCustomerId());
		matchedCustomer.setEmailAddress(customer.getEmailAddress() == null ? "" : customer.getEmailAddress());
		matchedCustomer.setFirstName(customer.getFirstName() == null ? "" : customer.getFirstName());
		matchedCustomer.setLastName(customer.getLastName() == null ? "" : customer.getLastName());
		matchedCustomer.setMembershipNumber(membershipNumber == null ? "" : membershipNumber);
		matchedCustomer.setPhone(customer.getPhone() == null ? "" : customer.getPhone());
		
		matchedCustomers.add(matchedCustomer);
		return matchedCustomers;
	}
	
	private Customer prepareCustomer(Contacts contact) throws Exception {
		
		Customer customer = new Customer();
		
		customer.setAddressLine1(contact.getAddressOne() == null ? "" : contact.getAddressOne());
		customer.setAddressLine2(contact.getAddressTwo() == null ? "" : contact.getAddressTwo());
		customer.setAnniversary(contact.getAnniversary() == null ? "" : ""+contact.getAnniversary());
		customer.setBirthday(contact.getBirthDay() == null ? "" : ""+contact.getBirthDay());
		customer.setCity(contact.getCity() == null ? "" : ""+contact.getCity());
		customer.setCountry(contact.getCountry() == null ? "" : ""+contact.getCountry());
		customer.setCustomerId(contact.getExternalId() == null ? "" : ""+contact.getExternalId());
		customer.setEmailAddress(contact.getEmailId() == null ? "" : ""+contact.getEmailId());
		customer.setFirstName(contact.getFirstName() == null ? "" : ""+contact.getFirstName());
		customer.setGender(contact.getGender() == null ? "" : ""+contact.getGender());
		customer.setLastName(contact.getLastName() == null ? "" : ""+contact.getLastName());
		customer.setPhone(contact.getMobilePhone() == null ? "" : ""+contact.getMobilePhone());
		customer.setPostal(contact.getZip() == null ? "" : ""+contact.getZip());
		customer.setState(contact.getState() == null ? "" : ""+contact.getState());
		
		return customer;
		
	}
	
	
	private LoyaltyTransactionExpiry createExpiryTransaction(ContactsLoyalty loyalty,
			Long expiryPoints, Double expiryAmount, Long orgId, Long transChildId ){
		
		LoyaltyTransactionExpiry transaction = null;
		try{
			
			transaction = new LoyaltyTransactionExpiry();
			transaction.setTransChildId(transChildId);
			transaction.setMembershipNumber(""+loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCreatedDate(Calendar.getInstance());
			transaction.setOrgId(orgId);
			transaction.setUserId(loyalty.getUserId());
			transaction.setExpiryPoints(expiryPoints);
			transaction.setExpiryAmount(expiryAmount);
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setRewardFlag(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			
			LoyaltyTransactionExpiryDao loyaltyTransactionExpiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
			LoyaltyTransactionExpiryDaoForDML loyaltyTransactionExpiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
			//loyaltyTransactionExpiryDao.saveOrUpdate(transaction);
			loyaltyTransactionExpiryDaoForDML.saveOrUpdate(transaction);
			
		}catch(Exception e){
			logger.error("Exception while logging enroll bonus expiry transaction...",e);
		}
		return transaction;
	}
	
	//private LoyaltyTransactionChild createBonusTransaction(ContactsLoyalty loyalty,	double earnedValue, String earnType, String bonusRate){
	//private LoyaltyTransactionChild createBonusTransaction(LoyaltyEnrollRequest enrollRequest , ContactsLoyalty loyalty,	double earnedValue, String earnType, String bonusRate){
	private LoyaltyTransactionChild createBonusTransaction(LoyaltyEnrollRequest enrollRequest , ContactsLoyalty loyalty,	double earnedAmount, double earnedPoints, String earnType, String bonusRate){

		LoyaltyTransactionChild transaction = null;
		try{
			
			transaction = new LoyaltyTransactionChild();
			transaction.setMembershipNumber(""+loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCardSetId(loyalty.getCardSetId());
			
			//transaction.setCreatedDate(Calendar.getInstance());
			if(enrollRequest.getCustomer().getCreatedDate() != null && !enrollRequest.getCustomer().getCreatedDate().trim().isEmpty()){
				String requestDate = enrollRequest.getCustomer().getCreatedDate();  
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
			else{				
				transaction.setCreatedDate(Calendar.getInstance());
			}
			transaction.setEarnType(earnType);
			if(earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)){
				transaction.setEarnedPoints((double)earnedPoints);
				transaction.setEnteredAmount((double)earnedPoints);
			}
			else if(earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
				transaction.setEarnedAmount((double)earnedAmount);
				transaction.setEnteredAmount((double)earnedAmount);
			}
			//transaction.setEnteredAmount((double)earnedValue);
			transaction.setOrgId(loyalty.getOrgId());
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setUserId(loyalty.getUserId());
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_BONUS);
			transaction.setDescription(bonusRate);
			transaction.setSourceType(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_AUTO);
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			//loyaltyTransactionChildDao.saveOrUpdate(transaction);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
			
		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
		}
		return transaction;
	}
	
	private Status validateStoreNumberExclusion(LoyaltyEnrollRequest enrollRequest, LoyaltyProgram program, 
			LoyaltyProgramExclusion loyaltyExclusion) throws Exception {
		
		Status status = null;
		if(loyaltyExclusion.getStoreNumberStr() != null && !loyaltyExclusion.getStoreNumberStr().trim().isEmpty()){
			if(enrollRequest.getHeader().getStoreNumber() == null || enrollRequest.getHeader().getStoreNumber().length() <= 0){
				status = new Status("111501", PropertyUtil.getErrorMessage(111501, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
				
			}
			String[] storeNumberArr = loyaltyExclusion.getStoreNumberStr().split(";=;");
			for(String storeNo : storeNumberArr){
				if(enrollRequest.getHeader().getStoreNumber().trim().equals(storeNo.trim())){
					status = new Status("111532", PropertyUtil.getErrorMessage(111532, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return status;
				}
			}
		}
		
		return status;
	}
	
	private LoyaltyProgramExclusion getLoyaltyExclusion(Long programId) throws Exception {
		try{
		LoyaltyProgramExclusionDao exclusionDao = (LoyaltyProgramExclusionDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_EXCLUSION_DAO);
		return exclusionDao.getExlusionByProgId(programId);
		}catch(Exception e){
			logger.error("Exception in getting loyalty exclusion ..", e);
		}
		return null;
	}

	private Contacts setContactFields(Contacts inputContact,List<POSMapping> contactPOSMap, LoyaltyEnrollRequest enrollRequest,String memType)throws BaseServiceException {
		Class strArg[] = new Class[] { String.class };
		Class calArg[] = new Class[] { Calendar.class };

		logger.debug("-------entered setContactFields---------");
		for(POSMapping posMapping : contactPOSMap){

			String custFieldAttribute = posMapping.getCustomFieldName();
			String fieldValue=getFieldValue(enrollRequest,posMapping,memType);
			if(fieldValue == null || fieldValue.trim().length() <= 0){
				logger.info("custom field value is empty ...for field : "+custFieldAttribute);
				continue;
			}
			fieldValue = fieldValue.trim();
			String dateTypeStr = null;
			dateTypeStr = posMapping.getDataType();
			if(dateTypeStr == null || dateTypeStr.trim().length() <=0){
				continue;
			}
			if(dateTypeStr.startsWith("Date")){
				String dateValue = getDateFormattedData(posMapping, fieldValue);
				if(dateValue == null) continue;
			}

			Object[] params = null;
			Method method = null;
			try {

				if (custFieldAttribute.equals(POSFieldsEnum.emailId.getOcAttr()) && fieldValue.length() > 0 &&
						Utility.validateEmail(fieldValue)) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.emailId.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.firstName.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.firstName.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.lastName.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.lastName.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.addressOne.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.addressOne.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.addressTwo.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.addressTwo.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.city.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.city.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.state.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.state.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.country.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.country.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.zip.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.zip.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.mobilePhone.getOcAttr()) && fieldValue.length() > 0) {
					Users user = inputContact.getUsers();
					String phoneParse = Utility.phoneParse(fieldValue,user!=null ? user.getUserOrganization() : null );
					if(phoneParse != null){
						//logger.info("after phone parse: "+phoneParse);
						method = Contacts.class.getMethod("set" + POSFieldsEnum.mobilePhone.getPojoField(), strArg);
						params = new Object[] { phoneParse };
					}
				}

				else if (custFieldAttribute.equals(POSFieldsEnum.externalId.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.externalId.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}

				else if (custFieldAttribute.equals(POSFieldsEnum.gender.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.gender.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}

				else if (custFieldAttribute.equals(POSFieldsEnum.birthDay.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.birthDay.getPojoField(), calArg);
					try {
						String dateformat = dateTypeStr.substring(dateTypeStr.indexOf("(")+1, dateTypeStr.indexOf(")"));
						DateFormat formatter ; 
						Date date ; 
						formatter = new SimpleDateFormat(dateformat);
						date = (Date)formatter.parse(fieldValue); 
						Calendar dobCal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(dateformat));
						dobCal.setTime(date);
						params = new Object[] { dobCal };
						//contact.setBirthDay(dobCal);
					} catch (Exception e) {
						logger.info("BirthDay date format not matched with data",e);
					}

				}

				else if (custFieldAttribute.equals(POSFieldsEnum.anniversary.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.anniversary.getPojoField(), calArg);
					try {
						String dateformat = dateTypeStr.substring(dateTypeStr.indexOf("(")+1, dateTypeStr.indexOf(")"));
						DateFormat formatter ; 
						Date date ; 
						formatter = new SimpleDateFormat(dateformat);
						date = (Date)formatter.parse(fieldValue); 
						Calendar dobCal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(dateformat));
						dobCal.setTime(date);
						params = new Object[] { dobCal };
						//contact.setBirthDay(dobCal);
					} catch (Exception e) {
						logger.info("Anniversary date format not matched with data",e);
					}
				}
				if (method != null) {
					try {
						method.invoke(inputContact, params);
						//logger.info("method name:  "+method.getName()+" field value: "+fieldValue);
					} catch (Exception e) {
						logger.error("Exception ::" , e);
					} 
				}
			} catch (Exception e) {
				//logger.info("securityexception");
				logger.error("Exception ::" , e);
			} 
			logger.debug("-------exit  setContactFields---------");
		}
		//logger.info("set contact data input contact: mobile: "+inputContact.getMobilePhone());
		return inputContact;
	}//setContactFields

	private String getDateFormattedData(POSMapping posMapping, String fieldValue) throws BaseServiceException{
		String dataTypeStr = posMapping.getDataType();
		String dateFieldValue = null;
		logger.debug("-------entered getDateFormattedData---------");
		//String custfieldName = posMapping.getCustomFieldName();
		if(posMapping.getDataType().trim().startsWith("Date")) {
			try {
				String dateFormat = dataTypeStr.substring(dataTypeStr.indexOf("(")+1, dataTypeStr.indexOf(")"));
				if(!Utility.validateDate(fieldValue, dateFormat)) {
					return null;
				}
				DateFormat formatter ; 
				Date date ; 
				formatter = new SimpleDateFormat(dateFormat);
				date = (Date)formatter.parse(fieldValue); 
				Calendar cal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(dateFormat));
				cal.setTime(date);
				dateFieldValue= MyCalendar.calendarToString(cal, MyCalendar.dateFormatMap.get(dateFormat));
			} catch (Exception e) {
				logger.error("Exception  ::", e);
				throw new BaseServiceException("Exception occured while processing getDateFormattedData::::: ", e);
			}
		}
		logger.debug("-------exit  getDateFormattedData---------");
		return dateFieldValue;
	}//getDateFormattedData

	private String getFieldValue(LoyaltyEnrollRequest enrollRequest,POSMapping posMapping,String memType)throws BaseServiceException {
		String fieldValue=null;
		logger.debug("-------entered getFieldValue---------");
		if(posMapping.getCustomFieldName().equalsIgnoreCase("street")) {
			fieldValue = enrollRequest.getCustomer().getAddressLine1();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("address two")) {
			fieldValue = enrollRequest.getCustomer().getAddressLine2();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("email")) {
			fieldValue = enrollRequest.getCustomer().getEmailAddress();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("mobile")) {
			if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE.equals(memType) && 
					enrollRequest.getMembership().getPhoneNumber() != null && enrollRequest.getMembership().getPhoneNumber().trim().length() > 0) {
				fieldValue = enrollRequest.getMembership().getPhoneNumber().trim();
			}
			else {
				fieldValue = enrollRequest.getCustomer().getPhone();
			}
//			fieldValue = enrollRequest.getCustomer().getPhone();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("first name")) {
			fieldValue = enrollRequest.getCustomer().getFirstName();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("last name")) {
			fieldValue = enrollRequest.getCustomer().getLastName();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("city")) {
			fieldValue = enrollRequest.getCustomer().getCity();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("state")) {
			fieldValue = enrollRequest.getCustomer().getState();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("country")) {
			fieldValue = enrollRequest.getCustomer().getCountry();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("zip")) {
			fieldValue = enrollRequest.getCustomer().getPostal();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("gender")) {
			fieldValue = enrollRequest.getCustomer().getGender();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("customer id")) {
			fieldValue = enrollRequest.getCustomer().getCustomerId();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("birthday")) {
			fieldValue = enrollRequest.getCustomer().getBirthday();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("anniversary")) {
			fieldValue = enrollRequest.getCustomer().getAnniversary();
			return fieldValue;
		}
		logger.debug("-------exit  getFieldValue---------");
		return fieldValue;
	}//getFieldValue
	
	private ContactsLoyalty getDestMembershipIfAny(ContactsLoyalty contactLoyalty) throws Exception{
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		if(contactLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED) && contactLoyalty.getTransferedTo() != null) {
			return loyaltyDao.findAllByLoyaltyId(contactLoyalty.getTransferedTo());
			
		}
		
		return null;
	}
	private LoyaltyCards findLoyaltyCardByUserId(String cardNumber, Long userId) throws Exception {
		
		LoyaltyCardsDao loyaltyCardDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		return loyaltyCardDao.findByCardNoAnduserId(cardNumber, userId);
		
	}
	private void updateBonusTransaction(LoyaltyTransactionChild transaction, ContactsLoyalty loyalty,
			String ptsDiff, String amtDiff, double convertAmt, LoyaltyProgramTier tier){
		
		try{
			
			transaction.setAmountDifference(Utility.truncateUptoTwoDecimal(Double.parseDouble(amtDiff)));
			transaction.setPointsDifference(ptsDiff);
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			//transaction.setDescription(conversionRate);
			transaction.setConversionAmt(convertAmt);
			transaction.setTierId(tier.getTierId());
			
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			//loyaltyTransactionChildDao.saveOrUpdate(transaction);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
			
		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
		}
	}
	

}
