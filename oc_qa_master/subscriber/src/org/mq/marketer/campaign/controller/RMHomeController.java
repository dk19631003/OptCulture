package org.mq.marketer.campaign.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.beans.CountryReceivingNumbers;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SMSCampaignReport;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.WACampaignReport;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CampaignReportDao;
import org.mq.marketer.campaign.dao.CampaignScheduleDao;
import org.mq.marketer.campaign.dao.CampaignSentDao;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.CountryReceivingNumbersDao;
import org.mq.marketer.campaign.dao.ExportFileDetailsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.SMSCampaignReportDao;
import org.mq.marketer.campaign.dao.SMSCampaignScheduleDao;
import org.mq.marketer.campaign.dao.SMSCampaignScheduleDaoForDML;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.RightsEnum;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Div;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

@SuppressWarnings({"serial","unchecked"})
public class RMHomeController extends GenericForwardComposer {
	
	Users user = null;
	CampaignScheduleDao campaignScheduleDao =null;
	SMSCampaignScheduleDao smsCampaignScheduleDao=null;
	MailingListDao mailingListDao = null;
	ContactsDao contactsDao=null;
	
	CampaignSentDao campaignSentDao = null;
	CampaignReportDao campaignReportDao = null;
	UserActivitiesDao userActivitiesDao;
	UserActivitiesDaoForDML userActivitiesDaoForDML;
	UsersDao usersDao;
	UsersDaoForDML usersDaoForDML;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Listbox recentCampListLbId ;
	private Label mainMsgsCountId;
	private Session sessionScope;
	private Include optintelIncId, loyaltyIncId;
	
	private Div addContactDivId, createSegmentDivId, userAccountaDivId, smsCampaignsDivId,
	CreateSmscampaignsDivId, smsCampReportsDivId, waCampReportsDivId,smsKeywordReportsDivId, optintelReportsDivId, 
	manageKeywordsDivId,manageAutoEmailsDivId,emailReportsDivId,manageContactsDivId,accountSettingsDivId,
	manageSegmentsDivId,emailCampaignsDivId,createEmailcampaignsDivId, faqaDivId, termsaDivId;
	private Label activeCampaignsLblId, totalSubscribersLblId, optInsLblId, optOutsLblId, messageScheduledLblId, unsubBouncedLblId;
	private Label activeSmsLblId;
	private Window changePwdWinId, mandatorychnagePwdWinId;
	private Textbox changePwdWinId$passwordTbId, changePwdWinId$rePasswordTbId, mandatorychnagePwdWinId$passwordTbId, mandatorychnagePwdWinId$rePasswordTbId;
	private Label changePwdWinId$responseLblId, mandatorychnagePwdWinId$responseLblId;
	
	
	//private Set<Long> userIdsSet = GetUser.getUsersSet();
	private Tab optIntelDashTabId,loyaltyDashTabId;
	SMSCampaignReportDao smsCampaignReportDao = null;
	private   final String SMS_MSG_TEXT = "You have not opted for SMS, Please contact admin.";
	private static final String WA_MSG_TEXT = "You have not opted for Whatsapp, Please contact admin.";
	private final String SMS_MSG_KEYWORD_TEXT = "You have not opted for SMS Keywords, Please contact admin.";
	
	private Set<Long> listIdsSet; 
	public RMHomeController(){
		this.user = GetUser.getLoginUserObj();
		this.sessionScope = Sessions.getCurrent();

		campaignScheduleDao = (CampaignScheduleDao)SpringUtil.getBean("campaignScheduleDao");
		smsCampaignScheduleDao = (SMSCampaignScheduleDao)SpringUtil.getBean("smsCampaignScheduleDao");
		mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
		
		
		this.campaignSentDao = (CampaignSentDao)SpringUtil.getBean("campaignSentDao");
		this.campaignReportDao = (CampaignReportDao)SpringUtil.getBean("campaignReportDao");
		this.userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		this.userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		this.usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		this.usersDaoForDML = (UsersDaoForDML)SpringUtil.getBean("usersDaoForDML");
		
		this.smsCampaignReportDao=(SMSCampaignReportDao)SpringUtil.getBean("smsCampaignReportDao");
		/*if(userActivitiesDao != null) {
        	userActivitiesDao.addToActivityList(ActivityEnum.VISIT_RM_HOME,user);
		}*/
		if(userActivitiesDaoForDML != null) {
        	userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_RM_HOME,GetUser.getLoginUserObj());
		}
		
		listIdsSet = (Set<Long>)sessionScope.getAttribute(Constants.LISTIDS_SET);
		
		
	}
	
	
	
	public void onCreate$RMHomeWinId() {
		
		
		String isFirstTimeStr = (String)sessionScope.getAttribute("isFirstTime");
		
		if(isFirstTimeStr == null ) {
			try {
				
				String welcmFlashMsg = PropertyUtil.getPropertyValueFromDB("welcomeFlashMsg");
				if(welcmFlashMsg != null && welcmFlashMsg.trim().length() > 0) {
					
					welcmFlashMsg = welcmFlashMsg.trim();
					
					String titleMsg="Information !";
					String flashMsg=welcmFlashMsg;
					
					if(welcmFlashMsg.contains(Constants.ADDR_COL_DELIMETER)) {
						
						flashMsg = "";
						String msgArr[] = welcmFlashMsg.split(Constants.ADDR_COL_DELIMETER);
						for (int i = 0; i < msgArr.length; i++) {
							if(i == 0)titleMsg = msgArr[0];
							else 	flashMsg += msgArr[i]+"\n";			
							
							
						}							
						
					}
					Messagebox.show(flashMsg, titleMsg,  Messagebox.OK, Messagebox.INFORMATION);
				
				}
				if(GetUser.getLoginUserObj().getLastLoggedInTime() == null){
					changePwdWinId.setVisible(true);
					changePwdWinId.doHighlighted(); 
					
				}else if(GetUser.getLoginUserObj().getMandatoryUpdatePwdOn() == null) {
					mandatorychnagePwdWinId.setVisible(true);
					mandatorychnagePwdWinId.doHighlighted();
					
				}
				else {
					GetUser.getUserObj(true);
				}
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//logger.error("Exception ::" , e);
			}
			sessionScope.setAttribute("isFirstTime", "false");
		}
		
		UserOrganization userOrg = user.getUserOrganization();
		String orgType = userOrg.getClientType();
		
		if(orgType == null) {
			
			logger.debug("got client type is null....");
			return;
			
			
		}//if
		
		makeDispMenuItems(orgType);
		
		
		
	}
	
	

	public void makeDispMenuItems(String clientType) {
		
		if(clientType.equals(Constants.CLIENT_TYPE_BCRM) ) {
			
			
			optIntelDashTabId.setVisible(false);
			loyaltyDashTabId.setVisible(false);
			smsCampaignsDivId.setVisible(false);
			CreateSmscampaignsDivId.setVisible(false);
			smsCampReportsDivId.setVisible(false);
			waCampReportsDivId.setVisible(false);
			smsKeywordReportsDivId.setVisible(false);
			optintelReportsDivId.setVisible(false);
			manageKeywordsDivId.setVisible(false);
			
		}else if(clientType.equals(Constants.CLIENT_TYPE_POS)) {
			
			Set<String> userRoleSet = (Set<String>)session.getAttribute("userRoleSet");
			if(userRoleSet.contains(RightsEnum.Tab_Optintel_VIEW.name()))optIntelDashTabId.setVisible(true);
			if(userRoleSet.contains(RightsEnum.Tab_Loyalty_VIEW.name()))loyaltyDashTabId.setVisible(true);
			/*optIntelDashTabId.setVisible(true);
			loyaltyDashTabId.setVisible(false);
			*/
			
			
		}
		
	}
	
	public void onClose$changePwdWinId() {
		changePwdWinId.setVisible(false);
	}
	public void onClose$mandatorychnagePwdWinId() {
		mandatorychnagePwdWinId.setVisible(false);
	}
	
	public void onClick$savePwdBtnId$changePwdWinId() {
//		String passwordTbId.getValue() = passwordTbId.getValue();
//		String rePasswordTbId.getValue() = rePasswordTbId.getValue();
		
		if(changePwdWinId$passwordTbId.getValue().trim().equals("")) {
			changePwdWinId$responseLblId.setValue("New Password field cannot be left empty."); 
			changePwdWinId$passwordTbId.setFocus(true);
			 return;
		}
		if(changePwdWinId$rePasswordTbId.getValue().trim().equals("")) {
			changePwdWinId$responseLblId.setValue("Confirm Password field cannot be left empty.");
			changePwdWinId$rePasswordTbId.setFocus(true);
			return;
		}
		
		String pattern = "^(?=.{8,50}$)(?=(.*[A-Z]))(?=(.*[a-z]))(?=(.*[0-9]))(?=(.*[-@!#$%^&-+=()])).*$";
		
		Pattern pwdPattern = Pattern.compile(pattern);
		
		if(changePwdWinId$passwordTbId.getValue() ==null || (!pwdPattern.matcher(changePwdWinId$passwordTbId.getValue().trim()).matches())
				|| changePwdWinId$rePasswordTbId.getValue() ==null || (!pwdPattern.matcher(changePwdWinId$rePasswordTbId.getValue().trim()).matches())) {
			changePwdWinId$responseLblId.setValue("Password must contain at least 8 characters,1 uppercase,"
					+ "\n 1 lowercase,1 special character (@!#$%^&+-=*'()) and 1 number");
			return;
		}
		
		String password = changePwdWinId$passwordTbId.getValue().trim();
		String rePassword = changePwdWinId$rePasswordTbId.getValue().trim();
		
		if( !password.equals(rePassword) ) {
			changePwdWinId$responseLblId.setValue("Two password must be same");
			return;
		} 
		String newPwdHash = Utility.encryptPassword(user.getUserName(), changePwdWinId$passwordTbId.getValue());
		/*Md5PasswordEncoder md5 = new Md5PasswordEncoder();
		String newPwdHash = md5.encodePassword(newPwdStr,user.getUserName());*/
		user.setPassword(newPwdHash);
		user.setMandatoryUpdatePwdOn(Calendar.getInstance());
		//usersDao.saveOrUpdate(user);
		usersDaoForDML.saveOrUpdate(user);
		
		GetUser.getUserObj(true);
		
		changePwdWinId.setVisible(false);
		MessageUtil.setMessage("Password changed successfully.", "blue");
	}
	
	public void onClick$savePwdBtnId$mandatorychnagePwdWinId() {
//		String passwordTbId.getValue() = passwordTbId.getValue();
//		String rePasswordTbId.getValue() = rePasswordTbId.getValue();
		
		if(mandatorychnagePwdWinId$passwordTbId.getValue().trim().equals("")) {
			mandatorychnagePwdWinId$responseLblId.setValue("New Password field cannot be left empty."); 
			mandatorychnagePwdWinId$passwordTbId.setFocus(true);
			 return;
		}
		if(mandatorychnagePwdWinId$rePasswordTbId.getValue().trim().equals("")) {
			mandatorychnagePwdWinId$responseLblId.setValue("Confirm Password field cannot be left empty.");
			mandatorychnagePwdWinId$rePasswordTbId.setFocus(true);
			return;
		}
		
		String pattern = "^(?=.{8,50}$)(?=(.*[A-Z]))(?=(.*[a-z]))(?=(.*[0-9]))(?=(.*[-@!#$%^&-+=()])).*$";
		
		Pattern pwdPattern = Pattern.compile(pattern);
		
		if(mandatorychnagePwdWinId$passwordTbId.getValue() ==null || (!pwdPattern.matcher(mandatorychnagePwdWinId$passwordTbId.getValue().trim()).matches())
				|| mandatorychnagePwdWinId$rePasswordTbId.getValue() ==null || (!pwdPattern.matcher(mandatorychnagePwdWinId$rePasswordTbId.getValue().trim()).matches())) {
			mandatorychnagePwdWinId$responseLblId.setValue("Password must contain at least 8 characters,1 uppercase,1 lowercase,"
					+ "\n 1 special character (@!#$%^&+-=*'()) and 1 number .");
			return;
		}
		
		String password = mandatorychnagePwdWinId$passwordTbId.getValue().trim();
		String rePassword = mandatorychnagePwdWinId$rePasswordTbId.getValue().trim();
		
		if( !password.equals(rePassword) ) {
			mandatorychnagePwdWinId$responseLblId.setValue("Two password must be same");
			return;
		} 
		String newPwdHash = Utility.encryptPassword(user.getUserName(), mandatorychnagePwdWinId$passwordTbId.getValue());
		/*Md5PasswordEncoder md5 = new Md5PasswordEncoder();
		String newPwdHash = md5.encodePassword(newPwdStr,user.getUserName());*/
		user.setPassword(newPwdHash);
		user.setMandatoryUpdatePwdOn(Calendar.getInstance());
		//usersDao.saveOrUpdate(user);
		usersDaoForDML.saveOrUpdate(user);
		
		GetUser.getUserObj(true);
		
		mandatorychnagePwdWinId.setVisible(false);
		MessageUtil.setMessage("Password changed successfully.", "blue");
	}
	private Label myDownloadCountId;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		
		long starttime = System.currentTimeMillis();
		
		try{
			super.doAfterCompose(comp);
		String userName = GetUser.getOnlyUserName();
		PageUtil.setHeader("Welcome, " + userName + "!", "", "font-size:15px;color:#2C7095;font-family:verdana;marg", true);
		int msgCount = (new MessageHandler()).getNewMsgsCount(user.getUserId());
		
		mainMsgsCountId.setValue("" + msgCount);
		if(msgCount==0) {
			mainMsgsCountId.setSclass("messagesDataRead");
		}
		else {
			mainMsgsCountId.setSclass("messagesDataUnRead");
		}
		
		// Update Quick Overview
		updateQuickOverview();
		
 			
 			Set<String> userRoleSet = (Set<String>)session.getAttribute("userRoleSet");
 			
 			if(userRoleSet != null) {
 				
 					if(userRoleSet.contains(RightsEnum.MenuItem_AddImport_Contacts_VIEW.name()))addContactDivId.setVisible(true);
 					if(userRoleSet.contains(RightsEnum.MenuItem_CreateSegment_VIEW.name()))createSegmentDivId.setVisible(true);
 					if(userRoleSet.contains(RightsEnum.MenuItem_ManageUsers_VIEW.name()))userAccountaDivId.setVisible(true);
 					if(userRoleSet.contains(RightsEnum.MenuItem_CreateEmail_VIEW.name()))createEmailcampaignsDivId.setVisible(true);
 					if(userRoleSet.contains(RightsEnum.MenuItem_CreateSMS_VIEW.name()))CreateSmscampaignsDivId.setVisible(true);
 					if(userRoleSet.contains(RightsEnum.MenuItem_MySMSCampaigns_VIEW.name()))smsCampaignsDivId.setVisible(true);
 					if(userRoleSet.contains(RightsEnum.MenuItem_SMSCampaignReports_VIEW.name()))smsCampReportsDivId.setVisible(true);
 					if(userRoleSet.contains(RightsEnum.MenuItem_WACampaignReports_VIEW.name()))waCampReportsDivId.setVisible(true);
 					if(userRoleSet.contains(RightsEnum.MenuItem_SMSKeywordUsageReports_VIEW.name()))smsKeywordReportsDivId.setVisible(true);
 					if(userRoleSet.contains(RightsEnum.MenuItem_OptIntelReports_VIEW.name()))optintelReportsDivId.setVisible(true);
 					if(userRoleSet.contains(RightsEnum.MenuItem_ManageKeywords_VIEW.name()))manageKeywordsDivId.setVisible(true);
 					if(userRoleSet.contains(RightsEnum.MenuItem_ManageAutoEmails_VIEW.name()))manageAutoEmailsDivId.setVisible(true);
 					if(userRoleSet.contains(RightsEnum.MenuItem_EmailCampaignReports_VIEW.name()))emailReportsDivId.setVisible(true);
 					if(userRoleSet.contains(RightsEnum.MenuItem_Lists_VIEW.name()))manageContactsDivId.setVisible(true);
 					if(userRoleSet.contains(RightsEnum.MenuItem_MyProfile_VIEW.name()))accountSettingsDivId.setVisible(true);
 					if(userRoleSet.contains(RightsEnum.MenuItem_ViewContactSegments_VIEW.name()))manageSegmentsDivId.setVisible(true);
 					if(userRoleSet.contains(RightsEnum.MenuItem_MyEmailsCampaigns_VIEW.name()))emailCampaignsDivId.setVisible(true);
 					if(userRoleSet.contains(RightsEnum.MenuItem_Faq_VIEW.name()))faqaDivId.setVisible(true);
 					if(userRoleSet.contains(RightsEnum.MenuItem_Terms_VIEW.name()))termsaDivId.setVisible(true);
 					
 					if(userRoleSet.contains(RightsEnum.Tab_Optintel_VIEW.name()))optIntelDashTabId.setVisible(true);
 					if(userRoleSet.contains(RightsEnum.Tab_Loyalty_VIEW.name()))loyaltyDashTabId.setVisible(true);
 			}
		
		logger.info("getTime zone is >>>>>>>> ::"+sessionScope.getAttribute("clientTimeZone"));
		
		if(sessionScope.getAttribute("clientTimeZone") != null) {
			prepareData();
		}
		
		// set Loyalty and optintel dashboard 
		//loyaltyIncId.setSrc("/zul/LoyaltyDashboard.zul");
		//optintelIncId.setSrc("/zul/OptintelDashboard.zul");
		
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ::" , e);
		}		
		
		
		long endtime = System.currentTimeMillis() - starttime;
		logger.fatal("**PerfTest** Total Time RMHomeController(login)... "+endtime);
		
		
		try {
			//set MyDownlod count
			ExportFileDetailsDao fileExportDetailsDao = (ExportFileDetailsDao) ServiceLocator.getInstance().getDAOByName("exportFileDetailsDao");
			int count = fileExportDetailsDao.findAllCompletedByUserId(user.getUserId());
			myDownloadCountId.setValue(""+count);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	//optIntelDashTabId,loyaltyDashTabId
	public void onClick$optIntelDashTabId() {
		
		//MailingList mlList = mailingListDao.findPOSMailingList(GetUser.getUserObj());
		
		Long listId = mailingListDao.findPOSListIdByUserId(GetUser.getUserObj());
		if(listId == null) {
			//MessageUtil.setMessage("No POS type mailing list exists.", "red");
			MessageUtil.setMessage("No POS type mailing list exists.", "color:blue."); 
			return;
		}	
		//optIntelDashTabId.setVisible(true);
		optintelIncId.setSrc("/zul/OptintelDashboard.zul");
				
	}

	public void onClick$loyaltyDashTabId() {

		//MailingList mlList = mailingListDao.findPOSMailingList(GetUser.getUserObj());
		
		Long listId = mailingListDao.findPOSListIdByUserId(GetUser.getUserObj());
		if(listId == null) {
			//MessageUtil.setMessage("No POS type mailing list exists.", "red");
			MessageUtil.setMessage("No POS type mailing list exists.", "color:blue.");
			return;
		}
		//loyaltyDashTabId.setVisible(false);
		loyaltyIncId.setSrc("/zul/LoyaltyDashboard.zul");
	}
	
	private void updateQuickOverview() {
		try {
			//Active Campaigns
			int activeCount = campaignScheduleDao.getActiveCampaignCount(GetUser.getUserId());
			//activeCampaignsLblId.setValue(""+activeCount);			
			
			  int activeCountSMS =
			  smsCampaignScheduleDao.getSMSCampaignCount(GetUser.getUserId());
			  //activeSmsLblId.setValue(""+activeCountSMS);
			  int campaignCount=(activeCount+activeCountSMS);
			  activeCampaignsLblId.setValue(""+campaignCount);
			 
			
			// Total Subscribers
			//List<MailingList> mlList = mailingListDao.findAllByUser(userIdsSet);
			
			
			/*List<MailingList> mlList = mailingListDao.findByIds(listIdsSet);
			Set<MailingList> mlSet = new HashSet<MailingList>();
			if(mlList != null && mlList.size() > 0) {
				String mlIds="";
				
				for(MailingList ml:mlList) {
					
					if(mlIds.length()>0) mlIds =mlIds+ ",";
					mlIds += ml.getListId();
					mlSet.add(ml);
				}// for
				logger.debug("total mlIds ..."+mlIds);*/
				//long totSize = contactsDao.getAllContactsCount(mlSet);
				long totSize = contactsDao.getAllContactsCount(GetUser.getUserId());
				totalSubscribersLblId.setValue(""+totSize);
				
				
				Calendar tempCal = Calendar.getInstance();
				tempCal.set(Calendar.DATE, 1);
				tempCal.set(Calendar.HOUR,0);
				tempCal.set(Calendar.MINUTE,0);
				tempCal.set(Calendar.SECOND,0);
				
				//Opt-Ins this month
				long optInCount = contactsDao.getOptinContactsCount(GetUser.getUserObj(), tempCal, Calendar.getInstance());
				optInsLblId.setValue(""+optInCount);

				//Opt-Outs this month
				long optOutCount = contactsDao.getOptOutContactsCount(GetUser.getUserId(), tempCal, Calendar.getInstance());
				optOutsLblId.setValue(""+optOutCount);
				
			//} // if
			
			
			// Messages Sent today
			
			long emailSentToday = campaignReportDao.getTodayMessages(GetUser.getUserId());
			long smsSentToday = smsCampaignReportDao.getTodayMessages(GetUser.getUserId());
			
			messageScheduledLblId.setValue(""+ emailSentToday);
			
			unsubBouncedLblId.setValue(""+ smsSentToday);
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	}
	
	
	public void addRepItem(Object obj) {
		Listitem lItem = null;
		Listcell lCell = null;
		long sent;
		
		TimeZone clientTimeZone = (TimeZone)sessionScope.getAttribute("clientTimeZone");
		Calendar tempCal;
		
		CampaignReport campaignReport = null;
		SMSCampaignReport smsCampaignReport =null;
		
		

		
		String campaignType="";
		String CampName="";
		Calendar sentDate=null;
		long sentCount=0;
		
		if(obj instanceof CampaignReport) {
			campaignReport = (CampaignReport)obj;
			
			CampName=campaignReport.getCampaignName();
			campaignType=Constants.TYPE_EMAIL_CAMPAIGN;
			
			sentDate=campaignReport.getSentDate();
			sentCount=campaignReport.getSent();
		}
		else if(obj instanceof SMSCampaignReport) {
			smsCampaignReport = (SMSCampaignReport)obj;
			
			CampName = smsCampaignReport.getSmsCampaignName();
			campaignType = Constants.TYPE_SMS_CAMPAIGN;
			sentDate = smsCampaignReport.getSentDate();
			sentCount = smsCampaignReport.getSent();
		
		}
		
		lItem = new Listitem();
		lItem.setValue(obj);
		lCell = new Listcell(CampName);
		lCell.setTooltiptext(CampName);
		lCell.setStyle("padding-left:10px;");
		lCell.setParent(lItem);
		lCell = new Listcell(campaignType);
		lCell.setParent(lItem);
		
		//tempCal = campaignReport.getSentDate();
//		tempCal.setTimeZone(clientTimeZone);
		lCell = new Listcell(
				MyCalendar.calendarToString(sentDate, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
		lCell.setParent(lItem);
		
	//	sent = campaignReport.getSent();
		lCell = new Listcell(""+sentCount);
		lCell.setParent(lItem);
		
		lCell = new Listcell();
		Toolbarbutton tb = new Toolbarbutton();
		tb.setImage("/img/theme/home/reports_icon.png");
		tb.setParent(lCell);
		tb.setStyle("cursor:pointer;");
		lCell.setParent(lItem);
		lItem.setParent(recentCampListLbId);
		
		tb.addEventListener("onClick", new EventListener() {
			
			public void onEvent(Event event) {
				Listitem li = (Listitem)(event.getTarget().getParent().getParent());
				//Redirect.goTo("report/detailedReport?cr="+(CampaignReport)li.getValue());

				if(li.getValue() instanceof CampaignReport) {
					Session sessionScope = Sessions.getCurrent();
					sessionScope.removeAttribute("campaignReport");
					CampaignReport cr = (CampaignReport)li.getValue();
					if(cr.getSent() == 0) {
						
						MessageUtil.setMessage("Detailed report can not be viewed as sent count is :0. ", "color:green;");
						return;
						
						
					}
					sessionScope.setAttribute("campaignReport",cr);
					Redirect.goTo(PageListEnum.REPORT_DETAILED_REPORT);
					
				
				}
				else if(li.getValue() instanceof SMSCampaignReport){
					Session sessionScope = Sessions.getCurrent();
					sessionScope.removeAttribute("smsCampaignReport");
					SMSCampaignReport cr = (SMSCampaignReport)li.getValue();
					if(cr.getSent() == 0) {
						
						MessageUtil.setMessage("SMS detailed report can not be viewed as sent count is :0.", "color:green;");
						return;
						
						
					}
					sessionScope.setAttribute("smsCampaignReport",cr);
					Redirect.goTo(PageListEnum.REPORT_DETAILED_SMS_REPORTS);
					
					
					/*sessionScope.setAttribute("smsCampaignReport", li.getValue());
					Redirect.goTo("report/DetailedSmsCampaignReport");
					logger.info("Need to redirect to SMS reports");*/
				}
				else {
					Session sessionScope = Sessions.getCurrent();
					sessionScope.removeAttribute("waCampaignReport");
					WACampaignReport cr = (WACampaignReport)li.getValue();
					if(cr.getSent() == 0) {
						
						MessageUtil.setMessage("WA detailed report can not be viewed as sent count is :0.", "color:green;");
						return;
						
						
					}
					sessionScope.setAttribute("waCampaignReport",cr);
					Redirect.goTo(PageListEnum.REPORT_DETAILED_WA_REPORTS);
				}
			}}
		);
		
		
		
		
	}
	
	public void prepareData() {
		try{
			List recentCampList = campaignReportDao.getRecentCampignReportList(user.getUserId());
			List recentSmsCampList=smsCampaignReportDao.getRecentSmsCampignReportList(user.getUserId());
			
			
		/*	List recentTotList= new ArrayList();
			recentTotList.add(recentCampList);
			recentTotList.add(recentSmsCampList);
			*/
			
			TreeMap<String , Object> tmap= new TreeMap<String, Object>();
			
			TreeMap<String , List> dupCampTmap= new TreeMap<String, List>();
			
			
			// Add email's to treemap
			for (Object obj : recentCampList) {
				
				String tempStr= MyCalendar.calendarToString(((CampaignReport)obj).getSentDate(),MyCalendar.FORMAT_DATETIME_STYEAR);
				
				if(!tmap.containsKey(tempStr)) {
					tmap.put(tempStr, obj);
					
				}
				else if(tmap.containsKey(tempStr)){
					
						
						List campReportList = dupCampTmap.get(tempStr);
						if(campReportList == null) {
							
							campReportList = new ArrayList();
							
						}
						
						campReportList.add(obj);
					
					dupCampTmap.put(tempStr, campReportList);
				}
			}//Email
			
			//add sms to treemap
			for (Object obj : recentSmsCampList) {
				
				String tempStr= MyCalendar.calendarToString(((SMSCampaignReport)obj).getSentDate(),MyCalendar.FORMAT_DATETIME_STYEAR);
				
				if(!tmap.containsKey(tempStr)) {
					tmap.put(tempStr, obj);
					
				}
				
				else if(tmap.containsKey(tempStr)){
					
						List smsReportList = dupCampTmap.get(tempStr);
						if(smsReportList == null) {
							
							smsReportList = new ArrayList();
							
						}
						
						smsReportList.add(obj);
					
					dupCampTmap.put(tempStr, smsReportList);
				}
				
			}//sms
			 NavigableSet<String> nset=tmap.descendingKeySet();
			
		      
			logger.info("tmap="+tmap);
			logger.info("after descending :"+nset);
			
			
			for(String eachStr : nset) {
				try {
					if(recentCampListLbId.getItemCount()>=7) break;
					Object obj = tmap.get(eachStr);
					
			if(dupCampTmap.containsKey(eachStr)) {
						
						List list = dupCampTmap.get(eachStr);
						for (Object object : list) {
							//if(! obj.getClass().getName().equals(object.getClass().getName())){
							
								addRepItem(object);
							
							//}
							
						}//for
						
					}
					
					addRepItem(obj);
					
					
				} catch (Exception e) {
					logger.error(" ** Exception : ",(Throwable)e);
				}
			} // for
		
			// recentCampListLbId.invalidate();
		}catch(Exception e){
			logger.error("Exception ::" , e);
		}
	}
	
	/*
	public void init(Listbox recentCampListLbId , Label mainMsgsCountId) {
		this.recentCampListLbId = recentCampListLbId;
		this.mainMsgsCountId = mainMsgsCountId;
		PageUtil.setHeader("Welcome, " + userName + "!", "", "font-size:15px;color:#2C7095;font-family:verdana;marg", true);
		this.mainMsgsCountId.setValue("" + (new MessageHandler()).getNewMsgsCount(user.getUserId()));
		if(sessionScope.getAttribute("clientTimeZone") != null) prepareData();
	}

*/
	
	/*public void onClick$creatEmailAId() {
		try {
			Redirect.goTo("campaign/CampCreateIndex");
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // onClick$creatEmailAId

	public void onClick$addContactAId() {
		try {
			Redirect.goTo("contact/upload");
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // onClick$addContactListItemId
	public void onClick$viewReportListItemId() {
		try {
			Redirect.goTo("report/Report");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	} // onClick$viewReportListItemId
	public void onClick$messagesAId() {
		try {
			Redirect.goTo("message/messages");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	} // onClick$messagesAId
	
	public void onClick$viewEmailAId() {
		try {
			Redirect.goTo("campaign/View");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	} // onClick$viewEmailBtnId
	
	public void onClick$viewReportAId() {
		try {
			Redirect.goTo("report/Report");
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // 
	
	public void onClick$creatTriggerAId() {
		try {
			Redirect.goTo("campaign/EventTriggerEmail");
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // 
	
	public void onClick$allListsAId() {
		try {
			Redirect.goTo("contact/myLists");
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // 
	
	public void onClick$allSegmentsAId() {
		try {
			Redirect.goTo("contact/viewSegments");
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // 
	
	public void onClick$createSegmentAId() {
		try {
			Redirect.goTo("contact/manageSegments");
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // 
	

	public void onClick$userAccountsAId() {
		try {
			Redirect.goTo("useradmin/manageUsers");
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // 
	
	public void onClick$accountSettingsAId() {
		try {
			Redirect.goTo("myAccount/userDetails");
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // 
	
	public void onClick$quickOverviewAId() {
		updateQuickOverview();
	} // 

	*/
	
	//------------------
	public void onClick$creatEmailAId() {
		try {
			Redirect.goTo(PageListEnum.CAMPAIGN_CREATE);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // onClick$creatEmailAId

	public void onClick$creatSMSAId() {
		try {
			if(isOptedForSMS())Redirect.goTo(PageListEnum.SMS_CAMP_CREATE_iNDEX);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // onClick$addContactListItemId
	public void onClick$viewSMSAId() {
		try {
			//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW_SMS_CAMPAIGNS);
			if(isOptedForSMS())Redirect.goTo(PageListEnum.CAMPAIGN_SMSCAMPAIGN_LIST);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	} // onClick$viewReportListItemId
	public void onClick$messagesAId() {
		try {
			Redirect.goTo(PageListEnum.MESSAGE_VIEW);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	} // onClick$messagesAId
	
	public void onClick$viewEmailAId() {
		try {
			//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW);
			Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	} // onClick$viewEmailBtnId
	//--------------------------------------
	
	/**
	 * contacts
	 */
	public void onClick$addContactAId() {
		try {
			Redirect.goTo(PageListEnum.CONTACT_UPLOAD);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // onClick$addContactListItemId
	
	
	public void onClick$allListsAId() {
		try {
			Redirect.goTo(PageListEnum.CONTACT_LIST_VIEW);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // 
	
	public void onClick$allSegmentsAId() {
		try {
			Redirect.goTo(PageListEnum.CONTACT_VIEW_SEGMENTS);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // 
	
	public void onClick$createSegmentAId() {
		try {
			Redirect.goTo(PageListEnum.CONTACT_MANAGE_SEGMENTS);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // 
	
	
	//-----------------------------------------
	/**
	 * reports
	 */
	
	public void onClick$emailCampReportsAId() {
		try {
			Redirect.goTo(PageListEnum.REPORT_REPORT);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // 
	public void onClick$SMSCampReportsAId() {
		try {
			if(isOptedForSMS())Redirect.goTo(PageListEnum.REPORT_SMS_CAMPAIGN_REPORTS);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}}
		public void onClick$WACampReportsAId() {
			try {
				if(isOptedForWA())Redirect.goTo(PageListEnum.REPORT_WA_CAMPAIGN_REPORTS);
			} catch (Exception e) {
				logger.error("Exception ::" , e);
			}
	} // 
	public void onClick$SMSKeywordReportsAId() {
		try {
			if(isOptedForSMS() && isOptedForSMSKeywords())Redirect.goTo(PageListEnum.REPORT_SMS_KEYWORD_REPORTS);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // 
	public void onClick$optintelReportsAId() {
		try {
			Redirect.goTo(PageListEnum.REPORT_OPTINTEL_REPORTS);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // 
	
	public boolean isOptedForSMS() {
		//logger.debug("current USer ::"+currentUser);
		
		if(user == null) user = GetUser.getUserObj();
		
		if(!user.isEnableSMS()) {
			
			MessageUtil.setMessage(SMS_MSG_TEXT, "color:red;");
			return false;
		}
	
		return true;
	}
	public boolean isOptedForWA() {
		//logger.debug("current USer ::"+currentUser);
		
		if(user == null) user = GetUser.getUserObj();
		
        	if(!user.isEnableWA()) {
			
			MessageUtil.setMessage(WA_MSG_TEXT, "color:red;");
			return false;
		}
	
		return true;
	}
	public boolean isOptedForSMSKeywords() {
		//logger.debug("current USer ::"+currentUser);
		
		if(user == null) user = GetUser.getUserObj();
	
		CountryReceivingNumbersDao countryReceivingNumbersDao =(CountryReceivingNumbersDao)SpringUtil.getBean("countryReceivingNumbersDao");
		List<CountryReceivingNumbers> recevingNumList = countryReceivingNumbersDao.findBy(user.getCountryType());//getReceivingNumByCountry(countryMap.get(country), typeMap.get(type));
		if(recevingNumList == null) {
			MessageUtil.setMessage(SMS_MSG_KEYWORD_TEXT, "color:red;");
			return false;
		}
		return true;
	}
	

	//----------------------------------
	
	/**
	 * others
	 */
	
	

	public void onClick$userAccountsAId() {
		try {
			Redirect.goTo(PageListEnum.USERADMIN_MANAGE_USER);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // 
	
	public void onClick$faqAId() {
		try {
			Redirect.goTo(PageListEnum.USERADMIN_FAQ);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	}
	
	public void onClick$termsAId() {
		try {
			Redirect.goTo(PageListEnum.USERADMIN_FAQ);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	}
	
	public void onClick$accountSettingsAId() {
		try {
			Redirect.goTo(PageListEnum.MYACCOUNT_USER_DETAILS);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // 
	public void onClick$manageAutoEmailsAId() {
		try {
			Redirect.goTo(PageListEnum.CONTACT_MANAGE_AUTO_EMAILS);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // 
	public void onClick$manageKeywordAId() {
		try {
			if(isOptedForSMS() && isOptedForSMSKeywords())Redirect.goTo(PageListEnum.SMS_CAMP_SETUP);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // 
	
	
	
	public void onClick$quickOverviewAId() {
		updateQuickOverview();
	}
	
	public void onClick$downloadAId(){
		Redirect.goTo(PageListEnum.CONTACT_FILE_DOWNLOAD);
	}
	
}


