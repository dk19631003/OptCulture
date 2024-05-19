package org.mq.marketer.campaign.controller.useradmin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.GenerateReportSetting;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.GenerateReportSettingDao;
import org.mq.marketer.campaign.dao.GenerateReportSettingDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;

public class FTPSettingsController extends GenericForwardComposer{
  
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Rows ftpSettingsRowsId;
	private Listbox timeListId,timezoneListId,TimesZonesLbId;
	private Users currentUser;
	
	private GenerateReportSettingDao   generateReportSettingDao;
	private GenerateReportSettingDaoForDML generateReportSettingDaoForDML;
	int serverTimeZoneValInt = Integer.parseInt(PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE));
   public	FTPSettingsController(){
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
     	PageUtil.setHeader("FTP Settings","",style,true);
     	currentUser = GetUser.getUserObj();
     	generateReportSettingDao=(GenerateReportSettingDao) SpringUtil.getBean(OCConstants.GENERATE_REPORT_SETTING_DAO);
     	generateReportSettingDaoForDML=(GenerateReportSettingDaoForDML) SpringUtil.getBean(OCConstants.GENERATE_REPORT_SETTING_DAO_FOR_DML);
     	
	}
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		setTime();
		prepareGrid();
		
	}
	
	public void setTime(){
		Listitem item=null;
		for(int i=1;i<=24;i++){
			if(i<=11)
			item=new Listitem(i+"AM",i);
			else if(i==12)
				item=new Listitem(i+"PM",i);
			else if(i==24)
				item=new Listitem(12+"AM",0);
			else
				item=new Listitem((i-12)+"PM",i);
			item.setParent(timeListId);
			timeListId.setSelectedIndex(0);
			
		}
		
	}
public void prepareGrid(){
	Row row = new Row();
	//Host Address
	Textbox tempTextbox = null; 
	tempTextbox = new Textbox();
	tempTextbox.setWidth("200px;");
	tempTextbox.setParent(row);
	
	//User Name
	tempTextbox = new Textbox();
	tempTextbox.setWidth("150px;");
	tempTextbox.setParent(row);
	
	//Password
	tempTextbox = new Textbox();
	tempTextbox.setType("password");
	tempTextbox.setWidth("150px;");
	tempTextbox.setParent(row);
	
	//Directory Location
	tempTextbox = new Textbox();
	tempTextbox.setWidth("250px;");
	tempTextbox.setParent(row);
	
	//Port
	tempTextbox = new Textbox();
	tempTextbox.setWidth("100px;");
	tempTextbox.setParent(row);
	
	row.setParent(ftpSettingsRowsId);
	defaultFTPSettings();
}
	
	public void onClick$saveBtnId(){
		logger.info("===inside save button "+timeListId.getSelectedItem().getValue());
		List chaildRowList =ftpSettingsRowsId.getChildren();
		if(chaildRowList == null || chaildRowList.size() == 0) {
			logger.debug("No data available for saving");
			MessageUtil.setMessage("No FTP settings found. Please provide FTP settings by clicking on 'Add' button.","color:red","TOP");
			return;
		}
		
		for (Object object : chaildRowList) {
			Row tempRow  =(Row)object;
			if(!ftpSettingValidation(tempRow)) {
				return;
			}
		}
		Listitem timeZone = TimesZonesLbId.getSelectedItem();
		if(timeZone == null || timeZone.getIndex() == 0) {
			MessageUtil.setMessage("Please select the client's time zone.", "color:red;");
			return;
		}
		Textbox tempTextbox = null;
		String msg=null;
		for (Object object : chaildRowList) {
			Row tempRow  =(Row)object;
			GenerateReportSetting generateReportSetting=	(GenerateReportSetting) tempRow.getAttribute("UserFTPSettings");
			if(generateReportSetting==null){
				generateReportSetting=new GenerateReportSetting();
				generateReportSetting.setOrgId(currentUser.getUserOrganization().getUserOrgId());
				generateReportSetting.setType(OCConstants.FTP_SETTINGS_TYPE_DR);
				generateReportSetting.setUserId(currentUser.getUserId());
				generateReportSetting.setUsername(currentUser.getUserName());
				generateReportSetting.setCreatedBy(currentUser.getUserId());
				generateReportSetting.setCreatedOn(MyCalendar.getNewCalendar());
				generateReportSetting.setFileFormat("json");
				generateReportSetting.setFreequency(OCConstants.LTY_SETTING_REPORT_FRQ_DAY);
				generateReportSetting.setEnable(true);
				msg="FTP Setting created successfully.";
			}
			else
				msg="FTP Setting changed successfully.";
			List chaildLblList = tempRow.getChildren();
			//host
		tempTextbox = (Textbox)chaildLblList.get(0);
		generateReportSetting.setHost(tempTextbox.getValue());
		
		//User Name
		tempTextbox = (Textbox)chaildLblList.get(1);
		generateReportSetting.setUsername(tempTextbox.getValue());
		
		
		//Password
		tempTextbox = (Textbox)chaildLblList.get(2);
		generateReportSetting.setPassword(tempTextbox.getValue());
		
		
		//DirectoryPath
		tempTextbox = (Textbox)chaildLblList.get(3);
		generateReportSetting.setTargetDir(tempTextbox.getValue());
		
		//Port
		tempTextbox = (Textbox)chaildLblList.get(4);
		generateReportSetting.setPort(Integer.parseInt(tempTextbox.getValue()));
		int timezoneDiffrenceMinutesInt = 0;
		String selectedTimezone = timeZone.getValue();
		logger.info(" Server time zone "+serverTimeZoneValInt+"   selected time zone "+selectedTimezone);
		if(selectedTimezone != null)  
			timezoneDiffrenceMinutesInt = Integer.parseInt(selectedTimezone);
		 timezoneDiffrenceMinutesInt = timezoneDiffrenceMinutesInt - serverTimeZoneValInt;
		 logger.info("diff. time zome  "+timezoneDiffrenceMinutesInt);
		 int selectedTime= timeListId.getSelectedItem().getValue();
		 //timezoneDiffrenceMinutesInt=timezoneDiffrenceMinutesInt+(selectedTime*60);
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MINUTE, -timezoneDiffrenceMinutesInt);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.SECOND, 0);
			logger.info("Total time "+cal.getTime());
			cal.add(Calendar.HOUR_OF_DAY,selectedTime);
			logger.info("after adding select time "+cal.getTime());
			 SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
			 String dt=format1.format(cal.getTime());
	      logger.info("  String "+dt+"    date "+cal.getTime());
	    generateReportSetting.setSelectedTimeZone(selectedTimezone);
		generateReportSetting.setGenerateAt(cal.getTime());
		generateReportSetting.setModifiedBy(currentUser.getUserId());
		generateReportSetting.setModifiedOn(MyCalendar.getNewCalendar());
		generateReportSetting.setTimeZoneName(timeZone.getLabel());
		generateReportSettingDaoForDML.saveOrUpdate(generateReportSetting);
		MessageUtil.setMessage(msg,"green", "top");
		}
		defaultFTPSettings();
	}
	private boolean ftpSettingValidation(Row tempRow) {

		
		 try {
			List rowList = tempRow.getChildren();
			 
			//Host Address
			 Textbox tempTextbox = null;
			 tempTextbox = (Textbox)rowList.get(0);
			 tempTextbox.setStyle("border:1px solid #7F9DB9;");
			 
			 if(tempTextbox.getValue().trim().equals("")) {
				 tempTextbox.setStyle("border:1px solid #DD7870;");
				 MessageUtil.setMessage("Please provide Host Address.","color:red","TOP");
				 return false;
			 }
			
			//UserName
			 tempTextbox = (Textbox)rowList.get(1);
			 tempTextbox.setStyle("border:1px solid #7F9DB9;");
			 
			 if(tempTextbox.getValue().trim().equals("")) {
				 tempTextbox.setStyle("border:1px solid #DD7870;");
				 MessageUtil.setMessage("Please provide username.","color:red","TOP");
				 return false;
			 }
			 
			 //Password
			 tempTextbox = (Textbox)rowList.get(2);
			 tempTextbox.setStyle("border:1px solid #7F9DB9;");
			 
			 if(tempTextbox.getValue().trim().equals("")) {
				 tempTextbox.setStyle("border:1px solid #DD7870;");
				 MessageUtil.setMessage("Please provide password.","color:red","TOP");
				 return false;
			 }
			
			 //Directory Path
			 tempTextbox = (Textbox)rowList.get(3);
			 tempTextbox.setStyle("border:1px solid #7F9DB9;");
			 
			 if(tempTextbox.getValue().trim().equals("")) {
				 tempTextbox.setStyle("border:1px solid #DD7870;");
				 MessageUtil.setMessage("Please provide Directory Path.","color:red","TOP");
				 return false;
			 }
			 
			 //File Format
			 tempTextbox = (Textbox)rowList.get(4);
			 tempTextbox.setStyle("border:1px solid #7F9DB9;");
			 
			 if(tempTextbox.getValue().trim().equals("")) {
				 tempTextbox.setStyle("border:1px solid #DD7870;");
				 MessageUtil.setMessage("Please provide port.","color:red","TOP");
				 return false;
			 }
			 if(!tempTextbox.getValue().trim().matches("-?\\d+(\\.\\d+)?")){
				 tempTextbox.setStyle("border:1px solid #DD7870;");
				 MessageUtil.setMessage("Port should be numeric.","color:red","TOP");
				 return false;
			 }
			return true;
		} catch (Exception e) {
			logger.error("Exception  ::", e);
			return false;
		}
	} 
	//ftpSettingValidation
	private void defaultFTPSettings() {
		try {
		 
			List<GenerateReportSetting> userFTPSettingsList = generateReportSettingDao.findBy(currentUser.getUserOrganization().getUserOrgId());
			if(userFTPSettingsList == null || userFTPSettingsList.size() ==0) return;
			Components.removeAllChildren(ftpSettingsRowsId);
			Row row = new Row();
			//Host Address
			Textbox tempTextbox = null;
			for (GenerateReportSetting userFTPSettings : userFTPSettingsList) {
				//Host Address
				tempTextbox = new Textbox(userFTPSettings.getHost().trim());
				tempTextbox.setWidth("200px;");
				tempTextbox.setParent(row);
				
				//User Name
				tempTextbox = new Textbox(userFTPSettings.getUsername().trim());
				tempTextbox.setWidth("150px;");
				tempTextbox.setParent(row);
				
				//Password
				tempTextbox = new Textbox(userFTPSettings.getPassword().trim());
				tempTextbox.setType("password");
				tempTextbox.setWidth("150px;");
				tempTextbox.setParent(row);
				
				//Directory Location
				tempTextbox = new Textbox(userFTPSettings.getTargetDir().trim());
				tempTextbox.setWidth("250px;");
				tempTextbox.setParent(row);
				
				//Port
				tempTextbox = new Textbox(""+userFTPSettings.getPort());
				tempTextbox.setWidth("100px;");
				tempTextbox.setParent(row);
				
				Image img ;
				Hbox hbox = null;
				if(userFTPSettings.isEnable()){
					hbox=new Hbox();
					img= new Image("/img/pause_icn.png");
					img.setStyle("margin-right:5px;cursor:pointer;");
					img.setTooltiptext("Pause Settings");
					img.setAttribute("imageEventName", "inactive");
					img.setAttribute("FTPSettings", userFTPSettings);
					img.addEventListener("onClick",this);
					Space space = new Space();
					space.setParent(hbox);
					img.setParent(hbox);
					
				}
				else {
					hbox=new Hbox();
					img= new Image("/img/play_icn.png");
					img.setStyle("margin-right:5px;cursor:pointer;");
					img.setTooltiptext("Activate Settings");
					img.setAttribute("imageEventName", "active");
					img.setAttribute("FTPSettings", userFTPSettings);
					img.addEventListener("onClick",this);
					Space space = new Space();
					space.setParent(hbox);
					img.setParent(hbox);
					
				}
				hbox.setParent(row);
				row.setParent(ftpSettingsRowsId);
				row.setAttribute("UserFTPSettings", userFTPSettings);
				List<Listitem> items=timeListId.getItems();
				Date date=userFTPSettings.getGenerateAt();
				//DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
				//String strdate=dateFormat.format(date);
				//String [] arrtime=strdate.split(":");
				int selectedTimeZone=Integer.parseInt(userFTPSettings.getSelectedTimeZone());
				selectedTimeZone=(selectedTimeZone- serverTimeZoneValInt);
				logger.info(" selected time zone with server time zone   "+selectedTimeZone);
				int seletedTime=0;
				seletedTime=date.getHours()*60;
				seletedTime=seletedTime+date.getMinutes();
				seletedTime=seletedTime+selectedTimeZone;
				logger.info("time in m:  "+seletedTime);
				seletedTime=seletedTime/60;
				logger.info(" finally selected time in hours "+seletedTime);
				if(seletedTime==24)
					seletedTime=0;
				if(seletedTime>24)
					seletedTime=seletedTime-24;
				for(Listitem item:items){
					int time=item.getValue();
					if(time==seletedTime){
						timeListId.setSelectedItem(item);	
						break;
					}
					
				}
			List<Listitem> listitem=	TimesZonesLbId.getItems();
			for(Listitem item:listitem){
				String zone=item.getValue();
				if(zone!=null)
					if(zone.equals(userFTPSettings.getSelectedTimeZone())&& item.getLabel().equalsIgnoreCase(userFTPSettings.getTimeZoneName())){
						TimesZonesLbId.setSelectedItem(item);
						break;
					}
			   }
			}
			
			}
		catch(Exception e){
			logger.error("Exception "+e);
		}
	}
	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
		if (event.getTarget() instanceof Image) {
			logger.info("img clicked ...");
			Image img = (Image) event.getTarget();
			GenerateReportSetting generateReportSetting=(GenerateReportSetting) img.getAttribute("FTPSettings");
			if(generateReportSetting.isEnable()){
				int confirm = Messagebox.show("Are you sure you want to change the setting?", "Prompt", 
						 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm != 1) return;
				generateReportSetting.setEnable(false);
				img.setSrc("/img/play_icn.png");
				img.setTooltiptext("Activate Settings");
				
			}
			else{
				int confirm = Messagebox.show("Are you sure you want to change the setting?", "Prompt", 
						 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm != 1) return;
			
				generateReportSetting.setEnable(true);
				img.setSrc("/img/pause_icn.png");
				img.setTooltiptext("Pause Settings");
			 }
			generateReportSettingDaoForDML.saveOrUpdate(generateReportSetting);
			defaultFTPSettings();
			}
		}
}

