package org.mq.marketer.campaign.controller.campaign;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.DRSMSSent;
import org.mq.marketer.campaign.beans.DRSent;
import org.mq.marketer.campaign.beans.DigitalReceiptMyTemplate;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.UserActivities;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.CampaignSentDao;
import org.mq.marketer.campaign.dao.ClicksDao;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.DRSMSSentDao;
import org.mq.marketer.campaign.dao.DRSentDao;
import org.mq.marketer.campaign.dao.DigitalReceiptMyTemplatesDao;
import org.mq.marketer.campaign.dao.DigitalReceiptUserSettingsDao;
import org.mq.marketer.campaign.dao.DigitalReceiptsJSONDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.OpensDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.LineChartEngine;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.utils.OCCSVWriter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.Chart;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.SimpleCategoryModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

public class DigitalRecieptsReportsController extends GenericForwardComposer {

	private OpensDao opensDao;
	private ClicksDao clicksDao;
	private Listbox reportsPerPageLBId, recipientsReportsPerPageLBId,statusPendingReportsPerPageLBId,reportsSmsPerPageLBId,statusPendingReportsSmsPerPageLBId,recipientsSmsReportsPerPageLBId,storeReportsPerPageLBId;
	private MyDatebox fromDateboxId;
	private MyDatebox toDateboxId;
	private TimeZone clientTimeZone;
	Desktop desktopScope = null;
	Session sessionScope = null;

	
	private Window custExport,statusPendingCustExport,custSmsExport;//custSmsExport1;
	private Div custExport$chkDivId,pendingReportsDivId,pendingReportsSmsDivId,custSmsExport$chkDivId,custSmsExport1$chkDivId,custSmsExpor1t$chkDivId;
	private Listfoot totalStoresDivID, totalRecipientsDivID,totalStatusPendingDivID,smsStatusPendingDivID,totalSmsRecipientsDivID;

	private Users currentUser;
	private DRSentDao drSentDao;
	private DRSMSSentDao drSmsSentDao;
	private Paging reportsLocationsPagingId, storeReportsLocationsPagingId,reportsSmsLocationsPagingId,
			recipientsReportsLocationsPagingId,statusPendingReportsLocationsPagingId,statusPendingReportsSmsLocationsPagingId,
			recipientsSmsReportsLocationsPagingId;
	private Chart drReportsChartId;
	private Window viewAllDrWinId,viewAllSmsDrWinId;
	// private Grid viewAllDrWinId$viewDrGridId;
	private Rows viewAllDrWinId$viewAllDrRowsId,viewAllSmsDrWinId$viewAllSmsDrRowsId;
	private static final Logger logger = LogManager
			.getLogger(Constants.SUBSCRIBER_LOGGER);
	// private ListitemRenderer renderer = new MyRenderer();
	private DigitalReceiptsJSONDao digitalReceiptsJSONDao;
	private DigitalReceiptMyTemplatesDao digitalReceiptMyTemplatesDao;
	private DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao;

	private ContactsDao contactsDao;
	private OrganizationStoresDao organizationStoresDao;
	private ContactsLoyaltyDao contactsLoyaltyDao;
	private RetailProSalesDao retailProSalesDao;
	private POSMappingDao posMappingDao;
	private UsersDao usersDao;
	private JSONObject jsonMainObject;
	private EmailQueueDao emailQueueDao;
	private UserActivitiesDaoForDML userActivitiesDaoForDML = null;
	private Map<String, String> storeNameMap = new HashMap<String, String>();
	private Map<String, String> storeSBSNameMap = new HashMap<String, String>();
	private Bandbox storeBandBoxId;

	// private String key;
	private String fromDate, endDate;
	private Object[] selectedStoreItem = new Object[2];

	public DigitalRecieptsReportsController() {
		desktopScope = Executions.getCurrent().getDesktop();
		sessionScope = Sessions.getCurrent();
		currentUser = GetUser.getUserObj();
		clientTimeZone = (TimeZone) sessionScope.getAttribute("clientTimeZone");
		this.opensDao = (OpensDao) SpringUtil.getBean("opensDao");
		this.clicksDao = (ClicksDao) SpringUtil.getBean("clicksDao");
		this.drSentDao = (DRSentDao) SpringUtil.getBean("drSentDao");
		this.drSmsSentDao = (DRSMSSentDao) SpringUtil.getBean("drSmsSentDao");
		this.digitalReceiptUserSettingsDao = (DigitalReceiptUserSettingsDao) SpringUtil
				.getBean("digitalReceiptUserSettingsDao");
		this.digitalReceiptsJSONDao = (DigitalReceiptsJSONDao) SpringUtil
				.getBean("digitalReceiptsJSONDao");
		this.digitalReceiptMyTemplatesDao = (DigitalReceiptMyTemplatesDao) SpringUtil
				.getBean("digitalReceiptMyTemplatesDao");
		this.contactsDao = (ContactsDao) SpringUtil.getBean("contactsDao");
		this.organizationStoresDao = (OrganizationStoresDao) SpringUtil
				.getBean("organizationStoresDao");
		this.contactsLoyaltyDao = (ContactsLoyaltyDao) SpringUtil
				.getBean("contactsLoyaltyDao");
		this.retailProSalesDao = (RetailProSalesDao) SpringUtil
				.getBean("retailProSalesDao");
		this.posMappingDao = (POSMappingDao) SpringUtil
				.getBean("posMappingDao");
		this.usersDao = (UsersDao) SpringUtil.getBean("usersDao");
		this.emailQueueDao = (EmailQueueDao) SpringUtil
				.getBean("emailQueueDao");
		userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");


		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("e-Receipt Reports", "", style, true);
	}

	private Label sentLblId, deliveredLblId, bouncedLblId, opensLblId,
			clicksLblId, spamLblId, deliveryTimeLbId, deliveryReportLbId, storeReportLbId, totalStoresLbID, totalRecipientsLbID, totalOpensLbID, totalClicksLbID,
			totalSmsOpensLbID, totalSmsClicksLbID,totalSmsRecipientsLbID,
			ntSentSeperator,notSentId,notSentLblId,totalStatusPendingLbID,smsdeliveryReportLbId,smsStatusPendingLbID,
			smsSentLblId,smsDeliveredLblId,smsBouncedLblId,smsOpensLblId,smsClicksLblId,smsNotSentId,smsNotSentLblId,totalSmsStoresLbID;
	private Listbox drReportsLbId, storeReportsLbId, recipientsReportsLbId,statusPendingReportsLbId,drSmsReportsLbId,statusPendingSmsReportsLbId,recipientsSmsReportsLbId;
	private final String DR_TYPE_MONTHS = "Months";
	private final String DR_TYPE_DAYS = "Days";
	private Listbox drStoreLbId,emailSmsControlsLbId;
	private String emailSatusTobeFetched,smsStatusTobeFetched;

	public static Map<String, String> MONTH_MAP = new HashMap<String, String>();

	static {

		MONTH_MAP.put("1", "Jan");
		MONTH_MAP.put("2", "Feb");
		MONTH_MAP.put("3", "Mar");
		MONTH_MAP.put("4", "Apr");
		MONTH_MAP.put("5", "May");
		MONTH_MAP.put("6", "Jun");
		MONTH_MAP.put("7", "Jul");
		MONTH_MAP.put("8", "Aug");
		MONTH_MAP.put("9", "Sep");
		MONTH_MAP.put("10", "Oct");
		MONTH_MAP.put("11", "Nov");
		MONTH_MAP.put("12", "Dec");

	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		try {
			// TODO Auto-generated method stub
			super.doAfterCompose(comp);
			Long userId = GetUser.getUserObj().getUserId();
			if(userActivitiesDaoForDML!=null) {
			UserActivities userActivity = new UserActivities("Visited dr report page", "Visited pages", Calendar.getInstance(),userId );
			userActivitiesDaoForDML.saveOrUpdate(userActivity);
			}

			// Call the default Setting method here it will set Current
			// Date,drawChart etc
			setStoreIds();
			populateEmailSmsControlsLbId();
			defaultSettings();
			reciepientsLifetimeMetrics();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}

	}
	
	
	

	
	
	//sorting hard coded
public String orderby_colName="sent_date",desc_Asc="desc"; 
    
    public void desc2ascasc2desc()
    {
    	if(desc_Asc=="desc")
			desc_Asc="asc";
		else
			desc_Asc="desc";
	
    }
	 
	
	public void onClick$sortbyEmailSentDate() {
		orderby_colName = "sent_date";
		desc2ascasc2desc();	
		getDigiReports(fromDate, endDate, 0,
				reportsLocationsPagingId.getPageSize(), null);
	 }
	 
	 public void onClick$sortbyEmailAddress() {
			orderby_colName = "email_id";
			desc2ascasc2desc();	
			getDigiReports(fromDate, endDate, 0,
					reportsLocationsPagingId.getPageSize(), null);
	}
	
	 public void onClick$sortbySmsSentDate() {
			orderby_colName = "sent_date";
			desc2ascasc2desc();	
			getSMSDigiReports(fromDate, endDate, 0,
					reportsSmsLocationsPagingId.getPageSize(), null);
		 }
	
		 public void onClick$sortbyMobile() {
				orderby_colName = "mobile";
				desc2ascasc2desc();	
				getSMSDigiReports(fromDate, endDate, 0,
						reportsSmsLocationsPagingId.getPageSize(), null);
		}
	
	
	
	
	
	
	
	
	
	
	
	

	private void reciepientsLifetimeMetrics() {

		recipientsReportsLocationsPagingId.setTotalSize(drSentDao
				.findTotalReciepients(currentUser.getUserId(), null));
		recipientsReportsLocationsPagingId.setPageSize(Integer
				.parseInt(recipientsReportsPerPageLBId.getSelectedItem()
						.getLabel()));
		recipientsReportsLocationsPagingId.setActivePage(0);
		recipientsReportsLocationsPagingId.addEventListener("onPaging", this);
		getRecipientReport(0, recipientsReportsLocationsPagingId.getPageSize(), null);
		recipientsSmsReportsLocationsPagingId.setTotalSize(drSmsSentDao
				.findTotalReciepients(currentUser.getUserId(), null));
		recipientsSmsReportsLocationsPagingId.setPageSize(Integer
				.parseInt(recipientsSmsReportsPerPageLBId.getSelectedItem()
						.getLabel()));
		recipientsSmsReportsLocationsPagingId.setActivePage(0);
		recipientsSmsReportsLocationsPagingId.addEventListener("onPaging", this);
		getRecipientSmsReport(0, recipientsSmsReportsLocationsPagingId.getPageSize(), null);
	}

	private void getRecipientReport(int start, int end, String key) {
		int count = recipientsReportsLbId.getItemCount();
		for (; count > 0; count--) {
			recipientsReportsLbId.removeItemAt(count - 1);
		}

		List<DRSent> drList = drSentDao.findRecipientsReports(
				currentUser.getUserId(), start,
				end, key);

		if (drList == null) {
			totalRecipientsDivID.setVisible(false);
			logger.debug("No recipient reports exist");
			return;
		}

		for (DRSent drsenObjects : drList) {

			String email = drsenObjects.getEmailId();
			long opens = drsenObjects.getUniqueOpens();
			long clicks = drsenObjects.getUniqueClicks();
			long sent = drsenObjects.getSentCount();
			long sentId = drsenObjects.getId();

			// DRSent drSent = drSentDao.findDrById(email);
			Listcell lc;
			Listitem li;
			li = new Listitem();

			lc = new Listcell(email);
			lc.setParent(li);

			lc = new Listcell("" + sent);
			lc.setParent(li);

			lc = new Listcell("" + opens);
			lc.setParent(li);

			lc = new Listcell("" + clicks);
			lc.setParent(li);

			Hbox hbox = new Hbox();
			Image img = new Image("/img/theme/preview_icon.png");
			img.setStyle("margin-right:5px;cursor:pointer;");
			img.setTooltiptext("View Sent History");
			img.setAttribute("imageEventName", "viewAll");
			img.addEventListener("onClick", this);
			img.setParent(hbox);

			lc = new Listcell();
			hbox.setParent(lc);

			lc.setParent(li);

			li.setParent(recipientsReportsLbId);
			li.setValue(drsenObjects);
			// li.setAttribute("digiRcptReportsObj", drSent);

		}
		Map<String, Object> obj = drSentDao.findTotalRecipientsReport(currentUser.getUserId(), key);
		totalRecipientsLbID.setValue(obj.get("totalSentCount").toString());
		totalOpensLbID.setValue(obj.get("uniopens").toString());
		totalClicksLbID.setValue(obj.get("uniclicks").toString());
		totalRecipientsDivID.setVisible(true);
	}
	
	private void getRecipientSmsReport(int start, int end, String key) {
		int count = recipientsSmsReportsLbId.getItemCount();
		for (; count > 0; count--) {
			recipientsSmsReportsLbId.removeItemAt(count - 1);
		}

		List<DRSMSSent> drSmsList = drSmsSentDao.findRecipientsReports(
				currentUser.getUserId(), start,
				end, key);

		if (drSmsList == null) {
			totalSmsRecipientsDivID.setVisible(false);
			logger.debug("No recipient reports exist");
			return;
		}

		for (DRSMSSent drSmsSentObjects : drSmsList) {

			String mobile = drSmsSentObjects.getMobile();
			long opens = drSmsSentObjects.getUniqueOpens();
			long clicks = drSmsSentObjects.getUniqueClicks();
			long sent = drSmsSentObjects.getSentCount();
			long sentId = drSmsSentObjects.getId();

			// DRSent drSent = drSentDao.findDrById(email);
			Listcell lc;
			Listitem li;
			li = new Listitem();

			lc = new Listcell(mobile);
			lc.setParent(li);

			lc = new Listcell("" + sent);
			lc.setParent(li);

			lc = new Listcell("" + opens);
			lc.setParent(li);

			lc = new Listcell("" + clicks);
			lc.setParent(li);

			Hbox hbox = new Hbox();
			Image img = new Image("/img/theme/preview_icon.png");
			img.setStyle("margin-right:5px;cursor:pointer;");
			img.setTooltiptext("View Sent History");
			img.setAttribute("imageEventName", "viewAllSms");
			img.addEventListener("onClick", this);
			img.setParent(hbox);

			lc = new Listcell();
			hbox.setParent(lc);

			lc.setParent(li);

			li.setParent(recipientsSmsReportsLbId);
			li.setValue(drSmsSentObjects);

		}
		Map<String, Object> obj = drSmsSentDao.findTotalRecipientsReport(currentUser.getUserId(), key);
		totalSmsRecipientsLbID.setValue(obj.get("totalSentCount").toString());
		totalSmsOpensLbID.setValue(obj.get("uniopens").toString());
		totalSmsClicksLbID.setValue(obj.get("uniclicks").toString());
		totalSmsRecipientsDivID.setVisible(true);
		
	}
	
	private void getStatusPendingReport(int start, int end, String key){
		int count = statusPendingReportsLbId.getItemCount();
		for (; count > 0; count--) {
			statusPendingReportsLbId.removeItemAt(count - 1);
		}

		List<DRSent> drList = drSentDao.findStatusPendingReports(
				currentUser.getUserId(), start,
				end, key);

		if (drList == null) {
			totalStatusPendingDivID.setVisible(false);
			logger.debug("No status pending reports exist");
			return;
		}

		for (DRSent drsenObjects : drList) {

			String email = drsenObjects.getEmailId();
			Calendar sentDate = drsenObjects.getSentDate();

			Listcell lc;
			Listitem li;
			li = new Listitem();

			lc = new Listcell(email);
			lc.setParent(li);
			
			lc = new Listcell(MyCalendar.calendarToString(sentDate,
					MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
			lc.setParent(li);
			
			li.setParent(statusPendingReportsLbId);
			li.setValue(drsenObjects);

		}
		Map<String, Object> obj = drSentDao.findTotalStatusPendingReport(currentUser.getUserId(), fromDate, endDate, key,Constants.DR_STATUS_SUBMITTED);
		if(obj!=null){
		totalStatusPendingLbID.setValue("Total :"+obj.get("totalPendingCount").toString());
		logger.info("total:"+totalStatusPendingLbID.getValue());
		totalStatusPendingDivID.setVisible(true);
		}
	}
	
	private void getSmsStatusPendingReport(int start, int end, String key){
		int count = statusPendingSmsReportsLbId.getItemCount();
		for (; count > 0; count--) {
			statusPendingSmsReportsLbId.removeItemAt(count - 1);
		}

		List<DRSMSSent> drSmsList = drSmsSentDao.findStatusPendingReports(
				currentUser.getUserId(), start,
				end, key);

		if (drSmsList == null) {
			smsStatusPendingDivID.setVisible(false);
			logger.debug("No status pending reports exist");
			return;
		}

		for (DRSMSSent drSmssentObjects : drSmsList) {

			String mobile = drSmssentObjects.getMobile();
			Calendar sentDate = drSmssentObjects.getSentDate();

			Listcell lc;
			Listitem li;
			li = new Listitem();

			lc = new Listcell(mobile);
			lc.setParent(li);
			
			lc = new Listcell(MyCalendar.calendarToString(sentDate,
					MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
			lc.setParent(li);
			
			li.setParent(statusPendingSmsReportsLbId);
			li.setValue(drSmssentObjects);

		}
		Map<String, Object> obj = drSmsSentDao.findTotalStatusPendingReport(currentUser.getUserId(), fromDate, endDate, key,Constants.DR_STATUS_SUBMITTED);
		if(obj!=null){
		smsStatusPendingLbID.setValue("Total :"+obj.get("totalPendingCount").toString());
		logger.info("total:"+smsStatusPendingLbID.getValue());
		smsStatusPendingDivID.setVisible(true);
		}
	}
	private void setStoreIds1() {

		for (int i = drStoreLbId.getItemCount(); i > 1; i--) {

			drStoreLbId.removeItemAt(i);
		}
		
		List<String> dremailStoreList = drSentDao.findAllStores(currentUser
				.getUserId());
		List<String> drsmsStoreList = drSmsSentDao.findAllStores(currentUser
				.getUserId());
		logger.info("email list is"+dremailStoreList);
		logger.info("sms list is"+drsmsStoreList);
		dremailStoreList.addAll(drsmsStoreList);
		List<String> DrStoresList = dremailStoreList.stream().distinct().collect(Collectors.toList());
		logger.info("final list is"+DrStoresList);

		List<OrganizationStores> storeIdList = organizationStoresDao
				.findAllStores(currentUser.getUserOrganization().getUserOrgId());
		Listitem homeStoreItem = null;

		for (OrganizationStores org : storeIdList) {
			storeNameMap.put(org.getHomeStoreId(), org.getStoreName());
			storeSBSNameMap.put(org.getHomeStoreId()+(org.getSubsidiaryId() != null ? 
					Constants.ADDR_COL_DELIMETER+org.getSubsidiaryId():""), org.getStoreName());
		}
		
		Set<String> storeNames = new HashSet<String>();
		outer: for (String storeName : DrStoresList) {
			for (OrganizationStores org : storeIdList) {

				if ( org.getHomeStoreId().equalsIgnoreCase(storeName) && !storeNames.contains(org.getStoreName()) ) {

					homeStoreItem = new Listitem(org.getStoreName(), storeName);

					homeStoreItem.setParent(drStoreLbId);
					storeNames.add(org.getStoreName());
					continue outer;
				}

			}
			homeStoreItem = new Listitem("Store ID " + storeName, storeName);

			homeStoreItem.setParent(drStoreLbId);
		}

	}

	private void setStoreIds() {

		for (int i = drStoreLbId.getItemCount(); i > 1; i--) {

			drStoreLbId.removeItemAt(i);
		}
		
		List<OrganizationStores> storeIdList = organizationStoresDao
				.findAllStores(currentUser.getUserOrganization().getUserOrgId());
		Listitem homeStoreItem = null;

		for (OrganizationStores org : storeIdList) {
		    storeNameMap.put(org.getHomeStoreId(), org.getStoreName());
		    storeSBSNameMap.put(org.getHomeStoreId() + (org.getSubsidiaryId() != null ?
		            Constants.ADDR_COL_DELIMETER + org.getSubsidiaryId() : ""), org.getStoreName());
		    
		    String displayName = org.getStoreName();
		    if (displayName == null) {
		        displayName = "Store ID " + org.getHomeStoreId();
		    }
		    
		    homeStoreItem = new Listitem(displayName, org.getHomeStoreId());
		    homeStoreItem.setParent(drStoreLbId);
		}
	}

	
	// Default Settings(Copied from doAfterCompose) helps to reduce lines of
	// code
	public void defaultSettings() {

		drReportsChartId.setEngine(new LineChartEngine());

		// Default DateSettings
		setDateValues();
		drStoreLbId.setSelectedIndex(0);
		
		
		emailSatusTobeFetched = "'Success','spamreport','dropped','bounce','special_condtion_for_all'";
		smsStatusTobeFetched = "'Delivered','Bounced','special_condtion_for_all'";
		//emailSatusTobeFetched = "'Success','spamreport','dropped','bounce','submitted','special_condtion_for_all'";
		
		
		
		
		populateEmailSmsControlsLbIdAndSetDefaults();
		
		
		emailSmsControlsLbId.selectAll();
		
		int storeSelectedCount = emailSmsControlsLbId.getSelectedCount();
		int storeItemCount = emailSmsControlsLbId.getItemCount();
        if(storeSelectedCount == storeItemCount){
        	storeBandBoxId.setValue("All");
        }
        else if((storeSelectedCount>1) && !(storeSelectedCount == storeItemCount)){
			storeBandBoxId.setValue("Multiple");
		}
        else if((storeSelectedCount==1) && !(storeSelectedCount == storeItemCount)){
        	storeBandBoxId.setValue(emailSmsControlsLbId.getSelectedItem().getLabel());
        }
        else if(emailSmsControlsLbId.getSelectedIndex() == -1){
			storeBandBoxId.setValue("");
		}
		
		
		
		
		
		
		/*
		 * Calendar serverFromDateCal = fromDateboxId.getServerValue(); Calendar
		 * tempClientFromCal = fromDateboxId.getClientValue();
		 * serverFromDateCal.set(Calendar.HOUR_OF_DAY,
		 * serverFromDateCal.get(Calendar
		 * .HOUR_OF_DAY)-tempClientFromCal.get(Calendar.HOUR_OF_DAY));
		 * serverFromDateCal.set(Calendar.MINUTE,
		 * serverFromDateCal.get(Calendar.
		 * MINUTE)-tempClientFromCal.get(Calendar.MINUTE));
		 * serverFromDateCal.set(Calendar.SECOND, 0);
		 * 
		 * 
		 * Calendar serverToDateCal = toDateboxId.getServerValue();
		 * 
		 * Calendar tempClientToCal = toDateboxId.getClientValue();
		 * 
		 * logger.debug("client From :"+tempClientFromCal
		 * +", client To :"+tempClientToCal);
		 * 
		 * //change the time for startDate and endDate in order to consider
		 * right from the // starting time of startDate to ending time of
		 * endDate
		 * 
		 * serverToDateCal.set(Calendar.HOUR_OF_DAY,
		 * 23+serverToDateCal.get(Calendar
		 * .HOUR_OF_DAY)-tempClientToCal.get(Calendar.HOUR_OF_DAY));
		 * serverToDateCal.set(Calendar.MINUTE,
		 * 59+serverToDateCal.get(Calendar.MINUTE
		 * )-tempClientToCal.get(Calendar.MINUTE));
		 * serverToDateCal.set(Calendar.SECOND, 59);
		 * 
		 * String endDate = MyCalendar.calendarToString(serverToDateCal,
		 * MyCalendar.FORMAT_DATETIME_STYEAR);
		 * 
		 * String fromDate =
		 * MyCalendar.calendarToString(fromDateboxId.getServerValue(),
		 * MyCalendar.FORMAT_DATETIME_STYEAR); Calendar endCal =
		 * toDateboxId.getServerValue(); endCal.set(Calendar.HOUR_OF_DAY, 23);
		 * endCal.set(Calendar.MINUTE, 59); endCal.set(Calendar.SECOND, 59);
		 * 
		 * //String endDate =
		 * MyCalendar.calendarToString(toDateboxId.getServerValue(),
		 * MyCalendar.FORMAT_DATETIME_STYEAR);
		 */

		fromDate = MyCalendar.calendarToString(getStartDate(),
				MyCalendar.FORMAT_DATETIME_STYEAR);
		endDate = MyCalendar.calendarToString(getEndDate(),
				MyCalendar.FORMAT_DATETIME_STYEAR);
		String timePeriod = " ("+MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone)+" - "+MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone)+")";
		deliveryTimeLbId.setValue(timePeriod); deliveryReportLbId.setValue(timePeriod); smsdeliveryReportLbId.setValue(timePeriod); storeReportLbId.setValue(timePeriod);
		selectedStoreItem[0] = drStoreLbId.getSelectedItem();
		selectedStoreItem[1] = drStoreLbId.getSelectedIndex();

		totalSize = drSentDao
				.findDRBasedOnDatesForStore(
						currentUser.getUserId(),
						fromDate,
						endDate,
						null,
						selectedStoreItem[1].equals(0) ? null
								: (String) ((Listitem) selectedStoreItem[0])
										.getValue(),emailSatusTobeFetched);
		totalSizeSms = drSmsSentDao
				.findDRBasedOnDatesForStore(
						currentUser.getUserId(),
						fromDate,
						endDate,
						null,
						selectedStoreItem[1].equals(0) ? null
								: (String) ((Listitem) selectedStoreItem[0])
										.getValue(),smsStatusTobeFetched);
		
		getDrDetails(fromDate, endDate, selectedStoreItem[1].equals(0) ? null
				: (String) ((Listitem) selectedStoreItem[0]).getValue());

		getDigiReports(fromDate, endDate, 0,
				reportsLocationsPagingId.getPageSize(), null);
		getSMSDigiReports(fromDate, endDate, 0,
				reportsSmsLocationsPagingId.getPageSize(), null);
		getStoreReports(fromDate, endDate, 0,
				storeReportsLocationsPagingId.getPageSize());
		logger.info("GET_PAGE_SIZE(700) : "+ storeReportsLocationsPagingId.getPageSize());
		getStatusPendingReports(fromDate, endDate, 0,
				statusPendingReportsLocationsPagingId.getPageSize());
		getSmsStatusPendingReports(fromDate, endDate, 0,
				statusPendingReportsSmsLocationsPagingId.getPageSize());
		
		storeSize = jrsh.totalRecordsSize();/*drSentDao.findStoreCountBasedOnDates(
				currentUser.getUserId(), fromDate, endDate, null,emailSatusTobeFetched);*/
		pageSize = Integer.parseInt(reportsPerPageLBId.getSelectedItem()
				.getLabel());
		pageSizeSms = Integer.parseInt(reportsSmsPerPageLBId.getSelectedItem()
				.getLabel());
		pendingReportsPageSize = Integer.parseInt(statusPendingReportsPerPageLBId.getSelectedItem()
				.getLabel());
		pendingSmsReportsPageSize = Integer.parseInt(statusPendingReportsSmsPerPageLBId.getSelectedItem()
				.getLabel());
		
		/*List<DRSent> drSentList = drSentDao
		.findDrReports(
				currentUser.getUserId(),
				-1,
				-1,
				fromDate,
				endDate,
				null,
				selectedStoreItem[1].equals(0) ? null
						: (String) ((Listitem) selectedStoreItem[0])
								.getValue(),emailSatusTobeFetched,orderby_colName,desc_Asc);
		List<DRSMSSent> drSmsSentList = drSmsSentDao
				.findDrReports(
						currentUser.getUserId(),
						-1,
						-1,
						fromDate,
						endDate,
						null,
						selectedStoreItem[1].equals(0) ? null
								: (String) ((Listitem) selectedStoreItem[0])
										.getValue(),smsStatusTobeFetched,orderby_colName,desc_Asc);
		List<DRSent> drStatusPendingList = drSentDao
				.findPenidngReports(
						currentUser.getUserId(),
						-1,
						-1,
						fromDate,
						endDate,
						null,
						selectedStoreItem[1].equals(0) ? null
								: (String) ((Listitem) selectedStoreItem[0])
										.getValue(),Constants.DR_STATUS_SUBMITTED,orderby_colName,desc_Asc);
		List<DRSMSSent> drSmsStatusPendingList = drSmsSentDao
				.findPenidngReports(
						currentUser.getUserId(),
						-1,
						-1,
						fromDate,
						endDate,
						null,
						selectedStoreItem[1].equals(0) ? null
								: (String) ((Listitem) selectedStoreItem[0])
										.getValue(),Constants.DR_STATUS_SUBMITTED,orderby_colName,desc_Asc);
		*/
		Map<String, Object> obj = drSentDao.findTotalStatusPendingReport(currentUser.getUserId(), fromDate, endDate, 
				null,Constants.DR_STATUS_SUBMITTED);
		Map<String, Object> obj1 = drSentDao.findTotalStatusPendingReport(currentUser.getUserId(), fromDate, endDate, null,Constants.DR_STATUS_SUBMITTED);
		
		reportsLocationsPagingId.setTotalSize(totalSize );
		reportsSmsLocationsPagingId.setTotalSize(totalSizeSms);
		storeReportsLocationsPagingId.setTotalSize(storeSize);

		reportsLocationsPagingId.setPageSize(pageSize);
		reportsSmsLocationsPagingId.setPageSize(pageSizeSms);
		storeReportsLocationsPagingId.setPageSize(10);
		
		statusPendingReportsLocationsPagingId.setTotalSize(obj1 != null ? Integer.parseInt(obj1.get("totalPendingCount").toString()) : 0);
		statusPendingReportsLocationsPagingId.setPageSize(pendingReportsPageSize);
		statusPendingReportsLocationsPagingId.setActivePage(0);
		statusPendingReportsLocationsPagingId.addEventListener("onPaging", this);
		
		statusPendingReportsSmsLocationsPagingId.setTotalSize(obj != null ? Integer.parseInt(obj.get("totalPendingCount").toString()) : 0);
		statusPendingReportsSmsLocationsPagingId.setPageSize(pendingSmsReportsPageSize);
		statusPendingReportsSmsLocationsPagingId.setActivePage(0);
		statusPendingReportsSmsLocationsPagingId.addEventListener("onPaging", this);
		reportsLocationsPagingId.setActivePage(0);
		reportsSmsLocationsPagingId.setActivePage(0);
		storeReportsLocationsPagingId.setActivePage(0);
		reportsLocationsPagingId.addEventListener("onPaging", this);
		reportsSmsLocationsPagingId.addEventListener("onPaging", this);
		storeReportsLocationsPagingId.addEventListener("onPaging", this);

		
		long totalStoreSize = drSentDao.findTotalStorePurchases(currentUser.getUserId(), fromDate, endDate, null,emailSatusTobeFetched);
//		if(totalStoreSize !=0 ){
//			totalStoresDivID.setVisible(true);
//			totalStoresLbID.setValue(totalStoreSize+"");
//		}
		/*TESTING-SMS-RECEIPTS*/
		long totalSmsStoreSize = drSmsSentDao.findTotalStorePurchases(currentUser.getUserId(), fromDate, endDate, null,smsStatusTobeFetched);
		logger.info("793AAABBBCCCDDD ===>>> :" + totalSmsStoreSize);
//		if(totalSmsStoreSize !=0 ){
//			totalSmsStoresLbID.setVisible(true);
//			totalSmsStoresLbID.setValue(totalSmsStoreSize+"");
//		}
		if (totalStoreSize != 0 || totalSmsStoreSize != 0) {
			totalStoresDivID.setVisible(true);
			totalStoresLbID.setValue(totalStoreSize+"");
			totalSmsStoresLbID.setValue(totalSmsStoreSize + "");
		}else{
			totalStoresDivID.setVisible(false);
		}
		
	//	 pendingReportsDivId.setVisible(statusPendingReportsLocationsPagingId.getTotalSize() > 0);
	//	 pendingReportsSmsDivId.setVisible(statusPendingReportsSmsLocationsPagingId.getTotalSize() > 0);
		
		drawChart();

	}

	public Calendar getStartCalendar() {
		Calendar cal = null;

		if (fromDate != null) {
			cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				cal.setTime(sdf.parse(fromDate));
			} catch (ParseException e) {

				logger.error("Exception in setTime() method", e);

			}
		}
		return cal;

	}

	public Calendar getEndCalendar() {
		Calendar cal = null;

		if (endDate != null) {
			cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				cal.setTime(sdf.parse(endDate));
			} catch (ParseException e) {

				logger.error("Exception in setTime() method", e);

			}
		}
		return cal;

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
		// String fromDate = MyCalendar.calendarToString(serverFromDateCal,
		// MyCalendar.FORMAT_DATETIME_STYEAR);

		return serverFromDateCal;

	}

	public Calendar getEndDate() {

		Calendar serverToDateCal = toDateboxId.getServerValue();

		Calendar tempClientToCal = toDateboxId.getClientValue();

		// change the time for startDate and endDate in order to consider right
		// from the
		// starting time of startDate to ending time of endDate

		serverToDateCal.set(Calendar.HOUR_OF_DAY,
				23 + serverToDateCal.get(Calendar.HOUR_OF_DAY)
						- tempClientToCal.get(Calendar.HOUR_OF_DAY));
		serverToDateCal.set(
				Calendar.MINUTE,
				59 + serverToDateCal.get(Calendar.MINUTE)
						- tempClientToCal.get(Calendar.MINUTE));
		serverToDateCal.set(Calendar.SECOND, 59);

		String endDate = MyCalendar.calendarToString(serverToDateCal,
				MyCalendar.FORMAT_DATETIME_STYEAR);

		return serverToDateCal;

	}

	public void getDrDetails(String fromDate, String endDate, String store) {

		/*
		 * String fromDate =
		 * MyCalendar.calendarToString(fromDateboxId.getServerValue(),
		 * MyCalendar.FORMAT_DATETIME_STYEAR);
		 * 
		 * Calendar endCal = toDateboxId.getServerValue();
		 * endCal.set(Calendar.HOUR_OF_DAY, 23); endCal.set(Calendar.MINUTE,
		 * 59); endCal.set(Calendar.SECOND, 59);
		 * 
		 * String endDate = MyCalendar.calendarToString(endCal,
		 * MyCalendar.FORMAT_DATETIME_STYEAR);
		 */
		// total sent count
		long sentcount = drSentDao.findTotSentCount(currentUser.getUserId(),
				fromDate, endDate, store);
		sentLblId.setValue("" + sentcount);
		//not sent (submitted) count
		long notSentCount=drSentDao.findTotNotSentCount(currentUser.getUserId(), fromDate, endDate, store);
		if(statusPendingChkId.isChecked() && notSentCount>0){
		notSentLblId.setValue("" + notSentCount);
		//ntSentSeperator.setVisible(true);
		//ntSentSeperator.setVisible(true);
		notSentId.setVisible(true);
		notSentLblId.setVisible(true);
		}
		// bounced count
		long bounceCount = drSentDao.findTotBounceCount(
				currentUser.getUserId(), fromDate, endDate, store);
		bouncedLblId.setValue("" + bounceCount);

		// delivered count
		// long actualSent = sentcount-bounceCount;
		long delCount = drSentDao.findTotDelCount(currentUser.getUserId(),
				fromDate, endDate, store);
		deliveredLblId.setValue(delCount + "("
				+ getPercentage(delCount, sentcount) + "%)");

		long openCount = drSentDao.findOpenCount(currentUser.getUserId(),
				fromDate, endDate, store);

		// unique open count
		opensLblId.setValue(openCount + "("
				+ getPercentage(openCount, delCount) + "%)");

		// unique click
		long clickCount = drSentDao.findClickCount(currentUser.getUserId(),
				fromDate, endDate, store);

		clicksLblId.setValue(clickCount + "("
				+ getPercentage(clickCount, openCount) + "%)");
		// spam count
		spamLblId.setValue("0");
		long spamCount = drSentDao.findTotSpammedCount(currentUser.getUserId(),
				fromDate, endDate, store);
		if (spamCount > 0) {
			spamLblId.setValue("" + spamCount);
		}

		long smsSentCount = drSmsSentDao.findTotSentCount(currentUser.getUserId(),fromDate,endDate,store);
		smsSentLblId.setValue(""+smsSentCount);
		//not sent (submitted) count
		
				long smsNotSentCount=drSmsSentDao.findTotNotSentCount(currentUser.getUserId(), fromDate, endDate, store);
				logger.info("smsNotSentCountvalue is--------> "+smsNotSentCount);
				if(statusPendingChkId.isChecked() && smsNotSentCount>=0){
				smsNotSentLblId.setValue("" + smsNotSentCount);
				smsNotSentId.setVisible(true);
				smsNotSentLblId.setVisible(true);
				}
				// bounced count
				long smsBounceCount = drSmsSentDao.findTotBounceCount(
						currentUser.getUserId(), fromDate, endDate, store);
				smsBouncedLblId.setValue("" + smsBounceCount);
		// delivered count
				long smsDelCount = drSmsSentDao.findTotDelCount(currentUser.getUserId(),
						fromDate, endDate, store);
				smsDeliveredLblId.setValue(smsDelCount + "("
						+ getPercentage(smsDelCount, smsSentCount) + "%)");

				long smsOpenCount = drSmsSentDao.findOpenCount(currentUser.getUserId(),
						fromDate, endDate, store);

				// unique open count
				smsOpensLblId.setValue(smsOpenCount + "("
						+ getPercentage(smsOpenCount, smsDelCount) + "%)");

				// unique click
				long smsClickCount = drSmsSentDao.findClickCount(currentUser.getUserId(),
						fromDate, endDate, store);

				smsClicksLblId.setValue(smsClickCount + "("
						+ getPercentage(smsClickCount, smsOpenCount) + "%)");
	}

	public void setDateValues() {

		Calendar cal = MyCalendar.getNewCalendar();
		toDateboxId.setValue(cal);
		logger.debug("ToDate (server) :" + cal);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
		logger.debug("FromDate (server) :" + cal);
		fromDateboxId.setValue(cal);

		/*
		 * Calendar toCal = MyCalendar.getNewCalendar(); //
		 * toCal.setTimeZone(clientTimeZone); toDateboxId.setValue(toCal);
		 * toDateboxId.setConstraint("no future");
		 * 
		 * Calendar fromCal = MyCalendar.getNewCalendar(); //
		 * fromCal.setTimeZone(clientTimeZone);
		 * 
		 * fromCal.set(Calendar.MONTH, fromCal.get(fromCal.MONTH) - 1);
		 * fromCal.set(Calendar.DATE, fromCal.get(fromCal.DATE) + 1);
		 * 
		 * fromDateboxId.setValue(fromCal);
		 * fromDateboxId.setConstraint("no future");
		 */
	}

	private int pageSize = 0;
	private int storePageSize = 0;
	private int pageSizeSms = 0;

	int totalSize = 0;
	int storeSize = 0;
	int storeReportSize = 0;
	int pendingReportsPageSize=0;
	int pendingSmsReportsPageSize=0;
	int totalSizeSms=0;

	JdbcResultsetHandler jrsh = null;
	
	private void getStoreReports(String fromDate, String endDate,
			int startIndex, int endIndex) {

		int count = storeReportsLbId.getItemCount();
		for (; count > 0; count--) {
			storeReportsLbId.removeItemAt(count - 1);
		}
		/*String store = selectedStoreItem[1].equals(0) ? null
				: (String) ((Listitem) selectedStoreItem[0]).getValue();*/
		
		JdbcResultsetHandler jh = null;
		try {

			jh = new JdbcResultsetHandler();
			jh.executeStmt(getStoreReportsQuery()+" LIMIT "+startIndex +", " +endIndex , true);
			logger.info("QUERY ===>>> : "+ getStoreReportsQuery());
			jrsh = new JdbcResultsetHandler();
			jrsh.executeStmt(getStoreReportsQuery(), true);
			storeReportSize = jrsh.totalRecordsSize();
			
			ResultSet resultSet = jh.getResultSet();

			logger.info("STORE_REPORTS_SIZE : "+ storeReportSize);
			int recordsCount = 0;
			while (resultSet.next()) {
				Listcell lc;
				Listitem li;
				li = new Listitem();
				logger.info("--------->>> : "+ resultSet.getString("SBS_no"));
				StringBuffer sb = new StringBuffer();
				String storeName = resultSet
						.getString("SBS_no") == null ?( storeNameMap.containsKey(resultSet.getString("store_number")) ? storeNameMap
								.get(resultSet.getString("store_number")) : resultSet.getString("store_number") == null ? "--" 
										: "Store ID " + resultSet.getString("store_number")) : 
							(storeSBSNameMap.containsKey(resultSet.getString("store_number")+Constants.ADDR_COL_DELIMETER+resultSet.getString("SBS_no") ) ? 
									storeSBSNameMap.get(resultSet.getString("store_number")+Constants.ADDR_COL_DELIMETER+resultSet.getString("SBS_no"))
									:  "Store ID " + resultSet.getString("store_number") );
				lc = new Listcell(storeName);
				lc.setParent(li);
				lc = new Listcell(resultSet.getString("emailCount"));
				lc.setParent(li);
				lc = new Listcell(resultSet.getString("smsCount"));
				lc.setParent(li);
				li.setParent(storeReportsLbId);
			}
		} catch(Exception e) {logger.info("Exception : ", e);}

		/*
		List<Object[]> drList = drSentDao.findStoreReports(
				currentUser.getUserId(), startIndex, endIndex, fromDate,
				endDate, store,emailSatusTobeFetched );

		if (drList == null) {
			logger.debug("No store reports exist");
			return;
		}

		for (Object[] eachStore : drList) {
			Listcell lc;
			Listitem li;
			li = new Listitem();

			String StoreName = eachStore[2] == null ?( storeNameMap.containsKey(eachStore[0]) ? storeNameMap
					.get(eachStore[0]) : "Store ID " + eachStore[0]) : 
						(storeSBSNameMap.containsKey(eachStore[0]+Constants.ADDR_COL_DELIMETER+eachStore[2] ) ? 
								storeSBSNameMap.get(eachStore[0]+Constants.ADDR_COL_DELIMETER+eachStore[2]):  "Store ID " + eachStore[0] );
			lc = new Listcell(
					StoreName);
			lc.setParent(li);

			lc = new Listcell("" + eachStore[1]);
			lc.setParent(li);

			li.setParent(storeReportsLbId);

		} */
	}
	
	private void getStatusPendingReports(String fromDate, String endDate,
			int startIndex, int endIndex) {

		int count = statusPendingReportsLbId.getItemCount();
		for (; count > 0; count--) {
			statusPendingReportsLbId.removeItemAt(count - 1);
		}
		String store = selectedStoreItem[1].equals(0) ? null
				: (String) ((Listitem) selectedStoreItem[0]).getValue();

		List<Object[]> drList = drSentDao.findDeliveryStatusPendingReports(
				currentUser.getUserId(), startIndex, endIndex, fromDate,
				endDate, store,Constants.DR_STATUS_SUBMITTED );
		logger.info(" "+drList.getClass());
		if (drList == null) {
			logger.debug("No store reports exist");
			return;
		}

		for (Object[] eachEmail : drList) {
			Listcell lc;
			Listitem li;
			li = new Listitem();

			lc = new Listcell(""+eachEmail[0]);
			lc.setParent(li);
			lc = new Listcell(""+MyCalendar.calendarToString((Calendar)eachEmail[1],
					MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
			lc.setParent(li);

			li.setParent(statusPendingReportsLbId);

		}
		Map<String, Object> obj = drSentDao.findTotalStatusPendingReport(currentUser.getUserId(), fromDate, endDate, null,Constants.DR_STATUS_SUBMITTED);
		if(obj!=null){
		totalStatusPendingLbID.setValue("Total :"+obj.get("totalPendingCount").toString());
		totalStatusPendingDivID.setVisible(true);
		}
	}

	private void getSmsStatusPendingReports(String fromDate, String endDate,
			int startIndex, int endIndex) {

		int count = statusPendingSmsReportsLbId.getItemCount();
		for (; count > 0; count--) {
			statusPendingSmsReportsLbId.removeItemAt(count - 1);
		}
		String store = selectedStoreItem[1].equals(0) ? null
				: (String) ((Listitem) selectedStoreItem[0]).getValue();

		List<Object[]> drSmsList = drSmsSentDao.findDeliveryStatusPendingReports(
				currentUser.getUserId(), startIndex, endIndex, fromDate,
				endDate, store,Constants.DR_STATUS_SUBMITTED );
		logger.info(" "+drSmsList.getClass());
		if (drSmsList == null) {
			logger.debug("No store reports exist");
			return;
		}

		for (Object[] eachMobile : drSmsList) {
			Listcell lc;
			Listitem li;
			li = new Listitem();

			lc = new Listcell(""+eachMobile[0]);
			lc.setParent(li);
			lc = new Listcell(""+MyCalendar.calendarToString((Calendar)eachMobile[1],
					MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
			lc.setParent(li);

			li.setParent(statusPendingSmsReportsLbId);

		}
		Map<String, Object> obj = drSmsSentDao.findTotalStatusPendingReport(currentUser.getUserId(), fromDate, endDate, null,Constants.DR_STATUS_SUBMITTED);
		if(obj!=null){
		smsStatusPendingLbID.setValue("Total :"+obj.get("totalPendingCount").toString());
		smsStatusPendingDivID.setVisible(true);
		}
	}
	private void getDigiReports(String fromDate, String endDate,
			int startIndex, int endIndex, String key) {

		/*
		 * String fromDate =
		 * MyCalendar.calendarToString(fromDateboxId.getServerValue(),
		 * MyCalendar.FORMAT_DATETIME_STYEAR); Calendar endCal =
		 * toDateboxId.getServerValue(); endCal.set(Calendar.HOUR_OF_DAY, 23);
		 * endCal.set(Calendar.MINUTE, 59); endCal.set(Calendar.SECOND, 59);
		 * 
		 * String endDate = MyCalendar.calendarToString(endCal,
		 * MyCalendar.FORMAT_DATETIME_STYEAR, clientTimeZone);
		 */

		
		try{
			logger.info("storeNameMap >>> "+storeNameMap);
		}catch(Exception e){
			
		}
			
		
		
		int count = drReportsLbId.getItemCount();
		for (; count > 0; count--) {
			drReportsLbId.removeItemAt(count - 1);
		}

		// getDrDetails();

		List<DRSent> drList = drSentDao
				.findDrReports(
						currentUser.getUserId(),
						startIndex,
						endIndex,
						fromDate,
						endDate,
						key,
						selectedStoreItem[1].equals(0) ? null
								: (String) ((Listitem) selectedStoreItem[0])
										.getValue(),emailSatusTobeFetched,orderby_colName,desc_Asc);

		if (drList == null) {
			logger.debug("No digital reports exist");
			return;
		}

		for (DRSent drsenObjects : drList) {

			String email = drsenObjects.getEmailId();
			Calendar sentDate = drsenObjects.getSentDate();

			String status = drsenObjects.getStatus();

			long sentId = drsenObjects.getId();

			if (status != null)
				if (status.equalsIgnoreCase(Constants.DR_STATUS_DROPPED)
						|| status.equalsIgnoreCase(Constants.DR_STATUS_BOUNCE)) {
					status = Constants.DR_STATUS_BOUNCED;
				}
				/*if(status.equalsIgnoreCase(Constants.DR_STATUS_SUBMITTED)){
					status = "Not sent";
				}*/

			// DRSent drSent = drSentDao.findDrById(email);
			Listcell lc;
			Listitem li;
			li = new Listitem();

			lc = new Listcell(MyCalendar.calendarToString(sentDate,
					MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
			lc.setParent(li);

			lc = new Listcell(email);
			lc.setParent(li);

			/*lc = new Listcell(status);
			lc.setParent(li);*/
			String StoreName = drsenObjects.getSbsNumber() == null ?( storeNameMap.containsKey(drsenObjects
					.getStoreNumber()) ? storeNameMap.get(drsenObjects
					.getStoreNumber())
					: drsenObjects.getStoreNumber() == null ? "--"
							: "Store ID " + drsenObjects.getStoreNumber()) : 
						(storeSBSNameMap.containsKey(drsenObjects
								.getStoreNumber()+Constants.ADDR_COL_DELIMETER+drsenObjects
								.getSbsNumber() ) ? 
								storeSBSNameMap.get(drsenObjects.getStoreNumber()+Constants.ADDR_COL_DELIMETER+drsenObjects
										.getSbsNumber() ):  "Store ID " + drsenObjects.getStoreNumber() );

					lc = new Listcell(StoreName);
			
			/*lc = new Listcell(storeNameMap.containsKey(drsenObjects
					.getStoreNumber()) ? storeNameMap.get(drsenObjects
					.getStoreNumber())
					: drsenObjects.getStoreNumber() == null ? "--"
							: "Store ID " + drsenObjects.getStoreNumber());*/
			lc.setParent(li);
			
			lc = new Listcell(status);
			if(drsenObjects.getStatus().equalsIgnoreCase(Constants.DR_STATUS_SUBMITTED)) {
				lc.setLabel(Constants.DR_STATUS_PENDING);
			}
			else{
			lc.setLabel(status);
				
			}
			lc.setParent(li);
			
			lc = new Listcell((drsenObjects.getOpens() == 0 ? "0":drsenObjects.getOpens())+"");
			lc.setParent(li);
			
			lc = new Listcell((drsenObjects.getClicks() == 0 ? "0":drsenObjects.getClicks())+"");
			lc.setParent(li);
			
			lc = new Listcell((drsenObjects.getSentCount() == 0 ? "0" : drsenObjects.getSentCount()) + "");
			lc.setParent(li);

			Hbox hbox = new Hbox();
			Image img = new Image(
					"/img/digi_receipt_Icons/View-receipt_icn.png");
			img.setStyle("margin-right:5px;cursor:pointer;");
			img.setTooltiptext("View Sent Email");
			img.setAttribute("imageEventName", "view");
			img.addEventListener("onClick", this);
			img.setParent(hbox);

			lc = new Listcell();
			hbox.setParent(lc);

			lc.setParent(li);

			li.setParent(drReportsLbId);
			li.setValue(drsenObjects);
			//li.setValue(email);
			// li.setAttribute("digiRcptReportsObj", drSent);

		}
		logger.info("drReportsLbId.getItemCount()"+drReportsLbId.getItemCount());
	}

	private void getSMSDigiReports(String fromDate, String endDate,
			int startIndex, int endIndex, String key) {

		try{
			logger.info("storeNameMap >>> "+storeNameMap);
		}catch(Exception e){
			
		}
			
		
		
		int count = drSmsReportsLbId.getItemCount();
		for (; count > 0; count--) {
			drSmsReportsLbId.removeItemAt(count - 1);
		}

		List<DRSMSSent> drSmsList = drSmsSentDao
				.findDrReports(
						currentUser.getUserId(),
						startIndex,
						endIndex,
						fromDate,
						endDate,
						key,
						selectedStoreItem[1].equals(0) ? null
								: (String) ((Listitem) selectedStoreItem[0])
										.getValue(),smsStatusTobeFetched,orderby_colName,desc_Asc);

		if (drSmsList == null) {
			logger.debug("No digital reports exist");
			return;
		}

		for (DRSMSSent drSmsSentObjects : drSmsList) {

			String mobile = drSmsSentObjects.getMobile();
			Calendar sentDate = drSmsSentObjects.getSentDate();

			String status = drSmsSentObjects.getStatus();

			long sentId = drSmsSentObjects.getId();

			if (status != null)
				if (status.equalsIgnoreCase(Constants.DR_STATUS_DROPPED)
						|| status.equalsIgnoreCase(Constants.DR_STATUS_BOUNCE)) {
					status = Constants.DR_STATUS_BOUNCED;
				}

			Listcell lc;
			Listitem li;
			li = new Listitem();

			lc = new Listcell(MyCalendar.calendarToString(sentDate,
					MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
			lc.setParent(li);

			lc = new Listcell(mobile);
			lc.setParent(li);

			
			String StoreName = drSmsSentObjects.getSbsNumber() == null ?( storeNameMap.containsKey(drSmsSentObjects
					.getStoreNumber()) ? storeNameMap.get(drSmsSentObjects
					.getStoreNumber())
					: drSmsSentObjects.getStoreNumber() == null ? "--"
							: "Store ID " + drSmsSentObjects.getStoreNumber()) : 
						(storeSBSNameMap.containsKey(drSmsSentObjects
								.getStoreNumber()+Constants.ADDR_COL_DELIMETER+drSmsSentObjects
								.getSbsNumber() ) ? 
								storeSBSNameMap.get(drSmsSentObjects.getStoreNumber()+Constants.ADDR_COL_DELIMETER+drSmsSentObjects
										.getSbsNumber() ):  "Store ID " + drSmsSentObjects.getStoreNumber() );
			
			lc = new Listcell(StoreName);

			
//			lc = new Listcell(storeNameMap.containsKey(drSmsSentObjects
//					.getStoreNumber()) ? storeNameMap.get(drSmsSentObjects
//					.getStoreNumber())
//					: drSmsSentObjects.getStoreNumber() == null ? "--"
//							: "Store ID " + drSmsSentObjects.getStoreNumber());
			lc.setParent(li);
			
			lc = new Listcell(status);
			if(drSmsSentObjects.getStatus().equalsIgnoreCase(Constants.DR_STATUS_SUBMITTED)) {
				lc.setLabel(Constants.DR_STATUS_PENDING);
			}
			else{//lc = new Listcell(status);
			lc.setLabel(status);
				//lc.setParent(li)  ;
			}
			lc.setParent(li);
			lc = new Listcell((drSmsSentObjects.getOpens() == 0 ? "0":drSmsSentObjects.getOpens())+"");
			lc.setParent(li);
			
			lc = new Listcell((drSmsSentObjects.getClicks() == 0 ? "0":drSmsSentObjects.getClicks())+"");
			lc.setParent(li);
			
			lc = new Listcell((drSmsSentObjects.getSentCount() == 0 ? "0":drSmsSentObjects.getSentCount())+"");
			lc.setParent(li);
			

			Hbox hbox = new Hbox();
			Image img = new Image(
					"/img/digi_receipt_Icons/View-receipt_icn.png");
			img.setStyle("margin-right:5px;cursor:pointer;");
			img.setTooltiptext("View Sent SMS");
			img.setAttribute("imageEventName", "viewSms");
			img.addEventListener("onClick", this);
			img.setParent(hbox);

			lc = new Listcell();
			hbox.setParent(lc);

			lc.setParent(li);

			li.setParent(drSmsReportsLbId);
			li.setValue(drSmsSentObjects);

		}
		logger.info("drSmsReportsLbId.getItemCount()"+drSmsReportsLbId.getItemCount());
	}
	public void onSelect$recipientsReportsPerPageLBId() {

		try {

			//recipientsSearchBoxId.setText("");

			int tempCount = Integer.parseInt(recipientsReportsPerPageLBId
					.getSelectedItem().getLabel());
			recipientsReportsLocationsPagingId.setPageSize(tempCount);
			/*recipientsReportsLocationsPagingId.setTotalSize(drSentDao
					.findTotalReciepients(currentUser.getUserId(), recipientsSearchBoxId.getText() != null && recipientsSearchBoxId.getText().trim().length() > 0 ? recipientsSearchBoxId.getText().trim():null));*/
			getRecipientReport(0,recipientsReportsLocationsPagingId.getPageSize(), recipientsSearchBoxId.getText() != null && recipientsSearchBoxId.getText().trim().length() > 0 ? recipientsSearchBoxId.getText().trim():null);

		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}

	}
	
	public void onSelect$recipientsSmsReportsPerPageLBId() {

		try {

			int tempCount = Integer.parseInt(recipientsSmsReportsPerPageLBId
					.getSelectedItem().getLabel());
			recipientsSmsReportsLocationsPagingId.setPageSize(tempCount);
			getRecipientSmsReport(0,recipientsSmsReportsLocationsPagingId.getPageSize(), recipientsSmsSearchBoxId.getText() != null && recipientsSmsSearchBoxId.getText().trim().length() > 0 ? recipientsSmsSearchBoxId.getText().trim():null);

		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}

	}
	public void onSelect$statusPendingReportsPerPageLBId() {

		try {

			//recipientsSearchBoxId.setText("");

			int tempCount = Integer.parseInt(statusPendingReportsPerPageLBId
					.getSelectedItem().getLabel());
			statusPendingReportsLocationsPagingId.setPageSize(tempCount);
			getStatusPendingReport(0,statusPendingReportsLocationsPagingId.getPageSize(), statusPendingSearchBoxId.getText() != null && statusPendingSearchBoxId.getText().trim().length() > 0 ? statusPendingSearchBoxId.getText().trim():null);

		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}

	}
	public void onSelect$statusPendingReportsSmsPerPageLBId() {

		try {

			int tempCount = Integer.parseInt(statusPendingReportsSmsPerPageLBId
					.getSelectedItem().getLabel());
			statusPendingReportsSmsLocationsPagingId.setPageSize(tempCount);
			getSmsStatusPendingReport(0,statusPendingReportsSmsLocationsPagingId.getPageSize(), statusPendingSmsSearchBoxId.getText() != null && statusPendingSmsSearchBoxId.getText().trim().length() > 0 ? statusPendingSmsSearchBoxId.getText().trim():null);

		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}

	}

	public void onSelect$reportsSmsPerPageLBId() {

		try {

			int tempCount = Integer.parseInt(reportsSmsPerPageLBId
					.getSelectedItem().getLabel());
			reportsSmsLocationsPagingId.setPageSize(tempCount);
						setValues();
			
			getSMSDigiReports(fromDate, endDate, 0, tempCount, searchSmsBoxId.getValue() != null && searchSmsBoxId.getValue().trim().length() >0 ?searchSmsBoxId.getValue().trim():null);

		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}

	}// onSelect$pageSizeLbId()

	public void onSelect$reportsPerPageLBId() {

		try {

			int tempCount = Integer.parseInt(reportsPerPageLBId
					.getSelectedItem().getLabel());
			reportsLocationsPagingId.setPageSize(tempCount);
			// Utility.refreshModel(drReportsLbELObj, 0, true);

			// fromDate = MyCalendar.calendarToString(getStartDate(),
			// MyCalendar.FORMAT_DATETIME_STYEAR);
			/*
			 * Calendar endCal = toDateboxId.getServerValue();
			 * endCal.set(Calendar.HOUR_OF_DAY, 23); endCal.set(Calendar.MINUTE,
			 * 59); endCal.set(Calendar.SECOND, 59);
			 */

			// endDate = MyCalendar.calendarToString(getEndDate(),
			// MyCalendar.FORMAT_DATETIME_STYEAR);
			setValues();
			/*totalSize = drSentDao.findDRBasedOnDatesForStore(currentUser
					.getUserId(), fromDate, endDate, null, selectedStoreItem[1]
					.equals(0) ? null
					: (String) ((Listitem) selectedStoreItem[0]).getValue());
			reportsLocationsPagingId.setTotalSize(totalSize);*/
			getDigiReports(fromDate, endDate, 0, tempCount, searchBoxId.getValue() != null && searchBoxId.getValue().trim().length() >0 ?searchBoxId.getValue().trim():null);

		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}

	}// onSelect$pageSizeLbId()

	public void onClick$datesFilterBtnId() {

		/*
		 * Calendar fromCal=fromDateboxId.getServerValue(); String fromDate =
		 * MyCalendar.calendarToString(fromCal,2
		 * MyCalendar.FORMAT_DATETIME_STYEAR); Calendar endCal =
		 * toDateboxId.getServerValue(); endCal.set(Calendar.HOUR_OF_DAY, 23);
		 * endCal.set(Calendar.MINUTE, 59); endCal.set(Calendar.SECOND, 59);
		 */
		notSentId.setVisible(false);
		notSentLblId.setVisible(false);
		pendingReportsDivId.setVisible(false);
		pendingReportsSmsDivId.setVisible(false);
		Calendar fromCal = getStartDate();
		Calendar endCal = getEndDate();
		if (endCal.before(fromCal)) {
			MessageUtil.setMessage("'To' date must be later than 'From' date.",
					"color:red");
			setValues();
			return;
			
		}
		
		
		
		int storeSelectedCount = emailSmsControlsLbId.getSelectedCount();
		int storeItemCount = emailSmsControlsLbId.getItemCount();
        if(storeSelectedCount == storeItemCount){
        	storeBandBoxId.setValue("All");
        }
        else if((storeSelectedCount>1) && !(storeSelectedCount == storeItemCount)){
			storeBandBoxId.setValue("Multiple");
		}
        else if((storeSelectedCount==1) && !(storeSelectedCount == storeItemCount)){
        	storeBandBoxId.setValue(emailSmsControlsLbId.getSelectedItem().getLabel());
        }
        else if(emailSmsControlsLbId.getSelectedIndex() == -1){
			storeBandBoxId.setValue("");
		}

		searchBoxId.setValue("");
		searchSmsBoxId.setValue("");
		fromDate = MyCalendar.calendarToString(fromCal,
				MyCalendar.FORMAT_DATETIME_STYEAR);
		endDate = MyCalendar.calendarToString(endCal,
				MyCalendar.FORMAT_DATETIME_STYEAR);
		String timePeriod = " ("+MyCalendar.calendarToString(fromCal, MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone)+" - "+MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone)+")";
		deliveryTimeLbId.setValue(timePeriod); deliveryReportLbId.setValue(timePeriod); smsdeliveryReportLbId.setValue(timePeriod); storeReportLbId.setValue(timePeriod);

		int diffDays = (int) ((endCal.getTime().getTime() - fromCal.getTime()
				.getTime()) / (1000 * 60 * 60 * 24));

		if (diffDays > 330) {

			MessageUtil.setMessage(
					"Please select Sent Date range within 11 months.",
					"color:green");
			return;

		}

		
		setRequiredStatusToBeFetched();
		
		selectedStoreItem[0] = drStoreLbId.getSelectedItem();
		selectedStoreItem[1] = drStoreLbId.getSelectedIndex();

		totalSize = drSentDao
				.findDRBasedOnDatesForStore(
						currentUser.getUserId(),
						fromDate,
						endDate,
						null,
						selectedStoreItem[1].equals(0) ? null
								: (String) ((Listitem) selectedStoreItem[0])
										.getValue(),emailSatusTobeFetched);
		jrsh.executeStmt(getStoreReportsQuery(), true);
		storeSize = jrsh.totalRecordsSize();/*drSentDao
				.findStoreCountBasedOnDates(
						currentUser.getUserId(),
						fromDate,
						endDate,
						selectedStoreItem[1].equals(0) ? null
								: (String) ((Listitem) selectedStoreItem[0])
										.getValue(), emailSatusTobeFetched);*/
		
		
		List<DRSent> drSentList = drSentDao
				.findDrReports(
						currentUser.getUserId(),
						-1,
						-1,
						fromDate,
						endDate,
						null,
						selectedStoreItem[1].equals(0) ? null
								: (String) ((Listitem) selectedStoreItem[0])
										.getValue(),emailSatusTobeFetched,orderby_colName,desc_Asc);
				
				
				reportsLocationsPagingId.setTotalSize(drSentList != null ? drSentList.size() : 0);
				List<DRSMSSent> drSmsSentList = drSmsSentDao
						.findDrReports(
								currentUser.getUserId(),
								-1,
								-1,
								fromDate,
								endDate,
								null,
								selectedStoreItem[1].equals(0) ? null
										: (String) ((Listitem) selectedStoreItem[0])
												.getValue(),smsStatusTobeFetched,orderby_colName,desc_Asc);
						
						
						reportsSmsLocationsPagingId.setTotalSize(drSmsSentList != null ? drSmsSentList.size() : 0);
		List<DRSent> drStatusPendingList = drSentDao
						.findPenidngReports(
								currentUser.getUserId(),
								-1,
								-1,
								fromDate,
								endDate,
								null,
								selectedStoreItem[1].equals(0) ? null
										: (String) ((Listitem) selectedStoreItem[0])
												.getValue(),Constants.DR_STATUS_SUBMITTED,orderby_colName,desc_Asc);
						
		List<DRSMSSent> drSmsStatusPendingList = drSmsSentDao
						.findPenidngReports(
								currentUser.getUserId(),
								-1,
								-1,
								fromDate,
								endDate,
								null,
								selectedStoreItem[1].equals(0) ? null
										: (String) ((Listitem) selectedStoreItem[0])
												.getValue(),Constants.DR_STATUS_SUBMITTED,orderby_colName,desc_Asc);
						
		statusPendingReportsLocationsPagingId.setTotalSize(drStatusPendingList != null ? drStatusPendingList.size() : 0);
		statusPendingReportsSmsLocationsPagingId.setTotalSize(drSmsStatusPendingList != null ? drSmsStatusPendingList.size() : 0);
				
		
		//reportsLocationsPagingId.setTotalSize(totalSize);
		storeReportsLocationsPagingId.setTotalSize(storeSize);
		//statusPendingReportsLocationsPagingId.setTotalSize(pendingReportsPageSize);
		getDigiReports(fromDate, endDate, 0,
				reportsLocationsPagingId.getPageSize(), null);
		getSMSDigiReports(fromDate, endDate, 0,
				reportsSmsLocationsPagingId.getPageSize(), null);
		getDrDetails(fromDate, endDate, selectedStoreItem[1].equals(0) ? null
				: (String) ((Listitem) selectedStoreItem[0]).getValue());
		getStoreReports(fromDate, endDate, 0,
				storeReportsLocationsPagingId.getPageSize());
		getStatusPendingReports(fromDate, endDate,0,
				statusPendingReportsLocationsPagingId.getPageSize());
		getSmsStatusPendingReports(fromDate, endDate,0,
				statusPendingReportsSmsLocationsPagingId.getPageSize());
		long totalStoreSize = drSentDao.findTotalStorePurchases(currentUser.getUserId(), fromDate, endDate, selectedStoreItem[1].equals(0) ? null: (String) ((Listitem) selectedStoreItem[0]).getValue(),emailSatusTobeFetched);
//		if(totalStoreSize !=0 ){
//			totalStoresDivID.setVisible(true);
//			totalStoresLbID.setValue(totalStoreSize+"");
//		}else{
//			totalStoresDivID.setVisible(false);
//		}

		
		/*TESTING-SMS-RECEIPTS*/
		long totalSmsStoreSize = drSmsSentDao.findTotalStorePurchases(currentUser.getUserId(), fromDate, endDate, selectedStoreItem[1].equals(0) ? null: (String) ((Listitem) selectedStoreItem[0]).getValue(),smsStatusTobeFetched);
		
		if (totalStoreSize != 0 || totalSmsStoreSize != 0) {
			totalStoresDivID.setVisible(true);
			totalStoresLbID.setValue(totalStoreSize+"");
			totalSmsStoresLbID.setValue(totalSmsStoreSize + "");
		}else{
			totalStoresDivID.setVisible(false);
		}
		
		if(drStatusPendingList !=null && drStatusPendingList.size()!=0) pendingReportsDivId.setVisible(false);
		if(drSmsStatusPendingList !=null && drSmsStatusPendingList.size()!=0) pendingReportsSmsDivId.setVisible(false);
		drawChart();

	}

	// to Reset the User change to default,the code of doAfterCompose should
	// execute so simply called the defaultSettings

	public void onClick$datesResetBtnId() {

		try {
			orderby_colName="sent_date";
			desc_Asc="desc";
			notSentId.setVisible(false);
			notSentLblId.setVisible(false);
			pendingReportsDivId.setVisible(false);
			pendingReportsSmsDivId.setVisible(false);
			defaultSettings();
		} catch (Exception e) {
			// TODO: handle exception

			logger.error("Exception ::", e);
		}

	}

	private Window previewWin,previewWinForSMS;
	private Html previewWin$html,previewWinForSMS$html;

	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
		if (event.getTarget() instanceof Paging) {

			Paging paging = (Paging) event.getTarget();
			int desiredPage = paging.getActivePage();

			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			// drReportsLbId.setItemRenderer(renderer);

			// String fromDate = MyCalendar.calendarToString(getStartDate(),
			// MyCalendar.FORMAT_DATETIME_STYEAR);
			// String endDate = MyCalendar.calendarToString(getEndDate(),
			// MyCalendar.FORMAT_DATETIME_STYEAR);

			if (paging.getId().equals("reportsLocationsPagingId")) {
				setValues();
				reportsLocationsPagingId.setActivePage(desiredPage);
				getDigiReports(fromDate, endDate, ofs, (byte) pagingEvent
						.getPageable().getPageSize(), searchBoxId.getValue()!=null && searchBoxId.getValue().trim().length() >0 ? searchBoxId.getValue().trim():null);
			}else if (paging.getId().equals("reportsSmsLocationsPagingId")) {
				setValues();
				reportsSmsLocationsPagingId.setActivePage(desiredPage);
				getSMSDigiReports(fromDate, endDate, ofs, (byte) pagingEvent
						.getPageable().getPageSize(), searchBoxId.getValue()!=null && searchBoxId.getValue().trim().length() >0 ? searchBoxId.getValue().trim():null);
			} else if (paging.getId().equals("storeReportsLocationsPagingId")) {
				setValues();
				storeReportsLocationsPagingId.setActivePage(desiredPage);
				getStoreReports(fromDate, endDate, ofs, (byte) pagingEvent
						.getPageable().getPageSize());
			} else if(paging.getId().equals("statusPendingReportsLocationsPagingId")){
				statusPendingReportsLocationsPagingId.setActivePage(desiredPage);
				getStatusPendingReports(fromDate, endDate, ofs, (byte) pagingEvent
						.getPageable().getPageSize());
			}else if(paging.getId().equals("statusPendingReportsSmsLocationsPagingId")){
				statusPendingReportsSmsLocationsPagingId.setActivePage(desiredPage);
				getSmsStatusPendingReports(fromDate, endDate, ofs, (byte) pagingEvent
						.getPageable().getPageSize());
			}else if(paging.getId().equals("recipientsSmsReportsLocationsPagingId")){
				recipientsSmsReportsLocationsPagingId.setActivePage(desiredPage);
				getRecipientSmsReport(ofs, (byte) pagingEvent
						.getPageable().getPageSize(), recipientsSmsSearchBoxId.getValue()!=null && recipientsSmsSearchBoxId.getValue().trim().length() >0 ?recipientsSmsSearchBoxId.getValue().trim():null);
			}else {
				recipientsReportsLocationsPagingId.setActivePage(desiredPage);
				getRecipientReport(ofs, (byte) pagingEvent
						.getPageable().getPageSize(), recipientsSearchBoxId.getValue()!=null && recipientsSearchBoxId.getValue().trim().length() >0 ?recipientsSearchBoxId.getValue().trim():null);
			}
		}

		if (event.getTarget() instanceof Image) {
			Image img = (Image) event.getTarget();
			Listitem item = (Listitem) img.getParent().getParent().getParent();
			String eventName = (String) img.getAttribute("imageEventName");
			logger.info(" object  "+item.getValue());
			logger.info(" eventName  "+eventName);
			DRSent dr=null; DRSMSSent drSms=null;
			String mobile="",email="",docsid="";
			if(eventName.equalsIgnoreCase("viewSms") || eventName.equalsIgnoreCase("viewAllSms")) {
				drSms= (DRSMSSent)item.getValue();
				mobile= drSms.getMobile();
				docsid = drSms.getDocSid();
			}else {
				dr= (DRSent)item.getValue();
				email =  dr.getEmailId();
				docsid = dr.getDocSid();
			}
			
		/*	String email =  dr.getEmailId();
			String docsid = dr.getDocSid();*/

			logger.info("docsid & emaill............"+email + " " +docsid);
			if (eventName.equalsIgnoreCase("view")) {

				
				String htmlContent = Constants.STRING_NILL;
				dr = drSentDao.findById(dr.getId());
					if (dr != null && dr.getHtmlStr() != null) {
					//if (dr.getHtmlStr() != null) {
						htmlContent = dr.getHtmlStr();
						
						if(htmlContent.contains("href='")){
							htmlContent = htmlContent.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\"");	
						}
						if(htmlContent.contains("href=\"")){
							htmlContent = htmlContent.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\"");
						}
						try {
							String ApplicationUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
							ApplicationUrl = ApplicationUrl.replace("/", "\\/").trim();
							String patternr = "("+ApplicationUrl+"updateDigitalReport.mqrm\\?requestedAction=open&sentId=\\d*)";
							 if(htmlContent.contains("&amp;")) patternr =  StringEscapeUtils.escapeHtml(patternr); 
							Pattern pattern = Pattern.compile(patternr,Pattern.CASE_INSENSITIVE);
							Matcher matcher = pattern.matcher(htmlContent);
							Boolean match =  matcher.find();
							if(match){
								String imgUrl = matcher.group();
								htmlContent = htmlContent.replace(imgUrl,"");
							}
							
						}catch (Exception e) {
							logger.error("Error during replace of html in dr preview "+e);
						}
						htmlContent = Utility.mergeTagsForPreviewAndTestMail(htmlContent, "preview");
					} else {
						MessageUtil.setMessage("Can not process the Html content","color:red;");
						return;
					}
				
				previewWin$html.setContent(htmlContent);
				previewWin.setVisible(true);
				/*previewWinForSMS$html.setContent(htmlContent);
				previewWinForSMS.setVisible(true);*/
			} else if (eventName.equalsIgnoreCase("viewSms")) {

				
				String htmlContent = Constants.STRING_NILL;

				drSms = drSmsSentDao.findById(drSms.getId());

				if (drSms != null && drSms.getHtmlStr() != null) {
						htmlContent = drSms.getHtmlStr();
						
						if(htmlContent.contains("href='")){
							htmlContent = htmlContent.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\"");	
						}
						if(htmlContent.contains("href=\"")){
							htmlContent = htmlContent.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\"");
						}
						try {
							String ApplicationUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
							ApplicationUrl = ApplicationUrl.replace("/", "\\/").trim();
							String patternr = "("+ApplicationUrl+"updateDigitalReport.mqrm\\?requestedAction=openSMS&sentId=\\d*)";
							 if(htmlContent.contains("&amp;")) patternr =  StringEscapeUtils.escapeHtml(patternr); 
							Pattern pattern = Pattern.compile(patternr,Pattern.CASE_INSENSITIVE);
							Matcher matcher = pattern.matcher(htmlContent);
							Boolean match =  matcher.find();
							if(match){
								String imgUrl = matcher.group();
								htmlContent = htmlContent.replace(imgUrl,"");
							}
							
						}catch (Exception e) {
							logger.error("Error during replace of html in dr preview "+e);
						}
						htmlContent = Utility.mergeTagsForPreviewAndTestMail(htmlContent, "preview");
					} else {
						MessageUtil.setMessage("Can not process the Html content","color:red;");
						return;
					}
				
				/*previewWin$html.setContent(htmlContent);
				previewWin.setVisible(true);*/
				
				previewWinForSMS$html.setContent(htmlContent);
				previewWinForSMS.setVisible(true);
			} else if (eventName.equalsIgnoreCase("viewAll")) {

				getAllDrView(email, currentUser.getUserId());
			}
			 else if (eventName.equalsIgnoreCase("viewAllSms")) {

				 getAllSmsDrView(mobile,currentUser.getUserId());
			}
		}

	}// onEvent()

	
	
/*	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
		if (event.getTarget() instanceof Paging) {

			Paging paging = (Paging) event.getTarget();
			int desiredPage = paging.getActivePage();

			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			// drReportsLbId.setItemRenderer(renderer);

			// String fromDate = MyCalendar.calendarToString(getStartDate(),
			// MyCalendar.FORMAT_DATETIME_STYEAR);
			// String endDate = MyCalendar.calendarToString(getEndDate(),
			// MyCalendar.FORMAT_DATETIME_STYEAR);

			if (paging.getId().equals("reportsLocationsPagingId")) {
				setValues();
				reportsLocationsPagingId.setActivePage(desiredPage);
				getDigiReports(fromDate, endDate, ofs, (byte) pagingEvent
						.getPageable().getPageSize(), searchBoxId.getValue()!=null && searchBoxId.getValue().trim().length() >0 ? searchBoxId.getValue().trim():null);
			} else if (paging.getId().equals("storeReportsLocationsPagingId")) {
				setValues();
				storeReportsLocationsPagingId.setActivePage(desiredPage);
				getStoreReports(fromDate, endDate, ofs, (byte) pagingEvent
						.getPageable().getPageSize());
			} else if(paging.getId().equals("statusPendingReportsLocationsPagingId")){
				statusPendingReportsLocationsPagingId.setActivePage(desiredPage);
				getStatusPendingReports(fromDate, endDate, ofs, (byte) pagingEvent
						.getPageable().getPageSize());
			}else {
				recipientsReportsLocationsPagingId.setActivePage(desiredPage);
				getRecipientReport(ofs, (byte) pagingEvent
						.getPageable().getPageSize(), recipientsSearchBoxId.getValue()!=null && recipientsSearchBoxId.getValue().trim().length() >0 ?recipientsSearchBoxId.getValue().trim():null);
			}
		}

		if (event.getTarget() instanceof Image) {
			Image img = (Image) event.getTarget();
			Listitem item = (Listitem) img.getParent().getParent().getParent();
			String eventName = (String) img.getAttribute("imageEventName");
			logger.info(" object  "+item.getValue());
			DRSent dr= (DRSent)item.getValue();
			String email =  dr.getEmailId();
			String docsid = dr.getDocSid();

			logger.info("docsid & emaill............"+email + " " +docsid);
			if (eventName.equalsIgnoreCase("view")) {

				DRSent drsent = drSentDao.findBy( currentUser
						.getUserId().longValue(),docsid, email );
				if (drsent == null) {

					MessageUtil.setMessage("Can not process the Html content",
							"color:red;");
					return;

				}
				String htmlContent = Constants.STRING_NILL;
				String templateName = drsent.getTemplateName();
				if (drsent.getDrJsonObjId() == null || templateName == null) {
					long msgId = Long.parseLong(drsent.getMessage());
					EmailQueue eqObj = emailQueueDao.findEqById(msgId);
					htmlContent = eqObj.getMessage();

				} else {
					if (drsent.getHtmlStr() != null) {
						htmlContent = drsent.getHtmlStr();
					} else {

						htmlContent = DrReportsView(templateName,
								drsent.getPhValStr(), drsent.getDrJsonObjId(),
								drsent);
					}
				}
				previewWin$html.setContent(htmlContent);
				previewWin.setVisible(true);
			} else if (eventName.equalsIgnoreCase("viewAll")) {

				getAllDrView(email, currentUser.getUserId());
			}
		}

	}// onEvent()
*/
	public String getPercentage(long amount, long totalAmount) {
		try {

			return Utility.getPercentage(((Long) amount).intValue(),
					totalAmount, 2);

		} catch (RuntimeException e) {
			logger.error("** Exception ", (Throwable) e);
			return "";
		}
	} // getPercentage

	private Checkbox uniqOpensChkId, clicksChkId, bouncedChkId, deleveredChkId,statusPendingChkId,
			sentChkId;

	public Chart drawChart() {

		try {

			/*
			 * endDate.set(Calendar.HOUR_OF_DAY, 23);
			 * endDate.set(Calendar.MINUTE, 59); endDate.set(Calendar.SECOND,
			 * 59);
			 * 
			 * 
			 * String startDateStr = (startDate ==
			 * null)?null:startDate.toString(); String endDateStr = (endDate ==
			 * null)?null:endDate.toString();
			 */

			/*
			 * Calendar startDate = getStartDate(); Calendar endDate =
			 * getEndDate();
			 */
			Date dt = fromDateboxId.getValue();
			
			int tzOffSet =(clientTimeZone.getOffset(dt.getTime()) - Calendar.getInstance().getTimeZone().getOffset(dt.getTime()))/(1000*60);
			String store = selectedStoreItem[1].equals(0) ? null
					: (String) ((Listitem) selectedStoreItem[0]).getValue();
			Calendar startDate = getStartCalendar();
			Calendar cendDate = getEndCalendar();
			String startDateStr = MyCalendar.calendarToString(startDate,
					MyCalendar.FORMAT_DATETIME_STYEAR);
			String endDateStr = MyCalendar.calendarToString(cendDate,
					MyCalendar.FORMAT_DATETIME_STYEAR);

			int diffDays = (int) ((cendDate.getTime().getTime() - startDate
					.getTime().getTime()) / (1000 * 60 * 60 * 24));

			int maxDays = startDate.getActualMaximum(Calendar.DAY_OF_MONTH);

			

			String type = "";

			if (diffDays >= maxDays) {
				type = DR_TYPE_MONTHS;
				drReportsChartId.setXAxis(type);
			} else {
				type = DR_TYPE_DAYS;
				drReportsChartId.setXAxis(type);
			}

			logger.info("DiffDays==" + diffDays + " Type=" + type + " MaxDays="
					+ maxDays);
			String t = type.equalsIgnoreCase(DR_TYPE_MONTHS) ? "month" : "day";
			CategoryModel model = new SimpleCategoryModel();

			Map<String, Integer> sentMap = new HashMap<String, Integer>();
			Map<String, Integer> delMap = new HashMap<String, Integer>();
			Map<String, Integer> opensMap = new HashMap<String, Integer>();
			Map<String, Integer> clicksMap = new HashMap<String, Integer>();
			Map<String, Integer> bounceMap = new HashMap<String, Integer>();
			Map<String, Integer> statuPendingMap = new HashMap<String, Integer>();

			if (sentChkId.isChecked()) {
				List<Map<String, Object>> sentRates = drSentDao.findTotSentRate(
						currentUser.getUserId(), startDateStr, endDateStr,
						type, store, tzOffSet);
				for (Map<String, Object> obj : sentRates) {
					sentMap.put(obj.get(t).toString(),
							Integer.parseInt(obj.get("count").toString()));
				} // for
			}

			if (deleveredChkId.isChecked()) {
				List<Map<String,Object>> delRates = drSentDao.findTotDelRate(
						currentUser.getUserId(), startDateStr, endDateStr,
						type, store, tzOffSet);
				for (Map<String, Object> obj : delRates) {
					delMap.put(obj.get(t).toString(),
							Integer.parseInt(obj.get("count").toString()));
				} // for
			}

			if (bouncedChkId.isChecked()) {
				List<Map<String,Object>> bounceRates = drSentDao.findTotBounceRate(
						currentUser.getUserId(), startDateStr, endDateStr,
						type, store, tzOffSet);
				for (Map<String, Object> obj : bounceRates) {
					bounceMap.put(obj.get(t).toString(),
							Integer.parseInt(obj.get("count").toString()));
				} // for
			}
			
			if (statusPendingChkId.isChecked()) {
				List<Map<String,Object>> statusPendingRates = drSentDao.findTotStatusPendingRate(
						currentUser.getUserId(), startDateStr, endDateStr,
						type, store, tzOffSet);
				for (Map<String, Object> obj : statusPendingRates) {
					statuPendingMap.put(obj.get(t).toString(),
							Integer.parseInt(obj.get("count").toString()));
				} // for
			}

			if (uniqOpensChkId.isChecked()) {
				List<Map<String,Object>> opensRates = drSentDao.findTotOpenRate(
						currentUser.getUserId(), startDateStr, endDateStr,
						type, store, tzOffSet);
				for (Map<String, Object> obj : opensRates) {
					opensMap.put(obj.get(t).toString(),
							Integer.parseInt(obj.get("count").toString()));
				} // for
			}

			if (clicksChkId.isChecked()) {
				List<Map<String,Object>> clickRates = drSentDao.findTotClickRate(
						currentUser.getUserId(), startDateStr, endDateStr,
						type, store, tzOffSet);
				for (Map<String, Object> obj : clickRates) {
					clicksMap.put(obj.get(t).toString(),
							Integer.parseInt(obj.get("count").toString()));
				} // for
			}

			Calendar tempCal = Calendar.getInstance();
			tempCal.setTime(dt);
			cendDate.setTime(toDateboxId.getValue()); 
			String currDate = "";
			
			int monthsDiff = ((cendDate.get(Calendar.YEAR) * 12 + cendDate
					.get(Calendar.MONTH)) - (tempCal.get(Calendar.YEAR) * 12 + tempCal
					.get(Calendar.MONTH))) + 1;
			// int i=0;

			/*
			 * do {
			 * 
			 * if(type.equals(DR_TYPE_DAYS)) {
			 * 
			 * currDate = ""+tempCal.get(startDate.DATE);
			 * 
			 * 
			 * if(sentChkId.isChecked()) model.setValue("Sent", currDate,
			 * sentMap.containsKey(currDate) ? sentMap.get(currDate) : 0);
			 * if(deleveredChkId.isChecked()) model.setValue("Delivered",
			 * currDate, delMap.containsKey(currDate) ? delMap.get(currDate) :
			 * 0); if(bouncedChkId.isChecked()) model.setValue("Bounced",
			 * currDate, bounceMap.containsKey(currDate) ?
			 * bounceMap.get(currDate) : 0);
			 * 
			 * if(uniqOpensChkId.isChecked()) model.setValue("Unique Opens",
			 * currDate, opensMap.containsKey(currDate) ? opensMap.get(currDate)
			 * : 0); if(clicksChkId.isChecked()) model.setValue("Unique Clicks",
			 * currDate, clicksMap.containsKey(currDate) ?
			 * clicksMap.get(currDate) : 0);
			 * 
			 * tempCal.set(Calendar.DATE, tempCal.get(Calendar.DATE) + 1); }
			 * else if(type.equals(DR_TYPE_MONTHS)) {
			 * 
			 * currDate = ""+(tempCal.get(startDate.MONTH)+1);
			 * 
			 * if(sentChkId.isChecked()) model.setValue("Sent",
			 * MONTH_MAP.get(currDate), sentMap.containsKey(currDate) ?
			 * sentMap.get(currDate) : 0); if(deleveredChkId.isChecked())
			 * model.setValue("Delivered", MONTH_MAP.get(currDate),
			 * delMap.containsKey(currDate) ? delMap.get(currDate) : 0);
			 * if(bouncedChkId.isChecked()) model.setValue("Bounced",
			 * MONTH_MAP.get(currDate), bounceMap.containsKey(currDate) ?
			 * bounceMap.get(currDate) : 0);
			 * 
			 * if(uniqOpensChkId.isChecked()) model.setValue("Unique Opens",
			 * MONTH_MAP.get(currDate), opensMap.containsKey(currDate) ?
			 * opensMap.get(currDate) : 0); if(clicksChkId.isChecked())
			 * model.setValue("Unique Clicks", MONTH_MAP.get(currDate),
			 * clicksMap.containsKey(currDate) ? clicksMap.get(currDate) : 0);
			 * 
			 * tempCal.set(Calendar.MONTH, tempCal.get(Calendar.MONTH) + 1); }
			 * 
			 * } while(tempCal.get(Calendar.MONTH)<=
			 * endDate.get(Calendar.MONTH));
			 */
			if (type.equals(DR_TYPE_DAYS)) {
				do {
					// logger.debug("in if days ========");
					currDate = "" + tempCal.get(Calendar.DATE);

					if (sentChkId.isChecked()) {
						// logger.debug("in if days ========"+sentMap.get(currDate));
						model.setValue("Sent", currDate, sentMap
								.containsKey(currDate) ? sentMap.get(currDate)
								: 0);
					}
					if (deleveredChkId.isChecked())
						model.setValue("Delivered", currDate, delMap
								.containsKey(currDate) ? delMap.get(currDate)
								: 0);
					if (bouncedChkId.isChecked())
						model.setValue(
								"Bounced",
								currDate,
								bounceMap.containsKey(currDate) ? bounceMap
										.get(currDate) : 0);
					if (statusPendingChkId.isChecked())
						model.setValue(
								"Delivery Status Pending",
								currDate,
								statuPendingMap.containsKey(currDate) ? statuPendingMap
										.get(currDate) : 0);

					if (uniqOpensChkId.isChecked())
						model.setValue("Unique Opens", currDate, opensMap
								.containsKey(currDate) ? opensMap.get(currDate)
								: 0);
					if (clicksChkId.isChecked())
						model.setValue(
								"Unique Clicks",
								currDate,
								clicksMap.containsKey(currDate) ? clicksMap
										.get(currDate) : 0);

					tempCal.set(Calendar.DATE, tempCal.get(Calendar.DATE) + 1);

				} while (tempCal.before(cendDate) || tempCal.equals(cendDate));
			}
			if (type.equals(DR_TYPE_MONTHS)) {
				int i = 1;
				do {
					// logger.debug("executing ========"+monthsDiff);
					i++;

					currDate = "" + (tempCal.get(Calendar.MONTH) + 1);

					if (sentChkId.isChecked())
						model.setValue("Sent", MONTH_MAP.get(currDate), sentMap
								.containsKey(currDate) ? sentMap.get(currDate)
								: 0);
					if (deleveredChkId.isChecked())
						model.setValue(
								"Delivered",
								MONTH_MAP.get(currDate),
								delMap.containsKey(currDate) ? delMap
										.get(currDate) : 0);
					if (bouncedChkId.isChecked())
						model.setValue(
								"Bounced",
								MONTH_MAP.get(currDate),
								bounceMap.containsKey(currDate) ? bounceMap
										.get(currDate) : 0);
					if (statusPendingChkId.isChecked())
						model.setValue(
								"Delivery Status Pending",
								MONTH_MAP.get(currDate),
								statuPendingMap.containsKey(currDate) ? statuPendingMap
										.get(currDate) : 0);

					if (uniqOpensChkId.isChecked())
						model.setValue(
								"Unique Opens",
								MONTH_MAP.get(currDate),
								opensMap.containsKey(currDate) ? opensMap
										.get(currDate) : 0);
					if (clicksChkId.isChecked())
						model.setValue(
								"Unique Clicks",
								MONTH_MAP.get(currDate),
								clicksMap.containsKey(currDate) ? clicksMap
										.get(currDate) : 0);

					tempCal.set(Calendar.MONTH, tempCal.get(Calendar.MONTH) + 1);

				} while (i <= monthsDiff);
			}
			
			if(!statusPendingChkId.isChecked()){
				pendingReportsDivId.setVisible(false);
				pendingReportsSmsDivId.setVisible(false);
				notSentId.setVisible(false);
				notSentLblId.setVisible(false);
			}
			drReportsChartId.setModel(model);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}

		return drReportsChartId;
	}

	// serach filter option

	private Textbox searchBoxId, recipientsSearchBoxId,statusPendingSearchBoxId,searchSmsBoxId,statusPendingSmsSearchBoxId,recipientsSmsSearchBoxId;

	public void onChanging$recipientsSearchBoxId(InputEvent event) {

		String key = event.getValue();

		logger.info("got the key ::" + key);

		if (key.trim().length() != 0) {
			recipientsReportsLocationsPagingId.setTotalSize(drSentDao
					.findTotalReciepients(currentUser.getUserId(), key));
			recipientsReportsLocationsPagingId.setActivePage(0);
			getRecipientReport(0, recipientsReportsLocationsPagingId.getPageSize(),key);

		}

		else {
			recipientsReportsLocationsPagingId.setTotalSize(drSentDao
					.findTotalReciepients(currentUser.getUserId(), null));
			recipientsReportsLocationsPagingId.setActivePage(0);
			getRecipientReport(0, recipientsReportsLocationsPagingId.getPageSize(),null);
		}

	}
	
	public void onChanging$recipientsSmsSearchBoxId(InputEvent event) {

		String key = event.getValue();

		logger.info("got the key ::" + key);

		if (key.trim().length() != 0) {
			recipientsSmsReportsLocationsPagingId.setTotalSize(drSmsSentDao
					.findTotalReciepients(currentUser.getUserId(), key));
			recipientsSmsReportsLocationsPagingId.setActivePage(0);
			getRecipientSmsReport(0, recipientsSmsReportsLocationsPagingId.getPageSize(),key);

		}

		else {
			recipientsSmsReportsLocationsPagingId.setTotalSize(drSmsSentDao
					.findTotalReciepients(currentUser.getUserId(), null));
			recipientsReportsLocationsPagingId.setActivePage(0);
			getRecipientSmsReport(0, recipientsSmsReportsLocationsPagingId.getPageSize(),null);

		}

	}
	
	public void onChanging$statusPendingSearchBoxId(InputEvent event) {

		String key = event.getValue();

		logger.info("got the key ::" + key);

		if (key.trim().length() != 0) {
			statusPendingReportsLocationsPagingId.setTotalSize(drSentDao
					.findTotalReciepients(currentUser.getUserId(), key));
			statusPendingReportsLocationsPagingId.setActivePage(0);
			getStatusPendingReport(0, statusPendingReportsLocationsPagingId.getPageSize(),key);

		}

		else {
			statusPendingReportsLocationsPagingId.setTotalSize(drSentDao
					.findTotalReciepients(currentUser.getUserId(), null));
			statusPendingReportsLocationsPagingId.setActivePage(0);
			getStatusPendingReport(0, statusPendingReportsLocationsPagingId.getPageSize(),null);
		}

	}
	
	public void onChanging$statusPendingSmsSearchBoxId(InputEvent event) {

		String key = event.getValue();

		logger.info("got the key ::" + key);

		if (key.trim().length() != 0) {
			statusPendingReportsSmsLocationsPagingId.setTotalSize(drSmsSentDao
					.findTotalReciepients(currentUser.getUserId(), key));
			statusPendingReportsSmsLocationsPagingId.setActivePage(0);
			getSmsStatusPendingReport(0, statusPendingReportsSmsLocationsPagingId.getPageSize(),key);

		}

		else {
			statusPendingReportsSmsLocationsPagingId.setTotalSize(drSmsSentDao
					.findTotalReciepients(currentUser.getUserId(), null));
			statusPendingReportsSmsLocationsPagingId.setActivePage(0);
			getSmsStatusPendingReport(0, statusPendingReportsSmsLocationsPagingId.getPageSize(),null);

		}

	}

	
	public void onChanging$searchBoxId(InputEvent event) {

		// String fromDate = MyCalendar.calendarToString(getStartDate(),
		// MyCalendar.FORMAT_DATETIME_STYEAR);
		/*
		 * Calendar endCal = toDateboxId.getServerValue();
		 * endCal.set(Calendar.HOUR_OF_DAY, 23); endCal.set(Calendar.MINUTE,
		 * 59); endCal.set(Calendar.SECOND, 59);
		 */
		// String endDate = MyCalendar.calendarToString(getEndDate(),
		// MyCalendar.FORMAT_DATETIME_STYEAR);
		setValues();
		String key = event.getValue();

		logger.info("got the key ::" + key);

		if (key.trim().length() != 0) {
			totalSize = drSentDao.findDRBasedOnDatesForStore(currentUser
					.getUserId(), fromDate, endDate, key, selectedStoreItem[1]
					.equals(0) ? null
					: (String) ((Listitem) selectedStoreItem[0]).getValue(),emailSatusTobeFetched);

			reportsLocationsPagingId.setTotalSize(totalSize);
			reportsLocationsPagingId.setActivePage(0);
			getDigiReports(fromDate, endDate, 0,
					reportsLocationsPagingId.getPageSize(), key.trim());

		}

		else {
			totalSize = drSentDao.findDRBasedOnDatesForStore(currentUser
					.getUserId(), fromDate, endDate, null, selectedStoreItem[1]
					.equals(0) ? null
					: (String) ((Listitem) selectedStoreItem[0]).getValue(),emailSatusTobeFetched);

			reportsLocationsPagingId.setTotalSize(totalSize);
			reportsLocationsPagingId.setActivePage(0);
			getDigiReports(fromDate, endDate, 0,
					reportsLocationsPagingId.getPageSize(), null);

		}

	}// onChanging$searchBoxId
	public void onChanging$searchSmsBoxId(InputEvent event) {

		setValues();
		String key = event.getValue();

		logger.info("got the key ::" + key);

		if (key.trim().length() != 0) {
			totalSizeSms = drSmsSentDao.findDRBasedOnDatesForStore(currentUser
					.getUserId(), fromDate, endDate, key, selectedStoreItem[1]
					.equals(0) ? null
					: (String) ((Listitem) selectedStoreItem[0]).getValue(),smsStatusTobeFetched);

			reportsSmsLocationsPagingId.setTotalSize(totalSizeSms);
			reportsSmsLocationsPagingId.setActivePage(0);
			getSMSDigiReports(fromDate, endDate, 0,
					reportsSmsLocationsPagingId.getPageSize(), key.trim());

		}

		else {
			totalSizeSms = drSmsSentDao.findDRBasedOnDatesForStore(currentUser
					.getUserId(), fromDate, endDate, null, selectedStoreItem[1]
					.equals(0) ? null
					: (String) ((Listitem) selectedStoreItem[0]).getValue(),smsStatusTobeFetched);

			reportsSmsLocationsPagingId.setTotalSize(totalSizeSms);
			reportsSmsLocationsPagingId.setActivePage(0);
			getSMSDigiReports(fromDate, endDate, 0,
					reportsSmsLocationsPagingId.getPageSize(), null);

		}

	}// onChanging$searchSmsBoxId

	public void onClick$resetSearchCriteriaAnchId() {

		try {
			// String fromDate = MyCalendar.calendarToString(getStartDate(),
			// MyCalendar.FORMAT_DATETIME_STYEAR);
			/*
			 * Calendar endCal = toDateboxId.getServerValue();
			 * endCal.set(Calendar.HOUR_OF_DAY, 23); endCal.set(Calendar.MINUTE,
			 * 59); endCal.set(Calendar.SECOND, 59);
			 */
			// String endDate = MyCalendar.calendarToString(getEndDate(),
			// MyCalendar.FORMAT_DATETIME_STYEAR);
			/*
			 * fromDateboxId.setValue(startDate);
			 * toDateboxId.setValue(cendDate);
			 */
			searchBoxId.setValue("");
			searchSmsBoxId.setValue("");
			jrsh.executeStmt(getStoreReportsQuery(), true);
			storeSize = jrsh.totalRecordsSize();/*drSentDao.findStoreCountBasedOnDates(currentUser
					.getUserId(), fromDate, endDate, selectedStoreItem[1]
					.equals(0) ? null
					: (String) ((Listitem) selectedStoreItem[0]).getValue(),emailSatusTobeFetched);*/
			searchSmsBoxId.setValue("");
			reportsLocationsPagingId.setTotalSize(totalSize);
			reportsSmsLocationsPagingId.setTotalSize(totalSizeSms);
			storeReportsLocationsPagingId.setTotalSize(storeSize);
			statusPendingReportsLocationsPagingId.setTotalSize(pendingReportsPageSize);
			statusPendingReportsSmsLocationsPagingId.setTotalSize(pendingSmsReportsPageSize);
			reportsLocationsPagingId.setActivePage(0);
			reportsSmsLocationsPagingId.setActivePage(0);
			storeReportsLocationsPagingId.setActivePage(0);
			statusPendingReportsLocationsPagingId.setActivePage(0);
			statusPendingReportsSmsLocationsPagingId.setActivePage(0);
			getDigiReports(fromDate, endDate, 0,
					reportsLocationsPagingId.getPageSize(), null);
			getSMSDigiReports(fromDate, endDate, 0,
					reportsSmsLocationsPagingId.getPageSize(), null);
			getStoreReports(fromDate, endDate, 0,
					storeReportsLocationsPagingId.getPageSize());
			getStatusPendingReports(fromDate, endDate,0,statusPendingReportsLocationsPagingId.getPageSize());
			getSmsStatusPendingReports(fromDate, endDate,0,statusPendingReportsSmsLocationsPagingId.getPageSize());

		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
	}// onClick$resetSearchCriteriaAnchId()

	public String DrReportsView(String templateName, String phValStr,
			Long jsonObjId, DRSent drSent) {

		try {
			String jsonMessage = "";
			String templateContentStr = "";

			if (templateName == null) {

				logger.debug("Digital reciept template is not found .. assigning default template ********");
				templateName = "SYSTEM:Corporate_template";
				// return null;
			}

			if (templateName.indexOf("MY_TEMPLATE:") != -1) {

				templateName = templateName.substring(12);
				// logger.debug("Template Name is : "+ selectedTemplate);
				DigitalReceiptMyTemplate digitalReceiptMyTemplates = digitalReceiptMyTemplatesDao
						.findByUserNameAndTemplateName(currentUser.getUserId(),
								templateName);

				if (digitalReceiptMyTemplates == null) {
					logger.debug("Configured digital reciept template is not found ********");
					return null;
				}

				templateContentStr = digitalReceiptMyTemplates.getContent();

			} else if (templateName.indexOf("SYSTEM:") != -1) {

				templateName = templateName.substring(7);
				// logger.debug("Template Name is : "+ selectedTemplate);
				String digiReciptsDir = PropertyUtil
						.getPropertyValue("DigitalRecieptsDirectory");
				File templateFile = new File(digiReciptsDir + "/"
						+ templateName + "/index.html");
				// logger.debug("File is :" + templateFile);

				if (!templateFile.exists()) {
					return null;
				}

				FileReader fr = new FileReader(templateFile);

				BufferedReader br2 = new BufferedReader(fr);

				String line = "";
				StringBuffer sb2 = new StringBuffer();
				while ((line = br2.readLine()) != null) {

					sb2.append(line);
				}
				templateContentStr = sb2.toString();
			}

			String userJSONSettings = PropertyUtil
					.getPropertyValueFromDB("DigitalReceiptSetting");

			if (userJSONSettings == null) {
				jsonMessage = "Given JSON is not found .";
				logger.debug("userJSONSettings is null ... returning");
				return null;
			}

			// DigitalReceiptsJSON jsonMainObject =
			// digitalReceiptsJSONDao.find(jsonObjId);
			String jsonStr = digitalReceiptsJSONDao.findJsonStrById(jsonObjId);

			JSONObject jsonMainObj = null;
			// logger.debug(" Requst Query String : "+
			// Utility.decodeUrl(jsonStr));
			if (jsonStr != null && jsonStr.length() > 1) {

				logger.debug("jsonStr :>>>>>>>" + jsonStr);
				jsonMainObj = (JSONObject) JSONValue.parse(jsonStr);
				// logger.debug(" JSON VALUE TO STR : " +
				// JSONValue.toJSONString(jsonMainObj));

			} else {
				logger.debug("UNable to creeate main obj");
				return null;
			}

			if (jsonMainObj != null) {
				if (!jsonMainObj.containsKey("Items")) {
					jsonMainObj = (JSONObject) jsonMainObj.get("Body");
				}
			} else {
				logger.debug("*** Main Object is NUll ***");
				jsonMessage = "Unable to parse the JSON request .";
				return null;
			}

			String[] settingFieldsArr = userJSONSettings.split(Pattern
					.quote("^|^"));

			Map<String, String> userJSONSettingHM = new HashMap<String, String>();
			String[] tokensStr;

			// Get Individual mapping and add them to our MAP ,#templatePH# :
			// JSONKey
			for (int i = 0; i < settingFieldsArr.length; i++) {

				tokensStr = settingFieldsArr[i].split(":");
				userJSONSettingHM.put(tokensStr[0], tokensStr[1]);
			}

			// Get Values form Application properties
			String paymentSetPlaceHolders = PropertyUtil
					.getPropertyValue("DRPaymentLoopPlaceHolders");
			String ItemsSetPlaceHolders = PropertyUtil
					.getPropertyValue("DRItemLoopPlaceHolders");

			String[] paymentSetPlaceHoldersArr = paymentSetPlaceHolders
					.split(",");
			String[] ItemsSetPlaceHoldersArr = ItemsSetPlaceHolders.split(",");

			JSONObject receiptJSONObj = (JSONObject) jsonMainObj.get("Receipt");
			if (receiptJSONObj == null) {
				logger.debug("**** Exception : Receipt Object Not found ***");
				return null;
			}

			if (templateContentStr.indexOf(ItemsSetPlaceHoldersArr[0]) != -1
					&& templateContentStr.indexOf(ItemsSetPlaceHoldersArr[5]) != -1) {
				String loopBlockOne = templateContentStr.substring(
						templateContentStr.indexOf(ItemsSetPlaceHoldersArr[0])
								+ ItemsSetPlaceHoldersArr[0].length(),
						templateContentStr.indexOf(ItemsSetPlaceHoldersArr[5]));
				logger.debug("Items HTML is :" + loopBlockOne);

				// String temploopbackContent = loopBlockOne;

				String replacedStr = replaceLoopBlock(loopBlockOne,
						userJSONSettingHM, jsonMainObj);

				DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");
				Double totTax = (Double) (receiptJSONObj.get("TotalTax")
						.toString().isEmpty() ? Double.parseDouble("0.00")
						: Double.parseDouble(receiptJSONObj.get("TotalTax")
								.toString()));
				Double subTotal = (Double) (receiptJSONObj.get("Subtotal")
						.toString().isEmpty() ? Double.parseDouble("0.00")
						: Double.parseDouble(receiptJSONObj.get("Subtotal")
								.toString()));
				Double shipping = (Double) (receiptJSONObj.get("Shipping")
						.toString().isEmpty() ? Double.parseDouble("0.00")
						: Double.parseDouble(receiptJSONObj.get("Shipping")
								.toString()));
				Double fee = (Double) (receiptJSONObj.get("Fee").toString()
						.isEmpty() ? Double.parseDouble("0.00") : Double
						.parseDouble(receiptJSONObj.get("Fee").toString()));
				Double total = (Double) (receiptJSONObj.get("Total").toString()
						.isEmpty() ? Double.parseDouble("0.00") : Double
						.parseDouble(receiptJSONObj.get("Total").toString()));

				/*
				 * if(receiptJSONObj == null) {
				 * logger.debug("**** Exception : Receipt Object Not found ***"
				 * ); return null; }
				 */

				/**
				 * Removed after Faye's Request(With out Items and SONumber also
				 * We'l send DR)
				 */

				/*
				 * if((receiptJSONObj.get("SONumber") == null ||
				 * receiptJSONObj.get("SONumber").toString().isEmpty() ) &&
				 * replacedStr == null) { errorCode = 300008;
				 * jsonMessage="unable to find proper mappings."; logger.debug(
				 * "**** Exception : Error occured while replacing the item default values ... "
				 * ); return null; }
				 */

				// logger.debug("Items extra fields are getting added....");
				// Add extras prices like tax shipping etc
				if (replacedStr != null) {

					String itemHolder = "";
					itemHolder = loopBlockOne;
					itemHolder = itemHolder.replace("#Item.Description#",
							"&nbsp;");
					itemHolder = itemHolder.replace("#Item.Attr#", "&nbsp;");
					itemHolder = itemHolder.replace("#Item.Size#", "&nbsp;");
					itemHolder = itemHolder.replace("#Item.Description1#",
							"&nbsp;");
					itemHolder = itemHolder.replace("#Item.Description2#",
							"&nbsp;");
					itemHolder = itemHolder.replace("#Item.SerialNumber#",
							"&nbsp;");
					itemHolder = itemHolder
							.replace("#Item.Quantity#", "&nbsp;");

					itemHolder = itemHolder.replace("#Item.ALU#", "&nbsp;");
					itemHolder = itemHolder.replace("#Item.Lookup#", "&nbsp;");
					itemHolder = itemHolder
							.replace("#Item.Discount#", "&nbsp;");
					itemHolder = itemHolder.replace("#Item.DiscountPercent#",
							"&nbsp;");

					itemHolder = itemHolder.replace("#Item.UnitPrice#",
							"<strong> Sub Total </strong>");
					// itemHolder = itemHolder.replace("#Item.Total#",
					// "<strong>" + receiptJSONObj.get("Subtotal") +
					// "</strong>");
					itemHolder = itemHolder.replace("#Item.Total#", "<strong>"
							+ decimalFormat.format(subTotal) + "</strong>");
					replacedStr += itemHolder;

					// Tax
					itemHolder = loopBlockOne;
					itemHolder = itemHolder.replace("#Item.Description#",
							"&nbsp;");
					itemHolder = itemHolder.replace("#Item.Description1#",
							"&nbsp;");
					itemHolder = itemHolder.replace("#Item.Description2#",
							"&nbsp;");
					itemHolder = itemHolder.replace("#Item.Attr#", "&nbsp;");
					itemHolder = itemHolder.replace("#Item.Size#", "&nbsp;");
					itemHolder = itemHolder.replace("#Item.SerialNumber#",
							"&nbsp;");
					itemHolder = itemHolder
							.replace("#Item.Quantity#", "&nbsp;");

					itemHolder = itemHolder.replace("#Item.ALU#", "&nbsp;");
					itemHolder = itemHolder.replace("#Item.Lookup#", "&nbsp;");
					itemHolder = itemHolder
							.replace("#Item.Discount#", "&nbsp;");
					itemHolder = itemHolder.replace("#Item.DiscountPercent#",
							"&nbsp;");

					itemHolder = itemHolder.replace("#Item.UnitPrice#", "Tax");
					// itemHolder = itemHolder.replace("#Item.Total#",
					// "<strong>" + receiptJSONObj.get("TotalTax") +
					// "</strong>");
					itemHolder = itemHolder.replace("#Item.Total#", "<strong>"
							+ decimalFormat.format(totTax) + "</strong>");
					replacedStr += itemHolder;

					// Shipping
					if (((JSONObject) jsonMainObj.get("Receipt"))
							.get("Shipping") != null) {
						itemHolder = loopBlockOne;
						itemHolder = itemHolder.replace("#Item.Description#",
								"&nbsp;");
						itemHolder = itemHolder
								.replace("#Item.Attr#", "&nbsp;");
						itemHolder = itemHolder
								.replace("#Item.Size#", "&nbsp;");
						itemHolder = itemHolder.replace("#Item.SerialNumber#",
								"&nbsp;");
						itemHolder = itemHolder.replace("#Item.Description1#",
								"&nbsp;");
						itemHolder = itemHolder.replace("#Item.Description2#",
								"&nbsp;");
						itemHolder = itemHolder.replace("#Item.Quantity#",
								"&nbsp;");

						itemHolder = itemHolder.replace("#Item.ALU#", "&nbsp;");
						itemHolder = itemHolder.replace("#Item.Lookup#",
								"&nbsp;");
						itemHolder = itemHolder.replace("#Item.Discount#",
								"&nbsp;");
						itemHolder = itemHolder.replace(
								"#Item.DiscountPercent#", "&nbsp;");

						itemHolder = itemHolder.replace("#Item.UnitPrice#",
								"Shipping");
						// itemHolder = itemHolder.replace("#Item.Total#",
						// "<strong>" +
						// ((JSONObject)jsonMainObj.get("Receipt")).get("Shipping")
						// + "</strong>");
						itemHolder = itemHolder.replace("#Item.Total#",
								"<strong>" + decimalFormat.format(shipping)
										+ "</strong>");
						replacedStr += itemHolder;
					}

					// Fee
					if (((JSONObject) jsonMainObj.get("Receipt")).get("Fee") != null) {
						itemHolder = loopBlockOne;
						itemHolder = itemHolder.replace("#Item.Description#",
								"&nbsp;");
						itemHolder = itemHolder
								.replace("#Item.Attr#", "&nbsp;");
						itemHolder = itemHolder
								.replace("#Item.Size#", "&nbsp;");
						itemHolder = itemHolder.replace("#Item.Description1#",
								"&nbsp;");
						itemHolder = itemHolder.replace("#Item.Description2#",
								"&nbsp;");
						itemHolder = itemHolder.replace("#Item.Quantity#",
								"&nbsp;");

						itemHolder = itemHolder.replace("#Item.ALU#", "&nbsp;");
						itemHolder = itemHolder.replace("#Item.Lookup#",
								"&nbsp;");
						itemHolder = itemHolder.replace("#Item.Discount#",
								"&nbsp;");
						itemHolder = itemHolder.replace(
								"#Item.DiscountPercent#", "&nbsp;");

						itemHolder = itemHolder.replace("#Item.SerialNumber#",
								"&nbsp;");
						itemHolder = itemHolder
								.replace(
										"#Item.UnitPrice#",
										"Fees-"
												+ ((JSONObject) jsonMainObj
														.get("Receipt"))
														.get("FeeType"));
						// itemHolder = itemHolder.replace("#Item.Total#",
						// "<strong>" +
						// ((JSONObject)jsonMainObj.get("Receipt")).get("Fee") +
						// "</strong>");
						itemHolder = itemHolder.replace("#Item.Total#",
								"<strong>" + decimalFormat.format(fee)
										+ "</strong>");
						replacedStr += itemHolder;
					}

					// Total
					itemHolder = loopBlockOne;
					itemHolder = itemHolder.replace("#Item.Description#",
							"&nbsp;");
					itemHolder = itemHolder.replace("#Item.Attr#", "&nbsp;");
					itemHolder = itemHolder.replace("#Item.Size#", "&nbsp;");
					itemHolder = itemHolder.replace("#Item.Description1#",
							"&nbsp;");
					itemHolder = itemHolder.replace("#Item.Description2#",
							"&nbsp;");
					itemHolder = itemHolder
							.replace("#Item.Quantity#", "&nbsp;");

					itemHolder = itemHolder.replace("#Item.ALU#", "&nbsp;");
					itemHolder = itemHolder.replace("#Item.Lookup#", "&nbsp;");
					itemHolder = itemHolder
							.replace("#Item.Discount#", "&nbsp;");
					itemHolder = itemHolder.replace("#Item.DiscountPercent#",
							"&nbsp;");

					itemHolder = itemHolder.replace("#Item.SerialNumber#",
							"&nbsp;");
					itemHolder = itemHolder.replace("#Item.UnitPrice#",
							"<strong> Total </strong>");
					// itemHolder = itemHolder.replace("#Item.Total#",
					// "<strong>" +
					// ((JSONObject)jsonMainObj.get("Receipt")).get("Total") +
					// "</strong>");
					itemHolder = itemHolder.replace("#Item.Total#", "<strong>"
							+ decimalFormat.format(total) + "</strong>");
					replacedStr += itemHolder;

					templateContentStr = templateContentStr.replace(
							loopBlockOne, replacedStr);
				} else {

					templateContentStr = templateContentStr
							.replace("#Item.Description#", "&nbsp;")
							.replace("#Item.Attr#", "&nbsp;")
							.replace("#Item.Description1#", "&nbsp;")
							.replace("#Item.Description2#", "&nbsp;")
							.replace("#Item.Quantity#", "&nbsp;")
							.replace("#Item.Size#", "&nbsp;")
							.replace("#Item.SerialNumber#", "&nbsp;")

							.replace("#Item.ALU#", "&nbsp;")
							.replace("#Item.Lookup#", "&nbsp;")
							.replace("#Item.Discount#", "&nbsp;")
							.replace("#Item.DiscountPercent#", "&nbsp;")

							.replace("#Item.UnitPrice#",
									"<strong> Sub Total </strong>")
							// .replace("#Item.Total#", "<strong>" +
							// receiptJSONObj.get("Subtotal") + "</strong>");
							.replace(
									"#Item.Total#",
									"<strong>" + decimalFormat.format(subTotal)
											+ "</strong>");

				}
			}

			if (templateContentStr.indexOf(paymentSetPlaceHoldersArr[0]) != -1
					&& templateContentStr.indexOf(paymentSetPlaceHoldersArr[4]) != -1) {

				String loopBlockTwo = templateContentStr.substring(
						templateContentStr
								.indexOf(paymentSetPlaceHoldersArr[0])
								+ paymentSetPlaceHoldersArr[0].length(),
						templateContentStr
								.indexOf(paymentSetPlaceHoldersArr[4]));

				// logger.debug("Payment HTML is :"+ loopBlockTwo);

				// String replacedStr2 =
				// replaceLoopBlock(loopBlockTwo,userJSONSettingHM,jsonMainObj);
				// String replacedStr2 = "";

				// logger.debug("Payment block after replace is : "+
				// replacedStr2);
				/*
				 * if(replacedStr2 == null) { logger.debug(
				 * "**** Exception : Error occured while replacing the PAYMENT default values ... "
				 * ); return null; }
				 */

				// Add extra fields to template
				// logger.debug("PAYMENT extra fields are getting added....");
				// JSONObject receiptJSONObj =
				// (JSONObject)jsonMainObj.get("Receipt");
				/*
				 * if(receiptJSONObj == null) {
				 * logger.debug("**** Exception : Receipt Object Not found ***"
				 * ); return null; }
				 */

				String paymentParts = "";
				DecimalFormat deciFormat = new DecimalFormat("#,###,##0.00");
				if (jsonMainObj.get("Cash") != null) {
					JSONObject cashObj = (JSONObject) jsonMainObj.get("Cash");
					Double cashVal = (Double) (cashObj.get("Amount").toString()
							.isEmpty() ? Double.parseDouble("0.00") : Double
							.parseDouble(cashObj.get("Amount").toString()));

					// String cashVal =
					// deciFormat.format(cashObj.get("Amount")).toString().isEmpty()
					// ? Double.parseDouble("0.00") :
					// deciFormat.format(cashObj.get("Amount")).toString();
					paymentParts += GetMergedPaymentPart(loopBlockTwo, "",
							"Cash", deciFormat.format(cashVal));
				}

				if (jsonMainObj.get("StoreCredit") != null) {
					// Added after new DR Schema
					JSONArray storeCreditCardArr = (JSONArray) jsonMainObj
							.get("StoreCredit");

					for (Object object : storeCreditCardArr) {

						JSONObject tempStoreCreditObj = (JSONObject) object;
						Double storeCreditVal = (Double) (tempStoreCreditObj
								.get("Amount").toString().isEmpty() ? Double
								.parseDouble("0.00") : Double
								.parseDouble(tempStoreCreditObj.get("Amount")
										.toString()));
						// String storeCreditVal =
						// deciFormat.format(tempStoreCreditObj.get("Amount")).toString().isEmpty()
						// ? Double.parseDouble("0.00") :
						// deciFormat.format(tempStoreCreditObj.get("Amount")).toString();
						paymentParts += GetMergedPaymentPart(loopBlockTwo, "",
								"Store Credit",
								deciFormat.format(storeCreditVal));
					}

					/*
					 * JSONObject tempStoreCreditObj = null; try {
					 * tempStoreCreditObj =
					 * (JSONObject)jsonMainObj.get("StoreCredit"); }
					 * catch(ClassCastException e) { tempStoreCreditObj =
					 * (JSONObject
					 * )((JSONArray)jsonMainObj.get("StoreCredit")).get(0); }
					 * paymentParts +=
					 * GetMergedPaymentPart(loopBlockTwo,"","SC",
					 * deciFormat.format
					 * (tempStoreCreditObj.get("Amount").toString()) );
					 */
				}

				if (jsonMainObj.get("CreditCard") != null) {
					JSONArray creditCardArr = (JSONArray) jsonMainObj
							.get("CreditCard");

					for (Object object : creditCardArr) {

						JSONObject tempObj = (JSONObject) object;
						Double creditCardVal = (Double) (tempObj.get("Amount")
								.toString().isEmpty() ? Double
								.parseDouble("0.00") : Double
								.parseDouble(tempObj.get("Amount").toString()));
						// String creditCardVal =
						// deciFormat.format(tempObj.get("Amount")).toString().isEmpty()
						// ? Double.parseDouble("0.00") :
						// deciFormat.format(tempObj.get("Amount")).toString() ;
						paymentParts += GetMergedPaymentPart(loopBlockTwo,
								tempObj.get("Number").toString(),
								tempObj.get("Type").toString(),
								deciFormat.format(creditCardVal));
					}
				}

				if (jsonMainObj.get("DebitCard") != null) {

					// Added after new DR Schema
					JSONArray debitCreditCardArr = (JSONArray) jsonMainObj
							.get("DebitCard");

					for (Object object : debitCreditCardArr) {

						JSONObject tempDebitCreditObj = (JSONObject) object;

						Double debitVal = (Double) (tempDebitCreditObj
								.get("Amount").toString().isEmpty() ? Double
								.parseDouble("0.00") : Double
								.parseDouble(tempDebitCreditObj.get("Amount")
										.toString()));
						// String debitVal =
						// deciFormat.format(tempDebitCreditObj.get("Amount")).toString().isEmpty()
						// ? Double.parseDouble("0.00") :
						// deciFormat.format(tempDebitCreditObj.get("Amount")).toString();
						paymentParts += GetMergedPaymentPart(loopBlockTwo, "",
								"Debit Card", deciFormat.format(debitVal));
					}

					/*
					 * JSONObject tempDebitCreditObj = null; try {
					 * tempDebitCreditObj =
					 * (JSONObject)jsonMainObj.get("DebitCard"); }
					 * catch(ClassCastException e) { tempDebitCreditObj =
					 * (JSONObject
					 * )((JSONArray)jsonMainObj.get("DebitCard")).get(0); }
					 * 
					 * paymentParts += GetMergedPaymentPart(loopBlockTwo,
					 * tempDebitCreditObj.get("Number").toString(), "DEBIT",
					 * deciFormat
					 * .format(tempDebitCreditObj.get("Amount").toString()) );
					 */
				}

				if (jsonMainObj.get("Gift") != null) {

					// Added after new DR Schema
					JSONArray giftArr = (JSONArray) jsonMainObj.get("Gift");

					for (Object object : giftArr) {

						JSONObject tempGiftObj = (JSONObject) object;
						Double giftVal = (Double) (tempGiftObj.get("Amount")
								.toString().isEmpty() ? Double
								.parseDouble("0.00") : Double
								.parseDouble(tempGiftObj.get("Amount")
										.toString()));
						// String giftVal =
						// deciFormat.format(tempGiftObj.get("Amount")).toString().isEmpty()
						// ? Double.parseDouble("0.00") :
						// deciFormat.format(tempGiftObj.get("Amount")).toString();
						paymentParts += GetMergedPaymentPart(loopBlockTwo, "",
								"Gift Certificate", deciFormat.format(giftVal));
					}

					/*
					 * JSONObject tempGiftObj = null; try { tempGiftObj =
					 * (JSONObject)jsonMainObj.get("GiftCertificate"); }
					 * catch(ClassCastException e) { tempGiftObj =
					 * (JSONObject)((
					 * JSONArray)jsonMainObj.get("GiftCertificate")).get(0); }
					 * 
					 * paymentParts += GetMergedPaymentPart(loopBlockTwo, "",
					 * "GiftCertificate",
					 * deciFormat.format(tempGiftObj.get("Amount").toString())
					 * );
					 */
				}

				if (jsonMainObj.get("GiftCard") != null) {

					// Added after new DR Schema
					JSONArray giftArr = (JSONArray) jsonMainObj.get("GiftCard");

					for (Object object : giftArr) {

						JSONObject tempGiftCardObj = (JSONObject) object;
						Double giftCrdVal = (Double) (tempGiftCardObj
								.get("Amount").toString().isEmpty() ? Double
								.parseDouble("0.00") : Double
								.parseDouble(tempGiftCardObj.get("Amount")
										.toString()));
						// String giftCrdVal =
						// deciFormat.format(tempGiftCardObj.get("Amount")).toString().isEmpty()
						// ? Double.parseDouble("0.00") :
						// deciFormat.format(tempGiftCardObj.get("Amount")).toString();
						paymentParts += GetMergedPaymentPart(loopBlockTwo, "",
								"Gift Card", deciFormat.format(giftCrdVal));
					}

					/*
					 * JSONObject tempGiftCardObj = null; try { tempGiftCardObj
					 * = (JSONObject)jsonMainObj.get("GiftCard"); }
					 * catch(ClassCastException e) { tempGiftCardObj =
					 * (JSONObject
					 * )((JSONArray)jsonMainObj.get("GiftCard")).get(0); }
					 * 
					 * paymentParts +=
					 * GetMergedPaymentPart(loopBlockTwo,"","GC",
					 * deciFormat.format
					 * (tempGiftCardObj.get("Amount").toString()) );
					 */

				}

				if (jsonMainObj.get("Charge") != null) {
					// Added after new DR Schema
					JSONObject chargeObj = (JSONObject) jsonMainObj
							.get("Charge");
					Double chargeVal = (Double) (chargeObj.get("Amount")
							.toString().isEmpty() ? Double.parseDouble("0.00")
							: Double.parseDouble(chargeObj.get("Amount")
									.toString()));
					// String chargeVal =
					// deciFormat.format(chargeObj.get("Amount")).toString().isEmpty()
					// ? Double.parseDouble("0.00") :
					// deciFormat.format(chargeObj.get("Amount")).toString();
					paymentParts += GetMergedPaymentPart(loopBlockTwo, "",
							"Charge", deciFormat.format(chargeVal));
				}

				if (jsonMainObj.get("COD") != null) {
					// Added after new DR Schema

					JSONObject CODObj = (JSONObject) jsonMainObj.get("COD");
					Double codVal = (Double) (CODObj.get("Amount").toString()
							.isEmpty() ? Double.parseDouble("0.00") : Double
							.parseDouble(CODObj.get("Amount").toString()));
					// String codVal =
					// deciFormat.format(CODObj.get("Amount")).toString()
					// .isEmpty() ? Double.parseDouble("0.00") :
					// deciFormat.format(CODObj.get("Amount")).toString() ;
					paymentParts += GetMergedPaymentPart(loopBlockTwo, "",
							"COD ", deciFormat.format(codVal));
				}

				if (jsonMainObj.get("Deposit") != null) {
					// Added after new DR Schema
					JSONObject depositObj = (JSONObject) jsonMainObj
							.get("Deposit");
					Double depositVal = (Double) (depositObj.get("Amount")
							.toString().isEmpty() ? Double.parseDouble("0.00")
							: Double.parseDouble(depositObj.get("Amount")
									.toString()));
					// String depositVal =
					// deciFormat.format(depositObj.get("Amount")).toString().isEmpty()
					// ? Double.parseDouble("0.00") :
					// deciFormat.format(depositObj.get("Amount")).toString();
					paymentParts += GetMergedPaymentPart(loopBlockTwo, "",
							"Deposit", deciFormat.format(depositVal));
				}

				if (jsonMainObj.get("Check") != null) {

					JSONObject checkObj = (JSONObject) jsonMainObj.get("Check");
					Double checkVal = (Double) (checkObj.get("Amount")
							.toString().isEmpty() ? Double.parseDouble("0.00")
							: Double.parseDouble(checkObj.get("Amount")
									.toString()));
					// String checkVal =
					// deciFormat.format(checkObj.get("Amount")).toString().isEmpty()
					// ? Double.parseDouble("0.00") :
					// deciFormat.format(checkObj.get("Amount")).toString();

					paymentParts += GetMergedPaymentPart(loopBlockTwo, "",
							"Check" + "-" + checkObj.get("Number"),
							deciFormat.format(checkVal));
				}

				if (jsonMainObj.get("FC") != null) {
					// Added after new DR Schema
					JSONObject FCObj = (JSONObject) jsonMainObj.get("FC");
					Double fcVal = (Double) (FCObj.get("Amount").toString()
							.isEmpty() ? Double.parseDouble("0.00") : Double
							.parseDouble(FCObj.get("Amount").toString()));
					// String fcVal =
					// deciFormat.format(FCObj.get("Amount")).toString().isEmpty()
					// ? Double.parseDouble("0.00") :
					// deciFormat.format(FCObj.get("Amount")).toString();
					paymentParts += GetMergedPaymentPart(loopBlockTwo, "",
							"Foreign Currency", deciFormat.format(fcVal));
				}

				// Added another 3 tender types(rarely used)

				if (jsonMainObj.get("Payments") != null) {

					JSONArray paymentsArr = (JSONArray) jsonMainObj
							.get("Payments");

					for (Object object : paymentsArr) {

						JSONObject tempPaymentObj = (JSONObject) object;
						Double paymentVal = (Double) (tempPaymentObj
								.get("Amount").toString().isEmpty() ? Double
								.parseDouble("0.00") : Double
								.parseDouble(tempPaymentObj.get("Amount")
										.toString()));
						// String paymentVal =
						// deciFormat.format(tempPaymentObj.get("Amount")).toString().isEmpty()
						// ? Double.parseDouble("0.00"):
						// deciFormat.format(tempPaymentObj.get("Amount")).toString();
						paymentParts += GetMergedPaymentPart(loopBlockTwo, "",
								"Payments", deciFormat.format(paymentVal));
					}

				}

				if (jsonMainObj.get("TravelerCheck") != null) {
					JSONObject travelerCheckObj = (JSONObject) jsonMainObj
							.get("TravelerCheck");
					Double travelerCheckVal = (Double) (travelerCheckObj
							.get("Amount").toString().isEmpty() ? Double
							.parseDouble("0.00") : Double
							.parseDouble(travelerCheckObj.get("Amount")
									.toString()));
					// String travelerCheckVal =
					// deciFormat.format(travelerCheckObj.get("Amount")).toString().isEmpty()
					// ? Double.parseDouble("0.00"):
					// deciFormat.format(travelerCheckObj.get("Amount")).toString();
					paymentParts += GetMergedPaymentPart(loopBlockTwo, "",
							"Traveler Check",
							deciFormat.format(travelerCheckVal));
				}

				if (jsonMainObj.get("FCCheck") != null) {

					JSONObject FCCheckObj = (JSONObject) jsonMainObj
							.get("FCCheck");
					Double fcCheckval = (Double) (FCCheckObj.get("Amount")
							.toString().isEmpty() ? Double.parseDouble("0.00")
							: Double.parseDouble(FCCheckObj.get("Amount")
									.toString()));
					// String fcCheckval =
					// deciFormat.format(FCCheckObj.get("Amount")).toString().isEmpty()
					// ? Double.parseDouble("0.00") :
					// deciFormat.format(FCCheckObj.get("Amount")).toString();
					paymentParts += GetMergedPaymentPart(loopBlockTwo, "",
							"FCCheck", deciFormat.format(fcCheckval));
				}

				// logger.debug("** After process paymentParts val :  " +
				// paymentParts);

				if (paymentParts != null) {

					templateContentStr = templateContentStr.replace(
							loopBlockTwo, paymentParts);
				}
			}

			// Remove ##START## ##END## PH from the template
			templateContentStr = templateContentStr.replace(
					paymentSetPlaceHoldersArr[0], "").replace(
					paymentSetPlaceHoldersArr[4], "");
			templateContentStr = templateContentStr.replace(
					ItemsSetPlaceHoldersArr[0], "").replace(
					ItemsSetPlaceHoldersArr[5], "");

			// logger.debug("After processing LOOP Contents : "+
			// templateContentStr);

			// Replace All individual place holders place Holders
			Set<String> set = userJSONSettingHM.keySet();
			String jsonKey;
			String[] jsonPathArr;
			String jsonKeyValue = "";

			DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");
			Double amount = (Double) (receiptJSONObj.get("Total").toString()
					.isEmpty() ? Double.parseDouble("0.00") : Double
					.parseDouble(receiptJSONObj.get("Total").toString()));
			templateContentStr = templateContentStr.replace(
					Constants.DR_RECEIPT_AMOUNT,
					(String) decimalFormat.format(amount));

			for (String phStr : set) {

				jsonKeyValue = "";
				JSONObject parentObject = null;
				JSONArray parentArrayObject = null;

				if (templateContentStr.indexOf(phStr) != -1) { // PH exists in
																// template

					jsonKey = userJSONSettingHM.get(phStr);
					// logger.debug("*******<< jsonKey : "+ jsonKey);

					if (jsonKey.indexOf("_$$_") != -1) {

						// logger.debug("contains dollar .....");

						String[] jsonKeytokens = jsonKey.split(Pattern
								.quote("_$$_"));

						for (int j = 0; j < jsonKeytokens.length; j++) {

							// . path exists in json key

							jsonPathArr = jsonKeytokens[j].split(Pattern
									.quote("."));
							// logger.debug("*******<< jsonKey : "+
							// jsonPathArr[0] + " ::: " + jsonPathArr[1]);

							try {

								// logger.debug("SINGLE OBJ CREATED...");
								parentObject = (JSONObject) jsonMainObj
										.get(jsonPathArr[0]);
							} catch (ClassCastException e) {

								// logger.debug("ARRAY OBJ CREATED...");
								parentArrayObject = (JSONArray) jsonMainObj
										.get(jsonPathArr[0]);
								// logger.debug("--Array---"+
								// parentArrayObject);
								parentObject = (JSONObject) parentArrayObject
										.get(0);
								// logger.debug("--First element in array---"+
								// parentObject);
								// logger.debug("--Second element in array---"+
								// (JSONObject)parentArrayObject.get(1));
							}

							if (parentObject != null) {

								try {

									if (parentObject.get(jsonPathArr[1]) != null) {

										jsonKeyValue += (String) parentObject
												.get(jsonPathArr[1]) + " ";
									}
								} catch (ClassCastException e) {
									try {
										jsonKeyValue += ((Double) parentObject
												.get(jsonPathArr[1])) + " ";
									} catch (ClassCastException f) {
										jsonKeyValue += ((Long) parentObject
												.get(jsonPathArr[1])) + " ";
									}
								}

							} else {

								// logger.debug("** Parent JSON is null *****");
							}

						}

						// logger.debug("*** Replacing " + phStr + " with " +
						// jsonKeyValue);
						templateContentStr = templateContentStr.replaceAll(
								phStr, jsonKeyValue);

					} else if (jsonKey.contains(".")) { // . path exists in json
														// key

						jsonPathArr = jsonKey.split(Pattern.quote("."));
						// logger.debug("*******<< jsonKey : "+ jsonPathArr[0] +
						// " ::: " + jsonPathArr[1]);

						try {

							// logger.debug("SINGLE OBJ CREATED...");
							parentObject = (JSONObject) jsonMainObj
									.get(jsonPathArr[0]);
						} catch (ClassCastException e) {

							// logger.debug("ARRAY OBJ CREATED...");
							parentArrayObject = (JSONArray) jsonMainObj
									.get(jsonPathArr[0]);
							// logger.debug("--Array---"+ parentArrayObject);
							parentObject = (JSONObject) parentArrayObject
									.get(0);
							// logger.debug("--First element in array---"+
							// parentObject);
							// logger.debug("--Second element in array---"+
							// (JSONObject)parentArrayObject.get(1));
						}

						if (parentObject != null) {

							try {

								if (parentObject.get(jsonPathArr[1]) != null) {

									jsonKeyValue = (String) parentObject
											.get(jsonPathArr[1]);
								}
							} catch (ClassCastException e) {
								try {
									jsonKeyValue = ((Double) parentObject
											.get(jsonPathArr[1])).toString();
								} catch (ClassCastException f) {
									jsonKeyValue = ((Long) parentObject
											.get(jsonPathArr[1])).toString();
								}
							}

							// logger.debug("*** Replacing " + phStr + " with "
							// + jsonKeyValue);
							templateContentStr = templateContentStr.replaceAll(
									phStr, jsonKeyValue);
						} else {

							// logger.debug("** Parent JSON is null *****");
						}

					}
				}
			} // for

			// logger.debug("After processing INDIVIDUAL Contents : "+
			// templateContentStr);

			// remove all no-value place holders form template
			String digiRcptDefPHStr = PropertyUtil
					.getPropertyValue("DRPlaceHolders");
			if (digiRcptDefPHStr != null) {
				String[] defPHArr = digiRcptDefPHStr.split(Pattern.quote(","));
				for (int i = 0; i < defPHArr.length; i++) {
					templateContentStr = templateContentStr.replaceAll(
							defPHArr[i], "");
				}

				// logger.debug("After Removing no-value Place holder(#PH#) values.. Content is :"
				// + templateContentStr );
			}

			// Add domain name to images urls
			String appUrl = PropertyUtil.getPropertyValue("subscriberIp");
			templateContentStr = templateContentStr.replaceAll(
					"/subscriber/SystemData/digital-templates/", appUrl
							+ "/subscriber/SystemData/digital-templates/");

			// Get email from json request..
			JSONObject receiptObj = (JSONObject) jsonMainObj.get("Receipt");
			String emailId = (String) receiptObj.get("BillToEMail");

			if (emailId == null || emailId.trim().length() == 0) {

				jsonMessage = "Bill-To-EmailId is null in json request";
				logger.debug("**** Bill-To-EmailId is null is json request returning ...");
				return null;
			}

			// preparing html content which handles clicks
			// String htmlContent = PrepareFinalHtml
			// .prepareStuff(templateContentStr);

			String htmlContent = templateContentStr;
			htmlContent = htmlContent.replaceAll(Pattern.quote("|^"), "[")
					.replaceAll(Pattern.quote("^|"), "]");
			String phStr = null;
			if (drSent.getPhValStr() != null) {
				phStr = drSent.getPhValStr();

				String[] phKeyValPairStrArr = phStr
						.split(Constants.ADDR_COL_DELIMETER);
				for (String keyValStr : phKeyValPairStrArr) {
					String[] keyPairStrArr = keyValStr
							.split(Constants.DELIMETER_DOUBLECOLON);

					try {
						htmlContent = htmlContent.replace(keyPairStrArr[0],
								keyPairStrArr[1]);
					} catch (Exception e) {
						htmlContent = htmlContent.replace(keyPairStrArr[0], "");
					}

				}

			}

			if (htmlContent.contains("href='")) {
				htmlContent = htmlContent
						.replaceAll("href='([^\"]+)'",
								"href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");

			}
			if (htmlContent.contains("href=\"")) {
				htmlContent = htmlContent
						.replaceAll("href=\"([^\"]+)\"",
								"href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
			}

			return htmlContent;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}

	}

	private String replaceLoopBlock(String loopBlock,
			Map<String, String> userJSONSettingHM, JSONObject jsonMainObj) {
		try {
			String finalHtmlBlockVal = "";
			Matcher matcher = Pattern.compile("(#\\w+).(\\w+#)").matcher(
					loopBlock);

			// JSONObject jsonObj = null;

			JSONArray jsonArr = null;

			DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");

			if (matcher.find()) {

				// logger.debug("--PLACE HOLDERS FOUND --"+ matcher.group(0));
				// logger.debug("--1--"+
				// userJSONSettingHM.get(matcher.group(0)));

				if (userJSONSettingHM.get(matcher.group(0)) == null) {
					//
					logger.debug("*** Place holder value is not found in HashMap ***");
					return null;
				}

				String[] arrayElement = userJSONSettingHM.get(matcher.group(0))
						.split(Pattern.quote("."));

				// Items from Items.billTomail
				logger.debug("arrayElement[0]" + arrayElement[0]);
				jsonArr = (JSONArray) jsonMainObj.get(arrayElement[0]);

			} else {

				logger.debug("Pattern not found");
				return null;
			}

			matcher.reset();

			if (jsonArr == null) {
				return null;
			}

			// logger.debug("Array size :" + jsonArr.size());

			for (int i = 0; i < jsonArr.size(); i++) // Loop all array elements
			{

				JSONObject jsonObj = (JSONObject) jsonArr.get(i);
				String tempStr = loopBlock;
				// logger.debug(" Current jsonobj object from the jsonarray is :"+
				// jsonObj);

				while (matcher.find()) { // Loop all fields
					String val = "";

					String placeHolderStr = matcher.group(0);
					// logger.debug(" Found place holders : " +placeHolderStr);
					String jsonKey = userJSONSettingHM.get(placeHolderStr);
					String[] jsonValArr = null;
					if (jsonKey != null) {

						// logger.debug("*** JSON key path  is : " + jsonKey);
						// if contains _$$_ multi valued keys .
						if (jsonKey.indexOf("_$$_") != -1) {

							// logger.debug("******************** Multi values in json Key exist ...");
							// logger.debug("*** json Val is : "+ jsonKey);

							String[] jsonKeytokens = jsonKey.split(Pattern
									.quote("_$$_"));

							for (int j = 0; j < jsonKeytokens.length; j++) {

								// logger.debug("Individual key is  : "+
								// jsonKeytokens[j]);
								jsonValArr = jsonKeytokens[j].split(Pattern
										.quote("."));
								// logger.debug("key is "+ jsonValArr[1]);

								if (jsonObj.get(jsonValArr[1]) == null) {

									logger.debug("**** Excepted value not found in the json object ***");
									continue;
								}

								// logger.debug("***^^ replace template with object value  : "+
								// jsonObj.get(jsonValArr[1])+ " key is "+
								// jsonValArr[1]);

								try {

									val += (String) jsonObj.get(jsonValArr[1])
											+ " ";
								} catch (ClassCastException e) {
									try {
										val += ((Long) jsonObj
												.get(jsonValArr[1])).toString()
												+ " ";
									} catch (ClassCastException f) {
										val += ((Double) jsonObj
												.get(jsonValArr[1])).toString()
												+ " ";
									}
								}
							}
							// logger.debug("Value(1) is : " + val);
							tempStr = tempStr.replace(placeHolderStr, val);
						} else {

							// logger.debug("<<<< json Val is : "+ jsonKey);

							jsonValArr = jsonKey.split(Pattern.quote("."));
							// logger.debug("***$$ replace template with object value  : "+
							// jsonObj.get(jsonValArr[1])+" key is "+ jsonKey);

							if (jsonObj.get(jsonValArr[1]) == null) {
								logger.debug("**** Excepted value not found in the json object ***");
								continue;
							}

							try {
								// val = (String)jsonObj.get(jsonValArr[1]);
								if (jsonKey
										.equals(Constants.DR_ITEM_INVC_ITEM_PRC)
										|| jsonKey
												.equals(Constants.DR_ITEM_EXTPRC)
										|| jsonKey
												.equals(Constants.DR_ITEM_DOCI_TEM_DISC_AMT)
										|| jsonKey
												.equals(Constants.DR_ITEM_DOC_ITEM_DISC)
										|| jsonKey
												.equals(Constants.DR_ITEM_TAX)) {
									Double value = (Double) (jsonObj
											.get(jsonValArr[1]).toString()
											.isEmpty() ? Double
											.parseDouble("0.00") : Double
											.parseDouble(jsonObj.get(
													jsonValArr[1]).toString()));

									val = (String) decimalFormat.format(value);

								} else {
									val = (String) jsonObj.get(jsonValArr[1]);
								}
							} catch (ClassCastException e) {
								try {
									val = ((Long) jsonObj.get(jsonValArr[1]))
											.toString();
								} catch (ClassCastException f) {
									val = ((Double) jsonObj.get(jsonValArr[1]))
											.toString();
								}
							}

							// logger.debug("Value(2) is : " + val);
							tempStr = tempStr.replace(matcher.group(0), val);
						}
					} // if

				}

				matcher.reset();

				finalHtmlBlockVal = finalHtmlBlockVal + tempStr;

			}

			// logger.debug("final block to replace is "+ finalHtmlBlockVal);
			return finalHtmlBlockVal;
		} catch (Exception e) {

			logger.error("Exception ::", e);
			return null;
		}
	}

	// View all
	public void getAllDrView(String email, long userId) {
		try {
			Components.removeAllChildren(viewAllDrWinId$viewAllDrRowsId);
			List<DRSent> list = drSentDao.findAllDrView(email, userId);
			viewAllDrWinId.setVisible(true);
			viewAllDrWinId.doHighlighted();
			viewAllDrWinId.setPosition("center");
			if (list != null && list.size() > 0) {
				logger.info("Log size : " + list.size());

				for (DRSent drSent : list) {

					String status = drSent.getStatus();
					Row row = new Row();
					Label lbl = new Label(email);
					lbl.setWidth("330px");
					lbl.setParent(row);

					lbl = new Label(MyCalendar.calendarToString(
							drSent.getSentDate(),
							MyCalendar.FORMAT_DATETIME_STYEAR, clientTimeZone));
					lbl.setWidth("170px");
					lbl.setParent(row);

					if (status.equalsIgnoreCase(Constants.DR_STATUS_DROPPED)
							|| status
									.equalsIgnoreCase(Constants.DR_STATUS_BOUNCE)) {
						status = Constants.DR_STATUS_BOUNCED;
					}
					lbl = new Label(status);
					lbl.setParent(row);

					row.setParent(viewAllDrWinId$viewAllDrRowsId);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
	}

	public void getAllSmsDrView(String mobile, long userId) {
		try {
			Components.removeAllChildren(viewAllSmsDrWinId$viewAllSmsDrRowsId);
			List<DRSMSSent> list = drSmsSentDao.findAllDrView(mobile, userId);
			viewAllSmsDrWinId.setVisible(true);
			viewAllSmsDrWinId.doHighlighted();
			viewAllSmsDrWinId.setPosition("center");
			if (list != null && list.size() > 0) {
				logger.info("Log size : " + list.size());

				for (DRSMSSent drSent : list) {

					String status = drSent.getStatus();
					Row row = new Row();
					Label lbl = new Label(mobile);
					lbl.setWidth("330px");
					lbl.setParent(row);

					lbl = new Label(MyCalendar.calendarToString(
							drSent.getSentDate(),
							MyCalendar.FORMAT_DATETIME_STYEAR, clientTimeZone));
					lbl.setWidth("170px");
					lbl.setParent(row);

					if (status.equalsIgnoreCase(Constants.DR_STATUS_DROPPED)
							|| status
									.equalsIgnoreCase(Constants.DR_STATUS_BOUNCE)) {
						status = Constants.DR_STATUS_BOUNCED;
					}
					lbl = new Label(status);
					lbl.setParent(row);

					row.setParent(viewAllSmsDrWinId$viewAllSmsDrRowsId);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
	}
	public void onClick$recipientsExportBtnId() {
		try {
			if (currentUser.getUserId() != null) {
				createWindow("Recipients Export::;::Email Address::;::Total Receipts::;::Total Unique Opens::;::Total Unique Clicks");

				anchorEvent(false);

				custExport.setVisible(true);
				custExport.doHighlighted();
			} else {

				MessageUtil.setMessage("Please select a user", "info");
			}
		} catch (Exception e) {
			logger.error("Error occured from the exportCSV method ***", e);
		}
	}
	public void onClick$recipientsSmsExportBtnId() {
		try {
			if (currentUser.getUserId() != null) {
				createSMSWindow("SMS Metrics::;::Phone::;::Total Receipts::;::Total Unique Opens::;::Total Unique Clicks on Receipt");

				anchorSMSEvent(false);

				custSmsExport.setVisible(true);
				custSmsExport.doHighlighted();
			} else {

				MessageUtil.setMessage("Please select a user", "info");
			}
		} catch (Exception e) {
			logger.error("Error occured from the exportCSV method ***", e);
		}
	}
	
	public void onClick$statusPendingExportBtnId() {
		try {
			if (currentUser.getUserId() != null) {
				//setValues();
				fromDateboxId.setValue(getStartCalendar());
				toDateboxId.setValue(getEndCalendar());
				int[] indexes= {0,0};
				exportCSV(indexes, "Status Pending");

				/*anchorEvent(false);

				custExport.setVisible(true);
				custExport.doHighlighted();*/
			} else {

				MessageUtil.setMessage("Please select a user", "info");
			}
		} catch (Exception e) {
			logger.error("Error occured from the exportCSV method ***", e);
		}
	}

	public void onClick$smsStatusPendingExportBtnId() {
		try {
			if (currentUser.getUserId() != null) {
				fromDateboxId.setValue(getStartCalendar());
				toDateboxId.setValue(getEndCalendar());
				int[] indexes= {0,0};
				exportCSV(indexes, "SMS Status Pending");

			} else {

				MessageUtil.setMessage("Please select a user", "info");
			}
		} catch (Exception e) {
			logger.error("Error occured from the exportCSV method ***", e);
		}
	}

	public void onClick$storeExportBtnId() {
		try {
			if (currentUser.getUserId() != null) {
				setValues();
				int[] indexes= {0,0,0};
				exportCSV(indexes, "Store Export");

				/*anchorEvent(false);

				custExport.setVisible(true);
				custExport.doHighlighted();*/
			} else {

				MessageUtil.setMessage("Please select a user", "info");
			}
		} catch (Exception e) {
			logger.error("Error occured from the exportCSV method ***", e);
		}
	}

	public void onClick$exportSmsBtnId() {
		try {
			if (currentUser.getUserId() != null) {
				setValues();
				createSMSWindow("SMS Delivery Report::;::SMS SBS No::;::SMS Sent Date::;::Phone::;::"
						+ "Store::;::Delivery Status::;::Opens::;::Clicks on Receipt::;::Sent Count");

				anchorSMSEvent(false);

				custSmsExport.setVisible(true);
				custSmsExport.doHighlighted();
			} else {

				MessageUtil.setMessage("Please select a user", "info");
			}
		} catch (Exception e) {
			logger.error("Error occured from the exportCSV method ***", e);
		}
	}
	public void onClick$exportBtnId() {
		try {
			if (currentUser.getUserId() != null) {
				setValues();
				createWindow("Delivery Report::;::SBS NO::;::Email Sent Date::;::Email Address::;::"
						+ "Store::;::Delivery Status::;::Opens::;::Clicks::;::Sent Count");

				anchorEvent(false);

				custExport.setVisible(true);
				custExport.doHighlighted();
			} else {

				MessageUtil.setMessage("Please select a user", "info");
			}
		} catch (Exception e) {
			logger.error("Error occured from the exportCSV method ***", e);
		}
	}

	public void anchorEvent(boolean flag) {
		List<Component> chkList = custExport$chkDivId.getChildren();
		Checkbox tempChk = null;
		for (int i = 0; i < chkList.size(); i++) {
			if (!(chkList.get(i) instanceof Checkbox))
				continue;

			tempChk = (Checkbox) chkList.get(i);
			tempChk.setChecked(flag);

		} // for

	}

	public void anchorSMSEvent(boolean flag) {
		List<Component> chkList = custSmsExport$chkDivId.getChildren();
		Checkbox tempChk = null;
		for (int i = 0; i < chkList.size(); i++) {
			if (!(chkList.get(i) instanceof Checkbox))
				continue;

			tempChk = (Checkbox) chkList.get(i);
			tempChk.setChecked(flag);

		} // for

	}
	
	public void anchorSMSEvent1(boolean flag) {
		List<Component> chkList = custSmsExport1$chkDivId.getChildren();
		Checkbox tempChk = null;
		for (int i = 0; i < chkList.size(); i++) {
			if (!(chkList.get(i) instanceof Checkbox))
				continue;

			tempChk = (Checkbox) chkList.get(i);
			tempChk.setChecked(flag);

		} // for

	}
	public void onClick$selectAllAnchr$custExport() {

		anchorEvent(true);

	}

	public void onClick$clearAllAnchr$custExport() {

		anchorEvent(false);
	}

	public void onClick$selectFieldBtnId$custExport() {

		custExport.setVisible(false);
		List<Component> chkList = custExport$chkDivId.getChildren();

		int indexes[] = new int[chkList.size()];
		boolean checked = false;

		for (int i = 0; i < chkList.size(); i++) {
			if (((Checkbox) chkList.get(i)).isChecked()) {
				indexes[i] = 0;
				checked = true;
			} else {
				indexes[i] = -1;
			}
		}

		if (checked) {
			int confirm = Messagebox.show(
					"Do you want to Export with selected fields ?", "Confirm",
					Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if (confirm == 1) {
				try {

					exportCSV(indexes,
							(String) custExport$chkDivId
									.getAttribute("exportType"));

				} catch (Exception e) {
					logger.error("Exception caught :: ", e);
				}
			} else {
				custExport.setVisible(true);
			}

		} else {

			MessageUtil.setMessage("Please select atleast one field", "red");
			custExport.setVisible(true);
		}

	}// on select

	public void onClick$selectAllAnchr$custSmsExport() {

		anchorSMSEvent(true);

	}
	public void onClick$selectAllAnchr$custSmsExport1() {

		anchorSMSEvent1(true);

	}

	public void onClick$clearAllAnchr$custSmsExport1() {

		anchorSMSEvent1(false);
	}
	public void onClick$clearAllAnchr$custSmsExport() {

		anchorSMSEvent(false);
	}

	public void onClick$selectFieldBtnId$custSmsExport() {

		custSmsExport.setVisible(false);
		List<Component> chkList = custSmsExport$chkDivId.getChildren();

		int indexes[] = new int[chkList.size()];
		boolean checked = false;

		for (int i = 0; i < chkList.size(); i++) {
			if (((Checkbox) chkList.get(i)).isChecked()) {
				indexes[i] = 0;
				checked = true;
			} else {
				indexes[i] = -1;
			}
		}

		if (checked) {
			int confirm = Messagebox.show(
					"Do you want to Export with selected fields ?", "Confirm",
					Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if (confirm == 1) {
				try {

					exportCSV(indexes,
							(String) custSmsExport$chkDivId
									.getAttribute("exportType"));

				} catch (Exception e) {
					logger.error("Exception caught :: ", e);
				}
			} else {
				custSmsExport.setVisible(true);
			}

		} else {

			MessageUtil.setMessage("Please select atleast one field", "red");
			custSmsExport.setVisible(true);
		}

	}// on select

	/*public void onClick$selectFieldBtnId$custSmsExport1() {

		custSmsExport1.setVisible(false);
		List<Component> chkList = custSmsExport1$chkDivId.getChildren();

		int indexes[] = new int[chkList.size()];
		boolean checked = false;

		for (int i = 0; i < chkList.size(); i++) {
			if (((Checkbox) chkList.get(i)).isChecked()) {
				indexes[i] = 0;
				checked = true;
			} else {
				indexes[i] = -1;
			}
		}

		if (checked) {
			int confirm = Messagebox.show(
					"Do you want to Export with selected fields ?", "Confirm",
					Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if (confirm == 1) {
				try {

					exportCSV(indexes,
							(String) custSmsExport1$chkDivId
									.getAttribute("exportType"));

				} catch (Exception e) {
					logger.error("Exception caught :: ", e);
				}
			} else {
				custSmsExport1.setVisible(true);
			}

		} else {

			MessageUtil.setMessage("Please select atleast one field", "red");
			custSmsExport1.setVisible(true);
		}

	}// on select
*/	public void exportCSV(int[] indexes, String exportType) {

		try {
			logger.debug("entered ");
			logger.info("EXPORT TYPE : "+ exportType);
			logger.info("itemCount"+drReportsLbId.getItemCount());
			logger.info("sms itemCount"+drSmsReportsLbId.getItemCount());
			if (exportType.equalsIgnoreCase("Delivery Report")
					&& drReportsLbId.getItemCount() == 0) {
				MessageUtil.setMessage(
						"No records exist in the selected search.",
						"color:red", "TOP");
				return;
			}else if (exportType.equalsIgnoreCase("SMS Delivery Report")
					&& drSmsReportsLbId.getItemCount() == 0) {
				MessageUtil.setMessage(
						"No records exist in the selected search.",
						"color:red", "TOP");
				return;
			} else if (exportType.equalsIgnoreCase("Store Export")
					&& storeReportsLbId.getItemCount() == 0) {
				MessageUtil.setMessage(
						"No records exist in the selected search.",
						"color:red", "TOP");
				return;
			} else if (exportType.equalsIgnoreCase("Status Pending")
					&& statusPendingReportsLbId.getItemCount() == 0) {
				MessageUtil.setMessage(
						"No records exist in the selected search.",
						"color:red", "TOP");
				return;
			} else if (exportType.equalsIgnoreCase("SMS Status Pending")
					&& statusPendingSmsReportsLbId.getItemCount() == 0) {
				MessageUtil.setMessage(
						"No records exist in the selected search.",
						"color:red", "TOP");
				return;
			}else if (exportType.equalsIgnoreCase("SMS Metrics")
					&& recipientsSmsReportsLbId.getItemCount() == 0) {
				MessageUtil.setMessage(
						"No records exist.",
						"color:red", "TOP");
				return;
			} else if (exportType.equalsIgnoreCase("Recipients Export") && recipientsReportsLbId.getItemCount() == 0) {
				MessageUtil.setMessage("No records exist.", "color:red", "TOP");
				return;
			}

			String ext = "csv";
			String userName = GetUser.getUserName();

			String usersParentDirectory = (String) PropertyUtil
					.getPropertyValue("usersParentDirectory");

			File downloadDir = new File(usersParentDirectory + "/" + userName
					+ "/List/download/");

			if (!downloadDir.exists()) {
				downloadDir.mkdirs();
			}

			String filePath = usersParentDirectory + "/" + userName
					+ "/List/download/DR_Report_" + System.currentTimeMillis()
					+ "." + ext;

			int size = 1000;
			StringBuffer sb = null;
			File file = new File(filePath);
			BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
			logger.debug("Writing to the file : " + filePath);

			String udfFldsLabel = Constants.STRING_NILL;
			String header[] = null;
			String fields = Constants.STRING_NILL;
			if (exportType.equalsIgnoreCase("Delivery Report")) {
				header = "Email Sent Date::dr.sent_date::;::Email Address::dr.email_id::;::Store::dr.store_number::;::Delivery Status::dr.status::;::Opens::dr.opens::;::Clicks::dr.clicks::;::Sent Count::dr.sent_count"
						.split("::;::");
			}else if (exportType.equalsIgnoreCase("SMS Delivery Report")) {
				header = "SMS Sent Date::dr.sent_date::;::Phone::dr.mobile::;::Store::dr.store_number::;::Delivery Status::dr.status::;::Opens::dr.opens::;::Clicks::dr.clicks::;::Sent Count::dr.sent_count"
						.split("::;::");
			}
			else if (exportType.equalsIgnoreCase("Store Export")) {
//				header = "Store::store_number::;::Total Receipts Sent::COUNT(dr.id) as sentCount"
//						.split("::;::");
				header = "Store::subTable.store_number::;::Total Email Receipts Sent::sum(emailCount) AS emailCount::;::Total SMS Receipts Sent::sum(smsCount) AS smsCount"
						.split("::;::");
			} else if (exportType.equalsIgnoreCase("Status Pending")) {
				header = "Email Address::dr.email_id::;::Sent Date::dr.sent_date"
						.split("::;::");
			}else if (exportType.equalsIgnoreCase("SMS Status Pending")) {
				header = "Phone::dr.mobile::;::Sent Date::dr.sent_date"
						.split("::;::");
			}else if (exportType.equalsIgnoreCase("SMS Metrics")) {
				header = "Phone::dr.mobile::;::Total Receipts::Count(dr.id) as totalSentCount::;::Total Unique Opens::SUM(IF(dr.opens >0, 1,0)) as uniopens::;::Total Unique Clicks::SUM(IF(dr.clicks >0, 1,0)) as uniclicks"
						.split("::;::");
			}
				else {
				header = "Email Address::dr.email_id::;::Total Receipts::Count(dr.id) as totalSentCount::;::Total Unique Opens::SUM(IF(dr.opens >0, 1,0)) as uniopens::;::Total Unique Clicks::SUM(IF(dr.clicks >0, 1,0)) as uniclicks"
						.split("::;::");
			}
			for (int i = 0; i < indexes.length; i++) {
				if (indexes[i] == 0) {
					udfFldsLabel += "\"" + header[i].split("::")[0] + "\"" + ",";
					if (!fields.isEmpty())
						fields += ",";
					fields += header[i].split("::")[1];
				}
			}

			sb = new StringBuffer();

			sb.append(udfFldsLabel);

			sb.append("\r\n");

			bw.write(sb.toString());
			String udfFldsLabel1 = "";

			String query = Constants.STRING_NILL;

			if (exportType.equalsIgnoreCase("Delivery Report")) {

				String key = searchBoxId.getValue();
				// String store = selectedStoreItem[1].equals(0) ? null :
				// (String)((Listitem) selectedStoreItem[0]).getValue();
				String subQry = "SELECT dr.id,dr.SBS_no," + fields
						+ " FROM dr_sent dr where   dr.user_id= "
						+ currentUser.getUserId() + " and dr.sent_date  >= '"
						+ fromDate + "' and  dr.sent_date <= '" + endDate + "'";

				if (key != null && key.trim().length() > 0) {

					subQry += " AND dr.email_id like '%" + key.trim() + "%'";
				}
				if (!selectedStoreItem[1].equals(0)) {
					subQry += " AND dr.store_number = '"
							+ (String) ((Listitem) selectedStoreItem[0])
									.getValue() + "'";
				}

				
				
				if(emailSatusTobeFetched.contains("_fetch_clicks_also_") && !emailSatusTobeFetched.contains("_fetch_opens_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
					subQry += " AND dr.clicks is not null AND dr.clicks !=0  ";
					
				}
				else if(emailSatusTobeFetched.contains("_fetch_opens_also_") && !emailSatusTobeFetched.contains("_fetch_clicks_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
					subQry += " AND dr.opens is not null AND dr.opens !=0  ";
				}
				else if(emailSatusTobeFetched.contains("_fetch_opens_also_") && emailSatusTobeFetched.contains("_fetch_clicks_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
					subQry += " AND ((dr.opens is not null AND dr.opens !=0)   AND (dr.clicks is not null AND dr.clicks !=0))   ";
				}
				
				
				if(! emailSatusTobeFetched.contains("special_condtion_for_all") ){
					subQry += "  AND dr.status IN("+emailSatusTobeFetched+")  ";
				}
				
				
				
				
				if(((emailSatusTobeFetched.contains("dropped") || emailSatusTobeFetched.contains("bounce")) && ! emailSatusTobeFetched.contains("special_condtion_for_all"))){ 
					
					if( !emailSatusTobeFetched.contains("Success") && !emailSatusTobeFetched.contains("spamreport")){
						subQry += "  AND (dr.status='bounce' OR dr.status='dropped')  ";
					}else {
						subQry += "  AND (dr.status='bounce' OR dr.status='dropped')   AND  (dr.status='spamreport' OR dr.status='Success')   ";
					}
					
				}
				
				
				
			//	subQry = subQry + " AND dr.status NOT IN('"+Constants.DR_STATUS_SUBMITTED+"')";
				
				query = subQry + " ORDER BY sent_date desc";
			}else if(exportType.equalsIgnoreCase("SMS Delivery Report")) {


				String key = searchSmsBoxId.getValue();
				String subQry = "SELECT dr.id,dr.SBS_no," + fields
						+ " FROM dr_sms_sent dr where   dr.user_id= "
						+ currentUser.getUserId() + " and dr.sent_date  >= '"
						+ fromDate + "' and  dr.sent_date <= '" + endDate + "'";

				if (key != null && key.trim().length() > 0) {

					subQry += " AND dr.mobile like '%" + key.trim() + "%'";
				}
				if (!selectedStoreItem[1].equals(0)) {
					subQry += " AND dr.store_number = '"
							+ (String) ((Listitem) selectedStoreItem[0])
									.getValue() + "'";
				}

				
				
				if(smsStatusTobeFetched.contains("_fetch_clicks_also_") && !smsStatusTobeFetched.contains("_fetch_opens_also_") && ! smsStatusTobeFetched.contains("special_condtion_for_all")){
					subQry += " AND dr.clicks is not null AND dr.clicks !=0  ";
					
				}
				else if(smsStatusTobeFetched.contains("_fetch_opens_also_") && !smsStatusTobeFetched.contains("_fetch_clicks_also_") && ! smsStatusTobeFetched.contains("special_condtion_for_all")){
					subQry += " AND dr.opens is not null AND dr.opens !=0  ";
				}
				else if(smsStatusTobeFetched.contains("_fetch_opens_also_") && smsStatusTobeFetched.contains("_fetch_clicks_also_") && ! smsStatusTobeFetched.contains("special_condtion_for_all")){
					subQry += " AND ((dr.opens is not null AND dr.opens !=0)   AND (dr.clicks is not null AND dr.clicks !=0))   ";
				}
				
				
				if(! smsStatusTobeFetched.contains("special_condtion_for_all") ){
					subQry += "  AND dr.status IN("+smsStatusTobeFetched+")  ";
				}
				/*if(((emailSatusTobeFetched.contains("dropped") || emailSatusTobeFetched.contains("bounce")) && ! emailSatusTobeFetched.contains("special_condtion_for_all"))){ 
					
					if( !emailSatusTobeFetched.contains("Success") && !emailSatusTobeFetched.contains("spamreport")){
						subQry += "  AND (dr.status='bounce' OR dr.status='dropped')  ";
					}else {
						subQry += "  AND (dr.status='bounce' OR dr.status='dropped')   AND  (dr.status='spamreport' OR dr.status='Success')   ";
					}
					
				}*/
				
			//	subQry = subQry + " AND dr.status NOT IN('"+Constants.DR_STATUS_SUBMITTED+"')";
				
				query = subQry + " ORDER BY sent_date desc";

			} else if (exportType.equalsIgnoreCase("Store Export")) {
				
				query = getStoreReportsQuery();

				/*query = "SELECT dr.id,dr.SBS_no,"
						+ fields
						+ " FROM dr_sent dr WHERE store_number IS NOT NULL AND user_id = "
						+ currentUser.getUserId() + " AND sent_date >= '"
						+ fromDate + "' AND sent_date <= '" + endDate + "' ";
				if (!selectedStoreItem[1].equals(0))
					query += " AND store_number = '"
							+ (String) ((Listitem) selectedStoreItem[0])
									.getValue() + "'";
				
				
				if(! emailSatusTobeFetched.contains("special_condtion_for_all") ){
					query += "  AND dr.status IN("+emailSatusTobeFetched+")  ";
				}
				
				
				query += "GROUP BY store_number,dr.SBS_no";*/
			}else if (exportType.equalsIgnoreCase("Status Pending")) {
				query = "SELECT dr.id,"
						+ fields
						+ " FROM dr_sent dr WHERE user_id = "
						+ currentUser.getUserId() + " AND sent_date >= '"
						+ fromDate + "' AND sent_date <= '" + endDate + "' ";
				if (!selectedStoreItem[1].equals(0))
					query += " AND store_number = '"
							+ (String) ((Listitem) selectedStoreItem[0])
									.getValue() + "'";
				
				
					query += "  AND dr.status IN('Submitted')  ";
				
			} else if (exportType.equalsIgnoreCase("SMS Status Pending")) {
				query = "SELECT dr.id,"
						+ fields
						+ " FROM dr_sms_sent dr WHERE user_id = "
						+ currentUser.getUserId() + " AND sent_date >= '"
						+ fromDate + "' AND sent_date <= '" + endDate + "' ";
				if (!selectedStoreItem[1].equals(0))
					query += " AND store_number = '"
							+ (String) ((Listitem) selectedStoreItem[0])
									.getValue() + "'";
				
				
					query += "  AND dr.status IN('Submitted')  ";
				
			}else if(exportType.equalsIgnoreCase("SMS Metrics")){
				String key = recipientsSmsSearchBoxId.getValue();
				String subQry = "SELECT MAX(dr.sent_date) as lastSent," + fields
						+ " FROM dr_sms_sent dr where   dr.user_id= "
						+ currentUser.getUserId();

				if (key != null) {

					subQry += " AND dr.mobile like '%" + key + "%'";
				}

				query = subQry + " group by dr.mobile  ORDER BY lastSent desc";
			}
			else {

				String key = recipientsSearchBoxId.getValue();
				String subQry = "SELECT MAX(dr.sent_date) as lastSent," + fields
						+ " FROM dr_sent dr where   dr.user_id= "
						+ currentUser.getUserId();

				if (key != null) {

					subQry += " AND dr.email_id like '%" + key + "%'";
				}

				query = subQry + " group by dr.email_id  ORDER BY lastSent desc";
			}
			
			logger.info("qrryyy "+query);
			JdbcResultsetHandler jh = null;
			try {

				jh = new JdbcResultsetHandler();

				jh.executeStmt(query, true);
				long count = jh.totalRecordsSize();
				if (count == 0) {
					MessageUtil.setMessage(
							"No records exist in the selected search.",
							"color:red", "TOP");
					return;
				}

				ResultSet resultSet = jh.getResultSet();
				if (exportType.equalsIgnoreCase("Delivery Report") || exportType.equalsIgnoreCase("SMS Delivery Report")) {
					while (resultSet.next()) {
						if(indexes[0]==0){
						Timestamp t = resultSet.getTimestamp("sent_date");
						if (t != null) {
							if (new Date(t.getTime()) != null) {
								Calendar cal = Calendar.getInstance();
								cal.setTime(t);

								resultSet
										.updateString(
												"sent_date",
												MyCalendar
														.calendarToString(
																cal,
																MyCalendar.FORMAT_DATETIME_STYEAR,
																clientTimeZone));
							}
						}}if(indexes[2]==0){
							String StoreName = resultSet
									.getString("SBS_no") == null ?( storeNameMap.containsKey(resultSet
											.getString("store_number")) ? storeNameMap.get(resultSet
													.getString("store_number"))
													: resultSet
													.getString("store_number") == null? "--":"Store ID "
															+ resultSet
															.getString("store_number")) : 
										(storeSBSNameMap.containsKey(resultSet
												.getString("store_number")+Constants.ADDR_COL_DELIMETER+resultSet
												.getString("SBS_no") ) ? 
												storeSBSNameMap.get(resultSet
														.getString("store_number")+Constants.ADDR_COL_DELIMETER+resultSet
														.getString("SBS_no")):  "Store ID " + resultSet
												.getString("store_number") );
											resultSet.updateString(
													"store_number", StoreName);
							/*resultSet.updateString(
										"store_number",
										storeNameMap.containsKey(resultSet
												.getString("store_number")) ? storeNameMap.get(resultSet
												.getString("store_number"))
												: resultSet
												.getString("store_number") == null? "--":"Store ID "
														+ resultSet
															.getString("store_number"));*/
						}
						
						if(indexes[3]==0){// for status in 2.4.8  
							
							
							if(resultSet.getString("status").equalsIgnoreCase("dropped") || (resultSet.getString("status").equalsIgnoreCase("bounce") )){
								resultSet.updateString("status","Bounced");
							
							}else if(resultSet.getString("status").equalsIgnoreCase("Submitted")){
								 
									resultSet.updateString("status","Status Pending");
								}
								else {
									resultSet.updateString("status",resultSet.getString("status"));
								}
							
							
							
							
						}
						resultSet.updateRow();
					}
					resultSet.beforeFirst();
				} else if (exportType.equalsIgnoreCase("Store Export")) {
					while (resultSet.next()) {
						sb = new StringBuffer();
						String store1 = resultSet
								.getString("SBS_no") == null ?( storeNameMap.containsKey(resultSet
										.getString("store_number")) ? storeNameMap.get(resultSet
												.getString("store_number"))
												: resultSet
												.getString("store_number") == null? "--":"Store ID "
														+ resultSet
														.getString("store_number")) : 
									(storeSBSNameMap.containsKey(resultSet
											.getString("store_number")+Constants.ADDR_COL_DELIMETER+resultSet
											.getString("SBS_no") ) ? 
											storeSBSNameMap.get(resultSet
													.getString("store_number")+Constants.ADDR_COL_DELIMETER+resultSet
													.getString("SBS_no")):  "Store ID " + resultSet
											.getString("store_number") );
										
						//String store1 = storeNameMap.get(resultSet.getString("store_number")) !=null ? storeNameMap.get(resultSet.getString("store_number")) : "Store ID "	+ resultSet	.getString("store_number");
				try {	
						if(indexes[0]==0)sb.append("\""+store1+"\"");
						if(indexes[1]==0){if(sb.length()!=0)sb.append(",");sb.append("\""+resultSet.getString("emailCount")+"\"");}
						if(indexes[2]==0){if(sb.length()!=0)sb.append(",");sb.append("\""+resultSet.getString("smsCount")+"\"");}
				}catch(Exception e) {}
						sb.append("\r\n");
						bw.write(sb.toString());
					}
					bw.flush();
					Filedownload.save(file, "text/csv");
					logger.debug("exited");
					throw new IllegalArgumentException();
				}else if (exportType.equalsIgnoreCase("Status Pending")) {
					while (resultSet.next()) {
						sb = new StringBuffer();
						String email = resultSet.getString("email_id") !=null ? resultSet.getString("email_id") : "Email ID "	+ resultSet	.getString("email_id");
						if(indexes[0]==0)sb.append("\""+email+"\"");
						//if(indexes[1]==0){if(sb.length()!=0)sb.append(",");sb.append("\""+resultSet.getString("sent_date")+"\"");}
						if(indexes[1]==0){
							Timestamp t = resultSet.getTimestamp("sent_date");
							if (t != null) {
								if (new Date(t.getTime()) != null) {
									Calendar cal = Calendar.getInstance();
									cal.setTime(t);
									if(sb.length()!=0)sb.append(",");sb.append("\"");sb.append(MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STYEAR, clientTimeZone));sb.append("\",");
								}
							}
						}
						sb.append("\r\n");
						bw.write(sb.toString());
					}
					bw.flush();
					Filedownload.save(file, "text/csv");
					logger.debug("exited");
					throw new IllegalArgumentException();
				}
				else if (exportType.equalsIgnoreCase("SMS Status Pending")) {
					while (resultSet.next()) {
						sb = new StringBuffer();
						String mobile = resultSet.getString("mobile") !=null ? resultSet.getString("mobile") : "Phone"	+ resultSet	.getString("mobile");
						if(indexes[0]==0)sb.append("\""+mobile+"\"");
						if(indexes[1]==0){
							Timestamp t = resultSet.getTimestamp("sent_date");
							if (t != null) {
								if (new Date(t.getTime()) != null) {
									Calendar cal = Calendar.getInstance();
									cal.setTime(t);
									if(sb.length()!=0)sb.append(",");sb.append("\"");sb.append(MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STYEAR, clientTimeZone));sb.append("\",");
								}
							}
						}
						sb.append("\r\n");
						bw.write(sb.toString());
					}
					bw.flush();
					Filedownload.save(file, "text/csv");
					logger.debug("exited");
					throw new IllegalArgumentException();
				}
				OCCSVWriter csvWriter = null;
				try {
					if(exportType.equalsIgnoreCase("Delivery Report") || exportType.equalsIgnoreCase("SMS Delivery Report")){
						csvWriter = new OCCSVWriter(bw);
						int[] indexesToIgnore = new int[2];
						indexesToIgnore[0]=1;
						indexesToIgnore[1]=2;
						csvWriter.writeAll(resultSet, false, indexesToIgnore);
					}else{
						
						csvWriter = new OCCSVWriter(bw);
						csvWriter.writeAll(resultSet, false, 1);
					}
				} catch (Exception e) {
					logger.info("Exception while writing into file ", e);
				} finally {
					csvWriter.flush();
					csvWriter.close();
				}

				Filedownload.save(file, "text/csv");
				logger.debug("exited");
			}catch(IllegalArgumentException ia){
			}
			catch (Exception e) {
				logger.error("** Exception : ", e);
			} finally {
				jh.rollback();
				jh.destroy();
				jh=null; bw=null; sb=null;
			}

		} catch (Exception e) {
			logger.error("** Exception : ", e);
		}
	}

	public void createWindow(String str) {

		try {

			Components.removeAllChildren(custExport$chkDivId);
			String headers[] = str.split("::;::");
			custExport$chkDivId.setAttribute("exportType", headers[0].trim());
			Checkbox tempCheck = null;
			for (int i = 2; i < headers.length; i++) {
				tempCheck = new Checkbox(headers[i].trim());
				tempCheck.setSclass("custCheck");
				tempCheck.setParent(custExport$chkDivId);
			}

		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}
	
	public void createSMSWindow(String str) {

		try {

			Components.removeAllChildren(custSmsExport$chkDivId);
			String headers[] = str.split("::;::");
			custSmsExport$chkDivId.setAttribute("exportType", headers[0].trim());
			Checkbox tempCheck = null;
			for (int i = 2; i < headers.length; i++) {
				tempCheck = new Checkbox(headers[i].trim());
				tempCheck.setSclass("custCheck");
				tempCheck.setParent(custSmsExport$chkDivId);
			}

		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}
	
	private String GetMergedPaymentPart(String paymentPart, String number,
			String type, String Amount) {
		String paymentPartStr = paymentPart
				.replace("#Payment.Amount#", "<strong>" + Amount + "</strong>")
				.replace("#Payment.Number#", number)
				.replace("#Payment.Type#", type);
		return paymentPartStr;
	}

	public void onCheck$uniqOpensChkId() {
		setValues();
		drawChart();
	}

	public void onCheck$bouncedChkId() {
		setValues();
		drawChart();
	}
	
	public void onCheck$statusPendingChkId() {
		setValues();
		drawChart();
	}

	public void onCheck$sentChkId() {
		setValues();
		drawChart();
	}

	public void onCheck$deleveredChkId() {
		setValues();
		drawChart();
	}

	public void onCheck$clicksChkId() {
		setValues();
		drawChart();
	}
private void setValues(){
	fromDateboxId.setValue(getStartCalendar());
	toDateboxId.setValue(getEndCalendar());
	drStoreLbId.setSelectedIndex((Integer) selectedStoreItem[1]);
	
}


/*public void onSelect$emailSmsControlsLbId(){
	logger.info("event listener called >>> ");
	boolean selectedAll = false;
	Set<Listitem> selectedItemsSet = new HashSet<Listitem>();
	Listitem itemAll = null; 
	
	
	for (Listitem item : emailSmsControlsLbId.getSelectedItems()) {
		if(item.getLabel().equalsIgnoreCase("All")){
			itemAll = item;
			selectedAll = true;
		}
		selectedItemsSet.add(item);
		
	}
	
	if(selectedAll == true){
		emailSmsControlsLbId.selectAll();
		
	}else{
		emailSmsControlsLbId.setSelectedItems(selectedItemsSet);
	}
	
	
	if(selectedItemsSet.size() == 4 && selectedAll == false){
		emailSmsControlsLbId.setSelectedItem(null);
		
	}
	
	//setRequiredStatusToBeFetched();
}*/

private void setRequiredStatusToBeFetched(){
	
	
	
	Set<String> requiredStatusSet = new HashSet<String>();
	if(emailSmsControlsLbId.getSelectedItems() != null && emailSmsControlsLbId.getSelectedItems().size() > 0 ){
		
		//normal
		sentChkId.setChecked(false);
		
		//delivery related
		deleveredChkId.setChecked(false);
		uniqOpensChkId.setChecked(false);
		clicksChkId.setChecked(false);
		
		//bounce kind of
		bouncedChkId.setChecked(false);
		
		//status pending
		statusPendingChkId.setChecked(false);
		
		
		for (Listitem item : emailSmsControlsLbId.getSelectedItems()) {
			if(item.getLabel().equalsIgnoreCase("All")){

				requiredStatusSet.add("Success");
				requiredStatusSet.add("dropped");
				requiredStatusSet.add("bounce");
				requiredStatusSet.add("spamreport");
				requiredStatusSet.add("submitted");
				
				requiredStatusSet.add("special_condtion_for_all");
				
				
				sentChkId.setChecked(true);
				deleveredChkId.setChecked(true);
				bouncedChkId.setChecked(true);
				statusPendingChkId.setChecked(true);
				uniqOpensChkId.setChecked(true);
				clicksChkId.setChecked(true);

			}else if(item.getLabel().equals("Delivered")){

				requiredStatusSet.add("Success");
				requiredStatusSet.add("spamreport");
				
				
				deleveredChkId.setChecked(true);
				uniqOpensChkId.setChecked(true);
				clicksChkId.setChecked(true);

			}else if(item.getLabel().equals("Bounced")){

				requiredStatusSet.add("dropped");
				requiredStatusSet.add("bounce");

				
				bouncedChkId.setChecked(true);
			}else if(item.getLabel().equals("Delivery Status Pending")){

				requiredStatusSet.add("submitted");

				
				statusPendingChkId.setChecked(true);
			}else if(item.getLabel().equals("Opens")){
				
				requiredStatusSet.add("Success");
				requiredStatusSet.add("spamreport");
				
				
				requiredStatusSet.add("_fetch_opens_also_");
				
				deleveredChkId.setChecked(true);
				uniqOpensChkId.setChecked(true);
				
			}else if(item.getLabel().equals("Clicks")){

				requiredStatusSet.add("Success");
				requiredStatusSet.add("spamreport");
				
				
				
				requiredStatusSet.add("_fetch_clicks_also_");
				
				deleveredChkId.setChecked(true);
				clicksChkId.setChecked(true);
			}

		}
		
		emailSatusTobeFetched = "";
		
		for(String aStatus : requiredStatusSet){
			if(emailSatusTobeFetched.trim().length() > 0) emailSatusTobeFetched += ",";
			emailSatusTobeFetched += "'"+aStatus+"'";
		}
		
		
		
		if(storeBandBoxId.getValue().equalsIgnoreCase("All")){
			
			sentChkId.setChecked(true);
			deleveredChkId.setChecked(true);
			bouncedChkId.setChecked(true);
			uniqOpensChkId.setChecked(true);
			clicksChkId.setChecked(true);
			statusPendingChkId.setChecked(true);

			
			
			emailSatusTobeFetched = "'Success','spamreport','dropped','bounce','special_condtion_for_all'";
		}
		
		
		
		
		
		logger.info(" status to be fetched >>> "+emailSatusTobeFetched);
		

	}else{
		
		MessageUtil.setMessage("Please select appropriate option(s) in drop-down", "color:red");
		return;
	}
	
	
}


private void populateEmailSmsControlsLbId(){
	
	
	
	
	
	
	Listitem deliveredChkIdListitem = new Listitem("Delivered");
	deliveredChkIdListitem.setParent(emailSmsControlsLbId);
	
	Listitem nonDeliveryChkIdListitem = new Listitem("Bounced");
	nonDeliveryChkIdListitem.setParent(emailSmsControlsLbId);
	
	Listitem uniqueStatusPendingChkIdListitem = new Listitem("Delivery Status Pending");
	uniqueStatusPendingChkIdListitem.setParent(emailSmsControlsLbId);
	
	Listitem uniqueOpensChkIdListitem = new Listitem("Opens");
	uniqueOpensChkIdListitem.setParent(emailSmsControlsLbId);
	
	Listitem uniqueClicksChkIdListitem = new Listitem("Clicks");
	uniqueClicksChkIdListitem.setParent(emailSmsControlsLbId);
	
	
	
	emailSmsControlsLbId.selectAll();
	
}

private void populateEmailSmsControlsLbIdAndSetDefaults() {
	
	/*Components.removeAllChildren(emailSmsControlsLbId);
	
	Listitem listitemAll = new Listitem("All");
	listitemAll.setParent(emailSmsControlsLbId);
	
	
	
	Listitem deliveredChkIdListitem = new Listitem("Delivery");
	deliveredChkIdListitem.setParent(emailSmsControlsLbId);
	
	Listitem nonDeliveryChkIdListitem = new Listitem("Undelivered");
	nonDeliveryChkIdListitem.setParent(emailSmsControlsLbId);
	
	
	
	Listitem uniqueOpensChkIdListitem = new Listitem("Opens");
	uniqueOpensChkIdListitem.setParent(emailSmsControlsLbId);
	
	Listitem uniqueClicksChkIdListitem = new Listitem("Clicks");
	uniqueClicksChkIdListitem.setParent(emailSmsControlsLbId);
   
	
	//default setting
	emailSmsControlsLbId.setSelectedItem(deliveredChkIdListitem);*/
	
	
	//normal
	sentChkId.setChecked(true);

	//delivery related
	deleveredChkId.setChecked(true);
	uniqOpensChkId.setChecked(true);
	clicksChkId.setChecked(true);

	//Undelivered kind of
	bouncedChkId.setChecked(true);
	
	//status pending
	statusPendingChkId.setChecked(true);
	
	
	/*isemailChecked  = true;
	emailChkId.setChecked(true);
	
	isSmsChecked  = true;
	smsChkId.setChecked(true);*/
	
	
	
	/*uniqOpensChkId.setChecked(false);
	clicksChkId.setChecked(false);
	bouncedChkId.setChecked(false);
	deleveredChkId.setChecked(true);
	sentChkId.setChecked(false);
	sentSmsChkId.setChecked(false);
	receivedSmsChkId.setChecked(true);
	undeliveredSmsChkId.setChecked(false);*/
	
	
	
	}

	public String getStoreReportsQuery() {
		String query = "SELECT subTable.store_number, SUM(emailCount) AS emailCount, SUM(smsCount) AS smsCount, subTable.SBS_no  FROM ("
				+ "SELECT temp.store_number, temp.SBS_no, CASE WHEN temp.drType='EMAIL' THEN temp.Count ELSE 0 END AS emailCount, CASE WHEN temp.drType='SMS' THEN temp.Count ELSE 0 END AS smsCount FROM "
				+ "(SELECT dr.store_number, dr.SBS_no, COUNT(dr.id) AS Count, 'EMAIL' AS drType FROM dr_sent AS dr WHERE store_number IS NOT NULL AND user_id="
				+ currentUser.getUserId() + "   AND sent_date >= '" + fromDate + "'" + " AND sent_date <= '" + endDate
				+ "'";
		if (!selectedStoreItem[1].equals(0))
			query += " AND store_number = '" + (String) ((Listitem) selectedStoreItem[0]).getValue() + "'";

		if (!emailSatusTobeFetched.contains("special_condtion_for_all")) {
			query += "  AND dr.status IN(" + emailSatusTobeFetched + ")  ";
		}

		query += " GROUP BY dr.store_number, dr.SBS_no" + " UNION ALL "
				+ "SELECT dss.store_number, dss.SBS_no, COUNT(dss.id) AS Count, 'SMS' AS drType FROM dr_sms_sent AS dss WHERE store_number IS NOT NULL AND user_id="
				+ currentUser.getUserId() + " AND sent_date >= '" + fromDate + "'" + " AND sent_date <= '" + endDate
				+ "'";

		if (!selectedStoreItem[1].equals(0))
			query += " AND store_number = '" + (String) ((Listitem) selectedStoreItem[0]).getValue() + "'";

		if (!smsStatusTobeFetched.contains("special_condtion_for_all")) {
			query += " AND dr.status IN(" + smsStatusTobeFetched + ")  ";
		}
		query += "  GROUP BY dss.store_number , dss.SBS_no) AS temp) AS subTable GROUP BY subTable.store_number, subTable.SBS_no";

		return query;
	}


}
