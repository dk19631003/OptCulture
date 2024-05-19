package org.mq.marketer.campaign.controller;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ResetPasswordToken;
import org.mq.marketer.campaign.dao.ResetPasswordTokenDao;
import org.mq.marketer.campaign.dao.ResetPasswordTokenDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class ResetPasswordController extends AbstractController {

	private static final Logger logger = LogManager
			.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private ResetPasswordTokenDao resetPasswordTokenDao;
	private ResetPasswordTokenDaoForDML resetPasswordTokenDaoForDML;
	public ResetPasswordTokenDaoForDML getResetPasswordTokenDaoForDML() {
		return resetPasswordTokenDaoForDML;
	}

	public void setResetPasswordTokenDaoForDML(
			ResetPasswordTokenDaoForDML resetPasswordTokenDaoForDML) {
		this.resetPasswordTokenDaoForDML = resetPasswordTokenDaoForDML;
	}

	public void setResetPasswordTokenDao(
			ResetPasswordTokenDao resetPasswordTokenDao) {
		this.resetPasswordTokenDao = resetPasswordTokenDao;
	}

	public ResetPasswordTokenDao getResetPasswordTokenDao() {
		return this.resetPasswordTokenDao;
	}
	
	/*private UsersDao usersDao;
	public void setUsersDao(
			UsersDao usersDao) {
		this.usersDao = usersDao;
	}

	public UsersDao getUsersDao() {
		return this.usersDao;
	}*/

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		String requestedAction = request.getParameter("requestedAction");

		if (requestedAction == null) {
			return null;
		}
		if (requestedAction.equalsIgnoreCase("resetPwd")) {
			String token = request.getParameter("token");
			Long userId = Long.parseLong(request.getParameter("userId"));
			
			ResetPasswordToken tokenObj = resetPasswordTokenDao.findByTokenValue(token);
			
			Calendar createdDate = tokenObj.getCreatedDate();
			
			Calendar expireDate = Calendar.getInstance();
			expireDate.set(createdDate.get(Calendar.YEAR), createdDate.get(Calendar.MONTH), createdDate.get(Calendar.DATE), 
					createdDate.get(Calendar.HOUR_OF_DAY), createdDate.get(Calendar.MINUTE), createdDate.get(Calendar.SECOND));
			expireDate.add(Calendar.HOUR_OF_DAY, 24);
			Calendar currentDate = Calendar.getInstance();
			
			if (tokenObj != null && tokenObj.getStatus().equals(Constants.TOKEN_STATUS_ACTIVE) &&
					tokenObj.getUserId().toString().equals(userId.toString()) && currentDate.before(expireDate)) {
				
				request.setAttribute("token",token);
				request.setAttribute("userId", userId);
				//response.sendRedirect("http://localhost:8080/subscriber/changePassword.jsp?token="+token+"&userId="+userId);
				RequestDispatcher reqDispatcher = getServletContext().
						getRequestDispatcher("/changePassword.jsp");
	
				reqDispatcher.forward(request, response);
				
			}
			else {
				if (tokenObj != null && tokenObj.getStatus().equals(Constants.TOKEN_STATUS_ACTIVE) && 
						tokenObj.getUserId().toString().equals(userId.toString())) {
					tokenObj.setStatus(Constants.TOKEN_STATUS_EXPIRED);
					resetPasswordTokenDaoForDML.saveOrUpdate(tokenObj);
				}
				RequestDispatcher reqDispatcher = getServletContext().
						getRequestDispatcher("/view/resetPwdError.jsp");

				reqDispatcher.forward(request, response);
				
			}
			
			
		}

		return null;
	}

}
