package org.mq.marketer.campaign.controller.layout;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignSchedule;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.EmailContent;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.SystemTemplates;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CampaignScheduleDao;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.CampaignsDaoForDML;
import org.mq.marketer.campaign.dao.EmailContentDao;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
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
import org.mq.marketer.campaign.general.ZipImport;
import org.mq.optculture.utils.OCConstants;
import org.zkforge.ckez.CKeditor;
import org.zkoss.util.media.Media;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
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

@SuppressWarnings("serial")
public class PlainEditorController extends EditorController {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	Textbox emailNameTbId;
	Textbox testEmailTbId, autoSaveTxtId;
	Html html = new Html();
	Toolbarbutton addBlockBtnId;
	String userName;
	String usersParentDirectory;
	String htmlFilePath;
	//CKeditor ckEditorId = null;
	CKeditor fckEditorId;
	
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
	//private TimeZone clientTimeZone;
	
	public PlainEditorController() {
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
		      	userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_PLAIN_EDITOR,GetUser.getLoginUserObj());
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
		String htmlStuff = "";
		
		try {
			super.doAfterCompose(comp);
			
			if(logger.isDebugEnabled())logger.debug("-- just entered --");
			
			
			if(campaign == null || currentUser == null){
				Redirect.goTo(PageListEnum.RM_HOME);
				return;
			}
			
		/*	this.testEmailTbId= testEmailTbId;
			this.fckEditorid = fckEditor;
			this.contentNameTBId = contentNameTBId;
			this.emailContentsLBId = emailContentsLBId;*/
			
			if(logger.isDebugEnabled())logger.debug(" isEdit :"+isEdit);
			
			
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
			else if(isEdit != null && isEdit.equalsIgnoreCase("edit")) {
				
				//editorType = campaign.getEditorType();
				logger.debug("##################  in Edit mode: "+editorType);
				saveAsDraftBtnId.setVisible(false);
				nextBtnId.setLabel("Save & Close");
				htmlStuff = campaign.getHtmlText();
				if(htmlStuff==null) {
					htmlStuff = "";
				}
				
			}
			else {
				logger.debug(isEdit+"=isEdit  ################## and  Editor type= "+editorType);
				
				String selectedTemplate = (String)sessionScope.getAttribute("selectedTemplate");
				
				if(selectedTemplate != null && selectedTemplate.contains("/")) {
				
					String[] temp = StringUtils.split(selectedTemplate, '/');
					String categoryName = temp[0];
					String templateName = temp[1];
					if(logger.isDebugEnabled())logger.debug("----templateName ---:"+
							templateName + "----categoryName ---:"+categoryName);
					
					
					String isRetain = (String)sessionScope.getAttribute("retainChanges");
					
					logger.debug("RETAIN"+isRetain);
					
					
					if(categoryName.equalsIgnoreCase("MyTemplates") && isRetain == null ) {
						MyTemplates myTemplate = myTemplatesDao.
						findByUserNameAndTemplateName(campaign.getUsers().getUserId(), temp[2],templateName);
						logger.debug("myTemplate :"+myTemplate);
						htmlStuff = myTemplate.getContent();
					}
					else if(isRetain != null && isRetain.equals("retain")){
						logger.debug(">>>>>>>>>>>>> Loading content from Campaign1...");
						htmlStuff = campaign.getHtmlText()!=null?campaign.getHtmlText():"";
					}else{
						logger.debug(">>>>>>>>>>>>> Loading content from Campaign2...");
						htmlStuff = campaign.getHtmlText()!=null?campaign.getHtmlText():"";
					}
				}
				else {
					htmlStuff = campaign.getHtmlText()!=null?campaign.getHtmlText():"";
				}
				
			}
			
			logger.info("t1 :"+ fckEditorId);
			//logger.info("t2 :"+ htmlStuff);
			
			fckEditorId.setValue(htmlStuff);
			sessionScope.removeAttribute("retainChanges");
			

//			List<String> placeHoldersList = getPlaceHolderList(campaign.getMailingLists());
			getPlaceHolderList(campaign.getMailingLists());
			EditorController.getCouponsList();
			//phLbId.setItemRenderer(new phListRenderer());
			//phLbId.setModel(new SimpleListModel(placeHoldersList));
			
			//setting for this attribute to enabling ckEditor type of button in imageLibrary.zul
			sessionScope.setAttribute("EditorType","ckEditor");
			
		}catch(Exception e) {
			logger.error(" Exception : ", e );
		}
	}
	
	
	public void onTimer$autoSaveTimerId() {
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
		//******this is how it should happen in autosave everywhere********** 
		
		campaign = (Campaigns)sessionScope.getAttribute("campaign");
		
		String htmlStuff = fckEditorId.getValue();
		campaign.setHtmlText(htmlStuff);
		campaign.setPrepared(false);
		campaign.setModifiedDate(Calendar.getInstance());
		sessionScope.setAttribute("campaign", campaign);
		
		campaignsDaoForDML.saveOrUpdate(campaign);
		//*************************
		//super.saveEmail(htmlStuff, null , null, null, false);
		logger.debug("after saving");
		logger.debug("current time"+MyCalendar.calendarToString(Calendar.getInstance(),
						MyCalendar.FORMAT_SCHEDULE_TIME));
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
        	fckEditorId.setValue(outsb.toString());
			
	    } catch (Exception e) {
	            logger.error("Error while setting html in Editor",(Throwable)e);
	    }
		 	
	}
	
	//public void saveEmail(String htmlStuff) {
	public void onClick$saveBtnId() {
		
		try {
			
			String htmlStuff = fckEditorId.getValue();
			
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
			
			super.saveEmail(htmlStuff,null, campSchedule, contentName, true);
			
			
			sessionScope.setAttribute("retainChanges", "retain");
			/*Messagebox.show("Content saved successfully.",
					"Captiway", Messagebox.OK, Messagebox.INFORMATION );*/
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
			
			super.saveInMyTemplates(zipImport,nameTbId.getValue(),fckEditorId.getValue(),"plainTextEditor",zipImport$myTemplatesListId);
		}catch (Exception e) {
			logger.error("** Exception :", e );
		}
	}

	 
	//public void gotoPlainMsg(String htmlStuff){
	 public void onClick$nextBtnId() throws Exception {
		super.gotoPlainMsg(fckEditorId.getValue(), null);
	}
	
	//public void saveAsDraft(String htmlStuff){
	 public void onClick$saveAsDraftBtnId() throws Exception {
		 List<CampaignSchedule> campScheduleList = campaignScheduleDao.getByCampaignId(campaign.getCampaignId());
			long activeCount = campScheduleList.stream().filter(campaignSchedule -> campaignSchedule.getStatus() == 0).count(); 
			if(campScheduleList.size() == 0 || activeCount == 0) {
				super.saveAsDraft(fckEditorId.getValue(), null);
			}else {
				//MessageUtil.setMessage(" A campaign with active schedules cannot be saved as draft. Please delete all active schedules first.", "color:red");
				MessageUtil.setMessage(" A campaign with upcoming schedule/s \n cannot be saved as a draft.\n Please delete all active schedules first.", "color:red");
				return;
			}
		
	}
	
	//public void sendTestMail(String htmlStuff){
	 
	 // changes here
	 public void onClick$testEmailGoBtnId() throws Exception {
		//super.sendTestMail(fckEditorId.getValue(), testEmailTbId.getValue());
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
	
	public void loadEmailContent() {
		
		try {
			
			if(emailContentsLBId.getSelectedIndex() == -1) {
				Messagebox.show("Please provide name for the content.", 
						"Error", Messagebox.OK, Messagebox.ERROR);
				return;
			}
			
			EmailContent emailContent = (EmailContent)
							emailContentsLBId.getSelectedItem().getValue();
			fckEditorId.setValue(emailContent.getHtmlContent());
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

	 public void useExistedEmail(){
		 
		 if(emailContentsLBId.getSelectedIndex() == -1) {
			 MessageUtil.setMessage("Select the content to use.", "color:red", "TOP");
			 return;
		 }
		 
		 EmailContent emailContent = (EmailContent)
		 				emailContentsLBId.getSelectedItem().getValue();
		 fckEditorId.setValue(emailContent.getHtmlContent());
		 
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
				if(source != null && source.equalsIgnoreCase("schedule")) {
//					emailContent.getHtmlContent();
					logger.debug("html content is not null from the schedule type of source...");
					htmlStuff = getHtmlStuffFromSourceTypeIsSchedule();
				}
				else  {//
					htmlStuff = campaign.getHtmlText();
				}
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
		 zipImport.setTitle("Save in My Templates");
		 zipImport$resLbId.setVisible(false);
		 zipImport$importZipFileDivId.setVisible(false);
		 zipImport$fetchUrlDivId.setVisible(false);
		 zipImport$saveMyTemplateDivId.setVisible(true);
		 zipImport$templatNameTbId.setValue("");
		 zipImport.setVisible(true);
		 getMyTemplatesFromDb(currentUser.getUserId());
		 zipImport.doHighlighted();
		 
	 } //onClick$saveInMyTemplateTB
	 
	 private Window previewIframeWin; 
		private  Iframe previewIframeWin$iframeId;
		public void onClick$plainPreviewImgId() {
			String htmlStuff = fckEditorId.getValue();
			/*if(Utility.validateHtmlSize(htmlStuff)) {
				Messagebox.show("It looks like the content of your email is more " +
						"than the recommended size per the email-" + 
						"sending best practices. " + "To avoid email landing " + 
						"in recipient's spam folder, please redesign " 
									+"email to reduce content.", "Info", Messagebox.OK, Messagebox.INFORMATION);
			}*/
			
			long size = Utility.validateHtmlSize(htmlStuff);
			if(size >100) {
				String msgcontent = OCConstants.HTML_VALIDATION;
				msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
				MessageUtil.setMessage(msgcontent, "color:Blue");
			}
			
			logger.info("entered into html preview....");
			if(campaign != null) {
				if(fckEditorId.getValue() != null){
				 //String htmlContent=campaign.getHtmlText();
				 Utility.showPreview(previewIframeWin$iframeId,currentUser.getUserName(), fckEditorId.getValue());
				 previewIframeWin.setVisible(true);
				}
			}
			
			
		}
		
		public void getMyTemplatesFromDb(Long userId){
			String userTemplatesDirectory = PropertyUtil.getPropertyValue("userTemplatesDirectory");
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
	 
} //Class
