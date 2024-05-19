package org.mq.optculture.business.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.SMSBounces;
import org.mq.captiway.scheduler.beans.SMSCampaignReport;
import org.mq.captiway.scheduler.beans.SMSCampaignSent;
import org.mq.captiway.scheduler.beans.SMSSuppressedContacts;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.SMSBouncesDao;
import org.mq.captiway.scheduler.dao.SMSBouncesDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDao;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDao;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDaoForDML;
import org.mq.captiway.scheduler.dao.SMSSuppressedContactsDao;
import org.mq.captiway.scheduler.services.InfobipSMSGateway;
import org.mq.captiway.scheduler.services.SMSCampaignDeliveryReportsHandler;
import org.mq.captiway.scheduler.services.UnicelSMSGateway;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.SMSStatusCodes;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.smpp.pdu.SubmitSMResp;

public class SMSDlrReportUpdateThread extends Thread{
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER); 
	public   Map<Long, String> updateStatusMap ;
	public  Map<Long, String> getUpdateStatusMap() {
		
		return updateStatusMap;
	}
	public void setUpdateStatusMap(Map<Long, String> updateStatusMap) {
		this.updateStatusMap = updateStatusMap;
	}

	private   Set<String> unicelDlrReciepts ;
	private   Set<String> infoBipDlrReciepts ;
	
	public  Set<String>  getUnicelDlrReciepts() {
		return unicelDlrReciepts;
	}

	public void setUnicelDlrReciepts(Set<String> unicelDlrReciepts) {
		this.unicelDlrReciepts = unicelDlrReciepts;
	}

	public  Set<String> getInfoBipDlrReciepts() {
		
		return infoBipDlrReciepts;
	}

	public void setInfoBipDlrReciepts(Set<String> infoBipDlrReciepts) {
		this.infoBipDlrReciepts = infoBipDlrReciepts;
	}
	public SMSDlrReportUpdateThread() {
		// TODO Auto-generated constructor stub
	}
	
	public SMSDlrReportUpdateThread(Set<String> unicelDlrReciepts, Set<String> infoBipDlrReciepts, Map<Long, String> updateStatusMap) {
		this.unicelDlrReciepts = unicelDlrReciepts;
		this.infoBipDlrReciepts = infoBipDlrReciepts;
		this.updateStatusMap = updateStatusMap;
		logger.debug(">>>>>>> infoBipDlrReciepts >>>>>"+ (infoBipDlrReciepts != null ? infoBipDlrReciepts.size() : "infoBipDlrReciepts is Null or 0")); 
	}
	
	public void run() {
		
	logger.debug(">>>>>>> Started SMSDlrReportUpdateThread :: run <<<<<<< ");
		
		try {
			processUpdateStatusMap(this.getUpdateStatusMap());
			processDlrReciepts(this.getInfoBipDlrReciepts(), Constants.USER_SMSTOOL_INFOBIP);
			processDlrReciepts(this.getUnicelDlrReciepts(), Constants.USER_SMSTOOL_UNICEL);
		} catch (BaseServiceException e) {
			logger.error("Exception while processing Status map / dlrreceipts ", e);
		} catch (Exception e) {
			logger.error("Exception while processing Status map / dlrreceipts ", e);
		}
		
	logger.debug(">>>>>>> Completed SMSDlrReportUpdateThread :: run <<<<<<< ");
		
		
	}

	/**
	 * 
	 * @param dlrReciepts
	 * @param userSMSTool
	 * @throws Exception
	 */
	public void processDlrReciepts(Set<String> dlrReciepts, String userSMSTool) throws Exception{
	logger.debug(">>>>>>> Started SMSDlrReportUpdateThread :: processDlrReciepts <<<<<<< ");
	logger.debug("Size of dlr receipts ............."+ (dlrReciepts != null ? dlrReciepts.size() : "dlrReciepts is null or 0" ));
		if(dlrReciepts == null|| dlrReciepts.size() == 0) {
			logger.warn("Some error got, as this is called to process but no entries found.");
			return;
		}
		//process the msg_idsmap first not required now
		/*Map<Long, String> updateStatusMap = getUpdateStatusMap();
		if(updateStatusMap != null && updateStatusMap.size() > 0 ){
			SMSCConnectorObj.processUpdateStatusMap(null, updateStatusMap, true);
			//*** commented code***
			Map<Long, String> tempUpdateStatusMap = new HashMap<Long, String>();
			tempUpdateStatusMap.putAll(updateStatusMap);
			tempUpdateStatusMap.clear();
			//*** end comment ****
		}*/
		
		Map<String, String> DlrMap = null;
		Map<Integer,String> InfobipDlrMap = null;
		Set<String> DeliveredSet = null ;
	    Set<String> SuppressedSet = null;
		//suppressed
		
		/**
		 * Every Gateway has its own set of status codes,hence depending on service provider creating map's
		 * 
		 */
		if(userSMSTool.equals(Constants.USER_SMSTOOL_UNICEL) ) {
			
			DlrMap = UnicelSMSGateway.unicellDlrMap;
			DeliveredSet = UnicelSMSGateway.deliveredStatusSet;
			SuppressedSet = UnicelSMSGateway.suppressedStatusSet;
			
		}else if(userSMSTool.equals(Constants.USER_SMSTOOL_INFOBIP) ) {
			
			//DlrMap = InfobipSMSGateway.infobipDlrMap;
			InfobipDlrMap = InfobipSMSGateway.infobipDlrMap;
			DeliveredSet = InfobipSMSGateway.deliveredStatusSet;
			SuppressedSet = InfobipSMSGateway.suppressedStatusSet;
		}
		
		
		String[] dlrmsgTokenArr = null;
		String sentIdsStr = Constants.STRING_NILL;
		String sentIdsWithNoMsgId = Constants.STRING_NILL;
		
		Map<String, String> updateSentDlrMap = new HashMap<String, String>();
		
		Map<String, Integer> infobipUpdateSentDlrMap = new HashMap<String, Integer>();
		try{
			for (String recptMessage : dlrReciepts) {

				String msgID =  null;
				String seqNum = null;
				String statusCode = null;
				Integer infobipIntegralStatusCode = null;
				dlrmsgTokenArr = recptMessage.split(Constants.STRING_WHITESPACE);


				for (String token : dlrmsgTokenArr) {

					String[] tokenArr = token.split(Constants.DELIMETER_COLON);

					if(tokenArr[0].equals(Constants.SMS_DLR_STATUSCODE_TOKEN)) {
						//Status code should be 0000
						statusCode = tokenArr[1];
						logger.info("status Code"+statusCode);

						if(userSMSTool.equals(Constants.USER_SMSTOOL_UNICEL)){

						}else if(userSMSTool.equals(Constants.USER_SMSTOOL_INFOBIP)){
							infobipIntegralStatusCode = Integer.parseInt(statusCode.trim());
						}

					}else if(tokenArr[0].equals(Constants.SMS_DLR_MSGID_TOKEN)) {

						msgID = tokenArr[1]; // 135060506451611118
						if(sentIdsStr.length() > 0) sentIdsStr += Constants.DELIMETER_COMMA;
						sentIdsStr += "'"+msgID+"'"; // 135060506451611118 will a custom value


                       if(msgID.equalsIgnoreCase("b")){
							sentIdsWithNoMsgId  += Constants.DELIMETER_COMMA;
					    }
					}


				}//for

				//	logger.info(">>>>>>>>>> msg_id="+msgID+"Status = "+statusCode);
				//updateSentDlrMap.put(msgID, statusCode);



				if(userSMSTool.equals(Constants.USER_SMSTOOL_UNICEL)){
					updateSentDlrMap.put(msgID, statusCode);
				}else if(userSMSTool.equals(Constants.USER_SMSTOOL_INFOBIP)){
					infobipUpdateSentDlrMap.put(msgID, infobipIntegralStatusCode);
				}

			}//for
		}catch(Exception e){
			logger.error("Exception >>>>>>>>>>>>>>> ",e);
		}
		if(sentIdsStr == null || sentIdsStr.isEmpty()) {
			logger.debug("No sent items found for msgIds== "+sentIdsStr);
			
			//TODO empty the dlrrecptset may be in synchronized block
			return;
		}
		
		SMSCampaignSentDao smsCampaignSentDao = (SMSCampaignSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_CAMPAIGNSENT_DAO);
		SMSCampaignSentDaoForDML smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_CAMPAIGNSENT_DAO_ForDML);

		if(smsCampaignSentDao == null ) {
			
			logger.debug("No sent dao found ");
			return;
		}
		
		List<SMSCampaignSent> sentList = smsCampaignSentDao.findByMsgIds(sentIdsStr);
		if(sentList == null ){
			//TODO
			logger.debug("No sent items found for msgIds== "+sentIdsStr);
			
			return;
		}
		List<SMSCampaignSent> listToBeUpdated = new ArrayList<SMSCampaignSent>();
		List<SMSBounces> smsBounceList = new ArrayList<SMSBounces>();
		List<String> contactMobilesList = new ArrayList<String>();
		List<SMSSuppressedContacts> suppressedContactsList = new ArrayList<SMSSuppressedContacts>(); 
		
		Long smsCrID = null;
		Users user  =  null;
		SMSCampaignReport smscr = null;
		
		for (SMSCampaignSent smsCampaignSent : sentList) {
			
			try {
				
				if(smsCampaignSent.getApiMsgId()  == null ) continue;
			/*	logger.debug("smsCampaignSent.getApiMsgId() ............:"+smsCampaignSent.getApiMsgId());
				logger.debug("updateSentDlrMap.get(smsCampaignSent.getApiMsgId())........:"+ updateSentDlrMap.get(smsCampaignSent.getApiMsgId()));
				logger.debug("UnicelSMSGateway.unicellDlrMap.get(updateSentDlrMap.get(smsCampaignSent.getApiMsgId()))....:"+ UnicelSMSGateway.unicellDlrMap.get(updateSentDlrMap.get(smsCampaignSent.getApiMsgId())));
			*/	
				
                String status = "";
				//String status = DlrMap.get(updateSentDlrMap.get(smsCampaignSent.getApiMsgId())+"");
				
				
				if(userSMSTool.equals(Constants.USER_SMSTOOL_UNICEL)){
					status = DlrMap.get(updateSentDlrMap.get(smsCampaignSent.getApiMsgId())+"");
				}else if(userSMSTool.equals(Constants.USER_SMSTOOL_INFOBIP)){
					status = InfobipDlrMap.get(infobipUpdateSentDlrMap.get(smsCampaignSent.getApiMsgId()));
				}
				
				
				logger.debug("status for "+smsCampaignSent.getApiMsgId() + " status  "+status);
			//	logger.debug("As this map contains status for Unicel we need to write code for InfoBip");
				
				smscr = smsCampaignSent.getSmsCampaignReport();
				
				if(smsCrID == null || !smscr.getSmsCrId().equals(smsCrID)){ 
					
					if(listToBeUpdated.size() > 0 && !smscr.getSmsCrId().equals(smsCrID)){
						updateDlrChunk(listToBeUpdated, smsBounceList, suppressedContactsList, contactMobilesList, user, smsCrID);
					}
					smsCrID = smscr.getSmsCrId();
					user  = smscr.getUser();
				
				}
				
				smsCampaignSent.setStatus(status);
				/*boolean isNonDelivered = true;
				for (String  dstate : deliveredStatusSet) {
					
					if(status.equalsIgnoreCase(dstate)) {
						isNonDelivered = false;
					}
				}*/
				if(!DeliveredSet.contains(status)) {
				//	logger.debug("Need to be implemented for UAE !UnicelSMSGateway.deliveredStatusSet.contains(status)");
					smsCampaignSent.setStatus(SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);
					SMSBounces newBounce= new SMSBounces();
					newBounce.setCrId(smsCampaignSent.getSmsCampaignReport().getSmsCrId());
					newBounce.setSentId(smsCampaignSent);
					newBounce.setMessage(status);
					newBounce.setMobile(smsCampaignSent.getMobileNumber());
					newBounce.setCategory(status);
					newBounce.setBouncedDate(Calendar.getInstance());
					smsBounceList.add(newBounce);
					
					if(SuppressedSet.contains(status)) {
						
						SMSSuppressedContacts suppressedContact = new SMSSuppressedContacts();
						suppressedContact.setUser(user);
						suppressedContact.setMobile(smsCampaignSent.getMobileNumber());
						suppressedContact.setType(status);
						suppressedContact.setReason(status);
						
						suppressedContact.setSuppressedtime(Calendar.getInstance());
						
						suppressedContactsList.add(suppressedContact);
						if(status.equals(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER)) {
							
							contactMobilesList.add(smsCampaignSent.getMobileNumber());
							
						}//if
						
					}//if
					
				}//if
				
					
				listToBeUpdated.add(smsCampaignSent);
			} catch (Exception e) {
                logger.error("Exception >>>>>>>>>>>>>>>>>>> ",e);
				throw new BaseServiceException("Exception in process dlrreceipts ");
				//TODO need to put all back to the process dlr receipt
				
			}
			
		}//for
		
		
		try {
			updateDlrChunk(listToBeUpdated, smsBounceList, suppressedContactsList, contactMobilesList, user, smsCrID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
            logger.error("Exception >>>>>>>>>>>>>>>>>>> ",e);
			throw new BaseServiceException("Exception in process dlrreceipts ");
		}
		//not required
		/*synchronized (dlrReciepts) {
			dlrReciepts.clear();
			
		}*/
		
		dlrReciepts.clear();
		
		/**
		 * Process without msg ids
		 */
		if(sentIdsWithNoMsgId != null ){
			processBounces(sentIdsWithNoMsgId,sentIdsStr);
		}
		
		logger.debug(">>>>>>> Completed SMSDlrReportUpdateThread :: processDlrReciepts <<<<<<< ");
	}
	
	private void processBounces(String sentIdsStr, String except) {
		SMSCampaignSentDao smsCampaignSentDao = null;
		SMSCampaignSentDaoForDML smsCampaignSentDaoForDML = null;
		try {
			smsCampaignSentDao = (SMSCampaignSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_CAMPAIGNSENT_DAO);
			smsCampaignSentDaoForDML  = (SMSCampaignSentDaoForDML )ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_CAMPAIGNSENT_DAO_ForDML );

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(smsCampaignSentDao == null ){
			return;
		}
		
		List<SMSCampaignSent> sentList = smsCampaignSentDao.findBySentAndMsgIds(sentIdsStr,except);
		if(sentList == null ){
			//TODO
			logger.debug("No sent items found for msgIds== "+sentIdsStr);

			return;
		}
		List<SMSCampaignSent> listToBeUpdated = new ArrayList<SMSCampaignSent>();
		List<SMSBounces> smsBounceList = new ArrayList<SMSBounces>();
		List<String> contactMobilesList = new ArrayList<String>();
		List<SMSSuppressedContacts> suppressedContactsList = new ArrayList<SMSSuppressedContacts>(); 

		Long smsCrID = null;
		Users user  =  null;
		SMSCampaignReport smscr = null;

		for (SMSCampaignSent smsCampaignSent : sentList) {

			try {

				if(!("".equals(smsCampaignSent.getApiMsgId()))){
					continue;
				}
				
				//logger.debug("status for "+smsCampaignSent.getApiMsgId() + " status  "+status);
				//	logger.debug("As this map contains status for Unicel we need to write code for InfoBip");

				smscr = smsCampaignSent.getSmsCampaignReport();

				if(smsCrID == null || !smscr.getSmsCrId().equals(smsCrID)){ 

					if(listToBeUpdated.size() > 0 && !smscr.getSmsCrId().equals(smsCrID)){
						updateDlrChunk(listToBeUpdated, smsBounceList, suppressedContactsList, contactMobilesList, user, smsCrID);
					}
					smsCrID = smscr.getSmsCrId();
					user  = smscr.getUser();

				}
				String status ="Invalid Destination Number";
				
				smsCampaignSent.setStatus(SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);
				SMSBounces newBounce= new SMSBounces();
				newBounce.setCrId(smsCampaignSent.getSmsCampaignReport().getSmsCrId());
				newBounce.setSentId(smsCampaignSent);
				newBounce.setMessage(status);
				newBounce.setMobile(smsCampaignSent.getMobileNumber());
				newBounce.setCategory(status);
				newBounce.setBouncedDate(Calendar.getInstance());
				smsBounceList.add(newBounce);
				
				
				SMSSuppressedContacts suppressedContact = new SMSSuppressedContacts();
				suppressedContact.setUser(user);
				suppressedContact.setMobile(smsCampaignSent.getMobileNumber());
				suppressedContact.setType(status);
				suppressedContact.setReason(status);

				suppressedContact.setSuppressedtime(Calendar.getInstance());

				suppressedContactsList.add(suppressedContact);
				contactMobilesList.add(smsCampaignSent.getMobileNumber());
				
				
				
				try {
					updateDlrChunk(listToBeUpdated, smsBounceList, suppressedContactsList, contactMobilesList, user, smsCrID);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new BaseServiceException("Exception in process dlrreceipts ");
				}


				listToBeUpdated.add(smsCampaignSent);
			} catch (Exception e) {
				//throw new BaseServiceException("Exception in process dlrreceipts ");
				//TODO need to put all back to the process dlr receipt

			}

		}//for
	}
	/**
	 * 
	 * @param listToBeUpdated
	 * @param smsBounceList
	 * @param suppressedContactsList
	 * @param contactMobilesList
	 * @param user
	 * @param smsCrId
	 * @throws Exception
	 */
	private void updateDlrChunk(List<SMSCampaignSent> listToBeUpdated, List<SMSBounces> smsBounceList,
			List<SMSSuppressedContacts> suppressedContactsList, List<String> contactMobilesList,
			Users user, Long smsCrId) throws Exception{
		logger.debug(">>>>>>> Started SMSDlrReportUpdateThread :: updateDlrChunk <<<<<<< ");
		SMSCampaignSentDao smsCampaignSentDao = null;
		SMSCampaignSentDaoForDML smsCampaignSentDaoForDML = null;
		SMSBouncesDao smsBouncesDao = null;
		SMSBouncesDaoForDML smsBouncesDaoForDML = null;
		SMSCampaignReportDao smsCampaignReportDao = null;
		SMSCampaignReportDaoForDML smsCampaignReportDaoForDML = null;
		SMSSuppressedContactsDao smsSuppressedContactsDao = null;
		ContactsDao contactsDao = null;
		ContactsDaoForDML contactsDaoForDML;
		try {
			ServiceLocator serviceLocator = ServiceLocator.getInstance();
			smsCampaignSentDao = (SMSCampaignSentDao)serviceLocator.getDAOByName(OCConstants.SMS_CAMPAIGNSENT_DAO);
			smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML)serviceLocator.getDAOForDMLByName(OCConstants.SMS_CAMPAIGNSENT_DAO_ForDML);
			smsBouncesDao = (SMSBouncesDao)serviceLocator.getDAOByName(OCConstants.SMSBOUNCES_DAO);
			smsBouncesDaoForDML = (SMSBouncesDaoForDML)serviceLocator.getDAOForDMLByName(OCConstants.SMSBOUNCES_DAO_ForDML);
			smsCampaignReportDao = (SMSCampaignReportDao)serviceLocator.getDAOByName(OCConstants.SMS_CAMPAIGNREPORT_DAO);
			smsCampaignReportDaoForDML = (SMSCampaignReportDaoForDML)serviceLocator.getDAOForDMLByName(OCConstants.SMS_CAMPAIGNREPORT_DAO_FOR_DML);
			smsSuppressedContactsDao = (SMSSuppressedContactsDao)serviceLocator.getDAOByName(OCConstants.SMS_SUPPRESSED_CONTACTS_DAO);
			contactsDao = (ContactsDao)serviceLocator.getDAOByName(OCConstants.CONTACTS_DAO);
			contactsDaoForDML = (ContactsDaoForDML)serviceLocator.getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception while getting the dao ", e);
			throw new Exception("dao not found");
		}
		
		if(listToBeUpdated.size() > 0) {
			logger.debug("=============entered into sentUpdate ==========="+listToBeUpdated.size());
			//smsCampaignSentDao.saveByCollection(listToBeUpdated);
			smsCampaignSentDaoForDML.saveByCollection(listToBeUpdated);
			listToBeUpdated.clear();
			
			if(smsBounceList.size() > 0) {
				logger.debug("=============entered into bounceUpdate ==========="+smsBounceList.size());
				//smsBouncesDao.saveByCollection(smsBounceList);
				smsBouncesDaoForDML.saveByCollection(smsBounceList);
				smsBounceList.clear();
			}
			
			
			if(suppressedContactsList.size() > 0) {
				SMSCampaignDeliveryReportsHandler handler = new SMSCampaignDeliveryReportsHandler();
				logger.debug("=============entered into suppressedUpdate ==========="+suppressedContactsList.size());
				handler.addToSuppressedContacts(user, suppressedContactsList);//;(user, suppressedContactsList);
				
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
					logger.debug("=============entered into contactUpdate ==========="+mobileStr);
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
		logger.debug(">>>>>>> Completed SMSDlrReportUpdateThread :: updateDlrChunk <<<<<<< ");
		
	}
	/**
	 * 
	 * @param updateStatusMap
	 * @return
	 * @throws BaseServiceException
	 */
	public  int processUpdateStatusMap( Map<Long, String> updateStatusMap) throws BaseServiceException{
		
		logger.debug(">>>>>>>>>>>> Started processUpdateStatusMap ............: "+updateStatusMap);
		if(updateStatusMap == null || updateStatusMap.size() <= 0) return 0;
		
		
		int updatedCount = 0;
		Set<Long> sentIdKeySet = updateStatusMap.keySet();
		String sentIdsStr = Constants.STRING_NILL;
		for (Long sentIdL : sentIdKeySet) {
			
			sentIdsStr += sentIdsStr.isEmpty() ? sentIdL.longValue()+sentIdsStr 
					: Constants.DELIMETER_COMMA+sentIdL.longValue();
			
		}
		//logger.info("sentIdsStrsentIdsStr ...."+sentIdsStr);
		SMSCampaignSentDao smsCampaignSentDao = null;
		SMSCampaignSentDaoForDML smsCampaignSentDaoForDML=null;

		try {
			smsCampaignSentDao = (SMSCampaignSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_CAMPAIGNSENT_DAO);
			smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_CAMPAIGNSENT_DAO_ForDML);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("no sms sent dao found ");
		}
		
		 logger.debug(" sentIdsStr "+sentIdsStr );
		List<SMSCampaignSent> retList = smsCampaignSentDao.findByIds(sentIdsStr);
		
		if(retList != null && retList.size() > 0 && retList.size() == sentIdKeySet.size()) {
			
			 logger.debug(" update msgids "+retList.size()  );
			
			List<SMSCampaignSent> listToBeUpdated = new ArrayList<SMSCampaignSent>();
			
			for (SMSCampaignSent smsCampaignSent : retList) {
				
				smsCampaignSent.setApiMsgId(updateStatusMap.get(smsCampaignSent.getSentId()));
				listToBeUpdated.add(smsCampaignSent);
				
			}
			
			if(listToBeUpdated.size() > 0) {
				
				updatedCount = listToBeUpdated.size();
				//smsCampaignSentDao.saveByCollection(listToBeUpdated);
				smsCampaignSentDaoForDML.saveByCollection(listToBeUpdated);
				listToBeUpdated.clear();
				updateStatusMap.clear();
				/*updateStatusMap.clear();
				smsCount = 0;*/
			}	
				       				
		}
		logger.debug(">>>>>>>>>>>> Completed processUpdateStatusMap ............: "+updateStatusMap);
		return updatedCount;
		
	}//processUpdateStatusMap
	
}
