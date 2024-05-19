<%@page import="org.zkoss.zul.South"%>
<%@page import="org.mq.marketer.campaign.general.Utility"%>
<%@page import="org.mq.marketer.campaign.general.Constants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.mq.marketer.campaign.beans.*" %>
<%@ page import="org.mq.marketer.campaign.dao.*" %>
<%@ page import="org.mq.marketer.campaign.controller.*" %>
<%@ page import="org.springframework.beans.factory.xml.XmlBeanFactory" %>
<%@ page import="org.springframework.core.io.ClassPathResource" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.logging.log4j.Logger" %>
<%@ page import="org.apache.logging.log4j.LogManager" %>
<%@page import="java.util.List"%>
<%@page import="org.mq.marketer.campaign.general.PropertyUtil"%>
<%@page import="org.mq.marketer.campaign.custom.MyCalendar"%>
<%@page import="org.mq.optculture.business.helper.ForgotPasswordHelper"%>



<link rel="shortcut icon" type="image/x-icon" href="img/favicon.ico" />
<link rel="icon" type="image/x-icon" href="img/favicon.ico" />
 <link type="text/css" href="css/optloginv1.css" rel="stylesheet" />
 	<script>
		//<![CDATA[
		(function() {var s=document.createElement('script');
		s.type='text/javascript';s.async=true;
		s.src=('https:'==document.location.protocol?'https':'http') +
		'://oc.groovehq.com/widgets/b5be14c1-0def-4057-ad4c-2dd7efce268d/ticket.js'; var q = document.getElementsByTagName('script')[0];q.parentNode.insertBefore(s, q);})();
		//]]>
	</script>

 
 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>Forgot Password</title>


<script src=
"https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js">
	</script>

<script type="text/javascript">
   
$(document).ready(function() {
    $('#submit').click(function(e) {
    e.preventDefault();
    var user = $("#username").val();
    var orgid = $("#orgid").val()
    var value ='username='+user+'&&orgid='+orgid;
    var url ="userValidation.jsp"; 
    console.log(value);
    
    
    if(user==""){
		document.getElementById('errorDivId').innerHTML = "";
    	document.getElementById('errorDivId').innerHTML="Please enter your username.";
    	document.getElementById('username').focus();
    	
    	return false;
	}
	if(orgid==""){
		document.getElementById('errorDivId').innerHTML = "";
    	document.getElementById('errorDivId').innerHTML="Please enter your organization id.";
    	document.getElementById('orgid').focus();
    	return false;
	}
       
    
    $.ajax({
    url: url,
    type: "post",
    data: value,
    cache: false,
    success: function(data) {
    	$("#username").val('');
    	$("#orgid").val('');
    $("#errorDivId").html(data);
    console.log(data);
    }
    });
});
});
</script>

<link href='http://fonts.googleapis.com/css?family=Raleway:400,100,200,300,500,600,800,700,900' rel='stylesheet' type='text/css'>
<style type="text/css">
	body {
	padding: 0px;
	margin: 0px;
	}

#footer {
	background: url(img/fotterBG.gif) no-repeat center center;
	padding: 10px 0px;
	font: 11px verdana;
	color: #6d6e71;
	margin-top: 40px;
	text-align: center;
}

img {
	border: 0px;
}

#loginArea {
	width: 620px;
	height: 280px;
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

#errorDivId {
	font-style: arial;
	color: red;
	font-size: 11px;
}

#capheader {
    border-bottom: 1px solid #B6B6B8;
    display: block;
    overflow: hidden;
    position: relative;
    width: 100%;
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

.shadow {
	background: url(img/shadow_under.gif) repeat-x center top;
	height: 20px;
}


#dashboard-login table td{
	height:16px;
}
#dashboard-login #header {
	padding: 25px 30px 12px;
}

#redirect-text {
	
	padding: 15px 30px 5px;
	font-size: 15px;
}
.form-footer {
	border-top: 2px solid #D0DDEC;
	height: 80px;
	text-aligh: center;
}
#dashboard-login #header h2{
	padding-top: 23px;
}
#username {
    border: 1px solid #CBD6E3;
}


.forgotClass{ font:18px Tahoma; color:#FFF; letter-spacing:1px; padding:0px; margin:0px;}
.but{ font:11px arial bold; color:#FFF; background-color:#275b6d; padding:5px 10px; text-transform:uppercase; border:0px; font:12px arial;}
</style>
</head>


<body>



<%

 String reqUrl = request.getRequestURL().toString();
 if(reqUrl.toLowerCase().contains("captiway.us") || reqUrl.toLowerCase().contains("optculture") ||
		 reqUrl.toLowerCase().contains("captiway.com") || reqUrl.toLowerCase().contains("captiway.biz") ||
		 reqUrl.toLowerCase().contains("mailhandler01.info")|| reqUrl.toLowerCase().contains("localhost") || reqUrl.toLowerCase().contains("123.176.39.104")) {
 %>
<!-- <link rel="shortcut icon" type="image/x-icon" href="img/red-icon.png" />
<link rel="icon" type="image/x-icon" href="/img/red-icon.png" /> -->


<div id="account-login" class="login-container"> 
	

<form action='userValidation.jsp' method="POST"  id="ocForm" style="margin:0;" onsubmit="return ValidateEmail();">


 			
<div id="centralize" >

<div id="dashboard-login" class="rounded">
    <div class="right-container">
						<div class="logo"><img src="img/oc-logo.png" alt="OptCulture" /></div>
						<div class="hd-small">Engage your Customers.</div>
						<div class="hd-big">Encourage <span class="brand-color">Repeat Visits.</span> </div>
					</div>

	<div class="form-container rounded">   	
        
        <div id="header" class="rounded top">
        <a href="login.jsp" class="logo"><img src="img/oc-logo.png" alt="OptCulture"></a>
        	<h2>Forgot Password</h2>
        	
        </div><!-- end #header -->
    	
    	<div id="redirect-text">Please enter your username and organization ID,<br/>
							and details on how to reset your password would be sent to your email address</div>
       
        <div class="the-form">
                <table cellpadding="2" border="0" cellspacing="2">
                <tr>
						<td colspan="2">
			                  <div id="errorDivId" 
			                	style=" text-align: center; height: 10px; display:block;" align="center">
					 		
					 		</div>
						</td>
					</tr>	
                
					<tr>
					
						<td  colspan="2">
						
						
	                <!-- 	<div style="font-style: arial; font-size: 11px; ">
							Please enter your username and organization ID,<br/>
							and details on how to reset your password would be sent to your email address
						</div> -->
						
						</td>
						
                    </tr>      
                    <tr>
						<td width="150" style="font-style: arial; font-size: 12px;"><label >Username</label></td>
						<td><input name='username' type="text" id="username" style="height: 30px; padding: 3px;" /></td>
					</tr>
					<tr>
						<td width="150" style="font-style: arial; font-size: 12px;"><label>Organization ID</label></td>
						<td><input name='orgid' type="text" id="orgid" style="height: 30px; padding: 3px;" />
							
						</td>
					</tr>    
					<tr >
					</tr>
						       
                    
				</table>
                    <div id="login-submit" class="form-footer rounded bottom">
                <input type="submit" name="submit"  id="submit" value="Submit" class="button button_green"   />
                  </div>
             
                    
                </div>
                
                
        
    	<!-- <div id="login-submit" class="form-footer rounded bottom">
                <input type="submit" name="submit"  id="submit" value="Submit" class="button button_green"   />
        </div>  -->
       
    
    </div><!-- end .form-container -->
    <div class="clear"></div>
</div><!-- end #dashboard-login -->




</div><!-- end #centralize -->

</form>


<div id="rays" style="position:absolute;z-index:1000;"></div>

</div><!-- end #login-container -->  



<%  }  else { %>





<div id="mainContainer"><!--Header Section Starts here-->
 <jsp:include page="header.jsp"></jsp:include> <!--Header Section Ends here--> 
 <!--content Section Start here-->
<div id="loginArea">
 	<form action='userValidation.jsp' method="POST" id="ocForm" >
 	
 		<div id="errorDivId" style="width: 350px; text-align: center; margin: 0 auto;display:block;" align="center">
 		</div>
 		<table width="330" border="0" align="center" cellpadding="15"
	cellspacing="0">
	<tr>
		<td bgcolor="#3a879f">
		<table width="275" border="0" align="center" cellpadding="2"
			cellspacing="0">
			<tr>
				<td  colspan="2" class="forgotClass">Forgot Password</td>
			</tr>
			<tr>
				<td  colspan="2" height="8px" style="color:#ffffff; font: 12px verdana;">Enter the email address you registered with and details on how to reset your password would be sent to you.</td>
			</tr>
			<tr>
        	    <td  colspan="2" height="8px"></td>
      	    </tr>
			<tr>
				<td style="color:#ffffff; font: 12px verdana;">Username :</td>
				<td><input type="text" id="username" name="username" size="33" /></td>
			</tr>
			<tr>
				<td style="color:#ffffff; font: 12px verdana;">Organization Id :</td>
				<td><input type="text" id="orgid" name="orgid" size="33" /></td>
			</tr>
			
			<tr>
				<td align="left">
					<input type="submit" name="submit" id="submit" class="but" value="Submit"  />
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td class="shadow">&nbsp;</td>
	</tr>
</table>
 	</form>
 	</div>
 	
 	
 	<!-- Content Section Ends Here--> <!--Footer Section Starts here--> 
 	<jsp:include page="footer.jsp" /> <!--Footer Section ends here-->
 	</div>
<!--Main Container Ends Here-->
<%  }  %>

</body>
</html>
