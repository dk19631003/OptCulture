<!DOCTYPE html>
<%@page import="org.apache.logging.log4j.LogManager"%>
<%@page import="org.apache.logging.log4j.Logger"%>
<%@page import="org.mq.loyality.utils.EncryptDecryptLtyMembshpPwd"%>
<%@page import="org.mq.loyality.utils.OCConstants"%>
<%@ page import="org.mq.loyality.common.hbmbean.LoyaltySettings"%>
<%@ page import="org.mq.loyality.utils.Constants"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html lang="en">
<%
	final String ctxPath = request.getContextPath();
%>
<head>
<style>
.error {
	color: red;
}
</style>

<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Expires" content="Sat, 01 Dec 2001 00:00:00 GMT">


<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<title>${orgname} | Login</title>
<link href="<c:url value="/resources/css/bootstrap.min.css" />"
	rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/css/metisMenu.min.css" />"
	rel="stylesheet">
<link href="<c:url value="/resources/css/style.css" />" rel="stylesheet">
<link href="<c:url value="/resources/css/font-awesome.min.css" />"
	rel="stylesheet">
<!-- <script src="resources/js/jquery-1.11.1.min.js"></script> -->
<script src="js/jquery-1.10.2.js"></script>
<script src="js/jquery.jsonp-2.4.0.js"></script>

<script src="js/jquery.form.js"></script>
<script src="resources/js/bootstrap.min.js"></script>
<script src="resources/js/metisMenu.min.js"></script>
<script src="resources/js/sb-admin-2.js"></script>
<script src="resources/js/sb-admin-2.js"></script>

<script type="text/javascript">
$(document).ready(function() {

	    
	if (localStorage.chkbx && localStorage.chkbx != '') {
        $('#rememberMe').attr('checked', 'checked');
        $('#userName').val(localStorage.usrname);
        $('#password').val(localStorage.pass);
    } else {
        $('#rememberMe').removeAttr('checked');
        $('#userName').val('');
        $('#password').val('');
    }
	$('#id').keydown(function(event) {
        // Allow special chars + arrows 
        if (event.keyCode == 46 || event.keyCode == 8 || event.keyCode == 9 
            || event.keyCode == 27 || event.keyCode == 13 
            || (event.keyCode == 65 && event.ctrlKey === true) 
            || (event.keyCode >= 35 && event.keyCode <= 39))
        {
                return;
        }
        else {
            if (event.shiftKey || (event.keyCode < 48 || event.keyCode > 57) && (event.keyCode < 96 || event.keyCode > 105 ))
            {
                event.preventDefault(); 
            }  
        }
        });
		var mobileRange= ${mobileRange} ;
	   var color1=$('#color').val();
	   $('.navbar').css({"background":color1});
	   $('.navbar, .home_content h3, .readmore a, .sign_h a,.sign .panel-title').css({"color":color1});
	   $(".submit").mouseover(function() {
	       $(this).css({"border-color":color1,"color":color1});
	   }).mouseout(function() {
	       $(this).css({"border-color":"#aeaeae","color":"#aeaeae"});
	   });
	   $('#cantLogin').click(function(){
		   var cardid=$('#cardid').val();
		   if($('#cardid').val()=="")
			   {
			   alert("Please enter your card # or your registered mobile number/email address.");
				return false;
			   }
		   var emailRegex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		   var numberRegex = /^[+-]?\d+(\.\d+)?([eE][+-]?\d+)?$/;
			if(!(numberRegex.test($("#cardid").val())))
	{
				if(!emailRegex.test($("#cardid").val()))
					{
					 alert("Please verify card #/email/mobile # you have entered.");
						return false;
					}
	}
		 
			$.ajax({
				type: "post",
		        url: "cantLogin",
		        data:  "cardid=" +cardid,
		        success: function(response) 
		        	{
		        	$("#LoginModal").hide();
		        	alert(response);
		        	window.location.replace("");
		        	
		        	}
		        });  	
	   });
	   /* $('#cantLoginCancel').click(function (){
		   window.location.replace("");
	   }); */
	    $('#LoginModal').on('show.bs.modal', function () {
		   $("#cardid").val($("#cardid").placeholder);
		 });
	   $('#rememberMe').click(function() {
			
			if ($('#rememberMe').is(':checked')) {
           // save username and password
           //alert("checked")
           localStorage.usrname = $('#userName').val();
           localStorage.pass = $('#password').val();
           localStorage.chkbx = $('#rememberMe').val();
       } else {
           localStorage.usrname = '';
           localStorage.pass = '';
           localStorage.chkbx = '';
       }
		});
	   $('#login').ajaxForm({
			dataType : 'json',
			beforeSubmit : function(formData, $form, options) {
				for ( var i = 0; i < formData.length; i++) {
					if (!formData[i].value) {
						$("#errorModal").on('show.bs.modal', function(event){
							if(i==0){
								$(this).find('.modal-body').text("Please enter your card # or "+mobileRange+" digits mobile # without the country carrier.");
							}else{
							$(this).find('.modal-body').text("Please enter your password.");}
							 });
							$("#errorModal").modal('show');
							return false;
					}
				}
			},
			success : function(json, statusText, xhr, $form)
			{
				if (json.success == true) {
					var url = json.returnUrl ;
					document.location.href = url;
				} 
				else {
					if(json.info=="105")
						{
						$("#errorModal").on('show.bs.modal', function(event){
							$(this).find('.modal-body').text(json.message);
							 });
						$("#errorModalbodyId").css({"width": "500px"});
						$("#errorModal").modal('show');
						}
					else
						{
						
						$("#errorModal").on('show.bs.modal', function(event){
						$(this).find('.modal-body').text(json.message);
						 });
						$("#errorModal").modal('show');
						}
				}
			},
			error : function(xhr) {
				alert(xhr.statusText);
			}
		});
		
		$(':input[name=id]').focus();

	});
</script>

</head>
<!-- <body>
	<h1>Login</h1>
	<form id="loginForm" action="login.ajax" method="post">
		<label for="username">Login</label>:
		<input type="text" id="id" name="id" title="User Name" />
		<br />
		<label for="password">Password</label>:
		<input type="password" id="password" name="password" title="Password" />
		<br />
-----		<button type="submit">Login</button>
	</form>

</body>
</html> -->

<body>
<%! Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);%>
<%
String loyaltyType = Constants.STRING_NILL;
	String userName = Constants.STRING_NILL;
	String password = Constants.STRING_NILL;					
Cookie cookies[] = request.getCookies();
logger.info("Started reading ");
	if (cookies != null) {
		for (Cookie cookie : cookies) {
			logger.info(cookie.getName() + " ----> "
					+ cookie.getValue());
			try {
				if ("lu".equals(cookie.getName())) {
					userName = EncryptDecryptLtyMembshpPwd
							.decryptCookie(cookie.getValue());
					cookie.setMaxAge(0);
				} else if ("lt".equals(cookie.getName())) {
					loyaltyType = EncryptDecryptLtyMembshpPwd
							.decryptCookie(cookie.getValue());
					cookie.setMaxAge(0);
				} else if ("lp".equals(cookie.getName())) {
					password = EncryptDecryptLtyMembshpPwd
							.decryptCookie(cookie.getValue());
					cookie.setMaxAge(0);
				}
			if(loyaltyType.equals("ct") && userName.length()!=0 && password.length()!=0){%>
				<script>
				$(function () {
					$('#userName').val("<%=userName%>");
				    $('#password').val("<%=password%>");
				    $('#rememberMe').attr('checked', true);
				    $('#login').submit();
				});
				</script>
			<%}
				
			} catch (Exception e) {
				logger.info("Exception in login.jsp ::: while retreiving cookies ",e);
			}
			
		}
	}
	
%>
<div class="logscreen" id="wrapper">
		<nav class="navbar navbar-default navbar-static-top">
			<input type="hidden" value="${colorCode}" id="color" />
			<%--  <%} %> --%>
			<div class="navbar-header">
				<!-- <a class="navbar-brand" href="#"> --> 
				<a href="#"><img src="${image}"
					height="58" align=left style="max-height:58px; padding-left:10px;"></a>
			</div>
		</nav>
		<div class="col-md-6 sign">
		<div class="col-md-11 sign">
			<div class="login-panel panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">Sign In</h3>
				</div>

				<div class="panel-body" class="col-xs-67 ">
					<form class="form-horizontal" method="post" id="login"
						action="login.ajax">
						<div><label style="bottom-padding: 10px;"></label></div>
						Please enter your card number or registered mobile number without country code.<div><label>&nbsp;</label></div>
						<div class="form-group">
							<label for="inputEmail" class="control-label col-xs-5">Card
								#/ Mobile # :</label>
							<div class="col-xs-7 ">
								<input type="text" id="userName" class="form-control" name="id"
									title="User Name" />
							</div>
						</div>
						<div class="form-group">
							<label for="inputPassword" class="control-label col-xs-5">Password
								:</label>
							<div class="col-xs-7">
								<input type="password" id="password" class="form-control"
									name="password" title="Password"  />
							</div>
						</div>
						<div class="form-group">
							<div class="col-xs-offset-5 col-xs-7 log_bt"><label style=" bottom-padding:10px"></label>
								<button type="submit"
									class="btn btn-lg form-control btn-default submit">Login</button>
							</div>
						</div>
						<div class="form-group">
							<div class="col-xs-offset-5 col-xs-7 log_bt">
								<div class="checkbox">
									<label><input type="checkbox" id="rememberMe"
										name="rememberMeID">Remember me</label>
								</div>
							</div>
						</div>
						<input type="button" hidden="true" id="cantLoginBtn"
							data-toggle="modal" data-target="#LoginModal" />
						<div class="sign_h">
							<a href="#" data-toggle="modal" data-target="#LoginModal">Can't
								login?</a>
							<div class="clearfix visible-sm-block"></div>

						</div>
					</form>
				</div>
			</div>
		</div>
		</div>
		<div class="container">
			<div class="col-md-6">
				<div class="home_content">
					<label class="intro_h" style="color: ${colorCode}; font-size: 16pt;">Anytime, anywhere access - right on your finger-tips!</label>
					<div class="row  vertical-align">
						<div class="col-xs-1">
							<img src="resources/images/features.png" height="25"
								class="intro_img">
						</div>
						<div class="col-xs-offset-0 col-xs-10 set_img">
							<p class="intro_h" style="color: #333333; font-size: 12pt;">Check Account Balance</p>
							<p class="intro" style="color: #666666; font-size: 11pt;">View your most up-to-date account balance, maintained on a real-time basis. 
							This means every time you shop and earn more points or redeem existing ones, you can instantly view your new balance within a few clicks.</p>
							<!-- <p class="intro">Lorem Ipsum is simply dummy text of the
								printing and typesetting industry. Lorem Ipsum has been the
								industry's standard dummy text ever since the 1500s,</p> -->
							<!-- <div class="readmore">
								<a href=""> Readmore</a>
							</div> -->
						</div>
					</div>
					<div class="row  vertical-align">
						<div class="col-xs-1">
							<img src="resources/images/tea.png" height="25"
								class="intro_img">
						</div>
						<div class="col-xs-offset-0 col-xs-10 set_img">
							<p class="intro_h" style="color: #333333; font-size: 12pt;">Track & View Transactions</p>
							<p class="intro" style="color: #666666; font-size: 11pt;">
							Keeping track of transactions is made easy now. In one single view, track all your transactions leading to your current balance. 
							Get to experience the full transparency that you can expect with our program!</p>
							<!-- <p class="intro">Lorem Ipsum is simply dummy text of the
								printing and typesetting industry. Lorem Ipsum has been the
								industry's standard dummy text ever since the 1500s,</p>
							<div class="readmore">
								<a href=""> Readmore</a>
							</div> -->
						</div>
					</div>
					<div class="row  vertical-align">
						<div class="col-xs-1">
							<img src="resources/images/clock.png" height="25"
								class="intro_img">
						</div>
						<div class="col-xs-offset-0 col-xs-10 set_img">
							<p class="intro_h" style="color: #333333; font-size: 12pt;">Manage Communication Preferences</p>
							<p class="intro" style="color: #666666; font-size: 11pt;">A much simpler and faster way to manage your communication preferences is right here. 
							Change your phone number, add a new email address and provide profile details to receive tailored offers and promotions.</p>
							<!-- <p class="intro">Lorem Ipsum is simply dummy text of the
								printing and typesetting industry. Lorem Ipsum has been the
								industry's standard dummy text ever since the 1500s,</p>
							<div class="readmore">
								<a href=""> Readmore</a>
							</div> -->
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="modal fade" id="LoginModal" tabindex="-1">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">
							<span aria-hidden="true">&times; </span><span class="sr-only">
								Close</span>
						</button>
						<h4 class="modal-title" id="myModalLabel" style="padding-left: 4;">Log-in Help</h4>
					</div>
					<div class="modal-body" id="inDiv">
						<p class="intro_card">Please enter your card # or your registered email / ${mobileRangeJava} digits mobile # without the country carrier and we'll send log-in details to your inbox right-away!</p>
						<div class="form-group">
							<label for="inputPassword" class="control-label col-xs-4 card"
								style="padding-left: 1;"> Card # / Email / Mobile# :</label>
							<div class="col-xs-6 card">
								<input type="text" class="form-control" id="cardid"
									name="cardId">
							</div>
						</div>
						<div class="form-group">
							<!-- <div class="col-xs-offset-4 col-xs-8">
								<button type="button"  id="cantLogin"  class="btn  col-xs-4 btn-lg btn-default submit" style="padding-left:5;">Submit</button>
								<button type="button"  id="cantLoginCancel"  class="btn  col-xs-4 btn-lg btn-default submit" >Cancel</button>
							</div> -->
							<div class="col-xs-offset-4 col-xs-10 col-md-7 ">
								<button type="button" id="cantLogin"
									class="btn  col-md-4 btn-default submit">Submit</button>
								<button type="button" id="cantLoginCancel"
									class="btn  col-xs-offset-1  col-md-4 btn-default submit " data-dismiss="modal">Cancel</button>
							</div>
						</div>
					</div>
					<div class="modal-footer"></div>
				</div>
			</div>
		</div>

		<div class="modal fade" id="errorModal" tabindex="-1" role="dialog"
			aria-labelledby="helpModalLabel" aria-hidden="true" >
			<div class="modal-dialog"  id="errorModalbodyId">
				<div class="modal-content" class="col-xs-11">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">
							<span aria-hidden="true">&times; </span><span class="sr-only">
								Close</span>
						</button>
						<h4 class="modal-title" id="myModalLabel">Error</h4>
					</div>
					<div class="modal-body" id="inDiv"></div>
					<div class="modal-footer"></div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
