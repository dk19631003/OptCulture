package org.mq.captiway.scheduler.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.OCSMSGateway;
import org.mq.captiway.scheduler.beans.SMSBounces;
import org.mq.captiway.scheduler.beans.SMSCampaignReport;
import org.mq.captiway.scheduler.beans.SMSCampaignSent;
import org.mq.captiway.scheduler.beans.SMSDeliveryReport;
import org.mq.captiway.scheduler.beans.SMSSuppressedContacts;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.OCSMSGatewayDao;
import org.mq.captiway.scheduler.dao.SMSBouncesDao;
import org.mq.captiway.scheduler.dao.SMSBouncesDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDao;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDao;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDaoForDML;
import org.mq.captiway.scheduler.dao.SMSDeliveryReportDao;
import org.mq.captiway.scheduler.dao.SMSDeliveryReportDaoForDML;
import org.mq.captiway.scheduler.dao.SMSSuppressedContactsDao;
import org.mq.captiway.scheduler.dao.SMSSuppressedContactsDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.SMSStatusCodes;
import org.mq.optculture.exception.BaseDAOException;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.context.ApplicationContext;

public class SMSCampaignDeliveryReportsHandler {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private ApplicationContext context;
	public SMSCampaignDeliveryReportsHandler() {
		ServiceLocator serviceLocator = ServiceLocator.getInstance();
		
		try {
			smsDeliveryReportDao = (SMSDeliveryReportDao)serviceLocator.getDAOByName("smsDeliveryReportDao");
			smsDeliveryReportDaoForDML = (SMSDeliveryReportDaoForDML)serviceLocator.getDAOForDMLByName("smsDeliveryReportDaoForDML");
			smsCampaignSentDao = (SMSCampaignSentDao)serviceLocator.getDAOByName("smsCampaignSentDao");
			smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML )serviceLocator.getDAOForDMLByName("smsCampaignSentDaoForDML");
			smsBouncesDao = (SMSBouncesDao)serviceLocator.getDAOByName("smsBouncesDao");
			smsBouncesDaoForDML = (SMSBouncesDaoForDML)serviceLocator.getDAOForDMLByName("smsBouncesDaoForDML");
			smsCampaignReportDao = (SMSCampaignReportDao)serviceLocator.getDAOByName("smsCampaignReportDao");
			smsCampaignReportDaoForDML = (SMSCampaignReportDaoForDML)serviceLocator.getDAOForDMLByName("smsCampaignReportDaoForDML");
			smsSuppressedContactsDao = (SMSSuppressedContactsDao)serviceLocator.getDAOByName("smsSuppressedContactsDao");
			smsSuppressedContactsDaoForDML = (SMSSuppressedContactsDaoForDML)serviceLocator.getDAOForDMLByName("smsSuppressedContactsDaoForDML");
			contactsDao = (ContactsDao)serviceLocator.getDAOByName("contactsDao");
			contactsDaoForDML = (ContactsDaoForDML)serviceLocator.getDAOForDMLByName("contactsDaoForDML");
			OCSMSGatewayDao = (OCSMSGatewayDao)serviceLocator.getDAOByName("OCSMSGatewayDao");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("one of the bean is not available ", e);
		}
		
	}
	
	private SMSDeliveryReportDao smsDeliveryReportDao;
	private SMSDeliveryReportDaoForDML smsDeliveryReportDaoForDML;
	private SMSCampaignSentDao smsCampaignSentDao;
	private SMSCampaignSentDaoForDML smsCampaignSentDaoForDML;
	private OCSMSGatewayDao OCSMSGatewayDao;
	private SMSBouncesDao smsBouncesDao;
	private SMSBouncesDaoForDML smsBouncesDaoForDML;
	private SMSCampaignReportDao smsCampaignReportDao;
	private SMSCampaignReportDaoForDML smsCampaignReportDaoForDML;
	private SMSSuppressedContactsDao smsSuppressedContactsDao;
	private SMSSuppressedContactsDaoForDML smsSuppressedContactsDaoForDML;
	private ContactsDao contactsDao;
	private ContactsDaoForDML contactsDaoForDML;
	

	/*public SMSCampaignDeliveryReportsHandler(ApplicationContext context) {
		
		this.context = context;
		smsDeliveryReportDao = (SMSDeliveryReportDao)context.getBean("smsDeliveryReportDao");
		smsCampaignSentDao = (SMSCampaignSentDao)context.getBean("smsCampaignSentDao");
		smsBouncesDao = (SMSBouncesDao)context.getBean("smsBouncesDao");
		smsCampaignReportDao = (SMSCampaignReportDao)context.getBean("smsCampaignReportDao");
		smsSuppressedContactsDao = (SMSSuppressedContactsDao)context.getBean("smsSuppressedContactsDao");
		contactsDao = (ContactsDao)context.getBean("contactsDao");
		OCSMSGatewayDao = (OCSMSGatewayDao)context.getBean("OCSMSGatewayDao");
		
	}*/
	
	public boolean runHandlerForActiveDlrReports()  {
		
		try {
			MyCalendar myCal = new MyCalendar();
			CaptiwayToSMSApiGateway captiwayToSMSApiGateway = new CaptiwayToSMSApiGateway();
			List<SMSDeliveryReport> smsDlrList = smsDeliveryReportDao.getActiveList(""+(myCal.toString()));
			
			if(smsDlrList == null || smsDlrList.size() == 0) return true;
			logger.debug("fetched the active dlr list ::"+smsDlrList.size());
				List<SMSDeliveryReport> dlvrReportUpdateList = new ArrayList<SMSDeliveryReport>();
				
				for (SMSDeliveryReport smsDeliveryReport : smsDlrList) {
					
					if(smsDeliveryReport.getUserSMSTool() == null) {
						
						logger.warn("No sms gateway details found for this dlr report "+smsDeliveryReport.getSmsDlrId().longValue());
						continue;
						
					}
					
					OCSMSGateway ocsmsGateway = OCSMSGatewayDao.findById(smsDeliveryReport.getUserSMSTool());
					
					if(ocsmsGateway == null) {
						
						logger.warn("No sms gateway details found for this gateway "+smsDeliveryReport.getUserSMSTool().longValue());
						continue;
						
						
					}
					
					String ackType = captiwayToSMSApiGateway.findTypeOfAckBy(ocsmsGateway.getGatewayName());
					if(ackType == null) continue;
					
					Long smsCrId = smsDeliveryReport.getSmsCampRepId();
					if(ackType.equals(Constants.SMS_TYPE_OF_ACK_TRANSACTIONID)){
						//multiple sent objects may have same tid, its just like a groupTID
						//sometimes each obj will have its own tid but to follow one standard 
						//we will still store it under request_id column
						logger.debug("====fetch reports by TIDs====");
						updateSentByTID(ocsmsGateway, smsDeliveryReport);
						
					}else if(ackType.equals(Constants.SMS_TYPE_OF_ACK_MSGID)){
						//each msg will have its own msg id returned by gateway when submitted the request
						updateSentByMID(ocsmsGateway, smsDeliveryReport);
						
					}
					
					
					/*Users user = null;
					
					int totalSent = smsCampaignSentDao.getCount(smsCrId, "'"+Constants.SMS_SENT_STATUS_STATUS_PENDING+"','"+SMSStatusCodes.MVAYOO_STATUS_PENDING+"'");
					int totalUpdtedCount = 0;
					int size = 100;
					for(int i=0;i < totalSent; i+=size){*//*
						
						
						//TODO
						List<SMSCampaignSent> setList = smsCampaignSentDao.findByRepId(smsCrId, 
								"'"+Constants.SMS_SENT_STATUS_STATUS_PENDING+"','"+SMSStatusCodes.MVAYOO_STATUS_PENDING+"'", i, size);
						
						if(setList == null || setList.size() == 0) continue;//continue for next chunk of sent records //that there is a problem this means
						
						String msgIdStr = Constants.STRING_NILL;
						
						for (SMSCampaignSent smsCampaignSent : setList) {
							
							if(smsCampaignSent.getApiMsgId() == null || smsCampaignSent.getRequestId() == null) continue;
							
								msgIdStr += msgIdStr.isEmpty() ? smsCampaignSent.getApiMsgId() : 
										Constants.DELIMETER_COMMA+smsCampaignSent.getApiMsgId();
								
								
							
							
						}//for
						
						
						
						OCSMSGateway ocsmsGateway = OCSMSGatewayDao.findById(smsDeliveryReport.getUserSMSTool());
						
						if(ocsmsGateway == null) {
							
							logger.warn("No sms gateway details found for this gateway "+smsDeliveryReport.getUserSMSTool().longValue());
							continue;
							
							
						}
						
						Map<String, String> returnStatusMap = captiwayToSMSApiGateway.requestToSMSApi(ocsmsGateway, msgIdStr);
						
						if(returnStatusMap == null || returnStatusMap.size() == 0) continue;//continue for next chunk of sent records
						
						logger.debug("returned map size:: "+returnStatusMap.size()+" For report ::"+smsCrId);
						
						List<SMSCampaignSent> listToBeUpdated = new ArrayList<SMSCampaignSent>();
						List<SMSBounces> smsBounceList = new ArrayList<SMSBounces>();
						List<String> contactMobilesList = new ArrayList<String>();
						List<SMSSuppressedContacts> suppressedContactsList = new ArrayList<SMSSuppressedContacts>(); 
						
						for (String msgId : returnStatusMap.keySet()) {
							
							if(returnStatusMap.get(msgId) == null) continue;
							
							for (SMSCampaignSent smsCampaignSent : setList) {
								
								try {
									if(smsCampaignSent.getApiMsgId()  == null ) continue;
									
									if(smsCampaignSent.getApiMsgId().equals(msgId)) {
										
										if(user == null) user = smsCampaignSent.getSmsCampaignReport().getUser();
										
										totalUpdtedCount ++;
										
										String status = MVaayooApi.MVaayooStatusCodes.get(returnStatusMap.get(msgId));
										if(status == null) status = SMSStatusCodes.CLICKATELL_STATUS_DELIVERY_ERROR;
										
										smsCampaignSent.setStatus(status);
										if(status.equals(SMSStatusCodes.MVAYOO_STATUS_CANCELLED) ||
												status.equals(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER ) || 
												status.equals(SMSStatusCodes.CLICKATELL_STATUS_DELIVERY_ERROR ) ||
												status.equals(Constants.SMS_SUPP_TYPE_DND ) ||
												status.equals(SMSStatusCodes.MVAYOO_STATUS_DND_INVALID_10_DIGIT ) ||
												status.equals(SMSStatusCodes.CLICKATELL_STATUS_MESSAGE_EXPIRED )) {
											
											//TODO need to handle all types of undelivered mobile numbers here
											//and add those into sms_bounces table
											
											smsCampaignSent.setStatus(SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);
											
											SMSBounces newBounce= new SMSBounces();
											newBounce.setCrId(smsCampaignSent.getSmsCampaignReport().getSmsCrId());
											newBounce.setSentId(smsCampaignSent);
											newBounce.setMessage(status);
											newBounce.setMobile(smsCampaignSent.getMobileNumber());
											newBounce.setCategory(status);
											newBounce.setBouncedDate(Calendar.getInstance());
											smsBounceList.add(newBounce);
											
											if(status.equals(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER ) ||
													//status.equals(Constants.SMS_SUPP_TYPE_DND ) || 
													status.equals(SMSStatusCodes.MVAYOO_STATUS_DND_INVALID_10_DIGIT )) {
												
												
												
												SMSSuppressedContacts suppressedContact = new SMSSuppressedContacts();
												suppressedContact.setUser(user);
												suppressedContact.setMobile(smsCampaignSent.getMobileNumber());
												suppressedContact.setType(status);
												suppressedContact.setReason(status);
												suppressedContact.setSuppressedtime(Calendar.getInstance());
												
												suppressedContactsList.add(suppressedContact);
												
												addToSuppressedContacts(user, 
														smsCampaignSent.getMobileNumber(), status);
												
												
												
												if(status.equals(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER ) ||
														status.equals(SMSStatusCodes.MVAYOO_STATUS_DND_INVALID_10_DIGIT )) {
													contactMobilesList.add(smsCampaignSent.getMobileNumber());
													//contactsDao.updatemobileStatus(smsCampaignSent.getMobileNumber(), status, user.getUserId());
													
												}
											}
											
											//addToBounces(smsCampaignSent.getSmsCampaignReport(), smsCampaignSent.getMobileNumber(), smsCampaignSent, status);
										}
										
										
										
										listToBeUpdated.add(smsCampaignSent);
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									logger.error("error while fetching the Active reports-inner for each status",e);
								}
								
							}//for
							
						}//for
						
						
					*///}//for
					
					//this is required 
					/*int pendingCount = smsCampaignSentDao.getCount(smsCrId, "'"+Constants.SMS_SENT_STATUS_STATUS_PENDING+"','"+SMSStatusCodes.MVAYOO_STATUS_PENDING+"'");
					if(totalSent == totalUpdtedCount && pendingCount == 0) {
						
						smsDeliveryReport.setStatus(Constants.CR_DLVR_STATUS_FETCHED);
						dlvrReportUpdateList.add(smsDeliveryReport);
						
					}*/
					
					
				}//for

			/*if(dlvrReportUpdateList.size() > 0) {
				
				smsDeliveryReportDao.saveByCollection(dlvrReportUpdateList);
				dlvrReportUpdateList.clear();
			}
			*/
			/*//this is require to set all the sent objects whose tids are expired
			List<SMSDeliveryReport> smsExpiredDlrList = smsDeliveryReportDao.getExpiredList(""+(myCal.toString()));
			
			logger.debug("fetched the expired dlr list ::"+smsExpiredDlrList.size());
			if(smsExpiredDlrList == null || smsExpiredDlrList.size() == 0) return true;
			List<SMSDeliveryReport> expiredDlvrReportUpdateList = new ArrayList<SMSDeliveryReport>();
			
			for (SMSDeliveryReport smsExpiredDeliveryReport : expiredDlvrReportUpdateList) {
				
				Long smsCrId = smsExpiredDeliveryReport.getSmsCampRepId();
				Users user = null;
				int totalSent = smsCampaignSentDao.getCount(smsCrId, Constants.SMS_SENT_STATUS_STATUS_PENDING);
				int totalUpdtedCount = 0;
				int size = 100;
				for(int i=0;i < totalSent; i+=size){
					
					//TODO
					List<SMSCampaignSent> setList = smsCampaignSentDao.findByRepId(smsCrId, Constants.SMS_SENT_STATUS_STATUS_PENDING, i, size);
					
					if(setList == null || setList.size() == 0) continue;//continue for next chunk of sent records //that there is a problem this means

					List<SMSCampaignSent> listToBeUpdated = new ArrayList<SMSCampaignSent>();
					List<SMSBounces> smsBounceList = new ArrayList<SMSBounces>();
					
					for (SMSCampaignSent smsCampaignSent : setList) {
						
						smsCampaignSent.setStatus(SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);
						
						SMSBounces newBounce= new SMSBounces();
						newBounce.setCrId(smsCampaignSent.getSmsCampaignReport().getSmsCrId());
						newBounce.setSentId(smsCampaignSent);
						newBounce.setMessage(SMSStatusCodes.CLICKATELL_STATUS_MESSAGE_EXPIRED);
						newBounce.setMobile(smsCampaignSent.getMobileNumber());
						newBounce.setCategory(SMSStatusCodes.CLICKATELL_STATUS_MESSAGE_EXPIRED);
						newBounce.setBouncedDate(Calendar.getInstance());
						smsBounceList.add(newBounce);
					
						
						
						
					}
				
					if(listToBeUpdated.size() > 0) {
						
						smsCampaignSentDao.saveByCollection(listToBeUpdated);
						listToBeUpdated.clear();
						
						if(smsBounceList.size() > 0) {
							
							smsBouncesDao.saveByCollection(smsBounceList);
							smsBounceList.clear();
						}
					
						int updateCount = smsCampaignReportDao.updateBounceReport(smsCrId);
						logger.debug("bounced count  ::"+updateCount);
					}
				}
				
				smsExpiredDeliveryReport.setStatus(Constants.CR_DLVR_STATUS_FETCHED);
				expiredDlvrReportUpdateList.add(smsExpiredDeliveryReport);
			
			}//for
			
			if(expiredDlvrReportUpdateList.size() > 0) {
				
				smsDeliveryReportDao.saveByCollection(expiredDlvrReportUpdateList);
				expiredDlvrReportUpdateList.clear();
			}*/
			
			
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("error while fetching the Active reports",e);
			return false;
		}
		
		
	}//runHandler()
	
	public void updateSentByTID(OCSMSGateway ocsmsGateway , SMSDeliveryReport smsDeliveryReport) throws Exception{
		
		//get the count of TIDs from sent objects 
		Long smsCrId = smsDeliveryReport.getSmsCampRepId();
		long totalSent;
			totalSent = smsCampaignSentDao.getAllRequestIdsCountBy(smsCrId);
		
		if(totalSent == 0) return;
		
		int size = 100;
		for(int i=0;i < totalSent; i+=size){
			
			List<String> tidList = smsCampaignSentDao.getAllRequestIdsBy(smsCrId, i, size);
			
			if(tidList == null || tidList.size() == 0){
				logger.debug("===no TIDs fetched from DB====");
				continue;
			}
			
			String TIDStr = Constants.STRING_NILL;
			String TIDStrForQry = Constants.STRING_NILL;
			
			for (String tid : tidList) {
				
				if(!TIDStr.isEmpty()) TIDStr += Constants.DELIMETER_COMMA;
				TIDStr += tid;
				
				if(!TIDStrForQry.isEmpty()) TIDStrForQry += Constants.DELIMETER_COMMA;
				TIDStrForQry += "'"+tid+"'";
				
			}
			CaptiwayToSMSApiGateway captiwayToSMSApiGateway = new CaptiwayToSMSApiGateway();
			Map<String, String> returnStatusMap = captiwayToSMSApiGateway.requestToSMSApi(ocsmsGateway, TIDStr);
			
			if(returnStatusMap == null || returnStatusMap.size() == 0){
				logger.debug("===no dlrs fetched from gateway====");
				continue;//continue for next chunk of sent records
			}
			
			long totCount = smsCampaignSentDao.findCountBy(smsCrId, TIDStrForQry);
			
			int fetchSize = 100;
			List<SMSCampaignSent> listToBeUpdated = new ArrayList<SMSCampaignSent>();
			for(int j=0; j < totCount; j+=fetchSize){
				
				List<SMSCampaignSent> sentList =  smsCampaignSentDao.findBy(smsCrId, TIDStrForQry, j, fetchSize);
				if(sentList== null || sentList.size() == 0) {
					logger.debug("===no sent objs fetched from gateway====");
					continue;//continue for next chunk of sent records
				}
				for (SMSCampaignSent smsCampaignSent : sentList) {
					if(returnStatusMap.containsKey(smsCampaignSent.getMobileNumber())){
						String status = (returnStatusMap.get(smsCampaignSent.getMobileNumber()).split(Constants.ADDR_COL_DELIMETER)[1]);
						smsCampaignSent.setStatus(status);
						listToBeUpdated.add(smsCampaignSent);
					}
					
				}
				if(listToBeUpdated.size() > 0) {
					//smsCampaignSentDao.saveByCollection(listToBeUpdated);
					smsCampaignSentDaoForDML.saveByCollection(listToBeUpdated);

					listToBeUpdated.clear();
					//int updateCount = smsCampaignReportDao.updateBounceReport(smsCrId);
					int updateCount = smsCampaignReportDaoForDML.updateBounceReport(smsCrId);
					logger.debug("bounced count  ::"+updateCount);
				}
			}
			
			/*logger.debug("returned map size:: "+returnStatusMap.size()+" For report ::"+smsCrId);
			StringBuilder mobileBuilder = new StringBuilder(); 
			for (String Tids : returnStatusMap.values()) {
				
				if(mobileBuilder.length() >0) mobileBuilder.append(Constants.DELIMETER_COMMA);
				
				mobileBuilder.append(mobile);
				
				
			}//for
			
			smsCampaignSentDao.fin*/
		}
		
		
		int pendingCount = smsCampaignSentDao.getCount(smsCrId, "'"+Constants.SMS_SENT_STATUS_STATUS_PENDING+"','"+SMSStatusCodes.MVAYOO_STATUS_PENDING+"'");
		if(pendingCount == 0) {
			
			smsDeliveryReport.setStatus(Constants.CR_DLVR_STATUS_FETCHED);
			//smsDeliveryReportDao.saveOrUpdate(smsDeliveryReport);
			smsDeliveryReportDaoForDML.saveOrUpdate(smsDeliveryReport);
			
		}
	}
	public void updateSentByMID(OCSMSGateway ocsmsGateway , SMSDeliveryReport smsDeliveryReport) throws Exception{
		
		CaptiwayToSMSApiGateway captiwayToSMSApiGateway = new CaptiwayToSMSApiGateway();
		Users user = null;
		Long smsCrId = smsDeliveryReport.getSmsCampRepId();
		
		int totalSent = smsCampaignSentDao.getCount(smsCrId, "'"+Constants.SMS_SENT_STATUS_STATUS_PENDING+
						"','"+SMSStatusCodes.MVAYOO_STATUS_PENDING+"'");
		int totalUpdtedCount = 0;
		int size = 100; //keep size 50 for Infocomm, as per their API
		if(ocsmsGateway.getGatewayName().equalsIgnoreCase(Constants.USER_SMSTOOL_INFOCOMM)) size=50;
		for(int i=0;i < totalSent; i+=size){
			//TODO
			List<SMSCampaignSent> setList = smsCampaignSentDao.findByRepId(smsCrId, 
					"'"+Constants.SMS_SENT_STATUS_STATUS_PENDING+"','"+SMSStatusCodes.MVAYOO_STATUS_PENDING+"'", i, size);
			
			if(setList == null || setList.size() == 0) continue;//continue for next chunk of sent records //that there is a problem this means
			
			String msgIdStr = Constants.STRING_NILL;
			
			for (SMSCampaignSent smsCampaignSent : setList) {
				
				if(smsCampaignSent.getApiMsgId() == null ) continue;
				
					msgIdStr += msgIdStr.isEmpty() ? smsCampaignSent.getApiMsgId() : 
							Constants.DELIMETER_COMMA+smsCampaignSent.getApiMsgId();
					
					
				
				
			}//for
					
					Map<String, String> returnStatusMap = captiwayToSMSApiGateway.requestToSMSApi(ocsmsGateway, msgIdStr);
					
					if(returnStatusMap == null || returnStatusMap.size() == 0) continue;//continue for next chunk of sent records
					
					logger.debug("returned map size:: "+returnStatusMap.size()+" For report ::"+smsCrId);
					
					List<SMSCampaignSent> listToBeUpdated = new ArrayList<SMSCampaignSent>();
					List<SMSBounces> smsBounceList = new ArrayList<SMSBounces>();
					List<String> contactMobilesList = new ArrayList<String>();
					List<SMSSuppressedContacts> suppressedContactsList = new ArrayList<SMSSuppressedContacts>();
					logger.info("keyset "+returnStatusMap.keySet());
					for (String msgId : returnStatusMap.keySet()) {
						
						if(returnStatusMap.get(msgId) == null) continue;
						
						for (SMSCampaignSent smsCampaignSent : setList) {
							
							try {
								if(smsCampaignSent.getApiMsgId()  == null ) continue;
								int apiMsgId=Integer.parseInt(smsCampaignSent.getApiMsgId());
								int msgid=Integer.parseInt(msgId);
								logger.info("apiMsgId "+apiMsgId);
								logger.info("msgid "+msgid);
								if(apiMsgId==msgid) {
									logger.info("apiMsgId equal to msgid");
									if(ocsmsGateway.getGatewayName().equalsIgnoreCase(Constants.USER_SMSTOOL_MVAYOO)) {
									
									if(user == null) user = smsCampaignSent.getSmsCampaignReport().getUser();
									
									totalUpdtedCount ++;
									
									String status = MVaayooApi.MVaayooStatusCodes.get(returnStatusMap.get(msgId));
									if(status == null) status = SMSStatusCodes.CLICKATELL_STATUS_DELIVERY_ERROR;
									
									smsCampaignSent.setStatus(status);
									if(status.equals(SMSStatusCodes.MVAYOO_STATUS_CANCELLED) ||
											status.equals(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER ) || 
											status.equals(SMSStatusCodes.CLICKATELL_STATUS_DELIVERY_ERROR ) ||
											status.equals(Constants.SMS_SUPP_TYPE_DND ) ||
											status.equals(SMSStatusCodes.MVAYOO_STATUS_DND_INVALID_10_DIGIT ) ||
											status.equals(SMSStatusCodes.CLICKATELL_STATUS_MESSAGE_EXPIRED )) {
										
										//TODO need to handle all types of undelivered mobile numbers here
										//and add those into sms_bounces table
										
										smsCampaignSent.setStatus(SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);
										
										SMSBounces newBounce= new SMSBounces();
										newBounce.setCrId(smsCampaignSent.getSmsCampaignReport().getSmsCrId());
										newBounce.setSentId(smsCampaignSent);
										newBounce.setMessage(status);
										newBounce.setMobile(smsCampaignSent.getMobileNumber());
										newBounce.setCategory(status);
										newBounce.setBouncedDate(Calendar.getInstance());
										smsBounceList.add(newBounce);
										
										if(status.equals(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER ) ||
												//status.equals(Constants.SMS_SUPP_TYPE_DND ) || 
												status.equals(SMSStatusCodes.MVAYOO_STATUS_DND_INVALID_10_DIGIT )) {
											
											
											
											SMSSuppressedContacts suppressedContact = new SMSSuppressedContacts();
											suppressedContact.setUser(user);
											suppressedContact.setMobile(smsCampaignSent.getMobileNumber());
											suppressedContact.setType(status);
											suppressedContact.setReason(status);
											suppressedContact.setSuppressedtime(Calendar.getInstance());
											
											suppressedContactsList.add(suppressedContact);
											
											/*addToSuppressedContacts(user, 
													smsCampaignSent.getMobileNumber(), status);
											*/
											
											
											if(status.equals(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER ) ||
													status.equals(SMSStatusCodes.MVAYOO_STATUS_DND_INVALID_10_DIGIT )) {
												contactMobilesList.add(smsCampaignSent.getMobileNumber());
												//contactsDao.updatemobileStatus(smsCampaignSent.getMobileNumber(), status, user.getUserId());
												
											}
										}
										
										//addToBounces(smsCampaignSent.getSmsCampaignReport(), smsCampaignSent.getMobileNumber(), smsCampaignSent, status);
									}
									
									
									
									listToBeUpdated.add(smsCampaignSent);
								}
							//}
									else if(ocsmsGateway.getGatewayName().equalsIgnoreCase(Constants.USER_SMSTOOL_INFOCOMM)) { 
									
									if(user == null) user = smsCampaignSent.getSmsCampaignReport().getUser();
									
									totalUpdtedCount ++;
									
									//if(returnStatusMap.keySet().contains(smsCampaignSent.getApiMsgId())) {
									//logger.info("contains in keyset ");
									String status = SMSStatusCodes.infocommStatusCodesMap.get(returnStatusMap.get(msgId));
									logger.info("status :"+status);
									if(status == null) status = SMSStatusCodes.INFOCOMM_REPORTS_UNKNOWN_ERROR;
									if(status!=null && !status.isEmpty()) smsCampaignSent.setStatus(status);
									if(status.equalsIgnoreCase(SMSStatusCodes.INFOCOMM_REPORTS_DELIVERED)) {
										smsCampaignSent.setStatus(SMSStatusCodes.INFOCOMM_REPORTS_DELIVERED);
									}
									if(status.equals(SMSStatusCodes.INFOCOMM_REPORTS_UNKNOWN_ERROR)) {
										
										//TODO need to handle all types of undelivered mobile numbers here
										//and add those into sms_bounces table
										
										smsCampaignSent.setStatus(SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);
										
										SMSBounces newBounce= new SMSBounces();
										newBounce.setCrId(smsCampaignSent.getSmsCampaignReport().getSmsCrId());
										newBounce.setSentId(smsCampaignSent);
										newBounce.setMessage(status);
										newBounce.setMobile(smsCampaignSent.getMobileNumber());
										newBounce.setCategory(status);
										newBounce.setBouncedDate(Calendar.getInstance());
										smsBounceList.add(newBounce);
										
									}
								//}
									
									listToBeUpdated.add(smsCampaignSent);
								}	
									
							  }
							} catch (Exception e) {
								// TODO Auto-generated catch block
								logger.error("error while fetching the Active reports-inner for each status",e);
							}
							
						}//for
						
					}//for
					
					if(listToBeUpdated.size() > 0) {
						
						//smsCampaignSentDao.saveByCollection(listToBeUpdated);
						smsCampaignSentDaoForDML.saveByCollection(listToBeUpdated);

						listToBeUpdated.clear();
						
						if(smsBounceList.size() > 0) {
							
							//smsBouncesDao.saveByCollection(smsBounceList);
							smsBouncesDaoForDML.saveByCollection(smsBounceList);

							smsBounceList.clear();
						}
						
						
						if(suppressedContactsList.size() > 0) {
							
							addToSuppressedContacts(user, suppressedContactsList);
							
						}
						
						if(contactMobilesList.size() > 0) {
							
							try {
								String mobileStr = Constants.STRING_NILL;
								for (String mobile : contactMobilesList) {
									
									if(mobile.startsWith(user.getCountryCarrier().toString())) {
										
										mobile = mobile.substring(user.getCountryCarrier().toString().length());
										
									}
									
									if(!mobileStr.isEmpty()) mobileStr += "|";
									
									mobileStr += mobile;
									
									
									
								}
								contactsDaoForDML.updatemobileStatusForMultipleContacts(mobileStr, 
										SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER, user.getUserId());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								logger.error("Exception while updating contacts ", e);
								
							}
							
							contactMobilesList.clear();
						}
						//TODO need to update bounce count in sms reports
						//int updateCount = smsCampaignReportDao.updateBounceReport(smsCrId);
						int updateCount = smsCampaignReportDaoForDML.updateBounceReport(smsCrId);
						logger.debug("bounced count  ::"+updateCount);
						//smsCampaignSentDao.saveByCollection(listToBeUpdated);
						
					}//if
					
				
			}//for
			
			int pendingCount = smsCampaignSentDao.getCount(smsCrId, "'"+Constants.SMS_SENT_STATUS_STATUS_PENDING+"','"+SMSStatusCodes.MVAYOO_STATUS_PENDING+"'");
			logger.info("pendingCount "+pendingCount);
			if(totalSent == totalUpdtedCount && pendingCount == 0) {
				
				smsDeliveryReport.setStatus(Constants.CR_DLVR_STATUS_FETCHED);
				//smsDeliveryReportDao.saveOrUpdate(smsDeliveryReport);
				smsDeliveryReportDaoForDML.saveOrUpdate(smsDeliveryReport);
				
			}
	}
	
	public boolean runHandlerForExpiredDlrReports()  {
		
	try {
		MyCalendar myCal = new MyCalendar();
		/*CaptiwayToSMSApiGateway router = new CaptiwayToSMSApiGateway();
		int expiredPeriod = router.getExpiryPeriod(ocsmsGateway);*/
		//this is require to set all the sent objects whose tids are expired
		List<SMSDeliveryReport> smsExpiredDlrList = smsDeliveryReportDao.getExpiredList(""+(myCal.toString()));
		
		logger.debug("fetched the expired dlr list ::"+smsExpiredDlrList.size());
		if(smsExpiredDlrList == null || smsExpiredDlrList.size() == 0) return true;
		List<SMSDeliveryReport> expiredDlvrReportUpdateList = new ArrayList<SMSDeliveryReport>();
		
		for (SMSDeliveryReport smsExpiredDeliveryReport : smsExpiredDlrList) {
			
			Long smsCrId = smsExpiredDeliveryReport.getSmsCampRepId();
			Users user = null;
			int totalSent = smsCampaignSentDao.getCount(smsCrId, "'"+Constants.SMS_SENT_STATUS_STATUS_PENDING+"','"+SMSStatusCodes.MVAYOO_STATUS_PENDING+"'");
			int totalUpdtedCount = 0;
			int size = 100;
			for(int i=0;i < totalSent; i+=size){
				
				//TODO
				List<SMSCampaignSent> setList = smsCampaignSentDao.findByRepId(smsCrId, 
						"'"+Constants.SMS_SENT_STATUS_STATUS_PENDING+"','"+SMSStatusCodes.MVAYOO_STATUS_PENDING+"'", i, size);
				
				if(setList == null || setList.size() == 0) continue;//continue for next chunk of sent records //that there is a problem this means

				List<SMSCampaignSent> listToBeUpdated = new ArrayList<SMSCampaignSent>();
				List<SMSBounces> smsBounceList = new ArrayList<SMSBounces>();
				
				for (SMSCampaignSent smsCampaignSent : setList) {
					
					smsCampaignSent.setStatus(SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);
					
					SMSBounces newBounce= new SMSBounces();
					newBounce.setCrId(smsCampaignSent.getSmsCampaignReport().getSmsCrId());
					newBounce.setSentId(smsCampaignSent);
					newBounce.setMessage(SMSStatusCodes.CLICKATELL_STATUS_MESSAGE_EXPIRED);
					newBounce.setMobile(smsCampaignSent.getMobileNumber());
					newBounce.setCategory(SMSStatusCodes.CLICKATELL_STATUS_MESSAGE_EXPIRED);
					newBounce.setBouncedDate(Calendar.getInstance());
					smsBounceList.add(newBounce);
				
					listToBeUpdated.add(smsCampaignSent);
					
					
				}
			
				if(listToBeUpdated.size() > 0) {
					
					//smsCampaignSentDao.saveByCollection(listToBeUpdated);
					smsCampaignSentDaoForDML.saveByCollection(listToBeUpdated);
					listToBeUpdated.clear();
					
					if(smsBounceList.size() > 0) {
						
						//smsBouncesDao.saveByCollection(smsBounceList);
						smsBouncesDaoForDML.saveByCollection(smsBounceList);

						smsBounceList.clear();
					}
				
					//int updateCount = smsCampaignReportDao.updateBounceReport(smsCrId);
					int updateCount = smsCampaignReportDaoForDML.updateBounceReport(smsCrId);
					logger.debug("bounced count  ::"+updateCount);
				}
			}
			
			smsExpiredDeliveryReport.setStatus(Constants.CR_DLVR_STATUS_FETCHED);
			expiredDlvrReportUpdateList.add(smsExpiredDeliveryReport);
		
		}//for
		
		if(expiredDlvrReportUpdateList.size() > 0) {
			
			//smsDeliveryReportDao.saveByCollection(expiredDlvrReportUpdateList);
			smsDeliveryReportDaoForDML.saveByCollection(expiredDlvrReportUpdateList);
			expiredDlvrReportUpdateList.clear();
		}
		//**************************************************
			
			
			
			
			
			return true;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("error while fetching the expired reports",e);
		return false;
	}
		
		
	}//runHandler()
	
	
	
	
	public void addToBounces(SMSCampaignReport smsCampaignReport, String mobile,
			SMSCampaignSent smsCampaignSent, String status ) {
		
		if(smsCampaignSent == null || smsCampaignReport == null) {
			
			if(logger.isErrorEnabled()) logger.error("** got source objects as null");
			return;
			
		}
		//need to find the existing object with the same given mobile if any..
		Long crId = smsCampaignReport.getSmsCrId(); 
		SMSBounces newBounce = smsBouncesDao.findBymobile(crId, mobile);
		
		if(newBounce == null) {
			
			newBounce = new SMSBounces();
			newBounce.setCrId(crId);
			newBounce.setSentId(smsCampaignSent);
			
			
		}
		
		newBounce.setMessage(status);
		newBounce.setMobile(mobile);
		newBounce.setCategory(status);
		newBounce.setBouncedDate(Calendar.getInstance());
		//smsBouncesDao.saveOrUpdate(newBounce);
		smsBouncesDaoForDML.saveOrUpdate(newBounce);

		//smsCampaignReportDao.updateBounceReport(smsCampaignReport.getSmsCrId());
		
		
	}//addToBounces
	
	public void addToSuppressedContacts(Users user, List<SMSSuppressedContacts> suppressedContactsList ) {
		try {
			String mobileStr = Constants.STRING_NILL;
			
			Map<String, String> suppressStatusMap = new HashMap<String, String>();
			
			for (SMSSuppressedContacts smsSuppressedContacts : suppressedContactsList) {
				
				if(!mobileStr.isEmpty()) mobileStr += "|";
				
				mobileStr += smsSuppressedContacts.getMobile();
				suppressStatusMap.put(smsSuppressedContacts.getMobile(), smsSuppressedContacts.getType());
				
			}
			
			
			List<SMSSuppressedContacts> retList =  smsSuppressedContactsDao.searchContactsByMultipleMobiles(user.getUserId(), mobileStr);
			List<SMSSuppressedContacts> updateList = new ArrayList<SMSSuppressedContacts>();
			List<SMSSuppressedContacts> newList = new ArrayList<SMSSuppressedContacts>();
			if(retList != null && retList.size() > 0) {
				
				if(logger.isDebugEnabled()) logger.debug("suppressed contacts  already exist ");
				
				for (SMSSuppressedContacts smsSuppressedContact : retList) {
					
					smsSuppressedContact.setType(suppressStatusMap.get(smsSuppressedContact.getMobile()));
					smsSuppressedContact.setReason(suppressStatusMap.get(smsSuppressedContact.getMobile()));
					updateList.add(smsSuppressedContact);
					
					
					
				}//for
				
				
			}//if
			
			for (SMSSuppressedContacts smsSuppressedContacts : suppressedContactsList) {
				
				if(retList != null && retList.size() > 0 && retList.contains(smsSuppressedContacts)) continue;
				
				newList.add(smsSuppressedContacts);
				
				
			}
			
			if(updateList.size() > 0) {
				
				//smsSuppressedContactsDao.saveByCollection(updateList);
				smsSuppressedContactsDaoForDML.saveByCollection(updateList);
			}
			if(newList.size() > 0) {
				
				//smsSuppressedContactsDao.saveByCollection(newList);
				smsSuppressedContactsDaoForDML.saveByCollection(newList);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception while updating suppressed contacts ", e);
		}
		
		
		
		
		
		
	}
	
	
	
	public void addToSuppressedContacts(Users user, String mobile, String reason) {
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
				suppressedContact.setSuppressedtime(Calendar.getInstance());
				
			}
			//Users users = usersDao.find(userId); 
			suppressedContact.setType(reason);
			//smsSuppressedContactsDao.saveOrUpdate(suppressedContact);
			smsSuppressedContactsDaoForDML.saveOrUpdate(suppressedContact);
			if(logger.isDebugEnabled()) logger.debug("Added successfully to sms suppress contacts .");
		} catch (Exception e) {
			if(logger.isErrorEnabled()) logger.error("**Exception : while adding contact to suppress Contacts list :", e);
		}
	}
	
	
}
