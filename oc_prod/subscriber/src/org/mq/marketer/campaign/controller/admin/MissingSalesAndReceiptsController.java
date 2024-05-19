package org.mq.marketer.campaign.controller.admin;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

public class MissingSalesAndReceiptsController extends GenericForwardComposer{

	
	private Listbox orgListBxId, usersListBxId;
	private UsersDao usersDao;
	private MailingListDao mailingListDao;
	
	private static String selectStr = "--select--";
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private Users users;
	
	private Textbox posListTxtBoxId;
	
	private MyDatebox fromDateboxId, toDateboxId;
	private Div strtdatesDivId, enddatesDivId, endNumberDivId, strtNumbersDivId, ignoreDayDivId, missingDatesDivId, missingNumbersDivId;
	
	private Toolbarbutton posContTbBtnId;
	private Radiogroup missingOptionRgId;
	private Radio missingDatesRadioId;
	
	RetailProSalesDao retailProSalesDao;
	
	private Rows salesDateRowsId, receiptNumRowsId;
	public MissingSalesAndReceiptsController() {
		usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		retailProSalesDao = (RetailProSalesDao)SpringUtil.getBean("retailProSalesDao");
		
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
     	PageUtil.setHeader("Missing Sales & Receipts","",style,true);
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		setUserOrg();
		setDateValues();
		
		
	}
	
	
	private void setUserOrg() {

		
		List<UserOrganization> orgList	= usersDao.findAllOrganizations();
		
		if(orgList == null) {
			logger.debug("no organization list exist from the DB...");
			return ;
		}
		
		Listitem tempList = new Listitem(selectStr);
		tempList.setParent(orgListBxId);
		
		Listitem tempItem = null;
		
		for (UserOrganization userOrganization : orgList) {
			
			//set Organization Name
			if(userOrganization.getOrganizationName() == null || userOrganization.getOrganizationName().trim().equals("")) continue;
			
			tempItem = new Listitem(userOrganization.getOrganizationName().trim(),userOrganization.getUserOrgId());
			tempItem.setParent(orgListBxId);
		} // for
		orgListBxId.setSelectedIndex(0);
		
		
	} // setUserOrg()
	
	public void onSelect$orgListBxId() {
		
		if(orgListBxId.getSelectedItem().getLabel().equals(selectStr)) {
			users = null;
			return;
		}
		
		
		posListTxtBoxId.setValue("");
		posListTxtBoxId.setDisabled(false);
		
		Components.removeAllChildren(usersListBxId);
		
		Listitem tempList = new Listitem(selectStr);
		tempList.setParent(usersListBxId);
		
		List<Users> usersList = usersDao.getPrimaryUsersByOrg((Long)orgListBxId.getSelectedItem().getValue());
		
		if(usersList == null || usersList.size() == 0) {
			logger.debug("No users exists for the Selected Organization..");
			return;
		}
		Listitem tempItem = null;
		for (Users users : usersList) {
			String userNameStr = Utility.getOnlyUserName(users.getUserName());
//			logger.debug("UserName is ::"+userNameStr);
			
			tempItem = new Listitem(userNameStr,users);
			tempItem.setParent(usersListBxId);
			
		} // for
		
		usersListBxId.setSelectedIndex(0);
		
		
	}//
	
	
	public void onSelect$usersListBxId() {
		
		posListTxtBoxId.setValue("");
		posListTxtBoxId.setDisabled(false);
		
		if(usersListBxId.getSelectedItem().getLabel().equals(selectStr)) {
			users = null;
			
			
			return;
		}
		users = (Users)usersListBxId.getSelectedItem().getValue();
		
		
		logger.debug("user Object is Exist"+users);
		
		
		if(users == null) return;
		
		MailingList posML = mailingListDao.findPOSMailingList(users);
		
		if(posML == null) {
			logger.debug("no POS mailing list existed ..");
			return ;
		}
		
		posListTxtBoxId.setValue(posML.getListName());
		posListTxtBoxId.setAttribute("POSMLID", posML.getListId());
		posListTxtBoxId.setDisabled(true);
		
	}
	
	
	public void setDateValues() {
		
		Calendar cal = MyCalendar.getNewCalendar();
		toDateboxId.setValue(cal);
		logger.debug("ToDate (server) :"+cal);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-1);
		logger.debug("FromDate (server) :"+cal);
		fromDateboxId.setValue(cal);
		
	}
	
	
	
	public void onCheck$missingOptionRgId() {
		
		if(missingDatesRadioId.isSelected()) {
			
			strtdatesDivId.setVisible(true);
			enddatesDivId.setVisible(true);
			ignoreDayDivId.setVisible(true);
			
			strtNumbersDivId.setVisible(false);
			endNumberDivId.setVisible(false);
			
			
			missingNumbersDivId.setVisible(false);
			Components.removeAllChildren(salesDateRowsId);
			
		}
		else {
			
			strtdatesDivId.setVisible(false);
			enddatesDivId.setVisible(false);
			ignoreDayDivId.setVisible(false);
			
			strtNumbersDivId.setVisible(true);
			endNumberDivId.setVisible(true);
			
			
			missingDatesDivId.setVisible(false);
			Components.removeAllChildren(receiptNumRowsId);
			
			
			
		}
		
		
		
	}
	
	public void onClick$getBtnId() {
		
		if(orgListBxId.getSelectedIndex() == 0) {
			
			MessageUtil.setMessage("Please select at least one organization.", "color:red;");
			return;
			
		}
		
		if(usersListBxId.getSelectedIndex() == 0) {
			
			MessageUtil.setMessage("Please select at least one user.", "color:red;");
			return;
			
		}
		
		if(posListTxtBoxId.getText().trim().length() == 0 ) {
			
			MessageUtil.setMessage("No POS-type mailing list exists for the selected user.", "color:red;");
			return;
			
		}
		
		//int option = missingOptionRgId.getSelectedIndex();
		
		Long listId = (Long)posListTxtBoxId.getAttribute("POSMLID");
		
		if(missingDatesRadioId.isChecked()) {
			
			getMissingDates(listId);
		}//if
		else {
			
			getMissingReceiptNumbers(listId);
		}//else if
		
	}
	
	
	public void getMissingDates(Long listId) {
		
		//validate dates
		
		List dateList = null;
		int stIndex=0;
		int size=2000;
		
		Calendar serverFromDateCal = fromDateboxId.getServerValue();
		Calendar serverToDateCal = toDateboxId.getServerValue();
		
		Calendar tempClientFromCal = fromDateboxId.getClientValue();
		Calendar tempClientToCal = toDateboxId.getClientValue();
		
		logger.debug("client From :"+tempClientFromCal +", client To :"+tempClientToCal);
		
		//change the time for startDate and endDate in order to consider right from the 
		// starting time of startDate to ending time of endDate
		
		
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
		
		if(serverToDateCal.compareTo(serverFromDateCal) < 0){
			MessageUtil.setMessage("'To' date should be after 'From' date.", "color:red",	"TOP");
			return;
		}
		
		long toCalInmillis = serverToDateCal.getTimeInMillis();
		long fromCalInmillis = serverFromDateCal.getTimeInMillis();
		
		
		
		long value = (1000l*60*60*24*30*3);
		
		//logger.info("diff ::"+(toCalInmillis-fromCalInmillis) +"=="+(value));
		
		if(toCalInmillis-fromCalInmillis > value){
			MessageUtil.setMessage("Date range should not exceed 90 days.", "color:red",	"TOP");
			return;
		}
		
		Components.removeAllChildren(salesDateRowsId);
		
		
		
		if(listId == null || users == null) {
			
			logger.debug("got no users or no List ");
			return;
			
		}
		
		
		Calendar tempCal = null;
		
		Calendar salesCal = Calendar.getInstance();

		Calendar fromCal = Calendar.getInstance();
		Calendar d1Cal = fromDateboxId.getServerValue();
		Calendar d2Cal = toDateboxId.getServerValue();

		//*************************************************************************************
		
		boolean isFirstDone = false; 
		
		Date l1Date = null;
		Date l2Date = null;
		long diff;
		do{
//			dateList =  retailProSalesDao.getSalesDateBetWeenDates(listId, serverFromDateCal.toString(), serverToDateCal.toString(), stIndex, size);
			dateList =  retailProSalesDao.getSalesDateBetWeenDates(users.getUserId(), serverFromDateCal.toString(), serverToDateCal.toString(), stIndex, size);
			logger.info("dateList ::"+dateList.size());
			if(dateList == null || dateList.size() == 0) return;
			
			if(!isFirstDone) {
				
				//d1Cal = serverToDateCal;
				
				l1Date = (Date)dateList.get(0);
				
				
				diff = l1Date.getTime()-d1Cal.getTimeInMillis();
				logger.info("diff ::"+diff);
				
				if(diff > (24*60*60*1000)) {
					
					Row row = new Row();
					row.setParent(salesDateRowsId);
					
					salesCal.setTimeInMillis(l1Date.getTime());
					salesCal.set(Calendar.DATE, salesCal.get(Calendar.DATE)-1);
					
					Label lbl = new Label(
							MyCalendar.calendarToString(d1Cal, MyCalendar.FORMAT_STDATE) + " -- "+
							MyCalendar.calendarToString(salesCal, MyCalendar.FORMAT_STDATE));
					lbl.setParent(row);
				}
				else if(diff>0) {
					Row row = new Row();
					row.setParent(salesDateRowsId);
					Label lbl = new Label(MyCalendar.calendarToString(d1Cal, MyCalendar.FORMAT_STDATE));
					lbl.setParent(row);
				}
				
				isFirstDone = true;
				
			}
			
			d1Cal= Calendar.getInstance();
			d2Cal = Calendar.getInstance();
			
			Calendar d1CalInc = Calendar.getInstance();
			Calendar d2CalInc = Calendar.getInstance();
		
		for (int i = 0; i < dateList.size(); i++) {
			
			if(i+1==dateList.size()) continue;
			
			d1Cal.setTime((Date)dateList.get(i));
			d2Cal.setTime((Date)dateList.get(i+1));
			
			d1Cal.set(Calendar.HOUR, 0);
			d1Cal.set(Calendar.MINUTE, 0);
			d1Cal.set(Calendar.SECOND, 0);
			d1Cal.set(Calendar.MILLISECOND, 0);
			
			d2Cal.set(Calendar.HOUR, 0);
			d2Cal.set(Calendar.MINUTE, 0);
			d2Cal.set(Calendar.SECOND, 0);
			d2Cal.set(Calendar.MILLISECOND, 0);			

			
			d1CalInc.setTime(d1Cal.getTime());
			d2CalInc.setTime(d2Cal.getTime());
			
			d1CalInc.set(Calendar.DATE, d1Cal.get(Calendar.DATE)+1);
			d2CalInc.set(Calendar.DATE, d2Cal.get(Calendar.DATE)-1);
			
			int compareVal = d1CalInc.compareTo(d2CalInc);
			logger.info(d1CalInc.getTimeInMillis()+"=d1Inc=="+MyCalendar.calendarToString(d1CalInc, MyCalendar.FORMAT_DATETIME_STDATE));
			logger.info(d2CalInc.getTimeInMillis()+"=d2Inc=="+MyCalendar.calendarToString(d2CalInc, MyCalendar.FORMAT_DATETIME_STDATE) +" == "+compareVal);

			
			
			
			
			
			if(compareVal==0) {
				
				//tempRange = MyCalendar.calendarToString(d1CalInc, MyCalendar.FORMAT_STDATE);
				Row row = new Row();
				row.setParent(salesDateRowsId);
				Label lbl = new Label(MyCalendar.calendarToString(d1CalInc, MyCalendar.FORMAT_STDATE));
				lbl.setParent(row);
			}
			else if(compareVal < 0) {

				//tempRange = MyCalendar.calendarToString(d1CalInc, MyCalendar.FORMAT_STDATE) + " -- "+
				MyCalendar.calendarToString(d2CalInc, MyCalendar.FORMAT_STDATE);
				Row row = new Row();
				row.setParent(salesDateRowsId);
				Label lbl = new Label(
						MyCalendar.calendarToString(d1CalInc, MyCalendar.FORMAT_STDATE) + " -- "+
						MyCalendar.calendarToString(d2CalInc, MyCalendar.FORMAT_STDATE));
				lbl.setParent(row);
			} // else
	
		
			
		} // for
		if(d1Cal != null) serverFromDateCal = d1Cal;
		
		}while(dateList.size() == size);
		
		////////////
		l2Date = (Date)dateList.get(dateList.size()-1);
		d2Cal = toDateboxId.getServerValue();
		diff = d2Cal.getTimeInMillis() -(l2Date).getTime();
		if(diff > (24*60*60*1000)) {
			
			Row row = new Row();
			row.setParent(salesDateRowsId);
			
			salesCal.setTimeInMillis(l2Date.getTime());
			salesCal.set(Calendar.DATE, salesCal.get(Calendar.DATE)+1);
			
			Label lbl = new Label(
					MyCalendar.calendarToString(salesCal, MyCalendar.FORMAT_STDATE) + " -- "+
					MyCalendar.calendarToString(d2Cal, MyCalendar.FORMAT_STDATE));
			lbl.setParent(row);
		}
		else if(diff>0) {
			Row row = new Row();
			row.setParent(salesDateRowsId);
			Label lbl = new Label(MyCalendar.calendarToString(d2Cal, MyCalendar.FORMAT_STDATE));
			lbl.setParent(row);
		}
		
				
		
		
		//**************************************************************************************
		
	}
	
	private Longbox strtNumIntBox, endNumIntBox;
	public void getMissingReceiptNumbers(Long listId) {
		missingNumbersDivId.setVisible(true);
		
		if(strtNumIntBox.getValue() == null || endNumIntBox.getValue() == null) {
			
			MessageUtil.setMessage("Please provide proper date range. 'From' / 'To' should not be empty.", "color:red;");
			return;
			
		}
		
		long strtNum = strtNumIntBox.getValue().longValue();
		long endNum = endNumIntBox.getValue().longValue();
		
		if(strtNum > endNum) {
			
			MessageUtil.setMessage("'From' date cannot be after 'To' date.", "color:red;");
			return;
			
		}
		
		
		if(endNum-strtNum > 50000) {
			
			MessageUtil.setMessage("Range should not exceed 50000.", "color:red;");
			return;
			
		}
		
		Components.removeAllChildren(receiptNumRowsId);
		
		List dateList = null;
		int stIndex=0;
		int size=2000;
		Long receiptNum1 = null;
		Long receiptNum2 = null;
		boolean isFirstDone = false;
		Long receiptNum1Inc = null;
		Long receiptNum2Inc = null;
		Row row = null;
		do{
			
			
			dateList = retailProSalesDao.getMissingReceiptNumbers(users.getUserId(), strtNum+"", endNum+"", stIndex,size);
			
			if(dateList == null || dateList.size() == 0) return;
		//	logger.info("dateList ::"+dateList.size());
			if(!isFirstDone) {
				
				receiptNum2 = (Long)dateList.get(0);
				receiptNum1 = strtNum-1;
				
				receiptNum1Inc = receiptNum1.longValue()+1;
				receiptNum2Inc = receiptNum2.longValue()-1;
				String dispStr = "";
				
				if(receiptNum1Inc.longValue() < receiptNum2Inc.longValue()) {
					
					dispStr =  receiptNum1Inc.longValue()+"" + " -- "+
					receiptNum2Inc.longValue();
	
					
				}else if(receiptNum1Inc.longValue() == receiptNum2Inc.longValue()) {
					
					dispStr = ""+receiptNum1Inc.longValue();
					
					
				}
				
				row = new Row();
				row.setParent(receiptNumRowsId);
				Label lbl = new Label(dispStr);
				lbl.setParent(row);
				
				
				isFirstDone = true;
				
				
			}
			
			
			
			
			for (int i = 0; i < dateList.size(); i++) {
				
				if(i+1==dateList.size()) continue;
				
				Object receiptNumObj = dateList.get(i);
					
					receiptNum1 = (Long)receiptNumObj;
					receiptNum2 = (Long)dateList.get(i+1);
					
				//	logger.info(receiptNum1Inc+"=d1Inc==");
				//	logger.info(receiptNum1Inc+"=d2Inc==" );
					
					
					receiptNum1Inc = receiptNum1.longValue()+1;
					receiptNum2Inc = receiptNum2.longValue()-1;
					
					
					
					
					row = (Row)receiptNumRowsId.getLastChild();
					Label label = null;
					
					if(row == null) {
						
						row = new Row();
						row.setParent(receiptNumRowsId);
						label = new Label();
						label.setParent(row);
						
						
					}
					
					label = (Label)row.getLastChild();
					String ranges = label.getValue().trim();
					
					int commaCount = ranges.split(",").length-1;
					String tempRange = "";
					
					if(receiptNum1Inc.longValue() < receiptNum2Inc.longValue()) {
						
						tempRange =  receiptNum1Inc.longValue()+"" + " -- "+
										receiptNum2Inc.longValue();
						
						
						
					}
					
					else if(receiptNum1Inc.longValue() == receiptNum2Inc.longValue()) {
						
						tempRange = ""+receiptNum1Inc.longValue();
						
						
					}
					//****************************
					if(commaCount == 4 && tempRange.trim().length() > 0) {
						
						row = new Row();
						row.setParent(receiptNumRowsId);
						Label lbl = new Label(tempRange);
						lbl.setParent(row);
						} 
					
					else if(commaCount < 4 && tempRange.trim().length() > 0) {
						
							if(ranges.trim().length() > 0 && tempRange.trim().length() > 0) {
							ranges += ", "+tempRange;
							}else{
								
								ranges += tempRange;
							}
							
							label.setValue(ranges);
							label.setParent(row);
							
							
						}
				
					 
				
			}//for
			
			
			
			
			if(receiptNum1 != null ) strtNum = receiptNum1.longValue()+1;
			
		}while(dateList.size() == size);
		
		
		
		//for end num
		
		receiptNum2 = endNum+1;
		receiptNum1 = (Long)dateList.get(dateList.size()-1);;
		
		receiptNum1Inc = receiptNum1.longValue()+1;
		receiptNum2Inc = receiptNum2.longValue()-1;
		String dispStr = "";
		
		if(receiptNum1Inc.longValue() < receiptNum2Inc.longValue()) {
			
			dispStr =  receiptNum1Inc.longValue()+"" + " -- "+
			receiptNum2Inc.longValue();

			
		}else if(receiptNum1Inc.longValue() == receiptNum2Inc.longValue()) {
			
			dispStr = ""+receiptNum1Inc.longValue();
			
			
		}
		
		row = new Row();
		row.setParent(receiptNumRowsId);
		Label lbl = new Label(dispStr);
		lbl.setParent(row);
		
		
	}
	
	public void onClick$posContTbBtnId() {
		
		missingDatesDivId.setVisible(!missingDatesDivId.isVisible());
		missingNumbersDivId.setVisible(missingNumbersDivId.isVisible());
		
		boolean visibleFlag = missingDatesDivId.isVisible() || missingNumbersDivId.isVisible();
		
		String image = visibleFlag ? "/img/icons/icon_minus.png" : "/img/icons/icon_plus.png";
		posContTbBtnId.setImage(image);
		
		
		
		
	}
	
}
