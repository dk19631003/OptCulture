package org.mq.captiway.scheduler.campaign;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.SMSCampaignSent;
import org.mq.captiway.scheduler.beans.SMSSuppressedContacts;
import org.mq.captiway.scheduler.beans.SuppressedContacts;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDao;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDaoForDML;
import org.mq.captiway.scheduler.dao.SMSSuppressedContactsDao;
import org.mq.captiway.scheduler.dao.SMSSuppressedContactsDaoForDML;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;


/**
 * these are some valuable params along with their types and sample data and the description
 * 
 * Parameters   Field type    Sample Data     Description  reqid
 *********************************************************************************
				reqid        character     3453456723      the id that is originally returned to the client per request.
				
				mobile	     integer       919000000000     the mobile number tat message was sent to
					
	     					 (upto 64 bytes)	

				delv_date    datetime      2009-02-20 00:00:00  the date and time when message was delivered.
					
				status       char          S                    Status will be S for submit / N for Not-Delivered / D for delivered

				feedid       integer       534867             The feedid from which client sent the message
							(upto 64 bytes)

 * ****************************************************************************************
 * the sample requested url is..... 
 * 
 * //https://www.smsapi.com/dlrnotification.jsp?reqid=3453456723&mobile=919000000000&delv_date=2010-02-20%2004:34:43&status=D
 * 
 */

public class UpdateSMSDeliveryReportsController extends AbstractController{
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private SMSCampaignSentDao smsCampaignSentDao;
	private SMSCampaignSentDaoForDML smsCampaignSentDaoForDML;

	public SMSCampaignSentDaoForDML getSmsCampaignSentDaoForDML() {
		return smsCampaignSentDaoForDML;
	}

	public void setSmsCampaignSentDaoForDML(
			SMSCampaignSentDaoForDML smsCampaignSentDaoForDML) {
		this.smsCampaignSentDaoForDML = smsCampaignSentDaoForDML;
	}

	public SMSCampaignSentDao getSmsCampaignSentDao() {
		return smsCampaignSentDao;
	}
	
	public void setSmsCampaignSentDao(SMSCampaignSentDao smsCampaignSentDao) {
		this.smsCampaignSentDao = smsCampaignSentDao;
	}
	
	private UsersDao usersDao;
	//String sesid ="";
	
	public UsersDao getUsersDao() {
		return usersDao;
	}

	public void setUsersDao(UsersDao usersDao) {
		this.usersDao = usersDao;
	}
	
	
	private SMSSuppressedContactsDao smsSuppressedContactsDao;
	private SMSSuppressedContactsDaoForDML smsSuppressedContactsDaoForDML;
	
	public SMSSuppressedContactsDaoForDML getSmsSuppressedContactsDaoForDML() {
		return smsSuppressedContactsDaoForDML;
	}

	public void setSmsSuppressedContactsDaoForDML(
			SMSSuppressedContactsDaoForDML smsSuppressedContactsDaoForDML) {
		this.smsSuppressedContactsDaoForDML = smsSuppressedContactsDaoForDML;
	}

	public SMSSuppressedContactsDao getSmsSuppressedContactsDao() {
		return smsSuppressedContactsDao;
	}

	public void setSmsSuppressedContactsDao(
			SMSSuppressedContactsDao smsSuppressedContactsDao) {
		this.smsSuppressedContactsDao = smsSuppressedContactsDao;
	}

	/**
	 * this overridden method allows to update the smsCampaignSent delivered status given by the 
	 * Push back URL process from NetCore and following tasks.
	 * 1.takes the data from the requested parameters and 
	 * 2.simply update the status for a specified contact in the sms_campaign_sent table
	 * 
	 * 
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
			
		PrintWriter responseWriter = null;
		responseWriter = response.getWriter();
		
		try {
			if(logger.isInfoEnabled()) logger.info("Received URL Path  :"+request.getRequestURL()+request.getQueryString());
			
			String reqId = request.getParameter("reqid");
			String mobile = request.getParameter("mobile");
			String status = request.getParameter("status");		//need to know the actual status msg if incase 
																//of any error we need the description specifying the error reason.
			
			if( (reqId == null || reqId.equals("")) || (mobile == null || mobile.equals("")) || (status == null || status.equals("")) ) {
				if(logger.isErrorEnabled()) logger.error("Exception : *** One of the required fields is empty or null");
				return null;
			}
			//Long mobileNum = Long.parseLong(mobile);
			String delDate = request.getParameter("delv_date");		//as of now this is not going to be utilized
			
			if(logger.isInfoEnabled()) logger.info("the received request parameteres are====>"+"status="+status+", mobileNumber="+mobile+", reqId="+reqId);
			
			//int updateCount = smsCampaignSentDao.updateStatus(status, mobile, reqId);
			int updateCount = smsCampaignSentDaoForDML.updateStatus(status, mobile, reqId);

			if(updateCount == 0  &&
					PropertyUtil.getPropertyValue("ApplicationUrl").contains("mailhandler01.info"))  {//modified to get the reports for even test
				
				if(logger.isErrorEnabled()) logger.error("** Exception : no record found with the specified crieteria so connecting to test");
				
				String postData = "status="+status+"&mobile="+mobile+"&reqid="+reqId;
				String encode = response.encodeRedirectUrl("http://test.captiway.com/Scheduler/smsDeliveryReport.mqrm?"+postData);
				response.sendRedirect(encode);
				
				return null;
				
				
			}//if
			
			if(status.equalsIgnoreCase("NDNCRejected")) {
				//TODO need to get the user Id as i dont have any user info here
				
				SMSCampaignSent smsCampaignSent = smsCampaignSentDao.findByReqIdAndMobile(reqId,mobile);
				if(smsCampaignSent != null) {
				
					Users user = smsCampaignSent.getSmsCampaignReport().getUser();
					addToSuppressedContacts(user, mobile);
				}//if
				
			}//if
			
			/*if(updateCount == 0) {
				
				if(logger.isErrorEnabled()) logger.error("** Exception : no record found with the specified crieteria");
			}
			else {
				if(logger.isInfoEnabled()) logger.info("the number of records updated are====>"+updateCount);
			}*/
			
			response.setContentType("text/html");
			
			
			responseWriter.print("success");
			
			return null;
			
		} catch (Exception e) { 
			
			if(logger.isErrorEnabled()) logger.error("** Exception while processing the request parameteres data to update the delivered status",e);
			responseWriter.print("success");
			return null;
			
		} finally {
			
			responseWriter.flush();
			responseWriter.close();
			
			
		}
		
	}// handleRequestInternal

	public void addToSuppressedContacts(Users user, String mobile) {
		try {
			SMSSuppressedContacts suppressedContact = null;
			List<SMSSuppressedContacts> retList =  smsSuppressedContactsDao.searchContactsById(user.getUserId(), mobile);
			if(retList != null && retList.size() == 1) {
				if(logger.isDebugEnabled()) logger.debug("This contact is already exist in suppressed contacts list");
				suppressedContact = retList.get(0);
				
				
			}
			else {
				if(logger.isDebugEnabled()) logger.debug("This contact is new for this user in suppressed contacts list");
				suppressedContact = new SMSSuppressedContacts();
				suppressedContact.setUser(user);
				suppressedContact.setMobile(mobile);
				
			}
			//Users users = usersDao.find(userId); 
			suppressedContact.setType(Constants.SMS_SUPP_TYPE_DND);
			//smsSuppressedContactsDao.saveOrUpdate(suppressedContact);
			smsSuppressedContactsDaoForDML.saveOrUpdate(suppressedContact);
			if(logger.isDebugEnabled()) logger.debug("Added successfully to sms suppress contacts .");
		} catch (Exception e) {
			if(logger.isErrorEnabled()) logger.error("**Exception : while adding contact to suppress Contacts list :", e);
		}
	}
	
	
	
}
