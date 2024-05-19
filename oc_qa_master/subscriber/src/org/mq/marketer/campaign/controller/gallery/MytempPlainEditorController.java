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
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.PrepareFinalHtml;
import org.mq.marketer.campaign.controller.layout.EditorController;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.MyTemplatesDaoForDML;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.HTMLUtility;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
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
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.ForEach;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;


@SuppressWarnings("serial")
public class MytempPlainEditorController  extends MyTempEditorController{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
		
		Textbox emailNameTbId;
		Textbox testEmailTbId;
		Html html = new Html();
		Toolbarbutton addBlockBtnId;
		String userName;
		String usersParentDirectory;
		String htmlFilePath;
		CKeditor ckEditorId = null;
		CKeditor fckEditorId;
		
		String htmlStuff;
		String htmlPreviewStuff;
		Timer mytempAutoSaveId;
		Label autoSaveLbId;
		
		private Textbox zipImport$urlTbId;
		
		private Button nextBtnId;
		private Window winId,zipImport;
		private String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
		static final String APP_MAIN_URL = "https://qcapp.optculture.com/subscriber/";
		static final String APP_MAIN_URL_HTTP = "http://qcapp.optculture.com/subscriber/";
		static final String MAILCONTENT_URL = "http://mailcontent.info/subscriber/";
		static final String MAILHANDLER_URL = "http://mailhandler01.info/subscriber/";
		private static final String ImageServer_Url = PropertyUtil.getPropertyValue("ImageServerUrl");

		public MytempPlainEditorController() {
			
			String style = "font-weight:bold;font-size:15px;color:#313031;" +"font-family:Arial,Helvetica,sans-serif;align:left";
			
			PageUtil.setHeader("My Templates","",style,true);
			
			
			
			userName = GetUser.getUserName();
			usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");
			
			
		
			 UserActivitiesDao userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
			 UserActivitiesDaoForDML userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
			 /* if(userActivitiesDao != null) {
		      	userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_PLAIN_EDITOR,GetUser.getUserObj());
			  }*/
			  if(userActivitiesDaoForDML != null) {
			      	userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_PLAIN_EDITOR,GetUser.getLoginUserObj());
			  }
			 
			  
		}
		
		
		
		@Override
		public void doAfterCompose(Component comp) throws Exception {
			
			try {
				super.doAfterCompose(comp);
				
				logger.debug(" in plain editor");
				
				if(logger.isDebugEnabled())logger.debug("-- just entered --"+isEdit);
				
				if((myTemplates ==null && systemTemplates == null) && currentUser == null){
					Redirect.goTo(PageListEnum.RM_HOME);
					return;
				}
				
				if(isEdit != null){
					
					
					editorType = myTemplates.getEditorType();
					htmlStuff = myTemplates.getContent();
					
					
					
						
				}else {
					logger.debug("----create new tempalte------");
					htmlStuff = myTemplates != null ? myTemplates.getContent() : "";
					
					
				}
				
				
				htmlPreviewStuff = htmlStuff;
				//html.setContent (MyTempEditorController.createEditorTags(htmlStuff));
			
				//logger.info("t2 :"+ htmlStuff);
				
				fckEditorId.setValue(htmlStuff);

				getPlaceHolderList();
				EditorController.getCouponsList();
				//phLbId.setItemRenderer(new phListRenderer());
				//phLbId.setModel(new SimpleListModel(placeHoldersList));
				
				//setting for this attribute to enabling ckEditor type of button in imageLibrary.zul
				sessionScope.setAttribute("EditorType","ckEditor");
				
			}catch(Exception e) {
				logger.error(" Exception : ", e );
			}
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
			
			
			if(isEdit != null && isEdit) {
				
				
					if(myTemplates != null && htmlStuff != null) {
						
							htmlStuff =fckEditorId.getValue();
							
							myTemplates.setModifiedDate(Calendar.getInstance());
							myTemplates.setContent(htmlStuff);
						
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
									PropertyUtil.getPropertyValue("userTemplatesDirectory")+File.separator+myTemplates.getFolderName()+
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
						autoSaveLbId.setValue("Last auto-saved at :"+MyCalendar.calendarToString(Calendar.getInstance(),
								MyCalendar.FORMAT_SCHEDULE_TIME,tz));
				}
			}
			else {
				logger.info("----in new save part of autosave");
				htmlStuff =fckEditorId.getValue();
				htmlStuffId.setValue(htmlStuff);
				htmlPreviewStuff = htmlStuffId.getValue();
				String foldernName=Constants.MYTEMPATES_FOLDERS_DRAFTS;
				String timeStamp = MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, tz);
				String  name = Constants.MYTEMPATES_FOLDERS_FOLDERNAME+timeStamp;
				if(super.autoSaveInMyTemplates(name,fckEditorId.getValue(),"plainTextEditor",foldernName)) {
					autoSaveLbId.setValue("Last auto-saved at :"+MyCalendar.calendarToString(Calendar.getInstance(),
							MyCalendar.FORMAT_SCHEDULE_TIME,tz));
					isEdit=true;
					myTemplates =(MyTemplates)sessionScope.getAttribute("Template");
					logger.info("---------after isedit------"+myTemplates);
				}
				
			}
		}
		
		//public void saveEmail(String htmlStuff) {
		private Textbox winId$templatNameTbId,htmlStuffId,winId$htmlStuffId3;
		private Listbox myTemplatesListId;
		public void onClick$saveBtnId() {
			
			
			
			try {
				if(logger.isDebugEnabled())logger.debug("-- just entered --");
				
				//htmlStuff = htmlStuffId.getValue();
				if(isEdit != null) {
					
					if(myTemplates.getFolderName().equals(Constants.MYTEMPATES_FOLDERS_DRAFTS)) {
						logger.info("------in autosave save button---");
						winId.setAttribute("autosave","t");
						htmlStuff= fckEditorId.getValue();
						//htmlStuff = PrepareFinalHtml.replaceImgURL(htmlStuff, currentUser.getUserName());
						htmlStuff = htmlStuff.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
								.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
								.replace(MAILHANDLER_URL, ImageServer_Url);
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
						
					if(myTemplates != null && htmlStuff != null) {
						
						myTemplates = (MyTemplates)sessionScope.getAttribute("Template");
						int confirm = Messagebox.show("Are you sure, you want to modify the template?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						if(confirm != Messagebox.OK){
							return;
						}else {
							htmlStuff =fckEditorId.getValue();
							if(htmlStuff == null || htmlStuff.isEmpty()) {
								
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
							htmlStuff =  htmlStuff.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
									.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
									.replace(MAILHANDLER_URL, ImageServer_Url);
							myTemplates.setModifiedDate(Calendar.getInstance());
							myTemplates.setContent(htmlStuff);
						
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
								bw.close();*/
								
							/**
							 * creates a html file and saves in user/MyTemplate directory
							 */
							String myTemplateFilePathStr = 
									
									PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
									PropertyUtil.getPropertyValue("userTemplatesDirectory")+File.separator+myTemplates.getFolderName()+
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
					winId.setAttribute("autosave","f");
					logger.info("------in normal save button--");
					htmlStuff= fckEditorId.getValue();
					//htmlStuff = PrepareFinalHtml.replaceImgURL(htmlStuff, currentUser.getUserName());
					htmlStuff = htmlStuff.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
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
			
		
			
		
			
		}// saveEmail()
		 public void onClick$myTemplatesSubmtBtnId$winId(){
				
				try{
					if(logger.isDebugEnabled())logger.debug("-- just entered --");
					
					Textbox nameTbId = (Textbox)winId.getFellowIfAny("templatNameTbId");
					
					super.saveInMyTemplates(winId,nameTbId.getValue(),fckEditorId.getValue(),"plainTextEditor",winId$myTempListId);
				}catch (Exception e) {
					logger.error("** Exception :", e );
				}
			}
		

		public void onClose$winId(Event event) {
			self.setVisible(false);
			event.stopPropagation();
		}
		 public void onClick$nextBtnId() throws Exception {
			 onClick$saveBtnId() ;
		}
		
		
		 public void onClick$testEmailGoBtnId() throws Exception {
			
			/*if(super.sendTestMail(fckEditorId.getValue(), testEmailTbId.getValue())) {
				testEmailTbId.setValue("Email Address...");
				}*/
			 
			 String emailId=testEmailTbId.getValue();
			 if( emailId.equals("Email Address...") || emailId.isEmpty() ){
					MessageUtil.setMessage("There is no mail id to send a test mail.", "color:red");
					testEmailTbId.setValue("Email Address...");
				}
			
			 else if(super.sendTestMail(fckEditorId.getValue(), testEmailTbId.getValue())) {
				//testMailTbId.setValue("");
				testEmailTbId.setValue("Email Address...");
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
		 public void onFocus$testEmailTbId() throws Exception {
			 	String mail=testEmailTbId.getValue(); 
				if(mail.equals("Email Address...") || mail.equals("")){
					 testEmailTbId.setValue("");
					 
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
			 super.checkSpam(fckEditorId.getValue(),true);
		 }
		 
		 public void onClick$reloadTblId() throws Exception {
			 try{
				 logger.debug("reloadBtn Id called");
					int confirm = Messagebox.show("Do you want to reload the email?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != 1){
						return;
					}else{
					fckEditorId.setValue("");
					//if( !htmlStuff.trim().isEmpty()) {
					String	htmlStuff = null;
						htmlStuff = myTemplates.getContent();
						fckEditorId.setValue(htmlStuff);
					//}
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
					htmlContent=myTemplates.getContent();
					 //String htmlContent=campaign.getHtmlText();
					
				}else{
					htmlContent =fckEditorId.getValue();
					
				}
				Utility.showPreview(previewIframeWin$iframeId,currentUser.getUserName(), htmlContent);
				previewIframeWin.setVisible(true);
				
				
			}
			private Listbox winId$myTempListId; 
			public void getMyTemplatesFromDb(Long userId){
				
				String userTemplatesDirectory = PropertyUtil.getPropertyValue("userTemplatesDirectory");
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
			
	} //Class
