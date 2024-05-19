package org.mq.optculture.controller.loyalty;

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
import org.mq.marketer.campaign.beans.ContactsLoyaltyStage;
import org.mq.marketer.campaign.beans.LoyaltyCards;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.LoyaltyTransactionParent;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyStageDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyStageDaoForDML;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.loyalty.LoyaltyEnrollmentOCService;
import org.mq.optculture.data.dao.LoyaltyCardsDao;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDao;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.ocloyalty.Customer;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyUser;
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.ocloyalty.MembershipRequest;
import org.mq.optculture.model.ocloyalty.MembershipResponse;
import org.mq.optculture.model.ocloyalty.RequestHeader;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.google.gson.Gson;

public class AddMemberController extends GenericForwardComposer {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	//private Long userId;
	private Users currUser;
	private Listbox programTypeLbId,genderTypeLbId,storeTypeLbId;
	private LoyaltyProgramDao loyaltyProgramDao;
	private ContactsLoyaltyDao contactsLoyaltyDao;
	private UsersDao usersDao;
	private OrganizationStoresDao orgDao;
	private Textbox cardNumberTbId,emailTbId,mobileTbId,firstNameTbId,lastNameTbId,birthdayTbId,anniversaryTbId,streetTbId,cityTbId,stateTbId,
	                countryTbId,postalCodeTbId;
	private Button savBtnId;
	private final String TRANSFER_DEST_CARDNO="Transfer_Dest_CardNo";
	private final String FOR_USER="FOR_USER";
	
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		
		currUser = GetUser.getUserObj();
		loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		orgDao = (OrganizationStoresDao) ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
		usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		
		setActiveLtyPrgm();
		setDefaultStores();
		defaultMobileBasedSelected();
	}
	
	public void setActiveLtyPrgm() {
		
		try {
			List<LoyaltyProgram> prgmsList = loyaltyProgramDao.fetchAllActiveProgramsNames(currUser.getUserId());
			
			if (prgmsList != null && prgmsList.size() > 0) {
				prgmsList.forEach(prgm -> {
					Listitem item = new Listitem(prgm.getProgramName());
					item.setValue(prgm.getProgramName());
					item.setParent(programTypeLbId);
				});
				programTypeLbId.setSelectedIndex(0);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		
		
	}
	
	private void setDefaultStores() {
		
		try {
		Long orgId = currUser.getUserOrganization().getUserOrgId();
		Long domainId = usersDao.findDomainByUserId(currUser.getUserId());
		//OrganizationStoresDao orgDao = (OrganizationStoresDao) ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
		//List<OrganizationStores> storeIdList = orgDao.findStore(orgId,domainId.toString());
		List<OrganizationStores> storeIdList = orgDao.findAllStores(orgId);
		
		logger.info("storeIdList" + storeIdList);
		if (storeIdList == null || storeIdList.size() == 0)return;
		
		for (OrganizationStores store : storeIdList) {
			 Listitem li = new Listitem(store.getHomeStoreId());
			 li.setParent(storeTypeLbId);
		  }
		} catch(Exception e) {
			
		}

	}
	
	public void onSelect$programTypeLbId() {
		
		String selProgramName = programTypeLbId.getSelectedItem().getValue();
		LoyaltyProgram ltyProgram = loyaltyProgramDao.getProgramIdByName(selProgramName,currUser.getUserId());
		
		if(ltyProgram.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
			
			cardNumberTbId.setDisabled(true);
			cardNumberTbId.setValue("");
		}
		else 
			cardNumberTbId.setDisabled(false);
	}
	
	public void defaultMobileBasedSelected() {
		
		String selProgramName = programTypeLbId.getSelectedItem().getValue();
		LoyaltyProgram ltyProgram = loyaltyProgramDao.getProgramIdByName(selProgramName,currUser.getUserId());
		
		if(ltyProgram.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
			
			cardNumberTbId.setDisabled(true);
			cardNumberTbId.setValue("");
		}
		else 
			cardNumberTbId.setDisabled(false);
	}
	
	public LoyaltyEnrollResponse prepareEnrollmentRequest(LoyaltyProgram ltyProgram) {
		
		LoyaltyEnrollRequest loyaltyEnrollRequest= new LoyaltyEnrollRequest();
		LoyaltyEnrollResponse responseObject = new LoyaltyEnrollResponse();
		RequestHeader requestHeader =new RequestHeader();
		MembershipRequest membershipRequest = new MembershipRequest();
		Customer customer = new Customer();
		LoyaltyUser loyaltyUser = new LoyaltyUser();
		
		
			
			String requestId = OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+currUser.getToken()+"_"+System.currentTimeMillis();
			requestHeader.setRequestId(requestId);
			Calendar syscal = new MyCalendar(Calendar.getInstance(), null,
					MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
			requestHeader.setRequestDate(syscal.toString());
			String storenum = storeTypeLbId.getSelectedItem().getLabel();
			logger.info("store number>>"+storenum);
			requestHeader.setStoreNumber(storenum!=null && !storenum.isEmpty()?storenum:"");
			requestHeader.setSourceType(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MANUAL);
			
			if(ltyProgram!=null && ltyProgram.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
				
				membershipRequest.setPhoneNumber(mobileTbId.getText().trim());
				membershipRequest.setCardNumber("");
			}else {
				
				membershipRequest.setCardNumber(cardNumberTbId.getText().trim());
				if(membershipRequest.getCardNumber() == null || membershipRequest.getCardNumber().trim().length() == 0) {
						
					membershipRequest.setIssueCardFlag("Y");
				}
				membershipRequest.setPhoneNumber("");
			}
			
			customer.setCustomerId("");
			customer.setFirstName(firstNameTbId.getText().trim());
			customer.setLastName(lastNameTbId.getText().trim());
			customer.setPhone(mobileTbId.getText().trim());
			customer.setEmailAddress(emailTbId.getText().trim());
			customer.setBirthday(birthdayTbId.getText().trim()); 
			customer.setCity(cityTbId.getText().trim()); 
			customer.setState(stateTbId.getText().trim());
			customer.setPostal(postalCodeTbId.getText().trim());
			customer.setAnniversary(anniversaryTbId.getText().trim());
			customer.setCountry(countryTbId.getText().trim());
			customer.setGender(genderTypeLbId.getSelectedItem().getValue());
			customer.setAddressLine1(streetTbId.getText().trim());
			customer.setCreatedDate(syscal.toString());
			
			loyaltyUser.setUserName(Utility.getOnlyUserName(currUser.getUserName()));
			loyaltyUser.setOrganizationId(currUser.getUserOrganization().getOrgExternalId());
			loyaltyUser.setToken(currUser.getUserOrganization().getOptSyncKey());
			
			loyaltyEnrollRequest.setHeader(requestHeader);
			loyaltyEnrollRequest.setMembership(membershipRequest);
			loyaltyEnrollRequest.setCustomer(customer);
			loyaltyEnrollRequest.setUser(loyaltyUser);
			
			//find duplicate request
			ContactsLoyaltyStage loyaltyStage = null;
				
				LoyaltyTransactionParent tranParent = createNewTransaction(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);
				Date date = tranParent.getCreatedDate().getTime();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
				String transDate = df.format(date);
				
				LoyaltyEnrollResponse enrollResponse = null;
				//String userName = null;
				Status status = null;
				String responseJson = Constants.STRING_NILL;
				String reqJson="";
				Gson gson = new Gson();
				try{
					
					ResponseHeader responseHeader = new ResponseHeader();
					responseHeader.setRequestDate(Constants.STRING_NILL);
					responseHeader.setRequestId(Constants.STRING_NILL);
					responseHeader.setTransactionDate(transDate);
					responseHeader.setTransactionId(Constants.STRING_NILL+tranParent.getTransactionId());
					
					
					try{
						reqJson = gson.toJson(loyaltyEnrollRequest, LoyaltyEnrollRequest.class);
						logger.info("reqJson : "+reqJson);
					}catch(Exception e){
						status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
						responseJson = gson.toJson(enrollResponse);
						updateEnrollmentTransaction(tranParent, enrollResponse, null);
						logger.info("Response = "+responseJson);
						logger.error("Exception in parsing request json ...",e);
						return null;
					}
			
			
			LoyaltyEnrollmentOCService enrollService = (LoyaltyEnrollmentOCService)ServiceLocator.getInstance().getServiceById(OCConstants.LOYALTY_ENROLMENT_OC_BUSINESS_SERVICE);
			responseObject = enrollService.processEnrollmentRequest(loyaltyEnrollRequest, 
					OCConstants.LOYALTY_OFFLINE_MODE, ""+tranParent.getTransactionId(), transDate);
			responseJson = new Gson().toJson(responseObject, LoyaltyEnrollResponse.class);	
			logger.info("Response = "+responseJson);
			
			}catch(Exception e){
				logger.error("Error in add member enrolment", e);
				responseJson = "{\"ENROLLMENTRESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101000\",\"MESSAGE\":\"Server error  101000.\",\"STATUS\":\"Failure\"}}}";
				logger.info("Response = "+responseJson);
				
			}finally{
				if(loyaltyStage != null) deleteRequestFromStageTable(loyaltyStage);
				
				logger.info("Completed prepareEnrolFromDRRequest");
			}
			return responseObject;	
			
		
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
	
	public void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyEnrollResponse response){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			transaction.setJsonResponse(responseJson);
			if (response.getMembership() != null && response.getMembership().getCardNumber() != null &&
					!response.getMembership().getCardNumber().trim().isEmpty()) {
				transaction.setCardNumber(response.getMembership().getCardNumber());
			} else {
				transaction.setCardNumber(response.getMembership() == null ? "" : response.getMembership().getPhoneNumber());
			}
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}
	
	private LoyaltyEnrollResponse prepareEnrollmentResponse(ResponseHeader header, MembershipResponse membershipResponse,
			List<MatchedCustomer> matchedCustomers, Status status) throws BaseServiceException {
		LoyaltyEnrollResponse enrollResponse = new LoyaltyEnrollResponse();
		enrollResponse.setHeader(header);
		if(membershipResponse == null){
			membershipResponse = new MembershipResponse();
			membershipResponse.setCardNumber(Constants.STRING_NILL);
			membershipResponse.setCardPin(Constants.STRING_NILL);
			membershipResponse.setExpiry(Constants.STRING_NILL);
			membershipResponse.setPhoneNumber(Constants.STRING_NILL);
			membershipResponse.setTierLevel(Constants.STRING_NILL);
			membershipResponse.setTierName(Constants.STRING_NILL);
		}
		if(matchedCustomers == null){
			matchedCustomers = new ArrayList<MatchedCustomer>();
		}
		enrollResponse.setMembership(membershipResponse);
		enrollResponse.setMatchedCustomers(matchedCustomers);
		enrollResponse.setStatus(status);
		return enrollResponse;
	}
	
	private void updateEnrollmentTransaction(LoyaltyTransactionParent trans, LoyaltyEnrollResponse enrollResponse, String userName) {
		
		try{
			LoyaltyTransactionParentDao parentDao = (LoyaltyTransactionParentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO);
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);
			
			if(userName != null){
				trans.setUserName(userName);
			}
			if(enrollResponse.getStatus() != null) {
				trans.setStatus(enrollResponse.getStatus().getStatus());
				trans.setErrorMessage(enrollResponse.getStatus().getMessage());
			}
			if(enrollResponse.getHeader() != null){
				trans.setRequestId(enrollResponse.getHeader().getRequestId());
				trans.setRequestDate(enrollResponse.getHeader().getTransactionDate());
			}
			if(enrollResponse.getMembership() != null) {
					trans.setMembershipNumber(enrollResponse.getMembership().getCardNumber());
					trans.setMobilePhone(enrollResponse.getMembership().getPhoneNumber());
			}
			if(enrollResponse.getMatchedCustomers() != null) {
				//trans.setMobilePhone(enrollResponse.getMatchedCustomers().getPhone());
			}
			//parentDao.saveOrUpdate(trans);
			parentDaoForDML.saveOrUpdate(trans);
		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
	}

	private static void deleteRequestFromStageTable(ContactsLoyaltyStage loyaltyStage) {

		try{

			ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
			ContactsLoyaltyStageDaoForDML contactsLoyaltyStageDaoForDML = (ContactsLoyaltyStageDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO_FOR_DML);
			logger.info("deleting loyalty stage record...");
			//contactsLoyaltyStageDao.delete(loyaltyStage);
			contactsLoyaltyStageDaoForDML.delete(loyaltyStage);

		}catch(Exception e){
			logger.error("Exception in while deleting request record from staging table...", e);
		}

	}
	
	
	public LoyaltyTransaction logEnrollmentTransactionRequest(LoyaltyEnrollRequest requestObject, String jsonRequest, String mode){
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
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_ENROLMENT);
			transaction.setUserDetail(requestObject.getUser().getUserName()+"__"+requestObject.getUser().getOrganizationId());
			transaction.setCustomerId(requestObject.getCustomer().getCustomerId().trim());
			//transaction.setDocSID(requestObject.getHeader().getDocSID().trim());
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
	
	public void onClick$savBtnId() {
		
		String selProgramName = programTypeLbId.getSelectedItem().getValue();
		String cardNumber = cardNumberTbId.getText().trim();
		LoyaltyProgram ltyProgram = loyaltyProgramDao.getProgramIdByName(selProgramName,currUser.getUserId());
		//LoyaltyProgram ltyProgram = loyaltyProgramDao.findById(prgmId);
		boolean isCardAvailable = false;
		boolean mobileBased = false;
		
		if(ltyProgram.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE))
			mobileBased = true;	
		
		try {
		
			
		if(ltyProgram!=null && !mobileBased && cardNumber!=null && !cardNumber.isEmpty()) {
			
			LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
			//ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			ContactsLoyalty contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(currUser.getUserId(),cardNumber);
			
			if(contactsLoyalty!=null) {
				
				Long contactsLtyPrgmId = contactsLoyalty.getProgramId();
				LoyaltyProgram EnroLtyProgramName = loyaltyProgramDao.findById(contactsLtyPrgmId);
				
				MessageUtil.setMessage(" Contact already enrolled to - "+EnroLtyProgramName.getProgramName(), "color:red", "TOP");
				return;
				
			}
			List<LoyaltyCards> listOfCards = loyaltyCardsDao.findInventoryCards(currUser.getUserId(),ltyProgram.getProgramId());
			if(listOfCards!=null && !listOfCards.isEmpty()) {
				
				for(LoyaltyCards card :listOfCards) {
					
					if(card.getCardNumber().equalsIgnoreCase(cardNumber)) {
						
						isCardAvailable = true;
						break;
					}
					
				}
				
				if(!isCardAvailable) {
					
					MessageUtil.setMessage(" Please select respective program associated with card number", "color:red", "TOP");
					return;
					
				} 
				
			}else {
				MessageUtil.setMessage(" No inventory cards available for this program", "color:red", "TOP");
				return;
			}
			
		}else if(mobileBased && mobileTbId.getValue()!=null && !mobileTbId.getValue().isEmpty()) {
			
            ContactsLoyalty contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(currUser.getUserId(),mobileTbId.getText().trim());
			
			if(contactsLoyalty!=null) {
				
				Long contactsLtyPrgmId = contactsLoyalty.getProgramId();
				LoyaltyProgram EnroLtyProgramName = loyaltyProgramDao.findById(contactsLtyPrgmId);
				
				MessageUtil.setMessage(" Contact already enrolled to - "+EnroLtyProgramName.getProgramName(), "color:red", "TOP");
				return;
				
			}
			
		}
		
		}catch (Exception e) {
			// TODO Auto-generated catch block
		}
		
		if(!validateFields(ltyProgram)) {
			return;
		}
		
		LoyaltyEnrollResponse enrolResponseObject = prepareEnrollmentRequest(ltyProgram);
		
		if(enrolResponseObject.getStatus().getMessage().contains("successful") && enrolResponseObject.getStatus().getErrorCode().equals("0")) {
			
			org.zkoss.zul.Messagebox.Button confirm= Messagebox.show(" Member Created Successful. Do you want to redirect to view contact?", "Confirm",
					new Messagebox.Button[] {Messagebox.Button.NO, Messagebox.Button.YES },
					Messagebox.INFORMATION, null, null);
			
			if(mobileBased)
				session.setAttribute(TRANSFER_DEST_CARDNO, enrolResponseObject.getMembership().getPhoneNumber().trim());
			else
				session.setAttribute(TRANSFER_DEST_CARDNO, cardNumberTbId.getText().trim());
			
			session.setAttribute(FOR_USER, currUser);
			
			if(confirm==null || !confirm.equals(Messagebox.Button.YES)) return;
			else if (confirm.equals(Messagebox.Button.YES))
				Redirect.goTo(PageListEnum.LOYALTY_CUSTOMER_LOOKUP);
			
		} else {
			
			MessageUtil.setMessage(enrolResponseObject.getStatus().getMessage(), "Color:red", "Top");
			return;
		}
		
		
		
		
	}
	
	public boolean validateFields(LoyaltyProgram ltyProgram) {
		
		String cardNumber = cardNumberTbId.getText().trim();
		String mobileStr = mobileTbId.getText().trim();
		if(!(ltyProgram.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE))) {
			
			if((cardNumber==null || cardNumber.isEmpty())) {
				
				MessageUtil.setMessage(" Please enter card number", "Color:red", "Top");
				cardNumberTbId.setFocus(true);
				return false;
			}
		} else if(ltyProgram.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
			
			if((mobileStr==null || mobileStr.isEmpty())) {
				
				MessageUtil.setMessage(" Please enter mobile number", "Color:red", "Top");
				mobileTbId.setFocus(true);
				return false;
			}
		}
		
		String emailStr = emailTbId.getText().trim();
		if(emailStr.isEmpty() || emailStr==null) {
			MessageUtil.setMessage(" Please enter e-mail address", "Color:red", "Top");
			emailTbId.setFocus(true);
			return false;
		}
		
		//String emailStr = emailTbId.getText().trim();
		if(emailStr!=null && emailStr.length() >0 && !Utility.validateEmail(emailStr)) {
			MessageUtil.setMessage(" Please enter a valid e-mail address", "Color:red", "Top");
			emailTbId.setFocus(true);
			return false;
		}
		
		//String mobileStr = mobileTbId.getText().trim();
		if(mobileStr.isEmpty() || mobileStr==null) {
			MessageUtil.setMessage(" Please enter mobile number", "Color:red", "Top");
			mobileTbId.setFocus(true);
			return false;
		}
		
		if(mobileStr!= null  && !mobileStr.isEmpty()) {
			String patterns = mobileTbId.getValue();
			StringBuilder sb = new StringBuilder();
			//String number = "";
			char[] alph = patterns.toCharArray();
			for(char c:alph) {
				if(Character.isDigit(c) || Character.isWhitespace(c) || c=='+') {
					sb.append(c);
				}
				else {
					MessageUtil.setMessage(" Please provide valid mobile number", "Color:red", "Top");
					mobileTbId.setFocus(true);
					return false;
				}
			}
		}
		
		//LoyaltyProgram ltyProgram = loyaltyProgramDao.findById(prgmId);
		String requisites = ltyProgram.getRegRequisites();
		if(requisites != null && requisites.trim().length() > 0){
			
			String[] requisitesArr = requisites.split(";=;");
			for(String requisite : requisitesArr){
				
				if(requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_FIRSTNAME) && (firstNameTbId.getText() == null || firstNameTbId.getText().trim().isEmpty())){
					MessageUtil.setMessage(" Please provide first name", "Color:red", "Top");
					firstNameTbId.setFocus(true);
					return false;
				}
				else if(requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_LASTNAME) && (lastNameTbId.getText() == null || lastNameTbId.getText().trim().isEmpty())){
					MessageUtil.setMessage(" Please provide last name", "Color:red", "Top");
					lastNameTbId.setFocus(true);
					return false;
				}
				/*else if(requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_EMAILID) && (emailTbId.getText() == null || emailTbId.getText().trim().isEmpty())){
					MessageUtil.setMessage(" please provide e-mail", "Color:red", "Top");
					emailTbId.setFocus(true);
					return false;
				}
				else if((OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(ltyProgram.getMembershipType())  ) && 
						requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_MOBILEPHONE) && (mobileTbId.getText() == null || mobileTbId.getText().trim().isEmpty())){
					MessageUtil.setMessage(" please provide mobile number", "Color:red", "Top");
					mobileTbId.setFocus(true);
					return false;
				}*/
				else if(requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_CITY) && (cityTbId.getText() == null || cityTbId.getText().trim().isEmpty())){
					MessageUtil.setMessage(" Please provide city", "Color:red", "Top");
					cityTbId.setFocus(true);
					return false;
				}
				else if(requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_STATE) && (stateTbId.getText() == null || stateTbId.getText().trim().isEmpty())){
					MessageUtil.setMessage(" Please provide state", "Color:red", "Top");
					stateTbId.setFocus(true);
					return false;
				}
				else if(requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_COUNTRY) && (countryTbId.getText() == null || countryTbId.getText().trim().isEmpty())){
					MessageUtil.setMessage(" Please provide country", "Color:red", "Top");
					countryTbId.setFocus(true);
					return false;
				}
				else if(requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_BIRTHDAY) && (birthdayTbId.getText() == null || birthdayTbId.getText().trim().isEmpty())){
					MessageUtil.setMessage(" Please provide birthday", "Color:red", "Top");
					birthdayTbId.setFocus(true);
					return false;
				}
				else if(requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_ANNIVERSARY) && (anniversaryTbId.getText() == null || anniversaryTbId.getText().trim().isEmpty())){
					MessageUtil.setMessage(" Please provide anniversary", "Color:red", "Top");
					anniversaryTbId.setFocus(true);
					return false;
				}
				else if(requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_ADDRESSONE) && (streetTbId.getText() == null || streetTbId.getText().trim().isEmpty())){
					MessageUtil.setMessage(" Please provide street", "Color:red", "Top");
					streetTbId.setFocus(true);
					return false;
				}
				else if(requisite.equals(OCConstants.LOYALTY_REG_REQUISITE_ZIP) && (postalCodeTbId.getText() == null || postalCodeTbId.getText().trim().isEmpty())){
					MessageUtil.setMessage(" Please provide postal code", "Color:red", "Top");
					postalCodeTbId.setFocus(true);
					return false;
				}
			}
			
		}
		
		if(storeTypeLbId.getSelectedIndex()==0) {
			
			MessageUtil.setMessage(" Please select store", "Color:red", "Top");
			return false;
		}
		
		return true;
	}

}
