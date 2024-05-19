package org.mq.optculture.controller.loyalty;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.simple.JSONObject;
import org.mq.marketer.campaign.beans.AutoSMS;
import org.mq.marketer.campaign.beans.AutoSmsQueue;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.CouponCodes;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.EventTrigger;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyThresholdAlerts;
import org.mq.marketer.campaign.beans.LoyaltyThresholdBonus;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.LoyaltyTransactionExpiry;
import org.mq.marketer.campaign.beans.LoyaltyTransactionParent;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.RetailProSalesCSV;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.UserSMSGateway;
import org.mq.marketer.campaign.beans.UserSMSSenderId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.contacts.UploadCSVFile;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObservable;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObserver;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.AutoSMSDao;
import org.mq.marketer.campaign.dao.AutoSmsQueueDao;
import org.mq.marketer.campaign.dao.AutoSmsQueueDaoForDML;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.CountryCodeDao;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.EventTriggerDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.OCSMSGatewayDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDao;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDaoForDML;
import org.mq.marketer.campaign.dao.SuppressedContactsDao;
import org.mq.marketer.campaign.dao.SuppressedContactsDaoForDML;
import org.mq.marketer.campaign.dao.UnsubscribesDaoForDML;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.dao.UserSMSGatewayDao;
import org.mq.marketer.campaign.dao.UserSMSSenderIdDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptLtyMembshpPwd;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.CardSet;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.loyalty.LoyaltyAutoCommGenerator;
import org.mq.optculture.business.loyalty.LoyaltyMultipleTierUpgradeRules;
import org.mq.optculture.business.loyalty.LoyaltyOTPOCService;
import org.mq.optculture.business.loyalty.LoyaltyOTPOCServiceImpl;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.business.loyalty.LoyaltyRedemptionOCService;
import org.mq.optculture.business.loyalty.LoyaltyRedemptionOCServiceImpl;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.data.dao.LoyaltyAutoCommDao;
import org.mq.optculture.data.dao.LoyaltyCardSetDao;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
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
import org.mq.optculture.model.digitalReceipt.DRHead;
import org.mq.optculture.model.digitalReceipt.DRHead.User;
import org.mq.optculture.model.digitalReceipt.DRJsonRequest;
import org.mq.optculture.model.digitalReceipt.DRReceipt;
import org.mq.optculture.model.ocloyalty.AdditionalInfo;
import org.mq.optculture.model.ocloyalty.Amount;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.model.ocloyalty.Customer;
import org.mq.optculture.model.ocloyalty.Discounts;
import org.mq.optculture.model.ocloyalty.HoldBalance;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyOTPRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyOTPResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyRedemptionRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyRedemptionResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyUser;
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.ocloyalty.MembershipRequest;
import org.mq.optculture.model.ocloyalty.MembershipResponse;
import org.mq.optculture.model.ocloyalty.OTPRedeemLimit;
import org.mq.optculture.model.ocloyalty.Promotion;
import org.mq.optculture.model.ocloyalty.RequestHeader;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.util.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Span;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import com.google.gson.Gson;

import org.mq.optculture.business.common.BaseService;
//import org.mq.optculture.business.digitalReceipt.DRToLtyExtractionImpl;
/**
 * This object perform  operations to fetch loyalty details of OC Type Loyalty Customer, display's Balance's,Transaction Summary, Purchase Summary,Available Points & Rewards,
 * along with this it helps to Suspend the membership,add or subtract points & rewards
 * @author vinod.bokare
 */
public class LoyaltyCustomerLookUpAndRedeemController extends GenericForwardComposer<Window> implements EventListener{


	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Users currUser;
	private Window LoyaltyCustomerLookUpAndRedeemWinId,latestDetailsSubWinId,custExport;

	private Div moveTierDivId,datesDivId,skuDetailsDivId,promotionDetailsDivId,whichprogramDivId,custExport$chkDivId,searchCriteriaDivId,latestDetailsSubWinId$descDivId;
	private Div latestDetailsSubWinId$viewSkuDetailsDivId,latestDetailsSubWinId$viewPromotionDetailsDivId,latestDetailsSubWinId$foundMultipleMatchesDivId,latestDetailsSubWinId$closeCardDivId;
	private Rows  latestDetailsSubWinId$viewAllSKURowsId,latestDetailsSubWinId$promotionRowsId,latestDetailsSubWinId$multipleFoundRowId;
	private Div latestDetailsSubWinId$viewPurchaseDetailsDivId,addTypeDivId,subTypeDivId,finalHoldBalDivId,transferLblDivId,transferTableDivId, editContactDivId;
	private Radiogroup latestDetailsSubWinId$multipleMatchesRgId;
	private Label nameLblId,toLbId,fromLbId,membershipLblId,moblieNumberLblId,emailAddressLblId, membershipMSummaryLblId,membershipStatusMSummaryLblId,optInDateMSummaryLblId,sourceMSummaryLblId,subsidiaryNumberMSummaryLblId, storeNumberMSummaryLblId,
	lifeTimePurchaseValueMSummaryLblId,tierNameMSummaryLblId,expirationDateMSummaryLblId,totalIssuanceTSummaryLblId,totalRedemptionsTSummaryLblId,lifetimePointsTSummaryLblId,
	totalAmountEarnedTSummaryLblId,pointRedeemedTSummaryLblId,amountRedeemedTSummaryLblId,totalGiftTopupAmountTSummaryLblId,
	giftAmountRedeemedTSummaryLblId,holdBalLbId,holdBalValLbId,finalHoldBalanceLblId;
	private Label finalBalanceRewardsLblId,finalBalancePointsLblId,latestDetailsSubWinId$searchCriteriaLblId,latestDetailsSubWinId$closedCardLblId,latestDetailsSubWinId$descLBId;
	private Label balancePointLblId,balanceCurrencyLblId,expiredPointsLblId,expiredAmountLblId,
				  	balanceGiftAmountLblId,expiredGiftAmountLblId,transferedOrNewCardLblId,balanceCurrency2LblId;
	//private Label visitOnLblId,storeLblId,purchaseAmountLblId,issuedAmountLblId,earningLblId,redemptionLblId,itemPurchasedLblId,promotionsAppliedLblId;
	private Span fullNameSpanId;
	private Textbox searchByTbId,addTbId,subTbId,first_nameTbId,last_nameTbId,ltyPwdTbMvtierdId,
	subTb2Id,storeNoTbId,receiptNoTbId,receiptAmtTbId, emailTxtBxId,phnTxtBxId;;;
	private Label programNameLbId,cardSetNameLbId,currentTierLbId,descpopupId$descLableId;
	private Button suspendMembershipBtnId,addBtnId,subtractBtnId,submitPwdBtnMvtierId,moveTierBtnId;
	private Listbox transListLbId,timeDurLbId,addLbId2,searchbyLbId,addLbId,subLbId,pageSizeLbId,addTypeLbId,subTypeLbId,transferListLbId,toTierLbId;
	//private Listitem upgradeId,downgradeId;
	private Combobox exportCbId;
	private Intbox lpIBxId;
	private Paging loyaltyListBottomPagingId;
	private MyDatebox fromDateboxId,toDateboxId;
	private TimeZone clientTimeZone;
	private Listitem listItemHoldPts,listItemHoldAmt,listItemPts,listItemAmt;
	private  static String ERROR_STYLE = "border:1px solid #DD7870;";
	private  static String NORMAL_STYLE = "border:1px solid #7F9DB9;";
	private  static String ITALIC_GREY_STYLE = "color:grey;font-style: italic;font-size:12px;";
	private int monthsDiff,  heightForTrans;
	private Calendar startDate,endDate;
	private ContactsLoyalty contactsLoyaltyObj;
	private Popup ltyPwdPopupId,ltyPwdPopupSubId,ltyPwdPopupSuspendId,ltyPwdPopupMvtierId;
	private Textbox ltyPwdTbId,ltyPwdTbSubId,ltyPwdTbSuspendId, firstNameTbId, lastNameTbId, emailTbId, phoneTbId;
	private Borderlayout displaybrdrLOut;
	//private A whichItemsPurchaseAnchId,whichItemsPromotionAnchId;
	private List<ContactsLoyalty> transferList=null;
	private List<Map<String, Object>> storeNumberNameMapList;
	private List<Map<String, Object>> subsidiaryNumberNameMapList;

	private Doublebox redeemedAmountTbId,receiptAmountTbId;
	private Textbox storeNumberTbId,receiptNumTbId,otpTbId;
	private Label errorMsgTbId;
	private Div otpDivId;
	
	private Session session ;
	private  String  userCurrencySymbol = "$ ";  
	
	private final String FRAUD_ALERT_CARDNO="Fraud_Alert_CardNo";
	private final String TRANSFER_DEST_CARDNO="Transfer_Dest_CardNo";
	private final String FOR_USER = "FOR_USER";
	private Boolean isPasswordRequired;
	private UserActivitiesDaoForDML userActivitiesDaoForDML;
	private Long currentOCAdminUserId;
	private LoyaltyProgramDao loyaltyProgramDao;
	/**
	 * default constructor
	 */
	public LoyaltyCustomerLookUpAndRedeemController(){
		super();
		currUser = GetUser.getUserObj();
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Customer Look-up", Constants.STRING_NILL, style, true);
		 session = Sessions.getCurrent();
		 userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		 loyaltyProgramDao = (LoyaltyProgramDao) SpringUtil.getBean("loyaltyProgramDao");
		 currentOCAdminUserId = (Long)session.getAttribute("currentOCAdmin");
	}
	/**
	 * doAfterCompose
	 */
	@Override
	public void doAfterCompose(Window comp) throws Exception {

		logger.debug("<<<<< entered the method:: doAfterCompose");
		super.doAfterCompose(comp);
		OrganizationStoresDao organizationStoresDao =	(OrganizationStoresDao) ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
		storeNumberNameMapList  = organizationStoresDao.findStoreNumberNameMapList(currUser.getUserOrganization().getUserOrgId());
		subsidiaryNumberNameMapList = organizationStoresDao.findSubsidiaryNameNumberMapList(currUser.getUserOrganization().getUserOrgId());
		isPasswordRequired = (Boolean)session.getAttribute("isPasswordRequired");
		contactsLoyaltyObj = null;
		exportCbId.setSelectedIndex(0);
		clientTimeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
		searchbyLbId.setSelectedIndex(0);
		
		// setting owner of the organization if current user is not the owner(Store_operator usecase)
		UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Long orgOwnerId = usersDao.getOwnerofOrg(currUser.getUserOrganization().getUserOrgId());
		Users orgOwner = usersDao.findByUserId(orgOwnerId);
		if(orgOwner.getUserId() != currUser.getUserId()){
			currUser = orgOwner;//?for APP - 1015
		}

		//Clear's the fields
		clearFeilds();
		searchByTbId.setStyle(ITALIC_GREY_STYLE);
		//searchByTbId.setFocus(true);
		searchByTbId.setValue(Constants.EMAIL_ADDRESS);
		
		//Get default dates
		getDateValues();
		String transferCardNO=	(String)session.getAttribute(TRANSFER_DEST_CARDNO);
		Users user =	(Users)session.getAttribute(FOR_USER);
		if(transferCardNO!=null){
			logger.info("card No "+transferCardNO);
			searchByTbId.setValue(transferCardNO); 
			// searchCriteriaDivId.setVisible(false);
			currUser = user;
			onClick$filterBtnId();
			session.removeAttribute(TRANSFER_DEST_CARDNO); 
		}
		String cardNO= (String)session.getAttribute(FRAUD_ALERT_CARDNO);
	    if(cardNO!=null){
	     logger.info("card No "+cardNO);
	     searchByTbId.setValue(cardNO); 
	    // searchCriteriaDivId.setVisible(false);
	     onClick$filterBtnId();
	    session.removeAttribute(FRAUD_ALERT_CARDNO); 
	    }
		/*String transferCard = (String)session.getAttribute(TRANSFER_DEST_CARDNO);
		if(transferCard!=null){
			logger.info("Transfer card No "+transferCard);
			searchByTbId.setValue(transferCard); 
			// searchCriteriaDivId.setVisible(false);
			onClick$filterBtnId();
			session.removeAttribute(TRANSFER_DEST_CARDNO); 
		}*/
	   //
/*	    String currSymbol = currUser.getCountryCurrency();
	    if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol;*/
	    //
	    
	   String currSymbol = Utility.countryCurrencyMap.get(currUser.getCountryType());
	   if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol + " ";
	    
	     
	    
		logger.debug(">>>>>> End of the method :: doAfterCompose");
	}//doAfterCompose

	private void displayAdjustmentType() {
		try {
			if(contactsLoyaltyObj == null) return;
			if(contactsLoyaltyObj.getRewardFlag() != null && (OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equalsIgnoreCase(contactsLoyaltyObj.getRewardFlag()) || 
					OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equalsIgnoreCase(contactsLoyaltyObj.getRewardFlag()))){
				if((contactsLoyaltyObj.getHoldAmountBalance() != null && !contactsLoyaltyObj.getHoldAmountBalance().toString().isEmpty())
						|| (contactsLoyaltyObj.getHoldPointsBalance() != null && !contactsLoyaltyObj.getHoldPointsBalance().toString().isEmpty())) {
					//addTypeDivId.setVisible(true);
					toLbId.setVisible(true);
					subTypeDivId.setVisible(true);
					fromLbId.setVisible(true);
					finalHoldBalDivId.setVisible(true);
					listItemHoldPts.setVisible(true);
					listItemHoldAmt.setVisible(true);
				}
				else if(contactsLoyaltyObj.getProgramTierId() != null){
					LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
					LoyaltyProgramTier loyaltyProgramTier = loyaltyProgramTierDao.findByTierId(contactsLoyaltyObj.getProgramTierId());
					if(loyaltyProgramTier.getActivationFlag() == OCConstants.FLAG_YES) {
						//addTypeDivId.setVisible(true);
						toLbId.setVisible(true);
						subTypeDivId.setVisible(true);
						fromLbId.setVisible(true);
						finalHoldBalDivId.setVisible(true);
						listItemHoldPts.setVisible(true);
						listItemHoldAmt.setVisible(true);
					}
				}
			}
		}catch(Exception e) {
			logger.error("Exception ::",e);
		}
	}
	
	
	private String fetchStoreName(String store_number){
		 
		 for (Map<String, Object> eachMap: storeNumberNameMapList) {

			 if(eachMap.get("home_store_id").toString().equals(store_number)){
				 if(eachMap.get("store_name") != null){
					 return eachMap.get("store_name").toString();
				 }else{
					 return "Store ID "+store_number;
				 }
			 }
		 }
		 
		 return "Store ID "+store_number;
	}
	private String fetchSubsidiaryName(String subsidiary_number){
		 
		 for (Map<String, Object> eachMap: subsidiaryNumberNameMapList) {

			 if(eachMap.get("subsidary_id").toString().equals(subsidiary_number)){
				 if(eachMap.get("subsidiary_name") != null){
					 return eachMap.get("subsidiary_name").toString();
				 }else{
					 return "Subsidiary ID "+subsidiary_number;
				 }
			 }
		 }
		 
		 return "Subsidiary ID "+subsidiary_number;
	}
	/**
	 * This method captures the on click event of filter button
	 */
	public void onClick$filterBtnId(){
		try {
		logger.debug("<<<<< entered the method:: onClick$filterBtnId");
		//clear the fields
		clearFeilds();
		//searchBy=searchbyLbId.getSelectedItem();
		//calling fetch loyalty details
		fetchLoyaltyDetails();
		//Display Adjustment type
		displayAdjustmentType();
		/*displaybrdrLOut.setHeight((700+transferListLbId.getChildren().size()*30+heightForTrans)+"px");
		LoyaltyCustomerLookUpAndRedeemWinId.setHeight((900+transferListLbId.getChildren().size()*30+heightForTrans)+"px");*/
		displayChangingTier();
		editContactDivId.setVisible(false);
		onSelect$addLbId2();
		logger.debug(">>>>>> end of the method :: onClick$filterBtnId");
		}catch(Exception e) {
		logger.info("while filtering for look up and redeem "+e);
		}
		}//onClick$filterBtnId
	
	public void onClick$editContactBtnId(){
		logger.debug("<<<<< entered the method:: onClick$editContactBtnId");
		editContactDivId.setVisible(true);
		
		logger.debug(">>>>>> end of the method :: onClick$editContactBtnId");
	}//onClick$editContactBtnId
	
	public void onClick$cancelBtnId(){
		logger.debug("<<<<< entered the method:: onClick$cancelBtnId");
		editContactDivId.setVisible(false);
	}// onClick$cancelBtnId()
	
	public void onClick$saveBtnId(){
		logger.debug("<<<<< entered the method:: onClick$saveBtnId");
		try{
			if(contactsLoyaltyObj == null){
				MessageUtil.setMessage("Please look-up membership before performing this action.", "red");
				clearFeilds();
				return;
			}
			Contacts contact = contactsLoyaltyObj.getContact();
			Users conUser = contact.getUsers();
			ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);		
			Contacts contactOriginal = contactsDao.findById(contact.getContactId());
			ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
			
			LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName("loyaltyProgramDao");
			LoyaltyProgram loyaltyProgram = loyaltyProgramDao.findById(contactsLoyaltyObj.getProgramId());
		

			if( !firstNameTbId.getValue().trim().isEmpty()){
				contact.setFirstName(firstNameTbId.getValue().trim());

			}
			if( !lastNameTbId.getValue().trim().isEmpty()){
				contact.setLastName(lastNameTbId.getValue().trim());
			}
			
			String fullName = contact.getFirstName() != null ? (contact.getFirstName() + " ") : "";
			
			fullName = fullName + ( contact.getLastName() != null ? contact.getLastName() : ""); 
			nameLblId.setValue(fullName);
			if( !emailTbId.getValue().trim().isEmpty()){
				if(Utility.validateEmail(emailTbId.getValue().trim())){
				//	String qry = "FROM Contacts WHERE users="+contactsLoyaltyObj.getUserId()+" AND emailId = '"+emailTbId.getValue().trim()+"'";
					List<Contacts> emailContactList = contactsDao.findBy(conUser.getUserId(), emailTbId.getValue().trim());
					if(emailContactList == null || emailContactList.isEmpty()){
						contact.setEmailId(emailTbId.getValue().trim());	
						
						//Set Email Address
						emailAddressLblId.setValue(contact.getEmailId() == null ? Constants.STRING_NILL:contact.getEmailId());

					}else{
						MessageUtil.setMessage("Another Customer exists with the given Email Address. Please provide another Email", "green");
						return;
					}
				}else{
					MessageUtil.setMessage("Please enter valid email address.", "red");
					return;
				}
			}
			if(!phoneTbId.getValue().trim().isEmpty()){
				String phoneVal = Utility.phoneParse(phoneTbId.getValue().trim(),currUser.getUserOrganization());
					if(phoneVal != null){
						//String qry = "FROM Contacts WHERE users="+contactsLoyaltyObj.getUserId()+" AND mobilePhone like '%"+phoneTbId.getValue().trim()+"'";
						List<Long> mobileContactList = contactsDao.findCidsList(phoneVal, conUser.getUserId());//(phoneId, userId)(qry);
						if(mobileContactList == null || mobileContactList.isEmpty()){
							contact.setMobilePhone(phoneVal);
							//Set Mobile Number
							moblieNumberLblId.setValue(contact.getMobilePhone() == null ? Constants.STRING_NILL:contact.getMobilePhone());
							LoyaltyProgramHelper.updateLoyaltyMembrshpPhone( contact, phoneVal);
						}else{
							MessageUtil.setMessage("Another Customer exists with the given Phone NUmber. Please provide another Mobile.", "green");
							return;
						}
					}
					else{
						MessageUtil.setMessage("Please enter valid phone number.", "red");
						return;
					}
			}
			//updating contact
			if(Utility.isModifiedContact(contact,contactOriginal ))
			{
				logger.info("entered Modified date");
				contact.setModifiedDate(Calendar.getInstance());
			}	
			contactsDaoForDML.saveOrUpdate(contact);
			LoyaltyProgramHelper.updateLoyaltyMembrshpPhone(contact, contact.getMobilePhone());
			LoyaltyProgramHelper.updateLoyaltyEmailId(contact, contact.getEmailId());
			MessageUtil.setMessage("Contact Updated Successfully.", "green");
			clearSaveContact();
			
			editContactDivId.setVisible(false);
			
			
			
			
			
		}catch(Exception e){
			logger.error("Exception while updating contact::",e);
		}
		logger.debug(">>>>>> end of the method :: onClick$saveBtnId");
	}
	

	
	private void displayChangingTier(){
		
		try{
			List<LoyaltyProgramTier> loyaltyProgramTiers;
			
			LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName("loyaltyProgramTierDao");
			//List<LoyaltyProgramTier> loyaltyProgramTiers = loyaltyProgramTierDao.fetchTiersByProgramId(contactsLoyaltyObj.getProgramId());
			if(contactsLoyaltyObj!=null)
				loyaltyProgramTiers = loyaltyProgramTierDao.fetchTiersByProgramId(contactsLoyaltyObj.getProgramId());
			else
				loyaltyProgramTiers=new ArrayList();
			if(loyaltyProgramTiers.size() <=1){
				moveTierDivId.setVisible(false);
				return;
			}
			else {
				moveTierDivId.setVisible(true);
			}
			
			
			Collections.sort(loyaltyProgramTiers, new Comparator<LoyaltyProgramTier>() {

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
			
			LoyaltyProgramTier currentTier = null;
			/*Map<String, Long> upgradeList=new HashMap<String, Long>(), downgradeList=new HashMap<String, Long>();*/
			if(toTierLbId.getItemCount() != 0){
				for(int count = toTierLbId.getItemCount(); count>0; count--){
					toTierLbId.removeItemAt(count-1);
				}
				}
			for(LoyaltyProgramTier tier : loyaltyProgramTiers){
			if(contactsLoyaltyObj.getProgramTierId()!= null  && contactsLoyaltyObj.getProgramTierId().toString().equals(tier.getTierId().toString())){
				//currentTier = tier;
				currentTierLbId.setValue("Tier adjustment from "+tier.getTierName()+" to ");
			}else{
				toTierLbId.appendItem(tier.getTierName(), tier.getTierId().toString());
			}
			}
			
			toTierLbId.setSelectedIndex(0);
			
		}catch (Exception e) {
			logger.error("Exception :: ",e);
		}
		
		
	}
	
	
	
	
	/**
	 * This method fetch the loyalty details based on search criteria.
	 */
	private void fetchLoyaltyDetails() {
	try {
		logger.debug("<<<<< entered the method:: fetchLoyaltyDetails");
		if((Constants.STRING_NILL.equals(searchByTbId.getValue()) && searchByTbId.getValue().isEmpty()) && ( Constants.STRING_NILL.equals(first_nameTbId.getValue())) && ( Constants.STRING_NILL.equals(last_nameTbId.getValue()))){
			searchByTbId.setStyle(ERROR_STYLE);
			MessageUtil.setMessage("Please enter value in search field.", "red");
			return;
		}//if

		String searchVal = searchByTbId.getValue().trim();
		if(Constants.CARD_NUMBER.equals(searchbyLbId.getSelectedItem().getValue())){
			if(validateMembershipNumber(searchVal)){
				contactsLoyaltyObj = getContactsLoyaltyByMembershipSearch(searchVal);
				if(contactsLoyaltyObj != null){
					if(contactsLoyaltyObj.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
						displayClosedCardPopUp();
					}else{
						displayLoyaltyDetails(contactsLoyaltyObj);
					}
				}
				return;
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
	}catch(Exception e) {}
	
	}//fetchLoyaltyDetails

	/**
	 * This method fetch loyalty details based on the search  key and value
	 * @param userId
	 * @param searchKey
	 * @param searchVal
	 */
	private void findLoyaltyDetails(Users user,String searchKey,String searchVal ){
		logger.debug("<<<<< entered the method:: findLoyaltyDetails");
		LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
		//List<Object[]> list = ltyPrgmService.getContactLtyBySearch( userId, searchKey, searchVal);
		 List<Map<String, Object>> contactLoyalties = ltyPrgmService.getContactLtyBySearchCriteria(user, searchKey, searchVal);
		if(contactLoyalties == null || contactLoyalties.isEmpty()){
			MessageUtil.setMessage("No records found with search criteria.", "red");
			logger.error("No records found with search Criteria ");
			clearFeilds();
			//resetSearchFeilds();
			return;
		}
		else if(contactLoyalties.size()==1){
			Long loyaltyId = (Long)contactLoyalties.get(0).get("loyalty_id");
			contactsLoyaltyObj = ltyPrgmService.getContactLtyById(loyaltyId);
			if(contactsLoyaltyObj == null){
				MessageUtil.setMessage("Please look-up membership before performing this action.", "red");
				clearFeilds();
				return;
			}
			else{
				displayLoyaltyDetails(contactsLoyaltyObj);
			}
		}
		else if(contactLoyalties.size() > 1) {
			foundMultipleMatches(contactLoyalties ,searchKey ,searchVal);
		}//else
		logger.debug(">>>>>> end of the method :: findLoyaltyDetails");
	}//findLoyaltyDetails


	/**
	 * This method search's for Contact's loyalty based on membership number
	 * @param searchVal
	 * @return ContactsLoyalty
	 */
	private ContactsLoyalty getContactsLoyaltyByMembershipSearch(String searchVal) {
		logger.debug("<<<<< entered the method:: getContactsLoyaltyByMembershipSearch");
		LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
		ContactsLoyalty contactsLoyalty= null;
		Long cardNum = Long.parseLong(searchVal);
		contactsLoyalty = ltyPrgmService.getContactLtyByMembershipNumber(cardNum,currUser.getUserId());

		if(contactsLoyalty != null){
			logger.debug(">>>>>> end of the method :: getContactsLoyaltyByMembershipSearch");
			return contactsLoyalty;
		}
		else{
			MessageUtil.setMessage("No records found.", "red");
			clearFeilds();
			//resetSearchFeilds();
			logger.debug(">>>>>> end of the method :: getContactsLoyaltyByMembershipSearch");
			return null;
		}
	}//getContactsLoyaltyByMembershipSearch

	/**
	 * This method helps to display pop-up window with multiple matches
	 * @param list
	 * @param searchKey
	 * @param searchVal
	 */
	private void foundMultipleMatches( List<Map<String, Object>> contactLoyalties, String searchKey,String searchVal) {
		logger.debug(">>>>>>>>>>>>> entered in foundMultipleMatches");
		latestDetailsSubWinId.doHighlighted();
		openSubWindDiv(latestDetailsSubWinId$foundMultipleMatchesDivId);
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
		latestDetailsSubWinId.setWidth("1200px");
		latestDetailsSubWinId.setHeight("500px");
		latestDetailsSubWinId.setTop("45.5px");
		latestDetailsSubWinId.setStyle("scroll:auto;");
		//latestDetailsSubWinId.setPosition("top");
		//Components.removeAllChildren(latestDetailsSubWinId$multipleMatchesRgId);	
					
		drawRowGrid(contactLoyalties, searchKey);
		logger.debug("<<<<<<<<<<<<< completed foundMultipleMatches ");
	}//foundMultipleMatches

	/**
	 * This method help's to draw each row in Multiple matches window
	 * @param object
	 * @param searchKey
	 */
	private void drawRow(Map<String, Object> contactLoyaltyMap , String searchKey) {
		logger.debug(">>>>>>>>>>>>> entered in drawRow");
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

		logger.debug("<<<<<<<<<<<<< completed drawRow ");
	}//drawRow


	/**
	 * This method prepare's membership number label with toolTipText
	 * @param contactLoyaltyMap
	 * @param hlayout
	 * @return label
	 */
	private Label setMembershipNumber(Map<String, Object> contactLoyaltyMap,  Hlayout hlayout) {
		logger.debug(">>>>>>>>>>>>> entered in setMembershipNumber");
		String tooltip = getToolTip(contactLoyaltyMap);
		Label label = new Label("Membership# : ");
		label.setParent(hlayout);
		label = new Label(contactLoyaltyMap.get("card_number")==null?Constants.STRING_NILL:contactLoyaltyMap.get("card_number").toString().trim());
		label.setTooltiptext(tooltip);
		//label.setSclass("underline");
		logger.debug("<<<<<<<<<<<<< completed setMembershipNumber ");
		return label;
	}
	/**
	 * This method returns the tool tip 
	 * @param contactLoyaltyMap
	 * @return toolTip
	 */

	private String getToolTip(Map<String, Object> contactLoyaltyMap ) {
		logger.debug(">>>>>>>>>>>>> entered in getToolTip");
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
		logger.debug("<<<<<<<<<<<<< completed getToolTip ");
		return toolTip;
	}//getToolTip
	
	/**
	 * This method helps to select one contact from multiple match's
	 */
	public void openMembership(String contLoyaltyIdString){
		logger.debug(">>>>>>>>>>>>> entered in onClick$selectMultipleChoiceBtnId$latestDetailsSubWinId");
		LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
		
			contactsLoyaltyObj = null ;
			latestDetailsSubWinId.setVisible(false);
			Long contLoyaltyId = Long.parseLong(contLoyaltyIdString);
			contactsLoyaltyObj = ltyPrgmService.getContactLtyByMembershipNumber(contLoyaltyId,currUser.getUserId());

			if(contactsLoyaltyObj == null){
				MessageUtil.setMessage("No records found.", "red");
				latestDetailsSubWinId$multipleMatchesRgId.setSelectedItem(null);
				latestDetailsSubWinId$multipleMatchesRgId.setSelectedIndex(-1);
				clearFeilds();
				return;
			}

			else{
				if(contactsLoyaltyObj.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
					displayClosedCardPopUp();
				}else{
				displayLoyaltyDetails(contactsLoyaltyObj);
				displayChangingTier();//APP-3049
				latestDetailsSubWinId$multipleMatchesRgId.setSelectedItem(null);
				latestDetailsSubWinId$multipleMatchesRgId.setSelectedIndex(-1);
				editContactDivId.setVisible(false);
				}
			}

		onSelect$addLbId2();
		logger.debug("<<<<<<<<<<<<< completed onClick$selectMultipleChoiceBtnId$latestDetailsSubWinId ");
	}//onClick$selectMultipleChoiceBtnId$latestDetailsSubWinId
	private void displayClosedCardPopUp(){
		latestDetailsSubWinId.doHighlighted();
		openSubWindDiv(latestDetailsSubWinId$closeCardDivId);
		latestDetailsSubWinId$closedCardLblId.setValue(contactsLoyaltyObj.getCardNumber()+"");
		latestDetailsSubWinId$closedCardLblId.setSclass("theme-lbHeading");
		latestDetailsSubWinId.setVisible(true);
		latestDetailsSubWinId.setTitle("Closed Card");
		latestDetailsSubWinId.setHeight("200px");
		latestDetailsSubWinId.setWidth("500px");
	}
	public void onClick$goToNewCardBtnId$latestDetailsSubWinId(){
		if(contactsLoyaltyObj.getTransferedTo()!=null){
		latestDetailsSubWinId.setVisible(false);
		LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService(); 
		contactsLoyaltyObj =  ltyPrgmService.getContactLtyById(contactsLoyaltyObj.getTransferedTo());
		if(Constants.CARD_NUMBER.equals(searchbyLbId.getSelectedItem().getValue()))searchByTbId.setValue(contactsLoyaltyObj.getCardNumber()+"");
		displayLoyaltyDetails(contactsLoyaltyObj);
		latestDetailsSubWinId$multipleMatchesRgId.setSelectedItem(null);
		latestDetailsSubWinId$multipleMatchesRgId.setSelectedIndex(-1);
		latestDetailsSubWinId.setHeight("300px");
		latestDetailsSubWinId.setWidth("800px");
		}
	}
	
	public void onClick$goToClosedCardBtnId$latestDetailsSubWinId(){
		latestDetailsSubWinId.setVisible(false);
		displayLoyaltyDetails(contactsLoyaltyObj);
		latestDetailsSubWinId$multipleMatchesRgId.setSelectedItem(null);
		latestDetailsSubWinId$multipleMatchesRgId.setSelectedIndex(-1);
		latestDetailsSubWinId.setHeight("300px");
		latestDetailsSubWinId.setWidth("800px");
	}

	/**
	 * This method displays  Loyalty Details
	 * @param contactsLoyalty
	 */
	private void displayLoyaltyDetails(ContactsLoyalty contactsLoyalty) {
		logger.debug(">>>>>>>>>>>>> entered in displayLoyaltyDetails");
		try {
			ContactsLoyaltyDao 	contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			transferList = contactsLoyaltyDao.findChildrenByParent(contactsLoyaltyObj.getUserId(), contactsLoyaltyObj.getLoyaltyId());
		} catch (Exception e) {
			logger.error("Exception :: ",e);
		}
		LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
		Contacts contacts = null;
//		LoyaltyTransactionChild loyaltyTransactionChild = ltyPrgmService.getLoyaltyTransactionChild(contactsLoyalty.getCardNumber(),currUser.getUserId());
		List<RetailProSalesCSV> retailProSalesList = null;
		
		contacts=contactsLoyalty.getContact();

		//Display's Contact Details
		displayContactDetails(contacts, contactsLoyalty.getCardNumber());
		//Display's Membership Summary
		displayMembershipSummary(contactsLoyalty,contacts);

		//Display's Balance Summary
		displayBalanceSummary(contactsLoyalty);
		//Display's Transaction Summary
		displayTransactionSummary(contactsLoyalty);
		//Display's Final Balances
		displayFinalBalances(contactsLoyalty);
		//Check's Suspend Button
		displaySuspendButton(contactsLoyalty);
		//Display's Pagination
		displayPagination(contactsLoyalty);
		// Display's Transferred card Details
		displayTransferList(contactsLoyalty);
		//Display's Add Subtract Button
		displayAddSubtractButton(contactsLoyalty);
		
		String contactIds = Constants.STRING_NILL;
		if(contacts !=null)contactIds +=contacts.getContactId();
		if(transferList != null){
			for(ContactsLoyalty eachLoyalty:transferList){
				if(contactIds.length()>0 && eachLoyalty.getContact()!=null){
					contactIds +=","+eachLoyalty.getContact().getContactId();
				}else if(eachLoyalty.getContact()!=null){
					contactIds +=eachLoyalty.getContact().getContactId();
				}

			}
			
		}
		
		//Last Purchase Details
		//Retail Pro Sales details
		/*RetailProSalesCSV  retailProSales = null;
		if(contactIds.length()>0){ 
			retailProSales = ltyPrgmService.getLastPurchaseSalesData(contactIds, contacts.getUsers().getUserId());
			if(retailProSales != null) displayLastPurchase(retailProSales,contactsLoyalty);
		}//if not equal null
		if(contactIds.length()==0 || retailProSales == null) {
			visitOnLblId.setValue(Constants.STRING_NILL);
			storeLblId.setValue(Constants.STRING_NILL);
			purchaseAmountLblId.setValue("$ 0.00");
			issuedAmountLblId.setValue("$ 0.00");
			earningLblId.setValue("0 Points");
			redemptionLblId.setValue(Constants.STRING_NILL);
			itemPurchasedLblId.setValue(Constants.STRING_NILL);
			promotionsAppliedLblId.setValue(Constants.STRING_NILL);
		}*/
		displaybrdrLOut.setHeight((700+transferListLbId.getChildren().size()*30+heightForTrans)+"px");
		LoyaltyCustomerLookUpAndRedeemWinId.setHeight((900+transferListLbId.getChildren().size()*30+heightForTrans)+"px");
		logger.debug("<<<<<<<<<<<<< completed displayLoyaltyDetails ");
	}//displayLoyaltyDetails



	/**
	 * This method enables or disables the add or subtract button based on rewardFlag type. 
	 * @param contactsLoyalty
	 */
	private void displayAddSubtractButton(ContactsLoyalty contactsLoyalty) {
		logger.debug(">>>>>>>>>>>>> entered in displayAddSubtractButton");
		if(contactsLoyalty.getRewardFlag() != null && OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){
			logger.info("For Gift Card Addition & Subtraction of Points are not allowed, hence button's are disabled.");
			viewAddSubtractBtn(true);
		}
		else{
			viewAddSubtractBtn(false);
		}
		logger.debug("<<<<<<<<<<<<< completed displayAddSubtractButton ");
	}//displayAddSubtractButton

	/**
	 * This method disable add an subtract button's
	 */
	private void viewAddSubtractBtn(boolean flag) {
		logger.debug(">>>>>>>>>>>>> entered in disableAddSubBtn");
		addLbId.setDisabled(flag);
		addLbId2.setDisabled(flag);
		subLbId.setDisabled(flag);
		addTbId.setDisabled(flag);
		subTb2Id.setDisabled(flag);
		subtractBtnId.setDisabled(flag);
		logger.debug("<<<<<<<<<<<<< completed disableAddSubBtn ");
	}//disableAddSubBtn
	
	/**
	 * This method display's contact details.
	 * @param contacts
	 * @param cardNumber
	 */
	private void displayContactDetails(Contacts contacts, String membershipNumber) {
		logger.debug(">>>>>>>>>>>>> entered in displayContactDetails");
		//Get Full Name
		String fullName =Constants.STRING_NILL;
		if(contacts != null){
			if(contacts.getLastName() != null && contacts.getFirstName() != null){
				fullName = contacts.getLastName()+", "+contacts.getFirstName();
			}
			else if(contacts.getLastName() == null && contacts.getFirstName() != null){
				fullName = contacts.getFirstName();
			}
			else if(contacts.getLastName() != null && contacts.getFirstName() == null){
				fullName = contacts.getLastName();
			}
			else{
				fullName = Constants.STRING_NILL;
			}
			//Set Mobile Number
			moblieNumberLblId.setValue(contacts.getMobilePhone() == null ? Constants.STRING_NILL:contacts.getMobilePhone());
			//Set Email Address
			emailAddressLblId.setValue(contacts.getEmailId() == null ? Constants.STRING_NILL:contacts.getEmailId());
		}
		//Set Full Name
		nameLblId.setValue(fullName);
		//Set Membership Number
		membershipLblId.setValue(membershipNumber == null ? Constants.STRING_NILL : membershipNumber+Constants.STRING_NILL);
		logger.debug("<<<<<<<<<<<<< completed displayContactDetails ");
	}//displayContactDetails

	/**
	 * This method display's Balance Information
	 * @param contactsLoyalty
	 */
	
	//replacing $ with country currency
	private void displayBalanceSummary(ContactsLoyalty contactsLoyalty) {
		logger.debug(">>>>>>>>>>>>> entered in displayBalanceSummary");
		logger.info("Reward flag type ::"+contactsLoyalty.getRewardFlag());
		
		try {
		if(contactsLoyalty.getRewardFlag() != null && OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){
			setNABalanceSummaryForGiftCard();
			balanceGiftAmountLblId.setValue(contactsLoyalty.getGiftBalance() == null ? userCurrencySymbol + " 0.00":Utility.getAmountInUSFormat(contactsLoyalty.getGiftBalance()  ));
			expiredGiftAmountLblId.setValue(contactsLoyalty.getExpiredGiftAmount() == null ?  userCurrencySymbol +" 0.00":Utility.getAmountInUSFormat(contactsLoyalty.getExpiredGiftAmount() ));
		}
		else if( contactsLoyalty.getRewardFlag() != null && OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){
			balancePointLblId.setValue(contactsLoyalty.getLoyaltyBalance() == null ? "0 Points":contactsLoyalty.getLoyaltyBalance().intValue()+" Points");
			balanceCurrencyLblId.setValue(contactsLoyalty.getGiftcardBalance() == null ?  userCurrencySymbol + " 0.00" : Utility.getAmountInUSFormat(contactsLoyalty.getGiftcardBalance()  ));
			Double redeemableCurr = getTotalRedeemableAmount();
			balanceCurrency2LblId.setValue(Utility.getAmountInUSFormat(redeemableCurr));
			balanceCurrency2LblId.setAttribute("redeemableCurr", redeemableCurr);
			expiredPointsLblId.setValue(contactsLoyalty.getExpiredPoints() == null ? "0 Points":contactsLoyalty.getExpiredPoints().intValue()+" Points");
			expiredAmountLblId.setValue(contactsLoyalty.getExpiredRewardAmount() == null ? userCurrencySymbol + " 0.00" : Utility.getAmountInUSFormat(contactsLoyalty.getExpiredRewardAmount()  ));
			setNABalanceSummaryForLoyaltyCard();
		}
		else if(contactsLoyalty.getRewardFlag() != null && OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){
			balancePointLblId.setValue(contactsLoyalty.getLoyaltyBalance() == null ? "0 Points":contactsLoyalty.getLoyaltyBalance().intValue()+" Points");
			balanceCurrencyLblId.setValue(contactsLoyalty.getGiftcardBalance() == null ?  userCurrencySymbol + " 0.00":Utility.getAmountInUSFormat(contactsLoyalty.getGiftcardBalance()));
			
			//balanceCurrency2LblId.setValue(contactsLoyalty.getGiftcardBalance() == null ?  userCurrencySymbol + " 0.00":Utility.getAmountInUSFormat(contactsLoyalty.getGiftcardBalance()));
			//balanceCurrency2LblId.setValue(Utility.getAmountInUSFormat(getTotalRedeemableAmount()));
			Double redeemableCurr = getTotalRedeemableAmount();
			balanceCurrency2LblId.setValue(Utility.getAmountInUSFormat(redeemableCurr));
			balanceCurrency2LblId.setAttribute("redeemableCurr", redeemableCurr);
			expiredPointsLblId.setValue(contactsLoyalty.getExpiredPoints() == null ? "0 Points":contactsLoyalty.getExpiredPoints().intValue()+" Points");
			expiredAmountLblId.setValue(contactsLoyalty.getExpiredRewardAmount() == null ? userCurrencySymbol + " 0.00":Utility.getAmountInUSFormat(contactsLoyalty.getExpiredRewardAmount()  ));
			balanceGiftAmountLblId.setValue(contactsLoyalty.getGiftBalance() == null ?  userCurrencySymbol + " 0.00":Utility.getAmountInUSFormat(contactsLoyalty.getGiftBalance()  ));
			expiredGiftAmountLblId.setValue(contactsLoyalty.getExpiredGiftAmount() == null ?  userCurrencySymbol + " 0.00":Utility.getAmountInUSFormat(contactsLoyalty.getExpiredGiftAmount() ));
		}
		
		//Hold balance
		if(contactsLoyalty.getHoldAmountBalance() != null && !contactsLoyalty.getHoldAmountBalance().toString().isEmpty() && contactsLoyalty.getHoldAmountBalance() != 0.0
		   && contactsLoyalty.getHoldPointsBalance() != null &&  !contactsLoyalty.getHoldPointsBalance().toString().isEmpty() && contactsLoyalty.getHoldPointsBalance() != 0.0) {
			holdBalLbId.setVisible(true);
			holdBalValLbId.setVisible(true);
			holdBalValLbId.setValue(Utility.getAmountInUSFormat(contactsLoyalty.getHoldAmountBalance())+" & "+
					contactsLoyalty.getHoldPointsBalance().intValue()+" Points");
		}else if(contactsLoyalty.getHoldAmountBalance() != null && !contactsLoyalty.getHoldAmountBalance().toString().isEmpty() && contactsLoyalty.getHoldAmountBalance() != 0.0 
				&& (contactsLoyalty.getHoldPointsBalance() == null || contactsLoyalty.getHoldPointsBalance().toString().isEmpty() || contactsLoyalty.getHoldPointsBalance() == 0.0)) {
			holdBalLbId.setVisible(true);
			holdBalValLbId.setVisible(true);
			holdBalValLbId.setValue(Utility.getAmountInUSFormat(contactsLoyalty.getHoldAmountBalance()));
		}else if( contactsLoyalty.getHoldPointsBalance() != null &&  !contactsLoyalty.getHoldPointsBalance().toString().isEmpty() && contactsLoyalty.getHoldPointsBalance() != 0.0
				 && ( contactsLoyalty.getHoldAmountBalance() == null || contactsLoyalty.getHoldAmountBalance().toString().isEmpty() || contactsLoyalty.getHoldAmountBalance() == 0.0)) {
			holdBalLbId.setVisible(true);
			holdBalValLbId.setVisible(true);
			holdBalValLbId.setValue(contactsLoyalty.getHoldPointsBalance().intValue()+" Points");
		}
		else if(contactsLoyalty.getProgramTierId() != null) {
			LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
			LoyaltyProgramTier loyaltyProgramTier = loyaltyProgramTierDao.findByTierId(contactsLoyalty.getProgramTierId());
			if(loyaltyProgramTier.getActivationFlag() == OCConstants.FLAG_YES) {
				holdBalLbId.setVisible(true);
				holdBalValLbId.setVisible(true);
			}
		}
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
		logger.debug("<<<<<<<<<<<<< completed displayBalanceSummary ");
	}//displayBalanceSummary


	/**
	 * This method display's Last Purchase details
	 * @param retailProSalesList
	 * @param contactsLoyalty
	 * @param contacts
	 */
	private void displayLastPurchase(RetailProSalesCSV retailProSales , ContactsLoyalty contactsLoyalty) {/*
		logger.debug(">>>>>>>>>>>>> entered in displayLastPurchase");
		LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
		OrganizationStores organizationStores = null;
		LoyaltyTransactionChild ltyTransactionChild = null;
		long numOfItems = 0L;
		String lastSalesDate = Constants.STRING_NILL;

		String lastPurchaseDocSid = retailProSales.getDocSid();

		if(retailProSales.getSalesDate() != null) {
			lastSalesDate = MyCalendar.calendarToString(retailProSales.getSalesDate(), MyCalendar.FORMAT_DATETIME_STDATE);
		}

		//Last Purchase store
		
		//Visit on
		visitOnLblId.setValue(lastSalesDate);
		//Store Name
		storeLblId.setValue(retailProSales.getStoreNumber() != null ? fetchStoreName(retailProSales.getStoreNumber()): "--");

		//Purchased Amount
		Object[] lastPurQtyAndAmt = ltyPrgmService.getLastPurchaseQtyAndAmtByDocSid(lastPurchaseDocSid, currUser.getUserId());
		if(lastPurQtyAndAmt != null){
			purchaseAmountLblId.setValue(lastPurQtyAndAmt[1] == null ? "$ 0.00" : Utility.getAmountInUSFormat(Double.valueOf(lastPurQtyAndAmt[1].toString())));
			numOfItems = lastPurQtyAndAmt[0] == null ? 0 : Long.valueOf(lastPurQtyAndAmt[0].toString());
		}
		else{
			purchaseAmountLblId.setValue("$ 0.00");
			numOfItems = 0;
		}

		List<CouponCodes> couponCodeList = ltyPrgmService.getCouponCodeByDocSid(lastPurchaseDocSid, currUser.getUserOrganization().getUserOrgId());
		ltyTransactionChild = ltyPrgmService.getLtyTransByIssuanceAndDocSid(lastPurchaseDocSid, currUser.getUserId());
		//Issued Amount
		if( contactsLoyalty.getRewardFlag()!= null && OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){
			issuedAmountLblId.setValue(Constants.NOT_APPLICABLE);
		}
		else{
			if (ltyTransactionChild != null ) {
				//Entered Amount- Excluded Amount
				Double enteredAmt = ltyTransactionChild.getEnteredAmount() == null ? 0.0 : ltyTransactionChild.getEnteredAmount();
				Double excludeAmt = ltyTransactionChild.getExcludedAmount() == null ? 0.0 : ltyTransactionChild.getExcludedAmount();
				issuedAmountLblId.setValue(Utility.getAmountInUSFormat((enteredAmt - excludeAmt)));
			}
			else {
				//logger.info("Issued Amount Null");
				issuedAmountLblId.setValue("$ 0.00");
			}
		}
		//Earnings
		if (ltyTransactionChild != null) {
			if(ltyTransactionChild.getEarnedPoints() != null){
				earningLblId.setValue(ltyTransactionChild.getEarnedPoints().intValue()+ " Points");
			}
			else if(ltyTransactionChild.getEarnedAmount() != null){
				earningLblId.setValue(Utility.getAmountInUSFormat(ltyTransactionChild.getEarnedAmount()));
			}
			else{
				earningLblId.setValue(Constants.STRING_NILL);
			}
		} else {
			//			logger.info("Earnings Null");
			earningLblId.setValue(Constants.STRING_NILL);
		}
		Object[] redemptionList = ltyPrgmService.findRedemptionByDocSid(lastPurchaseDocSid, currUser.getUserId().longValue());
		//Redemption
		String pointsRedeemed = Constants.STRING_NILL;
		String amountRedeemed = Constants.STRING_NILL;
		if(redemptionList != null){
			if(redemptionList[0] != null && !redemptionList[0].toString().isEmpty()){
				pointsRedeemed = redemptionList[0].toString().replace("-", "");
			}
			if(redemptionList[1] != null && !redemptionList[1].toString().isEmpty() &&
					(redemptionList[2] == null || redemptionList[2].toString().isEmpty())){
				amountRedeemed = redemptionList[1].toString().replace("-", "");
			}
			else if(redemptionList[2] != null && !redemptionList[2].toString().isEmpty() &&
					(redemptionList[1] == null || redemptionList[1].toString().isEmpty())){
				amountRedeemed = redemptionList[2].toString().replace("-", "");
			}
			else if(redemptionList[1] != null && !redemptionList[1].toString().isEmpty() &&
					redemptionList[2] != null && !redemptionList[2].toString().isEmpty()){
				Double amtRed = Double.valueOf(redemptionList[1].toString()) + Double.valueOf(redemptionList[2].toString());
				amountRedeemed = amtRed.toString().replace("-", "");
			}
		}
		if(amountRedeemed.isEmpty() && !pointsRedeemed.isEmpty()){
			redemptionLblId.setValue(pointsRedeemed+" Points");
		}
		else if(!amountRedeemed.isEmpty() && pointsRedeemed.isEmpty()){
			redemptionLblId.setValue(Utility.getAmountInUSFormat(Double.valueOf(amountRedeemed)));
		}
		else if(!amountRedeemed.isEmpty() && !pointsRedeemed.isEmpty()){
			redemptionLblId.setValue(Utility.getAmountInUSFormat(Double.valueOf(amountRedeemed)) + " & " + pointsRedeemed + " Points");
		}
		else{
			redemptionLblId.setValue(Constants.STRING_NILL);
		}
		
		if (ltyTransactionChildList != null) {
			Double redemptionAmt = 0.0, redemptionPoints = 0.0;
			for (LoyaltyTransactionChild loyaltyTransactionChild : ltyTransactionChildList) {
				redemptionAmt = redemptionAmt + (loyaltyTransactionChild.getEnteredAmount() == null ? 0.0 : loyaltyTransactionChild.getEnteredAmount());
				redemptionPoints = redemptionPoints
						+ (loyaltyTransactionChild.getEarnedPoints() == null ? 0.0
								: loyaltyTransactionChild.getEarnedPoints());
			}
			redemptionLblId.setValue( Utility.getAmountInUSFormat(redemptionAmt) + " &  "+ redemptionPoints + " Points");
		} else {
			//			logger.info("Redemption Null");
			redemptionLblId.setValue(Constants.STRING_NILL);
		}
		//Items Purchased
		if (numOfItems > 0) {
			logger.info("Items purchased ::"+numOfItems);
			itemPurchasedLblId.setValue(numOfItems + Constants.STRING_NILL);
			skuDetailsDivId.setVisible(true);
			whichItemsPurchaseAnchId.setAttribute("docSid", lastPurchaseDocSid);
		} else {
			itemPurchasedLblId.setValue("0");
			logger.info("Items purchased.. ::"+numOfItems);
		}
		//promotionsAppliedLblId;
		if (couponCodeList != null && couponCodeList.size() >0) {
			promotionsAppliedLblId.setValue(couponCodeList.size() + Constants.STRING_NILL);
			promotionDetailsDivId.setVisible(true);
			whichItemsPromotionAnchId.setAttribute("docSid", lastPurchaseDocSid);
			logger.info("Promotions applied ::"+couponCodeList.size());

		} else {
			promotionsAppliedLblId.setValue(Constants.STRING_NILL);
		}
		logger.debug("<<<<<<<<<<<<< completed displayLastPurchase ");
	*/}///displaySalesSummary

	
	/**
	 * This method display's Membership Summary
	 * @param contactsLoyalty
	 * @param contacts
	 */
	private void displayMembershipSummary(ContactsLoyalty contactsLoyalty,Contacts contacts) {
		
		try {
		logger.debug(">>>>>>>>>>>>> entered in displayMembershipSummary");
		LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
		LoyaltyProgram loyaltyProgram =null;
		LoyaltyCardSet loyaltyCardSet =null;
		LoyaltyProgramTier loyaltyProgramTier = null;

		if(contactsLoyalty.getProgramId() != null){
			loyaltyProgram = ltyPrgmService.getProgmObj(contactsLoyalty.getProgramId());
		}
		if(contactsLoyalty.getCardSetId() != null){
			loyaltyCardSet = ltyPrgmService.getLoyaltyCardSetObj(contactsLoyalty.getCardSetId());
		}

		if(contactsLoyalty.getProgramTierId() !=null){
			loyaltyProgramTier = ltyPrgmService.getTierObj(contactsLoyalty.getProgramTierId().longValue());
			tierNameMSummaryLblId.setAttribute("tier", loyaltyProgramTier);
		}
		
		membershipMSummaryLblId.setValue(contactsLoyalty.getCardNumber() == null ? Constants.STRING_NILL : contactsLoyalty.getCardNumber()+Constants.STRING_NILL);
		if(loyaltyProgram != null ){
			//whichProgramMSummaryLblId.setTooltip("Program Name :"+loyaltyProgram.getProgramName() == null ?Constants.STRING_NILL: loyaltyProgram.getProgramName()+"\n Card-Set Name :"+ loyaltyCardSet.getCardSetName() == null ? Constants.STRING_NILL:loyaltyCardSet.getCardSetName());
			programNameLbId.setValue(loyaltyProgram.getProgramName() == null ?Constants.STRING_NILL: loyaltyProgram.getProgramName());
			programNameLbId.setAttribute("program", loyaltyProgram);
			if(loyaltyCardSet != null){
				cardSetNameLbId.setValue(loyaltyCardSet.getCardSetName() == null ? Constants.STRING_NILL:loyaltyCardSet.getCardSetName());
			}
		}
		else{
			programNameLbId.setValue(Constants.STRING_NILL);
			cardSetNameLbId.setValue(Constants.STRING_NILL);
		}
		membershipStatusMSummaryLblId.setValue(contactsLoyalty.getMembershipStatus() == null ? Constants.STRING_NILL : contactsLoyalty.getMembershipStatus());
		whichprogramDivId.setVisible(true);
		optInDateMSummaryLblId.setValue(contactsLoyalty.getCreatedDate() == null ? "--" : MyCalendar.calendarToString(contactsLoyalty.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR,clientTimeZone));//APP - 95
		String optInMedium = Constants.STRING_NILL;
		if(contactsLoyalty.getContactLoyaltyType() != null){
			if(contactsLoyalty.getContactLoyaltyType().equalsIgnoreCase(Constants.CONTACT_LOYALTY_TYPE_POS)) {
				optInMedium = Constants.CONTACT_LOYALTY_TYPE_STORE;
			}
			else {
				optInMedium = contactsLoyalty.getContactLoyaltyType();
			}
		}
		optInMedium=optInMedium.replace("_", " ");//APP-1774
		sourceMSummaryLblId.setValue(optInMedium);
		subsidiaryNumberMSummaryLblId.setValue(contactsLoyalty.getSubsidiaryNumber() == null ? Constants.STRING_NILL : fetchSubsidiaryName(contactsLoyalty.getSubsidiaryNumber()));
		storeNumberMSummaryLblId.setValue(contactsLoyalty.getPosStoreLocationId() == null ? Constants.STRING_NILL : fetchStoreName(contactsLoyalty.getPosStoreLocationId()));
		Double lifeTimeLoyaltyPurchaseValue = 0.00;
		LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
		//lifeTimeLoyaltyPurchaseValue = loyaltyTransactionChildDao.getLifeTimeLoyaltyPurchaseValue(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId());
		//lifeTimeLoyaltyPurchaseValue = contactsLoyalty.getLifeTimePurchaseValue() == null ? 0.0 : contactsLoyalty.getLifeTimePurchaseValue();//loyaltyTransactionChildDao.getLifeTimeLoyaltyPurchaseValue(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId());//contactsLoyalty.getLifeTimePurchaseValue();// loyaltyTransactionChildDao.getLifeTimeLoyaltyPurchaseValue(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId());
		lifeTimeLoyaltyPurchaseValue = LoyaltyProgramHelper.getLPV(contactsLoyalty) == null ? 0.0 : LoyaltyProgramHelper.getLPV(contactsLoyalty);//loyaltyTransactionChildDao.getLifeTimeLoyaltyPurchaseValue(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId());//contactsLoyalty.getLifeTimePurchaseValue();// loyaltyTransactionChildDao.getLifeTimeLoyaltyPurchaseValue(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId());
		
		 
		logger.info("purchase value = "+lifeTimeLoyaltyPurchaseValue);
		//Changes 2.5.5.0
		if(!contactsLoyalty.getRewardFlag().equals(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G)) {
		lifeTimePurchaseValueMSummaryLblId.setValue(Utility.getAmountInUSFormat(Double.valueOf(lifeTimeLoyaltyPurchaseValue)));
		}
		//lifeTimePurchaseValueMSummaryLblId.setValue(Utility.getAmountInUSFormat(Double.valueOf(lifeTimePurchaseValue)));
		
		//For Gift Card
		if(contactsLoyalty.getRewardFlag() != null && OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyaltyObj.getRewardFlag())){
			tierNameMSummaryLblId.setValue(Constants.NOT_APPLICABLE);
		}
		else{
			String tier = Constants.STRING_NILL ,level =Constants.STRING_NILL;
			if(loyaltyProgram.getTierEnableFlag() == OCConstants.FLAG_YES && loyaltyProgramTier != null){
				tier = loyaltyProgramTier.getTierName() ;
				level = " ( Level : "+(loyaltyProgramTier.getTierType() == null ? Constants.STRING_NILL : loyaltyProgramTier.getTierType())+" )";
				tierNameMSummaryLblId.setValue(tier+level);
			}
			else{
				tierNameMSummaryLblId.setValue(tier);
			}
		}
		//Expiration Date
		if(loyaltyProgram != null && contactsLoyaltyObj.getRewardFlag() != null){

			if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equalsIgnoreCase(contactsLoyalty.getRewardFlag()) || OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){

				if(loyaltyProgramTier != null && loyaltyProgram.getMembershipExpiryFlag() == 'Y' && loyaltyProgramTier.getMembershipExpiryDateType() != null 
						&& loyaltyProgramTier.getMembershipExpiryDateValue() != null){

					boolean upgdReset = loyaltyProgram.getMbrshipExpiryOnLevelUpgdFlag() == 'Y' ? true : false;
					
					expirationDateMSummaryLblId.setValue(LoyaltyProgramHelper.getMbrshipExpiryDate(contactsLoyalty.getCreatedDate(), contactsLoyalty.getTierUpgradedDate(), 
							upgdReset, loyaltyProgramTier.getMembershipExpiryDateType(), loyaltyProgramTier.getMembershipExpiryDateValue()));
				}//if
				else {
					expirationDateMSummaryLblId.setValue("");
				}
			}//if
			else if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyaltyObj.getRewardFlag())){

				if(loyaltyProgram.getGiftMembrshpExpiryFlag() == 'Y'){
					expirationDateMSummaryLblId.setValue(LoyaltyProgramHelper.getGiftMbrshipExpiryDate(contactsLoyalty.getCreatedDate(), 
							loyaltyProgram.getGiftMembrshpExpiryDateType(), loyaltyProgram.getGiftMembrshpExpiryDateValue()));
				}//if

			}//else if
			else{
				expirationDateMSummaryLblId.setValue("");
			}
		}//if
		else{
			logger.error("Expiration date is not set loyaltyProgram or loyaltyProgramTier or contactsLoyalty is null");
			expirationDateMSummaryLblId.setValue("--");
		}
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
		logger.debug("<<<<<<<<<<<<< completed displayMembershipSummary ");
	}//displayMembershipSummary


	/**
	 * This method display's Transaction Summary
	 * @param contactsLoyalty
	 */
	//replacing doller with country currency symbol
	private void displayTransactionSummary(ContactsLoyalty contactsLoyalty) {
		logger.debug(">>>>>>>>>>>>> entered in displayTransactionSummary");
		LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
		long countOfIssuance  = ltyPrgmService.getAllCountOfIssuance(currUser.getUserId().longValue(),contactsLoyalty.getLoyaltyId().longValue(),OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE);
		long countOfRedemption = ltyPrgmService.getAllCountOfRedemption(currUser.getUserId().longValue(),contactsLoyalty.getLoyaltyId().longValue(),OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION);

		if(contactsLoyalty.getRewardFlag() != null && OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){
			//Set fields not applicable for gift card
			setNATransactionSummaryForGiftCard();
			//Gift Card
			totalGiftTopupAmountTSummaryLblId.setValue(contactsLoyalty.getTotalGiftAmount() == null ? userCurrencySymbol + " 0.00" : Utility.getAmountInUSFormat(contactsLoyalty.getTotalGiftAmount() ));
			giftAmountRedeemedTSummaryLblId.setValue(contactsLoyalty.getTotalGiftRedemption() == null ?  userCurrencySymbol +" 0.00" : Utility.getAmountInUSFormat(contactsLoyalty.getTotalGiftRedemption() ));
		}
		else if(contactsLoyalty.getRewardFlag() != null && OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){
			
			totalIssuanceTSummaryLblId.setValue(countOfIssuance+Constants.STRING_NILL);
			totalRedemptionsTSummaryLblId.setValue(countOfRedemption+Constants.STRING_NILL);
			lifetimePointsTSummaryLblId.setValue(contactsLoyalty.getTotalLoyaltyEarned() == null ? "0 Points" :  contactsLoyalty.getTotalLoyaltyEarned().intValue()+" Points");
			pointRedeemedTSummaryLblId.setValue(contactsLoyalty.getTotalLoyaltyRedemption() == null ? "0 Points" : contactsLoyalty.getTotalLoyaltyRedemption().intValue()+" Points");
			totalAmountEarnedTSummaryLblId.setValue(contactsLoyalty.getTotalGiftcardAmount() == null ?  userCurrencySymbol +" 0.00" :Utility.getAmountInUSFormat(contactsLoyalty.getTotalGiftcardAmount() ));
			amountRedeemedTSummaryLblId.setValue(contactsLoyalty.getTotalGiftcardRedemption() == null ?  userCurrencySymbol +" 0.00" : Utility.getAmountInUSFormat(contactsLoyalty.getTotalGiftcardRedemption() ));

			//set fields not applicable for loyalty card
			setNATransactionSummaryForLoyaltyCard();

		}
		else if(contactsLoyalty.getRewardFlag() != null && OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){
			totalIssuanceTSummaryLblId.setValue(countOfIssuance+Constants.STRING_NILL);
			totalRedemptionsTSummaryLblId.setValue(countOfRedemption+Constants.STRING_NILL);
			lifetimePointsTSummaryLblId.setValue(contactsLoyalty.getTotalLoyaltyEarned() == null ? "0 Points" :  contactsLoyalty.getTotalLoyaltyEarned().intValue()+" Points");
			pointRedeemedTSummaryLblId.setValue(contactsLoyalty.getTotalLoyaltyRedemption() == null ? "0 Points" : contactsLoyalty.getTotalLoyaltyRedemption().intValue()+" Points");
			totalAmountEarnedTSummaryLblId.setValue(contactsLoyalty.getTotalGiftcardAmount() == null ?  userCurrencySymbol + " 0.00" :Utility.getAmountInUSFormat(contactsLoyalty.getTotalGiftcardAmount() ));
			amountRedeemedTSummaryLblId.setValue(contactsLoyalty.getTotalGiftcardRedemption() == null ?  userCurrencySymbol + " 0.00" : Utility.getAmountInUSFormat(contactsLoyalty.getTotalGiftcardRedemption() ));

			//Gift Card
			totalGiftTopupAmountTSummaryLblId.setValue(contactsLoyalty.getTotalGiftAmount() == null ?  userCurrencySymbol + " 0.00" : Utility.getAmountInUSFormat(contactsLoyalty.getTotalGiftAmount() ));
			giftAmountRedeemedTSummaryLblId.setValue(contactsLoyalty.getTotalGiftRedemption() == null ?  userCurrencySymbol +" 0.00" : Utility.getAmountInUSFormat(contactsLoyalty.getTotalGiftRedemption() ));
		}
		else{
			setNATransactionSummaryForGiftCard();
			setNATransactionSummaryForLoyaltyCard();
		}
		logger.debug("<<<<<<<<<<<<< completed displayTransactionSummary ");
	}//displayTransactionSummary


	/**
	 * This method display's final Balances
	 * @param contactsLoyalty
	 */
	//replace $ with country currency
	private void displayFinalBalances(ContactsLoyalty contactsLoyalty) {
		logger.debug(">>>>>>>>>>>>> entered in displayFinalBalances");
		//Final BALANCE REWARDS
		finalBalanceRewardsLblId.setValue(contactsLoyalty.getGiftcardBalance() == null ?  userCurrencySymbol + " 0.00":Utility.getAmountInUSFormat(contactsLoyalty.getGiftcardBalance()));
		//Final BALANCE POINTS
		finalBalancePointsLblId.setValue(contactsLoyalty.getLoyaltyBalance() == null ? "0 Points":  contactsLoyalty.getLoyaltyBalance().intValue()+" Points");
		//Final Hold Currency Balance
		displayFinalHoldBalances(contactsLoyalty);
		logger.debug("<<<<<<<<<<<<< completed displayFinalBalances");		
	}//displayFinalBalances

	/**
	 * This method decides the display label for Suspend button
	 * @param contactsLoyalty
	 */
	private void displaySuspendButton(ContactsLoyalty contactsLoyalty) {
		logger.debug(">>>>>>>>>>>>> entered in displaySuspendButton");
		//Suspend/Activate Button
		if(contactsLoyalty.getMembershipStatus() != null){
			if(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED.equalsIgnoreCase((contactsLoyalty.getMembershipStatus()))){
				suspendMembershipBtnId.setLabel("Activate");
			}
			else if(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE.equalsIgnoreCase((contactsLoyalty.getMembershipStatus()))){
				suspendMembershipBtnId.setLabel("Suspend");
			}
		}//if
		logger.debug("<<<<<<<<<<<<< completed displaySuspendButton");
	}//displaySuspendButton

	/**
	 * This method help's to diplay pagination details
	 * @param contactsLoyalty
	 */

	private void displayPagination(ContactsLoyalty contactsLoyalty) {
		logger.debug(">>>>>>>>>>>>> entered in displayPagination");
		LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService(); 
		String transferLoyaltyId = ""+contactsLoyaltyObj.getLoyaltyId();
				
		int totalSize;
		//transactionChildList = ltyPrgmService.getAllTransactionsByCardNumber(contactsLoyalty.getCardNumber(),currUser.getUserId());
		if(transferList != null){
			for(ContactsLoyalty eachTranfer:transferList){
				transferLoyaltyId += ","+eachTranfer.getLoyaltyId();
			}
			totalSize = ltyPrgmService.getMaxTransactionsByMembershipnumberCount(currUser.getUserId(), transferLoyaltyId, MyCalendar.calendarToString(startDate, null), MyCalendar.calendarToString(endDate, null));
			logger.info("Max Size .........."+totalSize);
		}
		else{
			totalSize = ltyPrgmService.getAllTransactionsByMembershipnumberCount(currUser.getUserId(), contactsLoyaltyObj.getLoyaltyId(), MyCalendar.calendarToString(startDate, null), MyCalendar.calendarToString(endDate, null));
			logger.info("Total Size .........."+totalSize);

		}
		loyaltyListBottomPagingId.setTotalSize(totalSize);
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		loyaltyListBottomPagingId.setPageSize(pageSize);
		loyaltyListBottomPagingId.setActivePage(0);
		loyaltyListBottomPagingId.setDetailed(true);
		loyaltyListBottomPagingId.addEventListener("onPaging", this);


		//			logger.info("Redrawing the List ..........:: startDate "+startDate+ "\t endDate "+endDate);
		drawTransList();
		displaybrdrLOut.setHeight((700+transferListLbId.getChildren().size()*30+heightForTrans)+"px");
		LoyaltyCustomerLookUpAndRedeemWinId.setHeight((900+transferListLbId.getChildren().size()*30+heightForTrans)+"px");
		logger.debug("<<<<<<<<<<<<< completed displayPagination ");
	}//displayPagination

	private void drawTransList(){
		LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService(); 
		List<LoyaltyTransactionChild> transactionChildList  = ltyPrgmService.getAllTransactionsByMembershipnumber(currUser.getUserId(), contactsLoyaltyObj.getLoyaltyId(), MyCalendar.calendarToString(startDate, null), MyCalendar.calendarToString(endDate, null),
				loyaltyListBottomPagingId.getActivePage()*loyaltyListBottomPagingId.getPageSize(), loyaltyListBottomPagingId.getPageSize());
		heightForTrans = 0;
		int count =  transListLbId.getItemCount();
		for(; count>0; count--) {
			transListLbId.removeItemAt(count-1);
		}
		//System.gc();
		redrawTransList(transactionChildList, false);
		if(transferList != null){
			for(ContactsLoyalty childContactsLoyalty:transferList){
				transactionChildList  = ltyPrgmService.getAllTransactionsByMembershipnumber(currUser.getUserId(), childContactsLoyalty.getLoyaltyId(), MyCalendar.calendarToString(startDate, null), MyCalendar.calendarToString(endDate, null),
						loyaltyListBottomPagingId.getActivePage()*loyaltyListBottomPagingId.getPageSize(), loyaltyListBottomPagingId.getPageSize());
				redrawTransList(transactionChildList, true);
			}
		}
	}
	/**
	 * Fields N/A for loyalty card
	 */
	private void setNABalanceSummaryForLoyaltyCard() {
		logger.debug(">>>>>>>>>>>>> entered in displayBalanceSummaryNAForLoyaltyCard");
		balanceGiftAmountLblId.setValue(Constants.NOT_APPLICABLE);
		expiredGiftAmountLblId.setValue(Constants.NOT_APPLICABLE);
		logger.debug("<<<<<<<<<<<<< completed displayBalanceSummaryNAForLoyaltyCard ");

	}//displayBalanceSummaryNAForLoyaltyCard
	/**
	 * Fields N/A for gift card.
	 */
	private void setNABalanceSummaryForGiftCard() {
		logger.debug(">>>>>>>>>>>>> entered in displayBalanceSummaryNAForGiftCard");
		balancePointLblId.setValue(Constants.NOT_APPLICABLE);
		balanceCurrencyLblId.setValue(Constants.NOT_APPLICABLE);
		
		balanceCurrency2LblId.setValue(Constants.NOT_APPLICABLE);
		balanceCurrency2LblId.removeAttribute("redeemableCurr");//, redeemableCurr);
		expiredPointsLblId.setValue(Constants.NOT_APPLICABLE);
		expiredAmountLblId.setValue(Constants.NOT_APPLICABLE);
		logger.debug("<<<<<<<<<<<<< completed displayBalanceSummaryNAForGiftCard ");
	}//displayBalanceSummaryNAForGiftCard

	/**
	 *  Fields N/A for loyalty card
	 */
	private void setNATransactionSummaryForLoyaltyCard() {
		logger.debug(">>>>>>>>>>>>> entered in setNATransactionSummaryForLoyaltyCard");
		totalGiftTopupAmountTSummaryLblId.setValue(Constants.NOT_APPLICABLE);
		giftAmountRedeemedTSummaryLblId.setValue(Constants.NOT_APPLICABLE);
		logger.debug("<<<<<<<<<<<<< completed setNATransactionSummaryForLoyaltyCard ");
	}//setNATransactionSummaryForLoyaltyCard

	/**
	 *  Fields N/A for gift card
	 */
	private void setNATransactionSummaryForGiftCard() {
		logger.debug(">>>>>>>>>>>>> entered in setNATransactionSummaryForGiftCard");
		totalIssuanceTSummaryLblId.setValue(Constants.NOT_APPLICABLE);
		totalRedemptionsTSummaryLblId.setValue(Constants.NOT_APPLICABLE);
		lifetimePointsTSummaryLblId.setValue(Constants.NOT_APPLICABLE);
		pointRedeemedTSummaryLblId.setValue(Constants.NOT_APPLICABLE);
		totalAmountEarnedTSummaryLblId.setValue(Constants.NOT_APPLICABLE);
		amountRedeemedTSummaryLblId.setValue(Constants.NOT_APPLICABLE);
		logger.debug("<<<<<<<<<<<<< completed setNATransactionSummaryForGiftCard ");
	}//setNATransactionSummaryForGiftCard

	/**
	 * This method display's updated points & balances
	 * @param contactsLoyalty
	 */
	//replacing $ with country currency
	private void updateLoyaltyData(ContactsLoyalty contactsLoyalty) {
		try {
			logger.debug(">>>>>>>>>>>>> entered in updateLoyaltyData");
			LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
			ContactsLoyalty ltyObj = ltyPrgmService.getContactLtyById(contactsLoyalty.getLoyaltyId());
			//balance points
			balancePointLblId.setValue(ltyObj.getLoyaltyBalance() == null ? "0 Points":ltyObj.getLoyaltyBalance().intValue()+" Points");
			//balance rewards	
			balanceCurrencyLblId.setValue(ltyObj.getGiftcardBalance() == null ? userCurrencySymbol + " 0.00":Utility.getAmountInUSFormat(ltyObj.getGiftcardBalance() ));
			
			//redeemtion 
			//balanceCurrency2LblId.setValue(ltyObj.getGiftcardBalance() == null ? userCurrencySymbol + " 0.00":Utility.getAmountInUSFormat(ltyObj.getGiftcardBalance() ));;
			Double redeemableCurr = getTotalRedeemableAmount();
			balanceCurrency2LblId.setValue(Utility.getAmountInUSFormat(redeemableCurr));
			//balanceCurrency2LblId.setValue(Utility.getAmountInUSFormat(getTotalRedeemableAmount()));
			balanceCurrency2LblId.setAttribute("redeemableCurr", redeemableCurr);
			//total amount earned	
			totalAmountEarnedTSummaryLblId.setValue(ltyObj.getTotalGiftcardAmount() == null ? userCurrencySymbol + " 0.00" :Utility.getAmountInUSFormat(ltyObj.getTotalGiftcardAmount() ));

		//BALANCE REWARDS
		finalBalanceRewardsLblId.setValue(ltyObj.getGiftcardBalance() == null ? userCurrencySymbol + " 0.00":Utility.getAmountInUSFormat(ltyObj.getGiftcardBalance()));
		//BALANCE POINTS
		finalBalancePointsLblId.setValue(ltyObj.getLoyaltyBalance() == null ? "0 Points":  ltyObj.getLoyaltyBalance().intValue()+" Points");
		//Update TransactionSummary
		displayTransactionSummary(contactsLoyalty);		
		logger.debug("<<<<<<<<<<<<< completed updateLoyaltyData ");
		return;
		}catch (Exception e) {
			logger.info("error in updateLoyaltyData :"+e);
		}
	}
		//updateLoyaltyData


	/**
	 * This method helps to suspend or activate membership status
	 */
	public void onClick$suspendMembershipBtnId(){
		logger.debug(">>>>>>>>>>>>> entered in onClick$suspendMembershipBtnId");
			if(isPasswordRequired!=null && isPasswordRequired) {
				LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
				LoyaltyThresholdAlerts loyaltyThresholdAlerts =ltyPrgmSevice.findPwdByUserID(currUser.getUserId());
				try {
					String encryptedPwd = EncryptDecryptLtyMembshpPwd.decryptProgramPwd(loyaltyThresholdAlerts.getLtySecurityPwd());
					ltyPwdTbSuspendId.setValue("");
					ltyPwdTbSuspendId.setValue(encryptedPwd.trim());
					onClick$submitPwdBtnSuspendId();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
			ltyPwdPopupSuspendId.open(ltyPwdPopupSuspendId);
		}
	}
	public void onClick$submitPwdBtnSuspendId() {
	try {
		String display = Constants.STRING_NILL;
		String pwdStr = ltyPwdTbSuspendId.getValue().trim();

		if(pwdStr == null || pwdStr.trim().length() ==0) {
			MessageUtil.setMessage( "Please provide password.", "color:red;", "TOP");
			ltyPwdTbSuspendId.setText("");
			return;
		}
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		LoyaltyThresholdAlerts loyaltyThresholdAlerts = ltyPrgmSevice.findPwdByUserID(currUser.getUserId());
		if(loyaltyThresholdAlerts != null && EncryptDecryptLtyMembshpPwd.decryptProgramPwd(loyaltyThresholdAlerts.getLtySecurityPwd()).equals(pwdStr)) {
	
		LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
		//		logger.info("onClick of suspend membership status.");
		String btnLabel = suspendMembershipBtnId.getLabel();
		if(contactsLoyaltyObj == null){
			MessageUtil.setMessage("Please look-up membership before performing this action.", "red");
			ltyPwdTbSuspendId.setText("");
			return;
		}
		else{
			if(contactsLoyaltyObj.getMembershipStatus() != null && contactsLoyaltyObj.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED)){
				MessageUtil.setMessage("The membership has expired and cannot be suspended / activated.", "red");
				ltyPwdTbSuspendId.setText("");
				return;
			}
			
			boolean flag = false;
			if(Constants.BUTTON_LABEL_SUSPEND.equalsIgnoreCase(btnLabel)){
				if(Messagebox.show("Do you want to suspend membership of the contact ?", "Confirm" ,Messagebox.OK | Messagebox.CANCEL, 
						Messagebox.QUESTION) == Messagebox.OK) {
					contactsLoyaltyObj.setMembershipStatus(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED);
					flag = ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
					if(flag){
						membershipStatusMSummaryLblId.setValue(Constants.STRING_NILL);
						ltyPwdTbSuspendId.setText("");
						membershipStatusMSummaryLblId.setValue(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED);
					}
					display = "suspended";
				}
			}
			else if(Constants.BUTTON_LABEL_ACTIVATE.equalsIgnoreCase(btnLabel)){
				if(Messagebox.show("Do you want to activate membership of the contact ?", "Confirm" ,Messagebox.OK | Messagebox.CANCEL, 
						Messagebox.QUESTION) == Messagebox.OK) {
					contactsLoyaltyObj.setMembershipStatus(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE);
					flag = ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
					if(flag){
						membershipStatusMSummaryLblId.setValue(Constants.STRING_NILL);
						ltyPwdTbSuspendId.setText("");
						membershipStatusMSummaryLblId.setValue(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE);
					}
					display = "activated";
				} 
			}

			if(flag == true){
				MessageUtil.setMessage("Membership "+display+" successfully.", "blue");

				if(display.equalsIgnoreCase("suspended")){
					suspendMembershipBtnId.setLabel(Constants.BUTTON_LABEL_ACTIVATE);
					ltyPwdTbSuspendId.setText("");
				}
				else{
					suspendMembershipBtnId.setLabel(Constants.BUTTON_LABEL_SUSPEND);
					ltyPwdTbSuspendId.setText("");
				}
			}
			else{
				logger.error("Unable to update the membership status");
				ltyPwdTbSuspendId.setText("");
			}
		}
		logger.debug("<<<<<<<<<<<<< completed onClick$suspendMembershipBtnId ");
	}
		else {
			   MessageUtil.setMessage("Please enter the correct password", "color:red", "TOP");
			   return;
			  }
		LoyaltyProgram loyaltyProgram = loyaltyProgramDao.findById(contactsLoyaltyObj.getProgramId());
		userActivitylogAdminUser(display, contactsLoyaltyObj.getCardNumber(),loyaltyProgram.getProgramName());
	}
		catch(Exception e){
			logger.error("Exception ",e);
		}
	}
	//onClick$suspendMembershipBtnId


	public void onClick$resendBtnId() {
		logger.debug(">>>>>>>>>>>>> entered in onClick$resendBtnId");
		try{
			if(contactsLoyaltyObj != null) {
				Contacts contact = contactsLoyaltyObj.getContact();
				if(contact == null){
					MessageUtil.setMessage("No contact details found.", "red");
					return;
				}
				if(Messagebox.show("Do you want to resend loyalty membership details to the contact ?", "Confirm" ,Messagebox.OK | Messagebox.CANCEL, 
						Messagebox.QUESTION) == Messagebox.OK) {
					
					if(contact.getEmailId() != null && !contact.getEmailId().trim().isEmpty()){
						logger.debug("-----------email----------"+contact.getEmailId());
						sendEnrollTemplate(currUser, contact.getEmailId(), contact.getContactId(), contactsLoyaltyObj.getLoyaltyId());
					}
					
					if(contact.getMobilePhone() != null && !contact.getMobilePhone().trim().isEmpty()){
						logger.debug("-----------phone----------"+contact.getMobilePhone());
						sendEnrollSMSTemplate(currUser, contact.getContactId(), contactsLoyaltyObj.getLoyaltyId(), contact.getMobilePhone());
					}
					// to send the loyalty related email
					/*CustomTemplates custTemplate = null;
					String message = PropertyUtil.getPropertyValueFromDB("loyaltyOptinMsgTemplate");

					MailingList mailingList =ltyPrgmService.findListTypeMailingList(Constants.MAILINGLIST_TYPE_POS, currUser.getUserId());
					//mailingListDao.findListTypeMailingList(Constants.MAILINGLIST_TYPE_POS, currUser.getUserId());

					if (mailingList.getLoyaltyCutomTempId() != null) {
						custTemplate = ltyPrgmService.findCustTemplateById(mailingList.getLoyaltyCutomTempId());
						message = custTemplate.getHtmlText();
					}



					logger.debug("-----------email----------"+contact.getEmailId());

					Long memberShipNum = Long.parseLong(contactsLoyaltyObj.getCardNumber().toString().trim());
					ltyPrgmService.getContactLtyByMembershipNumber(memberShipNum,currUser.getUserId().longValue());
					Users currentUser = currUser;

					message = message.replace("[OrganizationName]", currentUser.getUserOrganization().getOrganizationName())
							.replace("[CardNumber]", contactsLoyaltyObj.getCardNumber().toString())
							.replace("[CardPin]", contactsLoyaltyObj.getCardPin().toString())
							.replace("[senderName]", Utility.getOnlyUserName(currentUser.getUserName()));

					EmailQueue testEmailQueue = new EmailQueue(custTemplate,
							Constants.EQ_TYPE_TEST_LOYALTY_DETAILS_MAIL, message,
							"Active", contact.getEmailId().toString().trim(), currentUser,
							MyCalendar.getNewCalendar(), "Loyalty Card Details.", null,
							null, null, contact.getContactId());

					ltyPrgmService.saveOrUpdateEmailQueue(testEmailQueue);*/
					logger.debug("Loyalty card details template saved in email queue....ready to send mail... ");
					Messagebox.show("Loyalty Membership details will be delivered shortly to the contact.", "Information" ,Messagebox.OK, 
							Messagebox.INFORMATION);
				}
			}
			else {
				Messagebox.show("Please look-up membership before performing this action.", "Error" ,Messagebox.OK, 
						Messagebox.ERROR);
			}
		}catch(Exception e){
			logger.error("Exception in sending card details::",e);
		}
		logger.debug("<<<<<<<<<<<<< completed onClick$resendBtnId ");
	}// onClick$resendBtnId

	public void sendEnrollTemplate(Users user, String emailId, Long contactId, Long loyaltyId) {

		try{
			CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
			EmailQueueDao emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			LoyaltyAutoComm loyaltyAutoComm = getLoyaltyAutoComm(contactsLoyaltyObj.getProgramId());
			CustomTemplates custTemplate = null;
			String message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_EMAIL_REGISTRATION);
			if(loyaltyAutoComm != null && loyaltyAutoComm.getRegEmailTmpltId() != null){
				custTemplate = customTemplatesDao.findCustTemplateById(loyaltyAutoComm.getRegEmailTmpltId());
				if(custTemplate != null) {
					if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
					  message = custTemplate.getHtmlText();
					}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
					  MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
					  if(myTemplates != null)message = myTemplates.getContent();
					}
			  }
			}

			/*if(loyaltyAutoComm == null || loyaltyAutoComm.getRegEmailTmpltId() == null || custTemplate == null){
				
			}*/

			String subject = "Welcome to [OrganizationName]'s Loyalty Program!";
			subject = subject.replace("[OrganizationName]", user.getUserOrganization().getOrganizationName());

			EmailQueue emailQueue = new EmailQueue(custTemplate != null ?custTemplate.getTemplateId() : null, Constants.EQ_TYPE_TEST_LOYALTY_DETAILS_MAIL, 
					message, "Active", emailId, user, MyCalendar.getNewCalendar(),subject, 
					contactId, loyaltyId);

			//emailQueueDao.saveOrUpdate(emailQueue);
			emailQueueDaoForDML.saveOrUpdate(emailQueue);
		}catch(Exception e){
			logger.error("Exception in placing loyalty template email in email queue...", e);
		}

	}

	public void sendEnrollSMSTemplate(Users user, Long cid, Long loyaltyId, String toMobileNo) {

		logger.info("Entered sendEnrollSMSTemplate...");
		try{
			OCSMSGatewayDao oCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
			UserSMSGatewayDao userSmsGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
			AutoSMSDao autoSmsDao = (AutoSMSDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			AutoSmsQueueDao smsQueueDao = (AutoSmsQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_QUEUE_DAO);
			AutoSmsQueueDaoForDML smsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
			LoyaltyAutoComm loyaltyAutoComm = getLoyaltyAutoComm(contactsLoyaltyObj.getProgramId());
			SMSSettingsDao smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			SMSSettings usersmsSettings =  smsSettingsDao.findHeaderbyUser(user.getUserId());
			AutoSMS autoSms = null;
			OCSMSGateway ocSMSGateway = null;
			UserSMSGateway userSMSGateway = null;
			String message = "";
			String senderId = "";
			
			logger.info("autoSms is "+autoSms);
			if(loyaltyAutoComm != null && loyaltyAutoComm.getRegSmsTmpltId() != null){
				autoSms = autoSmsDao.getAutoSmsTemplateById(loyaltyAutoComm.getRegSmsTmpltId()); 
				senderId = autoSms.getSenderId();
				message = autoSms.getMessageContent();
				if(message.contains("[Organization Name]")){
					if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
					{
						message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
					}else{
						message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
					}	
				}

			}

			if(loyaltyAutoComm == null || loyaltyAutoComm.getRegSmsTmpltId() == null || autoSms == null){
				String senderIdWithOrWithoutComma = null;

				userSMSGateway = userSmsGatewayDao.findByUserId(user.getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
				ocSMSGateway = oCSMSGatewayDao.findById(userSMSGateway.getGatewayId());
				senderIdWithOrWithoutComma =ocSMSGateway.getSenderId();
				String[] senderIdArr;
				senderIdArr = senderIdWithOrWithoutComma.split(",");
				senderId = senderIdArr[0];
				senderId = senderId.trim();
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_REGISTRATION);
				if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
				{
					message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
				}else{
					message = message.replace("[Organization Name]", user.getUserOrganization().getOrganizationName());	
				}
			}
			//String senderId = "";
			String accountType = null;

			accountType = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType());

			//UserSMSSenderId userSMSSenderId = getsenderIds(user.getUserId());

			//if(userSMSSenderId != null) {
			//	senderId = userSMSSenderId.getSenderId();
			//}

			AutoSmsQueue autoSmsQueue = new AutoSmsQueue(message, OCConstants.ASQ_TYPE_LOYALTY_DETAILS
					, "Active", toMobileNo, 
					accountType, senderId, Calendar.getInstance(), user.getUserId(), cid, loyaltyId);
			//smsQueueDao.saveOrUpdate(autoSmsQueue);
			if (autoSms != null && autoSms.getAutoSmsId() != null) {
				autoSmsQueue.setTemplateId(autoSms.getAutoSmsId());
			}
			if(autoSms!=null && autoSms.getTemplateRegisteredId() !=null) {
				autoSmsQueue.setTemplateRegisteredId(autoSms.getTemplateRegisteredId());
			}
			smsQueueDaoForDML.saveOrUpdate(autoSmsQueue);
		}catch(Exception e){
			logger.error("Exception in placing loyalty template enroll in sms queue...", e);
		}

	}

	public UserSMSSenderId  getsenderIds(Long userId) {
		List<UserSMSSenderId> retSenderIds =  null;
		try{
			UserSMSSenderIdDao userSMSSenderIdDao = (UserSMSSenderIdDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USER_SMS_SENDER_ID_DAO);
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			logger.info("the logged user id is====>"+userId);
			Users user = usersDao.findByUserId(userId);
			String type = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType());
			retSenderIds = userSMSSenderIdDao.findSenderIdBySMSType(userId, type);
			if(retSenderIds == null) return null;
			return (UserSMSSenderId) retSenderIds.get(0);
		}catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		return (UserSMSSenderId) retSenderIds.get(0);
	}
	
	public void onClick$moveTierBtnId(){
		if(contactsLoyaltyObj == null) {
			Messagebox.show("Please look-up membership before performing this action.", "Error" ,Messagebox.OK, 
			Messagebox.ERROR);
			ltyPwdTbMvtierdId.setText("");
			return;
		}
		if(toTierLbId.getSelectedItem() == null){
			MessageUtil.setMessage( "Membership's program is not tier based program.", "color:red;", "TOP");
			ltyPwdTbMvtierdId.setText("");
			return;
		}
			if(isPasswordRequired!=null && isPasswordRequired) {
				LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
				LoyaltyThresholdAlerts loyaltyThresholdAlerts =ltyPrgmSevice.findPwdByUserID(currUser.getUserId());
				try {
					String encryptedPwd = EncryptDecryptLtyMembshpPwd.decryptProgramPwd(loyaltyThresholdAlerts.getLtySecurityPwd());
					ltyPwdTbMvtierdId.setValue("");
					ltyPwdTbMvtierdId.setValue(encryptedPwd.trim());
					onClick$submitPwdBtnMvtierId();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				ltyPwdPopupMvtierId.open(ltyPwdPopupMvtierId);
		}
	}


	public void onClick$addBtnId() {
		logger.debug(">>>>>>>>>>>>> entered in onClick$addBtnId");
			if(isPasswordRequired!=null && isPasswordRequired) {
				LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
				LoyaltyThresholdAlerts loyaltyThresholdAlerts =ltyPrgmSevice.findPwdByUserID(currUser.getUserId());
				try {
					String encryptedPwd = EncryptDecryptLtyMembshpPwd.decryptProgramPwd(loyaltyThresholdAlerts.getLtySecurityPwd());
					ltyPwdTbId.setValue("");
					ltyPwdTbId.setValue(encryptedPwd.trim());
					onClick$submitPwdBtnId();
				} catch (Exception e) {
					logger.error("Exception ::", e);
				}
			}else {
			ltyPwdPopupId.open(ltyPwdPopupId);
		}
	}
	
	public void onClick$submitPwdBtnMvtierId(){
		try {
			String pwdStr = ltyPwdTbMvtierdId.getValue().trim();

			if(pwdStr == null || pwdStr.trim().length() ==0) {
				MessageUtil.setMessage( "Please provide password.", "color:red;", "TOP");
				ltyPwdTbMvtierdId.setText("");
				return;
			}
			if(toTierLbId.getSelectedItem() == null){
				MessageUtil.setMessage( "Please select tier to change.", "color:red;", "TOP");
				ltyPwdTbMvtierdId.setText("");
				return;
			}
			
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			LoyaltyThresholdAlerts loyaltyThresholdAlerts = ltyPrgmSevice.findPwdByUserID(currUser.getUserId());
			if(loyaltyThresholdAlerts == null || !EncryptDecryptLtyMembshpPwd.decryptProgramPwd(loyaltyThresholdAlerts.getLtySecurityPwd()).equals(pwdStr)) {
				MessageUtil.setMessage("Please enter the correct password", "color:red", "TOP");
				ltyPwdTbMvtierdId.setText("");
				return;
			}else{
				

				if(Messagebox.show("Are you sure you want to adjust the tier of membership ?", "Confirm" ,Messagebox.OK | Messagebox.CANCEL, 
						Messagebox.QUESTION) == Messagebox.OK) {
				if(contactsLoyaltyObj != null) {
				contactsLoyaltyObj.setProgramTierId(Long.parseLong(toTierLbId.getSelectedItem().getValue().toString()));
				LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
				ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
				LoyaltyTransactionChild ltyChild = createMoveTierTransaction(contactsLoyaltyObj);
				LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
				//loyaltyTransactionChildDao.saveOrUpdate(ltyChild);
				loyaltyTransactionChildDaoForDML.saveOrUpdate(ltyChild);
				String tier = Constants.STRING_NILL ,level =Constants.STRING_NILL;
				LoyaltyProgram loyaltyProgram =null;
				LoyaltyProgramTier loyaltyProgramTier = null;
				loyaltyProgram = ltyPrgmService.getProgmObj(contactsLoyaltyObj.getProgramId());
				loyaltyProgramTier = ltyPrgmService.getTierObj(contactsLoyaltyObj.getProgramTierId());
				if(loyaltyProgram.getTierEnableFlag() == OCConstants.FLAG_YES && loyaltyProgramTier != null){
					tier = loyaltyProgramTier.getTierName() ;
					level = " ( Level : "+(loyaltyProgramTier.getTierType() == null ? Constants.STRING_NILL : loyaltyProgramTier.getTierType())+" )";
					tierNameMSummaryLblId.setValue(tier+level);
				}
				else{
					tierNameMSummaryLblId.setValue(tier);
				}
				displayChangingTier();
				}else {

				Messagebox.show("Please look-up membership before performing this action.", "Error" ,Messagebox.OK, 
				Messagebox.ERROR);
				ltyPwdTbMvtierdId.setText("");
				return;
				}
				}
			}
		}catch (Exception e) {
			logger.error("Exception :: ",e);
		}
		ltyPwdPopupSubId.close();
		return;
	}
	
	public void onClick$submitPwdBtnId() {
		
		try {
			String receiptAmount = receiptAmtTbId.getValue().trim();
			String addedAmount = null;
			String pwdStr = ltyPwdTbSubId.getValue().trim();

			if(pwdStr == null || pwdStr.trim().length() ==0) {
				MessageUtil.setMessage( "Please provide password.", "color:red;", "TOP");
				ltyPwdTbSubId.setText("");
				return;
			}
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			LoyaltyThresholdAlerts loyaltyThresholdAlerts = ltyPrgmSevice.findPwdByUserID(currUser.getUserId());
			if(loyaltyThresholdAlerts != null && EncryptDecryptLtyMembshpPwd.decryptProgramPwd(loyaltyThresholdAlerts.getLtySecurityPwd()).equals(pwdStr)) {
			LoyaltyProgram loyaltyProgram = null;
			
			if(contactsLoyaltyObj != null) {
				LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();

				Long programId = contactsLoyaltyObj.getProgramId();
				loyaltyProgram = ltyPrgmService.getProgmObj(programId);
				String valueCode = addLbId2.getSelectedItem().getValue();
				String displayLabel = "";
			if(!valueCode.contains("-")){
				displayLabel="value code";
				//valueCode="valueCode";
			}else{
				String[] valueCodeSplit = valueCode.split("-");
				//String displayLabel = "Amount".equalsIgnoreCase(valueCodeSplit[1]) ? "rewards":"points"; //Ask Mam for LPV
				if(OCConstants.LOYALTY_TYPE_AMOUNT.equalsIgnoreCase(valueCodeSplit[1])){
					 displayLabel="rewards";
				}else if(OCConstants.LOYALTY_POINTS.equalsIgnoreCase(valueCodeSplit[1])){
					 displayLabel="points";
				}else{
					 displayLabel="lifetime purchase value";
				}
				valueCode = valueCodeSplit[1];
			}
								
				String enteredAmount = addTbId.getValue().trim();
				String[] amount = new String[2];
				amount[0] = valueCode;
				amount[1] = enteredAmount;
				Double balanceToAdd=0.0;
				if(valueCode.equalsIgnoreCase("points")) {
					addedAmount = "added "+enteredAmount.trim()+"points to";
				}else if(valueCode.equalsIgnoreCase("Amount")){
					/*addedAmount = "added $"+enteredAmount.trim()+" to";*/
					addedAmount = "added "+ userCurrencySymbol +enteredAmount.trim()+" to"; //ask mam for LPV 
				}else if(valueCode.equalsIgnoreCase("LPV")){
					addedAmount = "added "+ userCurrencySymbol +enteredAmount.trim()+" LPV to";
				}else{
					addedAmount = "added " +enteredAmount.trim()+" value code to";					
				}
				/*if(loyaltyProgram == null &&  transactionChild == null && loyaltyProgramTier == null){
					MessageUtil.setMessage("Not a loyalty card.", "red");
					return;
				}*/

				if(loyaltyProgram.getStatus() != null && OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equalsIgnoreCase(loyaltyProgram.getStatus()) && OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE.equalsIgnoreCase(contactsLoyaltyObj.getMembershipStatus()) ){

					if(enteredAmount.isEmpty()){
						MessageUtil.setMessage("Please enter "+displayLabel+" to add.", "red");
						ltyPwdTbSubId.setText("");
						addTbId.setText("");
						return;
					}//if

					if(amount[1].startsWith("-") || amount[1].startsWith("+")) {
						MessageUtil.setMessage("Please enter valid  "+displayLabel+".", "red");
						ltyPwdTbSubId.setText("");
						addTbId.setText("");
						return;
					}
					else {

						if((addLbId2.isVisible() && addLbId2.getSelectedItem().getValue().toString().equalsIgnoreCase("Hold-Points"))||(addLbId2.isVisible() && addLbId2.getSelectedItem().getValue().toString().equalsIgnoreCase("Hold-Amount"))) {
							if(contactsLoyaltyObj.getProgramTierId() != null){
								LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
								LoyaltyProgramTier loyaltyProgramTier = loyaltyProgramTierDao.findByTierId(contactsLoyaltyObj.getProgramTierId());
								if(loyaltyProgramTier.getActivationFlag() == OCConstants.FLAG_NO) {
									MessageUtil.setMessage("Balance cannot be added. Please enable the activation flag in program tier rules.", "color:red", "TOP");
									ltyPwdTbSubId.setText("");
									addTbId.setText("");
									subTb2Id.setText(Constants.STRING_NILL);
									storeNoTbId.setText(Constants.STRING_NILL);
									receiptNoTbId.setText(Constants.STRING_NILL);
									receiptAmtTbId.setText(Constants.STRING_NILL);
									return;
								}
							}
							
							else {
									MessageUtil.setMessage("Balance cannot be added. Please enable the activation flag in program tier rules.", "color:red", "TOP");
									ltyPwdTbSubId.setText("");
									addTbId.setText("");
									subTb2Id.setText(Constants.STRING_NILL);
									storeNoTbId.setText(Constants.STRING_NILL);
									receiptNoTbId.setText(Constants.STRING_NILL);
									receiptAmtTbId.setText(Constants.STRING_NILL);
									return;
							}
						}
						
						boolean isValidNumber = false;
						if(displayLabel.equalsIgnoreCase("points")){
							isValidNumber = checkIfLong(amount[1]);
						}else{
							isValidNumber = checkIfDouble(amount[1]);
						}
						 
						
						if(isValidNumber) {
							//balanceToAdd = enteredAmount != null ? Double.parseDouble(enteredAmount) : 0;
							double balToTwoDecimals;
							balToTwoDecimals = enteredAmount != null ? Double.parseDouble(enteredAmount) : 0;
							String result = Utility.truncateUptoTwoDecimal(balToTwoDecimals);
							if(result != null)
								balanceToAdd = Double.parseDouble(result);
							
							if(valueCode.equals("Amount")) {
								if(balanceToAdd==0){
									MessageUtil.setMessage("Rewards to be added should be greater than zero.", "color:red;");
									addTbId.setFocus(true);
									ltyPwdTbSubId.setText("");
									addTbId.setText("");
									subTb2Id.setText(Constants.STRING_NILL);
									storeNoTbId.setText(Constants.STRING_NILL);
									receiptNoTbId.setText(Constants.STRING_NILL);
									receiptAmtTbId.setText(Constants.STRING_NILL);
									return;
								}
							}
							else if(valueCode.equals("Points")){
								if(balanceToAdd==0){
									MessageUtil.setMessage("Points to be added should be greater than zero.", "color:red;");
									ltyPwdTbSubId.setText("");
									addTbId.setText("");
									addTbId.setFocus(true);
									subTb2Id.setText(Constants.STRING_NILL);
									storeNoTbId.setText(Constants.STRING_NILL);
									receiptNoTbId.setText(Constants.STRING_NILL);
									receiptAmtTbId.setText(Constants.STRING_NILL);
									return;
								}
							}
							else if(valueCode.equals("LPV")){
								if(balanceToAdd==0){
									MessageUtil.setMessage("Lifetime Purchase Value to be added should be greater than zero.", "color:red;");
									ltyPwdTbSubId.setText("");
									addTbId.setText("");
									addTbId.setFocus(true);
									subTb2Id.setText(Constants.STRING_NILL);
									storeNoTbId.setText(Constants.STRING_NILL);
									receiptNoTbId.setText(Constants.STRING_NILL);
									receiptAmtTbId.setText(Constants.STRING_NILL);
									return;
								}
								
							}else{
								if(balanceToAdd==0){
									MessageUtil.setMessage("Value codes to be added should be greater than zero.", "color:red;");
									ltyPwdTbSubId.setText("");
									addTbId.setText("");
									addTbId.setFocus(true);
									subTb2Id.setText(Constants.STRING_NILL);
									storeNoTbId.setText(Constants.STRING_NILL);
									receiptNoTbId.setText(Constants.STRING_NILL);
									receiptAmtTbId.setText(Constants.STRING_NILL);
									return;
								}
							}
//removed loyalty
							if(Messagebox.show("Are you sure you want to add  "+displayLabel+" ? ", "Confirm" ,Messagebox.OK | Messagebox.CANCEL, 
									Messagebox.QUESTION) == Messagebox.OK) { //ask mam

								// GET CONTACTLOYATLY OBJECT

								// CHECK IF THE CARD REWARD FLAG OF TYPE L/GL, THEN ALLOW ADJUSTMENT. FOR G TYPE DO NOT ALLOW ADJUSTMENTS.
								if(!OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyaltyObj.getRewardFlag())){ //ask mam
									boolean flag = false;
									Double fromLtyBalance = contactsLoyaltyObj.getTotalLoyaltyEarned();
									Double fromAmtBalance = contactsLoyaltyObj.getTotalGiftcardAmount();
									Double fromLPVBalance = LoyaltyProgramHelper.getLPV(contactsLoyaltyObj);
									Double fromCPVBalance = contactsLoyaltyObj.getCummulativePurchaseValue();
									// UPDATE BALANCE
									try {
										if((addLbId2.isVisible() && addLbId2.getSelectedItem().getValue().toString().equalsIgnoreCase("Hold-Points"))||(addLbId2.isVisible() && addLbId2.getSelectedItem().getValue().toString().equalsIgnoreCase("Hold-Amount"))) {
											if(valueCode.equalsIgnoreCase("Amount")) {
												Double holdAmtBalance =  contactsLoyaltyObj.getHoldAmountBalance() == null ? 0.0 : contactsLoyaltyObj.getHoldAmountBalance();
												holdAmtBalance = balanceToAdd + holdAmtBalance;
												//contactsLoyaltyObj.setHoldAmountBalance(holdAmtBalance);
												contactsLoyaltyObj.setHoldAmountBalance(new BigDecimal(holdAmtBalance).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
											}else if(valueCode.equalsIgnoreCase("Points")) {
												Double holdPtsBalance =  contactsLoyaltyObj.getHoldPointsBalance()== null ? 0.0 : contactsLoyaltyObj.getHoldPointsBalance();
												holdPtsBalance = balanceToAdd + holdPtsBalance;
												contactsLoyaltyObj.setHoldPointsBalance(holdPtsBalance);
											}
											ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
											
											//calculate activation date
											LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
											LoyaltyProgramTier tier = loyaltyProgramTierDao.findByTierId(contactsLoyaltyObj.getProgramTierId());
											String activationDate = null;
											if(tier.getPtsActiveDateValue() != null && OCConstants.LOYALTY_TYPE_DAY.equals(tier.getPtsActiveDateType().trim())){
												Calendar cal = Calendar.getInstance();
												cal.add(Calendar.DAY_OF_MONTH, tier.getPtsActiveDateValue().intValue());
												activationDate = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_YEARTODATE);
											}
											
											String description2 = null;
											createPurchaseTransaction(contactsLoyaltyObj, balanceToAdd, valueCode, Constants.STRING_NILL,activationDate,OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_NEW, OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD,description2);
											displayFinalHoldBalances(contactsLoyaltyObj);
											addLbId2.setSelectedIndex(0);
											addLbId.setSelectedIndex(0);
											addLbId.getItemAtIndex(1).setVisible(true);
											addTbId.setValue(Constants.STRING_NILL);
											subTb2Id.setText(Constants.STRING_NILL);
											storeNoTbId.setText(Constants.STRING_NILL);
											receiptNoTbId.setText(Constants.STRING_NILL);
											receiptAmtTbId.setText(Constants.STRING_NILL);
											ltyPwdTbSubId.setText("");
										}
										else {
											if(valueCode.equalsIgnoreCase("Amount")) {
												//update totalGiftcardAmount,giftcardBalance
												Double totalGiftCardAmount = contactsLoyaltyObj.getTotalGiftcardAmount()== null ? 0.0 : contactsLoyaltyObj.getTotalGiftcardAmount();
												//totalGiftCardAmount = totalGiftCardAmount + balanceToAdd ;
												totalGiftCardAmount = new BigDecimal(totalGiftCardAmount + balanceToAdd).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
												Double giftCardBalance =  contactsLoyaltyObj.getGiftcardBalance() == null ? 0.0 : contactsLoyaltyObj.getGiftcardBalance();
												//giftCardBalance = giftCardBalance + balanceToAdd ;
												giftCardBalance = new BigDecimal(giftCardBalance + balanceToAdd).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
												contactsLoyaltyObj.setTotalGiftcardAmount(totalGiftCardAmount);
												logger.info("giftcard balance before rounding:::" + giftCardBalance);
												
												
												logger.info("Double setTotalGiftcardAmount = "+totalGiftCardAmount);
												contactsLoyaltyObj.setGiftcardBalance(giftCardBalance);
																																		
												logger.info("giftcard balance after rounding:::"+contactsLoyaltyObj.getGiftcardBalance());
												flag  =  ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
											}
											else if(valueCode.equalsIgnoreCase("Points")) {//POINTS
												//loyaltyBalance,totalLoyaltyEarned
												Double loyaltyBalance =  contactsLoyaltyObj.getLoyaltyBalance() == null ? 0.0 : contactsLoyaltyObj.getLoyaltyBalance();
												Double totalLoyaltyEarned = contactsLoyaltyObj.getTotalLoyaltyEarned() == null ? 0.0 : contactsLoyaltyObj.getTotalLoyaltyEarned();
												logger.info("Previous LoyaltyBalance was ::::::::::::"+loyaltyBalance);
												loyaltyBalance = loyaltyBalance + balanceToAdd;
												totalLoyaltyEarned = totalLoyaltyEarned + balanceToAdd;
												contactsLoyaltyObj.setLoyaltyBalance(loyaltyBalance);
												contactsLoyaltyObj.setTotalLoyaltyEarned(totalLoyaltyEarned);
												flag = ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
												logger.info("New LoyaltyBalance is ::::::::::::"+loyaltyBalance);
											}
											else if(valueCode.equalsIgnoreCase("LPV")){
												Double LPVBal =  contactsLoyaltyObj.getCummulativePurchaseValue() == null ? 0.0 : contactsLoyaltyObj.getCummulativePurchaseValue();
												logger.info("Previous LPVBal was ::::::::::::"+LPVBal);
												LPVBal = LPVBal + balanceToAdd;
												contactsLoyaltyObj.setCummulativePurchaseValue(LPVBal);
												flag = ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
												logger.info("New LPVBal is ::::::::::::"+LPVBal);
												
											}
											if(flag){	
												String description2 = null;
												// CREATE TRANSACTION
												LoyaltyTransactionChild transactionChild = createPurchaseTransaction(contactsLoyaltyObj, balanceToAdd, valueCode, Constants.STRING_NILL, null, null,OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD,description2);
												// CREATE EXPIRY TRANSACTION
												Long adjustPoints = 0l;
												Double adjustAmt = 0.0;
												Double adjustLPV = 0.0;
												if(valueCode.equals(OCConstants.LOYALTY_TYPE_POINTS)){
													adjustPoints = balanceToAdd.longValue();
												}
												else if(valueCode.equals("Amount")){
													adjustAmt = balanceToAdd;
												}
												else adjustLPV = balanceToAdd;
											
												createExpiryTransaction(contactsLoyaltyObj, adjustPoints, adjustAmt, contactsLoyaltyObj.getOrgId(), 
														transactionChild.getTransChildId(),null);
												
												LoyaltyProgramTier loyaltyProgramTier = null;
												if(contactsLoyaltyObj.getProgramTierId() == null) {
													loyaltyProgramTier = findTier(contactsLoyaltyObj);
													if (loyaltyProgramTier == null) {
														// CALL BONUS
														updateThresholdBonus(contactsLoyaltyObj, loyaltyProgram, fromLtyBalance, fromAmtBalance,fromCPVBalance ,fromLPVBalance, null,false);
														ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
														updateLoyaltyData(contactsLoyaltyObj);
														addTbId.setValue(Constants.STRING_NILL);
														ltyPwdTbSubId.setText("");
														addLbId2.setSelectedIndex(0);
														addLbId.setSelectedIndex(0);
														addLbId.getItemAtIndex(1).setVisible(true);
														MessageUtil.setMessage(StringUtils.capitalize(displayLabel)+" added successfully.", "blue");
														subTb2Id.setText(Constants.STRING_NILL);
														storeNoTbId.setText(Constants.STRING_NILL);
														receiptNoTbId.setText(Constants.STRING_NILL);
														receiptAmtTbId.setText(Constants.STRING_NILL);
														return;
													}
													else {
														contactsLoyaltyObj.setProgramTierId(loyaltyProgramTier.getTierId());
													}
												}
												else{
													loyaltyProgramTier = getLoyaltyTier(contactsLoyaltyObj.getProgramTierId());
												}
												// CALL CONVERSION
												//applyConversionRules(contactsLoyaltyObj, transactionChild, loyaltyProgram, loyaltyProgramTier);
												
												Long pointsDifference = 0l;
												Double amountDifference = 0.0;
												String[] diffArr = applyConversionRules(contactsLoyaltyObj, loyaltyProgramTier); //0 - amountdiff, 1 - pointsdiff
												logger.info("balances After conversion rules updatation --  points = "+contactsLoyaltyObj.getLoyaltyBalance()+" currency = "+contactsLoyaltyObj.getGiftcardBalance());
												
												String conversionRate = null;
												long convertPoints = 0;
												double convertAmount = 0;
												if(diffArr != null){
													convertAmount = Double.valueOf(diffArr[0].trim());
													convertPoints = Double.valueOf(diffArr[1].trim()).longValue();
													conversionRate = diffArr[2];
												}
												
												pointsDifference = adjustPoints - convertPoints;
												amountDifference = (double)adjustAmt + (diffArr != null ? Double.parseDouble(diffArr[0].trim()) : 0.0);
												
												LoyaltyProgramTier preTier = loyaltyProgramTier;
												// CALL TIER UPGD
												if(loyaltyProgramTier!=null && loyaltyProgramTier.getMultipleTierUpgrdRules()!=null && !loyaltyProgramTier.getMultipleTierUpgrdRules().isEmpty())//APP-4511
													loyaltyProgramTier = applyMultipleTierUpgradeRule(contactsLoyaltyObj, loyaltyProgramTier,loyaltyProgram,transactionChild);
												else
													loyaltyProgramTier = applyTierUpgradeRule(contactsLoyaltyObj, loyaltyProgram, transactionChild, loyaltyProgramTier);
												
												String description21 = null;
												updatePurchaseTransaction(transactionChild, contactsLoyaltyObj, ""+pointsDifference, ""+amountDifference, conversionRate, convertAmount,loyaltyProgramTier);
												logger.info("balances before balance object = "+contactsLoyaltyObj.getLoyaltyBalance()+" currency = "+contactsLoyaltyObj.getGiftcardBalance());

												boolean tierUpgd = false;
												if (!preTier.getTierType().equalsIgnoreCase(loyaltyProgramTier.getTierType())) {
													tierUpgd = true;
												}
												// CALL BONUS
												updateThresholdBonus(contactsLoyaltyObj, loyaltyProgram, fromLtyBalance, fromAmtBalance, fromCPVBalance,fromLPVBalance, loyaltyProgramTier,tierUpgd);

												ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
												
												updateLoyaltyData(contactsLoyaltyObj);
												addTbId.setValue(Constants.STRING_NILL);
												ltyPwdTbSubId.setText("");
												addLbId2.setSelectedIndex(0);
												addLbId.setSelectedIndex(0);
												addLbId.getItemAtIndex(1).setVisible(true);
												MessageUtil.setMessage(StringUtils.capitalize(displayLabel)+" added successfully.", "blue");
												subTb2Id.setText(Constants.STRING_NILL);
												storeNoTbId.setText(Constants.STRING_NILL);
												receiptNoTbId.setText(Constants.STRING_NILL);
												receiptAmtTbId.setText(Constants.STRING_NILL);
												
												
											}
											else{
												MessageUtil.setMessage("Transaction was not successful.", "red");
												ltyPwdTbSubId.setText("");
												addTbId.setText("");
												subTb2Id.setText(Constants.STRING_NILL);
												storeNoTbId.setText(Constants.STRING_NILL);
												receiptNoTbId.setText(Constants.STRING_NILL);
												receiptAmtTbId.setText(Constants.STRING_NILL);
												return;
											}

										}
									}
									catch (Exception e) {
										logger.error("Exception ,Transaction was not successful ::",e);
										ltyPwdTbSubId.setText("");
										addTbId.setText("");
										subTb2Id.setText(Constants.STRING_NILL);
										storeNoTbId.setText(Constants.STRING_NILL);
										receiptNoTbId.setText(Constants.STRING_NILL);
										receiptAmtTbId.setText(Constants.STRING_NILL);
									}

								}//status if
								else{
									MessageUtil.setMessage("Transaction was not successful.", "red");
									logger.error("Unable to perform adjustments because Reward Flag is G");
									ltyPwdTbSubId.setText("");
									addTbId.setText("");
									subTb2Id.setText(Constants.STRING_NILL);
									storeNoTbId.setText(Constants.STRING_NILL);
									receiptNoTbId.setText(Constants.STRING_NILL);
									receiptAmtTbId.setText(Constants.STRING_NILL);
									return;
								}
							}//confirmation if
						}
						else {
							MessageUtil.setMessage("Please enter valid "+displayLabel+".", "red");
							ltyPwdTbSubId.setText("");
							addTbId.setText("");
							subTb2Id.setText(Constants.STRING_NILL);
							storeNoTbId.setText(Constants.STRING_NILL);
							receiptNoTbId.setText(Constants.STRING_NILL);
							receiptAmtTbId.setText(Constants.STRING_NILL);
						}
					}

				}// if status 
				else {
					Messagebox.show("Please select a contact with active program & membership status.", "Error" ,Messagebox.OK, 
							Messagebox.ERROR);
					ltyPwdTbSubId.setText("");
					addTbId.setText("");
					subTb2Id.setText(Constants.STRING_NILL);
					storeNoTbId.setText(Constants.STRING_NILL);
					receiptNoTbId.setText(Constants.STRING_NILL);
					receiptAmtTbId.setText(Constants.STRING_NILL);
				}
			}
			else{

				Messagebox.show("Please look-up membership before performing this action.", "Error" ,Messagebox.OK, 
						Messagebox.ERROR);
				ltyPwdTbSubId.setText("");
				addTbId.setText("");
				subTb2Id.setText(Constants.STRING_NILL);
				storeNoTbId.setText(Constants.STRING_NILL);
				receiptNoTbId.setText(Constants.STRING_NILL);
				receiptAmtTbId.setText(Constants.STRING_NILL);
				return;
			}
		} else{
			MessageUtil.setMessage("Please enter the correct password", "color:red", "TOP");
			ltyPwdTbSubId.setText("");
			addTbId.setText("");
			subTb2Id.setText(Constants.STRING_NILL);
			storeNoTbId.setText(Constants.STRING_NILL);
			receiptNoTbId.setText(Constants.STRING_NILL);
			receiptAmtTbId.setText(Constants.STRING_NILL);
			return;
		}
			LoyaltyProgram loyaltyProgram = loyaltyProgramDao.findById(contactsLoyaltyObj.getProgramId());
		
			
			LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
			Users user=currUser;
			ContactsLoyalty contactsLoyalty=contactsLoyaltyObj;
			Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
			if (contact != null && (contact.getEmailId() != null || 
					(user.isEnableSMS() && contactsLoyalty.getMobilePhone() != null)) ) {
				LoyaltyAutoComm autoComm = getLoyaltyAutoComm(contactsLoyaltyObj.getProgramId());
				if(autoComm != null ){
					if(autoComm.getAdjustmentAutoEmailTmplId()!=null ){
					
						if (contact.getEmailId() != null) {
							autoCommGen.sendAdjustmentTemplate(autoComm.getAdjustmentAutoEmailTmplId(),
									Constants.STRING_NILL + contactsLoyalty.getCardNumber(), contactsLoyalty.getCardPin(), user,
									contact.getEmailId(), contact.getFirstName(), contact.getContactId(),
									contactsLoyalty.getLoyaltyId(),receiptAmount);
						}
					}
					
					if (autoComm.getAdjustmentAutoSmsTmplId()!=null && user.isEnableSMS() 
							&& contactsLoyalty.getMobilePhone() != null) {
						autoCommGen.sendAdjustmentSMSTemplate(autoComm.getAdjustmentAutoSmsTmplId(), user,
								contactsLoyalty.getContact().getContactId(), contactsLoyalty.getLoyaltyId(),
								contactsLoyalty.getMobilePhone(),receiptAmount);
					}
					
				}
						
				
			
				
			}
			
			
			userActivitylogAdminUser(addedAmount, contactsLoyaltyObj.getCardNumber(),loyaltyProgram.getProgramName());			
		}
		catch (WrongValueException e) {
			logger.error("Exception ",e);
		} catch (NumberFormatException e) {
			logger.error("Exception ",e);
		}catch(Exception e){
			logger.error("Exception ",e);
		}
		 
	logger.debug("<<<<<<<<<<<<< completed onClick$addBtnId ");
	}// onClick$addBtnId

	public void onClick$subtractBtnId() {
		logger.debug(">>>>>>>>>>>>> entered in onClick$subtractBtnId");
			if(isPasswordRequired!=null && isPasswordRequired) {
				LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			 	LoyaltyThresholdAlerts loyaltyThresholdAlerts =ltyPrgmSevice.findPwdByUserID(currUser.getUserId());
				try {
					String encryptedPwd = EncryptDecryptLtyMembshpPwd.decryptProgramPwd(loyaltyThresholdAlerts.getLtySecurityPwd());
					ltyPwdTbSubId.setValue("");
					ltyPwdTbSubId.setValue(encryptedPwd.trim());
					onClick$submitPwdBtnSubId();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
			ltyPwdPopupSubId.open(ltyPwdPopupSubId);
		}
	}
	public void onClick$submitPwdBtnSubId() {
			if("Add".equals(addLbId.getSelectedItem().getValue())){
				onClick$submitPwdBtnId();
			}else if("Subtract".equals(addLbId.getSelectedItem().getValue())){
	try {
			String receiptAmount =receiptAmtTbId.getValue().trim();
			String substratAmount = null;
			String pwdStr = ltyPwdTbSubId.getValue().trim();

			if(pwdStr == null || pwdStr.trim().length() ==0) {
				MessageUtil.setMessage( "Please provide password.", "color:red;", "TOP");
				ltyPwdTbSubId.setText("");
				return;
			}
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			LoyaltyThresholdAlerts loyaltyThresholdAlerts = ltyPrgmSevice.findPwdByUserID(currUser.getUserId());
			if(loyaltyThresholdAlerts != null && EncryptDecryptLtyMembshpPwd.decryptProgramPwd(loyaltyThresholdAlerts.getLtySecurityPwd()).equals(pwdStr)) {
			LoyaltyProgram loyaltyProgram = null;

			if(contactsLoyaltyObj != null) {

				LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
				String membershipNumber = contactsLoyaltyObj.getCardNumber();
				Long programId = contactsLoyaltyObj.getProgramId();
				loyaltyProgram = ltyPrgmService.getProgmObj(programId);
				String valueCode = addLbId2.getSelectedItem().getValue();
				String displayLabel = "";
				String enteredAmount =  addTbId.getValue().trim();
				if(!valueCode.contains("-")){
					displayLabel="value code";
					valueCode="valueCode";
				}else{
					String[] valueCodeSplit = valueCode.split("-");
					valueCode = valueCodeSplit[1];
					if("Amount".equalsIgnoreCase(valueCodeSplit[1])) {
						displayLabel="rewards";
					}else if("points".equalsIgnoreCase(valueCodeSplit[1])){
						displayLabel="points";
					}
				}
				String[] amount = new String[2];
				
				amount[0] = valueCode;
				amount[1] = enteredAmount;
				double balanceToSub=0.0;
				if(valueCode.equalsIgnoreCase("points")) {
					substratAmount = "subtracted "+enteredAmount.trim()+"points from";
				}else if(valueCode.equalsIgnoreCase("Amount")){
					substratAmount = "subtracted "+ userCurrencySymbol +enteredAmount.trim()+" from";
				}else{
					substratAmount = "subtracted " +enteredAmount.trim()+"value codes from";
				}
				/*if(loyaltyProgram == null &&  transactionChild == null && loyaltyProgramTier == null){
					MessageUtil.setMessage("Not a loyalty card.", "red");
					return;
				}*/


				if(loyaltyProgram.getStatus() != null && OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equalsIgnoreCase(loyaltyProgram.getStatus()) && OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE.equalsIgnoreCase(contactsLoyaltyObj.getMembershipStatus()) ){


					if((addTbId.getValue().trim().isEmpty())){

						MessageUtil.setMessage("Please enter  "+displayLabel+" to subtract.", "red");
						ltyPwdTbSubId.setText("");
						addTbId.setText("");
						return;
					}//if

					if(amount[1].startsWith("-") || amount[1].startsWith("+")) {
						MessageUtil.setMessage("Please enter valid  "+displayLabel+".", "red");
						ltyPwdTbSubId.setText("");
						addTbId.setText("");
					}
					else {
						boolean isValidNumber = false;
						if(displayLabel.equalsIgnoreCase("rewards")){
							isValidNumber = checkIfDouble(amount[1]);
						}
						else{
							isValidNumber = checkIfLong(amount[1]);
						}
						
						if(isValidNumber) {
							//balanceToSub = enteredAmount != null ? Double.parseDouble(enteredAmount) : 0;
							
							double balToTwoDecimals = 0.0;
							balToTwoDecimals = enteredAmount != null ? Double.parseDouble(enteredAmount) : 0;
							String result = Utility.truncateUptoTwoDecimal(balToTwoDecimals);
							if(result != null){
								balanceToSub = Double.parseDouble(result);
							}
							
							//	balanceToSub =balanceToSub*-1;

							if(valueCode.equals("Amount")) {
								if(balanceToSub==0){
									MessageUtil.setMessage("Rewards to be subtracted should be greater than Zero.", "color:red;");
									ltyPwdTbSubId.setText("");
									addTbId.setText("");
									return;
								}
								
								if((addLbId2.isVisible() && addLbId2.getSelectedItem().getValue().toString().equalsIgnoreCase("Hold-Points"))||(addLbId2.isVisible() && addLbId2.getSelectedItem().getValue().toString().equalsIgnoreCase("Hold-Amount"))) {
									if((contactsLoyaltyObj.getHoldAmountBalance() == null || contactsLoyaltyObj.getHoldAmountBalance().toString().isEmpty())) {
										MessageUtil.setMessage("Balance is insufficient to subtract.","color:red","TOP");
										ltyPwdTbSubId.setText("");
										addTbId.setText("");
										return;
									}
									if(balanceToSub > contactsLoyaltyObj.getHoldAmountBalance()) {
										MessageUtil.setMessage("Balance to be subtracted should be less or equal to existing  balance.","color:red","TOP");
										ltyPwdTbSubId.setText("");
										addTbId.setText("");
										return;
									}
								}
								else {
									double existingGiftcardBalance = contactsLoyaltyObj.getGiftcardBalance() == null ? 0.0 : contactsLoyaltyObj.getGiftcardBalance();
									logger.info("Temp ::"+balanceToSub+"Existing loyalty balance"+existingGiftcardBalance);
									
									if(balanceToSub > existingGiftcardBalance){
										//balance to be is subtracted is greater than existing balance condition is true & display popup & return
										MessageUtil.setMessage("Rewards to be subtracted should be less or equal to existing currency balance.", "color:red;");
										//	addTbId.setValue(Constants.STRING_NILL);
										ltyPwdTbSubId.setText("");
										addTbId.setText("");
										addTbId.setFocus(true);
										return;
									}
								}
								
							} //if Amount
							else if(valueCode.equals("Points")){
								if(balanceToSub==0){
									MessageUtil.setMessage("Points to be subtracted should be greater than Zero.", "color:red;");
									ltyPwdTbSubId.setText("");
									addTbId.setText("");
									return;
								}
								if((addLbId2.isVisible() && addLbId2.getSelectedItem().getValue().toString().equalsIgnoreCase("Hold-Points"))||(addLbId2.isVisible() && addLbId2.getSelectedItem().getValue().toString().equalsIgnoreCase("Hold-Amount"))) {
									if((contactsLoyaltyObj.getHoldPointsBalance() == null || contactsLoyaltyObj.getHoldPointsBalance().toString().isEmpty())) {
										MessageUtil.setMessage("Balance is insufficient to subtract.","color:red","TOP");
										ltyPwdTbSubId.setText("");
										addTbId.setText("");
										return;
									}
									if(balanceToSub > contactsLoyaltyObj.getHoldPointsBalance()) {
										MessageUtil.setMessage("Balance to be subtracted should be less or equal to existing  balance.","color:red","TOP");
										ltyPwdTbSubId.setText("");
										addTbId.setText("");
										return;
									}
								}
								else {
									double existingLoyaltyBalance = contactsLoyaltyObj.getLoyaltyBalance() == null ? 0.0 : contactsLoyaltyObj.getLoyaltyBalance();

									logger.info("Temp ::"+balanceToSub+"Existing loyalty balance"+existingLoyaltyBalance);
									if(balanceToSub > existingLoyaltyBalance){
										MessageUtil.setMessage("Points to be subtracted should be less or equal to existing point balance.", "color:red;");
										ltyPwdTbSubId.setText("");
										addTbId.setText("");
										addTbId.setFocus(true);
										return;
									}	
								}
							}//else points
							else{
								if(balanceToSub==0){
									MessageUtil.setMessage("Value code to be subtracted should be greater than Zero.", "color:red;");
									ltyPwdTbSubId.setText("");
									addTbId.setText("");
									return;
								}
							}

							if(Messagebox.show("Are you sure you want to subtract loyalty  "+displayLabel+"  ? ", "Confirm" ,Messagebox.OK | Messagebox.CANCEL, 
									Messagebox.QUESTION) == Messagebox.OK) {


								// GET CONTACTLOYATLY OBJECT

								// CHECK IF THE CARD REWARD FLAG OF TYPE L/GL, THEN ALLOW ADJUSTMENT. FOR G TYPE DO NOT ALLOW ADJUSTMENTS.
								if(!OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyaltyObj.getRewardFlag())){

									// UPDATE BALANCE
									try {
										if((addLbId2.isVisible() && addLbId2.getSelectedItem().getValue().toString().equalsIgnoreCase("Hold-Points"))||(addLbId2.isVisible() && addLbId2.getSelectedItem().getValue().toString().equalsIgnoreCase("Hold-Amount"))) {
												if(valueCode.equalsIgnoreCase("Amount")) {
														Double holdCurrBal = contactsLoyaltyObj.getHoldAmountBalance() == null ? 0.0 : contactsLoyaltyObj.getHoldAmountBalance();
														holdCurrBal = holdCurrBal - balanceToSub;
														//contactsLoyaltyObj.setHoldAmountBalance(holdCurrBal);
														contactsLoyaltyObj.setHoldAmountBalance(new BigDecimal(holdCurrBal).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
												}else if(valueCode.equalsIgnoreCase("Points")) {
														Double holdPtsBal = contactsLoyaltyObj.getHoldPointsBalance() == null ? 0.0 : contactsLoyaltyObj.getHoldPointsBalance();
														holdPtsBal = holdPtsBal - balanceToSub;
														contactsLoyaltyObj.setHoldPointsBalance(holdPtsBal);
												}
												ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
												String description2 = null;
												//calculate activation date APP - 120
												LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
												LoyaltyProgramTier tier = loyaltyProgramTierDao.findByTierId(contactsLoyaltyObj.getProgramTierId());
												String activationDate = null;
												if(OCConstants.LOYALTY_TYPE_DAY.equals(tier.getPtsActiveDateType().trim())&&(tier.getPtsActiveDateValue()!=null)){
													Calendar cal = Calendar.getInstance();
													cal.add(Calendar.DAY_OF_MONTH, tier.getPtsActiveDateValue().intValue());
													activationDate = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_YEARTODATE);
												}
												//createPurchaseTransaction(contactsLoyaltyObj, balanceToSub, valueCode, Constants.STRING_NILL, null, null, OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_SUB); For APP-120
												createPurchaseTransaction(contactsLoyaltyObj, balanceToSub, valueCode, Constants.STRING_NILL, activationDate, null, OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_SUB,description2);
												if(valueCode.equals(OCConstants.LOYALTY_TYPE_POINTS)){
													deductHoldPoints(contactsLoyaltyObj, (long) balanceToSub);
												}
												else if(valueCode.equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
													deductHoldAmount(contactsLoyaltyObj, balanceToSub);
												}
												displayFinalHoldBalances(contactsLoyaltyObj);
												subTypeLbId.setSelectedIndex(0);
												subLbId.setSelectedIndex(0);
												addTbId.setValue(Constants.STRING_NILL);
												ltyPwdTbSubId.setText("");
												
											}
										else {
											if(valueCode.equalsIgnoreCase("Amount")) {
												
												

												//update totalGiftcardAmount,giftcardBalance
												//	balanceToSub =balanceToSub*-1;
												Double totalGiftCardAmount = contactsLoyaltyObj.getTotalGiftcardAmount()== null ? 0.0 : contactsLoyaltyObj.getTotalGiftcardAmount();
												totalGiftCardAmount = new BigDecimal(totalGiftCardAmount -balanceToSub).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
												contactsLoyaltyObj.setTotalGiftcardAmount(totalGiftCardAmount);
											
												
												//update totalGiftcardAmount,giftcardBalance
												//	balanceToSub =balanceToSub*-1;
												Double giftCardBalance =  contactsLoyaltyObj.getGiftcardBalance() == null ? 0.0 : contactsLoyaltyObj.getGiftcardBalance();
												//giftCardBalance = giftCardBalance - balanceToSub ;
												giftCardBalance = new BigDecimal(giftCardBalance - balanceToSub).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
												logger.info("giftCardBalance "+giftCardBalance+" - "+"balanceToSub "+balanceToSub);
												contactsLoyaltyObj.setGiftcardBalance(giftCardBalance);
												ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
											}
											else if(valueCode.equalsIgnoreCase("Points")){ //POINTS
												//loyaltyBalance,totalLoyaltyEarned
												//	balanceToSub =balanceToSub*-1;
												//loyaltyBalance,totalLoyaltyEarned
												Double totalLoyaltyEarned = contactsLoyaltyObj.getTotalLoyaltyEarned() == null ? 0.0 : contactsLoyaltyObj.getTotalLoyaltyEarned();
												totalLoyaltyEarned = totalLoyaltyEarned - balanceToSub;
												
												contactsLoyaltyObj.setTotalLoyaltyEarned(totalLoyaltyEarned);
												
												
												Double loyaltyBalance =  contactsLoyaltyObj.getLoyaltyBalance() == null ? 0.0 : contactsLoyaltyObj.getLoyaltyBalance();
												logger.info("Previous LoyaltyBalance was ::::::::::::"+loyaltyBalance);
												loyaltyBalance = loyaltyBalance - balanceToSub;
												logger.info("loyaltyBalance "+loyaltyBalance+" - "+"balanceToSub "+balanceToSub);
												contactsLoyaltyObj.setLoyaltyBalance(loyaltyBalance);
												ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
												logger.info("New LoyaltyBalance is ::::::::::::"+loyaltyBalance);
											}


											String description2 = null;
											// CREATE TRANSACTION
											createPurchaseTransaction(contactsLoyaltyObj, balanceToSub, valueCode, Constants.STRING_NILL, null, null, OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_SUB,description2);
											
											if(valueCode.equals(OCConstants.LOYALTY_TYPE_POINTS)){
												deductPointsFromExpiryTable(contactsLoyaltyObj, (long) balanceToSub);
											}
											else if(valueCode.equals("Amount")){
												deductLoyaltyAmtFromExpiryTable(contactsLoyaltyObj, balanceToSub);
											}
										}
										} catch (Exception e) {
											logger.error("Exception ::",e);
										}
										updateLoyaltyData(contactsLoyaltyObj);
										addTbId.setValue(Constants.STRING_NILL);
										ltyPwdTbSubId.setText("");
										subLbId.setSelectedIndex(0);
										MessageUtil.setMessage( StringUtils.capitalize(displayLabel) +" subtracted successfully.", "blue");
										subTb2Id.setText(Constants.STRING_NILL);
										storeNoTbId.setText(Constants.STRING_NILL);
										receiptNoTbId.setText(Constants.STRING_NILL);
										receiptAmtTbId.setText(Constants.STRING_NILL);
										}
									else{
										MessageUtil.setMessage("Transaction was not successful.", "red");
										logger.error("Unable to perform adjustments because Reward Flag is G");
										ltyPwdTbSubId.setText("");
										addTbId.setText("");
										subTb2Id.setText(Constants.STRING_NILL);
										storeNoTbId.setText(Constants.STRING_NILL);
										receiptNoTbId.setText(Constants.STRING_NILL);
										receiptAmtTbId.setText(Constants.STRING_NILL);
										return;
									}
								}
							}//
							else {
								MessageUtil.setMessage("Please enter valid  "+displayLabel+".", "red");
								ltyPwdTbSubId.setText("");
								addTbId.setText("");
								subTb2Id.setText(Constants.STRING_NILL);
								storeNoTbId.setText(Constants.STRING_NILL);
								receiptNoTbId.setText(Constants.STRING_NILL);
								receiptAmtTbId.setText(Constants.STRING_NILL);
							}
						}

					} //Active
					else {
						Messagebox.show("Please select a contact with active program & membership status.", "Error" ,Messagebox.OK, 
								Messagebox.ERROR);
						ltyPwdTbSubId.setText("");
						addTbId.setText("");
						subTb2Id.setText(Constants.STRING_NILL);
						storeNoTbId.setText(Constants.STRING_NILL);
						receiptNoTbId.setText(Constants.STRING_NILL);
						receiptAmtTbId.setText(Constants.STRING_NILL);
					}
				}// loyaltyObj is null
				else{

					Messagebox.show("Please look-up membership before performing this action.", "Error" ,Messagebox.OK, 
							Messagebox.ERROR);
					ltyPwdTbSubId.setText("");
					addTbId.setText("");
					subTb2Id.setText(Constants.STRING_NILL);
					storeNoTbId.setText(Constants.STRING_NILL);
					receiptNoTbId.setText(Constants.STRING_NILL);
					receiptAmtTbId.setText(Constants.STRING_NILL);
					return;
				}
			
		}else{
			MessageUtil.setMessage("Please enter the correct password", "color:red", "TOP");
			ltyPwdTbSubId.setText("");
			addTbId.setText("");
			return;
		}
			LoyaltyProgram loyaltyProgram = loyaltyProgramDao.findById(contactsLoyaltyObj.getProgramId());
			
			LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
			Users user=currUser;
			ContactsLoyalty contactsLoyalty=contactsLoyaltyObj;
			Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
			if (contact != null && (contact.getEmailId() != null || 
					(user.isEnableSMS() && contactsLoyalty.getMobilePhone() != null)) ) {
				LoyaltyAutoComm autoComm = getLoyaltyAutoComm(contactsLoyaltyObj.getProgramId());
				if(autoComm != null ){
					if(autoComm.getAdjustmentAutoEmailTmplId()!=null ){
					
						if (contact.getEmailId() != null) {
							autoCommGen.sendAdjustmentTemplate(autoComm.getAdjustmentAutoEmailTmplId(),
									Constants.STRING_NILL + contactsLoyalty.getCardNumber(), contactsLoyalty.getCardPin(), user,
									contact.getEmailId(), contact.getFirstName(), contact.getContactId(),
									contactsLoyalty.getLoyaltyId(),receiptAmount);
						}
					}
					
					if (autoComm.getAdjustmentAutoSmsTmplId()!=null && user.isEnableSMS() 
							&& contactsLoyalty.getMobilePhone() != null) {
						autoCommGen.sendAdjustmentSMSTemplate(autoComm.getAdjustmentAutoSmsTmplId(), user,
								contactsLoyalty.getContact().getContactId(), contactsLoyalty.getLoyaltyId(),
								contactsLoyalty.getMobilePhone(),receiptAmount);
					}
					
				}
						
				
			
				
			}
			
			
			
			
			userActivitylogAdminUser(substratAmount, contactsLoyaltyObj.getCardNumber(),loyaltyProgram.getProgramName());
		} catch (WrongValueException e) {
			logger.error("Exception ::",e);
		} catch (NumberFormatException e) {
			logger.error("Exception ::",e);
		}catch(Exception e){
			logger.error("Exception ",e);
		}
		
		logger.debug("<<<<<<<<<<<<< completed onClick$subtractBtnId ");
		}
	}// onClick$subtractBtnId


	private void deductHoldAmount(ContactsLoyalty loyalty, double balanceToSub) {

		try {
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			List<LoyaltyTransactionChild> transList = null; 
			Iterator<LoyaltyTransactionChild> iterList = null;
			LoyaltyTransactionChild loyaltyTransactionChild = null;
			double remAmount = balanceToSub;

			do{
				transList = loyaltyTransactionChildDao.fetchHoldAmtTrans(loyalty.getLoyaltyId(), 100, loyalty.getUserId());
				if(transList == null || remAmount <= 0) break;
				iterList = transList.iterator();

				while(iterList.hasNext()){
					loyaltyTransactionChild = iterList.next();

					if(loyaltyTransactionChild.getHoldAmount() == null || loyaltyTransactionChild.getHoldAmount() <= 0){ 
						logger.info("WRONG TRANSACTION FETCHED...");
						continue;
					}
					else if(loyaltyTransactionChild.getHoldAmount() < remAmount){
						logger.info("subtracted hold amount = "+loyaltyTransactionChild.getHoldAmount());
						remAmount = remAmount - loyaltyTransactionChild.getHoldAmount().doubleValue();
						if(loyaltyTransactionChild.getDescription2() != null){
							loyaltyTransactionChild.setDescription2(loyaltyTransactionChild.getDescription2()+Constants.ADDR_COL_DELIMETER+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+":"+loyaltyTransactionChild.getHoldAmount());
						}else{
							loyaltyTransactionChild.setDescription2(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+":"+loyaltyTransactionChild.getHoldAmount());
						}
						loyaltyTransactionChild.setHoldAmount(0.0);
						//loyaltyTransactionChildDao.saveOrUpdate(loyaltyTransactionChild);
						loyaltyTransactionChildDaoForDML.saveOrUpdate(loyaltyTransactionChild);
						logger.info("Hold Amount deducted..."+loyaltyTransactionChild.getHoldAmount().doubleValue());
						continue;

					}
					else if(loyaltyTransactionChild.getHoldAmount() >= remAmount){
						logger.info("subtracted loyalty amount = "+loyaltyTransactionChild.getHoldAmount());
						loyaltyTransactionChild.setHoldAmount(loyaltyTransactionChild.getHoldAmount() - remAmount);
						if(loyaltyTransactionChild.getDescription2() != null){
							loyaltyTransactionChild.setDescription2(loyaltyTransactionChild.getDescription2()+Constants.ADDR_COL_DELIMETER+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+":"+remAmount);
						}else{
							loyaltyTransactionChild.setDescription2(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+":"+remAmount);
						}
						remAmount = 0; 
						//loyaltyTransactionChildDao.saveOrUpdate(loyaltyTransactionChild);
						loyaltyTransactionChildDaoForDML.saveOrUpdate(loyaltyTransactionChild);
						logger.info("Hold Amount deducted..."+remAmount);
						break;
					}

				}
				transList = null;

			}while(remAmount > 0);
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
	}
	
	private void deductHoldPoints(ContactsLoyalty loyalty, long balanceToSub) {
		
		try {
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			List<LoyaltyTransactionChild> transList = null; 
			Iterator<LoyaltyTransactionChild> iterList = null;
			LoyaltyTransactionChild loyaltyTransactionChild = null;
			long remPoints = balanceToSub;

			do{
				transList = loyaltyTransactionChildDao.fetchHoldPtsTrans(loyalty.getLoyaltyId(), 100, loyalty.getUserId());
				if(transList == null || remPoints <= 0) break;
				iterList = transList.iterator();

				while(iterList.hasNext()){
					loyaltyTransactionChild = iterList.next();

					if(loyaltyTransactionChild.getHoldPoints() == null || loyaltyTransactionChild.getHoldPoints() <= 0){ 
						logger.info("WRONG  TRANSACTION FETCHED...");
						continue;
					}
					else if(loyaltyTransactionChild.getHoldPoints() < remPoints){
						logger.info("subtracted hold points = "+loyaltyTransactionChild.getHoldPoints());
						remPoints = remPoints - loyaltyTransactionChild.getHoldPoints().longValue();
						if(loyaltyTransactionChild.getDescription2() != null){
							loyaltyTransactionChild.setDescription2(loyaltyTransactionChild.getDescription2()+Constants.ADDR_COL_DELIMETER+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+":"+loyaltyTransactionChild.getHoldPoints());
						}else{
							loyaltyTransactionChild.setDescription2(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+":"+loyaltyTransactionChild.getHoldPoints());
						}
						loyaltyTransactionChild.setHoldPoints(0.0);
						//loyaltyTransactionChildDao.saveOrUpdate(loyaltyTransactionChild);
						loyaltyTransactionChildDaoForDML.saveOrUpdate(loyaltyTransactionChild);
						continue;

					}
					else if(loyaltyTransactionChild.getHoldPoints() >= remPoints){
						logger.info("subtracted hold points = "+loyaltyTransactionChild.getHoldPoints());
						loyaltyTransactionChild.setHoldPoints(loyaltyTransactionChild.getHoldPoints() - remPoints);
						if(loyaltyTransactionChild.getDescription2() != null){
							loyaltyTransactionChild.setDescription2(loyaltyTransactionChild.getDescription2()+Constants.ADDR_COL_DELIMETER+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+":"+remPoints);
						}else{
							loyaltyTransactionChild.setDescription2(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+":"+remPoints);
						}
						remPoints = 0; 
						//loyaltyTransactionChildDao.saveOrUpdate(loyaltyTransactionChild);
						loyaltyTransactionChildDaoForDML.saveOrUpdate(loyaltyTransactionChild);
						break;
					}

				}
				transList = null;
			}while(remPoints > 0);
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
	}
	
	private void displayFinalHoldBalances(ContactsLoyalty ltyObj) {
		
		//HOLD BALANCE CURRENCY
		if(ltyObj.getHoldAmountBalance() != null && !ltyObj.getHoldAmountBalance().toString().isEmpty()  && ltyObj.getHoldAmountBalance() != 0.0
				&& ltyObj.getHoldPointsBalance() != null &&  !ltyObj.getHoldPointsBalance().toString().isEmpty() && ltyObj.getHoldPointsBalance() != 0.0) {
			holdBalLbId.setVisible(true);
			holdBalValLbId.setVisible(true);
			holdBalValLbId.setValue(Utility.getAmountInUSFormat(ltyObj.getHoldAmountBalance())+" & "+
									ltyObj.getHoldPointsBalance().intValue()+" Points");
			finalHoldBalDivId.setVisible(true);
			finalHoldBalanceLblId.setValue(Utility.getAmountInUSFormat(ltyObj.getHoldAmountBalance())+" & "+
									ltyObj.getHoldPointsBalance().intValue()+" Points");
		}else if(ltyObj.getHoldAmountBalance() != null && !ltyObj.getHoldAmountBalance().toString().isEmpty() && ltyObj.getHoldAmountBalance() != 0.0
				&& (ltyObj.getHoldPointsBalance() == null || ltyObj.getHoldPointsBalance().toString().isEmpty() || ltyObj.getHoldPointsBalance() == 0.0)) {
			holdBalLbId.setVisible(true);
			holdBalValLbId.setVisible(true);
			holdBalValLbId.setValue(Utility.getAmountInUSFormat(ltyObj.getHoldAmountBalance()));
			finalHoldBalDivId.setVisible(true);
			finalHoldBalanceLblId.setValue(Utility.getAmountInUSFormat(ltyObj.getHoldAmountBalance()));
		}else if( ltyObj.getHoldPointsBalance() != null &&  !ltyObj.getHoldPointsBalance().toString().isEmpty() && ltyObj.getHoldPointsBalance() != 0.0
				 && ( ltyObj.getHoldAmountBalance() == null || ltyObj.getHoldAmountBalance().toString().isEmpty() || ltyObj.getHoldAmountBalance() == 0.0)) {
			holdBalLbId.setVisible(true);
			holdBalValLbId.setVisible(true);
			holdBalValLbId.setValue(ltyObj.getHoldPointsBalance().intValue()+" Points");
			finalHoldBalDivId.setVisible(true);
			finalHoldBalanceLblId.setValue(ltyObj.getHoldPointsBalance().intValue()+" Points");
		}
		
	}
	public void onChange$timeDurLbId(){
		logger.debug(">>>>>>>>>>>>> entered in onChange$timeDurLbId");
		if(contactsLoyaltyObj ==  null){
			MessageUtil.setMessage("Please look-up membership before performing this action.", "red");
			return;
		}
		getDateValues();
		//Pagination 
		displayPagination(contactsLoyaltyObj);
		logger.debug("<<<<<<<<<<<<< completed onChange$timeDurLbId ");
	}//onChange$timeDurLbId



	public void onSelect$pageSizeLbId(){
		logger.debug(">>>>>>>>>>>>> entered in onSelect$pageSizeLbId");
		if(contactsLoyaltyObj ==  null){

			MessageUtil.setMessage("Please look-up membership before performing this action.", "red");
			return;
		}

		LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
		try {
			int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());


			//int totalSize = ltyPrgmService.getAllTransactionsByMembershipnumberCount(currUser.getUserId(), contactsLoyaltyObj.getLoyaltyId(), MyCalendar.calendarToString(startDate, null), MyCalendar.calendarToString(endDate, null));

			//loyaltyListBottomPagingId.setTotalSize(totalSize);
			loyaltyListBottomPagingId.setPageSize(pageSize);
			loyaltyListBottomPagingId.setActivePage(0);
			loyaltyListBottomPagingId.addEventListener("onPaging", this);
			drawTransList();
			displaybrdrLOut.setHeight((700+heightForTrans+transferListLbId.getChildren().size()*30)+"px");
			LoyaltyCustomerLookUpAndRedeemWinId.setHeight((900+heightForTrans+transferListLbId.getChildren().size()*30)+"px");

		}catch (Exception e) {
			logger.error("Exception");
		} 
		logger.debug("<<<<<<<<<<<<< completed onSelect$pageSizeLbId ");
	}//onSelect$pageSizeLbId
	
	public void onSelect$addLbId2(){
			if(addLbId2.getSelectedItem().getLabel().toString().equalsIgnoreCase("Lifetime Purchase Value")){
				addLbId.getItemAtIndex(1).setVisible(false);
			}else addLbId.getItemAtIndex(1).setVisible(true);
		
		listItemAmt.setVisible(true);
		listItemHoldAmt.setVisible(true);
		listItemPts.setVisible(true);
		listItemHoldPts.setVisible(true);
		LoyaltyProgramTierDao loyaltyProgramTierDao=null;
		try {
			loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("e==>"+e);
		}
		if(contactsLoyaltyObj!=null) {
		LoyaltyProgramTier tier = loyaltyProgramTierDao.findByTierId(contactsLoyaltyObj.getProgramTierId());
		if(tier!=null) {
			if(tier.getEarnType().equalsIgnoreCase(OCConstants.LOYALTY_POINTS)) {
				listItemHoldAmt.setVisible(false);
				if((tier.getConversionType() == null || tier.getConversionType().isEmpty())/*|| (!tier.getConversionType().trim().isEmpty() &&
						!tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO) &&
						!tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_ONDEMAND))*/
						&& tier.getConvertFromPoints() == null && tier.getConvertToAmount() == null){
					listItemAmt.setVisible(false);
				}else{
					listItemAmt.setVisible(true);
				}
			}
			else {
				listItemPts.setVisible(false);
				listItemHoldPts.setVisible(false);
			}
		}  
		}
		
		
	}
	
	public void onSelect$addLbId(){
		if(addLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Subtract")){
			addLbId2.getItemAtIndex(2).setVisible(false);
		}else addLbId2.getItemAtIndex(2).setVisible(true); 
		
	}


	@Override
	public void onEvent(Event event) throws Exception {
		super.onEvent(event);
		logger.debug(">>>>>>>>>>>>> entered in onEvent");
		if(event == null) return;

		if(event.getTarget() instanceof A) {
			A tempAnch = (A)event.getTarget();
			LoyaltyTransactionChild transactionChild = (LoyaltyTransactionChild)tempAnch.getAttribute("loyaltyTransactionChild");

			if(transactionChild == null){
				Messagebox.show("No purchase details found.","Information",Messagebox.OK,Messagebox.INFORMATION);
				return;
			}
			if(transactionChild.getTransactionType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT)){
			latestDetailsSubWinId.doHighlighted();
			openSubWindDiv(latestDetailsSubWinId$descDivId);
			latestDetailsSubWinId.setVisible(true);
			latestDetailsSubWinId.setWidth("500px"); 
			latestDetailsSubWinId.setHeight("100px");
			latestDetailsSubWinId.setTitle(Constants.STRING_NILL);
			latestDetailsSubWinId.setTitle("Reason for Adjusment");
			latestDetailsSubWinId$descLBId.setStyle("font:bold");
			latestDetailsSubWinId$descLBId.setValue(transactionChild.getDescription2()!=null && !transactionChild.getDescription2().isEmpty()?transactionChild.getDescription2(): "No Reason !");
			} 

			else{
		    if(transactionChild.getDocSID() == null){
				Messagebox.show("No purchase details found.","Information",Messagebox.OK,Messagebox.INFORMATION);
				return;
			}

			onClickViewLoyaltyDetailsAnchId(transactionChild.getDocSID());
			}
		}//onClick of view purchase


		if(event.getTarget() instanceof Paging) {
			if(contactsLoyaltyObj == null){
				MessageUtil.setMessage("Please look-up membership before performing this action.", "red");
				return;
			}
			LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
			Paging paging = (Paging)event.getTarget();

			int desiredPage = paging.getActivePage();
			this.loyaltyListBottomPagingId.setActivePage(desiredPage);
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;

			drawTransList();


		}//if Paging

		if(event.getTarget() instanceof Label) {
			Label label =(Label) event.getTarget();
			if(label == null || label.getValue()==null){
				MessageUtil.setMessage("Please look-up membership before performing this action.", "red");
				return;
			}
			openMembership(label.getValue());

		}
		
		logger.debug("<<<<<<<<<<<<< completed onEvent ");
	}//onEvent

	private Map<Long, LoyaltyProgramTier> fetchTiersMap(String tierIdStr) {
		
		Map<Long, LoyaltyProgramTier> tierMap = null;
		
		
		try{
			LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
			List<LoyaltyProgramTier> listOfTiers = loyaltyProgramTierDao.fetchTiersBytierIdStr(tierIdStr);
			
			if(listOfTiers != null && listOfTiers.size() > 0){
				tierMap = new HashMap<Long, LoyaltyProgramTier>();
				
				for(LoyaltyProgramTier tier : listOfTiers){
					tierMap.put(tier.getTierId(), tier);
				}
			}
			
		}catch(Exception e){
			logger.error("Exception in fetching tiers...",e);
		}
		return tierMap;
	}
	
	
	
	/**
	 * This method draws all the Transfer Details
	 * @param transferList
	 */
	private void displayTransferList(ContactsLoyalty contactsLoyalty) {
		
		try {
		MessageUtil.clearMessage();
		int count =  transferListLbId.getItemCount();
		for(; count>0; count--) {
				transferListLbId.removeItemAt(count-1);
		}
		//System.gc();
		
		DecimalFormat f = new DecimalFormat("#0.00");
		transferedOrNewCardLblId.setValue("Transferred Card(s) Details");
		if(transferList == null){
			/*transferLblDivId.setVisible(false);
			transferTableDivId.setVisible(false);*/
			return;
		}else if(transferList.size() > 0) {
			Listitem listItem = null;
			Listcell listCell = null;
			String tierIdStr = Constants.STRING_NILL;
			String cardSetIdStr = Constants.STRING_NILL;
			for(ContactsLoyalty eachTransferMem:transferList){
				// need to ask if tier id is null 
				if(tierIdStr.length()>0 && eachTransferMem.getProgramTierId()!=null){
					tierIdStr += ","+eachTransferMem.getProgramTierId();
				}else if(eachTransferMem.getProgramTierId()!=null){
					tierIdStr += eachTransferMem.getProgramTierId();
				}
				if(cardSetIdStr.length()>0 && eachTransferMem.getCardSetId()!=null){
					cardSetIdStr += ","+eachTransferMem.getCardSetId();
				}else if(eachTransferMem.getCardSetId()!=null){
					cardSetIdStr += eachTransferMem.getCardSetId();
				}
				
				
			}
			Map<String, Object> cardSetMap = new HashMap<String, Object>();
			Map<Long, LoyaltyProgramTier> tierMap = new HashMap<Long, LoyaltyProgramTier>();
			if(cardSetIdStr.length()>0){
			LoyaltyCardSetDao loyaltyCardSetDao = (LoyaltyCardSetDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
			cardSetMap = loyaltyCardSetDao.fetchCardSetsByCardSetIdStr(cardSetIdStr);
			}
		
			if(tierIdStr.length()>0)tierMap = fetchTiersMap(tierIdStr);
			for(ContactsLoyalty eachTransferMem:transferList){
				listItem = new Listitem();
				listCell = new Listcell();
				//listCell.setStyle("padding-left:10px;");
				listCell.setLabel(eachTransferMem.getCardNumber().toString());
				listCell.setTooltiptext(eachTransferMem.getCardNumber().toString());
				listCell.setParent(listItem);

				listCell = new Listcell();
				listCell.setStyle("padding-left:10px;");
				listCell.setLabel(eachTransferMem.getMembershipStatus());
				listCell.setTooltiptext(eachTransferMem.getMembershipStatus());
				listCell.setParent(listItem);

				listCell = new Listcell();
				listCell.setStyle("padding-left:10px;");
				listCell.setLabel((String) cardSetMap.get(eachTransferMem.getCardSetId()));
				listCell.setTooltiptext((String) cardSetMap.get(eachTransferMem.getCardSetId()));
				listCell.setParent(listItem);

				listCell = new Listcell();
				listCell.setStyle("padding-left:10px;");
				LoyaltyProgramTier currentTierObj = tierMap.get(eachTransferMem.getProgramTierId());
				listCell.setLabel(currentTierObj!=null?currentTierObj.getTierName()+" ("+currentTierObj.getTierType()+")":"--");
				listCell.setTooltiptext(currentTierObj!=null?currentTierObj.getTierName()+" ("+currentTierObj.getTierType()+")":"--");
				listCell.setParent(listItem);

				listCell = new Listcell();
				listCell.setStyle("padding-left:10px;");
				/*listCell.setLabel(f.format(eachTransferMem.getGiftcardBalance()));
				listCell.setTooltiptext(f.format(eachTransferMem.getGiftcardBalance()));*/
				listCell.setLabel(eachTransferMem.getGiftcardBalance() != null ? f.format(eachTransferMem.getGiftcardBalance()):"0.00");
				listCell.setTooltiptext(eachTransferMem.getGiftcardBalance() != null ? f.format(eachTransferMem.getGiftcardBalance()):"0.00");
				listCell.setParent(listItem);

				listCell = new Listcell();
				listCell.setStyle("padding-left:10px;");
				//listCell.setLabel(eachTransferMem.getLoyaltyBalance().longValue()+"");
				//listCell.setTooltiptext(eachTransferMem.getLoyaltyBalance().longValue()+"");
				listCell.setLabel(eachTransferMem.getLoyaltyBalance() != null ? eachTransferMem.getLoyaltyBalance().longValue()+"":"--");
				listCell.setTooltiptext(eachTransferMem.getLoyaltyBalance() != null ? eachTransferMem.getLoyaltyBalance().longValue()+"":"--");
				listCell.setParent(listItem);

				listCell = new Listcell();
				listCell.setStyle("padding-left:10px;");
				listCell.setLabel(MyCalendar.calendarToString(eachTransferMem.getTransferedOn(), MyCalendar.FORMAT_YEARTODATE, clientTimeZone));
				listCell.setTooltiptext(MyCalendar.calendarToString(eachTransferMem.getTransferedOn(), MyCalendar.FORMAT_YEARTODATE, clientTimeZone));
				listCell.setParent(listItem);
				listItem.setParent(transferListLbId);
				
			}
			
		}else if(contactsLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
			transferedOrNewCardLblId.setValue("New Card Details");
			LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService(); 
			ContactsLoyalty newCardDetails = ltyPrgmService.getContactLtyById(contactsLoyalty.getTransferedTo());
			LoyaltyCardSetDao loyaltyCardSetDao = (LoyaltyCardSetDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
			String cardSetName = loyaltyCardSetDao.findByCardSetId(newCardDetails.getCardSetId()).getCardSetName();
			LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
			LoyaltyProgramTier currentTierObj  = loyaltyProgramTierDao.getTierById(newCardDetails.getProgramTierId());
			
			Listitem listItem = new Listitem();
			Listcell listCell = new Listcell();
			//listCell.setStyle("padding-left:10px;");
			listCell.setLabel(newCardDetails.getCardNumber().toString());
			listCell.setTooltiptext(newCardDetails.getCardNumber().toString());
			listCell.setParent(listItem);
			listItem.setParent(transferListLbId);
			listCell = new Listcell();
			listCell.setStyle("padding-left:10px;");
			listCell.setLabel(newCardDetails.getMembershipStatus());
			listCell.setTooltiptext(newCardDetails.getMembershipStatus());
			listCell.setParent(listItem);
			listItem.setParent(transferListLbId);
			listCell = new Listcell();
			listCell.setStyle("padding-left:10px;");
			listCell.setLabel(cardSetName);
			listCell.setTooltiptext(cardSetName);
			listCell.setParent(listItem);
			listItem.setParent(transferListLbId);
			listCell = new Listcell();
			listCell.setStyle("padding-left:10px;");
			listCell.setLabel(currentTierObj!=null?currentTierObj.getTierName()+" ("+currentTierObj.getTierType()+")":"--");
			listCell.setTooltiptext(currentTierObj!=null?currentTierObj.getTierName()+" ("+currentTierObj.getTierType()+")":"--");
			listCell.setParent(listItem);
			listItem.setParent(transferListLbId);
			listCell = new Listcell();
			listCell.setStyle("padding-left:10px;");
			/*listCell.setLabel(f.format(newCardDetails.getGiftcardBalance()));
			listCell.setTooltiptext(f.format(newCardDetails.getGiftcardBalance()));*/
			listCell.setLabel(newCardDetails.getGiftcardBalance() != null ? f.format(newCardDetails.getGiftcardBalance()):"0.00");
			listCell.setTooltiptext(newCardDetails.getGiftcardBalance() != null ? f.format(newCardDetails.getGiftcardBalance()):"0.00");
			listCell.setParent(listItem);
			listItem.setParent(transferListLbId);
			listCell = new Listcell();
			listCell.setStyle("padding-left:10px;");
			/*listCell.setLabel(newCardDetails.getLoyaltyBalance().longValue()+"");
			listCell.setTooltiptext(newCardDetails.getLoyaltyBalance().longValue()+"");*/
			listCell.setLabel(newCardDetails.getLoyaltyBalance() != null ? newCardDetails.getLoyaltyBalance().longValue()+"":"--");
			listCell.setTooltiptext(newCardDetails.getLoyaltyBalance() != null ? newCardDetails.getLoyaltyBalance().longValue()+"":"--");
			listCell.setTooltiptext(newCardDetails.getLoyaltyBalance().longValue()+"");
			listCell.setParent(listItem);
			listItem.setParent(transferListLbId);
			listCell = new Listcell();
			listCell.setStyle("padding-left:10px;");
			listCell.setLabel(MyCalendar.calendarToString(contactsLoyalty.getTransferedOn(), MyCalendar.FORMAT_YEARTODATE, clientTimeZone));
			listCell.setTooltiptext(MyCalendar.calendarToString(contactsLoyalty.getTransferedOn(), MyCalendar.FORMAT_YEARTODATE, clientTimeZone));
			listCell.setParent(listItem);
			listItem.setParent(transferListLbId);
			
		}
		logger.debug("<<<<<<<<<<<<< completed displayTransferList ");
		} catch (Exception e) {
			logger.error("Exception ocured while displaying transfer list :: ",e);
		}
		/*if(transferListLbId.getItemCount()==0){
			transferLblDivId.setVisible(false);
			transferTableDivId.setVisible(false);
		}else{
			transferLblDivId.setVisible(true);
			transferTableDivId.setVisible(true);
		}*/
	}//redrawTransList

	/**
	 * This method draws all the Transaction
	 * @param transList
	 */
	private void redrawTransList(List<LoyaltyTransactionChild> transList, boolean isTransferedCard) {
		logger.debug(">>>>>>>>>>>>> entered in redrawTransList");
		MessageUtil.clearMessage();
				
		if(transList == null) return;
		
		if(transList.size() > 0) {
			if(isTransferedCard){
				heightForTrans +=30;
				Listgroup listGroup = new Listgroup("Transactions with card#: "+transList.get(0).getMembershipNumber());
				listGroup.setParent(transListLbId);
				listGroup.setOpen(true);
			}
			for (LoyaltyTransactionChild loyaltyTransactionChild : transList) {
				heightForTrans +=75;
				settingLoyaltyTransactionRow(loyaltyTransactionChild);
			}
		}
		logger.debug("<<<<<<<<<<<<< completed redrawTransList ");
	}//redrawTransList

	private void settingLoyaltyTransactionRow(LoyaltyTransactionChild loyaltyTransactionChild) {
		logger.debug(">>>>>>>>>>>>> entered in settingLoyaltyTransactionRow");
		DecimalFormat f = new DecimalFormat("#0.00");
		LoyaltyProgramService loyaltyProgramService = new LoyaltyProgramService();
		Listitem listItem = new Listitem();
		Listcell listCell = new Listcell();
		
		listCell.setLabel(MyCalendar.calendarToString(loyaltyTransactionChild.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR,clientTimeZone));
		listCell.setStyle("padding-left:10px;");
		listCell.setTooltiptext(MyCalendar.calendarToString(loyaltyTransactionChild.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR,clientTimeZone));
		listCell.setParent(listItem);
		logger.info("loyaltyTransactionChild.getDocSID()....................."+loyaltyTransactionChild.getDocSID());
		//Setting receipt Number
		String receiptNumber = null;
		String receiptNo = loyaltyTransactionChild.getReceiptNumber();
		if(((receiptNo == null || receiptNo.isEmpty()) && (loyaltyTransactionChild.getDocSID() != null && !loyaltyTransactionChild.getDocSID().isEmpty()) && (loyaltyTransactionChild.getReceiptNumber() == null || loyaltyTransactionChild.getReceiptNumber().isEmpty())) ){
			receiptNumber = loyaltyProgramService.getReceiptNumberByDocsid(loyaltyTransactionChild.getDocSID(), currUser.getUserId());
			if(receiptNumber == null){
				receiptNo ="--";
			}
			else{
				receiptNo = receiptNumber+Constants.STRING_NILL;
			}
		}else if(loyaltyTransactionChild.getReceiptNumber() != null && !loyaltyTransactionChild.getReceiptNumber().isEmpty() ){
			receiptNo = loyaltyTransactionChild.getReceiptNumber();
			
		}
		listCell = new Listcell();
		listCell.setLabel(receiptNo);
		listCell.setStyle("padding-left:10px;");
		listCell.setTooltiptext(receiptNo);
		listCell.setParent(listItem);
		//Setting Subsidiary No.
		listCell = new Listcell();
		listCell.setStyle("padding-left:10px;");
		listCell.setLabel(loyaltyTransactionChild.getSubsidiaryNumber()!=null?fetchSubsidiaryName(loyaltyTransactionChild.getSubsidiaryNumber()):"--");
		listCell.setTooltiptext(loyaltyTransactionChild.getSubsidiaryNumber()!=null?fetchSubsidiaryName(loyaltyTransactionChild.getSubsidiaryNumber()):"--");
		listCell.setParent(listItem);
		//Setting Store Number
		listCell = new Listcell();
		listCell.setStyle("padding-left:10px;");
		listCell.setLabel(loyaltyTransactionChild.getStoreNumber()!=null?fetchStoreName(loyaltyTransactionChild.getStoreNumber()):"--");
		listCell.setTooltiptext(loyaltyTransactionChild.getStoreNumber()!=null?fetchStoreName(loyaltyTransactionChild.getStoreNumber()):"--");
		listCell.setParent(listItem);
		//Setting Transaction type
		listCell = new Listcell();
		listCell.setStyle("padding-left:10px;");
		//String transType = loyaltyTransactionChild.getTransactionType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_ISSUANCE)?loyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PURCHASE) || loyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD)?"Loyalty Issuance":"Gift Issuance":loyaltyTransactionChild.getTransactionType();
		String transType = loyaltyTransactionChild.getTransactionType();
        if(transType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_ISSUANCE)){
            if(loyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PURCHASE)) {
                transType= OCConstants.LOYALTY_ISSUANCE;
            }else if(loyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_GIFT)){
                transType= OCConstants.LOYALTY_GIFT_ISSUANCE;
            }else if(loyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD)){
                transType= OCConstants.LOYALTY_TYPE_REWARD;
            }
        }
        if(transType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_RETURN)){//APP-2081
        	if(loyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL)) {
        		transType= OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL_UI;
        	}
        	else if(loyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL)) {
        		transType= OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL_UI;
        	}
        	
        }
		listCell.setLabel(transType);
		listCell.setTooltiptext(transType);
		listCell.setParent(listItem);
		//Setting Entered Amount
		listCell = new Listcell();
		listCell.setStyle("padding-left:10px;");
		
		/*double enteredAmt = loyaltyTransactionChild.getEnteredAmount() == null ? 0.0 : loyaltyTransactionChild.getEnteredAmount();
		double excludeAmt = loyaltyTransactionChild.getExcludedAmount() == null ? 0.0 : loyaltyTransactionChild.getExcludedAmount();*/
		/*if(loyaltyTransactionChild.getEarnedPoints()==null){
		listCell.setLabel(Utility.getAmountInUSFormat(enteredAmt-excludeAmt));
		listCell.setTooltiptext(Utility.getAmountInUSFormat(enteredAmt-excludeAmt));
		listCell.setParent(listItem);
		}
		else{
			int a=(int)(enteredAmt-excludeAmt);
			listCell.setLabel(Integer.toString((int)(a)));
			//listCell.setLabel(Double.toString(enteredAmt-excludeAmt));
			listCell.setTooltiptext(Double.toString(a));
			listCell.setParent(listItem);
		}*/
		/*String earnType=loyaltyTransactionChild.getEarnType()!=null ? loyaltyTransactionChild.getEarnType():"";
		String enteredAmtType=loyaltyTransactionChild.getEnteredAmountType()!=null ? loyaltyTransactionChild.getEnteredAmountType() : "";
	     String enterAmount=""; 
	    if(enteredAmtType.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD) || enteredAmtType.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_SUB))
	           if(earnType.equalsIgnoreCase(OCConstants.LOYALTY_POINTS))
	    	       enterAmount = (enteredAmt-excludeAmt)!=0 ?Integer.valueOf ((int) (enteredAmt-excludeAmt))+" Points":"";
	          else
	    	     enterAmount = (enteredAmt-excludeAmt)!=0 ? "$ "+f.format(enteredAmt-excludeAmt):"$ 0.00"; 
	 
       else if(enteredAmtType.contains(OCConstants.LOYALTY_POINTS)|| earnType.equalsIgnoreCase(OCConstants.LOYALTY_POINTS))
		  enterAmount = (enteredAmt-excludeAmt)!=0 ? Integer.valueOf((int) (enteredAmt-excludeAmt))+" Points":"";			
	   else
		  enterAmount = (enteredAmt-excludeAmt)!=0 ? "$ "+f.format(enteredAmt-excludeAmt):"$ 0.00";
	    
		listCell.setLabel(enterAmount);
		listCell.setTooltiptext(enterAmount);*/
		
		if (((loyaltyTransactionChild.getEarnType() != null && ((String) loyaltyTransactionChild.getEarnType()).equalsIgnoreCase(OCConstants.LOYALTY_POINTS))
                || (loyaltyTransactionChild.getEnteredAmountType() != null
                && loyaltyTransactionChild.getEnteredAmountType().toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTSREDEEM)))
                && (!((String) loyaltyTransactionChild.getTransactionType()).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE) 
                ||(loyaltyTransactionChild.getTransactionType()).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE) 
                && (loyaltyTransactionChild.getEnteredAmountType() != null
                && loyaltyTransactionChild.getEnteredAmountType().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REWARD)))
                && !((String) loyaltyTransactionChild.getTransactionType()).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)) {
			
			Double enteredAmt = loyaltyTransactionChild.getEnteredAmount() == null ? 0 : Double.parseDouble(loyaltyTransactionChild.getEnteredAmount().toString());
			String valueEntered = ((String) loyaltyTransactionChild.getTransactionType()).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT) ? 
					(loyaltyTransactionChild.getEnteredAmountType() != null ? ((String) loyaltyTransactionChild.getEnteredAmountType()).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD) ? "+ "+ enteredAmt.longValue()
					: "- " + enteredAmt.longValue()
					: "0")
					: "" + enteredAmt.longValue();
			listCell.setLabel(valueEntered + " Points");
			listCell.setTooltiptext(valueEntered + " Points");
			
		}else{			

			double enteredAmt = loyaltyTransactionChild.getEnteredAmount() == null ? 0.0 : Double.parseDouble(loyaltyTransactionChild.getEnteredAmount().toString());
			double excludeAmt = loyaltyTransactionChild.getExcludedAmount() == null ? 0.0 : Double.parseDouble(loyaltyTransactionChild.getExcludedAmount().toString());
			String valueEntered = ((String) loyaltyTransactionChild.getTransactionType()).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT) ? 
					(loyaltyTransactionChild.getEnteredAmountType() != null ? ((String) loyaltyTransactionChild.getEnteredAmountType()).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD) ? "+ "+ f.format(enteredAmt - excludeAmt)
					: "- " + f.format(enteredAmt - excludeAmt)
					: "0.00")
					: f.format(enteredAmt - excludeAmt);
			if (loyaltyTransactionChild.getEnteredAmount() != null) {
				if(!((String) loyaltyTransactionChild.getTransactionType()).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT)){
				
					listCell.setLabel(valueEntered + " Currency");
					listCell.setTooltiptext(valueEntered + " Currency");
				}else{
					if(loyaltyTransactionChild.getEarnType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_LPV)){
						listCell.setLabel(valueEntered + " LPV");
						listCell.setTooltiptext(valueEntered + " LPV");
					}else if(loyaltyTransactionChild.getEarnType().equalsIgnoreCase("Amount")){
						listCell.setLabel(valueEntered + " Currency");
						listCell.setTooltiptext(valueEntered + " Currency");
					
					}else{
						listCell.setLabel(valueEntered +" "+ loyaltyTransactionChild.getEarnType());
						listCell.setTooltiptext(valueEntered +" "+ loyaltyTransactionChild.getEarnType());
					}
				}
				
				
			} else if (((String) loyaltyTransactionChild.getTransactionType()).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT)
					|| ((String) loyaltyTransactionChild.getTransactionType()).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_BONUS)) {
				/*valueEntered = ((String) loyaltyTransactionChild.getEarnType()).equalsIgnoreCase(OCConstants.LOYALTY_POINTS) ? "0 Points"
						: "0.00 Currency";*/
				if(((String) loyaltyTransactionChild.getEarnType()).equalsIgnoreCase(OCConstants.LOYALTY_POINTS)){
					valueEntered = "0 Points";
				}else if(((String) loyaltyTransactionChild.getEarnType()).equalsIgnoreCase(OCConstants.LOYALTY_TYPE_LPV)){
					valueEntered = "0.00 LPV";
				}else valueEntered ="0.00 Currency";
				
				
				listCell.setLabel(valueEntered);
				listCell.setTooltiptext(valueEntered);
			} else {
				listCell.setLabel("--");
				listCell.setTooltiptext("--");
			}
		
		}
		
		listCell.setParent(listItem);

		//set amount diff
				String amountDiff=Constants.STRING_NILL; 
				amountDiff=loyaltyTransactionChild.getAmountDifference()==null? "0": loyaltyTransactionChild.getAmountDifference();
				listCell=new Listcell();
				listCell.setStyle("padding-left:10px;");
				listCell.setLabel(amountDiff);
				listCell.setTooltiptext(amountDiff);
				listCell.setParent(listItem);
         //setting point diff
                String pointDiff=Constants.STRING_NILL;
                pointDiff=loyaltyTransactionChild.getPointsDifference()!=null?loyaltyTransactionChild.getPointsDifference():"0";
                listCell=new Listcell();
				listCell.setStyle("padding-left:10px;");
				listCell.setLabel(Utility.truncateToInteger(pointDiff));//APP-823
				listCell.setTooltiptext(pointDiff);
				listCell.setParent(listItem);
                logger.info(" point diff "+pointDiff);
		//Setting Balance String
		String balanceStr = Constants.STRING_NILL;
		if(loyaltyTransactionChild.getAmountBalance() != null && !loyaltyTransactionChild.getAmountBalance().toString().isEmpty()
				&& loyaltyTransactionChild.getGiftBalance() != null && !loyaltyTransactionChild.getGiftBalance().toString().isEmpty()) {
			balanceStr = "Gift : " + Utility.getAmountInUSFormat(loyaltyTransactionChild.getGiftBalance()) +" Loyalty : "+ Utility.getAmountInUSFormat(loyaltyTransactionChild.getAmountBalance());
		}else if(loyaltyTransactionChild.getAmountBalance() == null && loyaltyTransactionChild.getGiftBalance() != null
				&& !loyaltyTransactionChild.getGiftBalance().toString().isEmpty()){
			balanceStr = Utility.getAmountInUSFormat(loyaltyTransactionChild.getGiftBalance());
		}else if(loyaltyTransactionChild.getAmountBalance() != null && !loyaltyTransactionChild.getAmountBalance().toString().isEmpty()
				&& loyaltyTransactionChild.getGiftBalance() == null){
			balanceStr = Utility.getAmountInUSFormat(loyaltyTransactionChild.getAmountBalance());
		}else {
			balanceStr = Utility.getAmountInUSFormat(0.00)+Constants.STRING_NILL;
		}
		listCell = new Listcell();
		listCell.setStyle("padding-left:10px;");
		listCell.setLabel(balanceStr);
		listCell.setTooltiptext(balanceStr);
		listCell.setParent(listItem);
		//Setting Points Balance
		listCell = new Listcell();
		listCell.setStyle("padding-left:10px;");
		listCell.setLabel(loyaltyTransactionChild.getPointsBalance() == null ? "0" : loyaltyTransactionChild.getPointsBalance().intValue()+Constants.STRING_NILL);
		listCell.setTooltiptext(loyaltyTransactionChild.getPointsBalance() == null ? Constants.STRING_NILL : loyaltyTransactionChild.getPointsBalance().intValue()+Constants.STRING_NILL);
		listCell.setParent(listItem);
		
		String holdBalanceStr = "";
		//if(loyaltyTransactionChild.getTransactionType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_ISSUANCE) && loyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PURCHASE)){APP - 120
			if(loyaltyTransactionChild.getHoldPoints() != null){
				Double holdBal = 0.0;
			holdBal = loyaltyTransactionChild.getHoldPoints();
			if(loyaltyTransactionChild.getDescription2() != null){
				String transactions[] = loyaltyTransactionChild.getDescription2().split(Constants.ADDR_COL_DELIMETER);
				for(String eachTrx:transactions){
					if(eachTrx.contains(":"))
					holdBal +=Double.valueOf(eachTrx.split(":")[1].trim());
				}
			}
			holdBalanceStr = holdBal !=0 ? holdBal.intValue()+" Points":"";
		}else if(loyaltyTransactionChild.getHoldAmount() != null){
			double holdBal = 0;
			holdBal = loyaltyTransactionChild.getHoldAmount();
			if(loyaltyTransactionChild.getDescription2() != null){
				String transactions[] = loyaltyTransactionChild.getDescription2().split(Constants.ADDR_COL_DELIMETER);
				for(String eachTrx:transactions){
					if(eachTrx.contains(":"))
					holdBal +=Double.valueOf(eachTrx.split(":")[1].trim());
				}
			}
			/*holdBalanceStr = holdBal!=0 ? "$ "+f.format(holdBal):"";*/
			holdBalanceStr = holdBal!=0 ? userCurrencySymbol +f.format(holdBal):"";
		}
		//}
		else {
			holdBalanceStr = "";
		}
		
		listCell = new Listcell();
		listCell.setStyle("padding-left:10px;");
		listCell.setLabel(holdBalanceStr);
		listCell.setTooltiptext(holdBalanceStr);
		listCell.setParent(listItem);
		
		
		
		//Setting Receipt Amount
		
		listCell = new Listcell();
		listCell.setStyle("padding-left:10px;");
		listCell.setLabel(loyaltyTransactionChild.getReceiptAmount() == null ? "0" : loyaltyTransactionChild.getReceiptAmount()+Constants.STRING_NILL);
		listCell.setTooltiptext(loyaltyTransactionChild.getReceiptAmount() == null ? "0" : loyaltyTransactionChild.getReceiptAmount()+Constants.STRING_NILL);
		listCell.setParent(listItem);
		
		
		
		//Setting Anchor 
		listCell = new Listcell();
		listCell.setStyle("padding-left:10px;");
		A anchor=null;
		 if(loyaltyTransactionChild.getTransactionType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT))
		 anchor= new A("View Reason");
		else
		 anchor = new A("View Purchase");
		anchor.setStyle("color:blue; text-decoration: underline;");
		anchor.setAttribute("loyaltyTransactionChild", loyaltyTransactionChild);
		anchor.addEventListener("onClick", this);
		anchor.setParent(listCell);
		listCell.setParent(listItem);

		listItem.setHeight("30px");
		listItem.setValue(loyaltyTransactionChild);
		listItem.setParent(transListLbId);
	logger.debug("<<<<<<<<<<<<< completed settingLoyaltyTransactionRow ");
	}//settingLoyaltyTransactionRow
	/**
	 * on click of Which Items Anchor Id
	 */

	/*public void onClick$whichItemsPurchaseAnchId(){
		logger.debug(">>>>>>>>>>>>> entered in onClick$whichItemsPurchaseAnchId");
		LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
		Contacts contacts=contactsLoyaltyObj.getContact();
		String docSid = (String) whichItemsPurchaseAnchId.getAttribute("docSid");

		if(docSid == null){
			MessageUtil.setMessage("No items found", "red");
			return;
		}

		latestDetailsSubWinId.doHighlighted();
		openSubWindDiv(latestDetailsSubWinId$viewSkuDetailsDivId);

		try {
			latestDetailsSubWinId.doHighlighted();
			latestDetailsSubWinId.setVisible(true);
			latestDetailsSubWinId.setTitle(Constants.STRING_NILL);
			latestDetailsSubWinId.setTitle("Items In Last Purchase");
			latestDetailsSubWinId.setHeight("300px");

			Components.removeAllChildren(latestDetailsSubWinId$viewAllSKURowsId);
			logger.info("contactId"+contacts.getContactId()+"\t userid"+currUser.getUserId());
			List<Object[]> skuList = ltyPrgmService.getLastPurchaseSKUDetailsByDocsid(docSid, currUser.getUserId());
			for (Object[] object : skuList) {
				Row row = new Row();
				row.setParent(latestDetailsSubWinId$viewAllSKURowsId);

				row.appendChild(new Label(object[0]==null?Constants.STRING_NILL:object[0].toString().trim())); //sku
				row.appendChild(new Label(object[1]==null?Constants.STRING_NILL:object[1].toString().trim())); //item category
				row.appendChild(new Label(object[3]==null?Constants.STRING_NILL:object[3].toString().trim())); //quantity
				row.appendChild(new Label(object[2]==null?"$ 0.00":Utility.getAmountInUSFormat(object[2])));//price
			}
		}catch (Exception e) {
			logger.error("Exception while fetching last purchase SKU details.....");
			logger.error("Exception ::" , e);
		}
		logger.debug("<<<<<<<<<<<<< completed onClick$whichItemsPurchaseAnchId");
	}*///onClick$whichItemsPurchaseAnchId

	/**
	 * on click of Which Promotions Anchor Id
	 */
	/*public void onClick$whichItemsPromotionAnchId(){
		logger.debug(">>>>>>>>>>>>> entered in onClick$whichItemsPromotionAnchId");
		String docSid = (String) whichItemsPromotionAnchId.getAttribute("docSid");
		LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
		List<CouponCodes>couponCodeList = ltyPrgmService.getCouponCodeByDocSid(docSid,currUser.getUserOrganization().getUserOrgId());

		if(couponCodeList == null){
			MessageUtil.setMessage("No promotion details found.", "red");
			return;
		}
		
		latestDetailsSubWinId.doHighlighted();
		openSubWindDiv(latestDetailsSubWinId$viewPromotionDetailsDivId);
		try {
			latestDetailsSubWinId.doHighlighted();
			latestDetailsSubWinId.setVisible(true);
			latestDetailsSubWinId.setTitle(Constants.STRING_NILL);
			latestDetailsSubWinId.setTitle("Promotions In Last Purchase");
			latestDetailsSubWinId.setHeight("300px");
			Components.removeAllChildren(latestDetailsSubWinId$promotionRowsId);

			for (CouponCodes coupon : couponCodeList) {
				Row row = new Row();
				row.setParent(latestDetailsSubWinId$promotionRowsId);

				row.appendChild(new Label(coupon.getCouponCode()==null?Constants.STRING_NILL:coupon.getCouponCode().trim()));
				row.appendChild(new Label(coupon.getTotDiscount()==null?"$ 0.00":Utility.getAmountInUSFormat(coupon.getTotDiscount())));

			}//for

		}catch (Exception e) {
			logger.error("Exception while fetching last purchase SKU details.....");
			logger.error("Exception ::" , e);
		}
		logger.debug("<<<<<<<<<<<<< completed onClick$whichItemsPromotionAnchId");
	}*///onClick$whichItemsPromotionAnchId

	/**
	 * This method handles the Anchor event on View Purchase.
	 * @param docSid
	 */

	public void onClickViewLoyaltyDetailsAnchId(String docSid) {
		logger.debug(">>>>>>>>>>>>> entered in onClickViewLoyaltyDetailsAnchId");
		LoyaltyProgramService loyaltyProgramService = new LoyaltyProgramService();
		List<Object[]> skuList =  null;
		if(docSid == null){
			MessageUtil.setMessage("No items found", "red");
			return;
		}
		try{
			latestDetailsSubWinId.doHighlighted();
			openSubWindDiv(latestDetailsSubWinId$viewSkuDetailsDivId);
			latestDetailsSubWinId.doHighlighted();
			latestDetailsSubWinId.setVisible(true);
			latestDetailsSubWinId.setTitle(Constants.STRING_NILL);
			latestDetailsSubWinId.setTitle("Purchase Details");
			latestDetailsSubWinId.setHeight("300px");
			latestDetailsSubWinId.setWidth("800px"); 
			Components.removeAllChildren(latestDetailsSubWinId$viewAllSKURowsId);
			logger.info(docSid+" \t userid"+currUser.getUserId());
			skuList = loyaltyProgramService.getLastPurchaseSKUDetailsByDocsid(docSid, currUser.getUserId());
			if(skuList == null){
				MessageUtil.setMessage("No items found", "red");
				logger.info("sku list is null");
				return;
			}
			for (Object[] object : skuList) {
				Row row = new Row();
				row.setParent(latestDetailsSubWinId$viewAllSKURowsId);

				row.appendChild(new Label(object[0]==null?Constants.STRING_NILL:object[0].toString().trim())); //sku
				row.appendChild(new Label(object[1]==null?Constants.STRING_NILL:object[1].toString())); //item category
				row.appendChild(new Label(object[3]==null?Constants.STRING_NILL:object[3].toString().trim())); //quantity
				/*row.appendChild(new Label(object[2]==null?"$ 0.00":Utility.getAmountInUSFormat(object[2]))); //price*/
				row.appendChild(new Label(object[2]==null? userCurrencySymbol + " 0.00":Utility.getAmountInUSFormat(object[2]))); //price
				}//for

		}catch (Exception e) {
			logger.error("Exception while fetching last purchase SKU details.....");
			logger.error("Exception ::" , e);
		}
		logger.debug("<<<<<<<<<<<<< completed onClickViewLoyaltyDetailsAnchId ");
	}//onClick$viewLoyaltyDetailsAnchId

	/**
	 * This method is use to create a sub-window with passed parameter.
	 * @param tempDiv
	 */
	private void openSubWindDiv(Div tempDiv) {
		logger.debug(">>>>>>>>>>>>> entered in openSubWindDiv");

		Div[] openSubWindDiv = {latestDetailsSubWinId$viewSkuDetailsDivId,latestDetailsSubWinId$viewPromotionDetailsDivId,latestDetailsSubWinId$viewPurchaseDetailsDivId,latestDetailsSubWinId$foundMultipleMatchesDivId,latestDetailsSubWinId$closeCardDivId,latestDetailsSubWinId$descDivId};

		for(int i=0; i<openSubWindDiv.length; i++ ) {

			if(openSubWindDiv[i] == tempDiv) {
				openSubWindDiv[i].setVisible(true);
			}
			else{
				openSubWindDiv[i].setVisible(false);
			}
		} //for

		logger.debug("<<<<<<<<<<<<< completed openSubWindDiv ");
	} // openSubWindDiv




	public void onClick$transactionFilterAncId(){
		logger.debug(">>>>>>>>>>>>> entered in onClick$transactionFilterAncId");
		if(contactsLoyaltyObj == null){
			MessageUtil.setMessage("Please look-up membership before performing this action.", "red");
			return;
		}

		if(timeDurLbId.getSelectedItem().getLabel().equalsIgnoreCase("Custom Dates")){ 
			if(!isValidate()){
				return;
			}
		}
		//Display Date Values
		getDateValues();

		//Pagination 
		displayPagination(contactsLoyaltyObj);
		logger.debug("<<<<<<<<<<<<< completed onClick$transactionFilterAncId ");
	}//onClick$transactionFilterAncId

	public void onClick$resetAnchId(){
		logger.debug(">>>>>>>>>>>>> entered in onClick$resetAnchId");
		heightForTrans = 0;
		contactsLoyaltyObj = null;
		searchByTbId.setValue(Constants.STRING_NILL);
		searchbyLbId.setSelectedIndex(0);

		clearFeilds();
		resetSearchFeilds();
		logger.debug("<<<<<<<<<<<<< completed onClick$resetAnchId ");
	}//onClick$resetAnchId

	public void onClick$transactionResetAnchId(){
		logger.debug(">>>>>>>>>>>>> entered in onClick$transactionResetAnchId");
		Date date = null;
		fromDateboxId.setValue(date);
		toDateboxId.setValue(date);
		clearPaginationDetails();
		logger.debug("<<<<<<<<<<<<< completed onClick$transactionResetAnchId ");
	}//onClick$transactionResetAnchId

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

	/*public void onSelect$first_nameTbId(){
		logger.debug(">>>>>>>>>>>>> entered in onSelect$first_nameTbId");
		first_nameTbId.setValue(Constants.STRING_NILL);
		logger.debug("<<<<<<<<<<<<< completed onSelect$first_nameTbId ");
	}*/
	//last_nameTbId

	public void onFocus$last_nameTbId(){
		logger.debug(">>>>>>>>>>>>> entered in onFocus$last_nameTbId");
		if(last_nameTbId.getValue() != null && "Last Name".equalsIgnoreCase(last_nameTbId.getValue())){

			last_nameTbId.setValue(Constants.STRING_NILL);
		}
		//last_nameTbId.setValue(Constants.STRING_NILL);
		logger.debug("<<<<<<<<<<<<< completed onFocus$last_nameTbId ");
	}

	/*public void onSelect$last_nameTbId(){
		last_nameTbId.setValue(Constants.STRING_NILL);
	}*/


/*
	public void onSelect$searchByTbId(){
		searchByTbId.setValue(Constants.STRING_NILL);
	}*/
	/**
	 * This method helps to create search box text 
	 */
	public void onSelect$searchbyLbId(){
		searchByTbId.setValue("");
		first_nameTbId.setValue("First Name");
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

	/**
	 * This method validate the Number
	 * @param in
	 * @return true/false
	 */
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
	
	/**
	 * This method validate the Number
	 * @param in
	 * @return true/false
	 */
	public boolean checkIfDouble(String inputString) {
		logger.debug(">>>>>>>>>>>>> entered in checkIfDouble");
		try {
			Double.parseDouble(inputString);
		} catch (NumberFormatException ex) {
			return false;
		}
		logger.debug("<<<<<<<<<<<<< completed checkIfDouble ");
		return true;
	}// checkIfDouble

	/**
	 * This method captures onClick Event on Export button, it create's window for selected feilds for export.
	 */
	public void onClick$exportBtnId() {
		logger.debug(">>>>>>>>>>>>> entered in onClick$exportBtnId");
		if(contactsLoyaltyObj == null){
			MessageUtil.setMessage("Please look-up membership before performing this action.", "red");
			return;
		}
		createWindow();
		//anchorEvent(false);

		custExport.setVisible(true);
		custExport.doHighlighted();
		logger.debug("<<<<<<<<<<<<< completed onClick$exportBtnId ");
	} //onClick$exportLblId

	public void anchorEvent(boolean flag) {
		logger.debug(">>>>>>>>>>>>> entered in anchorEvent");
		List<Component> chkList = custExport$chkDivId.getChildren();
		Checkbox tempChk = null;
		for (int i = 0; i < chkList.size(); i++) {
			if(!(chkList.get(i) instanceof Checkbox)) continue;

			tempChk = (Checkbox)chkList.get(i);
			tempChk.setChecked(flag);

		} // for
		logger.debug("<<<<<<<<<<<<< completed anchorEvent ");
	}//anchorEvent

	public void createWindow()	{
		logger.debug(">>>>>>>>>>>>> entered in createWindow");
		try {
			Components.removeAllChildren(custExport$chkDivId);
			//Processed Time
			Checkbox tempChk2 = new Checkbox("Card #");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			//Processed Time
			tempChk2 = new Checkbox("Processed Time");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			//Transaction ID
			tempChk2 = new Checkbox("Receipt No.");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			//Subsidiary No.
			tempChk2 = new Checkbox("Subsidiary No.");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			//Store No.
			tempChk2 = new Checkbox("Store No.");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			//Transaction Type
			tempChk2 = new Checkbox("Transaction Type");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			//Amount Entered
			tempChk2 = new Checkbox("Amount Entered");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			//amount diff
			tempChk2 = new Checkbox("Amount Diff");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			//point diff
			tempChk2 = new Checkbox("Points Diff");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			//Balance Amount
			tempChk2 = new Checkbox("Balance Amount");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			//Balance Points
			tempChk2 = new Checkbox("Balance Points");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			//Hold Balance
			tempChk2 = new Checkbox("Hold Balance");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			

			//Reciept Amount
			tempChk2 = new Checkbox("Reciept Amount");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			
			//Additional info
			tempChk2 = new Checkbox("Additional Info");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			
			

		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		logger.debug("<<<<<<<<<<<<< completed createWindow ");
	}//createWindow

	public void onClick$selectAllAnchr$custExport() {
		logger.debug(">>>>>>>>>>>>> entered in onClick$selectAllAnchr$custExport");
		anchorEvent(true);
		logger.debug("<<<<<<<<<<<<< completed onClick$selectAllAnchr$custExport ");
	}//onClick$selectAllAnchr$custExport

	public void onClick$clearAllAnchr$custExport() {
		logger.debug(">>>>>>>>>>>>> entered in onClick$clearAllAnchr$custExport");
		anchorEvent(false);
		logger.debug("<<<<<<<<<<<<< completed onClick$clearAllAnchr$custExport ");
	}//onClick$clearAllAnchr$custExport




	public void onClick$selectFieldBtnId$custExport() {
		logger.debug(">>>>>>>>>>>>> entered in onClick$selectFieldBtnId$custExport");
		custExport.setVisible(false);
		List<Component> chkList = custExport$chkDivId.getChildren();
		int indexes[]=new int[chkList.size()];
		boolean checked=false;
		Checkbox tempChk = null;

		for(int i=0;i<chkList.size();i++) {
			indexes[i]=-1;
		} // for


		for (int i = 0; i < chkList.size(); i++) {
			if(!(chkList.get(i) instanceof Checkbox)) continue;

			tempChk = (Checkbox)chkList.get(i);

			if(tempChk.isChecked()) {
				indexes[i]=0;
				checked=true;
			}else{
				indexes[i]=-1;
			}

		} // for

		if( ((Checkbox)custExport$chkDivId.getLastChild()).isChecked()) {

			checked=true;
		} //if

		if(checked) {

			int confirm=Messagebox.show("Do you want to export with selected fields ?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			/*if(confirm==1){
				try{

					exportCSV((String)exportCbId.getSelectedItem().getValue(),indexes);

				}catch(Exception e){
					logger.error("Exception caught :: ",e);
				}
			}*/
			if(confirm==1){
				 try{
					 String type = exportCbId.getSelectedItem().getValue();
					 logger.info("FileType---- >"+type);
					 if(type.contains("csv"))
						 exportCSV((String)exportCbId.getSelectedItem().getValue(),indexes);
					 exportCSV(type.toString(),indexes);
					 if(type.contains("xls"))
							 exportExcel((String)exportCbId.getSelectedItem().getValue(),indexes);

				 }catch(Exception e){
					 logger.error("Exception caught :: ",e);
				 }
			 }
			else{
				custExport.setVisible(true);
			}

		}
		else {

			MessageUtil.setMessage("Please select atleast one field", "red");
			custExport.setVisible(false);
		}

		logger.debug("<<<<<<<<<<<<< completed onClick$selectFieldBtnId$custExport ");
	}//onClick$selectFieldBtnId$custExport

	private void exportExcel(String value, int[] indexes) {
		// TODO Auto-generated method stub
		logger.debug(">>>>>>>>>>>>> entered in exportExcel");
		String type = exportCbId.getSelectedItem().getLabel();
		StringBuffer sb = null;
		String userName = currUser.getUserName();
		LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
		JdbcResultsetHandler jdbcResultsetHandler = null;
		//LoyaltyProgram loyaltyProgram = null;
		if(transListLbId.getChildren().size() ==0){
			MessageUtil.setMessage("No transactions found to export", "color:red", "TOP");
			return;
		}
		String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
		String exportDir = usersParentDirectory + "/" + userName + "/Export/" ;
		File downloadDir = new File(exportDir);
		if(downloadDir.exists()){
			try {
				FileUtils.deleteDirectory(downloadDir);
				logger.debug(downloadDir.getName() + " is deleted");
			} catch (Exception e) {
				logger.error("Exception ::" , e);

				logger.debug(downloadDir.getName() + " is not deleted");
			}
		}
		if(!downloadDir.exists()){
			downloadDir.mkdirs();
		}
		if(type.contains("xls")){
			logger.debug(">>>>>>>>>>>>> entered in typeExcel");
			String filePath = exportDir +  "Loyalty_Report_Mem#" + contactsLoyaltyObj.getCardNumber() + "_" +
					MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);
			try {
				DecimalFormat f = new DecimalFormat("#0.00");
				filePath = filePath + "_Transactions.xls";
				logger.debug("Download File path : " + filePath);
				File file = new File(filePath);
				BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
				String transferLoyaltyId = ""+contactsLoyaltyObj.getLoyaltyId();
				if(transferList != null){
					for(ContactsLoyalty eachTranfer:transferList){
						transferLoyaltyId += ","+eachTranfer.getLoyaltyId();
					}
				}
				HSSFWorkbook hwb = new HSSFWorkbook();
				 HSSFSheet sheet = hwb.createSheet("Store Reports");
				 HSSFRow row = sheet.createRow((short) 0);
				 int columnId=0;
				 HSSFCell cell = null;
				 logger.debug(">>>>>>>>>>>>> Before Cells");
				 if(indexes[0]==0) {
				 cell = row.createCell(columnId++);
				 cell.setCellValue("Card #");
				 }
				 if(indexes[1]==0) {
				 cell = row.createCell(columnId++);
				 cell.setCellValue("Processed Time");
				 }
				 if(indexes[2]==0) {
				 cell = row.createCell( columnId++);
				 cell.setCellValue("Receipt No.");
				 }
				 if(indexes[3]==0) {
				 cell = row.createCell( columnId++);
				 cell.setCellValue("Subsidiary No.");
				 }
				 if(indexes[4]==0) {
				 cell = row.createCell( columnId++);
				 cell.setCellValue("Store No.");
				 }
				 if(indexes[5]==0) {
				 cell = row.createCell( columnId++);
				 cell.setCellValue("Transaction Type");
				 }
				 if(indexes[6]==0) {
				 cell = row.createCell( columnId++);
				 cell.setCellValue("Amount Entered");
				 }
				 if(indexes[7]==0) {
				 cell = row.createCell( columnId++);
				 cell.setCellValue("Difference in Value.");
				 }
				 if(indexes[8]==0) {
				 cell = row.createCell( columnId++);
				 cell.setCellValue("Value Code.");
				 }
				 if(indexes[9]==0) {
				 cell = row.createCell( columnId++);
				 cell.setCellValue("Balance Currency");
				 }
				 if(indexes[10]==0) {
				 cell = row.createCell( columnId++);
				 cell.setCellValue("Balance Pts.");
				 }
				 if(indexes[11]==0) {
				 cell = row.createCell( columnId++);
				 cell.setCellValue("Hold Balance");
				 }
				 if(indexes[12]==0) {
				 cell = row.createCell( columnId++);
				 cell.setCellValue("Additional Info");
				 }
				 jdbcResultsetHandler = new JdbcResultsetHandler();
					
					String query = "SELECT membership_number, created_date, docsid, subsidiary_number, store_number,transaction_type, entered_amount_type, entered_amount, excluded_amount, amount_balance, " +
							" gift_balance, points_balance,receipt_amount, description2, hold_points, hold_amount,amount_difference,earn_type ,points_difference,reward_difference, receipt_number  FROM loyalty_transaction_child " +
					" WHERE user_id ="+ currUser.getUserId() + " AND loyalty_id in ("+transferLoyaltyId+")"+
					" AND transaction_type IN('"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"','"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"','"+
					OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_BONUS+"','"+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')"+
					
					//" AND source_type ='"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"'"
					 " AND created_date BETWEEN '"+MyCalendar.calendarToString(startDate, null)+"' AND '"+MyCalendar.calendarToString(endDate, null)+"'  "+
					" ORDER BY trans_child_id DESC,created_date DESC";
					
					logger.info("query===>"+query);
					jdbcResultsetHandler.executeStmt(query);
					List transList = null;
					ResultSet resultSet = jdbcResultsetHandler.getResultSet();
					LoyaltyProgramService loyaltyProgramService = new LoyaltyProgramService();
					int rowId=1;
						while(resultSet.next()) {
							row = sheet.createRow((short) rowId++);
							 columnId=0;
							 cell=null;
							 String receiptNumber = null;
							 if(indexes[0]==0) {
							 cell = row.createCell( columnId++);
							 cell.setCellValue(resultSet.getString("membership_number"));
							 logger.info(""+cell.getColumnIndex());
							 }
							 logger.debug(">>>>>>>>>>>>> inside While JDBC");
							 if(indexes[1]==0) {
								 Timestamp t = resultSet.getTimestamp("created_date");
									Calendar cal = null;
									if (t != null) {
										if (new Date(t.getTime()) != null) {
											cal = Calendar.getInstance();
											cal.setTime(t);
										}
									}
								 cell = row.createCell( columnId++);
								 cell.setCellValue(cal !=null?MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STYEAR,clientTimeZone):"--") ;
								 logger.info(""+cell.getColumnIndex());
								 }
							 if(indexes[2]==0) {
								 String receiptNo =resultSet.getString("receipt_number");
									if((resultSet.getString("docsid") != null && !resultSet.getString("docsid").isEmpty()) &&( receiptNo == null || receiptNo.isEmpty())){
										receiptNo = loyaltyProgramService.getReceiptNumberByDocsid(resultSet.getString("docsid"), currUser.getUserId());
									}
									if(receiptNo == null){
										receiptNo ="--";
									}
									else{
										receiptNo = receiptNumber+Constants.STRING_NILL;
									}
								 cell = row.createCell( columnId++);
								 cell.setCellValue(receiptNo) ;
								 logger.info(""+cell.getColumnIndex());
								 }
							 if(indexes[3]==0) {
								 cell = row.createCell(columnId++);
								 cell.setCellValue(resultSet.getString("subsidiary_number") == null? "--" : fetchSubsidiaryName(resultSet.getString("subsidiary_number"))) ;
								 logger.info(""+cell.getColumnIndex());
								 }
							 if(indexes[4]==0) {
								 cell = row.createCell( columnId++);
								 cell.setCellValue(resultSet.getString("store_number") == null ?"--" : fetchStoreName(resultSet.getString("store_number"))) ;
								 logger.info(""+cell.getColumnIndex());
								 }
							 if(indexes[5]==0) {
								 cell = row.createCell( columnId++);
								 cell.setCellValue(resultSet.getString("transaction_type").equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_ISSUANCE)? resultSet.getString("entered_amount_type").equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PURCHASE)?"Loyalty Issuance":resultSet.getString("entered_amount_type").equals(OCConstants.LOYALTY_TYPE_REWARD)? OCConstants.LOYALTY_TYPE_REWARD:"Gift Issuance":resultSet.getString("transaction_type")) ;
								 logger.info(""+cell.getColumnIndex());
								 }
							 if(indexes[6]==0) {
								 String enterAmt = "";
									if (((resultSet.getString("earn_type") != null && 
													((String) resultSet.getString("earn_type")).equalsIgnoreCase(OCConstants.LOYALTY_POINTS)) 
													|| (resultSet.getString("entered_amount_type") != null 
													&& resultSet.getString("entered_amount_type").toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTSREDEEM)))
													&& (!((String) resultSet.getString("transaction_type")).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE))
													&& !((String) resultSet.getString("transaction_type")).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)) {
												
												Double enteredAmt = resultSet.getString("entered_amount") == null ? 0 : Double.parseDouble(resultSet.getString("entered_amount").toString());
												String valueEntered = ((String) resultSet.getString("transaction_type")).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT) ? 
														(resultSet.getString("entered_amount_type") != null ? ((String) resultSet.getString("entered_amount_type")).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD) ? "+ "+ enteredAmt.longValue()
														: "- " + enteredAmt.longValue()
														: "0")
														: "" + enteredAmt.longValue();
												enterAmt = valueEntered + " Points";
											
											}else{			

												double enteredAmt = resultSet.getString("entered_amount") == null ? 0.0 : Double.parseDouble(resultSet.getString("entered_amount").toString());
												double excludeAmt = resultSet.getString("excluded_amount") == null ? 0.0 : Double.parseDouble(resultSet.getString("excluded_amount").toString());
												String valueEntered = ((String) resultSet.getString("transaction_type")).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT) ? 
														(resultSet.getString("entered_amount_type") != null ? ((String) resultSet.getString("entered_amount_type")).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD) ? "+ "+ f.format(enteredAmt - excludeAmt)
														: "- " + f.format(enteredAmt - excludeAmt)
														: "0.00")
														: f.format(enteredAmt - excludeAmt);
												if (resultSet.getString("entered_amount") != null) {
													if(!((String) resultSet.getString("transaction_type")).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT)){
														enterAmt = valueEntered + " Currency";
													}else{
														if((resultSet.getString("earn_type") != null) && ((String) resultSet.getString("earn_type")).equalsIgnoreCase(OCConstants.LOYALTY_TYPE_LPV)){
															enterAmt = valueEntered + " LPV";
														}else enterAmt = valueEntered + " Currency";
													}
																
													
												} else if (((String) resultSet.getString("transaction_type")).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT)
														|| ((String) resultSet.getString("transaction_type")).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_BONUS)) {
													/*valueEntered = ((String) resultSet.getString("earn_type")).equalsIgnoreCase(OCConstants.LOYALTY_POINTS) ? "0 Points"
															: "0.00 Currency";*/
													
													if(((String) resultSet.getString("earn_type")).equalsIgnoreCase(OCConstants.LOYALTY_POINTS)){
														valueEntered = "0 Points";
													}else if(((String) resultSet.getString("earn_type")).equalsIgnoreCase(OCConstants.LOYALTY_TYPE_LPV)){
														valueEntered = "0.00 LPV";
													}else {
														valueEntered ="0.00 Currency";
													}
													
													enterAmt = valueEntered + "";
												} else {
													enterAmt = "--";
												}
											
											}
								 
								 
								 cell = row.createCell( columnId++);
								 cell.setCellValue(enterAmt) ;
								 logger.info(""+cell.getColumnIndex());
								 }
							 if(indexes[7]==0) {
									String valueDiff=Constants.STRING_NILL;	

									if(resultSet.getString("amount_difference")!=null && !resultSet.getString("amount_difference").isEmpty() && 
											!resultSet.getString("amount_difference").equals("0")) {
										valueDiff = resultSet.getString("amount_difference");
									}else if(resultSet.getString("points_difference")!=null && !resultSet.getString("points_difference").isEmpty() 
											&& !resultSet.getString("points_difference").equals("0")) {
										valueDiff = Utility.truncateToInteger(resultSet.getString("points_difference"));
									}else if(resultSet.getString("reward_difference")!=null && !resultSet.getString("reward_difference").isEmpty() 
											&& !resultSet.getString("reward_difference").equals("0")){
										valueDiff = resultSet.getString("reward_difference");
									}
									
								 cell = row.createCell( columnId++);
								 cell.setCellValue(valueDiff+" ") ;
								 logger.info(""+cell.getColumnIndex());
								 }
							 if(indexes[8]==0) {
								 String valueCode=Constants.STRING_NILL;
									
									if(resultSet.getString("amount_difference")!=null && !resultSet.getString("amount_difference").isEmpty() && 
											!resultSet.getString("amount_difference").equals("0")) {
										valueCode = OCConstants.LOYALTY_TYPE_CURRENCY;
									}else if(resultSet.getString("points_difference")!=null && !resultSet.getString("points_difference").isEmpty()  
											&&	!resultSet.getString("points_difference").equals("0")) {
										valueCode = OCConstants.LOYALTY_TYPE_POINTS;
									}else if(resultSet.getString("reward_difference")!=null && !resultSet.getString("reward_difference").isEmpty() &&
											!resultSet.getString("reward_difference").equals("0")){
										valueCode = resultSet.getString("earn_type");
									}
									
								 cell = row.createCell( columnId++);
								 cell.setCellValue(valueCode+" ") ;
								 logger.info(""+cell.getColumnIndex());
								 logger.info("Index[8]"+cell.getColumnIndex());
								 }
							 String balanceStr = "";
								/*amount_balance" +
								" gift_balance, points_balance 
			*/					if(resultSet.getDouble("amount_balance") != 0 &&  
										 resultSet.getDouble("gift_balance") != 0 ) {
									balanceStr = "Gift : " + Utility.getAmountInExport(resultSet.getDouble("gift_balance")) +" Loyalty : "+ Utility.getAmountInExport(resultSet.getDouble("amount_balance"));
								}else if(resultSet.getDouble("amount_balance") == 0 && resultSet.getDouble("gift_balance") != 0){
									balanceStr = Utility.getAmountInExport(resultSet.getDouble("gift_balance"));
								}else if(resultSet.getDouble("amount_balance") != 0
										&& resultSet.getDouble("gift_balance") == 0){
									balanceStr = Utility.getAmountInExport(resultSet.getDouble("amount_balance"));
								}else {
									balanceStr = Utility.getAmountInExport(0.00)+"";
								}
								if(indexes[9]==0) {
									 cell = row.createCell( columnId++);
									 cell.setCellValue(balanceStr) ;
									 logger.info(""+cell.getColumnIndex());
									 }
								if(indexes[10]==0) {
									 cell = row.createCell( columnId++);
									 cell.setCellValue(resultSet.getDouble("points_balance") ==0 ? 0 : Double.valueOf(resultSet.getDouble("points_balance")).intValue()) ;
									 logger.info(""+cell.getColumnIndex());
									 }
								if(indexes[11]==0) {
									String holdBalanceStr = "";
									if(resultSet.getString("transaction_type").equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_ISSUANCE) && resultSet.getString("entered_amount_type").equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PURCHASE)){
										if(resultSet.getString("hold_points") != null){
											Double holdBal = 0.0;
										holdBal = Double.valueOf(resultSet.getString("hold_points"));
										if(resultSet.getString("description2") != null){
											String transactions[] = resultSet.getString("description2").split(Constants.ADDR_COL_DELIMETER);
											for(String eachTrx:transactions){
												holdBal +=Double.valueOf(eachTrx.split(":")[1].trim());
											}
										}
										holdBalanceStr = 	holdBal !=0 ? holdBal.intValue()+" Points":"";
									}else if(resultSet.getString("hold_amount") != null){
										double holdBal = 0;
										holdBal = Double.valueOf(resultSet.getString("hold_amount"));
										if(resultSet.getString("description2") != null){
											String transactions[] = resultSet.getString("description2").split(Constants.ADDR_COL_DELIMETER);
											for(String eachTrx:transactions){
												holdBal +=Double.valueOf(eachTrx.split(":")[1].trim());
											}
										}
										/*holdBalanceStr = holdBal !=0 ? "$ "+f.format(holdBal):"";*/
										holdBalanceStr = holdBal !=0 ? f.format(holdBal):"";
									}
									}
									else {
										holdBalanceStr = "";
									}
									 cell = row.createCell( columnId++);
									 cell.setCellValue(holdBalanceStr) ;
									 logger.info(""+cell.getColumnIndex());
									 }
								if(indexes[12]==0) {
									if(((String) resultSet.getString("transaction_type")).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT))
										if(resultSet.getString("description2") != null) {
									 cell = row.createCell( columnId++);
									 cell.setCellValue(resultSet.getString("description2")) ;
									 logger.info(""+cell.getColumnIndex());
										}
									 }
								FileOutputStream fileOut = new FileOutputStream(filePath);
								 hwb.write(fileOut);
								 fileOut.flush();
								 fileOut.close();
								 transList = null;
								 sb = null;
								 Filedownload.save(file, "application/vnd.ms-excel");						 
								 //System.gc();
								 logger.debug(">>>>>>>>>>>>> exit exportExcel");
							 
							
						}
			}catch (IOException e) {
				logger.error("Exception ::",e);

			}catch (Exception e) {
				logger.error("Exception ::",e);
			}finally{
				if(jdbcResultsetHandler !=null)jdbcResultsetHandler.destroy();
				jdbcResultsetHandler=null;
			}
			
		}
		
	}
	private void exportCSV(String value, int[] indexes) {
		logger.debug(">>>>>>>>>>>>> entered in exportCSV");
		String type = exportCbId.getSelectedItem().getLabel();
		StringBuffer sb = null;
		String userName = currUser.getUserName();
		LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
		JdbcResultsetHandler jdbcResultsetHandler = null;
		//LoyaltyProgram loyaltyProgram = null;
		logger.info("transListLbId.getChildren().size()==>"+transListLbId.getChildren().size());
		if(transListLbId.getChildren().size() <=1){
			MessageUtil.setMessage("No transactions found to export", "color:red", "TOP");
			return;
		}
		/*if(contactsLoyaltyObj.getProgramId() != null){
			loyaltyProgram =ltyPrgmService.getProgmObj(contactsLoyaltyObj.getProgramId());
		}*/
		//String programName=Constants.STRING_NILL;
		String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
		String exportDir = usersParentDirectory + "/" + userName + "/Export/" ;
		File downloadDir = new File(exportDir);
		if(downloadDir.exists()){
			try {
				FileUtils.deleteDirectory(downloadDir);
				logger.debug(downloadDir.getName() + " is deleted");
			} catch (Exception e) {
				logger.error("Exception ::" , e);

				logger.debug(downloadDir.getName() + " is not deleted");
			}
		}
		if(!downloadDir.exists()){
			downloadDir.mkdirs();
		}

		/*if(loyaltyProgram != null){
			programName = loyaltyProgram.getProgramName() == null ? Constants.STRING_NILL : loyaltyProgram.getProgramName();
		}
		else{
			programName = System.currentTimeMillis()+Constants.STRING_NILL;
		}*/

		if(type.contains("csv")){

			//	String transType = null;
			String filePath = exportDir +  "Loyalty_Report_Mem#" + contactsLoyaltyObj.getCardNumber() + "_" +
					MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);
			try {
				DecimalFormat f = new DecimalFormat("#0.00");
				filePath = filePath + "_Transactions.csv";
				logger.debug("Download File path : " + filePath);
				File file = new File(filePath);
				BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
				String transferLoyaltyId = ""+contactsLoyaltyObj.getLoyaltyId();
				if(transferList != null){
					for(ContactsLoyalty eachTranfer:transferList){
						transferLoyaltyId += ","+eachTranfer.getLoyaltyId();
					}
				}
				String udfFldsLabel= "";
				if(indexes[0]==0) {
					udfFldsLabel = "\""+"Card #"+"\""+",";
				}
				if(indexes[1]==0) {
					udfFldsLabel += "\""+"Processed Time"+"\""+",";
				}
				if(indexes[2]==0) {
					udfFldsLabel += "\""+"Receipt No."+"\""+",";
				}
				// added for displaying Subsidiary No.
				if(indexes[3]==0) {
					udfFldsLabel += "\""+"Subsidiary No."+"\""+",";
				}	
				if(indexes[4]==0) {
					udfFldsLabel += "\""+"Store No."+"\""+",";
				}	
				if(indexes[5]==0) {

					udfFldsLabel += "\""+"Transaction Type"+"\""+",";
				}
				if(indexes[6]==0) {

					udfFldsLabel += "\""+"Amount Entered"+"\""+",";
				}
				if(indexes[7]==0) {

					udfFldsLabel += "\""+"Amount Diff."+"\""+",";
				}
				if(indexes[8]==0) {

					udfFldsLabel += "\""+"Points Diff."+"\""+",";
				}
				if(indexes[9]==0) {

					udfFldsLabel += "\""+"Balance Currency"+"\""+",";
				}
				if(indexes[10]==0) {

					udfFldsLabel += "\""+"Balance Pts."+"\""+",";
				}
				if(indexes[11]==0) {

					udfFldsLabel += "\""+"Hold Balance"+"\""+",";
				}

				if(indexes[12]==0) {

					udfFldsLabel += "\""+"Reciept Amount"+"\""+",";
				}
				
				if(indexes[13]==0) {

					udfFldsLabel += "\""+"Additional Info"+"\""+",";
				}

				sb = new StringBuffer();
				sb.append(udfFldsLabel);
				sb.append("\r\n");

				bw.write(sb.toString());
				
				jdbcResultsetHandler = new JdbcResultsetHandler();
				
				String query = "SELECT membership_number, created_date, docsid, subsidiary_number, store_number, transaction_type, entered_amount_type, entered_amount, excluded_amount, amount_balance, " +
						" gift_balance, points_balance, receipt_amount,description2, hold_points, hold_amount,amount_difference,earn_type ,points_difference,reward_difference,receipt_number  FROM loyalty_transaction_child " +
				" WHERE user_id ="+ currUser.getUserId() + " AND loyalty_id in ("+transferLoyaltyId+")"+
				" AND transaction_type IN('"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"','"+OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION+"','"+
				OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_BONUS+"','"+OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT+"','"+OCConstants.LOYALTY_TRANS_TYPE_RETURN+"')"+
				
				//" AND source_type ='"+OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE+"'"
				 " AND created_date BETWEEN '"+MyCalendar.calendarToString(startDate, null)+"' AND '"+MyCalendar.calendarToString(endDate, null)+"'  "+
				" ORDER BY trans_child_id DESC,created_date DESC";
				
				
				jdbcResultsetHandler.executeStmt(query);
				List transList = null;
				ResultSet resultSet = jdbcResultsetHandler.getResultSet();
				LoyaltyProgramService loyaltyProgramService = new LoyaltyProgramService();
				while(resultSet.next()){
					sb = new StringBuffer();
					String receiptNumber = null;
					if(indexes[0]==0) {
						sb.append("\"");sb.append(resultSet.getString("membership_number")); sb.append("\",");
					}
					if(indexes[1]==0) {
						
						Timestamp t = resultSet.getTimestamp("created_date");
						Calendar cal = null;
						if (t != null) {
							if (new Date(t.getTime()) != null) {
								cal = Calendar.getInstance();
								cal.setTime(t);
							}
						}
						sb.append("\"");sb.append(cal !=null?MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STYEAR,clientTimeZone):"--"); sb.append("\",");
					}
					if(indexes[2]==0) {
						//Get receipt Number
						String receiptNo = resultSet.getString("receipt_number");
						
						if((resultSet.getString("docsid") != null && !resultSet.getString("docsid").isEmpty()) &&( receiptNo == null || receiptNo.isEmpty()) ){
							receiptNumber = loyaltyProgramService.getReceiptNumberByDocsid(resultSet.getString("docsid"), currUser.getUserId());
							if(receiptNo == null){
								receiptNo ="--";
							}
							else{
								receiptNo = receiptNumber+Constants.STRING_NILL;
							}
						}else if(resultSet.getString("receipt_number") != null && !resultSet.getString("receipt_number").isEmpty() ){
							receiptNo = resultSet.getString("receipt_number")+Constants.STRING_NILL;
							
						}
						
						
//						if(resultSet.getString("docsid") != null){
//							receiptNumber = loyaltyProgramService.getReceiptNumberByDocsid(resultSet.getString("docsid"), currUser.getUserId());
//						}
//						if(receiptNumber == 0){
//							receiptNo ="--";
//						}
//						else{
//							receiptNo = receiptNumber+Constants.STRING_NILL;
//						}
						sb.append("\"");sb.append(receiptNo); sb.append("\",");
					}
					if(indexes[3]==0) {
						sb.append("\"");sb.append(resultSet.getString("subsidiary_number") == null ? "--" : fetchSubsidiaryName(resultSet.getString("subsidiary_number"))); sb.append("\",");
					}
					if(indexes[4]==0) {
						sb.append("\"");sb.append(resultSet.getString("store_number") == null ? "--" : fetchStoreName(resultSet.getString("store_number"))); sb.append("\",");
					}
					if(indexes[5]==0) {
						sb.append("\"");sb.append(resultSet.getString("transaction_type").equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_ISSUANCE)? resultSet.getString("entered_amount_type").equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PURCHASE)?"Loyalty Issuance":"Gift Issuance":resultSet.getString("transaction_type")); sb.append("\",");
					}	
					if(indexes[6] == 0){
						String enterAmt = "";
						if (((resultSet.getString("earn_type") != null && 
										((String) resultSet.getString("earn_type")).equalsIgnoreCase(OCConstants.LOYALTY_POINTS)) 
										|| (resultSet.getString("entered_amount_type") != null 
										&& resultSet.getString("entered_amount_type").toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTSREDEEM)))
										&& (!((String) resultSet.getString("transaction_type")).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE))
										&& !((String) resultSet.getString("transaction_type")).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)) {
									
									Double enteredAmt = resultSet.getString("entered_amount") == null ? 0 : Double.parseDouble(resultSet.getString("entered_amount").toString());
									String valueEntered = ((String) resultSet.getString("transaction_type")).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT) ? 
											(resultSet.getString("entered_amount_type") != null ? ((String) resultSet.getString("entered_amount_type")).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD) ? "+ "+ enteredAmt.longValue()
											: "- " + enteredAmt.longValue()
											: "0")
											: "" + enteredAmt.longValue();
									enterAmt = valueEntered + " Points";
								
								}else{			

									double enteredAmt = resultSet.getString("entered_amount") == null ? 0.0 : Double.parseDouble(resultSet.getString("entered_amount").toString());
									double excludeAmt = resultSet.getString("excluded_amount") == null ? 0.0 : Double.parseDouble(resultSet.getString("excluded_amount").toString());
									String valueEntered = ((String) resultSet.getString("transaction_type")).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT) ? 
											(resultSet.getString("entered_amount_type") != null ? ((String) resultSet.getString("entered_amount_type")).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD) ? "+ "+ f.format(enteredAmt - excludeAmt)
											: "- " + f.format(enteredAmt - excludeAmt)
											: "0.00")
											: f.format(enteredAmt - excludeAmt);
									if (resultSet.getString("entered_amount") != null) {
										if(!((String) resultSet.getString("transaction_type")).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT)){
											enterAmt = valueEntered + " Currency";
										}else{
											if((resultSet.getString("earn_type") != null) && ((String) resultSet.getString("earn_type")).equalsIgnoreCase(OCConstants.LOYALTY_TYPE_LPV)){
												enterAmt = valueEntered + " LPV";
											}else enterAmt = valueEntered + " Currency";
										}
													
										
									} else if (((String) resultSet.getString("transaction_type")).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT)
											|| ((String) resultSet.getString("transaction_type")).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_BONUS)) {
										/*valueEntered = ((String) resultSet.getString("earn_type")).equalsIgnoreCase(OCConstants.LOYALTY_POINTS) ? "0 Points"
												: "0.00 Currency";*/
										
										if(((String) resultSet.getString("earn_type")).equalsIgnoreCase(OCConstants.LOYALTY_POINTS)){
											valueEntered = "0 Points";
										}else if(((String) resultSet.getString("earn_type")).equalsIgnoreCase(OCConstants.LOYALTY_TYPE_LPV)){
											valueEntered = "0.00 LPV";
										}else {
											valueEntered ="0.00 Currency";
										}
										
										enterAmt = valueEntered + "";
									} else {
										enterAmt = "--";
									}
								
								}
						sb.append("\"");sb.append(enterAmt); sb.append("\","); 
					}
					
					if(indexes[7]==0) {
						String amoutnDiff = resultSet.getString("amount_difference") == null ? "" : resultSet.getString("amount_difference");
						
						sb.append("\"");sb.append(amoutnDiff+" "); sb.append("\",");
					} 
					
					//set point diff
					if(indexes[8]==0) {
						String pointDiff = resultSet.getString("points_difference") == null ? "" : resultSet.getString("points_difference");
						
						sb.append("\"");sb.append(pointDiff+" "); sb.append("\",");
					} 
					String balanceStr = "";
					/*amount_balance" +
					" gift_balance, points_balance 
*/					if(resultSet.getDouble("amount_balance") != 0 &&  
							 resultSet.getDouble("gift_balance") != 0 ) {
						balanceStr = "Gift : " + Utility.getAmountInExport(resultSet.getDouble("gift_balance")) +" Loyalty : "+ Utility.getAmountInExport(resultSet.getDouble("amount_balance"));
					}else if(resultSet.getDouble("amount_balance") == 0 && resultSet.getDouble("gift_balance") != 0){
						balanceStr = Utility.getAmountInExport(resultSet.getDouble("gift_balance"));
					}else if(resultSet.getDouble("amount_balance") != 0
							&& resultSet.getDouble("gift_balance") == 0){
						balanceStr = Utility.getAmountInExport(resultSet.getDouble("amount_balance"));
					}else {
						balanceStr = Utility.getAmountInExport(0.00)+"";
					}
					if(indexes[9]==0) {
						sb.append("\"");sb.append(balanceStr); sb.append("\",");
					}
					if(indexes[10]==0) {
						sb.append("\"");sb.append(resultSet.getDouble("points_balance") ==0 ? "0" : Double.valueOf(resultSet.getDouble("points_balance")).intValue()); sb.append("\",");
					}
					/*if(indexes[7]==0) {
									 sb.append(MyCalendar.calendarToString(obj.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR,clientTimeZone)); sb.append("\"");
								}*/
					if(indexes[11]==0) {
						String holdBalanceStr = "";
						if(resultSet.getString("transaction_type").equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_ISSUANCE) && resultSet.getString("entered_amount_type").equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PURCHASE)){
							if(resultSet.getString("hold_points") != null){
								Double holdBal = 0.0;
							holdBal = Double.valueOf(resultSet.getString("hold_points"));
							if(resultSet.getString("description2") != null){
								String transactions[] = resultSet.getString("description2").split(Constants.ADDR_COL_DELIMETER);
								for(String eachTrx:transactions){
									holdBal +=Double.valueOf(eachTrx.split(":")[1].trim());
								}
							}
							holdBalanceStr = 	holdBal !=0 ? holdBal.intValue()+" Points":"";
						}else if(resultSet.getString("hold_amount") != null){
							double holdBal = 0;
							holdBal = Double.valueOf(resultSet.getString("hold_amount"));
							if(resultSet.getString("description2") != null){
								String transactions[] = resultSet.getString("description2").split(Constants.ADDR_COL_DELIMETER);
								for(String eachTrx:transactions){
									holdBal +=Double.valueOf(eachTrx.split(":")[1].trim());
								}
							}
							/*holdBalanceStr = holdBal !=0 ? "$ "+f.format(holdBal):"";*/
							holdBalanceStr = holdBal !=0 ? f.format(holdBal):"";
						}
						}
						else {
							holdBalanceStr = "";
						}
						sb.append("\"");sb.append(holdBalanceStr); sb.append("\",");
					}
					
					if(indexes[12]==0) {
						String receiptAmount = resultSet.getString("receipt_amount") == null ? "0" : resultSet.getString("receipt_amount");
						
						sb.append("\"");sb.append(receiptAmount+" "); sb.append("\",");
					}
					
					if(indexes[13]==0) {
						if(((String) resultSet.getString("transaction_type")).equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT))
						if(resultSet.getString("description2") != null){
							sb.append("\"");
							sb.append(resultSet.getString("description2")); 
							sb.append("\"");
						}
						else{
						sb.append("\"");
						}
					}
					
					
					
					
					
					sb.append("\r\n");
					bw.write(sb.toString());
				}

					transList = null;
					sb = null;
				
				bw.flush();
				bw.close();
				bw = null;
				//System.gc();
				Filedownload.save(file, "text/plain");
			} catch (IOException e) {
				logger.error("Exception ::",e);

			}catch (Exception e) {
				logger.error("Exception ::",e);
			}finally{
				if(jdbcResultsetHandler !=null)jdbcResultsetHandler.destroy();
				jdbcResultsetHandler=null;
			}

		}
		logger.debug("<<<<<<<<<<<<< completed exportCSV ");
	}//exportCSV

	private void getDateValues() {
		logger.debug(">>>>>>>>>>>>> entered in getDateValues");
		endDate = new MyCalendar(clientTimeZone);
		endDate.set(MyCalendar.HOUR_OF_DAY, 23);
		endDate.set(MyCalendar.MINUTE, 59);
		endDate.set(MyCalendar.SECOND, 59);

		startDate = new MyCalendar(clientTimeZone);
		startDate.set(MyCalendar.HOUR_OF_DAY, 00);
		startDate.set(MyCalendar.MINUTE, 00);
		startDate.set(MyCalendar.SECOND, 00);

		if(timeDurLbId.getSelectedItem().getLabel().equalsIgnoreCase("Last 30 Days")) {
			monthsDiff = 30;
			startDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - monthsDiff);
			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE));
		}else if(timeDurLbId.getSelectedItem().getLabel().equalsIgnoreCase("Last 3 Months")) {
			monthsDiff = 3;
			startDate.set(MyCalendar.MONTH, (endDate.get(MyCalendar.MONTH) - monthsDiff)+1);
			startDate.set(MyCalendar.DATE, 1);
			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE));
		}else if(timeDurLbId.getSelectedItem().getLabel().equalsIgnoreCase("Last 6 Months")) {
			monthsDiff =6;
			startDate.set(MyCalendar.MONTH, (endDate.get(MyCalendar.MONTH) - monthsDiff)+1);
			startDate.set(MyCalendar.DATE, 1);
			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE));
		}else if(timeDurLbId.getSelectedItem().getLabel().equalsIgnoreCase("Last 1 Year")) {
			monthsDiff = 12;
			startDate.set(MyCalendar.MONTH, (endDate.get(MyCalendar.MONTH) - monthsDiff)+1);
			startDate.set(MyCalendar.DATE, 1);
			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE));
		}else if(timeDurLbId.getSelectedItem().getLabel().equalsIgnoreCase("Custom Dates")) {
			startDate = getStartDate();
			endDate = getEndDate();

			/*if(startDate.get(Calendar.DATE) == endDate.get(Calendar.DATE) && startDate.get(Calendar.MONTH) == endDate.get(Calendar.MONTH)
					&& startDate.get(Calendar.YEAR) == endDate.get(Calendar.YEAR)) {
				logger.info("startDate::"+startDate + "endDate::"+endDate);
				return;
			}*/


			//			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - 1);

		}else{
			if(timeDurLbId.getSelectedItem().getLabel().equalsIgnoreCase("Today")) {
				startDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE));
				endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE));
			}
		}
		logger.info("Starting date "+startDate);
		logger.info("End date "+endDate);
		logger.debug("<<<<<<<<<<<<< completed getDateValues ");
	}//getDateValues
	/**
	 * This method get's the start date
	 * @return serverFromDateCal
	 */
	public Calendar getStartDate(){
		logger.debug(">>>>>>>>>>>>> entered in getStartDate");
		if(fromDateboxId.getValue() != null && !fromDateboxId.getValue().toString().isEmpty()) {
			Calendar serverFromDateCal = fromDateboxId.getServerValue();
			Calendar tempClientFromCal = fromDateboxId.getClientValue();
			serverFromDateCal.set(Calendar.HOUR_OF_DAY, 
					serverFromDateCal.get(Calendar.HOUR_OF_DAY)-tempClientFromCal.get(Calendar.HOUR_OF_DAY));
			serverFromDateCal.set(Calendar.MINUTE, 
					serverFromDateCal.get(Calendar.MINUTE)-tempClientFromCal.get(Calendar.MINUTE));
			serverFromDateCal.set(Calendar.SECOND, 0);

			logger.debug("<<<<<<<<<<<<< completed getStartDate ");
			return serverFromDateCal;
		}
		else {
			MessageUtil.setMessage("From date cannot be empty.", "color:red", "TOP");
			logger.debug("<<<<<<<<<<<<< completed getStartDate ");
			return null;
		}
	}//getStartDate

	/**
	 * This method gets the end date
	 * @return serverToDateCal
	 */
	public Calendar getEndDate() {
		logger.debug(">>>>>>>>>>>>> entered in getEndDate");
		if(toDateboxId.getValue() != null && !toDateboxId.getValue().toString().isEmpty()) {
			Calendar serverToDateCal = toDateboxId.getServerValue();
			Calendar tempClientToCal = toDateboxId.getClientValue();

			serverToDateCal.set(Calendar.HOUR_OF_DAY, 
					23+serverToDateCal.get(Calendar.HOUR_OF_DAY)-tempClientToCal.get(Calendar.HOUR_OF_DAY));
			serverToDateCal.set(Calendar.MINUTE, 
					59+serverToDateCal.get(Calendar.MINUTE)-tempClientToCal.get(Calendar.MINUTE));
			serverToDateCal.set(Calendar.SECOND, 59);

			logger.debug("<<<<<<<<<<<<< completed getEndDate ");
			return serverToDateCal;
		}
		else{
			MessageUtil.setMessage("To date cannot be empty.", "color:red", "TOP");
			logger.debug("<<<<<<<<<<<<< completed getEndDate ");
			return null;
		}
	}//getEndDate


	/**
	 * This method validates the date.
	 * @return
	 */
	private boolean isValidate() {
		logger.debug(">>>>>>>>>>>>> entered in isValidate");
		
		if(fromDateboxId.getValue() != null && !fromDateboxId.getValue().toString().isEmpty()) {
			startDate = MyCalendar.getNewCalendar();
			startDate.setTime(fromDateboxId.getValue());
		}
		else {
			MessageUtil.setMessage("From date cannot be empty.", "color:red", "TOP");
			logger.debug("<<<<<<<<<<<<< completed getStartDate ");
			return false;
		}
		if(startDate == null) {
			return false;
		}
		if(toDateboxId.getValue() != null && !toDateboxId.getValue().toString().isEmpty()) {
			 endDate = MyCalendar.getNewCalendar();
			 endDate.setTime(toDateboxId.getValue());
		}
		else{
			MessageUtil.setMessage("To date cannot be empty.", "color:red", "TOP");
			logger.debug("<<<<<<<<<<<<< completed getEndDate ");
			return false;
		}
		if(endDate == null) {
			return false;
		}
		if(endDate.before(startDate)) {
			MessageUtil.setMessage("To date must be later than from date", "color:red", "TOP");
			return false;
		}
		logger.debug("<<<<<<<<<<<<< completed isValidate ");
		return true;
	}//isValidate

	/**
	 * This method captures on select event of time duration listBox
	 */
	public void onSelect$timeDurLbId() {
		logger.debug(">>>>>>>>>>>>> entered in onSelect$timeDurLbId");
		if(contactsLoyaltyObj ==  null){
			MessageUtil.setMessage("Please look-up membership before performing this action.", "red");
			timeDurLbId.setSelectedIndex(0);
			return;
		}

		if(timeDurLbId.getSelectedItem().getLabel().equalsIgnoreCase("Custom Dates")) {
			datesDivId.setVisible(true);
			fromDateboxId.setText("");
			toDateboxId.setText("");
		}
		else {
			datesDivId.setVisible(false);
			getDateValues();
			//Pagination 
			displayPagination(contactsLoyaltyObj);
		}
		logger.debug("<<<<<<<<<<<<< completed onSelect$timeDurLbId ");
	}//onSelect$timeDurLbId


	/**
	 * this method clear's all the fields
	 */
	private void clearFeilds(){
		logger.debug(">>>>>>>>>>>>> entered in clearFeilds");
		//clear's contact Details.
		clearContactDetails();
		//clear's membership summary
		clearMembershipSummary();
		//clear's Transaction Details.
		clearTransactionSummary();
		// clears final balances
		clearFinalBalances();
		// clears Balance summary
		clearBalanceSummary();
		//clear's last purchase details
		//clearLastPurchase();
		//clear's date fields
		clearDateFeilds();
		//clear's pagination details
		clearPaginationDetails();
		//clear transfer details
		clearTransferDetails();
		//Enable add subtract button's
		viewAddSubtractBtn(false);
		clearMoveTier();
		clearSaveContact();
		clearAdjustmentVCDetails();
		/*whichItemsPurchaseAnchId.removeAttribute("docSid");
		whichItemsPromotionAnchId.removeAttribute("docSid");*/
		contactsLoyaltyObj = null;
		displaybrdrLOut.setHeight((700+heightForTrans+transferListLbId.getChildren().size()*30)+"px");
		LoyaltyCustomerLookUpAndRedeemWinId.setHeight((900+heightForTrans+transferListLbId.getChildren().size()*30)+"px");
		timeDurLbId.setSelectedIndex(0);
		getDateValues();
		searchByTbId.setStyle(NORMAL_STYLE);
		searchByTbId.setStyle(ITALIC_GREY_STYLE);
		logger.debug("<<<<<<<<<<<<< completed clearFeilds ");

	}//clearFeilds
	
	private void clearSaveContact(){
		firstNameTbId.setText("");
		lastNameTbId.setText("");
		emailTbId.setText("");
		phoneTbId.setText("");
	}
	
	private void clearMoveTier(){
		currentTierLbId.setValue("Tier adjustment from");
		
		moveTierDivId.setVisible(true);
		/*for(int count = changeTierLbId.getItemCount(); count>0; count--){
			changeTierLbId.removeItemAt(count);
		}*/
		if(toTierLbId.getItemCount() != 0){
		for(int count = toTierLbId.getItemCount(); count>0; count--){
			toTierLbId.removeItemAt(count-1);
		}
		}
	}
	
	private void clearTransferDetails(){
		int count=transferListLbId.getItemCount();
				
				for(; count>0; count--) {
					transferListLbId.removeItemAt(count-1);
				}
	}
	
	private void clearAdjustmentVCDetails(){
		int count=addLbId2.getItemCount();
				
		storeNoTbId.setText(Constants.STRING_NILL);
		receiptNoTbId.setText(Constants.STRING_NILL);
		receiptAmtTbId.setText(Constants.STRING_NILL);
		subTb2Id.setText(Constants.STRING_NILL);
				for(; count>0; count--) {
					Listitem item=addLbId2.getItemAtIndex(count-1);
					if(item.getLabel().equalsIgnoreCase("Currency Balance")||
							item.getLabel().equalsIgnoreCase("Points Balance")||
							item.getLabel().equalsIgnoreCase("Lifetime Purchase Value")||
							item.getLabel().equalsIgnoreCase("Hold Points")||
							item.getLabel().equalsIgnoreCase("Hold Currency")
							) continue;
					//addLbId2.removeItemAt(count-1);
				}
	}

	/**
	 * This method clear's contact Details.
	 */
	private void clearContactDetails() {
		logger.debug(">>>>>>>>>>>>> entered in clearContactDetails");
		nameLblId.setValue(Constants.STRING_NILL);
		membershipLblId.setValue(Constants.STRING_NILL);
		moblieNumberLblId.setValue(Constants.STRING_NILL);
		emailAddressLblId.setValue(Constants.STRING_NILL);	
		
		logger.debug("<<<<<<<<<<<<< completed clearContactDetails ");
	}//clearContactDetails
	/**
	 * This method clear's membership summary
	 */
	private void clearMembershipSummary() {
		logger.debug(">>>>>>>>>>>>> entered in clearMembershipSummary");
		whichprogramDivId.setVisible(false);
		membershipMSummaryLblId.setValue(Constants.STRING_NILL);
		membershipStatusMSummaryLblId.setValue(Constants.STRING_NILL);
		optInDateMSummaryLblId.setValue(Constants.STRING_NILL);
		subsidiaryNumberMSummaryLblId.setValue(Constants.STRING_NILL);
		sourceMSummaryLblId.setValue(Constants.STRING_NILL);
		subsidiaryNumberMSummaryLblId.setValue(Constants.STRING_NILL);
		storeNumberMSummaryLblId.setValue(Constants.STRING_NILL);
		lifeTimePurchaseValueMSummaryLblId.setValue(Constants.STRING_NILL);
		tierNameMSummaryLblId.setValue(Constants.STRING_NILL);
		expirationDateMSummaryLblId.setValue(Constants.STRING_NILL);	
		logger.debug("<<<<<<<<<<<<< completed clearMembershipSummary");
	}//clearMembershipSummary
	/**
	 * This method clear's Transaction Summary
	 */
	private void clearTransactionSummary() {
		logger.debug(">>>>>>>>>>>>> entered in clearTransactionSummary");
		totalIssuanceTSummaryLblId.setValue(Constants.STRING_NILL);
		totalRedemptionsTSummaryLblId.setValue(Constants.STRING_NILL);
		lifetimePointsTSummaryLblId.setValue(Constants.STRING_NILL);
		totalAmountEarnedTSummaryLblId.setValue(Constants.STRING_NILL);
		pointRedeemedTSummaryLblId.setValue(Constants.STRING_NILL);
		amountRedeemedTSummaryLblId.setValue(Constants.STRING_NILL);
		totalGiftTopupAmountTSummaryLblId.setValue(Constants.STRING_NILL);
		giftAmountRedeemedTSummaryLblId.setValue(Constants.STRING_NILL);
		logger.debug("<<<<<<<<<<<<< completed clearTransactionSummary");
	}//clearTransactionSummary

	/**
	 * This method clear's final balances
	 */
	private void clearFinalBalances() {
		logger.debug(">>>>>>>>>>>>> entered in clearFinalBalances");
		addTbId.setValue(Constants.STRING_NILL);
		addLbId.setSelectedIndex(0);
		addLbId2.setSelectedIndex(0);
		subLbId.setSelectedIndex(0);
		addLbId.getItemAtIndex(1).setVisible(true);
		finalBalanceRewardsLblId.setValue(Constants.STRING_NILL);
		finalBalancePointsLblId.setValue(Constants.STRING_NILL);
		finalHoldBalanceLblId.setValue(Constants.STRING_NILL);
		logger.debug("<<<<<<<<<<<<< completed clearFinalBalances");
	}//clearFinalBalances

	/**
	 * This method clear Balance Summary
	 */
	private void clearBalanceSummary() {
		logger.debug(">>>>>>>>>>>>> entered in clearBalanceSummary");
		balancePointLblId.setValue(Constants.STRING_NILL);
		balanceCurrencyLblId.setValue(Constants.STRING_NILL);
		
		balanceCurrency2LblId.setValue(Constants.STRING_NILL);
		balanceCurrency2LblId.removeAttribute("redeemableCurr");//, redeemableCurr);
		expiredPointsLblId.setValue(Constants.STRING_NILL);
		expiredAmountLblId.setValue(Constants.STRING_NILL);
		balanceGiftAmountLblId.setValue(Constants.STRING_NILL);
		expiredGiftAmountLblId.setValue(Constants.STRING_NILL);
		holdBalValLbId.setValue(Constants.STRING_NILL);
		holdBalLbId.setVisible(false);
		logger.debug("<<<<<<<<<<<<< completed clearBalanceSummary");
	}//clearBalanceSummary
	/**
	 * This method clears date feild's.
	 */
	private void clearDateFeilds() {
		logger.debug(">>>>>>>>>>>>> entered in clearDateFeilds");
		Date frmAndTodateDef = null;
		fromDateboxId.setValue(frmAndTodateDef);
		toDateboxId.setValue(frmAndTodateDef);
		datesDivId.setVisible(false);	
		logger.debug("<<<<<<<<<<<<< completed clearDateFeilds");
	}//clearDateFeilds
	/**
	 * This method clear's Pagination Details
	 */
	private void clearPaginationDetails() {
		logger.debug(">>>>>>>>>>>>> entered in clearPaginationDetails");
		pageSizeLbId.setSelectedIndex(0);
		loyaltyListBottomPagingId.setTotalSize(0);
		loyaltyListBottomPagingId.setPageSize(5);
		loyaltyListBottomPagingId.setActivePage(0);
		loyaltyListBottomPagingId.addEventListener("onPaging", this); 
		int count =  transListLbId.getItemCount();
		for(; count>0; count--) {
			transListLbId.removeItemAt(count-1);
		}		
		logger.debug("<<<<<<<<<<<<< completed clearPaginationDetails");
	}//clearPaginationDetails

	/**
	 * This method helps to reset Serach Criteria
	 */
	private void resetSearchFeilds() {
		logger.debug(">>>>>>>>>>>>> entered in resetSearchFeilds");
		fullNameSpanId.setVisible(false);
		searchByTbId.setVisible(true);
		searchByTbId.setValue(Constants.EMAIL_ADDRESS);
		searchByTbId.setStyle(ITALIC_GREY_STYLE);
		first_nameTbId.setValue(Constants.STRING_NILL);
		last_nameTbId.setValue(Constants.STRING_NILL);
		searchbyLbId.setSelectedIndex(0);
		logger.debug("<<<<<<<<<<<<< completed resetSearchFeilds");
	}//resetValues


	/**
	 * This method validate the membership number
	 * @param searchVal
	 * @return true/false
	 */
	private boolean validateMembershipNumber(String searchVal) {
		logger.debug(">>>>>>>>>>>>> entered in validateMembershipNumber");
		if(searchVal.isEmpty() || (Constants.MEMBERSHIP_NUMBER.equalsIgnoreCase(searchVal))) {
			MessageUtil.setMessage("Please enter membership number.", "red");
			//searchByTbId.setValue(Constants.STRING_NILL);
			searchByTbId.setStyle(ERROR_STYLE);
			return false;
		}
		else {
			if(checkIfLong(searchVal)){
				return true;
			}
			else {
				MessageUtil.setMessage("Please enter valid membership number.", "red");
				//searchByTbId.setValue(Constants.STRING_NILL);
				searchByTbId.setStyle(ERROR_STYLE);
				return false;
			}
		}

	}//validateMembershipNumber


	/**
	 * This method validates Email Address
	 * @param searchVal
	 * @return true/false
	 */

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
				findLoyaltyDetails(currUser, searchKey, searchVal);
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
				if((Utility.phoneParse(searchVal,currUser.getUserOrganization())==null)){
					searchByTbId.setStyle(ERROR_STYLE);
					MessageUtil.setMessage("Please enter valid phone number in search field.", "red");
				//	searchByTbId.setValue(Constants.STRING_NILL);
					searchByTbId.setStyle(ERROR_STYLE);
					flag = false;;
				}
				else{
					flag = true;
					String searchKey =	searchbyLbId.getSelectedItem().getValue();
					findLoyaltyDetails(currUser, searchKey, searchVal);
				}
			}
			else {
				MessageUtil.setMessage("Please enter valid phone number.", "red");
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
		if(("First Name".equalsIgnoreCase(first_nameTbId.getValue().trim())) && ("Last Name".equalsIgnoreCase(last_nameTbId.getValue().trim()))||
				(Constants.STRING_NILL.equals(first_nameTbId.getValue().trim()) && Constants.STRING_NILL.equals(last_nameTbId.getValue().trim())) ){
			MessageUtil.setMessage("Please enter name.", "red");
			fullNameSpanId.setVisible(true);
			return false;
		}

		/*if(first_nameTbId.getValue() != null && first_nameTbId.getValue().trim().isEmpty()){
			MessageUtil.setMessage("Please enter first name.", "red");
			fullNameSpanId.setVisible(true);
			return false;
		}

		if(last_nameTbId.getValue() != null && last_nameTbId.getValue().trim().isEmpty()){
			MessageUtil.setMessage("Please enter last name.", "red");
			fullNameSpanId.setVisible(true);
			return false;
		}*/
		
		String fname=Constants.STRING_NILL,lname=Constants.STRING_NILL,str=Constants.STRING_NILL; 
		if(("First Name".equalsIgnoreCase(first_nameTbId.getValue().trim())) || Constants.STRING_NILL.equals(first_nameTbId.getValue().trim()) ){
			fname = Constants.STRING_NILL;
		}
		else{
			fname = first_nameTbId.getValue().trim();
			/*if(!Utility.validateName(fname)){
				MessageUtil.setMessage("Enter valid first name. Special characters are not allowed.","color:red","TOP");
				fullNameSpanId.setVisible(true);
				return false;
			}*/
		}
		if(("Last Name".equalsIgnoreCase(last_nameTbId.getValue().trim())) || Constants.STRING_NILL.equals(last_nameTbId.getValue().trim()) ){
			lname = Constants.STRING_NILL;
		}
		else{
			lname = last_nameTbId.getValue().trim();

			/*if(!Utility.validateName(lname)){
				MessageUtil.setMessage("Enter valid last name. Special characters are not allowed.","color:red","TOP");
				fullNameSpanId.setVisible(true);
				return false ;
			}*/
		}

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

		findLoyaltyDetails(currUser, searchKey, searchVal);
		logger.debug(">>>>>> end of the method :: validateFullName");
		return true;
	}//validateFullName

	//Upgrade Tier Business Logic

	/**
	 * This method is use update Threshold Bonus
	 * @param contactsLoyalty
	 * @param transaction
	 * @param program
	 * @param tier
	 * @param fromAmtBalance 
	 * @param fromLtyBalance 
	 * @param loyaltyProgramTier 
	 * @throws Exception
	 */
	private void updateThresholdBonus(ContactsLoyalty contactsLoyalty, LoyaltyProgram program, Double fromLtyBalance, 
			Double fromAmtBalance,Double fromCPVBalance,Double fromLPVBalance, LoyaltyProgramTier loyaltyProgramTier,boolean tierUpgd) throws Exception {
		logger.debug(">>>>>>>>>>>>> entered in updateThresholdBonus");
		try{
			LoyaltyThresholdBonusDao loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
			List<LoyaltyThresholdBonus> threshBonusList = loyaltyThresholdBonusDao.getBonusListByPrgmId(program.getProgramId(), 'N' );
			List<LoyaltyThresholdBonus> pointsBonusList = new ArrayList<LoyaltyThresholdBonus>();
			List<LoyaltyThresholdBonus> amountBonusList = new ArrayList<LoyaltyThresholdBonus>();
			List<LoyaltyThresholdBonus> LPVBonusList = new ArrayList<LoyaltyThresholdBonus>();
			List<LoyaltyThresholdBonus> TierBonusList = new ArrayList<LoyaltyThresholdBonus>();
			
			fromAmtBalance = fromAmtBalance == null ? 0.0 : fromAmtBalance;
			fromLtyBalance = fromLtyBalance == null ? 0.0 : fromLtyBalance;
			fromLPVBalance = fromLPVBalance == null ? 0.0 : fromLPVBalance;
			fromCPVBalance = fromCPVBalance == null ? 0.0 : fromCPVBalance;

			
			//String[] bonusArr = null; //new String[2];
			if(threshBonusList != null && threshBonusList.size()>0){
				for(LoyaltyThresholdBonus bonus : threshBonusList){
					if(bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_POINTS)){
						pointsBonusList.add(bonus);
					}
					else if (bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
						amountBonusList.add(bonus);
					}
					else if (bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_LPV)){
						LPVBonusList.add(bonus);
					}
					else if (bonus.getEarnedLevelType().equals(OCConstants.THRESHOLD_TYPE_TIER)) {
						TierBonusList.add(bonus);
					}
				}

				List<LoyaltyThresholdBonus> matchedBonusList = new ArrayList<LoyaltyThresholdBonus>();

				
				if (pointsBonusList.size() > 0) {
					Collections.sort(pointsBonusList, new Comparator<LoyaltyThresholdBonus>(){
						@Override
						public int compare(LoyaltyThresholdBonus ltb1, LoyaltyThresholdBonus ltb2) {
							return ltb1.getEarnedLevelValue().compareTo(ltb2.getEarnedLevelValue());
						}
					});
				}
				
				if (amountBonusList.size() > 0) {
					Collections.sort(amountBonusList, new Comparator<LoyaltyThresholdBonus>(){
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
				
				if (TierBonusList.size() > 0) {
					Collections.sort(TierBonusList, new Comparator<LoyaltyThresholdBonus>() {
						@Override
						public int compare(LoyaltyThresholdBonus ltb1, LoyaltyThresholdBonus ltb2) {
							return ltb1.getEarnedLevelValue().compareTo(ltb2.getEarnedLevelValue());
						}
					});
				}
				matchedBonusList.addAll(LPVBonusList);
				matchedBonusList.addAll(pointsBonusList);
				matchedBonusList.addAll(amountBonusList);
				matchedBonusList.addAll(TierBonusList);
				

				/*if(contactsLoyalty.getTotalLoyaltyEarned() != null && contactsLoyalty.getTotalLoyaltyEarned() > 0){
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
*/
				long bonusPoints = 0;
				double bonusAmount = 0.0;
				String bonusRate = null;
				boolean bonusflag =false;
				LoyaltyTransactionChild transaction = null;
				if(matchedBonusList != null && matchedBonusList.size() > 0){
					for (LoyaltyThresholdBonus matchedBonus : matchedBonusList) {
						
						bonusflag = false;
						long multiplier = -1;
						
						double afterBalLoyaltyEarned= contactsLoyalty.getTotalLoyaltyEarned() == null ? 0.0 : contactsLoyalty.getTotalLoyaltyEarned();
						double afterBalGiftCardAmt= contactsLoyalty.getTotalGiftcardAmount() == null ? 0.0 : contactsLoyalty.getTotalGiftcardAmount();
						double afterBalLPV= LoyaltyProgramHelper.getLPV(contactsLoyalty);
						double afterBalCPV= contactsLoyalty.getCummulativePurchaseValue() == null ? 0.0 : contactsLoyalty.getCummulativePurchaseValue();
						
						
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
								multiplier = 1;
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
								
								multiplier = 1;
								bonusflag = true;
							}
							
						}else if(OCConstants.LOYALTY_TYPE_LPV.equals(matchedBonus.getEarnedLevelType())) {
							
							logger.info("---------LPV-----------");
							logger.info("previous points balance (fromLPVBalance)"+fromLPVBalance);
							logger.info("after points balance (getEarnedLevelValue())"+matchedBonus.getEarnedLevelValue());
							
							/*if(matchedBonus.isRecurring()){
								multiplier = LoyaltyProgramHelper.calculateMultiplier(contactsLoyalty, fromCPVBalance, afterBalCPV, matchedBonus.getEarnedLevelValue());
								if(multiplier>0) bonusflag = true;
								logger.info("multiplier===="+multiplier);
							}else if (! matchedBonus.isRecurring() && afterBalCPV >= matchedBonus.getEarnedLevelValue()
									&& (fromCPVBalance == null || fromCPVBalance.doubleValue() < matchedBonus.getEarnedLevelValue())) {
								
								bonusflag = true;
								
							}*/
							multiplier = LoyaltyProgramHelper.doIssueBonus(contactsLoyalty, fromLPVBalance, afterBalLPV, 
									matchedBonus.getEarnedLevelValue(), matchedBonus);
							logger.info("last threshold after bonus ----"+contactsLoyalty.getLastThreshold()==null?0:contactsLoyalty.getLastThreshold());
							if(matchedBonus.isRecurring() && multiplier > 0){
								
								bonusflag = true;
								logger.info("multiplier===="+multiplier);
							
							}else if (! matchedBonus.isRecurring() && multiplier ==0 ) {
								multiplier = 1;
								bonusflag = true;
							}
							
						}else if(tierUpgd && OCConstants.THRESHOLD_TYPE_TIER.equals(matchedBonus.getEarnedLevelType())) {//APP-4508
							
							if(Integer.valueOf(loyaltyProgramTier.getTierType().substring(5))==matchedBonus.getEarnedLevelValue().intValue()) {
								multiplier = 1;
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
							
							LoyaltyTransactionChild childTxbonus = createBonusTransaction(contactsLoyalty, 
									bonusPoints, OCConstants.LOYALTY_TYPE_POINTS, bonusRate);
							transaction = childTxbonus;
							logger.info("balances before balance object = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
							createExpiryTransaction(contactsLoyalty, bonusPoints, bonusAmount, contactsLoyalty.getOrgId(), 
									childTxbonus.getTransChildId(),matchedBonus.getThresholdBonusId());
							if(loyaltyProgramTier != null){
								// CALL CONVERSION
								//applyConversionRules(contactsLoyaltyObj, childTxbonus, program, loyaltyProgramTier);
								// CALL TIER UPGD
								//loyaltyProgramTier = applyTierUpgradeRule(contactsLoyaltyObj, program, childTxbonus, loyaltyProgramTier);
								Long pointsDifference = 0l;
								Double amountDifference = 0.0;
								String[] diffBonArr = applyConversionRules(contactsLoyalty, loyaltyProgramTier); 
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
								amountDifference = (double)bonusAmount + (diffBonArr != null ? Double.parseDouble(diffBonArr[0].trim()) : 0.0);
								
								/*if(loyaltyProgramTier!=null && loyaltyProgramTier.getMultipleTierUpgrdRules()!=null && !loyaltyProgramTier.getMultipleTierUpgrdRules().isEmpty())//APP-4511
									loyaltyProgramTier = applyMultipleTierUpgradeRule(contactsLoyaltyObj, loyaltyProgramTier,program,childTxbonus);
								else
									loyaltyProgramTier = applyTierUpgradeRule(contactsLoyaltyObj, program, childTxbonus, loyaltyProgramTier);*/
								
								String description2 = null;
								updatePurchaseTransaction(childTxbonus, contactsLoyalty, ""+pointsDifference, ""+amountDifference, conversionBonRate, convertBonAmount, loyaltyProgramTier);
							
							
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

							
							LoyaltyTransactionChild childTxbonus = createBonusTransaction(contactsLoyalty, 
									bonusAmount, OCConstants.LOYALTY_TYPE_AMOUNT, bonusRate);
							transaction = childTxbonus;

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
						if(bonusflag &&  contactsLoyalty.getContact() != null &&
								contactsLoyalty.getContact().getContactId() != null){
							contact = findContactById(contactsLoyalty.getContact().getContactId());
							if(contact != null && contact.getEmailId() != null){
								/*autoCommGen.sendEarnBonusTemplate(autoComm.getThreshBonusEmailTmpltId(), ""+contactsLoyalty.getCardNumber(),
										contactsLoyalty.getCardPin(), contact.getUsers(), contact.getEmailId(), contact.getFirstName(),
										contact.getContactId(), contactsLoyalty.getLoyaltyId());*/
								autoCommGen.sendEarnBonusTemplate(autoComm != null ? autoComm.getThreshBonusEmailTmpltId() :null,
										"" + contactsLoyalty.getCardNumber(), contactsLoyalty.getCardPin(),
										contact.getUsers(), contact.getEmailId(), contact.getFirstName(),
										contact.getContactId(), contactsLoyalty.getLoyaltyId(),
										transaction.getTransChildId(),matchedBonus);
							}
						}
						UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
						Users user = userDao.findByUserId(contactsLoyalty.getUserId());
						if(user.isEnableSMS() && bonusflag) { 
							Long contactId = null;	
							if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
								contactId = contactsLoyalty.getContact().getContactId();
							}
							/*autoCommGen.sendEarnBonusSMSTemplate(autoComm.getThreshBonusSmsTmpltId(), user, contactId,
									contactsLoyalty.getLoyaltyId(), null);*/
							autoCommGen.sendEarnBonusSMSTemplate(autoComm!=null ? autoComm.getThreshBonusSmsTmpltId() : null, user, contactId,
									contactsLoyalty.getLoyaltyId(),
									contactsLoyalty.getMobilePhone() != null ? contactsLoyalty.getMobilePhone() : null,
									transaction.getTransChildId(),matchedBonus);
						}
						
						
						
						
						
					}
				}

				

			}
			else{
				logger.error("Thershold bonus is Null");
			}
			logger.debug("<<<<<<<<<<<<< completed updateThresholdBonus");
			//return bonusArr;
		}catch(Exception e){
			logger.error("Exception in update threshold bonus...", e);
			throw new LoyaltyProgramException("Exception in threshold bonus...");
		}
	}//updateThresholdBonus

	private LoyaltyProgramTier getLoyaltyTier(Long tierId) throws Exception{
		
		LoyaltyProgramTierDao tierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		return tierDao.getTierById(tierId);
		
	}
	
	private LoyaltyProgramTier findTier(ContactsLoyalty contactsLoyalty) throws Exception {

		LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);

		List<LoyaltyProgramTier> tiersList = loyaltyProgramTierDao.fetchTiersByProgramId(contactsLoyalty.getProgramId());
		if (tiersList == null || tiersList.size() <= 0) {
			logger.info("Tiers list is empty...");
			return null;
		}
		else if (tiersList.size() >= 1) {
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
		}

		//Prepare eligible tiers map
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
		}

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
		else if(contactsLoyalty.getContact() == null || contactsLoyalty.getContact().getContactId() == null){
			logger.info("contactId is null and selected tier..."+tiersList.get(0).getTierType());
			return tiersList.get(0);
		}
		else if(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE.equals(tiersList.get(0).getTierUpgdConstraint())){
			logger.info("tier condition on :"+OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE);
			
			ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);				

			//List<Map<String, Object>> contactPurcahseList = contactsDao.findContactPurchaseDetails(contactsLoyalty.getUserId(), contactsLoyalty.getContact().getContactId());
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
			totPurchaseValue = LoyaltyProgramHelper.getLPV(contactsLoyalty);
			
			logger.info("purchase value = "+totPurchaseValue);
			
		//	if(contactPurcahseList == null || totPurchaseValue == null || totPurchaseValue <= 0){

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
			try{
				Double cumulativeAmount = 0.0;
//				Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
				ListIterator<LoyaltyProgramTier> it = new ArrayList(eligibleMap.keySet()).listIterator(eligibleMap.size());
//				LoyaltyProgramTier prevKeyTier = null;
				LoyaltyProgramTier nextKeyTier = null;
				while(it.hasPrevious()){
					nextKeyTier = it.previous();
					logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
					logger.info("-------------currTier::"+tiersList.get(0).getTierType());
					if(OCConstants.LOYALTY_PROGRAM_TIER1.equalsIgnoreCase(nextKeyTier.getTierType())){
//						prevKeyTier = nextKeyTier;
						return tiersList.get(0);
					}
					Calendar startCal = Calendar.getInstance();
					Calendar endCal = Calendar.getInstance();
					endCal.add(Calendar.MONTH, -eligibleMap.get(nextKeyTier).getTierUpgradeCumulativeValue().intValue());

					String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
					String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
					logger.info("contactId = "+contactsLoyalty.getContact().getContactId()+" startDate = "+startDate+" endDate = "+endDate);

					/*RetailProSalesDao salesDao = (RetailProSalesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
					Object[] cumulativeAmountArr = salesDao.getCumulativePurchase(contactsLoyalty.getUserId(), contactsLoyalty.getContact().getContactId(), startDate, endDate);
					cumulativeAmount = Double.valueOf(cumulativeAmountArr[0].toString());*/

					LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
					cumulativeAmount = Double.valueOf(loyaltyTransactionChildDao.getLoyaltyCumulativePurchase(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId(), startDate, endDate));

					if(cumulativeAmount == null || cumulativeAmount <= 0){
						logger.info("cumulative purchase value is empty...");
						continue;
					}
					
					if(cumulativeAmount > 0 && cumulativeAmount >= eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
						return nextKeyTier;
					}
					
				}
				/*while(it.hasNext()){
					nextKeyTier = it.next();
					logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
					logger.info("-------------tiersList.get(0)::"+tiersList.get(0).getTierType());
					if(OCConstants.LOYALTY_PROGRAM_TIER1.equalsIgnoreCase(nextKeyTier.getTierType())){
						prevKeyTier = nextKeyTier;
						continue;
					}
					Calendar startCal = Calendar.getInstance();
					Calendar endCal = Calendar.getInstance();
					endCal.add(Calendar.MONTH, -((LoyaltyProgramTier) eligibleMap.get(nextKeyTier)).getTierUpgradeCumulativeValue().intValue());

					String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
					String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
					logger.info("contactId = "+contactId+" startDate = "+startDate+" endDate = "+endDate);

					Object[] cumulativeAmountArr = getCumulativeValue(startDate, endDate);

					cumulativeAmount = Double.valueOf(cumulativeAmountArr[0].toString());

					if(cumulativeAmount == null || cumulativeAmount <= 0){
						logger.info("cumulative purchase value is empty...");
						continue;
					}
					if(cumulativeAmount > 0 && cumulativeAmount < eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
						if(prevKeyTier == null){
							logger.info("selected tier is currTier..."+tiersList.get(0).getTierType());
							return tiersList.get(0);
						}
						logger.info("selected tier..."+prevKeyTier.getTierType());
						return prevKeyTier;
					}
					else if (cumulativeAmount > 0 && cumulativeAmount >= eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue() && !it.hasNext()) {
						logger.info("selected tier..."+nextKeyTier.getTierType());
						return nextKeyTier;
					}
					prevKeyTier = nextKeyTier;
				}*/
				return tiersList.get(0);
			}catch(Exception e){
				logger.error("Excepion in cpv thread ", e);
				return tiersList.get(0);
			}
		}
		else{
			return null;
		}
	}
	
	
	/**
	 * This method is use apply Tier Upgrade Rule
	 * @param contactsLoyalty
	 * @param transaction
	 * @param program
	 * @param transactionChild 
	 * @param tier
	 * @return 
	 */
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
	
	
	private LoyaltyProgramTier applyMultipleTierUpgradeRule(ContactsLoyalty contactsLoyalty,
			LoyaltyProgramTier currTier,LoyaltyProgram program,LoyaltyTransactionChild transactionChild) {

		try {
			boolean tierUpgd = false;
			LoyaltyMultipleTierUpgradeRules mulUpgrdRuls = new LoyaltyMultipleTierUpgradeRules();
			LoyaltyProgramTier newTier = mulUpgrdRuls
					.applyMultipleTierUpgdRules(contactsLoyalty.getContact().getContactId(), contactsLoyalty, currTier);
			if (!newTier.getTierType().equalsIgnoreCase(currTier.getTierType())) {
				currTier = newTier;
				tierUpgd = true;
			}

			if (tierUpgd) {
				contactsLoyalty.setProgramTierId(currTier.getTierId());
				contactsLoyalty.setTierUpgradedDate(Calendar.getInstance());
				//contactsLoyalty.setTierUpgradeReason(currTier.getTierUpgdConstraint());
				ContactsLoyaltyDaoForDML contactsLoyaltyDao = (ContactsLoyaltyDaoForDML) ServiceLocator.getInstance()
						.getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
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

			// contactsLoyaltyDao.saveOrUpdate(contactsLoyalty);
		} catch (Exception e) {
			logger.error("Exception while upgrading tier...", e);
		}
		return currTier;
	}

	/**
	 * This method is use to apply Conversion Rules
	 * @param contactsLoyalty
	 * @param transaction
	 * @param program
	 * @param tier
	 */
	/*private void applyConversionRules(ContactsLoyalty contactsLoyalty, LoyaltyTransactionChild transaction, LoyaltyProgram program, LoyaltyProgramTier tier){
		logger.debug(">>>>>>>>>>>>> entered in applyConversionRules");
		String[] differenceArr = null;

		try{

			if(tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO)){

				if(tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0 
						&& contactsLoyalty.getLoyaltyBalance() != null && contactsLoyalty.getLoyaltyBalance() > 0 
						&& contactsLoyalty.getLoyaltyBalance() >= tier.getConvertFromPoints()){

					differenceArr = new String[3];

					double multipledouble = contactsLoyalty.getLoyaltyBalance()/tier.getConvertFromPoints();
					//int multiple = (int)multipledouble;
					double multiple = multipledouble;
					//double convertedAmount = tier.getConvertToAmount() * multiple;
					double convertedAmount = 0.0;
					String result = Utility.truncateUptoTwoDecimal(tier.getConvertToAmount() * multiple);
					if(result != null)
						convertedAmount = Double.parseDouble(result);
					//double convertedAmount = new BigDecimal(tier.getConvertToAmount() * multiple).setScale(2, RoundingMode.FLOOR).doubleValue();
					//double subPoints = multiple * tier.getConvertFromPoints();
					String res = Utility.truncateUptoTwoDecimal(multiple * tier.getConvertFromPoints());
					double subPoints = 0.0;
					if(res != null)
						subPoints = Double.parseDouble(res);

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
						//contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() + convertedAmount);
						contactsLoyalty.setGiftcardBalance(new BigDecimal(contactsLoyalty.getGiftcardBalance() + convertedAmount)));
					}
					if(contactsLoyalty.getTotalGiftcardAmount() == null){
						contactsLoyalty.setTotalGiftcardAmount(convertedAmount);
					}
					else{
						//contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount() + convertedAmount);
						contactsLoyalty.setTotalGiftcardAmount(Double.parseDouble(Utility.truncateUptoTwoDecimal(contactsLoyalty.getTotalGiftcardAmount() + convertedAmount)));
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
					deductPointsFromExpiryTable(contactsLoyalty, subPoints, convertedAmount);
				}
			}

		}catch(Exception e){
			logger.error("Exception while applying auto conversion rules...", e);
		}
		logger.debug("<<<<<<<<<<<<< completed applyConversionRules");
	}//applyConversionRules
*/
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
						//contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() + convertedAmount);
						contactsLoyalty.setGiftcardBalance(new BigDecimal(contactsLoyalty.getGiftcardBalance() + convertedAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					}
					if(contactsLoyalty.getTotalGiftcardAmount() == null){
						contactsLoyalty.setTotalGiftcardAmount(convertedAmount);
					}
					else{
						//contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount() + convertedAmount);
						contactsLoyalty.setTotalGiftcardAmount(new BigDecimal(contactsLoyalty.getTotalGiftcardAmount() + convertedAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					}
					
					//deduct loyalty points
					contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() - subPoints);
					contactsLoyalty.setTotalLoyaltyRedemption(contactsLoyalty.getTotalLoyaltyRedemption() == null ? subPoints :
						contactsLoyalty.getTotalLoyaltyRedemption() + subPoints);
					
					logger.info("contactsLoyalty.getGiftcardBalance() = "+contactsLoyalty.getGiftcardBalance());
					
					deductPointsFromExpiryTable(contactsLoyalty, subPoints, convertedAmount);
				}
			}
		
		}catch(Exception e){
			logger.error("Exception while applying auto conversion rules...", e);
			return null;
		}
		return differenceArr;
	}
	
	
	/**
	 * This method is use create Bonus Transaction
	 * @param transaction
	 * @param loyalty
	 * @param earnedValue
	 * @param earnType
	 * @param bonusRate
	 * @return loyaltyTransactionChild
	 */
	private LoyaltyTransactionChild createBonusTransaction(ContactsLoyalty loyalty,
			double earnedValue, String earnType, String bonusRate){
		logger.debug(">>>>>>>>>>>>> entered in createBonusTransaction");
		LoyaltyTransactionChild bonusTransaction = null;
		try{

			bonusTransaction = new LoyaltyTransactionChild();
			bonusTransaction.setMembershipNumber(loyalty.getCardNumber()+"");
			bonusTransaction.setMembershipType(loyalty.getMembershipType());
			bonusTransaction.setCardSetId(loyalty.getCardSetId());

			bonusTransaction.setCreatedDate(Calendar.getInstance());
			bonusTransaction.setEarnType(earnType);
			if(earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)){
				bonusTransaction.setEarnedPoints(earnedValue);
			}
			else if(earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
				bonusTransaction.setEarnedAmount(earnedValue);
			}
			bonusTransaction.setEnteredAmount((double)earnedValue);
			//bonusTransaction.setEarnStatus(earnStatus);
			//bonusTransaction.setEnteredAmountType(entAmountType);
			bonusTransaction.setOrgId(loyalty.getOrgId());
			bonusTransaction.setPointsBalance(loyalty.getLoyaltyBalance());
			bonusTransaction.setGiftBalance(loyalty.getGiftBalance());
			bonusTransaction.setAmountBalance(loyalty.getGiftcardBalance());
			bonusTransaction.setProgramId(loyalty.getProgramId());
			bonusTransaction.setTierId(loyalty.getProgramTierId());
			bonusTransaction.setUserId(loyalty.getUserId());
			bonusTransaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_BONUS);
			bonusTransaction.setDescription("Threshold bonus: "+bonusRate);
			bonusTransaction.setLoyaltyId(loyalty.getLoyaltyId());
			bonusTransaction.setReceiptNumber(" Manual");
			//setting Description2 
			bonusTransaction.setDescription2(" Manual");

			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			//loyaltyTransactionChildDao.saveOrUpdate(bonusTransaction);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(bonusTransaction);

		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
		}
		logger.debug("<<<<<<<<<<<<< completed createBonusTransaction");
		return bonusTransaction;
	}//createBonusTransaction


	
	/**
	 * This method is use to create Expiry Transaction
	 * @param loyalty
	 * @param expiryPoints
	 * @param expiryAmount
	 * @param orgId
	 * @param transChildId
	 * @param transType
	 * @param tier
	 * @return
	 */
	private LoyaltyTransactionExpiry createExpiryTransaction(ContactsLoyalty loyalty,
			Long expiryPoints, Double expiryAmount, Long orgId, Long transChildId,Long bonusId){
		logger.debug(">>>>>>>>>>>>> entered in createExpiryTransaction");
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
			transaction.setRewardFlag(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			transaction.setBonusId(bonusId);

			LoyaltyTransactionExpiryDao loyaltyTransactionExpiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
			LoyaltyTransactionExpiryDaoForDML loyaltyTransactionExpiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
			//loyaltyTransactionExpiryDao.saveOrUpdate(transaction);
			loyaltyTransactionExpiryDaoForDML.saveOrUpdate(transaction);

		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
		}
		logger.debug("<<<<<<<<<<<<< completed createExpiryTransaction");
		return transaction;
	}//createExpiryTransaction

	/**
	 * This method is used to deduct auto converted Points From ExpiryTable
	 * @param contactLoyalty
	 * @param subPoints
	 * @param earnedAmt
	 * @throws Exception
	 */
	private void deductPointsFromExpiryTable(ContactsLoyalty contactLoyalty, double subPoints, double earnedAmt) throws Exception{
		logger.debug(">>>>>>>>>>>>> entered in deductPointsFromExpiryTable");
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
		List<LoyaltyTransactionExpiry> expiryList = null; //expiryDao.fetchExpPointsTrans(""+membershipNumber, 100, userId);
		Iterator<LoyaltyTransactionExpiry> iterList = null; //expiryList.iterator();
		LoyaltyTransactionExpiry expiry = null;
		long remainingPoints = (long)subPoints;

		do{

			expiryList = expiryDao.fetchExpLoyaltyPtsTrans(contactLoyalty.getLoyaltyId(), 
					100, contactLoyalty.getUserId());
			//logger.info("expiryList size = "+expiryList.size());
			if(expiryList == null || remainingPoints <= 0) break;
			iterList = expiryList.iterator();

			while(iterList.hasNext()){

				logger.info("remainingPoints = "+remainingPoints +" earnedAmt = "+earnedAmt);
				expiry = iterList.next();

				//logger.info("expiry points= "+expiry.getExpiryPoints()+" expiry amount = "+expiry.getExpiryAmount());

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

		}while(remainingPoints > 0);
		logger.debug("<<<<<<<<<<<<< completed deductPointsFromExpiryTable");
	}//deductPointsFromExpiryTable

	/**
	 * This method is used to deduct subtracted Points From ExpiryTable	 * 
	 * @param loyalty
	 * @param subPoints
	 * @throws Exception
	 */
	private void deductPointsFromExpiryTable(ContactsLoyalty loyalty, long subPoints) throws Exception{

		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
		List<LoyaltyTransactionExpiry> expiryList = null; 
		Iterator<LoyaltyTransactionExpiry> iterList = null;
		LoyaltyTransactionExpiry expiry = null;
		long remPoints = subPoints;

		do{
			expiryList = expiryDao.fetchExpLoyaltyPtsTrans(loyalty.getLoyaltyId(), 100, loyalty.getUserId());
			if(expiryList == null || remPoints <= 0) break;
			iterList = expiryList.iterator();

			while(iterList.hasNext()){
				expiry = iterList.next();

				if(expiry.getExpiryPoints() == null || expiry.getExpiryPoints() <= 0){ 
					logger.info("WRONG EXPIRY TRANSACTION FETCHED...");
					continue;
				}
				else if(expiry.getExpiryPoints() < remPoints){
					logger.info("subtracted loyalty points = "+expiry.getExpiryPoints());
					remPoints = remPoints - expiry.getExpiryPoints().longValue();
					expiry.setExpiryPoints(0l);
					//expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					continue;

				}
				else if(expiry.getExpiryPoints() >= remPoints){
					logger.info("subtracted loyalty points = "+expiry.getExpiryPoints());
					expiry.setExpiryPoints(expiry.getExpiryPoints() - remPoints);
					remPoints = 0; 
					//expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					break;
				}

			}
			expiryList = null;

		}while(remPoints > 0);

		//createTransactionForExpiry(loyalty, subPoints-remPoints, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTS_EXP);
	}
	
	/**
	 * This method is used to deduct subtracted amount From ExpiryTable	 * 
	 * @param loyalty
	 * @param subPoints
	 * @throws Exception
	 */
	private void deductLoyaltyAmtFromExpiryTable(ContactsLoyalty loyalty, double subAmt) throws Exception{
		
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
		List<LoyaltyTransactionExpiry> expiryList = null; 
		Iterator<LoyaltyTransactionExpiry> iterList = null;
		LoyaltyTransactionExpiry expiry = null;
		double remAmount = subAmt;
		
		do{
			expiryList = expiryDao.fetchExpLoyaltyAmtTrans(loyalty.getLoyaltyId(), 100, loyalty.getUserId());
			if(expiryList == null || remAmount <= 0) break;
			iterList = expiryList.iterator();
			
			while(iterList.hasNext()){
				expiry = iterList.next();
				
				if(expiry.getExpiryAmount() == null || expiry.getExpiryAmount() <= 0){ 
					logger.info("WRONG EXPIRY TRANSACTION FETCHED...");
					continue;
				}
				else if(expiry.getExpiryAmount() < remAmount){
					logger.info("subtracted loyalty amount = "+expiry.getExpiryAmount());
					remAmount = remAmount - expiry.getExpiryAmount().doubleValue();
					expiry.setExpiryAmount(0.0);
					//expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					logger.info("Expiry Amount deducted..."+expiry.getExpiryAmount().doubleValue());
					continue;
					
				}
				else if(expiry.getExpiryAmount() >= remAmount){
					logger.info("subtracted loyalty amount = "+expiry.getExpiryAmount());
					expiry.setExpiryAmount(expiry.getExpiryAmount() - remAmount);
					remAmount = 0; 
					//expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					logger.info("Expiry Amount deducted..."+remAmount);
					break;
				}
				
			}
			expiryList = null;
		
		}while(remAmount > 0);
		
		//createTransactionForExpiry(loyalty, subAmt, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNT_EXP);
	}
	
	/**
	 * This method is use to get loyalty auto comm 
	 * @param programId
	 * @return LoyaltyAutoComm
	 * @throws Exception
	 */
	private LoyaltyAutoComm getLoyaltyAutoComm(Long programId) throws Exception {
		logger.debug(">>>>>>>>>>>>> entered in getLoyaltyAutoComm");
		LoyaltyAutoCommDao autoCommDao = (LoyaltyAutoCommDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
		logger.debug("<<<<<<<<<<<<< completed getLoyaltyAutoComm ");
		return autoCommDao.findById(programId);
	}//getLoyaltyAutoComm

	/**
	 * This method is use to find contact based on contact id
	 * @param cid
	 * @return contact
	 * @throws Exception
	 */
	private Contacts findContactById(Long cid) throws Exception {
		logger.debug(">>>>>>>>>>>>> entered in findContactById");
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		logger.debug("<<<<<<<<<<<<< completed findContactById");
		return contactsDao.findById(cid);
	}//findContactById
	
	private LoyaltyTransactionChild createMoveTierTransaction(ContactsLoyalty cl){
		LoyaltyTransactionChild loyaltyTransactionChild = new LoyaltyTransactionChild();
		loyaltyTransactionChild.setMembershipNumber(cl.getCardNumber());
		loyaltyTransactionChild.setMembershipType(cl.getMembershipType());
		loyaltyTransactionChild.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_CHANGE_TIER);
		loyaltyTransactionChild.setProgramId(cl.getProgramId());
		loyaltyTransactionChild.setCardSetId(cl.getCardSetId());
		loyaltyTransactionChild.setTierId(cl.getProgramTierId());
		loyaltyTransactionChild.setUserId(cl.getUserId());
		loyaltyTransactionChild.setOrgId(cl.getOrgId());
		loyaltyTransactionChild.setAmountBalance(cl.getGiftcardBalance());
		loyaltyTransactionChild.setPointsBalance(cl.getLoyaltyBalance());
		loyaltyTransactionChild.setGiftBalance(cl.getGiftBalance());
		//loyaltyTransactionChild.setAmountDifference(cl.getGiftcardBalance() != null ? cl.getGiftcardBalance()+Constants.STRING_NILL : null);
		//loyaltyTransactionChild.setPointsDifference(cl.getLoyaltyBalance() != null ? cl.getLoyaltyBalance()+Constants.STRING_NILL : null);
		//loyaltyTransactionChild.setGiftDifference(cl.getGiftBalance() != null ? cl.getGiftBalance()+Constants.STRING_NILL : null);
		loyaltyTransactionChild.setHoldAmount(cl.getHoldAmountBalance());
		loyaltyTransactionChild.setHoldPoints(cl.getHoldPointsBalance());
		loyaltyTransactionChild.setCreatedDate(Calendar.getInstance());
		loyaltyTransactionChild.setSourceType(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MANUAL);
		loyaltyTransactionChild.setContactId(cl.getContact() == null ? null : cl.getContact().getContactId());
		loyaltyTransactionChild.setLoyaltyId(cl.getLoyaltyId());
		return loyaltyTransactionChild;
	}

	/**
	 * This method is use to operate on Purchase Transaction
	 * @param loyalty
	 * @param adjustValue
	 * @param earnType
	 * @param entAmountType
	 * @param activationDate 
	 * @return loyaltyTransactionChild
	 */
	//ask mam
	private LoyaltyTransactionChild createPurchaseTransaction(ContactsLoyalty loyalty, Double adjustValue, String earnType,
															String entAmountType, String activationDate,String earnStatus, String adjType,String description2){
		logger.debug(">>>>>>>>>>>>> entered in createPurchaseTransaction");
		LoyaltyTransactionChild transaction = null;
		try{

			transaction = new LoyaltyTransactionChild();
			transaction.setMembershipNumber(""+loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCardSetId(loyalty.getCardSetId());
			transaction.setCreatedDate(Calendar.getInstance());
			transaction.setEarnType(earnType);
			if(earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)){
				long adjustValueLong = adjustValue.longValue();//APP-823
				transaction.setEarnedPoints(adjustValue);
				transaction.setPointsDifference(""+(adjType.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD)?adjustValueLong:-adjustValueLong));
			}
			else if(earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
				transaction.setEarnedAmount(adjustValue);
				transaction.setAmountDifference(""+(adjType.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD)?adjustValue:-adjustValue));

			}else{
				transaction.setRewardDifference(""+(adjType.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD)?adjustValue:-adjustValue));
				transaction.setEarnedReward(adjustValue);
			}
			if(earnStatus != null) {
				transaction.setEarnStatus(earnStatus);
			}
			if(activationDate != null){
				if(earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)){
					transaction.setHoldPoints((double)adjustValue);
				}
				else if(earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
					transaction.setHoldAmount((double)adjustValue);
				}
				transaction.setValueActivationDate(new SimpleDateFormat("yyyy-MM-dd").parse(activationDate));
			}
			transaction.setEnteredAmount(adjustValue);
			transaction.setEnteredAmountType(adjType);
			transaction.setOrgId(loyalty.getOrgId());
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setUserId(loyalty.getUserId());
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT);
			transaction.setSourceType(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MANUAL);
			transaction.setContactId(loyalty.getContact() == null ? null : loyalty.getContact().getContactId());
//			transaction.setEventTriggStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			//transaction.setDescription2(subTb2Id.getValue().trim());
			description2=subTb2Id.getValue().trim();
			transaction.setDescription2(description2);
			transaction.setStoreNumber(storeNoTbId.getValue().trim());
			transaction.setReceiptNumber(receiptNoTbId.getValue().trim());
			if((receiptAmtTbId!=null && !receiptAmtTbId.getValue().isEmpty()))
			transaction.setReceiptAmount(Double.parseDouble(receiptAmtTbId.getValue().trim()));
			
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			//loyaltyTransactionChildDao.saveOrUpdate(transaction);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
			
			//Event Trigger sending part
			EventTriggerEventsObservable eventTriggerEventsObservable = (EventTriggerEventsObservable) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVABLE);
			EventTriggerEventsObserver eventTriggerEventsObserver = (EventTriggerEventsObserver) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVER);
			eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
			EventTriggerDao eventTriggerDao  = (EventTriggerDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EVENT_TRIGGER_DAO);
			List<EventTrigger> etList = eventTriggerDao.findAllETByUserAndType(transaction.getUserId(),Constants.ET_TYPE_ON_LOYALTY_ADJUSTMENT);
			
			if(etList != null) {
				eventTriggerEventsObservable.notifyToObserver(etList, transaction.getTransChildId(), transaction.getTransChildId(), 
																transaction.getUserId(), OCConstants.LOYALTY_ADJUSTMENT,Constants.ET_TYPE_ON_LOYALTY_ADJUSTMENT);
			}

		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
		}
		
		logger.debug("<<<<<<<<<<<<< completed createPurchaseTransaction");
		return transaction;
	}
	private void updatePurchaseTransaction(LoyaltyTransactionChild transaction, ContactsLoyalty loyalty,
			String ptsDiff, String amtDiff, String conversionRate, double convertAmt, LoyaltyProgramTier tier){
		
		try{
			
			transaction.setAmountDifference(Utility.truncateUptoTwoDecimal(Double.parseDouble(amtDiff)));
			transaction.setPointsDifference(ptsDiff);
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setDescription(conversionRate);
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
	private void userActivitylogAdminUser(String actionType,String membershipId,String lolyaltyProgramName) {
		if(userActivitiesDaoForDML != null) {
			try {
				String name = null;
				if(currUser.getLastName()!=null && !currUser.getLastName().isEmpty()) {
					 name = Utility.getOnlyUserName(currUser.getFirstName()+" "+currUser.getLastName());	
				}else {
					 name = Utility.getOnlyUserName(currUser.getFirstName());
				}
				UsersDao usersDao;
				usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
				Users adminUser = usersDao.find(currentOCAdminUserId);
				String adminUsername = Utility.getOnlyUserName(adminUser.getUserName());
		      	userActivitiesDaoForDML.addToActivityList(ActivityEnum.LOYALTY_CUSTOMER_LOOP_UP_PASSWORD_BYPASS,currUser,true,adminUsername,actionType,membershipId,lolyaltyProgramName,name);
			} catch (Exception e) {
				logger.error("LoyaltyCustomerLookUpController :: userActivitylogAdminUser ::"+e);
			}
			
		}
	}

	
	/**
	 * APP-2055
	 */
	public void onClick$verifyBtnId(){
			
		//TODO validation
		String retMsg = validatdata("verify");
		if(retMsg != null ){
			
			MessageUtil.setMessage(retMsg, "red");
			return;
		}
			
		
		//String response = null;
		//if(program is enabled with OTP then only prepare the request)
		LoyaltyProgramTier tier = (LoyaltyProgramTier)tierNameMSummaryLblId.getAttribute("tier");
		//LoyaltyProgram loyaltyProgram = (LoyaltyProgram)programNameLbId.getAttribute("program");
		Double redeemableCurrency = (Double)(balanceCurrency2LblId.getAttribute("redeemableCurr"));
		
		boolean sendOTP = (tier.getRedemptionOTPFlag() == 'Y') ;
		if(tier.getOtpLimitAmt() != null && tier.getOtpLimitAmt().doubleValue() > 0 
				&& redeemedAmountTbId.getValue() < tier.getOtpLimitAmt().doubleValue()){//TODO condition check
			sendOTP = false;
		}
		/*Double OTPLimitAmt = loyaltyProgram.getOtpLimitAmt()!=null ? loyaltyProgram.getOtpLimitAmt() : 0.0;
		if( OTPLimitAmt != 0.0 && redeemableCurrency>=redeemableCurrency){
			sendOTP = true;
		}*/
		
		if(sendOTP) {
			
			LoyaltyOTPResponse otpresponse = prepareLoyaltyOTPRequest();
			
			if(otpresponse!=null) {
				Status status = otpresponse.getStatus(); 
				if(status!=null){
					if(status.getErrorCode()!=null && status.getErrorCode().equals("0") ){
						errorMessage(status.getMessage(), styleSuccess);
						otpDivId.setVisible(true);
						otpTbId.setDisabled(false);
					}else {
						errorMessage(status.getMessage(),styleError );
						otpDivId.setVisible(false);
						otpTbId.setDisabled(true);
					}
				}else {
					errorMessage("An Error occured", styleError);
					otpDivId.setVisible(false);
				}	
			}
		}
		else{
			otpDivId.setVisible(true);
			otpTbId.setDisabled(true);
			
		}
	}
	
	
	public void onClick$applyBtnId(){
		
		//TODO Validation
		String errorMsg = validatdata("apply");
		if(errorMsg != null) {
			MessageUtil.setMessage(errorMsg, "red");
			return;
			
		}
		LoyaltyRedemptionResponse loyaltyRedemptionResponse = prepareLoyaltyRedemptionRequest();

		if(loyaltyRedemptionResponse!=null) {
			Status status = loyaltyRedemptionResponse.getStatus(); 
			if(status!=null){
				if(status.getErrorCode()!=null && status.getErrorCode().equals("0") && status.getMessage()!=null){
					MessageUtil.setMessage(MessageUtil.getOnlyMessage(status.getMessage()), "blue");
					//errorMessage(status.getMessage(),styleSuccess);
					//otpDivId.setVisible(true);
					Redirect.goTo(PageListEnum.EMPTY);
				    Redirect.goTo(PageListEnum.LOYALTY_CUSTOMER_LOOKUP_AND_REDEEM);
					return;
				}else {
					errorMessage(status.getMessage(), styleError);
					otpDivId.setVisible(false);
				}
			}else {
				errorMessage("An Error occured : Unable to redeemed", styleError);
				otpDivId.setVisible(false);
			} 
		}
	}
	private final String styleError="font-size:12px;color:red;font-weight:bold;";
	private final String styleSuccess="font-size:12px;color:green;font-weight:bold;";
	public String validatdata(String applyOrVerify) {
		
		try {
			errorMsgTbId.setValue("");
			if(contactsLoyaltyObj == null){
				return ("Please look-up membership before performing this action.");
				
			}
			
			//if(contactsLoyaltyObj.getGiftcardBalance() == null) return("Redeemable balance not available"); 		
			
			if(redeemedAmountTbId.getValue()==null || redeemedAmountTbId.getValue() == 0 ){
				
				return ("Redeem Amount should be greater than zero.");
			}
			
			logger.info("redeemedAmountTbId.getValue()"+redeemedAmountTbId.getValue());
			if(redeemedAmountTbId.getValue()!=null && redeemedAmountTbId.getValue()<=0.0)
				return("Redeem Amount should be greater than zero.");
			
			Double redeemedAmount = redeemedAmountTbId.getValue();
			Double redeemableCurrency = (Double)(balanceCurrency2LblId.getAttribute("redeemableCurr"));
			if( redeemableCurrency<=0.0)
				return("Redeemable balance Amount should be greater than zero.");
			
			//Double giftcardBalanceAmount = Double.parseDouble(Utility.getAmountInUSFormat(contactsLoyaltyObj.getGiftcardBalance()));
			
			logger.debug("redeemedAmount === "+redeemedAmount+" redeemableCurrency==="+redeemableCurrency);
			if(redeemedAmount > redeemableCurrency) return("Redeem Amount should not be greater than Redeemable Balance.");
			
			if(applyOrVerify.equals("apply")) {
				if(receiptAmountTbId.getValue() == null) return ("Please enter Receipt Amount.");
				if( receiptAmountTbId.getValue()<=0.0) return("Receipt Amount should be greater than 0.");
				if( storeNumberTbId.getValue()!=null && storeNumberTbId.getValue().isEmpty() ) return("Store number should not be empty.");
				if( receiptNumTbId.getValue().isEmpty()) return("Receipt Number should not be empty or negative.");
				if(!otpTbId.isDisabled() && otpTbId.getValue().isEmpty())return("OTP Code is required.");
				if(receiptAmountTbId.getValue() < redeemedAmountTbId.getValue()) return ("Receipt Amount can not be less than redeem amount.");
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("Exception :: "+e);
			logger.error("Exception ::", e);
		}
		return null;
		
	}
	
	public void clearData() {
		//redeemedAmountTbId.setValue(0);
		receiptAmountTbId.setValue(null);
		storeNumberTbId.setValue("");
		receiptNumTbId.setValue("");
		otpTbId.setValue("");
	}
	
	/**
	 * ErrorMsg to display in screen based on response from LoyaltyRedemptionRequest
	 * @return String
	 */
	public String errorMessage(String responsetype, String style) {
		responsetype = MessageUtil.getOnlyMessage(responsetype);
		errorMsgTbId.setValue("");
		errorMsgTbId.setValue(responsetype);
		errorMsgTbId.setStyle(style);
		
		clearData();
		return null;
	}
	
	//String docSIDcurrentTime = (System.currentTimeMillis())+Constants.STRING_NILL;

	
	
	public LoyaltyOTPResponse prepareLoyaltyOTPRequest() {

		try {
			LoyaltyOTPRequest otpRequest = new LoyaltyOTPRequest();
			OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
			UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			
			
			RequestHeader requestHeader = new RequestHeader();

			String requestIdcurrentTime = (System.currentTimeMillis())+Constants.STRING_NILL;
			
			Calendar cal =  Calendar.getInstance();
			String requestedDate = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STYEAR,null);
			
			requestHeader.setRequestId(requestIdcurrentTime);
			requestHeader.setRequestType(OCConstants.OTP_REQUSET_TYPE_ISSUE);// Issuance *****
			requestHeader.setRequestDate(requestedDate);
			//requestHeader.setPcFlag(pcFlag); //???????
			
			
			/**
			 * StoreNumber as dummy(2) its length should be greater than Zero, because it is validated in OTPRequestImpl
			 * but we are not making use of StoreNumber for generating OTP
			 * 
			 */
			
			requestHeader.setStoreNumber(OCConstants.DUMMY_STORE_NUMBER);//******     compulsory 
			/*Long domainId = userDao.findDomainByUserId(currUser.getUserId());
			if(domainId!=null){
				OrganizationStores orgStores = organizationStoresDao.findOrgByDomain(currUser.getUserOrganization().getUserOrgId(), domainId, storeNumber);
				requestHeader.setSubsidiaryNumber(orgStores!=null ? orgStores.getSubsidiaryId() : null);
			}*/

			
			//requestHeader.setEmployeeId(employeeId);????????
			//requestHeader.setTerminalId(terminalId);????????  SparkBased
					
			requestHeader.setDocSID("");
			//requestHeader.setReceiptNumber(receiptNumber);???????????? 
			
			otpRequest.setHeader(requestHeader);		
					
					
			LoyaltyUser user = new LoyaltyUser();
			String token = currUser.getToken() == null || currUser.getToken().isEmpty() ? currUser.getUserOrganization().getOptSyncKey() : currUser.getToken();
			user.setUserName(Utility.getOnlyUserName(currUser.getUserName()));
			user.setOrganizationId(Utility.getOnlyOrgId(currUser.getUserName()));
			user.setToken(token);
			
			otpRequest.setUser(user);
			
			
			/*String otpCode;
			otpRequest.setOtpCode(null);*/
			
			Customer customer = new Customer();
			
			Contacts contactsObj = contactsLoyaltyObj.getContact();
			//TODO validation
			customer.setCustomerId(contactsObj.getExternalId());
			customer.setInstanceId(contactsObj.getInstanceId());
			customer.setFirstName(contactsObj.getFirstName());
			customer.setLastName(contactsObj.getLastName());
			customer.setPhone(contactsObj.getMobilePhone());
			customer.setEmailAddress(contactsObj.getEmailId());
			customer.setAddressLine1(contactsObj.getAddressOne());
			customer.setAddressLine2(contactsObj.getAddressTwo());
			customer.setCity(contactsObj.getCity());
			customer.setState(contactsObj.getState());
			//customer.setPostal(contactsObj.getPin());
			customer.setCountry(contactsObj.getCountry());
			
			/*Calendar bdCal = contactsObj.getBirthDay();
			
			if(bdCal!=null)
			{
				//Calendar bdCal = MyCalendar.calendarToString(birthday,MyCalendar.FORMAT_DATETIME_STYEAR);
				String bDStr = MyCalendar.calendarToString(contactsObj.getBirthDay(),MyCalendar.FORMAT_DATETIME_STYEAR);
				customer.setBirthday(bDStr);
			}
			
			Calendar annCal = contactsObj.getBirthDay();
			
			if(annCal!=null)
			{
				//Calendar bdCal = MyCalendar.calendarToString(birthday,MyCalendar.FORMAT_DATETIME_STYEAR);
				String annStr = MyCalendar.calendarToString(contactsObj.getBirthDay(),MyCalendar.FORMAT_DATETIME_STYEAR);
				customer.setAnniversary(annStr);
			}
			customer.setGender(contactsObj.getGender());
			
			Calendar createdDateCal = contactsObj.getBirthDay();
			if(createdDateCal!=null)
			{
				//Calendar bdCal = MyCalendar.calendarToString(birthday,MyCalendar.FORMAT_DATETIME_STYEAR);
				String createdDateStr = MyCalendar.calendarToString(contactsObj.getCreatedDate(),MyCalendar.FORMAT_DATETIME_STYEAR);
				customer.setCreatedDate(createdDateStr);
			}*/
			otpRequest.setCustomer(customer);
			
			String transactionDate = MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STYEAR);
			String transactionId = (System.currentTimeMillis())+Constants.STRING_NILL;
			
			
			LoyaltyOTPResponse loyaltyOTPResponseObj = null;
			try {
				LoyaltyOTPOCService otpService= (LoyaltyOTPOCService)ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_OTP_OC_BUSINESS_SERVICE);
				loyaltyOTPResponseObj = otpService.processOTPRequest(otpRequest, OCConstants.LOYALTY_ONLINE_MODE, transactionId, transactionDate);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.info("Exception OTPOCrequest ::"+e);
				logger.error("Exception ::", e);
				return loyaltyOTPResponseObj;
			}
			
			return loyaltyOTPResponseObj;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("Exception :: "+e);
			logger.error("Exception ::", e);
			return null;
		}
	}
	
		
	public LoyaltyRedemptionResponse prepareLoyaltyRedemptionRequest() {

		try {
			String redeemAmount = redeemedAmountTbId.getValue()+Constants.STRING_NILL;
			String receiptAmount = receiptAmountTbId.getValue()+Constants.STRING_NILL;
			String receiptNumber = receiptNumTbId.getValue()+Constants.STRING_NILL;
			String storeNumber =  storeNumberTbId.getValue();
			 String OtpCode =  otpTbId.getValue()+Constants.STRING_NILL;
			OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
			UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			
					
			LoyaltyRedemptionRequest loyaltyRedemptionRequest = new LoyaltyRedemptionRequest();
			LoyaltyRedemptionResponse redemptionResponse = null;
			
			RequestHeader requestHeader = new RequestHeader();

			String requestIdcurrentTime = (System.currentTimeMillis())+Constants.STRING_NILL;
			
			Calendar cal =  Calendar.getInstance();
			String requestedDate = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STYEAR,null);
			
			requestHeader.setRequestId(requestIdcurrentTime);
			requestHeader.setRequestType("");// Issuance *****
			requestHeader.setRequestDate(requestedDate);
			//requestHeader.setPcFlag(pcFlag); //??????? It is used only in sparkBased
			
			Long domainId = userDao.findDomainByUserId(currUser.getUserId());
			if(domainId!=null){
				OrganizationStores orgStores = organizationStoresDao.findOrgByDomain(currUser.getUserOrganization().getUserOrgId(), domainId, storeNumber);
				requestHeader.setSubsidiaryNumber(orgStores!=null ? orgStores.getSubsidiaryId() : "");
			}
			requestHeader.setStoreNumber(storeNumber);
			//requestHeader.setEmployeeId(employeeId);????????
			//requestHeader.setTerminalId(terminalId);????????
			
			//String docSIDcurrentTime = (System.currentTimeMillis())+Constants.STRING_NILL;
			requestHeader.setDocSID(storeNumber+"-"+MyCalendar.calendarToString(MyCalendar.getInstance(clientTimeZone), MyCalendar.FORMAT_SB_DATEONLY)+"-"+receiptNumber);
			requestHeader.setReceiptNumber(receiptNumber);
			
			LoyaltyUser user = new LoyaltyUser();
			String token = currUser.getToken() == null || currUser.getToken().isEmpty() ? currUser.getUserOrganization().getOptSyncKey() : currUser.getToken();
			user.setUserName(Utility.getOnlyUserName(currUser.getUserName()));
			user.setOrganizationId(Utility.getOnlyOrgId(currUser.getUserName()));
			user.setToken(token);
			
			Customer customer = new Customer();
			Contacts contactsObj = contactsLoyaltyObj.getContact();
			
			customer.setCustomerId(contactsObj.getExternalId());
			customer.setInstanceId(contactsObj.getInstanceId());
			customer.setFirstName(contactsObj.getFirstName());
			customer.setLastName(contactsObj.getLastName());
			customer.setPhone(contactsObj.getMobilePhone());
			customer.setEmailAddress(contactsObj.getEmailId());
			customer.setAddressLine1(contactsObj.getAddressOne());
			customer.setAddressLine2(contactsObj.getAddressTwo());
			customer.setCity(contactsObj.getCity());
			customer.setState(contactsObj.getState());
			//customer.setPostal(contactsObj.getPin());
			customer.setCountry(contactsObj.getCountry());
			
			/*Calendar bdCal = contactsObj.getBirthDay();
			
			if(bdCal!=null)
			{
				//Calendar bdCal = MyCalendar.calendarToString(birthday,MyCalendar.FORMAT_DATETIME_STYEAR);
				String bDStr = MyCalendar.calendarToString(contactsObj.getBirthDay(),MyCalendar.FORMAT_DATETIME_STYEAR);
				customer.setBirthday(bDStr);
			}
			
			Calendar annCal = contactsObj.getBirthDay();
			if(annCal!=null)
			{
				//Calendar bdCal = MyCalendar.calendarToString(birthday,MyCalendar.FORMAT_DATETIME_STYEAR);
				String annStr = MyCalendar.calendarToString(contactsObj.getBirthDay(),MyCalendar.FORMAT_DATETIME_STYEAR);
				customer.setAnniversary(annStr);
			}
			customer.setGender(contactsObj.getGender());
			Calendar createdDateCal = contactsObj.getBirthDay();
			if(createdDateCal!=null)
			{
				//Calendar bdCal = MyCalendar.calendarToString(birthday,MyCalendar.FORMAT_DATETIME_STYEAR);
				String createdDateStr = MyCalendar.calendarToString(contactsObj.getCreatedDate(),MyCalendar.FORMAT_DATETIME_STYEAR);
				customer.setCreatedDate(createdDateStr);
			}*/
			
			MembershipRequest memberShipRequestObj = new MembershipRequest();

			String membershipNumber = contactsLoyaltyObj.getCardNumber();
			String cardPin = contactsLoyaltyObj.getCardPin();
			Boolean fpFlag = contactsLoyaltyObj.getFpRecognitionFlag();
			String fpString = "false";		
			String mobileBased = "";
			
			if(contactsLoyaltyObj.getMembershipType() != null && contactsLoyaltyObj.getMembershipType().equals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
			//if(contactsLoyaltyObj.getMobilePhone() != null && contactsLoyaltyObj.getMobilePhone().equals(membershipNumber)){
				memberShipRequestObj.setPhoneNumber(contactsLoyaltyObj.getCardNumber());
			}else{
				memberShipRequestObj.setCardNumber(contactsLoyaltyObj.getCardNumber());
				
			}
			memberShipRequestObj.setIssueCardFlag("");
			
			/*memberShipRequestObj.setCardPin(cardPin!=null && !cardPin.isEmpty() ? cardPin :null);

			String memberShipcreatedDateStr = MyCalendar.calendarToString(contactsLoyaltyObj.getCreatedDate(),MyCalendar.FORMAT_DATETIME_STYEAR);
			memberShipRequestObj.setCreatedDate(memberShipcreatedDateStr);*/
			
			/*if(fpFlag!=null && fpFlag)
					fpString = "True";
			memberShipRequestObj.setFingerprintValidation(fpString);*/
			
			/*if(memberShipRequestObj.getCardNumber() == null || memberShipRequestObj.getCardNumber().trim().length() == 0)	
				memberShipRequestObj.setIssueCardFlag(OCConstants.LOYALTY_CARD_GENERATION_FLAG_Y);
			else
				memberShipRequestObj.setIssueCardFlag(OCConstants.LOYALTY_CARD_GENERATION_FLAG_N);
			
			memberShipRequestObj.setPhoneNumber(contactsLoyaltyObj.getMobilePhone());
			*/
			
			
			Amount amountObj = new Amount();
			
			amountObj.setEnteredValue(redeemAmount);//?????????????????????????????????????????
			amountObj.setReceiptAmount(receiptAmount);
			amountObj.setType(OCConstants.LOYALTY_TYPE_REWARD); //Reward or gift
			amountObj.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);// Currency 
			
			// Get details from Madam(Proumya)
			Discounts discounts=new Discounts();
			List<Promotion> promotionList = new ArrayList<Promotion>();
			Promotion promotion = new Promotion();
			promotion.setName("");
			promotionList.add(promotion);
			discounts.setAppliedPromotion("NA");
			discounts.setPromotions(promotionList);
			
			
			loyaltyRedemptionRequest.setHeader(requestHeader);
			loyaltyRedemptionRequest.setMembership(memberShipRequestObj);
			loyaltyRedemptionRequest.setDiscounts(discounts);
			loyaltyRedemptionRequest.setUser(user);
			loyaltyRedemptionRequest.setOtpCode(OtpCode);
			loyaltyRedemptionRequest.setAmount(amountObj);
			loyaltyRedemptionRequest.setCustomer(customer);
			
			String transactionDate = MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STYEAR);
			String transactionId = (System.currentTimeMillis())+Constants.STRING_NILL;
			
			LoyaltyTransactionParent tranParent = createNewTransaction(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION); 
			Date date = tranParent.getCreatedDate().getTime();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
			String transDate = df.format(date);
			
			
			
			Status status = null;
			String responseJson = "";
			String requestJson="";
			Gson gson = new Gson();
			String userName = null;
			
			
			try{
				ResponseHeader responseHeader = new ResponseHeader();
				responseHeader.setRequestDate("");
				responseHeader.setRequestId("");
				responseHeader.setTransactionDate(transactionDate);
				responseHeader.setTransactionId(""+tranParent.getTransactionId());
				
				try{
					requestJson = gson.toJson(loyaltyRedemptionRequest, LoyaltyRedemptionRequest.class);
				}catch(Exception e){
					status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
					responseJson = gson.toJson(redemptionResponse);
					updateRedemptionTransaction(tranParent, redemptionResponse, null);
					logger.info("Response = "+responseJson);
					logger.error("Exception in parsing request json ...",e);
					return null;
				}
				
				if(loyaltyRedemptionRequest.getHeader() == null){
					status = new Status("1004", PropertyUtil.getErrorMessage(1004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
					responseJson = gson.toJson(redemptionResponse);
					updateRedemptionTransaction(tranParent, redemptionResponse, null);
					logger.info("Response = "+responseJson);
					return null;
				}
				if(loyaltyRedemptionRequest.getHeader().getRequestId() == null || loyaltyRedemptionRequest.getHeader().getRequestId().trim().isEmpty() ||
						loyaltyRedemptionRequest.getHeader().getRequestDate() == null || loyaltyRedemptionRequest.getHeader().getRequestDate().trim().isEmpty()){
					status = new Status("111553", PropertyUtil.getErrorMessage(111553, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
					responseJson = gson.toJson(redemptionResponse);
					updateRedemptionTransaction(tranParent, redemptionResponse, null);
					logger.info("Response = "+responseJson);
					return null;
				}
				
				if(loyaltyRedemptionRequest.getMembership() == null){
					responseHeader.setRequestId(loyaltyRedemptionRequest.getHeader().getRequestId());
					responseHeader.setRequestDate(loyaltyRedemptionRequest.getHeader().getRequestDate());
					status = new Status("101004", PropertyUtil.getErrorMessage(101004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
					responseJson = gson.toJson(redemptionResponse);
					updateRedemptionTransaction(tranParent, redemptionResponse, null);
					logger.info("Response = "+responseJson);
					return null;
				}
				if(loyaltyRedemptionRequest.getUser() == null){
					responseHeader.setRequestId(loyaltyRedemptionRequest.getHeader().getRequestId());
					responseHeader.setRequestDate(loyaltyRedemptionRequest.getHeader().getRequestDate());
					status = new Status("101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
					responseJson = gson.toJson(redemptionResponse);
					updateRedemptionTransaction(tranParent, redemptionResponse, null);
					logger.info("Response = "+responseJson);
					return null;
				}
				if(loyaltyRedemptionRequest.getUser().getUserName() == null || loyaltyRedemptionRequest.getUser().getUserName().trim().length() <=0 || 
						loyaltyRedemptionRequest.getUser().getOrganizationId() == null || loyaltyRedemptionRequest.getUser().getOrganizationId().trim().length() <=0 || 
								loyaltyRedemptionRequest.getUser().getToken() == null || loyaltyRedemptionRequest.getUser().getToken().trim().length() <=0) {
					responseHeader.setRequestId(loyaltyRedemptionRequest.getHeader().getRequestId());
					responseHeader.setRequestDate(loyaltyRedemptionRequest.getHeader().getRequestDate());
					status = new Status("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
					responseJson = gson.toJson(redemptionResponse);//APP-1206
					logger.info("Response = "+responseJson);
					updateRedemptionTransaction(tranParent, redemptionResponse, null);
					return null;
				}
				userName = loyaltyRedemptionRequest.getUser().getUserName() + Constants.USER_AND_ORG_SEPARATOR +
						          loyaltyRedemptionRequest.getUser().getOrganizationId();
				
				LoyaltyTransaction trans = findRequestByReqIdAndDocSid(loyaltyRedemptionRequest.getUser().getUserName() + "__" +
						loyaltyRedemptionRequest.getUser().getOrganizationId(), loyaltyRedemptionRequest.getHeader().getRequestId().trim(), 
						loyaltyRedemptionRequest.getHeader().getDocSID().trim());
				String reqJson = gson.toJson(loyaltyRedemptionRequest, LoyaltyRedemptionRequest.class);
				if(trans != null){
					responseHeader.setRequestId(loyaltyRedemptionRequest.getHeader().getRequestId());
					responseHeader.setRequestDate(loyaltyRedemptionRequest.getHeader().getRequestDate());
					status = new Status("111536", PropertyUtil.getErrorMessage(111536, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
					responseJson = gson.toJson(redemptionResponse);
					logger.info("Response = "+responseJson);
					updateRedemptionTransaction(tranParent, redemptionResponse, null);
					return null;
				}
				if(trans==null){
					trans = logRedemptionTransactionRequest(loyaltyRedemptionRequest, reqJson,"online");
				}
			
			try {
				LoyaltyRedemptionOCService loyaltyRedemptionOCServiceObj = (LoyaltyRedemptionOCService)ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_REDEMPTION_OC_BUSINESS_SERVICE);
				
				redemptionResponse = loyaltyRedemptionOCServiceObj.processRedemptionRequest(loyaltyRedemptionRequest, OCConstants.LOYALTY_ONLINE_MODE, transactionId, transactionDate,null);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.info("Exception OTPOCrequest ::"+e);
				logger.error("Exception ::", e);
				return null;
			}
			
			responseJson = gson.toJson(redemptionResponse);
			logger.info("Redemption response : "+responseJson);
			updateTransactionStatus(trans, responseJson, redemptionResponse);
			}catch(Exception e){
				logger.error("Error in redemption restservice", e);
				ResponseHeader responseHeader = null;
				if(redemptionResponse == null){
					responseHeader = new ResponseHeader();
					responseHeader.setRequestDate("");
					responseHeader.setRequestId("");
					responseHeader.setTransactionDate(transDate);
					responseHeader.setTransactionId(""+tranParent.getTransactionId());
				}
				else{
					responseHeader = redemptionResponse.getHeader();
				}
				status = new Status("101000", PropertyUtil.getErrorMessage(101000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				try {
					redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					logger.error("Exception ",e);
				}
				responseJson = gson.toJson(redemptionResponse);
				updateRedemptionTransaction(tranParent, redemptionResponse, userName);
				logger.info("Response = "+responseJson);
				return null;
			}
			return redemptionResponse;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("Exception ::"+e);
			logger.error("Exception ::", e);
			return null;
		}
	}
	
	private void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyRedemptionResponse response){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			transaction.setRequestStatus(response.getStatus().getStatus());
			//transaction.setJsonResponse(responseJson);
			if(response.getMembership() != null && response.getMembership().getCardNumber() != null 
					&& !response.getMembership().getCardNumber().trim().isEmpty()){
				transaction.setCardNumber(response.getMembership().getCardNumber());
			}
			else{
				transaction.setCardNumber(response.getMembership() == null ? "" : response.getMembership().getPhoneNumber());
			}
			if(Utility.countryCurrencyMap.get(currUser.getCountryType()) != null) {
				 
				 responseJson = responseJson.replace(Utility.countryCurrencyMap.get(currUser.getCountryType()), "");
			 }
			transaction.setJsonResponse(responseJson);
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}
	private LoyaltyTransaction findRequestByReqIdAndDocSid(String userName, String requestId, String docSid) throws Exception {
		LoyaltyTransactionDao loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
		return loyaltyTransactionDao.findRequestByReqIdAndDocSid(userName, requestId, docSid, OCConstants.LOYALTY_TRANSACTION_REDEMPTION, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
	}
	private LoyaltyTransaction logRedemptionTransactionRequest(LoyaltyRedemptionRequest requestObject, String jsonRequest, String mode){
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
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_REDEMPTION);
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
	private void updateRedemptionTransaction(LoyaltyTransactionParent trans, LoyaltyRedemptionResponse responseObject, String userName) {
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
			logger.error("Exception while updating transaction...", e);
		}
	}
	private LoyaltyRedemptionResponse prepareRedemptionResponse(ResponseHeader header, MembershipResponse membershipResponse,
			List<Balance> balances, HoldBalance holdBalance, AdditionalInfo additionalInfo, List<MatchedCustomer> matchedCustomers, Status status) throws BaseServiceException {
		LoyaltyRedemptionResponse redemptionResponse = new LoyaltyRedemptionResponse();
		redemptionResponse.setHeader(header);
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
		if(additionalInfo == null){
			additionalInfo = new AdditionalInfo();
			additionalInfo.setOtpEnabled("");
			//OTPRedeemLimit otpRedeemLimit = new OTPRedeemLimit();
			/*otpRedeemLimit.setAmount("");
			otpRedeemLimit.setValueCode("");*/
			List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
			//otpRedeemLimitlist.add(otpRedeemLimit);
			additionalInfo.setOtpRedeemLimit(otpRedeemLimitlist);
			additionalInfo.setPointsEquivalentCurrency("");
			additionalInfo.setTotalRedeemableCurrency("");
		}
		if(matchedCustomers == null){
			matchedCustomers = new ArrayList<MatchedCustomer>();
		}
		
		redemptionResponse.setMembership(membershipResponse);
		redemptionResponse.setBalances(balances);
		redemptionResponse.setHoldBalance(holdBalance);
		redemptionResponse.setAdditionalInfo(additionalInfo);
		redemptionResponse.setMatchedCustomers(matchedCustomers);
		redemptionResponse.setStatus(status);
		return redemptionResponse;
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
	
	
	public Double getTotalRedeemableAmount() throws Exception{
		double loyaltyAmount = contactsLoyaltyObj.getGiftcardBalance() == null ? 0.00 : contactsLoyaltyObj.getGiftcardBalance();
		double giftAmount = contactsLoyaltyObj.getGiftBalance() == null ? 0.00 : contactsLoyaltyObj.getGiftBalance();
		double pointsAmount = 0.00;
		double totalReedmCurr = 0.00;
		LoyaltyProgramTier tier = (LoyaltyProgramTier)tierNameMSummaryLblId.getAttribute("tier");
		if(tier == null){
			logger.debug("No Tier info for this membership=="+tierNameMSummaryLblId.getValue());
			return 0.00;
		}
		if(OCConstants.LOYALTY_CONVERSION_TYPE_ONDEMAND.equalsIgnoreCase(tier.getConversionType().trim())) {
			pointsAmount = calculatePointsAmount(contactsLoyaltyObj, tier);
		}
		logger.debug("loyaltyAmount =="+loyaltyAmount+" pointsAmount== "+pointsAmount+" giftAmount=="+giftAmount);
		totalReedmCurr = loyaltyAmount + pointsAmount + giftAmount;
		logger.debug("totalReedmCurr =="+totalReedmCurr);
		return totalReedmCurr;
		
	}
	private double calculatePointsAmount(ContactsLoyalty contactsLoyalty, LoyaltyProgramTier tier) throws Exception {
		
		if(tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0 
				&& contactsLoyalty.getLoyaltyBalance() != null && contactsLoyalty.getLoyaltyBalance() > 0){
		
			double factor = contactsLoyalty.getLoyaltyBalance()/tier.getConvertFromPoints();
			int intFactor = (int)factor;
			return tier.getConvertToAmount() * intFactor;
			
		}
		else return 0.0;
	}
	
	public void onClick$resendotp() {
		onClick$verifyBtnId();
	}
	
	private void drawRowGrid(List<Map<String, Object>> contactLoyalties , String searchKey) {
		logger.debug(">>>>>>>>>>>>> entered in drawRowGrid");
		Components.removeAllChildren(latestDetailsSubWinId$multipleFoundRowId);
		if(contactLoyalties == null) {
			//TODO
			logger.debug(" No Promo-codes exists ");
			return;
		}
		
		for(Map<String, Object> contactLoyaltyMap: contactLoyalties) {
		
		Row row = new Row();

		Label label = null;
		Contacts contacts = null;
		LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
		LoyaltyTransactionChild loyaltyTransactionChild = null; //APP-3219

		if(contactLoyaltyMap.containsKey("contact_id")){
			Long contact_id = (Long) contactLoyaltyMap.get("contact_id");
			if(contact_id != null){
				contacts = ltyPrgmService.getContactObj(contact_id);
			}
			else{
				logger.error("Contact Details are not found for card with Membership Number : "+contactLoyaltyMap.get("card_number"));
			}
		}
	
		
		if(Constants.FIRST_NAME.equalsIgnoreCase(searchKey) ||
				Constants.LAST_NAME.equalsIgnoreCase(searchKey) || 
				Constants.FULL_NAME.equalsIgnoreCase(searchKey) ||
				Constants.MOBILE_PHONE.equalsIgnoreCase(searchKey)|| 
				Constants.EMAIL_ID.equalsIgnoreCase(searchKey)){
			
			
			Label tempLabel= setMembershipNumber(contactLoyaltyMap,row);
			tempLabel.setStyle("cursor:pointer;color:blue;text-decoration: underline;margin-top:10px;");
			tempLabel.addEventListener("onClick", this);
			row.appendChild(tempLabel);
		
			label = new Label(contacts.getMobilePhone() == null ? "--":contacts.getMobilePhone());
			label.setStyle("margin-top:10px;");
			row.appendChild(label);
		
			label = new Label(contacts.getEmailId() == null ? "--":contacts.getEmailId());
			label.setStyle("margin-top:10px;");
			row.appendChild(label);
		
			label = new Label(contactLoyaltyMap.get("customer_id")==null?"--":contactLoyaltyMap.get("customer_id").toString().trim());
			label.setStyle("margin-top:10px;");
			row.appendChild(label);

			String loyaltyId = contactLoyaltyMap.get("loyalty_id")==null?"--":contactLoyaltyMap.get("loyalty_id").toString().trim();
			
			loyaltyTransactionChild = fetchLatestTransactionByLoyaltyId(loyaltyId);
						
			label = new Label(contactLoyaltyMap.get("pos_location_id")==null?"--":contactLoyaltyMap.get("pos_location_id").toString().trim());
			label.setStyle("margin-top:10px;");
			row.appendChild(label);
			
			label = new Label((loyaltyTransactionChild ==null || loyaltyTransactionChild.getCreatedDate() == null) ? "--" : (MyCalendar.calendarToString(loyaltyTransactionChild.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR,clientTimeZone)));
			label.setStyle("margin-top:10px;");
			row.appendChild(label);
			
			label = new Label(contacts.getAddressOne() == null ? "--":contacts.getAddressOne());
			label.setStyle("margin-top:10px;");
			row.appendChild(label);
			
			label = new Label(contactLoyaltyMap.get("tier_name")==null?"--":contactLoyaltyMap.get("tier_name").toString().trim());
			label.setStyle("margin-top:10px;");
			row.appendChild(label);

			//label.setParent(hlayout);
		}
		
		row.setParent(latestDetailsSubWinId$multipleFoundRowId);
		// outerHlayout.setParent(latestDetailsSubWinId$multipleFoundRowId);
		}
		
	
		logger.debug("<<<<<<<<<<<<< completed drawRow ");
		
	}//drawRowGrid
	
	private Label setMembershipNumber(Map<String, Object> contactLoyaltyMap,  Row row) {
		logger.debug(">>>>>>>>>>>>> entered in setMembershipNumber");
		Label label;
		String tooltip = getToolTip(contactLoyaltyMap);
		//label.setParent(row);
		label = new Label(contactLoyaltyMap.get("card_number")==null?Constants.STRING_NILL:contactLoyaltyMap.get("card_number").toString().trim());
		label.setTooltiptext(tooltip);
		logger.debug("<<<<<<<<<<<<< completed setMembershipNumber ");
		return label;
	}
	
	private LoyaltyTransactionChild fetchLatestTransactionByLoyaltyId(String loyaltyId) {
		try {
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			return loyaltyTransactionChildDao.getLatestTransactionByLoyaltyId(currUser.getUserId(), loyaltyId);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

		
	}
	public void onClick$deleteEmailId() {
		String emailId=emailTxtBxId.getValue();
		int rowsUpdated=0;
		int contactUpdated=0;
		boolean searchEmail=false;
		if(emailId!=null&&!emailId.isEmpty()) {
			SuppressedContactsDaoForDML suppressedContactsDaoForDML;
			suppressedContactsDaoForDML = (SuppressedContactsDaoForDML)SpringUtil.getBean("suppressedContactsDaoForDML");
			ContactsDaoForDML contactsDaoForDML;
			contactsDaoForDML = (ContactsDaoForDML)SpringUtil.getBean("contactsDaoForDML");
			SuppressedContactsDao suppressedContactsDao=null;
			UnsubscribesDaoForDML unsubscribesDaoForDML=null;
			
			try {
				unsubscribesDaoForDML = (UnsubscribesDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.UNSUBSCRIBE_DAO_FOR_DML);
				suppressedContactsDao = (SuppressedContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_SUPPRESSED_CONTACTS_DAO);
				
				//searchEmail=suppressedContactsDao.findByUserId(user.getUserId(),emailId);
				rowsUpdated = suppressedContactsDaoForDML.deleteByEmailId(currUser.getUserId(),emailId);
				if(! (rowsUpdated>0))rowsUpdated=unsubscribesDaoForDML.deleteByEmailId(currUser.getUserId(),emailId);
				contactsDaoForDML.updateEmailStatusByAdmin("'"+emailId+"'",currUser.getUserId(),Constants.CONT_STATUS_ACTIVE);//Constants.CONT_STATUS_SUPPRESSED);
				
				//contactsDaoForDML.updateEmailStatusByStatus("'"+emailId+"'",user.getUserId(),Constants.CONT_STATUS_ACTIVE,Constants.CONT_STATUS_BOUNCED);
				//For Status unsubscribe
				//contactsDaoForDML.updateEmailStatusByStatus("'"+emailId+"'",user.getUserId(),Constants.CONT_STATUS_ACTIVE,Constants.CONT_STATUS_UNSUBSCRIBED);
				//	contactsDaoForDML.updateEmailStatusByStatus("'"+emailId+"'",user.getUserId(),Constants.CONT_STATUS_ACTIVE,Constants.CONT_STATUS_SUPPRESSED);
				logger.info("Deleted from Suppression and updated");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e);
				logger.info("Deleted from Suppression");
			}
			if(rowsUpdated>0) {
				MessageUtil.setMessage("EmaiId deleted from SuppressesList: "+rowsUpdated, "color:blue", "TOP");

			}else {
			logger.info("String"+rowsUpdated);	
			MessageUtil.setMessage("EmailId doesn't exist", "color:red", "TOP");
			}

		}else {
		MessageUtil.setMessage("Please enter EmailId", "color:red", "TOP");
		}
		emailTxtBxId.setValue("");
	}
	
	
	
	
	
	public void onClick$deletephnId() {
		String phoneId=phnTxtBxId.getValue();
		int rowsUpdated=0;
		if(phoneId!=null&&!phoneId.isEmpty()) {
			
			
			UserOrganization organization = currUser!=null ? currUser.getUserOrganization(): null;
			String phone = Utility.phoneParse(phnTxtBxId.getValue(),organization);
			if(phone == null || phone.trim().length() == 0) {
				MessageUtil.setMessage("Please provide valid data for the fields in red.", "color:red", "TOP");
				return;
			}
			
			ServiceLocator locator = ServiceLocator.getInstance();
			SMSSuppressedContactsDaoForDML smsSuppressedContactsDaoForDML=null;
			SMSSuppressedContactsDao smsSuppressedContactsDao = null;
			try {
				smsSuppressedContactsDao = (SMSSuppressedContactsDao)locator.getDAOByName(OCConstants.SMS_SUPPRESSEDCONTACT_DAO);
    			smsSuppressedContactsDaoForDML = (SMSSuppressedContactsDaoForDML)locator.getDAOForDMLByName(OCConstants.SMS_SUPPRESSEDCONTACT_DAO_FOR_DML);

			
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			ContactsDaoForDML contactsDaoForDML;
			contactsDaoForDML = (ContactsDaoForDML)SpringUtil.getBean("contactsDaoForDML");
			
			try {
				
				rowsUpdated = smsSuppressedContactsDaoForDML.deleteFromSuppressedContacts(currUser,phoneId);
				contactsDaoForDML.updatemobileStatus(phoneId,Constants.CONT_STATUS_ACTIVE, currUser);
				
				logger.info("Deleted from Suppression and updated");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e);
				logger.info("Deleted from Suppression");
			}
			if(rowsUpdated>0) {
				MessageUtil.setMessage("Phone Number deleted from SuppressesList: "+rowsUpdated, "color:blue", "TOP");

			}else {
			logger.info("String"+rowsUpdated);	
			MessageUtil.setMessage("PhoneNumber doesn't exist", "color:red", "TOP");
			}

		}else {
		MessageUtil.setMessage("Please enter PhoneNumber", "color:red", "TOP");
		}
		phnTxtBxId.setValue("");
	}


}
