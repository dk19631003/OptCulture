package org.mq.marketer.campaign.controller.contacts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Address;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.UserFromEmailId;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.layout.EditorController;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactParentalConsentDao;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.CustomTemplatesDaoForDML;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.UserFromEmailIdDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.RightsEnum;
import org.mq.marketer.campaign.general.Utility;
import org.zkforge.ckez.CKeditor;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

public class ManageAutoEmailsController extends GenericForwardComposer {
	private Listbox typeOfAutoEmailListLbId;
	private Users currentUser;
    private UsersDao usersDao;
    private UsersDaoForDML usersDaoForDML;
    private UserFromEmailIdDao userFromEmailIdDao;
    private OrganizationStoresDao organizationStoresDao;
    private CustomTemplatesDao customTemplateDao;
    private CustomTemplatesDaoForDML customTemplateDaoForDML;
    private ContactParentalConsentDao contactParentalConsentDao;
    private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
 /*   private String optinMsgTemplate = PropertyUtil.getPropertyValueFromDB("optinMsgTemplate");
    private String parentalConsentMsgtemplate = PropertyUtil.getPropertyValueFromDB("parentalConsentMsgtemplate");
    private String LoyaltyOptinMsgTemplate = PropertyUtil.getPropertyValueFromDB("LoyaltyOptinMsgTemplate");*/
    private CustomTemplates loadedTemplate;
    
    
    private static final int MESSAGE_ADD = 0;
	private static final int MESSAGE_UPDATE = 2;
	private static final String PARENTALCONSENT="parentalConsent";
	private static final String DOUBLEOPTIN="welcomemail";
	private static final String LOYALTYOPTIN="loyaltyOptin";
	private static final String WEBFORMEMAIL="webformWelcomeEmail";
	private static final String TIERUPGRADATION="tierUpgradation";
	private static final String EARNEDBONUS="earnedBonus";
	private static final String EARNEDREWARDEXPIRATION="earnedRewardExpiration";
	private static final String MEMBERSHIPEXPIRATION="membershipExpiration";
	private static final String GIFTAMOUNTEXPIRATION="giftAmountExpiration";
	private static final String GIFTCARDEXPIRATION="giftCardExpiration";
	private static final String GIFTCARDISSUANCE="giftCardIssuance";
	
    private List welcomeMsgList = null;
    //Set<Long> userIdsSet = GetUser.getUsersSet();
    
    private Set<Long> listIdsSet; 
    private Paging approvalListPaging;
    private String appName;
	private CKeditor ckEditorId;
    private Textbox cSubTb;
	private Textbox cFromNameTb;
	private Combobox cFromEmailCb; 
	private Textbox cAddressOneTbId;
	private Textbox cAddressTwoTbId;
	private Textbox cCityTbId;
	private Textbox cStateTbId;
	private Textbox cCountryTbId;
	private Textbox permRemTextId;
	private Textbox cWebLinkTextTb;
	private Textbox cWebLinkUrlTextTb;
	private Radio postalAddressRbId;
	private Textbox cPinLbId;
	private Textbox cPhoneTbId;
	private Checkbox cWebPageCb;
	private Checkbox toNameChkId,orgUnitChkBoxId,orgNameChkBoxId;
	private Radiogroup cPermRemRb;
	private Listbox couponLbId, insertCouponLbId, insertMergetagsLbId;
	private Textbox caretPosTB, htmlTbId;
	
	private Div permRemDivId;
	private Div persToDivId;
	private Div cWebLinkHboxId;
	private Listbox phLbId; 
	private Div changeAddressDivId, postalAddressDivId;
	
	private Session session;
	
	private Listbox welcomeListLbId;
	private Button submitBtnId;
	private Button updateBtnId,newBtnId;
	private Button delBtnId;
	private A sendTestMsgBtnId;
	MailingListDao mailingListDao;
	private CouponsDao couponsDao;
	private Label typeLbId,msgLbId;
	private boolean isAdmin;
	public ManageAutoEmailsController(){
	
		
	String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
	PageUtil.setHeader("Auto-Emails","",style,true);
	currentUser = GetUser.getUserObj();
	String userDomainStr = "";
	
	usersDao = (UsersDao)SpringUtil.getBean("usersDao");
	usersDaoForDML = (UsersDaoForDML)SpringUtil.getBean("usersDaoForDML");
	
	session = Sessions.getCurrent();
	
	isAdmin = (Boolean)session.getAttribute("isAdmin");
	
	List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(currentUser.getUserId());
	
	Set<UsersDomains> domainSet = new HashSet<UsersDomains>();//currentUser.getUserDomains();
	if(domainsList != null) {
		domainSet.addAll(domainsList);
		for (UsersDomains usersDomains : domainSet) {
			
			if(userDomainStr.length()>0) userDomainStr+=",";
			userDomainStr += usersDomains.getDomainName();
			
		}
	}
	currentUser.setUserDomainStr(userDomainStr);
	
	usersDao = (UsersDao)SpringUtil.getBean("usersDao");
	usersDaoForDML = (UsersDaoForDML)SpringUtil.getBean("usersDaoForDML");
	organizationStoresDao = (OrganizationStoresDao)SpringUtil.getBean("organizationStoresDao");
	customTemplateDao =	(CustomTemplatesDao)SpringUtil.getBean("customTemplatesDao");
	customTemplateDaoForDML =	(CustomTemplatesDaoForDML)SpringUtil.getBean("customTemplatesDaoForDML");
	userFromEmailIdDao = (UserFromEmailIdDao)SpringUtil.getBean("userFromEmailIdDao");
	contactParentalConsentDao=(ContactParentalConsentDao)SpringUtil.getBean("contactParentalConsentDao");
	mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
	couponsDao = (CouponsDao)SpringUtil.getBean("couponsDao");
	
	listIdsSet = (Set<Long>)session.getAttribute(Constants.LISTIDS_SET);

	}
	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		
		String  typeOfAutoEmailsStr= PropertyUtil.getPropertyValueFromDB("AutoEmail");
		logger.info("autoTypeStr>>>>>>>>>>>>"+typeOfAutoEmailsStr);
		//TODO
		if(typeOfAutoEmailsStr == null){
			return;
		}
		 String[] autoTypeStr = typeOfAutoEmailsStr.split(Constants.ADDR_COL_DELIMETER);
		Listitem li = null;
		String value = null;
	for (String eachType : autoTypeStr) {
		
		value = null;
		if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_DOUBLEOPTIN)) {
			value = "optinMsgTemplate";
						
			
		}else if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_PARENTALCONSENT)) {
			value="parentalConsentMsgtemplate";
			
		}else if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_LOYALTYOPTIN) ){
			
			value="LoyaltyOptinMsgTemplate";
		}else if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_WEBFORMEMAIL)){
			value ="welcomeMsgTemplate";
		}else if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_TIER_UPGRADATION)) {
			value ="tierUpgradationMsgTemplate";
		}else if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION)) {
			value ="rewardExpMsgTemplate";
		}else if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_MEMBERSHIP_EXPIRATION)) {
			value ="memExpMsgTemplate";
		}else if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_EARNED_BONUS)) {
			value ="earnedThresholdBonusMsgTemplate";
		}else if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION )) {
			value ="giftAmountExpMsgTemplate";
		}else if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION)) {
			value ="giftCardExpMsgTemplate";
		}else if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE)) {
			value ="giftCardIssMsgTemplate";
		}
		li = new Listitem(eachType, value);
		li.setParent(typeOfAutoEmailListLbId);
		
	}
	if(typeOfAutoEmailListLbId.getItemCount() > 0){
		
		typeOfAutoEmailListLbId.setSelectedIndex(0);
		
		//logger.info("dsjkfdsjakgfjdkg "+typeOfAutoEmailListLbId.getItemAtIndex(0).getValue()); 
		
	//	logger.info(">>>>>"+PropertyUtil.getPropertyValueFromDB(optinMsgTemplate));
		ckEditorId.setValue(PropertyUtil.getPropertyValueFromDB((String)typeOfAutoEmailListLbId.getSelectedItem().getValue()));	
		}
	//1
	setStores();
	
		
		//2 populate the double optin custom templates from DB
		CustomTemplates customTemplates = null;
		welcomeMsgList = new ArrayList();
		welcomeMsgList.add("Default message");
		
		
		
		
		
		String from =(String)session.getAttribute("fromAddNewBtn");
		String types=(String)session.getAttribute("typeOfEmail");
		
		//request from other sources
		
		if(from != null && types != null ) {
			
			for (Listitem item : typeOfAutoEmailListLbId.getItems()) {
				logger.info("value is"+item.getLabel()+" = " +types);
				
				if(item.getLabel().equals(types)) {
					
					typeOfAutoEmailListLbId.setSelectedItem(item);
					break;
				}
				
			}//for
			customTemplates = (CustomTemplates)session.getAttribute("editCustomTemplate");
			//session.removeAttribute("editCustomTemplate");
			 if(from.equalsIgnoreCase("contact/webform")  ) {
				 
				 //Sessions.getCurrent().removeAttribute("fromAddNewBtn");
				 //Sessions.getCurrent().removeAttribute("fromAddNewConsentMsg");
				 
				 logger.info("customTemplatecvcxvcxv====>"+customTemplates);

				 int index =1;
				 String type = PARENTALCONSENT;
				 if(types.equals(Constants.CUSTOM_TEMPLATE_TYPE_PARENTALCONSENT)) {
					 
					 type = PARENTALCONSENT;
					 index = 1;
				 }else 
					 if(types.equals(Constants.CUSTOM_TEMPLATE_TYPE_LOYALTYOPTIN)) {
						 
						 type = LOYALTYOPTIN;
						 index = 2;
					 }else if(types.equals(Constants.CUSTOM_TEMPLATE_TYPE_WEBFORMEMAIL)) {
					 
					 type = WEBFORMEMAIL;
					 index = 3;
				 }
				 typeOfAutoEmailListLbId.setSelectedIndex(index);
				
				 
				 List<CustomTemplates> list = customTemplateDao.getTemplatesByType(listIdsSet, type);
				 
				 if(list != null && list.size() > 0) {
					 
					 welcomeMsgList.addAll(list);
				 }
				 
				 refreshListBox(customTemplates,MESSAGE_UPDATE); 
				 String mode = (String) session.getAttribute("Mode");
				 if(mode.equalsIgnoreCase("edit")) {
				 typeOfAutoEmailListLbId.setDisabled(true);
				 welcomeListLbId.setDisabled(true);
				 }
				 //from.equalsIgnoreCase("contact/edit") || 
				 
			 }
			 
			 else if(from.equalsIgnoreCase("contact/edit")  ){
				 
				 //Sessions.getCurrent().removeAttribute("fromAddNewBtn");
				 //Sessions.getCurrent().removeAttribute("fromAddNewConsentMsg");
				 
				 logger.info("customTemplate====>"+customTemplates);
				 
				 int index = 0;
				 String type = DOUBLEOPTIN;
				 if(types.equals(Constants.CUSTOM_TEMPLATE_TYPE_DOUBLEOPTIN)) {
					 
					 type = DOUBLEOPTIN;
					 index = 0;
				 }else if(types.equals(Constants.CUSTOM_TEMPLATE_TYPE_PARENTALCONSENT)) {
					 
					 type = PARENTALCONSENT;
					 index = 1;
				 }else if(types.equals(Constants.CUSTOM_TEMPLATE_TYPE_WEBFORMEMAIL)) {
					 
					 type = WEBFORMEMAIL;
					 index = 3;
				 }
				 typeOfAutoEmailListLbId.setSelectedIndex(index);
				 
				 
				 List<CustomTemplates> list = customTemplateDao.getTemplatesByType(listIdsSet, type);
				 
				 if(list != null && list.size() > 0) {
					 
					 welcomeMsgList.addAll(list);
				 }
				
				 
				 refreshListBox(customTemplates,MESSAGE_UPDATE); 
				 String mode = (String) session.getAttribute("Mode");
				 if(mode.equalsIgnoreCase("edit")) {
				 typeOfAutoEmailListLbId.setDisabled(true);
				 welcomeListLbId.setDisabled(true);
				 }
				 //from.equalsIgnoreCase("contact/edit") || 
				 
			 }
			 else if(from.equalsIgnoreCase("contact/upload") ) {
				 
				 refreshListBox(null,MESSAGE_ADD);
			 }
			 else if(from.equalsIgnoreCase("loyalty/loyaltyAutoCommunication") ) {
				 
				 int index=1;
				 String type = LOYALTYOPTIN;
				 if(types.equals(Constants.CUSTOM_TEMPLATE_TYPE_LOYALTYOPTIN)) {
					 type = LOYALTYOPTIN;
					 index = 2;
				 }
				 if(types.equals(Constants.CUSTOM_TEMPLATE_TYPE_TIER_UPGRADATION)) {
					 type = TIERUPGRADATION;
					 index = 5;
				 }
				 if(types.equals(Constants.CUSTOM_TEMPLATE_TYPE_EARNED_BONUS)) {
					 type = EARNEDBONUS;
					 index = 6;
				 }
				 if(types.equals(Constants.CUSTOM_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION)) {
					 type = EARNEDREWARDEXPIRATION;
					 index = 7;
				 }
				 if(types.equals(Constants.CUSTOM_TEMPLATE_TYPE_MEMBERSHIP_EXPIRATION)) {
					 type = MEMBERSHIPEXPIRATION;
					 index = 8;
				 }
				 if(types.equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION)) {
					 type = GIFTAMOUNTEXPIRATION;
					 index = 9;
				 }
				 if(types.equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION)) {
					 type = GIFTCARDEXPIRATION;
					 index = 10;
				 }
				 if(types.equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE)) {
					 type = GIFTCARDISSUANCE;
					 index = 4;
				 }
                 typeOfAutoEmailListLbId.setSelectedIndex(index);
				 List<CustomTemplates> list = customTemplateDao.getTemplatesByType(listIdsSet, type);
				 if(list != null && list.size() > 0) {
					 welcomeMsgList.addAll(list);
				 }
				 refreshListBox(customTemplates,MESSAGE_UPDATE); 
				 String mode = (String) session.getAttribute("Mode");
				 if(mode.equalsIgnoreCase("edit")) {
				 typeOfAutoEmailListLbId.setDisabled(true);
				 welcomeListLbId.setDisabled(true);
				 }else if(mode.equalsIgnoreCase("add")){
					 typeOfAutoEmailListLbId.setDisabled(false);
					 welcomeListLbId.setDisabled(false);
					 }
			 }
			
			
		}else {
			 List<CustomTemplates> list = customTemplateDao.getTemplatesByType(listIdsSet, DOUBLEOPTIN);
			 if(list != null && list.size() > 0) {
				 
				 welcomeMsgList.addAll(list);
			 }
			refreshListBox(null, MESSAGE_ADD);
			
		}
		
		List<UserFromEmailId> userFromEmailIdList = userFromEmailIdDao.getEmailsByUserId(currentUser.getUserId());
		logger.debug("List size : " + userFromEmailIdList.size());
		
		if(currentUser!=null && currentUser.getEmailId()!=null) {
			cFromEmailCb.appendItem(currentUser.getEmailId());
		}
		if(userFromEmailIdList.size()>0) {
			for(Object obj:userFromEmailIdList) {
				logger.debug("obj : " + obj);
				UserFromEmailId userFromEmailId = (UserFromEmailId)obj;
				logger.debug("userFromEmailId : " + userFromEmailId);
				cFromEmailCb.appendItem(userFromEmailId.getEmailId());
			}
		} 
		
		if(!(cFromEmailCb.getItemCount()>0)) {
			cFromEmailCb.appendItem("No emails registered.");
		}
		cFromEmailCb.setSelectedIndex(0);
		//********************************************************************************
		//**********************SET USERS DEFAULT SENDER ADDRESS***********************************************
		
		loadSettings(customTemplates);
		setAddress(false);
		setUserAddress(null);
		
		
		// Set Editor PH values.
		Set<MailingList> set = new HashSet<MailingList>();
		
		List<MailingList> mlist = mailingListDao.findByIds(listIdsSet);
		if(mlist != null) {
			set.addAll(mlist);
		}
		
		EditorController.getPlaceHolderList(set, true, null);
		
		/* String company = currentUser.getCompanyName();
		 
		 if(company != null) {
			 cFromNameTb.setValue(company);
		 }*/
		
//*******************Insert Coupon place holder in subject field
		
		EditorController.getPlaceHolderList(set, true, insertMergetagsLbId);
		
		
		
		Set<String> userRoleSet = (Set<String>)Sessions.getCurrent().getAttribute("userRoleSet");
			//logger.debug("userRoleSet ::"+userRoleSet);
		/*PageListEnum plEnum = PageListEnum.ADMIN_VIEW_COUPONS;
		if(userRoleSet.contains(plEnum.getPageRightEnum().name())) {*/
			if(userRoleSet.contains(RightsEnum.MenuItem_Promocodes_VIEW.name())) {

			List<String> couponPhList = EditorController.getCouponsList();
			if(couponPhList == null || couponPhList.size() == 0) {
				
				logger.debug("got no Promo-code for this org");
				return;
			}
			Listitem item = null;
			Listitem coupItem = null;
			
			for (String phStr : couponPhList) {
				
				if(phStr.indexOf(Constants.DELIMETER_DOUBLECOLON) < 0) return;
				String[] tokens = phStr.split(Constants.DELIMETER_DOUBLECOLON);
				item = new Listitem(tokens[0], tokens[1]);
				
				item.setParent(couponLbId);
				
				coupItem = new Listitem(tokens[0], tokens[1]);
				
				coupItem.setParent(insertCouponLbId);
				
				
				
			}//for
			
			insertCouponLbId.setVisible(true);
		}//if
		/*if(isAdmin) {
			List<String> couponPhList = EditorController.getCouponsList();
			if(couponPhList == null || couponPhList.size() == 0) {
				
				logger.debug("got no Promo-code for this org");
				return;
			}
			Listitem item = null;
			Listitem coupItem = null;
			
			for (String phStr : couponPhList) {
				
				if(phStr.indexOf(Constants.DELIMETER_DOUBLECOLON) < 0) return;
				String[] tokens = phStr.split(Constants.DELIMETER_DOUBLECOLON);
				item = new Listitem(tokens[0], tokens[1]);
				
				item.setParent(couponLbId);
				
				coupItem = new Listitem(tokens[0], tokens[1]);
				
				coupItem.setParent(insertCouponLbId);
				
				
				
			}//for
		
		}*/
		
		session.setAttribute("EditorType","ckEditor");
	}
	
/*public  List<String> getPlaceHolderList(Set<MailingList> mlistSet) {
		
	try {
		
		logger.debug("-- Just Entered --");
		MLCustomFieldsDao mlCustomFieldsDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
		logger.debug("Got Ml Set of size :" + mlistSet.size());
		
		List<String> placeHoldersList = new ArrayList<String>(); 
		//placeHoldersList.addAll(Constants.PLACEHOLDERS_LIST);
		POSMappingDao posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
		
		List<POSMapping> contactsGENList = posMappingDao.findOnlyByGenType(Constants.POS_MAPPING_TYPE_CONTACTS, GetUser.getUserId() );
		
		placeHoldersList.addAll(Constants.PLACEHOLDERS_LIST);
		
		//Changes to add mapped UDF fields as placeholders
		//1.get all the pos mapped UDFs from the user pos settings(table:pos_mappings)
		List<POSMapping> contactsUDFList = posMappingDao.findOnlyByType(Constants.POS_MAPPING_TYPE_CONTACTS, GetUser.getUserId() );
			
		if(contactsUDFList != null) {
				
				//2.prepare merge tag and add to placeHoldersList
				//format : display lable :: |^GEN_<UDF>^|
				for (POSMapping posMapping : contactsUDFList) {
					
					String udfStr;
					if(posMapping.getDefaultPhValue()==null || posMapping.getDefaultPhValue().trim().isEmpty()) {
					
					udfStr = Constants.UDF_TOKEN + posMapping.getDisplayLabel() +
									Constants.DELIMETER_DOUBLECOLON +
									Constants.CF_START_TAG + Constants.UDF_TOKEN +
									posMapping.getCustomFieldName()  + Constants.CF_END_TAG ;
					
					
					}
					else {
						 udfStr = Constants.UDF_TOKEN + posMapping.getDisplayLabel() +
								Constants.DELIMETER_DOUBLECOLON +
								Constants.CF_START_TAG + Constants.UDF_TOKEN +
								posMapping.getCustomFieldName()+ Constants.DELIMETER_SLASH + posMapping.getDefaultPhValue() + Constants.CF_END_TAG ;
				
					
					}
					placeHoldersList.add(udfStr);
				}//for
				
				
				
			}//if
		
		
		for (MailingList mailingList : mlistSet) {
			if(!mailingList.isCustField())  continue;
			
			List<MLCustomFields> mlcust = mlCustomFieldsDao.findAllByList(mailingList);
			String custField ;
			for (MLCustomFields customField : mlcust) {
				custField = Constants.CF_TOKEN + customField.getCustFieldName() 
							+ Constants.DELIMETER_DOUBLECOLON + Constants.CF_START_TAG + 
							Constants.CF_TOKEN + 
							customField.getCustFieldName().toLowerCase() + Constants.CF_END_TAG;
				
				if(placeHoldersList.contains(custField)) continue;
				placeHoldersList.add(custField);
			}
			
		} // for
		
		Listitem item = null;
		for (String placeHolder : placeHoldersList) {
			
			if(placeHolder.trim().startsWith("--") || placeHolder.toLowerCase().contains(("place holder"))) { //Ignore
				continue;
			}
				
			if(placeHolder.startsWith("Unsubscribe Link") || placeHolder.startsWith("Web-Page Version Link") ||
					placeHolder.startsWith("Share on Twitter") || placeHolder.startsWith("Share on Facebook") ||
					placeHolder.startsWith("Forward To Friend")){
				continue;
			}
			String[] phTokenArr =  placeHolder.split("::"); 
			String key = phTokenArr[0];
			StringBuffer value = new StringBuffer(phTokenArr[1]);
			logger.debug("key ::"+key+" value ::"+value);
			for (POSMapping posMapping : contactsGENList) {
				
				if(!key.equalsIgnoreCase(posMapping.getCustomFieldName()) || posMapping.getCustomFieldName().startsWith("UDF")  ) continue;
				
				if(posMapping.getDefaultPhValue() == null || posMapping.getDefaultPhValue().isEmpty() ) break;
				
				value.insert(value.lastIndexOf("^"),Constants.DELIMETER_SLASH + posMapping.getDefaultPhValue() );
				logger.debug(" value ::"+value);
			}
			
			item =  new Listitem(key,value.toString());
			item.setParent(insertMergetagsLbId);
			
			
		} // for
		
		
			logger.debug("-- Exit --");
			return placeHoldersList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}
	}*/
			
	
	
	
public void onSelect$insertMergetagsLbId() {
	
	if(insertMergetagsLbId.getSelectedIndex() == 0) {
		
		return;
	}
	
	
	insertText((String)insertMergetagsLbId.getSelectedItem().getValue());
	insertMergetagsLbId.setSelectedIndex(0);
	
}
	
	
	public void onSelect$insertCouponLbId() {
		
		
		if(insertCouponLbId.getSelectedIndex() <= 0) {
			
			logger.debug("selected 0");
			return ;
			
		}
		
		
	/*	String replaceStr = Constants.CF_START_TAG+Constants.CC_TOKEN+selCoupon.getCouponId()
							+"_"+selCoupon.getCouponName()+Constants.CF_END_TAG;*/
		
		insertText((String)insertCouponLbId.getSelectedItem().getValue());
		
		insertCouponLbId.setSelectedIndex(0);
		
	}

	public void onChange$caretPosTB(){
		logger.debug("---just entered----");
	}
	
	
	public void onBlur$caretPosTB() {
		
		logger.debug("-----just entered: onBlur event----");
		
	}
	
	public void insertText(String  value){
		logger.info("insertText");
		//String value = item.getValue();
		
		
		String cp = caretPosTB.getValue();
		if (cp == null || cp.length() == 0) {
		cp = "0";
		}
		
		if(htmlTbId.getValue().trim().length() == 0) {
			
			cp="0";
		}
		
		
		try{
		int caretPos = Integer.parseInt(cp);
		if (caretPos != -1) {
		String currentValue = htmlTbId.getValue();
		String newValue = currentValue.substring(0, caretPos) + value + currentValue.substring(caretPos);
		
		htmlTbId.setValue(newValue);
		
		
		
		//sessionScope.put("messageContent", SMSMsgTbId.getValue());
		//personalizationTagsWinId.setVisible(false);
		htmlTbId.focus();
		
		}
		}catch(Exception e) {
		logger.error("Exception ::", e);
		}
		}
	
	
	//************** Insert coupons place holders in the subject textbox cursor position	
		public void onClick$couponTbId() {

			try{
						
			 String value = couponLbId.getSelectedItem().getValue();

			 String cp = caretPosTB.getValue();
	         if (cp == null || cp.length() == 0) {
	             cp = "0";
	         }
	         int caretPos = Integer.parseInt(cp);
	         if (caretPos != -1) {
	             String currentValue = cSubTb.getValue();
	             String newValue = currentValue.substring(0, caretPos) + value + currentValue.substring(caretPos);
	             cSubTb.setValue(newValue);
	         }
	         
	         cSubTb.focus();
	         }catch(Exception e) {
	        	 logger.error("Exception ::", e);
	         }
		}

		
		
		
	//************************************************************************************	

	
	
	public void refreshListBox(CustomTemplates customTemplates,int flag) {
    	
		try {
			Components.removeAllChildren(welcomeListLbId);
			logger.debug("Got CustomTemplates list of size :" + welcomeMsgList.size()+" the first element "+welcomeMsgList.get(0));
			
			if(customTemplates != null && flag == MESSAGE_ADD) {
				welcomeMsgList.add(customTemplates);
			}
			
			Listitem item = null;
			CustomTemplates custTemp = null;
			
			
			for(Object obj : welcomeMsgList) {
				 if(obj instanceof String) {
						
					item = new Listitem("Default Message", null);
					item.setParent(welcomeListLbId);
					
						
						
				}
				else if(obj instanceof CustomTemplates) {
						
						
					custTemp = (CustomTemplates)obj;
					
					item = new Listitem(custTemp.getTemplateName(), custTemp);
					item.setParent(welcomeListLbId);
					
				}
						
			
				 
			}
			if(customTemplates != null) {
				for (Listitem items : welcomeListLbId.getItems()) {
					if(items.getLabel().equals(customTemplates.getTemplateName())) {
						
						welcomeListLbId.setSelectedIndex(items.getIndex());
						break;
					}
					
					
				}
			}else{
				logger.info("selected 0 th item");
				welcomeListLbId.setSelectedIndex(0);
				
			}
			/*if(welcomeListLbId.getItemCount() > 0){
				welcomeListLbId.setSelectedIndex(0);
			}*/
			//onSelect$welcomeListLbId();
			
			
			String editorType = Constants.EDITOR_TYPE_PLAIN;
			if(customTemplates != null ) {
				
				editorType = customTemplates.getEditorType();
				
			}
			customtemplateSettings(editorType.equals(Constants.EDITOR_TYPE_PLAIN));
			
		} finally {
			if(welcomeListLbId.getItemCount() > 0) {
				if(!(welcomeListLbId.getSelectedIndex() > 0)) {
					welcomeListLbId.setSelectedIndex(0);
					loadSettings(null);
				}
				else{
					//welcomeListLbId.setSelectedIndex(item);
					loadSettings(customTemplates);
				}
			}
			
		}
    }
	private Listbox storesLbId;
	public void setStores() {
		
		Listitem item = null;
		
		Components.removeAllChildren(storesLbId);
		
		List<OrganizationStores> storeList = organizationStoresDao.findByOrganization(currentUser.getUserOrganization().getUserOrgId());
		
		for (OrganizationStores organizationStores : storeList) {
			
			item = new Listitem(organizationStores.getStoreName(), organizationStores);
			
			item.setParent(storesLbId);
			
		}//for
		
		if(storesLbId.getItemCount() > 0) {
			storesLbId.setSelectedIndex(0);
			onSelect$storesLbId();
		}
		
		
	}
	 String[] phArray = {"firstName","lastName","fullName"};
	 
	 private Tab textEditorTabId,  plainEditorTabId;
public void loadSettings(CustomTemplates customTemplate) {
		
		if(customTemplate !=  null) {
			
			String htmlContent  = customTemplate.getHtmlText();
			String editorType = customTemplate.getEditorType();
			
			if(editorType != null && editorType.equals(Constants.EDITOR_TYPE_PLAIN)) {
				
				ckEditorId.setValue(htmlContent);
				editorTabboxId.setSelectedTab(plainEditorTabId);
				
			}
			else if(editorType != null && editorType.equals(Constants.EDITOR_TYPE_TEXT)) {
				
				htmlTbId.setValue(htmlContent);
				editorTabboxId.setSelectedTab(textEditorTabId);
				
			}
			
			cSubTb.setValue(customTemplate.getSubject());
			cFromNameTb.setValue(customTemplate.getFromName());
			
			String fromEmailId = customTemplate.getFromEmail();
			for(int index=0;index<cFromEmailCb.getItemCount();index++) {
				
				logger.debug(cFromEmailCb.getItemAtIndex(index).getLabel() + " == " + fromEmailId);
			   if(cFromEmailCb.getItemAtIndex(index).getLabel().equals(fromEmailId)) {
				   cFromEmailCb.setSelectedIndex(index);
			   }  
			}
			
			cPermRemRb.setSelectedIndex(customTemplate.isPermissionRemainderFlag()?0:1);
			if(customTemplate.isPermissionRemainderFlag()){
				permRemDivId.setVisible(true);
				permRemTextId.setValue(customTemplate.getPermissionRemainderText());
			}else{
				
				permRemDivId.setVisible(false);
			}
			cWebPageCb.setChecked(customTemplate.isWebLinkFlag());
			if(customTemplate.isWebLinkFlag()){
				cWebLinkHboxId.setVisible(true);
				cWebLinkTextTb.setValue(customTemplate.getWebLinkText());
				cWebLinkUrlTextTb.setValue(customTemplate.getWebLinkUrlText());
			}else{
				cWebLinkHboxId.setVisible(false);
				
			}
			//personalize to filed 
			
			toNameChkId.setChecked(customTemplate.isPersonalizeTo());
			if(customTemplate.isPersonalizeTo()){
				persToDivId.setVisible(true);
				String to = customTemplate.getToName();
				for(int i=0;i<phArray.length;i++){
					if(phArray[i].equalsIgnoreCase(to)){
						phLbId.setSelectedIndex(i);
					}
				}
			}else{
				persToDivId.setVisible(false);
				
			}
			
	
		orgUnitChkBoxId.setChecked(customTemplate.isIncludeOrgUnit());
		orgNameChkBoxId.setChecked(customTemplate.isIncludeOrg());
			
			
		
		if(customTemplate.isAddressFlag()){
			
			cAddressOneTbId.setValue(currentUser.getAddressOne());
			cAddressOneTbId.setAttribute("value", currentUser.getAddressOne());
			
			cAddressTwoTbId.setValue(currentUser.getAddressTwo());
			cAddressTwoTbId.setAttribute("value", currentUser.getAddressTwo());
			
			
			cCityTbId.setValue(currentUser.getCity());
			cCityTbId.setAttribute("value", currentUser.getCity());
			
			cStateTbId.setValue(currentUser.getState());
			cStateTbId.setAttribute("value", currentUser.getState());
			
			cCountryTbId.setValue(currentUser.getCountry());
			cCountryTbId.setAttribute("value", currentUser.getCountry());
			try {
				if(currentUser.getPinCode()!=null){
					cPinLbId.setValue(""+currentUser.getPinCode());
					cPinLbId.setAttribute("value", currentUser.getPinCode()+"");
				}
			} catch (Exception e) {
				logger.error("** Exception: Problem occured while setting PinCode value : "+e+" **");
			}	
			try {
				if(currentUser.getPhone()!= null){
					cPhoneTbId.setValue(currentUser.getPhone());
					cPhoneTbId.setAttribute("value", currentUser.getPhone());
				}
			} catch (Exception e) {
				logger.error("** Exception: Problem occured while setting updating user information . "+e+" **");
			}
			
			
			
			
			
			String addrType = customTemplate.getAddrType();
			
			if(addrType != null && addrType.equals(Constants.CAMP_ADDRESS_TYPE_USER)) {
				try {
					Address address = customTemplate.getAddress();
					cAddressOneTbId.setValue(address.getAddressOne());
					cAddressOneTbId.setAttribute("value", address.getAddressOne());
					
					cAddressTwoTbId.setValue(address.getAddressTwo());
					cAddressTwoTbId.setAttribute("value", address.getAddressTwo());
					
					
					cCityTbId.setValue(address.getCity());
					cCityTbId.setAttribute("value", address.getCity());
					
					cStateTbId.setValue(address.getState());
					cStateTbId.setAttribute("value", address.getState());
					
					cCountryTbId.setValue(address.getCountry());
					cCountryTbId.setAttribute("value", address.getCountry());
					try {
						if(address.getPin()!=null){
							cPinLbId.setValue(""+address.getPin());
							cPinLbId.setAttribute("value", ""+address.getPin());
						}
					} catch (Exception e) {
						logger.error("** Exception: Problem occured while setting PinCode value : "+e+" **");
					}	
					try {
						if(address.getPhone()!= null){
							cPhoneTbId.setValue(address.getPhone());
							cPhoneTbId.setAttribute("value", address.getPhone());
						}
					} catch (Exception e) {
						logger.error("** Exception: Problem occured while setting updating user information . "+e+" **");
					}
				} catch (WrongValueException e) {
					logger.error("Exception : " + e);
				}	
				addrRgId.setSelectedIndex(0);
			}else if(addrType != null && addrType.startsWith(Constants.CAMP_ADDRESS_TYPE_STORE)){
				
				if(storesLbId.getItemCount() > 0) {
					
					storesLbId.setSelectedIndex(getStore(addrType.split("\\|")[1].trim()));
					onSelect$storesLbId();
					addrRgId.setSelectedIndex(1);
				}else{
					
					addrRgId.setSelectedIndex(0);
				}
				
			}
		}
		
	}else{
		try {
			
			defaultSettings();
			
			cAddressOneTbId.setValue(currentUser.getAddressOne());
			cAddressOneTbId.setAttribute("value", currentUser.getAddressOne());
			
			
			cAddressTwoTbId.setValue(currentUser.getAddressTwo());
			cAddressTwoTbId.setAttribute("value", currentUser.getAddressTwo());
			
			cCityTbId.setValue(currentUser.getCity());
			cCityTbId.setAttribute("value", currentUser.getCity());
			
			cStateTbId.setValue(currentUser.getState());
			cStateTbId.setAttribute("value", currentUser.getState());
			
			cCountryTbId.setValue(currentUser.getCountry());
			cCountryTbId.setAttribute("value", currentUser.getCountry());
			

			try {
				if(currentUser.getPinCode()!=null){
				  if(currentUser.getPinCode().length()!=0)
					cPinLbId.setValue((currentUser.getPinCode().trim()));
				  cPinLbId.setAttribute("value", (currentUser.getPinCode().trim()));
					
				  
				}
			} catch (Exception e) {
				logger.error("** Exception: Problem occured while setting PinCode value : "+e+" **");
			}	
			try {
				if(currentUser.getPhone()!=null){
					if(currentUser.getPhone().trim().length()!=0)
					cPhoneTbId.setValue(currentUser.getPhone());
					cPhoneTbId.setAttribute("value", currentUser.getPhone());
				}
			} catch (Exception e) {
				logger.error("** Exception: Problem occured while setting updating user information . "+e+" **");
			}
			
			setUserAddr();
		}catch(Exception e){
			logger.error("** Exception: Problem occured while setting updating user information . "+e+" **");
		}

	//	addrRgId.setSelectedIndex(0);
	}//else

		
		
		
		
		
		
	}
	
public void defaultSettings() {
		
	int typeOfAutoEmailIndex=typeOfAutoEmailListLbId.getSelectedIndex();
	switch(typeOfAutoEmailIndex)
	{
	case 0:
		cSubTb.setValue("Confirmation Request");
		break;
	case 1:
		cSubTb.setValue("Require Parental Consent");
		break;
	case 2:
		cSubTb.setValue("Welcome to "+currentUser.getUserOrganization().getOrganizationName()+"'s Loyalty Program!");
		break;
	case 3:
		cSubTb.setValue("Welcome Mail");
		break;
	case 4:
		cSubTb.setValue("Thank you for purchasing Gift Card from "+currentUser.getUserOrganization().getOrganizationName()+"!");
		break;
	case 5:
		cSubTb.setValue(currentUser.getUserOrganization().getOrganizationName()+"'s Loyalty Program - Membership Upgraded!");
		break;
	case 6:
		cSubTb.setValue(currentUser.getUserOrganization().getOrganizationName()+"'s Loyalty Program - Earned Additional Reward!");
		break;
	case 7:
		cSubTb.setValue(currentUser.getUserOrganization().getOrganizationName()+"'s Loyalty Program - Reward Expiring!");
		break;
	case 8:
		cSubTb.setValue(currentUser.getUserOrganization().getOrganizationName()+"'s Loyalty Program - Membership Expiring!");
		break;
	case 9:
		cSubTb.setValue("Your "+currentUser.getUserOrganization().getOrganizationName()+"'s Gift Amount Is Expiring!");
		break;
	case 10:
		cSubTb.setValue("Your "+currentUser.getUserOrganization().getOrganizationName()+"'s Gift Card Is Expiring!");
		break;
	default: 
		cSubTb.setValue("");
		break;
	}
		cFromNameTb.setValue(currentUser.getCompanyName());
		
		addrRgId.setSelectedIndex(0);
		 
		orgNameChkBoxId.setChecked(true);
		orgUnitChkBoxId.setChecked(false);
		
		if(storesLbId.getItemCount() > 0) {
			
			storesLbId.setSelectedIndex(0);
	//		onSelect$storesLbId();
			
			
		}
		if(gbContentDivId.isVisible()) {
			onClick$optSettingsTbBtnId();
		}
		
		if(typeOfAutoEmailListLbId.getSelectedIndex() == 0) {
			
			weblinkVersionDivId.setVisible(false);
		}else{
			weblinkVersionDivId.setVisible(true);
		}
		
		cWebPageCb.setChecked(true);
		cWebLinkHboxId.setVisible(cWebPageCb.isChecked());
		
		cPermRemRb.setSelectedIndex(1);
		permRemDivId.setVisible(false);
		
		
		toNameChkId.setChecked(false);
		persToDivId.setVisible(false);
		
		
		editorTabboxId.setSelectedTab(plainEditorTabId);
		ckEditorId.setValue(PropertyUtil.getPropertyValueFromDB((String)typeOfAutoEmailListLbId.getSelectedItem().getValue()));
		
		
	}
	
public int getStore(String storeId){
	
	int index = 0;
	String orgName = organizationStoresDao.findNameById(storeId, currentUser.getUserOrganization().getUserOrgId());
	
	if(orgName == null){
		logger.debug("got no store with configured custom template");
		
		addrRgId.setSelectedIndex(0);
		return index;
		
		
	}
	
	for (Listitem item : storesLbId.getItems()) {
		
		if(orgName.trim().equals(item.getLabel())) {
			
			item.setSelected(true);
			index = item.getIndex();
			break;
		}
		
	}//for
	
	
	return index;
	
}//getStore
	
	
public void setAddress(boolean isModifyUser) {
	
	try{
		
		String postalAddrStr = "";
		if(isModifyUser && postalAddressDivId.isVisible() ) {

			postalAddressDivId.setVisible(false);
			changeAddressDivId.setVisible(true);
		} else {
			
			String value = null;
			
			value = cAddressOneTbId.getValue();
			if(value == null || value.equals("")) {
				
				MessageUtil.setMessage("Please provide Address Line 1.", "color:red;");
				
				return ;
				
			}else{
				
				cAddressOneTbId.setAttribute("value", value);
				postalAddrStr += value+", ";
			}
			
			
			
			value = cAddressTwoTbId.getValue();
			if(value != null && !value.equals("")) {
				
				cAddressTwoTbId.setAttribute("value", value);
				postalAddrStr += value+", ";
				
			}
			
			value = cCityTbId.getValue();
			if(value == null || value.equals("")) {
				
				MessageUtil.setMessage("Please provide City.", "color:red;");
				
				return ;
				
			}else{
				
				cCityTbId.setAttribute("value", value);
				postalAddrStr += value+", ";
			}
			
			value = cStateTbId.getValue();
			if(value == null || value.equals("")) {
				
				MessageUtil.setMessage("Please provide State.", "color:red;");
				
				return ;
				
			}else{
				
				cStateTbId.setAttribute("value", value);
				postalAddrStr += value+", ";
			}
			
			value = cCountryTbId.getValue();
			if(value == null || value.equals("")) {
				
				MessageUtil.setMessage("Please provide Country.", "color:red;");
				
				return ;
				
			}else{
				
				cCountryTbId.setAttribute("value", value);
				postalAddrStr += value+", ";
			}
			
			String pinValue = cPinLbId.getValue().trim();
			String countryType = currentUser.getCountryType();
			
			if(Utility.zipValidateMap.containsKey(countryType)){
				
			if(pinValue.length() == 0 || pinValue.equals("")) {		
				MessageUtil.setMessage("Pin / Zip code cannot be left empty.", "color:red;");			
				return ;			
			}
			boolean validZip = Utility.validateZipCode(pinValue, countryType);
			 if(!validZip){
				 MessageUtil.setMessage("Please enter valid zip code.","color:red;");
					return ;
			 }else {
				 cPinLbId.setAttribute("value", pinValue.trim());
					postalAddrStr += pinValue;
			 }
			 
			}else{	
				
				if(pinValue != null && pinValue.length() > 0){
					
					try{
						
						Long pinLong = Long.parseLong(pinValue);
						
		      } catch (NumberFormatException e) {
						MessageUtil.setMessage("Please provide 5 / 6 digits Zip Code.","color:red;");
						return ;
		      }
					
				if(pinValue.length() > 6 || pinValue.length() < 5) {
					
					//	Messagebox.show("Please provide 5 / 6 digits Zip code / pin.");
						MessageUtil.setMessage("Please provide 5 / 6 digits Zip code / Pin.", "Color:red", "Top");
						return;
						
					}else {
						cPinLbId.setAttribute("value", pinValue.trim());
						postalAddrStr += pinValue;
					}
				}
			}
			/*if(pinValue.length() == 0 ) {
				
				MessageUtil.setMessage("Please provide Pin.", "color:red;");
				
				return ;
				
			}else{
				
				if((pinValue).length() > 6 || (pinValue).length() < 5 ){ 
					MessageUtil.setMessage("Please provide 5 / 6 digits Zip code / Pin.", "color:red;");
					
					return;
				}
				
				try {
					long pVal = Long.parseLong(pinValue);
				} catch (Exception e) {
					MessageUtil.setMessage("Please provide only number for Zip code / Pin.", "color:red;");
					return;
				}
				
				cPinLbId.setAttribute("value", pinValue);
				postalAddrStr += pinValue;
			}*/
			
			
			value = cPhoneTbId.getValue().trim();
			if(value != null  && value.trim().length() > 0) {
				

				try{
					
					
					String userPhoneRegex = "\\d+";
					Pattern phonePattern = Pattern.compile(userPhoneRegex);  
					Matcher m = phonePattern.matcher(value);//(value);
					String poneMatch = "";
					while (m.find()) {
						poneMatch += m.group();
					}
					try {
						value  = ""+Long.parseLong(poneMatch);
					} catch (NumberFormatException e) {
						MessageUtil.setMessage("Please provide valid Phone Number.", "color:red", "TOP");
						cPhoneTbId.setFocus(true);
						return;
					}
					UserOrganization organization = currentUser!=null ? currentUser.getUserOrganization(): null;
					value =  Utility.phoneParse(value,organization);
					if(value == null || value.trim().length() == 0){

						MessageUtil.setMessage("Please provide valid Phone Number.", "Color:Red", "Top");
					//long phone = Long.parseLong(value);
						return;
						
					}
					
					cPhoneTbId.setAttribute("value", ""+value);
					postalAddrStr += ", "+value;
					
				}catch (Exception e) {
					
					MessageUtil.setMessage("Please provide valid Phone Number.", "color:red", "TOP");
					cPhoneTbId.setFocus(true);
					return;
				
				}
				
				//address.setPhone(""+cPhoneTbId.getValue().trim());
			
				
				
				
				
				
				
				
				
				
				
				
				////
				
//				Long phone = null;
				try {
					/*if(!value.trim().equals("")) {
					
						phone = Long.parseLong(value);
						cPhoneTbId.setAttribute("value", ""+phone);
						postalAddrStr += ", "+phone;
					}
					else {
						cPhoneTbId.setAttribute("value", ""+value);
						postalAddrStr += value;
						
					}*/
					
					/*if(value.length() > 0 ){
						
						if( !Utility.validateUserPhoneNum(value) ) {
							MessageUtil.setMessage("Please provide valid Phone number.", "color:red;");
							return;
						}
							
						else {
							cPhoneTbId.setAttribute("value", ""+value);
							postalAddrStr += ", "+value;;
							
						}
					
					}else {
						
						cPhoneTbId.setAttribute("value", ""+value);
						postalAddrStr += value;
					}*/
					
				} catch (NumberFormatException e) {

					MessageUtil.setMessage("Please provide valid Phone number.", "color:red;");
					
					return ;
				}
				
				
			}else {
				
				cPhoneTbId.setAttribute("value", ""+value);
				postalAddrStr += value;
			}
			
			postalAddressRbId.setLabel(postalAddrStr);
			postalAddressDivId.setVisible(true);
			changeAddressDivId.setVisible(false);
			
			//if it the logged in user
			if(isModifyUser) {
				 if (Messagebox.show("Address fields are modified successfully. Do you want to apply \n address data to your user-account?", "Prompt", 
		        		  Messagebox.YES|Messagebox.NO, Messagebox.QUESTION) == Messagebox.NO) {
		            return;
		          }
				
				currentUser.setAddressOne(cAddressOneTbId.getValue());
				
				if(cAddressTwoTbId.getValue() != null) {
					
					currentUser.setAddressTwo(cAddressTwoTbId.getValue());
				}
				currentUser.setCity(cCityTbId.getValue());
				currentUser.setCountry(cCountryTbId.getValue());
				currentUser.setPinCode(cPinLbId.getValue().trim());
				
				if(cPhoneTbId.getValue() != null || cPhoneTbId.getValue().trim().length() > 0) {
					
					currentUser.setPhone(cPhoneTbId.getValue().trim());
				 
				}
					//usersDao.saveOrUpdate(currentUser);
				    usersDaoForDML.saveOrUpdate(currentUser);
				
			}//if
		}//else
			
		
	}catch (Exception e) {
		logger.error("** Exception while saving the postalAddress--:",e);
	}


}

public void setUserAddress(CustomTemplates customtemplate) {
	
	setUserAddr();
	
	if(customtemplate == null) {
	
	
		if( (currentUser.getAddressOne() == null || currentUser.getAddressOne().equals("") ) || 
				(currentUser.getCity() == null || currentUser.getCity().equals("")) ||
				(currentUser.getState() == null || currentUser.getState().equals("")) ||
				(currentUser.getCountry() == null || currentUser.getCountry().equals("")) ||
				(currentUser.getPinCode() == null || currentUser.getPinCode().equals("")) ) {
				
				changeAddressDivId.setVisible(true);
				postalAddressDivId.setVisible(false);
				
				
			}
		else{
			postalAddressDivId.setVisible(true);
			changeAddressDivId.setVisible(false);
		}
	}else {
			if(customtemplate.isAddressFlag()) {
				Address address = customtemplate.getAddress();
				if(customtemplate.getAddrType() != null && customtemplate.getAddrType().equalsIgnoreCase("User") ) {
				
					if( (address.getAddressOne() == null || address.getAddressOne().equals("") ) ||
							(address.getCity() == null || address.getCity().equals("")) ||
							(address.getState() == null || address.getState().equals("")) || 
							(address.getCountry() == null || address.getCountry().equals("") )||
							(address.getPin() == null  ) ) {
						
						
						changeAddressDivId.setVisible(true);
						postalAddressDivId.setVisible(false);
						
						
					}else {
						
						postalAddressDivId.setVisible(true);
						changeAddressDivId.setVisible(false);
						
					}
				}
				
			}//if
		
		
	}//else
		
	
}//setUserAddress

public void setUserAddr() {
	
	String postalAddressData = "";
	
	String value = null;
	
	value = (String)cAddressOneTbId.getAttribute("value");
	if(value != null && !value.equals("")) {
		
		postalAddressData += value+", ";
		
	}
	
	value = (String)cAddressTwoTbId.getAttribute("value");
	if(value != null && !value.equals("")) {
		
		postalAddressData += value+", ";
		
	}
	
	value = (String)cCityTbId.getAttribute("value");
	if(value != null && !value.equals("")) {
		
		postalAddressData += value+", ";
		
	}
	
	value = (String)cStateTbId.getAttribute("value");
	if(value != null && !value.equals("")) {
		
		postalAddressData += value+", ";
		
	}
	
	value = (String)cCountryTbId.getAttribute("value");
	if(value != null && !value.equals("")) {
		
		postalAddressData += value+", ";
		
	}
	
	String pinValue = (String)cPinLbId.getAttribute("value");
	if(pinValue != null ) {
		
		postalAddressData += pinValue;
		
	}
	
	value = (String)cPhoneTbId.getAttribute("value");
	if(value != null && !value.equals("")) {
		
		postalAddressData += ", "+value;
		
	}
	
	postalAddressRbId.setLabel(postalAddressData);
	

}
	
	public void onSelect$typeOfAutoEmailListLbId(){
		//select list item 
		Components.removeAllChildren(welcomeListLbId);
		/*CustomTemplates customTemplates = null;
		welcomeMsgList = new ArrayList();
		welcomeMsgList.add("Default message")*/;
		
		//welcomeMsgList.addAll(customTemplateDao.getTemplatesByType(userIdsSet, "welcomemail"));
		if(typeOfAutoEmailListLbId.getSelectedCount() == 0) {
			
			//TODO validation
			MessageUtil.setMessage("There are no email types found.", "green");
			return;
		}
		
		Listitem selItem = typeOfAutoEmailListLbId.getSelectedItem();
		
		if(selItem == null) {
			//TODO logger
			logger.info("There are no items selected to select "+selItem);
			return;
		}
		String value= selItem.getValue();
		ckEditorId.setValue(PropertyUtil.getPropertyValueFromDB(value));
		editorTabboxId.setSelectedTab(plainEditorTabId);
		
		logger.info("value ::"+value+" selected index:: "+typeOfAutoEmailListLbId.getSelectedIndex()+" sel "+selItem.getValue());

		String type = getTemplateType() ;
		
		
		
	
		welcomeMsgList = new ArrayList();
		welcomeMsgList.add("Default message");
		
		 List<CustomTemplates> list = customTemplateDao.getTemplatesByType(listIdsSet, type);
		 
		 if(list != null && list.size() > 0) {
			 
			 welcomeMsgList.addAll(list);
		 }
		 
		
	
		refreshListBox(null, MESSAGE_ADD);
		
		// based on item populate data 
		
		
		
		
		
		
		
	}
	
	private Label persLbId;
	private Div personalizeToDivId;
	private Div permRemindDivId, weblinkVersionDivId;
	public String getTemplateType() {
		
		String type = null;
		
		if(typeOfAutoEmailListLbId.getSelectedIndex() == 0) {
			
			type = DOUBLEOPTIN;
			personalizeToDivId.setVisible(true);
			weblinkVersionDivId.setVisible(false);
			
		}
		else if(typeOfAutoEmailListLbId.getSelectedIndex() == 1){
			
			type = PARENTALCONSENT;
			personalizeToDivId.setVisible(false);
			permRemindDivId.setVisible(false);
			/*toNameChkId.setVisible(false);
			persLbId.setVisible(false);
			persToDivId.setVisible(false);
		*/
			
			
		}else if(typeOfAutoEmailListLbId.getSelectedIndex() == 2){

			type = LOYALTYOPTIN;
			personalizeToDivId.setVisible(true);
			
			
		}else if(typeOfAutoEmailListLbId.getSelectedIndex() == 3){
			type = WEBFORMEMAIL;
			personalizeToDivId.setVisible(true);
			
		}
		else if(typeOfAutoEmailListLbId.getSelectedIndex() == 4){
			type = GIFTCARDISSUANCE;
			personalizeToDivId.setVisible(true);
			
		}else if(typeOfAutoEmailListLbId.getSelectedIndex() == 5){
			type = TIERUPGRADATION;
			personalizeToDivId.setVisible(true);
			
		}else if(typeOfAutoEmailListLbId.getSelectedIndex() == 6){
			type = EARNEDBONUS;
			personalizeToDivId.setVisible(true);
			
		}else if(typeOfAutoEmailListLbId.getSelectedIndex() == 7){
			type = EARNEDREWARDEXPIRATION;
			personalizeToDivId.setVisible(true);
			
		}else if(typeOfAutoEmailListLbId.getSelectedIndex() == 8){
			type = MEMBERSHIPEXPIRATION;
			personalizeToDivId.setVisible(true);
			
		}else if(typeOfAutoEmailListLbId.getSelectedIndex() == 9){
			type = GIFTAMOUNTEXPIRATION;
			personalizeToDivId.setVisible(true);
			
		}else if(typeOfAutoEmailListLbId.getSelectedIndex() == 10){
			type=GIFTCARDEXPIRATION;
			personalizeToDivId.setVisible(true);
			
		}
		
		
		return type;
	}
	
	
	 
	public void customtemplateSettings(boolean isEditor) {

		try {
			MessageUtil.clearMessage();
			
			if(welcomeListLbId.getSelectedIndex() == 0) {
				//logger.info("get value from db is "+PropertyUtil.getPropertyValueFromDB((String)typeOfAutoEmailListLbId.getSelectedItem().getValue()));
				//createNewMessage();
				ckEditorId.setValue(PropertyUtil.getPropertyValueFromDB((String)typeOfAutoEmailListLbId.getSelectedItem().getValue()));
				loadSettings(null);
				delBtnId.setVisible(false);
				//return;
			}else {
				updateBtnId.setVisible(true);
				delBtnId.setVisible(true);
				sendTestMsgBtnId.setVisible(true);
				
			CustomTemplates customTemplates = (CustomTemplates)welcomeListLbId.getSelectedItem().getValue();
			logger.info(">>>>3"+welcomeListLbId.getSelectedItem().getValue());
			
			/*if(isEditor) ckEditorId.setValue(customTemplates.getHtmlText());
			else htmlTbId.setText(customTemplates.getHtmlText());*/
			
			/*if(typeOfAutoEmailListLbId.getSelectedIndex() == 0){
				
				
			}else if(typeOfAutoEmailListLbId.getSelectedIndex() == 1){
				
				if(isEditor) ckEditorId.setValue(customTemplates.getHtmlText());
				else htmlTbId.setText(customTemplates.getHtmlText());
				
			//	persToDivId.setVisible(false);
				personalizeToDivId.setVisible(false);
				permRemDivId.setVisible(false);
				
			}else if(typeOfAutoEmailListLbId.getSelectedIndex() ==2){
				
				ckEditorId.setValue(customTemplates.getHtmlText());
			}else if(typeOfAutoEmailListLbId.getSelectedIndex() == 3){
				ckEditorId.setValue(customTemplates.getHtmlText());
				
			}*/
			loadSettings(customTemplates);
			setAddress(false);
			setUserAddress(customTemplates);
			
			
			}
			
		} catch (Exception e) {
			logger.error("Exception : ",e);
		}
		
		
	}
	
	
	public void onSelect$welcomeListLbId() {
		
		
		customtemplateSettings(false);
		
	}
	
	
	/**
	 * This method executes when submit button is clicked <br/>
	 * Creates new optin welcome message
	 * @param event
	 */
	public void onClick$submitBtnId$createCustTemplateWinId() {
		
		try {
			if(createCustTemplateWinId$custTempNameTbId.getValue().trim().length() == 0) {
				createCustTemplateWinId$msgLblId.setValue("Please enter custom template name.");
				return;
			}
			String templateName = createCustTemplateWinId$custTempNameTbId.getValue().trim();
			if(checkExist(templateName) ) {
				createCustTemplateWinId$msgLblId.setValue("Template name already exists.");
				return;
			}
			
			CustomTemplates customTemplates = (CustomTemplates)createCustTemplateWinId.getAttribute("newCustomTemplate");
			
			customTemplates.setTemplateName(createCustTemplateWinId$custTempNameTbId.getText());
			
			//customTemplateDao.saveOrUpdate(customTemplates);
			customTemplateDaoForDML.saveOrUpdate(customTemplates);
			if(welcomeMsgList != null) {
				welcomeMsgList.add(customTemplates);
			}
			else {
				welcomeMsgList = new ArrayList();
				welcomeMsgList.add(customTemplates);
			}
			
			createCustTemplateWinId.setVisible(false);
			
			MessageUtil.setMessage("Template saved successfully.", "green");
			refreshListBox(customTemplates,MESSAGE_UPDATE);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} 
		
		
	}
	
	/**
	 * checks whether the new template name already exist in the welcomeMsgList list
	 * @param name - String - New template name
	 * @return boolean - true if exists else false
	 */
	private boolean checkExist(String name) {
		if(welcomeMsgList == null) return false;
		CustomTemplates customTemplate;
		for (Object object : welcomeMsgList) {
			if(object instanceof String) continue;
			customTemplate = (CustomTemplates)object;
			if(customTemplate.getTemplateName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	private Window testMailWinId;
	public void onClick$sendTestMsgBtnId() {

		try {
			
			CustomTemplates custTemp = (CustomTemplates)welcomeListLbId.getSelectedItem().getValue();
			
			testMailWinId.removeAttribute("customTemplate");
			testMailWinId.setAttribute("customTemplate", custTemp);
			 testMailWinId$msgLblId.setValue("");
			testMailWinId.doModal();
		} catch (SuspendNotAllowedException e) {
			logger.error("***** Exception : ",e);
		} catch(Exception e) {
			logger.error("***** Exception : ",e);
		}
	
		
		
		
		
	}
	
	
	
	
	private Textbox testMailWinId$emailIdTbId;
	public void onClick$sendTestMailBtnId$testMailWinId() {
		
		
		CustomTemplates testCustTemp = (CustomTemplates)testMailWinId.getAttribute("customTemplate");
		
		
		
		 String emailId = testMailWinId$emailIdTbId.getValue();
		if(sendTestMail(emailId,testCustTemp)){
		testMailWinId$emailIdTbId.setValue("");
		}
		
	}
	
	
	public void onClick$cancelSendTestMailBtnId$testMailWinId() {
		
		testMailWinId$msgLblId.setValue("");
		testMailWinId$emailIdTbId.setValue("");
		testMailWinId.setVisible(false);
		
	}
	
	private Label testMailWinId$msgLblId;
	boolean sendTestMail(String emailId, CustomTemplates customTemplates){
		try {
			//Label msgLblId = (Label)testMailWinId.getFellow("msgLblId");
			
			String subject = "";
			String htmlContent ="";
			if(typeOfAutoEmailListLbId.getSelectedIndex() == 0){
				subject="Confirmation Request";
				 htmlContent = PropertyUtil.getPropertyValueFromDB("optinMsgTemplate");
			}else if(typeOfAutoEmailListLbId.getSelectedIndex() == 1){
				subject ="Require Parental Consent";
				 htmlContent = PropertyUtil.getPropertyValueFromDB("parentalConsentMsgtemplate");
			}else if(typeOfAutoEmailListLbId.getSelectedIndex() == 2){
				subject = "Welcome to [OrganizationName]'s Loyalty Program!";
				subject = subject.replace("[OrganizationName]", currentUser.getUserOrganization().getOrganizationName());
				htmlContent=PropertyUtil.getPropertyValueFromDB("LoyaltyOptinMsgTemplate");
			}else if(typeOfAutoEmailListLbId.getSelectedIndex() == 3){
				subject = "Welcome Email";
				htmlContent = PropertyUtil.getPropertyValueFromDB("welcomeMsgTemplate");
			}else if(typeOfAutoEmailListLbId.getSelectedIndex() == 4){
				subject = "Thank you for purchasing Gift Card from [OrganizationName]!";
				subject = subject.replace("[OrganizationName]", currentUser.getUserOrganization().getOrganizationName());
				htmlContent = PropertyUtil.getPropertyValueFromDB("giftCardIssMsgTemplate");
			}
			else if(typeOfAutoEmailListLbId.getSelectedIndex() == 5){
				subject = "[OrganizationName]'s Loyalty Program - Membership Upgraded!";
				subject = subject.replace("[OrganizationName]", currentUser.getUserOrganization().getOrganizationName());
				htmlContent = PropertyUtil.getPropertyValueFromDB("tierUpgradationMsgTemplate");
			}
			else if(typeOfAutoEmailListLbId.getSelectedIndex() == 6){
				subject = "[OrganizationName]'s Loyalty Program - Earned Additional Reward!";
				subject = subject.replace("[OrganizationName]", currentUser.getUserOrganization().getOrganizationName());
				htmlContent = PropertyUtil.getPropertyValueFromDB("earnedThresholdBonusMsgTemplate");
			}
			else if(typeOfAutoEmailListLbId.getSelectedIndex() == 7){
				subject = "[OrganizationName]'s Loyalty Program - Reward Expiring!";
				subject = subject.replace("[OrganizationName]", currentUser.getUserOrganization().getOrganizationName());
				htmlContent = PropertyUtil.getPropertyValueFromDB("rewardExpMsgTemplate");
			}
			else if(typeOfAutoEmailListLbId.getSelectedIndex() == 8){
				subject = "[OrganizationName]'s Loyalty Program - Membership Expiring!";
				subject = subject.replace("[OrganizationName]", currentUser.getUserOrganization().getOrganizationName());
				htmlContent = PropertyUtil.getPropertyValueFromDB("memExpMsgTemplate");
			}
			else if(typeOfAutoEmailListLbId.getSelectedIndex() == 9){
				subject = "Your [OrganizationName]'s Gift Amount Is Expiring!";
				subject = subject.replace("[OrganizationName]", currentUser.getUserOrganization().getOrganizationName());
				
				htmlContent = PropertyUtil.getPropertyValueFromDB("giftAmountExpMsgTemplate");
			}
			else if(typeOfAutoEmailListLbId.getSelectedIndex() == 10){
				subject = "Your [OrganizationName]'s Gift Card Is Expiring!";
				subject = subject.replace("[OrganizationName]", currentUser.getUserOrganization().getOrganizationName());
				htmlContent = PropertyUtil.getPropertyValueFromDB("giftCardExpMsgTemplate");
			}
			if(customTemplates != null){
				subject = customTemplates.getSubject();
				htmlContent = customTemplates.getHtmlText();
				
			}
			
			
			
			
			if(emailId != null && emailId.trim().length() > 0) {
				

				
				if(logger.isDebugEnabled())logger.debug("Sending the test mail....");
				
				MessageUtil.clearMessage();
				
				String isValidPhStr = null;
				isValidPhStr = Utility.validatePh(htmlContent, currentUser);
				
				if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
					
					MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
					return false;
				}else if( isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
					
					MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
					return false;
					
				}
				
				String isValidCCDim = null;
				isValidCCDim = Utility.validateCouponDimensions(htmlContent);
				if(isValidCCDim != null){
					return false;
				}
				String isValidCouponAndBarcode = null;
				isValidCouponAndBarcode = Utility.validateCCPh(htmlContent, currentUser);
				if(isValidCouponAndBarcode != null){
					return false;
				}
				
				
				if(Utility.validateHtmlSize(htmlContent)>100) {
					
					Messagebox.show("HTML size cannot exceed 100kb. Please reduce the" +
							" size and try again.", "Error", Messagebox.OK, Messagebox.ERROR);
					return false;
				}
				
				String[] emailArr = null;
				
				emailArr = emailId.split(",");
				for (String email : emailArr) {
					
					if(!Utility.validateEmail(email.trim())){
						testMailWinId$msgLblId.setValue("Invalid Email Id:");
						return false;
					}
					
					//Utility.sendTestMail(campaign, campaign.getHtmlText(), email);
					
					
				}//for
				
				//Check whether user is expired or not
				if(Calendar.getInstance().after(currentUser.getPackageExpiryDate())){
					testMailWinId.setVisible(false);
					logger.debug("Current User::"+currentUser.getUserId()+" is expired, hence cannot send test mail");
					MessageUtil.setMessage("Your account validity period has expired. Please renew your subscription to continue.", "color:red", "TOP");
					return false;
				}
				
				for (String email : emailArr) {
					Utility.sendInstantMail(null, subject, htmlContent,
							Constants.EQ_TYPE_TEST_OPTIN_MAIL, email,customTemplates );
					
							
				}//for
				
				
				testMailWinId.setVisible(false);
				MessageUtil.setMessage("Test email will be sent in a moment.", "color:blue", "TOP");
				
				
			}else {
				testMailWinId$msgLblId.setValue("Invalid Email Id");
				testMailWinId$msgLblId.setVisible(true);
			}
			return true;
		}
		catch(Exception e) {
			logger.error("** Exception : " + e);
			return false;
		}
		
	}
	
	private Div gbContentDivId,basicSettingsContentDivId, addrContentDivId;
	private Toolbarbutton optSettingsTbBtnId, basicSettingsTbBtnId, addrSettingsTbBtnId;
	public void onClick$optSettingsTbBtnId() {
		
		gbContentDivId.setVisible(!gbContentDivId.isVisible());
		
		String image = gbContentDivId.isVisible() ? "/img/icons/icon_minus.png" : "/img/icons/icon_plus.png";
		
		
		optSettingsTbBtnId.setImage(image);
	}
	public void onClick$basicSettingsTbBtnId() {
		
		basicSettingsContentDivId.setVisible(!basicSettingsContentDivId.isVisible());
		
		String image = basicSettingsContentDivId.isVisible() ? "/img/icons/icon_minus.png" : "/img/icons/icon_plus.png";
		
		
		basicSettingsTbBtnId.setImage(image);
	}
	
	
	
	public void onClick$addrSettingsTbBtnId() {
		
		addrContentDivId.setVisible(!addrContentDivId.isVisible());
		
		String image = addrContentDivId.isVisible() ? "/img/icons/icon_minus.png" : "/img/icons/icon_plus.png";
		
		
		addrSettingsTbBtnId.setImage(image);
	}
public void onSelect$storesLbId () {
		
		
		OrganizationStores orgStore = (OrganizationStores)storesLbId.getSelectedItem().getValue();
		
		String storeAddrStr = "";
		if(orgStore.isAddressFlag()) {
			
			storeAddrStr = orgStore.getAddressStr().trim(); 
			if(storeAddrStr.contains(Constants.ADDR_COL_DELIMETER+Constants.ADDR_COL_DELIMETER)) {
				
				storeAddrStr = storeAddrStr.replace(Constants.ADDR_COL_DELIMETER+Constants.ADDR_COL_DELIMETER, ", ");
				
			}
			
			//logger.debug("addr===>"+storeAddrStr);
			storeAddrStr = storeAddrStr.replace(Constants.ADDR_COL_DELIMETER, ", ");
			storeAddrStr = storeAddrStr.trim();
			
			//logger.debug("addr ===>"+storeAddrStr);
			if(storeAddrStr.endsWith(",")) {
				
				storeAddrStr = storeAddrStr.substring(0,storeAddrStr.length()-1);
				
			}
			
		}
		
		storeAddrLblId.setValue(storeAddrStr); 
		storeAddrLblId.setVisible(true);
	
	}
	
	
public void onClick$changeAddressId() {
	try {
		

		if(postalAddressDivId.isVisible() ) {

			postalAddressDivId.setVisible(false);
			changeAddressDivId.setVisible(true);
		}else{
			
			changeAddressDivId.setVisible(false);
			postalAddressDivId.setVisible(true);
			
		}
		//setAddress();
		
		
		
	} catch (Exception e) {
		
		logger.error("Exception ::", e);
	}
}




public void onClick$doneAnchId() {
	try {
		setAddress(true);
	} catch (Exception e) {
		
		logger.error("Exception ::", e);
	}
}


public void onClick$cancelAddressId() {
	
	
	cAddressOneTbId.setValue((String)cAddressOneTbId.getAttribute("value"));
	cAddressTwoTbId.setValue((String)cAddressTwoTbId.getAttribute("value"));
	cCityTbId.setValue((String)cCityTbId.getAttribute("value"));
	cStateTbId.setValue((String)cStateTbId.getAttribute("value"));
	cStateTbId.setValue((String)cCountryTbId.getAttribute("value"));
	cPinLbId.setValue((String)cPinLbId.getAttribute("value"));
	cPhoneTbId.setValue((String)cPhoneTbId.getAttribute("value"));
	
	postalAddressDivId.setVisible(true);
	changeAddressDivId.setVisible(false);
	
	
	
}
public void onClick$updateBtnId() {
	CustomTemplates customTemplates = welcomeListLbId.getSelectedItem().getValue();
	logger.info("selected item is====>"+welcomeListLbId.getSelectedItem());

	boolean isNew = false;
	if(customTemplates == null) {
		
		customTemplates = new CustomTemplates();
		customTemplates.setUserId(currentUser);
		
		
		customTemplates.setType(getTemplateType());//call getTemplate()
		isNew =true;
		
	}
	
	submit(isNew, customTemplates);
	//refreshListBox(customTemplates, MESSAGE_UPDATE);
	//welcomeListLbId.setSelectedIndex(0);
}



	
	
	private Window createCustTemplateWinId;
	private Textbox createCustTemplateWinId$custTempNameTbId;
	private Label createCustTemplateWinId$msgLblId;
	
	
	public void onClick$newBtnId(Event event) {
		
		
		CustomTemplates customTemplates = new CustomTemplates();
		customTemplates.setUserId(currentUser);
		customTemplates.setType(getTemplateType());
	
	submit(true, customTemplates);
}
	
	
	private Tabbox editorTabboxId;
	public void onSelect$editorTabboxId() {
		
		Tab selTabId = editorTabboxId.getSelectedTab();
		Listitem tempSelItem = welcomeListLbId.getSelectedItem();
		Listitem selItem = typeOfAutoEmailListLbId.getSelectedItem();
		String htmlContent = "";
		
		CustomTemplates selTemplate = null;
		
		if(tempSelItem.getIndex() != 0){
			
			selTemplate = (CustomTemplates)tempSelItem.getValue();
			htmlContent = selTemplate.getHtmlText();
			
			if(selTemplate.getEditorType() != null && selTemplate.getEditorType().equals(Constants.EDITOR_TYPE_TEXT)) {
				htmlTbId.setValue(htmlContent != null ? htmlContent : "");
				ckEditorId.setValue("");
			
			}else {
				
				ckEditorId.setValue(htmlContent != null ? htmlContent : "");
				htmlTbId.setValue("");
			}
		}
		else{
			
			if(selTabId.getId().equals("textEditorTabId")) {
				
				htmlTbId.setValue("");
				
			}else if(selTabId.getId().equals("plainEditorTabId")) {
				
				ckEditorId.setValue(PropertyUtil.getPropertyValueFromDB((String)selItem.getValue()));
			}
			
		}
		
	}
	
	public String getEditorContent() {
		
		Tab selTabId = editorTabboxId.getSelectedTab();
		String textHTML = "";
		String editorType = Constants.EDITOR_TYPE_PLAIN;
		if(selTabId.getId().equals("textEditorTabId")) {
			
			textHTML = htmlTbId.getValue();
			
		}else if(selTabId.getId().equals("plainEditorTabId")) {
			
			textHTML = ckEditorId.getValue();
		}
		
		return textHTML;
		
	}
	
	
	/**
	 * Create or update the welcome template
	 * @param isUpdate - boolean
	 */
	private void submit(boolean isUpdate, CustomTemplates customTemplates) {
		try {
			
			Tab selTabId = editorTabboxId.getSelectedTab();
			String textHTML = "";
			String editorType = Constants.EDITOR_TYPE_PLAIN;
			if(selTabId.getId().equals("textEditorTabId")) {
				
				editorType = Constants.EDITOR_TYPE_TEXT;
				textHTML = htmlTbId.getValue();
				
			}else if(selTabId.getId().equals("plainEditorTabId")) {
				
				editorType = Constants.EDITOR_TYPE_PLAIN;
				textHTML = ckEditorId.getValue();
			}
			
			
			String isValidPhStr = null;
			isValidPhStr = Utility.validatePh(textHTML, currentUser);
			
			if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
				
				MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
				return ;
			}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
				
				MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either  enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
				return ;
				
			}
			
			String isValidCCDim = null;
			isValidCCDim = Utility.validateCouponDimensions(textHTML);
			if(isValidCCDim != null){
				return;
			}
					
			String isValidCouponAndBarcode = null;
			isValidCouponAndBarcode = Utility.validateCCPh(textHTML, currentUser);
			if(isValidCouponAndBarcode != null){
				return;
			}
			
			logger.debug("HTML text is :"+textHTML);
			String subscribeLink = PropertyUtil.getPropertyValue("subscribeLink");
			if(typeOfAutoEmailListLbId.getSelectedIndex() == 0 && textHTML.indexOf(subscribeLink)<0) {
				MessageUtil.setMessage("Custom HTML must have at least one [url].", "red", "top");
				return;
			}
			customTemplates.setHtmlText(textHTML);
			customTemplates.setEditorType(editorType);
			
			
			if(cSubTb.isValid() && !cSubTb.getValue().isEmpty()){
				customTemplates.setSubject(cSubTb.getValue());
			}
			else{
				MessageUtil.setMessage("Please provide subject. Subject should not be empty.", "color:red", "TOP");
				cSubTb.setFocus(true);
				//onClick$basicSettingsTbBtnId();
				return;
			}
			
			if(cFromNameTb.isValid() && Utility.validateFromName(cFromNameTb.getValue())) {
				customTemplates.setFromName(cFromNameTb.getValue());
			}
			else{
				MessageUtil.setMessage("Provide valid 'From Name'. Special characters are not allowed.", "color:red", "TOP");
				cFromNameTb.setFocus(true);
				//onClick$basicSettingsTbBtnId();
				return;
			}
			
			if(cFromEmailCb.getSelectedItem().getLabel().indexOf('@')<0) {
				MessageUtil.setMessage("Register 'From Email' to create Double Opt-in email.",
						"color:red", "TOP");
				return;
			}
			else if(!(cFromEmailCb.getSelectedIndex()==-1)) {
				customTemplates.setFromEmail(cFromEmailCb.getSelectedItem().getLabel());
			}
			else {
				MessageUtil.setMessage("Provide valid From Email Address.", "color:red", "TOP");
				cFromEmailCb.setFocus(true);
				//onClick$basicSettingsTbBtnId();
				return;
			}
			
			customTemplates.setAddressFlag(true);
			Address address = new Address();
			
			String includeBfrStr = "";
			
			if(orgNameChkBoxId.isChecked()) {
				
				customTemplates.setIncludeOrg(true);
				if(includeBfrStr.trim().length()>0) includeBfrStr+=Constants.ADDR_COL_DELIMETER;
				includeBfrStr += currentUser.getUserOrganization().getOrganizationName();
				
			}else{
				customTemplates.setIncludeOrg(false);
				
			}
			if(orgUnitChkBoxId.isChecked()) {
				
				customTemplates.setIncludeOrgUnit(true);
				if(includeBfrStr.trim().length()>0) includeBfrStr+=Constants.ADDR_COL_DELIMETER;
				List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(currentUser.getUserId());
				String userDomainStr = "";
				Set<UsersDomains> domainSet = new HashSet<UsersDomains>();//currentUser.getUserDomains();
				if(domainsList != null) {
					domainSet.addAll(domainsList);
					for (UsersDomains usersDomains : domainSet) {
						
						if(userDomainStr.length()>0) userDomainStr+=",";
						userDomainStr += usersDomains.getDomainName();
						
					}
				}
				
				
				
				includeBfrStr += userDomainStr;//currentUser.getUserDomainStr();
				
				
			}else {
				
				customTemplates.setIncludeOrgUnit(false);
			}
			customTemplates.setIncludeBeforeStr(includeBfrStr);
			
			
			if(addrRgId.getSelectedIndex()==0) {
				
				
				if(!cAddressOneTbId.getValue().trim().equals("")){
					address.setAddressOne(cAddressOneTbId.getValue());
				}else{
					MessageUtil.setMessage("Please provide Address Line 1.", "color:red", "TOP");
					cAddressOneTbId.setFocus(true);
					onClick$addrSettingsTbBtnId();
					return;
				}
				
				address.setAddressTwo(cAddressTwoTbId.getValue()!= null?
						cAddressTwoTbId.getValue().trim():"");
				
				if(!cCityTbId.getValue().trim().equals("")) {
					address.setCity(cCityTbId.getValue());
				}
				else{
					MessageUtil.setMessage("Please provide City.", "color:red", "TOP");
					cCityTbId.setFocus(true);
					onClick$addrSettingsTbBtnId();
					return;
				}
				if(!cStateTbId.getValue().trim().equals("")){
					address.setState(cStateTbId.getValue());
				}else{
					MessageUtil.setMessage("Please provide State.", "color:red", "TOP");
					cStateTbId.setFocus(true);
					onClick$addrSettingsTbBtnId();
					return;
				}
				if(!cCountryTbId.getValue().trim().equals("")) {
					address.setCountry(cCountryTbId.getValue());
				}else {
					MessageUtil.setMessage("Please provide Country.", "color:red", "TOP");
					onClick$addrSettingsTbBtnId();
					cCountryTbId.setFocus(true);
					return;
				}
				
				String pin = cPinLbId.getValue().trim();
				String countryType = currentUser.getCountryType();
				
				if(Utility.zipValidateMap.containsKey(countryType)){
				
				if(pin.length() == 0) MessageUtil.setMessage("Please provide Pin.", "color:red", "TOP");
				boolean validZip = Utility.validateZipCode(pin, countryType);
				if(validZip){
					address.setPin((pin));
				}else {
					 MessageUtil.setMessage("Please enter valid zip code.","color:red;");
					 cPinLbId.setFocus(true);
						return;
				}
				
				}else{	
					
					if(pin != null && pin.length() > 0){
						
						try{
							
							Long pinLong = Long.parseLong(pin);
							
			      } catch (NumberFormatException e) {
							MessageUtil.setMessage("Please provide 5 / 6 digits Zip Code.","color:red;");
							return ;
			      }
						
					if(pin.length() > 6 || pin.length() < 5) {
						
						//	Messagebox.show("Please provide 5 / 6 digits Zip code / pin.");
							MessageUtil.setMessage("Please provide 5 / 6 digits Zip code / Pin.", "Color:red", "Top");
							return;
							
						}
					}
				}
				
				/*if((cPinLbId.getValue().trim()).length() <= 6 && cPinLbId.getValue().trim().length() >= 5) {
					address.setPin((cPinLbId.getValue()));
				}else {
					if(cPinLbId.getValue().trim().length() == 0) MessageUtil.setMessage("Please provide Pin.", "color:red", "TOP");
					
					else if((cPinLbId.getValue().trim()).length() > 6 || (cPinLbId.getValue().trim()).length() < 5 ) MessageUtil.setMessage("Please provide 5 / 6 digits Zip code / Pin.", "color:red", "TOP");
					
					cPinLbId.setFocus(true);
					//onClick$addrSettingsTbBtnId();
					return;
				}*/
				if(cPhoneTbId.getValue()!= null  && !cPhoneTbId.getValue().isEmpty() ){
					try {
						String value = cPhoneTbId.getValue();
						
						
						String userPhoneRegex = "\\d+";
						Pattern phonePattern = Pattern.compile(userPhoneRegex);  
						Matcher m = phonePattern.matcher(value);//(value);
						String poneMatch = "";
						while (m.find()) {
							poneMatch += m.group();
						}
						try {
							value  = ""+Long.parseLong(poneMatch);
						} catch (NumberFormatException e) {
							MessageUtil.setMessage("Please provide valid Phone Number.", "color:red", "TOP");
							onClick$addrSettingsTbBtnId();
							return;
						}
						UserOrganization organization = currentUser!=null ? currentUser.getUserOrganization(): null;
						value =  Utility.phoneParse(value,organization);
						if(value == null || value.trim().length() == 0){

							MessageUtil.setMessage("Please provide valid Phone Number.", "Color:Red", "Top");
						//long phone = Long.parseLong(value);
							onClick$addrSettingsTbBtnId();
							return;
							
						}
						
						address.setPhone(value);
					} catch (Exception e) {
						MessageUtil.setMessage("Please provide valid Phone Number.", "color:red", "TOP");
						cPhoneTbId.setFocus(true);
						//onClick$addrSettingsTbBtnId();
						return;
					}
						
					
						
						
						
					/*	
						try{
						
						
						
						
						//Long phone = null;
						if(!cPhoneTbId.getValue().trim().equals("") 
								&& !Utility.validateUserPhoneNum(cPhoneTbId.getValue().trim())) {
						//phone = Long.parseLong(cPhoneTbId.getValue());
						
								
								MessageUtil.setMessage("Please provide valid Phone number.", "Color:Red", "Top");
								onClick$addrSettingsTbBtnId();
							//long phone = Long.parseLong(value);
								return;
							
						}
					}catch (Exception e) {

						MessageUtil.setMessage("Please provide valid Phone number.", "color:red", "TOP");
						cPhoneTbId.setFocus(true);
						//onClick$addrSettingsTbBtnId();
						return;
					
					}
					
					address.setPhone(""+cPhoneTbId.getValue().trim());*/
				}
				
				customTemplates.setAddrType(Constants.CAMP_ADDRESS_TYPE_USER);
				
				customTemplates.setAddress(address);
				
				
				
				
			}else if(addrRgId.getSelectedIndex() == 1){
				
				if(storesLbId.getItemCount() == 0) {
					
					MessageUtil.setMessage("Please choose another sender address option. No stores exist for the organization.", "color:red;");
					return;
					
				}
				
				OrganizationStores store = (OrganizationStores)storesLbId.getSelectedItem().getValue(); 
				address = store.getAddress();
				customTemplates.setAddrType(Constants.CAMP_ADDRESS_TYPE_STORE+"|"+store.getStoreId());
				customTemplates.setAddress(address);
				
			}
			
			
			customTemplates.setAddressStr(customTemplates.getAddressStr());
			
			if(cPermRemRb.getSelectedIndex() == 0){
				if(!permRemTextId.getValue().trim().equals("")){
					customTemplates.setPermissionRemainderFlag(true);
					customTemplates.setPermissionRemainderText(permRemTextId.getValue());
				}
			}else{
				customTemplates.setPermissionRemainderFlag(false);
			}
			
			if(weblinkVersionDivId.isVisible() ) {
				if(cWebPageCb.isChecked()){
					customTemplates.setWebLinkFlag(true);
					customTemplates.setWebLinkText(cWebLinkTextTb.getValue());
					if(!cWebLinkUrlTextTb.getValue().equals("")){
						customTemplates.setWebLinkUrlText(cWebLinkUrlTextTb.getValue());
					}else{
						MessageUtil.setMessage("Provide web-link URL text.", "color:red", "TOP");
						cWebLinkUrlTextTb.setFocus(true);
						onClick$optSettingsTbBtnId();
						return;
					}
				}else{
					customTemplates.setWebLinkFlag(false);
				}
			}else{
				
				customTemplates.setWebLinkFlag(false);
			}
			if(toNameChkId.isChecked()){
				customTemplates.setPersonalizeTo(true);
				if(phLbId.getSelectedIndex()>0){
					customTemplates.setToName((String)phLbId.getSelectedItem().getValue());
				}else{
					phLbId.setSelectedIndex(0);
					customTemplates.setToName((String)phLbId.getSelectedItem().getValue());
				}
			}else{
				customTemplates.setPersonalizeTo(false);
			}
				
			
			
			if(isUpdate) {
				
				
				
				createCustTemplateWinId.removeAttribute("newCustomTemplate");
				createCustTemplateWinId.setAttribute("newCustomTemplate", customTemplates);
				createCustTemplateWinId$custTempNameTbId.setValue("");
				createCustTemplateWinId$msgLblId.setValue("");
				
				createCustTemplateWinId.doModal();
				
				
			}
			else {
			
				//customTemplateDao.saveOrUpdate(customTemplates);
				customTemplateDaoForDML.saveOrUpdate(customTemplates);
			MessageUtil.clearMessage();
			
			
			
			
				MessageUtil.setMessage("Template saved successfully.","green", "top");
			
			}
		} catch (Exception e) {
			logger.error("** Exception :", e);
		}
	}

	private Label storeAddrLblId;
	private Radiogroup addrRgId;
	public void onCheck$addrRgId() {
		
		if(addrRgId.getSelectedIndex() == 1) {
			
			if(storesLbId.getItemCount() == 0) {
				
				
				
				MessageUtil.setMessage("Please choose another sender address option. No stores exist for the organization.", "color:red;");
				return;
				
				
				
			}
			
			OrganizationStores orgStore = (OrganizationStores)storesLbId.getSelectedItem().getValue();
			
			String storeAddrStr = "";
			if(orgStore.isAddressFlag()) {
				
				storeAddrStr = orgStore.getAddressStr().trim(); 
				if(storeAddrStr.contains(Constants.ADDR_COL_DELIMETER+Constants.ADDR_COL_DELIMETER)) {
					
					storeAddrStr = storeAddrStr.replace(Constants.ADDR_COL_DELIMETER+Constants.ADDR_COL_DELIMETER, ", ");
					
				}
				
				//logger.debug("addr===>"+storeAddrStr);
				storeAddrStr = storeAddrStr.replace(Constants.ADDR_COL_DELIMETER, ", ");
				storeAddrStr = storeAddrStr.trim();
				
				//logger.debug("addr ===>"+storeAddrStr);
				if(storeAddrStr.endsWith(",")) {
					
					storeAddrStr = storeAddrStr.substring(0,storeAddrStr.length()-1);
					
				}
				
			}
			
			storeAddrLblId.setValue(storeAddrStr); 
			storeAddrLblId.setVisible(true);
		}
		
		
	}
	private Window previewWin;
	private Iframe previewWin$html;
	public void onClick$custTempPreviewBtnId() {
		
	
	
		String htmlStr = getEditorContent();
		
		htmlStr = replaceDatePh(htmlStr);
		
		Utility.showPreview(previewWin$html, currentUser.getUserName(), htmlStr);
		/*previewWin$html.setSrc(null);
		previewWin$html.invalidate();
		*//**
		 * creates a html file and saves in user/MyTemplate directory
		 *//*
		String previewFilePathStr = 
			PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
			File.separator+"Preview"+File.separator+"preview.html";
		try {
			File myTemplateFile = new File(previewFilePathStr);
			File parentDir = myTemplateFile.getParentFile();
			 if(!parentDir.exists()) {
					
					parentDir.mkdir();
				}

			
			if(myTemplateFile.exists()) {
				
				myTemplateFile.delete();
			}
			
			
			myTemplateFile = new File(previewFilePathStr);
			myTemplateFile.createNewFile();
			
			//TODO Have to copy image files if exists
			BufferedWriter bw = new BufferedWriter(new FileWriter(previewFilePathStr));
			bw.write(htmlStr);
			bw.flush();
			bw.close();
			
			//myTemplateFile.renameTo(parentDir);
			
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ::", e);
		}
		previewWin$html.invalidate();
		previewWin$html.setSrc(PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+
				"/"+currentUser.getUserName()+"/"+"Preview"+"/"+"preview.html");
		*/
		//Html html = (Html)previewWin$html;
		//html.setContent(htmlStr);
		previewWin.setVisible(true);
	
	}

	 /**
	 *  onClick event for delete Button 
	 *  deletes the selected message in List box.
	 */
	public void onClick$delBtnId() {
		
		try {
			if (Messagebox.show("Are you sure you want to delete " +
					"the selected template?", "Delete?", Messagebox.YES | 
					Messagebox.NO, Messagebox.QUESTION)  ==  Messagebox.YES) {
				
				if(welcomeListLbId.getSelectedIndex() < 1) {
					MessageUtil.setMessage("Select a template to delete.", "red", "top");
					return;
					
				/*if(typeOfAutoEmailListLbId.getSelectedIndex() == 0){
						MessageUtil.setMessage("Select a welcome message to delete.", "red", "top");
						return;
					}
					else if(typeOfAutoEmailListLbId.getSelectedIndex() == 1 ){
					
						MessageUtil.setMessage("Select a Parental Consent message to delete.", "red", "top");
						return;
					}
					 else if(typeOfAutoEmailListLbId.getSelectedIndex() == 2){
						
							MessageUtil.setMessage("Select a loyalty opt-in message to delete.", "red", "top");
							return;
						}*/
					}
					MessageUtil.clearMessage();
					CustomTemplates customTemplates = (CustomTemplates)welcomeListLbId.getSelectedItem().getValue();
					
					
					
					String type = getTemplateType();
					int count = mailingListDao.findTemplateAssociateCount(customTemplates.getTemplateId(), type);
					if(count > 0) {
						MessageUtil.setMessage("Selected template is associated with one or more mailing list and cannot be deleted.", "color:red;");
						return;
					}
					
					
					//customTemplateDao.delete(customTemplates);
					customTemplateDaoForDML.delete(customTemplates);
					welcomeListLbId.getSelectedItem().setVisible(false);
					welcomeListLbId.setSelectedIndex(0);
					
					customtemplateSettings(true);
					//onSelect$welcomeListLbId();
					//submitBtnId.setVisible(true);
					//updateBtnId.setVisible(false);
					delBtnId.setVisible(false);
					//sendTestMsgBtnId.setVisible(false);
					//ckEditorId.setValue("");
					logger.info("deleted selected welcome message successfully.");
					MessageUtil.setMessage("' "+ customTemplates.getTemplateName()+"'  deleted successfully.", "blue", "top");
			}	
		} catch(Exception e ) {
			logger.error("Exception while deleting welcome message : ",e);
		}		
		
	}
 
	/*
	 * this method allows to go to previous page
	 */
	public void onClick$backBtnId(){
		String fromPage = (String)Sessions.getCurrent().getAttribute("fromPage");
		logger.debug("moving to the page----->"+fromPage+".zul");
		Redirect.goToPreviousPage();
		
		
		
	}
	
	//************* Code to replace Date ph of auto emails*****************
		private String replaceDatePh(String content){
			
			Set<String> symbolSet = getSymbolAndDateFields(content);
			if(symbolSet != null && symbolSet.size()>0){
				for (String symbol : symbolSet) {
						 if(symbol.startsWith(Constants.DATE_PH_DATE_)) {
							if(symbol.equalsIgnoreCase(Constants.DATE_PH_DATE_today)){
								Calendar cal = MyCalendar.getNewCalendar();
								content = content.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
							}
							else if(symbol.equalsIgnoreCase(Constants.DATE_PH_DATE_tomorrow)){
								Calendar cal = MyCalendar.getNewCalendar();
								cal.set(Calendar.DATE, cal.get(Calendar.DATE)+1);
								content = content.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
							}
							else if(symbol.endsWith(Constants.DATE_PH_DAYS)){
								
								try {
									String[] days = symbol.split("_");
									Calendar cal = MyCalendar.getNewCalendar();
									cal.set(Calendar.DATE, cal.get(Calendar.DATE)+Integer.parseInt(days[1].trim()));
									content = content.replace("["+symbol+"]", MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
								} catch (Exception e) {
									logger.info("exception in parsing date placeholder");
								}
							}
						}//if
				}//for
			}//if
			
			return content;
			
			
		}
		
		//************* END**********************************************************************
		
		
		private Set<String> getSymbolAndDateFields(String content) {
		
			content = content.replace("|^", "[").replace("^|", "]");
			String cfpattern = "\\[([^\\[]*?)\\]";
			Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
			Matcher m = r.matcher(content);

			String ph = null;
			Set<String> subjectSymbolSet = new HashSet<String>();

			try {
				while(m.find()) {

					ph = m.group(1); //.toUpperCase()
					logger.info("Ph holder :" + ph);

					if(ph.startsWith(Constants.SYMBOL_PH_SYM)) {
						subjectSymbolSet.add(ph);
					}
					else if(ph.startsWith(Constants.DATE_PH_DATE_)){
						subjectSymbolSet.add(ph);
					}
					
				} // while
				
			} catch (Exception e) {
				logger.error("Exception while getting the symbol place holders ", e);
			}

			logger.info("symbol PH  Set : "+ subjectSymbolSet);

			return subjectSymbolSet;
		}

	
	
	/**
	 * Renderer class for the welcome message templates listbox
	 */
    class ListRenderer implements ListitemRenderer{
    	/**
    	 * executes when setModel is called on the listbox
    	 */
		public void render(Listitem li, java.lang.Object data,int arg2) {
			if(data instanceof CustomTemplates) {
				CustomTemplates ct = (CustomTemplates) data;
				Listcell lc = new Listcell(ct.getTemplateName());
				lc.setParent(li);
				li.setValue(ct);
			}
			else if(data instanceof String) {
				li.setLabel((String)data);
			}
		}
	}
	
	
   /* public class phListRenderer implements ListitemRenderer {

		@Override
		public void render(Listitem item, Object data,int arg2) throws Exception {
			if(data instanceof String) {
				String phStr = (String)data;
				if(phStr.indexOf(Constants.DELIMETER_DOUBLECOLON) < 0) return;
				String[] tokens = phStr.split(Constants.DELIMETER_DOUBLECOLON);
				item.setLabel(tokens[0]);
				item.setValue(tokens[1]);
			}
		}
	}
	*/
	
	
			 
	}
		
		
		
	
	

