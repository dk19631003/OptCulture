package org.mq.loyality.common.dao;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.loyality.common.hbmbean.ContactsLoyalty;
import org.mq.loyality.utils.Constants;
import org.mq.loyality.utils.OCConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

public class RetailProSalesDao {

	@Autowired
	private SessionFactory sessionFactory;
	private static final Logger logger = LogManager
			.getLogger(Constants.LOYALTY_LOGGER);

	public RetailProSalesDao() {
	}

	@Transactional
	public Object getCumulativePurchase(Long userId, Long contactId,
			String startDate, String endDate) {

		List cumulativePurchaseArr = null;

		String qry = "SELECT ROUND(SUM((quantity*sales_price) + tax - IF(discount is null,0,discount)  ),2) FROM  retail_pro_sales  "
				+ " WHERE user_id="
				+ userId
				+ " AND cid = "
				+ contactId.longValue()
				+ " AND sales_date <='"
				+ startDate
				+ "' AND sales_date >='" + endDate + "'";
		logger.info("getCumulativePurchase >>>>>" + qry);
		cumulativePurchaseArr = sessionFactory.getCurrentSession()
				.createSQLQuery(qry).list();

		if (cumulativePurchaseArr != null && cumulativePurchaseArr.size() > 0) {
			return cumulativePurchaseArr.get(0);
		}
		return null;
	}

	@Transactional
	public int findTotalCount(String dateRange, String transactionType,
			ContactsLoyalty contactsLoyalty) {

		StringBuffer sb = new StringBuffer(
				"SELECT  count(distinct tc.trans_child_id) FROM retail_pro_sales rp"
						+ " INNER JOIN loyalty_transaction_child tc ON rp.doc_sid = tc.docsid  "
						+ " LEFT JOIN org_stores o ON o.home_store_id = tc.store_number and o.org_id=tc.org_id  where");
		if (dateRange != null) {
			StringTokenizer stringTokenizer = new StringTokenizer(dateRange,
					"-");
			try {
				while (stringTokenizer.hasMoreElements()) {
					String startDateStr = stringTokenizer.nextElement()
							.toString();
					String endDateStr = stringTokenizer.nextElement()
							.toString().trim();
					SimpleDateFormat inFmt = new SimpleDateFormat("MMM dd,yyyy");
					SimpleDateFormat outFmt = new SimpleDateFormat("yyyy-MM-dd");
					String outStart = outFmt.format(inFmt.parse(startDateStr));
					String outEnd = outFmt.format(inFmt.parse(endDateStr));
					sb.append(" tc.created_date  BETWEEN DATE('" + outStart
							+ "') AND (DATE('" + outEnd + "')+1) AND ");
				}
			} catch (ParseException e) {
				logger.info(" Exception ::: ", e);
			}
		}
		if (transactionType == null && dateRange == null) {
			sb.append(" tc.transaction_type IN ('"
					+ OCConstants.LOYALTY_TRANSACTION_REDEMPTION + "' , '"
					+ OCConstants.LOYALTY_TRANSACTION_ISSUANCE + "')");
		}

		else if (transactionType != null && transactionType.equals("1")) {
			sb.append(" tc.transaction_type IN ('"
					+ OCConstants.LOYALTY_TRANSACTION_REDEMPTION + "' , '"
					+ OCConstants.LOYALTY_TRANSACTION_ISSUANCE + "')");
		} else if (transactionType != null && transactionType.equals("3")) {
			sb.append(" tc.transaction_type IN ('"
					+ OCConstants.LOYALTY_TRANSACTION_REDEMPTION + "')");
		} else if (transactionType != null && transactionType.equals("2")) {
			sb.append(" tc.transaction_type IN ('"
					+ OCConstants.LOYALTY_TRANSACTION_ISSUANCE + "')");
		}
		if (contactsLoyalty != null) {
			sb.append(" AND ( tc.loyalty_id ="
					+ contactsLoyalty.getLoyaltyId() + " OR tc.transfered_to ="
							+ contactsLoyalty.getLoyaltyId()+")");
		}
		sb.append(" GROUP BY tc.loyalty_id ");

		try {
			List<BigInteger> list = sessionFactory.getCurrentSession()
					.createSQLQuery(sb.toString()).list();
			int max = 0;
			if (list != null && list.size() > 0) {
				for(BigInteger l:list){
					if(max < l.intValue())max=l.intValue();
				}
				return max;
			}
		} catch (DataAccessException e) {
			logger.info("Exception ::", e);
			return 0;
		}

		return 0;
	}
	
	@Transactional
	public int findTrxTotalCount(String dateRange, String transactionType,
			ContactsLoyalty contactsLoyalty) {

	/*	StringBuffer sb = new StringBuffer(
				"SELECT  count(distinct tc.trans_child_id) FROM retail_pro_sales rp"
						+ " INNER JOIN loyalty_transaction_child tc ON rp.doc_sid = tc.docsid  "
						+ " LEFT JOIN org_stores o ON o.home_store_id = tc.store_number and o.org_id=tc.org_id  where");*/
		StringBuffer sb = new StringBuffer(
				"SELECT  count(distinct tc.trans_child_id) FROM loyalty_transaction_child tc where");
		if (dateRange != null) {
			StringTokenizer stringTokenizer = new StringTokenizer(dateRange,
					"-");
			try {
				while (stringTokenizer.hasMoreElements()) {
					String startDateStr = stringTokenizer.nextElement()
							.toString();
					String endDateStr = stringTokenizer.nextElement()
							.toString().trim();
					SimpleDateFormat inFmt = new SimpleDateFormat("MMM dd,yyyy");
					SimpleDateFormat outFmt = new SimpleDateFormat("yyyy-MM-dd");
					String outStart = outFmt.format(inFmt.parse(startDateStr));
					String outEnd = outFmt.format(inFmt.parse(endDateStr));
					sb.append(" tc.created_date  BETWEEN DATE('" + outStart
							+ "') AND (DATE('" + outEnd + "')+1) AND ");
				}
			} catch (ParseException e) {
				logger.info(" Exception ::: ", e);
			}
		}
		if (transactionType == null && dateRange == null) {
			sb.append(" tc.transaction_type IN ('"
					+ OCConstants.LOYALTY_TRANSACTION_REDEMPTION + "' , '"
					+ OCConstants.LOYALTY_TRANSACTION_ISSUANCE + "' , '"
					+ OCConstants.LOYALTY_TRANSACTION_ADJUSTMENT + "')");
		}

		else if (transactionType != null && transactionType.equals("1")) {
			sb.append(" tc.transaction_type IN ('"
					+ OCConstants.LOYALTY_TRANSACTION_REDEMPTION + "' , '"
					+ OCConstants.LOYALTY_TRANSACTION_ISSUANCE + "' , '"
					+ OCConstants.LOYALTY_TRANSACTION_ADJUSTMENT + "')");
		} else if (transactionType != null && transactionType.equals("3")) {
			sb.append(" tc.transaction_type IN ('"
					+ OCConstants.LOYALTY_TRANSACTION_REDEMPTION + "')");
		} else if (transactionType != null && transactionType.equals("2")) {
			sb.append(" tc.transaction_type IN ('"
					+ OCConstants.LOYALTY_TRANSACTION_ISSUANCE + "')");
		}else if (transactionType != null && transactionType.equals("4")) {
			sb.append(" tc.transaction_type IN ('"
					+ OCConstants.LOYALTY_TRANSACTION_ADJUSTMENT + "')");
		}
		if (contactsLoyalty != null) {
			sb.append(" AND ( tc.loyalty_id ="
					+ contactsLoyalty.getLoyaltyId() + " OR tc.transfered_to ="
							+ contactsLoyalty.getLoyaltyId()+")");
		}
		sb.append(" GROUP BY tc.loyalty_id ");

		try {
			List<BigInteger> list = sessionFactory.getCurrentSession()
					.createSQLQuery(sb.toString()).list();
			int max = 0;
			if (list != null && list.size() > 0) {
				for(BigInteger l:list){
					if(max < l.intValue())max=l.intValue();
				}
				return max;
			}
		} catch (DataAccessException e) {
			logger.info("Exception ::", e);
			return 0;
		}

		return 0;
	}
	
}// EOF
