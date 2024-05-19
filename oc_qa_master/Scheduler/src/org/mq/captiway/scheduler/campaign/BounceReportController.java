package org.mq.captiway.scheduler.campaign;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.Bounces;
import org.mq.captiway.scheduler.beans.CampaignSent;
import org.mq.captiway.scheduler.beans.SuppressedContacts;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.BouncesDao;
import org.mq.captiway.scheduler.dao.BouncesDaoForDML;
import org.mq.captiway.scheduler.dao.CampaignReportDao;
import org.mq.captiway.scheduler.dao.CampaignReportDaoForDML;
import org.mq.captiway.scheduler.dao.CampaignSentDao;
import org.mq.captiway.scheduler.dao.CampaignSentDaoForDML;
import org.mq.captiway.scheduler.dao.CampaignsDao;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.SuppressedContactsDao;
import org.mq.captiway.scheduler.dao.SuppressedContactsDaoForDML;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.utility.BounceCategories;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;


/**
 * 
 * @author proumya
 *added to update the bounce report(alternate to 'mount point').
 * As and When  BounceTracker founds the reports in the Pmta/bounceInbox directory it will process those files and
 * sends the request to this Servelt along with all the require parameters.</BR>
 * 
 * This controller process the request and update the status of all respective records in DB.  
 */
public class BounceReportController extends AbstractController{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER); 
	
private BouncesDao bouncesDao;
	
	public BouncesDao getBouncesDao() {
		return bouncesDao;
	}

	public void setBouncesDao(BouncesDao bouncesDao) {
		this.bouncesDao = bouncesDao;
	}

	private BouncesDaoForDML bouncesDaoForDML;

	
	public BouncesDaoForDML getBouncesDaoForDML() {
		return bouncesDaoForDML;
	}

	public void setBouncesDaoForDML(BouncesDaoForDML bouncesDaoForDML) {
		this.bouncesDaoForDML = bouncesDaoForDML;
	}

	private CampaignsDao campaignsDao;

	public void setCampaignsDao(CampaignsDao campaignsDao) {
		this.campaignsDao = campaignsDao;
	}

	public CampaignsDao getCampaignsDao() {
		return this.campaignsDao;
	}

	private SessionFactory sessionFactory;
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

	private CampaignSentDao campaignSentDao;
	private CampaignSentDaoForDML campaignSentDaoForDML;
	public CampaignSentDaoForDML getCampaignSentDaoForDML() {
		return campaignSentDaoForDML;
	}

	public void setCampaignSentDaoForDML(CampaignSentDaoForDML campaignSentDaoForDML) {
		this.campaignSentDaoForDML = campaignSentDaoForDML;
	}

	public void setCampaignSentDao(CampaignSentDao campaignSentDao) {
		this.campaignSentDao = campaignSentDao;
	}
	public CampaignSentDao getCampaignSentDao() {
		return this.campaignSentDao;
	}


	private ContactsDao contactsDao;
	public void setContactsDao(ContactsDao contactsDao) {
		this.contactsDao = contactsDao;
	}
	public ContactsDao getContactsDao() {
		return this.contactsDao;
	}
	private ContactsDaoForDML contactsDaoForDML;
	public ContactsDaoForDML getContactsDaoForDML() {
		return contactsDaoForDML;
	}

	public void setContactsDaoForDML(ContactsDaoForDML contactsDaoForDML) {
		this.contactsDaoForDML = contactsDaoForDML;
	}
	private SuppressedContactsDao suppressedContactsDao;
	public SuppressedContactsDao getSuppressedContactsDao() {
		return suppressedContactsDao;
	}
	public void setSuppressedContactsDao(SuppressedContactsDao suppressedContactsDao) {
		this.suppressedContactsDao = suppressedContactsDao;
	}
	
	private SuppressedContactsDaoForDML suppressedContactsDaoForDML;
	
	public SuppressedContactsDaoForDML getSuppressedContactsDaoForDML() {
		return suppressedContactsDaoForDML;
	}

	public void setSuppressedContactsDaoForDML(
			SuppressedContactsDaoForDML suppressedContactsDaoForDML) {
		this.suppressedContactsDaoForDML = suppressedContactsDaoForDML;
	}
	
	private UsersDao usersDao;
	public UsersDao getUsersDao() {
		return usersDao;
	}
	public void setUsersDao(UsersDao usersDao) {
		this.usersDao = usersDao;
	}
	
	private CampaignReportDao campaignReportDao;
	public CampaignReportDao getCampaignReportDao() {
		return campaignReportDao;
	}
	public CampaignReportDaoForDML getCampaignReportDaoForDML() {
		return campaignReportDaoForDML;
	}

	public void setCampaignReportDaoForDML(
			CampaignReportDaoForDML campaignReportDaoForDML) {
		this.campaignReportDaoForDML = campaignReportDaoForDML;
	}

	public void setCampaignReportDao(CampaignReportDao campaignReportDao) {
		this.campaignReportDao = campaignReportDao;
	}
	
	private CampaignReportDaoForDML campaignReportDaoForDML;
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		if(logger.isDebugEnabled()) logger.debug("FROM :: "+request.getRequestURL());
		
		String reqParamSentId = request.getParameter("sentId");
		String reqParamCrId = request.getParameter("crId");
		String reqParamUserId = request.getParameter("userId");
		String reqParamCategory = request.getParameter("category");
		String message = request.getParameter("message");
		
		if ( (reqParamSentId == null) || (reqParamCrId == null) || (reqParamUserId == null) || (reqParamCategory == null) ) {
			
			if(logger.isErrorEnabled()) logger.error("Error occured :: one of the required parameter got as 'null'....returning");
			return null;
			
		}//if
		
		Long sentId = Long.parseLong(reqParamSentId);
		Long crId = Long.parseLong(reqParamCrId);
		Long userId = Long.parseLong(reqParamUserId);
		
		
		
		//CampaignSent campaignSent = null;
		CampaignSent campaignSent =  campaignSentDao.findById(sentId);
		int rowsAffected = 0;
		
		if(logger.isDebugEnabled())	
			logger.debug("User Id : " + userId+ " sentId : " + sentId);
		
		//rowsAffected = campaignSentDao.setStatusBySentId(sentId, Constants.CS_STATUS_BOUNCED, crId);
		rowsAffected = campaignSentDaoForDML.setStatusBySentId(sentId, Constants.CS_STATUS_BOUNCED, crId);
		
		if(logger.isDebugEnabled())
			logger.debug(" No of rows affected while updating the campaign sent : "+rowsAffected);
		if(rowsAffected <= 0) {
			if(logger.isWarnEnabled()) logger.warn(" No rows updated in the database when trying for sentId to update as Bounce :"+sentId);
			return null;
			//TODO need to Verify the sent_id in the Bounce table.
		}
		if(logger.isDebugEnabled()) 
			logger.debug("Bounce status is updated for the Sent Id : "+sentId);
		
		//rowsAffected = campaignReportDao.updateCampaignReport(crId, Constants.CR_TYPE_BOUNCES);
		rowsAffected = campaignReportDaoForDML.updateCampaignReport(crId, Constants.CR_TYPE_BOUNCES);
		
		if(!(rowsAffected > 0)){
			if(logger.isWarnEnabled()) logger.warn(" CampaignReport with Id - "+crId+" bounce updation failed for sent Id : "+
					sentId);
		}
		
		
		/*if(bounceList.size() >= 30) {
			saveByCollection(bounceList);
		}*/
		
		String tempCat = BounceCategories.categories.get(reqParamCategory.trim().toLowerCase());
		if(tempCat==null) {
			tempCat = BounceCategories.categories.get("others");
		}
		
		String catStr = tempCat + "/" + reqParamCategory;
		Bounces bounce = new Bounces(campaignSent, catStr, message, new Date(), crId);
		
		//bouncesDao.saveOrUpdate(bounce);
		bouncesDaoForDML.saveOrUpdate(bounce);
		inactiveContact(userId, crId, campaignSent.getEmailId(), reqParamCategory);//to add into suppressed contacts
		
		
		return null;
	}
	
	/**
	 * This method analyses various  bounce categories to make a particular contact as 'inactive'. 
	 * @param userId
	 * @param crId
	 * @param emailId
	 * @param category
	 */
	private void inactiveContact(Long userId, Long crId, String emailId, String category) {
		
		List<String> statusList = null;
		boolean isSuccess = false;
		int size = 0;
		int minConsideredCount = 0;
		try {
			category = BounceCategories.categories.get(category);
	
			if(logger.isDebugEnabled()) {
				logger.debug(">>>>>>>Category  "+ category);
			}
			
			if(category.equals(BounceCategories.NON_EXISTENT_ADDRESS)) {
				
				contactsDaoForDML.updateContactStatus(emailId, userId, Constants.CS_STATUS_BOUNCED);
				addToSuppressedContacts(userId, emailId);
			}
			else if(category.equals(BounceCategories.UNDELIVERABLE)) {
				
				statusList = campaignSentDao.getRecentStatusForEmailId(crId, emailId); //3
				size = statusList.size() < 3 ? statusList.size() : 3;
				minConsideredCount = 2;
			} 
			else if(category.equals(BounceCategories.MAILBOX_FULL)) {
				
				statusList = campaignSentDao.getRecentStatusForEmailId(crId, emailId); // 4		
				size = statusList.size() < 4 ? statusList.size() : 4;
				minConsideredCount = 3;
			}
			if(statusList != null ) {
				
				for (int i = 0; i < size; i++) {
					
					if(statusList.get(i).equalsIgnoreCase(Constants.CS_STATUS_SUCCESS)) {
						isSuccess = true;
						break;
					}
				}
				if(!isSuccess && statusList.size() > minConsideredCount) {
					
					contactsDaoForDML.updateContactStatus(emailId, userId, Constants.CS_STATUS_BOUNCED);
					addToSuppressedContacts(userId, emailId);
				}
			}
			
		}catch(Exception e) {
			if(logger.isErrorEnabled()) logger.error("**Exception : while inactivating the contact ", e);
		}
	}
	
	public void addToSuppressedContacts(Long userId, String emailId) {
		try {
			SuppressedContacts suppressedContact = new SuppressedContacts();
			Users users = usersDao.find(userId); 
			suppressedContact.setUser(users);
			suppressedContact.setEmail(emailId);
			suppressedContact.setType(Constants.SUPP_TYPE_BOUNCED);
			suppressedContactsDaoForDML.saveOrUpdate(suppressedContact);
			if(logger.isDebugEnabled()) logger.debug("Added successfully to suppress contacts .");
		} catch (Exception e) {
			if(logger.isErrorEnabled()) logger.error("**Exception : while adding contact to suppress Contacts list :", e);
		}
	}
	
	
}
