/*package org.mq.loyality.utils;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component("customAuthenticationSuccessHandler")
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	  public CustomAuthenticationSuccessHandler() {   
	    }
	 

	public CustomAuthenticationSuccessHandler(String defaultUrl) {
setDefaultTargetUrl(defaultUrl);
}
 
 (non-Javadoc)
* @see org.springframework.security.web.authentication.AuthenticationSuccessHandler#onAuthenticationSuccess(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.Authentication)

@Override
public void onAuthenticationSuccess(HttpServletRequest request,
HttpServletResponse response, Authentication authentication)
throws IOException, ServletException {
if(RequestUtil.isAjaxRequest(request)) {
RequestUtil.sendJsonResponse(response, "success", "true");
} else {
super.onAuthenticationSuccess(request, response, authentication);
}
 
}
 
} */