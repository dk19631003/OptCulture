package org.mq.optculture.business.loyalty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.LoyaltyTransactionParent;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDao;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDaoForDML;
import org.mq.optculture.data.dao.ReIssuePerksOnExpiryDao;
import org.mq.optculture.data.dao.ReIssuePerksOnExpiryDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.loyalty.IssuanceInfo;
import org.mq.optculture.model.ocloyalty.Amount;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.model.ocloyalty.Customer;
import org.mq.optculture.model.ocloyalty.HoldBalance;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyUser;
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.ocloyalty.MembershipRequest;
import org.mq.optculture.model.ocloyalty.MembershipResponse;
import org.mq.optculture.model.ocloyalty.RequestHeader;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;

public class ReIssuePerksServiceBusinessImpl  implements ReIssuePerksBusinessService  {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public void processReIssuePerksRequest(Long tierId, Long prgmId) {
		
		try {
			ReIssuePerksOnExpiryDao reIssuePerkDao = (ReIssuePerksOnExpiryDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REISSUE_ON_EXPIRY_DAO);
			
			//1.find the totalcount   
			//2.chunk size is 100 
			//loop for every 100 get those 100 till the count is reached 
			logger.info("inside processReIssuePerksRequest >>>> tier id "+tierId+" prgmid"+prgmId);
			long totalCount = reIssuePerkDao.findTheMembersToReissue(prgmId, tierId);
			
			int threshold=100;
			 int initialIndex=0;
			 long num_of_chunks= 1;
			 if(totalCount > threshold){
				 
				  num_of_chunks=(totalCount/threshold);
				 if(totalCount<threshold)
				 {
					 num_of_chunks=1;
				 }
				 else if((totalCount%threshold)>0){
					 num_of_chunks=(totalCount/threshold)+1;
				 }
				 else
				 {
					 num_of_chunks=(totalCount/threshold);
				 }
				
				 
			 }
			 
			logger.info("num_of_chunks>>>"+num_of_chunks);
			 for(int loop_var=1;loop_var<=num_of_chunks;loop_var++)
			 {
				 logger.info("inside for num_of_chunks>>>"+loop_var);
				List<Long> loyaltyList = reIssuePerkDao.findLoyaltyIdList(prgmId, tierId, initialIndex, threshold);
				String loyaltyidsStr = "";
				
				if(loyaltyList == null || loyaltyList.size() == 0){
					logger.info(" loyalty id list is empty >>>"+loyaltyList.size());
					return;
				 }
				
				for(Long loyaltyid : loyaltyList) {
					if(!loyaltyidsStr.isEmpty()) loyaltyidsStr += Constants.DELIMETER_COMMA;
					loyaltyidsStr += loyaltyid.toString();
					
					logger.info("loyaltyidsStr>>>"+loyaltyidsStr);
					
				}
					
				ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
				List<ContactsLoyalty> contactltyList = contactsLoyaltyDao.findAllByLoyaltyIdStr(loyaltyidsStr);
				if(contactltyList == null || contactltyList.isEmpty()) return;
				for (ContactsLoyalty contactsLoyalty : contactltyList) {
					
					logger.info("before prepareAndIssuePerks>>>");
					boolean successfullyissued = prepareAndIssuePerks(contactsLoyalty);
					logger.info("after prepareAndIssuePerks>>>");
					
				}
				
				 initialIndex =(loop_var*threshold)+1;
			}
			
			
		} catch (Exception e) {
			
		}
		
		try {
			ReIssuePerksOnExpiryDaoForDML reIssuePerkDaoForDML = (ReIssuePerksOnExpiryDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.REISSUE_ON_EXPIRY_DAOForDML);
		
		long delCount = reIssuePerkDaoForDML.deleteSuccessfullTrx(prgmId, tierId, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		
        } catch (Exception e) {
			
		}
		 
		return;
	}
	private boolean prepareAndIssuePerks(ContactsLoyalty contactsLoyalty ) {
	
		        logger.info("inside prepareAndIssuePerks contactsLoyalty>>>");
				LoyaltyIssuanceRequest issuanceRequest = new LoyaltyIssuanceRequest();
				LoyaltyIssuanceResponse issuanceResponse = new LoyaltyIssuanceResponse();
				
				RequestHeader header= new RequestHeader();
				MembershipRequest membershipRequest =new MembershipRequest();
				Amount amount = new Amount();
				LoyaltyUser loyaltyUser =new LoyaltyUser();
				Customer customer = new Customer();
				
				try {
					Calendar syscal = new MyCalendar(Calendar.getInstance(), null,
							MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
					UsersDao usersDao;
					usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
					Users user=usersDao.findByUserId(contactsLoyalty.getUserId());
					String requestId = OCConstants.RE_ISSUE_PERKS_REQUEST_ID+user.getToken()+"_"+System.currentTimeMillis();
					header.setRequestId(requestId);
					header.setRequestDate(syscal.toString());
					header.setStoreNumber(contactsLoyalty.getPosStoreLocationId());
				    header.setDocSID("");
				    //header.setReceiptNumber(enrollRequest.getHeader().getReceiptNumber());
				    header.setSubsidiaryNumber(contactsLoyalty.getSubsidiaryNumber());
				    header.setSourceType(contactsLoyalty.getSourceType());
				    header.setEmployeeId(contactsLoyalty.getEmpId());
				    header.setTerminalId(contactsLoyalty.getTerminalId());
				    
				    
				    if(contactsLoyalty.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
				    	  membershipRequest.setPhoneNumber(contactsLoyalty.getCardNumber());
				    	  membershipRequest.setCardNumber("");
				    	  logger.info("inside membership type mobile>>>"+contactsLoyalty.getMembershipType());
				      }else {
				    	membershipRequest.setCardNumber(contactsLoyalty.getCardNumber());
						membershipRequest.setCardPin(contactsLoyalty.getCardPin());
						membershipRequest.setPhoneNumber("");
						logger.info("inside membership type card>>>"+contactsLoyalty.getMembershipType()+" card number>>>"+contactsLoyalty.getCardNumber());
				      }
				    
				    amount.setType(OCConstants.LOYALTY_TYPE_REWARD);
				    amount.setValueCode(OCConstants.LOYALTY_TYPE_PERKS);
				    
				    //double definedValue = tier.getEarnValue()!=null?tier.getEarnValue():0.0;
				    //long definedMonths = tier.getRewardExpiryDateValue();
				    /*long currentMonth = syscal.get(Calendar.MONTH);
				    
				    LoyaltyProgramTier tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
				    Double multipleFactordbl = tier.getEarnValue()/tier.getRewardExpiryDateValue();
				    long multipleFactor = multipleFactordbl.intValue();
				    double perkstobegiven = multipleFactor*(tier.getRewardExpiryDateValue()-currentMonth);*/
				    
				    LoyaltyProgramTier tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
				    double perkstobegiven = 0.0;
				    if(tier!=null) {
				    	perkstobegiven = tier.getEarnValue();//while reisue we give 100%
				    }
				    
				    
				    //amount.setReceiptAmount(String.valueOf(enteredValue));
				    amount.setEnteredValue(String.valueOf(Math.abs(perkstobegiven)));
				    
					
					customer.setCustomerId(contactsLoyalty.getCustomerId());
					//customer.setFirstName(enrollRequest.getCustomer().getFirstName());
					//customer.setLastName(enrollRequest.getCustomer().getLastName());
					customer.setPhone(contactsLoyalty.getMobilePhone());
					customer.setEmailAddress(contactsLoyalty.getEmailId());
					//customer.setBirthday(enrollRequest.getCustomer().getBirthday());
					//customer.setAddressLine1(enrollRequest.getCustomer().getAddressLine1()); 
					//customer.setCity(enrollRequest.getCustomer().getCity()); 
					//customer.setState(enrollRequest.getCustomer().getState());
					//customer.setPostal(enrollRequest.getCustomer().getPostal());
					//customer.setAnniversary(enrollRequest.getCustomer().getAnniversary());
					
					//String tokenOrOptSync = user.getToken()!=null && !user.getToken().isEmpty()?user.getToken():user.getUserOrganization().getOptSyncKey();
					loyaltyUser.setUserName(Utility.getOnlyUserName(user.getUserName()));
					loyaltyUser.setOrganizationId(user.getUserOrganization().getOrgExternalId());
					loyaltyUser.setToken(user.getUserOrganization().getOptSyncKey());
					
					
					issuanceRequest.setHeader(header);
					issuanceRequest.setMembership(membershipRequest);
					issuanceRequest.setAmount(amount);
					issuanceRequest.setCustomer(customer);
					issuanceRequest.setUser(loyaltyUser);
				    
					LoyaltyTransaction transaction = null;
					//transaction = findTransactionByRequestId(requestId);
					LoyaltyTransactionParent tranParent = createNewTransaction(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE); 
					Date date = tranParent.getCreatedDate().getTime();
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
					String transDate = df.format(date);
					String responseJson ="";
					String reqJson="";
					Status status1 = null;
					Gson gson = new Gson();
					String userName = null;
					ResponseHeader responseHeader;
					try{
						responseHeader = new ResponseHeader();
						responseHeader.setRequestDate("");
						responseHeader.setRequestId("");
						responseHeader.setTransactionDate(transDate);
						responseHeader.setTransactionId(""+tranParent.getTransactionId());
						reqJson = gson.toJson(issuanceRequest, LoyaltyIssuanceRequest.class);
						//if(transaction == null){
							transaction = logIssuanceTransactionRequest(issuanceRequest, reqJson, OCConstants.LOYALTY_OFFLINE_MODE);
						//}
						issuanceRequest.setJsonValue(reqJson);
						logger.info("request json "+reqJson);
						//send issuance request
						LoyaltyIssuanceOCService loyaltyIssuanceOCService = (LoyaltyIssuanceOCService) ServiceLocator.getInstance()
								.getServiceByName(OCConstants.LOYALTY_ISSUANCE_OC_BUSINESS_SERVICE);
						issuanceResponse = loyaltyIssuanceOCService.processIssuanceRequest(issuanceRequest,
								OCConstants.LOYALTY_OFFLINE_MODE,transaction.getId().toString(),transDate,
								OCConstants.DR_TO_LTY_EXTRACTION);
						responseJson = new Gson().toJson(issuanceResponse, LoyaltyIssuanceResponse.class);	
						logger.info("Response = "+responseJson);
						if(responseJson!=null){
							updateIssuanceTransactionStatus(transaction,responseJson,issuanceResponse);
							ReIssuePerksOnExpiryDaoForDML reIssuePerkDaoForDML = (ReIssuePerksOnExpiryDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.REISSUE_ON_EXPIRY_DAOForDML);
							String status = OCConstants.JSON_RESPONSE_FAILURE_MESSAGE;
							if(issuanceResponse.getStatus().toString().equalsIgnoreCase("0")) {
								
								status = OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE;
								
							}
							long count = reIssuePerkDaoForDML.updateReIssueExpTrxIdAndStatus(contactsLoyalty.getLoyaltyId(), transaction.getId(), status, contactsLoyalty.getProgramId(), contactsLoyalty.getProgramTierId());
							
							return true;
						}
					}catch(Exception e){
						logger.error("Error in issuance restservice", e);
						responseHeader = null;
						if(issuanceResponse == null){
							responseHeader = new ResponseHeader();
							responseHeader.setRequestDate("");
							responseHeader.setRequestId("");
							responseHeader.setTransactionDate(transDate);
							responseHeader.setTransactionId(""+tranParent.getTransactionId());
						}
						else{
							responseHeader = issuanceResponse.getHeader();
						}
						
						status1 = new Status("101000", PropertyUtil.getErrorMessage(101000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status1);
						responseJson = gson.toJson(issuanceResponse);
						updateIssuanceTransaction(tranParent, issuanceResponse, userName);
						logger.info("Response = "+responseJson);
						logger.info("Ended Loyalty Issuance Rest Service... at "+System.currentTimeMillis());
						
					}finally{
						logger.info("Response = "+responseJson);
						//send alert mail for falures
						/*if(!issuanceResponse.getStatus().getErrorCode().equalsIgnoreCase("0")) {
							Utility.sendDRToLtyFailureMail(receipt.getDocSID(),receipt.getDocDate(), receipt.getDocTime(),user,OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE,issuanceResponse.getStatus().getMessage());
						}*/
						logger.info("Completed Loyalty Issuance Rest Service.");
					}
				}catch(Exception e){
					logger.error("Exception ",e);
				}
			return false;
	}
	
	
    public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException {
		
		return null;
	}
	
    private LoyaltyProgramTier getLoyaltyTier(Long tierId) throws Exception{
		
		LoyaltyProgramTierDao tierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		return tierDao.getTierById(tierId);
		
	}
    
    public LoyaltyTransaction findTransactionByRequestId(String requestId){
		LoyaltyTransaction transaction = null;
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			transaction = loyaltyTransactionDao.findByRequestId(requestId, OCConstants.LOYALTY_SERVICE_TYPE_OC);
		}catch(Exception e){
			logger.error("Exception in find transaction by requestid", e);
		}
		return transaction;
	}
   private LoyaltyTransactionParent createNewTransaction(String type){
		
		LoyaltyTransactionParent tranx  = null; 
		try{
			LoyaltyTransactionParentDao parentDao = (LoyaltyTransactionParentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO);
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);
			tranx = new LoyaltyTransactionParent();
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getDefault());
			tranx.setCreatedDate(cal);
			tranx.setTransactionType(type);
			//parentDao.saveOrUpdate(tranx);
			parentDaoForDML.saveOrUpdate(tranx);

		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
		return tranx;
	}
   
   private LoyaltyTransaction logIssuanceTransactionRequest(LoyaltyIssuanceRequest requestObject, String jsonRequest, String mode){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		LoyaltyTransaction transaction = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction = new LoyaltyTransaction();
			transaction.setJsonRequest(jsonRequest);
			transaction.setRequestId(requestObject.getHeader().getRequestId());
			transaction.setPcFlag(Boolean.valueOf(requestObject.getHeader().getPcFlag()));
			transaction.setMode(mode);//online or offline
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_ISSUANCE);
			transaction.setUserDetail(requestObject.getUser().getUserName()+"__"+requestObject.getUser().getOrganizationId());
			transaction.setDocSID(requestObject.getHeader().getDocSID().trim());
			transaction.setStoreNumber(requestObject.getHeader().getStoreNumber().trim());
			transaction.setEmployeeId(requestObject.getHeader().getEmployeeId()!=null && !requestObject.getHeader().getEmployeeId().trim().isEmpty() ? requestObject.getHeader().getEmployeeId().trim():null);
			transaction.setTerminalId(requestObject.getHeader().getTerminalId()!=null && !requestObject.getHeader().getTerminalId().trim().isEmpty() ? requestObject.getHeader().getTerminalId().trim():null);
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
		}
		return transaction;
	}
   
    private void updateIssuanceTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyIssuanceResponse response){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			transaction.setRequestStatus(response.getStatus().getStatus());
			transaction.setJsonResponse(responseJson);
			if(response.getMembership() != null && response.getMembership().getCardNumber() != null 
					&& !response.getMembership().getCardNumber().trim().isEmpty()){
				transaction.setCardNumber(response.getMembership().getCardNumber());
			}
			else{
				transaction.setCardNumber(response.getMembership() == null ? "" : response.getMembership().getPhoneNumber());
			}
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}
    private LoyaltyIssuanceResponse prepareIssuanceResponse(ResponseHeader header, MembershipResponse membershipResponse,
			List<Balance> balances, HoldBalance holdBalance, List<MatchedCustomer> matchedCustomers, Status status) throws BaseServiceException {
		LoyaltyIssuanceResponse issuanceResponse = new LoyaltyIssuanceResponse();
		issuanceResponse.setHeader(header);
		if(membershipResponse == null){
			membershipResponse = new MembershipResponse();
			membershipResponse.setCardNumber("");
			membershipResponse.setCardPin("");
			membershipResponse.setExpiry("");
			membershipResponse.setPhoneNumber("");
			membershipResponse.setTierLevel("");
			membershipResponse.setTierName("");
		}
		if(balances == null){
			balances = new ArrayList<Balance>();
		}
		if(holdBalance == null){
			holdBalance = new HoldBalance();
			holdBalance.setActivationPeriod("");
			holdBalance.setCurrency("");
			holdBalance.setPoints("");
		}
		if(matchedCustomers == null){
			matchedCustomers = new ArrayList<MatchedCustomer>();
		}
		issuanceResponse.setMembership(membershipResponse);
		issuanceResponse.setBalances(balances);
		issuanceResponse.setHoldBalance(holdBalance);
		issuanceResponse.setMatchedCustomers(matchedCustomers);
		issuanceResponse.setStatus(status);
		return issuanceResponse;
	}
    private void updateIssuanceTransaction(LoyaltyTransactionParent trans, LoyaltyIssuanceResponse responseObject, String userName) {
		try{
			LoyaltyTransactionParentDao parentDao = (LoyaltyTransactionParentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO);
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);
			if(userName != null){
				trans.setUserName(userName);
			}
			if(responseObject == null) return;
			if(responseObject.getStatus() != null) {
				trans.setStatus(responseObject.getStatus().getStatus());
				trans.setErrorMessage(responseObject.getStatus().getMessage());
			}
			if(responseObject.getHeader() != null){
				trans.setRequestId(responseObject.getHeader().getRequestId());
				trans.setRequestDate(responseObject.getHeader().getRequestDate());
			}
			if(responseObject.getMembership() != null) {
				if(responseObject.getMembership().getCardNumber() != null && !responseObject.getMembership().getCardNumber().trim().isEmpty()){
					trans.setMembershipNumber(responseObject.getMembership().getCardNumber());
				}
				else{
					trans.setMembershipNumber(responseObject.getMembership().getPhoneNumber());
				}
			}
			if(responseObject.getMatchedCustomers() != null && responseObject.getMatchedCustomers().size() >= 1 && responseObject.getMatchedCustomers().get(0) != null) {
				trans.setMobilePhone(responseObject.getMatchedCustomers().get(0).getPhone());
			}
			//parentDao.saveOrUpdate(trans);
			parentDaoForDML.saveOrUpdate(trans);
		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
	}

}


