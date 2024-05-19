package org.mq.optculture.restservice.updatecontacts;

import java.io.PrintWriter;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.ocloyalty.LoyaltyInquiryRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyInquiryResponse;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.updatecontacts.ContactRequest;
import org.mq.optculture.model.updatecontacts.ContactResponse;
import org.mq.optculture.model.updatecontacts.Header;
import org.mq.optculture.model.updatecontacts.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.Gson;

public class UpdateContactsService extends AbstractController{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		BaseResponseObject baseResponseObject=null;
		BaseRequestObject baseRequestObject=null;
		BaseService baseService=null;
		try {
			logger.info("update contact request calling >>>> : ");
			String jsonValue=OptCultureUtils.getParameterJsonValue(request);
			Gson gson = new Gson();
			ContactRequest contactRequest = null;
			Status status = null;
			String responseJson = "";
			ContactResponse contactResponse = null;
			
			Header responseHeader = new Header();
			responseHeader.setRequestDate("");
			responseHeader.setRequestId("");
			
			try{
				contactRequest = gson.fromJson(jsonValue, ContactRequest.class);
			}catch(Exception e){
				status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				contactResponse = prepareFinalResponse(responseHeader, status, contactRequest);
				responseJson = gson.toJson(contactResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				//updateTransactionStatus(transaction, responseJson, response);(tranParent, inquiryResponse, null);
				logger.info("Response = "+responseJson);
				logger.error("Exception in parsing request json ...",e);
				return null;
			}
			
			String userDetail = contactRequest.getUser().getUserName() + Constants.USER_AND_ORG_SEPARATOR +
					contactRequest.getUser().getOrganizationId();
			
			
			
			LoyaltyTransaction transaction = null;
			//String pcFlag = inquiryRequest.getHeader().getPcFlag();
			//if(pcFlag != null && pcFlagrtbj.equalsIgnoreCase("true")){
			transaction = findTransactionBy(userDetail,contactRequest.getHeader().getRequestId(), null);
			
			if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED)){
				responseJson = transaction.getJsonResponse();
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}else if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW)){
				contactResponse = prepareFinalResponse(responseHeader, status, contactRequest);
				responseJson = gson.toJson(contactResponse);
				
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}
			//}
			if(transaction == null){
				transaction = logTransactionRequest(contactRequest, jsonValue, "online");
			}
			
			
			
			baseRequestObject = new BaseRequestObject();
			baseRequestObject.setJsonValue(jsonValue);
			baseRequestObject.setAction(OCConstants.UPDATE_CONTACTS_SERVICE_REQUEST);
			baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.UPDATE_CONTACTS_BUSINESS_SERVICE);
			logger.info("baseRequestObject >>>> : "+baseRequestObject.getJsonValue());
			baseResponseObject =baseService.processRequest(baseRequestObject);
			
			contactResponse = gson.fromJson(baseResponseObject.getJsonValue(), ContactResponse.class);
			updateTransactionStatus(transaction, baseResponseObject.getJsonValue(),
					contactResponse);

		} catch (Exception e) {
			logger.error("Exception ::" +e.getStackTrace());
		}
		finally {
			try {
				if(baseResponseObject != null) {
					logger.info("baseResponseObject >>>> : "+baseResponseObject.getJsonValue());
				}else {
					logger.info("baseResponseObject is  : "+baseResponseObject);
				}
				response.setContentType("application/json");
				PrintWriter pw = response.getWriter();
				pw.write(baseResponseObject.getJsonValue());
				pw.flush();
				pw.close();
			}catch (Exception e) {
				logger.error("Exception ::" +e.getStackTrace());
			}
		}
		return null;
	}
	
	
	
	private LoyaltyTransaction logTransactionRequest(ContactRequest contactRequest, String jsonRequest, String mode){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		LoyaltyTransaction transaction = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction = new LoyaltyTransaction();
			transaction.setJsonRequest(jsonRequest);
			transaction.setRequestId(contactRequest.getHeader().getRequestId());
			//transaction.setPcFlag(Boolean.valueOf(contactRequest.getHeader().getPcFlag()));
			transaction.setMode(mode);//online or offline
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setType(OCConstants.TRANSACTION_ADD_UPDATE_CONTACT);
			transaction.setServiceType(OCConstants.LOYALTY_SERVICE_TYPE_OC);
			transaction.setUserDetail(contactRequest.getUser().getUserName()+"__"+contactRequest.getUser().getOrganizationId());
			/*if(requestObject.getHeader().getDocSID() != null && !contactRequest.getHeader().getDocSID().trim().isEmpty()){
				transaction.setDocSID(requestObject.getHeader().getDocSID().trim());
			}*/
			//transaction.setStoreNumber(contactRequest.getHeader().getStoreNumber().trim());
			//transaction.setEmployeeId(requestObject.getHeader().getEmployeeId()!=null && !requestObject.getHeader().getEmployeeId().trim().isEmpty() ? requestObject.getHeader().getEmployeeId().trim():null);
		//	transaction.setTerminalId(requestObject.getHeader().getTerminalId()!=null && !requestObject.getHeader().getTerminalId().trim().isEmpty() ? requestObject.getHeader().getTerminalId().trim():null);
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
		}
		return transaction;
	}
	
	
	
	
	private ContactResponse prepareFinalResponse(Header header, Status status, ContactRequest contactRequest)
			throws BaseServiceException {
		logger.debug("-------entered prepareFinalResponse---------");
		ContactResponse contactResponse = new ContactResponse();
		header = new Header();
		if (contactRequest != null && contactRequest.getHeader() != null) {
			header.setRequestId(contactRequest.getHeader().getRequestId());
			header.setRequestDate(contactRequest.getHeader().getRequestDate());
		}
		contactResponse.setHeader(header);
		contactResponse.setStatus(status);
		logger.debug("-------exit  prepareFinalResponse---------");
		return contactResponse;
	}// prepareFinalResponse
	
	public LoyaltyTransaction findTransactionBy(String userDetail, String requestId, String docSId){
		LoyaltyTransaction transaction = null;
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			//transaction = loyaltyTransactionDao.findByRequestIdAndType(userDetail, requestId, docSId, OCConstants.LOYALTY_TRANSACTION_ISSUANCE);
			transaction = loyaltyTransactionDao.findByRequestId( requestId, userDetail, OCConstants.TRANSACTION_ADD_UPDATE_CONTACT);
		}catch(Exception e){
			logger.error("Exception in find transaction by requestid", e);
		}
		return transaction;
	}
	
	
	private void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, ContactResponse response){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			transaction.setRequestStatus(response.getStatus().getStatus());
			transaction.setJsonResponse(responseJson);
			if(response.getMembership() != null && response.getMembership().getCardNumber() != null 
					&& !response.getMembership().getCardNumber().trim().isEmpty()){
				transaction.setCardNumber(response.getMembership().getCardNumber());
			}
			else{
				transaction.setCardNumber(response.getMembership() == null ? "" : response.getMembership().getPhoneNumber());
			}
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}
	
	
}