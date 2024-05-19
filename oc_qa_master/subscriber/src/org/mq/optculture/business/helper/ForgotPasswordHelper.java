package org.mq.optculture.business.helper;

import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.ResetPasswordToken;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.ResetPasswordTokenDao;
import org.mq.marketer.campaign.dao.ResetPasswordTokenDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;

public class ForgotPasswordHelper {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public String setFlagValue(String username,String orgid)
	{
		//String responseFlag =null;
		String responseFlag = "<font style='color:green'>A link to reset your password has been emailed to you.</font> <br/> <a href='/subscriber'>Click here to Login</a>";
		try {
		 	 /*Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);	 
		 	 
		 	 String username = request.getParameter("username").trim();
		 	 String orgid = request.getParameter("orgid").trim();
		 	 //String emailId=request.getParameter("emailId").trim();
			 ServletContext servletContext =this.getServletContext();
			 WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
			 UsersDao usersDao = (UsersDao)wac.getBean("usersDao");
			 ResetPasswordTokenDao resetPasswordTokenDao = (ResetPasswordTokenDao)wac.getBean("resetPasswordTokenDao");
			 ResetPasswordTokenDaoForDML resetPasswordTokenDaoForDML = (ResetPasswordTokenDaoForDML)wac.getBean("resetPasswordTokenDaoForDML");
			 EmailQueueDao emailQueueDao = (EmailQueueDao)wac.getBean("emailQueueDao");
			 EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)wac.getBean("emailQueueDaoForDML");
			*/
			
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			ResetPasswordTokenDao resetPasswordTokenDao = (ResetPasswordTokenDao)ServiceLocator.getInstance().getDAOByName(OCConstants.RESET_PASSWORD_TOKEN_DAO);
			ResetPasswordTokenDaoForDML resetPasswordTokenDaoForDML = (ResetPasswordTokenDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.RESET_PASSWORD_TOKEN_DAO_FOR_DML);
			EmailQueueDao emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("emailQueueDaoForDML");

			
		 	String fullUserName = username+"__org__"+orgid;
		 	Users user = usersDao.findByUsername(fullUserName);
		 	UserOrganization user_org_name = usersDao.findByOrgName(orgid);
		 	if(user == null) {
		 		return responseFlag = "No User exist with the given username and organization id";
		 		
		 	}
		 	else if (!user.isEnabled()) {
		 		return responseFlag = "Your account is currently deactivated. Contact our support team at support@optculture.com";
		 	}
		 	else {
			 	String fname = user.getFirstName().trim();
			 	String userOrgName = user_org_name.getOrganizationName();
			 	String emailId = user.getEmailId();
			 	String supportEmailId = PropertyUtil.getPropertyValueFromDB("SupportEmailId");
			 	Long userId = user.getUserId();
			 	String token = null;
			 	
		 		/*  final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
				Random rnd = new Random();
			 	int len = 16; */
			 	do {
				    /* StringBuilder sb = new StringBuilder( len );
				    for( int i = 0; i < len; i++ ) {
				      sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
				    } */
					token = Long.toHexString(Double.doubleToLongBits(Math.random()));
			 	} while(resetPasswordTokenDao.findByTokenValue(token) != null);
				
				ResetPasswordToken tokenObj = new ResetPasswordToken(token,userId,Calendar.getInstance(),Constants.TOKEN_STATUS_ACTIVE);
				
				resetPasswordTokenDaoForDML.saveOrUpdate(tokenObj);
				
				
			 	String url = PropertyUtil.getPropertyValue("resetPasswordUrl");
			 	url = url.replace("|^", "[").replace("^|", "]");
			 	url = url.replace("[userId]",userId.toString()).replace("[token]",token);
			 	
			 	String messageToUser = PropertyUtil.getPropertyValueFromDB("forgotPasswordTemplate");
			 	
			 	messageToUser = messageToUser.replace(Constants.FORGOT_PLACEHOLDERS_FNAME,fname).replace(Constants.FORGOT_PLACEHOLDERS_URL,url).replace(Constants.FORGOT_PLACEHOLDERS_USER_NAME,username).replace(Constants.FORGOT_PLACEHOLDERS_ORG_NAME,userOrgName);
			 	
			 
		 		/* String messageToUser = "Hi " + fname + ",<br><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
			 			"We have received a request from you for your password. Please click on the link below to reset your password.<br/>"+
		 		 		"<a href="+url+">Reset Password</a>"+
		 		 		"<br><br/><b>Note: </b>This link will expire after 24 hours."+
		 		 		"<br><br/>If you have not sent this request, please contact our support team at <a href=\"mailto:support@optculture.com\">support@optculture.com</a> immediately. " +
		 		 		" <br><br/> Regards,<br/>Support Team"; */
		 		 
		 		
			 	try {
				 	EmailQueue emailQueue = new EmailQueue("Reset Password",messageToUser,Constants.EQ_TYPE_FORGOT_PASSWORD,"Active",emailId,MyCalendar.getNewCalendar(),user);
				 	emailQueueDaoForDML.saveOrUpdate(emailQueue);
			 	} catch(Exception e) {
			 		logger.error("Exception");
			 	}
			 	return responseFlag = "<font style='color:green'>A link to reset your password has been emailed to you.</font> <br/> <a href='/subscriber' style='color:blue'>Click here to Login</a>";
		 	
		 	} // else
	 } catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);	
			return responseFlag;
			}

	}

}
