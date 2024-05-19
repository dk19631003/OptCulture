package org.mq.marketer.campaign.controller.layout;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.UserDesignedCustomRows;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.PrepareFinalHtml;
import org.mq.marketer.campaign.controller.contacts.ManageAutoEmailsControllerBee;
import org.mq.marketer.campaign.controller.gallery.MyTempEditorController;
import org.mq.marketer.campaign.controller.gallery.MyTemplatesController;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.MyTemplatesDaoForDML;
import org.mq.marketer.campaign.dao.UserDesignedCustomRowsDao;
import org.mq.marketer.campaign.dao.UserDesignedCustomRowsDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataIntegrityViolationException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.google.gson.JsonObject;

import bsh.ParseException;

@SuppressWarnings("serial")
public class BeeAutoEmailController extends MyTempEditorController {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Window createCustTemplateWinId;
	private String usersParentDirectory ;
	private Textbox createCustTemplateWinId$customTemplateTextId;
	private Textbox editTemplateIdTextBox;
	private Label testWinId$msgLblId;
	private Window testWinId;
	private Textbox testWinId$testTbId;
	private Button testWinId$sendTestMailBtnId;
	private boolean isSendTestMail;
	private Session sessionScope = null;
	private Users currentUser;
	private Label autoSaveLbId;
	private Boolean autoSave = false;
	private String hidePopupAutoSave;
	private Window customRowId;
	private Textbox customRowId$rowTextId;
	private Combobox customRowId$comboCategoryRowId;
	private UserDesignedCustomRowsDaoForDML userDesignedCustomRowsDaoForDML;
	private UserDesignedCustomRowsDao userDesignedCustomRowsDao;
	private MailingListDao mailingListDao;
	private Set<Long> listIdsSet;
	private String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
	static final String APP_MAIN_URL = "https://app.optculture.com/subscriber/";
	static final String APP_MAIN_URL_HTTP = "http://app.optculture.com/subscriber/";
	static final String MAILCONTENT_URL = "http://mailcontent.info/subscriber/";
	static final String MAILHANDLER_URL = "http://mailhandler01.info/subscriber/";
	private static final String ImageServer_Url = PropertyUtil.getPropertyValue("ImageServerUrl");
	
	
	private MyTemplatesDao myTemplatesDao;
	
	@SuppressWarnings("unchecked")
	public BeeAutoEmailController() throws JSONException {
		usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");
		myTemplatesDao	 = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
		sessionScope = Sessions.getCurrent();
		currentUser = GetUser.getUserObj();
		this.userDesignedCustomRowsDaoForDML = (UserDesignedCustomRowsDaoForDML) SpringUtil.getBean("userDesignedCustomRowsDaoForDML");
		this.userDesignedCustomRowsDao = (UserDesignedCustomRowsDao) SpringUtil.getBean("userDesignedCustomRowsDao");
		mailingListDao = (MailingListDao) SpringUtil.getBean("mailingListDao");
		listIdsSet = (Set<Long>) sessionScope.getAttribute(Constants.LISTIDS_SET);
		prepareCustomRowUrlForCampaign();
	} 
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		logger.info("Bee plugin");
		Clients.evalJavaScript("namevalue='"+currentUser.getUserName()+"'");
		
		Long editTemplateId = (Long)sessionScope.getAttribute("editTemplateId");
		String typeOfAutoEmailsStr = PropertyUtil.getPropertyValueFromDB(Constants.BEE_CLIENT_KEYVALUE_DR);
		Clients.evalJavaScript("beekey='"+typeOfAutoEmailsStr+"'");
		Set<MailingList> set = new HashSet<MailingList>();
		List<MailingList> mlist = mailingListDao.findByIds(listIdsSet);
		if (mlist != null) {
			set.addAll(mlist);
		}
		String mergeList=Constants.STRING_NILL;
		 String type=(String) Sessions.getCurrent().getAttribute("otptype");
	
		 if (type!=null && type.equalsIgnoreCase("OTP MESSAGE")) {
		  mergeList = ManageAutoEmailsControllerBee.getPlaceHolderListAutoEmailOTP(set, false, null);
		  Sessions.getCurrent().removeAttribute("otptype");
	}else {
		 mergeList = ManageAutoEmailsControllerBee.getPlaceHolderListAutoEmail(set, false, null);
	}	
		List<String> promotionList=EditorController.getCouponsList();
		StringBuffer specialLinksBuffer=new StringBuffer();
		for (String couponPlaceHolder : promotionList) {
			if(couponPlaceHolder.trim().startsWith("--") || couponPlaceHolder.toLowerCase().contains(("place holder"))) { //Ignore
				continue;
			}
			String[] coupPHArr = couponPlaceHolder.split(Constants.DELIMETER_DOUBLECOLON );
			String name = coupPHArr[0];
			String value = coupPHArr[1];
			if(specialLinksBuffer.length() > 0) specialLinksBuffer.append(",");
			specialLinksBuffer.append("{type: 'PromoCodes',label: '" +name+ "', link: 'BEEFREEPROMOCODE_START"+value+"BEEFREEPROMOCODE_END'}"); 
		} // for
		
		String specialLinkStr=specialLinksBuffer.toString();
		String finallink = "";
		if(specialLinkStr!=null && !specialLinkStr.isEmpty()) {
			
			finallink += specialLinkStr;
		}
		
		Clients.evalJavaScript("specialLinks79 = ["+finallink+"]");
		Clients.evalJavaScript("mergeTags97 = ["+mergeList+"]");
		
		 if(editTemplateId!= null && editTemplateId!=0) {
			  MyTemplates myTemp = myTemplatesDao.getTemplateByMytemplateId(editTemplateId);
			  JSONObject jsontemplate = (JSONObject) JSONValue.parse(myTemp.getJsoncontent());
			  Clients.evalJavaScript("mytemplate = "+jsontemplate+"");
			  currentUser = myTemp.getUsers();
			 editTemplateIdTextBox.setValue(editTemplateId.toString());
		 }else {
			 JSONObject jsonData = (JSONObject) JSONValue.parse(Constants.DEFAULT_JSON_VALUE);
			 Clients.evalJavaScript("mytemplate = "+jsonData+"");
			 editTemplateIdTextBox.setValue(Constants.STRING_NILL);
			 currentUser = GetUser.getUserObj();
		 }
	}
	
	public void onClick$saveAutoEmailId() {
		if(editTemplateIdTextBox.getValue()!= null && !editTemplateIdTextBox.getValue().isEmpty()) {
			MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateIdandUserId(Long.parseLong(editTemplateIdTextBox.getValue()), currentUser.getUserId());
			if(myTemplates!= null && !myTemplates.getName().isEmpty()) {
				createCustTemplateWinId$customTemplateTextId.setValue(myTemplates.getName());
				getMyTempCopy(myTemplates.getUsers().getUserId(),Constants.EDITOR_TYPE_BEE);
				for(Listitem folderNames:createCustTemplateWinId$myTempListId.getItems()) {
					if(folderNames.getLabel().equalsIgnoreCase(myTemplates.getFolderName())) {
						createCustTemplateWinId$myTempListId.setSelectedIndex(folderNames.getIndex());
					}
				}
			}
		}else {
			getMyTempCopy(currentUser.getUserId(),Constants.EDITOR_TYPE_BEE);
		}
		 Long editTemplateId = (Long)sessionScope.getAttribute("editTemplateId");
		 if((editTemplateId!= null && editTemplateId!=0) || (editTemplateIdTextBox.getValue()!= null && !editTemplateIdTextBox.getValue().isEmpty() && !autoSave)){
				 if(hidePopupAutoSave!=null && hidePopupAutoSave.equalsIgnoreCase(createCustTemplateWinId$customTemplateTextId.getValue())) {
					 createCustTemplateWinId$customTemplateTextId.setValue(Constants.STRING_NILL);
					 createCustTemplateWinId.doHighlighted();
					 createCustTemplateWinId.setVisible(true);
				 }else {
					 createCustTemplateWinId.setVisible(false);
					 onClick$saveAutoEmailTemplate$createCustTemplateWinId$BeeAutoEmailId();
				 }
		 }else {
			 createCustTemplateWinId$customTemplateTextId.setValue(Constants.STRING_NILL);
			 createCustTemplateWinId.doHighlighted();
			 createCustTemplateWinId.setVisible(true); 
		 }
	}
	
	
	public void onClick$saveAutoEmailTemplate$createCustTemplateWinId$BeeAutoEmailId() {
		Clients.evalJavaScript("bee.save()");
	}
	
	public void onCustomEvent$jsonData(ForwardEvent event) throws JSONException{
		  Object o1 = JSONValue.parse(event.getOrigin().getData().toString());
		  if(o1!= null) {
			  JSONObject jsonObj = (JSONObject) o1;
			  String json = (String) jsonObj.get("json");
			  String html = (String) jsonObj.get("html");
			  if(json==null||!(json.contains(Constants.DEFAULT_JSON_CHECK_TEXTBOX)||json.contains(Constants.DEFAULT_JSON_CHECK_IMAGE)||json.contains(Constants.DEFAULT_JSON_CHECK_MERGE_CONTENT)||json.contains(Constants.DEFAULT_JSON_CHECK_HTML)||json.contains(Constants.DEFAULT_JSON_CHECK_BUTTON)||json.contains(Constants.DEFAULT_JSON_CHECK_SOCIAL)||json.contains(Constants.DEFAULT_JSON_CHECK_DIVIDER))){
				  if(html == null) {
					  logger.info("auto save Empty");
				  }else {
					  if(!autoSave) {
						  Messagebox.show("Please use 'Text Block' to save the template.", "Error", Messagebox.OK, Messagebox.ERROR); //APP-3458
						  testWinId.setVisible(false);
						  testWinId$sendTestMailBtnId.setDisabled(false);
					  }
					  autoSave = false;
					  return;
				  }
			  }
			  if(json!= null && !json.isEmpty()) {
				  saveJSONHTMLContent(json,html);
			  } /*
				 * else if(isSendTestMail && !autoSave) { String emailId =
				 * testWinId$testTbId.getValue(); super.sendTestMail(html, emailId);
				 * testWinId.setVisible(false); testWinId$sendTestMailBtnId.setDisabled(false);
				 * isSendTestMail=false; }
				 */
		  }
	}
	
	public void onCustomEventAutoSave$jsonData(ForwardEvent event) throws JSONException{
		autoSave = true;
		Clients.evalJavaScript("bee.send()");
	}
	
	
	
	TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
	private void saveJSONHTMLContent(String json, String html) {
		try{
			String pathTojs= PropertyUtil.getPropertyValueFromDB("pathToPhantomjs");
			MyTemplates myTemplates = null;
			if(editTemplateIdTextBox.getValue()!= null && editTemplateIdTextBox.getValue() !="" && editTemplateIdTextBox.isValid()) {
				 myTemplates = myTemplatesDao.getTemplateByMytemplateIdandUserId(Long.parseLong(editTemplateIdTextBox.getValue()), currentUser.getUserId());
				 myTemplates.setModifiedDate(Calendar.getInstance());
			}else if(!autoSave) {
				myTemplates = new MyTemplates();
				myTemplates.setCreatedDate(Calendar.getInstance());
			}
			//APP-1849
			String[] beeHtmlContentParts = html.split("<a");
			if(html!=null&!html.isEmpty()){
				for(int i=0;i<=beeHtmlContentParts.length-1;i++){
					String anchorTagStr="<a"+beeHtmlContentParts[i];
					//System.out.println("anchorTagStr.."+anchorTagStr);
					String promocodeLink="<a"+StringUtils.substringBetween(anchorTagStr, "<a", "</a>")+"</a>";
					if(promocodeLink.contains("BEEFREEPROMOCODE_START")&&promocodeLink.contains("BEEFREEPROMOCODE_END")){
						String promocodeValue=StringUtils.substringBetween(html, "BEEFREEPROMOCODE_START", "BEEFREEPROMOCODE_END");
						//System.out.println("promocodeValue.."+promocodeValue);
						html=html.replace(promocodeLink, promocodeValue);
					}
				}
				html= html.replace("&lt;img", "<img");
				html= html.replace("/&gt;", "/>");
				html=Utility.replaceTextBarcodeHeightWidth(html);
			}
			
			//done
		if(!autoSave && !isSendTestMail) {	
			myTemplates.setName(createCustTemplateWinId$customTemplateTextId.getValue());
			if(createCustTemplateWinId$myTempListId.getSelectedItem()!= null && createCustTemplateWinId$myTempListId.getSelectedItem().getLabel()!= null) {
					myTemplates.setFolderName(createCustTemplateWinId$myTempListId.getSelectedItem().getLabel());
			}else {
				Messagebox.show("Please Enter Template Name To save Template ");
				return;
			}
			myTemplates.setJsoncontent(json);
			if(html!= null) {
				myTemplates.setContent(html);
			}
			myTemplates.setEditorType(Constants.EDITOR_TYPE_BEE);
			myTemplates.setUsers(currentUser);
			if(myTemplates.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE)){
				saveInMyTemplatesAutoEmail(createCustTemplateWinId,myTemplates);
			}
			Sessions.getCurrent().removeAttribute("beeEditAutoEmail");
		}
		if(isSendTestMail && !autoSave){
			 String emailId = testWinId$testTbId.getValue();
			 	String htmlContentForMergeReplace = Utility.mergeTagsForPreviewAndTestMail(html,"testMail");
				super.sendTestMail(htmlContentForMergeReplace, emailId);
				testWinId.setVisible(false);
				testWinId$sendTestMailBtnId.setDisabled(false);
				isSendTestMail=false;
		}
		if(autoSave) {
			if(myTemplates != null && html != null) {
				try {
				if(editTemplateIdTextBox.getValue()!= null && !editTemplateIdTextBox.getValue().isEmpty()) {
					//html = PrepareFinalHtml.replaceImgURL(html, currentUser.getUserName());
					html = html.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
					json = json.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
					myTemplates.setModifiedDate(Calendar.getInstance());
					myTemplates.setContent(html);
					myTemplates.setJsoncontent(json);

					if(html.contains("href='")){
						html = html.replaceAll("href='(^((\"+\\s+\")|(\"\")|('+\\s+')|(''))$)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
					}
					if(html.contains("href=\"")){
						html = html.replaceAll("href=\"(^((\"+\\s+\")|(\"\")|('+\\s+')|(''))$)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
					}

					if(html!= null) {
						if(html.contains(Constants.CONFIRM_SUBSCRIPTION_LINK)) {
							html = html.replace(Constants.CONFIRM_SUBSCRIPTION_LINK, "<a href='[url]'>Confirm Subscription</a><br/>");
						}
					}

					String myTemplateFilePathStr = 

							PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
							PropertyUtil.getPropertyValue("userNewEditorTemplatesDirectory")+File.separator+myTemplates.getFolderName()+
							File.separator+myTemplates.getName()+File.separator+"email.html";
					logger.info("AutoSave myTemplateFilePathStr...:"+myTemplateFilePathStr);
					try {
						File myTemplateFile = new File(myTemplateFilePathStr);
						File parentDir = myTemplateFile.getParentFile();

						if(parentDir.exists() ) {
							FileUtils.deleteDirectory(parentDir);
						}

						parentDir.mkdirs();

						BufferedWriter bw = new BufferedWriter(new FileWriter(myTemplateFile));
						bw.write(html);
						bw.flush();
						bw.close();
						MyTemplatesController.generateThumbnil(new File(PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
								PropertyUtil.getPropertyValue("userNewEditorTemplatesDirectory")+File.separator+myTemplates.getFolderName()+
								File.separator+myTemplates.getName()),pathTojs);

					} catch (IOException e) {
						logger.error("** Exception : MyTemplates file creation failed",(Throwable)e);
						MessageUtil.setMessage("Encountered a problem while updating the template.", "color:red", "TOP");
					}

					MyTemplatesDaoForDML myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
					myTemplatesDaoForDML.saveOrUpdate(myTemplates);	
					logger.debug("after saving the template");
					autoSaveLbId.setValue("Last auto-saved at: "+ (MyCalendar.calendarToString(Calendar.getInstance(),MyCalendar.FORMAT_SCHEDULE_TIME,tz)).substring(13));
				}
			}catch (Exception e) {
				logger.error("** Exception :", e );
			}
			}
			else {
				logger.info("----in new save part of autosave");
				String foldernName=Constants.NEWEDITOR_TEMPLATES_FOLDERS_DRAFTS;
				String timeStamp = MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, tz);
				String  name = Constants.NEWEDITOR_TEMPLATES_FOLDERS_FOLDERNAME+timeStamp;
				if(super.autoSaveInMyTemplates(name,html,"beeEditor",foldernName,json)) {
					autoSaveLbId.setValue("Last auto-saved at: "+ (MyCalendar.calendarToString(Calendar.getInstance(),MyCalendar.FORMAT_SCHEDULE_TIME,tz)).substring(13));
					myTemplates =(MyTemplates)sessionScope.getAttribute("Template");
					editTemplateIdTextBox.setValue(myTemplates.getMyTemplateId().toString());
					logger.info("---------after isedit------"+myTemplates);
					hidePopupAutoSave = myTemplates.getName();
				}
			}
			autoSave =false;
		}
		}catch (Exception e) {
			logger.error("** Exception :", e );
		}
	}
	
	private void saveInMyTemplatesAutoEmail(Window createCustTemplateWinId, MyTemplates myTemplates) {
		String pathTojs= PropertyUtil.getPropertyValueFromDB("pathToPhantomjs");
		String foldernName="";
		Label resLbId = (Label)createCustTemplateWinId.getFellow("resLbId");
		String isValidPhStr = null;
		isValidPhStr = Utility.validatePh(myTemplates.getContent(), currentUser);

		if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
			MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
			return ;
		}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
			MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
			return ;

		}
		long size =	Utility.validateHtmlSize(myTemplates.getContent());
		if(size >100) {
			String msgcontent = OCConstants.HTML_VALIDATION;
			msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
			MessageUtil.setMessage(msgcontent, "color:Blue");
		}

		int override = -1;
		try{
			if(myTemplates.getContent() == null || myTemplates.getContent().isEmpty()) {
				MessageUtil.setMessage("Template content can not be empty.", "color:red;");
				return;
			}

			if(myTemplates.getName() == null || myTemplates.getName().trim().length() == 0){
				resLbId.setValue("Please provide Template name to save template");
				resLbId.setVisible(true);
				return;
			}else if(!Utility.validateName(myTemplates.getName())) {
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
			
			if(myTemplatesDao == null)
				myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
			foldernName= myTemplates.getFolderName();
			foldernName = foldernName.contains("(") ? foldernName.substring(0,foldernName.indexOf("(")) : foldernName;


			MyTemplates myTemplatesCheck = myTemplatesDao.findByUserNameAndTempNameInFolder(currentUser.getUserId(),myTemplates.getName(),foldernName);
			if(myTemplatesCheck != null) {

				try {
					override = Messagebox.show("The name already exists. Do you want to replace it?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(override == 1 ) {
						myTemplates.setContent(myTemplates.getContent());
						myTemplates.setJsoncontent(myTemplates.getJsoncontent());

					}else {
						return;
					}
				} catch (Exception e) {
					logger.error("Exception ::" , e);;
				}
			}
			else {
				foldernName=  myTemplates.getFolderName();
			}
			myTemplates.setParentDir(Constants.NEWEDITOR_TEMPLATES_PARENT);
			if(isSendTestMail == true) {
				String emailId = testWinId$testTbId.getValue();
				super.sendTestMail(myTemplates.getContent(), emailId);
				testWinId.setVisible(false);
				testWinId$sendTestMailBtnId.setDisabled(false);
			}
			
			
			if(myTemplates.getContent().contains("href='")){
				myTemplates.setContent(myTemplates.getContent().replaceAll("href='(^((\"+\\s+\")|(\"\")|('+\\s+')|(''))$)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\""));
			}
			if(myTemplates.getContent().contains("href=\"")){
				myTemplates.setContent(myTemplates.getContent().replaceAll("href=\"(^((\"+\\s+\")|(\"\")|('+\\s+')|(''))$)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\""));
			}
			
			if(myTemplates.getContent()!= null) {
				if(myTemplates.getContent().contains(Constants.CONFIRM_SUBSCRIPTION_LINK)) {
					myTemplates.setContent(myTemplates.getContent().replace(Constants.CONFIRM_SUBSCRIPTION_LINK, "<a href='[url]'>Confirm Subscription</a><br/>"));
				}
			}
			
			String myTemplateFilePathStr = 
					PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
					PropertyUtil.getPropertyValue("userNewEditorTemplatesDirectory")+File.separator+foldernName+
					File.separator+myTemplates.getName()+File.separator+"email.html";
			
			try {
				File myTemplateFile = new File(myTemplateFilePathStr);
				File parentDir = myTemplateFile.getParentFile();

				if(override == 1) {
					FileUtils.deleteDirectory(parentDir);
				}

				parentDir.mkdirs();

				BufferedWriter bw = new BufferedWriter(new FileWriter(myTemplateFile));

				bw.write(myTemplates.getContent());
				//bw.write(myTemplates.getJsoncontent());
				bw.flush();
				bw.close();
				MyTemplatesController.generateThumbnil(new File(PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
						PropertyUtil.getPropertyValue("userNewEditorTemplatesDirectory")+File.separator+foldernName+
						File.separator+myTemplates.getName()),pathTojs);
				String msgStr = myTemplates.getName() +" saved in "+myTemplates.getFolderName()+" successfully.";
				if(override == 1) {
					msgStr = myTemplates.getName() +" updated successfully in My templates";
				}
				//String Final_html = PrepareFinalHtml.replaceImgURL(myTemplates.getContent(), myTemplates.getUsers().getUserName());
				String Final_html = myTemplates.getContent().replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
						.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
						.replace(MAILHANDLER_URL, ImageServer_Url);
				String Final_json = myTemplates.getJsoncontent().replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
						.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
						.replace(MAILHANDLER_URL, ImageServer_Url);
				myTemplates.setContent(Final_html);
				myTemplates.setJsoncontent(Final_json);
				MyTemplatesDaoForDML myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
				myTemplatesDaoForDML.saveOrUpdate(myTemplates);	
				editTemplateIdTextBox.setValue(myTemplates.getMyTemplateId().toString());
				MessageUtil.setMessage(msgStr, "color:blue", "TOP");
				createCustTemplateWinId.setVisible(false);
			} catch (IOException e) {
				logger.error("** Exception : MyTemplates file creation failed",(Throwable)e);
			}
		} catch(DataIntegrityViolationException die) {
			resLbId.setValue("Unable to save in my templates");
			resLbId.setVisible(true);
			logger.error("** Exception : while saving template in to MyTemplates", (Throwable)die);
		} catch(Exception e) {
			logger.error("** Exception : while saving the template in MyTemplates", (Throwable)e);
		}
	}

	
	private Listbox createCustTemplateWinId$myTempListId;
	private final String  USER_TEMPLATES_DIRECTORY= "userTemplatesDirectory"; 
	private final String  USER_NEW_EDITOR_TEMPLATES_DIRECTORY= "userNewEditorTemplatesDirectory"; 
	public void getMyTempCopy(Long userId, String editor){
		String userTemplatesDirectory = PropertyUtil.getPropertyValue(editor.equals(Constants.EDITOR_TYPE_BEE) ? USER_NEW_EDITOR_TEMPLATES_DIRECTORY : USER_TEMPLATES_DIRECTORY);
		try {
			Components.removeAllChildren(createCustTemplateWinId$myTempListId);
			File myTemp = null;
			File[] templateList = null;
			String myTempPathStr = usersParentDirectory+ File.separator+currentUser.getUserName()+userTemplatesDirectory+File.separator;
			myTemp = new File(myTempPathStr);
			templateList = myTemp.listFiles();
			for (Object obj : templateList) {
				final File template = (File) obj;
				if(!template.getName().equalsIgnoreCase(Constants.NEWEDITOR_TEMPLATES_FOLDERS_DRAFTS)) {
					Listitem item = new Listitem();
					String folderName = template.getName();
					item.setLabel(folderName);
					item.setParent(createCustTemplateWinId$myTempListId);
				}
			}
			if(createCustTemplateWinId$myTempListId.getItemCount() > 0) createCustTemplateWinId$myTempListId.setSelectedIndex(0);
		} catch (Exception e) {
			logger.error("Exception ::" , e);;
		}

	}

	public void onClick$backAutoEmailId() {
		Boolean redirectForFooterFormCampaign = (Boolean) Sessions.getCurrent().getAttribute("redirectForFooterFormCampaign");
		if(redirectForFooterFormCampaign!=null && redirectForFooterFormCampaign) {
			Redirect.goTo(PageListEnum.CAMPAIGN_SETTINGS);
		}else {
			Sessions.getCurrent().setAttribute("dispalyCreateNew", true);
			Long templateId = null;
			if(Sessions.getCurrent().getAttribute("beeBackButtonAutoPoplate")!= null) {
				templateId = (Long)Sessions.getCurrent().getAttribute("beeBackButtonAutoPoplate");
			}
			Sessions.getCurrent().setAttribute("beeBackButtonAutoPoplateInCreateTemplate",templateId);
			Redirect.goTo(PageListEnum.CONTACT_MANAGE_AUTO_EMAILS_BEE);
		}
	}
	
	public void onClick$plainPreviewImgId() {
		Clients.evalJavaScript("bee.preview()");
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

	private boolean validateEmailAddr(String emailId) {
		try {
			boolean isValid = true;			
			if(emailId != null && 	emailId.trim().length() > 0) {
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
					}
				}

			}else {
				isValid = false;
				testWinId$msgLblId.setValue("Invalid Email address");
				testWinId$msgLblId.setVisible(true);
			}
			return isValid;
		}
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
	
	public void onError$jsonData() {
		MessageUtil.setMessage("You have encountered an error viewing this page, please refresh to try again. \n If you continue having viewing difficulties, please submit a ticket to support@optculture.com", "color:blue");
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
	
	
	public void onClickSaveRowAutoEmail$jsonDataSaveRow(ForwardEvent event) throws JSONException{
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
	public void onClick$onsaveRowTemplate$customRowId$BeeAutoEmailId() throws ParseException, InterruptedException, JSONException {
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
			JSONArray array = Utility.dynamicUrlforCustomRowsBeeEditor(currentUser, userDesignedCustomRowsDao, "autoEmail");
			Clients.evalJavaScript("externalUrl ="+array+";");
			//Sessions.getCurrent().setAttribute("externalUrl", array);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
/*	@SuppressWarnings("unchecked")
	public void prepareCustomRowUrlForCampaign() throws JSONException {
		try {
			JSONArray array = new JSONArray();
			JSONObject item = new JSONObject();
			List<ApplicationProperties> defaultCustomRows  = userDesignedCustomRowsDao.getDefaultTemplate("autoEmail");
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
			Sessions.getCurrent().setAttribute("externalUrl", array);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	
}
