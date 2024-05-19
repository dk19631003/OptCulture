package org.mq.optculture.restservice.loyalty;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.loyalty.LoyaltyEnrollJsonRequest;
import org.mq.optculture.model.loyalty.LoyaltyEnrollJsonResponse;
import org.mq.optculture.model.loyalty.LoyaltyEnrollRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyEnrollResponseObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.Gson;
import com.newrelic.api.agent.Trace;

public class AsyncLoyaltyEnrollRestservice extends AbstractController{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	/**
	 * Delegates request.
	 * Calls Async Loyalty Enrollment business service to process request.
	 */
	@Trace
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		logger.info("Started Async Loyalty Enroll Rest Service..."); 
		ContactsLoyaltyStage loyaltyStage = null;
		try{
			response.setContentType("application/json");
			String requestJson = OptCultureUtils.getParameterJsonValue(request);
			logger.info("JSON Request: = "+requestJson);
			Gson gson = new Gson();
			LoyaltyEnrollJsonRequest jsonRequest = null;
			try{
				jsonRequest = gson.fromJson(requestJson, LoyaltyEnrollJsonRequest.class);
			}catch(Exception e){
				String responseJson = "{\"ENROLLMENTRESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101001\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"STATUS\":\"Failure\"}}}";
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}
			
			
			if(jsonRequest == null){
				String responseJson = "{\"ENROLLMENTRESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101001\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"STATUS\":\"Failure\"}}}";
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(jsonRequest.getENROLLMENTREQ() == null){
				String responseJson = "{\"ENROLLMENTRESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101003\",\"MESSAGE\":\"Error 101003: Invalid request.\",\"STATUS\":\"Failure\"}}}";
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}
			if(jsonRequest.getENROLLMENTREQ().getHEADERINFO() == null){
				String responseJson = "{\"ENROLLMENTRESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"1004\",\"MESSAGE\":\"Error 1004: Invalid request.\",\"STATUS\":\"Failure\"}}}";
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}
			
			String pcFlag = jsonRequest.getENROLLMENTREQ().getHEADERINFO().getPCFLAG();
			String requestId = jsonRequest.getENROLLMENTREQ().getHEADERINFO().getREQUESTID();
			LoyaltyTransaction transaction = null;
			//transaction = findTransactionByRequestId(requestId);
			//if(pcFlag != null && pcFlag.equalsIgnoreCase("true")){
				transaction = findTransactionByRequestIdAndUserName(requestId,jsonRequest.getENROLLMENTREQ());
				if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED)){
					String responseJson = transaction.getJsonResponse();
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					logger.info("Response = "+responseJson);
					return null;
				}else if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW)){
					String responseJson = "{\"ENROLLMENTRESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101020\",\"MESSAGE\":\"Pending \"101020\": Request is being processed..\",\"STATUS\":\"Failure\"}}}";
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					logger.info("Response = "+responseJson);
					return null;
				}
			//}	
			
			//code to handle multiple enrolments for single customer due to timeout at pos or connection delay by sparkbase.
			loyaltyStage = findDuplicateRequest(jsonRequest.getENROLLMENTREQ());
			
			if(loyaltyStage != null){
				logger.info("Duplicate request....timed out request...");
				String responseJson = "{\"ENROLLMENTRESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101505\",\"MESSAGE\":\"Error 101505: Request is being processed.\",\"STATUS\":\"Failure\"}}}";
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}
			else{
				loyaltyStage = saveRequestInStageTable(jsonRequest.getENROLLMENTREQ());
				
			}
			
			//log transaction
			if(transaction == null){
				transaction = logTransactionRequest(jsonRequest.getENROLLMENTREQ(), requestJson, OCConstants.LOYALTY_ONLINE_MODE);
			}
			
			Date date = transaction.getRequestDate().getTime();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
			String transDate = df.format(date);
			
			
			BaseRequestObject requestObject = new BaseRequestObject();
			requestObject.setJsonValue(requestJson);
			requestObject.setAction(OCConstants.LOYALTY_SERVICE_ACTION_ENROLMENT);
			requestObject.setTransactionId(""+transaction.getId());
			requestObject.setTransactionDate(transDate);
			
			BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.ASYNC_LOYALTY_ENROLMENT_BUSINESS_SERVICE);
			
			BaseResponseObject responseObject = baseService.processRequest(requestObject);
			
			logger.info("JSON Response: = "+responseObject.getJsonValue());
			LoyaltyEnrollJsonResponse responseobject = gson.fromJson(responseObject.getJsonValue(), 
					LoyaltyEnrollJsonResponse.class);
			
			//changes w.r.t migration
			/*if(responseObject.getResponseObject() != null && 
					!OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equals(responseObject.getResponseObject().toString())) {
			*/	
				updateTransactionStatus(loyaltyStage, transaction, responseObject.getJsonValue(), responseobject.getENROLLMENTRESPONSE());
			//}
			
			//we can make use of this table?if yes we have to  stop deletion here 
			/*if(responseObject.getResponseObject() != null && 
					OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equals(responseObject.getResponseObject().toString())) {
				LoyaltyTransactionDao loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
				    transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
				    logger.debug("responseJson ======"+responseObject.getJsonValue());
				    transaction.setJsonResponse(responseObject.getJsonValue());
				    transaction.setCardNumber(responseobject.getENROLLMENTRESPONSE().getENROLLMENTINFO().getCARDNUMBER());
				    loyaltyTransactionDao.saveOrUpdate(transaction);
				if(loyaltyStage != null)
					deleteRequestFromStageTable(loyaltyStage);
			}*/
			
			if(responseObject.getAction().equals(OCConstants.LOYALTY_SERVICE_ACTION_ENROLMENT)){
				String responseJson = responseObject.getJsonValue();
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				logger.info("Completed Loyalty Enroll Rest Service.");
				return null;
			}
		}catch(Exception e){
			logger.error("Error in Enroll rest service", e);
			String responseJson = "{\"ENROLLMENTRESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101000\",\"MESSAGE\":\"Server error  101000.\",\"STATUS\":\"Failure\"}}}";
			PrintWriter printWriter = response.getWriter();
			printWriter.write(responseJson);
			printWriter.flush();
			printWriter.close();
			logger.info("Response = "+responseJson);
			if(loyaltyStage != null)
				deleteRequestFromStageTable(loyaltyStage);
			return null;
		}/*finally{
			
			deleteRequestFromStageTable(loyaltyStage);
			
			logger.info("Completed Loyalty Enroll Rest Service.");
		}*/
		logger.info("Completed Loyalty Enroll Rest Service.");
		return null;
	}
	
		public LoyaltyTransaction findTransactionByRequestIdAndUserName(String requestId,LoyaltyEnrollRequestObject requestObject){
			LoyaltyTransaction transaction = null;
			String userDetail = requestObject.getUSERDETAILS().getUSERNAME()+Constants.USER_DETAIL_SEPARATOR +requestObject.getUSERDETAILS().getORGANISATION();
			LoyaltyTransactionDao loyaltyTransactionDao = null;
			try {
				loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
				transaction = loyaltyTransactionDao.findByRequestId(requestId, userDetail,OCConstants.LOYALTY_TRANSACTION_ENROLMENT);
			}catch(Exception e){
				logger.error("Exception in find transaction by requestid", e);
			}
			return transaction;
		}
		
	public LoyaltyTransaction logTransactionRequest(LoyaltyEnrollRequestObject requestObject, String jsonRequest, String mode){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		LoyaltyTransaction transaction = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			
			transaction = new LoyaltyTransaction();
			transaction.setJsonRequest(jsonRequest);
			transaction.setStoreNumber(requestObject.getHEADERINFO().getSTORENUMBER());
			transaction.setRequestId(requestObject.getHEADERINFO().getREQUESTID());
			transaction.setPcFlag(Boolean.valueOf(requestObject.getHEADERINFO().getPCFLAG()));
			transaction.setMode(mode);//online or offline
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_ENROLMENT);
			transaction.setUserDetail(requestObject.getUSERDETAILS().getUSERNAME()+"__"+requestObject.getUSERDETAILS().getORGANISATION());
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
			
		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
		}
		return transaction;
	}
	
	public void updateTransactionStatus(ContactsLoyaltyStage ltyStage, LoyaltyTransaction transaction, String responseJson, LoyaltyEnrollResponseObject response){
		try {
			AsyncLoyaltyTrxDao asyncLoyaltyTrxDao = null;
			AsyncLoyaltyTrxDaoForDML asyncLoyaltyTrxDaoForDML = null;
			  AsyncLoyaltyTrx asyncLoyaltyTrx = null;
			  LoyaltyTransactionDao loyaltyTransactionDao = null;
			  LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
			  loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			  loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			 
			  transaction = findTransaction(transaction.getId());
			  if(transaction.getLoyaltyServiceType() != null && OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equals(transaction.getLoyaltyServiceType())) {
				  
				  transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
				  logger.debug("responseJson ======"+responseJson);
				    transaction.setJsonResponse(responseJson);
				    transaction.setCardNumber(response.getENROLLMENTINFO().getCARDNUMBER());
				//    loyaltyTransactionDao.saveOrUpdate(transaction);
				    loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
				    if(ltyStage != null)
					deleteRequestFromStageTable(ltyStage);
				  
			  }else{
				  if(response.getSTATUS().getERRORCODE()!= null  && response.getSTATUS().getERRORCODE().equalsIgnoreCase("0")){
					    asyncLoyaltyTrxDao    = (AsyncLoyaltyTrxDao) ServiceLocator.getInstance().getDAOByName(OCConstants.ASYNC_LOYALTY_TRX_DAO);
					    asyncLoyaltyTrxDaoForDML    = (AsyncLoyaltyTrxDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.ASYNC_LOYALTY_TRX_DAO_FOR_DML);
					    asyncLoyaltyTrx = new AsyncLoyaltyTrx();
					    asyncLoyaltyTrx.setCreatedTime(MyCalendar.getInstance());
					    asyncLoyaltyTrx.setLoyaltyTransaction(transaction);
					    asyncLoyaltyTrx.setStatus(OCConstants.ASQ_STATUS_NEW);
					    asyncLoyaltyTrx.setTrxType(OCConstants.LOYALTY_ENROLLMENT);
					    //asyncLoyaltyTrxDao.saveOrUpdate(asyncLoyaltyTrx);
					    asyncLoyaltyTrxDaoForDML.saveOrUpdate(asyncLoyaltyTrx);
					    ltyStage.setTrxId(transaction.getId());
					    ltyStage.setCardNumber(response.getENROLLMENTINFO().getCARDNUMBER());
					    ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
					    ContactsLoyaltyStageDaoForDML contactsLoyaltyStageDaoForDML = (ContactsLoyaltyStageDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO_FOR_DML);
					    contactsLoyaltyStageDaoForDML.saveOrUpdate(ltyStage);
				   }else{
				    
				    transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
				    logger.debug("responseJson ======"+responseJson);
				    transaction.setJsonResponse(responseJson);
				    transaction.setCardNumber(response.getENROLLMENTINFO().getCARDNUMBER());
				    //loyaltyTransactionDao.saveOrUpdate(transaction);
				    loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
				    
				  //we can make use of this table?if yes we have to  stop deletion here 
					if(ltyStage != null)
						deleteRequestFromStageTable(ltyStage);

				    
				   }
			  }
			  
			
		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}
	public LoyaltyTransaction findTransaction(Long trxID){
		LoyaltyTransaction transaction = null;
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			transaction = loyaltyTransactionDao.findById(trxID);
		}catch(Exception e){
			logger.error("Exception in find transaction by requestid", e);
		}
		return transaction;
	}
	private ContactsLoyaltyStage findDuplicateRequest(LoyaltyEnrollRequestObject requestObject) {
		//find the request in stage
		ContactsLoyaltyStage loyaltyStage = null;
		
		try{
			
			ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
			String custId = requestObject.getCUSTOMERINFO().getCUSTOMERID() == null ? "" : requestObject.getCUSTOMERINFO().getCUSTOMERID().trim(); 
			String email = requestObject.getCUSTOMERINFO().getEMAIL() == null ? "" : requestObject.getCUSTOMERINFO().getEMAIL().trim();
			String phone = requestObject.getCUSTOMERINFO().getPHONE() == null ? "" : requestObject.getCUSTOMERINFO().getPHONE().trim(); 		
			String card = requestObject.getENROLLMENTINFO().getCARDNUMBER() == null ? "" : requestObject.getENROLLMENTINFO().getCARDNUMBER().trim(); 
			String userName = requestObject.getUSERDETAILS().getUSERNAME()+Constants.USER_AND_ORG_SEPARATOR +requestObject.getUSERDETAILS().getORGANISATION();  
					
			ContactsLoyaltyStage requestStage = contactsLoyaltyStageDao.findRequest(custId, email, phone, card, userName,
					OCConstants.LOYALTY_SERVICE_TYPE_SB, OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);
			if(requestStage != null){
				return loyaltyStage;
			}
			
		}catch(Exception e){
			logger.error("Exception in finding loyalty duplicate request...");
		}
		return loyaltyStage;
	}
	
	private ContactsLoyaltyStage saveRequestInStageTable(LoyaltyEnrollRequestObject requestObject){

		ContactsLoyaltyStage loyaltyStage = null;
		try{
			
			ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
			ContactsLoyaltyStageDaoForDML contactsLoyaltyStageDaoForDML = (ContactsLoyaltyStageDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO_FOR_DML);
			String custId = requestObject.getCUSTOMERINFO().getCUSTOMERID() == null ? "" : requestObject.getCUSTOMERINFO().getCUSTOMERID().trim(); 
			String email = requestObject.getCUSTOMERINFO().getEMAIL() == null ? "" : requestObject.getCUSTOMERINFO().getEMAIL().trim();
			String phone = requestObject.getCUSTOMERINFO().getPHONE() == null ? "" : requestObject.getCUSTOMERINFO().getPHONE().trim(); 		
			String card = requestObject.getENROLLMENTINFO().getCARDNUMBER() == null ? "" : requestObject.getENROLLMENTINFO().getCARDNUMBER().trim(); 
			String userName = requestObject.getUSERDETAILS().getUSERNAME()+Constants.USER_AND_ORG_SEPARATOR +requestObject.getUSERDETAILS().getORGANISATION();  
					
			logger.info("saving request in stage table...");
			loyaltyStage = new ContactsLoyaltyStage();
			loyaltyStage.setCustomerId(custId);
			loyaltyStage.setEmailId(email);
			loyaltyStage.setPhoneNumber(phone);
			loyaltyStage.setUserName(userName);
			loyaltyStage.setReqType(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);
			loyaltyStage.setServiceType(OCConstants.LOYALTY_SERVICE_TYPE_SB);
			loyaltyStage.setStatus(Constants.LOYALTY_STAGE_PENDING);
			
			contactsLoyaltyStageDaoForDML.saveOrUpdate(loyaltyStage);
		}catch(Exception e){
			logger.error("Exception while saving loyalty request in stage table...", e);
		}
		return loyaltyStage;
		
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
