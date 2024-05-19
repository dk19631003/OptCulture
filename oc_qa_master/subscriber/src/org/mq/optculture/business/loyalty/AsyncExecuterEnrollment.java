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
import java.util.ListIterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyCards;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyThresholdBonus;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.LoyaltyTransactionExpiry;
import org.mq.marketer.campaign.beans.LoyaltyTransactionParent;
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
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
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
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.helper.SmsQueueHelper;
import org.mq.optculture.data.dao.LoyaltyAutoCommDao;
import org.mq.optculture.data.dao.LoyaltyCardsDao;
import org.mq.optculture.data.dao.LoyaltyCardsDaoForDML;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDaoForDML;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDaoForDML;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDao;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.mq.optculture.model.ocloyalty.Amount;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.model.ocloyalty.Customer;
import org.mq.optculture.model.ocloyalty.Discounts;
import org.mq.optculture.model.ocloyalty.HoldBalance;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyUser;
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.ocloyalty.MembershipRequest;
import org.mq.optculture.model.ocloyalty.MembershipResponse;
import org.mq.optculture.model.ocloyalty.Promotion;
import org.mq.optculture.model.ocloyalty.RequestHeader;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.SkuDetails;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;

public class AsyncExecuterEnrollment extends Thread {
	
	
	private LoyaltyEnrollRequest enrollRequest;
	private ResponseHeader responseHeader; 
	private LoyaltyProgram loyaltyProgram;
	private LoyaltyCards loyaltyCard;
	private String mode; 
	private Users user; 
	private MailingList mlList;
	private String memberShipType;
	private LoyaltyProgramTier linkedTierObj;
	private Boolean GLFlag;
	private String encPwd;
	
	public AsyncExecuterEnrollment(){
		
	}
	
	public AsyncExecuterEnrollment(LoyaltyEnrollRequest enrollRequest, ResponseHeader responseHeader, 
			LoyaltyProgram loyaltyProgram, LoyaltyCards loyaltyCard,
		String mode, Users user, MailingList mlList, String memberShipType, LoyaltyProgramTier linkedTierObj,Boolean GLFlag, String encPwd){
		this.enrollRequest=enrollRequest;
		this.responseHeader=responseHeader;
		this.loyaltyProgram=loyaltyProgram;
		this.loyaltyCard=loyaltyCard;
		this.mode=mode;
		this.user=user;
		this.mlList=mlList;
		this.memberShipType=memberShipType;
		this.linkedTierObj=linkedTierObj;
		this.GLFlag=GLFlag;
		this.encPwd = encPwd;
	}
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	public void run() {
        logger.info("MyThread - START "+Thread.currentThread().getName());
        try {
            if(!GLFlag)
        	createMembership(enrollRequest, responseHeader, loyaltyProgram, loyaltyCard, mode, user, mlList, memberShipType, linkedTierObj);
            else
            convertGiftToLoyalty(enrollRequest, responseHeader, loyaltyProgram, loyaltyCard, mode, user, mlList, linkedTierObj, memberShipType);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void performEnrollment(){
        logger.info("MyThread - START "+Thread.currentThread().getName());
        try {
            if(!GLFlag)
        	createMembership(enrollRequest, responseHeader, loyaltyProgram, loyaltyCard, mode, user, mlList, memberShipType, linkedTierObj);
            else
            convertGiftToLoyalty(enrollRequest, responseHeader, loyaltyProgram, loyaltyCard, mode, user, mlList, linkedTierObj, memberShipType);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
		
	}
	
	
	private void createMembership(LoyaltyEnrollRequest enrollRequest, ResponseHeader responseHeader, 
				LoyaltyProgram program, LoyaltyCards card,
			String mode, Users user, MailingList mlList, String memberShipType, LoyaltyProgramTier linkedTierObj) throws Exception {
		logger.info("Entered createMembership method >>>");
		boolean isDCard = OCConstants.LOYALTY_PROGRAM_TYPE_DYNAMIC.equals(program.getProgramType());
		LoyaltyEnrollResponse enrollResponse = null;
		ContactsLoyalty contactLoyalty = null;
		
		List<POSMapping> contactPOSMap = null;
		POSMappingDao posMappingDao = (POSMappingDao) ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
		contactPOSMap = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", user.getUserId());
		
		Contacts jsonContact = new Contacts();
		jsonContact.setUsers(user);
		String subsidiary = enrollRequest.getHeader().getSubsidiaryNumber() != null && !enrollRequest.getHeader().getSubsidiaryNumber().trim().isEmpty() ? enrollRequest.getHeader().getSubsidiaryNumber().trim() : null; 
		String store = enrollRequest.getHeader().getStoreNumber() != null ? enrollRequest.getHeader().getStoreNumber().trim() : enrollRequest.getHeader().getStoreNumber();
		if(enrollRequest.getCustomer().getHomeStore() != null && !enrollRequest.getCustomer().getHomeStore().isEmpty()) store = enrollRequest.getCustomer().getHomeStore();
		if(enrollRequest.getCustomer().getSubsidiaryNumber() !=null && !enrollRequest.getCustomer().getSubsidiaryNumber().isEmpty()) subsidiary = enrollRequest.getCustomer().getSubsidiaryNumber();
		if(contactPOSMap != null){
			jsonContact = setContactFields(jsonContact, contactPOSMap, enrollRequest,memberShipType);
		}
		if(jsonContact.getMobilePhone() != null && !jsonContact.getMobilePhone().isEmpty())
			enrollRequest.getCustomer().setPhone(jsonContact.getMobilePhone());


		Map<String, Object> contactAndDataFlags = validateAndSavedbContact(jsonContact, mlList, user,enrollRequest);
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
				
				Status status = new Status("111563", PropertyUtil.getErrorMessage(111563, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactLoyalty.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);//Could be done before
			}
		}
		
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
		
		List<LoyaltyProgramTier> tierList = null;//Same I guess --No Significance here 
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
			}
		}
		

		if(Utility.isModifiedContact(dbContact,jsonContact ))
		{
			logger.info("entered Modified date");
			dbContact.setModifiedDate(Calendar.getInstance());
		}	
		contactsDaoForDML.saveOrUpdate(dbContact);
		
		/*String phone = null;
		if(jsonContact.getMobilePhone() != null){
			phone = jsonContact.getMobilePhone();
		}
		else{*/
		String phone = dbContact.getMobilePhone();
		String email = dbContact.getEmailId();
		//}
		if(memberShipType.equals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD)){
			String optinMedium = Constants.CONTACT_LOYALTY_TYPE_POS; 
			if(enrollRequest.getHeader().getSourceType()!=null && 
					!enrollRequest.getHeader().getSourceType().isEmpty() &&
					!enrollRequest.getHeader().getSourceType().toString().equalsIgnoreCase("Store")) {
				optinMedium = enrollRequest.getHeader().getSourceType().trim();
			}
			contactLoyalty = prepareLoyaltyMembership(card.getCardNumber(), OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD, card.getCardPin(), phone,email,
					optinMedium, subsidiary, store, mode, program, linkedTierObj, card.getCardSetId(),
					enrollRequest.getHeader().getEmployeeId(),enrollRequest.getHeader().getTerminalId(), enrollRequest.getHeader().getSourceType(),user);
			
		}
		else if(memberShipType.equals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)){
			contactLoyalty = prepareLoyaltyMembership(enrollRequest.getMembership().getPhoneNumber(), OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE, 
					null, phone,email, Constants.CONTACT_LOYALTY_TYPE_POS, subsidiary, store, mode, program, linkedTierObj,
					null,enrollRequest.getHeader().getEmployeeId(),enrollRequest.getHeader().getTerminalId(), enrollRequest.getHeader().getSourceType(),user);
		}
		contactLoyalty.setUserId(user.getUserId());
		contactLoyalty.setOrgId(user.getUserOrganization().getUserOrgId());
		contactLoyalty.setContact(dbContact);
		contactLoyalty.setCustomerId(dbContact.getExternalId());
		contactLoyalty.setRewardFlag(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);

		//generate a pwd and encrypt it and save it...
		String memPwd = "";
		logger.debug("encPwd === "+encPwd);
		if(encPwd == null) {
			 memPwd = RandomStringUtils.randomAlphanumeric(6);
			
			encPwd = generateMembrshpPwd(memPwd);
		}
		contactLoyalty.setMembershipPwd(encPwd);
		//APP-1651
		if(enrollRequest.getHeader().getSourceType() != null && 
				!enrollRequest.getHeader().getSourceType().isEmpty() && 
				(enrollRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP)|| 
				enrollRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP))){
	
			
			dbContact.setOptinMedium(enrollRequest.getHeader().getSourceType());
			}	
		String fpRecognitionFlagString = enrollRequest.getMembership().getFingerprintValidation();
		if(fpRecognitionFlagString!=null && (fpRecognitionFlagString.equalsIgnoreCase("false")
				|| fpRecognitionFlagString.trim().equalsIgnoreCase("True"))) {
			
			boolean fpRecognitionFlag = Boolean.parseBoolean(fpRecognitionFlagString.toLowerCase());
			contactLoyalty.setFpRecognitionFlag(fpRecognitionFlag);
		}
		if(enrollRequest.getCustomer().getInstanceId()!=null && !enrollRequest.getCustomer().getInstanceId().isEmpty()) {
			dbContact.setInstanceId(enrollRequest.getCustomer().getInstanceId());
		}
		
		if(enrollRequest.getCustomer().getDeviceType()!=null && !enrollRequest.getCustomer().getDeviceType().isEmpty()) {
			dbContact.setDeviceType(enrollRequest.getCustomer().getDeviceType());
		}
		dbContact.setLoyaltyCustomer((byte)1);
		if(Utility.isModifiedContact(dbContact,jsonContact ))
		{
			logger.info("entered Modified date");
			dbContact.setModifiedDate(Calendar.getInstance());
		}	
		contactsDaoForDML.saveOrUpdate(dbContact);
		
		saveContactLoyalty(contactLoyalty);
		
		if(memberShipType.equals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD)){
			card.setMembershipId(contactLoyalty.getLoyaltyId());
			card.setActivationDate(Calendar.getInstance());
			card.setStatus(OCConstants.LOYALTY_CARD_STATUS_ENROLLED);
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
			contactLoyalty.setProgramTierId(tier.getTierId());
			saveContactLoyalty(contactLoyalty);
			
		}
		else {
			tier = linkedTierObj;
		}
		
		MembershipResponse accountResponse = new MembershipResponse();
		
		if(memberShipType.equals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD)){
			accountResponse.setCardNumber(String.valueOf(contactLoyalty.getCardNumber()));
			//accountResponse.setPassword(contactLoyalty.getMembershipPwd() != null ? contactLoyalty.getMembershipPwd() : Constants.STRING_NILL );
			accountResponse.setCardPin(card.getCardPin());
			accountResponse.setPhoneNumber("");
		}
		else if(memberShipType.equals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)){
			accountResponse.setPhoneNumber(String.valueOf(contactLoyalty.getCardNumber()));
			accountResponse.setCardNumber("");
			//accountResponse.setPassword(contactLoyalty.getMembershipPwd() != null ? contactLoyalty.getMembershipPwd() : Constants.STRING_NILL );
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
		
		
		Status status = new Status("0", "Enrollment was successful", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		
		
		LoyaltyTransactionChild transChild = createSuccessfulTransaction(enrollRequest, contactLoyalty, responseHeader, user.getUserId(), user.getUserOrganization().getUserOrgId()
				, program.getProgramId(), memberShipType, enrollRequest.getHeader().getDocSID(), "loyaltyEnroll", store,enrollRequest.getHeader().getEmployeeId(),enrollRequest.getHeader().getTerminalId());
		
		//preparing issuance request when program type is Perk APP-3666
		logger.info(" before calling perk condition >>>" +program.getRewardType());
		if(program!=null && program.getRewardType()!=null && 
				program.getRewardType().equalsIgnoreCase(OCConstants.REWARD_TYPE_PERK)) {
			
			logger.info("inside if perk condition>>>>"+program.getRewardType());
			
			LoyaltyIssuanceRequest issuanceRequest = new LoyaltyIssuanceRequest();
			LoyaltyIssuanceResponse issuanceResponse = new LoyaltyIssuanceResponse();
			
			RequestHeader header= new RequestHeader();
			MembershipRequest membershipRequest =new MembershipRequest();
			Amount amount = new Amount();
			LoyaltyUser loyaltyUser =new LoyaltyUser();
			Customer customer = new Customer();
			String requestId = enrollRequest.getHeader().getRequestId();
			
			try {
				Calendar syscal = new MyCalendar(Calendar.getInstance(), null,
						MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
				header.setRequestId(requestId);
				header.setRequestDate(syscal.toString());
				header.setStoreNumber(enrollRequest.getHeader().getStoreNumber());
			    header.setDocSID("");
			    header.setReceiptNumber(enrollRequest.getHeader().getReceiptNumber());
			    header.setSubsidiaryNumber(enrollRequest.getHeader().getSubsidiaryNumber());
			    header.setSourceType(enrollRequest.getHeader().getSourceType());
			    header.setEmployeeId(enrollRequest.getHeader().getEmployeeId());
			    header.setTerminalId(enrollRequest.getHeader().getTerminalId());
			    
			    if(contactLoyalty.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
			    	  membershipRequest.setPhoneNumber(contactLoyalty.getCardNumber());
			    	  membershipRequest.setCardNumber("");
			    	  logger.info("inside membership type mobile>>>"+contactLoyalty.getMembershipType());
			      }else {
			    	membershipRequest.setCardNumber(contactLoyalty.getCardNumber());
					membershipRequest.setCardPin(enrollRequest.getMembership().getCardPin());
					membershipRequest.setPhoneNumber("");
					logger.info("inside membership type card>>>"+contactLoyalty.getMembershipType()+" card number>>>"+enrollRequest.getMembership().getCardNumber());
			      }
			    
			    amount.setType(OCConstants.LOYALTY_TYPE_REWARD);
			    amount.setValueCode(OCConstants.LOYALTY_TYPE_PERKS);
			    
			    int currentMonth = syscal.get(Calendar.MONTH);
			    double perkstobegiven = 0.0;
			    
			    if(tier.getRewardExpiryDateType()!=null && tier.getRewardExpiryDateType().equalsIgnoreCase(OCConstants.PERKS_EXP_TYPE_QUARTER)) {
			    	
			    	if(currentMonth>=0 && currentMonth<3) {
			    		if(currentMonth==0) {
				    		perkstobegiven = tier.getEarnValue();
				    	}else {
				    		Double multipleFactordbl = tier.getEarnValue()/3;
				    		long multipleFactor = multipleFactordbl.intValue();
				    		perkstobegiven = multipleFactor*(3-currentMonth);
				    	}
			    	} else if(currentMonth>=3 && currentMonth<6) {
			    		if(currentMonth==3) {
				    		perkstobegiven = tier.getEarnValue();
				    	}else {
				    		Double multipleFactordbl = tier.getEarnValue()/3;
				    		long multipleFactor = multipleFactordbl.intValue();
				    		perkstobegiven = multipleFactor*(6-currentMonth);
				    	}
			    	} else if(currentMonth>=6 && currentMonth<9) {
			    		if(currentMonth==6) {
				    		perkstobegiven = tier.getEarnValue();
				    	}else {
				    		Double multipleFactordbl = tier.getEarnValue()/3;
				    		long multipleFactor = multipleFactordbl.intValue();
				    		perkstobegiven = multipleFactor*(9-currentMonth);
				    	}
			    	} else if(currentMonth>=9 && currentMonth<12) {
			    		if(currentMonth==9) {
				    		perkstobegiven = tier.getEarnValue();
				    	}else {
				    		Double multipleFactordbl = tier.getEarnValue()/3;
				    		long multipleFactor = multipleFactordbl.intValue();
				    		perkstobegiven = multipleFactor*(12-currentMonth);
				    	}
			    	}
			    	
			    } else if(tier.getRewardExpiryDateType()!=null && tier.getRewardExpiryDateType().equalsIgnoreCase(OCConstants.PERKS_EXP_TYPE_HALFYEAR)) {
			    	if(currentMonth>=0 && currentMonth<6) {
			    		if(currentMonth==0) {
				    		perkstobegiven = tier.getEarnValue();
				    	}else {
				    		Double multipleFactordbl = tier.getEarnValue()/6;
				    		long multipleFactor = multipleFactordbl.intValue();
				    		perkstobegiven = multipleFactor*(6-currentMonth);
				    	}
			    	} else if(currentMonth>=6 && currentMonth<12) {
			    		if(currentMonth==6) {
				    		perkstobegiven = tier.getEarnValue();
				    	}else {
				    		Double multipleFactordbl = tier.getEarnValue()/6;
				    		long multipleFactor = multipleFactordbl.intValue();
				    		perkstobegiven = multipleFactor*(12-currentMonth);
				    	}
			    	}
			    }else if(tier.getRewardExpiryDateType()!=null && tier.getRewardExpiryDateType().equalsIgnoreCase(OCConstants.PERKS_EXP_TYPE_YEAR)) {
			    	if(currentMonth==0) {
			    		perkstobegiven = tier.getEarnValue();
			    	}else {
			    		Double multipleFactordbl = tier.getEarnValue()/12;
			    		long multipleFactor = multipleFactordbl.intValue();
			    		perkstobegiven = multipleFactor*(12-currentMonth);
			    	}
			    } else if(tier.getRewardExpiryDateType()!=null && tier.getRewardExpiryDateType().equalsIgnoreCase(OCConstants.PERKS_EXP_TYPE_MONTH)) {
			    	
		    		perkstobegiven = tier.getEarnValue();
			    }
			    /*Double multipleFactordbl = tier.getEarnValue()/tier.getRewardExpiryDateValue();
			    long multipleFactor = multipleFactordbl.intValue();
			    perkstobegiven = multipleFactor*(tier.getRewardExpiryDateValue()-currentMonth);
			    logger.info("reward expiry date values>>>>>"+tier.getRewardExpiryDateValue()+" earn value>>>"+tier.getEarnValue());*/
			    
			    
			    //amount.setReceiptAmount(String.valueOf(enteredValue));
			    amount.setEnteredValue(String.valueOf(Math.abs(perkstobegiven)));
			    
				
				customer.setCustomerId(enrollRequest.getCustomer().getCustomerId());
				customer.setFirstName(enrollRequest.getCustomer().getFirstName());
				customer.setLastName(enrollRequest.getCustomer().getLastName());
				customer.setPhone(enrollRequest.getCustomer().getPhone());
				customer.setEmailAddress(enrollRequest.getCustomer().getEmailAddress());
				customer.setBirthday(enrollRequest.getCustomer().getBirthday());
				customer.setAddressLine1(enrollRequest.getCustomer().getAddressLine1()); 
				customer.setCity(enrollRequest.getCustomer().getCity()); 
				customer.setState(enrollRequest.getCustomer().getState());
				customer.setPostal(enrollRequest.getCustomer().getPostal());
				customer.setAnniversary(enrollRequest.getCustomer().getAnniversary());
				
				loyaltyUser.setUserName(enrollRequest.getUser().getUserName());
				loyaltyUser.setOrganizationId(enrollRequest.getUser().getOrganizationId());
				loyaltyUser.setToken(enrollRequest.getUser().getToken());
				
				
				issuanceRequest.setHeader(header);
				issuanceRequest.setMembership(membershipRequest);
				issuanceRequest.setAmount(amount);
				issuanceRequest.setCustomer(customer);
				issuanceRequest.setUser(loyaltyUser);
			    
				LoyaltyTransaction transaction = null;
				transaction = findTransactionByRequestId(requestId);
				LoyaltyTransactionParent tranParent = createNewTransaction(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE); 
				Date date = tranParent.getCreatedDate().getTime();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
				String transDate = df.format(date);
				String responseJson ="";
				String reqJson="";
				Status status1 = null;
				Gson gson = new Gson();
				String userName = null;
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
					logger.info("request json for perks "+reqJson);
					//send issuance request
					logger.info("before calling issuance request inside enrolment>>>>");
					
					LoyaltyIssuanceOCService loyaltyIssuanceOCService = (LoyaltyIssuanceOCService) ServiceLocator.getInstance()
							.getServiceByName(OCConstants.LOYALTY_ISSUANCE_OC_BUSINESS_SERVICE);
					issuanceResponse = loyaltyIssuanceOCService.processIssuanceRequest(issuanceRequest,
							OCConstants.LOYALTY_OFFLINE_MODE,transaction.getId().toString(),transDate,
							OCConstants.DR_TO_LTY_EXTRACTION);
					logger.info("after calling issuance request inside enrolment>>>>");
					responseJson = new Gson().toJson(issuanceResponse, LoyaltyIssuanceResponse.class);	
					logger.info("Response = "+responseJson);
					if(responseJson!=null){
						updateIssuanceTransactionStatus(transaction,responseJson,issuanceResponse);
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
					return;
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
			String responseJson = new Gson().toJson(issuanceResponse, LoyaltyIssuanceResponse.class);
			logger.info("Issuance response : "+responseJson);
			return;
		}
		
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
			
		LoyaltyAutoComm loyaltyAutoComm = getLoyaltyAutoComm(program.getProgramId());
		LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
		//Send Loyalty Registration Email
		if(status.getErrorCode().equals("0") && dbContact.getEmailId() != null && loyaltyAutoComm != null && loyaltyAutoComm.getRegEmailTmpltId() != null){
			//email queue
			autoCommGen.sendEnrollTemplate(loyaltyAutoComm.getRegEmailTmpltId(), ""+contactLoyalty.getCardNumber(), 
					contactLoyalty.getCardPin(), user, dbContact.getEmailId(), dbContact.getFirstName(),
					dbContact.getContactId(), contactLoyalty.getLoyaltyId(), memPwd);
		}
		if (status.getErrorCode().equals("0") && user.isEnableSMS() && loyaltyAutoComm != null && loyaltyAutoComm.getRegSmsTmpltId() != null 
				&& contactLoyalty.getMobilePhone() != null) {
			//sms queue
			Long cid = null;
			if (contactLoyalty.getContact() != null && contactLoyalty.getContact().getContactId() != null) {
				cid = contactLoyalty.getContact().getContactId();
			}
			autoCommGen.sendEnrollSMSTemplate(loyaltyAutoComm.getRegSmsTmpltId(), user, cid, contactLoyalty.getLoyaltyId(),
					contactLoyalty.getMobilePhone(), memPwd);
		}
		
		updateMembershipBalances(enrollRequest, contactLoyalty, program, loyaltyAutoComm,
				dbContact.getEmailId(), dbContact.getUsers(), dbContact.getFirstName(), dbContact.getContactId(), tier);
		
		saveContactLoyalty(contactLoyalty);
		
		logger.info("Exited createMembership method <<<");      
		
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
	private LoyaltyTransaction findRequestBydocSid(String userName, String docSid) throws Exception {
		LoyaltyTransactionDao loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
		return loyaltyTransactionDao.findRequestByDocSid(userName, docSid, OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
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
						logger.info("after phone parse: "+phoneParse);
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
				else if (custFieldAttribute.equals(POSFieldsEnum.homeStore.getOcAttr()) && fieldValue.length() > 0) {

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
				if (method != null && inputContact!=null && params!=null) {
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
		return inputContact;
	}//setContactFields
private Map<String,Object> validateAndSavedbContact(Contacts jsonContact, MailingList mlList, Users user,LoyaltyEnrollRequest enrollRequest) throws Exception {
		
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
			dbContact.setEmailId(validateEmailId(dbContact.getEmailId()));
			dbContact.setMlBits(mlList.getMlBit());
			dbContact.setUsers(user);
			dbContact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_POS);
			dbContact.setEmailStatus(Constants.CONT_STATUS_PURGE_PENDING);
			dbContact.setCreatedDate(Calendar.getInstance());
			dbContact.setModifiedDate(Calendar.getInstance());
			logger.info("SubsidiaryNumber==>"+enrollRequest.getCustomer().getSubsidiaryNumber());
			dbContact.setSubsidiaryNumber((enrollRequest.getCustomer().getSubsidiaryNumber()!=null && 
					!enrollRequest.getCustomer().getSubsidiaryNumber().isEmpty())
					?enrollRequest.getCustomer().getSubsidiaryNumber():Constants.STRING_NILL);
			
			
			
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
			logger.info("Entered before merge contacts");
			dbContact = Utility.mergeContacts(jsonContact, dbContact);
			validateMobilePhoneStatus(dbContact);

			dbContact.setEmailStatus(emailStatus);
			dbContact.setPurged(emailFlag);
			if(purgeFlag) {
				purgeList.checkForValidDomainByEmailId(dbContact);
			}
			
		}
		
		ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
		if(Utility.isModifiedContact(dbContact,jsonContact ))
		{
			logger.info("entered Modified date");
			dbContact.setModifiedDate(Calendar.getInstance());
		}	
		dbContact.setModifiedDate(Calendar.getInstance());//for an existing contact , the loyalty is added newly here hence treat its modified
		contactsDaoForDML.saveOrUpdate(dbContact);
		
		logger.info("----dbContact.getExternalId()----");
		if(jsonContact.getExternalId()!=null && dbContact.getExternalId()!=null && jsonContact.getExternalId().equals(dbContact.getExternalId()))
		LoyaltyProgramHelper.updateLoyaltyMembrshpPhone(dbContact, dbContact.getMobilePhone());//APP-1909
		
		/*MailingListDaoForDML mailingListDaoForDML = (MailingListDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.MAILINGLIST_DAO_FOR_DML);
		if(updateMLFlag) {
			
			mlList.setListSize(mlList.getListSize() + 1);
			mlList.setLastModifiedDate(Calendar.getInstance());
			mailingListDaoForDML.saveOrUpdate(mlList);
		}*/
		
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
private void updateLoyaltyCardStatus(LoyaltyCards loyaltyCard, String status) throws Exception {
	
	LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
	LoyaltyCardsDaoForDML loyaltyCardsDaoForDML = (LoyaltyCardsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);
	
		
		loyaltyCard.setStatus(status);
		//loyaltyCardsDao.saveOrUpdate(loyaltyCard);
		loyaltyCardsDaoForDML.saveOrUpdate(loyaltyCard);
	
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
private String generateMembrshpPwd(String memPwd) throws Exception {
ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
String encPwd = "";
//do {
	//memPwd = RandomStringUtils.randomAlphanumeric(6);
	encPwd = EncryptDecryptLtyMembshpPwd.encrypt(memPwd);
	//} while(loyaltyDao.findByMembrshpPwd(encPwd) != null);
return encPwd;
}
private void saveContactLoyalty(ContactsLoyalty loyalty) throws Exception{

ContactsLoyaltyDaoForDML loyaltyDao = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
loyaltyDao.saveOrUpdate(loyalty);
}
private void saveLoyaltyCard(LoyaltyCards loyaltyCard) throws Exception {

LoyaltyCardsDao loyaltyCardDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
LoyaltyCardsDaoForDML loyaltyCardDaoForDML = (LoyaltyCardsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);
//loyaltyCardDao.saveOrUpdate(loyaltyCard);
loyaltyCardDaoForDML.saveOrUpdate(loyaltyCard);

}
private LoyaltyProgramTier findTier(Long contactId, Long userId, Long loyaltyId, List<LoyaltyProgramTier> tiersList, Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap) throws Exception {


ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
ContactsLoyalty contactsLoyalty = null;
contactsLoyalty = contactsLoyaltyDao.findByContactId(userId, contactId);

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

	Double totPurchaseValue = null;
	LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
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
	}
}
else if(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE.equals(tiersList.get(0).getTierUpgdConstraint())){
Double cumulativeAmount = 0.0;
ListIterator<LoyaltyProgramTier> it = new ArrayList(eligibleMap.keySet()).listIterator(eligibleMap.size());
LoyaltyProgramTier nextKeyTier = null;
while(it.hasPrevious()){
	nextKeyTier = it.previous();
	logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
	logger.info("-------------currTier::"+tiersList.get(0).getTierType());
	if(OCConstants.LOYALTY_PROGRAM_TIER1.equalsIgnoreCase(nextKeyTier.getTierType())){
		return tiersList.get(0);
	}
	Calendar startCal = Calendar.getInstance();
	Calendar endCal = Calendar.getInstance();
	endCal.add(Calendar.MONTH, -eligibleMap.get(nextKeyTier).getTierUpgradeCumulativeValue().intValue());

	String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
	String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);

	
	LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
	cumulativeAmount = Double.valueOf(loyaltyTransactionChildDao.getLoyaltyCumulativePurchase(user.getUserId(),nextKeyTier.getProgramId(), loyaltyId, startDate, endDate));


	if(cumulativeAmount == null || cumulativeAmount <= 0){
		logger.info("cumulative purchase value is empty...");
		continue;
	}
	
	if(cumulativeAmount > 0 && cumulativeAmount >= eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
		return nextKeyTier;
	}
	
}
return tiersList.get(0);
} else if(tiersList.get(0).getTierUpgdConstraint()==null) {
	return tiersList.get(0);
}
else{
	return null;
}

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
	transaction.setSubsidiaryNumber(enrollRequest.getHeader().getSubsidiaryNumber() != null && !enrollRequest.getHeader().getSubsidiaryNumber().trim().isEmpty() ? enrollRequest.getHeader().getSubsidiaryNumber().trim() : getSBS(storeNumber, userId, orgId));
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
private LoyaltyAutoComm getLoyaltyAutoComm(Long programId){
try{
	LoyaltyAutoCommDao autoCommDao = (LoyaltyAutoCommDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
	return autoCommDao.findById(programId);
}catch(Exception e){
	logger.error("Exception in getting auto comm object...", e);
	return null;
}
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
				childTxbonus.getTransChildId(),bonus.getThresholdBonusId());
		
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
private String validateEmailId(String emailId){
if(emailId != null) {
	if(Utility.validateEmail(emailId)) {
		return emailId;
	}
}
return null;
}
private Contacts findOCContact(Contacts jsonContact, Users user) throws Exception {
POSMappingDao posMappingDao = (POSMappingDao)ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
TreeMap<String, List<String>> priorMap =  Utility.getPriorityMap(user.getUserId(), Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
Contacts dbContact = contactsDao.findContactByUniqPriority(priorMap, jsonContact, user.getUserId(), user);
return dbContact;
}
private void validateMobilePhoneStatus(Contacts dbContact) throws Exception{
if(dbContact.getMobilePhone() != null && dbContact.getMobilePhone().trim().length() > 0) {
	try {
		Users user = dbContact.getUsers();
		String phoneStr = Utility.phoneParse(dbContact.getMobilePhone().toString().trim(), user!=null ? user.getUserOrganization() : null );
		if(phoneStr != null ) {
			dbContact.setMobilePhone(phoneStr);
			//to make db contact as default opted-in contact.
			if(user.isOptinMobileByDefault()){
				logger.info("to make db contact as default opted-in contact.");
				dbContact.setMobileOptin(true);
				dbContact.setMobileOptinDate(Calendar.getInstance());
				dbContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
				dbContact.setMobileOptinSource(Constants.CONTACT_OPTIN_MEDIUM_POS);
			}else{
				if(dbContact.getUsers().isEnableSMS() && dbContact.getUsers().isConsiderSMSSettings()){
					dbContact.setMobileStatus(performMobileOptin(dbContact, dbContact.getUsers()));
				}else{
					dbContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
				}
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
private void sendWelcomeEmail(Contacts contact, MailingList mailingList, Users user) {
logger.info("Entered sendWelcomeEmail method >>>>>");
//to send the loyalty related email
EmailQueueDao emailQueueDao = null;
EmailQueueDaoForDML emailQueueDaoForDML = null;
CustomTemplatesDao customTemplatesDao = null;
MyTemplatesDao myTemplatesDao = null;
try{
emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName("emailQueueDao");
emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("emailQueueDaoForDML");

customTemplatesDao = (CustomTemplatesDao)ServiceLocator.getInstance().getDAOByName("customTemplatesDao");
myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
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
			  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
			  if(myTemplates != null)message = myTemplates.getContent();
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
private LoyaltyTransactionExpiry createExpiryTransaction(ContactsLoyalty loyalty,
	Long expiryPoints, Double expiryAmount, Long orgId, Long transChildId ,Long bonusId){

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
	transaction.setBonusId(bonusId);
	
	LoyaltyTransactionExpiryDao loyaltyTransactionExpiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
	LoyaltyTransactionExpiryDaoForDML loyaltyTransactionExpiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
	//loyaltyTransactionExpiryDao.saveOrUpdate(transaction);
	loyaltyTransactionExpiryDaoForDML.saveOrUpdate(transaction);
	
}catch(Exception e){
	logger.error("Exception while logging enroll bonus expiry transaction...",e);
}
return transaction;
}
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
	//set Receipt number
	transaction.setReceiptNumber(enrollRequest.getHeader().getReceiptNumber() != null
			&& !enrollRequest.getHeader().getReceiptNumber().trim().isEmpty()
					? enrollRequest.getHeader().getReceiptNumber()
					: "On Enroll");
	// set Description2 
	transaction.setDescription2(enrollRequest.getHeader().getReceiptNumber() != null
			&& !enrollRequest.getHeader().getReceiptNumber().trim().isEmpty()
			? enrollRequest.getHeader().getReceiptNumber()
			: "On Enroll");
	//set Docsid
	transaction.setDocSID(enrollRequest.getHeader().getDocSID() != null
			&& !enrollRequest.getHeader().getDocSID().trim().isEmpty()
			? enrollRequest.getHeader().getDocSID()
			: null);
	
	
	LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
	LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
	//loyaltyTransactionChildDao.saveOrUpdate(transaction);
	loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
	
}catch(Exception e){
	logger.error("Exception while logging enroll transaction...",e);
}
return transaction;
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
	matchedBonusList.addAll(pointsBonusList);
	matchedBonusList.addAll(amountBonusList);

	 
	long bonusPoints = 0;
	double bonusAmount = 0.0;
	String bonusRate = null;
	boolean bonusflag =false;
	

	if(matchedBonusList != null && matchedBonusList.size() > 0){
		for (LoyaltyThresholdBonus matchedBonus : matchedBonusList) {
			
			bonusflag = false;
			long multiplier = 1;
			
			double afterBalLoyaltyEarned= contactsLoyalty.getTotalLoyaltyEarned() == null ? 0.0 : contactsLoyalty.getTotalLoyaltyEarned();
			double afterBalGiftCardAmt= contactsLoyalty.getTotalGiftcardAmount() == null ? 0.0 : contactsLoyalty.getTotalGiftcardAmount();

			
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
				else if (! matchedBonus.isRecurring() && afterBalLoyaltyEarned >= matchedBonus.getEarnedLevelValue()
						&& (fromLtyBalance == null || fromLtyBalance.doubleValue() < matchedBonus.getEarnedLevelValue())) {
					bonusflag = true;
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
				
				}else if (! matchedBonus.isRecurring() && afterBalGiftCardAmt >= matchedBonus.getEarnedLevelValue()
						&& (fromAmtBalance == null || fromAmtBalance.doubleValue() < matchedBonus.getEarnedLevelValue())) {
					
					bonusflag = true;
				}
				
			}
			if(!bonusflag) continue;
			if(OCConstants.LOYALTY_TYPE_POINTS.equals(matchedBonus.getExtraBonusType())){
				

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
				
			
				bonusRate = Constants.STRING_NILL + matchedBonus.getEarnedLevelValue() + " "
						+ matchedBonus.getEarnedLevelType() + " --> " + matchedBonus.getExtraBonusValue() + " "
						+ matchedBonus.getExtraBonusType();
				
				LoyaltyTransactionChild childTxbonus = createBonusTransaction(enrollRequest, contactsLoyalty, bonusAmount, bonusPoints, OCConstants.LOYALTY_TYPE_AMOUNT, bonusRate);
				
				logger.info("balances before balance object = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
				createExpiryTransaction(contactsLoyalty, bonusPoints, bonusAmount, contactsLoyalty.getOrgId(), 
						childTxbonus.getTransChildId(),matchedBonus.getThresholdBonusId());
			
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
			else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(matchedBonus.getExtraBonusType())){
				

				
				String result = Utility.truncateUptoTwoDecimal(multiplier*matchedBonus.getExtraBonusValue());
				if (result != null)
					bonusAmount = Double.parseDouble(result);
				bonusRate = Constants.STRING_NILL + matchedBonus.getEarnedLevelValue() + " "
						+ matchedBonus.getEarnedLevelType() + " --> " + matchedBonus.getExtraBonusValue() + " "
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
				
				
				//bonusAmount = matchedBonus.getExtraBonusValue();
				bonusRate = ""+matchedBonus.getEarnedLevelValue()+" "+matchedBonus.getEarnedLevelType()+
						" --> "+matchedBonus.getExtraBonusValue()+" "+OCConstants.LOYALTY_TYPE_AMOUNT;

				
				LoyaltyTransactionChild childTxbonus = createBonusTransaction(enrollRequest, contactsLoyalty, bonusAmount, bonusPoints, OCConstants.LOYALTY_TYPE_AMOUNT, bonusRate);

				logger.info("balances before balance object = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
				createExpiryTransaction(contactsLoyalty, bonusPoints, bonusAmount, contactsLoyalty.getOrgId(), 
						childTxbonus.getTransChildId(),matchedBonus.getThresholdBonusId());
				/*if(loyaltyProgramTier != null){
					// CALL CONVERSION
					applyConversionRules(contactsLoyalty, childTxbonus, program, loyaltyProgramTier);
					// CALL TIER UPGD
					loyaltyProgramTier = applyTierUpgradeRule(contactsLoyalty, program, childTxbonus, loyaltyProgramTier);
				}*/
			}
			
			LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
			Contacts contact = null;
			LoyaltyAutoComm autoComm = getLoyaltyAutoComm(program.getProgramId());
			if(bonusflag && autoComm != null && autoComm.getThreshBonusEmailTmpltId() != null && contactsLoyalty.getContact() != null &&
					contactsLoyalty.getContact().getContactId() != null){
				contact = findContactById(contactsLoyalty.getContact().getContactId());
				if(contact != null && contact.getEmailId() != null){
					autoCommGen.sendEarnBonusTemplate(autoComm.getThreshBonusEmailTmpltId(), ""+contactsLoyalty.getCardNumber(),
							contactsLoyalty.getCardPin(), contact.getUsers(), contact.getEmailId(), contact.getFirstName(),
							contact.getContactId(), contactsLoyalty.getLoyaltyId());
				}
			}
			UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = userDao.findByUserId(contactsLoyalty.getUserId());
			if(user.isEnableSMS() && bonusflag && autoComm != null && autoComm.getThreshBonusSmsTmpltId() != null) { 
				Long contactId = null;	
				if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
					contactId = contactsLoyalty.getContact().getContactId();
				}
				autoCommGen.sendEarnBonusSMSTemplate(autoComm.getThreshBonusSmsTmpltId(), user, contactId,
						contactsLoyalty.getLoyaltyId(), null);
			}
			
			
			
			
			
		}
	}

	

}catch(Exception e){
	logger.error("Exception in update threshold bonus...", e);
	throw new LoyaltyProgramException("Exception in threshold bonus...");
}
logger.debug("<<<<<<<<<<<<< completed updateThresholdBonus");
//return bonusArr;
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
private Contacts findContactById(Long cid) throws Exception {
ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
return contactsDao.findById(cid);
}

private ContactsLoyalty prepareLoyaltyMembership(String mbershipNumber, String mbershipType, String cardpin, String phone, String emailId,
	String optInMedium, String subsidiary, String storeNumber, String mode, LoyaltyProgram program, 
	LoyaltyProgramTier linkedTierObj, Long cardSetId,String empId,String termId, String sourceType,Users user) throws Exception {
	//logger.info("Entered prepareContactsLoyaltyObject >>>>>");
	ContactsLoyalty contactLoyalty = new ContactsLoyalty();
	contactLoyalty.setCardNumber(mbershipNumber);
	contactLoyalty.setMembershipType(mbershipType);
	contactLoyalty.setMobilePhone(phone);
	contactLoyalty.setEmailId(emailId);
	contactLoyalty.setCardPin(cardpin == null ? "" : cardpin);
	contactLoyalty.setCreatedDate(Calendar.getInstance());
	//contactLoyalty.setOptinMedium(optInMedium);
	contactLoyalty.setContactLoyaltyType(optInMedium);
	contactLoyalty.setSourceType(sourceType);
	contactLoyalty.setSubsidiaryNumber((subsidiary!=null && !subsidiary.isEmpty())?subsidiary : getSBS(storeNumber, user.getUserId(), user.getUserOrganization().getUserOrgId()));
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
private void deleteDCard(LoyaltyCards loyaltyCard) throws Exception{
	
	LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
	LoyaltyCardsDaoForDML loyaltyCardsDaoForDML = (LoyaltyCardsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);
	//loyaltyCardsDao.deleteBy(loyaltyCard.getCardId());
	loyaltyCardsDaoForDML.deleteBy(loyaltyCard.getCardId());
	
}
private ContactsLoyalty findMembershpByCard(String cardNumber, Long programId, Long userId) throws Exception{
	
	ContactsLoyalty loyalty = null;
	ContactsLoyaltyDao contactLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
	loyalty = contactLoyaltyDao.findByProgram(cardNumber, programId, userId);
	return loyalty;
}
private LoyaltyEnrollResponse convertGiftToLoyalty(LoyaltyEnrollRequest enrollRequest,
		ResponseHeader responseHeader, LoyaltyProgram program, LoyaltyCards card, String mode, Users user,
		MailingList mlList, LoyaltyProgramTier linkedTierObj, String memberShipType) throws Exception {

	logger.info("Convert gift to loyalty membership..");

	LoyaltyEnrollResponse enrollResponse = null;
	ContactsLoyalty contactLoyalty = null;

	List<POSMapping> contactPOSMap = null;
	POSMappingDao posMappingDao = (POSMappingDao) ServiceLocator.getInstance()
			.getDAOByName(OCConstants.POSMAPPING_DAO);
	contactPOSMap = posMappingDao.findByType("'" + Constants.POS_MAPPING_TYPE_CONTACTS + "'", user.getUserId());

	Contacts jsonContact = new Contacts();
	jsonContact.setUsers(user);

	if (contactPOSMap != null) {
		jsonContact = setContactFields(jsonContact, contactPOSMap, enrollRequest, memberShipType);
	}
	Map<String, Object> contactAndDataFlags = validateAndSavedbContact(jsonContact, mlList, user,enrollRequest);
	Contacts dbContact = (Contacts) contactAndDataFlags.get("dbContact");
	boolean isExists = (Boolean) contactAndDataFlags.get("isExists");

	if (isExists) {
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		contactLoyalty = contactsLoyaltyDao.findByContactIdStrAndPrgmId(user.getUserId(),
				dbContact.getContactId() + "", program.getProgramId());
		if (contactLoyalty != null) {
			Customer customer = prepareCustomer(dbContact);

			Status status = new Status("111563",
					PropertyUtil.getErrorMessage(111563, OCConstants.ERROR_LOYALTY_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		}
	}

	ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML) ServiceLocator.getInstance()
			.getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
	dbContact.setLoyaltyCustomer((byte) 1);

	List<LoyaltyProgramTier> tierList = null;
	if (linkedTierObj == null) {// added condition
		tierList = validateTierList(program.getProgramId(), user.getUserId());

		if (tierList == null || tierList.size() == 0
				|| !OCConstants.LOYALTY_PROGRAM_TIER1.equals(tierList.get(0).getTierType())) {
			Status status = new Status("111555",
					PropertyUtil.getErrorMessage(111555, OCConstants.ERROR_LOYALTY_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		}
	}

	contactLoyalty = findMembershpByCard(card.getCardNumber(), card.getProgramId(), user.getUserId());
	contactLoyalty.setContact(dbContact);
	contactLoyalty.setCreatedDate(Calendar.getInstance());
	contactLoyalty.setCustomerId(dbContact.getExternalId());
	if (jsonContact.getMobilePhone() != null) {
		contactLoyalty.setMobilePhone(jsonContact.getMobilePhone());
	} else {
		contactLoyalty.setMobilePhone(dbContact.getMobilePhone());
	}
	contactLoyalty.setRewardFlag(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL);

	// generate a pwd and encrypt it and save it...
	String memPwd = "";
	logger.debug("encPwd === "+encPwd);
	if(encPwd == null) {
		 memPwd = RandomStringUtils.randomAlphanumeric(6);
		
		encPwd = generateMembrshpPwd(memPwd);
	}
	contactLoyalty.setMembershipPwd(encPwd);
	if(enrollRequest.getHeader().getSourceType() != null && 
			!enrollRequest.getHeader().getSourceType().isEmpty() && 
			(enrollRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) || 
					enrollRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP))){

		 
		
		dbContact.setOptinMedium(enrollRequest.getHeader().getSourceType());
		}	
	String fpRecognitionFlagString = enrollRequest.getMembership().getFingerprintValidation();
	if(fpRecognitionFlagString!=null && (fpRecognitionFlagString.equalsIgnoreCase("false")
			|| fpRecognitionFlagString.trim().equalsIgnoreCase("True"))) {
		
		boolean fpRecognitionFlag = Boolean.parseBoolean(fpRecognitionFlagString.toLowerCase());
		contactLoyalty.setFpRecognitionFlag(fpRecognitionFlag);
	}
	if(enrollRequest.getCustomer().getInstanceId()!=null && !enrollRequest.getCustomer().getInstanceId().isEmpty()) {
		dbContact.setInstanceId(enrollRequest.getCustomer().getInstanceId());
	}
	if(enrollRequest.getCustomer().getDeviceType()!=null && !enrollRequest.getCustomer().getDeviceType().isEmpty()) {
		dbContact.setDeviceType(enrollRequest.getCustomer().getDeviceType());
	}
	if(Utility.isModifiedContact(dbContact,jsonContact ))
	{
		logger.info("entered Modified date");
		dbContact.setModifiedDate(Calendar.getInstance());
	}	
	contactsDaoForDML.saveOrUpdate(dbContact);//APP-1775
	saveContactLoyalty(contactLoyalty);

	card.setStatus(OCConstants.LOYALTY_CARD_STATUS_ENROLLED);
	saveLoyaltyCard(card);

	LoyaltyProgramTier tier = null;
	Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap = null;
	if (linkedTierObj == null) {// added condition
		// Prepare eligible tiers map
		Iterator<LoyaltyProgramTier> iterTier = tierList.iterator();
		eligibleMap = new LinkedHashMap<LoyaltyProgramTier, LoyaltyProgramTier>();
		LoyaltyProgramTier prevtier = null;
		LoyaltyProgramTier nexttier = null;

		while (iterTier.hasNext()) {
			nexttier = iterTier.next();
			if (OCConstants.LOYALTY_PROGRAM_TIER1.equals(nexttier.getTierType())) {
				eligibleMap.put(nexttier, null);
			} else {
				if ((Integer.valueOf(prevtier.getTierType().substring(5)) + 1) == Integer
						.valueOf(nexttier.getTierType().substring(5))
						&& prevtier.getTierUpgdConstraintValue() != null) {
					eligibleMap.put(nexttier, prevtier);
					logger.info("eligible tier =" + nexttier.getTierType() + " upgdconstrant value = "
							+ prevtier.getTierUpgdConstraintValue());
				}
			}
			prevtier = nexttier;
		}

		tier = findTier(dbContact.getContactId(), user.getUserId(), contactLoyalty.getLoyaltyId(), tierList,
				eligibleMap);
		contactLoyalty.setProgramTierId(tier.getTierId());
		saveContactLoyalty(contactLoyalty);
		
	} else {
		tier = linkedTierObj;
		contactLoyalty.setProgramTierId(tier.getTierId());
		saveContactLoyalty(contactLoyalty);
	}
	MembershipResponse accountResponse = new MembershipResponse();

	accountResponse.setCardNumber(String.valueOf(contactLoyalty.getCardNumber()));
	//accountResponse.setPassword(contactLoyalty.getMembershipPwd() != null ? contactLoyalty.getMembershipPwd() : Constants.STRING_NILL );
	accountResponse.setCardPin(card.getCardPin());
	accountResponse.setPhoneNumber("");

	if (tier != null && !"Pending".equalsIgnoreCase(tier.getTierType())) {
		if (program.getTierEnableFlag() == OCConstants.FLAG_YES) {
			accountResponse.setTierLevel(tier.getTierType());
			accountResponse.setTierName(tier.getTierName());
		} else {
			accountResponse.setTierLevel("");
			accountResponse.setTierName("");
		}
		if (program.getMembershipExpiryFlag() == 'Y' && tier.getMembershipExpiryDateType() != null
				&& tier.getMembershipExpiryDateValue() != null) {
			accountResponse.setExpiry(LoyaltyProgramHelper.getMbrshipExpiryDate(contactLoyalty.getCreatedDate(),
					contactLoyalty.getTierUpgradedDate(), false, tier.getMembershipExpiryDateType(),
					tier.getMembershipExpiryDateValue()));
		} else {
			accountResponse.setExpiry("");
		}

	} else {
		accountResponse.setTierLevel("");
		accountResponse.setTierName("");
		accountResponse.setExpiry("");
	}

	Customer customer = prepareCustomer(dbContact);
	//List<MatchedCustomer> customers = prepareMatchedCustomers(customer, "" + contactLoyalty.getCardNumber());?

	Status status = new Status("0", "Enrollment was successful", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
	//enrollResponse = prepareEnrollmentResponse(responseHeader, accountResponse, customers, status);

	String store = enrollRequest.getHeader().getStoreNumber() != null
			? enrollRequest.getHeader().getStoreNumber().trim()
			: enrollRequest.getHeader().getStoreNumber();
	LoyaltyTransactionChild transChild = createSuccessfulTransaction(enrollRequest, contactLoyalty, responseHeader,
			user.getUserId(), user.getUserOrganization().getUserOrgId(), program.getProgramId(),
			OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD, enrollRequest.getHeader().getDocSID(), "GiftToLoyalty", store,
			enrollRequest.getHeader().getEmployeeId(), enrollRequest.getHeader().getTerminalId());

	/*if ("Pending".equalsIgnoreCase(tier.getTierType())) {
		LoyaltyEnrollCPVThread cpvThread = new LoyaltyEnrollCPVThread(eligibleMap, user, contactLoyalty, tierList,
				transChild, program, enrollRequest);
		Thread th = new Thread(cpvThread);
		th.start();

		return enrollResponse;
	}*/

	LoyaltyAutoComm loyaltyAutoComm = getLoyaltyAutoComm(program.getProgramId());
	LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
	// Send Loyalty Registration Email
	if (status.getErrorCode().equals("0") && dbContact.getEmailId() != null && loyaltyAutoComm != null
			&& loyaltyAutoComm.getRegEmailTmpltId() != null) {
		// email queue
		/*
		 * autoCommGen.sendEnrollTemplate(loyaltyAutoComm.getRegEmailTmpltId(),
		 * ""+contactLoyalty.getCardNumber(), contactLoyalty.getCardPin(), user,
		 * dbContact.getEmailId(), dbContact.getFirstName(), dbContact.getContactId());
		 */

		autoCommGen.sendEnrollTemplate(loyaltyAutoComm.getRegEmailTmpltId(), "" + contactLoyalty.getCardNumber(),
				contactLoyalty.getCardPin(), user, dbContact.getEmailId(), dbContact.getFirstName(),
				dbContact.getContactId(), contactLoyalty.getLoyaltyId(), memPwd);
	}
	if (status.getErrorCode().equals("0") && user.isEnableSMS() && loyaltyAutoComm != null
			&& loyaltyAutoComm.getRegSmsTmpltId() != null && contactLoyalty.getMobilePhone() != null) {
		// sms queue
		Long cid = null;
		if (contactLoyalty.getContact() != null && contactLoyalty.getContact().getContactId() != null) {
			cid = contactLoyalty.getContact().getContactId();
		}
		/*
		 * autoCommGen.sendEnrollSMSTemplate(loyaltyAutoComm.getRegSmsTmpltId(), user,
		 * cid, contactLoyalty.getLoyaltyId(), enrollRequest.getCustomer().getPhone());
		 */

		autoCommGen.sendEnrollSMSTemplate(loyaltyAutoComm.getRegSmsTmpltId(), user, cid,
				contactLoyalty.getLoyaltyId(), contactLoyalty.getMobilePhone(), memPwd);
	}

	updateMembershipBalances(enrollRequest, contactLoyalty, program, loyaltyAutoComm, dbContact.getEmailId(),
			dbContact.getUsers(), dbContact.getFirstName(), dbContact.getContactId(), tier);

	saveContactLoyalty(contactLoyalty);

	return enrollResponse;
}
private String getSBS(String storeNO, Long userID, Long orgID) {
	
	//written to get subsidiary_number from database instead of SBToOC json as plugins are not ready to get subsidiary number in json
			if(storeNO == null || storeNO.isEmpty() ) return null;
			try{
					OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
					UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
					//UsersDomainsDao UsersDomainsDao = (UsersDomainsDao) SpringUtil.getBean("usersDomainsDao");
					Long domainId = userDao.findDomainByUserId(userID);
					OrganizationStores orgStores = organizationStoresDao.findOrgByDomain(orgID, domainId, storeNO);
					return orgStores!=null ? orgStores.getSubsidiaryId() : null;
			}catch(Exception e){
				logger.info("Exception::",e);
			}
			return null;
}

}

