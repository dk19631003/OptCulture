package org.mq.marketer.campaign.controller.layout;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignSchedule;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.SystemTemplates;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.PrepareFinalHtml;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CampaignScheduleDao;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.CampaignsDaoForDML;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.SystemTemplatesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.general.CampaignStepsEnum;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.RightsEnum;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataIntegrityViolationException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class UploadHTMLController extends EditorController {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	//private Session sessionScope = null;
	
	private Textbox htmlTbId;
	private Textbox testEmailTbId;
	private String htmlText="";
	private MyTemplatesDao myTemplatesDao;
	private Button saveAsDraftBtnId,nextBtnId,saveNewBtnId,saveEditBtnId;
	private Listbox phLbId, cphLbId;
	private boolean draft = true;
	private Window winId;
	private boolean isAdmin;
	private Groupbox coupGbId;
	private Timer htmlAutoSaveTimerId;
	private Label autoSaveLbId;
	private CampaignScheduleDao campaignScheduleDao;
	private String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
	static final String APP_MAIN_URL = "https://qcapp.optculture.com/subscriber/";
	static final String APP_MAIN_URL_HTTP = "http://qcapp.optculture.com/subscriber/";
	static final String MAILCONTENT_URL = "http://mailcontent.info/subscriber/";
	static final String MAILHANDLER_URL = "http://mailhandler01.info/subscriber/";
	private static final String ImageServer_Url = PropertyUtil.getPropertyValue("ImageServerUrl");

	public UploadHTMLController(){
		String style = "font-weight:bold;font-size:15px;color:#313031;" +"font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Create Email (Step 4 of 6)","",style,true);
		this.campaignScheduleDao = (CampaignScheduleDao)
				SpringUtil.getBean("campaignScheduleDao");
		
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		//sessionScope = Sessions.getCurrent();
		 isAdmin = (Boolean)sessionScope.getAttribute("isAdmin");
		this.myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
		if(campaign==null){
			//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW);
			Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
		}
		
		Utility.breadCrumbFrom(4);
		
		UserActivitiesDao userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		UserActivitiesDaoForDML userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
	
		/*if(userActivitiesDao != null) {
	      	userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_HTML_EDITOR,GetUser.getUserObj());
		 }*/
		if(userActivitiesDaoForDML != null) {
	      	userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_HTML_EDITOR,GetUser.getLoginUserObj());
		 }

		/*if(campaign==null){
			Redirect.goTo(PageListEnum.CAMPAIGN_VIEW);
		}*/
		
		this.htmlTbId = htmlTbId;
		this.testEmailTbId = testEmailTbId;
		if(isEdit != null){
			if(isEdit.equals("edit")){
				saveAsDraftBtnId.setVisible(false);
				nextBtnId.setVisible(false);
				saveNewBtnId.setVisible(false);
				saveEditBtnId.setVisible(true);
			}
		}
		String selectedTemplate = (String)sessionScope.getAttribute("selectedTemplate");
		if(selectedTemplate != null && selectedTemplate.contains("/")) {
			String[] temp = StringUtils.split(selectedTemplate, '/');
				String categoryName = temp[0];
				String templateName = temp[1];
				if(logger.isDebugEnabled())logger.debug("----templateName ---:"+
						templateName + "----categoryName ---:"+categoryName);
				
				if(categoryName.equalsIgnoreCase("MyTemplates")) {//may not mean the current user
					MyTemplates myTemplate = myTemplatesDao.
					findByUserNameAndTemplateName(currentUser.getUserId(), temp[2],templateName);
					logger.debug("myTemplate :"+myTemplate);
					htmlTbId.setValue(myTemplate.getContent());
				}
				else {
					logger.debug("<<<<<<<<<<<< Loading UploadHtmlstuff>>>>>>");
					htmlTbId.setValue(campaign.getHtmlText()!=null?campaign.getHtmlText():"");
				}
		}
		else {
			htmlTbId.setValue(campaign.getHtmlText()!=null?campaign.getHtmlText():"");
		}
		
		getPlaceHolderList(campaign.getMailingLists(), false, phLbId);
		//if(phLbId.getItemCount() > 0) phLbId.setSelectedIndex(0);
		/*phLbId.setItemRenderer(new phListRenderer());
		phLbId.setModel(new SimpleListModel(placeHoldersList));*/
		
		Set<String> userRoleSet = (Set<String>)Sessions.getCurrent().getAttribute("userRoleSet");
		//logger.debug("userRoleSet ::"+userRoleSet);
			//PageListEnum plEnum = PageListEnum.ADMIN_VIEW_COUPONS;
		if(userRoleSet.contains(RightsEnum.MenuItem_Promocodes_VIEW.name())) {
			
			List<String> couponPhList = EditorController.getCouponsList();
			cphLbId.setItemRenderer(new phListRenderer());
			cphLbId.setModel(new SimpleListModel(couponPhList));
			coupGbId.setVisible(true);
		}
		Clients.evalJavaScript("parent.window.scrollTo(0,0)");
	}
	
	public void onTimer$htmlAutoSaveTimerId() {
		
		try {
			TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
			CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao"); 
			Campaigns dbcampaign = campaignsDao.findByCampaignId(campaign.getCampaignId());
			CampaignsDaoForDML campaignsDaoForDML = (CampaignsDaoForDML)SpringUtil.getBean("campaignsDaoForDML");
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
			SystemTemplatesDao systemTemplateDao = (SystemTemplatesDao)SpringUtil.getBean("systemTemplatesDao");
			htmlText = htmlTbId.getValue();
			SystemTemplates st = systemTemplateDao.findByName("plainHtmlEditor");
			campaign.setEditorType("plainHtmlEditor");
			campaign.setTemplate(st);
			campaign.setHtmlText(htmlText);
			campaign.setPrepared(false);
			campaign.setModifiedDate(Calendar.getInstance());
			campaignsDaoForDML.saveOrUpdate(campaign);
			//saveHTMLEmail(false);
			autoSaveLbId.setValue("Last auto-saved at: "+ MyCalendar.calendarToString(Calendar.getInstance(),
					MyCalendar.FORMAT_SCHEDULE_TIME,tz));
		}
		catch (Exception e) {
			logger.error("** Exception :", e);
		}
		
	}
		
	
	 //public boolean saveHTMLEmail(boolean draft){
	public boolean onClick$saveNewBtnId(){
		
		try{
			
			 // TODO : Need to check this again.
			CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao"); 
			CampaignsDaoForDML campaignsDaoForDML = (CampaignsDaoForDML)SpringUtil.getBean("campaignsDaoForDML"); 
			SystemTemplatesDao systemTemplateDao = (SystemTemplatesDao)SpringUtil.getBean("systemTemplatesDao");
			Campaigns dbcampaign = campaignsDao.findByCampaignId(campaign.getCampaignId());
			if (campaign.getStatus().equalsIgnoreCase("Draft") && !(campaign.getDraftStatus().equalsIgnoreCase("complete") || campaign.getDraftStatus().equalsIgnoreCase("CampFinal"))) {
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
			logger.debug("-- just entered --");

			htmlText = htmlTbId.getValue();
			
			if(draft) {
			String isValidPhStr = null;
			isValidPhStr = Utility.validatePh(htmlText, currentUser);
			
			if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
				
				MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
				return false;
			}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
				
				MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either  enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
				return false;
				
			}
			
			String isValidCCDim = null;
			isValidCCDim = Utility.validateCouponDimensions(htmlText);
			if(isValidCCDim != null){
				return false;
			}
			String isValidCouponAndBarcode = null;
			isValidCouponAndBarcode = Utility.validateCCPh(htmlText, currentUser);
			if(isValidCouponAndBarcode != null){
				return false;
			}
			long size = Utility.validateHtmlSize(htmlText);
			if(htmlText.trim().length() == 0 && !draft==false){
				MessageUtil.setMessage("Email cannot be empty. Paste your HTML code.", "color:red", "TOP");
				return false;
			} else if(size >100) {
				String msgcontent = OCConstants.HTML_VALIDATION;
				msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
				MessageUtil.setMessage(msgcontent, "color:Blue");
			} 
			/*if(Utility.validateHtmlSize(htmlText)) {
				Messagebox.show("It looks like the content of your email is more " +
						"than the recommended size per the email-" + 
						"sending best practices. " + "To avoid email landing " + 
						"in recipient's spam folder, please redesign " 
									+"email to reduce content.", "Info", Messagebox.OK, Messagebox.INFORMATION);
			}*/			
			
			}
			if(!checkSpam(htmlText)){
				return false;
			}

			//logger.debug("htmlText :"+htmlText);
			
			SystemTemplates st = systemTemplateDao.findByName("plainHtmlEditor");
			campaign.setEditorType("plainHtmlEditor");
			campaign.setTemplate(st);
			campaign.setHtmlText(htmlText);
			campaign.setPrepared(false);
			
			if(isEdit!=null){
				if(isEdit.equalsIgnoreCase("view")) {
					//campaign.setStatus("Draft");
					//campaign.setStatusChangedOn(Calendar.getInstance());
					// campaign.setDraftStatus("CampTextMsg");
					
					if((campaign.getDraftStatus()==null) || 
							CampaignStepsEnum.plainHtmlEditor.getPos() >= CampaignStepsEnum.valueOf(campaign.getDraftStatus()).getPos()) {
						campaign.setDraftStatus("CampTextMsg");
					}
				}
				else {
					campaign.setModifiedDate(Calendar.getInstance());
				}
			}else{
				//campaign.setStatus("Draft");
				//campaign.setStatusChangedOn(Calendar.getInstance());
				// campaign.setDraftStatus("CampTextMsg");
				
				if((campaign.getDraftStatus()==null) || 
						CampaignStepsEnum.plainHtmlEditor.getPos() >= CampaignStepsEnum.valueOf(campaign.getDraftStatus()).getPos()) {
					campaign.setDraftStatus("CampTextMsg");
				}

			}
			//String Final_html = PrepareFinalHtml.replaceImgURL(campaign.getHtmlText(), campaign.getUsers().getUserName());
			String Final_html = campaign.getHtmlText().replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
					.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
					.replace(MAILHANDLER_URL, ImageServer_Url);
			campaign.setHtmlText(Final_html);
			//campaignsDao.saveOrUpdate(campaign);
			campaignsDaoForDML.saveOrUpdate(campaign);
			if(draft){
				MessageUtil.setMessage("Your HTML code has been saved successfully.", "color:blue", "TOP");
			}
			return true;
		}catch(DataIntegrityViolationException ie){
			logger.error(" Exception1 :"+ ie +" **");
			return false;
		}catch (Exception e) {
			logger.error("** Exception :"+ e.getMessage() +" **");
			return false;
		}
	}
	
	public boolean saveHTMLEmail(boolean draftStatus)  {
		
				draft = draftStatus; 
				boolean flag = onClick$saveNewBtnId();
				draft = true;
				return flag;
	} 
	
	//public void saveInMyTemplates(Window winId,String name,String htmlStuff){
	private Listbox winId$myTemplatesListId;
	private Textbox winId$templatNameTbId;
	public void onClick$myTemplatesSubmtBtnId$winId() {
		try{
			if(logger.isDebugEnabled())logger.debug("-- just entered --");
			Textbox nameTbId = (Textbox)winId.getFellowIfAny("templatNameTbId");
			super.saveInMyTemplates(winId,nameTbId.getValue(),htmlTbId.getValue(),"plainHtmlEditor",winId$myTemplatesListId);
		}catch (Exception e) {
			logger.error("Exception ::" , e);;
			logger.error("** Exception :"+ e +" **");
		}
	}
	
	//public void gotoPlainMsg(){
	public void onClick$saveEditBtnId() {
		
		if(!saveHTMLEmail(false)) return;
		
		sessionScope.removeAttribute("editCampaign");
		sessionScope.setAttribute("campaign", campaign);
		
		if(isEdit!=null){
			if(isEdit.equalsIgnoreCase("edit")){
				Redirect.goTo(PageListEnum.CAMPAIGN_FINAL);
			}else if(isEdit.equalsIgnoreCase("view")){
				Redirect.goTo(PageListEnum.CAMPAIGN_TEXT_MESSAGE);
			}
		}else{
			Redirect.goTo(PageListEnum.CAMPAIGN_TEXT_MESSAGE);
		}
	}
	
	public void onClick$nextBtnId() throws Exception {
		/*if(Utility.validateHtmlSize(htmlText)) {
			Messagebox.show("It looks like the content of your email is more " +
					"than the recommended size per the email-" + 
					"sending best practices. " + "To avoid email landing " + 
					"in recipient's spam folder, please redesign " 
								+"email to reduce content.", "Info", Messagebox.OK, Messagebox.INFORMATION);
		}*/
		
		long size = Utility.validateHtmlSize(htmlText);
		if(size >100) {
			String msgcontent = OCConstants.HTML_VALIDATION;
			msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
			MessageUtil.setMessage(msgcontent, "color:Blue");
		}
		
		onClick$saveEditBtnId();
	}
	
	//public void saveAsDraft(){
	public void onClick$saveAsDraftBtnId() throws Exception {
		List<CampaignSchedule> campScheduleList = campaignScheduleDao.getByCampaignId(campaign.getCampaignId());
		long activeCount = campScheduleList.stream().filter(campaignSchedule -> campaignSchedule.getStatus() == 0).count(); 
		if(campScheduleList.size() == 0 || activeCount == 0) {
			if(!saveHTMLEmail(false)) return;
			sessionScope.removeAttribute("campaign");
			sessionScope.removeAttribute("editCampaign");
			//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW);
			Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
		}else {
			//MessageUtil.setMessage(" A campaign with active schedules cannot be saved as draft. Please delete all active schedules first.", "color:red");
			MessageUtil.setMessage(" A campaign with upcoming schedule/s \n cannot be saved as a draft.\n Please delete all active schedules first.", "color:red");
			return;
		}
		
	}
	
	//public void back(){
	public void onClick$backBtnId() throws Exception {
		if(isEdit!=null){
			if(isEdit.equalsIgnoreCase("edit")){
				Redirect.goTo(PageListEnum.CAMPAIGN_FINAL);
			}else if(isEdit.equalsIgnoreCase("view")){
				//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW);
				Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
			}
		}else{
			Redirect.goTo(PageListEnum.CAMPAIGN_LAYOUT);
		}
	}
	
	
	
	//public void sendTestMail(String htmlStuff){
	// changes may be here
	public void onClick$testMailGoBtnId() throws Exception {
		
		try {
			
			String emailId=testEmailTbId.getValue();
			 if( emailId.equals("Email Address...") || emailId.isEmpty() ){
					MessageUtil.setMessage("There is no mail id to send a test mail.", "color:red");
					testEmailTbId.setValue("Email Address...");
				}
			
			else if(super.sendTestMail(htmlTbId.getValue(), testEmailTbId.getValue())) {
				//testMailTbId.setValue("");
				testEmailTbId.setValue("Email Address...");
			}
			
		/*	MessageUtil.clearMessage();
			
			String htmlStuff = htmlTbId.getValue();
			
			if(campaign==null){
				logger.error("Exception :Campaign was null");
				return;
			}
			
			EmailQueueDao emailQueueDao = (EmailQueueDao)SpringUtil.getBean("emailQueueDao");
			String emailId = testEmailTbId.getValue();
			boolean valid = Utility.validateEmail(emailId.trim());

			if(!valid){
				MessageUtil.setMessage("Enter valid email.", "color:red", "TOP");
				return;
			}
			
			if(htmlStuff.trim().length()==0) {
				MessageUtil.setMessage("Email should not be empty.", "color:red", "TOP");
				return;
			}
			
			if(!checkSpam(htmlStuff)){
				return;
			}
			
			campaign.setHtmlText(htmlStuff);
			campaign.setPrepared(false);
			Messagebox.show("Test mail will be sent in a moment.","Information",Messagebox.OK, Messagebox.INFORMATION);
			EmailQueue testEmailQueue = new EmailQueue(campaign,Constants.EQ_TYPE_TESTMAIL,"Active",emailId,MyCalendar.getNewCalendar(),GetUser.getUserObj()); 
			try{
				testEmailQueue.setMessage(htmlStuff);
				emailQueueDao.saveOrUpdate(testEmailQueue);
				testEmailTbId.setValue("Email Address...");
			}catch(Exception e1){
				logger.error("** Exception : Error while saving the Test Email into queue "+ e1 + " **");
			}*/
		} catch (Exception e) {
			logger.error("Exception : " + e);
		}
	}
	
	// changes here
	public void onBlur$testEmailTbId() throws Exception {
		 
		 String mail=testEmailTbId.getValue();
		 //logger.debug("here in on blur method mail id "+mail);
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

	public void onClick$spamScrBtnId(){
		super.checkSpam(htmlTbId.getValue(), true);
	}
	
	/*public void checkSpam(String htmlStuff, Button spamScrBtnId){
		if(campaign!=null && htmlStuff !=null){
			spamScrBtnId.setLabel("Checking....");
			String sub = campaign.getSubject();
			String emlFilePath = PropertyUtil.getPropertyValue("usersParentDirectory") + "/" + GetUser.getUserName() + "/message.eml";
			logger.debug("Eml file Path : " + emlFilePath);
			String output = (new SpamChecker()).checkSpam(sub, htmlStuff, emlFilePath);
			
			try {
				String[] arr = output.split("/");
				if(Float.parseFloat(arr[0]) > Float.parseFloat(arr[1])){
					spamScrBtnId.setStyle("color:red;");
				}else{
					spamScrBtnId.setStyle("color:black;");
				}
			} catch (Exception e) {
				logger.error("problem in calculating the float comparision : " + e);
			}
			spamScrBtnId.setLabel("Spam Score: " + output);
			logger.debug("Spam out put : " + output);
		}
	}*/
	
	//public void reload(){
	public void onClick$reloadTlbBtnId() {
		try{
			int confirm = Messagebox.show("Do you want to reload the email?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm != 1){
				return;
			}else{
				String htmlStuff = "";
				if(isEdit == null){
					htmlStuff = "";
				}
				else{
					htmlStuff = campaign.getHtmlText();
				}
				htmlTbId.setValue(htmlStuff);
			}
		}catch (Exception e) {
			logger.error("** Exception : " + e + "**");
		}
	}
	
	private Window previewIframeWin; 
	private  Iframe previewIframeWin$iframeId;
	public void onClick$htmlPreviewImgId() {
		htmlText = htmlTbId.getValue();
		/*if(Utility.validateHtmlSize(htmlText)) {
			Messagebox.show("It looks like the content of your email is more " +
					"than the recommended size per the email-" + 
					"sending best practices. " + "To avoid email landing " + 
					"in recipient's spam folder, please redesign " 
								+"email to reduce content.", "Info", Messagebox.OK, Messagebox.INFORMATION);
		}*/
		
		long size = Utility.validateHtmlSize(htmlText);
		if(size >100) {
			String msgcontent = OCConstants.HTML_VALIDATION;
			msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
			MessageUtil.setMessage(msgcontent, "color:Blue");
		}
		
		logger.info("entered into html preview....");
		if(campaign != null) {
			if(htmlTbId.getValue() != null){			
			 //String htmlContent=campaign.getHtmlText();
			 Utility.showPreview(previewIframeWin$iframeId,currentUser.getUserName(), htmlTbId.getValue());
			 previewIframeWin.setVisible(true);
			}
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
	private Textbox winId$htmlStuffId3;
	private Label winId$resLbId;
	public void onClick$saveInMyTemplateTbarId() {
		logger.debug("---save in my template toolbar btn clicked----");
		winId$htmlStuffId3.setValue("");
		winId$templatNameTbId.setValue("");
		winId$resLbId.setValue("");
		winId.setVisible(true);
		winId.setPosition("center");
		winId.doHighlighted();
		getMyTemplatesFromDb(currentUser.getUserId());
		
		
	}
	
	
	
	public void getMyTemplatesFromDb(Long userId){
		try {
			String userTemplatesDirectory = PropertyUtil.getPropertyValue("userTemplatesDirectory");
			Components.removeAllChildren(winId$myTemplatesListId);
			//MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
			File myTemp = null;
			File[] templateList = null;
			 String myTempPathStr = PropertyUtil.getPropertyValue("usersParentDirectory")+
						File.separator+ currentUser.getUserName()+userTemplatesDirectory;
			
			 myTemp= new File(myTempPathStr);
			 templateList = myTemp.listFiles();
			 for (Object obj: templateList) {
					final File template = (File)obj;
					Listitem item = new Listitem();
					String folderName=template.getName();
					
					 item.setLabel(folderName);
					 
					item.setParent(winId$myTemplatesListId);
					
					
}
			 if(winId$myTemplatesListId.getItemCount() > 0) winId$myTemplatesListId.setSelectedIndex(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}

}
 
		
}
