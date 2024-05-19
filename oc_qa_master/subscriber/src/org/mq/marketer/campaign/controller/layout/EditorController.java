package org.mq.marketer.campaign.controller.layout;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignSchedule;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.EmailContent;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyThresholdBonus;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.ReferralProgram;
import org.mq.marketer.campaign.beans.SystemTemplates;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.PrepareFinalHtml;
import org.mq.marketer.campaign.custom.MultiLineMessageBox;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CampaignScheduleDao;
import org.mq.marketer.campaign.dao.CampaignScheduleDaoForDML;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.CampaignsDaoForDML;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.MyTemplatesDaoForDML;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.general.CampaignStepsEnum;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.HTMLUtility;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PlaceHolders;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.SpamChecker;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDao;
import org.mq.optculture.data.dao.ReferralProgramDao;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataIntegrityViolationException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;

@SuppressWarnings("serial")
public class EditorController extends GenericForwardComposer {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	String isEdit;
	String editorType = null;
	Session sessionScope = null;
	String userName = null;
	//Users user = null;
	Users currentUser;
	Campaigns campaign = null;
	UserActivitiesDao userActivitiesDao;
	UserActivitiesDaoForDML userActivitiesDaoForDML;
	private MyTemplatesDao myTemplatesDao;
	private static ReferralProgramDao referralprogramDao;

	private MyTemplatesDaoForDML myTemplatesDaoForDML;
	private static LoyaltyProgramDao loyaltyProgramDao;
	private static LoyaltyProgramTierDao loyaltyProgramTierDao;
	private static LoyaltyThresholdBonusDao loyaltyThresholdBonusDao;
	private static  String  userCurrencySymbol = "$ "; 
	private String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
	static final String APP_MAIN_URL = "https://app.optculture.com/subscriber/";
	static final String APP_MAIN_URL_HTTP = "http://app.optculture.com/subscriber/";
	static final String MAILCONTENT_URL = "http://mailcontent.info/subscriber/";
	static final String MAILHANDLER_URL = "http://mailhandler01.info/subscriber/";
	private static final String ImageServer_Url = PropertyUtil.getPropertyValue("ImageServerUrl");
	
	/*public EditorController(){
		String style = "font-weight:bold;font-size:15px;color:#313031;" +
				"font-family:Arial,Helvetica,sans-serif;align:left";
PageUtil.setHeader("Create Email (Step 4 of 6)","",style,true);

		
	}*/
	
	public EditorController(){
		String style = "font-weight:bold;font-size:15px;color:#313031;" +"font-family:Arial,Helvetica,sans-serif;align:left";
		HttpServletRequest  request = (HttpServletRequest)
		Executions.getCurrent().getNativeRequest();
		String	source = (String)request.getAttribute("source");
		if(source != null && !source.equalsIgnoreCase("Schedule") ){
			PageUtil.setHeader("Create Email (Step 4 of 6)","",style,true);
			
		}

		
	}

	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		
		sessionScope = Sessions.getCurrent();
		editorType = (String)sessionScope.getAttribute("editorType");
		
		logger.debug("-------------- Editor Type : "+editorType);
		
		isEdit = (String)sessionScope.getAttribute("editCampaign");
		campaign = (Campaigns)sessionScope.getAttribute("campaign");
		userName = GetUser.getUserName();
		//user = GetUser.getUserObj();
		currentUser = GetUser.getUserObj();
		userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		if(isEdit == null){
			/*if(Utility.isSomethingWrong(campaign, 4)){
				MessageUtil.setMessage(OCConstants.CAMPAIGN_MSG, "color:blue;");
				Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
				
			}*/
		}
		
	}
	protected void gotoPlainMsg(String htmlStuff,SystemTemplates st) {
		
		MessageUtil.clearMessage();
		String isValidPhStr = null;
		isValidPhStr = Utility.validatePh(htmlStuff, currentUser);
		
		if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
			
			MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
			return ;
		}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
			
			MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
			return;
			
		}
		
		String isValidCCDim = null;
		isValidCCDim = Utility.validateCouponDimensions(htmlStuff);
		if(isValidCCDim != null){
			return;
		}
		String isValidCouponAndBarcode = null;
		isValidCouponAndBarcode = Utility.validateCCPh(htmlStuff, currentUser);
		if(isValidCouponAndBarcode != null){
			return;
		}
		
		/*if(Utility.validateHtmlSize(htmlStuff)) {
			Messagebox.show("It looks like the content of your email is more " +
					"than the recommended size per the email-" + 
					"sending best practices. " + "To avoid email landing " + 
					"in recipient's spam folder, please redesign " 
								+"email to reduce content.", "Info", Messagebox.OK, Messagebox.INFORMATION);
		}*/
		
		long size =	Utility.validateHtmlSize(htmlStuff);
		if(size >100) {
			String msgcontent = OCConstants.HTML_VALIDATION;
			msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
			MessageUtil.setMessage(msgcontent, "color:Blue");
		}
		
		if(!saveEmail(htmlStuff,st, null, null, false)) return;
		setFromPage();
		
		/*		
		String textMessage  = HTMLUtility.getTextFromHtml(htmlStuff);
		if(campaign != null) {
			campaign.setTextMessage(textMessage);
		}
		*/
		if(campaign.getHtmlText()!=null && !HTMLUtility.getTextFromHtml(campaign.getHtmlText()).equalsIgnoreCase(campaign.getTextMessage())){
			int confirm = Messagebox.show("Would you like to use text from the template to create the plaintext message?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);					
		if(confirm==1) {
			campaign.setTextMessage(HTMLUtility.getTextFromHtml(campaign.getHtmlText()));
		}
		else
			logger.info("Previous plain text only");
		}
		
		if(isEdit!=null){
			if(isEdit.equalsIgnoreCase("view")){
				sessionScope.removeAttribute("editCampaign");
				Redirect.goTo(PageListEnum.CAMPAIGN_TEXT_MESSAGE);
			}else if(isEdit.equalsIgnoreCase("edit")){
				Redirect.goTo(PageListEnum.CAMPAIGN_FINAL);
			}
		}else{
			sessionScope.removeAttribute("selectedTemplate");
			Redirect.goTo(PageListEnum.CAMPAIGN_TEXT_MESSAGE);
		}
	}







    //Editing here
	protected void gotoPlainMsg(String htmlStuff,String jsonStuff,SystemTemplates st) {

		MessageUtil.clearMessage();
		String isValidPhStr = null;
		/*if(checkEmailContentEmptyOrNot(jsonStuff)) {
			System.out.println("Email content is empty");
			return;
		}
		logger.info("Email content is not empty");*/
		isValidPhStr = Utility.validatePh(htmlStuff, currentUser);

		if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){

			MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
			return ;
		}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){

			MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
			return;

		}

		/*String isValidCCDim = null;
		isValidCCDim = Utility.validateCouponDimensions(htmlStuff);
		if(isValidCCDim != null){
			return;
		}*/
		String isValidCouponAndBarcode = null;
		isValidCouponAndBarcode = Utility.validateCCPh(htmlStuff, currentUser);
		if(isValidCouponAndBarcode != null){
			return;
		}

/*		if(Utility.validateHtmlSize(htmlStuff)) {
			Messagebox.show("It looks like the content of your email is more " +
					"than the recommended size per the email-" + 
					"sending best practices. " + "To avoid email landing " + 
					"in recipient's spam folder, please redesign " 
								+"email to reduce content.", "Info", Messagebox.OK, Messagebox.INFORMATION);
		}*/
		long size =	Utility.validateHtmlSize(htmlStuff);
		if(size >100) {
			String msgcontent = OCConstants.HTML_VALIDATION;
			msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
			MessageUtil.setMessage(msgcontent, "color:Blue");
		}
		if(!saveEmail(htmlStuff,st, null, null, false,jsonStuff)) return;
		setFromPage();

		/*		
		String textMessage  = HTMLUtility.getTextFromHtml(htmlStuff);
		if(campaign != null) {
			campaign.setTextMessage(textMessage);
		}*/
		if(campaign.getHtmlText()!=null && !HTMLUtility.getTextFromHtml(campaign.getHtmlText()).equalsIgnoreCase(campaign.getTextMessage())){
			int confirm = Messagebox.show("Would you like to use text from the template to create the plaintext message?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);					
		if(confirm==1) {
			campaign.setTextMessage(HTMLUtility.getTextFromHtml(campaign.getHtmlText()));
		}
		else
			logger.info("Previous plain text only");
		}

		if(isEdit!=null){
			if(isEdit.equalsIgnoreCase("view")){
				sessionScope.removeAttribute("editCampaign");
				Redirect.goTo(PageListEnum.CAMPAIGN_TEXT_MESSAGE);
			}else if(isEdit.equalsIgnoreCase("edit")){
				Redirect.goTo(PageListEnum.CAMPAIGN_FINAL);
			}
		}else{
			sessionScope.removeAttribute("selectedTemplate");
			Redirect.goTo(PageListEnum.CAMPAIGN_TEXT_MESSAGE);
		}
	}

	//Error message if email content is empty
/*	public boolean checkEmailContentEmptyOrNot(String beeJsonContent){
		if(beeJsonContent.equalsIgnoreCase(Constants.DEFAULT_JSON_VALUE)){ 
			MessageUtil.setMessage("Email content cannot be empty", "color:red");
			return true;
		}
		return false;
	}*/
	
	
	
	
	//Json
	

	protected void saveAsDraft( String htmlStuff, String jsonStuff, SystemTemplates st) {

		MessageUtil.clearMessage();
		/*if(checkEmailContentEmptyOrNot(jsonStuff)) {
			logger.info("Email content is empty");
			return;
		}
		logger.info("Email content is not empty");*/
		if(!saveEmail(htmlStuff,st, null, null, false,jsonStuff)) return;

		setFromPage();

		/*if(userActivitiesDao != null) {
    		userActivitiesDao.addToActivityList(ActivityEnum.CAMP_CRE_SAVE_AS_DRAFT_p1campaignName, currentUser, campaign.getCampaignName());
		}*/
		if(userActivitiesDaoForDML != null) {
			userActivitiesDaoForDML.addToActivityList(ActivityEnum.CAMP_CRE_SAVE_AS_DRAFT_p1campaignName, GetUser.getLoginUserObj(), campaign.getCampaignName());
		}

		sessionScope.removeAttribute("campaign");
		sessionScope.removeAttribute("editCampaign");
		//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW);
		Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);

	}





	protected void saveAsDraft( String htmlStuff, SystemTemplates st) {
		
		MessageUtil.clearMessage();
		if(!saveEmail(htmlStuff, st, null, null, true)) return;
		
		setFromPage();
		
		/*if(userActivitiesDao != null) {
    		userActivitiesDao.addToActivityList(ActivityEnum.CAMP_CRE_SAVE_AS_DRAFT_p1campaignName, currentUser, campaign.getCampaignName());
		}*/
		if(userActivitiesDaoForDML != null) {
    		userActivitiesDaoForDML.addToActivityList(ActivityEnum.CAMP_CRE_SAVE_AS_DRAFT_p1campaignName, GetUser.getLoginUserObj(), campaign.getCampaignName());
		}
		
		sessionScope.removeAttribute("campaign");
		sessionScope.removeAttribute("editCampaign");
		//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW);
		Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
		
	}
 

	//orginal
		protected boolean saveEmail( String htmlStuff, SystemTemplates st , 
							CampaignSchedule campSchedule, String contentName, boolean flag) {
		
		try{
			
			if(logger.isDebugEnabled())logger.debug("-- just entered --");
			CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
			Campaigns dbcampaign = campaignsDao.findByCampaignId(campaign.getCampaignId());
			if (!campaign.getStatus().equalsIgnoreCase("Draft") && !campaign.getDraftStatus().equalsIgnoreCase("complete")) {
			if((isEdit == null || isEdit.equalsIgnoreCase("view")) && Utility.isSomethingWrong(campaign, 4)){
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
			MessageUtil.clearMessage();
			String isValidPhStr = null;
			isValidPhStr = Utility.validatePh(htmlStuff, currentUser);
			
			if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
				
				MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
				return false;
			}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
				
				MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
				return false;
				
			}
			
			String isValidCCDim = null;
			isValidCCDim = Utility.validateCouponDimensions(htmlStuff);
			if(isValidCCDim != null){
				return false;
			}
			String isValidCouponAndBarcode = null;
			isValidCouponAndBarcode = Utility.validateCCPh(htmlStuff, currentUser);
			if(isValidCouponAndBarcode != null){
				return false;
			}
			
			/*if(Utility.validateHtmlSize(htmlStuff)) {
				Messagebox.show("It looks like the content of your email is more " +
						"than the recommended size per the email-" + 
						"sending best practices. " + "To avoid email landing " + 
						"in recipient's spam folder, please redesign " 
									+"email to reduce content.", "Info", Messagebox.OK, Messagebox.INFORMATION);
			}*/
			
			long size =	Utility.validateHtmlSize(htmlStuff);
			if(size >100) {
				String msgcontent = OCConstants.HTML_VALIDATION;
				msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
				MessageUtil.setMessage(msgcontent, "color:Blue");
			}
			
			if(!checkSpam(htmlStuff)){
				return false;
			}
			
			if(htmlStuff.trim().length() == 0 && !flag==false){
				MessageUtil.setMessage("Email cannot be empty.", "color:red", "TOP");
				return false;
			}
			}
			
			//CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
			CampaignsDaoForDML campaignsDaoForDML = (CampaignsDaoForDML)SpringUtil.getBean("campaignsDaoForDML");
			campaign = (Campaigns)sessionScope.getAttribute("campaign");
			
			try{
				
				// if editing the content for resending schedule criteria
				if(campSchedule != null) {
					
					if(logger.isDebugEnabled()) {
						logger.debug(">>>>>> CampaignSchedule block ");
					}
					
					EmailContent emailContent;
					
					// if newly creating the content for this schedule
					if((emailContent = campSchedule.getEmailContent()) == null) {
						
						emailContent = new EmailContent();
						
						emailContent.setHtmlContent(htmlStuff);
						emailContent.setCampaignId(campaign.getCampaignId());
						emailContent.setName(contentName);
						campSchedule.setEmailContent(emailContent);
						
					}
					else {
						
						emailContent.setHtmlContent(htmlStuff);
						emailContent.setName(contentName);
						campSchedule.setEmailContent(emailContent);
						
						
						// editing the already existed emailContent of CampaignSchedule object
						campSchedule.getEmailContent().setHtmlContent(htmlStuff);
					}
					
					try {
						
						if(logger.isDebugEnabled()) {
							logger.debug(">>>>>> saving the emailcontent ");
						}
						
						//EmailContentDao emailContentDao = (EmailContentDao)SpringUtil.getBean("emailContentDao");
						//emailContentDao.saveOrUpdate(emailContent);
						
						try {
							int confirm = Messagebox.show("Are you sure you want to save the campaign? ",
									"Save Campaign  ", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
							if(confirm != Messagebox.OK) return false;
						} catch (Exception e) {
							logger.error("Exception ::" , e);;
							return false;
						}
						
						//String Final_html = PrepareFinalHtml.replaceImgURL(campSchedule.getEmailContent().getHtmlContent(), campSchedule.getUser().getUserName());
						String Final_html = campSchedule.getEmailContent().getHtmlContent().replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
								.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
								.replace(MAILHANDLER_URL, ImageServer_Url);
						//logger.info("final html ....."+Final_html);
						//logger.info("User html ....."+campSchedule.getUser().getUserName());
						campSchedule.getEmailContent().setHtmlContent(Final_html);
						
						
						CampaignScheduleDao campScheduleDao = (CampaignScheduleDao)
										SpringUtil.getBean("campaignScheduleDao");
						
						CampaignScheduleDaoForDML campScheduleDaoForDML = (CampaignScheduleDaoForDML)
								SpringUtil.getBean("campaignScheduleDaoForDML");
						//campScheduleDao.saveOrUpdate(campSchedule);
						campScheduleDaoForDML.saveOrUpdate(campSchedule);
						MessageUtil.setMessage("Email campaign saved successfully.", "color:blue", "TOP");
					} 
					catch (RuntimeException e) {
						logger.error("** Exception while saving the email content", e);
					}
					
					return true;

				}// if
				else {
					campaign.setHtmlText(htmlStuff);
					campaign.setPrepared(false);
					
					logger.debug("> > > > > > > Setting the Editor type :"+editorType);
					
					campaign.setEditorType(editorType);
					if(isEdit!=null){
						if(isEdit.equalsIgnoreCase("view")){
							if(logger.isDebugEnabled())logger.debug("Creating new campaign");
							campaign.setTemplate(st);
							//campaign.setStatus("Draft");
							//campaign.setStatusChangedOn(Calendar.getInstance());
							
//							campaign.setDraftStatus("CampTextMsg");
							if((campaign.getDraftStatus()==null) || 
									CampaignStepsEnum.blockEditor.getPos() >= CampaignStepsEnum.valueOf(campaign.getDraftStatus()).getPos() ||
									CampaignStepsEnum.plainTextEditor.getPos() >= CampaignStepsEnum.valueOf(campaign.getDraftStatus()).getPos()) {
								campaign.setDraftStatus("CampTextMsg");
							} 
							
							//campaign.setUsers(user);
							if(logger.isDebugEnabled())logger.debug("added required fields");
						}else if(isEdit.equalsIgnoreCase("edit")){
							campaign.setModifiedDate(Calendar.getInstance());
						}
					}else{
						
						campaign.setTemplate(st);
						//campaign.setStatus("Draft");
						campaign.setStatusChangedOn(Calendar.getInstance());
						
//						campaign.setDraftStatus("CampTextMsg");
						if((campaign.getDraftStatus()==null) || 
								CampaignStepsEnum.blockEditor.getPos() >= CampaignStepsEnum.valueOf(campaign.getDraftStatus()).getPos() ||
								CampaignStepsEnum.plainTextEditor.getPos() >= CampaignStepsEnum.valueOf(campaign.getDraftStatus()).getPos()) {
							campaign.setDraftStatus("CampTextMsg");
						}
						
						//campaign.setUsers(user);
					}
	//				campaign.setFinalHtmlText(removeEditorTags(htmlStuff));
					//String Final_html = PrepareFinalHtml.replaceImgURL(campaign.getHtmlText(), campaign.getUsers().getUserName());
					String Final_html = campaign.getHtmlText().replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
					//logger.info("final html ....."+Final_html);
					//logger.info("User html ....."+campSchedule.getUser().getUserName());
					campaign.setHtmlText(Final_html);
					campaign.setModifiedDate(Calendar.getInstance());
					sessionScope.setAttribute("campaign", campaign);
					//campaignsDao.saveOrUpdate(campaign);
					campaignsDaoForDML.saveOrUpdate(campaign);
					if(logger.isDebugEnabled())logger.debug("campaign saved successfully");
					
					if(flag) {
						MessageUtil.setMessage("Email campaign saved successfully.", "color:blue", "TOP");
					}
					/*if(userActivitiesDao != null) {
            		userActivitiesDao.addToActivityList(ActivityEnum.CAMP_CRE_ADD_TEMPLATE_p1campaignName, currentUser, campaign.getCampaignName());
					}*/
					if(userActivitiesDaoForDML != null) {
	            		userActivitiesDaoForDML.addToActivityList(ActivityEnum.CAMP_CRE_ADD_TEMPLATE_p1campaignName, GetUser.getLoginUserObj(), campaign.getCampaignName());
						}
					
					//setting recurring schedules to draft
					if(isEdit== null ||!isEdit.equalsIgnoreCase("edit")){
					CampaignScheduleDao campaignScheduleDao = (CampaignScheduleDao)
							SpringUtil.getBean("campaignScheduleDao");
					CampaignScheduleDaoForDML campaignScheduleDaoForDML = (CampaignScheduleDaoForDML)
							SpringUtil.getBean("campaignScheduleDaoForDML");
					
					logger.info("setting recurring schedules to draft");
					if(campaign.getDraftStatus().equalsIgnoreCase("complete")){
					List<CampaignSchedule> campScheduleList = campaignScheduleDao.findAllByUpComingActiveSchedules(campaign.getCampaignId());
					if(campScheduleList != null && campScheduleList.size() > 0){
						for(CampaignSchedule campSch:campScheduleList) {
							logger.info("campSch id "+campSch.getCampaignId());
							campSch.setStatus((byte) 2);
							campaignScheduleDaoForDML.saveOrUpdate(campSch);
						}
					}
					}
				}
					return true;
					
				} // else if
				
			}
			catch(DataIntegrityViolationException dive){
				logger.error("** Exception : Data integrity violation " +
						"while saving the campaign -", dive);
				return false;
			}	
			
		}catch (Exception e) {
			logger.error("** Exception :"+ e +" **");
			return false;
		}
		
	}








    //Editing here
	protected boolean saveEmail( String htmlStuff, SystemTemplates st , 
			CampaignSchedule campSchedule, String contentName, boolean flag, String jsonStuff) {

		try{

			if(logger.isDebugEnabled())logger.debug("-- just entered --");
			/*if(checkEmailContentEmptyOrNot(jsonStuff)){ 
				logger.info("Email content is empty");
				return false;
			}
			logger.info("Email content is not empty");*/
			CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
			Campaigns dbcampaign = campaignsDao.findByCampaignId(campaign.getCampaignId());
			if (!campaign.getStatus().equalsIgnoreCase("Draft") && !campaign.getDraftStatus().equalsIgnoreCase("complete")) {
			if((isEdit == null || isEdit.equalsIgnoreCase("view")) && Utility.isSomethingWrong(campaign, 4)){
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
				MessageUtil.clearMessage();
				String isValidPhStr = null;
				isValidPhStr = Utility.validatePh(htmlStuff, currentUser);

				if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){

					MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
					return false;
				}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){

					MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
					return false;

				}

				/*String isValidCCDim = null;
				isValidCCDim = Utility.validateCouponDimensions(htmlStuff);
				if(isValidCCDim != null){
					return false;
				}*/
				String isValidCouponAndBarcode = null;
				isValidCouponAndBarcode = Utility.validateCCPh(htmlStuff, currentUser);
				if(isValidCouponAndBarcode != null){
					return false;
				}

				/*if(Utility.validateHtmlSize(htmlStuff)) {
					Messagebox.show("It looks like the content of your email is more " +
							"than the recommended size per the email-" + 
							"sending best practices. " + "To avoid email landing " + 
							"in recipient's spam folder, please redesign " 
										+"email to reduce content.", "Info", Messagebox.OK, Messagebox.INFORMATION);
				}*/
				
				long size =	Utility.validateHtmlSize(htmlStuff);
				if(size >100) {
					String msgcontent = OCConstants.HTML_VALIDATION;
					msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
					MessageUtil.setMessage(msgcontent, "color:Blue");
				}

				if(!checkSpam(htmlStuff)){
					return false;
				}

				if(htmlStuff.trim().length() == 0 && !flag==false){
					MessageUtil.setMessage("Email cannot be empty.", "color:red", "TOP");
					return false;
				}
			}

			//CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
			CampaignsDaoForDML campaignsDaoForDML = (CampaignsDaoForDML)SpringUtil.getBean("campaignsDaoForDML");
			campaign = (Campaigns)sessionScope.getAttribute("campaign");

			try{

				// if editing the content for resending schedule criteria
				if(campSchedule != null) {

					if(logger.isDebugEnabled()) {
						logger.debug(">>>>>> CampaignSchedule block ");
					}

					EmailContent emailContent;

					// if newly creating the content for this schedule
					if((emailContent = campSchedule.getEmailContent()) == null) {

						emailContent = new EmailContent();

						emailContent.setHtmlContent(htmlStuff);
						emailContent.setCampaignId(campaign.getCampaignId());
						emailContent.setName(contentName);
						campSchedule.setEmailContent(emailContent);

					}
					else {

						emailContent.setHtmlContent(htmlStuff);
						emailContent.setName(contentName);
						campSchedule.setEmailContent(emailContent);


						// editing the already existed emailContent of CampaignSchedule object
						campSchedule.getEmailContent().setHtmlContent(htmlStuff);
					}

					try {

						if(logger.isDebugEnabled()) {
							logger.debug(">>>>>> saving the emailcontent ");
						}

						//EmailContentDao emailContentDao = (EmailContentDao)SpringUtil.getBean("emailContentDao");
						//emailContentDao.saveOrUpdate(emailContent);

						try {
							int confirm = Messagebox.show("Are you sure you want to save the campaign? ",
									"Save Campaign  ", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
							if(confirm != Messagebox.OK) return false;
						} catch (Exception e) {
							logger.error("Exception ::" , e);;
							return false;
						}


						//String Final_html = PrepareFinalHtml.replaceImgURL(campSchedule.getEmailContent().getHtmlContent(), campSchedule.getUser().getUserName());
						String Final_html = campSchedule.getEmailContent().getHtmlContent().replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
								.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
								.replace(MAILHANDLER_URL, ImageServer_Url);
						//logger.info("final html ....."+Final_html);
						//logger.info("User html ....."+campSchedule.getUser().getUserName());
						campSchedule.getEmailContent().setHtmlContent(Final_html);
						CampaignScheduleDao campScheduleDao = (CampaignScheduleDao)
								SpringUtil.getBean("campaignScheduleDao");

						CampaignScheduleDaoForDML campScheduleDaoForDML = (CampaignScheduleDaoForDML)
								SpringUtil.getBean("campaignScheduleDaoForDML");
						//campScheduleDao.saveOrUpdate(campSchedule);
						campScheduleDaoForDML.saveOrUpdate(campSchedule);
						MessageUtil.setMessage("Email campaign saved successfully.", "color:blue", "TOP");
					} 
					catch (RuntimeException e) {
						logger.error("** Exception while saving the email content", e);
					}

					return true;

				}// if
				else {
					campaign.setHtmlText(htmlStuff);
					campaign.setPrepared(false);
					campaign.setJsonContent(jsonStuff);
 
					logger.debug("> > > > > > > Setting the Editor type :"+editorType);

					campaign.setEditorType(editorType);
					if(isEdit!=null){
						if(isEdit.equalsIgnoreCase("view")){
							if(logger.isDebugEnabled())logger.debug("Creating new campaign");
							campaign.setTemplate(st);
							//campaign.setStatus("Draft");
							//campaign.setStatusChangedOn(Calendar.getInstance());

							//			campaign.setDraftStatus("CampTextMsg");
							if((campaign.getDraftStatus()==null) || 
									CampaignStepsEnum.blockEditor.getPos() >= CampaignStepsEnum.valueOf(campaign.getDraftStatus()).getPos() ||
									CampaignStepsEnum.plainTextEditor.getPos() >= CampaignStepsEnum.valueOf(campaign.getDraftStatus()).getPos() ||
									CampaignStepsEnum.beeEditor.getPos() >= CampaignStepsEnum.valueOf(campaign.getDraftStatus()).getPos()) {
								campaign.setDraftStatus("CampTextMsg");
							} 

							//campaign.setUsers(user);
							if(logger.isDebugEnabled())logger.debug("added required fields");
						}else if(isEdit.equalsIgnoreCase("edit")){
							campaign.setModifiedDate(Calendar.getInstance());
						}
					}else{
						/*if(Utility.isSomethingWrong(campaign, 4)){
							MessageUtil.setMessage(OCConstants.CAMPAIGN_MSG, "color:blue;");
							Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
							
						}*/
						campaign.setTemplate(st);
						//campaign.setStatus("Draft");
						campaign.setStatusChangedOn(Calendar.getInstance());

						//		campaign.setDraftStatus("CampTextMsg");
						if((campaign.getDraftStatus()==null) || 
								CampaignStepsEnum.blockEditor.getPos() >= CampaignStepsEnum.valueOf(campaign.getDraftStatus()).getPos() ||
								CampaignStepsEnum.plainTextEditor.getPos() >= CampaignStepsEnum.valueOf(campaign.getDraftStatus()).getPos() ||
										CampaignStepsEnum.beeEditor.getPos() >= CampaignStepsEnum.valueOf(campaign.getDraftStatus()).getPos()) {
							campaign.setDraftStatus("CampTextMsg");
						}
						
						//setting recurring schedules to draft
						CampaignScheduleDao campaignScheduleDao = (CampaignScheduleDao)
								SpringUtil.getBean("campaignScheduleDao");
						CampaignScheduleDaoForDML campaignScheduleDaoForDML = (CampaignScheduleDaoForDML)
								SpringUtil.getBean("campaignScheduleDaoForDML");
						
						logger.info("setting recurring schedules to draft");
						if(campaign.getDraftStatus().equalsIgnoreCase("complete")){
						List<CampaignSchedule> campScheduleList = campaignScheduleDao.findAllByUpComingActiveSchedules(campaign.getCampaignId());
						if(campScheduleList != null && campScheduleList.size() > 0){
							for(CampaignSchedule campSch:campScheduleList) {
								logger.info("campSch id "+campSch.getCampaignId());
								campSch.setStatus((byte) 2);
								campaignScheduleDaoForDML.saveOrUpdate(campSch);
							}
						}
						}
						//campaign.setUsers(user);
					}
					//				campaign.setFinalHtmlText(removeEditorTags(htmlStuff));
					campaign.setModifiedDate(Calendar.getInstance());
					sessionScope.setAttribute("campaign", campaign);
					//String Final_html = PrepareFinalHtml.replaceImgURL(campaign.getHtmlText(), campaign.getUsers().getUserName());
					String Final_html = campaign.getHtmlText().replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
					String Final_json = campaign.getJsonContent().replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
					//logger.info("final html ....."+Final_html);
					//logger.info("User html ....."+campaign.getUsers().getUserName());
					campaign.setJsonContent(Final_json);
					campaign.setHtmlText(Final_html);
					//campaignsDao.saveOrUpdate(campaign);
					campaignsDaoForDML.saveOrUpdate(campaign);
					if(logger.isDebugEnabled())logger.debug("campaign saved successfully");

					if(flag) {
						MessageUtil.setMessage("Email campaign saved successfully.", "color:blue", "TOP");
					}
					/*if(userActivitiesDao != null) {
	userActivitiesDao.addToActivityList(ActivityEnum.CAMP_CRE_ADD_TEMPLATE_p1campaignName, currentUser, campaign.getCampaignName());
	}*/
					if(userActivitiesDaoForDML != null) {
						userActivitiesDaoForDML.addToActivityList(ActivityEnum.CAMP_CRE_ADD_TEMPLATE_p1campaignName, GetUser.getLoginUserObj(), campaign.getCampaignName());
					}
					return true;

				} // else if

			}
			catch(DataIntegrityViolationException dive){
				logger.error("** Exception : Data integrity violation " +
						"while saving the campaign -", dive);
				return false;
			}	

		}catch (Exception e) {
			logger.error("** Exception :"+ e +" **");
			return false;
		}

	}

	//private Listbox myTemplatesListId;
	protected void saveInMyTemplates(Window winId, String name, String content, String editorType, Listbox myTemplatesListId,String jsoncontent) {
		String foldernName="";
		Label resLbId = (Label)winId.getFellow("resLbId");
		String isValidPhStr = null;
		isValidPhStr = Utility.validatePh(content, currentUser);

		if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){

			MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
			return ;
		}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){

			MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
			return ;

		}

		/*String isValidCCDim = null;
		isValidCCDim = Utility.validateCouponDimensions(content);
		if(isValidCCDim != null){
			return ;
		}*/

		/*if(Utility.validateHtmlSize(content)) {

			Messagebox.show("It looks like the content of your email is more " +
					"than the recommended size per the email-" + 
					"sending best practices. " + "To avoid email landing " + 
					"in recipient's spam folder, please redesign " 
								+"email to reduce content.", "Info", Messagebox.OK, Messagebox.INFORMATION);
		}*/
		
		long size =	Utility.validateHtmlSize(content);
		if(size >100) {
			String msgcontent = OCConstants.HTML_VALIDATION;
			msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
			MessageUtil.setMessage(msgcontent, "color:Blue");
		}

		int override = -1;
		try{
			if(content == null || content.isEmpty()) {

				MessageUtil.setMessage("Template content can not be empty.", "color:red;");
				return;

			}

			if(name == null || name.trim().length() == 0){
				//MessageUtil.setMessage("Please provide a name to save in My Templates folder.", "color:red", "TOP");
				resLbId.setValue("Please provide a name to save in my templates");
				resLbId.setVisible(true);
				return;
			}else if(!Utility.validateName(name)) {
				resLbId.setValue("Please provide a valid name without any special characters ");
				resLbId.setVisible(true);
				return;
			}
			else {
				resLbId.setVisible(false);
			}
			/**
			 * creates a MyTemplate object and saves in DB
			 */
			MyTemplates myTemplate = null;
			myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
			if(myTemplatesDao == null)
				myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
			foldernName= myTemplatesListId.getSelectedItem().getLabel();
			foldernName = foldernName.contains("(") ? foldernName.substring(0,foldernName.indexOf("(")) : foldernName;


			myTemplate = myTemplatesDao.findByUserNameAndTempNameInFolder(currentUser.getUserId(),name,foldernName);
			if(myTemplate != null) {

				try {
					override = Messagebox.show("The name already exists. Do you want to replace it?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(override == 1 ) {
						myTemplate.setContent(content);
						myTemplate.setJsoncontent(jsoncontent);

					}else {
						return;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::" , e);;
				}
			}
			else {
				Calendar cal = MyCalendar.getNewCalendar();
				foldernName= myTemplatesListId.getSelectedItem().getLabel();

				myTemplate = new MyTemplates(name, content, cal, editorType, currentUser,foldernName,jsoncontent);
			}
			//myTemplatesDao.saveOrUpdate(myTemplate);
			myTemplatesDaoForDML.saveOrUpdate(myTemplate);

			/**
			 * creates a html file and saves in user/MyTemplate directory
			 */

			if(content.contains("href='")){
				content = content.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");

			}
			if(content.contains("href=\"")){
				content = content.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
			}
			String myTemplateFilePathStr = 
					PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
					PropertyUtil.getPropertyValue("userTemplatesDirectory")+File.separator+foldernName+
					File.separator+name+File.separator+"email.html";
			try {
				File myTemplateFile = new File(myTemplateFilePathStr);
				File parentDir = myTemplateFile.getParentFile();

				if(override == 1) {
					FileUtils.deleteDirectory(parentDir);
				}

				parentDir.mkdirs();

				//TODO Have to copy image files if exists
				BufferedWriter bw = new BufferedWriter(new FileWriter(myTemplateFile));

				bw.write(content);
				bw.write(jsoncontent);
				bw.flush();
				bw.close();
				String msgStr = name +" saved in My templates successfully.";
				if(override == 1) {
					msgStr = name +" updated successfully in My templates";
				}
				MessageUtil.setMessage(msgStr, "color:blue", "TOP");

				/*				if(userActivitiesDao != null) {
	        		userActivitiesDao.addToActivityList(ActivityEnum.CAMP_SAVED_TO_MYTEMPLATE_p1campaignName, currentUser,campaign.getCampaignName());
				}*/
				if(userActivitiesDaoForDML != null) {
					userActivitiesDaoForDML.addToActivityList(ActivityEnum.CAMP_SAVED_TO_MYTEMPLATE_p1campaignName, GetUser.getLoginUserObj(),campaign.getCampaignName());
				}

			} catch (IOException e) {
				logger.error("** Exception : MyTemplates file creation failed",(Throwable)e);
			}

			//TODO need to write a code for generating image file of html for preview purpose

			winId.setVisible(false);
		} catch(DataIntegrityViolationException die) {
			//MessageUtil.setMessage("Name already exists in My Templates.", "color:red", "TOP");
			resLbId.setValue("Unable to save in my templates");
			resLbId.setVisible(true);
			logger.error("** Exception : while saving template in to MyTemplates", (Throwable)die);
		} catch(Exception e) {
			logger.error("** Exception : while saving the template in MyTemplates", (Throwable)e);
		}
	}


	
	
	protected void saveInMyTemplates(Window winId, String name, String content, String editorType, Listbox myTemplatesListId) {
		String foldernName="";
		Label resLbId = (Label)winId.getFellow("resLbId");
		String isValidPhStr = null;
		isValidPhStr = Utility.validatePh(content, currentUser);
		
		if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
			
			MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
			return ;
		}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
			
			MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
			return ;
			
		}
		
		String isValidCCDim = null;
		isValidCCDim = Utility.validateCouponDimensions(content);
		if(isValidCCDim != null){
			return ;
		}
		
		/*if(Utility.validateHtmlSize(content)) {
			
			Messagebox.show("It looks like the content of your email is more " +
					"than the recommended size per the email-" + 
					"sending best practices. " + "To avoid email landing " + 
					"in recipient's spam folder, please redesign " 
								+"email to reduce content.", "Info", Messagebox.OK, Messagebox.INFORMATION);
		}*/
		
		long size =	Utility.validateHtmlSize(content);
		if(size >100) {
			String msgcontent = OCConstants.HTML_VALIDATION;
			msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
			MessageUtil.setMessage(msgcontent, "color:Blue");
		}

		int override = -1;
		try{
			if(content == null || content.isEmpty()) {
				
				MessageUtil.setMessage("Template content can not be empty.", "color:red;");
				return;
				
			}
			
			if(name == null || name.trim().length() == 0){
				//MessageUtil.setMessage("Please provide a name to save in My Templates folder.", "color:red", "TOP");
				resLbId.setValue("Please provide a name to save in my templates");
				resLbId.setVisible(true);
				return;
			}else if(!Utility.validateName(name)) {
				resLbId.setValue("Please provide a valid name without any special characters ");
				resLbId.setVisible(true);
				return;
			}
			else {
				resLbId.setVisible(false);
			}
			/**
			 * creates a MyTemplate object and saves in DB
			 */
			MyTemplates myTemplate = null;
			myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
			if(myTemplatesDao == null)
				myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
			foldernName= myTemplatesListId.getSelectedItem().getLabel();
			foldernName = foldernName.contains("(") ? foldernName.substring(0,foldernName.indexOf("(")) : foldernName;
			
			
			myTemplate = myTemplatesDao.findByUserNameAndTempNameInFolder(currentUser.getUserId(),name,foldernName);
			if(myTemplate != null) {
				
				try {
					override = Messagebox.show("The name already exists. Do you want to replace it?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(override == 1 ) {
						myTemplate.setContent(content);

					}else {
						return;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::" , e);;
				}
			}
			else {
				Calendar cal = MyCalendar.getNewCalendar();
				foldernName= myTemplatesListId.getSelectedItem().getLabel();

				myTemplate = new MyTemplates(name, content, cal, editorType, currentUser,foldernName, Constants.MYTEMPATES_PARENT);
			}
			//myTemplatesDao.saveOrUpdate(myTemplate);
			myTemplatesDaoForDML.saveOrUpdate(myTemplate);
			
			/**
			 * creates a html file and saves in user/MyTemplate directory
			 */
			
			if(content.contains("href='")){
				content = content.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
				
			}
			if(content.contains("href=\"")){
				content = content.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
			}
			String myTemplateFilePathStr = 
					PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
					PropertyUtil.getPropertyValue("userTemplatesDirectory")+File.separator+foldernName+
					File.separator+name+File.separator+"email.html";
			try {
				File myTemplateFile = new File(myTemplateFilePathStr);
				File parentDir = myTemplateFile.getParentFile();
				
				if(override == 1) {
					FileUtils.deleteDirectory(parentDir);
				}
				
				parentDir.mkdirs();
				
				//TODO Have to copy image files if exists
				BufferedWriter bw = new BufferedWriter(new FileWriter(myTemplateFile));
				
				bw.write(content);
				bw.flush();
				bw.close();
				String msgStr = name +" saved in My templates successfully.";
				if(override == 1) {
					msgStr = name +" updated successfully in My templates";
				}
				MessageUtil.setMessage(msgStr, "color:blue", "TOP");
				
/*				if(userActivitiesDao != null) {
	        		userActivitiesDao.addToActivityList(ActivityEnum.CAMP_SAVED_TO_MYTEMPLATE_p1campaignName, currentUser,campaign.getCampaignName());
				}*/
				if(userActivitiesDaoForDML != null) {
	        		userActivitiesDaoForDML.addToActivityList(ActivityEnum.CAMP_SAVED_TO_MYTEMPLATE_p1campaignName, GetUser.getLoginUserObj(),campaign.getCampaignName());
				}

			} catch (IOException e) {
				logger.error("** Exception : MyTemplates file creation failed",(Throwable)e);
			}
			
			//TODO need to write a code for generating image file of html for preview purpose

			winId.setVisible(false);
		} catch(DataIntegrityViolationException die) {
			//MessageUtil.setMessage("Name already exists in My Templates.", "color:red", "TOP");
			resLbId.setValue("Unable to save in my templates");
			resLbId.setVisible(true);
			logger.error("** Exception : while saving template in to MyTemplates", (Throwable)die);
		} catch(Exception e) {
			logger.error("** Exception : while saving the template in MyTemplates", (Throwable)e);
		}
	}
	
	/*protected void sendTestMail(String htmlStuff,String emailId) {
		
		if(campaign==null){
			logger.error("Exception :Campaign was null");
		}
		
		EmailQueueDao emailQueueDao = (EmailQueueDao)SpringUtil.getBean("emailQueueDao");
		
		try {
			
			if(logger.isDebugEnabled())logger.debug("Sending the test mail....");
			
			MessageUtil.clearMessage();
			
			if(Utility.validateHtmlSize(htmlStuff)) {
				
				Messagebox.show("HTML size cannot exceed 100kb. Please reduce" +
						" the size and try again.", "Captiway", Messagebox.OK, Messagebox.ERROR);
				return;
			}
			
			if(!Utility.validateEmail(emailId)) {
				
				Messagebox.show("Enter a valid email address.", "Captiway", Messagebox.OK, Messagebox.ERROR);
				return;
			}
				
			if(!checkSpam(htmlStuff)){
				return;
			}
			
			try{

				Messagebox.show("Test mail will be sent in a moment.", "Information", 
						Messagebox.OK, Messagebox.INFORMATION);
				
				EmailQueue testEmailQueue = new EmailQueue(
						campaign, Constants.EQ_TYPE_TESTMAIL, "Active", emailId, 
						MyCalendar.getNewCalendar(), user);
				
				testEmailQueue.setMessage(htmlStuff);
				
				try{
					emailQueueDao.saveOrUpdate(testEmailQueue);
					if(userActivitiesDao != null) {
	            		userActivitiesDao.addToActivityList(ActivityEnum.CAMP_SENT_TSTMAIL_p1campaignName, user, campaign.getCampaignName());
					}
				}
				catch(Exception e1){
					logger.error("** Exception : Error while saving the Test Email" +
							" into queue ", e1);
				}
				
			}
			catch(Exception e){
				logger.error("** Exception : ", e);
			}
			
		} catch (Exception e) {
			logger.error("Exception : ", e);
		}
	}
*/
	protected boolean sendTestMail(String htmlStuff,String emailId) {
		
		if( htmlStuff == null || htmlStuff.isEmpty() ){
			MessageUtil.setMessage("There is no content to send a test mail.", "color:red");

			return false;
		}
	
		if(campaign==null){
			logger.error("Exception :Campaign was null");
			return false;
		}
		
		
		/*if(campaign.getTextMessage() == null || campaign.getTextMessage().trim() .length()== 0) {
			
			//campaign.setHtmlText(htmlStuff);
			campaign.setTextMessage(HTMLUtility.getTextFromHtml(htmlStuff));
			CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
			
			campaignsDao.saveOrUpdate(campaign);
			
		}*/
		
		EmailQueueDao emailQueueDao = (EmailQueueDao)SpringUtil.getBean("emailQueueDao");
		
		try {
			
			if(logger.isDebugEnabled())logger.debug("Sending the test mail....");
			
			MessageUtil.clearMessage();
			String isValidPhStr = null;
			isValidPhStr = Utility.validatePh(htmlStuff, currentUser);
			
			if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
				
				MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
				return false;
			}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
				
				MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
				return false;
				
			}
			
			/*String isValidCCDim = null;
			isValidCCDim = Utility.validateCouponDimensions(htmlStuff);
			if(isValidCCDim != null){
				return false;
			}*/
			String isValidCouponAndBarcode = null;
			isValidCouponAndBarcode = Utility.validateCCPh(htmlStuff, currentUser);
			if(isValidCouponAndBarcode != null){
				return false;
			}
			
			/*if(Utility.validateHtmlSize(htmlStuff)) {
				
				Messagebox.show("It looks like the content of your email is more " +
						"than the recommended size per the email-" + 
						"sending best practices. " + "To avoid email landing " + 
						"in recipient's spam folder, please redesign " 
									+"email to reduce content.", "Info", Messagebox.OK, Messagebox.INFORMATION);
			}*/
			
			long size =	Utility.validateHtmlSize(htmlStuff);
			if(size >100) {
				String msgcontent = OCConstants.HTML_VALIDATION;
				msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
				Messagebox.show(msgcontent, "Info", Messagebox.OK, Messagebox.INFORMATION);
			}
			
			if(!checkSpam(htmlStuff)){
				return false;
			}
			
			
			String[] emailArr = null;

			emailArr = emailId.split(",");
			for (String email : emailArr) {

				if(!Utility.validateEmail(email.trim())) {

					Messagebox.show("Please enter a valid email address. '"+email+"' is invalid.", "Error", Messagebox.OK, Messagebox.ERROR);
					return false;
				}else {
					//Testing for invalid email domains-APP-308
					String result = PurgeList.checkForValidDomainByEmailId(email.trim());
					if(!result.equalsIgnoreCase("Active")) {
						Messagebox.show("Please enter email address with valid email domain. '"+email+"' is invalid.", "Error", Messagebox.OK, Messagebox.ERROR);
						return false;
					}
				}

				//Utility.sendTestMail(campaign, htmlStuff, email);


			}//for

			//Check whether user is expired or not
			if(Calendar.getInstance().after(currentUser.getPackageExpiryDate())){
				logger.debug("Current User::"+currentUser.getUserId()+" is expired, hence cannot send test mail");
				MessageUtil.setMessage("Your account validity period has expired. Please renew your subscription to continue.", "color:red", "TOP");
				return false;
			}


			for (String email : emailArr) {

				//	Utility.sendTestMail(campaign, htmlStuff, email);
				Utility.sendInstantMail(campaign, campaign.getSubject(), htmlStuff,
						Constants.EQ_TYPE_TESTMAIL, email, null );


			}//for
			/*if(userActivitiesDao != null) {
            		userActivitiesDao.addToActivityList(ActivityEnum.CAMP_SENT_TSTMAIL_p1campaignName, GetUser.getUserObj(), campaign.getCampaignName());
				}*/
			if(userActivitiesDaoForDML != null) {
				userActivitiesDaoForDML.addToActivityList(ActivityEnum.CAMP_SENT_TSTMAIL_p1campaignName, GetUser.getLoginUserObj(), campaign.getCampaignName());
			}


			Messagebox.show("Test mail will be sent in a moment.", "Information", 
					Messagebox.OK, Messagebox.INFORMATION);

			return true;
		} catch (Exception e) {
			logger.error("Exception : ", e);
			return true;
		}
	
	}

	
	public static Map<String , String> getDefaultStorePhValue(List<String> storePlaceholders) {
		
		Map<String , String> storeDefPHValues = new HashMap<String, String>();
		Users user = GetUser.getUserObj();
		int i=-1;
		for (String phToken : storePlaceholders) {
			i++;
			if(i == 0 || !phToken.contains("/")) continue;
			
			//phToken = phToken.substring(phToken.indexOf(Constants.DELIMETER_DOUBLECOLON)+1);
			//logger.info(" phToken ::"+phToken);
			
			String placeholder = phToken.split(Constants.DELIMETER_DOUBLECOLON)[1];
			placeholder = placeholder.substring(6, placeholder.indexOf("/")).trim();
			
			if(!placeholder.startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_STORE) 
					|| placeholder.equals(PlaceHolders.CAMPAIGN_PH_STORENAME)) continue;
			//logger.info("placeholder ::"+placeholder);
		
			if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_STOREMANAGER)){
				storeDefPHValues.put(phToken,  Utility.getOnlyUserName(user.getUserName()));
			}
			else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_STOREEMAIL)){
				storeDefPHValues.put(phToken, user.getEmailId() ) ;//!= null  ? user.getEmailId() :  Constants.STRING_NOTAVAILABLE;
			}
			else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_STOREPHONE)){
				storeDefPHValues.put(phToken, user.getPhone());// != null  ? user.getPhone() :  Constants.STRING_NOTAVAILABLE;
				
			}
			else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_STORESTREET)){
				storeDefPHValues.put(phToken,user.getAddressOne()) ;//!= null  ? user.getAddressOne() :  Constants.STRING_NOTAVAILABLE;
			}
			else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_STORECITY)){
				storeDefPHValues.put(phToken, user.getCity());// != null  ? user.getCity() :  Constants.STRING_NOTAVAILABLE;
			}
			else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_STORESTATE)){
				storeDefPHValues.put(phToken, user.getState());// != null  ? user.getState() :  Constants.STRING_NOTAVAILABLE;
				
			}
			else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_STOREZIP)){
				storeDefPHValues.put(phToken,user.getPinCode());// != null  ? user.getAddressOne() :  Constants.STRING_NOTAVAILABLE;
			}
			
		
		}
		
		return storeDefPHValues;
	}
	
	
	public static Set<String> getPlaceHolderList(Set<MailingList> mlistSet) {
		return  getPlaceHolderList(mlistSet, false, null); 
	}

	public static Set<String> getPlaceHolderList(Set<MailingList> mlistSet, boolean ignoreUnSubAndWebLink, Listbox phLbId) {
		
		try {
			logger.debug("-- Just Entered --");
			Clients.evalJavaScript("var mergeTags97 = [];");

			MLCustomFieldsDao mlCustomFieldsDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
			//logger.debug("Got Ml Set of size :" + mlistSet.size());
			
			List<String> placeHoldersList = new ArrayList<String>(); 
			
			POSMappingDao posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
			//***************
			List<POSMapping> contactsGENList = posMappingDao.findOnlyByGenType(Constants.POS_MAPPING_TYPE_CONTACTS, GetUser.getUserId() );
			
			//Map<String, String> placeholders = new HashMap<String, String>();
			placeHoldersList.addAll(Constants.PLACEHOLDERS_LIST);
			Users user = GetUser.getUserObj();
			
			
			if(user.getloyaltyServicetype() != null && user.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_SB) )
			{
				placeHoldersList.removeIf(e -> e.contains("Loyalty Gift Balance"));
			}
			if(user.getloyaltyServicetype() != null && user.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_OC) )
			{
				placeHoldersList.removeIf(e -> e.contains("Loyalty Membership Pin"));
			}
			
			if(user.getloyaltyServicetype() != null && !user.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_SB) )
			{
				//logger.info("the current user is a oc user");
				placeHoldersList.addAll(Constants.OCPLACEHOLDERS_LIST);
			}

			//logger.info("PlaceHolders............."+placeHoldersList);
			//we need to add here new place holder list
			
			Map<String , String> StoreDefaultPHValues = getDefaultStorePhValue(placeHoldersList);
			
			//logger.debug(" StoreDefaultPHValues ==== "+StoreDefaultPHValues);
			//placeholders.putAll(Constants.PLACEHOLDERS_MAP);
			
			/*for (String Ph : placeHoldersList) {
				String[] phTokenArr =  Ph.split("::"); 
				String key = phTokenArr[0];
				StringBuffer value = new StringBuffer(phTokenArr[1]);
				
				for (POSMapping posMapping : contactsGENList) {
					
					if(!key.equalsIgnoreCase(posMapping.getCustomFieldName())) continue;
					
					if(posMapping.getDefaultPhValue() == null || posMapping.getDefaultPhValue().isEmpty() ) break;
					
					value.append(Constants.DELIMETER_SLASH + posMapping.getDefaultPhValue() ,value.indexOf("^"), value.indexOf("^"));
					
					
					
					
					
				}
				
				
				
				
			}*/
			
			
			/*if( placeholders != null) {
				//2.prepare merge tag and add to placeHoldersList
				//format : display lable :: |^GEN_<UDF>^|
				
				Set<String> phKeyset =  placeholders.keySet();
				for (String phkey : phKeyset) {
					
					String phVal = placeholders.get(phkey);
					
					String[] phVal1 = phVal.split("::"); 
					String ph = phVal1[0].trim();
					String phDeftval = null;
					String genStr = null;
					
					for (POSMapping posMapping : contactsGENList) {
						if(posMapping.getCustomFieldName().trim().equals(phVal1[1].trim())){
							phDeftval = posMapping.getDefaultPhValue();
							break;
						}
					
						
						}
					
					if( phDeftval == null || phDeftval.trim().isEmpty() ) {
					
						genStr = phVal1[1]+ Constants.DELIMETER_DOUBLECOLON +
								Constants.CF_START_TAG + ph + Constants.CF_END_TAG ;
					
					}
					else {
						
						genStr = phVal1[1] + Constants.DELIMETER_DOUBLECOLON +
								Constants.CF_START_TAG + ph + Constants.DELIMETER_SLASH + phDeftval + Constants.CF_END_TAG ;
					}
					placeHoldersList.add(genStr);
					
					
					
			}
				
				//********TO BE COMMENTED******************
				for (POSMapping posMapping : contactsGENList) {
					
					String genStr = null;
					
					if( posMapping.getDefaultPhValue() == null  || posMapping.getDefaultPhValue().trim().isEmpty() ) {
					 genStr = posMapping.getDisplayLabel() +
									Constants.DELIMETER_DOUBLECOLON +
									Constants.CF_START_TAG + Constants.GEN_TOKEN +
									posMapping.getCustomFieldName() + Constants.CF_END_TAG ;
					
					
					}
					else {
						 genStr = posMapping.getDisplayLabel() +
								Constants.DELIMETER_DOUBLECOLON +
								Constants.CF_START_TAG + Constants.GEN_TOKEN +
								posMapping.getCustomFieldName() + Constants.DELIMETER_SLASH +  posMapping.getDefaultPhValue()  + Constants.CF_END_TAG ;
				
				
					}
					placeHoldersList.add(genStr);
				}//for
				//*****************END*********************
			
				placeHoldersList.addAll(Constants.PLACEHOLDERS_LIST);
				
			}//if*/
			
			
			//Changes to add mapped UDF fields as placeholders
			//1.get all the pos mapped UDFs from the user pos settings(table:pos_mappings)
			//POSMappingDao posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
			
			List<POSMapping> contactsUDFList = posMappingDao.findOnlyByType(Constants.POS_MAPPING_TYPE_CONTACTS, GetUser.getUserId() );
			
			if(contactsUDFList != null) {
				
				//2.prepare merge tag and add to placeHoldersList
				//format : display lable :: |^GEN_<UDF>^|
				for (POSMapping posMapping : contactsUDFList) {
					
					String udfStr;
					if(posMapping.getDefaultPhValue()==null || posMapping.getDefaultPhValue().trim().isEmpty()) {

						udfStr = Constants.UDF_TOKEN + posMapping.getDisplayLabel() +
								Constants.DELIMETER_DOUBLECOLON +
								Constants.CF_START_TAG + Constants.UDF_TOKEN +
								posMapping.getCustomFieldName()  + Constants.DELIMETER_SPACE+ Constants.DELIMETER_SLASH + Constants.DELIMETER_SPACE+Constants.DEFUALT_TOKEN+Constants.CF_END_TAG ;


					}
					else {
						udfStr = Constants.UDF_TOKEN + posMapping.getDisplayLabel() +
								Constants.DELIMETER_DOUBLECOLON +
								Constants.CF_START_TAG + Constants.UDF_TOKEN +
								posMapping.getCustomFieldName()+ Constants.DELIMETER_SPACE + Constants.DELIMETER_SLASH + Constants.DELIMETER_SPACE + Constants.DEFUALT_TOKEN + posMapping.getDefaultPhValue() + Constants.CF_END_TAG ;
				
					
					}
					placeHoldersList.add(udfStr);
				}//for
				
				
				
			}//if
			
			//END
			if(mlistSet != null) {
				for (MailingList mailingList : mlistSet) {
					if(!mailingList.isCustField())  continue;
					
					List<MLCustomFields> mlcust = mlCustomFieldsDao.findAllByList(mailingList);
					String custField ;
					for (MLCustomFields customField : mlcust) {
						custField = Constants.CF_TOKEN + customField.getCustFieldName() 
								+ Constants.DELIMETER_DOUBLECOLON + Constants.CF_START_TAG + 
								Constants.CF_TOKEN + 
								customField.getCustFieldName().toLowerCase() + Constants.CF_END_TAG;

						if(placeHoldersList.contains(custField)) continue;
						placeHoldersList.add(custField);
					}
					
				} // for
			}
			
			// Populate js variable 'phArr' with the place holders for all Editors
			
			int jsInd=0;
			if(phLbId == null) {
				Clients.evalJavaScript("var phArr = [];");
				//Clients.evalJavaScript("var mergeTags97 = [];");
			}
			boolean isadded = false;
			Set<String> retList = new HashSet<String>();
			StringBuffer buffer = new StringBuffer();

			for (String placeHolder : placeHoldersList) {
				//logger.debug("placeHolder = "+placeHolder);
				String str1=placeHolder;
				if(placeHolder.trim().startsWith("--") || placeHolder.toLowerCase().contains(("place holder"))) { //Ignore
					continue;
				}
				if(ignoreUnSubAndWebLink) {
					
					if(placeHolder.startsWith("Unsubscribe Link") || placeHolder.startsWith("Web-Page Version Link") ||
							placeHolder.startsWith("Share on Twitter") || placeHolder.startsWith("Share on Facebook") ||
							placeHolder.startsWith("Forward To Friend") ||placeHolder.startsWith("Subscriber Preference Link") ){
						retList.add(placeHolder);
						isadded = true;
						continue;
					}
					
				}
				if(placeHolder.contains(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LOYALTY) 
						|| placeHolder.contains(PlaceHolders.CAMPAIGN_PH_LASTPURCHASE_DATE)
						|| placeHolder.contains(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LASETPURCHASE)
						|| placeHolder.contains(PlaceHolders.CAMPAIGN_PH_STORENAME)){
					retList.add(placeHolder);
					isadded = true;
				}
				String[] phTokenArr =  placeHolder.split("::"); 
				String key = phTokenArr[0];
				StringBuffer value = new StringBuffer(phTokenArr[1]);
				//logger.debug("key ::"+key+" value ::"+value);
				if(StoreDefaultPHValues.containsKey(placeHolder)) {
					
					value.insert(value.lastIndexOf("^"), StoreDefaultPHValues.get(placeHolder));
					//logger.info(" store ::"+placeHolder + " ====== value == "+value );
					placeHolder = placeHolder.replace(phTokenArr[1], value.toString());
					retList.add(placeHolder);
					isadded = true;
					
				}
				for (POSMapping posMapping : contactsGENList) {
					
					if(!key.equalsIgnoreCase(posMapping.getCustomFieldName()) ) continue;
					
					
					
					if(posMapping.getDefaultPhValue() == null || posMapping.getDefaultPhValue().isEmpty() ){
						retList.add(placeHolder);
						isadded = true;
						break;
					}
					
					
					value.insert(value.lastIndexOf("^"), posMapping.getDefaultPhValue() );
					//logger.debug(" value ::"+value);
					placeHolder = placeHolder.replace(phTokenArr[1], value.toString());
					retList.add(placeHolder);
					isadded = true;
					
				}
				
				if(!isadded) {
					retList.add(placeHolder);
					
				}
				
				
				if(phLbId != null) {
					Listitem item =  new Listitem(key, value.toString());
					item.setParent(phLbId);
					
					
				}//if
				else{
					//logger.info("beforeeeeeeeee 1111111 :"+placeHolder);
					placeHolder = StringEscapeUtils.escapeJavaScript(placeHolder);
					//logger.info("afterrrrrrrrrrrrr 1111111 :"+placeHolder);
					Clients.evalJavaScript("phArr["+ (jsInd++) +"]=\""+placeHolder+"\";");
				}
				
				
				
				
				if(buffer.length() > 0) buffer.append(",");
				//buffer.append("{name: '" +key+ "', value: '["+value+"]'}");
				key=StringEscapeUtils.escapeJavaScript(key);
				value = new StringBuffer(StringEscapeUtils.escapeJavaScript(value.toString()));
				buffer.append("{name: '" +key+ "', value: '"+value+"'}"); 

				
			} // for
			
			String str1=buffer.toString();
			//logger.info(str1);
			
						
			Clients.evalJavaScript("mergeTags97 = ["+str1+"]");


			logger.debug("-- Exit --");
			
			return retList;
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
			return null;
		}
	}


	public static List<String> getCouponsList_OLD() {
		try {
			// **************** Populate Coupon codes as a Place holder *********************
			// add("CC_coupon Name::|^CC_123_couponName^|");

			List<String> couponsPhList = new ArrayList<String>();
			List<String> imageCouponsPhList = new ArrayList<String>();
			CouponsDao couponsDao =  (CouponsDao)SpringUtil.getBean("couponsDao");
			List<Coupons> couponList = couponsDao.findCouponsByOrgId(GetUser.getUserObj().getUserOrganization().getUserOrgId());
			//			List<Coupons> couponList = couponsDao.findCouponsByUserId(GetUser.getUserId());
			if(couponList!=null) {

				String textcouponStr="";
				String imagecouponStr="";
				for (Coupons coupon : couponList) {

					textcouponStr = coupon.getCouponName() + Constants.DELIMETER_DOUBLECOLON + 
							Constants.CF_START_TAG + Constants.CC_TOKEN + coupon.getCouponId() +"_"+coupon.getCouponName()+ 	
							Constants.CF_END_TAG;


					if(coupon.getEnableBarcode() && coupon.getBarcodeType() != null && coupon.getBarcodeWidth() != null
							&& coupon.getBarcodeHeight() != null){

						/*couponStr = Constants.CC_TOKEN + coupon.getCouponName() + Constants.DELIMETER_DOUBLECOLON + 
								Constants.CF_START_TAG + Constants.CC_TOKEN + coupon.getCouponId() +"_"+coupon.getCouponName()+ 	
								"_"+coupon.getBarcodeType()+"_"+coupon.getBarcodeWidth()
								+"_"+coupon.getBarcodeHeight()+Constants.CF_END_TAG;*/


						String couponIdStr = Constants.CC_TOKEN + coupon.getCouponId() +"_"+coupon.getCouponName()+ 	
								"_"+coupon.getBarcodeType()+"_"+coupon.getBarcodeWidth()
								+"_"+coupon.getBarcodeHeight();

						//generate image dynamically and set
						//generate barcode image
						BitMatrix bitMatrix = null;

						String COUPON_CODE_URL = null;
						//String COUPON_CODE_URL_IMG = null;
						String ccPreviewUrl = null;

						String message = "Test:"+coupon.getCouponName();
						String barcodeType = coupon.getBarcodeType().trim();
						int width = coupon.getBarcodeWidth().intValue();
						int height = coupon.getBarcodeHeight().intValue();

						if(barcodeType.equals(Constants.COUP_BARCODE_QR)){

							bitMatrix = new QRCodeWriter().encode(message, BarcodeFormat.QR_CODE, width, height,null);
							String bcqrImg = GetUser.getUserName()+File.separator+
									"Preview"+File.separator+"QRCODE"+File.separator+couponIdStr+".png";

							COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcqrImg;
							//COUPON_CODE_URL_IMG = "/subscriber/UserData"+File.separator+bcqrImg;
							ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcqrImg;
						}
						else if(barcodeType.equals(Constants.COUP_BARCODE_AZTEC)){

							bitMatrix = new AztecWriter().encode(message, BarcodeFormat.AZTEC, width, height);
							String bcazImg = GetUser.getUserName()+File.separator+
									"Preview"+File.separator+"AZTEC"+File.separator+couponIdStr+".png";

							COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcazImg;

							ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcazImg;
						}
						else if(barcodeType.equals(Constants.COUP_BARCODE_LINEAR)){

							bitMatrix = new Code128Writer().encode(message, BarcodeFormat.CODE_128, width, height,null);
							String bclnImg = GetUser.getUserName()+File.separator+
									"Preview"+File.separator+"LINEAR"+File.separator+couponIdStr+".png";

							COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bclnImg;

							ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bclnImg;
						}
						else if(barcodeType.equals(Constants.COUP_BARCODE_DATAMATRIX)){

							bitMatrix = new DataMatrixWriter().encode(message, BarcodeFormat.DATA_MATRIX, width, height,null);
							String bcdmImg = GetUser.getUserName()+File.separator+
									"Preview"+File.separator+"DATAMATRIX"+File.separator+couponIdStr+".png";

							COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcdmImg;

							ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcdmImg;
						}

						/*if(bitMatrix == null){
							return;
						}*/
						File myTemplateFile = new File(COUPON_CODE_URL);
						File parentDir = myTemplateFile.getParentFile();
						if(!parentDir.exists()) {

							parentDir.mkdir();
						}


						if(!myTemplateFile.exists()) {

							MatrixToImageWriter.writeToStream(bitMatrix, "png", new FileOutputStream(
									new File(COUPON_CODE_URL)));	
						}

						imagecouponStr = coupon.getCouponName() + Constants.DELIMETER_DOUBLECOLON +
								"<img id="+couponIdStr +
								" src="+ccPreviewUrl+" width="+coupon.getBarcodeWidth() +
								" height="+coupon.getBarcodeHeight()+" />";
						imageCouponsPhList.add(imagecouponStr);

					}///subscriber/images/CC_test_150_150.png

					couponsPhList.add(textcouponStr);

				} // for

				//couponsPhList.add("CC_TEST::<img id=dummyid src=http://localhost:8080/subscriber/images/CC_test_150_150.png /> ");

			} // if


			// populate Coupons JS Array
			Clients.evalJavaScript("var ocCouponsArr = [];");
			Clients.evalJavaScript("var mergeContents79 = [];");
			int jsInd=0;
			for (String couponPlaceHolder : couponsPhList) {
				if(couponPlaceHolder.trim().startsWith("--") || couponPlaceHolder.toLowerCase().contains(("place holder"))) { //Ignore
					continue;
				}
				Clients.evalJavaScript("ocCouponsArr["+ (jsInd++) +"]=\""+couponPlaceHolder+"\";");
			} // for

			Clients.evalJavaScript("var ocImageCouponsArr = [];");
			
			int jsInd1=0;
			for (String couponPlaceHolder : imageCouponsPhList) {
				if(couponPlaceHolder.trim().startsWith("--") || couponPlaceHolder.toLowerCase().contains(("place holder"))) { //Ignore
					continue;
				}
				Clients.evalJavaScript("ocImageCouponsArr["+ (jsInd1++) +"]=\""+couponPlaceHolder+"\";");
			} // for


			///////////VVVVV
			int len = couponsPhList.size();

			StringBuffer buffer = new StringBuffer();
			for (String couponPlaceHolder : couponsPhList) {
				if(couponPlaceHolder.trim().startsWith("--") || couponPlaceHolder.toLowerCase().contains(("place holder"))) { //Ignore
					continue;
				}
				String[] coupPHArr = couponPlaceHolder.split(Constants.DELIMETER_DOUBLECOLON );
				//System.out.println(couponPlaceHolder);
				String name = coupPHArr[0];
				String value = coupPHArr[1];
				if(buffer.length() > 0) buffer.append(",");
				buffer.append("{name: '" +name+ "', value: '["+value+"]'}"); 

			} // for


			String str=buffer.toString();
			//System.out.println("mergeContents = ["+str+"];");
			//Clients.evalJavaScript("alert(str);");

			
			Clients.evalJavaScript("mergeContents79 = ["+str+"];");
			

			logger.debug("-- Exit --");
			return couponsPhList;
		} catch (Exception e) {
			logger.error("Exception ::" , e);;
			return null;
		}
		
	}
	static String specialLink1;
	//static StringBuffer specialLinksBuffer;
	public static List<String> getCouponsList() {
		try {
			// **************** Populate Coupon codes as a Place holder *********************
			// add("CC_coupon Name::|^CC_123_couponName^|");
			
			List<String> couponsPhList = new ArrayList<String>();
			List<String> imageCouponsPhList = new ArrayList<String>();
			List<String> imageCouponsPhBeeList = new ArrayList<String>();
			CouponsDao couponsDao =  (CouponsDao)SpringUtil.getBean("couponsDao");
			//List<Coupons> couponList = couponsDao.findCouponsByOrgId(GetUser.getUserObj().getUserOrganization().getUserOrgId());
			List<Coupons> couponList = couponsDao.findActiveAndRunningCouponsbyOrgId(GetUser.getUserObj().getUserOrganization().getUserOrgId(),Constants.STRING_NILL,Constants.STRING_NILL);
		//	String Refferalcoupon = "";
			referralprogramDao=(ReferralProgramDao) SpringUtil.getBean(OCConstants.REFERRAL_PROGRAM_DAO);
			ReferralProgram refprgm=null;
			//List<Coupons> couponList = couponsDao.findCouponsByUserId(GetUser.getUserId());
		try {
			 refprgm=referralprogramDao.findReferalprogramByUserId(GetUser.getUserObj().getUserId());
		}catch(Exception e) {
			 logger.info("referral program obj exception"+refprgm);

			
		}
			 logger.info("referral program obj"+refprgm);

			
			if(couponList!=null) {

				String textcouponStr="";
				String imagecouponStr="";
				String imagecouponBeeStr="";
			

				for (Coupons coupon : couponList) {
				
					logger.info("entering referral"+coupon.isUseasReferralCode() );

				//	logger.info("entering referral id"+refprgm.getCouponId() );

					logger.info("entering coupon id"+coupon.getCouponId() );

					
					if(refprgm != null && coupon.isUseasReferralCode()) {
						
						if(refprgm.getCouponId().equals(coupon.getCouponId())) {
						
							logger.info("entering referral block");
							
							textcouponStr = "ReferralCode" + Constants.DELIMETER_DOUBLECOLON + 
									Constants.CF_START_TAG + "REF_"+Constants.CC_TOKEN + coupon.getCouponId() +"_"+coupon.getCouponName()+ 	
									Constants.CF_END_TAG;
						
						
							logger.info("referral string"+textcouponStr);

						
						}else{
							continue;
						}
					}else {
						
						logger.info("entering Normal block");

						textcouponStr = coupon.getCouponName() + Constants.DELIMETER_DOUBLECOLON + 
								Constants.CF_START_TAG + Constants.CC_TOKEN + coupon.getCouponId() +"_"+coupon.getCouponName()+ 	
								Constants.CF_END_TAG;
						
					}
					
					
						
					
					if(coupon.getEnableBarcode() && coupon.getBarcodeType() != null && coupon.getBarcodeWidth() != null
							&& coupon.getBarcodeHeight() != null){
						
						/*couponStr = Constants.CC_TOKEN + coupon.getCouponName() + Constants.DELIMETER_DOUBLECOLON + 
								Constants.CF_START_TAG + Constants.CC_TOKEN + coupon.getCouponId() +"_"+coupon.getCouponName()+ 	
								"_"+coupon.getBarcodeType()+"_"+coupon.getBarcodeWidth()
								+"_"+coupon.getBarcodeHeight()+Constants.CF_END_TAG;*/

						
						String couponIdStr = Constants.CC_TOKEN + coupon.getCouponId() +"_"+coupon.getCouponName()+ 	
								"_"+coupon.getBarcodeType()+"_"+coupon.getBarcodeWidth()
								+"_"+coupon.getBarcodeHeight();

						//generate image dynamically and set
						//generate barcode image
						BitMatrix bitMatrix = null;
						
						String COUPON_CODE_URL = null;
						//String COUPON_CODE_URL_IMG = null;
						String ccPreviewUrl = null;
						
						String message = "Test:"+coupon.getCouponName();
						String barcodeType = coupon.getBarcodeType().trim();
						int width = coupon.getBarcodeWidth().intValue();
						int height = coupon.getBarcodeHeight().intValue();
						
						if(barcodeType.equals(Constants.COUP_BARCODE_QR)){
							
							bitMatrix = new QRCodeWriter().encode(message, BarcodeFormat.QR_CODE, width, height,null);
							String bcqrImg = GetUser.getUserName()+File.separator+
									"Preview"+File.separator+"QRCODE"+File.separator+couponIdStr+".png";
							
							COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcqrImg;
							//COUPON_CODE_URL_IMG = "/subscriber/UserData"+File.separator+bcqrImg;
							ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcqrImg;
						}
						else if(barcodeType.equals(Constants.COUP_BARCODE_AZTEC)){
							
							bitMatrix = new AztecWriter().encode(message, BarcodeFormat.AZTEC, width, height);
							String bcazImg = GetUser.getUserName()+File.separator+
									"Preview"+File.separator+"AZTEC"+File.separator+couponIdStr+".png";
							
							COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcazImg;
							
							ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcazImg;
						}
						else if(barcodeType.equals(Constants.COUP_BARCODE_LINEAR)){
							
							bitMatrix = new Code128Writer().encode(message, BarcodeFormat.CODE_128, width, height,null);
							String bclnImg = GetUser.getUserName()+File.separator+
									"Preview"+File.separator+"LINEAR"+File.separator+couponIdStr+".png";
							
							COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bclnImg;
							
							ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bclnImg;
						}
						else if(barcodeType.equals(Constants.COUP_BARCODE_DATAMATRIX)){
							
							bitMatrix = new DataMatrixWriter().encode(message, BarcodeFormat.DATA_MATRIX, width, height,null);
							String bcdmImg = GetUser.getUserName()+File.separator+
									"Preview"+File.separator+"DATAMATRIX"+File.separator+couponIdStr+".png";
							
							COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcdmImg;
							
							ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcdmImg;
						}
						
						/*if(bitMatrix == null){
							return;
						}*/
						File myTemplateFile = new File(COUPON_CODE_URL);
						File parentDir = myTemplateFile.getParentFile();
						if(!parentDir.exists()) {

							parentDir.mkdir();
						}

						if(!myTemplateFile.exists()) {
							
							MatrixToImageWriter.writeToStream(bitMatrix, "png", new FileOutputStream(
									new File(COUPON_CODE_URL)));	
						}
							
						imagecouponStr = coupon.getCouponName() + Constants.DELIMETER_DOUBLECOLON + 
								"<img id="+couponIdStr +
								" src="+ccPreviewUrl+" width="+coupon.getBarcodeWidth() +
								" height="+coupon.getBarcodeHeight()+ " />";
						imageCouponsPhList.add(imagecouponStr);
						imagecouponBeeStr = coupon.getCouponName() + Constants.DELIMETER_DOUBLECOLON + 
								"<img id=\""+couponIdStr + 
								"\" src=\""+ccPreviewUrl+"\" width=\""+coupon.getBarcodeWidth() +
								"\" height=\""+coupon.getBarcodeHeight()+ "\" />";
						imageCouponsPhBeeList.add(imagecouponBeeStr); 
						
					}///subscriber/images/CC_test_150_150.png
					
					couponsPhList.add(textcouponStr);
					
					
					
					
				} // for
					
				//couponsPhList.add("CC_TEST::<img id=dummyid src=http://localhost:8080/subscriber/images/CC_test_150_150.png /> ");
				
			} // if
				
			// populate Coupons JS Array
			Clients.evalJavaScript("var ocCouponsArr = [];");
			//Clients.evalJavaScript("var mergeContents79 = [];");
			Clients.evalJavaScript("var specialLinks79 = [];");
			int jsInd=0;
			for (String couponPlaceHolder : couponsPhList) {
				if(couponPlaceHolder.trim().startsWith("--") || couponPlaceHolder.toLowerCase().contains(("place holder"))) { //Ignore
					continue;
				}
				Clients.evalJavaScript("ocCouponsArr["+ (jsInd++) +"]=\""+couponPlaceHolder+"\";");
			} // for
			
			Clients.evalJavaScript("var ocImageCouponsArr = [];");
			Clients.evalJavaScript("var barCodes79 = [];");

			int jsInd1=0;
			for (String couponPlaceHolder : imageCouponsPhList) {
				if(couponPlaceHolder.trim().startsWith("--") || couponPlaceHolder.toLowerCase().contains(("place holder"))) { //Ignore
					continue;
				}
				Clients.evalJavaScript("ocImageCouponsArr["+ (jsInd1++) +"]=\""+couponPlaceHolder+"\";");
			} // for

			//barCodes
			
			StringBuffer bufferImg = new StringBuffer();
			//int jsInd1=0;
			for (String couponPlaceHolder : imageCouponsPhBeeList) {
				if(couponPlaceHolder.trim().startsWith("--") || couponPlaceHolder.toLowerCase().contains(("place holder"))) { //Ignore
					continue;
				}
				String[] imagePHArr= couponPlaceHolder.split(Constants.DELIMETER_DOUBLECOLON );
				String coupImgName = imagePHArr[0];
				String coupImgValue = imagePHArr[1];
				if(bufferImg.length() > 0) bufferImg.append(",");
				bufferImg.append("{name: '" +coupImgName+ "', value: '"+coupImgValue+"'}"); 
			} // for
			
			String barCodeImg=bufferImg.toString();
			
			Clients.evalJavaScript("barCodes79 = ["+barCodeImg+"];");
			///////////VVVVV
			int len = couponsPhList.size();

			StringBuffer buffer = new StringBuffer();
			for (String couponPlaceHolder : couponsPhList) {
				if(couponPlaceHolder.trim().startsWith("--") || couponPlaceHolder.toLowerCase().contains(("place holder"))) { //Ignore
					continue;
				}
				String[] coupPHArr = couponPlaceHolder.split(Constants.DELIMETER_DOUBLECOLON );
				//System.out.println(couponPlaceHolder);
				String name = coupPHArr[0];
				String value = coupPHArr[1];
				if(buffer.length() > 0) buffer.append(",");
				buffer.append("{name: '" +name+ "', value: '["+value+"]'}"); 

			} // for
			StringBuffer specialLinksBuffer = new StringBuffer();
			for (String couponPlaceHolder : couponsPhList) {
				if(couponPlaceHolder.trim().startsWith("--") || couponPlaceHolder.toLowerCase().contains(("place holder"))) { //Ignore
					continue;
				}
				String[] coupPHArr = couponPlaceHolder.split(Constants.DELIMETER_DOUBLECOLON );
				//System.out.println(couponPlaceHolder);
				String name = coupPHArr[0];
				String value = coupPHArr[1];
				if(specialLinksBuffer.length() > 0) specialLinksBuffer.append(",");
				specialLinksBuffer.append("{type: 'DiscountCodes',label: '" +name+ "', link: 'BEEFREEPROMOCODE_START"+value+"BEEFREEPROMOCODE_END'}"); 
			} // for


			String str=buffer.toString();
			//System.out.println("mergeContents = ["+str+"];");
			//Clients.evalJavaScript("alert(str);");
			String specialLinkStr=specialLinksBuffer.toString();
		//	specialLinkStr= specialLinkStr + specialLinksBuffer1.toString();
			specialLink1 = specialLinkStr;
			Clients.evalJavaScript("mergeContents79 = ["+str+"];");
			Clients.evalJavaScript("specialLinks79 = ["+specialLinkStr+"];");
			logger.debug("-- Exit --");
			return couponsPhList;
		} catch (Exception e) {
			logger.error("Exception ::" , e);;
			return null;
		}
	}
	
	
	
	public static List<String> getMilestones()	{ 
	
		try {
			// **************** Populate Coupon codes as a Place holder *********************
			// add("CC_coupon Name::|^CC_123_couponName^|");
			List<String> displayList= new ArrayList<String>(); 			
			List<LoyaltyProgram> progTList = new ArrayList<LoyaltyProgram>(); 
			List<LoyaltyProgramTier> tierList = new ArrayList<LoyaltyProgramTier>();
			List<LoyaltyProgram> progList = new ArrayList<LoyaltyProgram>(); 
			List<LoyaltyThresholdBonus> bonusList = new ArrayList<LoyaltyThresholdBonus>();

			
			loyaltyProgramDao = (LoyaltyProgramDao)SpringUtil.getBean(OCConstants.LOYALTY_PROGRAM_DAO);
			progTList = loyaltyProgramDao.getTierEnabledProgListByUserId(GetUser.getUserObj().getUserId());
			progList = loyaltyProgramDao.getProgListByUserId(GetUser.getUserObj().getUserId()) ;
			
			if(progTList!=null){
				for (LoyaltyProgram loyaltyProgram : progTList) {
						loyaltyProgramTierDao = (LoyaltyProgramTierDao)SpringUtil.getBean(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
						tierList = loyaltyProgramTierDao.getTierListByPrgmId(loyaltyProgram.getProgramId());
					//	tierList1.addAll(tierList);
						String programName= loyaltyProgram.getProgramName();
						if(programName.contains("'"))
						{
							programName = programName.replace("'", "\\'");
						}
						//logger.info("programName============="+programName);
				
						if(tierList!=null) {
				
							for (LoyaltyProgramTier tier : tierList) {

								String textTierStr="";
								String textTierName="";
								String textTierType="";
								String Type="LPT";
								textTierType=tier.getTierType();
								if(!textTierType.equals("Tier 1")){
									textTierStr = programName  + " " + textTierType + Constants.DELIMETER_DOUBLECOLON + 
											Constants.CF_START_TAG + "MLS"+ "_" + Type + "_" +  tier.getTierId() + Constants.CF_END_TAG;;
							
							displayList.add(textTierStr);
					 }
					}
			       } // for
				}					
			} // if
			  
			if(progList!= null){
						
			for(LoyaltyProgram loyaltyProgram : progList)
			{
				loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao)SpringUtil.getBean(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);				
				bonusList = loyaltyThresholdBonusDao.getBonusListByPrgmId(loyaltyProgram.getProgramId());
				String programName= loyaltyProgram.getProgramName();
				if(programName.contains("'")){
					programName= programName.replace("'", "\\'");
					
				}
				String textBonusStr = " ";
				if(bonusList!= null){ 
			
			for (LoyaltyThresholdBonus bonus : bonusList) {
				if(bonus.getRegistrationFlag()=='N'){
					
				
				String Type="LB";
				String currSymbol = Utility.countryCurrencyMap.get(GetUser.getUserObj().getCountryType());
				if(currSymbol != null && !currSymbol.isEmpty())  userCurrencySymbol  = currSymbol + " ";
			
				if(!bonus.getExtraBonusType().equalsIgnoreCase("Points")){
						
					String bonusType =  userCurrencySymbol;
					
				}
				
				String bonusType = (bonus.getExtraBonusType().equalsIgnoreCase("Points")) ? bonus.getExtraBonusType() : userCurrencySymbol ; 
				
				
				textBonusStr =  programName + " " + "Bonus of "+ bonus.getExtraBonusValue()+ " "  + bonusType + 
									 Constants.DELIMETER_DOUBLECOLON + Constants.CF_START_TAG 
									+ "MLS"+ "_" + Type + "_" +  bonus.getThresholdBonusId() + Constants.CF_END_TAG;;
						
						
					
						displayList.add(textBonusStr);
				}
				}
			}
			}
			
			}
			
			
			
			
			
			CouponsDao couponsDao =  (CouponsDao)SpringUtil.getBean("couponsDao");
			List<Coupons> couponList = couponsDao.findCouponsByOrgId(GetUser.getUserObj().getUserOrganization().getUserOrgId());
			
			if(couponList!=null) {
				String textPromoStr = "";
				String Type = "LP";
				for (Coupons coupon : couponList) {
					//logger.info("just a coupon---------------------"+coupon);
					try{
				 if(coupon.getLoyaltyPoints()!=null){
					 //byte b=(Byte)coupon.getLoyaltyPoints().byteValue();
				//	 logger.info("loyalty points------------------->"+b);
					if(coupon.getLoyaltyPoints().byteValue()==1)
					{
						if(coupon.getRequiredLoyltyPoits()>0){
					textPromoStr =coupon.getCouponName()  + Constants.DELIMETER_DOUBLECOLON + 
							Constants.CF_START_TAG + "MLS"+ "_" + Type + "_" +  coupon.getCouponId() + Constants.CF_END_TAG;;
							
							displayList.add(textPromoStr);
						}
					}
				 }
					else continue;
					//logger.info("loyalty points....................._>"+coupon.getLoyaltyPoints().byteValue());
					}
					catch (Exception e) {
						logger.error("Exception ::" , e);
				}
				}
			}
		//logger.info("displayList---------------------"+displayList);
						
			// populate Coupons JS Array
			Clients.evalJavaScript("var ocCouponsArr = [];");
			Clients.evalJavaScript("var specialLinks79 = [];");					
			StringBuffer specialLinksBuffer1 = new StringBuffer();
			for (String couponPlaceHolder : displayList) {
				if(couponPlaceHolder.trim().startsWith("--") || couponPlaceHolder.toLowerCase().contains(("place holder"))) { //Ignore
					continue;
				}
				String[] coupPHArr = couponPlaceHolder.split(Constants.DELIMETER_DOUBLECOLON );
				String name = coupPHArr[0];
				String value = coupPHArr[1];	
				
				if(specialLinksBuffer1.length() > 0) specialLinksBuffer1.append(",");
				specialLinksBuffer1.append("{type: 'Milestones',label: '" +name+ "', link: 'BEEFREEPROMOCODE_START"+value+"BEEFREEPROMOCODE_END'}"); 
				
				//logger.info("names that will be displayed"+name);
				//logger.info("value of the merge tag-----------"+value);
				//logger.info("specialLinksBuffer1---------------"+specialLinksBuffer1);
			} // for
			
			
			String specialLink2= specialLinksBuffer1.toString();
			
			
			String specialLink3 = "";
			if(specialLink1!=null && !specialLink1.isEmpty()) {
				
				specialLink3 += specialLink1;
			} 
			if(specialLink2  != null && !specialLink2.isEmpty()){
				if(!specialLink3.isEmpty()) specialLink3+=",";
				
				specialLink3 += specialLink2;
				
			}
			
			/*if(specialLink1!=null && !specialLink1.isEmpty() && specialLink2!=null && !specialLink2.isEmpty()){
				 specialLink3 = specialLink1 + ","+ specialLink2;
			}else if(specialLink1==null || specialLink1.isEmpty() && specialLink2!=null && !specialLink2.isEmpty())
			{
				specialLink3 = specialLink2;
			}else if(specialLink1!=null || !specialLink1.isEmpty() && specialLink2==null && specialLink2.isEmpty()){
				specialLink3 = specialLink1;
			}*/
			
			
			//String specialLink3 = specialLink1 + "," + specialLink2;
			//specialLink3 = specialLink1 + "," + specialLink2;
			Clients.evalJavaScript("specialLinks79 = ["+specialLink3+"];");
			//Clients.evalJavaScript("specialLinks79 = ["+specialLink1+""+ specialLink1!=null && !specialLink1.isEmpty() && specialLink2!=null && !specialLink2.isEmpty() ? ","+specialLink2+"":""+specialLink2+"];");
			//logger.info("specialLink3------------------"+specialLink3);
			//logger.debug("displayList-------------------->"+displayList);

			logger.debug("-- Exit --");
			return displayList;
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
	
}
	
	
	//getToken
	/*public static String getToken(){
		
		 try {
			logger.info("Issued to is:::");
				//String postData = "cfStr="+cfStr+"&issuedTo="+to+"&type="+type;
			 
			JSONObject jsonToken=null;
			String value="";
			 String postData = "grant_type=password&client_id=268870de-d888-4dae-8edd-cb5b5c07a4ed&client_secret=5vXU6IKWyU3yXfH78yo7KhYTcdiufOzAtWv1ms4ZHLo3bNK7VU0";
			 //URL url = new URL(PropertyUtil.getPropertyValue(Constants.COUP_PROVIDER_FOR_SUBSCRIBER_URL));
			 URL url = new URL("https://auth.getbee.io/apiauth");
			 
				HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
				
				urlconnection.setRequestMethod("POST");
				urlconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				urlconnection.setDoOutput(true);

				OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
				out.write(postData);
				out.flush();
				out.close();
				
				
				BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
				
				String decodedString = "";
				while ((decodedString = in.readLine()) != null) {
					value += decodedString;
				}
				in.close();
				logger.info("response is======>"+value);
				jsonToken  = (JSONObject)JSONValue.parse(value);
				String finialTokenValue = (String)jsonToken.get("access_token");
				
				logger.info("access_token...."+finialTokenValue);
				
				
				//Clients.evalJavaScript("var token="+finialTokenValue+";");
				
				
				
				
				
				
				
				
				return value;
				
				//return finialTokenValue;
				
				
				
				
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return null;
		
			
	}//getToken

	*/
	
	protected boolean checkSpam(String htmlStuff) {
		logger.debug("-- Just Entered --");
		
		boolean result = false;

		String response = getSpamResult(htmlStuff); 
		logger.debug("Spam Report : \n" + response );

		if(response==null) {
			return true;
		}

		try {
			if(response.indexOf("(")<0) {
				logger.debug("Problem while getting spam report");
				return result;
			}
			String output = response.substring(response.indexOf("Content analysis details"));
			String scoreStr = output.substring(output.indexOf("(")+1,output.indexOf(")"));
			String[] scores = scoreStr.split(" ");
			for (String token : scores) {
				logger.debug("Token : " + token);
				
			}
		
			float hit = Float.parseFloat(scores[0]);
			if(0 <= hit && hit <= 2) {
				result = true;
			}
			else if(2 < hit && hit <= 4) {
				int confirm = Messagebox.show("Email has spam content. Do you want to continue saving the email?\n" +
						"Click on \"Check for Spam score\" for more details.",
						"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				result = (confirm == 1);
			}
			else if(hit > 4) {
				MessageUtil.setMessage("Email has SPAM content. Click on \"Check for Spam score\" for more details.", "color:red", "TOP");
				result = false;
			}

			logger.debug(" Result : "+ result);
		} catch (NumberFormatException e) {
			logger.debug("NumberFormatException : ",(Throwable)e);
		}catch (IndexOutOfBoundsException e) {
			logger.debug("IndexOutOfBoundsException : ",(Throwable)e);
		} catch (Exception e) {
			logger.debug("Exception : ",(Throwable)e);
		}
		return result;
	}
	
	public void checkSpam(String htmlStuff, boolean doWindow){
		logger.debug("-- Just Entered --");
		
		String response = getSpamResult(htmlStuff); 
		logger.debug("Spam Report : \n" + response );
		
		if(response==null) {
			MessageUtil.setMessage("Unable to get SPAM report.", "color:blue", "TOP");
			return;
		}
		
		String output;
		try {
			output = response.substring(response.indexOf("Content analysis details"));
		} catch (NumberFormatException e1) {
			logger.debug("NumberFormatException : ",(Throwable)e1);
			output = "Problem while getting spam report"; 
		}
		
		
		String legend = "If Spam Score Is:\n\t 0  to  2 : Good;" +
				"\n\t 2  to  4 : Not bad;\n\tabove 4 : Bad.\n\n" ;
		output=output.replace(output.substring(25, 29),"Spam Score is ");
		output=output.replace(", 5.0 required)", "");
		output =  legend + output;
		
		
		try {
			MultiLineMessageBox.doSetTemplate();
			MultiLineMessageBox.show(output, "Spam Info", MultiLineMessageBox.OK, "INFORMATION", false);
		} catch (InterruptedException e2) {
			logger.debug("Exeption : ", (Throwable)e2);
		}
	}
	
	protected String getSpamResult(String htmlStuff) {
		
		if(campaign==null) {
			return null;
		}
		
		String sub = campaign.getSubject();
		String emlFilePath = PropertyUtil.getPropertyValue("usersParentDirectory") + 
				"/" + userName + "/message.eml";
		if(logger.isDebugEnabled())logger.debug("Eml file Path : " + emlFilePath);
		
		StringBuffer response = (new SpamChecker()).checkSpam(sub, htmlStuff, emlFilePath, true); 
		return ( response==null?null:response.toString() );
	}
	
	public void back(){
		String isBack = (String)sessionScope.getAttribute("isTextMsgBack");
		if((isBack != null && isBack.equalsIgnoreCase("true"))|| isEdit == null){
			Redirect.goTo(PageListEnum.CAMPAIGN_LAYOUT);
		}else if(isEdit!=null){
			if(isEdit.equalsIgnoreCase("edit")){
				Redirect.goTo(PageListEnum.CAMPAIGN_FINAL);
			}else if(isEdit.equalsIgnoreCase("view")){
				//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW);
				Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
			}
		}
	}
	
	public void cancelEmailWizard(){
		try{
			int confirm = Messagebox.show("Do you want to cancel editing of email?",
					"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm == 1){
				sessionScope.removeAttribute("emailLayout");
				sessionScope.removeAttribute("selectedTemplate");
				Redirect.goTo(PageListEnum.CAMPAIGN_LAYOUT);
			}
		}catch (Exception e) {
		}
	}
	
	protected void setFromPage(){
		if(editorType!=null){
			if(editorType.equalsIgnoreCase("blockEditor")){
				PageUtil.setFromPage("Editor");
			}else if(editorType.equalsIgnoreCase("plainTextEditor")){
				PageUtil.setFromPage("plainEditor");
			}else if(editorType.equalsIgnoreCase("beeEditor")){
					PageUtil.setFromPage("beeEditor");
			}else
				PageUtil.setFromPage("uploadHTML");
		}
	}
	
	public static String removeEditorTags(String content) {
		StringBuffer finalHTML = new StringBuffer(content);
		String pattern = "<div[^>]*?name=\"TMCEeditableDiv\"[^>]*?>";
		Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(finalHTML);
		StringBuffer removeEditor = new StringBuffer();
		if(logger.isDebugEnabled())logger.debug("---------------------" + pattern + "------------------------------");
		while (m.find()) {
			String replaceStr = "<div name=\"TMCEeditableDiv\" style=\"padding-top:5px;\">";
			logger.debug ("Replaced String :" + replaceStr);
			m.appendReplacement(removeEditor, replaceStr);
		}
		if(logger.isDebugEnabled())logger.debug("---------------------editor stuff is replaced------------------------------");
		m.appendTail(removeEditor);
		return removeEditor.toString();
	}
	
	public static String createEditorTags(String content){
		StringBuffer finalHTML = new StringBuffer(content);
		String pattern = "<div[^>]*?name=\"TMCEeditableDiv\"[^>]*?>";
		Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(finalHTML);
		long timeInMillis = System.currentTimeMillis();
		StringBuffer contentWithTags = new StringBuffer();
		if(logger.isDebugEnabled())logger.debug("---------------------" + pattern + "------------------------------");
		while (m.find()) {
			String replaceStr = PropertyUtil.getPropertyValue("tmceEditableDivSyntax");
			if(replaceStr!=null) {
				replaceStr = replaceStr.replace("|^divId^|", (timeInMillis++) +"");
				logger.debug ("Replaced String :" + replaceStr);
				m.appendReplacement(contentWithTags, replaceStr);
			}
		}
		if(logger.isDebugEnabled())logger.debug("---------------------editor stuff is replaced------------------------------");
		m.appendTail(contentWithTags);
		return contentWithTags.toString();
	}
	
	public static List<String> getDigitalReciptPlaceHolderList(Set<MailingList> mlistSet) {
		
		try {
			logger.debug("-- Just Entered --");
			MLCustomFieldsDao mlCustomFieldsDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
			logger.debug("Got Ml Set of size :" + mlistSet.size());
			
			List<String> placeHoldersList = new ArrayList<String>(); 
			POSMappingDao posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
			List<POSMapping> contactsGENList = posMappingDao.findOnlyByGenType(Constants.POS_MAPPING_TYPE_CONTACTS, GetUser.getUserId() );
			placeHoldersList.addAll(Constants.PLACEHOLDERS_LIST);
			Users user = GetUser.getUserObj();
			
			if(user.getloyaltyServicetype() != null && user.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_SB) )
			{
				placeHoldersList.removeIf(e -> e.contains("Loyalty Gift Balance"));
			}
			if(user.getloyaltyServicetype() != null && user.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_OC) )
			{
				placeHoldersList.removeIf(e -> e.contains("Loyalty Membership Pin"));
			}
			
			if(user.getloyaltyServicetype() != null && !user.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_SB) )
			{
				//logger.info("the current user is a oc user");
				placeHoldersList.addAll(Constants.OCPLACEHOLDERS_LIST);
			}

			
			Map<String , String> StoreDefaultPHValues = getDefaultStorePhValue(placeHoldersList);
			/*Map<String, String> placeholders = new HashMap<String, String>();
			placeholders.putAll(Constants.PLACEHOLDERS_MAP);*/
			
			
			/*if( placeholders != null) {
				//2.prepare merge tag and add to placeHoldersList
				//format : display lable :: |^GEN_<UDF>^|
				
				Set<String> phKeyset =  placeholders.keySet();
				for (String phkey : phKeyset) {
					
					String phVal = placeholders.get(phkey);
					
					String[] phVal1 = phVal.split("::"); 
					String ph = phVal1[0].trim();
					String phDeftval = null;
					String genStr = null;
					
					for (POSMapping posMapping : contactsGENList) {
						if(posMapping.getCustomFieldName().trim().equals(phVal1[1].trim())){
							phDeftval = posMapping.getDefaultPhValue();
							break;
						}
					
						
						}
					
					if( phDeftval == null || phDeftval.trim().isEmpty() ) {
					
						genStr = phVal1[1]+ Constants.DELIMETER_DOUBLECOLON +
								Constants.CF_START_TAG + ph + Constants.CF_END_TAG ;
					
					}
					else {
						
						genStr = phVal1[1] + Constants.DELIMETER_DOUBLECOLON +
								Constants.CF_START_TAG + ph + Constants.DELIMETER_SLASH + phDeftval + Constants.CF_END_TAG ;
					}
					placeHoldersList.add(genStr);
					
					
					
				}//for
				
			placeHoldersList.addAll(Constants.PLACEHOLDERS_LIST);
			
		}//if
			*/
			//Changes to add mapped UDF fields as placeholders
			//1.get all the pos mapped UDFs from the user pos settings(table:pos_mappings)
			List<POSMapping> contactsUDFList = posMappingDao.findOnlyByType(Constants.POS_MAPPING_TYPE_CONTACTS, GetUser.getUserId() );



			if(contactsUDFList != null) {

				//2.prepare merge tag and add to placeHoldersList
				//format : display lable :: |^GEN_<UDF>^|
				for (POSMapping posMapping : contactsUDFList) {

					String udfStr;
					if(posMapping.getDefaultPhValue()==null || posMapping.getDefaultPhValue().trim().isEmpty()) {

						udfStr = Constants.UDF_TOKEN + posMapping.getDisplayLabel() +
								Constants.DELIMETER_DOUBLECOLON +
								Constants.CF_START_TAG + Constants.UDF_TOKEN +
								posMapping.getCustomFieldName()  +Constants.DELIMETER_SPACE + Constants.DELIMETER_SLASH + Constants.DELIMETER_SPACE + Constants.DEFUALT_TOKEN+ Constants.CF_END_TAG ;


					}
					else {
						udfStr = Constants.UDF_TOKEN + posMapping.getDisplayLabel() +
								Constants.DELIMETER_DOUBLECOLON +
								Constants.CF_START_TAG + Constants.UDF_TOKEN +
								posMapping.getCustomFieldName()+ Constants.DELIMETER_SPACE + Constants.DELIMETER_SLASH + Constants.DELIMETER_SPACE + Constants.DEFUALT_TOKEN + posMapping.getDefaultPhValue() + Constants.CF_END_TAG ;


					}
					placeHoldersList.add(udfStr);
				}//for



			}//if

			for (MailingList mailingList : mlistSet) {
				if(!mailingList.isCustField())  continue;
				
				List<MLCustomFields> mlcust = mlCustomFieldsDao.findAllByList(mailingList);
				String custField ;
				for (MLCustomFields customField : mlcust) {
					custField = Constants.CF_TOKEN + customField.getCustFieldName() 
							+ Constants.DELIMETER_DOUBLECOLON + Constants.CF_START_TAG + 
							Constants.CF_TOKEN + 
							customField.getCustFieldName().toLowerCase() + Constants.CF_END_TAG;

					if(placeHoldersList.contains(custField)) continue;
					placeHoldersList.add(custField);
				}
				
			} // for
			
			// Populate js variable 'phArr' with the place holders for all Editors
			Clients.evalJavaScript("var phArr = [];");
			int jsInd=0;
			for (String placeHolder : placeHoldersList) {
				if(placeHolder.trim().startsWith("--") || placeHolder.toLowerCase().contains("place holder") || 
					placeHolder.startsWith("Unsubscribe Link") || placeHolder.startsWith("Web-Page Version Link") /*||
					placeHolder.startsWith("Store")*/ ||placeHolder.startsWith("Share on Twitter")||placeHolder.startsWith("Share on Facebook")||placeHolder.startsWith("Forward To Friend")
					||placeHolder.startsWith("Created Date")||placeHolder.startsWith("Last Purchase Store Name")||placeHolder.startsWith("Last Purchase Store Manager")||placeHolder.startsWith("Last Purchase Store Phone")
					||placeHolder.startsWith("Last Purchase Store Email")||placeHolder.startsWith("Last Purchase Store Street")||placeHolder.startsWith("Last Purchase Store City")
					||placeHolder.startsWith("Last Purchase Store State")||placeHolder.startsWith("Last Purchase Store Zip")
					||placeHolder.startsWith("Subscriber Preference Link")) { //Ignore
					continue;
				}
				
				String[] phTokenArr =  placeHolder.split("::"); 
				String key = phTokenArr[0];
				StringBuffer value = new StringBuffer(phTokenArr[1]);
				
				
				if(StoreDefaultPHValues.containsKey(placeHolder)) {
					
					value.insert(value.lastIndexOf("^"), StoreDefaultPHValues.get(placeHolder));
					//logger.info(" store ::"+placeHolder + " ====== value == "+value );
					placeHolder = placeHolder.replace(phTokenArr[1], value.toString());
					
					
				}
				
				
				//logger.debug("key ::"+key+" value ::"+value);
				for (POSMapping posMapping : contactsGENList) {
					
					if(!key.equalsIgnoreCase(posMapping.getCustomFieldName()) || posMapping.getCustomFieldName().startsWith("UDF")  ) continue;
					
					if(posMapping.getDefaultPhValue() == null || posMapping.getDefaultPhValue().isEmpty() ) break;
					
					value.insert(value.lastIndexOf("^"), posMapping.getDefaultPhValue() );
					logger.debug(" value ::"+value);
					placeHolder = placeHolder.replace(phTokenArr[1], value);
				}
				//logger.info("beforeeeeeeeee 2222222 :"+placeHolder);
				placeHolder = StringEscapeUtils.escapeJavaScript(placeHolder);
				//logger.info("afterrrrrrrrrrrrr 22222222 :"+placeHolder);
				Clients.evalJavaScript("phArr["+ (jsInd++) +"]=\""+placeHolder+"\";");
			} // for
						
			
			logger.debug("-- Exit --");
			return placeHoldersList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
			return null;
		}
	}
	
	
		
	
	public class phListRenderer implements ListitemRenderer {

		@Override
		public void render(Listitem item, Object data,int arg2) throws Exception {
			// TODO Auto-generated method stub
			if(data instanceof String) {
				String phStr = (String)data;
				if(phStr.indexOf(Constants.DELIMETER_DOUBLECOLON) < 0) return;
				String[] tokens = phStr.split(Constants.DELIMETER_DOUBLECOLON);
				item.setLabel(tokens[0]);
				item.setValue(tokens[1]);
			}
		}
	}

	public static void main(String[] args) {
		
		getDefaultStorePhValue(Constants.PLACEHOLDERS_LIST);
		
	}
	
}
