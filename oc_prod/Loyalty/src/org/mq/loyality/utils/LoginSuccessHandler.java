package org.mq.loyality.utils;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                   Authentication authentication) throws ServletException, IOException {
        request.getSession().setMaxInactiveInterval(60 * 60); //one hour
        logger.info("Session set up for 60min");
        super.onAuthenticationSuccess(request, response, authentication);
     }
}
