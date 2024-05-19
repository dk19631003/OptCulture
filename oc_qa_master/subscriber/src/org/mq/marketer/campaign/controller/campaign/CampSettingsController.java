/**
 * This class is bind with CampSettings.zul, first page in Campaign Creation
 * 
 */
package org.mq.marketer.campaign.controller.campaign;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import org.mq.marketer.campaign.beans.CampaignSchedule;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.DefaultCategories;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.UserCampaignCategories;
import org.mq.marketer.campaign.beans.UserFromEmailId;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.contacts.ManageAutoEmailsController;
import org.mq.marketer.campaign.controller.contacts.ManageAutoEmailsControllerBee;
import org.mq.marketer.campaign.controller.layout.EditorController;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CampaignScheduleDao;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.CampaignsDaoForDML;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.DefaultCategoriesDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.dao.UserCampaignCategoriesDao;
import org.mq.marketer.campaign.dao.UserFromEmailIdDao;
import org.mq.marketer.campaign.dao.UserFromEmailIdDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.CampaignStepsEnum;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PlaceHolders;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataIntegrityViolationException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.Readonly;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

@SuppressWarnings("serial")
public class CampSettingsController extends GenericForwardComposer {

	private Textbox cNameTbId;
	private Textbox cSubTb;
	private Textbox cFromNameTb;
	private Combobox cFromEmailCb; 
	private Combobox cReplyEmailCb;
	private Combobox cCategoryCb;
	private Textbox cAddressOneTbId;
	private Textbox cAddressTwoTbId;
	private Textbox cCityTbId;
	private Textbox cStateTbId;
	private Textbox cCountryTbId;
	private Textbox permRemTextId, gaCampTitleTxtbxId;
	private Textbox cWebLinkTextTb;
	private Textbox cWebLinkUrlTextTb;
	private Radio postalAddressRbId,homeStoreAddressDetailsId,lastPurchaseStoreId,storeAddressId;
	private Textbox cPinLbId;
	private Textbox cPhoneTbId;
	private Checkbox cAddressCbId;
	private Checkbox cWebPageCb;
	private Checkbox toNameChkId, gaChekbxId,downloadPdf;
	
	private Radiogroup cPermRemRb;
	private Button backBtnId;
	private Button saveAsDraftBtnId;
	//private Button saveAsDraftBtnIdAtTop;
	private Button saveBtnId;
	//private Button saveBtnIdAtTop;
	private Div postalAddressDivId;
	private Div changeAddressDivId;
	//private Label phValLblId;
	private Div permRemDivId;
	private Div persToDivId;
	private Div cCategoryDivId;
	private Div cWebLinkHboxId;
	private Div gaTitleDivId;
	private Popup regEmailPopupId;
	private Window personalizationTagsWinId;
	
/*	private Div tagsDivId$personalizationTagsWinId;
	private Div symbolDivId$personalizationTagsWinId;
*/	
	private Div personalizationTagsWinId$tagsDivId;
	private Div personalizationTagsWinId$symbolDivId;
	private Div personalizationTagsWinId$emojiDivId;

	
	private Session session = null;
	private Listbox phLbId; 
	private Listbox couponLbId;
	private Textbox caretPosTB;
	//private String userName;
	//private Long userId;
	
	private A mergeTagsAId, symbolTagsAId,emojiTagsAId,changeAddressId;
	
	private boolean nameExist;
	private Users currentUser;
	private CampaignsDao campaignsDao;
	private CampaignsDaoForDML campaignsDaoForDML;
	private UserFromEmailIdDao userFromEmailIdDao;
	private UserFromEmailIdDaoForDML userFromEmailIdDaoForDML;
	private EmailQueueDao emailQueueDao;
	private UsersDao usersDao;
	private UsersDaoForDML usersDaoForDML;
	private OrganizationStoresDao organizationStoresDao;
	private CouponsDao couponsDao;
	private UserCampaignCategoriesDao userCampaignCategoriesDao;
	private DefaultCategoriesDao defaultCategoriesDao;
	private CampaignScheduleDao campaignScheduleDao;
	
	private String campaignName;
	private Campaigns campaign;
	private Label nameStatusLblId;
	String[] phArray = {"firstName","lastName","fullName"};
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private String isEdit = null;
	private Checkbox customFooterId; 
	
	//APP-3431
	private Radiogroup alignmentRbId;
	private Radio leftRadioButtonId,rightRadioButtonId,centerRadioButtonId;
	
	//APP-4123
	private Checkbox cPreviewTextCb;
	private Div cPreviewTextHboxId;
	private Textbox cPreviewTextTb;
	//private Set<Long> userIdsSet = GetUser.getUsersSet();//added for multi user Acc
	private boolean isAdmin;
	public CampSettingsController(){
		this.session = Sessions.getCurrent();
		//this.userName = GetUser.getUserName();
		//this.userId = GetUser.getUserId();
		this.campaignScheduleDao = (CampaignScheduleDao)
				SpringUtil.getBean("campaignScheduleDao");
		this.campaign = (Campaigns)session.getAttribute("campaign");
		this.isEdit = (String)session.getAttribute("editCampaign");
		if(isEdit!=null){
			if(campaign==null){
				//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW);
				Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
			}
		}
		
		logger.info("isEdit------>"+isEdit);
		
		Utility.breadCrumbFrom(1);
		isAdmin = (Boolean)session.getAttribute("isAdmin");

	}
	
	public void onClick$mergeTagsAId() {
		personalizationTagsWinId$tagsDivId.setVisible(true);
		personalizationTagsWinId$symbolDivId.setVisible(false);
		personalizationTagsWinId$emojiDivId.setVisible(false);
		personalizationTagsWinId.setVisible(true);
		personalizationTagsWinId.doHighlighted();
	}
	
	public void onClick$symbolTagsAId() {
		personalizationTagsWinId$tagsDivId.setVisible(false);
		personalizationTagsWinId$symbolDivId.setVisible(true);
		personalizationTagsWinId$emojiDivId.setVisible(false);
		personalizationTagsWinId.setVisible(true);
		personalizationTagsWinId.doHighlighted();
	}
	public void onClick$emojiTagsAId() {
		personalizationTagsWinId$tagsDivId.setVisible(false);
		personalizationTagsWinId$symbolDivId.setVisible(false);
		personalizationTagsWinId$emojiDivId.setVisible(true);
		personalizationTagsWinId.setVisible(true);
		personalizationTagsWinId.doHighlighted();
	}
	
	
	Radiogroup personalizationTagsWinId$tagsTypeRgId;
	Radio personalizationTagsWinId$generalMergeTagRId, personalizationTagsWinId$salesMergeTagRId, personalizationTagsWinId$loyaltyMergeTagRId;
	Div  personalizationTagsWinId$generalTagsDivId ,personalizationTagsWinId$storeTagsDivId , personalizationTagsWinId$loyaltyTagsDivId ;
	
	
	public void onCheck$tagsTypeRgId$personalizationTagsWinId(){
		logger.info("radio group");
		
		if(!(personalizationTagsWinId$tagsTypeRgId.getSelectedIndex()== -1) ){
			
			if(personalizationTagsWinId$generalMergeTagRId.isChecked()){
				logger.info("general radio");
				personalizationTagsWinId$generalTagsDivId.setVisible(true);
				personalizationTagsWinId$storeTagsDivId.setVisible(false);
				personalizationTagsWinId$loyaltyTagsDivId.setVisible(false);
			}
			else if(personalizationTagsWinId$salesMergeTagRId.isChecked()){
				logger.info("sales radio");
				personalizationTagsWinId$generalTagsDivId.setVisible(false);
				personalizationTagsWinId$storeTagsDivId.setVisible(true);
				personalizationTagsWinId$loyaltyTagsDivId.setVisible(false);
			}
			else if(personalizationTagsWinId$loyaltyMergeTagRId.isChecked()){
				logger.info("loyalty radio");
				personalizationTagsWinId$generalTagsDivId.setVisible(false);
				personalizationTagsWinId$storeTagsDivId.setVisible(false);
				personalizationTagsWinId$loyaltyTagsDivId.setVisible(true);
				
			}
		}
	}
	
	
	public void onSelect$generalMergeTagRId$personalizationTagsWinId(){
		logger.info("select general radio");
		personalizationTagsWinId$generalTagsDivId.setVisible(true);
		personalizationTagsWinId$storeTagsDivId.setVisible(false);
		personalizationTagsWinId$loyaltyTagsDivId.setVisible(false);
	}
	
	public void onSelect$salesMergeTagRId$personalizationTagsWinId(){
		logger.info("select sales radio");
		personalizationTagsWinId$generalTagsDivId.setVisible(false);
		personalizationTagsWinId$storeTagsDivId.setVisible(true);
		personalizationTagsWinId$loyaltyTagsDivId.setVisible(false);
	}
	
	public void onSelect$loyaltyMergeTagRId$personalizationTagsWinId(){
		logger.info("select loyalty radio");
		personalizationTagsWinId$generalTagsDivId.setVisible(false);
		personalizationTagsWinId$storeTagsDivId.setVisible(false);
		personalizationTagsWinId$loyaltyTagsDivId.setVisible(true);
	}
	
	/**
	 * This method returns SimpleListModel object to initialize the personalize-to listbox
	 * @return
	 */
	public ListModel getPhModel() {
		return  (new SimpleListModel(phArray));
	}
	private Listbox categoryLbId;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		personalizationTagsWinId$tagsTypeRgId.setSelectedIndex(0);
		currentUser = GetUser.getUserObj();
		this.campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
		this.campaignsDaoForDML = (CampaignsDaoForDML)SpringUtil.getBean("campaignsDaoForDML");
		emailQueueDao = (EmailQueueDao)SpringUtil.getBean("emailQueueDao");
		this.userFromEmailIdDao = (UserFromEmailIdDao)SpringUtil.getBean("userFromEmailIdDao");
		this.userFromEmailIdDaoForDML = (UserFromEmailIdDaoForDML)SpringUtil.getBean("userFromEmailIdDaoForDML");

		usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		usersDaoForDML = (UsersDaoForDML)SpringUtil.getBean("usersDaoForDML");
		organizationStoresDao = (OrganizationStoresDao)SpringUtil.getBean("organizationStoresDao");
		couponsDao = (CouponsDao)SpringUtil.getBean("couponsDao");
		userCampaignCategoriesDao = (UserCampaignCategoriesDao)SpringUtil.getBean("userCampaignCategoriesDao");
		defaultCategoriesDao =(DefaultCategoriesDao)SpringUtil.getBean("defaultCategoriesDao");
		
		
		
		MessageUtil.clearMessage();
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
     	PageUtil.setHeader("Create Email (Step 1 of 6)","",style,true);
		
		//PageUtil.setHeader("Create Email (Step 1 of 6)","","",true);
     	setStores();
     	
     	if(currentUser.getSubscriptionEnable()){
     		getCampCategorties();
     	}else{
     		categoryLbId.setDisabled(true);
     	}
     	helpImgId.setVisible(categoryLbId.isDisabled());
     	
     	
		loadSettings();
		cSubTb.setFocus(true);
		
		
		
		cCategoryCb.setVisible(false);
		
		/*if( (user.getAddressOne() == null || user.getAddressOne().equals("") ) || 
			(user.getCity() == null || user.getCity().equals("")) ||
			(user.getState() == null || user.getState().equals("")) ||
			(user.getCountry() == null || user.getCountry().equals("")) ||
			(user.getPinCode() == null || user.getPinCode().equals("")) ) {
			
			
			onClick$changeAddressId();
			
			
		}
		
		String postalAddressData = user.getAddressOne() +", "+user.getAddressTwo()+", "+user.getCity()+", "+user.getCountry()+
		", "+user.getState()+", "+user.getPinCode();
		postalAddressTbId.setValue(postalAddressData);*/
		
		setUserAddress();
		
		
		Map<String, String> mergTagsMap = prepareMergeTagsMap();
		personalizationTagsWinId.setAttribute("mergeTagMap", mergTagsMap);
		
		//************************** Insert Coupons place holders in subject field
		
		/*if(isAdmin) {
			List<String> couponPhList = EditorController.getCouponsList();
			couponLbId.setItemRenderer(new phListRenderer());
			couponLbId.setModel(new SimpleListModel(couponPhList));	
		}*/

	}
	private Image helpImgId;
	public void getCampCategorties(){
		
	/*	
		for(int i = categoryLbId.getItemCount() ; i>1 ; i--) {
			
			categoryLbId.removeItemAt(i);
		}*/
		
		List<UserCampaignCategories> userCategoriesList= userCampaignCategoriesDao.findCatByUserId(currentUser.getUserId());
		
		//TODO chk with mallika
		Listitem campCategory = null;
		if(userCategoriesList == null || userCategoriesList.size() == 0){
			
			categoryLbId.setDisabled(true);
			/*helpImgId.setVisible(true);
			help2.open(helpImgId);*/
			
		}
		else{
			
			for (UserCampaignCategories userCampaignCategories : userCategoriesList) {
				campCategory = new Listitem(userCampaignCategories.getCategoryName(),userCampaignCategories);
				campCategory.setParent(categoryLbId);
				
			}
			
			categoryLbId.setDisabled(false);
			
		/*	helpImgId.setVisible(false);
			help2.close();*/
			
		}
		
		helpImgId.setVisible(categoryLbId.isDisabled());
		if(categoryLbId.getItemCount() > 0 ) categoryLbId.setSelectedIndex(0);
	}
	
	public Map<String, String> prepareMergeTagsMap() {
		
		Map<String, String> mergTagsMap = new HashMap<String, String>();
		Set<String> PhList = EditorController.getPlaceHolderList(null);
		
		for (String phToken : PhList) {
			String[] StringArr = phToken.split(Constants.DELIMETER_DOUBLECOLON);
			mergTagsMap.put(StringArr[0], StringArr[1]);
		}
		return mergTagsMap;
	}
	
//************** Insert coupons place holders in the subject text box cursor position	
	public void onClick$couponTbId() {
	    try{
	    	if(couponLbId.getSelectedItem() == null){
	    		MessageUtil.setMessage("Please select a Promo-code.", "red");
	    		return;
	    	}
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
	
	/**
	 * method checks the availability of the values of the address fields for the user.
	 * If not the address div will be set open and user will be asked to provide the values.
	 */
	public void setUserAddress() {
		
		setUserAddr();
		
		if(campaign == null) {
		
		
			if( (currentUser.getAddressOne() == null || currentUser.getAddressOne().equals("") ) || 
					(currentUser.getCity() == null || currentUser.getCity().equals("")) ||
					(currentUser.getState() == null || currentUser.getState().equals("")) ||
					(currentUser.getCountry() == null || currentUser.getCountry().equals(""))){
					//(currentUser.getPinCode() == null || currentUser.getPinCode().equals("")) ) {
					
					changeAddressDivId.setVisible(true);
					postalAddressDivId.setVisible(false);
					
					
				}
			else{
				postalAddressDivId.setVisible(true);
				changeAddressDivId.setVisible(false);
			}
		}else {
				
			if(campaign.isCustomizeFooter())
				customFooterId.setChecked(true);
			else
				customFooterId.setChecked(false);
				
				onCheck$customFooterId();
				if(campaign.isAddressFlag()) {
					Address address = campaign.getAddress();
					
					//logger.info(" address "+address);
					if(address.getDinamicAddrstr() == null && campaign.getAddrsType() != null && campaign.getAddrsType().equalsIgnoreCase("User") ) {
						if( (address.getAddressOne() == null || address.getAddressOne().equals("") ) ||
								(address.getCity() == null || address.getCity().equals("")) ||
								(address.getState() == null || address.getState().equals("")) || 
								(address.getCountry() == null || address.getCountry().equals("") )){
								//(address.getPin() == null  ) ) {
							
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
		
		String pinValue = ((String)cPinLbId.getAttribute("value"));
		if(pinValue != null && pinValue.length() > 0 ) {
			
			postalAddressData += pinValue;
			
		}
		
		value = (String)cPhoneTbId.getAttribute("value");
		if(value != null && !value.equals("")) {
			
			postalAddressData += ", "+value;
			
		}
		
		
		
		
		postalAddressRbId.setLabel(postalAddressData);
		
		
	}
	
	public void onChange$caretPosTB() {
		
		logger.info("dummy event onChange");
	}
	
	public void onBlur$caretPosTB() {
		
		logger.info("dummy event onBlur");
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
	
	
	private void setUserStores(boolean isModify) {
		
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
		if(isModify) {
			addrRgId.setSelectedIndex(3);
			onCheck$addrRgId();
		}
		
		
	}
	
	private Label storeAddrLblId;
	public void onSelect$storesLbId () {
		
		setUserStores(true);
		
	}
	
private Radiogroup addrRgId;
public void onCheck$addrRgId() {
	
	try {
		Address address = null;
		String addrType = null;
		
		addrRgId.removeAttribute("campaignAddress");
		addrRgId.removeAttribute("campAddressType");
		
		
		if(addrRgId.getSelectedIndex() == 0) {
			addrType = Constants.CAMP_ADDRESS_TYPE_USER;
			postalAddressDivId.setVisible(true);
			changeAddressDivId.setVisible(false);
			
			address = new Address();
			
			if(!cAddressOneTbId.getValue().trim().equals("")){
				address.setAddressOne(cAddressOneTbId.getValue());
			}else{
				MessageUtil.setMessage("Please enter Address Line 1.", "color:red", "TOP");
				cAddressOneTbId.setFocus(true);
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
				return;
			}
			if(!cStateTbId.getValue().trim().equals("")){
				address.setState(cStateTbId.getValue().trim());
			}else{
				MessageUtil.setMessage("Please provide State.", "color:red", "TOP");
				cStateTbId.setFocus(true);
				return;
			}
			if(!cCountryTbId.getValue().trim().equals("")) {
				address.setCountry(cCountryTbId.getValue());
			}else {
				MessageUtil.setMessage("Please provide Country.", "color:red", "TOP");
				cCountryTbId.setFocus(true);
				return;
			}
			String pin = cPinLbId.getValue().trim();
			String countryType = currentUser.getCountryType();
			
			if(Utility.zipValidateMap.containsKey(countryType)){
				if(pin.length() == 0 || pin.equals("")) {
					
					//	Messagebox.show("pin should not be empty.");
						MessageUtil.setMessage("Pin / Zip code cannot be left empty.", "Color:red", "Top");
						return;
				}
				boolean zipCode = Utility.validateZipCode(pin, countryType);
				 
				 if(!zipCode){
					 
						MessageUtil.setMessage("Please enter valid zip code.","color:red;");
						return ;
						
					}else address.setPin((pin));
				
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
			
			
			/*if(pin.length() > 0 && pin.length() <= 6 && pin.length() >= 5) {
				address.setPin((pin));
			}else {
				if(pin.length() == 0) MessageUtil.setMessage("Please provide Pin.", "color:red", "TOP");
				
				else if( (pin.length() > 6) || (pin.length() < 5 ) )MessageUtil.setMessage("Please provide 5 / 6 digits Zip Code / Pin.", "color:red", "TOP");
				
				cPinLbId.setFocus(true);
				return;
			}*/
			String value = cPhoneTbId.getValue();
			if(value != null && value.length() > 0 ){
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
					UserOrganization organization = currentUser != null ? currentUser.getUserOrganization() : null ;
					value =  Utility.phoneParse(value, organization);
					if(value == null || value.trim().length() == 0){

						MessageUtil.setMessage("Please provide valid Phone Number.", "Color:Red", "Top");
					//long phone = Long.parseLong(value);
						return;
						
					}
					
					address.setPhone(value);
					
				}catch (Exception e) {
					
					MessageUtil.setMessage("Please provide valid Phone Number.", "color:red", "TOP");
					cPhoneTbId.setFocus(true);
					return;
				
				}
				
				//address.setPhone(""+cPhoneTbId.getValue().trim());
			}
			
			
		}//if user address
		else if(addrRgId.getSelectedIndex() == 1) {
			
			address = new Address(Constants.PH_CAMPAIGN_ADDRESS_CONTACT_HOMESTORE);
			addrType = Constants.CAMP_ADDRESS_TYPE_CONTACT_HOME_STORE;
			
		}else if(addrRgId.getSelectedIndex() == 2) {
			//logger.debug("-----------10-------------");
			addrType = Constants.CAMP_ADDRESS_TYPE_CONTACT_LAST_PURCHASED_STORE;
		
			address = new Address(Constants.PH_CAMPAIGN_ADDRESS_CONTACT_LAST_PURCHASED_STORE);
			
		}else if(addrRgId.getSelectedIndex() == 3) {
			
			if(storesLbId.getItemCount() == 0) {
				
				MessageUtil.setMessage("No Organization Stores exist.", "color:red;");
				return;
				
				
			}
			
			OrganizationStores orgStore = (OrganizationStores)storesLbId.getSelectedItem().getValue();
			
			addrType = Constants.CAMP_ADDRESS_TYPE_STORE+"|"+(orgStore.getStoreId().longValue());
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
				
			}//if
			
			
			
			storeAddrLblId.setValue(storeAddrStr); 
			storeAddrLblId.setVisible(true);
			
			address = orgStore.getAddress();
			
			
			
			
		}
		
		
		
		addrRgId.setAttribute("campaignAddress", address);
		addrRgId.setAttribute("campAddressType", addrType);
		
	} catch (WrongValueException e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::", e);
	} catch (NumberFormatException e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::", e);
	}catch (Exception e) {
		logger.error("Exception ::", e);
	}
	
	
}
	
	private Div gbContentDivId;
	private Toolbarbutton optSettingsTbBtnId;
	public void onClick$optSettingsTbBtnId() {
	
		gbContentDivId.setVisible(!gbContentDivId.isVisible());
		
		String image = gbContentDivId.isVisible() ? "/img/icons/icon_minus.png" : "/img/icons/icon_plus.png";
		
		
		optSettingsTbBtnId.setImage(image);
	}

	public void setDefaultName(){
		
		try {
			Session sessionScope = Sessions.getCurrent();
			TimeZone clientTimeZone = (TimeZone)sessionScope.getAttribute("clientTimeZone");
			
			Calendar tempCal=null;
//			tempCal=triggerCustomEvent.getEventDate();
			tempCal = Calendar.getInstance();
			tempCal.setTimeZone(clientTimeZone);
			
//			  Format formatter = new SimpleDateFormat(" dd MMM yyyy hh_mm_ss ");
			  Format formatter = new SimpleDateFormat(" dd MMM, hh:mm a");
			 String timeStamp = "Email of "+MyCalendar.calendarToString(tempCal, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone);
//			 String timeStamp = user.getUserName()+today;
			// cNameTbId.setValue(timeStamp); 
			 String company = currentUser.getCompanyName();
			 
			 if(company != null) {
				 cFromNameTb.setValue(company);
			 }
			
		} catch (Exception e) {
			logger.error("** Exception : error occured while setting campaign Name . ",e);
		}
		
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
		cCountryTbId.setValue((String)cCountryTbId.getAttribute("value"));
		cPinLbId.setValue(((String)cPinLbId.getAttribute("value")).trim());
		cPhoneTbId.setValue(((String)cPhoneTbId.getAttribute("value")).trim());
		
		postalAddressDivId.setVisible(true);
		changeAddressDivId.setVisible(false);
		
		
		
	}
	
	
	
	public void setAddress(boolean isModify) {
		
		try{
			
			String postalAddrStr = "";
			if(isModify && postalAddressDivId.isVisible() ) {

				postalAddressDivId.setVisible(false);
				changeAddressDivId.setVisible(true);
			} else {
				
				String value = null;
				
				value = cAddressOneTbId.getValue();
				if(value == null || value.equals("")) {
					
					MessageUtil.setMessage("Please enter Address Line 1.", "color:red;");
					
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
						
						//	Messagebox.show("pin should not be empty.");
							MessageUtil.setMessage("Pin / Zip code cannot be left empty.", "Color:red", "Top");
							return;
					}
					boolean zipCode = Utility.validateZipCode(pinValue, countryType);
					 
					 if(!zipCode){
						 
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
					
					logger.info("pinValue.longValue() = "+pinValue.length());
					if( (pinValue.length() > 6) || (pinValue.length() < 5) ){ 
						MessageUtil.setMessage("Please provide 5 / 6 digits Zip code / Pin.", "color:red;");
						
						return;
					}
					
					try {
						long val = Long.parseLong(pinValue);
					} catch (Exception e) {
						
						MessageUtil.setMessage("Please provide only 'Number' type value for Zip code / Pin.", "color:red;");
						
						return;
					}
					
					cPinLbId.setAttribute("value", pinValue.trim());
					postalAddrStr += pinValue;
				}*/
				
				
				value = cPhoneTbId.getValue().trim();
				if(value != null && value.trim().length() > 0 ) {
//					Long phone = null;
					try {
						//String value = cPhoneTbId.getValue();
						
						
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
							return;
						}
						UserOrganization organization = currentUser != null ? currentUser.getUserOrganization() : null ;
						value =  Utility.phoneParse(value,organization);
						if(value == null || value.trim().length() == 0){

							MessageUtil.setMessage("Please provide valid Phone Number.", "Color:Red", "Top");
						//long phone = Long.parseLong(value);
							return;
							
						}
						cPhoneTbId.setAttribute("value", ""+value);
						postalAddrStr += ", "+value;;
					} catch (Exception e) {
						MessageUtil.setMessage("Please provide valid Phone number.", "color:red", "TOP");
						cPhoneTbId.setFocus(true);
						//onClick$addrSettingsTbBtnId();
						return;
					}
					
					
				}else {
					
					cPhoneTbId.setAttribute("value", ""+value);
					postalAddrStr += value;;
				}
				
				postalAddressRbId.setLabel(postalAddrStr);
				postalAddressDivId.setVisible(true);
				changeAddressDivId.setVisible(false);
				
				onCheck$addrRgId();//it should be called otherwise address wont be updated with modified values.(issue fixing after 2.1.0.1).
				
				if(isModify) {
					
					if(campaign == null || (campaign != null && campaign.getUsers().getUserName().equals(currentUser.getUserName()) ) ) {//if it the logged in user
						
						if (Messagebox.show("Address fields modified successfully. \n Do you want to apply this data to your user-account?", "Prompt", 
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
						
						if(cPhoneTbId.getValue() != null) {
							
							currentUser.setPhone(cPhoneTbId.getValue());
							
						}
						//usersDao.saveOrUpdate(currentUser);
						usersDaoForDML.saveOrUpdate(currentUser);
					}
					
				}
				
				
			}
		}catch (Exception e) {
			logger.error("** Exception while saving the postalAddress--:",e);
		}
	}
	
	/*public void setCancelAddress() {
		try {
			postalAddressDivId.setVisible(true);
			changeAddressDivId.setVisible(false);
		} catch (Exception e) {
			logger.error("** Exception : Error occured while performing cancel operation. **");
		}
	}*/
	
	public void onClick$saveAsDraftBtnIdAtTop() {
		try {
			saveCampSettings(true);
		} catch (Exception e) {
			
			logger.error("Exception ::", e);
		}
	} // onClick$saveAsDraftBtnIdAtTop
	
	public void onClick$saveBtnIdAtTop() {
		try {
			saveCampSettings(false);
		} catch (Exception e) {
			
			logger.error("Exception ::", e);
		}
	} // onClick$saveBtnIdAtTop
	
	public void onClick$saveAsDraftBtnId() {
		try {
			if(campaign!=null) {
			List<CampaignSchedule> campScheduleList = campaignScheduleDao.getByCampaignId(campaign.getCampaignId());
			long activeCount = campScheduleList.stream().filter(campaignSchedule -> campaignSchedule.getStatus() == 0).count(); 
			if(campScheduleList.size() == 0 || activeCount == 0) {
				saveCampSettings(true);
			}else {
				//MessageUtil.setMessage(" A campaign with active schedules cannot be saved as draft. Please delete all active schedules first.", "color:red");
				MessageUtil.setMessage(" A campaign with upcoming schedule/s \n cannot be saved as a draft.\n Please delete all active schedules first.", "color:red");
				return;
			}
			}else {
				saveCampSettings(true);
			}
		} catch (Exception e) {
			
			logger.error("Exception ::", e);
		}
	}
	
	public void onClick$saveBtnId() {
		try {
			//logger.info("campaign...."+campaign);
			if (campaign!=null) {
				Campaigns dbcampaign = campaignsDao.findByCampaignId(campaign.getCampaignId());
				if (campaign.getStatus() != null && !campaign.getStatus().equalsIgnoreCase("Draft") && !campaign.getDraftStatus().equalsIgnoreCase("complete")) {
			if((isEdit == null || isEdit.equalsIgnoreCase("view")) && Utility.isSomethingWrong(campaign, CampaignStepsEnum.CampSett.getPos())){
				MessageUtil.setMessage(OCConstants.CAMPAIGN_MSG, "color:blue;");
				Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST); 
				return ;
			} 
				} 
				else if(dbcampaign != null && !dbcampaign.getStatus().equalsIgnoreCase("Draft") && dbcampaign.getDraftStatus().equalsIgnoreCase("complete")) {
					if((isEdit == null || isEdit.equalsIgnoreCase("view")) && Utility.isSomethingWrong(campaign, CampaignStepsEnum.CampSett.getPos())){
						MessageUtil.setMessage(OCConstants.CAMPAIGN_MSG, "color:blue;");
						Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST); 
						return ;
					} 
				}
			}
			saveCampSettings(false);
		} catch (Exception e) {
			
			logger.error("Exception ::", e);
		}
	} // onClick$saveBtnIdAtTop
	
	private Checkbox orgNameChkBoxId, orgUnitChkBoxId;
	public void saveCampSettings(boolean draft) throws Exception {
		
		
		
		
		String inValidStr = null;
		inValidStr = Utility.validatePh(cSubTb.getValue(), currentUser);
		
		if(inValidStr != null && !inValidStr.equals("GEN_updatePreferenceLink")){
				
				MessageUtil.setMessage("Invalid Placeholder: "+inValidStr, "red");
				return ;
		}else if(inValidStr != null && inValidStr.equals("GEN_updatePreferenceLink")){
				
				MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
				return;
				
		}
		
		
		if(campaign == null)
			campaign = new Campaigns();
			campaign.setUsers(currentUser);
			
		
		if(cNameTbId.isValid()) {
			
			/*if(!Utility.validateName(cNameTbId.getValue())) {
				MessageUtil.setMessage("Enter valid email name. " +
						"Should not use any special characters.","color:red","TOP");
				return;
			}
			else*/ if(!nameExist){
				campaignName = Utility.condense(cNameTbId.getValue());
				campaign.setCampaignName(campaignName);
			}
			else{
				MessageUtil.setMessage("Email name entered is already in use. " +
						"Please choose a different name.", "color:red", "TOP");
				cNameTbId.setFocus(true);
				return;
			}
			
		}
		else{
			MessageUtil.setMessage("Provide valid email name.", "color:red", "TOP");
			cNameTbId.setFocus(true);
			return;
		}
		
		if(cSubTb.isValid() && !cSubTb.getValue().isEmpty()){
			campaign.setSubject(cSubTb.getValue());
		}
		else{
			MessageUtil.setMessage("Please provide subject. Subject should not be left empty.", "color:red", "TOP");
			cSubTb.setFocus(true);
			return;
		}
		
		if(cFromNameTb.isValid() && Utility.validateFromName(cFromNameTb.getValue())){
			campaign.setFromName(cFromNameTb.getValue());
		}
		else{
			MessageUtil.setMessage("Provide valid 'From Name'. Special characters are not allowed.", "color:red", "TOP");
			cFromNameTb.setFocus(true);
			return;
		}
		
		if(cFromEmailCb.getSelectedItem().getLabel().indexOf('@')<0) {
			MessageUtil.setMessage("Register a 'From Email' to create an email.",
					"color:red", "TOP");
		}
		else if(!(cFromEmailCb.getSelectedIndex()==-1)) {
			campaign.setFromEmail(cFromEmailCb.getSelectedItem().getLabel());
		}
		else {
			MessageUtil.setMessage("Provide valid 'From Email Address'.", "color:red", "TOP");
			cFromEmailCb.setFocus(true);
			return;
		}
		
		campaign.setReplyEmail(cReplyEmailCb.getSelectedItem().getLabel());
		
		// Added for campaign categories
		if(currentUser.getSubscriptionEnable()){
			if(! categoryLbId.isDisabled()) {
				
				if(categoryLbId.getSelectedItem().getLabel().equals("Select Category") || categoryLbId.getSelectedItem().getLabel().isEmpty() ){
					
					MessageUtil.setMessage("Please select campaign category.", "color:red", "TOP");
					return;
					
				}
				
			}
		}
		Long categoryName =null;
		Listitem catgoryItem = categoryLbId.getSelectedItem();
		
		if(catgoryItem.getIndex() != 0) {
			UserCampaignCategories userCampaignCategories = (UserCampaignCategories)catgoryItem.getValue();
			categoryName =userCampaignCategories.getId();
		}
	
		campaign.setCategories(categoryName);
		
		
	
		
		
	/*	if(cCategoryCb.getSelectedIndex() == 0 ) {
			MessageUtil.setMessage("Please select the email category.", "color:red", "TOP");
			cCategoryCb.setFocus(true);
			return;
		}*/
		// campaign.setCategoryWeight((Short)cCategoryCb.getSelectedItem().getValue());
		campaign.setCategoryWeight((short)1); // TODO by default selecting 'others ' category
		
		//******************BEGIN FOR ADDRESS SETTINGS**********************************************************
		
		
		//campaign.setCustomizeFooter((String)customFooterId.getSelectedIndex());
		if(customFooterId.isChecked() == true) {
			campaign.setCustomizeFooter(true);
			addrRgId.setSelectedIndex(0);
			onCheck$addrRgId();
		}else {
			campaign.setCustomizeFooter(false);
		}
		
		if(cAddressCbId.isChecked()) {
			campaign.setAddressFlag(true);
			if(addrRgId.getSelectedIndex() == 3 && storesLbId.getItemCount() == 0) {
				MessageUtil.setMessage("No organization stores exist. Please choose another sender address option.", "color:red;");
				return;
			}
			Address address = (Address)addrRgId.getAttribute("campaignAddress");
			campaign.setAddress(address);
			campaign.setAddrsType((String)addrRgId.getAttribute("campAddressType"));
		}else{
			campaign.setAddressFlag(false);
		}
		
		
		String includeBfrStr = "";
		campaign.setIncludeOrg(false);
		campaign.setIncludeOrgUnit(false);
		
		if(orgNameChkBoxId.isChecked()) {
			
			campaign.setIncludeOrg(true);
			if(includeBfrStr.trim().length()>0) includeBfrStr+=Constants.ADDR_COL_DELIMETER;
			includeBfrStr += currentUser.getUserOrganization().getOrganizationName();
			
		}
		if(orgUnitChkBoxId.isChecked()) {
			
			campaign.setIncludeOrgUnit(true);
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
			
			
			
		}
		campaign.setIncludeBeforeStr(includeBfrStr);
		
		
		
		
		//************************END FOR ADDRESS SETTINGS************************************************
		if(cPermRemRb.getSelectedIndex() == 0){
			if(!permRemTextId.getValue().trim().equals("")){
				campaign.setPermissionRemainderFlag(true);
				campaign.setPermissionRemainderText(permRemTextId.getValue());
			}
		}else{
			campaign.setPermissionRemainderFlag(false);
		}
		if(cPreviewTextCb.isChecked()) {
			campaign.setHasPreviewText(true);
			String preText = cPreviewTextTb.getValue().isEmpty() ? null : cPreviewTextTb.getValue().trim();
			campaign.setPreviewText(preText);
		}else {
			campaign.setHasPreviewText(false);
		}
		
		if(cWebPageCb.isChecked()){
			campaign.setWebLinkFlag(true);
			campaign.setWebLinkText(cWebLinkTextTb.getValue());
			if(!cWebLinkUrlTextTb.getValue().equals("")){
				campaign.setWebLinkUrlText(cWebLinkUrlTextTb.getValue());
			}else{
				MessageUtil.setMessage("Provide web-link Url text.", "color:red", "TOP");
				cWebLinkUrlTextTb.setFocus(true);
				return;
			}//3431
			campaign.setAlignmentFlag(alignmentRbId.getSelectedItem().getLabel().toLowerCase());

		}else{
			campaign.setWebLinkFlag(false);
			if(alignmentRbId.getSelectedItem() != null)
			campaign.setAlignmentFlag(alignmentRbId.getSelectedItem().getLabel().toLowerCase());
		else
			campaign.setAlignmentFlag(Constants.ALIGNMENT_CENTER);
//should be center without alignment */
		//	campaign.setAlignmentFlag(Constants.ALIGNMENT_CENTER);// default value should be center not null.
		}
			
		if(toNameChkId.isChecked()){
			campaign.setPersonalizeTo(true);
			if(phLbId.getSelectedIndex()>0){
				campaign.setToName((String)phLbId.getSelectedItem().getValue());
			}else{
				phLbId.setSelectedIndex(0);
				campaign.setToName((String)phLbId.getSelectedItem().getValue());
			}
		}else{
			campaign.setPersonalizeTo(false);
		}
		if(downloadPdf.isChecked()) {
			campaign.setDownloadPdf(true);
		}else {
			campaign.setDownloadPdf(false);
		}
		
		//Google Analytics settings
//		if(gaChekbxId.isChecked()){
//			campaign.setGoogleAnalytics(true);
//			
//			if(gaCampTitleTxtbxId.getValue().trim().isEmpty()){
//				MessageUtil.setMessage("Please provide Google Analytics campaign title", "blue");
//				return;
//			}
//			campaign.setGoogleAnalyticsCampTitle(gaCampTitleTxtbxId.getValue().trim());
//		}
//		else{
//			campaign.setGoogleAnalytics(false);
//		}
		
		if(gaChekbxId.isChecked()){
			campaign.setGoogleAnalytics(true);
			
			if(gaCampTitleTxtbxId.isValid() && !gaCampTitleTxtbxId.getValue().isEmpty()){
				campaign.setGoogleAnalyticsCampTitle(gaCampTitleTxtbxId.getValue().trim());
			}
			else{
				//MessageUtil.setMessage("Please provide Google Analytics campaign title", "color:red", "TOP");
				campaign.setGoogleAnalytics(false);
				return;
			}
		}
		
		//--------------------------
		
		MessageUtil.clearMessage();
		if(isEdit==null){
			campaign.setStatus("Draft");
			campaign.setStatusChangedOn(Calendar.getInstance());
			campaign.setCreatedDate(MyCalendar.getNewCalendar());
			
			campaign.setModifiedDate(MyCalendar.getNewCalendar());
			
			
			if((campaign.getDraftStatus()==null) || 
					CampaignStepsEnum.CampSett.getPos() >= CampaignStepsEnum.valueOf(campaign.getDraftStatus()).getPos()) {
				
				campaign.setDraftStatus("CampMlist");
			} //
			
			
		}else
			campaign.setModifiedDate(Calendar.getInstance());
		

		try{
			if(session!=null || currentUser != null){
				
				/*campaign.setUsers(user);*/
				//campaignsDao.saveOrUpdate(campaign);
				
				campaignsDaoForDML.saveOrUpdate(campaign);
				
				
//				String activityMessage = "Creating new Email, Email settings page completed for campaign : "+ campaign.getCampaignName();
				
				ActivityEnum tempActivity = ActivityEnum.CAMP_CRE_SETT_p1campaignName;
				
				if(isEdit==null){
					Campaigns campaignNew = campaignsDao.getSingleCampaign(campaignName, currentUser.getUserId());
					if(campaign!=null)
						session.setAttribute("campaign", campaignNew);
					else
						session.setAttribute("campaign", campaign);
				}else{
					session.setAttribute("campaign", campaign);
//					activityMessage = "Edited email settings for campaign : "+ campaign.getCampaignName();
					tempActivity = ActivityEnum.CAMP_EDIT_SETT_p1campaignName;
				}
				
				
				UserActivitiesDao userActivitiesDao = (UserActivitiesDao)
										SpringUtil.getBean("userActivitiesDao");
				UserActivitiesDaoForDML userActivitiesDaoForDML = (UserActivitiesDaoForDML)
						SpringUtil.getBean("userActivitiesDaoForDML");
				/*if(userActivitiesDao != null) {
					userActivitiesDao.addToActivityList(tempActivity, currentUser, campaign.getCampaignName());
				}*/
				if(userActivitiesDaoForDML != null) {
					userActivitiesDaoForDML.addToActivityList(tempActivity, currentUser, campaign.getCampaignName());
				}
				
				PageUtil.setFromPage("campaign/CampSettings");
				if(isEdit!=null){
					if(isEdit.equalsIgnoreCase("edit")){
						Redirect.goTo(PageListEnum.CAMPAIGN_FINAL);
					}
					else if(isEdit.equalsIgnoreCase("view")){
						Redirect.goTo(PageListEnum.CAMPAIGN_MLIST);
					}
				}else{
					session.removeAttribute("editCampaign");					
					if(!draft){
						Redirect.goTo(PageListEnum.CAMPAIGN_MLIST);
					}else{
						session.removeAttribute("campaign");
						//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW);
						Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
					}
				}
			}else{
				MessageUtil.setMessage("Problem experienced while saving. Please re-login and try again.", "color:red", "TOP");
				logger.error("** Exception : session is null ** ");
			}
		}catch (DataIntegrityViolationException dive) {
			logger.error("** Exception: Data Integrity Violation while saving the Email Settings: " + dive + " **");
		}catch (Exception e) {
			logger.error("** Exception: Problem while saving the Email Settings: " + e + " **");
		}
		
		
			
	} // saveCampSettings
	
	public void loadSettings() {
		if(isEdit==null){
			if(campaign==null){
				if(currentUser.getAddressOne() == null  || 
						currentUser.getCity() == null   || 
						currentUser.getState() == null   || 
						currentUser.getCountry() == null ){ 
						//currentUser.getPinCode() == null ) {
					try {
						setAddress(false);
					} catch (Exception e) {
						
						logger.error("Exception ::", e);
					}
				}
				else if(currentUser.getAddressOne().isEmpty()  || 
						currentUser.getCity().isEmpty() || 
						currentUser.getState().isEmpty() || 
						currentUser.getCountry().isEmpty() ){
						//currentUser.getPinCode().isEmpty()) {
					try {
						setAddress(false);
					} catch (Exception e) {
						
						logger.error("Exception ::", e);
					}
				}
				setDefaultName();
			}
		}
		
		/*Long userId = null;
		Users user = null;
		
		if(campaign == null) {
			
			userId = currentUser.getUserId();
			user = currentUser;
			
		}else if (isEdit != null && campaign != null) {
			
			user = campaign.getUsers();
			userId = user.getUserId();
		}
		*/
		List<UserFromEmailId> userFromEmailIdList = userFromEmailIdDao.getEmailsByUserId(currentUser.getUserId());
		logger.debug("List size : " + userFromEmailIdList.size());
		
		if(currentUser!=null && currentUser.getEmailId()!=null) {
			cFromEmailCb.appendItem(currentUser.getEmailId());
			cReplyEmailCb.appendItem(currentUser.getEmailId());
		}
		if(userFromEmailIdList.size()>0) {
			for(Object obj:userFromEmailIdList) {
				logger.debug("obj : " + obj);
				UserFromEmailId userFromEmailId = (UserFromEmailId)obj;
				logger.debug("userFromEmailId : " + userFromEmailId);
				cFromEmailCb.appendItem(userFromEmailId.getEmailId());
				cReplyEmailCb.appendItem(userFromEmailId.getEmailId());
			}
		} 
		
		if(!(cFromEmailCb.getItemCount()>0)) {
			cFromEmailCb.appendItem("No emails registered.");
			cReplyEmailCb.appendItem("No emails registered.");
		}
		cFromEmailCb.setSelectedIndex(0);
		
		cReplyEmailCb.setSelectedIndex(0);

		//TODO 
		//Email Categories
		Set<String> keySet = PropertyUtil.emailCategoriesMap.keySet();
		String keyStr;
		Comboitem ci;
		
		Comboitem othersCi = new Comboitem("Others");
		ci = new Comboitem("-- select category--");
		ci.setValue(new Short((short)0));
		cCategoryCb.appendChild(ci);
		cCategoryCb.setSelectedIndex(0);
		
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			
			keyStr = iterator.next();
			if(keyStr.equalsIgnoreCase("others")) {
				othersCi.setValue(PropertyUtil.emailCategoriesMap.get(keyStr));
			}else {
				ci = new Comboitem(keyStr);
				ci.setValue(PropertyUtil.emailCategoriesMap.get(keyStr));
			}
			cCategoryCb.appendChild(ci);
		}
		// add 'Others' last 
		cCategoryCb.appendChild(othersCi);
		//End of Email categories
		
		if(campaign!=null){
			backBtnId.setVisible(true);
			cNameTbId.setValue(campaign.getCampaignName());
			cNameTbId.setDisabled(true);
			cSubTb.setValue(campaign.getSubject());
			cFromNameTb.setValue(campaign.getFromName());
			
			// Added for campaign categories
			if(!currentUser.getSubscriptionEnable())categoryLbId.setDisabled(true);
			Long category = campaign.getCategories();
			UserCampaignCategories userCmapObj = null;
			if(category != null){
				
				userCmapObj =userCampaignCategoriesDao.findByCatId(category, currentUser.getUserId());
			}
			
			String categoryName ="";
			if(userCmapObj != null){
				 categoryName = userCmapObj.getCategoryName();
				 for(Listitem item : categoryLbId.getItems()) {
					 
					 if(item.getLabel().equals(categoryName)) {
						 item.setSelected(true);
						 break;
					 }
					 
					 
				 }
				 
			}
			//logger.info("cat name "+categoryName);
			
			
		
			String fromEmailId = campaign.getFromEmail();
			for(int index=0;index<cFromEmailCb.getItemCount();index++) {
				
				logger.debug(cFromEmailCb.getItemAtIndex(index).getLabel() + " == " + fromEmailId);
			   if(cFromEmailCb.getItemAtIndex(index).getLabel().equals(fromEmailId)) {
				   cFromEmailCb.setSelectedIndex(index);
			   }  
			   if(cReplyEmailCb.getItemAtIndex(index).getLabel().equals(campaign.getReplyEmail())) {
				   cReplyEmailCb.setSelectedIndex(index);
			   }  
			}
			
			short catWeight = campaign.getCategoryWeight();
			for(int index =0; index<cCategoryCb.getItemCount();index++) {
				if((Short)cCategoryCb.getItemAtIndex(index).getValue() == catWeight) {
					cCategoryCb.setSelectedIndex(index);
					break;
				}
			}
			
			
			gaChekbxId.setChecked(campaign.isGoogleAnalytics());
			if(campaign.isGoogleAnalytics()){
				gaTitleDivId.setVisible(true);
				String gaCampTitle = campaign.getGoogleAnalyticsCampTitle();
				if(gaCampTitle != null && gaCampTitle.trim().length() > 0){
					gaCampTitleTxtbxId.setValue(gaCampTitle);
				}
			}
			
			
			cPermRemRb.setSelectedIndex(campaign.isPermissionRemainderFlag()?0:1);
			if(campaign.isPermissionRemainderFlag()){
				permRemDivId.setVisible(true);
				permRemTextId.setValue(campaign.getPermissionRemainderText());
			}else{
				
				permRemDivId.setVisible(false);
			}
			cPreviewTextCb.setChecked(campaign.isHasPreviewText());
			if(campaign.isHasPreviewText()) {
				cPreviewTextHboxId.setVisible(true);
				String preText = (campaign.getPreviewText()==null && campaign.getPreviewText().isEmpty())? "" :campaign.getPreviewText();
				cPreviewTextTb.setValue(preText);
			}else {
				cPreviewTextHboxId.setVisible(false);
			}
			cWebPageCb.setChecked(campaign.isWebLinkFlag());
			if(campaign.isWebLinkFlag()){
				cWebLinkHboxId.setVisible(true);
				cWebLinkTextTb.setValue(campaign.getWebLinkText());
				cWebLinkUrlTextTb.setValue(campaign.getWebLinkUrlText());
				if(campaign.getAlignmentFlag() == null)
					alignmentRbId.setSelectedIndex(1);
				else { 
				alignmentRbId.setSelectedIndex(campaign.getAlignmentFlag().equalsIgnoreCase("left") ? 0 :campaign.getAlignmentFlag().equalsIgnoreCase("right")? 2 : 1 );
				}
			}else{
				cWebLinkHboxId.setVisible(false);
				
			}
			toNameChkId.setChecked(campaign.isPersonalizeTo());
			if(campaign.isPersonalizeTo()){
				persToDivId.setVisible(true);
				String to = campaign.getToName();
				for(int i=0;i<phArray.length;i++){
					if(phArray[i].equalsIgnoreCase(to)){
						phLbId.setSelectedIndex(i);
					}
				}
			}else{
				persToDivId.setVisible(false);
				
			}
			
			orgNameChkBoxId.setChecked(campaign.isIncludeOrg());
			orgUnitChkBoxId.setChecked(campaign.isIncludeOrgUnit());
			
			cAddressCbId.setChecked(campaign.isAddressFlag());
			if(campaign.isAddressFlag()){
				
				//addrRgId.setSelectedIndex(0);
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
				
				
				String addreType = campaign.getAddrsType();
				try {
					Address address = campaign.getAddress();
					
					if(address.getDinamicAddrstr() != null && (addreType != null && 
							(addreType.equals(Constants.CAMP_ADDRESS_TYPE_CONTACT_HOME_STORE) || 
								addreType.equals(Constants.CAMP_ADDRESS_TYPE_CONTACT_LAST_PURCHASED_STORE)	))) {
						
						if(address.getDinamicAddrstr().equals(Constants.PH_CAMPAIGN_ADDRESS_CONTACT_HOMESTORE )){
							//logger.debug("-----------2-------------");
							addrRgId.setSelectedIndex(1);
							
						}
						else if(address.getDinamicAddrstr().equals(Constants.PH_CAMPAIGN_ADDRESS_CONTACT_LAST_PURCHASED_STORE)) {
							addrRgId.setSelectedIndex(2);
						}
						onCheck$addrRgId();
						
					}else {
						
						if(addreType != null && addreType.equals(Constants.CAMP_ADDRESS_TYPE_USER)) {
							//logger.debug("-----------4-------------");
							addrRgId.setSelectedIndex(0);
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
									cPinLbId.setAttribute("value", address.getPin()+"");
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
							
							addrRgId.setSelectedIndex(0);
							onCheck$addrRgId();
							
							
						}
						else if(addreType != null && addreType.startsWith(Constants.CAMP_ADDRESS_TYPE_STORE)) {
						storesLbId.setSelectedIndex(getStore(addreType.split("\\|")[1].trim()));
						onSelect$storesLbId();
						
					}
						
					}
					
					onCheck$customFooterId();
				} catch (WrongValueException e) {
					logger.error("Exception : " + e);
				}	
			}
			
		}else{
			try {
				//logger.debug("-----------6-------------");
				addrRgId.setSelectedIndex(0);
				
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
						cPinLbId.setValue(currentUser.getPinCode() == null ? "" : currentUser.getPinCode() );
					  cPinLbId.setAttribute("value", currentUser.getPinCode() == null ? "" : currentUser.getPinCode());
						
					  
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
				//logger.debug("-----------7-------------");
				onCheck$addrRgId();
			}catch(Exception e){
				logger.error("** Exception: Problem occured while setting updating user information . "+e+" **");
			}

		}
		
		
		if(isEdit!=null && isEdit.equalsIgnoreCase("edit")) {
			saveAsDraftBtnId.setVisible(false);
			//saveAsDraftBtnIdAtTop.setVisible(false);
			saveBtnId.setLabel("Save");
			//saveBtnIdAtTop.setLabel("Save");
			backBtnId.setVisible(true);
		}else {
			saveAsDraftBtnId.setVisible(true);
			//saveAsDraftBtnIdAtTop.setVisible(true);
			saveBtnId.setLabel("Next");
			//saveBtnIdAtTop.setLabel("Next");
			backBtnId.setVisible(false);
		}
		if(campaign!=null) {
		downloadPdf.setChecked(campaign.isDownloadPdf());
		}
		
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
		
		
		
		
		
	}
	
	
	
	public void onBlur$cNameTbId() {
		try {
			checkEmailName(cNameTbId,nameStatusLblId);
		} catch (Exception e) {
			logger.error("Exception :: error getting from the checkEmailName method ");
		}
	}
	
	public void checkEmailName(Textbox cNameTbId, Label nameStatusLblId) throws Exception{
		String emailName = Utility.condense(cNameTbId.getValue().trim());
		
		if(emailName.trim().equals("")){
			nameStatusLblId.setValue("");
			return;
		}/*else if(!Utility.validateName(emailName)){
			nameStatusLblId.setStyle("color:red");
			nameStatusLblId.setValue("Special characters not allowed");
			return;
		}*/
		MessageUtil.clearMessage();
		CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
		nameExist =  campaignsDao.checkName(emailName,currentUser.getUserId());
		if(nameExist){
			nameStatusLblId.setStyle("color:red");
			nameStatusLblId.setValue("Already exists");
		}else{
			nameStatusLblId.setStyle("color:#023849");
			nameStatusLblId.setValue("Available");
//			cNameTbId.setValue(emailName);
		}
	}
	
	private Textbox cFromEmailTb;
	
	
	
	public void onClick$regRepToEmlAnchId() {
		
		regEmailPopupId.setAttribute("flagStr", "Reply-Email");
	}
	
	
	public void onClick$regFrmEmlAnchId() {
		
		regEmailPopupId.setAttribute("flagStr", "From-Email");
	}
	
	public void onClick$submitBtnId() {
		try {
			
			registerNewFromEmail(cFromEmailTb,(String)regEmailPopupId.getAttribute("flagStr"));
		} catch (Exception e) {
			
			logger.error("Exception ::", e);
		}
	}
	
	/*
	 * added to facilitate the choice of reply 'To' email ids
	 */
	
	//private Textbox cRepToEmailTb;
	/*public void onClick$repToSubmitBtnId() {
		
		try {
			registerNewFromEmail(cFromEmailTb);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("**, Exception while registering the reply to email id for the user.",e);
			
		}
		
		
		
		
	}//onClick$repToSubmitBtnId()
	*/
	
	Popup help,help1;
	public void onFocus$cSubTb() {
		
		//cSubTb.setPopup(help);
		help.open(cSubTb, "end_after");
		
	}
	
	
	private Popup regRepToEmailPopupId;
	public void registerNewRepToEmail(Textbox cRepToEmailTb) throws Exception {
		
		String newRepEmail = cRepToEmailTb.getValue();
		
		if(newRepEmail.trim().equals("")) {
			
			regRepToEmailPopupId.close();
			MessageUtil.setMessage("Email field cannot be left empty.", "color:red", "TOP");
			return;
			
		}//if
		if(!Utility.validateEmail(newRepEmail.trim())) {
			regEmailPopupId.close();
			MessageUtil.setMessage("Please enter a valid email address.", "color:red", "TOP");
			return;
	 	}
		
		if(newRepEmail.equalsIgnoreCase(currentUser.getEmailId())) {
			MessageUtil.setMessage("Given email address already exists.", "color:red", "TOP");
			return;
		}
		
		//need to create new POJO?
		
	}//registerNewRepToEmail
	
	public void onClick$cancelBtnId() {
		
		regEmailPopupId.close();
		
	}
	
	public void registerNewFromEmail(Textbox cFromEmailTb,String flagStr) throws Exception {
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
			EmailQueueDao emailQueueDao = (EmailQueueDao)SpringUtil.getBean("emailQueueDao");
			UserFromEmailId userFromEmailId =  userFromEmailIdDao.checkEmailId(newFromEmail, currentUser.getUserId());
			String confirmationURL = PropertyUtil.getPropertyValue("confirmationURL") + "?requestedAction=fromEmail&userId=" + currentUser.getUserId() + "&email=" + newFromEmail;
			if(userFromEmailId == null) {
				userFromEmailId = new UserFromEmailId(currentUser, newFromEmail, 0);
				//userFromEmailIdDao.saveOrUpdate(userFromEmailId);
				userFromEmailIdDaoForDML.saveOrUpdate(userFromEmailId);

				addVerificationMailToQueue(flagStr, confirmationURL, newFromEmail);
				
				/*String emailStr = "Hi " + user.getFirstName() + ",<br/> You recently added a new email address  to your "+flagStr+".<br/>" 
						+ " To verify that you own this email address, simply click on the link below :<br/> " 
						+ "  <a href='"+ confirmationURL +"'>"+ confirmationURL + "</a>" 
						+ "<br/><br/> If you can't click the link, you can verify your email address by copy/paste(or typing) "
						+ "the above URL address into the browser.<br/>Regards,<br/>The Captiway Team";
				EmailQueue emailQueue = new EmailQueue("Captiway - Register new "+flagStr,emailStr, Constants.EQ_TYPE_USER_MAIL_VERIFY,"Active",newFromEmail,MyCalendar.getNewCalendar(),user);
			 	emailQueueDao.saveOrUpdate(emailQueue);
				regEmailPopupId.close();
				MessageUtil.clearMessage();
				Messagebox.show("We have sent you a confirmation email to "+newFromEmail+".\n Follow the instructions in the email and this new "+flagStr+" will be verified.");
				cFromEmailTb.setValue("");*/
			} else if(userFromEmailId.getStatus() == 0) {
				
				//MessageUtil.setMessage("Given email ID approval is pending.", "color:red", "TOP");
				 try {
					int confirm = Messagebox.show("The given email address is pending for approval. Do you want to resend the verification?","Send Verification ?",
						 		Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != 1) {
						regEmailPopupId.close();
						cFromEmailTb.setValue("");
						return ;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::", e);
				}
				
				addVerificationMailToQueue(flagStr, confirmationURL, newFromEmail);
				
				/*String emailStr = "Hi " + user.getFirstName() + ",<br/> You recently added a new email address  to your "+flagStr+".<br/>" 
				+ " To verify that you own this email address, simply click on the link below :<br/> " 
				+ "  <a href='"+ confirmationURL +"'>"+ confirmationURL + "</a>" 
				+ "<br/><br/> If you can't click the link, you can verify your email address by copy/paste(or typing) "
				+ "the above URL address into the browser.<br/>Regards,<br/>The Captiway Team";
				EmailQueue emailQueue = new EmailQueue("Captiway - Register new "+flagStr,emailStr, Constants.EQ_TYPE_USER_MAIL_VERIFY,"Active",newFromEmail,MyCalendar.getNewCalendar(),user);
			 	emailQueueDao.saveOrUpdate(emailQueue);
				regEmailPopupId.close();
				MessageUtil.clearMessage();
				Messagebox.show("We have sent you a confirmation email to "+newFromEmail+".\n Follow the instructions in the email and this new "+flagStr+" will be verified.");
				cFromEmailTb.setValue("");
				*/
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
			/*EmailQueue emailQueue = new EmailQueue(brandStr+" - Register new "+flagStr,emailStr, Constants.EQ_TYPE_USER_MAIL_VERIFY,"Active",newFromEmail,MyCalendar.getNewCalendar(),currentUser);
			emailQueueDao.saveOrUpdate(emailQueue);*/
			regEmailPopupId.close();
			MessageUtil.clearMessage();
			Messagebox.show("We have sent you a confirmation email to "+newFromEmail+".\n " +
					"Follow the instructions in the email and this new "+flagStr+" will be verified.", "Register email", Messagebox.OK, Messagebox.INFORMATION);
			cFromEmailTb.setValue("");
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}catch (Exception e) {

		logger.error("** Exception while sending verification",e);
		
		
		}
		
		
	}
	
	
	public void closeCampSettings(){
		PageUtil.setFromPage("campaign/CampSettings");
		Redirect.goTo(PageListEnum.RM_HOME);
	}
	
	public void onClick$backBtnId() {
		try {
			back();
		} catch (Exception e) {
			logger.error("Exception :: error getting from the back method");
		}
	}
	public void back() throws Exception{
		if(isEdit!=null){
			Redirect.goTo(PageListEnum.CAMPAIGN_FINAL);
		}else{
			Redirect.goTo(PageListEnum.RM_HOME);
		}
	}
	
	public void upDateCursorPosition() {
		logger.debug("campsettingcontroller.updateCursorPosition");
	}
	
	
	public class phListRenderer implements ListitemRenderer {

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
	
	public void onClick$createNewTemplateId() throws ParseException {
		    session.setAttribute("redirectForFooterFormCampaign", true);
			ManageAutoEmailsControllerBee manageAutoEmailsController = new ManageAutoEmailsControllerBee();
			manageAutoEmailsController.onClick$createNewTemplateId();
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
			changeAddressId.setDisabled(true);
		}else {
			postalAddressRbId.setDisabled(false);
			homeStoreAddressDetailsId.setDisabled(false);
			lastPurchaseStoreId.setDisabled(false);
			storeAddressId.setDisabled(false);
			orgNameChkBoxId.setDisabled(false);
			orgUnitChkBoxId.setDisabled(false);
			storesLbId.setDisabled(false);
			changeAddressId.setDisabled(false);
		}
	}
	
}
