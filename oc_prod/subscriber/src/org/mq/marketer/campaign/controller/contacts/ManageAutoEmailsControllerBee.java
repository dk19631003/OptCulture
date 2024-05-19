package org.mq.marketer.campaign.controller.contacts;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.mq.marketer.campaign.beans.Address;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.UserFromEmailId;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.gallery.MyTempEditorController;
import org.mq.marketer.campaign.controller.gallery.MytempBeeEditorController;
import org.mq.marketer.campaign.controller.layout.EditorController;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.CustomTemplatesDaoForDML;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.UserFromEmailIdDao;
import org.mq.marketer.campaign.dao.UserFromEmailIdDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PlaceHolders;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zhtml.Td;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;

@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
public class ManageAutoEmailsControllerBee extends MyTempEditorController {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private Session sessionScope = null;
	private String jsonStuff;
	private Users currentUser;
	private UsersDao usersDao;
	private UserFromEmailIdDao userFromEmailIdDao;
	private Set<Long> listIdsSet;
	private MailingListDao mailingListDao;
	private UsersDaoForDML usersDaoForDML;
	private OrganizationStoresDao organizationStoresDao;
	private CustomTemplatesDaoForDML customTemplateDaoForDML;
	private MyTemplatesDao myTemplatesDao;
	private boolean isEdit = false;
	private boolean sendTestMailListRequest;
	private CustomTemplatesDao customTemplateDao;
	private boolean reset = false;
	private String usersParentDirectory = null;
	private UserFromEmailIdDaoForDML userFromEmailIdDaoForDML;
	
	private Listbox typeOfAutoEmailListLbId;
	private Combobox cFromEmailCb;
	private Combobox creplyToEmailCb;
	private Textbox cSubTb;
	private Textbox cFromNameTb;
	private Textbox cAddressOneTbId;
	private Textbox cAddressTwoTbId;
	private Textbox cCityTbId;
	private Textbox cStateTbId;
	private Textbox cCountryTbId;
	private Textbox permRemTextId;
	private Radio postalAddressRbId,homeStoreAddressDetailsId,lastPurchaseStoreId,storeAddressId;
	private Textbox cPinLbId;
	private Textbox cPhoneTbId;
	private Div changeAddressDivId, postalAddressDivId;
	private Checkbox customFooterId;
	private Checkbox orgUnitChkBoxId, orgNameChkBoxId,downloadPdf;
	private Label storeAddrLblId;
	private Radiogroup addrRgId;
	private Radiogroup cPermRemRb;
	private Listbox chooseTemplateFolderId;
	private Listbox chooseTemplateId;
	private Paging autoEmailBEEListPaging;
	private Listbox autoEmailPageSizeLbId;
	private Grid autoEmailGridId;
	private Window testWinId;
	private Textbox testWinId$testTbId;
	private Label testWinId$msgLblId;
	private Button testWinId$sendTestMailBtnId;
	private Tab createAutoEmailTabPanelId,autoEmailListId;
	private Listbox srchLbId;
	private Listbox storesLbId;
	private Textbox updateIdValue;
	private Listbox selectSearchItemId;
	private Div createdDateId;
	private boolean dispalyCreateNew;
	private Textbox custTempNameTbId;
	private Td tableDataWidth;
	private Td previewTdId;
	private Td editTemplateTdId;
	private Td sendMailTdId;
	private Div gbContentDivId;
	private Toolbarbutton optSettingsTbBtnId;
	private Checkbox toNameChkId;
	private Listbox phLbId; 
	private Div persToDivId;
	private boolean previewTemplateListScreen;
	private Popup regEmailPopupId;
	private Textbox cFromEmailTb;
	private MyDatebox fromDateboxId;
	private MyDatebox toDateboxId;
	private A modifySenderAddrId;
	private Map<String, String> searchMap = null;
	TimeZone timeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
	private final String  USER_TEMPLATES_DIRECTORY= "userTemplatesDirectory"; 
	private final String  USER_NEW_EDITOR_TEMPLATES_DIRECTORY= "userNewEditorTemplatesDirectory";
	

	public ManageAutoEmailsControllerBee() {
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Auto-Emails","",style,true);
		usersDao = (UsersDao) SpringUtil.getBean("usersDao");
		session = Sessions.getCurrent();
		currentUser = GetUser.getUserObj();
		String userDomainStr = "";
		List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(currentUser.getUserId());
		Set<UsersDomains> domainSet = new HashSet<UsersDomains>();
		if (domainsList != null) {
			domainSet.addAll(domainsList);
			for (UsersDomains usersDomains : domainSet) {

				if (userDomainStr.length() > 0)
					userDomainStr += ",";
				userDomainStr += usersDomains.getDomainName();
			}
		}
		currentUser.setUserDomainStr(userDomainStr);
		usersDaoForDML = (UsersDaoForDML) SpringUtil.getBean("usersDaoForDML");
		organizationStoresDao = (OrganizationStoresDao) SpringUtil.getBean("organizationStoresDao");
		userFromEmailIdDao = (UserFromEmailIdDao) SpringUtil.getBean("userFromEmailIdDao");
		mailingListDao = (MailingListDao) SpringUtil.getBean("mailingListDao");
		listIdsSet = (Set<Long>) session.getAttribute(Constants.LISTIDS_SET);
		myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
		customTemplateDaoForDML = (CustomTemplatesDaoForDML)SpringUtil.getBean("customTemplatesDaoForDML");
		customTemplateDao = (CustomTemplatesDao)SpringUtil.getBean("customTemplatesDao");
		sessionScope = Sessions.getCurrent();
		usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");
		this.userFromEmailIdDaoForDML = (UserFromEmailIdDaoForDML)SpringUtil.getBean("userFromEmailIdDaoForDML");
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		logger.info("Bee Editor");
		//.getMilestones();
		

		String newEditorfolderPathStr = usersParentDirectory+
				File.separator+ GetUser.getUserObj().getUserName()+PropertyUtil.getPropertyValue("userNewEditorTemplatesDirectory")+File.separator;
		 File newEditorfolder = new File(newEditorfolderPathStr);
		 logger.info("file size is"+newEditorfolder.listFiles().length);
		 logger.info("is directory "+newEditorfolder.isDirectory());
		 
		 
		 if(newEditorfolder.isDirectory() && newEditorfolder.listFiles().length == 0){
			 String dirName= Constants.NEWEDITOR_TEMPLATES_FOLDERS_DEFAULT;
			 File newDir= new File(newEditorfolderPathStr+dirName);
			 newDir.mkdir();
			 logger.info("entered in to default folders"+newDir.getName());
				String dirNameAuto=Constants.NEWEDITOR_TEMPLATES_FOLDERS_DRAFTS;
				 File autoDir=new File(newEditorfolderPathStr+dirNameAuto);
				 autoDir.mkdir();
			 
				 logger.info("entered in to draft folders"+autoDir.getName());
		 }
		
		 if(newEditorfolder.isDirectory() && newEditorfolder.listFiles().length >= 0){
			File[] foldstr=newEditorfolder.listFiles();
			for (File file : foldstr) {
				if(!file.getName().equals(Constants.MYTEMPATES_FOLDERS_DRAFTS)){
					
					String dirNameAuto=Constants.MYTEMPATES_FOLDERS_DRAFTS;
					 File autoDir=new File(newEditorfolderPathStr+dirNameAuto);
					 autoDir.mkdir();
				}
					
				
			}
			
		 }
		 
		 String folderPathStr = usersParentDirectory+
					File.separator+ GetUser.getUserObj().getUserName()+PropertyUtil.getPropertyValue(USER_TEMPLATES_DIRECTORY)+File.separator;
		 File folderFile = new File(folderPathStr);
		 logger.info("file size is"+folderFile.listFiles().length);
		 logger.info("is directory "+folderFile.isDirectory());
		 
		 if(folderFile.isDirectory() && folderFile.listFiles().length == 0){
			 String dirName= Constants.MYTEMPATES_FOLDERS_DEFAULT;
			 File newDir= new File(folderPathStr+dirName);
			 newDir.mkdir();
			 logger.info("entered in to default folders"+newDir.getName());
				String dirNameAuto=Constants.MYTEMPATES_FOLDERS_DRAFTS;
				 File autoDir=new File(folderPathStr+dirNameAuto);
				 autoDir.mkdir();
			 
				 logger.info("entered in to draft folders"+autoDir.getName());
		 }
		
		 if(folderFile.isDirectory() && folderFile.listFiles().length >= 0){
			File[] foldstr=folderFile.listFiles();
			for (File file : foldstr) {
				if(!file.getName().equals(Constants.MYTEMPATES_FOLDERS_DRAFTS)){
					
					String dirNameAuto=Constants.MYTEMPATES_FOLDERS_DRAFTS;
					 File autoDir=new File(folderPathStr+dirNameAuto);
					 autoDir.mkdir();
				}
					
				
			}
			
		 }
		
		//MyTempEditorController.getMilestones();
		updateIdValue.setVisible(false);
		chooseTemplateId.setWidth("100%");
		getAutoEmailList();
		onClick$resetAnchId();
		onSelect$storesLbId();
		updateIdValue.setValue(Constants.STRING_NILL);
		if(Sessions.getCurrent().getAttribute("dispalyCreateNew")!= null) {
			dispalyCreateNew = (boolean)Sessions.getCurrent().getAttribute("dispalyCreateNew");
			createAutoEmailTabPanelId.setSelected(dispalyCreateNew);
			if(Sessions.getCurrent().getAttribute("beeBackButtonAutoPoplateInCreateTemplate")!= null){
				Long autoEmailTemplateId = (Long)Sessions.getCurrent().getAttribute("beeBackButtonAutoPoplateInCreateTemplate");
				CustomTemplates custTemp = customTemplateDao.findCustTemplateById(autoEmailTemplateId);
				if(custTemp!=null) {
					currentUser = custTemp.getUserId();
					defaultFolderList();
					editAutoEmailData(autoEmailTemplateId);
					Sessions.getCurrent().removeAttribute("beeBackButtonAutoPoplateInCreateTemplate");
				}
			}else {
				onClick$autoEmailListId();
				hideLinkFields();
			}
		}
		Sessions.getCurrent().removeAttribute("dispalyCreateNew");
		
		if(Sessions.getCurrent().getAttribute("editCustomTemplate")!= null){
			String mode = (String)Sessions.getCurrent().getAttribute("Mode");
			Object object = (Object) Sessions.getCurrent().getAttribute("editCustomTemplate");
			String custom_template_type_webformemail = (String)Sessions.getCurrent().getAttribute("typeOfEmail");
			if(object!= null && object!="DefaultTemplate" && custom_template_type_webformemail!= null) {
				if(mode.equalsIgnoreCase("edit")) {
					CustomTemplates customTemplates = (CustomTemplates)object;
					currentUser = customTemplates.getUserId();
					defaultFolderList();
					editAutoEmailData(customTemplates.getTemplateId());
					Sessions.getCurrent().removeAttribute("editCustomTemplate");
					isEdit = true;
					logger.info("the message is regarding edit : "+ isEdit);
				}else if(mode.equalsIgnoreCase("add")) {
					createAutoEmailTabPanelId.setSelected(true);
					 onClick$autoEmailListId();
				}
			}else if(object!= null && object=="DefaultTemplate" && custom_template_type_webformemail!= null){
				Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES_AUTOEMAIL);
			}
		}else if(Sessions.getCurrent().getAttribute("editCustomTemplate")== null && Sessions.getCurrent().getAttribute("fromAddNewBtn")!= null
				&& Sessions.getCurrent().getAttribute("fromAddNewBtn").equals("loyalty/loyaltyAutoCommunication")){
				createAutoEmailTabPanelId.setSelected(true);
				onClick$autoEmailListId();
		}
	}
	
	
	private void hideLinkFields() {
		previewTdId.setVisible(false);
		editTemplateTdId.setVisible(false);
		sendMailTdId.setVisible(false);
	}

	private void getAutoEmailList() {
		/** pagination **/
		autoEmailBEEListPaging.setTotalSize(0);
		autoEmailBEEListPaging.setActivePage(0);
		autoEmailBEEListPaging.addEventListener("onPaging", this);
		autoEmailGridId.setRowRenderer(rowRender);
		autoEmailGridId.setModel(getCustomTemplateModel(0, autoEmailBEEListPaging.getPageSize(), null,null,null));
		/** pagination **/

		String typeOfAutoEmailsStr = PropertyUtil.getPropertyValueFromDB("AutoEmail");
		logger.info("autoTypeStr" + typeOfAutoEmailsStr);
		if (typeOfAutoEmailsStr == null) {
			return;
		}
		if(typeOfAutoEmailListLbId.getSelectedIndex()==17 || typeOfAutoEmailListLbId.getSelectedIndex()==18 ) {
			logger.info("entering otp loop");
			Set<MailingList> set = new HashSet<MailingList>();
			getPlaceHolderListAutoEmailOTP(set, false, null);
		}
		
		defaultFolderList();
		/*List<String> myFolders = myTemplatesDao.findDistinctMyTemplatesFolders(currentUser.getUserId());
			for (String folderList : myFolders) {
				chooseTemplateFolderId.appendItem(folderList, folderList);
			}*/
			
		String[] autoTypeStr = typeOfAutoEmailsStr.split(Constants.ADDR_COL_DELIMETER);
		Listitem li = null;
		String value = null;

		selectList(autoTypeStr, value, li);

		if (typeOfAutoEmailListLbId.getItemCount() > 0) {
			typeOfAutoEmailListLbId.setSelectedIndex(0);
		}

		if (srchLbId.getItemCount() > 0) {
			srchLbId.setSelectedIndex(0);
		}
		setStores();

		CustomTemplates customTemplates = null;

		List<UserFromEmailId> userFromEmailIdList = userFromEmailIdDao.getEmailsByUserId(currentUser.getUserId());
		logger.debug("List size : " + userFromEmailIdList.size());

		cFromEmailCb.getItems().clear();
		creplyToEmailCb.getItems().clear();
		if(currentUser!=null && currentUser.getEmailId()!=null) {
			cFromEmailCb.appendItem(currentUser.getEmailId());
			creplyToEmailCb.appendItem(currentUser.getEmailId());
		}
		if (userFromEmailIdList.size() > 0) {
			for (Object obj : userFromEmailIdList) {
				logger.debug("obj : " + obj);
				UserFromEmailId userFromEmailId = (UserFromEmailId) obj;
				logger.debug("userFromEmailId : " + userFromEmailId);
				cFromEmailCb.appendItem(userFromEmailId.getEmailId());
				creplyToEmailCb.appendItem(userFromEmailId.getEmailId());
			}
		}

		if (!(cFromEmailCb.getItemCount() > 0)) {
			cFromEmailCb.appendItem("No emails registered.");
			creplyToEmailCb.appendItem("No emails registered.");
		}
		cFromEmailCb.setSelectedIndex(0);
		creplyToEmailCb.setSelectedIndex(0);

		loadSettings();
		setAddress(false);
		setUserAddress(customTemplates);
	}

	public void defaultFolderList() {
		chooseTemplateFolderId.getItems().clear();
		chooseTemplateFolderId.appendItem("Select Folder", "").setSelected(true);
		File myTemp = null;
		File[] templateList = null;
		String myTempPathStr = usersParentDirectory+File.separator+ currentUser.getUserName()+PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATES_DIRECTORY)+File.separator;
		myTemp= new File(myTempPathStr);
		templateList = myTemp.listFiles();
		logger.debug("myTemp.listFiles().length"+myTemp.listFiles().length);
		List<File> fileList= new ArrayList<File>();
		for(File file:templateList) {
			if(!file.getName().equalsIgnoreCase(Constants.NEWEDITOR_TEMPLATES_FOLDERS_DRAFTS)) {
				if(file.getName().equalsIgnoreCase(Constants.NEWEDITOR_TEMPLATES_FOLDERS_DEFAULT)){	
					logger.debug("------in if case----"+file.getName());
					String folderName=file.getName();						
					Listitem item = new Listitem();
					item.setLabel(folderName);
					item.setValue(folderName);
					item.setAttribute("dirPath", PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATES_DIRECTORY));
					item.setAttribute("parentDir", Constants.NEWEDITOR_TEMPLATES_PARENT);
					item.setParent(chooseTemplateFolderId);
				}else {
					String folderName=file.getName();
					Listitem item = new Listitem();
					item.setLabel(folderName);
					item.setValue(folderName);
					item.setParent(chooseTemplateFolderId);
					fileList.add(file);
				}
			}
		}
	}
	
	
	private void setUserAddress(CustomTemplates customtemplate) {
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
				}
		}
	}

/*	private void selectList(String[] autoTypeStr, String value, Listitem li) {
			typeOfAutoEmailListLbId.getItems().clear();
			srchLbId.getItems().clear();
			li = new Listitem("Select Category","");
			li.setParent(srchLbId);
			li = new Listitem("Select Category","");
			li.setParent(typeOfAutoEmailListLbId);
		for (String eachType : autoTypeStr) {
			boolean isSbToOc=true;
			value = null;
			if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_DOUBLEOPTIN)) {
				value = DOUBLEOPTIN;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_PARENTALCONSENT)) {
				value = PARENTALCONSENT;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_LOYALTYOPTIN)) {
				value = LOYALTYOPTIN;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_WEBFORMEMAIL)) {
				value = WEBFORMEMAIL;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_TIER_UPGRADATION)) {
				value = TIERUPGRADATION;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION)) {
				value = EARNEDREWARDEXPIRATION;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_MEMBERSHIP_EXPIRATION)) {
				value = MEMBERSHIPEXPIRATION;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_EARNED_BONUS)) {
				value = EARNEDBONUS;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE)) {
				value = GIFTCARDISSUANCE;
				isSbToOc=false;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION)) {
				value = GIFTAMOUNTEXPIRATION;
				isSbToOc=false;
			}else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION)) {
				value = GIFTCARDEXPIRATION;
				isSbToOc=false;
			}else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_FEEDBACK_FORM)) {
				value = FEEDBACKFORM;
			}else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_SPECIAL_REWARDS)) {
				value = SPECIALREWARDS;
			}
			if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())){
				if(isSbToOc){ 
					li = new Listitem(eachType, value);
				}else continue;
			}
			else {
				li = new Listitem(eachType, value);
			}
		//	li = new Listitem(eachType, value);
			li.setParent(typeOfAutoEmailListLbId);
			li = new Listitem(eachType, value);
			li.setParent(srchLbId);
		}
			
		if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())){
			for (Listitem item : typeOfAutoEmailListLbId.getItems()) {
					if (item.getLabel().equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE) || 
							item.getLabel().equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION) || 
							item.getLabel().equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION)) {
						item.setVisible(false);
					}
					
				}
			}
	}*/
	private void selectList(String[] autoTypeStr, String value, Listitem li) {
		typeOfAutoEmailListLbId.getItems().clear();
		srchLbId.getItems().clear();
		li = new Listitem("Select Category","");
		li.setParent(srchLbId);
		li = new Listitem("Select Category","");
		li.setParent(typeOfAutoEmailListLbId);
	for (String eachType : autoTypeStr) {
		value = null;
		if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_DOUBLEOPTIN)) {
			value = DOUBLEOPTIN;
		} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_PARENTALCONSENT)) {
			value = PARENTALCONSENT;
		} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_LOYALTYOPTIN)) {
			value = LOYALTYOPTIN;
		} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_WEBFORMEMAIL)) {
			value = WEBFORMEMAIL;
		} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_TIER_UPGRADATION)) {
			value = TIERUPGRADATION;
		} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION)) {
			value = EARNEDREWARDEXPIRATION;
		} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_MEMBERSHIP_EXPIRATION)) {
			value = MEMBERSHIPEXPIRATION;
		} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_EARNED_BONUS)) {
			value = EARNEDBONUS;
		} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION)) {
			value = GIFTAMOUNTEXPIRATION;
		} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION)) {
			value = GIFTCARDEXPIRATION;
		}else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_FEEDBACK_FORM)) {
			value = FEEDBACKFORM;
		}else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_SPECIAL_REWARDS)) {
			value = SPECIALREWARDS;
		}else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_ADJUSTMENT)) {
			value = LOYALTYADJUSTMENT;
		}else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_ISSUANCE)) {
			value = LOYALTYISSUANCE;
		}else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_REDEMPTION)) {
			value = LOYALTYREDEMPTION;
		}else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_OTP_MESSAGE)) {
			value = OTPMESSAGES;
		}else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_OTP_MESSAGE)) {
			value = OTPMESSAGES;
		}else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_REDEMPTION_OTP)) {
			value = REDEMPTIONOTP;
		}
		
		
		li = new Listitem(eachType, value);
		li.setParent(typeOfAutoEmailListLbId);
		
		li = new Listitem(eachType, value);
		li.setParent(srchLbId);
		
		/*if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())){
			if(li.getLabel().equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE) || 
					li.getLabel().equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION) || 
					li.getLabel().equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION)){ 
				li.setVisible(false);
			}else continue;
		}*/
	}
	if(typeOfAutoEmailListLbId.getSelectedIndex()==17) {
		logger.info("entering otp loop");
		Set<MailingList> set = new HashSet<MailingList>();
		getPlaceHolderListAutoEmailOTP(set, false, null);
	}
	
	
	if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())){
		for (Listitem item : typeOfAutoEmailListLbId.getItems()) {
				if (item.getLabel().equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE) || 
						item.getLabel().equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION) || 
						item.getLabel().equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION)) {
					item.setVisible(false);
					logger.info("Items ----- "+item.getLabel());
				}
				
			}
		for (Listitem item : srchLbId.getItems()) {
			if (item.getLabel().equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE) || 
					item.getLabel().equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION) || 
					item.getLabel().equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION)) {
				item.setVisible(false);
				logger.info("Items ----- "+item.getLabel());
			}
			
		}
		}
		
		
		
		/*if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())){
			if(li.getLabel().equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE) || 
					li.getLabel().equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION) || 
					li.getLabel().equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION)){ 
				li.setVisible(false);
			}else continue;
		}*/
	
	/*if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())){
		for (Listitem item : typeOfAutoEmailListLbId.getItems()) {
				if (item.getLabel().equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE) || 
						item.getLabel().equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION) || 
						item.getLabel().equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION)) {
					item.setVisible(false);
					logger.info("Items ----- "+item.getLabel());
				}
				
			}
		}*/
	
}

	public void onSelect$chooseTemplateFolderId(Event e) {
		  chooseTemplateId.getItems().clear();
		  chooseTemplateId.appendItem("Select Template", "").setSelected(true);
		  if(chooseTemplateFolderId.getSelectedIndex() == 0 || chooseTemplateId.getSelectedIndex() == 0) {
			  hideLinkFields();
			  tableDataWidth.setStyle("width:36%;");
		  }
		  if(chooseTemplateFolderId.getSelectedItem()!= null && chooseTemplateFolderId.getSelectedItem().getValue() != null && !chooseTemplateFolderId.getSelectedItem().getValue().equals("Legacy Editor")){
			  List<MyTemplates> myTemplateName =  (List<MyTemplates>)myTemplatesDao.findTemplatesFromUserIdAndFolderName(currentUser.getUserId(),chooseTemplateFolderId.getSelectedItem().getValue());
			  if(myTemplateName!= null && !myTemplateName.isEmpty()) {
				  for(MyTemplates templateName : myTemplateName) {
					  if(templateName.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE)) {
						  chooseTemplateId.appendItem(templateName.getName(), templateName.getName()).setAttribute("data-id", templateName.getMyTemplateId());
					  }else if (templateName.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_PLAIN)) {
						  chooseTemplateId.appendItem(templateName.getName(), templateName.getName()).setAttribute("data-id-ck", templateName.getMyTemplateId());
					  }
				  }
				  
			  }
		  }else if(chooseTemplateFolderId.getSelectedItem()!= null && chooseTemplateFolderId.getSelectedItem().getValue() != null && chooseTemplateFolderId.getSelectedItem().getValue().equals("Legacy Editor")) {
			  List<CustomTemplates> autoEmailCustomTemplate =  (List<CustomTemplates>)customTemplateDao.findTemplatesFromUserId(currentUser.getUserId());
			  if(autoEmailCustomTemplate!=null && !autoEmailCustomTemplate.isEmpty()) {
				  for(CustomTemplates templateName : autoEmailCustomTemplate) {
					  chooseTemplateId.appendItem(templateName.getTemplateName(), templateName.getTemplateName()).setAttribute("data-id-ck", templateName.getTemplateId());
				  }
			  }
		  }
	}
	
	public void onSelect$chooseTemplateId() {
		if(chooseTemplateId.getSelectedIndex() == 0 && !chooseTemplateId.isDisabled()) {
			hideLinkFields();
			tableDataWidth.setStyle("width:36%;");
		}else {
		previewTdId.setVisible(true);
		editTemplateTdId.setVisible(true);
		sendMailTdId.setVisible(true);
		tableDataWidth.setStyle("width:23%;");
		}
	}


	public void setStores() {
		Listitem item = null;
		Components.removeAllChildren(storesLbId);
		List<OrganizationStores> storeList = organizationStoresDao
				.findByOrganization(currentUser.getUserOrganization().getUserOrgId());
		for (OrganizationStores organizationStores : storeList) {
			item = new Listitem(organizationStores.getStoreName(), organizationStores);
			item.setParent(storesLbId);
		}
		if (storesLbId.getItemCount() > 0) {
			storesLbId.setSelectedIndex(0);
			onSelect$storesLbId();
		}
	}

	public void loadSettings() {
			try {
				//defaultSettings();
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
					if (currentUser.getPinCode() != null) {
						if (currentUser.getPinCode().length() != 0)
							cPinLbId.setValue((currentUser.getPinCode().trim()));
						cPinLbId.setAttribute("value", (currentUser.getPinCode().trim()));

					}
				} catch (Exception e) {
					logger.error("** Exception: Problem occured while setting PinCode value : " + e + " **");
				}
				try {
					if (currentUser.getPhone() != null) {
						if (currentUser.getPhone().trim().length() != 0)
							cPhoneTbId.setValue(currentUser.getPhone());
						cPhoneTbId.setAttribute("value", currentUser.getPhone());
					}
				} catch (Exception e) {
					logger.error(
							"** Exception: Problem occured while setting updating user information . " + e + " **");
				}

				setUserAddr();
			} catch (Exception e) {
				logger.error("** Exception: Problem occured while setting updating user information . " + e + " **");
			}
	}

	public void onSelect$storesLbId() {
		if(storesLbId!=null && storesLbId.getSelectedItem()!=null) {
			OrganizationStores orgStore = (OrganizationStores) storesLbId.getSelectedItem().getValue();
			String storeAddrStr = "";
			if (orgStore.isAddressFlag()) {
				storeAddrStr = orgStore.getAddressStr().trim();
				if (storeAddrStr.contains(Constants.ADDR_COL_DELIMETER + Constants.ADDR_COL_DELIMETER)) {
					storeAddrStr = storeAddrStr.replace(Constants.ADDR_COL_DELIMETER + Constants.ADDR_COL_DELIMETER, ", ");
				}
				storeAddrStr = storeAddrStr.replace(Constants.ADDR_COL_DELIMETER, ", ");
				storeAddrStr = storeAddrStr.trim();
				if (storeAddrStr.endsWith(",")) {
					storeAddrStr = storeAddrStr.substring(0, storeAddrStr.length() - 1);
				}
			}
			storeAddrLblId.setValue(storeAddrStr);
			storeAddrLblId.setVisible(true);
		}
	}

	public void setAddress(boolean isModifyUser) {
		try {

			String postalAddrStr = "";
			if (isModifyUser && postalAddressDivId.isVisible()) {
				postalAddressDivId.setVisible(false);
				changeAddressDivId.setVisible(true);
			} else {

				String value = null;

				value = cAddressOneTbId.getValue();
				if (value == null || value.equals("")) {

					MessageUtil.setMessage("Please provide Address Line 1.", "color:red;");

					return;

				} else {

					cAddressOneTbId.setAttribute("value", value);
					postalAddrStr += value + ", ";
				}

				value = cAddressTwoTbId.getValue();
				if (value != null && !value.equals("")) {

					cAddressTwoTbId.setAttribute("value", value);
					postalAddrStr += value + ", ";

				}

				value = cCityTbId.getValue();
				if (value == null || value.equals("")) {

					MessageUtil.setMessage("Please provide City.", "color:red;");

					return;

				} else {

					cCityTbId.setAttribute("value", value);
					postalAddrStr += value + ", ";
				}

				value = cStateTbId.getValue();
				if (value == null || value.equals("")) {

					MessageUtil.setMessage("Please provide State.", "color:red;");

					return;

				} else {

					cStateTbId.setAttribute("value", value);
					postalAddrStr += value + ", ";
				}

				value = cCountryTbId.getValue();
				if (value == null || value.equals("")) {

					MessageUtil.setMessage("Please provide Country.", "color:red;");

					return;

				} else {

					cCountryTbId.setAttribute("value", value);
					postalAddrStr += value + ", ";
				}

				String pinValue = cPinLbId.getValue().trim();
				String countryType = currentUser.getCountryType();

				if (Utility.zipValidateMap.containsKey(countryType)) {

					if (pinValue.length() == 0 || pinValue.equals("")) {
						MessageUtil.setMessage("Pin / Zip code cannot be left empty.", "color:red;");
						return;
					}
					boolean validZip = Utility.validateZipCode(pinValue, countryType);
					if (!validZip) {
						MessageUtil.setMessage("Please enter valid zip code.", "color:red;");
						return;
					} else {
						cPinLbId.setAttribute("value", pinValue.trim());
						postalAddrStr += pinValue;
					}

				} else {

					if (pinValue != null && pinValue.length() > 0) {
						if (pinValue.length() > 6 || pinValue.length() < 5) {
							MessageUtil.setMessage("Please provide 5 / 6 digits Zip code / Pin.", "Color:red", "Top");
							return;
						} else {
							cPinLbId.setAttribute("value", pinValue.trim());
							postalAddrStr += pinValue;
						}
					}
				}
				value = cPhoneTbId.getValue().trim();
				if (value != null && value.trim().length() > 0) {

					try {
						String userPhoneRegex = "\\d+";
						Pattern phonePattern = Pattern.compile(userPhoneRegex);
						Matcher m = phonePattern.matcher(value);// (value);
						String poneMatch = "";
						while (m.find()) {
							poneMatch += m.group();
						}
						try {
							value = "" + Long.parseLong(poneMatch);
						} catch (NumberFormatException e) {
							MessageUtil.setMessage("Please provide valid Phone Number.", "color:red", "TOP");
							cPhoneTbId.setFocus(true);
							return;
						}
						UserOrganization organization = currentUser != null ? currentUser.getUserOrganization() : null;
						value = Utility.phoneParse(value, organization);
						if (value == null || value.trim().length() == 0) {
							MessageUtil.setMessage("Please provide valid Phone Number.", "Color:Red", "Top");
							return;
						}
						cPhoneTbId.setAttribute("value", "" + value);
						postalAddrStr += ", " + value;
					} catch (Exception e) {
						MessageUtil.setMessage("Please provide valid Phone Number.", "color:red", "TOP");
						cPhoneTbId.setFocus(true);
						return;
					}
				} else {
					cPhoneTbId.setAttribute("value", "" + value);
					postalAddrStr += value;
				}

				postalAddressRbId.setLabel(postalAddrStr);
				postalAddressDivId.setVisible(true);
				changeAddressDivId.setVisible(false);

				if (isModifyUser) {
					if (Messagebox.show(
							"Address fields are modified successfully. Do you want to apply \n address data to your user-account?",
							"Prompt", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.NO) {
						return;
					}

					currentUser.setAddressOne(cAddressOneTbId.getValue());

					if (cAddressTwoTbId.getValue() != null) {

						currentUser.setAddressTwo(cAddressTwoTbId.getValue());
					}
					currentUser.setCity(cCityTbId.getValue());
					currentUser.setState(cStateTbId.getValue());
					currentUser.setCountry(cCountryTbId.getValue());
					currentUser.setPinCode(cPinLbId.getValue().trim());
					if (cPhoneTbId.getValue() != null || cPhoneTbId.getValue().trim().length() > 0) {
						currentUser.setPhone(cPhoneTbId.getValue().trim());
					}
					usersDaoForDML.saveOrUpdate(currentUser);
				}
			}

		} catch (Exception e) {
			logger.error("** Exception while saving the postalAddress--:", e);
		}
	}

	public void setUserAddr() {
		String postalAddressData = "";
		String value = null;
		value = (String) cAddressOneTbId.getAttribute("value");
		if (value != null && !value.equals("")) {
			postalAddressData += value + ", ";
		}
		value = (String) cAddressTwoTbId.getAttribute("value");
		if (value != null && !value.equals("")) {
			postalAddressData += value + ", ";
		}
		value = (String) cCityTbId.getAttribute("value");
		if (value != null && !value.equals("")) {
			postalAddressData += value + ", ";
		}
		value = (String) cStateTbId.getAttribute("value");
		if (value != null && !value.equals("")) {
			postalAddressData += value + ", ";
		}
		value = (String) cCountryTbId.getAttribute("value");
		if (value != null && !value.equals("")) {
			postalAddressData += value + ", ";
		}
		String pinValue = (String) cPinLbId.getAttribute("value");
		if (pinValue != null) {
			postalAddressData += pinValue;
		}
		value = (String) cPhoneTbId.getAttribute("value");
		if (value != null && !value.equals("")) {
			postalAddressData += ", " + value;
		}
		postalAddressRbId.setLabel(postalAddressData);

	}

	public int getStore(String storeId) {
		int index = 0;
		String orgName = organizationStoresDao.findNameById(storeId, currentUser.getUserOrganization().getUserOrgId());
		if (orgName == null) {
			logger.debug("got no store with configured custom template");
			addrRgId.setSelectedIndex(0);
			return index;
		}
		for (Listitem item : storesLbId.getItems()) {
			if (orgName.trim().equals(item.getLabel())) {
				item.setSelected(true);
				index = item.getIndex();
				break;
			}

		}
		return index;

	}

	/*****************************************Create new Template Drag and Drop Editor*****************************************************/
	
	public void onClick$createNewTemplateId() throws ParseException {
		boolean dataFieldsEmptyCheck = false;
		if(updateIdValue!=null && updateIdValue.getValue()!=null && !updateIdValue.getValue().isEmpty()) {
			dataFieldsEmptyCheck = checkforchangesFormDBData();
		}else {
			Boolean redirectForFooterFormCampaign = (Boolean) Sessions.getCurrent().getAttribute("redirectForFooterFormCampaign");
			if(redirectForFooterFormCampaign!=null && !redirectForFooterFormCampaign) {
				dataFieldsEmptyCheck = checkAutoEmailFieldsContainValue();
			}
		}
		if(dataFieldsEmptyCheck && !isEdit) {
			if( Messagebox.show("There are unsaved changes on this page. Do you want to continue?", "Continue?",
					Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.YES) {
					savedSuccessfully =true;
			} else {
				savedSuccessfully =true;
				return;
			}
		}
		if(savedSuccessfully || isEdit || !dataFieldsEmptyCheck) {
			sessionScope = Sessions.getCurrent();
			if (!isEdit) {
				Sessions.getCurrent().removeAttribute("editTemplateId");
			}
			getCouponsListAutoEmail();
            Sessions.getCurrent().setAttribute("beeReload", false);
			Redirect.goTo(PageListEnum.AUTOEMAIL_HTML_BEE_EDITOR);
		}
	}
	
	public void onSelect$typeOfAutoEmailListLbId(){

		try {
			
			
			
			if(typeOfAutoEmailListLbId.getSelectedCount() == 0) {
				MessageUtil.setMessage("There are no sms types found.", "green");
				return;
			}
			
			if(typeOfAutoEmailListLbId.getSelectedIndex()==17 || typeOfAutoEmailListLbId.getSelectedIndex()==18) {
				Sessions.getCurrent().setAttribute("otptype","OTP MESSAGE");
			
				logger.info("entering otp loop");
				Set<MailingList> set = new HashSet<MailingList>();
				getPlaceHolderListAutoEmailOTP(set, false, null);
			}
			
		
		}catch(Exception e) {}
	
	}
	
	
	public JSONObject getDefaultTemplate(Users currentUser) {
		logger.debug(" in BEE editor");
		if (jsonStuff == null) {
			jsonStuff = Constants.DEFAULT_JSON_VALUE;
		}
		JSONObject jsontemplate = (JSONObject) JSONValue.parse(jsonStuff);
		return jsontemplate;
	}
	
	private boolean isNew=false;
	public void onClick$submitBtnId() {

		try {
			if (custTempNameTbId.getValue().trim().length() == 0) {
				MessageUtil.setMessage("Please enter Auto Email name.", "color:red","TOP");
				custTempNameTbId.setFocus(true);		
				return;
			}
			CustomTemplates customTemplates = null;
			if(updateIdValue.getValue()!= null && !updateIdValue.getValue().isEmpty()) {
				customTemplates = customTemplateDao.findAllByUserIdAndTemplateId(currentUser.getUserId(),Long.parseLong(updateIdValue.getValue()));
				if(customTemplates == null) {
					Messagebox.show("Template Not Found.");
				}
			}else {
				customTemplates = new CustomTemplates();
				isNew = true;
				customTemplates.setCreatedDate(Calendar.getInstance());
				customTemplates.setUserId(currentUser);
			}
			customTemplates.setModifiedDate(Calendar.getInstance());
			String categoryType = getTemplateType();
			if(categoryType == null) {
				MessageUtil.setMessage(" Auto-Email Category Cannot be Empty. ","red");
				return;
			}
			if(customFooterId.isChecked() && chooseTemplateId.getSelectedItem()!=null && chooseTemplateId.getSelectedItem().getAttribute("data-id")!=null) {
				MyTemplates template  = myTemplatesDao.getTemplateByMytemplateIdandUserId(Long.parseLong(chooseTemplateId.getSelectedItem().getAttribute("data-id").toString()),currentUser.getUserId());
				if(!template.getJsoncontent().contains(Constants.SENDER_FOOTER_EMAIL_ADDRESS_AUTOEMAIL)) {
					MessageUtil.setMessage("Looks like the Sender Email Address merge-tag \n"
								+ "is missing in your email content. Please note \n"
								+ "that default sender address will be added to \n"
								+ "email-footer at the time of sending to be \n"
								+ "compliant with email sending laws.","blue");
				}
			}
			
			customTemplates.setType(categoryType);
			customTemplates.setTemplateName(custTempNameTbId.getValue());
		
			storeData(false, customTemplates);
			//updateIdValue.setValue(Constants.STRING_NILL);
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}
	

	private static final String PARENTALCONSENT = "parentalConsent";
	private static final String DOUBLEOPTIN = "welcomemail";
	private static final String LOYALTYOPTIN = "loyaltyOptin";
	private static final String WEBFORMEMAIL = "webformWelcomeEmail";
	private static final String TIERUPGRADATION = "tierUpgradation";
	private static final String EARNEDBONUS = "earnedBonus";
	private static final String EARNEDREWARDEXPIRATION = "earnedRewardExpiration";
	private static final String MEMBERSHIPEXPIRATION = "membershipExpiration";
	private static final String GIFTAMOUNTEXPIRATION = "giftAmountExpiration";
	private static final String GIFTCARDEXPIRATION = "giftCardExpiration";
	private static final String GIFTCARDISSUANCE = "giftCardIssuance";
	private static final String FEEDBACKFORM="feedbackform";
	private static final String SPECIALREWARDS="specialRewards";
	private static final String LOYALTYADJUSTMENT="loyaltyAdjustment";
	private static final String LOYALTYISSUANCE="loyaltyIssuance";
	private static final String LOYALTYREDEMPTION="loyaltyRedemption";
	private static final String OTPMESSAGES="OTPMESSAGE ";
	private static final String REDEMPTIONOTP="redemptionOtp";
	
	public String getTemplateType() {
		String type = null;
		if (typeOfAutoEmailListLbId.getSelectedIndex() == 1) {
			type = DOUBLEOPTIN;
		} else if (typeOfAutoEmailListLbId.getSelectedIndex() == 2) {
			type = PARENTALCONSENT;
		} else if (typeOfAutoEmailListLbId.getSelectedIndex() == 3) {
			type = LOYALTYOPTIN;
		} else if (typeOfAutoEmailListLbId.getSelectedIndex() == 4) {
			type = WEBFORMEMAIL;
		} else if (typeOfAutoEmailListLbId.getSelectedIndex() == 5) {
			type = GIFTCARDISSUANCE;
		} else if (typeOfAutoEmailListLbId.getSelectedIndex() == 6) {
			type = TIERUPGRADATION;
		} else if (typeOfAutoEmailListLbId.getSelectedIndex() == 7) {
			type = EARNEDBONUS;
		} else if (typeOfAutoEmailListLbId.getSelectedIndex() == 8) {
			type = EARNEDREWARDEXPIRATION;
		} else if (typeOfAutoEmailListLbId.getSelectedIndex() == 9) {
			type = MEMBERSHIPEXPIRATION;
		} else if (typeOfAutoEmailListLbId.getSelectedIndex() == 10) {
			type = GIFTAMOUNTEXPIRATION;
		} else if (typeOfAutoEmailListLbId.getSelectedIndex() == 11) {
			type = GIFTCARDEXPIRATION;
		}else if (typeOfAutoEmailListLbId.getSelectedIndex() == 12) {
			type = FEEDBACKFORM;
		}else if (typeOfAutoEmailListLbId.getSelectedIndex() == 13) {
			type = SPECIALREWARDS;
		}else if(typeOfAutoEmailListLbId.getSelectedIndex() == 14) {
			type= LOYALTYADJUSTMENT;
		}else if(typeOfAutoEmailListLbId.getSelectedIndex() == 15) {
			type= LOYALTYISSUANCE;
		}else if(typeOfAutoEmailListLbId.getSelectedIndex() == 16) {
			type= LOYALTYREDEMPTION;
		}else if(typeOfAutoEmailListLbId.getSelectedIndex() == 17) {
			type= OTPMESSAGES;
		}else if(typeOfAutoEmailListLbId.getSelectedIndex() == 18) {
			type= REDEMPTIONOTP;
		}
		
		
		
		return type;
	}

	/************************************* ******Auto Email Save *****************************************************/
	private boolean savedSuccessfully;
	private void storeData(boolean isUpdate, CustomTemplates customTemplates) {
		try {
			String textHTML = "";
			if(chooseTemplateId.getSelectedItem()!=null  && chooseTemplateId.getSelectedItem().hasAttribute("data-id")) {
				if(chooseTemplateId.getSelectedItem().getAttribute("data-id").toString()!=null) {
					MyTemplates myTemplates  = myTemplatesDao.getTemplateByMytemplateIdandUserId(Long.parseLong(chooseTemplateId.getSelectedItem().getAttribute("data-id").toString()),currentUser.getUserId());
					textHTML  = myTemplates.getContent();
				}
			}
			String isValidPhStr = null;
			isValidPhStr = Utility.validatePh(textHTML, currentUser);

			if (isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")) {

				MessageUtil.setMessage("Invalid Placeholder: " + isValidPhStr, "red");
				return;
			} else if (isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")) {

				MessageUtil.setMessage(
						"You have currently disabled subscriber preference center setting.\n To continue, either  enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.",
						"color:red");
				return;

			}

			String isValidCCDim = null;
			isValidCCDim = Utility.validateCouponDimensions(textHTML);
			if (isValidCCDim != null) {
				return;
			}

			String isValidCouponAndBarcode = null;
			isValidCouponAndBarcode = Utility.validateCCPh(textHTML, currentUser);
			if (isValidCouponAndBarcode != null) {
				return;
			}

			String subscribeLink = PropertyUtil.getPropertyValue("subscribeLink");
			if(textHTML!=null && !textHTML.isEmpty()) {
				if (typeOfAutoEmailListLbId.getSelectedIndex() == 1 && textHTML.indexOf(subscribeLink) < 0) {
					MessageUtil.setMessage("In order for the auto-email to function correctly, please add 'Confirm subscription' merge-tag in the email content.", "top");
				}
			}
			
			//otp mergetag validation
			String otpCode="[OTP]";
			if(textHTML!=null && !textHTML.isEmpty()) {
				if ((typeOfAutoEmailListLbId.getSelectedIndex() == 17 || typeOfAutoEmailListLbId.getSelectedIndex() == 18)&& textHTML.indexOf(otpCode) < 0) {
					MessageUtil.setMessage("In order for the auto-email to function correctly, please add 'OTP Code ' merge-tag in the email content.", "top");
					return;
				}
			}

			if (cSubTb.isValid() && !cSubTb.getValue().isEmpty()) {
				customTemplates.setSubject(cSubTb.getValue());
			} else {
				MessageUtil.setMessage("Please provide subject. Subject should not be empty.", "color:red", "TOP");
				cSubTb.setFocus(true);
				return;
			}

			if (cFromNameTb.isValid() && Utility.validateFromName(cFromNameTb.getValue())) {
				customTemplates.setFromName(cFromNameTb.getValue());
			} else {
				MessageUtil.setMessage("Provide valid 'From Name'. Special characters are not allowed.", "color:red",
						"TOP");
				cFromNameTb.setFocus(true);
				return;
			}

			if (cFromEmailCb.getSelectedItem().getLabel().indexOf('@') < 0) {
				MessageUtil.setMessage("Register 'From Email' to create Double Opt-in email.", "color:red", "TOP");
				return;
			} else if (!(cFromEmailCb.getSelectedIndex() == -1)) {
				customTemplates.setFromEmail(cFromEmailCb.getSelectedItem().getLabel());
			} else {
				MessageUtil.setMessage("Provide valid From Email Address.", "color:red", "TOP");
				cFromEmailCb.setFocus(true);
				return;
			}
			
			if(creplyToEmailCb!= null && !creplyToEmailCb.getValue().isEmpty()) {
				if (creplyToEmailCb.getValue().indexOf('@') < 0) {
					MessageUtil.setMessage("Invalid Reply to Email.", "color:red", "TOP");
					return;
				} else if ((creplyToEmailCb.getValue() != null)) {
					customTemplates.setReplyToEmail(creplyToEmailCb.getValue());
				} else {
					MessageUtil.setMessage("Provide valid From Email Address.", "color:red", "TOP");
					creplyToEmailCb.setFocus(true);
					return;
				}
			}
			

			customTemplates.setAddressFlag(true);
			Address address = new Address();

			String includeBfrStr = "";

			if (orgNameChkBoxId.isChecked()) {

				customTemplates.setIncludeOrg(true);
				if (includeBfrStr.trim().length() > 0)
					includeBfrStr += Constants.ADDR_COL_DELIMETER;
				includeBfrStr += currentUser.getUserOrganization().getOrganizationName();

			} else {
				customTemplates.setIncludeOrg(false);

			}
			if (orgUnitChkBoxId.isChecked()) {

				customTemplates.setIncludeOrgUnit(true);
				if (includeBfrStr.trim().length() > 0)
					includeBfrStr += Constants.ADDR_COL_DELIMETER;
				List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(currentUser.getUserId());
				String userDomainStr = "";
				Set<UsersDomains> domainSet = new HashSet<UsersDomains>();// currentUser.getUserDomains();
				if (domainsList != null) {
					domainSet.addAll(domainsList);
					for (UsersDomains usersDomains : domainSet) {

						if (userDomainStr.length() > 0)
							userDomainStr += ",";
						userDomainStr += usersDomains.getDomainName();

					}
				}

				includeBfrStr += userDomainStr;// currentUser.getUserDomainStr();

			} else {

				customTemplates.setIncludeOrgUnit(false);
			}
			customTemplates.setIncludeBeforeStr(includeBfrStr);
			
			customTemplates.setCustomizeFooter(customFooterId.isChecked());
			if(customFooterId.isChecked()) {
				addrRgId.setSelectedIndex(0);
			}

			if (addrRgId.getSelectedIndex() == 0) {

				if (!cAddressOneTbId.getValue().trim().equals("")) {
					address.setAddressOne(cAddressOneTbId.getValue());
				} else {
					MessageUtil.setMessage("Please provide Address Line 1.", "color:red", "TOP");
					cAddressOneTbId.setFocus(true);
					
					return;
				}

				address.setAddressTwo(cAddressTwoTbId.getValue() != null ? cAddressTwoTbId.getValue().trim() : "");

				if (!cCityTbId.getValue().trim().equals("")) {
					address.setCity(cCityTbId.getValue());
				} else {
					MessageUtil.setMessage("Please provide City.", "color:red", "TOP");
					cCityTbId.setFocus(true);
					
					return;
				}
				if (!cStateTbId.getValue().trim().equals("")) {
					address.setState(cStateTbId.getValue());
				} else {
					MessageUtil.setMessage("Please provide State.", "color:red", "TOP");
					cStateTbId.setFocus(true);
					
					return;
				}
				if (!cCountryTbId.getValue().trim().equals("")) {
					address.setCountry(cCountryTbId.getValue());
				} else {
					MessageUtil.setMessage("Please provide Country.", "color:red", "TOP");
					cCountryTbId.setFocus(true);
					
					return;
				}

				String pin = cPinLbId.getValue().trim();
				String countryType = currentUser.getCountryType();

				if (Utility.zipValidateMap.containsKey(countryType)) {

					if (pin.length() == 0)
						MessageUtil.setMessage("Please provide Pin.", "color:red", "TOP");
					boolean validZip = Utility.validateZipCode(pin, countryType);
					if (validZip) {
						address.setPin((pin));
					} else {
						MessageUtil.setMessage("Please enter valid zip code.", "color:red;");
						cPinLbId.setFocus(true);
						
						return;
					}
				} else {
					if (pin != null && pin.length() > 0) {
						if (pin.length() > 6 || pin.length() < 5) {
							MessageUtil.setMessage("Please provide 5 / 6 digits Zip code / Pin.", "Color:red", "Top");
							
							return;
						}
					}
				}

				if (cPhoneTbId.getValue() != null && !cPhoneTbId.getValue().isEmpty()) {
					try {
						String value = cPhoneTbId.getValue();

						String userPhoneRegex = "\\d+";
						Pattern phonePattern = Pattern.compile(userPhoneRegex);
						Matcher m = phonePattern.matcher(value);// (value);
						String poneMatch = "";
						while (m.find()) {
							poneMatch += m.group();
						}
						try {
							value = "" + Long.parseLong(poneMatch);
						} catch (NumberFormatException e) {
							MessageUtil.setMessage("Please provide valid Phone Number.", "color:red", "TOP");
							
							return;
						}
						UserOrganization organization = currentUser != null ? currentUser.getUserOrganization() : null;
						value = Utility.phoneParse(value, organization);
						if (value == null || value.trim().length() == 0) {

							MessageUtil.setMessage("Please provide valid Phone Number.", "Color:Red", "Top");
							
							return;
						}
						address.setPhone(value);
					} catch (Exception e) {
						MessageUtil.setMessage("Please provide valid Phone Number.", "color:red", "TOP");
						cPhoneTbId.setFocus(true);
						
						return;
					}
				}
				customTemplates.setAddrType(Constants.CAMP_ADDRESS_TYPE_USER);
				customTemplates.setAddress(address);
				customTemplates.setAddressStr(customTemplates.getAddressStr());

			}else if(addrRgId.getSelectedIndex() == 1) {
				
				address = new Address(Constants.PH_CAMPAIGN_ADDRESS_CONTACT_HOMESTORE);
				customTemplates.setAddrType(Constants.CAMP_ADDRESS_TYPE_CONTACT_HOME_STORE);
				customTemplates.setAddress(address);
				customTemplates.setAddressStr(address.getDinamicAddrstr());
			}else if(addrRgId.getSelectedIndex() == 2) {
				//logger.debug("-----------10-------------");
				customTemplates.setAddrType(Constants.CAMP_ADDRESS_TYPE_CONTACT_LAST_PURCHASED_STORE);
			
				address = new Address(Constants.PH_CAMPAIGN_ADDRESS_CONTACT_LAST_PURCHASED_STORE);
				customTemplates.setAddress(address);
				customTemplates.setAddressStr(address.getDinamicAddrstr());
			}else if (addrRgId.getSelectedIndex() == 3) {
				if (storesLbId.getItemCount() == 0) {
					MessageUtil.setMessage(
							"Please choose another sender address option. No stores exist for the organization.",
							"color:red;");
					
					return;

				}

				OrganizationStores store = (OrganizationStores) storesLbId.getSelectedItem().getValue();
				address = store.getAddress();
				customTemplates.setAddrType(Constants.CAMP_ADDRESS_TYPE_STORE + "|" + store.getStoreId());
				customTemplates.setAddress(address);
				customTemplates.setAddressStr(customTemplates.getAddressStr());

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
			
			if(cPermRemRb.getSelectedIndex() == 0){
				if(!permRemTextId.getValue().trim().equals("")){
					customTemplates.setPermissionRemainderFlag(true);
					customTemplates.setPermissionRemainderText(permRemTextId.getValue());
				}
			}else{
				customTemplates.setPermissionRemainderFlag(false);
			}
			if(downloadPdf.isChecked()) {
				customTemplates.setDownloadPdf(true);
			}else {
				customTemplates.setDownloadPdf(false);
			}

			if (isUpdate) {
				custTempNameTbId.setValue("");
			} else {
				if (chooseTemplateId.getSelectedItem() != null) {
					if(chooseTemplateId.getSelectedItem().hasAttribute("data-id-ck")) {
						customTemplates.setEditorType(Constants.EDITOR_TYPE_PLAIN);
					}else {
						if(chooseTemplateId.getSelectedItem()!=null && chooseTemplateId.getSelectedItem().getAttribute("data-id")!=null) {
							customTemplates.setMyTemplateId(Long.parseLong(chooseTemplateId.getSelectedItem().getAttribute("data-id").toString()));
							customTemplates.setEditorType(Constants.EDITOR_TYPE_BEE);
							if(customTemplates.getHtmlText()!=null && !customTemplates.getHtmlText().isEmpty()) {
								customTemplates.setHtmlText(null);
							}
						}else {
							MessageUtil.setMessage(" Missing template. Please configure a template with the auto-email message.", "color:red;");
							return;
						}
					}
				} else {
					MessageUtil.setMessage(" Missing template. Please configure a template with the auto-email message.", "color:red;");
					return;
				}
				customTemplateDaoForDML.saveOrUpdate(customTemplates);
				savedSuccessfully =true;
				createAutoEmailTabPanelId.setLabel("Create New Message");
				//typeOfAutoEmailListLbId.getItems().clear();
				//chooseTemplateFolderId.getItems().clear();
				MessageUtil.clearMessage();
				MessageUtil.setMessage("Auto-email message was saved successfully.", "green", "top");
				getAutoEmailList();
				autoEmailListId.setSelected(true);
			}
		} catch (Exception e) {
			logger.error("** Exception :", e);
		}

	}
	
	/*********************************** filter Code ****************************************/
	
	public void onClick$filterBtnId() {
		List<String> autoTypeStr = new ArrayList<String>();
		searchMap = new LinkedHashMap<String, String>();
		String autoEmailType = srchLbId.getSelectedItem().getLabel();
		if(srchLbId.getSelectedIndex() == 0 && !createdDateId.isVisible()) {
			onClick$resetAnchId();
			return;
		}
		if(createdDateId.isVisible()) {
			if((fromDateboxId!=null && fromDateboxId.getValue()==null) || (toDateboxId!=null && toDateboxId.getValue()==null)) {
				MessageUtil.setMessage("Please choose Date To Filter", "color:red;");
				return;
			}
		}
		String dateSearch = null;
		if(createdDateId.isVisible()) {
			String startDate = MyCalendar.calendarToString(getStartDate(),
					MyCalendar.FORMAT_DATETIME_STYEAR);
			String enddate = MyCalendar.calendarToString(getEndDate(),
					MyCalendar.FORMAT_DATETIME_STYEAR);
			dateSearch = (startDate)+","+(enddate);
		}
			searchMap = new LinkedHashMap<String, String>();
			autoTypeStr.add(autoEmailType);
			String value = getselectList(autoTypeStr);
			if(value!=null) {
				searchMap.put("autoEmailType", value);	
			}else if(dateSearch!=null) {
				searchMap.put("createdDate", dateSearch);
			}
		try {
			autoEmailBEEListPaging.setActivePage(0);
			int count = Integer.parseInt(autoEmailPageSizeLbId.getSelectedItem().getLabel());
			autoEmailGridId.setModel(getCustomTemplateModel(0, count, searchMap,null,null));
			autoEmailGridId.setRowRenderer(rowRender);
			autoEmailBEEListPaging.setPageSize(count);
			if(srchLbId.isVisible() == false){
				srchLbId.setSelectedIndex(0);
			}
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
		} finally {
			reset = false;
		}
	}
	
	private String getselectList(List<String> autoTypeStr) {
		String value = null;
		for (String eachType : autoTypeStr) {
			if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_DOUBLEOPTIN)) {
				value = DOUBLEOPTIN;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_PARENTALCONSENT)) {
				value = PARENTALCONSENT;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_LOYALTYOPTIN)) {
				value = LOYALTYOPTIN;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_WEBFORMEMAIL)) {
				value = WEBFORMEMAIL;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_TIER_UPGRADATION)) {
				value = TIERUPGRADATION;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION)) {
				value = EARNEDREWARDEXPIRATION;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_MEMBERSHIP_EXPIRATION)) {
				value = MEMBERSHIPEXPIRATION;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_EARNED_BONUS)) {
				value = EARNEDBONUS;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION)) {
				value = GIFTAMOUNTEXPIRATION;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION)) {
				value = GIFTCARDEXPIRATION;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE)) {
				value = GIFTCARDISSUANCE;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_FEEDBACK_FORM)) {
				value = FEEDBACKFORM;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_SPECIAL_REWARDS)) {
				value = SPECIALREWARDS;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_ADJUSTMENT)) {
				value = LOYALTYADJUSTMENT;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_ISSUANCE)) {
				value = LOYALTYISSUANCE;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_REDEMPTION)) {
				value = LOYALTYREDEMPTION;
			}  else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_OTP_MESSAGE)) {
				value = OTPMESSAGES;
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_REDEMPTION_OTP)) {
				value = REDEMPTIONOTP;
			}
			
			
		
		
			
		}
		return value;

	}
	
	
	/*********************************** Pagination Code ****************************************/
	
	private ListModel<CustomTemplates> getCustomTemplateModel(int start, int pageSize, Map<String, String> searchMap,String orderby_colName,String desc_Asc) {
		CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao) SpringUtil.getBean("customTemplatesDao");
		List<CustomTemplates> customTempUserCount = null;
		List<CustomTemplates> customTemp = null;
		customTempUserCount = customTemplatesDao.findAllByUserIdPagingCount(currentUser.getUserId(), searchMap);
		autoEmailBEEListPaging.setTotalSize(customTempUserCount.size());
		pageSize = Integer.parseInt(autoEmailPageSizeLbId.getSelectedItem().getLabel());
		autoEmailBEEListPaging.setPageSize(pageSize);
		if(searchMap!= null && !searchMap.containsValue(null) && !searchMap.isEmpty()) {
			customTemp = customTemplatesDao.findAllByUserIdPaging(currentUser.getUserId(), start,pageSize, searchMap,orderby_colName,desc_Asc);
		}else {
			customTemp = customTemplatesDao.findAllByUserIdPaging(currentUser.getUserId(), start,pageSize, searchMap,orderby_colName,desc_Asc);
		}
		return new ListModelList(customTemp);
	}

	public RowRenderer getRowRenderer() {
		return rowRender;
	}
	
	private final RowRenderer rowRender  = new MyRenderer();

	private class MyRenderer implements RowRenderer, EventListener {
		MyRenderer() {
			super();
			logger.debug("new MyRenderer object is created");
		}

		@Override
		public void onEvent(Event evt) throws Exception {
			Object obj = evt.getTarget();
			try {
				if (evt.getTarget() instanceof Image) {
					Image currentImage = (Image) evt.getTarget();
					String campaignImageType = (String) currentImage.getAttribute("type");

					if (campaignImageType.equalsIgnoreCase("emailPage")) {
						doPopUp(currentImage);
					} else if (campaignImageType.equalsIgnoreCase("emailEdit")) {
						edit(currentImage);
					} else if (campaignImageType.equalsIgnoreCase("emailDelete")) {
						delete(currentImage);
					} else if (campaignImageType.equalsIgnoreCase("preview")) {
						preview(currentImage);
					} else if (campaignImageType.equalsIgnoreCase("emailReport")) {
						report(currentImage);
					}
				}
				if (obj instanceof Paging) {
					logger.debug("--Entered onClick Row --");
					Paging a = (Paging) obj;
					Rows rows = (Rows) (a.getParent()).getParent();
					Row row = (Row) rows.getAttribute("prevSelRow");
					if (row != null) {
						row.setStyle("cursor:pointer");
					}
					row = (Row) a.getParent();
					rows.setAttribute("prevSelRow", row);
					row.setStyle("background-color:#BFEAFF");
					CustomTemplates customTemplates = (CustomTemplates) a.getAttribute("userObj");
					if (customTemplates == null)
						return;

					Include inc = (Include) rows.getParent().getAttribute("editUserIncId");
//				logger.debug("-- 1 --");
					inc.setSrc(PageListEnum.EMPTY.getPagePath());
//				logger.debug("-- 2 --");
					inc.setSrc("/zul/admin/editUser.zul");
//				logger.debug("-- 3 --");
				}
			} catch (Exception e) {
				logger.error("** Exception :", e);
			}
		}

		
		
		
		
		private void report(Image currentImage) {
			StringBuilder sb = new StringBuilder();
			String returnTypeValue = null;
			if(currentImage.hasAttribute("data-id-ck")) {
				CustomTemplates templateId =  (CustomTemplates)currentImage.getAttribute("data-id-ck");
				String listValue = setAutoEmailListDisplayValue(templateId.getType().toString());
				returnTypeValue = getAutoEmailSelectedType(listValue);
				sb.append(returnTypeValue).append(Constants.ADDR_COL_DELIMETER).append(templateId.getTemplateId());
				sessionScope.setAttribute("selectedAutoEmail", sb.toString());
				Redirect.goTo(PageListEnum.REPORT_AUTO_EMAIL_DETAILED_REPORT);
			}else if(currentImage!= null && currentImage.getAttribute("data-id")!= null) {
				CustomTemplates templateId =  (CustomTemplates)currentImage.getAttribute("data-id");
				String listValue = setAutoEmailListDisplayValue(templateId.getType().toString());
				returnTypeValue = getAutoEmailSelectedType(listValue);
				sb.append(returnTypeValue).append(Constants.ADDR_COL_DELIMETER).append(templateId.getTemplateId());
				sessionScope.setAttribute("selectedAutoEmail", sb.toString());
				Redirect.goTo(PageListEnum.REPORT_AUTO_EMAIL_DETAILED_REPORT);
			}
		}

		private String getAutoEmailSelectedType(String type) {
			String returnTypeValue = null;
			switch (type) {
			case Constants.CUSTOM_TEMPLATE_TYPE_DOUBLEOPTIN:
				returnTypeValue = "needtochange";
				break;
			case Constants.CUSTOM_TEMPLATE_TYPE_PARENTALCONSENT:
				returnTypeValue = "TestParentalMail";
				break;
			case Constants.CUSTOM_TEMPLATE_TYPE_LOYALTYOPTIN:
				returnTypeValue = "LoyaltyDetails";
				break;
			case Constants.CUSTOM_TEMPLATE_TYPE_WEBFORMEMAIL:
				returnTypeValue = "WelcomeEmail";
				break;
			case Constants.CUSTOM_TEMPLATE_TYPE_TIER_UPGRADATION:
				returnTypeValue = "LoyaltyTierUpgradation";
				break;
			case Constants.CUSTOM_TEMPLATE_TYPE_EARNED_BONUS:
				returnTypeValue = "LoyaltyEarningBonus";
				break;
			case Constants.CUSTOM_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION:
				returnTypeValue = "LoyaltyRewardExpiry";
				break;
			case Constants.CUSTOM_TEMPLATE_TYPE_MEMBERSHIP_EXPIRATION:
				returnTypeValue = "LoyaltyMembershipExpiry";
				break;
			case Constants.CUSTOM_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION:
				returnTypeValue = "LoyaltyGiftAmountExpiry";
				break;
			case Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION:
				returnTypeValue = "LoyaltyGiftCardExpiry";
				break;
			case Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE:
				returnTypeValue = "giftCardIssuance";
				break;
			case Constants.CUSTOM_TEMPLATE_TYPE_FEEDBACK_FORM:
				returnTypeValue = "feedBackForm";
				break;
			case Constants.CUSTOM_TEMPLATE_TYPE_SPECIAL_REWARDS:
				returnTypeValue = "specialRewards";
				break;
			case Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_ADJUSTMENT:
				returnTypeValue = "loyaltyAdjustment";
				break;
			case Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_ISSUANCE:
				returnTypeValue = "loyaltyIssuance";
				break;
			case Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_REDEMPTION:
				returnTypeValue = "loyaltyRedemption";
				break;
			case Constants.CUSTOM_TEMPLATE_TYPE_OTP_MESSAGE:
				returnTypeValue = "OTPMESSAGE";
				break;
			case Constants.CUSTOM_TEMPLATE_TYPE_REDEMPTION_OTP:
				returnTypeValue = "redemptionOtp";
				break;
			
			
			}
			return returnTypeValue;
		}


		@Override
		public void render(Row row, Object data, int arg2) throws Exception {

			try {
				if (data instanceof CustomTemplates) {
					CustomTemplates customTemplatesData = (CustomTemplates) data;
					new Label(customTemplatesData.getTemplateName().toString()).setParent(row);
					if (customTemplatesData.getType() != null) {
						String listValue = setAutoEmailListDisplayValue(customTemplatesData.getType().toString());
						new Label(listValue).setParent(row);

					} else {
						new Label("--").setParent(row);
					}
					if(customTemplatesData.getCreatedDate()!= null) {
						new Label(MyCalendar.calendarToString(customTemplatesData.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STDATE, timeZone)).setParent(row);
					}else {
						new Label("--").setParent(row);
					}
					new Label(customTemplatesData.getSubject()).setParent(row);
					new Label(customTemplatesData.getFromName()).setParent(row);
					new Label(customTemplatesData.getFromEmail()).setParent(row);
					if(customTemplatesData.getModifiedDate()!= null) {
						new Label(MyCalendar.calendarToString(customTemplatesData.getModifiedDate(), MyCalendar.FORMAT_DATETIME_STDATE, timeZone)).setParent(row);
					}else {
						new Label("--").setParent(row);
					}
					Hbox hbox = new Hbox();

					Image preview = new Image("/img/theme/preview_icon.png");
					preview.setTooltiptext("Preview");
					preview.setStyle("cursor:pointer;margin-right:5px;");
					preview.addEventListener("onClick", this);
					preview.setAttribute("type", "preview");
					if(customTemplatesData.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE)) {
						preview.setAttribute("data-id", customTemplatesData.getMyTemplateId());
					}else if(customTemplatesData.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_PLAIN)) {
						preview.setAttribute("data-id-ck", customTemplatesData.getTemplateId());
					}
					preview.setParent(hbox);

					Image editImg = new Image("/img/email_edit.gif");
					editImg.setTooltiptext("Edit");
					editImg.setStyle("cursor:pointer;margin-right:5px;");
					editImg.addEventListener("onClick", this);
					editImg.setAttribute("type", "emailEdit");
					editImg.setAttribute("data-id", customTemplatesData.getTemplateId());
					editImg.setParent(hbox);

					Image img = new Image("/img/email_page.gif");
					img.setTooltiptext("Send Test Mail");
					img.setStyle("cursor:pointer;margin-right:5px;");
					img.addEventListener("onClick", this);
					img.setAttribute("type", "emailPage");
					if(customTemplatesData.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE)) {
						img.setAttribute("data-id", customTemplatesData.getMyTemplateId());
					}else if(customTemplatesData.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_PLAIN)) {
						img.setAttribute("data-id-ck", customTemplatesData.getTemplateId());
					}
					img.setParent(hbox);

					Image delImg = new Image("/img/action_delete.gif");
					delImg.setTooltiptext("Delete");
					delImg.setStyle("cursor:pointer;");
					delImg.addEventListener("onClick", this);
					delImg.setAttribute("type", "emailDelete");
					delImg.setAttribute("data-id", customTemplatesData.getTemplateId());
					delImg.setParent(hbox);

					Image reportImg = new Image("/img/theme/home/reports_icon.png");
					reportImg.setTooltiptext("Report");
					reportImg.setStyle("cursor:pointer;");
					reportImg.addEventListener("onClick", this);
					reportImg.setAttribute("type", "emailReport");
					reportImg.setAttribute("data-id", customTemplatesData);
					reportImg.setParent(hbox);

					hbox.setParent(row);
					hbox.setWidth("100%");

				}
			} catch (Exception e) {
				logger.error("Exception while rendering the records ", e);

			}
		}
	}
	
	
	public void onEvent(Event event) throws Exception {
		super.onEvent(event);
		if (event.getTarget() instanceof Paging) {
			Paging paging = (Paging) event.getTarget();
			int desiredPage = paging.getActivePage();
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			Map<String, String> searchMap = null;
			if (reset == false) {
				List<String> autoTypeStr = new ArrayList<String>();
				searchMap = new LinkedHashMap<String, String>();
				String autoEmailType = srchLbId.getSelectedItem().getLabel();
				if(createdDateId.isVisible()) {
					if((fromDateboxId!=null && fromDateboxId.getValue()==null) || (toDateboxId!=null && toDateboxId.getValue()==null)) {
						return;
					}
				}
				String dateSearch = null;
				if(createdDateId.isVisible()) {
					String startDate = MyCalendar.calendarToString(getStartDate(),
							MyCalendar.FORMAT_DATETIME_STYEAR);
					String enddate = MyCalendar.calendarToString(getEndDate(),
							MyCalendar.FORMAT_DATETIME_STYEAR);
					dateSearch = (startDate)+","+(enddate);
				}
				autoTypeStr.add(autoEmailType);
				String value = getselectList(autoTypeStr);
				searchMap.put("autoEmailType", value);
				searchMap.put("createdDate", dateSearch);
			} else {
				searchMap = null;
			}
			autoEmailGridId
					.setModel(getCustomTemplateModel(ofs, (byte) pagingEvent.getPageable().getPageSize(), searchMap,null,null));
			autoEmailGridId.setRowRenderer(rowRender);
		}
	}

	public void onSelect$autoEmailPageSizeLbId() {
		try {
			if(srchLbId.getSelectedIndex() <= 0 && !createdDateId.isVisible()) {
				autoEmailBEEListPaging.setActivePage(0);
				int count = Integer.parseInt(autoEmailPageSizeLbId.getSelectedItem().getLabel());
				autoEmailGridId.setModel(getCustomTemplateModel(0, count, null,null,null));
				autoEmailGridId.setRowRenderer(rowRender);
				autoEmailBEEListPaging.setPageSize(count);
			}else {
				autoEmailBEEListPaging.setActivePage(0);
				int count = Integer.parseInt(autoEmailPageSizeLbId.getSelectedItem().getLabel());
				if(fromDateboxId.getValue()!=null && toDateboxId.getValue()!=null) {
					onClick$filterBtnId();
				}
				autoEmailGridId.setModel(getCustomTemplateModel(0, count, searchMap,null,null));
				autoEmailGridId.setRowRenderer(rowRender);
				autoEmailBEEListPaging.setPageSize(count);
			}
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}
	
	/****************************************EDIT Template*******************************************/
	
	public void onClick$editAutoEmailTemplateId() throws ParseException {
		
		//autoe=false;
	
		boolean dataFieldsEmptyCheck = false;
		if(updateIdValue!=null && updateIdValue.getValue()!=null && !updateIdValue.getValue().isEmpty()) {
			dataFieldsEmptyCheck = checkforchangesFormDBData();
		}else {
			dataFieldsEmptyCheck = checkAutoEmailFieldsContainValue();
		}
		if(dataFieldsEmptyCheck && !isEdit) {
			if( Messagebox.show("There are unsaved changes on this page. Do you want to continue?", "Continue?",
					Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.YES) {
					savedSuccessfully =true;
			} else {
				savedSuccessfully =true;
				return;
			}
		}
		if(typeOfAutoEmailListLbId.getSelectedIndex()==17 || typeOfAutoEmailListLbId.getSelectedIndex()==18) {
			Sessions.getCurrent().setAttribute("otptype","OTP MESSAGE");
		
			logger.info("entering otp loop");
			Set<MailingList> set = new HashSet<MailingList>();
			getPlaceHolderListAutoEmailOTP(set, false, null);
		}
		if (chooseTemplateId.getSelectedItem() != null) {
			if (chooseTemplateId.getSelectedItem().hasAttribute("data-id-ck")) {
				Long templateId = (Long) chooseTemplateId.getSelectedItem().getAttribute("data-id-ck");
				redirectToCkEditor(templateId);
			} else {
				Long templateId = (Long) chooseTemplateId.getSelectedItem().getAttribute("data-id");
				genericEditTemplate(templateId);
			}
		} else {
			MessageUtil.setMessage("Please choose Template To Edit", "color:red;");
			return;
		}
	}
	

	private void edit(Image currentImage) throws ParseException {
		if (currentImage != null && currentImage.getAttribute("data-id") != null) {
			Long templateId = (Long) currentImage.getAttribute("data-id");
			editAutoEmailData(templateId);
			isEdit = true;
		} else {
			MessageUtil.setMessage("Template Not Available", "color:red;");
			return;
		}
	}
	                
	private void editAutoEmailData(Long templateId) {
		
	boolean	isedit=true;
		createAutoEmailTabPanelId.setSelected(true);
		createAutoEmailTabPanelId.setLabel("Edit Message");
		CustomTemplates custTemp = customTemplateDao.findCustTemplateById(templateId);
		if(custTemp!= null) {
			try {
			updateIdValue.setValue(custTemp.getTemplateId().toString());
			if(updateIdValue.getValue()!= null && !updateIdValue.getValue().isEmpty()) {
				sessionScope.setAttribute("beeBackButtonAutoPoplate",Long.parseLong(updateIdValue.getValue()));
			}
		    custTempNameTbId.setValue(custTemp.getTemplateName());
			List<Listitem> categoryItems = typeOfAutoEmailListLbId.getItems();
			String typeOfAutoEmail = setAutoEmailListDisplayValue(custTemp.getType());
			for(Listitem listitem:categoryItems) {
				if(listitem.getLabel().equals(typeOfAutoEmail)) {
					typeOfAutoEmailListLbId.setSelectedItem(listitem);
					typeOfAutoEmailListLbId.setDisabled(true);
				};
			}
			
			if(custTemp.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_PLAIN)) {
				List<Listitem> folderItems = chooseTemplateFolderId.getItems();
				 for(Listitem listitemFolder:folderItems) {
					if( listitemFolder.getLabel().equalsIgnoreCase("Legacy Editor")) {
						folderItems.remove(listitemFolder.getIndex());
					}
				 }
				chooseTemplateId.getItems().clear();
				chooseTemplateFolderId.appendItem("Legacy Editor","Legacy Editor").setSelected(true);
				chooseTemplateId.appendItem("Select Template",Constants.STRING_NILL);
				chooseTemplateId.appendItem(custTemp.getTemplateName(),custTemp.getTemplateName()).setSelected(true);
				chooseTemplateId.getSelectedItem().setAttribute("data-id-ck", custTemp.getTemplateId());
				onSelect$chooseTemplateId();
			}else if(custTemp.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE)) {
				chooseTemplateFolderId.setDisabled(false);
				chooseTemplateId.setDisabled(false);
				 Long myTemplateId = custTemp.getMyTemplateId();
				 MyTemplates myTemplates  = myTemplatesDao.getTemplateByMytemplateIdandUserId(myTemplateId,currentUser.getUserId());
				 if(myTemplates != null) {
					 List<Listitem> folderItems = chooseTemplateFolderId.getItems();
					 for(Listitem listitemFolder:folderItems) {
						 if(listitemFolder != null && listitemFolder.getValue()!= null) {
							if( listitemFolder.getLabel().equalsIgnoreCase("Legacy Editor")) {
									folderItems.remove(listitemFolder.getIndex());
							}
							if(myTemplates.getFolderName().equals(listitemFolder.getLabel())) {
								chooseTemplateFolderId.setSelectedItem(listitemFolder);
							};
						}
					 }
					 Event e = new Event("onChange");
					 onSelect$chooseTemplateFolderId(e);
					 List<Listitem> folderTemplateItems = chooseTemplateId.getItems();
					 for(Listitem listitem:folderTemplateItems) {
							if(myTemplates.getName().equals(listitem.getValue())) {
								chooseTemplateId.setSelectedItem(listitem);
							};
						}
					 onSelect$chooseTemplateId();
				 }else {
					 chooseTemplateId.appendItem("Select Template", "").setSelected(true);
					 hideLinkFields();
					 tableDataWidth.setStyle("width:36%;");
				 }
			}
			
			cSubTb.setValue(custTemp.getSubject());
			cFromNameTb.setValue(custTemp.getFromName());
			List<Comboitem> items = cFromEmailCb.getItems();
			for(Comboitem comb:items) {
				if(comb.getLabel().equals(custTemp.getFromEmail())) {
					cFromEmailCb.setSelectedIndex(comb.getIndex());
				};
			}
			
			if(creplyToEmailCb!= null) {
				List<Comboitem> replyitems = creplyToEmailCb.getItems();
				for(Comboitem comb:replyitems) {
					if(comb.getLabel().equals(custTemp.getReplyToEmail())) {
						creplyToEmailCb.setSelectedIndex(comb.getIndex());
					};
				}
			}
			
			if(custTemp.getReplyToEmail()!=null) {
				creplyToEmailCb.setValue(custTemp.getReplyToEmail());
			}else {
				creplyToEmailCb.setValue(Constants.STRING_NILL);
			}
			
			orgNameChkBoxId.setChecked(custTemp.isIncludeOrg());
			orgUnitChkBoxId.setChecked(custTemp.isIncludeOrgUnit());
			
			setUserAddress(custTemp);
			loadSettings();
			if(!custTemp.getAddrType().equalsIgnoreCase("User") && custTemp.getAddrType().contains("|")) {
				addrRgId.setSelectedIndex(3);
				getStore(custTemp.getAddrType().split("\\|")[1].trim());
				onSelect$storesLbId();
			}else if(custTemp.getAddrType().equalsIgnoreCase(Constants.CAMP_ADDRESS_TYPE_CONTACT_HOME_STORE)) {
				addrRgId.setSelectedIndex(1);
			}else if(custTemp.getAddrType().equalsIgnoreCase(Constants.CAMP_ADDRESS_TYPE_CONTACT_LAST_PURCHASED_STORE)) {
				addrRgId.setSelectedIndex(2);
			}else {
				addrRgId.setSelectedIndex(0);
			}
			
			if(custTemp.isPersonalizeTo() || custTemp.isPermissionRemainderFlag()) {
				gbContentDivId.setVisible(true);
				if(custTemp.isPermissionRemainderFlag()) {
					cPermRemRb.setSelectedIndex(0);
					permRemTextId.setValue(custTemp.getPermissionRemainderText());
					permRemTextId.setVisible(true);
				}else {
					cPermRemRb.setSelectedIndex(1);
				}
				String[] phArray = {"firstName","lastName","fullName"};
				if(custTemp.isPersonalizeTo()){
					toNameChkId.setChecked(true);
					persToDivId.setVisible(true);
					String to = custTemp.getToName();
					for(int i=0;i< phArray.length;i++){
						if(phArray[i].equalsIgnoreCase(to)){
							phLbId.setSelectedIndex(i);
						}
					}
				}else{
					toNameChkId.setChecked(false);
					persToDivId.setVisible(false);
					
				}
			}else {
				gbContentDivId.setVisible(false);
			}
			customFooterId.setChecked(custTemp.isCustomizeFooter());
			downloadPdf.setChecked(custTemp.isDownloadPdf());
			onCheck$customFooterId();
			
			}catch (Exception e) {
				logger.error("::  Error in custom template edit ::" +e);
			}
		}else {
			Messagebox.show("Auto Email Not Found");
		}
		
	}

	private void genericEditTemplate(Long templateId) throws ParseException {
		MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateIdandUserId(templateId,currentUser.getUserId());
		if(myTemplates.getJsoncontent()!= null) {
			sessionScope.setAttribute("beeEditAutoEmail", myTemplates.getJsoncontent());
			sessionScope.setAttribute("editTemplateId", myTemplates.getMyTemplateId());
		}
		isEdit=true;
		onClick$createNewTemplateId();
		
	}
	
	private void redirectToCkEditor(Long templateId) {
		CustomTemplates myTemplate = customTemplateDao.findAllByUserIdAndTemplateId(currentUser.getUserId(), templateId);
		Sessions.getCurrent().setAttribute("autoEmailOldTemplateRedirect", myTemplate);
		 Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES_AUTOEMAIL);
	}
	
	/***************************************************DISPLAY TEMPLATE(PREVIEW)********************************************************/
	
	public void onClick$previewTemplateId() {
		previewTemplateListScreen= false;
		displayTemplate();
	}
	
	private Window previewIframeWin; 
	private  Iframe previewIframeWin$iframeId;
	public void displayTemplate() {
	
		Long templateId = null;
		MyTemplates myTemplates = null;
		String htmlContent = null;
		if(previewTemplateListScreen) {
			if(currentImageFromList!=null && currentImageFromList.hasAttribute("data-id-ck") && currentImageFromList.getAttribute("data-id-ck")!=null) {
				templateId =  (Long)currentImageFromList.getAttribute("data-id-ck");
				CustomTemplates customtemplate   =  customTemplateDao.findAllByUserIdAndTemplateId(currentUser.getUserId(),templateId);
				if(customtemplate.getHtmlText()!= null && !customtemplate.getHtmlText().isEmpty()) {
					htmlContent = customtemplate.getHtmlText();
					htmlContent = Utility.mergeTagsForPreviewAndTestMail(htmlContent,"preview");
					Utility.showPreview(previewIframeWin$iframeId,currentUser.getUserName(), htmlContent);
					previewIframeWin$iframeId.detach();
				}else {
					MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.", "color:red", "TOP");
					return;
				}
			}else if(currentImageFromList!= null && currentImageFromList.getAttribute("data-id")!= null) {
				templateId =  (Long)currentImageFromList.getAttribute("data-id");
				myTemplates = myTemplatesDao.getTemplateByMytemplateIdandUserId(templateId,currentUser.getUserId());
				if(myTemplates!=null && myTemplates.getContent()!= null && !myTemplates.getContent().isEmpty()) {
					htmlContent =  myTemplates.getContent();
					htmlContent = Utility.mergeTagsForPreviewAndTestMail(htmlContent,"preview");
					Utility.showPreview(previewIframeWin$iframeId,currentUser.getUserName(), htmlContent);
				}else {
					MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.", "color:red", "TOP");
					return;
				}
				currentImageFromList = null;
				sendTestMailListRequest= false;
			}else {
				MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.", "color:red", "TOP");
				return;
			}
		}else{
			if(chooseTemplateId.getSelectedItem() != null) {
				if(chooseTemplateId.getSelectedItem().hasAttribute("data-id-ck")) {
					templateId = (Long) chooseTemplateId.getSelectedItem().getAttribute("data-id-ck");
					CustomTemplates customtemplate   = customTemplateDao.findAllByUserIdAndTemplateId(currentUser.getUserId(), templateId);
					if(customtemplate.getHtmlText()!= null && !customtemplate.getHtmlText().isEmpty()) {
						htmlContent = customtemplate.getHtmlText();
						htmlContent = Utility.mergeTagsForPreviewAndTestMail(htmlContent,"preview");
						Utility.showPreview(previewIframeWin$iframeId,currentUser.getUserName(), htmlContent);
					}else {
						MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.", "color:red", "TOP");
						return;
					}
				}else {
					templateId = (Long) chooseTemplateId.getSelectedItem().getAttribute("data-id");
					myTemplates = myTemplatesDao.getTemplateByMytemplateIdandUserId(templateId,currentUser.getUserId());
					if(myTemplates !=null && myTemplates.getContent()!= null && !myTemplates.getContent().isEmpty()) {
						htmlContent =  myTemplates.getContent();
						htmlContent = Utility.mergeTagsForPreviewAndTestMail(htmlContent,"preview");
						Utility.showPreview(previewIframeWin$iframeId,currentUser.getUserName(), htmlContent);
					}else {
						MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.", "color:red", "TOP");
						return;
					}
				}
			}else {
				MessageUtil.setMessage("Please choose Template To preview","color:red;");
				return;
			}
			
		}
		//Utility.showPreview(previewIframeWin$iframeId,currentUser.getUserName(), htmlContent);
		previewIframeWin.doHighlighted();
		previewIframeWin.setVisible(true);
	}
	
	private void preview(Image currentImage) {
		currentImageFromList = currentImage;
		previewTemplateListScreen= true;
		displayTemplate();
	}
	
	/******************************************SEND TEST MAIL***********************************************/
	
  public void onClick$sendTestMailId() {
		if(chooseTemplateId.getSelectedItem()!= null) {
			testWinId$msgLblId.setValue("");
			testWinId.setVisible(false);
			testWinId$testTbId.setValue("");
			testWinId.setVisible(true);
			testWinId.setPosition("center");
			testWinId.doHighlighted();
		}else {
			MessageUtil.setMessage("Please choose Template To Send Test Mail","color:red;");
			return;
		}
	}
  
  public void sendMailpopUpListScreen() {
			testWinId$msgLblId.setValue("");
			testWinId.setVisible(false);
			testWinId$testTbId.setValue("");
			testWinId.setVisible(true);
			testWinId.setPosition("center");
			testWinId.doHighlighted();
	}


public void onClick$cancelSendTestMailBtnId$testWinId() {
	testWinId$msgLblId.setValue("");
	testWinId$msgLblId.setValue("");
	testWinId.setVisible(false);
}

	Image currentImageFromList = null;
	public void onClick$sendTestMailBtnId$testWinId() {		
		String emailId = testWinId$testTbId.getValue();
		boolean isValid = validateEmailAddr(emailId);
		MyTemplates myTemplates = null;
		String htmlContent = null;
		if(isValid){
			testWinId$sendTestMailBtnId.setDisabled(true);
			Long templateId = null;
			if(sendTestMailListRequest) {
				if(currentImageFromList.hasAttribute("data-id-ck")) {
					templateId =  (Long)currentImageFromList.getAttribute("data-id-ck");
					CustomTemplates customtemplate   =  customTemplateDao.findAllByUserIdAndTemplateId(currentUser.getUserId(),templateId);
					if(customtemplate != null && customtemplate.getHtmlText()!= null && !customtemplate.getHtmlText().isEmpty()) {
						htmlContent = customtemplate.getHtmlText();
					}else {
						MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.", "color:red", "TOP");
						onClick$cancelSendTestMailBtnId$testWinId();
						testWinId$sendTestMailBtnId.setDisabled(false);
						return;
					}
					
				}else{
					templateId =  (Long)currentImageFromList.getAttribute("data-id");
					myTemplates = sendTestMailGeneric(templateId);
					if(myTemplates == null) {
						MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.", "color:red", "TOP");
						onClick$cancelSendTestMailBtnId$testWinId();
						testWinId$sendTestMailBtnId.setDisabled(false);
						return;
					}
					
					htmlContent = myTemplates.getContent();
					currentImageFromList = null;
					sendTestMailListRequest= false;
				}
			}else{
				if(chooseTemplateId.getSelectedItem().hasAttribute("data-id-ck")) {
					templateId = (Long) chooseTemplateId.getSelectedItem().getAttribute("data-id-ck");
					CustomTemplates customtemplate   = customTemplateDao.findAllByUserIdAndTemplateId(currentUser.getUserId(), templateId);
					if(customtemplate!=null && customtemplate.getHtmlText()!= null && !customtemplate.getHtmlText().isEmpty()) {
						htmlContent =  customtemplate.getHtmlText();
					}else {
						return;
					}
				}else {
					templateId = (Long) chooseTemplateId.getSelectedItem().getAttribute("data-id");
					myTemplates = sendTestMailGeneric(templateId);
					if(myTemplates!=null && myTemplates.getContent()!=null && !myTemplates.getContent().isEmpty()) {
						htmlContent = myTemplates.getContent();
					}else {
						return;
					}
				}
			}
			htmlContent = Utility.mergeTagsForPreviewAndTestMail(htmlContent,"testMail");
			super.sendTestMail(htmlContent, emailId);
			testWinId.setVisible(false);
			testWinId$sendTestMailBtnId.setDisabled(false);
		}
	}
	
	
	private void doPopUp(Image currentImage) {
		//onClick$sendTestMailId();
		Long templateId = null;
		if(currentImage.hasAttribute("data-id-ck")) {
			templateId =  (Long)currentImage.getAttribute("data-id-ck");
			CustomTemplates customtemplate   =  customTemplateDao.findAllByUserIdAndTemplateId(currentUser.getUserId(),templateId);
			if(customtemplate == null || customtemplate.getHtmlText()== null || customtemplate.getHtmlText().isEmpty()) {
				MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.", "color:red", "TOP");
				onClick$cancelSendTestMailBtnId$testWinId();
				testWinId$sendTestMailBtnId.setDisabled(false);
				return;
			}
		}else{
			templateId =  (Long)currentImage.getAttribute("data-id");
			MyTemplates myTemplates = sendTestMailGeneric(templateId);
			if(myTemplates == null) {
				MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.", "color:red", "TOP");
				onClick$cancelSendTestMailBtnId$testWinId();
				testWinId$sendTestMailBtnId.setDisabled(false);
				return;
			}
			currentImageFromList = null;
			sendTestMailListRequest= false;
		}
		sendMailpopUpListScreen();
		sendTestMailListRequest=true;
		currentImageFromList = currentImage;
	}
	
	private MyTemplates sendTestMailGeneric(Long templateId) {
		MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateIdandUserId(templateId,currentUser.getUserId());
		return myTemplates;
	}
	
	public boolean validateEmailAddr(String emailId) {
		try {
			boolean isValid = true;			
			if(emailId != null && emailId.trim().length() > 0) {
				if(logger.isDebugEnabled())logger.debug("Sending the test mail....");
				MessageUtil.clearMessage();
				testWinId$msgLblId.setValue("");
				String[] emailArr = null;
				if(isValid) {
					emailArr = emailId.split(",");
					for (String email : emailArr) {
						if(!Utility.validateEmail(email.trim())){
							testWinId$msgLblId.setValue("Invalid Email address:'"+email+"'");
							isValid = false;
							break;
						}else {
							// to Check invalid email format - APP-308
							String result = PurgeList.checkForValidDomainByEmailId(email);
							if(!result.equalsIgnoreCase("Active")) {
								testWinId$msgLblId.setValue("Invalid Email address:'"+email+"'");
								isValid = false;
								break;
							}
						}
					}
				}

			}else {
				isValid = false;
				testWinId$msgLblId.setValue("Invalid Email address");
				testWinId$msgLblId.setVisible(true);
			}
			return isValid;
		}
		catch(Exception e) {
			logger.error("** Exception : " , e);
			return false;
		}

	}
	
	/**************************************Reset List************************************************/
	 public void onClick$resetAnchId() {
		    reset= true;
		 	autoEmailBEEListPaging.setActivePage(0);
			int count = Integer.parseInt(autoEmailPageSizeLbId.getSelectedItem().getLabel());
			autoEmailGridId.setModel(getCustomTemplateModel(0,count,null,null,null));
			autoEmailGridId.setRowRenderer(rowRender);
			autoEmailBEEListPaging.setPageSize(count);
			srchLbId.setVisible(true);
			selectSearchItemId.setSelectedIndex(0);
			srchLbId.setSelectedIndex(0);
			createdDateId.setVisible(false);
			fromDateboxId.setText(null);
			toDateboxId.setText(null);
	 }

	 
	 /*********************************** delete AutoEmail ****************************************/
	 
	 private void delete(Image currentImage) {
		if (currentImage != null && currentImage.getAttribute("data-id") != null) {
			Long templateId = (Long) currentImage.getAttribute("data-id");
			CustomTemplates custTemp = customTemplateDao.findCustTemplateById(templateId);
			if(custTemp!=null && Messagebox.show("This auto-email message may be configured to a list/web-form/loyalty program. Deleting it may cause auto-communication to stop working.\n" + 
					"Are you sure you want to continue?", "Delete?",
					Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.YES) {
				   try {
				    customTemplateDaoForDML.deleteFormById(custTemp.getTemplateId());
				   }catch (Exception e) {
					   MessageUtil.setMessage("Sorry! Looks like there are delivery reports generated for this auto-email message and hence it can't be deleted.", "color:red;");
					   return;
				   }
				    getAutoEmailList();
			} else {
				return;
			}
		}else {
			MessageUtil.setMessage("No Record Found To Delete", "color:red;");			
		}
	 }
	 
	 /*************************************generic methods****************************************************/
	 
	 private String setAutoEmailListDisplayValue(String dbListValue) {
			String value = null;
			if (dbListValue.equals(DOUBLEOPTIN)) {
				value =  Constants.CUSTOM_TEMPLATE_TYPE_DOUBLEOPTIN;
			} else if (dbListValue.equals(PARENTALCONSENT)) {
				value = Constants.CUSTOM_TEMPLATE_TYPE_PARENTALCONSENT ;
			} else if (dbListValue.equals(LOYALTYOPTIN)) {
				value = Constants.CUSTOM_TEMPLATE_TYPE_LOYALTYOPTIN;
			} else if (dbListValue.equals(WEBFORMEMAIL)) {
				value = Constants.CUSTOM_TEMPLATE_TYPE_WEBFORMEMAIL;
			} else if (dbListValue.equals(TIERUPGRADATION )) {
				value = Constants.CUSTOM_TEMPLATE_TYPE_TIER_UPGRADATION;
			} else if (dbListValue.equals(EARNEDREWARDEXPIRATION)) {
				value = Constants.CUSTOM_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION ;
			} else if (dbListValue.equals(MEMBERSHIPEXPIRATION)) {
				value = Constants.CUSTOM_TEMPLATE_TYPE_MEMBERSHIP_EXPIRATION;
			} else if (dbListValue.equals(EARNEDBONUS)) {
				value = Constants.CUSTOM_TEMPLATE_TYPE_EARNED_BONUS;
			} else if (dbListValue.equals(GIFTAMOUNTEXPIRATION )) {
				value = Constants.CUSTOM_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION;
			} else if (dbListValue.equals(GIFTCARDEXPIRATION)) {
				value = Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION;
			}  else if (dbListValue.equals(GIFTCARDISSUANCE)) {
				value = Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE;
			} else if (dbListValue.equals(FEEDBACKFORM)) {
				value = Constants.CUSTOM_TEMPLATE_TYPE_FEEDBACK_FORM;
			} else if (dbListValue.equals(SPECIALREWARDS)) {
				value = Constants.CUSTOM_TEMPLATE_TYPE_SPECIAL_REWARDS;
			} else if (dbListValue.equals(LOYALTYADJUSTMENT)) {
				value = Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_ADJUSTMENT;
			} else if (dbListValue.equals(LOYALTYISSUANCE)) {
				value = Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_ISSUANCE;
			} else if (dbListValue.equals(LOYALTYREDEMPTION)) {
				value = Constants.CUSTOM_TEMPLATE_TYPE_LOYALTY_REDEMPTION;
			} else if (dbListValue.equals(OTPMESSAGES)) {
				value = Constants.CUSTOM_TEMPLATE_TYPE_OTP_MESSAGE;                                   
			} else if (dbListValue.equals(REDEMPTIONOTP)) {
				value = Constants.CUSTOM_TEMPLATE_TYPE_REDEMPTION_OTP;                                   
			}
			
			
			return value;
			}
	 
	 
	 /*****************************************Close Edit Address In Add AutoEmail******************************************************/
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
	 
	 public void onClick$doneAnchId() {
			try {
				setAddress(true);
			} catch (Exception e) {
				logger.error("Exception ::", e);
			}
		}
	 
	 public void onSelect$selectSearchItemId() {
		 if(selectSearchItemId.getSelectedIndex() == 0) {
			 srchLbId.setVisible(true);
			 createdDateId.setVisible(false);
			 fromDateboxId.setText(null);
			 toDateboxId.setText(null);
		 }else if(selectSearchItemId.getSelectedIndex() == 1) {
			 srchLbId.setVisible(false);
			 createdDateId.setVisible(true);
		 }
		 
	 }
	 
	 
	 /*********************************Merge Tags********************************************/
	 
	 public static String getPlaceHolderListAutoEmail(Set<MailingList> mlistSet, boolean ignoreUnSubAndWebLink, Listbox phLbId) {
			
			try {

				MLCustomFieldsDao mlCustomFieldsDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
				
				List<String> placeHoldersList = new ArrayList<String>(); 
				
				POSMappingDao posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
				List<POSMapping> contactsGENList = posMappingDao.findOnlyByGenType(Constants.POS_MAPPING_TYPE_CONTACTS, GetUser.getUserId() );
				
				
				
				
				placeHoldersList.addAll(Constants.PLACEHOLDERS_LIST);
				Users user = GetUser.getUserObj();
				
			
				
				if(user.getloyaltyServicetype() != null && user.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_SB) )
				{
					placeHoldersList.removeIf(e -> e.contains("Loyalty Gift Balance"));
				}
				if(user.getloyaltyServicetype() != null && user.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_OC) )
				{
					placeHoldersList.removeIf(e -> e.contains("Loyalty Membership Pin"));
				}
				
				if(user.getloyaltyServicetype() != null && !user.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_SB) )
				{
					placeHoldersList.addAll(Constants.OCPLACEHOLDERS_LIST);
				}

				Map<String , String> StoreDefaultPHValues = EditorController.getDefaultStorePhValue(placeHoldersList);
				
				List<POSMapping> contactsUDFList = posMappingDao.findOnlyByType(Constants.POS_MAPPING_TYPE_CONTACTS, GetUser.getUserId() );
				
				if(contactsUDFList != null) {
					
					for (POSMapping posMapping : contactsUDFList) {
						
						String udfStr;
						if(posMapping.getDefaultPhValue()==null || posMapping.getDefaultPhValue().trim().isEmpty()) {

							udfStr = Constants.UDF_TOKEN + posMapping.getDisplayLabel() +
									Constants.DELIMETER_DOUBLECOLON +
									Constants.CF_START_TAG + Constants.UDF_TOKEN +
									posMapping.getCustomFieldName()  + Constants.DELIMETER_SPACE+ Constants.DELIMETER_SLASH + Constants.DELIMETER_SPACE+Constants.DEFUALT_TOKEN+Constants.CF_END_TAG ;


						}
						else {
							udfStr = Constants.UDF_TOKEN + posMapping.getDisplayLabel() +
									Constants.DELIMETER_DOUBLECOLON +
									Constants.CF_START_TAG + Constants.UDF_TOKEN +
									posMapping.getCustomFieldName()+ Constants.DELIMETER_SPACE + Constants.DELIMETER_SLASH + Constants.DELIMETER_SPACE + Constants.DEFUALT_TOKEN + posMapping.getDefaultPhValue() + Constants.CF_END_TAG ;
					
						
						}
						placeHoldersList.add(udfStr);
					}//for
					
					
					
				}//if
				
				//END
				if(mlistSet != null) {
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
				}
				
				boolean isadded = false;
				Set<String> retList = new HashSet<String>();
				StringBuffer buffer = new StringBuffer();

				for (String placeHolder : placeHoldersList) {
					if(placeHolder.trim().startsWith("--") || placeHolder.toLowerCase().contains(("place holder"))) { //Ignore
						continue;
					}
					if(ignoreUnSubAndWebLink) {
						
						if(placeHolder.startsWith("Unsubscribe Link") || placeHolder.startsWith("Web-Page Version Link") ||
								placeHolder.startsWith("Share on Twitter") || placeHolder.startsWith("Share on Facebook") ||
								placeHolder.startsWith("Forward To Friend") ||placeHolder.startsWith("Subscriber Preference Link") ){
							retList.add(placeHolder);
							isadded = true;
							continue;
						}
						
					}
					if(placeHolder.contains(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LOYALTY) 
							|| placeHolder.contains(PlaceHolders.CAMPAIGN_PH_LASTPURCHASE_DATE)
							|| placeHolder.contains(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LASETPURCHASE)
							|| placeHolder.contains(PlaceHolders.CAMPAIGN_PH_STORENAME)){
						retList.add(placeHolder);
						isadded = true;
					}
					String[] phTokenArr =  placeHolder.split("::"); 
					String key = phTokenArr[0];
					StringBuffer value = new StringBuffer(phTokenArr[1]);
					if(StoreDefaultPHValues.containsKey(placeHolder)) {
						
						value.insert(value.lastIndexOf("^"), StoreDefaultPHValues.get(placeHolder));
						placeHolder = placeHolder.replace(phTokenArr[1], value.toString());
						retList.add(placeHolder);
						isadded = true;
						
					}
					for (POSMapping posMapping : contactsGENList) {
						
						if(!key.equalsIgnoreCase(posMapping.getCustomFieldName()) ) continue;
						
						
						
						if(posMapping.getDefaultPhValue() == null || posMapping.getDefaultPhValue().isEmpty() ){
							retList.add(placeHolder);
							isadded = true;
							break;
						}
						
						
						value.insert(value.lastIndexOf("^"), posMapping.getDefaultPhValue() );
						placeHolder = placeHolder.replace(phTokenArr[1], value.toString());
						retList.add(placeHolder);
						isadded = true;
						
					}
					
					if(!isadded) {
						retList.add(placeHolder);
						
					}
					
					
					if(phLbId != null) {
						Listitem item =  new Listitem(key, value.toString());
						item.setParent(phLbId);
						
						
					}//if
					else{
						placeHolder = StringEscapeUtils.escapeJavaScript(placeHolder);
					}
					
					if(buffer.length() > 0) buffer.append(",");
					key=StringEscapeUtils.escapeJavaScript(key);
					value = new StringBuffer(StringEscapeUtils.escapeJavaScript(value.toString()));
					
					buffer.append("{name: '" +key+ "', value: '"+value+"'}"); 

					
				} 
				
				String str1=buffer.toString();
				logger.debug("-- Exit --");
				return str1;
			}
			catch (Exception e) {
				logger.error("Exception ::" , e);;
				return null;
			}
		}	
	 
/*********************************Merge Tags FOR OTP ********************************************/
	 public static String getPlaceHolderListAutoEmailOTP(Set<MailingList> mlistSet, boolean ignoreUnSubAndWebLink, Listbox phLbId) {
			
			try {

				MLCustomFieldsDao mlCustomFieldsDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
				
				List<String> placeHoldersList = new ArrayList<String>(); 
				
				POSMappingDao posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
				List<POSMapping> contactsGENList = posMappingDao.findOnlyByGenType(Constants.POS_MAPPING_TYPE_CONTACTS, GetUser.getUserId() );
				
				
				
				
				placeHoldersList.addAll(Constants.OTP_TAGS);
				Users user = GetUser.getUserObj();
				
			Set<String> retList = new HashSet<String>();
				StringBuffer buffer = new StringBuffer();

				for (String placeHolder : placeHoldersList) {
				
					if(placeHolder.trim().startsWith("--") || placeHolder.toLowerCase().contains(("select"))) { //Ignore
						continue;
					}
				
				
					String[] phTokenArr =  placeHolder.split("::"); 
					String key = phTokenArr[0];
					StringBuffer value = new StringBuffer(phTokenArr[1]);
					if(phLbId != null) {
						Listitem item =  new Listitem(key, value.toString());
						item.setParent(phLbId);
						
						
					}//if
					else{
						placeHolder = StringEscapeUtils.escapeJavaScript(placeHolder);
					}
					
					if(buffer.length() > 0) buffer.append(",");
					key=StringEscapeUtils.escapeJavaScript(key);
					buffer.append("{name: '" +key+ "', value: '"+value+"'}"); 

					
				} 
				
				String str1=buffer.toString();
				logger.debug("-- Exit --");
				return str1;
			}
			catch (Exception e) {
				logger.error("Exception ::" , e);;
				return null;
			}
		}	
	 public void onClick$autoEmailListId(){
		 createAutoEmailTabPanelId.setLabel("Create New Message");
		 currentUser = GetUser.getUserObj();
		 defaultFolderList();
		 Sessions.getCurrent().removeAttribute("beeBackButtonAutoPoplate");
		 Sessions.getCurrent().removeAttribute("beeBackButtonAutoPoplateInCreateTemplate");
		 custTempNameTbId.setValue("");
		 typeOfAutoEmailListLbId.setDisabled(false);
		 typeOfAutoEmailListLbId.setSelectedIndex(0);
		 chooseTemplateFolderId.setSelectedIndex(0);
		 chooseTemplateId.getItems().clear();
		 chooseTemplateId.appendItem("Select Template", "").setSelected(true);
		 if(chooseTemplateFolderId.isDisabled()) {
			 chooseTemplateFolderId.getItems().forEach(listFilter ->{
				if(listFilter.getLabel().equalsIgnoreCase("legacy Editor")) {
					chooseTemplateFolderId.removeItemAt(listFilter.getIndex());
				} 
			 });
			 
			 chooseTemplateFolderId.setDisabled(false);
		 }
		 if(chooseTemplateId.isDisabled()) {
			 chooseTemplateId.setDisabled(false);
		 }
		 cSubTb.setValue("");
		 cFromNameTb.setValue("");
		 cFromEmailCb.setSelectedIndex(0);
		 creplyToEmailCb.setSelectedIndex(0);
		 addrRgId.setSelectedIndex(0);
		 orgNameChkBoxId.setChecked(false);
		 orgUnitChkBoxId.setChecked(false);
		 hideLinkFields();
		 tableDataWidth.setStyle("width:36%;");
		 updateIdValue.setValue(Constants.STRING_NILL);
	 }
	 
	 public void onClick$createAutoEmailTabPanelId() {
		 onClick$autoEmailListId();
	 }
	 
	 
	 public void onClick$optSettingsTbBtnId() {
			gbContentDivId.setVisible(!gbContentDivId.isVisible());
			String image = gbContentDivId.isVisible() ? "/img/icons/icon_minus.png" : "/img/icons/icon_plus.png";
			optSettingsTbBtnId.setImage(image);
		}
	 
	 private Boolean checkAutoEmailFieldsContainValue() {
		 boolean returnValue = false;
		 if(custTempNameTbId.getValue()!= null && !custTempNameTbId.getValue().isEmpty()) {
			 returnValue = true;
		 }else if (typeOfAutoEmailListLbId.getSelectedIndex() > 0) {
			 returnValue = true;
		 }else if(cSubTb.getValue()!= null && !cSubTb.getValue().isEmpty()) {
			 returnValue = true;
		 }else if(cFromNameTb.getValue()!= null && !cFromNameTb.getValue().isEmpty()) {
			 returnValue = true;
		 }else if (addrRgId.getSelectedIndex() > 0) {
			 returnValue = true;
		 }
		if (orgNameChkBoxId != null) {
			if (orgNameChkBoxId.isChecked()) {
				returnValue = true;
			}
		}
		if (orgUnitChkBoxId != null) {
			if (orgUnitChkBoxId.isChecked()) {
				returnValue = true;
			}
		}
		if(cAddressOneTbId!=null && currentUser.getAddressOne()!=null) {
			if(!currentUser.getAddressOne().equalsIgnoreCase(cAddressOneTbId.getValue())) {
				returnValue = true;
			}
		}
		if(cAddressTwoTbId!=null && currentUser.getAddressTwo()!=null) {
			if(!currentUser.getAddressTwo().equalsIgnoreCase(cAddressTwoTbId.getValue())) {
				returnValue = true;
			}
		}
		if(cCityTbId!=null && currentUser.getCity()!=null) {
			if(!currentUser.getCity().equalsIgnoreCase(cCityTbId.getValue())) {
				returnValue = true;
		}
		if(cStateTbId!=null && currentUser.getState()!=null) {
			if(!currentUser.getState().equalsIgnoreCase(cStateTbId.getValue())) {
				returnValue = true;
			}
		}
		if(cCountryTbId!=null && currentUser.getCountry()!=null) {
			if(!currentUser.getCountry().equalsIgnoreCase(cCountryTbId.getValue())) {
				returnValue = true;
			}
		}
		if(cPinLbId!=null && currentUser.getPinCode()!=null) {
			if(!currentUser.getPinCode().equalsIgnoreCase(cPinLbId.getValue())) {
				returnValue = true;
			}
		}
		if(cPhoneTbId!=null && currentUser.getPhone()!=null) {
			if(!currentUser.getPhone().equalsIgnoreCase(cPhoneTbId.getValue())) {
				returnValue = true;
			}
		}
	}
	return returnValue;
 }
	 
	 
	 /****************************************Register Email Address **************************************/
	 
	 public void onClick$regRepToEmlAnchId() {
			regEmailPopupId.setAttribute("flagStr", "Reply-Email");
		}
		
		
	public void onClick$regFrmEmlAnchId() {
			regEmailPopupId.setAttribute("flagStr", "From-Email");
		}
	 
	 public void onClick$registerSubmitBtnId() {
			try {
				registerNewFromEmail(cFromEmailTb,(String)regEmailPopupId.getAttribute("flagStr"));
			} catch (Exception e) {
				logger.error("Exception ::", e);
			}
		}

	private void registerNewFromEmail(Textbox cFromEmailTb,String flagStr) {

		String newFromEmail = cFromEmailTb.getValue();
		
		if(newFromEmail.trim().equals("")){
			regEmailPopupId.close();
			MessageUtil.setMessage("Email field cannot be left empty.", "color:red", "TOP");
			return;
		}
		
		if(!Utility.validateEmail(newFromEmail.trim())) {
			regEmailPopupId.close();
			MessageUtil.setMessage("Please enter a valid email address.", "color:red", "TOP");
			cFromEmailTb.setValue("");
			return;
	 	}
		
		try {
			if(newFromEmail.equalsIgnoreCase(currentUser.getEmailId())) {
				MessageUtil.setMessage("Given email address already exists.", "color:red", "TOP");
				cFromEmailTb.setValue("");
				return;
			}
			UserFromEmailIdDao userFromEmailIdDao = (UserFromEmailIdDao)SpringUtil.getBean("userFromEmailIdDao");
			UserFromEmailId userFromEmailId =  userFromEmailIdDao.checkEmailId(newFromEmail, currentUser.getUserId());
			String confirmationURL = PropertyUtil.getPropertyValue("confirmationURL") + "?requestedAction=fromEmail&userId=" + currentUser.getUserId() + "&email=" + newFromEmail;
			if(userFromEmailId == null) {
				userFromEmailId = new UserFromEmailId(currentUser, newFromEmail, 0);
				//userFromEmailIdDao.saveOrUpdate(userFromEmailId);
				userFromEmailIdDaoForDML.saveOrUpdate(userFromEmailId);

				addVerificationMailToQueue(flagStr, confirmationURL, newFromEmail);
			} else if(userFromEmailId.getStatus() == 0) {
				
				 try {
					int confirm = Messagebox.show("The given email address is pending for approval. Do you want to resend the verification?","Send Verification ?",
						 		Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != 1) {
						regEmailPopupId.close();
						cFromEmailTb.setValue("");
						return ;
					}
				} catch (Exception e) {
					logger.error("Exception ::", e);
				}
				
				addVerificationMailToQueue(flagStr, confirmationURL, newFromEmail);
			} 
			else {
				regEmailPopupId.close();
				MessageUtil.setMessage("Given email address already exists.", "color:red", "TOP");
				cFromEmailTb.setValue("");
			}
		} catch (Exception e) {
			regEmailPopupId.close();
			logger.error("Exception :"+e);
		}

	}

	private void addVerificationMailToQueue(String flagStr, String confirmationURL, String newFromEmail) {
		try {
			String branding = GetUser.getUserObj().getUserOrganization().getBranding();

			String brandStr="OptCulture";
			if(branding!=null && branding.equalsIgnoreCase("CAP")) {
				brandStr="Captiway";
			}
			
			String emailStr = "Hi " + currentUser.getFirstName() + ",<br/> You recently added a new email address  to your "+flagStr+".<br/>" 
			+ " To verify that you own this email address, simply click on the link below :<br/> " 
			+ "  <a href='"+ confirmationURL +"'>"+ confirmationURL + "</a>" 
			+ "<br/><br/> If you can't click the link, you can verify your email address by copy/paste(or typing) "
			+ "the above URL address into the browser.<br/>Regards,<br/>The "+brandStr+" Team";
			
			Utility.sendInstantMail(null,brandStr+" - Register new "+flagStr, emailStr,
					Constants.EQ_TYPE_USER_MAIL_VERIFY,newFromEmail, null);
			regEmailPopupId.close();
			MessageUtil.clearMessage();
			Messagebox.show("We have sent you a confirmation email to "+newFromEmail+".\n " +
			"Follow the instructions in the email and this new "+flagStr+" will be verified.", "Register email", Messagebox.OK, Messagebox.INFORMATION);
			cFromEmailTb.setValue("");
		} catch (WrongValueException e) {
			logger.error("Exception ::", e);
		}catch (Exception e) {
		logger.error("** Exception while sending verification",e);
		}
	}
	
	/****************************************Row sorting************************************************/
	public String orderby_colName,desc_Asc="desc";
	public void onClick$sortbyTemplateDate() {
		orderby_colName = "modified_date";
		desc2ascasc2desc();	
		onClick$filterBtnId();
		autoEmailGridId.setRowRenderer(rowRender);
		autoEmailGridId.setModel(getCustomTemplateModel(0,Integer.parseInt(autoEmailPageSizeLbId.getSelectedItem().getLabel()),searchMap,orderby_colName,desc_Asc));
	}
	
	public void onClick$sortbyTemplateCreatedDate() {
		orderby_colName = "created_date";
		desc2ascasc2desc();	
		onClick$filterBtnId();
		autoEmailGridId.setRowRenderer(rowRender);
		autoEmailGridId.setModel(getCustomTemplateModel(0,Integer.parseInt(autoEmailPageSizeLbId.getSelectedItem().getLabel()),searchMap,orderby_colName,desc_Asc));
	}
	
	 public void desc2ascasc2desc()
	    {
	    	if(desc_Asc=="desc")
				desc_Asc="asc";
			else
				desc_Asc="desc";
		
	    }
	
	 public Calendar getStartDate() {
			Calendar serverFromDateCal = fromDateboxId.getServerValue();
			Calendar tempClientFromCal = fromDateboxId.getClientValue();
			serverFromDateCal.set(
					Calendar.HOUR_OF_DAY,
					serverFromDateCal.get(Calendar.HOUR_OF_DAY)
							- tempClientFromCal.get(Calendar.HOUR_OF_DAY));
			serverFromDateCal.set(
					Calendar.MINUTE,
					serverFromDateCal.get(Calendar.MINUTE)
							- tempClientFromCal.get(Calendar.MINUTE));
			serverFromDateCal.set(Calendar.SECOND, 0);
			return serverFromDateCal;

		}

		public Calendar getEndDate() {

			Calendar serverToDateCal = toDateboxId.getServerValue();

			Calendar tempClientToCal = toDateboxId.getClientValue();

			serverToDateCal.set(Calendar.HOUR_OF_DAY,
					23 + serverToDateCal.get(Calendar.HOUR_OF_DAY)
							- tempClientToCal.get(Calendar.HOUR_OF_DAY));
			serverToDateCal.set(
					Calendar.MINUTE,
					59 + serverToDateCal.get(Calendar.MINUTE)
							- tempClientToCal.get(Calendar.MINUTE));
			serverToDateCal.set(Calendar.SECOND, 59);

			@SuppressWarnings("unused")
			String endDate = MyCalendar.calendarToString(serverToDateCal,
					MyCalendar.FORMAT_DATETIME_STYEAR);

			return serverToDateCal;

		}
		
		public void onClick$cancelBtnId() {
			cFromEmailTb.setValue(Constants.STRING_NILL);
			regEmailPopupId.close();
			
		}
		
		public boolean checkforchangesFormDBData(){
			Boolean returnIsChanged = false;
			if(updateIdValue!=null && updateIdValue.getValue()!=null && !updateIdValue.getValue().isEmpty()) {
				CustomTemplates customTemplate =  customTemplateDao.findCustTemplateById(Long.parseLong(updateIdValue.getValue()));
				if(customTemplate!=null){
				 if(custTempNameTbId!=null && custTempNameTbId.getValue()!=null) {	
					if(!customTemplate.getTemplateName().equalsIgnoreCase(custTempNameTbId.getValue())) {
						returnIsChanged = true;
						return returnIsChanged;
					}
				 }
				 if(cSubTb!=null && cSubTb.getValue()!=null) { 
					if(!customTemplate.getSubject().equalsIgnoreCase(cSubTb.getValue())) {
						returnIsChanged = true;
						return returnIsChanged;
					}
				 }
				 if(cFromNameTb!=null && cFromNameTb.getValue()!=null) { 
					if(!customTemplate.getFromName().equalsIgnoreCase(cFromNameTb.getValue())) {
						returnIsChanged = true;
						return returnIsChanged;
					}
				 }
				if(cFromEmailCb!=null && cFromEmailCb.getSelectedItem()!=null && cFromEmailCb.getSelectedItem().getLabel()!=null) { 
					if(!customTemplate.getFromEmail().equalsIgnoreCase(cFromEmailCb.getSelectedItem().getLabel())) {
						returnIsChanged = true;
						return returnIsChanged;
					}
				}
				if(creplyToEmailCb!=null && creplyToEmailCb.getSelectedItem()!=null && creplyToEmailCb.getSelectedItem().getLabel()!=null) {
					if(!customTemplate.getReplyToEmail().equalsIgnoreCase(creplyToEmailCb.getSelectedItem().getLabel())) {
						returnIsChanged = true;
						return returnIsChanged;
					}
				}
				if(postalAddressRbId!=null && postalAddressRbId.getValue()!=null) {
						if(customTemplate.getAddress()!= postalAddressRbId.getValue()) {
							returnIsChanged = true;
							return returnIsChanged;
						}
					}
				}
				if(orgNameChkBoxId!=null) {
					if(customTemplate.isIncludeOrg() != (orgNameChkBoxId.isChecked())) {
						returnIsChanged = true;
						return returnIsChanged;
					}
				}
				if(orgUnitChkBoxId!=null) {
					if(customTemplate.isIncludeOrgUnit() != (orgUnitChkBoxId.isChecked())) {
						returnIsChanged = true;
						return returnIsChanged;
					}
				}
				
				if(cAddressOneTbId!=null) {
					if(!currentUser.getAddressOne().equalsIgnoreCase(cAddressOneTbId.getValue())) {
						returnIsChanged = true;
						return returnIsChanged;
					}
				}
				if(cAddressTwoTbId!=null) {
					if(!currentUser.getAddressTwo().equalsIgnoreCase(cAddressTwoTbId.getValue())) {
						returnIsChanged = true;
						return returnIsChanged;
					}
				}
				if(cCityTbId!=null) {
					if(!currentUser.getCity().equalsIgnoreCase(cCityTbId.getValue())) {
						returnIsChanged = true;
						return returnIsChanged;
					}
				}
				if(cStateTbId!=null) {
					if(!currentUser.getState().equalsIgnoreCase(cStateTbId.getValue())) {
						returnIsChanged = true;
						return returnIsChanged;
					}
				}
				if(cCountryTbId!=null) {
					if(!currentUser.getCountry().equalsIgnoreCase(cCountryTbId.getValue())) {
						returnIsChanged = true;
						return returnIsChanged;
					}
				}
				if(cPinLbId!=null) {
					if(!currentUser.getPinCode().equalsIgnoreCase(cPinLbId.getValue())) {
						returnIsChanged = true;
						return returnIsChanged;
					}
				}
				if(cPhoneTbId!=null && currentUser.getPhone()!=null) {
					if(!currentUser.getPhone().equalsIgnoreCase(cPhoneTbId.getValue())) {
						returnIsChanged = true;
						return returnIsChanged;
					}
				}
				
				if(gbContentDivId.isVisible()) {
					int permissionFlag = customTemplate.isPermissionRemainderFlag() ? 1 : 0;
					if(cPermRemRb!=null && cPermRemRb.getSelectedIndex() > 0) {
						if(permissionFlag != cPermRemRb.getSelectedIndex()) {
							returnIsChanged = true;
							return returnIsChanged;
						}
					}
					if(permRemTextId!=null && permRemTextId.getValue() !=null) {
						if(!permRemTextId.getValue().equalsIgnoreCase("You are receiving this email because you had opted in on our website.")) {
							if(customTemplate.getPermissionRemainderText()==null && permRemTextId.getValue()!=null) {
								returnIsChanged = true;
								return returnIsChanged;
							}else if(!customTemplate.getPermissionRemainderText().equalsIgnoreCase(permRemTextId.getValue())) {
								returnIsChanged = true;
								return returnIsChanged;
							}
						}
					}
					if(toNameChkId!=null) {
						if(customTemplate.isPersonalizeTo() != toNameChkId.isChecked()) {
							returnIsChanged = true;
							return returnIsChanged;
						}
					}
					if(persToDivId.isVisible()) {
						if(phLbId!=null && phLbId.getSelectedItem()!=null && phLbId.getSelectedItem().getValue()!=null) {
							if(customTemplate.getToName() != phLbId.getSelectedItem().getValue()) {
								returnIsChanged = true;
								return returnIsChanged;
							}
						}
					}
					}
				}
			return returnIsChanged;
		}
		
		public void onCheck$customFooterId() {
			if(customFooterId.isChecked()) {
				postalAddressRbId.setDisabled(true);
				homeStoreAddressDetailsId.setDisabled(true);
				lastPurchaseStoreId.setDisabled(true);
				storeAddressId.setDisabled(true);
				orgNameChkBoxId.setDisabled(true);
				orgUnitChkBoxId.setDisabled(true);
				storesLbId.setDisabled(true);
				modifySenderAddrId.setDisabled(true);
			}else {
				postalAddressRbId.setDisabled(false);
				homeStoreAddressDetailsId.setDisabled(false);
				lastPurchaseStoreId.setDisabled(false);
				storeAddressId.setDisabled(false);
				orgNameChkBoxId.setDisabled(false);
				orgUnitChkBoxId.setDisabled(false);
				storesLbId.setDisabled(false);
				modifySenderAddrId.setDisabled(false);
			}
		}
		
		
		public void getCouponsListAutoEmail() {
			try {
				List<String> imageCouponsPhList = new ArrayList<String>();
				List<String> imageCouponsPhBeeList = new ArrayList<String>();
				CouponsDao couponsDao =  (CouponsDao)SpringUtil.getBean("couponsDao");
				//List<Coupons> couponList = couponsDao.findCouponsByOrgId(GetUser.getUserObj().getUserOrganization().getUserOrgId());
				List<Coupons> couponList = couponsDao.findActiveAndRunningCouponsbyOrgId(GetUser.getUserObj().getUserOrganization().getUserOrgId(),Constants.STRING_NILL,Constants.STRING_NILL);
				
				//List<Coupons> couponList = couponsDao.findCouponsByUserId(GetUser.getUserId());
				if(couponList!=null) {

					String imagecouponStr="";
					String imagecouponBeeStr="";
					for (Coupons coupon : couponList) {
						
						if(coupon.getEnableBarcode() && coupon.getBarcodeType() != null && coupon.getBarcodeWidth() != null
								&& coupon.getBarcodeHeight() != null){
							
							String couponIdStr = Constants.CC_TOKEN + coupon.getCouponId() +"_"+coupon.getCouponName()+ 	
									"_"+coupon.getBarcodeType()+"_"+coupon.getBarcodeWidth()
									+"_"+coupon.getBarcodeHeight();

							//generate image dynamically and set
							//generate barcode image
							BitMatrix bitMatrix = null;
							
							String COUPON_CODE_URL = null;
							//String COUPON_CODE_URL_IMG = null;
							String ccPreviewUrl = null;
							
							String message = "Test:"+coupon.getCouponName();
							String barcodeType = coupon.getBarcodeType().trim();
							int width = coupon.getBarcodeWidth().intValue();
							int height = coupon.getBarcodeHeight().intValue();
							
							if(barcodeType.equals(Constants.COUP_BARCODE_QR)){
								
								bitMatrix = new QRCodeWriter().encode(message, BarcodeFormat.QR_CODE, width, height,null);
								String bcqrImg = GetUser.getUserName()+File.separator+
										"Preview"+File.separator+"QRCODE"+File.separator+couponIdStr+".png";
								
								COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcqrImg;
								//COUPON_CODE_URL_IMG = "/subscriber/UserData"+File.separator+bcqrImg;
								ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcqrImg;
							}
							else if(barcodeType.equals(Constants.COUP_BARCODE_AZTEC)){
								
								bitMatrix = new AztecWriter().encode(message, BarcodeFormat.AZTEC, width, height);
								String bcazImg = GetUser.getUserName()+File.separator+
										"Preview"+File.separator+"AZTEC"+File.separator+couponIdStr+".png";
								
								COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcazImg;
								
								ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcazImg;
							}
							else if(barcodeType.equals(Constants.COUP_BARCODE_LINEAR)){
								
								bitMatrix = new Code128Writer().encode(message, BarcodeFormat.CODE_128, width, height,null);
								String bclnImg = GetUser.getUserName()+File.separator+
										"Preview"+File.separator+"LINEAR"+File.separator+couponIdStr+".png";
								
								COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bclnImg;
								
								ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bclnImg;
							}
							else if(barcodeType.equals(Constants.COUP_BARCODE_DATAMATRIX)){
								
								bitMatrix = new DataMatrixWriter().encode(message, BarcodeFormat.DATA_MATRIX, width, height,null);
								String bcdmImg = GetUser.getUserName()+File.separator+
										"Preview"+File.separator+"DATAMATRIX"+File.separator+couponIdStr+".png";
								
								COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcdmImg;
								
								ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcdmImg;
							}
							
							/*if(bitMatrix == null){
								return;
							}*/
							File myTemplateFile = new File(COUPON_CODE_URL);
							File parentDir = myTemplateFile.getParentFile();
							if(!parentDir.exists()) {

								parentDir.mkdir();
							}

							if(!myTemplateFile.exists()) {
								
								MatrixToImageWriter.writeToStream(bitMatrix, "png", new FileOutputStream(
										new File(COUPON_CODE_URL)));	
							}
								
							imagecouponStr = coupon.getCouponName() + Constants.DELIMETER_DOUBLECOLON + 
									"<img id="+couponIdStr +
									" src="+ccPreviewUrl+" width="+coupon.getBarcodeWidth() +
									" height="+coupon.getBarcodeHeight()+ " />";
							imageCouponsPhList.add(imagecouponStr);
							imagecouponBeeStr = coupon.getCouponName() + Constants.DELIMETER_DOUBLECOLON + 
									"<img id=\""+couponIdStr + 
									"\" src=\""+ccPreviewUrl+"\" width=\""+coupon.getBarcodeWidth() +
									"\" height=\""+coupon.getBarcodeHeight()+ "\" />";
							imageCouponsPhBeeList.add(imagecouponBeeStr); 
							
						}
					} // for
					
					
				} // if
				
							
				
				StringBuffer bufferImg = new StringBuffer();
				for (String couponPlaceHolder : imageCouponsPhBeeList) {
					if(couponPlaceHolder.trim().startsWith("--") || couponPlaceHolder.toLowerCase().contains(("place holder"))) { //Ignore
						continue;
					}
					String[] imagePHArr= couponPlaceHolder.split(Constants.DELIMETER_DOUBLECOLON );
					String coupImgName = imagePHArr[0];
					String coupImgValue = imagePHArr[1];
					if(bufferImg.length() > 0) bufferImg.append(",");
					bufferImg.append("{name: '" +coupImgName+ "', value: '"+coupImgValue+"'}"); 
				} 
				String barCodeImg=bufferImg.toString();
				Sessions.getCurrent().setAttribute("barCodes79","["+barCodeImg+"]");
				logger.debug("-- Exit --");
			} catch (Exception e) {
				logger.error("Exception ::" , e);;
			}
		}
		
		
}
