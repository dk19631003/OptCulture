package org.mq.marketer.campaign.controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.clapper.util.io.FileUtil;
import org.mq.marketer.campaign.beans.SupportTicket;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.service.ExternalSMTPDigiReceiptSender;
import org.mq.marketer.campaign.controller.service.ExternalSMTPSupportSender;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.MessagesDao;
import org.mq.marketer.campaign.dao.SupportTicketDao;
import org.mq.marketer.campaign.dao.SupportTicketDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.util.media.Media;
import org.zkoss.zhtml.Fileupload;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
//import org.zkforge.bwcaptcha.Captcha;

public class SupportController extends GenericForwardComposer {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private final String serverName = PropertyUtil.getPropertyValue("subscriberIp");
	
	private final String supportFileDir = PropertyUtil.getPropertyValue("usersSupportDirectory").trim();
	
	String supportEmail =PropertyUtil.getPropertyValueFromDB("SupportEmailId");
	
	
	
	
	private static Pattern fileExtnPtrn = Pattern.compile("([^\\s]+(\\.(?i)(png|doc|jpg|gif|jpeg))$)");
	
	private static final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(gif|doc|png|jpg))$)";
          //  "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
	
	private Textbox clientNametbId,contactNametbId,contactPhonetbId,contactEmailtbId,descriptiontbId,captchTbId,fileUploadtbId;
	private A uploadAId;
	private Listbox websiteLbId,posIntegrationLbId;
	private Radiogroup typeRadioRgId,productAreaRadioRgId;
	private Radio websiteRgId,posIntegrationRgId,bugRgId,featureReqRgId,serviceReqRgId,technicalReqRgId;
	private Div websiteDivId,posIntegrationDivId;
	private SupportTicketDao supportTicketDao;
	private SupportTicketDaoForDML supportTicketDaoForDML;
	private Users currentUser;
	private String messageHeader =Constants.STRING_NILL;
	private String htmlContent = Constants.STRING_NILL;
	private String textContent = Constants.STRING_NILL;
	private static String selectStr="-Select-";
	private Button reGenratBtnId;
	
	public SupportController(){
		String style="font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Support", "", style, true);
	}
	
	 TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		supportTicketDao = (SupportTicketDao)SpringUtil.getBean("supportTicketDao");
		supportTicketDaoForDML = (SupportTicketDaoForDML)SpringUtil.getBean("supportTicketDaoForDML");
		currentUser = GetUser.getUserObj();
		
	}//doAfterCompose
	
	/**
	 * this method is for saving support form data into DB
	 */

	public void onClick$saveBtnId(){
		
		try {
			// to validate given values
			if(! validateSupportForm()){
				return;	
			}
			
			SupportTicket supportTicket = new SupportTicket();
			
			// type specification
			if(!(typeRadioRgId.getSelectedIndex() == -1)){
				
				if(bugRgId.isChecked()){
					supportTicket.setType((byte)1);
					
				}else if(featureReqRgId.isChecked()){
					supportTicket.setType((byte)2);
					
				}else if(serviceReqRgId.isChecked()){
					supportTicket.setType((byte)3);
					
				}else if(technicalReqRgId.isChecked()){
					supportTicket.setType((byte)4);
					
				}
				
			}// if
					
			// Client name
			String clientName = clientNametbId.getValue().trim();
			
			if(!clientName.isEmpty() ||  clientName.length() != 0 ){
				
				supportTicket.setClientName(clientName);
				logger.info("clientName is"+clientName);
				
			}
			
			// contact Name 
			String contactName= contactNametbId.getValue().trim();
			
			if(!contactName.isEmpty() ||  contactName.length() != 0 ){
						
				supportTicket.setContactName(contactName);
				logger.info("contactName is"+contactName);
						
			}
			
			//contact email address
			String contactEmail= contactEmailtbId.getValue().trim();
			
			
			
			if(!contactEmail.isEmpty() ||  contactEmail.length() != 0 ){
				
				if( !Utility.validateEmail(contactEmail.trim()) ) {
					
					MessageUtil.setMessage("Please provide valid Email address.", "color:red;");
					
					return ;
					
				}//if	
					
				supportTicket.setContactEmail(contactEmail);
				logger.info("contactEmail is"+contactEmail);		
			}
			
			// contact phone
			String contactPhone= contactPhonetbId.getValue().trim();
			
			if(!contactPhone.isEmpty() ||  contactPhone.length() != 0 ){

				/*if(contactPhone != null && contactPhone.length() > 0 && !Utility.validateUserPhoneNum(contactPhone)) {
					MessageUtil.setMessage(" Please provide valid Phone Number.","color:red;");
					return ;
				}*/
						
				supportTicket.setContactPhone(contactPhone);
				logger.info("contactPhone is"+contactPhone);		
			}
			
			
			
			//  product area type
			String productLabel=Constants.STRING_NILL;
			
			if(!(productAreaRadioRgId.getSelectedIndex() == -1)){
				
				if(websiteRgId.isChecked()){
					
					supportTicket.setProductAreaType((byte)1);
					
					websiteDivId.setVisible(true);
					posIntegrationDivId.setVisible(false);
					
					websiteLbId.setVisible(true);
					posIntegrationLbId.setVisible(false);
					
					if(websiteLbId.getSelectedIndex() != -1){
						
						productLabel = websiteLbId.getSelectedItem().getLabel();
						supportTicket.setProductArea(productLabel);
					
					}
					
				}  else if(posIntegrationRgId.isChecked()){
					
					supportTicket.setProductAreaType((byte)2);
					
					posIntegrationDivId.setVisible(true);
					websiteDivId.setVisible(false);
					
					posIntegrationLbId.setVisible(true);
					websiteLbId.setVisible(false);
					
					if(posIntegrationLbId.getSelectedIndex() != -1){
						
						productLabel = posIntegrationLbId.getSelectedItem().getLabel();
						supportTicket.setProductArea(productLabel);
						
					}
				}
			}// if
			
			// description
			String description= descriptiontbId.getValue().trim();
			
			if(!description.isEmpty() ||  description.length() != 0 ){
						
				supportTicket.setDescription(description);
				logger.info("description is"+description);		
			}
			
			// captcha
			String captcha= captchTbId.getValue().trim();
			
			if(!captcha.isEmpty() ||  captcha.length() != 0 ){
						
				supportTicket.setCaptcha(captcha);
				logger.info("captcha is"+captcha);		
			}
			 
			
			// TODO 
			
			// where file attachments
			
			String fileName = fileUploadtbId.getValue().trim();
			
			DateFormat format = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_");
			String timeStamp = format.format(new Date());
			
			if(!fileName.isEmpty() ||  fileName.length() != 0 ){
				
				supportTicket.setFileName(fileName);
				logger.info("fileName is"+fileName);	
			}
			
			
			String filePath= supportFileDir+File.separator;
			supportTicket.setFilePath(filePath+fileName);
			
			
			
			/*try {
				File supportFile= new File(filePath+timeStamp+fileName);
				BufferedWriter bw = new BufferedWriter(new FileWriter(supportFile));
				//bw.write(htmlStuff);
				bw.flush();
				bw.close();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				logger.error("Exception is" ,e1);
			}
			*/
			
			
			
			logger.info("filePath is"+filePath);
			
			supportTicket.setUserId(currentUser.getUserId().longValue());
			
			logger.info("user id is"+currentUser.getUserId().longValue());
			
			// set user name and user organization
			String userName = currentUser.getUserName();
			
			String usetNameStr =Utility.getOnlyUserName(userName);
			String userOrgStr = Utility.getOnlyOrgId(userName);
			
			supportTicket.setUserName(usetNameStr);
			logger.info("usetNameStr is"+usetNameStr);
			
			supportTicket.setUserOrgName(userOrgStr);
			logger.info("userOrgStr is"+userOrgStr);
			
			
			supportTicket.setCreatedDate(Calendar.getInstance());
			supportTicket.setStatus(Constants.SUPPORT_STATUS_ACTIVE);
			
			try {
				supportTicketDaoForDML.saveOrUpdate(supportTicket);
				MessageUtil.setMessage("Support request submitted successfully", "color:green;");
				fileUploadtbId.setAttribute("supportTicket", supportTicket);
				//TODO
				//Show as it is or empty form
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception " ,e);
			}
			
			// TODO send mail to support person as well as user acknowledgment email
			
			sendAcknowledgementToUser(supportTicket);
			
			sendMailToSupport(supportTicket);
			
			Redirect.goTo(PageListEnum.EMPTY);
			Redirect.goTo(PageListEnum.SUPPORT);
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception " ,e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception " ,e);
		}
		
		
	}//onClick$saveBtnId
	
	public void onCheck$productAreaRadioRgId(){
		
		
		if(websiteRgId.isChecked()){
			
			websiteDivId.setVisible(true);
			websiteLbId.setVisible(true);
			
			posIntegrationDivId.setVisible(false);
			posIntegrationLbId.setVisible(false);
			
		} else if(posIntegrationRgId.isChecked()){
			
			
			posIntegrationDivId.setVisible(true);
			posIntegrationLbId.setVisible(true); 
			
			websiteDivId.setVisible(false);
			websiteLbId.setVisible(false);
	}
	}
	
	/**
	 * this method is for validate  fields in support form
	 */
	
	public boolean validateSupportForm(){
		
		try {
			if(typeRadioRgId.getSelectedItem() == null) {
				
				MessageUtil.setMessage("Please specify the ticket type.","color:red","TOP");
				return false ; 
				
			}
			
			if(clientNametbId.getValue().trim().isEmpty() || clientNametbId.getValue().trim().length() == 0 ){
				MessageUtil.setMessage("Please provide client name", "color:red");
				return false;
				
			}
			
			if(contactNametbId.getValue().trim().isEmpty() || contactNametbId.getValue().trim().length() == 0 ){
				MessageUtil.setMessage("Please provide contact name", "color:red");
				return false;
				
			}
			
			if(contactPhonetbId.getValue().trim().isEmpty() || contactPhonetbId.getValue().trim().length() == 0 ){
				MessageUtil.setMessage("Please provide contact phone", "color:red");
				return false;
				
			}
			/*if(! validateNum(contactPhonetbId)) {
				MessageUtil.setMessage("Enter only number type value for the contact phone.","color:red;");
				return  false;			
			}//if
*/			
			
			
			if( contactPhonetbId.getValue().trim().length() > 0 && (Utility.phoneParse(contactPhonetbId.getValue().trim(),currentUser.getUserOrganization())==null)) {
				
				MessageUtil.setMessage("Please provide valid Phone number.", "Color:red", "Top");
				//long phone = Long.parseLong(value);
				return false;
			}
			
			if(contactEmailtbId.getValue().trim().isEmpty() || contactEmailtbId.getValue().trim().length() == 0 ){
				MessageUtil.setMessage("Please provide contact email address ", "color:red");
				return false;
				
			}
			
			if(productAreaRadioRgId.getSelectedItem() == null) {
				
				MessageUtil.setMessage("Please select the option specifying product area type.","color:red","TOP");
				return false; 
				
			}
			
			
			if(websiteRgId.isChecked()){
				logger.info("is enterd"+websiteLbId.getSelectedItem());
				
				if(websiteLbId.getSelectedItem().getLabel().equals(selectStr) ){
					logger.info("is enterd");
					
					MessageUtil.setMessage("Please select the web application  product area type.","color:red","TOP");
					return false; 
				}
			}else if(posIntegrationRgId.isChecked()){
				
				logger.info("is enterd"+posIntegrationLbId.getSelectedItem());
				if(posIntegrationLbId.getSelectedItem().getLabel().equals(selectStr)){
					
					MessageUtil.setMessage("Please select the pos integration  product area type.","color:red","TOP");
					return false; 
				}
			}
			
			if(descriptiontbId.getValue().trim().isEmpty() || descriptiontbId.getValue().trim().length() == 0 ){
				MessageUtil.setMessage("Please provide description", "color:red");
				return false;
				
			}
			
			if(captchTbId.getValue().trim().isEmpty() || captchTbId.getValue().trim().length() == 0 ){
				MessageUtil.setMessage("Please provide captcha value", "color:red");
				return false;
				
			}

			return true;
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error(" Exception in valdation" , e);
			return false;
		}
		
	}// validateSupportForm
	
	// to send acknowledgement to user
	public void sendAcknowledgementToUser(SupportTicket supportTicket){
		
		String supportAckMail = PropertyUtil.getPropertyValueFromDB(Constants.SUPPORT_ACK_MAIL);
		
		String contactName =supportTicket.getContactName();
		String emailStr =supportTicket.getContactEmail();
		String phoneStr = supportTicket.getContactPhone();
		String clientName=supportTicket.getClientName();
		String description = supportTicket.getDescription();
		
		
		String type=Constants.STRING_NILL;
		
		  if(supportTicket.getType() == 1 ){
			  
			  type =Constants.SUPPORT_TYPE_BUG; 
			  
		}else if(supportTicket.getType() == 2){
			
			 type =Constants.SUPPORT_TYPE_FEATURE;
			 
		}else if(supportTicket.getType() == 3){
			
			 type =Constants.SUPPORT_TYPE_SERVICE;
			 
		}else if(supportTicket.getType() == 4){
			
			 type =Constants.SUPPORT_TYPE_TECH;
		}
		
		String productAreaType=Constants.STRING_NILL;
		
		if(supportTicket.getProductAreaType() == 1){
			
			productAreaType = Constants.SUPPORT_PRODUCT_TYPE_WEB;
			
		}else if(supportTicket.getProductAreaType() == 2){
			
			productAreaType = Constants.SUPPORT_PRODUCT_TYPE_POS;
			
			
		}
		
		String productArea = supportTicket.getProductArea();
		
		
		
		
		String orgStr =Constants.SUPPORT_ORG_NAME;
		
		supportAckMail = supportAckMail.replace(Constants.SUPPORT_SENDER_NAME, orgStr)
				.replace(Constants.SUPPORT_USER_CONTACT_NAME, contactName)
				.replace(Constants.SUPPORT_USER_CONTACT_EMAIL, emailStr)
				.replace(Constants.SUPPORT_USER_CLEINT_NAME, clientName)
				.replace(Constants.SUPPORT_USER_CONTACT_NAME, contactName)
				.replace(Constants.SUPPORT_USER_CONTACT_PHONE, phoneStr)
				.replace(Constants.SUPPORT_USER_DESCRIPTION, description)
				.replace(Constants.SUPPORT_TYPE, type)
				.replace(Constants.SUPPORT_PRODUCT_AREA_TYPE, productAreaType)
				.replace(Constants.SUPPORT_PRODUCT_AREA, productArea);
				
		htmlContent = supportAckMail;
		textContent = htmlContent;
		
		// added for subject	
		String subject="Your support request number "+supportTicket.getTicketId();
		String createsDatestr = MyCalendar.calendarToString(supportTicket.getCreatedDate(),MyCalendar.FORMAT_MDATEONLY, tz);
		subject=subject+" dated " +createsDatestr;
		
		//if(currentUser.getVmta().equalsIgnoreCase("SendGridAPI")){
		 if(Constants.VMTA_SENDGRIDAPI.equalsIgnoreCase(currentUser.getVmta().getVmtaName())){	
			try {
				 

				if(currentUser != null) {
					messageHeader =  "{\"unique_args\": {\"userId\": \""+ currentUser.getUserId() +"\" ,\"EmailType\" : \""+Constants.EMAIL_TYPE_SUPPORT_ACK +"\",\"sentId\" : \""+supportTicket.getTicketId()+"\" ,\"ServerName\": \""+ serverName +"\" }}";
				}
				//logger.debug("SENDING THROUGH sendGridAPI ...>>>>>>>>>>>>");
				ExternalSMTPSupportSender externalSMTPSupportSender =  new ExternalSMTPSupportSender();
				htmlContent = htmlContent.replace("[sentId]", supportTicket.getTicketId()+"");
				textContent = textContent.replace("[sentId]", supportTicket.getTicketId()+"");
				
				// 2.4.3 asana task, changing From Name settings. - rajeev date 24th july 2015 - place2
				/*externalSMTPSupportSender.submitSupportAckMail(messageHeader, htmlContent, textContent,supportEmail,
						subject,  supportTicket.getContactEmail());*/
				String supportFromNameSetting = "OptCulture Support"+"<"+supportEmail+">";
				externalSMTPSupportSender.submitSupportAckMail(messageHeader, htmlContent, textContent,supportFromNameSetting,
						subject,  supportTicket.getContactEmail());// 2.4.3 asana task, changing From Name settings. - rajeev date 24th july 2015 - place1
			
			} catch (Exception e) {
				
				logger.debug("Exception while sending through sendGridAPI .. returning ",e);
				
			}
		}
			
		
		
		
		
		
	}//sendAcknowledgementtoUser
	
	// to send mail to support people regarding support request
	public void sendMailToSupport(SupportTicket supportTicket){
		
		
			String supportMail = PropertyUtil.getPropertyValueFromDB(Constants.SUPPORT_MAIL);
		
	 		String userName= supportTicket.getUserName();
			String contactName =supportTicket.getContactName();
			String emailStr =supportTicket.getContactEmail();
			String phoneStr = supportTicket.getContactPhone();
			String clientName=supportTicket.getClientName();
			String description = supportTicket.getDescription();
			String productArea = supportTicket.getProductArea();
			
			String orgStr =Constants.SUPPORT_ORG_NAME;
			
			String type=Constants.STRING_NILL;
			
			  if(supportTicket.getType() == 1 ){
				  
				  type =Constants.SUPPORT_TYPE_BUG; 
				  
			}else if(supportTicket.getType() == 2){
				
				 type =Constants.SUPPORT_TYPE_FEATURE;
				 
			}else if(supportTicket.getType() == 3){
				
				 type =Constants.SUPPORT_TYPE_SERVICE;
				 
			}else if(supportTicket.getType() == 4){
				
				 type =Constants.SUPPORT_TYPE_TECH;
			}
			
			String productAreaType=Constants.STRING_NILL;
			
			if(supportTicket.getProductAreaType() == 1){
				
				productAreaType = Constants.SUPPORT_PRODUCT_TYPE_WEB;
				
			}else if(supportTicket.getProductAreaType() == 2){
				
				productAreaType = Constants.SUPPORT_PRODUCT_TYPE_POS;
				
				
			}
			
			String fileName = supportTicket.getFileName();
			
			String filePath = supportFileDir+File.separator+fileName;
			
			supportMail = supportMail.replace(Constants.SUPPORT_USER_CONTACT_EMAIL, emailStr)
								.replace(Constants.SUPPORT_USER_CLEINT_NAME, clientName)
								.replace(Constants.SUPPORT_USER_CONTACT_NAME, contactName)
								.replace(Constants.SUPPORT_USER_CONTACT_PHONE, phoneStr)
								.replace(Constants.SUPPORT_USER_DESCRIPTION, description)
								.replace(Constants.SUPPORT_TYPE, type)
								.replace(Constants.SUPPORT_PRODUCT_AREA_TYPE, productAreaType)
								.replace(Constants.SUPPORT_SENDER_NAME, orgStr)
								.replace(Constants.SUPPORT_PRODUCT_AREA, productArea);
						
			htmlContent = supportMail;
			textContent = htmlContent;
			
			// added for subject	
			String subject= currentUser.getUserOrganization().getOrganizationName() + " support request number "+supportTicket.getTicketId();
			String createsDatestr = MyCalendar.calendarToString(supportTicket.getCreatedDate(),MyCalendar.FORMAT_MDATEONLY, tz);
			subject=subject+"  of date " +createsDatestr;
				
			//if(currentUser.getVmta().equalsIgnoreCase("SendGridAPI")){
			if(Constants.VMTA_SENDGRIDAPI.equalsIgnoreCase(currentUser.getVmta().getVmtaName())){	
				try {
					 
					

					if(currentUser != null) {
						messageHeader =  "{\"unique_args\": {\"userId\": \""+ currentUser.getUserId() +"\" ,\"EmailType\" : \""+Constants.EMAIL_TYPE_SUPPORT +"\",\"sentId\" : \""+supportTicket.getTicketId()+"\" ,\"ServerName\": \""+ serverName +"\" }}";
					}
					//logger.debug("SENDING THROUGH sendGridAPI ...>>>>>>>>>>>>");
					ExternalSMTPSupportSender externalSMTPSupportSender =  new ExternalSMTPSupportSender();
					htmlContent = htmlContent.replace("[sentId]", supportTicket.getTicketId()+"");
					textContent = textContent.replace("[sentId]", supportTicket.getTicketId()+"");
					/*externalSMTPSupportSender.submitSupportMail(messageHeader, htmlContent, textContent,supportEmail,
							subject, supportEmail, fileName,filePath);*/
					String supportFromNameSetting = "OptCulture Support"+"<"+supportEmail+">";
					externalSMTPSupportSender.submitSupportMail(messageHeader, htmlContent, textContent,supportFromNameSetting,
							subject, supportEmail, fileName,filePath);// 2.4.3 asana task, changing From Name settings. - rajeev date 24th july 2015 - place2
				
				} catch (Exception e) {
					
					logger.debug("Exception while sending through sendGridAPI .. returning ",e);
					
				}
			}
				
		}//sendMailToSupport
	
	
	// Added for uploading attachemnts
	
	public void onClick$uploadAId(){
		
		MessagesDao messagesDao = (MessagesDao) SpringUtil.getBean("messagesDao");
		Media media = null;
		try {
			 media = Fileupload.get();	
			MessageUtil.clearMessage();
			if(media == null) {
				MessageUtil.setMessage("Please select a file.", "color:red", "TOP");
				return;
			}
			if(media != null){
				
				if(media.getByteData().length >= 500*1024){
					MessageUtil.setMessage("File size should be less than or equal to 500KB .", "color:red", "TOP");
					
					return;
					
				}
			}
			
			

			DateFormat format = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_");
			String timeStamp = format.format(new Date());
			
			String fileNameStr =timeStamp+((Media)media).getName().trim();
			
			String path = supportFileDir +File.separator+fileNameStr;
			String ext = FileUtil.getFileNameExtension(path.trim());
			
			
			if(!validateFileExtn(path.trim())){
				
				MessageUtil.setMessage("Upload image files / Doc type files only .","color:red","TOP");
				return;
				
			}
			
			
			
			
			
		//	String pathString = PropertyUtil.getPropertyValue("usersParentDirectory").trim() +File.separator+ "Support"+File.separator+ media.getName().trim();
			//boolean isSuccess = copyDataFromMediaToFile(pathString,media);
			fileUploadtbId.setValue(fileNameStr);
			fileUploadtbId.setDisabled(true);
			
			
			
			File file1  = new File(path.trim());
			//logger.debug("imageName is already exist >>> ::"+file.exists());
			if(file1.exists()) {
				MessageUtil.setMessage("Image already exists.","color:blue","TOP");
				return;
			}
			
			byte[] fi = media.getByteData();
			BufferedInputStream in = new BufferedInputStream (new ByteArrayInputStream (fi)); 
			FileOutputStream out = new FileOutputStream (new File(path.trim()));
			//Copy the contents of the file to the output stream
			byte[] buf = new byte[1024];
			int count = 0;
			while ((count = in.read(buf)) >= 0){
				out.write(buf, 0, count);
			}
			in.close();
			out.close();
			
			
			
			
			
			
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("exception" ,e);
		} catch (Exception e) {
			String message = "Support attachment upload failed,"+media.getName()+"\n could not copied reason may be due to network problem or may be very large file";
			Users user = GetUser.getUserObj();
			(new MessageHandler(messagesDao,user.getUserName())).sendMessage("Support","uploaded failed",message,"Inbox",false,"INFO", user);
			return ;
		}
		
		
		
		
		
		
	}//onClick$uploadAId
	
	public static boolean validateFileExtn(String fileName){
        
        Matcher mtch = fileExtnPtrn.matcher(fileName);
        if(mtch.matches()){
            return true;
        }
        return false;
    }
	
	
	public boolean validateNum(Textbox txtbox) {
		
		
		//logger.debug("----just entered with the intbox======>"+txtbox);
		
		String tbErrorCss = "border:1px solid #F37373; background:#FFCFCF";
		
		String tbNormalCss = "border:1px solid #B2B0B1; background:url('/subscriber/zkau/web/1d1ebab6/zul/img/misc/text-bg.gif') repeat-x scroll 0 0 #FFFFFF";
		try {
		if(txtbox.isValid()){
		int str = Integer.parseInt(txtbox.getValue().trim());
		if(  str <= 0) {
			
			txtbox.setStyle(tbErrorCss);
			
			
			return false;
			
		}//if
		txtbox.setStyle(tbNormalCss);
		}
		
		return true;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		txtbox.setStyle(tbErrorCss);
		logger.error("Exception " ,e);
		return false;
	}
	
}//validateNum(-)
	
	
	
	 public void onClick$reGenratBtnId(){
		 
		 captchTbId.setValue("");
	 }
	 
	 
	 
		
	}
