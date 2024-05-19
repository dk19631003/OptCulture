package org.mq.marketer.campaign.controller.layout;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.ApplicationProperties;
import org.mq.marketer.campaign.beans.CampaignSchedule;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.EmailContent;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.SystemTemplates;
import org.mq.marketer.campaign.beans.UserDesignedCustomRows;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.PrepareFinalHtml;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CampaignScheduleDao;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.CampaignsDaoForDML;
import org.mq.marketer.campaign.dao.EmailContentDao;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.MyTemplatesDaoForDML;
import org.mq.marketer.campaign.dao.SystemTemplatesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.dao.UserDesignedCustomRowsDao;
import org.mq.marketer.campaign.dao.UserDesignedCustomRowsDaoForDML;
import org.mq.marketer.campaign.general.CampaignStepsEnum;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.HTMLUtility;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.marketer.campaign.general.ZipImport;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataIntegrityViolationException;
import org.zkoss.util.media.Media;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.google.gson.JsonObject;

import bsh.ParseException;


@SuppressWarnings("serial")
public class BeeController  extends EditorController {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	Textbox emailNameTbId,testWinId$testTbId;
	Textbox testEmailTbId, autoSaveTxtId;
	Html html = new Html();
	Toolbarbutton addBlockBtnId;
	String userName, beeclientkey;
	String usersParentDirectory;
	String htmlFilePath;
	Window testWinId;
	Label testWinId$msgLblId;
	Div toolhide;
	private SystemTemplates st;
	private Textbox contentNameTBId,zipImport$urlTbId;
	private Listbox emailContentsLBId;
	private String source;
	private CampaignSchedule campSchedule;
	private EmailContent emailContent;
	private CampaignScheduleDao campaignScheduleDao;
	private MyTemplatesDao myTemplatesDao;
	private Button saveAsDraftBtnId,nextBtnId;
	//	private Listbox phLbId;
	private Window winId,zipImport;
	private Label emlContentLblId,autoSaveLbId;
	private boolean isAdmin;
	private Timer autoSaveTimerId;
	private Textbox htmlTextBoxId;
	private Textbox jsonTextBoxId;
	private Textbox caretPosSE, jsonAutoSaveTextBoxId,nextButtonJsonTextBoxId,autoSaveJsonTextBoxId,sendMethodHtmlTextBoxId,errormsg;
	private String beeHtmlContent,beeJsonContent,beeHTMLTestMailContent,beeAutosaveJson;
	private boolean next, dnext,isSaveAsTemplate,isSendTestMail,isAutoSave;
	private Window customRowId;
	private Textbox customRowId$rowTextId;
	private Combobox customRowId$comboCategoryRowId;
	
	private Button testWinId$sendTestMailBtnId;
	private UserDesignedCustomRowsDaoForDML userDesignedCustomRowsDaoForDML;
	private UserDesignedCustomRowsDao userDesignedCustomRowsDao;
	private String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
	static final String APP_MAIN_URL = "https://app.optculture.com/subscriber/";
	static final String APP_MAIN_URL_HTTP = "http://app.optculture.com/subscriber/";
	static final String MAILCONTENT_URL = "http://mailcontent.info/subscriber/";
	static final String MAILHANDLER_URL = "http://mailhandler01.info/subscriber/";
	private static final String ImageServer_Url = PropertyUtil.getPropertyValue("ImageServerUrl");
	static final String LINK_PATTERN = PropertyUtil.getPropertyValue("LinkPattern");
	//private TimeZone clientTimeZone;

	//private Include beeScriptIncId;

	public BeeController() {
		//PageUtil.setHeader("Create Email (Step 4 of 6)", "", "", true);

		userName = GetUser.getUserName();
		usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");

		this.myTemplatesDao = (MyTemplatesDao)
				SpringUtil.getBean("myTemplatesDao");
		this.campaignScheduleDao = (CampaignScheduleDao)
				SpringUtil.getBean("campaignScheduleDao");

		HttpServletRequest request = (HttpServletRequest)
				Executions.getCurrent().getNativeRequest();
		source = (String)request.getAttribute("source");
		//logger.debug("source is=================="+source);
		campSchedule = (CampaignSchedule)request.getAttribute("campSchedule");
		this.userDesignedCustomRowsDaoForDML = (UserDesignedCustomRowsDaoForDML) SpringUtil.getBean("userDesignedCustomRowsDaoForDML");
		this.userDesignedCustomRowsDao = (UserDesignedCustomRowsDao) SpringUtil.getBean("userDesignedCustomRowsDao");

		if( source == null && isEdit != null && campaign == null ) {
			//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW);
			Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
		}
		if(source != null && !source.equalsIgnoreCase("Schedule") || (source == null && isEdit == null ) || (source == null && isEdit != null)) {
			Utility.breadCrumbFrom(4);
		}

		UserActivitiesDao userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		UserActivitiesDaoForDML userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		/* if(userActivitiesDao != null) {
	      	userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_PLAIN_EDITOR,GetUser.getUserObj());
		  }*/
		if(userActivitiesDaoForDML != null) {
			userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_HTML_BEE_EDITOR,GetUser.getLoginUserObj());
		} 
		sessionScope = Sessions.getCurrent();
		isAdmin = (Boolean)sessionScope.getAttribute("isAdmin");
		String style = "font-weight:bold;font-size:15px;color:#313031;" +"font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Create Email (Step 4 of 6)","",style,true);
	}


	/* public void init(Textbox testEmailTbId, Button saveAsDraftBtnId, 
				Button nextBtnId,  CKeditor fckEditor, Listbox phLbId,
				Textbox contentNameTBId, Listbox emailContentsLBId) { */

	@Override
	public void doAfterCompose(Component comp) throws Exception {

		try {
			super.doAfterCompose(comp);
			prepareCustomRowUrlForCampaign();
			//beeclientkey = PropertyUtil.getPropertyValueFromDB(Constants.BEE_CLIENT_KEYVALUE);
			beeclientkey = PropertyUtil.getPropertyValueFromDB(Constants.BEE_CLIENT_KEYVALUE_DR);
			String htmlStuff = "";
			String jsonStuff = "";

			if(logger.isDebugEnabled())logger.debug("-- just entered --"+campaign+"===="+currentUser);

			if(campaign == null || currentUser == null){
				Redirect.goTo(PageListEnum.RM_HOME);
				return;
			}

			/*	this.testEmailTbId= testEmailTbId;
			this.fckEditorid = fckEditor;
			this.contentNameTBId = contentNameTBId;
			this.emailContentsLBId = emailContentsLBId;*/

			if(logger.isDebugEnabled())logger.debug(" isEdit :"+isEdit);
			String selectedTemplate = (String)sessionScope.getAttribute("selectedTemplate");
			// if the editor is loaded with CampaignSchedule edit mode
			if(source != null && source.equalsIgnoreCase("schedule")) {

				/**** The following block is for getting the content of CampaignSchedule****/
				if(logger.isDebugEnabled()) {
					logger.debug(">>>>>>> CampaignSchedule Id :"+campSchedule.getCsId());
				}

				// get the email content of the campaign schedule object
				emailContent = campSchedule.getEmailContent();

				htmlStuff = getHtmlStuffFromSourceTypeIsSchedule();
				/*if(emailContent != null) {

					// get the content if CampaignSchedule's emailcontent is not null
					htmlStuff = emailContent.getHtmlContent();
					contentNameTBId.setValue(emailContent.getName());
				}
				else {

					// if content of CampaignSchedule is null try to get its parent content
					Long parentId = campSchedule.getParentId();
					CampaignSchedule parentCS = campaignScheduleDao.findById(parentId);

					if(parentCS != null && parentCS.getEmailContent() != null ) {
						htmlStuff =  parentCS.getEmailContent().getHtmlContent();
						contentNameTBId.setValue("");
					}
					// if parent's content also null get the content from campaign
					else {
						htmlStuff = campaign.getHtmlText();
					}
				}*/
				/** end of the content getting block**/

				/** 
				 * The following block is to get email content objects of this campaign
				 *  and assign the objects to the listbox
				 * 
				 * ***/ 

				EmailContentDao emailContentDao = (EmailContentDao)SpringUtil.getBean("emailContentDao");
				List<EmailContent> contentList = emailContentDao.getByCampaignId(campaign.getCampaignId());

				if(contentList != null && contentList.size() > 0 ) {

					Listitem li;
					for (EmailContent emailContent : contentList) {

						li = new Listitem(emailContent.getName());
						li.setValue(emailContent);
						li.setParent(emailContentsLBId);
					}//foreach EmailContent

				}// if contentList 
				/** end of the email content objects getting block ***/
				emlContentLblId.setVisible(true);
				contentNameTBId.setVisible(true);

			}
			// if the editor is loaded with campaign content edit mode
			else if(isEdit != null && isEdit.equalsIgnoreCase("edit") && (selectedTemplate== null || selectedTemplate.isEmpty())) {

				//editorType = campaign.getEditorType();
				logger.debug("##################  in Edit mode: "+editorType);
				saveAsDraftBtnId.setVisible(false);
				nextBtnId.setLabel("Save & Close");
				jsonStuff = campaign.getJsonContent();
				//TODO default json to be set at this place
				if(jsonStuff==null) {
					jsonStuff = "";
				}
				//JSONObject jsontemplate = (JSONObject)JSONValue.parse(campaign.getJsonContent());	
				JSONObject jsontemplate = (JSONObject)JSONValue.parse(jsonStuff);
				Clients.evalJavaScript("mytemplate ="+jsontemplate+";");
				//Clients.evalJavaScript("var jsonstuff ="+jsonStuff+";");
				//Clients.evalJavaScript("beePluginInstance.load(template);");
			}
			else {
				logger.debug(isEdit+"=isEdit  ################## and  Editor type= "+editorType);

				logger.debug("selectedTemplate:"+selectedTemplate);
				if(editorType.equalsIgnoreCase("beeEditor") && selectedTemplate != null && selectedTemplate.contains("/")) {

					String[] temp = StringUtils.split(selectedTemplate, '/');
					String categoryName = temp[0];
					String templateName = temp[1];
					if(logger.isDebugEnabled())logger.debug("----templateName ---:"+
							templateName + "----categoryName ---:"+categoryName);

					String isRetain = (String)sessionScope.getAttribute("retainChanges");

					logger.debug("RETAIN"+isRetain);

					if(categoryName.equalsIgnoreCase("MyTemplates") && isRetain == null ) {
					//	MyTemplates myTemplate = myTemplatesDao.findByUserNameAndTemplateName(campaign.getUsers().getUserId(), temp[2],templateName);
						MyTemplates myTemplate = myTemplatesDao.findByUserNameAndTemplateName(campaign.getUsers().getUserId(), temp[2], templateName, Constants.MYTEMPATES_NEW_EDITOR_PARENT);
						logger.debug("myTemplate :"+myTemplate);
						//1111htmlStuff = myTemplate.getContent();
						jsonStuff = myTemplate.getJsoncontent();
						JSONObject jsontemplate = (JSONObject)JSONValue.parse(jsonStuff);
						//logger.info("MyTemplates jsontemplate...:"+jsontemplate);
						Clients.evalJavaScript("mytemplate ="+jsontemplate+";");
						
						if(campaign!=null){
							CampaignsDaoForDML campaignsDaoForDML = (CampaignsDaoForDML)SpringUtil.getBean("campaignsDaoForDML");
							campaign.setTextMessage(HTMLUtility.getTextFromHtml(myTemplate.getContent()));
							campaign.setHtmlText(myTemplate.getContent());
							campaign.setJsonContent(jsonStuff);
							campaign.setModifiedDate(Calendar.getInstance());
							campaignsDaoForDML.saveOrUpdate(campaign);
						}
					}
					else if(isRetain != null && isRetain.equals("retain")){
						logger.debug(">>>>>>>>>>>>> Loading content from Campaign1...");
						jsonStuff = campaign.getJsonContent();
						JSONObject jsontemplate = (JSONObject)JSONValue.parse(jsonStuff);
						Clients.evalJavaScript("mytemplate ="+jsontemplate+";");
					}
					else {
						logger.debug("SystemTempalates");
						SystemTemplatesDao systemTemplatesDao = (SystemTemplatesDao)SpringUtil.getBean("systemTemplatesDao");
						st = systemTemplatesDao.findByNameAndCategory(templateName,categoryName);
						/*getBlocks(templateName);
						logger.info("templatetype---**"+templateName);
						htmlStuff =st.getHtmlText();
						html.setContent (EditorController.createEditorTags(htmlStuff));*/

						jsonStuff = st.getJsonText();
						JSONObject jsontemplate = (JSONObject)JSONValue.parse(jsonStuff);
						//logger.info("MyTemplates jsontemplate...:"+jsontemplate);
						Clients.evalJavaScript("mytemplate ="+jsontemplate+";");
						if(campaign!=null){
							CampaignsDaoForDML campaignsDaoForDML = (CampaignsDaoForDML)SpringUtil.getBean("campaignsDaoForDML");
							campaign.setTextMessage(HTMLUtility.getTextFromHtml(st.getHtmlText()));
							campaign.setHtmlText(st.getHtmlText());
							campaign.setJsonContent(jsonStuff);
							campaign.setModifiedDate(Calendar.getInstance());
							campaignsDaoForDML.saveOrUpdate(campaign);
						}
					}
					/*else if(isRetain != null && isRetain.equals("retain")){
						logger.debug(">>>>>>>>>>>>> Loading content from Campaign1...");
						jsonStuff = campaign.getJsonContent()!=null?campaign.getJsonContent():"";
					}else{
						logger.debug(">>>>>>>>>>>>> Loading content from Campaign2...");
						jsonStuff = campaign.getJsonContent()!=null?campaign.getJsonContent():"";
					}*/
				}else if(editorType.equalsIgnoreCase("beeEditor") &&  selectedTemplate == null){

					if(campaign.getJsonContent()==null){

						//jsonStuff = campaign.getJsonContent()!=null?campaign.getJsonContent():"";

						//logger.info("jsonStuff..........."+jsonStuff);
						//JSONObject jsontemplate = (JSONObject)JSONValue.parse(jsonStuff);
						//Clients.evalJavaScript("mytemplate ="+jsontemplate+";");
						//Clients.evalJavaScript("beePluginInstance.start("+jsontemplate+");");

						//9_june_venkata	ApplicationPropertiesDao appPropDao = (ApplicationPropertiesDao)SpringUtil.getBean("applicationPropertiesDao");

						// call it from constants file Default_JSON
						//9_june_venakata	ApplicationProperties appProp= appPropDao.findByPropertyKey(Constants.DEFAULT_JSON);

						//logger.info("First time from Scrtacth...:"+appProp.getValue());
						JSONObject jsontemplate = (JSONObject)JSONValue.parse(Constants.DEFAULT_JSON_VALUE);
						//logger.info("Second time from Scrtacth...:"+jsontemplate);
						Clients.evalJavaScript("mytemplate ="+jsontemplate+";");

					}
					else {
						JSONObject jsontemplate = (JSONObject)JSONValue.parse(campaign.getJsonContent());
						Clients.evalJavaScript("mytemplate ="+jsontemplate+";");
						//logger.info(jsontemplate);
					}

				}

			}

			//111111logger.info("t1 :"+ fckEditorId);
			//logger.info("t2 :"+ htmlStuff);

			//111111fckEditorId.setValue(htmlStuff);
			Clients.evalJavaScript("beekey ='"+beeclientkey+"';");
			sessionScope.removeAttribute("retainChanges");


			//			List<String> placeHoldersList = getPlaceHolderList(campaign.getMailingLists());
			EditorController.getPlaceHolderList(campaign.getMailingLists());
			EditorController.getCouponsList();
			//EditorController.getMilestones();
			//phLbId.setItemRenderer(new phListRenderer());
			sessionScope.setAttribute("EditorType","beeEditor");
			//phLbId.setModel(new SimpleListModel(placeHoldersList));


			/*String retToken = getToken();
			Clients.evalJavaScript("alert('hi');");
			Clients.evalJavaScript("var token79 ="+retToken+";");
			Clients.evalJavaScript("function(token);");
			 */			
			//beeScriptIncId.setSrc("/zul/campaign/Beetoolbar.zul");
			//include="/Webcontect/campaign/Beetoolbar.zul";
			//setting for this attribute to enabling ckEditor type of button in imageLibrary.zul

			//Clients.evalJavaScript("initialcall();");

		}catch(Exception e) {
			logger.error(" Exception : ", e );
		}
	}

	/*	public void onChange$caretPosTB() {

		//System.out.println("dummy event Htmlllllllllllllllllllllll............."+caretPosTB.getValue());
		//String htmlStuff = caretPosTB.getValue();
		//save(htmlStuff);
		beeHtmlContent = caretPosTB.getValue();
		//gotoPlainMsg(htmlStuff, null);
		logger.info("onChange$caretPosTB");
		saveJSONHTMLContent();
	}*/
	/*public void onChange$nextButtonJsonTextBoxId() {
		logger.info("onchange$jsonTextBoxId");
		saveJSONHTMLContent();
	}*/
	
	public void onChange$errormsg() {
		//logger.info("hi i am printing");
		errormsg.setValue("");
		MessageUtil.setMessage("You have encountered an error viewing this page, please refresh to try again. \n If you continue having viewing difficulties, please submit a ticket to support@optculture.com", "color:blue");
	}
	
	//public void onChange$jsonTextBoxId() {
	public void onCustomEvent$jsonData(ForwardEvent event) throws JSONException{
		Object o1 = JSONValue.parse(event.getOrigin().getData().toString());
		logger.info("onchange$jsonTextBoxId");
		if(o1!= null) {
			  JSONObject jsonObj = (JSONObject) o1;
			  String json = (String) jsonObj.get("json");
			  String html = (String) jsonObj.get("html");
			  if(json==null||!(json.contains(Constants.DEFAULT_JSON_CHECK_TEXTBOX)||json.contains(Constants.DEFAULT_JSON_CHECK_IMAGE)||json.contains(Constants.DEFAULT_JSON_CHECK_MERGE_CONTENT)||json.contains(Constants.DEFAULT_JSON_CHECK_HTML)||json.contains(Constants.DEFAULT_JSON_CHECK_BUTTON)||json.contains(Constants.DEFAULT_JSON_CHECK_SOCIAL)||json.contains(Constants.DEFAULT_JSON_CHECK_DIVIDER))){
				  if(html == null) {
					  logger.info("auto save Empty");
				  }else {
					  if(!isAutoSave) {
						  Messagebox.show("Please use 'Text block' to save the template.", "Error", Messagebox.OK, Messagebox.ERROR); //App-3458
						  testWinId.setVisible(false);
						  testWinId$sendTestMailBtnId.setDisabled(false);
					  }
					  isAutoSave = false;
					  return;
				  }
			  }
			  
			  	beeHtmlContent = html;
			    String[] beeHtmlContentParts = beeHtmlContent.split("<a");
			    if(beeHtmlContent!=null&!beeHtmlContent.equalsIgnoreCase("EMPTY_HTML")){
					for(int i=0;i<=beeHtmlContentParts.length-1;i++){
						String anchorTagStr="<a"+beeHtmlContentParts[i];
						String promocodeLink="<a"+StringUtils.substringBetween(anchorTagStr, "<a", "</a>")+"</a>";
						if(promocodeLink.contains("BEEFREEPROMOCODE_START")&&promocodeLink.contains("BEEFREEPROMOCODE_END")){
							String promocodeValue=StringUtils.substringBetween(beeHtmlContent, "BEEFREEPROMOCODE_START", "BEEFREEPROMOCODE_END");
							beeHtmlContent=beeHtmlContent.replace(promocodeLink, promocodeValue);
						}
						logger.info("----------.....................------------");
					}
					beeHtmlContent= beeHtmlContent.replace("&lt;img", "<img");
					beeHtmlContent= beeHtmlContent.replace("/&gt;", "/>");
					beeHtmlContent=Utility.replaceTextBarcodeHeightWidth(beeHtmlContent);
				}
			  
			  if(json!= null && !json.isEmpty()) {
				  saveJSONHTMLContent(json,beeHtmlContent);
			  }
			/*
			 * if(isSendTestMail && !isAutoSave){ String emailId =
			 * testWinId$testTbId.getValue(); testWinId$msgLblId.setValue("");
			 * super.sendTestMail(beeHtmlContent, emailId); testWinId.setVisible(false);
			 * testWinId$sendTestMailBtnId.setDisabled(false); } isSendTestMail=false;
			 */
		  }
	}
	public void saveJSONHTMLContent(String json, String html){
		//System.out.println("dummy event Jsonnnnnnnnnnnnnnnnnnn.....JSON........"+caretPosJB.getValue());
		beeJsonContent = json ;//jsonTextBoxId.getValue();
		beeHtmlContent = html; //htmlTextBoxId.getValue();
		htmlTextBoxId.setValue("EMPTY_HTML");
		jsonTextBoxId.setValue("EMPTY_JSON");
		logger.info("jsoncontent in save ....................."+beeJsonContent.indexOf("\uFEFF"));
		
		//logger.info("jsoncontent in save....................."+beeJsonContent);
		
		beeJsonContent=beeJsonContent.replace("\uFEFF","&nbsp;");
		
		//logger.info("jsoncontent in after replacement....................."+beeJsonContent);		
		
		//SpecialLinks AnchorTag replace with Promocode Value

		//logger.info("Before replace Promocode"+beeHtmlContent);
		//String[] beeHtmlContentParts = beeHtmlContent.split("<a");
		//boolean isContentEmpty=true;
		//System.out.println("beeJsonContent.................:"+beeJsonContent);
		//System.out.println("Constants.DEFAULT_JSON_VALUE2..:"+Constants.DEFAULT_JSON_VALUE2);
		if(beeJsonContent==null||!(beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_TEXTBOX)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_IMAGE)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_MERGE_CONTENT)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_HTML)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_BUTTON)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_SOCIAL)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_DIVIDER)|| beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_VIDEO))){    
			if(next||dnext||isSaveAsTemplate||isSendTestMail){
				next=false;
				dnext=false;
				isSaveAsTemplate=false;
				isSendTestMail=false;
				MessageUtil.setMessage("Email content cannot be empty", "color:red");
				zipImport.setVisible(false);
				testWinId.setVisible(false);
				return;
			}else{
				MessageUtil.setMessage("Email content cannot be empty", "color:red");
				return;
			}
		}

		/*if(beeHtmlContent!=null&!beeHtmlContent.equalsIgnoreCase("EMPTY_HTML")){
			for(int i=0;i<=beeHtmlContentParts.length-1;i++){
				String anchorTagStr="<a"+beeHtmlContentParts[i];
				//System.out.println("anchorTagStr.."+anchorTagStr);
				String promocodeLink="<a"+StringUtils.substringBetween(anchorTagStr, "<a", "</a>")+"</a>";
				//System.out.println("promocodeLink.."+promocodeLink);
				if(promocodeLink.contains("BEEFREEPROMOCODE_START")&&promocodeLink.contains("BEEFREEPROMOCODE_END")){
					String promocodeValue=StringUtils.substringBetween(beeHtmlContent, "BEEFREEPROMOCODE_START", "BEEFREEPROMOCODE_END");
					//System.out.println("promocodeValue.."+promocodeValue);
					beeHtmlContent=beeHtmlContent.replace(promocodeLink, promocodeValue);
					//logger.info("Html after Replace.."+beeHtmlContent);
				}
				logger.info("----------.....................------------");
			}
			//logger.info("After replace Promocode and before replacing barcode"+beeHtmlContent);
			beeHtmlContent= beeHtmlContent.replace("&lt;img", "<img");
			beeHtmlContent= beeHtmlContent.replace("/&gt;", "/>");
			//logger.info("After replace Barcode &lt;img and /&gt;"+beeHtmlContent);
			beeHtmlContent=Utility.replaceTextBarcodeHeightWidth(beeHtmlContent);
		} */
		
		if(beeHtmlContent!=null&!beeHtmlContent.equalsIgnoreCase("EMPTY_HTML")){
			if(campaign.isCustomizeFooter() && !isAutoSave) {
				if(!beeHtmlContent.contains(Constants.FOOTER_UNSUBSCRIBE_LINK)) {
					MessageUtil.setMessage("Looks like the Unsubscribe Link merge-tag is \n"
											 + "missing in your email content. \n"
											 + "Please note that \n"
											 + "default sender address will be added to \n"
											 + "email-footer at the time of sending to be \n"
											 + "compliant with email sending laws.", "color:blue");
				}
				if(currentUser.getSubscriptionEnable() == true && !beeHtmlContent.contains(Constants.FOOTER_UPDATE_PREFERENCE_LINK)) {
					MessageUtil.setMessage("Looks like you have enabled \n"
											 + "Subscriber Preference Center feature \n"
											 + "but the 'Subscriber Preference Link' \n"
											 + "merge-tag is missing in the email. \n "
											 + "Please add the merge-tag in the \n"
											 + "email footer.", "color:blue");
				}
			}
			beeHtmlContent= beeHtmlContent.replace("&lt;img", "<img");
			beeHtmlContent= beeHtmlContent.replace("/&gt;", "/>");
		} 
		
		
		
		/*if(beeJsonContent.equalsIgnoreCase(Constants.DEFAULT_JSON_VALUE)){ 
			MessageUtil.setMessage("Email content cannot be empty", "color:red");
			return;
		}*/
		if(!isSaveAsTemplate)sessionScope.removeAttribute("selectedTemplate");
		if(next){
			//MessageUtil.setMessage("gotoPlainMsg", "color:orange");
			int options = 0;
			Pattern r = Pattern.compile(LINK_PATTERN, Pattern.CASE_INSENSITIVE);
			Matcher m = r.matcher(beeHtmlContent);
		    String anchorUrl;
	        while (m.find()) {
				anchorUrl = m.group(2).trim();
				if(logger.isDebugEnabled()) logger.debug("Anchor Tag : " + anchorUrl);
				try{
		            if( anchorUrl.indexOf("sms") != -1) {
		            	options = options+1;
		            	logger.info("optins...," + options);
		            }
				}catch (Exception e) {
					logger.error("** Exception : Problem while encoding the URL " + e);
				}
	        }
	        if (options >= 1)
	        {
	        	Messagebox.show("SMS links will not work properly in Gmail & Outlook.", "Information" , 
						Messagebox.OK, Messagebox.INFORMATION);
	        }
			gotoPlainMsg(beeHtmlContent,beeJsonContent,null);
			next=false;
		}
		else if(dnext){
			//MessageUtil.setMessage("saveAsDraft", "color:brown");
			int options = 0;
			Pattern r = Pattern.compile(LINK_PATTERN, Pattern.CASE_INSENSITIVE);
			Matcher m = r.matcher(beeHtmlContent);
		    String anchorUrl;
	        while (m.find()) {
				anchorUrl = m.group(2).trim();
				if(logger.isDebugEnabled()) logger.debug("Anchor Tag : " + anchorUrl);
				try{
		            if( anchorUrl.indexOf("sms") != -1) {
		            	options = options+1;
		            	logger.info("optins...," + options);
		            }
				}catch (Exception e) {
					logger.error("** Exception : Problem while encoding the URL " + e);
				}
	        }
	        if (options >= 1)
	        {
	        	Messagebox.show("SMS links will not work properly in Gmail & Outlook.", "Information" , 
						Messagebox.OK, Messagebox.INFORMATION);
	        }
			dnext=false;
			isEdit = null;
			saveAsDraft(beeHtmlContent,beeJsonContent,null);
		}
		else if(isSaveAsTemplate){
			isSaveAsTemplate=false;
			long size =	Utility.validateHtmlSize(beeHtmlContent);
			if(size >100) {
				String msgcontent = OCConstants.HTML_VALIDATION;
				msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
				MessageUtil.setMessage(msgcontent, "color:Blue");
			}
			Textbox nameTbId = (Textbox)zipImport.getFellowIfAny("templatNameTbId");
			zipImport.setVisible(false);
			saveInMyTemplates(nameTbId.getValue(),beeHtmlContent,"beeEditor",zipImport$myTemplatesListId,beeJsonContent);
			
		}
		else if(isSendTestMail && !isAutoSave){
			testWinId$msgLblId.setValue("");
			String emailId = testWinId$testTbId.getText();
			super.sendTestMail(beeHtmlContent, emailId);
			testWinId.setVisible(false);
			testWinId$sendTestMailBtnId.setDisabled(false);
			isSendTestMail=false;
		}
		else if(isAutoSave){
			isAutoSave=false;
			//beeAutosave = jsonAutoSaveTextBoxId.getValue();
			//String autohtml = campaign.getHtmlText(); 
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
				}} /*else if (campaign.getStatus().equalsIgnoreCase("Draft") && campaign.getDraftStatus().equalsIgnoreCase("complete")) {
				if((isEdit == null || isEdit.equalsIgnoreCase("view")) && Utility.isSomethingWrong(campaign, CampaignStepsEnum.CampTextMsg.getPos())){
					autoSaveLbId.setValue("Failed to Auto save. Reason: This step is completed, perhaps from another tab/browser.");
					return;
				} }*/
			/*int campScheduleList = campaignScheduleDao.getActiveCountByCampaignId(campaign.getUsers().getUserId(),campaign.getCampaignId());
			if(!(campScheduleList == 0) ) {
				autoSaveLbId.setValue("Failed to Auto save. Reason: This step is completed, perhaps from another tab/browser.");
				return;
			}*/
			
			campaign = (Campaigns)sessionScope.getAttribute("campaign");
			campaign.setHtmlText(beeHtmlContent);
			campaign.setPrepared(false);
			campaign.setJsonContent(beeJsonContent);
			campaign.setModifiedDate(Calendar.getInstance());
			sessionScope.setAttribute("campaign", campaign);
			
			campaignsDaoForDML.saveOrUpdate(campaign);

			//saveEmail(beeHtmlContent,null, null, null, false,beeJsonContent);
			TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
			autoSaveLbId.setValue("Last auto-saved at: "+ (MyCalendar.calendarToString(Calendar.getInstance(),MyCalendar.FORMAT_SCHEDULE_TIME,tz)).substring(13));
	
		}
		else {
			//MessageUtil.setMessage("saveEmail", "color:blue");
			/*if(Utility.validateHtmlSize(beeHtmlContent)) {
				Messagebox.show("It looks like the content of your email is more " +
						"than the recommended size per the email-" + 
						"sending best practices. " + "To avoid email landing " + 
						"in recipient's spam folder, please redesign " 
									+"email to reduce content.", "Info", Messagebox.OK, Messagebox.INFORMATION);
			}*/
			int options = 0;
			Pattern r = Pattern.compile(LINK_PATTERN, Pattern.CASE_INSENSITIVE);
			Matcher m = r.matcher(beeHtmlContent);
		    String anchorUrl;
	        while (m.find()) {
				anchorUrl = m.group(2).trim();
				if(logger.isDebugEnabled()) logger.debug("Anchor Tag : " + anchorUrl);
				try{
		            if( anchorUrl.indexOf("sms") != -1) {
		            	options = options+1;
		            	logger.info("optins...," + options);
		            }
				}catch (Exception e) {
					logger.error("** Exception : Problem while encoding the URL " + e);
				}
	        }
	        if (options >= 1)
	        {
	        	Messagebox.show("SMS links will not work properly in Gmail & Outlook.", "Information" , 
						Messagebox.OK, Messagebox.INFORMATION);
	        }
			long size =	Utility.validateHtmlSize(beeHtmlContent);
			if(size >100) {
				String msgcontent = OCConstants.HTML_VALIDATION;
				msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
				MessageUtil.setMessage(msgcontent, "color:Blue");
			}
			saveEmail(beeHtmlContent,null, null, null, false,beeJsonContent);
		}

	}

	public void onChange$caretPosSE() {

		beeHTMLTestMailContent =  caretPosSE.getValue();
		caretPosSE.setValue("EMPTY_HTML");
		
		//SpecialLinks AnchorTag replace with Promocode Value

				//logger.info("Before replace Promocode"+beeHTMLTestMailContent);
				String[] beeHtmlContentParts = beeHTMLTestMailContent.split("<a");

				if(beeHTMLTestMailContent!=null&!beeHTMLTestMailContent.equalsIgnoreCase("EMPTY_HTML")){
					for(int i=0;i<=beeHtmlContentParts.length-1;i++){
						String anchorTagStr="<a"+beeHtmlContentParts[i];
						//System.out.println("anchorTagStr.."+anchorTagStr);
						String promocodeLink="<a"+StringUtils.substringBetween(anchorTagStr, "<a", "</a>")+"</a>";
						//System.out.println("promocodeLink.."+promocodeLink);
						if(promocodeLink.contains("BEEFREEPROMOCODE_START")&&promocodeLink.contains("BEEFREEPROMOCODE_END")){
							String promocodeValue=StringUtils.substringBetween(beeHTMLTestMailContent, "BEEFREEPROMOCODE_START", "BEEFREEPROMOCODE_END");
							//System.out.println("promocodeValue.."+promocodeValue);
							beeHTMLTestMailContent=beeHTMLTestMailContent.replace(promocodeLink, promocodeValue);
							//logger.info("Html after Replace.."+beeHTMLTestMailContent);
						}
						//logger.info("----------.....................------------");
					}
					//logger.info("After replace Promocode and before replacing barcode"+beeHTMLTestMailContent);
					beeHTMLTestMailContent= beeHTMLTestMailContent.replace("&lt;img", "<img");
					beeHTMLTestMailContent= beeHTMLTestMailContent.replace("/&gt;", "/>");
					//logger.info("After replace Barcode &lt;img and /&gt;"+beeHTMLTestMailContent);
					
				} 

		if(!beeHTMLTestMailContent.isEmpty()&& caretPosSE.getAttribute("action").equals("test")) {
			
			testWinId$msgLblId.setValue("");
			/*if(Utility.validateHtmlSize(beeHTMLTestMailContent)) {

				Messagebox.show("It looks like the content of your email is more " +
						"than the recommended size per the email-" + 
						"sending best practices. " + "To avoid email landing " + 
						"in recipient's spam folder, please redesign " 
									+"email to reduce content.", "Info", Messagebox.OK, Messagebox.INFORMATION);
			}*/
			long size =	Utility.validateHtmlSize(beeHtmlContent);
			if(size >100) {
				String msgcontent = OCConstants.HTML_VALIDATION;
				msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
				MessageUtil.setMessage(msgcontent, "color:Blue");
			}
			String emailId = testWinId$testTbId.getText();
			
			String[] emailArr = emailId.split(",");
			for (String email : emailArr) {

				super.sendTestMail(beeHTMLTestMailContent, email);
			}//for
			testWinId.setVisible(false);
			
			
		}
		
	}

	//public void onChange$jsonAutoSaveTextBoxId() {
	public void onCustomEventAutoSave$jsonData(ForwardEvent event) throws JSONException{
		isAutoSave=true;
		//Clients.evalJavaScript("bee.save()");
		Clients.evalJavaScript("bee.send()");
		//System.out.println(jsonAutoSaveTextBoxId.getValue());
	/*	beeAutosave = jsonAutoSaveTextBoxId.getValue();
		String autohtml = campaign.getHtmlText(); 
		saveEmail(autohtml,null, null, null, false,beeAutosave);
		TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
		autoSaveLbId.setValue("Last auto-saved at: "+ (MyCalendar.calendarToString(Calendar.getInstance(),MyCalendar.FORMAT_SCHEDULE_TIME,tz)).substring(13));
	*/}
	
	
	private String  beeAutosaveJsonContent;
	public void onChange$autoSaveJsonTextBoxId(){
		logger.info("onChange$autoSaveJsonTextBoxId just enterd : "+System.currentTimeMillis());
		beeAutosaveJsonContent =  autoSaveJsonTextBoxId.getValue();
		//logger.info("in autosave before................"+beeAutosaveJsonContent);
		beeAutosaveJsonContent=beeAutosaveJsonContent.replace("\uFEFF","&nbsp;");
		//logger.info("in after replacement autosave................"+beeAutosaveJsonContent);
		autoSaveJsonTextBoxId.setValue("EMPTY_JSON");
				if(beeAutosaveJsonContent!=null&!beeAutosaveJsonContent.equalsIgnoreCase("EMPTY_JSON")){
					logger.info("Just before ending onChange$autoSaveJsonTextBoxId and just before calling bee.send() : "+System.currentTimeMillis());
					Clients.evalJavaScript("bee.send()");
				} 
	}
	
	
	public void onChange$sendMethodHtmlTextBoxId(){
		logger.info("onChange$sendMethodHtmlTextBoxId just enterd : "+System.currentTimeMillis());
		String beeAutosaveJsonContentLocal =beeAutosaveJsonContent;
		String beeHtmlContentLocal = sendMethodHtmlTextBoxId.getValue();
		sendMethodHtmlTextBoxId.setValue("EMPTY_HTML");
		
		
		String[] beeHtmlContentParts = beeHtmlContentLocal.split("<a");
		if(beeHtmlContentLocal!=null&!beeHtmlContentLocal.equalsIgnoreCase("EMPTY_HTML")){
			for(int i=0;i<=beeHtmlContentParts.length-1;i++){
				String anchorTagStr="<a"+beeHtmlContentParts[i];
				//System.out.println("anchorTagStr.."+anchorTagStr);
				String promocodeLink="<a"+StringUtils.substringBetween(anchorTagStr, "<a", "</a>")+"</a>";
				//System.out.println("promocodeLink.."+promocodeLink);
				if(promocodeLink.contains("BEEFREEPROMOCODE_START")&&promocodeLink.contains("BEEFREEPROMOCODE_END")){
					String promocodeValue=StringUtils.substringBetween(beeHtmlContentLocal, "BEEFREEPROMOCODE_START", "BEEFREEPROMOCODE_END");
					//System.out.println("promocodeValue.."+promocodeValue);
					logger.info("promocodeValue---------"+promocodeValue);
					beeHtmlContentLocal=beeHtmlContentLocal.replace(promocodeLink, promocodeValue);
					//logger.info("Html after Replace.."+beeHtmlContentLocal);
				}
				//logger.info("----------.....................------------");
			}
			//logger.info("After replace Promocode and before replacing barcode"+beeHtmlContentLocal);
			beeHtmlContentLocal= beeHtmlContentLocal.replace("&lt;img", "<img");
			beeHtmlContentLocal= beeHtmlContentLocal.replace("/&gt;", "/>");
			//logger.info("After replace Barcode &lt;img and /&gt;"+beeAutosaveJson);
			beeHtmlContentLocal=Utility.replaceTextBarcodeHeightWidth(beeHtmlContentLocal);
		}
		
		
		
		if(beeAutosaveJsonContentLocal!=null & !beeAutosaveJsonContentLocal.equalsIgnoreCase("EMPTY_JSON") & beeHtmlContentLocal!=null & !beeHtmlContentLocal.equalsIgnoreCase("EMPTY_HTML")){
			beeAutosaveJsonContent ="EMPTY_JSON";
			logger.info("just before calling saveEmail: "+System.currentTimeMillis());
			//logger.info("beeAutosaveJsonContentLocal: "+beeAutosaveJsonContentLocal);
			 sessionScope.removeAttribute("selectedTemplate");
			saveEmail(beeHtmlContentLocal,null, null, null, false,beeAutosaveJsonContentLocal);
			logger.info("just after calling saveEmail: "+System.currentTimeMillis());

			
			TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
			autoSaveLbId.setValue("Last auto-saved at: "+ (MyCalendar.calendarToString(Calendar.getInstance(),MyCalendar.FORMAT_SCHEDULE_TIME,tz)).substring(13));
		}
		else{
			beeAutosaveJsonContent ="EMPTY_JSON";
		}
	}



	public void onTimer$autoSaveTimerId() {
		TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
		//111111String htmlStuff = fckEditorId.getValue();
		//111111super.saveEmail(htmlStuff, null , null, null, false);
		logger.debug("after saving");
		logger.debug("current time"+MyCalendar.calendarToString(Calendar.getInstance(),
				MyCalendar.FORMAT_SCHEDULE_TIME));
		logger.debug("current only time"+MyCalendar.calendarToString(Calendar.getInstance(),MyCalendar.FORMAT_SCHEDULE_TIME));
		autoSaveLbId.setValue("Last auto-saved at: "+ MyCalendar.calendarToString(Calendar.getInstance(),
				MyCalendar.FORMAT_SCHEDULE_TIME,tz));
	}

	public String getHtmlStuffFromSourceTypeIsSchedule() {
		String htmlStuff = "";
		if(emailContent != null) {

			// get the content if CampaignSchedule's emailcontent is not null
			htmlStuff = emailContent.getHtmlContent();
			contentNameTBId.setValue(emailContent.getName());
		}
		else {

			// if content of CampaignSchedule is null try to get its parent content
			Long parentId = campSchedule.getParentId();
			CampaignSchedule parentCS = campaignScheduleDao.findById(parentId);

			if(parentCS != null && parentCS.getEmailContent() != null ) {
				htmlStuff =  parentCS.getEmailContent().getHtmlContent();
				contentNameTBId.setValue("");
			}
			// if parent's content also null get the content from campaign
			else {
				htmlStuff = campaign.getHtmlText();
			}
		}


		return htmlStuff;
	}//getHtmlStuffFromSourceTypeIsSchedule


	public void zipImport(Media media) {
		try {
			logger.debug("just entered");
			MessageUtil.clearMessage();
			Media m = (Media)media;
			String emailName=null;
			if(campaign != null){
				emailName=campaign.getCampaignName();
			}else {
				logger.error("** Exception : Campaign object is null. **");
				return;
			}
			if(userName==null) {
				logger.error("** Exception : userName object is null. **");
				return;	
			}

			String zipPath = usersParentDirectory +File.separator +  userName +File.separator+m.getName();
			String unzipPath = usersParentDirectory  + File.separator +  userName +File.separator+"Email";
			String invalidFiles=null;
			BufferedInputStream in = new BufferedInputStream(m.getStreamData());
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(zipPath));

			byte[] buff = new byte[1024];
			int count=0;
			while((count = in.read(buff)) >= 0) {
				out.write(buff,0,count);
			}
			out.flush();
			in.close();
			out.close();

			File zipFile = new File(zipPath);
			if(zipFile.length() > 4 * MEGABYTE) {
				logger.error("Zip file size cannot be more than 4 MB. Returning.");
				MessageUtil.setMessage("Zip file size cannot be more than 4MB.","color:red", "TOP");
				zipFile.delete();
				return;
			}

			logger.debug("Copied the zip file to the User root folder Successfully.");

			if(!validateZipFiles(zipFile)) { 
				logger.error("Invalid Zip File. Returning.");
				zipFile.delete();
				return;
			}			

			try {
				long millis = System.currentTimeMillis();
				String zipName =  m.getName();
				invalidFiles = ZipImport.unzipFileIntoDirectory(zipFile, unzipPath, emailName, millis);

				if(invalidFiles.indexOf('\n') > 1) { 
					Messagebox.show("File uploaded successfully. However the following" +
							" invalid files will be ignored.\n\r"+invalidFiles);
				}
				else if(invalidFiles.indexOf(',')>0){
					MessageUtil.setMessage("File uploaded successfully. However the " +
							"following invalid files will be ignored."+invalidFiles, 
							"color:blue", "TOP");
				}

				setHTMLFromFolder(unzipPath+File.separator+emailName+File.separator+millis);

				logger.debug("zipName :"+zipName+"  millis :"+millis+" zipPath:"+zipPath+
						" unzipPath:"+unzipPath);
			}	
			catch (Exception e) {
				logger.error("** Exception while extracting files from Zip file to users Parent Directory :"+e+" **");
			}
		} catch (Exception e) {
			logger.error("** Exception while uploading zip file : "+e.getMessage()+" **");
		}
	}

	public void setHTMLFromFolder(String filePath) {
		logger.debug("Just Entered ..."+filePath);
		File htmlFile = new File(filePath+File.separator+htmlFilePath);

		String htmlParentDir = htmlFile.getParent();
		logger.info("htmlParentDir="+htmlParentDir);

		try {
			BufferedReader br = new BufferedReader(new FileReader(htmlFile));
			StringBuffer sb = new StringBuffer(""); 
			String line="";

			while((line=br.readLine())!=null) {
				sb.append(line);
			}
			br.close();

			String pattern = "\\ssrc\\s*?=\\s*?\"(.*?)(?=\")";
			String bgpattern = "(?<=background)\\s*?=\\s*?\"(.*?)(?=\")";

			String urlpattern ="url\\s*?\\((.+?)\\)";
			Pattern r ;
			Matcher m ;
			String userDataDir = "UserData"+File.separator+userName+File.separator + 
					"Email"+File.separator+campaign.getCampaignName();


			htmlParentDir = 
					htmlParentDir.substring(htmlParentDir.indexOf(userDataDir) + userDataDir.length());

			logger.info("After htmlParentDir="+htmlParentDir);

			String replaceUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+File.separator+""+userDataDir+htmlParentDir+File.separator;
			logger.debug("replace Url : "+replaceUrl);

			r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
			m = r.matcher(sb);
			StringBuffer outsb = new StringBuffer();
			while (m.find()) {
				String url = m.group(1).trim();
				String replaceStr = url;

				if(!url.contains("http://")){
					replaceStr =" src=\""+ replaceUrl+url;
					//					replaceStr = Utility.encodeSpace(replaceStr);
					logger.debug(" src replace >>>::: "+replaceStr);
					m.appendReplacement(outsb, replaceStr);
				}
				/*else {
					m.appendReplacement(outsb, m.group());
				}*/
			}
			m.appendTail(outsb);
			sb=outsb;
			outsb=new StringBuffer();

			r = Pattern.compile(bgpattern, Pattern.CASE_INSENSITIVE);
			m = r.matcher(sb);

			while (m.find()) {
				String orgUrl=m.group();
				String url = m.group(1).trim();
				String replaceStr =null;

				logger.info(">>>>>>> ORGSTR="+orgUrl);
				logger.info(">>>>>>> URL="+url);
				logger.info(">>>>>>> repUrl="+replaceUrl);

				if(!url.contains("http://")){
					replaceStr = replaceUrl+url;
					replaceStr = Utility.encodeSpace(replaceStr);
					replaceStr = "=\""+ replaceStr;
					logger.debug("bg replace : "+replaceStr);
					m.appendReplacement(outsb, replaceStr);
					logger.info(">>>>>>>>>>>>>"+outsb.toString());
				}
				/*else {
					m.appendReplacement(outsb, orgUrl);
				}*/
			}
			m.appendTail(outsb);						

			sb=outsb;
			outsb=new StringBuffer();


			r = Pattern.compile(urlpattern, Pattern.CASE_INSENSITIVE);
			m = r.matcher(sb);

			while (m.find()) {
				String url = m.group(1).trim();
				String replaceStr = url;
				logger.debug("Url  is ::"+url);
				if(!url.contains("http://")){
					//replaceStr = "("+replaceUrl+ url.substring(url.indexOf('(')+1,url.indexOf(')'))+")";
					replaceStr = "url(" +replaceUrl + replaceStr + ")";
					replaceStr = Utility.encodeSpace(replaceStr);
					logger.debug("url replace : "+replaceStr);
					m.appendReplacement(outsb, replaceStr);
				}
				/*else {
					m.appendReplacement(outsb, m.group());
				}*/
			}
			m.appendTail(outsb);

			campaign.setHtmlText(outsb.toString());
			campaign.setPrepared(false);
			//111111fckEditorId.setValue(outsb.toString());

		} catch (Exception e) {
			logger.error("Error while setting html in Editor",(Throwable)e);
		}

	}

	//public void saveEmail(String htmlStuff) {
	public void onClick$saveBtnId() {

		try {

			//111111String htmlStuff = fckEditorId.getValue();

			if(logger.isDebugEnabled())logger.debug("-- just entered --");

			String contentName = null;
			
			// if source is schedule then it means editing the 
			// content of campaign's RE SENDING SCHEDULE
			if(source !=  null && source.equals("schedule") ) {

				contentName = contentNameTBId.getValue();

				logger.debug(" >>>> EmailContent :"+campSchedule.getEmailContent());

				// when newly creating the email content checking 
				// whether the name is provided or not for the Content.
				if(campSchedule.getEmailContent() == null && 
						(contentName == null ||	contentName.trim().length() == 0)) {

					Messagebox.show("Please provide name for the content.", 
							"Error", Messagebox.OK, Messagebox.ERROR);
					contentNameTBId.setFocus(true);
					return;
				}

			} // if source is saving the content of re sending campaign schedule

			//111111super.saveEmail(htmlStuff,null, campSchedule, contentName, true);
			/*8_june_venkatlogger.debug("Before calling bee.save()");
			Clients.evalJavaScript("bee.save()");
			logger.debug("After calling bee.save()");

			sessionScope.setAttribute("retainChanges", "retain");
			 *//*Messagebox.show("Content saved successfully.",
					"Captiway", Messagebox.OK, Messagebox.INFORMATION );*/
			sessionScope.setAttribute("retainChanges", "retain");
			Clients.evalJavaScript("bee.save()");

		}
		catch (Exception e) {
			logger.error("** Exception :", e);
		}

	}// saveEmail()

	//public void saveInMyTemplates(Window winId, String name, String htmlStuff){
	private Textbox zipImport$templatNameTbId;
	private Listbox zipImport$myTemplatesListId;
	public void onClick$myTemplatesSubmtBtnId$zipImport(){

		try{
			if(logger.isDebugEnabled())logger.debug("-- just entered --");
			Textbox nameTbId = (Textbox)zipImport.getFellowIfAny("templatNameTbId");
			String name=nameTbId.getValue();
			if(name == null || name.trim().length() == 0){
				MessageUtil.setMessage("Please provide a name to save in My Templates folder.", "color:red", "TOP");
				return;	
			}
			else if(!Utility.validateName(name)) {

				MessageUtil.setMessage("Please provide a valid name witn no special characters.", "color:red", "TOP");
				return;
			}
			/*else {
				resLbId.setVisible(false);
			}
			 */			isSaveAsTemplate=true;
			 Clients.evalJavaScript("bee.save()");
		}catch (Exception e) {
			logger.error("** Exception :", e );
		}
	}


	//public void gotoPlainMsg(String htmlStuff){
	public void onClick$nextBtnId() throws Exception {
		//111111	super.gotoPlainMsg(fckEditorId.getValue(), null);
		next = true;
		Clients.evalJavaScript("isnextbutton = 'isnextbutton';");
		Clients.evalJavaScript("bee.save()");
	}

	//public void saveAsDraft(String htmlStuff){
	public void onClick$saveAsDraftBtnId() throws Exception {
		//111111	super.saveAsDraft(fckEditorId.getValue(), null);
		List<CampaignSchedule> campScheduleList = campaignScheduleDao.getByCampaignId(campaign.getCampaignId());
		long activeCount = campScheduleList.stream().filter(campaignSchedule -> campaignSchedule.getStatus() == 0).count(); 
		if(campScheduleList.size() == 0 || activeCount == 0) {
			dnext = true;
			Clients.evalJavaScript("bee.save()");
		}else {
			MessageUtil.setMessage("A campaign with upcoming schedule/s cannot be saved as a draft.\n Please delete all active schedules first.", "color:red");
			return;
		}
	}
	
	public Calendar getCurrentTimePlus3Huors() {
		Calendar currentTimePlus3hours = Calendar.getInstance();
		currentTimePlus3hours.add(Calendar.HOUR, +3);
		return currentTimePlus3hours;
	}
	
	// changes here
	public boolean validateEmailAddr(String emailId) {
		//super.sendTestMail(fckEditorId.getValue(), testEmailTbId.getValue());
		try {
			boolean isValid = true;			
			if(emailId != null && emailId.trim().length() > 0) {

				if(logger.isDebugEnabled())logger.debug("Sending the test mail....");

				MessageUtil.clearMessage();
				testWinId$msgLblId.setValue("");
				String[] emailArr = null;
				if(isValid) {

					emailArr = emailId.split(",");
					for (String email : emailArr) {

						if(!Utility.validateEmail(email.trim())){
							testWinId$msgLblId.setValue("Invalid Email address:'"+email+"'");
							isValid = false;
							break;
						}else {
							//Testing for invalid email domains-APP-308
							String result = PurgeList.checkForValidDomainByEmailId(email.trim());
							if(!result.equalsIgnoreCase("Active")) {
								testWinId$msgLblId.setValue("Invalid Email address:'"+email+"'");
								isValid = false;
								break;
							}
						}

					}//for
				}

			}else {
				isValid = false;
				testWinId$msgLblId.setValue("Invalid Email address");
				testWinId$msgLblId.setVisible(true);
			}
			return isValid;
		}
		/*
		 * if(isValid) {
					for (String email : emailArr) {

						super.sendTestMail(caretPosSE.getValue(), testWinId$testTbId.getValue());
					}//for
					testWinId.setVisible(false);

				}	
		 */

		//111111 else if(super.sendTestMail(fckEditorId.getValue(), testEmailTbId.getValue())) {
		//testMailTbId.setValue("");
		//111111	testEmailTbId.setValue("Email Address...");
		//111111	}
		catch(Exception e) {
			logger.error("** Exception : " , e);
			return false;
		}

	}


	// changes here

	public void onClick$sendTestBtnId() {
		//take the content think about ph logic first
		//to be clarified , do we need validations or not...
		
		/*testWinId$msgLblId.setValue("");
		boolean isValid = true;
		if(Utility.validateHtmlSize(caretPosSE.getValue())) {

			Messagebox.show("HTML size cannot exceed 100kb. Please reduce" +
					"the size and try again.", "Error", Messagebox.OK, Messagebox.ERROR);
			isValid = false;	
		}

		if(isValid) {
			testWinId$testTbId.setValue("");
			testWinId.setVisible(true);
			testWinId.setPosition("center");
			testWinId.doHighlighted();
		}*/
		//To clear Invalid email address message
		
		
		testWinId$msgLblId.setValue("");
		testWinId.setVisible(false);

		
		
		testWinId$testTbId.setValue("");
		testWinId.setVisible(true);
		testWinId.setPosition("center");
		testWinId.doHighlighted();
	}

	public void onClick$sendTestMailBtnId$testWinId() {		
		String emailId = testWinId$testTbId.getValue();
		boolean isValid = validateEmailAddr(emailId);
		if(isValid){
			testWinId$sendTestMailBtnId.setDisabled(true);
			isSendTestMail=true;
			Clients.evalJavaScript("bee.save()");
			testWinId.setVisible(false);
		}
	}

	public void onClick$cancelSendTestMailBtnId$testWinId() {

		testWinId$msgLblId.setValue("");
		testWinId$msgLblId.setValue("");
		testWinId.setVisible(false);

	}

	public void onBlur$testEmailTbId() throws Exception {

		String mail=testEmailTbId.getValue();
		//////logger.debug("here in on blur method mail id "+mail);
		if(mail.equals("Email Address...") || mail.equals("")){
			testEmailTbId.setValue("Email Address...");

		}
	}

	// changes here
	public void onFocus$testEmailTbId() throws Exception {
		String mail=testEmailTbId.getValue(); 
		if(mail.equals("Email Address...") || mail.equals("")){
			testEmailTbId.setValue("");

		} 
	}

	public void loadEmailContent() {

		try {

			if(emailContentsLBId.getSelectedIndex() == -1) {
				Messagebox.show("Please provide name for the content.", 
						"Error", Messagebox.OK, Messagebox.ERROR);
				return;
			}

			EmailContent emailContent = (EmailContent)
					emailContentsLBId.getSelectedItem().getValue();
			//111111	fckEditorId.setValue(emailContent.getHtmlContent());
		}
		catch (Exception e) {
			logger.error("** Exception while loading the selected email content", e);
		}
	}

	private static final long MEGABYTE = 1024l * 1024l;
	private static final long KILOBYTE = 1024l;

	public boolean validateZipFiles(File zipfile) {
		ZipInputStream zis =null;
		try{
			zis = new ZipInputStream(new FileInputStream(zipfile));
			ZipEntry ze;
			String substr=null;
			String substrExt=null;
			int htmlCount=0,validImgCount=0,validDocCount=0,cssCount=0;

			while ((ze = zis.getNextEntry()) != null) {
				substr=ze.getName();
				substr = substr.substring(substr.lastIndexOf(File.separator)+1);
				substrExt = substr.substring(substr.lastIndexOf(".")+1);

				if(!substrExt.equals("")) {
					logger.debug("Extention :"+substrExt);
					if(substrExt.equalsIgnoreCase("htm") || substrExt.equalsIgnoreCase("html")) {
						htmlCount++;
						htmlFilePath = ze.getName();

						if(ze.getSize()>(100*KILOBYTE)) {
							Messagebox.show("It looks like the content of your email is more " +
									"than the recommended size per the email-" + 
									"sending best practices. " + "To avoid email landing " + 
									"in recipient's spam folder, please redesign " 
												+"email to reduce content.", "Info", Messagebox.OK, Messagebox.INFORMATION);
						}
					}	else if(substrExt.equalsIgnoreCase("jpeg") || substrExt.equalsIgnoreCase("jpg") || 
							substrExt.equalsIgnoreCase("gif") || substrExt.equalsIgnoreCase("png")|| 
							substrExt.equalsIgnoreCase("bmp")) {
						validImgCount++;	
					}
					else if(substrExt.equalsIgnoreCase("pdf")) {
						validDocCount++;
					}
					else if(substrExt.equalsIgnoreCase("css")) {
						cssCount++;
					}
				} // if

				zis.closeEntry();
			} // while

			if(htmlCount==0 || htmlCount>1) {
				Messagebox.show("Zip file with one HTML file is required/allowed.", 
						"Error", Messagebox.OK, Messagebox.ERROR);
				return false;
			}
			return true;
		} catch(Exception e) {
			logger.error("** Exception while validating zip file contents **",(Throwable)e);
			return false;
		}
		finally {
			try {
				zis.close();
			}
			catch(Exception e) {
				logger.error("Exception ::" , e);;
			}
		}

	} //

	private Label zipImport$fetchUrlErrMsgLblId;
	//public void getHtmlPage(Textbox zipImport$urlTbId) {
	public void onClick$fetchURLGoBtnId$zipImport() throws Exception {
		try {

			String urlStr = zipImport$urlTbId.getValue();
			logger.debug("URL : " + urlStr);
			if( (urlStr == null || urlStr.length() == 0)) {
				zipImport$fetchUrlErrMsgLblId.setValue("Error Message : Enter the URL to fetch the page..");
				//Messagebox.show("Enter the URL to fetch the page.", "Captiway", Messagebox.OK, Messagebox.ERROR);
				return;
			}

			if(urlStr.indexOf("/") < 0 && !urlStr.contains(".")) {
				urlStr = "http://www." + urlStr + ".com";
			}
			else if(!urlStr.contains("://")) {
				urlStr = "http://" + urlStr;
			}
			else if(!(urlStr.startsWith("http://") || urlStr.startsWith("www"))) {
				Messagebox.show("Enter valid URL to fetch the page.", "Error", Messagebox.OK, Messagebox.ERROR);
				return;
			}
			zipImport$urlTbId.setValue(urlStr);
			MessageUtil.clearMessage();
			try {
				StringBuffer pageSb = new StringBuffer();
				URL url = new URL(urlStr);
				URLConnection conn = url.openConnection();
				logger.debug("Connection opened to the specified url :" + urlStr);
				DataInputStream in = new DataInputStream ( conn.getInputStream (  )  ) ;
				BufferedReader d = new BufferedReader(new InputStreamReader(in));
				logger.debug("Reader obj created");
				while(d.ready()) {
					pageSb.append(d.readLine());
				}
				logger.debug("Read the data from the URL, data lenght:" + pageSb.length());
				in.close();
				d.close();
				logger.debug(">>>>>>>>>>>>>>>>>>>>>>>" + pageSb.toString());
				String content = HTMLUtility.getBodyContentOnly(pageSb.toString(),urlStr);
				if(content == null) {
					Messagebox.show("Invalid HTML: Unable to fetch HTML content from the specified URL.",
							"Error", Messagebox.OK, Messagebox.ERROR);
					return;
				}
				//111111fckEditorId.setValue(content);
			} catch (MalformedURLException e) {
				Messagebox.show("Malformed URL: Unable to fetch the web-page from the specified URL.",
						"Error", Messagebox.OK, Messagebox.ERROR);
				logger.error("MalformedURLException : ", e);
			} catch (IOException e) {
				Messagebox.show("Network problem experienced while fetching the page. "
						, "Error", Messagebox.OK, Messagebox.ERROR);
				logger.error("IOException : ", e);
			}
		} catch (Exception e) {
			logger.error("Exception : ", e);
		}

		zipImport.setVisible(false);

	}

	public void useExistedEmail(){

		if(emailContentsLBId.getSelectedIndex() == -1) {
			MessageUtil.setMessage("Select the content to use.", "color:red", "TOP");
			return;
		}

		EmailContent emailContent = (EmailContent)
				emailContentsLBId.getSelectedItem().getValue();
		//111111 fckEditorId.setValue(emailContent.getHtmlContent());

	}

	public void onClick$spamScrBtnId() throws Exception {
		//111111 super.checkSpam(fckEditorId.getValue(),true);
	}

	public void onClick$reloadTblId() throws Exception {
		try{
			logger.debug("reloadBtn Id called");
			int confirm = Messagebox.show("Do you want to reload the email?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm != 1){
				return;
			}else{
				//111111 fckEditorId.setValue("");
				//if( !htmlStuff.trim().isEmpty()) {
				String	htmlStuff = null;
				if(source != null && source.equalsIgnoreCase("schedule")) {
					//	emailContent.getHtmlContent();
					logger.debug("html content is not null from the schedule type of source...");
					htmlStuff = getHtmlStuffFromSourceTypeIsSchedule();
				}
				else  {//
					//111111htmlStuff = campaign.getHtmlText();
					JSONObject jsontemplate = (JSONObject)JSONValue.parse(campaign.getJsonContent());	
					//logger.info("jsontoload............"+jsontemplate);		
					Clients.evalJavaScript("bee.load("+jsontemplate+");");
				}
				//111111	fckEditorId.setValue(htmlStuff);
				//111111}
			}
		}catch (Exception e) {
			logger.error("** Exception : " , e );
		}
	}

	public void onClick$backBtnId() throws Exception {
		super.back();
	}

	private Div zipImport$importZipFileDivId,zipImport$saveMyTemplateDivId,zipImport$fetchUrlDivId;
	public void onClick$zipImportTlbBtnId() throws Exception {
		zipImport$resultLblId.setValue("");
		zipImport$selectedFileTbId.setValue("");
		zipImport$saveMyTemplateDivId.setVisible(false);
		zipImport$fetchUrlDivId.setVisible(false);
		zipImport$importZipFileDivId.setVisible(true);
		zipImport.setTitle("Zip Import");
		zipImport.setVisible(true);
		zipImport.doHighlighted();
	}

	//uploadBtnId,zipImport
	private Label zipImport$resultLblId;
	private Textbox zipImport$selectedFileTbId;
	private Media uploadHtmlZipFileMedia;

	public void onUpload$uploadBtnId$zipImport(UploadEvent event) {
		try {
			logger.info("Browse is called");
			uploadHtmlZipFileMedia = event.getMedia();
			String filename = uploadHtmlZipFileMedia.getName();
			if (filename.indexOf(".zip") == -1) {
				zipImport$resultLblId.setValue(filename + " in not a ZIP file.");
				return;
			}

			zipImport$selectedFileTbId.setValue(uploadHtmlZipFileMedia.getName());
			//gMedia = media;
			zipImport$resultLblId.setValue("");
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}

	} // onClick$uploadBtnId$zipImport

	public void onClick$zipUploadBtnId$zipImport() {

		logger.info("Upload is called");
		Media media = uploadHtmlZipFileMedia; 

		if(media == null) {
			zipImport$resultLblId.setVisible(true);
			zipImport$resultLblId.setValue("Please select a File");
			return;
		}
		zipImport(media);

		zipImport$selectedFileTbId.setValue("");
		zipImport$resultLblId.setValue("");

		reset(true);


	} // onClick$zipUploadBtnId$zipImport

	private void reset(boolean doWinClose) {
		zipImport.setVisible(!doWinClose);
	}

	/*public void onClick$closeTBtnId$zipImport() {
		 reset(true); 
	 }*/

	public void onClick$cancelBtnId$zipImport() {
		reset(true);
	}

	private Label zipImport$resLbId;
	public void onClick$urlToFetchHtmlTBtnId() {

		zipImport$urlTbId.setValue("");
		zipImport$fetchUrlErrMsgLblId.setValue("");
		zipImport$saveMyTemplateDivId.setVisible(false);
		zipImport$importZipFileDivId.setVisible(false);
		zipImport$fetchUrlDivId.setVisible(true);
		zipImport.setTitle("Fetch Html from Url");
		zipImport.setVisible(true);
		zipImport.doHighlighted();

	} //onClick$urlToFetchHtmlTBtnId

	public void onClick$saveInMyTemplateTB() {
		//zipImport.setTitle("Save in My Templates");
		getSaveTemplateWindow();
		//Clients.evalJavaScript("bee.save()");
		/*zipImport$resLbId.setVisible(false);
		zipImport$importZipFileDivId.setVisible(false);
		zipImport$fetchUrlDivId.setVisible(false);
		zipImport$saveMyTemplateDivId.setVisible(true);
		zipImport$templatNameTbId.setValue("");
		zipImport.setVisible(true);
		getMyTemplatesFromDb(currentUser.getUserId());
		zipImport.doHighlighted();
		 */
	} //onClick$saveInMyTemplateTB

	public void getSaveTemplateWindow(){
		zipImport.setTitle("Save in My Templates");
		zipImport$resLbId.setVisible(false);
		zipImport$importZipFileDivId.setVisible(false);
		zipImport$fetchUrlDivId.setVisible(false);
		zipImport$saveMyTemplateDivId.setVisible(true);
		zipImport$templatNameTbId.setValue("");
		zipImport.setVisible(true);
		getMyTemplatesFromDb(currentUser.getUserId());
		zipImport.doHighlighted();
	}

	private Window previewIframeWin; 
	private  Iframe previewIframeWin$iframeId;
	public void onClick$plainPreviewImgId() {
		logger.info("entered into html preview....");
		if(campaign != null) {
			Clients.evalJavaScript("bee.preview()");
			//Clients.evalJavaScript("bee.save()");

			//111111if(fckEditorId.getValue() != null){
			//String htmlContent=campaign.getHtmlText();
			//111111 Utility.showPreview(previewIframeWin$iframeId,currentUser.getUserName(), fckEditorId.getValue());
			//111111	 previewIframeWin.setVisible(true);
			//111111	}
			/*	if(beeHtmlContent != null){
					 String htmlContent=campaign.getHtmlText();
					 Utility.showPreview(previewIframeWin$iframeId,currentUser.getUserName(), beeHtmlContent);
					 previewIframeWin.setVisible(true);
					}*/
		}

	}
	private final String  USER_NEW_EDITOR_TEMPLATES_DIRECTORY= "userNewEditorTemplatesDirectory"; 
	public void getMyTemplatesFromDb(Long userId){
		String userTemplatesDirectory = PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATES_DIRECTORY);
		Components.removeAllChildren(zipImport$myTemplatesListId);
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

			item.setParent(zipImport$myTemplatesListId);		
		}
		if(zipImport$myTemplatesListId.getItemCount() > 0) zipImport$myTemplatesListId.setSelectedIndex(0);
	}

	//private Listbox myTemplatesListId;
	protected void saveInMyTemplates(String fileName, String beeHtmlContent, String editorType, Listbox myTemplatesListId,String beeJsonContent) {
		String foldernName="";
		String isValidPhStr = null;
		isValidPhStr = Utility.validatePh(beeHtmlContent, currentUser);
		if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
			MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
			return ;
		}
		else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
			MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
			return ;
		}

		/*String isValidCCDim = null;
		isValidCCDim = Utility.validateCouponDimensions(beeHtmlContent);
		if(isValidCCDim != null){
			return ;
		}*/

		int override = -1;
		try{
			/**
			 * creates a MyTemplate object and saves in DB
			 */
			MyTemplates myTemplate = null;
			MyTemplatesDaoForDML myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
			if(myTemplatesDao == null)
				myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
			foldernName= myTemplatesListId.getSelectedItem().getLabel();
			foldernName = foldernName.contains("(") ? foldernName.substring(0,foldernName.indexOf("(")) : foldernName;

			myTemplate = myTemplatesDao.findByUserNameAndTempNameInFolder(currentUser.getUserId(),fileName,foldernName, Constants.NEWEDITOR_TEMPLATES_PARENT);
			if(myTemplate != null) {

				try {
					override = Messagebox.show("The name already exists. Do you want to replace it?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(override == 1 ) {

						beeHtmlContent= beeHtmlContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
								.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
								.replace(MAILHANDLER_URL, ImageServer_Url);
						beeJsonContent = beeJsonContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
								.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
								.replace(MAILHANDLER_URL, ImageServer_Url);
						myTemplate.setContent(beeHtmlContent);
						myTemplate.setJsoncontent(beeJsonContent);

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
				beeHtmlContent = beeHtmlContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
						.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
						.replace(MAILHANDLER_URL, ImageServer_Url);
				beeJsonContent = beeJsonContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
						.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
						.replace(MAILHANDLER_URL, ImageServer_Url);
				myTemplate = new MyTemplates(fileName, beeHtmlContent, cal, editorType, currentUser,foldernName,beeJsonContent, Constants.NEWEDITOR_TEMPLATES_PARENT);
			}
			//myTemplatesDao.saveOrUpdate(myTemplate);
			myTemplatesDaoForDML.saveOrUpdate(myTemplate);

			/**
			 * creates a html file and saves in user/MyTemplate directory
			 */

			if(beeHtmlContent.contains("href='")){
				beeHtmlContent = beeHtmlContent.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");

			}
			if(beeHtmlContent.contains("href=\"")){
				beeHtmlContent = beeHtmlContent.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
			}
			
			/*String myTemplateFilePathStr = 
					PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
					PropertyUtil.getPropertyValue("userTemplatesDirectory")+File.separator+foldernName+
					File.separator+fileName+File.separator+"email.html";
			*/
			//To save in bee  templates
			String myTemplateFilePathStr = 
					PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
					PropertyUtil.getPropertyValue("userNewEditorTemplatesDirectory")+File.separator+foldernName+
					File.separator+fileName+File.separator+"email.html";
			try {
				File myTemplateFile = new File(myTemplateFilePathStr);
				File parentDir = myTemplateFile.getParentFile();

				if(override == 1) {
					FileUtils.deleteDirectory(parentDir);
				}

				parentDir.mkdirs();

				//TODO Have to copy image files if exists
				BufferedWriter bw = new BufferedWriter(new FileWriter(myTemplateFile));

				bw.write(beeHtmlContent);
				//bw.write(beeJsonContent);
				bw.flush();
				bw.close();
				String msgStr = fileName +" saved in My templates successfully.";
				if(override == 1) {
					msgStr = fileName +" updated successfully in My templates";
				}
				MessageUtil.setMessage(msgStr, "color:blue", "TOP");

				/*				if(userActivitiesDao != null) {
	        		userActivitiesDao.addToActivityList(ActivityEnum.CAMP_SAVED_TO_MYTEMPLATE_p1campaignfileName, currentUser,campaign.getCampaignName());
				}*/
				if(userActivitiesDaoForDML != null) {
					userActivitiesDaoForDML.addToActivityList(ActivityEnum.CAMP_SAVED_TO_MYTEMPLATE_p1campaignName, currentUser,campaign.getCampaignName());
				}

			} catch (IOException e) {
				logger.error("** Exception : MyTemplates file creation failed",(Throwable)e);
			}

			//TODO need to write a code for generating image file of html for preview purpose

		} catch(DataIntegrityViolationException die) {
			//MessageUtil.setMessage("Name already exists in My Templates.", "color:red", "TOP");
			MessageUtil.setMessage("Unable to save in my templates", "color:red", "TOP");
			logger.error("** Exception : while saving template in to MyTemplates", (Throwable)die);
		} catch(Exception e) {
			logger.error("** Exception : while saving the template in MyTemplates", (Throwable)e);
		}
	}
	
	
	private JSONObject metaData;
	private Long userCustomRowId;
	public Long getUserCustomRowId() {
		return userCustomRowId;
	}
	public void setUserCustomRowId(Long userCustomRowId) {
		this.userCustomRowId = userCustomRowId;
	}
	private JSONObject getMetaData() {
		return metaData;
	}
	private void setMetaData(JSONObject setMetaData) {
		this.metaData = setMetaData;
	}
	
	
	public void onClickSaveRowCampaign$jsonDataSaveRow(ForwardEvent event) throws JSONException{
		 Object o1 = JSONValue.parse(event.getOrigin().getData().toString());
		 customRowId$rowTextId.setValue(Constants.STRING_NILL);
		 customRowId$comboCategoryRowId.setValue(Constants.STRING_NILL);
		 if(o1!= null) {
			 JSONObject objectJson =  (JSONObject) o1;
			 customRowId.doHighlighted();
			 customRowId.setVisible(true);
			 setMetaData(objectJson);
			 customRowId$comboCategoryRowId.setAutodrop(false);
			 customRowId$comboCategoryRowId.setReadonly(true);
		 if(objectJson.containsKey("metadata")) {
			 JSONObject s = (JSONObject)objectJson.get("metadata");
			 String templateName = (String) s.get("name");
			 String categoryName = (String) s.get("category");
			 customRowId$rowTextId.setValue(templateName);
			 UserDesignedCustomRows userDesignedCustomRows  = userDesignedCustomRowsDao.getUserDesignedCustomRowsBasedonTemplateAndCategoryName(currentUser.getUserId(),templateName,categoryName);
			 if(userDesignedCustomRows!=null)
				 setUserCustomRowId(userDesignedCustomRows.getTemplateRowId());
			 else {
				 setUserCustomRowId(null);
			 }
		 }
		 customRowId$comboCategoryRowId.getItems().clear();
		 customRowId$comboCategoryRowId.appendItem("My Custom Row");
		 customRowId$comboCategoryRowId.setSelectedIndex(0);
	   }
	}
	
	@SuppressWarnings("unchecked")
	public void onClick$onsaveRowTemplate$customRowId$BeeWinId() throws ParseException, InterruptedException, JSONException {
		String rowName = customRowId$rowTextId.getValue();
		String rowCategoryName = customRowId$comboCategoryRowId.getValue();
		if(rowCategoryName == null || rowCategoryName.isEmpty()) {
			MessageUtil.setMessage("Category cannot be empty", "color:red");
			return;
		}
		if(rowName == null || rowName.isEmpty()) {
			MessageUtil.setMessage("RowName cannot be empty", "color:red");
			return;
		}
		
		UserDesignedCustomRows userDesignedCustomRows  = userDesignedCustomRowsDao.getUserDesignedCustomRowsBasedonTemplateAndCategoryName(currentUser.getUserId(),rowName,rowCategoryName);
		if(userDesignedCustomRows!=null && userDesignedCustomRows.getTemplateRowId()!=null) {
			int confirm = Messagebox.show("A row with this name already exists. Click 'OK' to override its content or click 'Cancel'.", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm != Messagebox.OK ) {
				return;
			}else {
				setUserCustomRowId(userDesignedCustomRows.getTemplateRowId());
			}
		}
		
		JsonObject metadata = new JsonObject();
		JSONObject rowMetaData = getMetaData();
		metadata.addProperty("name", rowName);
		metadata.addProperty("category", rowCategoryName);
		rowMetaData.put("metadata", metadata);
		saveUserDesignedRow(rowCategoryName,rowName,rowMetaData,getUserCustomRowId());
		customRowId.setVisible(false);
		customRowId$rowTextId.setValue(Constants.STRING_NILL);
		customRowId$comboCategoryRowId.setValue(Constants.STRING_NILL);
	}


	private void saveUserDesignedRow(String rowCategoryName, String rowName, JSONObject rowMetaData,Long id) throws JSONException {
		UserDesignedCustomRows rowDetails = new UserDesignedCustomRows();
		if(id!=null) {
			rowDetails.setTemplateRowId(id);
		}
		rowDetails.setRowCategory(rowCategoryName);
		rowDetails.setUserId(currentUser.getUserId());
		rowDetails.setTemplateName(rowName);
		rowDetails.setRowJsonData(rowMetaData.toString());
		rowDetails.setCreatedDate(Calendar.getInstance());
		rowDetails.setModifiedDate(Calendar.getInstance());
		userDesignedCustomRowsDaoForDML.saveOrUpdate(rowDetails);
		prepareCustomRowUrlForCampaign();
	}
	
	public void prepareCustomRowUrlForCampaign() throws JSONException {
		try {
			JSONArray array = Utility.dynamicUrlforCustomRowsBeeEditor(currentUser, userDesignedCustomRowsDao, "campaign");
			Clients.evalJavaScript("var externalUrl ="+array+";");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/*@SuppressWarnings("unchecked")
	public void prepareCustomRowUrlForCampaign() throws JSONException {
		try {
			JSONArray array = new JSONArray();
			JSONObject item = new JSONObject();
			List<ApplicationProperties> defaultCustomRows  = userDesignedCustomRowsDao.getDefaultTemplate("campaign");
			List<String> userDesignedCustomRows  = userDesignedCustomRowsDao.findTemplatesFromUserId(currentUser.getUserId());
			if(defaultCustomRows!=null && !defaultCustomRows.isEmpty() && defaultCustomRows.size() > 0) {
				for (ApplicationProperties defaultRows : defaultCustomRows) {
					item.put("name", defaultRows.getKey());
					item.put("value", ""+appUrl+"savedRows.mqrm?name="+defaultRows.getKey()+"");
					array.add(item);
					item = new JSONObject();
				}
			}
			if(userDesignedCustomRows!=null && !userDesignedCustomRows.isEmpty() && userDesignedCustomRows.size() > 0) {
				for (String customRows : userDesignedCustomRows) {
					item.put("name", customRows);
					item.put("value", ""+appUrl+"userDesignedSavedRows.mqrm?name="+customRows+"&userId="+currentUser.getUserId()+"");
					array.add(item);
					item = new JSONObject();
				}
			}
			Clients.evalJavaScript("externalUrl = "+array+";");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	/*private String getToken(){

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

					return finialTokenValue;

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


		}*/	
	
} //Class
