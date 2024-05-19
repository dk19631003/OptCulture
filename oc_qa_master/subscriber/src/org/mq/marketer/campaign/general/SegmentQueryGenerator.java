package org.mq.marketer.campaign.general;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SegmentQueryGenerator {

	private final Logger logger = LoggerFactory.getLogger(SegmentQueryGenerator.class);

	private String contactSelectQuery = "SELECT DISTINCT c.cid FROM contacts c <CONTACT_LEFT_JOIN> JOIN (SELECT * FROM contacts_loyalty cl WHERE cl.user_id = <USER_ID>) loyalty ON c.cid = loyalty.contact_id WHERE c.user_id = <USER_ID> ";

	private StringBuilder contactWhereCondAND = new StringBuilder("");

	private String activeEmailCond = " AND (c.email_id IS NOT NULL AND c.email_id != '' AND c.email_status = 'Active')  GROUP BY c.email_id";
	private String activeMobileCond = " AND (c.mobile_phone IS NOT NULL AND c.mobile_phone != '' AND c.mobile_status = 'Active')  GROUP BY c.mobile_phone";

	private String salesSelectQuery = "SELECT sal.cid FROM retail_pro_sales sal LEFT JOIN (SELECT * FROM retail_pro_sku k WHERE k.user_id = <USER_ID>) sku ON sal.inventory_id = sku.sku_id WHERE sal.user_id = <USER_ID> AND sal.cid IS NOT NULL ";
	private StringBuilder salesWhereCondAND = new StringBuilder("");
	private StringBuilder salesHavingCondAND = new StringBuilder("GROUP BY sal.cid HAVING");
	
	private String couponsSelectQuery = "SELECT cd.contact_id AS cid FROM coupon_codes cd JOIN (SELECT * FROM coupons WHERE user_id = <USER_ID>) cp ON cp.coupon_id = cd.coupon_id WHERE cd.contact_id IS NOT NULL ";
	private StringBuilder couponsWhereCondAND = new StringBuilder("");
	private StringBuilder couponsHavingCondAND = new StringBuilder("GROUP BY cd.contact_id HAVING");

	private static List<String> contactWhereFields = new ArrayList<>();

	private static List<String> salesWhereFields = new ArrayList<>();
	private static Map<String, String> salesHavingFieldsMap = new HashMap<>();
	
	private static List<String> couponsWhereFields = new ArrayList<>();

	private static Map<String, String> tokensMap = new HashMap<>();

	private boolean isContactSegmentsSelected = false;
	private boolean isSalesSegmentsSelected = false;
	private boolean isCouponSegmentsSelected = false;

	private static final String DELIMETER_COMMA = ",";

	static {
		// CONTACT
		contactWhereFields.add("Date(c.created_date)");
		contactWhereFields.add("c.email_id");
		contactWhereFields.add("c.city");
		contactWhereFields.add("c.state");
		contactWhereFields.add("c.country");
		contactWhereFields.add("c.zip");
		contactWhereFields.add("c.gender");
		contactWhereFields.add("c.home_store");
		contactWhereFields.add("c.optin_medium");
		contactWhereFields.add("Date(c.birth_day)");
		contactWhereFields.add("Date(c.anniversary_day)");
		contactWhereFields.add("c.udf1");
		contactWhereFields.add("c.udf2");
		contactWhereFields.add("c.udf3");
		contactWhereFields.add("c.udf4");
		contactWhereFields.add("c.udf5");
		contactWhereFields.add("c.udf6");
		contactWhereFields.add("c.udf7");
		contactWhereFields.add("c.udf8");
		contactWhereFields.add("c.udf9");
		contactWhereFields.add("c.udf10");
		contactWhereFields.add("c.udf11");
		contactWhereFields.add("c.udf12");
		contactWhereFields.add("c.udf13");
		contactWhereFields.add("c.udf14");
		contactWhereFields.add("c.udf15");
		// CONTACT LOYALTY
		contactWhereFields.add("Date(loyalty.created_date)");
		contactWhereFields.add("loyalty.program_tier_id");
		contactWhereFields.add("loyalty.membership_status");
		contactWhereFields.add("loyalty.loyalty_balance");
		contactWhereFields.add("loyalty.giftcard_balance");
		contactWhereFields.add("loyalty.total_loyalty_earned");

		// SALES
		salesWhereFields.add("Date(sal.sales_date)");
		salesWhereFields.add("sal.store_number");
		salesWhereFields.add("sal.subsidiary_number");
		salesWhereFields.add("sal.udf1");
		salesWhereFields.add("sal.udf2");
		salesWhereFields.add("sal.udf3");
		salesWhereFields.add("sal.udf4");
		salesWhereFields.add("sal.udf5");
		salesWhereFields.add("sal.udf6");
		salesWhereFields.add("sal.udf7");
		salesWhereFields.add("sal.udf8");
		salesWhereFields.add("sal.udf9");
		salesWhereFields.add("sal.udf10");
		salesWhereFields.add("sal.udf11");
		salesWhereFields.add("sal.udf12");
		salesWhereFields.add("sal.udf13");
		salesWhereFields.add("sal.udf14");
		salesWhereFields.add("sal.udf15");
		salesWhereFields.add("sal.promo_code");
		salesWhereFields.add("sal.sku");
		//SKU
		salesWhereFields.add("sku.item_category");
		salesWhereFields.add("sku.department_code");
		salesWhereFields.add("sku.description");
		salesWhereFields.add("sku.class_code");
		salesWhereFields.add("sku.subclass_code");
		salesWhereFields.add("sku.dcs");
		salesWhereFields.add("sku.vendor_code");
		salesWhereFields.add("sku.udf1");
		salesWhereFields.add("sku.udf2");
		salesWhereFields.add("sku.udf3");
		salesWhereFields.add("sku.udf4");
		salesWhereFields.add("sku.udf5");
		salesWhereFields.add("sku.udf6");
		salesWhereFields.add("sku.udf7");
		salesWhereFields.add("sku.udf8");
		salesWhereFields.add("sku.udf9");
		salesWhereFields.add("sku.udf10");
		salesWhereFields.add("sku.udf11");
		salesWhereFields.add("sku.udf12");
		salesWhereFields.add("sku.udf13");
		salesWhereFields.add("sku.udf14");
		salesWhereFields.add("sku.udf15");

		salesHavingFieldsMap.put("aggr_tot", "SUM((sal.sales_price * sal.quantity) + sal.tax - (IF(sal.discount is null, 0, sal.discount)))"); // Total Purchase Amount
		salesHavingFieldsMap.put("aggr_avg", "(SUM((sal.sales_price * sal.quantity) + sal.tax - (IF(sal.discount is null, 0, sal.discount))) / COUNT(sal.cid))");
		salesHavingFieldsMap.put("aggr_count", "COUNT(sal.cid)");
		//salesHavingFieldsMap.put("aggr.tot_reciept_count", "COUNT(distinct doc_sid)");

		couponsWhereFields.add("cd.coupon_id");
		couponsWhereFields.add("cp.coupon_id");
		couponsWhereFields.add("cd.status");
		couponsWhereFields.add("date(cd.issued_on)");
		couponsWhereFields.add("date(cd.redeemed_on)");
		couponsWhereFields.add("cp.coupon_expiry_date");

		tokensMap.put("string:is", " <COLUMN_NAME> = '<VALUE>'");
		tokensMap.put("string:equal", " <COLUMN_NAME> = '<VALUE>'");
		tokensMap.put("string:equals", "<COLUMN_NAME> = '<VALUE>'");
		tokensMap.put("string:equal to", "  <COLUMN_NAME> = '<VALUE>'");
		tokensMap.put("string:is equal", "<COLUMN_NAME> = '<VALUE>'");
		tokensMap.put("string:not equal", "<COLUMN_NAME> != '<VALUE>' AND <COLUMN_NAME> != '' ");
		tokensMap.put("string:not equals", " <COLUMN_NAME> != '<VALUE>' AND <COLUMN_NAME> != ''");
		tokensMap.put("string:not equal to", "<COLUMN_NAME> != '<VALUE>' AND <COLUMN_NAME> != ''");
		tokensMap.put("string:is not", " <COLUMN_NAME> != '<VALUE>' AND <COLUMN_NAME> != ''");
		tokensMap.put("string:is not equal", "<COLUMN_NAME> != '<VALUE>' AND <COLUMN_NAME> != ''");
		tokensMap.put("string:is in", " <COLUMN_NAME> IN (<VALUE>)");
		tokensMap.put("string:is not in", " <COLUMN_NAME> NOT IN (<VALUE>)");
		tokensMap.put("string:contains", " <COLUMN_NAME> ILIKE '%<VALUE>%'");
		tokensMap.put("string:does not contain", " <COLUMN_NAME> NOT ILIKE '%<VALUE>%' AND <COLUMN_NAME> != ''");
		tokensMap.put("string:starts with", " <COLUMN_NAME> ILIKE '<VALUE>%'");
		tokensMap.put("string:ends with", " <COLUMN_NAME> ILIKE '%<VALUE>'");
		tokensMap.put("string:none", " <COLUMN_NAME> = ''");
		tokensMap.put("string:only like", "arrayAll(x -> <VALUE>, groupUniqArray(<COLUMN_NAME>))");
		tokensMap.put("string:is value", " <COLUMN_NAME> <VALUE>");
		tokensMap.put("string:is in redeem", " <COLUMN_NAME> IN (<VALUE>)");
		tokensMap.put("string:is not in redeem", " <COLUMN_NAME> IN (<VALUE>)");
		tokensMap.put("string:is issued", " <COLUMN_NAME> IN ('<VALUE>')");
		tokensMap.put("string:is redeemed", " <COLUMN_NAME> IN ('<VALUE>')");
		tokensMap.put("string:is expired", " <COLUMN_NAME> IN ('<VALUE>')");

		tokensMap.put("number:=", " <COLUMN_COND> = '<VALUE>'");
		tokensMap.put("number:!=", " <COLUMN_COND> != '<VALUE>'");
		tokensMap.put("number:>", " <COLUMN_COND> > '<VALUE>'");
		tokensMap.put("number:<", " <COLUMN_COND> < '<VALUE>'");
		tokensMap.put("number:none", " <COLUMN_COND> <VALUE>");
		tokensMap.put("number:is not in", " <COLUMN_COND> NOT IN (<VALUE>)");
		tokensMap.put("number:between", " <COLUMN_COND> BETWEEN '<VALUE1>' AND '<VALUE2>'");
		tokensMap.put("number:date_diff_equal_to", " DATEDIFF('dd', MAX(<DATE_FIELD>), NOW()) = <VALUE> ");

		tokensMap.put("date:is", " <DATE_FIELD> = '<VALUE>'");
		tokensMap.put("date:istoday", " DATEDIFF('dd', <DATE_FIELD>, now()) = 0 " );
		tokensMap.put("date:between", " <DATE_FIELD> BETWEEN '<VALUE1>' AND '<VALUE2>'");
		tokensMap.put("date:after", " <DATE_FIELD> > '<VALUE>'");
		tokensMap.put("date:before", " <DATE_FIELD> < '<VALUE>'");
		tokensMap.put("date:onorafter", " <DATE_FIELD> >= '<VALUE>'");
		tokensMap.put("date:onorbefore", " <DATE_FIELD> <= '<VALUE>'");
		tokensMap.put("date:range_between", " DATEDIFF('dd', <DATE_FIELD>, now()) >= <VALUE1> AND DATEDIFF('dd', <DATE_FIELD>, now()) <= <VALUE2> ");
		tokensMap.put("date:withinlast", "DATEDIFF('dd', <DATE_FIELD>, now()) >= 0 AND DATEDIFF('dd', <DATE_FIELD>, now()) <= <VALUE>"); // date:withinlast_days, weeks, months
		tokensMap.put("date:notwithinlast", "DATEDIFF('dd', max(<DATE_FIELD>), now()) >= <VALUE>");
		tokensMap.put("date:none", " <DATE_FIELD> IS NULL");
		tokensMap.put("date:equal to", " <DATE_FIELD> = '<VALUE>'");
		tokensMap.put("date:iswithinnext", "DATEDIFF('dd', now(), <DATE_FIELD>) >= 0 AND DATEDIFF('dd', now(), <DATE_FIELD>) <= <VALUE>");

		tokensMap.put("date:ignoreyear_before", "((toMonth(<DATE_FIELD>), toDayOfMonth(<DATE_FIELD>)) < (toMonth(Date('<VALUE>')), toDayOfMonth(Date('<VALUE>'))))");
		tokensMap.put("date:ignoreyear_after", "((toMonth(<DATE_FIELD>), toDayOfMonth(<DATE_FIELD>)) > (toMonth(Date('<VALUE>')), toDayOfMonth(Date('<VALUE>'))))");

		tokensMap.put("date:ignoreyear_onorbefore", "((toMonth(<DATE_FIELD>), toDayOfMonth(<DATE_FIELD>)) <= (toMonth(Date('<VALUE>')), toDayOfMonth(Date('<VALUE>'))))");
		tokensMap.put("date:ignoreyear_onorafter", "((toMonth(<DATE_FIELD>), toDayOfMonth(<DATE_FIELD>)) >= (toMonth(Date('<VALUE>')), toDayOfMonth(Date('<VALUE>'))))");
//		tokensMap.put("date:ignoreyear_between", "(((toMonth(<DATE_FIELD>), toDayOfMonth(<DATE_FIELD>)) >= (toMonth(Date('<VALUE1>')), toDayOfMonth(Date('<VALUE1>')))) OR ((toMonth(<DATE_FIELD>), toDayOfMonth(<DATE_FIELD>)) <= (toMonth(Date('<VALUE2>')), toDayOfMonth(Date('<VALUE2>')))))");
		tokensMap.put("date:ignoreyear_between", "((toMonth(<DATE_FIELD>), toDayOfMonth(<DATE_FIELD>)) BETWEEN (toMonth(Date('<VALUE1>')), toDayOfMonth(Date('<VALUE1>'))) AND (toMonth(Date('<VALUE2>')), toDayOfMonth(Date('<VALUE2>'))))");
		tokensMap.put("date:ignoreyear_is", "((toMonth(<DATE_FIELD>), toDayOfMonth(<DATE_FIELD>)) = (toMonth(Date('<VALUE>')), toDayOfMonth(Date('<VALUE>'))))");

		tokensMap.put("date:after_between", " (DATEDIFF('dd', toDate32('<ZEROYEAR_DATE>'), ((<DATE_FIELD> + INTERVAL 1 YEAR) - INTERVAL YEAR(<DATE_FIELD>)-1900 YEAR)) % 365 >= <VALUE1> AND DATEDIFF('dd', toDate32('<ZEROYEAR_DATE>'), ((<DATE_FIELD> + INTERVAL 1 YEAR) - INTERVAL YEAR(<DATE_FIELD>)-1900 YEAR)) % 365 <= <VALUE2>) ");
		tokensMap.put("date:before_between", " (DATEDIFF('dd', (<DATE_FIELD> - INTERVAL YEAR(<DATE_FIELD>)-1900 YEAR), toDate32('<FIRSTYEAR_DATE>')) % 365 >= <VALUE1> AND DATEDIFF('dd', (<DATE_FIELD> - INTERVAL YEAR(<DATE_FIELD>)-1900 YEAR), toDate32('<FIRSTYEAR_DATE>')) % 365 <= <VALUE2>) ");
		tokensMap.put("date:notbefore", " MAX(<DATE_FIELD>) > DATE('<VALUE>') ");
		tokensMap.put("date:notafter", " MAX(<DATE_FIELD>) < DATE('<VALUE>') ");
	}

	public String getSegmentQuery(String segmentRule, String type) {

		logger.info("SEG-RULE :: {}", segmentRule);
		logger.info("SEG-TYPE :: {}", type);

		String finalQuery = "";

		String userIdStr = "";

		for (String andSegmentRule : segmentRule.split("\\|\\|")) {

			if (andSegmentRule.startsWith("all:")) {
				userIdStr = andSegmentRule.split(":")[1];
				continue;
			}

			processSegmentRuleAND(andSegmentRule);

		}

		
		String finalContactQuery = "";

		if (isContactSegmentsSelected)
			contactSelectQuery += contactWhereCondAND.toString();

		if (isSalesSegmentsSelected) {
			String salesHavingStr = salesHavingCondAND.toString().equals("GROUP BY sal.cid HAVING") ? ""
					: salesHavingCondAND.toString();
			contactSelectQuery += " AND c.cid IN (" + salesSelectQuery + salesWhereCondAND + salesHavingStr + ")";
		}

		if (isCouponSegmentsSelected) {
			String couponsHavingStr = couponsHavingCondAND.toString().equals("GROUP BY cd.contact_id HAVING") ? ""
					: couponsHavingCondAND.toString();
			contactSelectQuery += " AND c.cid IN (" + couponsSelectQuery + couponsWhereCondAND.toString() + couponsHavingStr +")";
		}

		finalQuery = contactSelectQuery.replace("<USER_ID>", userIdStr).replace("<CONTACT_LEFT_JOIN>", "LEFT");

		if (type.equalsIgnoreCase("ACTIVE_EMAIL_CONTACTS")) {
			finalQuery += activeEmailCond;
			finalQuery = finalQuery.replaceFirst("DISTINCT c.cid", "DISTINCT any(c.cid)");
		}
		else if (type.equalsIgnoreCase("ACTIVE_MOBILE_CONTACTS")) {
			finalQuery += activeMobileCond;
			finalQuery = finalQuery.replaceFirst("DISTINCT c.cid", "DISTINCT any(c.cid)");
		}
		
		//Testing
		//finalQuery = finalQuery.replaceFirst("DISTINCT c.cid", "COUNT(DISTINCT c.cid)");

		logger.info("Final Query :: {}", finalQuery);

		return finalQuery;
	}

	private void processSegmentRuleAND(String andSegmentRule) {

		String[] orSegmentsArr = andSegmentRule.split("<OR>");

		String segmentAttributeType = findSegmentAttributeType(orSegmentsArr[0].split("\\|")[1]);

		switch (segmentAttributeType) {

		case "CONTACT":
			generateContactRule(orSegmentsArr);
			isContactSegmentsSelected = true;
			break;

		case "SALES":
			generateSalesRule(orSegmentsArr);
			isSalesSegmentsSelected = true;
			break;

		case "COUPON":
			generateCouponsRule(orSegmentsArr);
			isCouponSegmentsSelected = true;
			break;

		default:
		}
	}

	private String findSegmentAttributeType(String segmentField) {
		
		logger.info("Segment-Field :: {}", segmentField);

		if (salesWhereFields.contains(segmentField) || salesHavingFieldsMap.containsKey(segmentField))
			return "SALES";

		else if (contactWhereFields.contains(segmentField))
			return "CONTACT";
		
		else if (couponsWhereFields.contains(segmentField))
			return "COUPON";

		else
			return "";
	}

	private void generateContactRule(String[] orSegmentsArr) {

		StringBuilder contactCondOR = new StringBuilder("");

		int loopCount = 0;
		for (String orSegRule : orSegmentsArr) {

			loopCount++;
			String[] segmentTokenArr = orSegRule.split("\\|");

			String orConditionStr = replaceTokensAndGetRule(segmentTokenArr);

			if (contactCondOR.toString().isEmpty())
				contactCondOR.append(orConditionStr);
			else
				contactCondOR.append(" OR " + orConditionStr);

			if (loopCount == orSegmentsArr.length) {
				contactWhereCondAND.append(" AND (" + contactCondOR + ") ");
				
				if (segmentTokenArr[1].contains("c."))
					contactSelectQuery = contactSelectQuery.replace("<CONTACT_LEFT_JOIN>", "LEFT");
				else
					contactSelectQuery = contactSelectQuery.replace("<CONTACT_LEFT_JOIN>", "");
			}
			

		}
	}

	private void generateSalesRule(String[] orSegmentsArr) {

		StringBuilder salesWhereCondOR = new StringBuilder("");
		StringBuilder salesHavingCondOR = new StringBuilder("");

		int loopCount = 0;
		for (String orSegRule : orSegmentsArr) {
			loopCount++;
			String[] segmentTokenArr = orSegRule.split("\\|");

			String conditionStr = replaceTokensAndGetRule(segmentTokenArr);
			
			String token = segmentTokenArr[2].toLowerCase().trim();

			if (token.contains("withinlast_") || token.contains("notwithinlast_")) {
				token = token.substring(0, token.indexOf("_"));
			}

			boolean groupByDocsid = false;
			if (segmentTokenArr[1].equalsIgnoreCase("aggr_tot")
					|| segmentTokenArr[1].equalsIgnoreCase("aggr_avg")
					|| segmentTokenArr[1].equalsIgnoreCase("aggr_count"))
				groupByDocsid = true;

			// Aggregate OR Not
			boolean isAggregateCond = false;
			if (tokensMap.get(token).toLowerCase().contains("count")
					|| tokensMap.get(token).toLowerCase().contains("sum")
					|| tokensMap.get(token).toLowerCase().contains("avg")
					|| tokensMap.get(token).toLowerCase().contains("min")
					|| tokensMap.get(token).toLowerCase().contains("max")
					|| tokensMap.get(token).toLowerCase().contains("groupuniqarray"))
				isAggregateCond = true;
			else
				isAggregateCond = false;

			// WHERE 
			if (salesWhereFields.contains(segmentTokenArr[1]) && !isAggregateCond) {
				if (salesWhereCondOR.toString().isEmpty())
					salesWhereCondOR.append(conditionStr);
				else
					salesWhereCondOR.append(" OR " + conditionStr);
			}

			// HAVING
			else if (salesHavingFieldsMap.containsKey(segmentTokenArr[1]) || isAggregateCond) {
				if (salesHavingCondOR.toString().isEmpty())
					salesHavingCondOR.append(conditionStr);
				else
					salesHavingCondOR.append(" OR " + conditionStr);
			}

			// Appending OR-Conditions to AND-Condition in last loop
			if (loopCount == orSegmentsArr.length) {
				if (!salesWhereCondOR.toString().isEmpty() && !isAggregateCond)
					salesWhereCondAND.append(" AND (" + salesWhereCondOR + ") ");

				if (!salesHavingCondOR.toString().isEmpty() || isAggregateCond) {
					if (salesHavingCondAND.toString().equals("GROUP BY sal.cid HAVING"))
						salesHavingCondAND.append(" (" + salesHavingCondOR + ") ");
					else
						salesHavingCondAND.append(" AND (" + salesHavingCondOR + ") ");
				}

				if (groupByDocsid) {
					String strWithDocsid = salesHavingCondAND.toString().replaceFirst("GROUP BY sal.cid", "GROUP BY sal.doc_sid, sal.cid");
					salesHavingCondAND = new StringBuilder(strWithDocsid);
				}
			}
		}
	}

	private void generateCouponsRule(String[] orSegmentsArr) {

		StringBuilder couponCondOR = new StringBuilder("");

		int loopCount = 0;
		for (String orSegRule : orSegmentsArr) {

			loopCount++;
			String[] segmentTokenArr = orSegRule.split("\\|");

			String orConditionStr = replaceTokensAndGetRule(segmentTokenArr);
			
			if (orConditionStr.contains("coupon_id"))
				orConditionStr = orConditionStr.replace("lowerUTF8(", "").replace(")", "");
			
			String token = segmentTokenArr[2].toLowerCase().trim();

			if (token.contains("withinlast_") || token.contains("notwithinlast_") || token.contains("iswithinnext_")) {
				token = token.substring(0, token.indexOf("_"));
			}

			// Aggregate OR Not
			boolean isAggregateCond = false;
			if (tokensMap.get(token).toLowerCase().contains("count")
					|| tokensMap.get(token).toLowerCase().contains("sum")
					|| tokensMap.get(token).toLowerCase().contains("avg")
					|| tokensMap.get(token).toLowerCase().contains("min")
					|| tokensMap.get(token).toLowerCase().contains("max"))
				isAggregateCond = true;
			else
				isAggregateCond = false;

			if (couponCondOR.toString().isEmpty())
				couponCondOR.append(orConditionStr);
			else
				couponCondOR.append(" OR " + orConditionStr);

			if (loopCount == orSegmentsArr.length) {
				if (!isAggregateCond)
					couponsWhereCondAND.append(" AND (" + couponCondOR + ") ");
				else if (isAggregateCond) {
					if (couponsHavingCondAND.toString().equals("GROUP BY cd.contact_id HAVING"))
						couponsHavingCondAND.append(" (" + couponCondOR + ") ");
					else
						couponsHavingCondAND.append(" AND (" + couponCondOR + ") ");
				}
			}
		}
	}

	private String replaceTokensAndGetRule(String[] segmentTokenArr) {

		String outputStr = "";
		String tempStr = "";

		String token = segmentTokenArr[2].toLowerCase().trim();
		String value = segmentTokenArr[3].trim().replace("'", "\\'");

		if (value.contains(";=;")) {
			value = value.split(";=;")[1];
		}

		if (segmentTokenArr[1].contains("birth_day") || segmentTokenArr[1].contains("anniversary_day"))
			segmentTokenArr[1] = segmentTokenArr[1].replace("Date(", "").replace(")", "");

		if (token.contains("withinlast_") || token.contains("notwithinlast_") || token.contains("iswithinnext_")) {
			token = token.substring(0, token.indexOf("_"));
		}
		if (token.contains("is in") || token.contains("is not in")) {
			value = prepareINValues(value);

			if ((token.contains("not") && token.contains("string") || (token.contains("not") && segmentTokenArr[1].contains("c.zip"))))
				value += ",''";
		}
		if (token.contains("only like")) {
			value = prepareOnlyLikeValues(value);
		}

		logger.info("TOKEN :: {}", token);

		String mapStr = tokensMap.get(token);
		mapStr = mapStr.replace("<DATE_FIELD>", segmentTokenArr[1]);
		
		if (segmentTokenArr[1].toLowerCase().contains(".coupon_id"))
			mapStr = mapStr.replace("<COLUMN_NAME>", segmentTokenArr[1]);
		else
			mapStr = mapStr.replace("<COLUMN_NAME>", "lowerUTF8(" + segmentTokenArr[1] + ")");

		if (segmentTokenArr[1].equalsIgnoreCase("c.gender")) {
			if (value.equalsIgnoreCase("male") || value.equalsIgnoreCase("m"))
				tempStr = "lowerUTF8(c.gender) IN ('male', 'm')";
			if (value.equalsIgnoreCase("female") || value.equalsIgnoreCase("f"))
				tempStr = "lowerUTF8(c.gender) IN ('female', 'f')";
		} else
			tempStr = mapStr.replace("<VALUE>", value.toLowerCase());
		
		if (segmentTokenArr[1].contains("c.zip") && token.contains("number:!=")){
			tempStr += " AND c.zip != ''";
		}

		if (token.contains("between")) {
			String value1 = segmentTokenArr[3].trim();
			String value2 = segmentTokenArr[4].trim();

			tempStr = mapStr.replace("<VALUE1>", value1.toLowerCase()).replace("<VALUE2>", value2.toLowerCase());

			if (token.contains("before_between") || token.contains("after_between")) {
				Calendar currCal = Calendar.getInstance();
				String currMonthDate = (currCal.get(Calendar.MONTH) + 1) + "-" + (currCal.get(Calendar.DATE));

				tempStr = tempStr.replace("<ZEROYEAR_DATE>", "1900-" + currMonthDate);
				tempStr = tempStr.replace("<FIRSTYEAR_DATE>", "1901-" + currMonthDate);
			}
		}

		if (salesHavingFieldsMap.containsKey(segmentTokenArr[1]))
			outputStr = tempStr.replace("<COLUMN_COND>", salesHavingFieldsMap.get(segmentTokenArr[1]));
		// outputStr = " (" + salesHavingFieldsMap.get(segmentTokenArr[1]) + tempStr + ") ";
		else
			outputStr = tempStr.replace("<COLUMN_COND>", segmentTokenArr[1]);
//			outputStr = tempStr.replace("<COLUMN_COND>", "lowerUTF8(" + segmentTokenArr[1] + ")");

		logger.info("OUTPUT-STRING :: {}", outputStr);
		return outputStr;
	}

	public static String prepareINValues(String inValues) {

		StringBuilder valueStr = new StringBuilder();

		for (String value : inValues.split(DELIMETER_COMMA)) {

			if (valueStr.toString().length() > 0)
				valueStr.append(DELIMETER_COMMA);
			valueStr.append("\'" + value.trim() + "\'");
		}

		return valueStr.toString();
	}

	public static String prepareOnlyLikeValues(String onlyLikeValues) {

		StringBuilder valueStr = new StringBuilder();

		for (String value : onlyLikeValues.split(DELIMETER_COMMA)) {

			if (valueStr.toString().length() > 0)
				valueStr.append(" OR ");
			valueStr.append("x ILIKE "+ "\'%" + value.trim() + "%\'");
		}

		return valueStr.toString();
	}
	
}
