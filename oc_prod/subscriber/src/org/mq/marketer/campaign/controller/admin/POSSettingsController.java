package org.mq.marketer.campaign.controller.admin;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.hibernate.event.SaveOrUpdateEvent;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.DigitalReceiptsJSON;
//import org.mq.marketer.campaign.beans.DigitalReceiptUserSettings;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.MastersToTransactionMappings;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.UpdateOptSyncData;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.UserPosFTPSettings;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.DigitalReceiptUserSettingsDao;
import org.mq.marketer.campaign.dao.DigitalReceiptsJSONDao;
import org.mq.marketer.campaign.dao.DigitalReceiptsJSONDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MailingListDaoForDML;
import org.mq.marketer.campaign.dao.MastersToTransactionMappingsDao;
import org.mq.marketer.campaign.dao.MastersToTransactionMappingsDaoForDML;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.POSMappingDaoForDML;
import org.mq.marketer.campaign.dao.UpdateOptSyncDataDao;
import org.mq.marketer.campaign.dao.UserPosFTPSettingsDao;
import org.mq.marketer.campaign.dao.UserPosFTPSettingsDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.POSFieldsEnum;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ItemRenderer;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

@SuppressWarnings("rawtypes")
public class POSSettingsController extends GenericForwardComposer implements EventListener {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private UsersDao usersDao=  null;
	private UsersDaoForDML usersDaoForDML=  null;
	MailingListDao mailingListDao = null;
	MailingListDaoForDML mailingListDaoForDML = null;
	POSMappingDao posMappingDao = null;
	POSMappingDaoForDML posMappingDaoForDML = null;
	UserPosFTPSettingsDao userPOSFTPSettingsDao = null;
	UserPosFTPSettingsDaoForDML userPOSFTPSettingsDaoForDML = null;
	//private DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao;
	DigitalReceiptsJSONDao digitalReceiptsJSONDao =null;
	DigitalReceiptsJSONDaoForDML digitalReceiptsJSONDaoForDML = null;
	
	Div posMappingDivId,alertTitleDivId,alertMailDivId;
	Groupbox createListGroupBoxId, posFTPSettingsGroupBoxId,mastersPosFTPSettingsGroupBoxId;
	
	private Listbox orgListBxId,usersListBxId,orgListBx,redeemTenderListBxId,ninItemFieldListBxId,redeemAsListBxId;
	private Users users;
	private MailingList posML;
	private Textbox posListTxtBoxId,posListDescTbId,optSynAuthKeyTbId,alertMailTxtbxId,redemptionTenderTbId,redemptionTenderDispLblTbId,ninItemFieldTbId,SMSMsgTbId;
	private Checkbox doubleOptInCbId,parentalConsentCbId, loyaltyOptInCbId,alertChekbxId;
	private Rows contactRowsId,salesRowsId,skuRowsId,ftpSettingsRowsId;
	private CustomTemplatesDao customeTemplatesDao ;
	private Div doubleOptLtDiv,parentalOptLtDiv, loyaltyOptLtDiv,posMapContDivId,posMapSalesDivId,posMapSKUDivId;
	private Listbox optInEmailsLbId,parentalConsentEmailLbId, LoyaltyOptinEmailLbId;
	private Toolbarbutton posContTbBtnId, posSalesTbBtnId, posSKUTbBtnId,addMoreEmailTBId;
	private Set<Long> listIdsSet; 
	private Listbox TimesZonesLbId,alertConfigLstbxId,itemNoteListBxId,itemInfoFieldListBxId,receiptNoteListBxId,cardInfoListBxId;
	private Checkbox dataExtractionChkBxId,SendAllDiscountChkBxId,ismultiuserCkbId,zoneWiseChkBxId,extractLoyaltyChkBxId,redemptionReversalChkBxId,redeemPromoChkBxId,
	enrolChkBxId,issuanceChkBxId,returnChkBxId,redemptionChkBxId,IgnChkBxId,validateItemsInReturnTrx,ignorePointsRedemption,isUserSpecDirCkbId,sendRealtimeLoyaltyStatus,eReceiptOverSMS,
	AllowBothDiscounts,RedeemAsDiscount,ExcludeDiscountedItem,HighDiscountedItem,ignoreissuanceOnRedemChkBxId;
	private UpdateOptSyncDataDao updateOptSyncDataDao;
	private Button nxtBtnId;
	private Button saveContinueBtnId;
	private Button saveBtnId;
	private Button creatListNxtBtnId;
	private Button step1NxtBtnId;
	private Button step1SaveBtnId;
	private Label optionsbasedOnloyalityType;
	private Div optionsbasedOnloyalityTypeCheckbox,enrolLbl,issuanceLbl,returnLbl,redemptionLbl,IgnLbl,redemptionTenderLbl,redemptionReversalLbl,
	receiptNoteLbl,itemNoteLbl,itemInfoLbl,cardInfoLbl,redemptionTenderDisplayLblId;
	private A editMsgBtnId, welcomeMessagePreviewBtnId,previewBtnId,parentalPreviewBtnId,loyaltyPreviewBtnId;
	private Image previewIconId,optInPreviewIconId, parentalPreviewIconId, loyaltyPreviewIconId;
	private MyTemplatesDao myTemplatesDao;
	
	List<String> scopeCustfieldList = new ArrayList<String>();
	//List<String> scopeDrfieldList = new ArrayList<String>();
	List<String> scopePosAttrList = new ArrayList<String>();
	List<String> scopeDispLblList = new ArrayList<String>();
	private static String CREATED_DATE = "Created Date";
	private static String ANNIVERSARY = "Anniversary";
	private static String BIRTHDAY = "BirthDay";
	private static String ZIP = "ZIP";
	private static String MOBILE = "Mobile";
	private static String SALE_DATE ="Sale Date";
	private static String LIST_PRICE = "List Price";
	private static String SALE_PRICE ="Sale Price";
	private static String TAX = "Tax";
	private static String DISCOUNT = "Discount";
	private static String QUANTITY = "Quantity";
	private static String RECEIPT_NUM = "Receipt Number";
	
	private static String selectStr = "--select--";
	private static String[] udfSetStr ={"UDF1" ,"UDF2" ,"UDF3","UDF4", "UDF5","UDF6","UDF7","UDF8",
										"UDF9","UDF10",	"UDF11","UDF12","UDF13","UDF14","UDF15"};
	private static String[] itemInfoStr= {"ALU","Attr","DCS","DCSName","Desc1","Desc2","Desc3","Desc4","ItemLookup","ItemSID","Size","UPC","VendorCode"};
	private static String[] itemDiscStr= {"ItemNote1","ItemNote2","ItemNote3","ItemNote4","ItemNote5","ItemNote6","ItemNote7","ItemNote8","ItemNote9","ItemNote10"};
	private static String[] receiptDiscStr= {"ECOMOrderNo","InvcHdrNotes","InvcComment1","InvcComment2"};
	private static String[] cardInfoStr = {"BillToInfo1","BillToInfo2"};
	private static String[] nonInventoryItemField = {"ALU"};
	
	private static String contctGenFieldStr = PropertyUtil.getPropertyValue("defaultPOSContactMapFieldList");
	private static String salesGenFieldStr = PropertyUtil.getPropertyValue("defaultPOSSalesFieldLst");
	private static String skuGenfieldStr = PropertyUtil.getPropertyValue("defaultSKUFieldList");
	//private static String digitalReceiptDefaultStr = PropertyUtil.getPropertyValue("defaultDigitalReceiptFieldList");
	private static String DOC_SID="Doc Sid";
	
	
	private static Map<String, String> genFieldContMap = new HashMap<String, String>();
	/*defaultSalesFieldLst = CustomerID,ReceiptNumber,SaleDate,Qty,SalePrice,Tax,PromoCode,StoreNumber,SKU,TenderType
			defaultSKUFieldList = StoreNumber,SKU,Description,ListPrice,ItemCategory*/
	private static Map<String, String> genFieldSalesMap = new HashMap<String, String>();
	
	private static Map<String, String> genFieldSKUMap = new HashMap<String, String>();
	
	//private static Map<String, String> digiReceiptContMap = new HashMap<String, String>();
	//private static Map<String, String> digiReceiptSalesMap = new HashMap<String, String>();
	//private static Map<String, String> digiReceiptSKUMap = new HashMap<String, String>();
	
	private final String selectMsgStr = "Select Message";
	private final String defaultStr = "Default Message";
	
	static{
		genFieldContMap.put("Email" , "Email");
		genFieldContMap.put("FirstName" , "First Name");
		genFieldContMap.put("LastName" , "Last Name");
		genFieldContMap.put("Street" , "Street");
		genFieldContMap.put("City" , "City");
		genFieldContMap.put("State" , "State");
		genFieldContMap.put("Country" , "Country");
		genFieldContMap.put("ZIP" , "ZIP");
		genFieldContMap.put("MobilePhone" , "Mobile");
		genFieldContMap.put("CustomerID" , "Customer ID" );
		genFieldContMap.put("Gender" , "Gender");
		genFieldContMap.put("HomeStore" , "Home Store");
		genFieldContMap.put("SubsidiaryNumber", "Subsidiary Number");
		genFieldContMap.put("BirthDay" , "BirthDay");
		genFieldContMap.put("Anniversary" , "Anniversary");
		genFieldContMap.put("CreatedDate" , "Created Date");
		
		genFieldSalesMap.put("CustomerID", "Customer ID");
		genFieldSalesMap.put("ReceiptNumber", "Receipt Number");
		genFieldSalesMap.put("SaleDate", "Sale Date");
		genFieldSalesMap.put("Qty", "Quantity");
		genFieldSalesMap.put("SalePrice", "Sale Price");
		genFieldSalesMap.put("Tax", "Tax");
		genFieldSalesMap.put("PromoCode", "Promo Code");
		genFieldSalesMap.put("StoreNumber", "Store Number");
		genFieldSalesMap.put("SubsidiaryNumber", "Subsidiary Number");
		genFieldSalesMap.put("SKU", "SKU");
		genFieldSalesMap.put("TenderType", "Tender Type");
		genFieldSalesMap.put("Doc_Sid", DOC_SID);
		genFieldSalesMap.put("Item_Sid", "Item Sid");
		genFieldSalesMap.put("Discount", "Discount");
		
		genFieldSKUMap.put("StoreNumber", "Store Number");
		genFieldSKUMap.put("SubsidiaryNumber", "Subsidiary Number");
		genFieldSKUMap.put("SKU", "SKU");
		genFieldSKUMap.put("Description", "Description");
		genFieldSKUMap.put("ListPrice", "List Price");
		genFieldSKUMap.put("ItemCategory", "Item Category");
		genFieldSKUMap.put("Item_Sid", "Item Sid");
		genFieldSKUMap.put("VC", "Vendor");
		genFieldSKUMap.put("D_Code", "Department");
		genFieldSKUMap.put("C_Code", "Class");
		genFieldSKUMap.put("S_Code", "Subclass");
		genFieldSKUMap.put("DCS", "DCS");
		

	}
	
	
	private static String[] defaultFieldArray = StringUtils.split(contctGenFieldStr, ','); 
	private static String[] genSalesArray = StringUtils.split(salesGenFieldStr, ','); 
	private static String[] genSkuArr  = StringUtils.split(skuGenfieldStr, ','); 
	//private static String[] defaultDRArray = StringUtils.split(digitalReceiptDefaultStr, ',');
	private static String[] drStrArr = null;
	
	private boolean isAdmin;
	Session sessionScope = null;
	private Div userOrgListDivId, digiReceiptSettingsDivId;
	String CreditCard="Credit Card";
	String BillToInfo1="BillToInfo1";
	String BillToInfo2="BillToInfo2";
	String COD="COD";
	String CustomTender="CustomTender";
	String NonInventoryItem="NonInvItem";
	String Tender="Tender";
	int count=0;
	public POSSettingsController(){
		
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
     	PageUtil.setHeader("POS Settings","",style,true);
     	customeTemplatesDao = (CustomTemplatesDao)SpringUtil.getBean("customTemplatesDao");
     	sessionScope = Sessions.getCurrent();
     	listIdsSet = (Set<Long>)sessionScope.getAttribute(Constants.LISTIDS_SET);
     	myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
     	updateOptSyncDataDao = (UpdateOptSyncDataDao) SpringUtil.getBean("updateOptSyncDataDao");
     	
     	
	}
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		usersDaoForDML = (UsersDaoForDML)SpringUtil.getBean("usersDaoForDML");
		mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		mailingListDaoForDML = (MailingListDaoForDML)SpringUtil.getBean("mailingListDaoForDML");
		posMappingDao= (POSMappingDao)SpringUtil.getBean("posMappingDao");
		posMappingDaoForDML= (POSMappingDaoForDML)SpringUtil.getBean("posMappingDaoForDML");
		userPOSFTPSettingsDao= (UserPosFTPSettingsDao)SpringUtil.getBean("userPOSFTPSettingsDao");
		userPOSFTPSettingsDaoForDML= (UserPosFTPSettingsDaoForDML)SpringUtil.getBean("userPOSFTPSettingsDaoForDML");
		digitalReceiptsJSONDao = (DigitalReceiptsJSONDao)SpringUtil.getBean("digitalReceiptsJSONDao");
		digitalReceiptsJSONDaoForDML = (DigitalReceiptsJSONDaoForDML)SpringUtil.getBean("digitalReceiptsJSONDaoForDML");
		//digitalReceiptUserSettingsDao= (DigitalReceiptUserSettingsDao) SpringUtil.getBean("digitalReceiptUserSettingsDao");
//		fileTypeComboBxId.setSelectedIndex(0);
		isAdmin = (Boolean)sessionScope.getAttribute("isAdmin");
		
		//get The UserOrganization List
		if(isAdmin) {
			setUserOrg();
			setUserOrgID();
//			setOptSyncAuthKey();
			userOrgListDivId.setVisible(true);
			digiReceiptSettingsDivId.setVisible(true);
		}else {
			users = GetUser.getUserObj();
			//get DoubleOpt custom templates of the User
			getWelcomeTemplateList();
			//get Parental Consent templates of  the User
			getParentalConsentTemplateList();
			// get loyalty opt in template for the user
			getLoyaltyTemplateList();
			// get welcome email message template for the user
			getWelcomeEmailTemplateList();
			if(defaultPOSListSettings(users)) {
				clearAndDefaultPOSMappSettings();
				//the truth value of this if condition also implies that text box for list name is disabled.
				//based on the truth condition, visibility for the following buttons are set.
				creatListNxtBtnId.setVisible(false);
				step1NxtBtnId.setVisible(true);
				step1SaveBtnId.setVisible(true);
				
			}
			else{
				creatListNxtBtnId.setVisible(true);
				step1NxtBtnId.setVisible(false);
				step1SaveBtnId.setVisible(false);
				
			}
			if(users.getloyaltyServicetype()!=null && users.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_SB)){
                optionsbasedOnloyalityType.setVisible(true);
                optionsbasedOnloyalityTypeCheckbox.setVisible(true);
	        } else {
	        		optionsbasedOnloyalityType.setVisible(false);
	        		optionsbasedOnloyalityTypeCheckbox.setVisible(false);
	        } 
			
		}
		alertMailTxtbxId.setValue(PropertyUtil.getPropertyValueFromDB("AlertToEmailId"));
		//Set<String> drAttrSet = Utility.getDigitalReceiptsAttr();
				
		//drStrArr = drAttrSet.toArray(new String[0]);
	}
	
	
	
	private void setUserOrg() {

		
	//	List<UserOrganization> orgList	= usersDao.findAllOrganizationID((String)orgListBxId.getSelectedItem().getValue(),(String)orgListBx.getSelectedItem().getValue());
		List<UserOrganization> orgList	= usersDao.findAllOrganizationsPOS();
		if(orgList == null) {
			logger.debug("no organization list exist from the DB...");
			return ;
		}
		
		//Listitem tempList = new Listitem();
		//tempList.setParent(orgListBxId);
		
		Listitem tempItem = null;
		
		for (UserOrganization userOrganization : orgList) {
			
			//set Organization Name
			if(userOrganization.getOrganizationName() == null || userOrganization.getOrganizationName().trim().equals("")) continue;
			
			tempItem = new Listitem(userOrganization.getOrganizationName().trim(),userOrganization.getUserOrgId());
			tempItem.setParent(orgListBxId);
		} // for
		orgListBxId.setSelectedIndex(0);
		
	} // setUserOrg()
	
private void setUserOrgID() {

		
		List<UserOrganization> orgList	= usersDao.findAllOrganizationsPOS();
		
		if(orgList == null) {
			logger.debug("no organization list exist from the DB...");
			return ;
		}
		
		Listitem tempItem = null;
		
		for (UserOrganization userOrganization : orgList) {
			
			//set Organization Name
			if(userOrganization.getOrgExternalId() == null || userOrganization.getOrgExternalId().trim().equals("")) continue;
			
			tempItem = new Listitem(userOrganization.getOrgExternalId().trim(),userOrganization.getUserOrgId());
			tempItem.setParent(orgListBx);
		} // for
		orgListBxId.setSelectedIndex(0);
		
		
	} // setUserOrg()
	
	/*UserOrganization userOrgObj ;
private void setOptSyncAuthKey() {
		
		userOrgObj= GetUser.getUserObj().getUserOrganization();//.setOptSyncKey(optSyncAuthkey);
		if(userOrgObj != null) {
		
			optSynAuthKeyTbId.setValue(userOrgObj.getOptSyncKey());
			optSynAuthKeyTbId.setReadonly(true);
			generateKeyAnchId.setVisible(true);
		}
		
		
	}*/
	
	public void onSelect$orgListBxId() {
		
		if(orgListBxId.getSelectedIndex() == 0) {
			orgListBx.setSelectedIndex(0);
			usersListBxId.setSelectedIndex(0);
			return;
			
		}
		Long orgId = orgListBxId.getSelectedItem().getValue();
		
		for (Listitem orgIdItem : orgListBx.getItems()) {
			
			
			if(orgIdItem.getValue() != null && orgIdItem.getValue().equals(orgId)) {
				orgIdItem.setSelected(true);
				break;
			}
			//if(orgListBxId.getSelectedItem().getLabel().equals(selectStr))
			
			
		}
		
		Components.removeAllChildren(usersListBxId);
		if(optionsbasedOnloyalityType!= null) {
			optionsbasedOnloyalityType.setVisible(false);
		}
		if(optionsbasedOnloyalityTypeCheckbox!= null) {
			optionsbasedOnloyalityTypeCheckbox.setVisible(false);
		}
		creatListNxtBtnId.setVisible(false);
		step1NxtBtnId.setVisible(false);
		step1SaveBtnId.setVisible(false);
		
		posListTxtBoxId.setValue("");
		posListTxtBoxId.setDisabled(false);
		posListDescTbId.setValue("");
		doubleOptInCbId.setChecked(false);
		doubleOptInCbId.setDisabled(false);
		parentalConsentCbId.setChecked(false);
		parentalOptLtDiv.setVisible(false);
		doubleOptLtDiv.setVisible(false);
		parentalConsentCbId.setDisabled(false);
		
		//welcome email message
		welcomeEmailsCbId.setChecked(false);
		welcomeEmailsCbId.setDisabled(false);
		enableWelcomeEmailDivId.setVisible(false);
		
		
		//Loyalty check box
		loyaltyOptInCbId.setChecked(false);
		loyaltyOptInCbId.setDisabled(false);
		loyaltyOptLtDiv.setVisible(false);
		
		if(orgListBxId.getSelectedItem().getLabel().equals(selectStr)) {
			users = null;
			return;
		}
		Listitem tempList = new Listitem(selectStr);
		tempList.setParent(usersListBxId);
		
		List<Users> usersList = usersDao.getPrimaryUsersByOrg((Long)orgListBxId.getSelectedItem().getValue());
		
		if(usersList == null || usersList.size() == 0) {
			logger.debug("No users exists for the Selected Organization..");
			return;
		}
		Listitem tempItem = null;
		for (Users users : usersList) {
			String userNameStr = Utility.getOnlyUserName(users.getUserName());
//			logger.debug("UserName is ::"+userNameStr);
			
			tempItem = new Listitem(userNameStr,users);
			tempItem.setParent(usersListBxId);
			
		} // for
		
		if(usersListBxId.getItemCount() > 0) {
			logger.debug("usersListBxId count is .."+usersListBxId.getItemCount());
			usersListBxId.setSelectedIndex(0);
		}
		
		
	} // onSelect$orgListBxId
	
public void onSelect$orgListBx() {
	if(orgListBx.getSelectedIndex() == 0) {
		orgListBxId.setSelectedIndex(0);
		usersListBxId.setSelectedIndex(0);
		return;
		
	}
	Long orgEId = orgListBx.getSelectedItem().getValue();
	
	for (Listitem orgIdItem :orgListBxId.getItems()) {
		
		
		if(orgIdItem.getValue() != null && orgIdItem.getValue().equals(orgEId)) {
			orgIdItem.setSelected(true);
			break;
		}
		
	}
		
		Components.removeAllChildren(usersListBxId);
		if(optionsbasedOnloyalityType!= null) {
			optionsbasedOnloyalityType.setVisible(false);
		}
		if(optionsbasedOnloyalityTypeCheckbox!= null) {
			optionsbasedOnloyalityTypeCheckbox.setVisible(false);
		}
		creatListNxtBtnId.setVisible(false);
		step1NxtBtnId.setVisible(false);
		step1SaveBtnId.setVisible(false);
		
		posListTxtBoxId.setValue("");
		posListTxtBoxId.setDisabled(false);
		posListDescTbId.setValue("");
		doubleOptInCbId.setChecked(false);
		doubleOptInCbId.setDisabled(false);
		parentalConsentCbId.setChecked(false);
		parentalOptLtDiv.setVisible(false);
		doubleOptLtDiv.setVisible(false);
		parentalConsentCbId.setDisabled(false);
		
		//welcome email message
		welcomeEmailsCbId.setChecked(false);
		welcomeEmailsCbId.setDisabled(false);
		enableWelcomeEmailDivId.setVisible(false);
		
		
		//Loyalty check box
		loyaltyOptInCbId.setChecked(false);
		loyaltyOptInCbId.setDisabled(false);
		loyaltyOptLtDiv.setVisible(false);
		
		if(orgListBx.getSelectedItem().getLabel().equals(selectStr)) {
			users = null;
			return;
		}
		Listitem tempList = new Listitem(selectStr);
		tempList.setParent(usersListBxId);
		
		List<Users> usersList = usersDao.getPrimaryUsersByOrg((Long)orgListBx.getSelectedItem().getValue());
		
		if(usersList == null || usersList.size() == 0) {
			logger.debug("No users exists for the Selected Organization..");
			return;
		}
		Listitem tempItem = null;
		for (Users users : usersList) {
			String userNameStr = Utility.getOnlyUserName(users.getUserName());
//			logger.debug("UserName is ::"+userNameStr);
			
			tempItem = new Listitem(userNameStr,users);
			tempItem.setParent(usersListBxId);
			
		} // for
		
		if(usersListBxId.getItemCount() > 0) {
			logger.debug("usersListBxId count is .."+usersListBxId.getItemCount());
			usersListBxId.setSelectedIndex(0);
		}
		
		
	} // onSelect$orgListBx

	public void onSelect$usersListBxId() {
		posListTxtBoxId.setValue("");
		posListTxtBoxId.setDisabled(false);
		posListDescTbId.setValue("");
		doubleOptInCbId.setChecked(false);
//		doubleOptInCbId.setDisabled(false);
		doubleOptLtDiv.setVisible(false);
		parentalOptLtDiv.setVisible(false);
		
		loyaltyOptInCbId.setChecked(false);
		loyaltyOptLtDiv.setVisible(false);
		
		enableWelcomeEmailDivId.setVisible(false);
		
		parentalConsentCbId.setChecked(false);
//		parentalConsentCbId.setDisabled(false);
		
		// welcome email message 
		welcomeEmailsCbId.setChecked(false);
		
		if(usersListBxId.getSelectedItem().getLabel().equals(selectStr)) {
			
			creatListNxtBtnId.setVisible(false);
			step1NxtBtnId.setVisible(false);
			step1SaveBtnId.setVisible(false);
			users = null;
			posML = null;
			
			return;
		}
		users = (Users)usersListBxId.getSelectedItem().getValue();
		
		
		logger.debug("user Object is Exist"+users);
		
		
		if(users == null) return;
		
		//get DoubleOpt custom templates of the User
		getWelcomeTemplateList();
		//get Parental Consent templates of  the User
		getParentalConsentTemplateList();
		getLoyaltyTemplateList();
		// get welcome email message template for the user
		getWelcomeEmailTemplateList();
		
		//POS Mapping
		if(defaultPOSListSettings(users)) {
			
			creatListNxtBtnId.setVisible(false);
			step1NxtBtnId.setVisible(true);
			step1SaveBtnId.setVisible(true);
			clearAndDefaultPOSMappSettings();
		}
		else{
			creatListNxtBtnId.setVisible(true);
			step1NxtBtnId.setVisible(false);
			step1SaveBtnId.setVisible(false);
		}
		
		
		//POS FTP Settings
		//defaultFTPSettings();
	
		if(users.getloyaltyServicetype()!=null && users.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_SB)){
                optionsbasedOnloyalityType.setVisible(true);
                optionsbasedOnloyalityTypeCheckbox.setVisible(true);
        } else {
        		optionsbasedOnloyalityType.setVisible(false);
        		optionsbasedOnloyalityTypeCheckbox.setVisible(false);
        } 
	}
	
	public void onSelect$redeemTenderListBxId() {
		ninItemFieldListBxId.setVisible(false);
		ninItemFieldTbId.setVisible(false);
		redemptionTenderTbId.setVisible(redeemTenderListBxId.getSelectedItem().getValue()!=null && 
				(redeemTenderListBxId.getSelectedItem().getValue().toString().equals(CreditCard)
						|| redeemTenderListBxId.getSelectedItem().getValue().toString().equals(CustomTender)));
	}
	public void onSelect$redeemAsListBxId() {
		redeemTenderListBxId.setVisible(false);
		ninItemFieldListBxId.setVisible(false);
		redemptionTenderTbId.setVisible(false);
		if(redeemAsListBxId.getSelectedItem().getValue()!=null && redeemAsListBxId.getSelectedItem().getValue().equals(NonInventoryItem)) {
		ninItemFieldListBxId.setVisible(true);
		ninItemFieldListBxId.setSelectedIndex(0);
		ninItemFieldTbId.setVisible(redeemAsListBxId.getSelectedItem().getValue()!=null && 
				redeemAsListBxId.getSelectedItem().getValue().toString().equals(NonInventoryItem));
		}else if(redeemAsListBxId.getSelectedItem().getValue()!=null && redeemAsListBxId.getSelectedItem().getValue().equals(Tender)) {
			ninItemFieldListBxId.setVisible(false);
			ninItemFieldTbId.setVisible(false);
			redeemTenderListBxId.setVisible(true);
			onSelect$redeemTenderListBxId();
		}
	}
	
	private boolean defaultPOSListSettings(Users userObj) {
		try {
			posML = mailingListDao.findPOSMailingList(userObj);
			
			if(posML == null) {
				logger.debug("no POS mailing list existed ..");
				return false;
			}
			
			posListTxtBoxId.setValue(posML.getListName());
			posListTxtBoxId.setDisabled(true);
			posListDescTbId.setValue(posML.getDescription() == null ? "":posML.getDescription());
			posListDescTbId.setAttribute("OLDVALUE", posML.getDescription() == null ? "":posML.getDescription());
			//changeDefaultValueForCheckBoxItems(posML);
			//Double Opt Checkbox
			doubleOptInCbId.setChecked(posML.getCheckDoubleOptin());
			doubleOptInCbId.setAttribute("OLDVALUE", posML.getCheckDoubleOptin());
			optInEmailsLbId.setAttribute("OLDVALUE", selectMsgStr);
			if(doubleOptInCbId.isChecked()) {
				doubleOptLtDiv.setVisible(!doubleOptLtDiv.isVisible());
				Long custmTemplId = posML.getCustTemplateId();
				if(custmTemplId != null) {
					// get object of customtemplate
					
					for(Listitem item:optInEmailsLbId.getItems()) {
						
						CustomTemplates curItemTemp = (CustomTemplates)item.getValue();
						
						if(curItemTemp == null) continue;
						
						if(curItemTemp.getTemplateId().longValue() == posML.getCustTemplateId().longValue()){
							item.setSelected(true);
							optInEmailsLbId.setAttribute("OLDVALUE", item.getLabel());
							break;
						}
					}//for
				
			}else {
				optInEmailsLbId.getItemAtIndex(0).setLabel(defaultStr);
				optInEmailsLbId.getItemAtIndex(0).setSelected(true);
				optInEmailsLbId.setAttribute("OLDVALUE", defaultStr);
			}
//			doubleOptInCbId.setDisabled(true);
				onSelect$optInEmailsLbId();
			}
			//Parental Checkbox
			parentalConsentCbId.setChecked(posML.isCheckParentalConsent());
			parentalConsentCbId.setAttribute("OLDVALUE", posML.isCheckParentalConsent());
			
			if(parentalConsentCbId.isChecked()) {
				parentalOptLtDiv.setVisible(!parentalOptLtDiv.isVisible());
				
				Long custmTemplId = posML.getConsentCutomTempId();
				if(custmTemplId != null) {
					

					// get object of customtemplate
					
					for(Listitem item:parentalConsentEmailLbId.getItems()) {
						
						CustomTemplates curItemTemp = (CustomTemplates)item.getValue();
						
						if(curItemTemp == null) continue;
						
						if(curItemTemp.getTemplateId().longValue() == custmTemplId.longValue()){
							item.setSelected(true);
							parentalConsentEmailLbId.setAttribute("OLDVALUE", item.getLabel());
							break;
						}
					}//for
					
						
				} else {
					parentalConsentEmailLbId.getItemAtIndex(0).setLabel(defaultStr);
					parentalConsentEmailLbId.getItemAtIndex(0).setSelected(true);
					parentalConsentEmailLbId.setAttribute("OLDVALUE", defaultStr);
				}
			}else {
				parentalConsentEmailLbId.setAttribute("OLDVALUE", selectMsgStr);
			}
			onSelect$parentalConsentEmailLbId();
			//loyalty check
			loyaltyOptInCbId.setChecked(posML.isCheckLoyaltyOptin());
			loyaltyOptInCbId.setAttribute("OLDVALUE", posML.isCheckLoyaltyOptin());
			
			if(loyaltyOptInCbId.isChecked()) {
				loyaltyOptLtDiv.setVisible(!loyaltyOptLtDiv.isVisible());
				
				Long custmTemplId = posML.getLoyaltyCutomTempId();
				if(custmTemplId != null) {
					
					
						
					for(Listitem item:LoyaltyOptinEmailLbId.getItems()) {
						
							
							CustomTemplates curItemTemp = (CustomTemplates)item.getValue();
							
							if(curItemTemp == null) continue;
							
							if(curItemTemp.getTemplateId().longValue() == custmTemplId.longValue()){
								item.setSelected(true);
								LoyaltyOptinEmailLbId.setAttribute("OLDVALUE", item.getLabel());
								break;
							}
						
					}//for
						
				} else {
					LoyaltyOptinEmailLbId.getItemAtIndex(0).setLabel(defaultStr);
					LoyaltyOptinEmailLbId.getItemAtIndex(0).setSelected(true);
					onSelect$optInEmailsLbId();
					LoyaltyOptinEmailLbId.setAttribute("OLDVALUE", defaultStr);
				}
			}else {
				LoyaltyOptinEmailLbId.setAttribute("OLDVALUE", selectMsgStr);
			}
			// welcome email  message 
			onSelect$LoyaltyOptinEmailLbId();
			welcomeEmailsCbId.setChecked(posML.isCheckWelcomeMsg());
			welcomeEmailsCbId.setAttribute("OLDVALUE", posML.isCheckWelcomeMsg());
			
			if(welcomeEmailsCbId.isChecked()) {
				enableWelcomeEmailDivId.setVisible(!enableWelcomeEmailDivId.isVisible());
				
				Long custmTemplId = posML.getWelcomeCustTempId();
				if(custmTemplId != null) {
					
					CustomTemplates customTemplate = customeTemplatesDao.findCustTemplateById(custmTemplId);
					
					//logger.debug("got the custom template =====>"+customTemplate.getTemplateName());
						
					for(Listitem item:welcomeEmailsLbId.getItems()) {
						
						
						CustomTemplates curItemTemp = (CustomTemplates)item.getValue();
						
						if(curItemTemp == null) continue;
						
						if(curItemTemp.getTemplateId().longValue() == custmTemplId.longValue()){
							item.setSelected(true);
							editMsgBtnId.setVisible(true);
							welcomeEmailsLbId.setAttribute("OLDVALUE", item.getLabel());
							break;
						}
					
				}//forMessage
						
				} else {
					welcomeEmailsLbId.getItemAtIndex(0).setLabel(defaultStr);
					welcomeEmailsLbId.getItemAtIndex(0).setSelected(true);
					welcomeEmailsLbId.setAttribute("OLDVALUE", defaultStr);
				}
			}else {
				welcomeEmailsLbId.setAttribute("OLDVALUE", selectMsgStr);
			}
			onSelect$welcomeEmailsLbId();
			
			
			
			
			
			
			
			
			return true;
		} catch (Exception e) {
			logger.error("Exception  ::", e);
			return false;
		}
		
	} //defaultSettings
	
	
	private void changeDefaultValueForCheckBoxItems(MailingList posMaplingList) {
		if(posMaplingList != null && !posMaplingList.getListName().isEmpty() && posMaplingList.getCheckDoubleOptin()) {
			try {
				List<Listitem> optInEmailList = optInEmailsLbId.getItems();
				for(Listitem list:optInEmailList) {
					if(list.getLabel().equalsIgnoreCase("Select Template")) {
						list.setLabel("Default Template");
						editMsgBtnId.setVisible(false);
					}
				}
			} catch(Exception e) {
				logger.error(" - ** Exception to get the Default Message List - " + e + " **");
			}
		}
		if(posMaplingList != null && !posMaplingList.getListName().isEmpty() && posMaplingList.isCheckWelcomeMsg()) {
			try {
				List<Listitem> optInEmailList = welcomeEmailsLbId.getItems();
				for(Listitem list:optInEmailList) {
					if(list.getLabel().equalsIgnoreCase("Select Welcome Email Template")) {
						list.setLabel("Default Welcome Email Message");
						editMsgBtnId.setVisible(false);
					}
				}
			} catch(Exception e) {
				logger.error(" - ** Exception to get the Welcome template List - " + e + " **");
			}
		}
		if(posMaplingList != null && !posMaplingList.getListName().isEmpty() && posMaplingList.isCheckLoyaltyOptin()) {
			try {
				List<Listitem> optInEmailList = LoyaltyOptinEmailLbId.getItems();
				for(Listitem list:optInEmailList) {
					if(list.getLabel().equalsIgnoreCase("Select Loyalty Opt-in Message")) {
						list.setLabel("Default Loyalty Message");
						editMsgBtnId.setVisible(false);
					}
				}
			} catch(Exception e) {
				logger.error(" - ** Exception to get the Loyalty template List - " + e + " **");
			}
		}
		if(posMaplingList != null && !posMaplingList.getListName().isEmpty() && posMaplingList.isCheckParentalConsent()) {
			try {
				List<Listitem> optInEmailList = parentalConsentEmailLbId.getItems();
				for(Listitem list:optInEmailList) {
					if(list.getLabel().equalsIgnoreCase("Select Consent Email Template")) {
						list.setLabel("Default Consent Message");
						editMsgBtnId.setVisible(false);
					}
				}
			} catch(Exception e) {
				logger.error(" - ** Exception to get the Loyalty template List - " + e + " **");
			}
		}
	}
	
	public boolean validateSelectedTemplate() {
		

		boolean returnValue = false;
		
		if(doubleOptInCbId.isChecked()){
			if(optInEmailsLbId.getSelectedItem().getLabel().equals(selectMsgStr) ) {
				if(optInEmailsLbId.getItems().size() == 1 ) {
					MessageUtil.setMessage("Please create at least one Double Opt-In message under Auto Emails.", "color:red","TOP");
					returnValue = true;
					
				}else{
					
					MessageUtil.setMessage("Please Select Double Opt-In message.", "color:red","TOP");
					returnValue = true;
				}
			}
		}
		if(welcomeEmailsCbId.isChecked() && welcomeEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase(selectMsgStr)) {
			if(welcomeEmailsLbId.getItems().size() == 1) {
				MessageUtil.setMessage("Please create at least one Welcome message under Auto Emails.", "color:red","TOP");
				returnValue = true;
			}else  {
				MessageUtil.setMessage("Please Select Welcome message.", "color:red","TOP");
				returnValue = true;
			}
		}
		
		if(loyaltyOptInCbId.isChecked() && LoyaltyOptinEmailLbId.getSelectedItem().getLabel().equalsIgnoreCase(selectMsgStr)) {
			if(LoyaltyOptinEmailLbId.getItems().size() == 1) {
				MessageUtil.setMessage("Please create at least one Loyalty welcome message under Auto Emails.", "color:red","TOP");
				returnValue = true;
			}else  {
				MessageUtil.setMessage("Please Select Loyalty Welcome message.", "color:red","TOP");
				returnValue = true;
			}
		}
		if(parentalConsentCbId.isChecked() && parentalConsentEmailLbId.getSelectedItem().getLabel().equalsIgnoreCase(selectMsgStr)) {
			if(parentalConsentEmailLbId.getItems().size() == 1) {
				MessageUtil.setMessage("Please create at least one Parental Consent message under Auto Emails.", "color:red","TOP");
				returnValue = true;
			}else  {
				MessageUtil.setMessage("Please Select Parental Consent message.", "color:red","TOP");
				returnValue = true;
			}
		}
		return returnValue;
		
		
		
	}
	
	public  void getWelcomeTemplateList() {
		List<CustomTemplates> wlist = null;
		try {
			
			wlist = customeTemplatesDao.findAllByUser(users.getUserId(),"welcomemail");
			
			
			Components.removeAllChildren(optInEmailsLbId);

			
			Listitem item = null;
			item = new Listitem(selectMsgStr, null);
			item.setParent(optInEmailsLbId);
			
			if(wlist !=null && wlist.size() >0) {
				
				for (CustomTemplates customTemplates : wlist) {
					
					item = new Listitem(customTemplates.getTemplateName(), customTemplates);
					item.setParent(optInEmailsLbId);
					
					
				}
			}
			if(optInEmailsLbId.getItemCount() > 0) optInEmailsLbId.setSelectedIndex(0);
			
			
			
			logger.debug(" - Got welcome lists for the user. size : "+wlist.size());
			
		} catch(Exception e) {
			logger.error(" - ** Exception to get the Welcome template List - " + e + " **");
			
		}
	} // getWelcomeTemplateList
	
	public  void getParentalConsentTemplateList() {
		
		List<CustomTemplates> consentlist = null;
		try {
			
			
			//CustomTemplatesDao customeTemplatesDao = (CustomTemplatesDao)SpringUtil.getBean("customTemplatesDao");
			consentlist = customeTemplatesDao.findAllByUser(users.getUserId(),"parentalConsent");
			
			
			Components.removeAllChildren(parentalConsentEmailLbId);

			
			Listitem item = null;
			item = new Listitem(selectMsgStr, null);
			item.setParent(parentalConsentEmailLbId);
			
			if(consentlist != null && consentlist.size() >0) {
				logger.debug(" - Got consent email lists for the user. size : "+consentlist.size());
				
				for (CustomTemplates customTemplates : consentlist) {
					
					item = new Listitem(customTemplates.getTemplateName(), customTemplates);
					item.setParent(parentalConsentEmailLbId);
					
					
				}
			}
			if(parentalConsentEmailLbId.getItemCount() > 0 ) parentalConsentEmailLbId.setSelectedIndex(0);
			
		} catch(Exception e) {
			logger.error(" - ** Exception to get the consent template List - " + e + " **");
			
		}
	} // getParentalConsentTemplateList
	
	
	/**
	 * This method fetches all the user's Loyalty type custom templates</br>
	 * And Populates into its associated list box. 
	 * 
	 */
	public void getLoyaltyTemplateList() {
		

		//logger.info();
		List<CustomTemplates> consentlist = null;
		try {
			
			
			
			consentlist = customeTemplatesDao.findAllByUser(users.getUserId(),"loyaltyOptin");
			
			
			Components.removeAllChildren(LoyaltyOptinEmailLbId);

			
			Listitem item = null;
			item = new Listitem(selectMsgStr, null);
			item.setParent(LoyaltyOptinEmailLbId);
			
			if(consentlist != null && consentlist.size() >0) {
				logger.debug(" - Got consent email lists for the user. size : "+consentlist.size());
				
				for (CustomTemplates customTemplates : consentlist) {
					
					item = new Listitem(customTemplates.getTemplateName(), customTemplates);
					item.setParent(LoyaltyOptinEmailLbId);
					
					
				}
			}
			if(LoyaltyOptinEmailLbId.getItemCount() > 0 ) LoyaltyOptinEmailLbId.setSelectedIndex(0);
			
		} catch(Exception e) {
			logger.error(" - ** Exception to get the Loyalty template List - " + e + " **");
			
		}
	
		
	}
	
	//welcome msg template list
	
	
	public void getWelcomeEmailTemplateList() {
		

		List<CustomTemplates> consentlist = null;
		try {
			
			
			
			consentlist = customeTemplatesDao.findAllByUser(users.getUserId(),"webformWelcomeEmail");
			
			
			Components.removeAllChildren(welcomeEmailsLbId);

			
			Listitem item = null;
			item = new Listitem(selectMsgStr, null);
			item.setParent(welcomeEmailsLbId);
			
			if(consentlist != null && consentlist.size() > 0) {
				logger.debug(" - Got consent email lists for the user. size : "+consentlist.size());
				
				for (CustomTemplates customTemplates : consentlist) {
					
					item = new Listitem(customTemplates.getTemplateName(), customTemplates);
					item.setParent(welcomeEmailsLbId);
					
					
				}
			}
			if(welcomeEmailsLbId.getItemCount() > 0 ) welcomeEmailsLbId.setSelectedIndex(0);
			
		} catch(Exception e) {
			//logger.error(" - ** Exception to get the Loyalty template List - " + e + " **");
		
			}
}
	
		
	
	
	private void gotoStep(int step) {
		
		createListGroupBoxId.setVisible(step==1);
		posMappingDivId.setVisible(step==2);
		posFTPSettingsGroupBoxId.setVisible(step==3);
		mastersPosFTPSettingsGroupBoxId.setVisible(step==3);
	} // gotoStep
	
	
	
	//If Pos List not exist for selected user sets default mapping for created new list other wise get mapping fields for existed Mailing list
	public void onClick$creatListNxtBtnId() {
		if(validateSelectedTemplate()) {
			return;
		}
		if(isAdmin && orgListBxId.getSelectedIndex() == 0) {
			users =null;
			MessageUtil.setMessage("Please select organization.","color:red","TOP");
			return;
		}
		
		if(users == null){
			MessageUtil.setMessage("Please select user.","color:red","TOP");
			return ;
		}
		String posListStr = posListTxtBoxId.getValue().trim();
		
		if(posListStr == null || posListStr.trim().length() == 0){
			MessageUtil.setMessage("Please specify list name.","color:red","TOP");
			return ;
		}else if(!Utility.validateName(posListStr)){
			MessageUtil.setMessage("Please enter a valid list name. Special characters are not allowed.","color:red","TOP");
			return ;
		}else
			MessageUtil.clearMessage();
		
		posListStr = Utility.condense(posListStr);
		boolean isExists = isMLExist(posListStr);
		logger.debug("isExists  >>>"+isExists);
		
		
		
		if(!isExists){
			/*MessageUtil.setMessage("Name already exists. ","color:red","TOP");
			return;*/
			
			
			Calendar cal = MyCalendar.getNewCalendar();
			
			//Create New PosList if not Exist
			
			  boolean optin = doubleOptInCbId.isChecked() &&  doubleOptLtDiv.isVisible();
			  boolean consent = parentalConsentCbId.isChecked() && parentalOptLtDiv.isVisible();
			  boolean loyalty = loyaltyOptInCbId.isChecked() && loyaltyOptLtDiv.isVisible();
			  boolean welcome = welcomeEmailsCbId.isChecked() && enableWelcomeEmailDivId.isVisible();
				
				
				Long optInCustTemplateId = null;
				Long consentCustTemplateId = null;
				Long loyaltyCustomTempId = null;
				Long welcomeMsgCustTempId= null;
					
					if(optin) {
						Listitem optInListitem = optInEmailsLbId.getSelectedItem();
						
						if(optInListitem.getIndex() != 0) {
							CustomTemplates optInCustomTemplates = (CustomTemplates)optInListitem.getValue();
							optInCustTemplateId = optInCustomTemplates.getTemplateId();
						}
					}//if optin
					
					if(consent) {
						Listitem consentListitem1 = parentalConsentEmailLbId.getSelectedItem();
						
						if(consentListitem1.getIndex() != 0) {
							CustomTemplates consentCustomTemplates = (CustomTemplates)consentListitem1.getValue();
								consentCustTemplateId = consentCustomTemplates.getTemplateId();
						}
					
					}
					
					if(loyalty) {
						Listitem consentListitem1 = LoyaltyOptinEmailLbId.getSelectedItem();
						
						if(consentListitem1.getIndex() != 0) {
							CustomTemplates loyaltyCustomTemplates = (CustomTemplates)consentListitem1.getValue();
							loyaltyCustomTempId = loyaltyCustomTemplates.getTemplateId();
						}
					
					}
					
					if(welcome) {
						Listitem consentListitem1 = welcomeEmailsLbId.getSelectedItem();
						
						if(consentListitem1.getIndex() != 0) {
							CustomTemplates welcomeMsgCustomTemplates = (CustomTemplates)consentListitem1.getValue();
							welcomeMsgCustTempId = welcomeMsgCustomTemplates.getTemplateId();
						}
					
					}//welcome 
					
					
					logger.debug("Custom template Id is :"+consentCustTemplateId);
					
					
					
					
					posML = new MailingList(posListStr, posListDescTbId.getValue(),cal,
								"Active", cal,cal,false, false,null,users,
								optin, optInCustTemplateId, consent, consentCustTemplateId,
								loyalty, loyaltyCustomTempId, welcome, welcomeMsgCustTempId);
					
					long mlbit = mailingListDao.getNextAvailableMbit(users.getUserId());
					if(mlbit == 0l){
						MessageUtil.setMessage("You have exceeded limit on maximum number of lists(60). " +
								"Please delete one or more lists to create a new list.", "red");
						return;
					}
					
					posML.setMlBit(mlbit);
					posML.setListSize(0L);
				
			/*//
			if(doubleOptInCbId.isChecked() && (optInEmailsLbId.getSelectedIndex() > 0) && 
						parentalConsentCbId.isChecked() && (parentalConsentEmailLbId.getSelectedIndex() > 0)) {
				
				posML =  new MailingList(posListStr, posListDescTbId.getValue(), cal,
					"Active", cal, cal, false, false, null, users, doubleOptInCbId.isChecked(), custTemplateId, parentalConsentCbId.isChecked(), consentCutomTempId);
			}else 
				posML =  new MailingList(posListStr, posListDescTbId.getValue(),cal,
						"Active", cal,cal,false, false,null,users,doubleOptInCbId.isChecked(),parentalConsentCbId.isChecked());
			*/
			
			
			posML.setListType("POS");
			
		} 
		else {
			
			String oldVal = (String)posListDescTbId.getAttribute("OLDVALUE");
			Boolean doubleOptFlag =(Boolean)doubleOptInCbId.getAttribute("OLDVALUE");
			Boolean parentalFlag = (Boolean)parentalConsentCbId.getAttribute("OLDVALUE");
			Boolean loyaltyFlag = (Boolean)loyaltyOptInCbId.getAttribute("OLDVALUE");
			Boolean welcomeFlag =(Boolean)welcomeEmailsCbId.getAttribute("OLDVALUE");
			String doubleOptOldValStr = (String)optInEmailsLbId.getAttribute("OLDVALUE");
			String parentalConsOlValStr = (String)parentalConsentEmailLbId.getAttribute("OLDVALUE");
			String loyaltyOldValStr = (String)LoyaltyOptinEmailLbId.getAttribute("OLDVALUE");
			String welcomMsgOldValStr =(String)welcomeEmailsLbId.getAttribute("OLDVALUE");
			
			if(oldVal!=null && oldVal.trim().equals(posListDescTbId.getValue().trim())
							&& doubleOptFlag.booleanValue() == doubleOptInCbId.isChecked()
							&& loyaltyFlag.booleanValue() == loyaltyOptInCbId.isChecked()
							&& parentalFlag.booleanValue() == parentalConsentCbId.isChecked()
							&& welcomeFlag.booleanValue() == welcomeEmailsCbId.isChecked()
							&& doubleOptOldValStr.equals(optInEmailsLbId.getSelectedItem().getLabel())
							&& parentalConsOlValStr.equals(parentalConsentEmailLbId.getSelectedItem().getLabel())
							&& loyaltyOldValStr.equals(LoyaltyOptinEmailLbId.getSelectedItem().getLabel())
							&& welcomMsgOldValStr.equals(welcomeEmailsLbId.getSelectedItem().getLabel())) {
				gotoStep(2);
				return;
			}
			//set Description
			posML.setDescription(posListDescTbId.getValue());
			//Double Opt in Check
			posML.setCheckDoubleOptin(doubleOptInCbId.isChecked());
			
			//Parental Consent Check
			posML.setCheckParentalConsent(parentalConsentCbId.isChecked());
			//Loyalty Opt-in check
			
			posML.setCheckLoyaltyOptin(loyaltyOptInCbId.isChecked());
			
			// welcome msg check
			posML.setCheckWelcomeMsg(welcomeEmailsCbId.isChecked());
			//set doubleOPt customTemplates
			
			if(doubleOptInCbId.isChecked()) {
				
				Long optInCustTemplateId = null;
				Listitem optInListitem = optInEmailsLbId.getSelectedItem();
				
				if(optInListitem.getIndex() != 0) {
					CustomTemplates optInCustomTemplates = (CustomTemplates)optInListitem.getValue();
					optInCustTemplateId = optInCustomTemplates.getTemplateId();
				}
				posML.setCustTemplateId(optInCustTemplateId);
			}else {
				posML.setCustTemplateId(null);
			}
			
			//set ParentalConsent Custom Template
			if(parentalConsentCbId.isChecked()) {
				Long consentCustTemplateId = null;
				Listitem consentListitem1 = parentalConsentEmailLbId.getSelectedItem();
				
				if(consentListitem1.getIndex() != 0) {
					CustomTemplates consentCustomTemplates = (CustomTemplates)consentListitem1.getValue();
						consentCustTemplateId = consentCustomTemplates.getTemplateId();
				}
				posML.setConsentCutomTempId(consentCustTemplateId);
			}else {
				posML.setConsentCutomTempId(null);
			}
			//set Loyalty Opt-in Custom Template
			if(loyaltyOptInCbId.isChecked()) {
				Long loyaltyCustTemplateId = null;
				Listitem loyaltyListitem1 = LoyaltyOptinEmailLbId.getSelectedItem();
				
				if(loyaltyListitem1.getIndex() != 0) {
					CustomTemplates consentCustomTemplates = (CustomTemplates)loyaltyListitem1.getValue();
						loyaltyCustTemplateId= consentCustomTemplates.getTemplateId();
				}
				posML.setLoyaltyCutomTempId(loyaltyCustTemplateId);
			}else {
				posML.setLoyaltyCutomTempId(null);
			}
			// set webform Welcome message custom Template 
			if(welcomeEmailsCbId.isChecked()) {
				Long welcomemsgCustTemplateId = null;
				Listitem welcomeMsgListitem1 = welcomeEmailsLbId.getSelectedItem();
				
				if(welcomeMsgListitem1.getIndex() != 0) {
					CustomTemplates welcomeCustomTemplates = (CustomTemplates)welcomeMsgListitem1.getValue();
					welcomemsgCustTemplateId= welcomeCustomTemplates.getTemplateId();
				}
				posML.setWelcomeCustTempId(welcomemsgCustTemplateId);
			}else {
				posML.setWelcomeCustTempId(null);
			}
			
		}
		
		mailingListDaoForDML.saveOrUpdate(posML);
		
		if(!isAdmin){
			listIdsSet.add(posML.getListId());
			}
		//listIdsSet.add(posML.getListId());
		listIdsSet = (Set<Long>)session.setAttribute(Constants.LISTIDS_SET, listIdsSet);
		logger.debug("pos List saved Successfully");
		
		posListTxtBoxId.setDisabled(true);
		
		MessageUtil.setMessage(posListStr+" list saved successfully.","color:blue","TOP");
		
		if(!isExists) {
			logger.debug("set default POS mapping for the new POS List");
			Set<String> set  = null;
			
			List contMapList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", posML.getUsers().getUserId());
				
			if(contMapList == null || contMapList.size() == 0)	{
				logger.info("contact map list is null");
				set =  genFieldContMap.keySet();
				for (String string : set) {
					defaultGenFieldMapp(string ,Constants.POS_MAPPING_TYPE_CONTACTS);
				}
			}
			
			
			set =  genFieldSalesMap.keySet();
			for (String string : set) {
				defaultGenFieldMapp(string ,Constants.POS_MAPPING_TYPE_SALES);
			}
			
			set =  genFieldSKUMap.keySet();
			for (String string : set) {
				defaultGenFieldMapp(string ,"SKU");
			}
			
			clearAndDefaultPOSMappSettings();
		}
		
		gotoStep(2);
		
	} //onClick$creatListNxtBtnId
	
	
	public void onClick$step1SaveBtnId(){
		if(validateSelectedTemplate()) {
			return;
		}
        String posListStr = posListTxtBoxId.getValue().trim();
		
		String oldVal = (String)posListDescTbId.getAttribute("OLDVALUE");
		Boolean doubleOptFlag =(Boolean)doubleOptInCbId.getAttribute("OLDVALUE");
		Boolean parentalFlag = (Boolean)parentalConsentCbId.getAttribute("OLDVALUE");
		Boolean loyaltyFlag = (Boolean)loyaltyOptInCbId.getAttribute("OLDVALUE");
		Boolean welcomeFlag =(Boolean)welcomeEmailsCbId.getAttribute("OLDVALUE");
		String doubleOptOldValStr = (String)optInEmailsLbId.getAttribute("OLDVALUE");
		String parentalConsOlValStr = (String)parentalConsentEmailLbId.getAttribute("OLDVALUE");
		String loyaltyOldValStr = (String)LoyaltyOptinEmailLbId.getAttribute("OLDVALUE");
		String welcomMsgOldValStr =(String)welcomeEmailsLbId.getAttribute("OLDVALUE");
		
		if(oldVal!=null && oldVal.trim().equals(posListDescTbId.getValue().trim())
						&& doubleOptFlag.booleanValue() == doubleOptInCbId.isChecked()
						&& loyaltyFlag.booleanValue() == loyaltyOptInCbId.isChecked()
						&& parentalFlag.booleanValue() == parentalConsentCbId.isChecked()
						&& welcomeFlag.booleanValue() == welcomeEmailsCbId.isChecked()
						&& doubleOptOldValStr.equals(optInEmailsLbId.getSelectedItem().getLabel())
						&& parentalConsOlValStr.equals(parentalConsentEmailLbId.getSelectedItem().getLabel())
						&& loyaltyOldValStr.equals(LoyaltyOptinEmailLbId.getSelectedItem().getLabel())
						&& welcomMsgOldValStr!=null &&  welcomMsgOldValStr.equals(welcomeEmailsLbId.getSelectedItem().getLabel())) {
			//gotoStep(2);
			MessageUtil.setMessage(posListStr+" list saved successfully.","color:blue","TOP");
			return;
		}
		//set Description
		posML.setDescription(posListDescTbId.getValue());
		//Double Opt in Check
		posML.setCheckDoubleOptin(doubleOptInCbId.isChecked());
		
		//Parental Consent Check
		posML.setCheckParentalConsent(parentalConsentCbId.isChecked());
		//Loyalty Opt-in check
		
		posML.setCheckLoyaltyOptin(loyaltyOptInCbId.isChecked());
		
		// welcome msg check
		posML.setCheckWelcomeMsg(welcomeEmailsCbId.isChecked());
		//set doubleOPt customTemplates
		
		if(doubleOptInCbId.isChecked()) {
			
			Long optInCustTemplateId = null;
			Listitem optInListitem = optInEmailsLbId.getSelectedItem();
			
			if(optInListitem.getIndex() != 0) {
				CustomTemplates optInCustomTemplates = (CustomTemplates)optInListitem.getValue();
				optInCustTemplateId = optInCustomTemplates.getTemplateId();
			}
			posML.setCustTemplateId(optInCustTemplateId);
		}else {
			posML.setCustTemplateId(null);
		}
		
		//set ParentalConsent Custom Template
		if(parentalConsentCbId.isChecked()) {
			Long consentCustTemplateId = null;
			Listitem consentListitem1 = parentalConsentEmailLbId.getSelectedItem();
			
			if(consentListitem1.getIndex() != 0) {
				CustomTemplates consentCustomTemplates = (CustomTemplates)consentListitem1.getValue();
					consentCustTemplateId = consentCustomTemplates.getTemplateId();
			}
			posML.setConsentCutomTempId(consentCustTemplateId);
		}else {
			posML.setConsentCutomTempId(null);
		}
		//set Loyalty Opt-in Custom Template
		if(loyaltyOptInCbId.isChecked()) {
			Long loyaltyCustTemplateId = null;
			Listitem loyaltyListitem1 = LoyaltyOptinEmailLbId.getSelectedItem();
			
			if(loyaltyListitem1.getIndex() != 0) {
				CustomTemplates consentCustomTemplates = (CustomTemplates)loyaltyListitem1.getValue();
					loyaltyCustTemplateId= consentCustomTemplates.getTemplateId();
			}
			posML.setLoyaltyCutomTempId(loyaltyCustTemplateId);
		}else {
			posML.setLoyaltyCutomTempId(null);
		}
		// set webform Welcome message custom Template 
		if(welcomeEmailsCbId.isChecked()) {
			Long welcomemsgCustTemplateId = null;
			Listitem welcomeMsgListitem1 = welcomeEmailsLbId.getSelectedItem();
			
			if(welcomeMsgListitem1.getIndex() != 0) {
				CustomTemplates welcomeCustomTemplates = (CustomTemplates)welcomeMsgListitem1.getValue();
				welcomemsgCustTemplateId= welcomeCustomTemplates.getTemplateId();
			}
			posML.setWelcomeCustTempId(welcomemsgCustTemplateId);
		}else {
			posML.setWelcomeCustTempId(null);
		}
		
        mailingListDaoForDML.saveOrUpdate(posML);
		
        if(!isAdmin){
		listIdsSet.add(posML.getListId());
        }
		listIdsSet = (Set<Long>)session.setAttribute(Constants.LISTIDS_SET, listIdsSet);
		logger.debug("pos List saved Successfully");
		
		posListTxtBoxId.setDisabled(true);
		
		MessageUtil.setMessage(posListStr+" list saved successfully.","color:blue","TOP");
		Redirect.goTo(PageListEnum.EMPTY);
		Redirect.goTo(PageListEnum.ADMIN_POS_SETTINGS);
		
	}//onClick$step1SaveBtnId()
	
	public void onClick$step1NxtBtnId(){
		if(validateSelectedTemplate()) {
			return;
		}
		clearAndDefaultPOSMappSettings();
		gotoStep(2);
	}
	
	
	
	
	//create Settings of All general Field Map if we creating a new List
	private void defaultGenFieldMapp(String genFieldLbl, String mapType) {
		
		try {
			POSMapping posMapping = new POSMapping();
			
			//POS Attr
			posMapping.setPosAttribute(genFieldLbl);
//			posMapping.setDisplayLabel(genFieldLbl);
			String optCulFieldStr = "";
			String drFieldStr = "";
			if(mapType.equals(Constants.POS_MAPPING_TYPE_CONTACTS)) {
				optCulFieldStr = genFieldContMap.get(genFieldLbl);
				//drFieldStr = digiReceiptContMap.get(genFieldLbl);
                //logger.info("pos attr: "+genFieldLbl);
				/*drFieldStr = POSFieldsEnum.findByPOSAttribute(genFieldLbl).getDrKeyField();
				String[] drKeyFieldArr = drFieldStr.split(",");
				posMapping.setDigitalReceiptAttribute(drKeyFieldArr[0]);*/
				
				//Display Label
				if(optCulFieldStr.equals("Email")){
					posMapping.setDisplayLabel("Email ID");
					posMapping.setUniquePriority(2);
				}
				else if(optCulFieldStr.equals(MOBILE)) {
					posMapping.setDisplayLabel("Mobile Phone");
					posMapping.setUniquePriority(3);
				}
				else {
					posMapping.setDisplayLabel(optCulFieldStr);
				}
				
				//CustField Name
				posMapping.setCustomFieldName(genFieldContMap.get(genFieldLbl));
				
				drFieldStr = POSFieldsEnum.findByOCAttribute(genFieldContMap.get(genFieldLbl)).getDrKeyField();
				String[] drKeyFieldArr = drFieldStr.split(",");
				posMapping.setDigitalReceiptAttribute(drKeyFieldArr[0]);
				
				//DataTypes
				/*if(genFieldLbl.equals(ZIP) || genFieldLbl.equals("MobilePhone")) {
					posMapping.setDataType("Number");
				}
				else*/ 
				if(genFieldLbl.equals(BIRTHDAY) || genFieldLbl.equals(ANNIVERSARY)
						|| genFieldLbl.equals("CreatedDate")) {
					posMapping.setDataType("Date(MM/dd/yyyy HH:mm:ss)");
					posMapping.setDrDataType("Date(MM/dd/yyyy HH:mm:ss)");
				}else {
					posMapping.setDataType("String");
					posMapping.setDrDataType("String");
				}
				
				if(genFieldLbl.equals("CustomerID")) {
					posMapping.setUniquePriority(1);
				}
				
			}
			else if(mapType.equals(Constants.POS_MAPPING_TYPE_SALES)) {
				//logger.info("entered into default");
				optCulFieldStr = genFieldSalesMap.get(genFieldLbl);
				//drFieldStr =digiReceiptSalesMap.get(genFieldLbl);
				
				/*drFieldStr =POSFieldsEnum.findByPOSAttribute(genFieldLbl).getDrKeyField();
				
				String[] drKeyFieldArr = drFieldStr.split(",");
				//dr
				posMapping.setDigitalReceiptAttribute(drKeyFieldArr[0]);*/
				
				//Display Label
				if(optCulFieldStr.equals("Sale Date")) {
					posMapping.setDisplayLabel("Purchase Date");
				}else if(optCulFieldStr.equals("Sale Price")) {
					posMapping.setDisplayLabel("Selling Price");
				}
				else {
					
					posMapping.setDisplayLabel(optCulFieldStr);
				}
				
				
				//set Unique In Across file 
				if(optCulFieldStr.equals(DOC_SID)) {
					posMapping.setUniqueInAcrossFiles(1);
				}
				
				
				//CustField Name
				posMapping.setCustomFieldName(genFieldSalesMap.get(genFieldLbl));
				
				drFieldStr =POSFieldsEnum.findByOCAttribute(genFieldSalesMap.get(genFieldLbl)).getDrKeyField();
				
				String[] drKeyFieldArr = drFieldStr.split(",");
				//dr
				posMapping.setDigitalReceiptAttribute(drKeyFieldArr[0]);//newm
				
				if(genFieldLbl.equals("SaleDate")) {
					posMapping.setDataType("Date(MM/dd/yyyy HH:mm:ss)");
					posMapping.setDrDataType("Date(MM/dd/yyyy HH:mm:ss)");
				}
				else if(genFieldLbl.equals("SalePrice") ||genFieldLbl.equals("Qty") || genFieldLbl.equals(TAX) || genFieldLbl.equals(DISCOUNT)) {
					posMapping.setDataType("Double");
					posMapping.setDrDataType("Double");
				}else if(genFieldLbl.equals("ReceiptNumber")) {
					posMapping.setDataType("String");
					posMapping.setDrDataType("String");
				}
				else {
					posMapping.setDataType("String");
					posMapping.setDrDataType("String");
				}
			}
			
			else if(mapType.equals(Constants.POS_MAPPING_TYPE_SKU)) {
				
				optCulFieldStr = genFieldSKUMap.get(genFieldLbl);
				//drFieldStr = digiReceiptSKUMap.get(genFieldLbl);
				
				
				//Display Label
				posMapping.setDisplayLabel(optCulFieldStr);
				
				//CustField Name
				posMapping.setCustomFieldName(genFieldSKUMap.get(genFieldLbl));
				
				drFieldStr = POSFieldsEnum.findByOCAttribute(genFieldSKUMap.get(genFieldLbl)).getDrKeyField();
				
				String[] drKeyFieldArr = drFieldStr.split(",");
				//dr
				posMapping.setDigitalReceiptAttribute(drKeyFieldArr[0]);
				if(genFieldLbl.equals("ListPrice")) {
					posMapping.setDataType("Double");
					posMapping.setDrDataType("Double");
				}else {
					posMapping.setDataType("String");
					posMapping.setDrDataType("String");
				}
				
				if(genFieldLbl.equals("Item_Sid") ) {
					posMapping.setUniquePriority(1);
				}
				/*if(genFieldLbl.equals(genFieldSKUMap.get("SKU"))) {
					posMapping.setUniquePriority(1);
				}*/
			}
			
			//Mapping Type
			posMapping.setMappingType(mapType);
			
			//Set user
			posMapping.setUserId(users.getUserId());
			
			//digitalReceiptUserSettings.setDataExtractionFlag(dataExtractionChkBxId.isChecked());
			//digitalReceiptUserSettingsDao.saveOrUpdate(digitalReceiptUserSettings);
			
			//logger.info(mapType+"pos mapping ..."+posMapping.getDataType()+"  "+posMapping.getDrDataType());
			//posMappingDao.saveOrUpdate(posMapping);
			posMappingDaoForDML.saveOrUpdate(posMapping);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception  ::", e);
		}
		
	} //defaultGenFieldMapp
	
	public boolean isMLExist(String listName){
		try{
			MailingList mailingList = mailingListDao.findByListName(listName, users.getUserId());
			if(mailingList == null)
				return false;
			else
				return true;
		}catch(Exception e){
			logger.error(" ** Exception :"+ e +" **");
			return false;
		}
	} //isMLExist

	
	private void deafultPOSMappingSettings() {
//		logger.debug("USerId is::"+user.getUserId());
		
		nxtBtnId.setVisible(false);
		saveContinueBtnId.setVisible(false);
		saveBtnId.setClass("idle140 greenbtn");
		
		if(isAdmin) {
			nxtBtnId.setVisible(true);
			//saveContinueBtnId.setVisible(true);
			saveContinueBtnId.setVisible(false);
			saveBtnId.setClass("idle140");
			saveContinueBtnId.setClass("idle140");
		}
		
		dataExtractionChkBxId.setChecked(users.isDigitalReceiptExtraction());
		SendAllDiscountChkBxId.setChecked(users.isShowOnlyHighestDiscReceiptDC());
		
		zoneWiseChkBxId.setChecked(users.isZoneWise());
		
		extractLoyaltyChkBxId.setChecked(users.isEnableLoyaltyExtraction());
		redeemPromoChkBxId.setChecked(users.isEnablePromoRedemption());
		validateItemsInReturnTrx.setChecked(users.isValidateItemsInReturnTrx());
		ignorePointsRedemption.setChecked(users.isIgnorePointsRedemption());
		sendRealtimeLoyaltyStatus.setChecked(users.getUserOrganization().isSendRealtimeLoyaltyStatus());
		eReceiptOverSMS.setChecked(users.isReceiptOnSMS());
		AllowBothDiscounts.setChecked(users.isAllowBothDiscounts());
		RedeemAsDiscount.setChecked(users.isRedemptionAsDiscount());
		ExcludeDiscountedItem.setChecked(users.isExcludeDiscountedItem());
		HighDiscountedItem.setChecked(users.isShowOnlyHighestLtyDC());
		ignoreissuanceOnRedemChkBxId.setChecked(users.isIgnoreissuanceOnRedemption());
	String temp=	users.getUserOrganization().getLoyaltyDisplayTemplate();
	logger.debug(temp);
	if(temp!=null) {
		SMSMsgTbId.setValue(temp);
	}
	else if(temp==null && count>0){
		String temp1="";
		SMSMsgTbId.setValue(temp1);
	}	
	else
	{
		String disTemplate = PropertyUtil.getPropertyValueFromDB("DefaultTemplate");
		SMSMsgTbId.setValue(disTemplate);
	}
		if(users.isEnableLoyaltyExtraction()){
			//enrolAllReqLbl.setVisible(true);
			//enrolRequestChkBxId.setVisible(true);
			
			enrolLbl.setVisible(true);
			enrolChkBxId.setVisible(true);
			
			issuanceLbl.setVisible(true);
			issuanceChkBxId.setVisible(true);
			
			returnLbl.setVisible(true);
			returnChkBxId.setVisible(true);
			
			redemptionLbl.setVisible(true);
			redemptionChkBxId.setVisible(true);
			IgnLbl.setVisible(true);
			IgnChkBxId.setVisible(true);
			redemptionTenderLbl.setVisible(extractLoyaltyChkBxId.isChecked());
			redeemAsListBxId.setVisible(extractLoyaltyChkBxId.isChecked());
			redeemTenderListBxId.setVisible(extractLoyaltyChkBxId.isChecked() && (redeemAsListBxId.getSelectedItem()!=null && redeemAsListBxId.getSelectedItem().getValue()!=null? redeemAsListBxId.getSelectedItem().getValue().equals(Tender):false));
			redemptionTenderDisplayLblId.setVisible(extractLoyaltyChkBxId.isChecked());
			redemptionTenderDispLblTbId.setVisible(true); 
			Components.removeAllChildren(redeemAsListBxId);
			Listitem redeemItem = new Listitem("--Select--",null);
			Listitem redeemItemNinv = new Listitem(NonInventoryItem,NonInventoryItem);
			Listitem redeemItemTender = new Listitem(Tender,Tender);
			redeemItem.setParent(redeemAsListBxId);
			redeemItemNinv.setParent(redeemAsListBxId);
			redeemItemTender.setParent(redeemAsListBxId);
			
			Components.removeAllChildren(redeemTenderListBxId);
			Listitem redeemTenderItem = new Listitem("--Select--",null);
			Listitem redeemTenderItemCreditCard = new Listitem(CreditCard,CreditCard);
			Listitem redeemTenderItemCOD = new Listitem(COD,COD);
			Listitem redeemTenderItemCustomTender = new Listitem("Custom Tender",CustomTender);
			redeemTenderItem.setParent(redeemTenderListBxId);
			redeemTenderItemCreditCard.setParent(redeemTenderListBxId);
			redeemTenderItemCOD.setParent(redeemTenderListBxId);
			redeemTenderItemCustomTender.setParent(redeemTenderListBxId);
			redeemTenderListBxId.setSelectedIndex(redeemTenderListBxId.isVisible() && users.getRedeemTender()!=null ? 
					((users.getRedeemTender().split(""+Constants.DELIMETER_COLON))[0].equalsIgnoreCase(CreditCard)?1
							:(users.getRedeemTender().split(""+Constants.DELIMETER_COLON))[0].equalsIgnoreCase(COD)?2
							:(users.getRedeemTender().split(""+Constants.DELIMETER_COLON))[0].equalsIgnoreCase(CustomTender)?3:0):0);

			redemptionTenderTbId.setVisible(redeemTenderListBxId.isVisible() && redeemTenderListBxId.getSelectedItem().getValue()!=null ? 
					((redeemTenderListBxId.getSelectedItem().getValue().toString().equalsIgnoreCase(CreditCard)
							|| redeemTenderListBxId.getSelectedItem().getValue().toString().equalsIgnoreCase(CustomTender))?true:false):false);
			
			redeemAsListBxId.setSelectedIndex(redeemAsListBxId.isVisible() && users.getNonInventoryItem()!=null?1:
				(redeemAsListBxId.isVisible() && users.getRedeemTender()!=null?2:0));
			ninItemFieldTbId.setVisible(redeemAsListBxId.isVisible() && ninItemFieldListBxId.isVisible() && ninItemFieldListBxId.getSelectedItem()!=null && ninItemFieldListBxId.getSelectedItem().getValue()!=null);
			
			redemptionReversalLbl.setVisible(returnLbl.isVisible());
			redemptionReversalChkBxId.setVisible(returnLbl.isVisible()); 
			
			cardInfoLbl.setVisible(true);
			cardInfoListBxId.setVisible(extractLoyaltyChkBxId.isChecked());
			Components.removeAllChildren(cardInfoListBxId);
			for(int i=0; i<cardInfoStr.length; i++) { 
				Listitem cardInfoItem = new Listitem(cardInfoStr[i],cardInfoStr[i]);
				cardInfoItem.setParent(cardInfoListBxId);
			}
			for(Listitem li : cardInfoListBxId.getItems()) {
				if(li.getValue()==null) {
					cardInfoListBxId.setSelectedIndex(0);
				}else {
					if(li.getValue().equals(users.getCardInfo())) {
						cardInfoListBxId.setSelectedItem(li);
						break;
					}
				}
			}
		}
		receiptNoteLbl.setVisible(users.isEnablePromoRedemption());
		receiptNoteListBxId.setVisible(users.isEnablePromoRedemption());
		itemNoteLbl.setVisible(users.isEnablePromoRedemption());
		itemNoteListBxId.setVisible(users.isEnablePromoRedemption());
		itemInfoLbl.setVisible(users.isEnablePromoRedemption());
		itemInfoFieldListBxId.setVisible(users.isEnablePromoRedemption());
		IgnLbl.setVisible(users.isEnablePromoRedemption()|| users.isEnableLoyaltyExtraction());
		IgnChkBxId.setVisible(users.isEnablePromoRedemption()||users.isEnableLoyaltyExtraction());
		Components.removeAllChildren(receiptNoteListBxId);
		Listitem recDiscItem = new Listitem("--Select--",null);
		recDiscItem.setParent(receiptNoteListBxId);
		for(int i=0; i<receiptDiscStr.length; i++) { 
			recDiscItem = new Listitem(receiptDiscStr[i],receiptDiscStr[i]);
			recDiscItem.setParent(receiptNoteListBxId);
		}
		for(Listitem li : receiptNoteListBxId.getItems()) {
			if(li.getValue()==null) {
				receiptNoteListBxId.setSelectedIndex(0);
			}else {
				if(li.getValue().equals(users.getReceiptNoteUsed())) {
					receiptNoteListBxId.setSelectedItem(li);
					break;
				}
			}
		}
		Components.removeAllChildren(itemNoteListBxId);
		Listitem itemNoteItem = new Listitem("--Select--",null);
		itemNoteItem.setParent(itemNoteListBxId);
		for(int i=0; i<itemDiscStr.length; i++) { 
			itemNoteItem = new Listitem(itemDiscStr[i],itemDiscStr[i]);
			itemNoteItem.setParent(itemNoteListBxId);
		}
		for(Listitem li : itemNoteListBxId.getItems()) {
			if(li.getValue()==null) {
				itemNoteListBxId.setSelectedIndex(0);
			}else {
				if(li.getValue().equals(users.getItemNoteUsed())) {
					itemNoteListBxId.setSelectedItem(li);
					break;
				}
			}
		}
		Components.removeAllChildren(itemInfoFieldListBxId);
		Listitem itemInfoItem = new Listitem("--Select--",null);
		itemInfoItem.setParent(itemInfoFieldListBxId);
		Listitem tempInfoItem = null;
		for(int i=0; i<itemInfoStr.length; i++) { 
			tempInfoItem = new Listitem(itemInfoStr[i],itemInfoStr[i]);
			tempInfoItem.setParent(itemInfoFieldListBxId);
		}
		for(Listitem li : itemInfoFieldListBxId.getItems()) {
			if(li.getValue()==null) {
				itemInfoFieldListBxId.setSelectedIndex(0);
			}else {
				if(li.getValue().equals(users.getItemInfo())) {
					itemInfoFieldListBxId.setSelectedItem(li);
					break;
				}
			}
		}
		//enrolRequestChkBxId.setChecked(users.isEnrollAllRequests());
		enrolChkBxId.setChecked(users.isEnrollFromDR());
		issuanceChkBxId.setChecked(users.isIssuanceFromDR());
		returnChkBxId.setChecked(users.isReturnFromDR());
		redemptionChkBxId.setChecked(users.isRedemptionFromDR());
		IgnChkBxId.setChecked(users.isIgnoretrxUpOnExtraction());
		redemptionTenderLbl.setVisible(users.isEnableLoyaltyExtraction());
		String redeemTender=users.getRedeemTender();
		if(redeemTender !=null && !redeemTender.isEmpty() && users.getNonInventoryItem()==null) {
			if(redeemTender.contains(""+Constants.DELIMETER_COLON)) {
					String[] redeemTenderType = redeemTender.split(""+Constants.DELIMETER_COLON);
					redeemTenderListBxId.setVisible(true);
					redeemTenderListBxId.setSelectedIndex(1);
					if (redeemTenderType[0].equalsIgnoreCase(CustomTender)) redeemTenderListBxId.setSelectedIndex(3);
					redemptionTenderTbId.setVisible(true);
					redemptionTenderTbId.setText(redeemTenderType!=null && redeemTenderType.length>1 ? redeemTenderType[1] : "");
			}else {
				redeemTenderListBxId.setSelectedIndex(2);	
			}
		}
		Components.removeAllChildren(ninItemFieldListBxId);
		Listitem tempInvnItem = null;
		for(int i=0; i<nonInventoryItemField.length; i++) { 
			tempInvnItem = new Listitem(nonInventoryItemField[i],nonInventoryItemField[i]);
			tempInvnItem.setParent(ninItemFieldListBxId);
		}
		
		String nonInvenItem = users.getNonInventoryItem();
		if(nonInvenItem!=null && !nonInvenItem.isEmpty() && users.getRedeemTender()==null) {
			if(nonInvenItem.contains(""+Constants.DELIMETER_COLON)) {
				ninItemFieldListBxId.setVisible(true);
				String[] nonInvnItemType = nonInvenItem.split(""+Constants.DELIMETER_COLON);
				for(Listitem li : ninItemFieldListBxId.getItems()) {
					if(li.getValue()==null) {
						ninItemFieldListBxId.setSelectedIndex(0);
					}else {
						if(li.getValue().equals(nonInvnItemType[0])) {
							ninItemFieldListBxId.setSelectedItem(li);
							break;
						}
					}
				}
				ninItemFieldTbId.setText(nonInvnItemType[1]);
			}
		}
		redemptionTenderDispLblTbId.setText(users.getRedeemTenderDispLabel());
		
		redemptionReversalLbl.setVisible(users.isReturnFromDR());
		redemptionReversalChkBxId.setVisible(users.isReturnFromDR());
		redemptionReversalChkBxId.setChecked(users.isPerformRedeemedAmountReversal());
		
		
		List<POSMapping> recordList = null;
		recordList = posMappingDao.findAllByUserId(users.getUserId());
		MastersToTransactionMappingsDao masterToTranscDao=(MastersToTransactionMappingsDao)SpringUtil.getBean("mastersToTransactionMappingsDao");
		List<MastersToTransactionMappings> tempList = masterToTranscDao.findByUserId(users.getUserId());
		
		if(recordList == null || recordList.size() == 0) return;
		//logger.info("total recordList size : "+recordList.size());
		for (POSMapping posMapping : recordList) {
			Row row = new Row();
			
			
			Listbox posMappingListBx = null;
//			logger.debug("posMapping.getMappingType() is :: "+posMapping.getMappingType());
			
			if(posMapping.getMappingType().trim().equals("BCRM")) continue;
			
			if(posMapping.getMappingType().trim().equalsIgnoreCase(Constants.POS_MAPPING_TYPE_CONTACTS)) {
				posMappingListBx =  createContactPosMappingListbox();
				posMappingListBx.setParent(row);
				
			}else if(posMapping.getMappingType().trim().equalsIgnoreCase(Constants.POS_MAPPING_TYPE_SALES)) {
				posMappingListBx =  createSalePosMappingFieldListBox();
				posMappingListBx.setParent(row);
			}else if(posMapping.getMappingType().trim().equals(Constants.POS_MAPPING_TYPE_SKU)) {
				posMappingListBx =  createSkuPosMappingFieldListBox();
				posMappingListBx.setParent(row);
			}
			
			if(posMappingListBx == null) continue;
			
			posMappingListBx.setAttribute("CUST_FIELD_LISTBOX", "CUST_FIELD_LISTBOX");
			posMappingListBx.addEventListener("onSelect", this);
			List posMappingChilItemList = posMappingListBx.getChildren();
			Listitem tempItem= null;
			for (Object object : posMappingChilItemList) {
				tempItem = (Listitem)object;
				if(tempItem.getLabel().equals(posMapping.getCustomFieldName())){
					posMappingListBx.setSelectedItem(tempItem);
					break;
				}
			}
			
			if(posMappingListBx == null) continue;		
//			Data Type
			String dataTypeStr = posMapping.getDataType();

			Listbox dataTypelb = createDataTypeListBox();
			dataTypelb.setAttribute("DATATYPE_LISTBOX", "DATATYPE_LISTBOX");
			dataTypelb.addEventListener("onSelect", this);
			
			dataTypelb.setParent(row);
			
			List ChildItemList =  dataTypelb.getChildren();
			
			for (Object object : ChildItemList) {
				
				Listitem listItem = (Listitem)object;
				
				if(dataTypeStr.contains("Date") &&  listItem.getLabel().equals("Date")) {
					dataTypelb.setSelectedItem(listItem); break;
					
				}else if(listItem.getLabel().equals(dataTypeStr)) {
					dataTypelb.setSelectedItem(listItem);
					break;
				}
				
			} // for
			
			if(posMappingListBx.getSelectedItem()!= null && !posMappingListBx.getSelectedItem().getLabel().startsWith("UDF")) {
				
				dataTypelb.setDisabled(true);
			}
			
			//dataTypelb.addEventListener("onSelect", this);
			
			
//			POS Attribute
			//Textbox posAttrTextBx = new Textbox(posMapping.getPosAttribute());
			//posAttrTextBx.setParent(row);
			
			Div posTypeDiv = new Div();
			Textbox posAttrTextBx = new Textbox(posMapping.getPosAttribute());
			posAttrTextBx.setParent(posTypeDiv);
			posAttrTextBx.setWidth("130px");
			//String dataTypeStr = posMapping.getDataType();
			
			Listbox dateFormatListBx = createDateFormatListbox();
			dateFormatListBx.setParent(posTypeDiv);
			dateFormatListBx.setStyle("margin: 0 2px 0 14px;");
			dateFormatListBx.setSelectedIndex(0);
			dateFormatListBx.setVisible(false);
			List ChildItemList1 =  dataTypelb.getChildren();
			
			
			for (Object object : ChildItemList1) {
				
				Listitem listItem = (Listitem)object;
				
				if(dataTypeStr.contains("Date") &&  listItem.getLabel().equals("Date")) {
					dataTypelb.setSelectedItem(listItem);
					dateFormatListBx.setVisible(true);
					
					List dateFormatList = dateFormatListBx.getChildren();
					String dataTypeStr1 =  dataTypeStr.substring(dataTypeStr.indexOf("(")+1, dataTypeStr.indexOf(")"));
					
					for (Object obj : dateFormatList) {
						
						Listitem tempListItem = (Listitem)obj;
						if(tempListItem.getLabel().equals(dataTypeStr1)) {
							dateFormatListBx.setSelectedItem(tempListItem);
							break;
						}
					}
				
					
				}else if(listItem.getLabel().equals(dataTypeStr)) {
					dataTypelb.setSelectedItem(listItem);
					break;
				}
				
			} // for
			
			if(posMappingListBx.getSelectedItem()!= null && !posMappingListBx.getSelectedItem().getLabel().startsWith("UDF")) {
				
				dataTypelb.setDisabled(true);
			}
			
			
			//dataTypelb.addEventListener("onSelect", this);
			posTypeDiv.setParent(row);
			
			
			
			//DR Attribute
			Div drTypeDiv = new Div();
			
			//dr combobox
			Combobox drAttrComboBx = null;
			if(posMappingListBx.getSelectedItem()!= null ){
				drAttrComboBx =	new Combobox(); 
				createDigitalReceiptCombobox(drAttrComboBx, posMappingListBx.getSelectedItem().getLabel(), null);
				drAttrComboBx.addEventListener("onChanging", this);
				drAttrComboBx.setSclass("cb_150w");
		
			}
					
			if(drAttrComboBx == null) continue;
			
			boolean drItemExist = true;
			List comboList = drAttrComboBx.getChildren();
			Comboitem comboItem= null;
			for (Object object : comboList) {
				comboItem = (Comboitem)object;
				if(comboItem.getLabel().equals(posMapping.getDigitalReceiptAttribute())){
					drAttrComboBx.setSelectedItem(comboItem);
					drItemExist = false;
					break;
				}
				
			}
			//fetch input text from db
			if(drItemExist){
				Comboitem cmbItem = new Comboitem();
				cmbItem.setLabel(posMapping.getDigitalReceiptAttribute());
				drAttrComboBx.appendChild(cmbItem);
				drAttrComboBx.setSelectedItem(cmbItem);
			//	drAttrComboBx.setSelectedIndex(0);
				//drAttrComboBx.setText(posMapping.getDigitalReceiptAttribute());
			}
	
			drAttrComboBx.setParent(drTypeDiv);
			
			//dr date format
			Listbox drDateFormatListBx = createDateFormatListbox();
			drDateFormatListBx.setParent(posTypeDiv);
			drDateFormatListBx.setStyle("margin: 0 2px 0 14px;");
			drDateFormatListBx.setSelectedIndex(0);
			drDateFormatListBx.setVisible(false);
			List ChildItemList2 =  dataTypelb.getChildren();
			String drdataTypeStr = posMapping.getDrDataType(); 
			for (Object object : ChildItemList2) {
				
				Listitem listItem = (Listitem)object;
				
				if(drdataTypeStr != null && drdataTypeStr.contains("Date") &&  listItem.getLabel().equals("Date")) {
					dataTypelb.setSelectedItem(listItem);
					drDateFormatListBx.setVisible(true);
					
					List dateFormatList = drDateFormatListBx.getChildren();
					String drdataTypeStr2 =  drdataTypeStr.substring(drdataTypeStr.indexOf("(")+1, drdataTypeStr.indexOf(")"));
					
					for (Object obj : dateFormatList) {
						
						Listitem tempListItem = (Listitem)obj;
						if(tempListItem.getLabel().equals(drdataTypeStr2)) {
							drDateFormatListBx.setSelectedItem(tempListItem);
							break;
						}
					}
				
					
				}/*else if(listItem.getLabel().equals(drdataTypeStr)) {
					dataTypelb.setSelectedItem(listItem);
					break;
				}*/
				
			} // for
			
			/*if(!posMappingListBx.getSelectedItem().getLabel().startsWith("UDF")) {
				
				dataTypelb.setDisabled(true);
			}*/
			
			
			if(isAdmin){
				
				drAttrComboBx.setDisabled(false);
				drDateFormatListBx.setDisabled(false);
				
			}
			else{
				drAttrComboBx.setDisabled(true);
				drDateFormatListBx.setDisabled(true);
			}
			
			
			
			
			//dataTypelb.addEventListener("onSelect", this);
			
			drDateFormatListBx.setParent(drTypeDiv);
			
			drTypeDiv.setParent(row);
			
			
			
//			Custom Field
			
			

			
			// Optional Value
			
			Div optDiv=new Div();
			optDiv.setParent(row);
			
			Combobox cb = new Combobox();
			cb.setSclass("cb_100w");
			cb.setParent(optDiv);
			
			if(posMapping.getOptionalValues()!=null) {
				String optValues[] = posMapping.getOptionalValues().split(Constants.ADDR_COL_DELIMETER);
				for (String optVal : optValues) {
					cb.appendItem(optVal);
				}
			}
			if(cb.getItemCount()>0) cb.setSelectedIndex(0);
			
			//Optional Value Add Action
			Image optAddValImg = new Image();
			optAddValImg.setSrc("/img/action_add.jpg");
			optAddValImg.setStyle("cursor:pointer;margin:0 5px 0 15px;");
			optAddValImg.addEventListener("onClick", this);
			optAddValImg.setAttribute("TYPE", "POS_ADD_OPTIONAL_VALUE");
			optAddValImg.setParent(optDiv);			
			
			//Optional Value Add Action
			Image optDelValImg = new Image();
			optDelValImg.setSrc("/img/action_delete.gif");
			optDelValImg.setStyle("cursor:pointer;");
			optDelValImg.addEventListener("onClick", this);
			optDelValImg.setAttribute("TYPE", "POS_DEL_OPTIONAL_VALUE");
			optDelValImg.setParent(optDiv);			
			
//			Display Label
			Textbox dispLabelTextBx = new Textbox(posMapping.getDisplayLabel());
			dispLabelTextBx.setWidth("120px");
			dispLabelTextBx.setParent(row);
			
//			Unique priority
			Intbox priIntBx = new Intbox();
			priIntBx.setMaxlength(1);
			priIntBx.setWidth("30px");
			if(posMapping.getUniquePriority()!=null) priIntBx.setValue(posMapping.getUniquePriority());
			
			priIntBx.setDisabled(true);
			if(isAdmin) {
				priIntBx.setDisabled(false);
			}
			priIntBx.setParent(row);
			
			Div div=new Div();
			div.setParent(row);

				
			row.setAttribute("referenceId", posMapping);
			String mappingType = null;
			boolean canproceed = false;
			if(posMapping.getMappingType().trim().equals(Constants.POS_MAPPING_TYPE_CONTACTS)) {
				mappingType = "POS_CONTACT_MAPPING";
//				delImg.setAttribute("TYPE", "POS_CONTACT_MAPPING");
				row.setParent(contactRowsId);
			}else if(posMapping.getMappingType().trim().equals(Constants.POS_MAPPING_TYPE_SALES)) {
				mappingType = "POS_SALES_MAPPING" ;
				//delImg.setAttribute("TYPE", "POS_SALES_MAPPING");
				row.setParent(salesRowsId);
			}else if(posMapping.getMappingType().trim().equals(Constants.POS_MAPPING_TYPE_SKU)) {
				mappingType = "POS_SKU_MAPPING";
				//delImg.setAttribute("TYPE", "POS_SKU_MAPPING");
				row.setParent(skuRowsId);
			}
			if(tempList != null && tempList.size() >0) {
				
				for(MastersToTransactionMappings eachmapping : tempList) {
					
					if( ( eachmapping.getParentId().getPosId().longValue() == posMapping.getPosId().longValue() ) || 
							( eachmapping.getChildId().getPosId().longValue() == posMapping.getPosId().longValue() )) canproceed = true;
					
				}
			}

			//Delete Action
			if(!canproceed) {
				
				Image delImg = new Image();
				delImg.setSrc("/images/action_delete.gif");
				delImg.setStyle("cursor:pointer;");
				delImg.setTooltiptext("Delete");
				delImg.addEventListener("onClick", this);
				delImg.setParent(div);
				delImg.setAttribute("TYPE", mappingType);
			}
			
		} //for
	} //deafultSettingPOSMappingSettings


	//DataType
	private Listbox createDataTypeListBox() {
		
		Listbox dataTypelb = new Listbox();
		
		Listitem tempItem = new Listitem("String");
		tempItem.setParent(dataTypelb);
		
		//String","Date","Number","Double","Boolean"
		tempItem = new Listitem("Date");
		tempItem.setParent(dataTypelb);
		
		tempItem = new Listitem("Number");
		tempItem.setParent(dataTypelb);
		
		tempItem = new Listitem("Double");
		tempItem.setParent(dataTypelb);
		dataTypelb.setMold("select");
		return dataTypelb;
		
	} //createDataTypeListBox()
	

	//DateFormat
	private Listbox createDateFormatListbox() {

		Listbox dateFormatListBx = new Listbox();
		
		Listitem tempItem = new Listitem("dd/MM/yyyy");
		tempItem.setParent(dateFormatListBx);
		
		//String","Date","Number","Double","Boolean"
		tempItem = new Listitem("dd-MM-yyyy");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("yyyy-MM-dd");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("MM/dd/yyyy");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("MM-dd-yyyy");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("MM/dd/yy");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("MM-dd-yy");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("dd/MM/yyyy HH:mm");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("MM/dd/yyyy HH:mm");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("MM/dd/yyyy HH:mm:ss");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("yyyy-MM-dd HH:mm:ss");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("dd-MMMMM-yyyy");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("dd-MMMMM-yy");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("yyyy-MM-dd HH:mm:ss.SSS");//added for mobile-app
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("MM-dd-yyyy HH:mm:ss"); //new one
		tempItem.setParent(dateFormatListBx);
		
		
		
		
		dateFormatListBx.setMold("select");
		return dateFormatListBx;
	} //createDateFormatListbox()
	
	//create ContactGenField ListBox
	private Listbox createContactPosMappingListbox() {

		Listbox dateFormatListBx = new Listbox();
//			contactGenFieldList = null;
		Listitem tempItem = null;
		
		
		//genralFieldList
		for (int i=0; i< defaultFieldArray.length; i++) {
//				tempStr = (String)contactGenFieldList.get(i);
			tempItem = new Listitem(defaultFieldArray[i]);
			tempItem.setParent(dateFormatListBx);
		}
		
		//UDF FieldList
		for(int i=0; i<udfSetStr.length; i++) { 
			tempItem = new Listitem(udfSetStr[i]);
			tempItem.setParent(dateFormatListBx);
		}
		dateFormatListBx.setMold("select");
		return dateFormatListBx;
		
	}// createContactPosMappingListbox()
	
	
	private void createDigitalReceiptCombobox(Combobox drCombobox, String ocfield, String mappingType){
		
		//Combobox digitalReceiptCombobx = new Combobox();
		//drCombobox
		Components.removeAllChildren(drCombobox);
		
		if(ocfield.startsWith("UDF") && mappingType != null && mappingType.equals("Contacts")){
			
			Set<String> drAttrSet = Utility.getDigitalReceiptsAttr();
			
			List list=new ArrayList();
			list.addAll(drAttrSet);
			Collections.sort(list);
			
			for(Object digiKey : list){
				
				if(digiKey.toString().startsWith("CreditCard") || digiKey.toString().startsWith("Items")){
					continue;
				}
				
				Comboitem comboItem = new Comboitem();
				comboItem.setLabel(digiKey.toString());
				drCombobox.appendChild(comboItem);
				
			}
			drCombobox.setSelectedIndex(0);
					
		}
		else if(ocfield.startsWith("UDF")){
			
			Set<String> drAttrSet = Utility.getDigitalReceiptsAttr();
			
			List list=new ArrayList();
			list.addAll(drAttrSet);
			Collections.sort(list);
			
			//drStrArr = drAttrSet.toArray(new String[0]);
			
			for(Object digiKey : list){
				
				if(digiKey.toString().startsWith("CreditCard")){
					continue;
				}
				
				Comboitem comboItem = new Comboitem();
				comboItem.setLabel(digiKey.toString());
				drCombobox.appendChild(comboItem);
				
			}
			//drCombobox.setSelectedIndex(0);   
					
			/*List list=new ArrayList();
			list.addAll(drAttrSet);
			Collections.sort(list);
			ListModelList lm2 = new ListModelList(list);
			drCombobox.setModel(lm2);
			drCombobox.setMold("rounded");
			drCombobox.setAutodrop(true);
			drCombobox.addEventListener("onChanging", this);*/
	        
			//return drCombobox;	
			
		}
		
		else{
			String digiKeyFieldStr = POSFieldsEnum.findByOCAttribute(ocfield).getDrKeyField();
			
			String[] digiKeyFieldArr = digiKeyFieldStr.split(",");
		
			for(String digiKey : digiKeyFieldArr){
			
				Comboitem comboItem = new Comboitem();
				comboItem.setLabel(digiKey);
				drCombobox.appendChild(comboItem);
				
			}
			//drCombobox.setSelectedIndex(0); 
			
		}
		
		
	}//createDigitalReceiptCombobox
	
	//create SalesGenralField Listbox
		private Listbox createSalePosMappingFieldListBox() {
			Listbox dateFormatListBx = new Listbox();
//			contactGenFieldList = null;
			Listitem tempItem = null;
			//Sales genralFieldList
			for (int i=0; i< genSalesArray.length; i++) {
//				tempStr = (String)contactGenFieldList.get(i);
				tempItem = new Listitem(genSalesArray[i]);
				tempItem.setParent(dateFormatListBx);
			}
			
			//UDF FieldList
			for(int i=0; i<udfSetStr.length; i++) { 
				tempItem = new Listitem(udfSetStr[i]);
				tempItem.setParent(dateFormatListBx);
			}
			dateFormatListBx.setMold("select");
			return dateFormatListBx;
		}
		
		
		private Listbox createSkuPosMappingFieldListBox() {
			
			Listbox dateFormatListBx = new Listbox();
			Listitem tempItem = null;
			
			//SKU genralFieldList
			for (int i=0; i< genSkuArr.length; i++) {
//				tempStr = (String)contactGenFieldList.get(i);
				tempItem = new Listitem(genSkuArr[i]);
				tempItem.setParent(dateFormatListBx);
			}
			
			//UDF FieldList
			for(int i=0; i<udfSetStr.length; i++) { 
				tempItem = new Listitem(udfSetStr[i]);
				tempItem.setParent(dateFormatListBx);
			}
			dateFormatListBx.setMold("select");
			return dateFormatListBx;
		}
		
		//posMapBackBtnId,saveContinueBtnId
		public void onClick$addContactPosMapBtnId() {
			logger.debug("Users ::"+users);
			logger.debug("posML ::"+posML);
			if(users == null) {
				MessageUtil.setMessage("Please select user.","color:red","TOP");
				return;
			}else if(posML == null) {
				MessageUtil.setMessage("Please create POS type of mailing list.","color:red","TOP");
				return;
			}
			
			Row tempRow = new Row();
			
			Listbox tempListbox = null;
			
			//Custom Field
			tempListbox = createContactPosMappingListbox();
			tempListbox.setAttribute("CUST_FIELD_LISTBOX", "CUST_FIELD_LISTBOX");
			tempListbox.addEventListener("onSelect", this);
			tempListbox.setSelectedIndex(16);
			tempListbox.setParent(tempRow);
			
			Listbox dataTypelb = createDataTypeListBox();
			dataTypelb.setSelectedIndex(0);
			dataTypelb.setAttribute("DATATYPE_LISTBOX", "DATATYPE_LISTBOX");
			dataTypelb.addEventListener("onSelect", this);
			
			dataTypelb.setParent(tempRow);
			
			
			
			Textbox tempTextBox;
			
			//POS Attr
			//tempTextBox = new Textbox();
			//tempTextBox.setParent(tempRow);
			
			Div posTypeDiv = new Div();
			//pos textbox
			Textbox posAttrTextBx = new Textbox();
			posAttrTextBx.setParent(posTypeDiv);
			posAttrTextBx.setWidth("130px");
			//String dataTypeStr = posMapping.getDataType();
			//pos dateformat
			Listbox dateFormatListBx = createDateFormatListbox();
			dateFormatListBx.setParent(posTypeDiv);
			dateFormatListBx.setStyle("margin: 0 2px 0 14px;");
			dateFormatListBx.setVisible(false);
			dateFormatListBx.setSelectedIndex(0);
			
			//dataTypelb.addEventListener("onSelect", this);
			posTypeDiv.setParent(tempRow);
			
			
			//Combobox drComboBox = createDigitalReceiptCombobox();
			
			Div drTypeDiv = new Div();
			//dr
			String mappingType = "Contacts";
			Combobox drComboBox = new Combobox();
					createDigitalReceiptCombobox(drComboBox, tempListbox.getSelectedItem().getLabel(), mappingType);
			drComboBox.setClass("cb_150w");
			drComboBox.setParent(drTypeDiv);
			
			//dr datetype
			Listbox drdateFormatListBx = createDateFormatListbox();
			drdateFormatListBx.setParent(drTypeDiv);
			drdateFormatListBx.setStyle("margin: 0 2px 0 14px;");
			drdateFormatListBx.setVisible(false);
			drdateFormatListBx.setSelectedIndex(0);
			
			//dataTypelb.addEventListener("onSelect", this);
			drTypeDiv.setParent(tempRow);
			
			
			if(isAdmin){
				
				drComboBox.setDisabled(false);
				drdateFormatListBx.setDisabled(false);
				
			}
			else{
				drComboBox.setDisabled(true);
				drdateFormatListBx.setDisabled(true);
			}
			/*//DataType
			Div tempDiv = new Div();
			
			//Data Type
			tempListbox = createDataTypeListBox();
			tempListbox.addEventListener("onSelect", this);
			tempListbox.setSelectedIndex(0);
			tempListbox.setParent(tempDiv);
			//Date Format
			tempListbox = createDateFormatListbox();
			tempListbox.setSelectedIndex(0);
			tempListbox.setVisible(false);
			tempListbox.setParent(tempDiv);
			
			tempDiv.setParent(tempRow);*/
			
			
			
			// Optional Value

			Div optDiv=new Div();
			optDiv.setParent(tempRow);
			
			Combobox cb = new Combobox();
			cb.setSclass("cb_100w");
			cb.setParent(optDiv);
			
			
			//Optional Value Add Action
			Image optAddValImg = new Image();
			optAddValImg.setSrc("/img/action_add.jpg");
			optAddValImg.setStyle("cursor:pointer;margin:0 5px 0 15px;");
			optAddValImg.addEventListener("onClick", this);
			optAddValImg.setAttribute("TYPE", "POS_ADD_OPTIONAL_VALUE");
			optAddValImg.setParent(optDiv);			
			
			//Optional Value Add Action
			Image optDelValImg = new Image();
			optDelValImg.setSrc("/img/action_delete.gif");
			optDelValImg.setStyle("cursor:pointer;");
			optDelValImg.addEventListener("onClick", this);
			optDelValImg.setAttribute("TYPE", "POS_DEL_OPTIONAL_VALUE");
			optDelValImg.setParent(optDiv);			
			
			//Display Label
			tempTextBox = new Textbox();
			tempTextBox.setWidth("120px");
			tempTextBox.setParent(tempRow);
			
//			Unique priority
			Intbox priIntBx = new Intbox();
			priIntBx.setMaxlength(1);
			priIntBx.setWidth("30px");
			
			priIntBx.setDisabled(true);
			if(isAdmin) {
				priIntBx.setDisabled(false);
			}
			
			priIntBx.setParent(tempRow);
			
			Div div=new Div();
			div.setParent(tempRow);

			//Delete Action
			Image delImg = new Image();
			delImg.setAttribute("TYPE", "POS_CONTACT_MAPPING");
			delImg.setTooltiptext("Delete");
			delImg.setSrc("/images/action_delete.gif");
			delImg.setStyle("cursor:pointer;");
			delImg.addEventListener("onClick", this);
			delImg.setParent(div);
			
			
			tempRow.setParent(contactRowsId);
			
			
		}
		
		public void onClick$addSalesPosMapBtnId() {
			
			if(users == null) {
				MessageUtil.setMessage("Please select user.","color:red","TOP");
				return;
			}else if(posML == null) {
				MessageUtil.setMessage("Please create POS type of mailing list.","color:red","TOP");
				return;
			}
			
			Row tempRow = new Row();
			
			Listbox tempListbox = null;
			
			
			//Custom Field
			tempListbox = createSalePosMappingFieldListBox();
			tempListbox.setAttribute("CUST_FIELD_LISTBOX", "CUST_FIELD_LISTBOX");
			tempListbox.addEventListener("onSelect", this);
			//tempListbox.setSelectedIndex(12);
			tempListbox.setSelectedIndex(13);
			tempListbox.setParent(tempRow);
			
			
			Listbox dataTypelb = createDataTypeListBox();
			dataTypelb.setSelectedIndex(0);
			dataTypelb.setAttribute("DATATYPE_LISTBOX", "DATATYPE_LISTBOX");
			dataTypelb.addEventListener("onSelect", this);
			
			dataTypelb.setParent(tempRow);
			
			
			
			Textbox tempTextBox;
			
			//POS Attr
			//tempTextBox = new Textbox();
			//tempTextBox.setParent(tempRow);
			
			Div posTypeDiv = new Div();
			//pos textbox
			Textbox posAttrTextBx = new Textbox();
			posAttrTextBx.setParent(posTypeDiv);
			posAttrTextBx.setWidth("130px");
			//String dataTypeStr = posMapping.getDataType();
			//pos dateformat
			Listbox dateFormatListBx = createDateFormatListbox();
			dateFormatListBx.setParent(posTypeDiv);
			dateFormatListBx.setStyle("margin: 0 2px 0 14px;");
			dateFormatListBx.setVisible(false);
			dateFormatListBx.setSelectedIndex(0);
			
			//dataTypelb.addEventListener("onSelect", this);
			posTypeDiv.setParent(tempRow);
			
			
			//Combobox drComboBox = createDigitalReceiptCombobox();
			
			Div drTypeDiv = new Div();
			//dr
			Combobox drComboBox = new Combobox();
					createDigitalReceiptCombobox(drComboBox, tempListbox.getSelectedItem().getLabel(), null);
			drComboBox.setClass("cb_150w");
			drComboBox.setParent(drTypeDiv);
			
			//dr datetype
			Listbox drdateFormatListBx = createDateFormatListbox();
			drdateFormatListBx.setParent(drTypeDiv);
			dateFormatListBx.setStyle("margin: 0 2px 0 14px;");
			drdateFormatListBx.setSelectedIndex(0);
			drdateFormatListBx.setVisible(false);
			
			//dataTypelb.addEventListener("onSelect", this);
			drTypeDiv.setParent(tempRow);
			
			
			if(isAdmin){
				
				drComboBox.setDisabled(false);
				drdateFormatListBx.setDisabled(false);
				
			}
			else{
				drComboBox.setDisabled(true);
				drdateFormatListBx.setDisabled(true);
			}
			
		/*	
			//dr
			Combobox drComboBox = new Combobox(); 
					createDigitalReceiptCombobox(drComboBox, tempListbox.getSelectedItem().getLabel());
			drComboBox.setClass("cb_160w");
			drComboBox.setParent(tempRow);
			
			
			
			//DataType
			Div tempDiv = new Div();
			
			//Data Type
			tempListbox = createDataTypeListBox();
			tempListbox.addEventListener("onSelect", this);
			tempListbox.setSelectedIndex(0);
			tempListbox.setParent(tempDiv);
			//Date Format
			tempListbox = createDateFormatListbox();
			tempListbox.setSelectedIndex(0);
			tempListbox.setVisible(false);
			tempListbox.setParent(tempDiv);
			
			tempDiv.setParent(tempRow);
			*/
						
			// Optional Value

			Div optDiv=new Div();
			optDiv.setParent(tempRow);
			
			Combobox cb = new Combobox();
			cb.setSclass("cb_100w");
			cb.setParent(optDiv);
			
			
			//Optional Value Add Action
			Image optAddValImg = new Image();
			optAddValImg.setSrc("/img/action_add.jpg");
			optAddValImg.setStyle("cursor:pointer;margin:0 5px 0 15px;");
			optAddValImg.addEventListener("onClick", this);
			optAddValImg.setAttribute("TYPE", "POS_ADD_OPTIONAL_VALUE");
			optAddValImg.setParent(optDiv);			
			
			//Optional Value Add Action
			Image optDelValImg = new Image();
			optDelValImg.setSrc("/img/action_delete.gif");
			optDelValImg.setStyle("cursor:pointer;");
			optDelValImg.addEventListener("onClick", this);
			optDelValImg.setAttribute("TYPE", "POS_DEL_OPTIONAL_VALUE");
			optDelValImg.setParent(optDiv);			
			
			//Display Label
			tempTextBox = new Textbox();
			tempTextBox.setWidth("120px");
			tempTextBox.setParent(tempRow);
			
//			Unique priority
			Intbox priIntBx = new Intbox();
			priIntBx.setMaxlength(1);
			priIntBx.setWidth("30px");
			
			priIntBx.setDisabled(true);
			if(isAdmin) {
				priIntBx.setDisabled(false);
			}
			priIntBx.setParent(tempRow);
			
			Div div=new Div();
			div.setParent(tempRow);

			//Delete Action
			Image delImg = new Image();
			delImg.setAttribute("TYPE", "POS_SALES_MAPPING");
			delImg.setSrc("/images/action_delete.gif");
			delImg.setTooltiptext("Delete");
			delImg.setStyle("cursor:pointer;");
			delImg.addEventListener("onClick", this);
			delImg.setParent(div);			

			
			tempRow.setParent(salesRowsId);
			
			
		}
		
		public void onClick$addSKUPosMapBtnId() {
			
			if(users == null) {
				MessageUtil.setMessage("Please select user.","color:red","TOP");
				return;
			}else if(posML == null) {
				MessageUtil.setMessage("Please create POS type of mailing list.","color:red","TOP");
				return;
			}
			
			Row tempRow = new Row();
			
			Listbox tempListbox = null;
			
			
			//Custom Field
			tempListbox = createSkuPosMappingFieldListBox();
			tempListbox.setSelectedIndex(6);
			tempListbox.setAttribute("CUST_FIELD_LISTBOX", "CUST_FIELD_LISTBOX");
			tempListbox.addEventListener("onSelect", this);
			tempListbox.setParent(tempRow);
			
			Listbox dataTypelb = createDataTypeListBox();
			dataTypelb.setSelectedIndex(0);
			dataTypelb.setAttribute("DATATYPE_LISTBOX", "DATATYPE_LISTBOX");
			dataTypelb.addEventListener("onSelect", this);
			
			dataTypelb.setParent(tempRow);
			
			Textbox tempTextBox;
			
			//POS Attr
			//tempTextBox = new Textbox();
			//tempTextBox.setParent(tempRow);
			
			
			
			Div posTypeDiv = new Div();
			//pos textbox
			Textbox posAttrTextBx = new Textbox();
			posAttrTextBx.setParent(posTypeDiv);
			posAttrTextBx.setWidth("130px");
			//String dataTypeStr = posMapping.getDataType();
			//pos dateformat
			Listbox dateFormatListBx = createDateFormatListbox();
			dateFormatListBx.setParent(posTypeDiv);
			dateFormatListBx.setStyle("margin: 0 2px 0 14px;");
			dateFormatListBx.setVisible(false);
			dateFormatListBx.setSelectedIndex(0);
			
			//dataTypelb.addEventListener("onSelect", this);
			posTypeDiv.setParent(tempRow);
			
			
			//Combobox drComboBox = createDigitalReceiptCombobox();
			
			Div drTypeDiv = new Div();
			//dr
			Combobox drComboBox = new Combobox();
					createDigitalReceiptCombobox(drComboBox, tempListbox.getSelectedItem().getLabel(), null);
			drComboBox.setClass("cb_150w");
			drComboBox.setParent(drTypeDiv);
			
			//dr datetype
			Listbox drdateFormatListBx = createDateFormatListbox();
			drdateFormatListBx.setParent(drTypeDiv);
			dateFormatListBx.setStyle("margin: 0 2px 0 14px;");
			drdateFormatListBx.setVisible(false);
			drdateFormatListBx.setSelectedIndex(0);
			
			//dataTypelb.addEventListener("onSelect", this);
			drTypeDiv.setParent(tempRow);
			
			
			if(isAdmin){
				
				drComboBox.setDisabled(false);
				drdateFormatListBx.setDisabled(false);
				
			}
			else{
				drComboBox.setDisabled(true);
				drdateFormatListBx.setDisabled(true);
			}
			
			
			/*//dr
			Combobox drComboBox = new Combobox(); 
					createDigitalReceiptCombobox(drComboBox, tempListbox.getSelectedItem().getLabel());
			drComboBox.setClass("cb_160w");
			drComboBox.setParent(tempRow);
			
			
			
			
			//DataType
			Div tempDiv = new Div();
			
			//Data Type
			tempListbox = createDataTypeListBox();
			tempListbox.addEventListener("onSelect", this);
			tempListbox.setSelectedIndex(0);
			tempListbox.setParent(tempDiv);
			//Date Format
			tempListbox = createDateFormatListbox();
			tempListbox.setSelectedIndex(0);
			tempListbox.setVisible(false);
			tempListbox.setParent(tempDiv);
			
			tempDiv.setParent(tempRow);*/
			
			
			// Optional Value

			Div optDiv=new Div();
			optDiv.setParent(tempRow);
			
			Combobox cb = new Combobox();
			cb.setSclass("cb_100w");
			cb.setParent(optDiv);
			
			
			//Optional Value Add Action
			Image optAddValImg = new Image();
			optAddValImg.setSrc("/img/action_add.jpg");
			optAddValImg.setStyle("cursor:pointer;margin:0 5px 0 15px;");
			optAddValImg.addEventListener("onClick", this);
			optAddValImg.setAttribute("TYPE", "POS_ADD_OPTIONAL_VALUE");
			optAddValImg.setParent(optDiv);			
			
			//Optional Value Add Action
			Image optDelValImg = new Image();
			optDelValImg.setSrc("/img/action_delete.gif");
			optDelValImg.setStyle("cursor:pointer;");
			optDelValImg.addEventListener("onClick", this);
			optDelValImg.setAttribute("TYPE", "POS_DEL_OPTIONAL_VALUE");
			optDelValImg.setParent(optDiv);			
			
			//Display Label
			tempTextBox = new Textbox();
			tempTextBox.setWidth("120px");
			tempTextBox.setParent(tempRow);
			
//			Unique priority
			Intbox priIntBx = new Intbox();
			priIntBx.setMaxlength(1);
			priIntBx.setWidth("30px");
			
			priIntBx.setDisabled(true);
			if(isAdmin) {
				priIntBx.setDisabled(false);
			}
			priIntBx.setParent(tempRow);
			
			Div div=new Div();
			div.setParent(tempRow);

			//Delete Action
			Image delImg = new Image();
			delImg.setAttribute("TYPE", "POS_SKU_MAPPING");
			delImg.setSrc("/images/action_delete.gif");
			delImg.setStyle("cursor:pointer;");
			delImg.setTooltiptext("Delete");
			delImg.addEventListener("onClick", this);
			delImg.setParent(div);			

			tempRow.setParent(skuRowsId);
		}
		
		public void onClick$addMoreEmailTBId(){
			
			Div alertDiv = new Div();
			Textbox alertTextBx = new Textbox();
			alertTextBx.setParent(alertDiv);
			alertTextBx.setWidth("250px");
			alertTextBx.setStyle("margin-left:85px;margin-top: 10px;margin-right:7px ;");
			
			Image delImg = new Image();
			delImg.setAttribute("TYPE", "ALERT_DEL");
			delImg.setSrc("/images/action_delete.gif");
			delImg.setStyle("cursor:pointer;color:#2886B9;font-weight:bold;text-decoration: underline;");
			delImg.setTooltiptext("Delete");
			delImg.addEventListener("onClick", this);
			delImg.setParent(alertDiv);
			
			alertDiv.setParent(alertMailDivId);
		}
		public void onClick$posMapBackBtnId() {
			gotoStep(1);
		} //onClick$posMapBackBtnId()
		
		public void onClick$posFTPSettingsBackBtnId() {
			gotoStep(2);
		} //onClick$posMapBackBtnId()
		
	//Following method is added to save the POSMapping and remain on the same step for any further modifications  and saving.	
		public void onClick$saveBtnId() {
			
			try {
				
				int confirm = Messagebox.show("Are you sure you want to save the POS mapping?", 
						"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				
				if(confirm == Messagebox.OK) {
					
					try {
						//scopeDrfieldList.clear();
						scopeCustfieldList.clear();
						scopePosAttrList.clear();
						scopeDispLblList.clear();
						
						count++;
						// Validate Contact POS Mapping
						List contactRowChildList =  contactRowsId.getChildren();
						
						if(contactRowChildList != null && contactRowChildList.size() >0) {
							
							if(validatePOSMappingColl(contactRowChildList, Constants.POS_MAPPING_TYPE_CONTACTS) == false) {
								return;
							}
						}
						// Validate Sales POS Mapping
						List salesRowChildList = salesRowsId.getChildren();
						
						if(salesRowChildList != null && salesRowChildList.size() >0) {
							if(validatePOSMappingColl(salesRowChildList, Constants.POS_MAPPING_TYPE_SALES) == false) {
								return;
							}
						}
						// Validate SKU POS Mapping
						List skuRowChildList = skuRowsId.getChildren();
						
						if(skuRowChildList != null && skuRowChildList.size() >0) {
							if(validatePOSMappingColl(skuRowChildList, Constants.POS_MAPPING_TYPE_SKU) == false) {
								return;
							}
						}
						
						/*if((issuanceChkBxId.isChecked() || redemptionChkBxId.isChecked()) && redeemTenderListBxId.getSelectedItem().getValue()==null) {
							MessageUtil.setMessage("Please select a redemption tender.","color:red", "TOP");
							return;
						}else*/
						if(redemptionTenderTbId.isVisible() && redemptionTenderTbId.getValue().trim().equalsIgnoreCase("")) {
							MessageUtil.setMessage("Please enter a redemption tender type.","color:red", "TOP");
							return;
						}
						
						savePOSMapping();
						
						MessageUtil.setMessage("POS mapping saved successfully.", "green", "TOP");
						
						
						clearAndDefaultPOSMappSettings();
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception  ::", e);
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception  ::", e);
			}
			
		}//onClick$saveBtnId()
		
		//This method is added to just move to FTPPOSsettings step without any saving of POSMapping.
		public void onClick$nxtBtnId() {
		
			if(isAdmin){
				
				try {
					gotoStep(3);
					/*mastersPosSettings();*/
					defaultFTPSettings();
					salesDataAlertSetting();
					mastersSettings();
					prepareRelationMappingData();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception  ::", e);
				}
			}
			
		}//onClick$nxtBtnId()
		
		//contactRowsId,salesRowsId,skuRowsId,
		public void onClick$saveContinueBtnId() {
			
			try {
				
				int confirm = Messagebox.show("Are you sure you want to save the POS mapping?", 
						"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				
				if(confirm == Messagebox.OK) {
					
					try {
						//scopeDrfieldList.clear();
						scopeCustfieldList.clear();
						scopePosAttrList.clear();
						scopeDispLblList.clear();
						
						
						// Validate Contact POS Mapping
						List contactRowChildList =  contactRowsId.getChildren();
						
						if(contactRowChildList != null && contactRowChildList.size() >0) {
							
							if(validatePOSMappingColl(contactRowChildList, Constants.POS_MAPPING_TYPE_CONTACTS) == false) {
								return;
							}
						}
						// Validate Sales POS Mapping
						List salesRowChildList = salesRowsId.getChildren();
						
						if(salesRowChildList != null && salesRowChildList.size() >0) {
							if(validatePOSMappingColl(salesRowChildList, Constants.POS_MAPPING_TYPE_SALES) == false) {
								return;
							}
						}
						// Validate SKU POS Mapping
						List skuRowChildList = skuRowsId.getChildren();
						
						if(skuRowChildList != null && skuRowChildList.size() >0) {
							if(validatePOSMappingColl(skuRowChildList, Constants.POS_MAPPING_TYPE_SKU) == false) {
								return;
							}
						}
						
						/*if((issuanceChkBxId.isChecked() || redemptionChkBxId.isChecked()) && redeemTenderListBxId.getSelectedItem().getValue()==null) {
							MessageUtil.setMessage("Please select a redemption tender.","color:red", "TOP");
							return;
						}else */
						if(redemptionTenderTbId.isVisible() && redemptionTenderTbId.getValue().trim().equalsIgnoreCase("")) {
							MessageUtil.setMessage("Please enter a redemption tender type.","color:red", "TOP");
							return;
						}
						
						savePOSMapping();
						
						MessageUtil.setMessage("POS mapping saved successfully.", "green", "TOP");
						
						
						clearAndDefaultPOSMappSettings();
						if(isAdmin){
							
							try {
								gotoStep(3);
								/*mastersPosSettings();*/
								defaultFTPSettings();
								mastersSettings();
								prepareRelationMappingData();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								logger.error("Exception  ::", e);
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception  ::", e);
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception  ::", e);
			}
			
		} //onClick$saveContinueBtnId()
		
/*public void onClick$saveContinueBtnId() {
			
			scopeCustfieldList.clear();
			scopePosAttrList.clear();
			
			//Contact POS Mapping
			List contactRowChildList =  contactRowsId.getChildren();
			
			if(contactRowChildList != null && contactRowChildList.size() >0) {
				
				if(validatePOSMappingColl(contactRowChildList, "Contacts") == false) {
					return;
				}
			}
			//Sales POS Mapping
			List salesRowChildList = salesRowsId.getChildren();
			
			if(salesRowChildList != null && salesRowChildList.size() >0) {
				if(validatePOSMappingColl(salesRowChildList, "Sales") == false) {
					return;
				}
			}
			//SKU POS Mapping
			List skuRowChildList = skuRowsId.getChildren();
			
			if(skuRowChildList != null && skuRowChildList.size() >0) {
				if(validatePOSMappingColl(skuRowChildList, "SKU") == false) {
					return;
				}
			}
			
			savePOSMapping();
			MessageUtil.setMessage("POS mapping saved successfully. ", "green", "TOP");
			
			clearAndDefaultPOSMappSettings();
			if(isAdmin){
				
				gotoStep(3);
			}
			
		} //onClick$saveContinueBtnId()
		*/
		
		
		private void clearAndDefaultPOSMappSettings() {
			Components.removeAllChildren(contactRowsId); 
			Components.removeAllChildren(salesRowsId); 
			Components.removeAllChildren(skuRowsId); 
			deafultPOSMappingSettings();
		}

		private boolean validatePOSMappingColl(List tempList,String mappingType) {
			//logger.info("entered into validate pos mapping column:>>>");
			
			Set<String> drAttrSet = Utility.getDigitalReceiptsAttr();
			
			List drlist=new ArrayList();
			drlist.addAll(drAttrSet);
			Collections.sort(drlist);
			
			ArrayList<String> arrListOfDrComboBxItems = new ArrayList<String>();
			
			for (Object object : tempList) {
				Row temRow = (Row)object;
				
//				POSMapping posMapping = null;
				List chaildLis = temRow.getChildren();
				
				//DR attribute
				Div drDiv = (Div)chaildLis.get(3);
				Div posDiv = (Div)chaildLis.get(2);
				
				Combobox combobox = (Combobox)drDiv.getChildren().get(0);
				String drComboText = "";
				if(combobox.getSelectedItem() == null){
					drComboText = combobox.getText().trim();
				}
				else{
					drComboText = combobox.getSelectedItem().getLabel().trim();
					if(!drComboText.equals("")) arrListOfDrComboBxItems.add(drComboText);
				}
				/*if(drComboText.trim().length() <= 0){
					MessageUtil.setMessage("Provide "+mappingType+" Digital Receipt attribute. ", "color:red", "TOP");
					//return false;
				}*/
				
				//POS Attribute
				Textbox posAttrbuteTextbox = (Textbox) posDiv.getChildren().get(0);
				//Textbox posAttrbuteTextbox1 = (Textbox)drDiv.getChildren().get(0);
				combobox.setStyle("border:1px solid #7F9DB9;");
				posAttrbuteTextbox.setStyle("border:1px solid #7F9DB9;");
				
				//Display label
				Textbox displayTextbox = (Textbox) chaildLis.get(5);
				displayTextbox.setStyle("border:1px solid #7F9DB9;");
				
				//Uniq Prio label
				Intbox uniqPriIntbox = (Intbox) chaildLis.get(6);
				uniqPriIntbox.setStyle("border:1px solid #7F9DB9;");
				
				if(posAttrbuteTextbox.getValue().trim().equals("")) {
					logger.debug("POs fieldData is eampty");
					posAttrbuteTextbox.setStyle("border:1px solid #DD7870;");
					
					MessageUtil.setMessage("Provide "+mappingType+" POS attribute. ", "color:red", "TOP");
					return false;
				}
				else if(isAdmin && drComboText !=null && drComboText.trim().length() > 0){
						
						if(!drlist.contains(drComboText)){
							logger.debug("DR json key field data is empty");
							MessageUtil.setMessage(mappingType+" Digital Receipt Attribute: "+drComboText+" Not Available ", "color:red", "TOP");
							return false;
						}
					
				}
				else if(displayTextbox.getValue().trim().equals("")) {
					displayTextbox.setStyle("border:1px solid #DD7870;");
					logger.debug("Custom fieldData is eampty");
					MessageUtil.setMessage("Provide "+ mappingType +" Display label. ", "color:red", "TOP");
					return false;
				}
				else {
					try {
						if(uniqPriIntbox.getValue()!=null && uniqPriIntbox.getValue()<=0) {
							uniqPriIntbox.setStyle("border:1px solid #DD7870;");
							logger.debug("Invalid unique priority eampty");
							MessageUtil.setMessage("Provide "+ mappingType +" valid Unique Priority value. ", "color:red", "TOP");
							return false;
						}
					} catch (Exception e) {
						uniqPriIntbox.setStyle("border:1px solid #DD7870;");
						logger.debug("Invalid unique priority eampty");
						MessageUtil.setMessage("Provide "+ mappingType +" valid Unique Priority value. ", "color:red", "TOP");
						return false;
					}
				}
				
				
				
				//optculture field
				Listbox custFieldListbx = (Listbox)chaildLis.get(0);
				String scopeCustFieldStr = mappingType+"_"+ custFieldListbx.getSelectedItem().getLabel();
				String scopeDRStr = mappingType+"_"+drComboText;
				String scopePosStr = mappingType+"_"+posAttrbuteTextbox.getValue();
				String scopeDispStr = mappingType+"_"+displayTextbox.getValue();
				
				if(scopeCustfieldList.contains(scopeCustFieldStr) ) {
					MessageUtil.setMessage(mappingType +"  Optculture Field  "+ custFieldListbx.getSelectedItem().getLabel() +" already mapped.","color:red","TOP");
					return false;
				}else if(scopePosAttrList.contains(scopePosStr)) {
					MessageUtil.setMessage(mappingType +"  POS Attribute "+ posAttrbuteTextbox.getValue() +" already mapped.","color:red","TOP");
					return false;
				}else if(scopeDispLblList.contains(scopeDispStr)){
					MessageUtil.setMessage(mappingType +"  POS DispLay Label "+ displayTextbox.getValue() +" already mapped.","color:red","TOP");
					return false;
				}
				scopeCustfieldList.add(scopeCustFieldStr);
				scopePosAttrList.add(scopePosStr);
				//scopeDrfieldList.add(scopeDRStr);
				scopeDispLblList.add(scopeDispStr);
			} //for
			
			Collections.sort(arrListOfDrComboBxItems);
			String prevComboBxItem = null;
			
			logger.info("size of arrListOfDrComboBxItems == "+arrListOfDrComboBxItems.size());
			logger.info("arrListOfDrComboBxItems == "+arrListOfDrComboBxItems);
			
			for(String currentComboBxItem : arrListOfDrComboBxItems){
				if(prevComboBxItem != null && currentComboBxItem.equals(prevComboBxItem)) {
					MessageUtil.setMessage("Same digital receipt attribute can't be mapped multiple times in POS "+mappingType+" Field Mappings.","color:red","TOP");
					return false;
				}
				prevComboBxItem = currentComboBxItem;
			}
			return true;
			
		} //validatePOSMappingColl
		
		private void savePOSMapping() {
			
			List contactRowChildList =  contactRowsId.getChildren();
			
			if(contactRowChildList != null && contactRowChildList.size() > 0) {
				savePOSMappingByType(contactRowChildList ,  Constants.POS_MAPPING_TYPE_CONTACTS);
			}
			//Sales POS Mapping
			List salesRowChildList = salesRowsId.getChildren();
			if(salesRowChildList != null && salesRowChildList.size() > 0) {
				savePOSMappingByType(salesRowChildList ,  Constants.POS_MAPPING_TYPE_SALES);
			}
			
			//SKU POS Mapping
			List skuRowChildList = skuRowsId.getChildren();
			if(skuRowChildList != null && skuRowChildList.size() > 0) {
				savePOSMappingByType(skuRowChildList ,  Constants.POS_MAPPING_TYPE_SKU);
			}
			String template=SMSMsgTbId.getValue();
			logger.debug(template);
			if(!template.isEmpty())
			{
				users.getUserOrganization().setLoyaltyDisplayTemplate(template);
			}
			else {
				String template1=null;
				users.getUserOrganization().setLoyaltyDisplayTemplate(template1);
			}
			
			//DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(users.getUserId());
			//digitalReceiptUserSettings.setDataExtractionFlag(dataExtractionChkBxId.isChecked());
			//digitalReceiptUserSettingsDao.saveOrUpdate(digitalReceiptUserSettings);

			//Changing old jsons to Mark as ignored
			boolean isDREnabled =users.isDigitalReceiptExtraction();
			logger.info("dr extraction previously "+isDREnabled);
			if(!isDREnabled && dataExtractionChkBxId.isChecked()) {
				digitalReceiptsJSONDaoForDML.updateIgnoredStatus(OCConstants.DR_STATUS_IGNORED,users.getUserId());
				logger.info("marking Ignored");
				
			}
			users.setDigitalReceiptExtraction(dataExtractionChkBxId.isChecked());
			users.setShowOnlyHighestDiscReceiptDC(SendAllDiscountChkBxId.isChecked());
			
			/** Zonewise setting 
			 * */
			users.setZoneWise(zoneWiseChkBxId.isChecked());
			
			// loyalty extraction from DR
			users.setEnableLoyaltyExtraction(extractLoyaltyChkBxId.isChecked());
			users.setEnablePromoRedemption(redeemPromoChkBxId.isChecked());
			users.setReceiptNoteUsed(redeemPromoChkBxId.isChecked() && receiptNoteLbl.isVisible() ? receiptNoteListBxId.getSelectedItem().getValue():null);
			users.setItemNoteUsed(redeemPromoChkBxId.isChecked() && itemNoteLbl.isVisible() ? itemNoteListBxId.getSelectedItem().getValue():null);
			users.setItemInfo(redeemPromoChkBxId.isChecked() && itemInfoLbl.isVisible() ? itemInfoFieldListBxId.getSelectedItem().getValue():null);
			users.setValidateItemsInReturnTrx(validateItemsInReturnTrx.isChecked());
			users.setIgnorePointsRedemption(ignorePointsRedemption.isChecked());
			
			users.setEnrollFromDR(extractLoyaltyChkBxId.isChecked() ? enrolChkBxId.isChecked() : false);
			users.setIssuanceFromDR(extractLoyaltyChkBxId.isChecked() ? issuanceChkBxId.isChecked() : false);
			users.setReturnFromDR(extractLoyaltyChkBxId.isChecked() ? returnChkBxId.isChecked() : false);
			users.setPerformRedeemedAmountReversal(extractLoyaltyChkBxId.isChecked() ? redemptionReversalChkBxId.isChecked() : false);
			users.setNonInventoryItem(redeemAsListBxId.getSelectedItem()!=null && redeemAsListBxId.getSelectedItem().getValue()!=null
					&& redeemAsListBxId.getSelectedItem().getValue().equals(NonInventoryItem)?
					ninItemFieldListBxId.getSelectedItem().getValue().toString()+Constants.DELIMETER_COLON+ninItemFieldTbId.getValue():null);
			users.setRedeemTender(redemptionTenderTbId.isVisible() && redeemAsListBxId.getSelectedItem()!=null && redeemAsListBxId.getSelectedItem().getValue()!=null
					&& redeemAsListBxId.getSelectedItem().getValue().equals(Tender)? 
					redeemTenderListBxId.getSelectedItem().getValue().toString()+Constants.DELIMETER_COLON+redemptionTenderTbId.getValue().toUpperCase() :
						((redeemTenderListBxId.isVisible() && redeemTenderListBxId.getSelectedItem().getValue()!=null) 
								? redeemTenderListBxId.getSelectedItem().getValue() : null));
			//users.setRedeemTenderDispLabel(redemptionTenderDispLblTbId.isVisible()? redemptionTenderDispLblTbId.getValue():null);
			users.setRedeemTenderDispLabel(redemptionTenderDispLblTbId.isVisible()&&!redemptionTenderDispLblTbId.getValue().isEmpty()? 
					redemptionTenderDispLblTbId.getValue():null);
			/*users.setRedemptionFromDR(extractLoyaltyChkBxId.isChecked() ? redemptionChkBxId.isChecked() && 
					redeemTenderListBxId.getSelectedItem().getValue()!=null :false);*/
			users.setRedemptionFromDR(extractLoyaltyChkBxId.isChecked() ? redemptionChkBxId.isChecked() :false);
			users.setIgnoretrxUpOnExtraction(extractLoyaltyChkBxId.isChecked() || redeemPromoChkBxId.isChecked() ? IgnChkBxId.isChecked():false);
		
			//users.setRedeemTender(redemptionTenderTbId.getText());
			users.getUserOrganization().setSendRealtimeLoyaltyStatus(sendRealtimeLoyaltyStatus.isChecked());
			users.setReceiptOnSMS(eReceiptOverSMS.isChecked());
			users.setAllowBothDiscounts(AllowBothDiscounts.isChecked());
			users.setRedemptionAsDiscount(RedeemAsDiscount.isChecked());
			users.setExcludeDiscountedItem(ExcludeDiscountedItem.isChecked());
			users.setShowOnlyHighestLtyDC(HighDiscountedItem.isChecked());
			users.setIgnoreissuanceOnRedemption(ignoreissuanceOnRedemChkBxId.isChecked());
			
			users.setCardInfo(extractLoyaltyChkBxId.isChecked() && cardInfoLbl.isVisible() ? cardInfoListBxId.getSelectedItem().getValue():BillToInfo1);
			
			//usersDao.saveOrUpdate(users);
			usersDaoForDML.saveOrUpdate(users);
			usersDaoForDML.saveOrUpdate(users.getUserOrganization());
			
		} //savePOSMapping
		
		private void savePOSMappingByType(List rowList , String mappingType) {
			
			boolean isObjModified = false;

			if(rowList.size() > 0) {
				
				for (Object object : rowList) {
					
					isObjModified = false;
					
					Row temRow = (Row)object;
					
					POSMapping posMapping = null;
					List chaildLis = temRow.getChildren();
					
					Div posFieldDiv = (Div)chaildLis.get(2);
					Div drFieldDiv = (Div)chaildLis.get(3);
					
					//POS Attribute
					//Textbox posAttrbuteTextbox = (Textbox) chaildLis.get(1);
					Textbox posAttrbuteTextbox = (Textbox) posFieldDiv.getChildren().get(0);
					
					//DR Attribute
					//Combobox drComboBox = (Combobox)chaildLis.get(2);
					Combobox drComboBox = (Combobox)drFieldDiv.getChildren().get(0);
					//String drComboItemStr;
					//if(drComboBox.getSelectedItem() != null){
						//drComboItemStr = drComboBox.getSelectedItem().getLabel().toString();
						//logger.info("drcomboitemstr: "+drComboItemStr);
					//}
					
					//Data Type
					//Div dateFormatDiv = (Div)chaildLis.get(3); 
					
					
					Listbox dataTypeListbox  = (Listbox)chaildLis.get(1);
					String dataTypeStr = dataTypeListbox.getSelectedItem().getLabel();
					if(dataTypeStr.equals("Date")) {
						dataTypeStr = dataTypeStr+"("+((Listbox)posFieldDiv.getChildren().get(1)).getSelectedItem().getLabel()+")";
					}
					
					Listbox drdataTypeListbox  = (Listbox)chaildLis.get(1);
					String drdataTypeStr = drdataTypeListbox.getSelectedItem().getLabel();
					if(drdataTypeStr.equals("Date")) {
						drdataTypeStr = drdataTypeStr+"("+((Listbox)drFieldDiv.getChildren().get(1)).getSelectedItem().getLabel()+")";
					}
					//logger.info("dataTypeStr = "+dataTypeStr);
					//logger.info("drdataTypeStr = "+drdataTypeStr);
					
					//custom field
					Listbox custFielListBx = (Listbox) chaildLis.get(0);
					
					//Display Label
					Textbox  dispLblTextBx = (Textbox) chaildLis.get(5);
					
					//Unique Priority 
					Intbox  uniqPriIntBx = (Intbox) chaildLis.get(6);

					//Optional Values
					Combobox optCombBox = (Combobox) ((Div) chaildLis.get(4)).getFirstChild();
					String optCombStr="";
					
					List<Comboitem> cbItemsList = optCombBox.getItems();
					for (Comboitem cbItem : cbItemsList) {
						if(optCombStr.length()>0) optCombStr += Constants.ADDR_COL_DELIMETER ;
						optCombStr += cbItem.getLabel();
					}
					
					
					
					//String scopeStr,String posAttrStr,String custFieldStr, long userId
					posMapping = (POSMapping)temRow.getAttribute("referenceId");
					//posMapping = posMappingDao.findRecord(scopeLbl.getValue(), posAttrLbl.getValue(), custFieldLbl.getValue(), users.getUserId());
					
					//logger.info("comboselected: "+drComboBox.getText());
					
					String drComboText = "";
					if(drComboBox.getSelectedItem() == null){
						drComboText = drComboBox.getText().trim();
					}
					else{
						drComboText = drComboBox.getSelectedItem().getLabel().trim();
					}
					
					if(drComboText.trim().length() <= 0) drComboText = null;
					//Check if the Fields are modified...
					if(posMapping != null) {
						
						if(!(posMapping.getPosAttribute().trim().equals(posAttrbuteTextbox.getValue().trim()))) {
//							logger.info("Pos Attri old::"+posMapping.getPosAttribute().trim());
							isObjModified = true;
						}
						else if(isAdmin && (posMapping.getDigitalReceiptAttribute() == null || !(posMapping.getDigitalReceiptAttribute().equals(drComboText)))) {
//							logger.info("Pos Attri old::"+posMapping.getPosAttribute().trim());
							isObjModified = true;
						}
						else if(!(posMapping.getCustomFieldName().equals(custFielListBx.getSelectedItem().getLabel().trim()))){
//							logger.info("Pos custField old::"+posMapping.getCustomFieldName().trim());
							isObjModified = true;
						}
						else if(!(posMapping.getDataType().trim().equals(dataTypeStr.trim()))) {
//							logger.info("Pos getDataType old::"+posMapping.getDataType().trim() );
							isObjModified = true;
						}
						else if(isAdmin && (posMapping.getDrDataType() == null || 
								!(posMapping.getDrDataType().trim().equals(drdataTypeStr.trim())))) {
//							logger.info("Pos getDataType old::"+posMapping.getDataType().trim() );
							isObjModified = true;
						}
						else if(!(posMapping.getDisplayLabel().trim().equals(dispLblTextBx.getValue().trim()))) {
							isObjModified = true;
						}
						
						else if(posMapping.getOptionalValues()==null && optCombStr.length()>0) {
							isObjModified = true;
						}
						else if(posMapping.getOptionalValues()!=null && !posMapping.equals(optCombStr)) {
							isObjModified = true;
						}
						
						else if(posMapping.getUniquePriority()!=null && uniqPriIntBx.getValue()!=null && 
								posMapping.getUniquePriority().intValue()!=uniqPriIntBx.intValue()) {
							isObjModified = true;
						}
						else if(posMapping.getUniquePriority()==null && uniqPriIntBx.getValue()!=null) {
							isObjModified = true;
						}
						else if(posMapping.getUniquePriority()!=null && uniqPriIntBx.getValue()==null) {
							isObjModified = true;
						}
						
					}
					
					else if(posMapping == null) {
						posMapping = new POSMapping();
						isObjModified = true;
					}
					
					if(!isObjModified) {
						continue;
					}
					
					
					
					
					//Digital Receipt Attribute
					//posMapping.setDigitalReceiptAttribute(drComboBox.getSelectedItem().getLabel().trim());
					//posMapping.setDigitalReceiptAttribute(drComboText);
					//scope
					posMapping.setMappingType(mappingType.trim());
					//CustomField Name
					posMapping.setCustomFieldName(custFielListBx.getSelectedItem().getLabel());
					//POSAttribute
					posMapping.setPosAttribute(posAttrbuteTextbox.getValue());
					//logger.info("digital receipt combo box :: "+drComboBox.getSelectedItem().getLabel());
					//posMapping.setDigitalReceiptAttribute(drComboBox.getSelectedItem().getLabel());
					//Display Label
					posMapping.setDisplayLabel(dispLblTextBx.getValue());

					// Optional Values 
					if(optCombStr.length()>0) posMapping.setOptionalValues(optCombStr);
					else posMapping.setOptionalValues(null);
					
					//Unique priority Label
					posMapping.setUniquePriority(uniqPriIntBx.getValue());
					
					//Unique across files
					if((drComboText !=null && drComboText.equalsIgnoreCase("Receipt::DocSID")) || custFielListBx.getSelectedItem().getLabel().equals(DOC_SID)){
						posMapping.setUniqueInAcrossFiles(1);
					}
					
					//Data type
					posMapping.setDataType(dataTypeStr);
					
					//drData type
					if(isAdmin){
						posMapping.setDigitalReceiptAttribute(drComboText);
						posMapping.setDrDataType(drdataTypeStr);
					}
					//UserId
					posMapping.setUserId(users.getUserId());
					
					
					//posMappingDao.saveOrUpdate(posMapping);
					posMappingDaoForDML.saveOrUpdate(posMapping);
					
				} //for
				logger.debug("POS mapping saved successfully..");
			}
			
			
			
		}
		
		
		
		
		//FTP Settings
		private void defaultFTPSettings() {

			
			try {
				
				List<UserPosFTPSettings> usersFtpSettings = userPOSFTPSettingsDao.salesSettingsFindByUserId(( (Users) usersListBxId.getSelectedItem().getValue()).getUserId());
				if(usersFtpSettings != null) {
					
							UserPosFTPSettings usersSetting = usersFtpSettings.get(0);
							
							boolean isSalesDataAlert = usersSetting.isCheckAlert();
							if(isSalesDataAlert){
								alertTitleDivId.setVisible(true);
								alertChekbxId.setChecked(true);
							}
							else {
								alertTitleDivId.setVisible(false);
								alertChekbxId.setChecked(false);
							}
				}
				
				//set  the timezone value of user
				String userTimeZoneVal = users.getClientTimeZone();
				if(userTimeZoneVal != null) {
					
					for (Listitem item : TimesZonesLbId.getItems()) {
						
						if(!userTimeZoneVal.equals(item.getValue())) continue;
						
						item.setSelected(true);
						
					}//for
				}
				//String loyaltyType = users.getloyaltyServicetype();
			/*	if(loyaltyType != null) {
					
					for (Listitem loyaltyitem : loyaltyServicetypeLbId.getItems()) {
						
						if(!loyaltyType.equals(loyaltyitem.getValue())) continue;
						
						loyaltyitem.setSelected(true);
						
					}
				}*/
				optSynAuthKeyTbId.setValue("");
				optSynAuthKeyTbId.setReadonly(false);
				generateKeyAnchId.setVisible(true);
				//set user organization OptSync key
				if(users.getUserOrganization().getOptSyncKey() != null &&
						users.getUserOrganization().getOptSyncKey().trim().length() > 0) {
					optSynAuthKeyTbId.setValue(users.getUserOrganization().getOptSyncKey());
					optSynAuthKeyTbId.setReadonly(true);
					generateKeyAnchId.setVisible(false);
				}
				ismultiuserCkbId.setChecked(users.getUserOrganization().isMultiUser());
				isUserSpecDirCkbId.setChecked(users.isSpecificDir());
				
				List<UserPosFTPSettings> userPosSettingsList = userPOSFTPSettingsDao.findByUserId(users.getUserId());
				Components.removeAllChildren(ftpSettingsRowsId);
				
				if(userPosSettingsList == null || userPosSettingsList.size() ==0) return;
				
				for (UserPosFTPSettings userPosFTPSettings : userPosSettingsList) {
					Row row = new Row();
							
					//File Type
					Listbox  fileTypeLstBox = createFieldTypeListBox();
					
					List chaildItemList = fileTypeLstBox.getChildren();
					Listitem  tempItem = null;
					for (Object object : chaildItemList) {
						tempItem = (Listitem)object;
						if(tempItem.getLabel().equals(userPosFTPSettings.getFileType().trim())) {
							
							fileTypeLstBox.setSelectedItem(tempItem);
						}
					}
					fileTypeLstBox.setParent(row);
					
					
					/*Label fileTypeLbl = new Label(userPosFTPSettings.getFileType());
					fileTypeLbl.setParent(row);*/
					
					//Host Address
					Textbox tempTextbox = null; 
					tempTextbox = new Textbox(userPosFTPSettings.getHostAddress().trim());
					tempTextbox.setWidth("130px;");
					tempTextbox.setParent(row);
					//Label userHostAddrLbl = new Label();
					
					//User Name
					tempTextbox = new Textbox(userPosFTPSettings.getFtpUserName().trim());
					tempTextbox.setWidth("130px;");
					tempTextbox.setParent(row);
//				Label userNameLbl = new Label(userPosFTPSettings.getFtpUserName());
					
					//Password
					tempTextbox = new Textbox(userPosFTPSettings.getFtpPassword().trim());
					tempTextbox.setType("password");
					tempTextbox.setWidth("90px;");
					tempTextbox.setParent(row);
					/*Label userPwdLbl = new Label(userPosFTPSettings.getFtpPassword());
					userPwdLbl.setParent(row);*/
					
					
					//Directory Location
					tempTextbox = new Textbox(userPosFTPSettings.getDirectoryPath().trim());
					tempTextbox.setWidth("125px;");
					tempTextbox.setParent(row);
					/*Label userDirectPathLbl = new Label(userPosFTPSettings.getDirectoryPath());
					userDirectPathLbl.setParent(row);*/
					
					//File Format
					tempTextbox = new Textbox(userPosFTPSettings.getFileFormat().trim());
					tempTextbox.setWidth("125px;");
					tempTextbox.setParent(row);
					
					/*Label fileFormatLbl = new Label(userPosFTPSettings.getFileFormat());
					fileFormatLbl.setParent(row);*/
					
					//Enabled Ftp server
					Listbox isEnableListbox = new Listbox();
					isEnableListbox.setMold("select");
					tempItem = new Listitem("True");
					tempItem.setParent(isEnableListbox);
					
					tempItem = new Listitem("False");
					tempItem.setParent(isEnableListbox);
					
					if(userPosFTPSettings.getEnabled()) {
						isEnableListbox.setSelectedIndex(0);
					}else  isEnableListbox.setSelectedIndex(1);
					
					isEnableListbox.setParent(row);
					
					
					//Scheduled Freq Time
					Div timpDiv = new Div();
					
					Intbox tempIntbox = new Intbox();
					tempIntbox.setMaxlength(2);
					tempIntbox.setWidth("20px");
					Listbox tempListbox = createTimeListbox();
					tempListbox.setStyle("margin: 0 2px 0 14px;");
					List chaildList = tempListbox.getChildren();
					for (Object object : chaildList) {
						tempItem = (Listitem)object;
						if(tempItem.getLabel().equals(userPosFTPSettings.getScheduleType().trim())) {
							tempListbox.setSelectedItem(tempItem);
							
						}
						
					}
					
					
					long tempMints = userPosFTPSettings.getScheduledFreqInMintues();
					if(userPosFTPSettings.getScheduleType().equals("Hours")) {
						tempMints = tempMints / 60;
					}else if(userPosFTPSettings.getScheduleType().equals("Days")) {
						tempMints = tempMints / (24*60);
					}
					tempIntbox.setValue((int)tempMints);
					
					tempIntbox.setParent(timpDiv);
					tempListbox.setParent(timpDiv);
					
					timpDiv.setParent(row);
					
					
					//delete Image
					Image delImg = new Image();
					delImg.setAttribute("TYPE", "FTP_SETTINGS");
					delImg.setSrc("/images/action_delete.gif");
					delImg.setStyle("cursor:pointer;");
					delImg.addEventListener("onClick", this);
					delImg.setParent(row);
					
					
					row.setParent(ftpSettingsRowsId);
					row.setAttribute("referenceId", userPosFTPSettings);
				}
			} catch (Exception e) {
				logger.error("Exception  ::", e);
			}
			
			
		} //defaultFTPsSettings
		
		private Listbox createFieldTypeListBox() {

			Listbox tempListBox =new Listbox();
			tempListBox.setMold("select");
			Listitem tempItem = null;
			
			tempItem  =new Listitem(Constants.POS_MAPPING_TYPE_CONTACTS);
			tempItem.setParent(tempListBox);
			
			tempItem  =new Listitem(Constants.POS_MAPPING_TYPE_SALES);
			tempItem.setParent(tempListBox);
			
			tempItem  =new Listitem(Constants.POS_MAPPING_TYPE_SKU);
			tempItem.setParent(tempListBox);
			
			return tempListBox;
			
		}
		
		private Listbox createTimeListbox() {
			
			Listbox tempListBox =new Listbox();
			tempListBox.setMold("select");
			Listitem tempItem = null;
			
			tempItem  =new Listitem("Minutes");
			tempItem.setParent(tempListBox);
			
			tempItem  =new Listitem("Hours");
			tempItem.setParent(tempListBox);
			
			
			tempItem  =new Listitem("Days");
			tempItem.setParent(tempListBox);
			
			return tempListBox;
		}
		
		
		private Listbox scheduleTypeListBxId;
		
		
		public void onClick$ftpSettingsAddBtnId() {
			
			if(users == null){
				MessageUtil.setMessage("Please select user. ","color:red","TOP");
				return ;
			}
			
			Row tempRow = new Row();
			Listbox tempListbox = null;
			Textbox  tempTextBox = null;
			Listitem tempItem = null;
			
			//File Type
			tempListbox = createFieldTypeListBox();
			tempListbox.setSelectedIndex(0);
			tempListbox.setParent(tempRow);
			
			//Host Address
			tempTextBox = new Textbox();
			tempTextBox.setWidth("130px;");
			tempTextBox.setParent(tempRow);
			
			//User Name
			tempTextBox = new Textbox();
			tempTextBox.setWidth("130px;");
			tempTextBox.setParent(tempRow);
			
			//Password
			tempTextBox = new Textbox();
			tempTextBox.setType("password");
			tempTextBox.setWidth("90px;");
			tempTextBox.setParent(tempRow);
			
			//Directory Path
			tempTextBox = new Textbox();
			tempTextBox.setWidth("125px;");
			tempTextBox.setParent(tempRow);
			
			//File Format
			tempTextBox = new Textbox();
			tempTextBox.setWidth("125px;");
			tempTextBox.setParent(tempRow);
			
			//Enabled FTP server
			tempListbox = new Listbox();
			tempListbox.setMold("select");
			tempItem = new Listitem("True");
			tempItem .setParent(tempListbox);
			tempItem = new Listitem("False");
			tempItem .setParent(tempListbox);
			tempListbox.setSelectedIndex(0);
			tempListbox.setParent(tempRow);
			
			//Scheduled  Freq Time
			Div tempDiv = new Div();
			Intbox tempIntbox =new Intbox();
			tempIntbox.setMaxlength(2);
			tempIntbox.setWidth("20px");
			tempIntbox.setParent(tempDiv);
			 
			tempListbox = createTimeListbox();
			tempListbox.setStyle("margin: 0 2px 0 14px;");
			tempListbox.setSelectedIndex(0);
			tempListbox.setParent(tempDiv);
			
			tempDiv.setParent(tempRow);
			
			
			//Delete Image
			Image delImg = new Image();
			delImg.setAttribute("TYPE", "FTP_SETTINGS");
			delImg.setSrc("/images/action_delete.gif");
			delImg.setStyle("cursor:pointer;");
			delImg.addEventListener("onClick", this);
			delImg.setParent(tempRow);
			
			
			tempRow.setParent(ftpSettingsRowsId);
			
		}
		
		// here we need to save that value of time zone
		public void onClick$submitBtnId() {
			
			try {
				/*************Ftp Settings*******************/
				
				Listitem Item = TimesZonesLbId.getSelectedItem();
				//Listitem loyaltyServicetype = loyaltyServicetypeLbId.getSelectedItem();
				if(Item == null || Item.getIndex() == 0) {
					
					MessageUtil.setMessage("Please select the client's time zone", "color:red;");
					return;
					
				}
				
				
				
				//Validate OptSync Auth key 
				
				String optSyncAuth = optSynAuthKeyTbId.getValue().trim();
				if(!validateOptSyncKey(optSyncAuth)){
					generateKeyAnchId.setVisible(true);
					optSynAuthKeyTbId.setReadonly(false);
					return ;
				}
				
				
				
				//int clientTimeZone = IntegItem.getValue();
				
				//logger.debug("Here in Submit Button  "+ clientTimeZone);
				
				users.setClientTimeZone(Item.getValue().toString());
				
				//Loyalty_Service_Type 2.4.11
				//users.setloyaltyServicetype(loyaltyServicetype.getValue().toString());
				
			

				List chaildRowList =ftpSettingsRowsId.getChildren();
				
				if(chaildRowList == null || chaildRowList.size() == 0) {
					logger.debug("No data available for saving");
					MessageUtil.setMessage("No FTP settings found. Please provide FTP settings by clicking on 'Add' button.","color:red","TOP");
					return;
					
				}
				
				//Validation of Ftp setting
				for (Object object : chaildRowList) {
					Row tempRow  =(Row)object;
					if(ftpSettingValidation(tempRow) == false) {
						return;
					}
					
				}
				
				//save alert
				String emailIdStr = null;
				int chkProcessedTimePeriod = Integer.parseInt((String) alertConfigLstbxId.getSelectedItem().getValue());
				logger.info("==Alert Data Listbox=="+chkProcessedTimePeriod);
				boolean chkalert = alertChekbxId.isChecked();
				logger.info("==Alert Data Checkbox=="+chkalert);
				if(chkalert){
					if(!isValidate()){
						MessageUtil.setMessage("Please enter atleast one email address.", "color:red");
						return;
					}
					emailIdStr = prepareEmailIdStr();
					if(emailIdStr == null || emailIdStr.isEmpty()){
						return;
					}
				}else {
					Components.removeAllChildren(alertMailDivId);
					alertConfigLstbxId.setSelectedIndex(2);
				}
				
				//Save
				List<UserPosFTPSettings> userFtpSelltingList = new ArrayList<UserPosFTPSettings>(); 
				UserPosFTPSettings userPosFTPSettings = null;
				int scheduleMintInt = 0;
				Listbox tempListbox = null;
				Textbox tempTextbox = null;
				Div tempDiv  = null;
				Intbox tempIntbox = null;
				String fileTypeAndFormatStr = "";
				for (Object object : chaildRowList) {
					Row tempRow  =(Row)object;
					
					List chaildLblList = tempRow.getChildren();
					userPosFTPSettings = (UserPosFTPSettings)tempRow.getAttribute("referenceId");
					
					if(userPosFTPSettings == null) {
						userPosFTPSettings = new UserPosFTPSettings();
						userPosFTPSettings.setLastFetchedTime(Calendar.getInstance());

					}
					//FileType
					tempListbox = (Listbox)chaildLblList.get(0);
					userPosFTPSettings.setFileType(tempListbox.getSelectedItem().getLabel());
					if(tempListbox.getSelectedItem().getLabel().contains("Sales")){
						userPosFTPSettings.setCheckAlert(chkalert);
						userPosFTPSettings.setAlertEmailAddress(emailIdStr);
						if(chkalert){
							userPosFTPSettings.setCheckProcessPeriod(chkProcessedTimePeriod);
						}
						else{
							userPosFTPSettings.setCheckProcessPeriod(0);
						}
					}
					
					//Host Address
					tempTextbox = (Textbox)chaildLblList.get(1);
					userPosFTPSettings.setHostAddress(tempTextbox.getValue());
					
					//User Name
					tempTextbox = (Textbox)chaildLblList.get(2);
					userPosFTPSettings.setFtpUserName(tempTextbox.getValue());
					
					
					//Password
					tempTextbox = (Textbox)chaildLblList.get(3);
					userPosFTPSettings.setFtpPassword(tempTextbox.getValue());
					
					
					//DirectoryPath
					tempTextbox = (Textbox)chaildLblList.get(4);
					userPosFTPSettings.setDirectoryPath(tempTextbox.getValue());
					
					//File Format
					tempTextbox = (Textbox)chaildLblList.get(5);
					userPosFTPSettings.setFileFormat(tempTextbox.getValue());
					
					if(tempListbox.getSelectedIndex() == 0 && !(tempTextbox.getValue().trim().toLowerCase().contains("#contacts#"))) {
						
						fileTypeAndFormatStr += tempListbox.getSelectedItem().getLabel() +" :"+tempTextbox.getValue().trim() +"\n\n";
						
					}else if(tempListbox.getSelectedIndex() == 1 && !(tempTextbox.getValue().trim().toLowerCase().contains("#sales#"))) {
						fileTypeAndFormatStr += tempListbox.getSelectedItem().getLabel() +" :"+tempTextbox.getValue().trim() +"\n\n";
						
						/*userPosFTPSettings.setCheckAlert(chkalert);
						userPosFTPSettings.setAlertEmailAddress(emailIdStr);
						if(chkalert){
							userPosFTPSettings.setCheckProcessPeriod(chkProcessedTimePeriod);
						}
						else{
							userPosFTPSettings.setCheckProcessPeriod(0);
						}*/
						
					}else if(tempListbox.getSelectedIndex() == 2 && !(tempTextbox.getValue().trim().toLowerCase().contains("#sku#"))) {
						fileTypeAndFormatStr += tempListbox.getSelectedItem().getLabel() +" :"+tempTextbox.getValue().trim() +"\n\n";
					}
					
					//Enabled Ftp server
					tempListbox = (Listbox) chaildLblList.get(6);
//				logger.debug("---->"+tempLbl.getValue());
					if(tempListbox.getSelectedItem().getLabel().equalsIgnoreCase(("true"))) {
						
						userPosFTPSettings.setEnabled(true);
					}else userPosFTPSettings.setEnabled(false);
					
					//Scheduled Freq Time
					tempDiv = (Div)chaildLblList.get(7);
					tempIntbox = (Intbox)tempDiv.getChildren().get(0);
					scheduleMintInt = tempIntbox.getValue();
					
					tempListbox = (Listbox)tempDiv.getChildren().get(1);
					
					if(tempListbox.getSelectedIndex() == 1) {
						scheduleMintInt = scheduleMintInt * 60;
					}else if(tempListbox.getSelectedIndex() == 2) {
						scheduleMintInt = scheduleMintInt *24* 60;
					}
					userPosFTPSettings.setScheduledFreqInMintues((long)scheduleMintInt);
					
					//Schedule Type
					userPosFTPSettings.setScheduleType(tempListbox.getSelectedItem().getLabel());
					
					
					//set User Id
					userPosFTPSettings.setUserId(users.getUserId());
					
					
					userFtpSelltingList.add(userPosFTPSettings);
					
				} 
				/*int confirm = Messagebox.show("Are you sure you want to save file format settings? \n\n "+fileTypeAndFormatStr, "Prompt", 
						 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);*/
				int confirm = Messagebox.show("Are you sure you want to save the settings? \n\n "+fileTypeAndFormatStr, "Prompt", 
						 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				
				if(confirm == Messagebox.OK) {
					
					//userPOSFTPSettingsDao.saveByCollection(userFtpSelltingList);
					userPOSFTPSettingsDaoForDML.saveByCollection(userFtpSelltingList);
					
					/*
					//Save Generated OptSync Key
					UserOrganization userOrgObj = GetUser.getUserObj().getUserOrganization();
					if(userOrgObj != null) {
						
						long userOrgId= userOrgObj.getUserOrgId();
						String orgExternalId =userOrgObj.getOrgExternalId();
						
								
								logger.info("Setting optsyncAuthkey to user org");
								
								int rowUpdated = usersDao.updateOptSynKey(userOrgId,orgExternalId , optSyncAuthkey);
						logger.info("OptSyncAuthKey is Stored successfully ......");
							
					}*/
					UserOrganization userOrgObj = users.getUserOrganization();
					if(userOrgObj.getOptSyncKey() == null || 
							!userOrgObj.getOptSyncKey().equals(optSynAuthKeyTbId.getValue().trim())) {
						userOrgObj.setOptSyncKey(optSynAuthKeyTbId.getValue().trim());
						//usersDao.saveOrUpdate(userOrgObj);
						usersDaoForDML.saveOrUpdate(userOrgObj);
					}
					userOrgObj.setMultiUser(ismultiuserCkbId.isChecked());
					users.setSpecificDir(isUserSpecDirCkbId.isChecked());
					usersDaoForDML.saveOrUpdate(userOrgObj);
					//logger.debug("User id while saving "+users.getUserId());
					//usersDao.saveOrUpdate(users);
					usersDaoForDML.saveOrUpdate(users);
					
					//MessageUtil.setMessage("Saved successfully.","color:green","TOP");
					
					defaultFTPSettings();
					saveMasterToTransMappings();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception  ::", e);
			}
			
			logger.info("==Exit from Submit Button==");
			//Save Master To Trans Realation Mapping
			
		}
		
		private boolean isValidate() {
			List<Component> tempDivList = alertMailDivId.getChildren();
			if(alertMailTxtbxId.getValue() != null && !alertMailTxtbxId.getValue().trim().isEmpty()){
				return true;
			}
			if(tempDivList != null && tempDivList.size() > 0){
				for (Component tempDiv : tempDivList) {
					Textbox tempTb = (Textbox) tempDiv.getFirstChild();
					if(tempTb.getValue() != null && !tempTb.getValue().trim().isEmpty()){
						return true;
					}
				}
			}
			
			return false;
		}
		private String prepareEmailIdStr() {
			String emailIdStr = null;
			List<Component> tempDivList = alertMailDivId.getChildren();
			if(alertMailTxtbxId.getValue() != null && !alertMailTxtbxId.getValue().trim().isEmpty()){
				if(!Utility.validateEmail(alertMailTxtbxId.getValue().trim())){
					MessageUtil.setMessage("Please enter valid email address.","color:red","TOP");
					return emailIdStr;
				}
				emailIdStr = alertMailTxtbxId.getValue().trim();
			}
			if(tempDivList != null && tempDivList.size() > 0){
				for (Component tempDiv : tempDivList) {
					Textbox tempTb = (Textbox) tempDiv.getFirstChild();
					
					if(tempTb.getValue() != null && !tempTb.getValue().trim().isEmpty()){
						if(!Utility.validateEmail(tempTb.getValue().trim())){
							MessageUtil.setMessage("Please enter valid email address.","color:red","TOP");
							return emailIdStr;
						}
						if(emailIdStr == null || emailIdStr.isEmpty()){
							emailIdStr = tempTb.getValue().trim();
						}
						else{
							emailIdStr += Constants.ADDR_COL_DELIMETER + tempTb.getValue().trim();
						}
					}
				}
			}

			return emailIdStr;
		}
		//Ftp Setting Validation
		private boolean ftpSettingValidation(Row tempRow) {

			
			 try {
				List rowList = tempRow.getChildren();
				 Listbox tempListbox = null;
				 
				 //File Type
				 tempListbox =  (Listbox)rowList.get(0);
				 if(tempListbox.getSelectedIndex() == -1) {
					 MessageUtil.setMessage("Please select file type.","color:red","TOP");
					 return false;
				 }
				 
				//Host Address
				 Textbox tempTextbox = null;
				 tempTextbox = (Textbox)rowList.get(1);
				 tempTextbox.setStyle("border:1px solid #7F9DB9;");
				 
				 if(tempTextbox.getValue().trim().equals("")) {
					 tempTextbox.setStyle("border:1px solid #DD7870;");
					 MessageUtil.setMessage("Please provide Host Address.","color:red","TOP");
					 return false;
				 }
				
				//UserName
				 tempTextbox = (Textbox)rowList.get(2);
				 tempTextbox.setStyle("border:1px solid #7F9DB9;");
				 
				 if(tempTextbox.getValue().trim().equals("")) {
					 tempTextbox.setStyle("border:1px solid #DD7870;");
					 MessageUtil.setMessage("Please provide username.","color:red","TOP");
					 return false;
				 }
				 
				 //Password
				 tempTextbox = (Textbox)rowList.get(3);
				 tempTextbox.setStyle("border:1px solid #7F9DB9;");
				 
				 if(tempTextbox.getValue().trim().equals("")) {
					 tempTextbox.setStyle("border:1px solid #DD7870;");
					 MessageUtil.setMessage("Please provide password.","color:red","TOP");
					 return false;
				 }
				 
				 //Directory Path
				 tempTextbox = (Textbox)rowList.get(4);
				 tempTextbox.setStyle("border:1px solid #7F9DB9;");
				 
				 if(tempTextbox.getValue().trim().equals("")) {
					 tempTextbox.setStyle("border:1px solid #DD7870;");
					 MessageUtil.setMessage("Please provide Directory Path.","color:red","TOP");
					 return false;
				 }
				 
				 //File Format
				 tempTextbox = (Textbox)rowList.get(5);
				 tempTextbox.setStyle("border:1px solid #7F9DB9;");
				 
				 if(tempTextbox.getValue().trim().equals("")) {
					 tempTextbox.setStyle("border:1px solid #DD7870;");
					 MessageUtil.setMessage("Please provide File Format.","color:red","TOP");
					 return false;
				 }
				 
				 UserPosFTPSettings userFtpObject =  userPOSFTPSettingsDao.findByName(tempTextbox.getValue());
				 UserPosFTPSettings userPosFTPSettings = (UserPosFTPSettings)tempRow.getAttribute("referenceId");
				 
				 if((userFtpObject != null && userPosFTPSettings == null) ||
						 (userFtpObject != null && userPosFTPSettings != null && userPosFTPSettings.getUserFTPId().longValue() != userFtpObject.getUserFTPId())){
					 
					 tempTextbox.setStyle("border:1px solid #DD7870;");
					 MessageUtil.setMessage(tempTextbox.getValue()+" already exists.","color:red","TOP");
					 return false;
				 }
				 
				 
				 //Scheduled Freq Time 
				 Div tempDiv =  (Div)rowList.get(7);
				 
				 Intbox tempIntBox = (Intbox)tempDiv.getChildren().get(0);
				 tempIntBox.setStyle("border:1px solid #7F9DB9;");
				 
				 if(tempIntBox.getValue()== null) {
					 tempIntBox.setStyle("border:1px solid #DD7870;");
					 MessageUtil.setMessage("Please provide Scheduled Time.","color:red","TOP");
					 return false;
				 }
				 
				
				return true;
			} catch (Exception e) {
				logger.error("Exception  ::", e);
				return false;
			}
		} //ftpSettingValidation
		
		
		
		public void onClick$posContTbBtnId() {
			
			posMapContDivId.setVisible(!posMapContDivId.isVisible());
			
			String image = posMapContDivId.isVisible() ? "/img/icons/icon_minus.png" : "/img/icons/icon_plus.png";
			posContTbBtnId.setImage(image);
			
		}
		
		public void onClick$posSalesTbBtnId() {
			
			posMapSalesDivId.setVisible(!posMapSalesDivId.isVisible());
			String image = posMapSalesDivId.isVisible() ? "/img/icons/icon_minus.png" : "/img/icons/icon_plus.png";
			posSalesTbBtnId.setImage(image);
		}
		
		public void onClick$posSKUTbBtnId() {
			
			posMapSKUDivId.setVisible(!posMapSKUDivId.isVisible());
			String image = posMapSKUDivId.isVisible() ? "/img/icons/icon_minus.png" : "/img/icons/icon_plus.png";
			posSKUTbBtnId.setImage(image);
		}
		
		
		public void onCheck$doubleOptInCbId() {
			
			/*if(posML == null) {
				return;
			}*/
			doubleOptLtDiv.setVisible(!doubleOptLtDiv.isVisible());
			onSelect$optInEmailsLbId();
		} // onCheck$doubleOptInCbId 
		
		
		public void onCheck$parentalConsentCbId() {
			/*if(posML == null) {
				return;
			}*/
			
			parentalOptLtDiv.setVisible(!parentalOptLtDiv.isVisible());
			onSelect$parentalConsentEmailLbId();
			
		} //onCheck$parentalConsentCbId
		
		
		public void onCheck$loyaltyOptInCbId() {
			
			/*if(posML == null) {
				return;
			}*/
			loyaltyOptLtDiv.setVisible(!loyaltyOptLtDiv.isVisible());
			onSelect$LoyaltyOptinEmailLbId();
		} // onCheck$doubleOptInCbId 
  // check for welcome email 
	public void onCheck$welcomeEmailsCbId() {

		/*if (posML == null) {
			return;
		}*/
		enableWelcomeEmailDivId.setVisible(!enableWelcomeEmailDivId.isVisible());
		onSelect$welcomeEmailsLbId();
		
	} // onCheck$welcomeEmailsCbId
	
	public void onCheck$extractLoyaltyChkBxId() { 

		//enrolAllReqLbl.setVisible(extractLoyaltyChkBxId.isChecked());
		//enrolRequestChkBxId.setVisible(extractLoyaltyChkBxId.isChecked());
		//enrolRequestChkBxId.setChecked(users.isEnrollAllRequests());
		
		if(extractLoyaltyChkBxId.isChecked()){
		enrolLbl.setVisible(extractLoyaltyChkBxId.isChecked());
		enrolChkBxId.setVisible(extractLoyaltyChkBxId.isChecked());
		enrolChkBxId.setChecked(users.isEnrollFromDR() && extractLoyaltyChkBxId.isChecked());
		
		issuanceLbl.setVisible(extractLoyaltyChkBxId.isChecked());
		issuanceChkBxId.setVisible(extractLoyaltyChkBxId.isChecked());
		issuanceChkBxId.setChecked(users.isIssuanceFromDR());
		
		returnLbl.setVisible(extractLoyaltyChkBxId.isChecked());
		returnChkBxId.setVisible(extractLoyaltyChkBxId.isChecked());
		returnChkBxId.setChecked(users.isReturnFromDR());
		
		redemptionReversalLbl.setVisible(extractLoyaltyChkBxId.isChecked() && returnChkBxId.isChecked());
		redemptionReversalChkBxId.setVisible(extractLoyaltyChkBxId.isChecked() && returnChkBxId.isChecked());
		redemptionReversalChkBxId.setChecked(users.isPerformRedeemedAmountReversal());
		
		redemptionLbl.setVisible(extractLoyaltyChkBxId.isChecked());
		redemptionChkBxId.setVisible(extractLoyaltyChkBxId.isChecked());
		redemptionChkBxId.setChecked(users.isRedemptionFromDR());
		
		IgnLbl.setVisible(extractLoyaltyChkBxId.isChecked());
		IgnChkBxId.setVisible(extractLoyaltyChkBxId.isChecked());
		IgnChkBxId.setChecked(users.isIgnoretrxUpOnExtraction());
		
		redemptionTenderLbl.setVisible(extractLoyaltyChkBxId.isChecked());
		redeemTenderListBxId.setVisible(extractLoyaltyChkBxId.isChecked() && (redeemAsListBxId.getSelectedItem()!=null && redeemAsListBxId.getSelectedItem().getValue()!=null? redeemAsListBxId.getSelectedItem().getValue().equals(Tender):false));
		
		Components.removeAllChildren(redeemTenderListBxId);
		redemptionTenderTbId.setText(Constants.STRING_NILL);
		
		Components.removeAllChildren(redeemAsListBxId);
		ninItemFieldTbId.setText(Constants.STRING_NILL);
		
		Listitem redeemItem = new Listitem("--Select--",null);
		Listitem redeemItemNinv = new Listitem(NonInventoryItem,NonInventoryItem);
		Listitem redeemItemTender = new Listitem(Tender,Tender);
		redeemItem.setParent(redeemAsListBxId);
		redeemItemNinv.setParent(redeemAsListBxId);
		redeemItemTender.setParent(redeemAsListBxId);
		Listitem redeemTenderItem = new Listitem("--Select--",null);
		Listitem redeemTenderItemCreditCard = new Listitem(CreditCard,CreditCard);
		Listitem redeemTenderItemCOD = new Listitem(COD,COD);
		redeemTenderItem.setParent(redeemTenderListBxId);
		redeemTenderItemCreditCard.setParent(redeemTenderListBxId);
		redeemTenderItemCOD.setParent(redeemTenderListBxId);
		redeemTenderListBxId.setSelectedIndex(0);
		redeemTenderListBxId.setVisible(extractLoyaltyChkBxId.isChecked() && (redeemAsListBxId.getSelectedItem()!=null && redeemAsListBxId.getSelectedItem().getValue()!=null? redeemAsListBxId.getSelectedItem().getValue().equals(Tender):false));
		redemptionTenderLbl.setVisible(extractLoyaltyChkBxId.isChecked());
		redemptionTenderTbId.setVisible(extractLoyaltyChkBxId.isChecked() && 
				(redeemTenderListBxId.getSelectedItem().getValue()!=null &&
				redeemTenderListBxId.getSelectedItem().getValue().toString().equalsIgnoreCase(CreditCard)));
		redemptionTenderDisplayLblId.setVisible(extractLoyaltyChkBxId.isChecked());
		redemptionTenderDispLblTbId.setVisible(true);
		
		Components.removeAllChildren(cardInfoListBxId);
		for(int i=0; i<cardInfoStr.length; i++) { 
			Listitem cardInfoItem = new Listitem(cardInfoStr[i],cardInfoStr[i]);
			cardInfoItem.setParent(cardInfoListBxId);
		}
		for(Listitem li : cardInfoListBxId.getItems()) {
			if(li.getValue()==null) {
				cardInfoListBxId.setSelectedIndex(0);
			}else {
				if(li.getValue().equals(users.getCardInfo())) {
					cardInfoListBxId.setSelectedItem(li);
					break;
				}
			}
		}
		cardInfoListBxId.setSelectedIndex(0);
		cardInfoLbl.setVisible(extractLoyaltyChkBxId.isChecked());
		cardInfoListBxId.setVisible(extractLoyaltyChkBxId.isChecked());
		}else{
			enrolLbl.setVisible(false);
			enrolChkBxId.setVisible(false);
			
			issuanceLbl.setVisible(false);
			issuanceChkBxId.setVisible(false);
			
			returnLbl.setVisible(false);
			returnChkBxId.setVisible(false);
			
			redemptionLbl.setVisible(false);
			redemptionChkBxId.setVisible(false);
			redemptionTenderLbl.setVisible(false);
			redemptionTenderTbId.setVisible(false);
			redemptionTenderDisplayLblId.setVisible(false);
			redemptionTenderDispLblTbId.setVisible(false);
			redemptionReversalLbl.setVisible(false);
			redeemTenderListBxId.setVisible(false);
			redemptionReversalChkBxId.setVisible(false);
			cardInfoLbl.setVisible(false);
			cardInfoListBxId.setVisible(false);
		}
		if(redeemPromoChkBxId.isChecked()) {
			receiptNoteListBxId.setVisible(true);
			itemNoteListBxId.setVisible(true);
			itemNoteLbl.setVisible(true);
			receiptNoteLbl.setVisible(true);
			itemInfoLbl.setVisible(true);
			itemInfoFieldListBxId.setVisible(true);
		
		}
		else if(extractLoyaltyChkBxId.isChecked() ||redeemPromoChkBxId.isChecked()) {
			IgnLbl.setVisible(extractLoyaltyChkBxId.isChecked());
				IgnChkBxId.setVisible(extractLoyaltyChkBxId.isChecked());
				IgnChkBxId.setChecked(users.isIgnoretrxUpOnExtraction());
		}
		else {
			receiptNoteListBxId.setVisible(false);
			itemNoteListBxId.setVisible(false);
			itemNoteLbl.setVisible(false);
			receiptNoteLbl.setVisible(false);
			itemInfoLbl.setVisible(false);
			itemInfoFieldListBxId.setVisible(false);
			IgnLbl.setVisible(false);
			IgnChkBxId.setVisible(false);
		}
	}
		
	public void onCheck$enrolChkBxId() { 
			
		enrolChkBxId.setChecked(enrolChkBxId.isChecked());
		}
	public void onCheck$issuanceChkBxId() { 
		
		issuanceChkBxId.setChecked(issuanceChkBxId.isChecked());
	}
	/*public void onCheck$redemptionChkBxId() { 
		
		Components.removeAllChildren(redeemTenderListBxId);
		redemptionTenderTbId.setText(Constants.STRING_NILL);
		Listitem redeemTenderItem = new Listitem("--Select--",null);
		Listitem redeemTenderItemCreditCard = new Listitem(CreditCard,CreditCard);
		Listitem redeemTenderItemCOD = new Listitem(COD,COD);
		redeemTenderItem.setParent(redeemTenderListBxId);
		redeemTenderItemCreditCard.setParent(redeemTenderListBxId);
		redeemTenderItemCOD.setParent(redeemTenderListBxId);
		redeemTenderListBxId.setSelectedIndex(0);
		redeemTenderListBxId.setVisible(redemptionChkBxId.isChecked());
		redemptionTenderLbl.setVisible(redemptionChkBxId.isChecked());
		redemptionTenderTbId.setVisible(redemptionChkBxId.isChecked() && 
				(redeemTenderListBxId.getSelectedItem().getValue()!=null &&
				redeemTenderListBxId.getSelectedItem().getValue().toString().equalsIgnoreCase(CreditCard)));
	}*/
	public void onCheck$redeemPromoChkBxId() {
		Components.removeAllChildren(receiptNoteListBxId);
		Listitem recDiscItem = new Listitem("--Select--",null);
		recDiscItem.setParent(receiptNoteListBxId);
		for(int i=0; i<receiptDiscStr.length; i++) { 
			recDiscItem = new Listitem(receiptDiscStr[i],receiptDiscStr[i]);
			recDiscItem.setParent(receiptNoteListBxId);
		}
		for(Listitem li : receiptNoteListBxId.getItems()) {
			if(li.getValue()==null) {
				receiptNoteListBxId.setSelectedIndex(0);
			}else {
				if(li.getValue().equals(users.getReceiptNoteUsed())) {
					receiptNoteListBxId.setSelectedItem(li);
					break;
				}
			}
		}
		receiptNoteListBxId.setSelectedIndex(0);
		receiptNoteLbl.setVisible(redeemPromoChkBxId.isChecked());
		receiptNoteListBxId.setVisible(redeemPromoChkBxId.isChecked());

		Components.removeAllChildren(itemNoteListBxId);
		Listitem itemNoteItem = new Listitem("--Select--",null);
		itemNoteItem.setParent(itemNoteListBxId);
		for(int i=0; i<itemDiscStr.length; i++) { 
			itemNoteItem = new Listitem(itemDiscStr[i],itemDiscStr[i]);
			itemNoteItem.setParent(itemNoteListBxId);
		}
		for(Listitem li : itemNoteListBxId.getSelectedItems()) {
			if(li.getValue().equals(users.getItemNoteUsed())) itemNoteListBxId.setSelectedItem(li);
		}
		itemNoteListBxId.setSelectedIndex(0);
		itemNoteLbl.setVisible(redeemPromoChkBxId.isChecked());
		itemNoteListBxId.setVisible(redeemPromoChkBxId.isChecked());
		
		Components.removeAllChildren(itemInfoFieldListBxId);
		Listitem itemInfoItem = new Listitem("--Select--",null);
		itemInfoItem.setParent(itemInfoFieldListBxId);
		Listitem tempItem = null;
		for(int i=0; i<itemInfoStr.length; i++) { 
			tempItem = new Listitem(itemInfoStr[i],itemInfoStr[i]);
			tempItem.setParent(itemInfoFieldListBxId);
		}
		for(Listitem li : itemInfoFieldListBxId.getSelectedItems()) {
			if(li.getValue().equals(users.getItemInfo())) itemInfoFieldListBxId.setSelectedItem(li);
		}
		itemInfoFieldListBxId.setSelectedIndex(0);
		itemInfoLbl.setVisible(redeemPromoChkBxId.isChecked());
		IgnLbl.setVisible(redeemPromoChkBxId.isChecked()||extractLoyaltyChkBxId.isChecked());
		itemInfoFieldListBxId.setVisible(redeemPromoChkBxId.isChecked());
		IgnChkBxId.setVisible(redeemPromoChkBxId.isChecked()||extractLoyaltyChkBxId.isChecked());
		IgnChkBxId.setChecked(users.isIgnoretrxUpOnExtraction());
	
	
	}
	public void onCheck$returnChkBxId(){
		
		redemptionReversalLbl.setVisible(returnChkBxId.isChecked());
		redemptionReversalChkBxId.setVisible(returnChkBxId.isChecked());
		
	}
	/*public void onCheck$enrolRequestChkBxId(){
		
		enrolChkBxId.setChecked(users.isEnrollFromDR() ? users.isEnrollFromDR() : enrolRequestChkBxId.isChecked());
	}*/
		private Checkbox welcomeEmailsCbId;
		private Div enableWelcomeEmailDivId;
		private Window previewWin;
		private Iframe previewWin$html;
		public void onClick$previewBtnId() {
			String templateContent = "";
			
			//previewWin$html.setContent(templateContent);
			
			CustomTemplates customTemplates = optInEmailsLbId.getSelectedItem().getValue();
			if(customTemplates == null) {
				templateContent = 	PropertyUtil.getPropertyValueFromDB("optinMsgTemplate");
			}else {
				
				
				templateContent = customTemplates.getHtmlText();
				if(templateContent != null && !templateContent.isEmpty()) {
					templateContent = customTemplates.getHtmlText();
				}else if(customTemplates.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE) && customTemplates.getMyTemplateId()!= null) {
					 MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(customTemplates.getMyTemplateId());
					 if(myTemplates!=null) {
						 templateContent = myTemplates.getContent();
					 }else {
						 MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.", "color:red", "TOP");
						 return;
					 }
				}
			}
		//	previewWin$html.setContent(templateContent);
			if(users== null ){
				MessageUtil.setMessage("Please provide user-details.", "color:blue");
				return ;
			}
			
			Utility.showPreview(previewWin$html, users.getUserName(), templateContent);
			
			previewWin.setVisible(true);
			
		}//onClick$previewBtnId
		
		public void onClick$parentalPreviewBtnId() {
			String templateContent = "";
			
			//previewWin$html.setContent(templateContent);
			
			CustomTemplates customTemplates = parentalConsentEmailLbId.getSelectedItem().getValue();
			if(customTemplates == null) {
				templateContent = 	PropertyUtil.getPropertyValueFromDB("parentalConsentMsgtemplate");
			}else {
				
				
				templateContent = customTemplates.getHtmlText();
				if(templateContent != null && !templateContent.isEmpty()) {
					templateContent = customTemplates.getHtmlText();
				}else if(customTemplates.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE) && customTemplates.getMyTemplateId()!= null) {
					 MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(customTemplates.getMyTemplateId());
					 if(myTemplates!=null) {
						 templateContent = myTemplates.getContent();
					 }else {
						 MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.", "color:red", "TOP");
						 return;
					 }
				}
			}
	//		previewWin$html.setContent(templateContent);
			
			if(users== null ){
				MessageUtil.setMessage("Please provide user-details. ", "color:blue");
				return ;
			}
			
			Utility.showPreview(previewWin$html, users.getUserName(), templateContent);
			
			previewWin.setVisible(true);
			
		}//onClick$parentalPreviewBtnId
		
		//added after auto email
		public void onClick$loyaltyPreviewBtnId() {
			String templateContent = "";
			
			//previewWin$html.setContent(templateContent);
			
			CustomTemplates customTemplates = LoyaltyOptinEmailLbId.getSelectedItem().getValue();
			if(customTemplates == null) {
				
			templateContent = 	PropertyUtil.getPropertyValueFromDB("LoyaltyOptinMsgTemplate");
		
				
			}else {
				templateContent = customTemplates.getHtmlText();
				if(templateContent != null && !templateContent.isEmpty()) {
					templateContent = customTemplates.getHtmlText();
				}else if(customTemplates.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE) && customTemplates.getMyTemplateId()!= null) {
					 MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(customTemplates.getMyTemplateId());
					 if(myTemplates!=null) {
						 templateContent = myTemplates.getContent();
					 }else {
						 MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.", "color:red", "TOP");
						 return;
					 }
				}
			}
		//	previewWin$html.setContent(templateContent);
			
			if(users== null ){
				MessageUtil.setMessage("Please provide user-details. ", "color:blue");
				return ;
			}
			
			Utility.showPreview(previewWin$html, users.getUserName(), templateContent);
			
			previewWin.setVisible(true);
			
		}//onClick$parentalPreviewBtnId
		
		// welcome webform, email
		
		private Listbox welcomeEmailsLbId;
		public void onClick$welcomeMessagePreviewBtnId() {
			String templateContent = "";
			
			//previewWin$html.setContent(templateContent);
			
			CustomTemplates customTemplates = welcomeEmailsLbId.getSelectedItem().getValue();
			if(customTemplates == null) {
				templateContent = 	PropertyUtil.getPropertyValueFromDB("welcomeMsgTemplate");
			}else {
				
				
				templateContent = customTemplates.getHtmlText();
				if(templateContent != null && !templateContent.isEmpty()) {
					templateContent = customTemplates.getHtmlText();
				}else if(customTemplates.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE) && customTemplates.getMyTemplateId()!= null) {
					 MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(customTemplates.getMyTemplateId());
					 if(myTemplates!=null) {
						 templateContent = myTemplates.getContent();
					 }else {
						 MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.", "color:red", "TOP");
						 return;
					 }
				}
			}
		//	previewWin$html.setContent(templateContent);
			
			if(users== null ){
				MessageUtil.setMessage("Please provide user-details. ", "color:blue");
				return ;
			}
			
			Utility.showPreview(previewWin$html, users.getUserName(), templateContent);
			
			previewWin.setVisible(true);
			
		}//onClick$welcomeMessagePreviewBtnId
		
		
		
		// editing the Welcome  Webform Email 
		
		
		public void onClick$editMsgBtnId() {
			
			if(GetUser.getUserObj()!=(Users)usersListBxId.getSelectedItem().getValue()) {
				MessageUtil.setMessage("Please login to the user to edit the message template","color:blue");
				return;
			 }
			//logger.info("just Call addnew button");
			logger.info("custom template value "+welcomeEmailsLbId.getSelectedItem().getValue());
			if(welcomeEmailsLbId.getSelectedItem().getValue()!= null) {
				session.setAttribute("editCustomTemplate", welcomeEmailsLbId.getSelectedItem().getValue());
			}else {
				session.setAttribute("editCustomTemplate", "DefaultTemplate");
			}
			session.setAttribute("Mode", "edit");
			session.setAttribute("typeOfEmail",Constants.CUSTOM_TEMPLATE_TYPE_WEBFORMEMAIL);
			session.setAttribute("fromAddNewBtn","contact/webform");
			Redirect.goTo(PageListEnum.CONTACT_MANAGE_AUTO_EMAILS_BEE);
			
		}
		
		
		
		
		
		private Comboitem findOptionalComboitem(Combobox cb, String label) {
			try {
				List<Comboitem> items = cb.getItems();
				for (Comboitem cbitem : items) {
					if(cbitem.getLabel().equals(label)) return cbitem;
				} // for
				
				return null;
			} catch (Exception e) {
				logger.error("Exception  ::", e);
				return null;
			}
		}
		
		
		private Listbox salesUniqueFieldsId,vendorCodeFieldsId,deptCodeFieldsId;
		
		public void mastersSettings(){
			
			Components.removeAllChildren(salesUniqueFieldsId);
			Components.removeAllChildren(vendorCodeFieldsId);
			Components.removeAllChildren(deptCodeFieldsId);
			
			String salesUnqField = null,vendirCodeFiled = null,deptCodeField = null;
			
			Listitem tempListItem = new Listitem("--Select--");
			tempListItem.setParent(salesUniqueFieldsId);
			salesUniqueFieldsId.setSelectedIndex(0);
			
			tempListItem = new Listitem("--Select--");
			tempListItem.setParent(vendorCodeFieldsId);
			vendorCodeFieldsId.setSelectedIndex(0);
			
			tempListItem = new Listitem("--Select--");
			tempListItem.setParent(deptCodeFieldsId);
			deptCodeFieldsId.setSelectedIndex(0);
			
			List<POSMapping> posList = posMappingDao.findAllByUserId(users.getUserId());
			
			for (POSMapping eachPos : posList) {
				Listitem tempItem = new Listitem(eachPos.getCustomFieldName(), eachPos);
				
				 if(eachPos.getMappingType().equals(Constants.POS_MAPPING_TYPE_SALES)) {
					tempItem.setParent(salesUniqueFieldsId);
					if(eachPos.getUniqueInAcrossFiles()!=null) salesUnqField=tempItem.getLabel();
					if(salesUnqField != null && salesUnqField.equals(tempItem.getLabel())) tempItem.setSelected(true);
				} else 	if(eachPos.getMappingType().equals(Constants.POS_MAPPING_TYPE_SKU)) {
					tempItem.setParent(vendorCodeFieldsId);
					if(vendirCodeFiled != null && vendirCodeFiled.equals(tempItem.getLabel())) tempItem.setSelected(true);
					
					
					Listitem tempItem1 = new Listitem(eachPos.getCustomFieldName(), eachPos);
					tempItem1.setParent(deptCodeFieldsId);
					if(deptCodeField != null && deptCodeField.equals(tempItem1.getLabel())) tempItem1.setSelected(true);
				}
			}
		}
			
			
			
		public void onClick$masterPosFTPSettingsBackBtnId() {
			gotoStep(2);
			clearAndDefaultPOSMappSettings();
		} //onClick$posMapBackBtnId()
		
		
	
		
		
		private Div relationMapDivId;
		
		private void prepareRelationMappingData() {
			
			Components.removeAllChildren(relationMapDivId);
			
			List<POSMapping> posList = posMappingDao.findAllByUserId(users.getUserId() );
			List<POSMapping> salesMappingList = new ArrayList<POSMapping>();
			List<POSMapping> inventoryMappingList = new ArrayList<POSMapping>();
			List<POSMapping> contactMappingList = new ArrayList<POSMapping>();
			
			for (POSMapping posMapping : posList) {
				
				if(posMapping.getMappingType().equals(Constants.POS_MAPPING_TYPE_SALES)) {
					salesMappingList.add(posMapping);
				}else if(posMapping.getMappingType().equals(Constants.POS_MAPPING_TYPE_SKU) && posMapping.getUniquePriority() != null 
																							&& posMapping.getUniquePriority() == 1){
					inventoryMappingList.add(posMapping);
				}else if(posMapping.getMappingType().equals(Constants.POS_MAPPING_TYPE_CONTACTS) && posMapping.getUniquePriority() != null 
																								 && posMapping.getUniquePriority() == 1){
					contactMappingList.add(posMapping);
				}
			}
			
			MastersToTransactionMappingsDao mastToTransMappDao =(MastersToTransactionMappingsDao)SpringUtil.getBean("mastersToTransactionMappingsDao");
			List<MastersToTransactionMappings> mastToTransMapList = mastToTransMappDao.findByUserId(users.getUserId());
			
			
			//Create Sales To Contacts Relation ship mapping
			
			if(contactMappingList.size() > 0) {
				
				for (POSMapping eachPosMapp : contactMappingList) {
					
					prepareRelationMapPingRow(Constants.POS_MAPPING_TYPE_CONTACTS, eachPosMapp, salesMappingList , mastToTransMapList);
				}
				
				
			}
			
			
			if(inventoryMappingList.size() > 0) {
				
				for (POSMapping eachPosMapp : inventoryMappingList) {
					
					prepareRelationMapPingRow(Constants.POS_MAPPING_TYPE_SKU, eachPosMapp, salesMappingList , mastToTransMapList);
				}
				
				
			}
			
			
		} // prepareRelationMappingData
		
		
		
		private void prepareRelationMapPingRow(String mappingType, POSMapping posmapObj, List<POSMapping> saleposMappLists, List<MastersToTransactionMappings> mastToTransMapList) {
			
			
			logger.info( "mastToTransMapList Size is ::"+mastToTransMapList);
			Div tempDiv  = new Div();
			tempDiv.setStyle("margin-top:20px");
			
			Label tempLbl = new Label("Select Sales Field :");
			tempLbl.setParent(tempDiv);
			
			Listbox salesListBox = new Listbox();
			salesListBox.setMold("select");
			salesListBox.setWidth("200px");
			Listitem tempItem = new Listitem("--Select--");
			tempItem.setParent(salesListBox);
			
			if(saleposMappLists.size()  > 0) {
				for (POSMapping eachSalePosMappObj : saleposMappLists) {
					
//					logger.info(" CustomFieldName() "+eachSalePosMappObj.getCustomFieldName()+ " POS ID IS ::"+eachSalePosMappObj.getPosId());
					tempItem = new Listitem(eachSalePosMappObj.getCustomFieldName() , eachSalePosMappObj);
					tempItem.setParent(salesListBox);
				}
				
			}
			
			
			salesListBox.setParent(tempDiv);
			
			
			Space tempSpace = new Space();
			tempSpace.setWidth("60px");
			tempSpace.setParent(tempDiv);
			
			tempLbl = new Label(mappingType.equals(Constants.POS_MAPPING_TYPE_CONTACTS) ? "mapped to Contact Field :" : "mapped to Inventory Field :");
			tempLbl.setParent(tempDiv);
			
			Listbox tempListBox = new Listbox();
			tempListBox.setMold("select");
			tempListBox.setWidth("200px");
			tempItem = new Listitem(posmapObj.getCustomFieldName() , posmapObj);
			tempItem.setParent(tempListBox);
			tempListBox.setParent(tempDiv);
			tempListBox.setSelectedIndex(0);
			String typeStr = mappingType.equals(Constants.POS_MAPPING_TYPE_CONTACTS) ? Constants.TYPE_SALES_TO_CONTACTS :Constants.TYPE_SALES_TO_Inventory;
			tempDiv.setAttribute("MAPPINGTYPE", typeStr);
			
			
			boolean isSelected = false;
			if(mastToTransMapList != null && mastToTransMapList.size() > 0) {
				List tempList  = salesListBox.getChildren();
				
				for (Object object : tempList) {
					
					
					Listitem tempLiItem = (Listitem)object;
					POSMapping eachSalePosMappObj = tempLiItem.getValue();
//					logger.info("eachSalePosMappObjis  ::"+eachSalePosMappObj);
					if(eachSalePosMappObj == null) {
						//logger.info("cONTINUE ");
						continue;
					}
//					logger.info("eachSalePosMappObj.getPosId()  "+eachSalePosMappObj.getPosId());
					Long posId = eachSalePosMappObj.getPosId();
					
					for (MastersToTransactionMappings eachObj : mastToTransMapList) {
						
						long chaildId = ((POSMapping)eachObj.getChildId()).getPosId();
//						logger.info("MastersToTransactionMappings .getType()::"+eachObj.getType() + " MATER TO CHALID RELATION ::"+mappingType);
//						logger.info("POS ID is  ::"+posId + " :: Cahild iD is ::"+chaildId+ " :: custFiled Name is "+tempLiItem.getLabel());
						if(mappingType.equals(Constants.POS_MAPPING_TYPE_CONTACTS) && eachObj.getType().equals("Sales to Contacts")
								&& posId == chaildId && (posmapObj.getPosId().longValue() == eachObj.getParentId().getPosId().longValue())) {
							salesListBox.setSelectedItem(tempLiItem);
							salesListBox.setAttribute("MASTER_TO_TRANS_OBJ", eachObj);
							isSelected =true;
						}
						else if(mappingType.equals(Constants.POS_MAPPING_TYPE_SKU) && eachObj.getType().equals("Sales to Inventory")
								 && posId == chaildId && (posmapObj.getPosId().longValue() == eachObj.getParentId().getPosId().longValue())) {
							salesListBox.setSelectedItem(tempLiItem);
							isSelected = true;
							salesListBox.setAttribute("MASTER_TO_TRANS_OBJ", eachObj);
						}
						
						
					}
				}
			}
			
			if(!isSelected){
				salesListBox.setSelectedIndex(0);
			}
			
			
			tempDiv.setParent(relationMapDivId);
		}
		
		
//		onClick$masterSubmitBtnId
		public void saveMasterToTransMappings() {
			
			MastersToTransactionMappingsDao mastersToTranMapDao = (MastersToTransactionMappingsDao)SpringUtil.getBean("mastersToTransactionMappingsDao");
			MastersToTransactionMappingsDaoForDML mastersToTranMapDaoForDML = (MastersToTransactionMappingsDaoForDML)SpringUtil.getBean("mastersToTransactionMappingsDaoForDML");
			List<MastersToTransactionMappings> oldMastList  = mastersToTranMapDao.findByUserId(users.getUserId());
			
			List chaildDivList = relationMapDivId.getChildren();
			MastersToTransactionMappings mastToTranMapObj = null;
			
			List<MastersToTransactionMappings> mastToTransMappLists = new ArrayList<MastersToTransactionMappings>();
			
			for (Object object : chaildDivList) {
				
				Div tempDiv = (Div)object;
				List chaildList = tempDiv.getChildren();
				
				String mappType = (String)tempDiv.getAttribute("MAPPINGTYPE");
				
				//Sales ListBox
				Listbox salesLb = (Listbox)chaildList.get(1);
				if(salesLb.getSelectedIndex() == 0) {
//					logger.info(" >> selected Count is  ::"+salesLb.getSelectedIndex());
//					logger.info(" >> Label is  ::"+salesLb.getSelectedItem().getLabel());
					MessageUtil.setMessage("Please select all the mapping fields.", "color:red");
					return;
				}
				mastToTranMapObj = (MastersToTransactionMappings)salesLb.getAttribute("MASTER_TO_TRANS_OBJ");
				
				
				
				if(mappType.equals(Constants.TYPE_SALES_TO_CONTACTS) && mastToTranMapObj  == null) {
						mastToTranMapObj = new MastersToTransactionMappings();
						mastToTranMapObj.setType(Constants.TYPE_SALES_TO_CONTACTS);
						mastToTranMapObj.setUserId(users.getUserId());
						mastToTranMapObj.setCreatedDate(Calendar.getInstance());
						//if(mastToTranMapObj  == null) {	}
					
				}else if(mappType.equals(Constants.TYPE_SALES_TO_Inventory) && mastToTranMapObj  == null) {
						mastToTranMapObj = new MastersToTransactionMappings();
						mastToTranMapObj.setType(Constants.TYPE_SALES_TO_Inventory);
						mastToTranMapObj.setUserId(users.getUserId());
						mastToTranMapObj.setCreatedDate(Calendar.getInstance());
						//if(mastToTranMapObj  == null) {		}
					
				}
				
				
				//Contacts or Inventory Listbox (setParent Id)
				Listbox masterLb = (Listbox)chaildList.get(4);
				Listitem tempItem = masterLb.getSelectedItem();
				POSMapping posMapObj = tempItem.getValue();
				mastToTranMapObj.setParentId(posMapObj);
				
				//set Chalid Id
				tempItem = salesLb.getSelectedItem();
				posMapObj = tempItem.getValue();
				mastToTranMapObj.setChildId(posMapObj);
				mastToTranMapObj.setLastModifieddDate(Calendar.getInstance());
				mastToTransMappLists.add(mastToTranMapObj);
				
				if(oldMastList == null || oldMastList.size()  ==  0) continue;
				
				Iterator<MastersToTransactionMappings> iter = oldMastList.iterator();
				if(oldMastList!= null && oldMastList.size() > 0) {
					
					for (int i = 0; i < oldMastList.size(); i++) {
						MastersToTransactionMappings tempMasTransObj = oldMastList.get(i);
						if(tempMasTransObj.getId() == mastToTranMapObj.getId()) {
							//logger.info(tempMasTransObj.getId() +" inserted Objecte get Id "+mastToTranMapObj.getId());
							oldMastList.remove(i);
						}
					}
				}
				
				
				
			}
			
			
			
			
			
			/*try { 
				int confirm = Messagebox.show("Are you sure you want to save the settings?", "Prompt", 
						 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm == Messagebox.OK) {
					mastersToTranMapDao.saveByCollection(mastToTransMappLists);
					
					
					if(oldMastList!= null && oldMastList.size() >0) {
						String deleteIds = "";
						for (MastersToTransactionMappings oldObj : oldMastList) {
							deleteIds += deleteIds.trim().length() == 0 ? ""+oldObj.getId() :","+oldObj.getId();
						}
						
						mastersToTranMapDao.deleteByIds(deleteIds);
					}
					
					
					
					MessageUtil.setMessage("Settings saved successfully.","color:green;");
				
				}
			} catch (Exception e) {
				logger.error("Exception  ::", e);
			}*/
			
			try { 
					mastersToTranMapDaoForDML.saveByCollection(mastToTransMappLists);
					
					
					if(oldMastList!= null && oldMastList.size() >0) {
						String deleteIds = "";
						for (MastersToTransactionMappings oldObj : oldMastList) {
							deleteIds += deleteIds.trim().length() == 0 ? ""+oldObj.getId() :","+oldObj.getId();
						}
						
						mastersToTranMapDaoForDML.deleteByIds(deleteIds);
					}
					
					
					
					MessageUtil.setMessage("Settings saved successfully.","color:green;");
				
			} catch (Exception e) {
				logger.error("Exception  ::", e);
			}
			
							
		}//onClick$masterSubmitBtnId()
		
		
		
		
		
		
		
		@Override
		public void onEvent(Event event) throws Exception {

			// TODO Auto-generated method stub
			super.onEvent(event);
			
			if(event.getTarget() instanceof Image) {
				
				Image img =(Image)event.getTarget();
				Row temRow = null;
				String imgAction = (String)img.getAttribute("TYPE");
				logger.info("LOGGER ALERT");
				//String evtType = (String)img.getAttribute("ALERT_DEL");

			/*	if("alert_delete".equalsIgnoreCase(evtType)) {
					Div tempDiv= (Div)( (Div)img.getParent().getParent());
					tempDiv.removeChild(alertMailDivId);
				}*/
				if("ALERT_DEL".equals(imgAction)){
					Div tempDiv= (Div)img.getParent();
					logger.info("ALERT@@");
					alertMailDivId.removeChild(tempDiv);
				}else if(!imgAction.equals("FTP_SETTINGS")) {
					
					temRow = (Row)img.getParent().getParent();
				}				
				
				try {
					
					if(imgAction.equals("POS_ADD_OPTIONAL_VALUE") || imgAction.equals("POS_DEL_OPTIONAL_VALUE")) { // POS CONTACT
						
						Div div = (Div)img.getParent();
						Combobox cb =  (Combobox)div.getFirstChild();
						
						if(cb.getValue()!=null && cb.getValue().trim().length() > 0) {
							String label = cb.getValue().trim();
							
							if(imgAction.equals("POS_ADD_OPTIONAL_VALUE") && findOptionalComboitem(cb, label)==null) {
								cb.appendChild(new Comboitem(label));
								cb.setValue("");
							}
							else if(imgAction.equals("POS_DEL_OPTIONAL_VALUE")) {
								Comboitem delItem = findOptionalComboitem(cb, label);
								if(delItem!=null) {
									cb.removeChild(delItem);
									cb.setValue("");
								}
							}
						}
						
						return;
					} // if


					try {
							
						if(img.getAttribute("TYPE").equals("POS_CONTACT_MAPPING")) { // POS CONTACT
							
							if(temRow.getAttribute("referenceId") == null) { //contactRowsId,,
								contactRowsId.removeChild(temRow);
							}
							else {
								int confirm = Messagebox.show("Are you sure you want to delete the record?",
										"Delete POS Mapping", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
								if(confirm != Messagebox.OK) return;
								
									POSMapping posCustFieldMapping = (POSMapping)temRow.getAttribute("referenceId");
									
									//posMappingDao.delete(posCustFieldMapping); //delete posMapping entry
									posMappingDaoForDML.delete(posCustFieldMapping); //delete posMapping entry
									contactRowsId.removeChild(temRow);
									MessageUtil.setMessage("Deleted successfully.", "green", "TOP");
							}
							
						} else if(img.getAttribute("TYPE").equals("POS_SALES_MAPPING")){ // POS SALES
							
							if(temRow.getAttribute("referenceId") == null) {
								salesRowsId.removeChild(temRow);
							}
							else {
								int confirm = Messagebox.show("Are you sure you want to delete the record?",
										"Delete POS Mapping", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
								if(confirm != Messagebox.OK) return;
								
								POSMapping posCustFieldMapping = (POSMapping)temRow.getAttribute("referenceId");

								//posMappingDao.delete(posCustFieldMapping); //delete posMapping entry
								posMappingDaoForDML.delete(posCustFieldMapping); //delete posMapping entry
								salesRowsId.removeChild(temRow);
								MessageUtil.setMessage("Deleted successfully.", "green", "TOP");
							}
							
						}else if(img.getAttribute("TYPE").equals("POS_SKU_MAPPING")){ // POS SKU
							
							if(temRow.getAttribute("referenceId") == null) {
								skuRowsId.removeChild(temRow);
							}
							else {
								int confirm = Messagebox.show("Are you sure you want to delete the record?",
										"Delete POS Mapping", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
								if(confirm != Messagebox.OK) return;
								
								POSMapping posCustFieldMapping = (POSMapping)temRow.getAttribute("referenceId");
								
								//posMappingDao.delete(posCustFieldMapping); //delete posMapping entry
								posMappingDaoForDML.delete(posCustFieldMapping); //delete posMapping entry
								skuRowsId.removeChild(temRow);
								MessageUtil.setMessage("Deleted successfully.", "green", "TOP");
							}
							
							
						}else if(img.getAttribute("TYPE").equals("FTP_SETTINGS")) {
							temRow = (Row)img.getParent();
							if(temRow.getAttribute("referenceId") == null) {
								ftpSettingsRowsId.removeChild(temRow);
							}
							else {
								int confirm = Messagebox.show("Are you sure you want to delete the record?",
										"Delete POS Mapping", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
								if(confirm != Messagebox.OK) return;
								
								UserPosFTPSettings userPosFTPSettings = (UserPosFTPSettings)temRow.getAttribute("referenceId");
								
								//userPOSFTPSettingsDao.delete(userPosFTPSettings); //delete posMapping entry
								userPOSFTPSettingsDaoForDML.delete(userPosFTPSettings); //delete posMapping entry
								ftpSettingsRowsId.removeChild(temRow);
								MessageUtil.setMessage("Deleted successfully.", "green", "TOP");
							}
							
						}
						
						
						
					} catch (Exception e) {
						logger.error("Exception  ::", e);
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception  ::", e);
				}
			}
			else if(event.getTarget() instanceof Listbox) {
				
				
				Listbox tempListBx = (Listbox) event.getTarget();
				
//				CUST_FIELD_LISTBOX
				if(tempListBx.getAttribute("CUST_FIELD_LISTBOX") != null && 
						tempListBx.getAttribute("CUST_FIELD_LISTBOX").equals("CUST_FIELD_LISTBOX")) {
					
					String selectDataType = tempListBx.getSelectedItem().getLabel();
					
					//render digital receipt column
					
					//Combobox combobox = (Combobox)((Row)tempListBx.getParent()).getChildren().get(2);
					Div drDiv = (Div)((Row)tempListBx.getParent()).getChildren().get(3);
					
					List drChildren = drDiv.getChildren();
					
					Combobox drcombobox = (Combobox)drChildren.get(0);
					Listbox drlistbox = (Listbox)drChildren.get(1);
					//Combobox combobox = new Combobox();
					createDigitalReceiptCombobox(drcombobox, selectDataType, null);
					
					
					//
					
					//logger.info("########## selectDateType #######"+selectDataType);
					Div posdataTypeDiv	 = (Div)(((Row)tempListBx.getParent()).getChildren().get(2));
					Div drdataTypeDiv	 = (Div)(((Row)tempListBx.getParent()).getChildren().get(3));
					//Listbox dataTypeListbox   = (Listbox)dataTypeDiv.getChildren().get(0);
					Listbox dataTypeListbox   = (Listbox)(((Row)tempListBx.getParent()).getChildren().get(1));
					
//					logger.info("row child "+((Row)tempListBx.getParent()).getChildren().get(2));
					if(selectDataType.startsWith(BIRTHDAY) || selectDataType.startsWith(ANNIVERSARY) 
														   || selectDataType.startsWith(SALE_DATE) 
														   || selectDataType.startsWith(CREATED_DATE)) {
						
						dataTypeListbox.setSelectedIndex(1);
						dataTypeListbox.setDisabled(true);
						((Listbox)posdataTypeDiv.getChildren().get(1)).setVisible(true);
						((Listbox)drdataTypeDiv.getChildren().get(1)).setVisible(true);
						//((Listbox)(((Row)tempListBx.getParent()).getChildren().get(1))).setVisible(true);
					}
					/*else if(selectDataType.startsWith(ZIP) || selectDataType.startsWith(MOBILE) 
															|| selectDataType.startsWith(RECEIPT_NUM)) {
						dataTypeListbox.setSelectedIndex(2);
//						dataTypeListbox.setDisabled(false);
						dataTypeListbox.setDisabled(true);
						((Listbox)dataTypeDiv.getChildren().get(1)).setVisible(false);
					}*/
					else if(selectDataType.startsWith(LIST_PRICE) || selectDataType.startsWith(SALE_PRICE)  
																	|| selectDataType.startsWith(TAX) || selectDataType.startsWith(QUANTITY) || selectDataType.startsWith(DISCOUNT)) {
						dataTypeListbox.setSelectedIndex(3);
//						dataTypeListbox.setDisabled(false);
						dataTypeListbox.setDisabled(true);
						((Listbox)posdataTypeDiv.getChildren().get(1)).setVisible(false);
						((Listbox)drdataTypeDiv.getChildren().get(1)).setVisible(false);
						
					} else if(selectDataType.startsWith("UDF")) {
						dataTypeListbox.setSelectedIndex(0);
						dataTypeListbox.setDisabled(false);
//						dataTypeListbox.setDisabled(true);
						((Listbox)posdataTypeDiv.getChildren().get(1)).setVisible(false);
						((Listbox)drdataTypeDiv.getChildren().get(1)).setVisible(false);
					}else {
						dataTypeListbox.setSelectedIndex(0);
//						dataTypeListbox.setDisabled(false);
						dataTypeListbox.setDisabled(true);
						((Listbox)posdataTypeDiv.getChildren().get(1)).setVisible(false);
						((Listbox)drdataTypeDiv.getChildren().get(1)).setVisible(false);
						
					}
					
				}else if(tempListBx.getAttribute("DATATYPE_LISTBOX") != null && 
						tempListBx.getAttribute("DATATYPE_LISTBOX").equals("DATATYPE_LISTBOX")) {//Data type
					
					//Div tempDiv = (Div)tempListBx.getParent();
					//Listbox dateFormatLb= (Listbox)(tempListBx.getParent().getChildren().get(1);
							
					Div posdataTypeDiv	 = (Div)(((Row)tempListBx.getParent()).getChildren().get(2));
					Div drdataTypeDiv	 = (Div)(((Row)tempListBx.getParent()).getChildren().get(3));
					
					Listbox posdateFormatLb   = (Listbox)(posdataTypeDiv.getChildren().get(1));
					Listbox drdateFormatLb   = (Listbox)(drdataTypeDiv.getChildren().get(1));
					
					
					if(tempListBx.getSelectedItem().getLabel().equals("Date")) {
						posdateFormatLb.setSelectedIndex(8);
						drdateFormatLb.setSelectedIndex(8);
						posdateFormatLb.setVisible(true);
						drdateFormatLb.setVisible(true);
					}else{ 
						posdateFormatLb.setVisible(false);
						drdateFormatLb.setVisible(false);
					}
					
				}
				
				
				
			}
			
					
			
			
		} //onEvent
		
		

		private final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		private Random rnd = new Random();
		
		private A generateKeyAnchId,generateAnchId ;
		
		
		/*
		List<UserOrganization> orgList =null;

		public void m1() {
			
			logger.info("in on select of userOrgLbId");
			
			long userOrdId= GetUser.getUserObj().getUserOrganization().getUserOrgId();
			String orgExternalId =GetUser.getUserObj().getUserOrganization().getOrgExternalId();
			
			//List<Users> usersList = usersDao.getUsersByOrgIdAndUserId(userOrgId , GetUser.getUserId()); 
		 orgList = (List<UserOrganization>)updateOptSyncDataDao.findOptSynKey(userOrdId,orgExternalId);
			
			if(orgList == null || orgList.size() == 0) {
				logger.info(" unable to set setting authkey");
				optSynAuthKeyTbId.setValue("");
				generateKeyAnchId.setVisible(true);
			}
			else{
				logger.info("setting authkey");
				optSynAuthKeyTbId.setValue(orgList.get(0).getOptSyncKey());
				optSynAuthKeyTbId.setReadonly(true);
				generateKeyAnchId.setVisible(true);
				
			}
			
			
			
		}//onSelect$userOrgLbId
		*/
		
		
		/*// Generate key
		public void onClick$generateKeyAnchId() {

			logger.info("on click of generate Anch key");
			
			String authkey = optSynAuthKeyTbId.getValue().trim();
			
			if(authkey != null && authkey.trim().length() > 0){
				
				logger.info("returning from here ..........."+authkey +"... Length is :::"+authkey.length());
				return;
			}
			

		       		
			// generate the 16 digit number
			boolean genOptSynKeyFlag = false;
			String 	generateString ="";
			
			while(!genOptSynKeyFlag) {
				
				generateString = randomString(16);
				//change the name 
				List<UserOrganization> userOgList =  updateOptSyncDataDao.findAllByOptSyncName(generateString);
				
				if(userOgList == null || userOgList.size() == 0){
					genOptSynKeyFlag = true;
					logger.info("Generated Plugin id is unquie "+generateString );
					break;
				}
				else{
					generateKeyAnchId.setVisible(true);
					continue;
				}
				
			}
			
			
			logger.debug("User ::::::::::::"+ generateString +"<<<<<<<< Length >>>>>.:"+generateString.length());

			
			optSynAuthKeyTbId.setValue(generateString);
			optSynAuthKeyTbId.setReadonly(true);
			generateKeyAnchId.setVisible(false);
			
			
		}//on click of key
*/
		
		// Generate key
		public void onClick$generateKeyAnchId() {
			logger.info("on click of generate Anch key");
			
			optSynAuthKeyTbId.getValue().trim();
					
			// generate the 16 digit number
			boolean genOptSynKeyFlag = false;
			String 	generateString ="";
				
			while(!genOptSynKeyFlag) {
						
				generateString = randomString(16);
				//change the name 
				List<UserOrganization> userOgList =  updateOptSyncDataDao.findAllByOptSyncName(generateString);
						
				if(userOgList == null || userOgList.size() == 0){
						genOptSynKeyFlag = true;
						logger.info("Generated Plugin id is unquie "+generateString );
						break;
					}
				else{
						generateKeyAnchId.setVisible(true);
						continue;
					}
			}//while loop
			logger.debug("User ::::::::::::"+ generateString +"<<<<<<<< Length >>>>>.:"+generateString.length());
				optSynAuthKeyTbId.setValue(generateString);
				optSynAuthKeyTbId.setReadonly(true);
				generateKeyAnchId.setVisible(false);
					
					
		}//on click of key
		

		//generate String
		private String randomString( int len ) 
			{
				StringBuilder sb = new StringBuilder( len );
				for( int i = 0; i < len; i++ ) 
					sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
				return sb.toString();
			}
		
		
		
	
		
		
		
		//validating optsync key
		
	/*	public boolean validateOptSyncKey(String optSyncAuth ){
			
			logger.info("validating optsync key");
			boolean isTrue =false;
			try{
				if (optSyncAuth.trim().equals("") ||  optSyncAuth.length() < 16) {
	
					MessageUtil.setMessage(
							"Please provide OptSync Authentication Key.",
							"color:red", "TOP");
					isTrue =  false;
					return isTrue;
				}
	
				if (optSyncAuth.length() == 0 && !Utility.validateName(optSyncAuth)) {
					MessageUtil.setMessage(
							"Please enter Valid 16 Character OptSync Authentication Key.",
							"color:red", "TOP");
					isTrue=  false;
					return isTrue;
				}
				
				
				if(optSyncAuth.length() == 16) {
					UserOrganization userOrgObj = users.getUserOrganization();
					if(userOrgObj == null) {
						logger.error("User org not existed ... and returning");
						isTrue =  false;
						return isTrue;
					}
					
					List<UserOrganization> userOrgList = updateOptSyncDataDao.findAllByOptSyncName(optSyncAuth);
					
					if(userOrgList == null || userOrgList.isEmpty()){
						logger.info("usr org list is::"+userOrgList);
						isTrue =  true;
						return isTrue;
					}
					boolean existOptSynKey = false;
					for (UserOrganization eachOrg : userOrgList) {
						if(eachOrg.getUserOrgId() != userOrgObj.getUserOrgId().longValue()) {
							MessageUtil.setMessage(
									"OptSync Authentication Key already exist. Please enter another OptSyncKey.",
									"color:red", "TOP");
							isTrue=  false;
							return isTrue;
						}
					}	
					
					if(existOptSynKey) {
						
					}
				}
				isTrue=  true;
			 }
			 catch(Exception e){
				 
				 logger.error("Exception ::::::::::::" ,e);
				 isTrue =  false;
			 }
				return isTrue;

			
		}
		*/
		
		public boolean validateOptSyncKey(String optSyncAuth ){
			
			logger.info("validating optsync key");
			boolean isTrue =false;
			try{
				if (optSyncAuth.trim().equals("") ||  optSyncAuth.length() < 16) {

					MessageUtil.setMessage(	"Please enter 16 Character  OptSync Authentication Key.","color:red", "TOP");
					isTrue =  false;
					return isTrue;
				}

				if (optSyncAuth.length() == 0 && !Utility.validateName(optSyncAuth)) {
					MessageUtil.setMessage("Please enter Valid 16 Character OptSync Authentication Key.","color:red", "TOP");
					isTrue=  false;
					return isTrue;
				}
				
				
				
				if(optSyncAuth.length() == 16) {
					UserOrganization userOrgObj = users.getUserOrganization();
					if(userOrgObj == null) {
						logger.error("User org not existed ... and returning");
						isTrue =  false;
						return isTrue;
					}
					
					List<UserOrganization> userOrgList = updateOptSyncDataDao.findAllByOptSyncName(optSyncAuth);
					
					if(userOrgList == null || userOrgList.isEmpty()){
						logger.info("usr org list is::"+userOrgList);
						isTrue =  true;
						return isTrue;
					}
				/*if(optSyncAuth.length() == 16) {
					if (!validateAccountId(userOrgLbId)) {
						return false;
					}
					
					String orgExternalId=userOrgLbId.getSelectedItem().getLabel();
					long userOrgId=userOrgLbId.getSelectedItem().getValue();
					
					List<UserOrganization> orgList = (List<UserOrganization>)updateOptSyncDataDao.findOptSynKey(userOrgId,orgExternalId);
					if(orgList == null) {
						logger.error("User org not existed ... and returning");
						isTrue =  false;
						return isTrue;
					}
					
					List<UserOrganization> userOrgList = updateOptSyncDataDao.findAllByOptSyncName(optSyncAuth);
					//int size =0;
					if(userOrgList == null || userOrgList.isEmpty()){
						logger.info("usr org list is::"+userOrgList);
						isTrue =  true;
						return isTrue;
					}*/
					
					boolean existOptSynKey = false;
					
					for (UserOrganization eachOrg : userOrgList) {
						if(eachOrg.getUserOrgId() ==  userOrgObj.getUserOrgId().longValue()) {
							existOptSynKey = true;
						}
					}	
					
					if(!existOptSynKey) {
						logger.error("OptSyncKey already exists, please provide another optsync key");
						MessageUtil.setMessage("OptSync Authentication Key already exist. Please enter another OptSyncKey.","color:red", "TOP");
						generateKeyAnchId.setVisible(true);
						optSynAuthKeyTbId.setReadonly(false);
						isTrue=  false;
						return isTrue;
					}
				}
				isTrue=  true;
			 }
			 catch(Exception e){
				 
				 logger.error("Exception ::::::::::::" ,e);
				 isTrue =  false;
			 }
				return isTrue;

			
		}

		private void salesDataAlertSetting(){
			
			
			List<UserPosFTPSettings> usersFtpSettings = userPOSFTPSettingsDao.salesSettingsFindByUserId(( (Users) usersListBxId.getSelectedItem().getValue()).getUserId());
			if(usersFtpSettings != null) {
				
						UserPosFTPSettings usersSetting = usersFtpSettings.get(0);
						logger.info("===salesDataAlertSetting function===" + usersSetting.getUserId());
						
						boolean isSalesDataAlert = usersSetting.isCheckAlert();
						
						int mailIdCount = 1;
						
						if(isSalesDataAlert)  {
							
							Components.removeAllChildren(alertMailDivId);
							
							String toEmailIds = usersSetting.getAlertEmailAddress();
							
							if( (!toEmailIds.isEmpty()) || toEmailIds.contains(Constants.ADDR_COL_DELIMETER)) {
								
								String mailIdsArray[] = toEmailIds.split(Constants.ADDR_COL_DELIMETER);
								
								for(String mailId : mailIdsArray) {
									
									if(mailIdCount == 1) 
										alertMailTxtbxId.setText(mailId);
									else {
											Div alertDiv = new Div();
											Textbox alertTextBx = new Textbox();
											alertTextBx.setText(mailId);
											alertTextBx.setParent(alertDiv);
											alertTextBx.setWidth("250px");
											alertTextBx.setStyle("margin-left:85px;margin-top: 10px;margin-right:7px ;");
											
											Image delImg = new Image();
											delImg.setAttribute("TYPE", "ALERT_DEL");
											delImg.setSrc("/images/action_delete.gif");
											delImg.setStyle("cursor:pointer;color:#2886B9;font-weight:bold;text-decoration: underline;");
											delImg.setTooltiptext("Delete");
											delImg.addEventListener("onClick", this);
											delImg.setParent(alertDiv);
											
											alertDiv.setParent(alertMailDivId);
									}
									
									mailIdCount ++;
								}
								
						    }
							
							int checkPeriodValue = usersSetting.getCheckProcessPeriod();
							
							for(int i=0; i< alertConfigLstbxId.getItemCount();i++) {
								
								
								if(checkPeriodValue == Integer.parseInt((alertConfigLstbxId.getItemAtIndex(i).getValue().toString()))) {
									
									alertConfigLstbxId.setSelectedIndex(i);
									break;
								}
							}
							
						}
						else{
							logger.info("user ftp settings is null");
							Components.removeAllChildren(alertMailDivId);
							alertConfigLstbxId.setSelectedIndex(2);
							alertMailTxtbxId.setValue(PropertyUtil.getPropertyValueFromDB("AlertToEmailId"));
						}
			}
			else {
				
				logger.info("user ftp settings is null");
				Components.removeAllChildren(alertMailDivId);
				alertConfigLstbxId.setSelectedIndex(2);
				alertMailTxtbxId.setValue(PropertyUtil.getPropertyValueFromDB("AlertToEmailId"));
				
				}
		}
		
		public void onSelect$welcomeEmailsLbId() {
			
			

			 
			 if(welcomeEmailsLbId.getSelectedIndex() == 0 && 
					 welcomeEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase(defaultStr)) {
				 
				 editMsgBtnId.setVisible(false);
				 welcomeMessagePreviewBtnId.setVisible(true);
				 previewIconId.setVisible(true);
				 
				 
			 }else if(welcomeEmailsLbId.getSelectedIndex() == 0 && 
					 welcomeEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase(selectMsgStr)) {
				 
				 editMsgBtnId.setVisible(false);
				 welcomeMessagePreviewBtnId.setVisible(false);
				 previewIconId.setVisible(false);
				 
				 
			 }else{
				 
				 editMsgBtnId.setVisible(true);
				 welcomeMessagePreviewBtnId.setVisible(true);
				 previewIconId.setVisible(true);
			 }
			
		}
		
		public void onSelect$optInEmailsLbId() {
			
			if(optInEmailsLbId.getSelectedIndex() == 0 && 
					 optInEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase(defaultStr)) {
				 
				 previewBtnId.setVisible(true);
					optInPreviewIconId.setVisible(true);
				 
			 } else if(optInEmailsLbId.getSelectedIndex() == 0 && 
					 optInEmailsLbId.getSelectedItem().getLabel().equalsIgnoreCase(selectMsgStr)) {
				 
				 previewBtnId.setVisible(false);
					optInPreviewIconId.setVisible(false);
				 
			 }else{
				 
				 previewBtnId.setVisible(true);
					optInPreviewIconId.setVisible(true);
			 }
		}
		
		public void onSelect$parentalConsentEmailLbId() {

			
			 if(parentalConsentEmailLbId.getSelectedIndex() == 0 && 
					 parentalConsentEmailLbId.getSelectedItem().getLabel().equalsIgnoreCase(defaultStr)) {
				 
				 parentalPreviewBtnId.setVisible(true);
				parentalPreviewIconId.setVisible(true);
				 
				 
			 }else if(parentalConsentEmailLbId.getSelectedIndex() == 0 && 
					 parentalConsentEmailLbId.getSelectedItem().getLabel().equalsIgnoreCase(selectMsgStr)) {
				 
				 parentalPreviewBtnId.setVisible(false);
				parentalPreviewIconId.setVisible(false);
				 
				 
			 }else{
				 
				 parentalPreviewBtnId.setVisible(true);
				 parentalPreviewIconId.setVisible(true);
			 }
	
		}
		
		public void onSelect$LoyaltyOptinEmailLbId() {
			
			
			if(LoyaltyOptinEmailLbId.getSelectedIndex() == 0 && 
					LoyaltyOptinEmailLbId.getSelectedItem().getLabel().equalsIgnoreCase(defaultStr)) {
				 
				loyaltyPreviewBtnId.setVisible(true);
				loyaltyPreviewIconId.setVisible(true);
				 
				 
			 }else if(LoyaltyOptinEmailLbId.getSelectedIndex() == 0 && 
					 LoyaltyOptinEmailLbId.getSelectedItem().getLabel().equalsIgnoreCase(selectMsgStr)) {
				 
				 loyaltyPreviewBtnId.setVisible(false);
					loyaltyPreviewIconId.setVisible(false);
				 
				 
			 }else{
				 
				 loyaltyPreviewBtnId.setVisible(true);
				 loyaltyPreviewIconId.setVisible(true);
			 }
	
			
		}
		
}//EOF
