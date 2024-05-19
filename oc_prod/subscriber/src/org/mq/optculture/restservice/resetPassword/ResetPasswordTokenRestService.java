package org.mq.optculture.restservice.resetPassword;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.model.resetPassword.ResetPasswordTokenRequest;
import org.mq.optculture.model.resetPassword.ResetPasswordTokenResponse;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class ResetPasswordTokenRestService  extends AbstractController  {

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) {
		ResetPasswordTokenResponse resetPasswordTokenResponse = null;


		String requestedAction = request.getParameter("requestedAction");
		if (requestedAction == null) {
			return null;
		}
		if (requestedAction.equalsIgnoreCase("resetPwd")) {
			try {
				String token = request.getParameter("token");
				Long userId = Long.parseLong(request.getParameter("userId"));

				ResetPasswordTokenRequest resetPasswordTokenRequestObj = new ResetPasswordTokenRequest();
				resetPasswordTokenRequestObj.setToken(token);
				resetPasswordTokenRequestObj.setUserId(userId);


				BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.RESET_PASSWORD_BUSINESS_SERVICE);
				resetPasswordTokenResponse = (ResetPasswordTokenResponse) baseService.processRequest(resetPasswordTokenRequestObj);

				if(!OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(resetPasswordTokenResponse.getStatus())){
					RequestDispatcher reqDispatcher = getServletContext().
							getRequestDispatcher("/view/resetPwdError.jsp");

					reqDispatcher.forward(request, response);
				}
				else {
					request.setAttribute("token",token);
					request.setAttribute("userId", userId);
					RequestDispatcher reqDispatcher = getServletContext().
							getRequestDispatcher("/changePassword.jsp");

					reqDispatcher.forward(request, response);
				}
			} catch (Exception e) {
				logger.error("Exception ::" , e);
			}
		}
		return null;
	}

}
