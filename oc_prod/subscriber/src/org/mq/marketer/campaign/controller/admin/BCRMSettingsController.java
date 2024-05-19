package org.mq.marketer.campaign.controller.admin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.MastersToTransactionMappings;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.UserPosFTPSettings;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MailingListDaoForDML;
import org.mq.marketer.campaign.dao.MastersToTransactionMappingsDao;
import org.mq.marketer.campaign.dao.MastersToTransactionMappingsDaoForDML;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.POSMappingDaoForDML;
import org.mq.marketer.campaign.dao.UserPosFTPSettingsDao;
import org.mq.marketer.campaign.dao.UserPosFTPSettingsDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

public class BCRMSettingsController extends GenericForwardComposer {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private UsersDao usersDao=  null;
	private UsersDaoForDML usersDaoForDML=  null;
	MailingListDao mailingListDao = null;
	MailingListDaoForDML mailingListDaoForDML = null;
	POSMappingDao posMappingDao = null;
	POSMappingDaoForDML posMappingDaoForDML = null;
	UserPosFTPSettingsDao userPOSFTPSettingsDao = null;
	UserPosFTPSettingsDaoForDML userPOSFTPSettingsDaoForDML = null;
	
	Div posMappingDivId;
	Groupbox createListGroupBoxId, posFTPSettingsGroupBoxId,mastersBCRMFTPSettingsGroupBoxId;
	
	private Listbox orgListBxId,usersListBxId;
	private Users users;
	private MailingList mailingList;
	private Textbox posListTxtBoxId,posListDescTbId;
	private Checkbox doubleOptInCbId,parentalConsentCbId;
	private Rows contactRowsId,salesRowsId,skuRowsId, homesPassedRowsId,ftpSettingsRowsId;
	private CustomTemplatesDao customeTemplatesDao ;
	private Div doubleOptLtDiv,parentalOptLtDiv,posMapContDivId,posMapSalesDivId,posMapSKUDivId, posMapHomesPassedDivId;
	private Listbox optInEmailsLbId,parentalConsentEmailLbId;
	//private Listbox listTypeLbId;
	private Toolbarbutton posContTbBtnId, posSalesTbBtnId, posSKUTbBtnId, homesPassedTbBtnId;
	private Listbox TimesZonesLbId;
	
	private Set<Long> listIdsSet; 
	List<String> scopeCustfieldList = new ArrayList<String>();
	List<String> scopePosAttrList = new ArrayList<String>();
	
	private static String CREATED_DATE = "Created Date";
	private static String ANNIVERSARY = "Anniversary";
	private static String BIRTHDAY = "BirthDay";
	private static String ZIP = "ZIP";
	private static String MOBILE = "Mobile";
	private static String SALE_DATE ="Sale Date";
	private static String LIST_PRICE = "List Price";
	private static String SALE_PRICE ="Sale Price";
	private static String TAX = "Tax";
	private static String QUANTITY = "Quantity";
	private static String RECEIPT_NUM = "Receipt Number";
	private static String ADDRESSUINTID = "Addressunit Id";
	
	private static String selectStr = "--select--";
	private static String[] udfSetStr ={"UDF1" ,"UDF2" ,"UDF3","UDF4", "UDF5","UDF6","UDF7","UDF8",
										"UDF9","UDF10",	"UDF11","UDF12","UDF13","UDF14","UDF15"};
	
	private static String contctGenFieldStr = PropertyUtil.getPropertyValue("defaultContactMapFieldList");
	private static String salesGenFieldStr = PropertyUtil.getPropertyValue("defaultSalesFieldLst");
	private static String skuGenfieldStr = PropertyUtil.getPropertyValue("defaultSKUFieldList");
	private static String homesPassedGenfieldStr = PropertyUtil.getPropertyValue("defaultHomesPassedFieldList");
	
	
	private static Map<String, String> genFieldContMap = new HashMap<String, String>();
	/*defaultSalesFieldLst = CustomerID,ReceiptNumber,SaleDate,Qty,SalePrice,Tax,PromoCode,StoreNumber,SKU,TenderType
			defaultSKUFieldList = StoreNumber,SKU,Description,ListPrice,ItemCategory*/
	private static Map<String, String> genFieldSalesMap = new HashMap<String, String>();
	
	private static Map<String, String> genFieldSKUMap = new HashMap<String, String>();
	private static Map<String, String> genHomesPassedMap = new HashMap<String, String>();
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
		genFieldContMap.put("ADDRESS_UNIT_ID" , "Addressunit ID" );
		genFieldContMap.put("Gender" , "Gender");
		genFieldContMap.put("HomeStore" , "Home Store");
		genFieldContMap.put("BirthDay" , "BirthDay");
		genFieldContMap.put("Anniversary" , "Anniversary");
		genFieldContMap.put("CreatedDate" , "Created Date");
		
		genFieldSalesMap.put("CustomerID", "Customer ID");
		genFieldSalesMap.put("CONTRACT_NO", "External ID");
		genFieldSalesMap.put("ReceiptNumber", "Receipt Number");
		genFieldSalesMap.put("SaleDate", "Sale Date");
		genFieldSalesMap.put("Qty", "Quantity");
		genFieldSalesMap.put("SalePrice", "Sale Price");
		genFieldSalesMap.put("Tax", "Tax");
		genFieldSalesMap.put("PromoCode", "Promo Code");
		genFieldSalesMap.put("StoreNumber", "Store Number");
		genFieldSalesMap.put("SKU", "SKU");
		genFieldSalesMap.put("TenderType", "Tender Type");
		genFieldSalesMap.put("Doc_Sid", "UDF10");
		genFieldSalesMap.put("Item_Sid", "UDF11");
		
		genFieldSKUMap.put("StoreNumber", "Store Number");
		genFieldSKUMap.put("SKU", "SKU");
		genFieldSKUMap.put("Description", "Description");
		genFieldSKUMap.put("ListPrice", "List Price");
		genFieldSKUMap.put("ItemCategory", "Item Category");
		genFieldSKUMap.put("Item_Sid", "UDF11");
		
		genHomesPassedMap.put("ADDRESSUNITID" , "Addressunit Id");
		genHomesPassedMap.put("Country" , "Country");
		genHomesPassedMap.put("State" , "State");
		genHomesPassedMap.put("District" , "District");
		genHomesPassedMap.put("City" , "City");
		genHomesPassedMap.put("ZIP" , "ZIP");
		genHomesPassedMap.put("Area" , "Area");
		genHomesPassedMap.put("Street" , "Street");
		genHomesPassedMap.put("Address One" , "Address One");
		genHomesPassedMap.put("Address Two" , "Address Two");
		
		
	}
	
	
	private static String[] defaultFieldArray = StringUtils.split(contctGenFieldStr, ','); 
	private static String[] genSalesArray = StringUtils.split(salesGenFieldStr, ','); 
	private static String[] genSkuArr  = StringUtils.split(skuGenfieldStr, ','); 
	private static String[] genHomesPassedArr  = StringUtils.split(homesPassedGenfieldStr, ','); 
	
	private boolean isAdmin;
	Session sessionScope = null;
	private Div userOrgListDivId;
	public BCRMSettingsController() {
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
     	PageUtil.setHeader("BCRM Settings","",style,true);
     	customeTemplatesDao = (CustomTemplatesDao)SpringUtil.getBean("customTemplatesDao");
    	
     	
     	
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
//		fileTypeComboBxId.setSelectedIndex(0);
		sessionScope = Sessions.getCurrent();
		isAdmin = (Boolean)sessionScope.getAttribute("isAdmin");
		listIdsSet = (Set<Long>)sessionScope.getAttribute(Constants.LISTIDS_SET);
		//get The UserOrganization List
		if(isAdmin) {
			setUserOrg();
			userOrgListDivId.setVisible(true);
		}
		else {
			users = GetUser.getUserObj();
			//get DoubleOpt custom templates of the User
			getWelcomeTemplateList();
			//get Parental Consent templates of  the User
			getParentalConsentTemplateList();
			defaultPOSListSettings(users);
			
			
			/*if(defaultPOSListSettings(users)) {
				
				clearAndDefaultPOSMappSettings();
			
				
			}//if
*/			
			
		}//else
		
		
	}
	
	
	
	private void setUserOrg() {

		
		List<UserOrganization> orgList	= usersDao.findAllOrganizationsBCRM();
		
		if(orgList == null) {
			logger.debug("no organization list exist from the DB...");
			return ;
		}
		
		Listitem tempList = new Listitem(selectStr);
		tempList.setParent(orgListBxId);
		
		Listitem tempItem = null;
		
		for (UserOrganization userOrganization : orgList) {
			
			//set Organization Name
			if(userOrganization.getOrganizationName() == null || userOrganization.getOrganizationName().trim().equals("")) continue;
			
			tempItem = new Listitem(userOrganization.getOrganizationName().trim(),userOrganization.getUserOrgId());
			tempItem.setParent(orgListBxId);
		} // for
		orgListBxId.setSelectedIndex(0);
		
		
	} // setUserOrg()
	
	
	public void onSelect$orgListBxId() {
		
		Components.removeAllChildren(usersListBxId);
		posListTxtBoxId.setValue("");
		posListTxtBoxId.setDisabled(false);
		posListDescTbId.setValue("");
		doubleOptInCbId.setChecked(false);
		doubleOptInCbId.setDisabled(false);
		parentalConsentCbId.setChecked(false);
		parentalOptLtDiv.setVisible(false);
		doubleOptLtDiv.setVisible(false);
		parentalConsentCbId.setDisabled(false);
		//posMapSalesDivId.setVisible(false);
		/*listTypeLbId.setDisabled(false);
		listTypeLbId.setSelectedIndex(0);*/
		
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
		
		usersListBxId.setSelectedIndex(0);
		
	} // onSelect$orgListBxId
	
	
	public void onSelect$usersListBxId() {
		
		posListTxtBoxId.setValue("");
		posListTxtBoxId.setDisabled(false);
		posListDescTbId.setValue("");
		doubleOptInCbId.setChecked(false);
//		doubleOptInCbId.setDisabled(false);
		
		parentalConsentCbId.setChecked(false);
//		parentalConsentCbId.setDisabled(false);
		
		if(usersListBxId.getSelectedItem().getLabel().equals(selectStr)) {
			users = null;
			mailingList = null;
			
			return;
		}
		users = (Users)usersListBxId.getSelectedItem().getValue();
		
		
		logger.debug("user Object is Exist"+users);
		
		
		if(users == null) return;
		
		//get DoubleOpt custom templates of the User
		getWelcomeTemplateList();
		//get Parental Consent templates of  the User
		getParentalConsentTemplateList();
		defaultPOSListSettings(users);
				
		//POS FTP Settings
		defaultFTPSettings();
	}
	
	/*public void onSelect$listTypeLbId() {
		

		if(isAdmin) {
			if(usersListBxId.getSelectedItem().getLabel().equals(selectStr)) {
				users = null;
				mailingList = null;
				return;
			}
			
			users = (Users)usersListBxId.getSelectedItem().getValue();
		}
		else{
			
			users = GetUser.getUserObj();
		}
		
		logger.debug("user Object is Exist"+users);
		
		
		if(users == null) return;
		
		
		
		//get DoubleOpt custom templates of the User
		getWelcomeTemplateList();
		//get Parental Consent templates of  the User
		getParentalConsentTemplateList();
		
		//String listType = listTypeLbId.getSelectedItem().getValue();
		
		//POS Mapping
		
		defaultPOSListSettings(users);
		
	
		
	}
	*/
	
	
	
	private boolean defaultPOSListSettings(Users userObj) {
		try {
			mailingList = mailingListDao.findUserBCRMList(userObj);
			
			if(mailingList == null) {
				logger.debug("no POS / BCRM mailing list existed ..");
				return false;
			}
			
			posListTxtBoxId.setValue(mailingList.getListName());
			posListTxtBoxId.setDisabled(true);
			posListDescTbId.setValue(mailingList.getDescription() == null ? "":mailingList.getDescription());
			posListDescTbId.setAttribute("OLDVALUE", mailingList.getDescription() == null ? "":mailingList.getDescription());
			
			//Double Opt Checkbox
			doubleOptInCbId.setChecked(mailingList.getCheckDoubleOptin());
			doubleOptInCbId.setAttribute("OLDVALUE", mailingList.getCheckDoubleOptin());
			optInEmailsLbId.setAttribute("OLDVALUE", selectMsgStr);
			if(doubleOptInCbId.isChecked()) {
				doubleOptLtDiv.setVisible(!doubleOptLtDiv.isVisible());
				Long custmTemplId = mailingList.getCustTemplateId();
				if(custmTemplId != null) {
					// get object of customtemplate
					
					for(Listitem item:optInEmailsLbId.getItems()) {
						
						CustomTemplates curItemTemp = (CustomTemplates)item.getValue();
						
						if(curItemTemp == null) continue;
						
						if(curItemTemp.getTemplateId().longValue() == mailingList.getCustTemplateId().longValue()){
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
			}
//			doubleOptInCbId.setDisabled(true);
			onSelect$optInEmailsLbId();
			//Parental Checkbox
			parentalConsentCbId.setChecked(mailingList.isCheckParentalConsent());
			parentalConsentCbId.setAttribute("OLDVALUE", mailingList.isCheckParentalConsent());
			
			if(parentalConsentCbId.isChecked()) {
				parentalOptLtDiv.setVisible(!parentalOptLtDiv.isVisible());
				
				Long custmTemplId = mailingList.getConsentCutomTempId();
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
			return true;
		} catch (Exception e) {
			logger.error("Exception  ::", e);
			return false;
		}
		
	} //defaultSettings
	
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
	
	
	
	
	private void gotoStep(int step) {
		
		createListGroupBoxId.setVisible(step==1);
		posMappingDivId.setVisible(step==2);
		posFTPSettingsGroupBoxId.setVisible(step==3);
		mastersBCRMFTPSettingsGroupBoxId.setVisible(step==3);
	} // gotoStep
	
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
	
	//If Pos List not exist for selected user sets default mapping for created new list other wise get mapping fields for existed Mailing list
	public void onClick$creatListNxtBtnId() {
		
		
		if(isAdmin && userOrgListDivId.isVisible() && orgListBxId.getSelectedIndex() == 0) {
			users =null;
			MessageUtil.setMessage("Please select organization.","color:red","TOP");
			return;
		}
		
		if(isAdmin && userOrgListDivId.isVisible() && users == null){
			MessageUtil.setMessage("Please select user.","color:red","TOP");
			return ;
		}
		
		if(validateSelectedTemplate()) {
			return;
		}
		/*if(listTypeLbId.getSelectedIndex() == 0) {
			
			MessageUtil.setMessage("Please select the list type.","color:red;","TOP");
			return ;
		}*/
		//String listType = listTypeLbId.getSelectedItem().getValue();
		
		String posListStr = posListTxtBoxId.getValue().trim();
		
		if(posListStr == null || posListStr.trim().length() == 0){
			MessageUtil.setMessage("Please specify list name.","color:red","TOP");
			return ;
		}else if(!Utility.validateName(posListStr)){
			MessageUtil.setMessage("Please enter valid list name. Special characters are not allowed.","color:red","TOP");
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
				
				
				Long optInCustTemplateId = null;
				Long consentCustTemplateId = null;
				
					
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
					logger.debug("Custom template Id is :"+consentCustTemplateId);
					mailingList = new MailingList(posListStr, posListDescTbId.getValue(),cal,
								"Active", cal,cal,false, false,null,users,
								optin, optInCustTemplateId, consent, consentCustTemplateId);
					
					long mlbit = mailingListDao.getNextAvailableMbit(users.getUserId());
					
					
					if(mlbit == 0l){
						MessageUtil.setMessage("You have exceeded limit on maximum number of lists(60). " +
								"Please delete one or more lists to create a new list.", "red");
						return;
					}
					
					mailingList.setMlBit(mlbit);
					mailingList.setListSize(0L);
				
			/*//
			if(doubleOptInCbId.isChecked() && (optInEmailsLbId.getSelectedIndex() > 0) && 
						parentalConsentCbId.isChecked() && (parentalConsentEmailLbId.getSelectedIndex() > 0)) {
				
				posML =  new MailingList(posListStr, posListDescTbId.getValue(), cal,
					"Active", cal, cal, false, false, null, users, doubleOptInCbId.isChecked(), custTemplateId, parentalConsentCbId.isChecked(), consentCutomTempId);
			}else 
				posML =  new MailingList(posListStr, posListDescTbId.getValue(),cal,
						"Active", cal,cal,false, false,null,users,doubleOptInCbId.isChecked(),parentalConsentCbId.isChecked());
			*/
			
			
			mailingList.setListType(Constants.MAILINGLIST_TYPE_HOMESPASSED);
			
		} 
		else {
			
			String oldVal = (String)posListDescTbId.getAttribute("OLDVALUE");
			Boolean doubleOptFlag =(Boolean)doubleOptInCbId.getAttribute("OLDVALUE");
			Boolean parentalFlag = (Boolean)parentalConsentCbId.getAttribute("OLDVALUE");
			String doubleOptOldValStr = (String)optInEmailsLbId.getAttribute("OLDVALUE");
			String parentalConsOlValStr = (String)parentalConsentEmailLbId.getAttribute("OLDVALUE");
			
			if(oldVal!=null && oldVal.trim().equals(posListDescTbId.getValue().trim())
							&& doubleOptFlag.booleanValue() == doubleOptInCbId.isChecked()
							&& parentalFlag.booleanValue() == parentalConsentCbId.isChecked()
							&& doubleOptOldValStr.equals(optInEmailsLbId.getSelectedItem().getLabel())
							&& parentalConsOlValStr.equals(parentalConsentEmailLbId.getSelectedItem().getLabel())) {
				clearAndDefaultPOSMappSettings();
				gotoStep(2);
				return;
			}
			
			logger.info("after creation ==== >");
			//set Description
			mailingList.setDescription(posListDescTbId.getValue());
			//Double Opt in Check
			mailingList.setCheckDoubleOptin(doubleOptInCbId.isChecked());
			
			//Parental Consent Check
			mailingList.setCheckParentalConsent(parentalConsentCbId.isChecked());
			
			//set doubleOPt customTemplates
			if(doubleOptInCbId.isChecked()) {
				
				Long optInCustTemplateId = null;
				Listitem optInListitem = optInEmailsLbId.getSelectedItem();
				
				if(optInListitem.getIndex() != 0) {
					CustomTemplates optInCustomTemplates = (CustomTemplates)optInListitem.getValue();
					optInCustTemplateId = optInCustomTemplates.getTemplateId();
				}
				mailingList.setCustTemplateId(optInCustTemplateId);
			}else {
				mailingList.setCustTemplateId(null);
			}
			
			//set ParentalConsent Custom Template
			if(parentalConsentCbId.isChecked()) {
				Long consentCustTemplateId = null;
				Listitem consentListitem1 = parentalConsentEmailLbId.getSelectedItem();
				
				if(consentListitem1.getIndex() != 0) {
					CustomTemplates consentCustomTemplates = (CustomTemplates)consentListitem1.getValue();
						consentCustTemplateId = consentCustomTemplates.getTemplateId();
				}
				mailingList.setConsentCutomTempId(consentCustTemplateId);
			}else {
				mailingList.setConsentCutomTempId(null);
			}
			
		}
		
		mailingListDaoForDML.saveOrUpdate(mailingList);
		if(!isAdmin){
		listIdsSet.add(mailingList.getListId());
		}
		listIdsSet = (Set<Long>)session.setAttribute(Constants.LISTIDS_SET, listIdsSet);
		
		logger.debug("pos List saved Successfully");
		
		posListTxtBoxId.setDisabled(true);
		
		MessageUtil.setMessage(posListStr+" List saved successfully","color:blue","TOP");
		
		if(!isExists) {
			logger.debug("set default POS mapping for the new POS List");
			Set<String> set  = null;
			set =  genFieldContMap.keySet();
			
			List contMapList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", mailingList.getUsers().getUserId());
			if(contMapList != null && contMapList.size() == 0) {
				for (String keyStr : set) {
					defaultGenFieldMapp(keyStr ,Constants.POS_MAPPING_TYPE_CONTACTS);
				}
			}
			set =  genFieldSalesMap.keySet();
			for (String keyStr : set) {
				defaultGenFieldMapp(keyStr ,Constants.POS_MAPPING_TYPE_SALES);
			}
			
			set =  genFieldSKUMap.keySet();
			for (String keyStr : set) {
				defaultGenFieldMapp(keyStr ,Constants.POS_MAPPING_TYPE_SKU);
			}
			
			set =  genHomesPassedMap.keySet();
			
			logger.info("key Set:: "+set);
			for (String keyStr : set) {
				defaultGenFieldMapp(keyStr ,Constants.MAILINGLIST_TYPE_HOMESPASSED);
			}
			
			clearAndDefaultPOSMappSettings();
		}
		
		gotoStep(2);
		
	} //onClick$creatListNxtBtnId
	
	
	//create Settings of All general Field Map we creating a new List
	private void defaultGenFieldMapp(String genFieldLbl, String mapType) {
		
		try {
			POSMapping posMapping = new POSMapping();
			
			//POS Attr
			posMapping.setPosAttribute(genFieldLbl);
			
//			posMapping.setDisplayLabel(genFieldLbl);
			String optCulFieldStr = "";
			if(mapType.equals(Constants.POS_MAPPING_TYPE_CONTACTS)) {
				
				optCulFieldStr = genFieldContMap.get(genFieldLbl);
				
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
				
				//DataTypes
				if(genFieldLbl.equals(ZIP) || genFieldLbl.equals("MobilePhone")) {
					posMapping.setDataType("Number");
				}
				else if(genFieldLbl.equals(BIRTHDAY) || genFieldLbl.equals(ANNIVERSARY)
						|| genFieldLbl.equals("CreatedDate")) {
					posMapping.setDataType("Date(MM/dd/yyyy HH:mm:ss)");
				}else {
					posMapping.setDataType("String");
				}
				
				if(genFieldLbl.equals("CustomerID")) {
					posMapping.setUniquePriority(1);
				}
				
			}
			else if(mapType.equals(Constants.POS_MAPPING_TYPE_SALES)) {
				
				optCulFieldStr = genFieldSalesMap.get(genFieldLbl);
				
				//Display Label
				if(optCulFieldStr.equals("Sale Date")) {
					posMapping.setDisplayLabel("Purchase Date");
				}else if(optCulFieldStr.equals("Sale Price")) {
					posMapping.setDisplayLabel("Selling Price");
				}else if(optCulFieldStr.equals("UDF10")) {
					posMapping.setDisplayLabel("Doc Sid");
				}else if(optCulFieldStr.equals("UDF11")) {
					posMapping.setDisplayLabel("Item Sid");
				}
				
				else if(optCulFieldStr.equals("External ID")) {
					posMapping.setUniquePriority(1);
					posMapping.setDisplayLabel(optCulFieldStr);
				}
				else {
					
					posMapping.setDisplayLabel(optCulFieldStr);
				}
				
				//CustField Name
				posMapping.setCustomFieldName(genFieldSalesMap.get(genFieldLbl));
				if(genFieldLbl.equals("SaleDate")) {
					posMapping.setDataType("Date(MM/dd/yyyy HH:mm:ss)");
				}
				else if(genFieldLbl.equals("SalePrice") ||genFieldLbl.equals("Qty") || genFieldLbl.equals(TAX)) {
					posMapping.setDataType("Double");
				}else if(genFieldLbl.equals("ReceiptNumber")) {
					posMapping.setDataType("String");
				}
				else posMapping.setDataType("String");
				
			}
			
			else if(mapType.equals(Constants.POS_MAPPING_TYPE_SKU)) {
				
				optCulFieldStr = genFieldSKUMap.get(genFieldLbl);
				//Display Label
				if(optCulFieldStr.equals("UDF11")) {
					posMapping.setDisplayLabel("Item Sid");
				}else {
					
					posMapping.setDisplayLabel(optCulFieldStr);
				}
				
				//CustField Name
				posMapping.setCustomFieldName(genFieldSKUMap.get(genFieldLbl));
				if(genFieldLbl.equals("ListPrice")) {
					posMapping.setDataType("Double");
				}else {
					posMapping.setDataType("String");
				}
				
				if(genFieldLbl.equals(genFieldSKUMap.get("SKU"))) {
					posMapping.setUniquePriority(1);
				}
			}
			else if(mapType.equals(Constants.MAILINGLIST_TYPE_HOMESPASSED)) {
				
				optCulFieldStr = genHomesPassedMap.get(genFieldLbl);
				//CustField Name
				posMapping.setCustomFieldName(optCulFieldStr);
				//Display Label
				posMapping.setDisplayLabel(optCulFieldStr);
				
				////set Data Type
				if(optCulFieldStr.equals("Addressunit Id")) {
					posMapping.setDataType("Number");
					posMapping.setUniquePriority(1);
				}else {
					posMapping.setDataType("String");
				}
				
				
				
			}
			//Mapping Type
			posMapping.setMappingType(mapType);
			
			//Set user
			posMapping.setUserId(users.getUserId());
			
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
		
		List<POSMapping> recordList = null;
		recordList = posMappingDao.findAllByUserId(users.getUserId());
		
		MastersToTransactionMappingsDao masterToTranscDao=(MastersToTransactionMappingsDao)SpringUtil.getBean("mastersToTransactionMappingsDao");
		List<MastersToTransactionMappings> tempList = masterToTranscDao.findByUserId(users.getUserId());
		
		if(recordList == null || recordList.size() == 0) return;
		
		for (POSMapping posMapping : recordList) {
			Row row = new Row();
			
//			POS Attribute
			Textbox posAttrTextBx = new Textbox(posMapping.getPosAttribute());
			posAttrTextBx.setParent(row);
			

			
//			Custom Field
			Listbox posMappingListBx = null;
//			logger.debug("posMapping.getMappingType() is :: "+posMapping.getMappingType());
			
			
			String mtype = Constants.POS_MAPPING_TYPE_CONTACTS;
			if(posMapping.getMappingType().trim().equalsIgnoreCase("Contacts")) {
				mtype = Constants.POS_MAPPING_TYPE_CONTACTS;
				posMappingListBx =  createContactPosMappingListbox();
				posMappingListBx.setParent(row);
				
			}else if(posMapping.getMappingType().trim().equalsIgnoreCase("Sales")) {
				mtype = Constants.POS_MAPPING_TYPE_SALES;
				posMappingListBx =  createSalePosMappingFieldListBox();
				posMappingListBx.setParent(row);
			}else if(posMapping.getMappingType().trim().equals("SKU")) {
				mtype = Constants.POS_MAPPING_TYPE_SKU;
				posMappingListBx =  createSkuPosMappingFieldListBox();
				posMappingListBx.setParent(row);
			}else if(posMapping.getMappingType().trim().equals(Constants.POS_MAPPING_TYPE_HOMES_PASSED)) {
				//logger.info("BCRM Type");
				mtype = Constants.POS_MAPPING_TYPE_HOMES_PASSED;
				posMappingListBx =  createHomesPassedPosMappingFieldListBox();
				posMappingListBx.setParent(row);
			}
//			if (posMappingListBx == null) continue;
			posMappingListBx.setAttribute("CUST_FIELD_LISTBOX", "CUST_FIELD_LISTBOX");
			posMappingListBx.setAttribute("MAPPING_TYPE", mtype);
			
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
			
			
			
//			Data Type
			String dataTypeStr = posMapping.getDataType();
			
			
			Div dataTypeDiv = new Div();
//			dataTypeDiv.setWidth("350px");
			Listbox dataTypelb = createDataTypeListBox();
			dataTypelb.setParent(dataTypeDiv);
			
			Listbox dateFormatListBx = createDateFormatListbox();
			dateFormatListBx.setParent(dataTypeDiv);
			dateFormatListBx.setSelectedIndex(0);
			dateFormatListBx.setVisible(false);
			List ChildItemList =  dataTypelb.getChildren();
			
			for (Object object : ChildItemList) {
				
				Listitem listItem = (Listitem)object;
				
				if(dataTypeStr.contains("Date") &&  listItem.getLabel().equals("Date")) {
					dataTypelb.setSelectedItem(listItem);
					dateFormatListBx.setVisible(true);
					
					List dateFormatList = dateFormatListBx.getChildren();
					dataTypeStr =  dataTypeStr.substring(dataTypeStr.indexOf("(")+1, dataTypeStr.indexOf(")"));
					
					for (Object obj : dateFormatList) {
						
						Listitem tempListItem = (Listitem)obj;
						if(tempListItem.getLabel().equals(dataTypeStr)) {
							dateFormatListBx.setSelectedItem(tempListItem);
							break;
						}
					}
				
					
				}else if(listItem.getLabel().equals(dataTypeStr)) {
					dataTypelb.setSelectedItem(listItem);
					break;
				}
				
			} // for
			if(!posMappingListBx.getSelectedItem().getLabel().startsWith("UDF")) {
				
				dataTypelb.setDisabled(true);
			}
			
			
			dataTypelb.addEventListener("onSelect", this);
			dataTypeDiv.setParent(row);
			
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
			
			//Optional Value Delete Action
			Image optDelValImg = new Image();
			optDelValImg.setSrc("/img/action_delete.gif");
			optDelValImg.setStyle("cursor:pointer;");
			optDelValImg.addEventListener("onClick", this);
			optDelValImg.setAttribute("TYPE", "POS_DEL_OPTIONAL_VALUE");
			optDelValImg.setParent(optDiv);			
			
//			Display Label
			Textbox dispLabelTextBx = new Textbox(posMapping.getDisplayLabel());
			dispLabelTextBx.setParent(row);
			
//			Unique priority
			Intbox priIntBx = new Intbox();
			priIntBx.setMaxlength(1);
			priIntBx.setWidth("30px");
			if(posMapping.getUniquePriority()!=null) priIntBx.setValue(posMapping.getUniquePriority());
			priIntBx.setParent(row);
			priIntBx.setDisabled(true);
			if(isAdmin) {
				
				priIntBx.setDisabled(false);
			}
			
			
			Div div=new Div();
			div.setParent(row);

			
			/*Image delImg = new Image();
			delImg.setSrc("/images/action_delete.gif");
			delImg.setStyle("cursor:pointer;");
			delImg.addEventListener("onClick", this);
			delImg.setParent(div);*/
			
				
			row.setAttribute("referenceId", posMapping);
			String mappingType = null;
			boolean canproceed = false;
			
			if(posMapping.getMappingType().trim().equals("Contacts")) {
				mappingType = "POS_CONTACT_MAPPING";
//				delImg.setAttribute("TYPE", "POS_CONTACT_MAPPING");
				row.setParent(contactRowsId);
			}else if(posMapping.getMappingType().trim().equals("Sales")) {
				mappingType = "POS_SALES_MAPPING";
//				delImg.setAttribute("TYPE", "POS_SALES_MAPPING");
				row.setParent(salesRowsId);
			}else if(posMapping.getMappingType().trim().equals("SKU")) {
				mappingType = "POS_SKU_MAPPING";
//				delImg.setAttribute("TYPE", "POS_SKU_MAPPING");
				row.setParent(skuRowsId);
			}
			else if(posMapping.getMappingType().trim().equals(Constants.POS_MAPPING_TYPE_HOMES_PASSED)) {
				mappingType = "POS_HOMES_PASSED_MAPPING";
//				delImg.setAttribute("TYPE", "POS_HOMES_PASSED_MAPPING");
				row.setParent(homesPassedRowsId);
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
		
		tempItem = new Listitem("dd-MMM-yy");
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
	
	
	/*private Listbox createHomesPassedPosMappingListbox() {
		
		
		Listbox dateFormatListBx = new Listbox();
//		contactGenFieldList = null;
	Listitem tempItem = null;
	
	
	//genralFieldList
	for (int i=0; i< genHomesPassedArr.length; i++) {
//			tempStr = (String)contactGenFieldList.get(i);
		tempItem = new Listitem(genHomesPassedArr[i]);
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
	*/
	
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
		
		private Listbox createHomesPassedPosMappingFieldListBox() {
			
//			logger.info("createHomesPassedPosMappingFieldListBox");
			Listbox dateFormatListBx = new Listbox();
			Listitem tempItem = null;
			
			//SKU genralFieldList
			for (int i=0; i< genHomesPassedArr.length; i++) {
//				tempStr = (String)contactGenFieldList.get(i);
//				logger.info("genHomesPassedArr "+genHomesPassedArr[i]);
				tempItem = new Listitem(genHomesPassedArr[i]);
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
		
		public void onClick$addHomesPassedMapBtnId() {
			
			

			logger.debug("Users ::"+users +" >> Pos Ml ::"+mailingList);
			if(users == null) {
				MessageUtil.setMessage("Please select user.","color:red","TOP");
				return;
			}else if(mailingList == null) {
				MessageUtil.setMessage("Please create mailing list.","color:red","TOP");
				return;
			}
			
			Row tempRow = new Row();
			
			Textbox tempTextBox;
			
			//POS Attr
			tempTextBox = new Textbox();
			tempTextBox.setParent(tempRow);
			
			
			Listbox tempListbox = null;
			
			//Custom Field
			tempListbox = createHomesPassedPosMappingFieldListBox();
			tempListbox.setAttribute("CUST_FIELD_LISTBOX", "CUST_FIELD_LISTBOX");
			//setAttribute("MAPPING_TYPE"
			tempListbox.setAttribute("MAPPING_TYPE", Constants.POS_MAPPING_TYPE_HOMES_PASSED);
			tempListbox.addEventListener("onSelect", this);
			tempListbox.setSelectedIndex(10);
			tempListbox.setParent(tempRow);
			
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
			tempTextBox.setParent(tempRow);
			
//			Unique priority
			Intbox priIntBx = new Intbox();
			priIntBx.setMaxlength(1);
			priIntBx.setWidth("30px");
			priIntBx.setParent(tempRow);
			priIntBx.setDisabled(true);
			if(isAdmin) {
				
				priIntBx.setDisabled(false);
			}
			
			
			Div div=new Div();
			div.setParent(tempRow);

			//Delete Action
			Image delImg = new Image();
			delImg.setAttribute("TYPE", "POS_HOMES_PASSED_MAPPING");
			delImg.setSrc("/images/action_delete.gif");
			delImg.setStyle("cursor:pointer;");
			delImg.addEventListener("onClick", this);
			delImg.setParent(div);
					
			
			tempRow.setParent(homesPassedRowsId);
			
			
			
			
		}//onClick$addHomesPassedMapBtnId
		
		
		
		//posMapBackBtnId,saveContinueBtnId
		public void onClick$addContactPosMapBtnId() {
			logger.debug("Users ::"+users +" >> Pos Ml ::"+mailingList);
			if(users == null) {
				MessageUtil.setMessage("Please select user.","color:red","TOP");
				return;
			}else if(mailingList == null) {
				MessageUtil.setMessage("Please create BCRM type of mailing list.","color:red","TOP");
				return;
			}
			
			Row tempRow = new Row();
			
			Textbox tempTextBox;
			
			//POS Attr
			tempTextBox = new Textbox();
			tempTextBox.setParent(tempRow);
			
			
			Listbox tempListbox = null;
			
			
			//Custom Field
			tempListbox = createContactPosMappingListbox();
			tempListbox.setAttribute("CUST_FIELD_LISTBOX", "CUST_FIELD_LISTBOX");
			tempListbox.setAttribute("MAPPING_TYPE", Constants.POS_MAPPING_TYPE_CONTACTS);
			tempListbox.addEventListener("onSelect", this);
			tempListbox.setSelectedIndex(17);
			tempListbox.setParent(tempRow);
			
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
			tempTextBox.setParent(tempRow);
			
//			Unique priority
			Intbox priIntBx = new Intbox();
			priIntBx.setMaxlength(1);
			priIntBx.setWidth("30px");
			priIntBx.setParent(tempRow);
			priIntBx.setDisabled(true);
			if(isAdmin) {
				
				priIntBx.setDisabled(false);
			}
			
			
			Div div=new Div();
			div.setParent(tempRow);

			//Delete Action
			Image delImg = new Image();
			delImg.setAttribute("TYPE", "POS_CONTACT_MAPPING");
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
			}else if(mailingList == null) {
				MessageUtil.setMessage("Please create BCRM type of mailing list.","color:red","TOP");
				return;
			}
			
			Row tempRow = new Row();
			
			Textbox tempTextBox;
			
			//POS Attr
			tempTextBox = new Textbox();
			tempTextBox.setParent(tempRow);
			
			Listbox tempListbox = null;
			
			
			//Custom Field
			tempListbox = createSalePosMappingFieldListBox();
			tempListbox.setAttribute("CUST_FIELD_LISTBOX", "CUST_FIELD_LISTBOX");
			tempListbox.setAttribute("MAPPING_TYPE", Constants.POS_MAPPING_TYPE_SALES);
			tempListbox.addEventListener("onSelect", this);
			tempListbox.setSelectedIndex(11);
			tempListbox.setParent(tempRow);
			
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
			tempTextBox.setParent(tempRow);
			
//			Unique priority
			Intbox priIntBx = new Intbox();
			priIntBx.setMaxlength(1);
			priIntBx.setWidth("30px");
			priIntBx.setParent(tempRow);
			priIntBx.setDisabled(true);
			if(isAdmin) {
				
				priIntBx.setDisabled(false);
			}
			
			

			Div div=new Div();
			div.setParent(tempRow);

			//Delete Action
			Image delImg = new Image();
			delImg.setAttribute("TYPE", "POS_SALES_MAPPING");
			delImg.setSrc("/images/action_delete.gif");
			delImg.setStyle("cursor:pointer;");
			delImg.addEventListener("onClick", this);
			delImg.setParent(div);	

			
			tempRow.setParent(salesRowsId);
			
			
		}
		
		public void onClick$addSKUPosMapBtnId() {
			
			if(users == null) {
				MessageUtil.setMessage("Please select user.","color:red","TOP");
				return;
			}else if(mailingList == null) {
				MessageUtil.setMessage("Please create POS type of mailing list.","color:red","TOP");
				return;
			}
			
			Row tempRow = new Row();
			
			Textbox tempTextBox;
			
			//POS Attr
			tempTextBox = new Textbox();
			tempTextBox.setParent(tempRow);
			
			Listbox tempListbox = null;
			
			
			//Custom Field
			tempListbox = createSkuPosMappingFieldListBox();
			tempListbox.setSelectedIndex(5);
			tempListbox.setAttribute("CUST_FIELD_LISTBOX", "CUST_FIELD_LISTBOX");
			tempListbox.setAttribute("MAPPING_TYPE", Constants.POS_MAPPING_TYPE_SKU);
			tempListbox.addEventListener("onSelect", this);
			tempListbox.setParent(tempRow);
			
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
			tempTextBox.setParent(tempRow);
			
			
//			Unique priority
			Intbox priIntBx = new Intbox();
			priIntBx.setMaxlength(1);
			priIntBx.setWidth("30px");
			priIntBx.setParent(tempRow);
			priIntBx.setDisabled(true);
			if(isAdmin) {
				
				priIntBx.setDisabled(false);
			}
			
			
			
			Div div=new Div();
			div.setParent(tempRow);
			
			//Delete Action
			Image delImg = new Image();
			delImg.setAttribute("TYPE", "POS_SKU_MAPPING");
			delImg.setSrc("/images/action_delete.gif");
			delImg.setStyle("cursor:pointer;");
			delImg.addEventListener("onClick", this);
			delImg.setParent(div);
					
			
			tempRow.setParent(skuRowsId);
		}
		
		
		
		public void onClick$posMapBackBtnId() {
			gotoStep(1);
		} //onClick$posMapBackBtnId()
		
		public void onClick$posFTPSettingsBackBtnId() {
			gotoStep(2);
		} //onClick$posMapBackBtnId()
		
		
		//contactRowsId,salesRowsId,skuRowsId,
		public void onClick$saveContinueBtnId() {
			
			try {
				
				int confirm = Messagebox.show("Are you sure you want to save the mapping?", 
						"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				
				if(confirm == Messagebox.OK) {
					
					try {
						scopeCustfieldList.clear();
						scopePosAttrList.clear();
						
						
						// Validate Contact POS Mapping
						List contactRowChildList =  contactRowsId.getChildren();
						
						if(contactRowChildList != null && contactRowChildList.size() >0) {
							
							if(validatePOSMappingColl(contactRowChildList, "Contacts") == false) {
								return;
							}
						}
						// Validate Sales POS Mapping
						List salesRowChildList = salesRowsId.getChildren();
						
						if(salesRowChildList != null && salesRowChildList.size() >0) {
							if(validatePOSMappingColl(salesRowChildList, "Sales") == false) {
								return;
							}
						}
						// Validate SKU POS Mapping
						List skuRowChildList = skuRowsId.getChildren();
						
						if(skuRowChildList != null && skuRowChildList.size() >0) {
							if(validatePOSMappingColl(skuRowChildList, "SKU") == false) {
								return;
							}
						}
						
						//validate HomesPassed Mapping
						List homesPassedChildList = homesPassedRowsId.getChildren();
						
						if(homesPassedChildList != null && homesPassedChildList.size() >0) {
							if(validatePOSMappingColl(homesPassedChildList, Constants.POS_MAPPING_TYPE_HOMES_PASSED) == false) {
								return;
							}
						}
						
						savePOSMapping();
						
						MessageUtil.setMessage("BCRM mapping saved successfully.", "green", "TOP");
						
						
						clearAndDefaultPOSMappSettings();
						if(isAdmin){
							
							gotoStep(3);
//							mastersBCRMPosSettings();
							prepareRelationMappingData();
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
			MessageUtil.setMessage("POS mapping saved successfully.", "green", "TOP");
			
			clearAndDefaultPOSMappSettings();
			if(isAdmin){
				
				gotoStep(3);
			}
			
		} //onClick$saveContinueBtnId()
		*/
		
		
		private void clearAndDefaultPOSMappSettings() {
//			logger.info("---just entered---");
			Components.removeAllChildren(contactRowsId); 
			Components.removeAllChildren(salesRowsId); 
			Components.removeAllChildren(skuRowsId); 
			Components.removeAllChildren(homesPassedRowsId);
			
			deafultPOSMappingSettings();
		}

		private boolean validatePOSMappingColl(List tempList,String mappingType) {
			for (Object object : tempList) {
				Row temRow = (Row)object;
				
//				POSMapping posMapping = null;
				List chaildLis = temRow.getChildren();
				
				
				
				//POS Attribute
				Textbox posAttrbuteTextbox = (Textbox) chaildLis.get(0);
				posAttrbuteTextbox.setStyle("border:1px solid #7F9DB9;");
				
				//Display label
				Textbox displayTextbox = (Textbox) chaildLis.get(4);
				displayTextbox.setStyle("border:1px solid #7F9DB9;");
				
				//Uniq Prio label
				Intbox uniqPriIntbox = (Intbox) chaildLis.get(5);
				uniqPriIntbox.setStyle("border:1px solid #7F9DB9;");
				
				if(posAttrbuteTextbox.getValue().trim().equals("")) {
					logger.debug("Custom fieldData is eampty");
					posAttrbuteTextbox.setStyle("border:1px solid #DD7870;");
					
					MessageUtil.setMessage("Provide "+mappingType+" BCRM attribute.", "color:red", "TOP");
					return false;
				}
				else if(displayTextbox.getValue().trim().equals("")) {
					displayTextbox.setStyle("border:1px solid #DD7870;");
					logger.debug("Custom fieldData is eampty");
					MessageUtil.setMessage("Provide "+ mappingType +" Display label.", "color:red", "TOP");
					return false;
				}
				else {
					try {
						if(uniqPriIntbox.getValue()!=null && uniqPriIntbox.getValue()<=0) {
							uniqPriIntbox.setStyle("border:1px solid #DD7870;");
							logger.debug("Invalid unique priority eampty");
							MessageUtil.setMessage("Provide "+ mappingType +" valid Unique Priority value.", "color:red", "TOP");
							return false;
						}
					} catch (Exception e) {
						uniqPriIntbox.setStyle("border:1px solid #DD7870;");
						logger.debug("Invalid unique priority eampty");
						MessageUtil.setMessage("Provide "+ mappingType +" valid Unique Priority value.", "color:red", "TOP");
						return false;
					}
				}
				
				
				
				//optculture field
				Listbox custFieldListbx = (Listbox)chaildLis.get(1);
				String scopeCustFieldStr = mappingType+"_"+ custFieldListbx.getSelectedItem().getLabel();
				String scopePosStr = mappingType+"_"+posAttrbuteTextbox.getValue();
				
				if(scopeCustfieldList.contains(scopeCustFieldStr) ) {
					MessageUtil.setMessage(mappingType +"  Optculture Field  "+ custFieldListbx.getSelectedItem().getLabel() +" already mapped","color:red","TOP");
					return false;
				}else if(scopePosAttrList.contains(scopePosStr)) {
					MessageUtil.setMessage(mappingType +"  BCRM Attribute "+ posAttrbuteTextbox.getValue() +" already mapped","color:red","TOP");
					return false;
				}
				scopeCustfieldList.add(scopeCustFieldStr);
				scopePosAttrList.add(scopePosStr);
			} //for
			return true;
			
		} //validatePOSMappingColl
		
		private void savePOSMapping() {
			
			List contactRowChildList =  contactRowsId.getChildren();
			
			if(contactRowChildList != null && contactRowChildList.size() > 0) {
				savePOSMappingByType(contactRowChildList ,  "Contacts");
			}
			
			//Sales POS Mapping
			List salesRowChildList = salesRowsId.getChildren();
			if(salesRowChildList != null && salesRowChildList.size() > 0) {
				savePOSMappingByType(salesRowChildList ,  "Sales");
			}
			
			//SKU POS Mapping
			List skuRowChildList = skuRowsId.getChildren();
			if(skuRowChildList != null && skuRowChildList.size() > 0) {
				savePOSMappingByType(skuRowChildList ,  "SKU");
			}
			
			//SKU HomesPassed Mapping
			List homesPassedRowChildList = homesPassedRowsId.getChildren();
			if(homesPassedRowChildList != null && homesPassedRowChildList.size() > 0) {
				savePOSMappingByType(homesPassedRowChildList ,  Constants.POS_MAPPING_TYPE_HOMES_PASSED);
			}
			
			
		} //savePOSMapping
		
		private void savePOSMappingByType(List rowList , String mappingType) {
			
			boolean isObjModified = false;

			if(rowList.size() > 0) {
				
				for (Object object : rowList) {
					
					isObjModified = false;
					
					Row temRow = (Row)object;
					
					POSMapping posMapping = null;
					List chaildLis = temRow.getChildren();
					
					
					//POS Attribute
					Textbox posAttrbuteTextbox = (Textbox) chaildLis.get(0);
					
					//Data Type
					Div dateFormatDiv = (Div)chaildLis.get(2); 
					
					Listbox dataTypeListbox  = (Listbox)dateFormatDiv.getChildren().get(0);
					String dataTypeStr = dataTypeListbox.getSelectedItem().getLabel();
					if(dataTypeStr.equals("Date")) {
						dataTypeStr = dataTypeStr+"("+((Listbox)dateFormatDiv.getChildren().get(1)).getSelectedItem().getLabel()+")";
					}
					
					//custom field
					Listbox custFielListBx = (Listbox) chaildLis.get(1);
					
					
					//Display Label
					Textbox  dispLblTextBx = (Textbox) chaildLis.get(4);
					
					//Unique Priority 
					Intbox  uniqPriIntBx = (Intbox) chaildLis.get(5);

					//Optional Values
					Combobox optCombBox = (Combobox) ((Div) chaildLis.get(3)).getFirstChild();
					String optCombStr="";
					
					List<Comboitem> cbItemsList = optCombBox.getItems();
					for (Comboitem cbItem : cbItemsList) {
						if(optCombStr.length()>0) optCombStr += Constants.ADDR_COL_DELIMETER ;
						optCombStr += cbItem.getLabel();
					}
					
					
					
					//String scopeStr,String posAttrStr,String custFieldStr, long userId
					posMapping = (POSMapping)temRow.getAttribute("referenceId");
					//posMapping = posMappingDao.findRecord(scopeLbl.getValue(), posAttrLbl.getValue(), custFieldLbl.getValue(), users.getUserId());
					
					
					//Check if the Fields are modified...
					if(posMapping != null) {
						
						if(!(posMapping.getPosAttribute().trim().equals(posAttrbuteTextbox.getValue().trim()))) {
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
					
					
					
					
					
					//scope
					posMapping.setMappingType(mappingType.trim());
					//CustomField Name
					posMapping.setCustomFieldName(custFielListBx.getSelectedItem().getLabel());
					//POSAttribute
					posMapping.setPosAttribute(posAttrbuteTextbox.getValue());
					//Display Label
					posMapping.setDisplayLabel(dispLblTextBx.getValue());

					// Optional Values 
					if(optCombStr.length()>0) posMapping.setOptionalValues(optCombStr);
					else posMapping.setOptionalValues(null);
					
					//Unique priority Label
					posMapping.setUniquePriority(uniqPriIntBx.getValue());
					
					//Data type
					posMapping.setDataType(dataTypeStr);
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
				
				
				//set  the timezone value of user
				String userTimeZoneVal = users.getClientTimeZone();
				
				for (Listitem item : TimesZonesLbId.getItems()) {
					
					if(!userTimeZoneVal.equals(item.getValue())) continue;
					
					item.setSelected(true);
					
					
					
				}//for
				
				
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
			tempListBox.setWidth("100px");
			tempListBox.setMold("select");
			Listitem tempItem = null;
			
			tempItem  =new Listitem("Contacts");
			tempItem.setParent(tempListBox);
			
			tempItem  =new Listitem("Sales");
			tempItem.setParent(tempListBox);
			
			tempItem  =new Listitem("SKU");
			tempItem.setParent(tempListBox);
			
			tempItem  =new Listitem(Constants.POS_MAPPING_TYPE_HOMES_PASSED);
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
				MessageUtil.setMessage("Please select user.","color:red","TOP");
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
		
		
		public void onClick$submitBtnId() {
			
			try {
				/*************Ftp Settings*******************/
				
				Listitem Item = TimesZonesLbId.getSelectedItem();
				if(Item == null || Item.getIndex() == 0) {
					
					MessageUtil.setMessage("Please select the client's time zone", "color:red;");
					return;
					
				}
				
				
				users.setClientTimeZone(Item.getValue().toString());

				
				List chaildRowList =ftpSettingsRowsId.getChildren();
				
				if(chaildRowList == null || chaildRowList.size() == 0) {
					logger.debug("No data available for saving");
					MessageUtil.setMessage("No FTP settings found. Please provide FTP settings by clicking on 'Add'.","color:red","TOP");
					return;
					
				}
				// above implementation in else here if ftp setting condition is to be considered
				//Validation of Ftp setting
				for (Object object : chaildRowList) {
					Row tempRow  =(Row)object;
					if(ftpSettingValidation(tempRow) == false) {
						return;
					}
					
				}
				
				//Save
				List<UserPosFTPSettings> userFtpSelltingList = new ArrayList<UserPosFTPSettings>(); 
				UserPosFTPSettings userPosFTPSettings = null;
				int scheduleMintInt = 0;
				Listbox tempListbox = null;
				Textbox tempTextbox = null;
				Div tempDiv  = null;
				Intbox tempIntbox = null;
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
				//userPOSFTPSettingsDao.saveByCollection(userFtpSelltingList);
				userPOSFTPSettingsDaoForDML.saveByCollection(userFtpSelltingList);
					
				//usersDao.saveOrUpdate(users);
				usersDaoForDML.saveOrUpdate(users);
				MessageUtil.setMessage("Saved successfully.","color:green","TOP");
				
				defaultFTPSettings();
				saveMasterToTransMappings();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception  ::", e);
			}
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
				 
				 //Scheduled Freq Time 
				 Div tempDiv =  (Div)rowList.get(7);
				 
				 Intbox tempIntBox = (Intbox)tempDiv.getChildren().get(0);
				 tempIntBox.setStyle("border:1px solid #7F9DB9;");
				 
				 if(tempIntBox.getValue()== null) {
					 tempIntBox.setStyle("border:1px solid #DD7870;");
					 MessageUtil.setMessage("Please provide Schedule Time.","color:red","TOP");
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
		
		public void onClick$homesPassedTbBtnId() {
			
			posMapHomesPassedDivId.setVisible(!posMapHomesPassedDivId.isVisible());
			String image = posMapHomesPassedDivId.isVisible() ? "/img/icons/icon_minus.png" : "/img/icons/icon_plus.png";
			homesPassedTbBtnId.setImage(image);
		}
		
		
		public void onCheck$doubleOptInCbId() {
			
			/*if(mailingList == null) {
				return;
			}*/
			doubleOptLtDiv.setVisible(!doubleOptLtDiv.isVisible());
			onSelect$optInEmailsLbId();
		} // onCheck$doubleOptInCbId 
		
		
		public void onCheck$parentalConsentCbId() {
			/*if(mailingList == null) {
				return;
			}*/
			
			parentalOptLtDiv.setVisible(!parentalOptLtDiv.isVisible());
			onSelect$parentalConsentEmailLbId();
			
		} //onCheck$parentalConsentCbId
		private A previewBtnId,parentalPreviewBtnId;
		private Image optInPreviewIconId, parentalPreviewIconId;
		private Window previewWin;
		private Iframe  previewWin$html;
		public void onClick$previewBtnId() {
			String templateContent = "";
			
			CustomTemplates customTemplates = optInEmailsLbId.getSelectedItem().getValue();
			if(customTemplates == null) {
				
			templateContent = 	PropertyUtil.getPropertyValueFromDB("optinMsgTemplate");
				
			}else {
					if(customTemplates.getHtmlText()!= null && !customTemplates.getHtmlText().isEmpty()) {
						templateContent = customTemplates.getHtmlText();
					}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(customTemplates.getEditorType()) && customTemplates.getMyTemplateId()!=null) {
					  MyTemplatesDao myTemplatesDao= (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(customTemplates.getMyTemplateId());
					  if(myTemplates!=null) {
						  templateContent = myTemplates.getContent();
					  }else {
						  MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.", "color:red", "TOP");
						  return;
					  }
					}
			}
			
			if(users== null ){
				MessageUtil.setMessage("Please provide the user-details.", "color:blue");
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
				if(customTemplates.getHtmlText()!= null && !customTemplates.getHtmlText().isEmpty()) {
					templateContent = customTemplates.getHtmlText();
				}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(customTemplates.getEditorType()) && customTemplates.getMyTemplateId()!=null) {
				  MyTemplatesDao myTemplatesDao= (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
				  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(customTemplates.getMyTemplateId());
				  if(myTemplates!=null) {
					  templateContent = myTemplates.getContent();
				  }else {
					  MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.", "color:red", "TOP");
					  return;
				  }
				}
			}
			//previewWin$html.setContent(templateContent);
			

			Utility.showPreview(previewWin$html, users.getUserName(), templateContent);
			
			previewWin.setVisible(true);
			
		}//onClick$parentalPreviewBtnId
		
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
		
		//Masters BCRM POS Settings
		
		
		private Listbox salesFieldsId,contactsFieldsId,salesFields1Id,inventoryFieldsId;
		private void mastersBCRMPosSettings(){
			
			Components.removeAllChildren(contactsFieldsId);
			Components.removeAllChildren(inventoryFieldsId);
			Components.removeAllChildren(salesFieldsId);
			Components.removeAllChildren(salesFields1Id);
			
			Listitem tempListItem = new Listitem("--Select--");
			tempListItem.setParent(contactsFieldsId);
			contactsFieldsId.setSelectedIndex(0);
			
			tempListItem = new Listitem("--Select--");
			tempListItem.setParent(inventoryFieldsId);
			inventoryFieldsId.setSelectedIndex(0);
			
			tempListItem = new Listitem("--Select--");
			tempListItem.setParent(salesFieldsId);
			salesFieldsId.setSelectedIndex(0);
			
			tempListItem = new Listitem("--Select--");
			tempListItem.setParent(salesFields1Id);
			salesFields1Id.setSelectedIndex(0);
			
			
			
			
			String selContactField = null, selSalesField = null,selInvField = null,selSalesField1 = null;
			
			MastersToTransactionMappings mastersToTransactionMappings = null;
			MastersToTransactionMappingsDao mastersToTransactionMappingsDao=(MastersToTransactionMappingsDao)SpringUtil.getBean("mastersToTransactionMappingsDao");
			List<MastersToTransactionMappings> tempList = mastersToTransactionMappingsDao.findByUserId(users.getUserId());
			logger.debug("tempList is :::"+tempList);
			
			if(tempList != null && tempList.size() > 0) {
				
				for (MastersToTransactionMappings mastToTransObj : tempList) {
					
					if(mastToTransObj.getType().equals(Constants.TYPE_SALES_TO_CONTACTS)) {
				          selContactField=mastToTransObj.getParentId().getCustomFieldName();
				          
				         logger.debug("contact filed is "+mastToTransObj.getParentId().getCustomFieldName());
						selSalesField=mastToTransObj.getChildId().getCustomFieldName();
						salesFieldsId.setAttribute("MASTTRANSOBJ", mastToTransObj);
					}
					if(mastToTransObj.getType().equals(Constants.TYPE_SALES_TO_Inventory)){
						 selInvField=	mastToTransObj.getParentId().getCustomFieldName();
						 selSalesField1=mastToTransObj.getChildId().getCustomFieldName();
						 salesFields1Id.setAttribute("MASTTRANSOBJ", mastToTransObj);
					}
				} // for
			}
				
			List<POSMapping> posList = posMappingDao.findAllByUserId(users.getUserId());
			logger.debug("posList size is ::"+posList.size());
			for (POSMapping eachPos : posList) {
				Listitem tempItem = new Listitem(eachPos.getCustomFieldName(), eachPos);
				
				
				if(eachPos.getMappingType().equals(Constants.POS_MAPPING_TYPE_CONTACTS)) {
					tempItem.setParent(contactsFieldsId);
					if(selContactField != null && selContactField.equals(tempItem.getLabel())) 
						tempItem.setSelected(true);
				}
				else if(eachPos.getMappingType().equals(Constants.POS_MAPPING_TYPE_SKU)) {
					tempItem.setParent(inventoryFieldsId);
					if(selInvField != null && selInvField.equals(tempItem.getLabel())) tempItem.setSelected(true);
				}
				else if(eachPos.getMappingType().equals(Constants.POS_MAPPING_TYPE_SALES)) {
					tempItem.setParent(salesFieldsId);
					if(selSalesField != null && selSalesField.equals(tempItem.getLabel())) tempItem.setSelected(true);
					
					
					Listitem tempItem1 = new Listitem(eachPos.getCustomFieldName(), eachPos);
					tempItem1.setParent(salesFields1Id);
					if(selSalesField1 != null && selSalesField1.equals(tempItem1.getLabel())) tempItem1.setSelected(true);
				}
				
				
			} // for
			
			
		}//mastersPosSettings()	
			
			
			
		public void onClick$masterBCRMFTPSettingsBackBtnId() {
			gotoStep(2);
		} //onClick$posMapBackBtnId()
		
		
		public void onClick$masterSubmitBtnId(){
			
			if(salesFieldsId.getSelectedIndex()== 0 || contactsFieldsId.getSelectedIndex() == 0 ||
			salesFields1Id.getSelectedIndex()==0 || inventoryFieldsId.getSelectedIndex()==0 ) {
			
			MessageUtil.setMessage("Please select all the mapping fields.", "color:blue");
			return;
	
		  }
				
				MastersToTransactionMappingsDao mastersToTransactionMappingsDao=(MastersToTransactionMappingsDao)SpringUtil.getBean("mastersToTransactionMappingsDao");
				MastersToTransactionMappingsDaoForDML mastersToTransactionMappingsDaoForDML=(MastersToTransactionMappingsDaoForDML)SpringUtil.getBean("mastersToTransactionMappingsDaoForDML");
				MastersToTransactionMappings salesToContactsObj =null;
				MastersToTransactionMappings salesToInventoryObj =null;
				
				List<MastersToTransactionMappings> tempList = mastersToTransactionMappingsDao.findByUserId(users.getUserId());
				
				if(tempList != null && tempList.size() > 0) {
					for (MastersToTransactionMappings mastToTransObj : tempList) {
						logger.info("(mastToTransObj.getType()>>>"+mastToTransObj.getType());
						if(mastToTransObj.getType().equals(Constants.TYPE_SALES_TO_CONTACTS)) salesToContactsObj = mastToTransObj;
						else if(mastToTransObj.getType().equals(Constants.TYPE_SALES_TO_Inventory)) salesToInventoryObj = mastToTransObj;
					} // for
				}
				
				
				//MastersToTransactionMappings salesToContactsObj =(MastersToTransactionMappings)salesFieldsId.getAttribute("MASTTRANSOBJ");
			
				 if(salesToContactsObj == null ) {
					 salesToContactsObj =new MastersToTransactionMappings();
					 salesToContactsObj.setCreatedDate(Calendar.getInstance());
					 salesToContactsObj.setUserId(users.getUserId());
					 salesToContactsObj.setListId(mailingList.getListId());
					 salesToContactsObj.setType(Constants.TYPE_SALES_TO_CONTACTS);
				 } 
				logger.debug(" get type"+ salesToContactsObj.getType());
				 salesToContactsObj.setParentId((POSMapping)contactsFieldsId.getSelectedItem().getValue());
				 salesToContactsObj.setChildId((POSMapping)salesFieldsId.getSelectedItem().getValue());
				 salesToContactsObj.setLastModifieddDate(Calendar.getInstance());
				
				
				// MastersToTransactionMappings salesToInventoryObj =(MastersToTransactionMappings)salesFields1Id.getAttribute("MASTTRANSOBJ");
				
				 if(salesToInventoryObj == null ) {
					salesToInventoryObj =new MastersToTransactionMappings();
					salesToInventoryObj.setCreatedDate(Calendar.getInstance());
					salesToInventoryObj.setUserId(users.getUserId());
					salesToInventoryObj.setListId(mailingList.getListId());
					salesToInventoryObj.setType(Constants.TYPE_SALES_TO_Inventory);
				}
				logger.debug("type is "+salesToInventoryObj.getType());
				salesToInventoryObj.setParentId((POSMapping)inventoryFieldsId.getSelectedItem().getValue());
				salesToInventoryObj.setChildId((POSMapping)salesFields1Id.getSelectedItem().getValue());
				salesToInventoryObj.setLastModifieddDate(Calendar.getInstance());

				
				
				
				try {
					int confirm = Messagebox.show("Are you sure you want to save the settings?", "Prompt", 
							 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm == Messagebox.OK) {
					
						
						mastersToTransactionMappingsDaoForDML.saveOrUpdate(salesToContactsObj);
						mastersToTransactionMappingsDaoForDML.saveOrUpdate(salesToInventoryObj);
						
						MessageUtil.setMessage("Settings saved successfully.","color:green;");
					
					}
				} catch (Exception e) {
					logger.error("Exception  ::", e);
				}
							
		}//onClick$masterSubmitBtnId()
		
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
		
	public void saveMasterToTransMappings() {
			
			MastersToTransactionMappingsDao mastersToTranMapDao = (MastersToTransactionMappingsDao)SpringUtil.getBean("mastersToTransactionMappingsDao");
			MastersToTransactionMappingsDaoForDML mastersToTranMapDaoForDML = (MastersToTransactionMappingsDaoForDML)SpringUtil.getBean("mastersToTransactionMappingsDaoForDML");
			List<MastersToTransactionMappings> oldMastList  = mastersToTranMapDao.findByUserId(users.getUserId());
			logger.debug("Saving Master to transaction details ::");
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
			
			
			
			
			
			try {
				int confirm = Messagebox.show("Are you sure you want to save the settings?", "Prompt", 
						 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm == Messagebox.OK) {
					mastersToTranMapDaoForDML.saveByCollection(mastToTransMappLists);
					
					
					if(oldMastList!= null && oldMastList.size() >0) {
						String deleteIds = "";
						for (MastersToTransactionMappings oldObj : oldMastList) {
							deleteIds += deleteIds.trim().length() == 0 ? ""+oldObj.getId() :","+oldObj.getId();
						}
						
						mastersToTranMapDaoForDML.deleteByIds(deleteIds);
					}
					
					
					
					MessageUtil.setMessage("Settings saved successfully.","color:green;");
				
				}
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
				if(!imgAction.equals("FTP_SETTINGS") ) {
					
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
										"Delete BCRM Mapping", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
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
										"Delete BCRM Mapping", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
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
										"Delete BCRM Mapping", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
								if(confirm != Messagebox.OK) return;
								
								POSMapping posCustFieldMapping = (POSMapping)temRow.getAttribute("referenceId");
								
								//posMappingDao.delete(posCustFieldMapping); //delete posMapping entry
								posMappingDaoForDML.delete(posCustFieldMapping); //delete posMapping entry
								skuRowsId.removeChild(temRow);
								MessageUtil.setMessage("Deleted successfully.", "green", "TOP");
							}
							
							
						}else if(img.getAttribute("TYPE").equals("POS_HOMES_PASSED_MAPPING")){ // POS HOMES PASSED
							
							if(temRow.getAttribute("referenceId") == null) {
								homesPassedRowsId.removeChild(temRow);
							}else{
								
								int confirm = Messagebox.show("Are you sure you want to delete the record?",
										"Delete BCRM Mapping", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
								if(confirm != Messagebox.OK) return;
								
								POSMapping posCustFieldMapping = (POSMapping)temRow.getAttribute("referenceId");
								
								//delete posMapping entry
								//posMappingDao.delete(posCustFieldMapping);
								posMappingDaoForDML.delete(posCustFieldMapping);
								homesPassedRowsId.removeChild(temRow);
								MessageUtil.setMessage("Deleted successfully.", "green", "TOP");
							}
							
							
						}
						
						
						else if(img.getAttribute("TYPE").equals("FTP_SETTINGS")) {
							temRow = (Row)img.getParent();
							
							if(temRow.getAttribute("referenceId") == null) {
								ftpSettingsRowsId.removeChild(temRow);
							}
							else {
								int confirm = Messagebox.show("Are you sure you want to delete the record?",
										"Delete BCRM Mapping", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
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
						tempListBx.getAttribute("CUST_FIELD_LISTBOX").equals("CUST_FIELD_LISTBOX")   && 
						tempListBx.getAttribute("MAPPING_TYPE") != null ) {
					
					String selectDataType = tempListBx.getSelectedItem().getLabel();
					
//					logger.info("########## selectDateType #######"+selectDateType);
					Div dataTypeDiv	 = (Div)((Row)tempListBx.getParent()).getChildren().get(2);
					Listbox dataTypeListbox   = (Listbox)dataTypeDiv.getChildren().get(0);
					
					if(selectDataType.startsWith(BIRTHDAY) || selectDataType.startsWith(ANNIVERSARY) 
														   || selectDataType.startsWith(SALE_DATE) 
														   || selectDataType.startsWith(CREATED_DATE)) {
						
						dataTypeListbox.setSelectedIndex(1);
						dataTypeListbox.setDisabled(true);
						((Listbox)dataTypeDiv.getChildren().get(1)).setVisible(true);
						
					}else if(selectDataType.startsWith(RECEIPT_NUM)) {
						dataTypeListbox.setSelectedIndex(0);
						if(tempListBx.getAttribute("MAPPING_TYPE").equals(Constants.POS_MAPPING_TYPE_HOMES_PASSED)) {
							dataTypeListbox.setSelectedIndex(0);
						}
//						dataTypeListbox.setDisabled(false);
						dataTypeListbox.setDisabled(true);
						((Listbox)dataTypeDiv.getChildren().get(1)).setVisible(false);
					}else if(selectDataType.startsWith(LIST_PRICE) || selectDataType.startsWith(SALE_PRICE)  
																	|| selectDataType.startsWith(TAX) || selectDataType.startsWith(QUANTITY) ) {
						dataTypeListbox.setSelectedIndex(3);
//						dataTypeListbox.setDisabled(false);
						dataTypeListbox.setDisabled(true);
						((Listbox)dataTypeDiv.getChildren().get(1)).setVisible(false);
					} else if(selectDataType.startsWith("UDF")) {
						dataTypeListbox.setSelectedIndex(0);
						dataTypeListbox.setDisabled(false);
//						dataTypeListbox.setDisabled(true);
						((Listbox)dataTypeDiv.getChildren().get(1)).setVisible(false);
					}else if(selectDataType.startsWith(ADDRESSUINTID)){
						dataTypeListbox.setSelectedIndex(2);
						dataTypeListbox.setDisabled(true);
					}else {
						dataTypeListbox.setSelectedIndex(0);
//						dataTypeListbox.setDisabled(false);
						dataTypeListbox.setDisabled(true);
						((Listbox)dataTypeDiv.getChildren().get(1)).setVisible(false);
					}
					
				}else {
					
					Div tempDiv = (Div)tempListBx.getParent();
					Listbox dateFormatLb= (Listbox)tempDiv.getChildren().get(1);
					
					if(tempListBx.getSelectedItem().getLabel().equals("Date")) {
						dateFormatLb.setSelectedIndex(8);
						dateFormatLb.setVisible(true);
					}else dateFormatLb.setVisible(false);
				}
				
				
				
			}
		} //onEvent
		
//		private static Map<String, Integer> optCulGenFieldDataTypeMap = new HashMap<String, Integer>();
//		static
		
	
}
