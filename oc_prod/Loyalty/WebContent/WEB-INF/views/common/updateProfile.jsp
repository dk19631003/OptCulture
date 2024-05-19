<!DOCTYPE html>
<html lang="en">
<head>
	<script type= "text/javascript" src = "js/countries.js"></script>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <meta http-equiv="Pragma" content="no-cache">
 <meta http-equiv="Cache-Control" content="no-cache">
 <meta http-equiv="Expires" content="Sat, 01 Dec 2001 00:00:00 GMT">
    
    <title>${orgname} | Update Profile</title>
<%@ include file="jquery.jsp"%>
   
 <script type="text/javascript">
	 $(function(){
		 $("#profile").addClass("active").append('<img id="theImg" style="right:0; position:absolute; margin:0;" src="resources/images/hover.png" />');
		
		
		/* $('#zip').keydown(function(event) {
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
	        }); */
	        populatedropdown("day","month","year")
	   	 populatedropdown("annDay","annMonth","annYear")
		 var color1=$('#color').val();
			   $('#userdetails, #side-menu, #wrappers,.sidebar ul li a.active,.nav,.userdetails, .userdetails a').css({"background-color":color1});
			$(".nav > li > a:hover, .nav > li > a:focus").css({"background-color":color1});
			$('#otp').val("");
			    $("#month").val( $('#monthVal').val());
			    $("#day").val( $('#dayVal').val());
			    $("#year").val( $('#yearVal').val());
			    $("#annDay").val( $('#aDay').val());
			    $("#annMonth").val( $('#aMonth').val());
			    $("#annYear").val( $('#aYear').val());
			     var gen=$("#genderVal").val();	
			     if(gen!=""){
			    $('input[name=gender][value='+gen+']').prop("checked",true);
			     }
			   
			     $("#updateProfile").click(function(){
			    	 /*  $('#zip').keydown(function(event) {
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
			    	        }); */
			    	  var emailRegex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
			    	 var emailVal= $.trim($("#email").val());
			    	  if(emailVal !="" && !emailRegex.test(emailVal))
						{
						 alert("Invalid entry! Please verify email address you have entered.");
							return false;
						}
			    	  var zipRegx= ${zipRegx} ;
			    if(($("#zip").val()!="") && !zipRegx.test($("#zip").val()) )
			    	{
			    	alert("Invalid entry! Please ener valid zip code.");
			    	return false;
			    	}
					$('#updateForm').submit();
				});
			    
			      $( "#country").autocomplete({
		               source: function(req, responseFn) {
		                   var re = $.ui.autocomplete.escapeRegex(req.term);
		                   var matcher = new RegExp( "^" + re, "i" );
		                   var a = $.grep( country_arr, function(item,index){
		                       return matcher.test(item);
		                   });
		                   responseFn( a );
		               }, 
		               minLength: 1
		            });
			    
			    $( "#state").keyup(function(){
			    	var val=$( "#country").val();
			    	var state_arr;
			    	if($( "#country").val()=="")
			    		{
			    		var all=s_a[1];
			    		for(var i =2; i<s_a.length; i++){
			    			all += '|'.concat(s_a[i]);
			    		}
			    	/* 	var all=s_a[1].concat('|').concat(s_a[2]).concat(s_a[3]).concat(s_a[4]).concat(s_a[5]).concat(s_a[6]).concat(s_a[7]).concat(s_a[8]).concat(s_a[9]).concat(s_a[10]).concat(s_a[11]).concat(s_a[12]).concat(s_a[13]).concat(s_a[14]).concat(s_a[15]).concat(s_a[16]).concat(s_a[17]).concat(s_a[18]).concat(s_a[19]).concat(s_a[20]).concat(s_a[21]).concat(s_a[22]).concat(s_a[23]).concat(s_a[24]).concat(s_a[25]).concat(s_a[26]).concat(s_a[27]).concat(s_a[28]).concat(s_a[29]).concat(s_a[30]).concat(s_a[31]).concat(s_a[32]).concat(s_a[33]).concat(s_a[34]).concat(s_a[35]).concat(s_a[36]).concat(s_a[37]).concat(s_a[38]).concat(s_a[39]).concat(s_a[40]).concat(s_a[41]).concat(s_a[42]).concat(s_a[43]).concat(s_a[44]).concat(s_a[45]).concat(s_a[46]).concat(s_a[47]).concat(s_a[48]).concat(s_a[49]).concat(s_a[50]).concat(s_a[51]).concat(s_a[52]).concat(s_a[53]).concat(s_a[54]).concat(s_a[55]).concat(s_a[56]).concat(s_a[57]).concat(s_a[58]).concat(s_a[59]).concat(s_a[60]).concat(s_a[61]).concat(s_a[62]).concat(s_a[63]).concat(s_a[64]).concat(s_a[65]).concat(s_a[66]).concat(s_a[67]).concat(s_a[68]).concat(s_a[69]).concat(s_a[70]).concat(s_a[71]).concat(s_a[72]).concat(s_a[73]).concat(s_a[74]).concat(s_a[75]).concat(s_a[76]).concat(s_a[77]).concat(s_a[78]).concat(s_a[79]).concat(s_a[80]).concat(s_a[81]).concat(s_a[82]).concat(s_a[83]).concat(s_a[84]).concat(s_a[85]).concat(s_a[86]).concat(s_a[87]).concat(s_a[88]).concat(s_a[89]).concat(s_a[90]).concat(s_a[91]).concat(s_a[92]).concat(s_a[93]).concat(s_a[94]).concat(s_a[95]).concat(s_a[96]).concat(s_a[97]).concat(s_a[98]).concat(s_a[99]).concat(s_a[100]).concat(s_a[101]).concat(s_a[102]).concat(s_a[103]).concat(s_a[104]).concat(s_a[105]).concat(s_a[106]).concat(s_a[107]).concat(s_a[108]).concat(s_a[109]).concat(s_a[110]).concat(s_a[111]).concat(s_a[112]).concat(s_a[113]).concat(s_a[114]).concat(s_a[115]).concat(s_a[116]).concat(s_a[117]).concat(s_a[118]).concat(s_a[119]).concat(s_a[120]).concat(s_a[121]).concat(s_a[122]).concat(s_a[123]).concat(s_a[124]).concat(s_a[125]).concat(s_a[126]).concat(s_a[127]).concat(s_a[128]).concat(s_a[129]).concat(s_a[130]).concat(s_a[131]).concat(s_a[132]).concat(s_a[133]).concat(s_a[134]).concat(s_a[135]).concat(s_a[136]).concat(s_a[137]).concat(s_a[138]).concat(s_a[139]).concat(s_a[140]).concat(s_a[141]).concat(s_a[142]).concat(s_a[143]).concat(s_a[144]).concat(s_a[145]).concat(s_a[146]).concat(s_a[147]).concat(s_a[148]).concat(s_a[149]).concat(s_a[150]).concat(s_a[151]).concat(s_a[152]).concat(s_a[153]).concat(s_a[154]).concat(s_a[155]).concat(s_a[156]).concat(s_a[157]).concat(s_a[158]).concat(s_a[159]).concat(s_a[160]).concat(s_a[161]).concat(s_a[162]).concat(s_a[163]).concat(s_a[164]).concat(s_a[165]).concat(s_a[166]).concat(s_a[167]).concat(s_a[168]).concat(s_a[169]).concat(s_a[170]).concat(s_a[171]).concat(s_a[172]).concat(s_a[173]).concat(s_a[174]).concat(s_a[175]).concat(s_a[176]).concat(s_a[177]).concat(s_a[178]).concat(s_a[179]).concat(s_a[180]).concat(s_a[181]).concat(s_a[182]).concat(s_a[183]).concat(s_a[184]).concat(s_a[185]).concat(s_a[186]).concat(s_a[187]).concat(s_a[188]).concat(s_a[189]).concat(s_a[190]).concat(s_a[191]).concat(s_a[192]).concat(s_a[193]).concat(s_a[194]).concat(s_a[195]).concat(s_a[196]).concat(s_a[197]).concat(s_a[198]).concat(s_a[199]).concat(s_a[200]).concat(s_a[201]).concat(s_a[202]).concat(s_a[203]).concat(s_a[204]).concat(s_a[205]).concat(s_a[206]).concat(s_a[207]).concat(s_a[208]).concat(s_a[209]).concat(s_a[210]).concat(s_a[211]).concat(s_a[212]).concat(s_a[213]).concat(s_a[214]).concat(s_a[215]).concat(s_a[216]).concat(s_a[217]).concat(s_a[218]).concat(s_a[219]).concat(s_a[220]).concat(s_a[221]).concat(s_a[222]).concat(s_a[223]).concat(s_a[224]).concat(s_a[225]).concat(s_a[226]).concat(s_a[227]).concat(s_a[228]).concat(s_a[229]).concat(s_a[230]).concat(s_a[231]).concat(s_a[232]).concat(s_a[233]).concat(s_a[234]).concat(s_a[235]).concat(s_a[236]).concat(s_a[237]).concat(s_a[238]).concat(s_a[239]).concat(s_a[240]).concat(s_a[241]).concat(s_a[242]).concat(s_a[243]).concat(s_a[244]).concat(s_a[245]).concat(s_a[246]).concat(s_a[247]).concat(s_a[248]).concat(s_a[249]).concat(s_a[250]).concat(s_a[251]).concat(s_a[252]); */
			    	state_arr = all.split("|");
			    		}
			    	else{
			    		var cid=country_arr_lower.indexOf(val.toLowerCase());
				       state_arr = s_a[cid].split("|");
			    	      }
			    	 $( "#state").autocomplete({
			               source: function(req, responseFn) {
			                   var re = $.ui.autocomplete.escapeRegex(req.term);
			                   var matcher = new RegExp( "^" + re, "i" );
			                   var a = $.grep( state_arr, function(item,index){
			                       return matcher.test(item);
			                   });
			                   responseFn( a );
			               }, 
			               minLength: 1
			            }); 
			    }); 
			    
			    $("#cancel").click(function(){
			    	document.location.href = 'profile';
			    }); 
			    
			    
			    function populatedropdown(dayfield, monthfield, yearfield){
			   	 var monthtext=['please select','January','Febuary','March','April','May','June','July','August','September','October','November','December'];
			   	 var today=new Date()
			   	 var dayfield=document.getElementById(dayfield)
			   	 var monthfield=document.getElementById(monthfield)
			   	 var yearfield=document.getElementById(yearfield)
			   	  var dayVal = "01";
			   	 dayfield.options[0]=new Option( 'please select',0)
			   	 for (var i=1; i<=31; i++){
			   		dayVal = i;
			   		if(i<10) dayVal ='0'+i;
			   	 dayfield.options[i]=new Option(i, dayVal)
			   	 }
			   	 var monthVal = "var";
			   	 monthfield.options[0]=new Option('please select',0)			
			   	 for (var m=1; m<=12; m++){
			   		monthVal = m;
			   	 if(m<10) monthVal ='0'+m;
			   	 monthfield.options[m]=new Option(monthtext[m], monthVal)
			   	 }
			   	 var thisyear=today.getFullYear()
			   	 yearfield.options[0]=new Option( 'please select',0)
			   	 for (var y=1; y<100; y++){
			   	 yearfield.options[y]=new Option(thisyear, thisyear)
			   	 thisyear-=1
			   	 }

			   	 }
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
                        <h1 class="page-header">Update Profile</h1>
                    </div>
                   </div>
                   <div class="panel panel-default">
					<div class="panel-body">
                     <div class="col-lg-12">
       <div class="boxex_text">
                    <div class="row">
       <form:form method="POST"  action="saveProfile"  id="updateForm"  class="form-horizontal"  commandName="contacts" >
                  <div class="form-group">
                      <label class="control-label col-sm-2" for="email">Name</label>
                      <label style="padding-left:15px" class="control-label  col-md-1">:</label>
                      <div class="col-sm-2">
                      <input type="text" name="firstName" class="form-control" id="firstname" placeholder="First name"  value="${contacts.firstName}"  ></div>
                      <div class="col-sm-2"> 
                      <input type="text"  name="lastName"  class="form-control" id="lasstname" placeholder="Last name" value="${contacts.lastName}"></div>
                    </div>
                   
                    <div class="form-group">
                      <label class="control-label col-sm-2" for="email">Email</label>
                      <label style="padding-left:15px" class="control-label  col-md-1">:</label>
                      <div class="col-sm-4">	
                        <input type="text" name="emailId" class="form-control" id="email" placeholder="Enter email" value="${contacts.emailId}">
                      </div>
                    </div>
                    
                    <div class="form-group">
                      <label class="control-label col-sm-2" for="Address">Address</label>
                      <label style="padding-left:15px" class="control-label  col-md-1">:</label>
                       <div class="col-sm-4">
                 
                 <input type="text" name="addressOne" class="form-control" id="addressone"  value="${contacts.addressOne}"  placeholder="Street">
                      </div>
                      <div class="col-sm-2">
                      <input type="text" class="form-control" id="zip" name="zip"   placeholder="Zip" value="${contacts.zip}"></div>
                     
                      </div>
                      <div class="form-group">
                      <label class="control-label col-sm-3" for="state"></label>
                      <div class="col-sm-2">
                      <input type="text" class="form-control"  name="country"  id="country" autocomplete="off" value="${contacts.country}"  placeholder="Enter Country" />
                      </div>
                       <div class="col-sm-2">
                       <input type="text" class="form-control" name="state" autocomplete="off" id="state" value="${contacts.state}"   placeholder="Enter State" />
                         </div> 
                        <%-- input type="text" class="form-control" name="country"  id="country" value="${contacts.country}"  placeholder="--Enter Country--"></div>
                                   --%>
                       <div class="col-sm-2">
                        <input type="text" class="form-control" id="city"  name="city"  value="${contacts.city}"  placeholder="Enter City"></div>
                      </div>
                       
                     
                      <div class="form-group">
                      <label class="control-label col-sm-2" for="Gender">Gender</label>
                      <label style="padding-left:15px" class="control-label  col-md-1">:</label>
                      <div class="col-sm-4"><label class="checkbox-inline"><input type="radio" name="gender" id="optionsRadios3" value="Female"  checked> Female </label><label class="checkbox-inline"><input type="radio" name="gender" id="optionsRadios3" value="Male"  checked>Male </label></div>
                     
                    </div>
                     
                     <div class="form-group">
                      <label class="control-label col-sm-2" for="state">Date Of Birth</label>
                      <label style="padding-left:15px" class="control-label  col-md-1">:</label>
                       <div class="col-sm-4">
                       <select  class="form-control" style="float:left; width:39%; margin-right:1%;"   id="month" name="birthMonth" > </select>
                       <select  class="form-control" style="float:left; width:28%; margin-right:1%; margin-left:1%;"   id="day" name="birthDay1" > </select>
                      <select  class="form-control" id="year"   style="float:left; width:29%; margin-left:1%;"  name="birthYear"> </select>

                      
                      </div>
                      </div>   
                        
                        <div class="form-group">
                      <label class="control-label col-sm-2" for="state">Anniversary</label>
                      <label style="padding-left:15px" class="control-label  col-md-1">:</label>
                       <div class="col-sm-4">
                       <select  class="form-control"  name="annMonth"  style="float:left; width:39%; margin-right:1%;"  id="annMonth"  > </select>
                        <select  class="form-control" name="annDay" style="float:left; width:28%; margin-right:1%; margin-left:1%;"   id="annDay"  ></select>

                      <select class="form-control"  style="float:left; width:29%; margin-left:1%;"  name="annYear" id="annYear"   > </select>
                      </div>
                      </div>   
                          <input type="hidden" value="${contacts.birthMonth}" id="monthVal"/>
                           <input type="hidden" value="${contacts.birthDay1}" id="dayVal"/>
                           <input type="hidden" value="${contacts.birthYear}" id="yearVal"/>
                            <input type="hidden" value="${contacts.gender}" id="genderVal"/>
                            
                            <input type="hidden" value="${contacts.annDay}" id="aDay"/>
                           <input type="hidden" value="${contacts.annMonth}" id="aMonth"/>
                           <input type="hidden" value="${contacts.annYear}" id="aYear"/>
                             <input type="hidden" id="otp"    />
                            
                      <div class="btn-group">
                         <div class="row"> 
                   <!-- <div class="col-sm-3">
                                   <p id="updatemobile">
                                 <a href="" data-toggle="modal" data-target="#Update" class="updatemobile" >Update Mobile</a></p>  </div>  -->
                                 <div class="col-sm-3"></div> 
                                   <div class="col-sm-2"> <!-- <p id="updatemobile"> -->
                                   
                                   
                                   <button type="button" id="updateProfile" class="btn col-sm-12 btn-lg btn-default submit">Save</button><!-- </p> -->
                                   </div>
                                   <div class="col-sm-2"> <!-- <p id="updatemobile"> -->
                                   <button type="button" id="cancel" class="btn col-sm-12 btn-lg btn-default submit">Cancel</button><!-- </p> -->
                                   </div>
                                               <div class="clearfix visible-sm-block"></div>
                                      </div>
                                               <div class="clearfix visible-sm-block"></div>
                            </div>
                            </form:form>
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
 </div>
 
 <div class="modal fade" id="Update" tabindex="-1" 
role="dialog" aria-labelledby="helpModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">
                            <span aria-hidden="true">&times;
                            </span><span class="sr-only">
                                     Close</span></button>
                        <h4 class="modal-title" id="myModalLabel">
                            Update My Mobile</h4> 
                    </div>
                
                    <div class="modal-body">
                     <p class="intro_card">Please enter your phone number</p>
                      <!--    <form> -->
                         <div class="form-group">
                                <label for="inputPassword" class="control-label col-xs-4 card">New Mobile No:</label>
                                <div class="col-xs-6 card">
                                    <input type="text" class="form-control" id="mobileno"  name="mobilePhone"   placeholder="--Enter Your New Mobile No--">
                                </div>
                            </div>
                           <div class="form-group">
                                <label for="inputPassword" class="control-label col-xs-4 card">Verfication Code:</label>
                                <div class="col-xs-6 card">
                                    <input type="text" class="form-control" id="verification" name="verfication"  placeholder="">
                                </div><div class="sendcode"> <a href="#"  id="code">Send Code</a></div>
                            </div>
                            <div class="form-group">
                                <div class="col-xs-offset-4 col-xs-9">
                                    <button type="submit" id="submitMobile"  class="btn btn-lg btn-default submit">Submit</button>
                                </div>
                            </div>
                         <!--   </form> -->
                    </div>
                      <input type="hidden" value="" id="countryVal"/>
                       <input type="hidden" value="" id="stateVal"/>
                    <div class="modal-footer">
                       
                    </div>
                </div></div>
                 
        </div>
 </div>
   <!--  -->


     <!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    Bootstrap Core JavaScript
    <script src="js/bootstrap.min.js"></script>

    Metis Menu Plugin JavaScript
    <script src="js/plugins/metisMenu/metisMenu.min.js"></script>
   
    <script src="js/plugins/bootstrap-formhelpers.min.js"></script>
    <script src="js/jquery-1.10.2.min.js"></script>
    
     
    
    
    Custom Theme JavaScript
    <script src="js/sb-admin-2.js"></script> -->
    <!-- tablesorter widget file - loaded after the plugin -->
 
         <script src="js/charisma.js"></script>    
 
 
 </body>
 </html>