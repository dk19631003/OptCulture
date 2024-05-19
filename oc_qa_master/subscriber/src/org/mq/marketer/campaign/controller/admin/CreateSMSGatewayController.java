package org.mq.marketer.campaign.controller.admin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.mapping.Array;
import org.mq.marketer.campaign.beans.CountryCodes;
import org.mq.marketer.campaign.beans.CountryReceivingNumbers;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.UserSMSGateway;
import org.mq.marketer.campaign.beans.UserSMSSenderId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CountryCodeDao;
import org.mq.marketer.campaign.dao.CountryReceivingNumbersDao;
import org.mq.marketer.campaign.dao.CountryReceivingNumbersDaoForDML;
import org.mq.marketer.campaign.dao.OCSMSGatewayDao;
import org.mq.marketer.campaign.dao.OCSMSGatewayDaoForDML;
import org.mq.marketer.campaign.dao.UserSMSGatewayDao;
import org.mq.marketer.campaign.dao.UserSMSSenderIdDao;
import org.mq.marketer.campaign.dao.UserSMSSenderIdDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.event.PagingEvent;

public class CreateSMSGatewayController extends GenericForwardComposer{ 
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Groupbox craeteGatewayGBId;
	private Rows smsGatwaysRowsId;
	private Paging gatewaysPagingId;
	private Listbox gatewaysPerPageLBId ,countryLbId ,accTypeLbId , modeLbId ,countryLBId ,typeLBId;
	private Textbox userIdTxtboxId ,portTxtboxId ,sysPwdTxtboxId ,sysIDTxtboxId ,pwdTxtboxId ,ipTxtboxId,
					pullReportsTxtId,postpaidTxtId ,msgRecvNumTxtId,sysTypeTxtboxId , senderIdTxtboxId ,apiIdTxtboxId,twoWayTxtId,peId;//gatwayTxtboxId
	private Checkbox pullReportsChkId ,postpaidChkId,enableMultiThreadChkId,enableCheckSessionAliveChkId,twoWayChkId;
	private Div smppDivId,httpRelatedChkbxDivId,smppRelatedChkbxDivId,twoWayDivId ;
	private Button saveBtnId , cancelBtnId;
	private Combobox gatwaycmbboxId , msgRecvNumCmbId;
	private Div pullReportsDivId,postpaidDivId;
	
	private OCSMSGatewayDao ocsmsGatewayDao;
	private OCSMSGatewayDaoForDML ocsmsGatewayDaoForDML;
	private UserSMSSenderIdDao userSMSSenderIdDao;
	private UserSMSGatewayDao userSMSGatewayDao;
	private UsersDao usersDao;
	private UserSMSSenderIdDaoForDML userSMSSenderIdDaoForDML;
	private Users currentUser;
	private OCSMSGateway ocsmsGateway;
	private String type;
	 private TimeZone clientTimeZone;
	 private Desktop desktopScope;
	 private Properties countryCodes;
	 private CountryCodeDao countryCodeDao;
	 private   Map<String, String> genericCampTypeMap;
	 
	 
	 private CountryReceivingNumbersDao countryReceivingNumbersDao;
	 private CountryReceivingNumbersDaoForDML countryReceivingNumbersDaoForDML;
	
	public   Map<String, String> usCampTypeMap = new HashMap<String, String>();
 	public   Map<String, String> indiaCampTypeMap = new HashMap<String, String>();
 	public   Map<String, String> uaeCampTypeMap = new HashMap<String, String>();
 	public   Map<String, String> pakistanCampTypeMap = new HashMap<String, String>();
 	public   Map<String,Map<String, String>> campTypeMap = new HashMap<String,Map<String, String>>();
 	
 	
 	public   Map<String, String> countryMap = new HashMap<String, String>();
 	public   Map<String, String> countryCarrierMap = new HashMap<String, String>();
 	
 	
 	public   Map<String, String> typeMap = new HashMap<String, String>();
 	
	
	public CreateSMSGatewayController(){
		desktopScope = Executions.getCurrent().getDesktop();
		 ocsmsGatewayDao = (OCSMSGatewayDao)SpringUtil.getBean("OCSMSGatewayDao");
		 ocsmsGatewayDaoForDML = (OCSMSGatewayDaoForDML)SpringUtil.getBean("OCSMSGatewayDaoForDML");
		 countryCodeDao = (CountryCodeDao)SpringUtil.getBean("countryCodeDao");
		 userSMSSenderIdDao = (UserSMSSenderIdDao)SpringUtil.getBean("userSMSSenderIdDao");
		 userSMSSenderIdDaoForDML = (UserSMSSenderIdDaoForDML)SpringUtil.getBean("userSMSSenderIdDaoForDML");
		 userSMSGatewayDao = (UserSMSGatewayDao)SpringUtil.getBean("userSMSGatewayDao");
		 usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		currentUser = GetUser.getUserObj();
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("SMS Gateways ",Constants.STRING_NILL,style,true);
		clientTimeZone = (TimeZone)desktopScope.getAttribute("clientTimeZone");
		
		countryReceivingNumbersDao =(CountryReceivingNumbersDao)SpringUtil.getBean("countryReceivingNumbersDao");
		countryReceivingNumbersDaoForDML =(CountryReceivingNumbersDaoForDML)SpringUtil.getBean("countryReceivingNumbersDaoForDML");
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		countryCodes = countryCodeDao.findAllCountryCodes();
		defaultSettings();
		defaultNumberSettings();
	}
	
	
	public void onSelect$accTypeLbId() {
		
		try {
			Components.removeAllChildren(gatwaycmbboxId);
			gatwaycmbboxId.setValue(Constants.STRING_NILL);
			
			String countryType=countryLbId.getSelectedItem().getLabel().trim();
			List<String> retList = ocsmsGatewayDao.findGateways(countryType, accTypeLbId.getSelectedItem().getValue().toString());
			
			if(retList != null && retList.size() > 0) {
				
				Comboitem item = null;
				for (String gatewayName : retList) {
					item = new Comboitem();
					item.setLabel(gatewayName);
					gatwaycmbboxId.appendChild(item);
				}
				
			}
			if(countryType.equals(Constants.SMS_COUNTRY_INDIA)){
				
			
				smppDivId.setVisible(true);
				sysIDTxtboxId.setValue(Constants.STRING_NILL);
				sysPwdTxtboxId.setValue(Constants.STRING_NILL);
				portTxtboxId.setValue(Constants.STRING_NILL);
				ipTxtboxId.setValue(Constants.STRING_NILL);
				
			}else if(countryType.equals(Constants.SMS_COUNTRY_INDIA)){
				
				smppDivId.setVisible(false);
				
			}
			
			userIdTxtboxId.setValue(Constants.STRING_NILL);
			pwdTxtboxId.setValue(Constants.STRING_NILL);
			
			senderIdTxtboxId.setValue(Constants.STRING_NILL);
			
			apiIdTxtboxId.setValue(Constants.STRING_NILL);
			
			//pullReportsChkId.setDisabled(true)	;
			if(countryType.equals(Constants.SMS_COUNTRY_INDIA) &&  accTypeLbId.getSelectedItem().getLabel().trim().equals(Constants.SMS_TYPE_NAME_TRANSACTIONAL)){
				pullReportsChkId.setChecked(true);
				pullReportsDivId.setVisible(pullReportsChkId.isChecked());
				
			}else if(countryType.equals(Constants.SMS_COUNTRY_UAE) &&  accTypeLbId.getSelectedItem().getLabel().trim().equals(Constants.SMS_TYPE_NAME_TRANSACTIONAL)){
				pullReportsChkId.setChecked(true);
				pullReportsDivId.setVisible(pullReportsChkId.isChecked());
			}else{
				pullReportsChkId.setChecked(false);
				pullReportsDivId.setVisible(pullReportsChkId.isChecked());
			}
			
			if(!postpaidChkId.isChecked())postpaidDivId.setVisible(!postpaidChkId.isChecked());
		
		} catch (Exception e) {
			logger.error("Exception ", e);
		}
		
		
	}
	
	public void defaultSettings(){
		
		type = (String)desktopScope.removeAttribute("ocsmsGatewayType");
		ocsmsGateway = (OCSMSGateway)desktopScope.removeAttribute("ocsmsGateway");
		
		
		try {
			int totalSize = ocsmsGatewayDao.getTotalCountOfAllGateways(); 
			
			gatewaysPagingId.setTotalSize(totalSize);
			gatewaysPagingId.setActivePage(0);
			gatewaysPagingId.addEventListener("onPaging", this);
			
			
			redraw(0, gatewaysPagingId.getPageSize());
			
			defaultCreateGatewaySettings();
			//onSelect$countryLbId();
			
		} catch (WrongValueException e) {
			logger.error("Exception" ,e);
			//logger.error("Exception :::",e);
		} catch (Exception e) {
			logger.error("Exception" ,e);
			//logger.error("Exception :::",e);
		}
		
		
		
	}
	
	public void redraw(int start ,int endIndex){
		
		try {
			List<OCSMSGateway> gatewaysList = null;
			 
			 Components.removeAllChildren(smsGatwaysRowsId);
			 
			 gatewaysList = ocsmsGatewayDao.findAllGateways(start, endIndex);
			 
				 for (OCSMSGateway ocsmsGateway : gatewaysList) {
					 
					 Row newRow = new Row();
					 
					 newRow.setParent(smsGatwaysRowsId);
					 
					 
					 
					newRow.appendChild(new Label(ocsmsGateway.getCountryName()));
					
					newRow.appendChild(new Label(ocsmsGateway.getGatewayName()));
					
					String accTypeStr=ocsmsGateway.getAccountType();
					
					String accType=getAccType(accTypeStr);
					
					newRow.appendChild(new Label(accType));
					
					newRow.appendChild(new Label(ocsmsGateway.getUserId()));
					
					
					newRow.appendChild(new Label(MyCalendar.calendarToString(ocsmsGateway.getCreatedDate(), MyCalendar.FORMAT_MDATEONLY ,clientTimeZone)));
					
					Hbox hbox = new Hbox();
					
					Image editImg = new Image("/img/email_edit.gif");
					editImg.setTooltiptext("Edit");
					editImg.setStyle("cursor:pointer;margin-right:5px;");
					editImg.addEventListener("onClick", this);
					editImg.setAttribute("type", "Edit");
					editImg.setParent(hbox);
					
					hbox.setParent(newRow);
					
					newRow.setValue(ocsmsGateway);
					 
					
				}// for
				 
			 
				
		} catch (Exception e) {
			logger.error("Exception",e);
			//logger.error("Exception :::",e);
		}

		 
		 
		 
		
	}// redraw()
	
	public String getAccType(String accType){
		
		String AccountType =Constants.STRING_NILL;
		
		if(accType.equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL)){
			
			AccountType=Constants.SMS_TYPE_NAME_TRANSACTIONAL;
		}else if(accType.equalsIgnoreCase(Constants.SMS_TYPE_PROMOTIONAL)){
			
			AccountType=Constants.SMS_TYPE_NAME_PROMOTIONAL;
		}else if(accType.equalsIgnoreCase(Constants.SMS_TYPE_2_WAY)){
			
			AccountType=Constants.SMS_TYPE_NAME_2_WAY;
		}else if(accType.equalsIgnoreCase(Constants.SMS_TYPE_OUTBOUND)){
			
			AccountType=Constants.SMS_TYPE_NAME_OUTBOUND;
		} else if(accType.equalsIgnoreCase(Constants.SMS_SENDING_TYPE_OPTIN)){
			
			AccountType=Constants.SMS_TYPE_NAME_OPTIN;
		}
		
		return AccountType;
	}
	
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
		
		if(event.getTarget() instanceof Image){
			
			Image img = (Image)event.getTarget();
			Row tempRow = ((Row)((Hbox)img.getParent()).getParent());
			OCSMSGateway ocsmsGateway = (OCSMSGateway)tempRow.getValue();
			
			if(ocsmsGateway == null) return;
			String evtType = (String)img.getAttribute("type");
			
			if(evtType.equalsIgnoreCase("Edit")) {
				
				desktopScope.setAttribute("ocsmsGatewayType", "edit");
				desktopScope.setAttribute("ocsmsGateway", ocsmsGateway);
				//newGatewayDivID.setVisible(true);
				craeteGatewayGBId.setVisible(true);
				gatewaySettings();
			}
			
		}else if(event.getTarget() instanceof Paging ){
			Paging paging = (Paging)event.getTarget();
			
			int desiredPage = paging.getActivePage();
			
			
			
			
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			redraw(ofs, (byte) pagingEvent.getPageable().getPageSize());
			
		}
	}//onEvent()
	public void gatewaySettings(){
		
		type = (String)desktopScope.getAttribute("ocsmsGatewayType");
		ocsmsGateway = (OCSMSGateway)desktopScope.getAttribute("ocsmsGateway");
		
		if(type != null && ocsmsGateway != null){
			
			String countryStr= ocsmsGateway.getCountryName().trim();
			
			for (Listitem item : countryLbId.getItems()) {
				
				if(countryStr != null){
					
					if( item.getLabel() != null && item.getLabel().equalsIgnoreCase(countryStr)){
						
						item.setSelected(true);
						onSelect$countryLbId();
						break;
					}
					
				}
				
			}// for
			String accTypeStr= ocsmsGateway.getAccountType().trim();
			String accType=getAccType(accTypeStr);
			
			for (Listitem item : accTypeLbId.getItems()) {
				
				if(accType != null){
					
					if( item.getLabel() != null && item.getLabel().equalsIgnoreCase(accType)){
						
						item.setSelected(true);
						onSelect$accTypeLbId();
						break;
					}
					
				}
				
			}// for
			
			String gateway=ocsmsGateway.getGatewayName().trim();
			
			
			
			gatwaycmbboxId.setValue(gateway);
			
			userIdTxtboxId.setValue(ocsmsGateway.getUserId().trim());
			
			pwdTxtboxId.setValue(ocsmsGateway.getPwd().trim());
			
			
			if(	ocsmsGateway.getAPIId() != null)apiIdTxtboxId.setValue(ocsmsGateway.getAPIId().trim());
			
			
			if(ocsmsGateway.getSenderId() != null )senderIdTxtboxId.setValue(ocsmsGateway.getSenderId().trim());
			
			String modeStr= ocsmsGateway.getMode().trim();
			
			for (Listitem item : modeLbId.getItems()) {
				
				if(modeStr != null){
					
					//smppDivId.setVisible(true);
					
					if( item.getLabel() != null && item.getLabel().equalsIgnoreCase(modeStr)){
						
						item.setSelected(true);
						if(modeStr.equalsIgnoreCase("HTTP")){
							enableMultiThreadChkId.setChecked(ocsmsGateway.isEnableMultiThreadSub());
						}else if(modeStr.equalsIgnoreCase("SMPP")){
							enableCheckSessionAliveChkId.setChecked(ocsmsGateway.isEnableSessionAlive());
						}
						onSelect$modeLbId();
						break;
					}
					
				}
				
			}// for
			
			if(countryStr.equals(Constants.SMS_COUNTRY_INDIA)) {
				
				smppDivId.setVisible(true);
				
				String sysIdStr= ocsmsGateway.getSystemId();
				if(sysIdStr != null){
					sysIDTxtboxId.setValue(sysIdStr);
				}
				
				String sysPwdStr= ocsmsGateway.getSystemPwd();
				if(sysPwdStr != null){
					sysPwdTxtboxId.setValue(sysPwdStr);
				}
				
				
				
				String systemTypeStr=ocsmsGateway.getSystemType();
				
				if(systemTypeStr != null){
				 
					sysTypeTxtboxId.setValue(systemTypeStr);
				}
				
				String ipStr= ocsmsGateway.getIp();
				if(sysPwdStr != null){
					ipTxtboxId.setValue(ipStr);
				}
				
				String portStr= ocsmsGateway.getPort();
				if(portStr != null){
					portTxtboxId.setValue(portStr);
				}
				
				
			}else if(countryStr.equals(Constants.SMS_COUNTRY_US) || countryStr.equals(Constants.SMS_COUNTRY_CANADA)
						|| countryStr.equals(Constants.SMS_COUNTRY_SA) || countryStr.equals(Constants.SMS_COUNTRY_SINGAPORE)){
				smppDivId.setVisible(false);
			}else if(countryStr.equals(Constants.SMS_COUNTRY_UAE)){
				
				smppDivId.setVisible(true);
				
				String sysIdStr= ocsmsGateway.getSystemId();
				if(sysIdStr != null){
					sysIDTxtboxId.setValue(sysIdStr);
				}
				
				String sysPwdStr= ocsmsGateway.getSystemPwd();
				if(sysPwdStr != null){
					sysPwdTxtboxId.setValue(sysPwdStr);
				}
				
				
				
				String systemTypeStr=ocsmsGateway.getSystemType();
				
				if(systemTypeStr != null){
				 
					sysTypeTxtboxId.setValue(systemTypeStr);
				}
				
				String ipStr= ocsmsGateway.getIp();
				if(sysPwdStr != null){
					ipTxtboxId.setValue(ipStr);
				}
				
				String portStr= ocsmsGateway.getPort();
				if(portStr != null){
					portTxtboxId.setValue(portStr);
				}
			}else{// for countries other than mentioned ones. 
				
				if(countryMap.containsKey(countryStr)){

					String httpOrSmpp= ocsmsGateway.getMode().trim();
					smppDivId.setVisible(httpOrSmpp.equalsIgnoreCase("SMPP"));
					
					String sysIdStr= ocsmsGateway.getSystemId();
					if(sysIdStr != null){
						sysIDTxtboxId.setValue(sysIdStr);
					}
					
					String sysPwdStr= ocsmsGateway.getSystemPwd();
					if(sysPwdStr != null){
						sysPwdTxtboxId.setValue(sysPwdStr);
					}
					
					
					
					String systemTypeStr=ocsmsGateway.getSystemType();
					
					if(systemTypeStr != null){
					 
						sysTypeTxtboxId.setValue(systemTypeStr);
					}
					
					String ipStr= ocsmsGateway.getIp();
					if(sysPwdStr != null){
						ipTxtboxId.setValue(ipStr);
					}
					
					String portStr= ocsmsGateway.getPort();
					if(portStr != null){
						portTxtboxId.setValue(portStr);
					}
				
					
				}
			}
			
			
			
			
			pullReportsChkId.setChecked(ocsmsGateway.isPullReports()) ;
			
			
			if(ocsmsGateway.isPullReports()){
				
				pullReportsDivId.setVisible(true);
				pullReportsTxtId.setText(ocsmsGateway.getPullReportsURL() != null ? ocsmsGateway.getPullReportsURL() : Constants.STRING_NILL);
			}
			
			
			postpaidChkId.setChecked(ocsmsGateway.isPostPaid());
			if(!ocsmsGateway.isPostPaid()){
				
				postpaidDivId.setVisible(!ocsmsGateway.isPostPaid());
				
				postpaidTxtId.setText(ocsmsGateway.getPostpaidBalURL() != null ? ocsmsGateway.getPostpaidBalURL() : Constants.STRING_NILL);
			}
			
			if(ocsmsGateway.getTwoWaySenderID() != null){
				twoWayChkId.setChecked(true);
				twoWayTxtId.setText(ocsmsGateway.getTwoWaySenderID());
				twoWayDivId.setVisible(true);
			}else{
				twoWayChkId.setChecked(false);
				twoWayTxtId.setText("");
				twoWayDivId.setVisible(false);
			}
				
			if(countryStr.equals(Constants.SMS_COUNTRY_INDIA)) {
				//peIdRow.setVisible(true);
				peId.setText(ocsmsGateway.getPrincipalEntityId()!=null?ocsmsGateway.getPrincipalEntityId():"");
			}
			
			if(type != null && type.equals("edit")) {
				
				
				saveBtnId.setLabel("Update");
				saveBtnId.setVisible(true);
				cancelBtnId.setVisible(false);
				
				
			}
			
		}// if
		
	}
	
	
	public void onSelect$gatewaysPerPageLBId(){
		
		try {
			int count = Integer.parseInt(gatewaysPerPageLBId.getSelectedItem().getLabel());
			
		
			gatewaysPagingId.setPageSize(count);
			redraw(0, count);
			
			//System.gc();
			
		} catch (Exception e) {
			logger.error("Exception" , e);
		}
		
	}//onSelect$gatewaysPerPageLBId
	
	public void onCheck$postpaidChkId(){
		
		postpaidDivId.setVisible(!postpaidChkId.isChecked());
	}
	public void onCheck$pullReportsChkId(){
		
		pullReportsDivId.setVisible(pullReportsChkId.isChecked());
		
	}
	
	public void onClick$addGatewayTbId(){
		
		desktopScope.removeAttribute("ocsmsGatewayType");
		desktopScope.removeAttribute("ocsmsGateway");
		craeteGatewayGBId.setVisible(true);
		defaultCreateGatewaySettings();
		
		
	}
	
	public void defaultCreateGatewaySettings(){
		
		
		indiaCampTypeMap.put(Constants.SMS_TYPE_NAME_TRANSACTIONAL, Constants.SMS_TYPE_TRANSACTIONAL);
		indiaCampTypeMap.put(Constants.SMS_TYPE_NAME_PROMOTIONAL, Constants.SMS_TYPE_PROMOTIONAL);
 		indiaCampTypeMap.put(Constants.SMS_TYPE_NAME_OPTIN, Constants.SMS_SENDING_TYPE_OPTIN);
 		
 		pakistanCampTypeMap.put(Constants.SMS_TYPE_NAME_PROMOTIONAL, Constants.SMS_TYPE_PROMOTIONAL);
 		
 		usCampTypeMap.put(Constants.SMS_TYPE_NAME_OUTBOUND, Constants.SMS_TYPE_OUTBOUND);
 		
 		uaeCampTypeMap.put(Constants.SMS_TYPE_NAME_PROMOTIONAL, Constants.SMS_TYPE_PROMOTIONAL);
 		uaeCampTypeMap.put(Constants.SMS_TYPE_NAME_TRANSACTIONAL, Constants.SMS_TYPE_TRANSACTIONAL);
 		
 		campTypeMap.put(Constants.SMS_COUNTRY_INDIA,indiaCampTypeMap);
 		
 		campTypeMap.put(Constants.SMS_COUNTRY_US, usCampTypeMap);
 		campTypeMap.put(Constants.SMS_COUNTRY_PAKISTAN, pakistanCampTypeMap);
 		campTypeMap.put(Constants.SMS_COUNTRY_UAE, uaeCampTypeMap);
 		campTypeMap.put(Constants.SMS_COUNTRY_CANADA, usCampTypeMap);
 		campTypeMap.put(Constants.SMS_COUNTRY_SA, usCampTypeMap);
 		campTypeMap.put(Constants.SMS_COUNTRY_SINGAPORE, usCampTypeMap);//APP-4688
 		
 		
 		
 		
 		countryMap.put(Constants.SMS_COUNTRY_INDIA, Constants.SMS_COUNTRY_INDIA);
 		countryMap.put(Constants.SMS_COUNTRY_US, Constants.SMS_COUNTRY_US);
 		countryMap.put(Constants.SMS_COUNTRY_PAKISTAN, Constants.SMS_COUNTRY_PAKISTAN);
 		countryMap.put(Constants.SMS_COUNTRY_UAE, Constants.SMS_COUNTRY_UAE);
 		countryMap.put(Constants.SMS_COUNTRY_CANADA, Constants.SMS_COUNTRY_CANADA);
 		countryMap.put(Constants.SMS_COUNTRY_SA, Constants.SMS_COUNTRY_SA);
 		countryMap.put(Constants.SMS_COUNTRY_SINGAPORE, Constants.SMS_COUNTRY_SINGAPORE);//APP-4688
 		
 		Enumeration enm = countryCodes.propertyNames(); 
		while(enm.hasMoreElements()){
			String currCountry = (String)enm.nextElement();
			countryMap.put(currCountry , currCountry);
			
			campTypeMap.put(currCountry, uaeCampTypeMap);//setting for each new(30th june 2017 onwards any new country addition) as with PR(uaeCampTypeMap contains PR).
		}
 		
 		
 		Set<String> countrySet = countryMap.keySet();
 		
 		TreeSet<String> countrySortedSet = new TreeSet<String>();
		countrySortedSet.addAll(countrySet);
		
		Components.removeAllChildren(countryLbId);
		
		for (String country : countrySortedSet) {
			
			
			Listitem item= new Listitem(country);
			
			item.setParent(countryLbId);
			item.setValue(countryMap.get(country));
		
		}
		
		if(countryLbId.getItemCount() > 0){
			countryLbId.setSelectedIndex(0);
			onSelect$countryLbId();
		}

 }//defaultCreateGatewaySettings
	
	public void onSelect$countryLbId(){
		type = (String)desktopScope.removeAttribute("ocsmsGatewayType");
		ocsmsGateway = (OCSMSGateway)desktopScope.removeAttribute("ocsmsGateway");
		
		
				
		try {
			
			String country = countryLbId.getSelectedItem().getLabel().trim();
			if(country.equals(Constants.SMS_COUNTRY_INDIA)){
				accTypeLbId.clearSelection();
				gatwaycmbboxId.setValue(Constants.STRING_NILL);
				Components.removeAllChildren(accTypeLbId);
				Components.removeAllChildren(gatwaycmbboxId);
				
				Set<String> accTypeSetIND = indiaCampTypeMap.keySet();
				
				for (String accTypeIND : accTypeSetIND) {
					
					Listitem accTypeItem = new Listitem(accTypeIND);
					
					accTypeItem.setParent(accTypeLbId);
					
					accTypeItem.setValue(indiaCampTypeMap.get(accTypeIND));
				}
				
				if(accTypeLbId.getItemCount() > 0) {
					accTypeLbId.setSelectedIndex(0);
					onSelect$accTypeLbId();
				}
				for (Listitem item : modeLbId.getItems()) {
					
					if(item.getLabel().equalsIgnoreCase(Constants.SMS_GATEWAY_MODE_SMPP)){
						
						item.setSelected(true);
						onSelect$modeLbId();
						//modeLbId.setDisabled(true);
						break;
						
					}
				}
				smppDivId.setVisible(true);
				sysIDTxtboxId.setValue(Constants.STRING_NILL);
				sysPwdTxtboxId.setValue(Constants.STRING_NILL);
				portTxtboxId.setValue(Constants.STRING_NILL);
				ipTxtboxId.setValue(Constants.STRING_NILL);
				sysTypeTxtboxId.setValue(Constants.STRING_NILL);
				 postpaidTxtId.setValue(Constants.STRING_NILL);
				 ////////////////////
				twoWayChkId.setChecked(false);
				twoWayTxtId.setText("");
				twoWayDivId.setVisible(false);
				pullReportsTxtId.setValue(Constants.STRING_NILL);
				peId.setValue(Constants.STRING_NILL);
				
			}else if(country.equals(Constants.SMS_COUNTRY_US) || country.equals(Constants.SMS_COUNTRY_CANADA) || 
					country.equals(Constants.SMS_COUNTRY_SA) || country.equals(Constants.SMS_COUNTRY_SINGAPORE) ||
					country.equals(Constants.SMS_COUNTRY_PAKISTAN)){
				
				gatwaycmbboxId.setValue(Constants.STRING_NILL);
				accTypeLbId.clearSelection();
				Components.removeAllChildren(accTypeLbId);
				//modeLbId.clearSelection();
				
				Set<String> accTypeSetUS = country.equals(Constants.SMS_COUNTRY_US) || country.equals(Constants.SMS_COUNTRY_CANADA) || 
						country.equals(Constants.SMS_COUNTRY_SA ) || country.equals(Constants.SMS_COUNTRY_SINGAPORE) ? 
						usCampTypeMap.keySet() : (country.equals(Constants.SMS_COUNTRY_PAKISTAN) ? 
								pakistanCampTypeMap.keySet() : null);
				Map<String, String> selMap = country.equals(Constants.SMS_COUNTRY_US) || country.equals(Constants.SMS_COUNTRY_CANADA) || 
						country.equals(Constants.SMS_COUNTRY_SA) || country.equals(Constants.SMS_COUNTRY_SINGAPORE) ? 
						usCampTypeMap : (country.equals(Constants.SMS_COUNTRY_PAKISTAN) ? 
								pakistanCampTypeMap : null);
						
				if(accTypeSetUS != null) {
					for (String accTypeUS : accTypeSetUS) {
						
						Listitem accTypeItem = new Listitem(accTypeUS);
						
						accTypeItem.setParent(accTypeLbId);
						accTypeItem.setValue(selMap.get(accTypeUS));
					}
				}
				if(accTypeLbId.getItemCount() > 0) {
					accTypeLbId.setSelectedIndex(0);
					onSelect$accTypeLbId();
				}
				for (Listitem item : modeLbId.getItems()) {
					
					if(item.getLabel().equalsIgnoreCase(Constants.SMS_GATEWAY_MODE_HTTP)){
						item.setSelected(true);
						//pullReportsChkId.setDisabled(country.equals(Constants.SMS_COUNTRY_US));
						onSelect$modeLbId();
						//modeLbId.setDisabled(country.equals(Constants.SMS_COUNTRY_US));
						break;
						
					}
				}
				
				smppDivId.setVisible(false);
			}else if(country.equals(Constants.SMS_COUNTRY_UAE)){
				

				accTypeLbId.clearSelection();
				gatwaycmbboxId.setValue(Constants.STRING_NILL);
				Components.removeAllChildren(accTypeLbId);
				Components.removeAllChildren(gatwaycmbboxId);
				
				Set<String> accTypeSetUAE = uaeCampTypeMap.keySet();
				
				for (String accTypeUAE : accTypeSetUAE) {
					
					Listitem accTypeItem = new Listitem(accTypeUAE);
					
					accTypeItem.setParent(accTypeLbId);
					
					accTypeItem.setValue(uaeCampTypeMap.get(accTypeUAE));
				}
				
				if(accTypeLbId.getItemCount() > 0) {
					accTypeLbId.setSelectedIndex(0);
					onSelect$accTypeLbId();
				}
				for (Listitem item : modeLbId.getItems()) {
					
					if(item.getLabel().equalsIgnoreCase(Constants.SMS_GATEWAY_MODE_SMPP)){
						
						item.setSelected(true);
						onSelect$modeLbId();
						modeLbId.setDisabled(true);
						if(type == null && ocsmsGateway == null){ // condition that new gateway is being added
							modeLbId.setDisabled(false);
						}
						
						break;
						
					}
				}
				smppDivId.setVisible(true);
				sysIDTxtboxId.setValue(Constants.STRING_NILL);
				sysPwdTxtboxId.setValue(Constants.STRING_NILL);
				portTxtboxId.setValue(Constants.STRING_NILL);
				ipTxtboxId.setValue(Constants.STRING_NILL);
				sysTypeTxtboxId.setValue(Constants.STRING_NILL);
				 postpaidTxtId.setValue(Constants.STRING_NILL);
				 
				 twoWayChkId.setChecked(false);
				 twoWayTxtId.setText("");
				 twoWayDivId.setVisible(false);
				 pullReportsTxtId.setValue(Constants.STRING_NILL);
				
				
			
				
			} 
			else // for all the countries other than mentioned above.
			{

				if(campTypeMap.containsKey(country)){
					genericCampTypeMap = campTypeMap.get(country);
					
					
					accTypeLbId.clearSelection();
					gatwaycmbboxId.setValue(Constants.STRING_NILL);
					Components.removeAllChildren(accTypeLbId);
					Components.removeAllChildren(gatwaycmbboxId);
					
					Set<String> accTypeSetUAE = genericCampTypeMap.keySet();
					
					for (String accTypeUAE : accTypeSetUAE) {
						
						Listitem accTypeItem = new Listitem(accTypeUAE);
						
						accTypeItem.setParent(accTypeLbId);
						
						accTypeItem.setValue(genericCampTypeMap.get(accTypeUAE));
					}
					
					if(accTypeLbId.getItemCount() > 0) {
						accTypeLbId.setSelectedIndex(0);
						onSelect$accTypeLbId();
					}
					for (Listitem item : modeLbId.getItems()) {
						
						if(item.getLabel().equalsIgnoreCase(Constants.SMS_GATEWAY_MODE_SMPP)){
							
							item.setSelected(true);
							onSelect$modeLbId();
							modeLbId.setDisabled(true);
							if(type == null && ocsmsGateway == null){ // condition that new gateway is being added
								modeLbId.setDisabled(false);
							}
							
							break;
							
						}
					}
					smppDivId.setVisible(true);
					sysIDTxtboxId.setValue(Constants.STRING_NILL);
					sysPwdTxtboxId.setValue(Constants.STRING_NILL);
					portTxtboxId.setValue(Constants.STRING_NILL);
					ipTxtboxId.setValue(Constants.STRING_NILL);
					sysTypeTxtboxId.setValue(Constants.STRING_NILL);
					 postpaidTxtId.setValue(Constants.STRING_NILL);
					 
					 twoWayChkId.setChecked(false);
					 twoWayTxtId.setText("");
					 twoWayDivId.setVisible(false);
					 pullReportsTxtId.setValue(Constants.STRING_NILL);
					
					
					
				}
				
				

				
				
				
			
				
			
				
			}
			
			userIdTxtboxId.setValue(Constants.STRING_NILL);
			pwdTxtboxId.setValue(Constants.STRING_NILL);
			
			
			apiIdTxtboxId.setValue(Constants.STRING_NILL);
			
			senderIdTxtboxId.setValue(Constants.STRING_NILL);
			
			
		
		if(!postpaidChkId.isChecked())postpaidDivId.setVisible(!postpaidChkId.isChecked());
					
			
		} catch (Exception e) {
			logger.error("Exception ",e);
		}
		
		
  }//onSelect$countryLbId
	

	
	public void onSelect$modeLbId(){
		
		String moldLbl=modeLbId.getSelectedItem().getLabel().trim();
		if(moldLbl.equals(Constants.SMS_GATEWAY_MODE_SMPP)){
			smppRelatedChkbxDivId.setVisible(true);
			httpRelatedChkbxDivId.setVisible(false);
			smppDivId.setVisible(true);
		}else if(moldLbl.equals(Constants.SMS_GATEWAY_MODE_HTTP)){
			smppRelatedChkbxDivId.setVisible(false);
			httpRelatedChkbxDivId.setVisible(true);
			smppDivId.setVisible(false);
		}
		
	}
	
	public boolean validateSmsGateways(){
		
		try {
			if(countryLbId.getSelectedIndex() == -1){
				
				
				MessageUtil.setMessage("Please select country.", "color:red;");
				return false;
			}
			
			if(accTypeLbId.getSelectedIndex() == -1){
				
				
				MessageUtil.setMessage("Please select account type.", "color:red;");
				return false;
			}
			
			
			
			if(gatwaycmbboxId.getValue().trim().isEmpty() || gatwaycmbboxId.getValue().trim().length() == 0){
				
				
				MessageUtil.setMessage("Please provide gateway.", "color:red;");
				return false;
			}
			
			
			
			if(userIdTxtboxId.getValue().trim().isEmpty() || userIdTxtboxId.getValue().trim().length() == 0){
				
				MessageUtil.setMessage("Please provide user ID .", "color:red;");
				return false;
				
				
			}
			
			
			boolean alreadyExists = doesGatewayAlreadyExist(((String) countryLbId.getSelectedItem().getValue()).trim(), 
					((String) accTypeLbId.getSelectedItem().getValue()).trim(), gatwaycmbboxId.getValue().trim(), userIdTxtboxId.getValue().trim());
			
			
			if(alreadyExists){
				MessageUtil.setMessage("Same gateway configuration already exists for the combination of Country, Account Type, Gateway Name and User ID.", "color:red;");
				return false;
			}
			
			
			if(pwdTxtboxId.getValue().trim().isEmpty() || pwdTxtboxId.getValue().trim().length() == 0){
				
				MessageUtil.setMessage("Please provide password .", "color:red;");
				return false;
				
				
			}
			
			if(modeLbId.getSelectedIndex() == -1){
				
				
				MessageUtil.setMessage("Please select mode.", "color:red;");
				return false;
				
			}
			
			/*if(countryLbId.getSelectedItem().getLabel().trim().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)){
				
				
				if(modeLbId.getSelectedItem().getLabel().trim().equalsIgnoreCase(Constants.SMS_GATEWAY_MODE_HTTP)){
					
					MessageUtil.setMessage("HTTP selection is not valid for India.", "color:red;");
					return false;
					
				}
				
				
				
			}*/
			
			if(countryLbId.getSelectedItem().getLabel().trim().equalsIgnoreCase(Constants.SMS_COUNTRY_US) || 
					countryLbId.getSelectedItem().getLabel().trim().equalsIgnoreCase(Constants.SMS_COUNTRY_CANADA) || 
					countryLbId.getSelectedItem().getLabel().trim().equalsIgnoreCase(Constants.SMS_COUNTRY_SA) ||
					countryLbId.getSelectedItem().getLabel().trim().equalsIgnoreCase(Constants.SMS_COUNTRY_SINGAPORE)){
				
				
				if(modeLbId.getSelectedItem().getLabel().trim().equalsIgnoreCase(Constants.SMS_GATEWAY_MODE_SMPP)){
					
					MessageUtil.setMessage("SMPP selection is not valid for US.", "color:red;");
					return false;
					
				}
				
				
				
			}
			if(modeLbId.getSelectedItem().getLabel().equals(Constants.SMS_GATEWAY_MODE_SMPP)){
				
				if(sysIDTxtboxId.getValue().trim().isEmpty() || sysIDTxtboxId.getValue().trim().length() == 0){
					
					MessageUtil.setMessage("Please provide system ID.", "color:red;");
					return false;
					
					
				}
				
				
				
				
				
				if(sysPwdTxtboxId.getValue().trim().isEmpty() || sysPwdTxtboxId.getValue().trim().length() == 0){
					
					MessageUtil.setMessage("Please provide system password.", "color:red;");
					return false;
					
					
				}
				if(ipTxtboxId.getValue().trim().isEmpty() || ipTxtboxId.getValue().trim().length() == 0){
					
					MessageUtil.setMessage("Please provide IP.", "color:red;");
					return false;
					
					
				}
				
				if(portTxtboxId.getValue().trim().isEmpty() || portTxtboxId.getValue().trim().length() == 0){
					
					MessageUtil.setMessage("Please provide port.", "color:red;");
					return false;
					
					
				}
				
			
			}
			if(pullReportsChkId.isChecked()) {
				
				if(pullReportsTxtId.getValue().trim().isEmpty() || pullReportsTxtId.getValue().trim().length() == 0){
					
					MessageUtil.setMessage("Please provide URL to fetch report.", "color:red;");
					return false;
					
					
				}
				
			}
			
			/*if( !postpaidChkId.isChecked()) {
				
				if(postpaidTxtId.getValue().trim().isEmpty() || postpaidTxtId.getValue().trim().length() == 0){
					
					MessageUtil.setMessage("Please provide URL to get balance.", "color:red;");
					return false;
					
					
				}
				
			}*/
			if(countryLbId.getSelectedItem().getLabel().trim().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)
					&& (peId.getValue()==null || peId.getValue().isEmpty())){
			MessageUtil.setMessage("Please enter a Principal Entity ID", "color:red;");
			return false;
			}
			
			return true;
		} catch (WrongValueException e) {
			logger.error("Exception" ,e);
			return false;
		}
	}
	
	
	
	private boolean doesGatewayAlreadyExist(String country, String account, String gateway, String userId){
		try{
			List<OCSMSGateway> oCSMSGatewayList = ocsmsGatewayDao.findOcSMSGatewaysByUserIds(country, gateway, account);
			
			if(oCSMSGatewayList != null && oCSMSGatewayList.size() > 0){
				
				for(OCSMSGateway aOCSMSGateway : oCSMSGatewayList){
					if(aOCSMSGateway.getUserId().equals(userId)){
						return true;
					}
				}
				
				return false;
				
			}else{
				
				return false;
			}
			
		}catch(Exception e){
			logger.error("Exception >>> ",e);
			return true;
		}
		
	}
	
	
	public void onClick$saveBtnId(){
		
		
		if(ocsmsGateway == null && type == null && !validateSmsGateways()){
		
			
			return;
		}
		
		
		
		
		if(ocsmsGateway == null && type == null){
			
			ocsmsGateway = new OCSMSGateway();
			ocsmsGateway.setCreatedDate(MyCalendar.getInstance(clientTimeZone));
			ocsmsGateway.setCreatedBy(currentUser.getUserId());
			
			ocsmsGateway.setModifiedBy(currentUser.getUserId());
			ocsmsGateway.setModifiedDate(MyCalendar.getInstance(clientTimeZone));
			
			ocsmsGateway.setStatus(Constants.SMS_GATEWAY_STATUS_ACTIVE);
			
			
			
			
			
			
			
		}else if(ocsmsGateway != null && type != null){
			
			ocsmsGateway.setModifiedBy(currentUser.getUserId());
			ocsmsGateway.setModifiedDate(MyCalendar.getInstance(clientTimeZone));
			
		}
		// country
		
		String countryStr=((String) countryLbId.getSelectedItem().getValue()).trim();
		if(! countryStr.isEmpty()){
			
			ocsmsGateway.setCountryName(countryStr);
		}
		
		// Account type
		
		
		String accTypeStr=((String) accTypeLbId.getSelectedItem().getValue()).trim();
		
		if(! accTypeStr.isEmpty() ){
			
			ocsmsGateway.setAccountType(accTypeStr);
		}
			
		
		
		// Gateway Name
		
		String gatewayStr= gatwaycmbboxId.getValue().trim();
		
		if(!gatewayStr.isEmpty()){
			
			
			ocsmsGateway.setGatewayName(gatewayStr);
			
		}
		
		// UserId
		
		String userIdStr= userIdTxtboxId.getValue().trim();
		
		if(!userIdStr.isEmpty()){
			
			
			ocsmsGateway.setUserId(userIdStr);
			
		}
		
		// Password
		
		String pwdStr= pwdTxtboxId.getValue().trim();
		
		if(!pwdStr.isEmpty()){
			
			
			ocsmsGateway.setPwd(pwdStr);
			
		}
		
		// API ID
		
		String apiId = apiIdTxtboxId.getValue();
		if(!apiId.isEmpty()){
			
			ocsmsGateway.setAPIId(apiId);
			
		}
		
		// senderId
		
		
		// API ID
		
		String senderId = senderIdTxtboxId.getValue();
		
		if(!senderId.isEmpty()){
			
			
			updateUserSMSSenderId(ocsmsGateway,senderId);
			logger.info("Sender id():"+senderIdTxtboxId.getValue());
			ocsmsGateway.setSenderId(senderId);
			String[] addsenderIds = senderId.split(Constants.DELIMETER_COMMA);
			try {
				if(ocsmsGateway.getId()!=null) {
				List<UserSMSGateway> retList = userSMSGatewayDao.findAllByGatewayId(ocsmsGateway.getId());
				List<UserSMSSenderId> userSMSSenderIdRecord = null;
				if(retList!=null) {
				for(UserSMSGateway userSmsgateway : retList) {
					Users user=usersDao.findByUserId(userSmsgateway.getUserId());
					for(String senderid : addsenderIds ){
					userSMSSenderIdRecord = userSMSSenderIdDao.findSenderIdBySMSType(userSmsgateway.getUserId(),ocsmsGateway.getAccountType());
					if(userSMSSenderIdRecord == null) {
						UserSMSSenderId userPRSMSSenderIdObj = new UserSMSSenderId();
						userPRSMSSenderIdObj.setUserName(user.getUserName());
						userPRSMSSenderIdObj.setUserId(user.getUserId());
						userPRSMSSenderIdObj.setSmsType(ocsmsGateway.getAccountType());
						userPRSMSSenderIdObj.setSenderId(senderid);
						userSMSSenderIdDaoForDML.saveOrUpdate(userPRSMSSenderIdObj);
					}
//					else {
//					for(UserSMSSenderId existingUserSMSSenderIdRecord : userSMSSenderIdRecord) {
//					existingUserSMSSenderIdRecord.setSenderId(senderid);
//						userSMSSenderIdDaoForDML.saveOrUpdate(existingUserSMSSenderIdRecord);
//					}
//					}
				}
			  }
			}
			}
		}catch(Exception e) {
			logger.error("Exception ",e);
		}
		}
			
			
		ocsmsGateway.setMode(modeLbId.getSelectedItem().getLabel());
		String modeStr = modeLbId.getSelectedItem().getLabel();
		
		if(modeStr.equalsIgnoreCase("HTTP")){
			ocsmsGateway.setEnableMultiThreadSub(enableMultiThreadChkId.isChecked());
		}else if(modeStr.equalsIgnoreCase("SMPP")){
			ocsmsGateway.setEnableSessionAlive(enableCheckSessionAliveChkId.isChecked());
		}
		
		if(smppDivId.isVisible()){
			
			//System Id
			
			
			String sysId=sysIDTxtboxId.getValue().trim();
			if(!sysId.isEmpty()){
				
				
				ocsmsGateway.setSystemId(sysId);
			}
			
			
			// system password
			
			String sysPwd=sysPwdTxtboxId.getValue().trim();
			
			if(!sysPwd.isEmpty()){
				
				ocsmsGateway.setSystemPwd(sysPwd);
			}
			
			// system Type
			
			String systemType=sysTypeTxtboxId.getValue().trim();
			
			if(!systemType.isEmpty()){
				
				ocsmsGateway.setSystemType(systemType);
			}
			
			// IP
			
			String ip= ipTxtboxId.getValue().trim();
			if(! ip.isEmpty()){
				
				ocsmsGateway.setIp(ip);
				
			}
			
			// Port
			
			String port = portTxtboxId.getValue().trim();
			if(! port.isEmpty()){
				
				
				ocsmsGateway.setPort(port);
			}
			
		}else{
			
			ocsmsGateway.setSystemId(null);
			ocsmsGateway.setSystemPwd(null);
			ocsmsGateway.setIp(null);
			ocsmsGateway.setPort(null);
			
		}
		
		// check for pull reports
		
			
			
		if((countryLbId.getSelectedItem().getLabel().trim().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA) &&
				accTypeLbId.getSelectedItem().getLabel().trim().equalsIgnoreCase(Constants.SMS_TYPE_NAME_TRANSACTIONAL))){
			
			ocsmsGateway.setPullReports(true);
			ocsmsGateway.setPullReportsURL(pullReportsTxtId.getValue().trim());
		} /*else {
			ocsmsGateway.setPullReports(false);
			ocsmsGateway.setPullReportsURL(null);
		}*/
		if((countryLbId.getSelectedItem().getLabel().trim().equalsIgnoreCase(Constants.SMS_COUNTRY_UAE) &&
				accTypeLbId.getSelectedItem().getLabel().trim().equalsIgnoreCase(Constants.SMS_TYPE_NAME_TRANSACTIONAL))){
			
			ocsmsGateway.setPullReports(true);
			ocsmsGateway.setPullReportsURL(pullReportsTxtId.getValue().trim());
		}
		
		ocsmsGateway.setPullReports(pullReportsChkId.isChecked());
		if(pullReportsChkId.isChecked())ocsmsGateway.setPullReportsURL(pullReportsTxtId.getText().trim());
		// check for postpaid
		
		if(postpaidChkId.isChecked()){
			
			
			ocsmsGateway.setPostPaid(true);
			ocsmsGateway.setPostpaidBalURL(null);
			
		}else{
			ocsmsGateway.setPostPaid(false);
			ocsmsGateway.setPostpaidBalURL(postpaidTxtId.getValue().trim());
		}
		
		if(twoWayChkId.isChecked()){
			
			ocsmsGateway.setTwoWaySenderID(twoWayTxtId.getValue().trim());
		}else{
			ocsmsGateway.setTwoWaySenderID(null);
		}
		
		if(countryLbId.getSelectedItem().getLabel().trim().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)
				&& (peId.getValue()==null || peId.getValue().isEmpty())){
		MessageUtil.setMessage("Please enter a Principal Entity ID", "color:red;");
		return;
		}
		
		if(countryLbId.getSelectedItem().getLabel().trim().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)
				&& peId.getValue()!=null && !peId.getValue().isEmpty()){
			ocsmsGateway.setPrincipalEntityId(peId.getValue());
		}
		
		try {
			String saveMsg=Constants.STRING_NILL;
			
			
			int confirm = Messagebox.show("Are you sure you want to save the gateway?", "Prompt", 
					 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm == Messagebox.OK) {
					
					
						
							if(type == null){
								saveMsg = "created";
							}else if(type != null){
								saveMsg = "updated";
							}
							//ocsmsGatewayDao.saveOrUpdate(ocsmsGateway);
							ocsmsGatewayDaoForDML.saveOrUpdate(ocsmsGateway);
							
							
							MessageUtil.setMessage("Gateway "+saveMsg+"  successfully.","color:green;");
							Redirect.goTo(PageListEnum.EMPTY);
							Redirect.goTo(PageListEnum.ADMIN_SMS_GATEWYS);
						
						
				}
		} catch (Exception e) {
			logger.error("Exception" , e);
		}
				
			
		
	}//onClick$saveBtnId()
	
	public void onClick$cancelBtnId(){
		
		Redirect.goTo(PageListEnum.EMPTY);
		Redirect.goTo(PageListEnum.ADMIN_SMS_GATEWYS);
	}
	
	// added for receiving numbers
	
	
	
	/*public void onClick$addMsgRecvNumTabId(){
		
		
		
		defaultNumberSettings();
		
		
		
		
	}*/
	public void defaultNumberSettings(){
		
		
 		countryMap.put(Constants.SMS_COUNTRY_INDIA, Constants.SMS_COUNTRY_INDIA);
 		countryMap.put(Constants.SMS_COUNTRY_US, Constants.SMS_COUNTRY_US);
 		countryMap.put(Constants.SMS_COUNTRY_CANADA, Constants.SMS_COUNTRY_CANADA);
 		countryMap.put(Constants.SMS_COUNTRY_SA, Constants.SMS_COUNTRY_SA);
 		countryMap.put(Constants.SMS_COUNTRY_SINGAPORE, Constants.SMS_COUNTRY_SINGAPORE);//APP-4688
 		
 		countryMap.put(Constants.SMS_COUNTRY_PAKISTAN, Constants.SMS_COUNTRY_PAKISTAN);
 		countryCarrierMap.put(Constants.SMS_COUNTRY_INDIA, Constants.SMS_COUNTRY_CODE_INDIA);
 		countryCarrierMap.put(Constants.SMS_COUNTRY_US, Constants.SMS_COUNTRY_CODE_US);
 		countryCarrierMap.put(Constants.SMS_COUNTRY_PAKISTAN, Constants.SMS_COUNTRY_CODE_PAKISTAN);
 		countryCarrierMap.put(Constants.SMS_COUNTRY_CANADA, Constants.SMS_COUNTRY_CODE_CANADA);
 		countryCarrierMap.put(Constants.SMS_COUNTRY_SA, Constants.SMS_COUNTRY_CODE_SA);
 		countryCarrierMap.put(Constants.SMS_COUNTRY_SINGAPORE, Constants.SMS_COUNTRY_CODE_SINGAPORE);//APP-4688
 		
 		
 		Set<String> countrySet = countryMap.keySet();
		
		Components.removeAllChildren(countryLBId);
		
		for (String country : countrySet) {
			
			
			Listitem item= new Listitem(country);
			
			item.setParent(countryLBId);
			item.setAttribute("carrier", countryCarrierMap.get(country));
			item.setValue(countryMap.get(country));
		
		}
		
		if(countryLBId.getItemCount() > 0){
			countryLBId.setSelectedIndex(0);
		}
		
		typeMap.put(Constants.SMS_TYPE_NAME_PROMOTIONAL, Constants.RECEVING_NUMBER_TYPE_PROMOTIONAL);
		typeMap.put(Constants.SMS_TYPE_NAME_OPTIN, Constants.RECEVING_NUMBER_TYPE_OPTIN);
		typeMap.put(Constants.RECEVING_NUMBER_TYPE_NAME_MISSEDCALL, Constants.RECEVING_NUMBER_TYPE_MISSEDCALL);
 		
 		Set<String> typeSet = typeMap.keySet();
		
 		Components.removeAllChildren(typeLBId);
		
		for (String type : typeSet) {
			
			
			Listitem item= new Listitem(type);
			
			item.setParent(typeLBId);
			item.setValue(typeMap.get(type));
		
		}
		
		if(typeLBId.getItemCount() > 0){
			typeLBId.setSelectedIndex(0);
			//onSelect$typeLBId();
		}
		onSelect$countryLBId();
		
		
	
		

	}//defaultNumSettings
	
	
	public void onSelect$countryLBId(){
		
		String country = countryLBId.getSelectedItem().getLabel().trim();
		
		String type = typeLBId.getSelectedItem().getLabel().trim();
		
		msgRecvNumTxtId.setValue(Constants.STRING_NILL);
		
		
		if((country != null && ! country.isEmpty()) && (type != null && ! type.isEmpty())){
			
			List<CountryReceivingNumbers> recevingNumList = countryReceivingNumbersDao.findBy(countryMap.get(country));//getReceivingNumByCountry(countryMap.get(country), typeMap.get(type));
			if(recevingNumList == null) return;
			
			
			Components.removeAllChildren(msgRecvNumCmbId);
			
				
			for (CountryReceivingNumbers NumberStr : recevingNumList) {
				
				Comboitem item = new Comboitem(NumberStr.getReceivingNumber().length() > 10 ? "+"+NumberStr.getReceivingNumber() : NumberStr.getReceivingNumber());
				item.setDescription("type: "+NumberStr.getRecvNumType() );
				item.setValue(NumberStr);
				item.setParent(msgRecvNumCmbId);
			}
			
		}
		
	}
	
	

	/*public void onSelect$typeLBId(){
		
		String country = countryLBId.getSelectedItem().getLabel().trim();
		
		String type = typeLBId.getSelectedItem().getLabel().trim();
		
		
		if((country != null && ! country.isEmpty()) && (type != null && ! type.isEmpty())){
			
			List<CountryReceivingNumbers> recevingNumList = countryReceivingNumbersDao.findBy(countryMap.get(country));//getReceivingNumByCountry(countryMap.get(country), typeMap.get(type));
			if(recevingNumList == null) return;
			
			System.out.println("country is"+country);
			System.out.println("type is"+type);
			System.out.println("recevingNumList size is"+recevingNumList.size());
			
			Components.removeAllChildren(msgRecvNumCmbId);
			
				
			for (CountryReceivingNumbers NumberStr : recevingNumList) {
				
				Comboitem item = new Comboitem(NumberStr.getReceivingNumber().length() > 10 ? "+"+NumberStr.getReceivingNumber() : NumberStr.getReceivingNumber());
				item.setDescription("type: "+NumberStr.getRecvNumType() );
				item.setValue(NumberStr);
				item.setParent(msgRecvNumCmbId);
			}
			
		}
	}
	*/
	
	public void onClick$addNumberTbId(){
		
		String recvNumber = msgRecvNumTxtId.getText().trim();
		
		
		if( recvNumber == null || recvNumber.trim().length() == 0) {
			
			MessageUtil.setMessage("Please enter your number.", "color:red;");
			return;
			
		}//if
		
		try {
			Long.parseLong(recvNumber);
		} catch (NumberFormatException e) {
			
			MessageUtil.setMessage("Please enter valid number.", "color:red;");
			return;
		
		}
		
		String carrier = countryLBId.getSelectedItem().getAttribute("carrier").toString();
		
			
		recvNumber = (recvNumber.length() == 10 && !recvNumber.startsWith(carrier) ) ? carrier+recvNumber : recvNumber;
		
		
		Comboitem addItem = findOptionalComboitem(msgRecvNumCmbId, recvNumber);
		if(addItem != null) {
			
			MessageUtil.setMessage("This number-type-country combination already exists, please enter another number.", "color:red;");
			return;
		
		}
		
		CountryReceivingNumbers countryReceivingNumbers = new CountryReceivingNumbers();
		
		countryReceivingNumbers.setCountry(countryMap.get(countryLBId.getSelectedItem().getLabel()));
		countryReceivingNumbers.setRecvNumType(typeMap.get(typeLBId.getSelectedItem().getLabel()));
		countryReceivingNumbers.setReceivingNumber(recvNumber);
		countryReceivingNumbers.setCreatedDate(Calendar.getInstance());
		countryReceivingNumbers.setCreatedBy(currentUser.getUserId());
	
		addItem = new Comboitem(recvNumber.length() > 10 ? "+"+recvNumber : recvNumber);
		addItem.setDescription("type: "+typeMap.get(typeLBId.getSelectedItem().getLabel()));
		addItem.setValue(countryReceivingNumbers);
		addItem.setParent(msgRecvNumCmbId);	
		
		msgRecvNumTxtId.setText(Constants.STRING_NILL);
	}
	private Comboitem findOptionalComboitem(Combobox cb, String label) {
		try {
			List<Comboitem> items = cb.getItems();
			for (Comboitem cbitem : items) {
				CountryReceivingNumbers recvNumber = (CountryReceivingNumbers)cbitem.getValue();
				String number = recvNumber.getReceivingNumber();
				
			
				
		//		System.out.println("number is"+number+ ":: label is ::"+label +"desc is"+cbitem.getDescription().endsWith(recvNumber.getRecvNumType()));
				if((number.endsWith(label) || 
						label.endsWith(number)) && cbitem.getDescription().endsWith(typeMap.get(typeLBId.getSelectedItem().getLabel())) ) return cbitem;
			} // for
			
			return null;
		} catch (Exception e) {
			logger.error("Exception",e);
			//logger.error("Exception :::",e);
			return null;
		}
	}
	
	public void onClick$saveNumBtnId(){
		
		try {
			
		
			
			//CountryReceivingNumbers	countryReceivingNumbers = null;
			if(msgRecvNumCmbId.getItemCount() == 0){
				
				MessageUtil.setMessage("No numbers found to add.", "color:red;");
				return;
				
			}
			
			List<CountryReceivingNumbers> numList = new ArrayList<CountryReceivingNumbers>();
				
			for (Comboitem item: msgRecvNumCmbId.getItems()) {
				
				CountryReceivingNumbers number = (CountryReceivingNumbers)item.getValue();
				
				if(number.getRecvNumId() == null) {
					numList.add(number);
				}
			}
			if(numList.size() > 0) {
				
				MessageUtil.setMessage("All new Numbers saved successfully", "color:blue;");
				countryReceivingNumbersDaoForDML.saveByCollection(numList);
				
				
			}
		
		} catch (Exception e) {
		
			logger.error("Exception", e);
		}
		
		
		
		
		
	}
	
	
	public boolean validateRecvNumbers(){
		
		
		if(countryLBId.getSelectedItem().getLabel().trim().isEmpty() || countryLBId.getSelectedItem().getLabel().trim().length() == 0){
			
			MessageUtil.setMessage("Please select country", "color:red;");
			return false;
		}

		if(typeLBId.getSelectedItem().getLabel().trim().isEmpty() || typeLBId.getSelectedItem().getLabel().trim().length() == 0){
			
			MessageUtil.setMessage("Please select type", "color:red;");
			return false;
		}
		
		
		if(msgRecvNumCmbId.getItemCount() < 0){
			
			MessageUtil.setMessage(" Please add at least one message receiving number.", "color:red;");
			return false ;
		
		}

		if(msgRecvNumCmbId.getSelectedItem() == null ){
			
			MessageUtil.setMessage("Please select number", "color:edr;");
			return false;
		}
		
		
		
		return true;
	}
	public void onClick$cancelNumBtnId(){
		
		if(countryLBId.getItemCount() > 0){
			countryLBId.setSelectedIndex(0);
		}
		
		if(typeLBId.getItemCount() > 0){
			typeLBId.setSelectedIndex(0);
		}
	
	}
	
	public void onCheck$twoWayChkId(){
		twoWayDivId.setVisible(twoWayChkId.isChecked());
	}
	
	public void updateUserSMSSenderId(OCSMSGateway ocsmsGateway,String newSenderId){
		
		//List<UserSMSSenderId> existingsenderIds = null;
		try{
		String[] newSenderIds = newSenderId.split(Constants.DELIMETER_COMMA);
		List<String> newSenderIdList = new ArrayList<String>();
		for(String senderid : newSenderIds ){
			newSenderIdList.add(senderid);
		}
		List<String> existingSenderIdList = new ArrayList<String>();
		if(ocsmsGateway.getSenderId()!=null){
			String[] existingSenderIds = ocsmsGateway.getSenderId().split(Constants.DELIMETER_COMMA);
		for(String senderid : existingSenderIds ){
			existingSenderIdList.add(senderid);
		}
		
			for(String senderId:existingSenderIds){
				if(!newSenderIdList.contains(senderId)){
					userSMSSenderIdDaoForDML.deleteBySenderId(senderId,ocsmsGateway.getAccountType());
				}
			}
		}
			
		}catch (Exception e) {

		logger.error("Exception ::" , e);
		}
	}
 }
