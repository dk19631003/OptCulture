<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%> 
<%@ taglib prefix="c" uri='http://java.sun.com/jstl/core_rt' %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="org.mq.captiway.scheduler.utility.Constants"%>
<%@page import="java.util.List"%>
<%@page import="org.mq.captiway.scheduler.beans.TemplateCategory"%>
<html>
 <head>
   <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Unsubscribe Request</title>
	 <script type="text/javascript">
				
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
	 </script>
	 <style type="text/css">
				#categoryTableId {
					font-family:arial,verdana;
					font-size:15px;
					font-weight:bold;
				}
				#footer {
					padding: 30px 0px;
					font: 11px verdana;
					color: #6d6e71;
					text-align: center;
				}
				
				.unsubscribeBtn {
					color:#FFFFFF;
					font-size:16px;
					font-weight:bold;
					background:url(img/btn-1.jpg) 0 0 no-repeat;
					width:207px;
					height:49px;
					border:0 none;
				}
				.resubscribeBtn {
					color:#FFFFFF;
					font-size:16px;
					font-weight:bold;
					background:url(img/btn2.jpg) 0 0 no-repeat;
					width:207px;
					height:49px;
					border:0 none;
				}
				.txtarea {
					width:290px;
					height:90px;
					padding:5px;
					border:1px solid #c5c5c5;
					border-radius:5px;
					font-family:Arial, Helvetica, sans-serif; 
					font-size:12px; 
					color:#666666;
			}
			.labelcell{
				padding-left:15px; 
				font-family:Arial, Helvetica, sans-serif; 
				font-size:12px; 
				color:#666666;
			}
			.sme{
			border:1px solid #c5c5c5;
			border-radius:5px;
			padding:10px;
			margin-top:20px;
			width:510px;
			}

		.inputsm {
				width:200px;
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
		<table width="620" border="0" align="center" style="background:url(img/bg_texure.png) 0 0;" cellspacing="0" cellpadding="0">
		  <tr>
		    <td height="550" align="center" valign="top" style="padding:50px 0">
		    
		    <form name="unsubForm" method="post" action="https://qcapp.optculture.com/subscriber/updateReport.mqrm?action=unsubReason" 
							onsubmit="return <%=request.getParameter("weight") == null ? "calWeight();" :"deselectAllWeight();"%>" >
					
							<input type="hidden" name="weight" id="weight"/>
							<input type="hidden" name="userId" value="<%=request.getAttribute(Constants.QS_USERID) %>"/>
							<input type="hidden" name="crId" value="<%=request.getAttribute(Constants.QS_CRID) %>"/>
							<input type="hidden" name="sentId" value="<%=request.getAttribute(Constants.QS_SENTID) %>"/>
							<input type="hidden" name="emailId" value="<%=request.getAttribute(Constants.QS_EMAIL) %>"/>
		    
		    
		    	<table style="background:#FFFFFF; border:1px solid #c5c5c5;" width="540" border="0" align="center" cellpadding="0" cellspacing="0">
			      <tr>
			        <td height="60" align="left" valign="bottom" style="padding-left:15px;">
			        	<table width="100%" border="0" cellspacing="0" cellpadding="0">
			            	<tr>
			            			<% if( request.getAttribute("message") != null && ((String)request.getAttribute("message")).equals(Constants._UNSUBSCRIBE_REQUEST_MSG) ) {%>
			              		<td style="font-family:Georgia, 'Times New Roman', Times, serif; font-size:16px; font-weight:bold; color:#333333; background:url(img/head-bottom.jpg) no-repeat left bottom; padding-bottom:10px;">Confirm Unsubscribe Request</td>
			              		<% }
			              		 else if( (request.getAttribute("message") != null) && ((String)request.getAttribute("message")).equals(Constants._UNSUBSCRIBE_RESPONSE_MSG) ) {%>
			              		 <td style="font-family:Georgia, 'Times New Roman', Times, serif; font-size:16px; font-weight:bold; color:#333333; background:url(img/head-bottom.jpg) no-repeat left bottom; padding-bottom:10px;">Unsubscribed Successfully</td>
			              		 <% } 
			              		 else if( (request.getAttribute("message") != null) && ((String)request.getAttribute("message")).equals(Constants._RESUBSCRIBE_RESPONSE_MSG) ) {%>
			              		  <td height="50" valign="bottom" style="font-family:Georgia, 'Times New Roman', Times, serif; font-size:16px; font-weight:bold; color:#333333; background:url(img/head-bottom.jpg) no-repeat left bottom; padding-bottom:10px;">Re-subscription Successful!</td>
			              		 <% } %>
			            	</tr>
			          </table></td>
			      </tr>
			      
			      <tr>
						<%  if( (request.getAttribute("message") != null) && ((String)request.getAttribute("message")).equals(Constants._UNSUBSCRIBE_RESPONSE_MSG) ) {%>
			              		  <td height="70" style="padding-left:15px;"> <div style="padding-bottom:20px;padding-top:20px;color:#333333;font-weight:bold; font-family: Arial,Helvetica,sans-serif;
    font-size: 12px;">You have successfully unsubscribed email ID<font color="#0000000" style="font-weight:bold;text-decoration:underline;font-family: Arial,Helvetica,sans-serif;"> <%=request.getAttribute(Constants.QS_EMAIL) %></font>  from this subscription list.<br><br> You will no longer receive 
									emails from us.</div></td>
			              		 <% } 
			              		 else if( (request.getAttribute("message") != null) && ((String)request.getAttribute("message")).equals(Constants._RESUBSCRIBE_RESPONSE_MSG) ) {%>
			              		 
			              		  <td height="150" align="center" valign="middle"><div style="font-family: Arial,Helvetica,sans-serif;font-size: 12px;background:#f0ffde; border:1px solid #c4eb96; color:#333333;margin: 0 10px; padding: 20px;">Thank you for your interest in receiving emails from us. You'll be hearing from us soon.</div></td>
			              		 <% } %>
				 </tr>
			      <tr>
			      
								<td>
									<fieldset width="100%"  style="margin:10px 0;display:none;">
									<legend>Categories</legend>
									
									<table id="categoryTableId" cellpadding="5" cellspacing="0" width="100%" style="margin:10px 0;">
									
										<%
											List<TemplateCategory> categoryList = (List<TemplateCategory>)
												                    request.getAttribute(Constants._CATEGORIES);
											TemplateCategory cat1, cat2; 
												for(int i=0; i<categoryList.size();){
												cat1 = categoryList.get(i++);
										%>
														<tr>
															<td>
																<input type="checkbox" name="category" value="<%=cat1.getWeightage() %>" checked="checked" />
																<%=cat1.getCategoryName()%>
															</td>
															<td>
															<%
																if(i<categoryList.size()) {
																	cat2 = categoryList.get(i++); %>
																<input type="checkbox" name="category" value="<%=cat2.getWeightage() %>"  checked="checked"  />
																<%=cat2.getCategoryName()%>
															</td>
														</tr>
														<% }
										}%>
									</table>
									</fieldset>
								</td>
							</tr>
							
						
							  <%
							 	if(request.getParameter("weight") != null && (Short.parseShort(request.getParameter("weight"))) > 0) {  %>
								 	
							 	     <tr >
							        	<td height="100" align="center" valign="middle" >
							        	<div style="font-family: Arial,Helvetica,sans-serif;font-size: 12px;background:#e7f5f8; border:1px solid #bfe6ef; color:#333333;margin: 0 10px; padding: 10px;">
							        	Did you unsubscribe by accident? Click on the Re-subscribe button below.<br />
              							<br/>
  
							 				<input name="submit" type="submit" name="button" id="button" value="Re-subscribe" class="resubscribeBtn" />
							        	</div>
							 		    </td>
							 		 </tr> 
							 		  <tr>
							           	<td height="30" align="left" valign="middle">&nbsp;</td>
							           </tr>  
							 		
							 <% } else if(request.getParameter("weight") == null) { %>
			      
			  
			      <tr>
			        <td height="340" valign="top" class="labelcell"><table width="100%" border="0" cellspacing="0" cellpadding="0">
			        	<tr>
			        		<td height="60"> <div style="padding-bottom:10px; font-weight:bold; font-size:12px; color:#333333;">You are choosing to unsubscribe email ID <font color="#000000" style="font-weight:bold;text-decoration:underline;font-family: Arial,Helvetica,sans-serif;"><%=request.getAttribute(Constants.QS_EMAIL) %></font> from this subscription list.</div></td>
			    		</tr>
				         <tr>
				            <td height="40"> <div style="padding-bottom:10px; font-weight:bold; font-size:12px; color:#333333;">To complete your unsubscribe request, please click on the "Unsubscribe" button below.</div></td>
				            </tr>
				         <tr>
			            	<td height="42" align="left" valign="middle">Reason for unsubscribing (optional):<br></br></td>
			            </tr>
			          	<tr>
			            	<td height="25" align="left" valign="middle"><textarea class="txtarea" name="textarea" id="textarea" cols="45" rows="5" ></textarea></td>
			           </tr>
			          <tr>
				            <td height="100" align="left" valign="middle"><input type="submit" 
				            class="unsubscribeBtn" name="button" id="button" value="Unsubscribe" /></td>
			            </tr>
			        </table></td>
			        </tr>
			            <% 
							 	}
							%>
			        
		    </table></form></td>
		  </tr>
		  <!-- <tr>
				<td>
					<div id="footer">&copy;2012 OptCulture. All rights reserved. </div>
				</td>
			</tr> -->
		</table>
</body>
	
</html>
