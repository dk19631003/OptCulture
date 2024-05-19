package org.mq.optculture.business.sparkbase;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.json.simple.JSONObject;
import org.mq.marketer.campaign.beans.GetTokenRequestLog;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.sparkbase.Body;
import org.mq.optculture.model.sparkbase.Head;
import org.mq.optculture.model.sparkbase.TokenRequestObject;
import org.mq.optculture.model.sparkbase.TokenResponse;
import org.mq.optculture.model.sparkbase.TokenResponseObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.dao.DataIntegrityViolationException;

import com.google.gson.Gson;

public class SparkBaseTokenServiceImpl implements SparkBaseTokenService{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		BaseResponseObject baseResponseObject=new BaseResponseObject();
		TokenResponseObject tokenResponseObject=null;
		try {
			TokenRequestObject tokenRequestObject=(TokenRequestObject)baseRequestObject;
			SparkBaseTokenService sparkBaseTokenService=(SparkBaseTokenService) ServiceLocator.getInstance().getServiceByName(OCConstants.SPARKBASE_TOKEN_SERVICE);
			tokenResponseObject = (TokenResponseObject) sparkBaseTokenService.processTokenRequest(tokenRequestObject);

			//toJson
			Gson gson = new Gson();
			String json = gson.toJson(tokenResponseObject);
			baseResponseObject.setJsonValue(json);
			
		}catch (Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing processRequest::::: ", e);
		}
		return baseResponseObject;
	}//processRequest
	@Override
	public TokenResponseObject processTokenRequest(
			TokenRequestObject tokenRequestObject) throws BaseServiceException {
		TokenResponseObject tokenResponseObject=null;
		//TokenResponse tokenResponse=null;
		Head head=null;
		//Body body=null;
		Users user=null;
		try {
			String userName = tokenRequestObject.getUserName();
			//String password = tokenRequestObject.getPassword();
			String org = tokenRequestObject.getOrgId();
			String token = null;
			head=validateRequestUser(userName,org);
			if(head!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(head.getRESPONSESTATUS())){
				tokenResponseObject=finalResponse(user,head);
				return tokenResponseObject;
			}
			userName = userName + Constants.USER_AND_ORG_SEPARATOR + org;
			UsersDao usersDao= (UsersDao) ServiceLocator.getInstance().getDAOByName("usersDao");
			//UsersDaoForDML usersDaoForDML= (UsersDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName("usersDaoForDML");
			//user = usersDao.findByUsernameAndPassword(userName, password);
			user=usersDao.findByUsername(userName);
			head=validateUser(user);
			if(head!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(head.getRESPONSESTATUS())){
				tokenResponseObject=finalResponse(user,head);
				return tokenResponseObject;
			}
			head=validateUserToken(user,userName,org);
			token=user.getToken();
			head=validateToken(token,userName);
			if(head!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(head.getRESPONSESTATUS())){
				tokenResponseObject=finalResponse(user,head);
				return tokenResponseObject;
			}
			head = new Head("0","",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			tokenResponseObject=finalResponse(user,head);
			
			return tokenResponseObject;
		} catch(Exception e) {
			logger.error("Exception ::::", e);
			logger.info("Error : server error occured");
			throw new BaseServiceException("Exception occured while processing processTokenRequest::::: ", e);
		} 
		/*finally {
			tokenResponseObject=finalResponse(user,head);
			return tokenResponseObject;
		}*/
	}//processTokenRequest
	
	private Head validateToken(String token,String userName) throws BaseServiceException{
		Head head=null;
		if(token == null) {
			logger.info("Error Unable to Create Token to user  :"+ userName );
			head = new Head("400","Error Unable to Create Token to user  :"+ userName ,OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return head ;
		}
		return head ;
	}//validateToken
	
	private Head validateUserToken(Users user,String userName,String org)  throws BaseServiceException{
		Head head=null;
		try {
			UsersDao usersDao= (UsersDao) ServiceLocator.getInstance().getDAOByName("usersDao");
			UsersDaoForDML usersDaoForDML= (UsersDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName("usersDaoForDML");
			if(user.getToken() != null) {
				head = new Head("0","",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			}
			else {
				List<StringBuffer> list = Utility.getSixDigitURLCode(userName +org);
				for (StringBuffer stringBuffer : list) {
					try {
						user.setToken(stringBuffer.toString());
						//usersDao.saveOrUpdate(user);
						usersDaoForDML.saveOrUpdate(user);
						break;
					}catch (DataIntegrityViolationException e) {
						logger.error("given User Token is already exist in DB.....",e);
						continue;

					}catch (ConstraintViolationException e) {
						logger.error("given User Token is already exist in DB.....",e);
						continue;
					}
				} // for
				head = new Head("0","",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			} // else 
		}catch (Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing validateUserToken::::: ", e);
		}
		return head ;
	}//validateUserToken

	private Head validateUser(Users user)  throws BaseServiceException{
		Head head=null;
		if(user == null) {
			logger.info("No user exists with the given credentials");
			head = new Head("300","No user exists with the given credentials",OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		}
		return head;
	}//validateUser

	private Head validateRequestUser(String userName,String org)  throws BaseServiceException{
		Head head=null;
		if(userName == null || org == null) {
			logger.info("Username or organisation cannot be empty");
			head = new Head("100","Username or organisation  cannot be empty",OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return head;
		}
		if(userName.trim().length() < 1 || org.trim().length() < 1) {
			logger.info("Username or organisation is invalid");
			head = new Head("200","Username or organisation cannot be empty",OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return head;
		}
		return head ;
	}//validateRequestUser
	
	
	
	public TokenResponseObject finalResponse(Users user,Head head) throws BaseServiceException{
		TokenResponseObject tokenResponseObject=new TokenResponseObject();
		TokenResponse tokenResponse=new TokenResponse();
		tokenResponse.setHead(head);
		if(head.getRESPONSECODE()== "0") {
			String token=user.getToken();
			Body body=new Body(token);
			tokenResponse.setBody(body);
		}
		tokenResponseObject.setTokenResponse(tokenResponse);
		return tokenResponseObject;
	}
}//finalResponse
