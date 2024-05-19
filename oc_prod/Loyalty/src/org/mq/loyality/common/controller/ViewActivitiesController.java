package org.mq.loyality.common.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.loyality.common.dao.ContactsLoyaltyDao;
import org.mq.loyality.common.dao.RetailProSalesDao;
import org.mq.loyality.common.hbmbean.ContactsLoyalty;
import org.mq.loyality.common.hbmbean.LoyaltySettings;
import org.mq.loyality.common.service.UserService;
import org.mq.loyality.utils.ActivityForm;
import org.mq.loyality.utils.Constants;
import org.mq.loyality.utils.DefaultPageNavigator;
import org.mq.loyality.utils.OCConstants;
import org.mq.loyality.utils.TransDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewActivitiesController {
	@Autowired
	private UserService userService;
	@Autowired
	private RetailProSalesDao retailProSalesDao;
	@Autowired
	private ContactsLoyaltyDao contactsLoyalityDao;
	private String trans;
	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);


	@RequestMapping(value = "/activities", method = RequestMethod.GET)
	public String totTransDetails(HttpServletRequest request,
			@RequestParam(value = "transactions", required = false) String is,
			@RequestParam(value = "reservation", required = false) String rd,
			Model model) throws ParseException {
		String startDateStr = null;
		String endDateStr = null;
		List<ContactsLoyalty> transferList=null;
		HttpSession session = request.getSession();
		ContactsLoyalty contactLoyality = null;
		if (session.getAttribute("loyalityConfig") != null) {
			contactLoyality = (ContactsLoyalty) session
					.getAttribute("loyalityConfig");
		}

		int pg = ServletRequestUtils.getIntParameter(request, "pg", 1);
		int ps = ServletRequestUtils.getIntParameter(request, "ps", 5);
		if(is == null || is.isEmpty()) is ="1";
		int totalCount;
		if(request.getAttribute("sess") == null || !request.getAttribute("sess").equals("yes") || session.getAttribute("totalTrans") == null){
		/*totalCount = retailProSalesDao.findTotalCount(rd, is,contactLoyality);
		session.setAttribute("totalTrans", totalCount);*/
		totalCount = retailProSalesDao.findTrxTotalCount(rd, is,contactLoyality);
		session.setAttribute("totalTrans", totalCount);
		}else{
			totalCount = (Integer)session.getAttribute("totalTrans");
		}
		List<TransDetails> totTransList = new ArrayList<TransDetails>();
		if (totalCount > 0) {
			String outStart = null;
			String outEnd = null;
			if(rd != null){
				StringTokenizer stringTokenizer = new StringTokenizer(rd, "-");
				while (stringTokenizer.hasMoreElements()) {
					startDateStr = stringTokenizer.nextElement().toString();
					endDateStr = stringTokenizer.nextElement().toString()
							.trim();
					SimpleDateFormat inFmt = new SimpleDateFormat("MMM dd,yyyy");
					SimpleDateFormat outFmt = new SimpleDateFormat("yyyy-MM-dd");
					outStart = outFmt.format(inFmt.parse(startDateStr));
					outEnd = outFmt.format(inFmt.parse(endDateStr));
				}
			}
			ContactsLoyalty contactsLoyalty = (ContactsLoyalty) session.getAttribute("loyalityConfig");
			transferList = contactsLoyalityDao.findChildrenByParent(contactsLoyalty.getUserId(), contactsLoyalty.getLoyaltyId());
			totTransList.addAll(getTrxList(is,pg,ps, outStart, outEnd,contactsLoyalty.getUserId(), contactsLoyalty.getLoyaltyId()));
			for(ContactsLoyalty eachTranferedCard:transferList){
				List<TransDetails> pageList = 	getTrxList( is,pg,ps, outStart, outEnd,eachTranferedCard.getUserId(), eachTranferedCard.getLoyaltyId());
				if (pageList !=null && pageList.size()>0){
					TransDetails transDetails = new TransDetails();
					transDetails.setTransactionType("Transactions with card # : "+eachTranferedCard.getCardNumber());
					totTransList.add(transDetails);
					totTransList.addAll(pageList);
				}
			}
			request.setAttribute("pageNav", new DefaultPageNavigator()
					.buildPageNav("#", totalCount, pg, ps, 2));
			request.setAttribute("subtractor", (pg * ps - ps));
			request.setAttribute("transList", totTransList);
		}
		request.setAttribute("reqtrans",  is);
		request.setAttribute("pageSize", ps);
		session.setAttribute("trans", is);
		session.setAttribute("date", rd != null ? rd : "");

		return "common/ViewActivities";
	}
	/*private List<TransDetails> getTrxList(String is,int pg,int ps,String outStart,String outEnd,Long userId,Long loyaltyId) throws ParseException{
		List<TransDetails> pageList = new ArrayList<TransDetails>();
		StringBuffer sb = new StringBuffer(
				"SELECT  SUM(rp.sales_price*rp.quantity+rp.tax	- IF(discount is null,0,discount)) as totPrice,rp.reciept_number, IFNULL(o.store_name,''),"
						+ " tc.created_date,tc.transaction_type, tc.docsid, "
						+ " tc.membership_number,tc.entered_amount_type,tc.amount_difference,tc.gift_difference,"
						+ " tc.transaction_type,tc.earned_points,tc.earned_amount,tc.points_difference, tc.entered_amount, tc.store_number "
						+ " FROM retail_pro_sales rp"
						+ " INNER JOIN loyalty_transaction_child tc ON rp.doc_sid = tc.docsid "
						+ " LEFT JOIN org_stores o ON o.home_store_id = rp.store_number and o.org_id=tc.org_id  where");
		if (outStart != null && outEnd != null) {
			
				sb.append(" tc.created_date  BETWEEN DATE('" + outStart
						+ "') AND (DATE('" + outEnd + "')+1) and  ");
			}
		
		if (is == null && outStart == null && outEnd == null) {
			sb.append(" tc.transaction_type IN ('"
					+ OCConstants.LOYALTY_TRANSACTION_REDEMPTION + "' , '"
					+ OCConstants.LOYALTY_TRANSACTION_ISSUANCE + "')");
		}

		else if (is != null && is.equals("1")) {
			sb.append(" tc.transaction_type IN ('"
					+ OCConstants.LOYALTY_TRANSACTION_REDEMPTION + "' , '"
					+ OCConstants.LOYALTY_TRANSACTION_ISSUANCE + "')");
		} else if (is != null && is.equals("3")) {
			sb.append(" tc.transaction_type IN ('"
					+ OCConstants.LOYALTY_TRANSACTION_REDEMPTION + "')");
		} else if (is != null && is.equals("2")) {
			sb.append(" tc.transaction_type IN ('"
					+ OCConstants.LOYALTY_TRANSACTION_ISSUANCE + "')");
		}
		if (loyaltyId != null) {
			sb.append(" AND tc.loyalty_id ="
				+ loyaltyId);
		}
		sb.append(" GROUP BY rp.doc_sid , tc.transaction_type ,tc.created_date,tc.docsid , o.store_name order by 4 desc");

		// List<TransDetails> list=userService.getTotTransDetails(sb);
		// pageSize = totalCount;
		
		 * int firstResult = pg * pageSize - pageSize;
		 * sb.append(" limit "+firstResult+" , "+pageSize);
		 
		logger.info("Transaction Query = "+sb);

		pageList = userService.getPageTransDetails(sb, pg, ps);
		return pageList;
	}*/
	private List<TransDetails> getTrxList(String is,int pg,int ps,String outStart,String outEnd,Long userId,Long loyaltyId) throws ParseException{
		List<TransDetails> pageList = new ArrayList<TransDetails>();
		logger.info("Entered into getTrcList method....");
		StringBuffer sb = new StringBuffer(
				"SELECT distinct(rp.reciept_number), tc.entered_amount as totPrice, IFNULL(o.store_name,''), "
						+ " tc.created_date,tc.transaction_type, tc.docsid, "
						+ " tc.membership_number,tc.entered_amount_type,tc.amount_difference,tc.gift_difference,"
						+ " tc.transaction_type,tc.earned_points,tc.earned_amount,tc.points_difference, tc.entered_amount, tc.earn_type, tc.user_id,tc.org_id "
						+ " FROM loyalty_transaction_child tc"
						+ " LEFT JOIN org_stores o ON o.home_store_id = tc.store_number and o.org_id=tc.org_id"
						+ " LEFT JOIN retail_pro_sales rp ON rp.cid=tc.contact_id and rp.doc_sid=tc.docsid where");
		if (outStart != null && outEnd != null) {
			
				sb.append(" tc.created_date  BETWEEN DATE('" + outStart
						+ "') AND (DATE('" + outEnd + "')+1) and  ");
			}
		
		if (is == null && outStart == null && outEnd == null) {
			sb.append(" tc.transaction_type IN ('"
					+ OCConstants.LOYALTY_TRANSACTION_REDEMPTION + "' , '"
					+ OCConstants.LOYALTY_TRANSACTION_ISSUANCE + "' , '"
					+ OCConstants.LOYALTY_TRANSACTION_ADJUSTMENT + "' )");
		}

		else if (is != null && is.equals("1")) {
			sb.append(" tc.transaction_type IN ('"
					+ OCConstants.LOYALTY_TRANSACTION_REDEMPTION + "' , '"
					+ OCConstants.LOYALTY_TRANSACTION_ISSUANCE + "' , '"
					+ OCConstants.LOYALTY_TRANSACTION_ADJUSTMENT + "' )");
		} else if (is != null && is.equals("3")) {
			sb.append(" tc.transaction_type IN ('"
					+ OCConstants.LOYALTY_TRANSACTION_REDEMPTION + "')");
		} else if (is != null && is.equals("2")) {
			sb.append(" tc.transaction_type IN ('"
					+ OCConstants.LOYALTY_TRANSACTION_ISSUANCE + "')");
		}else if (is != null && is.equals("4")) {
			sb.append(" tc.transaction_type IN ('"
					+ OCConstants.LOYALTY_TRANSACTION_ADJUSTMENT + "')");
		}
		
		if (loyaltyId != null) {
			sb.append(" AND tc.loyalty_id ="
				+ loyaltyId);
		}
		sb.append(" ORDER BY created_date DESC");

		// List<TransDetails> list=userService.getTotTransDetails(sb);
		// pageSize = totalCount;
		/*
		 * int firstResult = pg * pageSize - pageSize;
		 * sb.append(" limit "+firstResult+" , "+pageSize);
		 */
		logger.info("Transaction Query = "+sb);

		pageList = userService.getPageTransDetails(sb, pg, ps);
		return pageList;
	}
	@RequestMapping(value = "/viewBill", method = RequestMethod.GET)
	public String viewBill(HttpServletRequest request,
			@RequestParam(value = "id", required = false) String id, @RequestParam(value = "docSid", required = false) String docSid) {

		HttpSession session = request.getSession();
		String storeName = request.getParameter("storeName");
		LoyaltySettings settings = (LoyaltySettings) session
				.getAttribute("loyaltySettings");
		ContactsLoyalty contactsLoyalty = (ContactsLoyalty) session
				.getAttribute("loyalityConfig");

		List<ActivityForm> act = userService.getRecieptDetails(id,
				settings.getUserOrgId(), storeName, docSid, contactsLoyalty.getUserId());
		Double subTot = 0.00;
		Double tax = 0.00;
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		for (ActivityForm a : act) {
			subTot = subTot + a.getExtPrice();
			tax = tax + a.getTax();
		}

		Double total = subTot + tax;
		request.setAttribute("activity", act);
		request.setAttribute("tax", decimalFormat.format(tax));
		request.setAttribute("subTot",
				decimalFormat.format(new BigDecimal(subTot)));
		request.setAttribute("total", decimalFormat.format(total));
		return "common/bill";
	}
}
