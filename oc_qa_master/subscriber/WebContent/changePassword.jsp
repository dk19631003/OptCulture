<%@page import="java.util.Calendar"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ page import="org.mq.marketer.campaign.beans.*" %>
<%@ page import="org.mq.marketer.campaign.dao.*" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@page import="org.mq.marketer.campaign.general.Constants"%>
<%@page import="org.springframework.security.authentication.encoding.Md5PasswordEncoder"%>
<%@page import="org.springframework.security.crypto.bcrypt.BCrypt"%>
<%@page import="org.mq.optculture.business.helper.ChangePasswordHelper"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
 <link type="text/css" href="css/optloginv1.css" rel="stylesheet" />



 <%
	 String token = request.getParameter("token");
	 Long userId = Long.parseLong(request.getParameter("userId"));
	 //request.setAttribute("token",token);
	// request.setAttribute("userId", userId.toString()); %>
	 
	<%! String flag=null; %> 
	 <%  // If process is true, attempt to validate and process the form
 if ("true".equals(request.getParameter("process"))) 
 {
	 /* ServletContext servletContext =this.getServletContext();
	 WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
	 
	 UsersDao usersDao = (UsersDao)wac.getBean("usersDao");
	 ResetPasswordTokenDao resetPasswordTokenDao = (ResetPasswordTokenDao)wac.getBean("resetPasswordTokenDao");
	 UsersDaoForDML usersDaoForDML = (UsersDaoForDML)wac.getBean("usersDaoForDML");
	 ResetPasswordTokenDaoForDML resetPasswordTokenDaoForDML = (ResetPasswordTokenDaoForDML)wac.getBean("resetPasswordTokenDaoForDML");
	 
	 
	 ResetPasswordToken tokenObj = resetPasswordTokenDao.findByTokenValue(token);
	 Users userObj = usersDao.findByUserId(userId);
	
	 String password = request.getParameter("password");
	// Md5PasswordEncoder md5 = new Md5PasswordEncoder();
	 //String hash = md5.encodePassword(password,userObj.getUserName());
	 String hash = BCrypt.hashpw(password, BCrypt.gensalt());
	 
	 userObj.setPassword(hash);
	 userObj.setMandatoryUpdatePwdOn(Calendar.getInstance());
	 usersDaoForDML.saveOrUpdate(userObj);
	
	 tokenObj.setStatus(Constants.TOKEN_STATUS_EXPIRED);
	 resetPasswordTokenDaoForDML.saveOrUpdate(tokenObj);
	 
	 flag = "<font style='color:green'>Password changed successfully.</font> <br/> <a href='/subscriber'>Click here to Login</a>"; */
	 String password = request.getParameter("password");
	 
	 ChangePasswordHelper changePasswordHelper = new ChangePasswordHelper();
	 flag= changePasswordHelper.setFlagValue(userId,token,password);
 	}
 %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>

<title>Reset Password</title>
<link rel="shortcut icon" type="image/x-icon" href="img/favicon.ico" />
<link rel="icon" type="image/x-icon" href="img/favicon.ico" />
<script type="text/javascript">
function validation() 
{     
		var password = document.getElementById('password');
		var rePassword = document.getElementById('rePassword');
		var pattern =new RegExp("^(?=.{8,50}$)(?=(.*[A-Z]))(?=(.*[a-z]))(?=(.*[0-9]))(?=(.*[-@!#$%^&-+=()])).*$");
		
		if(password.value == "") {
			document.getElementById('errorDivId').innerHTML = "";
        	document.getElementById('errorDivId').innerHTML="Password field cannot be empty.";
        	document.getElementById('password').focus();
        	
        	return false;
		}
		if(rePassword.value == "") {
			document.getElementById('errorDivId').innerHTML = "";
        	document.getElementById('errorDivId').innerHTML="Re-type Password field cannot be empty.";
        	document.getElementById('rePassword').focus();
        	return false;
		}
		
		if(password.value ==null || !pattern.test(password.value)) {
			document.getElementById('errorDivId').innerHTML = "";
        	document.getElementById('errorDivId').innerHTML="Password must contain at least 8 characters,1 uppercase,1 lowercase,1 special character (@!#$%^&+-=*'()) and 1 number.";
        	document.getElementById('password').focus();
			return false;
		}
		
		if(rePassword.value ==null || !pattern.test(rePassword.value)) {
			document.getElementById('errorDivId').innerHTML = "";
        	document.getElementById('errorDivId').innerHTML="Re-type Password must contain at least 8 characters,1 uppercase,1 lowercase,1 special character (@!#$%^&+-=*'()) and 1 number.";
        	document.getElementById('rePassword').focus();
			return false;
		}
		
		if(password.value!=rePassword.value) {
			document.getElementById('errorDivId').innerHTML = "";
        	document.getElementById('errorDivId').innerHTML="Both passwords should match.";
        	document.getElementById('rePassword').focus();
        	return false;
		}
	
            return true;
     
}
</script>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href='http://fonts.googleapis.com/css?family=Raleway:400,100,200,300,500,600,800,700,900' rel='stylesheet' type='text/css'>
	<style type="text/css">

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
	font-size: 12px;
	padding: 0px 35px 35px;
	
}
#dashboard-login {
    background-size: 58% 310px;
}

#dashboard-login #header {
    margin-top: 25px;
	padding: 15px 30px 12px;
}

.form-footer {
	border-top: 2px solid #D0DDEC;
	height: 90px;
	text-aligh: center;
	margin-top: 25px;
}
#dashboard-login #header h2{
	padding-top: 30px;
}


</style>
</head>
<body>
<%
 String reqUrl = request.getRequestURL().toString();
 if(reqUrl.toLowerCase().contains("captiway.us") || reqUrl.toLowerCase().contains("optculture") ||
		 reqUrl.toLowerCase().contains("captiway.com") || reqUrl.toLowerCase().contains("captiway.biz") ||
		 reqUrl.toLowerCase().contains("mailhandler01.info") || reqUrl.toLowerCase().contains("localhost") || reqUrl.toLowerCase().contains("123.176.39.104")) {
 %>



<div id="account-login" class="login-container"> 
	

<form method="POST" style="margin:0;" onsubmit="return validation();">


 			
<div id="centralize" >

<div id="dashboard-login" class="rounded">
<div class="right-container">
						<div class="logo"><img src="img/oc-logo.png" alt="OptCulture" /></div>
						<div class="hd-small">Engage your Customers.</div>
						<div class="hd-big">Encourage <span class="brand-color">Repeat Visits.</span> </div>
					</div>

	<div class="form-container rounded">   	
        
        <div id="header" class="rounded top">
        	<a href="" class="logo"><img src="images/oc-logo.png" alt="OptCulture"></a>
            <h2>Reset Password</h2>
            <div class="clear"></div>
        </div><!-- end #header -->
    	<div id="errorDivId" style="height:20px; text-align: center; margin-top: 10px; display:block;" align="center">
					 		<% if(flag!=null) {  
					 			 out.println(flag); 
					 			flag=null;
					 			}
					 		else {
					 			out.println("");
					 		}
					 		%> 
		</div>

        <div class="the-form">
                <table cellpadding="2" border="0" cellspacing="2">
                
					<!-- <tr>
						<td  colspan="2">
						
						
	                	<div style="font-style: arial; font-size: 11px; ">
							Please enter your username and organization id,<br/>
							and details on how to reset your password would be sent to you
						</div>
						
						</td>
						
                    </tr>   -->   
                     
                    <tr>
						<td width="150" style="font-style: arial; font-size: 12px;"><label >Password</label></td>
						<td><input name='password' type="password" id="password" style="height: 20px; padding: 3px;" /></td>
					</tr>
					<tr>
						<td width="150" style="font-style: arial; font-size: 12px;"><label>Re-type Password</label></td>
						<td><input name='rePassword' type="password" id="rePassword" style="height: 20px; padding: 3px;" />
							<input type="HIDDEN" name="process" value="true" />
						</td>
					</tr>                         
                    	
				</table>
                    <div id="login-submit" class="form-footer rounded bottom">
                <input type="submit" name="submit"  value="Reset Password" class="button button_green"   />
            </div>
             
                    
                </div> 
       
    
    </div><!-- end .form-container -->
    <div class="clear"></div>
</div><!-- end #dashboard-login -->




</div><!-- end #centralize -->

</form>


<div id="rays" style="position:absolute;z-index:1000;"></div>

</div><!-- end #login-container -->  
<% } %>
</body>
</html>
