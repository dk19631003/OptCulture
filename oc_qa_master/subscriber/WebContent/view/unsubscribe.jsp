
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri='http://java.sun.com/jstl/core_rt'%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="org.mq.marketer.campaign.general.Constants"%>
<%@page import="java.util.List"%>
<%@page import="org.mq.marketer.campaign.beans.TemplateCategory"%>

<%@page import="org.mq.optculture.utils.ServiceLocator"%>
<%@page import="org.mq.marketer.campaign.beans.CampaignSent"%>
<%@page import="org.mq.marketer.campaign.dao.CampaignSentDao"%>
<%@page import="org.mq.marketer.campaign.dao.ContactsDao"%>
<%@page import="org.mq.optculture.utils.OCConstants"%>
<%@page import="org.mq.marketer.campaign.beans.Contacts"%>
<%@ page import="org.apache.logging.log4j.Logger" %>
<%@ page import="org.apache.logging.log4j.LogManager" %>
<%@page import="org.mq.marketer.campaign.general.PropertyUtil"%>
<%@page import="org.mq.marketer.campaign.general.EncryptDecryptUrlParameters" %>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="shortcut icon" type="image/x-icon" href="img/favicon.ico" />
<link rel="icon" type="image/x-icon" href="img/favicon.ico" />
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
	font-size: 12px;
	font-weight: bold;
	background: url(img/unsub_v_1.png) 0 0 no-repeat;
	width: 171px;
	height: 41px;
	border: 0 none;
}


.updatePreferencesBtn {
	color: #FFFFFF;
	font-size: 12px;
	font-weight: bold;
	background: url(img/spc_v_1.png) 0 0 no-repeat;
	width: 171px;
	height: 41px;
	border: 0 none;
}

.resubscribeBtn {
	color: #FFFFFF;
	font-size: 16px;
	font-weight: bold;
	background: url(img/btn2.jpg) 0 0 no-repeat;
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
 .platformHide
 {
 display:none;
 }
</style>

</head>

<body>
	<table width="620" border="0" align="center"
		style="background: url(img/bg_texure.png) 0 0;" cellspacing="0"
		cellpadding="0">
		<tr>
			<td height="550" align="center" valign="top" style="padding: 50px 0">

				<form name="unsubForm" method="post"
					action="https://qcapp.optculture.com/subscriber/updateReport.mqrm?action=unsubReason" <%-- onsubmit="return <%=request.getAttribute("weight") == null ? "calWeight();" :"deselectAllWeight();"%>" --%> >

					<%-- <input type="hidden" name="weight" id="weight"/>
							<input type="hidden" name="userId" value="<%=request.getAttribute(Constants.QS_USERID) %>"/>
							<input type="hidden" name="crId" value="<%=request.getAttribute(Constants.QS_CRID) %>"/>
							<input type="hidden" name="sentId" value="<%=request.getAttribute(Constants.QS_SENTID) %>"/>
							<input type="hidden" name="emailId" value="<%=request.getAttribute(Constants.QS_EMAIL) %>"/> --%>


					<%-- <input type="hidden" name="sentId"
						value="<%=request.getAttribute(Constants.QS_SENTID)%>" /> <input
						type="hidden" name="emailId"
						value="<%=request.getAttribute(Constants.QS_EMAIL)%>" /> <input
						type="hidden" name="<%=Constants.UNSUBSCRIBE_REQUEST_TYPE%>"
						value="<%=request
					.getAttribute(Constants.UNSUBSCRIBE_REQUEST_VALUE_UNSUB)%>" /> --%>
					<table style="background: #FFFFFF; border: 1px solid #c5c5c5;"
						width="540" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td height="60" align="left" valign="bottom"
								style="padding-left: 15px;">
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<%
											if (request.getAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE) != null
													&& ((String) request
															.getAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE))
															.equals(Constants.UNSUBSCRIBE_REQUEST_VALUE_UNSUB)) {
										%>
										<td
											style="font-family: Georgia, 'Times New Roman', Times, serif; font-size: 16px; font-weight: bold; color: #333333; background: url(img/head-bottom.jpg) no-repeat left bottom; padding-bottom: 10px;">Confirm
											Unsubscribe Request</td>
										<%
											} else if ((request
													.getAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE) != null)
													&& ((String) request
															.getAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE))
															.equals(Constants.UNSUBSCRIBE_REQUEST_VALUE_UNSUB_UPDATE)) {
										%>
										<td
											style="font-family: Georgia, 'Times New Roman', Times, serif; font-size: 16px; font-weight: bold; color: #333333; background: url(img/head-bottom.jpg) no-repeat left bottom; padding-bottom: 10px;">Unsubscribed
											Successfully</td>
										<%
											} else if ((request
													.getAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE) != null)
													&& ((String) request
															.getAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE))
															.equals(Constants.UNSUBSCRIBE_REQUEST_VALUE_RESUB)) {
										%>
										<td height="50" valign="bottom"
											style="font-family: Georgia, 'Times New Roman', Times, serif; font-size: 16px; font-weight: bold; color: #333333; background: url(img/head-bottom.jpg) no-repeat left bottom; padding-bottom: 10px;">Re-subscription
											Successful!</td>
										<%
											}
										%>
									</tr>
								</table>
							</td>
						</tr>

						<tr>
							<%
								
								if ((request.getAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE) != null)
										&& ((String) request
												.getAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE))
												.equals(Constants.UNSUBSCRIBE_REQUEST_VALUE_UNSUB_UPDATE)) {
									
									request.setAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE,Constants.UNSUBSCRIBE_REQUEST_VALUE_RESUB_UPDATE);
							%>
							<td height="70" style="padding-left: 15px;">
								<div
									style="padding-bottom: 20px; padding-top: 20px; color: #333333; font-weight: bold; font-family: Arial, Helvetica, sans-serif; font-size: 12px;">
									You have successfully unsubscribed email ID<font
										color="#0000000"
										style="font-weight: bold; text-decoration: underline; font-family: Arial, Helvetica, sans-serif;">
										<%=request.getAttribute(Constants.QS_EMAIL)%></font> from this
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
								value="<%=request.getAttribute(Constants.QS_USERID)%> " /> <input
								type="hidden" name="sentId"
								value="<%=request.getAttribute(Constants.QS_SENTID)%>" /> <input
								type="hidden" name="emailId"
								value="<%=request.getAttribute(Constants.QS_EMAIL)%>" /> <input
								type="hidden" name="unsubReqType" value="reSubscribe" />
												</td>

						</tr>
							<%
							
								} else if ((request
										.getAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE) != null)
										&& ((String) request
												.getAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE))
												.equals(Constants.UNSUBSCRIBE_REQUEST_VALUE_RESUB_UPDATE)) {
							%>
						<tr>
							<td height="150" align="center" valign="middle"><div
									style="font-family: Arial, Helvetica, sans-serif; font-size: 12px; background: #f0ffde; border: 1px solid #c4eb96; color: #333333; margin: 0 10px; padding: 20px;">Thank
									you for your interest in receiving emails from us. You'll be
									hearing from us soon.</div></td>
							<%
								}
							%>
						</tr>


						<%
							//if(request.getAttribute("weight") != null && (Short.parseShort(request.getAttribute("weight"))) > 0) {
						
							if (request.getAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE) != null
									&& Constants.UNSUBSCRIBE_REQUEST_VALUE_RESUB.equals(request
											.getAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE))) {
								
								
									
									request.setAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE,
											Constants.UNSUBSCRIBE_REQUEST_VALUE_RESUB_UPDATE);
							
						%>

						

						<%
							} /* else if(request.getAttribute("weight") == null) */

							//Constants.UNSUBSCRIBE_REQUEST_TYPE, Constants.UNSUBSCRIBE_REQUEST_VALUE_UNSUB
							if (request.getAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE) != null
									&& Constants.UNSUBSCRIBE_REQUEST_VALUE_UNSUB.equals(request
											.getAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE))) {
								
								request.setAttribute(Constants.UNSUBSCRIBE_REQUEST_TYPE,
										Constants.UNSUBSCRIBE_REQUEST_VALUE_UNSUB_UPDATE);
						%>
						

						<tr>
							<td height="340" valign="top" class="labelcell"><table
									width="100%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td height="60">
											<div
												style="padding-bottom: 10px; font-size: 12px; color: #333333;">
												You have chosen to unsubscribe email ID <font
													color="#000000"
													style="text-decoration: underline; font-family: Arial, Helvetica, sans-serif;"><%=request.getAttribute(Constants.QS_EMAIL)%></font>
												from this subscription list.
											</div>
										</td>
									</tr>
									<tr>
										<td height="40">
											<div
												style="padding-bottom: 10px; font-size: 12px; color: #333333;">To
												complete your unsubscribe request, please click on the
												 
												<font color="#000000"
													style=" font-weight: bold; font-family: Arial, Helvetica, sans-serif;"> "Unsubscribe"</font>
												button below.
												</div>
										</td>
									</tr>
									<tr>
										<td height="40">
											<div
												style="padding-bottom: 10px; font-size: 12px; color: #333333;">
												Instead, you can click on 
												 
												<font color="#000000"
													style=" font-weight: bold; font-family: Arial, Helvetica, sans-serif;"> "Update Preferences"</font>
												, and choose the type of emails or frequency that will match your preferences.
												</div>
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
										<td><input type="hidden" name="userId"
											value="<%=request.getAttribute(Constants.QS_USERID)%> " />
											<input type="hidden" name="sentId"
											value="<%=request.getAttribute(Constants.QS_SENTID)%>" />
											 <input type="hidden" name="emailId" 
											 value="<%=request.getAttribute(Constants.QS_EMAIL)%>" />
											 <input	type="hidden" name="unsubReqType" value="unsubUpdate" />
											 <input type="hidden" name="cId" value="<%=request.getAttribute(Constants.QS_CID) != null ? request.getAttribute(Constants.QS_CID) : null %>"/>
		    						<input type="hidden" name="campaignId" value="<%=request.getAttribute("campaignId") != null ? request.getAttribute("campaignId") : "" %>"/>    				    			
				    				<input type="hidden" name="CampaignSource" value="<%=request.getAttribute("CampaignSource") != null ? request.getAttribute("CampaignSource") : "" %>"/>
							    	<input type="hidden" name="crId" value="<%=request.getAttribute("crId") != null ? request.getAttribute("crId") : "" %>"/>
			
										</td>
									</tr>
									<tr>
									<td height="100" align="left" valign="middle" width="50"><input
											type="submit" class="unsubscribeBtn" name="button"
											id="button" value="Unsubscribe" /></td>
		
									<%	
									
									try{
										
										CampaignSent campaignSent =null;
										Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
										
										Long sent_Id = (Long)request.getAttribute(Constants.QS_SENTID);
										String cId=null; 
										String sentId=null; 
										
										
										
										String updateSubsLink =  null;	
										CampaignSentDao campaignSentDao = (CampaignSentDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CAMPAIGN_SENT_DAO);
										campaignSent = campaignSentDao .findById(sent_Id);
										if(campaignSent != null) {
												
										ContactsDao contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
										Contacts contact = contactsDao.findById(campaignSent.getContactId());
										//contact!=null && contact.getUsers().getSubscriptionEnable()
										if(contact!=null && contact.getUsers().getSubscriptionEnable()){
											updateSubsLink=PropertyUtil.getPropertyValue("updateSubscriptionLink");
											updateSubsLink =updateSubsLink.replace("|^", "[").replace("^|", "]");
											
											if(sent_Id!=null)
												updateSubsLink = updateSubsLink.replace("[sentId]", EncryptDecryptUrlParameters.encrypt(sent_Id.toString()));
											if(campaignSent!=null && campaignSent.getContactId()!=null)
												updateSubsLink = updateSubsLink.replace("[cId]", EncryptDecryptUrlParameters.encrypt(campaignSent.getContactId().toString()));
												
											
											
											request.setAttribute("updateSubscriptionLink",updateSubsLink);
											logger.info("request.getAttribute(updateSubscriptionLink) :" +request.getAttribute("updateSubscriptionLink"));
									%>	
									
									
									
											 
											<td height="100" align="center" valign="middle">
											<a href="<%=request.getAttribute("updateSubscriptionLink") %>">
  													 <%-- <input type="button" class="resubscribeBtn" value="updateSubscriptionPreferenceForm" onclick=" <%
  													logger.info("request.getAttribute :" +request.getAttribute("qqqqq"));	
  													 RequestDispatcher reqDispatcher = getServletContext().getRequestDispatcher(request.getAttribute("qqqqq").toString());
  													 reqDispatcher.forward(request, response);
			%>"  /> --%>
											<input type="button" class="updatePreferencesBtn" value="Update Preferences" />
											</a>
											<!-- <input	type="submit" class="unsubscribeBtn" name="button"
											id="button" value="sPC" /> -->
					
					
											</td>
									
									<% 	
										}
									
										}	
									}catch (Exception e) {
										//TODO check with madam
										Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
										
										logger.error("Exception : Problem while getting the SPC URL \n", e);
									}
									
									

									
									%>
		
								</tr>

								</table></td>
						</tr>
						<%
							}
						%>

					</table>
				</form>
								

			

		</tr>
		<!-- <tr>
				<td>
					<div id="footer">&copy;2012 OptCulture. All rights reserved. </div>
				</td>
			</tr> -->
	</table>
</body>

</html>
