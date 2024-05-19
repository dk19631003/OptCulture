package org.mq.marketer.campaign.controller.admin;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SecRoles;
import org.mq.marketer.campaign.beans.TransactionalTemplates;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.UserSMSSenderId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.CouponCodesDaoForDML;
import org.mq.marketer.campaign.dao.DRSentDao;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDao;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDaoForDML;
import org.mq.marketer.campaign.dao.SecRolesDao;
import org.mq.marketer.campaign.dao.SuppressedContactsDao;
import org.mq.marketer.campaign.dao.SuppressedContactsDaoForDML;
import org.mq.marketer.campaign.dao.TransactionalTemplatesDao;
import org.mq.marketer.campaign.dao.TransactionalTemplatesDaoForDML;
import org.mq.marketer.campaign.dao.UnsubscribesDaoForDML;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.dao.UserSMSSenderIdDao;
import org.mq.marketer.campaign.dao.UserSMSSenderIdDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.LRUCache;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.UserHelper;
import org.mq.optculture.timer.CheckEmailSmsCreditThread;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

public class EditUserController extends GenericForwardComposer {

	Combobox vmtaCbId;
	Textbox reNewPass;
	Textbox newPass;
	Textbox promoTxtBxId,emailTxtBxId,phnTxtBxId;
	Button disableUserBtnId, deleteUserBtnId, editUserBtnId,updatePromoBtnId,deleteEmailId;
	MyDatebox startDbId;
	MyDatebox expDbId;
	Intbox totalLimitLbId;
	Label vmtaStatusLblId;
	Label responseLblId;
	Label pwdLblId;
	Checkbox enabledCbId ,enableBillingAdminChkBoxId,crossProgramCardTransferChkBxId, suspendedProgramTransferChkBxId;
	Div pwdTdId , enableBillingAdminDivId;
	Window editUserWinId;
	Listbox promoStatusLbId;
	private  Users user = null;
	private UsersDao usersDao;
	private UsersDaoForDML usersDaoForDML;
	private DRSentDao drSentDao;
	private ContactsDao contactsDao;
	private UserActivitiesDao userActivitiesDao;
	private Label smsSenderIdsLblId;
	private Toolbarbutton clickHereTlbId;
	private Combobox approveTempCmbBoxId,uaeApproveTempCmbBoxId;
	private TransactionalTemplatesDao  transactionalTemplatesDao;
	private TransactionalTemplatesDaoForDML  transactionalTemplatesDaoForDML;
	private UserSMSSenderIdDao userSMSSenderIdDao;
	private UserSMSSenderIdDaoForDML userSMSSenderIdDaoForDML;
	private SecRolesDao secRolesDao;
	private Button updateEmailCreditBtnId,updateEmailCreditApprove;
	private Window emailUpdateCredit;
	private Textbox emailUpdateCredit$updateEmailCreditText;
	private Label totalEmailCreditLabelId,usedEmailCreditLabelId,availableEmailCreditLabelId;
	private A totalEmailCreditId,usedEmailCreditId,availableEmailCreditId;
	private UserActivitiesDaoForDML userActivitiesDaoForDML;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public EditUserController() {
		logger.info("Default constructor.");
		this.usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		this.usersDaoForDML = (UsersDaoForDML)SpringUtil.getBean("usersDaoForDML");
		contactsDao=(ContactsDao) SpringUtil.getBean("contactsDao");
		drSentDao = (DRSentDao) SpringUtil.getBean("drSentDao");
		userActivitiesDao=(UserActivitiesDao) SpringUtil.getBean("userActivitiesDao");
		transactionalTemplatesDao = (TransactionalTemplatesDao)SpringUtil.getBean("transactionalTemplatesDao");
		transactionalTemplatesDaoForDML = (TransactionalTemplatesDaoForDML)SpringUtil.getBean("transactionalTemplatesDaoForDML");
		userSMSSenderIdDao = (UserSMSSenderIdDao)SpringUtil.getBean("userSMSSenderIdDao");
		userSMSSenderIdDaoForDML = (UserSMSSenderIdDaoForDML)SpringUtil.getBean("userSMSSenderIdDaoForDML");
		secRolesDao =(SecRolesDao)SpringUtil.getBean("secRolesDao");
		userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		try {
			user =(Users)getUser();
			if(user!= null) {
				
				logger.info("user details"+user.getCompanyName());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception  ::", e);
		}
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		
		super.doAfterCompose(comp);
		
		logger.debug("-- Just Entered -- ");
		
		
	/*	user = (Users)sessionScope.get("editUserObj");

		VmtaDao vmtaDao = (VmtaDao)SpringUtil.getBean("vmtaDao");
		List<Vmta> vmtaList = vmtaDao.findAll();
		
		logger.debug("Got vmta List of size :" + vmtaList.size());
		
		Comboitem ci;
		ci = new Comboitem("--Select VMTA--");
		ci.setDescription(" ");
		ci.setParent(vmtaCbId);
		
		for (Vmta vmta : vmtaList) {
			ci = new Comboitem(vmta.getVmtaName());
			ci.setDescription(vmta.getDescription());
			if(vmta.getStatus().equalsIgnoreCase("good")) {
				ci.setImage("/img/vmta/goodVmta.JPG");
			} else if(vmta.getStatus().equalsIgnoreCase("bad")) {
				ci.setImage("/img/vmta/badVmta.JPG");
			} else if(vmta.getStatus().equalsIgnoreCase("average")) {
				ci.setImage("/img/vmta/avgVmta.png");
			}
			ci.setValue(vmta.getStatus());
			ci.setParent(vmtaCbId);
			
			if(user.getVmta().equalsIgnoreCase(vmta.getVmtaName())) {
				vmtaCbId.setSelectedItem(ci);
				vmtaStatusLblId.setValue(vmta.getStatus());
			} //if
		} //for
		
		if(vmtaCbId.getSelectedIndex() <= 0)
			vmtaCbId.setSelectedIndex(0);*/
//		user = (Users)sessionScope.get("editUserObj");
		
		// enable billing Admin
		
		List<SecRoles> userRoles = secRolesDao.findByUserId(user.getUserId());
		SecRoles tempRole = null;
		logger.info("userRoles="+userRoles);
		
		if(userRoles==null || userRoles.isEmpty()) return;
		
		Iterator<SecRoles> rolesIt = userRoles.iterator();
		boolean isSuperUser = false;
	
		boolean isAlreadyEnabled = false;
		while(rolesIt.hasNext()) {
			tempRole = rolesIt.next();
			//System.out.println("role name is::;"+tempRole.getName());
			if(tempRole.getName().equals(Constants.USER_ROLE_OCADMIN)  || tempRole.getName().equals(Constants.USER_ROLE_CUSTOM_USER ) || 
											tempRole.getName().equals(Constants.USER_ROLE_SUPER_USER)) {
				isSuperUser = true;
				break;
			}
			//if(tempRole.getName().equals(Constants.ROLE_USER_BILLING_ADMIN))billingRoleName = true;
			
		} // while
		
		while(rolesIt.hasNext()) {
			tempRole = rolesIt.next();
		//	System.out.println("role name is::;"+tempRole.getName());
			if(isSuperUser && tempRole.getName().equals(Constants.ROLE_USER_BILLING_ADMIN) ) {
				
				 isAlreadyEnabled = true;
				 break;
			}
											
			
		} // while
		
		enableBillingAdminDivId.setVisible(isSuperUser);
		if(enableBillingAdminDivId.isVisible()) {
			enableBillingAdminChkBoxId.setChecked(isSuperUser && isAlreadyEnabled);
			enableBillingAdminChkBoxId.setDisabled(enableBillingAdminChkBoxId.isChecked());
		}
		// added for enabling cross program card transfer
		UserOrganization userOrg = usersDao.findByOrgId(user.getUserOrganization().getUserOrgId());
		crossProgramCardTransferChkBxId.setChecked(userOrg.isCrossProgramCardTransfer());
		suspendedProgramTransferChkBxId.setChecked(userOrg.isSuspendedProgramTransfer());
		//onCheck$enableBillingAdminChkBoxId();
		
		String senderIds = "";
	    List<String> senderIdLst = usersDao.getSenderIdByUserName(user.getUserName());
	    logger.info("senderIdLst====>"+senderIdLst.size());
	    
	    for(String senderId : senderIdLst) {
	    	
	    	if(senderIds.length()>0) senderIds += ",";
	    	senderIds += senderId;
	    	
	    	
	    }
		
	    smsSenderIdsLblId.setValue(senderIds);
	    
	    Calendar tempCal = user.getPackageExpiryDate();
		tempCal.setTimeZone((TimeZone) sessionScope.get("clientTimeZone"));
		expLblId.setValue(MyCalendar.calendarToString(tempCal,MyCalendar.FORMAT_STDATE));
		
		tempCal = user.getPackageStartDate();
		tempCal.setTimeZone((TimeZone) sessionScope.get("clientTimeZone"));
		startLblId.setValue(MyCalendar.calendarToString(tempCal,MyCalendar.FORMAT_STDATE));
		
		
		//set TransactionalSMS check
		if(user.getMsgChkType() != null){
			
			transationalSMSChkBxId.setChecked(user.getMsgChkType().equals("TR"));
		}
		
		//set Transactional Sender Id
		if(senderIds != null && senderIds.trim().length() > 0)
							senderIdTxtBxId.setValue(senderIds);
		
		
		//set TransactionalTemplates if any
		logger.debug("user.getUserId() please  ::"+user.getUserId());
		List<TransactionalTemplates>  templateList = transactionalTemplatesDao.findTemplatesByOrgId(user.getUserOrganization().getUserOrgId());
		Comboitem combItem = null;
		combItem = new Comboitem("--select--");
		combItem.setParent(approveTempCmbBoxId);
		
		if(templateList != null && templateList.size() >0) {
			
			for (TransactionalTemplates eachObj : templateList) {
				combItem = new Comboitem(eachObj.getTemplateName());
				combItem.setDescription(eachObj.getStatus() == 0? "Pending" : "Approved");
				combItem.setValue(eachObj);
				combItem.setParent(approveTempCmbBoxId);
			}
		}
		
		approveTempCmbBoxId.setSelectedIndex(0);
		if(user.getMsgChkType() != null){
			
			uaeTransationalSMSChkBxId.setChecked(user.getMsgChkType().equals("TR"));
		}
		
		//set Transactional Sender Id
		if(senderIds != null && senderIds.trim().length() > 0)
							uaeSenderIdTxtBxId.setValue(senderIds);
		
		
		//set TransactionalTemplates if any
		logger.debug("user.getUserId() please  ::"+user.getUserId());
		List<TransactionalTemplates>  uaeTemplateList = transactionalTemplatesDao.findTemplatesByOrgId(user.getUserOrganization().getUserOrgId());
		Comboitem uaeCombItem = null;
		uaeCombItem = new Comboitem("--select--");
		uaeCombItem.setParent(uaeApproveTempCmbBoxId);
		
		if(uaeTemplateList != null && uaeTemplateList.size() >0) {
			
			for (TransactionalTemplates eachObj : uaeTemplateList) {
				uaeCombItem = new Comboitem(eachObj.getTemplateName());
				uaeCombItem.setDescription(eachObj.getStatus() == 0? "Pending" : "Approved");
				uaeCombItem.setValue(eachObj);
				uaeCombItem.setParent(uaeApproveTempCmbBoxId);
			}
		}
		
		uaeApproveTempCmbBoxId.setSelectedIndex(0);
		
		//set SMSGate way type
		String smsGateWayStr = "";
		if(user.getCountryType()!= null && user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA)) smsGateWayStr =  "IN";
		else if (user.getCountryType()!= null &&  user.getCountryType().equals(Constants.SMS_COUNTRY_US)) smsGateWayStr =  "US";
		smsGatewayId.setValue(smsGateWayStr);
		
		//Set SMS Country Code
		smsCountryCodeLblId.setValue(user.getCountryCarrier() != null ? ""+user.getCountryCarrier(): "");
		
		//drSent
		Long userId=user.getUserId();
		Long count = drSentDao.findTotDRSentCount(userId);
		totalDRSentId.setValue(count.toString());
		
		//lastaccountactivity	
		
		Calendar date=userActivitiesDao.findLastUserActivityDate(userId);
		lastAccountActivityDateLbId.setValue(MyCalendar.calendarToString(date,MyCalendar.FORMAT_DATETIME_STYEAR));
		
		//totaluniqcontacts
		Long uniqueCount=contactsDao.findUniqActiveContacts(userId);
		totalNoOfUniqueActiveContactsLbId.setValue(uniqueCount.toString());
		
		if(!user.isEnabled())
		{
			disableUserBtnId.setLabel("Enable User");
		}
		//checkThreshold();
	} //doAfterCompose
	private Label expLblId,startLblId,smsGatewayId,smsCountryCodeLblId,totalDRSentId,lastAccountActivityDateLbId,totalNoOfUniqueActiveContactsLbId;
	
	public Users getUser(){
		user = (Users)Sessions.getCurrent().getAttribute("editUserObj");
		return user;
	}
	public void onCheck$enableBillingAdminChkBoxId(){
		
		try {
			List<SecRoles> userRoles = secRolesDao.findByUserId(user.getUserId());
			Set<SecRoles> setRolesSet = new HashSet<SecRoles>();
			//setRolesSet.add(userRoles);
			
			SecRoles biilingRole = secRolesDao.findBy(Constants.ROLE_USER_BILLING_ADMIN);
			if(enableBillingAdminDivId.isVisible() && enableBillingAdminChkBoxId.isChecked()){
				
				if( Messagebox.show("Are you sure you want to add Billing Admin role for user? ","Confirm",
						Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK ){
				for (SecRoles secRoles : userRoles) {
					
					if(secRoles.getName().equals(biilingRole.getName()))continue;
					setRolesSet.add(secRoles);
					
				}
				
				if(!setRolesSet.contains(biilingRole)){
					setRolesSet.add(biilingRole);
					UserHelper userHelper = new UserHelper();
					userHelper.createBillingProfile(user);
				}
				enableBillingAdminChkBoxId.setDisabled(enableBillingAdminChkBoxId.isChecked());
				user.setRoles(setRolesSet);
				//usersDao.saveOrUpdate(user);
				usersDaoForDML.saveOrUpdate(user);
				
				}	
			}/*else{
				if( Messagebox.show("Are you sure you want to delete Billing Admin role for user? ","Confirm",
						Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK ){
				
				for (SecRoles secRoles : userRoles) {
					
					if(secRoles.getName().equals(biilingRole.getName())){
						
						if(setRolesSet.contains(secRoles))setRolesSet.remove(secRoles);
					}else{
						
						setRolesSet.add(secRoles);
					}
					
				}
			}
				
			}*/
			user.setRoles(setRolesSet);
			//usersDao.saveOrUpdate(user);
			usersDaoForDML.saveOrUpdate(user);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

	/*public void onCheck$submitCbId() {
			try {
				logger.debug("--Just Entered--");
				int confirm = Messagebox.show("Are you sure you want to update the user?","Update User", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION); 
				if(confirm != Messagebox.OK) {
					return;
				}
				
				user.setEnabled(enabledCbId.isChecked());
				
				Calendar startDt = startDbId.getServerValue();
				Calendar expDt = expDbId.getServerValue();
				user.setPackageExpiryDate(startDt);
				user.setPackageStartDate(expDt);
				
				if(vmtaCbId.getSelectedIndex() > 0 ) {
					user.setVmta(vmtaCbId.getSelectedItem().getLabel());
				}else if(user.isEnabled()) {
					MessageUtil.setMessage("Select VMTA for enabled user.", "color:red", "TOP");
					return;
				}
				
				user.setEmailCount(totalLimitLbId.getValue());
				
				
				UsersDao usersDao = (UsersDao) SpringUtil.getBean("usersDao");
				try {
					usersDao.saveOrUpdate(user);
					MessageUtil.setMessage("User updated successfully.", "color:blue", "TOP");
				} catch (Exception e) {
					MessageUtil.setMessage("Problem while updating the user.", "color:red", "TOP");
					logger.error("** Exception : Problem while updating the user" , e);
				}
			} catch (WrongValueException e) {
				logger.error("** Exception : " ,e );
			} catch (InterruptedException e) {
				logger.error("** Exception : " ,e );
			} catch (Exception e) {
				logger.error("** Exception : " ,e );
				MessageUtil.setMessage("Problem while updating user.", "color:red", "TOP");
			}
			
			logger.debug("--Exit--");
			
	}
	*/
	public void onCheck$crossProgramCardTransferChkBxId(){
		try{
			String qry = "update UserOrganization set crossProgramCardTransfer = "+crossProgramCardTransferChkBxId.isChecked()+" where userOrgId = "+user.getUserOrganization().getUserOrgId();
			//usersDao.executeUpdate(qry);
			logger.info("crossProgramCardTransferChkBxId Query :"+qry);
			
			UsersDaoForDML usersDaoForDML = (UsersDaoForDML) SpringUtil.getBean("usersDaoForDML");
			usersDaoForDML.executeUpdate(qry);
			
		}
		catch(Exception e){
			logger.info("Exception in setting value in cross program card transfer :::::: "+e);
		}
		
	}
	public void onCheck$suspendedProgramTransferChkBxId(){
		try{
			String qry = "update UserOrganization set suspendedProgramTransfer = "+suspendedProgramTransferChkBxId.isChecked()+" where userOrgId = "+user.getUserOrganization().getUserOrgId();
			//usersDao.executeUpdate(qry);
			logger.info("suspendedProgramTransferChkBxId Query :"+qry);
			
			UsersDaoForDML usersDaoForDML = (UsersDaoForDML) SpringUtil.getBean("usersDaoForDML");
			usersDaoForDML.executeUpdate(qry);
			
		}
		catch(Exception e){
			logger.info("Exception in setting value in cross program card transfer :::::: "+e);
		}
		
	}
	public void onClick$updatePwdBtnId() {
		
		MessageUtil.clearMessage();
		String newPwdStr = newPass.getValue();
		String reNewPwdStr = reNewPass.getValue();
		
		if(newPwdStr.trim().equals("")) {
			MessageUtil.setMessage("New Password field cannot be left empty.", "color:red","TOP"); 
			 newPass.setFocus(true);
			 return;
		}
		if(reNewPwdStr.trim().equals("")) {
			MessageUtil.setMessage("Retype Password field cannot be left empty.", "color:red","TOP");
			reNewPass.setFocus(true);
			return;
		}
		
		if(newPwdStr ==null || newPwdStr.trim().length() <= 3 
				|| reNewPwdStr ==null || reNewPwdStr.trim().length() <= 3) {
			responseLblId.setValue("Password must be greater than 3 characters");
			return;
		}
		
		newPwdStr = newPwdStr.trim();
		reNewPwdStr = reNewPwdStr.trim();
		String newPwdHash = Utility.encryptPassword(user.getUserName(), newPwdStr);
		/*Md5PasswordEncoder md5 = new Md5PasswordEncoder();
		String newPwdHash = md5.encodePassword(newPwdStr,user.getUserName());*/
		
		if( !newPwdStr.equals(reNewPwdStr) ) {
			responseLblId.setValue("Two password must be same");
			return;
		} //try
		
		responseLblId.setValue("");
		
		user.setPassword(newPwdHash);
		user.setMandatoryUpdatePwdOn(Calendar.getInstance());
		UsersDao usersDao = (UsersDao) SpringUtil.getBean("usersDao");
		UsersDaoForDML usersDaoForDML = (UsersDaoForDML) SpringUtil.getBean("usersDaoForDML");
		try {
			//usersDao.saveOrUpdate(user);
			usersDaoForDML.saveOrUpdate(user);
			//pwdLblId.setValue(newPwdStr);
			pwdTdId.setVisible(false);
			MessageUtil.setMessage("Password updated successfully.", "color:blue", "TOP");
		} catch (Exception e) {
			logger.error("** Exception : Problem while upadating the password" ,e );
			MessageUtil.setMessage("Problem experienced while updating the password.", "color:red", "TOP");
		}
		
		logger.debug("--Exit--");
		
	}
	public void onClick$disableUserBtnId() {
		try {
			logger.debug("--Just Entered--");
			MessageUtil.clearMessage();
			String msg=user.isEnabled()?"disable":"enable";
			String msgHeader=user.isEnabled()?"Disable User":"Enable User";
					
			try {
				
				int confirm = Messagebox.show("Are you sure you want to "+msg+" the user : " + user.getUserName() + "?",
						msgHeader, Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION); 
				if(confirm != Messagebox.OK) {
					return;
				}
				
				UsersDao usersDao = (UsersDao) SpringUtil.getBean("usersDao");
				UsersDaoForDML usersDaoForDML = (UsersDaoForDML) SpringUtil.getBean("usersDaoForDML");
				user.setEnabled(!user.isEnabled());				
				//usersDao.saveOrUpdate(user);
				usersDaoForDML.saveOrUpdate(user);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception  ::", e);
			}
			
			try {
				Messagebox.show("User "+msg +"d successfully.", "Information", Messagebox.OK, Messagebox.INFORMATION);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception  ::", e);
			}
			Redirect.goTo(PageListEnum.EMPTY);
			Redirect.goTo(PageListEnum.ADMIN_LIST_USERS);
			//Executions.getCurrent().getDesktop().invalidate();
		} catch (Exception e) {
			logger.error("Exception  ::", e);
		}
		
}
	
	public void onClick$deleteUserBtnId() {
			try {
				logger.debug("--Just Entered--");
				MessageUtil.clearMessage();

				if(GetUser.getUserId() == user.getUserId() ) {
					MessageUtil.setMessage("Cannot delete a currently logged-in user.", "color:red", "TOP");
					return;
				}
				
				try {
					int confirm = Messagebox.show("Are you sure you want to delete the user : " + user.getUserName() + "?",
							"Delete User", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION); 
					if(confirm != Messagebox.OK) {
						return;
					}
					
					UsersDao usersDao = (UsersDao) SpringUtil.getBean("usersDao");
					UsersDaoForDML usersDaoForDML = (UsersDaoForDML) SpringUtil.getBean("usersDaoForDML");
					
					
					//usersDao.delete(user);
					usersDaoForDML.delete(user);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception  ::", e);
				}
				
				try {
					Messagebox.show("User deleted successfully.", "Information", Messagebox.OK, Messagebox.INFORMATION);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception  ::", e);
				}
				Redirect.goTo(PageListEnum.EMPTY);
				Redirect.goTo(PageListEnum.ADMIN_LIST_USERS);
				//Executions.getCurrent().getDesktop().invalidate();
			} catch (Exception e) {
				logger.error("Exception  ::", e);
			}
			
	}
	
	public void onClick$editUserBtnId() {
		//((Include)editUserWinId.getParent()).setSrc("/zul/admin/createUser.zul?type=edit");
		
		
		
		Executions.getCurrent().setAttribute("type", "edit");
		Redirect.goTo(PageListEnum.ADMIN_CREATE_USER);
		
		
		
	}
	
	public void onClick$clickHereTlbId() throws Exception {
		Redirect.goTo(PageListEnum.ADMIN_USER_SMTP);
	}
	
	public void onClick$resetBtnId() {
			newPass.setValue("");
			reNewPass.setValue("");
	}
	
	private Checkbox transationalSMSChkBxId,uaeTransationalSMSChkBxId;
	private Textbox senderIdTxtBxId,uaeSenderIdTxtBxId;
	public void onClick$smsSettingsSaveBtnId() {
		if(!transationalSMSChkBxId.isChecked()) {
			MessageUtil.setMessage("Please enable transactional SMS.", "red", "top");
			return;
		}
		else if(senderIdTxtBxId.getValue().trim().length() == 0 ) {
			MessageUtil.setMessage("Please provide a valid sender ID.", "red", "top");
			return;
		}
		
		List<UserSMSSenderId> senderIdList = userSMSSenderIdDao.findByUserId(user.getUserId());
		UserSMSSenderId userSMSSenderIdObj = null;
		
		if(senderIdList != null && senderIdList.size() > 0){
			for (UserSMSSenderId userSMSSenderId : senderIdList) {
				
				if(senderIdTxtBxId.getValue().trim().equals(userSMSSenderId.getSenderId())) {
					userSMSSenderIdObj = userSMSSenderId;
					break;
				}else if(userSMSSenderId.getSmsType() != null &&  userSMSSenderId.getSmsType().equals("TR")) {
					userSMSSenderIdObj = userSMSSenderId;
					break;
				}
			}
		}
			
		
		
		logger.info("user details >> "+user.getUserName());
		if(transationalSMSChkBxId.isChecked()){
			user.setMsgChkType("TR");
			user.setUserSMSTool(Constants.USER_SMSTOOL_MVAYOO);
		}else{
			user.setMsgChkType(null);
		}
		//usersDao.saveOrUpdate(user);
		usersDaoForDML.saveOrUpdate(user);
		
		if(userSMSSenderIdObj == null) {
			userSMSSenderIdObj = new UserSMSSenderId();
			userSMSSenderIdObj.setUserName(user.getUserName());
			userSMSSenderIdObj.setUserId(user.getUserId());
		}
		
		if(transationalSMSChkBxId.isChecked()){
			userSMSSenderIdObj.setSmsType("TR");
		}else {
			userSMSSenderIdObj.setSmsType(null);
		}
		userSMSSenderIdObj.setSenderId(senderIdTxtBxId.getValue());
		//userSMSSenderIdDao.saveOrUpdate(userSMSSenderIdObj);
		userSMSSenderIdDaoForDML.saveOrUpdate(userSMSSenderIdObj);
		
		
		MessageUtil.setMessage("SMS settings saved successfully.", "color:green;");
		
	}
	public void onClick$uaeSmsSettingsSaveBtnId() {
		if(!uaeTransationalSMSChkBxId.isChecked()) {
			MessageUtil.setMessage("Please enable transactional SMS.", "red", "top");
			return;
		}
		else if(uaeSenderIdTxtBxId.getValue().trim().length() == 0 ) {
			MessageUtil.setMessage("Please provide a valid sender ID.", "red", "top");
			return;
		}
		
		List<UserSMSSenderId> senderIdList = userSMSSenderIdDao.findByUserId(user.getUserId());
		UserSMSSenderId userSMSSenderIdObj = null;
		
		if(senderIdList != null && senderIdList.size() > 0){
			for (UserSMSSenderId userSMSSenderId : senderIdList) {
				
				if(uaeSenderIdTxtBxId.getValue().trim().equals(userSMSSenderId.getSenderId())) {
					userSMSSenderIdObj = userSMSSenderId;
					break;
				}else if(userSMSSenderId.getSmsType() != null &&  userSMSSenderId.getSmsType().equals("TR")) {
					userSMSSenderIdObj = userSMSSenderId;
					break;
				}
			}
		}
			
		
		
		logger.info("user details >> "+user.getUserName());
		if(uaeTransationalSMSChkBxId.isChecked()){
			user.setMsgChkType("TR");
			user.setUserSMSTool(Constants.USER_SMSTOOL_MVAYOO);
		}else{
			user.setMsgChkType(null);
		}
		//usersDao.saveOrUpdate(user);
		usersDaoForDML.saveOrUpdate(user);
		
		if(userSMSSenderIdObj == null) {
			userSMSSenderIdObj = new UserSMSSenderId();
			userSMSSenderIdObj.setUserName(user.getUserName());
			userSMSSenderIdObj.setUserId(user.getUserId());
		}
		
		if(uaeTransationalSMSChkBxId.isChecked()){
			userSMSSenderIdObj.setSmsType("TR");
		}else {
			userSMSSenderIdObj.setSmsType(null);
		}
		userSMSSenderIdObj.setSenderId(uaeSenderIdTxtBxId.getValue());
		//userSMSSenderIdDao.saveOrUpdate(userSMSSenderIdObj);
		userSMSSenderIdDaoForDML.saveOrUpdate(userSMSSenderIdObj);
		
		
		MessageUtil.setMessage("SMS settings saved successfully.", "color:green;");
		
	}
	
	
	
	private Window previewIframeWin; 
	private  Iframe previewIframeWin$iframeId;
	public void onClick$tempContPreviewTbId() {
		//String htmlContent=campaign.getHtmlText();
		if(approveTempCmbBoxId.getSelectedIndex()==0) return;
		Comboitem combItem = approveTempCmbBoxId.getSelectedItem();
		TransactionalTemplates trTemplateObj = (TransactionalTemplates) combItem.getValue();
		Utility.showPreview(previewIframeWin$iframeId,user.getUserName(), trTemplateObj.getTemplateContent());
		previewIframeWin.setVisible(true);
		
	} //onClick$tempContPreviewTbId
	
	public void onClick$uaeTempContPreviewTbId() {
		//String htmlContent=campaign.getHtmlText();
		if(uaeApproveTempCmbBoxId.getSelectedIndex()==0) return;
		Comboitem combItem = uaeApproveTempCmbBoxId.getSelectedItem();
		TransactionalTemplates trTemplateObj = (TransactionalTemplates) combItem.getValue();
		Utility.showPreview(previewIframeWin$iframeId,user.getUserName(), trTemplateObj.getTemplateContent());
		previewIframeWin.setVisible(true);
		
	} //onClick$tempContPreviewTbId
	
	
	
	
	
	public void onClick$approveTempTbId() {
		if(approveTempCmbBoxId.getSelectedIndex()==0) return;
		Comboitem combItem = approveTempCmbBoxId.getSelectedItem();
		TransactionalTemplates trTemplateObj = (TransactionalTemplates) combItem.getValue();
		if(trTemplateObj.getStatus() == 0){
			trTemplateObj.setStatus(1);
			//transactionalTemplatesDao.saveOrUpdate(trTemplateObj);
			transactionalTemplatesDaoForDML.saveOrUpdate(trTemplateObj);
		}else return;
		combItem.setDescription("Approved");
		MessageUtil.setMessage("Template  approved successfully.", "color:green;");
	} //onClick$approveTempTbId
	public void onClick$uaeApproveTempTbId() {
		if(uaeApproveTempCmbBoxId.getSelectedIndex()==0) return;
		Comboitem combItem = uaeApproveTempCmbBoxId.getSelectedItem();
		TransactionalTemplates trTemplateObj = (TransactionalTemplates) combItem.getValue();
		if(trTemplateObj.getStatus() == 0){
			trTemplateObj.setStatus(1);
			//transactionalTemplatesDao.saveOrUpdate(trTemplateObj);
			transactionalTemplatesDaoForDML.saveOrUpdate(trTemplateObj);
		}else return;
		combItem.setDescription("Approved");
		MessageUtil.setMessage("Template  approved successfully.", "color:green;");
	} //onClick$approveTempTbId
	
	
	public void onClick$logInToUserAnchId(){
		
		/*Sessions.getCurrent().removeAttribute("userRoleSet");
		Sessions.getCurrent().removeAttribute("userName");
		Sessions.getCurrent().removeAttribute("userObj");
		Sessions.getCurrent().removeAttribute(Constants.LISTIDS_SET);
		Sessions.getCurrent().removeAttribute(Constants.SEGMENTIDS_SET);
		Sessions.getCurrent().removeAttribute("currentPage");
		sessionScope.remove("currentPage");
		
		session.setAttribute("userObj", user);*/
		
		HttpServletRequest request =(HttpServletRequest) Executions.getCurrent().getNativeRequest();
		
		HttpSession httpSession =(HttpSession)request.getSession(true);
		httpSession.setAttribute("userObj", user);
		Users adminUser = (Users)Sessions.getCurrent().getAttribute("OC_Admin_User");
		PropertyUtil.isOCAdminCache.put(user.getUserId(),adminUser.getUserId());
		if(userActivitiesDaoForDML != null) {
			String username = Utility.getOnlyUserName(user.getUserName());
			String orgId = Utility.getOnlyOrgId(user.getUserName());
	      	userActivitiesDaoForDML.addToActivityList(ActivityEnum.ADMIN_LOGGED_IN,adminUser,true,username,orgId);
		}
		Executions.getCurrent().sendRedirect("/loginRedirect.jsp");
		/*try{
			
			Authentication request = new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
		
			SecurityContext securityContext = new SecurityContextImpl();
			
			Sessions.getCurrent().removeAttribute("userRoleSet");
			Sessions.getCurrent().removeAttribute("userName");
			Sessions.getCurrent().removeAttribute("userObj");
			Sessions.getCurrent().removeAttribute(Constants.LISTIDS_SET);
			Sessions.getCurrent().removeAttribute(Constants.SEGMENTIDS_SET);
			
//			SecurityContextHolder.clearContext();
	        SecurityContextHolder.setContext(securityContext);
	        AuthenticationManager authenticationManager = (AuthenticationManager) SpringUtil.getBean("switchUserAuthMgr");
	        Authentication result = authenticationManager.authenticate(request);
	        SecurityContextHolder.getContext().setAuthentication(null);
	        SecurityContextHolder.getContext().setAuthentication(result);
	        
	        GetUser.getUserInfo();
	        
	        String sesId = ((HttpSession)session.getNativeSession()).getId();
			if( ActiveUsers.sessions.contains(sesId) ) {
				logger.info("the Session id is====>"+sesId+" "+GetUser.getUserObj());
				ActiveUsers.activeUsersMap.put(sesId, GetUser.getUserObj());
				logger.info("the size of active users are===>"+ActiveUsers.activeUsersMap.size()+GetUser.getUserName());
			}
			
			String useMQS = PropertyUtil.getPropertyValueFromDB("useMQS");
			if(useMQS!=null){
				if(!useMQS.equalsIgnoreCase("true")){
					Menubar menubar = (Menubar)Utility.getComponentById("indexMenubarId");
					List menus = menubar.getChildren();
					for(Object obj:menus){
						if(obj instanceof Menu){
							Menu menu = (Menu)obj;
							String id = menu.getId();
							if(!id.contains("mqs")){
								Menupopup menuPopup= menu.getMenupopup();
								List<Component> mis = menuPopup.getChildren();
								String miValue = "";
								for(Component eachComp : mis){
									if(!(eachComp instanceof Menuitem)) {
										continue;
									}
									Menuitem mi=(Menuitem)eachComp;
									miValue = mi.getId();
									if(miValue.contains("Mqs")){
										mi.setVisible(false);
									}
								}
							}else
								menu.setVisible(false);
							
						}
					}//for
				}//if
			}//if
	        
	        sessionScope.put("isAdmin",SecurityUtil.isAllGranted(RightsEnum.Menu_Adminstrator_VIEW.name()));
	        Redirect.goTo(PageListEnum.RM_HOME);
		} catch (Exception e) {
			logger.error("Exception  ::", e);
		}
*/
	}
	
	
	/*private class MyComboboxRenderer implements ComboitemRenderer{

		@Override
		public void render(Comboitem ci, Object obj) throws Exception {
			if(obj instanceof Vmta) {
				Vmta vmta = (Vmta) obj;
//				logger.debug("VMTA : " + vmta.getVmtaName());
				ci.setLabel(vmta.getVmtaName());
				ci.setDescription(vmta.getDescription());
				ci.setImage("img/index1.jpg");
			}
		}
		
	}*/
	
            	public void onClick$updateEmailCreditBtnId() {
            	    emailUpdateCredit.setVisible(true);
            	    emailUpdateCredit.doHighlighted();
            	}
            	
            	public void onClick$updateEmailCreditApprove$emailUpdateCredit() throws NumberFormatException {
            	    try {
            	        Users updateUserEmail = usersDao.findByUserId(user.getUserId());
            	        int count = updateUserEmail.getEmailCount() - updateUserEmail.getUsedEmailCount();
            	        if(count != 0 && count > 0) {
            	            updateUserEmail.setEmailCount(updateUserEmail.getEmailCount() + Integer.parseInt(emailUpdateCredit$updateEmailCreditText.getValue()));
            	        }else if(updateUserEmail.getEmailCount().equals(updateUserEmail.getUsedEmailCount()) || (count != 0 && count < 0)) {
            	        	updateUserEmail.setEmailCount(updateUserEmail.getEmailCount() + Integer.parseInt(emailUpdateCredit$updateEmailCreditText.getValue()));
            	        }
                	    usersDaoForDML.saveOrUpdate(updateUserEmail);
                	    if(emailUpdateCredit$updateEmailCreditText.getValue() != null || !emailUpdateCredit$updateEmailCreditText.getValue().isEmpty()) {
                	        emailUpdateCredit$updateEmailCreditText.setValue("");                	        
                	    }
                	    emailUpdateCredit.setVisible(false);
                	    
                	    totalEmailCreditLabelId.detach();
                	    usedEmailCreditLabelId.detach();
                	    availableEmailCreditLabelId.detach();
                	    
                	    totalEmailCreditId.setLabel(updateUserEmail.getEmailCount().toString());
                	    //updateEmailCreditId.setLabel(updateUserEmail.getUpdateEmailCount().toString());
                	    usedEmailCreditId.setLabel(updateUserEmail.getUsedEmailCount().toString());
                	    Integer availableEmailCount = updateUserEmail.getEmailCount() - updateUserEmail.getUsedEmailCount();
                	    availableEmailCreditId.setLabel(availableEmailCount.toString());
                	    
                	    CheckEmailSmsCreditThread checkEmail = new CheckEmailSmsCreditThread();
                	    
                	    if(user!= null) {
    						Users updatedUser = usersDao.findByUserId(updateUserEmail.getUserId());
    						checkEmail.checkEmailSmsCredit(updatedUser);
    					}
                	    
            	    }catch (Exception e) {
            	        logger.error("failed to update user email credits");
            	    }
            	}
            	
            	public void onClick$updatePromoBtnId() {
            		String coupCode=promoTxtBxId.getValue();
            		//if (StringUtils.isNotBlank(coupCode) && promoStatusLbId.getSelectedIndex() > 0) {
            		if (coupCode!=null&&!coupCode.isEmpty() && promoStatusLbId.getSelectedIndex() > 0) {
						String Status = promoStatusLbId.getSelectedItem().getLabel();
						CouponCodesDaoForDML couponCodesDaoForDML=null;
						try {
							couponCodesDaoForDML = (CouponCodesDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.COUPONCODES_DAOForDML);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error(e);
						}
						int rowsUpdated = couponCodesDaoForDML.updateCouponCodeStatus(Status, coupCode,
								user.getUserOrganization().getUserOrgId());
						if(rowsUpdated > 0) {
							// success pop-up
							MessageUtil.setMessage("Promocode updated successfully Count: "+rowsUpdated, "color:blue", "TOP");
						}else {
							// failure pop-up
							MessageUtil.setMessage("Please enter a valid Promocode", "color:red", "TOP");
						}
					}else {
						// failure pop-up
						MessageUtil.setMessage("Please enter Promocode along with Status", "color:red", "TOP");
					}
            		promoTxtBxId.setValue("");
            		promoStatusLbId.setSelectedIndex(0);
            		
            		
            	}
            	
            	public void onClick$deleteEmailId() {
            		String emailId=emailTxtBxId.getValue();
            		int rowsUpdated=0;
            		int contactUpdated=0;
            		boolean searchEmail=false;
            		if(emailId!=null&&!emailId.isEmpty()) {
            			SuppressedContactsDaoForDML suppressedContactsDaoForDML;
            			suppressedContactsDaoForDML = (SuppressedContactsDaoForDML)SpringUtil.getBean("suppressedContactsDaoForDML");
            			ContactsDaoForDML contactsDaoForDML;
            			contactsDaoForDML = (ContactsDaoForDML)SpringUtil.getBean("contactsDaoForDML");
            			SuppressedContactsDao suppressedContactsDao=null;
            			UnsubscribesDaoForDML unsubscribesDaoForDML=null;
            			
            			try {
            				unsubscribesDaoForDML = (UnsubscribesDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.UNSUBSCRIBE_DAO_FOR_DML);
            				suppressedContactsDao = (SuppressedContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_SUPPRESSED_CONTACTS_DAO);
            				
            				//searchEmail=suppressedContactsDao.findByUserId(user.getUserId(),emailId);
            				rowsUpdated = suppressedContactsDaoForDML.deleteByEmailId(user.getUserId(),emailId);
            				if(! (rowsUpdated>0))rowsUpdated=unsubscribesDaoForDML.deleteByEmailId(user.getUserId(),emailId);
            				contactsDaoForDML.updateEmailStatusByAdmin("'"+emailId+"'",user.getUserId(),Constants.CONT_STATUS_ACTIVE);//Constants.CONT_STATUS_SUPPRESSED);
            				
            				//contactsDaoForDML.updateEmailStatusByStatus("'"+emailId+"'",user.getUserId(),Constants.CONT_STATUS_ACTIVE,Constants.CONT_STATUS_BOUNCED);
            				//For Status unsubscribe
            				//contactsDaoForDML.updateEmailStatusByStatus("'"+emailId+"'",user.getUserId(),Constants.CONT_STATUS_ACTIVE,Constants.CONT_STATUS_UNSUBSCRIBED);
            				//	contactsDaoForDML.updateEmailStatusByStatus("'"+emailId+"'",user.getUserId(),Constants.CONT_STATUS_ACTIVE,Constants.CONT_STATUS_SUPPRESSED);
							logger.info("Deleted from Suppression and updated");
            			} catch (Exception e) {
							// TODO Auto-generated catch block
            				logger.error(e);
							logger.info("Deleted from Suppression");
						}
            			if(rowsUpdated>0) {
							MessageUtil.setMessage("EmaiId deleted from SuppressesList: "+rowsUpdated, "color:blue", "TOP");

            			}else {
            			logger.info("String"+rowsUpdated);	
						MessageUtil.setMessage("EmailId doesn't exist", "color:red", "TOP");
            			}

            		}else {
					MessageUtil.setMessage("Please enter EmailId", "color:red", "TOP");
            		}
            		emailTxtBxId.setValue("");
            	}
            	
            	
            	
            	
            	
            	public void onClick$deletephnId() {
            		String phoneId=phnTxtBxId.getValue();
            		int rowsUpdated=0;
            		if(phoneId!=null&&!phoneId.isEmpty()) {
            			
            			
            			UserOrganization organization = user!=null ? user.getUserOrganization(): null;
        				String phone = Utility.phoneParse(phnTxtBxId.getValue(),organization);
        				if(phone == null || phone.trim().length() == 0) {
        					MessageUtil.setMessage("Please provide valid data for the fields in red.", "color:red", "TOP");
        					return;
        				}
            			
        				ServiceLocator locator = ServiceLocator.getInstance();
        				SMSSuppressedContactsDaoForDML smsSuppressedContactsDaoForDML=null;
        				SMSSuppressedContactsDao smsSuppressedContactsDao = null;
        				try {
        					smsSuppressedContactsDao = (SMSSuppressedContactsDao)locator.getDAOByName(OCConstants.SMS_SUPPRESSEDCONTACT_DAO);
	            			smsSuppressedContactsDaoForDML = (SMSSuppressedContactsDaoForDML)locator.getDAOForDMLByName(OCConstants.SMS_SUPPRESSEDCONTACT_DAO_FOR_DML);

        				
        				} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

            			ContactsDaoForDML contactsDaoForDML;
            			contactsDaoForDML = (ContactsDaoForDML)SpringUtil.getBean("contactsDaoForDML");
            			
            			try {
            				
            				rowsUpdated = smsSuppressedContactsDaoForDML.deleteFromSuppressedContacts(user,phoneId);
            				contactsDaoForDML.updatemobileStatus(phoneId,Constants.CONT_STATUS_ACTIVE, user);
            				
							logger.info("Deleted from Suppression and updated");
            			} catch (Exception e) {
							// TODO Auto-generated catch block
            				logger.error(e);
							logger.info("Deleted from Suppression");
						}
            			if(rowsUpdated>0) {
							MessageUtil.setMessage("Phone Number deleted from SuppressesList: "+rowsUpdated, "color:blue", "TOP");

            			}else {
            			logger.info("String"+rowsUpdated);	
						MessageUtil.setMessage("PhoneNumber doesn't exist", "color:red", "TOP");
            			}

            		}else {
					MessageUtil.setMessage("Please enter PhoneNumber", "color:red", "TOP");
            		}
            		phnTxtBxId.setValue("");
            	}
            	
            	
            	
            	/*private void checkThreshold(){
                    try {
                        Users updateUserEmail = usersDao.findByUserId(user.getUserId());
                        int thresholdUserCredit = ((int) Math.ceil((updateUserEmail.getEmailCount() * 10) / 100));
                        int remainingEmailCredit = (updateUserEmail.getEmailCount() - updateUserEmail.getUsedEmailCount());
                        if (updateUserEmail.getUsedEmailCount() > updateUserEmail.getEmailCount() || (remainingEmailCredit < thresholdUserCredit)) {
                            updateEmailCreditBtnId.setVisible(true);
                        } else {
                            updateEmailCreditBtnId.setVisible(false);
                        }
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }*/
}


/*	
			StringBuffer message = new StringBuffer("");
			
			logger.info("Start Date :" + startDt);
			logger.info("Exp Date :" + expDt);
			logger.info("startDt.after(expDt) :" + startDt.after(expDt));
			
			if(startDt.after(expDt)) {
				message.append("Start Date must be before Expiry Date \n");
			}
			
			if(expDt.before(new Date())) {
				message.append("Expiry Date must be future Date \n");
			}
			
			if(totalLimitLbId.getValue() == null) {
				message.append("Enter Total Limit count");
			}
			
			if(message.length()>0) {
				MessageUtil.setMessage(message.toString(), "color:red", "TOP");
				return;
			}*/
