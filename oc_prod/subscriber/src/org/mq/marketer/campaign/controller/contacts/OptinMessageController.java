package org.mq.marketer.campaign.controller.contacts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Address;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.UserFromEmailId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.CustomTemplatesDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.UserFromEmailIdDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
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
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

@SuppressWarnings({"serial","unchecked"})
public class OptinMessageController extends GenericForwardComposer {
    
	private CustomTemplates loadedTemplate;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private String optinMsgTemplate = PropertyUtil.getPropertyValueFromDB("optinMsgTemplate");
	
	private List welcomeMsgList = null;
	private CustomTemplatesDao customTemplateDao;
	private CustomTemplatesDaoForDML customTemplateDaoForDML;
	
	private static final int MESSAGE_ADD = 0;
	private static final int MESSAGE_UPDATE = 2;
	
	
	
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
	//private Checkbox cAddressCbId;
	private Checkbox cWebPageCb;
	private Checkbox toNameChkId,orgUnitChkBoxId,orgNameChkBoxId;
	private Radiogroup cPermRemRb;
	
	private Div permRemDivId;
	private Div persToDivId;
	private Div cWebLinkHboxId;
	private Listbox phLbId; 
	private Div changeAddressDivId, postalAddressDivId;
	
	//Set<Long> userIdsSet = GetUser.getUsersSet();//added for multiuser acc
	
	//Textbox createCustTemplateWinId$custTempNameTbId;
    Listbox welcomeListLbId;
    CKeditor ckEditorId;
    Button submitBtnId;
	Button updateBtnId;
	Button delBtnId;
	A sendTestMsgBtnId;
    Div listDivId;
    
    private Users currentUser;
    private UsersDao usersDao;
    private UsersDaoForDML usersDaoForDML;
    private UserFromEmailIdDao userFromEmailIdDao;
    private OrganizationStoresDao organizationStoresDao;
   
    private Session session;
    
    public OptinMessageController() {
    	
    	session = Sessions.getCurrent();
    	
    	String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
    	PageUtil.setHeader("Double Opt-in Email","",style,true);
    	
    	currentUser = GetUser.getUserObj();
    	String userDomainStr = "";
    	List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(currentUser.getUserId());
    	//Set<UsersDomains> domainSet = currentUser.getUserDomains();
    	for (UsersDomains usersDomains : domainsList) {
    		
    		if(userDomainStr.length()>0) userDomainStr+=",";
    		userDomainStr += usersDomains.getDomainName();
    		
    	}
    	
    	currentUser.setUserDomainStr(userDomainStr);
    	
    	usersDao = (UsersDao)SpringUtil.getBean("usersDao");
    	usersDaoForDML = (UsersDaoForDML)SpringUtil.getBean("usersDaoForDML");
    	organizationStoresDao = (OrganizationStoresDao)SpringUtil.getBean("organizationStoresDao");
    	customTemplateDao =	(CustomTemplatesDao)SpringUtil.getBean("customTemplatesDao");
    	customTemplateDaoForDML=(CustomTemplatesDaoForDML)SpringUtil.getBean("customTemplatesDaoForDML");
    	userFromEmailIdDao = (UserFromEmailIdDao)SpringUtil.getBean("userFromEmailIdDao");
    	
    }
    
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		logger.debug("--just entered--");
		super.doAfterCompose(comp);
     	
     	setStores();
     	CustomTemplates customTemplates = null;
		if(optinMsgTemplate !=null ) {
			ckEditorId.setValue(optinMsgTemplate);
		}
		Long userId = GetUser.getUserId();
		
		welcomeMsgList = new ArrayList();
		welcomeMsgList.add("Default message");
		
		Set<Long> listIdsSet = (Set<Long>)session.getAttribute(Constants.LISTIDS_SET);
		welcomeMsgList.addAll(customTemplateDao.getTemplatesByType(listIdsSet, "welcomemail"));
		String from = (String)session.getAttribute("fromAddNewBtn");
		if(from != null) {
			Sessions.getCurrent().removeAttribute("fromAddNewBtn");
			if(from.equalsIgnoreCase("contact/edit")) {
				
				customTemplates = (CustomTemplates)session.getAttribute("editCustomTemplate");
				
				refreshListBox(customTemplates,MESSAGE_UPDATE); 
				
				/*for(Listitem item : welcomeListLbId.getItems()) {
					
					
					if(customTemplates == null) {
						
						welcomeListLbId.setSelectedIndex(0);
						break;
						
					}else if(item.getLabel().equalsIgnoreCase(customTemplates.getTemplateName())) {
						
						welcomeListLbId.setSelectedIndex(item.getIndex());
						onSelect$welcomeListLbId();
						break;
						
					}
					
					
				}//for
*/				
				
				
			}//if
		else if(from.equalsIgnoreCase("contact/upload")) {
			refreshListBox(null,MESSAGE_ADD);
			
		}
		}else {
			refreshListBox(null,MESSAGE_ADD);
		}
		listDivId.setVisible(true);
		
		
		//*******************SET USERS FROM EMAIL ADDRESS******************************************************
		
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
		
		
		
		//***************************************************************************************
		
		logger.debug("--exited--");
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
			setUserStores(false);
			
		}
		
		
	}
	
	private Label storeAddrLblId;
	public void onCheck$addrRgId() {
		
		if(addrRgId.getSelectedIndex() == 1) {
			
			if( storesLbId.getItemCount() == 0) {
				
				
				
				MessageUtil.setMessage("Please choose another sender address option. No stores exist for the organization.", "color:red;");
				return;
				
				
				
			}
			
			OrganizationStores orgStore = (OrganizationStores)storesLbId.getSelectedItem().getValue();
			
			String storeAddrStr = "";
			if(orgStore.isAddressFlag()) {
				
				storeAddrStr = orgStore.getAddressStr(); 
				storeAddrStr = storeAddrStr.replace(Constants.ADDR_COL_DELIMETER, ", ");
				
			}
			
			storeAddrLblId.setValue(storeAddrStr); 
			storeAddrLblId.setVisible(true);
		}
		
		
	}
	
	private Window previewWin;
	private Html previewWin$html;
	public void onClick$custTempPreviewBtnId() {
		
		
		
		String htmlStr = ckEditorId.getValue();
		
		/*CustomTemplates customTemplate = welcomeListLbId.getSelectedItem().getValue();
		
		if(customTemplate == null) {
			
			htmlStr = PropertyUtil.getPropertyValueFromDB("optinMsgTemplate");
		}else {
			
			htmlStr = customTemplate.getHtmlText();
		}
		*/
		previewWin$html.setContent(htmlStr);
		//Html html = (Html)previewWin$html;
		//html.setContent(htmlStr);
		previewWin.setVisible(true);
		
	}
	
	public void setUserStores(boolean isModify) {
		
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
	
	public void onSelect$storesLbId () {
		
		setUserStores(true);
		
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
		/*postalAddressDivId.setVisible(false);
		changeAddressDivId.setVisible(true);*/
		String image = addrContentDivId.isVisible() ? "/img/icons/icon_minus.png" : "/img/icons/icon_plus.png";
		
		
		addrSettingsTbBtnId.setImage(image);
	}
	
	private Window createCustTemplateWinId;
	private Textbox createCustTemplateWinId$custTempNameTbId;
	private Label createCustTemplateWinId$msgLblId;
	/*public void onClick$addNewCustBtnId() {
		
		
		ckEditorId.setValue("");
		
		
		
	}*/
	
	
	
	  public void refreshListBox(CustomTemplates customTemplates,int flag) {
	    	
			logger.debug("Got CustomTemplates list of size :" + welcomeMsgList.size());
			
			if(customTemplates != null && flag == MESSAGE_ADD) {
				welcomeMsgList.add(customTemplates);
			}
			
			Listitem item = null;
			CustomTemplates custTemp = null;
			
			Components.removeAllChildren(welcomeListLbId);
			
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
						
						welcomeListLbId.setSelectedItem(items);
						break;
						
					}
					
					
				}
			}else{
				welcomeListLbId.setSelectedItem(welcomeListLbId.getItemAtIndex(0));
				
			}
			
			onSelect$welcomeListLbId();
			
	    }
	
	  String[] phArray = {"firstName","lastName","fullName"};
	
	/**
	 * This method executes when submit button is clicked <br/>
	 * Creates new optin welcome message
	 * @param event
	 */
	public void onClick$submitBtnId$createCustTemplateWinId(Event event) {
		
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
				welcomeMsgList = new ArrayList<CustomTemplates>();
				welcomeMsgList.add(customTemplates);
			}
			
			createCustTemplateWinId.setVisible(false);
			listDivId.setVisible(true);
			MessageUtil.setMessage("Template saved successfully.", "green");
			refreshListBox(customTemplates,MESSAGE_UPDATE);
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} 
		
		
	}

	/**
	 * This method executes when update button is clicked <br/>
	 * Updates selected welcome template
	 * @param event
	 */
	public void onClick$updateBtnId(Event event) {
		
		CustomTemplates customTemplates = welcomeListLbId.getSelectedItem().getValue();
		boolean isNew = false;
		if(customTemplates == null) {
			
			customTemplates = new CustomTemplates();
			customTemplates.setUserId(GetUser.getUserObj());
			customTemplates.setType("welcomemail");
			isNew =true;
			
		}
		
		submit(isNew, customTemplates);
	}

	/**
	 * This method executes when save as.. button is clicked <br/>
	 * creates a new template with a new given name with the selected template's content
	 * @param event
	 */
	public void onClick$newBtnId(Event event) {
		
		
			CustomTemplates customTemplates = new CustomTemplates();
			customTemplates.setUserId(GetUser.getUserObj());
			customTemplates.setType("welcomemail");
			
		
		
		submit(true, customTemplates);
	}

	
	
	
	
	//*************************************************************8
	public void loadSettings(CustomTemplates customTemplate) {
		logger.debug("--------------"+customTemplate);
		if(customTemplate !=  null) {
			
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
			logger.debug("addr type ::"+addrType);
			
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
							cPinLbId.setValue(address.getPin());
							cPinLbId.setAttribute("value", address.getPin());
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
				storesLbId.setSelectedIndex(getStore(addrType.split("\\|")[1].trim()));
				onSelect$storesLbId();
				addrRgId.setSelectedIndex(1);
				
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
					cPinLbId.setValue((currentUser.getPinCode()).trim());
				  cPinLbId.setAttribute("value", (currentUser.getPinCode()).trim());
					
				  
				}
			} catch (Exception e) {
				logger.error("** Exception: Problem occured while setting PinCode value : "+e+" **");
			}	
			try {
				if(currentUser.getPhone().trim()!=null){
					if(currentUser.getPhone().length()!=0)
					cPhoneTbId.setValue(currentUser.getPhone());
					cPhoneTbId.setAttribute("value", currentUser.getPhone());
				}
			} catch (Exception e) {
				logger.error("** Exception: Problem occured while setting updating user information . "+e+" **");
			}
		}catch(Exception e){
			logger.error("** Exception: Problem occured while setting updating user information . "+e+" **");
		}

		addrRgId.setSelectedIndex(0);
	}//else
		
	}
	
	
	public void defaultSettings() {
		
		cSubTb.setValue("");
		cFromNameTb.setValue(currentUser.getCompanyName());
		
		
		orgNameChkBoxId.setChecked(true);
		orgUnitChkBoxId.setChecked(true);
		
		if(storesLbId.getItemCount() > 0) {
			
			storesLbId.setSelectedIndex(0);
			onSelect$storesLbId();
			
			
		}
		
		cWebPageCb.setChecked(true);
		cWebLinkHboxId.setVisible(!cWebLinkHboxId.isVisible());
		
		cPermRemRb.setSelectedIndex(1);
		permRemDivId.setVisible(false);
		
		toNameChkId.setChecked(false);
		persToDivId.setVisible(false);
		
		
		
		
	}
	
	
	
	public int getStore(String storeId){
		
		int index = 0;
		String orgName = organizationStoresDao.findNameById(storeId, currentUser.getUserOrganization().getUserOrgId());
		
		for (Listitem item : storesLbId.getItems()) {
			
			if(orgName.trim().equals(item.getLabel())) {
				
				item.setSelected(true);
				index = item.getIndex();
				break;
			}
			
		}//for
		
		
		return index;
		
	}//getStore
	
	
	private Window testMailWinId;
	public void onClick$sendTestMsgBtnId() {

		try {
			
			CustomTemplates custTemp = (CustomTemplates)welcomeListLbId.getSelectedItem().getValue();
			
			testMailWinId.removeAttribute("customTemplate");
			testMailWinId.setAttribute("customTemplate", custTemp);
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
			
			
			String subject = "Confirmation Request";
			String htmlContent = PropertyUtil.getPropertyValueFromDB("optinMsgTemplate");
			if(customTemplates != null){
				subject = customTemplates.getSubject();
				if(customTemplates.getHtmlText()!= null && !customTemplates.getHtmlText().isEmpty()) {
					htmlContent = customTemplates.getHtmlText();
				}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(customTemplates.getEditorType()) && customTemplates.getMyTemplateId()!=null) {
				  MyTemplatesDao myTemplatesDao= (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
				  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(customTemplates.getTemplateId());
					  if(myTemplates!=null) htmlContent = myTemplates.getContent();
				}
			}
			
			
			
			if(emailId != null && emailId.trim().length() > 0) {
				

				
				if(logger.isDebugEnabled())logger.debug("Sending the Test mail....");
				
				MessageUtil.clearMessage();
				
				if(Utility.validateHtmlSize(htmlContent)>100) {
					
					Messagebox.show("HTML size cannot exceed 100kb. Please reduce" +
							" the size and try again.", "Error", Messagebox.OK, Messagebox.ERROR);
					return false;
				}
				
				String[] emailArr = null;
				
				emailArr = emailId.split(",");
				for (String email : emailArr) {
					
					if(!Utility.validateEmail(email.trim())){
						testMailWinId$msgLblId.setValue("Invalid Email Id:'"+email+"'");
						return false;
					}
					
					//Utility.sendTestMail(campaign, campaign.getHtmlText(), email);
					
					
				}//for
				
				for (String email : emailArr) {
					Utility.sendInstantMail(null, subject, htmlContent,
							Constants.EQ_TYPE_TEST_OPTIN_MAIL, email,customTemplates );
					
			//		Utility.sendTestMail(campaign, campaign.getHtmlText(), email);
					/*String postData = "UserRequest=enable";
					URL url = new URL("http://localhost:8080/Scheduler/simpleMailSender.mqrm");
					
					HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
					
					urlconnection.setRequestMethod("POST");
					urlconnection.setRequestProperty("Content-Type","text/html");
					urlconnection.setDoOutput(true);
					
					OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
					out.write(postData);
					out.flush();
					out.close();*/
					
					
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
	
	
	
	
	
	
	
	
	
	
	/**
	 * method checks the availability of the values of the address fields for the user.
	 * If not the address div will be set open and user will be asked to provide the values.
	 */
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
					
					//logger.info("custom template "+customtemplate);
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
	
	
public void setAddress(boolean ismodifyUserAddr) {
		
		try{
			
			String postalAddrStr = "";
			if(ismodifyUserAddr && postalAddressDivId.isVisible() ) {

				postalAddressDivId.setVisible(false);
				changeAddressDivId.setVisible(true);
			} 
			
			else {
				
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
					
					MessageUtil.setMessage("Please provide State.",  "color:red;");
					
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
				if(pinValue == null ) {
					
					MessageUtil.setMessage("Please provide Pin / Zip code.", "color:red;");
					
					return ;
					
				}else{
					
					logger.info("pinValue.longValue() = "+(pinValue).length());
					if((pinValue).length() > 6 || (pinValue).length() < 5 ){ 
						MessageUtil.setMessage("Please provide 5 / 6 digits Zip code / Pin.", "color:red;");
						
						return;
					}
					
					try {
						long val = Long.parseLong(pinValue);
					} catch (Exception e) {
						
						MessageUtil.setMessage("Please provide only number for Zip code / Pin.", "color:red;");
						return;
					}
					
					cPinLbId.setAttribute("value", pinValue);
					postalAddrStr += pinValue;
				}
				
				
				value = cPhoneTbId.getValue().trim();
				if(value != null ) {
//					Long phone = null;
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
						
						if(value.length() > 0 ){
							
							if( Utility.validateUserPhoneNum(value) ) {
								MessageUtil.setMessage("Please provide valid Phone number.", "color:red;");
								return;
							}
								
							else {
								cPhoneTbId.setAttribute("value", ""+value);
								postalAddrStr += ", "+value;;
								
							}
						
						}else {
							
							cPhoneTbId.setAttribute("value", ""+value);
							postalAddrStr += value;;
						}
						
					} catch (NumberFormatException e) {

						MessageUtil.setMessage("Please provide valid Phone number.", "color:red;");
						
						return ;
					}
					
					
				}//if
				
				postalAddressRbId.setLabel(postalAddrStr);
				postalAddressDivId.setVisible(true);
				changeAddressDivId.setVisible(false);
				if(ismodifyUserAddr) {
					
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
					currentUser.setPinCode(cPinLbId.getValue());
					
					if(cPhoneTbId.getValue() != null) {
						
						currentUser.setPhone(cPhoneTbId.getValue().trim());
						
					}
					//usersDao.saveOrUpdate(currentUser);
					usersDaoForDML.saveOrUpdate(currentUser);
					
				}//if
				
			
			}
			
		}catch (Exception e) {
			logger.error("** Exception while saving the postalAddress--:",e);
		}
	
	
}
	
	
	
	
	
	
	private Radiogroup addrRgId;
	
	//****************************************************
	/**
	 * Create or update the welcome template
	 * @param isUpdate - boolean
	 */
	private void submit(boolean isUpdate, CustomTemplates customTemplates) {
		try {
			
			String textHTML = ckEditorId.getValue();
			logger.debug("HTML text is :"+textHTML);
			String subscribeLink = PropertyUtil.getPropertyValue("subscribeLink");
			if(textHTML.indexOf(subscribeLink)<0) {
				MessageUtil.setMessage("Custom HTML must have at least one [url].", "red", "top");
				return;
			}
			
			customTemplates.setHtmlText(ckEditorId.getValue());
			
			if(cSubTb.isValid() && !cSubTb.getValue().isEmpty()){
				customTemplates.setSubject(cSubTb.getValue());
			}
			else{
				MessageUtil.setMessage("Please provide subject. Subject should not be empty.", "color:red", "TOP");
				cSubTb.setFocus(true);
				//onClick$basicSettingsTbBtnId();
				return;
			}
			
			if(cFromNameTb.isValid() && Utility.validateFromName(cFromNameTb.getValue())){
				customTemplates.setFromName(cFromNameTb.getValue());
			}
			else{
				MessageUtil.setMessage("Please provide valid 'From Name'. Special characters are not allowed.", "color:red", "TOP");
				cFromNameTb.setFocus(true);
				//onClick$basicSettingsTbBtnId();
				return;
			}
			
			if(cFromEmailCb.getSelectedItem().getLabel().indexOf('@')<0) {
				MessageUtil.setMessage("Register a 'From Email' to create Double Opt-in email.",
						"color:red", "TOP");
				return;
			}
			else if(!(cFromEmailCb.getSelectedIndex()==-1)) {
				customTemplates.setFromEmail(cFromEmailCb.getSelectedItem().getLabel());
			}
			else {
				MessageUtil.setMessage("Provide valid 'From email address'.", "color:red", "TOP");
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
				
				
				
				//includeBfrStr += currentUser.getUserDomainStr();
				
				
				
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
					//onClick$addrSettingsTbBtnId();
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
					//onClick$addrSettingsTbBtnId();
					return;
				}
				if(!cStateTbId.getValue().trim().equals("")){
					address.setState(cStateTbId.getValue());
				}else{
					MessageUtil.setMessage("Please provide State.", "color:red", "TOP");
					cStateTbId.setFocus(true);
					//onClick$addrSettingsTbBtnId();
					return;
				}
				if(!cCountryTbId.getValue().trim().equals("")) {
					address.setCountry(cCountryTbId.getValue());
				}else {
					MessageUtil.setMessage("Please provide Country.", "color:red", "TOP");
					//onClick$addrSettingsTbBtnId();
					cCountryTbId.setFocus(true);
					return;
				}
				if( (cPinLbId.getValue().trim()).length() <= 6 && cPinLbId.getValue().trim().length() >= 5  ) {
					address.setPin((cPinLbId.getValue()));
				}else {
					if(cPinLbId.getValue().trim().length() == 0){
						MessageUtil.setMessage("Please provide Pin / Zip code.", "color:red", "TOP");
					}
					
					else if((cPinLbId.getValue().trim()).length() > 6 || (cPinLbId.getValue().trim()).length() < 5 ){ MessageUtil.setMessage("Please provide 5 / 6 digits Zip code / Pin.", "color:red", "TOP");
					
					//onClick$addrSettingsTbBtnId();
					}
					cPinLbId.setFocus(true);
					return;
				}
				if(cPhoneTbId.getValue()!= null){
					try{
//						Long phone = null;
						if(!cPhoneTbId.getValue().trim().equals("") 
								&& (Utility.phoneParse(cPhoneTbId.getValue().trim(),GetUser.getUserObj().getUserOrganization())==null)){
								//!Utility.validateUserPhoneNum(cPhoneTbId.getValue().trim())) {
						//phone = Long.parseLong(cPhoneTbId.getValue());
						
								
								MessageUtil.setMessage("Please provide valid Phone number.", "Color:Red", "Top");
								cPhoneTbId.setFocus(true);
								//onClick$addrSettingsTbBtnId();
							//long phone = Long.parseLong(value);
								return;
							
						}
					}catch (NumberFormatException e) {

						MessageUtil.setMessage("Please provide valid Phone number.", "color:red", "TOP");
						cPhoneTbId.setFocus(true);
						//onClick$addrSettingsTbBtnId();
						return;
					
					}
					
					address.setPhone(""+cPhoneTbId.getValue().trim());
				}
				
				//logger.debug("-----address----"+address);
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
			
			
			
			
				MessageUtil.setMessage("Welcome email saved successfully.","green", "top");
			
			}
		} catch (Exception e) {
			logger.error("** Exception :", e);
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
			if(customTemplate != null && customTemplate.getTemplateName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * onClick event for the edit button
	 * loads the selected template for editing
	 * @param event - ZK event
	 */
	public void onSelect$welcomeListLbId() {
		try {
			MessageUtil.clearMessage();
			
			if(welcomeListLbId.getSelectedIndex() == 0) {
				createNewMessage();
				loadSettings(null);
				return;
			}
			CustomTemplates customTemplates = (CustomTemplates)welcomeListLbId.getSelectedItem().getValue();
			ckEditorId.setValue(customTemplates.getHtmlText());
			loadSettings(customTemplates);
			setAddress(false);
			setUserAddress(customTemplates);
			
			
			
			
			loadedTemplate = customTemplates;
			
			updateBtnId.setVisible(true);
			delBtnId.setVisible(true);
			sendTestMsgBtnId.setVisible(true);
			
		} catch (Exception e) {
			logger.error("Exception : ",e);
		}
	}
	
	/**
	 *  onClick event for delete Button 
	 *  deletes the selected message in List box.
	 */
	public void onClick$delBtnId() {
		
		try {
			if (Messagebox.show("Are you sure you want to delete " +
					"the seleted welcome message?", "Delete?", Messagebox.YES | 
					Messagebox.NO, Messagebox.QUESTION)  ==  Messagebox.YES) {
					if(welcomeListLbId.getSelectedIndex() < 1) {
						MessageUtil.setMessage("Select a message to delete.", "red", "top");
						return;
					}
					MessageUtil.clearMessage();
					CustomTemplates customTemplates = (CustomTemplates)welcomeListLbId.getSelectedItem().getValue();
					
					
					MailingListDao mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
					int count = mailingListDao.findOptInAssociateCount(customTemplates.getTemplateId());
					if(count > 0) {
						
						MessageUtil.setMessage("Selected template is associated with one or more mailing lists and cannot be deleted.", "color:red;");
						return;
						
						
						
					}
					
					
					//customTemplateDao.delete(customTemplates);
					customTemplateDaoForDML.delete(customTemplates);
					welcomeListLbId.getSelectedItem().setVisible(false);
					welcomeListLbId.setSelectedIndex(0);
					//submitBtnId.setVisible(true);
					//updateBtnId.setVisible(false);
					delBtnId.setVisible(false);
					//sendTestMsgBtnId.setVisible(false);
					//ckEditorId.setValue("");
					logger.info("deleted selected welcome message successfully.");
					MessageUtil.setMessage( customTemplates.getTemplateName()+" Welcome message deleted successfully.", "blue", "top");
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
	
	
	public void createNewMessage() {
		
		//submitBtnId.setVisible(true);
		//updateBtnId.setVisible(false);
		//createCustTemplateWinId$custTempNameTbId.setValue("");
		if(optinMsgTemplate !=null ) {
			ckEditorId.setValue(optinMsgTemplate);
		}
		loadedTemplate = null;
		//createCustTemplateWinId$custTempNameTbId.setDisabled(false);
		delBtnId.setVisible(false);
		
		//sendTestMsgBtnId.setVisible(false);
	}
	
	/**
	 * Renderer class for the welcome message templates listbox
	 *//*
    class ListRenderer implements ListitemRenderer{
    	*//**
    	 * executes when setModel is called on the listbox
    	 *//*
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
	}*/
}
