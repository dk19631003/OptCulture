package org.mq.optculture.business.loyalty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.ocloyalty.LoyaltyDataRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyDataResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyReport;
import org.mq.optculture.model.ocloyalty.LoyaltyReportResponse;
import org.mq.optculture.model.ocloyalty.MatchedCustomerReport;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;

public class LoyaltyDataOCServiceImpl implements LoyaltyDataOCService{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		BaseResponseObject baseResponseObject = new BaseResponseObject();
		baseResponseObject.setAction(baseRequestObject.getAction());
		LoyaltyDataResponse loyaltyDataResponse = null;

		try {
			logger.debug("-------entered processRequest---------");
			Gson gson = new Gson();
			LoyaltyDataRequest loyaltyDataRequest = null;
			Status status = null;
			
			try {
				loyaltyDataRequest = gson.fromJson(baseRequestObject.getJsonValue(), LoyaltyDataRequest.class);
			} catch (Exception e) {
				status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				loyaltyDataResponse = new LoyaltyDataResponse();
				loyaltyDataResponse.setStatus(status);
				String responseJson = gson.toJson(loyaltyDataResponse);
				baseResponseObject.setJsonValue(responseJson);
				return baseResponseObject;
			}
			
			LoyaltyDataOCService loyaltyDataOCService = (LoyaltyDataOCService) ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_DATA_OC_SERVICE_IMPL);
			loyaltyDataResponse = (LoyaltyDataResponse) loyaltyDataOCService.processLoyaltyDataRequest(loyaltyDataRequest, 
					baseRequestObject.getTransactionId(), baseRequestObject.getTransactionDate());
			
			String json = gson.toJson(loyaltyDataResponse);
			baseResponseObject.setJsonValue(json);
			logger.debug("-------exit  processRequest---------");
		} catch (Exception e) {
			logger.error("Exception ::" ,e);
			throw new BaseServiceException("Exception occured while processing processRequest::::: ", e);
		}

		return baseResponseObject;
	}//processRequest

	@Override
	public LoyaltyDataResponse processLoyaltyDataRequest(LoyaltyDataRequest loyaltyDataRequest, String transactionId, String transactionDate)
					throws BaseServiceException {
		LoyaltyDataResponse loyaltyDataResponse = null;
		Status status = null;
		
		ResponseHeader responseHeader = new ResponseHeader();
		responseHeader.setRequestDate(loyaltyDataRequest.getHeader().getRequestDate());
		responseHeader.setRequestId(loyaltyDataRequest.getHeader().getRequestId());
		responseHeader.setTransactionDate(transactionDate);
		responseHeader.setTransactionId(transactionId);
		
		try {
			logger.debug("-------entered  processLoyaltyDataRequest---------");
			status = validateRootObjects(loyaltyDataRequest);
			if(status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())){
				loyaltyDataResponse = prepareFinalResponseObject(responseHeader, null, null, status);
				return loyaltyDataResponse;
			}
			status = validateLoyaltyDataRequestObjects(loyaltyDataRequest);
			if(status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())){
				loyaltyDataResponse = prepareFinalResponseObject(responseHeader, null, null, status);
				return loyaltyDataResponse;
			}

			status = validateInnerObjects(loyaltyDataRequest.getHeader().getStoreNumber(), loyaltyDataRequest.getUser().getToken(), 
					loyaltyDataRequest.getUser().getUserName(), loyaltyDataRequest.getUser().getOrganizationId());
			if(status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())){
				loyaltyDataResponse = prepareFinalResponseObject(responseHeader, null, null, status);
				return loyaltyDataResponse;
			}
			
			String userName = loyaltyDataRequest.getUser().getUserName();
			String orgId = loyaltyDataRequest.getUser().getOrganizationId();
			String token = loyaltyDataRequest.getUser().getToken();

			String fullUserName = userName+ Constants.USER_AND_ORG_SEPARATOR +orgId;
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = usersDao.findUserByToken(fullUserName,token);

			status= validateUser(user);
			if(status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())){
				loyaltyDataResponse = prepareFinalResponseObject(responseHeader, null, null, status);
				return loyaltyDataResponse;
			}

			MailingListDao mailingListDao = (MailingListDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
			MailingList mlList = mailingListDao.findPOSMailingList(user);

			status= validateMlList(mlList);
			if(status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())){
				loyaltyDataResponse = prepareFinalResponseObject(responseHeader, null, null, status);
				return loyaltyDataResponse;
			}

			String type = loyaltyDataRequest.getReport().getType();
			
			status = validateReportData(loyaltyDataRequest);
			if(status != null){
				loyaltyDataResponse = prepareFinalResponseObject(responseHeader, null, null, status);
				return loyaltyDataResponse;
			}
			
			/*String startDate = loyaltyDataRequest.getReport().getStartDate();
			String endDate = loyaltyDataRequest.getReport().getEndDate();
			status= validateDates(startDate,endDate);
			if(status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())){
				loyaltyDataResponse = prepareFinalResponseObject(responseHeader, null, null, status);
				return loyaltyDataResponse;
			}*/
			
			if(OCConstants.LOYALTY_ENROLLHISTORY_TYPE_DETAILED.equalsIgnoreCase(type)){
				loyaltyDataResponse = prepareDetailedData(loyaltyDataRequest.getReport(), user.getUserId(), responseHeader);
			}
			else if(OCConstants.LOYALTY_ENROLLHISTORY_TYPE_SUMMARY.equalsIgnoreCase(type)){
				loyaltyDataResponse = prepareSummaryData(loyaltyDataRequest.getReport(), user.getUserId(), responseHeader);
			}
			else{
				logger.info("INVALID CONDITION...");
				
			}
			
			logger.debug("-------exit  processLoyaltyDataRequest---------");
		} catch (Exception e) {
			status = new Status("101000",PropertyUtil.getErrorMessage(101000,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			loyaltyDataResponse = prepareFinalResponseObject(responseHeader, null, null, status);
			logger.error("Exception ::" ,e);
			throw new BaseServiceException("Exception occured while processing processLoyaltyDataRequest::::: ", e);
		}
		return loyaltyDataResponse;
	}//processLoyaltyDataRequest

	private Status validateRootObjects(	LoyaltyDataRequest loyaltyDataRequest) throws BaseServiceException {
		logger.debug("-------entered  validateRootObjects---------");
		Status status = null;
		if(loyaltyDataRequest == null){
			logger.info("Error : Invalid json Object .. Returning. ****");
			status = new Status("101002",PropertyUtil.getErrorMessage(101002,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			
		}
		logger.debug("-------exit  validateRootObjects---------");
		return status;
	}

	private Status validateLoyaltyDataRequestObjects(LoyaltyDataRequest loyaltyDataRequest) throws BaseServiceException {
		logger.debug("-------entered  validateLoyaltyDataRequestObjects---------");
		Status status = null;
		if(loyaltyDataRequest.getHeader() == null) {
			logger.info("Error : unable to find the HearderInfo in JSON ****");
			status = new Status("1004",PropertyUtil.getErrorMessage(1004,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(loyaltyDataRequest.getReport() == null) {
			logger.info("Error : unable to find the  required Loyalty details in JSON ****");
			status = new Status("1009",PropertyUtil.getErrorMessage(1009,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(loyaltyDataRequest.getUser() == null) {
			logger.info("Error : unable to find the User Details in JSON ****");
			status = new Status("101011",PropertyUtil.getErrorMessage(101011,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		logger.debug("-------exit  validateLoyaltyDataRequestObjects---------");
		return status;
	}//validateLoyaltyDataRequestObjects

	private Status validateInnerObjects(String storeLocationId, String token, String userName, String orgId) throws BaseServiceException {
		logger.debug("-------entered  validateInnerObjects---------");
		Status status = null;
		/*if(requestId == null || requestId.trim().length() == 0) {
			logger.info("Error : Request ID of HearderInfo is empty in JSON ****");
			status = new Status("1010",PropertyUtil.getErrorMessage(1010,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}*/
		if(storeLocationId == null || storeLocationId.trim().length() == 0) {
			logger.info("Error : find the  spark based StoreLocationID as empty in JSON ****");
			status = new Status("111501",PropertyUtil.getErrorMessage(111501,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(userName == null || userName.trim().length() <=0 || 
				orgId == null || orgId.trim().length() <=0 || 
						token == null || token.trim().length() <=0) {
			status = new Status("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		/*if(token == null || token.trim().length() == 0) {
			logger.info("Error : User Token cannot be empty.");
			status = new Status("1012",PropertyUtil.getErrorMessage(1012,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(userName == null || userName.trim().length() == 0 || orgId == null || orgId.trim().length() == 0) {
			logger.info("Error : Username or organisation cannot be empty.");
			status = new Status("1013",PropertyUtil.getErrorMessage(1013,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}*/
		logger.debug("-------exit  validateInnerObjects---------");
		return status;
	}//validateInnerObjects

	private Status validateUser(Users user) throws BaseServiceException {
		logger.debug("-------entered  validateUser---------");
		Status status = null;
		if(user == null){
			logger.info("Unable to find the user Obj");
			status = new Status("101013",PropertyUtil.getErrorMessage(101013,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(!user.isEnabled()){
			status = new Status("111558", PropertyUtil.getErrorMessage(111558, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(user.getPackageExpiryDate().before(Calendar.getInstance())){
			status = new Status("111559", PropertyUtil.getErrorMessage(111559, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		logger.debug("-------exit  validateUser---------");
		return status;
	}//validateUser

	private Status validateMlList(MailingList mlList) throws BaseServiceException {
		logger.debug("-------entered  validateSparkBaseLocAndMlList---------");
		Status status = null;
		if(mlList == null) {
			logger.info("Unable to find the user POS ml List");
			status = new Status("101007",PropertyUtil.getErrorMessage(101007,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		logger.debug("-------exit  validateSparkBaseLocAndMlList---------");
		return status;
	}//validateSparkBaseLocAndMlList

	/*private Status validateDates(String startDate,String endDate) throws BaseServiceException {
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
*/	
	private LoyaltyDataResponse prepareFinalResponseObject(ResponseHeader responseHeader, 
			List<MatchedCustomerReport> customerReport, LoyaltyReportResponse reportResponse, 
			Status status) throws BaseServiceException {
		
		logger.debug("-------entered  prepareFinalResponseObject---------");
		
		LoyaltyDataResponse loyaltyDataResponse = new LoyaltyDataResponse();
		loyaltyDataResponse.setHeader(responseHeader);
		
		if(customerReport == null){
			List<MatchedCustomerReport> custReport = new ArrayList<MatchedCustomerReport>();
			loyaltyDataResponse.setMatchedCustomers(custReport);
		}
		else{
			loyaltyDataResponse.setMatchedCustomers(customerReport);
		}
		
		if(reportResponse == null){
			LoyaltyReportResponse reportRes = new LoyaltyReportResponse();
			loyaltyDataResponse.setReport(reportRes);
		}
		else{
			loyaltyDataResponse.setReport(reportResponse);
		}
		
		loyaltyDataResponse.setStatus(status);
		
		logger.debug("-------exit  prepareFinalResponseObject---------");
		return loyaltyDataResponse;
	}//prepareFinalResponseObject

	private Status validateReportData(LoyaltyDataRequest loyaltyDataRequest) throws Exception {
		
		String source = loyaltyDataRequest.getReport().getSource();
		logger.info("source = "+source);
		if(!OCConstants.LOYALTY_ENROLLHISTORY_SOURCE_ALL.equalsIgnoreCase(source) &&
				!OCConstants.LOYALTY_ENROLLHISTORY_SOURCE_STORE.equalsIgnoreCase(source) &&
				!OCConstants.LOYALTY_ENROLLHISTORY_SOURCE_WEBFORM.equalsIgnoreCase(source)){
			return new Status("111542",PropertyUtil.getErrorMessage(111542,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		}
		
		String type = loyaltyDataRequest.getReport().getType();
		if(!OCConstants.LOYALTY_ENROLLHISTORY_TYPE_DETAILED.equalsIgnoreCase(type) &&
				!OCConstants.LOYALTY_ENROLLHISTORY_TYPE_SUMMARY.equalsIgnoreCase(type)){
			return new Status("111543",PropertyUtil.getErrorMessage(111543,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		}
		
		String mode = loyaltyDataRequest.getReport().getMode();
		if(!OCConstants.LOYALTY_ENROLLHISTORY_MODE_ALL.equalsIgnoreCase(mode) &&
				!OCConstants.LOYALTY_ENROLLHISTORY_MODE_Offline.equalsIgnoreCase(mode) &&
				!OCConstants.LOYALTY_ENROLLHISTORY_MODE_Online.equalsIgnoreCase(mode)){
			return new Status("111544",PropertyUtil.getErrorMessage(111544,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		}
		
		String serviceType = loyaltyDataRequest.getReport().getServiceType();
		if(!OCConstants.LOYALTY_ENROLLHISTORY_SERVICETYPE_ALL.equalsIgnoreCase(serviceType) &&
				!OCConstants.LOYALTY_ENROLLHISTORY_SERVICETYPE_OC.equalsIgnoreCase(serviceType) &&
				!OCConstants.LOYALTY_ENROLLHISTORY_SERVICETYPE_SB.equalsIgnoreCase(serviceType)){
			return new Status("111545",PropertyUtil.getErrorMessage(111545,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		}
		
		if(loyaltyDataRequest.getReport().getStartDate() == null && 
				loyaltyDataRequest.getReport().getStartDate().trim().isEmpty()){
			return new Status("1015",PropertyUtil.getErrorMessage(1015,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		}
		
		if(loyaltyDataRequest.getReport().getEndDate() == null &&
				loyaltyDataRequest.getReport().getStartDate().trim().isEmpty()){
			return new Status("1016",PropertyUtil.getErrorMessage(1016,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		}
		
		String startDate = loyaltyDataRequest.getReport().getStartDate();
		String endDate = loyaltyDataRequest.getReport().getEndDate();
		String regexMMDDYYYY="((19|20)[0-9]{2})-((0?[1-9])|1[012])-((0?[1-9])|(1[0-9])|(2[0-9])|(3[01]))\\s((0?[0-9])|1[0-9]|2[0-4]):([0-5][0-9]):([0-5][0-9])";
		if(!Pattern.matches(regexMMDDYYYY, startDate)) {
			return new Status("1018",PropertyUtil.getErrorMessage(1018, OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		}

		if(!Pattern.matches(regexMMDDYYYY, endDate)) {
			return new Status("1019",PropertyUtil.getErrorMessage(1019, OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		}

		SimpleDateFormat sdf = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
		Calendar startDateCal = MyCalendar.string2Calendar(loyaltyDataRequest.getReport().getStartDate());
		Calendar endDateCal = MyCalendar.string2Calendar(loyaltyDataRequest.getReport().getEndDate());
		if(!sdf.format(startDateCal.getTime()).equals(sdf.format(endDateCal.getTime()))){
			if(endDateCal.before(startDateCal)) {
				return new Status("1017",PropertyUtil.getErrorMessage(1017,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}
		}

		return null;
		
	}
	
	private LoyaltyDataResponse prepareDetailedData(LoyaltyReport loyaltyReport, Long userId, 
			ResponseHeader responseHeader) throws Exception {
		LoyaltyDataResponse response = null;
		Status status = null;
		
		String sourceQry = null;
		if(OCConstants.LOYALTY_ENROLLHISTORY_SOURCE_STORE.equalsIgnoreCase(loyaltyReport.getSource())){
			sourceQry = " cl.contact_loyalty_type = '"+Constants.CONTACT_LOYALTY_TYPE_POS+"'";
		}
		else if(OCConstants.LOYALTY_ENROLLHISTORY_SOURCE_WEBFORM.equalsIgnoreCase(loyaltyReport.getSource())){
			sourceQry = " cl.contact_loyalty_type = '"+Constants.CONTACT_LOYALTY_TYPE_WEBFORM+"'";
		}
		
		String serviceTypeQry = null;
		if(OCConstants.LOYALTY_ENROLLHISTORY_SERVICETYPE_OC.equalsIgnoreCase(loyaltyReport.getServiceType())){
			serviceTypeQry = " cl.service_type = '"+OCConstants.LOYALTY_ENROLLHISTORY_SERVICETYPE_OC+"'";
		}
		else if(OCConstants.LOYALTY_ENROLLHISTORY_SERVICETYPE_SB.equalsIgnoreCase(loyaltyReport.getServiceType())){
			serviceTypeQry = " (cl.service_type IS NULL OR cl.service_type != '"+OCConstants.LOYALTY_ENROLLHISTORY_SERVICETYPE_OC+"')";
		}
		
		String modeQry = null;
		if(OCConstants.LOYALTY_ENROLLHISTORY_MODE_Online.equalsIgnoreCase(loyaltyReport.getMode().trim())){
			modeQry = " cl.mode = '"+OCConstants.LOYALTY_ONLINE_MODE+"'";
		}
		else if(OCConstants.LOYALTY_ENROLLHISTORY_MODE_Offline.equalsIgnoreCase(loyaltyReport.getMode().trim())){
			modeQry = " cl.mode = '"+OCConstants.LOYALTY_OFFLINE_MODE+"'";
		}
		
		String storeQry = null;
		if(loyaltyReport.getStore() != null && !loyaltyReport.getStore().trim().isEmpty() 
				&& !OCConstants.LOYALTY_ENROLLHISTORY_STORE_ALL.equalsIgnoreCase(loyaltyReport.getStore())){
			storeQry = " cl.pos_location_id = '"+loyaltyReport.getStore().trim()+"'";
		}
		
		String startDate = loyaltyReport.getStartDate().trim();
		String endDate = loyaltyReport.getEndDate().trim();
		String dateQry = " cl.created_date BETWEEN '"+startDate+"' AND '"+endDate+"'";
		String orderbyQry = " order by cl.loyalty_id asc";
		
		int offset = 0;
		int maxRecords = 0;
		if(loyaltyReport.getOffset() != null && !loyaltyReport.getOffset().trim().isEmpty() &&
				loyaltyReport.getMaxRecords() != null && !loyaltyReport.getMaxRecords().trim().isEmpty()){
			try{
				Integer offset1 = Integer.parseInt(loyaltyReport.getOffset().trim());
				Integer maxRecords1 = Integer.parseInt(loyaltyReport.getMaxRecords().trim());
				offset = offset1.intValue();
				maxRecords = maxRecords1.intValue();
			}catch(Exception e){
				status = new Status("111546",PropertyUtil.getErrorMessage(111546,OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return prepareFinalResponseObject(responseHeader, null, null, status);
			}
		
		}
		
		String selectlist = "SELECT cl.card_number, cl.card_pin, cl.created_date, c.external_id, c.mobile_phone, "
				+ "c.email_id, c.first_name, c.last_name";
		
		String query = null;
		if(sourceQry == null && serviceTypeQry != null){
			query = selectlist+" FROM contacts_loyalty cl left join contacts c on cl.contact_id = c.cid "
					+ "WHERE cl.user_id ="+userId+
					" AND (cl.reward_flag = '"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+"' OR cl.reward_flag = '"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"')"+
					" AND "+dateQry+" AND "+serviceTypeQry;
			
		}
		else if(serviceTypeQry == null && sourceQry != null){
			query = selectlist+" FROM contacts_loyalty cl left join contacts c on cl.contact_id = c.cid "
					+ "WHERE cl.user_id ="+userId+
					" AND (cl.reward_flag = '"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+"' OR cl.reward_flag = '"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"')"+
					" AND "+dateQry+" AND "+sourceQry;
		}
		else if(sourceQry == null && serviceTypeQry == null){
			query = selectlist+" FROM contacts_loyalty cl left join contacts c on cl.contact_id = c.cid "
					+ "WHERE cl.user_id ="+userId+
					" AND (cl.reward_flag = '"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+"' OR cl.reward_flag = '"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"')"+
					" AND "+dateQry;
		}
		else{
			query = selectlist+" FROM contacts_loyalty cl left join contacts c on cl.contact_id = c.cid "
					+ "WHERE cl.user_id ="+userId+
					" AND (cl.reward_flag = '"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+"' OR cl.reward_flag = '"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"')"+
					" AND "+dateQry+
					" AND "+serviceTypeQry+" AND "+sourceQry;
		}
		
		if(modeQry != null){
			query = query+" AND "+modeQry;
		}
		
		if(storeQry != null){
			query = query+" AND "+storeQry;
		}
		
		query = query+orderbyQry;
		logger.info("Detailed query = "+query);
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		
		List<Object[]> data = null;
		if(offset != 0 && maxRecords != 0){
			 data =  loyaltyDao.fetchEnrolledHistoryData(query, offset, maxRecords);
		}
		else{
			data =  loyaltyDao.fetchEnrolledHistoryData(query);
		}
		
		List<MatchedCustomerReport> matchedCustomerlist = matchedCustomerReport(data);
		
		LoyaltyReportResponse reportResponse = new LoyaltyReportResponse();
		long count = 0;
		if(data == null || data.size() == 0){
			count = 0;
		}
		else{
			count = data.size();
		}
		reportResponse.setTotalCount(""+count);
		
		status = new Status("0","Number of contacts fetched successfully ::"+count, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		response = prepareFinalResponseObject(responseHeader, matchedCustomerlist, reportResponse, status);
		return response;
		
	}
	
	private LoyaltyDataResponse prepareSummaryData(LoyaltyReport loyaltyReport, Long userId, 
			ResponseHeader responseHeader) throws Exception {
		LoyaltyDataResponse response = null;
		
		String sourceQry = null;
		if(OCConstants.LOYALTY_ENROLLHISTORY_SOURCE_STORE.equalsIgnoreCase(loyaltyReport.getSource())){
			sourceQry = " contactLoyaltyType = '"+Constants.CONTACT_LOYALTY_TYPE_POS+"'";
		}
		else if(OCConstants.LOYALTY_ENROLLHISTORY_SOURCE_WEBFORM.equalsIgnoreCase(loyaltyReport.getSource())){
			sourceQry = " contactLoyaltyType = '"+Constants.CONTACT_LOYALTY_TYPE_WEBFORM+"'";
		}
		
		String serviceTypeQry = null;
		if(OCConstants.LOYALTY_ENROLLHISTORY_SERVICETYPE_OC.equalsIgnoreCase(loyaltyReport.getServiceType())){
			serviceTypeQry = " serviceType = '"+OCConstants.LOYALTY_ENROLLHISTORY_SERVICETYPE_OC+"'";
		}
		else if(OCConstants.LOYALTY_ENROLLHISTORY_SERVICETYPE_SB.equalsIgnoreCase(loyaltyReport.getServiceType())){
			serviceTypeQry = " (serviceType IS NULL OR serviceType != '"+OCConstants.LOYALTY_ENROLLHISTORY_SERVICETYPE_OC+"')";
		}
		
		String modeQry = null;
		if(OCConstants.LOYALTY_ENROLLHISTORY_MODE_Online.equalsIgnoreCase(loyaltyReport.getMode().trim())){
			modeQry = " mode = '"+OCConstants.LOYALTY_ONLINE_MODE+"'";
		}
		else if(OCConstants.LOYALTY_ENROLLHISTORY_MODE_Offline.equalsIgnoreCase(loyaltyReport.getMode().trim())){
			modeQry = " mode = '"+OCConstants.LOYALTY_OFFLINE_MODE+"'";
		}
		
		String storeQry = null;
		if(loyaltyReport.getStore() != null && !loyaltyReport.getStore().trim().isEmpty() 
				&& !OCConstants.LOYALTY_ENROLLHISTORY_STORE_ALL.equalsIgnoreCase(loyaltyReport.getStore())){
			storeQry = " posStoreLocationId = '"+loyaltyReport.getStore().trim()+"'";
		}
		
		String startDate = loyaltyReport.getStartDate().trim();
		String endDate = loyaltyReport.getEndDate().trim();
		String dateQry = " createdDate BETWEEN '"+startDate+"' AND '"+endDate+"'";
		String query = null;
		
		if(sourceQry == null && serviceTypeQry != null){
			query = " SELECT COUNT(loyaltyId) FROM ContactsLoyalty WHERE userId = "+userId+
					" AND (rewardFlag = '"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+"' OR rewardFlag = '"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"')"+
					" AND "+dateQry+
					" AND "+serviceTypeQry;
		}
		else if(serviceTypeQry == null && sourceQry != null){
			query = " SELECT COUNT(loyaltyId) FROM ContactsLoyalty WHERE userId = "+userId+
					" AND (rewardFlag = '"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+"' OR rewardFlag = '"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"')"+
					" AND "+dateQry+
					" AND "+sourceQry;
		}
		else if(sourceQry == null && serviceTypeQry == null){
			query = " SELECT COUNT(loyaltyId) FROM ContactsLoyalty WHERE userId = "+userId+
					" AND (rewardFlag = '"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+"' OR rewardFlag = '"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"')"+
					" AND "+dateQry;
		}
		else{
			query = " SELECT COUNT(loyaltyId) FROM ContactsLoyalty WHERE userId = "+userId+
					" AND (rewardFlag = '"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L+"' OR rewardFlag = '"+OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+"')"+
					" AND "+dateQry+
					" AND "+serviceTypeQry+" AND "+sourceQry;
		}
		
		if(modeQry != null){
			query = query+" AND "+modeQry;
		}
		if(storeQry != null){
			query = query+" AND "+storeQry;
		}
		
		logger.info("Summary query = "+query);
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		
		LoyaltyReportResponse reportResponse = new LoyaltyReportResponse();
		
		Long summaryCount = loyaltyDao.findEnrolledCount(query);
		long count = 0;
		if(summaryCount == null || summaryCount.longValue() == 0){
			count = 0;
		}
		else{
			count = summaryCount.longValue();
		}
		reportResponse.setTotalCount(""+count);
		
		Status status = new Status("0","Number of contacts fetched successfully ::"+count, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		response = prepareFinalResponseObject(responseHeader, null, reportResponse, status);
		return response;
	}
	
	private List<MatchedCustomerReport> matchedCustomerReport(List<Object[]> data){
		
		if(data == null || data.size() == 0){
			return null;
		}
		
		List<MatchedCustomerReport> customerReportList = new ArrayList<MatchedCustomerReport>();
		MatchedCustomerReport matchedCustomer = null;
		for(Object[] customer : data){
			matchedCustomer = new MatchedCustomerReport();
			
			matchedCustomer.setMembershipNumber(customer[0] == null ? "" : ""+customer[0].toString());
			matchedCustomer.setCardPin(customer[1] == null ? "" : ""+customer[1].toString());
			
			//format enrolled date
			SimpleDateFormat formatter = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
			Date date = null;
			try {
				date = (Date)formatter.parse(customer[2].toString().trim());
			} catch (ParseException e) {
				logger.error("Exception ::" , e);
			} 
			Calendar cal=Calendar.getInstance();
			cal.setTime(date);
			String enrolledDate = "";
			enrolledDate = MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STYEAR);
			matchedCustomer.setEnrolledDate(enrolledDate);
			
			matchedCustomer.setCustomerId(customer[3] == null ? "" : ""+customer[3].toString());
			matchedCustomer.setPhone(customer[4] == null ? "" : ""+customer[4].toString());
			matchedCustomer.setEmailAddress(customer[5] == null ? "" : ""+customer[5].toString());
			matchedCustomer.setFirstName(customer[6] == null ? "" : ""+customer[6].toString());
			matchedCustomer.setLastName(customer[7] == null ? "" : ""+customer[7].toString());
			customerReportList.add(matchedCustomer);
		}
		return customerReportList;
	}
	
	
}
