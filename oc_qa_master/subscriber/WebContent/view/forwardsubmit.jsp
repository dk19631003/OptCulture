<%@page import="org.mq.marketer.campaign.general.Utility"%>
<%@page import="org.mq.marketer.campaign.general.Constants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import="org.mq.marketer.campaign.beans.*" %>
<%@ page import="org.mq.marketer.campaign.dao.*" %>
<%@ page import="org.mq.marketer.campaign.controller.*" %>
<%@page import="org.mq.marketer.campaign.general.Constants"%>
<%@ page import="org.springframework.beans.factory.xml.XmlBeanFactory" %>
<%@ page import="org.springframework.core.io.ClassPathResource" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.logging.log4j.Logger" %>
<%@ page import="org.apache.logging.log4j.LogManager" %>
<%@page import="java.util.List"%>
<%@page import="org.mq.optculture.business.helper.ForwardSubmitHelper"%>


 <%

Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
/* ServletContext servletContext =this.getServletContext();
WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
FarwardToFriendDao farwardToFriendDao = (FarwardToFriendDao)wac.getBean("farwardToFriendDao");
FarwardToFriendDaoForDML farwardToFriendDaoForDML = (FarwardToFriendDaoForDML)wac.getBean("farwardToFriendDaoForDML");
 */
%>

	<%
	
	String custMessage = request.getParameter("custmsg");
	logger.info("custMessage is" +custMessage);
	
	String userIdStr = request.getParameter("userId");
	logger.info("user id  str is"+userIdStr);
	
	Long userId= Long.parseLong(userIdStr);
	logger.info("user id is"+userIdStr);
	
	String refererName= request.getParameter("fullname") ;
	logger.info("refererName is"+refererName);
	
	String refererEmailStr =request.getParameter("emailId");
	logger.info("refererEmailStr is"+refererEmailStr);
	
	String campRepIdStr = request.getParameter("crId");
	logger.info("campRepIdStr is"+campRepIdStr);
	Long crId= Long.parseLong(campRepIdStr);
	
	String sentIdStr = request.getParameter("sentId");
	logger.info("sentIdStr is"+sentIdStr);
	Long sentId = Long.parseLong(sentIdStr);
	
	String cIdStr = request.getParameter("contactId");
	logger.info("cIdStr is"+cIdStr);
	Long cId= Long.parseLong(cIdStr);
	
	String [] email = request.getParameterValues("email");
	logger.info("email is"+email.length);
	
	String [] name =request.getParameterValues("name");
	logger.info("name is"+name.length);
	logger.info("name is"+name);
	
	ForwardSubmitHelper forwardsubmithelper = new ForwardSubmitHelper();
	forwardsubmithelper.setFlagValue(custMessage,userIdStr,userId,refererName,refererEmailStr,campRepIdStr,crId,sentIdStr,sentId,cIdStr,cId,email,name);
	
	
	/*  List<FarwardToFriend> farwardToFriendList = new ArrayList<FarwardToFriend>();
	
	
	for (int z=0; z < email.length; z++)
	{
		
		 	
			if(email[z].length() == 0 ||  email[z].isEmpty() || email[z].equals("Your friend's email ID") ) {
				continue;
			}
			
			FarwardToFriend farwardToFriend = new FarwardToFriend();
			farwardToFriend.setReferer(refererName);
			farwardToFriend.setEmail(refererEmailStr);
			farwardToFriend.setToEmailId(email[z]);
			farwardToFriend.setSentDate(Calendar.getInstance());
			
			farwardToFriend.setCustMsg(custMessage);
			farwardToFriend.setContactId(cId);
			farwardToFriend.setSentId(sentId);
			farwardToFriend.setUserId(userId);
			farwardToFriend.setCrId(crId);
			farwardToFriend.setStatus(Constants.CAMP_STATUS_ACTIVE);
			if(name[z].length() == 0 ||  name[z].isEmpty() || name[z].equals("Your friend's name") ) {
				
				farwardToFriend.setToFullName("");
			
			}else{
				farwardToFriend.setToFullName(name[z]);
			}
			
			farwardToFriendList.add(farwardToFriend);
			
			
		
	}
	
	farwardToFriendDaoForDML.saveByCollection(farwardToFriendList); */ 
	%>
	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Forward to Friend</title>


		

			<%--  <%
			 List<FarwardToFriend> farwardToFriendList = new ArrayList<FarwardToFriend>();
			 request.setAttribute("fList", farwardToFriendList);
		 	//Stirng custMsg= document.getElementById('textarea');
			 
			 %>
		 
		 
					 <!-- 
						 var nameElement = allRows[0].value;
						 var emailValue = allRows[1].value; -->
						 
						 
						 <%
						 	String name = "<script>document.writeln(nameElement)</script>";
						 	String emailIdstr ="<script>document.writeln(emailValue)</script>";
						 	String custMessage ="<script>document.writeln(custMsg)</script>";
						 	
						 	String refererName= (String)request.getAttribute(Constants.QS_FULL_NAME) ;
							String emailStr =(String)request.getAttribute(Constants.QS_EMAIL);
							
							Long campRepId=(Long)request.getAttribute(Constants.QS_CRID);
							Long referUserId = (Long)request.getAttribute(Constants.QS_USERID);
							CampaignSent campaignSent2 = (CampaignSent)request.getAttribute(Constants.QS_SENTID);
							Long sentId = campaignSent2.getSentId().longValue();
							Long contactId =campaignSent.getContactId().longValue();
							
							
							FarwardToFriend farwardToFriend = new FarwardToFriend();
						 	logger.info("farward obj is "+farwardToFriend);
							farwardToFriend.setReferer(refererName);
							farwardToFriend.setEmail(emailStr);
							farwardToFriend.setSentDate(Calendar.getInstance());
							farwardToFriend.setToEmailId(emailIdstr);
							farwardToFriend.setToFullName(name);
							farwardToFriend.setCustMsg(custMessage);
							farwardToFriend.setContactId(contactId);
							farwardToFriend.setSentId(sentId);
							farwardToFriend.setUserId(userId);
							farwardToFriend.setCrId(campRepId);
							
							List<FarwardToFriend> farwardToFriendarLst = (List<FarwardToFriend>)request.getAttribute("fList");
							
							farwardToFriendarLst.add(farwardToFriend);
							
							request.setAttribute("f2fList", farwardToFriendarLst);
							
							
							 
					 
						 %>
						  
						
						 
							<%
							List<FarwardToFriend> farwardToFriendarLst1  = (List)request.getAttribute("f2fList");
							farwardToFriendDao.saveByCollection(farwardToFriendarLst1);
							out.println("<html><body><table><tr><td>Thank you very much for your interest to receive the emails from this user.</td></tr></table></body></html>");
							%> --%>
		
  </head>
  
  
  <body>
  
  
  <table>
  <tr>
  <td height="150" align="center" valign="middle">
  <div style="font-family: Arial,Helvetica,sans-serif;font-size: 12px;background:#f0ffde; border:1px solid #c4eb96; color:#333333;margin: 0 10px; padding: 20px;">
  Thank you! The email has been forwarded to your friend(s) successfully.</div>
  </td>
  </tr>
  </table>
 
  </html>
  
  
  