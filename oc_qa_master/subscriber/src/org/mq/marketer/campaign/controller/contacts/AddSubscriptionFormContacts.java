package org.mq.marketer.campaign.controller.contacts;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ContactParentalConsent;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.CustomFieldData;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.ContactParentalConsentDao;
import org.mq.marketer.campaign.dao.ContactParentalConsentDaoForDML;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.CustomFieldDataDao;
import org.mq.marketer.campaign.dao.CustomFieldDataDaoForDML;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;


@SuppressWarnings("unused")
public class AddSubscriptionFormContacts extends AbstractController{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private ContactsDao contactsDao ;
	private MailingListDao mailingListDao;
	private MLCustomFieldsDao mlCustomFieldsDao;
	private CustomFieldDataDao cfDataDao;
	private CustomFieldDataDaoForDML cfDataDaoForDML;
	public CustomFieldDataDaoForDML getCfDataDaoForDML() {
		return cfDataDaoForDML;
	}
	public void setCfDataDaoForDML(CustomFieldDataDaoForDML cfDataDaoForDML) {
		this.cfDataDaoForDML = cfDataDaoForDML;
	}

	private EmailQueueDao emailQueueDao;
	private EmailQueueDaoForDML emailQueueDaoForDML;
	
	public EmailQueueDaoForDML getEmailQueueDaoForDML() {
		return emailQueueDaoForDML;
	}
	public void setEmailQueueDaoForDML(EmailQueueDaoForDML emailQueueDaoForDML) {
		this.emailQueueDaoForDML = emailQueueDaoForDML;
	}
	public ContactsDaoForDML getContactsDaoForDML() {
		return contactsDaoForDML;
	}
	public void setContactsDaoForDML(ContactsDaoForDML contactsDaoForDML) {
		this.contactsDaoForDML = contactsDaoForDML;
	}

	private ContactsDaoForDML contactsDaoForDML;
	
	public EmailQueueDao getEmailQueueDao() {
		return emailQueueDao;
	}
	public void setEmailQueueDao(EmailQueueDao emailQueueDao) {
		this.emailQueueDao = emailQueueDao;
	}


	//CustomFieldDataDao cfDataDao = (CustomFieldDataDao)SpringUtil.getBean("cfDataDao");
	
	//MLCustomFieldsDao mlCFDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
	
	public ContactsDao getContactsDao(){
		return this.contactsDao;
	}
	public void setContactsDao(ContactsDao contactsDao){
		this.contactsDao = contactsDao;
	}
	
	public MailingListDao getMailingListDao() {
		return mailingListDao;
	}
	
	public void setMailingListDao(MailingListDao mailingListDao) {
		this.mailingListDao = mailingListDao;
	}
	
	private CustomTemplatesDao customTemplatesDao;
	public void setCustomTemplatesDao(CustomTemplatesDao customTemplatesDao) {
		this.customTemplatesDao = customTemplatesDao;
	}
	
	public CustomTemplatesDao getCustomTemplatesDao() {
		return customTemplatesDao;
	}
	
	public MLCustomFieldsDao getMlCustomFieldsDao() {
		return mlCustomFieldsDao;
	}
	public void setMlCustomFieldsDao(MLCustomFieldsDao mlCustomFieldsDao) {
		this.mlCustomFieldsDao = mlCustomFieldsDao;
	}
	public CustomFieldDataDao getCfDataDao() {
		return cfDataDao;
	}
	public void setCfDataDao(CustomFieldDataDao cfDataDao) {
		this.cfDataDao = cfDataDao;
	}
	
	
	private PurgeList purgeList;
	public void setPurgeList(PurgeList purgeList) {
		this.purgeList = purgeList;
	}
	
	public PurgeList getPurgeList() {
		return this.purgeList;
	}
	
	
	private ContactParentalConsentDao contactParentalConsentDao;
	private ContactParentalConsentDaoForDML contactParentalConsentDaoForDML;
	
	public ContactParentalConsentDaoForDML getContactParentalConsentDaoForDML() {
		return contactParentalConsentDaoForDML;
	}
	public void setContactParentalConsentDaoForDML(
			ContactParentalConsentDaoForDML contactParentalConsentDaoForDML) {
		this.contactParentalConsentDaoForDML = contactParentalConsentDaoForDML;
	}
	public ContactParentalConsentDao getContactParentalConsentDao() {
		return contactParentalConsentDao;
	}
	public void setContactParentalConsentDao(
			ContactParentalConsentDao contactParentalConsentDao) {
		this.contactParentalConsentDao = contactParentalConsentDao;
	}


	private PrintWriter responseWriter;
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, Exception {
		logger.info("---just entered---");
		response.setContentType("text/html");
		responseWriter = response.getWriter();
		String err = "";
		String emailId="";
		String parentEmail = "";
		
		StringBuffer sb = new StringBuffer("<html><head><style type='text/css'>span { font-family:Verdana,Tahoma,Arial,Helvetica,sans-serif;" +
		"font-size:10px;}</style> <script type='text/javascript' src='/subscriber/js/validation.js'></script></head>");
		
				
		if(request.getParameter("uId") == null || request.getParameter("mId") == null) {
			logger.error("Request is missing required parameters.");
			err = "Request is missing required parameters.";
			sb.append("<div id='errorDivId'><span style='color:red;font-size:10px;'>Errors:</span>"+err+"</div>");
			
			responseWriter.print(sb);
			responseWriter.flush();
			responseWriter.close();
			
			return null;
		}
		
		Long userId = Long.parseLong(request.getParameter("uId")); 
		Long mListId = Long.parseLong(request.getParameter("mId")); 
		
		// Executes on Form Submit.
		if("true".equals(request.getParameter("process"))) {    
			//logger.info("-------2-----");
			
			try {
				MailingList mailingList = mailingListDao.find(mListId);
				
				if(mailingList == null) {
					err = "Contacts list does not exist.<br/>";
				} 
				else {
					logger.info("just entered in else part");
					Contacts contact = new Contacts(mailingList,"",Constants.CONT_STATUS_PURGE_PENDING);
					
					//added after implementation for contact's optin medium
					contact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM);
					
					
					/*contact.setMailingList(mailingList);
				    contact.setEmailStatus(Constants.CONT_STATUS_PURGE_PENDING);
				    contact.setEmailId("");*/
				    //contactsDao.saveOrUpdate(contact);
				    logger.info("----contact hasbeen saved id is...>"+contact.getContactId()+"      "+contact.getEmailStatus());
				
				    Enumeration paramNames = request.getParameterNames();
				    
				  while (paramNames.hasMoreElements()) {
				    String name = (String) paramNames.nextElement();
				    logger.info(name);
				    
				    
				    if(name.startsWith("Email_")){
				    	//logger.info("-------3-----");
				    	contact.setEmailId(request.getParameter(name.trim()));
				    	emailId=request.getParameter(name);
						  if(!Utility.validateEmail(emailId)) {
							  logger.error("Invalid Email Id.");
							  err = "Email Id is invalid.<br/>";
						  }

				    }else
				    if(name.trim().startsWith("First Name_")){
				    	//logger.info("-------4-----");
				    	contact.setFirstName(request.getParameter(name));
				    }else
				    if(name.trim().startsWith("Last Name_")){
				    	//logger.info("-------5-----");
				    	contact.setLastName(request.getParameter(name));
				    }else
				    if(name.trim().startsWith("Address One_")){
				    	//logger.info("-------6-----");
				    	contact.setAddressOne(request.getParameter(name));
				    }else
				    if(name.trim().startsWith("Address Two_")){
				    	//logger.info("-------7-----");
				    	//logger.info("address two is====>"+request.getParameter(name));
				    	contact.setAddressTwo(request.getParameter(name));
				    }else
				    if(name.startsWith("City_")){
				    	//logger.info("-------8-----");
				    	contact.setCity(request.getParameter(name));
				    }else
				    if(name.startsWith("State_")){
				    	//logger.info("-------9-----");
				    	contact.setState(request.getParameter(name));
				    }else
				    if(name.startsWith("Country_")){
				    	//logger.info("-------10-----");
				    	contact.setCountry(request.getParameter(name));
				    }else
				    if(name.startsWith("Pin_")){
				    	//logger.info("------11------");
//				    	contact.setPin(Integer.parseInt(request.getParameter(name)));
				    	contact.setZip(request.getParameter(name));
				    }else
				    if(name.startsWith("Phone_")){
				    	//logger.info("------12------");
//				    	contact.setPhone(Long.parseLong(request.getParameter(name)));
				    	
				    	String mobileStr = request.getParameter(name);
				    	if(mobileStr != null && mobileStr.trim().length() >0 ) {
				    		Users currentUser = mailingList.getUsers();
				    		UserOrganization organization = currentUser != null ? currentUser.getUserOrganization() : null ;
				    		mobileStr = Utility.phoneParse(mobileStr,organization);
				    		contact.setMobilePhone(mobileStr);
				    	}
				    }
				    else 
				    	if(name.startsWith("Parent Email_")) {
				    		
				    		//contact.setEmailId(request.getParameter(name.trim()));
					    	parentEmail=request.getParameter(name);
							  if(!Utility.validateEmail(parentEmail)) {
								  logger.error("Invalid parent Email Id.");
								  err = "parent Email Id is invalid.<br/>";
							  }
				    }
			    	 else 
				    	if(name.startsWith("BirthDay_")) {
				    		
				    		String bday = request.getParameter(name.trim());
				    		
				    		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
				    		Date  date = (Date)formatter.parse(bday); 
				    		
				    		Calendar cal = Calendar.getInstance();
				    		cal.setTime(date);
				    		contact.setBirthDay(cal);
				    		//contact.setEmailId(request.getParameter(name.trim()));
					    	
					    }
						    
				    else
				    if(name.startsWith("CF:")){
				    	//Set<MailingList> mlSet = contact.getMlSet();
				    	//mlSet.add(mailingList);
				    	//contact.setMlSet(mlSet);
				    	contact.setMlBits(contact.getMlBits().longValue() | mailingList.getMlBit().longValue());
				    	contact.setUsers(mailingList.getUsers());
				    	contactsDaoForDML.saveOrUpdate(contact);
				    	//logger.info("----contact hasbeen saved id is...>"+contact.getContactId()+"      "+contact.getEmailStatus());
				    	//logger.info("-----13------"+request.getParameter(name));
					    String[] custFields = name.split("_");
					    int index = mlCustomFieldsDao.findIndexByNameMl(mailingList, custFields[0].split(":")[1]);
					    String dt= mlCustomFieldsDao.findDataType(mailingList, custFields[0].split(":")[1]);
					    //logger.info("index is -------14 "+index);
					    CustomFieldData customFieldData = new CustomFieldData(contact);
					    //logger.info("customfielddata obj is created with contact is..>"+contact.getContactId());
					    if(CustomFieldValidator.validate(request.getParameter(name),dt)){
						  err="type is not matching for the data entered";
						 
						  customFieldData = storCfData(index,customFieldData,request.getParameter(name));
					    }else{
						  customFieldData = storCfData(index,customFieldData,null);
					    }
						  /*
						   * if (CustomFieldValidator.validate(data,dt)) {
								storeCFData(cfData,index,(String)field2_List.get(i));
							}
							else {
								storeCFData(cfData,index,null);
							}
						   */
					  
					  //customFieldData = storCfData(index,customFieldData,request.getParameter(name));
					//  cfDataDao.saveOrUpdate(customFieldData);
					  cfDataDaoForDML.saveOrUpdate(customFieldData);

				    }
			    
				   
				  }//while
				  //logger.info("-------14-----");
				  
				  
				  
			      contactsDaoForDML.saveOrUpdate(contact);
			      if(mailingList.isCheckParentalConsent() && contact.getBirthDay() != null) {
			    	  
			    	  
			    	  if(((contact.getBirthDay().getTimeInMillis()-Calendar.getInstance().getTimeInMillis())/(1000 * 60*60*24)) <= (365*13)) {
			    		  
			    		  ContactParentalConsent contactConsent = new ContactParentalConsent(contact.getContactId(), parentEmail,
			    				  Constants.CONT_PARENTAL_STATUS_PENDING_APPROVAL, mailingList.getUsers().getUserId(), Calendar.getInstance(),contact.getEmailId());
			    		  
			    		  
			    		  //contactParentalConsentDao.saveOrUpdate(contactConsent);
			    		  contactParentalConsentDaoForDML.saveOrUpdate(contactConsent);

			    		  CustomTemplates custTemplate = null;
			    		  String message = PropertyUtil.getPropertyValueFromDB("parentalConsentMsgtemplate");
			    		  if(mailingList.getConsentCutomTempId() != null) {
			    			  custTemplate = customTemplatesDao.findCustTemplateById(mailingList.getConsentCutomTempId());
			    			  if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
								  message = custTemplate.getHtmlText();
								}
			    			  else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && 
			    					  custTemplate.getMyTemplateId()!=null) {
									try {
										MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
										 MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
										 if(myTemplates != null) message = myTemplates.getContent();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
			    		  }
			    		  Utility.sendInstantMail(null, "Require Parental Consent.", message,
			    				  Constants.EQ_TYPE_TEST_OPTIN_MAIL, parentEmail, custTemplate, emailQueueDao,emailQueueDaoForDML, mailingList.getUsers());
			    		  
			    		  
			    		  
			    	  }
			    	  
			    	  
			    	  
			    	  
			    	  
			    	  
			      }
			      
			      
			      
				  List<Long> list = new ArrayList<Long>();
				  list.add(mailingList.getListId());
				  purgeList.addAndStartPurging(mailingList.getUsers().getUserId(), list);
				  err = "Contact added successfully.<br/>";
				  
				/*String userName = request.getParameter("contactName");
				String emailId = request.getParameter("emailId");
				
				logger.info(" User Name :"+ userName + " Email Id :" + emailId + " mList :" + mListId + " userId :"+ userId);*/
				
				
				/*MailingList mailingList = mailingListDao.find(mListId);
				
				if(mailingList == null) {
					err = "Contacts list does not exist.<br/>";
				} */
				/*else {
				Contacts contact = new Contacts(mailingList, emailId, Constants.CONT_STATUS_PURGE_PENDING);
				contactsDao.saveOrUpdate(contact);
				//start purging
				List<Long> list = new ArrayList<Long>();
				list.add(mailingList.getListId());
				purgeList.addAndStartPurging(list);
				err = "Contact added successfully.<br/>";
				}
				
				logger.info(" User Name :"+ userName + " Email Id :" + emailId + " mList :" + mListId + " userId :"+ userId);
				//sb.append("Contact added successfully.");*/
				
			} 
		}catch(DataIntegrityViolationException e) {
		    err = "Email Id already exists.<br/>";	
		}catch (NumberFormatException e) {
			err = "provide values as number type only.<br/>";
		} 
		catch (Exception e) {
			logger.error("**Exception : ",e);
			
		}	
		
		}
		
		// Check if both Mlist name or mListId are null 
		if(request.getParameter("dId") == null) {
			logger.error("Request is missing required parameters.");
			err = "Request is missing required parameters.<br/>";
			sb.append("<div id='errorDivId'>"+err+"</div>");
			
			responseWriter.print(sb);
			responseWriter.flush();
	        responseWriter.close();
	        
			return null;
		}
		
		Long designId = Long.parseLong(request.getParameter("dId"));
		StringBuffer finalHTML = new StringBuffer(customTemplatesDao.getTemplateHTMLById(designId));
		logger.info("the Html content is"+finalHTML);
		
		// Replace uId
		this.replaceSBContent(finalHTML, "formHiddenUid", userId.toString());
			
		// Replace Action Attribute
		this.replaceSBContent(finalHTML, "subscrptnFrmActnAtt", PropertyUtil.getPropertyValue("subscriptionSrc")); 
		
		// Replace mId
		this.replaceSBContent(finalHTML, "formHiddenMList", mListId.toString());
				
		// Replace dId
		this.replaceSBContent(finalHTML, "formHiddenDid", designId.toString());
		
		// Replace Js onclick fnc. 
		this.replaceSBContent(finalHTML, "onclickjscall", "return ValidateEmail();");
		
		this.replaceSBContent(finalHTML, "onClickjsFunctionCall", "clearText(this);");
		// Replace err Message
		String errStr = "<div id='errorDivId'></div>";
		int index = finalHTML.indexOf(errStr);
		logger.info("index is========>"+index);
		if(!err.trim().equals("")){
			err = "<div id='errorDivId'>" + err + "</div>";
			while(index > 0) {
				finalHTML.replace(index, index + errStr.length(), err);
				index = finalHTML.indexOf(errStr);
			}
		}
		logger.info("the final html content is--->"+finalHTML.toString());
		
		//sb.append("<div id='errorDivId'>"+err+"</div><div style='font-size:15px;color:blue;font-family:verdena;font-weight:bold;margin-top:50px'><form action='" + request.getRequestURI() + "' method='post'>UserName : <input type='text' name='contactName'><br/>Email :<input type='text' id='emailId' name='emailId'><br/><input type='HIDDEN' name='process' value='true' /><input type='HIDDEN' name='uId' value='2' /><input type='HIDDEN' name='mId' value='140' /><input type='submit' value='Submit' onClick='return ValidateEmail();'></form></div></html>");
		sb.append("<html><body>" + finalHTML.toString() + "</body></html>");
		
		responseWriter.print(sb);
		responseWriter.flush();
        responseWriter.close();
        
		return null;
		
	}
	
	private void replaceSBContent(StringBuffer mainSB, String placeHolder,String replaceWith) {
		String tempStr = PropertyUtil.getPropertyValue(placeHolder);
		int index = mainSB.indexOf(tempStr);
		while(index > 0) {
			mainSB.replace(index, index + tempStr.length(), replaceWith);
			index = mainSB.indexOf(tempStr);
		}
	}
	
	private CustomFieldData storCfData(int index,CustomFieldData customFieldData, String data) {
		switch(index){
		case 1:{
			customFieldData.setCust1(data);
			return customFieldData;
		}
		case 2:{
			customFieldData.setCust2(data);
			return customFieldData;
		}
		case 3:{
			customFieldData.setCust3(data);
			return customFieldData;
		}
		case 4:{
			customFieldData.setCust4(data);
			return customFieldData;
		}
		case 5:{
			customFieldData.setCust5(data);
			return customFieldData;
		}
		case 6:{
			customFieldData.setCust6(data);
			return customFieldData;
		}
		case 7:{
			customFieldData.setCust7(data);
			return customFieldData;
		}
		case 8:{
			customFieldData.setCust8(data);
			return customFieldData;
		}
		case 9:{
			customFieldData.setCust9(data);
			return customFieldData;
		}
		case 10:{
			customFieldData.setCust10(data);
			return customFieldData;
		}
		case 11:{
			customFieldData.setCust11(data);
			return customFieldData;
		}
		case 12:{
			customFieldData.setCust12(data);
			return customFieldData;
		}
		case 13:{
			customFieldData.setCust13(data);
			return customFieldData;
		}
		case 14:{
			customFieldData.setCust14(data);
			return customFieldData;
		}
		case 15:{
			customFieldData.setCust15(data);
			return customFieldData;
		}
		case 16:{
			customFieldData.setCust16(data);
			return customFieldData;
		}
		case 17:{
			customFieldData.setCust17(data);
			return customFieldData;
		}
		case 18:{
			customFieldData.setCust18(data);
			return customFieldData;
		}
		case 19:{
			customFieldData.setCust19(data);
			return customFieldData;
		}
		case 20:{
			customFieldData.setCust20(data);
			return customFieldData;
		}
		}
		return customFieldData;
	}
	
	

}
