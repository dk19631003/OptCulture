package org.mq.captiway.scheduler.campaign;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jasypt.util.text.BasicTextEncryptor;
import org.mq.captiway.scheduler.beans.CampaignReport;
import org.mq.captiway.scheduler.beans.CampaignSent;
import org.mq.captiway.scheduler.beans.Campaigns;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.CustomTemplates;
import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.OptInReport;
import org.mq.captiway.scheduler.beans.UserFromEmailId;
import org.mq.captiway.scheduler.dao.ContactParentalConsentDao;
import org.mq.captiway.scheduler.dao.ContactParentalConsentDaoForDML;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.CustomTemplatesDao;
import org.mq.captiway.scheduler.dao.EmailQueueDao;
import org.mq.captiway.scheduler.dao.EmailQueueDaoForDML;
import org.mq.captiway.scheduler.dao.MailingListDao;
import org.mq.captiway.scheduler.dao.OptInReportDao;
import org.mq.captiway.scheduler.dao.OptInReportDaoForDML;
import org.mq.captiway.scheduler.dao.UserFromEmailIdDao;
import org.mq.captiway.scheduler.dao.UserFromEmailIdDaoForDML;
import org.mq.captiway.scheduler.services.PurgeList;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.EncryptDecryptUrlParameters;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PlaceHolders;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.Utility;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;


public class ConfirmationController extends AbstractController {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private PrintWriter responseWriter;
	
	public ConfirmationController() {
	}
	
	 private UserFromEmailIdDao userFromEmailIdDao;
	 private UserFromEmailIdDaoForDML userFromEmailIdDaoForDML;
	 public UserFromEmailIdDaoForDML getUserFromEmailIdDaoForDML() {
		return userFromEmailIdDaoForDML;
	}

	public void setUserFromEmailIdDaoForDML(
			UserFromEmailIdDaoForDML userFromEmailIdDaoForDML) {
		this.userFromEmailIdDaoForDML = userFromEmailIdDaoForDML;
	}

	private ContactsDao contactsDao;
	 private ContactsDaoForDML contactsDaoForDML;
	 private ContactParentalConsentDaoForDML contactParentalConsentDaoForDML;
	 
	 public ContactParentalConsentDaoForDML getContactParentalConsentDaoForDML() {
		return contactParentalConsentDaoForDML;
	}

	public void setContactParentalConsentDaoForDML(
			ContactParentalConsentDaoForDML contactParentalConsentDaoForDML) {
		this.contactParentalConsentDaoForDML = contactParentalConsentDaoForDML;
	}

	public ContactsDaoForDML getContactsDaoForDML() {
		return contactsDaoForDML;
	}

	public void setContactsDaoForDML(ContactsDaoForDML contactsDaoForDML) {
		this.contactsDaoForDML = contactsDaoForDML;
	}

	public void setUserFromEmailIdDao(UserFromEmailIdDao userFromEmailIdDao) {
	        this.userFromEmailIdDao = userFromEmailIdDao;
	 } 
	 
	 public UserFromEmailIdDao getUserFromEmailIdDao() {
	        return this.userFromEmailIdDao;
	 }
	 
	 public void setContactsDao(ContactsDao contactsDao) {
	        this.contactsDao = contactsDao;
	 } 
	 
	 public ContactsDao getContactsDao() {
	        return this.contactsDao;
	 }
	
	 
	 private EmailQueueDao emailQueueDao;
	 private EmailQueueDaoForDML emailQueueDaoForDML;
	 
	public EmailQueueDaoForDML getEmailQueueDaoForDML() {
		return emailQueueDaoForDML;
	}

	public void setEmailQueueDaoForDML(EmailQueueDaoForDML emailQueueDaoForDML) {
		this.emailQueueDaoForDML = emailQueueDaoForDML;
	}

	public EmailQueueDao getEmailQueueDao() {
		return emailQueueDao;
	}

	public void setEmailQueueDao(EmailQueueDao emailQueueDao) {
		this.emailQueueDao = emailQueueDao;
	}
	private OptInReportDao optInReportDao;

	public OptInReportDao getOptInReportDao() {
		return optInReportDao;
	}

	public void setOptInReportDao(OptInReportDao optInReportDao) {
		this.optInReportDao = optInReportDao;
	}
	
	private OptInReportDaoForDML optInReportDaoForDML;


	public OptInReportDaoForDML getOptInReportDaoForDML() {
		return optInReportDaoForDML;
	}

	public void setOptInReportDaoForDML(OptInReportDaoForDML optInReportDaoForDML) {
		this.optInReportDaoForDML = optInReportDaoForDML;
	}

	private ContactParentalConsentDao contactParentalConsentDao;
	
	
	
	public ContactParentalConsentDao getContactParentalConsentDao() {
		return contactParentalConsentDao;
	}

	public void setContactParentalConsentDao(
			ContactParentalConsentDao contactParentalConsentDao) {
		this.contactParentalConsentDao = contactParentalConsentDao;
	}

	
	private CustomTemplatesDao customTemplatesDao;
	
	
	public CustomTemplatesDao getCustomTemplatesDao() {
		return customTemplatesDao;
	}

	public void setCustomTemplatesDao(CustomTemplatesDao customTemplatesDao) {
		this.customTemplatesDao = customTemplatesDao;
	}
	
	private MailingListDao mailingListDao;
	
	public MailingListDao getMailingListDao() {
		return mailingListDao;
	}

	public void setMailingListDao(MailingListDao mailingListDao) {
		this.mailingListDao = mailingListDao;
	}

	private static final String ERROR_RESPONSE = 
		"<div style='font-size:15px;color:blue;font-family:verdena;" +
		"font-weight:bold;margin-top:50px'>The Web Page is expired</div>";
	
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, Exception {
		if(logger.isInfoEnabled()) logger.info("just entered ");
	    
	    String requestedAction = request.getParameter("requestedAction");
	    
	    if(requestedAction==null){
	    	if(logger.isDebugEnabled()) logger.debug("Action attribute is null . Returning ...");
        	return null;
        }
	    
	    try {
	    	
	    	 if (requestedAction.compareTo("fromEmail") == 0) {
	    		 
	         	 responseWriter = response.getWriter();
	             updateFromEmailStatus(request, response);
	             responseWriter.flush();
	             responseWriter.close();
	             
	         } else if (requestedAction.compareTo("confirmoptin") == 0) { 
	        	 
	        	 responseWriter = response.getWriter();
	        	 confirmContactOptIn(request, response);
	        	 responseWriter.flush();
	             responseWriter.close();
	             
	         }else if(requestedAction.compareTo("webpage") == 0) {
	        	 
	        	 responseWriter = response.getWriter();
	        	 processWeblink(request, response);
	        	 responseWriter.flush();
	             responseWriter.close();
	        	 
	        	 
	         }
	         else if(requestedAction.compareTo("parentalWebpage") == 0) {
	        	 
	        	 responseWriter = response.getWriter();
	        	 processParentalWeblink(request, response);
	        	 responseWriter.flush();
	             responseWriter.close();
	        	 
	        	 
	         }
	         else if(requestedAction.compareTo("userStoreFromEmailId") == 0 
	        		 || requestedAction.compareTo("userStoreReplyToEmailId") == 0) {
	        	 
	        	 responseWriter = response.getWriter();
	        	 updateToOrFromStoreEmailStatus(request,response);
	        	 responseWriter.flush();
	             responseWriter.close();
	        	 
	        	 
	         }
	    	
		} catch (Exception e) {
			logger.error("** Exception : "+e);
		}
	    
	    return null;
	}
	
	  public void processWeblink(HttpServletRequest request, HttpServletResponse response)  throws Exception {
			
		   try {
			//if(logger.isDebugEnabled()) logger.debug("---------------just entered in processWebLink--------"+contactId);
				response.setContentType("text/html");
				
				if(logger.isInfoEnabled()) logger.info("Request Data : "+request.getParameter("cId")+"  "+request.getParameter("optRepId"));
				
				Long contactId = Long.parseLong(request.getParameter("cId"));
				Long optInRepId = Long.parseLong(request.getParameter("optRepId"));
				
				if(contactId == null || optInRepId == null) {
					
					if(logger.isDebugEnabled()) logger.debug("cant process the web link...REASON :: contactId = "+contactId+" OR optInRepId = "+optInRepId);
					return;
					
				}
				
				Contacts contact = contactsDao.findById(contactId);
				
				OptInReport optReport = optInReportDao.findById(optInRepId);
				
				
				//if(logger.isDebugEnabled()) logger.debug("---------------just entered in processWebLink--------report"+campaignReport);
				if(optReport == null) {
					if(logger.isWarnEnabled()) logger.warn(" opt in Report object not found for Id :"+optInRepId);
					responseWriter.write(ERROR_RESPONSE);
					return;
				}
				
				
				String htmlContent = optReport.getHtmlContent();
				
				if(htmlContent == null) {
					responseWriter.write(ERROR_RESPONSE);
					return ;
				}
				
				htmlContent = htmlContent.replace("[url]", PropertyUtil.getPropertyValue("confirmOptinUrl"));
				htmlContent = htmlContent.replace("[cId]", contactId.longValue()+"");
				htmlContent = htmlContent.replace("[optRepId]", optReport.getOptRepId().longValue()+"");
				htmlContent = htmlContent.replace("[email]", contact == null ? " your email Id " : contact.getEmailId());
				htmlContent = htmlContent.replace("[greetings]", contact.getFirstName()==null?"Dear sir/madam, ":"Hi "+contact.getFirstName()+",");
				
				
				responseWriter.write(htmlContent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
		}
		}
	
	
	  public void processParentalWeblink(HttpServletRequest request, HttpServletResponse response)  throws Exception {
			
		   try {
			//if(logger.isDebugEnabled()) logger.debug("---------------just entered in processWebLink--------"+contactId);
				response.setContentType("text/html");
				
				if(logger.isInfoEnabled()) logger.info("Request Data : "+request.getParameter("eqId")+"  ");
				
				Long emailQId = Long.parseLong(request.getParameter("eqId"));
				
				if(emailQId == null ) {
					
					if(logger.isDebugEnabled()) logger.debug("cant process the web link...REASON :: emailQId = "+emailQId);
					return;
					
				}
				
				EmailQueue emailQueue = emailQueueDao.findById(emailQId);
				
				
				
				//if(logger.isDebugEnabled()) logger.debug("---------------just entered in processWebLink--------report"+campaignReport);
				if(emailQueue == null) {
					if(logger.isWarnEnabled()) logger.warn(" emailQueue object not found for Id :"+emailQueue.getId());
					responseWriter.write(ERROR_RESPONSE);
					return;
				}
				
				
				String htmlContent = emailQueue.getMessage();
				Campaigns campaign = emailQueue.getCampaign();
				if(campaign != null) {
					
					String webLinkText = campaign.getWebLinkText();
					String webLinkUrlText = campaign.getWebLinkUrlText();
					
					String weblinkUrl =  PropertyUtil.getPropertyValue("parentalWeblinkUrl");
					weblinkUrl =weblinkUrl.replace("|^", "[").replace("^|", "]");
					String DIV_TEMPLATE = PropertyUtil.getPropertyValue("divTemplate");
					String webUrl =  DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT, webLinkText + 
							" <a href='" + weblinkUrl + "'>"+ webLinkUrlText + "</a>").
							replace("[eqId]", emailQId.longValue()+Constants.STRING_NILL);
					htmlContent = htmlContent.replace(webUrl, "");
					
				}
				
				if(emailQueue.getStatus() != null)
				htmlContent = htmlContent.replace("[sentId]", EncryptDecryptUrlParameters.encrypt(emailQueue.getId()+""));

				
				
				if(htmlContent == null) {
					responseWriter.write(ERROR_RESPONSE);
					return ;
				}
				
				
				
				responseWriter.write(htmlContent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
		}
		}
	
	  
	  
	public void updateFromEmailStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String userIdStr = request.getParameter("userId");
		String email = request.getParameter("email");
		
		if(userIdStr==null || email ==null) return;
		
		long userId = Long.parseLong(userIdStr);
		
		StringBuffer confirmPage = new StringBuffer(PropertyUtil.getPropertyValue("fromEmailConfirmHtml")); 
		String responseTxt = PropertyUtil.getPropertyValue("confirmTxt");
		String searchText = PropertyUtil.getPropertyValue("PHschedulerUrl");
		String schedulerUrl = PropertyUtil.getPropertyValue("schedulerUrl");
		
		if(logger.isDebugEnabled()) logger.debug("User Id : " + userId + " Email Name : " + email);
		List<UserFromEmailId> list = userFromEmailIdDao.getEmailIdByUserId(userId,email);
		for(Object obj : list) {
			UserFromEmailId userFromEmailId = (UserFromEmailId)obj;
			userFromEmailId.setStatus(1);
			//userFromEmailIdDao.saveOrUpdate(userFromEmailId);
			userFromEmailIdDaoForDML.saveOrUpdate(userFromEmailId);
			if(logger.isDebugEnabled()) logger.debug("Exiting ...");
	    }
		
		int index = confirmPage.indexOf(searchText);
		while(index>0){
			confirmPage.replace(index, index+searchText.length(), schedulerUrl);
			index = confirmPage.indexOf(searchText);
		}
		searchText = PropertyUtil.getPropertyValue("PHresponse");
		index = confirmPage.indexOf(searchText);
		while(index>0){
			confirmPage.replace(index, index+searchText.length(), responseTxt);
			index = confirmPage.indexOf(searchText);
		}
		
		response.setContentType("text/html");
		responseWriter.print(confirmPage);
	}
	
	
	private void updateToOrFromStoreEmailStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String requestedAction = request.getParameter("requestedAction"); //userStoreFromEmailId
		String homeStoreId = request.getParameter("homeStoreId");
		String email = request.getParameter("email");
		String userIdStr = request.getParameter("userId");
		
		//if(userIdStr== null || homeStoreId==null || email ==null || requestedAction==null) return;
		if(userIdStr== null || email ==null || requestedAction==null) return;
		
		
		StringBuffer confirmPage = new StringBuffer(PropertyUtil.getPropertyValue("fromEmailConfirmHtml")); 
		String responseTxt = PropertyUtil.getPropertyValue("confirmTxt");
		String searchText = PropertyUtil.getPropertyValue("PHschedulerUrl");
		String schedulerUrl = PropertyUtil.getPropertyValue("schedulerUrl");
		
		if(logger.isDebugEnabled()) logger.debug("userId : "+userIdStr+" homeStoreId : " + homeStoreId + " Email Name : " + email);
		
		if(requestedAction.equals("userStoreFromEmailId") || requestedAction.equals("userStoreReplyToEmailId")){
			
			List<UserFromEmailId> userFromEmailIdList = userFromEmailIdDao.getEmailIdByUserId(Long.parseLong(userIdStr),email);
			for(UserFromEmailId userFromEmailId : userFromEmailIdList){
				userFromEmailId.setStatus(1);
				//userFromEmailIdDao.saveOrUpdate(userFromEmailId);
				userFromEmailIdDaoForDML.saveOrUpdate(userFromEmailId);
			}
		}
		
		
		int index = confirmPage.indexOf(searchText);
		while(index>0){
			confirmPage.replace(index, index+searchText.length(), schedulerUrl);
			index = confirmPage.indexOf(searchText);
		}
		searchText = PropertyUtil.getPropertyValue("PHresponse");
		index = confirmPage.indexOf(searchText);
		while(index>0){
			confirmPage.replace(index, index+searchText.length(), responseTxt);
			index = confirmPage.indexOf(searchText);
		}
		
		response.setContentType("text/html");
		responseWriter.print(confirmPage);
	}
	
	public void confirmContactOptIn(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		try {
			if(logger.isInfoEnabled()) logger.info("Request Data : "+request.getParameter("cId"));
			Long confirmContactId = Long.parseLong(request.getParameter("cId"));
			
			if(logger.isInfoEnabled()) logger.info("Contact Id :" + confirmContactId);
			boolean status = false;
			
			//check for the parental consent
			try {
				Contacts  contact = contactsDao.findById(confirmContactId);
				//MailingList mailingList = contact.getMailingList();
				//update contact's email status w.r.t parental consent
				contact.setEmailStatus(Constants.CONT_STATUS_ACTIVE);
				contactsDaoForDML.saveOrUpdate(contact);
				//contactsDao.merge(contact);
				
				/*String emailStatus = Utility.getContactEmailStatus(contact, mailingList, contactParentalConsentDao);
				boolean success = false;
				if(emailStatus != null) {
					status = true;
					contact.setEmailStatus(emailStatus);
					contactsDao.saveOrUpdate(contact);
					success = true;
					
				}*/
				
				//if(success ) {
					
				status = true;
				
				//Set<MailingList> mlset = contact.getMlSet();
				List<MailingList> mlList = mailingListDao.findByContactBit(contact.getUsers().getUserId(), contact.getMlBits());
				Set<MailingList> mlset = new HashSet<MailingList>(mlList);
				Iterator<MailingList> mlItr = mlset.iterator();
				MailingList mailingList = null;
				while (mlItr.hasNext()) {
					mailingList = mlItr.next();
					if(logger.isDebugEnabled()) logger.debug("mailinglist is :"+mailingList.getListName());
					if (mailingList.isCheckWelcomeMsg()) {
						if(logger.isDebugEnabled()) logger.debug("ml template is :"+mailingList.isCheckWelcomeMsg());
						sendWelcomeEmail(contact,mailingList.getWelcomeCustTempId(),contact.getUsers());

					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::::" , e);
				status = false;
			}
			
				
			//}
			
			
			if(status) {
				if(logger.isDebugEnabled()) logger.debug("*** Contact email Status updated successfully. ***");
			} else {
				if(logger.isDebugEnabled()) logger.debug("<** Error occured while updating email Status. **>");
			}
			
			StringBuffer confirmOptInHtml = new StringBuffer(PropertyUtil.getPropertyValue("confirmOptInHtml"));
			String searchText = PropertyUtil.getPropertyValue("PHresponse");
			String phAppUrl = PropertyUtil.getPropertyValue("PHschedulerUrl");
			int index = confirmOptInHtml.indexOf(searchText); 
			while(index>0){
				if(status) { 
					confirmOptInHtml.replace(index, index+searchText.length(),PropertyUtil.getPropertyValue("confirmOptInTxt"));
				} else {
					confirmOptInHtml.replace(index, index+searchText.length(),PropertyUtil.getPropertyValue("confirmOptInFailTxt"));
				}	
				index = confirmOptInHtml.indexOf(searchText);
			}

			index = confirmOptInHtml.indexOf(phAppUrl);
			
			while(index>0){
				confirmOptInHtml.replace(index, index+phAppUrl.length(), PropertyUtil.getPropertyValue("schedulerUrl"));
				index = confirmOptInHtml.indexOf(phAppUrl);
			}

			if(!status) {
				index = confirmOptInHtml.indexOf("green-checkmark.gif");
				confirmOptInHtml.replace(index,index+"green-checkmark.gif".length(),"red-checkmark.gif");
				String thankyouMsg = "Thank you, your email Id is verified";
				index = confirmOptInHtml.indexOf(thankyouMsg);
				confirmOptInHtml.replace(index,index + thankyouMsg.length(),"Error!");
			}	
			
			response.setContentType("text/html");
			responseWriter.print(confirmOptInHtml);
			
			
			
			
		} catch (Exception e) {
			logger.error("Exception:",(Throwable)e);
		}
	}
	
	/*public static String DecryptString(String obsPassword, String strWord) {
		BasicTextEncryptor bte = new BasicTextEncryptor();
		bte.setPassword(obsPassword);
		String decWord = bte.decrypt(strWord);
		return decWord;
	}*/
	
	
public void sendWelcomeEmail(Contacts contact,  Long templateId, org.mq.captiway.scheduler.beans.Users user) {
		
		//to send the loyalty related email
		 CustomTemplates custTemplate = null;
		  String message = PropertyUtil.getPropertyValueFromDB("welcomeMsgTemplate");
		  
		  if(templateId != null) {
			  
			  custTemplate = customTemplatesDao.findCustTemplateById(templateId);
			  if(custTemplate != null) {
			  
				  message = custTemplate.getHtmlText();
			  }
		  }
		  //if(logger.isDebugEnabled()) logger.debug("-----------email----------"+tempContact.getEmailId());
		  
		  message = message.replace("[OrganisationName]", user.getUserOrganization().getOrganizationName())
				  .replace("[senderReplyToEmailID]", user.getEmailId());
		  
		  EmailQueue testEmailQueue = new EmailQueue(custTemplate,Constants.EQ_TYPE_WELCOME_MAIL, message, "Active",
				  				contact.getEmailId(), user, new Date(), " Welcome Mail",
				  				null, contact.getFirstName(), null, contact.getContactId());
				
			//testEmailQueue.setChildEmail(childEmail);
			if(logger.isDebugEnabled()) logger.debug("testEmailQueue"+testEmailQueue.getChildEmail());
			//emailQueueDao.saveOrUpdate(testEmailQueue);
			emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
		
		
	}//sendWelcomeEmail
	
		
} //class	
		
		

