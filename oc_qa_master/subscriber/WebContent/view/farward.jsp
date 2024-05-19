<%-- <%@page import="org.mq.marketer.campaign.general.Utility"%>
<%@page import="org.mq.marketer.campaign.general.Constants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import="org.mq.marketer.campaign.beans.*" %>
<%@ page import="org.mq.marketer.campaign.dao.*" %>
<%@ page import="org.mq.marketer.campaign.controller.*" %>
<%@page import="org.mq.marketer.campaign.general.Constants"%> --%>
<%@ page import="org.mq.marketer.campaign.beans.*" %>
<%@ page import="org.mq.marketer.campaign.dao.*" %>
<%@ page import="org.mq.marketer.campaign.controller.*" %>
<%@page import="org.mq.marketer.campaign.general.Constants"%>
<%@ page import="org.springframework.beans.factory.xml.XmlBeanFactory" %>
<%@ page import="org.springframework.core.io.ClassPathResource" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.logging.log4j.Logger" %>
<%@ page import="org.apache.logging.log4j.LogManager" %>
<%@page import="java.util.List"%>
<%@page import="org.mq.optculture.business.helper.FarwardHelper"%>

<%
/* String sentIdStr= (String)request.getAttribute(Constants.QS_SENTID);
long sentId = Long.parseLong(sentIdStr); */


/* ServletContext servletContext =this.getServletContext();
WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
ContactsDao contactsDao = (ContactsDao)wac.getBean("contactsDao");
FarwardToFriendDao farwardToFriendDao = (FarwardToFriendDao)wac.getBean("farwardToFriendDao");
 *///CampaignSentDao campaignSentDao = (CampaignSentDao)wac.getBean("campaignSentDao");
//Contacts contacts = contactsDao.findById(sentId);

CampaignSent campaignSent = (CampaignSent)request.getAttribute(Constants.QS_SENTID);
Long sentId =campaignSent.getSentId();
Long cid =campaignSent.getContactId(); 

FarwardHelper farwardhelper = new FarwardHelper();
Contacts contact =farwardhelper.setFlagValue(cid);

//Contacts contact = contactsDao.findById(cid);

if(contact == null) response.getWriter().print("Invalid Request");
String fullName=(contact.getFirstName()!= null?contact.getFirstName()+"  "+(contact.getLastName() != null ? contact.getLastName() : ""):"");
Long crId= campaignSent.getCampaignReport().getCrId();
String email = campaignSent.getEmailId();
request.setAttribute(Constants.QS_SENTID, sentId);
request.setAttribute(Constants.QS_FULL_NAME, fullName);
request.setAttribute(Constants.QS_CRID, crId);
request.setAttribute(Constants.QS_EMAIL, email); 

//for old campaigns userId can be null because userId is not passed in the unsub url
Long userId = contact.getUsers().getUserId();

request.setAttribute(Constants.QS_USERID,userId);
request.setAttribute(Constants.QS_CID ,cid);





%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Forward to Friend</title>
<script type="text/javascript">

var hasFocus = false;

function validate() 
 {
	
	//var namePattern = /^([A-Za-z].? ?){3,25}$/;
	
	var nameStr = document.getElementById('fullname');

	if (nameStr.value == "  " || nameStr.value.length == 0 ) {
		alert('Please enter your name.');
		return false;
	
	}
	
	
	
	
	var email = document.getElementsByName('email');
	
    var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	
	var isTrue = false;
	var falseCount = 0;
    for ( var i = 0; i < email.length; i++) {
		
		//alert("email value is :: "+email[i].value);
		
		if (email[i].length == 0 || email[i].value == '' || email[i].value == "Your friend's email ID") {
			continue;
		}
		
		if (filter.test(email[i].value)) {
			var name = document.getElementsByName('name'); 
			
			/* alert("1");
					if( name[i].value == '' || name[i].length == 0 || name[i].value == "Your friend's name" ){
						continue;
					} else{
						
						alert("2"+name[i].value);
						if( !namePattern.test(name[i].value)){
							alert("Enter valid name");
							return false;
						}
					} */
				
			isTrue = true;
			falseCount++;		
		}else {
			isTrue = false;
		}
		
		
		
	
		if(!isTrue){
			alert('Please provide a valid email address.');
		    return false;
		}
	}
    if (falseCount == 0) {
    	alert('Please provide at least one email address.');
    	return false;
	}
	
    return isTrue;
    
}




function insRow()
{
    var x=document.getElementById('POITable').insertRow(0);
    var c1=x.insertCell(0);
    var c2=x.insertCell(1);
    var c3=x.insertCell(2);
    c1.innerHTML="<input size=25 type='text' id='latbox'  name='name' value= 'Your friend&apos;s name' onfocus='gotFocus(this)'  class='inputsm' />";
    c2.innerHTML="<img src='img/&amp;_icon.png' width='16' height='18' />";
    c3.innerHTML="<input size=25 type='text' id='latbox'  class='inputsm' name='email'   onfocus='gotFocus(this)' value='Your friend&apos;s email ID' />";
}

window.onload = init;

function gotFocus(item) {
    if (item.value == "Your friend's name" ) {
        item.value = '';
    }
    if (item.value == "Your friend's email ID") {
        item.value = '';
    }
}



</script>

<style type="text/css">
.input {
	width:240px;
	padding:0 4px;
	height:33px;
	border:1px solid #c5c5c5;
	border-radius:5px;
	font-family:Arial, Helvetica, sans-serif; 
	font-size:12px; 
	color:#666666;
}

.maskup{
	position:absolute;
	z-index:10;
	width:210px;
	height:220px;
	background:rgba(255, 255, 255, 0);
	top:5px;
}

.thumbnail {
	width: 208px;
	height: 218px;
	overflow:hidden;
}

#emailerrorq, #emailerrorw, #emailerror{ font-family:Arial, Helvetica, sans-serif; 
font-size:12px;
color:#ff0000;
 margin-left: 7px;}

.thumbnail iframe {
	width: 700px;
	height: 723px;

	-webkit-transform-origin: 0 0;
	-moz-transform-origin: 0 0;
	-ms-transform-origin: 0 0;
	transform-origin: 0 0;


	-webkit-transform:  scale(0.3, 0.3);
	-moz-transform:  scale(0.3, 0.3);
	-ms-transform:  scale(0.3, 0.3);
	transform:  scale(0.3, 0.3);

	overflow: hidden;
}

.sendemail{
	color:#FFFFFF;
	font-size:16px;
	font-weight:bold;
	background:url(img/btn2.jpg) 0 0 no-repeat;
	width:207px;
	height:49px;
	border:0 none;

}

.sme{
	
	border-radius:5px;
	padding:10px;
	margin-top:20px;
	width:510px;
}

.inputsm {
	width:190px;
	padding:0 4px;
	height:33px;
	border:1px solid #c5c5c5;
	border-radius:5px;
	font-family:Arial, Helvetica, sans-serif; 
	font-size:12px; 
	color:#666666;
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

</style>
</head>
<body>
<table width="750" border="0" align="center" style="background:url(img/bg_texure.png) 0 0;" cellspacing="0" cellpadding="0">
  <tr>
    <td align="center" valign="top" style="padding:50px 0">
    <form  method="post" name="form"  action ="https://qcapp.optculture.com/subscriber/view/forwardsubmit.jsp" onSubmit="return validate();"  >
    
    
 				<input type="hidden" name="userId" value="<%=request.getAttribute(Constants.QS_USERID) %>"/>
				<input type="hidden" name="crId" value="<%=request.getAttribute(Constants.QS_CRID) %>"/>
				<input type="hidden" name="sentId" value="<%=request.getAttribute(Constants.QS_SENTID) %>"/>
				<input type="hidden" name="contactId" value="<%=request.getAttribute(Constants.QS_CID) %>"/>
				
				<input type="hidden" name="emailId" value="<%=request.getAttribute(Constants.QS_EMAIL) %>"/>
				
				
				
				
    <table style="background:#FFFFFF; border:1px solid #c5c5c5;" width="570" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td width="750" height="60" align="center" valign="bottom" style="font-family:Georgia, 'Times New Roman', Times, serif; font-size:28px; font-weight:bold; color:#333333; background:url(images/head-bottom.jpg) ; padding-bottom:10px;">Forward to Friend</td>
        <td />
        	
      </tr>
      <tr>
        <td class="labelcell"><table width="300" border="0" cellspacing="0" cellpadding="0">
          <tr>
          <td height="40" colspan="2"><div style="background:url(images/sep.jpg) left bottom no-repeat; padding-bottom:10px; font-weight:bold; font-size:14px;" >Add your details</div></td>
           

            </tr>
         <tr>
            <td width="50" height="50" align="left" valign="middle" style="font-size:14px;">Name:</td>
            <td width="250" align="left" valign="middle">
            <input type="text" value="<%=request.getAttribute(Constants.QS_FULL_NAME) %>" class="input" name="fullname" id="fullname" /></td>
          </tr>
          <tr>
            <td height="50" align="left" valign="middle" style="font-size:14px;">Email:</td>
            <td align="left" valign="middle"><input type="text" class="input" value= "<%=request.getAttribute(Constants.QS_EMAIL) %>" 
            			name="emailId" id="textfield2"   disabled="disabled"/></td>
          </tr>
        </table></td>
        <td>&nbsp;</td>
      </tr>
      <tr>
        <td class="labelcell"><table width="300" border="0" cellspacing="0" cellpadding="0">
          <tr>
          <td height="50" colspan="2"><div style="background:url(images/sep.jpg) left bottom no-repeat; padding-bottom:10px; font-weight:bold; font-size:14px;">Add a message</div></td>
         </tr>
          <tr>
            <td colspan="2" align="left" valign="middle">
              <textarea class="txtarea" name="custmsg" id="textarea" cols="45" rows="5" >I just received this email and thought 
you might find it interesting.</textarea></td>
          </tr>
          </table></td>
        <td>&nbsp;</td>
      </tr>
      <tr>
        <td class="labelcell" colspan="2"><div ><table border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="50" colspan="4">
            <div style="background:url(images/sep.jpg) left bottom no-repeat; padding-bottom:10px; font-weight:bold; font-size:14px;">Who should we forward this to?</div></td>
            </tr>
         
          <tr>
					        <td class="labelcell" colspan="2">
					        
						       	 <div id="test-POItablediv">
						
						
									   <table id="POITable" border="0" cellpadding="10" cellspacing="0">   
									        
									        <tr>
									            <td><input size=25 type="text" id="name"  name ="name" class="inputsm" onfocus="gotFocus(this)" value= "Your friend's name" /></td>
									             <td align="center" valign="middle"><img src="img/&amp;_icon.png" width="16" height="18" /></td>
									            
									            <td><input size=25 type="text"  name="email" class="inputsm" onfocus="gotFocus(this)"  id="email" value="Your friend's email ID" /><span id="emailerrorq" ></span></td>
									           <td></td>
									            
									        </tr>
									        <tr>
									            <td><input size=25 type="text" id="name" name ="name" class="inputsm" onfocus="gotFocus(this)"  value="Your friend's name"  /></td>
									             <td align="center" valign="middle"><img src="img/&amp;_icon.png" width="16" height="18" /></td>
									            
									            <td><input size=25 type="text"  name="email" class="inputsm" onfocus="gotFocus(this)"  id="email"  value="Your friend's email ID" /><span id="emailerrorq" ></span></td>
									           <td></td>
									           
									        </tr>
									        
									        <tr>
									            <td><input size=25 type="text" id="name" name ="name" class="inputsm" onfocus="gotFocus(this)"  value="Your friend's name"  /></td>
									             <td align="center" valign="middle"><img src="img/&amp;_icon.png" width="16" height="18" /></td>
									            
									            <td><input size=25 type="text"  name="email" class="inputsm" onfocus="gotFocus(this)"  id="email" value="Your friend's email ID" /><span id="emailerrorq" ></span></td>
									           <td> <img src="images/action_add.gif" onclick="insRow()"/></td>
									            
									        </tr>
									        
								    </table>
								    
						   	 	</div>
					   	 	 	<span id="errorDiv" style="color:red;"/>
							   
					    	</td>
				       </tr>
          
        </table></div></td>
      </tr>
      <tr>
        <td colspan="2" class="labelcell" height="70"><table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td align="center" valign="middle">
            <input type="submit"  name="button" id="button"  value="Send Email" class="sendemail" /></td>
          </tr>
        </table></td>
        </tr>
      <tr>
        <td height="45" colspan="2" align="left" valign="middle" bgcolor="#666666" class="labelcell" style="color:#FFFFFF;">Your friend's email address will only be used to forward this email to them <br />
          and will never be available to anyone else.</td>
      </tr>
    </table>
    
   
    </form>
    
    
    </td>
  </tr>
 </table>
</body>
</html>
