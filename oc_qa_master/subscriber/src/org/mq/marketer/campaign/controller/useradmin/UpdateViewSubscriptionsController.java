package org.mq.marketer.campaign.controller.useradmin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.catalina.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.SMSSuppressedContacts;
import org.mq.marketer.campaign.beans.Unsubscribes;
import org.mq.marketer.campaign.beans.UserCampaignCategories;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CampaignSentDao;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDao;
import org.mq.marketer.campaign.dao.UnsubscribesDao;
import org.mq.marketer.campaign.dao.UserCampaignCategoriesDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;

public class UpdateViewSubscriptionsController extends GenericForwardComposer implements EventListener {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private ContactsDao contactsDao;
	private UserCampaignCategoriesDao userCampaignCategoriesDao;
	private CampaignSentDao campaignSentDao;
	private UnsubscribesDao unsubscribesDao;
	private OrganizationStoresDao organizationStoresDao;
	private SMSSuppressedContactsDao smsSuppressedContactsDao;
	//private Users currentUser;
	
	
	public UpdateViewSubscriptionsController(){
		
		contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
		userCampaignCategoriesDao = (UserCampaignCategoriesDao)SpringUtil.getBean("userCampaignCategoriesDao");
		campaignSentDao = (CampaignSentDao)SpringUtil.getBean("campaignSentDao");
		unsubscribesDao = (UnsubscribesDao)SpringUtil.getBean("unsubscribesDao");
		organizationStoresDao = (OrganizationStoresDao)SpringUtil.getBean("organizationStoresDao");
		smsSuppressedContactsDao = (SMSSuppressedContactsDao)SpringUtil.getBean("smsSuppressedContactsDao");
		
		contactOwner =GetUser.getUserObj();
		
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
     	PageUtil.setHeader("Subscriber Preference Center ","",style,true);
		
		
	}
	

	private Textbox fnameTxtId,lnameTxtId,emailTxtId,mblTxtId,pinTxtId,emailTxtBoxId,mblTxtBoxId,postalTxtBoxId;
	private Radio allRgId,weekRgId,mnthRgId,quarterRgId,weekSmsRgId,mnthSmsRgId,quarterSmsRgId,allSmsRgId;
	private Label createDateLblId,sourceLblId;
	private A modifyAId,unsubsAId;
	private A modifySmsAId,optoutAId,modifyAddrAId;
	private Button saveBtnId;
	
	//private Contacts contact;
	private Long cid;
	private Long sentId;
	private Users contactOwner;
	private long userId;
	private Textbox addrTxtBoxId,cityTxtBoxId,stateTxtBoxId,countryTxtBoxId,pinTxtBoxId;
	
	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
		
		Object componet = event.getTarget();
		if(componet instanceof Checkbox) {
			
			Checkbox targetChkBox = (Checkbox)componet;
			Div childCompDiv = (Div)targetChkBox.getAttribute("nextChild");
			if(childCompDiv != null) {
				
				
				childCompDiv.setVisible(targetChkBox.isChecked());
				
			}
			
			
			
		}
		
		
		
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		
		 userId=contactOwner.getUserId();
		 saveBtnId.setDisabled(true);
		 editAId.setDisabled(true);
		
		
		String createdDate= MyCalendar.calendarToString(contactOwner.getCreatedDate(), MyCalendar.FORMAT_MDATEONLY);
		createDateLblId.setValue(createdDate+",");
		
		
		String emailId= contactOwner.getEmailId();
		String phone=contactOwner.getPhone();
		String zip =contactOwner.getPinCode();
		fnameTxtId.setValue(contactOwner.getFirstName()!= null ? contactOwner.getFirstName() : "" );
		lnameTxtId.setValue(contactOwner.getLastName() != null ?contactOwner.getLastName() :"" );
		emailTxtId.setValue(contactOwner.getEmailId() != null ?contactOwner.getEmailId() :"" );
		mblTxtId.setValue(phone != null ? phone : "" );
		pinTxtId.setValue(zip!= null ? zip :"" );
		
		
		
		/**/
		
		if(emailId != null ){
			emailTxtBoxId.setValue(emailId);
		}
		
		if(phone != null){
			mblTxtBoxId.setValue(phone);
		}
		
		addrTxtBoxId.setValue(contactOwner.getAddressOne() == null ||contactOwner.getAddressOne().isEmpty() ? "Enter Street" :contactOwner.getAddressOne());
		cityTxtBoxId.setValue(contactOwner.getCity() == null || contactOwner.getCity().isEmpty() ?"Enter city" : contactOwner.getCity() );
		stateTxtBoxId.setValue(contactOwner.getState() == null|| contactOwner.getState().isEmpty() ?"Enter State" :contactOwner.getState() );
		countryTxtBoxId.setValue(contactOwner.getCountry() == null || contactOwner.getCountry().isEmpty() ?"Enter Country" : contactOwner.getCountry());
		pinTxtBoxId.setValue(contactOwner.getPinCode() == null || contactOwner.getPinCode().isEmpty() ? "Enter ZIP" :contactOwner.getPinCode() );
		
		
		 createdivs(contactOwner);
		 
		 allRgId.setSelected(true);
		 allSmsRgId.setSelected(true);
		 			
		
		
	}
	private Div categoriesDivId,pesrinalisedDivId,storeDivId,noteDivId;
	private A subsEmailsId,transEmailsId;
	
		
	public void createdivs(Users user){
		
		List<UserCampaignCategories> userCampList = userCampaignCategoriesDao.findCatByUserId(user.getUserId());
		if(userCampList == null  || userCampList.size() ==0 ){
			categoriesDivId.setVisible(false);
			return;
		}
		logger.debug("size is"+userCampList.size());
		/*for (UserCampaignCategories userCampaignCategories : userCampList) {
			
			Div rootDiv= new Div();
			
			// check box
			
			Checkbox catChkBox = new Checkbox(userCampaignCategories.getCategoryName());
			catChkBox.addEventListener("onCheck", this);
			catChkBox.setStyle("font-size:12px; color:#333333; vertical-align:top;");
		
		
			
			//catChkBox.setId(nameStr.toLowerCase()+"Id");
			catChkBox.setChecked(true);
			catChkBox.setValue(userCampaignCategories);
			catChkBox.setParent(rootDiv);
			
			
			// Description label
			Label descLabel = new Label("- "+userCampaignCategories.getDescription());
			descLabel.setStyle("color:#999999; padding-left:8px; ");
			descLabel.setParent(rootDiv);
			
			if(userCampaignCategories.getCategoryName().equalsIgnoreCase(Constants.CAMP_CATEGORY_PERSONAL)){
				
				pesrinalisedDivId.setVisible(true);
				pesrinalisedDivId.setParent(rootDiv);
				
				catChkBox.setAttribute("nextChild", pesrinalisedDivId);
				
				
				getaDays();
				getaMonths();
				getaYears();
				getbDays();
				getbMonths();
				getbYears();
			}
			if(userCampaignCategories.getCategoryName().equalsIgnoreCase(Constants.CAMP_CATEGORY_EVENTS)){
				storeDivId.setVisible(true);
				getStroeIds(user.getUserOrganization().getUserOrgId());
				storeDivId.setParent(rootDiv);
				
				catChkBox.setAttribute("nextChild", storeDivId);
				
			}
			
			
			rootDiv.setParent(categoriesDivId);
			
		}*/
		
		
		
		Map<Long, List<UserCampaignCategories>> postioningMap = new HashMap<Long, List<UserCampaignCategories>>();
		List<UserCampaignCategories> userCampCatList = null;
		
		for (UserCampaignCategories userCampCatObj : userCampList) {
			if(userCampCatObj.getType().equals("Default") || userCampCatObj.getType().equals("CP")){
				if(postioningMap.containsKey(userCampCatObj.getId())){
					userCampCatList = postioningMap.get(userCampCatObj.getId());
					if(userCampCatList == null ) userCampCatList = new ArrayList<UserCampaignCategories>();
				}else {
					userCampCatList = new ArrayList<UserCampaignCategories>();
				}
				userCampCatList.add(userCampCatObj);
				postioningMap.put(userCampCatObj.getId(), userCampCatList);
			}else {
				if(postioningMap.containsKey(userCampCatObj.getParentPositionId())) {
					userCampCatList = postioningMap.get(userCampCatObj.getParentPositionId());
					if(userCampCatList == null ) userCampCatList = new ArrayList<UserCampaignCategories>();
				}else {
					userCampCatList = new ArrayList<UserCampaignCategories>();
				}
				userCampCatList.add(userCampCatObj);
				postioningMap.put(userCampCatObj.getParentPositionId(), userCampCatList);
			}
		} // for
		
		Set<Long> campCatSet = postioningMap.keySet();
		if(campCatSet == null && campCatSet.size() <= 0) return;
		
		for (Long eachId : campCatSet) {
			 userCampCatList =  postioningMap.get(eachId);
			
			 for (UserCampaignCategories eachObj : userCampCatList) {
				  createRow(eachObj,user);
				  				
			}
			
		}
		
		
		
		
		
		
	}//createdivs
	
	
	private void createRow(UserCampaignCategories userCampaignCategories,Users user){
		
		/*for (UserCampaignCategories userCampaignCategories : userCampList) {
		*/			
					Div rootDiv= new Div();
					
					// check box
					
					Checkbox catChkBox = new Checkbox(userCampaignCategories.getCategoryName());
					catChkBox.addEventListener("onCheck", this);
					catChkBox.setStyle("font-size:12px; color:#333333; vertical-align:top;");
				
				
					
					//catChkBox.setId(nameStr.toLowerCase()+"Id");
					catChkBox.setChecked(true);
					catChkBox.setValue(userCampaignCategories);
					catChkBox.setParent(rootDiv);
					
					
					// Description label
					Label descLabel = new Label("- "+userCampaignCategories.getDescription());
					descLabel.setStyle("color:#999999; padding-left:8px; ");
					descLabel.setParent(rootDiv);
					
					if(userCampaignCategories.getCategoryName().equalsIgnoreCase(Constants.CAMP_CATEGORY_PERSONAL)){
						
						pesrinalisedDivId.setVisible(true);
						pesrinalisedDivId.setParent(rootDiv);
						
						catChkBox.setAttribute("nextChild", pesrinalisedDivId);
						
						
						getaDays();
						getaMonths();
						getaYears();
						getbDays();
						getbMonths();
						getbYears();
					}
					if(userCampaignCategories.getCategoryName().equalsIgnoreCase(Constants.CAMP_CATEGORY_EVENTS)){
						storeDivId.setVisible(true);
						getStroeIds(user.getUserOrganization().getUserOrgId());
						storeDivId.setParent(rootDiv);
						
						catChkBox.setAttribute("nextChild", storeDivId);
						
					}
					
					
					rootDiv.setParent(categoriesDivId);
					
	/*			}
	*/
					logger.info("ccccccccccccc");
	}
	
	
	
	/**
	 * This method is to set the visibility of opt-out only if SMS is enabled
	 */
	public void setVisibility(){

		
		if(contactOwner!=null && contactOwner.isEnableSMS())
			optoutAId.setVisible(contactOwner.isEnableSMS());
		
	}
	/*public void onClick$unsubsAId(){
		
	
	
	
		
		userId=contactOwner.getUserId();
		String emailStr = emailTxtBoxId.getValue();
		
		if(unsubsAId.getLabel().equalsIgnoreCase("Unsubscribe")){
			
			campaignSentDao.updateCampaignSent(sentId, Constants.CS_STATUS_UNSUBSCRIBED);
			contactsDao.updateEmailStatusByUserId(emailStr, userId, "Unsubscribed");	
			
			Unsubscribes unsubscribe = new Unsubscribes();
			unsubscribe.setUserId(userId);
			unsubscribe.setEmailId(emailStr);
			unsubscribe.setDate(Calendar.getInstance());
			unsubscribe.setReason(Constants.UNSUB_EMAIL_RAESON);
		
			unsubscribesDao.saveOrUpdate(unsubscribe);
			MessageUtil.setMessage("Email address" +emailStr+ " has been unsubscribed successfully", "color:blue","TOP");
			unsubsAId.setLabel("Resubscribe");
			
		}else{
			unsubsAId.setLabel("Unsubscribe");
			campaignSentDao.updateCampaignSent(sentId, Constants.CS_STATUS_SUCCESS);
			contactsDao.updateEmailStatusByUserId(emailStr, userId, "Active");
			unsubscribesDao.deleteByEmailIdUserId(emailStr, userId);
			
			MessageUtil.setMessage("Email address "+emailStr+" has been re-subscribed successfully", "color:blue","TOP");
		}
		
	}//onClick$unsubsAId
	
	
		public void onClick$optoutAId(){
		

			
			 userId=contactOwner.getUserId();
		
			String mblStr = mblTxtBoxId.getValue();
			SMSSuppressedContacts	suppressedContact  = null;
		
			if(optoutAId.getLabel().equalsIgnoreCase("Opt-out")){
				
				contactsDao.updatemobileStatus(mblStr, Constants.CON_MOBILE_STATUS_OPTED_OUT, contactOwner);
				
				List<SMSSuppressedContacts> retList =  smsSuppressedContactsDao.searchContactsById(userId, mblStr);
				if(retList != null && retList.size() == 1) {
					logger.info("This contact is already exist in suppressed contacts list");
					suppressedContact = retList.get(0);
					
				}
				else {
					
					logger.info("This contact is new for this user in suppressed contacts list");
					suppressedContact = new SMSSuppressedContacts();
					suppressedContact.setUser(contactOwner);
					suppressedContact.setMobile(mblStr);
					suppressedContact.setSuppressedtime(Calendar.getInstance());
					suppressedContact.setReason(Constants.OPTOUT_MOBILE_RAESON);
					
				}
				suppressedContact.setType(SMSStatusCodes.CLICKATELL_STATUS_OPTED_OUT);
				smsSuppressedContactsDao.saveOrUpdate(suppressedContact);
				
			
				
				MessageUtil.setMessage("Mobile number" +mblStr+ " has been opted-out successfully", "color:blue","TOP");
				optoutAId.setLabel("Opt-in");
				
			}else{
				
				contactsDao.updatemobileStatus(mblStr, Constants.CON_MOBILE_STATUS_ACTIVE, contactOwner);
				smsSuppressedContactsDao.deleteMblByUserId(contactOwner, mblStr);
				
				MessageUtil.setMessage("Mobile number" +mblStr+ " has been opted-in  successfully", "color:blue","TOP");
				optoutAId.setLabel("Opt-out");
			}
			
		}//onClick$unsubsAId
	
	
	
	public void onClick$modifyAId(){
		if(modifyAId.getLabel().equals("Modify")){
			
			emailTxtBoxId.setDisabled(false);
			modifyAId.setLabel("Cancel");
		}else{
			emailTxtBoxId.setDisabled(true);
			modifyAId.setLabel("Modify");
		}
		
	}//onClick$modifyAId
	
	private A modifySmsAId,optoutAId,modifyAddrAId;
	
	
	public void onClick$modifySmsAId(){
		if(modifySmsAId.getLabel().equalsIgnoreCase("Modify")){
			
			mblTxtBoxId.setDisabled(false);
			modifySmsAId.setLabel("Cancel");
		}else{
			mblTxtBoxId.setDisabled(true);
			modifySmsAId.setLabel("Modify");
		}
		
	}//onClick$modifySmsAId
	

	private Textbox addrTxtBoxId,cityTxtBoxId,stateTxtBoxId,countryTxtBoxId,pinTxtBoxId;
	public void onClick$modifyAddrAId(){
		if(modifyAddrAId.getLabel().equalsIgnoreCase("Modify")){
			
			addrTxtBoxId.setDisabled(false);
			cityTxtBoxId.setDisabled(false);
			stateTxtBoxId.setDisabled(false);
			countryTxtBoxId.setDisabled(false);
			pinTxtBoxId.setDisabled(false);
			
			modifyAddrAId.setLabel("Cancel");
		}else{
			addrTxtBoxId.setDisabled(true);
			cityTxtBoxId.setDisabled(true);
			stateTxtBoxId.setDisabled(true);
			countryTxtBoxId.setDisabled(true);
			pinTxtBoxId.setDisabled(true);
			
			modifyAddrAId.setLabel("Modify");
		}
		
	}//onClick$modifyAddrAId$modifyAId
	*/
	
	
	private Listbox storeListBxId;

	private Radio maleRgId,femaleRgId;
	private Listbox birthdayCb,birthdmnthCb,birthYearCb,anniversartDayCb,anniversaryMnthCb,anniversaryYearCb;
	public void getbDays(){
		
		
		for(int i=1; i<=31;i++){
			Listitem dayItem= null;
			dayItem = new Listitem(""+i);
			dayItem.setValue(i);
			dayItem.setParent(birthdayCb);
			
		}
		birthdayCb.setSelectedIndex(0);
	}
	
	public void getbMonths(){
		
		Calendar tempCal =Calendar.getInstance();
			for(int i=0; i< 12;i++){
				tempCal.set(Calendar.MONTH,i);
				
				String name = tempCal.getDisplayName(Calendar.MONTH, 
						Calendar.SHORT, Locale.ENGLISH);
				Listitem monthItem= null;
				monthItem = new Listitem(name);
				monthItem.setValue(i+1);
				monthItem.setParent(birthdmnthCb);
				
			}
			birthdmnthCb.setSelectedIndex(0);
			
		}
	public void getbYears(){
		Calendar tempCal =Calendar.getInstance();
		int year =tempCal.get(Calendar.YEAR);
		
		for(int i=1900; i<=year;i++){
			Listitem yeartem= null;
			yeartem = new Listitem(""+i);
			yeartem.setParent(birthYearCb);
			
		}
		birthYearCb.setSelectedIndex(0);
		
	}
	
	// Anniversary settings
	public void getaDays(){
		
		for(int i=1; i<=31;i++){
			Listitem dayItem= null;
			dayItem = new Listitem(""+i);
			dayItem.setValue(i);
			dayItem.setParent(anniversartDayCb);
			
		}
		anniversartDayCb.setSelectedIndex(0);
	}
	
	
	public void getaMonths(){
			
		Calendar tempCal =Calendar.getInstance();
				for(int i=0; i< 12;i++){
					tempCal.set(Calendar.MONTH,i);
					
					String name = tempCal.getDisplayName(Calendar.MONTH, 
							Calendar.SHORT, Locale.ENGLISH);
					Listitem monthItem= null;
					monthItem = new Listitem(name);
					monthItem.setValue(i+1);
				monthItem.setParent(anniversaryMnthCb);
				
			}
				anniversaryMnthCb.setSelectedIndex(0);
			
		}
	public void getaYears(){
		
		Calendar tempCal =Calendar.getInstance();
		int year =tempCal.get(Calendar.YEAR);
		
		for(int i=1900; i<=year;i++){
			Listitem yeartem= null;
			yeartem = new Listitem(""+i);
			yeartem.setParent(anniversaryYearCb);
			
		}
		anniversaryYearCb.setSelectedIndex(0);
		
	}
	
	
	/*public void onClick$saveBtnId(){
		
		String bdayStr="";
		String anniversaryStr="";
		
		Contacts contact = contactsDao.findById(cid);
		if(weekRgId.isChecked()){
			contact.setLastMailSpan(7);
		}else if(mnthRgId.isChecked()){
			contact.setLastMailSpan(30);
		}else if(quarterRgId.isChecked()){
			contact.setLastMailSpan(90);
		}else if(allRgId.isChecked()){
			contact.setLastMailSpan(0);
		}
		
		if(weekSmsRgId.isChecked()){
			contact.setLastSmsSpan(7);
		}else if(mnthSmsRgId.isChecked()){
			contact.setLastSmsSpan(30);
		}else if(quarterSmsRgId.isChecked()){
			contact.setLastSmsSpan(90);
		}else if(allSmsRgId.isChecked()){
			contact.setLastSmsSpan(0);
		}
		
		
		if(maleRgId.isChecked()){
			contact.setGender("Male");
		}else{
			contact.setGender("Female");
		}
		
		
		// category saving
		
	List chkboxList = categoriesDivId.getChildren();
	String categoryStr="";
	for (Object object : chkboxList) {
		
		if(object instanceof Div){
			
			Div tempDiv= (Div)object;
			
			List tempList =tempDiv.getChildren();
			for (Object obj : tempList) {
				if(obj instanceof Checkbox){
					
					Checkbox tempChkBox = (Checkbox)obj;
					if(tempChkBox.isChecked()){
						
						UserCampaignCategories userCampaignCategories = tempChkBox.getValue();
						String nameStr=""+userCampaignCategories.getId();
						
						categoryStr+= nameStr+", ";
						bdayStr+=birthdayCb.getSelectedItem().getLabel();
						bdayStr+="-"+birthdmnthCb.getSelectedItem().getLabel();
						bdayStr+="-"+birthYearCb.getSelectedItem().getLabel();
						logger.info("birth day str"+bdayStr);
						
						anniversaryStr+=anniversartDayCb.getSelectedItem().getLabel();
						anniversaryStr+="-"+anniversaryMnthCb.getSelectedItem().getLabel();
						anniversaryStr+="-"+anniversaryYearCb.getSelectedItem().getLabel();
						logger.info("anniversatStr day str"+anniversaryStr);
					
						if(! storeListBxId.getSelectedItem().getLabel().equals("Select your store") )
							contact.setHomeStore(storeListBxId.getSelectedItem().getLabel());
				
					
				}
			 else{
					pesrinalisedDivId.setVisible(false);
					storeDivId.setVisible(false);
					
				}
			  }
			}
			
			
			
			
			
		} else if(object instanceof Checkbox){
			
			
			Checkbox chkbox = (Checkbox)object;
				if(chkbox.isChecked()){
					
					UserCampaignCategories userCampaignCategories = chkbox.getValue();
					String nameStr=userCampaignCategories.getCategoryName();
					
					categoryStr+= nameStr+", ";
					
					
					
				}
				
		
				
			}
		
		
		
			
	}// for
		logger.info("catgory str is"+categoryStr);
		
		
		
	}//onClick$saveBtnId
	*/
	public void getStroeIds(long orgId){
		
		List<OrganizationStores> storeList = organizationStoresDao.findByOrganization(orgId);
		if(storeList == null || storeList.size() == 0) return;
		for (OrganizationStores organizationStores : storeList) {
			
			Listitem tempItem= null;
			//tempItem = new Listitem(organizationStores.getHomeStoreId());
			tempItem = new Listitem(organizationStores.getStoreName());
			tempItem.setParent(storeListBxId);
			
		}
		
		
	}
	private A editAId;
	/*public void onClick$editBtnId(){

		
		if(editBtnId.getLabel().equalsIgnoreCase("Edit")){
			fnameTxtId.setDisabled(false);
			lnameTxtId.setDisabled(false);
			emailTxtId.setDisabled(false);
			mblTxtId.setDisabled(false);
			pinTxtId.setDisabled(false);
			
			
			editBtnId.setLabel("Save");
			
			
		}else{
			fnameTxtId.setDisabled(true);
			lnameTxtId.setDisabled(true);
			emailTxtId.setDisabled(true);
			mblTxtId.setDisabled(true);
			pinTxtId.setDisabled(true);
			
		
			
			editBtnId.setLabel("Edit");
		}
		
	}*/
	
public void onClick$cancelBtnId(){
	Redirect.goTo(PageListEnum.USERADMIN_SUBSCRIBER_SETTINGS);
}

public void onClick$ExitbtnId(){
	Redirect.goTo(PageListEnum.USERADMIN_SUBSCRIBER_SETTINGS);
}


}
