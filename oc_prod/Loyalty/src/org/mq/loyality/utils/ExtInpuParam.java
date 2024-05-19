package org.mq.loyality.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class ExtInpuParam extends
UsernamePasswordAuthenticationFilter {
//private String extraParameter = "j_organization";
private String delimiter = ":";
@Override
public Authentication attemptAuthentication(HttpServletRequest request,
HttpServletResponse response) throws AuthenticationException {
logger.info("Attempting for authentication. " + "j_username = "
+ request.getParameter("j_username") + ", j_password = "
+ request.getParameter("j_password"));
return super.attemptAuthentication(request, response);
}
@Override
protected String obtainUsername(HttpServletRequest request) {
String username = request.getParameter(getUsernameParameter());
logger.debug("username = " + username);
/*String extraInput = request.getParameter(getExtraParameter()) == null ? ""
: request.getParameter(getExtraParameter());*/
String combinedUsername = "";
/*logger.debug("extParam = " + extParam);
if (extParam.length() == 0) {
combinedUsername = username;
} else {
combinedUsername = username + getDelimiter() + extraInput;
}
logger.debug("returning combinedUsername = " + combinedUsername);*/
return combinedUsername;
}
/**
* @return The parameter name which will be used to obtain the extra input
* from the login request
*/
/*public String getExtraParameter() {
return this.extraParameter;
}*/

/**
* @param extraParameter
* The parameter name which will be used to obtain the extra
* input from the login request
*/
/*public void setExtraParameter(String extraParameter) {
this.extraParameter = extraParameter;
}*/

/**
* @return The delimiter string used to separate the username and extra
* input values in the string returned by
* obtainUsername()
*/
public String getDelimiter() {
return this.delimiter;
}

/**
* @param delimiter
* The delimiter string used to separate the username and extra
* input values in the string returned by
* obtainUsername()
*/
public void setDelimiter(String delimiter) {
this.delimiter = delimiter;
}

}