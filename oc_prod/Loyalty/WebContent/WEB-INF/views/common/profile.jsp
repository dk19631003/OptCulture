<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Expires" content="Sat, 01 Dec 2001 00:00:00 GMT">


<%@ include file="jquery.jsp"%>
<title>${orgname} | Update Profile</title>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0,  maximum-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<title>Profile</title>
<!-- The styles -->

<script>
$(document).ready(function(){
	$("#profile").addClass("active").append('<img id="theImg" style="right:0; position:absolute; margin:0;" src="resources/images/hover.png" />');
 
    $('#mobileno').keydown(function(event) {
        if (event.keyCode == 46 || event.keyCode == 8 || event.keyCode == 9 
            || event.keyCode == 27 || event.keyCode == 13 
            || (event.keyCode == 65 && event.ctrlKey === true) 
            || (event.keyCode >= 35 && event.keyCode <= 39))
        {
                return;
        }
        else {
            // If it's not a number stop the keypress
            if (event.shiftKey || (event.keyCode < 48 || event.keyCode > 57) && (event.keyCode < 96 || event.keyCode > 105 )) 
            {
                event.preventDefault(); 
            }  
        }
        });
    var mobileRange= ${mobileRange} ;
	$("#code").click(function(){
		if($('#mobileno').val()==$('#contactMobile').val())
		{
		alert("This mobile# is already registered with your membership! To update your mobile#, please enter a different number.");
		return false;
		}
		if($('#mobileno').val()=="")
			{
			alert("Please enter new mobile# you want to update to.");
			return false;
			}
		var mobNo=$("#mobileno").val();
		
		if((mobNo==""))	
		{
		alert("Please enter "+mobileRange+" digits mobile# without the country carrier.");
		return false;
		}
		 /* $.ajax({
				type: "get",
		        url: "checkMobile",
		        data:  "phone=" +mobNo,
		        success: function(response) {
		        	//$('#otp').val(response.otpCode);
		        if(response=="103")	{
		        	alert("Please enter valid mobile#.");
		        	return false;
		        	}
		        if(response=="101")	{
		        	alert("The mobile# entered by you is registered to another membership. Please enter a different mobile# or contact your nearest store.");
		        	return false;
		        	}
		       $("#sendCodeId").hide();
		        }
		 	});
	 */
		$.ajax({
			type: "post",
	        url: "sendVerificationCode",
	        data:  "phone=" +mobNo,
	        success: function(response) {
	        	
	        	if(response.status=="101"){
	        		alert("The mobile# entered by you is registered to another membership. Please enter a different mobile# or contact your nearest store.");
		        	return false;
	        	}else if(response.status=="103"){
	        		alert("Please enter "+mobileRange+" digits mobile# without the country carrier.");
		        	return false;
	        	}else if(response.status=="105"){
	        		alert("This mobile# is already registered with your membership! To update your mobile#, please enter a different number.");
	        		return false;
	        	}
	        	$("#sendCodeId").hide();
	        	$('#otp').val(response.otpCode);	
	        	$('#phoneVal').val(mobNo);	
	        }
		});
		});
	$('#Update').on('show.bs.modal', function () {
		$("#mobileno").val($("#mobileno").placeholder);
    	$("#verification").val($("#verification").placeholder);
    	$("#sendCodeId").show();
		 });
	$("#submitMobile").click(function(){
		
		var mobNo=$('#mobileno').val();
		if(mobNo==$('#contactMobile').val())
			{
			alert("This mobile# is already registered with your membership! To update your mobile#, please enter a different number.");
			return false;
			}
		
		if(mobNo=="")
			{
			alert("Please enter "+mobileRange+" digits mobile# without the country carrier.");
			return false;
			}
		
		/* if((mobNo.length)!=10)
			{
			alert("Please enter 10-digits mobile#.");
			return false;
			} */
			 
			 if($('#otp').val()=="")
			{
			alert("OTP code. Please click on send code");
			return false;
			}
			if($('#otp').val()!=($('#verification').val()))
				{
				alert("Incorrect OTP code! Please try again.");
				return false;
				}
			if($('#phoneVal').val()!=(mobNo))
			{
			alert("Mobile# and OTP code pair mismatch! Please verify and try again.");
			return false;
			}
			$.ajax({
				type: "get",
		        url: "checkMobile",
		        data:  "phone=" +mobNo,
		        success: function(response) {
		        	//$('#otp').val(response.otpCode);	
		        if(response=="101")	{
		        	alert("The mobile# entered by you is registered to another membership. Please enter a different mobile# or contact your nearest store.");
		        	return false;
		        	}else if(response=="103"){
		        		alert("Please enter "+mobileRange+" digits mobile# without the country carrier.");
		        	}
		        	else{
		        		 $('#updateMobile').submit();
		        	}
		       
		        }
		 	});
			
			
			
			
				});
	
	

});
	</script>
</head>

<body>
	<%@ include file="top.jsp"%>
	<!-- topbar ends -->
	<%@ include file="leftmenu.jsp"%>

	<div id="wrappers" class="inner_page">
		<div id="page-wrapper" style="min-height: 597px;">
			<div class="rbar">
				<div class="row">
					<div class="col-lg-12">
						<h1 class="page-header">Profile Details</h1>
					</div>
				</div>
				<div class="panel panel-default">
					<div class="panel-body">
						<div class="row">
							<div class="col-lg-12">
								<div class="boxex_text">

									<div class="table-responsive" id="table_w">
										<table class="table borderless" border="0" cellspacing="0"
											cellpadding="5">
											<tbody>
												<tr>
													<td width="18%">Name</td>
													<td width="5%">:</td>
													<td>${contacts.firstName} ${contacts.lastName}</td>
												</tr>

												<tr>
													<td>Email</td>
													<td>:</td>
													<td>${contacts.emailId}</td>
												</tr>
												<tr>
													<td>Phone Number</td>
													<td>:</td>
													<td>${contacts.mobilePhone}</td>
												</tr>
												<tr>
													<td>Address</td>
													<td>:</td>
													<td>${contacts.usState}</td>
												</tr>
												<tr>
													<td>Date of Birth</td>
													<td>:</td>
													<td>${contacts.stringBirthDay}</td>
												</tr>
												<tr>
													<td>Anniversary</td>
													<td>:</td>
													<td>${contacts.stringAnniversary}</td>
												</tr>
											</tbody>
										</table>
									</div>
									<input type="hidden" class="form-control" id="contactMobile" value="${contacts.mobilePhone}"/>
									<div class="btn-group">
										<div class="container">
											<div class="row">
												<div class="col-md-2 col-sm-3" id="updates_row">
												<!--	<p id="updatemobile">
												 		<a href="updateProfile" class="updatemobile"
															id="updateProfile">Update Profile</a>
													</p> -->
													<button class="btn col-sm-12 btn-lg btn-default submit" id="updateProfile" onclick="location.href='updateProfile'">Update Profile</button>
													
												</div>
												<div class="col-md-2 col-sm-3">
													<button class="btn col-sm-12 btn-lg btn-default submit"  data-toggle="modal" data-target="#Update" >Update Mobile</button>
													
												</div>
												<div class="clearfix visible-sm-block"></div>
											</div>
										</div>
									</div>
								</div>
							</div>

						</div>
						<!-- /.row -->
					</div>
					<!-- /.container-fluid -->
				</div>
				<form:form method="POST" action="saveMobile" id="updateMobile"
					class="form-horizontal" commandName="contacts">
					<div class="modal fade" id="Update" tabindex="-1" role="dialog"
						aria-labelledby="helpModalLabel" aria-hidden="true"  data-backdrop="static">
						<div class="modal-dialog">
							<div class="modal-content">
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal">
										<span aria-hidden="true">&times; </span><span class="sr-only">
											Close</span>
									</button>
									<h4 class="modal-title" id="myModalLabel">Update My Mobile</h4>
								</div>
								<div class="modal-body">
									<p class="intro_card">Please enter your new ${mobileRangeJava} digits mobile# without the country carrier.</p>

									<div class="form-group">
										<label for="inputPassword"
											class="col-xs-5 col-md-3  card">New
											Mobile No:</label>
										<div class="col-xs-4 col-md-4 card">
											<input type="text" class="form-control" id="mobileno"
												name="mobilePhone">
										</div>
										<div class="sendcode" id="sendCodeId" >
											<a href="#" id="code"  style="text-decoration: none; border-bottom: 1px solid ${colorCode}; padding: 0px;" ><font color=<%=session.getAttribute("colorCode")%>>Send Code</font></a>
										</div>
									</div>
									<div class="form-group">
										<label for="inputPassword"
											class="col-xs-5 col-md-3 card">Verfication
											Code:</label>
										<div class="col-xs-4 col-md-4 card">
											<input type="text" class="form-control" name="verification"	id="verification" placeholder="" autocomplete="off"/> <input
												type="hidden" class="form-control" id="otp" placeholder="">
												 <input
												type="hidden" class="form-control" id="phoneVal" placeholder="">
												
										</div>
									</div>
									<div class="form-group">
										<label for="inputPassword"
											class="control-label col-xs-5 col-md-3 card"></label>
										<div class="col-xs-5 col-md-5 card">
											<button type="button" id="submitMobile"
											style="padding-right: 25px"	 class="btn  col-md-4 btn-default submit">Submit</button>
												<button type="button" id="cancelMobileBtnId"
												class="btn  col-xs-offset-1  col-md-4 btn-default submit" data-dismiss="modal">Cancel</button>
										</div>

									</div>

								</div>
							</div>
						</div>
					</div>
				</form:form>
			</div>
		</div>

	</div>


	<!--     <script src="js/jquery.js"></script> -->

	<!--  <script type="text/javascript">
		$(document).ready(function(){
			$(".btn").click(function(){
				$("#modal").modal('show');
			});
			/*$(".navbar-toggle").click(function(){
			
			$("#wrappers").toggleClass('expanded');
			$(".side_text").toggleClass('expanded');
			});*/
		});
     </script>
     <script src="js/jquery.cookie.js"></script>
<script src="js/jquery.bdt.js" type="text/javascript"></script>
    <script src="js/bootstrap.min.js"></script>
    
   <script src="resources/js/metisMenu.min.js"></script>
	 <script src="js/sb-admin-2.js"></script>
     <script src="js/plugins/dataTables/dataTables.bootstrap.js"></script>
      <script src="js/plugins/dataTables/jquery.dataTables.js"></script>
     <script src="js/jquery.cookie.js"></script>
   <script src="js/charisma.js"></script> 
     <script src="js/sb-admin-2.js"></script> -->



       <script src="js/charisma.js"></script>  

</body>
</html>