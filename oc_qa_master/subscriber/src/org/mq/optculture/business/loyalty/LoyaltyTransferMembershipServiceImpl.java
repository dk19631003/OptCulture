package org.mq.optculture.business.loyalty;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyCards;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramExclusion;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltySettings;
import org.mq.marketer.campaign.beans.LoyaltyThresholdBonus;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.LoyaltyTransactionExpiry;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.data.dao.LoyaltyAutoCommDao;
import org.mq.optculture.data.dao.LoyaltyCardSetDao;
import org.mq.optculture.data.dao.LoyaltyCardsDao;
import org.mq.optculture.data.dao.LoyaltyCardsDaoForDML;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramExclusionDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltySettingsDao;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDaoForDML;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.model.ocloyalty.HoldBalance;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyTransferMembershipJsonRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyTransferMembershipJsonResponse;
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.ocloyalty.MembershipResponse;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;
/**
 * loyalty transfer request processes here
 * @author proumyaa
 *
 */
public class LoyaltyTransferMembershipServiceImpl implements LoyaltyTransferMembershipService{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {

		logger.info("Started processRequest...");
		
		BaseResponseObject responseObject = null;
		String serviceRequest = baseRequestObject.getAction();
		String requestJson = baseRequestObject.getJsonValue();
		String responseJson = null;
		Gson gson = new Gson();
		LoyaltyTransferMembershipJsonResponse transferResponse = null;
		LoyaltyTransferMembershipJsonRequest transferRequest = null;
		
		if(serviceRequest == null || !serviceRequest.equals(OCConstants.LOYALTY_SERVICE_ACTION_TRANSFER)){
			logger.info("servicerequest is null...");
			
			Status status = new Status("101001", Constants.STRING_NILL+PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			
			transferResponse = new LoyaltyTransferMembershipJsonResponse();
			transferResponse.setStatus(status);
			responseJson = gson.toJson(transferResponse);
			
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			logger.info("Exited baserequest due to invalid service");
			return responseObject;
		}
		
		try{
			transferRequest = gson.fromJson(requestJson, LoyaltyTransferMembershipJsonRequest.class);
		}catch(Exception e){
			
			Status status = new Status("101001", Constants.STRING_NILL+PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			
			transferResponse = new LoyaltyTransferMembershipJsonResponse();
			transferResponse.setStatus(status);
			responseJson = gson.toJson(transferResponse);
			
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			logger.info("Exited baserequest due to invalid JSON ", e);
			return responseObject;
		}
		
		try{
			LoyaltyTransferMembershipService loyaltyTransferService = (LoyaltyTransferMembershipService) ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_TRANSFER_MEMBERSHIP_BUSINESS_SERVICE);
			transferResponse = loyaltyTransferService.processTransferRequest(transferRequest, OCConstants.LOYALTY_ONLINE_MODE, baseRequestObject.getTransactionId(), baseRequestObject.getTransactionDate());
			responseJson = gson.toJson(transferResponse);
			
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
	
	
	@Override
	public LoyaltyTransferMembershipJsonResponse processTransferRequest(
			LoyaltyTransferMembershipJsonRequest loyaltyTransferRequest,
			String mode, String transactionId, String transactionDate)
			throws BaseServiceException {
		
		try {
			logger.info("===started processing transfer request===");
			LoyaltyTransferMembershipJsonResponse transferResponse = null;
			Status status = null;
			Users user = null;
			
			ResponseHeader responseHeader = new ResponseHeader();
			responseHeader.setRequestDate(loyaltyTransferRequest.getHeader().getRequestDate());
			responseHeader.setRequestId(loyaltyTransferRequest.getHeader().getRequestId());
			responseHeader.setTransactionDate(transactionDate);
			responseHeader.setTransactionId(transactionId);
			responseHeader.setSourceType(loyaltyTransferRequest.getHeader().getSourceType() != null && 
					!loyaltyTransferRequest.getHeader().getSourceType().trim().isEmpty() ? loyaltyTransferRequest.getHeader().getSourceType().trim() : Constants.STRING_NILL);
			/*responseHeader.setSubsidiaryNumber(loyaltyTransferRequest.getHeader().getSubsidiaryNumber() != null && !loyaltyTransferRequest.getHeader().getSubsidiaryNumber().trim().isEmpty() ? loyaltyTransferRequest.getHeader().getSubsidiaryNumber().trim() : Constants.STRING_NILL);
			responseHeader.setReceiptNumber( Constants.STRING_NILL);
			responseHeader.setRecprocessTransferRequesteiptAmount(Constants.STRING_NILL);*/
			
			status = validateTransferJsonData(loyaltyTransferRequest);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				transferResponse = prepareTransferResponse(responseHeader, null, null,null, null, status);
				return transferResponse;
			}
			user = getUser(loyaltyTransferRequest.getUser().getUserName(), loyaltyTransferRequest.getUser().getOrganizationId(),
					loyaltyTransferRequest.getUser().getToken());
			if(user == null){
				status = new Status("101013", PropertyUtil.getErrorMessage(101013, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
			}
			if(!user.isEnabled()){
				status = new Status("111558", PropertyUtil.getErrorMessage(111558, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
			}
			if(user.getPackageExpiryDate().before(Calendar.getInstance())){
				status = new Status("111559", PropertyUtil.getErrorMessage(111559, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
			} 
			
			
			String sourceCard = loyaltyTransferRequest.getTransferSource().getCardNumber().trim();
			String destCard = loyaltyTransferRequest.getMembership().getCardNumber();
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO); 
			UserOrganization userOrg = usersDao.findByOrgId(user.getUserOrganization().getUserOrgId());
			//parse the card
			String sourceCardLong = null;
			String destCardLong = null;
			String sourceCardNumber = Constants.STRING_NILL;
			String destCardNumber = Constants.STRING_NILL;
			
			sourceCardLong = OptCultureUtils.validateOCLtyCardNumber(sourceCard);
			if(sourceCardLong == null){
				String msg = PropertyUtil.getErrorMessage(100107, OCConstants.ERROR_LOYALTY_FLAG)+" "+sourceCard+".";
				status = new Status("100107", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
			}
			sourceCardNumber = Constants.STRING_NILL+sourceCardLong;
			logger.info("CARD NUMBER After parsing :"+sourceCardNumber);
			loyaltyTransferRequest.getTransferSource().setCardNumber(sourceCardNumber);
			
			destCardLong = OptCultureUtils.validateOCLtyCardNumber(destCard);
			if(destCardLong == null){
				String msg = PropertyUtil.getErrorMessage(100107, OCConstants.ERROR_LOYALTY_FLAG)+" "+destCard+".";
				status = new Status("100107", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
			}
			destCardNumber = Constants.STRING_NILL+destCardLong;
			logger.info("CARD NUMBER After parsing :"+destCardNumber);
			loyaltyTransferRequest.getMembership().setCardNumber(destCardNumber);
			
			// added to restrict transfer between two same card numbers
			if(sourceCardNumber.equals(destCardNumber)){
				String msg = PropertyUtil.getErrorMessage(111586, OCConstants.ERROR_LOYALTY_FLAG);
				status = new Status("111586", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
			}
			
			LoyaltyCards sourceLtyCard = findCard(sourceCardNumber, user); 
			if(sourceLtyCard == null){
				String msg = PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+sourceCardNumber+".";
				status = new Status("111505", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
		
			}
			LoyaltyCards destLtyCard = findCard(destCardNumber, user);
			if(destLtyCard == null){
				String msg = PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+destCardNumber+".";
				status = new Status("111505", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
		
			}
			String cardstatus = destLtyCard.getStatus();
			//need to think
			if(destLtyCard.getStatus().equals(OCConstants.LOYALTY_CARD_STATUS_ACTIVATED)) {
				
				String msg = PropertyUtil.getErrorMessage(111575, OCConstants.ERROR_LOYALTY_FLAG)+" "+destCardNumber+".";
				status = new Status("111575", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
				
			}
			if(!sourceLtyCard.getStatus().equals(OCConstants.LOYALTY_CARD_STATUS_ENROLLED)){
				
				String msg = PropertyUtil.getErrorMessage(111576, OCConstants.ERROR_LOYALTY_FLAG)+" "+sourceCardNumber+".";
				status = new Status("111576", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
			
			}
		  /*if(sourceLtyCard.getProgramId().longValue() != destLtyCard.getProgramId().longValue()){
				String msg = PropertyUtil.getErrorMessage(111573, OCConstants.ERROR_LOYALTY_FLAG)+" "+sourceCardNumber+".";
				status = new Status("111573", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
			}*/
			//condition included for cross program card transfer
			if(!userOrg.isCrossProgramCardTransfer() && sourceLtyCard.getProgramId().longValue() != destLtyCard.getProgramId().longValue()){
				String msg = PropertyUtil.getErrorMessage(111573, OCConstants.ERROR_LOYALTY_FLAG)+" "+sourceCardNumber+".";
				status = new Status("111573", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
			}
				
			
			LoyaltyCardSet sourceCardSet = findCardSet(sourceLtyCard.getCardSetId());
			if(!sourceCardSet.getStatus().equals(OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE)){
				
				String msg = PropertyUtil.getErrorMessage(111574, OCConstants.ERROR_LOYALTY_FLAG)+" "+sourceCardNumber+".";
				status = new Status("111574", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
				
				
			}
			
			//find the program & cardset is it active or not????
			LoyaltyCardSet destCardSet = findCardSet(destLtyCard.getCardSetId());
			if(!destCardSet.getStatus().equals(OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE)){
				
				String msg = PropertyUtil.getErrorMessage(111574, OCConstants.ERROR_LOYALTY_FLAG)+" "+destCardNumber+".";
				status = new Status("111574", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
				
				
			}
			
			LoyaltyProgram loyaltyProgram = findProgram(destLtyCard.getProgramId());
			//finding source program from source card for cross_program_card_transfer
			LoyaltyProgram sourceLoyaltyProgram = findProgram(sourceLtyCard.getProgramId());
			//check for store exclusion
			LoyaltyProgramExclusion loyaltyExclusion = getLoyaltyExclusion(loyaltyProgram.getProgramId());
			if(loyaltyExclusion != null){
				status = validateStoreNumberExclusion(loyaltyTransferRequest, loyaltyProgram, loyaltyExclusion);
				if(status != null){
					transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
					return transferResponse;
					
				}
			}
			if(!userOrg.isSuspendedProgramTransfer() && !sourceLoyaltyProgram.getStatus().equals(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)){
				String msg = PropertyUtil.getErrorMessage(111573, OCConstants.ERROR_LOYALTY_FLAG)+" "+sourceCardNumber+".";
				status = new Status("111573", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
			}
			if(!userOrg.isSuspendedProgramTransfer() && !loyaltyProgram.getStatus().equals(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)){
				String msg = PropertyUtil.getErrorMessage(111573, OCConstants.ERROR_LOYALTY_FLAG)+" "+sourceCardNumber+".";
				status = new Status("111573", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
			}
			LoyaltyProgramTier dummyHighestTier = null;
			//find source & dest membership
			//ContactsLoyalty sourceMembership = findMembershpByCard(sourceCardLong, loyaltyProgram.getProgramId(), user.getUserId());
			//this is written to check cross program card transfer
			ContactsLoyalty sourceMembership = null;
			if(sourceLoyaltyProgram.getProgramId() != loyaltyProgram.getProgramId()){
				sourceMembership = findMembershpByCard(sourceCardLong, sourceLoyaltyProgram.getProgramId(), user.getUserId());
			}else{
				sourceMembership = findMembershpByCard(sourceCardLong, loyaltyProgram.getProgramId(), user.getUserId());
			}
			
			if(sourceMembership == null){
				status = new Status("1000", PropertyUtil.getErrorMessage(1000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
			}
			if(sourceMembership != null && sourceMembership.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)){
				status = new Status("111579", PropertyUtil.getErrorMessage(111579, OCConstants.ERROR_LOYALTY_FLAG)+" "+sourceCardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
			}
			if(sourceMembership.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED)){
				String message = PropertyUtil.getErrorMessage(111517, OCConstants.ERROR_LOYALTY_FLAG)+" "+sourceMembership.getCardNumber()+".";
				status = new Status("111517", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
		
			}
			//Suspended cards will transfer 
			/*else if(sourceMembership.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED)){
				String message = PropertyUtil.getErrorMessage(111539, OCConstants.ERROR_LOYALTY_FLAG)+" "+sourceMembership.getCardNumber()+".";
				status = new Status("111539", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
			}*/
			// suspended card will transfer if suspended_card_flag is enabled
			/*else if((!userOrg.isSuspendedCardTransfer()) && (sourceMembership.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED))){
				String message = PropertyUtil.getErrorMessage(111539, OCConstants.ERROR_LOYALTY_FLAG)+" "+sourceMembership.getCardNumber()+".";
				status = new Status("111539", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
			}*/
			else if(sourceMembership.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
				ContactsLoyalty destLoyalty = getDestMembershipIfAny(sourceMembership);
				String maskedNum = Constants.STRING_NILL;
				if(destLoyalty != null) {
					maskedNum = Utility.maskNumber(destLoyalty.getCardNumber()+Constants.STRING_NILL);
				}
				String message = PropertyUtil.getErrorMessage(111578, OCConstants.ERROR_LOYALTY_FLAG)+maskedNum+".";
				status = new Status("111578", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
			}
			else if(sourceMembership.getRewardFlag().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G)) {
				
				String msg = PropertyUtil.getErrorMessage(111576, OCConstants.ERROR_LOYALTY_FLAG)+" "+sourceMembership.getCardNumber()+".";
				status = new Status("111576", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
				
			}
			
			ContactsLoyalty destMembership = findMembershpByCard(destCardLong, loyaltyProgram.getProgramId(), user.getUserId());
			
			Double fromLtyBalance = null;//contactsLoyalty.getTotalLoyaltyEarned();
			Double fromAmtBalance = null;//contactsLoyalty.getTotalGiftcardAmount();
			Double fromCPVBalance = null;//contactsLoyalty.getCummulativePurchaseValue();
			
			//Double fromLPVBalance = LoyaltyProgramHelper.getLPV(destMembership);
			
			if(!destLtyCard.getStatus().equals(OCConstants.LOYALTY_CARD_STATUS_INVENTORY) && !destLtyCard.getStatus().equals(OCConstants.LOYALTY_CARD_STATUS_INVENTORY_TRANSFERED) && destMembership == null){
				status = new Status("1000", PropertyUtil.getErrorMessage(1000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
				return transferResponse;
			}
			if(destMembership != null) {
				if(destMembership.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)){
					status = new Status("111579", PropertyUtil.getErrorMessage(111579, OCConstants.ERROR_LOYALTY_FLAG)+" "+destCardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
					return transferResponse;
				}
				if(destMembership.getRewardFlag().equals(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G)) {
					String msg = PropertyUtil.getErrorMessage(111575, OCConstants.ERROR_LOYALTY_FLAG)+" "+destCardNumber+".";
					status = new Status("111575", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
					return transferResponse;
					
				}
				else if(destMembership.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED)){
					String message = PropertyUtil.getErrorMessage(111517, OCConstants.ERROR_LOYALTY_FLAG)+" "+destMembership.getCardNumber()+".";
					status = new Status("111517", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
					return transferResponse;
			
				}
				else if(destMembership.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED)){
					String message = PropertyUtil.getErrorMessage(111539, OCConstants.ERROR_LOYALTY_FLAG)+" "+destMembership.getCardNumber()+".";
					status = new Status("111539", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
					return transferResponse;
				}else if(destMembership.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
					ContactsLoyalty destLoyalty = getDestMembershipIfAny(destMembership);
					String maskedNum = Constants.STRING_NILL;
					if(destLoyalty != null) {
						maskedNum = Utility.maskNumber(destLoyalty.getCardNumber()+Constants.STRING_NILL);
					}
					String message = PropertyUtil.getErrorMessage(111578, OCConstants.ERROR_LOYALTY_FLAG)+maskedNum+".";
					status = new Status("111578", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
					return transferResponse;
				}
				boolean isDifferentContacts = checkLinkedContacts(sourceMembership, destMembership);
				logger.debug("isDifferentContacts ::"+isDifferentContacts);
				if(isDifferentContacts) {
					
					String msg = PropertyUtil.getErrorMessage(111577, OCConstants.ERROR_LOYALTY_FLAG)+" "+destCardNumber+".";
					status = new Status("111577", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
					return transferResponse;
				}
				if(sourceMembership.getContact().getMlBits() == 0l){
					MailingListDao mailingListDao = (MailingListDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
					MailingList mlList = mailingListDao.findPOSMailingList(user);
					
					if(mlList == null){
						status = new Status("101007", PropertyUtil.getErrorMessage(101007, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
						return transferResponse;
					}
					//we can only check POS list validation but contact cannot be added TODO need to think
					
				}
				//create or update membership
				/*destMembership = transferMembership(sourceMembership , destMembership, 
						destLtyCard.getCardNumber(), destLtyCard.getCardPin(), destCardSet.getCardSetId(), 
						loyaltyTransferRequest.getHeader().getStoreNumber(), true,loyaltyTransferRequest.getHeader().getEmployeeId(),loyaltyTransferRequest.getHeader().getTerminalId());*/
				destMembership = transferMembership(sourceMembership , destMembership, 
						destLtyCard.getCardNumber(), destLtyCard.getCardPin(), destCardSet.getCardSetId(), loyaltyTransferRequest.getHeader().getSubsidiaryNumber(),
						loyaltyTransferRequest.getHeader().getStoreNumber(), true,loyaltyTransferRequest.getHeader().getEmployeeId(),loyaltyTransferRequest.getHeader().getTerminalId(),destLtyCard.getProgramId());
				
				
				//set final reward flag
				String rewardFlag = getDestRewardFlag(sourceMembership, destMembership);
				if(rewardFlag == null) {
					status = new Status("100104", PropertyUtil.getErrorMessage(100104, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
					return transferResponse;
					
				}
				destMembership.setRewardFlag(rewardFlag);
				
				//tier determination
				logger.info(" Step I");
				Long tierForDestID = null;
				LoyaltyProgramTier tierForDest = findTierForDest(loyaltyTransferRequest, sourceMembership, destMembership, 
							sourceCardSet, destCardSet, loyaltyProgram, user, false);
					if(tierForDest == null){
						status = new Status("111555", PropertyUtil.getErrorMessage(111555, OCConstants.ERROR_LOYALTY_FLAG), 
								OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
						return transferResponse;
					}
					if(tierForDest != null){
						tierForDestID = tierForDest.getTierId();
						dummyHighestTier = tierForDest;
					}

					destMembership.setProgramTierId(tierForDestID);
				
				//balances
				 fromLtyBalance = destMembership.getTotalLoyaltyEarned();
				fromAmtBalance = destMembership.getTotalGiftcardAmount();
				fromCPVBalance = destMembership.getCummulativePurchaseValue();
				
				Double fromLPVBalance = LoyaltyProgramHelper.getLPV(destMembership);
					
				destMembership = adjustBalances(sourceMembership, destMembership);
				
				//set a proper threshold level
				destMembership = setProperThresholdLevel(destMembership, sourceMembership);
				LoyaltyAutoComm autoComm = getLoyaltyAutoComm(loyaltyProgram.getProgramId());
				tierForDest = applyTierUpgradeRule(destMembership, loyaltyProgram, tierForDest, autoComm, user);//APP-1769
				calculateThresholdBonus(destMembership, loyaltyProgram, fromLtyBalance, fromAmtBalance, fromCPVBalance, fromLPVBalance , tierForDest, autoComm,
						user );

				Calendar transferedOn = Calendar.getInstance();
				linkSourceTrxToDest(sourceMembership, destMembership, transferedOn);
				linkSourceExpiryToDest(sourceMembership, destMembership, transferedOn);
				linkSourceMembershipToDest(sourceMembership, destMembership, transferedOn);
							
				
				saveContactsLoyalty(destMembership);
			}
			else if(destMembership == null && destLtyCard.getStatus().equals(OCConstants.LOYALTY_CARD_STATUS_INVENTORY_TRANSFERED)) {
				
				if(sourceMembership.getContact().getMlBits() == 0l){
					MailingListDao mailingListDao = (MailingListDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
					MailingList mlList = mailingListDao.findPOSMailingList(user);
					
					if(mlList == null){
						status = new Status("101007", PropertyUtil.getErrorMessage(101007, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
						return transferResponse;
					}
					
					//we can only check POS list validation but contact cannot be added TODO need to think
				}
				/*destMembership = transferMembership(sourceMembership , destMembership, 
						destLtyCard.getCardNumber(), destLtyCard.getCardPin(), destCardSet.getCardSetId(), 
						loyaltyTransferRequest.getHeader().getStoreNumber(), false,loyaltyTransferRequest.getHeader().getEmployeeId(),loyaltyTransferRequest.getHeader().getTerminalId());*/
				destMembership = transferMembership(sourceMembership , destMembership, 
						destLtyCard.getCardNumber(), destLtyCard.getCardPin(), destCardSet.getCardSetId(), loyaltyTransferRequest.getHeader().getSubsidiaryNumber(),
						loyaltyTransferRequest.getHeader().getStoreNumber(), false,loyaltyTransferRequest.getHeader().getEmployeeId(),loyaltyTransferRequest.getHeader().getTerminalId(), destLtyCard.getProgramId());
			
				
				String rewardFlag = getDestRewardFlag(sourceMembership, destMembership);
				if(rewardFlag == null) {
					status = new Status("100104", PropertyUtil.getErrorMessage(100104, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
					return transferResponse;
					
				}
				destMembership.setRewardFlag(rewardFlag);
				logger.debug("rewardFlag is ::"+rewardFlag);
					
				//todo need to think about the tier 
				if(destCardSet.getLinkedTierLevel() > 0){
					logger.debug("destCardSet.getLinkedTierLevel() is ::"+destCardSet.getLinkedTierLevel());
					String type = "Tier "+destCardSet.getLinkedTierLevel();
					LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
					LoyaltyProgramTier linkedTierObj = loyaltyProgramTierDao.getTierByPrgmAndType(loyaltyProgram.getProgramId(), type);
					
					if(linkedTierObj == null){
						logger.debug("linkedTierObj is ::"+linkedTierObj);
						//active incomplete status response...
						status = new Status("111555", PropertyUtil.getErrorMessage(111555, OCConstants.ERROR_LOYALTY_FLAG),
								OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
						return transferResponse;
					}else{
						logger.debug("linkedTierObj is ::"+linkedTierObj);
						//need to find out the highest tier
						LoyaltyProgramTier tierForSource = findTierForDest(loyaltyTransferRequest,sourceMembership, destMembership, 
								sourceCardSet, destCardSet, loyaltyProgram, user, true);
						if(tierForSource == null){
							status = new Status("111555", PropertyUtil.getErrorMessage(111555, OCConstants.ERROR_LOYALTY_FLAG), 
									OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
							return transferResponse;
						}
						if(tierForSource != null && tierForSource.getTierType() != null && !tierForSource.getTierType().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_TIER_TYPE_PENDING)) {
							//LoyaltyProgramTier tierForDest = findHighestTier(tierForSource, linkedTierObj);
							LoyaltyProgramTier tierForDest = null;
							if(sourceMembership.getProgramId().longValue() == destMembership.getProgramId().longValue()){
								tierForDest = findHighestTier(tierForSource, linkedTierObj);
							}else{
								tierForDest = linkedTierObj;
							}
							dummyHighestTier = tierForDest;
							destMembership.setProgramTierId(tierForDest.getTierId());
						}
											
						destMembership = adjustBalances(sourceMembership, destMembership);
						
						Calendar transferedOn =  Calendar.getInstance();
						linkSourceMembershipToDest(sourceMembership, destMembership, transferedOn);
						linkSourceTrxToDest(sourceMembership, destMembership, transferedOn);
						linkSourceExpiryToDest(sourceMembership, destMembership, transferedOn);
					
					
						saveContactsLoyalty(destMembership);
						destLtyCard.setStatus(OCConstants.LOYALTY_CARD_STATUS_ENROLLED);
						destLtyCard.setMembershipId(destMembership.getLoyaltyId());
						destLtyCard.setActivationDate(Calendar.getInstance());
						//updateCardStatus(OCConstants.LOYALTY_CARD_STATUS_ENROLLED, card);
						saveLoyaltyCard(destLtyCard);
						
						
					}
									
				}else if(destCardSet.getLinkedTierLevel() == 0){ 
					
					//need to find out the highest tier
					Long sourceTierID = null; 
					if(sourceMembership.getProgramTierId() != null && sourceMembership.getProgramId()==destMembership.getProgramId()) {
						sourceTierID = sourceMembership.getProgramTierId();
						dummyHighestTier = getLoyaltyTier(sourceTierID);
					}else{
						
						LoyaltyProgramTier tierForSource = findTierForDest(loyaltyTransferRequest,sourceMembership, destMembership, 
								sourceCardSet, destCardSet, loyaltyProgram, user, true);
						if(tierForSource == null){
							status = new Status("111555", PropertyUtil.getErrorMessage(111555, OCConstants.ERROR_LOYALTY_FLAG), 
									OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							transferResponse = prepareTransferResponse(responseHeader, null, null, null, null,  status);
							return transferResponse;
						}
						if(tierForSource != null && tierForSource.getTierType() != null && !tierForSource.getTierType().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_TIER_TYPE_PENDING)) {
							dummyHighestTier = tierForSource;
							sourceTierID = tierForSource.getTierId();
						}
					}
					destMembership.setProgramTierId(sourceTierID);
					
					//balances
					destMembership = adjustBalances(sourceMembership, destMembership);
					
					Calendar transferedOn =  Calendar.getInstance();
					linkSourceMembershipToDest(sourceMembership, destMembership, transferedOn);
					linkSourceTrxToDest(sourceMembership, destMembership, transferedOn);
					linkSourceExpiryToDest(sourceMembership, destMembership, transferedOn);
				
					saveContactsLoyalty(destMembership);
					destLtyCard.setStatus(OCConstants.LOYALTY_CARD_STATUS_ENROLLED);
					destLtyCard.setMembershipId(destMembership.getLoyaltyId());
					destLtyCard.setActivationDate(Calendar.getInstance());
					//updateCardStatus(OCConstants.LOYALTY_CARD_STATUS_ENROLLED, card);
					saveLoyaltyCard(destLtyCard);
				}
				
			}
			List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
			contactLoyaltyList.add(sourceMembership);
			contactLoyaltyList.add(destMembership);
			List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList);
			
			MembershipResponse response = prepareAccountTransferResponse(destMembership, dummyHighestTier, loyaltyProgram);
			
			List<Balance> balances = null;
			HoldBalance holdBalance = null;
			
			balances = prepareBalancesObject(destMembership, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
			
			String expiryPeriod = Constants.STRING_NILL;
			if(dummyHighestTier != null && !dummyHighestTier.getTierType().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_TIER_TYPE_PENDING) 
					&& dummyHighestTier.getActivationFlag() == OCConstants.FLAG_YES					
				&& ((destMembership.getHoldAmountBalance() != null && destMembership.getHoldAmountBalance() > 0) ||
					(destMembership.getHoldPointsBalance() != null && destMembership.getHoldPointsBalance() > 0))){
				
				expiryPeriod = dummyHighestTier.getPtsActiveDateValue()+" "+dummyHighestTier.getPtsActiveDateType();
			}
			
			holdBalance = prepareHoldBalances(destMembership, expiryPeriod);
			
			//TODO check if balances need to be added
			status = new Status("0", "Transfer will be done shortly.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			transferResponse = prepareTransferResponse(responseHeader, response, balances, holdBalance, matchedCustomers,  status);
			String transHoldBals = Constants.STRING_NILL;
			if(sourceMembership.getHoldPointsBalance() != null || sourceMembership.getHoldAmountBalance() != null){
				transHoldBals += sourceMembership.getHoldPointsBalance() != null ? "Hold Points:"+sourceMembership.getHoldPointsBalance():"Hold Points: ";
				transHoldBals += transHoldBals.length() > 0 ? OCConstants.FORM_MAPPING_SPLIT_DELIMETER:Constants.STRING_NILL;
				transHoldBals += sourceMembership.getHoldAmountBalance() != null  ? "Hold Amount:"+sourceMembership.getHoldAmountBalance():"Hold Amount: ";
				transHoldBals =  "Transfered-"+transHoldBals;
			}
			LoyaltyTransactionChild transChild = createSuccessfulTransaction(loyaltyTransferRequest, sourceMembership, destMembership, responseHeader, user.getUserId(), user.getUserOrganization().getUserOrgId()
					, loyaltyProgram.getProgramId(), OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD, 
					"source:"+sourceMembership.getCardNumber()+"-dest:"+destMembership.getCardNumber(), loyaltyTransferRequest.getHeader().getStoreNumber().trim(),transHoldBals.trim().isEmpty()?null:transHoldBals.trim(),loyaltyTransferRequest.getHeader().getEmployeeId(),loyaltyTransferRequest.getHeader().getTerminalId());
			LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
			//LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
			//LoyaltyCards destCardStatus = loyaltyCardsDao.findByCardNoAndprgmId(destCardLong.toString(), loyaltyProgram.getProgramId(), user.getUserId());
			LoyaltySettingsDao loyaltySettingsDao = (LoyaltySettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_SETTINGS_DAO);
			LoyaltySettings orgId = loyaltySettingsDao.findByOrgId(destLtyCard.getOrgId());
			//Send Loyalty transfer Email(todo check only send when status is active????)
			if(status.getErrorCode().equals("0") && (cardstatus.equalsIgnoreCase("Inventory") || cardstatus.equalsIgnoreCase("Inventory/Transfered")) && orgId != null){
				logger.debug("in Inventory Auto email ");
				autoCommGen.sendTransferEmailforInventory(sourceMembership, destMembership, user);
				if(user.isEnableSMS()) {
					autoCommGen.sendTransferSMS(sourceMembership, destMembership, user);
					
				}
			}else{
				logger.debug("in Enrolled Auto email ");
				autoCommGen.sendTransferEmail(sourceMembership, destMembership, user);
				if(user.isEnableSMS()) {
					autoCommGen.sendTransferSMS(sourceMembership, destMembership, user);
					
				}
				
			}
			
			
			return transferResponse;
			
		} catch (Exception e) {
			logger.error("Exception in processing transfer", e);
			throw new BaseServiceException("Server Error.");
		}
		//return null;
	}
	
	
	
	private boolean checkLinkedContacts(ContactsLoyalty sourceMembership, ContactsLoyalty destMembership) {
		logger.debug("in checkLinkedContacts ");
		Contacts sourceContacts = sourceMembership.getContact();
		Contacts destContacts = destMembership.getContact();
		
		String sourceMobile = sourceContacts.getMobilePhone();
		String destMobile = destContacts.getMobilePhone();
		boolean isSameConatact = false;
		if(sourceContacts.getContactId().longValue() == destContacts.getContactId().longValue()) return false;
		
		if(sourceContacts.getContactId().longValue() != destContacts.getContactId().longValue()) {
			/*if(sourceMobile != null && destMobile != null && 
					( (sourceMobile.length()>=destMobile.length() && sourceMobile.endsWith(destMobile) ) ||
							(destMobile.length()>=sourceMobile.length() && destMobile.endsWith(sourceMobile)) )) {
				
				isSameMobile = true;
			}*/
			
			if((sourceContacts.getExternalId() != null && destContacts.getExternalId() != null && sourceContacts.getExternalId().equals(destContacts.getExternalId())) ||
					(sourceContacts.getEmailId() != null && destContacts.getEmailId() != null && sourceContacts.getEmailId().equalsIgnoreCase(destContacts.getEmailId())) || (sourceMobile != null && destMobile != null && 
							( (sourceMobile.length()>=destMobile.length() && sourceMobile.endsWith(destMobile) ) ||
									(destMobile.length()>=sourceMobile.length() && destMobile.endsWith(sourceMobile)) )) ) {
				logger.debug("in checkLinkedContacts case 1");
				return false;
				
			}
			if((sourceContacts.getExternalId() == null || destContacts.getExternalId() == null) && 
					(sourceContacts.getEmailId() == null || destContacts.getEmailId() == null ) && 
					(sourceContacts.getMobilePhone() == null || destContacts.getMobilePhone() == null)){
				logger.debug("in checkLinkedContacts case 2");
				return true;
			}
			logger.debug("in checkLinkedContacts case 3");
			 return true;
		}
		
		return true;
	}
	private HoldBalance prepareHoldBalances(ContactsLoyalty contactsLoyalty, String activationPeriod){
		logger.debug("in prepareHoldBalances ");
		HoldBalance holdBalance = new HoldBalance();
		holdBalance.setActivationPeriod(activationPeriod);
		//holdBalance.setCurrency(contactsLoyalty.getHoldAmountBalance() == null ? Constants.STRING_NILL : Constants.STRING_NILL+contactsLoyalty.getHoldAmountBalance());
		if(contactsLoyalty.getHoldAmountBalance() == null){
			holdBalance.setCurrency(Constants.STRING_NILL);
		}
		else{
			double value = new BigDecimal(contactsLoyalty.getHoldAmountBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			holdBalance.setCurrency(Constants.STRING_NILL+value);
		}
		
		holdBalance.setPoints(contactsLoyalty.getHoldPointsBalance() == null ? Constants.STRING_NILL : Constants.STRING_NILL+contactsLoyalty.getHoldPointsBalance().intValue());
		return holdBalance;
		
	}
	private List<Balance> prepareBalancesObject(ContactsLoyalty loyalty, String pointsDiff, 
			String amountDiff, String giftDiff) throws Exception{
		logger.debug("in prepareBalancesObject ");
		List<Balance> balancesList = null;
		Balance pointBalances = null;
		Balance amountBalances = null;
		Balance giftBalances = null;
		balancesList = new ArrayList<Balance>();
		
		pointBalances = new Balance();
		pointBalances.setType(OCConstants.LOYALTY_TYPE_REWARD);
		pointBalances.setValueCode(OCConstants.LOYALTY_TYPE_POINTS);
		pointBalances.setAmount(loyalty.getLoyaltyBalance() == null ? Constants.STRING_NILL : Constants.STRING_NILL+loyalty.getLoyaltyBalance().intValue());
		pointBalances.setDifference(pointsDiff);
			
		
		amountBalances = new Balance();
		amountBalances.setType(OCConstants.LOYALTY_TYPE_REWARD);
		amountBalances.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
		if(loyalty.getGiftcardBalance() == null){
			amountBalances.setAmount(Constants.STRING_NILL);
		}
		else{
			double value = new BigDecimal(loyalty.getGiftcardBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			amountBalances.setAmount(Constants.STRING_NILL+value);
		}
		if(amountDiff == null || amountDiff == Constants.STRING_NILL){
			amountBalances.setDifference(Constants.STRING_NILL);
		}
		else{
			double value = new BigDecimal(Double.valueOf(amountDiff)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			amountBalances.setDifference(Constants.STRING_NILL+value);
		}
		
		giftBalances = new Balance();
		giftBalances.setType(OCConstants.LOYALTY_TYPE_GIFT);
		giftBalances.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
		if(loyalty.getGiftBalance() == null){
			giftBalances.setAmount(Constants.STRING_NILL);
		}
		else{
			double value = new BigDecimal(loyalty.getGiftBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			giftBalances.setAmount(Constants.STRING_NILL+value);
		}
		if(giftDiff == null || giftDiff == Constants.STRING_NILL){
			giftBalances.setDifference(Constants.STRING_NILL);
		}
		else{
			double value = new BigDecimal(Double.valueOf(giftDiff)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			giftBalances.setDifference(Constants.STRING_NILL+value);
		}
		
		balancesList.add(pointBalances);
		balancesList.add(amountBalances);
		balancesList.add(giftBalances);
		
		return balancesList;
	}
	
	
	private MembershipResponse prepareAccountTransferResponse(ContactsLoyalty destMembership, LoyaltyProgramTier tier, LoyaltyProgram program) throws Exception {
		logger.debug("in prepareAccountTransferResponse ");
		MembershipResponse accountResponse = new MembershipResponse();
		if(destMembership.getMembershipType().equals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD)){
			accountResponse.setCardNumber(Constants.STRING_NILL+destMembership.getCardNumber());
			accountResponse.setCardPin(destMembership.getCardPin());
		}
		
		if( tier != null && !"Pending".equalsIgnoreCase(tier.getTierType())){
			
			if(program.getTierEnableFlag() == OCConstants.FLAG_YES) {
				accountResponse.setTierLevel(tier.getTierType());
				accountResponse.setTierName(tier.getTierName());
			}
			else {
				accountResponse.setTierLevel(Constants.STRING_NILL);
				accountResponse.setTierName(Constants.STRING_NILL);
			}
			
			if(program.getMembershipExpiryFlag() == 'Y' && tier.getMembershipExpiryDateType() != null 
					&& tier.getMembershipExpiryDateValue() != null){
				accountResponse.setExpiry(LoyaltyProgramHelper.getMbrshipExpiryDate(destMembership.getCreatedDate(), destMembership.getTierUpgradedDate(), 
						false, tier.getMembershipExpiryDateType(), tier.getMembershipExpiryDateValue()));
			}
			else{
				accountResponse.setExpiry(Constants.STRING_NILL);
			}
			
		}
		else{
			accountResponse.setTierLevel(Constants.STRING_NILL);
			accountResponse.setTierName(Constants.STRING_NILL);
			accountResponse.setExpiry(Constants.STRING_NILL);
		}
		
		return accountResponse;
		
	}
	
	private List<MatchedCustomer> prepareMatchedCustomers(List<ContactsLoyalty> enrollList) throws Exception {
		logger.debug("in prepareMatchedCustomers ");
		Contacts contact = null;
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		List<MatchedCustomer> matchedCustList = new ArrayList<MatchedCustomer>();
		MatchedCustomer matchedCustomer = null;
		
		for(ContactsLoyalty loyalty : enrollList){
			if(loyalty.getContact() != null && loyalty.getContact().getContactId() != null){
				contact = loyalty.getContact();
				if(contact != null){
					matchedCustomer = new MatchedCustomer();
					matchedCustomer.setMembershipNumber(Constants.STRING_NILL+loyalty.getCardNumber());
					matchedCustomer.setFirstName(contact.getFirstName() == null ? Constants.STRING_NILL : contact.getFirstName().trim());
					matchedCustomer.setLastName(contact.getLastName() == null ? Constants.STRING_NILL : contact.getLastName().trim());
					matchedCustomer.setCustomerId(contact.getExternalId() == null ? Constants.STRING_NILL : contact.getExternalId());
					matchedCustomer.setEmailAddress(contact.getEmailId() == null ? Constants.STRING_NILL : contact.getEmailId());
					matchedCustomer.setPhone(contact.getMobilePhone() == null ? Constants.STRING_NILL : contact.getMobilePhone());
					matchedCustList.add(matchedCustomer);
				}
			}
		}
		
		return matchedCustList;
		
	}
	
	private Status validateStoreNumberExclusion(LoyaltyTransferMembershipJsonRequest loyaltyTransferRequest, LoyaltyProgram program, 
			LoyaltyProgramExclusion loyaltyExclusion) throws Exception {
		logger.debug("in validateStoreNumberExclusion ");
		Status status = null;
		if(loyaltyExclusion.getStoreNumberStr() != null && !loyaltyExclusion.getStoreNumberStr().trim().isEmpty()){
			String[] storeNumberArr = loyaltyExclusion.getStoreNumberStr().split(";=;");
			for(String storeNo : storeNumberArr){
				if(loyaltyTransferRequest.getHeader().getStoreNumber().trim().equals(storeNo.trim())){
					status = new Status("111532", PropertyUtil.getErrorMessage(111532, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return status;
				}
			}
		}
		
		return status;
	}
	
	private LoyaltyProgramExclusion getLoyaltyExclusion(Long programId) throws Exception {
		logger.debug("in getLoyaltyExclusion ");
		try{
		LoyaltyProgramExclusionDao exclusionDao = (LoyaltyProgramExclusionDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_EXCLUSION_DAO);
		return exclusionDao.getExlusionByProgId(programId);
		}catch(Exception e){
			logger.error("Exception in getting loyalty exclusion ..", e);
		}
		return null;
	}
	private LoyaltyProgramTier findTierForDest(LoyaltyTransferMembershipJsonRequest transferRequest, ContactsLoyalty sourceMembership, ContactsLoyalty destMembership,
			LoyaltyCardSet sourceCardSet, LoyaltyCardSet destCardSet, LoyaltyProgram loyaltyProgram, Users user, boolean isDestNew) throws Exception{
		logger.info(" Step II");
		//check source tier and dest tier and then (determine if one of them is null) check for highest tier
		LoyaltyProgramTier sourceTier = null;
		LoyaltyProgramTier destTier = null;
		int caseNo = 0;
		if(sourceMembership.getProgramId()!=destMembership.getProgramId() && isDestNew){
			logger.info(" Step III");
			LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
			List<LoyaltyProgramTier> listTier = loyaltyProgramTierDao.getAllTierByProgramId(destMembership.getProgramId());
				if(listTier!=null && listTier.size()>0){
						Collections.sort(listTier, new Comparator<LoyaltyProgramTier>() {
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
						destTier=	listTier.get(0);
						return destTier;
			}
				
		}
		if(sourceMembership.getProgramTierId() != null ){
			caseNo += 1;
			try {
				sourceTier = getLoyaltyTier(sourceMembership.getProgramTierId());
				if(!isDestNew) {
					caseNo += 1;
					if(destMembership.getProgramTierId() == null){
						caseNo += 1;
						destTier = determineTier(sourceMembership, destMembership, user, loyaltyProgram, transferRequest, false, isDestNew);
						
						if(destTier != null && !OCConstants.LOYALTY_PROGRAM_TIER_TYPE_PENDING.equalsIgnoreCase(destTier.getTierType())){
								
							destMembership.setProgramTierId(destTier.getTierId());
							caseNo += 1;
							// destTier = findHighestTier(sourceTier, destTier);
							// condition added for cross program card transfer
							if(sourceMembership.getProgramId().longValue() == destMembership.getProgramId().longValue()){
								destTier = findHighestTier(sourceTier, destTier);
							}
							return destTier;
						}
						caseNo += 1;
						 return destTier;
						//condition added for cross program card transfer
						/*if(sourceMembership.getProgramId().longValue() == destMembership.getProgramId().longValue()){
							return destTier;
						}else{
							LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
							LoyaltyProgramTier destinationTier = null;
							destinationTier = loyaltyProgramTierDao.getTierByPrgmAndType(destMembership.getProgramId(), OCConstants.LOYALTY_PROGRAM_TIER1);
							return destinationTier;
						}*/
					}else {
						caseNo += 1;
						destTier = getLoyaltyTier(destMembership.getProgramTierId());
						// destTier = findHighestTier(sourceTier, destTier);
						// condition added for cross program card transfer
						if(sourceMembership.getProgramId().longValue() == destMembership.getProgramId().longValue()){
							destTier = findHighestTier(sourceTier, destTier);
							logger.info("dest tier with enrolled destination card with tierid.."+destTier.getTierType());
						}
						return destTier;
					}
				}else{
					caseNo += 1;
					// condition added for cross program card transfer
					//return sourceTier
					if(sourceMembership.getProgramId().longValue() == destMembership.getProgramId().longValue()){
						return sourceTier;
					}else{
						LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
						LoyaltyProgramTier destinationTier = null;
						destinationTier = loyaltyProgramTierDao.getTierByPrgmAndType(destMembership.getProgramId(), OCConstants.LOYALTY_PROGRAM_TIER1);
						return destinationTier;
					}
				}
			} catch (Exception e1) {
				logger.error("Eception in finding  tier ", e1);
				return null;
			}
		}
		else{
			caseNo += 1;
			try {
				sourceTier = determineTier(sourceMembership, destMembership, user, loyaltyProgram, transferRequest, true, isDestNew);
				
				if(sourceTier != null && !OCConstants.LOYALTY_PROGRAM_TIER_TYPE_PENDING.equalsIgnoreCase(sourceTier.getTierType())){
					caseNo += 1;
					sourceMembership.setProgramTierId(sourceTier.getTierId());
					if(!isDestNew) {
						caseNo += 1;
						if(destMembership.getProgramTierId() == null){
							caseNo += 1;
							destTier = determineTier(sourceMembership, destMembership, user, loyaltyProgram, transferRequest, false, isDestNew);
							
							if(destTier != null && !OCConstants.LOYALTY_PROGRAM_TIER_TYPE_PENDING.equalsIgnoreCase(destTier.getTierType())){
								caseNo += 1;
								destMembership.setProgramTierId(destTier.getTierId());
								// return destTier;
								// condition added for cross program card transfer
								if(sourceMembership.getProgramId().longValue() == destMembership.getProgramId().longValue()){
									destTier = findHighestTier(sourceTier, destTier);
								}
								return destTier;
							}
							caseNo += 1;
							return destTier;
							// condition added for cross program card transfer
							/*if(sourceMembership.getProgramId().longValue() == destMembership.getProgramId().longValue()){
								return destTier;
							}else{
								LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
								LoyaltyProgramTier destinationTier = null;
								destinationTier = loyaltyProgramTierDao.getTierByPrgmAndType(destMembership.getProgramId(), OCConstants.LOYALTY_PROGRAM_TIER1);
								return destinationTier;
							}*/
						}else {
							caseNo += 1;
							destTier = getLoyaltyTier(destMembership.getProgramTierId());
							// condition added for cross program card transfer
							if(sourceMembership.getProgramId().longValue() == destMembership.getProgramId().longValue()){
								destTier = findHighestTier(sourceTier, destTier);
							}
							return destTier;
						}
					}else{
						caseNo += 1;
						//return sourceTier;
						// condition added for cross program card transfer
						if(sourceMembership.getProgramId().longValue() == destMembership.getProgramId().longValue()){
							return sourceTier;
						}else{
							LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
							LoyaltyProgramTier destinationTier = null;
							destinationTier = loyaltyProgramTierDao.getTierByPrgmAndType(destMembership.getProgramId(), OCConstants.LOYALTY_PROGRAM_TIER1);
							return destinationTier;
						}
						
					}
				}
				//return sourceTier;
				// condition added for cross program card transfer
				if(sourceMembership.getProgramId().longValue() == destMembership.getProgramId().longValue()){
					return sourceTier;
				}else{
					LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
					LoyaltyProgramTier destinationTier = null;
					destinationTier = loyaltyProgramTierDao.getTierByPrgmAndType(destMembership.getProgramId(), OCConstants.LOYALTY_PROGRAM_TIER1);
					return destinationTier;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Eception in finding  tier ", e);
				return null;
			}
			
		
		}
		
	}
	
	private LoyaltyProgramTier findHighestTier(LoyaltyProgramTier sourceTier, LoyaltyProgramTier destTier) throws Exception {
		List<LoyaltyProgramTier> TierList = new ArrayList<LoyaltyProgramTier>();
		TierList.add(sourceTier);
		TierList.add(destTier);
		logger.debug("in findHighestTier "+ sourceTier + " "+destTier);
		
		Collections.sort(TierList, new Comparator<LoyaltyProgramTier>() {
			@Override
			public int compare(LoyaltyProgramTier o1, LoyaltyProgramTier o2) {
				//try {
					int num1 = Integer.valueOf(o1.getTierType().substring(5)).intValue();
					int num2 = Integer.valueOf(o2.getTierType().substring(5)).intValue();
					logger.debug("in findHighestTier "+ num1 + " "+num2);
					if(num1 < num2){
						return -1;
					}
					else if(num1 == num2){
						return 0;
					}
					else{
						return 1;
					}
				/*} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception,", e);
					return 
				}*/
			}
		});
		
		return TierList.get(TierList.size()-1);
	}
	private ContactsLoyalty setProperThresholdLevel(ContactsLoyalty destinationMember, ContactsLoyalty sourceMember){
		
		if(sourceMember.getBonusPointsThreshold() != null ){
			
			if( (destinationMember.getBonusPointsThreshold() != null && sourceMember.getBonusPointsThreshold() > destinationMember.getBonusPointsThreshold()) || destinationMember.getBonusPointsThreshold() == null){
			
				destinationMember.setBonusPointsThreshold(sourceMember.getBonusPointsThreshold());
			}
			
		}
		if(sourceMember.getBonusCurrencyThreshold() != null ){
			
			if( (destinationMember.getBonusCurrencyThreshold() != null && 
					sourceMember.getBonusCurrencyThreshold() > destinationMember.getBonusCurrencyThreshold()) || 
					destinationMember.getBonusCurrencyThreshold() == null){
			
				destinationMember.setBonusCurrencyThreshold(sourceMember.getBonusPointsThreshold());
			}
			
		}
		return destinationMember;
		
	}
	
	private void saveContactsLoyalty(ContactsLoyalty loyalty) throws Exception{
		logger.debug("in saveContactsLoyalty ");
		ContactsLoyaltyDaoForDML loyaltyDao = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
		loyaltyDao.saveOrUpdate(loyalty);
		
		
	}
	
	private void saveLoyaltyCard(LoyaltyCards loyaltyCard) throws Exception{
		logger.debug("in saveContactsLoyalty card ");
		LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		LoyaltyCardsDaoForDML loyaltyCardsDaoForDML = (LoyaltyCardsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);
		//loyaltyCardsDao.saveOrUpdate(loyaltyCard);
		loyaltyCardsDaoForDML.saveOrUpdate(loyaltyCard);
		
		
	}
	
	
	private LoyaltyProgramTier findTier(Long contactId, Long userId, ContactsLoyalty contactsLoyalty,
			List<LoyaltyProgramTier> tiersList, Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap) throws Exception {
		logger.debug("in findTier ");
		if(OCConstants.LOYALTY_LIFETIME_POINTS.equals(tiersList.get(0).getTierUpgdConstraint())){
			logger.info("tier condition on :"+OCConstants.LOYALTY_LIFETIME_POINTS);
			if(contactsLoyalty == null) {
				return tiersList.get(0);
			}
			else {
				Double totLoyaltyPointsValue = contactsLoyalty.getTotalLoyaltyEarned() == null ? 0.00 : contactsLoyalty.getTotalLoyaltyEarned();
				logger.info("totLoyaltyPointsValue value = "+totLoyaltyPointsValue);

				if(totLoyaltyPointsValue == null || totLoyaltyPointsValue <= 0){
					logger.info("totLoyaltyPointsValue value is empty...");
					return tiersList.get(0);
				}
				else{
					Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
					LoyaltyProgramTier prevKeyTier = null;
					LoyaltyProgramTier nextKeyTier = null;
					while(it.hasNext()){
						nextKeyTier = it.next();
						logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
						logger.info("-------------currTier::"+tiersList.get(0).getTierType());
						if(OCConstants.LOYALTY_PROGRAM_TIER1.equalsIgnoreCase(nextKeyTier.getTierType())){
							prevKeyTier = nextKeyTier;
							continue;
						}
						if(totLoyaltyPointsValue > 0 && totLoyaltyPointsValue < eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
							if(prevKeyTier == null){
								logger.info("selected tier is currTier..."+tiersList.get(0).getTierType());
								return tiersList.get(0);
							}
							logger.info("selected tier..."+prevKeyTier.getTierType());
							return prevKeyTier;
						}
						else if (totLoyaltyPointsValue > 0 && totLoyaltyPointsValue >= eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue() && !it.hasNext()) {
							logger.info("selected tier..."+nextKeyTier.getTierType());
							return nextKeyTier;
						}
						prevKeyTier = nextKeyTier;
					}
					return tiersList.get(0);
				}//else
			}
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
			}

			if(contactPurcahseList == null || totPurchaseValue == null || totPurchaseValue <= 0){*/
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			//totPurchaseValue = contactsLoyalty.getLifeTimePurchaseValue();//Double.valueOf(loyaltyTransactionChildDao.getLifeTimeLoyaltyPurchaseValue(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId()));
			totPurchaseValue = LoyaltyProgramHelper.getLPV(contactsLoyalty);
			logger.info("purchase value = "+totPurchaseValue);

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
			}//else
		}
		else if(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE.equals(tiersList.get(0).getTierUpgdConstraint())){
			
			//create a temp object and return it to caller. caller should handle this temp object.
			
			LoyaltyProgramTier tempTier = new LoyaltyProgramTier();
			tempTier.setTierType(OCConstants.LOYALTY_PROGRAM_TIER_TYPE_PENDING);
			return tempTier;			
		}
		else{
			return null;
		}

	}
	
	
	
	private List<LoyaltyProgramTier> getSortedTierList(Long programId, Long contactId) throws Exception {
		try{
			logger.debug("in getSortedTierList ");
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
	
	private LoyaltyProgramTier getLoyaltyTier(Long tierId) throws Exception{
		logger.debug("in getLoyaltyTier ");
		LoyaltyProgramTierDao tierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		return tierDao.getTierById(tierId);
		
	}
	
	private String getDestRewardFlag(ContactsLoyalty sourceMembership, ContactsLoyalty destMembership) throws Exception{
		//check if one of them is hibrid loyalty
		String rewardFlag = sourceMembership.getRewardFlag();
		String destRewardFlag = destMembership.getRewardFlag();
		logger.debug("in getDestRewardFlag "+destRewardFlag);
		
		if(destRewardFlag == null || rewardFlag.equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL)) return rewardFlag;
		//else if(destRewardFlag.equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL)) return destRewardFlag;
		else return destRewardFlag;
		
		
	}
	
	private LoyaltyProgramTier determineTier(ContactsLoyalty sourceMembership, ContactsLoyalty destMembership, Users user, 
			LoyaltyProgram loyaltyProgram, LoyaltyTransferMembershipJsonRequest transferRequest, boolean isForSource, boolean isDestNew) throws Exception{
		logger.debug("in determineTier ");
		ContactsLoyalty membership = (isForSource ? sourceMembership : destMembership);
		Long contactId = membership.getContact().getContactId();
		Long userID = user.getUserId();
		List<LoyaltyProgramTier> tierList = getSortedTierList(loyaltyProgram.getProgramId(), userID);
		if(tierList == null || tierList.size() == 0 || !OCConstants.LOYALTY_PROGRAM_TIER1.equals(tierList.get(0).getTierType())){
			return null;//TODO need to verify
		}
		
		//Prepare eligible tiers map
		Iterator<LoyaltyProgramTier> iterTier = tierList.iterator();
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
		}
		
		LoyaltyProgramTier tier = findTier(contactId, userID, membership, tierList, eligibleMap);
		if(tier != null && OCConstants.LOYALTY_PROGRAM_TIER_TYPE_PENDING.equalsIgnoreCase(tier.getTierType())){
			LoyaltyTransferMembershipCPVThread cpvThread = new LoyaltyTransferMembershipCPVThread(eligibleMap, user, 
					sourceMembership, destMembership, isForSource, isDestNew, tierList, loyaltyProgram, transferRequest);
			Thread th = new Thread(cpvThread);
			th.start();
		
		}
		return tier;
	}
	
	private ContactsLoyalty adjustBalances(ContactsLoyalty sourceMembership, ContactsLoyalty destMembership) throws Exception{
		logger.debug("in adjustBalances ");
		
		double CPV = (destMembership.getCummulativePurchaseValue() != null ? destMembership.getCummulativePurchaseValue() : 0.0)
				+ (sourceMembership.getCummulativePurchaseValue() != null ? sourceMembership.getCummulativePurchaseValue() : 0.0);
		destMembership.setCummulativePurchaseValue(CPV);
		double CRV = (destMembership.getCummulativeReturnValue() != null ? destMembership.getCummulativeReturnValue() : 0.0)
				+ (sourceMembership.getCummulativeReturnValue() != null ? sourceMembership.getCummulativeReturnValue() : 0.0);
		destMembership.setCummulativeReturnValue(CRV);
		
		/*double LPV = (destMembership.getLifeTimePurchaseValue() != null ? destMembership.getLifeTimePurchaseValue() : 0.0) 
				+ (sourceMembership.getLifeTimePurchaseValue() != null ? sourceMembership.getLifeTimePurchaseValue() : 0.0);
		
		destMembership.setLifeTimePurchaseValue(LPV);
		*///transfer 
		double expiredGiftAmount = (destMembership.getExpiredGiftAmount() != null ? destMembership.getExpiredGiftAmount() : 0.0) 
				+ (sourceMembership.getExpiredGiftAmount() != null ? sourceMembership.getExpiredGiftAmount() : 0.0);
		
		destMembership.setExpiredGiftAmount(expiredGiftAmount);
		
		
		long expiredPoints = (destMembership.getExpiredPoints() != null ? destMembership.getExpiredPoints() : 0) 
				+ (sourceMembership.getExpiredPoints() != null ? sourceMembership.getExpiredPoints() : 0);
		destMembership.setExpiredPoints(expiredPoints);
		
		double expiredRewardAmount = (destMembership.getExpiredRewardAmount() != null ? destMembership.getExpiredRewardAmount() : 0) 
				+ (sourceMembership.getExpiredRewardAmount() != null ? sourceMembership.getExpiredRewardAmount() : 0);
		destMembership.setExpiredRewardAmount(expiredRewardAmount);
		
		double giftBalance = (destMembership.getGiftBalance() != null ? destMembership.getGiftBalance() : 0) 
				+ (sourceMembership.getGiftBalance() != null ? sourceMembership.getGiftBalance() : 0);
		destMembership.setGiftBalance(giftBalance);
		
		double giftcardBalance = (destMembership.getGiftcardBalance() != null ? destMembership.getGiftcardBalance() : 0) 
				+ (sourceMembership.getGiftcardBalance() != null ? sourceMembership.getGiftcardBalance() : 0);
		destMembership.setGiftcardBalance(giftcardBalance);
		
		double holdAmountBalance = (destMembership.getHoldAmountBalance() != null ? destMembership.getHoldAmountBalance() : 0) 
				+ (sourceMembership.getHoldAmountBalance() != null ? sourceMembership.getHoldAmountBalance() : 0);
		destMembership.setHoldAmountBalance(holdAmountBalance);
		
		double holdPointsBalance = (destMembership.getHoldPointsBalance() != null ? destMembership.getHoldPointsBalance() : 0) 
				+ (sourceMembership.getHoldPointsBalance() != null ? sourceMembership.getHoldPointsBalance() : 0);
		destMembership.setHoldPointsBalance(holdPointsBalance);
		
		double loyaltyBalance = (destMembership.getLoyaltyBalance() != null ? destMembership.getLoyaltyBalance() : 0) 
				+ (sourceMembership.getLoyaltyBalance() != null ? sourceMembership.getLoyaltyBalance() : 0);
		destMembership.setLoyaltyBalance(loyaltyBalance);
		
		double totalGiftAmount = (destMembership.getTotalGiftAmount() != null ? destMembership.getTotalGiftAmount() : 0) 
				+ (sourceMembership.getTotalGiftAmount() != null ? sourceMembership.getTotalGiftAmount() : 0);
		destMembership.setTotalGiftAmount(totalGiftAmount);
		
		double totalGiftcardAmount = (destMembership.getTotalGiftcardAmount() != null ? destMembership.getTotalGiftcardAmount() : 0) 
				+ (sourceMembership.getTotalGiftcardAmount() != null ? sourceMembership.getTotalGiftcardAmount() : 0);
		destMembership.setTotalGiftcardAmount(totalGiftcardAmount);
		
		double totalGiftcardRedemption = (destMembership.getTotalGiftcardRedemption() != null ? destMembership.getTotalGiftcardRedemption() : 0) 
				+ (sourceMembership.getTotalGiftcardRedemption() != null ? sourceMembership.getTotalGiftcardRedemption() : 0);
		destMembership.setTotalGiftcardRedemption(totalGiftcardRedemption);
		
		double totalGiftRedemption = (destMembership.getTotalGiftRedemption() != null ? destMembership.getTotalGiftRedemption() : 0) 
				+ (sourceMembership.getTotalGiftRedemption() != null ? sourceMembership.getTotalGiftRedemption() : 0);
		destMembership.setTotalGiftRedemption(totalGiftRedemption);
		
		double totalLoyaltyEarned = (destMembership.getTotalLoyaltyEarned() != null ? destMembership.getTotalLoyaltyEarned() : 0) 
				+ (sourceMembership.getTotalLoyaltyEarned() != null ? sourceMembership.getTotalLoyaltyEarned() : 0);
		destMembership.setTotalLoyaltyEarned(totalLoyaltyEarned);
		
		double totalLoyaltyRedemption = (destMembership.getTotalLoyaltyRedemption() != null ? destMembership.getTotalLoyaltyRedemption() : 0) 
				+ (sourceMembership.getTotalLoyaltyRedemption() != null ? sourceMembership.getTotalLoyaltyRedemption() : 0);
		destMembership.setTotalLoyaltyRedemption(totalLoyaltyRedemption);
		
		return destMembership;
		
	}
	
	private void linkSourceMembershipToDest(ContactsLoyalty sourceMembership, ContactsLoyalty destMembership, Calendar transferedOn) throws Exception{
		logger.debug("in linkSourceMembershipToDest ");
		saveContactsLoyalty(destMembership);
		sourceMembership.setTransferedTo(destMembership.getLoyaltyId());
		sourceMembership.setTransferedOn(transferedOn);
		sourceMembership.setMembershipStatus(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED);
		saveContactsLoyalty(sourceMembership);
		ContactsLoyaltyDaoForDML loyaltyDao = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getBeanByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
		loyaltyDao.updateAllChildMembership(sourceMembership.getLoyaltyId(), destMembership.getLoyaltyId(),
				sourceMembership.getUserId(), sourceMembership.getProgramId(), MyCalendar.calendarToString(transferedOn, MyCalendar.FORMAT_DATETIME_STYEAR));
		
	}
	
	private void linkSourceTrxToDest(ContactsLoyalty sourceMembership, ContactsLoyalty destMembership, Calendar transferedOn) throws Exception{
		logger.debug("in linkSourceTrxToDest ");
		LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
		LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
		
		//loyaltyTransactionChildDao.transferSourceTrxnsToDestMembership(sourceMembership.getLoyaltyId(), destMembership.getLoyaltyId(), MyCalendar.calendarToString(transferedOn, MyCalendar.FORMAT_DATETIME_STYEAR));
		loyaltyTransactionChildDaoForDML.transferSourceTrxnsToDestMembership(sourceMembership.getLoyaltyId(), destMembership.getLoyaltyId(), MyCalendar.calendarToString(transferedOn, MyCalendar.FORMAT_DATETIME_STYEAR),sourceMembership.getUserId());//APP-4728 tune-up
		//loyaltyTransactionChildDao.updateAllChildTrxnsToDestMembership(sourceMembership.getLoyaltyId(), destMembership.getLoyaltyId(), MyCalendar.calendarToString(transferedOn, MyCalendar.FORMAT_DATETIME_STYEAR));
		loyaltyTransactionChildDaoForDML.updateAllChildTrxnsToDestMembership(sourceMembership.getLoyaltyId(), destMembership.getLoyaltyId(), MyCalendar.calendarToString(transferedOn, MyCalendar.FORMAT_DATETIME_STYEAR),sourceMembership.getUserId());//APP-4728 tune-up
		
	}
	
	private void linkSourceExpiryToDest(ContactsLoyalty sourceMembership, ContactsLoyalty destMembership, Calendar transferedOn) throws Exception{
		logger.debug("in linkSourceExpiryToDest ");
		LoyaltyTransactionExpiryDao loyaltyTransactionExpiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		LoyaltyTransactionExpiryDaoForDML loyaltyTransactionExpiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
		//loyaltyTransactionExpiryDao.transferSourceExpiryTrxnsToDestMembership(sourceMembership.getLoyaltyId(), destMembership.getLoyaltyId(), MyCalendar.calendarToString(transferedOn, MyCalendar.FORMAT_DATETIME_STYEAR));
		loyaltyTransactionExpiryDaoForDML.transferSourceExpiryTrxnsToDestMembership(sourceMembership.getLoyaltyId(), destMembership.getLoyaltyId(), MyCalendar.calendarToString(transferedOn, MyCalendar.FORMAT_DATETIME_STYEAR),sourceMembership.getUserId());//APP-4728 tune-up
		//loyaltyTransactionExpiryDao.updateAllChildTrxnsToDestMembership(sourceMembership.getLoyaltyId(), destMembership.getLoyaltyId(), MyCalendar.calendarToString(transferedOn, MyCalendar.FORMAT_DATETIME_STYEAR));
		loyaltyTransactionExpiryDaoForDML.updateAllChildTrxnsToDestMembership(sourceMembership.getLoyaltyId(), destMembership.getLoyaltyId(), MyCalendar.calendarToString(transferedOn, MyCalendar.FORMAT_DATETIME_STYEAR),sourceMembership.getUserId());//APP-4728 tune-up
		
	}
	
	
	private LoyaltyCards findCard(String cardNumber, Users user) throws Exception {
		logger.debug("in find Card ");
		LoyaltyCardsDao loyaltyCardDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		return loyaltyCardDao.findByCardNoAnduserId(cardNumber, user.getUserId());
	}
	private LoyaltyProgram findProgram(Long programId) throws Exception {
		logger.debug("in find program ");
		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		return loyaltyProgramDao.findById(programId);
	}
	
	private LoyaltyCardSet findCardSet(Long cardSetID) throws Exception {
		logger.debug("in find Card set");
		LoyaltyCardSetDao loyaltyCardSetDao = (LoyaltyCardSetDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
		return loyaltyCardSetDao.findByCardSetId(cardSetID);
	}
	private LoyaltyAutoComm getLoyaltyAutoComm(Long programId) throws Exception {
		LoyaltyAutoCommDao autoCommDao = (LoyaltyAutoCommDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
		return autoCommDao.findById(programId);
	}
	private void calculateThresholdBonus(ContactsLoyalty contactsLoyalty, LoyaltyProgram program, Double fromLtyBalance,
			Double fromAmtBalance,Double fromCPVBalance,Double fromLPVBalance , LoyaltyProgramTier tier, LoyaltyAutoComm autoComm, Users user)
			
			 {
		try {
			
			
			fromAmtBalance = fromAmtBalance == null ? 0.0 : fromAmtBalance;
			fromLPVBalance = fromLPVBalance == null ? 0.0 : fromLPVBalance;
			fromLtyBalance = fromLtyBalance == null ? 0.0 : fromLtyBalance;
			fromCPVBalance = fromCPVBalance == null ? 0.0 : fromCPVBalance;
			LoyaltyThresholdBonusDao loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao) ServiceLocator.getInstance()
					.getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
			List<LoyaltyThresholdBonus> threshBonusList = loyaltyThresholdBonusDao
					.getBonusListByPrgmId(program.getProgramId(), 'N');
			List<LoyaltyThresholdBonus> pointsBonusList = new ArrayList<LoyaltyThresholdBonus>();
			List<LoyaltyThresholdBonus> amountBonusList = new ArrayList<LoyaltyThresholdBonus>();
			List<LoyaltyThresholdBonus> LPVBonusList = new ArrayList<LoyaltyThresholdBonus>();
			
			Double bonusPointsThreshold = (contactsLoyalty.getBonusPointsThreshold()==null) ? 0.0 : contactsLoyalty.getBonusPointsThreshold();
			Double bonusCurrencyThreshold = (contactsLoyalty.getBonusCurrencyThreshold()==null) ? 0.0 : contactsLoyalty.getBonusCurrencyThreshold();
			Double bonusPointsIterator = (contactsLoyalty.getBonusPointsIterator()==null)? 0.0 : contactsLoyalty.getBonusPointsIterator();
			Double bonusCurrencyIterator = contactsLoyalty.getBonusCurrencyIterator();

			if (threshBonusList == null)
				return;

			for (LoyaltyThresholdBonus bonus : threshBonusList) {
				if (bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_POINTS)) {
					pointsBonusList.add(bonus);
				} else if (bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_AMOUNT)) {
					amountBonusList.add(bonus);
				} else if (bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_LPV)){
					LPVBonusList.add(bonus);
				}
			}

			if (pointsBonusList.isEmpty() && amountBonusList.isEmpty() && LPVBonusList.isEmpty())
				return;

			List<LoyaltyThresholdBonus> matchedBonusList = new ArrayList<LoyaltyThresholdBonus>();

			if (pointsBonusList.size() > 0) {
				Collections.sort(pointsBonusList, new Comparator<LoyaltyThresholdBonus>() {
					@Override
					public int compare(LoyaltyThresholdBonus ltb1, LoyaltyThresholdBonus ltb2) {
						return ltb1.getEarnedLevelValue().compareTo(ltb2.getEarnedLevelValue());
					}
				});
			}
			if (amountBonusList.size() > 0) {
				Collections.sort(amountBonusList, new Comparator<LoyaltyThresholdBonus>() {
					@Override
					public int compare(LoyaltyThresholdBonus ltb1, LoyaltyThresholdBonus ltb2) {
						return ltb1.getEarnedLevelValue().compareTo(ltb2.getEarnedLevelValue());
					}
				});
			}
			if (LPVBonusList.size() > 0) {
				Collections.sort(LPVBonusList, new Comparator<LoyaltyThresholdBonus>() {
					@Override
					public int compare(LoyaltyThresholdBonus ltb1, LoyaltyThresholdBonus ltb2) {
						return ltb1.getEarnedLevelValue().compareTo(ltb2.getEarnedLevelValue());
					}
				});
			}
			
			matchedBonusList.addAll(LPVBonusList);
			matchedBonusList.addAll(pointsBonusList);
			matchedBonusList.addAll(amountBonusList);
			
			
			/*if (contactsLoyalty.getTotalLoyaltyEarned() != null && contactsLoyalty.getTotalLoyaltyEarned() > 0) {
				for (LoyaltyThresholdBonus bonus : pointsBonusList) {
					if (contactsLoyalty.getTotalLoyaltyEarned() >= bonus.getEarnedLevelValue()
							&& (fromLtyBalance == null || fromLtyBalance.doubleValue() < bonus.getEarnedLevelValue())) {
						matchedBonusList.add(bonus);
					}
				}
			}
			if (contactsLoyalty.getTotalGiftcardAmount() != null && contactsLoyalty.getTotalGiftcardAmount() > 0) {
				for (LoyaltyThresholdBonus bonus : amountBonusList) {
					if (contactsLoyalty.getTotalGiftcardAmount() >= bonus.getEarnedLevelValue()
							&& (fromAmtBalance == null || fromAmtBalance.doubleValue() < bonus.getEarnedLevelValue())) {
						matchedBonusList.add(bonus);
					}
				}
			}*/
			long bonusPoints = 0;
			double bonusAmount = 0.0;
			String bonusRate = null;
			boolean bonusflag = false;
			LoyaltyTransactionChild transaction = null;
			if (matchedBonusList != null && matchedBonusList.size() > 0) {
				for (LoyaltyThresholdBonus matchedBonus : matchedBonusList) {
					bonusflag = false;
					long multiplier = -1;
					double afterBalLoyaltyEarned= contactsLoyalty.getTotalLoyaltyEarned() == null ? 0.0 : contactsLoyalty.getTotalLoyaltyEarned();
					double afterBalGiftCardAmt= contactsLoyalty.getTotalGiftcardAmount() == null ? 0.0 : contactsLoyalty.getTotalGiftcardAmount();
					double afterBalCPV= contactsLoyalty.getCummulativePurchaseValue() == null ? 0.0 : contactsLoyalty.getCummulativePurchaseValue();
					
					double afterBalLPV= LoyaltyProgramHelper.getLPV(contactsLoyalty);
					if (OCConstants.LOYALTY_TYPE_POINTS.equals(matchedBonus.getEarnedLevelType())) {
						logger.info("---------POINTS-----------");
						logger.info("previous points balance (fromLtyBalance)"+fromLtyBalance);
						logger.info("after points balance (getEarnedLevelValue())"+matchedBonus.getEarnedLevelValue());
						
						//This code is for recurring bonus
						if(matchedBonus.isRecurring()){
							Double beforeFactor = fromLtyBalance.doubleValue()/matchedBonus.getEarnedLevelValue();
							Double afterFactor = afterBalLoyaltyEarned/matchedBonus.getEarnedLevelValue();
							if(beforeFactor.intValue() < afterFactor.intValue()) {
								bonusflag = true;
								multiplier = afterFactor.intValue()-beforeFactor.intValue();
							}
							logger.info("before factor===="+beforeFactor);
							logger.info("after factor===="+afterFactor);
							logger.info("multiplier===="+multiplier);
							
						}
						else if (! matchedBonus.isRecurring() ){
							
							if( afterBalLoyaltyEarned >= matchedBonus.getEarnedLevelValue() && 
									(fromLtyBalance == null || fromLtyBalance.doubleValue() < matchedBonus.getEarnedLevelValue()) && 
									bonusPointsThreshold < matchedBonus.getEarnedLevelValue() ){
								multiplier = 1;
								bonusflag = true;
							}
							
						}
							
							
					}else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(matchedBonus.getEarnedLevelType())) {
						
						logger.info("---------AMOUNT-----------");
						logger.info("previous points balance (fromAmtBalance)"+fromAmtBalance);
						logger.info("after points balance (getEarnedLevelValue())"+matchedBonus.getEarnedLevelValue());
						
						if(matchedBonus.isRecurring()){
							Double beforeFactor = fromAmtBalance.doubleValue()/matchedBonus.getEarnedLevelValue();
							Double afterFactor = afterBalGiftCardAmt/matchedBonus.getEarnedLevelValue();
							if(beforeFactor.intValue() < afterFactor.intValue()){
								bonusflag = true;
								multiplier = afterFactor.intValue()-beforeFactor.intValue();
							}
							logger.info("before factor===="+beforeFactor);
							logger.info("after factor===="+afterFactor);
							logger.info("multiplier===="+multiplier);
							
						
						}else if (! matchedBonus.isRecurring()){
							
							if(afterBalGiftCardAmt >= matchedBonus.getEarnedLevelValue() && (fromAmtBalance == null || fromAmtBalance.doubleValue() < matchedBonus.getEarnedLevelValue() && bonusCurrencyThreshold < matchedBonus.getEarnedLevelValue())) {
								multiplier = 1;
								bonusflag = true;
							}
							
						}
								
						
					}else if(OCConstants.LOYALTY_TYPE_LPV.equals(matchedBonus.getEarnedLevelType())) {
						
						logger.info("---------LPV-----------");
						logger.info("previous points balance (fromLPVBalance)"+fromLPVBalance);
						logger.info("after points balance (getEarnedLevelValue())"+matchedBonus.getEarnedLevelValue());
						
						/*if(matchedBonus.isRecurring()){
							afterBalLPV= (afterBalLPV==afterBalCPV)  ? afterBalCPV : afterBalLPV;
							Double beforeFactor = fromCPVBalance.doubleValue()/matchedBonus.getEarnedLevelValue();
							Double afterFactor = afterBalLPV/matchedBonus.getEarnedLevelValue();
							if(beforeFactor.intValue() < afterFactor.intValue()){
								bonusflag = true;
								multiplier = afterFactor.intValue()-beforeFactor.intValue();
							}
							logger.info("before factor===="+beforeFactor);
							logger.info("after factor===="+afterFactor);
							logger.info("multiplier===="+multiplier);
						
						}*/
												
						multiplier = LoyaltyProgramHelper.doIssueBonus(contactsLoyalty, fromLPVBalance, afterBalLPV, 
								matchedBonus.getEarnedLevelValue(), matchedBonus);
						if(matchedBonus.isRecurring() && multiplier > 0){
							
							bonusflag = true;
							logger.info("multiplier===="+multiplier);
						
						}else if (! matchedBonus.isRecurring() && multiplier ==0 ) {
							multiplier = 1;
							bonusflag = true;
						}
					}
					if(!bonusflag) continue;
						logger.info("loyalty bonus type :Points:");
						if(OCConstants.LOYALTY_TYPE_POINTS.equals(matchedBonus.getExtraBonusType()) ){
							if (contactsLoyalty.getLoyaltyBalance() == null) {
								contactsLoyalty.setLoyaltyBalance(multiplier*matchedBonus.getExtraBonusValue());
							} else {
								contactsLoyalty.setLoyaltyBalance(
										contactsLoyalty.getLoyaltyBalance() + (multiplier*matchedBonus.getExtraBonusValue()));
							}
							if (contactsLoyalty.getTotalLoyaltyEarned() == null) {
								contactsLoyalty.setTotalLoyaltyEarned(multiplier*matchedBonus.getExtraBonusValue());
							} else {
								contactsLoyalty.setTotalLoyaltyEarned(
										contactsLoyalty.getTotalLoyaltyEarned() +(multiplier* matchedBonus.getExtraBonusValue()));
							}
							bonusPoints = multiplier*matchedBonus.getExtraBonusValue().longValue();
						}else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(matchedBonus.getExtraBonusType())){
							
							String result = Utility.truncateUptoTwoDecimal(multiplier*matchedBonus.getExtraBonusValue());
							if (result != null)
								bonusAmount = Double.parseDouble(result);
							bonusRate = Constants.STRING_NILL + matchedBonus.getEarnedLevelValue() + " "
									+ matchedBonus.getEarnedLevelType() + " --> " + multiplier*matchedBonus.getExtraBonusValue() + " "
									+ matchedBonus.getExtraBonusType();
							if (contactsLoyalty.getGiftcardBalance() == null) {
								// contactsLoyalty.setGiftcardBalance(matchedBonus.getExtraBonusValue());
								contactsLoyalty.setGiftcardBalance(bonusAmount);
							} else {
								// contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() +
								// matchedBonus.getExtraBonusValue());
								contactsLoyalty.setGiftcardBalance(
										new BigDecimal(contactsLoyalty.getGiftcardBalance() + bonusAmount)
												.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
							}
							if (contactsLoyalty.getTotalGiftcardAmount() == null) {
								// contactsLoyalty.setTotalGiftcardAmount(matchedBonus.getExtraBonusValue());
								contactsLoyalty.setTotalGiftcardAmount(bonusAmount);
							} else {
								// contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount()
								// + matchedBonus.getExtraBonusValue());
								contactsLoyalty.setTotalGiftcardAmount(
										new BigDecimal(contactsLoyalty.getTotalGiftcardAmount() + bonusAmount)
												.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
							}
						}
						if(OCConstants.LOYALTY_TYPE_POINTS.equals(matchedBonus.getEarnedLevelType())){
							logger.info("*****setting bonus points threshold*********");
							contactsLoyalty.setBonusPointsThreshold(matchedBonus.getEarnedLevelValue());
						}else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(matchedBonus.getEarnedLevelType())){
							logger.info("*****setting bonus currency threshold*********");
							contactsLoyalty.setBonusCurrencyThreshold(matchedBonus.getEarnedLevelValue());

						}
						LoyaltyProgramTier bonusTxTier = null;
						if (tier != null && !"Pending".equalsIgnoreCase(tier.getTierType())) {
							bonusTxTier = tier;
						}
						//fromLtyBalance += matchedBonus.getExtraBonusValue();
						// LoyaltyTransactionChild childTxbonus =
						// createBonusTransaction(contactsLoyalty, bonusPoints,
						// OCConstants.LOYALTY_TYPE_POINTS, bonusRate);
						
						//bonusPoints =multiplier* matchedBonus.getExtraBonusValue().longValue();
						bonusRate = Constants.STRING_NILL + matchedBonus.getEarnedLevelValue() + " "
								+ matchedBonus.getEarnedLevelType() + " --> " + matchedBonus.getExtraBonusValue() + " "
								+ matchedBonus.getExtraBonusType();
						
						LoyaltyTransactionChild childTxbonus = createBonusTransaction( contactsLoyalty,
								(bonusPoints != 0 ?bonusPoints : bonusAmount ), matchedBonus.getExtraBonusType(), bonusRate);
						transaction = childTxbonus;
						createExpiryTransaction(contactsLoyalty, (long) bonusPoints, (double) bonusAmount,
								childTxbonus.getTransChildId(), OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, null,matchedBonus.getThresholdBonusId());

						if (bonusTxTier != null && OCConstants.LOYALTY_TYPE_POINTS.equals(matchedBonus.getExtraBonusType())) {
							String[] diffBonArr = applyConversionRules(contactsLoyalty, bonusTxTier);
							logger.info("balances After conversion rules updatation --  points = "
									+ contactsLoyalty.getLoyaltyBalance() + " currency = "
									+ contactsLoyalty.getGiftcardBalance());
							
							String conversionBonRate = null;
							long convertBonPoints = 0;
							double convertBonAmount = 0;
							// double convertBonAmount = 0.0;
							if (diffBonArr != null) {
								logger.info("Arr[]" + diffBonArr);
								convertBonAmount = Double.valueOf(diffBonArr[0].trim());
								// convertBonAmount = Double.valueOf(diffBonArr[0].trim()).doubleValue();
								convertBonPoints = Double.valueOf(diffBonArr[1].trim()).longValue();
								conversionBonRate = diffBonArr[2];
							//}
							long pointsDifference = bonusPoints - convertBonPoints;
							double amountDifference = 0.0;
							// amountDifference = (long)bonusAmount + convertBonAmount;
							if (diffBonArr[0] != null){
								
								amountDifference = Double.valueOf(diffBonArr[0].trim()).doubleValue();
								bonusTxTier = applyTierUpgradeRule(contactsLoyalty, program, bonusTxTier, autoComm, user
										);
								logger.info("Earn Type: Point =============== ");
								logger.info(" Point diff::" + pointsDifference);
								logger.info("amount diff :" + amountDifference);
							}
							updatePurchaseTransaction(childTxbonus, contactsLoyalty,
									Constants.STRING_NILL + pointsDifference, Constants.STRING_NILL + amountDifference,
									conversionBonRate, convertBonAmount, bonusTxTier);
							// updatePurchaseTransaction(childTxbonus, contactsLoyalty,
							// Constants.STRING_NILL+pointsDifference,
							// Constants.STRING_NILL+amountDifference, conversionBonRate,
							// Double.valueOf(diffBonArr[0].trim()).longValue(), bonusTxTier);
							}
						
						}
											

					// by pravendra
						if (transaction != null) {
							updateTransactionStatus(transaction, OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED);
							LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
							if (bonusflag &&  contactsLoyalty.getContact() != null
									&& contactsLoyalty.getContact().getContactId() != null) {
								Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
								if (contact != null && contact.getEmailId() != null) {
									autoCommGen.sendEarnBonusTemplate(autoComm != null ? autoComm.getThreshBonusEmailTmpltId() :null,
											"" + contactsLoyalty.getCardNumber(), contactsLoyalty.getCardPin(),
											contact.getUsers(), contact.getEmailId(), contact.getFirstName(),
											contact.getContactId(), contactsLoyalty.getLoyaltyId(),
											transaction.getTransChildId(),matchedBonus);
								}
							}
							if (user.isEnableSMS() && bonusflag ) {
								Long contactId = null;
								if (contactsLoyalty.getContact() != null
										&& contactsLoyalty.getContact().getContactId() != null) {
									contactId = contactsLoyalty.getContact().getContactId();
								}
								autoCommGen.sendEarnBonusSMSTemplate(autoComm!=null ? autoComm.getThreshBonusSmsTmpltId() : null, user, contactId,
										contactsLoyalty.getLoyaltyId(),
										contactsLoyalty.getMobilePhone() != null ? contactsLoyalty.getMobilePhone() : null,
										transaction.getTransChildId(),matchedBonus);
							}
						}
						
				}

			}
		} catch (Exception e) {
			logger.error("Exception in update threshold bonus...", e);
			// return null;
		}
	}
	private LoyaltyProgramTier applyTierUpgradeRule(ContactsLoyalty contactsLoyalty, LoyaltyProgram program,
			LoyaltyProgramTier currTier, LoyaltyAutoComm autoComm, Users user) {

		try {
			boolean tierUpgd = false;
			LoyaltyProgramTier newTier = LoyaltyProgramHelper
					.applyTierUpgdRules(contactsLoyalty.getContact().getContactId(), contactsLoyalty, currTier);
			if (!newTier.getTierType().equalsIgnoreCase(currTier.getTierType())) {
				currTier = newTier;
				tierUpgd = true;
			}

			if (tierUpgd) {
				contactsLoyalty.setProgramTierId(currTier.getTierId());
				contactsLoyalty.setTierUpgradedDate(Calendar.getInstance());
				contactsLoyalty.setTierUpgradeReason(currTier.getTierUpgdConstraint());
				ContactsLoyaltyDaoForDML contactsLoyaltyDao = (ContactsLoyaltyDaoForDML) ServiceLocator.getInstance()
						.getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
				contactsLoyaltyDao.saveOrUpdate(contactsLoyalty);
			}

			// Send auto communication email
			// LoyaltyAutoComm loyaltyAutoComm = getLoyaltyAutoComm(program.getProgramId());
			LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
			if (tierUpgd && autoComm != null && autoComm.getTierUpgdEmailTmpltId() != null
					&& contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
				Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
				if (contact != null && contact.getEmailId() != null) {
					autoCommGen.sendTierUpgdTemplate(autoComm.getTierUpgdEmailTmpltId(),
							Constants.STRING_NILL + contactsLoyalty.getCardNumber(), contactsLoyalty.getCardPin(), user,
							contact.getEmailId(), contact.getFirstName(), contact.getContactId(),
							contactsLoyalty.getLoyaltyId());
				}
			}
			if (user.isEnableSMS() && tierUpgd && autoComm != null && autoComm.getTierUpgdSmsTmpltId() != null
					&& contactsLoyalty.getMobilePhone() != null) {
				Long contactId = null;
				if (contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
					contactId = contactsLoyalty.getContact().getContactId();
				}
				autoCommGen.sendTierUpgdSMSTemplate(autoComm.getTierUpgdSmsTmpltId(), user, contactId,
						contactsLoyalty.getLoyaltyId(), contactsLoyalty.getMobilePhone());
			}

			// contactsLoyaltyDao.saveOrUpdate(contactsLoyalty);
		} catch (Exception e) {
			logger.error("Exception while upgrading tier...", e);
		}
		return currTier;
	}

	private void updatePurchaseTransaction(LoyaltyTransactionChild transaction, ContactsLoyalty loyalty, String ptsDiff,
			String amtDiff, String conversionRate, double convertAmt, LoyaltyProgramTier tier) {

		try {
			logger.info("***************inside update trasaction ***************");
			// transaction.setAmountDifference(amtDiff);
			transaction.setAmountDifference(Utility.truncateUptoTwoDecimal(Double.valueOf(amtDiff)));
			transaction.setPointsDifference(ptsDiff);
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setDescription(conversionRate);
			transaction.setConversionAmt(convertAmt);
			transaction.setTierId(tier.getTierId());

			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator
					.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML) ServiceLocator
					.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			// loyaltyTransactionChildDao.saveOrUpdate(transaction);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);

		} catch (Exception e) {
			logger.error("Exception while logging enroll transaction...", e);
		}
	}

	private LoyaltyTransactionExpiry createExpiryTransaction(ContactsLoyalty loyalty, Long expiryPoints,
			Double expiryAmount, Long transChildId, String rewardFlag, Long specialRewardId,Long bonusId) {

		LoyaltyTransactionExpiry transaction = null;
		try {

			transaction = new LoyaltyTransactionExpiry();
			transaction.setTransChildId(transChildId);
			transaction.setMembershipNumber(Constants.STRING_NILL + loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCreatedDate(Calendar.getInstance());
			transaction.setOrgId(loyalty.getOrgId());
			transaction.setUserId(loyalty.getUserId());
			transaction.setExpiryPoints(expiryPoints);
			transaction.setExpiryAmount(expiryAmount);
			transaction.setRewardFlag(rewardFlag);
			transaction.setSpecialRewardId(specialRewardId);//neded in case of multipliers
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setBonusId(bonusId);
			LoyaltyTransactionExpiryDao loyaltyTransactionExpiryDao = (LoyaltyTransactionExpiryDao) ServiceLocator
					.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
			LoyaltyTransactionExpiryDaoForDML loyaltyTransactionExpiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML) ServiceLocator
					.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
			// loyaltyTransactionExpiryDao.saveOrUpdate(transaction);
			loyaltyTransactionExpiryDaoForDML.saveOrUpdate(transaction);

		} catch (Exception e) {
			logger.error("Exception while logging enroll transaction...", e);
		}
		return transaction;
	}
	
	private LoyaltyTransactionChild createBonusTransaction(
			ContactsLoyalty loyalty, double earnedValue, String earnType, String bonusRate) {
		


		LoyaltyTransactionChild transaction = null;
		try {

			transaction = new LoyaltyTransactionChild();
			transaction.setMembershipNumber(Constants.STRING_NILL + loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCardSetId(loyalty.getCardSetId());

			// transaction.setCreatedDate(Calendar.getInstance());
			transaction.setCreatedDate(Calendar.getInstance());
			transaction.setEarnType(earnType);
			if (earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)) {
				transaction.setEarnedPoints((double) earnedValue);
			} else if (earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)) {
				transaction.setEarnedAmount((double) earnedValue);
			}
			transaction.setEnteredAmount((double) earnedValue);
			transaction.setOrgId(loyalty.getOrgId());
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setUserId(loyalty.getUserId());
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat(MyCalendar.FORMAT_YEARTODATE);
			Date date = cal.getTime();
			String Strdate = format.format(date);
			transaction.setValueActivationDate(format.parse(Strdate));
			transaction.setEarnStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_BONUS);
			transaction.setDescription("Threshold Bonus : " + bonusRate);
			// transaction.setSource(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_AUTO);
			transaction.setSourceType(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_AUTO);
			transaction.setLoyaltyId(loyalty.getLoyaltyId());

			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator
					.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML) ServiceLocator
					.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			// loyaltyTransactionChildDao.saveOrUpdate(transaction);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);

		} catch (Exception e) {
			logger.error("Exception while logging enroll transaction...", e);
		}
		return transaction;
	
	}
	
	private String[] applyConversionRules(ContactsLoyalty contactsLoyalty, LoyaltyProgramTier tier) {

		String[] differenceArr = null;

		try {

			if (tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO)) {

				if (tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0
						&& contactsLoyalty.getLoyaltyBalance() != null && contactsLoyalty.getLoyaltyBalance() > 0
						&& contactsLoyalty.getLoyaltyBalance() >= tier.getConvertFromPoints()) {

					differenceArr = new String[3];

					double multipledouble = contactsLoyalty.getLoyaltyBalance() / tier.getConvertFromPoints();
					int multiple = (int) multipledouble;
					// double convertedAmount = tier.getConvertToAmount() * multiple;
					double convertedAmount = 0.0;
					String result = Utility.truncateUptoTwoDecimal(tier.getConvertToAmount() * multiple);
					if (result != null)
						convertedAmount = Double.parseDouble(result);
					// double convertedAmount = new BigDecimal(tier.getConvertToAmount() *
					// multiple).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
					double subPoints = multiple * tier.getConvertFromPoints();

					differenceArr[0] = Constants.STRING_NILL + convertedAmount;
					differenceArr[1] = Constants.STRING_NILL + subPoints;
					// differenceArr[2] = tier.getConvertFromPoints().intValue()+" Points ->
					// "+tier.getConvertToAmount().intValue();
					differenceArr[2] = tier.getConvertFromPoints().intValue() + " Points -> "
							+ tier.getConvertToAmount().doubleValue();

					logger.info("multiple factor = " + multiple);
					logger.info("Conversion amount =" + convertedAmount);
					logger.info("subtract points = " + subPoints);

					// update giftcard balance
					if (contactsLoyalty.getGiftcardBalance() == null) {
						contactsLoyalty.setGiftcardBalance(convertedAmount);
					} else {
						contactsLoyalty.setGiftcardBalance(
								new BigDecimal(contactsLoyalty.getGiftcardBalance() + convertedAmount)
										.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

					}
					if (contactsLoyalty.getTotalGiftcardAmount() == null) {
						contactsLoyalty.setTotalGiftcardAmount(convertedAmount);
					} else {
						contactsLoyalty.setTotalGiftcardAmount(
								new BigDecimal(contactsLoyalty.getTotalGiftcardAmount() + convertedAmount)
										.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					}

					// deduct loyalty points
					contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() - subPoints);
					contactsLoyalty
							.setTotalLoyaltyRedemption(contactsLoyalty.getTotalLoyaltyRedemption() == null ? subPoints
									: contactsLoyalty.getTotalLoyaltyRedemption() + subPoints);

					logger.info("contactsLoyalty.getGiftcardBalance() = " + contactsLoyalty.getGiftcardBalance());

					deductPointsFromExpiryTable(contactsLoyalty, subPoints, convertedAmount);
				}
			}

		} catch (Exception e) {
			logger.error("Exception while applying auto conversion rules...", e);
			return null;
		}
		return differenceArr;
	}
	private void deductPointsFromExpiryTable(ContactsLoyalty contactsLoyalty, double subPoints, double earnedAmt)
			throws Exception {


		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML) ServiceLocator
				.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
		List<LoyaltyTransactionExpiry> expiryList = null; // expiryDao.fetchExpPointsTrans(Constants.STRING_NILL+membershipNumber,
															// 100, userId);
		Iterator<LoyaltyTransactionExpiry> iterList = null; // expiryList.iterator();
		LoyaltyTransactionExpiry expiry = null;
		long remainingPoints = (long) subPoints;

		do {

			expiryList = expiryDao.fetchExpLoyaltyPtsTrans(contactsLoyalty.getLoyaltyId(), 100,
					contactsLoyalty.getUserId());
			// logger.info("expiryList size = "+expiryList.size());
			if (expiryList == null)
				break;
			iterList = expiryList.iterator();

			while (iterList.hasNext()) {

				logger.info("remainingPoints = " + remainingPoints + " earnedAmt = " + earnedAmt);
				expiry = iterList.next();

				// logger.info("expiry points= "+expiry.getExpiryPoints()+" expiry amount =
				// "+expiry.getExpiryAmount());

				if ((expiry.getExpiryPoints() == null || expiry.getExpiryPoints() <= 0)
						&& (expiry.getExpiryAmount() == null || expiry.getExpiryAmount() <= 0)) {
					logger.info("Wrong entry condition...");
				} else if (expiry.getExpiryPoints() < remainingPoints) {
					logger.info("subtracted points = " + expiry.getExpiryPoints());
					remainingPoints = remainingPoints - expiry.getExpiryPoints().longValue();
					expiry.setExpiryPoints(0l);
					// expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					continue;

				} else if (expiry.getExpiryPoints() >= remainingPoints) {
					logger.info("subtracted points = " + expiry.getExpiryPoints());
					expiry.setExpiryPoints(expiry.getExpiryPoints() - remainingPoints);
					remainingPoints = 0;
					if (expiry.getExpiryAmount() == null) {
						expiry.setExpiryAmount(earnedAmt);
					} else {
						expiry.setExpiryAmount(expiry.getExpiryAmount() + earnedAmt);
					}
					// logger.info("expiry.getExpiryAmount() = "+expiry.getExpiryAmount()+ "
					// earnedAmt = "+earnedAmt);
					// expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					// logger.info("expiry.getExpiryAmount() = "+expiry.getExpiryAmount()+ "
					// earnedAmt = "+earnedAmt);
					break;
				}

			}

		} while (remainingPoints > 0 && expiryList != null);

	
	}

	private ContactsLoyalty findMembershpByCard(String cardNumber, Long programId, Long userId) throws Exception{
		logger.debug("in find findMembershpByCard ");
		ContactsLoyalty loyalty = null;
		ContactsLoyaltyDao contactLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		loyalty = contactLoyaltyDao.findByProgram(cardNumber, programId, userId);
		return loyalty;
	}
	
	
	private Users getUser(String userName, String orgId, String userToken) throws Exception{
		logger.debug("in getUser ");
		String completeUserName = userName+Constants.USER_AND_ORG_SEPARATOR+orgId;
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Users user = usersDao.findUserByToken(completeUserName, userToken);
		return user;
	}
	private Status validateTransferJsonData(LoyaltyTransferMembershipJsonRequest loyaltyTransferRequest) throws Exception{
		logger.info("Entered validateTransferJsonData method >>>>");
		
		Status status = null;
		if(loyaltyTransferRequest == null ){
			status = new Status(
					"101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(loyaltyTransferRequest.getUser() == null){
			status = new Status(
					"101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(loyaltyTransferRequest.getMembership() == null){
			status = new Status(
					"101004", PropertyUtil.getErrorMessage(101004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(loyaltyTransferRequest.getTransferSource() == null){
			status = new Status(
					"101004", PropertyUtil.getErrorMessage(101004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(loyaltyTransferRequest.getUser().getUserName() == null || loyaltyTransferRequest.getUser().getUserName().trim().length() <=0 || 
				loyaltyTransferRequest.getUser().getOrganizationId() == null || loyaltyTransferRequest.getUser().getOrganizationId().trim().length() <=0 || 
						loyaltyTransferRequest.getUser().getToken() == null || loyaltyTransferRequest.getUser().getToken().trim().length() <=0) {
			status = new Status("1012", PropertyUtil.getErrorMessage(1012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(loyaltyTransferRequest.getHeader().getStoreNumber() == null || loyaltyTransferRequest.getHeader().getStoreNumber().length() <= 0){
			status = new Status("111501", PropertyUtil.getErrorMessage(111501, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(loyaltyTransferRequest.getMembership().getCardNumber() != null && loyaltyTransferRequest.getMembership().getCardNumber().trim().isEmpty() ){
			status = new Status(
					"111571", PropertyUtil.getErrorMessage(111571, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}	
		if(loyaltyTransferRequest.getTransferSource().getCardNumber() != null && loyaltyTransferRequest.getTransferSource().getCardNumber().trim().isEmpty() ){
			status = new Status(
					"111572", PropertyUtil.getErrorMessage(111572, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}	
		return status;
	}
	
	/*private ContactsLoyalty transferMembership(ContactsLoyalty sourceMembership, 
			ContactsLoyalty destMembership, String mbershipNumber, String cardPin, Long cardsetID, String storeNumber, boolean merge,String empId,String termId) throws Exception {*/
		private ContactsLoyalty transferMembership(ContactsLoyalty sourceMembership, 
				ContactsLoyalty destMembership, String mbershipNumber, String cardPin, Long cardsetID, String subsidiaryNumber, String storeNumber, boolean merge,String empId,String termId, Long destProgramId) throws Exception {
		//logger.info("Entered prepareContactsLoyaltyObject >>>>>");
		if(destMembership == null) destMembership = new ContactsLoyalty();
		if(!merge) {
			
			destMembership.setCardNumber(mbershipNumber);
			destMembership.setMembershipType(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD);
			destMembership.setMobilePhone(sourceMembership.getMobilePhone());
			destMembership.setCardPin(cardPin);
			destMembership.setCreatedDate(Calendar.getInstance());//check
			destMembership.setContactLoyaltyType(Constants.CONTACT_LOYALTY_TYPE_POS);
			destMembership.setSourceType(sourceMembership.getSourceType());
			destMembership.setSubsidiaryNumber(subsidiaryNumber != null && !subsidiaryNumber.trim().isEmpty() ? subsidiaryNumber.trim() : null);
			destMembership.setPosStoreLocationId(storeNumber);
			destMembership.setEmpId(empId!=null && !empId.trim().isEmpty() ? empId.trim():null);
			destMembership.setTerminalId(termId!=null && !termId.trim().isEmpty() ? termId.trim():null);
			destMembership.setMembershipStatus(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE);
			destMembership.setMode(OCConstants.LOYALTY_ONLINE_MODE);//check
			destMembership.setOptinDate(Calendar.getInstance());//check
			//destMembership.setProgramId(sourceMembership.getProgramId());
			//for cross program card transfer
			if(sourceMembership.getProgramId() != destProgramId){
				destMembership.setProgramId(destProgramId);
			}else{
				destMembership.setProgramId(sourceMembership.getProgramId());
			}
			destMembership.setBonusCurrencyIterator(sourceMembership.getBonusCurrencyIterator());
			destMembership.setBonusCurrencyThreshold(sourceMembership.getBonusCurrencyThreshold());
			destMembership.setBonusPointsIterator(sourceMembership.getBonusPointsIterator());
			destMembership.setBonusPointsThreshold(sourceMembership.getBonusPointsThreshold());
			//destMembership.setCummulativePurchaseValue(sourceMembership.getCummulativePurchaseValue());
			//destMembership.setCummulativeReturnValue(sourceMembership.getCummulativeReturnValue());
			destMembership.setAmountToIgnore(sourceMembership.getAmountToIgnore());
			destMembership.setLastThreshold(sourceMembership.getLastThreshold());
			
			destMembership.setCardSetId(cardsetID);
			destMembership.setServiceType(OCConstants.LOYALTY_SERVICE_TYPE_OC);
			destMembership.setContact(sourceMembership.getContact());
			destMembership.setUserId(sourceMembership.getUserId());
			destMembership.setCustomerId(sourceMembership.getCustomerId());
			destMembership.setIsRegistered(sourceMembership.getIsRegistered());
			destMembership.setLastLoggedInTime(sourceMembership.getLastLoggedInTime());
			destMembership.setLastRedumptionDate(sourceMembership.getLastRedumptionDate());
			destMembership.setMembershipPwd(sourceMembership.getMembershipPwd());
			destMembership.setOrgId(sourceMembership.getOrgId());
			destMembership.setTierUpgradedDate(sourceMembership.getTierUpgradedDate());//not required i guess
			destMembership.setTierUpgradeReason(sourceMembership.getTierUpgradeReason());//not required i guess
			
		}
		else {
			
			destMembership.setMobilePhone(destMembership.getMobilePhone() == null ? sourceMembership.getMobilePhone() : destMembership.getMobilePhone());
			
		}
//		destMembership.setOptinMedium(optInMedium);
		
		return destMembership;
	}
	
	
	private LoyaltyTransferMembershipJsonResponse prepareTransferResponse(ResponseHeader header, MembershipResponse membershipResponse,
			List<Balance> balances, HoldBalance holdBalance, List<MatchedCustomer> matchedCustomers, Status status) throws BaseServiceException {
		LoyaltyTransferMembershipJsonResponse transferResponse = new LoyaltyTransferMembershipJsonResponse();
		transferResponse.setHeader(header);
		
		if(membershipResponse == null){
			membershipResponse = new MembershipResponse();
			membershipResponse.setCardNumber(Constants.STRING_NILL);
			membershipResponse.setCardPin(Constants.STRING_NILL);
			membershipResponse.setExpiry(Constants.STRING_NILL);
			membershipResponse.setPhoneNumber(Constants.STRING_NILL);
			membershipResponse.setTierLevel(Constants.STRING_NILL);
			membershipResponse.setTierName(Constants.STRING_NILL);
		}
		if(balances == null){
			balances = new ArrayList<Balance>();
		}
		if(holdBalance == null){
			holdBalance = new HoldBalance();
			holdBalance.setActivationPeriod(Constants.STRING_NILL);
			holdBalance.setCurrency(Constants.STRING_NILL);
			holdBalance.setPoints(Constants.STRING_NILL);
		}
		if(matchedCustomers == null){
			matchedCustomers = new ArrayList<MatchedCustomer>();
		}
		transferResponse.setMembership(membershipResponse);
		transferResponse.setBalances(balances);
		transferResponse.setHoldBalance(holdBalance);
		transferResponse.setMatchedCustomers(matchedCustomers);
		transferResponse.setStatus(status);
		return transferResponse;
	}
	
	
	private LoyaltyTransactionChild createSuccessfulTransaction(LoyaltyTransferMembershipJsonRequest loyaltyTransferRequest, ContactsLoyalty contactLoyalty, ContactsLoyalty destContactLoyalty, ResponseHeader responseHeader, Long userId, Long orgId, 
			Long programId, String membershipType,  String desc, String storeNumber, String desc2,String empId, String termId) throws Exception{
		
		LoyaltyTransactionChild transaction = null;
		try{
		
			transaction = new LoyaltyTransactionChild();
			transaction.setTransactionId(Long.valueOf(responseHeader.getTransactionId()));
			transaction.setMembershipNumber(Constants.STRING_NILL+contactLoyalty.getCardNumber());
			transaction.setMembershipType(membershipType);
			transaction.setCreatedDate(Calendar.getInstance());
			transaction.setOrgId(orgId);
			transaction.setAmountBalance(destContactLoyalty.getGiftcardBalance());
			transaction.setPointsBalance(destContactLoyalty.getLoyaltyBalance());
			transaction.setGiftBalance(destContactLoyalty.getGiftBalance());
			transaction.setAmountDifference(contactLoyalty.getGiftcardBalance() != null ? contactLoyalty.getGiftcardBalance()+Constants.STRING_NILL : null);
			transaction.setPointsDifference(contactLoyalty.getLoyaltyBalance() != null ? contactLoyalty.getLoyaltyBalance()+Constants.STRING_NILL : null);
			transaction.setGiftDifference(contactLoyalty.getGiftBalance() != null ? contactLoyalty.getGiftBalance()+Constants.STRING_NILL : null);
			transaction.setHoldAmount(destContactLoyalty.getHoldAmountBalance());
			transaction.setHoldPoints(destContactLoyalty.getHoldPointsBalance());
			transaction.setProgramId(programId);
			transaction.setTierId(contactLoyalty.getProgramTierId());
			transaction.setUserId(userId);
			transaction.setOrgId(contactLoyalty.getOrgId());
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_TRANSFER);
			transaction.setSubsidiaryNumber(loyaltyTransferRequest.getHeader().getSubsidiaryNumber() != null && !loyaltyTransferRequest.getHeader().getSubsidiaryNumber().trim().isEmpty() ? loyaltyTransferRequest.getHeader().getSubsidiaryNumber().trim() : null);
			transaction.setStoreNumber(storeNumber);
			transaction.setEmployeeId(empId!=null && !empId.trim().isEmpty() ? empId.trim():null);
			transaction.setTerminalId(termId!=null && !termId.trim().isEmpty() ? termId.trim():null);
			transaction.setCardSetId(contactLoyalty.getCardSetId());
			//transaction.setSource(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE);
			transaction.setSourceType(loyaltyTransferRequest.getHeader().getSourceType());
			transaction.setDescription(desc);
			transaction.setDescription2(desc2);
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
	private Contacts findContactById(Long cid) throws Exception {

		ContactsDao contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		return contactsDao.findById(cid);
	}

	private void updateTransactionStatus(LoyaltyTransactionChild transaction, String status) throws Exception {
		LoyaltyTransactionChildDaoForDML LoyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML) ServiceLocator
				.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
		transaction.setEarnStatus(status);
		LoyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
	}
	private ContactsLoyalty getDestMembershipIfAny(ContactsLoyalty contactLoyalty) throws Exception{
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		if(contactLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED) && contactLoyalty.getTransferedTo() != null) {
			return loyaltyDao.findAllByLoyaltyId(contactLoyalty.getTransferedTo());
			
		}
		
		return null;
	}
}
