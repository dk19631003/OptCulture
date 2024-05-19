package org.mq.optculture.business.loyalty;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SparkBaseLocationDetails;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.contacts.CustomFieldValidator;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.SparkBaseLocationDetailsDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.loyalty.ContactsLoyaltyDetails;
import org.mq.optculture.model.loyalty.Customer;
import org.mq.optculture.model.loyalty.HeaderInfo;
import org.mq.optculture.model.loyalty.LoyaltyBasicInfo;
import org.mq.optculture.model.loyalty.LoyaltyDataRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyHistoryBasicInfo;
import org.mq.optculture.model.loyalty.LoyaltyHistoryRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyHistoryResponse;
import org.mq.optculture.model.loyalty.LoyaltyHistoryResponseObject;
import org.mq.optculture.model.loyalty.LoyaltyHistoryInfo;
import org.mq.optculture.model.loyalty.LoyaltyHistoryRequest;
import org.mq.optculture.model.loyalty.LoyaltyHistoryResponse;
import org.mq.optculture.model.loyalty.LoyaltyHistoryResponseObject;
import org.mq.optculture.model.loyalty.LoyaltyInfo;
import org.mq.optculture.model.loyalty.Status;
import org.mq.optculture.model.loyalty.StoreLocationInfo;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;

public class LoyaltyEnrollmentHistoryServiceImpl implements LoyaltyEnrollmentHistoryService{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		BaseResponseObject baseResponseObject = new BaseResponseObject();
		LoyaltyHistoryResponseObject loyaltyHistoryResponseObject = null;

		try {
			logger.debug("-------entered processRequest---------");
			//json to object
			Gson gson = new Gson();
			LoyaltyHistoryRequestObject loyaltyHistoryRequestObject = null;
			try {
				loyaltyHistoryRequestObject = gson.fromJson(baseRequestObject.getJsonValue(), LoyaltyHistoryRequestObject.class);
			} catch (Exception e) {
				Status status = new Status("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				LoyaltyHistoryResponse loyaltyHistoryResponse = new LoyaltyHistoryResponse();
				loyaltyHistoryResponse.setSTATUS(status);
				loyaltyHistoryResponseObject = new LoyaltyHistoryResponseObject();
				loyaltyHistoryResponseObject.setLOYALTYDATARESPONSE(loyaltyHistoryResponse);
				String responseJson = gson.toJson(loyaltyHistoryResponseObject);
				baseResponseObject.setJsonValue(responseJson);
				return baseResponseObject;
			}
			
			LoyaltyEnrollmentHistoryService loyaltyEnrollmentHistoryService = (LoyaltyEnrollmentHistoryService) ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_ENROLLMENT_HISTORY_SERVICE_IMPL);
			loyaltyHistoryResponseObject=(LoyaltyHistoryResponseObject) loyaltyEnrollmentHistoryService.processLoyaltyDataRequest(loyaltyHistoryRequestObject);
			//object to json
			String json = gson.toJson(loyaltyHistoryResponseObject);
			baseResponseObject.setJsonValue(json);
			logger.debug("-------exit  processRequest---------");
		} catch (Exception e) {
			logger.error("Exception ::" ,e);
			throw new BaseServiceException("Exception occured while processing processRequest::::: ", e);
		}

		return baseResponseObject;
	}//processRequest

	@Override
	public LoyaltyHistoryResponseObject processLoyaltyDataRequest(
			LoyaltyHistoryRequestObject loyaltyHistoryRequestObject)
					throws BaseServiceException {
		LoyaltyHistoryResponseObject loyaltyHistoryResponseObject = null;
		Status status = null;
		List<ContactsLoyaltyDetails> customerInfoList = null;
		long count = 0;
		boolean getOnlyCount = false;
		try {
			logger.debug("-------entered  processLoyaltyDataRequest---------");
			status = validateRootObjects(loyaltyHistoryRequestObject);
			if(status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getSTATUS())){
				loyaltyHistoryResponseObject = prepareFinalResponseObject(null,null,customerInfoList,null,count,getOnlyCount,status);
				return loyaltyHistoryResponseObject;
			}
			LoyaltyHistoryRequest loyaltyHistoryRequest = loyaltyHistoryRequestObject.getLOYALTYDATAREQ();
			status = validateloyaltyHistoryRequests(loyaltyHistoryRequest);
			if(status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getSTATUS())){
				loyaltyHistoryResponseObject = prepareFinalResponseObject(loyaltyHistoryRequest.getSTORELOCATIONINFO(),loyaltyHistoryRequest.getHEADERINFO(),customerInfoList,loyaltyHistoryRequest.getLOYALTYINFO(),count,getOnlyCount,status);
				return loyaltyHistoryResponseObject;
			}

			String userName = loyaltyHistoryRequest.getUSERDETAILS().getUSERNAME();
			String orgId = loyaltyHistoryRequest.getUSERDETAILS().getORGID();
			String token = loyaltyHistoryRequest.getUSERDETAILS().getTOKEN();
			status= validateInnerObjects(loyaltyHistoryRequest.getHEADERINFO().getREQUESTID(),
					loyaltyHistoryRequest.getSTORELOCATIONINFO().getSTORELOCATIONID(),token,userName,orgId);
			
			if(status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getSTATUS())){
				loyaltyHistoryResponseObject = prepareFinalResponseObject(loyaltyHistoryRequest.getSTORELOCATIONINFO(),loyaltyHistoryRequest.getHEADERINFO(),customerInfoList,loyaltyHistoryRequest.getLOYALTYINFO(),count,getOnlyCount,status);
				return loyaltyHistoryResponseObject;
			}
			
			String fullUserName = userName+ Constants.USER_AND_ORG_SEPARATOR +orgId;
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = usersDao.findByToken(fullUserName,token);

			status= validateUser(user);
			if(status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getSTATUS())){
				loyaltyHistoryResponseObject = prepareFinalResponseObject(loyaltyHistoryRequest.getSTORELOCATIONINFO(),loyaltyHistoryRequest.getHEADERINFO(),customerInfoList,loyaltyHistoryRequest.getLOYALTYINFO(),count,getOnlyCount,status);
				return loyaltyHistoryResponseObject;
			}
			
			MailingListDao mailingListDao = (MailingListDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
			MailingList mlList = mailingListDao.findPOSMailingList(user);
			
			if(mlList == null) {
				logger.info("Unable to find the user POS ml List");
				status = new Status("1007",PropertyUtil.getErrorMessage(1007,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				loyaltyHistoryResponseObject = prepareFinalResponseObject(loyaltyHistoryRequest.getSTORELOCATIONINFO(),loyaltyHistoryRequest.getHEADERINFO(),customerInfoList,loyaltyHistoryRequest.getLOYALTYINFO(),count,getOnlyCount,status);
				return loyaltyHistoryResponseObject;
			}

			String getOnlyCountStr = loyaltyHistoryRequest.getLOYALTYINFO().getGETONLYCOUNT();
			getOnlyCount = (getOnlyCountStr == null || getOnlyCountStr.equals("") ||
					getOnlyCountStr.toLowerCase().equals("n") || getOnlyCountStr.toLowerCase().equals("no")  ) ? false : true;
			String loyaltyEnrolledSource  = loyaltyHistoryRequest.getLOYALTYINFO().getSOURCETYPE();
			loyaltyEnrolledSource = loyaltyEnrolledSource == null || loyaltyEnrolledSource.isEmpty() ? "All" : loyaltyEnrolledSource;//need to confirm about default val here 
			String startDate = loyaltyHistoryRequest.getLOYALTYINFO().getSTARTDATE();
			String endDate = loyaltyHistoryRequest.getLOYALTYINFO().getENDDATE();
			status= validateDates(startDate,endDate);
			startDate = startDate.split(" ")[0]+" 00:00:00";
			endDate = endDate.split(" ")[0]+" 23:59:59";
			//logger.info("start :: "+startDate+" end "+endDate);
			if(status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getSTATUS())){ 
				loyaltyHistoryResponseObject = prepareFinalResponseObject(loyaltyHistoryRequest.getSTORELOCATIONINFO(),loyaltyHistoryRequest.getHEADERINFO(),customerInfoList,loyaltyHistoryRequest.getLOYALTYINFO(),count,getOnlyCount,status);
				return loyaltyHistoryResponseObject;
			}
			
			String mode = loyaltyHistoryRequest.getLOYALTYINFO().getMODE();
			
			if(mode == null || mode.isEmpty()){
				mode = "ALL";
			}
			else{
				mode = mode.toLowerCase();
			}
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			
			if(OCConstants.LOYALTY_SERVICE_TYPE_SB.equals(user.getloyaltyServicetype())) {
				if(loyaltyHistoryRequest.getSTORELOCATIONINFO() == null) {
					logger.info("Error : unable to find the StoreLocationInfo in JSON ****");
					status = new Status("1008",PropertyUtil.getErrorMessage(1008,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					loyaltyHistoryResponseObject = prepareFinalResponseObject(loyaltyHistoryRequest.getSTORELOCATIONINFO(),loyaltyHistoryRequest.getHEADERINFO(),customerInfoList,loyaltyHistoryRequest.getLOYALTYINFO(),count,getOnlyCount,status);
					return loyaltyHistoryResponseObject;
				}
				String storeLocationId = loyaltyHistoryRequest.getSTORELOCATIONINFO().getSTORELOCATIONID();
				if(storeLocationId == null || storeLocationId.trim().length() == 0) {
					logger.info("Error : find the  spark based StoreLocationID as empty in JSON ****");
					status = new Status("1014",PropertyUtil.getErrorMessage(1014,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					loyaltyHistoryResponseObject = prepareFinalResponseObject(loyaltyHistoryRequest.getSTORELOCATIONINFO(),loyaltyHistoryRequest.getHEADERINFO(),customerInfoList,loyaltyHistoryRequest.getLOYALTYINFO(),count,getOnlyCount,status);
					return loyaltyHistoryResponseObject;
				}
				SparkBaseLocationDetailsDao sparkBaseLocationDetailsDao = (SparkBaseLocationDetailsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASE_LOCATIONDETAILS_DAO);
				SparkBaseLocationDetails sparkBaseLoc = sparkBaseLocationDetailsDao.findBylocationId(user.getUserOrganization().getUserOrgId(), storeLocationId);
				status= validateSparkBaseLocAndMlList(sparkBaseLoc);
				if(status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getSTATUS())){
					loyaltyHistoryResponseObject = prepareFinalResponseObject(loyaltyHistoryRequest.getSTORELOCATIONINFO(),loyaltyHistoryRequest.getHEADERINFO(),customerInfoList,loyaltyHistoryRequest.getLOYALTYINFO(),count,getOnlyCount,status);
					return loyaltyHistoryResponseObject;
				}
				
					
					count = contactsLoyaltyDao.findLoyaltyCountBy(startDate, endDate, sparkBaseLoc.getLocationId(), loyaltyEnrolledSource, mode, user.getUserId());
				
				if(!getOnlyCount) {
					customerInfoList = getCustomerInfoList(loyaltyEnrolledSource,startDate,endDate,sparkBaseLoc, mode, count, user.getUserId());
				}
			}else if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equals(user.getloyaltyServicetype())){
					
					count = contactsLoyaltyDao.findLoyaltyCountBy(startDate, endDate, null, loyaltyEnrolledSource, mode, user.getUserId());
				
				if(!getOnlyCount) {
					
					customerInfoList = getCustomerInfoList(loyaltyEnrolledSource,startDate,endDate,null, mode, count, user.getUserId());
				}
				
				
			}
			
			//count = contactsLoyaltyDao.findLoyaltyCountByDates(startDate, endDate, sparkBaseLoc.getLocationId(), loyaltyEnrolledSource);
			
			
			//String mode = loyaltyHistoryRequest.getLOYALTYINFO().getMODE();
			/*if(mode == null || mode.isEmpty()){
				mode = "ALL";
			}*/
			
			status = new Status("0"," Number of Customers fetched :: "+count,OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			loyaltyHistoryResponseObject = prepareFinalResponseObject(loyaltyHistoryRequest.getSTORELOCATIONINFO(),loyaltyHistoryRequest.getHEADERINFO(),customerInfoList,loyaltyHistoryRequest.getLOYALTYINFO(),count,getOnlyCount,status);
			logger.debug("-------exit  processLoyaltyDataRequest---------");
		} catch (Exception e) {
			status = new Status("101000",PropertyUtil.getErrorMessage(101000,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			loyaltyHistoryResponseObject = prepareFinalResponseObject(loyaltyHistoryRequestObject.getLOYALTYDATAREQ().getSTORELOCATIONINFO(),
					loyaltyHistoryRequestObject.getLOYALTYDATAREQ().getHEADERINFO(),customerInfoList,loyaltyHistoryRequestObject.getLOYALTYDATAREQ().getLOYALTYINFO(),count,getOnlyCount,status);
			logger.error("Exception ::" ,e);
			throw new BaseServiceException("Exception occured while processing processLoyaltyDataRequest::::: ", e);
		}
		return loyaltyHistoryResponseObject;
	}//processLoyaltyDataRequest



	private Status validateRootObjects(
			LoyaltyHistoryRequestObject loyaltyHistoryRequestObject) throws BaseServiceException {
		logger.debug("-------entered  validateRootObjects---------");
		Status status = null;
		if(loyaltyHistoryRequestObject == null){
			logger.info("Error : Invalid json Object .. Returning. ****");
			status = new Status("1201",PropertyUtil.getErrorMessage(1201,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			
		}
		else if(loyaltyHistoryRequestObject.getLOYALTYDATAREQ() == null) {
			logger.info("Error : unable to find the Loyalty data Req Location in JSON ****");
			status = new Status("1003",PropertyUtil.getErrorMessage(1003,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		}
		logger.debug("-------exit  validateRootObjects---------");
		return status;
	}

	private Status validateloyaltyHistoryRequests(LoyaltyHistoryRequest loyaltyHistoryRequest) throws BaseServiceException {
		logger.debug("-------entered  validateloyaltyHistoryRequests---------");
		Status status = null;
		if(loyaltyHistoryRequest.getHEADERINFO() == null) {
			logger.info("Error : unable to find the HearderInfo in JSON ****");
			status = new Status("1004",PropertyUtil.getErrorMessage(1004,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		if(loyaltyHistoryRequest.getLOYALTYINFO() == null) {
			logger.info("Error : unable to find the  required Loyalty details in JSON ****");
			status = new Status("1009",PropertyUtil.getErrorMessage(1009,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(loyaltyHistoryRequest.getUSERDETAILS() == null) {
			logger.info("Error : unable to find the User Details in JSON ****");
			status = new Status("1011",PropertyUtil.getErrorMessage(1011,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		logger.debug("-------exit  validateloyaltyHistoryRequests---------");
		return status;
	}//validateloyaltyHistoryRequests

	private Status validateInnerObjects(String requestId,
			String storeLocationId, String token, String userName, String orgId) throws BaseServiceException {
		logger.debug("-------entered  validateInnerObjects---------");
		Status status = null;
		if(requestId == null || requestId.trim().length() == 0) {
			logger.info("Error : Request ID of HearderInfo is empty in JSON ****");
			status = new Status("1010",PropertyUtil.getErrorMessage(1010,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		if(token == null || token.trim().length() == 0) {
			logger.info("Error : User Token cannot be empty.");
			status = new Status("1012",PropertyUtil.getErrorMessage(1012,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(userName == null || userName.trim().length() == 0 || orgId == null || orgId.trim().length() == 0) {
			logger.info("Error : Username or organisation cannot be empty.");
			status = new Status("1013",PropertyUtil.getErrorMessage(1013,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		logger.debug("-------exit  validateInnerObjects---------");
		return status;
	}//validateInnerObjects

	private Status validateUser(Users user) throws BaseServiceException {
		logger.debug("-------entered  validateUser---------");
		Status status = null;
		if(user == null) {
			logger.info("Unable to find the user Obj");
			status = new Status("1005",PropertyUtil.getErrorMessage(1005,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		logger.debug("-------exit  validateUser---------");
		return status;
	}//validateUser

	private Status validateSparkBaseLocAndMlList(SparkBaseLocationDetails sparkBaseLoc) throws BaseServiceException {
		logger.debug("-------entered  validateSparkBaseLocAndMlList---------");
		Status status = null;
		if(sparkBaseLoc == null) {
			logger.info("Error : No SparkBaseDetails Found with the given credentials. ****");
			status = new Status("1006",PropertyUtil.getErrorMessage(1006,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		logger.debug("-------exit  validateSparkBaseLocAndMlList---------");
		return status;
	}//validateSparkBaseLocAndMlList

	private Status validateDates(String startDate,String endDate) throws BaseServiceException {
		logger.debug("-------entered  validateDates---------");
		Status status = null;
		if(!CustomFieldValidator.validateDate(startDate, "date", MyCalendar.FORMAT_DATETIME_STYEAR)) {
			logger.info("Error : unable to fetch the requested data,got wrong start date in JSON ****");
			status = new Status("1015",PropertyUtil.getErrorMessage(1015,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(!CustomFieldValidator.validateDate(endDate, "Date", MyCalendar.FORMAT_DATETIME_STYEAR)) {
			logger.info("Error : unable to fetch the requested data,got wrong end date in JSON ****");
			status = new Status("1016",PropertyUtil.getErrorMessage(1016,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		logger.debug("-------exit  validateDates---------");
		return status;
	}//validateDates


	private List<ContactsLoyaltyDetails> getCustomerInfoList(String loyaltyEnrolledSource, String startDate, String endDate,
			SparkBaseLocationDetails sparkBaseLoc, String mode, long count, long userID) throws BaseServiceException {
		//need to fetch all the customers info for 
		List<ContactsLoyaltyDetails> customerInfoList = new ArrayList<ContactsLoyaltyDetails>();
		try {
			logger.debug("-------entered  getCustomerInfoList---------");
			String subQry ="";
			if(sparkBaseLoc != null) {
				 subQry += " AND  locationId='"+sparkBaseLoc.getLocationId()+"'";
				 
			 }
			if(loyaltyEnrolledSource != null){
				
				if(loyaltyEnrolledSource.toLowerCase().equals("web-form")){
					loyaltyEnrolledSource = "all";
				}
				
				if(!loyaltyEnrolledSource.toLowerCase().equals("all")) {
					if(loyaltyEnrolledSource.toLowerCase().equals("web-form")){
						loyaltyEnrolledSource = "WebForm";
					}else if(loyaltyEnrolledSource.toLowerCase().equals("e-commerce")){
						loyaltyEnrolledSource = "eComm";
					}
					
					subQry += " AND (sourceType='"+loyaltyEnrolledSource+"' OR contactLoyaltyType='"+loyaltyEnrolledSource+"')";
				}
			}
			
			String modeQrySubString = "";
			if(!"ALL".equalsIgnoreCase(mode)){
				modeQrySubString = " AND mode = '" + mode + "'";
			}

			String qry = " FROM ContactsLoyalty WHERE userId=" +userID+" AND createdDate BETWEEN '"
					+startDate+"' AND '"+endDate+"'"+subQry + modeQrySubString;

			logger.info("qry ::"+qry);

			List<ContactsLoyalty> loyaltyContactsList = null;
			int strtIndex = 0;
			int size = 1000;

			ContactsLoyaltyDetails customerInfo = null;
			Customer customer = null;
			Contacts contact = null;
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);

			do {

				loyaltyContactsList = contactsLoyaltyDao.findLoyaltyContactsBy(qry, strtIndex, size );
				logger.info("got size::"+loyaltyContactsList.size());

				for (ContactsLoyalty contactsLoyalty : loyaltyContactsList) {
					contact = contactsLoyalty.getContact();
					// Return Created User Object.
					customerInfo = new ContactsLoyaltyDetails();
					customer = new Customer();

					customer.setCUSTOMERID(contactsLoyalty.getCustomerId() == null ? "" : contactsLoyalty.getCustomerId());

					if(contact != null) {
						customer.setCUSTOMERTYPE("");
						customer.setFIRSTNAME(contact.getFirstName() != null ? contact.getFirstName() : ""); 
						customer.setMIDDLENAME("");
						customer.setLASTNAME(contact.getLastName() != null ? contact.getLastName() : ""); 
						customer.setADDRESS1(contact.getAddressOne() != null ? contact.getAddressOne() : ""); 
						customer.setADDRESS2(contact.getAddressTwo() != null ? contact.getAddressTwo() : ""); 
						customer.setCITY(contact.getCity() != null ? contact.getState() : ""); 
						customer.setSTATE(contact.getState() != null ? contact.getState() : ""); 
						customer.setPOSTAL(contact.getZip() != null ? contact.getZip() : ""); 
						customer.setCOUNTRY(contact.getCountry() != null ? contact.getCountry() : ""); 
						customer.setMAILPREF(""); 
						customer.setPHONE(contact.getMobilePhone() != null ? contact.getMobilePhone() : ""); 
						customer.setISMOBILE(""); 
						customer.setPHONEPREF(""); 
						customer.setEMAIL(contact.getEmailId() != null ? contact.getEmailId() : ""); 
						customer.setEMAILPREF(""); 
						customer.setBIRTHDAY(contact.getBirthDay() != null ? MyCalendar.calendarToString(contact.getBirthDay(), MyCalendar.FORMAT_DATETIME_STYEAR) : ""); 
						customer.setANNIVERSARY(contact.getAnniversary() != null ? MyCalendar.calendarToString(contact.getAnniversary(), MyCalendar.FORMAT_DATETIME_STYEAR) : ""); 
						customer.setGENDER(contact.getGender() != null ? contact.getGender() : "");
					}
					customer.setCARDNUMBER(contactsLoyalty.getCardNumber() != null ? contactsLoyalty.getCardNumber().toString() : "");
					customer.setCARDTYPE(contactsLoyalty.getCardType() != null ? contactsLoyalty.getCardType() : "");
					customer.setCARDPIN(contactsLoyalty.getCardPin() != null ? contactsLoyalty.getCardPin() : "");
					customer.setTOTALLOYALTYEARNED(contactsLoyalty.getTotalLoyaltyEarned() != null ? contactsLoyalty.getTotalLoyaltyEarned().toString() : "");
					customer.setTOTALLOYALTYREDEMPTION(contactsLoyalty.getTotalLoyaltyRedemption() != null ? contactsLoyalty.getTotalLoyaltyRedemption().toString() : "");
					customer.setTOTALGIFTCARDAMOUNT(contactsLoyalty.getTotalGiftcardAmount() != null ? contactsLoyalty.getTotalGiftcardAmount().toString() : "");
					customer.setTOTALGIFTCARDREDEMPTION(contactsLoyalty.getTotalGiftcardRedemption() != null ? contactsLoyalty.getTotalGiftcardRedemption().toString() : "");
					customer.setGIFTCARDBALANCE(contactsLoyalty.getGiftcardBalance() != null ? contactsLoyalty.getGiftcardBalance().toString() : "");
					customer.setLOYALTYENROLLEDDATE(contactsLoyalty.getCreatedDate() != null ? MyCalendar.calendarToString(contactsLoyalty.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR) : "");
					customerInfo.setCUSTOMER(customer);
					customerInfoList.add(customerInfo); 
				}//for

				strtIndex += size;
				loyaltyContactsList.clear();
				
			}while(loyaltyContactsList.size() == size);
			//}while(strtIndex <= count);
			logger.debug("-------exit  getCustomerInfoList---------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception ::" ,e);
			throw new BaseServiceException("Exception occured while processing getCustomerInfoList::::: ", e);
		}

		return customerInfoList;
	}//getCustomerInfoList
	
	
	private LoyaltyHistoryResponseObject prepareFinalResponseObject(StoreLocationInfo storeLocationInfo,
			HeaderInfo headerInfo, List<ContactsLoyaltyDetails> customerInfoList, LoyaltyHistoryInfo loyaltyInfo, long count, boolean getOnlyCount,Status status) throws BaseServiceException {
		logger.debug("-------entered  prepareFinalResponseObject---------");
		
		LoyaltyHistoryResponseObject loyaltyHistoryResponseObject = new LoyaltyHistoryResponseObject();
		LoyaltyHistoryResponse loyaltyHistoryResponse = new LoyaltyHistoryResponse();

		loyaltyHistoryResponse.setSTORELOCATIONINFO(storeLocationInfo);
		
		loyaltyHistoryResponse.setHEADERINFO(headerInfo);
		
		if(!getOnlyCount) {
			loyaltyHistoryResponse.setCUSTOMERINFO(customerInfoList);
		}
		 
		LoyaltyHistoryBasicInfo loyaltyBasicInfo = new LoyaltyHistoryBasicInfo();
		if(loyaltyInfo != null) {
			loyaltyBasicInfo.setSOURCETYPE(loyaltyInfo.getSOURCETYPE() != null ? loyaltyInfo.getSOURCETYPE() : "");
			loyaltyBasicInfo.setTOTALCOUNT(count);
			loyaltyBasicInfo.setSTARTDATE(loyaltyInfo.getSTARTDATE() != null ? loyaltyInfo.getSTARTDATE() : "");
			loyaltyBasicInfo.setENDDATE(loyaltyInfo.getENDDATE() != null ? loyaltyInfo.getENDDATE() : "");
			loyaltyBasicInfo.setMODE(loyaltyInfo.getMODE() != null ? loyaltyInfo.getMODE() : "");
			loyaltyHistoryResponse.setLOYALTYBASICINFO(loyaltyBasicInfo);
		}
		else {
			loyaltyHistoryResponse.setLOYALTYBASICINFO(loyaltyBasicInfo);
		}
		
		loyaltyHistoryResponse.setSTATUS(status);
		loyaltyHistoryResponseObject.setLOYALTYDATARESPONSE(loyaltyHistoryResponse);
		
		logger.debug("-------exit  prepareFinalResponseObject---------");
		return loyaltyHistoryResponseObject;
	}//prepareFinalResponseObject

}