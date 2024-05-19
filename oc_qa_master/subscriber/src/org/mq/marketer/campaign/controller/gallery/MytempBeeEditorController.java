package org.mq.marketer.campaign.controller.gallery;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.ApplicationProperties;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.UserDesignedCustomRows;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.PrepareFinalHtml;
import org.mq.marketer.campaign.controller.layout.EditorController;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.MyTemplatesDaoForDML;
import org.mq.marketer.campaign.dao.SystemTemplatesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.dao.UserDesignedCustomRowsDao;
import org.mq.marketer.campaign.dao.UserDesignedCustomRowsDaoForDML;
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
import org.zkforge.ckez.CKeditor;
import org.zkoss.util.media.Media;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.ForEach;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.google.gson.JsonObject;

import bsh.ParseException;


@SuppressWarnings("serial")
public class MytempBeeEditorController extends MyTempEditorController{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	Textbox emailNameTbId;
	Textbox testEmailTbId;
	Html html = new Html();
	Toolbarbutton addBlockBtnId;
	String userName, beeclientkey;
	String usersParentDirectory;
	String htmlFilePath;
	CKeditor ckEditorId = null;
	CKeditor fckEditorId;
	Popup editPopup;
	Div appCenterDivId;
	Div toolhide;
	String htmlStuff,jsonStuff;
	String htmlPreviewStuff;
	Timer mytempAutoSaveId;
	Label autoSaveLbId;
	Campaigns campaign = null;
	Window testWinId;
	Textbox testWinId$testTbId;
	Label testWinId$msgLblId;

	private Textbox zipImport$urlTbId;

	private Button nextBtnId,testEmailGoBtnId;
	private Window winId,zipImport;
	private Textbox htmlTextBoxId;
	private Textbox jsonTextBoxId;
	private Textbox caretPosSE, caretPosAS, errormsg;
	private String beeHtmlContent,beeJsonContent,beeHTMLSend,beeAutosave;
	private boolean next, dnext,isAutoSave,isSave,ismyTemplates,isSendTestMail;
	Toolbarbutton saveBtnId;
	private Button testWinId$sendTestMailBtnId;
	private Window customRowId;
	private Textbox customRowId$rowTextId;
	private Combobox customRowId$comboCategoryRowId;
	private UserDesignedCustomRowsDaoForDML userDesignedCustomRowsDaoForDML;
	private UserDesignedCustomRowsDao userDesignedCustomRowsDao;
	private String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
	static final String APP_MAIN_URL = "https://qcapp.optculture.com/subscriber/";
	static final String APP_MAIN_URL_HTTP = "http://qcapp.optculture.com/subscriber/";
	static final String MAILCONTENT_URL = "http://mailcontent.info/subscriber/";
	static final String MAILHANDLER_URL = "http://mailhandler01.info/subscriber/";
	private static final String ImageServer_Url = PropertyUtil.getPropertyValue("ImageServerUrl");

	public MytempBeeEditorController() {

		String style = "font-weight:bold;font-size:15px;color:#313031;" +"font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Templates","",style,true);
		userName = GetUser.getUserName();
		usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");

		UserActivitiesDao userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		UserActivitiesDaoForDML userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		/* if(userActivitiesDao != null) {
		      	userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_PLAIN_EDITOR,GetUser.getUserObj());
			  }*/
		if(userActivitiesDaoForDML != null) {
			userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_HTML_BEE_EDITOR,GetUser.getLoginUserObj());
		}
		this.userDesignedCustomRowsDaoForDML = (UserDesignedCustomRowsDaoForDML) SpringUtil.getBean("userDesignedCustomRowsDaoForDML");
		this.userDesignedCustomRowsDao = (UserDesignedCustomRowsDao) SpringUtil.getBean("userDesignedCustomRowsDao");
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {

		try {
			super.doAfterCompose(comp);
			//beeclientkey = PropertyUtil.getPropertyValueFromDB(Constants.BEE_CLIENT_KEYVALUE);
			beeclientkey = PropertyUtil.getPropertyValueFromDB(Constants.BEE_CLIENT_KEYVALUE_DR);

			logger.debug(" in BEE editor");

			if(logger.isDebugEnabled())logger.debug("-- just entered --"+isEdit);

			if((myTemplates ==null && systemTemplates == null) && currentUser == null){
				Redirect.goTo(PageListEnum.RM_HOME);
				return;
			}

			/*if(isEdit != null){

					editorType = myTemplates.getEditorType();
					jsonStuff = myTemplates.getJsoncontent();
					//logger.info("First time from Scrtacth...:"+jsonStuff);
					JSONObject jsontemplate = (JSONObject)JSONValue.parse(jsonStuff);
					//logger.info("Second time from Scrtacth...:"+jsontemplate);
					Clients.evalJavaScript("mytemplate ="+jsontemplate+";");

				}else {
					logger.debug("----create new tempalte------");
					htmlStuff = myTemplates != null ? myTemplates.getContent() : "";

				}

				htmlPreviewStuff = htmlStuff;
				//html.setContent (MyTempEditorController.createEditorTags(htmlStuff));
				//logger.info("t2 :"+ htmlStuff);
				//fckEditorId.setValue(htmlStuff);
				getPlaceHolderList();
				EditorController.getCouponsList();
				//phLbId.setItemRenderer(new phListRenderer());
				//phLbId.setModel(new SimpleListModel(placeHoldersList));
				//setting for this attribute to enabling ckEditor type of button in imageLibrary.zul
			 */

			if(logger.isDebugEnabled())logger.debug(" isEdit :"+isEdit);
			SystemTemplatesDao systemTemplatesDao = (SystemTemplatesDao)SpringUtil.getBean("systemTemplatesDao");
			MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
			//boolean isView = true;
			if(isEdit != null){
				//isView = false;
				saveBtnId.setDisabled(false);
				logger.debug("in edit mode==="+myTemplates.getJsoncontent());
				editorType = myTemplates.getEditorType();
				htmlStuff = myTemplates.getContent();
				jsonStuff = myTemplates.getJsoncontent();
			}
			else {
				logger.debug("----create new tempalte------");
				saveBtnId.setDisabled(true);
				if(myTemplates != null){
					editorType = myTemplates.getEditorType();
					htmlStuff = myTemplates.getContent();
					jsonStuff = myTemplates.getJsoncontent();
				}
				else {
					htmlStuff = "";
					if(jsonStuff==null) {
						jsonStuff = Constants.DEFAULT_JSON_VALUE;
					}
				}
			}
			html.setId("htmlId");
			if(htmlStuff == null) {
				logger.debug("html content is null from the session");
				return;
			}
			htmlPreviewStuff = htmlStuff;
			html.setContent (MyTempEditorController.createEditorTags(htmlStuff));
			html.setParent(appCenterDivId);
			getPlaceHolderList();
			EditorController.getCouponsList();
			//EditorController.getMilestones();
			
			
			sessionScope.setAttribute("EditorType","beeEditor");
			JSONObject jsontemplate = (JSONObject)JSONValue.parse(jsonStuff);
			Clients.evalJavaScript("beekey ='"+beeclientkey+"';");
			Clients.evalJavaScript("mytemplate ="+jsontemplate+";");
			prepareCustomRowUrlForCampaign();
			//Clients.evalJavaScript("initialcall();");

		}catch(Exception e) {
			logger.error(" Exception : ", e );
		}
	}

	private void getBlocks(String templateName){
		try{
			SystemTemplatesDao systemTemplatesDao = (SystemTemplatesDao)SpringUtil.getBean("systemTemplatesDao");
			//Menupopup editPopup = new Menupopup();//(Menupopup)Utility.getComponentById("editPopup");

			if(logger.isDebugEnabled())logger.debug("Getting the available blocks list for the layout :"+templateName);
			if(logger.isDebugEnabled())logger.debug("Getting the available blocks list for the layout :"+ systemTemplates.getDirName());
			List blocksList = systemTemplatesDao.findDivisions(templateName, systemTemplates.getDirName());
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

	public void onChange$htmlTextBoxId() {
		/*	//System.out.println("dummy event onChangeeeeeeeeeeeeeeeeeeeeee.....HTML........"+caretPosTB.getValue());
		beeHtmlContent = htmlTextBoxId.getValue();
		htmlTextBoxId.setValue("");

		logger.info("After replace Promocode and before replacing barcode"+beeHtmlContent);
		beeHtmlContent= beeHtmlContent.replace("&lt;img", "<img");
		beeHtmlContent= beeHtmlContent.replace("/&gt;", "/>");
		logger.info("After replace Barcode &lt;img and /&gt;"+beeHtmlContent);
		beeHtmlContent=Utility.replaceTextBarcodeHeightWidth(beeHtmlContent);			
		 */
		//gotoPlainMsg(htmlStuff, null);
	}

	//public void onChange$jsonTextBoxId(){
	public void onCustomEvent$jsonData(ForwardEvent event) throws JSONException{
		//logger.info("onchange$jsonTextBoxId");
		//saveJSONHTMLContent();
		
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
						  Messagebox.show("Please use 'Text Block' to save the template.", "Error", Messagebox.OK, Messagebox.ERROR); //APP-3458
						  testWinId.setVisible(false);
						  testWinId$sendTestMailBtnId.setDisabled(false);
					  }
					  isAutoSave = false;
					  return;
				  }
			  }
			  if(json!= null && !json.isEmpty()) {
				  saveJSONHTMLContent(json,html);
			} /*
				 * else if(isSendTestMail && !isAutoSave){ String emailId =
				 * testWinId$testTbId.getValue(); testWinId$msgLblId.setValue("");
				 * super.sendTestMail(html, emailId); testWinId.setVisible(false);
				 * testWinId$sendTestMailBtnId.setDisabled(false); isSendTestMail=false; }
				 */
		  }
		
		
	} 
	
	public void saveJSONHTMLContent(String json, String html){
		try {
			//onChange$htmlTextBoxId();
			beeJsonContent = json ;//jsonTextBoxId.getValue();
			beeHtmlContent = html; //htmlTextBoxId.getValue();
			//beeJsonContent = jsonTextBoxId.getValue();
			//beeHtmlContent = htmlTextBoxId.getValue();
			//htmlTextBoxId.setValue("");
			//logger.info("in autosave before................"+beeJsonContent);
			beeJsonContent=beeJsonContent.replace("\uFEFF","&nbsp;");
			//logger.info("in after replacement autosave................"+beeJsonContent);

			logger.info("After replace Promocode and before replacing barcode"+beeHtmlContent);
			beeHtmlContent= beeHtmlContent.replace("&lt;img", "<img");
			beeHtmlContent= beeHtmlContent.replace("/&gt;", "/>");
			logger.info("After replace Barcode &lt;img and /&gt;"+beeHtmlContent);
			beeHtmlContent=Utility.replaceTextBarcodeHeightWidth(beeHtmlContent);
			//OnAutoSave further actions like saving to file and database will happen only when content in editor is changed.
			//if(isSave || ismyTemplates || isSendTestMail){
				htmlTextBoxId.setValue("EMPTY_HTML");
				jsonTextBoxId.setValue("EMPTY_JSON");
		//	}
			if(beeJsonContent==null||!(beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_TEXTBOX)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_IMAGE)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_MERGE_CONTENT)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_HTML)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_BUTTON)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_SOCIAL)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_DIVIDER))){ 
				if(isSave || ismyTemplates || isSendTestMail){
					isSave=false;
					ismyTemplates=false;
					isSendTestMail=false;
					isFrom="";
					MessageUtil.setMessage("Email content cannot be empty", "color:red");
					zipImport.setVisible(false);
					//testEmailGoBtnId.setDisabled(false);
					testWinId.setVisible(false);
					winId.setVisible(false);
					return;
				}
			}
			//APP-1849
			String[] beeHtmlContentParts = beeHtmlContent.split("<a");
			if(beeHtmlContent!=null&!beeHtmlContent.equalsIgnoreCase("EMPTY_HTML")){
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
			} 
			//Done



			if(isSave){

				try {

					isSave=false;
					//htmlStuff = htmlStuffId.getValue();
					if(isFrom.equalsIgnoreCase(CLOSE_AFTER_SAVE)) saveAndClose();
					if(isFrom.equalsIgnoreCase(SAVE)) save();
/*					if(isEdit != null) {
						if(logger.isDebugEnabled())logger.debug("-- just entered --");
						htmlStuff = beeHtmlContent;
						jsonStuff = beeJsonContent;
						if(myTemplates.getFolderName().equals(Constants.NEWEDITOR_TEMPLATES_FOLDERS_DRAFTS)) {
							logger.info("------in autosave save button---");
							winId.setAttribute("autosave","t");
							htmlStuff = beeHtmlContent;
							jsonStuff = beeJsonContent;
							htmlStuffId.setValue(htmlStuff);
							htmlPreviewStuff = htmlStuffId.getValue();
							winId$htmlStuffId3.setValue("");
							winId$resLbId.setValue("");
							winId$templatNameTbId.setValue(myTemplates.getName());
							winId.setVisible(true);
							winId.setPosition("center");
							getMyTemplatesFromDb(currentUser.getUserId());
							winId.doHighlighted();
						}
						else {

							if(myTemplates != null && htmlStuff != null ) {

								myTemplates = (MyTemplates)sessionScope.getAttribute("Template");
								int confirm = Messagebox.show("Are you sure, you want to modify the template?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
								if(confirm != Messagebox.OK){
									return;
								}else {
									htmlStuff = beeHtmlContent;
									jsonStuff = beeJsonContent;
									if(htmlStuff == null || htmlStuff.isEmpty() && jsonStuff == null || jsonStuff.isEmpty()) {

										MessageUtil.setMessage("Template content can not be empty.", "color:red;");
										return;

									}
									String isValidPhStr = null;
									isValidPhStr = Utility.validatePh(htmlStuff, currentUser);

									if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){

										MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
										return ;
									}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){

										MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either  enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
										return;

									}

									String isValidCCDim = null;
									isValidCCDim = Utility.validateCouponDimensions(htmlStuff);
									if(isValidCCDim != null){
										return ;
									}
									String isValidCouponAndBarcode = null;
									isValidCouponAndBarcode = Utility.validateCCPh(htmlStuff, currentUser);
									if(isValidCouponAndBarcode != null){
										return;
									}

									if(Utility.validateHtmlSize(htmlStuff)) {
										MessageUtil.setMessage("HTML size cannot exceed 100kb. " +
												"Please remove some content.", "color:red", "TOP");
										return ;
									}
									myTemplates.setModifiedDate(Calendar.getInstance());
									myTemplates.setContent(htmlStuff);
									myTemplates.setJsoncontent(jsonStuff);

									*//**
									 * creates a html file and saves in user/MyTemplate directory
									 *//*

									if(htmlStuff.contains("href='")){
										htmlStuff = htmlStuff.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");

									}
									if(htmlStuff.contains("href=\"")){
										htmlStuff = htmlStuff.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
									}

									*//**
									 * creates a html file and saves in user/MyTemplate directory
									 *//*
									String myTemplateFilePathStr = 

											PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
											PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATE)+File.separator+myTemplates.getFolderName()+
											File.separator+myTemplates.getName()+File.separator+"email.html";


									try {
										File myTemplateFile = new File(myTemplateFilePathStr);
										File parentDir = myTemplateFile.getParentFile();

										if(parentDir.exists() ) {
											FileUtils.deleteDirectory(parentDir);
										}

										parentDir.mkdirs();

										//TODO Have to copy image files if exists
										BufferedWriter bw = new BufferedWriter(new FileWriter(myTemplateFile));
										bw.write(htmlStuff);
										bw.write(jsonStuff);
										bw.flush();
										bw.close();


										MessageUtil.setMessage("Template updated successfully.", "color:blue", "TOP");

									} catch (IOException e) {
										logger.error("** Exception : MyTemplates file creation failed",(Throwable)e);
										MessageUtil.setMessage("Encountered a problem while updating the template.", "color:red", "TOP");
									}
									MyTemplatesDaoForDML myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
									myTemplatesDaoForDML.saveOrUpdate(myTemplates);	
									Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES);
								}

							}

						} 
					}
					else{
						htmlStuff = beeHtmlContent;
						jsonStuff = beeJsonContent;
						winId.setAttribute("autosave","f");
						logger.info("------in normal save button--");		
						htmlStuffId.setValue(htmlStuff);
						htmlPreviewStuff = htmlStuffId.getValue();
						winId$htmlStuffId3.setValue("");
						winId$resLbId.setValue("");
						winId$templatNameTbId.setValue("");
						winId.setVisible(true);
						winId.setPosition("center");
						getMyTemplatesFromDb(currentUser.getUserId());
						winId.doHighlighted();

					}
*/				} catch (WrongValueException e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::" , e);;
				}

			}
			else if(isAutoSave){


				isAutoSave = false;
				if(myTemplates != null && htmlStuff != null) {

					htmlStuff = beeHtmlContent;
					jsonStuff = beeJsonContent;
					if(isEdit != null && isEdit) {
						/*						if(beeJsonContent==null||!(beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_TEXTBOX)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_IMAGE)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_MERGE_CONTENT)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_HTML)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_BUTTON)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_SOCIAL)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_DIVIDER))){ 
							MessageUtil.setMessage("Email content cannot be empty", "color:red");

							zipImport.setVisible(false);
							return;
						}*/
						myTemplates.setModifiedDate(Calendar.getInstance());
						myTemplates.setContent(htmlStuff);
						myTemplates.setJsoncontent(jsonStuff);

						/**
						 * creates a html file and saves in user/MyTemplate directory
						 */

						if(htmlStuff.contains("href='")){
							htmlStuff = htmlStuff.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");

						}
						if(htmlStuff.contains("href=\"")){
							htmlStuff = htmlStuff.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
						}

						/**
						 * creates a html file and saves in user/MyTemplate directory
						 */
						String myTemplateFilePathStr = 

								PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
								PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATE)+File.separator+myTemplates.getFolderName()+
								File.separator+myTemplates.getName()+File.separator+"email.html";
						logger.info("AutoSave myTemplateFilePathStr...:"+myTemplateFilePathStr);
						try {
							File myTemplateFile = new File(myTemplateFilePathStr);
							File parentDir = myTemplateFile.getParentFile();

							if(parentDir.exists() ) {
								FileUtils.deleteDirectory(parentDir);
							}

							parentDir.mkdirs();

							//TODO Have to copy image files if exists
							BufferedWriter bw = new BufferedWriter(new FileWriter(myTemplateFile));
							bw.write(htmlStuff);
							//bw.write(jsonStuff);
							bw.flush();
							bw.close();

						} catch (IOException e) {
							logger.error("** Exception : MyTemplates file creation failed",(Throwable)e);
							MessageUtil.setMessage("Encountered a problem while updating the template.", "color:red", "TOP");
						}


						/*MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
							myTemplatesDao.saveOrUpdate(myTemplates);*/
						MyTemplatesDaoForDML myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
						myTemplatesDaoForDML.saveOrUpdate(myTemplates);	
						logger.debug("after saving the template");
						autoSaveLbId.setValue("Last auto-saved at: "+ (MyCalendar.calendarToString(Calendar.getInstance(),MyCalendar.FORMAT_SCHEDULE_TIME,tz)).substring(13));
					}
				}
				else {
					logger.info("----in new save part of autosave");
					htmlStuff = beeHtmlContent;
					jsonStuff = beeJsonContent;
					/*if(beeJsonContent==null||!(beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_TEXTBOX)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_IMAGE)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_MERGE_CONTENT)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_HTML)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_BUTTON)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_SOCIAL)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_DIVIDER))){ 
						MessageUtil.setMessage("Email content cannot be empty", "color:red");
						zipImport.setVisible(false);
						return;
					}*/
					htmlStuffId.setValue(htmlStuff);
					htmlPreviewStuff = htmlStuffId.getValue();
					String foldernName=Constants.NEWEDITOR_TEMPLATES_FOLDERS_DRAFTS;
					String timeStamp = MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, tz);
					String  name = Constants.NEWEDITOR_TEMPLATES_FOLDERS_FOLDERNAME+timeStamp;
					if(super.autoSaveInMyTemplates(name,htmlStuff,"beeEditor",foldernName,jsonStuff)) {
						autoSaveLbId.setValue("Last auto-saved at: "+ (MyCalendar.calendarToString(Calendar.getInstance(),MyCalendar.FORMAT_SCHEDULE_TIME,tz)).substring(13));
						isEdit=true;
						myTemplates =(MyTemplates)sessionScope.getAttribute("Template");
						logger.info("---------after isedit------"+myTemplates);
					}

				}
			}else if(ismyTemplates){
				try{
					ismyTemplates=false;
					if(logger.isDebugEnabled())logger.debug("-- just entered --");

					Textbox nameTbId = (Textbox)winId.getFellowIfAny("templatNameTbId");
					beeHtmlContent = beeHtmlContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
					beeJsonContent = beeJsonContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
					boolean rtnvalue = super.saveInMyTemplates(winId,nameTbId.getValue(),beeHtmlContent,Constants.EDITOR_TYPE_BEE,winId$myTempListId,beeJsonContent);
					saveBtnId.setDisabled(false);
					if(rtnvalue == true && isSaveAndClose.equalsIgnoreCase(CLOSE_AFTER_SAVE)){
						Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES);
					}
					else logger.info("only save and saveas");
				}catch (Exception e) {
					logger.error("** Exception :", e );
				}

			}
			else if(isSendTestMail && !isAutoSave){
				/*isSendTestMail=false;
				String emailId=testEmailTbId.getValue();
				if(super.sendTestMail(beeHtmlContent, emailId)) {
					testEmailTbId.setValue("Email Address...");
					testEmailGoBtnId.setDisabled(false);
				}*/
				isSendTestMail=false;
				testWinId$msgLblId.setValue("");
				/*if(Utility.validateHtmlSize(beeHtmlContent)) {
					Messagebox.show("HTML size cannot exceed 100kb. Please reduce" +
							"the size and try again.", "Error", Messagebox.OK, Messagebox.ERROR);
				}*/
				/*long size = Utility.validateHtmlSize(beeHtmlContent);
				if(size >100) {
					String msgcontent = OCConstants.HTML_VALIDATION;
					msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
					MessageUtil.setMessage(msgcontent, "color:Blue");
				}*/
				String emailId = testWinId$testTbId.getText();
				
				String[] emailArr = emailId.split(",");
				//for (String email : emailArr) {
					String htmlContentforTest = Utility.mergeTagsForPreviewAndTestMail(beeHtmlContent,"testMail");
					super.sendTestMail(htmlContentforTest, emailId);
				//}//for
				testWinId.setVisible(false);
				testWinId$sendTestMailBtnId.setDisabled(false);

			}
			else logger.info("Reached this stage with none of these actions--OnSave,AutoSave,SaveInMytemplates,SendTestMail.");
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception in auto save : ", e);
		}		catch (Exception e) {
			logger.error("Exception in auto save : ", e);
			// TODO: handle exception
		}
	}

	public void onChange$caretPosSE() {

		beeHTMLSend =  caretPosSE.getValue();
		caretPosSE.setAttribute("isReady", "yes");


		
		//SpecialLinks AnchorTag replace with Promocode Value

				//logger.info("Before replace Promocode"+beeHTMLTestMailContent);
				String[] beeHtmlContentParts = beeHTMLSend.split("<a");

				if(beeHTMLSend!=null&!beeHTMLSend.equalsIgnoreCase("EMPTY_HTML")){
					for(int i=0;i<=beeHtmlContentParts.length-1;i++){
						String anchorTagStr="<a"+beeHtmlContentParts[i];
						//System.out.println("anchorTagStr.."+anchorTagStr);
						String promocodeLink="<a"+StringUtils.substringBetween(anchorTagStr, "<a", "</a>")+"</a>";
						//System.out.println("promocodeLink.."+promocodeLink);
						if(promocodeLink.contains("BEEFREEPROMOCODE_START")&&promocodeLink.contains("BEEFREEPROMOCODE_END")){
							String promocodeValue=StringUtils.substringBetween(beeHTMLSend, "BEEFREEPROMOCODE_START", "BEEFREEPROMOCODE_END");
							//System.out.println("promocodeValue.."+promocodeValue);
							beeHTMLSend=beeHTMLSend.replace(promocodeLink, promocodeValue);
							//logger.info("Html after Replace.."+beeHTMLTestMailContent);
						}
						//logger.info("----------.....................------------");
					}
					//logger.info("After replace Promocode and before replacing barcode"+beeHTMLTestMailContent);
					beeHTMLSend= beeHTMLSend.replace("&lt;img", "<img");
					beeHTMLSend= beeHTMLSend.replace("/&gt;", "/>");
					//logger.info("After replace Barcode &lt;img and /&gt;"+beeHTMLTestMailContent);
					
				} 

		if(!beeHTMLSend.isEmpty()&& caretPosSE.getAttribute("action").equals("test")) {
			
			testWinId$msgLblId.setValue("");
			/*if(Utility.validateHtmlSize(beeHTMLSend)) {

				Messagebox.show("HTML size cannot exceed 100kb. Please reduce" +
						"the size and try again.", "Error", Messagebox.OK, Messagebox.ERROR);
			}*/
			long size = Utility.validateHtmlSize(beeHTMLSend);
			if(size >100) {
				String msgcontent = OCConstants.HTML_VALIDATION;
				msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
				MessageUtil.setMessage(msgcontent, "color:Blue");
			}
			String emailId = testWinId$testTbId.getText();
			
			String[] emailArr = emailId.split(",");
			for (String email : emailArr) {
				String htmlContentforTest = Utility.mergeTagsForPreviewAndTestMail(beeHTMLSend,"testMail");
				super.sendTestMail(htmlContentforTest, email);
			}//for
			testWinId.setVisible(false);
			
			
		}
		if(!beeHTMLSend.isEmpty()&& caretPosSE.getAttribute("action").equals("autoSave")) {
			jsonTextBoxId.setValue(caretPosAS.getValue());
			htmlTextBoxId.setValue(caretPosSE.getValue());
			//onChange$jsonTextBoxId();
		}
		
	
		/*if(caretPosSE.getAttribute("action").equals("test")) {

				testWinId$msgLblId.setValue("");
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
				}
				String emailId = testWinId$testTbId.getText();
				String[] emailArr = emailId.split(",");
				for (String email : emailArr) {

					super.sendTestMail(caretPosSE.getValue(), email);
				}//for
				testWinId.setVisible(false);


			}*/

	}

	public void onChange$errormsg() {
		//logger.info("hi i am printing");
		errormsg.setValue("");
		MessageUtil.setMessage("You have encountered an error viewing this page, please refresh to try again. \n If you continue having viewing difficulties, please submit a ticket to support@optculture.com", "color:blue");
	}
	
	//public void onChange$caretPosAS() {
	public void onCustomEventAutoSave$jsonData(ForwardEvent event) throws JSONException{


		isAutoSave=true;
		//Clients.evalJavaScript("bee.save()");
		caretPosSE.setAttribute("action", "autoSave");
		Clients.evalJavaScript("bee.send()");
		/*		
		//System.out.println(caretPosAS.getValue());
		beeAutosave = caretPosAS.getValue();

		if(isEdit != null && isEdit) {


			if(myTemplates != null && htmlStuff != null) {


				jsonStuff = beeAutosave;

				myTemplates.setModifiedDate(Calendar.getInstance());
				myTemplates.setContent(htmlStuff);
				myTemplates.setJsoncontent(jsonStuff);

		 *//**
						 creates a html file and saves in user/MyTemplate directory
						 /*

						if(htmlStuff.contains("href='")){
							htmlStuff = htmlStuff.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");

						}
						if(htmlStuff.contains("href=\"")){
							htmlStuff = htmlStuff.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
						}

						/**
		  * creates a html file and saves in user/MyTemplate directory
		  *//*
				String myTemplateFilePathStr = 

						PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
						PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATE)+File.separator+myTemplates.getFolderName()+
						File.separator+myTemplates.getName()+File.separator+"email.html";

				try {
					File myTemplateFile = new File(myTemplateFilePathStr);
					File parentDir = myTemplateFile.getParentFile();

					if(parentDir.exists() ) {
						FileUtils.deleteDirectory(parentDir);
					}

					parentDir.mkdirs();

					//TODO Have to copy image files if exists
					BufferedWriter bw = new BufferedWriter(new FileWriter(myTemplateFile));
					//bw.write(htmlStuff);
					bw.write(jsonStuff);
					bw.flush();
					bw.close();

				} catch (IOException e) {
					logger.error("** Exception : MyTemplates file creation failed",(Throwable)e);
					MessageUtil.setMessage("Encountered a problem while updating the template.", "color:red", "TOP");
				}


				/*MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
					myTemplatesDao.saveOrUpdate(myTemplates);
				MyTemplatesDaoForDML myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
				myTemplatesDaoForDML.saveOrUpdate(myTemplates);	
				logger.debug("after saving the template");
				autoSaveLbId.setValue("Last auto-saved at :"+MyCalendar.calendarToString(Calendar.getInstance(),
						MyCalendar.FORMAT_SCHEDULE_TIME,tz));
			}
		}
		else {
			logger.info("----in new save part of autosave");
			jsonStuff = beeAutosave;
			htmlStuffId.setValue(htmlStuff);
			htmlPreviewStuff = htmlStuffId.getValue();
			String foldernName=Constants.NEWEDITOR_TEMPLATES_FOLDERS_DRAFTS;
			String timeStamp = MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, tz);
			String  name = Constants.NEWEDITOR_TEMPLATES_FOLDERS_FOLDERNAME+timeStamp;
			if(super.autoSaveInMyTemplates(name,htmlStuff,"beeEditor",foldernName,jsonStuff)) {
				autoSaveLbId.setValue("Last auto-saved at :"+MyCalendar.calendarToString(Calendar.getInstance(),
						MyCalendar.FORMAT_SCHEDULE_TIME,tz));
				isEdit=true;
				myTemplates =(MyTemplates)sessionScope.getAttribute("Template");
				logger.info("---------after isedit------"+myTemplates);
			}

		}
		TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
		autoSaveLbId.setValue("Last auto-saved at: "+ MyCalendar.calendarToString(Calendar.getInstance(),
				MyCalendar.FORMAT_SCHEDULE_TIME,tz));
		   */
	}



	public void zipImport(Media media) {
		try {
			logger.debug("just entered");
			MessageUtil.clearMessage();
			Media m = (Media)media;
			String tempName=null;

			if(myTemplates != null){
				tempName=myTemplates.getName();
			}else {
				logger.error("** Exception : Campaign object is null. **");
				tempName = m.getName();
				//return;
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
				invalidFiles = ZipImport.unzipFileIntoMyTempDirectory(zipFile, unzipPath, tempName, millis);

				if(invalidFiles.indexOf('\n') > 1) { 
					Messagebox.show("File uploaded successfully. However the following" +
							" invalid files will be ignored.\n\r"+invalidFiles);
				}
				else if(invalidFiles.indexOf(',')>0){
					MessageUtil.setMessage("File uploaded successfully. However the " +
							"following invalid files will be ignored."+invalidFiles, 
							"color:blue", "TOP");
				}

				setHTMLFromFolder(unzipPath+File.separator+tempName+File.separator+millis,zipName);

				logger.debug("zipName :"+zipName+"  millis :"+millis+" zipPath:"+zipPath+
						" unzipPath:"+unzipPath);
			}	
			catch (Exception e) {
				logger.error("Exception ::" , e);
				logger.error("** Exception while extracting files from Zip file to users Parent Directory :"+e+" **");
			}
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			logger.error("** Exception while uploading zip file : "+e.getMessage()+" **");
		}
	}

	public void setHTMLFromFolder(String filePath,String tempName) {
		logger.debug("Just Entered ..."+filePath);
		File htmlFile = new File(filePath+File.separator+htmlFilePath);

		String htmlParentDir = htmlFile.getParent();
		logger.debug("htmlParentDir="+htmlParentDir);

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
			String userDataDir = "WebContent"+File.separator+"UserData"+File.separator+userName +File.separator+"Email"+File.separator+tempName;

			htmlParentDir =htmlParentDir.substring(htmlParentDir.indexOf(userDataDir) + userDataDir.length());

			logger.debug("After htmlParentDir="+htmlParentDir);

			String replaceUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+userDataDir+htmlParentDir+File.separator;
			logger.debug("replace Url : "+replaceUrl);

			r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
			m = r.matcher(sb);
			StringBuffer outsb = new StringBuffer();
			while (m.find()) {
				String url = m.group(1).trim();
				String replaceStr = url;
				if(!url.contains("http://")){
					replaceStr =" src=\""+ replaceUrl+url;
					//						replaceStr = Utility.encodeSpace(replaceStr);
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
			if(myTemplates != null){

				myTemplates.setContent(outsb.toString());
			}
			fckEditorId.setValue(outsb.toString());

		} catch (Exception e) {
			logger.error("Error while setting html in Editor",(Throwable)e);
		}

	}


	TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");

	public void onTimer$mytempAutoSaveId() {
		isAutoSave=true;
		Clients.evalJavaScript("bee.save()");

		/*	if(isEdit != null && isEdit) {


					if(myTemplates != null && htmlStuff != null) {

							htmlStuff = beeHtmlContent;
							jsonStuff = beeJsonContent;

							myTemplates.setModifiedDate(Calendar.getInstance());
							myTemplates.setContent(htmlStuff);
							myTemplates.setJsoncontent(jsonStuff);

		 *//**
		 * creates a html file and saves in user/MyTemplate directory
		 *//*

							if(htmlStuff.contains("href='")){
								htmlStuff = htmlStuff.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");

							}
							if(htmlStuff.contains("href=\"")){
								htmlStuff = htmlStuff.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
							}

		  *//**
		  * creates a html file and saves in user/MyTemplate directory
		  *//*
							String myTemplateFilePathStr = 

									PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
									PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATE)+File.separator+myTemplates.getFolderName()+
									File.separator+myTemplates.getName()+File.separator+"email.html";

							try {
								File myTemplateFile = new File(myTemplateFilePathStr);
								File parentDir = myTemplateFile.getParentFile();

								if(parentDir.exists() ) {
									FileUtils.deleteDirectory(parentDir);
								}

								parentDir.mkdirs();

								//TODO Have to copy image files if exists
								BufferedWriter bw = new BufferedWriter(new FileWriter(myTemplateFile));
								bw.write(htmlStuff);
								//bw.write(jsonStuff);
								bw.flush();
								bw.close();

						} catch (IOException e) {
							logger.error("** Exception : MyTemplates file creation failed",(Throwable)e);
							MessageUtil.setMessage("Encountered a problem while updating the template.", "color:red", "TOP");
						}


						MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
						myTemplatesDao.saveOrUpdate(myTemplates);
						MyTemplatesDaoForDML myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
						myTemplatesDaoForDML.saveOrUpdate(myTemplates);	
						logger.debug("after saving the template");
						autoSaveLbId.setValue("Last auto-saved at :"+MyCalendar.calendarToString(Calendar.getInstance(),
								MyCalendar.FORMAT_SCHEDULE_TIME,tz));
				}
			}
			else {
				logger.info("----in new save part of autosave");
				htmlStuff = beeHtmlContent;
				jsonStuff = beeJsonContent;
				htmlStuffId.setValue(htmlStuff);
				htmlPreviewStuff = htmlStuffId.getValue();
				String foldernName=Constants.NEWEDITOR_TEMPLATES_FOLDERS_DRAFTS;
				String timeStamp = MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, tz);
				String  name = Constants.NEWEDITOR_TEMPLATES_FOLDERS_FOLDERNAME+timeStamp;
				if(super.autoSaveInMyTemplates(name,htmlStuff,"beeEditor",foldernName,jsonStuff)) {
					autoSaveLbId.setValue("Last auto-saved at :"+MyCalendar.calendarToString(Calendar.getInstance(),
							MyCalendar.FORMAT_SCHEDULE_TIME,tz));
					isEdit=true;
					myTemplates =(MyTemplates)sessionScope.getAttribute("Template");
					logger.info("---------after isedit------"+myTemplates);
				}

			}*/
	}

	//public void saveEmail(String htmlStuff) {
	private Textbox winId$templatNameTbId,htmlStuffId,winId$htmlStuffId3;
	private Listbox myTemplatesListId;
	public void onClick$saveBtnId() {

		isSave=true;
		//Clients.evalJavaScript("bee.save()");
		isFrom=SAVE;
		Clients.evalJavaScript("bee.save()");
		/*try {


				//htmlStuff = htmlStuffId.getValue();
				if(isEdit != null) {
					if(logger.isDebugEnabled())logger.debug("-- just entered --");
					htmlStuff = beeHtmlContent;
					jsonStuff = beeJsonContent;
					if(beeJsonContent==null||!(beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_TEXTBOX)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_IMAGE)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_MERGE_CONTENT)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_HTML)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_BUTTON)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_SOCIAL)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_DIVIDER))){ 
						MessageUtil.setMessage("Email content cannot be empty", "color:red");
						//isContentEmpty=true;
						//htmlTextBoxId.setValue("EMPTY_HTML");
						//jsonTextBoxId.setValue("EMPTY_JSON");
						zipImport.setVisible(false);
						return;
					}
					if(myTemplates.getFolderName().equals(Constants.MYTEMPATES_FOLDERS_DRAFTS)) {
						logger.info("------in autosave save button---");
						winId.setAttribute("autosave","t");
						htmlStuff = beeHtmlContent;
						jsonStuff = beeJsonContent;
						htmlStuffId.setValue(htmlStuff);
						htmlPreviewStuff = htmlStuffId.getValue();
						winId$htmlStuffId3.setValue("");
						winId$resLbId.setValue("");
						winId$templatNameTbId.setValue(myTemplates.getName());
						winId.setVisible(true);
						winId.setPosition("center");
						getMyTemplatesFromDb(currentUser.getUserId());
						winId.doHighlighted();
					}
					else {

					if(myTemplates != null && htmlStuff != null ) {

						myTemplates = (MyTemplates)sessionScope.getAttribute("Template");
						int confirm = Messagebox.show("Are you sure, you want to modify the template?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						if(confirm != Messagebox.OK){
							return;
						}else {
							htmlStuff = beeHtmlContent;
							jsonStuff = beeJsonContent;
							if(htmlStuff == null || htmlStuff.isEmpty() && jsonStuff == null || jsonStuff.isEmpty()) {

								MessageUtil.setMessage("Template content can not be empty.", "color:red;");
								return;

							}
							String isValidPhStr = null;
							isValidPhStr = Utility.validatePh(htmlStuff, currentUser);

							if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){

								MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
								return ;
							}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){

								MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either  enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
								return;

							}

							String isValidCCDim = null;
							isValidCCDim = Utility.validateCouponDimensions(htmlStuff);
							if(isValidCCDim != null){
								return ;
							}
							String isValidCouponAndBarcode = null;
							isValidCouponAndBarcode = Utility.validateCCPh(htmlStuff, currentUser);
							if(isValidCouponAndBarcode != null){
								return;
							}

							if(Utility.validateHtmlSize(htmlStuff)) {
								MessageUtil.setMessage("HTML size cannot exceed 100kb. " +
										"Please remove some content.", "color:red", "TOP");
								return ;
							}
							myTemplates.setModifiedDate(Calendar.getInstance());
							myTemplates.setContent(htmlStuff);
							myTemplates.setJsoncontent(jsonStuff);

		 *//**
		 * creates a html file and saves in user/MyTemplate directory
		 *//*

							if(htmlStuff.contains("href='")){
								htmlStuff = htmlStuff.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");

							}
							if(htmlStuff.contains("href=\"")){
								htmlStuff = htmlStuff.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
							}

							String myTemplateFilePathStr = 
								PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
								PropertyUtil.getPropertyValue("userTemplatesDirectory")+File.separator+foldernName+
								File.separator+myTemplates.getName()+File.separator+"email.html";
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

		  *//**
		  * creates a html file and saves in user/MyTemplate directory
		  *//*
							String myTemplateFilePathStr = 

									PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
									PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATE)+File.separator+myTemplates.getFolderName()+
									File.separator+myTemplates.getName()+File.separator+"email.html";


							try {
								File myTemplateFile = new File(myTemplateFilePathStr);
								File parentDir = myTemplateFile.getParentFile();

								if(parentDir.exists() ) {
									FileUtils.deleteDirectory(parentDir);
								}

								parentDir.mkdirs();

								//TODO Have to copy image files if exists
								BufferedWriter bw = new BufferedWriter(new FileWriter(myTemplateFile));
								bw.write(htmlStuff);
								bw.write(jsonStuff);
								bw.flush();
								bw.close();


								MessageUtil.setMessage("Template updated successfully.", "color:blue", "TOP");

						} catch (IOException e) {
							logger.error("** Exception : MyTemplates file creation failed",(Throwable)e);
							MessageUtil.setMessage("Encountered a problem while updating the template.", "color:red", "TOP");
						}


						MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
						myTemplatesDao.saveOrUpdate(myTemplates);
						MyTemplatesDaoForDML myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
						myTemplatesDaoForDML.saveOrUpdate(myTemplates);	
						Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES);
					}

				}

				} 
				}
				else{
					htmlStuff = beeHtmlContent;
					jsonStuff = beeJsonContent;
					if(beeJsonContent==null||!(beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_TEXTBOX)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_IMAGE)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_MERGE_CONTENT)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_HTML)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_BUTTON)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_SOCIAL)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_DIVIDER))){ 
						MessageUtil.setMessage("Email content cannot be empty", "color:red");
						//isContentEmpty=true;
						//htmlTextBoxId.setValue("EMPTY_HTML");
						//jsonTextBoxId.setValue("EMPTY_JSON");
						zipImport.setVisible(false);
						return;
					}
					winId.setAttribute("autosave","f");
					logger.info("------in normal save button--");		
					htmlStuffId.setValue(htmlStuff);
					htmlPreviewStuff = htmlStuffId.getValue();
					winId$htmlStuffId3.setValue("");
					winId$resLbId.setValue("");
					winId$templatNameTbId.setValue("");
					winId.setVisible(true);
					winId.setPosition("center");
					getMyTemplatesFromDb(currentUser.getUserId());
					winId.doHighlighted();

				}
			} catch (WrongValueException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);;
			}*/





	}// saveEmail()
	public void onClick$myTemplatesSubmtBtnId$winId(){

		//try{
		ismyTemplates=true;
		Clients.evalJavaScript("bee.save()");
		/*
			if(logger.isDebugEnabled())logger.debug("-- just entered --");

			Textbox nameTbId = (Textbox)winId.getFellowIfAny("templatNameTbId");

			super.saveInMyTemplates(winId,nameTbId.getValue(),beeHtmlContent,Constants.EDITOR_TYPE_BEE,winId$myTempListId,beeJsonContent);
		}catch (Exception e) {
			logger.error("** Exception :", e );
		}
		 */	}


	public void onClose$winId(Event event) {
		self.setVisible(false);
		event.stopPropagation();
	}
	public void onClick$nextBtnId() throws Exception {
		isFrom=SAVE_AND_CLOSE;
		//onClick$saveBtnId() ;
		isSave=true;
		Clients.evalJavaScript("bee.save()");
	}

	
	public void onClick$sendTestBtnId() {
		
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

	/*public void onClick$testEmailGoBtnId() throws Exception {
		testEmailGoBtnId.setDisabled(true);
		String emailId=testEmailTbId.getValue();
		if( emailId.equals("Email Address...") || emailId.isEmpty() ){
			MessageUtil.setMessage("There is no mail id to send a test mail.", "color:red");
			testEmailTbId.setValue("Email Address...");
			testEmailGoBtnId.setDisabled(false);
			return;
		}
		boolean isValid = validateEmailAddr(emailId);
		if(isValid){			
			isSendTestMail=true;
			Clients.evalJavaScript("bee.save()");
		}
		else{
			logger.info("Invalid email entered "+emailId);
			testEmailGoBtnId.setDisabled(false);
		}
	}*/
	/*	public void onClick$testEmailGoBtnId() throws Exception {


		String emailId=testEmailTbId.getValue();
		if( emailId.equals("Email Address...") || emailId.isEmpty() ){
			MessageUtil.setMessage("There is no mail id to send a test mail.", "color:red");
			testEmailTbId.setValue("Email Address...");
			return;
		}

		boolean isValid = validateEmailAddr(emailId);
		String isReady = (String)caretPosSE.getAttribute("isReady");
		if(isValid){
			if( !isReady.equals("yes")){


			}else{


				//super.sendTestMail(caretPosSE.getValue(), emailId);
				if(super.sendTestMail(caretPosSE.getValue(), emailId)) {
					//testMailTbId.setValue("");
					testEmailTbId.setValue("Email Address...");
				}

			}





		}


	}*/
	/*public boolean validateEmailAddr(String emailId) {
		//super.sendTestMail(fckEditorId.getValue(), testEmailTbId.getValue());
		boolean isValid = true;			
		if(emailId != null && emailId.trim().length() > 0) {

			if(logger.isDebugEnabled())logger.debug("Sending the test mail....");

			MessageUtil.clearMessage();
			//testWinId$msgLblId.setValue("");
			String[] emailArr = null;
			if(isValid) {

				emailArr = emailId.split(",");
				for (String email : emailArr) {

					if(!Utility.validateEmail(email.trim())){
						MessageUtil.setMessage("Invalid Email address:'"+email+"'", "color:red");
						isValid = false;
						break;
					}

				}//for
			}

		}else {
			isValid = false;
			MessageUtil.setMessage("Invalid Email address", "color:red");
		}
		return isValid;
	}*/
	
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
						}
						else {
							//Testing for invalid email domain-APP-308
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
	
	public void onClick$cancelSendTestMailBtnId$testWinId() {

		testWinId$msgLblId.setValue("");
		testWinId$msgLblId.setValue("");
		testWinId.setVisible(false);

	}
	
	public void onFocus$testEmailTbId() throws Exception {
		String mail=testEmailTbId.getValue(); 
		if(mail.equals("Email Address...") || mail.equals("")){
			testEmailTbId.setValue("");

		} 
	}
	
	
	// changes here

	public void onBlur$testEmailTbId() throws Exception {

		String mail=testEmailTbId.getValue();
		//////logger.debug("here in on blur method mail id "+mail);
		if(mail.equals("Email Address...") || mail.equals("")){
			testEmailTbId.setValue("Email Address...");

		}
	}

	// changes here
	/*	public void onFocus$testEmailTbId2() throws Exception {

		caretPosSE.removeAttribute("action");
		caretPosSE.removeAttribute("isReady");

		caretPosSE.setAttribute("action", "test");
		Clients.evalJavaScript("bee.send()");
	}*/

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
							MessageUtil.setMessage("HTML file size cannot exceed 100kb.","color:red","TOP");
							return false;
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
				fckEditorId.setValue(content);
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

	public void onClick$spamScrBtnId() throws Exception {
		// super.checkSpam(fckEditorId.getValue(),true);
	}

	public void onClick$reloadTblId() throws Exception {
		try{
			logger.debug("reloadBtn Id called");
			int confirm = Messagebox.show("Do you want to reload the email?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm != 1){
				return;
			}else{
				//fckEditorId.setValue("");
				//if( !htmlStuff.trim().isEmpty()) {
				String	jsondb = null;
				jsondb = myTemplates.getJsoncontent();
				//fckEditorId.setValue(htmlStuff);
				JSONObject jsontemplate = (JSONObject)JSONValue.parse(jsondb);	
				logger.info("jsontoload............"+jsontemplate);		
				Clients.evalJavaScript("bee.load("+jsontemplate+");");
				//}
			}
		}catch (Exception e) {
			logger.error("** Exception : " , e );
		}

	}

	/*
	 * public void onClick$backBtnId() throws Exception { super.back(); }
	 */
	private Div zipImport$importZipFileDivId,zipImport$saveMyTemplateDivId,zipImport$fetchUrlDivId;
	public void onClick$zipImportTlbBtnId() throws Exception {
		// zipImport$saveMyTemplateDivId.setVisible(false);
		zipImport$resultLblId.setValue("");
		zipImport$selectedFileTbId.setValue("");
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
		// zipImport$saveMyTemplateDivId.setVisible(false);
		zipImport$importZipFileDivId.setVisible(false);
		zipImport$fetchUrlDivId.setVisible(true);
		zipImport.setTitle("Fetch Html from Url");
		zipImport.setVisible(true);
		zipImport.doHighlighted();

	} //onClick$urlToFetchHtmlTBtnId


	private Window previewIframeWin; 
	private  Iframe previewIframeWin$iframeId;
	public void onClick$plainPreviewImgId() {
		logger.debug("entered into html preview....");
		String htmlContent="";
		if(isEdit != null) {
			//htmlContent=myTemplates.getContent();
			//String htmlContent=campaign.getHtmlText();
			Clients.evalJavaScript("bee.preview()");

		}else{
			//htmlContent =fckEditorId.getValue();
			Clients.evalJavaScript("bee.preview()");

		}
		//Utility.showPreview(previewIframeWin$iframeId,currentUser.getUserName(), htmlContent);
		//previewIframeWin.setVisible(true);


	}
	private Listbox winId$myTempListId;
	private final String USER_NEW_EDITOR_TEMPLATE = "userNewEditorTemplatesDirectory";
	public void getMyTemplatesFromDb(Long userId){

		String userTemplatesDirectory = PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATE);
		try {
			Components.removeAllChildren(winId$myTempListId);
			//MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");

			File myTemp = null;
			File[] templateList = null;
			String myTempPathStr = PropertyUtil.getPropertyValue("usersParentDirectory")+
					File.separator+ currentUser.getUserName()+userTemplatesDirectory;
			logger.debug("path is"+myTempPathStr);

			myTemp= new File(myTempPathStr);
			templateList = myTemp.listFiles();
			for (Object obj: templateList) {
				final File template = (File)obj;
				String folderName=template.getName();
				Listitem item = new Listitem();
				item.setLabel(folderName);
				item.getIndex();
				item.setParent(winId$myTempListId);
			}

			if(winId$myTempListId.getItemCount() > 0) {
				List<Listitem> foldersList = winId$myTempListId.getItems();
				int index=0;
				if(myTemplates!=null) {
					for(Listitem li: foldersList) {

						if(li.getLabel().equalsIgnoreCase(Constants.MYTEMPATES_FOLDERS_DRAFTS)) {
							index=li.getIndex();
							winId$myTempListId.setSelectedIndex(index);
							break;
						}
					} 
				}
				else {
					winId$myTempListId.setSelectedIndex(index);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}

	}

	private Label winId$resLbId;

	private final String SAVE_AND_CLOSE = "saveAndClose";
	private final String SAVE_AS = "saveAs";
	private final String SAVE = "save";
	private String isFrom="";
	public void saveaAs(){
		try {
			isFrom="";
			if(isEdit != null && myTemplates.getName()!=null) {
				if(logger.isDebugEnabled())logger.debug("-- just entered --");
				htmlStuff = beeHtmlContent;
				jsonStuff = beeJsonContent;
				logger.info("------in autosave save button---");
				winId.setAttribute("autosave","t");
				htmlStuff = beeHtmlContent;
				jsonStuff = beeJsonContent;
				htmlStuffId.setValue(htmlStuff);
				htmlPreviewStuff = htmlStuffId.getValue();
				winId$htmlStuffId3.setValue("");
				winId$resLbId.setValue("");
				winId$templatNameTbId.setValue(myTemplates.getName());
				winId.setVisible(true);
				winId.setPosition("center");
				getMyTemplatesFromDb(currentUser.getUserId());
				winId.doHighlighted();
			}
			else{
				htmlStuff = beeHtmlContent;
				jsonStuff = beeJsonContent;
				winId.setAttribute("autosave","f");
				logger.info("------in normal save button--");		
				htmlStuffId.setValue(htmlStuff);
				htmlPreviewStuff = htmlStuffId.getValue();
				winId$htmlStuffId3.setValue("");
				winId$resLbId.setValue("");
				winId$templatNameTbId.setValue("");
				winId.setVisible(true);
				winId.setPosition("center");
				getMyTemplatesFromDb(currentUser.getUserId());
				winId.doHighlighted();

			}
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
	}

	public void onClick$saveAsId() {

		saveaAs();
		/*isSave=true;
		isFrom=SAVE_AS;
		Clients.evalJavaScript("bee.save()");
*/	}

	public void save(){
		try {
			
			isFrom="";
			if(isEdit != null && myTemplates != null && htmlStuff != null ) {
				if(logger.isDebugEnabled())logger.debug("-- just entered --");
				htmlStuff = beeHtmlContent;
				jsonStuff = beeJsonContent;
				//logger.info("jsonStuff in save....................."+jsonStuff);
				
				jsonStuff=jsonStuff.replace("\uFEFF","&nbsp;");
				
				//logger.info("jsonStuff in after replacement....................."+jsonStuff);
				myTemplates = (MyTemplates)sessionScope.getAttribute("Template");
				int confirm = Messagebox.show("Are you sure, you want to modify the template?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm != Messagebox.OK){
					return;
				}else {
					htmlStuff = beeHtmlContent;
					jsonStuff = beeJsonContent;
					//logger.info("jsonStuff in save....................."+jsonStuff);
					
					jsonStuff=jsonStuff.replace("\uFEFF","&nbsp;");
					
					//logger.info("jsonStuff in after replacement....................."+jsonStuff);
					if(htmlStuff == null || htmlStuff.isEmpty() && jsonStuff == null || jsonStuff.isEmpty()) {

						MessageUtil.setMessage("Template content can not be empty.", "color:red;");
						return;

					}
					String isValidPhStr = null;
					isValidPhStr = Utility.validatePh(htmlStuff, currentUser);

					if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){

						MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
						return ;
					}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){

						MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either  enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
						return;

					}

					String isValidCCDim = null;
					isValidCCDim = Utility.validateCouponDimensions(htmlStuff);
					if(isValidCCDim != null){
						return ;
					}
					String isValidCouponAndBarcode = null;
					isValidCouponAndBarcode = Utility.validateCCPh(htmlStuff, currentUser);
					if(isValidCouponAndBarcode != null){
						return;
					}

					/*if(Utility.validateHtmlSize(htmlStuff)) {
						MessageUtil.setMessage("HTML size cannot exceed 100kb. " +
								"Please remove some content.", "color:red", "TOP");
						return ;
					}*/
					long size = Utility.validateHtmlSize(htmlStuff);
					if(size >100) {
						String msgcontent = OCConstants.HTML_VALIDATION;
						msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
						MessageUtil.setMessage(msgcontent, "color:Blue");
					}
					//htmlStuff=PrepareFinalHtml.replaceImgURL(htmlStuff, currentUser.getUserName());
					htmlStuff = htmlStuff.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
					jsonStuff = jsonStuff.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
					myTemplates.setModifiedDate(Calendar.getInstance());
					myTemplates.setContent(htmlStuff);
					myTemplates.setJsoncontent(jsonStuff);

					/**
					 * creates a html file and saves in user/MyTemplate directory
					 */

					if(htmlStuff.contains("href='")){
						htmlStuff = htmlStuff.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");

					}
					if(htmlStuff.contains("href=\"")){
						htmlStuff = htmlStuff.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
					}


					/**
					 * creates a html file and saves in user/MyTemplate directory
					 */
					String myTemplateFilePathStr = 

							PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
							PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATE)+File.separator+myTemplates.getFolderName()+
							File.separator+myTemplates.getName().trim()+File.separator+"email.html";
					logger.info("myTemplateFilePathStr...:"+myTemplateFilePathStr);

					try {
						File myTemplateFile = new File(myTemplateFilePathStr);
						File parentDir = myTemplateFile.getParentFile();

						if(parentDir.exists() ) {
							FileUtils.deleteDirectory(parentDir);
						}

						parentDir.mkdirs();

						//TODO Have to copy image files if exists
						BufferedWriter bw = new BufferedWriter(new FileWriter(myTemplateFile));
						bw.write(htmlStuff);
						//bw.write(jsonStuff);
						bw.flush();
						bw.close();


						MessageUtil.setMessage("Template updated successfully.", "color:blue", "TOP");

					} catch (IOException e) {
						logger.error("** Exception : MyTemplates file creation failed",(Throwable)e);
						MessageUtil.setMessage("Encountered a problem while updating the template.", "color:red", "TOP");
					}


					MyTemplatesDaoForDML myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
					myTemplatesDaoForDML.saveOrUpdate(myTemplates);	
					//Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES);
				}

			}
			else logger.info("Save Button is enable only in Edit mode---> Edit!=null");

		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}

	}
	
	private final String CLOSE_AFTER_SAVE="saveAndClose";
	private String isSaveAndClose="onlySave";
	public void saveAndClose(){
		
		
		try {
			
			//isSave=false;
			//htmlStuff = htmlStuffId.getValue();
			isFrom="";
			isSaveAndClose=CLOSE_AFTER_SAVE;
			if(isEdit != null) {
				if(logger.isDebugEnabled())logger.debug("-- just entered --");
				htmlStuff = beeHtmlContent;
				jsonStuff = beeJsonContent;
				//logger.info("jsonStuff in save....................."+jsonStuff);
				
				jsonStuff=jsonStuff.replace("\uFEFF","&nbsp;");
				
				//logger.info("jsonStuff in after replacement....................."+jsonStuff);
				if(myTemplates.getFolderName().equals(Constants.NEWEDITOR_TEMPLATES_FOLDERS_DRAFTS)) {
					logger.info("------in autosave save button---");
					winId.setAttribute("autosave","t");
					htmlStuff = beeHtmlContent;
					jsonStuff = beeJsonContent;
					//logger.info("jsonStuff in save....................."+jsonStuff);
					
					jsonStuff=jsonStuff.replace("\uFEFF","&nbsp;");
					jsonStuff = jsonStuff.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
					//htmlStuff=PrepareFinalHtml.replaceImgURL(htmlStuff, currentUser.getUserName());
					htmlStuff = htmlStuff.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
					//logger.info("jsonStuff in after replacement....................."+jsonStuff);
					htmlStuffId.setValue(htmlStuff);
					htmlPreviewStuff = htmlStuffId.getValue();
					winId$htmlStuffId3.setValue("");
					winId$resLbId.setValue("");
					String name = Constants.NEWEDITOR_TEMPLATES_FOLDERS_FOLDERNAME;
					if (myTemplates.getName().isEmpty() || myTemplates.getName().startsWith(name)){
						winId$templatNameTbId.setValue(myTemplates.getName().trim());
						logger.info("mytemplate..."+myTemplates.getName().trim());
						winId.setVisible(true);
						winId.setPosition("center");
						getMyTemplatesFromDb(currentUser.getUserId());
						winId.doHighlighted();
					}else
					{
						save();
						Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES);
					}
				}
				else {

					if(myTemplates != null && htmlStuff != null ) {

						myTemplates = (MyTemplates)sessionScope.getAttribute("Template");
						int confirm = Messagebox.show("Are you sure, you want to modify the template?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						if(confirm != Messagebox.OK){
							return;
						}else {
							htmlStuff = beeHtmlContent;
							jsonStuff = beeJsonContent;
							//logger.info("jsonStuff in save....................."+jsonStuff);
							
							jsonStuff=jsonStuff.replace("\uFEFF","&nbsp;");
							
							//logger.info("jsonStuff in after replacement....................."+jsonStuff);
							if(htmlStuff == null || htmlStuff.isEmpty() && jsonStuff == null || jsonStuff.isEmpty()) {

								MessageUtil.setMessage("Template content can not be empty.", "color:red;");
								return;

							}
							String isValidPhStr = null;
							isValidPhStr = Utility.validatePh(htmlStuff, currentUser);

							if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){

								MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
								return ;
							}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){

								MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either  enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
								return;

							}

							String isValidCCDim = null;
							isValidCCDim = Utility.validateCouponDimensions(htmlStuff);
							if(isValidCCDim != null){
								return ;
							}
							String isValidCouponAndBarcode = null;
							isValidCouponAndBarcode = Utility.validateCCPh(htmlStuff, currentUser);
							if(isValidCouponAndBarcode != null){
								return;
							}

							/*if(Utility.validateHtmlSize(htmlStuff)) {
								MessageUtil.setMessage("HTML size cannot exceed 100kb. " +
										"Please remove some content.", "color:red", "TOP");
								return ;
							}*/
							long size = Utility.validateHtmlSize(htmlStuff);
							if(size >100) {
								String msgcontent = OCConstants.HTML_VALIDATION;
								msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
								MessageUtil.setMessage(msgcontent, "color:Blue");
							}
							//htmlStuff = PrepareFinalHtml.replaceImgURL(htmlStuff, currentUser.getUserName());
							htmlStuff = htmlStuff.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
									.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
									.replace(MAILHANDLER_URL, ImageServer_Url);
							jsonStuff = jsonStuff.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
									.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
									.replace(MAILHANDLER_URL, ImageServer_Url);
							myTemplates.setModifiedDate(Calendar.getInstance());
							myTemplates.setContent(htmlStuff);
							myTemplates.setJsoncontent(jsonStuff);

							/**
							 * creates a html file and saves in user/MyTemplate directory
							 */

							if(htmlStuff.contains("href='")){
								htmlStuff = htmlStuff.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");

							}
							if(htmlStuff.contains("href=\"")){
								htmlStuff = htmlStuff.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
							}

							/*String myTemplateFilePathStr = 
								PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
								PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATE)+File.separator+myTemplates.getFolderName()+
								File.separator+myTemplates.getName()+File.separator+"email.html";
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
								bw.close();*/

							/**
							 * creates a html file and saves in user/MyTemplate directory
							 */
							String myTemplateFilePathStr = 

									PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
									PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATE)+File.separator+myTemplates.getFolderName()+
									File.separator+myTemplates.getName()+File.separator+"email.html";


							try {
								File myTemplateFile = new File(myTemplateFilePathStr);
								File parentDir = myTemplateFile.getParentFile();

								if(parentDir.exists() ) {
									FileUtils.deleteDirectory(parentDir);
								}

								parentDir.mkdirs();
								
								//TODO Have to copy image files if exists
								BufferedWriter bw = new BufferedWriter(new FileWriter(myTemplateFile));
								bw.write(htmlStuff);
								//bw.write(jsonStuff);
								bw.flush();
								bw.close();


								MessageUtil.setMessage("Template updated successfully.", "color:blue", "TOP");

							} catch (IOException e) {
								logger.error("** Exception : MyTemplates file creation failed",(Throwable)e);
								MessageUtil.setMessage("Encountered a problem while updating the template.", "color:red", "TOP");
							}


							/*MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
						myTemplatesDao.saveOrUpdate(myTemplates);*/
							MyTemplatesDaoForDML myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
							myTemplatesDaoForDML.saveOrUpdate(myTemplates);	
							Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES);
						}

					}

				} 
			}
			else{
				htmlStuff = beeHtmlContent;
				jsonStuff = beeJsonContent;
				//logger.info("jsonStuff in save....................."+jsonStuff);
				
				jsonStuff=jsonStuff.replace("\uFEFF","&nbsp;");
				jsonStuff = jsonStuff.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
						.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
						.replace(MAILHANDLER_URL, ImageServer_Url);
				//logger.info("jsonStuff in after replacement....................."+jsonStuff);
				//htmlStuff=PrepareFinalHtml.replaceImgURL(htmlStuff, currentUser.getUserName());
				htmlStuff = htmlStuff.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
						.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
						.replace(MAILHANDLER_URL, ImageServer_Url);
				winId.setAttribute("autosave","f");
				logger.info("------in normal save button--");		
				htmlStuffId.setValue(htmlStuff);
				htmlPreviewStuff = htmlStuffId.getValue();
				winId$htmlStuffId3.setValue("");
				winId$resLbId.setValue("");
				winId$templatNameTbId.setValue("");
				winId.setVisible(true);
				winId.setPosition("center");
				getMyTemplatesFromDb(currentUser.getUserId());
				winId.doHighlighted();

			}
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
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
	public void onClick$onsaveRowTemplate$customRowId$beeEditorWinId() throws ParseException, InterruptedException, JSONException {
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
			JSONArray array = Utility.dynamicUrlforCustomRowsBeeEditor(currentUser, userDesignedCustomRowsDao, "MyTemplate");
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
			List<ApplicationProperties> defaultCustomRows  = userDesignedCustomRowsDao.getDefaultTemplate("MyTemplate");
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
			//Sessions.getCurrent().setAttribute("externalUrl", array);
			Clients.evalJavaScript("var externalUrl ="+array+";");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/


} //Class
