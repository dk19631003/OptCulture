package org.mq.optculture.timer;

import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.AsyncLoyaltyTrx;
import org.mq.marketer.campaign.beans.ContactsLoyaltyStage;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.AsyncLoyaltyTrxDao;
import org.mq.marketer.campaign.dao.AsyncLoyaltyTrxDaoForDML;
import org.mq.marketer.campaign.dao.ContactsLoyaltyStageDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyStageDaoForDML;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.business.loyalty.AsyncLoyaltyEnrollmentServiceImpl;
import org.mq.optculture.business.loyalty.AsyncLoyaltyIssuanceServiceImpl;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.loyalty.LoyaltyEnrollJsonResponse;
import org.mq.optculture.model.loyalty.LoyaltyEnrollResponseObject;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceJsonResponse;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceResponseObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;
/**
 * 
 * @author proumyaa
 *	Loyalty transaction asynchronus timer   
 */
public class AsyncLoyaltyTrxTimer extends TimerTask{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	public void run() {
		
		//look for enrollements
		
		
		try {
			ProcessEnrollmentRequests() ;
		} catch (Exception e1) {
			logger.info(" Exception while processing enrollment requests :: ",e1);

		}

		
		//look for issuances
		try {
			AsyncLoyaltyTrxDao asyncLoyaltyTrxDao = (AsyncLoyaltyTrxDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ASYNC_LOYALTY_TRX_DAO);
			AsyncLoyaltyTrxDaoForDML asyncLoyaltyTrxDaoForDML = (AsyncLoyaltyTrxDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.ASYNC_LOYALTY_TRX_DAO_FOR_DML);
			List<AsyncLoyaltyTrx> asyncLoyaltyList = asyncLoyaltyTrxDao.getNewByStatus( OCConstants.LOYALTY_ISSUANCE, OCConstants.ASQ_STATUS_NEW);
			
			LoyaltyIssuanceResponseObject issuanceResponseObject = null;
			AsyncLoyaltyIssuanceServiceImpl baseService = (AsyncLoyaltyIssuanceServiceImpl) ServiceLocator.getInstance().getServiceByName(OCConstants.ASYNC_LOYALTY_ISSUANCE_BUSINESS_SERVICE);
			
			for(AsyncLoyaltyTrx asyncLoyaltyTrx : asyncLoyaltyList){
				try{
			 issuanceResponseObject = baseService.processNewIssuanceRequest(asyncLoyaltyTrx);
			 
			 LoyaltyIssuanceJsonResponse jsonResponseObject = new LoyaltyIssuanceJsonResponse();
				jsonResponseObject.setLOYALTYISSUANCERESPONSE(issuanceResponseObject);

				//Convert Object to JSON string
				Gson gson = new Gson();
				String responseJson = gson.toJson(jsonResponseObject);
				BaseResponseObject responseObject = new BaseResponseObject();
				//responseObject.setAction(serviceRequest);
				responseObject.setJsonValue(responseJson);
			 updateTransactionStatus(asyncLoyaltyTrx.getLoyaltyTransaction(), responseObject.getJsonValue(), issuanceResponseObject);
			 asyncLoyaltyTrx.setProcessedTime(MyCalendar.getInstance());
			 if(issuanceResponseObject != null && issuanceResponseObject.getSTATUS().getERRORCODE().equalsIgnoreCase("0")){
				 asyncLoyaltyTrx.setStatus(OCConstants.ASQ_STATUS_PROCESSED);
			 }else {
				 asyncLoyaltyTrx.setStatus(OCConstants.ASQ_STATUS_FAILURE);
				 if(issuanceResponseObject != null )asyncLoyaltyTrx.setStatusCode(issuanceResponseObject.getSTATUS().getERRORCODE()+":"+issuanceResponseObject.getSTATUS().getMESSAGE());
				 
				 
				 
			 }
				}catch(Exception e){
					logger.info("Exception while Issuance updation ::", e);
				}
			 //asyncLoyaltyTrxDao.saveOrUpdate(asyncLoyaltyTrx);
			 asyncLoyaltyTrxDaoForDML.saveOrUpdate(asyncLoyaltyTrx);
			 
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception, ", e);
		}	
		
		
		
		
	}//run
	
	
private void ProcessEnrollmentRequests() {
		
		try {
			AsyncLoyaltyTrxDao asyncLoyaltyTrxDao = (AsyncLoyaltyTrxDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ASYNC_LOYALTY_TRX_DAO);
			AsyncLoyaltyTrxDaoForDML asyncLoyaltyTrxDaoForDML = (AsyncLoyaltyTrxDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.ASYNC_LOYALTY_TRX_DAO_FOR_DML);

			List<AsyncLoyaltyTrx> asyncLoyaltyList = asyncLoyaltyTrxDao.getNewByStatus( OCConstants.LOYALTY_ENROLLMENT, OCConstants.ASQ_STATUS_NEW);
			
			if(asyncLoyaltyList == null || asyncLoyaltyList.isEmpty()) return;
			
			LoyaltyEnrollResponseObject enrollResponseObject = null;
			AsyncLoyaltyEnrollmentServiceImpl asyncLoyaltyEnrollmentServiceImpl = (AsyncLoyaltyEnrollmentServiceImpl)ServiceLocator.getInstance()
					.getBeanByName(OCConstants.ASYNC_LOYALTY_ENROLMENT_BUSINESS_SERVICE);
			
			for (AsyncLoyaltyTrx asyncLoyaltyTrx : asyncLoyaltyList) {
				
				try {
					enrollResponseObject = asyncLoyaltyEnrollmentServiceImpl.processNewEnrolmentRequest(asyncLoyaltyTrx);
					 LoyaltyEnrollJsonResponse jsonResponseObject = new LoyaltyEnrollJsonResponse();
					   jsonResponseObject.setENROLLMENTRESPONSE(enrollResponseObject);

					//Convert Object to JSON string
					Gson gson = new Gson();
					String responseJson = gson.toJson(jsonResponseObject);
					BaseResponseObject responseObject = new BaseResponseObject();
					//responseObject.setAction(serviceRequest);
					responseObject.setJsonValue(responseJson);
					updateTransactionStatus(asyncLoyaltyTrx.getLoyaltyTransaction(), responseObject.getJsonValue(), enrollResponseObject);
					asyncLoyaltyTrx.setProcessedTime(MyCalendar.getInstance());
					
					
					if(enrollResponseObject != null && enrollResponseObject.getSTATUS().getERRORCODE().equalsIgnoreCase("0")){
						asyncLoyaltyTrx.setStatus(OCConstants.ASQ_STATUS_PROCESSED);
					}else {
						asyncLoyaltyTrx.setStatus(OCConstants.ASQ_STATUS_FAILURE);
						if(enrollResponseObject != null )asyncLoyaltyTrx.setStatusCode(enrollResponseObject.getSTATUS().getERRORCODE()+":"+enrollResponseObject.getSTATUS().getMESSAGE());
					}
					ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
				     ContactsLoyaltyStage ltyStage = contactsLoyaltyStageDao.findByTid(asyncLoyaltyTrx.getLoyaltyTransaction().getId());
				     if(ltyStage != null)deleteRequestFromStageTable(ltyStage);
					//asyncLoyaltyTrxDao.saveOrUpdate(asyncLoyaltyTrx);
					asyncLoyaltyTrxDaoForDML.saveOrUpdate(asyncLoyaltyTrx);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception in procesing asynctrx, ", e);
				}
			}//for
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception, ", e);
		}
		
	}
	
	private void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, BaseResponseObject responseObject ) {
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try{
			
			
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			String cardNumber = null;
			if(responseObject != null && responseObject instanceof LoyaltyIssuanceResponseObject) {
				
				LoyaltyIssuanceResponseObject issuresponseObject = (LoyaltyIssuanceResponseObject)responseObject;
				cardNumber = issuresponseObject.getISSUANCEINFO().getCARDNUMBER();
			}else if(responseObject != null && responseObject instanceof LoyaltyEnrollResponseObject) {
				
				LoyaltyEnrollResponseObject enrollResponseObject = (LoyaltyEnrollResponseObject)responseObject;
				cardNumber = enrollResponseObject.getENROLLMENTINFO().getCARDNUMBER();
			}
					
			transaction.setJsonResponse(responseJson );
			transaction.setCardNumber(cardNumber);
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		
		}catch(Exception e){
			logger.info("Exception while updating issuance transaction ::"+ e);
		}
	}
	
	private void deleteRequestFromStageTable(ContactsLoyaltyStage loyaltyStage) {
	  
	  try{
	   
	   ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
	   ContactsLoyaltyStageDaoForDML contactsLoyaltyStageDaoForDML = (ContactsLoyaltyStageDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO_FOR_DML);
	   logger.info("deleting loyalty stage record...");
	   //contactsLoyaltyStageDao.delete(loyaltyStage);
	   contactsLoyaltyStageDaoForDML.delete(loyaltyStage);
	   
	  }catch(Exception e){
	   logger.error("Exception in while deleting request record from staging table...", e);
	  }
	  
	 }
	
}
