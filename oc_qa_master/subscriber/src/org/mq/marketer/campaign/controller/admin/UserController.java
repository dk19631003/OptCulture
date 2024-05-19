package org.mq.marketer.campaign.controller.admin;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.EventTrigger;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltySettings;
import org.mq.marketer.campaign.beans.LoyaltyThresholdAlerts;
import org.mq.marketer.campaign.beans.LoyaltyThresholdBonus;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.LoyaltyTransactionExpiry;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.RetailProSalesCSV;
import org.mq.marketer.campaign.beans.SparkBaseLocationDetails;
import org.mq.marketer.campaign.beans.SuppressedContacts;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObservable;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObserver;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CampaignSentDao;
import org.mq.marketer.campaign.dao.CampaignSentDaoForDML;
import org.mq.marketer.campaign.dao.ContactSpecificDateEventsDao;
import org.mq.marketer.campaign.dao.ContactSpecificDateEventsDaoForDML;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.EventTriggerDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.EventTriggerEventsDao;
import org.mq.marketer.campaign.dao.EventTriggerEventsDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.RetailProSalesDaoForDML;
import org.mq.marketer.campaign.dao.SMSCampaignSentDao;
import org.mq.marketer.campaign.dao.SMSCampaignSentDaoForDML;
import org.mq.marketer.campaign.dao.SparkBaseCardDao;
import org.mq.marketer.campaign.dao.SparkBaseLocationDetailsDao;
import org.mq.marketer.campaign.dao.SuppressedContactsDao;
import org.mq.marketer.campaign.dao.SuppressedContactsDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptLtyMembshpPwd;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.marketer.sparkbase.SparkBaseServiceAsync;
import org.mq.marketer.sparkbase.transactionWsdl.AdjustmentResponse;
import org.mq.marketer.sparkbase.transactionWsdl.ErrorMessageComponent;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.loyalty.LoyaltyAutoCommGenerator;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.data.dao.LoyaltyAutoCommDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltySettingsDao;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDaoForDML;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDaoForDML;
import org.mq.optculture.exception.BaseDAOException;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.util.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.event.PagingEvent;

import au.com.bytecode.opencsv.CSVReader;

public class UserController extends GenericForwardComposer implements EventListener{

	private static final long serialVersionUID = 1L;
	Textbox searchBox;
	Grid customersGridId;
	Include editUserIncId;
	private List<Users> usersList;
	Listbox userOrgLbId,userNameLbId,usersPerPageLBId;
	Paging userPagingId;
	
	private static String allStr = "--All--";
	
	private ContactsDao contactsDao;
	private MailingListDao mailingListDao;
	private UsersDao usersDao;
	
	//Set<Long> posListIds = new HashSet<Long>(); 
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		logger.debug("-- Just Entered -- ");
		
		
		this.contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
		this.mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		this.usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		
		/*List<MailingList> mlList = mailingListDao.executeQuery(" FROM MailingList where listType='POS' ");
		for (MailingList mailingList : mlList) {
			posListIds.add(mailingList.getListId());
		}*/
		
		String style = "font-weight:bold;font-size:15px;color:#313031;" +
						"font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("List Users", "", style, true);
		
		setUserOrg();

		searchBox.setValue("");
		int totalSize = usersDao.getTotalCountOfAllUsers(null); 
		
		userPagingId.setTotalSize(totalSize);
		userPagingId.setActivePage(0);
		userPagingId.addEventListener("onPaging", this);
		
		//existingTriggersGrdId.setPaginal(triggerViewsPagingId);
		
		
		customersGridId.setRowRenderer(rowRender);
		customersGridId.setModel(getUsersModel(0, userPagingId.getPageSize(),null));
		customersGridId.setAttribute("editUserIncId", editUserIncId);
		
	}
	
	private void setUserOrg() {

		
		List<UserOrganization> orgList	= usersDao.findAllOrganizations();
		
		if(orgList == null) {
			logger.debug("no organization list exist from the DB...");
			return ;
		}
		
		Listitem tempList = new Listitem(allStr);
		tempList.setParent(userOrgLbId);
		
		Listitem tempItem = null;
		
		for (UserOrganization userOrganization : orgList) {
			
			//set Organization Name
			if(userOrganization.getOrganizationName() == null || userOrganization.getOrganizationName().trim().equals("")) continue;
			
			tempItem = new Listitem(userOrganization.getOrgExternalId().trim(),userOrganization.getUserOrgId());
			tempItem.setParent(userOrgLbId);
		} // for
		userOrgLbId.setSelectedIndex(0);
		
		
	} 
	
	public void onSelect$userOrgLbId() {
		Components.removeAllChildren(userNameLbId);
		searchBox.setValue("");

		
		if(userOrgLbId.getSelectedItem().getLabel().equals(allStr)) {
			Listitem tempList = new Listitem(allStr);
			tempList.setParent(userNameLbId);
			userNameLbId.setSelectedIndex(0);
			int totalSize = usersDao.getTotalCountOfAllUsers(searchBox.getValue()); 
			
			userPagingId.setTotalSize(totalSize);
			userPagingId.setActivePage(0);
			customersGridId.setModel(getUsersModel(0, userPagingId.getPageSize(),null));
			return;
		}
		List<Users> usersList = usersDao.getUsersByOrgIdAndUserId((Long)userOrgLbId.getSelectedItem().getValue(), null); //getPrimaryUsersByOrg((Long)userOrgLbId.getSelectedItem().getValue());
		
		if(usersList == null || usersList.size() == 0) {
			logger.debug("No users exists for the Selected Organization..");
			Listitem tempList = new Listitem(allStr);
			tempList.setParent(userNameLbId);
			int totalSize = 0; 
			
			userPagingId.setTotalSize(totalSize);
			userPagingId.setActivePage(0);
			customersGridId.setModel(new ListModelList(usersList));
			return;
		}
		
		Listitem tempList = new Listitem(allStr);
		tempList.setParent(userNameLbId);
		
		
		
		Listitem tempItem = null;
		for (Users users : usersList) {
			String userNameStr = Utility.getOnlyUserName(users.getUserName());
//			logger.debug("UserName is ::"+userNameStr);
			
			tempItem = new Listitem(userNameStr,users);
			tempItem.setParent(userNameLbId);
			
		} // for
		
		if(userNameLbId.getItemCount() > 0) {
			logger.debug("usersListBxId count is .."+userNameLbId.getItemCount());
			userNameLbId.setSelectedIndex(0);
		}
		int totalSize = usersDao.getTotalCountOfUsersByOrgIdAndUserId((Long)userOrgLbId.getSelectedItem().getValue(), null,searchBox.getValue()); 
		
		userPagingId.setTotalSize(totalSize);
		userPagingId.setActivePage(0);
		customersGridId.setModel(getUsersModel(0, userPagingId.getPageSize(),null));

	}
	
	
	public void onSelect$userNameLbId() {
		searchBox.setValue("");
		int totalSize = 0;
		if((userNameLbId.getSelectedItem().getLabel()).equals(allStr)) {
			totalSize = usersDao.getTotalCountOfUsersByOrgIdAndUserId((Long)userOrgLbId.getSelectedItem().getValue(), null,searchBox.getValue());
		}
		else {
			totalSize = usersDao.getTotalCountOfUsersByOrgIdAndUserId((Long)userOrgLbId.getSelectedItem().getValue(), ((Users) userNameLbId.getSelectedItem().getValue()).getUserId(),searchBox.getValue()); 
		}
		
		userPagingId.setTotalSize(totalSize);
		userPagingId.setActivePage(0);
		customersGridId.setModel(getUsersModel(0, userPagingId.getPageSize(),null));
//		customersGridId.setModel(new ListModelList(getUsersModel()));
	}
	
	public void onSelect$usersPerPageLBId() {
			String searchStr = searchBox.getValue();

		try {
			int count = Integer.parseInt(usersPerPageLBId.getSelectedItem().getLabel());
			
			customersGridId.setModel(getUsersModel(0,count,searchStr));
			//existingTriggersGrdId.setPaginal(triggerViewsPagingId);
			customersGridId.setRowRenderer(rowRender);
			
			userPagingId.setPageSize(count);
			userPagingId.setActivePage(0);
			
			//System.gc();
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}
		public void onEvent(Event event) throws Exception {
			// TODO Auto-generated method stub
			super.onEvent(event);
			if(event.getTarget() instanceof Paging) {
				
				String searchStr = searchBox.getValue();
				
				Paging paging = (Paging)event.getTarget();
				
				int desiredPage = paging.getActivePage();
				
				
				paging.setActivePage(desiredPage);
				//paging.addEventListener("onPaging", this);

				//PagingEvent pagingEvent = (PagingEvent) event;
				
				PagingEvent pagingEvent = (PagingEvent) event;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;
				
				customersGridId.setModel(getUsersModel(ofs, (byte) pagingEvent.getPageable().getPageSize(),searchStr));
				//existingTriggersGrdId.setPaginal(triggerViewsPagingId);
				customersGridId.setRowRenderer(rowRender);
			} 
		}

public void onClick$fetchCountLbId() {
		
		logger.debug("===Calling delete====");
		deleteDuplicateMobilesWithNoEmail(1);
		
		
	}
	
public void onClick$fetchCountLbId1() {
		
		logger.debug("===Calling delete====");
		deleteDuplicateMobilesWithNoEmail(2);
		
		
	}
	
	private Queue<Object> handleMerge(Contacts earliestContact, Contacts latestContact, boolean mergeForSameContact ) {
		
		
		String emailStatus = earliestContact.getEmailStatus();
		Queue<Object> returnObj = new LinkedList();
		Boolean dontDeleteFlag = false;
		returnObj.add(earliestContact);
		//TODO for email contacts
		if(!mergeForSameContact && latestContact.getEmailId()!=null ) { //no need to handle when this is called for same email id contact
			if(earliestContact.getEmailId() == null) {
				
				earliestContact.setEmailId(latestContact.getEmailId());
				earliestContact.setEmailStatus(latestContact.getEmailStatus());
				earliestContact.setOptin(latestContact.getOptin());
				earliestContact.setPurged(latestContact.getPurged());
				
			}
			else if(earliestContact.getEmailId() != null && latestContact.getEmailId() != null &&
					!latestContact.getEmailId().equalsIgnoreCase(earliestContact.getEmailId())){
				
				dontDeleteFlag = true;
				//returnObj.add(dontDeleteFlag);
				
				
			}else if(earliestContact.getEmailId() != null && latestContact.getEmailId() != null &&
					latestContact.getEmailId().equalsIgnoreCase(earliestContact.getEmailId())){
				
				handleMerge( earliestContact, latestContact, true);
				//handle merge with latest contact and delete the latest one
				
				
			}
			//if(priContact.getEmailStatus()==null) 
			
			/*if(!latestContact.getEmailStatus().equalsIgnoreCase(Constants.CONT_STATUS_UNSUBSCRIBED) && 
					!latestContact.getEmailStatus().equalsIgnoreCase(Constants.CONT_STATUS_BOUNCED) &&  )
			*/
			
			
			/*//if(priContact.getOptin()==0) 
				priContact.setOptin(contact.getOptin());
			//if(priContact.getPurged()==null) 
				priContact.setPurged(contact.getPurged());*/
		}
		
		
		
		if(latestContact.getFirstName()!=null) earliestContact.setFirstName(latestContact.getFirstName());
		if(latestContact.getLastName()!=null) earliestContact.setLastName(latestContact.getLastName());

		//TODO for email contacts
		/*if(priContact.getLastStatusChange()==null || 
				(contact.getLastStatusChange()!=null && priContact.getLastStatusChange().before(contact.getLastStatusChange()))) 
			priContact.setLastStatusChange(contact.getLastStatusChange());*/
		Calendar lastMailDate = (earliestContact.getLastMailDate() == null ? latestContact.getLastMailDate() : 
			(earliestContact.getLastMailDate() != null && latestContact.getLastMailDate() != null && 
			earliestContact.getLastMailDate().after(latestContact.getLastMailDate()) ? earliestContact.getLastMailDate() : latestContact.getLastMailDate()) );
		
		earliestContact.setLastMailDate(lastMailDate);
		/*if(latestContact.getLastMailDate()==null || 
				(contact.getLastMailDate()!=null && priContact.getLastMailDate().before(contact.getLastMailDate()))) 
			priContact.setLastMailDate(contact.getLastMailDate());
		*/
		//need to handle contact address, either set all address fields or none
		if(latestContact.getAddressOne()!=null) earliestContact.setAddressOne(latestContact.getAddressOne());
		if(latestContact.getAddressTwo()!=null) earliestContact.setAddressTwo(latestContact.getAddressTwo());
		if(latestContact.getCity()!=null) earliestContact.setCity(latestContact.getCity());
		if(latestContact.getState()!=null) earliestContact.setState(latestContact.getState());
		if(latestContact.getCountry()!=null) earliestContact.setCountry(latestContact.getCountry());
		if(latestContact.getZip()!=null) earliestContact.setZip(latestContact.getZip());
		//
		//if(latestContact.getMobilePhone()!=null) earliestContact.setMobilePhone(latestContact.getMobilePhone());
		if(latestContact.getSubscriptionType()!=null) earliestContact.setSubscriptionType(latestContact.getSubscriptionType());
		//if(latestContact.getMobileStatus()!=null) earliestContact.setMobileStatus(latestContact.getMobileStatus());
		if(latestContact.getUdf2()!=null) earliestContact.setUdf2(latestContact.getUdf2());
		if(latestContact.getUdf3()!=null) earliestContact.setUdf3(latestContact.getUdf3());
		if(latestContact.getUdf4()!=null) earliestContact.setUdf4(latestContact.getUdf4());
		if(latestContact.getUdf5()!=null) earliestContact.setUdf5(latestContact.getUdf5());
		if(latestContact.getUdf6()!=null) earliestContact.setUdf6(latestContact.getUdf6());
		if(latestContact.getUdf7()!=null) earliestContact.setUdf7(latestContact.getUdf7());
		if(latestContact.getUdf8()!=null) earliestContact.setUdf8(latestContact.getUdf8());
		if(latestContact.getUdf9()!=null) earliestContact.setUdf9(latestContact.getUdf9());
		if(latestContact.getUdf10()!=null) earliestContact.setUdf10(latestContact.getUdf10());
		if(latestContact.getUdf11()!=null) earliestContact.setUdf11(latestContact.getUdf11());
		if(latestContact.getUdf12()!=null) earliestContact.setUdf12(latestContact.getUdf12());
		if(latestContact.getUdf13()!=null) earliestContact.setUdf13(latestContact.getUdf13());
		if(latestContact.getUdf14()!=null) earliestContact.setUdf14(latestContact.getUdf14());
		if(latestContact.getUdf15()!=null) earliestContact.setUdf15(latestContact.getUdf15());
		if(latestContact.getUdf1()!=null) earliestContact.setUdf1(latestContact.getUdf1());
		if(latestContact.getGender()!=null) earliestContact.setGender(latestContact.getGender());
		if(latestContact.getBirthDay()!=null) earliestContact.setBirthDay(latestContact.getBirthDay());
		if(latestContact.getAnniversary()!=null) earliestContact.setAnniversary(latestContact.getAnniversary());
		if(latestContact.getOptedInto()!=null) earliestContact.setOptedInto(latestContact.getOptedInto());
		if(latestContact.getOptinPerType()!=null) earliestContact.setOptinPerType(latestContact.getOptinPerType());
		
		if(earliestContact.getOptinMedium()==null) earliestContact.setOptinMedium(latestContact.getOptinMedium());
		
		if(latestContact.getCategories() != null ) earliestContact.setCategories(latestContact.getCategories());
		if(latestContact.getLastSmsSpan() != 0 ) earliestContact.setLastSmsSpan(latestContact.getLastSmsSpan());
		if(latestContact.getLastMailSpan() != 0 ) earliestContact.setLastMailSpan(latestContact.getLastMailSpan());
		
		//no need to bother these three fields coz earliest contact is the one which only has the correct values when come to mobile
		/*String mobileStatus = earliestContact.getMobileStatus();
		boolean mobileOptin = earliestContact.isMobileOptin();
		Calendar mobileOptinDate = (earliestContact.getMobileOptinDate() == null && 
				latestContact.getMobileOptinDate() != null ? latestContact.getMobileOptinDate() : earliestContact.getMobileOptinDate());
		
		String mobileOptinSource = (earliestContact.getMobileOptinSource() == null && 
				latestContact.getMobileOptinSource() != null ? latestContact.getMobileOptinSource() : earliestContact.getMobileOptinSource());*/
		
		Calendar lastSMSDate = (earliestContact.getLastSMSDate() == null ? latestContact.getLastSMSDate() : 
			(earliestContact.getLastSMSDate() != null && latestContact.getLastSMSDate() != null && 
			earliestContact.getLastSMSDate().after(latestContact.getLastSMSDate()) ? earliestContact.getLastSMSDate() : latestContact.getLastSMSDate()) );
		
		
		/*if(!mobileStatus.equals(Constants.CON_MOBILE_STATUS_OPTED_OUT) && 
				!latestContact.getMobileStatus().equalsIgnoreCase()) earliestContact.setMobileStatus(latestContact.getMobileStatus());
		
		earliestContact.setMobileOptin(latestContact.isMobileOptin());
		earliestContact.setMobileOptinDate( latestContact.getMobileOptinDate() != null && );
		earliestContact.setMobileOptinDate(latestContact.getMobileOptinDate());
		
		*/	
		earliestContact.setLastSMSDate(lastSMSDate);
		
		if(latestContact.getActivityDate()!=null) earliestContact.setActivityDate(latestContact.getActivityDate());
		//if(latestContact.getExternalId()!=null) earliestContact.setExternalId(latestContact.getExternalId());
		if(latestContact.getHomeStore()!=null) earliestContact.setHomeStore(latestContact.getHomeStore());
		if(latestContact.getHpId()!=null) earliestContact.setHpId(latestContact.getHpId());
		if(latestContact.getLoyaltyCustomer()!=null) earliestContact.setLoyaltyCustomer(latestContact.getLoyaltyCustomer());
		
		earliestContact.setMlBits(earliestContact.getMlBits()|latestContact.getMlBits());
		
		returnObj.add(dontDeleteFlag);
		returnObj.add(earliestContact);
		
		return returnObj ;
		
		
	}
		
	private void deleteDuplicateMobilesWithNoEmail(int phase){
		
		String countQry = null;
		String qry = null;
		if(phase == 1) {
			//STEP:#1 First do the count of contacts having only mobile numbers
			countQry = " SELECT COUNT(c.contactId) FROM Contacts c WHERE c.users = 607 AND c.externalId IS  NULL AND c.emailId IS NULL " +
					" AND c.mobilePhone IS NOT NULL ";
			
			//STEP:#2 contacts those having only mobile numbers
			qry= " FROM Contacts c WHERE c.users = 607 AND c.externalId IS  NULL AND c.emailId IS NULL " +
				    " AND c.mobilePhone IS NOT NULL  ";
		}else if(phase == 2) {
			//before running this second phase try to unmark already marked contacts in the first phase
			
			//PHASE#2
				//STEP:#1 First do the count of contacts having only mobile numbers
			countQry = " SELECT COUNT(c.contactId) FROM Contacts c WHERE c.users = 607 AND c.externalId IS  NULL AND c.emailId IS NOT NULL " +
					" AND c.mobilePhone IS NOT NULL ";
			
			//STEP:#2 contacts those having only mobile numbers
			qry= " FROM Contacts c WHERE c.users = 607 AND c.externalId IS  NULL AND c.emailId IS NOT NULL" +
				    " AND c.mobilePhone IS NOT NULL  ";
			 	
			/*countQry = " SELECT COUNT(c.contactId) FROM Contacts c WHERE c.users = 607 AND c.externalId IS  NULL AND c.emailId IS NULL " +
					" AND c.mobilePhone IS NOT NULL ";
			
			//STEP:#2 contacts those having only mobile numbers
			qry= " FROM Contacts c WHERE c.users = 607 AND c.externalId IS  NULL AND c.emailId IS NULL " +
				    " AND c.mobilePhone IS NOT NULL  ";*/
			
		}
		
		//STEP:#3 for some count get those many contacts and for each such contact get all the
		//contacts with mobile and marked all contacts as considered
		ContactsDao contactsDao = (ContactsDao)SpringUtil.getBean(OCConstants.CONTACTS_DAO);
		ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)SpringUtil.getBean(OCConstants.CONTACTS_DAO_FOR_DML);
		Long totCount = contactsDao.getCountByCountQuery(countQry);
		logger.debug("===got count ==="+totCount);
		int size = 100;
		for(int i=0; i<totCount; i+=size) {
			
			List<Contacts> retList = contactsDao.executeQuery(qry, i, size);
			logger.debug("===got List size ==="+retList.size());
			
			for (Contacts contact : retList) {
				
				Contacts actualContact = contactsDao.findById(contact.getContactId());
				
				if(actualContact == null || actualContact.isMark()) {
					logger.debug("===deleted / marked  ===");
					continue;//already considered for check / deletion
				}
				//STEP:#4 each contact get all other contacts with its mobile number
				String mobileNumber = actualContact.getMobilePhone();
				if(mobileNumber.startsWith("91")) {
					
					mobileNumber = mobileNumber.substring(2);
				}
				List<Contacts> mobileContacts = null;
				if(phase == 1) {
					//PHASE #1
					mobileContacts = contactsDao.executeQuery(" FROM Contacts WHERE users = 607 "
						+ " AND externalId IS  NULL AND emailId IS NULL  AND mobilePhone IS NOT NULL AND "
						+ " mobilePhone LIKE '%"+mobileNumber+"' and contactId NOT IN("+actualContact.getContactId().longValue()+")");
				}else if(phase == 2) {
					
					//PHASE #2
					mobileContacts = contactsDao.executeQuery(" FROM Contacts WHERE users = 607 "
							+ "  AND emailId IS NOT NULL  AND mobilePhone IS NOT NULL AND "
							+ " mobilePhone LIKE '%"+mobileNumber+"' and contactId NOT IN("+actualContact.getContactId().longValue()+")");
					
					
				}
				
				if(mobileContacts == null || mobileContacts.size() == 0) {
					
					logger.debug("===Unique mobile number=== "+mobileNumber);
					continue;
				}
				List<Contacts> deletionList = new ArrayList<Contacts>();
				Contacts earliestContact = actualContact;
				for (Contacts mobileContact : mobileContacts) {
					
					//STEP:#
					Calendar actualContactCrDate = actualContact.getCreatedDate();
					Calendar mobileContactCrDate = mobileContact.getCreatedDate();
					
					boolean isTakeActual = true;
					
					Contacts latestContact = mobileContact;
					if(actualContactCrDate != null && mobileContactCrDate != null) {
						
						if(mobileContactCrDate.before(actualContact)){
							isTakeActual = false;
						}//if
						
					}
					if(!isTakeActual) {
						earliestContact = mobileContact;
						 latestContact = actualContact;
					}
					logger.debug("===calling hanlemerge=== ");
					//handle merge logic and then add 
					Queue<Object> retObj =  handleMerge(earliestContact, latestContact, false);
					
					Object[] retArr = retObj.toArray();
					boolean dontDeleteFlag = (Boolean)retArr[1];
					earliestContact = (Contacts)retArr[2];
					
					earliestContact.setMark(true);
					
					if(!dontDeleteFlag){
						
						deletionList.add(latestContact);
						
					}else{
						
						latestContact.setMark(true);
						contactsDaoForDML.saveOrUpdate(latestContact);
					}
					
					
				}//for
				contactsDaoForDML.saveOrUpdate(earliestContact);
				if(deletionList.size() > 0) {
					
					logger.debug("===deletionList.size()=== "+deletionList.size());
					String cidStr = Constants.STRING_NILL;
					
					for (Contacts delCon : deletionList) {
						
						if(!cidStr.isEmpty()) cidStr += Constants.DELIMETER_COMMA;
						
						cidStr += delCon.getContactId().longValue();
						
					}
					
					logger.debug("===cidStr=== "+cidStr);
					
					if(!cidStr.isEmpty()) {
						//update all dependent tables
						RetailProSalesDaoForDML retailProSalesDaoForDML = (RetailProSalesDaoForDML)SpringUtil.getBean("retailProSalesDaoForDML");
						CampaignSentDaoForDML campaignSentDaoForDML = (CampaignSentDaoForDML)SpringUtil.getBean(OCConstants.CAMPAIGN_SENT_DAO_FOR_DML);
						SMSCampaignSentDaoForDML smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML)SpringUtil.getBean("smsCampaignSentDaoForDML");
						EventTriggerEventsDaoForDML eventTriggerEventsDaoForDML = (EventTriggerEventsDaoForDML)SpringUtil.getBean("eventTriggerEventsDaoForDML");
						ContactSpecificDateEventsDaoForDML contactSpecificDateEventsDaoForDML = (ContactSpecificDateEventsDaoForDML)SpringUtil.getBean("contactSpecificDateEventsDaoForDML");
						
						retailProSalesDaoForDML.executeUpdate("UPDATE RetailProSalesCSV set cid="+earliestContact.getContactId()+ " WHERE cid IN ("+cidStr+")");
						campaignSentDaoForDML.executeUpdate("UPDATE CampaignSent set contactId="+earliestContact.getContactId()+ " WHERE contactId IN ("+cidStr+")");
						smsCampaignSentDaoForDML.executeUpdate("UPDATE SMSCampaignSent set contactId="+earliestContact.getContactId()+ " WHERE contactId IN ("+cidStr+")");
						eventTriggerEventsDaoForDML.executeUpdate("UPDATE EventTriggerEvents set contactId="+earliestContact.getContactId()+ " WHERE contactId IN ("+cidStr+")");
						contactSpecificDateEventsDaoForDML.executeUpdate("UPDATE ContactSpecificDateEvents set contactId="+earliestContact.getContactId()+ " WHERE contactId IN ("+cidStr+")");
						
						contactsDaoForDML.executeUpdateQuery("DELETE FROM contacts WHERE cid IN("+cidStr+")");
					}
					
					
				}
			}
			
			
		}//for
		
		
	}
	
	//Longbox startFromCountLbId, maxCountLbId, fetchCountLbId;
	
	/*private void handleMerge(List<Contacts> tempLis, int optNum) {
		Contacts priContact=null;
		
		for (Contacts contact : tempLis) {
			if(posListIds.contains(contact.getMailingList().getListId())) {
				//logger.info("POS="+contact);
				if(priContact==null) {
					priContact=contact;
				}
				else if(contact.getLoyaltyCustomer() != null && contact.getLoyaltyCustomer().intValue()==1) {
					priContact=contact;
				}
			}
		} // for
		
		if(priContact==null) {
			for (Contacts contact : tempLis) {
				
				if(contact.getLastMailDate()!=null) {
					
					if(priContact==null) {
						priContact=contact;
					}
					else if(priContact.getLastMailDate()!=null) {
						if(contact.getLastMailDate().after(priContact.getLastMailDate())) {
							priContact=contact;
						}
					}
				}
			} // for
		} // if
		
		
		for (Contacts contact : tempLis) {
			if(priContact==null) {
				priContact=contact;
				continue;
			}
			
			if(priContact.getContactId()==contact.getContactId()) continue; // skip the POS contact
			
			if(priContact.getEmailId()==null) {
				priContact.setEmailId(contact.getEmailId());
				//if(priContact.getEmailStatus()==null) 
					priContact.setEmailStatus(contact.getEmailStatus());
				//if(priContact.getOptin()==0) 
					priContact.setOptin(contact.getOptin());
				//if(priContact.getPurged()==null) 
					priContact.setPurged(contact.getPurged());
			}
			
			
			if(priContact.getFirstName()==null) priContact.setFirstName(contact.getFirstName());
			if(priContact.getLastName()==null) priContact.setLastName(contact.getLastName());
			if(priContact.getCreatedDate()==null) priContact.setCreatedDate(contact.getCreatedDate());

			if(priContact.getLastStatusChange()==null || 
					(contact.getLastStatusChange()!=null && priContact.getLastStatusChange().before(contact.getLastStatusChange()))) 
				priContact.setLastStatusChange(contact.getLastStatusChange());
			
			if(priContact.getLastMailDate()==null || 
					(contact.getLastMailDate()!=null && priContact.getLastMailDate().before(contact.getLastMailDate()))) 
				priContact.setLastMailDate(contact.getLastMailDate());
			//need to handle contact address, either set all address fields or none
			if(priContact.getAddressOne()==null) priContact.setAddressOne(contact.getAddressOne());
			if(priContact.getAddressTwo()==null) priContact.setAddressTwo(contact.getAddressTwo());
			if(priContact.getCity()==null) priContact.setCity(contact.getCity());
			if(priContact.getState()==null) priContact.setState(contact.getState());
			if(priContact.getCountry()==null) priContact.setCountry(contact.getCountry());
			if(priContact.getPin()==0) priContact.setPin(contact.getPin());
			//
			if(priContact.getPhone()==null) priContact.setPhone(contact.getPhone());
			if(priContact.getSubscriptionType()==null) priContact.setSubscriptionType(contact.getSubscriptionType());
			if(priContact.getMobileStatus()==null) priContact.setMobileStatus(contact.getMobileStatus());
			if(priContact.getUdf2()==null) priContact.setUdf2(contact.getUdf2());
			if(priContact.getUdf3()==null) priContact.setUdf3(contact.getUdf3());
			if(priContact.getUdf4()==null) priContact.setUdf4(contact.getUdf4());
			if(priContact.getUdf5()==null) priContact.setUdf5(contact.getUdf5());
			if(priContact.getUdf6()==null) priContact.setUdf6(contact.getUdf6());
			if(priContact.getUdf7()==null) priContact.setUdf7(contact.getUdf7());
			if(priContact.getUdf8()==null) priContact.setUdf8(contact.getUdf8());
			if(priContact.getUdf9()==null) priContact.setUdf9(contact.getUdf9());
			if(priContact.getUdf10()==null) priContact.setUdf10(contact.getUdf10());
			if(priContact.getUdf11()==null) priContact.setUdf11(contact.getUdf11());
			if(priContact.getUdf12()==null) priContact.setUdf12(contact.getUdf12());
			if(priContact.getUdf13()==null) priContact.setUdf13(contact.getUdf13());
			if(priContact.getUdf14()==null) priContact.setUdf14(contact.getUdf14());
			if(priContact.getUdf15()==null) priContact.setUdf15(contact.getUdf15());
			if(priContact.getUdf1()==null) priContact.setUdf1(contact.getUdf1());
			if(priContact.getGender()==null) priContact.setGender(contact.getGender());
			if(priContact.getBirthDay()==null) priContact.setBirthDay(contact.getBirthDay());
			if(priContact.getAnniversary()==null) priContact.setAnniversary(contact.getAnniversary());
			if(priContact.getOptedInto()==null) priContact.setOptedInto(contact.getOptedInto());
			if(priContact.getOptinPerType()==null) priContact.setOptinPerType(contact.getOptinPerType());
			if(priContact.getOptinMedium()==null) priContact.setOptinMedium(contact.getOptinMedium());
			if(priContact.getActivityDate()==null) priContact.setActivityDate(contact.getActivityDate());
			if(priContact.getExternalId()==null) priContact.setExternalId(contact.getExternalId());
			if(priContact.getHomeStore()==null) priContact.setHomeStore(contact.getHomeStore());
			if(priContact.getHpId()==null) priContact.setHpId(contact.getHpId());
			//if(priContact.getLoyaltyCustomer()==null) priContact.setLoyaltyCustomer(contact.getLoyaltyCustomer());
			
			priContact.setMlBits(priContact.getMlBits()|contact.getMlBits());
			
			contact.setUdf14("NEEDTODELETE_"+priContact.getContactId());
			
		} // for
		
		
		contactsDao.saveByCollection(tempLis);
		
	}
	
	
	public void onClick$genExtIdBtnId() {
		try {
			String qry=" FROM Contacts c WHERE c.externalId IS NOT NULL and udf14 IS NULL " +
					" ORDER BY c.users, c.externalId ";
			
			List<Contacts> tempList = new ArrayList<Contacts>();
			
			List<Contacts> list = null;
			int currInd=1;
			Contacts prevContact=null;
			
			int size=1000;
			Long maxCount = maxCountLbId.getValue();
			Long stfCount = startFromCountLbId.getValue();
			Long fetchCount = fetchCountLbId.getValue();
			
			if(stfCount!=null && stfCount.intValue()>0) currInd=stfCount.intValue();
			if(fetchCount!=null && fetchCount.intValue()>0) size=fetchCount.intValue();
			
			while(true) {
				
				if(maxCount!=null && maxCount>0 && currInd>maxCount.longValue()) break;
				
				logger.debug(">>>>>>>>> CurrIndex="+currInd);
				list = contactsDao.executeQuery(qry, currInd, size);
				
				if(list==null || list.isEmpty()) break;
				
				for (Contacts contact : list) {
					
					//logger.info("CURR exCount: "+exCount);
					
					if(prevContact==null) {
						
						prevContact=contact;
						logger.info("FIRST Contact: "+contact);
						tempList.add(contact);
						continue;
					}
					
/*					if(prevContact.getExternalId()==null && contact.getExternalId()==null) {
						tempList.add(contact);
						continue;
					}
*/					
					
	/*				if(prevContact.getUsers().getUserId().longValue()==contact.getUsers().getUserId().longValue() && 
							prevContact.getExternalId().equals(contact.getExternalId())) {
						tempList.add(contact);
						continue;
					}
					
					
					if(tempList.size()>1) {
						logger.info("==Need TO WorkON: "+tempList);
						handleMerge(tempList, 1);
					}
					
					prevContact=contact;
					tempList.clear();
					tempList.add(contact);
					
				} // for
				
				currInd+=size;
			} // while
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}
	
	
	public void onClick$genEmailBtnId() {
		try {
			String qry=" FROM Contacts c WHERE c.externalId IS NULL and c.emailId IS NOT NULL and udf14 IS NULL " +
					" ORDER BY c.users, c.externalId, c.emailId ";
			
			List<Contacts> tempList = new ArrayList<Contacts>();
			
			List<Contacts> list = null;
			int currInd=1;
			Contacts prevContact=null;
			
			int size=1000;
			
			Long maxCount = maxCountLbId.getValue();
			Long stfCount = startFromCountLbId.getValue();
			Long fetchCount = fetchCountLbId.getValue();
			
			if(stfCount!=null && stfCount.intValue()>0) currInd=stfCount.intValue();
			if(fetchCount!=null && fetchCount.intValue()>0) size=fetchCount.intValue();
			
			
			while(true) {
				//if(currInd>1000) break;
				if(maxCount!=null && maxCount>0 && currInd>maxCount.longValue()) break;
				logger.debug(">>>>>>>>> CurrIndex="+currInd);
				list = contactsDao.executeQuery(qry, currInd, size);
				
				if(list==null || list.isEmpty()) break;
				
				for (Contacts contact : list) {
					
					//logger.info("CURR contact: "+contact);
					
					if(prevContact==null) {
						
						prevContact=contact;
						logger.info("FIRST Contact: "+contact);
						tempList.add(contact);
						continue;
					}
					
/*					if(prevContact.getExternalId()==null && contact.getExternalId()==null) {
						tempList.add(contact);
						continue;
					}
*/					
					
	/*				if(prevContact.getUsers().getUserId().longValue()==contact.getUsers().getUserId().longValue() &&
							prevContact.getEmailId().equals(contact.getEmailId())) {
						tempList.add(contact);
						continue;
					}
					
					
					if(tempList.size()>1) {
						logger.info("==Need TO WorkON: "+tempList);
						handleMerge(tempList, 1);
					}
					
					prevContact=contact;
					tempList.clear();
					tempList.add(contact);
					
				} // for
				
				currInd+=size;
			} // while
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}
	
	
	public void onClick$genPhoneBtnId() {
		try {
			String qry=" FROM Contacts c WHERE c.externalId IS NULL and c.emailId IS NULL " +
					" and c.phone IS NOT NULL and udf14 IS NULL " +
					" ORDER BY c.users, c.externalId, c.emailId, c.phone ";
			
			List<Contacts> tempList = new ArrayList<Contacts>();
			
			List<Contacts> list = null;
			int currInd=1;
			Contacts prevContact=null;
			
			int size=1000;
			
			Long maxCount = maxCountLbId.getValue();
			Long stfCount = startFromCountLbId.getValue();
			Long fetchCount = fetchCountLbId.getValue();
			
			if(stfCount!=null && stfCount.intValue()>0) currInd=stfCount.intValue();
			if(fetchCount!=null && fetchCount.intValue()>0) size=fetchCount.intValue();
			
			
			while(true) {
				//if(currInd>1000) break;
				if(maxCount!=null && maxCount>0 && currInd>maxCount.longValue()) break;
				logger.debug(">>>>>>>>> CurrIndex="+currInd);
				list = contactsDao.executeQuery(qry, currInd, size);
				
				if(list==null || list.isEmpty()) break;
				
				for (Contacts contact : list) {
					
					//logger.info("CURR contact: "+contact);
					
					if(prevContact==null) {
						
						prevContact=contact;
						logger.info("FIRST Contact: "+contact);
						tempList.add(contact);
						continue;
					}
					
/*					if(prevContact.getExternalId()==null && contact.getExternalId()==null) {
						tempList.add(contact);
						continue;
					}
*/					
					
		/*			if(prevContact.getUsers().getUserId().longValue()==contact.getUsers().getUserId().longValue() &&
							prevContact.getPhone().longValue() == contact.getPhone().longValue()) {
						tempList.add(contact);
						continue;
					}
					
					
					if(tempList.size()>1) {
						logger.info("==Need TO WorkON: "+tempList);
						handleMerge(tempList, 1);
					}
					
					prevContact=contact;
					tempList.clear();
					tempList.add(contact);
					
				} // for
				
				currInd+=size;
			} // while
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}
		
	*/
	
	
	public void onChanging$searchBox(InputEvent event) {
		String searchStr = event.getValue();
		if(searchStr.contains("'")){
			MessageUtil.setMessage("Please enter valid user name without using single quotes.", "color:red", "TOP");
			return;
		}
		
		/*LinkedList<Users> item = new LinkedList<Users>();
		
		if(usersList == null && usersList.size() < 1) return;
		
		logger.debug("Total Users Count :" + usersList.size());
		Users user;
		if (key.trim().length() != 0) {
			for (int i = 0; i < usersList.size(); i++) {
				user = (Users)usersList.get(i);
				if (Utility.getOnlyUserName(user.getUserName()).toLowerCase()
						.indexOf(key.toLowerCase()) != -1 && Utility.getOnlyUserName(user.getUserName()).toLowerCase()
						.indexOf(key.toLowerCase()) == 0)
					item.add(user);
			}*/
//			customersGridId.setModel(new ListModelList(item));
//		} 
//		else customersGridId.setModel(new ListModelList(usersList));
		int totalSize = 0;
		String selUserOrg = userOrgLbId.getSelectedItem().getLabel();
		Long selUserOrgId = null;
		if(!selUserOrg.equals(allStr )) {
			selUserOrgId = userOrgLbId.getSelectedItem().getValue();
		}
		
		String selUserName = userNameLbId.getSelectedItem().getLabel();
		Long selUserId = null;
		if(!selUserName.equals(allStr ) ) {
			selUserId = userNameLbId.getSelectedItem().getValue() == null ? null : ((Users) userNameLbId.getSelectedItem().getValue()).getUserId();
		}
		
		if(selUserOrgId == null) {
			totalSize = usersDao.getTotalCountOfAllUsers(searchStr);
		}
		else {
			totalSize = usersDao.getTotalCountOfUsersByOrgIdAndUserId(selUserOrgId, selUserId, searchStr);
		}
		userPagingId.setTotalSize(totalSize);
		userPagingId.setActivePage(0);
		customersGridId.setModel(getUsersModel(0, userPagingId.getPageSize(),searchStr));
	}
	
	public ListModel getUsersModel(int start, int end,String searchStr) {
		return new ListModelList(getCustomers(start, end, searchStr));
	}
	
	@SuppressWarnings("unchecked")
	public List<Users> getCustomers(int start, int end, String searchStr) {
//		UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
//		usersList = usersDao.findAll();
		
		String selUserOrg = userOrgLbId.getSelectedItem().getLabel();
		Long selUserOrgId = null;
		if(!selUserOrg.equals(allStr )) {
			selUserOrgId = userOrgLbId.getSelectedItem().getValue();
			
		}
		
		String selUserName = userNameLbId.getSelectedItem().getLabel();
		Long selUserId = null;
		if(!selUserName.equals(allStr )  ) {
			selUserId = userNameLbId.getSelectedItem().getValue() == null ? null : ((Users) userNameLbId.getSelectedItem().getValue()).getUserId();
		}
		
		if(selUserOrgId == null) {
			usersList = usersDao.findOrderbyAllUsers(searchStr, start, end);
		}
		else {
			usersList = usersDao.getUsersByOrgIdAndUserId(selUserOrgId, selUserId, start, end, searchStr);
		}
//		usersList = usersDao.findOrderbyAllUsers();
		logger.info("usersList size is :"+usersList.size());
		return usersList;
	}
	
	public RowRenderer getRowRenderer() {
		return rowRender;
	}
	
	private static final RowRenderer rowRender  = new MyRenderer();
	
	private static class MyRenderer implements RowRenderer, EventListener{
		MyRenderer() {
			super();
			logger.debug("new MyRenderer object is created");
		}
		
		public void render(Row row, java.lang.Object data,int arg2) {
			try {
				TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
				Calendar tempCal=null;
				if(data instanceof Users) {
					UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
					Users user = (Users) data;
//				new Checkbox().setParent(row);
					new Label(user.getUserId().toString()).setParent(row);
					String userName = Utility.getOnlyUserName(user.getUserName());
					A a = new A(userName);
					a.addEventListener("onClick", this);
					a.setAttribute("userObj", user);
					a.setParent(row);
					UserOrganization userOrg = usersDao.findByOrgName(Utility.getOnlyOrgId(user.getUserName()));
					new Label(userOrg.getOrganizationName()).setParent(row);
					new Label(user.getFirstName() + " " + user.getLastName()).setParent(row);
					new Label(user.getCompanyName()).setParent(row);
					new Label(user.getAddress()).setParent(row);
					new Label(user.getPhone()).setParent(row);
					
					tempCal=user.getCreatedDate();
					tempCal.setTimeZone(tz);
					new Label(MyCalendar.calendarToString(tempCal, MyCalendar.FORMAT_STDATE)).setParent(row);//(MyCalendar.calendarToString(campaignReport.getSentDate(),MyCalendar.FORMAT_DATETIME_STDATE, tz));
					
					new Label(user.getEmailCount()+ "").setParent(row);
					new Label(user.getUsedEmailCount()+ "").setParent(row);
					new Label(user.getSmsCount()+"").setParent(row);
					new Label(user.getUsedSmsCount()+"").setParent(row);
					
					
					tempCal = user.getPackageExpiryDate();
					if(tempCal != null) {
						
						tempCal.setTimeZone(tz);
					}
					new Label(MyCalendar.calendarToString(tempCal, MyCalendar.FORMAT_STDATE)).setParent(row);
					
					
					if (user.getVmta() != null) {
						//new Label(user.getVmta()).setParent(row);
						new Label(user.getVmta().getVmtaName()).setParent(row);
						new Label(user.isEnabled() + "").setParent(row);
					}else{
						new Label("--").setParent(row);
					}

//				row.addEventListener("onClick", this);
//				row.setStyle("cursor:pointer");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception while rendering the records ",e);
				
			}
		}
		
		@Override
		public void onEvent(Event evt) throws Exception {
			Object obj = evt.getTarget();
			
			try {
				
			if(obj instanceof A) {
				logger.debug("--Entered onClick Row --");
				A a = (A)obj;
				Rows rows = (Rows)(a.getParent()).getParent();
				Row row = (Row)rows.getAttribute("prevSelRow");
				if(row !=null ) {
					row.setStyle("cursor:pointer");
				}
				row = (Row)a.getParent();
				rows.setAttribute("prevSelRow", row);
				row.setStyle("background-color:#BFEAFF");
				Users user = (Users)a.getAttribute("userObj");
				if(user == null) return;
				
				Include inc = (Include)rows.getParent().getAttribute("editUserIncId");
				Sessions.getCurrent().setAttribute("editUserObj", user);
				Sessions.getCurrent().setAttribute("OC_Admin_User",GetUser.getUserObj());
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
	}
	
	private Textbox userIdTxtBoxId,userNameTxtBoxId,filenameTxtBoxId, totFilenameTxtBoxId;
	
	public void onClick$runCleanBtnId() {
		
		String userIdStr = userIdTxtBoxId.getValue().trim();
		
		String fileName = filenameTxtBoxId.getValue().trim();
		 
		String totalfileName = totFilenameTxtBoxId.getValue().trim();
		
		char delimiter=',';
		String[] nextLine;
		String[] totalNextLine;
		
		String userName = userNameTxtBoxId.getValue().trim();
		
		
		if(userIdStr.trim().isEmpty() || userName.isEmpty() || fileName.isEmpty()){
			
			MessageUtil.setMessage("Please enter all required inputs.", "color:red;");
			return ;
		}
		Long userId = Long.parseLong(userIdStr);
		
		String path = PropertyUtil.getPropertyValue("usersParentDirectory") + "/" + userName + "/List/";
		//String totalFilePath = "/home/proumyaa/workspace/subscriber/WebContent/";
		
		String totalFilePath = PropertyUtil.getPropertyValue("appDirectory")+"/";
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(path+fileName+"_filtered.csv"));
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			logger.error("Exception ", ex);
			MessageUtil.setMessage("filtered file path is incorrect", "color:red;");
			return ;
		}catch (Exception e2) {
			// TODO: handle exception
			logger.error("Exception ", e2);
		}
		
		StringBuffer sb = new StringBuffer();
		/*sb.append("\"EmailId\"");
		sb.append("\r\n");
		*/
		
		
		CSVReader userFileReader;
		
		try {
			logger.info("filepath "+path+fileName+".csv");
			userFileReader = new CSVReader(new FileReader(path+fileName+".csv" ), delimiter);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e1);
			MessageUtil.setMessage("given inputs are incorrect", "color:red;");
			return;
		}
		
		int totLines =0;
		int invalidCount = 0;
		int emailIdsFilteredCnt = 0;
		try {
			StringBuffer invalidSB = new StringBuffer();
			while ((nextLine = userFileReader.readNext()) != null) {
				totLines++;
				int lineTokenCount = nextLine.length;
				if(lineTokenCount==1 && nextLine[0]!=null && nextLine[0].trim().equals("")) {
			    	if(logger.isDebugEnabled()) logger.debug("Empty line");
			    	invalidSB.append(totLines + "  \r\n   ");
					invalidCount++;
			    	continue;
			    }
				
				String email = nextLine[0];
				
				
				boolean isFound = false;
				CSVReader totalBouncesFileReader;
				try {
					String totalFile = totalFilePath+totalfileName;
					logger.debug("totalFile ::"+totalFile);
					totalBouncesFileReader = new CSVReader(new FileReader(totalFile),delimiter);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					MessageUtil.setMessage("total file path is incorrect", "color:red;");
					return;
				}
				try {
				while ((totalNextLine = totalBouncesFileReader.readNext()) != null ) {
					
						//logger.debug("iteration number "+emailIdsFilteredCnt);
						if(totalNextLine[0]!=null && totalNextLine[0].trim().equals("") && !Utility.validateEmail(totalNextLine[0].trim())) {
							logger.debug("invalid entry ::"+totalNextLine[0].trim());
							continue;
						}
						//logger.debug("email ======= "+email+" totalNextLine[0].trim() ======"+totalNextLine[0].trim()+" isFOUND :: "+email.equalsIgnoreCase(totalNextLine[0].trim()));
						isFound = email.equalsIgnoreCase(totalNextLine[0].trim());
						
						if(isFound){
							//logger.debug("Exception");
							//logger.debug("isFound ::"+isFound);
							break;
						}
						
					
					
				}//while
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ", e);
					
				}finally{
					totalBouncesFileReader.close();
					
				}
				if(!isFound){
					emailIdsFilteredCnt ++;
					logger.debug("but in !isFound now "+emailIdsFilteredCnt);
					sb.append(email);
					sb.append("\r\n");
				}//if
				/*logger.debug("Email Name : " + email + " User Id : " + userId);
				if(!Utility.validateEmail(email.trim())) {
					if(logger.isDebugEnabled()) logger.debug("Invalid Email: " + email);
					invalidSB.append(totLines + "  " + email + "\r\n   ");
					invalidCount++;
					continue;
				}*/
				
				//if(totLines%1000==0) {  System.gc(); }
			}
			logger.debug("sb "+sb.toString());
			bw.write(sb.toString());
			try {
				bw.flush();
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("IO Exception ", e);
			}
			int confirm = Messagebox.show("file is ready for the user "+userName+
					" count is :"+emailIdsFilteredCnt+".\n do you want to ", 
					"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm != 1) {	return;	}
			
			deleteSuppressedContacts( userName, fileName+"_filtered.csv", userId);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("IO Exception ", e);
			MessageUtil.setMessage("IO Exception-1251", "color:red;");
			return;
		}catch (Exception e) {
			logger.error("IO Exception ", e);
		}
		
	}
	public void onClick$cleanBtnId() {
		String userName = userNameTxtBoxId.getValue().trim();
		
		String userIdStr = userIdTxtBoxId.getValue().trim();
		
		String fileName = filenameTxtBoxId.getValue().trim();
		
		if(userIdStr.trim().isEmpty() || userName.isEmpty() || fileName.isEmpty()){
			
			MessageUtil.setMessage("Please enter all required inputs.", "color:red;");
			return ;
		}
		Long userId = Long.parseLong(userIdStr);
		
		int confirm = Messagebox.show("file is ready for the user "+userName+
				".\n do you want to continue???", 
				"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
		if(confirm != 1) {	return;	}
		
		
		deleteSuppressedContacts( userName,fileName+"_filtered.csv", userId);
		
	}
	public void deleteSuppressedContacts(String userName, String fileName, Long userId) {
		
		char delimiter=',';
		String[] nextLine;
		int emailIdsCount = 0;
		String path = PropertyUtil.getPropertyValue("usersParentDirectory") + "/" + userName + "/List/";
		CSVReader userFileReader;
		
		try {
			userFileReader = new CSVReader(new FileReader(path+fileName),delimiter);
		} catch (FileNotFoundException e1) {
			logger.error("Exception ", e1);
			MessageUtil.setMessage("given inputs are incorrect", "color:red;");
			return;
		}
		SuppressedContactsDaoForDML suppressedContactsDao = null;
		try {
			suppressedContactsDao = (SuppressedContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("suppressedContactsDaoForDML");
		} catch (Exception e) {
			MessageUtil.setMessage("No Dao found", "color:red;");
			return;
		}
		StringBuffer emailIdsBuffer = new StringBuffer();
		try {
			while ((nextLine = userFileReader.readNext()) != null) {
				try {
					int lineTokenCount = nextLine.length;
					if(lineTokenCount==1 && nextLine[0]!=null && nextLine[0].trim().equals(Constants.STRING_NILL)) {
						
						logger.debug("invalid line ");
						continue;
						
					}//if
					
					String email = nextLine[0];
					emailIdsCount ++;
					if(emailIdsBuffer.length() > 0) emailIdsBuffer.append("|");
					
					emailIdsBuffer.append(email);
					if(emailIdsCount == 100 && emailIdsBuffer.length() > 0){
						

						try {
							//long time = System.currentTimeMillis();
							int count = suppressedContactsDao.deleteBy(userId, "'"+emailIdsBuffer.toString()+"'");
							//logger.debug("Query executed in ::"+( time-System.currentTimeMillis()));
							
							//if(true) return;
							logger.debug("successfully deleted ::"+count);
							emailIdsCount =0;
							emailIdsBuffer.setLength(0);
						} catch (BaseDAOException e) {
							MessageUtil.setMessage("Exception while deleting the suppressed contacts ", "color:red;");
							logger.error("Exception while deleting the suppressed contacts", e);
							return;
						}
						
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ", e);
				}
				
			}
			//logger.debug("emailIdsBuffer ::"+emailIdsBuffer.toString());
			if(emailIdsBuffer.length() > 0){
				
				try {
					int count = suppressedContactsDao.deleteBy(userId, "'"+emailIdsBuffer.toString()+"'");
					logger.debug("successfully deleted ::"+count);
					emailIdsBuffer.setLength(0);
				}catch (BaseDAOException e) {
					MessageUtil.setMessage("Exception while deleting the suppressed contacts ", "color:red;");
					logger.error("Exception while deleting the suppressed contacts", e);
					return;
				}
			}
			
			MessageUtil.setMessage("succefully deleted from suppressed contacts for user::"+userName, "color:blue;");
			
		} catch (IOException e) {
			MessageUtil.setMessage("IO Exception", "color:red;");
			logger.error("IO Exception ", e);
			return;
		}
		
	}//deleteSuppressedContacts
	private Listbox addLbId, subLbId, chooseLbId;
	private Textbox cardTbId, userTbId, addTbId, subTbId, fileTbId;
	private Div fileDivId, singleDivId;
	private Checkbox seperateChkBxID;
	public boolean checkIfNumber(String in) {
        try {
            Long.parseLong(in);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }// checkIfNumber
	public void processBulkAdjustments(String valueCode, String enteredAmount,boolean isSingle, boolean isFromAdd) {
		try {
			String fileName = fileTbId.getText().trim(); 
			String userName = userTbId.getText().trim();
			char delimiter=',';
			String[] nextLine;
			int emailIdsCount = 0;
			String path = PropertyUtil.getPropertyValue("usersParentDirectory") + "/" + userName + "/List/";
			CSVReader userFileReader;
			
			try {
				logger.info("path+fileName "+( path+fileName));
				userFileReader = new CSVReader(new FileReader(path+fileName),delimiter);
			} catch (FileNotFoundException e1) {
				logger.error("Exception ", e1);
				MessageUtil.setMessage("given inputs are incorrect", "color:red;");
				return;
			}
			int totLines =0;
			int invalidCount = 0;
			String cardNumber = "";
			
			int uploaded = 0;
			StringBuffer invalidSB = new StringBuffer();
			while ((nextLine = userFileReader.readNext()) != null) {
				totLines++;
				int lineTokenCount = nextLine.length;
				if(!seperateChkBxID.isChecked()){
					if(lineTokenCount==1 && nextLine[0]!=null && nextLine[0].trim().equals("")) {
				    	if(logger.isDebugEnabled()) logger.debug("Empty line");
				    	invalidSB.append(totLines + "  \r\n   ");
						invalidCount++;
				    	continue;
					}
					cardNumber = nextLine[0];
					String cardLong = OptCultureUtils.validateOCLtyCardNumber(cardNumber.trim());
					if(cardLong == null){
						if(logger.isDebugEnabled()) logger.debug("Invalid card: " + cardNumber);
						invalidSB.append(totLines + "  " + cardNumber + "\r\n   ");
						invalidCount++;
						continue;
					}
					
					if(!cardNumber.isEmpty() && !userName.isEmpty()) {
						
						processAdjustments(valueCode, enteredAmount, cardNumber, userName, isSingle, isFromAdd);
					}
				}else{
					//added newly for  single card adjustment in a bulk file

					if(lineTokenCount==2 && nextLine[0]!=null && nextLine[0].trim().equals("") && nextLine[1]!=null && nextLine[1].trim().equals("")) {
				    	if(logger.isDebugEnabled()) logger.debug("Empty line");
				    	invalidSB.append(totLines + "  \r\n   ");
						invalidCount++;
				    	continue;
					}
					cardNumber = nextLine[0];
					String cardLong = OptCultureUtils.validateOCLtyCardNumber(cardNumber.trim());
					if(cardLong == null){
						if(logger.isDebugEnabled()) logger.debug("Invalid card: " + cardNumber);
						invalidSB.append(totLines + "  " + cardNumber + "\r\n   ");
						invalidCount++;
						continue;
					}
					if(!isFromAdd){
						enteredAmount = "-" +nextLine[1] ;
					}else{
						enteredAmount = nextLine[1];
						
					}
					if(!cardNumber.isEmpty() && !userName.isEmpty() && !enteredAmount.isEmpty()) {
						
						processAdjustments(valueCode, enteredAmount, cardNumber, userName, isSingle, isFromAdd);
					}
				
					
					
					
				}
				
			}
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
	}
	public void onSelect$chooseLbId() {
		if(chooseLbId.getSelectedItem().getLabel().equals("Bulk")){
			singleDivId.setVisible(false);
			fileDivId.setVisible(true);
			seperateChkBxID.setVisible(true);
			
		}else{
			
			singleDivId.setVisible(true);
			fileDivId.setVisible(false);
			seperateChkBxID.setVisible(false);
		}
			
			
		
	}
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
	
	private LoyaltyTransactionChild createPurchaseTransaction(ContactsLoyalty loyalty, double adjustValue, String earnType, 
			String entAmountType, String activationDate,String earnStatus, String adjType){
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
transaction.setEarnedPoints(adjustValue);
transaction.setPointsDifference(""+(adjType.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD)?adjustValue:-adjustValue));
}
else if(earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
transaction.setEarnedAmount(adjustValue);
transaction.setAmountDifference(""+(adjType.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD)?adjustValue:-adjustValue));
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
//transaction.setEventTriggStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
transaction.setLoyaltyId(loyalty.getLoyaltyId());

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
			
			//LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			totPurchaseValue = LoyaltyProgramHelper.getLPV(contactsLoyalty);//contactsLoyalty.getLifeTimePurchaseValue() == null ? 0.0 : contactsLoyalty.getLifeTimePurchaseValue();//Double.valueOf(loyaltyTransactionChildDao.getLifeTimeLoyaltyPurchaseValue(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId()));
			logger.info("purchase value = "+totPurchaseValue);

			//if(contactPurcahseList == null || totPurchaseValue == null || totPurchaseValue <= 0){
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

					//cumulativeAmount = Double.valueOf(cumulativeAmountArr[0].toString());

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
	
	
	private void applyConversionRules(ContactsLoyalty contactsLoyalty, LoyaltyTransactionChild transaction, LoyaltyProgram program, LoyaltyProgramTier tier){
		logger.debug(">>>>>>>>>>>>> entered in applyConversionRules");
		String[] differenceArr = null;

		try{

			if(tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO)){

				if(tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0 
						&& contactsLoyalty.getLoyaltyBalance() != null && contactsLoyalty.getLoyaltyBalance() > 0 
						&& contactsLoyalty.getLoyaltyBalance() >= tier.getConvertFromPoints()){

					differenceArr = new String[3];

					double multipledouble = contactsLoyalty.getLoyaltyBalance()/tier.getConvertFromPoints();
					int multiple = (int)multipledouble;
					double convertedAmount = tier.getConvertToAmount() * multiple;
					double subPoints = multiple * tier.getConvertFromPoints();

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
						contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() + convertedAmount);
					}
					if(contactsLoyalty.getTotalGiftcardAmount() == null){
						contactsLoyalty.setTotalGiftcardAmount(convertedAmount);
					}
					else{
						contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount() + convertedAmount);
					}

					transaction.setConversionAmt(convertedAmount);
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
				ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
				contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);

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
	
	private Contacts findContactById(Long cid) throws Exception {
		logger.debug(">>>>>>>>>>>>> entered in findContactById");
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		logger.debug("<<<<<<<<<<<<< completed findContactById");
		return contactsDao.findById(cid);
	}//findContactById
	
	private LoyaltyAutoComm getLoyaltyAutoComm(Long programId) throws Exception {
		logger.debug(">>>>>>>>>>>>> entered in getLoyaltyAutoComm");
		LoyaltyAutoCommDao autoCommDao = (LoyaltyAutoCommDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
		logger.debug("<<<<<<<<<<<<< completed getLoyaltyAutoComm ");
		return autoCommDao.findById(programId);
	}//getLoyaltyAutoComm
	private void updateThresholdBonus(ContactsLoyalty contactsLoyalty, LoyaltyProgram program, Double fromLtyBalance, Double fromAmtBalance, LoyaltyProgramTier loyaltyProgramTier) throws Exception {
		logger.debug(">>>>>>>>>>>>> entered in updateThresholdBonus");
		try{
			LoyaltyThresholdBonusDao loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
			List<LoyaltyThresholdBonus> threshBonusList = loyaltyThresholdBonusDao.getBonusListByPrgmId(program.getProgramId(), 'N' );
			List<LoyaltyThresholdBonus> pointsBonusList = new ArrayList<LoyaltyThresholdBonus>();
			List<LoyaltyThresholdBonus> amountBonusList = new ArrayList<LoyaltyThresholdBonus>();
			fromAmtBalance = fromAmtBalance == null ? 0.0 : fromAmtBalance;
			fromLtyBalance = fromLtyBalance == null ? 0.0 : fromLtyBalance;

			//String[] bonusArr = null; //new String[2];
			if(threshBonusList != null && threshBonusList.size()>0){
				for(LoyaltyThresholdBonus bonus : threshBonusList){
					if(bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_POINTS)){
						pointsBonusList.add(bonus);
					}
					else if (bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
						amountBonusList.add(bonus);
					}
				}

				List<LoyaltyThresholdBonus> matchedBonusList = new ArrayList<LoyaltyThresholdBonus>();

				Collections.sort(pointsBonusList, new Comparator<LoyaltyThresholdBonus>(){
					@Override
					public int compare(LoyaltyThresholdBonus ltb1, LoyaltyThresholdBonus ltb2) {
						return ltb1.getEarnedLevelValue().compareTo(ltb2.getEarnedLevelValue());
					}
				});


				Collections.sort(amountBonusList, new Comparator<LoyaltyThresholdBonus>(){
					@Override
					public int compare(LoyaltyThresholdBonus ltb1, LoyaltyThresholdBonus ltb2) {
						return ltb1.getEarnedLevelValue().compareTo(ltb2.getEarnedLevelValue());
					}
				});

				matchedBonusList.addAll(pointsBonusList);
				matchedBonusList.addAll(amountBonusList);

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
				
				if(matchedBonusList != null && matchedBonusList.size() > 0){
					for (LoyaltyThresholdBonus matchedBonus : matchedBonusList) {
						
						bonusflag = false;
						long multiplier = 1;
						
						double afterBalLoyaltyEarned= contactsLoyalty.getTotalLoyaltyEarned() == null ? 0.0 : contactsLoyalty.getTotalLoyaltyEarned();
						double afterBalGiftCardAmt= contactsLoyalty.getTotalGiftcardAmount() == null ? 0.0 : contactsLoyalty.getTotalGiftcardAmount();	
						
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

							logger.info("balances before balance object = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
							createExpiryTransaction(contactsLoyalty, bonusPoints, bonusAmount, contactsLoyalty.getOrgId(), 
									childTxbonus.getTransChildId(),matchedBonus.getThresholdBonusId());
							if(loyaltyProgramTier != null){
								// CALL CONVERSION
								applyConversionRules(contactsLoyalty, childTxbonus, program, loyaltyProgramTier);
								// CALL TIER UPGD
								loyaltyProgramTier = applyTierUpgradeRule(contactsLoyalty, program, childTxbonus, loyaltyProgramTier);
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
						if(OCConstants.LOYALTY_TYPE_POINTS.equals(matchedBonus.getEarnedLevelType())){
							logger.info("*****setting bonus points threshold*********");
							contactsLoyalty.setBonusPointsThreshold(matchedBonus.getEarnedLevelValue());
						}else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(matchedBonus.getEarnedLevelType())){
							logger.info("*****setting bonus currency threshold*********");
							contactsLoyalty.setBonusCurrencyThreshold(matchedBonus.getEarnedLevelValue());

						}
						
						LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
						Contacts contact = null;
						LoyaltyAutoComm autoComm = getLoyaltyAutoComm(program.getProgramId());
						if(bonusflag && autoComm != null && autoComm.getThreshBonusEmailTmpltId() != null && contactsLoyalty.getContact() != null &&
								contactsLoyalty.getContact().getContactId() != null){
							contact = findContactById(contactsLoyalty.getContact().getContactId());
							if(contact != null && contact.getEmailId() != null){
								autoCommGen.sendEarnBonusTemplate(autoComm.getThreshBonusEmailTmpltId(), ""+contactsLoyalty.getCardNumber(),
										contactsLoyalty.getCardPin(), contact.getUsers(), contact.getEmailId(), contact.getFirstName(),
										contact.getContactId(), contactsLoyalty.getLoyaltyId());
							}
						}
						UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
						Users user = userDao.findByUserId(contactsLoyalty.getUserId());
						if(user.isEnableSMS() && bonusflag && autoComm != null && autoComm.getThreshBonusSmsTmpltId() != null) { 
							Long contactId = null;	
							if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
								contactId = contactsLoyalty.getContact().getContactId();
							}
							autoCommGen.sendEarnBonusSMSTemplate(autoComm.getThreshBonusSmsTmpltId(), user, contactId,
									contactsLoyalty.getLoyaltyId(), null);
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
	}
	private LoyaltyProgramTier getLoyaltyTier(Long tierId) throws Exception{
		
		LoyaltyProgramTierDao tierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		return tierDao.getTierById(tierId);
		
	}
	
	public void processOCAdjustments(String valueCode, String enteredAmount, String cardNumber, 
			String userName, boolean isSingle , boolean isFromAdd, Users user){
		valueCode = "USD".equalsIgnoreCase(valueCode) ? "Amount":"Points";
		double temp=0.0;
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)SpringUtil.getBean("contactsLoyaltyDao");
		ContactsLoyalty contactsLoyaltyObj = contactsLoyaltyDao.getContactsLoyaltyByCardId(cardNumber, user.getUserId());
		
		if(enteredAmount.isEmpty()){
			
			if(isSingle)MessageUtil.setMessage("Please enter an amount to add.", "red");
			return;
		}
		if(isFromAdd && enteredAmount.startsWith("-")) {
			if(isSingle)MessageUtil.setMessage("Please enter valid amount.", "red");
		}else if(isFromAdd){
			LoyaltyProgram loyaltyProgram = null;
			
			if(contactsLoyaltyObj != null) {
				LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();

				Long programId = contactsLoyaltyObj.getProgramId();
				loyaltyProgram = ltyPrgmService.getProgmObj(programId);
				
				String displayLabel = "Amount".equalsIgnoreCase(valueCode) ? "rewards":"points";
				String[] amount = new String[2];
				amount[0] = valueCode;
				amount[1] = enteredAmount;
				double balanceToAdd=0.0;
				/*if(loyaltyProgram == null &&  transactionChild == null && loyaltyProgramTier == null){
					MessageUtil.setMessage("Not a loyalty card.", "red");
					return;
				}*/

				if(loyaltyProgram.getStatus() != null && OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equalsIgnoreCase(loyaltyProgram.getStatus()) && OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE.equalsIgnoreCase(contactsLoyaltyObj.getMembershipStatus()) ){

					if(enteredAmount.isEmpty()){
						if(isSingle)
							{
						MessageUtil.setMessage("Please enter "+displayLabel+" to add.", "red");
						addTbId.setText("");
							}
						return;
					}//if

					if(amount[1].startsWith("-") || amount[1].startsWith("+")) {
						if(isSingle)
						{
						MessageUtil.setMessage("Please enter valid  "+displayLabel+".", "red");
						addTbId.setText("");
						}
						return;
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
							balanceToAdd = enteredAmount != null ? Double.parseDouble(enteredAmount) : 0;
							if(valueCode.equals("Amount")) {
								if(balanceToAdd==0){
									if(isSingle){
									MessageUtil.setMessage("Rewards to be added should be greater than zero.", "color:red;");
									addTbId.setFocus(true);
									addTbId.setText("");
									}
									return;
								}
							}
							else {
								if(balanceToAdd==0){
									if(isSingle){
									MessageUtil.setMessage("Points to be added should be greater than zero.", "color:red;");
									addTbId.setText("");
									addTbId.setFocus(true);
									}
									return;
								}
							}


								// GET CONTACTLOYATLY OBJECT

								// CHECK IF THE CARD REWARD FLAG OF TYPE L/GL, THEN ALLOW ADJUSTMENT. FOR G TYPE DO NOT ALLOW ADJUSTMENTS.
								if(!OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyaltyObj.getRewardFlag())){
									boolean flag = false;
									Double fromLtyBalance = contactsLoyaltyObj.getTotalLoyaltyEarned();
									Double fromAmtBalance = contactsLoyaltyObj.getTotalGiftcardAmount();
									// UPDATE BALANCE
									try {
										
											if(valueCode.equalsIgnoreCase("Amount")) {
												//update totalGiftcardAmount,giftcardBalance
												
												
												Double totalGiftCardAmount = contactsLoyaltyObj.getTotalGiftcardAmount()== null ? 0.0 : contactsLoyaltyObj.getTotalGiftcardAmount();
												//totalGiftCardAmount = totalGiftCardAmount + balanceToAdd ;
												totalGiftCardAmount = new BigDecimal(totalGiftCardAmount + balanceToAdd).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
												Double giftCardBalance =  contactsLoyaltyObj.getGiftcardBalance() == null ? 0.0 : contactsLoyaltyObj.getGiftcardBalance();
												//giftCardBalance = giftCardBalance + balanceToAdd ;
												giftCardBalance = new BigDecimal(giftCardBalance + balanceToAdd).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
												contactsLoyaltyObj.setTotalGiftcardAmount(totalGiftCardAmount);
												contactsLoyaltyObj.setGiftcardBalance(giftCardBalance);
																																		
												flag  =  ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
												
												
											}
											else{ //POINTS
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
												/*Double loyaltyBalance =  contactsLoyaltyObj.getLoyaltyBalance() == null ? 0.0 : contactsLoyaltyObj.getLoyaltyBalance();
												Double totalLoyaltyEarned = contactsLoyaltyObj.getTotalLoyaltyEarned() == null ? 0.0 : contactsLoyaltyObj.getTotalLoyaltyEarned();
												logger.info("Previous LoyaltyBalance was ::::::::::::"+loyaltyBalance);
												loyaltyBalance = loyaltyBalance + balanceToAdd;
												totalLoyaltyEarned = totalLoyaltyEarned + balanceToAdd;
												contactsLoyaltyObj.setLoyaltyBalance(loyaltyBalance);
												contactsLoyaltyObj.setTotalLoyaltyEarned(totalLoyaltyEarned);
												flag = ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
												logger.info("New LoyaltyBalance is ::::::::::::"+loyaltyBalance);*/
											}
											if(flag){	
												// CREATE TRANSACTION
												LoyaltyTransactionChild transactionChild = createPurchaseTransaction(contactsLoyaltyObj, balanceToAdd, valueCode, Constants.STRING_NILL, null, null,OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD);
												// CREATE EXPIRY TRANSACTION
												Long adjustPoints = 0l;
												Double adjustAmt = 0.0;
												if(valueCode.equals(OCConstants.LOYALTY_TYPE_POINTS)){
													adjustPoints = (long) balanceToAdd;
												}
												else if(valueCode.equals("Amount")){
													adjustAmt = balanceToAdd;
												}
											
												createExpiryTransaction(contactsLoyaltyObj, adjustPoints, adjustAmt, contactsLoyaltyObj.getOrgId(), 
														transactionChild.getTransChildId(),null);
												
												LoyaltyProgramTier loyaltyProgramTier = null;
												if(contactsLoyaltyObj.getProgramTierId() == null) {
													loyaltyProgramTier = findTier(contactsLoyaltyObj);
													if (loyaltyProgramTier == null) {
														// CALL BONUS
														updateThresholdBonus(contactsLoyaltyObj, loyaltyProgram, fromLtyBalance, fromAmtBalance, null);
														ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
														//updateLoyaltyData(contactsLoyaltyObj);
														if(isSingle){
															addTbId.setValue(Constants.STRING_NILL);
														addLbId.setSelectedIndex(0);
														MessageUtil.setMessage(StringUtils.capitalize(displayLabel)+" added successfully.", "blue");
														}
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
												applyConversionRules(contactsLoyaltyObj, transactionChild, loyaltyProgram, loyaltyProgramTier);
												// CALL TIER UPGD
												loyaltyProgramTier = applyTierUpgradeRule(contactsLoyaltyObj, loyaltyProgram, transactionChild, loyaltyProgramTier);
												
												// CALL BONUS
												updateThresholdBonus(contactsLoyaltyObj, loyaltyProgram, fromLtyBalance, fromAmtBalance, loyaltyProgramTier);

												ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
												
												//updateLoyaltyData(contactsLoyaltyObj);
												if(isSingle){
												addTbId.setValue(Constants.STRING_NILL);
												
												addLbId.setSelectedIndex(0);
												MessageUtil.setMessage(StringUtils.capitalize(displayLabel)+" added successfully.", "blue");
												}
											}
											else{
												if(isSingle){
												MessageUtil.setMessage("Transaction was not successful.", "red");
												addTbId.setText("");
												}
												return;
											}

									}
									catch (Exception e) {
										logger.error("Exception ,Transaction was not successful ::",e);
										addTbId.setText("");
									}

								}//status if
								else{
									if(isSingle){
									MessageUtil.setMessage("Transaction was not successful.", "red");
									logger.error("Unable to perform adjustments because Reward Flag is G");
									addTbId.setText("");
									}
									return;
								}
						}
						else {
							if(isSingle){
							MessageUtil.setMessage("Please enter valid "+displayLabel+".", "red");
							}
							addTbId.setText("");
						}
					}

				}// if status 
				else {
					if(isSingle){
					Messagebox.show("Please select a contact with active program & membership status.", "Error" ,Messagebox.OK, 
							Messagebox.ERROR);
					addTbId.setText("");
					}
				}
			}
			else{
				if(isSingle){
				addTbId.setText("");
				return;
			}
			}
			
		}else{
			

			
			try {

				LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
				LoyaltyThresholdAlerts loyaltyThresholdAlerts = ltyPrgmSevice.findPwdByUserID(GetUser.getUserObj().getUserId());
				LoyaltyProgram loyaltyProgram = null;

				if(contactsLoyaltyObj != null) {

					LoyaltyProgramService ltyPrgmService = new LoyaltyProgramService();
					String membershipNumber = contactsLoyaltyObj.getCardNumber();
					Long programId = contactsLoyaltyObj.getProgramId();
					loyaltyProgram = ltyPrgmService.getProgmObj(programId);
					enteredAmount = enteredAmount.replaceAll("-", "");
					String displayLabel = "Amount".equalsIgnoreCase(valueCode) ? "rewards":"points";
					String[] amount = new String[2];
					amount[0] = valueCode;
					amount[1] = enteredAmount;
					double balanceToSub=0.0;
					/*if(loyaltyProgram == null &&  transactionChild == null && loyaltyProgramTier == null){
						MessageUtil.setMessage("Not a loyalty card.", "red");
						return;
					}*/


					if(loyaltyProgram.getStatus() != null && OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equalsIgnoreCase(loyaltyProgram.getStatus()) && OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE.equalsIgnoreCase(contactsLoyaltyObj.getMembershipStatus()) ){


						if((enteredAmount.trim().isEmpty())){
							if(isSingle){
							MessageUtil.setMessage("Please enter  "+displayLabel+" to subtract.", "red");
							subTbId.setText("");
							}
							return;
						}//if

						if(amount[1].startsWith("-") || amount[1].startsWith("+")) {
							if(isSingle){
							MessageUtil.setMessage("Please enter valid  "+displayLabel+".", "red");
							subTbId.setText("");
							}
							return;
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
								balanceToSub = enteredAmount != null ? Double.parseDouble(enteredAmount) : 0;
								//	balanceToSub =balanceToSub*-1;

								if(valueCode.equals("Amount")) {
									if(balanceToSub==0){
										if(isSingle){
										MessageUtil.setMessage("Rewards to be subtracted should be greater than Zero.", "color:red;");
										subTbId.setText("");
										}
										return;
									}
									
								
										double existingGiftcardBalance = contactsLoyaltyObj.getGiftcardBalance() == null ? 0.0 : contactsLoyaltyObj.getGiftcardBalance();
										logger.info("Temp ::"+balanceToSub+"Existing loyalty balance"+existingGiftcardBalance);
										
										if(balanceToSub > existingGiftcardBalance){
											if(isSingle){
											//balance to be is subtracted is greater than existing balance condition is true & display popup & return
											MessageUtil.setMessage("Rewards to be subtracted should be less or equal to existing currency balance.", "color:red;");
											//	subTbId.setValue(Constants.STRING_NILL);
											subTbId.setText("");
											subTbId.setFocus(true);
											}
											return;
										}
									
								} //if Amount
								else {
									if(balanceToSub==0){
										if(isSingle){
										MessageUtil.setMessage("Points to be subtracted should be greater than Zero.", "color:red;");
										subTbId.setText("");
										}
										return;
									}
									
										double existingLoyaltyBalance = contactsLoyaltyObj.getLoyaltyBalance() == null ? 0.0 : contactsLoyaltyObj.getLoyaltyBalance();

										logger.info("Temp ::"+balanceToSub+"Existing loyalty balance"+existingLoyaltyBalance);
										if(balanceToSub > existingLoyaltyBalance){
											if(isSingle){
											MessageUtil.setMessage("Points to be subtracted should be less or equal to existing point balance.", "color:red;");
											subTbId.setText("");
											subTbId.setFocus(true);
											}
											return;
										}	
								}//else points


									// GET CONTACTLOYATLY OBJECT

									// CHECK IF THE CARD REWARD FLAG OF TYPE L/GL, THEN ALLOW ADJUSTMENT. FOR G TYPE DO NOT ALLOW ADJUSTMENTS.
									if(!OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyaltyObj.getRewardFlag())){

										// UPDATE BALANCE
										try {
												if(valueCode.equalsIgnoreCase("Amount")) {
													//update totalGiftcardAmount,giftcardBalance
													//	balanceToSub =balanceToSub*-1;
													Double totalGiftCardAmount = contactsLoyaltyObj.getTotalGiftcardAmount()== null ? 0.0 : contactsLoyaltyObj.getTotalGiftcardAmount();
													totalGiftCardAmount = new BigDecimal(totalGiftCardAmount -balanceToSub).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
													Double giftCardBalance =  contactsLoyaltyObj.getGiftcardBalance() == null ? 0.0 : contactsLoyaltyObj.getGiftcardBalance();
													//giftCardBalance = giftCardBalance - balanceToSub ;
													giftCardBalance = new BigDecimal(giftCardBalance - balanceToSub).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
													logger.info("giftCardBalance "+giftCardBalance+" - "+"balanceToSub "+balanceToSub);
													contactsLoyaltyObj.setGiftcardBalance(giftCardBalance);
													contactsLoyaltyObj.setTotalGiftcardAmount(totalGiftCardAmount);
													ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
													/*Double giftCardBalance =  contactsLoyaltyObj.getGiftcardBalance() == null ? 0.0 : contactsLoyaltyObj.getGiftcardBalance();
													giftCardBalance = giftCardBalance - balanceToSub ;
													logger.info("giftCardBalance "+giftCardBalance+" - "+"balanceToSub "+balanceToSub);
													contactsLoyaltyObj.setGiftcardBalance(giftCardBalance);
													ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);*/
												}
												else{ //POINTS
													//loyaltyBalance,totalLoyaltyEarned
													//	balanceToSub =balanceToSub*-1;
													Double loyaltyBalance =  contactsLoyaltyObj.getLoyaltyBalance() == null ? 0.0 : contactsLoyaltyObj.getLoyaltyBalance();
													Double totalLoyaltyEarned = contactsLoyaltyObj.getTotalLoyaltyEarned() == null ? 0.0 : contactsLoyaltyObj.getTotalLoyaltyEarned();
													totalLoyaltyEarned = totalLoyaltyEarned - balanceToSub;
													
													logger.info("Previous LoyaltyBalance was ::::::::::::"+loyaltyBalance);
													loyaltyBalance = loyaltyBalance - balanceToSub;
													logger.info("loyaltyBalance "+loyaltyBalance+" - "+"balanceToSub "+balanceToSub);
													contactsLoyaltyObj.setLoyaltyBalance(loyaltyBalance);
													contactsLoyaltyObj.setTotalLoyaltyEarned(totalLoyaltyEarned);
													ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
													logger.info("New LoyaltyBalance is ::::::::::::"+loyaltyBalance);
													/*Double loyaltyBalance =  contactsLoyaltyObj.getLoyaltyBalance() == null ? 0.0 : contactsLoyaltyObj.getLoyaltyBalance();
													logger.info("Previous LoyaltyBalance was ::::::::::::"+loyaltyBalance);
													loyaltyBalance = loyaltyBalance - balanceToSub;
													logger.info("loyaltyBalance "+loyaltyBalance+" - "+"balanceToSub "+balanceToSub);
													contactsLoyaltyObj.setLoyaltyBalance(loyaltyBalance);
													ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
													logger.info("New LoyaltyBalance is ::::::::::::"+loyaltyBalance);*/
												}


												// CREATE TRANSACTION
												createPurchaseTransaction(contactsLoyaltyObj, balanceToSub, valueCode, Constants.STRING_NILL, null, null, OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_SUB);
												
												if(valueCode.equals(OCConstants.LOYALTY_TYPE_POINTS)){
													deductPointsFromExpiryTable(contactsLoyaltyObj, (long) balanceToSub);
												}
												else if(valueCode.equals("Amount")){
													deductLoyaltyAmtFromExpiryTable(contactsLoyaltyObj, balanceToSub);
												}
											} catch (Exception e) {
												logger.error("Exception ::",e);
											}
											//updateLoyaltyData(contactsLoyaltyObj);
											if(isSingle){
											subTbId.setValue(Constants.STRING_NILL);
											subLbId.setSelectedIndex(0);
											MessageUtil.setMessage( StringUtils.capitalize(displayLabel) +" subtracted successfully.", "blue");
											}
										}
										else{
											if(isSingle){
											MessageUtil.setMessage("Transaction was not successful.", "red");
											logger.error("Unable to perform adjustments because Reward Flag is G");
											subTbId.setText("");
											}
											return;
										}
								}//
								else {
									if(isSingle){
									MessageUtil.setMessage("Please enter valid  "+displayLabel+".", "red");
									subTbId.setText("");
									}
								}
							}

						} //Active
						else {
							if(isSingle){
							Messagebox.show("Please select a contact with active program & membership status.", "Error" ,Messagebox.OK, 
									Messagebox.ERROR);
							subTbId.setText("");
							}
						}
					}// loyaltyObj is null
					else{
						if(isSingle){
						Messagebox.show("Please look-up membership before performing this action.", "Error" ,Messagebox.OK, 
								Messagebox.ERROR);
						subTbId.setText("");
						}
						return;
					}
				
			} catch (WrongValueException e) {
				logger.error("Exception ::",e);
			} catch (NumberFormatException e) {
				logger.error("Exception ::",e);
			}catch(Exception e){
				logger.error("Exception ",e);
			}
			
			logger.debug("<<<<<<<<<<<<< completed onClick$subtractBtnId ");
			
		}
		
		
	}

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
	public void processAdjustments(String valueCode, String enteredAmount, String cardNumber, 
			String userName, boolean isSingle , boolean isFromAdd ){
		
		
		Users user = usersDao.findByUsername(userName);
		if(!user.getloyaltyServicetype().equalsIgnoreCase(OCConstants.LOYALTY_SERVICE_TYPE_SB)){
			processOCAdjustments( valueCode,  enteredAmount, cardNumber, 
					 userName,  isSingle , isFromAdd, user);
			return;
		}
		
		
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)SpringUtil.getBean("contactsLoyaltyDao");
		ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML)SpringUtil.getBean("contactsLoyaltyDaoForDML");
		SparkBaseLocationDetailsDao sparkBaseLocationDetailsDao = (SparkBaseLocationDetailsDao)SpringUtil.getBean("sparkBaseLocationDetailsDao");
		SparkBaseCardDao sparkBaseCardDao = (SparkBaseCardDao)SpringUtil.getBean("sparkBaseCardDao");
		
		 ContactsLoyalty contactLoyalty = contactsLoyaltyDao.getContactsLoyaltyByCardId(cardNumber, user.getUserId());
		 
		 String[] amount = new String[2];
		amount[0] = valueCode;
		amount[1] = enteredAmount;
		double temp=0.0;
		if(enteredAmount.isEmpty()){
			
			if(isSingle)MessageUtil.setMessage("Please enter an amount to add.", "red");
			return;
		}
		if(isFromAdd && amount[1].startsWith("-")) {
			if(isSingle)MessageUtil.setMessage("Please enter valid amount.", "red");
		}
		else {
			if(checkIfNumber(amount[1])) {
				temp = enteredAmount != null ? Double.parseDouble(enteredAmount) : 0;
				if(!isFromAdd) temp =temp*-1;
				if(valueCode.equals("USD")) {
					if(temp==0){
						if(isSingle)MessageUtil.setMessage("Rewards to be added should be greater than Zero.", "color:red;");
						return;
					}
					
					if(!isFromAdd){
						
						double existingGiftcardBalance = contactLoyalty.getGiftcardBalance() == null ? 0.0 : contactLoyalty.getGiftcardBalance();
						logger.debug("Temp ::"+temp+"Existing loyalty balance"+existingGiftcardBalance);
						
						if(temp > existingGiftcardBalance){
							if(isSingle) {
								MessageUtil.setMessage("Rewards to be subtracted should be less or equal to existing reward balance.", "color:red;");
								//	subTbId.setValue("");
								subTbId.setFocus(true);
							}
							return;
						}
					}
				}
				else {
					if(temp==0){
						if(isSingle)MessageUtil.setMessage("Points to be added should be greater than Zero.", "color:red;");
						return;
					}
					if(!isFromAdd) {
						
						double existingLoyaltyBalance = contactLoyalty.getLoyaltyBalance() == null ? 0.0 : contactLoyalty.getLoyaltyBalance();
						
						logger.debug("Temp ::"+temp+"Existing loyalty balance"+existingLoyaltyBalance);
						if(temp > existingLoyaltyBalance){
							logger.debug("true");
							if(isSingle) {
								MessageUtil.setMessage("Points to be subtracted should be less or equal to existing point balance.", "color:red;");
								//subTbId.setValue("");
								subTbId.setFocus(true);
							}
							return;
						}
						
					}
				}
			
				
				List<SparkBaseLocationDetails> sbDetailsList = sparkBaseLocationDetailsDao.findAllByOrganizationId(user.getUserOrganization().getUserOrgId());
				
				if(sbDetailsList == null){
					return;
				}
				
				SparkBaseLocationDetails sparkBaseLoc = sbDetailsList.get(0);
				
				Object responseObject = SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.ADJUSTMENT, sparkBaseLoc, contactLoyalty, null, amount, true);
				
				if(responseObject instanceof AdjustmentResponse){
					contactsLoyaltyDaoForDML.saveOrUpdate(contactLoyalty);
					if(isSingle) {
						if(valueCode.equals("USD")) {
							Messagebox.show("Rewards adjusted successfully.", "Info", Messagebox.OK,Messagebox.INFORMATION);
						}
						else {
							Messagebox.show("Points adjusted successfully.", "Info", Messagebox.OK,Messagebox.INFORMATION);
						}
						addTbId.setValue("");
					}
					//updateLoyaltyData();
				}else if (responseObject instanceof ErrorMessageComponent){
					ErrorMessageComponent errorobj = (ErrorMessageComponent)responseObject;
					String briefMsg = errorobj.getBriefMessage();
					String depthMsg = errorobj.getInDepthMessage();
					//MessageUtil.setMessage("Server error occured, transaction failed.", "color:red;");
					if(isSingle) {
						MessageUtil.setMessage(briefMsg+"("+depthMsg+")", "color:red;");
						subTbId.setValue("");
					}
					logger.debug("error msg is :: "+briefMsg+"("+depthMsg+")");
					return;
				}
				else {
					if(isSingle) {
						
						MessageUtil.setMessage("Server error occured, transaction failed.", "color:red;");
						addTbId.setValue("");
					}
					return;
				}
				//}//
			}
			else {
				if(isSingle) MessageUtil.setMessage("Please enter valid amount.", "red");
			}
		
		}
	}
	
	public void onCheck$seperateChkBxID() {
		addTbId.setVisible(!seperateChkBxID.isChecked());
		subTbId.setVisible(!seperateChkBxID.isChecked());
		
	}
	
	private Longbox startUserIDTxtID, endUserIDTxtID, userID, memberID, initialIndexID;
	public void onClick$updateMemPwd(){
		try {
			Long userId = userID.getValue();
			if(userId ==null){
				MessageUtil.setMessage("enter User ID as in DB.", "red");
				return;
			}
			Long loyaltyID = memberID.getValue();
			if(loyaltyID ==null){
				MessageUtil.setMessage("enter Member ID as in DB.", "red");
				return;
			}
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
			ContactsLoyalty contactsLoyalty = contactsLoyaltyDao.findAllByLoyaltyId(loyaltyID);
			if(contactsLoyalty == null){
				MessageUtil.setMessage("No member found enter valid Member ID as in DB.", "red");
				return;
			}
			if(contactsLoyalty.getMembershipPwdBackup() == null){
				
				contactsLoyaltyDaoForDML.executeUpdate(" UPDATE ContactsLoyalty set "
						+ "membershipPwdBackup=membershipPwd WHERE loyaltyId="+loyaltyID+" AND membershipPwdBackup IS NULL");
				
				
			}
			
			String oldPwd = EncryptDecryptLtyMembshpPwd.decryptProgramPwd(contactsLoyalty.getMembershipPwd());
			String newPwd = EncryptDecryptLtyMembshpPwd.encrypt(oldPwd);
			
			contactsLoyaltyDaoForDML.executeUpdate(" UPDATE ContactsLoyalty set "
					+ "membershipPwd='"+newPwd+"' WHERE loyaltyId="+loyaltyID+" AND membershipPwdBackup IS NOT NULL");
			MessageUtil.setMessage("Sucess.", "blue");
			return;
			
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
			MessageUtil.setMessage("Exception.", "red");
			return;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
			MessageUtil.setMessage("Exception.", "red");
			return;
		}
		
	}
	public void onClick$bulkUpdateMemPwd(){
		
		try {
			Long startID = startUserIDTxtID.getValue();
			Long endId = endUserIDTxtID.getValue();
			
			logger.debug("startID==="+startID+" endID=="+endId);
			
			
			LoyaltySettingsDao loyaltySettingsDao = (LoyaltySettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_SETTINGS_DAO);
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
			String qry = "FROM LoyaltySettings WHERE urlStr IS NOT NULL AND userId IS NOT NULL "
					+ "and isActive='A'"+(startID != null && endId !=null ? 
					" AND userId BETWEEN "+startID+" AND "+endId : "") 
					+" group by userId";
			
			 List<LoyaltySettings> retList = loyaltySettingsDao.executeQuery(qry);
			//List<LoyaltySettings> retList = loyaltySettingsDao.findAllActive(startID, endId); 
			if(retList == null || retList.isEmpty()){
				MessageUtil.setMessage("No users to run.", "blue");
				return;
			}
			if(Messagebox.show("Are you sure you want to Updgrade the Password? ", "Confirm" ,Messagebox.OK | Messagebox.CANCEL, 
					Messagebox.QUESTION) == Messagebox.OK) {
				
				for (LoyaltySettings loyaltySettings : retList) {
					
					if(Messagebox.show("Are you sure you want to Updgrade the Password for the next user? ", "Confirm" ,Messagebox.OK | Messagebox.CANCEL, 
							Messagebox.QUESTION) == Messagebox.OK) {
					try {
						Long userID = loyaltySettings.getUserId();
						
						if(userID == null){
							MessageUtil.setMessage("Invalid Settings for url= "+loyaltySettings.getUrlStr(), "red");
							continue;
						}
						
						List<Long> countLst = contactsLoyaltyDao.executeQuery("SELECT COUNT(loyaltyId) FROM ContactsLoyalty WHERE userId="+userID+
								" AND membershipPwd IS NOT NULL AND membershipPwdBackup IS NULL");
						
						if(countLst == null || countLst.isEmpty() || countLst.get(0) <=0 ) {
							MessageUtil.setMessage("No members to update for user="+userID.longValue(), "red");
							continue;
						}
						
						contactsLoyaltyDaoForDML.executeUpdate(" UPDATE ContactsLoyalty set "
								+ "membershipPwdBackup=membershipPwd WHERE userId="+userID+" AND membershipPwd IS NOT NULL AND membershipPwdBackup IS NULL");
						
						long count = countLst.get(0);
						
						MessageUtil.setMessage("running for the user "+ loyaltySettings.getUserId() +" count: "+count, "blue");
						
						int threshold=5000;
						long initialIndex=initialIndexID.getValue() == null ? 0 :initialIndexID.getValue();	
						int recordSize=0;
						int loopCount =0;
						do	{		
							recordSize=1;
							String query1 = "SELECT cl FROM ContactsLoyalty cl, Contacts c WHERE c.users ="+userID+" AND cl.userId="+userID+
								" AND cl.contact=c.contactId AND cl.membershipPwd IS NOT NULL ";
							List<ContactsLoyalty> resultSet = contactsLoyaltyDao.executeQuery(query1, (int)initialIndex, threshold);
							if(resultSet != null && !resultSet.isEmpty()){

								recordSize=resultSet.size();
							
								List<ContactsLoyalty> conLtyToUpdate = new ArrayList<ContactsLoyalty>();
								for (ContactsLoyalty contactsLoyalty : resultSet) {
									try {
										if(contactsLoyalty.getMembershipPwdBackup() == null)contactsLoyalty.setMembershipPwdBackup(contactsLoyalty.getMembershipPwd());
										String oldPassword = EncryptDecryptLtyMembshpPwd.decryptProgramPwd(contactsLoyalty.getMembershipPwd());
										String newPwd = EncryptDecryptLtyMembshpPwd.encrypt(oldPassword);
										contactsLoyalty.setMembershipPwd(newPwd);
										conLtyToUpdate.add(contactsLoyalty);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										logger.error("Exception ", e);
										
									}
								}
								if(conLtyToUpdate.size() > 0) contactsLoyaltyDaoForDML.saveByCollection(conLtyToUpdate);
							}
							/*jdbcResultsetHandler.executeStmt(query1,"fromExportThread");
							ResultSet rs=jdbcResultsetHandler.getResultSet();				
							csvWriter.writeAll(rs, false);
							bw.flush();
							csvWriter.flush();
							recordSize=jdbcResultsetHandler.totalRecordsSize();*/
							loopCount++;
							logger.info("completed writing chunk: "+loopCount);
							initialIndex = loopCount*threshold+1;
							/*if(Messagebox.show("Are you sure you want to Updgrade the next chunk? ", "Confirm" ,Messagebox.OK | Messagebox.CANCEL, 
									Messagebox.QUESTION) != Messagebox.OK) {
								break;
							}*/
						}
						while(recordSize >= threshold);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ", e);
						MessageUtil.setMessage("Exception ", "red");
					}				
				
					}
					
					MessageUtil.setMessage("success for user ="+loyaltySettings.getUserId(), "blue");
					
					
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
			MessageUtil.setMessage("Exception.", "red");
			return;
		}
			
		
		
		MessageUtil.setMessage("success ", "blue");
		
	}
	
	public void onClick$addBtnId() {
		if(Messagebox.show("Are you sure you want to add loyalty points/rewards? ", "Confirm" ,Messagebox.OK | Messagebox.CANCEL, 
				Messagebox.QUESTION) == Messagebox.OK) {
			
			if(seperateChkBxID.isChecked()){
				
				String valueCode = addLbId.getSelectedItem().getValue();
				if(fileDivId.isVisible()){
					processBulkAdjustments(valueCode, null, false, true);
					
				}
				
			}else{
				
				String valueCode = addLbId.getSelectedItem().getValue();
				String enteredAmount = addTbId.getValue().trim();
				if(fileDivId.isVisible()){
					processBulkAdjustments(valueCode, enteredAmount, false, true);
					
				}else{
				
					String cardNumber = cardTbId.getText().trim(); 
					String userName = userTbId.getText().trim();
					if(!cardNumber.isEmpty() && !userName.isEmpty()) {
						
						processAdjustments(valueCode, enteredAmount, cardNumber, userName, true, true);
					}
				}//end single
			
				
			}
				
		}
		
		
	}// onClick$addBtnId
	
public void onClick$subtractBtnId() {
	if(Messagebox.show("Are you sure you want to subtract loyalty points/rewards? ", "Confirm" ,Messagebox.OK | Messagebox.CANCEL, 
			Messagebox.QUESTION) == Messagebox.OK) {
		
		if(seperateChkBxID.isChecked()){
			String valueCode = subLbId.getSelectedItem().getValue();
			if(fileDivId.isVisible()){
				
				processBulkAdjustments(valueCode, null, false, false);
				
			}
		}else{
			String valueCode = subLbId.getSelectedItem().getValue();
			String enteredAmount = "-" + subTbId.getValue().trim();
			if(fileDivId.isVisible()){
				
				processBulkAdjustments(valueCode, enteredAmount, false, false);
				
			}else{
			
				String cardNumber = cardTbId.getText().trim(); 
				String userName = userTbId.getText().trim();
				if(!cardNumber.isEmpty() && !userName.isEmpty()) {
					
					processAdjustments(valueCode, enteredAmount, cardNumber, userName, true, false);
				}
			}//end single
			
		}
		
		
	}
	
		
}// onClick$subtractBtnId

}
