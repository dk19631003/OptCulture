package org.mq.marketer.campaign.controller.layout;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.SystemTemplates;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.SystemTemplatesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.general.CampaignStepsEnum;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.HTMLUtility;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkex.zul.Colorbox;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Popup;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

@SuppressWarnings("serial")
public class BlockEditorController extends EditorController {


	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private SystemTemplates st;
    
	String htmlStuff;
	String htmlPreviewStuff;
	String selectedTemplate;
	String templateName;
	String categoryName;
	
	Div appCenterDivId;
	Textbox htmlStuffId, htmlStuffId1, htmlStuffId2, spamChekTbId,
			testMailTbId, htmlStuffAId;
	Textbox testEmailTbId;
	Html html = new Html();
	Toolbarbutton addBlockBtnId, reloadTlbBtnId;
	Popup editPopup;
	Button saveAsDraftBtnId,nextBtnId,backBtId;
	Toolbarbutton saveNewTBarBtnId,saveEditBtnId, previewTBarBtnId;
//	Listbox phLbId;
	Window winId;
	Colorbox cboxId;
	private Label autoSaveLbId;

	// private boolean isAdmin;

	// appCenterDivId,htmlStuffId,testEmailTbId,saveAsDraftBtnId,nextBtnId,saveNewTBarBtnId,saveEditBtnId,phLbId,
	// addBlockBtnId, editPopup

	public BlockEditorController() {

		String style = "font-weight:bold;font-size:15px;color:#313031;"
				+ "font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Create Email (Step 4 of 6)", "", style, true);

		try {
			
			if(isEdit!=null){
				if(campaign == null){
					//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW);
					Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
				}
			}
			
			Utility.breadCrumbFrom(4);
			
			UserActivitiesDao userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
			/*if(userActivitiesDao != null) {
			  userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_BLOCK_EDITOR,GetUser.getUserObj());
			}*/
			
			UserActivitiesDaoForDML userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
			if(userActivitiesDaoForDML != null) {
			  userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_BLOCK_EDITOR,GetUser.getLoginUserObj());
			}
			
//			isAdmin = (Boolean)Sessions.getCurrent().getAttribute("isAdmin");
		} catch (Exception e) {
			logger.error("Exception : error occured while loading the controller **",e);
		}
	}

	
	
	/*public void init(Div appCenterDivId,Textbox htmlStuffId,
			Textbox testEmailTbId,Button saveAsDraftBtnId, Button nextBtnId, Button saveNewTBarBtnId, 
			Button saveEditBtnId, Listbox phLbId, Toolbarbutton addBlockBtnId , Menupopup editPopup ) {*/
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		
		try{
			
			super.doAfterCompose(comp);
			
			if(logger.isDebugEnabled())logger.debug("-- just entered --");
			if(campaign ==null || currentUser == null){
				Redirect.goTo(PageListEnum.RM_HOME);
				return;
			}
			/*this.appCenterDivId = appCenterDivId;
			this.htmlStuffId= htmlStuffId;
			this.testEmailTbId= testEmailTbId;
			this.addBlockBtnId = addBlockBtnId;
			this.editPopup = editPopup;*/
			
			getPlaceHolderList(campaign.getMailingLists());
			EditorController.getCouponsList();
			
			/*phLbId.setItemRenderer(new phListRenderer());
			phLbId.setModel(new SimpleListModel(placeHoldersList));*/
			

			if(logger.isDebugEnabled())logger.debug(" isEdit :"+isEdit);
			SystemTemplatesDao systemTemplatesDao = (SystemTemplatesDao)SpringUtil.getBean("systemTemplatesDao");
			MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
			boolean isView = true;
			String isBack = (String)sessionScope.getAttribute("isTextMsgBack");
			if(isEdit != null){
				if(isEdit.equalsIgnoreCase("edit")){
					isView = false;
					editorType = campaign.getEditorType();
					saveAsDraftBtnId.setVisible(false);
					htmlStuff = campaign.getHtmlText();
					nextBtnId.setLabel("Save & Close");
					if(campaign.getEditorType().equalsIgnoreCase("blockEditor")){
						if(isBack == null){
							nextBtnId.setVisible(false);
							if(saveNewTBarBtnId != null)
								saveNewTBarBtnId.setVisible(false);
							if(saveEditBtnId != null)
								saveEditBtnId.setVisible(true);
						}
						html.setId("htmlId");
						if(htmlStuff == null) {
							logger.debug("html content is null from the session");
							return;
						}
						html.setContent (EditorController.createEditorTags(htmlStuff));
						html.setParent(appCenterDivId);
						// TODO: enable following lines for add block with default content. 
						/*categoryName = campaign.getTemplate().getTemplateCategory().getDirName();
						getBlocks(campaign.getTemplate().getName());*/
					}
				}if(isEdit.equalsIgnoreCase("view")){
					isView = true;
				}
			}
			
			
			if(isView){
				if(logger.isDebugEnabled())logger.debug("editorType ---:"+editorType);
				
				if(editorType != null){
					if(editorType.equalsIgnoreCase("blockEditor")){
						logger.info("**********");
						
						
						if(campaign.getHtmlText() != null && 
								campaign.getEditorType().equalsIgnoreCase("blockEditor")) {
							htmlStuff = campaign.getHtmlText();
							html.setContent(EditorController.createEditorTags(campaign.getHtmlText()));
						}
						else{
						
							selectedTemplate = (String)sessionScope.getAttribute("selectedTemplate");
							if(logger.isDebugEnabled())logger.debug(" selectedTemplate :"+selectedTemplate);
							String[] temp = StringUtils.split(selectedTemplate, '/');
							
								categoryName = temp[0];
								templateName = temp[1];
								if(logger.isDebugEnabled())logger.debug("----templateName ---:"+
										templateName + "----categoryName ---:"+categoryName);
								
								if(categoryName.equalsIgnoreCase("MyTemplates")) {
									MyTemplates myTemplate = myTemplatesDao.
									findByUserNameAndTemplateName(campaign.getUsers().getUserId(), temp[2], templateName);
									logger.debug("myTemplate :"+myTemplate);
									htmlStuff = myTemplate.getContent();
									html.setContent(EditorController.createEditorTags(htmlStuff));
								}
								else  {
									
									logger.debug("SystemTempalates");
									
									st = systemTemplatesDao.findByNameAndCategory(templateName,categoryName);
									getBlocks(templateName);
									logger.info("templatetype---**"+templateName);
									htmlStuff =st.getHtmlText();
									html.setContent (EditorController.createEditorTags(htmlStuff));
								}
														
							
						}
						html.setId("htmlId");
						//if(logger.isDebugEnabled())logger.debug(htmlStuff);
						html.setParent(appCenterDivId);
					}else{
						st = systemTemplatesDao.findByName(editorType);
						if(logger.isDebugEnabled())logger.debug(" st :"+st.getName());
						
						htmlStuff =st.getHtmlText();
						html.setId("htmlId");
						html.setParent(appCenterDivId);
						
						logger.debug("editorType :"+editorType);
						
					}
					
				}
				else if(editorType == null){
					MessageUtil.setMessage("Choose one editor.", "color:red", "TOP");
				}
				
			}
		}catch(Exception e){
			logger.error(" Exception :", (Throwable)e);
		}
		
		//setting for this attribute to enabling tinyMce type of button in imageLibrary.zul
		sessionScope.setAttribute("EditorType","TinyMCEEditor");
	}
	
	//public void saveEmail(String htmlStuff){ 
	/*public void onChange$htmlStuffId(){ 
		try{
			if(logger.isDebugEnabled())logger.debug("-- just entered --");
			String htmlStuff = htmlStuffId.getValue();
			htmlPreviewStuff = htmlStuffId.getValue();
			super.saveEmail(htmlStuff, st, null, null, true);
			htmlStuffId.setValue("");
			sessionScope.setAttribute("editCampaign","view");
		}catch (Exception e) {
			logger.error("** Exception :"+ e +" **");
		}

		// setting for this attribute to enabling tinyMce type of button in
		// imageLibrary.zul
		sessionScope.setAttribute("EditorType", "TinyMCEEditor");
	}*/

	// public void autoSave(String htmlStuff){
	public void onChange$htmlStuffAId() {
		TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
		String htmlStuff = htmlStuffAId.getValue();
		htmlPreviewStuff = htmlStuffAId.getValue();
		super.saveEmail(htmlStuff, null , null, null, false);
		htmlStuffAId.setValue("");
		sessionScope.setAttribute("editCampaign", "view");
		autoSaveLbId.setValue("Last auto-saved at: "+ MyCalendar.calendarToString(Calendar.getInstance(),
				MyCalendar.FORMAT_SCHEDULE_TIME,tz));
	}

	// public void saveEmail(String htmlStuff){
	public void onChange$htmlStuffId() {
		if (logger.isDebugEnabled())
			logger.debug("-- just entered --");
		String htmlStuff = htmlStuffId.getValue();
		htmlPreviewStuff = htmlStuffId.getValue();
		super.saveEmail(htmlStuff, st, null, null, true);
		htmlStuffId.setValue("");
		sessionScope.setAttribute("editCampaign", "view");
	}
	
	//public void saveInMyTemplates(Window winId,String name,Textbox htmlStuffId3){
	private Textbox winId$templatNameTbId;
	public void onChange$htmlStuffId3$winId() throws Exception {
			if(logger.isDebugEnabled())logger.debug("-- just entered --");
			//winId$templatNameTbId.setValue("");
			Textbox templatNameTbId =  (Textbox) winId.getFellowIfAny("templatNameTbId"); 
			Textbox htmlStuffId3 = (Textbox) winId.getFellowIfAny("htmlStuffId3");
			String htmlStuff = htmlStuffId3.getValue();
			//getMyTemplatesFromDb(currentUser.getUserId()); -Changes App-current patch
			super.saveInMyTemplates(winId,templatNameTbId.getValue(),htmlStuff,editorType,winId$myTemplatesListId);
			htmlStuffId3.setValue("");
			/*templatNameTbId.setValue("");*/
			
		
	}
	
	// public void gotoPlainMsg(String htmlStuff){
	public void onChange$htmlStuffId2() throws Exception {
		String htmlStuff = htmlStuffId2.getValue();
		String isValidPhStr = null;
		isValidPhStr = Utility.validatePh(htmlStuff, currentUser);
		
		if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
			
			MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
			return ;
		}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
			
			MessageUtil.setMessage(" You have currently disabled subscriber preference center setting.\n To continue ,either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
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
		
		super.gotoPlainMsg(htmlStuff, st);
	}
	
	
	//public void saveAsDraft(String htmlStuff){ 
	public void onChange$htmlStuffId1() throws Exception {
		String htmlStuff = htmlStuffId1.getValue();
		super.saveAsDraft(htmlStuff, st);
	}
	
	
	@SuppressWarnings("unchecked")
	private void getBlocks(String templateName){
		try{
			SystemTemplatesDao systemTemplatesDao = (SystemTemplatesDao)SpringUtil.getBean("systemTemplatesDao");
			//Menupopup editPopup = new Menupopup();//(Menupopup)Utility.getComponentById("editPopup");
			
			if(logger.isDebugEnabled())logger.debug("Getting the available blocks list for the layout :"+templateName);
			if(logger.isDebugEnabled())logger.debug("Getting the available blocks list for the layout :"+categoryName);
			List blocksList = systemTemplatesDao.findDivisions(templateName,categoryName);
			if(logger.isDebugEnabled())logger.debug("blockList size :"+blocksList.size());
			String blocks = "";
			String[] blocksArray;
			if(blocksList.size()>0){
				blocks = (String)blocksList.get(0);
				if(logger.isDebugEnabled())logger.debug("availableBlocks from Database as a single linen :"+blocks);
				blocksArray = StringUtils.split(blocks,",");
				for(String blockString:blocksArray){
			        String id =blockString+"DivId";
			        String blck=blockString+"Block";
			        
			        Menuitem mi = new Menuitem();
			        mi.setWidgetListener("onClick", "addNewBlock('"+id+"',"+blck+")");
/*			        String action= "onclick:addNewBlock('"+id+"',"+blck+")";
			        mi.setAction(action);
*/			        mi.setLabel(blockString);
			        mi.setParent(editPopup);
				}
				if(blocksArray.length != 0){
					addBlockBtnId.setVisible(true);
					addBlockBtnId.setPopup(editPopup);
				}
			}else{
				if(logger.isDebugEnabled())logger.debug(" -- 1 --");
				addBlockBtnId.setVisible(false);
			}
		}catch (Exception e) {
			logger.error(" Error while getting the blocks for the selected Template :",e);
		}
	}
	
	//public void reload(){
	public void onClick$reloadTlbBtnId() throws Exception {
		try{
			int confirm = Messagebox.show("Do you want to reload the email?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm != 1){
				return;
			}else{
				Components.removeAllChildren(appCenterDivId);
				html.setId("htmlId");
				html.setContent (htmlStuff);
				Div myDiv = new Div();
				myDiv.setId("HTMLDivId");
				html.setParent (myDiv);
				myDiv.setParent(appCenterDivId);
			}
		}catch (Exception e) {
			logger.error("** Exception : " + e + "**");
		}
	}

	private  Iframe previewIframeWin$iframeId;
	private Window previewIframeWin; 
	public void onClick$previewTBarBtnId() throws Exception {
		try{
			
			if(htmlPreviewStuff != null){
				
				Utility.showPreview(previewIframeWin$iframeId,currentUser.getUserName(), htmlPreviewStuff);
				previewIframeWin.setVisible(true);
			}
		}catch(Exception e){
			logger.error("Exception ::" , e);;
		}
		
	}
	
	
	
	
	//public void sendTestMail(Textbox testMailTbId) throws Exception {
	/*public void onChange$testMailTbId() throws Exception {
		////////logger.debug("Entered onChange$testMailTbId method........");
		 String emailId=testEmailTbId.getValue();
		 if( emailId.equals("Email Address...") || emailId.isEmpty() ){
				MessageUtil.setMessage("There is no mail id to send a test mail.", "color:red");
				testEmailTbId.setValue("Email Address...");
				testEmailTbId.focus();
			}
		else if(super.sendTestMail(testMailTbId.getValue(), testEmailTbId.getValue())) {
		testEmailTbId.setValue("Email Address...");
			}
	}*/
	
	public void onBlur$testEmailTbId() throws Exception {
		 
		 String mail=testEmailTbId.getValue();
		 ////////logger.debug("here in on blur method mail id "+mail);
		 if(mail.equals("Email Address...") || mail.equals("")){
			 testEmailTbId.setValue("Email Address...");
			 
		 }
	 }
	
	public void onClick$sendBtnId() throws Exception {
		
		logger.debug("in onclick.... send ");
		//////logger.debug("inside onclick");
		String emailId=testEmailTbId.getValue();
		if( emailId.equals("Email Address...") || emailId.isEmpty() ){
			MessageUtil.setMessage("There is no mail id to send a test mail.", "color:red");
			testEmailTbId.setValue("Email Address...");
			testEmailTbId.focus();
			return;
		}
		else if(super.sendTestMail(testMailTbId.getValue(), testEmailTbId.getValue())) {
			
			testEmailTbId.setValue("Email Address...");
		}
	}
	public void onFocus$testEmailTbId() throws Exception {
	 	String mail=testEmailTbId.getValue(); 
		if(mail.equals("Email Address...") || mail.equals("")){
			 testEmailTbId.setValue("");
			 
		 } 
	 }
	
	// public void checkSpam(Textbox spamChekTbId){
	public void onChange$spamChekTbId() throws Exception {
		super.checkSpam(spamChekTbId.getValue(), true);
		spamChekTbId.setValue("");
	}
	
	public void onClick$backBtId() throws Exception {
		super.back();
	}
	
	public void onChange$cboxId() throws Exception {
		 Clients.evalJavaScript("parent.changeBgColor('"+cboxId.getColor()+"');");
	}
	
	Div launchEditorDivId;
//	private Window winId;
	//htmlStuffId3,resLbId
	private Textbox winId$htmlStuffId3;
	private Label winId$resLbId;
	public void onClick$saveInMyTemplateTbarId() {
		logger.debug("---save in my template toolbar btn clicked----");
		winId$htmlStuffId3.setValue("");
		winId$resLbId.setValue("");
		launchEditorDivId.setVisible(true);
		winId.setVisible(true);
		winId.setPosition("center");
		winId$templatNameTbId.setValue("");
		winId.doHighlighted();
		getMyTemplatesFromDb(currentUser.getUserId());
		
		
	}
	/*private Div launchEditorDivId;
	 * 
	 * 
	public void onClick$saveInMyTemplateTbarId() {
		winId.setVisible(true);
		winId.setPosition("center");
		winId.doHighlighted();
		launchEditorDivId.setVisible(launchEditorDivId.isVisible());
	} //onClick$saveInMyTemplateTbarId
*/
	private Listbox winId$myTemplatesListId; 
	public void getMyTemplatesFromDb(Long userId){
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

}
}
