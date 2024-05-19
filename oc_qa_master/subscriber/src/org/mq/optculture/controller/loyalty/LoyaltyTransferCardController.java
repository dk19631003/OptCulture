package org.mq.optculture.controller.loyalty;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Span;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.ContactsLoyaltyStage;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyCards;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyThresholdAlerts;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.LoyaltyTransactionParent;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.RetailProSalesCSV;
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
import org.mq.marketer.campaign.general.EncryptDecryptLtyMembshpPwd;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.data.dao.LoyaltyCardsDao;
import org.mq.optculture.data.dao.LoyaltyCardsDaoForDML;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDao;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.model.ocloyalty.HoldBalance;
import org.mq.optculture.model.ocloyalty.LoyaltyTransferMembershipJsonRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyTransferMembershipJsonResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyUser;
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.ocloyalty.MembershipRequest;
import org.mq.optculture.model.ocloyalty.MembershipResponse;
import org.mq.optculture.model.ocloyalty.RequestHeader;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.model.ocloyalty.TransferSource;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Window;

import com.google.gson.Gson;

public class LoyaltyTransferCardController extends GenericForwardComposer implements EventListener {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Window loyaltyTransferCardWinId, latestDetailsSubWinId;
	private Textbox srcCardTbId,destCardTbId,ltyPwdTbId, searchByTbId, first_nameTbId, last_nameTbId;
	private Button transferBtnId, submitBtnId, filterBtnId;
	private Popup ltyPwdPopupId;
	private Label firstNameLblId, lastNameLblId, membershipLblId, moblieNumberLblId, emailAddressLblId;
	private Label destFirstNameLblId, destLastNameLblId, destMembershipLblId, destMoblieNumberLblId, destEmailAddressLblId, latestDetailsSubWinId$searchCriteriaLblId;
	private Listbox storeNumberLbId, searchbyLbId;
	private UsersDao usersDao=null;
	private OrganizationStoresDao orgStoresDao = null;
	private LoyaltyProgramService ltyPrgmSevice;
	private Users currUser;
	private Users orgOwner;
	private Span fullNameSpanId;
	private Div latestDetailsSubWinId$foundMultipleMatchesDivId;
	private static String ERROR_STYLE = "border:1px solid #DD7870;";
	private Radiogroup latestDetailsSubWinId$multipleMatchesRgId;
	private Div latestDetailsSubWinId$viewSkuDetailsDivId,latestDetailsSubWinId$viewPromotionDetailsDivId,latestDetailsSubWinId$closeCardDivId,latestDetailsSubWinId$viewPurchaseDetailsDivId;
	private  static String ITALIC_GREY_STYLE = "color:grey;font-style: italic;font-size:12px;";
	private final String TRANSFER_DEST_CARDNO="Transfer_Dest_CardNo";
	private final String FOR_USER="FOR_USER";

	public LoyaltyTransferCardController(){
		super();
		usersDao=(UsersDao)SpringUtil.getBean("usersDao");
		ltyPrgmSevice = new LoyaltyProgramService();
		
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Loyalty Transfer or Merge Cards", Constants.STRING_NILL, style, true);
		//clearContactDetails();
		
	}
	public void doAfterCompose(Component comp) throws Exception {

		super .doAfterCompose(comp);
		currUser = GetUser.getUserObj();
		Long orgOwnerId = usersDao.getOwnerofOrg(currUser.getUserOrganization().getUserOrgId());
		orgOwner = usersDao.findByUserId(orgOwnerId);
		//find super user of that org
		//usersDao.getOwnerofOrg(orgId);
		searchbyLbId.setSelectedIndex(0);
		searchByTbId.setStyle(ITALIC_GREY_STYLE);
		searchByTbId.setValue(Constants.MEMBERSHIP_NUMBER);
		clearContactDetails();
		setDefaultStores();
	}

	public void onClick$transferBtnId() {
		logger.debug(">>>>>>>>>>>>> entered in onClick$transferBtnId");
	/*	ltyPwdTbId.setText("");
		ltyPwdPopupId.open(ltyPwdPopupId);*/


		try{
			/*String pwdStr = ltyPwdTbId.getValue().trim();

			if(pwdStr == null || pwdStr.trim().length() ==0) {
				MessageUtil.setMessage( "Please provide password.", "color:red;", "TOP");
				ltyPwdTbId.setText("");
				return;
			}
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			LoyaltyThresholdAlerts loyaltyThresholdAlerts = ltyPrgmSevice.findPwdByUserID(GetUser.getUserObj().getUserId());
			if(loyaltyThresholdAlerts != null && EncryptDecryptLtyMembshpPwd.decrypt(loyaltyThresholdAlerts.getLtySecurityPwd()).equals(pwdStr)) { */

				if((srcCardTbId.getValue().trim() != null && !srcCardTbId.toString().isEmpty()) &&
						(destCardTbId.getValue().trim() != null && !destCardTbId.toString().isEmpty())){

					Users user = usersDao.findByUserId(usersDao.getOwnerofOrg(currUser.getUserOrganization().getUserOrgId()));
					orgStoresDao = (OrganizationStoresDao)SpringUtil.getBean("organizationStoresDao");
					
					String srcCard = srcCardTbId.getValue().trim();
					String destCard = destCardTbId.getValue().trim();
					String storeName = storeNumberLbId.getSelectedItem().getLabel();
					logger.info("Store Name in rest:::"+storeName);
					List<OrganizationStores> storeNumberList = orgStoresDao.findBySearchStr(storeName,user.getUserOrganization().getUserOrgId());
					String storeNumber; 
					if(storeNumberList.isEmpty() && storeNumberList.size() == 0){
						storeNumber = "";
					}
					else{
						storeNumber = storeNumberList.get(0).getHomeStoreId();
					}
					logger.info("StoreNumber in rest service:: "+storeNumber);
					Calendar cal = new MyCalendar(Calendar.getInstance(), null, 
							MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));

					String requestId = "TransferCard_"+user.getToken()+"_"+System.currentTimeMillis();			
					RequestHeader header = new RequestHeader();
					header.setRequestId(""+requestId);
					header.setPcFlag("");
					header.setRequestDate(""+cal);
					header.setStoreNumber(""+storeNumber);
					header.setEmployeeId("");
					header.setTerminalId("");

					MembershipRequest membership = new MembershipRequest();
					membership.setCardNumber(""+destCard);
					membership.setCardPin("");

					TransferSource transferSource = new TransferSource();
					transferSource.setCardNumber(""+srcCard);
					transferSource.setCardPin("");

					LoyaltyUser loyaltyUser = new LoyaltyUser();
					loyaltyUser.setUserName(Utility.getOnlyUserName(user.getUserName()));
					loyaltyUser.setOrganizationId(Utility.getOnlyOrgId(user.getUserName()));
					loyaltyUser.setToken(user.getUserOrganization().getOptSyncKey());

					LoyaltyTransferMembershipJsonRequest loyaltyTransferMembershipJsonRequest = new LoyaltyTransferMembershipJsonRequest();
					loyaltyTransferMembershipJsonRequest.setHeader(header);
					loyaltyTransferMembershipJsonRequest.setMembership(membership);
					loyaltyTransferMembershipJsonRequest.setTransferSource(transferSource);
					loyaltyTransferMembershipJsonRequest.setUser(loyaltyUser);

					Gson gson = new Gson();
					//Convert Object to JSON string
					String requestJson = gson.toJson(loyaltyTransferMembershipJsonRequest);
					logger.info("RequestJson::::"+requestJson);

					LoyaltyTransferMembershipJsonResponse response = loyaltyTransferMembershipRestService(loyaltyTransferMembershipJsonRequest);
					String status = null;
					if(response!=null){
						if(response.getStatus().getErrorCode().equalsIgnoreCase("0")){
							MessageUtil.setMessage("The transfer has been succeessfully Completed", "color:green");
							session.setAttribute(TRANSFER_DEST_CARDNO, response.getMembership().getCardNumber());
							session.setAttribute(FOR_USER, user);
							if(currUser.getUserId().longValue() ==  orgOwner.getUserId().longValue())
								Redirect.goTo(PageListEnum.LOYALTY_CUSTOMER_LOOKUP);
							else
								Redirect.goTo(PageListEnum.CUSTOMER_LOOKUP);

						}else{
							status = response.getStatus().getErrorCode()+"-"+response.getStatus().getMessage();
							MessageUtil.setMessage(status, "color:red");
						}
					}

				}
				else{
					MessageUtil.setMessage("Please provide required inputs", "color:red");
				}
			/*}else{

				MessageUtil.setMessage("Please enter the correct password", "color:red");
				ltyPwdTbId.setText("");
				return;

			}*/

		}
		catch(Exception e){
			logger.error("Exception ",e);
		}
		logger.debug("<<<<<<<<<<<<< completed onClick$transferBtnId ");

	
		
	}
	public void onClick$submitBtnId(){
		clearContactDetails();
		sourceCardDetails(srcCardTbId.getValue());
		destCardDetails(destCardTbId.getValue());
		//clearContactDetails();
	}
	public void onClick$filterBtnId(){
		logger.debug("<<<<< entered the method:: onClick$filterBtnId");
		//clear the fields
		clearContactDetails();
		//searchBy=searchbyLbId.getSelectedItem();
		//calling fetch loyalty details
		fetchLoyaltyDetails();
	}
	public void onClick$resetAnchId(){
		logger.debug(">>>>>>>>>>>>> entered in onClick$resetAnchId");
		searchByTbId.setValue(Constants.STRING_NILL);
		searchbyLbId.setSelectedIndex(0);
		srcCardTbId.setValue("");
		destCardTbId.setValue("");
		clearContactDetails();
		resetSearchFeilds();
		logger.debug("<<<<<<<<<<<<< completed onClick$resetAnchId ");
	}//onClick$resetAnchId
	private void resetSearchFeilds() {
		logger.debug(">>>>>>>>>>>>> entered in resetSearchFeilds");
		fullNameSpanId.setVisible(false);
		searchByTbId.setVisible(true);
		searchByTbId.setValue(Constants.MEMBERSHIP_NUMBER);
		searchByTbId.setStyle(ITALIC_GREY_STYLE);
		first_nameTbId.setValue(Constants.STRING_NILL);
		last_nameTbId.setValue(Constants.STRING_NILL);
		searchbyLbId.setSelectedIndex(0);
		logger.debug("<<<<<<<<<<<<< completed resetSearchFeilds");
	}//resetValues
	public void onSelect$searchbyLbId(){

		logger.debug(">>>>>>>>>>>>> entered in onSelect$searchbyLbId");
		logger.info("Search by ::::::"+searchByTbId.getValue()+ "\t ::"+first_nameTbId.getValue()+"::");

		if(Constants.FULL_NAME.equals(searchbyLbId.getSelectedItem().getValue())){
			searchByTbId.setVisible(false);
			fullNameSpanId.setVisible(true);
			if(searchByTbId.getValue() !=null && !(Constants.STRING_NILL.equals(searchByTbId.getValue().trim())) && !(Constants.MEMBERSHIP_NUMBER.equalsIgnoreCase(searchByTbId.getValue()) || Constants.PHONE_NUMBER.equalsIgnoreCase(searchByTbId.getValue()) || Constants.EMAIL_ADDRESS.equalsIgnoreCase(searchByTbId.getValue()) )){
				first_nameTbId.setValue(searchByTbId.getValue());
			}
			else{
				first_nameTbId.setValue("First Name");
			}
			first_nameTbId.setStyle(ITALIC_GREY_STYLE);
			last_nameTbId.setValue("Last Name");
			last_nameTbId.setStyle(ITALIC_GREY_STYLE);
		}
		else if(Constants.CARD_NUMBER.equals(searchbyLbId.getSelectedItem().getValue())){
			//Setting List TextBox Value
			setListBoxValue(Constants.MEMBERSHIP_NUMBER);
			
		}
		else if(Constants.MOBILE_PHONE.equals(searchbyLbId.getSelectedItem().getValue())){
			//Setting List TextBox Value
			setListBoxValue(Constants.PHONE_NUMBER);
		}
		else if(Constants.EMAIL_ID.equals(searchbyLbId.getSelectedItem().getValue())){
			//Setting List TextBox Value
			setListBoxValue(Constants.EMAIL_ADDRESS);
		}
		searchByTbId.setStyle(ITALIC_GREY_STYLE);
		logger.debug("<<<<<<<<<<<<< completed onSelect$searchbyLbId ");
	}//onSelect$searchbyLbId
	/**
	 * This method set's the value to list box
	 */
	private void setListBoxValue(String value) {
		logger.debug(">>>>>>>>>>>>> entered in setMobileNumberListBox");
		logger.info("Search by ::::::"+searchByTbId.getValue()+ "\t ::"+first_nameTbId.getValue()+"::");
		
		searchByTbId.setVisible(true);
		fullNameSpanId.setVisible(false);
		
		if(Constants.STRING_NILL.equals(searchByTbId.getValue().trim()) && (Constants.STRING_NILL.equals(first_nameTbId.getValue().trim()))){
			searchByTbId.setValue(value);
		}
		else if(!Constants.STRING_NILL.equals(searchByTbId.getValue().trim()) && (Constants.STRING_NILL.equals(first_nameTbId.getValue().trim()))){
			if(Constants.MEMBERSHIP_NUMBER.equals(searchByTbId.getValue().trim()) || Constants.PHONE_NUMBER.equals(searchByTbId.getValue().trim()) || Constants.EMAIL_ADDRESS.equals(searchByTbId.getValue().trim())){
				searchByTbId.setValue(value);
			}
			else{
				searchByTbId.setValue(searchByTbId.getValue().trim());
			}
		}
		else if(Constants.STRING_NILL.equals(searchByTbId.getValue().trim()) && !(Constants.STRING_NILL.equals(first_nameTbId.getValue().trim()))){
			if("first name".equalsIgnoreCase(first_nameTbId.getValue().trim())){
				searchByTbId.setValue(value);
			}
			else{
				searchByTbId.setValue(first_nameTbId.getValue().trim());
			}
		}
		else if(!Constants.STRING_NILL.equals(searchByTbId.getValue().trim()) && !(Constants.STRING_NILL.equals(first_nameTbId.getValue().trim()))){
			if("first name".equalsIgnoreCase(first_nameTbId.getValue().trim()) && (Constants.MEMBERSHIP_NUMBER.equals(searchByTbId.getValue().trim()) || Constants.PHONE_NUMBER.equals(searchByTbId.getValue().trim()) || Constants.EMAIL_ADDRESS.equals(searchByTbId.getValue().trim()))){
				searchByTbId.setValue(value);
			}
			else{
				if("first name".equalsIgnoreCase(first_nameTbId.getValue().trim())){
					searchByTbId.setValue(searchByTbId.getValue().trim());
				}
				else{
					searchByTbId.setValue(first_nameTbId.getValue().trim());
				}
			}
		}
		logger.debug("<<<<<<<<<<<<< completed setMobileNumberListBox ");
	}//setMobileNumberListBox
	
	/**
	 * This method capture onFocus event of searchByTextBox.
	 */
	public void onFocus$searchByTbId(){
		logger.debug(">>>>>>>>>>>>> entered in onFocus$searchByTbId");
		
		if(Constants.STRING_NILL.equals( searchByTbId.getValue()) || ("First Name".equalsIgnoreCase(searchByTbId.getValue())||Constants.MEMBERSHIP_NUMBER.equalsIgnoreCase(searchByTbId.getValue()) || Constants.PHONE_NUMBER.equalsIgnoreCase(searchByTbId.getValue()) || Constants.EMAIL_ADDRESS.equalsIgnoreCase(searchByTbId.getValue()) )){
			searchByTbId.setValue(Constants.STRING_NILL);
		}
		logger.debug("<<<<<<<<<<<<< completed onFocus$searchByTbId ");
	}//onFocus$searchByTbId
	
	public void onFocus$first_nameTbId(){
		logger.debug(">>>>>>>>>>>>> entered in onFocus$first_nameTbId");
		if(searchByTbId.getValue() != null && first_nameTbId.getValue() != null && "First Name".equalsIgnoreCase(first_nameTbId.getValue())  && !(Constants.MEMBERSHIP_NUMBER.equalsIgnoreCase(searchByTbId.getValue()) || Constants.PHONE_NUMBER.equalsIgnoreCase(searchByTbId.getValue()) || Constants.EMAIL_ADDRESS.equalsIgnoreCase(searchByTbId.getValue()) )){
			first_nameTbId.setValue(searchByTbId.getValue());
		}
		else if(first_nameTbId.getValue() != null && "First Name".equalsIgnoreCase(first_nameTbId.getValue())){

			first_nameTbId.setValue(Constants.STRING_NILL);
		}
		logger.debug("<<<<<<<<<<<<< completed onFocus$first_nameTbId ");
	}//onFocus$first_nameTbId
	
	public void onFocus$last_nameTbId(){
		logger.debug(">>>>>>>>>>>>> entered in onFocus$last_nameTbId");
		if(last_nameTbId.getValue() != null && "Last Name".equalsIgnoreCase(last_nameTbId.getValue())){

			last_nameTbId.setValue(Constants.STRING_NILL);
		}
		//last_nameTbId.setValue(Constants.STRING_NILL);
		logger.debug("<<<<<<<<<<<<< completed onFocus$last_nameTbId ");
	}
	/*public void onClick$submitPwdBtnId() {

		try{
			String pwdStr = ltyPwdTbId.getValue().trim();

			if(pwdStr == null || pwdStr.trim().length() ==0) {
				MessageUtil.setMessage( "Please provide password.", "color:red;", "TOP");
				ltyPwdTbId.setText("");
				return;
			}
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			LoyaltyThresholdAlerts loyaltyThresholdAlerts = ltyPrgmSevice.findPwdByUserID(GetUser.getUserObj().getUserId());
			if(loyaltyThresholdAlerts != null && EncryptDecryptLtyMembshpPwd.decrypt(loyaltyThresholdAlerts.getLtySecurityPwd()).equals(pwdStr)) {

				if((srcCardTbId.getValue().trim() != null && !srcCardTbId.toString().isEmpty()) &&
						(destCardTbId.getValue().trim() != null && !destCardTbId.toString().isEmpty())){

					Users user = usersDao.findByUserId(GetUser.getUserObj().getUserId());
					orgStoresDao = (OrganizationStoresDao)SpringUtil.getBean("organizationStoresDao");
					
					String srcCard = srcCardTbId.getValue().trim();
					String destCard = destCardTbId.getValue().trim();
					String storeName = storeNumberLbId.getSelectedItem().getLabel();
					List<OrganizationStores> storeNumberList = orgStoresDao.findBySearchStr(storeName,user.getUserOrganization().getUserOrgId());
					String storeNumber = storeNumberList.get(0).getHomeStoreId();
					Calendar cal = new MyCalendar(Calendar.getInstance(), null, 
							MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));

					String requestId = "TransferCard_"+user.getToken()+"_"+System.currentTimeMillis();			
					RequestHeader header = new RequestHeader();
					header.setRequestId(""+requestId);
					header.setPcFlag("");
					header.setRequestDate(""+cal);
					header.setStoreNumber(""+storeNumber);
					header.setEmployeeId("");
					header.setTerminalId("");

					MembershipRequest membership = new MembershipRequest();
					membership.setCardNumber(""+destCard);
					membership.setCardPin("");

					TransferSource transferSource = new TransferSource();
					transferSource.setCardNumber(""+srcCard);
					transferSource.setCardPin("");

					LoyaltyUser loyaltyUser = new LoyaltyUser();
					loyaltyUser.setUserName(Utility.getOnlyUserName(user.getUserName()));
					loyaltyUser.setOrganizationId(Utility.getOnlyOrgId(user.getUserName()));
					loyaltyUser.setToken(user.getToken());

					LoyaltyTransferMembershipJsonRequest loyaltyTransferMembershipJsonRequest = new LoyaltyTransferMembershipJsonRequest();
					loyaltyTransferMembershipJsonRequest.setHeader(header);
					loyaltyTransferMembershipJsonRequest.setMembership(membership);
					loyaltyTransferMembershipJsonRequest.setTransferSource(transferSource);
					loyaltyTransferMembershipJsonRequest.setUser(loyaltyUser);

					Gson gson = new Gson();
					//Convert Object to JSON string
					String requestJson = gson.toJson(loyaltyTransferMembershipJsonRequest);
					logger.info("RequestJson::::"+requestJson);

					LoyaltyTransferMembershipJsonResponse response = loyaltyTransferMembershipRestService(loyaltyTransferMembershipJsonRequest);
					String status = null;
					if(response!=null){
						status = response.getStatus().getErrorCode()+"-"+response.getStatus().getMessage();
						MessageUtil.setMessage(status, "color:green");
					}

				}
				else{
					MessageUtil.setMessage("Please provide required inputs", "color:red");
				}
			}else{

				MessageUtil.setMessage("Please enter the correct password", "color:red");
				ltyPwdTbId.setText("");
				return;

			}

		}
		catch(Exception e){
			logger.error("Exception ",e);
		}
		logger.debug("<<<<<<<<<<<<< completed onClick$transferBtnId ");

	} */

	private LoyaltyTransferMembershipJsonResponse loyaltyTransferMembershipRestService(LoyaltyTransferMembershipJsonRequest loyaltyTransferMembershipJsonRequest){


		logger.info("Started Loyalty Transfer Rest Service...");

		LoyaltyTransactionParent tranParent = createNewTransaction();
		Date date = tranParent.getCreatedDate().getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		String transDate = df.format(date);

		LoyaltyTransferMembershipJsonResponse transferMembershipJsonResponse = null;
		ContactsLoyaltyStage loyaltyStage = null;
		//LoyaltyEnrollResponse enrollResponse = null;
		String userName = null;
		Status status = null;
		String responseJson = "";
		Gson gson = new Gson();
		try{


			ResponseHeader responseHeader = new ResponseHeader();
			responseHeader.setRequestDate("");
			responseHeader.setRequestId("");
			responseHeader.setTransactionDate(transDate);
			responseHeader.setTransactionId(""+tranParent.getTransactionId());
			String requestJson = gson.toJson(loyaltyTransferMembershipJsonRequest);

			LoyaltyTransferMembershipJsonRequest transferRequest = null;
			try{
				transferRequest = gson.fromJson(requestJson, LoyaltyTransferMembershipJsonRequest.class);
			}catch(Exception e){
				status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferMembershipJsonResponse = prepareTransferResponse(responseHeader, null, null, null, null, status);
				responseJson = gson.toJson(transferMembershipJsonResponse);
				updateTransaction(tranParent, transferMembershipJsonResponse, null);
				logger.info("Response = "+responseJson);
				logger.error("Exception in parsing request json ...",e);
				return transferMembershipJsonResponse;
			}

			if(transferRequest == null){
				status = new Status("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferMembershipJsonResponse = prepareTransferResponse(responseHeader, null, null, null, null, status);
				responseJson = gson.toJson(transferMembershipJsonResponse);
				updateTransaction(tranParent, transferMembershipJsonResponse, null);
				logger.info("Response = "+responseJson);
				return transferMembershipJsonResponse;
			}

			if(transferRequest.getHeader() == null){
				status = new Status("1004", PropertyUtil.getErrorMessage(1004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferMembershipJsonResponse = prepareTransferResponse(responseHeader, null, null, null, null, status);
				responseJson = gson.toJson(transferMembershipJsonResponse);
				updateTransaction(tranParent, transferMembershipJsonResponse, null);
				logger.info("Response = "+responseJson);
				return transferMembershipJsonResponse;
			}

			if(transferRequest.getHeader().getRequestId() == null || transferRequest.getHeader().getRequestId().trim().isEmpty() ||
					transferRequest.getHeader().getRequestDate() == null || transferRequest.getHeader().getRequestDate().trim().isEmpty()){
				status = new Status("111553", PropertyUtil.getErrorMessage(111553, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferMembershipJsonResponse = prepareTransferResponse(responseHeader, null, null, null, null, status);
				updateTransaction(tranParent, transferMembershipJsonResponse, null);
				logger.info("Response = "+responseJson);
				return transferMembershipJsonResponse;
			}
			if(transferRequest.getMembership() == null || transferRequest.getMembership().getCardNumber() == null){


				status = new Status("101004", PropertyUtil.getErrorMessage(101004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferMembershipJsonResponse = prepareTransferResponse(responseHeader, null, null, null, null, status);
				responseJson = gson.toJson(transferMembershipJsonResponse);
				updateTransaction(tranParent, transferMembershipJsonResponse, null);
				logger.info("Response = "+responseJson);
				return transferMembershipJsonResponse;

			}
			if(transferRequest.getTransferSource() == null || transferRequest.getTransferSource().getCardNumber() == null){
				status = new Status("101004", PropertyUtil.getErrorMessage(101004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferMembershipJsonResponse = prepareTransferResponse(responseHeader, null, null, null, null, status);
				responseJson = gson.toJson(transferMembershipJsonResponse);
				updateTransaction(tranParent, transferMembershipJsonResponse, null);
				logger.info("Response = "+responseJson);
				return transferMembershipJsonResponse;

			}

			if(transferRequest.getUser() == null){
				status = new Status("101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferMembershipJsonResponse = prepareTransferResponse(responseHeader, null, null, null, null, status);
				responseJson = gson.toJson(transferMembershipJsonResponse);
				updateTransaction(tranParent, transferMembershipJsonResponse, null);
				logger.info("Response = "+responseJson);
				return transferMembershipJsonResponse;
			}

			if(transferRequest.getUser().getUserName() == null || transferRequest.getUser().getOrganizationId() == null || transferRequest.getUser().getOrganizationId().trim().length() <=0 || 
					transferRequest.getUser().getToken() == null || transferRequest.getUser().getToken().trim().length() <=0) {
				status = new Status("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG)+" Token or OptSync key is missing.", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferMembershipJsonResponse = prepareTransferResponse(responseHeader, null, null, null, null, status);
				responseJson = gson.toJson(transferMembershipJsonResponse);
				updateTransaction(tranParent, transferMembershipJsonResponse, null);
				logger.info("Response = "+responseJson);
				return transferMembershipJsonResponse;
			}

			userName = transferRequest.getUser().getUserName() + Constants.USER_AND_ORG_SEPARATOR +
					transferRequest.getUser().getOrganizationId();

			String pcFlag = transferRequest.getHeader().getPcFlag();
			String requestId = transferRequest.getHeader().getRequestId();
			LoyaltyTransaction transaction = null;
			if(pcFlag != null && pcFlag.equalsIgnoreCase("true")){
				transaction = findTransactionByRequestId(requestId);
				if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED)){
					responseJson = transaction.getJsonResponse();
					transferMembershipJsonResponse = gson.fromJson(responseJson, LoyaltyTransferMembershipJsonResponse.class);
					updateTransaction(tranParent, transferMembershipJsonResponse, userName);
					logger.info("Response = "+responseJson);
					return transferMembershipJsonResponse;
				}
			}	

			//code to handle multiple transfer for single customer due to timeout at pos or connection delay.
			loyaltyStage = findDuplicateRequest(transferRequest);
			if(loyaltyStage != null){
				logger.info("Duplicate request....timed out request...");
				responseJson = "{\"STATUS\":{\"ERRORCODE\":\"101505\",\"MESSAGE\":\"Error 101505: Request is being processed.\",\"STATUS\":\"Failure\"}}";
				transferMembershipJsonResponse = gson.fromJson(responseJson, LoyaltyTransferMembershipJsonResponse.class);
				updateTransaction(tranParent, transferMembershipJsonResponse, userName);
				logger.info("Response = "+responseJson);
				return transferMembershipJsonResponse;
			}
			else{
				loyaltyStage = saveRequestInStageTable(transferRequest);
			}

			//TODO need to think
			if(transferRequest.getMembership() != null && transferRequest.getMembership().getCardNumber() != null
					&& transferRequest.getMembership().getCardNumber().trim().isEmpty() ){

				LoyaltyTransaction duplicateTrx = findRequestByCardAndReqId(transferRequest.getHeader().getRequestId().trim(),
						transferRequest.getTransferSource().getCardNumber().trim());
				if(duplicateTrx != null){
					logger.info("duplicate transaction found...");
					responseHeader.setRequestId(transferRequest.getHeader().getRequestId());
					responseHeader.setRequestDate(transferRequest.getHeader().getRequestDate());
					status = new Status("111536", PropertyUtil.getErrorMessage(111536, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					transferMembershipJsonResponse = prepareTransferResponse(responseHeader, null, null, null, null, status);
					responseJson = gson.toJson(transferMembershipJsonResponse);
					logger.info("Response = "+responseJson);
					updateTransaction(tranParent, transferMembershipJsonResponse, null);
					return transferMembershipJsonResponse;
				}
			}

			//log transaction
			if(transaction == null){
				transaction = logTransactionRequest(transferRequest, requestJson, OCConstants.LOYALTY_ONLINE_MODE);
			}


			//update dest card if it is inventory card to avoid any concurrent req between enrollment & transfer
			updateDestCardStatus(transferRequest.getMembership().getCardNumber(), userName);



			BaseRequestObject requestObject = new BaseRequestObject();
			requestObject.setJsonValue(requestJson);
			requestObject.setAction(OCConstants.LOYALTY_SERVICE_ACTION_TRANSFER);
			requestObject.setTransactionId(""+tranParent.getTransactionId());
			requestObject.setTransactionDate(transDate);
			BaseService baseService = null;
			BaseResponseObject responseObject = null;


			baseService= ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_TRANSFER_MEMBERSHIP_BUSINESS_SERVICE);
			responseObject = baseService.processRequest(requestObject);

			logger.info("JSON Response: = "+responseObject.getJsonValue());
			LoyaltyTransferMembershipJsonResponse responseobject = gson.fromJson(responseObject.getJsonValue(), 
					LoyaltyTransferMembershipJsonResponse.class);

			updateTransactionStatus(transaction, responseObject.getJsonValue(), responseobject);
			revertCardStatus(responseobject, transferRequest.getMembership().getCardNumber(), userName);

			if(responseObject.getAction().equals(OCConstants.LOYALTY_SERVICE_ACTION_TRANSFER)){
				responseJson = responseObject.getJsonValue();
				transferMembershipJsonResponse = gson.fromJson(responseJson, LoyaltyTransferMembershipJsonResponse.class);
				updateTransaction(tranParent, transferMembershipJsonResponse, userName);
				logger.info("Response = "+responseJson);
				return transferMembershipJsonResponse;
			}
		}catch(Exception e){
			logger.error("Error in Transfer rest service", e);
			responseJson = "{\"STATUS\":{\"ERRORCODE\":\"101000\",\"MESSAGE\":\"Server error  101000.\",\"STATUS\":\"Failure\"}}";
			transferMembershipJsonResponse = gson.fromJson(responseJson, LoyaltyTransferMembershipJsonResponse.class);
			updateTransaction(tranParent, transferMembershipJsonResponse, userName);
			logger.info("Response = "+responseJson);
			return transferMembershipJsonResponse;
		}finally{
			if(loyaltyStage != null) deleteRequestFromStageTable(loyaltyStage);
			logger.info("Completed Loyalty Transfer Rest Service.");
		}
		return null;
	}
	private void revertCardStatus(LoyaltyTransferMembershipJsonResponse responseobject, String cardNumber, String userName){

		if(responseobject.getStatus().getErrorCode()!= null  && !responseobject.getStatus().getErrorCode().equalsIgnoreCase("0")){

			revertDestCardStatus(cardNumber, userName);
		}

	}

	private void deleteRequestFromStageTable(ContactsLoyaltyStage loyaltyStage) {

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
	private void updateDestCardStatus(String cardNumber, String userName) {

		try {
			LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
			LoyaltyCardsDaoForDML loyaltyCardsDaoForDML = (LoyaltyCardsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);
			UsersDao usersdao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = usersdao.findByUsername(userName);
			//loyaltyCardsDao.updateDestCardStatus(cardNumber, user.getUserId());
			loyaltyCardsDaoForDML.updateDestCardStatus(cardNumber, user.getUserId());
		} catch (Exception e) {

		}


	}

	private void revertDestCardStatus(String cardNumber, String userName) {

		try {
			LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
			LoyaltyCardsDaoForDML loyaltyCardsDaoForDML = (LoyaltyCardsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);
			UsersDao usersdao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = usersdao.findByUsername(userName);
			//loyaltyCardsDao.revertDestCardStatus(cardNumber, user.getUserId());
			loyaltyCardsDaoForDML.revertDestCardStatus(cardNumber, user.getUserId());
		} catch (Exception e) {

		}


	}
	private LoyaltyTransferMembershipJsonResponse prepareTransferResponse(ResponseHeader header, MembershipResponse membershipResponse,
			List<Balance> balances, HoldBalance holdBalance, List<MatchedCustomer> matchedCustomers, Status status) throws BaseServiceException {
		LoyaltyTransferMembershipJsonResponse transferResponse = new LoyaltyTransferMembershipJsonResponse();
		transferResponse.setHeader(header);

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
		transferResponse.setMembership(membershipResponse);
		transferResponse.setBalances(balances);
		transferResponse.setHoldBalance(holdBalance);
		transferResponse.setMatchedCustomers(matchedCustomers);
		transferResponse.setStatus(status);
		return transferResponse;
	}

	private void updateTransaction(LoyaltyTransactionParent trans, LoyaltyTransferMembershipJsonResponse responseObject, String userName) {

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
	private LoyaltyTransactionParent createNewTransaction(){

		LoyaltyTransactionParent tranx  = null; 
		try{
			LoyaltyTransactionParentDao parentDao = (LoyaltyTransactionParentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO);
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);
			tranx = new LoyaltyTransactionParent();
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getDefault());
			tranx.setCreatedDate(cal);
			tranx.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_TRANSFER);
			//parentDao.saveOrUpdate(tranx);
			parentDaoForDML.saveOrUpdate(tranx);

		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
		return tranx;
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

	private LoyaltyTransaction findRequestByCardAndReqId(String requestId, String cardNumber) throws Exception {
		LoyaltyTransactionDao loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
		return loyaltyTransactionDao.findRequestByCardAndReqId(cardNumber, requestId, OCConstants.LOYALTY_TRANS_TYPE_TRANSFER, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
	}

	public LoyaltyTransaction logTransactionRequest(LoyaltyTransferMembershipJsonRequest requestObject, String jsonRequest, String mode){
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
			transaction.setCardNumber(requestObject.getMembership().getCardNumber().trim());
			transaction.setType(OCConstants.LOYALTY_TRANS_TYPE_TRANSFER);
			transaction.setUserDetail(requestObject.getUser().getUserName()+"__"+requestObject.getUser().getOrganizationId());
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

	public void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyTransferMembershipJsonResponse response){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			transaction.setRequestStatus(response.getStatus().getStatus());
			transaction.setJsonResponse(responseJson);


			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}

	private ContactsLoyaltyStage findDuplicateRequest(LoyaltyTransferMembershipJsonRequest requestObject) {
		//find the request in stage
		ContactsLoyaltyStage loyaltyStage = null;
		try{
			ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
			String userName = requestObject.getUser().getUserName()+Constants.USER_AND_ORG_SEPARATOR +requestObject.getUser().getOrganizationId();  
			ContactsLoyaltyStage requestStage = contactsLoyaltyStageDao.findTransferRequest(requestObject.getTransferSource().getCardNumber(), 
					requestObject.getMembership().getCardNumber(), userName, OCConstants.LOYALTY_SERVICE_TYPE_OC, OCConstants.LOYALTY_TRANS_TYPE_TRANSFER);
			if(requestStage != null){
				return loyaltyStage;
			}

		}catch(Exception e){
			logger.error("Exception in finding loyalty duplicate request...", e);
		}
		return loyaltyStage;
	}


	private ContactsLoyaltyStage saveRequestInStageTable(LoyaltyTransferMembershipJsonRequest requestObject){

		ContactsLoyaltyStage loyaltyStage = null;
		try{
			ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
			ContactsLoyaltyStageDaoForDML contactsLoyaltyStageDaoForDML = (ContactsLoyaltyStageDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO_FOR_DML);
			String userName = requestObject.getUser().getUserName()+Constants.USER_AND_ORG_SEPARATOR +requestObject.getUser().getOrganizationId();  
			logger.info("saving request in stage table...");
			loyaltyStage = new ContactsLoyaltyStage();
			loyaltyStage.setCardNumber("source:"+requestObject.getTransferSource().getCardNumber()+"-dest:"+ 
					requestObject.getMembership().getCardNumber());
			loyaltyStage.setServiceType(OCConstants.LOYALTY_SERVICE_TYPE_OC);
			loyaltyStage.setReqType(OCConstants.LOYALTY_TRANS_TYPE_TRANSFER);
			loyaltyStage.setStatus(Constants.LOYALTY_STAGE_PENDING);
			loyaltyStage.setUserName(userName);
			contactsLoyaltyStageDaoForDML.saveOrUpdate(loyaltyStage);
		}catch(Exception e){
			logger.error("Exception while saving loyalty request in stage table...", e);
		}
		return loyaltyStage;
	}
	private void displaySourceContactDetails(Contacts contacts, String membershipNumber) {
		logger.debug(">>>>>>>>>>>>> entered in displayContactDetails");
		//Get Full Name
		String firstName =Constants.STRING_NILL;
		String lastName =Constants.STRING_NILL;
		if(contacts != null){
			if(contacts.getLastName() != null && contacts.getFirstName() != null){
				firstName = contacts.getFirstName();
				lastName = contacts.getLastName();
			}
			else if(contacts.getLastName() == null && contacts.getFirstName() != null){
				firstName = contacts.getFirstName();
			}
			else if(contacts.getLastName() != null && contacts.getFirstName() == null){
				lastName = contacts.getLastName();
			}
			else{
				firstName = Constants.STRING_NILL;
				lastName = Constants.STRING_NILL;
			}
			//Set Mobile Number
			moblieNumberLblId.setValue(contacts.getMobilePhone() == null ? Constants.STRING_NILL:contacts.getMobilePhone());
			//Set Email Address
			emailAddressLblId.setValue(contacts.getEmailId() == null ? Constants.STRING_NILL:contacts.getEmailId());
		}
		//Set Full Name
		firstNameLblId.setValue(firstName);
		lastNameLblId.setValue(lastName);
		//Set Membership Number
		membershipLblId.setValue(membershipNumber == null ? Constants.STRING_NILL : membershipNumber+Constants.STRING_NILL);
		logger.debug("<<<<<<<<<<<<< completed displaySourceContactDetails ");
	}//displayContactDetails
	private void displayDestContactDetails(Contacts contacts, String membershipNumber) {
		logger.debug(">>>>>>>>>>>>> entered in displayContactDetails");
		//Get Full Name
		String firstName =Constants.STRING_NILL;
		String lastName =Constants.STRING_NILL;
		if(contacts != null){
			if(contacts.getLastName() != null && contacts.getFirstName() != null){
				firstName = contacts.getFirstName();
				lastName = contacts.getLastName();
			}
			else if(contacts.getLastName() == null && contacts.getFirstName() != null){
				firstName = contacts.getFirstName();
			}
			else if(contacts.getLastName() != null && contacts.getFirstName() == null){
				lastName = contacts.getLastName();
			}
			else{
				firstName = Constants.STRING_NILL;
				lastName = Constants.STRING_NILL;
			}
			//Set Mobile Number
			destMoblieNumberLblId.setValue(contacts.getMobilePhone() == null ? Constants.STRING_NILL:contacts.getMobilePhone());
			//Set Email Address
			destEmailAddressLblId.setValue(contacts.getEmailId() == null ? Constants.STRING_NILL:contacts.getEmailId());
		}
		//Set Full Name
		destFirstNameLblId.setValue(firstName);
		destLastNameLblId.setValue(lastName);
		//Set Membership Number
		destMembershipLblId.setValue(membershipNumber == null ? Constants.STRING_NILL : membershipNumber+Constants.STRING_NILL);
		logger.debug("<<<<<<<<<<<<< completed displayDestContactDetails ");
	}//displayContactDetails
	private void clearContactDetails() {
		logger.debug(">>>>>>>>>>>>> entered in clearContactDetails");
		firstNameLblId.setValue(Constants.STRING_NILL);
		lastNameLblId.setValue(Constants.STRING_NILL);
		membershipLblId.setValue(Constants.STRING_NILL);
		moblieNumberLblId.setValue(Constants.STRING_NILL);
		emailAddressLblId.setValue(Constants.STRING_NILL);	
		destFirstNameLblId.setValue(Constants.STRING_NILL);
		destLastNameLblId.setValue(Constants.STRING_NILL);
		destMembershipLblId.setValue(Constants.STRING_NILL);
		destMoblieNumberLblId.setValue(Constants.STRING_NILL);
		destEmailAddressLblId.setValue(Constants.STRING_NILL);	
		logger.debug("<<<<<<<<<<<<< completed clearContactDetails ");
	}//clearContactDetails
	private void fetchLoyaltyDetails() {
		logger.debug("<<<<< entered the method:: fetchLoyaltyDetails");
		if((Constants.STRING_NILL.equals(searchByTbId.getValue()) && searchByTbId.getValue().isEmpty()) && ( Constants.STRING_NILL.equals(first_nameTbId.getValue())) && ( Constants.STRING_NILL.equals(last_nameTbId.getValue()))){
			searchByTbId.setStyle(ERROR_STYLE);
			MessageUtil.setMessage("Please enter value in search field.", "red");
			return;
		}//if

		String searchVal = searchByTbId.getValue().trim();
		if(Constants.CARD_NUMBER.equals(searchbyLbId.getSelectedItem().getValue())){
			/*if(validateMembershipNumber(searchVal)){
				ContactsLoyalty contactsLoyaltyObj = getContactsLoyaltyByMembershipSearch(searchVal);
				if(contactsLoyaltyObj != null){
					if(contactsLoyaltyObj.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
						displayClosedCardPopUp();
					}else{
						displayLoyaltyDetails(contactsLoyaltyObj);
					}
					
				}
				return;
			}*/
			if(!checkIfLong(searchVal)){
				MessageUtil.setMessage("Please enter valid membership number.", "red");
			}
			else{
				srcCardTbId.setValue(searchVal);
			}
			return;
		}//cardNumber
		else if( Constants.EMAIL_ID.equals(searchbyLbId.getSelectedItem().getValue())){
			if( ! validateEmailAddress(searchVal)){
				logger.error("Invalid EmailAddress");
				return;
			}
		}//email_id
		else if(Constants.MOBILE_PHONE.equals( searchbyLbId.getSelectedItem().getValue()) ){
			if(! validatePhoneNumber(searchVal)){
				logger.error("Invalid Moblie Number");
				return;
			}
		}//mobile
		else if (Constants.FULL_NAME.equals(searchbyLbId.getSelectedItem().getValue())) { 
			if( !validateFullName()){
				logger.error("Invalid full name");
				return;
			}
		}//full name
		logger.debug(">>>>>> end of the method :: fetchLoyaltyDetails");
	}//fetchLoyaltyDetails
	private void sourceCardDetails(String sourceCard){
		
		ContactsLoyalty contactsLoyaltyObj;
		if(validateMembershipNumber(sourceCard)){
			contactsLoyaltyObj = getContactsLoyaltyByMembershipSearch(sourceCard);
			if(contactsLoyaltyObj != null){
				if(contactsLoyaltyObj.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
					MessageUtil.setMessage("Source Card is a closed Card", "red");
				}else if(contactsLoyaltyObj.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED)){
					MessageUtil.setMessage("Source Card is an expired Card", "red");
				}
				else{
					displaySourceLoyaltyDetails(contactsLoyaltyObj);
				}
			}
			else{
				MessageUtil.setMessage("No records found on source card", "red");
			}
			return;
		}

	}
	private void destCardDetails(String sourceCard){

		ContactsLoyalty contactsLoyaltyObj;
		if(validateMembershipNumber(sourceCard)){
			contactsLoyaltyObj = getContactsLoyaltyByMembershipSearch(sourceCard);
			if(contactsLoyaltyObj != null){
				if(contactsLoyaltyObj.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
					MessageUtil.setMessage("Dest Card is a closed Card", "red");
				}else if(contactsLoyaltyObj.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED)){
					MessageUtil.setMessage("Dest Card is an expired Card", "red");
				}else{
					displayDestLoyaltyDetails(contactsLoyaltyObj);
				}
			}
			else{
				try{
					LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
					LoyaltyCards loyaltyCard = loyaltyCardsDao.findByCardNoAnduserId(sourceCard, orgOwner.getUserId());
					if(loyaltyCard == null){
						MessageUtil.setMessage("No card found on destination card", "color:red");
					}
				}catch(Exception e){
					logger.info("Exception in LoyaltyTransferCardController:::"+e);
				}
			}
			return;
		}

	}
	private void setDefaultStores() {

		//List<String> ltyStoreList = ltyPrgmSevice.findAllLtyStores(GetUser.getUserObj().getUserId(),OCConstants.LOYALTY_SERVICE_TYPE_OC);
		//logger.info("loyaltystoreIdList" + ltyStoreList);//APP-4728
		Long orgId = currUser.getUserOrganization().getUserOrgId();
		logger.info("orgId" + orgId);
		List<OrganizationStores> storeIdList = ltyPrgmSevice.getAllStores(orgId);
		logger.info("storeIdList" + storeIdList);
		if (storeIdList == null || storeIdList.size() == 0)return;
		/*Listitem storeItem = null;
		for (String storeName : ltyStoreList) {
			for (OrganizationStores org : storeIdList) {
				if (org.getHomeStoreId().equalsIgnoreCase(storeName)) {
					storeItem = new Listitem(org.getStoreName(), storeName);
					storeItem.setParent(storeNumberLbId);
				}
			}
			storeItem = new Listitem("Store ID " + storeName, storeName);

			storeItem.setParent(storeNumberLbId);
		}*/	
		for (OrganizationStores store : storeIdList) {
			 Listitem li = new Listitem(store.getStoreName(),
					 store.getStoreId());
			 li.setParent(storeNumberLbId);
		 }

	}
	private boolean validateMembershipNumber(String searchVal) {
		logger.debug(">>>>>>>>>>>>> entered in validateMembershipNumber..." +searchVal );
		if(searchVal.isEmpty()) {
			MessageUtil.setMessage("Please enter membership number.", "red");
			//searchByTbId.setValue(Constants.STRING_NILL);
			return false;
		}
		else {
			if(checkIfLong(searchVal)){
				return true;
			}
			else {
				MessageUtil.setMessage("Please enter valid membership number.", "red");
				//searchByTbId.setValue(Constants.STRING_NILL);
				return false;
			}
		}

	}//validateMembershipNumber
	private boolean validateEmailAddress(String searchVal) {
		boolean flag = false;
		if(searchVal.isEmpty() || (Constants.EMAIL_ADDRESS.equalsIgnoreCase(searchVal))) {
			MessageUtil.setMessage("Please enter email address.", "red");
			searchByTbId.setStyle(ERROR_STYLE);
			searchByTbId.setValue(Constants.STRING_NILL);
			flag = false;
		}
		else {
			if(!Utility.validateEmail(searchVal)){
				searchByTbId.setStyle(ERROR_STYLE);
				MessageUtil.setMessage("Please enter valid email address in search field.", "red");
				//searchByTbId.setValue(Constants.STRING_NILL);
				flag = false;
			}
			else{
				flag = true;
				String searchKey =	searchbyLbId.getSelectedItem().getValue();
				findLoyaltyDetails(orgOwner, searchKey, searchVal);
			}
		}
		return flag;
	}//validateEmailAddress

	/**
	 * This method validates the Phone Number
	 * @param searchVal
	 * @return true/false
	 */
	private boolean validatePhoneNumber(String searchVal) {
		logger.debug(">>>>>>>>>>>>> entered in validatePhoneNumber");
		boolean flag = false;
		logger.info("In mobile number");
		if(searchVal.isEmpty() || (Constants.PHONE_NUMBER.equalsIgnoreCase(searchVal))) {
			MessageUtil.setMessage("Please enter phone number.", "red");
			//searchByTbId.setValue(Constants.STRING_NILL);
			searchByTbId.setStyle(ERROR_STYLE);
			flag = false;;
		}
		else {
			if(checkIfLong(searchByTbId.getValue().trim())){
				if((Utility.phoneParse(searchVal,GetUser.getUserObj().getUserOrganization())==null)){
					searchByTbId.setStyle(ERROR_STYLE);
					MessageUtil.setMessage("Please enter valid phone number in search field.", "red");
				//	searchByTbId.setValue(Constants.STRING_NILL);
					searchByTbId.setStyle(ERROR_STYLE);
					flag = false;;
				}
				else{
					flag = true;
					String searchKey =	searchbyLbId.getSelectedItem().getValue();
					findLoyaltyDetails(orgOwner, searchKey, searchVal);
				}
			}
			else {
				MessageUtil.setMessage("Please enter valid phone number.", "red");
				//searchByTbId.setValue(Constants.STRING_NILL);
				searchByTbId.setStyle(ERROR_STYLE);
				flag = false;
			}
		}
		logger.debug("<<<<<<<<<<<<< completed validatePhoneNumber");
		return flag;
	}//validatePhoneNumber

	/**
	 * This method validate the full name
	 * @return
	 */
	private boolean validateFullName() {
		logger.debug("<<<<< entered the method:: validateFullName");
		String searchKey = Constants.STRING_NILL,searchVal =Constants.STRING_NILL;

		logger.info("fname "+first_nameTbId.getValue().trim()+":"+first_nameTbId.getValue().trim().isEmpty() +":\t lname ::"+last_nameTbId.getValue().trim());

		if(("First Name".equalsIgnoreCase(first_nameTbId.getValue().trim())) && ("Last Name".equalsIgnoreCase(last_nameTbId.getValue().trim()) )){
			MessageUtil.setMessage("Please enter name.", "red");
			fullNameSpanId.setVisible(true);
			return false;
		}

		if(first_nameTbId.getValue() != null && first_nameTbId.getValue().trim().isEmpty() && last_nameTbId.getValue() != null && last_nameTbId.getValue().trim().isEmpty()){
			MessageUtil.setMessage("Please enter first name.", "red");
			fullNameSpanId.setVisible(true);
			return false;
		} 

		/*if(last_nameTbId.getValue() != null && last_nameTbId.getValue().trim().isEmpty()){
			MessageUtil.setMessage("Please enter last name.", "red");
			fullNameSpanId.setVisible(true);
			return false;
		}*/

		String fname=Constants.STRING_NILL,lname=Constants.STRING_NILL,str=Constants.STRING_NILL; 
		if(("First Name".equalsIgnoreCase(first_nameTbId.getValue().trim())) || Constants.STRING_NILL.equals(first_nameTbId.getValue().trim()) )
			fname = Constants.STRING_NILL;
		else
			fname = first_nameTbId.getValue().trim();
		if(("Last Name".equalsIgnoreCase(last_nameTbId.getValue().trim())) || Constants.STRING_NILL.equals(last_nameTbId.getValue().trim()) )
				lname = Constants.STRING_NILL;
			else
				lname = last_nameTbId.getValue().trim();

		/*
			if(!Utility.validateName(fname)){
				MessageUtil.setMessage("Enter valid first name. Special characters are not allowed.","color:red","TOP");
				fullNameSpanId.setVisible(true);
				return false;
			}
		}
			if(!Utility.validateName(lname)){
				MessageUtil.setMessage("Enter valid last name. Special characters are not allowed.","color:red","TOP");
				fullNameSpanId.setVisible(true);
				return false ;
			}
		}*/
		

		if(!(Constants.STRING_NILL.equals(fname)) && !(Constants.STRING_NILL.equals(lname))){
			str =fname+","+lname;
			searchKey =Constants.FULL_NAME;
			logger.info(searchKey+"::"+str);
		}
		else if((Constants.STRING_NILL.equals(fname)) && !(Constants.STRING_NILL.equals(lname))){
			str =lname;
			searchKey =Constants.LAST_NAME;
			logger.info(searchKey+"::"+str);
		}
		else if(!(Constants.STRING_NILL.equals(fname)) && (Constants.STRING_NILL.equals(lname))){
			str =fname;
			searchKey ="first_name";
			logger.info(searchKey+"::"+str);
		}
		searchVal = str;	

		findLoyaltyDetails(orgOwner, searchKey, searchVal);
		logger.debug(">>>>>> end of the method :: validateFullName");
		return true;
	}//validateFullName
	public boolean checkIfLong(String inputString) {
		logger.debug(">>>>>>>>>>>>> entered in checkIfLong");
		try {
			Long.parseLong(inputString);
		} catch (NumberFormatException ex) {
			return false;
		}
		logger.debug("<<<<<<<<<<<<< completed checkIfLong ");
		return true;
	}// checkIfLong
	private ContactsLoyalty getContactsLoyaltyByMembershipSearch(String searchVal) {
		logger.debug("<<<<< entered the method:: getContactsLoyaltyByMembershipSearch");
		LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
		ContactsLoyalty contactsLoyalty= null;
		Long cardNum = Long.parseLong(searchVal);
		contactsLoyalty = ltyPrgmService.getContactLtyByMembershipNumber(cardNum,orgOwner.getUserId());

		if(contactsLoyalty != null){
			logger.debug(">>>>>> end of the method :: getContactsLoyaltyByMembershipSearch");
			return contactsLoyalty;
		}
		else{
			//MessageUtil.setMessage("No records found.", "red");
			//resetSearchFeilds();
			logger.debug(">>>>>> end of the method :: getContactsLoyaltyByMembershipSearch");
			return null;
		}
	}//getContactsLoyaltyByMembershipSearch
	private void displaySourceLoyaltyDetails(ContactsLoyalty contactsLoyalty) {
		logger.debug(">>>>>>>>>>>>> entered in displayLoyaltyDetails");
		try {
			ContactsLoyaltyDao 	contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		} catch (Exception e) {
			logger.error("Exception :: ",e);
		}
		Contacts contacts = null;
		
		contacts=contactsLoyalty.getContact();

		//Display's Contact Details
		displaySourceContactDetails(contacts, contactsLoyalty.getCardNumber());
	}
	private void displayDestLoyaltyDetails(ContactsLoyalty contactsLoyalty) {
		logger.debug(">>>>>>>>>>>>> entered in displayLoyaltyDetails");
		try {
			ContactsLoyaltyDao 	contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		} catch (Exception e) {
			logger.error("Exception :: ",e);
		}
		Contacts contacts = null;
		
		contacts=contactsLoyalty.getContact();

		//Display's Contact Details
		displayDestContactDetails(contacts, contactsLoyalty.getCardNumber());
	}
	private void findLoyaltyDetails(Users user,String searchKey,String searchVal ){
		logger.debug("<<<<< entered the method:: findLoyaltyDetails");
		LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
		ContactsLoyalty contactsLoyaltyObj = null;
		//List<Object[]> list = ltyPrgmService.getContactLtyBySearch( userId, searchKey, searchVal);
		 List<Map<String, Object>> contactLoyalties = ltyPrgmService.getContactLtyBySearchCriteria(user, searchKey, searchVal);
		if(contactLoyalties == null || contactLoyalties.isEmpty()){
			MessageUtil.setMessage("No records found with search criteria.", "red");
			logger.error("No records found with search Criteria ");
			clearContactDetails();
			//resetSearchFeilds();
			return;
		}
		else if(contactLoyalties.size()==1){
			Long loyaltyId = (Long)contactLoyalties.get(0).get("loyalty_id");
			contactsLoyaltyObj = ltyPrgmService.getContactLtyById(loyaltyId);
			if(contactsLoyaltyObj == null){
				MessageUtil.setMessage("Please look-up membership before performing this action.", "red");
				clearContactDetails();
				return;
			}
			else{
				//displayLoyaltyDetails(contactsLoyaltyObj);
				srcCardTbId.setValue(contactsLoyaltyObj.getCardNumber());
			}
		}
		else if(contactLoyalties.size() > 1) {
			foundMultipleMatches(contactLoyalties ,searchKey ,searchVal);
		}//else
		logger.debug(">>>>>> end of the method :: findLoyaltyDetails");
	}//findLoyaltyDetails
	private void foundMultipleMatches( List<Map<String, Object>> contactLoyalties, String searchKey,String searchVal) {
		logger.debug(">>>>>>>>>>>>> entered in foundMultipleMatches");
		latestDetailsSubWinId.doHighlighted();
		//openSubWindDiv(latestDetailsSubWinId$foundMultipleMatchesDivId);
		latestDetailsSubWinId$foundMultipleMatchesDivId.setVisible(true);
		String displayLabel=Constants.STRING_NILL;

		if(Constants.FULL_NAME.equalsIgnoreCase(searchKey) || Constants.FIRST_NAME.equalsIgnoreCase(searchKey)|| Constants.LAST_NAME.equalsIgnoreCase(searchKey)){
			displayLabel = "Name";
			fullNameSpanId.setVisible(true);
		}
		else if(Constants.MOBILE_PHONE.equalsIgnoreCase(searchKey)){
			displayLabel = Constants.PHONE_NUMBER;
			fullNameSpanId.setVisible(false);
			searchByTbId.setVisible(true);
		}
		else if(Constants.EMAIL_ID.equalsIgnoreCase(searchKey)){
			displayLabel = Constants.EMAIL_ADDRESS;
			fullNameSpanId.setVisible(false);
			searchByTbId.setVisible(true);
		}

		latestDetailsSubWinId$searchCriteriaLblId.setValue(Constants.STRING_NILL+displayLabel+" : "+searchVal);
		latestDetailsSubWinId$searchCriteriaLblId.setSclass("theme-lbHeading");
		latestDetailsSubWinId.doHighlighted();
		latestDetailsSubWinId.setVisible(true);
		latestDetailsSubWinId.setTitle("Found Multiple Matches");
		latestDetailsSubWinId.setWidth("720px");
		latestDetailsSubWinId.setHeight("300px");
		latestDetailsSubWinId.setTop("45.5px");
		latestDetailsSubWinId.setStyle("scroll:auto;");
		//latestDetailsSubWinId.setPosition("top");
		Components.removeAllChildren(latestDetailsSubWinId$multipleMatchesRgId);
		//
		for (Map<String, Object> contactLoyaltyMap: contactLoyalties) {
			drawRow(contactLoyaltyMap,searchKey);
		}//for
		logger.debug("<<<<<<<<<<<<< completed foundMultipleMatches ");
	}//foundMultipleMatches
	private void drawRow(Map<String, Object> contactLoyaltyMap , String searchKey) {
		//logger.debug(">>>>>>>>>>>>> entered in drawRow");
		Hlayout outerHlayout = null;
		Radio radio = null;
		Hlayout hlayout = null;
		Label label = null;
		Contacts contacts = null;
		LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();

		if(contactLoyaltyMap.containsKey("contact_id")){
			Long contact_id = (Long) contactLoyaltyMap.get("contact_id");
			if(contact_id != null){
				contacts = ltyPrgmService.getContactObj(contact_id);
			}
			else{
				logger.error("Contact Details are not found for card with Membership Number : "+contactLoyaltyMap.get("card_number"));
			}
		}
	
		outerHlayout = new Hlayout();
		radio = new Radio();
		hlayout = new Hlayout();

		if(Constants.FIRST_NAME.equalsIgnoreCase(searchKey) || Constants.LAST_NAME.equalsIgnoreCase(searchKey) || Constants.FULL_NAME.equalsIgnoreCase(searchKey)){
			label = setMembershipNumber(contactLoyaltyMap,hlayout);
			label.setStyle("margin-top:10px;");
			label.setParent(hlayout);
			label = new Label(", Phone# : "+(contacts.getMobilePhone() == null ? Constants.STRING_NILL:contacts.getMobilePhone())+", Email Address : "+(contacts.getEmailId() == null ? Constants.STRING_NILL:contacts.getEmailId()));
			label.setStyle("margin-top:10px;");
			label.setParent(hlayout);
		}
		else if(Constants.MOBILE_PHONE.equalsIgnoreCase(searchKey)){
			label = setMembershipNumber(contactLoyaltyMap,hlayout);
			label.setStyle("margin-top:10px;");
			label.setParent(hlayout);
			label = new Label(", Email Address : "+(contacts.getEmailId() == null ? Constants.STRING_NILL:contacts.getEmailId()) +", Name : "+(contacts.getFirstName() != null ? contacts.getFirstName() : Constants.STRING_NILL)+" "+(contacts.getLastName() != null ? contacts.getLastName() : Constants.STRING_NILL));
			label.setStyle("margin-top:10px;");
			label.setParent(hlayout);
		}
		else if(Constants.EMAIL_ID.equalsIgnoreCase(searchKey)){
			label = setMembershipNumber(contactLoyaltyMap,hlayout);
			label.setStyle("margin-top:10px;");
			label.setParent(hlayout);
			label = new Label(", Phone# : "+(contacts.getMobilePhone() == null ? Constants.STRING_NILL:contacts.getMobilePhone())+", Name : "+(contacts.getFirstName() != null ? contacts.getFirstName() : Constants.STRING_NILL)+" "+(contacts.getLastName() != null ? contacts.getLastName() : Constants.STRING_NILL));
			label.setStyle("margin-top:10px;");
			label.setParent(hlayout);
		}

		radio.setValue(contactLoyaltyMap.get("loyalty_id"));
		radio.setStyle("margin-left:10px;padding-right:0.0em;");
		radio.setParent(outerHlayout);
		hlayout.setStyle("padding-top:7px;");
		hlayout.setParent(outerHlayout);
		outerHlayout.setParent(latestDetailsSubWinId$multipleMatchesRgId);

		//logger.debug("<<<<<<<<<<<<< completed drawRow ");
	}//drawRow
	/**
	 * This method prepare's membership number label with toolTipText
	 * @param contactLoyaltyMap
	 * @param hlayout
	 * @return label
	 */
	private Label setMembershipNumber(Map<String, Object> contactLoyaltyMap,  Hlayout hlayout) {
		//logger.debug(">>>>>>>>>>>>> entered in setMembershipNumber");
		String tooltip = getToolTip(contactLoyaltyMap);
		Label label = new Label("Membership# : ");
		label.setParent(hlayout);
		label = new Label(contactLoyaltyMap.get("card_number")==null?Constants.STRING_NILL:contactLoyaltyMap.get("card_number").toString().trim());
		label.setTooltiptext(tooltip);
		label.setSclass("underline");
		//logger.debug("<<<<<<<<<<<<< completed setMembershipNumber ");
		return label;
	}
	/**
	 * This method returns the tool tip 
	 * @param contactLoyaltyMap
	 * @return toolTip
	 */

	private String getToolTip(Map<String, Object> contactLoyaltyMap ) {
	//	logger.debug(">>>>>>>>>>>>> entered in getToolTip");
		LoyaltyProgram loyaltyProgram =null;
		LoyaltyCardSet loyaltyCardSet =null;
		String toolTip = Constants.STRING_NILL;
		LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
		if(contactLoyaltyMap.containsKey("program_id")){
			Long program_Id = (Long) contactLoyaltyMap.get("program_id");
			if(program_Id != null){
				loyaltyProgram = ltyPrgmService.getProgmObj(program_Id);
			}
			else{
				logger.error("Program Details are not found");
			}
		}

		if(contactLoyaltyMap.containsKey("card_set_id")){
			Long card_Set_Id = (Long) contactLoyaltyMap.get("card_set_id");
			if(card_Set_Id != null){
				loyaltyCardSet = ltyPrgmService.getLoyaltyCardSetObj(card_Set_Id);
			}
			else{
				logger.error("CardSet Details are not found");
			}
		}
		
		if(loyaltyProgram != null ){
			if(loyaltyCardSet != null){
				//For Mobile Based Program.
				toolTip = "Program Name :"+(loyaltyProgram.getProgramName() == null ?Constants.STRING_NILL: loyaltyProgram.getProgramName())+ "\nCard-Set Name : "+(loyaltyCardSet.getCardSetName() == null ? Constants.STRING_NILL:loyaltyCardSet.getCardSetName());
			}
			else{
				toolTip = "Program Name :"+(loyaltyProgram.getProgramName() == null ?Constants.STRING_NILL: loyaltyProgram.getProgramName())+ "\nCard-Set Name : "+ Constants.STRING_NILL;
			}
		}
		else{
			toolTip = "Program Name :"+Constants.STRING_NILL+ "\nCard-Set Name : "+Constants.STRING_NILL;
		}
		//logger.debug("<<<<<<<<<<<<< completed getToolTip ");
		return toolTip;
	}//getToolTip
	
	/**
	 * This method helps to select one contact from multiple match's
	 */
	public void onClick$selectMultipleChoiceBtnId$latestDetailsSubWinId(){
		logger.debug(">>>>>>>>>>>>> entered in onClick$selectMultipleChoiceBtnId$latestDetailsSubWinId");
		LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
		if(latestDetailsSubWinId$multipleMatchesRgId.getSelectedItem() == null || latestDetailsSubWinId$multipleMatchesRgId.getSelectedIndex() == -1){
			MessageUtil.setMessage("Please select a contact.", "red");
			clearContactDetails();
			return;
		}
		else{
			ContactsLoyalty contactsLoyaltyObj = null ;
			latestDetailsSubWinId.setVisible(false);
			Long contLoyaltyId = latestDetailsSubWinId$multipleMatchesRgId.getSelectedItem().getValue();
			contactsLoyaltyObj = ltyPrgmService.getContactLtyById(contLoyaltyId);

			if(contactsLoyaltyObj == null){
				MessageUtil.setMessage("No records found.", "red");
				latestDetailsSubWinId$multipleMatchesRgId.setSelectedItem(null);
				latestDetailsSubWinId$multipleMatchesRgId.setSelectedIndex(-1);
				clearContactDetails();;
				return;
			}

			else{
				if(contactsLoyaltyObj.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
					//displayClosedCardPopUp();
					srcCardTbId.setValue(contactsLoyaltyObj.getCardNumber());
				}else{
				//displayLoyaltyDetails(contactsLoyaltyObj);
				srcCardTbId.setValue(contactsLoyaltyObj.getCardNumber());
				latestDetailsSubWinId$multipleMatchesRgId.setSelectedItem(null);
				latestDetailsSubWinId$multipleMatchesRgId.setSelectedIndex(-1);
				}
			}

		}
		logger.debug("<<<<<<<<<<<<< completed onClick$selectMultipleChoiceBtnId$latestDetailsSubWinId ");
	}//onClick$selectMultipleChoiceBtnId$latestDetailsSubWinId
	private void openSubWindDiv(Div tempDiv) {
		logger.debug(">>>>>>>>>>>>> entered in openSubWindDiv");

		/*Div[] openSubWindDiv = {latestDetailsSubWinId$viewSkuDetailsDivId,latestDetailsSubWinId$viewPromotionDetailsDivId,latestDetailsSubWinId$viewPurchaseDetailsDivId,latestDetailsSubWinId$foundMultipleMatchesDivId,latestDetailsSubWinId$closeCardDivId};

		for(int i=0; i<openSubWindDiv.length; i++ ) {

			if(openSubWindDiv[i] == tempDiv) {
				openSubWindDiv[i].setVisible(true);
			}
			else{
				openSubWindDiv[i].setVisible(false);
			}
		} //for
		
*/		tempDiv.setVisible(true);
		logger.debug("<<<<<<<<<<<<< completed openSubWindDiv ");
	} // openSubWindDiv

}
