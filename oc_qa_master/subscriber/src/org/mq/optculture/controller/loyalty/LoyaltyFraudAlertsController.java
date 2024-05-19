package org.mq.optculture.controller.loyalty;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.mq.marketer.campaign.beans.LoyaltyFraudAlert;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.LoyaltyFraudAlertDao;
import org.mq.optculture.data.dao.LoyaltyFraudAlertDaoForDML;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

public class LoyaltyFraudAlertsController extends GenericForwardComposer {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Users users;
	private Checkbox ltyfraudrtAlertsChkId, ltyfraudAlertsDailyChkId, ltyfraudAlertsWeeklyChkId;
	private Div dateDivId, listboxDivId, checkDivId, ltyfraudDivId, dailyDivId,weeklyDivId, alertMailDivId,displayRuleDivId,displayEmailFrqDivId;
	private Listbox ltyTypeLbId, trxcriteriaLbId, trxTdateypeLbId, trxDWMLbId, hoursLbId, daysLbId,
			fraudAlertPageSizeLBId, exportFraudAlertTrxLbId,trxAmtPtLbId;
	private Textbox trxTbId, withinTbId, emailAddTxtBxId,rulenametxboxId;
	private MyDatebox fromDateId, toDateId;
	private String fromDateStr, toDateStr;
	private Button saveAllDetailsBtnId;
	private Rows fraudAlertTrxRowId,fraudTrxDetailsSubWinId$viewAllFraudTrx,alertReportTrxRowId;
	private Listbox fraudTrxDetailsSubWinId$viewAllFraudTrxListId;
	private Paging fraudAlertTrxPagingId;
	private Window custExport,fraudTrxDetailsSubWinId;
	private Div custExport$chkDivId,fraudTrxDetailsSubWinId$viewFraudTrxDetailsDivId,emailSavebtnDivID;
	private TimeZone clientTimeZone;
	private LoyaltyFraudAlertDao fraudAlertDao;
	private LoyaltyFraudAlertDaoForDML fraudAlertDaoForDML;
	private final String columnType="columnType";
	private Calendar startDate, endDate;
	private Calendar createDate;
	private Tabbox fraudAlertsTabBoxId;
	private Radiogroup  ltyfrdaletSendMailRGId;
	private Radio dailyRadioId,weeklyRadioId;
	private final String ATTR_DAILY = new String("daily");
	private final String ATTR_WEEKLY = new String("weekly");
	 private static Map<Integer,String> weekMap=new HashMap<>();
	public LoyaltyFraudAlertsController() {
		session = Sessions.getCurrent();
		String style = "font-weight:bold;font-size:15px;color:#313031;"
				+ "font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Fraud Alert ", "", style, true);
		users = GetUser.getUserObj();
		this.fraudAlertDao = (LoyaltyFraudAlertDao) SpringUtil.getBean("loyaltyFraudAlertDao");
		this.fraudAlertDaoForDML = (LoyaltyFraudAlertDaoForDML) SpringUtil.getBean("loyaltyFraudAlertDaoForDML");
	}

	private final String ATTRIBUTE_FRAUDALERT_OBJ = "ATTRIBUTE_FRAUDALERT_OBJ";
	private final String FRAUD_ALERT_CARDNO="Fraud_Alert_CardNo";
   static{
	   weekMap.put(0,"Sunday");
		  weekMap.put(1, "Monday");
		  weekMap.put(2,"Tuesday");
		  weekMap.put(3, "Wednesday");
		  weekMap.put(4,"Thursday");
		  weekMap.put(5, "Friday");
		  weekMap.put(6, "Saturday");  
   }
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		listboxDivId.setVisible(true);
	
			clientTimeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
		onSelect$fraudAlertsTabBoxId();
		
	}
	public void onSelect$trxTdateypeLbId() {
		if (trxTdateypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("period")) {
			dateDivId.setVisible(true);
			listboxDivId.setVisible(false);
		} else {
			listboxDivId.setVisible(true);
			dateDivId.setVisible(false);
		}

	}
	/*public void onCheck$ltyfrdaletSendMailRGId(){
		if(ltyfrdaletSendMailRGId.getSelectedIndex()==0){
			dailyDivId.setVisible(true);
		}
		else{
			weeklyDivId.setVisible(true);
		}
	}*/
	public void onCheck$ltyfraudrtAlertsChkId() {
		checkDivId.setVisible(ltyfraudrtAlertsChkId.isChecked());
		emailSavebtnDivID.setVisible(ltyfraudrtAlertsChkId.isChecked());
		ltyfraudDivId.setVisible(ltyfraudrtAlertsChkId.isChecked() &&(ltyfraudAlertsDailyChkId.isChecked()||ltyfraudAlertsWeeklyChkId.isChecked()));
	}

	public void onCheck$ltyfraudAlertsDailyChkId() {
		ltyfraudDivId.setVisible(ltyfraudAlertsDailyChkId.isChecked() || ltyfraudAlertsWeeklyChkId.isChecked());
		dailyDivId.setVisible(ltyfraudAlertsDailyChkId.isChecked() || ltyfraudAlertsWeeklyChkId.isChecked());
		weeklyDivId.setVisible(ltyfraudAlertsWeeklyChkId.isChecked());
	}

	public void onCheck$ltyfraudAlertsWeeklyChkId() {
		ltyfraudDivId.setVisible(ltyfraudAlertsDailyChkId.isChecked() || ltyfraudAlertsWeeklyChkId.isChecked());
		dailyDivId.setVisible(ltyfraudAlertsDailyChkId.isChecked() || ltyfraudAlertsWeeklyChkId.isChecked());
		weeklyDivId.setVisible(ltyfraudAlertsWeeklyChkId.isChecked());
	}
	
	public void onSelect$ltyTypeLbId(){
		if(ltyTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Redemption")&&trxAmtPtLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("point")){
			MessageUtil.setMessage("For Redemption value should be amount.", "color:blue", "TOP");	
			trxAmtPtLbId.setSelectedIndex(1);
		return;
		}
	}
	public void onSelect$trxAmtPtLbId(){
		if(ltyTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Redemption")&&trxAmtPtLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("point")){
			MessageUtil.setMessage("For Redemption value should be amount.", "color:blue", "TOP");	
			trxAmtPtLbId.setSelectedIndex(1);
		return;
		}
	}
	public void onSelect$fraudAlertsTabBoxId(){	
		
		displayRuleDivId.setVisible(true);
		displayEmailFrqDivId.setVisible(true);
		rulenametxboxId.setDisabled(false);
		rulenametxboxId.setText("");
		trxAmtPtLbId.setSelectedIndex(0);
		ltyTypeLbId.setSelectedIndex(0);
		trxcriteriaLbId.setSelectedIndex(0);
		trxTdateypeLbId.setSelectedIndex(0);
		withinTbId.setText("");
		trxTbId.setText("");
		hoursLbId.setSelectedIndex(0);
		daysLbId.setSelectedIndex(0);
		exportFraudAlertTrxLbId.setSelectedIndex(0);
		fraudAlertPageSizeLBId.setSelectedIndex(0);
		emailAddTxtBxId.setText("");
		fromDateId.setText("");
		toDateId.setText("");
		ltyfraudrtAlertsChkId.setChecked(false);
		ltyfraudAlertsDailyChkId.setChecked(false);
		ltyfraudAlertsWeeklyChkId.setChecked(false);
		saveAllDetailsBtnId.setLabel("Create");
		Components.removeAllChildren(fraudAlertTrxRowId);
		fraudAlertTrxPagingId.setTotalSize(0);
		onCheck$ltyfraudrtAlertsChkId();
		onSelect$trxTdateypeLbId();
		Components.removeAllChildren(alertMailDivId);
		saveAllDetailsBtnId.removeAttribute(ATTRIBUTE_FRAUDALERT_OBJ);
		if(fraudAlertsTabBoxId.getSelectedIndex()==0){
			displayAllCurrentAlert();
		}
	}
	public void onClick$saveAllDetailsBtnId(){
		
		try {
		//	String type=trxLbId.getSelectedItem().getValue().toString();
			String ruleName=rulenametxboxId.getText().trim();
			logger.info("Rule Name "+ruleName);
			if(ruleName.isEmpty() || ruleName==null){
				MessageUtil.setMessage("Rule name must be required.", "color:red", "TOP");	
				return ;
			}
			if(saveAllDetailsBtnId.getLabel().equalsIgnoreCase("Create")){
			LoyaltyFraudAlert lty=fraudAlertDao.getFraudAlertRuleByName(users.getUserId(), ruleName);
			if(lty!=null){
				MessageUtil.setMessage("Rule already exist.", "color:red", "TOP");	
				return ;
			}
			}
			String trxRule = "";
			String dateRule = "";
			LoyaltyFraudAlert fraudAlert = (LoyaltyFraudAlert) saveAllDetailsBtnId
					.getAttribute(ATTRIBUTE_FRAUDALERT_OBJ);

			if (fraudAlert == null) {
				fraudAlert = new LoyaltyFraudAlert();
			}
			StringBuffer trxsb = new StringBuffer("");
			/*trxsb.append(trxLbId.getSelectedItem().getValue().toString());
			trxsb.append(Constants.DELIMITER_PIPE);
		*/	trxsb.append(trxAmtPtLbId.getSelectedItem().getValue().toString());
			trxsb.append(Constants.DELIMITER_PIPE);
			trxsb.append(ltyTypeLbId.getSelectedItem().getValue().toString());
			trxsb.append(Constants.DELIMITER_PIPE);
			trxsb.append(trxcriteriaLbId.getSelectedItem().getValue().toString());
			trxsb.append(Constants.DELIMITER_PIPE);
			if (vaildateText(trxTbId.getText().trim()))
				trxsb.append(trxTbId.getText().trim());
			else
				return;
			trxRule = trxsb.toString();

			StringBuffer datesb = new StringBuffer("");
			if (trxTdateypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("Withinlast")) {
				datesb.append(trxTdateypeLbId.getSelectedItem().getValue().toString());
				datesb.append(Constants.DELIMITER_PIPE);
				datesb.append(trxDWMLbId.getSelectedItem().getValue().toString());
				datesb.append(Constants.DELIMITER_PIPE);
				if (vaildateText(withinTbId.getText().trim()))
					datesb.append(withinTbId.getText().trim());
				else
					return;

			}

			else {
				if (!isValidate()) {
					return;
				}
				fromDateStr = MyCalendar.calendarToString(getStartDate(fromDateId), MyCalendar.FORMAT_YEARTODATE,clientTimeZone);		
				toDateStr = MyCalendar.calendarToString(getEndDate(toDateId), MyCalendar.FORMAT_YEARTODATE,clientTimeZone);
				
			/*	fromDateStr = MyCalendar.calendarToString(startDate, MyCalendar.FORMAT_DATETIME_STYEAR,clientTimeZone);		
				toDateStr = MyCalendar.calendarToString(endDate, MyCalendar.FORMAT_DATETIME_STYEAR,clientTimeZone);
		*/
				datesb.append(trxTdateypeLbId.getSelectedItem().getValue().toString());
				datesb.append(Constants.DELIMITER_PIPE + "Range");
				datesb.append(Constants.DELIMITER_PIPE);
				datesb.append(fromDateStr);
				datesb.append(Constants.DELIMITER_PIPE);
				datesb.append(toDateStr);
			}
			dateRule = datesb.toString();
			fraudAlert.setRuleName(ruleName);
			fraudAlert.setCreatedByUserId(users.getUserId());
			fraudAlert.setModifiedByUserId(users.getUserId());
			if(createDate==null)
				createDate=Calendar.getInstance(clientTimeZone);
			fraudAlert.setCreatedDate(createDate);
			fraudAlert.setModifiedDate(Calendar.getInstance(clientTimeZone));
			fraudAlert.setTrxRule(trxRule);
			fraudAlert.setDateRule(dateRule);
			fraudAlertDaoForDML.saveOrUpdate(fraudAlert);
			MessageUtil.setMessage("Create rule saved  successfully.", "color:blue", "TOP");
			saveAllDetailsBtnId
					.setAttribute(ATTRIBUTE_FRAUDALERT_OBJ,fraudAlert);
			int totalSize = fraudAlertDao.getCountFraudAlertTrx(users.getUserId(),  getHavingCriteria(trxRule), getDateCriteria(dateRule));
			int pageSize = Integer.parseInt(fraudAlertPageSizeLBId.getSelectedItem().getLabel());
			fraudAlertTrxPagingId.setTotalSize(totalSize);
			fraudAlertTrxPagingId.setPageSize(pageSize);
			fraudAlertTrxPagingId.setActivePage(0);
			fraudAlertTrxPagingId.addEventListener("onPaging", this);
	         logger.info("total size "+totalSize);
			setFradAlertTrx(users.getUserId(),getHavingCriteria(trxRule), getDateCriteria(dateRule), 0, fraudAlertTrxPagingId.getPageSize());
			rulenametxboxId.setDisabled(true);
		} catch (Exception e) {
			logger.error("Exception :", e);
		}
	}
	 public String getDateCriteria(String dateRule){
		 String dateCondition=null;
			SimpleDateFormat format1 = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
		 if(dateRule!=null){
			 Long timezone=Long.parseLong(users.getClientTimeZone());
		    	String fromdt=null;
		    	String todt=null;
		    	String dt[]=dateRule.split("\\|");
		    	int day=0;
		    	if(dt[0].trim().equalsIgnoreCase("withinlast")){
		    	day=(dt[1].trim().equalsIgnoreCase("day"))?Integer.parseInt(dt[2]):(dt[1].trim().equalsIgnoreCase("week"))?(Integer.parseInt(dt[2])*7):Integer.parseInt(dt[2])*30;
		    	endDate = new MyCalendar();
				endDate.set(MyCalendar.HOUR_OF_DAY, 23);
				endDate.set(MyCalendar.MINUTE, 59);
				endDate.set(MyCalendar.SECOND, 59);

				startDate = new MyCalendar();
				startDate.set(MyCalendar.HOUR_OF_DAY, 00);
				startDate.set(MyCalendar.MINUTE, 00);
				startDate.set(MyCalendar.SECOND, 00);

		    	startDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - day);
				endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - 1);
				todt = "'"+format1.format(endDate.getTime())+"'";
				 fromdt = "'"+format1.format(startDate.getTime())+"'";
				 logger.info("client Time zone "+users.getClientTimeZone());
				 dateCondition=" AND  created_date between "+fromdt+" AND "+todt;
		    	}
		    	else{
		    		fromdt="'"+dt[2]+"'";
		    	    todt="'"+dt[3]+"'";
		    	    dateCondition=" AND  date(date_add(created_date, interval  "+timezone+"  minute)) between "+fromdt+" AND "+todt;
		    		
		    	}
		    	
		 } 
		 return dateCondition;
	 }
	 public String getHavingCriteria(String trxRule){
		 String havingCondition=null;
		 if(trxRule!=null){
		    	String trx[]=trxRule.split("\\|"); 
		    	String sign=(trx[2].trim().equalsIgnoreCase("morethan")) ? ">" :"=";
		    	
		    	if(trx[0].trim().equalsIgnoreCase("trx")){
		    	if(trx[1].trim().equalsIgnoreCase("Issuance"))	
		    		havingCondition=" having issuance_count "+sign+" "+trx[3];
		    	else if(trx[1].trim().equalsIgnoreCase("Redemption"))
		    		havingCondition=" having redemption_count "+sign+" "+trx[3];
		    	else
		    		havingCondition=" having (issuance_count "+sign+" "+trx[3]+" OR redemption_count"+sign+" "+trx[3]+")";
		    	}
		    	else{
		    		 if(trx[0].trim().equalsIgnoreCase("amount")){
		    		    if(trx[1].trim().equalsIgnoreCase("Issuance"))	
			    		  havingCondition=" having issuance_amt "+sign+" "+trx[3];
			    	   else if(trx[1].trim().equalsIgnoreCase("Redemption"))
			    		   havingCondition=" having redemption_amt "+sign+" "+trx[3];
			    	   else
			    		   havingCondition=" having (issuance_amt "+sign+" "+trx[3]+" OR redemption_amt "+sign+" "+trx[3]+")";
		    		 }
		    		 else{
		    			 if(trx[1].trim().equalsIgnoreCase("Issuance"))	
				    		  havingCondition=" having issuance_point "+sign+" "+trx[3];
		    			 else
		    				 havingCondition=" having (issuance_point "+sign+" "+trx[3]+" OR redemption_amt "+sign+" "+trx[3]+")";
		    		 }
		    	}
		    } 
		 return havingCondition;
	}
	public void onClick$saveSendEmailDetailsBtnId(){
		LoyaltyFraudAlert fraudAlert = (LoyaltyFraudAlert) saveAllDetailsBtnId
				.getAttribute(ATTRIBUTE_FRAUDALERT_OBJ);
		if(fraudAlert==null){
			MessageUtil.setMessage("First create rule.", "color:red", "TOP");	
			return;
		}
		if (ltyfraudrtAlertsChkId.isChecked()
				&& (ltyfraudAlertsDailyChkId.isChecked() || ltyfraudAlertsWeeklyChkId.isChecked())) {
			long numberVal = 0;
			if (ltyfraudAlertsDailyChkId.isChecked() && !ltyfraudAlertsWeeklyChkId.isChecked()) {
				fraudAlert.setFrequency(OCConstants.LTY_SETTING_REPORT_FRQ_DAY);
				numberVal = Long.parseLong((String) hoursLbId.getSelectedItem().getValue());
				fraudAlert.setTriggerAt(numberVal + Constants.STRING_NILL);

			} if(ltyfraudAlertsWeeklyChkId.isChecked() &&!ltyfraudAlertsDailyChkId.isChecked()){
				fraudAlert.setFrequency(OCConstants.LTY_SETTING_REPORT_FRQ_WEEK);
				numberVal = Long.parseLong((String) hoursLbId.getSelectedItem().getValue());
				String str = daysLbId.getSelectedItem().getValue();
				fraudAlert.setTriggerAt(numberVal + Constants.ADDR_COL_DELIMETER + str);
			}
			if(ltyfraudAlertsDailyChkId.isChecked() && ltyfraudAlertsWeeklyChkId.isChecked()){
				
				fraudAlert.setFrequency(OCConstants.LTY_SETTING_REPORT_FRQ_DAY+Constants.ADDR_COL_DELIMETER+OCConstants.LTY_SETTING_REPORT_FRQ_WEEK);
				numberVal = Long.parseLong((String) hoursLbId.getSelectedItem().getValue());
				String str = daysLbId.getSelectedItem().getValue();
				fraudAlert.setTriggerAt(numberVal + Constants.ADDR_COL_DELIMETER + str);

			}
			fraudAlert.setEnabled(ltyfraudrtAlertsChkId.isChecked()&& (ltyfraudAlertsDailyChkId.isChecked() || ltyfraudAlertsWeeklyChkId.isChecked()));
			String emailIdStr = prepareEmailIdStr();
			if(emailIdStr==null)
				return;
			fraudAlert.setEmailId(emailIdStr);
		}
		else if(ltyfraudrtAlertsChkId.isChecked()){
			fraudAlert.setEnabled(ltyfraudAlertsDailyChkId.isChecked() || ltyfraudAlertsWeeklyChkId.isChecked());
		}
		fraudAlertDaoForDML.saveOrUpdate(fraudAlert);
		MessageUtil.setMessage("Send email alert  saved  successfully.", "color:blue", "TOP");
		fraudAlertsTabBoxId.setSelectedIndex(0);
		onSelect$fraudAlertsTabBoxId();
	}
	public Calendar getStartDate(MyDatebox fromDateboxId) {
			if (fromDateId.getValue() != null
					&& !fromDateId.getValue().toString().isEmpty()) {
			Calendar serverFromDateCal = fromDateId.getServerValue();
			Calendar tempClientFromCal = fromDateId.getClientValue();
			serverFromDateCal.set(Calendar.HOUR_OF_DAY,
					serverFromDateCal.get(Calendar.HOUR_OF_DAY) - tempClientFromCal.get(Calendar.HOUR_OF_DAY));
			serverFromDateCal.set(Calendar.MINUTE,
					serverFromDateCal.get(Calendar.MINUTE) - tempClientFromCal.get(Calendar.MINUTE));
			serverFromDateCal.set(Calendar.SECOND, 0);
			return serverFromDateCal;
		} 
		
			else {
			MessageUtil.setMessage("From date cannot be empty.", "color:red",
					"TOP");
			return null;
		}

	}

	private boolean vaildateText(String text) {
		if (text == "" || text.length() == 0) {
			MessageUtil.setMessage("Please provide value in textbox.", "color:red", "TOP");
			return false;
		}
		if (text.length() != 0)
			try {
				Long.parseLong(text);
			} catch (NumberFormatException ex) {
				MessageUtil.setMessage("Please provide numeric value in textbox.", "color:red", "TOP");
				return false;
			}

		return true;

	}

	public Calendar getEndDate(MyDatebox toDateboxId) {

		if (toDateId.getValue() != null
				&& !toDateId.getValue().toString().isEmpty()) {
		Calendar serverToDateCal = toDateId.getServerValue();

		Calendar tempClientToCal = toDateId.getClientValue();

		// change the time for startDate and endDate in order to consider right
		// from the
		// starting time of startDate to ending time of endDate

		serverToDateCal.set(Calendar.HOUR_OF_DAY,
				23 + serverToDateCal.get(Calendar.HOUR_OF_DAY) - tempClientToCal.get(Calendar.HOUR_OF_DAY));
		serverToDateCal.set(Calendar.MINUTE,
				59 + serverToDateCal.get(Calendar.MINUTE) - tempClientToCal.get(Calendar.MINUTE));
		serverToDateCal.set(Calendar.SECOND, 59);

		return serverToDateCal;
		}
		else {
			MessageUtil.setMessage("To date cannot be empty.", "color:red",
					"TOP");
			return null;
		}
	}

	private void doEnableEditSettings(LoyaltyFraudAlert ltyfrdalert ,String type) {
		if (ltyfrdalert != null) {
			createDate=ltyfrdalert.getCreatedDate();
		//	saveAllDetailsBtnId.setAttribute(ATTRIBUTE_FRAUDALERT_OBJ, ltyfrdalert);
			String trxRule = null;
			String dateRule = null;
			if (ltyfrdalert != null) {
				trxRule = ltyfrdalert.getTrxRule();
				dateRule = ltyfrdalert.getDateRule();
			}
			int totalSize = fraudAlertDao.getCountFraudAlertTrx(users.getUserId(), getHavingCriteria(trxRule), getDateCriteria(dateRule));
			int pageSize = Integer.parseInt(fraudAlertPageSizeLBId.getSelectedItem().getLabel());
			fraudAlertTrxPagingId.setTotalSize(totalSize);
			fraudAlertTrxPagingId.setPageSize(pageSize);
			fraudAlertTrxPagingId.setActivePage(0);
			fraudAlertTrxPagingId.addEventListener("onPaging", this);
	         logger.info("total size "+totalSize);
			setFradAlertTrx(users.getUserId(), getHavingCriteria(trxRule), getDateCriteria(dateRule), 0, fraudAlertTrxPagingId.getPageSize());
		}
		try {
			rulenametxboxId.setText(ltyfrdalert.getRuleName());
			int strTime = 0;
			int strDay = 0;
			String emailAddrs = null;
			if (ltyfrdalert != null) {
				String trxRule = "";
				String dateRule = "";
				trxRule = ltyfrdalert.getTrxRule();
				dateRule = ltyfrdalert.getDateRule();
				// setting the trx Rule
				String trx[] = trxRule.split("\\|");
				for (Listitem ltyItem : trxAmtPtLbId.getItems()) {
					if (ltyItem.getValue().equals(trx[0])) {
						ltyItem.setSelected(true);
						
						break;
					}

				}
				for (Listitem ltyItem : ltyTypeLbId.getItems()) {
					if (ltyItem.getValue().equals(trx[1])) {
						ltyItem.setSelected(true);
						break;
					}
				}
				for (Listitem ltyItem : trxcriteriaLbId.getItems()) {
					if (ltyItem.getValue().equals(trx[2])) {
						ltyItem.setSelected(true);
						break;
					}
				}
				trxTbId.setText(trx[3]);
				String date[] = dateRule.split("\\|");
				for (Listitem ltyItem : trxTdateypeLbId.getItems()) {
					if (ltyItem.getValue().equals(date[0])) {
						ltyItem.setSelected(true);
						break;
					}
				}
				if (trxTdateypeLbId.getSelectedIndex() == 0) {
					listboxDivId.setVisible(true);
					dateDivId.setVisible(false);
					for (Listitem ltyItem : trxDWMLbId.getItems()) {
						if (ltyItem.getValue().equals(date[1])) {
							ltyItem.setSelected(true);
							break;
						}
					}
					withinTbId.setText(date[2]);
				} else {
					dateDivId.setVisible(true);
					listboxDivId.setVisible(false);
					SimpleDateFormat sdf = new SimpleDateFormat(MyCalendar.FORMAT_YEARTODATE);
					Date fdt = sdf.parse(date[2]);
					Date tdt = sdf.parse(date[3]);
					Calendar cal = Calendar.getInstance(clientTimeZone);
					cal.setTime(fdt);
					logger.info("from cal "+cal.getTimeZone());
					fromDateId.setValue(cal);
				    cal = Calendar.getInstance(clientTimeZone);
					cal.setTime(tdt);
					toDateId.setValue(cal);
					logger.info("to cal "+cal.getTime());
				}
				if (ltyfrdalert.getFrequency()!=null &&ltyfrdalert.getFrequency().equals(OCConstants.LTY_SETTING_REPORT_FRQ_DAY)) {
					ltyfraudrtAlertsChkId.setChecked(ltyfrdalert.isEnabled());
					checkDivId.setVisible(ltyfraudrtAlertsChkId.isChecked());
					emailSavebtnDivID.setVisible(ltyfraudrtAlertsChkId.isChecked());
					ltyfraudAlertsDailyChkId.setAttribute(ATTR_DAILY, ltyfrdalert);
					if (ltyfrdalert.isEnabled()) {
						strTime = Integer.parseInt(ltyfrdalert.getTriggerAt());
						hoursLbId.setSelectedIndex(strTime - 1);
						if (emailAddrs == null)
							emailAddrs = ltyfrdalert.getEmailId();

					}
					ltyfraudAlertsDailyChkId.setChecked(ltyfrdalert.isEnabled());
					onCheck$ltyfraudAlertsDailyChkId();

				}  
				if (ltyfrdalert.getFrequency()!=null &&ltyfrdalert.getFrequency().equals(OCConstants.LTY_SETTING_REPORT_FRQ_WEEK)) {
					ltyfraudrtAlertsChkId.setChecked(ltyfrdalert.isEnabled());
					checkDivId.setVisible(ltyfraudrtAlertsChkId.isChecked());
					emailSavebtnDivID.setVisible(ltyfraudrtAlertsChkId.isChecked());
					ltyfraudAlertsWeeklyChkId.setAttribute(ATTR_WEEKLY, ltyfrdalert);

					if (ltyfrdalert.isEnabled()) {
						String strWeek = ltyfrdalert.getTriggerAt();
						String strWeekArr[] = strWeek.split(Constants.ADDR_COL_DELIMETER);
						strDay = Integer.parseInt(strWeekArr[1]);
						hoursLbId.setSelectedIndex(Integer.parseInt(strWeekArr[0]) - 1);
						daysLbId.setSelectedIndex(strDay);
						if (emailAddrs == null)
							emailAddrs = ltyfrdalert.getEmailId();
					}
					ltyfraudAlertsWeeklyChkId.setChecked(ltyfrdalert.isEnabled());
					onCheck$ltyfraudAlertsWeeklyChkId();
				}
                if(ltyfrdalert.getFrequency()!=null && ltyfrdalert.getFrequency().equals(OCConstants.LTY_SETTING_REPORT_FRQ_DAY+Constants.ADDR_COL_DELIMETER+OCConstants.LTY_SETTING_REPORT_FRQ_WEEK )){
                	logger.info("daily weekly "+ltyfrdalert.getFrequency());
                	ltyfraudrtAlertsChkId.setChecked(ltyfrdalert.isEnabled());
					checkDivId.setVisible(ltyfraudrtAlertsChkId.isChecked());
					emailSavebtnDivID.setVisible(ltyfraudrtAlertsChkId.isChecked());
					ltyfraudAlertsDailyChkId.setAttribute(ATTR_DAILY, ltyfrdalert);
					ltyfraudAlertsWeeklyChkId.setAttribute(ATTR_WEEKLY, ltyfrdalert);
					if (ltyfrdalert.isEnabled()){
						String strWeek = ltyfrdalert.getTriggerAt();
						String strWeekArr[] = strWeek.split(Constants.ADDR_COL_DELIMETER);
						strDay = Integer.parseInt(strWeekArr[1]);
						hoursLbId.setSelectedIndex(Integer.parseInt(strWeekArr[0]) - 1);
						daysLbId.setSelectedIndex(strDay);
						if (emailAddrs == null)
							emailAddrs = ltyfrdalert.getEmailId();

					}
					ltyfraudAlertsDailyChkId.setChecked(ltyfrdalert.isEnabled());
					ltyfraudAlertsWeeklyChkId.setChecked(ltyfrdalert.isEnabled());
					onCheck$ltyfraudAlertsWeeklyChkId();
                }
				if (ltyfraudAlertsWeeklyChkId.isChecked() || ltyfraudAlertsDailyChkId.isChecked()) {
					int mailIdCount = 1;
					String emailAddrArry[] = new String[1];
					logger.info("email ids are ===" + emailAddrs);
					if (!emailAddrs.contains(Constants.ADDR_COL_DELIMETER)) {
						emailAddrArry[0] = emailAddrs;
					} else { 
						emailAddrArry = emailAddrs.split(Constants.ADDR_COL_DELIMETER);
					}

					if (emailAddrArry != null && emailAddrArry.length > 0) {
						Components.removeAllChildren(alertMailDivId);
						for (String emailIds : emailAddrArry) {
							if (mailIdCount == 1) {
								emailAddTxtBxId.setText(emailIds);
							} else {
                            	   logger.info(" Email id "+emailIds);
								Div alertDiv = new Div();
								Textbox alertTextBx = new Textbox();
								if(type.equalsIgnoreCase("view")){
									alertTextBx.setVisible(false);
									alertTextBx.setText(emailIds);
									alertTextBx.setParent(alertDiv);
									alertTextBx.setWidth("250px");
									alertTextBx.setStyle("margin-left:85px;margin-top: 10px;margin-right:7px ;");

								}
								else{
							    alertTextBx.setVisible(true);
							    alertTextBx.setText(emailIds);
								alertTextBx.setParent(alertDiv);
								alertTextBx.setWidth("250px");
								alertTextBx.setStyle("margin-left:85px;margin-top: 10px;margin-right:7px ;");

								Image delImg = new Image();
								delImg.setAttribute("type", "ALERT_DEL");
								delImg.setSrc("/images/action_delete.gif");
								delImg.setStyle(
										"cursor:pointer;color:#2886B9;font-weight:bold;text-decoration: underline;");
								delImg.setTooltiptext("Delete");
								delImg.addEventListener("onClick", this);
								delImg.setParent(alertDiv);
								}
								alertDiv.setParent(alertMailDivId);
                               
							}
							mailIdCount++;
						}
					}

				}
			}
		} catch (Exception e) {
			logger.error("Exception while setting loyalty report data...", e);
		}
	}
	private boolean isValidate() {

		if (fromDateId.getValue() != null
				&& !fromDateId.getValue().toString().isEmpty()) {
			startDate = MyCalendar.getNewCalendar();
			startDate.setTime(fromDateId.getValue());
			logger.debug("startDate ::"
					+ MyCalendar.calendarToString(startDate,
							MyCalendar.FORMAT_DATETIME_STYEAR));
		} else {
			MessageUtil.setMessage("From date cannot be empty.", "color:red",
					"TOP");
			return false;
		}

		if (startDate == null) {
			return false;
		}

		if (toDateId.getValue() != null
				&& !toDateId.getValue().toString().isEmpty()) {
			endDate = MyCalendar.getNewCalendar();
			endDate.setTime(toDateId.getValue());
			logger.debug("endDate ::"+ MyCalendar.calendarToString(endDate,MyCalendar.FORMAT_DATETIME_STYEAR));
		} else {
			MessageUtil.setMessage("To date cannot be empty.", "color:red","TOP");
			return false;
		}

		if (endDate == null) {
			return false;
		}
		if(endDate.equals(startDate)){
			MessageUtil.setMessage("To date and From date should not be same.","color:red", "TOP");
			return false;
		}
		if (endDate.before(startDate)) {
			MessageUtil.setMessage("To date must be later than From date.","color:red", "TOP");
			return false;
		}
	
		return true;
	}

	public void onClick$addMoreEmailTBId() {

		Div alertDiv = new Div();
		Textbox alertTextBx = new Textbox();
		alertTextBx.setParent(alertDiv);
		alertTextBx.setWidth("250px");
		alertTextBx.setStyle("margin-left:85px;margin-top: 10px;margin-right:7px ;");

		Image delImg = new Image();
		delImg.setAttribute("type", "ALERT_DEL");
		delImg.setSrc("/images/action_delete.gif");
		delImg.setStyle("cursor:pointer;color:#2886B9;font-weight:bold;text-decoration: underline;");
		delImg.setTooltiptext("Delete");
		delImg.addEventListener("onClick", this);
		delImg.setParent(alertDiv);

		alertDiv.setParent(alertMailDivId);
	}

	@Override
	public void onEvent(Event event) throws Exception {

		// TODO Auto-generated method stub
		super.onEvent(event);

		if (event.getTarget() instanceof Image) {
			Image img = (Image) event.getTarget();
			Row temRow = null;
			String imgAction = (String) img.getAttribute("type");

			if ("ALERT_DEL".equals(imgAction)) {
				Div tempDiv = (Div) img.getParent();
				alertMailDivId.removeChild(tempDiv);
			}
			else if(imgAction.equals("view")){
				fraudAlertsTabBoxId.setSelectedIndex(1);
				Image tempImg = (Image) event.getTarget();
				Hbox hbox=(Hbox) tempImg.getParent();
				Row tempRow = (Row)hbox.getParent();
				LoyaltyFraudAlert fraudAlert	=tempRow.getValue();
				doEnableEditSettings(fraudAlert,"view");
				displayRuleDivId.setVisible(false);
				displayEmailFrqDivId.setVisible(false);
				setFradAlertTrx(users.getUserId(), getHavingCriteria(fraudAlert.getTrxRule()), getDateCriteria(fraudAlert.getDateRule()), 0, fraudAlertTrxPagingId.getPageSize());
				  saveAllDetailsBtnId.setAttribute(ATTRIBUTE_FRAUDALERT_OBJ,fraudAlert);
			}
               else if(imgAction.equals("edit")){
            	   fraudAlertsTabBoxId.setSelectedIndex(1);
   				Image tempImg = (Image) event.getTarget();
   				Hbox hbox=(Hbox) tempImg.getParent();
   				Row tempRow = (Row)hbox.getParent();
   				LoyaltyFraudAlert fraudAlert	=tempRow.getValue();
   				rulenametxboxId.setDisabled(true);
   				displayRuleDivId.setVisible(true);
   				displayEmailFrqDivId.setVisible(true);
   				ltyfraudrtAlertsChkId.setChecked(false);
   				ltyfraudAlertsDailyChkId.setChecked(false);
   				ltyfraudAlertsWeeklyChkId.setChecked(false);
   				saveAllDetailsBtnId.setLabel("Update & Fetch");
   			     saveAllDetailsBtnId.setAttribute(ATTRIBUTE_FRAUDALERT_OBJ,fraudAlert);
   				doEnableEditSettings(fraudAlert,"edit");
   				
				
			}
               else if(imgAction.equals("delete")){
   				Image tempImg = (Image) event.getTarget();
   				Hbox hbox=(Hbox) tempImg.getParent();
   				Row tempRow = (Row)hbox.getParent();
   				LoyaltyFraudAlert fraudAlert=tempRow.getValue();
   				String msg = "Are you sure you want to delete selected rule?";
   				int confirm = Messagebox.show(msg, "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
   				if(confirm == 1){
   					fraudAlertDaoForDML.delete(fraudAlert);
   	   				saveAllDetailsBtnId.removeAttribute(ATTRIBUTE_FRAUDALERT_OBJ);
   	   				MessageUtil.setMessage("Rule deleted sucessfully.","color:blue", "TOP");
   					}
   				displayAllCurrentAlert();
   				return;
   			}
		}
		if (event.getTarget() instanceof Paging) {

			Paging paging = (Paging) event.getTarget();
			int desiredPage = paging.getActivePage();

			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			if (paging.getId().equals("fraudAlertTrxPagingId")) {

				fraudAlertTrxPagingId.setActivePage(desiredPage);
				LoyaltyFraudAlert fraudAlert = (LoyaltyFraudAlert) saveAllDetailsBtnId
						.getAttribute(ATTRIBUTE_FRAUDALERT_OBJ);
				String trxRule = null;
				String dateRule = null;
				if (fraudAlert != null) {
					trxRule = fraudAlert.getTrxRule();
					dateRule = fraudAlert.getDateRule();
				}
				setFradAlertTrx(users.getUserId(), getHavingCriteria(trxRule), getDateCriteria(dateRule), ofs,
						(byte) pagingEvent.getPageable().getPageSize());
			}
		}
		if(event.getTarget() instanceof Label ) {

			Label tempLable = (Label)event.getTarget();
			Row tempRow = (Row)tempLable.getParent();
			Map<String,Object>map=tempRow.getValue();
			if(map != null)
				session.setAttribute(FRAUD_ALERT_CARDNO, map.get("membership_number"));
			
	        String type=(String) tempLable.getAttribute(columnType);
	        if(type.equalsIgnoreCase("CardNo")){
				Redirect.goTo(PageListEnum.LOYALTY_CUSTOMER_LOOKUP);
			}
	        else{
			if(map == null){
				Messagebox.show("No Fraud Alert Trx Found !.","Information",Messagebox.OK,Messagebox.INFORMATION);
				return;
			}
			else
				onClickNoOfTrxAnchId();
		}
		}
	}
	public void onClickNoOfTrxAnchId() {
		
		try{
			fraudTrxDetailsSubWinId.doHighlighted();
			fraudTrxDetailsSubWinId.setVisible(true);
			fraudTrxDetailsSubWinId$viewFraudTrxDetailsDivId.setVisible(true);
			fraudTrxDetailsSubWinId.setTitle("Fraud Alert Transaction Report");
			fraudTrxDetailsSubWinId.setHeight("400px");
			int count =  fraudTrxDetailsSubWinId$viewAllFraudTrxListId.getItemCount();
			for(; count>0; count--) {
				fraudTrxDetailsSubWinId$viewAllFraudTrxListId.removeItemAt(count-1);
			}
			//Components.removeAllChildren(fraudTrxDetailsSubWinId$viewAllFraudTrxListId);
			String cardNo =(String) session.getAttribute("Fraud_Alert_CardNo");
			LoyaltyFraudAlert fraudAlert = (LoyaltyFraudAlert) saveAllDetailsBtnId
					.getAttribute(ATTRIBUTE_FRAUDALERT_OBJ);
			String trxRule = null;
			String dateRule = null;
			if (fraudAlert != null) {
				trxRule = fraudAlert.getTrxRule();
				dateRule = fraudAlert.getDateRule();
			}    	
			List<LoyaltyTransactionChild> trxList=fraudAlertDao.getAllFraudAlertTrxbyCardNo(users.getUserId(), cardNo, getHavingCriteria(trxRule), getDateCriteria(dateRule));
	
			if(trxList == null){
				MessageUtil.setMessage("No items found", "red");
				return;
			}
			/*for (LoyaltyTransactionChild ltyTrx : trxList) {
				Row row = new Row();
				row.setParent(fraudTrxDetailsSubWinId$viewAllFraudTrx);
				row.appendChild(new Label(""+MyCalendar.calendarToString(ltyTrx.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR,clientTimeZone)));
				row.appendChild(new Label(""+ltyTrx.getDocSID()));
				row.appendChild(new Label(""+ltyTrx.getStoreNumber()));
				row.appendChild(new Label(""+ltyTrx.getTransactionType()));
				row.appendChild(new Label(""+((ltyTrx.getAmountDifference())!=null?ltyTrx.getAmountDifference():" ")));
				row.appendChild(new Label(""+((ltyTrx.getPointsDifference())!=null?ltyTrx.getPointsDifference():"")));
				row.appendChild(new Label(""+((ltyTrx.getEnteredAmount())!=null?ltyTrx.getEnteredAmount():" ")));
				row.appendChild(new Label(""+((ltyTrx.getAmountBalance())!=null?ltyTrx.getAmountBalance():"")));
			
			}*/
			for (LoyaltyTransactionChild ltyTrx : trxList) {
            
				Listitem listItem = new Listitem();
				Listcell listCell = new Listcell();
				listCell.setLabel(""+MyCalendar.calendarToString(ltyTrx.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR,clientTimeZone));
				listCell.setStyle("padding-left:10px;");
				listCell.setParent(listItem);
				listCell = new Listcell();
				listCell.setStyle("padding-left:10px;");
				listCell.setLabel(ltyTrx.getDocSID()!=null?""+ltyTrx.getDocSID():"--");
				listCell.setTooltiptext(ltyTrx.getDocSID()!=null?""+ltyTrx.getDocSID():"--");
				listCell.setParent(listItem);
				listCell = new Listcell();
				listCell.setStyle("padding-left:10px;");
				listCell.setLabel(ltyTrx.getStoreNumber()!=null?""+ltyTrx.getStoreNumber():"--");
				listCell.setTooltiptext(ltyTrx.getStoreNumber()!=null?""+ltyTrx.getStoreNumber():"--");
				listCell.setParent(listItem);
				listCell = new Listcell();
				listCell.setStyle("padding-left:10px;");
				listCell.setLabel(""+ltyTrx.getTransactionType());
				listCell.setTooltiptext(ltyTrx.getTransactionType());
				listCell.setParent(listItem);
				listCell = new Listcell();
				listCell.setStyle("padding-left:10px;");
				listCell.setLabel(((ltyTrx.getAmountDifference())!=null?ltyTrx.getAmountDifference():" "));
				listCell.setTooltiptext(""+((ltyTrx.getAmountDifference())!=null?ltyTrx.getAmountDifference():" "));
				listCell.setParent(listItem);
				listCell = new Listcell();
				listCell.setStyle("padding-left:10px;");
				listCell.setLabel(((ltyTrx.getPointsDifference())!=null?ltyTrx.getPointsDifference():" "));
				listCell.setTooltiptext(((ltyTrx.getPointsDifference())!=null?ltyTrx.getPointsDifference():" "));
				listCell.setParent(listItem);
				listCell = new Listcell();
				listCell.setStyle("padding-left:10px;");
				listCell.setLabel(((ltyTrx.getEnteredAmount())!=null?""+ltyTrx.getEnteredAmount():" "));
				listCell.setTooltiptext(""+((ltyTrx.getEnteredAmount())!=null?ltyTrx.getEnteredAmount():" "));
				listCell.setParent(listItem);
				listCell = new Listcell();
				listCell.setStyle("padding-left:10px;");
				listCell.setLabel(((ltyTrx.getAmountBalance())!=null?""+ltyTrx.getAmountBalance():" "));
				listCell.setTooltiptext(""+((ltyTrx.getAmountBalance())!=null?ltyTrx.getAmountBalance():" "));
				listCell.setParent(listItem);
				listItem.setHeight("30px");
				listItem.setValue(ltyTrx);
				listItem.setParent(fraudTrxDetailsSubWinId$viewAllFraudTrxListId);
			}
		}catch (Exception e) {
			logger.error("Exception while fetching fraud alert trx details.....");
			logger.error("Exception ::" , e);
		}
		logger.debug("<<<<<<<<<<<<< completed onClickNoOfTrxAnchId ");
	}
	private String prepareEmailIdStr() {
		String emailIdStr = null;
		List<Component> tempDivList = alertMailDivId.getChildren();
		if (emailAddTxtBxId.getValue() != null && !emailAddTxtBxId.getValue().trim().isEmpty()) {
			if (!Utility.validateEmail(emailAddTxtBxId.getValue().trim())) {
				MessageUtil.setMessage("Please enter valid email address.", "color:red", "TOP");
				return emailIdStr;
			}
			emailIdStr = emailAddTxtBxId.getValue().trim();
		}
		if (tempDivList != null && tempDivList.size() > 0) {
			for (Component tempDiv : tempDivList) {
				Textbox tempTb = (Textbox) tempDiv.getFirstChild();

				if (tempTb.getValue() != null && !tempTb.getValue().trim().isEmpty()) {
					if (!Utility.validateEmail(tempTb.getValue().trim())) {
						MessageUtil.setMessage("Please enter valid email address.", "color:red", "TOP");
						return emailIdStr;
					}
					if (emailIdStr == null || emailIdStr.isEmpty()) {
						emailIdStr = tempTb.getValue().trim();
					} else {
						emailIdStr += Constants.ADDR_COL_DELIMETER + tempTb.getValue().trim();
					}
				}
			}
		}
		if (emailIdStr == null){
			MessageUtil.setMessage("Email is required.", "color:red", "TOP");	
			return null;
		}
		return emailIdStr;
	}

	public void setFradAlertTrx(Long userId, String havingCriteria, String dateCreiteria, int start, int end) {
		Components.removeAllChildren(fraudAlertTrxRowId);

		Label tempLabel = null;

		List<Map<String, Object>> list = fraudAlertDao.getFraudAlertTrxByUserId(userId, havingCriteria, dateCreiteria, start, end);
		for (Map<String, Object> map : list) {
			Row row = new Row();
			row.setParent(fraudAlertTrxRowId);
			// set id
			tempLabel = new Label("" + map.get("num_of_trx_count").toString());
			tempLabel.setAttribute(columnType, "NoOfTrx");
			tempLabel.setStyle("cursor:pointer;color:blue;text-decoration: underline;");
			tempLabel.addEventListener("onClick", this);
			row.appendChild(tempLabel);
			// set card no
			tempLabel = new Label("" + map.get("membership_number"));
			tempLabel.setAttribute(columnType, "CardNo");
			tempLabel.setStyle("cursor:pointer;color:blue;text-decoration: underline;");
			tempLabel.addEventListener("onClick", this);
			row.appendChild(tempLabel);
			// issuance count
			row.appendChild(new Label("" + map.get("issuance_count")));
			// redemption count
			row.appendChild(new Label("" + map.get("redemption_count")));
			// issuance amt
			
			row.appendChild(new Label( ""+ ((map.get("issuance_point")!=null)? map.get("issuance_point") : "")));
			row.appendChild(new Label( ""+ ((map.get("issuance_amt")!=null)? map.get("issuance_amt") : "")));
			// redemption amt
			row.appendChild(new Label("" + ((map.get("redemption_amt")!=null)?map.get("redemption_amt"): "")));
			row.setValue(map);
			row.setParent(fraudAlertTrxRowId);
		}

	}

	public void onSelect$fraudAlertTrxPagingId() {

		try {
			int tempCount = Integer.parseInt(fraudAlertPageSizeLBId.getSelectedItem().getLabel());
			fraudAlertTrxPagingId.setPageSize(tempCount);
			LoyaltyFraudAlert fraudAlert = (LoyaltyFraudAlert) saveAllDetailsBtnId
					.getAttribute(ATTRIBUTE_FRAUDALERT_OBJ);
			String trxRule = null;
			String dateRule = null;
			if (fraudAlert != null) {
				trxRule = fraudAlert.getTrxRule();
				dateRule = fraudAlert.getDateRule();
			}
			setFradAlertTrx(users.getUserId(), getHavingCriteria(trxRule), getDateCriteria(dateRule), 0, fraudAlertTrxPagingId.getPageSize());

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

	public void onSelect$fraudAlertPageSizeLBId() {

		try {

			int tempCount = Integer.parseInt(fraudAlertPageSizeLBId.getSelectedItem().getLabel());
			fraudAlertTrxPagingId.setPageSize(tempCount);
			LoyaltyFraudAlert fraudAlert = (LoyaltyFraudAlert) saveAllDetailsBtnId
					.getAttribute(ATTRIBUTE_FRAUDALERT_OBJ);
			String trxRule = null;
			String dateRule = null;
			if (fraudAlert != null) {
				trxRule = fraudAlert.getTrxRule();
				dateRule = fraudAlert.getDateRule();
			}
			setFradAlertTrx(users.getUserId(),getHavingCriteria(trxRule), getDateCriteria(dateRule), 0, fraudAlertTrxPagingId.getPageSize());

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
	
	public void displayAllCurrentAlert(){
		Components.removeAllChildren(alertReportTrxRowId);
		List<LoyaltyFraudAlert> listFraudAlert = fraudAlertDao.findByUserId(users.getUserId());
		if(listFraudAlert==null)
			return;
	//	Components.removeAllChildren(alertReportTrxRowId);
		for ( LoyaltyFraudAlert frdalter : listFraudAlert) {
			Row row = new Row();
			row.setParent(alertReportTrxRowId);
			
			row.appendChild(new Label(frdalter.getRuleName()));
			//making trx rule
			
			row.appendChild(new Label(getTrxRuleForDisplay(frdalter.getTrxRule())));
			row.appendChild(new Label(getDateRuleForDisplay(frdalter.getDateRule())));
			row.appendChild(new Label( getEmailFrq(frdalter)));
			
			Hbox hbox = new Hbox();
			Image previewImg = new Image("/images/Preview_icn.png");
			previewImg.setTooltiptext("View Transactions");
			previewImg.setStyle("cursor:pointer;margin-right:5px;");
			previewImg.addEventListener("onClick", this);
			previewImg.setAttribute("type", "view");
			previewImg.setParent(hbox);

			Image editImg = new Image("/img/email_edit.gif");
			editImg.setTooltiptext("Edit Rule");
			editImg.setStyle("cursor:pointer;margin-right:5px;");
			editImg.addEventListener("onClick", this);
			editImg.setAttribute("type", "edit");
			editImg.setParent(hbox);
			Image delImg = new Image("/img/action_delete.gif");
			delImg.setTooltiptext("Delete Rule");
			delImg.setStyle("cursor:pointer;margin-right:5px;");
			delImg.addEventListener("onClick", this);
			delImg.setAttribute("type", "delete");
			delImg.setParent(hbox);
            row.appendChild(hbox);
			row.setValue(frdalter);
			row.setParent(alertReportTrxRowId);
		}

	}
		public String getTrxRuleForDisplay(String trxRule){
			 StringBuilder sb=new StringBuilder();
			 if(trxRule!=null){
			    	String trx[]=trxRule.split("\\|"); 
			    	String sign=(trx[2].trim().equalsIgnoreCase("morethan")) ? "more than " :"equal to ";
			    	
			    	if(trx[0].trim().equalsIgnoreCase("trx")){
			    	if(trx[1].trim().equalsIgnoreCase("Issuance"))	
			    		sb.append("# of transaction for Issuance  is "+sign+""+trx[3]);
			    	else if(trx[1].trim().equalsIgnoreCase("Redemption"))
			    		sb.append("# of transaction for Redemption  is "+sign+""+trx[3]);
			    	else
			    		sb.append("# of transaction for Issuance & Redemption  are  "+sign+""+trx[3]);
			    	}
			    	else{
			    		 if(trx[0].trim().equalsIgnoreCase("amount")){
			    		    if(trx[1].trim().equalsIgnoreCase("Issuance"))	
			    		    	sb.append("Amount for Issuance  is "+sign+""+trx[3]);
				    	   else if(trx[1].trim().equalsIgnoreCase("Redemption"))
				    		   sb.append("Amount for Redemption  is "+sign+""+trx[3]);
				    	   else
				    		   sb.append("Amount for Issuance & Redemption  are "+sign+""+trx[3]);
			    		 }
			    		 else{
			    			 if(trx[1].trim().equalsIgnoreCase("Issuance"))	
			    				 sb.append("Points for Issuance  is "+sign+""+trx[3]);
			    			 else
			    				 sb.append("Points for Issuance & Redemption  are "+sign+""+trx[3]);
			    		 }
			    	}
			    } 
			 return sb.toString();
		}
		public String getDateRuleForDisplay(String dateRule){	
			 StringBuilder sb=new StringBuilder();
			      if(dateRule!=null){
				
			    	String dt[]=dateRule.split("\\|");
			    	String day=null;
			    	if(dt[0].trim().equalsIgnoreCase("withinlast")){
			    		if(Integer.parseInt(dt[2])==1)
			    		day=dt[1].equalsIgnoreCase("day")?"day":dt[1].equalsIgnoreCase("week")?"week":"month";
			    		else
			    			day=dt[1].equalsIgnoreCase("day")?"days":dt[1].equalsIgnoreCase("week")?"weeks":"months";
			    	 sb.append("Last "+dt[2]+" "+day);
			    	}
			    	else{
			    	sb.append("From "+dt[2]+" To "+dt[3]);
			    	}
			    	
			 } 
			 return sb.toString();
		}
		public String getEmailFrq(LoyaltyFraudAlert frdalert){
			StringBuilder sb=new StringBuilder();
			if(frdalert.getFrequency()!=null &&frdalert.getFrequency().equals(OCConstants.LTY_SETTING_REPORT_FRQ_DAY)){		
				if (frdalert.isEnabled()) {
			 String time="";
			 if(Integer.parseInt(frdalert.getTriggerAt()) <=11)
				time= frdalert.getTriggerAt()+" AM";
			 else if(Integer.parseInt(frdalert.getTriggerAt()) ==12)
				 time=(Integer.parseInt(frdalert.getTriggerAt()))+" PM";
			 else
				 time=(Integer.parseInt(frdalert.getTriggerAt())-12)+" PM";
			 sb.append("Daily at "+time);
			}
			}
			else if(frdalert.getFrequency()!=null &&frdalert.getFrequency().equals(OCConstants.LTY_SETTING_REPORT_FRQ_WEEK)){
				if (frdalert.isEnabled()) {
					String strWeek = frdalert.getTriggerAt();
					String strWeekArr[] = strWeek.split(Constants.ADDR_COL_DELIMETER);
					 String day="";
					 
					 day=weekMap.get(Integer.parseInt(strWeekArr[1]));
					 String time="";
					 if(Integer.parseInt(strWeekArr[0]) <=11)
						time= strWeekArr[0]+" AM";
					 else if(Integer.parseInt(strWeekArr[0]) ==12)
						 time=Integer.parseInt(strWeekArr[0])+" PM";
					 else
						 time=(Integer.parseInt(strWeekArr[0])-12)+" PM";
					sb.append("Weekly every "+day+" at "+time);
				}
			}
			else{
				if (frdalert.isEnabled()) {
					String strWeek = frdalert.getTriggerAt();
					String strWeekArr[] = strWeek.split(Constants.ADDR_COL_DELIMETER);
					 String day="";
					 
					 day=weekMap.get(Integer.parseInt(strWeekArr[1]));
					 String time="";
					 if(Integer.parseInt(strWeekArr[0]) <=11)
						time= strWeekArr[0]+" AM";
					 else if(Integer.parseInt(strWeekArr[0]) ==12)
						 time=Integer.parseInt(strWeekArr[0])+" PM";
					 else
						 time=(Integer.parseInt(strWeekArr[0])-12)+" PM";
					sb.append("Daily at "+time+" : Weekly every "+day+"  at "+time);
				}
			}	
			return sb.toString();
			
			
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
			if (!(chkList.get(i) instanceof Checkbox))
				continue;

			tempChk = (Checkbox) chkList.get(i);
			tempChk.setChecked(flag);

		} // for
	}

	public void onClick$exportBtnFraudAlertTrxId() {
		createWindow();
		custExport.setVisible(true);
		custExport.doHighlighted();

	}

	public void onClick$selectFieldBtnId$custExport() {

		custExport.setVisible(false);
		List<Component> chkList = custExport$chkDivId.getChildren();

		int indexes[] = new int[chkList.size()];

		boolean checked = false;

		for (int i = 0; i < chkList.size(); i++) {
			indexes[i] = -1;
		} // for

		Checkbox tempChk = null;

		for (int i = 0; i < chkList.size(); i++) {
			if (!(chkList.get(i) instanceof Checkbox))
				continue;

			tempChk = (Checkbox) chkList.get(i);

			if (tempChk.isChecked()) {
				indexes[i] = 0;
				checked = true;
			} else {
				indexes[i] = -1;
			}

		} // for

		if (((Checkbox) custExport$chkDivId.getLastChild()).isChecked()) {

			checked = true;
		}

		if (checked) {

			int confirm = Messagebox.show("Do you want to export with selected fields?", "Confirm",
					Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if (confirm == 1) {
				try {
					String fileFormate = exportFraudAlertTrxLbId.getSelectedItem().getValue().toString();
					if (fileFormate.contains("xls")) {
						exportExcel(fileFormate, indexes);
					} else if (fileFormate.contains("csv")) {
						exportCSV(fileFormate, indexes);
					}

				} catch (Exception e) {
					logger.error("Exception caught :: ", e);
				}
			} else {
				custExport.setVisible(true);
			}

		} else {

			MessageUtil.setMessage("Please select atleast one field", "red");
			custExport.setVisible(false);
		}

	}

	private void exportCSV(String type, int[] indexes) {
		logger.debug("-- just entered --");
		StringBuffer sb = null;
		String userName = GetUser.getUserName();
		String usersParentDirectory = (String) PropertyUtil.getPropertyValue("usersParentDirectory");
		String exportDir = usersParentDirectory + "/" + userName + "/Export/";
		File downloadDir = new File(exportDir);
		if (downloadDir.exists()) {
			try {
				FileUtils.deleteDirectory(downloadDir);
				logger.debug(downloadDir.getName() + " is deleted");
			} catch (Exception e) {
				logger.error("Exception ::", e);

				logger.debug(downloadDir.getName() + " is not deleted");
			}
		}
		if (!downloadDir.exists()) {
			downloadDir.mkdirs();
		}

		if (type.contains("csv")) {

			String filePath = exportDir + "Loyalty_Fraud_Alerts_Report_"
					+ MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC);
			try {
				logger.debug("Download File path : " + filePath);
				filePath=filePath+"."+type;
				File file = new File(filePath);
				BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
				LoyaltyFraudAlert fraudAlert = (LoyaltyFraudAlert) saveAllDetailsBtnId
						.getAttribute(ATTRIBUTE_FRAUDALERT_OBJ);

				String trxRule = null;
				String dateRule = null;
				if (fraudAlert != null) {
					trxRule = fraudAlert.getTrxRule();
					dateRule = fraudAlert.getDateRule();
				}
                 if(trxRule==null||dateRule==null){
                	 Messagebox.show("No rule found.", "Info", Messagebox.OK, Messagebox.INFORMATION);
 					return; 
                 }
				int count = fraudAlertDao.getCountFraudAlertTrx(users.getUserId(),  getHavingCriteria(trxRule), getDateCriteria(dateRule));
				if (count == 0) {
					Messagebox.show("No fraud alerts trx. found.", "Info", Messagebox.OK, Messagebox.INFORMATION);
					return;
				}
				String udfFldsLabel = "";

				if (indexes[0] == 0) {
					udfFldsLabel = "\"" + "No. of transactions" + "\"" + ",";
				}
				if (indexes[1] == 0) {
					udfFldsLabel += "\"" + "Card number" + "\"" + ",";
				}
				if (indexes[2] == 0) {
					udfFldsLabel += "\"" + "Issuance Count" + "\"" + ",";
				}
				if (indexes[3] == 0) {
					udfFldsLabel += "\"" + "Redemption Count" + "\"" + ",";
				}
				if (indexes[4] == 0) {
					udfFldsLabel += "\"" + "Total Issued Points" + "\"" + ",";
					
				}
				if (indexes[5] == 0) {
					udfFldsLabel += "\"" + "Total Issued Amt." + "\"" + ",";
					
				}
				if (indexes[6] == 0) {

					udfFldsLabel += "\"" + "Total Redeemed Amt." + "\"" + ",";
				}
				sb = new StringBuffer();
				sb.append(udfFldsLabel);
				sb.append("\r\n");

				bw.write(sb.toString());
				// System.gc();

				int size = 1000;
				for (int i = 0; i < count; i += size) {
					logger.info("inside csv export =============");
					sb = new StringBuffer();
					List<Map<String, Object>> list = fraudAlertDao.getFraudAlertTrxByUserId(users.getUserId(),  getHavingCriteria(trxRule), getDateCriteria(dateRule), i, size);

					if (list != null && list.size() > 0)
						for (Map<String, Object> map : list) {
							if (indexes[0] == 0) {
								sb.append("\"");
								sb.append(map.get("num_of_trx_count"));
								sb.append("\",");
							}
							if (indexes[1] == 0) {
								sb.append("\"");
								sb.append(map.get("membership_number"));
								sb.append("\",");
							}
							if (indexes[2] == 0) {

								sb.append("\"");
								sb.append(map.get("issuance_count"));
								sb.append("\",");
							}
							if (indexes[3] == 0) {

								sb.append("\"");
								sb.append(map.get("redemption_count"));
								sb.append("\",");
							}
							if (indexes[4] == 0) {
								sb.append("\"");
								sb.append(((map.get("issuance_point")!=null)? map.get("issuance_point") : ""));
								sb.append("\",");
								
							}
							if (indexes[5] == 0) {
								sb.append("\"");
								sb.append(((map.get("issuance_amt")!=null)? map.get("issuance_amt") : ""));
								sb.append("\",");
								
							}

							if (indexes[6] == 0) {

								sb.append("\"");
								sb.append(((map.get("redemption_amt")!=null)?map.get("redemption_amt"): ""));
								sb.append("\",");
							}
							sb.append("\r\n");
						}
					bw.write(sb.toString());

					sb = null;
				}
				bw.flush();
				bw.close();
				Filedownload.save(file, "text/plain");
			} catch (IOException e) {
				logger.error("Exception ::", e);

			}
			logger.debug("-- exit --");
		}

	}

	public void createWindow() {

		try {

			Components.removeAllChildren(custExport$chkDivId);

			Checkbox tempChk2 = new Checkbox("No. of transactions");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);

			tempChk2 = new Checkbox("Card number");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);

			tempChk2 = new Checkbox("Issuance Count");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);

			tempChk2 = new Checkbox("Redemption Count");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
            
			tempChk2 = new Checkbox("Total Issued Points");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			
			tempChk2 = new Checkbox("Total Issued Amt.");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
            
			

			tempChk2 = new Checkbox("Total Redeemed Amt.");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);

		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}

	public void exportExcel(String exportType, int[] indexes) {
		try {
			String userName = GetUser.getUserName();
			String usersParentDirectory = (String) PropertyUtil.getPropertyValue("usersParentDirectory");

			File downloadDir = new File(usersParentDirectory + "/" + userName + "/List/download/");

			if (!downloadDir.exists()) {
				downloadDir.mkdirs();
			}

			String filePath = usersParentDirectory + "/" + userName + "/List/download/fraud_Alert_Report_"
					+ System.currentTimeMillis() + "." +exportType;
			File file = new File(filePath);
			logger.debug("Writing to the file : " + filePath);
			FileOutputStream fileOut = new FileOutputStream(filePath);
			HSSFWorkbook hwb = new HSSFWorkbook();
			HSSFSheet sheet = hwb.createSheet("Fraud Alert");
			HSSFRow row = sheet.createRow((short) 0);
			HSSFCell cell = null;
			LoyaltyFraudAlert fraudAlert = (LoyaltyFraudAlert) saveAllDetailsBtnId
					.getAttribute(ATTRIBUTE_FRAUDALERT_OBJ);
			String trxRule = null;
			String dateRule = null;
			if (fraudAlert != null) {
				trxRule = fraudAlert.getTrxRule();
				dateRule = fraudAlert.getDateRule();
			}
			 if(trxRule==null||dateRule==null){
            	 Messagebox.show("No rule found.", "Info", Messagebox.OK, Messagebox.INFORMATION);
					return; 
             }
			int count = fraudAlertDao.getCountFraudAlertTrx(users.getUserId(),  getHavingCriteria(trxRule), getDateCriteria(dateRule));
		
			if (count == 0) {
				Messagebox.show("No fraud alerts trx. found.", "Info", Messagebox.OK, Messagebox.INFORMATION);
				return;
		      }
			
			row = sheet.createRow(0);
			int cellNo = 0;
			if (indexes[0] == 0) {
				
				cell = row.createCell((cellNo++));
				cell.setCellValue("No. of transactions");
			}
			if (indexes[1] == 0) {
				
				cell = row.createCell(cellNo++);
				cell.setCellValue("Card number");
			}
			if (indexes[2] == 0) {
				
				cell = row.createCell(cellNo++);
				cell.setCellValue("Issuance Count");
			}
			if (indexes[3] == 0) {
				
				cell = row.createCell(cellNo++);
				cell.setCellValue("Redemption Count");
			}
			if (indexes[4] == 0) {
				
				cell = row.createCell(cellNo++);
				cell.setCellValue("Total Issued Points");
				
			}
			if (indexes[5] == 0) {
				
				cell = row.createCell(cellNo++);
				cell.setCellValue("Total Issued Amt.");
			}
			if (indexes[6] == 0) {
				
				cell = row.createCell(cellNo++);
				cell.setCellValue("Total Redeemed Amt.");
			}
			int size = 1000; 
			for (int i = 0; i < count; i += size) {
				List<Map<String, Object>> list = fraudAlertDao.getFraudAlertTrxByUserId(users.getUserId(),  getHavingCriteria(trxRule), getDateCriteria(dateRule), i, size);
				if (list != null && list.size() > 0){
					int rowId = i+1;
					for (Map<String, Object> map : list) {
						row = sheet.createRow(rowId++);
						int columnId = 0;
						cell = null;
						if (indexes[0] == 0) {
							cell = row.createCell(columnId++);
							cell.setCellValue("" + map.get("num_of_trx_count"));
						}
						if (indexes[1] == 0) {
							cell = row.createCell(columnId++);
							cell.setCellValue("" + map.get("membership_number"));
						}
						if (indexes[2] == 0) {
							cell = row.createCell(columnId++);
							cell.setCellValue("" + map.get("issuance_count"));
						}
						if (indexes[3] == 0) {
							cell = row.createCell(columnId++);
							cell.setCellValue("" + map.get("redemption_count"));
						}
						if (indexes[4] == 0) {
							cell = row.createCell(columnId++);
							cell.setCellValue("" + ((map.get("issuance_point")!=null)? map.get("issuance_point") : ""));
						}
						if (indexes[5] == 0) {
							cell = row.createCell(columnId++);
							cell.setCellValue("" + ((map.get("issuance_amt")!=null)? map.get("issuance_amt") : ""));
							
						}
						if (indexes[6] == 0) {
							cell = row.createCell(columnId++);
							cell.setCellValue("" +((map.get("redemption_amt")!=null)?map.get("redemption_amt"): ""));
						}
						
						
					}
				  }	
			     }
				
			     hwb.write(fileOut);
			    fileOut.flush();
				fileOut.close();
		
			Filedownload.save(file, "application/vnd.ms-excel");
			logger.debug("exited");

		} catch (Exception e) {
			logger.error("** Exception : ", e);
		}

	}
}
