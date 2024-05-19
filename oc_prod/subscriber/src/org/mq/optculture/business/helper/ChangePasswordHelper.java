package org.mq.optculture.business.helper;

import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ResetPasswordToken;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.ResetPasswordTokenDao;
import org.mq.marketer.campaign.dao.ResetPasswordTokenDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCrypt;


public class ChangePasswordHelper {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	public String setFlagValue(Long userId,String token,String password)
	{
		String responseFlag ="<font style='color:green'>Password changed successfully.</font> <br/> <a href='/subscriber' style='color:blue'>Click here to Login</a>";

		try {
			/*ServletContext servletContext =this.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

			UsersDao usersDao = (UsersDao)wac.getBean("usersDao");
			ResetPasswordTokenDao resetPasswordTokenDao = (ResetPasswordTokenDao)wac.getBean("resetPasswordTokenDao");
			UsersDaoForDML usersDaoForDML = (UsersDaoForDML)wac.getBean("usersDaoForDML");
			ResetPasswordTokenDaoForDML resetPasswordTokenDaoForDML = (ResetPasswordTokenDaoForDML)wac.getBean("resetPasswordTokenDaoForDML");
			 */
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			UsersDaoForDML usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
			ResetPasswordTokenDao resetPasswordTokenDao = (ResetPasswordTokenDao)ServiceLocator.getInstance().getDAOByName(OCConstants.RESET_PASSWORD_TOKEN_DAO);
			ResetPasswordTokenDaoForDML resetPasswordTokenDaoForDML = (ResetPasswordTokenDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.RESET_PASSWORD_TOKEN_DAO_FOR_DML);



			ResetPasswordToken tokenObj = resetPasswordTokenDao.findByTokenValue(token);
			Users userObj = usersDao.findByUserId(userId);

			//String password = request.getParameter("password");
			// Md5PasswordEncoder md5 = new Md5PasswordEncoder();
			//String hash = md5.encodePassword(password,userObj.getUserName());
			String hash = BCrypt.hashpw(password, BCrypt.gensalt());

			userObj.setPassword(hash);
			userObj.setMandatoryUpdatePwdOn(Calendar.getInstance());
			usersDaoForDML.saveOrUpdate(userObj);

			tokenObj.setStatus(Constants.TOKEN_STATUS_EXPIRED);
			resetPasswordTokenDaoForDML.saveOrUpdate(tokenObj);

			return responseFlag;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);	
			return responseFlag;
		}
	}
}
