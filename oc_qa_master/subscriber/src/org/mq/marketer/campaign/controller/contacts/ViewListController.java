package org.mq.marketer.campaign.controller.contacts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MLCustomFieldsDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MailingListDaoForDML;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.LBFilterEventListener;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.RightsEnum;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.ContactsJdbcResultsetHandler;
import org.mq.optculture.exception.BaseDAOException;
import org.mq.optculture.utils.OCCSVWriter;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.dao.DataIntegrityViolationException;
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
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

//@SuppressWarnings({ "unchecked", "serial" })
public class ViewListController extends GenericForwardComposer implements Observer  {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
//	private Listbox  contactsListLBId = null;
	private Popup copyListPopupId;
	private Textbox copyListNameTbId;
	
	private Hashtable<String, Div> divHt = new Hashtable<String, Div>();
	private Vector<List> completedVec = new Vector<List>();
	
	private PurgeList purgeList = null;
	private UserActivitiesDao userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
	private UserActivitiesDaoForDML userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
	
	private String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
	private POSMappingDao posMappingDao;
	Long currentUserId = GetUser.getUserId();
	
	//Set<Long> userIdsSet = GetUser.getUsersSet();
	
	private Paging  mlistPaging,mlistPaging1;
	private Div warnDivId;
	private Session session;
	private Listbox contactsListLBId,membersPerPageListboxId;
	private Textbox listNameTbId,descTbId;
	private MailingList mailingList;
//	private Window viewContactsListWinId;
//	private Button buttonId;
	private UsersDao usersDao;
	private Grid custFieldsGbId;
	private int configuredCfMaxFieldIndex=0;
	
	private MailingListDao mailingListDao;
	private MailingListDaoForDML mailingListDaoForDML;
	private Bandbox listActionsBandBoxId;
	
	private Set<Long> listIdsSet; 
	private Window custExport;
	private Div custExport$chkDivId;
	private Checkbox checkBoxId;
	private Textbox textBoxId1,textBoxId2;
	private Intbox intBoxId;
	private Datebox dateBoxId;
	 private static Map<String, String> genFieldContMap = new LinkedHashMap<String, String>();
		
		static{
			
			genFieldContMap.put("Email", "c.email_id");
			genFieldContMap.put("Mobile", "c.mobile_phone");
			genFieldContMap.put("First Name", "c.first_name");
			genFieldContMap.put("Last Name", "c.last_name");
			genFieldContMap.put("Street", "c.address_one");
			genFieldContMap.put("Address Two", "c.address_two");
			genFieldContMap.put("City", "c.city");
			genFieldContMap.put("State", "c.state");
			genFieldContMap.put("Country", "c.country");
			genFieldContMap.put("ZIP", "c.zip");
			genFieldContMap.put("Customer ID", "c.external_id" );
			genFieldContMap.put("Gender", "c.gender");
			genFieldContMap.put("BirthDay", "c.birth_day");
			genFieldContMap.put("Anniversary", "c.anniversary_day");
		//	genFieldContMap.put("Home Store", "c.home_store");
			genFieldContMap.put("Home Store", "org.store_name");
			genFieldContMap.put("UDF1", "c.udf1");
			genFieldContMap.put("UDF2", "c.udf2");
			genFieldContMap.put("UDF3", "c.udf3");
			genFieldContMap.put("UDF4", "c.udf4");
			genFieldContMap.put("UDF5", "c.udf5");
			genFieldContMap.put("UDF6", "c.udf6");
			genFieldContMap.put("UDF7", "c.udf7");
			genFieldContMap.put("UDF8", "c.udf8");
			genFieldContMap.put("UDF9", "c.udf9");
			genFieldContMap.put("UDF10", "c.udf10");
			genFieldContMap.put("UDF11", "c.udf11");
			genFieldContMap.put("UDF12", "c.udf12");
			genFieldContMap.put("UDF13", "c.udf13");
			genFieldContMap.put("UDF14", "c.udf14");
			genFieldContMap.put("UDF15", "c.udf15");
		}
	public ViewListController() {
		
		
		session = Sessions.getCurrent();
		 mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		 mailingListDaoForDML = (MailingListDaoForDML)SpringUtil.getBean("mailingListDaoForDML");
	 		usersDao = (UsersDao)SpringUtil.getBean("usersDao");
	 		posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
		//added for sharing
		listIdsSet = (Set<Long>)session.getAttribute(Constants.LISTIDS_SET);
	}
	
	/**
	 * This is for edit.zul initialization 
	 * @param custFieldsGbId
	 */
	/*
	public void initCusfomFieldGrid(Grid custFieldsGbId) {
		this.custFieldsGbId = custFieldsGbId;

		List<MLCustomFields> conCfList;
		
		conCfList = getCustomFields();
		if(conCfList!=null && conCfList.size()>0) {
			
			for (MLCustomFields mlCustomFields : conCfList) {
				if(mlCustomFields.getFieldIndex() > configuredCfMaxFieldIndex) {
					configuredCfMaxFieldIndex = mlCustomFields.getFieldIndex(); 
				}
			} // for
			
			addCFEntryRule(conCfList);
		}
		
	} // init
	
	*/
	
	private A upLoadContactsAnchrId;
	private Tab mlListTabId;
	private Menupopup contactsListMpId;
	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		
		
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		//logger.info("----just entered---");
		purgeList = (PurgeList)SpringUtil.getBean("purgeList");
		//purgeList.addObserverToQue(currentUserId, this);

		String style = "font-weight:bold;font-size:15px;color:#313031;" +
     			"font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Lists","",style,true);
 		
		session.setAttribute("isNewML","false");
 		
 		//int totalCount = mailingListDao.getCountByUser(userIdsSet);
 		 
 		int totalCount = mailingListDao.getCountByListIds(listIdsSet);
 		logger.debug(">>>>>>>>>>>>>>>>>>"+totalCount);
 		if(totalCount ==0 ) {
 			//mlistVboxId.setVisible(false);
 			warnDivId.setVisible(true);
 		} else {
 			//mlistVboxId.setVisible(true);
 			warnDivId.setVisible(false);
 		}
 		

 		try{
 			
 			Set<String> userRoleSet = (Set<String>)session.getAttribute("userRoleSet");
 		//	logger.debug("userRoleSet ::"+userRoleSet);
 			
 			if(userRoleSet != null) {
 				upLoadContactsAnchrId.setVisible(userRoleSet.contains(RightsEnum.MenuItem_AddImport_Contacts_VIEW));
 				
 				/*if(userRoleSet.contains(Constants.ROLE_USER_BASIC)) {
 					upLoadContactsAnchrId.setVisible(false);
 					
 				}
 				*/
 				
 			}
 			
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ::", e);
		}
		
 		
 		
 		
 		//mlistPaging.setDetailed(true);
 		
		mlistPaging.setTotalSize(totalCount);
		mlistPaging.setAttribute("onPaging", "topPaging");
		mlistPaging.addEventListener("onPaging", this);
		
		mlistPaging1.setTotalSize(totalCount);
		mlistPaging1.setAttribute("onPaging", "bottomPaging");
		mlistPaging1.addEventListener("onPaging", this);
		
		
		
		//redraw(0,mlistPaging.getPageSize());
		redraw(0, totalCount);
		
		 /*if(userActivitiesDao != null) {
		      	userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CONTACT_LIST_VIEW,GetUser.getUserObj());
		  }*/
		if(userActivitiesDaoForDML != null) {
	      	userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CONTACT_LIST_VIEW,GetUser.getLoginUserObj());
		}
		 Map<Integer, Field> objMap = new HashMap<Integer, Field>();
			
		objMap.put(1, MailingList.class.getDeclaredField("listName"));
		objMap.put(2, MailingList.class.getDeclaredField("listSize"));
		objMap.put(3, MailingList.class.getDeclaredField("description"));
		objMap.put(4, MailingList.class.getDeclaredField("lastModifiedDate"));
			
		LBFilterEventListener.lbFilterSetup(contactsListLBId, null, null, null, null,  objMap);
		 
	}//doAfterCompose(-)
	
	
	/**
	 * This is for myLists.zul initialization
	 * @param  contactsListLBId
	 * @param mlistPaging
	 * @param mlistVboxId
	 * @param warnDivId
	 */
	/*
	public void init( Paging mlistPaging, Listbox  contactsListLBId, Div mlistVboxId , Div warnDivId, Popup copyListPopupId, Textbox copyListNameTbId) {
     	MessageUtil.clearMessage();
		this.contactsListLBId =  contactsListLBId;
		this.copyListPopupId = copyListPopupId;
		this.copyListNameTbId = copyListNameTbId;
		purgeList = (PurgeList)SpringUtil.getBean("purgeList");
		purgeList.addObserverToQue(userId, this);

		String style = "font-weight:bold;font-size:15px;color:#313031;" +
     			"font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("My Contacts","",style,true);
 		
		Sessions.getCurrent().setAttribute("isNewML","false");
 		 mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
 		
 		int totalCount = mailingListDao.getCountByUser(userId);

 		if(totalCount ==0 ) {
 			mlistVboxId.setVisible(false);
 			warnDivId.setVisible(true);
 		} else {
 			mlistVboxId.setVisible(true);
 			warnDivId.setVisible(false);
 		}
 			
	
		mlistPaging.setTotalSize(totalCount);
		mlistPaging.addEventListener("onPaging", this);
		new EventListener() {
		
		public void onEvent(Event e) {
			
			Paging mlistPaging = (Paging) e.getTarget();
			int desiredPage = mlistPaging.getActivePage();
			PagingEvent pagingEvent = (PagingEvent) e;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			redraw(ofs, (byte) pagingEvent.getPageable().getPageSize());
		}
	});
		redraw(0,mlistPaging.getPageSize());
		
		 if(userActivitiesDao != null) {
		      	userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CONTACT_LIST_VIEW,GetUser.getUserObj());
		  }
	}
	
	*/
	
	private Include viewContactsIncId;
	public void onClick$contactsTabId() {
		logger.debug("contacts are loaded successfully.........");
		session.setAttribute("viewType", "contact");
		viewContactsIncId.setSrc("/zul/contact/contacts.zul");
		
		
	}
	
	
	/****delete the slected contacts  *****/
	public void onClick$delContactsBtnId() {
		try {
			if(contactsListLBId.getItemCount() == 0){
				logger.debug("No MailingLists  existed for deleting");
				return;
			}
			//logger.debug("The selected list(s) are deleted.");
			deleteSelected(contactsListLBId,null);
		} catch (Exception e) {
			logger.error("Exception :: errorr while getting from the deleteSeleted method",e);
		}
	}
	
	Paging pagingId;
	
	/****display the no of contacts by select listbox  *****/
	public void onSelect$membersPerPageListboxId() {
		try {
			logger.debug("Just enter here...");
			
			if(contactsListLBId.getItemCount() == 0 ) {
				
				logger.debug("No mailing lists found for this user...");
				return;
			}
			//changeRows(membersPerPageListboxId.getSelectedItem().getLabel(),mlistPaging);
			mlistPaging.setPageSize(Integer.parseInt(membersPerPageListboxId.getSelectedItem().getLabel()));
						
			mlistPaging.setActivePage(0);
			pagingId.setPageSize(Integer.parseInt(membersPerPageListboxId.getSelectedItem().getLabel()));
			
		} catch (Exception e) {
			logger.error("Exception :: errorr while getting from the changeRows method",e);
		}
	}
	
	public void onSelect$contactsListLBId() {
		//logger.info("in onselect of the contacts list box.........");
		
		if(contactsListLBId.getSelectedCount() == 0){
			
			listActionsBandBoxId.setDisabled(true);
			listActionsBandBoxId.setButtonVisible(false);
		}else if(contactsListLBId.getSelectedCount() > 0) {
			
			listActionsBandBoxId.setDisabled(false);
			listActionsBandBoxId.setButtonVisible(true);
		}
		
	}//onSelect$contactListLBId()
	
	public void onCheck$checkBoxId(){
		
		if(!checkBoxId.isChecked()) {
			purgeList = (PurgeList)SpringUtil.getBean("purgeList");

			String style = "font-weight:bold;font-size:15px;color:#313031;" +
	     			"font-family:Arial,Helvetica,sans-serif;align:left";
			PageUtil.setHeader("Lists","",style,true);
	 		
			session.setAttribute("isNewML","false");
	 		
	 		 
	 		int totalCount = mailingListDao.getCountByListIds(listIdsSet);
	 		logger.debug(">>>>>>>>>>>>>>>>>>"+totalCount);
	 		if(totalCount ==0 ) {
	 			warnDivId.setVisible(true);
	 		} else {
	 			warnDivId.setVisible(false);
	 		}
	 		

	 		try{
	 			
	 			Set<String> userRoleSet = (Set<String>)session.getAttribute("userRoleSet");
	 			
	 			if(userRoleSet != null) {
	 				upLoadContactsAnchrId.setVisible(userRoleSet.contains(RightsEnum.MenuItem_AddImport_Contacts_VIEW));
	 				
	 				
	 				
	 			}
	 			
			}catch (Exception e) {
				// TODO: handle exception
				logger.error("Exception ::", e);
			}
			
	 		
	 		
	 		
	 		
			mlistPaging.setTotalSize(totalCount);
			mlistPaging.setAttribute("onPaging", "topPaging");
			mlistPaging.addEventListener("onPaging", this);
			
			mlistPaging1.setTotalSize(totalCount);
			mlistPaging1.setAttribute("onPaging", "bottomPaging");
			mlistPaging1.addEventListener("onPaging", this);
			
			
			
			redraw(0, totalCount);
			
			
			if(userActivitiesDaoForDML != null) {
		      	userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CONTACT_LIST_VIEW,GetUser.getLoginUserObj());
			}
			try {
			 Map<Integer, Field> objMap = new HashMap<Integer, Field>();
			
			objMap.put(1, MailingList.class.getDeclaredField("listName"));
			objMap.put(2, MailingList.class.getDeclaredField("listSize"));
			objMap.put(3, MailingList.class.getDeclaredField("description"));
			objMap.put(4, MailingList.class.getDeclaredField("lastModifiedDate"));
				
			LBFilterEventListener.lbFilterSetup(contactsListLBId, null, null, null, null,  objMap);
			}catch (Exception e) {
				// TODO: handle exception
				logger.error("Exception ::", e);
			}
		}else {
			
			textBoxId1.setText("");
			textBoxId2.setText("");
			intBoxId.setText("");
			dateBoxId.setText("");
			
		}
		
	}
	
	
	/****upload the contacts   *****/
	public void onClick$upLoadContactsAnchrId() {
		try {
			logger.debug("onClick$upLoadContactsAnchrId() maetod calling...");
			gotoUploadContacts();
		} catch (Exception e) {
			logger.error("Exception :: errorr while redirecting the page ::",e);
		}
	}
	
	/****duplicate entry of listName   *****/
	public void onClick$copyListNameBtnId() {
		try {
			logger.debug("copy list name method is calling...");
			copyList(copyListNameTbId.getValue());
		} catch (Exception e) {
			logger.error("Exception :: errorr getting from the copyList method >> ::",e);
		}
	}
	
	public void onTimer$timer() {
		try {
			
			
			checkListsPurgingStatus();
		} catch (Exception e) {
			logger.error("Exception :: errorr getting from the checkListsPurgingStatus method >> ::",e);
		}
	}
	
	
	private void redraw(int start_index, int _size) {
		
		MessageUtil.clearMessage();
		logger.debug("-- just entered --");
		int count =  contactsListLBId.getItemCount();
		
		for(; count>0; count--) {
			 contactsListLBId.removeItemAt(count-1);
		}
		
		//System.gc();
		TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone"); 
		MailingListDao mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		
		//List<MailingList> mlist = mailingListDao.findAllBySize(userId,start_index, _size);
		String userIds = Constants.STRING_NILL;
		try {
			List<Long> usersList = mailingListDao.findUsersBy(listIdsSet);
			
			if(usersList != null && usersList.size() > 0) {
				for (Long userId : usersList) {
					
					if(!userIds.isEmpty()) userIds += Constants.DELIMETER_COMMA;
					
					userIds += userId.longValue();
				}
				
			}
		} catch (BaseDAOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		long start = System.currentTimeMillis();
		List<MailingList> mlist = mailingListDao.findAllBySize(listIdsSet, userIds, start_index, _size);
		
		logger.debug("Time took for executing this query ::"+(System.currentTimeMillis()-start));
		
		if(mlist == null){
			
			   MessageUtil.setMessage("Please create Mailing List before adding contacts.", "color:red");
			   return;
    	  }
		
		logger.debug("Got list of mailinglists of size : " + mlist.size());
		
		Listitem li = null;
		Listcell lc = null;
		logger.debug("Creating rows");
		Html html= null;
		Div htmlDiv = null;
		
		for (MailingList mailingList : mlist) {
			
			li = new Listitem();
			li.setValue(mailingList);

			lc = new Listcell();
			lc.setParent(li);
			
			lc = new Listcell();
			Iterator<MailingList> mlistItr = mlist.iterator();
			String listNameStr = mailingList.getListName();
			
			while (mlistItr.hasNext()) {
				MailingList ml = (MailingList) mlistItr.next();
				if(listNameStr.equals(ml.getListName()) && mailingList.getListId().longValue() != ml.getListId().longValue() &&
						mailingList.getUsers().getUserId().longValue() != currentUserId.longValue()) {
					
					listNameStr = mailingList.getListName() +"(Shared By "+Utility.getOnlyUserName(mailingList.getUsers().getUserName())+")";
				}
			}
			
			Label label = new Label(listNameStr);
			label.setMaxlength(25);
			lc.setTooltiptext(listNameStr);
			lc.setValue(mailingList);
			label.setStyle("cursor:pointer;color:blue;");
			label.addEventListener("onClick", this);
			label.setAttribute("test", "actual");
			label.setParent(lc);
			lc.setParent(li);
			//logger.info("mailingList.getSize()+ "+ mailingList.getListSize()+ "");
			lc = new Listcell(mailingList.getListSize()+ Constants.STRING_NILL);
			//lc.setStyle("padding-right:35px");
			lc.setParent(li);
			
			lc = new Listcell(mailingList.getDescription() == null ? "":mailingList.getDescription());
			lc.setTooltiptext(mailingList.getDescription() == null ? "":mailingList.getDescription());
			lc.setParent(li);
			
			lc = new Listcell(MyCalendar.calendarToString(mailingList.getLastModifiedDate(),
					MyCalendar.FORMAT_DATETIME_STDATE, tz));
			lc.setParent(li);
			
			lc = new Listcell();
			Hbox hbox = new Hbox();
			Image img ;
			
			if(mailingList.getListType() == null || !(mailingList.getListType().equalsIgnoreCase(Constants.MAILINGLIST_TYPE_POS)  || 
														mailingList.getListType().equalsIgnoreCase(Constants.MAILINGLIST_TYPE_OPTIN_LIST) ||
														mailingList.getListType().equalsIgnoreCase(Constants.MAILINGLIST_TYPE_HOMESPASSED))) {
				
				
				img = new Image("/img/icons/add-bulk-contacts-icon1.png");
				img.setTooltiptext("Add Bulk Contacts");
				img.setStyle("margin-right:5px;cursor:pointer;");
				img.setAttribute("imageEventName", "bulkAddition");
				img.addEventListener("onClick", this);
				img.setParent(hbox);
				
				
				
				img = new Image("/img/icons/add_icon.png");
				//img.setTooltiptext("Add Contacts");
				img.setTooltiptext("Add Contact");
				img.setStyle("margin-right:5px;cursor:pointer;");
				img.setAttribute("imageEventName", "addContacts");
				img.addEventListener("onClick", this);
				img.setParent(hbox);
				
				
				
				img = new Image("/img/icons/edit_lists_icon.png");
				
				img.setTooltiptext("Edit");
				img.setStyle("margin-right:5px;cursor:pointer;");
				img.setAttribute("imageEventName", "editList");
				img.addEventListener("onClick", this);
				img.setParent(hbox);
				
				img = new Image("/img/icons/copy.png");
				img.setTooltiptext("Copy");
				img.setStyle("margin-right:5px;cursor:pointer;");
				img.setAttribute("imageEventName", "copyList");
				img.addEventListener("onClick", this);
				
				img.setParent(hbox);
				
				img = new Image("/img/icons/Export-of-Promo-codes-icon.png");
				img.setTooltiptext("Export");
				img.setStyle("margin-right:5px;cursor:pointer;");
				img.setAttribute("imageEventName", "export");
				img.addEventListener("onClick", this);
				
				img.setParent(hbox);
				
				img = new Image("/img/icons/delete_icon.png");
				img.setTooltiptext("Delete");
				img.setStyle("margin-right:3px;cursor:pointer;");
				img.setAttribute("imageEventName", "deleteSelected");
				img.addEventListener("onClick", this);
				
				img.setParent(hbox);
							
			}
			else
			{
				img = new Image("/img/icons/Export-of-Promo-codes-icon.png");
				img.setTooltiptext("Export");
				img.setStyle("margin-right:5px;cursor:pointer;");
				img.setAttribute("imageEventName", "export");
				img.addEventListener("onClick", this);
				img.setParent(hbox);
			}
			
			
			Boolean isInQueue = false;
			
			/*if(purgeList.contains(mailingList.getListId()) ||
					( purgeList.runningListId()!= null && 
							purgeList.runningListId().longValue() == mailingList.getListId().longValue())) {
				isInQueue = true;
			}*/
			
			 html = new Html();
		     htmlDiv = new Div();
		     htmlDiv.setId("htmlDiv" + mailingList.getListId());
		     
		     //long totalCount = mailingListDao.findTotOnlyEmail(mailingList);
		     //logger.info(">>>>>list Name ::"+mailingList.getListName()+" <<<< total contacts ::"+mailingList.getSize()+ " <<< email Id count ::"+totalCount+">>> unpurged count ::"+mailingList.getUnPergedCount());
		     htmlDiv.setAttribute("mailinglist", mailingList);
		     
		     html.setParent(htmlDiv);
		     if (isInQueue.booleanValue()) {
		    	img = new Image("/img/progress.gif");
		    	img.setParent(htmlDiv);
		    	htmlDiv.setTooltiptext("purging in progress");	
		    	 
		    	this.divHt.put("htmlDiv" + mailingList.getListId(), htmlDiv);
		     }	
		     else {
		    	 /*html.setContent(prepareInnerHTMLContent(mailingList.getSize().intValue(), 
		    					 mailingList.getUnPergedCount().intValue()));*/
				// logger.info("mailing list size==="+mailingList.getListSize().intValue());
				html.setContent(prepareInnerHTMLContent(mailingList.getListSize().intValue(), 
						0));
			}
			/*if (mailingList.getUnPergedCount().longValue() > 0L) {
		    	 if (!(isInQueue.booleanValue())) {
		    		 htmlDiv.setTooltiptext("List have unpurged contacts, click here to purge the list");
		    		 htmlDiv.setStyle("margin-right:3px;cursor:pointer;");
		    	 }
		    	 htmlDiv.setAttribute("listId", mailingList.getListId());
		    	 htmlDiv.addEventListener("onClick", this);
			}
			else {
				htmlDiv.setTooltiptext("List is purged already");
				htmlDiv.setStyle("margin-right:3px;");
			}*/
		     
		       	 htmlDiv.setTooltiptext("click here to purge the list");
		    	 htmlDiv.setStyle("margin-right:3px;cursor:pointer;");
		    	 
		    	 htmlDiv.setAttribute("listId", mailingList.getListId());
		    	 htmlDiv.addEventListener("onClick", this);
		    	 htmlDiv.setVisible(false);
		     
			htmlDiv.setParent(hbox);
			
			hbox.setParent(lc);
			lc.setParent(li);
			
			li.setParent( contactsListLBId);
		}
		mlist.clear();
		mlist = null;
		logger.debug("-- exit --");
	}
	
	public List<MailingList> getMailingLists() {
		mailingListDao=(MailingListDao)SpringUtil.getBean("mailingListDao");
		//return mailingListDao.findAllByUser(userIdsSet);
		
		return mailingListDao.findByIds(listIdsSet);
	}
	
	
	
	private String prepareInnerHTMLContent(int totalSize, int unPurgedSize) {
		
		//String unPurgedWidthStr = Utility.getPercentage(unPurgedSize, totalSize, 0);
		//String purgedWidthStr =Utility.getPercentage((totalSize-unPurgedSize), totalSize, 0);
		
		/*String innerHtml = "<table width=\"40px\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">" +
				"<tr style=\"height: 14px;\"><td style=\"width: "+purgedWidthStr+"%; background: url("+appUrl+"img/leftblue_img.gif) scroll left top no-repeat;\"></td>" +
				"<td style=\"width: "+unPurgedWidthStr+"%; background: url("+appUrl+"img/red_img.gif) scroll right top no-repeat;\"></td></tr></table>";*/
		
		String innerHtml = "<table width=\"40px\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">" +
				"<tr style=\"height: 14px;\"><td style=\"width: "+50+"%; background: url("+appUrl+"img/leftblue_img.gif) scroll left top no-repeat;\"></td>" +
				"</tr></table>";
		
		
		/*if(totalSize <= 0){
		
			 innerHtml = "<table width=\"40px\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">" +
					"<tr style=\"height: 14px;\"><td style=\"width: "+50+"%; background: url("+appUrl+"img/leftblue_img.gif) scroll left top no-repeat;\"></td>" +
					"<td style=\"width: "+50+"%; background: url("+appUrl+"img/red_img.gif) scroll right top no-repeat;\"></td></tr></table>";
		}*/
		return innerHtml;
	}
	
	private void addContacts(Image img){
		try{
			Listitem li = (Listitem)img.getParent().getParent().getParent();
			MailingList mlist = (MailingList) li.getValue();
			
			if( mlist.getUsers().getUserId().longValue() != GetUser.getUserObj().getUserId().longValue()) {
				
				MessageUtil.setMessage("The selected list has been shared by others.You cannot add a contact to shared list", "color:red;");
				return;
			}
			mlist = mailingListDao.findById(mlist.getListId());
			Set selSet = new HashSet();
			selSet.add(mlist);
			Sessions.getCurrent().setAttribute("AddSingle_Ml",selSet);
			Sessions.getCurrent().setAttribute("isNewML","false");
			//Sessions.getCurrent().setAttribute("from_page","/contact/myLists.zul");
			MessageUtil.clearMessage();
			PageUtil.setFromPage("/contact/myLists.zul");
			
			//xcontents.setSrc("contact/AddSingle.zul");
			Redirect.goTo(PageListEnum.CONTACT_ADDSINGLE_NEW);
		}catch (Exception e) {
			logger.error(" - ** Exception while adding the cotacts to the exitsting list - " + e + " **");
		}
	}

	private void editList(Image img){
		try{
			Listitem li = (Listitem)img.getParent().getParent().getParent();
			MailingList mailingList = (MailingList)li.getValue();
			mailingList = mailingListDao.findById(mailingList.getListId());
			MessageUtil.clearMessage();
			Sessions.getCurrent().setAttribute("mailingList", mailingList);
			logger.debug("Redirecting to edit settings for the list - " + mailingList.getListName());
			Redirect.goTo(PageListEnum.CONTACT_LIST_EDIT);
		}catch (Exception e) {
			logger.error(" - ** Exception while trying to edit the list settings - " + e + " **");
		}
	}

	/*private void copyList(Image img){
		try{
			Listitem li = (Listitem)img.getParent().getParent().getParent();
			MailingListDao mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
			ContactsDao contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
			MailingList mlist = (MailingList) li.getValue();
			String lname = mlist.getListName();
			String name = "copy of " + lname;
			if(name.length() > 50 ) {
				MessageUtil.setMessage("Unable to copy the list. List name length exceeds the maximum characters limit.", "color:red", "TOP");
				return;
			}
			MailingList newMlist = new MailingList(mlist);
			try{
				newMlist.setListName(name);
				mailingListDao.saveOrUpdate(newMlist);
				logger.debug(" - cotact lists - '" + name + "' is created for the user " + GetUser.getUserName());
				contactsDao.copyList(mlist.getListId(), newMlist.getListId());
				MessageUtil.setMessage( "Copy of '" + lname + "' is created as '" + name + "'", "color:blue;", "TOP");
				logger.debug(" - copy of " + lname + "is created as '" + name + "'");
				Include xcontents = Utility.getXcontents();
				xcontents.invalidate();
			}catch(DataIntegrityViolationException ex){
				MessageUtil.setMessage("Copy of the list " + lname + " already exists.","color:red;","TOP");
				logger.error(" - ** Exception contact list already exists with the name : " + ex + " **");
			}catch (Exception e) {
				logger.error(" - ** Exception while copying the contact list : " + e + " **");
			}
		}catch (Exception e) {
			logger.error(" - ** Exception making the copy of the list: " + e + " **");
		}
		
	}*/

	public void copyList(String listName) throws Exception {
		try {
			//ContactsDao contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
			ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)SpringUtil.getBean("contactsDaoForDML");
			MailingList mlist = (MailingList)copyListPopupId.getAttribute("mailingList");
			if(mlist == null){
				logger.debug("Selected mailing list is null");
				return;
				
			}
			listName = listName.trim();
			String lname = mlist.getListName().trim(); 
			if(listName.length() > 50 ) {
				MessageUtil.setMessage("Unable to copy the list. List name size exceeds 50 characters limit.", "color:red", "TOP");
				return;
			}
			if(!Utility.validateName(listName)) {
				Messagebox.show("Provide valid list name. Name should not contain any special characters.",
						"Information",Messagebox.OK,Messagebox.ERROR);
				return;
			}
			
			//MailingList newMlist = new MailingList(mlist);
			try{
				mlist = mailingListDao.findById(mlist.getListId());
				MailingList newMlist =  mlist.getMlCopy();
				
				long mlbit = mailingListDao.getNextAvailableMbit(currentUserId);
				
				if(mlbit == 0l){
					MessageUtil.setMessage("You have exceeded limit on maximum number of lists(60). " +
							"Please delete one or more lists to create a new list.", "red");
					return;
				}
				
				
				newMlist.setMlBit(mlbit);
				newMlist.setListName(listName);
				mailingListDaoForDML.saveOrUpdate(newMlist);
				
				listIdsSet.add(newMlist.getListId());
				session.setAttribute(Constants.LISTIDS_SET, listIdsSet);
				
				contactsDaoForDML.copyList(mlist, newMlist);
				//TODO need implement for sharing
				MessageUtil.setMessage( "Copy of '" + lname + "' is created as '" + listName + "'", "color:blue;", "TOP");
				Include xcontents = Utility.getXcontents();
				xcontents.invalidate();
			}catch(DataIntegrityViolationException ex){
				MessageUtil.setMessage("Copy of the list " + lname + " already exists.","color:red;","TOP");
				logger.error(" - ** Exception contact list already exists with the name : " + ex + " **");
			}catch (Exception e) {
				logger.error(" - ** Exception while copying the contact list : " + e + " **");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("** Exception while creating the copy of List.",e);
		}
		
	}
	
	public void exportSelected(Image img) throws Exception{
	
		
		MessageUtil.clearMessage();
		
		if(img!=null){
			Listitem li = (Listitem)img.getParent().getParent().getParent();
			mailingList = (MailingList) li.getValue();
			
		}
		try {
			if(currentUserId!=null){
				createWindow(img);
				
				anchorEvent(false);
				
				custExport.setVisible(true);
				custExport.doHighlighted();
			}
			else{
				
				MessageUtil.setMessage("Please select a user", "info");
			}
		} catch (Exception e) {
			logger.error("Error occured from the exportCSV method ***",e);
		}
		/*else{
			selSet  = lb.getSelectedItems();
			if(selSet.size()==0){
				MessageUtil.setMessage("Select the list to export.", "color:red;", "TOP");
				return;
			}
			if(selSet.size()>1){
				MessageUtil.setMessage("Select only one list to export.", "color:red;", "TOP");
				return;
			}
		}*/
		
		
	}
public void onClick$selectAllAnchr$custExport() {
		
		anchorEvent(true);

	}

	public void onClick$clearAllAnchr$custExport() {
		
		anchorEvent(false);
	}

	public void anchorEvent(boolean flag) {
		List<Component> chkList = custExport$chkDivId.getChildren();
		Checkbox tempChk = null;
		for (int i = 0; i < chkList.size(); i++) {
			if(!(chkList.get(i) instanceof Checkbox)) continue;
			
			tempChk = (Checkbox)chkList.get(i);
			tempChk.setChecked(flag);
			
		} // for

	}
	public void onClick$selectFieldBtnId$custExport() {

		custExport.setVisible(false);
		List<Component> chkList = custExport$chkDivId.getChildren();
		String query = "select ";

		int indexes[]=new int[chkList.size()];
		int[] loyaltyIndexsOne=new int[contactLoyaltyLabelsOne.length];
		int[] loyaltyIndexsTwo = new int[contactLoyaltyLabelsTwo.length];



		boolean checked=false;

		for(int i=0;i<contactLoyaltyLabelsOne.length;i++) {
			loyaltyIndexsOne[i]=-1;
		} // for

		for(int i=0;i<contactLoyaltyLabelsTwo.length;i++) {
			loyaltyIndexsTwo[i]=-1;
		} // for

		for(int i=0;i<chkList.size();i++) {
			indexes[i]=-1;
		} 

		Checkbox tempChk = (Checkbox)custExport$chkDivId.getFirstChild();

		//Created Date
		if( tempChk.isChecked()) {

			indexes[0]=0;
			checked=true;
			query += tempChk.getValue();
		}

		//Email
		tempChk = (Checkbox)chkList.get(1);
		if(tempChk.getLabel().equalsIgnoreCase("Email") ) {

			if(tempChk.isChecked()){
				indexes[1]=0;
				checked=true;
				query += tempChk.getValue();
			}
			else{
				indexes[1]=-1;
			}
		}

		//Email Status
				tempChk = (Checkbox)chkList.get(2);
				if(tempChk.getLabel().equalsIgnoreCase("Email Status") ) {

					if(tempChk.isChecked()){
						indexes[2]=0;
						checked=true;
						query += tempChk.getValue();
					}
					else{
						indexes[2]=-1;
					}
				}
				
				//Mobile Number

				tempChk = (Checkbox)chkList.get(3);
				if(tempChk.getLabel().equalsIgnoreCase("Mobile Number") ) {
						
					if(tempChk.isChecked()){
						indexes[3]=0;
						checked=true;
						query += tempChk.getValue();
					}
					else{
						indexes[3]=-1;
					}
				}
				
				//Mobile Status

				tempChk = (Checkbox)chkList.get(4);
				if(tempChk.getLabel().equalsIgnoreCase("Mobile Status") ) {
					
					if(tempChk.isChecked()){
							indexes[4]=0;
							checked=true;
							query += tempChk.getValue();
						}
							else{
								indexes[4]=-1;
							}
						}
		
		//card number
		tempChk = (Checkbox)chkList.get(5);
		if(tempChk.getLabel().equalsIgnoreCase("Membership Number") ) {

			if(tempChk.isChecked()){
				loyaltyIndexsOne[0]=0;
				checked=true;
				query += tempChk.getValue();
			}
			else{
				loyaltyIndexsOne[0]=-1;
			}
		}
		//card Pin
		tempChk = (Checkbox)chkList.get(6);
		if(tempChk.getLabel().equalsIgnoreCase("Card Pin") ) {

			if(tempChk.isChecked()){
				loyaltyIndexsOne[1]=0;
				checked=true;
				query += tempChk.getValue();
			}
			else{
				loyaltyIndexsOne[1]=-1;
			}
		}//if

		//Loyalty Balances
		for(int i=0; i < 5; i++)
		{
		tempChk = (Checkbox)chkList.get(i+7);
		if(tempChk.getLabel().equalsIgnoreCase(contactLoyaltyLabelsTwo[i]) ) {

			if(tempChk.isChecked()){
				loyaltyIndexsTwo[i]=0;
				checked=true;
				query += tempChk.getValue();
			}
			else{
				loyaltyIndexsTwo[i]=-1;
			}
		}
		}
		/*//rewards balance
		tempChk = (Checkbox)chkList.get(8);
		if(tempChk.getLabel().equalsIgnoreCase("Reward Balance") ) {

			if(tempChk.isChecked()){
				loyaltyIndexsTwo[1]=0;
				checked=true;
				query += tempChk.getValue();
			}
			else{
				loyaltyIndexsTwo[1]=-1;
			}
		}//if
*/

		for (int i = 0; i < chkList.size(); i++) {
			if(!(chkList.get(i) instanceof Checkbox)) continue;

			tempChk = (Checkbox)chkList.get(i);
			if(tempChk.getLabel().equalsIgnoreCase("Created Date") ||tempChk.getLabel().equalsIgnoreCase("Email")||tempChk.getLabel().equalsIgnoreCase("Email Status") || tempChk.getLabel().equalsIgnoreCase("Membership Number") ||
					tempChk.getLabel().equalsIgnoreCase("Card Pin") || tempChk.getLabel().equalsIgnoreCase("Mobile Number") ||tempChk.getLabel().equalsIgnoreCase("Mobile Status")||
					tempChk.getLabel().equalsIgnoreCase("Points") ||tempChk.getLabel().equalsIgnoreCase("Reward Balance") ||tempChk.getLabel().equalsIgnoreCase("Gift Balance")
					||tempChk.getLabel().equalsIgnoreCase("Hold Points")||tempChk.getLabel().equalsIgnoreCase("Hold Currency"))  {
				continue;
			}

			if(tempChk.isChecked()) {
				indexes[i]=0;
				checked=true;
				if(query.indexOf((String)tempChk.getValue()) == -1 ) {
					query += tempChk.getValue();
			}
				
			}
		} // for

		query = query.substring(0, query.length()-2);
		if(checked) {

			int confirm=Messagebox.show("Do you want to Export with selected fields ?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm==1){
				try{

					exportCSV(query, "csv", indexes,loyaltyIndexsOne, loyaltyIndexsTwo);

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


	}//on select
	
	public void exportCSV(String query, String ext,int[] indexes,int[] loyaltyIndexsOne, int[] loyaltyIndexsTwo) {



		long startTime = System.currentTimeMillis();

		long totalContacts = 0;
		ContactsJdbcResultsetHandler contactsJdbcResultsetHandler = null;
		try {
			logger.debug("entered ");
			Long userId =mailingList.getUsers().getUserId();
			Long orgid=mailingList.getUsers().getUserOrganization().getUserOrgId();
			
			/*query += " from contacts c left join contacts_loyalty cl on c.cid = cl.contact_id where c.user_id = "+mailingList.getUsers().getUserId()+
					"  and c.mlbits & "+ mailingList.getMlBit() + " > 0 order by email_id";*/
			query += " from (select * from contacts where user_id="+userId.longValue()+
					"  and mlbits & "+ mailingList.getMlBit() + 
					" > 0 order by email_id) as c LEFT JOIN (select cl.* from contacts_loyalty cl where user_id="+userId.longValue()+") as cl  "
							+ " on c.cid = cl.contact_id    "
							+ "LEFT JOIN (select org.home_store_id, org.subsidary_id, org.store_name from org_stores  org where org_id="+orgid.longValue()+") as org on"
									+ " (if(c.subsidiary_number IS NOT NULL and c.subsidiary_number != '', c.subsidiary_number= org.subsidary_id AND c.home_store=org.home_store_id , c.home_store=org.home_store_id))";//where cl.user_id="+userId.longValue() ;
		
			/*query += " from contacts c LEFT JOIN contacts_loyalty cl on c.user_id=cl.user_id and c.cid = cl.contact_id where c.user_id = "+mailingList.getUsers().getUserId()+
					"  and c.mlbits & "+ mailingList.getMlBit() + " > 0 order by c.email_id";*/
						
			logger.info("query for export is"+query);
			
			contactsJdbcResultsetHandler= new ContactsJdbcResultsetHandler();
			contactsJdbcResultsetHandler.executeStmt(query);
			logger.fatal("time taken to execute the query"+(System.currentTimeMillis()-startTime));
			totalContacts = contactsJdbcResultsetHandler.totalRecordsSize();
			
			if( totalContacts == 0) {
				MessageUtil.setMessage("No contacts exist in the selected search", "color:red", "TOP");
				return;
			}
			
			ext = ext.trim();
			String userName = GetUser.getUserName();
			String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
			File downloadDir = new File(usersParentDirectory + "/" + userName + "/List/download/" );
			if(downloadDir.exists()){
				try {
					FileUtils.deleteDirectory(downloadDir);
					logger.debug(downloadDir.getName() + " is deleted");
				} catch (Exception e) {
					logger.error("Exception ::", e);
					logger.warn(downloadDir.getName() + " is not deleted");
				}
			}
			if(!downloadDir.exists()){
				downloadDir.mkdirs();
			}
			

			StringBuffer sb = null;
			
			String udfFldsLabel= "";


			//Created Date
			if(indexes[0]==0) {

				udfFldsLabel = "\""+"Created Date"+"\",";
			}

			//Email
			if(indexes[1]==0) {

				udfFldsLabel += "\""+"Email"+"\",";
			}
			
			if(indexes[2]==0){
				udfFldsLabel += "\""+"Email Status"+"\",";
			}
	

			//Mobile Number
			if(indexes[3]==0) {

				udfFldsLabel += "\""+"Mobile Number"+"\",";
			}	
			
			//Mobile Status
			if(indexes[4]==0) {
				udfFldsLabel += "\""+"Mobile Status"+"\",";
			}
			

			Users user = null;

			user = usersDao.findMlUser(mailingList.getUsers().getUserId());


			if(user == null) {

				logger.debug("do not Export as user is null....");
				return;
			}

			List<POSMapping> posMappingsList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", user.getUserId());

			Map<String, POSMapping> orderedMappingMap = getOrderedMappingSet(posMappingsList);

			// udfs logic here i think header here only

			int k=0;
			//Card Number , Card Pin
			for (String contactLoyaltyLabel : contactLoyaltyLabelsOne) {

				if(loyaltyIndexsOne[k]==0 && k<loyaltyIndexsOne.length) {
					
					udfFldsLabel += "\""+contactLoyaltyLabel+"\"";
					if(udfFldsLabel.length() > 0) 
					udfFldsLabel += ",";
				}
				k++;
			}//for

			
			k=0;
			//Points & Loyalty Balance
			for (String contactLoyaltyLabel : contactLoyaltyLabelsTwo) {

				if(loyaltyIndexsTwo[k]==0 && k<loyaltyIndexsTwo.length) {
				
					udfFldsLabel += "\""+contactLoyaltyLabel+"\"";
					if(udfFldsLabel.length() > 0) 
					udfFldsLabel += ",";
				}
				k++;
			}//for
			int indexLength=(indexes.length);

			k=12;
			for (String custFldKey : orderedMappingMap.keySet()) {

				if(custFldKey.equalsIgnoreCase("Mobile") || custFldKey.equalsIgnoreCase("Email"))
					continue;

				if(k==indexLength)
					break;
				if(k!=0 || k!=1 || k!=2 || k!=3 || k!=4) {
					if(indexes[k]==0 && k < indexLength) {
						
						
						udfFldsLabel += "\""+orderedMappingMap.get(custFldKey).getDisplayLabel().trim()+"\"";
						if(udfFldsLabel.length() > 0) 
						udfFldsLabel += ",";
						//logger.debug("********** headers o wat *****  "+orderedMappingMap.get(custFldKey).getDisplayLabel().trim() );
							
					}
				}
				k++;
			}//for


			sb = new StringBuffer();
			sb.append(udfFldsLabel);
			sb.append("\r\n");
			
		
			OCCSVWriter csvWriter = null;
			BufferedWriter bw = null;
			File file = null;
			try {
				String filePath = "";
				filePath = usersParentDirectory + "/" + userName + "/List/download/" + mailingList.getListName() + "_" + System.currentTimeMillis() + "." + ext;
				try {
					file = new File(filePath);
					bw = new BufferedWriter(new FileWriter(file));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					logger.error("file not found ",e1);		
				}
				bw.write(sb.toString());
				try {
					csvWriter = new OCCSVWriter(bw);
					csvWriter.writeAll(contactsJdbcResultsetHandler.getResultSet(), false);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("exception while initiating writer ",e);
					
				}finally{
					bw.flush();
					csvWriter.flush();
					bw.close();
					csvWriter.close();
				}
				
				
				
				Filedownload.save(file, "text/csv");
			} catch (Exception e) {
				logger.error("exception while initiating writer ",e);
				
			}finally {
				
				sb = null;
				posMappingsList = null; orderedMappingMap = null;user = null; bw= null;csvWriter = null;file= null;   indexes = null;
			    loyaltyIndexsOne = null; loyaltyIndexsTwo = null; file= null;downloadDir = null; udfFldsLabel = null; query = null;ext = null; 
				
			}
				
				
				
				
				
			logger.debug("exited");
		} 
			catch (Exception e) {
			logger.error("** Exception : " , e);
		}finally{
			if(contactsJdbcResultsetHandler!=null ) contactsJdbcResultsetHandler.destroy();
			contactsJdbcResultsetHandler = null;
			//System.gc();
		}
		long endTime = System.currentTimeMillis();
		logger.fatal("Time taken to export listcontacts is :::    :: " + (endTime-startTime));
	}//end of exportCSV


	public List<String[]> getHeaders(Image img) {

		int i=0;

		Users user = null;
		//UsersDao usersDao=(UsersDao)SpringUtil.getBean("usersDao");usersDao.findMlUser(mailingList.getUsers().getUserId());
		MailingList mlist = (MailingList)((Listitem)img.getParent().getParent().getParent()).getValue();
		user = usersDao.findMlUser(mlist.getUsers().getUserId());


		if(user == null) {

			logger.debug("do not Export as user is null....");
			return null;
		}
		POSMappingDao posMappingDao=(POSMappingDao)SpringUtil.getBean("posMappingDao");
		List<POSMapping> posMappingsList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", user.getUserId());
		
		
		Map<String, POSMapping> orderedMappingMap = getOrderedMappingSet(posMappingsList);
		
		List<String[]> headers = new ArrayList<String[]>();
		String[] headerWithDbValue = null;
		for (String custFldKey : orderedMappingMap.keySet()) {
			
			headerWithDbValue = new String[2];
			headerWithDbValue[0] = orderedMappingMap.get(custFldKey).getDisplayLabel().trim();
			headerWithDbValue[1] = genFieldContMap.get(orderedMappingMap.get(custFldKey).getCustomFieldName());
			
			if(headerWithDbValue[0]!=null) {
				headers.add(headerWithDbValue);
				
			}
		
		}//for
		
		return headers;
		
		
	}
public Map<String, POSMapping> getOrderedMappingSet(List<POSMapping> mappingList) {
		
		
		Map<String,	POSMapping> orderedMap = new LinkedHashMap<String, POSMapping>();
		for (String custFldkey : genFieldContMap.keySet()) {
			
			//logger.debug("keySet is::"+custFldkey);
			
			
			for (POSMapping posMapping : mappingList) {
				
				if(posMapping.getCustomFieldName().equals(custFldkey)) {
					
					orderedMap.put(custFldkey, posMapping);
					break;
				}
				
			}
			
		}
		
		return orderedMap;
		
		
	}
private String[] contactLoyaltyLabelsOne=new String[]{"Membership Number","Card Pin"};
private String[] contactLoyaltyLabelsTwo= new String[]{"Points","Reward Balance","Gift Balance","Hold Points","Hold Currency"};

public void createWindow(Image img)	{
		
		try {
			
			Components.removeAllChildren(custExport$chkDivId);
			List<String[]> headerFields = getHeaders(img);
			
			//Created Date
			Checkbox tempChk1 = new Checkbox("Created Date");
			tempChk1.setSclass("custCheck");
			tempChk1.setValue("c.created_date, ");
			tempChk1.setParent(custExport$chkDivId);

			//Email
			tempChk1 = new Checkbox("Email");
			tempChk1.setSclass("custCheck");
			tempChk1.setValue("c.email_id, ");
			tempChk1.setParent(custExport$chkDivId);
				
			//Email Status
			tempChk1 = new Checkbox("Email Status");
			tempChk1.setSclass("custCheck");
			tempChk1.setValue("c.email_status, ");
			tempChk1.setParent(custExport$chkDivId);
			
			tempChk1 = new Checkbox("Mobile Number");
			tempChk1.setSclass("custCheck");
			tempChk1.setValue("c.mobile_phone, ");
			tempChk1.setParent(custExport$chkDivId);
			
			//Mobile Status
			tempChk1 = new Checkbox("Mobile Status");
			tempChk1.setSclass("custCheck");
			tempChk1.setValue("c.mobile_status, ");
			tempChk1.setParent(custExport$chkDivId);
			
			//Card Number
			tempChk1 = new Checkbox("Membership Number");
			tempChk1.setSclass("custCheck");
			tempChk1.setValue("cl.card_number, ");
			tempChk1.setParent(custExport$chkDivId);
			//Card Pin
			tempChk1 = new Checkbox("Card Pin");
			tempChk1.setSclass("custCheck");
			tempChk1.setValue("cl.card_pin, ");
			tempChk1.setParent(custExport$chkDivId);
			

			Checkbox tempChk=null;
			if(headerFields!=null) {
					////Point Balance & Reward Balance
				for (int i = 0; i < contactLoyaltyLabelsTwo.length; i++) {
										
					tempChk=new Checkbox(contactLoyaltyLabelsTwo[i]);
					tempChk.setSclass("custCheck");
					tempChk.setParent(custExport$chkDivId);
					
					if(i==0) {
						tempChk.setValue("cl.loyalty_balance, ");
					}
					else if(i == 1) {
						tempChk.setValue("cl.giftcard_balance, ");
					}
					else if(i == 2) {
						tempChk.setValue("cl.gift_balance, ");
					}
					else if(i == 3) {
						tempChk.setValue("cl.holdpoints_balance, ");
					}
					else {
						tempChk.setValue("cl.holdAmount_balance, ");
					}
				}

				for (int i = 0; i < headerFields.size(); i++) {
					if(((String)headerFields.get(i)[0]).equalsIgnoreCase("Email") || ((String)headerFields.get(i)[0]).equalsIgnoreCase("Email ID") || ((String)headerFields.get(i)[0]).equalsIgnoreCase("Email_ID") ||((String)headerFields.get(i)[0]).equalsIgnoreCase("MobilePhone") ||((String)headerFields.get(i)[0]).equalsIgnoreCase("Mobile_Phone") || ((String)headerFields.get(i)[0]).equalsIgnoreCase("Mobile Phone") ||((String)headerFields.get(i)[0]).equalsIgnoreCase("Contact Number")){
						continue;
					}
					tempChk = new Checkbox((String)headerFields.get(i)[0]);
					tempChk.setSclass("custCheck");
					tempChk.setValue(((String)headerFields.get(i)[1])+", ");
					tempChk.setParent(custExport$chkDivId);
				} // for
				}
				else {
				
				MessageUtil.setMessage("There is no field to export ", "info");
			}
			
			
		} catch (Exception e) {
			
				logger.error("Exception ::", e);
			}
		
	}

	/**
	 * this method allows to delete the selected list(s).
	 * @param lb is the Listbox object from where the selected list has taken.
	 * @param img if not null it is called from onClick$deleteSelectAnchorId
	 * @throws Exception
	 */
	public void deleteSelected(Listbox lb, Image img) throws Exception{
		MessageUtil.clearMessage();
		Set selSet  = new HashSet();
		if(img!=null){
			Listitem li = (Listitem)img.getParent().getParent().getParent();
			selSet.add(li);
		}
		else {
			selSet  = lb.getSelectedItems();
			if(selSet.size()==0){
				MessageUtil.setMessage("Select the list(s) to delete.", "color:red;", "TOP");
				return;
			}
		}
		
		try {
			
			
			String listIdStr = Constants.STRING_NILL;
			Map<String,String> listNameMap = new HashMap<String, String>();
			
			for(Object obj:selSet){
				
				Listitem li = (Listitem)obj;
				MailingList mailingList = (MailingList) li.getValue();
				
				if(mailingList.getListType() != null && mailingList.getListType().equals("POS")) {
					logger.debug("pos Type list not deleted ...");
					
					
					if(selSet.size() == 1) {
						MessageUtil.setMessage("POS list cannot be deleted", "color:red;", "TOP");
						return;
					}
					else {
						continue;
					}
				}
				if(currentUserId.longValue() != mailingList.getUsers().getUserId().longValue()) {
					
					if(selSet.size() == 1) {
						MessageUtil.setMessage("One of the selected list(s) is shared by others, so it cannot be deleted.", "color:red;", "TOP");
						return;
					}
					
					
					
				}
				
				
				listIdStr +=  listIdStr.equals("")?mailingList.getListId() :","+mailingList.getListId();
				listNameMap.put(""+mailingList.getListId(), mailingList.getListName());
			}
			
			MailingListDao mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
			
			//AutoProgramDao autoProgramDao = (AutoProgramDao)SpringUtil.getBean("autoProgramDao");
			//ISH
				String notDelList = Constants.STRING_NILL;
				String delIdStr = Constants.STRING_NILL;
				List<String> configuredList = mailingListDao.getCampaignCountByMlists(listIdStr);
				List<String> smsConfiguredList = mailingListDao.getSMSCountByMlists(listIdStr);
				//List<String> progConfiguredList = mailingListDao.getProgramCountByMlists(listIdStr);
				List<String> eventTriggerConfiguredList = mailingListDao.getEventTriggerCountByMlist(listIdStr);
				
				if((configuredList.size()!=0) || (smsConfiguredList.size()!=0) || (eventTriggerConfiguredList.size()!=0))
				{
				List<String> activeConfiguredList = mailingListDao.findStatusByMlists(listIdStr,currentUserId,Constants.MAILINGLIST_CONFIGURED_TYPE_CAMPAIGN);
				if(activeConfiguredList.size()==0)
				{
					List<String> activeSmsConfiguredList = mailingListDao.findStatusByMlists(listIdStr,currentUserId,Constants.MAILINGLIST_CONFIGURED_TYPE_SMS);
					if(activeSmsConfiguredList.size()==0)
					{
							List<String> activeEventTriggerConfiguredList = mailingListDao.findStatusByMlists(listIdStr,currentUserId,Constants.MAILINGLIST_CONFIGURED_TYPE_EVENT_TRIGGER);
							if(activeEventTriggerConfiguredList.size()==0)
							{
								String idsArray[] = listIdStr.split(",");
								for (String id : idsArray) {
										delIdStr += delIdStr.equals("")?id: "," + id; 
								} //for
								logger.debug("Need to delete list Ids : " + delIdStr);
							}
							else{
								for (String etListId : eventTriggerConfiguredList) {
									
									if(notDelList.equals("")) notDelList += listNameMap.get(etListId);
									else if(!notDelList.equals("") && !notDelList.contains(listNameMap.get(etListId))) {
										notDelList += ","+listNameMap.get(etListId);
										
									}
									
								}
							}
					}
					else{
						for(String smsListId : activeSmsConfiguredList) {
							
							if(notDelList.equals("")) notDelList += listNameMap.get(smsListId);
							else if(!notDelList.equals("") && !notDelList.contains(listNameMap.get(smsListId))) {
								notDelList += ","+listNameMap.get(smsListId);
								
							}
							
						}
					}
				}
				else{
					for (String key : activeConfiguredList) {
						notDelList +=  notDelList.equals("")?listNameMap.get(key):","+listNameMap.get(key);
					}
				}
			}
			else
			{
				String idsArray[] = listIdStr.split(",");
				for (String id : idsArray) {
				delIdStr += delIdStr.equals("")?id: "," + id; 
				} //for
				logger.debug("Need to delete list Ids : " + delIdStr);
			}
			//logger.debug("Active campaign mailig list ids : " + activeConfiguredList);
			
			//String msg = deleteDecission(configuredList,listNameMap,listIdStr);
			/*for (String listId : smsConfiguredList) {
				
				notDelList +=  notDelList.equals("")?campMap.get(campId):","+campMap.get(campId);
				
			}//for
*/			
			
			if(delIdStr.length()>0 && notDelList.length()>0) {
				
				int confirm = Messagebox.show("The following list(s) are configured to Emails / SMS / Event Trigger, \n " +
						"and they cannot be " +
						"deleted. " + notDelList + ". Do you want to continue deleting other list(s)?", 
						"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				
				if(confirm != 1){
					return;
				}
			} //if
			
			if(delIdStr.length()==0 && notDelList.length()>0) {
				MessageUtil.setMessage("Following list(s) are configured to Emails / SMS / Event Trigger, \n " +
						"and cannot be deleted.  [ " + notDelList + " ]", "color:red;", "TOP");
				return;
			} //if
			
			int confirm = Messagebox.show("Are you sure you want to delete selected list(s)?", 
					"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			
			if(confirm != 1){
				return;
			}
			
			
			
			if(delIdStr.length()>0){
				ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)SpringUtil.getBean("contactsDaoForDML");
				ContactsDao contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
				
				//String cidStr = "";
				List<Contacts> contList = null;
				List<MailingList> delMlList = null;
				
				delMlList = mailingListDao.findByIds(delIdStr);
				
				Iterator<MailingList> delMlListIter = delMlList.iterator();
				MailingList ml = null;
				while(delMlListIter.hasNext()){
					ml = delMlListIter.next();
					
					//Pending
					long count1 = contactsDao.getCountForActiveMembership(currentUserId, ml);
					logger.info("No of Active contacts are ======" + count1);
					if(count1 > 0){
						MessageUtil.setMessage("Some of the selected List(s) have Active membership.You cannot delete them.", "color:red;");
    					return;
						
					}
					
					long count = contactsDaoForDML.updateContMlbits(currentUserId, ml);
					logger.info("Count : "+count);
					
					if(count >0){
						
						contactsDaoForDML.setContactFieldsOnDeletion(currentUserId);
						logger.debug(" Deleted contacts fields are set to default values, with mlbits zero");
					}
					/*contList = contactsDao.findContactsByMlBit(currentUserId, ml.getMlBit());
					if(contList != null){
					 for(Contacts contact : contList){
						
						contact.setMlBits(contact.getMlBits().longValue()-ml.getMlBit().longValue());
						if(contact.getMlBits().longValue() == 0l){
							Utility.setContactFieldsOnDeletion(contact);
							
						}
						contactsDaoForDML.saveOrUpdate(contact);
					 }
					}*/
					Iterator<Long> It = listIdsSet.iterator();
					while (It.hasNext()) {
						
						Long mlId = (Long) It.next();
						if(mlId.longValue() == ml.getListId().longValue() ) It.remove();
							
					}
					
					mailingListDaoForDML.deleteSharedEmailCampaign(ml.getListId());
					mailingListDaoForDML.deleteSharedSMSCampaign(ml.getListId());
					mailingListDaoForDML.deleteSharedEventTrigger(ml.getListId());
					mailingListDaoForDML.deleteSharedAssociation(ml.getListId());
					
					mailingListDaoForDML.delete(ml.getListId().longValue()+"");
				}
				
				session.setAttribute(Constants.LISTIDS_SET, listIdsSet);
				//contList = contactsDao.findContactByListIdStr(delIdStr);
				/*
				contactsDao.deleteByListIds(delIdStr);
				mailingListDao.delete(delIdStr);
				
				contList = contactsDao.findContactBycidStr(cidStr);
				
				for(Contacts contact : contList){
					if(contact.getMlBits() == null){
						Utility.setContactFieldsOnDeletion(contact);
						contactsDao.saveOrUpdate(contact);
					}
				}*/
				
				
				if(userActivitiesDaoForDML != null && currentUserId != null) {
					String delIdArr[] = delIdStr.split(",");
					for(int i =0;i<delIdArr.length;i++) {
						delIdArr[i] = listNameMap.get(delIdArr[i]); 
					}
					//userActivitiesDao.addToActivityList(ActivityEnum.CONTS_DEL_ML_p1mlName, GetUser.getUserObj(), Arrays.toString(delIdArr));
					userActivitiesDaoForDML.addToActivityList(ActivityEnum.CONTS_DEL_ML_p1mlName, GetUser.getLoginUserObj(), Arrays.toString(delIdArr));
				}
				
			} //if
			
			if(notDelList.length()>0){
				MessageUtil.setMessage("Some list(s) are deleted. " +
						"[ " + notDelList + " ] lists are configured to Email / SMS / Event Trigger and are not deleted."
						, "color:maroon;", "TOP");
			}else{
				MessageUtil.setMessage("Selected list(s) deleted successfully.", "color:blue;", "TOP");
			}
			logger.debug(" - selected contact lists are deleted");
			Include xcontents = Utility.getXcontents();
			xcontents.invalidate();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" - problem while deleting the selected contact lists");
		}
	}
	
	//yet to be removed
	@Deprecated
	public String deleteDecission(List<String> configuredList, Map<String,String> listNameMap, String listIdStr) {
		
		
		try {
			logger.debug("Configured mailig list ids : " + configuredList);
			String notDelList = "";
			for (String key : configuredList) {
				notDelList +=  notDelList.equals("")?listNameMap.get(key):","+listNameMap.get(key);
			}
			
			
			String idsArray[] = listIdStr.split(",");
			String delIdStr = "";
			for (String id : idsArray) {
				if(!configuredList.contains(id)){
					delIdStr += delIdStr.equals("")?id: "," + id; 
				}
			} //for
			logger.debug("Need to delete list Ids : " + delIdStr);
			
			if(delIdStr.length()>0 && notDelList.length()>0) {
				
				int confirm = Messagebox.show("The following lists are configured to emails and they cannot be " +
						"deleted. " + notDelList + ". Do you want to continue deleting others?", 
						"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				
				if(confirm != 1) {
					return null;
				}
			} //if
			
			if(delIdStr.length()==0 && notDelList.length()>0) {
				MessageUtil.setMessage("Following lists are configured to one or more emails " +
						"so cannot be deleted.  [ " + notDelList + " ]", "color:red;", "TOP");
				return null;
			} //if
			
			if(delIdStr.length()>0){
				ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)SpringUtil.getBean("contactsDaoForDML");
				contactsDaoForDML.deleteByListIds(delIdStr);
				mailingListDaoForDML.delete(delIdStr);
				
				if(userActivitiesDaoForDML != null && currentUserId != null) {
					String delIdArr[] = delIdStr.split(",");
					for(int i =0;i<delIdArr.length;i++) {
						delIdArr[i] = listNameMap.get(delIdArr[i]); 
					}
					//userActivitiesDao.addToActivityList(ActivityEnum.CONTS_DEL_ML_p1mlName, GetUser.getUserObj(), Arrays.toString(delIdArr));
					userActivitiesDaoForDML.addToActivityList(ActivityEnum.CONTS_DEL_ML_p1mlName, GetUser.getLoginUserObj(), Arrays.toString(delIdArr));
					
				}
				
			} //if
			
			if(notDelList.length()>0){
				MessageUtil.setMessage("Some lists are deleted. " +
						"[ " + notDelList + " ] lists are configured to one or more emails and are not deleted."
						, "color:maroon;", "TOP");
			}else{
				MessageUtil.setMessage("Selected list(s) deleted successfully.", "color:blue;", "TOP");
			}
			
			
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}
		
	}
	
	
	
	
	
	public void viewList(Listitem li) {
		MailingList mailingList = (MailingList)li.getValue();
		MessageUtil.clearMessage();
		Sessions.getCurrent().setAttribute("mailingList", mailingList);
		//xcontents.setSrc("contact/contacts.zul");
		Redirect.goTo(PageListEnum.CONTACT_CONTACT_VIEW);
	}

	private boolean updateCFandSayIsNew(String cfName, String dataType, String defValue, MLCustomFields tempCfObj) {
		boolean flag=false;
		try {
			
			if(!cfName.trim().equalsIgnoreCase( (tempCfObj.getCustFieldName()==null? "": tempCfObj.getCustFieldName()) )) {
				flag=true; 
				tempCfObj.setCustFieldName(cfName);
			}
			
			if(!dataType.trim().equalsIgnoreCase( (tempCfObj.getDataType()==null? "": tempCfObj.getDataType()) )) {
				flag=true; 
				tempCfObj.setDataType(dataType);
			}
			
			if(!defValue.trim().equalsIgnoreCase( (tempCfObj.getDefaultValue()==null? "": tempCfObj.getDefaultValue()) )) {
				flag=true;
				tempCfObj.setDefaultValue(defValue);
			}
			
			if(tempCfObj.getFieldIndex() <= 0) {
				flag=true;
				configuredCfMaxFieldIndex++;
				tempCfObj.setFieldIndex(configuredCfMaxFieldIndex);
				tempCfObj.setSelectedField("Custom Field" + configuredCfMaxFieldIndex);
			}
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
			return false;
		}
		return flag;
	} // updateCFandSayIsNew
	
	
	public void updateList(String name, String desc) {
		
		try{
			MessageUtil.clearMessage();
			
			if(name==null || name.trim().equals("")) {
				MessageUtil.setMessage("Mailing list name cannot be left empty.", "color:red", "TOP");
				return;
			}
			
			boolean isMlModified=false;
			
			MailingList mlInSession = (MailingList)Sessions.getCurrent().getAttribute("mailingList");
			MailingListDao mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
			MailingListDaoForDML mailingListDaoForDML=(MailingListDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.MAILINGLIST_DAO_FOR_DML);
			MailingList ml = mailingListDao.findById(mlInSession.getListId());
			
			String oldName = ml.getListName();
			
			if(!ml.getListName().trim().equals(name.trim())) {
				isMlModified=true;
				ml.setListName(name);
			}
			
			String mlDesc = ml.getDescription() == null ? "":ml.getDescription();
			if(!mlDesc.trim().equals(desc.trim())) {
				isMlModified=true;
				ml.setDescription(desc);
			}

			
			
			logger.debug(">>>>>>>>>>>>>>>>>>  CF Update <<<<<<<<<<<<<<<<");
			
			List<MLCustomFields> cfmodifiedList= new ArrayList<MLCustomFields>();

			Rows tempRows = custFieldsGbId.getRows();
			List<Component> lst = tempRows.getChildren();

			String cfNameStr;
			String dataTypeStr;
			String defValueStr;
			
			// Validate The Custom fields data
			Vector<String> tempVec = new Vector<String>();
			
			for (Component rowComp : lst) {
				Row row = (Row)rowComp;
				if(!row.isVisible()) continue;
				
				List<Component> comps = row.getChildren();

				cfNameStr = ((Textbox)comps.get(0)).getValue().trim();
				dataTypeStr = ((Listbox)comps.get(1)).getSelectedItem().getLabel();
				defValueStr = ((Textbox)comps.get(2)).getValue();
				
				if(cfNameStr.equals("")) {
					MessageUtil.setMessage("Custom field name cannot be left empty.", "color:red", "TOP");
					return;
				}
				else if(tempVec.contains(cfNameStr.toUpperCase())){
					MessageUtil.setMessage("Custom field name should not be repeated :"+cfNameStr, "color:red", "TOP");
					return;
				}
				else {
					tempVec.add(cfNameStr.toUpperCase());
					
					if(!CustomFieldValidator.validate(defValueStr,dataTypeStr)) {
						MessageUtil.setMessage("Invalid default data for the custom field :" + cfNameStr, "color:red","TOP");
						return;
					}
				}
				
			} // for
			
			// Set the Custom fields data
			MLCustomFields tempCfObj;
			
			for (Component rowComp : lst) {
				Row row =(Row)rowComp;
			
				if(!row.isVisible()) continue;
				
				List<Component> comps = row.getChildren();
				
				//logger.debug(" TB VAl="+ ((Textbox)comps.get(0)).getValue() +"  "+row.isVisible());
				if(!row.isVisible()) continue;
				
				tempCfObj = (MLCustomFields)((Image)comps.get(3)).getAttribute("mlcfObj");
				
				boolean retFlag = updateCFandSayIsNew( ((Textbox)comps.get(0)).getValue(), 
									((Listbox)comps.get(1)).getSelectedItem().getLabel(), 
									((Textbox)comps.get(2)).getValue(), tempCfObj );
				
				if(retFlag) {
					cfmodifiedList.add(tempCfObj);
				}
			} // for row
			
			//logger.info("Modified List="+cfmodifiedList);
			MLCustomFieldsDao mlCustomFieldsDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
			MLCustomFieldsDaoForDML mlCustomFieldsDaoForDML = (MLCustomFieldsDaoForDML)SpringUtil.getBean("mlCustomFieldsDaoForDML");

			//Modify the Custom fields info
			if(cfmodifiedList.size()>0) {
				//mlCustomFieldsDao.saveByCollection(cfmodifiedList);
				mlCustomFieldsDaoForDML.saveByCollection(cfmodifiedList);

			}

			//*********************** Set the Mailing list custom field Flag ********************
			int mlCfCount = mlCustomFieldsDao.findAllByList(ml).size();
			
			if(mlCfCount>0 && ml.isCustField()==false) {
				isMlModified=true;
				ml.setCustField(true);
			}
			else if(mlCfCount==0 && ml.isCustField()==true) {
				isMlModified=true;
				ml.setCustField(false);
			}
			
			
			// Modify if Mailing list object if modified
			if(isMlModified) {
				ml.setLastModifiedDate(MyCalendar.getNewCalendar());
				mailingListDaoForDML.saveOrUpdate(ml);
			}
			//*******************************************************
			
			
			if(cfmodifiedList.size()>0 || isMlModified) {
				MessageUtil.setMessage("List settings edited successfully.", "color:blue", "TOP");
			}

			/*if(userActivitiesDao != null && currentUserId != null) {
				userActivitiesDao.addToActivityList(ActivityEnum.CONTS_EDIT_ML_p1mlName, GetUser.getUserObj(), ml.getListName());
			}
			*/
			if(userActivitiesDaoForDML != null && currentUserId != null) {
				userActivitiesDaoForDML.addToActivityList(ActivityEnum.CONTS_EDIT_ML_p1mlName, GetUser.getLoginUserObj(), ml.getListName());
			}
			
			logger.debug(" - contact list '" + oldName + "' settings are edited ");
		//	Include xcontents = Utility.getXcontents();
		//	xcontents.invalidate();
		}catch (Exception e) {
			logger.error(" - ** Exception : Problem in updating the list settings --" + e + " **");
			MessageUtil.setMessage("Problem encountered while updating list settings.", "color:red", "TOP");
		}
	}
	
	
	
	public List<MLCustomFields> getCustomFields() {
		
		MailingList ml = (MailingList)Sessions.getCurrent().getAttribute("mailingList");
		MLCustomFieldsDao mlCustomFieldsDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
		return mlCustomFieldsDao.findAllByList(ml);
	}
	
	
	/**
	 * Add Custom Field Rule
	 */
	public void addCFEntryRule(List<MLCustomFields> mlcfLst) {
		try {

			MailingList ml = (MailingList)Sessions.getCurrent().getAttribute("mailingList");
			
			if(mlcfLst == null ) {

				Row row = new Row();
				MLCustomFields mlCustomFields = new MLCustomFields("","String",ml,"");
				addRule(row, mlCustomFields);
				row.setParent(custFieldsGbId.getRows());
			}
			else {
				for (MLCustomFields mlCustomFields : mlcfLst) {
					Row row = new Row();
					addRule(row, mlCustomFields);
					row.setParent(custFieldsGbId.getRows());
				} // for
			}
			
			custFieldsGbId.invalidate();
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
			logger.error(" - ** Exception : Problem adding the Custom Field entry rule--" + e + " **");
		}
	}
	
	private DeleteEventListener delEventListener = new DeleteEventListener();
	
	/**
	 * 
	 * @param row
	 * @param mlcf
	 */
	public void addRule(Row row, MLCustomFields mlcf) {
		try {
			Image img;

			Textbox tb = new Textbox(mlcf.getCustFieldName());
				tb.setInplace(true);
				tb.setWidth("95%");
			tb.setParent(row);
			
			Listbox lb = new Listbox();
				lb.setMold("select");
				lb.setWidth("95%");
				lb.appendItem("String", "String");
				lb.appendItem("Date(DD/MM/YYYY)", "Date");
				lb.appendItem("Number", "Number");
				lb.appendItem("Double", "Double");
				lb.appendItem("Boolean", "Boolean");
			lb.setParent(row);
			
			//TODO need to write perfect code
			String tempVal = mlcf.getDataType();
			//logger.info("DATATYPE="+tempVal);

			for (int i = 0; i < lb.getItemCount(); i++) {
				if(tempVal.equals(lb.getItemAtIndex(i).getValue())) {
					lb.setSelectedIndex(i);
					break;
				}
			} // for
			

			tb = new Textbox(mlcf.getDefaultValue());
				tb.setInplace(true);
				tb.setWidth("95%");
			tb.setParent(row);

			img = new Image("/img/icons/delete_icon.png");
			img.setTooltiptext("Delete");
			img.setAttribute("mlcfObj", mlcf);
			img.setStyle("cursor:pointer;");
			img.addEventListener("onClick", delEventListener);
			img.setParent(row);
		
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}
	
	/**
	 * 
	 * @param selStr
	 * @param mlistPaging
	 */
	public void changeRows(String selStr, Paging mlistPaging) throws Exception {
		try {
			if(mlistPaging!=null){
				int pNo = Integer.parseInt(selStr);
				mlistPaging.setPageSize(pNo);
				mlistPaging1.setPageSize(pNo);
				redraw(0,pNo);
			}
		} catch (WrongValueException e) {
			logger.error("Exception while getting the contacts...",e);
			
		} catch (NumberFormatException e) {
			logger.error("Exception while gettinf the contacts...",e);
		}
	}

	public void checkListsPurgingStatus() throws Exception {
		
		if(this.completedVec.size()>0) {
			logger.debug("******* Timer is started ********");
		    logger.debug(" No of mailing lists completed purging :" + this.completedVec);
		}

	    ListIterator lit = this.completedVec.listIterator();

	    while (lit.hasNext()) {
	    	
	    	List list = (List)lit.next();
	    	this.logger.debug(" List :" + list);

	    	try{
	    		int unpurged = ((Integer)list.get(1)).intValue();
	    		String divId = "htmlDiv" + list.get(0);
	    		Div htmlDiv = (Div)this.divHt.get(divId);

	    		if (htmlDiv == null) {
	    			this.logger.debug(" Divisions in the controlloer map :" + this.divHt);
	    			this.logger.warn(">>>>>>>>>>>" + divId + " div is null");
	    			lit.remove();
	    			return;
	    		}
	    		//int total = ((Long)htmlDiv.getAttribute("totalCount")).intValue();
	    		MailingList mailingList = (MailingList)htmlDiv.getAttribute("mailinglist");
	    		long totalCount = mailingListDao.findTotOnlyEmail(mailingList);
	    		
	    		int total = (int)totalCount;
	    		this.logger.debug(" Div Id :" + divId + ", total:" + total + ", unpurged :" + unpurged);

	    		Html html = new Html();
	    		Components.removeAllChildren(htmlDiv);
	    		html.setParent(htmlDiv);
	    		//html.setContent(prepareInnerHTMLContent(total, unpurged));
	    		html.setContent(prepareInnerHTMLContent(total, 0));
	    		htmlDiv.setTooltiptext("Purging is completed");
	    		htmlDiv.invalidate();
	    		this.divHt.remove(divId);
	    		lit.remove();
	    	}
	    	catch (Exception e) {
	    		logger.debug("** Exceptione : while updating the div's gif", e);
	    	}
	    }
	} // checkListsPurgingStatus
	
//	@Override
	public void update(Observable arg0, Object arg1) {
		
		try {
			logger.debug(" searching for key in the key set " + "htmlDiv" + ((List)arg1).get(0));
			
		    logger.debug("***** Map after notification :" + this.divHt);
		      
		    String keyStr = "htmlDiv" + ((List)arg1).get(0);
		      
		    if(this.divHt.containsKey(keyStr)) {
		      completedVec.add((List)arg1);
		      logger.debug(" ***** ADDED to the List :" + this.completedVec);
		    }
			
		} catch (Exception e) {
			logger.error("** Exception", e);
		}
	} // update

	
	//******************** Event Listener ****************************
	
	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
		if(event.getTarget() instanceof Paging) {
			
			Paging mlistPaging = (Paging) event.getTarget();
			int desiredPage = mlistPaging.getActivePage();
			
			if(mlistPaging.getAttribute("onPaging").equals("topPaging")) {
				
				this.mlistPaging1.setActivePage(desiredPage);
				
			}else if(mlistPaging.getAttribute("onPaging").equals("bottomPaging")) {
				
				this.mlistPaging.setActivePage(desiredPage);
				
			}//else if
			
			
			
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			redraw(ofs, (byte) pagingEvent.getPageable().getPageSize());
		}
		else if(event.getTarget() instanceof Label) {
			
			Label lb = (Label)event.getTarget();
			MailingList mailingList = (MailingList)( (Listcell) lb.getParent() ).getValue();
			MessageUtil.clearMessage();
			Sessions.getCurrent().setAttribute("mailingList", mailingList);
			//xcontents.setSrc("contact/contacts.zul");
			session.setAttribute("viewType", null);
			Redirect.goTo(PageListEnum.CONTACT_CONTACT_VIEW);
		}
		else if(event.getTarget() instanceof Image) {
			
			Image img = (Image)event.getTarget();
			String imageEventName = img.getAttribute("imageEventName").toString();
			logger.debug("Event Type is ::"+imageEventName);
			if(imageEventName.equals("addContacts")) {
				addContacts(img);
			}
			else if(imageEventName.equals("editList")) {
				editList(img);
			} 
			else if(imageEventName.equals("copyList")) {
				//copyList(img);
			    
			           MailingList mlist = (MailingList)((Listitem)img.getParent().getParent().getParent()).getValue();
			           
			           if( mlist.getUsers().getUserId().longValue() != GetUser.getUserObj().getUserId().longValue()) {
							
							MessageUtil.setMessage("The selected list has been shared by others.You cannot make a copy of shared list", "color:red;");
							return;
						}
			           
			           
			           
			           //logger.info("mlist is====>"+mlist);
			           copyListPopupId.setAttribute("mailingList",mlist);
			           copyListNameTbId.setValue("Copy of " + mlist.getListName());
			            //Messagebox.show(campaign.getCampaignName());
			       
				
				copyListPopupId.open(img);
			} 
			else if(imageEventName.equals("deleteSelected")) {
				deleteSelected(contactsListLBId,img);
			} 
			else if(imageEventName.equals("export")){
				
				
				exportSelected(img);
			}
			else if(imageEventName.equals("bulkAddition")){
			/*Listitem li = contactsListLBId.getSelectedItem();
			MailingList mailingList = (MailingList) li.getValue();
				logger.info("Mailing list is ------->"+mailingList);
				addBulkContacts(mailingList);*/
				Listitem li = (Listitem)img.getParent().getParent().getParent();
				MailingList mlist = (MailingList) li.getValue();
				addBulkContacts(mlist);
				
				
			}
		}
		else if(event.getTarget() instanceof Div) {
            
			Div htmlDiv = (Div)event.getTarget();
			try {
	              Long listId = (Long)htmlDiv.getAttribute("listId");
	              /*if (purgeList.contains(listId)) {
	                MessageUtil.setMessage("List is already in queue for purging.", 
	                  "color:blue", "TOP");
	                return;
	              }*/

	              if ((htmlDiv.getTooltiptext().equalsIgnoreCase("Purging is completed")) || 
	                (htmlDiv.getTooltiptext().equalsIgnoreCase("Purging in progress"))) {
	                MessageUtil.setMessage("Purging is completed for this list.", "color:blue", "TOP");
	                return;
	              }

	              int confirm = Messagebox.show("Are you sure you want to purge the list?", 
	                "Confirm", 3, "z-msgbox z-msgbox-question");

	              if (confirm != 1) {
	                return;
	              }

	          	 //purgeList.addObserverToQue(currentUserId, this);
	             
	          	 logger.debug(" not even running, so adding to the queue ");
	            // purgeList.add(listId);

	           //   logger.debug(" ***** is Purge Thread running :" + purgeList.isRunning());
	             /* if (!(purgeList.isRunning())) {
	                Thread thread = new Thread(purgeList);
	                thread.start();
	              }*/
	              MessageUtil.setMessage("List will be purged in a moment.", 
	                "color:blue", "TOP");
	              Components.removeAllChildren(htmlDiv);
	              Image img = new Image("/img/progress.gif");
	              img.setParent(htmlDiv);
	              htmlDiv.setTooltiptext("purging in progress");
	              htmlDiv.invalidate();

	              divHt.put(htmlDiv.getId(), htmlDiv);
	              logger.debug("******* " + htmlDiv.getId() + " after adding to the map :" + divHt);
	            }
	            catch (Exception e) {
	              logger.error("** Exception : when clicked on mailing list to purge", e);
	            }
		}

	}
	
	public void gotoUploadContacts() throws Exception {
		Redirect.goTo(PageListEnum.CONTACT_UPLOAD);
	}
	
	private void addBulkContacts(MailingList mailingList){
		Set<MailingList> mailingLists = new HashSet<MailingList>();
		mailingLists.add(mailingList);
		
		session.removeAttribute("uploadFile_Ml");
		session.setAttribute("uploadFile_Ml",mailingLists);
		PageUtil.setFromPage("/contact/myLists.zul");
		Redirect.goTo(PageListEnum.CONTACT_UPLOAD_CSV_SETTINGS);
	}
	
}

/**
 * 
 * @author 
 *
 */ 
class DeleteEventListener implements EventListener {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
//	super.
	public void onEvent(Event evt) throws Exception {
		Object obj = evt.getTarget();
		try {
			if(obj instanceof Image) {
				Image img = (Image)obj;
				MLCustomFields mlcfObj = (MLCustomFields)img.getAttribute("mlcfObj");
				
				if(mlcfObj.getFieldIndex()<=0) { // If it is the new Row
					img.getParent().setVisible(false);
					return;
				}
				
				if (Messagebox.show("This will delete all the data in the custom field column: "+ mlcfObj.getCustFieldName() +" permanently."+" \n Do you want to continue?", 
	        		  "Prompt", Messagebox.YES|Messagebox.NO, Messagebox.QUESTION) == Messagebox.NO) {
					return;
				}
	          

	        MLCustomFieldsDao mlCustomFieldsDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
			mlCustomFieldsDao.removeCustomFieldAndClearTheData(mlcfObj);
			
			
			//*********************** Set the Mailing list custom field Flag ********************
			
			MailingList ml = (MailingList)Sessions.getCurrent().getAttribute("mailingList");
			int mlCfCount = mlCustomFieldsDao.findAllByList(ml).size();
			
			boolean isMlModified=false;
			if(mlCfCount>0 && ml.isCustField()==false) {
				isMlModified=true;
				ml.setCustField(true);
			}
			else if(mlCfCount==0 && ml.isCustField()==true) {
				isMlModified=true;
				ml.setCustField(false);
			}
			
			// Modify if Mailing list object if modified
			if(isMlModified) {
				MailingListDaoForDML mailingListDao = (MailingListDaoForDML)SpringUtil.getBean("mailingListDaoForDML");
				ml.setLastModifiedDate(MyCalendar.getNewCalendar());
				mailingListDao.saveOrUpdate(ml);
			}
			//************************
			
			img.getParent().setVisible(false);
			Messagebox.show("List custom field deleted successfully.", "Information", Messagebox.OK, Messagebox.INFORMATION);
		}
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}

}
