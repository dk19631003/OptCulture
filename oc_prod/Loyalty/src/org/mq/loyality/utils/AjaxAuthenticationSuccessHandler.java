package org.mq.loyality.utils;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


public class AjaxAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {  
        
    public AjaxAuthenticationSuccessHandler() {   
    }
 
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException { 
         
        HttpSession session = request.getSession();    
//      DefaultSavedRequest defaultSavedRequest = (DefaultSavedRequest) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        //check if login is originated from ajax call
        if ("true".equals(request.getHeader("X-Ajax-call"))) {
            try {
                response.getWriter().print("ok");//return "ok" string
                response.getWriter().flush();
        } catch (IOException e) {              
           //handle exception...
        }
        } else {           
            setAlwaysUseDefaultTargetUrl(false);       
        
        }
    }
}
