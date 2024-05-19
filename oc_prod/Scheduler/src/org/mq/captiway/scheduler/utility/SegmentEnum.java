package org.mq.captiway.scheduler.utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public enum SegmentEnum {
	 
	//*************************PROFILE ATTRIBUTES ***************************************
	
	PROFILE_DATE_ADDED(0, 1, "Date Added", "Date Added", true, "Created Date", "Date", null, null, null, null, "Date(c.created_date)"),
		PROFILE_DATE_ADDED_IS(101, 0, "is", "is", "date", PROFILE_DATE_ADDED, null, null, null, ""),
			PROFILE_DATE_ADDED_TO_DAY(102, 0, "Today", "Today", "date", PROFILE_DATE_ADDED_IS, null, null, "isToday", ""),
			PROFILE_DATE_ADDED_EQUAL_TO(103, 0,"Equal to","Equal to", "date", PROFILE_DATE_ADDED_IS, "Date", null, "is", ""),
			PROFILE_DATE_ADDED_BETWEEN(104,0, "Between", "Between", "date", PROFILE_DATE_ADDED_IS, "Date", "Date", "between", ""),
			PROFILE_DATE_ADDED_ON_OR_AFTER(105,0, "On or after", "On or after", "date", PROFILE_DATE_ADDED_IS, "Date", null, "onOrAfter", ""),
			PROFILE_DATE_ADDED_ON_OR_BEFORE(106,0, "On or before", "On or before", "date", PROFILE_DATE_ADDED_IS, "Date", null, "onOrBefore", ""),
			PROFILE_DATE_ADDED_AFTER(107,0, "After", "After", "date", PROFILE_DATE_ADDED_IS, "Date", null, "after", ""),
			PROFILE_DATE_ADDED_BEFORE(108,0, "Before", "Before", "date", PROFILE_DATE_ADDED_IS, "Date", null, "before", ""),
			
			PROFILE_DATE_ADDED_IS_IGNORE_YEAR(101, 0, "is(in range)", "is(in range)", "date", PROFILE_DATE_ADDED, null, null, null, ""),
			PROFILE_DATE_ADDED_BEFORE_BETWEEN_DAYS(109,0, "Last Range Of Days", "Last Range Of Days", "date", PROFILE_DATE_ADDED_IS_IGNORE_YEAR, "number", "number", "range_between", ""),
			
			//PROFILE_DATE_ADDED_BEFORE_BETWEEN_DAYS(109,0, "Last Range Of Days", "date", PROFILE_DATE_ADDED_IS_IGNORE_YEAR, "number", "number", "before_between", ""),
			//PROFILE_DATE_ADDED_AFTER_BETWEEN_DAYS(110,0, "Next Range Of Days", "date", PROFILE_DATE_ADDED_IS_IGNORE_YEAR, "number", "number", "after_between", ""),
			
			
		PROFILE_DATE_ADDED_WITH_IN_LAST(201,0, "within last", "within last", "date", PROFILE_DATE_ADDED, null,null, null, ""),
			PROFILE_DATE_ADDED_DAYS(202,0, "days", "days", "date", PROFILE_DATE_ADDED_WITH_IN_LAST, "31", "1", "withinlast_days", ""),
			PROFILE_DATE_ADDED_WEEKS(203,0, "weeks", "weeks", "date", PROFILE_DATE_ADDED_WITH_IN_LAST, "4", "7", "withinlast_weeks", ""),
			PROFILE_DATE_ADDED_MONTHS(204,0, "months", "months", "date", PROFILE_DATE_ADDED_WITH_IN_LAST, "12", "30", "withinlast_months", ""),
			
			
			
/*	PROFILE_OPTED_IN_DATE(1, 1, "Opted In Date", false, null, "Date", null, null, null, null, "Date(c.created_date)"),
		PROFILE_OPTED_IN_DATE_IS(101,0, "is", "date", PROFILE_OPTED_IN_DATE, null, null, null, ""),
			PROFILE_OPTED_IN_DATE_TO_DAY(102,0, "Today", "date", PROFILE_OPTED_IN_DATE_IS, null, null, "isToday", ""),
			PROFILE_OPTED_IN_DATE_EQUAL_TO(103, 0,"Equal to", "date", PROFILE_OPTED_IN_DATE_IS, "Date", null, "is", ""),
			PROFILE_OPTED_IN_DATE_BETWEEN(104,0, "Between", "date", PROFILE_OPTED_IN_DATE_IS, "Date", "Date", "between", ""),
			PROFILE_OPTED_IN_DATE_ON_OR_AFTER(105,0, "On or after", "date", PROFILE_OPTED_IN_DATE_IS, "Date", null, "onOrAfter", ""),
			PROFILE_OPTED_IN_DATE_ON_OR_BEFORE(106,0, "On or before", "date", PROFILE_OPTED_IN_DATE_IS, "Date", null, "onOrBefore", ""),
			PROFILE_OPTED_IN_DATE_AFTER(107,0, "After", "date", PROFILE_OPTED_IN_DATE_IS, "Date", null, "after", ""),
			PROFILE_OPTED_IN_DATE_BEFORE(108,0, "Before", "date", PROFILE_OPTED_IN_DATE_IS, "Date", null, "before", ""),
			
			PROFILE_OPTED_IN_DATE_IS_IGNORE_YEAR(101,0, "is(in range)", "date", PROFILE_OPTED_IN_DATE, null, null, null, ""),
			PROFILE_OPTED_IN_DATE_BEFORE_BETWEEN_DAYS(109,0, "Last Range Of Days", "date", PROFILE_OPTED_IN_DATE_IS_IGNORE_YEAR, "number", "number", "range_between", ""),
			//PROFILE_OPTED_IN_DATE_BEFORE_BETWEEN_DAYS(109,0, "Last Range Of Days", "date", PROFILE_OPTED_IN_DATE_IS_IGNORE_YEAR, "number", "number", "before_between", ""),
			//PROFILE_OPTED_IN_DATE_AFTER_BETWEEN_DAYS(110,0, "Next Range Of Days", "date", PROFILE_OPTED_IN_DATE_IS_IGNORE_YEAR, "number", "number", "after_between", ""),
			
	
		PROFILE_OPTED_IN_DATE_WITH_IN_LAST(201,0, "within last", "date", PROFILE_OPTED_IN_DATE, null,null, null, ""),
			PROFILE_OPTED_IN_DATE_DAYS(202,0, "days", "date", PROFILE_OPTED_IN_DATE_WITH_IN_LAST, "31", "1", "withinlast", ""),
			PROFILE_OPTED_IN_DATE_WEEKS(203,0, "weeks", "date", PROFILE_OPTED_IN_DATE_WITH_IN_LAST, "4", "7", "withinlast", ""),
			PROFILE_OPTED_IN_DATE_MONTHS(204,0, "months", "date", PROFILE_OPTED_IN_DATE_WITH_IN_LAST, "12", "30", "withinlast", ""),*/

	
	PROFILE_EMAIL_ID(2, 1, "Email Id","Email Id", true, "Email", "String", null, null, null, null, "c.email_id"),
		PROFILE_EMAIL_ID_CONTAINS(101,0, "contains", "contains", "String", PROFILE_EMAIL_ID, "text", null, "contains", ""),
		PROFILE_EMAIL_ID_DOES_NOT_CONTAINS(102,0, "doesn't contain", "doesn't contain", "String", PROFILE_EMAIL_ID, "text", null, "does not contain", ""),
		PROFILE_EMAIL_ID_NO_VALUE(102,0, "Is empty", "Is empty", "String", PROFILE_EMAIL_ID, null, null, "none", "", " IS NULL"),
	
	
	
	//need to enable after we get mobile carrier info
	/*PROFILE_MOBILE(3,1, "Mobile", "Carrier", null, null, null, null, "c.mobile"),
		PROFILE_MOBILE_CARRIER_IS(101,0, "Carrier is", "carrier", PROFILE_MOBILE, "drop", null, "is", ""),
		PROFILE_MOBILE_CARRIER_IS_NOT(102,0, "Carrier is not", "Carrier", PROFILE_MOBILE, "drop", null, "is not", ""),
			*/
			
	
	PROFILE_CITY(4, 1, "City", "City", true, "City", "String", null, null, null, null, "c.city"),
		PROFILE_CITY_IS(101,0, "is", "is", "string", PROFILE_CITY, null, null, null, ""),
			PROFILE_CITY_IS_EQUAL_TO(102,0, "Equal to", "Equal to", "string", PROFILE_CITY_IS, "text", null, "is", ""),
			PROFILE_CITY_IS_ONE_OF(103,0, "One of", "One of", "string", PROFILE_CITY_IS, "text", null, "is in", ""),
			PROFILE_CITY_NO_VALUE(103,0, "Empty", "Empty", "string", PROFILE_CITY_IS, null, null, "none", "", " IS NULL"),
	
		PROFILE_CITY_IS_NOT(102,0, "is not", "is not", "string", PROFILE_CITY, null, null, null, ""),
			PROFILE_MOBILE_CARRIER_IS_NOT_EQUAL_TO(102,0, "Equal to", "Equal to", "string", PROFILE_CITY_IS_NOT, "text", null, "is not", ""),
			PROFILE_MOBILE_CARRIER_IS_NOT_ONE_OF(103,0, "One of", "One of", "string", PROFILE_CITY_IS_NOT, "text", null, "is not in", ""),

	PROFILE_STATE(5, 1, "State", "State", true, "State", "String", null, null, null, null, "c.state"),
		PROFILE_STATE_IS(101,0, "is", "is", "string", PROFILE_STATE, null, null, null, ""),
			PROFILE_STATE_IS_EQUAL_TO(102,0, "Equal to", "Equal to", "string", PROFILE_STATE_IS, "text", null, "is", ""),
			PROFILE_STATE_IS_ONE_OF(103,0, "One of", "One of", "string", PROFILE_STATE_IS, "text", null, "is in", ""),
			PROFILE_STATE_NO_VALUE(103,0, "Empty", "Empty", "string", PROFILE_STATE_IS, null, null, "none", ""," IS NULL"),
		
		PROFILE_STATE_IS_NOT(102,0, "is not", "is not", "string", PROFILE_STATE, null, null, null, ""),
			PROFILE_STATE_IS_NOT_EQUAL_TO(102,0, "Equal to", "Equal to", "string", PROFILE_STATE_IS_NOT, "text", null, "is not", ""),
			PROFILE_STATE_IS_NOT_ONE_OF(103,0, "One of", "One of", "string", PROFILE_STATE_IS_NOT, "text", null, "is not in", ""),

	PROFILE_COUNTRY(6, 1, "Country", "Country", true, "Country", "String", null, null, null, null, "c.country"),
		PROFILE_COUNTRY_IS(101,0, "is", "is", "string", PROFILE_COUNTRY, null, null, null, ""),
			PROFILE_COUNTRY_IS_EQUAL_TO(201,0, "Equal to", "Equal to", "string", PROFILE_COUNTRY_IS, "text", null, "is", ""),
			PROFILE_COUNTRY_IS_ONE_OF(202,0, "One of", "One of", "string", PROFILE_COUNTRY_IS, "text", null, "is in", ""),
			PROFILE_COUNTRY_NO_VALUE(202,0, "Empty", "Empty", "string", PROFILE_COUNTRY_IS, null, null, "none", "", " IS NULL"),
	
		PROFILE_COUNTRY_IS_NOT(102,0, "is not", "is not", "string", PROFILE_COUNTRY, null, null, null, ""),
			PROFILE_COUNTRY_IS_NOT_EQUAL_TO(201,0, "Equal to", "Equal to", "string", PROFILE_COUNTRY_IS_NOT, "text", null, "is not", ""),
			PROFILE_COUNTRY_IS_NOT_ONE_OF(202,0, "One of", "One of", "string", PROFILE_COUNTRY_IS_NOT, "text", null, "is not in", ""),

	PROFILE_ZIP(7, 1, "Zip", "Zip", true, "ZIP", "Number", null, null, null, null, "c.zip"),
		PROFILE_ZIP_IS(101,0, "is", "number", "number", PROFILE_ZIP, null, null, null, ""),
			PROFILE_ZIP_EQUAL_TO(201,0, "Equal to", "Equal to", "number", PROFILE_ZIP_IS, "text", null, "=", ""),
			PROFILE_ZIP_IS_ONE_OF(201,0, "One of", "One of", "number", PROFILE_ZIP_IS, "text", null, "is in", ""),
			PROFILE_ZIP_NO_VALUE(201,0, "Empty", "Empty", "number", PROFILE_ZIP_IS, null, null, "none", "", " IS NULL"),

		PROFILE_ZIP_IS_NOT(102,0, "is not", "is not", "number", PROFILE_ZIP, null, null, null, ""),
			PROFILE_ZIP_IS_NOT_EQUAL_TO(201,0, "Equal to", "Equal to", "number", PROFILE_ZIP_IS_NOT, "text", null, "!=", ""),
			PROFILE_ZIP_IS_NOT_ONE_OF(201,0, "One of", "One of", "number", PROFILE_ZIP_IS_NOT, "text", null, "is not in", ""),

	
	
	//need to set column type afterwards
	/*PROFILE_GENDER(8, 1, "Gender", "Gender", true, "Gender","String", null, null, null, null, "c.gender"),
		PROFILE_GENDER_IS(101,0,"is","is","String",PROFILE_GENDER,null,null,null,""),
			PROFILE_GENDER_IS_MALE(201,0,"Male","Male", "String", PROFILE_GENDER_IS, null,null,"is",""),
			PROFILE_GENDER_IS_FEMALE(202,0, "Female", "Female", "string", PROFILE_GENDER_IS, null, null, "equal", "" ),
			PROFILE_GENDER_IS_NO_VALUE(202,0, "Empty", "Empty", "string", PROFILE_GENDER_IS, null, null, "none", ""," IS NULL" ),*/
			
			PROFILE_GENDER(8, 1, "Gender", "Gender", true, "Gender","String", null, null, null, null, "c.gender"),
			PROFILE_GENDER_IS(101,0,"is","is","String",PROFILE_GENDER,null,null,null,""),
				PROFILE_GENDER_IS_EQUAL_TO(102,0, "Equal to", "Equal to", "string", PROFILE_GENDER_IS, "text", null, "is", ""),	
		
		
	PROFILE_OPT_IN_MEDIUM(9, 1, "Contact Source", "Contact Source", false, null, "string", null, null, null, null, "c.optin_medium"),
		PROFILE_OPT_IN_MEDIUM_IS(102,0,"is","is","String",PROFILE_OPT_IN_MEDIUM,null,null,null,""),
			PROFILE_OPT_IN_MEDIUM_IS_POS(201,0,"POS","POS", "String", PROFILE_OPT_IN_MEDIUM_IS, null,null,"is","", "POS"),
			PROFILE_OPT_IN_MEDIUM_IS_ONLINE(202,0, "Manual Upload", "Manual Upload", "string", PROFILE_OPT_IN_MEDIUM_IS, null, null,"equal", "", "AddedManually" ),
			PROFILE_OPT_IN_MEDIUM_IS_OFFLINE(203,0, "Web Form", "Web Form", "string", PROFILE_OPT_IN_MEDIUM_IS, null,  null, "equals", "", "WebForm" ),
			PROFILE_OPT_IN_MEDIUM_IS_MOBILE_OPTIN(204,0, "Mobile Opt-In", "Mobile Opt-In", "string", PROFILE_OPT_IN_MEDIUM_IS, null,  null, "equal to", "", "Mobile Opt-In" ),
			PROFILE_OPT_IN_MEDIUM_IS_ECOMMERCE(205,0, "eCommerce", "eCommerce", "string", PROFILE_OPT_IN_MEDIUM_IS, null,  null, "is equal", "", "eComm" ),
	
	PROFILE_OPTED_IN_FOR_SMS(10, 1, "Opt-in for SMS", "Opt-in for SMS", false, null, "boolean", null, null, null, null, "c.mobile_opt_in"),
		PROFILE_OPTED_IN_FOR_SMS_IS(102,0,"is","is","boolean",PROFILE_OPTED_IN_FOR_SMS,null,null,null,""),
		PROFILE_OPTED_IN_FOR_SMS_IS_true(201,0,"Yes","Yes", "boolean", PROFILE_OPTED_IN_FOR_SMS_IS, null,null,"is","", "true"),
		PROFILE_OPTED_IN_FOR_SMS_IS_false(202,0, "No", "No", "boolean", PROFILE_OPTED_IN_FOR_SMS_IS, null, null,"equal", "", "false" ),
				
			
	PROFILE_CONTACT_TYPE(11, 1, "Contact Type", "Contact Type",  false, null, "string", null, null, null, null, null),//need to decide col name
		PROFILE_CONTACT_TYPE_IS(103,0,"is","is","String",PROFILE_CONTACT_TYPE,null,null,null,null),
			PROFILE_CONTACT_TYPE_IS_EMAIL(201,0,"Email","Email", "String", PROFILE_CONTACT_TYPE_IS, null,null,"not null","c.email_id", " IS NOT NULL AND c.email_id != '' AND c.email_status='Active'  "),
			PROFILE_CONTACT_TYPE_IS_SMS(202,0, "Mobile", "Mobile", "string", PROFILE_CONTACT_TYPE_IS, null, null, "is not null", "c.mobile_phone", " IS NOT NULL AND c.mobile_phone != '' " ),
			PROFILE_CONTACT_TYPE_IS_ADDRESS(203,0, "Address", "Address", "string", PROFILE_CONTACT_TYPE_IS, null, null, "not equal to null",
					"c.address_one", " IS NOT NULL AND c.address_one !='' AND c.zip IS NOT NULL AND c.zip != '' " ),
			
	PROFILE_HOME_STORE(12, 1, "Home Store", "Home Store", true, "Home Store", "String", null, null, null, null, "c.home_store"),
	PROFILE_HOME_STORE_IS(104,0,"is","is","String",PROFILE_HOME_STORE,null,null,null,""),
			PROFILE_HOME_STORE_EQUALS(105, 0, "Equal to", "Equal to", "String", PROFILE_HOME_STORE_IS, "text", null, "is", ""),
			PROFILE_HOME_STORE_DOES_NOT_EQUAL(106, 0, "Doesn't equal", "Doesn't equal", "String", PROFILE_HOME_STORE_IS, "text", null, "is not", ""),
			PROFILE_HOME_STORE_ONE_OF(107, 0, "One of", "One of", "String", PROFILE_HOME_STORE_IS, "text", null, "is in", ""),
			PROFILE_HOME_NO_VALUE(107, 0, "Empty", "Empty", "String", PROFILE_HOME_STORE_IS, null, null, "none", "", " IS NULL"),
	
			PROFILE_HOME_STORE_SUBSIDIARY(15, 1, "Home Store Subsidiary", "Home Store Subsidiary", true, "Home Store Subsidiary", "String", null, null, null, null, "c.subsidiary_number"),
			PROFILE_HOME_STORE_SUBSIDIARY_IS(104,0,"is","is","String",PROFILE_HOME_STORE_SUBSIDIARY,null,null,null,""),
							PROFILE_HOME_STORE_SUBSIDIARY_EQUALS(105, 0, "Equal to", "Equal to", "String", PROFILE_HOME_STORE_SUBSIDIARY_IS, "text", null, "is", ""),
							PROFILE_HOME_STORE_SUBSIDIARY_DOES_NOT_EQUAL(106, 0, "Doesn't equal", "Doesn't equal", "String", PROFILE_HOME_STORE_SUBSIDIARY_IS, "text", null, "is not", ""),
							PROFILE_HOME_STORE_SUBSIDIARY_ONE_OF(107, 0, "One of", "One of", "String", PROFILE_HOME_STORE_SUBSIDIARY_IS, "text", null, "is in", ""),
							PROFILE_HOME_SUBSIDIARY_NO_VALUE(107, 0, "Empty", "Empty", "String", PROFILE_HOME_STORE_SUBSIDIARY_IS, null, null, "none", "", " IS NULL"),
							
		PROFILE_BIRTHDAY(13, 1, "Birthday", "Birthday", true, "BirthDay", "Date", null, null, null, null, "Date(c.birth_day)"),
			PROFILE_BIRTHDAY_IS(101, 0, "is", "is", "date", PROFILE_BIRTHDAY, null, null, null, ""),
				//PROFILE_BIRTHDAY_TO_DAY(102, 0, "Today", "date", PROFILE_BIRTHDAY_IS, null, null, "isToday", ""),
				PROFILE_BIRTHDAY_EQUAL_TO(103, 0,"Equal to","Equal to", "date", PROFILE_BIRTHDAY_IS, "Date", null, "is", ""),
				PROFILE_BIRTHDAY_BETWEEN(104,0, "Between", "Between", "date", PROFILE_BIRTHDAY_IS, "Date", "Date", "between", ""),
				PROFILE_BIRTHDAY_ON_OR_AFTER(105,0, "On or after", "On or after", "date", PROFILE_BIRTHDAY_IS, "Date", null, "onOrAfter", ""),
				PROFILE_BIRTHDAY_ON_OR_BEFORE(106,0, "On or before", "On or before", "date", PROFILE_BIRTHDAY_IS, "Date", null, "onOrBefore", ""),
				PROFILE_BIRTHDAY_AFTER(107,0, "After", "After", "date", PROFILE_BIRTHDAY_IS, "Date", null, "after", ""),
				PROFILE_BIRTHDAY_BEFORE(108,0, "Before", "Before", "date", PROFILE_BIRTHDAY_IS, "Date", null, "before", ""),
				PROFILE_BIRTHDAY_NO_VALUE(108,0, "Empty", "Empty", "date", PROFILE_BIRTHDAY_IS, null, null, "none", "", " IS NULL"),
				
			PROFILE_BIRTHDAY_IS_IGNORE_YEAR(101, 0, "is(year ignored)", "is(year ignored)", "date", PROFILE_BIRTHDAY, null, null, null, ""),
				PROFILE_BIRTHDAY_BEFORE_BETWEEN_DAYS(109,0, "Last Range Of Days", "Last Range Of Days", "date", PROFILE_BIRTHDAY_IS_IGNORE_YEAR, "number", "number", "before_between", ""),
				PROFILE_BIRTHDAY_AFTER_BETWEEN_DAYS(110,0, "Next Range Of Days", "Next Range Of Days", "date", PROFILE_BIRTHDAY_IS_IGNORE_YEAR, "number", "number", "after_between", ""),
				PROFILE_BIRTHDAY_IS_IGNORE_YEAR_EQUAL_TO(103, 0,"Equal to","Equal to", "date", PROFILE_BIRTHDAY_IS_IGNORE_YEAR, "Date", null, "ignoreYear_is", ""),
				PROFILE_BIRTHDAY_IS_IGNORE_YEAR_BETWEEN(104,0, "Between", "Between", "date", PROFILE_BIRTHDAY_IS_IGNORE_YEAR, "Date", "Date", "ignoreYear_between", ""),
				PROFILE_BIRTHDAY_IS_IGNORE_YEAR_ON_OR_AFTER(105,0, "On or after", "On or after", "date", PROFILE_BIRTHDAY_IS_IGNORE_YEAR, "Date", null, "ignoreYear_onOrAfter", ""),
				PROFILE_BIRTHDAY_IS_IGNORE_YEAR_ON_OR_BEFORE(106,0, "On or before", "On or before", "date", PROFILE_BIRTHDAY_IS_IGNORE_YEAR, "Date", null, "ignoreYear_onOrBefore", ""),
				PROFILE_BIRTHDAY_IS_IGNORE_YEAR_AFTER(107,0, "After", "After", "date", PROFILE_BIRTHDAY_IS_IGNORE_YEAR, "Date", null, "ignoreYear_after", ""),
				PROFILE_BIRTHDAY_IS_IGNORE_YEAR_BEFORE(108,0, "Before", "Before", "date", PROFILE_BIRTHDAY_IS_IGNORE_YEAR, "Date", null, "ignoreYear_before", ""),
				//PROFILE_BIRTHDAY_IS_IGNORE_YEAR_NO_VALUE(108,0, "Empty", "Empty", "date", PROFILE_BIRTHDAY_IS_IGNORE_YEAR, null, null, "ignoreYear_none", "", " IS NULL"),
				
			PROFILE_BIRTHDAY_WITH_IN_LAST(201,0, "within last", "within last", "date", PROFILE_BIRTHDAY, null,null, null, ""),
				PROFILE_BIRTHDAY_DAYS(202,0, "days", "days", "date", PROFILE_BIRTHDAY_WITH_IN_LAST, "31", "1", "withinlast_days", ""),
				PROFILE_BIRTHDAY_WEEKS(203,0, "weeks", "weeks", "date", PROFILE_BIRTHDAY_WITH_IN_LAST, "4", "7", "withinlast_weeks", ""),
				PROFILE_BIRTHDAY_MONTHS(204,0, "months", "months", "date", PROFILE_BIRTHDAY_WITH_IN_LAST, "12", "30", "withinlast_months", ""),
			
			
				
				
				
				
			PROFILE_ANNIVERSARY(14, 1, "Anniversary", "Anniversary", true, "Anniversary", "Date", null, null, null, null, "Date(c.anniversary_day)"),
				PROFILE_ANNIVERSARY_IS(101, 0, "is", "is", "date", PROFILE_ANNIVERSARY, null, null, null, ""),
					//PROFILE_ANNIVERSARY_TO_DAY(102, 0, "Today", "date", PROFILE_ANNIVERSARY_IS, null, null, "isToday", ""),
					PROFILE_ANNIVERSARY_EQUAL_TO(103, 0,"Equal to","Equal to", "date", PROFILE_ANNIVERSARY_IS, "Date", null, "is", ""),
					PROFILE_ANNIVERSARY_BETWEEN(104,0, "Between", "Between", "date", PROFILE_ANNIVERSARY_IS, "Date", "Date", "between", ""),
					PROFILE_ANNIVERSARY_ON_OR_AFTER(105,0, "On or after", "On or after", "date", PROFILE_ANNIVERSARY_IS, "Date", null, "onOrAfter", ""),
					PROFILE_ANNIVERSARY_ON_OR_BEFORE(106,0, "On or before", "On or before", "date", PROFILE_ANNIVERSARY_IS, "Date", null, "onOrBefore", ""),
					PROFILE_ANNIVERSARY_AFTER(107,0, "After", "After", "date", PROFILE_ANNIVERSARY_IS, "Date", null, "after", ""),
					PROFILE_ANNIVERSARY_BEFORE(108,0, "Before", "Before", "date", PROFILE_ANNIVERSARY_IS, "Date", null, "before", ""),
					PROFILE_ANNIVERSARY_NO_VALUE(108,0, "Empty", "Empty", "date", PROFILE_ANNIVERSARY_IS, null, null, "none", "", " IS NULL"),
					
					PROFILE_ANNIVERSARY_IS_IGNORE_YEAR(101, 0, "is(year ignored)", "is(year ignored)", "date", PROFILE_ANNIVERSARY, null, null, null, ""),
					PROFILE_ANNIVERSARY_BEFORE_BETWEEN_DAYS(109,0, "Last Range Of Days", "Last Range Of Days", "date", PROFILE_ANNIVERSARY_IS_IGNORE_YEAR, "number", "number", "before_between", ""),
					PROFILE_ANNIVERSARY_AFTER_BETWEEN_DAYS(110,0, "Next Range Of Days", "Next Range Of Days", "date", PROFILE_ANNIVERSARY_IS_IGNORE_YEAR, "number", "number", "after_between", ""),
					
				PROFILE_ANNIVERSARY_WITH_IN_LAST(201,0, "within last", "within last", "date", PROFILE_ANNIVERSARY, null,null, null, ""),
					PROFILE_ANNIVERSARY_DAYS(202,0, "days", "days", "date", PROFILE_ANNIVERSARY_WITH_IN_LAST, "31", "1", "withinlast_days", ""),
					PROFILE_ANNIVERSARY_WEEKS(203,0, "weeks", "weeks", "date", PROFILE_ANNIVERSARY_WITH_IN_LAST, "4", "7", "withinlast_weeks", ""),
					PROFILE_ANNIVERSARY_MONTHS(204,0, "months", "months", "date", PROFILE_ANNIVERSARY_WITH_IN_LAST, "12", "30", "withinlast_months", ""),
				
				
				
	//***********************************************************************************************************************************
	//************************PURCHASE ATTRIBUTES*****************************************************************************************
	
					PURCHASE_DATE (0, 2, "Purchase Date", "Purchase Date", true, "Sale Date", "Date", null, null, null, null,"Date(sal.sales_date)"),
					PURCHASE_IS(101, 0, "is", "is", "date", PURCHASE_DATE, null, null, null, ""),
						PURCHASE_TO_DAY(102,  0, "Today", "Today", "date", PURCHASE_IS, null, null, "isToday", ""),
						PURCHASE_EQUAL_TO(103,  0, "Equal to", "Equal to", "date", PURCHASE_IS, "Date", null, "is", ""),
						PURCHASE_BETWEEN(104,  0, "Between", "Between", "date", PURCHASE_IS, "Date", "Date", "between", ""),
						PURCHASE_ON_OR_AFTER(105, 0,  "On or after",  "On or after", "date", PURCHASE_IS, "Date", null, "onOrAfter", ""),
						PURCHASE_ON_OR_BEFORE(106, 0, "On or before", "On or before", "date", PURCHASE_IS, "Date", null, "onOrBefore", ""),
						PURCHASE_AFTER(107, 0, "After", "After", "date", PURCHASE_IS, "Date", null, "after", ""),
						PURCHASE_BEFORE(108, 0, "Before", "Before", "date", PURCHASE_IS, "Date", null, "before", ""),
						
						PURCHASE_IS_IGNORE_YEAR(101, 0, "is(in range)", "is(in range)", "date", PURCHASE_DATE, null, null, null, ""),
						PURCHASE_BEFORE_BETWEEN_DAYS(109,0, "Last Range Of Days", "Last Range Of Days", "date", PURCHASE_IS_IGNORE_YEAR, "number", "number", "range_between", ""),
						//PURCHASE_AFTER_BETWEEN_DAYS(110,0, "Next Range Of Days", "date", PURCHASE_IS_IGNORE_YEAR, "number", "number", "after_between", ""),
						
						
					PURCHASE_WITH_IN_LAST(201, 0, "within last", "within last", "date", PURCHASE_DATE, null,null, null, ""),
						PURCHASE_DAYS(202, 0, "days", "days", "date", PURCHASE_WITH_IN_LAST, "31", "1", "withinlast_days", ""),
						PURCHASE_WEEKS(203, 0, "weeks", "weeks", "date", PURCHASE_WITH_IN_LAST, "4", "7", "withinlast_weeks", ""),
						PURCHASE_MONTHS(204, 0, "months", "months", "date", PURCHASE_WITH_IN_LAST, "12", "30", "withinlast_months", ""),
							
					
						
					PURCHASE_NOT_WITH_IN_LAST(301, 0, "not within last", "not within last","date", PURCHASE_DATE,null,null, null,""),
					
						PURCHASE_NOT_DAYS(302, 0, "days", "days", "date", PURCHASE_NOT_WITH_IN_LAST, "31", "1", "notwithinlast_days", ""),
						PURCHASE_NOT_WEEKS(303, 0, "weeks", "weeks", "date", PURCHASE_NOT_WITH_IN_LAST, "4", "7", "notwithinlast_weeks", ""),
						PURCHASE_NOT_MONTHS(304, 0, "months", "months", "date", PURCHASE_NOT_WITH_IN_LAST, "12", "30", "notwithinlast_months", ""),
					
					PURCHASE_IS_NOT(401, 0, "is not", "is not", "date", PURCHASE_DATE, null,null, null, ""),
						PURCHASE_NOT_AFTER(402, 0, "after", "after", "date", PURCHASE_IS_NOT, "date", null, "notafter", ""),
			PURCHASE_NOT_BRFORE(403, 0, "before", "before", "date", PURCHASE_IS_NOT, "date", null, "notbefore", ""),
			
						
			//APP-4201
			PURCHASE_LAST_DATE_EQUAL_TO(501,0, "days since last purchase", "days since last purchase", "number", PURCHASE_DATE, "number", null, "date_diff_equal_to", ""),
			
						TOTAL_PURCHASE_VALUE(10, 2, "Purchase Amount", "Total Purchase Amount", "number", null, null, null, null, null),
						
						
						TOTAL_PURCHASE_VALUE_TOTAL(10, 0, "Total", "Total", "number", TOTAL_PURCHASE_VALUE, null, null, null, "aggr_tot", "", "sum(totSal.totAmt) "),
						OTAL_PURCHASE_VALUE_TOTAL_IS_MORE_THAN(101, 0, "Is more than", "Is more than", "number", TOTAL_PURCHASE_VALUE_TOTAL,"double", null, ">",""),
						TOTAL_PURCHASE_VALUE_TOTAL_IS_LESS_THEN(102, 0, "Is less than", "Is less than", "number", TOTAL_PURCHASE_VALUE_TOTAL,"double", null, "<",""),
						TOTAL_PURCHASE_VALUE_TOTAL_IS_IN_THE_RANGE(101, 0, "Is in the range of", "Is in the range of", "number", TOTAL_PURCHASE_VALUE_TOTAL,"double", "double", "between",""),
						

							/*PURCHASE_TOTAL_PURCHASE_VALUE_AVG(7, 0, "Avg", "number", PURCHASE_TOTAL_PURCHASE_VALUE, null, null, null, "aggr.avg_purchase_amt"),
								PURCHASE_TOTAL_PURCHASE_VALUE_AVG_IS_MORE_THAN(101, 0, "Is more than", "number", PURCHASE_TOTAL_PURCHASE_VALUE_AVG,"double", null, ">",""),
								PURCHASE_TOTAL_PURCHASE_VALUE_AVG_IS_LESS_THEN(102, 0, "Is less than", "number", PURCHASE_TOTAL_PURCHASE_VALUE_AVG,"double", null, "<",""),
								PURCHASE_TOTAL_PURCHASE_VALUE_AVG_IS_IN_THE_RANGE(101, 0, "Is in the range of", "number", PURCHASE_TOTAL_PURCHASE_VALUE_AVG,"double", "double", "between",""),
							*/	
							TOTAL_PURCHASE_VALUE_AVG(10, 0, "Average of all", "Average of all", "number", TOTAL_PURCHASE_VALUE, null, null, null, "aggr_avg", "", "(sum(totSal.totAmt)/count(totSal.cid)) "),
								TOTAL_PURCHASE_VALUE_AVG_IS_MORE_THAN(101, 0, "Is more than", "Is more than", "number", TOTAL_PURCHASE_VALUE_AVG,"double", null, ">",""),
								TOTAL_PURCHASE_VALUE_AVG_IS_LESS_THEN(102, 0, "Is less than", "Is less than", "number", TOTAL_PURCHASE_VALUE_AVG,"double", null, "<",""),
								TOTAL_PURCHASE_VALUE_AVG_IS_IN_THE_RANGE(101, 0, "Is in the range of", "Is in the range of", "number", TOTAL_PURCHASE_VALUE_AVG,"double", "double", "between",""),
								
								
								
								
							TOTAL_PURCHASE_VALUE_MAX(10, 0, "Maximum of all", "Maximum of all", "number", TOTAL_PURCHASE_VALUE, null, null, null,"aggr_max", "",  "max(totSal.totAmt) "),
								TOTAL_PURCHASE_VALUE_MAX_IS_MORE_THAN(101, 0, "Is more than", "Is more than", "number", TOTAL_PURCHASE_VALUE_MAX,"double", null, ">",""),
								TOTAL_PURCHASE_VALUE_MAX_IS_LESS_THEN(102, 0, "Is less than", "Is less than", "number", TOTAL_PURCHASE_VALUE_MAX,"double", null, "<",""),
								TOTAL_PURCHASE_VALUE_MAX_IS_IN_THE_RANGE(101, 0, "Is in the range of", "Is in the range of", "number", TOTAL_PURCHASE_VALUE_MAX,"double", "double", "between",""),

						//newly added for RSBros as TOTAL_PURCHASE_VALUE taking time
						TOTAL_PURCHASE_VALUE_LIFETIME(10, 2, "Lifetime Purchase Amount ", "Lifetime Purchase Amount", "number", null, null, null, null, null),
							TOTAL_PURCHASE_VALUE_LIFETIME_TOTAL(10, 0, "Total spend till date", "Total spend till date", "number", TOTAL_PURCHASE_VALUE_LIFETIME, null, null, null, "aggr.tot_purchase_amt", "", "aggr.tot_purchase_amt"),
								TOTAL_PURCHASE_VALUE_LIFETIME_TOTAL_IS_MORE_THAN(101, 0, "Is more than", "Is more than", "number", TOTAL_PURCHASE_VALUE_LIFETIME_TOTAL,"double", null, ">",""),
								TOTAL_PURCHASE_VALUE_LIFETIME_TOTAL_IS_LESS_THEN(102, 0, "Is less than", "Is less than", "number", TOTAL_PURCHASE_VALUE_LIFETIME_TOTAL,"double", null, "<",""),
								TOTAL_PURCHASE_VALUE_LIFETIME_TOTAL_IS_IN_THE_RANGE(101, 0, "Is in the range of", "Is in the range of", "number", TOTAL_PURCHASE_VALUE_LIFETIME_TOTAL,"double", "double", "between",""),
							TOTAL_PURCHASE_VALUE_LIFETIME_AVG(10, 0, "Avg spend per product", "Avg spend per product", "number", TOTAL_PURCHASE_VALUE_LIFETIME, null, null, null, "aggr.avg_purchase_amt", "", "aggr.avg_purchase_amt"),
								TOTAL_PURCHASE_VALUE_LIFETIME_AVG_IS_MORE_THAN(101, 0, "Is more than", "Is more than", "number", TOTAL_PURCHASE_VALUE_LIFETIME_AVG,"double", null, ">",""),
								TOTAL_PURCHASE_VALUE_LIFETIME_AVG_IS_LESS_THEN(102, 0, "Is less than", "Is less than", "number", TOTAL_PURCHASE_VALUE_LIFETIME_AVG,"double", null, "<",""),
								TOTAL_PURCHASE_VALUE_LIFETIME_AVG_IS_IN_THE_RANGE(101, 0, "Is in the range of", "Is in the range of", "number", TOTAL_PURCHASE_VALUE_LIFETIME_AVG,"double", "double", "between",""),
							TOTAL_PURCHASE_VALUE_LIFETIME_MAX(10, 0, "Max spend per product", "Max spend per product", "number", TOTAL_PURCHASE_VALUE_LIFETIME, null, null, null, "aggr.max_purchase_amt", "", "aggr.max_purchase_amt"),
								TOTAL_PURCHASE_VALUE_LIFETIME_MAX_IS_MORE_THAN(101, 0, "Is more than", "Is more than", "number", TOTAL_PURCHASE_VALUE_LIFETIME_MAX,"double", null, ">",""),
								TOTAL_PURCHASE_VALUE_LIFETIME_MAX_IS_LESS_THEN(102, 0, "Is less than", "Is less than", "number", TOTAL_PURCHASE_VALUE_LIFETIME_MAX,"double", null, "<",""),
								TOTAL_PURCHASE_VALUE_LIFETIME_MAX_IS_IN_THE_RANGE(101, 0, "Is in the range of", "Is in the range of", "number", TOTAL_PURCHASE_VALUE_LIFETIME_MAX,"double", "double", "between",""),
							TOTAL_PURCHASE_VALUE_LIFETIME_AVG_IN_AN_YEAR(10, 0, "Avg spend per year", "Avg spend per year", "number", TOTAL_PURCHASE_VALUE_LIFETIME, null, null, null, "if((year(now())-year(c.created_date))=0,aggr.tot_purchase_amt,aggr.tot_purchase_amt/(year(now())-year(c.created_date)))", "", "aggr.avg_in_year"),
								TOTAL_PURCHASE_VALUE_LIFETIME_AVG_IN_AN_YEAR_IS_MORE_THAN(101, 0, "Is more than", "Is more than", "number", TOTAL_PURCHASE_VALUE_LIFETIME_AVG_IN_AN_YEAR,"double", null, ">",""),
								TOTAL_PURCHASE_VALUE_LIFETIME_AVG_IN_AN_YEAR_IS_LESS_THEN(102, 0, "Is less than", "Is less than", "number", TOTAL_PURCHASE_VALUE_LIFETIME_AVG_IN_AN_YEAR,"double", null, "<",""),
								TOTAL_PURCHASE_VALUE_LIFETIME_AVG_IN_AN_YEAR_IS_IN_THE_RANGE(101, 0, "Is in the range of", "Is in the range of", "number", TOTAL_PURCHASE_VALUE_LIFETIME_AVG_IN_AN_YEAR,"double", "double", "between",""),
								
						TOTAL_PURCHASE_VALUE_WITHOUT_TAX(10, 2, "Purchase Amount Before Tax", "Total Purchase Amount Before Tax", "number", null, null, null, null, null),
								
								
								TOTAL_PURCHASE_VALUE_WITHOUT_TAX_TOTAL(10, 0, "Total", "Total", "number", TOTAL_PURCHASE_VALUE_WITHOUT_TAX, null, null, null, "aggr_tot_withouttax", "", "sum(totSal.totAmtWithOutTax) "),
								TOTAL_PURCHASE_VALUE_WITHOUT_TAX_TOTAL_IS_MORE_THAN(101, 0, "Is more than", "Is more than", "number", TOTAL_PURCHASE_VALUE_WITHOUT_TAX_TOTAL,"double", null, ">",""),
								TOTAL_PURCHASE_VALUE_WITHOUT_TAX_TOTAL_IS_LESS_THEN(102, 0, "Is less than", "Is less than", "number", TOTAL_PURCHASE_VALUE_WITHOUT_TAX_TOTAL,"double", null, "<",""),
								TOTAL_PURCHASE_VALUE_WITHOUT_TAX_TOTAL_IS_IN_THE_RANGE(101, 0, "Is in the range of", "Is in the range of", "number", TOTAL_PURCHASE_VALUE_WITHOUT_TAX_TOTAL,"double", "double", "between",""),
								
									TOTAL_PURCHASE_VALUE_WITHOUT_TAX_AVG(10, 0, "Average of all", "Average of all", "number", TOTAL_PURCHASE_VALUE_WITHOUT_TAX, null, null, null, "aggr_avg_withouttax", "", "(sum(totSal.totAmtWithOutTax)/count(totSal.cid)) "),
										TOTAL_PURCHASE_VALUE_AVG_WITHOUT_TAX_IS_MORE_THAN(101, 0, "Is more than", "Is more than", "number", TOTAL_PURCHASE_VALUE_WITHOUT_TAX_AVG,"double", null, ">",""),
										TOTAL_PURCHASE_VALUE_AVG_WITHOUT_TAX_IS_LESS_THEN(102, 0, "Is less than", "Is less than", "number", TOTAL_PURCHASE_VALUE_WITHOUT_TAX_AVG,"double", null, "<",""),
										TOTAL_PURCHASE_VALUE_AVG_WITHOUT_TAX_IS_IN_THE_RANGE(101, 0, "Is in the range of", "Is in the range of", "number", TOTAL_PURCHASE_VALUE_WITHOUT_TAX_AVG,"double", "double", "between",""),
										
									TOTAL_PURCHASE_VALUE_WITHOUT_TAX_MAX(10, 0, "Maximum of all", "Maximum of all", "number", TOTAL_PURCHASE_VALUE_WITHOUT_TAX, null, null, null,"aggr_max_withouttax", "",  "max(totSal.totAmtWithOutTax) "),
										TOTAL_PURCHASE_VALUE_WITHOUT_TAX_MAX_IS_MORE_THAN(101, 0, "Is more than", "Is more than", "number", TOTAL_PURCHASE_VALUE_WITHOUT_TAX_MAX,"double", null, ">",""),
										TOTAL_PURCHASE_VALUE_WITHOUT_TAX_MAX_IS_LESS_THEN(102, 0, "Is less than", "Is less than", "number", TOTAL_PURCHASE_VALUE_WITHOUT_TAX_MAX,"double", null, "<",""),
										TOTAL_PURCHASE_VALUE_WITHOUT_TAX_MAX_IS_IN_THE_RANGE(101, 0, "Is in the range of", "Is in the range of", "number", TOTAL_PURCHASE_VALUE_WITHOUT_TAX_MAX,"double", "double", "between",""),
										
			PURCHASE_TOTAL_NUMBER_OF_ITEMS(8, 2, "Number of Items", "Number of Items", "number", null, null, null, null, null),			
			PURCHASE_TOTAL_NUMBER_OF_ITEMS_PURCHASED(7, 0, "Purchased", "Purchased", "number", PURCHASE_TOTAL_NUMBER_OF_ITEMS, null, null, null, "aggr_boughtQty", "", "SUM(totSal.boughtQty) "),
			PURCHASE_TOTAL_NUMBER_OF_ITEMS_PURCHASED_IS_EQUAL_TO(101, 0, "Equal to", "Equal to", "number", PURCHASE_TOTAL_NUMBER_OF_ITEMS_PURCHASED,"double", null, "=",""),
			PURCHASE_TOTAL_NUMBER_OF_ITEMS_PURCHASED_IS_MORE_THAN(101, 0, "Is more than", "Is more than", "number", PURCHASE_TOTAL_NUMBER_OF_ITEMS_PURCHASED,"double", null, ">",""),
			PURCHASE_TOTAL_NUMBER_OF_ITEMS_PURCHASED_IS_LESS_THEN(102, 0, "Is less than", "Is less than", "number", PURCHASE_TOTAL_NUMBER_OF_ITEMS_PURCHASED,"double", null, "<",""),
			PURCHASE_TOTAL_NUMBER_OF_ITEMS_PURCHASED_IS_IN_THE_RANGE(101, 0, "Is in the range of", "Is in the range of", "number", PURCHASE_TOTAL_NUMBER_OF_ITEMS_PURCHASED,"double", "double", "between",""),

		
			PURCHASE_TOTAL_NUMBER_OF_ITEMS_RETURNED(7, 0, "Returned", "Returned", "number", PURCHASE_TOTAL_NUMBER_OF_ITEMS, null, null, null, "aggr_returnedQty", "", "abs(SUM(totSal.returnedQty)) "),
			PURCHASE_TOTAL_NUMBER_OF_ITEMS_RETURNED_IS_EQUAL_TO(101, 0, "Equal to", "Equal to", "number", PURCHASE_TOTAL_NUMBER_OF_ITEMS_RETURNED,"double", null, "=",""),	
			PURCHASE_TOTAL_NUMBER_OF_ITEMS_RETURNED_IS_MORE_THAN(101, 0, "Is more than", "Is more than", "number", PURCHASE_TOTAL_NUMBER_OF_ITEMS_RETURNED,"double", null, ">",""),
				PURCHASE_TOTAL_NUMBER_OF_ITEMS_RETURNED_IS_LESS_THEN(102, 0, "Is less than", "Is less than", "number", PURCHASE_TOTAL_NUMBER_OF_ITEMS_RETURNED,"double", null, "<",""),
				PURCHASE_TOTAL_NUMBER_OF_ITEMS_RETURNED_IS_IN_THE_RANGE(101, 0, "Is in the range of", "Is in the range of", "number", PURCHASE_TOTAL_NUMBER_OF_ITEMS_RETURNED,"double", "double", "between",""),
		
						
						
			PURCHASE_TOTAL_NUMBER_OF_PURCHASES(8, 2, "Number of Purchases", "Total Number of Purchases", "number", null, null, null, null, null),
						
						PURCHASE_TOTAL_NUMBER_OF_PURCHASES_IN_ALL_PRODUCTS(7, 0, "in all Products", "in all Products", "number", PURCHASE_TOTAL_NUMBER_OF_PURCHASES, null, null, null,"aggr_count", "", "count(totSal.cid) "),
							PURCHASE_TOTAL_NUMBER_OF_PURCHASES_IN_ALL_PRODUCTS_IS_MORE_THAN(101, 0, "Is more than", "Is more than", "number", PURCHASE_TOTAL_NUMBER_OF_PURCHASES_IN_ALL_PRODUCTS,"double", null, ">",""),
							PURCHASE_TOTAL_NUMBER_OF_PURCHASES_IN_ALL_PRODUCTS_IS_LESS_THEN(102, 0, "Is less than", "Is less than", "number", PURCHASE_TOTAL_NUMBER_OF_PURCHASES_IN_ALL_PRODUCTS,"double", null, "<",""),
							PURCHASE_TOTAL_NUMBER_OF_PURCHASES_IN_ALL_PRODUCTS_IS_IN_THE_RANGE(101, 0, "Is in the range of", "Is in the range of", "number", PURCHASE_TOTAL_NUMBER_OF_PURCHASES_IN_ALL_PRODUCTS,"double", "double", "between",""),

			//newly added for RSBros
			PURCHASE_TOTAL_NUMBER_OF_LIFETIME_PURCHASES(18, 2, "Visits till date", "Visits till date", "number", null, null, null, null, null),
							
							PURCHASE_TOTAL_NUMBER_OF_LIFETIME_PURCHASES_IN_ALL_PRODUCTS(7, 0, "Total", "Total", "number", PURCHASE_TOTAL_NUMBER_OF_LIFETIME_PURCHASES, null, null, null,"aggr.tot_reciept_count", "", "aggr.tot_reciept_count"),
								PURCHASE_TOTAL_NUMBER_OF_LIFETIME_PURCHASES_IN_ALL_PRODUCTS_IS_EQUAL_TO(101, 0, "Equal to", "Equal to", "number", PURCHASE_TOTAL_NUMBER_OF_LIFETIME_PURCHASES_IN_ALL_PRODUCTS,"double", null, "=",""),
								PURCHASE_TOTAL_NUMBER_OF_LIFETIME_PURCHASES_IN_ALL_PRODUCTS_IS_MORE_THAN(101, 0, "Is more than", "Is more than", "number", PURCHASE_TOTAL_NUMBER_OF_LIFETIME_PURCHASES_IN_ALL_PRODUCTS,"double", null, ">",""),
								PURCHASE_TOTAL_NUMBER_OF_LIFETIME_PURCHASES_IN_ALL_PRODUCTS_IS_LESS_THEN(102, 0, "Is less than", "Is less than", "number", PURCHASE_TOTAL_NUMBER_OF_LIFETIME_PURCHASES_IN_ALL_PRODUCTS,"double", null, "<",""),
								PURCHASE_TOTAL_NUMBER_OF_LIFETIME_PURCHASES_IN_ALL_PRODUCTS_IS_IN_THE_RANGE(101, 0, "Is in the range of", "Is in the range of", "number", PURCHASE_TOTAL_NUMBER_OF_LIFETIME_PURCHASES_IN_ALL_PRODUCTS,"double", "double", "between",""),


			PURCHASE_TOTAL_PURCHASE_VALUE(7, 2, "Single Receipt", "Purchase Amount", "number", null, null, null, null, null),
							
							/*PURCHASE_TOTAL_PURCHASE_VALUE_TOTAL(7, 0, "Total", "number", PURCHASE_TOTAL_PURCHASE_VALUE, null, null, null, "aggr.tot_purchase_amt"),
								PURCHASE_TOTAL_PURCHASE_VALUE_TOTAL_IS_MORE_THAN(101, 0, "Is more than", "number", PURCHASE_TOTAL_PURCHASE_VALUE_TOTAL,"double", null, ">",""),
								PURCHASE_TOTAL_PURCHASE_VALUE_TOTAL_IS_LESS_THEN(102, 0, "Is less than", "number", PURCHASE_TOTAL_PURCHASE_VALUE_TOTAL,"double", null, "<",""),
								PURCHASE_TOTAL_PURCHASE_VALUE_TOTAL_IS_IN_THE_RANGE(101, 0, "Is in the range of", "number", PURCHASE_TOTAL_PURCHASE_VALUE_TOTAL,"double", "double", "between",""),
								*/
						PURCHASE_TOTAL_PURCHASE_VALUE_TOTAL(7, 0, "Total", "In Single Purchase", "number", PURCHASE_TOTAL_PURCHASE_VALUE, null, null, null, "new_tot", "", "totSal.totAmt "),
						PURCHASE_TOTAL_PURCHASE_VALUE_TOTAL_IS_MORE_THAN(101, 0, "Is more than", "Is more than", "number", PURCHASE_TOTAL_PURCHASE_VALUE_TOTAL,"double", null, ">",""),
						PURCHASE_TOTAL_PURCHASE_VALUE_TOTAL_IS_LESS_THEN(102, 0, "Is less than", "Is less than", "number", PURCHASE_TOTAL_PURCHASE_VALUE_TOTAL,"double", null, "<",""),
						PURCHASE_TOTAL_PURCHASE_VALUE_TOTAL_IS_IN_THE_RANGE(101, 0, "Is in the range of", "Is in the range of", "number", PURCHASE_TOTAL_PURCHASE_VALUE_TOTAL,"double", "double", "between",""),
						

							/*PURCHASE_TOTAL_PURCHASE_VALUE_AVG(7, 0, "Avg", "number", PURCHASE_TOTAL_PURCHASE_VALUE, null, null, null, "aggr.avg_purchase_amt"),
								PURCHASE_TOTAL_PURCHASE_VALUE_AVG_IS_MORE_THAN(101, 0, "Is more than", "number", PURCHASE_TOTAL_PURCHASE_VALUE_AVG,"double", null, ">",""),
								PURCHASE_TOTAL_PURCHASE_VALUE_AVG_IS_LESS_THEN(102, 0, "Is less than", "number", PURCHASE_TOTAL_PURCHASE_VALUE_AVG,"double", null, "<",""),
								PURCHASE_TOTAL_PURCHASE_VALUE_AVG_IS_IN_THE_RANGE(101, 0, "Is in the range of", "number", PURCHASE_TOTAL_PURCHASE_VALUE_AVG,"double", "double", "between",""),
							*/	
							PURCHASE_TOTAL_PURCHASE_VALUE_AVG(7, 0, "Average Item cost", "Average of all", "number", PURCHASE_TOTAL_PURCHASE_VALUE, null, null, null, "new_avg", "", "totSal.avgAmt"),
								PURCHASE_TOTAL_PURCHASE_VALUE_AVG_IS_MORE_THAN(101, 0, "Is more than", "Is more than", "number", PURCHASE_TOTAL_PURCHASE_VALUE_AVG,"double", null, ">",""),
								PURCHASE_TOTAL_PURCHASE_VALUE_AVG_IS_LESS_THEN(102, 0, "Is less than", "Is less than", "number", PURCHASE_TOTAL_PURCHASE_VALUE_AVG,"double", null, "<",""),
								PURCHASE_TOTAL_PURCHASE_VALUE_AVG_IS_IN_THE_RANGE(101, 0, "Is in the range of", "Is in the range of", "number", PURCHASE_TOTAL_PURCHASE_VALUE_AVG,"double", "double", "between",""),
								
								
								
								
							PURCHASE_TOTAL_PURCHASE_VALUE_MAX(7, 0, "Highest priced item's cost", "Maximum of all", "number", PURCHASE_TOTAL_PURCHASE_VALUE, null, null, null,"new_max", "",  "totSal.maxAmt"),
								PURCHASE_TOTAL_PURCHASE_VALUE_MAX_IS_MORE_THAN(101, 0, "Is more than", "Is more than", "number", PURCHASE_TOTAL_PURCHASE_VALUE_MAX,"double", null, ">",""),
								PURCHASE_TOTAL_PURCHASE_VALUE_MAX_IS_LESS_THEN(102, 0, "Is less than", "Is less than", "number", PURCHASE_TOTAL_PURCHASE_VALUE_MAX,"double", null, "<",""),
								PURCHASE_TOTAL_PURCHASE_VALUE_MAX_IS_IN_THE_RANGE(101, 0, "Is in the range of", "Is in the range of", "number", PURCHASE_TOTAL_PURCHASE_VALUE_MAX,"double", "double", "between",""),
								
							PURCHASE_TOTAL_NUMBER_OF_Items_IN_SINGLEPURCHASE(7, 0, "Basket Size", "Number of Purchases", "number", PURCHASE_TOTAL_PURCHASE_VALUE, null, null, null,"new_count", "", "totSal.itemCnt"),
							PURCHASE_TOTAL_NUMBER_OF_Items_IN_SINGLEPURCHASE_IS_MORE_THAN(101, 0, "Is more than", "Is more than", "number", PURCHASE_TOTAL_NUMBER_OF_Items_IN_SINGLEPURCHASE,"double", null, ">",""),
							PURCHASE_TOTAL_NUMBER_OF_Items_IN_SINGLEPURCHASE_IS_LESS_THEN(102, 0, "Is less than", "Is less than", "number", PURCHASE_TOTAL_NUMBER_OF_Items_IN_SINGLEPURCHASE,"double", null, "<",""),
							PURCHASE_TOTAL_NUMBER_OF_Items_IN_SINGLEPURCHASE_IS_IN_THE_RANGE(101, 0, "Is in the range of", "Is in the range of", "number", PURCHASE_TOTAL_NUMBER_OF_Items_IN_SINGLEPURCHASE,"double", "double", "between",""),
											
				//TODO need to modify after removing store number priority 
				PURCHASE_STORE_NUMBER(4, 2, "Store Number", "Store Number", true, "Store Number", "String", null, null, null, null, "sal.store_number"),
					PURCHASE_STORE_EQUALS(101, 0, "Equals", "Equals", "String", PURCHASE_STORE_NUMBER, "text", null, "is", ""),
					PURCHASE_STORE_DOES_NOT_EQUAL(3, 0, "Doesn't equal", "Doesn't equal", "String", PURCHASE_STORE_NUMBER, "text", null, "is not", ""),
					PURCHASE_STORE_ONE_OF(3, 0, "One of", "One of", "String", PURCHASE_STORE_NUMBER, "text", null, "is in", ""),
					
				PURCHASE_SUBSIDIARY_NUMBER(4, 2, "Subsidiary Number", "Subsidiary Number", true, "Subsidiary Number", "String", null, null, null, null, "sal.subsidiary_number"),
					PURCHASE_SUBSIDIARY_EQUALS(101, 0, "Equals", "Equals", "String", PURCHASE_SUBSIDIARY_NUMBER, "text", null, "is", ""),
					PURCHASE_SUBSIDIARY_DOES_NOT_EQUAL(3, 0, "Doesn't equal", "Doesn't equal", "String", PURCHASE_SUBSIDIARY_NUMBER, "text", null, "is not", ""),
					PURCHASE_SUBSIDIARY_ONE_OF(3, 0, "One of", "One of", "String", PURCHASE_SUBSIDIARY_NUMBER, "text", null, "is in", ""),
				
				PURCHASE_SKU(1, 2, "SKU","SKU", true, "SKU", "String", null, null, null, null, "sal.sku"),
					PURCHASE_SKU_EQUALS(101,0, "Equals", "Equals", "string", PURCHASE_SKU, "text", null,"is",""),
					PURCHASE_SKU_DOES_NOT_EQUAL(102, 0, "does not equal", "does not equal", "string", PURCHASE_SKU, "text", null,"is not",""),
					PURCHASE_SKU_ONE_OF(103, 0, "One of", "One of", "string", PURCHASE_SKU, "text", null,"is in",""),
					
					
				PURCHASE_ITEM_CATEGORY(2, 2, "Item Category", "Item Category", true, "Item Category", "string", null, null, null ,null, "sku.item_category"),
					
					PURCHASE_ITEM_CATEGORY_EQUALS(101, 0, "Equals", "Equals", "string", PURCHASE_ITEM_CATEGORY, "text", null ,"is ", ""),
					PURCHASE_ITEM_CATEGORY_DOES_NOT_EQUALS(100, 0, "Does not equal", "Does not equal", "string", PURCHASE_ITEM_CATEGORY, "text", null ,"is not", ""),
					PURCHASE_ITEM_CATEGORY_ONE_OF(103, 0, "One of", "One of", "string", PURCHASE_ITEM_CATEGORY, "text", null ,"is in", ""),
					PURCHASE_ITEM_CATEGORY_CONTAINS(104, 0, "Contains", "Contains", "string", PURCHASE_ITEM_CATEGORY, "text", null ,"contains", ""),
					PURCHASE_ITEM_CATEGORY_DOES_NOT_CONTAIN(105, 0, "Doesn't contain", "Doesn't contain", "string", PURCHASE_ITEM_CATEGORY, "text", null ,"does not contain", ""),
					PURCHASE_ITEM_CATEGORY_ONLY_IN(106, 0, "Only like", "Only like", "String", PURCHASE_ITEM_CATEGORY, "text", null, "only like", ""), //APP-4227
				
					
					
					PURCHASE_DEPARTMENT(4, 2, "Department", "Department", true, "Department", "String", null, null, null, null, "sku.department_code"),
					PURCHASE_DEPARTMENT_EQUALS(101, 0, "Equals", "Equals", "String", PURCHASE_DEPARTMENT, "text", null, "is", ""),
					PURCHASE_DEPARTMENT_DOES_NOT_EQUAL(3, 0, "Doesn't equal", "Doesn't equal", "String", PURCHASE_DEPARTMENT, "text", null, "is not", ""),
					PURCHASE_DEPARTMENT_ONE_OF(3, 0, "One of", "One of", "String", PURCHASE_DEPARTMENT, "text", null, "is in", ""),
					PURCHASE_DEPARTMENT_CODE_ONLY_IN(102, 0, "Only like", "Only like", "String", PURCHASE_DEPARTMENT, "text", null, "only like", ""), //APP-4227

					PURCHASE_DESCRIPTION(4, 2, "Description", "Description", true, "Description", "String", null, null, null, null, "sku.description"),
					PURCHASE_DESCRIPTION_EQUALS(101, 0, "Equals", "Equals", "String", PURCHASE_DESCRIPTION, "text", null, "is", ""),
					PURCHASE_DESCRIPTION_DOES_NOT_EQUAL(3, 0, "Doesn't equal", "Doesn't equal", "String", PURCHASE_DESCRIPTION, "text", null, "is not", ""),
					PURCHASE_DESCRIPTION_ONE_OF(3, 0, "One of", "One of", "String", PURCHASE_DESCRIPTION, "text", null, "is in", ""),
					PURCHASE_DESCRIPTION_CONTAINS(104, 0, "Contains", "Contains", "string", PURCHASE_DESCRIPTION, "text", null ,"contains", ""),
					PURCHASE_DESCRIPTION_DOES_NOT_CONTAIN(105, 0, "Doesn't contain", "Doesn't contain", "string", PURCHASE_DESCRIPTION, "text", null ,"does not contain", ""),

					
					PURCHASE_CLASS(4, 2, "Class", "Class", true, "Class", "String", null, null, null, null, "sku.class_code"),
					PURCHASE_CLASS_EQUALS(101, 0, "Equals", "Equals", "String", PURCHASE_CLASS, "text", null, "is", ""),
					PURCHASE_CLASS_DOES_NOT_EQUAL(3, 0, "Doesn't equal", "Doesn't equal", "String", PURCHASE_CLASS, "text", null, "is not", ""),
					PURCHASE_CLASS_ONE_OF(3, 0, "One of", "One of", "String", PURCHASE_CLASS, "text", null, "is in", ""),

					PURCHASE_SUBCLASS(4, 2, "Subclass", "Subclass", true, "Subclass", "String", null, null, null, null, "sku.subclass_code"),
					PURCHASE_SUBCLASS_EQUALS(101, 0, "Equals", "Equals", "String", PURCHASE_SUBCLASS, "text", null, "is", ""),
					PURCHASE_SUBCLASS_DOES_NOT_EQUAL(3, 0, "Doesn't equal", "Doesn't equal", "String", PURCHASE_SUBCLASS, "text", null, "is not", ""),
					PURCHASE_SUBCLASS_ONE_OF(3, 0, "One of", "One of", "String", PURCHASE_SUBCLASS, "text", null, "is in", ""),
					
					PURCHASE_DCS(4, 2, "DCS", "DCS", true, "DCS", "String", null, null, null, null, "sku.dcs"),
					PURCHASE_DCS_EQUALS(101, 0, "Equals", "Equals", "String", PURCHASE_DCS, "text", null, "is", ""),
					PURCHASE_DCS_DOES_NOT_EQUAL(3, 0, "Doesn't equal", "Doesn't equal", "String", PURCHASE_DCS, "text", null, "is not", ""),
					PURCHASE_DCS_ONE_OF(3, 0, "One of", "One of", "String", PURCHASE_DCS, "text", null, "is in", ""),
					
					PURCHASE_VENDOR_CODE(6, 2, "Vendor Code", "Vendor Code", true, "Vendor",  "String", null, null, null, null, "sku.vendor_code"),
					PURCHASE_VENDOR_CODE_EQUALS(101, 0, "Equals", "Equals", "String", PURCHASE_VENDOR_CODE, "text", null, "is", ""),
					PURCHASE_VENDOR_CODE_DOES_NOT_EQUAL(3, 0, "Doesn't equal","Doesn't equal", "String", PURCHASE_VENDOR_CODE, "text", null, "is not", ""),
					PURCHASE_VENDOR_CODE_ONE_OF(3, 0, "One of", "One of", "String", PURCHASE_VENDOR_CODE, "text", null, "is in", ""),
					
				//earned loyalty new		
				PURCHASE_LOYALTY_BALANCE(10, 2, "Earned Loyalty", "Loyalty Balance", "Number", null, null, null, null, ""),

				PURCHASE_LOYALTY_TOTAL_ISSUED_IS(101, 0, "Total", "Total Issued", "Number", PURCHASE_LOYALTY_BALANCE, "double", null, null, "loyalty.total_loyalty_earned"),
				PURCHASE_LOYALTY_BALANCE_IS(103, 0, "Balance", "Balance", "Number", PURCHASE_LOYALTY_BALANCE, "double", null, null, "loyalty.loyalty_balance"),
					
				PURCHASE_LOYALTY_POINTS_IS(1, 0, "Points", "Points", "Number", PURCHASE_LOYALTY_TOTAL_ISSUED_IS, "double", null, null, "loyalty.total_loyalty_earned"),
				PURCHASE_LOYALTY_TOTAL_USD_IS(102, 0, "Amount", "Amount", "Number", PURCHASE_LOYALTY_TOTAL_ISSUED_IS, "double", null, null, "loyalty.total_giftcard_amount"),
				
				PURCHASE_BALANCE_POINTS_IS(1, 0, "Points", "Points", "Number", PURCHASE_LOYALTY_BALANCE_IS, "double", null, null, "loyalty.loyalty_balance"),
				PURCHASE_BALANCE_TOTAL_USD_IS(102, 0, "Amount", "Amount", "Number", PURCHASE_LOYALTY_BALANCE_IS, "double", null, null, "loyalty.giftcard_balance"),
				
				PURCHASE_LOYALTY_POINTS_IS_MORE_THAN(101, 0, "More than", "More than", "number", PURCHASE_LOYALTY_POINTS_IS,"double", null, ">",""),
					PURCHASE_LOYALTY_TOTAL_IS_LESS_THAN(101, 0, "Less than", "Less than", "number", PURCHASE_LOYALTY_POINTS_IS,"double", null, "<",""),
					PURCHASE_LOYALTY_TOTAL_IS_EQUAL_TO(101, 0, "Equal to", "Equal to", "number", PURCHASE_LOYALTY_POINTS_IS,"double", null, "=",""),
					PURCHASE_LOYALTY_TOTAL_IS_BETWEEN(101, 0, "Between", "Between", "number", PURCHASE_LOYALTY_POINTS_IS,"double", "double", "between",""),
					PURCHASE_LOYALTY_TOTAL_IS_NO_VALUE(101, 0, "Empty", "Empty", "number", PURCHASE_LOYALTY_POINTS_IS, null, null, "none","", " IS NULL OR loyalty.total_loyalty_earned=0"),
				
				PURCHASE_LOYALTY_USD_IS_MORE_THAN(101, 0, "More than", "More than", "number", PURCHASE_LOYALTY_TOTAL_USD_IS,"double", null, ">",""),
					PURCHASE_USD_IS_LESS_THAN(101, 0, "Less than", "Less than", "number", PURCHASE_LOYALTY_TOTAL_USD_IS,"double", null, "<",""),
					PURCHASE_USD_IS_EQUAL_TO(101, 0, "Equal to", "Equal to", "number", PURCHASE_LOYALTY_TOTAL_USD_IS,"double", null, "=",""),
					PURCHASE_USD_IS_BETWEEN(101, 0, "Between", "Between", "number", PURCHASE_LOYALTY_TOTAL_USD_IS,"double", "double", "between",""),
					PURCHASE_USD_IS_NO_VALUE(101, 0, "Empty", "Empty", "number", PURCHASE_LOYALTY_TOTAL_USD_IS, null, null, "none","", " IS NULL OR loyalty.total_giftcard_amount=0"),
					
				PURCHASE_BALANCE_POINTS_IS_MORE_THAN(101, 0, "More than", "More than", "number", PURCHASE_BALANCE_POINTS_IS,"double", null, ">",""),
					PURCHASE_BALANCE_TOTAL_IS_LESS_THAN(101, 0, "Less than", "Less than", "number", PURCHASE_BALANCE_POINTS_IS,"double", null, "<",""),
					PURCHASE_BALANCE_TOTAL_IS_EQUAL_TO(101, 0, "Equal to", "Equal to", "number", PURCHASE_BALANCE_POINTS_IS,"double", null, "=",""),
					PURCHASE_BALANCE_TOTAL_IS_BETWEEN(101, 0, "Between", "Between", "number", PURCHASE_BALANCE_POINTS_IS,"double", "double", "between",""),
					PURCHASE_BALANCE_TOTAL_IS_NO_VALUE(101, 0, "Empty", "Empty", "number", PURCHASE_BALANCE_POINTS_IS, null, null, "none","", " IS NULL OR loyalty.loyalty_balance=0"),
				
				PURCHASE_BALANCE_USD_IS_MORE_THAN(101, 0, "More than", "More than", "number", PURCHASE_BALANCE_TOTAL_USD_IS,"double", null, ">",""),
					PURCHASE_BALANCE_USD_IS_LESS_THAN(101, 0, "Less than", "Less than", "number", PURCHASE_BALANCE_TOTAL_USD_IS,"double", null, "<",""),
					PURCHASE_BALANCE_USD_IS_EQUAL_TO(101, 0, "Equal to", "Equal to", "number", PURCHASE_BALANCE_TOTAL_USD_IS,"double", null, "=",""),
					PURCHASE_BALANCE_USD_IS_BETWEEN(101, 0, "Between", "Between", "number", PURCHASE_BALANCE_TOTAL_USD_IS,"double", "double", "between",""),
					PURCHASE_BALANCE_USD_IS_NO_VALUE(101, 0, "Empty", "Empty", "number", PURCHASE_BALANCE_TOTAL_USD_IS, null, null, "none","", " IS NULL OR loyalty.giftcard_balance=0"),
					
				VALUE_CODES_TOTAL(101,0,"","", "number", PURCHASE_LOYALTY_TOTAL_ISSUED_IS, "double",null,null,"aggrlty_rewardtot","","valuecodetotal"),
				VALUE_CODES_TOTAL_IS_MORE_THAN(101, 0, "More than", "More than", "number", VALUE_CODES_TOTAL,"double", null, ">",""),
				VALUE_CODES_TOTAL_IS_LESS_THAN(101, 0, "Less than", "Less than", "number", VALUE_CODES_TOTAL,"double", null, "<",""),
				VALUE_CODES_TOTAL_IS_EQUAL_TO(101, 0, "Equal to", "Equal to", "number", VALUE_CODES_TOTAL,"double", null, "=",""),
				VALUE_CODES_TOTAL_IS_BETWEEN(101, 0, "Between", "Between", "number", VALUE_CODES_TOTAL,"double", "double", "between",""),
				VALUE_CODES_TOTAL_IS_NO_VALUE(101, 0, "Empty", "Empty", "number", VALUE_CODES_TOTAL, null, null, "none","", " IS NULL OR ltybalance.total_earn_balance=0"),
				
				VALUE_CODES_TOTAL_BALANCE(101,0,"","", "number", PURCHASE_LOYALTY_BALANCE_IS, "double",null,null,"aggrlty_rewardbalance","","valuecodebalance"),
				VALUE_CODES_TOTAL_BALANCE_IS_MORE_THAN(101, 0, "More than", "More than", "number", VALUE_CODES_TOTAL_BALANCE,"double", null, ">",""),
				VALUE_CODES_TOTAL_BALANCE_IS_LESS_THAN(101, 0, "Less than", "Less than", "number", VALUE_CODES_TOTAL_BALANCE,"double", null, "<",""),
				VALUE_CODES_TOTAL_BALANCE_IS_EQUAL_TO(101, 0, "Equal to", "Equal to", "number", VALUE_CODES_TOTAL_BALANCE,"double", null, "=",""),
				VALUE_CODES_TOTAL_BALANCE_IS_BETWEEN(101, 0, "Between", "Between", "number", VALUE_CODES_TOTAL_BALANCE,"double", "double", "between",""),
				VALUE_CODES_TOTAL_BALANCE_IS_NO_VALUE(101, 0, "Empty", "Empty", "number", VALUE_CODES_TOTAL_BALANCE, null, null, "none","", " IS NULL OR ltybalance.balance=0"),
					 
				LOYALTY_TRANSACTIONS (11, 2, "Loyalty Transactions",false, "Loyalty transaction",  "Date", null, null, null,null,null),
				
				NUMBER_OF_TRANSACTIONS(101, 11, "Number of transactions", "Number of trx", "Number", LOYALTY_TRANSACTIONS, null, null,null,"", "",""),
				 NUMBER_OF_TRANSACTIONS_OF_ANY(1,101,"Any Transaction","All", "number", NUMBER_OF_TRANSACTIONS, "double",null,null,"aggrlty_count","","totaltrx"),
				 NUMBER_OF_TRANSACTIONS_OF_ISSUANCE(102,0,"Issuance","Issuance", "number", NUMBER_OF_TRANSACTIONS, "double",null,null,"aggrlty_totissued","","issued"),
				 NUMBER_OF_TRANSACTIONS_OF_GIFT_ISSUANCE(103,0,"Gift issuance","Gift issuance", "number", NUMBER_OF_TRANSACTIONS, "double",null,null,"aggrlty_totissued_gift","","giftIssued"),
				 NUMBER_OF_TRANSACTIONS_OF_REDEMPTION(104,0,"Redemption","Redemption", "number", NUMBER_OF_TRANSACTIONS, "double",null,null,"aggrlty_totredeemed","","redeemed"),
				 NUMBER_OF_TRANSACTIONS_OF_RETURN(105,0,"Return","Return", "number", NUMBER_OF_TRANSACTIONS, "double",null,null,"aggrlty_totreturned","","returntrx"),
				 //NUMBER_OF_TRANSACTIONS_OF_ADJUSTMENT(106,0,"Adjustment","Adjustment", "number", NUMBER_OF_TRANSACTIONS, "double",null,"aggrlty_totissued","ltytrxch.transaction_type ='Adjustment'"),
				 //NUMBER_OF_TRANSACTIONS_OF_VALUECODE(105,0,"","", "number", NUMBER_OF_TRANSACTIONS, "double",null,null,"aggrlty_totrewardissued","","rewardissued"),
				 
				 NUMBER_OF_TRANSACTIONS_IS_EQUAL_TO(01, 0, "Equal to", "Equal to", "number", NUMBER_OF_TRANSACTIONS_OF_ANY,"double",null, "=",""),
				 	NUMBER_OF_TRANSACTIONS_OF_ISSUANCE_IS_EQUAL_TO(101, 0, "Equal to", "Equal to", "number", NUMBER_OF_TRANSACTIONS_OF_ISSUANCE,"double", null, "=",""),
					NUMBER_OF_TRANSACTIONS_OF_GIFT_ISSUANCE_IS_EQUAL_TO(102, 0, "Equal to", "Equal to", "number", NUMBER_OF_TRANSACTIONS_OF_GIFT_ISSUANCE,"double", null, "=",""),
					NUMBER_OF_TRANSACTIONS_OF_REDEMPTION_IS_EQUAL_TO(103, 0, "Equal to", "Equal to", "number", NUMBER_OF_TRANSACTIONS_OF_REDEMPTION,"double", null, "=",""),
					NUMBER_OF_TRANSACTIONS_OF_RETURN_IS_EQUAL_TO(104, 0, "Equal to", "Equal to", "number", NUMBER_OF_TRANSACTIONS_OF_RETURN,"double", null, "=",""),
					//NUMBER_OF_TRANSACTIONS_OF_VALUECODE_IS_EQUAL_TO(104, 0, "Equal to", "Equal to", "number", NUMBER_OF_TRANSACTIONS_OF_VALUECODE,"double", null, "=",""),
					
				 NUMBER_OF_TRANSACTIONS_IS_MORE_THAN(01, 0, "Is more than", "Is more than", "number", NUMBER_OF_TRANSACTIONS_OF_ANY,"double",null, ">",""),
				 	NUMBER_OF_TRANSACTIONS_OF_ISSUANCE_IS_MORE_THAN(101, 0, "Is more than", "Is more than", "number", NUMBER_OF_TRANSACTIONS_OF_ISSUANCE,"double", null, ">",""),
					NUMBER_OF_TRANSACTIONS_OF_GIFT_ISSUANCE_IS_MORE_THAN(102, 0, "Is more than", "Is more than", "number", NUMBER_OF_TRANSACTIONS_OF_GIFT_ISSUANCE,"double", null, ">",""),
					NUMBER_OF_TRANSACTIONS_OF_REDEMPTION_IS_MORE_THAN(103, 0, "Is more than", "Is more than", "number", NUMBER_OF_TRANSACTIONS_OF_REDEMPTION,"double", null, ">",""),
					NUMBER_OF_TRANSACTIONS_OF_RETURN_IS_MORE_THAN(104, 0, "Is more than", "Is more than", "number", NUMBER_OF_TRANSACTIONS_OF_RETURN,"double", null, ">",""),
					//NUMBER_OF_TRANSACTIONS_OF_ADJUSTMENT_IS_MORE_THAN(105, 0, "Is more than", "Is more than", "number", NUMBER_OF_TRANSACTIONS_OF_ADJUSTMENT,"double", null, ">",""),
					//NUMBER_OF_TRANSACTIONS_OF_VALUECODE_IS_MORE_THAN(104, 0, "Is more than", "Is more than", "number", NUMBER_OF_TRANSACTIONS_OF_VALUECODE,"double", null, ">",""),
					
				 NUMBER_OF_TRANSACTIONS_IS_LESS_THAN(01, 0, "Is less than", "Is less than", "number", NUMBER_OF_TRANSACTIONS_OF_ANY,"double",null, "<",""),
				 	NUMBER_OF_TRANSACTIONS_OF_ISSUANCE_IS_LESS_THAN(101, 0, "Is less than", "Is less than", "number", NUMBER_OF_TRANSACTIONS_OF_ISSUANCE,"double", null, "<",""),
					NUMBER_OF_TRANSACTIONS_OF_GIFT_ISSUANCE_IS_LESS_THAN(102, 0, "Is less than", "Is less than", "number", NUMBER_OF_TRANSACTIONS_OF_GIFT_ISSUANCE,"double", null, "<",""),
					NUMBER_OF_TRANSACTIONS_OF_REDEMPTION_IS_LESS_THAN(103, 0, "Is less than", "Is less than", "number", NUMBER_OF_TRANSACTIONS_OF_REDEMPTION,"double", null, "<",""),
					NUMBER_OF_TRANSACTIONS_OF_RETURN_IS_LESS_THAN(104, 0, "Is less than", "Is less than", "number", NUMBER_OF_TRANSACTIONS_OF_RETURN,"double", null, "<",""),
					//NUMBER_OF_TRANSACTIONS_OF_ADJUSTMENT_IS_LESS_THAN(105, 0, "Is less than", "Is less than", "number", NUMBER_OF_TRANSACTIONS_OF_ADJUSTMENT,"double", null, ">",""),
					//NUMBER_OF_TRANSACTIONS_OF_VALUECODE_IS_LESS_THAN(104, 0, "Is less than", "Is less than", "number", NUMBER_OF_TRANSACTIONS_OF_VALUECODE,"double", null, "<",""),
				 
				 
				 
				 
				 TOTAL_AMOUNT(102, 11, "Total Transacted Amount", "Total amount", "Number", LOYALTY_TRANSACTIONS, null, null, null, "", "", ""),
				 //TOTAL_AMOUNT_OF_ANY(1,101,"Any Transaction","All", "number", TOTAL_AMOUNT, "double",null,"aggrlty_totamount","","count(totloyal.contact_id)"),
				 TOTAL_AMOUNT_OF_ISSUANCE(102,0,"Issuances","Issuance", "number", TOTAL_AMOUNT, "double",null,null,"aggrlty_totamtissued","","totIssuedAmt"),
				 TOTAL_AMOUNT_OF_GIFT_ISSUANCE(103,0,"Gift issuances","Gift issuance", "number", TOTAL_AMOUNT, "double",null,null,"aggrlty_totamtissued_gift","","totIssuedGiftAmt"),
				 TOTAL_AMOUNT_OF_REDEMPTION(104,0,"Redemptions","Redemption", "number", TOTAL_AMOUNT, "double",null,null,"aggrlty_totamtredeemed","","totredeemedAmt"),
				 TOTAL_AMOUNT_OF_RETURN(105,0,"Returns","Return", "number", TOTAL_AMOUNT, "double",null,null,"aggrlty_totamtreturned","","totreturnedAmt"),
				 //TOTAL_AMOUNT_OF_ADJUSTMENT(106,0,"Adjustments","Adjustment", "number", TOTAL_AMOUNT, "double",null,"aggrlty_totissued","ltytrxch.transaction_type ='Adjustment'"),
				 
				 //TOTAL_AMOUNT_IS_EQUAL_TO(01, 0, "Equal to", "Equal to", "number", TOTAL_AMOUNT_OF_ANY,"double",null, "=",""),
				 TOTAL_AMOUNT_OF_ISSUANCE_IS_EQUAL_TO(101, 0, "Equal to", "Equal to", "number", TOTAL_AMOUNT_OF_ISSUANCE,"double", null, "=",""),
				 TOTAL_AMOUNT_OF_GIFT_ISSUANCE_IS_EQUAL_TO(102, 0, "Equal to", "Equal to", "number", TOTAL_AMOUNT_OF_GIFT_ISSUANCE,"double", null, "=",""),
				 TOTAL_AMOUNT_OF_REDEMPTION_IS_EQUAL_TO(103, 0, "Equal to", "Equal to", "number", TOTAL_AMOUNT_OF_REDEMPTION,"double", null, "=",""),
				 TOTAL_AMOUNT_OF_RETURN_IS_EQUAL_TO(104, 0, "Equal to", "Equal to", "number", TOTAL_AMOUNT_OF_RETURN,"double", null, "=",""),
					
				 //TOTAL_AMOUNT_IS_MORE_THAN(01, 0, "Is more than", "Is more than", "number", TOTAL_AMOUNT_OF_ANY,"double",null, ">",""),
				 TOTAL_AMOUNT_OF_ISSUANCE_IS_MORE_THAN(101, 0, "Is more than", "Is more than", "number",TOTAL_AMOUNT_OF_ISSUANCE,"double", null, ">",""),
				 TOTAL_AMOUNT_OF_GIFT_ISSUANCE_IS_MORE_THAN(102, 0, "Is more than", "Is more than", "number", TOTAL_AMOUNT_OF_GIFT_ISSUANCE,"double", null, ">",""),
				 TOTAL_AMOUNT_OF_REDEMPTION_IS_MORE_THAN(103, 0, "Is more than", "Is more than", "number", TOTAL_AMOUNT_OF_REDEMPTION,"double", null, ">",""),
				 TOTAL_AMOUNT_OF_RETURN_IS_MORE_THAN(104, 0, "Is more than", "Is more than", "number", TOTAL_AMOUNT_OF_RETURN,"double", null, ">",""),
				 //TOTAL_AMOUNT_OF_ADJUSTMENT_IS_MORE_THAN(105, 0, "Is more than", "Is more than", "number", TOTAL_AMOUNT_OF_ADJUSTMENT,"double", null, ">",""),
					
				 //TOTAL_AMOUNT_IS_LESS_THAN(01, 0, "Is less than", "Is less than", "number", TOTAL_AMOUNT_OF_ANY,"double",null, "<",""),
				 TOTAL_AMOUNT_OF_ISSUANCE_IS_LESS_THAN(101, 0, "Is less than", "Is less than", "number", TOTAL_AMOUNT_OF_ISSUANCE,"double", null, "<",""),
				 TOTAL_AMOUNT_OF_GIFT_ISSUANCE_IS_LESS_THAN(102, 0, "Is less than", "Is less than", "number", TOTAL_AMOUNT_OF_GIFT_ISSUANCE,"double", null, "<",""),
				 TOTAL_AMOUNT_OF_REDEMPTION_IS_LESS_THAN(103, 0, "Is less than", "Is less than", "number", TOTAL_AMOUNT_OF_REDEMPTION,"double", null, "<",""),
				 TOTAL_AMOUNT_OF_RETURN_IS_LESS_THAN(104, 0, "Is less than", "Is less than", "number", TOTAL_AMOUNT_OF_RETURN,"double", null, "<",""),
				 //TOTAL_AMOUNT_OF_ADJUSTMENT_IS_LESS_THAN(105, 0, "Is less than", "Is less than", "number", TOTAL_AMOUNT_OF_ADJUSTMENT,"double", null, ">",""),
				 
				 TOTAL_REWARD(103, 11, "Total Points", "Total reward", "Number", LOYALTY_TRANSACTIONS, null, null, null, "aggr_tot", "", "SUM(ltytrxch.entered_amount) "),
				 TOTAL_REWARD_OF_REDEMPTION(104,0,"Redemptions","Redemption", "number", TOTAL_REWARD, "double",null,null,"aggrlty_totrewardredeemed","","totredeemedReward"),
				 TOTAL_REWARD_OF_REDEMPTION_IS_EQUAL_TO(104, 0, "Equal to", "Equal to", "number", TOTAL_REWARD_OF_REDEMPTION,"double", null, "=",""),
				 TOTAL_REWARD_OF_REDEMPTION_IS_MORE_THAN(103, 0, "Is more than", "Is more than", "number", TOTAL_REWARD_OF_REDEMPTION,"double", null, ">",""),
				 TOTAL_REWARD_OF_REDEMPTION_IS_LESS_THAN(103, 0, "Is less than", "Is less than", "number", TOTAL_REWARD_OF_REDEMPTION,"double", null, "<",""),

				 //dynamic for value codes
				 VALUE_CODES(104,11, "", "", "Number", LOYALTY_TRANSACTIONS, null, null,null,"", "",""),
				 VALUE_CODES_NUMBER_OF_TRANSACTIONS_OF_ANY(1,101,"Any Transaction","All", "number", VALUE_CODES, "double",null,null,"aggrlty_totrewardcount","","totalrewardtrx"),
				 VALUE_CODES_NUMBER_OF_TRANSACTIONS_OF_ISSUANCE(102,0,"Issuance","Issuance", "number", VALUE_CODES, "double",null,null,"aggrlty_totrewardissued","","rewardissued"),
				 VALUE_CODES_NUMBER_OF_TRANSACTIONS_OF_REDEMPTION(104,0,"Redemption","Redemption", "number", VALUE_CODES, "double",null,null,"aggrlty_totrewardsredeemed","","rewardredeemed"),
				 VALUE_CODES_NUMBER_OF_TRANSACTIONS_OF_RETURN(105,0,"Return","Return", "number", VALUE_CODES, "double",null,null,"aggrlty_totrewardreturned","","rewardreturntrx"),
				 	
				 VALUE_CODES_TRANSACTIONS_IS_EQUAL_TO(01, 0, "Equal to", "Equal to", "number", VALUE_CODES_NUMBER_OF_TRANSACTIONS_OF_ANY,"double",null, "=",""),
				 VALUE_CODES_TRANSACTIONS_OF_ISSUANCE_IS_EQUAL_TO(101, 0, "Equal to", "Equal to", "number", VALUE_CODES_NUMBER_OF_TRANSACTIONS_OF_ISSUANCE,"double", null, "=",""),
				 VALUE_CODES_TRANSACTIONS_OF_GIFT_ISSUANCE_IS_EQUAL_TO(102, 0, "Equal to", "Equal to", "number", VALUE_CODES_NUMBER_OF_TRANSACTIONS_OF_REDEMPTION,"double", null, "=",""),
				 VALUE_CODES_TRANSACTIONS_OF_REDEMPTION_IS_EQUAL_TO(103, 0, "Equal to", "Equal to", "number", VALUE_CODES_NUMBER_OF_TRANSACTIONS_OF_RETURN,"double", null, "=",""),
					
				 VALUE_CODES_TRANSACTIONS_IS_MORE_THAN(01, 0, "Is more than", "Is more than", "number", VALUE_CODES_NUMBER_OF_TRANSACTIONS_OF_ANY,"double",null, ">",""),
				 VALUE_CODES_TRANSACTIONS_OF_ISSUANCE_IS_MORE_THAN(101, 0, "Is more than", "Is more than", "number", VALUE_CODES_NUMBER_OF_TRANSACTIONS_OF_ISSUANCE,"double", null, ">",""),
				 VALUE_CODES_TRANSACTIONS_OF_GIFT_ISSUANCE_IS_MORE_THAN(102, 0, "Is more than", "Is more than", "number", VALUE_CODES_NUMBER_OF_TRANSACTIONS_OF_REDEMPTION,"double", null, ">",""),
				 VALUE_CODES_TRANSACTIONS_OF_REDEMPTION_IS_MORE_THAN(103, 0, "Is more than", "Is more than", "number", VALUE_CODES_NUMBER_OF_TRANSACTIONS_OF_RETURN,"double", null, ">",""),
					
				 VALUE_CODES_TRANSACTIONS_IS_LESS_THAN(01, 0, "Is less than", "Is less than", "number", VALUE_CODES_NUMBER_OF_TRANSACTIONS_OF_ANY,"double",null, "<",""),
				 VALUE_CODES_TRANSACTIONS_OF_ISSUANCE_IS_LESS_THAN(101, 0, "Is less than", "Is less than", "number", VALUE_CODES_NUMBER_OF_TRANSACTIONS_OF_ISSUANCE,"double", null, "<",""),
				 VALUE_CODES_TRANSACTIONS_OF_GIFT_ISSUANCE_IS_LESS_THAN(102, 0, "Is less than", "Is less than", "number", VALUE_CODES_NUMBER_OF_TRANSACTIONS_OF_REDEMPTION,"double", null, "<",""),
				 VALUE_CODES_TRANSACTIONS_OF_REDEMPTION_IS_LESS_THAN(103, 0, "Is less than", "Is less than", "number", VALUE_CODES_NUMBER_OF_TRANSACTIONS_OF_RETURN,"double", null, "<",""),
				 
				 
				 LOYALTY_TRANSACTION_DATE (12, 2, "Loyalty Transaction Date", "Loyalty transaction Date", true, "Created Date", "Date", null, null, null, null,"Date(ltytrxch.created_date)"),
					 LOYALTY_TRANSACTION_IS(101, 0, "is", "is", "date", LOYALTY_TRANSACTION_DATE, null, null, null, ""),
					 LOYALTY_TRANSACTION_IS_TO_DAY(102,  0, "Today", "Today", "date", LOYALTY_TRANSACTION_IS, null, null, "isToday", ""),
					 LOYALTY_TRANSACTION_EQUAL_TO(103,  0, "Equal to", "Equal to", "date", LOYALTY_TRANSACTION_IS, "Date", null, "is", ""),
					 LOYALTY_TRANSACTION_BETWEEN(104,  0, "Between", "Between", "date", LOYALTY_TRANSACTION_IS, "Date", "Date", "between", ""),
					 LOYALTY_TRANSACTION_ON_OR_AFTER(105, 0,  "On or after",  "On or after", "date", LOYALTY_TRANSACTION_IS, "Date", null, "onOrAfter", ""),
					 LOYALTY_TRANSACTION_ON_OR_BEFORE(106, 0, "On or before", "On or before", "date", LOYALTY_TRANSACTION_IS, "Date", null, "onOrBefore", ""),
					 LOYALTY_TRANSACTION_AFTER(107, 0, "After", "After", "date", LOYALTY_TRANSACTION_IS, "Date", null, "after", ""),
					 LOYALTY_TRANSACTION_BEFORE(108, 0, "Before", "Before", "date", LOYALTY_TRANSACTION_IS, "Date", null, "before", ""),
							
					 LOYALTY_TRANSACTION_IS_IGNORE_YEAR(101, 0, "is(in range)", "is(in range)", "date", LOYALTY_TRANSACTION_DATE, null, null, null, ""),
					 LOYALTY_TRANSACTION_BEFORE_BETWEEN_DAYS(109,0, "Last Range Of Days", "Last Range Of Days", "date", LOYALTY_TRANSACTION_IS_IGNORE_YEAR, "number", "number", "range_between", ""),
							//PURCHASE_AFTER_BETWEEN_DAYS(110,0, "Next Range Of Days", "date", PURCHASE_IS_IGNORE_YEAR, "number", "number", "after_between", ""),
							
							
					 LOYALTY_TRANSACTION_WITH_IN_LAST(201, 0, "within last", "within last", "date", LOYALTY_TRANSACTION_DATE, null,null, null, ""),
					 LOYALTY_TRANSACTION_DAYS(202, 0, "days", "days", "date", LOYALTY_TRANSACTION_WITH_IN_LAST, "31", "1", "withinlast_days", ""),
					 LOYALTY_TRANSACTION_WEEKS(203, 0, "weeks", "weeks", "date", LOYALTY_TRANSACTION_WITH_IN_LAST, "4", "7", "withinlast_weeks", ""),
					 LOYALTY_TRANSACTION_MONTHS(204, 0, "months", "months", "date", LOYALTY_TRANSACTION_WITH_IN_LAST, "12", "30", "withinlast_months", ""),
								
						
							
					 LOYALTY_TRANSACTION_NOT_WITH_IN_LAST(301, 0, "not within last", "not within last","date", LOYALTY_TRANSACTION_DATE,null,null, null,""),
						
					 LOYALTY_TRANSACTION_NOT_DAYS(302, 0, "days", "days", "date", LOYALTY_TRANSACTION_NOT_WITH_IN_LAST, "31", "1", "notwithinlast_days", ""),
					 LOYALTY_TRANSACTION_NOT_WEEKS(303, 0, "weeks", "weeks", "date", LOYALTY_TRANSACTION_NOT_WITH_IN_LAST, "4", "7", "notwithinlast_weeks", ""),
					 LOYALTY_TRANSACTION_NOT_MONTHS(304, 0, "months", "months", "date", LOYALTY_TRANSACTION_NOT_WITH_IN_LAST, "12", "30", "notwithinlast_months", ""),
						
					 LOYALTY_TRANSACTION_IS_NOT(401, 0, "is not", "is not", "date", LOYALTY_TRANSACTION_DATE, null,null, null, ""),
					 LOYALTY_TRANSACTION_NOT_AFTER(402, 0, "after", "after", "date", LOYALTY_TRANSACTION_IS_NOT, "date", null, "notafter", ""),
					 LOYALTY_TRANSACTION_NOT_BRFORE(403, 0, "before", "before", "date", LOYALTY_TRANSACTION_IS_NOT, "date", null, "notbefore", ""),
					
							//TODO need to enable after all thses data we fetch
						/*PURCHASE_TOTAL_NUMBER_OF_PURCHASES_IN_A_CATEGORY(7, 0, "in a Category", "number", PURCHASE_TOTAL_NUMBER_OF_PURCHASES, null, null, null, ""),
							PURCHASE_TOTAL_NUMBER_OF_PURCHASES_IN_A_CATEGORY_IS_MORE_THAN(101, 0, "Is more than", "number", PURCHASE_TOTAL_NUMBER_OF_PURCHASES_IN_A_CATEGORY,"double", null, ">",""),
							PURCHASE_TOTAL_NUMBER_OF_PURCHASES_IN_A_CATEGORY_IS_LESS_THEN(102, 0, "Is less than", "number", PURCHASE_TOTAL_NUMBER_OF_PURCHASES_IN_A_CATEGORY,"double", null, "<",""),
							PURCHASE_TOTAL_NUMBER_OF_PURCHASES_IN_A_CATEGORY_IS_IN_THE_RANGE(101, 0, "Is in the range of", "number", PURCHASE_TOTAL_NUMBER_OF_PURCHASES_IN_A_CATEGORY,"double", "double", "between",""),
							
						PURCHASE_TOTAL_NUMBER_OF_PURCHASES_IN_AN_SKU(7, 0, "in an SKU", "number", PURCHASE_TOTAL_NUMBER_OF_PURCHASES, null, null, null, ""),
							PURCHASE_TOTAL_NUMBER_OF_PURCHASES_IN_AN_SKU_IS_MORE_THAN(101, 0, "Is more than", "number", PURCHASE_TOTAL_NUMBER_OF_PURCHASES_IN_AN_SKU,"double", null, ">",""),
							PURCHASE_TOTAL_NUMBER_OF_PURCHASES_IN_AN_SKU_IS_LESS_THEN(102, 0, "Is less than", "number", PURCHASE_TOTAL_NUMBER_OF_PURCHASES_IN_AN_SKU,"double", null, "<",""),
							PURCHASE_TOTAL_NUMBER_OF_PURCHASES_IN_AN_SKU_IS_IN_THE_RANGE(101, 0, "Is in the range of", "number", PURCHASE_TOTAL_NUMBER_OF_PURCHASES_IN_AN_SKU,"double", "double", "between",""),
							*/
						
					//TODO need to enable after we fetch all the data
				/*PURCHASE_TOTAL_PURCHASE_IN_CATEGORY(8, 2, "Total Purchase in Category", "number", null, null, null, null, "total.totPurchaseCat"),
					PURCHASE_TOTAL_PURCHASE_IN_CATEGORY_IS_MORE_THAN(101, 0, "Is more than", "number", PURCHASE_TOTAL_PURCHASE_IN_CATEGORY,"double", null, ">",""),
					PURCHASE_TOTAL_PURCHASE_IN_CATEGORY_IS_LESS_THEN(100, 0, "Is less than", "number", PURCHASE_TOTAL_PURCHASE_IN_CATEGORY,"double", null, "<",""),
					PURCHASE_TOTAL_PURCHASE_IN_CATEGORY_IS_IN_THE_RANGE(101, 0, "Is in the range of", "number", PURCHASE_TOTAL_PURCHASE_IN_CATEGORY,"double", "double", "between",""),
					
				
					
				PURCHASE_TOTAL_PURCHASE_IN_SKU(8, 2, "Total Purchase in an SKU", "number", null, null, null, null, "total.totPurchaseCat"),
					PURCHASE_TOTAL_PURCHASE_IN_SKU_IS_MORE_THAN(101, 0, "Is more than", "number", PURCHASE_TOTAL_PURCHASE_IN_SKU,"double", null, ">",""),
					PURCHASE_TOTAL_PURCHASE_IN_SKU_IS_LESS_THEN(100, 0, "Is less than", "number", PURCHASE_TOTAL_PURCHASE_IN_SKU,"double", null, "<",""),
					PURCHASE_TOTAL_PURCHASE_IN_SKU_IS_IN_THE_RANGE(101, 0, "Is in the range of", "number", PURCHASE_TOTAL_PURCHASE_IN_SKU,"double", "double", "between",""),
					
				
				PURCHASE_TOTAL_LOYALTY_POINTS(10, 2, "Total Reward Points", "number",null, null, null, null, "total.rewardPoints"),	
					
					PURCHASE_TOTAL_LOYALTY_POINTS_IS(101, 0, "is", "number",PURCHASE_TOTAL_LOYALTY_POINTS, null, null, null, ""),
					PURCHASE_TOTAL_LOYALTY_POINTS_IS_MORE_THAN(201, 0, "more than", "number", PURCHASE_TOTAL_LOYALTY_POINTS_IS,"double", null, ">",""),
					PURCHASE_TOTAL_LOYALTY_POINTS_IS_LESS_THEN(200, 0, "less than", "number", PURCHASE_TOTAL_LOYALTY_POINTS_IS,"double", null, "<",""),
					PURCHASE_TOTAL_LOYALTY_POINTS_IS_EQUAL_TO(203, 0, "Equal to", "number", PURCHASE_TOTAL_LOYALTY_POINTS_IS,"double", null, "=",""),	
					PURCHASE_TOTAL_LOYALTY_POINTS_IS_BETWEEN(204, 0, "between", "number", PURCHASE_TOTAL_LOYALTY_POINTS_IS,"double", "double", "between",""),
					*/
				PURCHASE_PROMO(3, 2, "Store Promotion Code", "Promo", true, "Promo Code", "String", null, null, null, null, "sal.promo_code"),
					PURCHASE_PROMO_EQUALS(101, 0, "Equals", "Equals", "String", PURCHASE_PROMO, "text", null, "is", ""),
					PURCHASE_PROMO_DOES_NOT_EQUAL(3, 0, "Doesn't equal", "Doesn't equal", "String", PURCHASE_PROMO, "text", null, "is not", ""),
					PURCHASE_PROMO_ONE_OF(3, 0, "One of", "One of", "String", PURCHASE_PROMO, "text", null, "is in", ""),
				
					
				PURCHASE_TENDER_TYPE(9, 2, "Tender Type", "Tender Type", true, "Tender Type", "String", null, null, null, null, "sal.tender_type"),
					PURCHASE_TENDER_TYPE_IS(101, 0, "is", "is", "String", PURCHASE_TENDER_TYPE, null, null, null, ""),
						PURCHASE_TENDER_IS_CASH(201, 0, "Cash", "Cash", "String", PURCHASE_TENDER_TYPE_IS, null, null, "is", "", "Cash"),
						PURCHASE_TENDER_IS_CREDIT_CARD(202, 0, "Credit Card", "Credit Card", "String", PURCHASE_TENDER_TYPE_IS, null, null, "equal", "", "CC"),
						PURCHASE_TENDER_IS_DEBIT_CARD(203, 0, "Debit Card", "Debit Card", "String", PURCHASE_TENDER_TYPE_IS, null, null, "equals", "Debit Card"),
						PURCHASE_TENDER_IS_GIFT_CARD(204, 0, "Gift Card", "Gift Card", "String", PURCHASE_TENDER_TYPE_IS, null, null, "equal to", "Gift Card"),
						PURCHASE_TENDER_IS_GIFT_CERTIFICATE(205, 0, "Gift Certificate", "Gift Certificate", "String", PURCHASE_TENDER_TYPE_IS, null, null, "is equal", "Gift Certificate"),
						PURCHASE_TENDER_IS_TRAVELLERS_CHECK(206, 0, "Travelers Check", "Travelers Check", "String", PURCHASE_TENDER_TYPE_IS, null, null, "is equals", "Travelers Check"),
						PURCHASE_TENDER_IS_CHECK(207, 0, "Check", "Check", "String", PURCHASE_TENDER_TYPE_IS, null, null, "is equal to", "Check"),
						PURCHASE_TENDER_IS_DISNEY_MATRA_HOTEL(208, 0, "Disney Matra/Hotel", "Disney Matra/Hotel", "String", PURCHASE_TENDER_TYPE_IS, null, null, "is equals to", "Disney Matra/Hotel"),
						
			
	PURCHASE_TOT_LOYALTY_POINTS(10, 1, "Total Loyalty Earned", "Total Loyalty Earned", "Number", null, null, null, null, "loyalty.total_loyalty_earned"),
		PURCHASE_TOT_LOYALTY_POINTS_IS(101, 0, "is", "is", "Number", PURCHASE_TOT_LOYALTY_POINTS, null, null, null, ""),
			PURCHASE_TOT_LOYALTY_POINTS_IS_MORE_THAN(101, 0, "More than", "More than", "number", PURCHASE_TOT_LOYALTY_POINTS_IS,"double", null, ">",""),
			PURCHASE_TOT_LOYALTY_POINTS_IS_LESS_THAN(101, 0, "Less than", "Less than", "number", PURCHASE_TOT_LOYALTY_POINTS_IS,"double", null, "<",""),
			PURCHASE_TOT_LOYALTY_POINTS_IS_EQUAL_TO(101, 0, "Equal to", "Equal to", "number", PURCHASE_TOT_LOYALTY_POINTS_IS,"double", null, "=",""),
			PURCHASE_TOT_LOYALTY_POINTS_IS_BETWEEN(101, 0, "Between", "Between", "number", PURCHASE_TOT_LOYALTY_POINTS_IS,"double", "double", "between",""),
// promotion rules
				 PROMO_OPTCULTURE_PROMOTION_NAME(17, 4, "OptCulture Promotion Name", "OptCulture Promotion Name", "String", null, "text", null, null,"cp.coupon_id::cd.coupon_id"),
				      PROMO_OPTCULTURE_NAME(101,0,"is","is","String",PROMO_OPTCULTURE_PROMOTION_NAME,"text",null,"is",""), 
				
				    
				      PROMO_CODE_PROMO_STATUS(18, 2, "Promo-code Status", "Promo-code Status", "String", null, null, null, null, ""),
				      PROMO_OPTCULTURE_CONTACT_HAVE(101,0,"have","have","String",PROMO_CODE_PROMO_STATUS,null,null,"",""),
				         PROMO_OPTCULTURE_CONTACT_ISSUED(201,0,"Issued Promo Code","Issued Promo Code","String",PROMO_OPTCULTURE_CONTACT_HAVE,null,null,"is value","cd.status"," IN('Active','Redeemed')"), 
				         PROMO_OPTCULTURE_CONTACT_REDEEMED(202,0,"Redeemed Promo Code","Redeemed Promo Code","String",PROMO_OPTCULTURE_CONTACT_HAVE,null,null,"is in redeem","cd.status","Redeemed"),
				      PROMO_OPTCULTURE_CONTACT_HAVE_NOT(102,0,"have not","have not","String",PROMO_CODE_PROMO_STATUS,null,null,"!=",""),
				        PROMO_OPTCULTURE_CONTACT_ISSUED_NOT(201,0,"Issued Promo Code","Issued Promo Code","String",PROMO_OPTCULTURE_CONTACT_HAVE_NOT,null,null,"is not in active","cd.status","notactive"), 
				        PROMO_OPTCULTURE_CONTACT_REDEEMED_NOT(202,0,"Redeemed Promo Code","Redeemed Promo Code","String",PROMO_OPTCULTURE_CONTACT_HAVE_NOT,null,null,"is not in redeem","cd.status","Active"),
				     
				  
				     
				    PROMO_CODE_DATE_STATUS (19, 2, "Promo-code Date", "Promo-code Date",  "Date", null, null, null, null,""),
				       PROMO_CODE_ISSUED(101, 19, "Issued on", "Issued Promotion", "string", PROMO_CODE_DATE_STATUS, null, null,"is issued", "cd.status", "Active","","date(cd.issued_on)"),
				       PROMO_CODE_REDEEMED(102, 19,"Redeemed on", "Redeemed Promotion", "string", PROMO_CODE_DATE_STATUS, null, null, "is redeemed","cd.status", "Redeemed","","date(cd.redeemed_on)"),
				       //PROMO_CODE_EXPIRED(103, 19, "Expiry on", "Expiry Promotion", "string", PROMO_CODE_DATE_STATUS, null, null,"is expired", "cd.status", "Expired","","date(cd.expired_on)"),
				       PROMO_CODE_EXPIRED(103, 19, "Expiry on", "Expiry Promotion", "string", PROMO_CODE_DATE_STATUS, null, null,"is expired", "cd.status", "Active','Expired","","date(cd.expired_on)"),
			         
				       PROMO_DATE_IS_ISSUED(101, 0, "is", "is", "date", PROMO_CODE_ISSUED, null, null, "is", ""),
				       PROMO_DATE_WITH_IN_LAST_ISSUED(102, 0, "within last", "within last", "date", PROMO_CODE_ISSUED, null,null, "is within last", ""),
				       PROMO_DATE_NOT_WITH_IN_LAST_ISSUED(103, 0, "not within last", "not within last","date", PROMO_CODE_ISSUED,null,null, "not within last",""),
				       PROMO_IS_RANGE_IGNORE_YEAR_ISSSUED(105, 0, "is(in range)", "is(in range)", "date", PROMO_CODE_ISSUED, null, null, "is in range", ""),
					 
				       PROMO_DATE_IS_REDEEM(101, 0, "is", "is", "date", PROMO_CODE_REDEEMED, null, null, "is", ""),
				       PROMO_DATE_WITH_IN_LAST_REDEEM(102, 0, "within last", "within last", "date", PROMO_CODE_REDEEMED, null,null, "is within last", ""),
				       PROMO_DATE_NOT_WITH_IN_LAST_REDEEM(103, 0, "not within last", "not within last","date", PROMO_CODE_REDEEMED,null,null, "not within last",""),
				       PROMO_IS_RANGE_IGNORE_YEAR_REDEEM(105, 0, "is(in range)", "is(in range)", "date", PROMO_CODE_REDEEMED, null, null, "is in range", ""),
					  
				       PROMO_DATE_IS_EXPIRED(101, 0, "is", "is", "date", PROMO_CODE_EXPIRED, null, null, "is", ""),
				       PROMO_DATE_IS_IN_RANGE_EXPIRED(104, 0, "is in next " ,false, "is in next", "date", PROMO_CODE_EXPIRED, null,null, "is within next",""),
			         
			          
				       PROMO_ISSUED_DATE_TO_DAY(101,  0, "Today", "Today", "date", PROMO_DATE_IS_ISSUED, null, null, "isToday","", "Today"),
				         PROMO_ISSUED_DATE_EQUAL_TO(102,  0, "Equal to", "Equal to", "date", PROMO_DATE_IS_ISSUED, "Date", null, "equal to", "",""),
				         PROMO_ISSUED_DATE_BETWEEN(103,  0, "Between", "Between", "date", PROMO_DATE_IS_ISSUED, "Date", "Date", "between", ""),
				         PROMO_ISSUED_DATE_ON_OR_AFTER(104, 0,  "On or after",  "On or after", "date", PROMO_DATE_IS_ISSUED, "Date", null, "onOrAfter", ""),
				         PROMO_ISSUED_DATE_ON_OR_BEFORE(105, 0, "On or before", "On or before", "date", PROMO_DATE_IS_ISSUED, "Date", null, "onOrBefore", ""),
				         PROMO_ISSUED_DATE_AFTER(106, 0, "After", "After", "date", PROMO_DATE_IS_ISSUED, "Date", null, "after", ""),
				         PROMO_ISSUED_DATE_BEFORE(107, 0, "Before", "Before", "date", PROMO_DATE_IS_ISSUED, "Date", null, "before", ""),
				       PROMO_ISSUED_DATE_DAYS(101, 0, "days", "days", "date", PROMO_DATE_WITH_IN_LAST_ISSUED, "31", "1", "withinlast_days", ""),
				         PROMO_ISSUED_DATE_WEEKS(102, 0, "weeks", "weeks", "date", PROMO_DATE_WITH_IN_LAST_ISSUED, "4", "7", "withinlast_weeks", ""),
				         PROMO_ISSUED_DATE_MONTHS(103, 0, "months", "months", "date", PROMO_DATE_WITH_IN_LAST_ISSUED, "12", "30", "withinlast_months", ""),
				       PROMO_ISSUED_DATE_NOT_DAYS(101, 0, "days", "days", "date", PROMO_DATE_NOT_WITH_IN_LAST_ISSUED, "31", "1", "notwithinlast_days", ""),
				         PROMO_ISSUED_DATE_NOT_WEEKS(102, 0, "weeks", "weeks", "date", PROMO_DATE_NOT_WITH_IN_LAST_ISSUED, "4", "7", "notwithinlast_weeks", ""),
				         PROMO_ISSUED_DATE_NOT_MONTHS(103, 0, "months", "months", "date", PROMO_DATE_NOT_WITH_IN_LAST_ISSUED, "12", "30", "notwithinlast_months", ""),
				    PROMO_ISSUED_BEFORE_BETWEEN_IS_IGNORE_DAY(108,0, "Last Range Of Days", "Last Range Of Days", "date",PROMO_IS_RANGE_IGNORE_YEAR_ISSSUED, "number", "number", "range_between", ""),
						  
				         
				    PROMO_REDEEM_DATE_TO_DAY(101,  0, "Today", "Today", "date", PROMO_DATE_IS_REDEEM, null, null, "isToday","", "Today"),
				          PROMO_REDEEM_DATE_EQUAL_TO(102,  0, "Equal to", "Equal to", "date", PROMO_DATE_IS_REDEEM, "Date", null, "equal to", "",""),
				          PROMO_REDEEM_DATE_BETWEEN(103,  0, "Between", "Between", "date", PROMO_DATE_IS_REDEEM, "Date", "Date", "between", ""),
				          PROMO_REDEEM_DATE_ON_OR_AFTER(104, 0,  "On or after",  "On or after", "date", PROMO_DATE_IS_REDEEM, "Date", null, "onOrAfter", ""),
				          PROMO_REDEEM_DATE_ON_OR_BEFORE(105, 0, "On or before", "On or before", "date", PROMO_DATE_IS_REDEEM, "Date", null, "onOrBefore", ""),
				          PROMO_REDEEM_DATE_AFTER(106, 0, "After", "After", "date", PROMO_DATE_IS_REDEEM, "Date", null, "after", ""),
				          PROMO_REDEEM_DATE_BEFORE(107, 0, "Before", "Before", "date", PROMO_DATE_IS_REDEEM, "Date", null, "before", ""),
				    PROMO_REDEEM_DATE_DAYS(101, 0, "days", "days", "date", PROMO_DATE_WITH_IN_LAST_REDEEM, "31", "1", "withinlast_days", ""),
				          PROMO__REDEEM_DATE_WEEKS(102, 0, "weeks", "weeks", "date", PROMO_DATE_WITH_IN_LAST_REDEEM, "4", "7", "withinlast_weeks", ""),
				          PROMO_REDEEM_DATE_MONTHS(103, 0, "months", "months", "date", PROMO_DATE_WITH_IN_LAST_REDEEM, "12", "30", "withinlast_months", ""),
				        
				       PROMO_REDEEM_DATE_NOT_DAYS(101, 0, "days", "days", "date", PROMO_DATE_NOT_WITH_IN_LAST_REDEEM, "31", "1", "notwithinlast_days", ""),
				          PROMO_REDEEM_DATE_NOT_WEEKS(102, 0, "weeks", "weeks", "date", PROMO_DATE_NOT_WITH_IN_LAST_REDEEM, "4", "7", "notwithinlast_weeks", ""),
				          PROMO_REDEEM_DATE_NOT_MONTHS(103, 0, "months", "months", "date",PROMO_DATE_NOT_WITH_IN_LAST_REDEEM, "12", "30", "notwithinlast_months", ""),
				      PROMO_BEFORE_BETWEEN_IS_IGNORE_DAY_REDEEM(108,0, "Last Range Of Days", "Last Range Of Days", "date", PROMO_IS_RANGE_IGNORE_YEAR_REDEEM, "number", "number", "range_between", ""),	
				         
				         
				        PROMO_EXDATE_TO_DAY(101,  0, "Today", "Today", "date", PROMO_DATE_IS_EXPIRED, null, null, "isetoday","", "Today"),
				          PROMO_EXPIRY_DATE_EQUAL_TO(102,  0, "Equal to", "Equal to", "date", PROMO_DATE_IS_EXPIRED, "Date", null, "isequal", "",""),
				          PROMO_EXPIRY_DATE_BETWEEN(103,  0, "Between", "Between", "date", PROMO_DATE_IS_EXPIRED, "Date", "Date", "isebetween", ""),
				          PROMO_EXPIRY_DATE_ON_OR_AFTER(104, 0,  "On or after",  "On or after", "date", PROMO_DATE_IS_EXPIRED, "Date", null, "iseonOrAfter", ""),
				          PROMO_EXPIRY_DATE_ON_OR_BEFORE(105, 0, "On or before", "On or before", "date", PROMO_DATE_IS_EXPIRED, "Date", null, "iseonOrBefore", ""),
				          PROMO_EXPIRY_DATE_AFTER(106, 0, "After", "After", "date", PROMO_DATE_IS_EXPIRED, "Date", null, "iseafter", ""),
				          PROMO_EXPIRY_DATE_BEFORE(107, 0, "Before", "Before", "date", PROMO_DATE_IS_EXPIRED, "Date", null, "isebefore", ""),
				      
				   
				          PROMO_IS_IN_RANGE_DAYS(101, 0, "days", "days", "date", PROMO_DATE_IS_IN_RANGE_EXPIRED, "31", "1", "iswithinnext_days", ""),
				          PROMO_IS_IN_RANGE_WEEKS(102, 0, "weeks", "weeks", "date", PROMO_DATE_IS_IN_RANGE_EXPIRED, "4", "7", "iswithinnext_weeks", ""),
				          PROMO_IS_IN_RANGE_MONTHS(103, 0, "months", "months", "date", PROMO_DATE_IS_IN_RANGE_EXPIRED, "12", "30", "iswithinnext_months", ""),
						  
			         			
			
	PURCHASE_TOT_GIFT_CARD_BALANCE(11, 1, "Total Gift-card Balance", "Total Gift-card Balance", "Number", null, null, null, null, "loyalty.giftcard_balance"),
		PURCHASE_TOT_GIFT_CARD_BALANCE_IS(101, 0, "is", "is", "Number", PURCHASE_TOT_GIFT_CARD_BALANCE, null, null, null, ""),
			PURCHASE_TOT_GIFT_CARD_BALANCE_IS_MORE_THAN(101, 0, "More than", "More than", "number", PURCHASE_TOT_GIFT_CARD_BALANCE_IS,"double", null, ">",""),
			PURCHASE_TOT_GIFT_CARD_BALANCE_IS_LESS_THAN(101, 0, "Less than", "Less than", "number", PURCHASE_TOT_GIFT_CARD_BALANCE_IS,"double", null, "<",""),
			PURCHASE_TOT_GIFT_CARD_BALANCE_IS_EQUAL_TO(101, 0, "Equal to", "Equal to", "number", PURCHASE_TOT_GIFT_CARD_BALANCE_IS,"double", null, "=",""),
			PURCHASE_TOT_GIFT_CARD_BALANCE_IS_BETWEEN(101, 0, "Between", "Between", "number", PURCHASE_TOT_GIFT_CARD_BALANCE_IS,"double", "double", "between",""),
			PURCHASE_TOT_GIFT_CARD_BALANCE_IS_NO_VALUE(101, 0, "Empty", "Empty", "number", PURCHASE_TOT_GIFT_CARD_BALANCE_IS, null, null, "none","", " IS NULL"),	
		
	//earned loyalty old
	/*PURCHASE_LOYALTY_BALANCE(10, 2, "Earned Loyalty", "Loyalty Balance", "Number", null, null, null, null, ""),
		PURCHASE_LOYALTY_TOTAL_POINTS_IS(101, 0, "Total Points", "Total Points", "Number", PURCHASE_LOYALTY_BALANCE, null, null, null, "loyalty.total_loyalty_earned"),
			PURCHASE_LOYALTY_TOTAL_POINTS_IS_MORE_THAN(101, 0, "More than", "More than", "number", PURCHASE_LOYALTY_TOTAL_POINTS_IS,"double", null, ">",""),
			PURCHASE_LOYALTY_TOTAL_POINTS_IS_LESS_THAN(101, 0, "Less than", "Less than", "number", PURCHASE_LOYALTY_TOTAL_POINTS_IS,"double", null, "<",""),
			PURCHASE_LOYALTY_TOTAL_POINTS_IS_EQUAL_TO(101, 0, "Equal to", "Equal to", "number", PURCHASE_LOYALTY_TOTAL_POINTS_IS,"double", null, "=",""),
			PURCHASE_LOYALTY_TOTAL_POINTS_IS_BETWEEN(101, 0, "Between", "Between", "number", PURCHASE_LOYALTY_TOTAL_POINTS_IS,"double", "double", "between",""),
			PURCHASE_LOYALTY_TOTAL_POINTS_IS_NO_VALUE(101, 0, "Empty", "Empty", "number", PURCHASE_LOYALTY_TOTAL_POINTS_IS, null, null, "none","", " IS NULL OR loyalty.total_loyalty_earned=0"),
	
		PURCHASE_LOYALTY_TOTAL_USD_IS(102, 0, "Total Amount", "Total Amount", "Number", PURCHASE_LOYALTY_BALANCE, null, null, null, "loyalty.total_giftcard_amount"),
			PURCHASE_LOYALTY_TOTAL_USD_IS_MORE_THAN(102, 0, "More than", "More than", "number", PURCHASE_LOYALTY_TOTAL_USD_IS,"double", null, ">",""),
			PURCHASE_LOYALTY_TOTAL_USD_IS_LESS_THAN(102, 0, "Less than", "Less than", "number", PURCHASE_LOYALTY_TOTAL_USD_IS,"double", null, "<",""),
			PURCHASE_LOYALTY_TOTAL_USD_IS_EQUAL_TO(102, 0, "Equal to", "Equal to", "number", PURCHASE_LOYALTY_TOTAL_USD_IS,"double", null, "=",""),
			PURCHASE_LOYALTY_TOTAL_USD_IS_BETWEEN(102, 0, "Between", "Between", "number", PURCHASE_LOYALTY_TOTAL_USD_IS,"double", "double", "between",""),
			PURCHASE_LOYALTY_TOTAL_USD_IS_NO_VALUE(102, 0, "Empty", "Empty", "number", PURCHASE_LOYALTY_TOTAL_USD_IS, null, null, "none","", " IS NULL OR loyalty.total_giftcard_amount=0"),	
		
		
		PURCHASE_LOYALTY_BALANCE_POINTS_IS(103, 0, "Balance Points", "is points", "Number", PURCHASE_LOYALTY_BALANCE, null, null, null, "loyalty.loyalty_balance"),
			PURCHASE_LOYALTY_BALANCE_POINTS_IS_MORE_THAN(101, 0, "More than", "More than", "number", PURCHASE_LOYALTY_BALANCE_POINTS_IS,"double", null, ">",""),
			PURCHASE_LOYALTY_BALANCE_POINTS_IS_LESS_THAN(101, 0, "Less than", "Less than", "number", PURCHASE_LOYALTY_BALANCE_POINTS_IS,"double", null, "<",""),
			PURCHASE_LOYALTY_BALANCE_POINTS_IS_EQUAL_TO(101, 0, "Equal to", "Equal to", "number", PURCHASE_LOYALTY_BALANCE_POINTS_IS,"double", null, "=",""),
			PURCHASE_LOYALTY_BALANCE_POINTS_IS_BETWEEN(101, 0, "Between", "Between", "number", PURCHASE_LOYALTY_BALANCE_POINTS_IS,"double", "double", "between",""),
			PURCHASE_LOYALTY_BALANCE_POINTS_IS_NO_VALUE(101, 0, "Empty", "Empty", "number", PURCHASE_LOYALTY_BALANCE_POINTS_IS, null, null, "none","", " IS NULL OR loyalty.loyalty_balance=0"),
			
		PURCHASE_LOYALTY_BALANCE_USD_IS(104, 0, "Balance Amount", "is amount", "Number", PURCHASE_LOYALTY_BALANCE, null, null, null, "loyalty.giftcard_balance"),
			PURCHASE_LOYALTY_BALANCE_USD_IS_MORE_THAN(102, 0, "More than", "More than", "number", PURCHASE_LOYALTY_BALANCE_USD_IS,"double", null, ">",""),
			PURCHASE_LOYALTY_BALANCE_USD_IS_LESS_THAN(102, 0, "Less than", "Less than", "number", PURCHASE_LOYALTY_BALANCE_USD_IS,"double", null, "<",""),
			PURCHASE_LOYALTY_BALANCE_USD_IS_EQUAL_TO(102, 0, "Equal to", "Equal to", "number", PURCHASE_LOYALTY_BALANCE_USD_IS,"double", null, "=",""),
			PURCHASE_LOYALTY_BALANCE_USD_IS_BETWEEN(102, 0, "Between", "Between", "number", PURCHASE_LOYALTY_BALANCE_USD_IS,"double", "double", "between",""),
			PURCHASE_LOYALTY_BALANCE_USD_IS_NO_VALUE(102, 0, "Empty", "Empty", "number", PURCHASE_LOYALTY_BALANCE_USD_IS, null, null, "none","", " IS NULL OR loyalty.giftcard_balance=0"),*/	

				
		
			
	PURCHASE_LOYALTY_MEMBERSHIP(12, 1, "Loyalty Membership", "Loyalty Membership", "string", null, null, null, null, ""),
		PURCHASE_LOYALTY_MEMBERSHIP_IS(101, 0, "is", "is", "string", PURCHASE_LOYALTY_MEMBERSHIP, null, null, null, ""),
			PURCHASE_LOYALTY_MEMBERSHIP_IS_LOYALTY(203, 0, "Loyalty", "Loyalty", "string", PURCHASE_LOYALTY_MEMBERSHIP_IS, null, null, "equal", "loyalty.reward_flag", "L"),
			PURCHASE_LOYALTY_MEMBERSHIP_IS_GIFT(202, 0, "Gift", "Gift", "string", PURCHASE_LOYALTY_MEMBERSHIP_IS, null, null, "is", "loyalty.reward_flag", "G"),
			PURCHASE_LOYALTY_MEMBERSHIP_IS_BOTH(204, 0, "Both", "Both", "string", PURCHASE_LOYALTY_MEMBERSHIP_IS, null, null, "equals", "loyalty.reward_flag", "GL"),
			PURCHASE_LOYALTY_MEMBERSHIP_IS_ANY(201, 0, "Any one of", "Member", "string", PURCHASE_LOYALTY_MEMBERSHIP_IS, null, null, ">", "c.loyalty_customer", "0"),
			PURCHASE_LOYALTY_MEMBERSHIP_IS_NOT_MEMBER(205, 0, "Not a member", "not Member", "string", PURCHASE_LOYALTY_MEMBERSHIP_IS, null, null, "=", "c.loyalty_customer", "0 OR c.loyalty_customer IS NULL"),
		
			
			
			
	PURCHASE_LOYALTY_OPTED_IN_DATE(13, 1, "Loyalty Enrolled Date", "Loyalty Opt-in Date", "Date", null, null, null, null, "Date(loyalty.created_date)"),
		PURCHASE_LOYALTY_OPTED_IN_DATE_IS(101,0, "is", "is", "date", PURCHASE_LOYALTY_OPTED_IN_DATE, null, null, null, ""),
			PURCHASE_LOYALTY_OPTED_IN_DATE_TO_DAY(102,0, "Today", "Today", "date", PURCHASE_LOYALTY_OPTED_IN_DATE_IS, null, null, "isToday", ""),
			PURCHASE_LOYALTY_OPTED_IN_DATE_EQUAL_TO(103, 0,"Equal to","Equal to", "date", PURCHASE_LOYALTY_OPTED_IN_DATE_IS, "Date", null, "is", ""),
			PURCHASE_LOYALTY_OPTED_IN_DATE_BETWEEN(104,0, "Between", "Between", "date", PURCHASE_LOYALTY_OPTED_IN_DATE_IS, "Date", "Date", "between", ""),
			PURCHASE_LOYALTY_OPTED_IN_DATE_ON_OR_AFTER(105,0, "On or after", "On or after", "date", PURCHASE_LOYALTY_OPTED_IN_DATE_IS, "Date", null, "onOrAfter", ""),
			PURCHASE_LOYALTY_OPTED_IN_DATE_ON_OR_BEFORE(106,0, "On or before", "On or before", "date", PURCHASE_LOYALTY_OPTED_IN_DATE_IS, "Date", null, "onOrBefore", ""),
			PURCHASE_LOYALTY_OPTED_IN_DATE_AFTER(107,0, "After", "After", "date", PURCHASE_LOYALTY_OPTED_IN_DATE_IS, "Date", null, "after", ""),
			PURCHASE_LOYALTY_OPTED_IN_DATE_BEFORE(108,0, "Before", "Before", "date", PURCHASE_LOYALTY_OPTED_IN_DATE_IS, "Date", null, "before", ""),
			
			
			PURCHASE_LOYALTY_OPTED_IN_DATE_IS_IGNORE_YEAR(101,0, "is(in range)", "is(in range)", "date", PURCHASE_LOYALTY_OPTED_IN_DATE, null, null, null, ""),
			PURCHASE_LOYALTY_OPTED_IN_DATE_BEFORE_BETWEEN_DAYS(109,0, "Last Range Of Days", "Last Range Of Days", "date", PURCHASE_LOYALTY_OPTED_IN_DATE_IS_IGNORE_YEAR, "number", "number", "range_between", ""),
			//PURCHASE_LOYALTY_OPTED_IN_DATE_AFTER_BETWEEN_DAYS(110,0, "Next Range Of Days", "date", PURCHASE_LOYALTY_OPTED_IN_DATE_IS_IGNORE_YEAR, "number", "number", "after_between", ""),
			
			
			
		PURCHASE_LOYALTY_OPTED_IN_DATE_WITH_IN_LAST(201,0, "within last", "within last", "date", PURCHASE_LOYALTY_OPTED_IN_DATE, null,null, null, ""),
			PURCHASE_LOYALTY_OPTED_IN_DATE_DAYS(202,0, "days", "days", "date", PURCHASE_LOYALTY_OPTED_IN_DATE_WITH_IN_LAST, "31", "1", "withinlast_days", ""),
			PURCHASE_LOYALTY_OPTED_IN_DATE_WEEKS(203,0, "weeks", "weeks", "date", PURCHASE_LOYALTY_OPTED_IN_DATE_WITH_IN_LAST, "4", "7", "withinlast_weeks", ""),
			PURCHASE_LOYALTY_OPTED_IN_DATE_MONTHS(204,0, "months", "months", "date", PURCHASE_LOYALTY_OPTED_IN_DATE_WITH_IN_LAST, "12", "30", "withinlast_months", ""),

			
	PURCHASE_LOYALTY_OPT_IN_MEDIUM(14, 1, "Loyalty Opt-in Source", "Loyalty Opt-in Medium", "string", null, null, null, null, "loyalty.contact_loyalty_type"),
		PURCHASE_LOYALTY_OPT_IN_MEDIUM_IS(102,0,"is","is","String",PURCHASE_LOYALTY_OPT_IN_MEDIUM,null,null,null,""),
			PURCHASE_LOYALTY_OPT_IN_MEDIUM_IS_POS(201,0,"Store","POS", "String", PURCHASE_LOYALTY_OPT_IN_MEDIUM_IS, null,null,"is","","POS"),
			PURCHASE_LOYALTY_OPT_IN_MEDIUM_IS_OFFLINE(203,0, "Web-Form", "Web Form", "string", PURCHASE_LOYALTY_OPT_IN_MEDIUM_IS, null,  null, "equal", "", "WebForm" ),
				
	PURCHASE_LOYALTY_OPT_IN_STORE(14, 1, "Loyalty Opt-in Store", "Loyalty Opt-in Store", "string", null, null, null, null, "loyalty.pos_location_id"),
		PURCHASE_LOYALTY_OPT_IN_STORE_EQUALS(101, 0, "Equals", "Equals", "String", PURCHASE_LOYALTY_OPT_IN_STORE, "text", null, "is", ""),
		PURCHASE_LOYALTY_OPT_IN_STORE_DOES_NOT_EQUAL(3, 0, "Doesn't equal", "Doesn't equal", "String", PURCHASE_LOYALTY_OPT_IN_STORE, "text", null, "is not", ""),
		PURCHASE_LOYALTY_OPT_IN_STORE_ONE_OF(3, 0, "One of", "One of", "String", PURCHASE_LOYALTY_OPT_IN_STORE, "text", null, "is in", ""),
				
			
	PURCHASE_LOYALTY_MEMBERSHIP_STATUS(16, 1, "Membership Status", "Membership Status", "string", null, null, null, null, "loyalty.membership_status"),
		PURCHASE_LOYALTY_MEMBERSHIP_STATUS_IS(102,0,"is","is","String",PURCHASE_LOYALTY_MEMBERSHIP_STATUS,null,null,null,""),
			PURCHASE_LOYALTY_MEMBERSHIP_STATUS_IS_ACTIVE(201,0,"Active","Active", "String", PURCHASE_LOYALTY_MEMBERSHIP_STATUS_IS, null,null,"is","","Active"),
			PURCHASE_LOYALTY_MEMBERSHIP_STATUS_IS_EXPIRED(203,0, "Expired", "Expired", "string", PURCHASE_LOYALTY_MEMBERSHIP_STATUS_IS, null,  null, "equal", "", "Expired" ),
			PURCHASE_LOYALTY_MEMBERSHIP_STATUS_IS_SUSPENDED(203,0, "Suspended", "Suspended", "string", PURCHASE_LOYALTY_MEMBERSHIP_STATUS_IS, null,  null, "equals", "", "Suspended" ),

	PURCHASE_LOYALTY_TIER(17, 1, "Loyalty Tier", "Loyalty Tier", "Number", null, null, null, null, "loyalty.program_tier_id"),
		PURCHASE_LOYALTY_TIER_IS(101,0,"is","is","Number",PURCHASE_LOYALTY_TIER,null,null,"=",""),
		
	PURCHASE_LOYALTY_GIFT_BALANCE(16, 2, "Gift Amount", "Gift Amount", "Number", null, null, null, null, ""),
		PURCHASE_LOYALTY_GIFT_TOTAL_IS(101, 0, "Total Amount", "Total Amount", "Number", PURCHASE_LOYALTY_GIFT_BALANCE, null, null, null, "loyalty.total_gift_amount"),
			PURCHASE_LOYALTY_GIFT_TOTAL_IS_MORE_THAN(101, 0, "More than", "More than", "number", PURCHASE_LOYALTY_GIFT_TOTAL_IS,"double", null, ">",""),
			PURCHASE_LOYALTY_GIFT_TOTAL_IS_LESS_THAN(101, 0, "Less than", "Less than", "number", PURCHASE_LOYALTY_GIFT_TOTAL_IS,"double", null, "<",""),
			PURCHASE_LOYALTY_GIFT_TOTAL_IS_EQUAL_TO(101, 0, "Equal to", "Equal to", "number", PURCHASE_LOYALTY_GIFT_TOTAL_IS,"double", null, "=",""),
			PURCHASE_LOYALTY_GIFT_TOTAL_IS_BETWEEN(101, 0, "Between", "Between", "number", PURCHASE_LOYALTY_GIFT_TOTAL_IS,"double", "double", "between",""),
			PURCHASE_LOYALTY_GIFT_TOTAL_IS_NO_VALUE(101, 0, "Empty", "Empty", "number", PURCHASE_LOYALTY_GIFT_TOTAL_IS, null, null, "none","", " IS NULL OR loyalty.total_gift_amount=0"),
			
		PURCHASE_LOYALTY_GIFT_BALANCE_IS(102, 0, "Balance Amount", "Balance Amount", "Number", PURCHASE_LOYALTY_GIFT_BALANCE, null, null, null, "loyalty.gift_balance"),
			PURCHASE_LOYALTY_GIFT_BALANCE_IS_MORE_THAN(102, 0, "More than", "More than", "number", PURCHASE_LOYALTY_GIFT_BALANCE_IS,"double", null, ">",""),
			PURCHASE_LOYALTY_GIFT_BALANCE_IS_LESS_THAN(102, 0, "Less than", "Less than", "number", PURCHASE_LOYALTY_GIFT_BALANCE_IS,"double", null, "<",""),
			PURCHASE_LOYALTY_GIFT_BALANCE_IS_EQUAL_TO(102, 0, "Equal to", "Equal to", "number", PURCHASE_LOYALTY_GIFT_BALANCE_IS,"double", null, "=",""),
			PURCHASE_LOYALTY_GIFT_BALANCE_IS_BETWEEN(102, 0, "Between", "Between", "number", PURCHASE_LOYALTY_GIFT_BALANCE_IS,"double", "double", "between",""),
			PURCHASE_LOYALTY_GIFT_BALANCE_IS_NO_VALUE(102, 0, "Empty", "Empty", "number", PURCHASE_LOYALTY_GIFT_BALANCE_IS, null, null, "none","", " IS NULL OR loyalty.gift_balance=0"),	

		
		
		
			
		UDF_DATE(12, 0, "","","date", null, null, null, null, null),	
			UDF_DATE_IS(101, 0, "is", "is", "date", UDF_DATE, null, null, null, ""),
			UDF_DATE_TO_DAY(102,  0, "Today", "Today", "date", UDF_DATE_IS, null, null, "isToday", ""),
			UDF_DATE_EQUAL_TO(103,  0, "Equal to", "Equal to", "date", UDF_DATE_IS, "Date", null, "is", ""),
			UDF_DATE_BETWEEN(104,  0, "Between", "Between", "date", UDF_DATE_IS, "Date", "Date", "between", ""),
			UDF_DATE_ON_OR_AFTER(105, 0,  "On or after",  "On or after", "date", UDF_DATE_IS, "Date", null, "onOrAfter", ""),
			UDF_DATE_ON_OR_BEFORE(106, 0, "On or before", "On or before", "date", UDF_DATE_IS, "Date", null, "onOrBefore", ""),
			UDF_DATE_AFTER(107, 0, "After", "After", "date", UDF_DATE_IS, "Date", null, "after", ""),
			UDF_DATE_BEFORE(108, 0, "Before", "Before", "date", UDF_DATE_IS, "Date", null, "before", ""),
			UDF_DATE_NO_VALUE(108, 0, "Empty", "Empty", "date", UDF_DATE_IS, null, null, "none", "", " IS NULL"),
			
			UDF_DATE_IS_IGNORE_YEAR(101, 0, "is(year ignored)", "is(year ignored)", "date", UDF_DATE, null, null, null, ""),
			UDF_DATE_BEFORE_BETWEEN_DAYS(109,0, "Last Range Of Days", "Last Range Of Days", "date", UDF_DATE_IS_IGNORE_YEAR, "number", "number", "before_between", ""),
			UDF_DATE_AFTER_BETWEEN_DAYS(110,0, "Next Range Of Days", "Next Range Of Days", "date", UDF_DATE_IS_IGNORE_YEAR, "number", "number", "after_between", ""),
			
			
			
		UDF_DATE_WITH_IN_LAST(201, 0, "within last", "within last", "date", UDF_DATE, null,null, null, ""),
			UDF_DATE_DAYS(202, 0, "days", "days", "date", UDF_DATE_WITH_IN_LAST, "31", "1", "withinlast_days", ""),
			UDF_DATE_WEEKS(203, 0, "weeks", "weeks", "date", UDF_DATE_WITH_IN_LAST, "4", "7", "withinlast_weeks", ""),
			UDF_DATE_MONTHS(204, 0, "months", "months", "date", UDF_DATE_WITH_IN_LAST, "12", "30", "withinlast_months", ""),
				
		
			
		UDF_DATE_NOT_WITH_IN_LAST(301, 0, "not within last", "not within last","date", UDF_DATE,null,null, null,""),
		
			UDF_DATE_NOT_DAYS(302, 0, "days", "days", "date", UDF_DATE_NOT_WITH_IN_LAST, "31", "1", "notwithinlast_days", ""),
			UDF_DATE_NOT_WEEKS(303, 0, "weeks", "weeks", "date", UDF_DATE_NOT_WITH_IN_LAST, "4", "7", "notwithinlast_weeks", ""),
			UDF_DATE_NOT_MONTHS(304, 0, "months", "months", "date", UDF_DATE_NOT_WITH_IN_LAST, "12", "30", "notwithinlast_months", ""),
		
		UDF_DATE_IS_NOT(401, 0, "is not", "is not", "date", UDF_DATE, null,null, null, ""),
			UDF_DATE_NOT_AFTER(402, 0, "after", "after", "date", UDF_DATE_IS_NOT, "date", null, "before", ""),
			UDF_DATE_NOT_BRFORE(403, 0, "before", "before", "date", UDF_DATE_IS_NOT, "date", null, "after", ""),
	
		
		UDF_STRING(101,0, "","","string", null, null, null, null, null),
			UDF_STRING_EQUALS(102,0,"Equal to","Equal to", "String", UDF_STRING, "text",null,"is","" ),
			UDF_STRING_DOES_NOT_EQUAL(102,0,"Does not Equal to","Does not Equal to", "String", UDF_STRING, "text",null,"is not","" ),
			UDF_STRING_ONE_OF(102,0,"One of","One of", "String", UDF_STRING, "text",null,"is in","" ),
			UDF_STRING_CONTAINS(102,0,"Contains","Contains", "String", UDF_STRING, "text",null,"contains","" ),
			UDF_STRING_DOES_NOT_CONTAIN(102,0,"Does Not contain","Does Not contain", "String", UDF_STRING, "text",null,"does not contain","" ),
			UDF_STRING_STARTS_WITH(102,0,"Starts with","Starts with", "String", UDF_STRING, "text",null,"starts with","" ),
			UDF_STRING_ENDS_WITH(102,0,"Ends with","Ends with", "String", UDF_STRING, "text",null,"ends with","" ),
			UDF_STRING_NO_VALUE(102,0,"Is empty","Is empty", "String", UDF_STRING, null,null,"none","", " IS NULL" ),
			
			
			/*PROFILE_HOME_STORE(5, 2, "Home Store", "String", null, null, null, null, ""),
			PROFILE_HOME_STORE_EQUALS(101, 0, "Equals", "String", PROFILE_HOME_STORE, "text", null, "is", ""),
			PROFILE_HOME_STORE_DOES_NOT_EQUAL(3, 0, "Doesn't equal", "String", PROFILE_HOME_STORE, "text", null, "is not", ""),
			PROFILE_HOME_STORE_ONE_OF(3, 0, "One of", "String", PROFILE_HOME_STORE, "text", null, "is in", ""),
			*/
			
		UDF_NUMBER(102,0, "","", "number",null, null, null, null, null),
			
		UDF_NUMBER_EQUAL(102,0, "Equal to", "Equal to","number", UDF_NUMBER, "double", null, "=", ""),
			
		UDF_NUMBER_MORE_THAN(102,0, "Is more than", "Is more than","number", UDF_NUMBER, "double", null, ">", ""),
		UDF_NUMBER_LESS_THAN(102,0, "Is less than", "Is less than","number", UDF_NUMBER, "double", null, "<", ""),
		UDF_NUMBER_IN_THE_RANGE_OF(102,0, "is in the range of", "is in the range of","number", UDF_NUMBER, "double", "double", "between", ""),
		
		UDF_NUMBER_NOT_EQUAL(102,0, "not Equal to", "not Equal to","number", UDF_NUMBER, "double", null, "!=", ""),
		UDF_NUMBER_NO_VALUE(102,0, "Is empty", "Is empty","number", UDF_NUMBER, null, null, "none", "", " IS NULL"),
		
			
	
		//***************************************************************************************************
		//***************************INTERACTION ATTRIBUTES**************************************************
		
	/*INTERACTION_OPENS(101, 3, "Opens", "Opens", "Number", null, null, null, null ,"cs.opens" ),
		INTERACTION_OPENS_IS_MORE_THAN(201, 0, "Is more than", "Is more than", "number", INTERACTION_OPENS,"double", null, ">",""),
		
		
		
		
		
		INTERACTION_OPENS_IS_LESS_THEN(200, 0, "Is less than", "Is less than", "number", INTERACTION_OPENS,"double", null, "<",""),
		INTERACTION_OPENS_IS_IN_THE_RANGE(203, 0, "Is in the Range", "Is in the Range", "number", INTERACTION_OPENS,"double", "double", "between",""),	
		INTERACTION_OPENS_NO_OPENS(204, 0, "No Opens", "No Opens", "number", INTERACTION_OPENS, null, null, "=",""),
		
	INTERACTION_CLICKS(102, 3, "Clicks", "Clicks", "Number",null, null, null, null ,"cs.clicks"  ),
		INTERACTION_CLICKS_IS_MORE_THAN(201, 0, "Is more than", "Is more than", "number", INTERACTION_CLICKS,"double", null, ">",""),
		INTERACTION_CLICKS_IS_LESS_THEN(200, 0, "Is less than", "Is less than", "number", INTERACTION_CLICKS,"double", null, "<",""),
		INTERACTION_CLICKS_IS_IN_THE_RANGE(203, 0, "Is in the Range", "Is in the Range", "number", INTERACTION_CLICKS,"double", "double", "between",""),	
		INTERACTION_CLICKS_NO_CLICKS(204, 0, "No Clicks", "No Clicks", "number", INTERACTION_CLICKS, null, null, "=","")
		
	*/	
		INTERACTION_OPENS(101, 3, "Opens", "Opens", "Number", null, null, null, null ,null ),
		//INTERACTION_OPENS_IS_MORE_THAN(201, 0, "Is more than", "Is more than", "number", INTERACTION_OPENS,"double", null, ">",""),
		
		
		
		
		
	INTERACTION_OPENS_NO_OPENS(204, 0, "No Opens", "No Opens", "number", INTERACTION_OPENS, null, null, null, null),
		INTERACTION_OPENS_NO_OPENS_IN_ALL_OF(204, 0, "In All", "In All", "number", INTERACTION_OPENS_NO_OPENS, null, null, "=","SUM(IF(t.no_open_some >0, 1,0)) as total","0",  "SUM(t.no_open_some )", "SUM(IF(opens >0, 1,0)) as no_open_some"),
		
		INTERACTION_OPENS_IS_EXIST(200, 0, "Opened", "Opened", "number", INTERACTION_OPENS,	null, null, null, null),
			INTERACTION_OPENS_IS_EXIST_IN_ALL_OF(204, 0, "All", "All", "number", INTERACTION_OPENS_IS_EXIST, null, null, "=", "SUM(IF(t.open_all_some >0, 1,0)) as tot", "SUM(IF(t.open_all_some >0, 1,0))",  "count(t.email_id)", "SUM(IF(opens >0, 1,0)) AS open_all_some"),
			INTERACTION_OPENS_IS_EXIST_IN_ANY_OF(204, 0, "At least one", "At least one", "number", INTERACTION_OPENS_IS_EXIST, null, null, ">", "SUM(IF(t.open_one_some >0, 1,0)) as tot", "0", "SUM(IF(t.open_one_some >0, 1,0))", "SUM(IF(opens >0, 1,0)) as open_one_some"),
			
		/*INTERACTION_OPENS_IS_IN_THE_RANGE(203, 0, "Is in the Range", "Is in the Range", "number", INTERACTION_OPENS,null, null, null,""),	
			INTERACTION_OPENS_IS_IN_THE_RANGE_IN_ALL_OF(204, 0, "In All", "In all of", "number", INTERACTION_OPENS_IS_IN_THE_RANGE, "double", "double", "between", "", "" ),
			INTERACTION_OPENS_IS_IN_THE_RANGE_IN_ANY_OF(204, 0, "In At least one", "In At least one", "number", INTERACTION_OPENS_IS_IN_THE_RANGE, "double", "double", "range",""),
		*/	
		
		
	INTERACTION_CLICKS(102, 3, "Clicks", "Clicks", "Number",null, null, null, null ,null  ),
		INTERACTION_CLICKS_NO_CLICKS(204, 0, "No Clicks", "No Clicks", "number", INTERACTION_CLICKS, null, null, null, null),
			INTERACTION_CLICKS_NO_CLICKS_IN_ALL_OF(204, 0, "In All", "In All", "number", INTERACTION_CLICKS_NO_CLICKS, null, null, "=","SUM(IF(t.no_click_some >0, 1,0)) as total","0",  "SUM(t.no_click_some )", "SUM(IF(clicks >0, 1,0)) as no_click_some"),
		
		INTERACTION_CLICKS_IS_EXIST(200, 0, "Clicked", "Clicked", "number", INTERACTION_CLICKS, null, null, null, null),
			INTERACTION_CLICKS_IS_EXIST_IN_ALL_OF(204, 0, "All", "All", "number", INTERACTION_CLICKS_IS_EXIST, null, null,"=", "SUM(IF(t.click_all_some >0, 1,0)) as tot", "SUM(IF(t.click_all_some >0, 1,0))",  "count(t.email_id)", "SUM(IF(clicks >0, 1,0)) AS click_all_some"),
			INTERACTION_CLICKS_IS_EXIST_IN_ANY_OF(204, 0, "At least one", "At least one", "number", INTERACTION_CLICKS_IS_EXIST, null, null, ">", "SUM(IF(t.click_one_some >0, 1,0)) as tot", "0", "SUM(IF(t.click_one_some >0, 1,0))", "SUM(IF(clicks >0, 1,0)) as click_one_some"),
		
			INTERACTION_RECEIVED(102, 3, "Received", "Received", "string",null, null, null, null , null),
			INTERACTION_RECEIVED_IS_RECEIVED(201, 0, "Yes", "Yes", "string", INTERACTION_RECEIVED,null, null, "is", "status", "Success","t.status","status"),
			INTERACTION_RECEIVED_IS_NOT_RECEIVED(201, 0, "No", "No", "string", INTERACTION_RECEIVED,null, null, "is not", "not_sent", null),
	
		//*****************************************************************************************
		
		
	;
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
			
	
	public static HashSet<String> gethiddenSegments(){
		HashSet<String> hiddenSegmentsSet = new HashSet<String>();
		hiddenSegmentsSet.add("LOYALTY_TRANSACTIONS");
		hiddenSegmentsSet.add("LOYALTY_TRANSACTION_DATE");
		return hiddenSegmentsSet;
	}

	/*public HashSet<String> getHiddenSegmnetsSet() {
		return hiddenSegmentsSet;
	}

	public void setHiddenSegmnetsSet(HashSet<String> hiddenSegmnetsSet) {
		this.hiddenSegmentsSet.add("LOYALTY_TRANSACTIONS");
	}*/


	private int code;
	private int categoryCode;
	private boolean isVisible ;
	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}


	private String dispLabel;
	private String item;
	private String type;
	private SegmentEnum parentEnum;
	private String type1;
	private String type2;
	private String token;
	private String columnName;
	private String columnValue;
	private String selectFieldName;
	private String innerQryColumnName;
	
	public String getInnerQryColumnName() {
		return innerQryColumnName;
	}

	public void setInnerQryColumnName(String innerQryColumnName) {
		this.innerQryColumnName = innerQryColumnName;
	}

	public String getSelectFieldName() {
		return selectFieldName;
	}

	public void setSelectFieldName(String selectFieldName) {
		this.selectFieldName = selectFieldName;
	}


	private boolean isMappingField;
	private String posCustomFieldLbl;
	private String ignoreYearToken;
	

	public String getIgnoreYearToken() {
		return ignoreYearToken;
	}

	public void setIgnoreYearToken(String ignoreYearToken) {
		this.ignoreYearToken = ignoreYearToken;
	}

	private SegmentEnum(int code, int categoryCode, String dispLabel,String item , String type, SegmentEnum parentEnum, 
				String type1, String type2, String token, String columnName  ) {
		
		this(code, categoryCode, dispLabel,item, type, parentEnum, type1, type2, token, columnName ,null );

	}
	private SegmentEnum(int code, int categoryCode, String dispLabel,boolean isvisible, String item , String type, SegmentEnum parentEnum, 
			String type1, String type2, String token, String columnName  ) {
	
		this.code = code;
		this.categoryCode = categoryCode;
		this.dispLabel = dispLabel;
		this.type = type;
		this.parentEnum = parentEnum;
		this.type1 = type1;
		this.type2 = type2;
		this.token = token;
		this.columnName = columnName;
		this.item = item;
		this.isVisible = isvisible;
		

}
	private SegmentEnum(int code, int categoryCode, String dispLabel, String item, String type, SegmentEnum parentEnum, 
				String type1, String type2, String token, String columnName , String columnValue ) {
		this.code = code;
		this.categoryCode = categoryCode;
		this.dispLabel = dispLabel;
		this.type = type;
		this.parentEnum = parentEnum;
		this.type1 = type1;
		this.type2 = type2;
		this.token = token;
		this.columnName = columnName;
		this.columnValue = columnValue;
		this.item = item;
		
	}
	
	private SegmentEnum(int code, int categoryCode, String dispLabel, String item, boolean isMappingFiled, String posCustomFieldLbl, String type, SegmentEnum parentEnum, 
			String type1, String type2, String token, String columnName  ) {
		
		this(code, categoryCode, dispLabel, item, isMappingFiled, posCustomFieldLbl, type, parentEnum, type1, type2, token, columnName ,null );
	

	}
	
	
	
	private SegmentEnum(int code, int categoryCode, String dispLabel, String item, boolean isMappingFiled, String posCustomFieldLbl, String type, SegmentEnum parentEnum, 
			String type1, String type2, String token, String columnName , String columnValue ) {
	this.code = code;
	this.categoryCode = categoryCode;
	this.dispLabel = dispLabel;
	this.type = type;
	this.parentEnum = parentEnum;
	this.type1 = type1;
	this.type2 = type2;
	this.token = token;
	this.columnName = columnName;
	this.columnValue = columnValue;
	this.isMappingField = isMappingFiled;
	this.posCustomFieldLbl = posCustomFieldLbl;
	this.item = item;
	
	}
	//PURCHASE_TOTAL_PURCHASE_VALUE_AVG(7, 0, "Avg", "number", PURCHASE_TOTAL_PURCHASE_VALUE, null, null, null,"", "new_avg", "", ",avg((sal.sales_price*sal.quantity)+sal.tax) as new_avg"),
	private SegmentEnum(int code, int categoryCode, String dispLabel, String item, String type, SegmentEnum parentEnum, 
			String type1, String type2, String token, String columnName , String columnValue, String selectFieldName ) {
	this.code = code;
	this.categoryCode = categoryCode;
	this.dispLabel = dispLabel;
	this.type = type;
	this.parentEnum = parentEnum;
	this.type1 = type1;
	this.type2 = type2;
	this.token = token;
	this.columnName = columnName;
	this.columnValue = columnValue;
	this.selectFieldName = selectFieldName;
	this.item = item;
	
	}
	
	private SegmentEnum(int code, int categoryCode, String dispLabel, String item, String type, SegmentEnum parentEnum, 
			String type1, String type2, String token, String columnName , String columnValue, String selectFieldName, String innerQryColumnName ) {
	this.code = code;
	this.categoryCode = categoryCode;
	this.dispLabel = dispLabel;
	this.type = type;
	this.parentEnum = parentEnum;
	this.type1 = type1;
	this.type2 = type2;
	this.token = token;
	this.columnName = columnName;
	this.columnValue = columnValue;
	this.selectFieldName = selectFieldName;
	this.item = item;
	this.innerQryColumnName = innerQryColumnName;
	
	}
	
	public void setCode(int code) {
		this.code = code;
	}

	public void setCategoryCode(int categoryCode) {
		this.categoryCode = categoryCode;
	}

	public void setDispLabel(String dispLabel) {
		this.dispLabel = dispLabel;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setParentEnum(SegmentEnum parentEnum) {
		this.parentEnum = parentEnum;
	}

	public void setType1(String type1) {
		this.type1 = type1;
	}

	public void setType2(String type2) {
		this.type2 = type2;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}





	
	
	
	public String getColumnValue() {
		return columnValue;
	}

	public void setColumnValue(String columnValue) {
		this.columnValue = columnValue;
	}

	
	public int getCode() {
		return code;
	}

	public String getDispLabel() {
		return dispLabel;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}
	
	public String getType() {
		return type;
	}

	
		
	public SegmentEnum getParentEnum() {
		return parentEnum;
	}

	public String getType1() {
		return type1;
	}

	public String getType2() {
		return type2;
	}

	private SegmentEnum() {
		
	}
	
	public String getToken() {
		return token;
	}

	public String getColumnName() {
		return columnName;
	}

	public int getCategoryCode() {
		return categoryCode;
	}
	
	public String getPosCustomFieldLbl() {
		return posCustomFieldLbl;
	}

	public void setPosCustomFieldLbl(String posCustomFieldLbl) {
		this.posCustomFieldLbl = posCustomFieldLbl;
	}

	public boolean isMappingField() {
		return isMappingField;
	}
	
	public void setMappingField(boolean isMappingField) {
		this.isMappingField = isMappingField;
	}
	
	public List<SegmentEnum> getChidrenByParentEnum(SegmentEnum parentEnum) {
		List<SegmentEnum> childEnumList = new ArrayList<SegmentEnum>();
		SegmentEnum[] childEnum = SegmentEnum.values();
		for (SegmentEnum segmentEnum : childEnum) {
			//logger.debug("parentEnum "+parentEnum+" segmentEnum "+segmentEnum);
			
			if(segmentEnum.getParentEnum() == null) continue;
			
			if(segmentEnum.getParentEnum().name().equals(parentEnum.name())) {
				childEnumList.add(segmentEnum);
			}
			
		}
		
		return childEnumList;
		
		
		
	}
	
	
	public static List<SegmentEnum> getEnumsByCategoryCode(int categoryCode) {
		
		List<SegmentEnum> enumList = new ArrayList<SegmentEnum>();
		
		SegmentEnum[] childEnum = SegmentEnum.values();
		for (SegmentEnum segmentEnum : childEnum) {
			//logger.debug("parentEnum "+parentEnum+" segmentEnum "+segmentEnum);
			
			if(segmentEnum.getCategoryCode() == categoryCode) {
				
				enumList.add(segmentEnum);
				
			}//if
			
		}//for
		
		
		return enumList;
		
		
	}
	
	
	
	public  SegmentEnum createAndReturnNewSegmentEnum(int code, 
			int categoryCode, String dispLabel, String type, SegmentEnum parentEnum,
			String type1, String type2, String token, String columnName  )  {
		
		
		try {
			SegmentEnum newSegmentEnum = (SegmentEnum)this.clone(); 
			newSegmentEnum.setCategoryCode(2);
			
			
			
			
			
			return newSegmentEnum;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::", e);
			return null;
		}
		
	}
	
	
	
	public static SegmentEnum getUDFEnumByColumn(String customfieldname, int category) {
		
		
		
		
		SegmentEnum[] childEnum = SegmentEnum.values();
		for (SegmentEnum segmentEnum : childEnum) {
			//logger.debug("parentEnum "+parentEnum+" segmentEnum "+segmentEnum);
			
			if(segmentEnum.getCategoryCode() == category && segmentEnum.getColumnName().endsWith(customfieldname.toLowerCase())) {
				
				return segmentEnum;
				
			}//if
			
		}//for
		
		
		return null;
		
		
		
	}
	
	
	
	/*public static findSelectFieldNameByColName(String colName) {
		
		SegmentEnum[] childEnum = SegmentEnum.values();
		
		for (SegmentEnum segmentEnum : childEnum) {
			//logger.debug("parentEnum "+parentEnum+" segmentEnum "+segmentEnum);
			
			if(segmentEnum.getColumnName().equalsIgnoreCase(customfieldname) ) {
				
				return segmentEnum;
				
			}//if
			
		}//for
		
		
		return null;
		
		
	}*/
	
	
public static SegmentEnum getEnumByColumn(String customfieldname) {
		
		SegmentEnum[] childEnum = SegmentEnum.values();
		
		//logger.debug("ColName "+customfieldname);
		
		for (SegmentEnum segmentEnum : childEnum) {
			//logger.debug("parentEnum "+parentEnum+" segmentEnum "+segmentEnum);
			if(segmentEnum.getColumnName() == null || segmentEnum.getColumnName().isEmpty()) continue;
			if(segmentEnum.getColumnName().equalsIgnoreCase(customfieldname) ) {
				
				return segmentEnum;
				
			}//if
			
		}//for
		
		
		return null;
		
		
		
	}

public static SegmentEnum getEnumByItem(String item) {
	
	SegmentEnum[] childEnum = SegmentEnum.values();
	
	//logger.debug("item "+item);
	
	for (SegmentEnum segmentEnum : childEnum) {
		//logger.debug("parentEnum "+parentEnum+" segmentEnum "+segmentEnum);
		if(segmentEnum.getItem() == null || segmentEnum.getItem().isEmpty()) continue;
		if(segmentEnum.getItem().equalsIgnoreCase(item) ) {
			
			return segmentEnum;
			
		}//if
		
	}//for
	
	
	return null;
	
	
	
}
	
public static SegmentEnum getEnumByColumn(String columnName, String value) {
	
	SegmentEnum[] childEnum = SegmentEnum.values();
	
	//logger.debug("ColName "+columnName);
	
	for (SegmentEnum segmentEnum : childEnum) {
		//logger.debug("parentEnum "+parentEnum+" segmentEnum "+segmentEnum);
		if(segmentEnum.getColumnName() == null || segmentEnum.getColumnName().isEmpty()) continue;
		if(segmentEnum.getColumnName().equalsIgnoreCase(columnName)) {
			
			if(segmentEnum.getColumnValue() != null 
					&& !segmentEnum.getColumnValue().isEmpty() 
					&& segmentEnum.getColumnValue().equals(value)) {
				
				return segmentEnum;
				
			}//if
			
		}//if
		
	}//for
	
	
	return null;
	
	
	
}

	
	
	
} 

