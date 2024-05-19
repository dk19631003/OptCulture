package org.mq.optculture.business.nexon;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class NexonStoreEnquiryServiceImpl implements NexonStoreEnquiryBusinessService  {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException {
		
		BaseResponseObject baseResponseObject = new BaseResponseObject();
		NexonStoreEnquiryResponse nexonResponse = new NexonStoreEnquiryResponse();

		try {
			logger.debug("-------entered nexon processRequest---------");
			Gson gson = new Gson();
			NexonStoreEnquiryRequest nexonRequest = null;
			try {
				nexonRequest = gson.fromJson(baseRequestObject.getJsonValue(), NexonStoreEnquiryRequest.class);
			} catch (JsonSyntaxException e) {
				logger.error("Exception ::", e);
				Status status = new Status("1000002",
						PropertyUtil.getErrorMessage(1000002, OCConstants.ERROR_FAQ_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				nexonResponse.setStatus(status);
				String json = gson.toJson(nexonResponse);
				baseResponseObject.setJsonValue(json);
				baseResponseObject.setAction("NexonStoreEnquiryRequest");
				return baseResponseObject;
			}
			NexonStoreEnquiryBusinessService nexonBusinessService = (NexonStoreEnquiryBusinessService) ServiceLocator.getInstance().getServiceByName(OCConstants.NEXON_STORE_ENQUIRY_BUSINESS_SERVICE);
			nexonResponse = nexonBusinessService.processNexonRequest(nexonRequest);

			// object to json
			String json = gson.toJson(nexonResponse);
			baseResponseObject.setJsonValue(json);
			baseResponseObject.setAction("NexonStoreEnquiryRequest");
			return baseResponseObject;
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		logger.debug("-------exit nexon processRequest---------");
		
		return null;
	}

	@Override
	public NexonStoreEnquiryResponse processNexonRequest(NexonStoreEnquiryRequest nexonRequest)
			throws BaseServiceException {
		// TODO Auto-generated method stub
		
		Users user = null;
		Status status = new Status();
		NexonStoreEnquiryResponse response  = new NexonStoreEnquiryResponse();
		
		if(nexonRequest.getuserName() == null || nexonRequest.getuserName().trim().length() <=0 || 
				nexonRequest.getorganizationId() == null || nexonRequest.getorganizationId().trim().length() <=0 || 
						nexonRequest.getToken() == null || nexonRequest.getToken().trim().length() <=0) {
			status = new Status("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			response.setStatus(status);
			return response;
		}
		
		try {
			user = getUser(nexonRequest.getuserName(),nexonRequest.getorganizationId(),nexonRequest.getToken());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(user == null){
			status = new Status("101013", PropertyUtil.getErrorMessage(101013, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			response.setStatus(status);
			return response;
		}
		Long userId = user.getUserId();
		Long orgId = user.getUserOrganization().getUserOrgId();
		try {
			OrganizationStoresDao storesDao = (OrganizationStoresDao) ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
			RetailProSalesDao salesDao = (RetailProSalesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
			
			List<Object[]> salesList = salesDao.getLastMonthSalesByUserId(userId);
			
			List<StoreInfo> storeList = new ArrayList<StoreInfo>();
			
			response.setCompanyId(userId+"");
			
			for(Object[] object : salesList) {
				
				StoreInfo store = new StoreInfo();
				String storeNumber = object[0]+"";
				String storeSaleAmount = object[1]+"";
				logger.info("storeNumber "+storeNumber+" storeSaleAmount "+storeSaleAmount);
				
				OrganizationStores storeDetail = storesDao.findByStoresId(storeNumber,orgId);
				if(storeDetail!=null) {
					logger.info("store name "+storeDetail.getStoreName());
					store.setStoreName(storeDetail.getStoreName());
					store.setStoreNumber(storeNumber);
					store.setStoreCode(storeDetail.getERPStoreId());
					store.setStoreSaleAmount(storeSaleAmount);
					storeList.add(store);
				}
			}
			response.setStores(storeList);
			status = new Status("0","Store Enquiry Sucessfull",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			response.setStatus(status);
			
			return response;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private Users getUser(String userName, String orgId, String userToken) throws Exception{
		
		String completeUserName = userName+Constants.USER_AND_ORG_SEPARATOR+orgId;
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Users user = usersDao.findUserByToken(completeUserName, userToken);
		return user;
}

}
