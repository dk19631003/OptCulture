package org.mq.loyality.utils;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component("ajaxAuthenticationFailureHandler")
public class AjaxAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {  
        
    public AjaxAuthenticationFailureHandler() {   
    }
 
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
         
    	 if ("true".equals(request.getHeader("X-Ajax-call"))) {
             try {
                 response.getWriter().print("ok");//return "ok" string
                 response.getWriter().flush();
         } catch (IOException e) {              
            //handle exception...
         }
         } else {           
           
         
         }
    }
}
