<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="org.mq.optculture.business.helper.ForgotPasswordHelper"%>
<% String flag = null;
   
	String username = request.getParameter("username").trim();
	String orgid = request.getParameter("orgid").trim();
	ForgotPasswordHelper forgotPasswordHelper = new ForgotPasswordHelper();
	flag=forgotPasswordHelper.setFlagValue(username,orgid);
	if(flag!=null)
	{
		out.println(flag);

		flag = null;
	}else
	{
		out.println("");
	}%>
</body>
</html>
