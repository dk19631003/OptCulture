<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%-- <%
final String ctxPath1= request.getContextPath();
%> --%>
	 <link href="resources/css/bootstrap.min.css" rel="stylesheet">
    <link  href="resources/css/bootstrap-cerulean.min.css" rel="stylesheet">
     <link href="resources/css/metisMenu.min.css" rel="stylesheet">
    <link href="resources/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="resources/css/charisma-app.css" rel="stylesheet">
    <link href="resources/css/style.css" rel="stylesheet">
       <link href="resources/css/jquery.bdt.css" type="text/css" rel="stylesheet">
    <link href="resources/css/style.css" rel="stylesheet">
    <link href="resources/css/daterangepicker-bs3.css" rel="stylesheet">
    <link href="resources/css/jquery-ui.css" rel="stylesheet">
   
   <%--  <script src="resources/js/jquery-1.11.1.min.js"></script>
     <script src="resources/js/jquery.js"></script> --%>
       <script src="resources/js/jquery-1.11.1.min.js"></script>
     <script src="resources/js/jquery-ui.min.js"></script>
    <!-- Bootstrap Core JavaScript -->
    <script src="resources/js/bootstrap.min.js"></script>
    <script src="resources/js/metisMenu.min.js"></script>
	 <script src="resources/js/sb-admin-2.js"></script>
     <!-- <script src="resources/js/dataTables.bootstrap.js"></script> -->
     <!--  <script src="resources/js/jquery.dataTables.js"></script> -->
     <script src="resources/js/jquery.cookie.js"></script>
<%-- <script src="resources/js/charisma.js"></script> --%>   <!-- Custom Theme JavaScript -->
     <script src="resources/js/sb-admin-2.js"></script>
    <script src="resources/js/jquery.bdt.js" type="text/javascript"></script>
   <%--  <script src="resources/js/jquery-1.11.1.min.js" type="text/javascript"></script> --%>
     <script>
	

	function cls(){
		//	 var ln = $("#billdata").load("bill.html"); 
			/*$("#billdata").load("bill.html",function(responseTxt,statusTxt,xhr){
				if(statusTxt=="success")
				alert("External content loaded successfully!");
				if(statusTxt=="error")
				alert("Error: "+xhr.status+": "+xhr.statusText);
			}); */
		// jq(this).parents("tr.removable").remove();
	}
	function PrintElem(elem)
    {
        Popup($(elem).html());
    }

    function Popup(data) 
    {
        var mywindow = window.open('', 'my div', 'height=400,width=600');
        mywindow.document.write('<html><head><title>my div</title>');
        /*optional stylesheet*/ //mywindow.document.write('<link rel="stylesheet" href="main.css" type="text/css" />');
        mywindow.document.write('</head><body >');
        mywindow.document.write(data);
        mywindow.document.write('</body></html>');

        mywindow.document.close(); // necessary for IE >= 10
        mywindow.focus(); // necessary for IE >= 10

        mywindow.print();
        mywindow.close();

        return true;
    }
</script>
