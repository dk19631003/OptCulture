package org.mq.optculture.business.gateway;

import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SMSBounces;
import org.mq.marketer.campaign.beans.SMSCampaignReport;
import org.mq.marketer.campaign.beans.SMSCampaignSent;
import org.mq.marketer.campaign.beans.SMSSuppressedContacts;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.AutoSmsQueueDao;
import org.mq.marketer.campaign.dao.AutoSmsQueueDaoForDML;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.SMSBouncesDao;
import org.mq.marketer.campaign.dao.SMSBouncesDaoForDML;
import org.mq.marketer.campaign.dao.SMSCampaignReportDaoForDML;
import org.mq.marketer.campaign.dao.SMSCampaignSentDao;
import org.mq.marketer.campaign.dao.SMSCampaignSentDaoForDML;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDao;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.SynapseDLRRequestObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class SynapseDLRProcessThread extends Thread{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private List<SynapseDLRRequestObject> requestList;
	public SynapseDLRProcessThread(List<SynapseDLRRequestObject> requestList) {
		this.requestList = requestList;
	}
	
	@Override
	public void run() {
		try {
			// TODO Auto-generated method stub
			processCampaignDLR(requestList);
		} catch (BaseServiceException e) {
			// TODO Auto-generated catch block
			
		}
	}
	public void processCampaignDLR(List<SynapseDLRRequestObject> requestList) throws  BaseServiceException {

		for (SynapseDLRRequestObject respLst : requestList ){
			try {
				SynapseDLRRequestObject requestObject = new SynapseDLRRequestObject();
				requestObject.setReferenceId(respLst.getReferenceId());
				requestObject.setMsgId(respLst.getMsgId());
				requestObject.setMobile(respLst.getMobile());
				logger.info("requestObject.getMobile() : "+requestObject.getMobile());
				requestObject.setStatus(respLst.getStatus());
				requestObject.setDeliveryTime(respLst.getDeliveryTime());
				requestObject.setAction(OCConstants.REQUEST_PARAM_TYPE_VALUE_DLR);
				requestObject.setUserSMSTool(Constants.USER_SMSTOOL_SYNAPSE);
				
		String referenceId=requestObject.getReferenceId();
		String mobile=requestObject.getMobile();
				/*try {
					smsCampaignSentDao = (SMSCampaignSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_CAMPAIGN_SENT_DAO);
					smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_CAMPAIGN_SENT_DAO_FOR_DML);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new BaseServiceException("Exception while fetching smsCampaignSentDao");
				}
					
				//smsCampaignSentDaoForDML.setUniqueMsgId(synDLRRequestObject.getMsgId(),referenceId,mobile);
						
				SMSCampaignSent smsCampaignSent = smsCampaignSentDao.findByRefIdAndMobile(synDLRRequestObject.getMsgId(), mobile);
				logger.info("SentId :"+smsCampaignSent.getSentId());
				if(smsCampaignSent == null ) throw new BaseServiceException("no smsCampaignSent "
						+ "found with mrid "+referenceId+", "+mobile);
				 */
				//setDLRBounceStatus(synDLRRequestObject,referenceId,mobile);
		//String mrId=equenceDLRRequestObject.getMrId();
		//String mobile=equenceDLRRequestObject.getMobileNumber();
		SMSCampaignSentDao smsCampaignSentDao = null;
		SMSCampaignSentDaoForDML smsCampaignSentDaoForDML  = null;
		AutoSmsQueueDao autoSmsQueueDao =null;
		AutoSmsQueueDaoForDML autoSmsQueueDaoForDML = null;

				try {
					smsCampaignSentDao = (SMSCampaignSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_CAMPAIGN_SENT_DAO);
					smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_CAMPAIGN_SENT_DAO_FOR_DML);
					
					autoSmsQueueDao = (AutoSmsQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_QUEUE_DAO);
					autoSmsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new BaseServiceException("Exception while fetching smsCampaignSentDao");
				}
				
				/*if(synDLRRequestObject.getMsgId()!=null && !synDLRRequestObject.getMsgId().equals("") &&
						synDLRRequestObject.getMsgId().startsWith(OCConstants.AutoSMSPrefix)){
					String sentId = (synDLRRequestObject.getMsgId()).split(OCConstants.AutoSMSPrefix)[1];
					AutoSmsQueue autoSmsQueue = autoSmsQueueDao.findByMrIdAndMobile(sentId, mobile);
					if(autoSmsQueue == null ) throw new BaseServiceException("no autoSmsQueue "
							+ "found with mrid , mobile"+referenceId+", "+mobile);*/
	
					setDLRStatusForAutoSMS(requestObject, referenceId, mobile);
	
					
				//}else{
					/*SMSCampaignSent smsCampaignSent = smsCampaignSentDao.findByMrIdAndMobile(mrId, mobile);
					if(smsCampaignSent == null ) throw new BaseServiceException("no smsCampaignSent "
							+ "found with mrid , mobile"+mrId+", "+mobile);*/
				setDLRBounceStatus(requestObject,referenceId,mobile);
				//}
			}catch (Exception e) {
				// TODO: handle exception
				continue;
			}
			}
		
	}
	public void setDLRStatusForAutoSMS(SynapseDLRRequestObject synDLRRequestObject,String referenceId,String mobile 
			) throws BaseServiceException {
		
	try{
		String status = synDLRRequestObject.getStatus();
		//String id=synDLRRequestObject.getReferenceId();
		//String mobile=synDLRRequestObject.getMobile();
		AutoSmsQueueDaoForDML autoSmsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
			
			String type = SMSStatusCodes.synapseStatusCodesMap.get(status);
			logger.info("type---"+type);
			if(SMSStatusCodes.DeliveredSet.contains(type)){
				logger.info("Delivered");
				status=SMSStatusCodes.SYNAPSE_STATUS_DELIVERED;
				autoSmsQueueDaoForDML.updateStatus(status,referenceId,mobile);
			}
			else if(SMSStatusCodes.BouncedSet.contains(type)) {
				
				logger.info("in bouncedset");
				status = SMSStatusCodes.SYNAPSE_STATUS_BOUNCED;
				
				autoSmsQueueDaoForDML.updateStatus(status,referenceId,mobile);
				
				/*logger.info(" "+smsCampaignSent.getSmsCampaignReport());
				addToBounces(smsCampaignSent.getSmsCampaignReport(), 
						smsCampaignSent.getMobileNumber(), smsCampaignSent, type,type,status);
				
				
				if(SMSStatusCodes.SuppressedSet.contains(SMSStatusCodes.equenceStatusCodesMap.get(status))){
					
					//Users user = smsCampaignReport.getUser();
					
					addToSuppressedContacts(smsCampaignSent.getSmsCampaignReport().getUser(), 
							smsCampaignSent.getMobileNumber(),type,type);
				}*/
					
					
			}
			/*else{	 
				SMSBouncesDao smsBouncesDao = null;
				SMSBouncesDaoForDML smsBouncesDaoForDML  = null;

				SMSCampaignReportDao smsCampaignReportDao = null;
				SMSCampaignReportDaoForDML smsCampaignReportDaoForDML = null;
				try {
					smsBouncesDao = (SMSBouncesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSBOUNCES_DAO);
					smsBouncesDaoForDML = (SMSBouncesDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMSBOUNCES_DAO_FOR_DML);

					smsCampaignReportDao = (SMSCampaignReportDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_CAMPAIGNREPORT_DAO);
					smsCampaignReportDaoForDML = (SMSCampaignReportDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_CAMPAIGNREPORT_DAO_FOR_DML);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new BaseServiceException("No dao found smsBouncesDao");
				}
						
				smsCampaignReportDaoForDML.updateBounceReport(smsCampaignSent.getSmsCampaignReport().getSmsCrId());
			}*/
	}catch(Exception e){
		logger.error("Exception ",e);
	}
				
		}
	
	public String getDLRStatus(SynapseDLRRequestObject synDLRRequestObject) {
		String status = synDLRRequestObject.getStatus();		
			logger.info("status "+status);
			logger.info("--"+SMSStatusCodes.synapseStatusCodesMap.get(status));
			return SMSStatusCodes.synapseStatusCodesMap.get(status);		
		
	}
	
		public void setDLRBounceStatus(SynapseDLRRequestObject synDLRRequestObject, 
					String referenceId,String mobile) throws BaseServiceException {
				
			try{
				String status = synDLRRequestObject.getStatus();
				SMSCampaignSentDao smsCampaignSentDao = (SMSCampaignSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_CAMPAIGN_SENT_DAO);
				SMSCampaignSentDaoForDML smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_CAMPAIGN_SENT_DAO_FOR_DML);
					
					String type = SMSStatusCodes.synapseStatusCodesMap.get(status);
					logger.info("type---"+type);
					if(SMSStatusCodes.DeliveredSet.contains(type)){
						logger.info("Delivered");
						status=SMSStatusCodes.SYNAPSE_STATUS_DELIVERED;
						smsCampaignSentDaoForDML.updateStatus(status,referenceId,mobile);
					}else if(SMSStatusCodes.BouncedSet.contains(type)) {
						 SMSCampaignSent smsCampaignSent = smsCampaignSentDao.findByRefIdAndMobile(referenceId, mobile);
						
						logger.info("in bouncedset");
						status = SMSStatusCodes.SYNAPSE_STATUS_BOUNCED;
						
						logger.info(" "+smsCampaignSent.getSmsCampaignReport());
						addToBounces(smsCampaignSent.getSmsCampaignReport(), 
								smsCampaignSent.getMobileNumber(), smsCampaignSent, type,type,status);
						
						
						if(SMSStatusCodes.SuppressedSet.contains(type)){
							
							//Users user = smsCampaignReport.getUser();
							
							addToSuppressedContacts(smsCampaignSent.getSmsCampaignReport().getUser(), 
									smsCampaignSent.getMobileNumber(),type,type);
							
							
						}
					}
			}catch(Exception e){
				logger.error("Exception ",e);
			}
					
			}
		public void addToBounces(SMSCampaignReport smsCampaignReport, String mobile,
					SMSCampaignSent smsCampaignSent, String status, String msg,String statusInDB ) throws BaseServiceException {
				
				if(smsCampaignSent == null || smsCampaignReport == null) {
					
					if(logger.isErrorEnabled()) logger.error("** got source objects as null");
					return;
					
				}
				
				//need to find the existing object with the same given mobile if any..
				Long crId = smsCampaignReport.getSmsCrId(); 

				try {
					SMSBouncesDao smsBouncesDao = (SMSBouncesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSBOUNCES_DAO);
					SMSBouncesDaoForDML smsBouncesDaoForDML = (SMSBouncesDaoForDML )ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMSBOUNCES_DAO_FOR_DML);
					SMSCampaignSentDaoForDML smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_CAMPAIGN_SENT_DAO_FOR_DML);
					SMSCampaignReportDaoForDML smsCampaignReportDaoForDML =  (SMSCampaignReportDaoForDML )ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_CAMPAIGNREPORT_DAO_FOR_DML);
				SMSBounces newBounce = smsBouncesDao.findBymobile(crId, mobile);
				
				if(newBounce == null) {
					
					newBounce = new SMSBounces();
					newBounce.setCrId(crId);
					newBounce.setSentId(smsCampaignSent);
					
					
				}
				
				newBounce.setMessage(msg);
				newBounce.setMobile(mobile);
				newBounce.setCategory(status);
				newBounce.setBouncedDate(Calendar.getInstance());
				//smsBouncesDao.saveOrUpdate(newBounce);
				smsBouncesDaoForDML.saveOrUpdate(newBounce);

				logger.info("added to bounces====");
				if(logger.isDebugEnabled()) logger.debug("updateing report bounce count"+smsCampaignReport.getBounces());
				smsCampaignSentDaoForDML.updateBounceStatus(statusInDB,smsCampaignSent.getSentId());
				//smsCampaignReportDao.updateBounceReport(smsCampaignReport.getSmsCrId());
				smsCampaignReportDaoForDML.updateBounceReport(smsCampaignReport.getSmsCrId());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new BaseServiceException("No dao found smsBouncesDao");
				}
			}//addToBounces
		public void addToSuppressedContacts(Users user, String mobile, 
				 String type, String reason) throws BaseServiceException{
			logger.info("entered adding to suppressed====");
			SMSSuppressedContactsDao smsSuppressedContactsDao = null;
			SMSSuppressedContactsDaoForDML smsSuppressedContactsDaoForDML = null;
			ContactsDao contactsDao = null;
			ContactsDaoForDML contactsDaoForDML = null;
			try {
				ServiceLocator locator = ServiceLocator.getInstance();
				smsSuppressedContactsDao = (SMSSuppressedContactsDao)locator.getDAOByName(OCConstants.SMS_SUPPRESSEDCONTACT_DAO);
				smsSuppressedContactsDaoForDML = (SMSSuppressedContactsDaoForDML)locator.getDAOForDMLByName(OCConstants.SMS_SUPPRESSEDCONTACT_DAO_FOR_DML);
				contactsDao = (ContactsDao)locator.getDAOByName(OCConstants.CONTACTS_DAO);
				contactsDaoForDML = (ContactsDaoForDML)locator.getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new BaseServiceException("No dao(s) found in the context with id ");
			}
			SMSSuppressedContacts suppressedContact = null;
			Long userId = null; 
			
			if(user != null) {
				
				userId = user.getUserId();
			}
			List<SMSSuppressedContacts> retList =  smsSuppressedContactsDao.searchContactsById(user, mobile);
			if(retList != null && retList.size() == 1) {
				suppressedContact = retList.get(0);
				//suppressedContact.setSuppressedtime(Calendar.getInstance());
				
			}
			else {
				
				suppressedContact = new SMSSuppressedContacts();
				suppressedContact.setUser(user);
				suppressedContact.setMobile(mobile);
				suppressedContact.setSuppressedtime(Calendar.getInstance());
				
			}
			//Users users = usersDao.find(userId); 
			suppressedContact.setType(type);
			suppressedContact.setReason(reason == null ? type :reason);
			try{
				
				//smsSuppressedContactsDao.saveOrUpdate(suppressedContact);
				smsSuppressedContactsDaoForDML.saveOrUpdate(suppressedContact);
			}catch (Exception e) {
				if(logger.isErrorEnabled()) logger.error("**Exception : while adding contact to suppress Contacts list :", e);
			}
				
			if(user != null)contactsDaoForDML.updatemobileStatus(mobile, type, user);
		}

}
