package org.mq.marketer.campaign.controller.campaign;

import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignSchedule;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.layout.EditorController;
import org.mq.marketer.campaign.custom.MultiLineMessageBox;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CampaignScheduleDao;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.CampaignsDaoForDML;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.general.CampaignStepsEnum;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.HTMLUtility;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.RightsEnum;
import org.mq.marketer.campaign.general.SpamChecker;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Toolbarbutton;

@SuppressWarnings({"serial","unused"})
public class TextMessageController extends GenericForwardComposer {
 
	private Textbox plainMsgTbId;
	private Button saveAsDraftBtnId;
	private Session session;
	private Campaigns campaign;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private String isEdit = null;
	UserActivitiesDao userActivitiesDao = null; 
	UserActivitiesDaoForDML userActivitiesDaoForDML = null; 
	private Listbox phLbId, cphLbId;
	private Groupbox coupGrpbId;
	private Button backBtnId,nextBtnId;
	private Toolbarbutton checkSpamTlbId;
	private boolean isAdmin;
	private Users currentUser;
	private Timer textAutoSaveTimerId;
	private Label autoSaveLbId;
	private CampaignScheduleDao campaignScheduleDao;
	public TextMessageController() {
		try {
			this.session = Sessions.getCurrent();
			this.campaignScheduleDao = (CampaignScheduleDao)
					SpringUtil.getBean("campaignScheduleDao");
			campaign = (Campaigns)session.getAttribute("campaign");
			isEdit = (String)session.getAttribute("editCampaign");
			
			String style = "font-weight:bold;font-size:15px;color:#313031;" +
					"font-family:Arial,Helvetica,sans-serif;align:left";
	PageUtil.setHeader("Create Email (Step 5 of 6)","",style,true);
	
			
			if(isEdit!=null){
				if(campaign==null){
					//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW);
					Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
				}
			}else{
				/*if(Utility.isSomethingWrong(campaign, CampaignStepsEnum.CampTextMsg.getPos())){
					MessageUtil.setMessage(OCConstants.CAMPAIGN_MSG, "color:blue;");
					Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
					
				}*/
			}
			
			Utility.breadCrumbFrom(5);
			
			userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
			userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");

			 /*if(userActivitiesDao != null) {
			      	userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_TEXT_MESSAGE,GetUser.getUserObj());
			 }*/
			 if(userActivitiesDaoForDML != null) {
			      	userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_TEXT_MESSAGE,GetUser.getLoginUserObj());
			 }
			 
			 isAdmin = (Boolean)session.getAttribute("isAdmin");
		} catch (Exception e) {
			logger.error("Error **",e);
		}
	}
	
	//public void init(Textbox plainMsgTbId, Button saveAsDraftBtnId, Listbox phLbId){
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
	
		currentUser = GetUser.getUserObj();
		/*this.plainMsgTbId = plainMsgTbId;
		this.saveAsDraftBtnId = saveAsDraftBtnId;*/
		
		if(campaign==null){
			MessageUtil.setMessage("There was a problem encountered. Please try after some time.", "color:blue", "TOP");
			logger.error("** Exception : campaign is null");
			return;
		}
//		if(logger.isDebugEnabled()) logger.debug("Text message : "+campaign.getTextMessage());
//		plainMsgTbId.setValue(campaign.getTextMessage()!=null?campaign.getTextMessage():"");
		
		if(campaign.getTextMessage()!=null && campaign.getTextMessage().trim().length()>0) {
			plainMsgTbId.setValue(campaign.getTextMessage());
		}
		else {
				
				plainMsgTbId.setValue(HTMLUtility.getTextFromHtml(campaign.getHtmlText()));
		}
		
		if(isEdit!=null){
			if(isEdit.equalsIgnoreCase("edit")){
				saveAsDraftBtnId.setVisible(false);
//				saveAsDraftBtnId.setVisible(false);
			}
		}
		EditorController.getPlaceHolderList(campaign.getMailingLists(), false, phLbId);
		/*phLbId.setItemRenderer(new phListRenderer());
		phLbId.setModel(new SimpleListModel(placeHoldersList));*/
		
		Set<String> userRoleSet = (Set<String>)Sessions.getCurrent().getAttribute("userRoleSet");
		//logger.debug("userRoleSet ::"+userRoleSet);
			//PageListEnum plEnum = PageListEnum.ADMIN_VIEW_COUPONS;
			if(userRoleSet.contains(RightsEnum.MenuItem_Promocodes_VIEW.name())) {
					
			List<String> couponPhList = EditorController.getCouponsList();
			cphLbId.setItemRenderer(new phListRenderer());
			cphLbId.setModel(new SimpleListModel(couponPhList));
			coupGrpbId.setVisible(true);
		
		}
		
	}
	
	public void onTimer$textAutoSaveTimerId() {
		TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
		CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
		CampaignsDaoForDML campaignsDaoForDML = (CampaignsDaoForDML)SpringUtil.getBean("campaignsDaoForDML");
		Campaigns dbcampaign = campaignsDao.findByCampaignId(campaign.getCampaignId());
		if (campaign.getStatus().equalsIgnoreCase("Draft") && !(campaign.getDraftStatus().equalsIgnoreCase("complete") || campaign.getDraftStatus().equalsIgnoreCase("CampFinal"))) {
		if((isEdit == null || isEdit.equalsIgnoreCase("view")) && Utility.isSomethingWrong(campaign, CampaignStepsEnum.CampTextMsg.getPos())){
			autoSaveLbId.setValue("Failed to Auto save. Reason: This step is completed, perhaps from another tab/browser.");
			return;
		}  
		}else if(!dbcampaign.getStatus().equalsIgnoreCase("Draft") && dbcampaign.getDraftStatus().equalsIgnoreCase("complete")) {
			if((isEdit == null || isEdit.equalsIgnoreCase("view")) && Utility.isSomethingWrong(campaign, CampaignStepsEnum.CampSett.getPos())){
				autoSaveLbId.setValue("Failed to Auto save. Reason: This step is completed, perhaps from another tab/browser.");
				return ;
			}} 
		
		String plainTxtMsg = plainMsgTbId.getValue()!=null?plainMsgTbId.getValue():" ";
		campaign.setTextMessage(plainTxtMsg);
		session.setAttribute("campaign", campaign);
		campaignsDaoForDML.saveOrUpdate(campaign);
		
		//saveTextMessage(false);
		autoSaveLbId.setValue("Last auto-saved at: "+ MyCalendar.calendarToString(Calendar.getInstance(),
				MyCalendar.FORMAT_SCHEDULE_TIME,tz));
	}
	
	public void onClick$saveHtmlId() {
		saveTextMessage(true);
	} 
	
	public boolean saveTextMessage(boolean flag){
		try{
			MessageUtil.clearMessage();
			CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao"); 
			Campaigns dbcampaign = campaignsDao.findByCampaignId(campaign.getCampaignId());
			if (!campaign.getStatus().equalsIgnoreCase("Draft") && !campaign.getDraftStatus().equalsIgnoreCase("complete")) {
			if((isEdit == null || isEdit.equalsIgnoreCase("view")) && Utility.isSomethingWrong(campaign, CampaignStepsEnum.CampTextMsg.getPos())){
				MessageUtil.setMessage(OCConstants.CAMPAIGN_MSG, "color:blue;");
				Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
				return false;
			}
			}else if(!dbcampaign.getStatus().equalsIgnoreCase("Draft") && dbcampaign.getDraftStatus().equalsIgnoreCase("complete")) {
				if((isEdit == null || isEdit.equalsIgnoreCase("view")) && Utility.isSomethingWrong(campaign, CampaignStepsEnum.CampSett.getPos())){
					MessageUtil.setMessage(OCConstants.CAMPAIGN_MSG, "color:blue;");
					Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
					return false;
				}} 
			
			if(flag) {
			String isValidPhStr = null;
			isValidPhStr = Utility.validatePh(plainMsgTbId.getValue(), currentUser);
			
			if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
				
				MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
				return false;
			}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
				
				MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
				return false;
				
			}
			
			String isValidCCDim = null;
			isValidCCDim = Utility.validateCouponDimensions(plainMsgTbId.getValue());
			if(isValidCCDim != null){
				return false;
			}
			String isValidCouponAndBarcode = null;
			isValidCouponAndBarcode = Utility.validateCCPh(plainMsgTbId.getValue(), currentUser);
			if(isValidCouponAndBarcode != null){
				return false;
			}
			}
			
			//CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
			CampaignsDaoForDML campaignsDaoForDML = (CampaignsDaoForDML)SpringUtil.getBean("campaignsDaoForDML");
			if(campaign == null){
				MessageUtil.setMessage("Problem in saving the text message.", "color:blue", "TOP");
				logger.error("** Exception : campaign is null");
				return false;
			}
			String plainTxtMsg = plainMsgTbId.getValue()!=null?plainMsgTbId.getValue():" ";
			
			if(!checkSpam(plainTxtMsg, false))
				return false;
			
			campaign.setTextMessage(plainTxtMsg);
			if(isEdit != null){
				if(isEdit.equalsIgnoreCase("view")){
					//campaign.setStatus("Draft");
					//campaign.setStatusChangedOn(Calendar.getInstance());
					//campaign.setDraftStatus("CampFinal");

					if((campaign.getDraftStatus()==null) || 
							CampaignStepsEnum.CampTextMsg.getPos() >= CampaignStepsEnum.valueOf(campaign.getDraftStatus()).getPos()) {
						campaign.setDraftStatus("CampFinal");
					}
					
				}else if(isEdit.equalsIgnoreCase("edit")){
					campaign.setModifiedDate(Calendar.getInstance());
				}
			}else{
				//campaign.setStatus(Constants.CAMP_STATUS_DRAFT);
				campaign.setStatusChangedOn(Calendar.getInstance());
				//campaign.setDraftStatus("CampFinal");
				
				if((campaign.getDraftStatus()==null) || 
						CampaignStepsEnum.CampTextMsg.getPos() >= CampaignStepsEnum.valueOf(campaign.getDraftStatus()).getPos()) {
					campaign.setDraftStatus("CampFinal");
				}
			}
			session.setAttribute("campaign", campaign);
			//campaignsDao.saveOrUpdate(campaign);
			campaignsDaoForDML.saveOrUpdate(campaign);
			
			if(flag) {
			MessageUtil.setMessage("Text message saved.", "color:blue", "TOP");
			}
			
			/*if(userActivitiesDao != null) {
        		userActivitiesDao.addToActivityList( ActivityEnum.CAMP_CRE_ADD_TEXTMSG_p2campaignName,GetUser.getUserObj(),campaign.getCampaignName());
			}*/
			if(userActivitiesDaoForDML != null) {
        		userActivitiesDaoForDML.addToActivityList( ActivityEnum.CAMP_CRE_ADD_TEXTMSG_p2campaignName,GetUser.getLoginUserObj(),campaign.getCampaignName());
			}
			
			return true;
		}catch (Exception e) {
			logger.error("** Exception : Problem while saving the plain text message - " + e);
			return false;
		}
	}
	
	//public void gotoConfirmPage(){
	public void onClick$nextBtnId() throws Exception {
		if(!saveTextMessage(true)) return;;
		if(isEdit!=null){
			if(isEdit.equalsIgnoreCase("view")){
				session.removeAttribute("editCampaign");
			}
		}
		
		/*if(userActivitiesDao != null) {
    		userActivitiesDao.addToActivityList(ActivityEnum.CAMP_CRE_ADD_TEXTMSG_p2campaignName, GetUser.getUserObj(), campaign.getCampaignName());
		}*/
		if(userActivitiesDaoForDML != null) {
    		userActivitiesDaoForDML.addToActivityList(ActivityEnum.CAMP_CRE_ADD_TEXTMSG_p2campaignName, GetUser.getLoginUserObj(), campaign.getCampaignName());
		}
		//Executions.sendRedirect("/Home.zul?pageToLoad=campaign/CampFinal&userName="+ userName);
		Redirect.goTo(PageListEnum.CAMPAIGN_FINAL);
	}
	
	//public void saveAsDraft(){
	public void onClick$saveAsDraftBtnId() throws Exception {
		List<CampaignSchedule> campScheduleList = campaignScheduleDao.getByCampaignId(campaign.getCampaignId());
		long activeCount = campScheduleList.stream().filter(campaignSchedule -> campaignSchedule.getStatus() == 0).count(); 
		if(campScheduleList.size() == 0 || activeCount == 0) {
			if(!saveTextMessage(true)) return;;
			session.removeAttribute("editCampaign");
			session.removeAttribute("campaign");
			//Executions.sendRedirect("/Home.zul?pageToLoad=campaign/View&userName="+ userName);
			/*if(userActivitiesDao != null) {
	    		userActivitiesDao.addToActivityList(ActivityEnum.CAMP_CRE_SAVE_AS_DRAFT_p1campaignName, GetUser.getUserObj(), campaign.getCampaignName());
			}*/
			if(userActivitiesDaoForDML != null) {
	    		userActivitiesDaoForDML.addToActivityList(ActivityEnum.CAMP_CRE_SAVE_AS_DRAFT_p1campaignName, GetUser.getLoginUserObj(), campaign.getCampaignName());
			}
			//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW);
			Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
		}else {
			MessageUtil.setMessage("A campaign with upcoming schedule/s cannot be saved as a draft.\n Please delete all active schedules first.", "color:red");
			return;
		}
	}
	
	//public void back(){
	public void onClick$backBtnId() throws Exception {
		if(isEdit!=null){
			if(isEdit.equalsIgnoreCase("edit"))
				Redirect.goTo(PageListEnum.CAMPAIGN_FINAL);
			else if(isEdit.equalsIgnoreCase("view"))
				//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW);
				Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
		}else{
			String editorType = (String)session.getAttribute("editorType");
			session.setAttribute("editCampaign", "edit");
			session.setAttribute("isTextMsgBack", "true");
			if(editorType!= null && editorType.equalsIgnoreCase("blockEditor"))
				Redirect.goTo(PageListEnum.CAMPAIGN_BLOCK_EDITOR);
			else if(editorType!= null && editorType.equalsIgnoreCase("plainTextEditor"))
				Redirect.goTo(PageListEnum.CAMPAIGN_PLAIN_EDITOR);
			else if(editorType!= null && editorType.equalsIgnoreCase("beeEditor"))
				Redirect.goTo(PageListEnum.CAMPAIGN_HTML_BEE_EDITOR);
			else if(editorType!= null && editorType.equalsIgnoreCase("plainHtmlEditor"))
				Redirect.goTo(PageListEnum.CAMPAIGN_HTML_EDITOR);
			else
				Redirect.goTo(PageListEnum.CAMPAIGN_LAYOUT);
			
		}
	}
	
	//public void reload(){
	public void onClick$reloadTblBtnId() {
		try{
			int confirm = Messagebox.show("Do you want to reload the message?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm != 1){
				return;
			}else{
				String textMsg = "";
				if(isEdit == null)
					textMsg = "";
				else
					textMsg = campaign.getTextMessage();
				plainMsgTbId.setValue(textMsg);
			}
		}catch (Exception e) {
			logger.error("** Exception : " + e + "**");
		}
	}
	
	public void onClick$checkSpamTlbId() throws Exception {
		checkSpam(plainMsgTbId.getValue(), true);
	}
	
	public boolean checkSpam(String htmlStuff, boolean isWinDisplay) {
		boolean result = false;
		if(campaign==null) {
			return result;
		}
		
		String sub = campaign.getSubject();
		String emlFilePath = PropertyUtil.getPropertyValue("usersParentDirectory") + "/" + campaign.getUsers().getUserName() + "/message.eml";
		if(logger.isDebugEnabled())logger.debug("Eml file Path : " + emlFilePath);
		StringBuffer response = (new SpamChecker()).checkSpam(sub, htmlStuff, emlFilePath, false); 
		logger.debug("Spam Report : \n" + response );

		if(response==null || response.length() == 0 ) {
			return true;
		}

		String output;
		try {
			output = response.substring(response.indexOf("Content analysis details"));
		} catch (NumberFormatException e1) {
			logger.debug("NumberFormatException : ",(Throwable)e1);
			output = "Problem while getting spam report"; 
		}
		
		if(isWinDisplay) {
			String legend = "Score:\n\t 0  to  2 : Good;" +
			"\n\t 2  to  4 : Not bad;\n\tabove 4 : Bad.\n\n" ;
			output =  legend + output; 
			
			try {
				MultiLineMessageBox.doSetTemplate();
				MultiLineMessageBox.show(output, "Spam Info", MultiLineMessageBox.OK, "INFORMATION", false);
			} catch (InterruptedException e2) {
				logger.debug("Exeption : ", (Throwable)e2);
			}
		}
		else {
			try {
				int index = response.indexOf("Content analysis details");
				if(index < 0)	return false;
				
				output = response.substring(index);
				String scoreStr = output.substring(output.indexOf("(")+1,output.indexOf(")"));
				String[] scores = scoreStr.split(" ");
/*					for (String token : scores) {
						logger.debug("Token : " + token);
						
					}*/

				float hit = Float.parseFloat(scores[0]);
				if(0 <= hit && hit <= 2) {
					result = true;
				}
				else if(2 < hit && hit <= 4) {
					try {
						int confirm = Messagebox.show("Email has spam content. Do you want to continue saving email?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						result = (confirm == 1);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::", e);
					}
				}
				else if(hit > 4) {
					MessageUtil.setMessage("Email has SPAM content. Click on \"Check for Spam score\" for more details.", "color:red", "TOP");
					result = false;
				}
			} catch (NumberFormatException e) {
				logger.debug("NumberFormatException : ", (Throwable)e);
			} catch (Exception e) {
				logger.debug("Exeption : ", (Throwable)e);
			}
		}
		return result;
	}
	
	public void onClick$grabTxtFrHtmlIdTBarBtnId() {
		grabTextFromHTML();
	}
	
	public void grabTextFromHTML(){
		if(campaign!=null) {
			plainMsgTbId.setValue(HTMLUtility.getTextFromHtml(campaign.getHtmlText()));
		}
	}
	Textbox testEmailTbId;
	
	// changes here
	public void onClick$sendBtnId() {
		
		/*if(Utility.validateHtmlSize(campaign.getHtmlText())) {
			Messagebox.show("It looks like the content of your email is more " +
					"than the recommended size per the email-" + 
					"sending best practices. " + "To avoid email landing " + 
					"in recipient's spam folder, please redesign " 
								+"email to reduce content.", "Info", Messagebox.OK, Messagebox.INFORMATION);
		}*/
		
		long size = Utility.validateHtmlSize(campaign.getHtmlText());
		if(size >100) {
			String msgcontent = OCConstants.HTML_VALIDATION;
			msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
			MessageUtil.setMessage(msgcontent, "color:Blue");
		}
		
		
		String isValidCouponAndBarcode = null;
		isValidCouponAndBarcode = Utility.validateCCPh(campaign.getHtmlText(), currentUser);
		if(isValidCouponAndBarcode != null){
			return;
		}
		
		
		String emailId=testEmailTbId.getValue();
		 if( emailId.equals("Email Address...") || emailId.isEmpty() ){
				MessageUtil.setMessage("There is no mail id to send a test mail.", "color:red");
				testEmailTbId.setValue("Email Address...");
				return;
			}
		 else if(sendTestMail(testEmailTbId)) {
			testEmailTbId.setValue("Email Address...");
			
		}
	}
	
	/**
	 * this method sends the test mail
	 */
		/*public void sendTestMail(Textbox tempTestEmailTboxId) {
			try {
				String emailId = tempTestEmailTboxId.getValue();
				
				logger.debug("Test mailId is: "+emailId);
				
				if(!Utility.validateEmail(emailId)) {
					
					Messagebox.show("Please enter a valid email address.", "Captiway", Messagebox.OK, Messagebox.ERROR);
					return;
				} else {
	
					MessageUtil.setMessage("Test email will be sent in a moment.", "color:blue", "top");
					EmailQueueDao emailQueueDao = (EmailQueueDao)SpringUtil.getBean("emailQueueDao");
					EmailQueue testEmailQueue = new EmailQueue(campaign, Constants.EQ_TYPE_TESTMAIL,"Active",emailId,MyCalendar.getNewCalendar(),campaign.getUsers()); 
					testEmailQueue.setMessage(campaign.getHtmlText());
					emailQueueDao.saveOrUpdate(testEmailQueue);
					tempTestEmailTboxId.setValue("Email Address...");
				
				} 
			}catch (Exception e) {
				logger.error("** Exception : Error occured while sending test email.", e);
			}
			
		}*/
	
	
	public boolean sendTestMail(Textbox tempTestEmailTboxId) {
		
		
		try {
			
			if(campaign==null){
				logger.error("Exception :Campaign was null");
			}
			
			String emailId = tempTestEmailTboxId.getValue();
			try {
				
				if(logger.isDebugEnabled())logger.debug("Sending the test mail....");
				
				MessageUtil.clearMessage();
				
				
				String[] emailArr = null;
				
					emailArr = emailId.split(",");
					for (String email : emailArr) {
						
						if(!Utility.validateEmail(email.trim())) {
							
							Messagebox.show("Please enter valid email. '"+email+"' is invalid.", "Error", Messagebox.OK, Messagebox.ERROR);
							return true;
						}else {
							//Testing for invalid email domains-APP-308
								String result = PurgeList.checkForValidDomainByEmailId(email.trim());
								if(!result.equalsIgnoreCase("Active")) {
									Messagebox.show("Please enter email addresses with valid email domains. '"+email+"' is invalid.","Error",Messagebox.OK,Messagebox.ERROR);
									return true;
								}
						}
						
						//Utility.sendTestMail(campaign, campaign.getHtmlText(), email);
						
					}//for
					
					//Check whether user is expired or not
					if(Calendar.getInstance().after(currentUser.getPackageExpiryDate())){
						logger.debug("Current User::"+currentUser.getUserId()+" is expired, hence cannot send test mail");
						/*MessageUtil.setMessage("Your account validity period has expired. Please renew your subscription to continue.", "color:red", "TOP");*/
						return false;
					}
					
					for (String email : emailArr) {
						
				//		Utility.sendTestMail(campaign, campaign.getHtmlText(), email);
						Utility.sendInstantMail(campaign, campaign.getSubject(), campaign.getHtmlText(),
								Constants.EQ_TYPE_TESTMAIL, email, null );
						
						
						
					}//for
					/*if(userActivitiesDao != null) {
	            		userActivitiesDao.addToActivityList(ActivityEnum.CAMP_SENT_TSTMAIL_p1campaignName, GetUser.getUserObj(), campaign.getCampaignName());
					}*/
					if(userActivitiesDaoForDML != null) {
	            		userActivitiesDaoForDML.addToActivityList(ActivityEnum.CAMP_SENT_TSTMAIL_p1campaignName, GetUser.getLoginUserObj(), campaign.getCampaignName());
					}
					
					if( campaign.getHtmlText()==null ||  campaign.getHtmlText().isEmpty()) {
						Messagebox.show("Email content cannot be empty.", "Error", Messagebox.OK, Messagebox.ERROR);
						return false;
					}
					
					Messagebox.show("Test mail will be sent in a moment.", "Information", 
							Messagebox.OK, Messagebox.INFORMATION);
					return true;
				
			} catch (Exception e) {
				logger.error("Exception : ", e);
				return false;
			}
			
			
		}catch (Exception e) {
			logger.error("** Exception : Error occured while sending test email.", e);
			return false;
		}
		
	}
		
	
	
	public class phListRenderer implements ListitemRenderer {

		@Override
		public void render(Listitem item, Object data,int arg2) throws Exception {
			if(data instanceof String) {
				String phStr = (String)data;
				if(phStr.indexOf(Constants.DELIMETER_DOUBLECOLON) < 0) return;
				String[] tokens = phStr.split(Constants.DELIMETER_DOUBLECOLON);
				item.setLabel(tokens[0]);
				item.setValue(tokens[1]);
			}
		}
	}
	
	//here changes
	public void onBlur$testEmailTbId() throws Exception {
		 
		 String mail=testEmailTbId.getValue();
		 ////logger.debug("here in on blur method mail id "+mail);
		 if(mail.equals("Email Address...") || mail.equals("")){
			 testEmailTbId.setValue("Email Address...");
			 
		 }
	 }
	
	public void onFocus$testEmailTbId() throws Exception {
	 	String mail=testEmailTbId.getValue(); 
		if(mail.equals("Email Address...") || mail.equals("")){
			 testEmailTbId.setValue("");
			 
		 } 
	 }

}
