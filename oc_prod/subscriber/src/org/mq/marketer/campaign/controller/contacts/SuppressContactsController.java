package org.mq.marketer.campaign.controller.contacts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.clapper.util.io.FileUtil;
import org.json.JSONObject;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.SuppressedContacts;
import org.mq.marketer.campaign.beans.Unsubscribes;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.contacts.SuppressPhoneNumbersController.MyEventListener;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.SuppressedContactsDao;
import org.mq.marketer.campaign.dao.SuppressedContactsDaoForDML;
import org.mq.marketer.campaign.dao.UnsubscribesDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.dao.DataIntegrityViolationException;
import org.zkoss.util.media.Media;
import org.zkoss.zhtml.Table;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

public class SuppressContactsController extends GenericForwardComposer {
	
	Grid suppContGridId;
	private Rows suppContRowsId;
	private Users user;
	A viewSuppContsBtId;
	Button closeBtnId;
	Textbox suppLstSearchTbId, selectedFileTbId;
	Button delAllBtnId;
	Button delBtnId,exportBtnId;
	private Div manualAdditionWinId$suppressResDivId, suppressDivId,displayBottomMsgDivId;
	private Div displayMeaningOfDeleteImageDivId;
	Listbox supptypeLbId, pageSizeLbId,exportFilterLbId;
	Paging suppContsPgId;
	Bandbox actionsBandBoxId;
	private Window askForManualAdditionWinId;
	private Window manualAdditionWinId;
	private Radiogroup manualAdditionWinId$manualAdditionChoice;
	private Radio manualAdditionWinId$rdoBtn1;
	private Radio manualAdditionWinId$rdoBtn2;
	private Div manualAdditionWinId$bulkAdditionDivId1;
	private Div manualAdditionWinId$bulkAdditionDivId2;
	private Div manualAdditionWinId$suppContDivId;
	private Div manualAdditionWinId$successMsgDivId;
	private Div manualAdditionWinId$singleEmailIdDivId;
	private Div manualAdditionWinId$bulkEmailIdDivId;
	private Textbox manualAdditionWinId$singleUserEmailIdTbId;
	private Textbox manualAdditionWinId$selectedFileTbId;
	private Button manualAdditionWinId$addSingleEmailIdBtnId;
	private Button manualAdditionWinId$uploadBtnId;
	private Button manualAdditionWinId$uploadId;
	private Label manualAdditionWinId$errorMsgLblId;
	private Label manualAdditionWinId$successMsgLblId;
	private Label displayBottomMsgLblId;
	private final String LABEL_FOR_MANUALLY_ADDED = "Manually Added";
	private final String LABEL_FOR_BOUNCED = "Bounced";
	private final String LABEL_FOR_SPAMMED = "Reported Spam";
	private final String SELECTED_ITEM_OF_SUPPTYPE_LB_AS_ALL = "all";
	private final String LABEL_FOR_UNSUBSCRIBED = "Unsubscribed";
	private final String LABEL_FOR_NO_REASON = "--";
	private Table exportAndPaginationTableId;
	private final String MANUALLY_ADDED = "Manually Added";
	private boolean headerCheckBoxPreviouslyChkd;
	int totContacts;
	Media gMedia = null;
	String userName = null;
	String password = null;
	
	SuppressedContactsDao suppressedContactsDao;
	SuppressedContactsDaoForDML suppressedContactsDaoForDML;
	UnsubscribesDao unsubscribesDao;
	ContactsDao contactsDao;
	ContactsDaoForDML contactsDaoForDML;
	private Users currentUser;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		try {
			super.doAfterCompose(comp);
			
			suppContRowsId.setZclass("height:1px");
			logger.info("------------------------------------------"+suppContRowsId.getZclass());
			
			suppressedContactsDao = (SuppressedContactsDao)SpringUtil.getBean("suppressedContactsDao");
			suppressedContactsDaoForDML = (SuppressedContactsDaoForDML)SpringUtil.getBean("suppressedContactsDaoForDML");
			unsubscribesDao = (UnsubscribesDao)SpringUtil.getBean("unsubscribesDao");
			contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
			contactsDaoForDML = (ContactsDaoForDML)SpringUtil.getBean("contactsDaoForDML");
			userName =  PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_MULTIMAIL_USER_ID);
			password = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_MULTIMAIL_USER_PWD);
			suppContsPgId.setDetailed(true);
			
			suppContsPgId.addEventListener("onPaging", new MyEventListener());
				
			//suppLstSearchTbId.setVisible(true);
			supptypeLbId.setVisible(true);

			int size = getCount();
			suppContsPgId.setTotalSize(size);
			int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
			suppContsPgId.setPageSize(pageSize);
			suppContsPgId.setActivePage(0);
			
			if(supptypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(Constants.CS_STATUS_UNSUBSCRIBED)) {
				List<Unsubscribes> unsubList = getUnsubscribedList();
				redrawUnsubList(unsubList,suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize(), false);
			}
			else if(supptypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("all")){
				
				drawGridRecordsForSuppressionOptionAll();
			}
			else {
				List<SuppressedContacts> suppList = getSuppContactsList(suppContsPgId.getActivePage()*suppContsPgId.getPageSize(),suppContsPgId.getPageSize());
				redrawSuppList(suppList,suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize(),false);
			}
			
			String suppTypeStr = (String)supptypeLbId.getSelectedItem().getValue();
			
			if(size > 0 && suppTypeStr.equalsIgnoreCase(Constants.SUPP_TYPE_USERADDED)){
				actionsBandBoxId.setDisabled(false);
			}
			else {
				actionsBandBoxId.setDisabled(true);
			}
			//manualAdditionWinId$suppressResDivId.setVisible(false);
			displayMeaningOfDeleteImageDivId.setVisible(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception in doaftr compose ", e);
		}
	}//doAfterCompose()
	
	public int getCount() {
		int retCount = 0;
		Long userId = GetUser.getUserId();
		String searchStr = "";
		String type = (String) supptypeLbId.getSelectedItem().getValue(); 
		
		if(type.equalsIgnoreCase(Constants.CS_STATUS_UNSUBSCRIBED)) {
			if( suppLstSearchTbId != null && !suppLstSearchTbId.getValue().equals("") && !suppLstSearchTbId.getValue().equalsIgnoreCase("Search Email Address")) {
				searchStr = suppLstSearchTbId.getValue();
			}
			retCount =  unsubscribesDao.getTotCountByUserId(userId, searchStr);
		}
		else if(type.equalsIgnoreCase("all")){
			if( suppLstSearchTbId != null && !suppLstSearchTbId.getValue().equals("") && !suppLstSearchTbId.getValue().equalsIgnoreCase("Search Email Address")) {
				searchStr = suppLstSearchTbId.getValue();
			}
			retCount =  unsubscribesDao.getTotCountByUserId(userId, searchStr);
			retCount =  retCount + suppressedContactsDao.getTotCountByUserId(userId, type, searchStr );
			
		}
		else {
			if(  suppLstSearchTbId != null && !suppLstSearchTbId.getValue().equals("") && !suppLstSearchTbId.getValue().equalsIgnoreCase("Search Email Address")) {
				searchStr = suppLstSearchTbId.getValue();
			}
			retCount =  suppressedContactsDao.getTotCountByUserId(userId, type, searchStr );
		}
		
		return retCount;
	}//getCount()
	
	public List<Unsubscribes> getUnsubscribedList() {
		String searchStr = "";
		Long userId = GetUser.getUserId();
		
		if( suppLstSearchTbId != null && !suppLstSearchTbId.getValue().equals("") && !suppLstSearchTbId.getValue().equalsIgnoreCase("Search Email Address")) {
			searchStr = suppLstSearchTbId.getValue();
		}
		
		List<Unsubscribes> unsubCntctList =  unsubscribesDao.findAllByUsrId(userId, searchStr, 
													suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize());
		
		return unsubCntctList;
	}//getUnsubscribedList()

	public List<SuppressedContacts> getSuppContactsList(int startingRecord,int maxNumberOfRecords) {
		String type = (String)supptypeLbId.getSelectedItem().getValue(); 
		String searchStr = "";
		Long userId = GetUser.getUserId();
		
		if(  suppLstSearchTbId != null && !suppLstSearchTbId.getValue().equals("") && !suppLstSearchTbId.getValue().equalsIgnoreCase("Search Email Address")) {
			searchStr = suppLstSearchTbId.getValue();
		}
		
		List<SuppressedContacts> suppContactsList =  null;
		suppContactsList = suppressedContactsDao.findAllByUsrId(userId, type, searchStr, 
				startingRecord, maxNumberOfRecords);
		
		return suppContactsList;
	}//getSuppContactsList()
	
	private Columns suppContColsId;
	
	public void redrawUnsubList(List<Unsubscribes> tempList, int firstResult, int size, boolean selectedItemAsAll) {
		try {
			logger.info("--just entered--");
			logger.info("------------------------------------redrawUnsubList-------------------------------2");
			resetGridCols();
			//Rows rows = resetGridRows();
			Components.removeAllChildren(suppContRowsId);
			Row rowInner = null;
			TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone"); 
			/*MyCalendar.calendarToString(mailingList.getLastModifiedDate(),
					MyCalendar.FORMAT_DATETIME_STDATE, tz)
					
					MyCalendar.calendarToString(lastPurDate,MyCalendar.FORMAT_YEARTODATE)
					
					lastRefreshedlId.setValue(MyCalendar.calendarToString(contactLoyalty.getLastFechedDate(), MyCalendar.FORMAT_SCHEDULE));*/
			for (Unsubscribes unsubscribes : tempList) {
				//logger.info("----just entered for each row----");
				rowInner = new Row();
				rowInner.setAttribute("contactId", unsubscribes.getUnsubscribeId());
				//(new Checkbox()).setParent(rowInner);
				(new Label(unsubscribes.getEmailId())).setParent(rowInner);
				
				if(supptypeLbId.getSelectedItem().getValue().toString().equals("all")) (new Label("Unsubscribed")).setParent(rowInner);
				
				/*(new Label(unsubscribes.getReason())).setParent(rowInner);
				(new Label(MyCalendar.calendarToString(unsubscribes.getDate(),
						MyCalendar.FORMAT_DATETIME_STDATE,tz))).setParent(rowInner);*/
				
				
				(new Label(MyCalendar.calendarToString(unsubscribes.getDate(),
						MyCalendar.FORMAT_DATETIME_STDATE,tz))).setParent(rowInner);
				
				(new Label(unsubscribes.getReason())).setParent(rowInner);
				
				if (supptypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("all")) {
					Image img = new Image("/img/icons/delete_icon.png");
					img.setStyle("margin-right:5px;cursor:pointer;");
					img.setTooltiptext("Delete");
					img.addEventListener("onClick", new MyEventListener());
					img.setParent(rowInner);
				}
				rowInner.setParent(suppContRowsId);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception");
		}
	}//redrawUnsubList()
	
	public void redrawSuppList(List<SuppressedContacts> tempList, int firstResult, int size, boolean selectedItemAsAll) {
		try {
			logger.info("--just entered--");
			resetGridCols();
			//Rows rows = resetGridRows();
			if(!selectedItemAsAll) Components.removeAllChildren(suppContRowsId);
			Row rowInner = null;
			String type = supptypeLbId.getSelectedItem().getValue();
			
			TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone"); 
			
			if(type.equalsIgnoreCase(Constants.CS_STATUS_SPAMMED) || type.equalsIgnoreCase(Constants.SUPP_TYPE_USERADDED)) {
				
				for (SuppressedContacts suppressedContacts : tempList) {
					//logger.info("----just entered for each row----");
					rowInner = new Row();
					rowInner.setAttribute("contactId", suppressedContacts.getId());
					rowInner.setAttribute("type", suppressedContacts.getType());
					rowInner.setAttribute("emailId", suppressedContacts.getEmail());
					rowInner.setValue(suppressedContacts);
					
					/*if(type.equalsIgnoreCase(Constants.SUPP_TYPE_USERADDED)) (new Checkbox()).setParent(rowInner);*/
					if(type.equalsIgnoreCase(Constants.SUPP_TYPE_USERADDED)) {
						Checkbox chkbox = new Checkbox();
						chkbox.addEventListener("onCheck", new MyEventListener());
						chkbox.setParent(rowInner);
					}
					
					(new Label(suppressedContacts.getEmail())).setParent(rowInner);
					//(new Label(suppressedContacts.getReason())).setParent(rowInner);
					(new Label(MyCalendar.calendarToString(suppressedContacts.getSuppressedtime(),
							MyCalendar.FORMAT_DATETIME_STDATE,tz))).setParent(rowInner);
					if (!type.equalsIgnoreCase(Constants.CS_STATUS_SPAMMED) ){
						Image img = new Image("/img/icons/delete_icon.png");
						img.setStyle("margin-right:5px;cursor:pointer;");
						img.setTooltiptext("Delete");
						img.addEventListener("onClick", new MyEventListener());
						img.setParent(rowInner);
					}
					rowInner.setParent(suppContRowsId);
				}
			}
			else if(type.equalsIgnoreCase("all")){
				
				for (SuppressedContacts suppressedContacts : tempList) {
					//logger.info("----just entered for each row----");
					rowInner = new Row();
					rowInner.setAttribute("contactId", suppressedContacts.getId());
					rowInner.setAttribute("type", suppressedContacts.getType());
					rowInner.setAttribute("emailId", suppressedContacts.getEmail());
					rowInner.setValue(suppressedContacts);
					//(new Checkbox()).setParent(rowInner);
					(new Label(suppressedContacts.getEmail())).setParent(rowInner);
					//(new Label(suppressedContacts.getReason())).setParent(rowInner);
					if(suppressedContacts.getType().equalsIgnoreCase(Constants.SUPP_TYPE_USERADDED)){
						(new Label("Manually Added")).setParent(rowInner);
					}
					else if(suppressedContacts.getType().equalsIgnoreCase(Constants.SUPP_TYPE_BOUNCED)){
						
						(new Label("Bounced")).setParent(rowInner);
					}else if(suppressedContacts.getType().equalsIgnoreCase("Spammed")){
						(new Label("Reported Spam")).setParent(rowInner);
					}else if(suppressedContacts.getType().equalsIgnoreCase("Unsubscribed")){
						(new Label("Unsubscribed")).setParent(rowInner);
					}
					
					(new Label(MyCalendar.calendarToString(suppressedContacts.getSuppressedtime(),
							MyCalendar.FORMAT_DATETIME_STDATE,tz))).setParent(rowInner);
					
					(new Label(suppressedContacts.getReason())).setParent(rowInner);
					
					/*(new Label(suppressedContacts.getReason())).setParent(rowInner);
					
					(new Label(MyCalendar.calendarToString(suppressedContacts.getSuppressedtime(),
							MyCalendar.FORMAT_DATETIME_STDATE,tz))).setParent(rowInner);*/
					Image img = new Image("/img/icons/delete_icon.png");
					img.setStyle("margin-right:5px;cursor:pointer;");
					img.setTooltiptext("Delete");
					img.addEventListener("onClick", new MyEventListener());
					img.setParent(rowInner);
					rowInner.setParent(suppContRowsId);
				}
				
				
			}
			
			else {
				
				for (SuppressedContacts suppressedContacts : tempList) {
					//logger.info("----just entered for each row----");
					rowInner = new Row();
					rowInner.setAttribute("contactId", suppressedContacts.getId());
					rowInner.setAttribute("type", suppressedContacts.getType());
					rowInner.setAttribute("emailId", suppressedContacts.getEmail());
					rowInner.setValue(suppressedContacts);
					//(new Checkbox()).setParent(rowInner);
					(new Label(suppressedContacts.getEmail())).setParent(rowInner);
					/*(new Label(suppressedContacts.getReason())).setParent(rowInner);
					(new Label(MyCalendar.calendarToString(suppressedContacts.getSuppressedtime(),
							MyCalendar.FORMAT_DATETIME_STDATE,tz))).setParent(rowInner);*/
					
					(new Label(MyCalendar.calendarToString(suppressedContacts.getSuppressedtime(),
							MyCalendar.FORMAT_DATETIME_STDATE,tz))).setParent(rowInner);
					(new Label(suppressedContacts.getReason())).setParent(rowInner);
					Image img = new Image("/img/icons/delete_icon.png");
					img.setStyle("margin-right:5px;cursor:pointer;");
					img.setTooltiptext("Delete");
					img.addEventListener("onClick", new MyEventListener());
					img.setParent(rowInner);
					rowInner.setParent(suppContRowsId);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception");
		}
	}//redrawSuppList()
	
	private void resetGridCols() {
		//Columns cols = suppContGridId.getColumns();
		//suppContGridId.removeChild(suppContColsId);
		Components.removeAllChildren(suppContColsId);
		
		String type = supptypeLbId.getSelectedItem().getValue(); 

		if(type.equalsIgnoreCase(Constants.CS_STATUS_SPAMMED) || type.equalsIgnoreCase(Constants.SUPP_TYPE_USERADDED)) {
			
			/*if (type.equalsIgnoreCase(Constants.CS_STATUS_SPAMMED)) {
				suppContGridId.setWidth("43%");
			}else{
				suppContGridId.setWidth("50%");
			}*/
			//exportAndPaginationTableId.setStyle("width:43%");
			/*List<Component> componentsOfTable = exportAndPaginationTableId.getChildren();
			logger.info("componentsOfTable.size()========="+componentsOfTable.size());*/
			if(type.equalsIgnoreCase(Constants.SUPP_TYPE_USERADDED)) {
				
				Column chkboxCol = new Column();
				Checkbox chkbox = new Checkbox();
				chkbox.addEventListener("onCheck", new MyEventListener());
				chkbox.setParent(chkboxCol);
				chkboxCol.setWidth("2.6%");
				chkboxCol.setParent(suppContColsId);
				
				Column email = new Column();
				Label emlLabel =  new Label("Email Id");
				emlLabel.setStyle("margin-right:10px;");
				emlLabel.setParent(email);
				email.setWidth("59.2%");
				//email.setWidth("28%");
				email.setParent(suppContColsId);
				if(suppLstSearchTbId == null) {
					suppLstSearchTbId = new Textbox("Search Email Address");
					suppLstSearchTbId.setWidth("250px");
					suppLstSearchTbId.addEventListener("onOK", new MyEventListener());
					suppLstSearchTbId.addEventListener("onFocus", new MyEventListener());
					suppLstSearchTbId.addEventListener("onBlur", new MyEventListener());
					
				}
				suppLstSearchTbId.setParent(email);
				
				Column suppTime = new Column("Timestamp");
				suppTime.setWidth("33.2%");
				suppTime.setParent(suppContColsId);
				
				Column action = new Column("Action");
				action.setWidth("5%");//2.3.11
				action.setParent(suppContColsId);
				
				
			}else{
				Column email = new Column();
				Label emlLabel =  new Label("Email Id");
				emlLabel.setStyle("margin-right:10px;");
				emlLabel.setParent(email);
				email.setWidth("70%");
				email.setParent(suppContColsId);
				if(suppLstSearchTbId == null) {
					suppLstSearchTbId = new Textbox("Search Email Address");
					suppLstSearchTbId.setWidth("250px");
					suppLstSearchTbId.addEventListener("onOK", new MyEventListener());
					suppLstSearchTbId.addEventListener("onFocus", new MyEventListener());
					suppLstSearchTbId.addEventListener("onBlur", new MyEventListener());
					
				}
				suppLstSearchTbId.setParent(email);
				
				suppLstSearchTbId.setParent(email);
				Column suppTime = new Column("Timestamp");
				suppTime.setWidth("30%");
				suppTime.setParent(suppContColsId);
			}
			/*Column email = new Column();
			Label emlLabel =  new Label("Email Id");
			emlLabel.setStyle("margin-right:10px;");
			emlLabel.setParent(email);
			email.setWidth("60%");
			//email.setWidth("28%");
			email.setParent(suppContColsId);
			if(suppLstSearchTbId == null) {
				suppLstSearchTbId = new Textbox("Search Email Address");
				suppLstSearchTbId.setWidth("250px");
				suppLstSearchTbId.addEventListener("onOK", new MyEventListener());
				suppLstSearchTbId.addEventListener("onFocus", new MyEventListener());
				suppLstSearchTbId.addEventListener("onBlur", new MyEventListener());
				
			}
			suppLstSearchTbId.setParent(email);
			Column suppTime = new Column("Timestamp");
			suppTime.setWidth("30%");
			suppTime.setParent(suppContColsId);
			if (!type.equalsIgnoreCase(Constants.CS_STATUS_SPAMMED)) {
				Column action = new Column("Action");
				action.setWidth("10%");//2.3.11
				action.setParent(suppContColsId);
			}*/
			
		} else if(type.equalsIgnoreCase("all")){
			logger.info("------------------------------------resetGridCols-------------------------------3");
			suppContGridId.setWidth("100%");
	        Column email = new Column();
			Label emlLabel =  new Label("Email Id");
			emlLabel.setStyle("margin-right:10px;");
			emlLabel.setParent(email);
			email.setWidth("30%");
			email.setParent(suppContColsId);
			if(suppLstSearchTbId == null) {
				suppLstSearchTbId = new Textbox("Search Email Address");
				suppLstSearchTbId.setWidth("250px");
				suppLstSearchTbId.addEventListener("onOK", new MyEventListener());
				suppLstSearchTbId.addEventListener("onFocus", new MyEventListener());
				suppLstSearchTbId.addEventListener("onBlur", new MyEventListener());
			}
			
			suppLstSearchTbId.setParent(email);
			
			Column suppressionType = new Column("Suppression Type");
			suppressionType.setWidth("12%");
			suppressionType.setParent(suppContColsId);
			
			/*Column reason = new Column("Reason");
			reason.setParent(suppContColsId);
			//reason.setWidth("30%");
			
			Column suppTime = new Column("Timestamp");
			suppTime.setWidth("15%");
			suppTime.setParent(suppContColsId);*/
			
			
			Column suppTime = new Column("Timestamp");
			suppTime.setWidth("12%");
			suppTime.setParent(suppContColsId);
			
			Column reason = new Column("Reason");
			reason.setParent(suppContColsId);
			//reason.setWidth("38%");
			
			Column action = new Column("Action");
			action.setWidth("8%");
			action.setStyle("align:center;");
			action.setParent(suppContColsId);
			logger.info("------------------------------------resetGridCols-------------------------------3 ends action width set");
		}
		else {
			suppContGridId.setWidth("100%");
	        Column email = new Column();
			Label emlLabel =  new Label("Email Id");
			emlLabel.setStyle("margin-right:10px;");
			emlLabel.setParent(email);
			email.setWidth("30%");
			email.setParent(suppContColsId);
			if(suppLstSearchTbId == null) {
				suppLstSearchTbId = new Textbox("Search Email Address");
				suppLstSearchTbId.setWidth("250px");
				suppLstSearchTbId.addEventListener("onOK", new MyEventListener());
				suppLstSearchTbId.addEventListener("onFocus", new MyEventListener());
				suppLstSearchTbId.addEventListener("onBlur", new MyEventListener());
			}
			suppLstSearchTbId.setParent(email);
			
			//reason.setWidth("30%");
			Column suppTime = new Column("Timestamp");
			suppTime.setWidth("20%");
			suppTime.setParent(suppContColsId);
			
			Column reason = new Column("Reason");
			reason.setParent(suppContColsId);
			reason.setWidth("45%");
			/*Column reason = new Column("Reason");
			reason.setParent(suppContColsId);
			//reason.setWidth("30%");
			Column suppTime = new Column("Timestamp");
			suppTime.setWidth("15%");
			suppTime.setParent(suppContColsId);
			*/
			if (!type.equalsIgnoreCase(Constants.CS_STATUS_UNSUBSCRIBED)) {
				Column action = new Column("Action");
				action.setWidth("5%");
				action.setParent(suppContColsId);
			}
			else{
				reason.setWidth("50%");
			}
		}
		
		
	}//resetGridCols()
	
	/*private Rows resetGridRows() {
		
		Compo
		Rows rows = suppContGridId.getRows();

		if(rows != null) {
			suppContGridId.removeChild(rows);
		} 
		
		rows = new Rows();
		rows.setParent(suppContGridId);
		return rows;
	}//resetGridRows()
*/	
	public void onSelect$pageSizeLbId() {
		try {
			
			
			int size = getCount();
			logger.info("page select event=======================size="+size);
			suppContsPgId.setTotalSize(size);
			int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
			suppContsPgId.setPageSize(pageSize);
			suppContsPgId.setActivePage(0);
			
			String suppTypeStr = (String)supptypeLbId.getSelectedItem().getValue();
			
			if(suppTypeStr.equalsIgnoreCase(Constants.CS_STATUS_UNSUBSCRIBED)) {
				List<Unsubscribes> unsubContTypeList = null;
				unsubContTypeList = getUnsubscribedList();
				redrawUnsubList(unsubContTypeList,suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize(),false);
			}
			else if(supptypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("all")){
				drawGridRecordsForSuppressionOptionAll();
				

			}
			
			else {
				List<SuppressedContacts> suppContTypeList = null;
				suppContTypeList = getSuppContactsList(suppContsPgId.getActivePage()*suppContsPgId.getPageSize(),suppContsPgId.getPageSize());
				redrawSuppList(suppContTypeList,suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize(),false);
			}
			
		} catch (Exception e) {
			logger.error("Exception");
		} 
	}//onSelect$pageSizeLbId() 
	
	public void onSelect$supptypeLbId() {
		try {
			if(logger.isDebugEnabled()) logger.debug("-- Just entered --");
			MessageUtil.clearMessage();
			
			if(  suppLstSearchTbId != null) {
				suppLstSearchTbId.setValue("Search Email Address");
			}
			
			String suppTypeStr = (String)supptypeLbId.getSelectedItem().getValue();
			String suppTypeLabelStr = (String)supptypeLbId.getSelectedItem().getLabel();
			int size = getCount();
			int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
			suppContsPgId.setTotalSize(size);
			suppContsPgId.setPageSize(pageSize);
			suppContsPgId.setActivePage(0);
			//suppContsPgId.setDetailed(true);
			
			if(suppTypeStr.equalsIgnoreCase(Constants.CS_STATUS_UNSUBSCRIBED)) {
				List<Unsubscribes> unsubContTypeList = null;
				unsubContTypeList = getUnsubscribedList();
				redrawUnsubList(unsubContTypeList,suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize(),false);
				
				displayBottomMsgLblId.setValue("To remove an email address in '"+suppTypeLabelStr+"' category, please contact support team at support@optculture.com.");
				displayBottomMsgDivId.setVisible(true);
				displayMeaningOfDeleteImageDivId.setVisible(false);
				
				
			}else if(suppTypeStr.equalsIgnoreCase("all")){
				
				drawGridRecordsForSuppressionOptionAll();
				displayBottomMsgDivId.setVisible(false);
				displayMeaningOfDeleteImageDivId.setVisible(true);
			}
			else {
				
				List<SuppressedContacts> suppContTypeList = null;
				suppContTypeList = getSuppContactsList(suppContsPgId.getActivePage()*suppContsPgId.getPageSize(),suppContsPgId.getPageSize());
				redrawSuppList(suppContTypeList,suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize(),false);
				displayBottomMsgDivId.setVisible(false);
				displayMeaningOfDeleteImageDivId.setVisible(true);
				if(suppTypeStr.equalsIgnoreCase(Constants.CS_STATUS_SPAMMED)){
					displayBottomMsgLblId.setValue("To remove an email address in '"+suppTypeLabelStr+"' category, please contact support team at support@optculture.com.");
					displayBottomMsgDivId.setVisible(true);
					displayMeaningOfDeleteImageDivId.setVisible(false);
				}
			}
			
			
			/*if(size > 0 && suppTypeStr.equalsIgnoreCase(Constants.SUPP_TYPE_USERADDED)) {
				//actionsBandBoxId.setDisabled(false);
				actionsBandBoxId.setDisabled(false);
				actionsBandBoxId.setVisible(true);
			}
			else {
				//actionsBandBoxId.setDisabled(true);
				actionsBandBoxId.setVisible(false);
			}*/
			if(size > 0 && suppTypeStr.equalsIgnoreCase(Constants.SUPP_TYPE_USERADDED)) {
				//actionsBandBoxId.setDisabled(false);
				actionsBandBoxId.setDisabled(true);
				actionsBandBoxId.setVisible(true);
			}
			else {
				//actionsBandBoxId.setDisabled(true);
				actionsBandBoxId.setVisible(false);
			}
			
			
			
			//manualAdditionWinId$suppressResDivId.setVisible(false);
		} catch (Exception e) {
			logger.error("** Exception while displaying selected entries :",e);
		}
	}//onSelect$supptypeLbId()
	
	/*public void onOK$suppLstSearchTbId() {
		try {
			String searchStr = suppLstSearchTbId.getValue();
			logger.debug("Search string is :"+searchStr);
			int size = 0;
			String type = (String)supptypeLbId.getSelectedItem().getValue();
			size = getCount();
			
			int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
			suppContsPgId.setTotalSize(size);
			suppContsPgId.setPageSize(pageSize);
			suppContsPgId.setActivePage(0);
			
			if(type.equalsIgnoreCase(Constants.CS_STATUS_UNSUBSCRIBED)) {
				List<Unsubscribes> srchList = getUnsubscribedList();
				redrawUnsubList(srchList,suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize());
			}
			else {
				List<SuppressedContacts> srchList = getSuppContactsList();
				redrawSuppList(srchList,suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize());
			}
			
		} catch (Exception e) {
			logger.error("Exception");
			
		}
	}//onOK$suppLstSearchTbId()
*/	
	public void onClick$delBtnId() {
		try {
			MessageUtil.clearMessage();
						
			Rows rows = suppContGridId.getRows();
			if(rows.getChildren().size() == 0) {
				MessageUtil.setMessage("There is no contact to delete.","color:blue");
				return;
			}

			Iterator<Component> iterator = rows.getChildren().iterator();
			Row row;
			boolean isCheckedFlag = false;
			
			if (Messagebox.show("Are you sure you want to delete selected email addresses from suppressed list?", "Confirm Delete", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) ==  Messagebox.YES) {
				while(iterator.hasNext()) {
					try {
						row = (Row)iterator.next();
						if(!(row.getFirstChild() instanceof Checkbox)) continue;
						
						if(!row.isVisible()) {
							logger.debug("---inside not visible block---");
							continue;
						}
						
						if(((Checkbox)row.getFirstChild()).isChecked() == true) {
							isCheckedFlag = true;
							
							logger.info("calling deleteById...");
							suppressedContactsDaoForDML.deleteById(Long.parseLong(row.getAttribute("contactId").toString()));
							
							String emailId=row.getAttribute("emailId").toString();
							logger.info("email is---->"+emailId);
							
							contactsDaoForDML.updateEmailStatusByStatus("'"+emailId+"'",GetUser.getUserId(),Constants.CONT_STATUS_ACTIVE,Constants.CONT_STATUS_SUPPRESSED);
							
							row.setVisible(false);
						} // if
						
					} catch (Exception e) {
						logger.error("** Exception while looping selected entries :",e);
					} //catch
				} //while
				
				if(!(rows.getChildren().iterator().hasNext())) {
					actionsBandBoxId.setDisabled(true);
				}

				if(!isCheckedFlag) {
					MessageUtil.setMessage("No email address selected for deletion. Please " +
							"select the email address to be deleted.", "color:red", "top");
					return;
				}
				
				int size = getCount();
				int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
				suppContsPgId.setTotalSize(size);
				suppContsPgId.setPageSize(pageSize);
				
				
				actionsBandBoxId.setDisabled(true);
				List<SuppressedContacts> suppList = getSuppContactsList(suppContsPgId.getActivePage()*suppContsPgId.getPageSize(),suppContsPgId.getPageSize());
				redrawSuppList(suppList, suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize(),false);
				MessageUtil.setMessage("Selected email address removed from suppressed contacts successfully.", "green", "top");
			} //if(Messagebox)
			
		} catch (Exception e) {
			logger.error("** Exception while deleting selected entries :"+e);
		}
	}//onClick$delBtnId()
	
	public void onClick$delAllBtnId() {
		try {
			MessageUtil.clearMessage();
			Rows rows = suppContGridId.getRows();
			if(rows.getChildren().size() == 0) {
				
				MessageUtil.setMessage("There is no contact to be deleted.","color:blue");
				return;
				
			}
			
			if (Messagebox.show("Are you sure you want to delete all the email addresses from suppressed list?", "Confirm Delete", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) ==  Messagebox.YES) {
				suppressedContactsDaoForDML.deleteAllByUserId(GetUser.getUserId(),Constants.SUPP_TYPE_USERADDED);
	
				//String emailId=rows.getAttribute("emailId").toString();
				//logger.info("emailId,user.getUserId(),Constants.CONT_STATUS_ACTIVE,Constants.CONT_STATUS_SUPPRESSED"+emailId+"......"+GetUser.getUserId()+"......"+Constants.CONT_STATUS_ACTIVE+"......"+Constants.CONT_STATUS_SUPPRESSED);
				contactsDaoForDML.updateEmailStatus(GetUser.getUserId(),Constants.CONT_STATUS_ACTIVE,Constants.CONT_STATUS_SUPPRESSED);
				logger.info("Contact Status changed in contacts table...");
				
				Components.removeAllChildren(rows);
				actionsBandBoxId.setDisabled(true);
				suppContsPgId.setTotalSize(0);
				suppContsPgId.setActivePage(0);
				((Checkbox)suppContColsId.getFirstChild().getFirstChild()).setChecked(false);
				
			}
		} catch (Exception e) {
			logger.error("Exception",e);
		}
	}//onClick$delAllBtnId()
	
	public void onClick$exportBtnId(){
		logger.debug("-- just entered --");
		int count = getCount();
		int size = 1000;
		String searchStr = "";
		
		if(!suppLstSearchTbId.getValue().equals("") && !suppLstSearchTbId.getValue().equalsIgnoreCase("Search Email Address")) {
			
			searchStr = suppLstSearchTbId.getValue();
			
		}

		TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone"); 
		String type = (String)supptypeLbId.getSelectedItem().getValue();
		String fileType = exportFilterLbId.getSelectedItem().getLabel();
		String category = (String)supptypeLbId.getSelectedItem().getLabel();
		String userName = GetUser.getUserName();
		String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
		String exportDir = usersParentDirectory + "/" + userName + "/SuppressedContacts/" ;
		File downloadDir = new File(exportDir);
		
		if(downloadDir.exists()){
			try {
				FileUtils.deleteDirectory(downloadDir);
				logger.debug(downloadDir.getName() + " is deleted");
			} catch (Exception e) {
				logger.error("Exception");
				logger.warn(downloadDir.getName() + " is not deleted");
			}
		}
		if(!downloadDir.exists()){
			downloadDir.mkdirs();
		}
		
		String filePath = "";
		StringBuffer sb = null;

		logger.debug("Writing to the file : " + filePath);

		
		if(fileType.contains("csv")){
			try {
								
				filePath = exportDir +  "SuppressedContacts_" + category + "_" + System.currentTimeMillis() + ".csv";
				
				logger.debug("Download File path : " + filePath);
				File file = new File(filePath);
				BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
				//bw.write("\"Email Address\",\"Reason\" \r\n");
				sb = new StringBuffer();
				
				if(supptypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(Constants.CS_STATUS_UNSUBSCRIBED)) {
					sb.append("\"Email Address\",\"Reason\",\"Timestamp\"");
					sb.append("\r\n");
					bw.write(sb.toString());
					
					
					for(int i=0; i < count; i+=size) {
						sb.setLength(0);
						List<Unsubscribes> unsubList = unsubscribesDao.findAllByUsrId(GetUser.getUserId(), searchStr, i, size);
						if(unsubList.size() > 0) {
							for (Unsubscribes unsubscribes : unsubList) {
								sb.append("\"");sb.append(unsubscribes.getEmailId());sb.append("\",");
								sb.append("\"");sb.append(unsubscribes.getReason()==null?"":unsubscribes.getReason());sb.append("\",");
								sb.append("\"");sb.append(MyCalendar.calendarToString(unsubscribes.getDate(),
										MyCalendar.FORMAT_DATETIME_STDATE,tz));
								sb.append("\"\r\n");
							}
						}
						bw.write(sb.toString());
						unsubList = null;
						//System.gc();
					}
				}
				else if(supptypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(Constants.SUPP_TYPE_BOUNCED)){
					sb.append("\"Email Address\",\"Reason\",\"Timestamp\"");
					sb.append("\r\n");
					bw.write(sb.toString());
					
					
					for(int i=0; i < count; i+=size) {
						sb.setLength(0);
						//sb = new StringBuffer();
						List<SuppressedContacts> suppList = suppressedContactsDao.findAllByUsrId(GetUser.getUserId(), type, searchStr, i, size);
						if(suppList.size() > 0) {
							for (SuppressedContacts suppCont : suppList) {
								sb.append("\"");sb.append(suppCont.getEmail());sb.append("\",");
								sb.append("\"");sb.append(suppCont.getReason()==null?"":suppCont.getReason());sb.append("\",");
								sb.append("\"");sb.append(MyCalendar.calendarToString(suppCont.getSuppressedtime(),
										MyCalendar.FORMAT_DATETIME_STDATE,tz));
								sb.append("\"\r\n");
							}
						}
						bw.write(sb.toString());
						suppList = null;
						//System.gc();
						
					}
				}
				else if(supptypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(SELECTED_ITEM_OF_SUPPTYPE_LB_AS_ALL)){
					sb.append("\"Email Address\",\"Suppression Type\",\"Reason\",\"Timestamp\"");
					sb.append("\r\n");
					bw.write(sb.toString());
					
					
					for(int i=0; i < count; i+=size) {
						//sb = new StringBuffer();
						sb.setLength(0);
						List<Unsubscribes> unsubList = unsubscribesDao.findAllByUsrId(GetUser.getUserId(), searchStr, i, size);
						if(unsubList.size() > 0) {
							for (Unsubscribes unsubscribes : unsubList) {
								sb.append("\"");sb.append(unsubscribes.getEmailId());sb.append("\",");
								//sb.append("\"");sb.append(unsubscribes.getReason()==null?"":unsubscribes.getReason());sb.append("\",");
								sb.append("\"");sb.append(LABEL_FOR_UNSUBSCRIBED);sb.append("\",");
								sb.append("\"");sb.append((unsubscribes.getReason()==null || unsubscribes.getReason().equalsIgnoreCase("") )?LABEL_FOR_NO_REASON:unsubscribes.getReason());sb.append("\",");
								sb.append("\"");sb.append(MyCalendar.calendarToString(unsubscribes.getDate(),
										MyCalendar.FORMAT_DATETIME_STDATE,tz));
								sb.append("\"\r\n");
							}
						}
						bw.write(sb.toString());
						unsubList = null;
						//System.gc();
						
						
					}
					
					
					for (int i = 0; i < count; i+=size) {
						sb.setLength(0);
						List<SuppressedContacts> suppList = suppressedContactsDao
								.findAllByUsrId(GetUser.getUserId(), type,
										searchStr, i, size);
						if (suppList.size() > 0) {
							for (SuppressedContacts suppCont : suppList) {
								sb.append("\"");
								sb.append(suppCont.getEmail());
								sb.append("\",");
								sb.append("\"");
								if(suppCont.getType().equalsIgnoreCase(Constants.SUPP_TYPE_USERADDED)){
									sb.append(LABEL_FOR_MANUALLY_ADDED);
								}else if(suppCont.getType().equalsIgnoreCase(Constants.SUPP_TYPE_BOUNCED)){
									sb.append(LABEL_FOR_BOUNCED);
								}else if(suppCont.getType().equalsIgnoreCase(Constants.SUPP_TYPE_SPAMMED)){
									sb.append(LABEL_FOR_SPAMMED);
								}
								//sb.append(suppCont.getType().equalsIgnoreCase(Constants.SUPP_TYPE_USERADDED) ? LABEL_FOR_MANUALLY_ADDED : suppCont.getType());
								sb.append("\",");
								sb.append("\"");
								sb.append((suppCont.getReason() == null || suppCont.getReason().equalsIgnoreCase(""))? LABEL_FOR_NO_REASON
										: suppCont.getReason());
								sb.append("\",");
								sb.append("\"");
								sb.append(MyCalendar.calendarToString(
										suppCont.getSuppressedtime(),
										MyCalendar.FORMAT_DATETIME_STDATE, tz));
								sb.append("\"\r\n");
							}
						}
						bw.write(sb.toString());
						suppList = null;
						//System.gc();
					}
				}
				else {
					sb.append("\"Email Address\",\"Timestamp\"");
					sb.append("\r\n");
					bw.write(sb.toString());
					
					
					for(int i=0; i < count; i+=size) {
						sb.setLength(0);
						//sb = new StringBuffer();
						List<SuppressedContacts> suppList = suppressedContactsDao.findAllByUsrId(GetUser.getUserId(), type, searchStr, i, size);
						if(suppList.size() > 0) {
							for (SuppressedContacts suppCont : suppList) {
								sb.append("\"");sb.append(suppCont.getEmail());sb.append("\",");
								sb.append("\"");sb.append(MyCalendar.calendarToString(suppCont.getSuppressedtime(),
										MyCalendar.FORMAT_DATETIME_STDATE,tz));
								sb.append("\"\r\n");
							}
						}
						bw.write(sb.toString());
						suppList = null;
						//System.gc();
					}
				}
				bw.flush();
				bw.close();
				Filedownload.save(file, "text/plain");
			} catch (IOException e) {
				
			}
			logger.debug("-- exit --");
		}
	}//onClick$exportBtnId
	
	
	public void onUpload$uploadBtnId$manualAdditionWinId(UploadEvent event) {
		
		browse(event.getMedia());
		
	}
	
	public void browse(Media media) {
		logger.info("Browse is called");
		manualAdditionWinId$selectedFileTbId.setValue(media.getName());
		manualAdditionWinId$selectedFileTbId.setDisabled(true);
		gMedia = media;
		
	}

	public void upload(){
		
		MessageUtil.clearMessage();
		Media media = gMedia; 
		
		if(media == null) {
			//MessageUtil.setMessage("Please select a file.", "color:red", "TOP");
			manualAdditionWinId$errorMsgLblId.setValue("Please select a file.");
			return;
		}else{
			manualAdditionWinId$errorMsgLblId.setValue("");
		}
		
		logger.info("File name : " + media.getName());
		String path = PropertyUtil.getPropertyValue("usersParentDirectory").trim() + "/" + sessionScope.get("userName") + "/List/" +((Media)media).getName();
		String ext = FileUtil.getFileNameExtension(path);
		if(ext == null) {
			//MessageUtil.setMessage("Upload .csv file only.","color:red","TOP");
			manualAdditionWinId$errorMsgLblId.setValue("Only .csv file format is allowed.");
			return;
		}
		if(!ext.equalsIgnoreCase("csv")) {
			//MessageUtil.setMessage("Upload .csv file only.","color:red","TOP");
			manualAdditionWinId$errorMsgLblId.setValue("Only .csv file format is allowed.");
			return;
		}
		uploadSuppressCSVFile(media,suppressDivId,manualAdditionWinId$suppressResDivId);
		gMedia = null;
		actionsBandBoxId.setDisabled(false);
	}
	
	public void uploadSuppressCSVFile(Media media,Div suppressDivId,Div manualAdditionWinId$suppressResDivId) {
		try {
			
			if(logger.isDebugEnabled()) logger.debug("-- Just entered--");
			String successMsg = "";
			MessageUtil.clearMessage();
			Media m = (Media)media;
			String path = PropertyUtil.getPropertyValue("usersParentDirectory").trim() + "/" + sessionScope.get("userName") + "/List/" +((Media)media).getName();
			UploadCSVFileController uploadCSVFileController = new UploadCSVFileController();
			boolean isSuccess = uploadCSVFileController.copyDataFromMediaToFile(path,m);
			
			if(logger.isDebugEnabled()) logger.debug("Is copy of the file successfull :"+isSuccess);
			
			if(!isSuccess){
				if(logger.isDebugEnabled()) logger.debug("Could not copy the file from Media");
				return;
			}
			
			if(logger.isDebugEnabled()) logger.debug("File copied from media is successfull.");
			
			UploadCSVFile uploadCSVFile = (UploadCSVFile)SpringUtil.getBean("uploadCSVFile");
			
			if(logger.isDebugEnabled()) logger.debug("uploadCSVFile object : " +  uploadCSVFile);
			
			Object[] obj = {GetUser.getUserObj(),path,m.getName()};
			
			if(logger.isDebugEnabled()) logger.debug("Is uploadCSVFile thread running : " + uploadCSVFile.isRunning);
			
			synchronized(uploadCSVFile) {
				uploadCSVFile.uploadQueue.add(obj);
				if(!uploadCSVFile.isRunning){
					Thread thread = new Thread(uploadCSVFile);
					thread.start();
				}
			}
			
			if(logger.isDebugEnabled()) logger.debug("Thread Started . Exiting ...");
			currentUser =GetUser.getUserObj();
			
			//suppressDivId.setVisible(false);
			//manualAdditionWinId$selectedFileTbId.setValue("");
			//manualAdditionWinId$suppressResDivId.setVisible(true);
			    manualAdditionWinId$selectedFileTbId.setValue("");
			    manualAdditionWinId$manualAdditionChoice.setSelectedItem(null);
		        manualAdditionWinId$singleEmailIdDivId.setVisible(false);
				manualAdditionWinId$bulkEmailIdDivId.setVisible(false);
				
				//successMsg = "Email Addresses will be uploaded in a moment! You can view in the manually added section.";
				successMsg = "Email addresses will be uploaded in a moment. You can view them \nin the manually added section.";
				
		        manualAdditionWinId$successMsgLblId.setValue(successMsg);
		        manualAdditionWinId$successMsgDivId.setVisible(true);
			
	     } catch(Exception e) {
	    	 logger.error("** Exception :",(Throwable)e);
	     }
	}
	
	
	
	public void onClick$uploadId$manualAdditionWinId() {
		
		upload();
		String suppTypeStr = (String)supptypeLbId.getSelectedItem().getValue();
		int size = getCount();
		if(size > 0 && suppTypeStr.equalsIgnoreCase(Constants.SUPP_TYPE_USERADDED)) {
			actionsBandBoxId.setDisabled(false);
		}
		else {
			actionsBandBoxId.setDisabled(true);
		}
		
	}
	
	
	
	
	public class MyEventListener implements EventListener{
		
		
		public MyEventListener() {}
		
		@Override
		public void onEvent(Event event) throws Exception {

			Object target =  event.getTarget();
			
			if(target instanceof Paging) {
				
					Paging openPaging = (Paging)target;
					int desiredPage = openPaging.getActivePage();
					PagingEvent pagingEvent = (PagingEvent) event;
					int pSize = pagingEvent.getPageable().getPageSize();
					int ofs = desiredPage * pSize;
					
					String searchStr = "";
					if(!suppLstSearchTbId.getValue().equals("") && !suppLstSearchTbId.getValue().equalsIgnoreCase("Search Email Address")) {
						
						searchStr = suppLstSearchTbId.getValue();
					}
					
					if(supptypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(Constants.CS_STATUS_UNSUBSCRIBED)) {
						List<Unsubscribes> unsubList = unsubscribesDao.findAllByUsrId(GetUser.getUserId(), searchStr, ofs, pSize);
						redrawUnsubList(unsubList, ofs, pSize,false);
					}
					else if(supptypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("all")){
						
						drawGridRecordsForSuppressionOptionAll();
						
					}
					else {
					//List<SuppressedContacts> suppList = suppressedContactsDao.findAllByUsrId(GetUser.getUserId(), (String)supptypeLbId.getSelectedItem().getValue(), searchStr, ofs, pSize);
					List<SuppressedContacts> suppList = getSuppContactsList(suppContsPgId.getActivePage()*suppContsPgId.getPageSize(),suppContsPgId.getPageSize());
					redrawSuppList(suppList,ofs, pSize,false);
					}
				
				
			}
			else if(target instanceof Image) {
				if(supptypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(Constants.CS_STATUS_SPAMMED)
						|| supptypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(Constants.CS_STATUS_UNSUBSCRIBED)) {
					/*MessageUtil.setMessage("To remove an email address in Reported Spam/Unsubscribed \n" +
							" category, please contact support team at support@optculture.com.","color:blue");*/
				}
				else {
					try {
						Image img = (Image)event.getTarget();
						Row selRow = (Row) img.getParent();
						List<Component> listOfComponents = selRow.getChildren();
						Label lbl = (Label)listOfComponents.get(1);
						
						if(supptypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("all")){
							List<Component> componentsList = selRow.getChildren();
							Label lblOfSuppressionType = (Label)componentsList.get(1);
							logger.info("lblOfSuppressionType.getValue()============================="+lblOfSuppressionType.getValue());
							
							if(!(lblOfSuppressionType.getValue().equalsIgnoreCase(MANUALLY_ADDED)) && !(lblOfSuppressionType.getValue().equalsIgnoreCase("Bounced"))){
								MessageUtil.setMessage("To remove an email address in \n'"+lblOfSuppressionType.getValue()+"'  category" +
										",\n please contact support team at support@optculture.com.","color:blue");
								return;
							}
							
							
						}
						//Rows rows = suppContGridId.getRows();
						
						if (Messagebox.show("Are you sure you want to delete selected email address from suppressed list?", "Confirm Delete",
								Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) ==  Messagebox.YES) {
									
									if(supptypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(Constants.SUPP_TYPE_BOUNCED) || (lbl.getValue().equalsIgnoreCase(Constants.SUPP_TYPE_BOUNCED)  )) {
										try {
												PostMethod post = new PostMethod("https://sendgrid.com/api/bounces.delete.json");
	
												post.addParameter("api_user", userName);
												post.addParameter("api_key", password);
												post.addParameter("email", (String) selRow.getAttribute("emailId"));
	
												HttpClient httpClient = new HttpClient();
												httpClient.executeMethod(post);
										      
												String responseStr = StringEscapeUtils.unescapeHtml(post.getResponseBodyAsString());
												JSONObject responseObj = new JSONObject(responseStr);
										      
												if(responseObj.getString("message").equalsIgnoreCase("success")){
													
													suppressedContactsDaoForDML.deleteById(Long.parseLong(selRow.getAttribute("contactId").toString()));
													contactsDaoForDML.updateEmailStatusByUserId((String) selRow.getAttribute("emailId"), GetUser.getUserId(), Constants.CONT_STATUS_ACTIVE, Constants.CONT_STATUS_BOUNCED);
													
													selRow.setVisible(false);
													
													MessageUtil.setMessage("Selected email address removed from suppression list successfully.", "green", "top");
												}
												else {
													MessageUtil.setMessage("Problem occured while deleting the selected email address. ", "red", "top");
												}
										}catch(Exception e) {
											logger.error("Exception while deleting contact.");
										}
									}
									else {
										
										String emailId= (String) selRow.getAttribute("emailId");
										logger.info("email id is---->"+emailId);
										suppressedContactsDaoForDML.deleteById(Long.parseLong(selRow.getAttribute("contactId").toString()));
										
										logger.info("suppressedContactsDaoForDML"+suppressedContactsDaoForDML);
										logger.info("contactsDaoForDML"+contactsDaoForDML);
										try{
											
											
											logger.info("emailId,user.getUserId(),Constants.CONT_STATUS_ACTIVE,Constants.CONT_STATUS_SUPPRESSED"+emailId+"......"+GetUser.getUserId()+"......"+Constants.CONT_STATUS_ACTIVE+"......"+Constants.CONT_STATUS_SUPPRESSED);
										contactsDaoForDML.updateEmailStatusByStatus("'"+emailId+"'",GetUser.getUserId(),Constants.CONT_STATUS_ACTIVE,Constants.CONT_STATUS_SUPPRESSED);
										}
										catch (Exception e) {
											// TODO: handle exception
											logger.info("contactsDaoForDML........................"+contactsDaoForDML);
											logger.info("contactsDaoForDML.updateEmailStatusByStatus........",e);
										}
										logger.info("Contact Status changed in contacts table...");
										
										selRow.setVisible(false);
									}
										
									} // if
								} catch (Exception e) {
									logger.error("** Exception while looping selected entries :",e);
								} //catch
	
						int size = getCount();
						int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
						suppContsPgId.setTotalSize(size);
						suppContsPgId.setPageSize(pageSize);
						String suppTypeStr = (String)supptypeLbId.getSelectedItem().getValue();
						if(size > 0 && suppTypeStr.equalsIgnoreCase(Constants.SUPP_TYPE_USERADDED)){
							actionsBandBoxId.setDisabled(false);
						}else{
							actionsBandBoxId.setDisabled(true);
						}		
						List<SuppressedContacts> suppList = null;
						
						if(supptypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("all")){
							
							drawGridRecordsForSuppressionOptionAll();
							
						} else {
							
							suppList = getSuppContactsList(suppContsPgId.getActivePage()*suppContsPgId.getPageSize(),suppContsPgId.getPageSize());
							redrawSuppList(suppList, suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize(),false);
						}
						
						
						
						
						
						} //else
					} //else if
			else if(target instanceof Textbox) {
				if (event.getName().equalsIgnoreCase("onOK")) {
					try {
						//String searchStr = suppLstSearchTbId.getValue();
						//logger.debug("Search string is :"+searchStr);
						int size = 0;
						String type = (String)supptypeLbId.getSelectedItem().getValue();
						size = getCount();
						
						int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
						suppContsPgId.setTotalSize(size);
						suppContsPgId.setPageSize(pageSize);
						suppContsPgId.setActivePage(0);
						
						if(type.equalsIgnoreCase(Constants.CS_STATUS_UNSUBSCRIBED)) {
							List<Unsubscribes> srchList = getUnsubscribedList();
							redrawUnsubList(srchList,suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize(),false);
						}
						else if(supptypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("all")){
							
							drawGridRecordsForSuppressionOptionAll();

							
							
						}
						else {
							List<SuppressedContacts> srchList = getSuppContactsList(suppContsPgId.getActivePage()*suppContsPgId.getPageSize(),suppContsPgId.getPageSize());
							redrawSuppList(srchList,suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize(),false);
						}
						
					} catch (Exception e) {
						logger.error("Exception");
						
					}
				}
				else if(event.getName().equalsIgnoreCase("onFocus")) {
					suppLstSearchTbId.setValue(""); 
				}
				else if(event.getName().equalsIgnoreCase("onBlur")) {
					suppLstSearchTbId.setValue("Search Email Address"); 
					try {
						//String searchStr = suppLstSearchTbId.getValue();
						//logger.debug("Search string is :"+searchStr);
						int size = 0;
						String type = (String)supptypeLbId.getSelectedItem().getValue();
						size = getCount();
						
						int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
						suppContsPgId.setTotalSize(size);
						suppContsPgId.setPageSize(pageSize);
						suppContsPgId.setActivePage(0);
						
						if(type.equalsIgnoreCase(Constants.CS_STATUS_UNSUBSCRIBED)) {
							List<Unsubscribes> srchList = getUnsubscribedList();
							redrawUnsubList(srchList,suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize(),false);
						}
						else if(supptypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("all")){
							drawGridRecordsForSuppressionOptionAll();

						}
						else {
							List<SuppressedContacts> srchList = getSuppContactsList(suppContsPgId.getActivePage()*suppContsPgId.getPageSize(),suppContsPgId.getPageSize());
							redrawSuppList(srchList,suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize(),false);
						}
						
					} catch (Exception e) {
						logger.error("Exception");
						
					}
				}
			}
			else if(target instanceof Radiogroup){
				logger.info("radio event-------------------------------------------------");
				
				 if(event.getName().equalsIgnoreCase("onSelect")){
					 logger.info("radio event- occured------------------------------------------------");
				 }
						/*if (event.getName().equalsIgnoreCase("onCheck1")) {
							manualAdditionWinId$singleUserEmailIdTbId
									.setDisabled(false);
							manualAdditionWinId$addSingleEmailIdBtnId
									.setDisabled(false);
							//keeping other choices as disabled.
							manualAdditionWinId$selectedFileTbId
									.setDisabled(true);
							manualAdditionWinId$uploadBtnId.setDisabled(true);
							manualAdditionWinId$uploadId.setDisabled(true);
						} */
						/*if (event.getName().equalsIgnoreCase("onSelectRdoBtn2")) {
							manualAdditionWinId$singleUserEmailIdTbId
									.setDisabled(true);
							manualAdditionWinId$addSingleEmailIdBtnId
									.setDisabled(true);
							//keeping other choices as disabled.
							manualAdditionWinId$selectedFileTbId
									.setDisabled(false);
							manualAdditionWinId$uploadBtnId.setDisabled(false);
							manualAdditionWinId$uploadId.setDisabled(false);
						}*/
				}
			else if(target instanceof Checkbox){
				logger.info("Chkbox event happened for manually added section--------------------------------------------------------");
				Rows rows = suppContGridId.getRows();
				if(rows.getChildren().size() == 0) {
					//MessageUtil.setMessage("There is no contact to delete.","color:blue");
					return;
				}
				
				boolean displayActionsBandBox = false;
				boolean headerChkBoxChkd = ((Checkbox)suppContColsId.getFirstChild().getFirstChild()).isChecked();
				/*Iterator<Component> iterator = rows.getChildren().iterator();
				Row row;
				while(iterator.hasNext()){
					row = (Row)iterator.next();
					if (((Checkbox)suppContColsId.getFirstChild().getFirstChild()).isChecked()) {
						((Checkbox) row.getFirstChild()).setChecked(true);
					}
					else {
						((Checkbox) row.getFirstChild()).setChecked(false);
					}
				}*/
				
				Iterator<Component> iterator = rows.getChildren().iterator();
				Row row;
				while(iterator.hasNext()){
					row = (Row)iterator.next();
					
					
					if (headerChkBoxChkd) {
						((Checkbox) row.getFirstChild()).setChecked(true);
						displayActionsBandBox = true;
					}
					else {
						if(((Checkbox) row.getFirstChild()).isChecked()){
							((Checkbox) row.getFirstChild()).setChecked(true);
							displayActionsBandBox = true;
							
							if(headerCheckBoxPreviouslyChkd) {
								((Checkbox) row.getFirstChild()).setChecked(false);
								displayActionsBandBox = false;
							}
							
						}
						else{
							((Checkbox) row.getFirstChild()).setChecked(false);
						}
					}
					
					
				}//while loop ends
				
				if(headerChkBoxChkd){
					headerCheckBoxPreviouslyChkd = true;
				}else{
					headerCheckBoxPreviouslyChkd = false;
				}
				
				
				if(displayActionsBandBox){
					actionsBandBoxId.setDisabled(false);
				}else{
					actionsBandBoxId.setDisabled(true);
				}
				
				
			}
			}
		}	
	
	public void onClick$addManuallyAnchId(){
		manualAdditionWinId$singleEmailIdDivId.setVisible(true);
		manualAdditionWinId$bulkEmailIdDivId.setVisible(false);
        manualAdditionWinId$successMsgDivId.setVisible(false);	
        manualAdditionWinId$manualAdditionChoice.setSelectedItem(manualAdditionWinId$rdoBtn1);
		manualAdditionWinId$singleUserEmailIdTbId.setValue("Enter Email Address");
		manualAdditionWinId$errorMsgLblId.setValue("");
		//manualAdditionWinId$emptyTextBoxLblId.setValue("");
		//manualAdditionWinId$manualAdditionChoice.setSelectedItem(null);
		//manualAdditionWinId$emptyTextBoxLblId.setValue("");
		//manualAdditionWinId$suppressResDivId.setVisible(false);
		//manualAdditionWinId$singleUserEmailIdTbId.setDisabled(true);
		//manualAdditionWinId$addSingleEmailIdBtnId.setDisabled(true);
		//manualAdditionWinId$selectedFileTbId.setDisabled(true);
		//manualAdditionWinId$uploadBtnId.setDisabled(true);
		//manualAdditionWinId$uploadId.setDisabled(true);
		manualAdditionWinId.setPosition("center");
		manualAdditionWinId.setVisible(true);
		manualAdditionWinId.doHighlighted();
		
		
	}
	public void onFocus$singleUserEmailIdTbId$manualAdditionWinId(){
		manualAdditionWinId$singleUserEmailIdTbId.setValue("");
	}
	
	public void onCheck$rdoBtn1$manualAdditionWinId(){
		//logger.info("radio event- occured----1-------------------------------------------");
		manualAdditionWinId$singleUserEmailIdTbId.setValue("Enter Email Address");
		manualAdditionWinId$errorMsgLblId.setValue("");
		manualAdditionWinId$singleEmailIdDivId.setVisible(true);
		manualAdditionWinId$bulkEmailIdDivId.setVisible(false);
		manualAdditionWinId$successMsgDivId.setVisible(false);	
	}
    public void onCheck$rdoBtn2$manualAdditionWinId(){
    	//logger.info("radio event- occured-----2-------------------------------------------");
    	manualAdditionWinId$singleUserEmailIdTbId.setValue("Enter Email Address");
    	manualAdditionWinId$selectedFileTbId.setValue("");
    	manualAdditionWinId$errorMsgLblId.setValue("");
    	manualAdditionWinId$singleEmailIdDivId.setVisible(false);
		manualAdditionWinId$bulkEmailIdDivId.setVisible(true);
		manualAdditionWinId$successMsgDivId.setVisible(false);	
	}
    
    public void onClick$addSingleEmailIdBtnId$manualAdditionWinId(){
    	String email = manualAdditionWinId$singleUserEmailIdTbId.getValue();
        if(email.trim().isEmpty() || email.equalsIgnoreCase("Enter Email Address")){
        	//manualAdditionWinId$errorMsgLblId.setValue("Textbox is empty.");
        	manualAdditionWinId$errorMsgLblId.setValue("Please enter an email address.");
        	manualAdditionWinId$singleUserEmailIdTbId.setFocus(true);
        	return;
        }else if(!Utility.validateEmail(email.trim())){
        	//manualAdditionWinId$errorMsgLblId.setValue("Invalid Email Address.");
        	manualAdditionWinId$errorMsgLblId.setValue("Please verify email address you have entered.");
        	manualAdditionWinId$singleUserEmailIdTbId.setFocus(true);
        	return;
        }
        
        try {
			SuppressedContacts suppressedContacts = new SuppressedContacts(
					GetUser.getUserObj(), email,
					Constants.SUPPTYPE_MAP.get("SUPP_TYPE_USERADDED"));
			suppressedContacts.setSuppressedtime(Calendar.getInstance());
			suppressedContactsDaoForDML.saveOrUpdate(suppressedContacts);
			contactsDaoForDML.updateEmailStatusByStatus("'"+email+"'", GetUser.getUserId(), Constants.CONT_STATUS_SUPPRESSED, Constants.CONT_STATUS_ACTIVE);
			manualAdditionWinId$successMsgLblId.setValue("Email address has been added successfully. You can view it in the manually added section.");
			manualAdditionWinId$successMsgDivId.setVisible(true);
			logger.info("Contact Status changed in contacts table...");
			
		} catch(DataIntegrityViolationException dive)
		{
			manualAdditionWinId$errorMsgLblId.setStyle("color:red");
			manualAdditionWinId$errorMsgLblId.setValue("The given Email is already suppressed.");
			manualAdditionWinId$singleUserEmailIdTbId.setFocus(true);
			logger.info("errrrrrrrrrr......",dive);
			return;
		}
        catch (Exception e) {
			manualAdditionWinId$errorMsgLblId.setStyle("color:red");
			manualAdditionWinId$errorMsgLblId.setValue("Unable to suppress the entered email address.");
			manualAdditionWinId$singleUserEmailIdTbId.setFocus(true);
			logger.info("errrrrrrrrrr......",e);
			return;
		}
        manualAdditionWinId$errorMsgLblId.setValue("");
        manualAdditionWinId$manualAdditionChoice.setSelectedItem(null);
        manualAdditionWinId$singleEmailIdDivId.setVisible(false);
		manualAdditionWinId$bulkEmailIdDivId.setVisible(false);
		//manualAdditionWinId$singleUserEmailIdTbId.setFocus(true);
		//manualAdditionWinId$successMsgLblId.setValue("Email address has been added successfully. You can view it in the manually added section.");
        //manualAdditionWinId$successMsgDivId.setVisible(true);		
    }
	
    private void drawGridRecordsForSuppressionOptionAll(){

    	List<Unsubscribes> unsubContTypeList = null;
		List<SuppressedContacts> suppContTypeList = null;
		
		String searchStr = "";
		if( suppLstSearchTbId != null && !suppLstSearchTbId.getValue().equals("") && !suppLstSearchTbId.getValue().equalsIgnoreCase("Search Email Address")) {
			searchStr = suppLstSearchTbId.getValue();
		}
		
		int numberOfUnsubscribedRecords = unsubscribesDao.getTotCountByUserId(GetUser.getUserId(), searchStr);
		int selectedPageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		
		int numberOfPageForUnsubscribedRecords = numberOfUnsubscribedRecords / selectedPageSize;
		int numberOfExtraRecordsForUnsubscribed = numberOfUnsubscribedRecords % selectedPageSize;
		
		int selectedPageId = suppContsPgId.getActivePage();
		int startingRecord;
		int maxNumberOfRecords;
		
		
		logger.info("numberOfPageForUnsubscribedRecords = "+numberOfPageForUnsubscribedRecords+" numberOfExtraRecordsForUnsubscribed = "+numberOfExtraRecordsForUnsubscribed+" selectedPageId = "+selectedPageId);
		if(selectedPageId >= (numberOfPageForUnsubscribedRecords + 1)){
			if(numberOfExtraRecordsForUnsubscribed == 0){
				startingRecord = (selectedPageId - numberOfPageForUnsubscribedRecords)*selectedPageSize;
				maxNumberOfRecords = selectedPageSize;
			}
			else {
				startingRecord = (selectedPageId  - numberOfPageForUnsubscribedRecords)*selectedPageSize + selectedPageSize - numberOfExtraRecordsForUnsubscribed - selectedPageSize;
				if(selectedPageId == (numberOfPageForUnsubscribedRecords + 1)){
					startingRecord = selectedPageSize - numberOfExtraRecordsForUnsubscribed;
				}
				maxNumberOfRecords = selectedPageSize;
			}
			
			logger.info("selectedPageId >= (numberOfPageForUnsubscribedRecords + 1)   cond   startingRecord = "+startingRecord);
			suppContTypeList = getSuppContactsList(startingRecord,maxNumberOfRecords);
			redrawSuppList(suppContTypeList,suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize(),false);
		}
		else if(selectedPageId == numberOfPageForUnsubscribedRecords){
			
			unsubContTypeList = getUnsubscribedList();
			logger.info("unsubContTypeList.size()================="+unsubContTypeList.size());
			redrawUnsubList(unsubContTypeList,suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize(),false);
			
			startingRecord = 0;
			if(numberOfExtraRecordsForUnsubscribed == 0){
				maxNumberOfRecords = selectedPageSize;
			}
			else
			{
				maxNumberOfRecords = selectedPageSize - numberOfExtraRecordsForUnsubscribed;
			}
			
			logger.info("selectedPageId == numberOfPageForUnsubscribedRecords   cond   startingRecord = "+startingRecord);
			suppContTypeList = getSuppContactsList(startingRecord,maxNumberOfRecords);
			logger.info("suppContTypeList.size()================="+suppContTypeList.size());
			redrawSuppList(suppContTypeList,suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize(),true);
			
		}
		else{
			logger.info("unsubscribe section =========================");
			unsubContTypeList = getUnsubscribedList();
			redrawUnsubList(unsubContTypeList,suppContsPgId.getActivePage()*suppContsPgId.getPageSize(), suppContsPgId.getPageSize(),false);
			
		}
		
		
		
    }
    
    
    public void onClick$cancelBtnId$manualAdditionWinId(){
    	manualAdditionWinId.setVisible(false);
    }
	
	
}
	

