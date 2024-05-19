package org.mq.marketer.campaign.controller.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.SparkBaseLocationDetails;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MultiLineMessageBox;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.SparkBaseCardDao;
import org.mq.marketer.campaign.dao.SparkBaseLocationDetailsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.marketer.sparkbase.SparkBaseServiceAsync;
import org.mq.marketer.sparkbase.transactionWsdl.AdjustmentResponse;
import org.mq.marketer.sparkbase.transactionWsdl.ErrorMessageComponent;
import org.mq.marketer.sparkbase.transactionWsdl.InquiryResponse;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.utils.OCCSVWriter;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;


public class ViewLoyaltyCardsController extends GenericForwardComposer<Window> implements EventListener {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private MyDatebox toDateboxId, fromDateboxId;
	private Listbox searchbyLbId, pageSizeLbId, optInSourceLbId, exportFilterLbId,viewCardsReportLbId,addLbId, subLbId;
	private Textbox searchByTbId,addTbId, subTbId;
	private RetailProSalesDao retailProSalesDao ;
	private Users currentUser;
	//private Contacts contact;
	//private ContactsDao contactsDao;
	private ContactsLoyaltyDao contactsLoyaltyDao;
	private ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML;
	private EmailQueueDao emailQueueDao;
	private EmailQueueDaoForDML emailQueueDaoForDML;
	private CustomTemplatesDao customTemplatesDao;
	private MailingListDao mailingListDao; 
	private SparkBaseLocationDetailsDao sparkBaseLocationDetailsDao;
	private SparkBaseCardDao sparkBaseCardDao;
	private Paging loyaltyListBottomPagingId;
	private Label nameLblId, cardNumberLblId, cardTypeLblId, optInDateLblId, optInSourceLblId, totalRewardsRedeemedLblId, balanceRewardsLblId,
		balancePointsLblId, lastPurchaseDateLblId, lastPurchaseAmountLblId, finalBalanceRewardsLblId, finalBalancePointsLblId;
	private ContactsLoyalty contactLoyalty  = null;	
	private SparkBaseLocationDetails sparkBaseLoc;
	private Object[] rowObject;
	private Long contactId=null;
	Object responseObject = null;
	private int pageSize=0;
	//private Rows contactLoyaltyRowsId;
	private Window custExport;
	private Div custExport$chkDivId;
	private Combobox exportCbId;
	
	
	private Calendar fromDate,endDate;
	private Listitem optSource;

	private Listitem searchBy;
	private MyTemplatesDao myTemplatesDao;
	private String searchCriteria;
	private List<Map<String, Object>> storeNumberNameMapList;
	public ViewLoyaltyCardsController() {
		session = Sessions.getCurrent();
		currentUser = GetUser.getUserObj();
		String style = "font-weight:bold;font-size:15px;color:#313031;"
				+ "font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader(" Loyalty Program", "", style, true);
		//this.contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
		this.contactsLoyaltyDao = (ContactsLoyaltyDao)SpringUtil.getBean("contactsLoyaltyDao");
		contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML)SpringUtil.getBean("contactsLoyaltyDaoForDML");
		this.emailQueueDao = (EmailQueueDao)SpringUtil.getBean("emailQueueDao");
		this.emailQueueDaoForDML = (EmailQueueDaoForDML)SpringUtil.getBean("emailQueueDaoForDML");
		this.customTemplatesDao = (CustomTemplatesDao)SpringUtil.getBean("customTemplatesDao");
		this.mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		this.sparkBaseLocationDetailsDao = (SparkBaseLocationDetailsDao)SpringUtil.getBean("sparkBaseLocationDetailsDao");
		this.sparkBaseCardDao = (SparkBaseCardDao)SpringUtil.getBean("sparkBaseCardDao");
		this.retailProSalesDao = (RetailProSalesDao)SpringUtil.getBean("retailProSalesDao");
		this.myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
	}
	
	public void doAfterCompose(Window comp) throws Exception {
		
		super.doAfterCompose(comp);
		OrganizationStoresDao organizationStoresDao =	(OrganizationStoresDao) ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
		storeNumberNameMapList  = organizationStoresDao.findStoreNumberNameMapList(GetUser.getUserObj().getUserOrganization().getUserOrgId());
		setDefaultDateValues();
		
		//String fromDate = null ; //MyCalendar.calendarToString(fromDateboxId.getServerValue(), MyCalendar.FORMAT_DATETIME_STYEAR);
		/*Calendar endCal = toDateboxId.getServerValue();
		endCal.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
		endCal.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));
		endCal.set(Calendar.SECOND, Calendar.getInstance().get(Calendar.SECOND));*/
		//String toDate = null; // MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
		
		//String optInStr = optInSourceLbId.getSelectedItem().getValue();
		
		//Long totSize  = null;
		//int totalSize = 0;
		//totSize = contactsLoyaltyDao.findTotalLoyaltyOptinsByUserId(currentUser.getUserId().longValue(),optInStr, fromDate, toDate);
		
		/*totSize = contactsLoyaltyDao.findTotalLoyaltyOptinsBySearch
		if(totSize != null) {
			totalSize = totSize.intValue();
		}
		
		pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		loyaltyListBottomPagingId.setTotalSize(totalSize);
		loyaltyListBottomPagingId.setDetailed(true);
		loyaltyListBottomPagingId.setPageSize(pageSize);
		loyaltyListBottomPagingId.setActivePage(0);
		*/
		loyaltyListBottomPagingId.addEventListener("onPaging", this);
		fetchLoyaltyOptIns(0,0);
		
	}//doAfterCompose
	
	
	public void setDefaultDateValues() {
		logger.debug("Setting default values......");
		
			searchbyLbId.setSelectedIndex(0);
			searchByTbId.setValue("");
			optInSourceLbId.setSelectedIndex(0);
		
			Date frmAndTodateDef = null;
			fromDateboxId.setValue(frmAndTodateDef);
			toDateboxId.setValue(frmAndTodateDef);
			
			fromDate=null;
			endDate=null;
			optSource=optInSourceLbId.getSelectedItem();
			searchBy=searchbyLbId.getSelectedItem();
			searchCriteria=searchByTbId.getValue();
		/*if(toSetDate) {
			toDateboxId.setValue(MyCalendar.getNewCalendar());
			Calendar cal = MyCalendar.getNewCalendar();
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-1);
			fromDateboxId.setValue(cal);
		}
		*/
		logger.debug("Completed Setting default values......");
	}//setDefaultDateValues
	public void setValues()
	{
		
		Date frmAndTodateDef;
		
		if(fromDate==null)
		{
			frmAndTodateDef = null;
			fromDateboxId.setValue(frmAndTodateDef);
		}
		else
		fromDateboxId.setValue(fromDate);
		
	
	
		if(endDate==null)
		{
			 frmAndTodateDef = null;
			toDateboxId.setValue(frmAndTodateDef);
		}
		else
		toDateboxId.setValue(endDate);
		
	optInSourceLbId.setSelectedItem(optSource);
	searchbyLbId.setSelectedItem(searchBy);
	searchByTbId.setValue(searchCriteria);
	}
public Calendar getStartDate(){
		
		if(fromDateboxId.getValue() != null)
		{
		Calendar serverFromDateCal = fromDateboxId.getServerValue();
		Calendar tempClientFromCal = fromDateboxId.getClientValue();
		serverFromDateCal.set(Calendar.HOUR_OF_DAY, 
				serverFromDateCal.get(Calendar.HOUR_OF_DAY)-tempClientFromCal.get(Calendar.HOUR_OF_DAY));
		serverFromDateCal.set(Calendar.MINUTE, 
				serverFromDateCal.get(Calendar.MINUTE)-tempClientFromCal.get(Calendar.MINUTE));
		serverFromDateCal.set(Calendar.SECOND, 0);
		//String fromDate = MyCalendar.calendarToString(serverFromDateCal, MyCalendar.FORMAT_DATETIME_STYEAR);
		
		return serverFromDateCal;
		}
		else 
		{
			return null;
		}
		
	}
	
	
	
	
	public Calendar getEndDate() {
		
		if(toDateboxId.getValue() != null)
		{
		Calendar serverToDateCal = toDateboxId.getServerValue();
		
		Calendar tempClientToCal = toDateboxId.getClientValue();
		
		
		//change the time for startDate and endDate in order to consider right from the 
		// starting time of startDate to ending time of endDate
		
		serverToDateCal.set(Calendar.HOUR_OF_DAY, 
				23+serverToDateCal.get(Calendar.HOUR_OF_DAY)-tempClientToCal.get(Calendar.HOUR_OF_DAY));
		serverToDateCal.set(Calendar.MINUTE, 
				59+serverToDateCal.get(Calendar.MINUTE)-tempClientToCal.get(Calendar.MINUTE));
		serverToDateCal.set(Calendar.SECOND, 59);
		
		String endDate = MyCalendar.calendarToString(serverToDateCal, MyCalendar.FORMAT_DATETIME_STYEAR);
		
		
		return serverToDateCal;
		}
		else 
		{
			return null;
		}
		
		}
	
	public void fetchLoyaltyOptIns(int startIndex, int desiredPage) {
		
		MessageUtil.clearMessage();
		Calendar serverFromDateCal = null;
		Calendar serverToDateCal =  null;
		if(fromDateboxId.getValue() != null && toDateboxId.getValue() != null) {
			serverFromDateCal= fromDateboxId.getServerValue();
			serverToDateCal = toDateboxId.getServerValue();
		
		Calendar tempClientFromCal = fromDateboxId.getClientValue();
		Calendar tempClientToCal = toDateboxId.getClientValue();
		
		serverFromDateCal.set(Calendar.HOUR_OF_DAY, 
				serverFromDateCal.get(Calendar.HOUR_OF_DAY)-tempClientFromCal.get(Calendar.HOUR_OF_DAY));
		serverFromDateCal.set(Calendar.MINUTE, 
				serverFromDateCal.get(Calendar.MINUTE)-tempClientFromCal.get(Calendar.MINUTE));
		serverFromDateCal.set(Calendar.SECOND, 0);
		
		serverToDateCal.set(Calendar.HOUR_OF_DAY, 
				23+serverToDateCal.get(Calendar.HOUR_OF_DAY)-tempClientToCal.get(Calendar.HOUR_OF_DAY));
		serverToDateCal.set(Calendar.MINUTE, 
				59+serverToDateCal.get(Calendar.MINUTE)-tempClientToCal.get(Calendar.MINUTE));
		serverToDateCal.set(Calendar.SECOND, 59);
		
		if(serverToDateCal.compareTo(serverFromDateCal) < 0) {
			MessageUtil.setMessage("'To' date must be later than 'From' date.", "color:red",	"TOP");
			return;
		}
		}
					
		/*String fromDate = MyCalendar.calendarToString(fromDateboxId.getServerValue(), MyCalendar.FORMAT_DATETIME_STYEAR);
		
		Calendar endCal = toDateboxId.getServerValue();
		endCal.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
		endCal.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));
		endCal.set(Calendar.SECOND, Calendar.getInstance().get(Calendar.SECOND));
		
		String toDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);*/
		String optInStr = null;
		
		if(optInSourceLbId.getSelectedItem()!=null && optInSourceLbId.getSelectedIndex()>0) {
			optInStr = optInSourceLbId.getSelectedItem().getValue();
		}
		
		pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		
		String searchVal = searchByTbId.getValue().trim();
		String searchKey=null;
		
		if(searchVal.isEmpty()) {
			searchVal=null;
		}
		else if (searchbyLbId.getSelectedItem().getValue().equals("Card") || searchbyLbId.getSelectedItem().getValue().equals("mobile_phone")) {
			if(checkIfNumber(searchByTbId.getValue().trim())){
				searchKey =	searchbyLbId.getSelectedItem().getValue();
			}
			else {
				MessageUtil.setMessage("Please enter a valid entry in search by field.", "red");
				return;
			}
		}
		else if (searchbyLbId.getSelectedItem().getValue().equals("first_name") 
				|| searchbyLbId.getSelectedItem().getValue().equals("last_name")
				|| searchbyLbId.getSelectedItem().getValue().equals("email_id")) {
			
			searchKey =	searchbyLbId.getSelectedItem().getValue();
		}
		
		int totalSize = 0;
		Long totSize= contactsLoyaltyDao.findTotalLoyaltyOptinsBySearch(
					currentUser.getUserId().longValue(),optInStr, (serverFromDateCal != null ? serverFromDateCal.toString() : null),
					(serverToDateCal != null ? serverToDateCal.toString() : null), searchKey, searchVal,OCConstants.LOYALTY_SERVICE_TYPE_SB);
			if(totSize != null){
				totalSize = totSize.intValue();
			}
			loyaltyListBottomPagingId.setTotalSize(totalSize);
			loyaltyListBottomPagingId.setActivePage(desiredPage);
			loyaltyListBottomPagingId.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
		
		
		
		try {
			
			List<Listitem> items = viewCardsReportLbId.getItems();
			for (int i=viewCardsReportLbId.getItemCount();i>0;i--) {
				
				viewCardsReportLbId.removeItemAt(i-1);
				
			}
			
			List<Object[]> loyaltyList = contactsLoyaltyDao.findContactLoyaltyOptinsBySearch(
					currentUser.getUserId(),startIndex, pageSize, optInStr,
					(serverFromDateCal != null ? serverFromDateCal.toString() : null),
					(serverToDateCal != null ? serverToDateCal.toString() : null), searchKey, searchVal,OCConstants.LOYALTY_SERVICE_TYPE_SB,orderby_colName,desc_Asc);
			
			logger.info("loyalty list size is >>>>>>"+loyaltyList.size());
			
			if(loyaltyList == null || loyaltyList.size() == 0) {
				logger.info("loyalty list object is null>>>>>>>>>");
				return;
			}
			
			for(Object[] arr : loyaltyList) {
				Listitem li = new Listitem();
				li.appendChild(new Listcell(arr[0]==null?"":arr[0].toString().trim()));
				li.appendChild(new Listcell(arr[1]==null?"":arr[1].toString().trim()));
				li.appendChild(new Listcell(arr[2]==null?"":arr[2].toString().trim()));
				li.appendChild(new Listcell(arr[3]==null?"":arr[3].toString().trim()));
				String optInSource = "";
				if(arr[7] != null) {
					if(arr[7].toString().trim().equalsIgnoreCase(Constants.CONTACT_LOYALTY_TYPE_POS)) {
						optInSource = Constants.CONTACT_LOYALTY_TYPE_STORE;
					}
					else {
						optInSource = arr[7].toString().trim();
					}
				}
				li.appendChild(new Listcell(optInSource));
				li.appendChild(new Listcell(arr[4]==null?"":arr[4].toString().trim()));
				li.appendChild(new Listcell(arr[5]==null?"":arr[5].toString().trim()));

				li.setParent(viewCardsReportLbId);
				li.setValue(arr);
				//li.addEventListener("onClick", this);
				li.setStyle("cursor:pointer;");
			}
		} catch (Exception e) {
			logger.error("Exception while setting rows.....");
			logger.error("Exception ::" , e);
		}
	}//fetchLoyaltyOptIns
	
	public boolean checkIfNumber(String in) {
        try {
            Long.parseLong(in);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }// checkIfNumber
	public boolean checkIfDouble(String in) {
        try {
            Double.parseDouble(in);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }// checkIfDouble
	public void onSelect$exportFilterLbId()
	{
		setValues();
	}
	public void onSelect$pageSizeLbId() {
		try {
			setValues();	
			pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
			loyaltyListBottomPagingId.setPageSize(pageSize);
			
			fetchLoyaltyOptIns(0,0);
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		} 
	}//onSelect$pageSizeLbId() 
	
	
	
	
	//SORTING Hard coded
public String orderby_colName="cl.created_date",desc_Asc="desc"; 
    
    public void desc2ascasc2desc()
    {
    	if(desc_Asc=="desc")
			desc_Asc="asc";
		else
			desc_Asc="desc";
	
    }
	
	public void onClick$sortbyCardNumber() {
		orderby_colName = "cl.card_number";
		desc2ascasc2desc();
		fetchLoyaltyOptIns(0,0);
		
	}
	public void onClick$sortbyFirstName() {
		orderby_colName = "c.first_name";
		desc2ascasc2desc();
		fetchLoyaltyOptIns(0,0);
		
	}public void onClick$sortbyLastName() {
		orderby_colName = "c.last_name";
		desc2ascasc2desc();
		fetchLoyaltyOptIns(0,0);
		
	}
	public void onClick$sortbyEmailAddress() {
		orderby_colName = "c.email_id";
		desc2ascasc2desc();
		fetchLoyaltyOptIns(0,0);
		
	}
	public void onClick$sortbyPhoneNumber() {
		orderby_colName = "c.mobile_phone";
		desc2ascasc2desc();
		fetchLoyaltyOptIns(0,0);
		
	}
	public void onClick$sortbyAddress() {
		orderby_colName = "c.address_one";
		desc2ascasc2desc();
		fetchLoyaltyOptIns(0,0);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public void onClick$filterBtnId() {
		try {
			
			rowObject = null;
			nameLblId.setValue("");
			cardNumberLblId.setValue("");
			cardTypeLblId.setValue("");
			optInDateLblId.setValue("");
			optInSourceLblId.setValue("");
			totalRewardsRedeemedLblId.setValue("");
			balanceRewardsLblId.setValue("");
			balancePointsLblId.setValue("");
			lastPurchaseDateLblId.setValue("");
			lastPurchaseAmountLblId.setValue("");
			finalBalanceRewardsLblId.setValue("");
			finalBalancePointsLblId.setValue("");
			addTbId.setValue("");
			subTbId.setValue("");
			/*String fromDate,toDate;
			if(fromDateboxId.getValue() != null && toDateboxId.getValue() != null) {
			fromDate = MyCalendar.calendarToString(fromDateboxId.getServerValue(), MyCalendar.FORMAT_DATETIME_STYEAR);
			Calendar endCal = toDateboxId.getServerValue();
			endCal.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
			endCal.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));
			endCal.set(Calendar.SECOND, Calendar.getInstance().get(Calendar.SECOND));
			toDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
			}
			else {
				fromDate = null;
				toDate = null;
			}
			String optInStr = null;
			
			if(optInSourceLbId.getSelectedItem()!=null && optInSourceLbId.getSelectedIndex()>0) {
				optInStr = optInSourceLbId.getSelectedItem().getValue();
			}
			
			String searchVal = searchByTbId.getValue().trim();
			String searchKey=null;
			
			if(searchVal.isEmpty()) {
				searchVal=null;
			}
			else if (searchbyLbId.getSelectedItem().getValue().equals("Card") || searchbyLbId.getSelectedItem().getValue().equals("mobile_phone")){
				
				if(checkIfNumber(searchByTbId.getValue().trim())){
					searchKey =	searchbyLbId.getSelectedItem().getValue();
				}
				else {
					MessageUtil.setMessage("Please enter a valid entry in search by field.", "red");
					return;
				}
			}
			else if (searchbyLbId.getSelectedItem().getValue().equals("first_name")
					|| searchbyLbId.getSelectedItem().getValue().equals("last_name")
					|| searchbyLbId.getSelectedItem().getValue().equals("email_id")) {
				if(!checkIfNumber(searchByTbId.getValue().trim())){
					searchKey =	searchbyLbId.getSelectedItem().getValue();
				}
				else {
					MessageUtil.setMessage("Please enter a valid entry in search by field.", "red");
					return;
				}
			}
			Long totSize  = null;
			int totalSize =0;
			
			 totSize= contactsLoyaltyDao.findTotalLoyaltyOptinsBySearch(
					currentUser.getUserId().longValue(),optInStr, fromDate, toDate, searchKey, searchVal);
			if(totSize != null){
				totalSize = totSize.intValue();
			}
			loyaltyListBottomPagingId.setTotalSize(totalSize);
			loyaltyListBottomPagingId.setActivePage(0);
			loyaltyListBottomPagingId.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));*/
			fromDate=getStartDate();
			endDate=getEndDate();
			optSource=optInSourceLbId.getSelectedItem();
			searchBy=searchbyLbId.getSelectedItem();
			searchCriteria=searchByTbId.getValue();
			fetchLoyaltyOptIns(0, 0);
			
			/*if (searchByTbId.getValue().trim().length() == 0){
				MessageUtil.setMessage("Enter valid Text in searchbox.","color:red");
				 return ;
			}*/
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	}//onClick$filterBtnId()
	
	public void onClick$resetAnchId() {
		
		rowObject = null;
		nameLblId.setValue("");
		cardNumberLblId.setValue("");
		cardTypeLblId.setValue("");
		optInDateLblId.setValue("");
		optInSourceLblId.setValue("");
		totalRewardsRedeemedLblId.setValue("");
		balanceRewardsLblId.setValue("");
		balancePointsLblId.setValue("");
		lastPurchaseDateLblId.setValue("");
		lastPurchaseAmountLblId.setValue("");
		finalBalanceRewardsLblId.setValue("");
		finalBalancePointsLblId.setValue("");
		addTbId.setValue("");
		subTbId.setValue("");
		
				setDefaultDateValues();
		
		/*
		String fromDate = null; // MyCalendar.calendarToString(fromDateboxId.getServerValue(), MyCalendar.FORMAT_DATETIME_STYEAR);
		
		String toDate = null; // MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
		
		String optInStr = optInSourceLbId.getSelectedItem().getValue();
		
		Long totSize  = null;
		int totalSize =0;
		totSize = contactsLoyaltyDao.findTotalLoyaltyOptinsByUserId(currentUser.getUserId().longValue(),optInStr, fromDate, toDate);

		if(totSize != null) {
			totalSize = totSize.intValue();
		}
		
		loyaltyListBottomPagingId.setTotalSize(totalSize);
		//loyaltyListBottomPagingId.setAttribute("onPaging", "bottomPaging");
		loyaltyListBottomPagingId.setActivePage(0);
		loyaltyListBottomPagingId.addEventListener("onPaging", this);*/
		
		orderby_colName="cl.created_date";
		desc_Asc="desc";
		fetchLoyaltyOptIns(0, 0);
	}// onclick$resetAnchId
	
	
	
	@Override
	public void onEvent(Event event) throws Exception {
		super.onEvent(event);
		setValues();
		Object obj = event.getTarget();
		if(obj instanceof Paging) {
			Paging paging = (Paging)event.getTarget();
			
			int desiredPage = paging.getActivePage();
			
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			
			fetchLoyaltyOptIns(ofs, desiredPage);
		}
		
		
	}// onEvent
	
	
	String lastPurchaseDate;
	DecimalFormat f = new DecimalFormat("##.00");
	public void onSelect$viewCardsReportLbId() {
		

		Listitem selRow = viewCardsReportLbId.getSelectedItem();
		rowObject = selRow.getValue();
		
		Object[] lastPurAmt=null;
		Calendar lastPurDate=null;
		
		String card_id = rowObject[0].toString().trim();
		contactLoyalty = contactsLoyaltyDao.getContactsLoyaltyByCardId(card_id, currentUser.getUserId());
		
		SimpleDateFormat formatter = new SimpleDateFormat(MyCalendar.FORMAT_YEARTODATE);
		Date date = null;
		try {
			date = (Date)formatter.parse(rowObject[6].toString().trim());
		} catch (ParseException e) {
			logger.error("Exception ::" , e);
		} 
		
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		
		String optInDate = rowObject[6]==null?"":MyCalendar.calendarToString(cal,MyCalendar.FORMAT_YEARTODATE);
		
		String optInSource = "";
		
		if(rowObject[7] == null) {
			optInSource = "";
		}
		else if(rowObject[7].toString().trim().equalsIgnoreCase(Constants.CONTACT_LOYALTY_TYPE_POS)) {
			optInSource = Constants.CONTACT_LOYALTY_TYPE_STORE+ (rowObject[11]!=null && rowObject[11].toString().trim().length() >0 ? " - "+ fetchStoreName(rowObject[11].toString()):"");
		}
		else {
			optInSource = rowObject[7].toString().trim();
		}
		String nameStr = "";
		nameStr += (rowObject[2]==null||rowObject[2].toString().trim().isEmpty()?"": nameStr.trim().length() > 0 ? ", "+rowObject[2].toString().trim() :rowObject[2].toString().trim());
		nameStr += (rowObject[1]==null||rowObject[1].toString().trim().isEmpty()?"": nameStr.trim().length() > 0 ? ", "+rowObject[1].toString().trim() :rowObject[1].toString().trim());

		nameLblId.setValue(nameStr);
		cardNumberLblId.setValue(rowObject[0] == null?"":rowObject[0].toString().trim());
		cardTypeLblId.setValue(rowObject[9] == null?"":rowObject[9].toString().trim());
		optInDateLblId.setValue(optInDate);
		optInSourceLblId.setValue(optInSource);
		
		updateLoyaltyData();
		if(rowObject[10] != null){
		contactId = Long.parseLong(rowObject[10].toString().trim());
		lastPurDate= retailProSalesDao.findLastpurchasedDate(contactId, currentUser.getUserId());
		lastPurchaseDateLblId.setValue(lastPurDate == null ? "" : MyCalendar.calendarToString(lastPurDate,MyCalendar.FORMAT_YEARTODATE));
		
		lastPurchaseDate = MyCalendar.calendarToString(lastPurDate, MyCalendar.FORMAT_DATETIME_STYEAR);
		lastPurAmt = retailProSalesDao.getLastPurchaseAmountByContactId(contactId, lastPurchaseDate, currentUser.getUserId());
		
		if(lastPurAmt != null) {
			//Double lastPurchaseAmt = Double.parseDouble(lastPurAmt[1].toString().trim());
			
		     //logger.info(f.format(lastPurchaseAmt));
			//Math.round(a*100)/100.00;
			
		lastPurchaseAmountLblId.setValue(lastPurAmt[1] == null ? "" : "$"+f.format(lastPurAmt[1]));
		}
		else {
			lastPurchaseAmountLblId.setValue("");
		}
		}
	}// onSelect$viewCardsReportLbId
	
	private String fetchStoreName(String store_number){
		 
		 for (Map<String, Object> eachMap: storeNumberNameMapList) {

			 if(eachMap.get("home_store_id").toString().equals(store_number)){
				 if(eachMap.get("store_name") != null){
					 return eachMap.get("store_name").toString();
				 }else{
					 return "Store ID "+store_number;
				 }
			 }
		 }
		 
		 return "Store ID "+store_number;
	}
	
	public void updateLoyaltyData() {
		DecimalFormat format = new DecimalFormat("0.00");		
		totalRewardsRedeemedLblId.setValue(contactLoyalty.getTotalGiftcardRedemption() == null?"":"$"+format.format(contactLoyalty.getTotalGiftcardRedemption()));
		balanceRewardsLblId.setValue(contactLoyalty.getGiftcardBalance() == null?"":"$"+format.format(contactLoyalty.getGiftcardBalance()));
		balancePointsLblId.setValue(contactLoyalty.getLoyaltyBalance() == null?"":""+contactLoyalty.getLoyaltyBalance().intValue());
		finalBalanceRewardsLblId.setValue(contactLoyalty.getGiftcardBalance() == null?"":"$"+format.format(contactLoyalty.getGiftcardBalance()));
		finalBalancePointsLblId.setValue(contactLoyalty.getLoyaltyBalance() == null?"":""+contactLoyalty.getLoyaltyBalance().intValue());
	
	}// updateLoyaltyData
	
	public void onClick$refreshLoyaltyAnchId() {
		if(rowObject != null) {
			
			//contact = contactsDao.findById(contactId);

			SparkBaseLocationDetailsDao sblDao = 
					(SparkBaseLocationDetailsDao)SpringUtil.getBean("sparkBaseLocationDetailsDao");
			
			SparkBaseLocationDetails sbDetails = sblDao.findBylocationId(
					GetUser.getUserObj().getUserOrganization().getUserOrgId(), contactLoyalty.getLocationId());
			
			InquiryResponse errMsg = null;
			
			// Get the latest Loyalty balance info
			errMsg =(InquiryResponse)SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.INQUIRY, sbDetails, contactLoyalty, null,null, true);
			if(errMsg==null) {
				MessageUtil.setMessage("Error occured while fetching the data.", "color:red;");
				
				return;
			} 
	
			// Get the latest Account History for total counts
			ErrorMessageComponent errorMsg = (ErrorMessageComponent)SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.getInstance().ACCOUNT_HISTORY, sbDetails, contactLoyalty, null, null, true);
			if(errorMsg!=null) {
				showSparkBaseErrorMessage(errorMsg);
				return;
			} // if
			contactLoyalty.setLastFechedDate(Calendar.getInstance());
			contactsLoyaltyDaoForDML.saveOrUpdate(contactLoyalty);
			updateLoyaltyData();
		}
	}// onClick$refreshLoyaltyAnchId
	
	private void showSparkBaseErrorMessage(ErrorMessageComponent errMsg) {
		
		 logger.info("Error Message");
		 
		 String errMsgStr = 
			 "Error Message : \n\t " +
			 "  Rejection Id = " + errMsg.getRejectionId() + "\n\t" +
		     "  Error Code = " + errMsg.getErrorCode() + "\n\t" +
		     "  Brief Message = " + errMsg.getBriefMessage() + "\n\t" +
		     "  In-Depth Message = " + errMsg.getInDepthMessage();
		    
		 MessageUtil.setMessage(errMsgStr, "color:red;");
	}// showSparkBaseErrorMessage
	
	public void onClick$addBtnId() {
		if(rowObject != null) {
			
			String valueCode = addLbId.getSelectedItem().getValue();
			String enteredAmount = addTbId.getValue().trim();
			String[] amount = new String[2];
			amount[0] = valueCode;
			amount[1] = enteredAmount;
			double temp=0.0;
			if(enteredAmount.isEmpty()){
				
				MessageUtil.setMessage("Please enter an amount to add.", "red");
				return;
			}

			 
			
				if(amount[1].startsWith("-")) {
					MessageUtil.setMessage("Please enter valid amount.", "red");
				}
				else {
						if(valueCode.equals("USD")) {
							if(!checkIfDouble(amount[1])){
								MessageUtil.setMessage("Please enter valid amount.", "red");
								return;
							}
							StringBuffer sb = new StringBuffer(amount[1]);
							if(sb.substring(sb.indexOf(".")+1).toString().length()>2 ){
								MessageUtil.setMessage("Please enter valid amount. Allowed upto 2 decimal points.","red");
								return;
							}
							temp = enteredAmount != null ? Double.parseDouble(enteredAmount) : 0;
							if(temp==0){
								MessageUtil.setMessage("Rewards to be added should be greater than Zero.", "color:red;");
								return;
							} 
								
						}
						else {
							if(!checkIfNumber(amount[1])){
								MessageUtil.setMessage("Please enter valid amount.", "red");
								return;
							}
							temp = enteredAmount != null ? Double.parseDouble(enteredAmount) : 0;
							if(temp==0){
								MessageUtil.setMessage("Points to be added should be greater than Zero.", "color:red;");
								return;
							} 
						}
					
						if(Messagebox.show("Are you sure you want to add loyalty points/rewards? ", "Confirm" ,Messagebox.OK | Messagebox.CANCEL, 
								Messagebox.QUESTION) == Messagebox.OK) {
						
							List<SparkBaseLocationDetails> sbDetailsList = sparkBaseLocationDetailsDao.findAllByOrganizationId(currentUser.getUserOrganization().getUserOrgId());
							
							if(sbDetailsList == null){
								return;
							}
							
							sparkBaseLoc = sbDetailsList.get(0);
							
							responseObject = SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.ADJUSTMENT, sparkBaseLoc, contactLoyalty, null, amount, true);
							
							if(responseObject instanceof AdjustmentResponse){
								contactsLoyaltyDaoForDML.saveOrUpdate(contactLoyalty);
								
								if(valueCode.equals("USD")) {
									Messagebox.show("Rewards added successfully.", "Info", Messagebox.OK,Messagebox.INFORMATION);
								}
								else {
									Messagebox.show("Points added successfully.", "Info", Messagebox.OK,Messagebox.INFORMATION);
								}
								
								addTbId.setValue("");
								updateLoyaltyData();
							}
							else {
								
								MessageUtil.setMessage("Server error occured, transaction failed.", "color:red;");
								addTbId.setValue("");
								return;
							}
					}
					
				}
					}
		else {
			Messagebox.show("Please select a contact to add loyalty points/rewards.", "Error" ,Messagebox.OK, 
					Messagebox.ERROR);
		}
	}// onClick$addBtnId
	
	public void onClick$subtractBtnId() {
		
		
		if(rowObject != null) {
			
			
			String valueCode = subLbId.getSelectedItem().getValue();
			String enteredAmount = "-" + subTbId.getValue().trim();
			String[] amount = new String[2];
			amount[0] = valueCode;
			amount[1] = enteredAmount;
			double temp=0.0;
			
			if((subTbId.getValue().trim().isEmpty())){
				
				MessageUtil.setMessage("Please enter an amount to subtract.", "red");
				return;
			}

			
					
						
						if(valueCode.equals("USD")) {
							
							if(!checkIfDouble(amount[1])){
								MessageUtil.setMessage("Please enter valid amount.", "red");
								return;
							}
							StringBuffer sb = new StringBuffer(amount[1]);
							if(sb.substring(sb.indexOf(".")+1).toString().length()>2 ){
								MessageUtil.setMessage("Please enter valid amount. Allowed upto 2 decimal points.","red");
								return;
							}
							temp = enteredAmount != null ? Double.parseDouble(enteredAmount) : 0;
							temp =temp*-1;
							if(temp==0){
								MessageUtil.setMessage("Rewards to be subtracted should be greater than Zero.", "color:red;");
								return;
							} 
							
							
							double existingGiftcardBalance = contactLoyalty.getGiftcardBalance() == null ? 0.0 : contactLoyalty.getGiftcardBalance();
							logger.info("Temp ::"+temp+"Existing loyalty balance"+existingGiftcardBalance);
							
							if(temp > existingGiftcardBalance){
								MessageUtil.setMessage("Rewards to be subtracted should be less or equal to existing reward balance.", "color:red;");
							//	subTbId.setValue("");
								subTbId.setFocus(true);
								return;
							}
						}
						else {
							if(!checkIfNumber(amount[1])){
								MessageUtil.setMessage("Please enter valid amount.", "red");
								return;
							}
							temp = enteredAmount != null ? Double.parseDouble(enteredAmount) : 0;
							temp =temp*-1;
							if(temp==0){
								MessageUtil.setMessage("Points to be subtracted should be greater than Zero.", "color:red;");
								return;
							}
							
							double existingLoyaltyBalance = contactLoyalty.getLoyaltyBalance() == null ? 0.0 : contactLoyalty.getLoyaltyBalance();
							
							logger.info("Temp ::"+temp+"Existing loyalty balance"+existingLoyaltyBalance);
							if(temp > existingLoyaltyBalance){
								logger.info("true");
								MessageUtil.setMessage("Points to be subtracted should be less or equal to existing point balance.", "color:red;");
								//subTbId.setValue("");
								subTbId.setFocus(true);
								return;
							}
						}
					
					
				
					
					if(Messagebox.show("Are you sure you want to subtract loyalty points/rewards? ", "Confirm" ,Messagebox.OK | Messagebox.CANCEL, 
							Messagebox.QUESTION) == Messagebox.OK) {
					
						List<SparkBaseLocationDetails> sbDetailsList = sparkBaseLocationDetailsDao.findAllByOrganizationId(currentUser.getUserOrganization().getUserOrgId());
						sparkBaseLoc = sbDetailsList.get(0);
						
						responseObject = SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.ADJUSTMENT, sparkBaseLoc, contactLoyalty, null, amount, true);
						
						if(responseObject instanceof AdjustmentResponse){
							contactsLoyaltyDaoForDML.saveOrUpdate(contactLoyalty);
							
							if(valueCode.equals("USD")) {
								Messagebox.show("Rewards subtracted successfully.", "Info", Messagebox.OK,Messagebox.INFORMATION);
							}
							else {
								Messagebox.show("Points subtracted successfully.", "Info", Messagebox.OK,Messagebox.INFORMATION);
							}
							
							subTbId.setValue("");
							updateLoyaltyData();
						}
						else if (responseObject instanceof ErrorMessageComponent){
							ErrorMessageComponent errorobj = (ErrorMessageComponent)responseObject;
							String briefMsg = errorobj.getBriefMessage();
							String depthMsg = errorobj.getInDepthMessage();
							//MessageUtil.setMessage("Server error occured, transaction failed.", "color:red;");
							MessageUtil.setMessage(briefMsg+"("+depthMsg+")", "color:red;");
							subTbId.setValue("");
							return;
						}
						else {
							MessageUtil.setMessage("Server error occured, transaction failed.", "color:red;");
							subTbId.setValue("");
							return;
						}
					}
				
			
		}
		else {
			Messagebox.show("Please select a contact to subtract loyalty points/rewards.", "Error" ,Messagebox.OK, 
					Messagebox.ERROR);
		}
	}// onClick$subtractBtnId
	
	public void onClick$resendBtnId() {
		if(rowObject != null) {
			if(Messagebox.show("Do you want to resend loyalty card details to the contact?", "Confirm" ,Messagebox.OK | Messagebox.CANCEL, 
					Messagebox.QUESTION) == Messagebox.OK) {
				// to send the loyalty related email
				CustomTemplates custTemplate = null;
				String message = PropertyUtil.getPropertyValueFromDB("loyaltyOptinMsgTemplate");
				
				MailingList mailingList = mailingListDao.findListTypeMailingList(Constants.MAILINGLIST_TYPE_POS, currentUser.getUserId());
				
				if (mailingList.getLoyaltyCutomTempId() != null) {
					custTemplate = customTemplatesDao.findCustTemplateById(mailingList.getLoyaltyCutomTempId());
					if(custTemplate != null && custTemplate.getHtmlText()!= null) {
						  message = custTemplate.getHtmlText();
					  }else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && 
							  custTemplate.getMyTemplateId()!=null) {
						  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
						  if(myTemplates != null) message = myTemplates.getContent();
					  }
				}
				
				if(rowObject[3] == null || rowObject[3].toString().trim().length() == 0) {
					MessageUtil.setMessage("Contact doesn't have valid email id ", "red");
					return;
				}
				logger.debug("-----------email----------"+rowObject[3].toString().trim());
				
				String cardId = rowObject[0].toString().trim();
				ContactsLoyalty contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(currentUser.getUserId(), cardId);
				
				Contacts contact = contactsLoyalty.getContact();
				
				message = message.replace("[OrganizationName]", currentUser.getUserOrganization().getOrganizationName())
						.replace("[CardNumber]", rowObject[0].toString())
						.replace("[CardPin]", rowObject[8].toString())
						.replace("[senderName]", Utility.getOnlyUserName(currentUser.getUserName()));
	
				EmailQueue testEmailQueue = new EmailQueue(mailingList.getLoyaltyCutomTempId(),
						Constants.EQ_TYPE_TEST_LOYALTY_DETAILS_MAIL, message,
						"Active", rowObject[3].toString().trim(), currentUser,
						MyCalendar.getNewCalendar(), "Loyalty Card Details.", null,
						null, null, contact.getContactId());
	
				//emailQueueDao.saveOrUpdate(testEmailQueue);
				emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
				logger.debug("Loyalty card details template saved in email queue....ready to send mail... ");
				Messagebox.show("Email will be delivered shortly with card details.", "Confirm" ,Messagebox.OK, 
						Messagebox.INFORMATION);
			}
		}
		else {
			Messagebox.show("Please select a contact to send card details.", "Error" ,Messagebox.OK, 
					Messagebox.ERROR);
		}
	}// onclick$resendBtnId
	
	
	public void onClick$selectFieldBtnId$custExport() {

		custExport.setVisible(false);
		List<Component> chkList = custExport$chkDivId.getChildren();


		int indexes[]=new int[chkList.size()];
		boolean checked=false;

	
		for(int i=0;i<chkList.size();i++) {
			indexes[i]=-1;
		} 

		Checkbox tempChk = null;
		
		
		if( ((Checkbox)custExport$chkDivId.getFirstChild()).isChecked()) {

			indexes[0]=0;
			checked=true;
		}

		
		tempChk = (Checkbox)chkList.get(1);
		if(tempChk.getLabel().equalsIgnoreCase("First Name") ) {

			if(tempChk.isChecked()){
				indexes[1]=0;
				checked=true;
			}
			else{
				indexes[1]=-1;
			}
		}
		
		
		tempChk = (Checkbox)chkList.get(2);
		if(tempChk.getLabel().equalsIgnoreCase("Last Name") ) {

			if(tempChk.isChecked()){
				indexes[2]=0;
				checked=true;
			}
			else{
				indexes[2]=-1;
			}
		}


		
		tempChk = (Checkbox)chkList.get(3);
		if(tempChk.getLabel().equalsIgnoreCase("Email Address") ) {

			if(tempChk.isChecked()){
				indexes[3]=0;
				checked=true;
			}
			else{
				indexes[3]=-1;
			}
		}
		
		tempChk = (Checkbox)chkList.get(4);
		if(tempChk.getLabel().equalsIgnoreCase("Phone Number") ) {

			if(tempChk.isChecked()){
				indexes[4]=0;
				checked=true;
			}
			else{
				indexes[4]=-1;
			}
		}
		
		tempChk = (Checkbox)chkList.get(5);
		if(tempChk.getLabel().equalsIgnoreCase("Address") ) {

			if(tempChk.isChecked()){
				indexes[5]=0;
				checked=true;
			}
			else{
				indexes[5]=-1;
			}
		}
		
		tempChk = (Checkbox)chkList.get(6);
		if(tempChk.getLabel().equalsIgnoreCase("Opt-in Date") ) {

			if(tempChk.isChecked()){
				indexes[6]=0;
				checked=true;
			}
			else{
				indexes[6]=-1;
			}
		}
		tempChk = (Checkbox)chkList.get(7);
		if(tempChk.getLabel().equalsIgnoreCase("Opt-in Source") ) {

			if(tempChk.isChecked()){
				indexes[7]=0;
				checked=true;
			}
			else{
				indexes[7]=-1;
			}
		}
		if(!checked) {
			MessageUtil.setMessage("Please select atleast one field", "red");
			custExport.setVisible(false);
		}

		else {
			/*int confirm=Messagebox.show("Do you want to Export with selected fields ?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm==1){*/
				try{

					exportCSV(indexes);

				}catch(Exception e){
					logger.error("Exception caught :: ",e);
				}
				/*}
			else{
				custExport.setVisible(true);
			}*/

		}
	}
	

	public void exportCSV(int[] indexes)
	{
		/*if( viewCardsReportLbId.getItemCount()== 0) {
			MessageUtil.setMessage("No contacts exist in the selected search.", "color:red", "TOP");
			return;
		}*/

		String msgStr="";
		if(exportFilterLbId.getSelectedIndex()==0 || exportFilterLbId.getSelectedIndex()==1) {
			msgStr="Confirm to export with the below options :\r\n"+
					"Export Type :" +exportFilterLbId.getSelectedItem().getLabel(); 
		}
		else if(exportFilterLbId.getSelectedIndex() == 3) {
			msgStr="Confirm to export with the below options :\r\n";
			
			if(!searchByTbId.getValue().trim().isEmpty()) {
				msgStr += "Search By " + searchbyLbId.getSelectedItem().getLabel() +" = "+searchByTbId.getValue().trim()+"\r\n"; 
			}
			
			msgStr += "Opt-in Source = " +optInSourceLbId.getSelectedItem().getLabel()+"\r\n" ;
			
			if(fromDateboxId.getValue() != null && toDateboxId.getValue() != null) {
				
				msgStr +="Opt-in date From =" +MyCalendar.calendarToString(fromDateboxId.getClientValue(),MyCalendar.FORMAT_STDATE )+"\r\n" + 
						"Opt-in date To =" +MyCalendar.calendarToString(toDateboxId.getClientValue(),MyCalendar.FORMAT_STDATE )+"\r\n"; 
			}

		}
		else if(exportFilterLbId.getSelectedIndex() == 2) {
			msgStr=" Are you sure do you want to export all registered cards?";
			
			/*if(!searchByTbId.getValue().trim().isEmpty()) {
				msgStr += "Search By " + searchbyLbId.getSelectedItem().getLabel() +" = "+searchByTbId.getValue().trim()+"\r\n"; 
			}
			
			msgStr += "Opt-in Source = " +optInSourceLbId.getSelectedItem().getLabel()+"\r\n" +
					"Opt-in date From =" +MyCalendar.calendarToString(fromDateboxId.getClientValue(),MyCalendar.FORMAT_STDATE )+"\r\n" + 
					"Opt-in date To =" +MyCalendar.calendarToString(toDateboxId.getClientValue(),MyCalendar.FORMAT_STDATE )+"\r\n"; 
			 */

		}
		if(MultiLineMessageBox.show(msgStr, "Confirm", MultiLineMessageBox.YES|MultiLineMessageBox.NO, 
				MultiLineMessageBox.QUESTION)==MultiLineMessageBox.YES) {
			if(exportFilterLbId.getSelectedItem().getLabel().equalsIgnoreCase("All Active Cards")) {
				exportCSV("CSV", exportFilterLbId.getSelectedItem().getLabel(), null, null, null, null, null, false,indexes);
				return;
			}
			else if(exportFilterLbId.getSelectedItem().getLabel().equalsIgnoreCase("All Inventory Cards")) {
				exportCSV("CSV", exportFilterLbId.getSelectedItem().getLabel(), null, null, null, null, null, false,indexes);
				return;
			}
			else if(exportFilterLbId.getSelectedItem().getLabel().equalsIgnoreCase("With Selected Filters")) {
				
				Calendar serverFromDateCal = null;
				Calendar serverToDateCal =  null;
				if(fromDateboxId.getValue() != null && toDateboxId.getValue() != null) {
			
				serverFromDateCal = fromDateboxId.getServerValue();
				serverToDateCal = toDateboxId.getServerValue();
				
				Calendar tempClientFromCal = fromDateboxId.getClientValue();
				Calendar tempClientToCal = toDateboxId.getClientValue();
				
				serverFromDateCal.set(Calendar.HOUR_OF_DAY, 
						serverFromDateCal.get(Calendar.HOUR_OF_DAY)-tempClientFromCal.get(Calendar.HOUR_OF_DAY));
				serverFromDateCal.set(Calendar.MINUTE, 
						serverFromDateCal.get(Calendar.MINUTE)-tempClientFromCal.get(Calendar.MINUTE));
				serverFromDateCal.set(Calendar.SECOND, 0);
				
				serverToDateCal.set(Calendar.HOUR_OF_DAY, 
						23+serverToDateCal.get(Calendar.HOUR_OF_DAY)-tempClientToCal.get(Calendar.HOUR_OF_DAY));
				serverToDateCal.set(Calendar.MINUTE, 
						59+serverToDateCal.get(Calendar.MINUTE)-tempClientToCal.get(Calendar.MINUTE));
				serverToDateCal.set(Calendar.SECOND, 59);	
				
				if(serverToDateCal.compareTo(serverFromDateCal) < 0) {
					MessageUtil.setMessage("'To' date must be later than 'From' date.", "color:red",	"TOP");
					return;
				}
				}
				
				/* String fromDate = MyCalendar.calendarToString(fromDateboxId.getServerValue(), MyCalendar.FORMAT_DATETIME_STYEAR);
				
				Calendar endCal = toDateboxId.getServerValue();
				endCal.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
				endCal.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));
				endCal.set(Calendar.SECOND, Calendar.getInstance().get(Calendar.SECOND));
				String toDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR); */					
				String optInStr = null;
				
				if(optInSourceLbId.getSelectedItem()!=null && optInSourceLbId.getSelectedIndex()>0) {
					optInStr = optInSourceLbId.getSelectedItem().getValue();
				}
				
				String searchVal = searchByTbId.getValue().trim();
				String searchKey=null;
				
				if(searchVal.isEmpty()) {
					searchVal=null;
				}
				else if (searchbyLbId.getSelectedItem().getValue().equals("Card") ||
						searchbyLbId.getSelectedItem().getValue().equals("mobile_phone")) {
					
					if(checkIfNumber(searchByTbId.getValue().trim())) {
						searchKey =	searchbyLbId.getSelectedItem().getValue();
					}
					else {
						MessageUtil.setMessage("Please enter a valid entry in search by field.", "red");
						return;
					}
				}
				else if (searchbyLbId.getSelectedItem().getValue().equals("first_name") 
						|| searchbyLbId.getSelectedItem().getValue().equals("last_name")
						|| searchbyLbId.getSelectedItem().getValue().equals("email_id")) {
					
					searchKey =	searchbyLbId.getSelectedItem().getValue();
				}
				exportCSV("CSV", exportFilterLbId.getSelectedItem().getLabel(), 
						(serverFromDateCal != null ? serverFromDateCal.toString() : null),
						(serverToDateCal != null ? serverToDateCal.toString() : null), optInStr, searchKey, searchVal, false,indexes);
			}
			else if(exportFilterLbId.getSelectedItem().getLabel().equalsIgnoreCase("All Registered Cards")) {
				exportCSV("CSV", exportFilterLbId.getSelectedItem().getLabel(), null, null, null, null, null, true,indexes);
				return;
			}
		}
		else {
			return;
		}
		
			
	}
	public void exportCSV(String ext, String filterType, String fromDate, 
			String toDate, String optInStr, String searchKey, String searchVal, boolean isRegistered) {
		long startTime = System.currentTimeMillis();
		String userName = Constants.STRING_NILL;
		String usersParentDirectory = Constants.STRING_NILL;
		File downloadDir = null;
		String filePath = Constants.STRING_NILL;
		StringBuffer sb = null;
		File file = null;
		BufferedWriter bw = null;
		JdbcResultsetHandler jdbcResultsetHandler = null;
		String query = Constants.STRING_NILL;
		try {
			ext = ext.trim();
			userName = GetUser.getUserName();
			usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
			downloadDir = new File(usersParentDirectory + "/" + userName + "/LoyaltyCards/download/" );
			if(downloadDir.exists()) {
				try {
					FileUtils.deleteDirectory(downloadDir);
					logger.debug(downloadDir.getName() + " is deleted");
				} catch (Exception e) {
					logger.error("Exception ::" , e);
					logger.warn(downloadDir.getName() + " is not deleted");
				}
			}
			if(!downloadDir.exists()) {
				downloadDir.mkdirs();
			}
			
			filePath = "";
			filePath = usersParentDirectory + "/" + userName + "/LoyaltyCards/download/" + "LoyaltyCards_"+filterType + System.currentTimeMillis() + "." + ext;
			
			file = new File(filePath);
			bw = new BufferedWriter(new FileWriter(filePath));
			logger.debug("Writing to the file : " + filePath);

			sb = new StringBuffer();
			
			Long sbLocId = null;
			
				sb.append("\"Card Number\"");
				sb.append("\r\n");
				bw.write(sb.toString());
				sb.setLength(0);
				
				Long userOrgId = currentUser.getUserOrganization().getUserOrgId();
				List<SparkBaseLocationDetails> sbLoclist = sparkBaseLocationDetailsDao.findAllByOrganizationId(userOrgId.longValue());
				if(sbLoclist != null && sbLoclist.size()>0) {
					sbLocId = sbLoclist.get(0).getSparkBaseLocationDetails_id();
				}
				
				if(sbLocId == null)	{		
					Messagebox.show("No inventory loyalty cards found.",
							"Info", Messagebox.OK,Messagebox.INFORMATION);
					return;
					}
				query = "SELECT card_id from sparkbase_cards where sparkbase_location_id=" + 
						sbLocId.longValue()+" and status = '"+Constants.SPARKBASE_CARD_STATUS_INVENTORY+"'";
				jdbcResultsetHandler = new JdbcResultsetHandler();
				jdbcResultsetHandler.executeStmt(query);
				
				if(jdbcResultsetHandler.totalRecordsSize() == 0) {
					Messagebox.show("No inventory loyalty cards found.",
							"Info", Messagebox.OK,Messagebox.INFORMATION);
					return;
				}
				OCCSVWriter csvWriter = null;
				try{
				csvWriter = new OCCSVWriter(bw);
				csvWriter.writeAll(jdbcResultsetHandler.getResultSet(), false);
				}
				catch(Exception e){
					logger.info("Exception while writing through csvwriter ", e);
				}finally {
					bw.flush();
					csvWriter.flush();
					bw.close();
					csvWriter.close();
					csvWriter = null;
				}
			
			Filedownload.save(file, "text/plain");
			logger.debug("exited");
		} catch (Exception e) {
			logger.error("** Exception while exporting into CSV file: " , e);
		}finally {
			if(jdbcResultsetHandler != null)
			userName = null; usersParentDirectory = null; downloadDir = null; filePath = null; sb = null; file = null;
			bw = null; jdbcResultsetHandler = null; query = null;
		}
		logger.fatal("Time taken to export loyalty cards is :::   :: " + (System.currentTimeMillis()-startTime));
	}// exportCSV
		

	public void exportCSV(String ext, String filterType, String fromDate, 
			String toDate, String optInStr, String searchKey, String searchVal, boolean isRegistered,int[] indexes) {
		long startTime = System.currentTimeMillis();
		String query = Constants.STRING_NILL;
		String exportQuery = Constants.STRING_NILL; 
		JdbcResultsetHandler jdbcResultsetHandler = null;
		JdbcResultsetHandler exportJdbcResultsetHandler = null;
		String userName = Constants.STRING_NILL;
		String usersParentDirectory = Constants.STRING_NILL;
		File downloadDir = null;
		String filePath = Constants.STRING_NILL;
		StringBuffer sb = null;
		String udfFldsLabel= Constants.STRING_NILL;
		ResultSet resultSet = null;
		ResultSet exportResultSet = null;
		try {
			ext = ext.trim();
			userName = GetUser.getUserName();
			usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
			downloadDir = new File(usersParentDirectory + "/" + userName + "/LoyaltyCards/download/" );
			if(downloadDir.exists()) {
				try {
					FileUtils.deleteDirectory(downloadDir);
					logger.debug(downloadDir.getName() + " is deleted");
				} catch (Exception e) {
					logger.error("Exception ::" , e);
					logger.warn(downloadDir.getName() + " is not deleted");
				}
			}
			if(!downloadDir.exists()) {
				downloadDir.mkdirs();
			}
			
			filePath = usersParentDirectory + "/" + userName + "/LoyaltyCards/download/" + "LoyaltyCards_"+filterType + System.currentTimeMillis() + "." + ext;
			File file = new File(filePath);
			BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
			logger.debug("Writing to the file : " + filePath);

			sb = new StringBuffer();
					
			exportQuery = "select ticket_id,";

			//user_id--->card_number, client_name-->first_name, contact_name----> last_name, contact_email---->email_id, contact_phone-->phone ,description---> address, created_date---->Opt-in date, user_name-->opt-in source 
			if(indexes[0]==0) {

				udfFldsLabel = "\""+"Card Number"+"\""+",";
				exportQuery += "user_id, ";
			}

			
			if(indexes[1]==0) {

				udfFldsLabel += "\""+"First Name"+"\""+",";
				exportQuery += "client_name, ";
			}
			
			if(indexes[2]==0) {

				udfFldsLabel += "\""+"Last Name"+"\""+",";
				exportQuery += "contact_name, ";
			}	
			if(indexes[3]==0) {

				udfFldsLabel += "\""+"Email Address"+"\""+",";
				exportQuery += "contact_email, ";
			}
			
			if(indexes[4]==0) {

				udfFldsLabel += "\""+"Phone Number"+"\""+",";
				exportQuery += "contact_phone, ";
			}
			if(indexes[5]==0) {

				udfFldsLabel += "\""+"Address"+"\""+",";
				exportQuery += "description, ";
			}
			if(indexes[6]==0) {

				udfFldsLabel += "\""+"Opt-in Date"+"\""+",";
				exportQuery += "created_date, ";
			}
			if(indexes[7]==0) {

				udfFldsLabel += "\""+"Opt-in Source"+"\""+",";
				exportQuery += "user_name, ";
			}
			
			exportQuery = exportQuery.substring(0, exportQuery.length()-2);
			
			sb = new StringBuffer();
			
			sb.append(udfFldsLabel);
			
			
			if(filterType.equalsIgnoreCase("With Selected Filters")) {
			
				//sb.append("FirstName,LastName,EmailId,PhoneNumber,Address");
/*				sb.append("\r\n");
				bw.write(sb.toString());*/
				sb.append("\r\n");
				
				bw.write(sb.toString());
				sb = new StringBuffer();
				
						
				/*loyaltySize = contactsLoyaltyDao.findTotalLoyaltyOptinsBySearch(
						currentUser.getUserId().longValue(),optInStr, fromDate, toDate, searchKey, searchVal );
				if(loyaltySize != null) {
					optInSize = loyaltySize.longValue();
				}
				
				if(optInSize == 0) {
					Messagebox.show("No loyalty cards found with the given search criteria.",
							"Info", Messagebox.OK,Messagebox.INFORMATION);
					return;
				}*//* should be*/
				
				
					
				String optinSrcQry ="";
				String searchByQry ="";
				String dateSubQry = "";
				if(fromDate != null && toDate != null ) {
					
					dateSubQry = " AND cl.created_date >= '"+fromDate+"' AND cl.created_date <= '"+toDate+ "'";
				}
				
				if(optInStr!=null) {
					optinSrcQry = " AND cl.contact_loyalty_type = '"+optInStr+"' ";
				}
				
				if(searchKey!=null && searchVal!=null) {
					if(searchKey.equalsIgnoreCase("card")) {
						searchByQry = " AND cl.card_number="+searchVal+" ";
					}
					else if(searchKey.equalsIgnoreCase("first_name") ||
							searchKey.equalsIgnoreCase("last_name") ||
							searchKey.equalsIgnoreCase("email_id")) {
						searchByQry = " AND c."+searchKey+" LIKE '%"+searchVal+"%' ";
					}
					else if(searchKey.equalsIgnoreCase("mobile_phone")) {
						searchByQry = " AND c."+searchKey+"='"+searchVal+"' ";
					} 
				}
				
				

				query = "SELECT cl.card_number, c.first_name, c.last_name, c.email_id, c.mobile_phone,"+ 
						" c.address_one, c.address_two, c.city, c.state, c.country, c.zip, "+
						" cl.created_date, cl.contact_loyalty_type"+
						" FROM contacts_loyalty cl left join contacts c on cl.contact_id = c.cid  "+
						" where cl.user_id = "+ currentUser.getUserId()+" AND (cl.service_type = '"+OCConstants.LOYALTY_SERVICE_TYPE_SB+"'  or cl.service_type IS NULL) "+
						dateSubQry + optinSrcQry + searchByQry +
						" order by cl.created_date desc";
				optinSrcQry = null;
				searchByQry = null;
				dateSubQry = null;
				
				//
				
				/*for(int i=0;i < optInSize; i+=size) {
					optInslist = contactsLoyaltyDao.findContactLoyaltyOptinsBySearch(
							currentUser.getUserId(),i, size, optInStr, fromDate, toDate, searchKey, searchVal );*/
					
					/*if(optInslist.size()>0) {
						for(Object[] objectList : optInslist) {
							if(indexes[0]==0)
							{	sb.append("\""); sb.append(objectList[0]==null?"":objectList[0].toString().trim()); sb.append("\",");}
								if(indexes[1]==0)
								{	sb.append("\""); sb.append(objectList[1]==null?"":objectList[1].toString().trim()); sb.append("\",");}
								if(indexes[2]==0)
								{sb.append("\""); sb.append(objectList[2]==null?"":objectList[2].toString().trim()); sb.append("\",");}
								if(indexes[3]==0)
								{sb.append("\""); sb.append(objectList[3]==null?"":objectList[3].toString().trim()); sb.append("\",");}
								if(indexes[4]==0)
								{sb.append("\""); sb.append(objectList[4]==null?"":objectList[4].toString().trim()); sb.append("\",");}
								if(indexes[5]==0)
								{sb.append("\""); sb.append(objectList[5]==null?"":objectList[5].toString().trim()); sb.append("\",");}
							
							String optInDate = "";
							if(objectList[6] !=null){
							SimpleDateFormat formatter = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
							Date date = null;
							try {
								date = (Date)formatter.parse(objectList[6].toString().trim());
							} catch (ParseException e) {
								logger.error("Exception ::" , e);
							} 
							Calendar cal=Calendar.getInstance();
							cal.setTime(date);
				        	optInDate = MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STYEAR);
							}
							else
							{
								optInDate="";
							}
							if(indexes[6]==0){
							sb.append("\""); sb.append(optInDate); sb.append("\",");
							}
							
							String optInSource = "";
							
							if(objectList[7] == null) {
								optInSource = "";
							}
							else if(objectList[7].toString().trim().equalsIgnoreCase(Constants.CONTACT_LOYALTY_TYPE_POS)) {
								optInSource = Constants.CONTACT_LOYALTY_TYPE_STORE;
							}
							else {
								optInSource = objectList[7].toString().trim();
							}
							if(indexes[7]==0)
							{
							sb.append("\""); sb.append(optInSource); sb.append("\"");
							}
							if(objectList.length > 9) {
								
								sb.append("\""); sb.append(objectList[9] != null && objectList[9].toString().equals("1") ?"Yes" : ""); sb.append("\",");
								
							}//if
									
							sb.append("\r\n");
						}
					}
				// if
					
					bw.write(sb.toString());
					sb.setLength(0);
					optInslist = null;*/
					//System.gc()();
				/*}//for
*/			}//if
					
			else if(filterType.equalsIgnoreCase("All Active Cards")) {
			
				sb.append("\r\n");
				bw.write(sb.toString());
				sb = new StringBuffer();
				
					query = "SELECT cl.card_number, c.first_name, c.last_name, c.email_id, c.mobile_phone,"+
							" c.address_one, c.address_two, c.city, c.state, c.country, c.zip,"+
							" cl.created_date, cl.contact_loyalty_type"+
							" FROM contacts_loyalty cl left join contacts c on cl.contact_id = c.cid  "+
							" where cl.user_id = "+ currentUser.getUserId() + " AND (cl.service_type = '"+OCConstants.LOYALTY_SERVICE_TYPE_SB+"'  or cl.service_type IS NULL) " +
							" order by cl.created_date desc";
				/*for(int i=0;i < optInSize; i+=size) {
					optInslist = contactsLoyaltyDao.findContactLoyaltyOptinsByuserId(currentUser.getUserId(),isRegistered,i,size);
					
					if(optInslist.size()>0) {
						for(Object[] objectList : optInslist) {
							
							if(indexes[0]==0)
							{	sb.append("\""); sb.append(objectList[0]==null?"":objectList[0].toString().trim()); sb.append("\",");}
								if(indexes[1]==0)
								{	sb.append("\""); sb.append(objectList[1]==null?"":objectList[1].toString().trim()); sb.append("\",");}
								if(indexes[2]==0)
								{sb.append("\""); sb.append(objectList[2]==null?"":objectList[2].toString().trim()); sb.append("\",");}
								if(indexes[3]==0)
								{sb.append("\""); sb.append(objectList[3]==null?"":objectList[3].toString().trim()); sb.append("\",");}
								if(indexes[4]==0)
								{sb.append("\""); sb.append(objectList[4]==null?"":objectList[4].toString().trim()); sb.append("\",");}
								if(indexes[5]==0)
								{sb.append("\""); sb.append(objectList[5]==null?"":objectList[5].toString().trim()); sb.append("\",");}
							
							String optInDate = "";
							if(objectList[6] !=null){
							SimpleDateFormat formatter = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
							Date date = null;
							try {
								date = (Date)formatter.parse(objectList[6].toString().trim());
							} catch (ParseException e) {
								logger.error("Exception ::" , e);
							} 
							Calendar cal=Calendar.getInstance();
							cal.setTime(date);
				        	optInDate = MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STYEAR);
							}
							else
							{
								optInDate="";
							}
							if(indexes[6]==0){
							sb.append("\""); sb.append(optInDate); sb.append("\",");}
							String optInSource = "";
							
							if(objectList[7] == null) {
								optInSource = "";
							}
							else if(objectList[7].toString().trim().equalsIgnoreCase(Constants.CONTACT_LOYALTY_TYPE_POS)) {
								optInSource = Constants.CONTACT_LOYALTY_TYPE_STORE;
							}
							else {
								optInSource = objectList[7].toString().trim();
							}
							if(indexes[6]==0){
							sb.append("\""); sb.append(optInSource); sb.append("\"");}
							sb.append("\r\n");
						}
					}
					bw.write(sb.toString());
					sb.setLength(0);
					optInslist = null;*/
					//System.gc()();
				/*}//for
*/			}//else if
			
				else if(filterType.equalsIgnoreCase("All Registered Cards")) {
				sb.append(isRegistered ? ",Registered" : "");
				sb.append("\r\n");
				
				bw.write(sb.toString());
				sb = new StringBuffer();
				//sb.append("\"Card Number\",\"First Name\",\"Last Name\",\"Email Id\",\"Phone Number\",Address,\"Opt-in Date\",\"Opt-in Source\"");
			
				query = "SELECT cl.card_number, c.first_name, c.last_name, c.email_id, c.mobile_phone,"+
						" c.address_one, c.address_two, c.city, c.state, c.country, c.zip,"+
						" cl.created_date, cl.contact_loyalty_type, cl.is_registered"+
						" FROM contacts_loyalty cl left join contacts c on cl.contact_id = c.cid "+
						" where cl.user_id = "+ currentUser.getUserId() +" AND (cl.service_type = '"+OCConstants.LOYALTY_SERVICE_TYPE_SB+"'  or cl.service_type IS NULL) " +
						" AND cl.is_registered=1"+
						" order by cl.created_date desc";
			/*loyaltySize = contactsLoyaltyDao.findTotalLoyaltyCards(currentUser.getUserId(), isRegistered);
			if(loyaltySize != null) {
				
				optInSize = loyaltySize.longValue();
			}
			
			if(optInSize == 0) {
				
				Messagebox.show("No loyalty cards found with the given search criteria.",
						"Info", Messagebox.OK,Messagebox.INFORMATION);
				return;
			}*/
			
			/*for(int i=0;i < optInSize; i+=size) {
			
			//optInslist = contactsLoyaltyDao.findContactLoyaltyOptinsByuserId(currentUser.getUserId(),i,size);
				optInslist = contactsLoyaltyDao.findContactLoyaltyOptinsByuserId(currentUser.getUserId(),isRegistered,i,size);
			
			if(optInslist.size()>0){
				
				for(Object[] objectList : optInslist) {
					
					if(indexes[0]==0)
					{	sb.append("\""); sb.append(objectList[0]==null?"":objectList[0].toString().trim()); sb.append("\",");}
						if(indexes[1]==0)
						{	sb.append("\""); sb.append(objectList[1]==null?"":objectList[1].toString().trim()); sb.append("\",");}
						if(indexes[2]==0)
						{sb.append("\""); sb.append(objectList[2]==null?"":objectList[2].toString().trim()); sb.append("\",");}
						if(indexes[3]==0)
						{sb.append("\""); sb.append(objectList[3]==null?"":objectList[3].toString().trim()); sb.append("\",");}
						if(indexes[4]==0)
						{sb.append("\""); sb.append(objectList[4]==null?"":objectList[4].toString().trim()); sb.append("\",");}
						if(indexes[5]==0)
						{sb.append("\""); sb.append(objectList[5]==null?"":objectList[5].toString().trim()); sb.append("\",");}
					
					String optInDate = "";
					if(objectList[6] !=null){
					SimpleDateFormat formatter = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
					Date date = null;
					try {
						date = (Date)formatter.parse(objectList[6].toString().trim());
					} catch (ParseException e) {
						logger.error("Exception ::" , e);
					} 
					Calendar cal=Calendar.getInstance();
					cal.setTime(date);
		        	optInDate = MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STYEAR);
					}
					else
					{
						optInDate="";
					}
					if(indexes[6]==0){
					
					sb.append("\""); sb.append(optInDate); sb.append("\",");}
					String optInSource = "";
					
					if(objectList[7] == null) {
						optInSource = "";
					}
					else if(objectList[7].toString().trim().equalsIgnoreCase(Constants.CONTACT_LOYALTY_TYPE_POS)) {
						optInSource = Constants.CONTACT_LOYALTY_TYPE_STORE;
					}
					else {
						optInSource = objectList[7].toString().trim();
					}
					if(indexes[7]==0){
					sb.append("\""); sb.append(optInSource); sb.append("\",");}
					//sb.append("\",");
					if(objectList.length > 9) {
						
						sb.append("\""); sb.append(objectList[9] != null && objectList[9].toString().equals("1") ?"Yes" : ""); sb.append("\"");
						
					}//if
					
					sb.append("\r\n");
				}
			}
			bw.write(sb.toString());
			sb.setLength(0);
			optInslist = null;
			//System.gc()();
			}// for
*/			}// else if
			/*bw.flush();
			bw.close();
			logger.debug("----end---");
			*/
			//exportWithCsvWriter(query, bw,exportFilterLbId.getSelectedItem().getLabel(), false);
			jdbcResultsetHandler = new JdbcResultsetHandler();
			jdbcResultsetHandler.executeStmt(query);
			if(jdbcResultsetHandler.totalRecordsSize()==0)
			{
				if(filterType.equalsIgnoreCase("With Selected Filters"))
				{
					Messagebox.show("No loyalty cards found with the given search criteria.",
							"Info", Messagebox.OK,Messagebox.INFORMATION);
					return;
				}
				else if(filterType.equalsIgnoreCase("All Active Cards"))
				{
					Messagebox.show("No active loyalty cards found.",
							"Info", Messagebox.OK,Messagebox.INFORMATION);
					return;
				}
				else if(filterType.equalsIgnoreCase("All Registered Cards"))
				{
					Messagebox.show("No registered loyalty cards found.",
							"Info", Messagebox.OK,Messagebox.INFORMATION);
					return;
				}
					
			}
			
			if(isRegistered){
				//isRegisterd ---> company_name
				exportQuery += ", status";
			}
			exportQuery += " from support_ticket where ticket_id = -1 and ticket_id != -1";
			exportJdbcResultsetHandler = new JdbcResultsetHandler();
			exportJdbcResultsetHandler.executeStmt(exportQuery, true);
			resultSet = jdbcResultsetHandler.getResultSet();
			exportResultSet = exportJdbcResultsetHandler.getResultSet();
			//user_id--->card_number, client_name-->first_name, contact_name----> last_name, contact_email---->email_id, contact_phone-->phone ,description---> address, created_date---->Opt-in date, user_name-->opt-in source 
			/*query = "SELECT cl.card_number, c.first_name, c.last_name, c.email_id, c.mobile_phone,"+
						" c.address_one, c.address_two, c.city, c.state, c.country, c.zip,"+
						" cl.created_date, cl.contact_loyalty_type, cl.is_registered"+
						" FROM contacts_loyalty cl left join contacts c on cl.contact_id = c.cid "+
						" where cl.user_id = "+ currentUser.getUserId() +" AND is_registered=1"+
						" order by cl.created_date desc";*/
			while(resultSet.next())
			{
				exportResultSet.moveToInsertRow();
					if(indexes[0]==0)	{
						exportResultSet.updateString("user_id", resultSet.getString("card_number"));
					}									
						
					if(indexes[1]==0)	{
						exportResultSet.updateString("client_name", resultSet.getString("first_name"));
					}
					if(indexes[2]==0)	{
						exportResultSet.updateString("contact_name", resultSet.getString("last_name"));
					}
					if(indexes[3]==0)	{
						exportResultSet.updateString("contact_email", resultSet.getString("email_id"));
					}
					if(indexes[4]==0)	{
						exportResultSet.updateString("contact_phone", resultSet.getString("mobile_phone"));
					}
					if(indexes[5]==0)	{
						String addrStr = Constants.STRING_NILL;
						addrStr += (resultSet.getString("address_one")==null||resultSet.getString("address_one").toString().trim().isEmpty()?"": addrStr.trim().length() > 0 ? ","+resultSet.getString("address_one").toString().trim() :resultSet.getString("address_one").toString().trim());
			        	addrStr += (resultSet.getString("address_two")==null||resultSet.getString("address_two").toString().trim().isEmpty()?"": addrStr.trim().length() > 0 ? ","+resultSet.getString("address_two").toString().trim() :resultSet.getString("address_two").toString().trim());
			        	addrStr +=	(resultSet.getString("city")==null||resultSet.getString("city").toString().trim().isEmpty()?"": addrStr.trim().length() > 0 ? ","+resultSet.getString("city").toString().trim() :resultSet.getString("city").toString().trim());
			        	addrStr += (resultSet.getString("state")==null||resultSet.getString("state").toString().trim().isEmpty()?"": addrStr.trim().length() > 0 ? ","+resultSet.getString("state").toString().trim() :resultSet.getString("state").toString().trim());
			        	addrStr += (resultSet.getString("country")==null||resultSet.getString("country").toString().trim().isEmpty()?"": addrStr.trim().length() > 0 ? ","+resultSet.getString("country").toString().trim() :resultSet.getString("country").toString().trim());
			        	addrStr += (resultSet.getString("zip")==null||resultSet.getString("zip").toString().trim().isEmpty()?"": addrStr.trim().length() > 0 ? ","+resultSet.getString("zip").toString().trim() :resultSet.getString("zip").toString().trim());
						exportResultSet.updateString("description", addrStr);
					}
				
				String optInDate = "";
				if(resultSet.getString("created_date") !=null){
				SimpleDateFormat formatter = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
				Date date = null;
				try {
					date = (Date)formatter.parse(resultSet.getString("created_date").toString().trim());
				} catch (ParseException e) {
					logger.error("Exception ::" , e);
				} 
				Calendar cal=Calendar.getInstance();
				cal.setTime(date);
	        	optInDate = MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STYEAR);
				}
				else
				{
					optInDate="";
				}
				if(indexes[6]==0){
				
					exportResultSet.updateString("created_date", optInDate);}
				String optInSource = "";
				
				if(resultSet.getString("contact_loyalty_type") == null) {
					optInSource = "";
				}
				else if(resultSet.getString("contact_loyalty_type").toString().trim().equalsIgnoreCase(Constants.CONTACT_LOYALTY_TYPE_POS)) {
					optInSource = Constants.CONTACT_LOYALTY_TYPE_STORE;
				}
				else {
					optInSource = resultSet.getString("contact_loyalty_type").toString().trim();
				}
				if(indexes[7]==0){
					exportResultSet.updateString("user_name", optInSource);
				}
				//for all registered cards
				if(isRegistered) {
					
					exportResultSet.updateString("status", resultSet.getString("is_registered") != null && resultSet.getString("is_registered").toString().equals("1") ?"Yes" : ""); 
					
				}//if
				exportResultSet.insertRow();
				exportResultSet.moveToCurrentRow();
			}
			OCCSVWriter csvWriter = null;
			try{
				csvWriter = new OCCSVWriter(bw);
				csvWriter.writeAll(exportResultSet, false, 1);
			}catch(Exception e){
				logger.info("Exception occured while writing through csvwriter", e);
			}finally {
				bw.flush();
				csvWriter.flush();
				bw.close();
				csvWriter.close();
				csvWriter = null;
			}
			//yes
			Filedownload.save(file, "text/csv");
			logger.debug("exited");
		} catch (Exception e) {
			logger.error("** Exception while exporting into CSV file: " , e);
		}finally{
			if(jdbcResultsetHandler != null)jdbcResultsetHandler.destroy();
			if(exportJdbcResultsetHandler != null)exportJdbcResultsetHandler.destroy();
			query = null; exportQuery = null; jdbcResultsetHandler = null; exportJdbcResultsetHandler = null;userName = null; usersParentDirectory = null;
			downloadDir = null; filePath = null; sb = null; udfFldsLabel= null; resultSet = null; exportResultSet = null;
			//System.gc();
		}
		logger.fatal("Time taken to export loyalty cards is :::   :: " + (System.currentTimeMillis()-startTime));
	}// exportCSV
	public void anchorEvent(boolean flag) {
		List<Component> chkList = custExport$chkDivId.getChildren();
		Checkbox tempChk = null;
		for (int i = 0; i < chkList.size(); i++) {
			if(!(chkList.get(i) instanceof Checkbox)) continue;
			
			tempChk = (Checkbox)chkList.get(i);
			tempChk.setChecked(flag);
			
		} // for

	}
	public void onClick$selectAllAnchr$custExport() {
		
		anchorEvent(true);

	}

	public void onClick$clearAllAnchr$custExport() {
		
		anchorEvent(false);
	}
public void createWindow()	{
		
		try {
			
			Components.removeAllChildren(custExport$chkDivId);
			
			Checkbox tempChk2 = new Checkbox("Card Number");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			
		
			 tempChk2 = new Checkbox("First Name");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);

			
			 tempChk2 = new Checkbox("Last Name");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);

			 tempChk2 = new Checkbox("Email Address");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			
			 tempChk2 = new Checkbox("Phone Number");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			
				tempChk2 = new Checkbox("Address");
				tempChk2.setSclass("custCheck");
				tempChk2.setParent(custExport$chkDivId);
				
				tempChk2 = new Checkbox("Opt-in Date");
				tempChk2.setSclass("custCheck");
				tempChk2.setParent(custExport$chkDivId);
				
				tempChk2 = new Checkbox("Opt-in Source");
				tempChk2.setSclass("custCheck");
				tempChk2.setParent(custExport$chkDivId);
			
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}

	public void onClick$exportBtnId() {
		try {
			setValues();
			if(!exportFilterLbId.getSelectedItem().getLabel().equalsIgnoreCase("All Inventory Cards"))
			{
			createWindow();
			
			anchorEvent(false);
			
			custExport.setVisible(true);
			custExport.doHighlighted();
			}
			else
			{
				exportCSV("CSV", exportFilterLbId.getSelectedItem().getLabel(), null, null, null, null, null, false);
				return;
			}
			
				
		} catch (Exception e) {
			logger.error("Error occured from the exportCSV method ***",e);
		}
	}// onClick$exportBtnId
	
	private Window lastPurchaseDetailsWinId;
	Rows lastPurchaseDetailsWinId$viewAllSKURowsId;
	
	public void onClick$viewLastPurchaseDetailsAnchId(){
		try {
			if(rowObject != null) {
				lastPurchaseDetailsWinId.doHighlighted();
				lastPurchaseDetailsWinId.setVisible(true);
				
				Components.removeAllChildren(lastPurchaseDetailsWinId$viewAllSKURowsId);
				
				List<Object[]> skuList = retailProSalesDao.getLastPurchaseSKUDetailsByContactId(contactId, lastPurchaseDate, currentUser.getUserId());
				for (Object[] object : skuList) {
					Row row = new Row();
					row.setParent(lastPurchaseDetailsWinId$viewAllSKURowsId);
					
					row.appendChild(new Label(object[1]==null?"":object[1].toString().trim()));
					row.appendChild(new Label(object[2]==null?"":f.format(object[2])));
					row.appendChild(new Label(object[3]==null?"":object[3].toString().trim()));
					row.appendChild(new Label(object[4]==null?"":f.format(object[4])));
				}
			}
			else {
				Messagebox.show("Please select a contact to view last purchase details.", "Error" ,Messagebox.OK, 
						Messagebox.ERROR);
			}
		
		}catch (Exception e) {
			logger.error("Exception while fetching last purchase SKU details.....");
			logger.error("Exception ::" , e);
		}
	}
}

	
