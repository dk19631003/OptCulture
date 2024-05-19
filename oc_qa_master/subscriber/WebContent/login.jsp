<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>

<title>Account - Login</title>
<link rel="shortcut icon" type="image/x-icon" href="img/favicon.ico" />
<link rel="icon" type="image/x-icon" href="img/favicon.ico" />
<link type="text/css" href="css/optloginv1.css" rel="stylesheet">
	<script type="text/javascript" src="js/jsconfig.js"></script>
	
	
<script>
 //<![CDATA[
        // ...
!function(){
var analytics=window.analytics||[];
if(!analytics.initialize)
if(analytics.invoked)console.error("Segment snippet included twice.");
else{analytics.invoked=!0;analytics.methods=["trackSubmit","trackClick","trackLink","trackForm","pageview","identify","reset","group","track","ready","alias","debug","page","once","off","on"];
analytics.factory=function(t){return function(){var e=Array.prototype.slice.call(arguments);e.unshift(t);analytics.push(e);
return analytics}};
for(var t=0;t<(analytics.methods.length);t++){var e=analytics.methods[t];analytics[e]=analytics.factory(e)}analytics.load=function(t){ var e=document.createElement("script");
e.type="text/javascript";e.async=!0;
e.src=("https:"===document.location.protocol?"https://":"http://")+"cdn.segment.com/analytics.js/v1/"+t+"/analytics.min.js";
var n=document.getElementsByTagName("script")[0];
n.parentNode.insertBefore(e,n)};analytics.SNIPPET_VERSION="4.0.0";
  analytics.load("Kkvvu8CEGiMSVSYFrZFPPbu0ED1HBUxM");
  analytics.page();

  }}();
  //]]>
</script>

	<!-- <script>
		//<![CDATA[
		(function() {var s=document.createElement('script');
		s.type='text/javascript';s.async=true;
		s.src=('https:'==document.location.protocol?'https':'http') +
		'://oc.groovehq.com/widgets/b5be14c1-0def-4057-ad4c-2dd7efce268d/ticket.js'; var q = document.getElementsByTagName('script')[0];q.parentNode.insertBefore(s, q);})();
		//]]>
	</script>  -->

	<script type="text/javascript">
	function homePage(){
		
		if(gAppUrl != undefined){
			window.location = gAppUrl;
		}
		
	}
	
	function login(){
			//alert('validate');
			
			var orgid, un, pw, hidden_un;
			hidden_un = document.getElementById('hidden_un');
			
			orgid = document.getElementById('orgid');
			un = document.getElementById('un');
			pw = document.getElementById('pw');
			// alert("username : " + un.value + " Password : " + pw.value);

			if(un.value==''){
				if(pw.value==''){
					document.getElementById('errorDivId').innerHTML = "Enter Username and password";
					document.f.j_username.focus();
					return false;
				}
				else{
					document.getElementById('errorDivId').innerHTML ="Enter Username";
					document.f.j_username.focus();
					return false;
				}
			}
			else if(pw.value==''){
					document.getElementById('errorDivId').innerHTML ="Enter Password";
					document.f.j_password.focus();
					return false;
			}
			
			if(orgid.value=='') {
				document.getElementById('errorDivId').innerHTML = "Enter Organization Id";
				orgid.focus();
				return false;
			}

			document.getElementById('errorDivId').innerHTML ='';


			window.location = window.location.href.split("?")[0];


			
			hidden_un.value=un.value+"__org__"+orgid.value;
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
.Lform-footer {
	height: 60px;
	text-aligh: center;
}
#dashboard-login .form-container {
    height: 465px;
}
#un,#pw,#orgid {
  margin-bottom:18px;
}


</style>
</head>
<body onload="document.f.j_username.focus()">

      
      
	<%
	    String message = "Your login attempt was not successful, please try again...<br />		Reason:";
		String ua = request.getHeader("User-Agent");
			boolean isSupported = (ua != null && ua.indexOf("MSIE") != -1)
					|| (ua != null && ua.indexOf("Firefox") != -1)
					|| (ua != null && ua.indexOf("Chrome") != -1)
					|| (ua != null && ua.indexOf("") != -1);
			
			
	%>



	<%
		String reqUrl = request.getRequestURL().toString();
		if (reqUrl.toLowerCase().contains("localhost") || reqUrl.toLowerCase().contains("captiway.us") || 
				reqUrl.toLowerCase().contains("optculture") || reqUrl.toLowerCase().contains("captiway.biz")
				|| reqUrl.toLowerCase().contains("mailhandler01.info") || reqUrl.toLowerCase().contains("oc.magnaquest") 
				|| reqUrl.toLowerCase().contains("captiway.com") || reqUrl.toLowerCase().contains("123.176.39.104")) {
	%>
	
<!-- 	<link rel="shortcut icon" type="image/x-icon"
		href="/img/red-icon.png" /> -->


	<div id="account-login" class="login-container">


		<form name="f" action="<c:url value='j_spring_security_check'/>"
			method="post"  onsubmit="return login();">

			<div id="centralize">

				<div id="dashboard-login" class="rounded">
					<div class="right-container">
						<div class="logo"><img src="img/oc-logo.png" alt="OptCulture" /></div>
						<div class="hd-small">Engage your Customers.</div>
						<div class="hd-big">Encourage <span class="brand-color">Repeat Visits.</span> </div>
					</div>

					<div class="form-container rounded" >

						<div id="header" class="rounded top">
							<h2>Account Login</h2>
						</div>
						<!-- end #header -->

						<div class="the-form">
							<label for="UserName">Username</label>
							<input name='temp_username' type="text" id="un" /> 
							<input name='j_username' type="hidden" id="hidden_un" />
							
							<label for="Password">Password</label>
							<input name='j_password' type="password" id="pw" />
							
							<label>Organization ID</label>
							<input name='j_orgid' type="text" id="orgid" />
							
							<div id="errorDivId" style="width: 350px; text-align: center; height: 8px; margin: 0 auto;" align="center">
							   
								<c:if test="${not empty param.login_error}">
									 <c:out value="${fn:replace(SPRING_SECURITY_LAST_EXCEPTION.message, 'Bad credentials', 
									 'Your login attempt was not successful, please try again . Reason:Bad credentials')}"  />
									<c:remove var = "SPRING_SECURITY_LAST_EXCEPTION" scope = "session" />
								</c:if>
								</div>
							
							
							<div id="login-submit" class="Lform-footer rounded bottom">
								<input value="Login to OptCulture" class="button button_green" type="submit">
							</div>
						</div>
						<div class="form-footer rounded bottom">
							<div id="bottom-links" class="rounded">
								<!-- <input value="true" name="RememberMe" id="RememberMe"type="checkbox">
								<input value="false" name="RememberMe"type="hidden"> <label for="RememberMe">Remember me?</label> | --> 
								<a href="forgotPassword.jsp">Forgot Your Password?</a>
							</div>
						</div>
					</div>
					<!-- end .form-container -->
					<div class="clear"></div>
				</div>
				<!-- end #dashboard-login -->



				
			</div>
			<!-- end #centralize -->

		</form>



	<%
		if (!isSupported) {
	%>
	<div style="background-color: green" width="100%">
		<div style="padding: 5px 30px 5px 30px;">Please note : For
			best application performance , open OptCulture web application in
			Firefox or MS Internet Explorer 9 or Chrome.</div>
	</div>
	<%
		}
	%>
	
	</div>
	<!-- end #login-container -->


	<%
		} else {
	%>
<!-- 	<link rel="shortcut icon" type="image/x-icon" href="img/favicon.ico" /> -->

	<!-- <link rel="shortcut icon" type="image/gif" href="img/favicon.gif" /> -->
	<!--Main Container Start Here-->
	<div id="mainContainer">
		<!--Header Section Starts here-->
		<jsp:include page="header.jsp"></jsp:include>
		<!--Header Section Ends here-->
		<!--content Section Start here-->
		<div id="loginArea">
			<div id="errorDivId"
				style="width: 350px; text-align: center; margin: 0 auto;"
				align="center">
				<!--<c:if test="${not empty param.login_error}">
        Your login attempt was not successful, try again.<br />
	Reason: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}" />
				</c:if>-->
			</div>

			<form name="f" action="<c:url value='j_spring_security_check'/>"
				method="post" onsubmit="return login();">
				<table width="330" border="0" align="center" cellpadding="15"
					cellspacing="0">
					<tr>
						<td bgcolor="#3a879f">
							<table width="360" border="0" align="center" cellpadding="2"
								cellspacing="0">
								<tr>
									<td colspan="2"
										style="font: 18px Tahoma; color: #FFF; letter-spacing: 1px;">
										User Login</td>
								</tr>

								<tr height="10px" />



								<tr>
									<td width="110">User Name :</td>
									<td><input name='temp_username' type="text" id="un"
										size="26" /> <input name='j_username' type="hidden"
										id="hidden_un" /></td>
								</tr>

								<tr height="10px" />

								<tr>
									<td>Password :</td>
									<td><input name='j_password' type="password" id="pw"
										size="26" />
									</td>
								</tr>
								<tr height="10px" />
								<tr>
									<td>Organization Id :</td>
									<td><input name='j_orgid' type="text" id="orgid" size="26" />
									</td>
								</tr>
								<tr height="10px" />
								<tr>
									<td align="right" colspan="2"><input name="Reset"
										type="reset" class="but" id="button" value="Reset" /> <input
										name="submit" type="submit" class="but" value="Submit" /></td>
								</tr>

								<tr>
									<td colspan="2">
										<table width="100%">
											<tr>
												<td width="40%"><a href="forgotPassword.jsp"
													style="color: #fff;">Forgot Password ?</a>
												</td>
												<td align="right"><input type="checkbox"
													name="_spring_security_remember_me">Remember
														Password ?
												</td>
											</tr>
										</table></td>
								</tr>
							</table></td>
					</tr>
					<tr>
						<td class="shadow">&nbsp;</td>
					</tr>
				</table>
			</form>

			<%
				if (!isSupported) {
			%>
			<div style="background-color: green" width="100%">
				<div style="padding: 5px 30px 5px 30px;">Please note : For
					best application performance , open captiway web application in
					Firefox or MS Internet Explorer 9 or Chrome.</div>
			</div>
			<%
				}
			%>

		</div>
		<!-- Content Section Ends Here-->
		<!--Footer Section Starts here-->
		<jsp:include page="footer.jsp" />
		<!--Footer Section ends here-->
	</div>
	<!--Main Container Ends Here-->

	<%
		}
	%>

</body>
</html>
