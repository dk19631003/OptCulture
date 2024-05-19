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
import org.mq.marketer.campaign.beans.OrganizationStores;
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
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
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
import org.mq.optculture.model.ocloyalty.MembershipRequest;
import org.mq.optculture.model.ocloyalty.MembershipResponse;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;

public class LoyaltyOCEnrollmentServiceImpl implements LoyaltyEnrollmentOCService {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException {
		// TODO Auto-generated method stub

		logger.info("Started processRequest...");

		BaseResponseObject responseObject = null;
		String serviceRequest = baseRequestObject.getAction();
		String requestJson = baseRequestObject.getJsonValue();
		String responseJson = null;
		Gson gson = new Gson();
		LoyaltyEnrollResponse enrollResponse = null;
		LoyaltyEnrollRequest enrollRequest = null;

		if (serviceRequest == null || !serviceRequest.equals(OCConstants.LOYALTY_SERVICE_ACTION_ENROLMENT)) {
			logger.info("servicerequest is null...");

			Status status = new Status("101001",
					"" + PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);

			enrollResponse = new LoyaltyEnrollResponse();
			enrollResponse.setStatus(status);
			responseJson = gson.toJson(enrollResponse);

			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			logger.info("Exited baserequest due to invalid service");
			return responseObject;
		}

		try {
			enrollRequest = gson.fromJson(requestJson, LoyaltyEnrollRequest.class);
		} catch (Exception e) {

			Status status = new Status("101001",
					"" + PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);

			enrollResponse = new LoyaltyEnrollResponse();
			enrollResponse.setStatus(status);
			responseJson = gson.toJson(enrollResponse);

			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			logger.info("Exited baserequest due to invalid JSON ", e);
			return responseObject;
		}

		try {
			LoyaltyEnrollmentOCService loyaltyEnrollService = (LoyaltyEnrollmentOCService) ServiceLocator.getInstance()
					.getServiceByName(OCConstants.LOYALTY_ENROLMENT_OC_BUSINESS_SERVICE);
			enrollResponse = loyaltyEnrollService.processEnrollmentRequest(enrollRequest,
					OCConstants.LOYALTY_ONLINE_MODE, baseRequestObject.getTransactionId(),
					baseRequestObject.getTransactionDate());
			responseJson = gson.toJson(enrollResponse);

			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
		} catch (Exception e) {
			logger.error("Exception in loyalty enroll base service.", e);
			throw new BaseServiceException("Server Error.");
		}
		logger.info("Completed processing baserequest... ");
		return responseObject;

	}

	@Override
	public LoyaltyEnrollResponse processEnrollmentRequest(LoyaltyEnrollRequest enrollRequest, String mode,
			String transactionId, String transactionDate) throws BaseServiceException {
		// TODO Auto-generated method stub

		logger.info("Started processing enrollment request...");
		LoyaltyEnrollResponse enrollResponse = null;
		Status status = null;
		Users user = null;
		MembershipResponse membershipResponse = null;
		
		LoyaltyCardSetDao loyaltyCardSetDao = null;
		LoyaltyProgramDao loyaltyProgramDao = null;
		ResponseHeader responseHeader = new ResponseHeader();
		responseHeader.setRequestDate(enrollRequest.getHeader().getRequestDate());
		responseHeader.setRequestId(enrollRequest.getHeader().getRequestId());
		responseHeader.setTransactionDate(transactionDate);
		responseHeader.setTransactionId(transactionId);
		responseHeader.setSourceType(enrollRequest.getHeader().getSourceType() != null
				&& !enrollRequest.getHeader().getSourceType().trim().isEmpty()
						? enrollRequest.getHeader().getSourceType().trim()
						: Constants.STRING_NILL);
		try {
			loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance()
					.getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			loyaltyCardSetDao = (LoyaltyCardSetDao) ServiceLocator.getInstance()
					.getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);

			// Basic validations including store number validation
			status = validateEnrollmentJsonData(enrollRequest);
			if (status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())) {
				enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
				return enrollResponse;
			}
			user = getUser(enrollRequest.getUser().getUserName(), enrollRequest.getUser().getOrganizationId(),
					enrollRequest.getUser().getToken());
			enrollResponse = validateUserObject(enrollRequest, responseHeader, user);
			if (enrollResponse != null) {
				return enrollResponse;
			}
			//for loyaltyapps prioritise between the programs
			if(enrollRequest.getHeader().getSourceType() != null && 
					!enrollRequest.getHeader().getSourceType().isEmpty() && 
					(enrollRequest.getHeader().getSourceType() != null && enrollRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) || 
							enrollRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP))){
				
				
				LoyaltyProgram defaultLoyaltyProgram = findDefaultProgram(user.getUserId());
				if(defaultLoyaltyProgram !=null) {
					if(defaultLoyaltyProgram.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
						if((enrollRequest.getMembership().getCardNumber() == null || 
								enrollRequest.getMembership().getCardNumber().trim().isEmpty()) && 
								(enrollRequest.getMembership().getPhoneNumber() == null || enrollRequest.getMembership().getPhoneNumber().trim().isEmpty())
								&& enrollRequest.getMembership().getIssueCardFlag() != null && 
								enrollRequest.getMembership().getIssueCardFlag().equalsIgnoreCase("Y") &&
								(enrollRequest.getCustomer().getPhone() != null && !enrollRequest.getCustomer().getPhone().trim().isEmpty())	){
							
							MembershipRequest membership = enrollRequest.getMembership();
							membership.setIssueCardFlag("");
							membership.setPhoneNumber(enrollRequest.getCustomer().getPhone().trim());
							enrollRequest.setMembership(membership);
							
						}
						
					}
				}
				
			}
			
			// if the user has loyalty type oc(changed during migrationsbtooc)
			if (OCConstants.LOYALTY_SERVICE_TYPE_OC.equals(user.getloyaltyServicetype())) {

				if (enrollRequest.getHeader().getStoreNumber() == null
						|| enrollRequest.getHeader().getStoreNumber().length() <= 0) {
					status = new Status("111501", PropertyUtil.getErrorMessage(111501, OCConstants.ERROR_LOYALTY_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					return enrollResponse;
				}
				
			//updating subsidiary to request	
				if((enrollRequest.getHeader().getSubsidiaryNumber() == null || (enrollRequest.getHeader().getSubsidiaryNumber().isEmpty()) ) &&
						enrollRequest.getHeader().getStoreNumber() != null && !enrollRequest.getHeader().getStoreNumber().isEmpty()){
					OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
					UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
					Long domainId = userDao.findDomainByUserId(user.getUserId());
					if(domainId!=null){
					OrganizationStores orgStores = organizationStoresDao.findOrgByDomain(user.getUserOrganization().getUserOrgId(), domainId, enrollRequest.getHeader().getStoreNumber());
					enrollRequest.getHeader().setSubsidiaryNumber(orgStores!=null ? orgStores.getSubsidiaryId() : null);
				    }
				}

			}

			MailingListDao mailingListDao = (MailingListDao) ServiceLocator.getInstance()
					.getDAOByName(OCConstants.MAILINGLIST_DAO);
			MailingList mlList = mailingListDao.findPOSMailingList(user);

			if (mlList == null) {
				status = new Status("101007", PropertyUtil.getErrorMessage(101007, OCConstants.ERROR_LOYALTY_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
				return enrollResponse;
			}

			LoyaltyCards loyaltyCard = null;
			LoyaltyCardSet cardSetObj = null;
			LoyaltyProgram loyaltyProgram = null;
			ContactsLoyalty contactLoyalty = null;
			boolean isDCard = false;
			// LoyaltyProgram DCardBasedProgram = null;
			if (enrollRequest.getMembership().getCardNumber() != null
					&& enrollRequest.getMembership().getCardNumber().trim().length() > 0) {

				logger.info("Enrollment with card number : " + enrollRequest.getMembership().getCardNumber());

				String cardLong = null;

				

				cardLong = OptCultureUtils
						.validateOCLtyCardNumber(enrollRequest.getMembership().getCardNumber().trim());// Instead of
																										// Long check?.
				
				if (cardLong != null && checkIfLong(cardLong)) {
					loyaltyCard = findLoyaltyCardByUserId(cardLong, user.getUserId());
					
					if(loyaltyCard == null) {
						/*
						 * As there is only one D-card program for a user. Directly getting the Program
						 * from DB
						 */
						
						
					loyaltyProgram = loyaltyProgramDao.getDynamicProgramByUserId(user.getUserId());
							if (loyaltyProgram == null || loyaltyProgram.getStatus() == null
							|| loyaltyProgram.getStatus().isEmpty() || !loyaltyProgram.getStatus().trim()
									.equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)) {
						String msg = PropertyUtil.getErrorMessage(111593, OCConstants.ERROR_LOYALTY_FLAG);
						status = new Status("111593", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						return enrollResponse;
					}
					String validationRule = loyaltyProgram.getValidationRule();
					if (validationRule != null) {

						cardLong = OptCultureUtils.validateOCLtyDCardNumber(
								enrollRequest.getMembership().getCardNumber().trim(), validationRule);
						if (cardLong == null) {
							String val[] = validationRule.split(Constants.ADDR_COL_DELIMETER);
							String msg = PropertyUtil.getErrorMessage(100108, OCConstants.ERROR_LOYALTY_FLAG) + " "
									+ (val[0].equalsIgnoreCase("N") ? "Numeric" : "Alphanumeric")
									+ " and length should be " + val[1]
									+ (val[0].equalsIgnoreCase("N") ? " digits" : " characters") + ".";
							status = new Status("100108", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
							return enrollResponse;
						}
						enrollRequest.getMembership().setCardNumber(cardLong);
					}
					cardSetObj = loyaltyCardSetDao.findActiveSetByProgramId(loyaltyProgram.getProgramId()).get(0);
					loyaltyCard = insertDCard(cardLong, enrollRequest.getMembership().getCardPin(), user,
							loyaltyProgram, cardSetObj);
					if (loyaltyCard == null) {
						String msg = PropertyUtil.getErrorMessage(111584, OCConstants.ERROR_LOYALTY_FLAG) + " "
								+ cardLong + ".";
						status = new Status("111584", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						logger.info(
								"Another enrollment request is being processed on the entered card-number-" + cardLong);
						return enrollResponse;

					}
				
						
						
					}
					
					else { //card exists in OC
						
						status = validateCard(loyaltyCard,user,enrollRequest);
						ContactsLoyalty contactsLoyalty = findMembershpByCard(loyaltyCard.getCardNumber(),
								loyaltyCard.getProgramId(), user.getUserId());
						membershipResponse= setMembershipResponse(contactsLoyalty);
						if(status!=null) {
							enrollResponse = prepareEnrollmentResponse(responseHeader, membershipResponse, null, status);
							return enrollResponse;
						}
						
						cardSetObj = loyaltyCardSetDao.findByCardSetId(loyaltyCard.getCardSetId());
						status = validateCardSet(cardSetObj,loyaltyCard,enrollRequest);
						if(status!=null) {
							enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
							return enrollResponse;
						}
						
						loyaltyProgram = loyaltyProgramDao.findById(loyaltyCard.getProgramId());
					}
					
					status = validateProgram(loyaltyProgram,loyaltyCard);
					if(status!=null) {
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						return enrollResponse;
					}
					
				} 

				LoyaltyProgramTier linkedTierObj = null;
				LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance()
						.getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
				if (loyaltyCard.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_CARD_STATUS_ACTIVATED)) { // convert
																											// from G
																											// type card
																											// to GL
																											// type card

					ContactsLoyalty giftMembership = findMembershpByCard(loyaltyCard.getCardNumber(),
							loyaltyCard.getProgramId(), user.getUserId());
					if (giftMembership.getMembershipStatus()
							.equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED)) {
						String message = PropertyUtil.getErrorMessage(111517, OCConstants.ERROR_LOYALTY_FLAG) + " "
								+ giftMembership.getCardNumber() + ".";
						status = new Status("111517", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						return enrollResponse;
					} else if (giftMembership.getMembershipStatus()
							.equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED)) {
						String message = PropertyUtil.getErrorMessage(111539, OCConstants.ERROR_LOYALTY_FLAG) + " "
								+ giftMembership.getCardNumber() + ".";
						status = new Status("111539", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						return enrollResponse;
					} else if (giftMembership.getMembershipStatus()
							.equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)) {
						ContactsLoyalty destLoyalty = getDestMembershipIfAny(giftMembership);
						String maskedNum = Constants.STRING_NILL;
						if (destLoyalty != null) {
							maskedNum = Utility.maskNumber(destLoyalty.getCardNumber() + Constants.STRING_NILL);
						}
						String message = PropertyUtil.getErrorMessage(111578, OCConstants.ERROR_LOYALTY_FLAG)
								+ maskedNum + ".";
						status = new Status("111578", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						return enrollResponse;
					}

					String failedRequisites = validateProgramRequisites(enrollRequest, loyaltyProgram);
					logger.info("Mandatory failed fields:" + failedRequisites);
					if (failedRequisites != null) {
						status = new Status("111509",
								PropertyUtil.getErrorMessage(111509, OCConstants.ERROR_LOYALTY_FLAG) + " "
										+ failedRequisites + ".",
								OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						return enrollResponse;
					}

					if (loyaltyProgram.getUniqueMobileFlag() == 'Y' && 
							enrollRequest.getCustomer().getPhone() != null && 
							!enrollRequest.getCustomer().getPhone().isEmpty()) {

						contactLoyalty = findMembershpByPhone(enrollRequest.getCustomer().getPhone(),
								loyaltyProgram.getProgramId(), user.getUserId(), user.getCountryCarrier(),
								user.getUserOrganization().getMaxNumberOfDigits());
						if (contactLoyalty != null) {
							status = new Status("0",
									PropertyUtil.getErrorMessage(111506, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactLoyalty.getCardNumber()+", "
											+	"Email Address:"+ (contactLoyalty.getEmailId()!=null?contactLoyalty.getEmailId():"")+", "
											+	"Mobile #:"+(contactLoyalty.getMobilePhone()!=null?contactLoyalty.getMobilePhone():"")+", "
											+	"Name:"+  (contactLoyalty.getContact().getFirstName() !=null?contactLoyalty.getContact().getFirstName():"") +" "+
												 (contactLoyalty.getContact().getLastName()!=null?contactLoyalty.getContact().getLastName():"")+".",
									OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
							membershipResponse = setMembershipResponse(contactLoyalty);
							Customer customer = findCustomerByCid(contactLoyalty.getContact(), user.getUserId());
							List<MatchedCustomer> customers = prepareMatchedCustomers(customer,
									"" + contactLoyalty.getCardNumber());// Need to discuss.
							enrollResponse = prepareEnrollmentResponse(responseHeader, membershipResponse, customers, status);
							return enrollResponse;
						}
					}
					if (loyaltyProgram.getUniqueEmailFlag() != '\0' && loyaltyProgram.getUniqueEmailFlag() == 'Y' &&
							enrollRequest.getCustomer().getEmailAddress() != null && !enrollRequest.getCustomer().getEmailAddress().isEmpty()) {
						contactLoyalty = findMembershpByEmailId(enrollRequest.getCustomer().getEmailAddress(),
								loyaltyProgram.getProgramId(), user.getUserId(), 
								user.getUserOrganization().getMaxNumberOfDigits());
						if (contactLoyalty != null) {
							status = new Status("0",
									PropertyUtil.getErrorMessage(111506, OCConstants.ERROR_LOYALTY_FLAG) + " "+contactLoyalty.getCardNumber()+", "
											+	"Email Address:"+ (contactLoyalty.getEmailId()!=null?contactLoyalty.getEmailId():"")+", "
											+	"Mobile #:"+(contactLoyalty.getMobilePhone()!=null?contactLoyalty.getMobilePhone():"")+", "
											+	"Name:"+  (contactLoyalty.getContact().getFirstName() !=null?contactLoyalty.getContact().getFirstName():"") +" "+
												 (contactLoyalty.getContact().getLastName()!=null?contactLoyalty.getContact().getLastName():"")+".",
									OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
							Customer customer = findCustomerByCid(contactLoyalty.getContact(), user.getUserId());
							List<MatchedCustomer> customers = prepareMatchedCustomers(customer,
									"" + contactLoyalty.getCardNumber());
							membershipResponse = setMembershipResponse(contactLoyalty);
							enrollResponse = prepareEnrollmentResponse(responseHeader, membershipResponse, customers, status);
							if (isDCard) {// TODO
								deleteDCard(loyaltyCard);
							} else {

								updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
							}
							return enrollResponse;
						}
					}
					LoyaltyProgramExclusion loyaltyExclusion = getLoyaltyExclusion(loyaltyProgram.getProgramId());
					if (loyaltyExclusion != null) {
						status = validateStoreNumberExclusion(enrollRequest, loyaltyProgram, loyaltyExclusion);
						if (status != null) {
							enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
							return enrollResponse;
						}
					}

					if (cardSetObj.getLinkedTierLevel() > 0) {
						String type = "Tier " + cardSetObj.getLinkedTierLevel();
						linkedTierObj = loyaltyProgramTierDao.getTierByPrgmAndType(loyaltyProgram.getProgramId(), type);
						if (linkedTierObj == null) {
						
							if (isDCard) {// TODO
								deleteDCard(loyaltyCard);
							} else {

								updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
							}
							status = new Status("111555",
									PropertyUtil.getErrorMessage(111555, OCConstants.ERROR_LOYALTY_FLAG),
									OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
							return enrollResponse;
						}
					}

					logger.info("Loyalty card is already activated ");

					//---------------------------------------------------//
					/*
					 * These validation were before done inside createMembership block  APP-1326*/
					
					List<POSMapping> contactPOSMap = null;
					POSMappingDao posMappingDao = (POSMappingDao) ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
					contactPOSMap = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", user.getUserId());
					
					Contacts jsonContact = new Contacts();
					jsonContact.setUsers(user);				
					if(contactPOSMap != null){
						jsonContact = setContactFields(jsonContact, contactPOSMap, enrollRequest,OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD);//#Card flow
					}
					logger.info("----enrollRequest.getCustomer().getPhone()=="+enrollRequest.getCustomer().getPhone()+"----jsonContact.getMobilePhone()=="+jsonContact.getMobilePhone());
									
					if(jsonContact.getMobilePhone() != null && !jsonContact.getMobilePhone().isEmpty())
						enrollRequest.getCustomer().setPhone(jsonContact.getMobilePhone());

					Map<String, Object> contactAndDataFlags = validateAndSavedbContact(jsonContact, mlList, user,enrollRequest);
					Contacts dbContact = (Contacts) contactAndDataFlags.get("dbContact");
					boolean isExists = (Boolean) contactAndDataFlags.get("isExists");
					/*if(jsonContact.getExternalId() != null){
						isExists = true;
						
					}*/
					if(isExists){
						ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
						contactLoyalty = contactsLoyaltyDao.findByContactIdStrAndPrgmId(user.getUserId(), dbContact.getContactId()+"",loyaltyProgram.getProgramId());
						if(contactLoyalty != null) {
							
								if(isDCard){
									deleteDCard(loyaltyCard);
								}else{
									
									updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
								}
							
								String msg = PropertyUtil.getErrorMessage(111506, OCConstants.ERROR_LOYALTY_FLAG) + " "+contactLoyalty.getCardNumber()+", "
										+	"Email Address:"+ (contactLoyalty.getEmailId()!=null?contactLoyalty.getEmailId():"")+", "
										+	"Mobile #:"+(contactLoyalty.getMobilePhone()!=null?contactLoyalty.getMobilePhone():"")+", "
										+	"Name:"+  (contactLoyalty.getContact().getFirstName() !=null?contactLoyalty.getContact().getFirstName():"") +" "+
											 (contactLoyalty.getContact().getLastName()!=null?contactLoyalty.getContact().getLastName():"")+".";
								status = new Status("0", msg, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
								membershipResponse = setMembershipResponse(contactLoyalty);
								
								
							//status = new Status("111563", PropertyUtil.getErrorMessage(111563, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactLoyalty.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);//Could be done before
							
							return prepareEnrollmentResponse(responseHeader, membershipResponse, null, status);
						}
					}
					/*String encPwd = null;
					if(enrollRequest.getHeader().getSourceType() != null && 
							!enrollRequest.getHeader().getSourceType().isEmpty() && 
							(enrollRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) || 
									enrollRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP))){
						encPwd = generateMembrshpPwd();
					}*/
					String encPwd = null;
					String sourceType = enrollRequest.getHeader().getSourceType();
					String password = enrollRequest.getCustomer().getPassword();
					if(/*sourceType != null && !sourceType.isEmpty() && 
							sourceType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) 
							&&*/ password != null && !password.trim().isEmpty()){
						/*if(password == null || password.trim().isEmpty()){
							status = new Status("800033",
									PropertyUtil.getErrorMessage(800033, OCConstants.ERROR_MOBILEAPP_FLAG),
									OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
							return enrollResponse;
						}*/
						try {
							logger.debug("password.trim() === "+password.trim());
							encPwd = EncryptDecryptLtyMembshpPwd.decryptProgramPwd(password.trim());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("incorrect password===", e);
						}
						logger.debug("encPwd === "+encPwd);
						if(encPwd != null)encPwd = EncryptDecryptLtyMembshpPwd.encrypt(encPwd);
					}
					logger.debug("encPwd === "+encPwd);
					AsyncExecuterEnrollment executerThread = new AsyncExecuterEnrollment(enrollRequest, responseHeader,
							loyaltyProgram, loyaltyCard, mode, user, mlList, loyaltyProgram.getMembershipType(),
							linkedTierObj, true, encPwd);
					
					//Changes
					if(mode.equals(OCConstants.LOYALTY_OFFLINE_MODE)){
						executerThread.performEnrollment();
					}else{
						
						executerThread.start();
					}
					
					

					membershipResponse = new MembershipResponse();
					membershipResponse.setExpiry("");
					membershipResponse.setPhoneNumber("");
					membershipResponse.setTierLevel("");
					membershipResponse.setTierName("");
					membershipResponse.setCardNumber(loyaltyCard!=null ? loyaltyCard.getCardNumber(): "");
					//if(encPwd != null) membershipResponse.setPassword(encPwd);
					membershipResponse.setCardPin(loyaltyCard!=null ? loyaltyCard.getCardPin(): "");
					membershipResponse.setFingerprintValidation(enrollRequest.getMembership().getFingerprintValidation());
					
					status = new Status("0", "Enrollment was successful", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, membershipResponse, null, status);// customers and
																									// accountResponse
																									// had
																									// to be
																									// removed.APP-1326
					return enrollResponse;

				}
				
				
				if (loyaltyCard.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_CARD_STATUS_ENROLLED)) {
					try {
						contactLoyalty = findMembershpByCard(loyaltyCard.getCardNumber(),loyaltyCard.getProgramId(),loyaltyCard.getUserId());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.info("e===>"+e);
					}
					String msg = PropertyUtil.getErrorMessage(111506, OCConstants.ERROR_LOYALTY_FLAG) + " "+contactLoyalty.getCardNumber()+", "
							+	"Email Address:"+ (contactLoyalty.getEmailId()!=null?contactLoyalty.getEmailId():"")+", "
							+	"Mobile #:"+(contactLoyalty.getMobilePhone()!=null?contactLoyalty.getMobilePhone():"")+", "
							+	"Name:"+  (contactLoyalty.getContact().getFirstName() !=null?contactLoyalty.getContact().getFirstName():"") +" "+
								 (contactLoyalty.getContact().getLastName()!=null?contactLoyalty.getContact().getLastName():"")+".";
					status = new Status("0", msg, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
					Customer customer = findCustomer(loyaltyCard.getCardNumber(), loyaltyCard.getProgramId(),
							user.getUserId());
					List<MatchedCustomer> customers = prepareMatchedCustomers(customer, loyaltyCard.getCardNumber());
					membershipResponse = setMembershipResponse(contactLoyalty);
					enrollResponse = prepareEnrollmentResponse(responseHeader, membershipResponse, customers, status);
					logger.info("Loyalty card is already enrolled ");
					return enrollResponse;
				}

				String failedRequisites = validateProgramRequisites(enrollRequest, loyaltyProgram);
				logger.info("Mandatory failed fields:" + failedRequisites);
				if (failedRequisites != null) {
					status = new Status("111509", PropertyUtil.getErrorMessage(111509, OCConstants.ERROR_LOYALTY_FLAG)
							+ " " + failedRequisites + ".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					if (isDCard) {// TODO
						deleteDCard(loyaltyCard);
					} else {

						updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
					}
					return enrollResponse;
				}

				if (loyaltyProgram.getUniqueMobileFlag() == 'Y' &&  
						enrollRequest.getCustomer().getPhone() != null && 
						!enrollRequest.getCustomer().getPhone().isEmpty()) {
					contactLoyalty = findMembershpByPhone(enrollRequest.getCustomer().getPhone(),
							loyaltyProgram.getProgramId(), user.getUserId(), user.getCountryCarrier(),
							user.getUserOrganization().getMaxNumberOfDigits());
					if (contactLoyalty != null) {
						status = new Status("0",
								PropertyUtil.getErrorMessage(111506, OCConstants.ERROR_LOYALTY_FLAG) + " "+contactLoyalty.getCardNumber()+", "
										+	"Email Address:"+ (contactLoyalty.getEmailId()!=null?contactLoyalty.getEmailId():"")+", "
										+	"Mobile #:"+(contactLoyalty.getMobilePhone()!=null?contactLoyalty.getMobilePhone():"")+", "
										+	"Name:"+  (contactLoyalty.getContact().getFirstName() !=null?contactLoyalty.getContact().getFirstName():"") +" "+
											 (contactLoyalty.getContact().getLastName()!=null?contactLoyalty.getContact().getLastName():"")+".",
								OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
						Customer customer = findCustomerByCid(contactLoyalty.getContact(), user.getUserId());
						List<MatchedCustomer> customers = prepareMatchedCustomers(customer,
								"" + contactLoyalty.getCardNumber());
					    membershipResponse = setMembershipResponse(contactLoyalty);
						enrollResponse = prepareEnrollmentResponse(responseHeader, membershipResponse, customers, status);
						if (isDCard) {// TODO
							deleteDCard(loyaltyCard);
						} else {

							updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
						}
						return enrollResponse;
					}
				}
				if (loyaltyProgram.getUniqueEmailFlag() != '\0' && loyaltyProgram.getUniqueEmailFlag() == 'Y' &&
						enrollRequest.getCustomer().getEmailAddress() != null && !enrollRequest.getCustomer().getEmailAddress().isEmpty()) {
					contactLoyalty = findMembershpByEmailId(enrollRequest.getCustomer().getEmailAddress(),
							loyaltyProgram.getProgramId(), user.getUserId(), 
							user.getUserOrganization().getMaxNumberOfDigits());
					if (contactLoyalty != null) {
						status = new Status("0",
								PropertyUtil.getErrorMessage(111506, OCConstants.ERROR_LOYALTY_FLAG) + " "+contactLoyalty.getCardNumber()+", "
										+	"Email Address:"+ (contactLoyalty.getEmailId()!=null?contactLoyalty.getEmailId():"")+", "
										+	"Mobile #:"+(contactLoyalty.getMobilePhone()!=null?contactLoyalty.getMobilePhone():"")+", "
										+	"Name:"+  (contactLoyalty.getContact().getFirstName() !=null?contactLoyalty.getContact().getFirstName():"") +" "+
											 (contactLoyalty.getContact().getLastName()!=null?contactLoyalty.getContact().getLastName():"")+".",
								OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
						Customer customer = findCustomerByCid(contactLoyalty.getContact(), user.getUserId());
						List<MatchedCustomer> customers = prepareMatchedCustomers(customer,
								"" + contactLoyalty.getCardNumber());
						membershipResponse = setMembershipResponse(contactLoyalty);
						enrollResponse = prepareEnrollmentResponse(responseHeader, membershipResponse, customers, status);
						if (isDCard) {// TODO
							deleteDCard(loyaltyCard);
						} else {

							updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
						}
						return enrollResponse;
					}
				}

				
				
				
				

				LoyaltyProgramExclusion loyaltyExclusion = getLoyaltyExclusion(loyaltyProgram.getProgramId());
				if (loyaltyExclusion != null) {
					status = validateStoreNumberExclusion(enrollRequest, loyaltyProgram, loyaltyExclusion);
					if (status != null) {
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						if (isDCard) {// TODO
							deleteDCard(loyaltyCard);
						} else {

							updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
						}
						return enrollResponse;
					}
				}

				if (cardSetObj.getLinkedTierLevel() > 0) {
					String type = "Tier " + cardSetObj.getLinkedTierLevel();
					linkedTierObj = loyaltyProgramTierDao.getTierByPrgmAndType(loyaltyProgram.getProgramId(), type);
					if (linkedTierObj == null) {
						// active incomplete status response...
						if (isDCard) {// TODO
							deleteDCard(loyaltyCard);
						} else {

							updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
						}
						status = new Status("111555",
								PropertyUtil.getErrorMessage(111555, OCConstants.ERROR_LOYALTY_FLAG),
								OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						return enrollResponse;
					}
				}
				//---------------------------------------------------//
				/*
				 * These validation were before done inside createMembership block  APP-1326*/
				
				List<POSMapping> contactPOSMap = null;
				POSMappingDao posMappingDao = (POSMappingDao) ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
				contactPOSMap = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", user.getUserId());
				
				Contacts jsonContact = new Contacts();
				jsonContact.setUsers(user);				
				if(contactPOSMap != null){
					jsonContact = setContactFields(jsonContact, contactPOSMap, enrollRequest,OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD);//#Card flow
				}
				logger.info("----enrollRequest.getCustomer().getPhone()=="+enrollRequest.getCustomer().getPhone()+"----jsonContact.getMobilePhone()=="+jsonContact.getMobilePhone());
								
				if(jsonContact.getMobilePhone() != null && !jsonContact.getMobilePhone().isEmpty())
					enrollRequest.getCustomer().setPhone(jsonContact.getMobilePhone());

				Map<String, Object> contactAndDataFlags = validateAndSavedbContact(jsonContact, mlList, user,enrollRequest);
				Contacts dbContact = (Contacts) contactAndDataFlags.get("dbContact");
				boolean isExists = (Boolean) contactAndDataFlags.get("isExists");
			//	if(jsonContact.getExternalId() != null){
			//		isExists = true;
					/*if(dbContact.getExternalId() == null || dbContact.getExternalId().trim().isEmpty())
						dbContact.setExternalId(jsonContact.getExternalId());*/
				//}
				if(isExists){
					ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
					contactLoyalty = contactsLoyaltyDao.findByContactIdStrAndPrgmId(user.getUserId(), dbContact.getContactId()+"",loyaltyProgram.getProgramId());
					if(contactLoyalty != null) {
						//if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(memberShipType.trim())){#Card Flow no need to validate
							if(isDCard){
								deleteDCard(loyaltyCard);
							}else{
								
								updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
							}
						//}
						
					//	Customer customer = prepareCustomer(dbContact);Removed as per requirement.
					//	List<MatchedCustomer> customers = prepareMatchedCustomers(customer, ""+contactLoyalty.getCardNumber());
						
						//status = new Status("111563", PropertyUtil.getErrorMessage(111563, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactLoyalty.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);//Could be done before
						String msg = PropertyUtil.getErrorMessage(111506, OCConstants.ERROR_LOYALTY_FLAG) + " "+contactLoyalty.getCardNumber()+", "
								+	"Email Address:"+ (contactLoyalty.getEmailId()!=null?contactLoyalty.getEmailId():"")+", "
								+	"Mobile #:"+(contactLoyalty.getMobilePhone()!=null?contactLoyalty.getMobilePhone():"")+", "
								+	"Name:"+  (contactLoyalty.getContact().getFirstName() !=null?contactLoyalty.getContact().getFirstName():"") +" "+
									 (contactLoyalty.getContact().getLastName()!=null?contactLoyalty.getContact().getLastName():"")+".";
						status = new Status("0", msg, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
						membershipResponse = setMembershipResponse(contactLoyalty);
						
						return prepareEnrollmentResponse(responseHeader, membershipResponse, null, status);
					}
				}
				/*String encPwd = null;
				if(enrollRequest.getHeader().getSourceType() != null && 
						!enrollRequest.getHeader().getSourceType().isEmpty() && 
						(enrollRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) || 
								enrollRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP))){
					encPwd = generateMembrshpPwd();
				}*/	
				String encPwd = null;
				String sourceType = enrollRequest.getHeader().getSourceType();
				String password = enrollRequest.getCustomer().getPassword();
				if(/*sourceType != null && !sourceType.isEmpty() && 
						sourceType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) 
						&&*/ password != null && !password.trim().isEmpty()){
					/*if(password == null || password.trim().isEmpty()){
						status = new Status("800033",
								PropertyUtil.getErrorMessage(800033, OCConstants.ERROR_MOBILEAPP_FLAG),
								OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						return enrollResponse;
					}*/
					try {
						logger.debug("password.trim() === "+password.trim());
						encPwd = EncryptDecryptLtyMembshpPwd.decryptProgramPwd(password.trim());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("incorrect password===", e);
					}
					logger.debug("encPwd === "+encPwd);
					if(encPwd != null)encPwd = EncryptDecryptLtyMembshpPwd.encrypt(encPwd);
				}
				logger.debug("encPwd === "+encPwd);
				AsyncExecuterEnrollment executerThread = new AsyncExecuterEnrollment(enrollRequest, responseHeader,
						loyaltyProgram, loyaltyCard, mode, user, mlList, OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD, linkedTierObj,
						false, encPwd);
				
				if(mode.equals(OCConstants.LOYALTY_OFFLINE_MODE)){
					executerThread.performEnrollment();
				}else{
					
					executerThread.start();
				}
				
				
				
				membershipResponse = new MembershipResponse();
				membershipResponse.setExpiry("");
				membershipResponse.setPhoneNumber("");
				membershipResponse.setTierLevel("");
				membershipResponse.setTierName("");
				membershipResponse.setCardNumber(loyaltyCard!=null ? loyaltyCard.getCardNumber(): "");
				//if(encPwd != null) membershipResponse.setPassword(encPwd);
				membershipResponse.setCardPin(loyaltyCard!=null ? loyaltyCard.getCardPin(): "");
				membershipResponse.setFingerprintValidation(enrollRequest.getMembership().getFingerprintValidation());
				status = new Status("0", "Enrollment was successful", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				enrollResponse = prepareEnrollmentResponse(responseHeader, membershipResponse, null, status);// customers and
																								// accountResponse had
																								// to be
																								// removed.APP-1326
				return enrollResponse;
						
			} else if (enrollRequest.getMembership().getIssueCardFlag() != null
					&& enrollRequest.getMembership().getIssueCardFlag().length() > 0
					&& enrollRequest.getMembership().getIssueCardFlag().equalsIgnoreCase("Y")) {

				logger.info("Enrolling with auto issue card ..." + enrollRequest.getMembership().getIssueCardFlag());

				loyaltyProgram = findDefaultProgram(user.getUserId());
				if (loyaltyProgram == null
						|| !loyaltyProgram.getStatus().equals(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)) {
					status = new Status("111610", PropertyUtil.getErrorMessage(111610, OCConstants.ERROR_LOYALTY_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					return enrollResponse;
				}

				List<LoyaltyCardSet> activeCardSets = findLoyaltyActiveCardSet(loyaltyProgram.getProgramId());
				if (activeCardSets == null) {
					status = new Status("100102", PropertyUtil.getErrorMessage(100102, OCConstants.ERROR_LOYALTY_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					return enrollResponse;
				}

				String cardSetIdStr = null;
				for (LoyaltyCardSet cardSet : activeCardSets) {
					logger.info("cardSetIdStr = " + cardSetIdStr);
					if (cardSetIdStr == null) {
						cardSetIdStr = "" + cardSet.getCardSetId();
					} else {
						cardSetIdStr += "," + cardSet.getCardSetId();
					}
				}
				LoyaltyCardFinder loyaltyCardFinder = (LoyaltyCardFinder) ServiceLocator.getInstance()
						.getBeanByName(OCConstants.LOYALTY_CARD_FINDER);

				
/*					boolean doRecursiveCall = true;
					logger.info("I am not allowed to get a card");
					do{
						doRecursiveCall = !loyaltyCardFinder.letMeIn(user.getUserId());//Bean should be here not in loyalty card finder.
					}while(doRecursiveCall);
					loyaltyCard = LoyaltyCardFinder.findInventoryCard(""+loyaltyProgram.getProgramId(), cardSetIdStr, null, user.getUserId());
*/				

				if(!loyaltyCardFinder.letMeIn(user.getUserId())){
					boolean doRecursiveCall = true;
					logger.info("I am not allowed to get a card");
					do{
						doRecursiveCall = !loyaltyCardFinder.letMeIn(user.getUserId());
					}while(doRecursiveCall);
					
					loyaltyCard=loyaltyCardFinder.findInventoryCard(""+loyaltyProgram.getProgramId(), cardSetIdStr, null, user.getUserId());	
					loyaltyCardFinder.MarkIamDone(user.getUserId());	
					
				}else {
					loyaltyCard=loyaltyCardFinder.findInventoryCard(""+loyaltyProgram.getProgramId(), cardSetIdStr, null, user.getUserId());
					logger.info("I am done with my job"+Thread.currentThread().getName());
					loyaltyCardFinder.MarkIamDone(user.getUserId());
				}
				
				
				
				if (loyaltyCard == null) {
					status = new Status("100102", PropertyUtil.getErrorMessage(100102, OCConstants.ERROR_LOYALTY_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					return enrollResponse;
				}
				logger.info("card = " + loyaltyCard.getCardNumber() + " status = " + loyaltyCard.getStatus());

				String failedRequisites = validateProgramRequisites(enrollRequest, loyaltyProgram);
				if (failedRequisites != null) {
					status = new Status("111509", PropertyUtil.getErrorMessage(111509, OCConstants.ERROR_LOYALTY_FLAG)
							+ " " + failedRequisites + ".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
					return enrollResponse;
				}
				logger.info("failedRequisites == " + failedRequisites);
				if (loyaltyProgram.getUniqueMobileFlag() == 'Y' && 
						enrollRequest.getCustomer().getPhone() != null && !enrollRequest.getCustomer().getPhone().isEmpty()
						) {
					contactLoyalty = findMembershpByPhone(enrollRequest.getCustomer().getPhone(),
							loyaltyProgram.getProgramId(), user.getUserId(), user.getCountryCarrier(),
							user.getUserOrganization().getMaxNumberOfDigits());
					if (contactLoyalty != null) {
						/*status = new Status("111506",
								PropertyUtil.getErrorMessage(111506, OCConstants.ERROR_LOYALTY_FLAG) + " "+contactLoyalty.getCardNumber()+", "
										+	"Email Address:"+ (contactLoyalty.getEmailId()!=null?contactLoyalty.getEmailId():"")+", "
										+	"Mobile #:"+(contactLoyalty.getMobilePhone()!=null?contactLoyalty.getMobilePhone():"")+", "
										+	"Name:"+  (contactLoyalty.getContact().getFirstName() !=null?contactLoyalty.getContact().getFirstName():"") +" "+
											 (contactLoyalty.getContact().getLastName()!=null?contactLoyalty.getContact().getLastName():"")+".",
								OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);*/
						status = new Status("0",
								PropertyUtil.getErrorMessage(111506, OCConstants.ERROR_LOYALTY_FLAG) + " "+contactLoyalty.getCardNumber()+", "
										+	"Email Address:"+ (contactLoyalty.getEmailId()!=null?contactLoyalty.getEmailId():"")+", "
										+	"Mobile #:"+(contactLoyalty.getMobilePhone()!=null?contactLoyalty.getMobilePhone():"")+", "
										+	"Name:"+  (contactLoyalty.getContact().getFirstName() !=null?contactLoyalty.getContact().getFirstName():"") +" "+
											 (contactLoyalty.getContact().getLastName()!=null?contactLoyalty.getContact().getLastName():"")+".",
								OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
						Customer customer = findCustomerByCid(contactLoyalty.getContact(), user.getUserId());
						List<MatchedCustomer> customers = prepareMatchedCustomers(customer,
								"" + contactLoyalty.getCardNumber());
						membershipResponse = setMembershipResponse(contactLoyalty);
						enrollResponse = prepareEnrollmentResponse(responseHeader, membershipResponse, customers, status);
						updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
						return enrollResponse;
					}
				}
				if (loyaltyProgram.getUniqueEmailFlag() != '\0' && loyaltyProgram.getUniqueEmailFlag() == 'Y' &&
						enrollRequest.getCustomer().getEmailAddress() != null && !enrollRequest.getCustomer().getEmailAddress().isEmpty()) {
					contactLoyalty = findMembershpByEmailId(enrollRequest.getCustomer().getEmailAddress(),
							loyaltyProgram.getProgramId(), user.getUserId(),
							user.getUserOrganization().getMaxNumberOfDigits());
					if (contactLoyalty != null) {
						/*status = new Status("111506",
								PropertyUtil.getErrorMessage(111506, OCConstants.ERROR_LOYALTY_FLAG)+ " "+contactLoyalty.getCardNumber()+", "
										+	"Email Address:"+ (contactLoyalty.getEmailId()!=null?contactLoyalty.getEmailId():"")+", "
										+	"Mobile #:"+(contactLoyalty.getMobilePhone()!=null?contactLoyalty.getMobilePhone():"")+", "
										+	"Name:"+  (contactLoyalty.getContact().getFirstName() !=null?contactLoyalty.getContact().getFirstName():"") +" "+
											 (contactLoyalty.getContact().getLastName()!=null?contactLoyalty.getContact().getLastName():"")+".",
								OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);*/
						status = new Status("0",
								PropertyUtil.getErrorMessage(111506, OCConstants.ERROR_LOYALTY_FLAG) + " "+contactLoyalty.getCardNumber()+", "
										+	"Email Address:"+ (contactLoyalty.getEmailId()!=null?contactLoyalty.getEmailId():"")+", "
										+	"Mobile #:"+(contactLoyalty.getMobilePhone()!=null?contactLoyalty.getMobilePhone():"")+", "
										+	"Name:"+  (contactLoyalty.getContact().getFirstName() !=null?contactLoyalty.getContact().getFirstName():"") +" "+
											 (contactLoyalty.getContact().getLastName()!=null?contactLoyalty.getContact().getLastName():"")+".",
								OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
						Customer customer = findCustomerByCid(contactLoyalty.getContact(), user.getUserId());
						List<MatchedCustomer> customers = prepareMatchedCustomers(customer,
								"" + contactLoyalty.getCardNumber());
						membershipResponse = setMembershipResponse(contactLoyalty);
						enrollResponse = prepareEnrollmentResponse(responseHeader, membershipResponse, customers, status);
						if (isDCard) {// TODO
							deleteDCard(loyaltyCard);
						} else {

							updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
						}
						return enrollResponse;
					}
				}

				LoyaltyProgramExclusion loyaltyExclusion = getLoyaltyExclusion(loyaltyProgram.getProgramId());
				if (loyaltyExclusion != null) {
					status = validateStoreNumberExclusion(enrollRequest, loyaltyProgram, loyaltyExclusion);
					if (status != null) {
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
						return enrollResponse;
					}
				}

				//---------------------------------------------------//
				/*
				 * These validation were before done inside createMembership block  APP-1326*/
				
				List<POSMapping> contactPOSMap = null;
				POSMappingDao posMappingDao = (POSMappingDao) ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
				contactPOSMap = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", user.getUserId());
				
				Contacts jsonContact = new Contacts();
				jsonContact.setUsers(user);
				String subsidiary = enrollRequest.getHeader().getSubsidiaryNumber() != null && !enrollRequest.getHeader().getSubsidiaryNumber().trim().isEmpty() ? enrollRequest.getHeader().getSubsidiaryNumber().trim() : null; 
				String store = enrollRequest.getHeader().getStoreNumber() != null ? enrollRequest.getHeader().getStoreNumber().trim() : enrollRequest.getHeader().getStoreNumber();
				
				if(contactPOSMap != null){
					jsonContact = setContactFields(jsonContact, contactPOSMap, enrollRequest,OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD);//#Card flow
				}
				logger.info("----enrollRequest.getCustomer().getPhone()=="+enrollRequest.getCustomer().getPhone()+"----jsonContact.getMobilePhone()=="+jsonContact.getMobilePhone());
								
				if(jsonContact.getMobilePhone() != null && !jsonContact.getMobilePhone().isEmpty())
					enrollRequest.getCustomer().setPhone(jsonContact.getMobilePhone());

				Map<String, Object> contactAndDataFlags = validateAndSavedbContact(jsonContact, mlList, user,enrollRequest);
				Contacts dbContact = (Contacts) contactAndDataFlags.get("dbContact");
				boolean isExists = (Boolean) contactAndDataFlags.get("isExists");
				//if(jsonContact.getExternalId() != null){
					//isExists = true;
					/*if(dbContact.getExternalId() == null || dbContact.getExternalId().trim().isEmpty())
						dbContact.setExternalId(jsonContact.getExternalId());*/
				//}
				if(isExists){
					ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
					contactLoyalty = contactsLoyaltyDao.findByContactIdStrAndPrgmId(user.getUserId(), dbContact.getContactId()+"",loyaltyProgram.getProgramId());
					if(contactLoyalty != null) {
						//if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(memberShipType.trim())){#Card Flow no need to validate
							if(isDCard){
								deleteDCard(loyaltyCard);
							}else{
								
								updateLoyaltyCardStatus(loyaltyCard, OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
							}
						//}
						
					//	Customer customer = prepareCustomer(dbContact);Removed as per requirement.
					//	List<MatchedCustomer> customers = prepareMatchedCustomers(customer, ""+contactLoyalty.getCardNumber());
						
						//status = new Status("111563", PropertyUtil.getErrorMessage(111563, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactLoyalty.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);//Could be done before
							String msg = PropertyUtil.getErrorMessage(111506, OCConstants.ERROR_LOYALTY_FLAG) + " "+contactLoyalty.getCardNumber()+", "
									+	"Email Address:"+ (contactLoyalty.getEmailId()!=null?contactLoyalty.getEmailId():"")+", "
									+	"Mobile #:"+(contactLoyalty.getMobilePhone()!=null?contactLoyalty.getMobilePhone():"")+", "
									+	"Name:"+  (contactLoyalty.getContact().getFirstName() !=null?contactLoyalty.getContact().getFirstName():"") +" "+
										 (contactLoyalty.getContact().getLastName()!=null?contactLoyalty.getContact().getLastName():"")+".";
							status = new Status("0", msg, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
							membershipResponse = setMembershipResponse(contactLoyalty);
							
							
						return prepareEnrollmentResponse(responseHeader, membershipResponse, null, status);
					}
				}
				/*String encPwd = null;
				if(enrollRequest.getHeader().getSourceType() != null && 
						!enrollRequest.getHeader().getSourceType().isEmpty() && 
						(enrollRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) || 
								enrollRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP))){
					encPwd = generateMembrshpPwd();
				}	*/
				String encPwd = null;
				String sourceType = enrollRequest.getHeader().getSourceType();
				String password = enrollRequest.getCustomer().getPassword();
				if(/*sourceType != null && !sourceType.isEmpty() && 
						sourceType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) 
						&&*/ password != null && !password.trim().isEmpty()){
					/*if(password == null || password.trim().isEmpty()){
						status = new Status("800033",
								PropertyUtil.getErrorMessage(800033, OCConstants.ERROR_MOBILEAPP_FLAG),
								OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						return enrollResponse;
					}*/
					try {
						logger.debug("password.trim() === "+password.trim());
						encPwd = EncryptDecryptLtyMembshpPwd.decryptProgramPwd(password.trim());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("incorrect password===", e);
					}
					logger.debug("encPwd === "+encPwd);
					if(encPwd != null)encPwd = EncryptDecryptLtyMembshpPwd.encrypt(encPwd);
				}
				logger.debug("encPwd === "+encPwd);
				AsyncExecuterEnrollment executerThread = new AsyncExecuterEnrollment(enrollRequest, responseHeader,
						loyaltyProgram, loyaltyCard, mode, user, mlList, OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD, null,
						false, encPwd);
				
				if(mode.equals(OCConstants.LOYALTY_OFFLINE_MODE)){
					executerThread.performEnrollment();
				}else{
					
					executerThread.start();
				}
								
				membershipResponse = new MembershipResponse();
				membershipResponse.setExpiry("");
				membershipResponse.setPhoneNumber("");
				membershipResponse.setTierLevel("");
				membershipResponse.setTierName("");
				membershipResponse.setCardNumber(loyaltyCard!=null ? loyaltyCard.getCardNumber(): "");
				//if(encPwd != null) membershipResponse.setPassword(encPwd);
				membershipResponse.setCardPin(loyaltyCard!=null ? loyaltyCard.getCardPin(): "");
				membershipResponse.setFingerprintValidation(enrollRequest.getMembership().getFingerprintValidation());
				status = new Status("0", "Enrollment was successful", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				enrollResponse = prepareEnrollmentResponse(responseHeader, membershipResponse, null, status);// customers and
																								// accountResponse had
																								// to be
																								// removed.APP-1326
				return enrollResponse;

			} else if (enrollRequest.getMembership().getPhoneNumber() != null
					&& enrollRequest.getMembership().getPhoneNumber().trim().length() > 0) {

				logger.info(">>>Enrollment through mobile phone number : "
						+ enrollRequest.getMembership().getPhoneNumber());
				String validStatus = LoyaltyProgramHelper
						.validateMembershipMobile(enrollRequest.getMembership().getPhoneNumber().trim());
				if (OCConstants.LOYALTY_MEMBERSHIP_MOBILE_INVALID.equals(validStatus)) {
					status = new Status("111554",
							PropertyUtil.getErrorMessage(111554, OCConstants.ERROR_LOYALTY_FLAG) + " "
									+ enrollRequest.getMembership().getPhoneNumber().trim() + ".",
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					return enrollResponse;
				}
				String phoneParse = Utility.phoneParse(enrollRequest.getMembership().getPhoneNumber().trim(),
						user != null ? user.getUserOrganization() : null);
				if (phoneParse == null) {
					status = new Status("111554",
							PropertyUtil.getErrorMessage(111554, OCConstants.ERROR_LOYALTY_FLAG) + " "
									+ enrollRequest.getMembership().getPhoneNumber().trim() + ".",
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					return enrollResponse;
				}
				String phoneNumber=LoyaltyProgramHelper.preparePhoneNumber(user,phoneParse);//APP-3792
				enrollRequest.getMembership().setPhoneNumber(phoneNumber);
				//enrollRequest.getMembership().setPhoneNumber(phoneParse);

				loyaltyProgram = findMobileBasedProgram(user.getUserId());
				if (loyaltyProgram == null) {
					status = new Status("111507", PropertyUtil.getErrorMessage(111507, OCConstants.ERROR_LOYALTY_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					return enrollResponse;
				}

				contactLoyalty = findContactLoyaltyByPhone(enrollRequest.getMembership().getPhoneNumber().trim(),
						loyaltyProgram.getProgramId(), user.getUserId(), user.getCountryCarrier(),
						user.getUserOrganization().getMaxNumberOfDigits());
				
				List<POSMapping> contactPOSMap = null;
				POSMappingDao posMappingDao = (POSMappingDao) ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
				contactPOSMap = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", user.getUserId());
				
				Contacts jsonContact = new Contacts();
				jsonContact.setUsers(user);
				
				if (contactLoyalty != null) {
					
					boolean isExists = false;
					Contacts dbContact = null;
					ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
					ContactsLoyaltyDaoForDML loyaltyDao = (ContactsLoyaltyDaoForDML) ServiceLocator.getInstance()
							.getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
					
					if(enrollRequest.getHeader().getSourceType().equals(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP)) {//APP-4634
						
						if(contactPOSMap != null){
							jsonContact = setContactFields(jsonContact, contactPOSMap, enrollRequest,OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE);
						}
						logger.info("----Through L_APP enrollRequest.getCustomer().getPhone()=="+enrollRequest.getCustomer().getPhone()+"----jsonContact.getMobilePhone()=="+jsonContact.getMobilePhone());
						
						Map<String, Object> contactAndDataFlags = validateAndSavedbContact(jsonContact, mlList, user,enrollRequest);
						dbContact = (Contacts) contactAndDataFlags.get("dbContact");
						isExists = (Boolean) contactAndDataFlags.get("isExists");
						
					}
					String msg = PropertyUtil.getErrorMessage(111506, OCConstants.ERROR_LOYALTY_FLAG) + " "
							+ contactLoyalty.getCardNumber()
									+	"Email Address:"+ (contactLoyalty.getEmailId()!=null?contactLoyalty.getEmailId():"")+", "
									+	"Name:"+  (contactLoyalty.getContact().getFirstName() !=null?contactLoyalty.getContact().getFirstName():"") +" "+
										 (contactLoyalty.getContact().getLastName()!=null?contactLoyalty.getContact().getLastName():"")+".";
					status = new Status("0", msg, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
					Customer customer = findCustomerByCid(contactLoyalty.getContact(), user.getUserId());
					List<MatchedCustomer> customers = prepareMatchedCustomers(customer,
							"" + contactLoyalty.getCardNumber());
					membershipResponse = setMembershipResponse(contactLoyalty);
					
					if(isExists && dbContact!=null) {//APP-4634
						
						boolean isCltyUpdated = false;
						if(enrollRequest.getCustomer().getPassword()!=null && !enrollRequest.getCustomer().getPassword().isEmpty()) {	
							
							String password = EncryptDecryptLtyMembshpPwd.encrypt(EncryptDecryptLtyMembshpPwd.decryptPwd(enrollRequest.getCustomer().getPassword()));
							contactLoyalty.setMembershipPwd(password);
							isCltyUpdated = true;
						}
						if(enrollRequest.getCustomer().getEmailAddress()!=null && !enrollRequest.getCustomer().getEmailAddress().isEmpty()
								&& contactLoyalty.getEmailId()!=null && !contactLoyalty.getEmailId().equals(enrollRequest.getCustomer().getEmailAddress())) {
							
							contactLoyalty.setEmailId(enrollRequest.getCustomer().getEmailAddress());
							isCltyUpdated = true;
						}
						
						logger.info("isCltyUpdated : "+isCltyUpdated);
						contactsDaoForDML.saveOrUpdate(dbContact);
						if(isCltyUpdated) loyaltyDao.saveOrUpdate(contactLoyalty);
						logger.info("updated contact data ");
					}
					enrollResponse = prepareEnrollmentResponse(responseHeader, membershipResponse, customers, status);
					return enrollResponse;
				}

				String failedRequisites = validateProgramRequisites(enrollRequest, loyaltyProgram);
				if (failedRequisites != null) {
					status = new Status("111509", PropertyUtil.getErrorMessage(111509, OCConstants.ERROR_LOYALTY_FLAG)
							+ " " + failedRequisites + ".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					return enrollResponse;
				}


				/*if (enrollRequest.getCustomer().getPhone() != null
						&& !enrollRequest.getCustomer().getPhone().trim().isEmpty() && !enrollRequest.getMembership()
								.getPhoneNumber().trim().equals(enrollRequest.getCustomer().getPhone().trim())) {
					status = new Status("111541", PropertyUtil.getErrorMessage(111541, OCConstants.ERROR_LOYALTY_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					return enrollResponse;
				}*/
				//fixed for Ginesys_Dubai issue - 2139.04
				if (enrollRequest.getCustomer().getPhone() != null
						&& !enrollRequest.getCustomer().getPhone().trim().isEmpty()) {
					String customerphoneParse = Utility.phoneParse(enrollRequest.getCustomer().getPhone().trim(),
							user != null ? user.getUserOrganization() : null);
					
					if(customerphoneParse !=null )enrollRequest.getCustomer().setPhone(customerphoneParse);

					if(!customerphoneParse.equals(enrollRequest.getCustomer().getPhone().trim())) {
						status = new Status("111541", PropertyUtil.getErrorMessage(111541, OCConstants.ERROR_LOYALTY_FLAG),
								OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						return enrollResponse;
					}
				}

				LoyaltyProgramExclusion loyaltyExclusion = getLoyaltyExclusion(loyaltyProgram.getProgramId());
				if (loyaltyExclusion != null) {
					status = validateStoreNumberExclusion(enrollRequest, loyaltyProgram, loyaltyExclusion);
					if (status != null) {
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						return enrollResponse;
					}
				}
				
				/*
				 * These validation were before done inside createMembership block  APP-1326*/
								
				

				/*List<POSMapping> contactPOSMap = null;
				POSMappingDao posMappingDao = (POSMappingDao) ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
				contactPOSMap = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", user.getUserId());
				
				Contacts jsonContact = new Contacts();
				jsonContact.setUsers(user);*/
				String subsidiary = enrollRequest.getHeader().getSubsidiaryNumber() != null && !enrollRequest.getHeader().getSubsidiaryNumber().trim().isEmpty() ? enrollRequest.getHeader().getSubsidiaryNumber().trim() : null; 
				String store = enrollRequest.getHeader().getStoreNumber() != null ? enrollRequest.getHeader().getStoreNumber().trim() : enrollRequest.getHeader().getStoreNumber();
				
				if(contactPOSMap != null){
					jsonContact = setContactFields(jsonContact, contactPOSMap, enrollRequest,OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD);//#Card flow
				}
				logger.info("----enrollRequest.getCustomer().getPhone()=="+enrollRequest.getCustomer().getPhone()+"----jsonContact.getMobilePhone()=="+jsonContact.getMobilePhone());
								
				if(jsonContact.getMobilePhone() != null && !jsonContact.getMobilePhone().isEmpty())
					enrollRequest.getCustomer().setPhone(jsonContact.getMobilePhone());

				
				Map<String, Object> contactAndDataFlags = validateAndSavedbContact(jsonContact, mlList, user,enrollRequest);
				Contacts dbContact = (Contacts) contactAndDataFlags.get("dbContact");
				boolean isExists = (Boolean) contactAndDataFlags.get("isExists");
				
				if(isExists){
					ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
					contactLoyalty = contactsLoyaltyDao.findByContactIdStrAndPrgmId(user.getUserId(), dbContact.getContactId()+"",loyaltyProgram.getProgramId());
					if(contactLoyalty != null) {
						//Mobile based changes

						//status = new Status("111563", PropertyUtil.getErrorMessage(111563, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactLoyalty.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);//Could be done before
						String msg = PropertyUtil.getErrorMessage(111506, OCConstants.ERROR_LOYALTY_FLAG) + " "+contactLoyalty.getCardNumber()+", "
								+	"Email Address:"+ (contactLoyalty.getEmailId()!=null?contactLoyalty.getEmailId():"")+", "
								+	"Mobile #:"+(contactLoyalty.getMobilePhone()!=null?contactLoyalty.getMobilePhone():"")+", "
								+	"Name:"+  (contactLoyalty.getContact().getFirstName() !=null?contactLoyalty.getContact().getFirstName():"") +" "+
									 (contactLoyalty.getContact().getLastName()!=null?contactLoyalty.getContact().getLastName():"")+".";
						status = new Status("0", msg, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
						membershipResponse = setMembershipResponse(contactLoyalty);
						
						return prepareEnrollmentResponse(responseHeader, membershipResponse, null, status);
					}
				}
			
				
				//String encPwd = null;
				/*if(enrollRequest.getHeader().getSourceType() != null && 
						!enrollRequest.getHeader().getSourceType().isEmpty() && 
						(enrollRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) || 
								enrollRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP))){
					encPwd = generateMembrshpPwd();
				}*/	
				String encPwd = null;
				String sourceType = enrollRequest.getHeader().getSourceType();
				String password = enrollRequest.getCustomer().getPassword();
				if(/*sourceType != null && !sourceType.isEmpty() && 
						sourceType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) 
						&&*/ password != null && !password.trim().isEmpty()){
					/*if(password == null || password.trim().isEmpty()){
						status = new Status("800033",
								PropertyUtil.getErrorMessage(800033, OCConstants.ERROR_MOBILEAPP_FLAG),
								OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						return enrollResponse;
					}*/
					try {
						logger.debug("password.trim() === "+password.trim());
						encPwd = EncryptDecryptLtyMembshpPwd.decryptProgramPwd(password.trim());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("incorrect password===", e);
					}
					logger.debug("encPwd === "+encPwd);
					if(encPwd != null)encPwd = EncryptDecryptLtyMembshpPwd.encrypt(encPwd);
				}
				logger.debug("encPwd === "+encPwd);
				AsyncExecuterEnrollment executerThread = new AsyncExecuterEnrollment(enrollRequest, responseHeader,
						loyaltyProgram, null, mode, user, mlList, OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE, null,
						false, encPwd);
				
				if(mode.equals(OCConstants.LOYALTY_OFFLINE_MODE)){
					executerThread.performEnrollment();
				}else{
					
					executerThread.start();
				}
				
				membershipResponse = new MembershipResponse();
				membershipResponse.setCardNumber("");
				//if(encPwd != null) membershipResponse.setPassword(encPwd);
				membershipResponse.setCardPin("");
				membershipResponse.setExpiry("");
				membershipResponse.setTierLevel("");
				membershipResponse.setTierName("");
				membershipResponse.setPhoneNumber(enrollRequest.getMembership().getPhoneNumber());
				membershipResponse.setFingerprintValidation(enrollRequest.getMembership().getFingerprintValidation());
				status = new Status("0", "Enrollment was successful", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				enrollResponse = prepareEnrollmentResponse(responseHeader, membershipResponse, null, status);// customers and
																								// accountResponse had
																								// to be
																								// removed.APP-1326
				return enrollResponse;

			} else {
				status = new Status("111502", PropertyUtil.getErrorMessage(111502, OCConstants.ERROR_LOYALTY_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
				return enrollResponse;
			}
		} catch (Exception e) {
			logger.error("Exception in processing enrollment request...", e);
			status = new Status("101000", PropertyUtil.getErrorMessage(101000, OCConstants.ERROR_LOYALTY_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
			return enrollResponse;
		}

	}

	private ContactsLoyalty findMembershpByCard(String cardNumber, Long programId, Long userId) throws Exception {

		ContactsLoyalty loyalty = null;
		ContactsLoyaltyDao contactLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		loyalty = contactLoyaltyDao.findByProgram(cardNumber, programId, userId);
		return loyalty;
	}

	private ContactsLoyalty findMembershpByPhone(String mobilePhone, Long programId, Long userId, short countryCarrier,
			int maxDigits) throws Exception {

		mobilePhone = mobilePhone.replaceAll("[- ()]", "");// APP-117
		if (mobilePhone != null && mobilePhone.trim().length() != 0) {
			mobilePhone = mobilePhone.trim();
			// UserOrganization organization= user!=null ? user.getUserOrganization() : null
			// ;
			// phone = phoneParse(phone, organization);
			if (mobilePhone != null && mobilePhone.startsWith(countryCarrier + "")
					&& mobilePhone.length() > maxDigits) {
				mobilePhone = mobilePhone.replaceFirst(countryCarrier + "", "");
				// logger.info("phone is============>"+phone);
			}
			try {
				mobilePhone = Long.parseLong(mobilePhone) + "";

			} catch (Exception e) {
				logger.info("OOPs error ");
			}
		}

		String mobileWithCarrier = countryCarrier + mobilePhone;
		ContactsLoyalty loyalty = null;
		ContactsLoyaltyDao contactLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		loyalty = contactLoyaltyDao.findByMobilePhone(mobilePhone, mobileWithCarrier, programId, userId);
		return loyalty;
	}
	private ContactsLoyalty findMembershpByEmailId(String emailId, Long programId, Long userId,
			int maxDigits) throws Exception {

		ContactsLoyalty loyalty = null;
		ContactsLoyaltyDao contactLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		loyalty = contactLoyaltyDao.findMembershipByEmailInCl( emailId, userId,programId);
		return loyalty;
	}

	
	
	
	
	
	
	private String validateProgramRequisites(LoyaltyEnrollRequest enrollRequest, LoyaltyProgram program)
			throws Exception {

		Customer customer = enrollRequest.getCustomer();

		String mandatoryStr = null;
		String requisites = program.getRegRequisites();
		if (requisites != null && requisites.trim().length() > 0) {
			String[] requisitesArr = requisites.split(";=;");
			for (String requisite : requisitesArr) {

				if (requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_FIRSTNAME)
						&& (customer.getFirstName() == null || customer.getFirstName().trim().isEmpty())) {
					if (mandatoryStr == null) {
						mandatoryStr = "firstName";
					} else {
						mandatoryStr += "," + "firstName";
					}
				} else if (requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_LASTNAME)
						&& (customer.getLastName() == null || customer.getLastName().trim().isEmpty())) {
					if (mandatoryStr == null) {
						mandatoryStr = "lastName";
					} else {
						mandatoryStr += "," + "lastName";
					}
				} else if (requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_EMAILID)
						&& (customer.getEmailAddress() == null || customer.getEmailAddress().trim().isEmpty())) {
					if (mandatoryStr == null) {
						mandatoryStr = "emailAddress";
					} else {
						mandatoryStr += "," + "emailAddress";
					}
				} else if ((OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(program.getMembershipType()))
						&& requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_MOBILEPHONE)
						&& (customer.getPhone() == null || customer.getPhone().trim().isEmpty())) {
					if (mandatoryStr == null) {
						mandatoryStr = "phone";
					} else {
						mandatoryStr += "," + "phone";
					}
				} else if (requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_ADDRESSONE)
						&& (customer.getAddressLine1() == null || customer.getAddressLine1().trim().isEmpty())) {
					if (mandatoryStr == null) {
						mandatoryStr = "addressLine1";
					} else {
						mandatoryStr += "," + "addressLine1";
					}
				} else if (requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_CITY)
						&& (customer.getCity() == null || customer.getCity().trim().isEmpty())) {
					if (mandatoryStr == null) {
						mandatoryStr = "city";
					} else {
						mandatoryStr += "," + "city";
					}
				} else if (requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_STATE)
						&& (customer.getState() == null || customer.getState().trim().isEmpty())) {
					if (mandatoryStr == null) {
						mandatoryStr = "state";
					} else {
						mandatoryStr += "," + "state";
					}
				} else if (requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_ZIP)
						&& (customer.getPostal() == null || customer.getPostal().trim().isEmpty())) {
					if (mandatoryStr == null) {
						mandatoryStr = "postal";
					} else {
						mandatoryStr += "," + "postal";
					}
				} else if (requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_COUNTRY)
						&& (customer.getCountry() == null || customer.getCountry().trim().isEmpty())) {
					if (mandatoryStr == null) {
						mandatoryStr = "country";
					} else {
						mandatoryStr += "," + "country";
					}
				} else if (requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_BIRTHDAY)
						&& (customer.getBirthday() == null || customer.getBirthday().trim().isEmpty())) {
					if (mandatoryStr == null) {
						mandatoryStr = "birthday";
					} else {
						mandatoryStr += "," + "birthday";
					}
				} else if (requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_ANNIVERSARY)
						&& (customer.getAnniversary() == null || customer.getAnniversary().trim().isEmpty())) {
					if (mandatoryStr == null) {
						mandatoryStr = "anniversary";
					} else {
						mandatoryStr += "," + "anniversary";
					}
				} else if (requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_GENDER)
						&& (customer.getGender() == null || customer.getGender().trim().isEmpty())) {
					if (mandatoryStr == null) {
						mandatoryStr = "gender";
					} else {
						mandatoryStr += "," + "gender";
					}
				}
			}
		}
		return mandatoryStr;

	}

	private Status validateEnrollmentJsonData(LoyaltyEnrollRequest enrollRequest) throws Exception {
		logger.info("Entered validateEnrollmentJsonData method >>>>");

		Status status = null;
		if (enrollRequest == null) {
			status = new Status("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if (enrollRequest.getUser() == null) {
			status = new Status("101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if (enrollRequest.getMembership() == null) {
			status = new Status("101004", PropertyUtil.getErrorMessage(101004, OCConstants.ERROR_LOYALTY_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if (enrollRequest.getUser().getUserName() == null || enrollRequest.getUser().getUserName().trim().length() <= 0
				|| enrollRequest.getUser().getOrganizationId() == null
				|| enrollRequest.getUser().getOrganizationId().trim().length() <= 0
				|| enrollRequest.getUser().getToken() == null
				|| enrollRequest.getUser().getToken().trim().length() <= 0) {
			status = new Status("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		//JSON Validation for Mobile_APP
		if(enrollRequest.getHeader().getSourceType() != null && 
				!enrollRequest.getHeader().getSourceType().isEmpty() && 
				(enrollRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) || 
						enrollRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP))){
		
		
		if(enrollRequest.getMembership().getFingerprintValidation()!=null 
				&& !enrollRequest.getMembership().getFingerprintValidation().trim().equalsIgnoreCase("false") 
				&& !enrollRequest.getMembership().getFingerprintValidation().trim().equalsIgnoreCase("true")) {
			status = new Status("111594", PropertyUtil.getErrorMessage(111594, OCConstants.ERROR_LOYALTY_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
			}
		if(enrollRequest.getCustomer().getAddressLine1()!=null 
				  && !enrollRequest.getCustomer().getAddressLine1().isEmpty()
				  && enrollRequest.getCustomer().getAddressLine1().length()>200) {
				status = new Status("111613", PropertyUtil.getErrorMessage(111613, OCConstants.ERROR_LOYALTY_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
			
			if(enrollRequest.getCustomer().getAddressLine2()!=null 
					  && !enrollRequest.getCustomer().getAddressLine2().isEmpty()
					  && enrollRequest.getCustomer().getAddressLine2().length()>200) {
					status = new Status("111614", PropertyUtil.getErrorMessage(111614, OCConstants.ERROR_LOYALTY_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return status;
				}
		}
		//TODO changes needed
		/*if(enrollRequest.getCustomer().getEmailAddress()!= null
				&& !enrollRequest.getCustomer().getEmailAddress().equals(Constants.STRING_NILL) &&
				validateEmailId(enrollRequest.getCustomer().getEmailAddress()) == null) {
			status = new Status("111612", PropertyUtil.getErrorMessage(111612, OCConstants.ERROR_LOYALTY_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;	
		}*/
		
		return status;
	}

	private LoyaltyEnrollResponse validateUserObject(LoyaltyEnrollRequest enrollRequest, ResponseHeader responseHeader,
			Users user) throws Exception {
		LoyaltyEnrollResponse enrollResponse = null;
		Status status = null;

		if (user == null) {
			status = new Status("101013", PropertyUtil.getErrorMessage(101013, OCConstants.ERROR_LOYALTY_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
			return enrollResponse;
		}

		if (!user.isEnabled()) {
			status = new Status("111558", PropertyUtil.getErrorMessage(111558, OCConstants.ERROR_LOYALTY_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
			return enrollResponse;
		}
		if (user.getPackageExpiryDate().before(Calendar.getInstance())) {
			status = new Status("111559", PropertyUtil.getErrorMessage(111559, OCConstants.ERROR_LOYALTY_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
			return enrollResponse;
		}

		return enrollResponse;
	}

	private LoyaltyEnrollResponse prepareEnrollmentResponse(ResponseHeader header,
			MembershipResponse membershipResponse, List<MatchedCustomer> matchedCustomers, Status status)
			throws BaseServiceException {
		LoyaltyEnrollResponse enrollResponse = new LoyaltyEnrollResponse();
		enrollResponse.setHeader(header);

		if (membershipResponse == null) {
			membershipResponse = new MembershipResponse();
			membershipResponse.setCardNumber("");
			membershipResponse.setCardPin("");
			membershipResponse.setExpiry("");
			membershipResponse.setPhoneNumber("");
			membershipResponse.setTierLevel("");
			membershipResponse.setTierName("");
		}
		if (matchedCustomers == null) {
			matchedCustomers = new ArrayList<MatchedCustomer>();
		}
		enrollResponse.setMembership(membershipResponse);
		enrollResponse.setMatchedCustomers(matchedCustomers);
		enrollResponse.setStatus(status);
		return enrollResponse;
	}

	private Users getUser(String userName, String orgId, String userToken) throws Exception {

		String completeUserName = userName + Constants.USER_AND_ORG_SEPARATOR + orgId;
		UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Users user = usersDao.findUserByToken(completeUserName, userToken);
		return user;
	}

	private boolean checkIfLong(String in) {
		try {
			Long.parseLong(in);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}

	private LoyaltyCards findLoyaltyCardByUserId(String cardNumber, Long userId) throws Exception {

		LoyaltyCardsDao loyaltyCardDao = (LoyaltyCardsDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		return loyaltyCardDao.findByCardNoAnduserId(cardNumber, userId);

	}

	private ContactsLoyalty getDestMembershipIfAny(ContactsLoyalty contactLoyalty) throws Exception {
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		if (contactLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)
				&& contactLoyalty.getTransferedTo() != null) {
			return loyaltyDao.findAllByLoyaltyId(contactLoyalty.getTransferedTo());

		}

		return null;
	}

	private Customer findCustomerByCid(Contacts contact, Long userId) throws Exception {

		if (contact == null || contact.getContactId() == null)
			return null;

		ContactsDao contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		Contacts contact1 = contactsDao.findById(contact.getContactId());
		if (contact1 == null)
			return null;

		return prepareCustomerFromContact(contact);
	}

	private Customer prepareCustomerFromContact(Contacts contact) {

		Customer customer = new Customer();

		customer.setAddressLine1(contact.getAddressOne() == null ? "" : contact.getAddressOne());
		customer.setAddressLine2(contact.getAddressTwo() == null ? "" : contact.getAddressTwo());
		if (contact.getAnniversary() == null) {
			customer.setAnniversary("");
		} else {
			String anniversaryDate = MyCalendar.calendarToString(contact.getAnniversary(),
					MyCalendar.FORMAT_DATETIME_STYEAR);
			customer.setAnniversary(anniversaryDate);
		}
		if (contact.getBirthDay() == null) {
			customer.setBirthday("");
		} else {
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
		customer.setPhone(contact.getPhone() == null ? "" : "" + contact.getPhone());
		customer.setPostal(contact.getZip() == null ? "" : contact.getZip());
		customer.setState(contact.getState() == null ? "" : contact.getState());

		return customer;
	}

	private List<MatchedCustomer> prepareMatchedCustomers(Customer customer, String membershipNumber) {

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

	private Status validateStoreNumberExclusion(LoyaltyEnrollRequest enrollRequest, LoyaltyProgram program,
			LoyaltyProgramExclusion loyaltyExclusion) throws Exception {

		Status status = null;
		if (loyaltyExclusion.getStoreNumberStr() != null && !loyaltyExclusion.getStoreNumberStr().trim().isEmpty()) {
			if (enrollRequest.getHeader().getStoreNumber() == null
					|| enrollRequest.getHeader().getStoreNumber().length() <= 0) {
				status = new Status("111501", PropertyUtil.getErrorMessage(111501, OCConstants.ERROR_LOYALTY_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;

			}
			String[] storeNumberArr = loyaltyExclusion.getStoreNumberStr().split(";=;");
			for (String storeNo : storeNumberArr) {
				if (enrollRequest.getHeader().getStoreNumber().trim().equals(storeNo.trim())) {
					status = new Status("111532", PropertyUtil.getErrorMessage(111532, OCConstants.ERROR_LOYALTY_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return status;
				}
			}
		}

		return status;
	}

	private void deleteDCard(LoyaltyCards loyaltyCard) throws Exception {

		LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		LoyaltyCardsDaoForDML loyaltyCardsDaoForDML = (LoyaltyCardsDaoForDML) ServiceLocator.getInstance()
				.getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);
		// loyaltyCardsDao.deleteBy(loyaltyCard.getCardId());
		loyaltyCardsDaoForDML.deleteBy(loyaltyCard.getCardId());

	}

	private LoyaltyProgramExclusion getLoyaltyExclusion(Long programId) throws Exception {
		try {
			LoyaltyProgramExclusionDao exclusionDao = (LoyaltyProgramExclusionDao) ServiceLocator.getInstance()
					.getDAOByName(OCConstants.LOYALTY_PROGRAM_EXCLUSION_DAO);
			return exclusionDao.getExlusionByProgId(programId);
		} catch (Exception e) {
			logger.error("Exception in getting loyalty exclusion ..", e);
		}
		return null;
	}

	private void updateLoyaltyCardStatus(LoyaltyCards loyaltyCard, String status) throws Exception {

		LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		LoyaltyCardsDaoForDML loyaltyCardsDaoForDML = (LoyaltyCardsDaoForDML) ServiceLocator.getInstance()
				.getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);

		loyaltyCard.setStatus(status);
		// loyaltyCardsDao.saveOrUpdate(loyaltyCard);
		loyaltyCardsDaoForDML.saveOrUpdate(loyaltyCard);

	}

	private Customer findCustomer(String cardNumber, Long programId, Long userId) throws Exception {

		ContactsLoyalty loyalty = findMembershpByCard(cardNumber, programId, userId);
		if (loyalty == null || loyalty.getContact() == null || loyalty.getContact().getContactId() == null)
			return null;

		ContactsDao contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		Contacts contact = contactsDao.findById(loyalty.getContact().getContactId());
		if (contact == null)
			return null;

		return prepareCustomerFromContact(contact);
	}

	private ContactsLoyalty findContactLoyaltyByPhone(String mobilePhone, Long programId, Long userId,
			short countryCarrier, int maxDigits) throws Exception {
		if (mobilePhone != null && mobilePhone.trim().length() != 0) {
			mobilePhone = mobilePhone.trim();
			// UserOrganization organization= user!=null ? user.getUserOrganization() : null
			// ;
			// phone = phoneParse(phone, organization);
			if (mobilePhone != null && mobilePhone.startsWith(countryCarrier + "")
					&& mobilePhone.length() > maxDigits) {
				mobilePhone = mobilePhone.replaceFirst(countryCarrier + "", "");
				// logger.info("phone is============>"+phone);
			}
			try {
				mobilePhone = Long.parseLong(mobilePhone) + "";

			} catch (Exception e) {
				logger.info("OOPs error ");
			}
		}
		String mobileWithCarrier = countryCarrier + mobilePhone;
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		return contactsLoyaltyDao.findByMembershipNoAndUserId(Long.valueOf(mobilePhone),
				Long.valueOf(mobileWithCarrier), programId, userId);
	}

	private LoyaltyProgram findMobileBasedProgram(Long userId) throws Exception {
		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		return loyaltyProgramDao.findProgramByUserId(userId, OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE,
				OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE);
	}

	private LoyaltyCards insertDCard(String cardNumber, String cardPin, Users user, LoyaltyProgram dCardBasedProgram,
			LoyaltyCardSet dCardBasedCardSet) throws Exception {

		LoyaltyCards loyaltyCard = findCardByCardNumber(dCardBasedProgram.getProgramId().toString(),
				dCardBasedCardSet.getCardSetId().toString(), cardNumber, dCardBasedProgram.getUserId());

		if (loyaltyCard != null && loyaltyCard.getStatus().equals(OCConstants.LOYALTY_CARD_STATUS_SELECTED)) {
			logger.info("Usergenerated card with selected status");
			return null;
		} else if (loyaltyCard != null) {
			return loyaltyCard;
		} else {
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

	private LoyaltyCards findCardByCardNumber(String programIdStr, String cardSetIdStr, String cardNumber, Long userId)
			throws Exception {
		LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		return loyaltyCardsDao.findCardByProgram(programIdStr, cardSetIdStr, cardNumber, userId);
	}

	private void updateCardSetQuantity(LoyaltyCardSet cardSet) throws Exception {
		LoyaltyCardSetDao loyaltyCardSetDao = (LoyaltyCardSetDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
		LoyaltyCardSetDaoForDML loyaltyCardSetDaoForDML = (LoyaltyCardSetDaoForDML) ServiceLocator.getInstance()
				.getDAOForDMLByName(OCConstants.LOYALTY_CARD_SET_DAO_FOR_DML);
		// loyaltyCardSetDao.updateCardSetQuantity(cardSet.getCardSetId(), 1l);
		loyaltyCardSetDaoForDML.updateCardSetQuantity(cardSet.getCardSetId(), 1l);
	}

	private LoyaltyProgram findDefaultProgram(Long userId) throws Exception {
		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		return loyaltyProgramDao.findDefaultProgramByUserId(userId);
	}

	private List<LoyaltyCardSet> findLoyaltyActiveCardSet(Long programId) throws Exception {
		LoyaltyCardSetDao loyaltyCardSetDao = (LoyaltyCardSetDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
		return loyaltyCardSetDao.findActiveByProgramId(programId);
	}

	private Contacts findOCContact(Contacts jsonContact, Users user) throws Exception {
	//logger.info("Entered findOCContact method >>>>");
	POSMappingDao posMappingDao = (POSMappingDao)ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
	ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
	TreeMap<String, List<String>> priorMap =  Utility.getPriorityMap(user.getUserId(), Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
	Contacts dbContact = contactsDao.findContactByUniqPriority(priorMap, jsonContact, user.getUserId(), user);
	//logger.info("Exited findOCContact method >>>>");
	return dbContact;
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
			if(custFieldAttribute.startsWith("UDF") && dateTypeStr.startsWith("Date")){
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
						logger.info("after utility phone parse: "+phoneParse);
						phoneParse=LoyaltyProgramHelper.preparePhoneNumber(user,phoneParse);//APP-3792
						logger.info("final phone parse: "+phoneParse);
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
				}else if (custFieldAttribute.equals(POSFieldsEnum.homeStore.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.homeStore.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}else if (custFieldAttribute.equals(POSFieldsEnum.subsidiaryNumber.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.subsidiaryNumber.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf1.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf1.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf2.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf2.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf3.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf3.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf4.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf4.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf5.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf5.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf6.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf6.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf7.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf7.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf8.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf8.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf9.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf9.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf10.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf10.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf11.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf11.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf12.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf12.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf13.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf13.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf14.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf14.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf15.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf15.getPojoField(), strArg);
					params = new Object[] { fieldValue };
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
private Map<String,Object> validateAndSavedbContact(Contacts jsonContact, MailingList mlList, Users user,LoyaltyEnrollRequest enrollRequest) throws Exception {
		
		Contacts dbContact = findOCContact(jsonContact, user);
		boolean isExists = false;
		String source= enrollRequest.getHeader().getSourceType();
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
			if(source != null) {
				if (source.toString().equalsIgnoreCase("Store")) {
					source = Constants.CONTACT_OPTIN_MEDIUM_POS;
				} else if (source.toString().equalsIgnoreCase("Webform")) {
					source = Constants.CONTACT_OPTIN_MEDIUM_WEBFORM;
				}else if(source.toString().equalsIgnoreCase("LoyaltyApp")) {
					source = OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP;
				}else if(source.toString().equalsIgnoreCase("Mobile_App")) {
					source = OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP;
				}
				else if(source.equalsIgnoreCase(Constants.CONTACT_OPTIN_MEDIUM_ECOMMERCE)){
					source = Constants.CONTACT_OPTIN_MEDIUM_ECOMMERCE;
				}
			}

			dbContact.setOptinMedium(source != null ? source : Constants.CONTACT_OPTIN_MEDIUM_POS);
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
				isExists = true;
			
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
				
				if(source != null) {
					if (source.toString().equalsIgnoreCase("Store")) {
						source = Constants.CONTACT_OPTIN_MEDIUM_POS;
					} else if (source.toString().equalsIgnoreCase("Webform")) {
						source = Constants.CONTACT_OPTIN_MEDIUM_WEBFORM;
					}else if(source.toString().equalsIgnoreCase("LoyaltyApp")) {
						source = OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP;
					}else if(source.toString().equalsIgnoreCase("Mobile_App")) {
						source = OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP;
					}
					else if(source.equalsIgnoreCase(Constants.CONTACT_OPTIN_MEDIUM_ECOMMERCE)){
						source = Constants.CONTACT_OPTIN_MEDIUM_ECOMMERCE;
					}
				}
				dbContact.setOptinMedium(source != null ? source : Constants.CONTACT_OPTIN_MEDIUM_POS);
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
		}	
		Map<String, Object> returnValues = new HashMap<String, Object>();
		returnValues.put("dbContact", dbContact);
		returnValues.put("isExists", isExists);
		return returnValues;
		
	}
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
//	fieldValue = enrollRequest.getCustomer().getPhone();
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
if(posMapping.getCustomFieldName().equalsIgnoreCase("home store")) {
	fieldValue = enrollRequest.getCustomer().getHomeStore();
	return fieldValue;
}
if(posMapping.getCustomFieldName().equalsIgnoreCase("Subsidiary Number")) {
	fieldValue = enrollRequest.getCustomer().getSubsidiaryNumber();
	return fieldValue;
}

if(posMapping.getCustomFieldName().equalsIgnoreCase("UDF1")) {
	fieldValue = enrollRequest.getCustomer().getUDF1();
	return fieldValue;
}
if(posMapping.getCustomFieldName().equalsIgnoreCase("UDF2")) {
	fieldValue = enrollRequest.getCustomer().getUDF2();
	return fieldValue;
}
if(posMapping.getCustomFieldName().equalsIgnoreCase("UDF3")) {
	fieldValue = enrollRequest.getCustomer().getUDF3();
	return fieldValue;
}
if(posMapping.getCustomFieldName().equalsIgnoreCase("UDF4")) {
	fieldValue = enrollRequest.getCustomer().getUDF4();
	return fieldValue;
}
if(posMapping.getCustomFieldName().equalsIgnoreCase("UDF5")) {
	fieldValue = enrollRequest.getCustomer().getUDF5();
	return fieldValue;
}
if(posMapping.getCustomFieldName().equalsIgnoreCase("UDF6")) {
	fieldValue = enrollRequest.getCustomer().getUDF6();
	return fieldValue;
}
if(posMapping.getCustomFieldName().equalsIgnoreCase("UDF7")) {
	fieldValue = enrollRequest.getCustomer().getUDF7();
	return fieldValue;
}
if(posMapping.getCustomFieldName().equalsIgnoreCase("UDF8")) {
	fieldValue = enrollRequest.getCustomer().getUDF8();
	return fieldValue;
}
if(posMapping.getCustomFieldName().equalsIgnoreCase("UDF9")) {
	fieldValue = enrollRequest.getCustomer().getUDF9();
	return fieldValue;
}
if(posMapping.getCustomFieldName().equalsIgnoreCase("UDF10")) {
	fieldValue = enrollRequest.getCustomer().getUDF10();
	return fieldValue;
}
if(posMapping.getCustomFieldName().equalsIgnoreCase("UDF11")) {
	fieldValue = enrollRequest.getCustomer().getUDF11();
	return fieldValue;
}
if(posMapping.getCustomFieldName().equalsIgnoreCase("UDF12")) {
	fieldValue = enrollRequest.getCustomer().getUDF12();
	return fieldValue;
}
if(posMapping.getCustomFieldName().equalsIgnoreCase("UDF13")) {
	fieldValue = enrollRequest.getCustomer().getUDF13();
	return fieldValue;
}
if(posMapping.getCustomFieldName().equalsIgnoreCase("UDF14")) {
	fieldValue = enrollRequest.getCustomer().getUDF14();
	return fieldValue;
}
if(posMapping.getCustomFieldName().equalsIgnoreCase("UDF15")) {
	fieldValue = enrollRequest.getCustomer().getUDF15();
	return fieldValue;
}

logger.debug("-------exit  getFieldValue---------");
return fieldValue;
}//getFieldValue
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
Status validateCard(LoyaltyCards loyaltyCard,Users user,LoyaltyEnrollRequest enrollRequest) {
	Status status = null;
	
	if (loyaltyCard.getStatus() == null || loyaltyCard.getStatus().isEmpty()) {
		String msg = PropertyUtil.getErrorMessage(111538, OCConstants.ERROR_LOYALTY_FLAG) + " "
				+ enrollRequest.getMembership().getCardNumber().trim() + ".";
		status = new Status("111538", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		
		return status;
	}
	if(loyaltyCard.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_CARD_STATUS_ENROLLED)){
		ContactsLoyalty contactLoyalty = null;
		try {
			contactLoyalty = findMembershpByCard(loyaltyCard.getCardNumber(),loyaltyCard.getProgramId(),loyaltyCard.getUserId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("e===>"+e);
		}
		String msg = PropertyUtil.getErrorMessage(111506, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactLoyalty.getCardNumber()+", "
						+	"Email Address:"+ (contactLoyalty.getEmailId()!=null?contactLoyalty.getEmailId():"")+", "
						+	"Mobile #:"+(contactLoyalty.getMobilePhone()!=null?contactLoyalty.getMobilePhone():"")+", "
						+	"Name:"+  (contactLoyalty.getContact().getFirstName() !=null?contactLoyalty.getContact().getFirstName():"") +" "+
							 (contactLoyalty.getContact().getLastName()!=null?contactLoyalty.getContact().getLastName():"")+".";
		status = new Status("0", msg, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);// Removed matched customer as per the requirement.
		logger.info("Loyalty card is already enrolled ");
		return status;
	}
	
	return status;
	
}
Status validateCardSet(LoyaltyCardSet cardSetObj,LoyaltyCards loyaltyCard,LoyaltyEnrollRequest enrollRequest) {
	Status status = null;
	
	if (cardSetObj == null || cardSetObj.getStatus() == null || cardSetObj.getStatus().isEmpty()) {
		String msg = PropertyUtil.getErrorMessage(111574, OCConstants.ERROR_LOYALTY_FLAG) + " "
				+ enrollRequest.getMembership().getCardNumber().trim() + ".";
		status = new Status("111574", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		return status;
	}
	
	
	if(cardSetObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_SUSPENDED)){
		String msg = PropertyUtil.getErrorMessage(111591, OCConstants.ERROR_LOYALTY_FLAG)+" "+loyaltyCard.getCardNumber()+".";
		status = new Status("111591", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		return status;
	}
	
	
	if(!cardSetObj.getStatus().trim().equalsIgnoreCase(OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE)){
		String msg = PropertyUtil.getErrorMessage(111591, OCConstants.ERROR_LOYALTY_FLAG)+" "+loyaltyCard.getCardNumber()+".";
		status = new Status("111574", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		return status;
	}
	
	return status;
	
}

Status validateProgram(LoyaltyProgram loyaltyProgram,LoyaltyCards loyaltyCard) {
	Status status = null;
	
	if (loyaltyProgram == null || loyaltyProgram.getStatus() == null || loyaltyProgram.getStatus().isEmpty()) {
		String msg = PropertyUtil.getErrorMessage(111593, OCConstants.ERROR_LOYALTY_FLAG);
		status = new Status("111593", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		return status;
	}
	if(loyaltyProgram.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_SUSPENDED)){ 
		String msg = PropertyUtil.getErrorMessage(111585, OCConstants.ERROR_LOYALTY_FLAG)+" "+loyaltyCard.getCardNumber()+".";
		status = new Status("111585", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		return status;
	}
	if(!loyaltyProgram.getStatus().trim()
			.equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)){ 
		String msg = PropertyUtil.getErrorMessage(111585, OCConstants.ERROR_LOYALTY_FLAG)+" "+loyaltyCard.getCardNumber()+".";
		status = new Status("111593", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		return status;
	}
	
	
	return status;
}
private MembershipResponse setMembershipResponse(ContactsLoyalty contactsLoyalty){
	
	MembershipResponse	membershipResponse = new MembershipResponse();
			membershipResponse.setExpiry("");
			membershipResponse.setPhoneNumber(contactsLoyalty!= null && contactsLoyalty.getMobilePhone() !=null ?contactsLoyalty.getMobilePhone():"");
			membershipResponse.setTierLevel("");
			membershipResponse.setTierName("");
			membershipResponse.setCardNumber(contactsLoyalty!= null && contactsLoyalty.getCardNumber() !=null ?contactsLoyalty.getCardNumber() : "");
			//membershipResponse.setPassword("");
			membershipResponse.setCardPin("");
			membershipResponse.setFingerprintValidation("");
			membershipResponse.setEmailAddress(contactsLoyalty!= null && contactsLoyalty.getEmailId() != null ?contactsLoyalty.getEmailId():"");
	 return membershipResponse;
} 

private String generateMembrshpPwd() throws Exception {
String memPwd = "";  
String encPwd = "";
//do {
	memPwd = RandomStringUtils.randomAlphanumeric(6);
	encPwd = EncryptDecryptLtyMembshpPwd.encrypt(memPwd);
	//} while(loyaltyDao.findByMembrshpPwd(encPwd) != null);
return encPwd;
}

}

