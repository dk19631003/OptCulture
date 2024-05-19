<%@ taglib prefix="core" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="org.mq.loyality.common.hbmbean.ContactsLoyalty"%>
<%@page	import="org.springframework.security.core.context.SecurityContextHolder"%>
<%@ page import="org.springframework.security.core.Authentication"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="jquery.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script>
	$(document).ready(function() {
						var color1 = $('#color').val();

						$(
								'#userdetails, #side-menu, #wrappers,.sidebar ul li a.active,.nav,.userdetails, .userdetails a, .sidebar-nav .nav-canvas, .navbar-default .navbar-toggle, .navbar-default .navbar-toggle:focus, .navbar-default .navbar-toggle.active')
								.css({
									"background-color" : color1
								});
						$(".nav > li > a:hover, .nav > li > a:focus").css({
							"background-color" : color1
						});
						$(".btn-default, .updatemobile, #updatemobile")
								.mouseover(function() {
									$(this).css({
										"border-color" : color1,
										"color" : color1
									});
								}).mouseout(function() {
									$(this).css({
										"border-color" : "#aeaeae",
										"color" : "#aeaeae"
									});
								});
						$(".rbar").css({
							"border-color" : color1
						});
						var str = window.location.pathname;
						var tpath = str.replace("/Loyalty/", "");
						if ($('#side-menu a').attr('href') == tpath) {
							// $(this).addClass("active");
							// $(this).append('<img id="theImg" src="theImg.png" />');
						}
					});
</script>
</head>
<body>
	<div class="ch-container">
	<%
		/*   Authentication  authentication=SecurityContextHolder.getContext().getAuthentication();
		 ContactsLoyalty contact=(ContactsLoyalty)authentication.getPrincipal(); */

		//	HttpSession session=request.getSession();
		ContactsLoyalty contact = (ContactsLoyalty) session
				.getAttribute("loyalityConfig");
	%>
		<div class="row">
			<!-- left menu starts -->
			<div class="col-sm-2 col-lg-2">
				<div class="sidebar">
					<div class="sidebar-nav">
						<div class="nav-canvas">
							<div class="nav-sm nav nav-stacked">
								<div class="userdetails">
									<core:set var="found" value="0"/>
									<%
										if (contact.getContact() != null) {
									%>
									<!-- <div class="title"> -->
										<%
											if (contact.getContact().getFirstName() != null) {
													
										%><core:set var="found" value="1"/>
										<%=contact.getContact().getFirstName()%>
										<%
											} else {
										%>
										<%
											}
										%>
										<%
											if (contact.getContact().getLastName() != null) {
													
										%><core:set var="found" value="2"/>
										<%=contact.getContact().getLastName()%><!-- </div> -->
									<%
										} else {
									%>
									<%
										}
									%>
									<%
										}
									%>
									<core:if test="${found > 0}">
									<div style="padding-bottom:5px;"></div>
									<!-- <br/> -->
									</core:if>


									<a href="changePassword">Change Password</a>
									<%
										String color = (String) session.getAttribute("colorCode");
										String filePath = (String) session.getAttribute("filepath");
									%>
									<input type="hidden" value="<%=color%>" id="color" /> <img
										src="resources/images/bar_line.png" width="2" height="15"
										class="bar">
									<c:url value="logout" var="logoutUrl" />
									 <%-- <a href="${logoutUrl}" id="logoutId2">Sign Out 2</a>   --%>
									<a href="${logoutUrl}" id="logoutId1" style="display:none;"></a>
									<a href="#" id="logoutId" onclick="window.location.href='./deleteCookies'">Sign Out</a>
								</div>
							</div>
							<ul id="side-menu" class="nav">
								<li><a href="membership" id="membership"><img
										src="resources/images/member.png" width="23" height="23">
										Membership Summary</a></li>
								<li><a href="activities" id="activities"><img
										src="resources/images/view.png" width="23" height="23">
										View Activities</a></li>
								<li><a href="profile" id="profile"><img
										src="resources/images/update.png" width="23" height="23">
										Update Profile</a></li>
							</ul>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

</body>
</html>