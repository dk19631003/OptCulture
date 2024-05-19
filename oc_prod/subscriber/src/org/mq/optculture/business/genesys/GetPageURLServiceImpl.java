package org.mq.optculture.business.genesys;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.mobileapp.OCLoyaltyMemberLoginService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.genesys.BodyParams;
import org.mq.optculture.model.genesys.Data;
import org.mq.optculture.model.genesys.GetPageURLRequest;
import org.mq.optculture.model.genesys.GetPageURLResponse;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;

public class GetPageURLServiceImpl implements GetPageURLSerivce{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException {
		// TODO Auto-generated method stub
		logger.info(" Login base oc service called ...");
		String serviceRequest = baseRequestObject.getAction();
		String requestJson = baseRequestObject.getJsonValue();
		Gson gson = new Gson();
		GetPageURLResponse getPageURLResponse = null;
		GetPageURLRequest getPageURLRequest = null;
		BaseResponseObject responseObject = null;
		String responseJson = null;
		
		if(serviceRequest == null || !OCConstants.GET_PAGE_URL_ACTION.equals(serviceRequest)){
			
			
			getPageURLResponse = new GetPageURLResponse();
			getPageURLResponse.setError("");
			
			responseJson = gson.toJson(getPageURLResponse);
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			return responseObject;
		}
		
		try{
			getPageURLRequest = gson.fromJson(requestJson, GetPageURLRequest.class);
		}catch(Exception e){
			getPageURLResponse = new GetPageURLResponse();
			getPageURLResponse.setStatus("");
			responseJson = gson.toJson(getPageURLResponse);
			
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			logger.info("Exited baserequest due to invalid JSON ");
			return responseObject;
			
		}
		
		try{
			GetPageURLSerivce getPageURLSerivce = (GetPageURLSerivce) ServiceLocator.getInstance().getServiceByName(OCConstants.GET_PAGE_URL_BUSINESS_SERVICE);
			getPageURLResponse = getPageURLSerivce.processGetPageUrlRequest(getPageURLRequest, baseRequestObject.getTransactionId(), baseRequestObject.getTransactionDate());
			responseJson = gson.toJson(getPageURLResponse);
			
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
		}catch(Exception e){
			logger.error("Exception in loyalty inquiry base service.",e);
			throw new BaseServiceException("Server Error.");
		}
		return responseObject;
	}
	private boolean validateReqData(GetPageURLRequest getPageURLRequest){
		
		boolean isValid = true;
		BodyParams bodyParams = getPageURLRequest.getBodyParams();
		if(bodyParams == null || bodyParams.getEventType() == null || bodyParams.getEventType().isEmpty()){
			
			return false;
		}
		if(bodyParams.getEventType() != null && !bodyParams.getEventType().equals("viewCustomer") &&
				(bodyParams.getBillGUID()==null || bodyParams.getBillGUID().isEmpty() )){

			return false;
		}
				
		
		return true;
		
	}
	private GetPageURLResponse prepareGetPageURLResponse(String status, List<Data> data, String error){
		GetPageURLResponse getPageURLResponse = new GetPageURLResponse();
		getPageURLResponse.setStatus(status);
		getPageURLResponse.setError(error);
		getPageURLResponse.setData(data);
		return getPageURLResponse;
		
		
	}
	private Users getUser(String userName, String orgId) throws Exception{
		
		String completeUserName = userName+Constants.USER_AND_ORG_SEPARATOR+orgId;
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Users user = usersDao.findByUsername(completeUserName);
		return user;
	}	
	@Override
	public GetPageURLResponse processGetPageUrlRequest(GetPageURLRequest getPageURLRequest, String transactionId, String transactionDate) {
		// TODO Auto-generated method stub
		GetPageURLResponse getPageURLResponse = null;
		Users user = null;
		
		try{
			//validate mandatory fields userorgID, membershipnumber / phone number , password/OTP , incase OTP phone is must
			if(!validateReqData(getPageURLRequest)){
				
				return prepareGetPageURLResponse("", null, "Invalid Request");
			}
			
			//validate user
			user = getUser(getPageURLRequest.getUser().getUserName(), getPageURLRequest.getUser().getOrganizationId());
			if(user == null){
				return prepareGetPageURLResponse("", null, "Invalid user.");
			}
			if(!user.isEnabled()){
				return prepareGetPageURLResponse("", null, "User is disabled.");
			}
			if(user.getPackageExpiryDate().before(Calendar.getInstance())){
				return prepareGetPageURLResponse("", null, "User package is expired.");
			}
			
			BodyParams params = getPageURLRequest.getBodyParams();
			String eventType = params.getEventType();
			String urlKey = Utility.genesysEventMap.get(eventType);
			if(urlKey == null){
				return prepareGetPageURLResponse("", null, "Could not find the page.");
			}
			String url = PropertyUtil.getPropertyValueFromDB(urlKey);
			Data data = new Data();
			data.setRedemptionType("discount");
			data.setResponseUrl(url.replace("[MERCHANT]", 
					user.getUserOrganization().getOptSyncKey() != null ? user.getUserOrganization().getOptSyncKey() : user.getToken() )
					.replace("[BILLGUID]", params.getBillGUID()).replace("[MOBILE]", params.getMobile())
					.replace("[EVENT]", params.getEventType()).replace("[BILLVAL]", params.getBillValue()).
					replace("[USER]", Utility.getOnlyUserName(user.getUserName()))
					.replace("[ORGID]", Utility.getOnlyOrgId(user.getUserName()))
					.replace("[STORE]", getPageURLRequest.getHeader().getStoreNumber())
					
					.replace("[TOKEN]", user.getUserOrganization().getOptSyncKey() != null ? user.getUserOrganization().getOptSyncKey() : user.getToken())
					.replace("[HOST]", PropertyUtil.getPropertyValue("host")));
			List<Data> datas= new ArrayList<Data>();
			datas.add(data);
			
			return prepareGetPageURLResponse("200", datas, "");
			
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception: ", e);
			return prepareGetPageURLResponse("", null, "Server error.");
		}
		
	}

}
