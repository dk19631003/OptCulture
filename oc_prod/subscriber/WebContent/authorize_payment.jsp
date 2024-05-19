<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.poi.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%> 
<%@ taglib prefix="c" uri='http://java.sun.com/jstl/core_rt' %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--
INSTRUCTIONS:
If you are using a doctype of HTML 4.01 Strict or XHTML 1.0 Strict, it is recommended that you use HTML 4.01 Transitional 
or XHTML 1.0 Transitional instead, because an <iframe> tag will be inserted which is not officially allowed with strict doctypes. 
However, all the major browsers (IE, Firefox, Chrome, Opera, Safari) still work when you use an <iframe> tag with a strict doctype.

Here are the recommended transitional doctypes:
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
Or
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
-->


<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>Example Page</title>


  <!-- 
  INSTRUCTIONS:
  Add a link to this CSS file in your <head> section.
  -->
  <!-- <link href="contentx/manage.css" rel="stylesheet" type="text/css" /> -->


  <!-- 
  INSTRUCTIONS:
  To make it look right in Internet Explorer 6, add a conditional link to this CSS file in your <head> section.
  -->
  <!--[if lte IE 6]>
  <link href="contentx/manageIELTE6.css" rel="stylesheet" type="text/css" />
  <![endif]-->

  
  <!-- 
  INSTRUCTIONS:
  Add a link to this javascript file.
  -->
 

  <!-- 
  INSTRUCTIONS:
  After the link to popup.js, add in your own script section to override any options for the javascript file.
  -->
   <script type="text/javascript" >
       function insertToken(token1){
	   var tokenEle = document.getElementsByName('Token')[0];
           
            tokenEle.value = token1; 
            AuthorizeNetPopup.openManagePopup();
   }
        </script>
  <script type="text/javascript">
  //<![CDATA[
  // Uncomment this line if eCheck is enabled. This does not affect functionality, only the initial sizing of the popup page for add payment.
  //AuthorizeNetPopup.options.eCheckEnabled = true;

  // Uncomment these lines to define a function that will be called when the popup is closed.
  // For example, you may want to refresh your page and/or call the GetCustomerProfile API method from your server.
  //AuthorizeNetPopup.options.onPopupClosed = function() {
  //	your code here.
  //};

  // Uncomment this line if you do not have absolutely positioned elements on your page that can obstruct the view of the popup.
  // This can speed up the processing of the page slightly.
  //AuthorizeNetPopup.options.skipZIndexCheck = true;

  // Uncomment this line to use test.authorize.net instead of secure.authorize.net.
  //AuthorizeNetPopup.options.useTestEnvironment = true;
  //]]>
  </script>

</head>
<body>


<!-- 
INSTRUCTIONS:
Put this hidden <form> anywhere on your page with the token from the GetHostedProfilePage API call.
-->
<form method="post"  action="https://secure.authorize.net/hosted/profile/manage" id="formAuthorizeNetPopup" name="formAuthorizeNetPopup" target="iframeAuthorizeNet" style="display:none;">
  <input type="hidden" name="Token" value="" />
</form>


<!-- 
INSTRUCTIONS:
Put this button wherever you want on your page.
-->
<!-- <button onclick="AuthorizeNetPopup.openManagePopup()">Manage my payment and shipping information</button> -->


<!-- 
INSTRUCTIONS:
Put this divAuthorizeNetPopup section right before the closing </body> tag. The popup will be centered inside the whole browser window. 
If you want the popup to be centered inside some other element such as a div, put it inside that element.
-->
<div id="divAuthorizeNetPopup" style="display:none;" class="AuthorizeNetPopupGrayFrameTheme">
  <div class="AuthorizeNetPopupOuter">
    <div class="AuthorizeNetPopupTop">
      <div class="AuthorizeNetPopupClose">
        <a href="javascript:;" onclick="AuthorizeNetPopup.closePopup();" title="Close"> </a>
      </div>
    </div>
    <div class="AuthorizeNetPopupInner">
      <iframe name="iframeAuthorizeNet" id="iframeAuthorizeNet"  frameborder="0" scrolling="no"></iframe>
    </div>
    <div class="AuthorizeNetPopupBottom">
      <div class="AuthorizeNetPopupLogo" title="Powered by Authorize.Net"></div>
    </div>
  </div>
  <div class="AuthorizeNetShadow AuthorizeNetShadowT"></div>
  <div class="AuthorizeNetShadow AuthorizeNetShadowR"></div>
  <div class="AuthorizeNetShadow AuthorizeNetShadowB"></div>
  <div class="AuthorizeNetShadow AuthorizeNetShadowL"></div>
  <div class="AuthorizeNetShadow AuthorizeNetShadowTR"></div>
  <div class="AuthorizeNetShadow AuthorizeNetShadowBR"></div>
  <div class="AuthorizeNetShadow AuthorizeNetShadowBL"></div>
  <div class="AuthorizeNetShadow AuthorizeNetShadowTL"></div>
</div>


<!-- 
INSTRUCTIONS:
Put this divAuthorizeNetPopupScreen section right before the closing </body> tag.
-->
<div id="divAuthorizeNetPopupScreen" style="display:none;"></div>


</body>
</html>