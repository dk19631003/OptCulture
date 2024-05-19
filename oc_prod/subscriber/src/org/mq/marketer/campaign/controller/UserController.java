package org.mq.marketer.campaign.controller;


import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.catalina.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SecRoles;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.SecRolesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;


public class UserController extends GenericForwardComposer {
	
	UsersDao usersDao=null; 
	UsersDaoForDML usersDaoForDML=null;
	Users user = null;
	
	private Button changeAddressId, saveWeeklyReportDataBtnId;
	private Div changeUserAddrDivId,userAddrDivId,pwdTdId;//changes 2.5.3.0
	private Label userAddrLblId, userNameLblId;
	private Textbox cPinLbId, weeklyReportEmailTbId,expiryEmailTbId;
	private Textbox cAddressOneTbId,cAddressTwoTbId,cCityTbId,cStateTbId,cCountryTbId,cPhoneTbId;
	private Label subscriptionExpDateLblId,subscriptionStartdateLblId;
	private Listbox daysLbId, hoursLbId, minLbId, ampmLbId;
	private Checkbox emailCampaignChkId, smsCampaignChkId;
	String userAddr;
	//Added for 2.3.11
	private Label sendWeeklyCampaignReportLbId,setDayTimeLbId,sendScheduleExpiryAlertLbId;
	private Button modifyBtnId;
	private Div editWeeklyReportDivId,weeklyReportDivId,emailTextboxDivId,dayTimeLBDivId;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private  final String ROLE_BASIC = "Basic User";
	private  final String ROLE_POWER = "Power User";
	private final String ROLE_SUPER = "Super User";
	private final String ROLE_ADMIN = "Administrator";
	
	SecRolesDao secRolesDao;
	 
	public UserController(){
		usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		usersDaoForDML = (UsersDaoForDML)SpringUtil.getBean("usersDaoForDML");
		secRolesDao = (SecRolesDao)SpringUtil.getBean("secRolesDao");
		
		user=(Users)getUser();
		if(user ==null) {
			Redirect.goTo(PageListEnum.RM_HOME);
		}
		
		UserActivitiesDao userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		UserActivitiesDaoForDML userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		/*if(userActivitiesDao != null) {
	      	userActivitiesDao.addToActivityList(ActivityEnum.VISIT_UPDATE_PASSWORD,GetUser.getUserObj());
		}*/
		if(userActivitiesDaoForDML != null) {
	      	userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_UPDATE_PASSWORD,GetUser.getLoginUserObj());
		}
		
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
     	PageUtil.setHeader("Profile Settings","",style,true);
        
		
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
        
//        user = GetUser.getUserObj();
     	logger.info("user Address"+user.getAddressOne() +", "+user.getAddressTwo()+", "+user.getCity()+
							", "+user.getState()+", "+user.getCountry()+", "+user.getPinCode()+", "+user.getPhone());
     	
     	
     	setUserAddress();
     	setWeeklyReportDivData();
     	setWeeklyReportData();
     	userNameLblId.setValue(Utility.getOnlyUserName(user.getUserName()));
	    /*userAddr = user.getAddressOne() +", "+user.getAddressTwo()+", "+user.getCity()+
							", "+user.getState()+", "+user.getCountry()+", "+user.getPinCode()+", "+user.getPhone();
	    userAddrLblId.setValue(userAddr);*/
	    if(user == null) {
	    	Redirect.goTo(PageListEnum.RM_HOME);
	    }
	    
	    /*String senderIds = "";
		try{
		   
		    
		   	List senderIdsLst  = usersDao.getSenderIdByUserName(user.getUserName());
		   	String senderId = "";
		   	if(senderIdsLst != null && senderIdsLst.size()!=0){
			   	//logger.info("val"+ senderIdsLst);
			   	for(Object obj : senderIdsLst) {
			   		senderId = (String)obj;
			   		if(senderIds.length()>0) senderIds += ",";
			   		senderIds += senderId;
			   		
			   	}
		   	}
		}catch(Exception e) {
			logger.error("Exception ::" , e);
			
		}*/
		String packExpDateStr = MyCalendar.calendarToString(user.getPackageExpiryDate(), MyCalendar.FORMAT_STDATE);
	    String packStartDateStr = MyCalendar.calendarToString(user.getPackageStartDate(), MyCalendar.FORMAT_STDATE);
	    subscriptionExpDateLblId.setValue(packExpDateStr);
	    subscriptionStartdateLblId.setValue(packStartDateStr);
	    
	} // doAfterCompose
	
	
	

	public void setUserAddress() {
		
		cAddressOneTbId.setValue(user.getAddressOne());
		cAddressOneTbId.setAttribute("value", user.getAddressOne());
		
		
		cAddressTwoTbId.setValue(user.getAddressTwo());
		cAddressTwoTbId.setAttribute("value", user.getAddressTwo());
		
		cCityTbId.setValue(user.getCity());
		cCityTbId.setAttribute("value", user.getCity());
		
		cStateTbId.setValue(user.getState());
		cStateTbId.setAttribute("value", user.getState());
		
		cCountryTbId.setValue(user.getCountry());
		cCountryTbId.setAttribute("value", user.getCountry());
		

		try {
			if(user.getPinCode()!=null){
			  if(user.getPinCode().length()!=0)
				cPinLbId.setValue(user.getPinCode()== null ? "" : user.getPinCode() );
			  cPinLbId.setAttribute("value", user.getPinCode()== null ? "" : user.getPinCode());
				
			  
			}
		} catch (Exception e) {
			logger.error("** Exception: Problem occured while setting PinCode value : "+e+" **");
		}	
		try {
			if(user.getPhone().trim()!=null){
				if(user.getPhone().length()!=0)
				cPhoneTbId.setValue(user.getPhone());
				cPhoneTbId.setAttribute("value", user.getPhone());
			}
		} catch (Exception e) {
			logger.error("** Exception: Problem occured while setting updating user information . "+e+" **");
		}
		
		
		String postalAddressData = "";
		
		String value = null;
		
		value = (String)cAddressOneTbId.getAttribute("value");
		if(value != null && !value.equals("")) {
			
			postalAddressData += value+", ";
			
		}
		
		value = (String)cAddressTwoTbId.getAttribute("value");
		if(value != null && !value.equals("")) {
			
			postalAddressData += value+", ";
			
		}
		
		value = (String)cCityTbId.getAttribute("value");
		if(value != null && !value.equals("")) {
			
			postalAddressData += value+", ";
			
		}
		
		value = (String)cStateTbId.getAttribute("value");
		if(value != null && !value.equals("")) {
			
			postalAddressData += value+", ";
			
		}
		
		value = (String)cCountryTbId.getAttribute("value");
		if(value != null && !value.equals("")) {
			
			postalAddressData += value+", ";
			
		}
		
		String pinValue = ((String)cPinLbId.getAttribute("value")).trim();
		if(pinValue.length() > 0 ) {
			
			postalAddressData += pinValue;
			
		}
		
		value = (String)cPhoneTbId.getAttribute("value");
		if(value != null && !value.equals("")) {
			
			postalAddressData += ", "+value;
			
		}
		
		userAddrLblId.setValue(postalAddressData);
		
		
		if(  (user.getAddressOne() == null || user.getAddressOne().trim().equals("")) ||
				(user.getCity() == null || user.getCity().trim().equals("")) || 
				(user.getState() == null || user.getState().trim().equals("")) ||
				(user.getCountry() == null || user.getCountry().trim().equals("")) ||
				(user.getPinCode() == null || user.getPinCode().trim().equals("")) ) {
			
			changeUserAddrDivId.setVisible(true);
			userAddrDivId.setVisible(false);
			
		}else{
			
			changeUserAddrDivId.setVisible(false);
			userAddrDivId.setVisible(true);
			
			
		}
		
		
		
	}
	
	
	
public void onClick$cancelAddressId() {
		
		
		cAddressOneTbId.setValue((String)cAddressOneTbId.getAttribute("value"));
		cAddressTwoTbId.setValue((String)cAddressTwoTbId.getAttribute("value"));
		cCityTbId.setValue((String)cCityTbId.getAttribute("value"));
		cStateTbId.setValue((String)cStateTbId.getAttribute("value"));
		cStateTbId.setValue((String)cCountryTbId.getAttribute("value"));
		cPinLbId.setValue( ((String)cPinLbId.getAttribute("value")).trim());
		cPhoneTbId.setValue((String)cPhoneTbId.getAttribute("value"));
		
		userAddrDivId.setVisible(true);
		changeUserAddrDivId.setVisible(false);
		
		
		
	}
	
	
	
	
	
	public void updatePassword(Textbox  currPass,Textbox newPass,Textbox reType){
		try {
			if(logger.isDebugEnabled()) logger.debug("--Just Entered updatePassword()");
			
			String current = currPass.getValue();
			String newPassword = newPass.getValue();
			String reTypePassword = reType.getValue();
			
			if(current.trim().equals("")){
				MessageUtil.setMessage("Current password field cannot be left empty.", "color:red","TOP");
				currPass.setFocus(true);
				 return;
			}
			if(newPassword.trim().equals("")) {
				MessageUtil.setMessage("New password field cannot be left empty.", "color:red","TOP"); 
				 newPass.setFocus(true);
				 return;
			}
			if(reTypePassword.trim().equals("")) {
				MessageUtil.setMessage("Retype password field cannot be left empty.", "color:red","TOP");
				reType.setFocus(true);
				return;
			}
			String pattern = "^(?=.{8,50}$)(?=(.*[A-Z]))(?=(.*[a-z]))(?=(.*[0-9]))(?=(.*[-@!#$%^&-+=()])).*$";
			
			Pattern pwdPattern = Pattern.compile(pattern);
			
			if(newPassword ==null || (!pwdPattern.matcher(newPassword.trim()).matches())
					|| reTypePassword ==null || (!pwdPattern.matcher(reTypePassword.trim()).matches())) {
				MessageUtil.setMessage("Password must contain at least 8 characters,1 uppercase,1 lowercase,"
						+ "\n 1 special character (@!#$%^&+-=*'()) and 1 number .", "color:red","TOP");
				newPass.setFocus(true);
				return;
			}
			if(user != null){
				logger.info("userspwd"+user.getPassword());
							
				boolean isMatched = true;
				if(user.getMandatoryUpdatePwdOn() == null) {
					
					Md5PasswordEncoder md5 = new Md5PasswordEncoder();
			    	String currentHash = null;
						try {
							currentHash = md5.encodePassword(current,user.getUserName());
						} catch (Exception e) {

						logger.error("Exception in md5", e);
						}

						logger.info("userspwd1"+user.getPassword());
						if(currentHash != null && !currentHash.equals(user.getPassword())){
							logger.info("userspwd in"+user.getPassword());
						isMatched = false;
						}
						/*
						 * if(BCrypt.checkpw(current, user.getPassword())) { isMatched = true; }
						 */
						/*
						 * if(currPass.getValue().equalsIgnoreCase(user.getUserName())||BCrypt.checkpw(
						 * current, user.getPassword())) { isMatched = true; }
						 */
						if(BCrypt.checkpw(current, user.getPassword())||(currentHash != null&&currentHash.equals(user.getPassword()))) {
							isMatched = true;
						}
				}else{
					
					try {
						logger.info("EncryptedPwd"+(!BCrypt.checkpw(current, user.getPassword())));
						if(!BCrypt.checkpw(current, user.getPassword())){
							isMatched = false;
						}
					} catch (Exception e) {
						logger.error("Exception in bcrypt", e);
					}
				}
				
				logger.info("userspwd2"+user.getPassword());
				if(!isMatched) {
					
					MessageUtil.setMessage("Current password is incorrect.", "color:red","TOP");
					currPass.setFocus(true);
					return;
				}
			}
			
			/*Md5PasswordEncoder md5 = new Md5PasswordEncoder();
			String currentHash = md5.encodePassword(current,user.getUserName());*/
			String newPasswordHash = null;
			try {
				newPasswordHash = Utility.encryptPassword(user.getUserName(), newPassword);
			} catch (Exception e1) {
				logger.error("Exception in bcrypt", e1);
			}
			/*md5 = new Md5PasswordEncoder();
			String newPasswordHash = md5.encodePassword(newPassword,user.getUserName());*/
			
			
			if(!newPassword.equals(reTypePassword)) {
				 MessageUtil.setMessage("Both passwords must match.", "color:red","TOP");
				 reType.setFocus(true);
				 return;
			}
			MessageUtil.clearMessage(); 
			UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
			UsersDaoForDML usersDaoForDML = (UsersDaoForDML)SpringUtil.getBean("usersDaoForDML");
			if(logger.isDebugEnabled()) logger.debug("updating the password");
			if(newPasswordHash != null)user.setPassword(newPasswordHash);
			if(user.getLastLoggedInTime() == null) {
				user.setLastLoggedInTime(Calendar.getInstance());
			}
			user.setMandatoryUpdatePwdOn(Calendar.getInstance());
			try {
				//usersDao.saveOrUpdate(user);
				usersDaoForDML.saveOrUpdate(user);
			} catch (Exception e) {
				logger.error("**Error while updating database**");
			}
			currPass.setValue("");
			newPass.setValue("");
			reType.setValue("");
			MessageUtil.setMessage("Your password has been updated successfully.", "color:blue","TOP");//changes 2.5.3.0
				
			UserActivitiesDao  userActivitiesDao =  (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
			UserActivitiesDaoForDML  userActivitiesDaoForDML =  (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
			/*if(userActivitiesDao != null) {
        		userActivitiesDao.addToActivityList(ActivityEnum.USR_CHG_PSWRD,user);
			}*/
			if(userActivitiesDaoForDML != null) {
        		userActivitiesDaoForDML.addToActivityList(ActivityEnum.USR_CHG_PSWRD,GetUser.getLoginUserObj());
			}
			if(logger.isDebugEnabled()) logger.debug("Updated successfully");
		} catch (WrongValueException wve) {
			logger.error("** Exception : Wrong Value - "+wve+" **");
		}catch (Exception e) {
			logger.error("** Exception : Error while updating Password - ", e );
		}
		pwdTdId.setVisible(!pwdTdId.isVisible());//changes 2.5.3.0 
	}
	
	
	public Users getUser(){
		
		 user = GetUser.getLoginUserObj();
		// user.setUserName(Utility.getOnlyUserName(user.getUserName()));
		 
		 String userDomainStr = "";
		 List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(user.getUserId());
		 Set<UsersDomains> domainSet = new HashSet<UsersDomains>();
			
			domainSet.addAll(domainsList);//user.getUserDomains();
		 
		 
		 for (UsersDomains usersDomains : domainSet) {
			
			 if(userDomainStr.length()>0) userDomainStr+=",";
			 userDomainStr += usersDomains.getDomainName();
			 
			 
			 
		}
		 
		 
		 user.setUserDomainStr(userDomainStr);
		 
		 
		 String role = getUserRole(user.getUserId().longValue());
		/* if(role.equals(Constants.ROLE_USER_BASIC)){
				role = ROLE_BASIC;
			}else if(role.equals(Constants.ROLE_USER_POWER)) {
				
				role = ROLE_POWER;
			}else if(role.equals(Constants.ROLE_USER_SUPER)) {
				
				role = ROLE_SUPER;
			}else if(role.equals(Constants.ROLE_ADMIN)) {
				
				role = ROLE_ADMIN;
			}
			
		 */
		 
		 user.setUserRole(role);
		 
		 
		 if(user.getLastName() == null) user.setLastName("");
		 if(user.getFirstName() == null) user.setFirstName("");
		return user;
	}
	
	
	
public String getUserRole(Long userID) {
		
		//logger.info("usersDao ==="+usersDao);
		usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		usersDaoForDML = (UsersDaoForDML)SpringUtil.getBean("usersDaoForDML");
		
		List<SecRoles> rolesList = secRolesDao.findByUserId(userID);
		
		String role = "";
		
		//List<Authorities> roleLst = usersDao.findRole(userName);
		for (SecRoles secRole : rolesList) {
			
			if(!role.isEmpty()) role += ",";
			role += secRole.getName();
		}
		
		return role;
	}
	
	
	public String getSenderIds() {
		
		String senderIds = "";
		try{
		   
		    user = GetUser.getLoginUserObj();
		   	List senderIdsLst  = usersDao.getSenderIdByUserName(user.getUserName());
		   	String senderId = "";
		   	if(senderIdsLst != null && senderIdsLst.size()!=0){
			   	//logger.info("val"+ senderIdsLst);
			   	for(Object obj : senderIdsLst) {
			   		senderId = (String)obj;
			   		if(senderIds.length()>0) senderIds += ",";
			   		senderIds += senderId;
			   		
			   	}
		   	}
		}catch(Exception e) {
			logger.error("Exception ::" , e);
			
		}
		
		return senderIds;
	}
	
	public void onClick$doneAnchrId() {
		try {
			updateUserAddr();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	}
		private void updateUserAddr() throws Exception{
		
		try {
			String value = cAddressOneTbId.getValue();
			String tempStr = "";
			if(value == null || value.trim().equals("") ) {
				
			//	Messagebox.show("Address line 1 should not be empty.");
				MessageUtil.setMessage("Address line 1 cannot be left empty.", "Color:red", "Top");
				return;
				
			}
			cAddressOneTbId.setAttribute("value", value);
			user.setAddressOne(value);
			tempStr += value+", ";
			
			value = cAddressTwoTbId.getValue();
			cAddressTwoTbId.setAttribute("value", value);
			user.setAddressTwo(value);
			if(value.trim().length() > 0) tempStr += value+", ";

			value = cCityTbId.getValue();
			if(value == null || value.trim().equals("")) {
				
			//	Messagebox.show("City should not be empty.");
				MessageUtil.setMessage("City cannot be left empty.", "Color:red", "Top");
				return;
			}
			cCityTbId.setAttribute("value", value);
			
			user.setCity(value);
			tempStr += value+", ";
			/*if(cPinLbId.getValue() == null) {
				logger.info("null");
			}*/
			
			
			
			value = cStateTbId.getValue();
			if(value == null || value.trim().equals("")) {
				
			//	Messagebox.show("State should not be empty.");
				MessageUtil.setMessage("State cannot be left empty.", "Color:red", "Top");
				return;
			}
			cStateTbId.setAttribute("value", value);
			user.setState(value);
			tempStr += value+", ";
			
			value = cCountryTbId.getValue();
			if(value == null || value.trim().equals("")) {
				
//		Messagebox.show("Country should not be empty.");
				MessageUtil.setMessage("Country cannot be left empty.", "Color:red", "Top");
				return;
			}
			cCountryTbId.setAttribute("value", value);
			user.setCountry(value);
			tempStr += value+", ";
			
			
			value = cPinLbId.getValue().trim();
			String countryType = user.getCountryType();
			boolean checkPin = false;
			
			if(Utility.zipValidateMap.containsKey(countryType)){
				
				
				if(value.length() == 0 || value.equals("")) {
					
					//	Messagebox.show("pin should not be empty.");
						MessageUtil.setMessage("Pin / Zip code cannot be left empty.", "Color:red", "Top");
						return;
				}
				boolean zipCode = Utility.validateZipCode(value, countryType);
				 
				 if(!zipCode){
					 
						MessageUtil.setMessage("Please enter valid zip code.","color:red;");
						return ;
						
					}
				
				
			}else{
				
				if(value != null && value.length() > 0){
					
					try{
						
						Long pinLong = Long.parseLong(value);
						
		      } catch (NumberFormatException e) {
						MessageUtil.setMessage("Please provide 5 / 6 digits Zip Code.","color:red;");
						return ;
		      }
					
				if(value.length() > 6 || value.length() < 5) {
					
					//	Messagebox.show("Please provide 5 / 6 digits Zip code / pin.");
						MessageUtil.setMessage("Please provide 5 / 6 digits Zip code / Pin.", "Color:red", "Top");
						return;
						
					}
				}
			}
			
			/*if(value.length() == 0 || value.trim().equals("")) {
				
			//	Messagebox.show("pin should not be empty.");
				MessageUtil.setMessage("Pin / Zip code cannot be left empty.", "Color:red", "Top");
				return;
			}*/
			
			
			
			/*if(value.length() > 6 || value.length() < 5) {
				
			//	Messagebox.show("Please provide 5 / 6 digits Zip code / pin.");
				MessageUtil.setMessage("Please provide 5 / 6 digits Zip code / Pin.", "Color:red", "Top");
				return;
				
			}*/
			cPinLbId.setAttribute("value", cPinLbId.getValue().trim());
			user.setPinCode(value);
			tempStr += value;
			
			value = cPhoneTbId.getValue().trim();
			
			try {
				
				
				if(value != null && value.length() > 0 ){
					/*&& !Utility.validateUserPhoneNum(value)) {
				}
				*/	

					String userPhoneRegex = "\\d+";
					Pattern phonePattern = Pattern.compile(userPhoneRegex);  
					Matcher m = phonePattern.matcher(value);
					String poneMatch = "";
					while (m.find()) {
						poneMatch += m.group();
					}
					try {
						value  = ""+Long.parseLong(poneMatch);
					} catch (NumberFormatException e) {
						MessageUtil.setMessage("Please provide valid Phone number.", "Color:red", "Top");
						return;
					}
					UserOrganization organization = user != null ? user.getUserOrganization() : null;
					value =  Utility.phoneParse(value,organization);
					if(value == null || value.trim().length() == 0){
						MessageUtil.setMessage("Please provide valid Phone number.", "Color:red", "Top");
						return;
					}
				
					
					
					
					
					
					
					//MessageUtil.setMessage("Please provide valid Phone number.", "Color:red", "Top");
				//long phone = Long.parseLong(value);
					//return;
				}
			} catch (Exception e) {
				//Messagebox.show("Please provide valid Phone Number");
				MessageUtil.setMessage("Please provide valid phone number.","Color:red", "Top");
				return;
			}
				
				
			
			cPhoneTbId.setAttribute("value", value);
			user.setPhone(value);
			if(value.trim().length() > 0) {
				
				tempStr +=", "+ value;
			}
			
			//usersDao.saveOrUpdate(user);
			usersDaoForDML.saveOrUpdate(user);
			try {
				MessageUtil.setMessage("User address updated successfully.", "color:green");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
			}
			
			userAddr = tempStr;
			
			changeUserAddrDivId.setVisible(false);
			userAddrLblId.setValue(userAddr);
			userAddrDivId.setVisible(true);
			
			changeAddressId.setVisible(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
  		
  		
  	}
	
	public void onClick$changeAddressId() {
		try {
			

			if(userAddrDivId.isVisible() ) {

				userAddrDivId.setVisible(false);
				changeUserAddrDivId.setVisible(true);
			}else{
				
				changeUserAddrDivId.setVisible(false);
				userAddrDivId.setVisible(true);
				
			}
			//setAddress();
			
			
			
		} catch (Exception e) {
			
			logger.error("Exception ::" , e);
		}
	}
	
	
	
	
	private Textbox currPass,newPass,reType;
	
	public void onClick$updateBtnId() {
		updatePassword(currPass,newPass,reType);
	}
  	
	public void onClick$resetBtnId() {
		currPass.setValue("");
	      newPass.setValue("");
	      reType.setValue("");
	}
	public void onClick$closeBtnId() {
		currPass.setValue("");
	      newPass.setValue("");
	      reType.setValue("");
	}


	public void onClick$saveWeeklyReportDataBtnId1(){

		logger.debug(">>>>>>> Started  onClick$saveWeeklyReportDataBtnId :: ");
		
		String weeklyReportEmailId = weeklyReportEmailTbId.getText();
		String expEmailId = expiryEmailTbId.getText();
		String displayFieldEmptyMessage = null;
		String sccessMsgString = null;
		String validateEmailMsgString  = null;

		
		if(!weeklyReportEmailId.trim().isEmpty() && !weeklyReportEmailId.equalsIgnoreCase("Enter Email Address")){
			
			int day = Integer.parseInt(daysLbId.getSelectedItem().getValue().toString());
			int hours = Integer.parseInt(hoursLbId.getSelectedItem().getValue().toString());
			int minutes = Integer.parseInt(minLbId.getSelectedItem().getValue().toString());
			int ampm = Integer.parseInt(ampmLbId.getSelectedItem().getValue().toString());
			
			if(!Utility.validateEmail(weeklyReportEmailId.trim())) {
					logger.info("Vaildatin first email id");
				// email validation check for weekly report email
					if(validateEmailMsgString == null)  validateEmailMsgString=" email address to receive weekly campaign report to.";//validateEmailMsgString = "Weekly Campaign Report Email Address";
					else validateEmailMsgString += ", " + "Email Address";
			}
			else {

				if(!weeklyReportEmailId.equalsIgnoreCase(user.getWeeklyReportEmailId()) ) {
					user.setWeeklyReportEmailId(weeklyReportEmailId);	

					// successfully save of weekly report email
					if(sccessMsgString == null) sccessMsgString = "Weekly Campaign Report Email Address";
					else sccessMsgString += ", " + "Email Address";
				}

				if(day != user.getWeeklyReportDay()) {

					user.setWeeklyReportDay(day);

					// successfully save of weekly report day

					if(sccessMsgString == null) sccessMsgString = "Weekly Campaign Report Day";
					else sccessMsgString += ", " + "Day";

				}

				Date sentDate = new Date();

				if(ampm == 0) sentDate.setHours(hours);
				else sentDate.setHours(hours+12);
				sentDate.setMinutes(minutes);
				sentDate.setSeconds(0);
				Date userSettingsDate = user.getWeeklyReportTime();

				if(userSettingsDate != null){

					int userSettingHours = userSettingsDate.getHours();
					int userSettingMins = userSettingsDate.getMinutes();
					int userSettingAmPm = -1;

					if(userSettingHours >= 12){
						userSettingAmPm = 1;
						userSettingHours = userSettingHours - 12;
					}
					else {
						userSettingAmPm = 0;
					}

					if(userSettingHours != hours || userSettingMins != minutes || ampm != userSettingAmPm ) {
						user.setWeeklyReportTime(sentDate);

						// successfully save of weekly report time 
						if(sccessMsgString == null) sccessMsgString = "Weekly Campaign Report Time";
						else sccessMsgString += ", " + "Time";
					}
				}
				else{

					user.setWeeklyReportTime(sentDate);

					// successfully save of weekly report time
					if(sccessMsgString == null) sccessMsgString = "Weekly Campaign Report Time";
					else sccessMsgString += ", " + "Time";
				}

			}
		}//weekly camp report email
		else {
			
			if(user.getWeeklyReportEmailId() != null) {
				
				user.setWeeklyReportEmailId(null);
				user.setWeeklyReportDay(0);
				user.setWeeklyReportTime(null);
				
				// user setting weekly report email as empty

				if(displayFieldEmptyMessage == null)  displayFieldEmptyMessage = " email address to receive schedule expiry alert to";//displayFieldEmptyMessage = "Weekly Campaign Report Email Address";
				else displayFieldEmptyMessage += ", " + "Email Address";
			}
			else{
				
				// if its weekly report email is empty
				if(displayFieldEmptyMessage == null) displayFieldEmptyMessage = "email address to receive schedule expiry alert";
				else displayFieldEmptyMessage += ", " + "Email Address";
			}
			
			
		}
		
		if(!expEmailId.trim().isEmpty() && !expEmailId.trim().equalsIgnoreCase("Enter Email Address")){
			if(!Utility.validateEmail(expEmailId.trim())) {
				
				// email validation check for expiry email
				if(validateEmailMsgString == null) validateEmailMsgString = " email address to receive schedule expiry alert";
				else validateEmailMsgString +=  " and  email address to receive schedule expiry alert";
			}
			else if(!expEmailId.equalsIgnoreCase(user.getCampExpEmailId())){
				
				user.setCampExpEmailId(expEmailId);
				
				//successfully save of expiry email 
				
				if(sccessMsgString == null) sccessMsgString = "Schedule Expiry Alert Email Address";
				else sccessMsgString +=  " and Schedule Expiry Alert Email Address";
			}
		}
		else {
			
			if(user.getCampExpEmailId() != null) {
			
				user.setCampExpEmailId(null);
				
				// user setting expiry email as empty
				if(displayFieldEmptyMessage == null) displayFieldEmptyMessage = " email address to receive weekly campaign report to";
				else displayFieldEmptyMessage +=  " and  email address to receive weekly campaign report to";
			}
			else{
				
				// if its expiry email is empty
				if(displayFieldEmptyMessage == null) displayFieldEmptyMessage = " email address to receive schedule expiry alert";
				else displayFieldEmptyMessage +=  " and  email address to receive schedule expiry alert";
			}
			
		}
		
		if(validateEmailMsgString != null) Messagebox.show("Please enter valid " + validateEmailMsgString + ".", "Error", Messagebox.OK, Messagebox.ERROR);
		
		if(displayFieldEmptyMessage != null) {
			
			//usersDao.saveOrUpdate(user);
			usersDaoForDML.saveOrUpdate(user);
			setWeeklyReportDivData();
			editWeeklyReportDivId.setVisible(false);
			weeklyReportDivId.setVisible(true); 
			Messagebox.show("Please provide "+ displayFieldEmptyMessage +".", "Error", Messagebox.OK, Messagebox.ERROR);
		}
		logger.info("sccessMsgStringsccessMsgString: "+sccessMsgString);
		
		if(sccessMsgString != null) {
			//usersDao.saveOrUpdate(user);
			usersDaoForDML.saveOrUpdate(user);
			MessageUtil.setMessage("Email alert settings has been saved successfully.", "color:blue","TOP");
			setWeeklyReportData();
			setWeeklyReportDivData();
			editWeeklyReportDivId.setVisible(false);
			weeklyReportDivId.setVisible(true); 
		}
	
	}
	
	public void onClick$saveWeeklyReportDataBtnId() {
		logger.debug(">>>>>>> Started  onClick$saveWeeklyReportDataBtnId :: ");
		
		String weeklyReportEmailId = weeklyReportEmailTbId.getText();
		String expEmailId = expiryEmailTbId.getText();
		String msg =Constants.STRING_NILL;
		logger.info("weekly report email ::"+weeklyReportEmailId+"; : expEmailId -"+expEmailId+";");
		weeklyReportEmailId = weeklyReportEmailId.trim();
		expEmailId = expEmailId.trim();
		boolean emptyMessage = true;
		

		if((weeklyReportEmailId == null || Constants.STRING_NILL.equals(weeklyReportEmailId) || "Enter Email Address".equalsIgnoreCase(weeklyReportEmailId) )
				&& ((expEmailId == null || Constants.STRING_NILL.equals(expEmailId)) || "Enter Email Address".equalsIgnoreCase(expEmailId)  )){
			msg = " email address to receive weekly campaign report & schedule expiry alert to ";
			weeklyReportEmailId = Constants.STRING_NILL;
			expEmailId = Constants.STRING_NILL;
			logger.info("Both email id empty");
		}
		else if((weeklyReportEmailId != null || !Constants.STRING_NILL.equals(weeklyReportEmailId) || !"Enter Email Address".equalsIgnoreCase(weeklyReportEmailId))
				&& (expEmailId == null || Constants.STRING_NILL.equals(expEmailId)|| "Enter Email Address".equalsIgnoreCase(expEmailId) )){
			msg = " email address to receive schedule expiry alert to ";
			expEmailId = Constants.STRING_NILL;
			logger.info("receive schedule expiry id empty");
		}
		else if((weeklyReportEmailId == null || Constants.STRING_NILL.equals(weeklyReportEmailId) || "Enter Email Address".equalsIgnoreCase(weeklyReportEmailId))
				&& (expEmailId != null || !Constants.STRING_NILL.equals(expEmailId)|| !"Enter Email Address".equalsIgnoreCase(expEmailId) )){
			msg = " email address to receive weekly campaign report to ";
			weeklyReportEmailId = Constants.STRING_NILL;
			logger.info("address to receive weekly campaign report id empty");
		}
		
		
		//if Empty then message
		if(!Constants.STRING_NILL.equals(msg)){
			//MessageUtil.setMessage("Please provide "+msg, "color:red");
			emptyMessage = false;
			//return;
		}
		
		if(Constants.STRING_NILL.equals(weeklyReportEmailId) && !Constants.STRING_NILL.equals(expEmailId)){
			emptyMessage = true;
		}
			
		if(Constants.STRING_NILL.equals(expEmailId) && !Constants.STRING_NILL.equals(weeklyReportEmailId)){
			emptyMessage = true;
		}
		logger.info("..................:"+emptyMessage);
		//if Not empty then Check for email validation
		if(emptyMessage){
			logger.info("Saving");
			int day = Integer.parseInt(daysLbId.getSelectedItem().getValue().toString());
			int hours = Integer.parseInt(hoursLbId.getSelectedItem().getValue().toString());
			int minutes = Integer.parseInt(minLbId.getSelectedItem().getValue().toString());
			int ampm = Integer.parseInt(ampmLbId.getSelectedItem().getValue().toString());
			boolean campaignReport = false;
			boolean campaignExpiry = false;
		
			if(emailCampaignChkId.isChecked()){
				user.setWeeklyReportTypeEmail(true);
			}else user.setWeeklyReportTypeEmail(false);
			if(smsCampaignChkId.isChecked()){
				user.setWeeklyReportTypeSMS(true);
			}else user.setWeeklyReportTypeSMS(false);
			

			if(emptyMessage && ! Constants.STRING_NILL.equals(weeklyReportEmailId) && !"Enter Email Address".equalsIgnoreCase(weeklyReportEmailId) && !Utility.validateEmail(weeklyReportEmailId.trim())) {
				logger.info("1");

				MessageUtil.setMessage("Please enter a valid email address to receive weekly campaign report to. ", "color:red");
				campaignReport = true;
				return;
			}
			else {
				//Setting First Email id
				logger.info("storing report email"+weeklyReportEmailId);
				user.setWeeklyReportEmailId(weeklyReportEmailId);
				//Setting Date & Time
				if(day != user.getWeeklyReportDay()) {
					user.setWeeklyReportDay(day);
				}

				Date sentDate = new Date();

				if(ampm == 0 && hours == 12)sentDate.setHours(00);
				else if(ampm == 1 && hours == 12)sentDate.setHours(12); 
				else if(ampm == 0) sentDate.setHours(hours);
				else sentDate.setHours(hours+12);
				sentDate.setMinutes(minutes);
				sentDate.setSeconds(0);
				Date userSettingsDate = user.getWeeklyReportTime();

				if(userSettingsDate != null){
					int userSettingHours = userSettingsDate.getHours();
					int userSettingMins = userSettingsDate.getMinutes();
					int userSettingAmPm = -1;

					if(userSettingHours >= 12){
						userSettingAmPm = 1;
						userSettingHours = userSettingHours - 12;
					}
					else {
						userSettingAmPm = 0;
					}

					if(userSettingHours != hours || userSettingMins != minutes || ampm != userSettingAmPm ) {
						user.setWeeklyReportTime(sentDate);
					}
				}
				else{
					user.setWeeklyReportTime(sentDate);
				}
			}// first email added

			//Validate Second email id
			
			
			if(emptyMessage && ! Constants.STRING_NILL.equals(expEmailId) && !"Enter Email Address".equalsIgnoreCase(expEmailId)&& !Utility.validateEmail(expEmailId.trim())) {
				logger.info("2");
				if( !campaignReport){
					MessageUtil.setMessage("Please enter a valid email address to receive schedule expiry alert to. ", "color:red");
					campaignExpiry = true;
					return;
				}
			}
			else{
				logger.info("storing expiry email"+expEmailId);
				user.setCampExpEmailId(expEmailId);
			}

			//Saving in Db
			logger.info("Saving Valid Email in Db");
			//usersDao.saveOrUpdate(user);
			usersDaoForDML.saveOrUpdate(user);
			MessageUtil.setMessage("Email alert settings has been saved successfully.", "color:blue","TOP");
			setWeeklyReportDivData();
			editWeeklyReportDivId.setVisible(false);
			weeklyReportDivId.setVisible(true); 
		}
		else{
			logger.info("Storing Null");
			user.setWeeklyReportEmailId(null);
			user.setCampExpEmailId(null);
			user.setWeeklyReportDay(0);
			user.setWeeklyReportTime(null);
			//user.setWeeklyReportTime(null);
			//usersDao.saveOrUpdate(user);
			usersDaoForDML.saveOrUpdate(user);
			MessageUtil.setMessage("Email alert settings has been updated successfully.", "color:blue","TOP");
			setWeeklyReportDivData();
			editWeeklyReportDivId.setVisible(false);
			weeklyReportDivId.setVisible(true); 
			logger.info(".................:"+emptyMessage);
		}
		
		
		
	
	}
	
	private void setWeeklyReportDivData(){
		logger.debug(">>>>>>> Started  setWeeklyReportDivData :: ");
		Users users = usersDao.find(user.getUserId());
		
		boolean weeklyEmailCampaignCheckId = users.isWeeklyReportTypeEmail();
		boolean weeklySmsCampaignCheckId = users.isWeeklyReportTypeSMS();
		
		if(weeklyEmailCampaignCheckId || weeklySmsCampaignCheckId){

			if(users.isWeeklyReportTypeEmail()){
				emailCampaignChkId.setChecked(true);
				emailTextboxDivId.setVisible(true);
				dayTimeLBDivId.setVisible(true);
			}
			if(users.isWeeklyReportTypeSMS()){
				smsCampaignChkId.setChecked(true);
				emailTextboxDivId.setVisible(true);
				dayTimeLBDivId.setVisible(true);
			}
			if(users.getWeeklyReportEmailId() != null){

				sendWeeklyCampaignReportLbId.setValue(users.getWeeklyReportEmailId());
			}
			else{
				sendWeeklyCampaignReportLbId.setValue("--");
			}
			Date sentTime = users.getWeeklyReportTime();

			String setTime = Constants.STRING_NILL;
			if(sentTime!=null) {
				int hours = sentTime.getHours();
				int displayMin = sentTime.getMinutes();
				String displayHours = Constants.STRING_NILL;
				String mins = "00" ;

				if(displayMin == 0 ){
					mins = "00" ;
				}
				else{
					mins = "30";
				}

				if(hours ==0) {
					displayHours = "12"+":"+mins+" AM";
				}else if(hours == 12){
					displayHours = "12"+":"+mins+" PM";

				}else if(hours > 12){
					displayHours = (hours-12 )+":"+mins+" PM";
				}
				else if(hours < 12){
					displayHours = hours+":"+mins+" AM";
				}

				setTime = getDayFromList(users.getWeeklyReportDay());
				setTime = setTime +", "+displayHours;

			}
			else{
				setTime = "--";
			}
			setDayTimeLbId.setValue(setTime);

		}else {
			sendWeeklyCampaignReportLbId.setValue("--");
			setDayTimeLbId.setValue("--");
		}
		
		if(users.getCampExpEmailId() != null ){
			sendScheduleExpiryAlertLbId.setValue(users.getCampExpEmailId());
			//	saveWeeklyReportDataBtnId.setLabel("Update");
			//	saveWeeklyReportDataBtnId.setSclass("");
		}
		else{
			sendScheduleExpiryAlertLbId.setValue("--");
		}
		logger.debug("<<<<< Completed setWeeklyReportDivData .");
	}
	
	public String getDayFromList(int index){
		String day = Constants.STRING_NILL;

		switch(index)
		{
		case 1:
			day = "Monday";
			break;
		case 2:
			day = "Tuesday";
			break;
		case 3:
			day = "Wednesday";
			break;
		case 4:
			day = "Thursday";
			break;
		case 5:
			day = "Friday";
			break;
		case 6:
			day = "Saturday";
			break;
		default :
			day = "Sunday";
			break;
		}
		return day;
	}
	
	private void setWeeklyReportData() {
		// TODO Auto-generated method stub
		Users users = usersDao.find(user.getUserId());
		if(users.getWeeklyReportEmailId() == null || Constants.STRING_NILL.equals(users.getWeeklyReportEmailId())){
			weeklyReportEmailTbId.setText("Enter Email Address");
			
		}
		else{
			weeklyReportEmailTbId.setText(users.getWeeklyReportEmailId());
			
			saveWeeklyReportDataBtnId.setLabel("Update");
			saveWeeklyReportDataBtnId.setSclass("");
		
				Date sentTime = users.getWeeklyReportTime();
				
				if(sentTime!=null) {
					
					int hours = sentTime.getHours();
					/*if(hours ==0) {
						hoursLbId.setSelectedIndex(11);
						ampmLbId.setSelectedIndex(0);
					}
					if(hours == 12){
						hoursLbId.setSelectedIndex(11);
						ampmLbId.setSelectedIndex(1);
						
					}*/
					if(hours > 12){
						hoursLbId.setSelectedIndex(hours-13);
						ampmLbId.setSelectedIndex(1);
						
					}else if(hours == 0) {
						hoursLbId.setSelectedIndex(11);
						ampmLbId.setSelectedIndex(0);
					}else if(hours == 12){
						hoursLbId.setSelectedIndex(11);
						ampmLbId.setSelectedIndex(1);
					}else {
						hoursLbId.setSelectedIndex(hours-1);
						ampmLbId.setSelectedIndex(0);
					}
					
					int mins = sentTime.getMinutes();
					if(mins == 0 ) minLbId.setSelectedIndex(0);
					else minLbId.setSelectedIndex(1);
					
					daysLbId.setSelectedIndex(users.getWeeklyReportDay());
				}
		}
		
		if(users.getCampExpEmailId() == null ||  Constants.STRING_NILL.equals(users.getCampExpEmailId()) ){
			
			expiryEmailTbId.setText("Enter Email Address");
		}
		else{
			expiryEmailTbId.setText(users.getCampExpEmailId());
			saveWeeklyReportDataBtnId.setLabel("Update");
			saveWeeklyReportDataBtnId.setSclass("");
		}
	}

	//Added for 2.3.11
	public void onClick$modifyBtnId(){
		logger.debug(">>>>>>> Started  onClick$modifyBtnId :: ");
		
		editWeeklyReportDivId.setVisible(true);
		weeklyReportDivId.setVisible(false);
		setWeeklyReportData();
		logger.debug("<<<<< Completed onClick$modifyBtnId .");
	}
	
	public void onCheck$emailCampaignChkId(){
		displayDivComponents();
	}
	
	public void onCheck$smsCampaignChkId(){
		displayDivComponents();
	}
	
	private void displayDivComponents() {
		if((!emailCampaignChkId.isChecked()) && (!smsCampaignChkId.isChecked())){
			emailTextboxDivId.setVisible(false);
			dayTimeLBDivId.setVisible(false);
		}
		if(emailCampaignChkId.isChecked() || smsCampaignChkId.isChecked()){
			emailTextboxDivId.setVisible(true);
			dayTimeLBDivId.setVisible(true);
		}
	}
	
}//EOF
