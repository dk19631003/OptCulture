
<%@page import="org.mq.optculture.model.campaign.GenericUnsubscribeResponse"%>
<%@page import="org.mq.optculture.model.campaign.GenericUnsubscribeRequest"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri='http://java.sun.com/jstl/core_rt'%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="org.mq.marketer.campaign.general.Constants"%>
<%@page import="java.util.List"%>
<%@page import="org.mq.marketer.campaign.beans.TemplateCategory"%>
<%@page import="org.mq.optculture.business.helper.GenericUnsubscribeReportUpdateHelper" %>
<%@page import="org.mq.optculture.utils.OCConstants" %>
<%@page import ="org.mq.marketer.campaign.beans.Users"%>
<%@page import="org.mq.marketer.campaign.dao.UsersDao"%>
<%@page import="org.mq.optculture.utils.ServiceLocator"%>
<%@page import="org.mq.optculture.business.helper.PintoRanch_UnsubscribeHelper"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Unsubscribe Request</title>
<!-- <script type="text/javascript">
				
				function calWeight() {
				
					var catArr= document.getElementsByName("category");
					var weight = 0;
					for(var i=0;i<catArr.length;i++) {
						
						if(catArr[i].checked == true) {
							weight = weight + parseInt(catArr[i].value);
						}
					} 
					//alert('weight :'+weight);
					document.getElementById("weight").value= weight; 
					return true;
				}
				
				function deselectAllWeight() {
					
					var catListArr= document.getElementsByName("category");
					for(var i=0;i<catListArr.length;i++) {
						catListArr[i].checked = false;
					}
					document.getElementById("weight").value = 0; 
					//alert('second method js');
					return true;
				}
	 </script> -->
	 <script type="text/javascript">
	 function gotFocus(item) {
		    if (item.value == "Email Address" ) {
		        item.value = '';
		    }
	 }
	 function vallidateEmail(){
	 var emailID = document.forms["unsubForm"]["emailId"].value;
	 emailID = emailID.trim()
	    atpos = emailID.indexOf("@");
	    dotpos = emailID.lastIndexOf(".");
	    var re = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	    if(!re.test(emailID)){
	    	 alert("Please enter valid email Address ");
		        return false;
	    } 
	    return true;
	    }
	 </script>
<style type="text/css">
#categoryTableId {
	font-family: arial, verdana;
	font-size: 15px;
	font-weight: bold;
}

#footer {
	padding: 30px 0px;
	font: 11px verdana;
	color: #6d6e71;
	text-align: center;
}

.unsubscribeBtn {
	color: #FFFFFF;
	font-size: 16px;
	font-weight: bold;
	background: url(../img/btn-1.jpg) 0 0 no-repeat;
	width: 207px;
	height: 49px;
	border: 0 none;
}

.resubscribeBtn {
	color: #FFFFFF;
	font-size: 16px;
	font-weight: bold;
	background: url(../img/btn2.jpg) 0 0 no-repeat;
	width: 207px;
	height: 49px;
	border: 0 none;
}

.txtarea {
	width: 290px;
	height: 90px;
	padding: 5px;
	border: 1px solid #c5c5c5;
	border-radius: 5px;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 12px;
	color: #666666;
}

.labelcell {
	padding-left: 15px;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 12px;
	color: #666666;
}

.sme {
	border: 1px solid #c5c5c5;
	border-radius: 5px;
	padding: 10px;
	margin-top: 20px;
	width: 510px;
}

.inputsm {
	width: 200px;
	padding: 0 4px;
	height: 33px;
	border: 1px solid #c5c5c5;
	border-radius: 5px;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 12px;
	color: #666666;
}

.input {
	width:240px;
	padding:0 4px;
	height:33px;
	border:1px solid #c5c5c5;
	border-radius:5px;
	font-family:Arial, Helvetica, sans-serif; 
	font-size:12px; 
	color:#666666;
}
</style>

</head>

<body>
	<table width="620" border="0" align="center"
		style="background: url(../img/bg_texure.png) 0 0;" cellspacing="0"
		cellpadding="0">
		<tr>
			<td height="550" align="center" valign="top" style="padding: 50px 0">

				<form name="unsubForm" method="post"
					action="PintoRanch_Unsubscribe.jsp"<%-- onsubmit="return <%=request.getAttribute("weight") == null ? "calWeight();" :"deselectAllWeight();"%>" --%> >

					<table style="background: #FFFFFF; border: 1px solid #c5c5c5;"
						width="540" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td height="60" align="left" valign="bottom"
								style="padding-left: 15px;">
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<%
										GenericUnsubscribeReportUpdateHelper genericUnsubscribeReportUpdateHelper = new GenericUnsubscribeReportUpdateHelper();
										GenericUnsubscribeRequest genericUnsubscribeRequest = new GenericUnsubscribeRequest();
										genericUnsubscribeRequest.setEmailId(request.getParameter("emailId")!=null?request.getParameter("emailId").trim():null);
										genericUnsubscribeRequest.setAction(request.getParameter("action"));
										if(request.getParameter("userId") == null ||  request.getParameter("userId").isEmpty()  ){
										
										//UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName("usersDao");
				
											PintoRanch_UnsubscribeHelper pintoranch_Unsubscribehelper = new PintoRanch_UnsubscribeHelper();
											Users user=pintoranch_Unsubscribehelper.setFlagValue();

										
										//Users user = usersDao.getUserByGenericUnsubUrl("PintoRanch_Unsubscribe.jsp");
				
										if(user == null) return;
										genericUnsubscribeRequest.setUserId(user.getUserId());
										}else{
											genericUnsubscribeRequest.setUserId(Long.valueOf(request.getParameter("userId").trim()));
										}
										genericUnsubscribeRequest.setReason(request.getParameter("reason"));
										GenericUnsubscribeResponse genericUnsubscribeResponse = (GenericUnsubscribeResponse)genericUnsubscribeReportUpdateHelper.processRequest(genericUnsubscribeRequest);	
										if (genericUnsubscribeResponse.getResponseType() != null
												&& genericUnsubscribeResponse.getResponseType()
														.equals(OCConstants.RESPONSE_TYPE_UNSUBSCRIBE_REQ)) {
										%>
										<td height="50" valign="bottom"
											style="font-family: Georgia, 'Times New Roman', Times, serif; font-size: 16px; font-weight: bold; color: #333333; background: url(../img/head-bottom.jpg) no-repeat left bottom; padding-bottom: 10px;">
											Unsubscribe Request</td>
										<%		request.setAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE,
											Constants.UNSUBSCRIBE_REQUEST_VALUE_UNSUB_UPDATE);
										%>
										<tr>
										<td height="340" valign="top" class="labelcell"><table
										width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
										<td height="60">
											<div
												style="padding-bottom: 10px; font-weight: bold; font-size: 12px; color: #333333;">
												Please enter the email address you would like to unsubscribe from Pinto Ranch's mailing list:
											</div>
										</td>
									</tr>
									<tr>
										<td height="40">
											<input size=25 type="text"  class="input"  name="emailId" autocomplete="off"  placeholder="Email Address" />
										</td>
									</tr>
									<tr>
										<td height="42" align="left" valign="middle">Reason for
											unsubscribing (optional):<br></br>
										</td>
									</tr>
									<tr>
										<td height="25" align="left" valign="middle"><textarea
												class="txtarea" name="reason" id="textarea" cols="45"
												rows="5"></textarea></td>
									</tr>
									
									<tr>
									<td height="100" align="left" valign="middle"><input
											type="submit" class="unsubscribeBtn" name="button"
											id="button" value="Unsubscribe" onclick="return vallidateEmail()"/></td>
									</tr>
									<tr><tr>
										<td><input type="hidden" name="userId"
											value="<%=genericUnsubscribeResponse.getUserId()%>" />
											</td>
									</tr>
								</table></td>
								</tr>
										<%
											} else if (genericUnsubscribeResponse.getResponseType() != null
													&& genericUnsubscribeResponse.getResponseType()
													.equals(OCConstants.RESPONSE_TYPE_UNSUBSCRIBE_UPDATE)) {
												request.setAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE,Constants.UNSUBSCRIBE_REQUEST_VALUE_RESUB_UPDATE);
										%>
										<tr>
										<td height="50" valign="bottom"
											style="font-family: Georgia, 'Times New Roman', Times, serif; font-size: 16px; font-weight: bold; color: #333333; background: url(../img/head-bottom.jpg) no-repeat left bottom; padding-bottom: 10px;">Unsubscribed
											Successfully</td></tr>
											<tr>
							<td height="70" style="padding-left: 15px;">
								<div
									style="padding-bottom: 20px; padding-top: 20px; color: #333333; font-weight: bold; font-family: Arial, Helvetica, sans-serif; font-size: 12px;">
									You have successfully unsubscribed email ID<font
										color="#0000000"
										style="font-weight: bold; text-decoration: underline; font-family: Arial, Helvetica, sans-serif;">
										<%=genericUnsubscribeResponse.getEmailId()%></font> from this
									subscription list.<br> <br> You will no longer
									receive emails from us.
								</div>
							</td>
							<tr>
							<td height="100" align="center" valign="middle">
								<div
									style="font-family: Arial, Helvetica, sans-serif; font-size: 12px; background: #e7f5f8; border: 1px solid #bfe6ef; color: #333333; margin: 0 10px; padding: 10px;">
									Did you unsubscribe by accident? Click on the Re-subscribe
									button below.<br /> <br /> <input name="submit" type="submit"
										name="button" id="button" value="Re-subscribe"
										class="resubscribeBtn" />
								</div>
							</td>
						</tr>
						<tr>
							<td height="30" align="left" valign="middle">&nbsp;</td>
						</tr>
						<tr>
						<td><input type="hidden" name="userId"
											value="<%=genericUnsubscribeResponse.getUserId()%>" />
											 <input type="hidden" name="emailId" 
											 value="<%=genericUnsubscribeResponse.getEmailId()%>" />
											 <input	type="hidden" name="unsubReqType" value="unsubUpdate" /></td>
						</tr>
										<%
											request.setAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE,Constants.UNSUBSCRIBE_REQUEST_VALUE_RESUB_UPDATE);
											} else if (genericUnsubscribeResponse.getResponseType() != null
													&& genericUnsubscribeResponse.getResponseType()
													.equals(OCConstants.RESPONSE_TYPE_RESUBSCRIBE)) {
										%>
										<tr>
										<td height="50" valign="bottom"
											style="font-family: Georgia, 'Times New Roman', Times, serif; font-size: 16px; font-weight: bold; color: #333333; background: url(../img/head-bottom.jpg) no-repeat left bottom; padding-bottom: 10px;">Re-subscription
											Successful!</td></tr>
											<tr>
									<td height="150" align="center" valign="middle"><div
									style="font-family: Arial, Helvetica, sans-serif; font-size: 12px; background: #f0ffde; border: 1px solid #c4eb96; color: #333333; margin: 0 10px; padding: 20px;">Thank
									you for your interest in receiving emails from us. You'll be
									hearing from us soon.</div></td></tr>
										<%
										request.setAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE,
												OCConstants.RESPONSE_TYPE_RESUBSCRIBE);
											}
										%>
									</tr>
									
								</table>
							</td>
						</tr>
						<tr>
						<td>
											<input type ="hidden" name="action" 
											value="<%=request.getAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE)%>" />
						</td>
						</tr>
					</table>
				</form>
			</td>
		</tr>
		<!-- <tr>
				<td>
					<div id="footer">&copy;2012 OptCulture. All rights reserved. </div>
				</td>
			</tr> -->
	</table>
</body>

</html>