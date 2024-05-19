package org.mq.optculture.restservice.couponcodes;

import java.io.PrintWriter;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.PromoTrxLog;
import org.mq.marketer.campaign.dao.PromoTrxLogDao;
import org.mq.marketer.campaign.dao.PromoTrxLogDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.couponcodes.CouponCodeRedeemReq;
import org.mq.optculture.model.couponcodes.CouponCodeRedeemedObj;
import org.mq.optculture.model.couponcodes.CouponCodeRedeemedResponse;
import org.mq.optculture.model.loyalty.LoyaltyEnrollJsonRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollRequest;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.Gson;

public class CouponCodeRedeemedDataService extends AbstractController {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response){
		
		response.setContentType("application/json");
		BaseRequestObject baseRequestObject=null;
		BaseResponseObject baseResponseObject=null;
		BaseService baseService=null;
		CouponCodeRedeemedObj jsonRequest = null;
		Gson gson = new Gson();
		try {
			String jsonValue = OptCultureUtils.getParameterJsonValue(request);
			
			
			try{
				jsonRequest = gson.fromJson(jsonValue, CouponCodeRedeemedObj.class);
			}catch(Exception e){
				String responseJson = "{\"ENROLLMENTRESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101001\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"STATUS\":\"Failure\"}}}";
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}
			
			logTransactionRequest(jsonRequest.getCOUPONCODEREDEEMREQ(), jsonValue, OCConstants.LOYALTY_ONLINE_MODE);
			
			baseRequestObject=new BaseRequestObject();
			baseRequestObject.setAction(OCConstants.COUPON_CODE_REDEEMED_REQUEST);
			baseRequestObject.setJsonValue(jsonValue);
			baseService=ServiceLocator.getInstance().getServiceByName(OCConstants.COUPON_CODE_REDEEMED_BUSINESS_SERVICE);
			logger.info("baseRequestObject >>>> : "+baseRequestObject.getJsonValue());
			baseResponseObject=baseService.processRequest(baseRequestObject);
			
			
			
		}catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		finally {
			try {
				if(baseResponseObject != null) {
					logger.info("baseResponseObject >>>> : "+baseResponseObject.getJsonValue());
				}else {
					logger.info("baseResponseObject >>>> : "+baseResponseObject);
				}
				PrintWriter pw = response.getWriter();
				pw.write(baseResponseObject.getJsonValue());
				pw.flush();
				pw.close();
			}catch (Exception e) {
				logger.error("Exception ::" , e);
			}
		}
		return null;
	}

	public PromoTrxLog logTransactionRequest(CouponCodeRedeemReq requestObject, String jsonRequest, String mode){
		PromoTrxLogDao PromoTrxLogDao = null;
		PromoTrxLogDaoForDML PromoTrxLogDaoForDML = null;

		PromoTrxLog transaction = null;
		try {
			PromoTrxLogDao = (PromoTrxLogDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROMOTRXLOG_DAO);
			PromoTrxLogDaoForDML = (PromoTrxLogDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_PROMOTRXLOG_DAO_FOR_DML);
			transaction = new PromoTrxLog();
			transaction.setJsonRequest(jsonRequest);
			transaction.setRequestId(requestObject.getHEADERINFO().getREQUESTID());
			transaction.setPcFlag(Boolean.valueOf(requestObject.getHEADERINFO().getPCFLAG()));
			transaction.setMode(mode);//online or offline
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_ENROLMENT);
			transaction.setUserDetail(requestObject.getUSERDETAILS().getUSERNAME()+"__"+requestObject.getUSERDETAILS().getORGID());
			transaction.setCustomerId(requestObject.getCOUPONCODEINFO().getCUSTOMERID() != null && !requestObject.getCOUPONCODEINFO().getCUSTOMERID().isEmpty()? requestObject.getCOUPONCODEINFO().getCUSTOMERID().trim() : null);
			transaction.setDocSID(requestObject.getCOUPONCODEINFO().getDOCSID() != null && !requestObject.getCOUPONCODEINFO().getDOCSID().isEmpty()? requestObject.getCOUPONCODEINFO().getDOCSID().trim() : null);
			transaction.setStoreNumber(requestObject.getCOUPONCODEINFO().getSTORENUMBER() != null && !requestObject.getCOUPONCODEINFO().getSTORENUMBER().isEmpty()? requestObject.getCOUPONCODEINFO().getSTORENUMBER().trim() : null);
			//PromoTrxLogDao.saveOrUpdate(transaction);
			PromoTrxLogDaoForDML.saveOrUpdate(transaction);
			
		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
		}
		return transaction;
	}
	
}
