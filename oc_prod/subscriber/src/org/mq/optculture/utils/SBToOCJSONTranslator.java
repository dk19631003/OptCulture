package org.mq.optculture.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyCards;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDomainsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.data.dao.LoyaltyCardsDao;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.loyalty.AmountDetails;
import org.mq.optculture.model.loyalty.CustomerInfo;
import org.mq.optculture.model.loyalty.EnrollmentInfo;
import org.mq.optculture.model.loyalty.HeaderInfo;
import org.mq.optculture.model.loyalty.IssuanceInfo;
import org.mq.optculture.model.loyalty.LoyaltyEnrollRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyEnrollResponseObject;
import org.mq.optculture.model.loyalty.LoyaltyInquiryRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceResponseObject;
import org.mq.optculture.model.loyalty.LoyaltyRedemptionRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyRedemptionResponseObject;
import org.mq.optculture.model.loyalty.StatusInfo;
import org.mq.optculture.model.loyalty.UserDetails;
import org.mq.optculture.model.ocloyalty.Amount;
import org.mq.optculture.model.ocloyalty.Customer;
import org.mq.optculture.model.ocloyalty.Discounts;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyInquiryRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyRedemptionRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyRedemptionResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyUser;
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.ocloyalty.MembershipRequest;
import org.mq.optculture.model.ocloyalty.MembershipResponse;
import org.mq.optculture.model.ocloyalty.Promotion;
import org.mq.optculture.model.ocloyalty.RequestHeader;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.SkuDetails;
import org.mq.optculture.model.ocloyalty.Status;
import org.zkoss.zkplus.spring.SpringUtil;

public class SBToOCJSONTranslator {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	//private String trxType;

	public SBToOCJSONTranslator() {}
	
	/*public String getTrxType() {
		return trxType;
	}

	public void setTrxType(String trxType) {
		this.trxType = trxType;
	}*/
private LoyaltyCards findLoyaltyCardByUserId(String cardNumber, Long userId) throws Exception {
		
		LoyaltyCardsDao loyaltyCardDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		return loyaltyCardDao.findByCardNoAnduserId(cardNumber, userId);
		
	}
	public Object convertSbReqToOC(BaseRequestObject sourceSBJSONReqObj, String trxType) {
		
		if(trxType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_ENROLMENT)) {
			
			LoyaltyEnrollRequestObject loyaltyEnroRequestObject = (LoyaltyEnrollRequestObject)sourceSBJSONReqObj;
			return convertEnrollmentRequest(loyaltyEnroRequestObject);
			
		}else if(trxType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_ISSUANCE)){
			
			LoyaltyIssuanceRequestObject loyaltyIssuanceRequestObject = (LoyaltyIssuanceRequestObject)sourceSBJSONReqObj;
			return convertIssuanceRequest(loyaltyIssuanceRequestObject);
			
		}else if(trxType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_REDEMPTION)){
			LoyaltyRedemptionRequestObject LoyaltyRedemptionRequestObject = (LoyaltyRedemptionRequestObject)sourceSBJSONReqObj;
			return convertRedemptionRequest( LoyaltyRedemptionRequestObject);
			
		}else if(trxType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_INQUIRY)){
			LoyaltyInquiryRequestObject LoyaltyInquiryRequestObject = (LoyaltyInquiryRequestObject)sourceSBJSONReqObj;
			return convertInquiryRequest( LoyaltyInquiryRequestObject);
		}
		return null;
		
	}
	
	public StatusInfo convertStatus(Status status) {
		
		
		StatusInfo statusInfo = new StatusInfo(status.getErrorCode(), status.getMessage(), status.getStatus());
		return statusInfo;
		
	}
	
	public BaseResponseObject convertOCResponseToSB(Object sourceOCJSONRespObj, String trxType) {
		
		if(trxType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_ENROLMENT)) {
			
			LoyaltyEnrollResponse loyaltyEnrollResponse = (LoyaltyEnrollResponse)sourceOCJSONRespObj;
			return convertEnrollmentResponse(loyaltyEnrollResponse);
			
		}else if(trxType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_ISSUANCE)){
			
			LoyaltyIssuanceResponse loyaltyIssuanceResponse = (LoyaltyIssuanceResponse)sourceOCJSONRespObj;
			return convertIssuanceResponse(loyaltyIssuanceResponse);
			
		}else if(trxType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_REDEMPTION)){
			LoyaltyRedemptionResponse loyaltyRedemptionResponse = (LoyaltyRedemptionResponse)sourceOCJSONRespObj;
			return convertRedemptionResponse(loyaltyRedemptionResponse);
		}
		return null;
		
	}
	
	public LoyaltyEnrollResponseObject convertEnrollmentResponse(LoyaltyEnrollResponse loyaltyEnrollResponse){
		
		LoyaltyEnrollResponseObject loyaltyEnrollResponseObject = new LoyaltyEnrollResponseObject();
		
		List<MatchedCustomer> matchedCustomers = loyaltyEnrollResponse.getMatchedCustomers();
		MatchedCustomer matchedCustomer = matchedCustomers.get(0);//TODO confirm at least one customer will be there inside it.
		
		CustomerInfo customerInfo = new CustomerInfo();
		customerInfo.setCUSTOMERID(matchedCustomer.getCustomerId());
		customerInfo.setEMAIL(matchedCustomer.getEmailAddress());
		customerInfo.setPHONE(matchedCustomer.getPhone());
		customerInfo.setFIRSTNAME(matchedCustomer.getFirstName());
		customerInfo.setLASTNAME(matchedCustomer.getLastName());
		customerInfo.setADDRESS1("");
		customerInfo.setADDRESS2("");
		
		loyaltyEnrollResponseObject.setCUSTOMERINFO(customerInfo);
		Status statusInfo = loyaltyEnrollResponse.getStatus();
		StatusInfo status = new StatusInfo(statusInfo.getErrorCode(), statusInfo.getMessage(), statusInfo.getStatus());
		loyaltyEnrollResponseObject.setSTATUS(status);
		
		AmountDetails amountDetails = new AmountDetails();
		amountDetails.setENTEREDAMOUNT("");
		amountDetails.setVALUECODE("");
		loyaltyEnrollResponseObject.setAMOUNTDETAILS(amountDetails);
		
		
		/*EnrollmentInfo enrollmentInfo = new EnrollmentInfo();
		MembershipResponse membershipResponse = loyaltyEnrollResponse.getMembership();
		enrollmentInfo.setCARDNUMBER(membershipResponse.getCardNumber());
		enrollmentInfo.setCARDPIN(membershipResponse.getCardPin());
		enrollmentInfo.set*/
		
		
		/*HeaderInfo headerInfo = new HeaderInfo();
		ResponseHeader responseHeaderInfo = loyaltyEnrollResponse.getHeader();
		headerInfo.setREQUESTID(responseHeaderInfo.getRequestId());
		headerInfo.setPCFLAG("");//TODO need to chek impact when PCflagis not there
		*/
		
		
		return loyaltyEnrollResponseObject;
	}
	
	public LoyaltyIssuanceResponseObject convertIssuanceResponse(LoyaltyIssuanceResponse loyaltyIssuanceResponse){
		
		LoyaltyIssuanceResponseObject loyaltyIssuanceResponseObject = new LoyaltyIssuanceResponseObject();
		
		return loyaltyIssuanceResponseObject;
	}
	
	public LoyaltyRedemptionResponseObject convertRedemptionResponse(LoyaltyRedemptionResponse loyaltyRedemptionResponse){
		
		return null;
	}
	
	
	public LoyaltyEnrollRequest convertEnrollmentRequest(LoyaltyEnrollRequestObject loyaltyEnrolRequestObject) {
		
		
		//written to get subsidiary_number from database instead of SBToOC json as plugins are not ready to get subsidiary number in json
		HeaderInfo headerInfo = loyaltyEnrolRequestObject.getHEADERINFO();
		try{
			if((headerInfo.getSUBSIDIARYNUMBER() == null || headerInfo.getSUBSIDIARYNUMBER().isEmpty()) && 
					loyaltyEnrolRequestObject.getHEADERINFO().getSTORENUMBER() != null && !loyaltyEnrolRequestObject.getHEADERINFO().getSTORENUMBER().isEmpty()){
				OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
				UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
				//UsersDomainsDao UsersDomainsDao = (UsersDomainsDao) SpringUtil.getBean("usersDomainsDao");
				String completeUserName = loyaltyEnrolRequestObject.getUSERDETAILS().getUSERNAME()+Constants.USER_AND_ORG_SEPARATOR+loyaltyEnrolRequestObject.getUSERDETAILS().getORGANISATION();
				Users user = userDao.findByToken(completeUserName, loyaltyEnrolRequestObject.getUSERDETAILS().getTOKEN());
				Long domainId = userDao.findDomainByUserId(user.getUserId());
				OrganizationStores orgStores = organizationStoresDao.findOrgByDomain(user.getUserOrganization().getUserOrgId(), domainId, loyaltyEnrolRequestObject.getHEADERINFO().getSTORENUMBER());
				loyaltyEnrolRequestObject.getHEADERINFO().setSUBSIDIARYNUMBER(orgStores!=null ? orgStores.getSubsidiaryId() : null);
			}
		}catch(Exception e){
			logger.info("Exception::",e);
			
		}
		LoyaltyEnrollRequest loyaltyEnrollRequest = new LoyaltyEnrollRequest();
		EnrollmentInfo enrollmentInfo = loyaltyEnrolRequestObject.getENROLLMENTINFO();
		/*RequestHeader requestHeader = new RequestHeader(headerInfo.getREQUESTID(), MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_DATETIME_STYEAR), 
				headerInfo.getPCFLAG(), headerInfo.getSTORENUMBER(), null, null, null, headerInfo.getSOURCETYPE());*/
		
		String subsidiaryNumber = headerInfo.getSUBSIDIARYNUMBER() != null && !headerInfo.getSUBSIDIARYNUMBER().isEmpty() ? headerInfo.getSUBSIDIARYNUMBER() : null; 
		
		RequestHeader requestHeader = new RequestHeader(headerInfo.getREQUESTID(), MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_DATETIME_STYEAR), 
				headerInfo.getPCFLAG(), subsidiaryNumber , headerInfo.getSTORENUMBER(), enrollmentInfo.getEMPID(), null, null, headerInfo.getSOURCETYPE(),null);
		loyaltyEnrollRequest.setHeader(requestHeader);
		logger.debug("storeNumber is===>"+headerInfo.getSTORENUMBER() + " AND after ===>"+loyaltyEnrollRequest.getHeader().getStoreNumber());     
		MembershipRequest membershipRequest = new MembershipRequest();
		//EnrollmentInfo enrollmentInfo = loyaltyEnrolRequestObject.getENROLLMENTINFO();
		membershipRequest.setCardNumber(enrollmentInfo.getCARDNUMBER());
		membershipRequest.setCardPin(enrollmentInfo.getCARDPIN());
		
		
		if(membershipRequest.getCardNumber() == null || membershipRequest.getCardNumber().trim().length() == 0) {
			try {
				//find the default program first
				//LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
				
				UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
				String completeUserName = loyaltyEnrolRequestObject.getUSERDETAILS().getUSERNAME()+Constants.USER_AND_ORG_SEPARATOR+loyaltyEnrolRequestObject.getUSERDETAILS().getORGANISATION();
				Users user = userDao.findByToken(completeUserName, loyaltyEnrolRequestObject.getUSERDETAILS().getTOKEN());

				LoyaltyProgram program = findDefaultProgram(user.getUserId());
				
				if(program == null){
					
					membershipRequest.setIssueCardFlag("Y");
				}else {
					
					if(program.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)){ 
						if(loyaltyEnrolRequestObject.getCUSTOMERINFO().getPHONE() != null && !loyaltyEnrolRequestObject.getCUSTOMERINFO().getPHONE().isEmpty()){
					
							membershipRequest.setPhoneNumber(loyaltyEnrolRequestObject.getCUSTOMERINFO().getPHONE());
						}
					}else{
						
						membershipRequest.setIssueCardFlag("Y");
					}
					
				}
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ", e);
			}
		}
		//membershipRequest.setPhoneNumber("");
		loyaltyEnrollRequest.setMembership(membershipRequest);
		
		
		Customer customer = new Customer();
		CustomerInfo customerInfo = loyaltyEnrolRequestObject.getCUSTOMERINFO();
		customer.setCustomerId(customerInfo.getCUSTOMERID());
		customer.setEmailAddress(customerInfo.getEMAIL());
		customer.setPhone(customerInfo.getPHONE());
		customer.setFirstName(customerInfo.getFIRSTNAME());
		customer.setLastName(customerInfo.getLASTNAME());
		customer.setAddressLine1(customerInfo.getADDRESS1());
		customer.setAddressLine2(customerInfo.getADDRESS2());
		customer.setBirthday(customerInfo.getBIRTHDAY());
		customer.setAnniversary(customerInfo.getANNIVERSARY());
		customer.setGender(customerInfo.getGENDER());
		customer.setCity(customerInfo.getCITY());
		customer.setState(customerInfo.getSTATE());
		customer.setPostal(customerInfo.getPOSTAL());
		customer.setCountry(customerInfo.getCOUNTRY());
		customer.setCreatedDate(customerInfo.getCREATEDDATE());
		loyaltyEnrollRequest.setCustomer(customer);
		
		LoyaltyUser loyaltyUser = new LoyaltyUser();
		UserDetails userDetails = loyaltyEnrolRequestObject.getUSERDETAILS();
		loyaltyUser.setUserName(userDetails.getUSERNAME());
		loyaltyUser.setToken(userDetails.getTOKEN());
		loyaltyUser.setOrganizationId(userDetails.getORGANISATION());
		loyaltyEnrollRequest.setUser(loyaltyUser);
		
		return loyaltyEnrollRequest;
		
		
	}
	
	private LoyaltyProgram findDefaultProgram(Long userId) throws Exception {
		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		return loyaltyProgramDao.findDefaultProgramByUserId(userId);
	}
	
	public LoyaltyIssuanceRequest convertIssuanceRequest(LoyaltyIssuanceRequestObject loyaltyIssuanceRequestObject) {
		
		
		//written to get subsidiary_number from database instead of SBToOC json as plugins are not ready to get subsidiary number in json
		try{
			if((loyaltyIssuanceRequestObject.getHEADERINFO().getSUBSIDIARYNUMBER() == null || loyaltyIssuanceRequestObject.getHEADERINFO().getSUBSIDIARYNUMBER().isEmpty()) && loyaltyIssuanceRequestObject.getHEADERINFO().getSTORENUMBER() != null && !loyaltyIssuanceRequestObject.getHEADERINFO().getSTORENUMBER().isEmpty()){
				OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
				UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
				//UsersDomainsDao UsersDomainsDao = (UsersDomainsDao) SpringUtil.getBean("usersDomainsDao");
				String completeUserName = loyaltyIssuanceRequestObject.getUSERDETAILS().getUSERNAME()+Constants.USER_AND_ORG_SEPARATOR+loyaltyIssuanceRequestObject.getUSERDETAILS().getORGANISATION();
				Users user = userDao.findByToken(completeUserName, loyaltyIssuanceRequestObject.getUSERDETAILS().getTOKEN());
				Long domainId = userDao.findDomainByUserId(user.getUserId());
				OrganizationStores orgStores = organizationStoresDao.findOrgByDomain(user.getUserOrganization().getUserOrgId(), domainId, loyaltyIssuanceRequestObject.getHEADERINFO().getSTORENUMBER());
				logger.info("===>Enter 1");
			}
		}catch(Exception e){
			logger.info("Exception::",e);

		}
		LoyaltyIssuanceRequest loyaltyIssuanceRequest = new LoyaltyIssuanceRequest();
		HeaderInfo headerInfo = loyaltyIssuanceRequestObject.getHEADERINFO();
		/*RequestHeader requestHeader = new RequestHeader(headerInfo.getREQUESTID(), MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_DATETIME_STYEAR), 
				headerInfo.getPCFLAG(), headerInfo.getSTORENUMBER(), loyaltyIssuanceRequestObject.getISSUANCEINFO().getEMPID(), null,
				loyaltyIssuanceRequestObject.getISSUANCEINFO().getDOCSID(), headerInfo.getSOURCETYPE());*/
		logger.info("===>Enter 2");
		String subsidiaryNumber = headerInfo.getSUBSIDIARYNUMBER() != null && !headerInfo.getSUBSIDIARYNUMBER().isEmpty() ? headerInfo.getSUBSIDIARYNUMBER() : Constants.STRING_NILL; 
		
		RequestHeader requestHeader = new RequestHeader(headerInfo.getREQUESTID(), MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_DATETIME_STYEAR), 
										headerInfo.getPCFLAG(), subsidiaryNumber, headerInfo.getSTORENUMBER(), loyaltyIssuanceRequestObject.getISSUANCEINFO().getEMPID(), null,
										loyaltyIssuanceRequestObject.getISSUANCEINFO().getDOCSID(), headerInfo.getSOURCETYPE(), loyaltyIssuanceRequestObject.getISSUANCEINFO().getRECEIPTNUMBER() );
		loyaltyIssuanceRequest.setHeader(requestHeader);
		logger.debug("storeNumber is===>"+headerInfo.getSTORENUMBER() + " AND after ===>"+loyaltyIssuanceRequest.getHeader().getStoreNumber());
		MembershipRequest membershipRequest = new MembershipRequest();
		//membershipRequest.setIssueCardFlag(null);
		//membershipRequest.setPhoneNumber(null);

		try {
			//find the default program first
			//LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			
			UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			String completeUserName = loyaltyIssuanceRequestObject.getUSERDETAILS().getUSERNAME()+Constants.USER_AND_ORG_SEPARATOR+loyaltyIssuanceRequestObject.getUSERDETAILS().getORGANISATION();
			Users user = userDao.findByToken(completeUserName, loyaltyIssuanceRequestObject.getUSERDETAILS().getTOKEN());
			LoyaltyCards loyaltyCard = findLoyaltyCardByUserId(loyaltyIssuanceRequestObject.getISSUANCEINFO().getCARDNUMBER(), user.getUserId());
			
			if(loyaltyCard == null){
				LoyaltyProgram program = findDefaultProgram(user.getUserId());
				if(program != null && program.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)){ 
					membershipRequest.setPhoneNumber(loyaltyIssuanceRequestObject.getISSUANCEINFO().getCARDNUMBER());
					
				}else{
					
					membershipRequest.setCardNumber(loyaltyIssuanceRequestObject.getISSUANCEINFO().getCARDNUMBER());
				}
			}else {
				LoyaltyProgram program = findDefaultProgram(user.getUserId());
				
				
				if(program != null && program.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)){ 
					membershipRequest.setPhoneNumber(loyaltyIssuanceRequestObject.getISSUANCEINFO().getCARDNUMBER());
					
				}else{
					
					membershipRequest.setCardNumber(loyaltyIssuanceRequestObject.getISSUANCEINFO().getCARDNUMBER());
				}
			}
				membershipRequest.setCardPin(loyaltyIssuanceRequestObject.getISSUANCEINFO().getCARDPIN());
				membershipRequest.setCreatedDate(loyaltyIssuanceRequestObject.getISSUANCEINFO().getCREATEDDATE() != null && !loyaltyIssuanceRequestObject.getISSUANCEINFO().getCREATEDDATE().trim().isEmpty() ? loyaltyIssuanceRequestObject.getISSUANCEINFO().getCREATEDDATE() : null);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
	
		
		
		
		
		loyaltyIssuanceRequest.setMembership(membershipRequest);
		
		Amount amount = new Amount();
		amount.setType(OCConstants.LOYALTY_TYPE_PURCHASE);
		amount.setEnteredValue(loyaltyIssuanceRequestObject.getISSUANCEINFO().getENTEREDAMOUNT());
		amount.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
		loyaltyIssuanceRequest.setAmount(amount);
		
		Discounts discounts = new Discounts();
		discounts.setAppliedPromotion("NA");
		List<Promotion> promotions = new ArrayList<Promotion>();
		discounts.setPromotions(promotions);
		loyaltyIssuanceRequest.setDiscounts(discounts);
		
		List<SkuDetails> items = new ArrayList<SkuDetails>();
		loyaltyIssuanceRequest.setItems(items);
		
		Customer customer = new Customer();
		customer.setCustomerId(loyaltyIssuanceRequestObject.getISSUANCEINFO().getCUSTOMERID());
		loyaltyIssuanceRequest.setCustomer(customer);
		/*customer.setEmailAddress(customerInfo.getEMAIL());
		customer.setPhone(customerInfo.getPHONE());
		customer.setFirstName(customerInfo.getFIRSTNAME());
		customer.setLastName(customerInfo.getLASTNAME());
		customer.setAddressLine1(customerInfo.getADDRESS1());
		customer.setAddressLine2(customerInfo.getADDRESS2());
		customer.setBirthday(customerInfo.getBIRTHDAY());
		customer.setAnniversary(customerInfo.getANNIVERSARY());
		customer.setGender(customerInfo.getGENDER());
		customer.setCity(customerInfo.getCITY());
		customer.setState(customerInfo.getSTATE());
		customer.setPostal(customerInfo.getPOSTAL());
		customer.setCountry(customerInfo.getCOUNTRY());
		loyaltyEnrollRequest.setCustomer(customer);*/
		
		
		
		LoyaltyUser loyaltyUser = new LoyaltyUser();
		UserDetails userDetails = loyaltyIssuanceRequestObject.getUSERDETAILS();
		loyaltyUser.setUserName(userDetails.getUSERNAME());
		loyaltyUser.setToken(userDetails.getTOKEN());
		loyaltyUser.setOrganizationId(userDetails.getORGANISATION());
		loyaltyIssuanceRequest.setUser(loyaltyUser);
		
		return loyaltyIssuanceRequest;
		
		
	}
	
public LoyaltyRedemptionRequest convertRedemptionRequest(LoyaltyRedemptionRequestObject loyaltyRedemptionRequestObject) {
		
	//written to get subsidiary_number from database instead of SBToOC json as plugins are not ready to get subsidiary number in json
	try{
		if((loyaltyRedemptionRequestObject.getHEADERINFO().getSUBSIDIARYNUMBER() == null || loyaltyRedemptionRequestObject.getHEADERINFO().getSUBSIDIARYNUMBER().isEmpty()) && loyaltyRedemptionRequestObject.getHEADERINFO().getSTORENUMBER() != null && !loyaltyRedemptionRequestObject.getHEADERINFO().getSTORENUMBER().isEmpty()){
			OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
			UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			//UsersDomainsDao UsersDomainsDao = (UsersDomainsDao) SpringUtil.getBean("usersDomainsDao");
			String completeUserName = loyaltyRedemptionRequestObject.getUSERDETAILS().getUSERNAME()+Constants.USER_AND_ORG_SEPARATOR+loyaltyRedemptionRequestObject.getUSERDETAILS().getORGANISATION();
			Users user = userDao.findByToken(completeUserName, loyaltyRedemptionRequestObject.getUSERDETAILS().getTOKEN());
			Long domainId = userDao.findDomainByUserId(user.getUserId());
			OrganizationStores orgStores = organizationStoresDao.findOrgByDomain(user.getUserOrganization().getUserOrgId(), domainId, loyaltyRedemptionRequestObject.getHEADERINFO().getSTORENUMBER());
			loyaltyRedemptionRequestObject.getHEADERINFO().setSUBSIDIARYNUMBER(orgStores!=null ? orgStores.getSubsidiaryId() : null);
		}
	}catch(Exception e){
		logger.info("Exception::",e);

	}
		
		LoyaltyRedemptionRequest LoyaltyRedemptionRequest = new LoyaltyRedemptionRequest();
		HeaderInfo headerInfo = loyaltyRedemptionRequestObject.getHEADERINFO();
		/*RequestHeader requestHeader = new RequestHeader(headerInfo.getREQUESTID(), MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_DATETIME_STYEAR), 
				headerInfo.getPCFLAG(), headerInfo.getSTORENUMBER(), loyaltyRedemptionRequestObject.getREDEMPTIONINFO().getEMPID(), null, 
				loyaltyRedemptionRequestObject.getREDEMPTIONINFO().getDOCSID(), headerInfo.getSOURCETYPE());*/
		
		String subsidiaryNumber = headerInfo.getSUBSIDIARYNUMBER() != null && !headerInfo.getSUBSIDIARYNUMBER().isEmpty() ? headerInfo.getSUBSIDIARYNUMBER() : null;
		
		RequestHeader requestHeader = new RequestHeader(headerInfo.getREQUESTID(), MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_DATETIME_STYEAR), 
										headerInfo.getPCFLAG(), subsidiaryNumber, headerInfo.getSTORENUMBER(), loyaltyRedemptionRequestObject.getREDEMPTIONINFO().getEMPID(), null, 
										loyaltyRedemptionRequestObject.getREDEMPTIONINFO().getDOCSID(), headerInfo.getSOURCETYPE(), loyaltyRedemptionRequestObject.getREDEMPTIONINFO().getRECEIPTNUMBER());
		LoyaltyRedemptionRequest.setHeader(requestHeader);
		logger.debug("storeNumber is===>"+headerInfo.getSTORENUMBER() + " AND after ===>"+LoyaltyRedemptionRequest.getHeader().getStoreNumber());
		MembershipRequest membershipRequest = new MembershipRequest();
		//membershipRequest.setCardNumber(loyaltyRedemptionRequestObject.getREDEMPTIONINFO().getCARDNUMBER());
		//membershipRequest.setCardPin(loyaltyRedemptionRequestObject.getREDEMPTIONINFO().getCARDPIN());
		//membershipRequest.setCreatedDate(loyaltyRedemptionRequestObject.getREDEMPTIONINFO().getCREATEDDATE());
		//membershipRequest.setIssueCardFlag(null);
		//membershipRequest.setPhoneNumber(null);
		
		try {
			//find the default program first
			//LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			
			UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			String completeUserName = loyaltyRedemptionRequestObject.getUSERDETAILS().getUSERNAME()+Constants.USER_AND_ORG_SEPARATOR+loyaltyRedemptionRequestObject.getUSERDETAILS().getORGANISATION();
			Users user = userDao.findByToken(completeUserName, loyaltyRedemptionRequestObject.getUSERDETAILS().getTOKEN());
            LoyaltyCards loyaltyCard = findLoyaltyCardByUserId(loyaltyRedemptionRequestObject.getREDEMPTIONINFO().getCARDNUMBER(), user.getUserId());
			
			if(loyaltyCard == null){
				LoyaltyProgram program = findDefaultProgram(user.getUserId());
				if(program != null && program.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)){ 
					membershipRequest.setPhoneNumber(loyaltyRedemptionRequestObject.getREDEMPTIONINFO().getCARDNUMBER());
					
				}else{
					
					membershipRequest.setCardNumber(loyaltyRedemptionRequestObject.getREDEMPTIONINFO().getCARDNUMBER());
				}
			}else {
				LoyaltyProgram program = findDefaultProgram(user.getUserId());
				
				
				if(program != null && program.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)){ 
					membershipRequest.setPhoneNumber(loyaltyRedemptionRequestObject.getREDEMPTIONINFO().getCARDNUMBER());
					
				}else{
					
					membershipRequest.setCardNumber(loyaltyRedemptionRequestObject.getREDEMPTIONINFO().getCARDNUMBER());
				}
			}
				membershipRequest.setCardPin(loyaltyRedemptionRequestObject.getREDEMPTIONINFO().getCARDPIN());
				membershipRequest.setCreatedDate(loyaltyRedemptionRequestObject.getREDEMPTIONINFO().getCREATEDDATE() != null && !loyaltyRedemptionRequestObject.getREDEMPTIONINFO().getCREATEDDATE().trim().isEmpty() ? loyaltyRedemptionRequestObject.getREDEMPTIONINFO().getCREATEDDATE() : null);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
	
		
		
		LoyaltyRedemptionRequest.setMembership(membershipRequest);
		LoyaltyRedemptionRequest.setOtpCode("");
		String OTP = loyaltyRedemptionRequestObject.getREDEMPTIONINFO().getOTPCODE();
		if(OTP != null && !OTP.isEmpty())LoyaltyRedemptionRequest.setOtpCode(  OTP );
		Amount amount = new Amount();
		amount.setType(OCConstants.LOYALTY_TYPE_PURCHASE);
		amount.setReceiptAmount(loyaltyRedemptionRequestObject.getREDEMPTIONINFO().getRECEIPTAMOUNT());
		amount.setEnteredValue(loyaltyRedemptionRequestObject.getREDEMPTIONINFO().getENTEREDAMOUNT());
		String valueCode = loyaltyRedemptionRequestObject.getREDEMPTIONINFO().getVALUECODE();
		if(valueCode!= null && !valueCode.trim().isEmpty()) {
			if(valueCode.equals(OCConstants.LOYALTY_USD)) {
				amount.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY );
			}else if(valueCode.equalsIgnoreCase(OCConstants.LOYALTY_POINTS)) {
				
				amount.setValueCode(OCConstants.LOYALTY_TYPE_POINTS );
			}
			
		}
		LoyaltyRedemptionRequest.setAmount(amount);
		
		//LoyaltyRedemptionRequest.setOtpCode("");
		
		Discounts discounts = new Discounts();
		discounts.setAppliedPromotion("NA");
		List<Promotion> promotions = new ArrayList<Promotion>();
		discounts.setPromotions(promotions);
		LoyaltyRedemptionRequest.setDiscounts(discounts);
		
		
		
		Customer customer = new Customer();
		String phone = loyaltyRedemptionRequestObject.getREDEMPTIONINFO().getPHONE();
		if(phone != null && !phone.isEmpty()) {
			
			customer.setPhone(phone);
//			LoyaltyRedemptionRequest.setCustomer(customer);
			
			
		}
		
		customer.setCustomerId(loyaltyRedemptionRequestObject.getREDEMPTIONINFO().getCUSTOMERID());//this is the culprit
		/*customer.setEmailAddress(customerInfo.getEMAIL());
		customer.setPhone(customerInfo.getPHONE());
		customer.setFirstName(customerInfo.getFIRSTNAME());
		customer.setLastName(customerInfo.getLASTNAME());
		customer.setAddressLine1(customerInfo.getADDRESS1());
		customer.setAddressLine2(customerInfo.getADDRESS2());
		customer.setBirthday(customerInfo.getBIRTHDAY());
		customer.setAnniversary(customerInfo.getANNIVERSARY());
		customer.setGender(customerInfo.getGENDER());
		customer.setCity(customerInfo.getCITY());
		customer.setState(customerInfo.getSTATE());
		customer.setPostal(customerInfo.getPOSTAL());
		customer.setCountry(customerInfo.getCOUNTRY());*/
		LoyaltyRedemptionRequest.setCustomer(customer);
		
		
		
		LoyaltyUser loyaltyUser = new LoyaltyUser();
		UserDetails userDetails = loyaltyRedemptionRequestObject.getUSERDETAILS();
		loyaltyUser.setUserName(userDetails.getUSERNAME());
		loyaltyUser.setToken(userDetails.getTOKEN());
		loyaltyUser.setOrganizationId(userDetails.getORGANISATION());
		LoyaltyRedemptionRequest.setUser(loyaltyUser);
		
		return LoyaltyRedemptionRequest;
		
		
	}
	
public LoyaltyInquiryRequest convertInquiryRequest(LoyaltyInquiryRequestObject loyaltyInquiryRequestObject) {
	
	//written to get subsidiary_number from database instead of SBToOC json as plugins are not ready to get subsidiary number in json
	try{
		if((loyaltyInquiryRequestObject.getHEADERINFO().getSUBSIDIARYNUMBER() == null || loyaltyInquiryRequestObject.getHEADERINFO().getSUBSIDIARYNUMBER().isEmpty()) 
				&& loyaltyInquiryRequestObject.getHEADERINFO().getSTORENUMBER() != null && !loyaltyInquiryRequestObject.getHEADERINFO().getSTORENUMBER().isEmpty()){
			OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
			UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			//UsersDomainsDao UsersDomainsDao = (UsersDomainsDao) SpringUtil.getBean("usersDomainsDao");
			String completeUserName = loyaltyInquiryRequestObject.getUSERDETAILS().getUSERNAME()+Constants.USER_AND_ORG_SEPARATOR+loyaltyInquiryRequestObject.getUSERDETAILS().getORGANISATION();
			Users user = userDao.findByToken(completeUserName, loyaltyInquiryRequestObject.getUSERDETAILS().getTOKEN());
			Long domainId = userDao.findDomainByUserId(user.getUserId());
			OrganizationStores orgStores = organizationStoresDao.findOrgByDomain(user.getUserOrganization().getUserOrgId(), domainId, loyaltyInquiryRequestObject.getHEADERINFO().getSTORENUMBER());
			loyaltyInquiryRequestObject.getHEADERINFO().setSUBSIDIARYNUMBER(orgStores!=null ? orgStores.getSubsidiaryId() : null);
		}
	}catch(Exception e){
		logger.info("Exception::",e);

	}
	
	LoyaltyInquiryRequest LoyaltyInquiryRequest = new LoyaltyInquiryRequest();
	HeaderInfo headerInfo = loyaltyInquiryRequestObject.getHEADERINFO();
	String subsidiaryNumber = headerInfo.getSUBSIDIARYNUMBER() != null && !headerInfo.getSUBSIDIARYNUMBER().isEmpty() ? headerInfo.getSUBSIDIARYNUMBER() : null;
	RequestHeader requestHeader = new RequestHeader(headerInfo.getREQUESTID(), MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_DATETIME_STYEAR), 
									headerInfo.getPCFLAG(), subsidiaryNumber, headerInfo.getSTORENUMBER(), loyaltyInquiryRequestObject.getINQUIRYINFO().getEMPID(), null, 
									null, headerInfo.getSOURCETYPE(), loyaltyInquiryRequestObject.getINQUIRYINFO().getRECEIPTAMOUNT());//APP-1966
	LoyaltyInquiryRequest.setHeader(requestHeader);
	logger.debug("storeNumber is===>"+headerInfo.getSTORENUMBER() + " AND after ===>"+loyaltyInquiryRequestObject.getHEADERINFO().getSTORENUMBER());
	MembershipRequest membershipRequest = new MembershipRequest();
	try {
		//find the default program first
		//LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		
		UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		String completeUserName = loyaltyInquiryRequestObject.getUSERDETAILS().getUSERNAME()+Constants.USER_AND_ORG_SEPARATOR+loyaltyInquiryRequestObject.getUSERDETAILS().getORGANISATION();
		Users user = userDao.findByToken(completeUserName, loyaltyInquiryRequestObject.getUSERDETAILS().getTOKEN());
		LoyaltyCards loyaltyCard = findLoyaltyCardByUserId(loyaltyInquiryRequestObject.getINQUIRYINFO().getCARDNUMBER(), user.getUserId());
		
		if(loyaltyCard == null){
			LoyaltyProgram program = findDefaultProgram(user.getUserId());
			if(program != null && program.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)){ 
				membershipRequest.setPhoneNumber(loyaltyInquiryRequestObject.getINQUIRYINFO().getCARDNUMBER());
				
			}else{
				
				membershipRequest.setCardNumber(loyaltyInquiryRequestObject.getINQUIRYINFO().getCARDNUMBER());
			}
		}else {
			membershipRequest.setCardNumber(loyaltyInquiryRequestObject.getINQUIRYINFO().getCARDNUMBER());
			
		}
			
			
			
			membershipRequest.setCardPin(loyaltyInquiryRequestObject.getINQUIRYINFO().getCARDPIN());
			//membershipRequest.setCreatedDate(loyaltyInquiryRequestObject.getINQUIRYINFO().getCREATEDDATE() != null && !loyaltyIssuanceRequestObject.getISSUANCEINFO().getCREATEDDATE().trim().isEmpty() ? loyaltyIssuanceRequestObject.getISSUANCEINFO().getCREATEDDATE() : null);
		
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception ", e);
	}

		/*
		 * membershipRequest.setCardNumber(loyaltyInquiryRequestObject.getINQUIRYINFO().
		 * getCARDNUMBER());
		 * membershipRequest.setCardPin(loyaltyInquiryRequestObject.getINQUIRYINFO().
		 * getCARDPIN());
		 */
	//membershipRequest.setCreatedDate(loyaltyInquiryRequestObject.getREDEMPTIONINFO().getCREATEDDATE());
	//membershipRequest.setIssueCardFlag(null);
	//membershipRequest.setPhoneNumber(null);
	LoyaltyInquiryRequest.setMembership(membershipRequest);
	
	Customer customer = new Customer();
	customer.setCustomerId(loyaltyInquiryRequestObject.getINQUIRYINFO().getCUSTOMERID());
	/*customer.setEmailAddress(customerInfo.getEMAIL());
	customer.setPhone(customerInfo.getPHONE());
	customer.setFirstName(customerInfo.getFIRSTNAME());
	customer.setLastName(customerInfo.getLASTNAME());
	customer.setAddressLine1(customerInfo.getADDRESS1());
	customer.setAddressLine2(customerInfo.getADDRESS2());
	customer.setBirthday(customerInfo.getBIRTHDAY());
	customer.setAnniversary(customerInfo.getANNIVERSARY());
	customer.setGender(customerInfo.getGENDER());
	customer.setCity(customerInfo.getCITY());
	customer.setState(customerInfo.getSTATE());
	customer.setPostal(customerInfo.getPOSTAL());
	customer.setCountry(customerInfo.getCOUNTRY());
	loyaltyEnrollRequest.setCustomer(customer);*/
	LoyaltyInquiryRequest.setCustomer(customer);
	
	//APP-1966
	Amount amount = new Amount(); 
	amount.setReceiptAmount(loyaltyInquiryRequestObject.getINQUIRYINFO().getRECEIPTAMOUNT());
	LoyaltyInquiryRequest.setAmount(amount);
	
	
	
	LoyaltyUser loyaltyUser = new LoyaltyUser();
	UserDetails userDetails = loyaltyInquiryRequestObject.getUSERDETAILS();
	loyaltyUser.setUserName(userDetails.getUSERNAME());
	loyaltyUser.setToken(userDetails.getTOKEN());
	loyaltyUser.setOrganizationId(userDetails.getORGANISATION());
	LoyaltyInquiryRequest.setUser(loyaltyUser);
	
	return LoyaltyInquiryRequest;
	
	
}


}
