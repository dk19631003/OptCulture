<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Expires" content="Sat, 01 Dec 2001 00:00:00 GMT">
<title>${orgname} | Membership Details</title>
<%@ include file="jquery.jsp"%>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0,  maximum-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0,  maximum-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<script>
	$(document)
			.ready(
					function() {
						$("#membership")
								.addClass("active")
								.append(
										'<img id="theImg" style="right:0; position:absolute; margin:0;" src="resources/images/hover.png" />');
					});
</script>
<%@ include file="jquery.jsp"%>
</head>
<body>
	<%@ include file="top.jsp"%>
	<!-- topbar ends -->
	<%@ include file="leftmenu.jsp"%>
	</div>
	<!--/fluid-row-->
	</div>
	<!--/.fluid-container-->
	<div id="wrappers" class="inner_page">
		<div id="page-wrapper" style="min-height: 597px;">
			<div class="rbar">
				<div class="row">
					<div class="col-lg-12">
						<h1 class="page-header">Membership Summary</h1>
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
													<td width="20%">Membership #</td>
													<td width="5%">:</td>
													<td>${membership.memshipNo}</td>
												</tr>
												<tr style="margin-bottom: 10px;">
													<td>Member since</td>
													<td>:</td>
													<td>${membership.memshipSince}</td>
												</tr>
										</table>
										<c:if test="${not empty membership.memshipUpgradeOn}">
											<table class="table borderless" border="0" cellspacing="0"
												cellpadding="5">
												<tr>
													<td width="20%">Current Tier</td>
													<td width="5%">:</td>
													<td>${membership.currentTier}</td>
												</tr>
												<%--     <tr><td>Membership Upgrade On</td><td>:</td><td>${membership.memshipUpgradeOn}</td></tr> --%>



												<%-- <tr>
													<td width="20%">Next Upgrade On</td>
													<td width="5%">:</td>
													<td>${membership.memshipUpgradeOn}</td>
												</tr> --%>
											</table>
										</c:if>


										<table class="table borderless" border="0" cellspacing="0"
											cellpadding="5">
											<tr>
												<td width="20%">Current Points Balance</td>
												<td width="5%">:</td>
												<td>${membership.curBal}</td>
											</tr>
											<tr>
												<td>Current Currency Balance</td>
												<td>:</td>
												<td>${membership.currentCurrencyBal}</td>
											</tr>
										</table>


										<c:if test="${not empty membership.rewardsOnHold}">
											<table class="table borderless" border="0" cellspacing="0"
												cellpadding="5">
												<tr>
													<td width="20%">Reward On Hold</td>
													<td width="5%">:</td>
													<td>${membership.rewardsOnHold}</td>
												</tr>
												<tr>
													<td width="20%">Reward Activation Period</td>
													<td width="5%">:</td>
													<td>${membership.rewardActivationPeriod}</td>
												</tr>
											</table>
										</c:if>
										<table class="table borderless" border="0" cellspacing="0"
											cellpadding="5">
											<c:if test="${not empty membership.rewardExpiringThisMonth}">
												<tr>
													<td width="20%">Reward Expiring This Month</td>
													<td width="5%">:</td>
													<td>${membership.rewardExpiringThisMonth}</td>
												</tr>

											</c:if>
											</tbody>
										</table>
										

									</div>
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

	<script src="js/charisma.js"></script>
</body>
</html>