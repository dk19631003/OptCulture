<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<!-- <link rel="icon" type="image/gif" href="img/favicon.gif" /> -->

<title>OptCulture - Account Login</title>
<script type="text/javascript" src="js/jsconfig.js"></script>
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

			if(orgid.value=='') {
				document.getElementById('errorDivId').innerHTML = "Enter Organization Id";
				orgid.focus();
				return false;
			}

				
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
			document.getElementById('errorDivId').innerHTML ='';
			
			hidden_un.value=un.value+"__org__"+orgid.value;
			return true;
		}
	</script>

	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link type="text/css" href="css/optlogin.css" rel="stylesheet">

</head>


<body onload="document.f.j_username.focus();">


<c:if test="${not empty param.oc}">

<div id="account-login" class="login-container" style="position: relative; z-index: 3000"> 

	<div id="errorDivId"
		style="width: 350px; text-align: center; margin: 0 auto;"
		align="center">
		<c:if test="${not empty param.login_error}">
	        Your login attempt was not successful, try again.<br />
		Reason: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}" />
	</c:if>
	</div>

<form name="f" action="<c:url value='j_spring_security_check'/>" method="post" onsubmit="return login();">
	
<div id="centralize" style="position:absolute;z-index:2500;">

<div id="dashboard-login" class="rounded">

	<div class="form-container rounded">   	
        
        <div id="header" class="rounded top">
        	<a href="http://www.optculture.com/" class="logo"><img src="images/oc-logo.png" alt="OptCulture"></a>
            <h2>Account Login</h2>
            <div class="clear"></div>
        </div><!-- end #header -->
    	
        <div class="the-form">
                <table cellpadding="2" border="0" cellspacing="2">
					<tr>
						<td width="85"><label for="UserName">Username</label></td>
						<td>
							<input name='temp_username' type="text" id="un"  />
							<input name='j_username' type="hidden" id="hidden_un" />
						</td>
                    </tr>                               
                    <tr>
						<td><label for="Password">Password</label></td>
						<td>
							<input name='j_password' type="password" id="pw"  />
						</td>
					</tr>
					<tr>
						<td><label>Organization</label></td>
						<td><input name='j_orgid' type="text" id="orgid">
						</td>
					</tr>
				</table>
                    
             
                    
                </div>
                
                
        
    	<div id="login-submit" class="form-footer rounded bottom">
                <input value="Login to OptCulture" class="button button_green" type="submit">
        </div> 
       
    
    </div><!-- end .form-container -->
    
</div><!-- end #dashboard-login -->



<div id="bottom-links" class="rounded">
	<input value="true" name="RememberMe" id="RememberMe" type="checkbox"><input value="false" name="RememberMe" type="hidden">
                    <label for="RememberMe">Remember me?</label> | <a href="http://dashboard.optculture.com/account/passwordreset">Forgot Your Password?</a>
</div>
</div><!-- end #centralize -->

</form>


<div id="rays" style="position:absolute;z-index:1000;"></div>

</div><!-- end #login-container -->  

</c:if>

<c:if test="${not empty param.cap}">
<!--Main Container Start Here-->
<div id="mainContainer"><!--Header Section Starts here--> <jsp:include
	page="header.jsp"></jsp:include> <!--Header Section Ends here--> <!--content Section Start here-->
<div id="loginArea">
<div id="errorDivId"
	style="width: 350px; text-align: center; margin: 0 auto;"
	align="center"><c:if test="${not empty param.login_error}">
        Your login attempt was not successful, try again.<br />
	Reason: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}" />
</c:if></div>

<form name="f" action="<c:url value='j_spring_security_check'/>"
	method="post" onsubmit="return login();">
<table width="330" border="0" align="center" cellpadding="15"
	cellspacing="0">
	<tr>
		<td bgcolor="#3a879f">
		<table width="360" border="0" align="center" cellpadding="2" cellspacing="0">
			<tr>
				<td colspan="2">
				<h3>User Login</h3>
				</td>
			</tr>
			
			<tr height="10px" />
			
			<tr>
				<td width="110">Organization Id :</td>
				<td><input name='j_orgid' type="text" id="orgid" size="26" /></td>
			</tr>
			<tr height="10px" />
			
			<tr>
				<td>User Name :</td>
				<td>
					<input name='temp_username' type="text" id="un" size="26" />
					<input name='j_username' type="hidden" id="hidden_un" />
				</td>
			</tr>

			<tr height="10px" />

			<tr>
				<td>Password :</td>
				<td><input name='j_password' type="password" id="pw" size="26" /></td>
			</tr>
			<tr height="10px" />
			<tr>
					<td align="right" colspan="2">
						<input name="Reset" type="reset" class="but" id="button" value="Reset" /> 
						<input name="submit" type="submit" class="but" value="Submit" />
					</td>
			</tr>
			
			<tr>
				<td colspan="2">
				   	<table width="100%">
				   		 <tr>
				   			<td width="40%"><a href="forgotPassword.jsp" 
				   				style="color:#fff;">Forgot Password ?</a></td>
							<td align="right"><input type="checkbox"
								name="_spring_security_remember_me">Remember Password ?</td>
						 </tr>	
					</table>
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
<!-- Content Section Ends Here--> <!--Footer Section Starts here--> <jsp:include
	page="footer.jsp"></jsp:include> <!--Footer Section ends here--></div>
<!--Main Container Ends Here-->

</c:if>

</body>
</html>
