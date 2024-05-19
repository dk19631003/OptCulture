<!DOCTYPE html>
<html lang="en">
<head>

<meta http-equiv="Pragma" content="no-cache">
 <meta http-equiv="Cache-Control" content="no-cache">
 <meta http-equiv="Expires" content="Sat, 01 Dec 2001 00:00:00 GMT">

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0,  maximum-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>${orgname} | Change Password</title>
    <%@ include file="jquery.jsp"%>
    
     <script type='text/javascript'>

    $(document).ready(function() {
    	$('#opass').val("");
    	
          $('#sbmt').click(function(){
              var oldPass=$('#opass').val();
              var newPass=$('#npass').val();
              var confPass=$('#cpass').val();
             // var passFromServer=$('#passFromServer').val();
              
              if(oldPass==""  && newPass=="" &&  confPass==""  )
            	  {
            	  alert("Please enter your old password.");
            	  return false;
            	  }
              if(oldPass==""  )
        	  {
        	  alert("Please enter your old password.");
        	  return false;
        	  }
              if(newPass==""  )
        	  {
        	  alert("Please enter your new password.");
        	  return false;
        	  }
              if(confPass==""  )
        	  {
        	  alert("Please confirm your new password.");
        	  return false;
        	  }
             
              if( oldPass==newPass )
            	  {
            	  alert("New password can't be same as the old password.");
            	  return false;
            	  }
              
        	if(confPass!=newPass)
        		{
        		alert("Your new passwords do not match. Please try again.");
          	  return false;
        		}
        	
        		var dateVar = new Date();
        		var timezone = (dateVar.getMonth()+1)+'-'+dateVar.getDate()+'-'+dateVar.getFullYear()+' '+dateVar.getHours()+':'+dateVar.getMinutes()+':'+dateVar.getSeconds();
        		
           	  $('#clientTime').val(timezone);
        	 
        	$('#changePass').submit();
        	
          });  
          
          $("#cancel").click(function(){
		    	document.location.href = 'membership';
		    });    
          
          
          
    });
    
    </script>
    
</head>

<body>

<%@ include file="top.jsp" %>  
  <%@ include file="leftmenu.jsp" %>  


 <div id="wrappers" class="inner_page">
<div id="page-wrapper" style="min-height: 597px;">
            <div class="rbar">
			
                <div class="row">
                    <div class="col-lg-12">
                        <h1 class="page-header">Change Password</h1>
                    </div>
                   </div>
		<div class="panel panel-default">
   <div class="panel-body">
                   <div class="row">
 
                     <div class="col-lg-12">
                     <div class="boxex_text">
                    
                  <form:form method="POST"  action="changePass"  id="changePass"   class="form-horizontal"  autocomplete="off">
                    <div class="form-group" >
                   <%--  <%String passFromServer=(String)request.getAttribute("password"); %> --%>
                    <% String userName = (String)request.getSession(true).getAttribute("userName"); %>
                    <input type="text" name="user" value="<%=userName%>" style="display: none" />
                    <input type="hidden" id="clientTime" name = "clientTime" value=""/>
                   <%--  <input type="hidden" id="passFromServer" value="<%=passFromServer %>"/> --%>
                      <label class="control-label col-sm-2" for="opass">Old Password</label><span class="col-xs-1 cal">:</span>
                      <div class="col-sm-3">
                        <input type="password" class="form-control" id="opass"  name="opass"   placeholder="" autocomplete="off" value=""/>
                      </div>
                    </div>
                    <div class="form-group">
                      <label class="control-label col-sm-2" for="npass">New Password</label><span class="col-xs-1 cal">:</span>
                      <div class="col-sm-3">
                        <input type="password" class="form-control" id="npass"  name="npass"   placeholder="" autocomplete="off" value=""/>
                      </div>
                    </div>
                    <div class="form-group">
                      <label class="control-label col-sm-2" for="cpass" style="padding-right:3px;">Confirm New Password</label><span class="col-xs-1 cal">:</span>
                      <div class="col-sm-3">
                        <input type="password" class="form-control" id="cpass"  name="cpass"  placeholder=""  autocomplete="off" value=""/>
                      </div>
                    </div>
                    
                    
                    <!-- <div class="form-group">
                      <label class="control-label col-sm-2" for="cpass" style="padding-right:3px;"></label><span class="col-xs-1 cal"></span>
                      <div class="col-sm-1">
                        <input type="submit" id="sbmt" value="Save" class="col-md-3 btn btn-group btn-default save">
                      </div>
                       <div class="col-sm-3 ">
                        <input type="button" id="cancel"  value="Cancel"  class="  col-md-3 btn btn-group btn-default save">
                      </div>
                    </div> -->
                    <div class="col-xs-5 col-xs-offset-2 col-md-5 card">
                    <label class="control-label col-sm-1" style="padding-left:5px;"></label>
                   	<button type="button" id="sbmt"	class="btn  col-md-3 btn-default submit">Submit</button>
					<button type="button" id="cancel" class="btn  col-xs-offset-1  col-md-3 btn-default submit ">Cancel</button>
										</div>
                     </form:form>
                      </div>
                     
                       
                     
                   
                  
     </div>
     </div>
     
                
               
                <!-- /.row -->
            </div>
            </div>
            </div>
            <!-- /.container-fluid -->
        </div>
 </div>

    <!-- <script src="js/jquery.js"></script>

    Bootstrap Core JavaScript
    <script src="js/bootstrap.min.js"></script>

    Metis Menu Plugin JavaScript
    <script src="js/plugins/metisMenu/metisMenu.min.js"></script>
    <script src="js/sb-admin-2.js"></script>
    
    <script src="js/plugins/bootstrap-formhelpers.min.js"></script>
    <script src="js/jquery-1.10.2.min.js"></script>
    
    <script type='text/javascript'> -->

    <!-- $(document).ready(function() {

          $().bfhselectbox('toggle');
    }); -->


    
    <!--   <script src="js/jquery.cookie.js"></script>
      <script src="js/charisma.js"></script>  
    <script src="js/sb-admin-2.js"></script> -->
    <!-- tablesorter widget file - loaded after the plugin -->
    
 
 
 </body>
 </html>
