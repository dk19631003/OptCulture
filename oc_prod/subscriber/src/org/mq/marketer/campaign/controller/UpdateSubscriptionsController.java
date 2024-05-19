package org.mq.marketer.campaign.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.SMSSuppressedContacts;
import org.mq.marketer.campaign.beans.Unsubscribes;
import org.mq.marketer.campaign.beans.UserCampaignCategories;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CampaignSentDao;
import org.mq.marketer.campaign.dao.CampaignSentDaoForDML;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDao;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDaoForDML;
import org.mq.marketer.campaign.dao.UnsubscribesDao;
import org.mq.marketer.campaign.dao.UnsubscribesDaoForDML;
import org.mq.marketer.campaign.dao.UserCampaignCategoriesDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptUrlParameters;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;

@SuppressWarnings({ "rawtypes", "serial" })
public class UpdateSubscriptionsController extends GenericForwardComposer implements EventListener {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private ContactsDao contactsDao;
	private UserCampaignCategoriesDao userCampaignCategoriesDao;
	private CampaignSentDao campaignSentDao;
	private CampaignSentDaoForDML campaignSentDaoForDML;
	private UnsubscribesDao unsubscribesDao;
	private UnsubscribesDaoForDML unsubscribesDaoForDML;
	private OrganizationStoresDao organizationStoresDao;
	private SMSSuppressedContactsDao smsSuppressedContactsDao;
	private ContactsDaoForDML contactsDaoForDML;
	private SMSSuppressedContactsDaoForDML smsSuppressedContactsDaoForDML;
	//private Users currentUser;
	
	
	public UpdateSubscriptionsController(){
		
		contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
		contactsDaoForDML = (ContactsDaoForDML)SpringUtil.getBean("contactsDaoForDML");
		userCampaignCategoriesDao = (UserCampaignCategoriesDao)SpringUtil.getBean("userCampaignCategoriesDao");
		campaignSentDao = (CampaignSentDao)SpringUtil.getBean("campaignSentDao");
		campaignSentDaoForDML = (CampaignSentDaoForDML)SpringUtil.getBean("campaignSentDaoForDML");
		unsubscribesDao = (UnsubscribesDao)SpringUtil.getBean("unsubscribesDao");
		unsubscribesDaoForDML = (UnsubscribesDaoForDML)SpringUtil.getBean("unsubscribesDaoForDML");
		organizationStoresDao = (OrganizationStoresDao)SpringUtil.getBean("organizationStoresDao");
		smsSuppressedContactsDao = (SMSSuppressedContactsDao)SpringUtil.getBean("smsSuppressedContactsDao");
		smsSuppressedContactsDaoForDML = (SMSSuppressedContactsDaoForDML)SpringUtil.getBean("smsSuppressedContactsDaoForDML");
		
		HttpServletRequest request = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
		cid= (Long) request.getAttribute("cId");
		sentId= (Long) request.getAttribute("sentId");
		
	}
	

	private Textbox fnameTxtId,lnameTxtId,emailTxtId,mblTxtId,pinTxtId,emailTxtBoxId,mblTxtBoxId,postalTxtBoxId;
	private Radio allRgId,weekRgId,mnthRgId,quarterRgId,weekSmsRgId,mnthSmsRgId,quarterSmsRgId,allSmsRgId;
	private Label createDateLblId,sourceLblId,catLablId;
	private A modifyAId,unsubsAId;
	
	private Contacts contact;
	private Long cid;
	private Long sentId;
	private Users contactOwner;
	private long userId;
	
	
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
		
		logger.debug("contact id is"+cid);
		defaultSettings();
		/*if(contact == null){
		 contact = contactsDao.findById(cid);
		 }
		if(contact == null) return;
		 contactOwner =contact.getUsers();
		 userId=contact.getUsers().getUserId();
		
		
		
		String createdDate= MyCalendar.calendarToString(contact.getCreatedDate(), MyCalendar.FORMAT_DATEONLY);
		createDateLblId.setValue(createdDate);
		
		if(contact.getOptinMedium() == null || contact.getOptinMedium().equals(Constants.CONTACT_OPTIN_MEDIUM_POS) || contact.getOptinMedium().equals(Constants.CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY)){
			sourceLblId.setValue("store.");
		}else{
			sourceLblId.setValue("online web-form.");
		}
		
		String emailId= contact.getEmailId();
		String phone=contact.getMobilePhone();
		String zip =contact.getZip();
		
		fnameTxtId.setValue(contact.getFirstName() == null || contact.getFirstName().isEmpty() ? "First Name": contact.getFirstName() );
		lnameTxtId.setValue(contact.getLastName() == null || contact.getLastName().isEmpty() ?"Last Name": contact.getLastName());
		emailTxtId.setValue(emailId == null || emailId.isEmpty() ? "Primary Email Address":emailId );
		mblTxtId.setValue(phone == null || phone.isEmpty() ?"MobileNumber": phone );
		pinTxtId.setValue(zip == null  || zip.isEmpty() ? "Postal Code": zip );
		
		
		
		if(contact.getLastMailSpan() == 0){
			allRgId.setSelected(true);
		}else if(contact.getLastMailSpan() == 7){
			weekRgId.setSelected(true);
		}else if(contact.getLastMailSpan() == 30){
			mnthRgId.setSelected(true);
		}
		else if(contact.getLastMailSpan() == 90){
			quarterRgId.setSelected(true);
		}
		
		

		if(contact.getLastSmsSpan() == 0){
			allSmsRgId.setSelected(true);
		}else if(contact.getLastSmsSpan() == 7){
			weekSmsRgId.setSelected(true);
		}else if(contact.getLastSmsSpan() == 30){
			mnthSmsRgId.setSelected(true);
		}
		else if(contact.getLastSmsSpan() == 90){
			quarterSmsRgId.setSelected(true);
		}
		
		//unsubsAId.setAttribute("status", Constants.CAMP_STATUS_ACTIVE);
		
		if(contact.getEmailStatus().equals(Constants.CS_STATUS_UNSUBSCRIBED)){
			
			unsubsAId.setLabel("Resubscribe");
			
		}else{
			unsubsAId.setLabel("Unsubscribe");
		}
		
		if(contact.getMobileStatus().equals(Constants.CON_MOBILE_STATUS_OPTED_OUT)){
			optoutAId.setLabel("Opt-in");
		}else{
			
			optoutAId.setLabel("Opt-out");
		}
		
		getContactDetails(contact);
		 createdivs(contactOwner,contact);
		 viewStore(contact);
		 if(contact.getBirthDay() != null)setBday(contact.getBirthDay());
		 if(contact.getAnniversary()!= null)setAnniversary(contact.getAnniversary());*/
		
		
		
	}
	
	public void defaultSettings(){
		
		if(contact == null){
			 contact = contactsDao.findById(cid);
			 }
			if(contact == null) return;
			 contactOwner =contact.getUsers();
			 userId=contact.getUsers().getUserId();
			
			
			
			String createdDate= MyCalendar.calendarToString(contact.getCreatedDate(), MyCalendar.FORMAT_MDATEONLY);
			createDateLblId.setValue(createdDate+",");
			
			if(contact.getOptinMedium() == null || contact.getOptinMedium().equals(Constants.CONTACT_OPTIN_MEDIUM_POS) || contact.getOptinMedium().equals(Constants.CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY)){
				sourceLblId.setValue("store.");
			}else{
				sourceLblId.setValue("online web-form.");
			}
			
			String emailId= contact.getEmailId();
			String phone=contact.getMobilePhone();
			String zip =contact.getZip();
			
			fnameTxtId.setValue(contact.getFirstName() == null || contact.getFirstName().isEmpty() ? "First Name": contact.getFirstName() );
			lnameTxtId.setValue(contact.getLastName() == null || contact.getLastName().isEmpty() ?"Last Name": contact.getLastName());
			emailTxtId.setValue(emailId == null || emailId.isEmpty() ? "Primary Email Address":emailId );
			mblTxtId.setValue(phone == null || phone.isEmpty() ?"MobileNumber": phone );
			pinTxtId.setValue(zip == null  || zip.isEmpty() ? "Postal Code": zip );
			
			
			
			if(contact.getLastMailSpan() == 0){
				allRgId.setSelected(true);
			}else if(contact.getLastMailSpan() == 7){
				weekRgId.setSelected(true);
			}else if(contact.getLastMailSpan() == 30){
				mnthRgId.setSelected(true);
			}
			else if(contact.getLastMailSpan() == 90){
				quarterRgId.setSelected(true);
			}
			
			

			if(contact.getLastSmsSpan() == 0){
				allSmsRgId.setSelected(true);
			}else if(contact.getLastSmsSpan() == 7){
				weekSmsRgId.setSelected(true);
			}else if(contact.getLastSmsSpan() == 30){
				mnthSmsRgId.setSelected(true);
			}
			else if(contact.getLastSmsSpan() == 90){
				quarterSmsRgId.setSelected(true);
			}
			
			//unsubsAId.setAttribute("status", Constants.CAMP_STATUS_ACTIVE);
			
			if(contact.getEmailStatus().equals(Constants.CS_STATUS_UNSUBSCRIBED)){
				
				unsubsAId.setLabel("Resubscribe");
				
			}else{
				unsubsAId.setLabel("Unsubscribe");
			}
			
			if(contact.getMobileStatus().equals(Constants.CON_MOBILE_STATUS_OPTED_OUT)){
				optoutAId.setLabel("Opt-in");
			}else{
				
				optoutAId.setLabel("Opt-out");
			}
			
			getContactDetails(contact);
			
			//M
			setVisibility();
			
			 createdivs(contactOwner,contact);
			 viewStore(contact);
			 if(contact.getBirthDay() != null){
				 //setBday(contact.getBirthDay());
				 birthdayDivId.setVisible(false);
			 }
			 if(contact.getAnniversary()!= null){
				 //setAnniversary(contact.getAnniversary());
				 anniversaryDivId.setVisible(false);
			}
			 if(contact.getGender()!=null)genderDivId.setVisible(false);
			 
			 
	}
	public void setBday(Calendar bdayCal){
		
		Calendar tempCal = Calendar.getInstance();
		Date tempDate= bdayCal.getTime();
		int dateVal=tempDate.getDate();
		int monthVal=tempDate.getMonth();
		int yearVal=tempDate.getYear();
		yearVal=yearVal+1900;
		logger.info("date val is"+dateVal+" monthVal is ==="+monthVal+"year value "+yearVal);
		
		for (Listitem dayItem : birthdayCb.getItems()) {
			logger.info("date val is"+dateVal+" is ==="+dayItem.getLabel());
			if(dayItem.getLabel().equals(""+dateVal)){
				birthdayCb.setSelectedItem(dayItem);
				break;
			}
			
			
		}
		for (Listitem monthItem : birthdmnthCb.getItems()) {
			
			tempCal.set(Calendar.MONTH,monthVal);
			String mnthStr = tempCal.getDisplayName(Calendar.MONTH, 
					Calendar.SHORT, Locale.ENGLISH);
			logger.info("month str is"+mnthStr);
			
			if(monthItem.getLabel().equals(mnthStr)){
				birthdmnthCb.setSelectedItem(monthItem);
				break;
			}
			
			
		}
		
		for (Listitem yearItem : birthYearCb.getItems()) {
			
			if(yearItem.getLabel().equals(""+yearVal)){
				birthYearCb.setSelectedItem(yearItem);
				break;
			}
			
			
		}
		
		
		
		
		
		
	}//set
	
	public void setAnniversary(Calendar annCal){
		
		Calendar tempCal = Calendar.getInstance();
		Date tempDate= annCal.getTime();
		int dateVal=tempDate.getDate();
		int monthVal=tempDate.getMonth();
		int yearVal=tempDate.getYear();
		yearVal=yearVal+1900;
		logger.info("date val is"+dateVal+" monthVal is ==="+monthVal+"year value "+yearVal);
		
		for (Listitem dayItem : anniversartDayCb.getItems()) {
			logger.info("date val is"+dateVal+" is ==="+dayItem.getLabel());
			if(dayItem.getLabel().equals(""+dateVal)){
				anniversartDayCb.setSelectedItem(dayItem);
				break;
			}
			
			
		}
		for (Listitem monthItem : anniversaryMnthCb.getItems()) {
			
			tempCal.set(Calendar.MONTH,monthVal);
			String mnthStr = tempCal.getDisplayName(Calendar.MONTH, 
					Calendar.SHORT, Locale.ENGLISH);
			logger.info("month str is"+mnthStr);
			
			if(monthItem.getLabel().equals(mnthStr)){
				anniversaryMnthCb.setSelectedItem(monthItem);
				break;
			}
			
			
		}
		
		for (Listitem yearItem : anniversaryYearCb.getItems()) {
			
			if(yearItem.getLabel().equals(""+yearVal)){
				anniversaryYearCb.setSelectedItem(yearItem);
				break;
			}
			
			
		}
		
		
		
		
		
		
	}
	
	
	public void getContactDetails(Contacts contact){
		
			String emailStr= contact.getEmailId();
			String phoneStr=contact.getMobilePhone();
			String zipStr =contact.getZip();
			
			if(emailStr == null || emailStr.isEmpty()){
				emailTxtBoxId.setValue("Primary Email Address");
			}else{
				
				emailTxtBoxId.setValue(emailStr);
				String statusOfSubUnsubStr = unsubscribesDao.isAlreadyUnsubscribedContact(userId, emailStr);
				if(statusOfSubUnsubStr == null) 
					unsubsAId.setLabel("Unsubscribe");
				else
					unsubsAId.setLabel("Resubscribe");
			}
			
			if(phoneStr == null || phoneStr.isEmpty()){
				mblTxtBoxId.setValue("MobileNumber");
			}else{
				
				mblTxtBoxId.setValue(phoneStr);
				if(smsSuppressedContactsDao.isMobileOptedOut(userId, phoneStr) == false)
					optoutAId.setLabel("Opt-out");
				else
					optoutAId.setLabel("Opt-in");
			}
			
			
			addrTxtBoxId.setValue(contact.getAddressOne() == null ||contact.getAddressOne().isEmpty() ? "Enter Street" :contact.getAddressOne());
			cityTxtBoxId.setValue(contact.getCity() == null || contact.getCity().isEmpty() ?"Enter city" : contact.getCity() );
			stateTxtBoxId.setValue(contact.getState() == null|| contact.getState().isEmpty() ?"Enter State" :contact.getState() );
			countryTxtBoxId.setValue(contact.getCountry() == null || contact.getCountry().isEmpty() ?"Enter Country" : contact.getCountry());
			pinTxtBoxId.setValue(zipStr== null || zipStr.isEmpty() ? "Enter ZIP" : zipStr);
			
			if(contact.getGender() != null){
				if(contact.getGender().equalsIgnoreCase("Male")){
			
				maleRgId.setChecked(true);
			}else if(contact.getGender().equalsIgnoreCase("Female")){
				femaleRgId.setChecked(true);
			}
			
			}
			
		
	}
	public  void viewStore(Contacts contact){
		
		String homeStore = contact.getHomeStore();
		logger.info("home store is"+homeStore);
		for (Listitem storeItem : storeListBxId.getItems()) {
			
			if(storeItem.getValue() != null &&
					((OrganizationStores)storeItem.getValue()).getHomeStoreId().equals(homeStore)){
				
				logger.info("home store is"+homeStore);
				storeItem.setSelected(true);
				storeDivId.setVisible(false);
				break;
			}
		}
		
		
	}
	private Div categoriesDivId,pesrinalisedDivId,storeDivId,noteDivId,genderDivId,anniversaryDivId,birthdayDivId;
	private A subsEmailsId,transEmailsId;
	//private Checkbox eventsId,personalisedoffersId;
	
	/*public void onCheck$personalisedoffersId(){
		logger.info("checked 1");
		
		pesrinalisedDivId.setVisible(!pesrinalisedDivId.isVisible());
		
	}
	
	public void onCheck$storeDivId(){
		logger.info("checked 1");
		
		storeDivId.setVisible(!storeDivId.isVisible());
		
	}
	*/
	

		
	public void createdivs(Users user,Contacts contacts){
		
		Components.removeAllChildren(categoriesDivId);
		
		List<UserCampaignCategories> userCampList = null;
		String catIdStr =contacts.getCategories();
		userCampList = userCampaignCategoriesDao.findCatByUserId(user.getUserId());
		
		if(userCampList == null  || userCampList.size() == 0 ){
			categoriesDivId.setVisible(false);
			catLablId.setVisible(true);
			return;
		}
		logger.debug("size is"+userCampList.size());
		/*for (UserCampaignCategories userCampaignCategories : userCampList) {
			
			Div rootDiv= new Div();
			
			// check box
			
			Checkbox catChkBox = new Checkbox(userCampaignCategories.getCategoryName());
			catChkBox.addEventListener("onCheck", this);
			catChkBox.setStyle("font-size:12px; color:#333333;");
		
		
			
			//catChkBox.setId(nameStr.toLowerCase()+"Id");
			catChkBox.setChecked(true);
			
			
			 if(catIdStr != null && !catIdStr.trim().isEmpty() ) {
				if(catIdStr.equals(Constants.CATEGORY_NO_CATEGORY)){
					catChkBox.setChecked(false);
				}else{
				
				catChkBox.setChecked(catIdStr.contains(userCampaignCategories.getId().toString()+",") 
						|| catIdStr.contains( ","+userCampaignCategories.getId().toString()) 
						||catIdStr.contains( ","+userCampaignCategories.getId().toString()+",")
						|| catIdStr.equals(userCampaignCategories.getId().toString()));
					
				}
				
			}
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
				  createRow(eachObj,user,catIdStr);
				  				
			}
			
		}
		
		
		
		
	}//createdivs
	
	
	private void createRow(UserCampaignCategories userCampaignCategories,Users user,String catIdStr){
		
		/*for (UserCampaignCategories userCampaignCategories : userCampList) {
		*/	
			Div rootDiv= new Div();
			
			// check box
			
			Checkbox catChkBox = new Checkbox(userCampaignCategories.getCategoryName());
			catChkBox.addEventListener("onCheck", this);
			catChkBox.setStyle("font-size:12px; color:#333333;");
		
		
			
			//catChkBox.setId(nameStr.toLowerCase()+"Id");
			catChkBox.setChecked(true);
			
			
			 if(catIdStr != null && !catIdStr.trim().isEmpty() ) {
				if(catIdStr.equals(Constants.CATEGORY_NO_CATEGORY)){
					catChkBox.setChecked(false);
				}else{
				
				catChkBox.setChecked(catIdStr.contains(userCampaignCategories.getId().toString()+",") 
						|| catIdStr.contains( ","+userCampaignCategories.getId().toString()) 
						||catIdStr.contains( ","+userCampaignCategories.getId().toString()+",")
						|| catIdStr.equals(userCampaignCategories.getId().toString()));
					
				}
				
			}
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
			
		/*}*/

	}
	
	
	
	public void onClick$unsubsAId(){
		
	
	
		try {
			if(contact ==null){
				
			 contact = contactsDao.findById(cid);
			}
			if(contactOwner == null){
				contactOwner = contact.getUsers();
			}
			userId=contactOwner.getUserId();
			
			String emailStr = emailTxtBoxId.getValue();
			
			if(emailStr.equals("Primary Email Address")){
				emailStr="";
			}
			
			if(emailStr.isEmpty()){
				
				MessageUtil.setMessage("Contact don't have email ID  to Unsubscribe / Resubscribe", "color:red");
				return;
				
			}
			
			if(unsubsAId.getLabel().equalsIgnoreCase("Unsubscribe")){
				boolean isunsubs = false;
				
				if(contact.getEmailStatus().equals(Constants.CS_STATUS_UNSUBSCRIBED)){
					
					MessageUtil.setMessage("Email address " +emailStr+ " has been unsubscribed from this mailing list. \n Click on resubscribe link below to join this mailing list again.", "color:blue","TOP");
					unsubsAId.setLabel("Resubscribe");
				}else{
				
					//campaignSentDao.updateCampaignSent(sentId, Constants.CS_STATUS_UNSUBSCRIBED);
                    campaignSentDaoForDML.updateCampaignSent(sentId, Constants.CS_STATUS_UNSUBSCRIBED);
					contactsDaoForDML.updateEmailStatusByUserId(emailStr, userId, "Unsubscribed");	
					
					Unsubscribes unsubscribe = new Unsubscribes();
					unsubscribe.setUserId(userId);
					unsubscribe.setEmailId(emailStr);
					unsubscribe.setDate(Calendar.getInstance());
					unsubscribe.setReason(Constants.UNSUB_EMAIL_RAESON);
					
					try {
						unsubscribesDaoForDML.saveOrUpdate(unsubscribe);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ",e);
						isunsubs = true;
						MessageUtil.setMessage("Email address " +emailStr+ " has been unsubscribed from this mailing list. \n Click on resubscribe link below to join this mailing list again.", "color:red;");
						
					}
					if(!isunsubs){
						
						MessageUtil.setMessage("Email address " +emailStr+ " has been unsubscribed successfully", "color:blue","TOP");
					}
					contact.setEmailStatus(Constants.CS_STATUS_UNSUBSCRIBED);
					unsubsAId.setLabel("Resubscribe");
					//unsubsAId.setAttribute("status", Constants.CS_STATUS_UNSUBSCRIBED);
				}
				
			}else if(unsubsAId.getLabel().equalsIgnoreCase("Resubscribe")){
				unsubsAId.setLabel("Unsubscribe");
				//campaignSentDao.updateCampaignSent(sentId, Constants.CS_STATUS_SUCCESS);
                campaignSentDaoForDML.updateCampaignSent(sentId, Constants.CS_STATUS_SUCCESS);
				contactsDaoForDML.updateEmailStatusByUserId(emailStr, userId, "Active");
				unsubscribesDaoForDML.deleteByEmailIdUserId(emailStr, userId);
				contact.setEmailStatus(Constants.CAMP_STATUS_ACTIVE);
				
				//unsubsAId.setAttribute("status", Constants.CAMP_STATUS_ACTIVE);
				
				MessageUtil.setMessage("Email address  "+emailStr+" has been re-subscribed successfully", "color:blue","TOP");
			}
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception",e);
		}
		
	}//onClick$unsubsAId
	
	
		public void onClick$optoutAId(){
		

			if(contact ==null){
				
			 contact = contactsDao.findById(cid);
			}
			if(contactOwner == null){
				contactOwner = contact.getUsers();
			}
			 userId=contactOwner.getUserId();
		
			String mblStr = mblTxtBoxId.getValue();
			if(mblStr.equals("MobileNumber")){
				mblStr="";
			}
			SMSSuppressedContacts	suppressedContact  = null;
			
			if(mblStr.isEmpty()){
				
				MessageUtil.setMessage("Contact don't have mobile phone to opt-out / opt-in", "color:red");
				return;
			}
		
			if(optoutAId.getLabel().equalsIgnoreCase("Opt-out")){
				
				
				if(contact.getMobileStatus().equals(Constants.CON_MOBILE_STATUS_OPTED_OUT)){
					MessageUtil.setMessage("Mobile number " + mblStr+ " has been opted-out successfully from this mailing list. \n Click on Opt-in link below to join this mailing list again.", "color:blue","TOP");
					optoutAId.setLabel("Opt-in");
					
					
				}else{
					
				
				contactsDaoForDML.updatemobileStatus(mblStr, Constants.CON_MOBILE_STATUS_OPTED_OUT, contactOwner);
				
				List<SMSSuppressedContacts> retList =  smsSuppressedContactsDao.searchContactsById(contact.getUsers(), mblStr);
				if(retList != null && retList.size() == 1) {
					logger.info("This contact is already exist in suppressed contacts list");
					
					suppressedContact = retList.get(0);
					
				}
				else {
					
					logger.info("This contact is new for this user in suppressed contacts list");
					suppressedContact = new SMSSuppressedContacts();
					suppressedContact.setUser(contact.getUsers());
					suppressedContact.setMobile(mblStr);
					suppressedContact.setSuppressedtime(Calendar.getInstance());
					
				}
				suppressedContact.setReason(Constants.OPTOUT_MOBILE_RAESON);
				suppressedContact.setType(SMSStatusCodes.CLICKATELL_STATUS_OPTED_OUT);
				//smsSuppressedContactsDao.saveOrUpdate(suppressedContact);
				smsSuppressedContactsDaoForDML.saveOrUpdate(suppressedContact);
				
			
				
				MessageUtil.setMessage("Mobile number " + mblStr+ " has been opted-out successfully", "color:blue","TOP");
				optoutAId.setLabel("Opt-in");
				
			}
				
			}else if(optoutAId.getLabel().equalsIgnoreCase("Opt-in")){
				
				contactsDaoForDML.updatemobileStatus(mblStr, Constants.CON_MOBILE_STATUS_ACTIVE, contactOwner);
				//smsSuppressedContactsDao.deleteMblByUserId(contact.getUsers(), mblStr);
				smsSuppressedContactsDaoForDML.deleteMblByUserId(contact.getUsers(), mblStr);
				
				MessageUtil.setMessage("Mobile number " +mblStr+ " has been opted-in  successfully", "color:blue","TOP");
				optoutAId.setLabel("Opt-out");
			}
			
		}//onClick$unsubsAId
	
	
	
	public void onClick$modifyAId(){
		if(modifyAId.getLabel().equals("Modify")){
			
			emailTxtBoxId.setDisabled(false);
			modifyAId.setLabel("Cancel");
		}else{
			
			emailTxtBoxId.setValue(contact.getEmailId());
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
			
			mblTxtBoxId.setValue(contact.getMobilePhone());
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
			addrTxtBoxId.setValue(contact.getAddressOne() == null ||contact.getAddressOne().isEmpty() ? "Enter Street" :contact.getAddressOne());
			cityTxtBoxId.setValue(contact.getCity() == null || contact.getCity().isEmpty() ?"Enter city" : contact.getCity() );
			stateTxtBoxId.setValue(contact.getState() == null|| contact.getState().isEmpty() ?"Enter State" :contact.getState() );
			countryTxtBoxId.setValue(contact.getCountry() == null || contact.getCountry().isEmpty() ?"Enter Country" : contact.getCountry());
			pinTxtBoxId.setValue(contact.getZip() == null || contact.getZip().isEmpty() ? "Enter ZIP" : contact.getZip());
			
			addrTxtBoxId.setDisabled(true);
			cityTxtBoxId.setDisabled(true);
			stateTxtBoxId.setDisabled(true);
			countryTxtBoxId.setDisabled(true);
			pinTxtBoxId.setDisabled(true);
			
			modifyAddrAId.setLabel("Modify");
		}
		
	}//onClick$modifyAddrAId$modifyAId
	
	
	
	/**
	 * This method is to set the visibility of Email,SMS and Postal mail if they has no value
	 */
	public void setVisibility(){

		if(contactOwner!=null && contactOwner.isEnableSMS())
			optoutAId.setVisible(contactOwner.isEnableSMS());
		
		emailTxtBoxId.setDisabled((emailTxtBoxId.getValue()==null||emailTxtBoxId.getValue().isEmpty()||(emailTxtBoxId.getValue().trim()).equals("Primary Email Address"))?false:true);
		mblTxtBoxId.setDisabled((mblTxtBoxId.getValue()==null||mblTxtBoxId.getValue().isEmpty()||(mblTxtBoxId.getValue().trim()).equals("MobileNumber"))?false:true);
		addrTxtBoxId.setDisabled(addrTxtBoxId.getValue()==null||addrTxtBoxId.getValue().isEmpty()||(addrTxtBoxId.getValue().trim()).equalsIgnoreCase("Enter Street")?false:true);
		cityTxtBoxId.setDisabled(cityTxtBoxId.getValue()==null||cityTxtBoxId.getValue().isEmpty()||(cityTxtBoxId.getValue().trim()).equalsIgnoreCase("Enter City")?false:true);
		stateTxtBoxId.setDisabled(stateTxtBoxId.getValue()==null||stateTxtBoxId.getValue().isEmpty()||(stateTxtBoxId.getValue().trim()).equalsIgnoreCase("Enter State")?false:true);
		countryTxtBoxId.setDisabled(countryTxtBoxId.getValue()==null||countryTxtBoxId.getValue().isEmpty()||(countryTxtBoxId.getValue().trim()).equalsIgnoreCase("Enter Country")?false:true);
		pinTxtBoxId.setDisabled(pinTxtBoxId.getValue()==null||pinTxtBoxId.getValue().isEmpty()||(pinTxtBoxId.getValue().trim()).equals("Enter ZIP")?false:true);

		fnameTxtId.setDisabled(fnameTxtId.getValue()==null||fnameTxtId.getValue().isEmpty()||(fnameTxtId.getValue().trim()).equals("First Name")?false:true);
		lnameTxtId.setDisabled(lnameTxtId.getValue()==null||lnameTxtId.getValue().isEmpty()||(lnameTxtId.getValue().trim()).equals("Last Name")?false:true);
		emailTxtId.setDisabled(emailTxtId.getValue()==null||emailTxtId.getValue().isEmpty()||(emailTxtId.getValue().trim()).equals("Primary Email Address")?false:true);
		mblTxtId.setDisabled(mblTxtId.getValue()==null||mblTxtId.getValue().isEmpty()||(mblTxtId.getValue().trim()).equals("MobileNumber")?false:true);
		pinTxtId.setDisabled(pinTxtId.getValue()==null||pinTxtId.getValue().isEmpty()||(pinTxtId.getValue().trim()).equals("Postal Code")?false:true);
	}
	
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
	
	public void viewChkBox(Checkbox checkbox){
		/*UserCampaignCategories userCat =null;
		
		if(checkbox.isChecked()) userCat = checkbox.getValue();
		if(! checkbox.isChecked() && userCat.getCategoryName().equalsIgnoreCase(Constants.CAMP_CATEGORY_PERSONAL)){
		pesrinalisedDivId.setVisible(false);	
		}
		
		if(! checkbox.isChecked() && userCat.getCategoryName().equalsIgnoreCase(Constants.CAMP_CATEGORY_EVENTS)){
		storeDivId.setVisible(false);	
		}*/
		
		
	}
	
	// Save subscriber preferences
	
	public void onClick$saveBtnId(){
		if(contact == null){
			
			contact = contactsDao.findById(cid);
		}
		
		
		// birthday anniversary saving
		
		String bdayStr="";
		String anniversaryStr="";
		if(! birthYearCb.getSelectedItem().getLabel().equals("Year") && ! birthdmnthCb.getSelectedItem().getLabel().equals("Month")
				&& ! birthdayCb.getSelectedItem().getLabel().equals("Day")){
					
			bdayStr+=birthYearCb.getSelectedItem().getLabel();
			int bmonths =birthdmnthCb.getSelectedItem().getValue();
			String bmonthStr="";
			if(bmonths < 10){
				bmonthStr = "0"+bmonths;
				bdayStr+="-"+bmonthStr;
			}
			else{
				bdayStr+="-"+bmonths;
			}
			int bdays = birthdayCb.getSelectedItem().getValue();
			String bdaysStr="";
			if(bdays < 10){
				bdaysStr = "0"+bdays;
				bdayStr+="-"+bdaysStr;
			}
			else{
				bdayStr+="-"+bdays;
			}
			//bdayStr+= "-"+birthdayCb.getSelectedItem().getLabel();
			
			logger.info("bdayStr day str"+bdayStr);
			Calendar birthdaycal = MyCalendar.dateString2Calendar(bdayStr.trim());
			contact.setBirthDay(birthdaycal);	
			}
		if(! anniversaryYearCb.getSelectedItem().getLabel().equals("Year") && ! anniversaryMnthCb.getSelectedItem().getLabel().equals("Month")
				&& ! anniversartDayCb.getSelectedItem().getLabel().equals("Day")){


			anniversaryStr+=anniversaryYearCb.getSelectedItem().getLabel();
			int amonths =anniversaryMnthCb.getSelectedItem().getValue();
			String amonthStr="";
			if(amonths < 10){
				amonthStr = "0"+amonths;
				anniversaryStr+="-"+amonthStr;
			}
			else{
				anniversaryStr+="-"+amonths;
			}
			int adays = anniversartDayCb.getSelectedItem().getValue();
			String adaysStr="";
			if(adays < 10){
				adaysStr = "0"+adays;
				anniversaryStr+="-"+adaysStr;
			}
			else{
				anniversaryStr+="-"+adays;
			}

			anniversaryStr+= "-"+anniversartDayCb.getSelectedItem().getLabel();
			logger.info("anniversatStr day str"+anniversaryStr);
			Calendar anniversarycal = MyCalendar.dateString2Calendar(anniversaryStr.trim());
			contact.setAnniversary(anniversarycal);
		}	
		
		// time span saving
		
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
		}else if(femaleRgId.isChecked()){
			contact.setGender("Female");
		}else{
			contact.setGender(null);
		}
		
		
		// category saving
		
	List chkboxList = categoriesDivId.getChildren();
	String categoryStr=Constants.STRING_NILL;
	for (Object object : chkboxList) {
		
		if(object instanceof Div){
			
			Div tempDiv= (Div)object;
			
			List tempList =tempDiv.getChildren();
			for (Object obj : tempList) {
				if(obj instanceof Checkbox){
					
					Checkbox tempChkBox = (Checkbox)obj;
					
					viewChkBox(tempChkBox);
					
					if(tempChkBox.isChecked()){
						
						
						UserCampaignCategories userCampaignCategories = tempChkBox.getValue();
						String nameStr=userCampaignCategories.getId().toString();
						
						if(!categoryStr.isEmpty()) categoryStr += Constants.DELIMETER_COMMA;
						
						categoryStr += nameStr;
						
					
					
						if(!storeListBxId.getSelectedItem().getLabel().equals("Select your store") )
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
					String nameStr=userCampaignCategories.getId().toString();
					
					if(!categoryStr.isEmpty()) categoryStr += Constants.DELIMETER_COMMA;
					
					categoryStr += nameStr;
					
					
					
				}
				
		
			}
		
		
		
			
	}// for
	
	logger.info("catgory str is"+categoryStr);
	contact.setCategories(!categoryStr.trim().isEmpty() ? categoryStr : Constants.CATEGORY_NO_CATEGORY);
		
		// Contact details saving
	String firstName = fnameTxtId.getValue().trim();
	String lastName=lnameTxtId.getValue().trim();
	
		contact.setFirstName(firstName.equals("First Name") ? null :firstName);
		contact.setLastName(lastName.equals("Last Name") ? null :lastName);
		
	/*	contact.setEmailId(emailTxtBoxId.getValue().trim());
		contact.setMobilePhone(mblTxtBoxId.getValue().trim());
		contact.setZip(pinTxtBoxId.getValue().trim());
		*/
		// Email validation
					String emailStr=emailTxtBoxId.getValue().trim();
					if(emailStr.equals("Primary Email Address")){
						emailStr="";
					}
					logger.info("email str is"+emailStr);
					if( !emailStr.isEmpty() ){
						if( (contact.getEmailId()!= null &&  ! contact.getEmailId().trim().equalsIgnoreCase(emailStr) ) || 
								contact.getEmailId() == null ) {
							logger.info("is enterd"+emailStr);
						
						try {
							if(!Utility.validateEmail(emailStr)) {
								logger.debug( " emailString :"+emailStr);
								MessageUtil.setMessage("Please enter a valid email address.", "color:red", "TOP");
								return ;
							}else{

								String retVal = PurgeList
										.checkForValidDomainByEmailId(emailStr);

								if (retVal != null) {

									MessageUtil.setMessage("Invalid Domain / not a mail server:", "color:red", "TOP");
									logger.debug("Invalid Domain / not a mail server: "
											+ emailStr);
									contact.setEmailStatus(retVal);

								}else{
									// String StratusStr = unsubsAId.getAttribute("status").toString();
									contact.setEmailStatus(Constants.CAMP_STATUS_ACTIVE);
								}
								
							}
							
							contact.setEmailId(emailStr);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("Exception.....", e);
						}
						
						}
					}else{
						contact.setEmailStatus(Constants.CONT_STATUS_INVALID_EMAIL);
						contact.setEmailId(null);
					}
					
					// Phone validation
					String	mobilePhone = mblTxtBoxId.getValue();
					if(mobilePhone.equals("MobileNumber")){
						mobilePhone="";
					}
					
					String conMobile= contact.getMobilePhone();
					UserOrganization organization =contactOwner != null ? contactOwner.getUserOrganization() : null;
					
					if(!mobilePhone.isEmpty()){
						
						mobilePhone = Utility.phoneParse(""+mblTxtBoxId.getValue(), organization);
						if(mobilePhone == null ) {
							MessageUtil.setMessage("Invalid Phone number.", "color:red", "TOP");
							return ;
						}
						
						if( (conMobile != null && (!conMobile.equals(mobilePhone)  && ((mobilePhone.length() < conMobile.length() && !conMobile.endsWith(mobilePhone) ) ||
					
								(conMobile.length() < mobilePhone.length() && !mobilePhone.endsWith(conMobile)) || (mobilePhone.length() == conMobile.length())))) || 
								conMobile == null){
							
							contact.setMobilePhone(mobilePhone);
							LoyaltyProgramHelper.updateLoyaltyMembrshpPhone(contact, mobilePhone);
							contact.setMobileStatus(Constants.CON_STATUS_ACTIVE);
						}
						
					
				}
					else{
						contact.setMobilePhone(null);
						LoyaltyProgramHelper.updateLoyaltyMembrshpPhone(contact, mobilePhone);
						contact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
					}
					
			
					
						/*if(!mobilePhone.isEmpty()){
							if( conMobile != null){
						
							mobilePhone = Utility.phoneParse(""+mblTxtBoxId.getValue());
							if(mobilePhone == null ) {
								MessageUtil.setMessage("Invalid Phone number.", "color:red", "TOP");
								return ;
							}else if(!conMobile.equals(mobilePhone)  && ((mobilePhone.length() < conMobile.length() && !conMobile.endsWith(mobilePhone) ) ||
									(conMobile.length() < mobilePhone.length() && !mobilePhone.endsWith(conMobile)) || (mobilePhone.length() == conMobile.length()))){
								
								contact.setMobilePhone(mobilePhone);
								
								contact.setMobileStatus(Constants.CON_STATUS_ACTIVE);
							}
							
						}
					}
						else{
							contact.setMobilePhone(null);
							contact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
						}
						*/
							
						
					
					
					String zip =pinTxtBoxId.getValue().trim();
					if(zip.equals("Enter ZIP")){
						zip="";
					}
					
					
					if(!zip.isEmpty()) {
						
						int sizeOfEntry = pinTxtId.getValue().trim().length();
						if(sizeOfEntry > 6 || sizeOfEntry < 5) {
							MessageUtil.setMessage("Please provide 5 / 6 digits Zip code / Pin.","color:red;");
							return ;
						}
						
						if(!validateNum(pinTxtBoxId)) {
							MessageUtil.setMessage("Enter only number type value for the pin.","color:red;");
							return ;			
						}//if
						
						
						if(zip.length() > 6 || zip.length() < 5 ) {
							MessageUtil.setMessage("Please provide 5 / 6 digits Zip code / Pin.","color:red;");
							return ;
							
						}
						contact.setZip(zip);
						
					}else{
						contact.setZip(null);
					}
					
		String street = addrTxtBoxId.getValue().trim();
		String city = cityTxtBoxId.getValue().trim();
		String state = stateTxtBoxId.getValue().trim();
		String country = countryTxtBoxId.getValue().trim();
		
		
		
		contact.setAddressOne(street.equalsIgnoreCase("Enter Street") ? null: street);
		contact.setCity(city.equalsIgnoreCase("Enter City") ? null: city);
		contact.setState(state.equalsIgnoreCase("Enter State") ? null: state);
		contact.setCountry(country.equalsIgnoreCase("Enter Country") ? null: country);
		String storeName="";
		if(!storeListBxId.getSelectedItem().getLabel().trim().equals("Select your store")){
			//storeName = storeListBxId.getSelectedItem().getLabel().trim();((OrganizationStores)storeItem.getValue()).getHomeStoreId()
			
			storeName = ((OrganizationStores)(storeListBxId.getSelectedItem()).getValue()).getHomeStoreId();
		}else{
			storeName = null;
		}
		
		contact.setHomeStore(storeName);
		Contacts contactOriginal = contactsDao.findById(contact.getContactId());
		if(Utility.isModifiedContact(contact,contactOriginal )){
			logger.info("entered Modified date");
		contact.setModifiedDate(Calendar.getInstance());
		}
		contactsDaoForDML.saveOrUpdate(contact);
//		LoyaltyProgramHelper.updateLoyaltyMembrshpPhone(contact, mobilePhone);
		//for updating the links under "How would you like us to reach you?" section.
		
		//for Email:
		modifyAId.setLabel("Modify");
		String valueInEmailTxtbx = emailTxtBoxId.getValue().trim();
		
		String statusOfSubUnsubStr = unsubscribesDao.isAlreadyUnsubscribedContact(userId, valueInEmailTxtbx);
		if(statusOfSubUnsubStr == null) 
			unsubsAId.setLabel("Unsubscribe");
		else
			unsubsAId.setLabel("Resubscribe");
		
		/*if(contact.getEmailStatus().equals(Constants.CS_STATUS_UNSUBSCRIBED)){
			
			unsubsAId.setLabel("Resubscribe");
			
		}else{
			unsubsAId.setLabel("Unsubscribe");
		}*/
		emailTxtBoxId.setDisabled(true);
		
		//for SMS:
		modifySmsAId.setLabel("Modify");
		String valueInMobileTxtbx = mblTxtBoxId.getValue().trim();
		if(smsSuppressedContactsDao.isMobileOptedOut(userId, valueInMobileTxtbx) == false)
			optoutAId.setLabel("Opt-out");
		else
			optoutAId.setLabel("Opt-in");	
		
		/*if(contact.getMobileStatus().equals(Constants.CON_MOBILE_STATUS_OPTED_OUT)){
			optoutAId.setLabel("Opt-in");
		}else{
			
			optoutAId.setLabel("Opt-out");
		}*/
		mblTxtBoxId.setDisabled(true);
		
		//for Postal mail:
		modifyAddrAId.setLabel("Modify");
		addrTxtBoxId.setDisabled(true);
		cityTxtBoxId.setDisabled(true);
		stateTxtBoxId.setDisabled(true);
		countryTxtBoxId.setDisabled(true);
		pinTxtBoxId.setDisabled(true);
		
		
		setContactDetailsInTop(contact);
		MessageUtil.setMessage("Settings saved successfully", "color:blue");
		
		
		
	}//onClick$saveBtnId
	
	public void getStroeIds(long orgId){
		
		List<OrganizationStores> storeList = organizationStoresDao.findByOrganization(orgId);
		if(storeList == null || storeList.size() == 0) return;
		for (OrganizationStores organizationStores : storeList) {
			
			Listitem tempItem= null;
			//tempItem = new Listitem(organizationStores.getHomeStoreId(),organizationStores);
			tempItem = new Listitem(organizationStores.getStoreName(),organizationStores);
			tempItem.setParent(storeListBxId);
			
		}
		
		
	}
	private Button editBtnId;
	
	private A editAId;
	public void onClick$editAId(){

		if(contact ==null){
			
		 contact = contactsDao.findById(cid);
		}
		
		if(editAId.getLabel().equalsIgnoreCase("Edit")){
			
			fnameTxtId.setDisabled(false);
			lnameTxtId.setDisabled(false);
			emailTxtId.setDisabled(false);
			mblTxtId.setDisabled(false);
			pinTxtId.setDisabled(false);
			
			
			editAId.setLabel("Save");
			
			
		} else {
			fnameTxtId.setDisabled(true);
			lnameTxtId.setDisabled(true);
			emailTxtId.setDisabled(true);
			mblTxtId.setDisabled(true);
			pinTxtId.setDisabled(true);
			
			String fname=fnameTxtId.getValue().trim();
			String lname=lnameTxtId.getValue().trim();
			
			contact.setFirstName(fname.equals("First Name") ? null :fname);
			contact.setLastName(lname.equals("Last Name") ? null :lname);
			
			
			// Email validation
			String emailStr=emailTxtId.getValue().trim();
			
			if(emailStr.equals("Primary Email Address")){
				emailStr="";
			}
			
			if(!emailStr.isEmpty() ){
				if((contact.getEmailId()!= null &&  ! contact.getEmailId().trim().equalsIgnoreCase(emailStr) )) {
					
				
				if(!Utility.validateEmail(emailStr)) {
					logger.debug( " emailString :"+emailStr);
					MessageUtil.setMessage("Please enter a valid email address.", "color:red", "TOP");
					//editAId.setLabel("Edit");
					emailTxtId.setDisabled(false);
					editAId.setLabel("Save");
					return ;
				}else{

					String retVal = PurgeList
							.checkForValidDomainByEmailId(emailStr);

					if (retVal != null) {

						MessageUtil.setMessage("Invalid Domain / not a mail server:", "color:red", "TOP");
						logger.debug("Invalid Domain / not a mail server: "
								+ emailStr);
						contact.setEmailStatus(retVal);

					}else{
						 /*String StratusStr = unsubsAId.getAttribute("status").toString();
						contact.setEmailStatus(StratusStr)*/;
						contact.setEmailStatus(Constants.CON_STATUS_ACTIVE);
					}
					
				}
				
				}
				contact.setEmailId(emailStr);
				
				
			}else{
				contact.setEmailStatus(Constants.CONT_STATUS_INVALID_EMAIL);
				contact.setEmailId(null);
			}
			
			// Phone validation
			String	mobilePhone = mblTxtId.getValue().trim();
			
			if(mobilePhone.equals("MobileNumber")){
				logger.info("mobile is"+mobilePhone);
				mobilePhone= "" ;
				logger.info("mobile afetr is"+mobilePhone);
			}
			
			String conMobile= contact.getMobilePhone();
			
				if((mobilePhone != null && ! mobilePhone.isEmpty()) ){
					
						if ((conMobile != null && !conMobile.isEmpty()) && conMobile.equalsIgnoreCase(mobilePhone) ){
							
							mobilePhone = conMobile;
							
					}else{
					
					UserOrganization organization =contactOwner != null ? contactOwner.getUserOrganization() : null;
					
					mobilePhone = Utility.phoneParse(""+mblTxtId.getValue(), organization);
					if(mobilePhone == null ) {
						MessageUtil.setMessage("Invalid Phone number.", "color:red", "TOP");
						//editAId.setLabel("Edit");
						mblTxtId.setDisabled(false);
						editAId.setLabel("Save");
						return ;
					}/*else if((mobilePhone.length() < conMobile.length() && !conMobile.endsWith(mobilePhone) ) ||
							(conMobile.length() < mobilePhone.length() && !mobilePhone.endsWith(conMobile)) || (mobilePhone.length() == conMobile.length())){
						
						
					}*/
					contact.setMobilePhone(mobilePhone);
					
					contact.setMobileStatus(Constants.CON_STATUS_ACTIVE);
					
				}
				}else{
					contact.setMobilePhone(null);
					contact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
				}
				
					
				
			
			
			String zip =pinTxtId.getValue().trim();
			
			if(zip.equals("Postal Code")){
				zip = "";
			}
			if(!zip.isEmpty()) {
				
				int sizeOfEntry = pinTxtId.getValue().trim().length();
				if(sizeOfEntry > 6 || sizeOfEntry < 5) {
					MessageUtil.setMessage("Please provide 5 / 6 digits Zip code / Pin.","color:red;");
					return ;
				}
				
				if(!validateNum(pinTxtId)) {
					MessageUtil.setMessage("Enter only number type value for the pin.","color:red;");
					return ;			
				}//if
				
				
				if(zip.length() > 6 || zip.length() < 5 ) {
					MessageUtil.setMessage("Please provide 5 / 6 digits Zip code / Pin.","color:red;");
					//editAId.setLabel("Edit");
					pinTxtId.setDisabled(false);
					editAId.setLabel("Save");
					return ;
					
				}
				contact.setZip(zip);
				
			}else{
				contact.setZip(null);
			}
			Contacts contactOriginal = contactsDao.findById(contact.getContactId());
			if(Utility.isModifiedContact(contact,contactOriginal )){
				logger.info("entered Modified date");
			contact.setModifiedDate(Calendar.getInstance());
			}
			contactsDaoForDML.saveOrUpdate(contact);
			LoyaltyProgramHelper.updateLoyaltyMembrshpPhone(contact, mobilePhone);
			getContactDetails(contact);
			
			editAId.setLabel("Edit");
		}
		
	
		
	}
	
	// cancel handles
	public void onClick$cancelBtnId(){
		logger.info("enterd");
		/*Redirect.goTo(PageListEnum.EMPTY);
		Redirect.goTo(PageListEnum.UPDATE_SUBSCRIPTION);*/
		//defaultSettings();
		
		
		try {
			String updateSubscriptionLink =PropertyUtil.getPropertyValue("updateSubscriptionLink");
			updateSubscriptionLink =updateSubscriptionLink.replace("|^", "[").replace("^|", "]");
			if(sentId!=null)
				updateSubscriptionLink = updateSubscriptionLink.replace("[sentId]", EncryptDecryptUrlParameters.encrypt(sentId.toString()));
			if(cid!=null)
				updateSubscriptionLink = updateSubscriptionLink.replace("[cId]", EncryptDecryptUrlParameters.encrypt(cid.toString()));
			logger.info("updateSubscriptionLink :"+updateSubscriptionLink);
			Executions.sendRedirect(updateSubscriptionLink);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("onClick$cancelBtnId Exception:", e);
		}

	}
	
public boolean validateNum(Textbox txtbox) {
		
		
		//logger.debug("----just entered with the intbox======>"+txtbox);
		
		String tbErrorCss = "border:1px solid #F37373; background:#FFCFCF";
		String tbNormalCss = "border:1px solid #B2B0B1; background:url('/subscriber/zkau/web/1d1ebab6/zul/img/misc/text-bg.gif') repeat-x scroll 0 0 #FFFFFF";
		
		try {
		if(txtbox.isValid()){
		int str = Integer.parseInt(txtbox.getValue().trim());
		if(  str <= 0) {
			
			//txtbox.setStyle(tbErrorCss);
			
			
			return false;
			
		}//if
		txtbox.setStyle(tbNormalCss);
		}
		
		return true;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		txtbox.setStyle(tbErrorCss);
	logger.error("exception is", e);
		return false;
	}
	
}//validateNum(-)
	private void setContactDetailsInTop(Contacts contact){
		String emailStr= contact.getEmailId();
		String phoneStr=contact.getMobilePhone();
		String zipStr =contact.getZip();
		
		emailTxtId.setValue(emailStr == null || emailStr.isEmpty() ? "Primary Email Address":emailStr );
		mblTxtId.setValue(phoneStr == null || phoneStr.isEmpty() ?"MobileNumber": phoneStr );
		pinTxtId.setValue(zipStr == null  || zipStr.isEmpty() ? "Postal Code": zipStr );
	}
}
