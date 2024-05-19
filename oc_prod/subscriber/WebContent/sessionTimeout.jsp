<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="shortcut icon" type="image/x-icon" href="img/favicon.ico" />
<link rel="icon" type="image/x-icon" href="img/favicon.ico" />
<title>Account</title>
<style type="text/css">
body {
	padding: 0px;
	margin: 0px;
}

img {
	border: 0px;
}

h3 {
	font: 18px Tahoma;
	color: #FFF;
	letter-spacing: 1px;
	padding: 0px;
	margin: 0px;
}

input {
	font: 11px arial;
	color: #6d6e71;
}

td {
	color: #FFFFFF;
	font-size: 11px;
}

#mainContainer {
	width: 100%;
}

#header {
	width: 100%;
	border-bottom: 1px solid #b6b6b8;
	position: relative;
	display: block;
	overflow: hidden;
}

.logo {
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

#messageArea {
	width: 620px;
	height: 250px;
	font: 12px verdana;
	color: #333 margin-bottom : 75px;
	margin-top: 50px;
	padding-bottom: 50px;
	padding-left: 50px;
	padding-right: 50px;
	padding-top: 50px;
	margin: auto;
}

.but {
	font: 11px arial bold;
	color: #FFF;
	background-color: #275b6d;
	padding: 5px 10px;
	text-transform: uppercase;
	border: 0px;
}

#footer {
	background: url(img/fotterBG.gif) no-repeat center center;
	padding: 30px 0px;
	font: 11px verdana;
	color: #6d6e71;
	text-align: center;
}

#errorDivId {
	font-style: arial;
	color: red;
	font-size: 11px;
}
</style>
<script type="text/javascript" src="js/jsconfig.js"></script>
<script type="text/javascript">
				function relogin(){
					window.location.href = "/subscriber";
				}
			</script>
</head>
<body>
<div id="mainContainer">
<!--
<jsp:include page="header.jsp"></jsp:include>
-->
<div id="messageArea">The page or component you request is no
longer available. This is normally caused by timeout, opening too many
Web pages, or rebooting the server. <a href="javascript:relogin();">Click
here</a> to relogin.</div>
<jsp:include page="footer.jsp"></jsp:include></div>
</body>
</html>
