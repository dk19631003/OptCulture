package org.mq.loyality.handler;


import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.loyality.utils.Constants;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;


public class ForbiddenEntryPoint extends Http403ForbiddenEntryPoint{
    
	private String accessDeniedUrl;
	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);
	public String getAccessDeniedUrl() {
		return accessDeniedUrl;
	}

	public void setAccessDeniedUrl(String accessDeniedUrl) {
		this.accessDeniedUrl = accessDeniedUrl;
	}
	
	
    /**
     * Always returns a 403 error code to the client.
     */
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2) throws IOException,
            ServletException {
    	
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        logger.info(request.getAttribute("error").toString());
        //httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        RequestDispatcher rd = request.getRequestDispatcher(accessDeniedUrl);
        rd.forward(request, response);
       // httpResponse.sendRedirect(accessDeniedUrl);
    }

}
