package org.mq.marketer.campaign.controller.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.DRSMSSent;
import org.mq.marketer.campaign.dao.DRSMSSentDao;
import org.mq.marketer.campaign.dao.DRSMSSentDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class SMSDlrReportUpdateThread extends Thread{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER); 
	public   Map<Long, String> updateStatusMap ;
	public  Map<Long, String> getUpdateStatusMap() {
		
		return updateStatusMap;
	}
	public void setUpdateStatusMap(Map<Long, String> updateStatusMap) {
		this.updateStatusMap = updateStatusMap;
	}

	private   Set<String> CMDlrReciepts; ;
	public Set<String> getCMDlrReciepts() {
		return CMDlrReciepts;
	}
	public void setCMDlrReciepts(Set<String> cMDlrReciepts) {
		CMDlrReciepts = cMDlrReciepts;
	}

	
	public SMSDlrReportUpdateThread() {
		// TODO Auto-generated constructor stub
	}
	
	public SMSDlrReportUpdateThread(Set<String> CMDlrReciepts, Map<Long, String> updateStatusMap) {
		this.CMDlrReciepts = CMDlrReciepts;
		this.updateStatusMap = updateStatusMap;
	}
	
	public void run() {
		
	logger.debug(">>>>>>> Started SMSDlrReportUpdateThread :: run <<<<<<< ");
		
		try {
			processUpdateStatusMap(this.getUpdateStatusMap());
			processDlrReciepts(this.getCMDlrReciepts(), Constants.USER_SMSTOOL_CM);
		} catch (BaseServiceException e) {
			logger.error("Exception while processing Status map / dlrreceipts ", e);
		} catch (Exception e) {
			logger.error("Exception while processing Status map / dlrreceipts ", e);
		}
		
	logger.debug(">>>>>>> Completed SMSDlrReportUpdateThread :: run <<<<<<< ");
		
		
	}
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
		DRSMSSentDao DRSMSSentDao = null;
		DRSMSSentDaoForDML drSMSSentDaoForDML=null;
		try {
			DRSMSSentDao = (DRSMSSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DR_SMS_SENT_DAO);
			drSMSSentDaoForDML = (DRSMSSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.DR_SMS_SENT_DAO_ForDML);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
		}

		
		 logger.debug(" sentIdsStr "+sentIdsStr );
		List<DRSMSSent> retList = DRSMSSentDao.findByIds(sentIdsStr);
		
		if(retList != null && retList.size() > 0 ) {
			
			 logger.debug(" update msgids "+retList.size()  );
			
			List<DRSMSSent> listToBeUpdated = new ArrayList<DRSMSSent>();
			
			for (DRSMSSent smsCampaignSent : retList) {
				
				smsCampaignSent.setMessageId(updateStatusMap.get(smsCampaignSent.getId()));
				listToBeUpdated.add(smsCampaignSent);
				
			}
			
			if(listToBeUpdated.size() > 0) {
				
				updatedCount = listToBeUpdated.size();
				//smsCampaignSentDao.saveByCollection(listToBeUpdated);
				try{
					drSMSSentDaoForDML.saveByCollection(listToBeUpdated);
					}catch(Exception e){
						logger.error("error in saveByCollection");
					}
				listToBeUpdated.clear();
				updateStatusMap.clear();
				/*updateStatusMap.clear();
				smsCount = 0;*/
			}	
				       				
		}
		logger.debug(">>>>>>>>>>>> Completed processUpdateStatusMap ............: "+updateStatusMap);
		return updatedCount;
		
	}//processUpdateStatusMap
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
		if(userSMSTool.equals(Constants.USER_SMSTOOL_CM) ) {
			
			DlrMap = CMComSMSGateway.CMComDlrMap;
			DeliveredSet = CMComSMSGateway.deliveredStatusSet;
			
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

					String[] tokenArr = token.split(Constants.DELIMETER_COLON+"");

					if(tokenArr[0].equals(Constants.SMS_DLR_STATUS_TOKEN)) {
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



				if(userSMSTool.equals(Constants.USER_SMSTOOL_CM)){
					updateSentDlrMap.put(msgID, statusCode);
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
		
		DRSMSSentDao DRSMSSentDao = (DRSMSSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DR_SMS_SENT_DAO);
		DRSMSSentDaoForDML drSMSSentDaoForDML = (DRSMSSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.DR_SMS_SENT_DAO_ForDML);

		if(DRSMSSentDao == null ) {
			
			logger.debug("No sent dao found ");
			return;
		}
		

		List<DRSMSSent> sentList = DRSMSSentDao.findByMsgIds(sentIdsStr);
		List<DRSMSSent> listToBeUpdated = new ArrayList<DRSMSSent>();
		if(sentList == null ){
			//TODO
			logger.debug("No sent items found for msgIds== "+sentIdsStr);
			
			return;
		}
		
		
		for (DRSMSSent DRSMSSent : sentList) {
			
			try {
				
				if(DRSMSSent.getMessageId()  == null ) continue;
				
				try{
					String status =  DlrMap.get(updateSentDlrMap.get(DRSMSSent.getMessageId() )+"");
					if(!DeliveredSet.contains(status)) {
						DRSMSSent.setStatus(SMSStatusCodes.EQUENCE_STATUS_BOUNCED);	
					}else{
						DRSMSSent.setStatus(SMSStatusCodes.EQUENCE_STATUS_DELIVERED);
					}
						
				}catch(Exception e){
					logger.error("Exception ",e);
				}
				listToBeUpdated.add(DRSMSSent);
			} catch (Exception e) {
                logger.error("Exception >>>>>>>>>>>>>>>>>>> ",e);
				throw new BaseServiceException("Exception in process dlrreceipts ");
				//TODO need to put all back to the process dlr receipt
				
			}
			if(listToBeUpdated.size()>200) {
				
				drSMSSentDaoForDML.saveByCollection(listToBeUpdated);
				listToBeUpdated.clear();
				
			}
			
		}//for
		if(listToBeUpdated.size()>0) {
			
			drSMSSentDaoForDML.saveByCollection(listToBeUpdated);
			listToBeUpdated.clear();
			
		}
		
		dlrReciepts.clear();
		
		
		
		logger.debug(">>>>>>> Completed SMSDlrReportUpdateThread :: processDlrReciepts <<<<<<< ");
	}
	
	}
