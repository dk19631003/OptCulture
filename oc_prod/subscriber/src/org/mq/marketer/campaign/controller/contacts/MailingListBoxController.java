package org.mq.marketer.campaign.controller.contacts;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;


@SuppressWarnings("unused")
public class MailingListBoxController extends AbstractController{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private ContactsDao contactsDao ;
	private MailingListDao mailingListDao;
	private CustomTemplatesDao customTemplatesDao;
	private ContactsDaoForDML contactsDaoForDML;
	
	
	public ContactsDaoForDML getContactsDaoForDML() {
		return contactsDaoForDML;
	}
	public void setContactsDaoForDML(ContactsDaoForDML contactsDaoForDML) {
		this.contactsDaoForDML = contactsDaoForDML;
	}

	
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
	
	public void setCustomTemplatesDao(CustomTemplatesDao customTemplatesDao) {
		this.customTemplatesDao = customTemplatesDao;
	}
	
	public CustomTemplatesDao getCustomTemplatesDao() {
		return customTemplatesDao;
	}
	
	private PrintWriter responseWriter;
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, Exception {
		
		String err = "";
		
		responseWriter = response.getWriter();
		StringBuffer sb = new StringBuffer("<html><head><style type='text/css'>span { font-family:Verdana,Tahoma,Arial,Helvetica,sans-serif;" +
				"font-size:13px;}</style> <script type='text/javascript' src='/subscriber/js/validation.js'></script></head>");
		
		if(request.getParameter("uId") == null || request.getParameter("mId") == null) {
			logger.error("Request is missing required parameters.");
			err = "Request is missing required parameters.";
			sb.append("<div id='errorDivId'>"+err+"</div>");
			
			responseWriter.print(sb);
			responseWriter.flush();
			responseWriter.close();
			
			return null;
		}
		
		Long userId = Long.parseLong(request.getParameter("uId")); 
		Long mListId = Long.parseLong(request.getParameter("mId")); 
		
		// Executes on Form Submit.
		if("true".equals(request.getParameter("process")))
		{
			
			try {
				String userName = request.getParameter("contactName");
				String emailId = request.getParameter("emailId");
				
//				logger.info(" User Name :"+ userName + " Email Id :" + emailId + " mList :" + mListId + " userId :"+ userId);
				
				if(!Utility.validateEmail(emailId)) {
					logger.error("Invalid Email Id.");
					err = "Email Id is invalid.<br/>";
				}
				
				MailingList mailingList = mailingListDao.find(mListId);
				
				if(mailingList == null) {
					err = "Contacts list does not exist.<br/>";
				} 
				else {
				Contacts contact = new Contacts(mailingList, emailId, Constants.CONT_STATUS_PURGE_PENDING);
				contactsDaoForDML.saveOrUpdate(contact);
				err = "Contact added successfully.<br/>";
				}
				
//				logger.info(" User Name :"+ userName + " Email Id :" + emailId + " mList :" + mListId + " userId :"+ userId);
				//sb.append("Contact added successfully.");
				
			} catch(DataIntegrityViolationException e) {
			    err = "Email Id already exists.<br/>";	
			} catch (Exception e) {
				logger.error("**Exception : "+(Throwable)e);
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
		
		// Replace uId
		this.replaceSBContent(finalHTML, "formHiddenUid", userId.toString());
			
		
		// Replace mId
		this.replaceSBContent(finalHTML, "formHiddenMList", mListId.toString());
				
		// Replace dId
		this.replaceSBContent(finalHTML, "formHiddenDid", designId.toString());
		
		// Replace Js onclick fnc. 
		this.replaceSBContent(finalHTML, "onclickjscall", "return ValidateEmail();");
		
		
		// Replace err Message
		String errStr = "<div id=\"errorDivId\"></div>";
		int index = finalHTML.indexOf(errStr);
		err = "<div id='errorDivId'>" + err + "</div>";
		while(index > 0) {
			finalHTML.replace(index, index + errStr.length(), err);
			index = finalHTML.indexOf(errStr);
		}
		logger.info(finalHTML.toString());
		
		//sb.append("<div id='errorDivId'>"+err+"</div><div style='font-size:15px;color:blue;font-family:verdena;font-weight:bold;margin-top:50px'><form action='" + request.getRequestURI() + "' method='post'>UserName : <input type='text' name='contactName'><br/>Email :<input type='text' id='emailId' name='emailId'><br/><input type='HIDDEN' name='process' value='true' /><input type='HIDDEN' name='uId' value='2' /><input type='HIDDEN' name='mId' value='140' /><input type='submit' value='Submit' onClick='return ValidateEmail();'></form></div></html>");
		sb.append("<body>" + finalHTML.toString() + "</body></html>");
		
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

}
