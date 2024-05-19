<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
<%@page import="org.mq.marketer.campaign.beans.Users"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
 <link type="text/css" href="css/optloginv1.css" rel="stylesheet" />
<%
///HttpServletRequest request =(HttpServletRequest) Executions.getCurrent().getNativeRequest();
// System.out.println("request obj is"+request);
HttpSession sessions =(HttpSession)request.getSession();
Users user = (Users)sessions.getAttribute("userObj");
// System.out.println("user obj is"+user);
// System.out.println("user name is"+user.getUserName());
// System.out.println("user password is"+user.getPassword());
sessions.invalidate();

%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>

<title>Account - Login</title>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link href='http://fonts.googleapis.com/css?family=Raleway:400,100,200,300,500,600,800,700,900' rel='stylesheet' type='text/css'>
	<style type="text/css">


#un,#pw,#orgid {
	border: 1px solid #999999;
	color: #242424;
	font-family: "Lucida Console", Monaco, monospace;
	font-size: 14px;
	font-weight: normal;
	height: 23px;
	padding: 5px 5px 0;
}

#mainContainer {
	width: 100%;
}

#capheader {
	width: 100%;
	border-bottom: 1px solid #b6b6b8;
	position: relative;
	display: block;
	overflow: hidden;
}

.caplogo {
	float: left;
	padding: 5px 15px;
}

.qLinks {
	float: right;
	padding: 55px 15px 5px 15px;
	color: #6d6e71;
}

.qLinks a {
	color: #6d6e71;
	font: 12px arial;
	text-decoration: none;
}

.qLinks a:hover {
	color: #3a879f;
	font: 12px arial;
	text-decoration: none;
}

#loginArea {
	width: 620px;
	height: 300px;
	margin: 0 auto 40px;
	padding-top: 80px;
	background: url(img/loginBG.jpg) no-repeat center center;
	font: 12px verdana;
	color: #FFF;
}

.err {
	width: 330px;
	font: 18px arial;
	color: #ed1c24;
	margin: 5px auto;
	padding: 0px;
	text-align: center;
}

fieldset {
	padding: 0px 15px;
	text-align: center;
	color: #F00;
}

.but {
	font: 11px arial bold;
	color: #FFFFFF;
	background-color: #275b6d;
	padding: 5px 10px;
	text-transform: uppercase;
	border: 0px;
}

#footer {
	background: url(img/fotterBG.gif) no-repeat center center;
	padding: 10px 0px;
	font: 11px verdana;
	color: #6d6e71;
	margin-top: 40px;
	text-align: center;
}

.shadow {
	background: url(img/shadow_under.gif) repeat-x center top;
	height: 20px;
}

#errorDivId {
	font-style: arial;
	color: red;
	font-size: 11px;
}

#dashboard-login {
    background-size: 58% 310px;
}

#dashboard-login #header {
    margin-top: 15px;
	padding: 15px 30px 12px;
}

.form-footer {
	border-top: 2px solid #D0DDEC;
	height: 90px;
	text-aligh: center;
}
#dashboard-login #header h2{
	padding-top: 23px;
}



</style>
</head>
<body>
<%-- <%
		String ua = request.getHeader("User-Agent");
			boolean isSupported = (ua != null && ua.indexOf("MSIE") != -1)
					|| (ua != null && ua.indexOf("Firefox") != -1)
					|| (ua != null && ua.indexOf("Chrome") != -1);
	%>

<%
		String reqUrl = request.getRequestURL().toString();
		if (reqUrl.toLowerCase().contains("localhost") || reqUrl.toLowerCase().contains("captiway.us") || 
				reqUrl.toLowerCase().contains("optculture") 
				|| reqUrl.toLowerCase().contains("oc.magnaquest") || reqUrl.toLowerCase().contains("captiway.com")) {
	%> --%>
	<link rel="shortcut icon" type="image/x-icon"
		href="img/favicon.ico" />
<link rel="icon" type="image/x-icon" href="img/favicon.ico" />


	<div id="account-login" class="login-container">


		<form name="f" action="<c:url value='j_spring_security_check'/>"
			method="post" onsubmit="return login();">

			<div id="centralize">

				<div id="dashboard-login" class="rounded" >
				   <div class="right-container">
						<div class="logo"><img src="img/oc-logo.png" alt="OptCulture" /></div>
						<div class="hd-small">Engage your Customers.</div>
						<div class="hd-big">Encourage <span class="brand-color">Repeat Visits.</span> </div>
					</div>

					<div class="form-container rounded" >

						<div id="header" class="rounded top">
							<a href="login.jsp" class="logo"><img src="img/oc-logo.png" alt="OptCulture"></a>
							<h2>Account Login</h2>
							
						</div>
						
					  	<div id="redirect-text">You will be logged out of your account and will be logged into <%=user.getUserName() %> user's account. 
											</br></br>Click OK to proceed.</div>  
						<!-- end #header -->

						<div class="the-form">
						
						<table cellpadding="2" border="0" cellspacing="2" sclass=".myHeight">
								
								<tr>
									<td height="0" width="100"><label for="UserName"/></td>
									<td height="0" ><input name='j_username' type="hidden" value="<%=user.getUserName() %>" /></td>
								</tr>
							 <!-- 	<tr>
									<td colspan="2"><div>You will be logged out of your account and will be logged into <%=user.getUserName() %> user's account. 
											Click OK to proceed.</div></td>  -->
								</tr> 
								<tr>
									<td height="0" width="100"><label for="Password"/></td>
									<td height="0" ><input name='j_password' type="hidden" value="<%=user.getPassword() %>" /></td>
								</tr>
								
							</table>
							
					     <!--    <label for="UserName">Username</label>
							<input name='temp_username' type="text" id="un" disabled/> 
							<input name='j_username' type="hidden" id="hidden_un" />
							
							<label for="Password">Password</label>
							<input name='j_password' type="password" id="pw" disabled/>
							
							<label>Organization ID</label>
							<input name='j_orgid' type="text" id="orgid" disabled/>  -->
							
							<div id="login-submit" class="form-footer rounded bottom">
							<div id="login-submit">
							<input value="OK" class="button button_green"
								type="submit">
						</div>
						</div>
							
						 <!-- 	<div id="login-submit">
								<input value="Login to OptCulture" class="button button_green" type="submit">
							</div>  -->
							
						 	  



						</div>
					<!-- 	<div class="form-footer rounded bottom">
							<div id="bottom-links" class="rounded">
								<!-- <input value="true" name="RememberMe" id="RememberMe"type="checkbox">
								<input value="false" name="RememberMe"type="hidden"> <label for="RememberMe">Remember me?</label> | 
								<a href="forgotPassword.jsp">Forgot Your Password?</a>
							</div>
						</div>  -->


						
					<!--   	<div id="login-submit" class="form-footer rounded bottom">
							<input value="OK" class="button button_green"
								type="submit">
						</div> -->


					</div>
					<!-- end .form-container -->
					<div class="clear"></div>

				</div>
				<!-- end #dashboard-login -->



				
			</div>
			<!-- end #centralize -->

		</form>


	<!-- 	<div id="rays" style="position: absolute; z-index: 1000;"></div>  -->

	
	</div>
	<!-- end #login-container -->
</body>
</html>
