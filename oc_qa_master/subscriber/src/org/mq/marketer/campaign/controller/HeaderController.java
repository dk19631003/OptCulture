package org.mq.marketer.campaign.controller;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.Redirect;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;

public class HeaderController extends GenericForwardComposer {
	
	private Label userWelcomeId;
	private Image goToRMHomeImgId;
	private Div logoBgDivId, rightDivId;
	private A logoutAId, smsCreditsAId, emailCreditsAId;
	private UsersDao usersDao = null;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);	 
	
	public HeaderController() { }
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		logger.info("GetUser.getUserInfo()="+GetUser.getUserInfo()); // Call this method to Set the UserName in Session
		String branding = GetUser.getUserObj().getUserOrganization().getBranding();
		
		logger.info("branding="+branding);
		
		if(branding!=null && branding.equals("OPT")) {
			logoBgDivId.setStyle("background:url('/subscriber/img/opt_back_strip.png') repeat-x 0 0;");
			goToRMHomeImgId.setSrc("/img/opt_logo.png");
			rightDivId.setStyle("margin-right:10px; color:#FFFFFF;");
			logoutAId.setStyle("padding:0 10px; color:#FFFFFF;");
		}
		else {
		}
		
		String username = GetUser.getOnlyUserName();
		logger.info("logged userName is ========="+username);
		userWelcomeId.setValue(username);
		updateCredits();
		
		
	}
	
	private void updateCredits(){
		try{
			usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		
			Users user = usersDao.find(GetUser.getUserId());
				
				if(user.getEmailCount()==null){
					logger.info("SmsCount="+user.getSmsCount()+"UsedSmsCount="+user.getUsedSmsCount());
					emailCreditsAId.setLabel(0+"");
				}
				else if(user.getUsedEmailCount()==null){
					logger.info("EmailCount="+user.getEmailCount()+"UsedEmailCount="+user.getUsedEmailCount());
					emailCreditsAId.setLabel(user.getEmailCount()+"");
				}
				else{
					long avblEmailCredit = user.getEmailCount() - user.getUsedEmailCount();
					emailCreditsAId.setLabel(avblEmailCredit+""); 
				}
				
				if(user.getSmsCount()==null){
					logger.info("SmsCount="+user.getSmsCount()+"UsedSmsCount="+user.getUsedSmsCount());
					smsCreditsAId.setLabel(0+"");
				}
				else if(user.getUsedSmsCount()==null){
					smsCreditsAId.setLabel(user.getSmsCount()+"");
				}
				else{
					long avblSMSCredit = user.getSmsCount() - user.getUsedSmsCount();
					smsCreditsAId.setLabel(avblSMSCredit+"");
				}
		}catch(Exception e){
		logger.info("exception while fetching user email/sms credits"+e);
		}
		
	}
	
	public void onClick$smsCreditsAId() {
		updateCredits();
	}
	public void onClick$emailCreditsAId() {
		updateCredits();
	}   
	
	public void onClick$feedbackToolbarBtnId() {
     try {
			//		goToPage("general/feedback&quot");
    	 	session.setAttribute("feedbackHeaderLbl", "Feedback");
    	 	Redirect.goTo(PageListEnum.EMPTY);
			Redirect.goTo(PageListEnum.GENERAL_FEEDBACK);
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}
	
	
	public void onClick$goToRMHomeImgId() {
		try {
			
			
			/**
	         * DashBoard Menu will be disabled if the role is StoreOpWebRedemption 
	         */

	       try {
	           if(session.getAttribute("userRoleSet") instanceof Set) {
	               Set<String> userRoleSet = (Set<String>)session.getAttribute("userRoleSet");
	               
	               logger.info("userRoleSet.contains(RightsEnum.MenuItem_Loyalty_Menu_Customer_LookUp_Fbb_VIEW) :"+userRoleSet.contains(Constants.MENUITEM_LOYALTY_MENU_CUSTOMER_LOOKUP_FBB_VIEW));
	                
	               if(userRoleSet != null && userRoleSet.contains(Constants.MENUITEM_LOYALTY_MENU_CUSTOMER_LOOKUP_FBB_VIEW)) { 
	                   Redirect.goTo(PageListEnum.EMPTY);
	                   Redirect.goTo(PageListEnum.CUSTOMER_LOOKUP_FBB);
	               }else {
	            	   Redirect.goTo(PageListEnum.RM_HOME);
	               }
	           }else {
	        	   Redirect.goTo(PageListEnum.RM_HOME);
	           }
	       } catch (Exception e) {
	           // TODO Auto-generated catch block
	           logger.error("Exception ::", e);
	           Redirect.goTo(PageListEnum.RM_HOME);
	       }
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}
	
	// Added for support view page 
	public void onClick$supportToolbarBtnId() {
	     try {
				//		goToPage("general/feedback&quot");
	    	 	//session.setAttribute("supportHeaderLbl", "Support");
	    	 	Redirect.goTo(PageListEnum.EMPTY);
				Redirect.goTo(PageListEnum.SUPPORT);
			} catch (Exception e) {
				logger.error("Exception ::::", e);
			}
		}
	
	
}
