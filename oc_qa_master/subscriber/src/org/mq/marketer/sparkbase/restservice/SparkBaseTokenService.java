package org.mq.marketer.sparkbase.restservice;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.json.simple.JSONObject;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class SparkBaseTokenService extends AbstractController {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private UsersDao usersDao;
	private UsersDaoForDML usersDaoForDML;
	

	public UsersDaoForDML getUsersDaoForDML() {
		return usersDaoForDML;
	}

	public void setUsersDaoForDML(UsersDaoForDML usersDaoForDML) {
		this.usersDaoForDML = usersDaoForDML;
	}

	public UsersDao getUsersDao() {
		return usersDao;
	}

	public void setUsersDao(UsersDao usersDao) {
		this.usersDao = usersDao;
	}

	public SparkBaseTokenService() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		int errorNo = -1;
		String briefMessage = "";
		String token = null;
		PrintWriter pw = response.getWriter();
		//response.setContentType("");
		try {
			
			String userName = request.getParameter("username");
			String password = request.getParameter("password");
			String org = request.getParameter("orgid");
			if(userName == null || password == null || org == null) {
				
				logger.info("Username or password cannot be empty");
				errorNo = 100;
				briefMessage = "Username or password or organisation  cannot be empty";
				return null;
			}
			
			if(userName.trim().length() < 1 || password.trim().length() < 1 || org.trim().length() < 1) {
				
				logger.info("Username or password organisation is invalid");
				errorNo = 200;
				briefMessage = "Username or password cannot be empty";
				return null;
			}
			
			userName = userName + Constants.USER_AND_ORG_SEPARATOR + org;
			
			//Users user = usersDao.findByUsernameAndPassword(userName, password);
			Users user = usersDao.findByUsername(userName);
			
			if(user == null) {
				
				logger.info("No user exists with the given credentials");
				errorNo = 300;
				briefMessage = "No user exists with the given credentials";
				return null;
			}
			if(user.getToken() != null) {
				
				token = user.getToken();
				errorNo=0;	
			} else {
				
				List<StringBuffer> list = Utility.getSixDigitURLCode(userName + password + org);
				
				for (StringBuffer stringBuffer : list) {
					
					try {
						
						user.setToken(stringBuffer.toString());
						//usersDao.saveOrUpdate(user);
						usersDaoForDML.saveOrUpdate(user);
						break;
					}catch (DataIntegrityViolationException e) {
						// TODO: handle exception
						logger.error("given User Token is already exist in DB.....",e);
						continue;
						
					}catch (ConstraintViolationException e) {
						// TODO: handle exception
						logger.error("given User Token is already exist in DB.....",e);
						continue;
					}
					
				} // for
				token = user.getToken();					
				errorNo=0;				
			} // else 
			
			
			if(token == null) {
				
				
				logger.info("Error Unable to Create Token to user  :"+ userName + " password : "+ password);
				errorNo = 400;
				briefMessage = "Error Unable to Create Token to user  :"+ userName + " password : "+ password;
				return null;
			}
			
			
			
		} catch(Exception e) {
			
			logger.error("Exception ::::", e);
			logger.info("Error : server error occured");
			errorNo = 1000;
			briefMessage = "Error : server error occured";
			return null;
		} finally {
			
			JSONObject respRoot = new JSONObject();
			JSONObject respMain = new JSONObject();
			
			JSONObject respHead = new JSONObject();
			JSONObject respBody = new JSONObject();
			
			respHead.put("responseCode", errorNo);
			respHead.put("responseMessage", briefMessage);
			respHead.put("responseStatus", errorNo == 0 ? "Success" : "Failure");
			
			if(errorNo == 0) {
				
				respBody.put("token", token);
				respMain.put("body", respBody);
			}
			
			respMain.put("head", respHead);
			//respMain.put("body", respBody);
			respRoot.put("TokenResponse", respMain);
			
			pw.write(respRoot.toJSONString());
			
			pw.flush();
			pw.close();
		}
		
		return null;
	}
}
