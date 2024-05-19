<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="org.mq.loyality.utils.TransDetails"%>
<html lang="en">
<head>
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Expires" content="Sat, 01 Dec 2001 00:00:00 GMT">

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0,  maximum-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<title>${orgname} | View Activities</title>
<!-- The styles -->
<link href="resources/css/bootstrap.min.css" rel="stylesheet">
<link href="resources/css/bootstrap-cerulean.min.css" rel="stylesheet">
<link href="resources/css/datepicker3.css" rel="stylesheet">
<link href="resources/css/jquery.bdt.css" type="text/css"
	rel="stylesheet">
<link href="resources/css/charisma-app.css" rel="stylesheet">
<link href="resources/css/style.css" rel="stylesheet">
<link href="resources/css/daterangepicker-bs3.css" rel="stylesheet">
<script src="js/jquery.min.js"></script>
<style>
.date-form {
	margin: 10px;
}

label.control-label span {
	cursor: pointer;
}

#showbills {
	display: none !important;
}

#showbills.on {
	display: table-row !important;
}
</style>
<script>
	$(window).load(function() {
		$(".btn-success")
		.mouseover(function() {
			$(this).css({
				"backgound-color" : color1,
				//"color" : color1
				"border-color" : color1
			});
		})
		$(".btn-success").css({
			"background-color" : color1,
			"border-color" : color1
			//"color" : "#FFFFFF"
		});
	});
	var jq = $.noConflict();
	jq(document)
			.ready(
					function() {

						if (jq("#trans").val() != 'null' || jq("#trans").val() != '') {
							jq("#transactions").val(jq("#trans").val());
						}
						/* else
							{
							alert("in else");
							jq("#transactions").val(1);
							} */

						if (jq("#date").val() != 'null')
							jq('#reservation').val(jq("#date").val());

						jq("#activities")
								.addClass("active")
								.append(
										'<img id="theImg" style="right:0; position:absolute; margin:0;" src="resources/images/hover.png" />');
						color1 = jq('#color').val();
						jq(
								'#userdetails, #side-menu, #wrappers,.sidebar ul li a.active,.userdetails, .userdetails a')
								.css({
									"background-color" : color1
								});

						jq("#filter")
								.click(
										function() {
											var reservation = jq('#reservation')
													.val();
											var transaction = jq(
													'#transactions').val();
											var dateArray = reservation
													.split('-');
											if (Date.parse(dateArray[0] < Date
													.parse(dateArray[1]))) {
												alert('End Date should be greater than equal to Start Date');
												return false;
											}
											jq.ajax({
												url : '/activities',
												data : "transactions="
														+ transaction
														+ "&reservation="
														+ reservation,
												type : "GET",
												success : function(response) {

												},
												error : function(xhr, status,
														error) {
													//alert(xhr.responseText);
												}
											});
										});
						
						$('#itemsPerPageId').change(function(){
				        	var pagesize = $("#itemsPerPageId option:selected").text();
				        	var reservation = jq('#reservation').val();
				        	reservation = reservation.replace(/ /g,"+");
				        	reservation = reservation.replace(/,/g,"%2C");
							var transaction = jq('#transactions').val();
				           	$('#box_table').empty().html('<div style="text-align:center; margin:130px 0;"><img src="<%=request.getContextPath()%>/resources/images/ajax-loader.gif" /></div>');
				           	$('#entirePAgeId').load('<%=request.getContextPath()%>/activities?sess=yes&ps='+pagesize+'&transactions='+transaction+'&reservation='+reservation);
				           	e.preventDefault();
				        });
				        
						jq(".viewBill").click(
								function() {
									
									if(jq(this).parents("tr").next("tr.removable").hasClass('shw')) {
										//alert("clicked");
										jq(this).parents("tr").next("tr.removable")
										.removeClass("shw");
									}
									
									else{
									jq(this).parents("tr").next("tr.removable")
											.addClass("shw");
									var status_id = jq(this).attr('href');
									var arr = status_id.split("_");
									$.ajax({
										url : 'viewBill',
										data : "id=" + arr[0] + "&storeName="
												+ arr[2]+ "&docSid="
												+ arr[3],
										type : "GET",
										success : function(response) {
											jq('#helo').val(response);
											jq("#billdata_" + arr[1]).html(
													response);
										}
									});
									}
									//removable
									jq(".clsbtn")
											.click(
													function() {
														// alert("clicked");
														jq(this).closest(
																"tr.removable")
																.removeClass(
																		"shw");
													});
									return false;
								});
					});

	function PrintElem(elem) {
		Popup($(elem).html());
	}
	function Popup(data) {
		var mywindow = window.open('', 'my div', 'height=400,width=600');
		mywindow.document.write('<html><head><title>my div</title>');
		/*optional stylesheet*///mywindow.document.write('<link rel="stylesheet" href="main.css" type="text/css" />');
		mywindow.document.write('</head><body >');
		mywindow.document.write(data);
		mywindow.document.write('</body></html>');
		mywindow.document.close(); // necessary for IE >= 10
		mywindow.focus(); // necessary for IE >= 10
		mywindow.print();
		return false;
	}
</script>
</head>
<body>
	<div id="entirePAgeId">
		<%@ include file="top.jsp"%>
		<%@ include file="leftmenu.jsp"%>
		<div id="wrappers" class="inner_page">
			<div id="page-wrapper">
				<div class="rbar">
					<div class="row">
						<div class="col-lg-12">
							<h1 class="page-header">View Activities</h1>
						</div>
						<div class="col-lg-12">
							<div class="filter_box">
								<form class="form-inline" role="form">
									<div class="form-group">
										<label for="Transactions">View Transactions:</label> <select
											name="transactions" id="transactions" class="form-control" id="select_grt" >
											<option value="1"  <c:if test="${reqtrans =='1'}"> selected </c:if> >All</option>
											<option value="2" <c:if test="${reqtrans =='2'}"> selected </c:if> >Issuance</option>
											<option value="3" <c:if test="${reqtrans =='3'}"> selected </c:if> >Redemption</option>
											<option value="4" <c:if test="${reqtrans =='4'}"> selected </c:if> >Adjustment</option>
										</select>
									</div>
									<div class="form-group">
										<label for="Date">Date:</label>
										<div class="input-prepend input-group">
											<input type="text" name="reservation" id="reservation" autocomplete="off"
												class="form-control" value="<%=(String)session.getAttribute("date")%>"  /> <span
												class="add-on input-group-addon"><i
												class="glyphicon glyphicon-calendar fa fa-calendar"></i></span>
										</div>
									</div>
									<button type="submit" id="filter"
										class="btn btn-lg btn-default submit">Filter</button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										<button type="button" id="reset"
										class="btn btn-lg btn-default submit" >Reset</button>
								</form>
							</div>
							<div class="panel panel-default">
								<div class="table-responsive" id="box_table"
									style="overflow: auto;">
									<table class="table table-striped table-bordered table-hover"
										id="bootstrap-table">
										<thead>
											<tr>
												<th data-placeholder="Try 1/18/2013 12:12:12">Date</th>
												<th>Receipt #</th>
												<th class="total">Details</th>
												<th class="total">Receipt Amount</th>
												<th class="total">Balance Diff</th>
											</tr>
										</thead>
										<tbody>
											<%List<TransDetails> eList =(List<TransDetails>)request.getAttribute("transList");
									if(eList!=null && eList.size()>0){
									int i=1;
									for(TransDetails t:eList){
										if(t.getTransactionType() != null && !t.getTransactionType().contains("Transactions with card # :")){
											Long rec=t.getRecieptNumber();
									%>
											<tr id="bills">
												<td><%=t.getRecieptDate() %></td>
												<td><%=t.getRecieptNumber() != null ? t.getRecieptNumber() : "" %><%--  <a
													href="<%=t.getRecieptNumber()%>_<%=i %>_<%=t.getStoreName()%>_<%=t.getDocSid() %>"
													id="trapp"
													style="text-decoration: none; border-bottom: 1px solid ${colorCode};"
													class="viewBill"><font
														color=<%=session.getAttribute("colorCode")%>>View
															bill</font></a>  --%></td>
												<td>
												<% String store;%>
												<%if(t.getTransactionType().equals("Purchase")) {%>
													<%store= "Purchased";if(!t.getStoreName().isEmpty()) store+=" at ";%>
													<%=store+t.getStoreName() %>
												<%} else if((t.getTransactionType().equals("PointsRedeem") || (t.getTransactionType().equals("AmountRedeem")))){ %>
													<%store= "Redeemed";if(!t.getStoreName().isEmpty()) store+=" at ";%>
													<%=store+t.getStoreName() %>
												<%} else if((t.getTransactionType().equals("Add") || (t.getTransactionType().equals("Sub")))){ %>
													<%="Adjustment"%>
												<%} %></td>
												<td> <%=t.getRecieptAmount()%></td>
												<%if(t.getTransactionType().equals("PointsRedeem")) {%>
												<td><%=t.getLoyalityBal() %></td>
												<%} else{%>
												<td><%=t.getLoyalityBal() %></td>
												<%} %>
											</tr>
											<tr class='removable' style="display: none;">
												<td colspan='5'><div class="bdata"
														id='billdata_<%=i %>'></div>
													<div class='ics-set'>
														<img src="resources/images/cls-icon.png" class="clsbtn" />
														<img src="resources/images/prnt-icon.png"
															onclick="PrintElem('#billdata_<%=i %>')" class="pintbtn" />
													</div></td>
											</tr>
											<%}else{ %>
											<tr>
												<td colspan='5' ><label class="total"><%=t.getTransactionType()%></label></td>
											</tr>
											<%}
											i++;
                        } }else{
                        %>

											<tr id="bills">
												<td colspan="6" align="center">No data found</td>
											</tr>
											<%} %>
											<input id="helo" type="hidden" />
											<input type="hidden" value="<%=color%>" id="color" />
											<input type="hidden"
												value="<%=(String)session.getAttribute("trans")%>"
												id="trans" />
											<input type="hidden"
												value="<%=(String)session.getAttribute("date")%>" id="date" />

										</tbody>
									</table>
								
								<div id="table-footer" class="row" style="margin-right:0;"><div class="pull-left form-horizontal">
								<label class="pull-left control-label">Entries per Page:</label>
								<div class="pull-left">
								<select ID="itemsPerPageId" class="form-control">
									<c:choose>
									<c:when test="${pageSize=='5'}"><option value="5" selected>5</option></c:when>
									<c:otherwise><option value="5">5</option></c:otherwise></c:choose>
									<c:choose>
									<c:when test="${pageSize=='10'}"><option value="10" selected>10</option></c:when>
									<c:otherwise><option value="10">10</option></c:otherwise></c:choose><c:choose>
									<c:when test="${pageSize=='15'}"><option value="15" selected>15</option></c:when>
									<c:otherwise><option value="15">15</option></c:otherwise></c:choose><c:choose>
									<c:when test="${pageSize=='20'}"><option value="20" selected>20</option></c:when>
									<c:otherwise><option value="20">20</option></c:otherwise></c:choose>
								</select>
								</div>
								<nav class="pull-right" id="table-nav"><div class="pagination pull-right">
								<c:if test="${not empty transList}">
									<div class="page_nav pull-right">${pageNav}</div>
								</c:if>
								</div></nav>
								</div></div></div>
							</div>
						</div>
					</div>
					<!-- /.row -->
				</div>
				<!-- /.container-fluid -->
			</div>
			<div></div>
		</div>
		<script src="js/jquery.js"></script>
		<script type="text/javascript" src="js/moment.js"></script>
		<!-- Bootstrap Core JavaScript -->
		<script src="js/bootstrap.min.js"></script>
		<script type="text/javascript" src="js/daterangepicker.js"></script>

		<!-- Metis Menu Plugin JavaScript -->
		<script src="js/plugins/metisMenu/metisMenu.min.js"></script>

		<!-- Custom Theme JavaScript -->
		<script src="js/sb-admin-2.js"></script>

		<script>
		$(document).ready(function() {
			/* $('#bootstrap-table').bdt(); */
			$("div.id_100 select").val("15");
			$('#reservation').daterangepicker({
				format : 'MMMM DD, YYYY'
			}, function(start, end, label) {
				console.log(start.toISOString(), end.toISOString(), label);
			});
			 
		});
	</script>
		<script type="text/JavaScript">
    $(function(){
        $('.page_nav a').click(function(e) {
        	var reservation = jq('#reservation').val();
			var transaction = jq('#transactions').val();
			reservation = reservation.replace(/ /g,"+");
        	reservation = reservation.replace(/,/g,"%2C");
       		var pagesize = $("#itemsPerPageId option:selected").text();
     	$('#box_table').empty().html('<div style="text-align:center; margin:130px 0;"><img src="<%=request.getContextPath()%>/resources/images/ajax-loader.gif" /></div>');
         $('#entirePAgeId').load('<%=request.getContextPath()%>/activities?sess=yes&ps='+pagesize+'&pg='+ $(this).text()+'&transactions='+transaction+'&reservation='+reservation);
           	e.preventDefault();
								});
        $("#prevId").click(function(){
        	var pagesize = $("#itemsPerPageId option:selected").text();
        	var pg = $("#prevId").attr("value");
        	var reservation = jq('#reservation').val();
        	reservation = reservation.replace(/ /g,"+");
        	reservation = reservation.replace(/,/g,"%2C");
			var transaction = jq('#transactions').val();
        	$('#box_table').empty().html('<div style="text-align:center; margin:130px 0;"><img src="<%=request.getContextPath()%>/resources/images/ajax-loader.gif" /></div>');
        	$('#entirePAgeId').load('<%=request.getContextPath()%>/activities?sess=yes&ps='+pagesize+'&pg='+pg+'&transactions='+transaction+'&reservation='+reservation);
        });
        $("#reset").click(function(){
        	$('#box_table').empty().html('<div style="text-align:center; margin:130px 0;"><img src="<%=request.getContextPath()%>/resources/images/ajax-loader.gif" /></div>');
			$('#entirePAgeId').load('<%=request.getContextPath()%>/activities');
		});
         $("#nextId").click(function(){
        	var pagesize = $("#itemsPerPageId option:selected").text();
        	var pg = $("#nextId").attr("value");
        	var reservation = jq('#reservation').val();
        	reservation = reservation.replace(/ /g,"+");
        	reservation = reservation.replace(/,/g,"%2C");
			var transaction = jq('#transactions').val();
        	$('#box_table').empty().html('<div style="text-align:center; margin:130px 0;"><img src="<%=request.getContextPath()%>/resources/images/ajax-loader.gif" /></div>');
           	$('#entirePAgeId').load('<%=request.getContextPath()%>/activities?sess=yes&ps='+pagesize+'&pg='+pg+'&transactions='+transaction+'&reservation='+reservation);
        }); 
        
			});
 <%-- function paging(pageSize){

	 $('#entirePAgeId').empty().html('<img src="<%=request.getContextPath()%>/resources/images/ajax-loader.gif" />');
     $('#entirePAgeId').load('<%=request.getContextPath()%>/activities?ps=pageSize&pg='+ $(this).text());
    	e.preventDefault();
    	
    } --%>
		</script>
		<script src="js/jquery.cookie.js"></script>
		<script src="js/charisma.js"></script>
		<!-- <script src="js/vendor/jquery.sortelements.js" type="text/javascript"></script> --><!-- 
		<script src="js/jquery.bdt.js" type="text/javascript"></script> -->
	</div>
</body>
</html>
