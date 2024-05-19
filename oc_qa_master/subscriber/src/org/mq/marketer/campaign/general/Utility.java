package org.mq.marketer.campaign.general;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.catalina.Server;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.clapper.util.io.FileUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;
import org.mq.marketer.campaign.beans.ApplicationProperties;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.ContactParentalConsent;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.RetailProSalesCSV;
import org.mq.marketer.campaign.beans.SkuFile;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.components.PagingListModel;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.ContactParentalConsentDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.POSMappingDaoForDML;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.dao.UserDesignedCustomRowsDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.digitalReceipt.DRJsonRequest;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Div;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Include;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * validateEmail() unescape() getComponentById() condense() encodeUrl()
 * decodeUrl() getItemIndex() mergeImages() mergeCouponWithTemplate()
 * parseXMLString()
 */
@SuppressWarnings( { "unchecked"})
public class Utility {
	
	public static Map<String, String> genFieldContMap = new HashMap<String, String>();
	public static Map<String, String> deafultContMap = new HashMap<String, String>();
	public static Map<String, String> zipValidateMap = new HashMap<String, String>();
	public static Map<String, String> countryCurrencyMap = new HashMap<String, String>();
	public static Map<String, String> CDGAttrToSKUMap = new HashMap<String, String>();
	public static Map<String,String> specialRuleHashTag=new HashMap<>(); 
	public static Map<String, String> ItemsAnotherFactor = new HashMap<String, String>();
	public static Map<String, String> ItemsFeilds = new HashMap<String, String>();
	public static Map<String, String> limitQuantityMap = new HashMap<String, String>();
	public static Map<String, String> genesysEventMap = new HashMap<String, String>();	
	static{
		
		
		genesysEventMap.put("pointRedemtion", "genesysLtyRedemptionPage");
		genesysEventMap.put("CouponRedemption", "genesysCouponRedemptionPage");
		genesysEventMap.put("viewCustomer", "genesysCustomerProfilePage");
		
		genFieldContMap.put("Email" , "EmailId");
		genFieldContMap.put("First Name" , "FirstName");
		genFieldContMap.put("Last Name" , "LastName");
		genFieldContMap.put("Street" , "AddressOne");
		genFieldContMap.put("Address Two" , "AddressTwo");
		genFieldContMap.put("City" , "City");
		genFieldContMap.put("State" , "State");
		genFieldContMap.put("Country" , "Country");
		genFieldContMap.put("ZIP" , "Zip");
		genFieldContMap.put("Mobile" , "MobilePhone");
		genFieldContMap.put("Customer ID" , "ExternalId" );
		genFieldContMap.put("Addressunit ID" , "AddressunitId" );
		genFieldContMap.put("Gender" , "Gender");
		genFieldContMap.put("Home Store" , "HomeStore");
		genFieldContMap.put("BirthDay" , "BirthDay");
		genFieldContMap.put("Anniversary" , "Anniversary");
		genFieldContMap.put("Created Date" , "CreatedDate");
		genFieldContMap.put("Subsidiary Number" , "SubsidiaryNumber");
		
		deafultContMap.put("Email" , "Email");
		deafultContMap.put("FirstName" , "First Name");
		deafultContMap.put("LastName" , "Last Name");
		deafultContMap.put("Street" , "Street");
		deafultContMap.put("City" , "City");
		deafultContMap.put("State" , "State");
		deafultContMap.put("Country" , "Country");
		deafultContMap.put("ZIP" , "ZIP");
		deafultContMap.put("MobilePhone" , "Mobile");
		deafultContMap.put("CustomerID" , "Customer ID" );
		deafultContMap.put("Gender" , "Gender");
		deafultContMap.put("HomeStore" , "Home Store");
		deafultContMap.put("BirthDay" , "BirthDay");
		deafultContMap.put("Anniversary" , "Anniversary");
		deafultContMap.put("CreatedDate" , "Created Date");
		deafultContMap.put("Subsidiary Number" , "SubsidiaryNumber");
		
		
		zipValidateMap.put(Constants.SMS_COUNTRY_OMAN,"\\d{3}");
		zipValidateMap.put(Constants.SMS_COUNTRY_US,"^[0-9]{5}(?:-[0-9]{4})?$");
		zipValidateMap.put(Constants.SMS_COUNTRY_INDIA, "\\d{6}");
		zipValidateMap.put(Constants.SMS_COUNTRY_PAKISTAN, "\\d{5}");
	//	zipValidateMap.put("UAE", "");
		zipValidateMap.put(Constants.SMS_COUNTRY_CANADA, "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$");
		zipValidateMap.put(Constants.SMS_COUNTRY_KUWAIT, "\\d{5}");
		zipValidateMap.put(Constants.SMS_COUNTRY_PANAMA,"\\d{4}");
		zipValidateMap.put(Constants.SMS_COUNTRY_SA,"\\d{4}");//app-3802
		zipValidateMap.put(Constants.SMS_COUNTRY_SINGAPORE,"\\d{6}");//APP-4688
		zipValidateMap.put(Constants.SMS_COUNTRY_MYANMAR, "\\d{5}");
		zipValidateMap.put(Constants.SMS_COUNTRY_PHILIPPINES,"\\d{4}");
		
		countryCurrencyMap.put(Constants.SMS_COUNTRY_US,"$");
		countryCurrencyMap.put(Constants.SMS_COUNTRY_INDIA, "₹");
		countryCurrencyMap.put(Constants.SMS_COUNTRY_PAKISTAN, "₨");
		countryCurrencyMap.put(Constants.SMS_COUNTRY_CANADA, "$");
		countryCurrencyMap.put(Constants.SMS_COUNTRY_OMAN, "ر.ع.");
		countryCurrencyMap.put(Constants.SMS_COUNTRY_UAE, "د.إ");
		countryCurrencyMap.put(Constants.SMS_COUNTRY_PHILIPPINES, "₱");
		countryCurrencyMap.put(Constants.SMS_COUNTRY_MYANMAR, "$");//"K"
		countryCurrencyMap.put(Constants.SMS_COUNTRY_SA, "R"); //app-3802
		countryCurrencyMap.put(Constants.SMS_COUNTRY_SINGAPORE, "S$"); //APP-4688
		countryCurrencyMap.put(Constants.SMS_COUNTRY_PANAMA, "B/.");
		countryCurrencyMap.put(Constants.SMS_COUNTRY_KUWAIT, "د.ك");
		

		
		CDGAttrToSKUMap.put("Department", "department_code");
		CDGAttrToSKUMap.put("Vendor", "vendor_code");
		CDGAttrToSKUMap.put("Class", "class_code");
		CDGAttrToSKUMap.put("Subclass", "subclass_code");
		CDGAttrToSKUMap.put("SKU", "sku");
		CDGAttrToSKUMap.put("Item Category", "item_category");
		CDGAttrToSKUMap.put("DCS", "dcs");
		CDGAttrToSKUMap.put("ItemSID", "item_sid");
		CDGAttrToSKUMap.put("Description", "description");
		
		specialRuleHashTag.put("[#PurchasedItem#]", "items:<jsonAttribute>;=;<programId>;=;<val1>;=;<val2>;=;<val3>");
		specialRuleHashTag.put("[#ItemFactor#]", "<json>:<jsonAttribute>;=;<programId>;=;<val1>;=;<val2>;=;<val3>");
		specialRuleHashTag.put("[#Visit#]", "<json>:<jsonAttribute>;=;<programId>;=;<val1>;=;<val2>;=;<val3>");
		specialRuleHashTag.put("[#PuchaseDate#]", "header:requestDate;=;<programId>;=;<val1>;=;<val2>;=;<val3>");
		specialRuleHashTag.put("[#PurchaseStore#]", "header:storeNumber;=;<programId>;=;<val1>;=;<val2>;=;<val3>");
		specialRuleHashTag.put("[#PurchaseTier#]", "<json>:<jsonAttribute>;=;<programId>;=;<val1>;=;<val2>;=;<val3>");
		specialRuleHashTag.put("[#PurchaseCardSet#]", "<json>:<jsonAttribute>;=;<programId>;=;<val1>;=;<val2>;=;<val3>");
		specialRuleHashTag.put("[#Minimum Receipt Total#]","header:receiptAmount;=;<programId>;=;<val1>;=;<val2>;=;<val3>");
		
        ItemsAnotherFactor.put("vendorCode", "vendorName");
        ItemsAnotherFactor.put("departmentCode", "departmentName");
        ItemsAnotherFactor.put("itemClass", "itemClassName");
        ItemsAnotherFactor.put("itemSubClass", "itemSubClassName");
        
        ItemsFeilds.put("vendorCode", "vendorCode");
        ItemsFeilds.put("departmentCode", "departmentCode");
        ItemsFeilds.put("itemClass", "classCode");
        ItemsFeilds.put("itemSubClass", "subClassCode");
        ItemsFeilds.put("DCS", "DCS");
        ItemsFeilds.put("skuNumber", "sku");
        ItemsFeilds.put("itemCategory", "itemCategory");
        
   
		/*
        limitQuantityMap.put("LTE","Maximum");
        limitQuantityMap.put("GTE", "Minimum");
        limitQuantityMap.put("ET", "Equals");
        limitQuantityMap.put("M", "Multiple");*/

        limitQuantityMap.put("LTE","Maximum");
        limitQuantityMap.put("up to","up to");
        limitQuantityMap.put("GTE", "Minimum");
        limitQuantityMap.put("ET", "Equals");
        limitQuantityMap.put("M", "Multiple Of");
        
        
	}
	
	

	
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	
	
	public static String getStringDateFormat(Date date){
		if(date==null) return "--";
		DateFormat format = new SimpleDateFormat("dd MMM, yyyy HH:mm aaa"); 
		return format.format(date);
	}
	
	/**
	 * Validates the given string, allows space in between.
	 * 
	 * @param name
	 * @returns - boolean : Returns true if the given string doesn't 
	 * contains any special characters else false.
	 * If the given string is null, returns false
	 */
	public static boolean validateName(String name){
		if (name == null)
			return false;
		name = condense(name);
		
		if(name.length() == 0) return false;
		
		String pattern = PropertyUtil.getPropertyValue("namePattern").trim();
		Pattern p = Pattern.compile(pattern);
		
		Matcher m = p.matcher(name);
		
		return m.matches();
	}
	
	public static boolean validateDefaultValue(String name){
		if (name == null)
			return false;
		name = condense(name);
		
		if(name.length() == 0) return false;
		
		String pattern = PropertyUtil.getPropertyValue("defaultValuePattern").trim();
		Pattern p = Pattern.compile(pattern);
		
		Matcher m = p.matcher(name);
		
		return m.matches();
	}
	
	
	
	/**
	 * Validates the given string, allows space in between.
	 * 
	 * @param name
	 * @returns - boolean : Returns true if the given string doesn't 
	 * contains any special characters else false.
	 * If the given string is null, returns false
	 */
	public static boolean validateSMSHeader(String name){
		if (name == null)
			return false;
		name = condense(name);
		
		if(name.length() == 0) return false;
		
		String pattern = PropertyUtil.getPropertyValue("smsHeaderPattern").trim();
		Pattern p = Pattern.compile(pattern);
		
		Matcher m = p.matcher(name);
		
		return m.matches();
	}
	public static boolean isModifiedContact(Contacts inputContact, Contacts dbContact) {
		
		if(	inputContact!=null && dbContact!=null)
		{
			if( (inputContact.getFirstName()!=null && !inputContact.getFirstName().isEmpty() && (dbContact.getFirstName() ==null || !dbContact.getFirstName().equalsIgnoreCase(inputContact.getFirstName())))||
					(inputContact.getLastName()!=null && !inputContact.getLastName().isEmpty() && (dbContact.getLastName() ==null || !dbContact.getLastName().equalsIgnoreCase(inputContact.getLastName())))||
					(inputContact.getMobilePhone()!=null && !inputContact.getMobilePhone().isEmpty() && (dbContact.getMobilePhone() ==null ||!dbContact.getMobilePhone().equalsIgnoreCase(inputContact.getMobilePhone())))||
					(inputContact.getEmailId()!=null && !inputContact.getEmailId().isEmpty() && (dbContact.getEmailId() ==null || !dbContact.getEmailId().equalsIgnoreCase(inputContact.getEmailId())))||
					(inputContact.getAddressOne()!=null && !inputContact.getAddressOne().isEmpty() && (dbContact.getAddressOne() ==null || !dbContact.getAddressOne().equalsIgnoreCase(inputContact.getAddressOne())))||
					(inputContact.getAddressTwo()!=null && !inputContact.getAddressTwo().isEmpty() && (dbContact.getAddressTwo() ==null || !dbContact.getAddressTwo().equalsIgnoreCase(inputContact.getAddressTwo())))||
					(inputContact.getCity()!=null && !inputContact.getCity().isEmpty() && (dbContact.getCity() ==null || !dbContact.getCity().equalsIgnoreCase(inputContact.getCity())))||
					(inputContact.getZip()!=null && !inputContact.getZip().isEmpty() && (dbContact.getZip() ==null || !dbContact.getZip().equalsIgnoreCase(inputContact.getZip())))||
					(inputContact.getCountry()!=null && !inputContact.getCountry().isEmpty() && (dbContact.getCountry() ==null || !dbContact.getCountry().equalsIgnoreCase(inputContact.getCountry())))||
					(inputContact.getState()!=null && !inputContact.getState().isEmpty() && (dbContact.getState() ==null || !dbContact.getState().equalsIgnoreCase(inputContact.getState())))||
					(inputContact.getHomeStore()!=null && !inputContact.getHomeStore().isEmpty() && (dbContact.getHomeStore() ==null || !dbContact.getHomeStore().equalsIgnoreCase(inputContact.getHomeStore())))||
					(inputContact.getGender()!=null && !inputContact.getGender().isEmpty() && (dbContact.getGender() ==null || !dbContact.getGender().equalsIgnoreCase(inputContact.getGender())))||
					//(inputContact.getBirthDay()!=null  && (dbContact.getBirthDay() ==null || !dbContact.getBirthDay().equals(inputContact.getBirthDay())))||
					//(inputContact.getAnniversary()!=null  && (dbContact.getAnniversary() ==null || !dbContact.getAnniversary().equals(inputContact.getAnniversary())))||
					//(inputContact.getCreatedDate()!=null  && (dbContact.getCreatedDate() ==null || !dbContact.getCreatedDate().equals(inputContact.getCreatedDate())))||
					(inputContact.getBirthDay()!=null && (dbContact.getBirthDay() ==null || !(MyCalendar.calendarToString(dbContact.getBirthDay(), MyCalendar.FORMAT_DATETIME_STYEAR).equals(MyCalendar.calendarToString(inputContact.getBirthDay(), MyCalendar.FORMAT_DATETIME_STYEAR)))))||
					(inputContact.getAnniversary()!=null && (dbContact.getAnniversary() ==null || !(MyCalendar.calendarToString(dbContact.getAnniversary(), MyCalendar.FORMAT_DATETIME_STYEAR).equals(MyCalendar.calendarToString(inputContact.getAnniversary(), MyCalendar.FORMAT_DATETIME_STYEAR)))))||
					(inputContact.getCreatedDate()!=null && (dbContact.getCreatedDate() ==null || !(MyCalendar.calendarToString(dbContact.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR).equals(MyCalendar.calendarToString(inputContact.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR)))))||
					(inputContact.getUdf1()!=null && !inputContact.getUdf1().isEmpty() && (dbContact.getUdf1() ==null || !dbContact.getUdf1().equalsIgnoreCase(inputContact.getUdf1())))||
					(inputContact.getUdf2()!=null && !inputContact.getUdf2().isEmpty() && (dbContact.getUdf2() ==null || !dbContact.getUdf2().equalsIgnoreCase(inputContact.getUdf2())))||
					(inputContact.getUdf3()!=null && !inputContact.getUdf3().isEmpty() && (dbContact.getUdf3() ==null || !dbContact.getUdf3().equalsIgnoreCase(inputContact.getUdf3())))||
					(inputContact.getUdf4()!=null && !inputContact.getUdf4().isEmpty() && (dbContact.getUdf4() ==null || !dbContact.getUdf4().equalsIgnoreCase(inputContact.getUdf4())))||
					(inputContact.getUdf5()!=null && !inputContact.getUdf5().isEmpty() && (dbContact.getUdf5() ==null || !dbContact.getUdf5().equalsIgnoreCase(inputContact.getUdf5())))||
					(inputContact.getUdf6()!=null && !inputContact.getUdf6().isEmpty() && (dbContact.getUdf6() ==null || !dbContact.getUdf6().equalsIgnoreCase(inputContact.getUdf6())))||
					(inputContact.getUdf7()!=null && !inputContact.getUdf7().isEmpty() && (dbContact.getUdf7() ==null || !dbContact.getUdf7().equalsIgnoreCase(inputContact.getUdf7())))||
					(inputContact.getUdf8()!=null && !inputContact.getUdf8().isEmpty() && (dbContact.getUdf8() ==null || !dbContact.getUdf8().equalsIgnoreCase(inputContact.getUdf8())))||
					(inputContact.getUdf9()!=null && !inputContact.getUdf9().isEmpty() && (dbContact.getUdf9() ==null || !dbContact.getUdf9().equalsIgnoreCase(inputContact.getUdf9())))||
					(inputContact.getUdf10()!=null && !inputContact.getUdf10().isEmpty() && (dbContact.getUdf10() ==null || !dbContact.getUdf10().equalsIgnoreCase(inputContact.getUdf10())))||
					(inputContact.getUdf11()!=null && !inputContact.getUdf11().isEmpty() && (dbContact.getUdf11() ==null || !dbContact.getUdf12().equalsIgnoreCase(inputContact.getUdf11())))||
					(inputContact.getUdf12()!=null && !inputContact.getUdf12().isEmpty() && (dbContact.getUdf12() ==null || !dbContact.getUdf12().equalsIgnoreCase(inputContact.getUdf12())))||
					(inputContact.getUdf13()!=null && !inputContact.getUdf13().isEmpty() && (dbContact.getUdf13() ==null || !dbContact.getUdf13().equalsIgnoreCase(inputContact.getUdf13())))||
					(inputContact.getUdf14()!=null && !inputContact.getUdf14().isEmpty() && (dbContact.getUdf14() ==null || !dbContact.getUdf14().equalsIgnoreCase(inputContact.getUdf14())))||
					(inputContact.getUdf15()!=null && !inputContact.getUdf15().isEmpty() && (dbContact.getUdf15() ==null || !dbContact.getUdf15().equalsIgnoreCase(inputContact.getUdf15()))))
					
					
					
					
				/*||	inputContact.getPhone()!=dbContact.getPhone() || inputContact.getEmailId()!=dbContact.getEmailId() || inputContact.getAddressOne()!=dbContact.getAddressOne() 
				|| inputContact.getAddressTwo()!=dbContact.getAddressTwo() || inputContact.getCity()!=dbContact.getCity() || inputContact.getState()!=dbContact.getState()
				|| inputContact.getPin()!=dbContact.getPin() || inputContact.getCountry()!=dbContact.getCountry() || inputContact.getBirthDay()!=dbContact.getBirthDay()
				|| inputContact.getAnniversary()!=dbContact.getAnniversary() || inputContact.getGender()!=dbContact.getGender() || inputContact.getZip()!=dbContact.getZip()
		        || inputContact.getHomeStore()!=dbContact.getHomeStore() || inputContact.getCreatedDate()!=dbContact.getCreatedDate() || inputContact.getUdf1()!=dbContact.getUdf1()
		        || inputContact.getUdf2()!=dbContact.getUdf2() || inputContact.getUdf3()!=dbContact.getUdf3() || inputContact.getUdf4()!=dbContact.getUdf4()
		        || inputContact.getUdf5()!=dbContact.getUdf5() || inputContact.getUdf6()!=dbContact.getUdf6() || inputContact.getUdf7()!=dbContact.getUdf7()
		        || inputContact.getUdf8()!=dbContact.getUdf8() || inputContact.getUdf9()!=dbContact.getUdf9() || inputContact.getUdf10()!=dbContact.getUdf10()
		        || inputContact.getUdf11()!=dbContact.getUdf11() || inputContact.getUdf12()!=dbContact.getUdf12() || inputContact.getUdf13()!=dbContact.getUdf13()
		        || inputContact.getUdf14()!=dbContact.getUdf14() || inputContact.getUdf15()!=dbContact.getUdf15())
			*/{
			return true;
			}
			else {
			return false;
			}
		}
		return false;
		}  
	
	/**
	 * Validates the given string, allows space,'.' and " ' " in between.
	 * 
	 * @param name
	 * @returns - boolean : Returns true if the given string doesn't 
	 * contains any special characters else false.
	 * If the given string is null, returns false
	 */
	public static boolean validateNames(String name){
		if (name == null)
			return false;
		name = condense(name);
		
		if(name.length() == 0) return false;
		
		String pattern = PropertyUtil.getPropertyValue("namePatternNew").trim();
		Pattern p = Pattern.compile(pattern);
		
		Matcher m = p.matcher(name);
		
		return m.matches();
	}
	
	
	public static boolean validateBy(String pattern, String sourceStr) {
		
		logger.debug(pattern  +" is pattern and keword is " +sourceStr);
		if (sourceStr == null)
			return false;
		sourceStr = condense(sourceStr);
		
		if(sourceStr.length() == 0) return false;
		
		Pattern p = Pattern.compile(pattern);
		
		Matcher m = p.matcher(sourceStr);
		
		return m.matches();
		
		
	}
	public static boolean validateFromName(String name){
		if (name == null)
			return false;
		name = condense(name);
		
		if(name.length() == 0) return false;
		
		String pattern = PropertyUtil.getPropertyValue("nameFromNamePattern").trim();
		Pattern p = Pattern.compile(pattern);
		
		Matcher m = p.matcher(name);
		
		return m.matches();
	}
	
	
	
	public static boolean validateUserName(String name){
		if (name == null)
			return false;
		name = condense(name);
		
		if(name.length() == 0) return false;
		
		String pattern = PropertyUtil.getPropertyValue("userNamePattern").trim();
		Pattern p = Pattern.compile(pattern);
		
		Matcher m = p.matcher(name);
		
		return m.matches();
	}
	
	
	
	/**
	 * Validates the given string, doesn't allow any spaces in between. <BR/>
	 * String must starts with an alphabet.
	 * @param name
	 * @returns - boolean : Returns true if the given string doesn't contains
	 *  any special characters and spaces else false.
	 * If the given string is null, returns false
	 */
	public static boolean validateVariableName(String name){
		if (name == null)
			return false;
		name = condense(name);

		if(name.length() == 0) return false;
		String pattern = PropertyUtil.getPropertyValue("variablePattern").trim();
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(name);
		return m.matches();
	}
	
	public static boolean validateEmail(String email) {
		// Pattern p = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
		if (email == null || email.trim().length()==0) {
			return false;
		}
		String pattern = PropertyUtil.getPropertyValue("emailPattern").trim();
		//email = email.trim();
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(email);
		return m.matches();
	}
	

	public static boolean validateWebFormEmail(String email) {
		// Pattern p = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
		
		if (email == null || email.trim().length()==0) {
			return false;
		}
			email = email.trim();
			String pattern = PropertyUtil.getPropertyValue("emailPattern").trim();
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(email);
			return m.matches();
		
	}
	
	
//  ****	 validate URl   ****
	 public static boolean isURL(String url)
	 {
	  if(url==null)
	  {
	   return false;
	  }
	  //Assigning the url format regular expression
	  String urlPattern="^[a-zA-Z0-9_/\\-\\.\\:]+\\.([A-Za-z/]{2,5})[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*";
	  return url.matches(urlPattern); 
	  
	 } // isURL(String url)

	 //(http(s?):)([/|.|\w|\s|-])*\.(?:jpg|gif|png)
	// ^https?://(?:[a-z0-9\-]+\.)+[a-z]{2,6}(?:/[^/#?]+)+\.(?:jpg|gif|png)$
	 
	 public static boolean isImageURL(String url)
	 {
		 if(url==null){
			 return false;
		 }
		 String regex = "(http(s?):)([/|.|\\w|\\s|-])*\\.(?:jpg|gif|png|jpeg)";
		 return url.matches(regex); 
	 }
	 
	 
	 public static boolean isLatitude(String lat)
	 {
		 if(lat==null){
			 return false;
		 }
		 String regex = "^(\\+|-)?(?:90(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-8][0-9])(?:(?:\\.[0-9]{1,6})?))$";
		 return lat.matches(regex); 
	 }
	 public static boolean isLongitude(String lon)
	 {
		 if(lon==null){
			 return false;
		 }
		 String regex = "^(\\+|-)?(?:180(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\\.[0-9]{1,6})?))$";;
		 return lon.matches(regex); 
	 }

	public static long validateHtmlSize(String htmlStuff) {
		
		logger.debug("--Just Entered--");
		if(htmlStuff==null) {
			logger.warn("htmlStuff is null");
			return 0;
		}

		int htmlMaxInKb = 100;
		
		try {
			htmlMaxInKb = Integer.parseInt(PropertyUtil.getPropertyValue("htmlMaxSize").trim());
		} catch (Exception e1) {
			htmlMaxInKb = 100;
			logger.debug("** Exception :"+e1);
		}
		
		try{
			long kbCount = htmlStuff.getBytes("US-ASCII").length/1024;
			logger.debug("File size in kb : "+kbCount);
			return kbCount;
		} catch(Exception e) {
			logger.error("** Exception : Error while getting HTML stuff byte value :" + e + "**");
			return 0;
		} 	
		
	}
	
	public static boolean validateUploadFilName(String fileName) throws Exception {
		
		if(fileName == null && fileName.length() == 0) {
			logger.debug("upload filename is null ..");
			return false;
		}
		String pattern = PropertyUtil.getPropertyValue("uploadFileNamePattern").trim();
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(fileName);
		logger.info("=====>"+fileName);
		
		return m.matches();
	}
	//Changes 2.5.2.13 Start
public static boolean validateUploadFilNameWithoutExtension(String fileName) throws Exception {
		
		if(fileName == null && fileName.length() == 0) {
			logger.debug("upload filename is null ..");
			return false;
		}
		String pattern = PropertyUtil.getPropertyValue("uploadFileNamePatternWithOutExtentsion").trim();
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(fileName);
		logger.info("=====>"+fileName);
		
		return m.matches();
		
	}
//Changes 2.5.2.13 End 

	//added for multi user acc
	
	public static String getOnlyUserName(String userFullName) {
		
		if (userFullName == null || userFullName.indexOf("__org__")==-1) return userFullName;
		
		try {
			userFullName = userFullName.substring(0, userFullName.lastIndexOf("__org__"));
		}
		catch(Exception e) {
			logger.error("** Exception : ",e);
		}
		return userFullName;
		
		
	}//end block
	
	public static String getOnlyOrgId(String userFullName) {
		if (userFullName == null || userFullName.indexOf("__org__")==-1) return userFullName;
		try{
			userFullName = userFullName.substring(userFullName.lastIndexOf("__org__")+7);
		}
		catch(Exception e) {
			logger.error("** Exception : ",e);
		}
		return userFullName;
		
		
	}//end block
	
	
	public static String unescape(String s) {
		StringBuffer sbuf = new StringBuffer();
		try {
		int l = s.length();
		int ch = -1;
		int b, sumb = 0;
		for (int i = 0, more = -1; i < l; i++) {
			/* Get next byte b from URL segment s */
			switch (ch = s.charAt(i)) {
			case '%':
				ch = s.charAt(++i);
				int hb = (Character.isDigit((char) ch) ? ch - '0'
						: 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
				ch = s.charAt(++i);
				int lb = (Character.isDigit((char) ch) ? ch - '0'
						: 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
				b = (hb << 4) | lb;
				break;
			case '+':
				b = ' ';
				break;
			default:
				b = ch;
			}
			/* Decode byte b as UTF-8, sumb collects incomplete chars */
			if ((b & 0xc0) == 0x80) { // 10xxxxxx (continuation byte)
				sumb = (sumb << 6) | (b & 0x3f); // Add 6 bits to sumb
				if (--more == 0)
					sbuf.append((char) sumb); // Add char to sbuf
			} else if ((b & 0x80) == 0x00) { // 0xxxxxxx (yields 7 bits)
				sbuf.append((char) b); // Store in sbuf
			} else if ((b & 0xe0) == 0xc0) { // 110xxxxx (yields 5 bits)
				sumb = b & 0x1f;
				more = 1; // Expect 1 more byte
			} else if ((b & 0xf0) == 0xe0) { // 1110xxxx (yields 4 bits)
				sumb = b & 0x0f;
				more = 2; // Expect 2 more bytes
			} else if ((b & 0xf8) == 0xf0) { // 11110xxx (yields 3 bits)
				sumb = b & 0x07;
				more = 3; // Expect 3 more bytes
			} else if ((b & 0xfc) == 0xf8) { // 111110xx (yields 2 bits)
				sumb = b & 0x03;
				more = 4; // Expect 4 more bytes
			} else /* if ((b & 0xfe) == 0xfc) */{ // 1111110x (yields 1 bit)
				sumb = b & 0x01;
				more = 5; // Expect 5 more bytes
			}
			/* We don't test if the UTF-8 encoding is well-formed */
		}
		} catch (Exception e) {
			logger.error(" ** Exception : Unescape failed for string: "+s);
			logger.error(" ** Exception : "+e.getMessage());
			return s;
		}
		return sbuf.toString();
		
	}

	public static Component getComponentById(String id) {
		Component comp = null;
		Desktop a = Executions.getCurrent().getDesktop();
		Iterator<Component> iter = a.getComponents().iterator();
		while (iter.hasNext()) {
			comp = (Component) iter.next();
			if (comp.getId().equals(id)) {
				// Logger.getRootLogger().info("--getComponentById-- got
				// Component");
				return comp;
			}
		}
		return comp;
	}

	/**
	 * Collapse multiple spaces in string down to a single space. Remove lead
	 * and trailing spaces.
	 * 
	 * @param s
	 *            String to strip of blanks.
	 * @return String with all blanks, lead/trail/embedded removed.
	 */
	public static String condense(String s) {
		if(s==null)
			return s;
		
		s = s.trim();
		if (s.indexOf("  ") < 0) {
			return s;
		}
		int len = s.length();
		StringBuffer b = new StringBuffer(len - 1);
		boolean suppressSpaces = false;
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if (c == ' ') {
				if (!suppressSpaces) {
					b.append(c);
					suppressSpaces = true;
				}
			} else {
				b.append(c);
				suppressSpaces = false;
			}
		}// end for
		return b.toString();
	}// end condense

	public static String encodeUrl(String url) throws EncoderException,
			DecoderException {
		URLCodec encoder = new URLCodec();
		String result = encoder.encode(url);
		// System.err.println("URL Encoding result: " + result);
		return result;
	}

	/**
	 * Replaces space with '%20' 
	 * @param urlStr - Url string with spaces
	 * @return Returns converted String
	 */
	public static String encodeSpace(String urlStr) {
		return StringUtils.replace(urlStr, " ", "%20");
	}
	
	public static String decodeUrl(String url) throws EncoderException,
			DecoderException {
		URLCodec encoder = new URLCodec();
		String result = encoder.decode(url);
		//System.err.println("URL Encoding result: " + result);
		return result;
	}

	public static int getItemIndex(Listbox lb, String s) {
		if (lb != null) {
			List list = lb.getItems();
			for (Object obj : list) {
				Listitem li = (Listitem) obj;
				if (li.getLabel().equalsIgnoreCase(s)) {
					return lb.getIndexOfItem((Listitem) obj);
				}
			}
		}
		return -1;
	}

	public static File mergeImages(File imageOne, File imageTwo, int x, int y,
			String destinPath) throws Exception {

		logger.debug("just entered");
		logger.info("co-ordinates: x-" + x + ", y- " + y);
		logger.info("destination path: " + destinPath);
		File outImage = null;
		String ext = FileUtil.getFileNameExtension(imageOne);
		String destExt = FileUtil.getFileNameExtension(destinPath);
		if (!ext.equalsIgnoreCase(destExt)) {
			String path = FileUtil.getFileNameNoExtension(destinPath);
			destinPath = path + "." + ext;
		}
		BufferedImage input = ImageIO.read(imageOne);
		BufferedImage input1 = ImageIO.read(imageTwo);
		int h2 = input1.getHeight();
		int w2 = input1.getWidth();
		int starty = y;
		int startx = x;

		for (int j = starty; j < starty + h2; j++) {
			for (int i = startx; i < startx + w2; i++) {
				int pixColor = input1.getRGB(i - startx, j - starty);
				input.setRGB(i, j, pixColor);
			}
		}
		logger.info("Positioning completed");
		outImage = new File(destinPath);
		ImageIO.write(input, ext, outImage);
		logger.info(" images merged successfully");

		logger.debug("Exiting");
		return outImage;

	}

	public static String mergeCouponWithTemplate(String path,
			String htmlTextOriginal) {

		try {
			logger.debug(" just entered ");
			String replString = "<div name=\"RMCouponDiv\"><img src=\"" + path
					+ "\"/></div>";
			String HtmlCouponDivId = PropertyUtil
					.getPropertyValue("HtmlCouponDivId");
			logger.info("HtmlCouponDivId is" + HtmlCouponDivId);
			// logger.debug("-- mergeCouponWithTemplate --");
			htmlTextOriginal = htmlTextOriginal.replaceAll(HtmlCouponDivId,
					replString);
			logger.debug("Exiting");
			return htmlTextOriginal;

		} catch (Exception e) {
			logger.error("** Exception mergeCouponWithTemplate is "
					+ e.getMessage() + " **");
			logger.info("Exception in mergeCouponWithTemplate  :" + e);
			return htmlTextOriginal;
		}
	}

	public static boolean checkSMTPResponse(String host) throws Exception {
		Socket s = new Socket(host, 25);

		InputStream is = s.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String response = br.readLine();
		logger.info(response);
		boolean result = response.startsWith("220");
		if (!result == (true)) {
			logger.error("***SMTP SERVER is not responding***");
			return false;
		}
		logger.info("SMTP is responding");
		return true;
	}
	
	public static long DateDiff(Date from, Date to){
		long diff = from.getTime() - to.getTime();
		return (diff / (1000 * 60 * 60 * 24)) +1;
	}

	public static String getPercentage(Long value,Long sent,int index) {
		
		if(value==null || sent==null)
			return "-";
		else return getPercentage(value.intValue(),sent.longValue(), index);
	}
	
	
	public static String getPercentage(Double value,Double sent,int index) {
		
		if(value==null || sent==null) {
			
			return "-";
		}
		else {
			try {
				if(sent==0)
					return "0";
				
				NumberFormat nf = NumberFormat.getNumberInstance();
			    nf.setMaximumFractionDigits(index);
			    nf.setRoundingMode (RoundingMode.HALF_DOWN);
				return nf.format((value*100.0)/sent);
			} catch (Exception e) {
				logger.error("Exception :  " + e);
				return "0";
			}
		}
	}
	
	/**
	 * 
	 * @param value part specifies the result.
	 * @param sent total specifies on which field we need to calcu;ate the percentage.
	 * @param index specifies how mant digits we need to consider after the decimal point.
	 * @return
	 */
	public static String getPercentage(int value,long sent,int index){
		try {
			if(sent==0)
				return "0";
			
			NumberFormat nf = NumberFormat.getNumberInstance();
		    nf.setMaximumFractionDigits(index);
		    nf.setRoundingMode (RoundingMode.HALF_DOWN);
			return nf.format((value*100.0)/sent);
		} catch (Exception e) {
			logger.error("Exception :  " + e);
			return "0";
		}
	}
	
	public static Include getXcontents(){
		Include xcontents = null;
		xcontents = (Include)Sessions.getCurrent().getAttribute("xcontents");
		if(xcontents==null)
			xcontents = (Include)getComponentById("xcontents");
		return xcontents;
	}
	
	public static Object makeCopy(Object obj) {
		
		if(obj==null) return null;
		Object retObj=null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			oos.close();
			
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
			retObj = ois.readObject();
			ois.close();
		}catch(Exception e) {
			
		}
		return retObj;
	}
	
		
	public static void breadCrumbFrom(int fromPageNo) {
	
		try {
			
			Campaigns campaign=(Campaigns)Sessions.getCurrent().getAttribute("campaign");
			
			Div navigationDivId =(Div)Utility.getComponentById("navigationDivId");
			
			//disable navigation In Header part
			Div campCreationFirstStepId=(Div) navigationDivId.getFellowIfAny("campCreationFirstStepId");
			Div campCreationSecondStepId=(Div)navigationDivId.getFellowIfAny("campCreationSecondStepId");
			Div campCreationThirdStepId=(Div)navigationDivId.getFellowIfAny("campCreationThirdStepId");
			Div campCreationFourthStepId=(Div)navigationDivId.getFellowIfAny("campCreationFourthStepId");
			Div campCreationFifthStepId=(Div)navigationDivId.getFellowIfAny("campCreationFifthStepId");
			Div campCreationSixthStepId=(Div)navigationDivId.getFellowIfAny("campCreationSixthStepId");

			Div[] naviDivArr = {campCreationFirstStepId, campCreationSecondStepId, campCreationThirdStepId,
					campCreationFourthStepId, campCreationFifthStepId, campCreationSixthStepId};
			
			int draftedPages=0;
			
			if(campaign==null) {
				draftedPages=0;
			}
			else if(campaign.getDraftStatus().equalsIgnoreCase("CampMlist")) { // page 2
				draftedPages=1;
			}
			else if(campaign.getDraftStatus().equalsIgnoreCase("CampLayout")) { // page 3
				draftedPages=2;
			}
			else if(campaign.getDraftStatus().equalsIgnoreCase("CampTextMsg")) { // page 5
				draftedPages=4;
			}
			else if(campaign.getDraftStatus().equalsIgnoreCase("CampFinal")) { // page 6
				draftedPages=5;
			}
			else if(campaign.getDraftStatus().equalsIgnoreCase("complete")) { // page 6
				draftedPages=6;
			}

			logger.info("----------------- breadCrumbFrom called with :"+fromPageNo +"  :: draftedPages= "+draftedPages);
			
			
			for (int i = 0; i < naviDivArr.length; i++) {
				
				if(i+1 == fromPageNo) { 
//					naviImagesArr[i].setSrc("img/theme/button.png"); 
				//	naviDivArr[i].setSrc("img/camp_step_current.png");// Current Image
					
					/*naviDivArr[i].setStyle("background:url('" +
							"img/camp_step_current.png" +
							"');background-repeat:no-repeat; " +
							"cursor:pointer; cursor:hand;");*/
					
					naviDivArr[i].setSclass("create_email_step_current");
					
					continue;
				}
				
				if(i <= draftedPages) {
//					naviImagesArr[i].setSrc("img/theme/spam_score.png"); 
				//	naviDivArr[i].setSrc("img/camp_step_completed.png"); // Completed Image
					
					/*naviDivArr[i].setStyle("background:url('" +
							"img/camp_step_completed.png" +
							"');background-repeat:no-repeat; " +
							"cursor:pointer; cursor:hand;");*/
					
					naviDivArr[i].setSclass("create_email_step_completed");

				}
				else {
//					naviImagesArr[i].setSrc("img/theme/abc_active.png"); // Incompleted Image
					// naviDivArr[i].setSrc("img/camp_step_incomplete.png");
					
					/*naviDivArr[i].setStyle("background:url('" +
							"img/camp_step_incomplete.png" +
							"');background-repeat:no-repeat; " +
							"cursor:pointer; cursor:hand;");*/
					
					naviDivArr[i].setSclass("create_email_step_incomplete");
				}
				
			} // for i
			
			navigationDivId.invalidate();
			navigationDivId.setVisible(true);
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	}
	
	public static String stringToHex(String str) {
        char[] chars = str.toCharArray();
        StringBuffer strBuffer = new StringBuffer();
        String tempStr;
        for (int i = 0; i < chars.length; i++) {
        	
        	tempStr = Integer.toHexString((int) chars[i]);
        	logger.info(chars[i]+" = "+tempStr);
        	if(tempStr.length()==1) {
        		tempStr = "000"+tempStr;
        	}
        	else if(tempStr.length()==2) {
        		tempStr = "00"+tempStr;
        	}
        	else if(tempStr.length()==3) {
        		tempStr = "0"+tempStr;
        	}

            strBuffer.append(tempStr);
        }
        return strBuffer.toString().toUpperCase();
    }
	

	public static String HexToString(String str) {
		int size = str.length()/4;
		String[] charTokensArr = new String[size];
		StringBuffer buffer = new StringBuffer();
		int j = 0;
		int k =0;
		char c ;
		
		for(int i=0; i<str.length(); i+=4){
			
			charTokensArr[j] = str.substring(i, i+4);
			 k= Integer.parseInt(charTokensArr[j],16);
			    logger.info("Decimal:="+ k);
			    c = (char)k;
			    ++j;
			    buffer.append(c);
		}
			/*++j;
		}
		
		 for(int i=0; i<charTokensArr.length; i++){
			 
			    k= Integer.parseInt(charTokensArr[i],16);
			    logger.info("Decimal:="+ k);
			    c = (char)k;
			    buffer.append(c);
		 }*/
		 
		return buffer.toString();
		
	
	}
	//***********Generate 32 bit cipherText(encrypted Text) of given URL using  MessageDigest5(MD5) Algo
	private static String MD5Algo(String inputUrl) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			 byte[] messageDigest = md.digest(inputUrl.getBytes());
			 
//			 logger.debug("md5 lengtyh ==="+messageDigest.length);
			 BigInteger number = new BigInteger(1, messageDigest);
			 String hashtext = number.toString(16);
			   
			   // Now we need to zero pad it if you actually want the full 32 chars.
			   while (hashtext.length() < 32) {
				   
			       hashtext = "0" + hashtext;
			   }
			   return hashtext;
		} catch (Exception e) {
			logger.error("Exception ::error occured while generating the MD5 algoitham",e);
			return null;
		}
	} // MD5Algo
	
	//****return list(size 4) of String  each string contain 6-digit length ****//
	public static List getSixDigitURLCode(String inputURL) {
		
		try {
			List sixDigitStrList = new ArrayList();
			char[] base32 =  {
				    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
				    'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
				    'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
				    'y', 'z', '0', '1', '2', '3', '4', '5'
			};
			
			String Md5result = MD5Algo(inputURL);
			if(Md5result == null) {
				logger.debug(" :: generated  MD5 algo is null");
				return null;
			}
			logger.debug("MD5 result string is ===>"+Md5result);
			 int lenthofInt = Md5result.length();
			 int subHexLenInt = lenthofInt / 8;
			 int maxLength=8;
			 
			 for(int i=0; i < subHexLenInt; i++) {
				 String subHexStr = Md5result.substring(i*8, (i*8)+maxLength);

//				 logger.debug("Hex ::"+Long.parseLong(subHexStr, 16));
				 
				 long hexaValue = 0x3FFFFFFF & Long.parseLong(subHexStr, 16);
//				 logger.debug("hexaValue is::"+hexaValue);
				 
				 StringBuffer outSb = new StringBuffer();
				 
				 for (int j = 0; j < 6; j++) {
				      long val = 0x0000001F & hexaValue;
				      outSb.append(base32[(int)val]);
				      hexaValue = hexaValue >>> 5;
				  } //inner for
				 
//				 logger.debug("== "+outSb);
				 if(outSb !=  null)
				 sixDigitStrList.add(outSb);
			 } // outer for
			 return sixDigitStrList;
			 
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
		 
	} // getSixDigitURLCode
	
	public static List getEightDigitURLCode(String inputURL) {
		
		try {
			List eightDigitStrList = new ArrayList();
			char[] base32 =  {
				    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
				    'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
				    'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
				    'y', 'z', '0', '1', '2', '3', '4', '5'
			};
			
			String Md5result = MD5Algo(inputURL);
			if(Md5result == null) {
				logger.debug(" :: generated  MD5 algo is null");
				return null;
			}
			logger.debug("MD5 result string is ===>"+Md5result);
			 int lenthofInt = Md5result.length();
			 int subHexLenInt = lenthofInt / 8;
			 int maxLength=8;
			 
			 for(int i=0; i < subHexLenInt; i++) {
				 String subHexStr = Md5result.substring(i*8, (i*8)+maxLength);

//				 logger.debug("Hex ::"+Long.parseLong(subHexStr, 16));
				 
				 long hexaValue = 0x3FFFFFFF & Long.parseLong(subHexStr, 16);
//				 logger.debug("hexaValue is::"+hexaValue);
				 
				 StringBuffer outSb = new StringBuffer();
				 
				 for (int j = 0; j < 8; j++) {
				      long val = 0x0000001F & hexaValue;
				      outSb.append(base32[(int)val]);
				      hexaValue = hexaValue >>> 5;
				  } //inner for
				 
//				 logger.debug("== "+outSb);
				 if(outSb !=  null)
					 eightDigitStrList.add(outSb);
			 } // outer for
			 return eightDigitStrList;
			 
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
		 
	} // getSixDigitURLCode








	public static void  sendTestMail(Campaigns campaign, String htmlStuff,String emailId) {
		
	
		
		EmailQueueDao emailQueueDao = (EmailQueueDao)SpringUtil.getBean("emailQueueDao");
		EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)SpringUtil.getBean("emailQueueDaoForDML");
		UserActivitiesDao userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		UserActivitiesDaoForDML userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		Users user = GetUser.getUserObj();
		
		try{
			EmailQueue testEmailQueue = new EmailQueue(
					campaign.getCampaignId(), Constants.EQ_TYPE_TESTMAIL, "Active", emailId, 
					MyCalendar.getNewCalendar(), user);
			
			testEmailQueue.setMessage(htmlStuff);
			
			try{
				//emailQueueDao.saveOrUpdate(testEmailQueue);
				emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
				/*if(userActivitiesDao != null) {
            		userActivitiesDao.addToActivityList(ActivityEnum.CAMP_SENT_TSTMAIL_p1campaignName, user, campaign.getCampaignName());
				}*/
				if(userActivitiesDaoForDML != null) {
            		userActivitiesDaoForDML.addToActivityList(ActivityEnum.CAMP_SENT_TSTMAIL_p1campaignName, user, campaign.getCampaignName());
				}
			}
			catch(Exception e1){
				logger.error("** Exception : Error while saving the Test Email" +
						" into queue ", e1);
			}
			
		}
		catch(Exception e){
			logger.error("** Exception : ", e);
		}
	}

	
	
	 public static String getUserIdsAsString(Set<Long> userIds) {	
		
		String userIdsStr="";
		for (Long userId : userIds) {
			if(userIdsStr.length()>0) userIdsStr = userIdsStr +", ";
			userIdsStr = userIdsStr + userId;
		}	
		return userIdsStr;
	 }
	
	 
	 /**
	  * added for sharing
	  * @param userIds
	  * @return
	  */
	 public static String getIdsAsString(Set<Long> listIdsSet) {	
			
			String listIdsStr="";
			for (Long listId : listIdsSet) {
				if(listIdsStr.length()>0) listIdsStr = listIdsStr +", ";
				listIdsStr = listIdsStr + listId;
			}	
			return listIdsStr;
		 }
	 
	 public static boolean validateUserPhoneNum(String userPhoneNum) {

			if (userPhoneNum == null || userPhoneNum.trim().length()==0) {
				return false;
			}
			String pattern = PropertyUtil.getPropertyValue("validtaePhoneNumber").trim();
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(userPhoneNum);
			
/*			logger.info(" ========>"+ m.matches());
			if(! m.matches()) {
				 pattern = PropertyUtil.getPropertyValue("validateUSPhoneNum_two").trim();
				 p = Pattern.compile(pattern);
				 m = p.matcher(userPhoneNum);
			}
*/			return m.matches();
		}//validateUserPhoneNum
	
	


/**
	 * this method creates the EmailQueue object and save intoDB <BR>
	 * hits the Servlet which enables the EmailQueuScheduler
	 */
	public static void sendInstantMail(Campaigns campaign,String subject,String message, String type, String toEmailId, CustomTemplates customTemplate) {
		
		try{
			
			/**
		     * The following line is commented bcz the message in that method should not be displayed 
		     * when sending the test-mail bcz that message content will be displayed in that received mail.
		     */
			
			//barcodeIndicativeMsg(message);
			logger.debug("type3 ::"+type+" subject::"+subject+"customTemplate ::");
			//logger.info("got subject as====>"+subject);
			EmailQueueDao emailQueueDao = (EmailQueueDao)SpringUtil.getBean("emailQueueDao");
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)SpringUtil.getBean("emailQueueDaoForDML");
			//UserActivitiesDao userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
			Users user = GetUser.getUserObj();
			EmailQueue testEmailQueue = null;
			
				if(customTemplate != null) {
					
					testEmailQueue = new EmailQueue(
							null,customTemplate.getTemplateId(), type, "Active", toEmailId, 
							MyCalendar.getNewCalendar(), user);
					
					testEmailQueue.setMessage(message);
					
				}
				
				else if(campaign != null && customTemplate == null) {
					testEmailQueue = new EmailQueue(
						campaign.getCampaignId(), type, "Active", toEmailId, 
						MyCalendar.getNewCalendar(), user);
				
				testEmailQueue.setMessage(message);
				//logger.info("just entered sendInstantMail");
				}
				else if(campaign == null && customTemplate == null ) {
								
					testEmailQueue = new EmailQueue(
						subject, message, type, "Active", toEmailId, 
							MyCalendar.getNewCalendar(), user);
				
					
				}
				
					//emailQueueDao.saveOrUpdate(testEmailQueue);
					emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
					
					String InstantMailUrl = PropertyUtil.getPropertyValue("InstantMailUrl");
					//Send request to servlet which activates the simpleMailSender(which actually sends the instant mails)
					pingSchedulerService(InstantMailUrl);
					
					
					
				/*	String postData = "";
					String response = "";
					String tempData = "";
					postData += "UserRequest="+URLEncoder.encode(tempData, "UTF-8");
					logger.debug("Data to be sent is=====>"+postData);
					
					try {
						String InstantMailUrl = PropertyUtil.getPropertyValue("InstantMailUrl");
						URL url = new URL(InstantMailUrl);
				            URLConnection schedulerConnection = url.openConnection();
				            DataInputStream dis = new DataInputStream(schedulerConnection.getInputStream());
				            String inputLine;
	
				            while ((inputLine = dis.readLine()) != null) {
				                logger.info("Received message : "+ inputLine);
				            }
				            dis.close();
				            
				        } catch (MalformedURLException me) {
				            logger.info("MalformedURLException: " + me);
				        } catch (IOException ioe) {
				            logger.info("IOException: " + ioe);
				        }
						*/
			
			
		}
		catch(Exception e){
			logger.error("** Exception : ", e);
		}
		
	}

	
	
public static void sendInstantMail(Campaigns campaign,String subject,String message, 
		String type, String toEmailId, CustomTemplates customTemplate, EmailQueueDao emailQueueDao,EmailQueueDaoForDML emailQueueDaoForDML,Users user) {
		
		try{
			logger.debug("type ::"+type+" subject::"+subject+"customTemplate ::");
			//logger.info("got subject as====>"+subject);
						//UserActivitiesDao userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
			
			EmailQueue testEmailQueue = null;
			
				if(customTemplate != null) {
					
					testEmailQueue = new EmailQueue(
							null,customTemplate.getTemplateId(), type, "Active", toEmailId, 
							MyCalendar.getNewCalendar(), user);
					
					testEmailQueue.setMessage(message);
					
				}
				
				else if(campaign != null && customTemplate == null) {
					testEmailQueue = new EmailQueue(
						campaign.getCampaignId(), type, "Active", toEmailId, 
						MyCalendar.getNewCalendar(), user);
				
				testEmailQueue.setMessage(message);
				//logger.info("just entered sendInstantMail");
				}
				else if(campaign == null && customTemplate == null ) {
								
					testEmailQueue = new EmailQueue(
						subject, message, type, "Active", toEmailId, 
							MyCalendar.getNewCalendar(), user);
					
					
				}
				
					//emailQueueDao.saveOrUpdate(testEmailQueue);
					
				emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
					String InstantMailUrl = PropertyUtil.getPropertyValue("InstantMailUrl");
					//Send request to servlet which activates the simpleMailSender(which actually sends the instant mails)
					pingSchedulerService(InstantMailUrl);
	
		}catch (Exception e) {
			logger.error("Exception ::" , e);
		}
					
}
	

public static void sentInstantParentalConsent(String childName,String childEmail, 
			String birthDay, String subject, String message, String toEmail, String type,
			CustomTemplates customTemplate,EmailQueueDaoForDML emailQueueDaoForDML, Users user, Long cid) {
	
	String InstantMailUrl = PropertyUtil.getPropertyValue("InstantMailUrl");
	try {
		/**
		 *  public EmailQueue(CustomTemplates customTemplates, String type,
			  String status, String toEmailId,Users user,Calendar sentDate, String subject, String childEmail, String childFirstName, String dateOfBirth) {
		
		 */
		 if(customTemplate != null && customTemplate.getHtmlText()!= null) {
			  message = customTemplate.getHtmlText();
		  }else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(customTemplate.getEditorType()) && customTemplate.getMyTemplateId()!=null) {
			  MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
			  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(customTemplate.getMyTemplateId());
			  if(myTemplates!=null)message = myTemplates.getContent();
		  }
		logger.debug("type ::"+type+" subject::"+subject+"customTemplate ::"+childEmail);
		EmailQueue testEmailQueue =  null;
		testEmailQueue = new EmailQueue(customTemplate != null ? customTemplate.getTemplateId() : null, type, message, "Active", toEmail, user, MyCalendar.getNewCalendar(),
				subject, childEmail, childName, birthDay, cid);
			
		testEmailQueue.setChildEmail(childEmail);
		logger.info("testEmailQueue"+testEmailQueue.getChildEmail());
		//emailQueueDao.saveOrUpdate(testEmailQueue);
		emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
		
		//Send request to servlet which activates the simpleMailSender(which actually sends the instant mails)
		pingSchedulerService(InstantMailUrl);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
	}
	
	
	
	
}



					/**
	 * To ping the instance mail sender	
	 */
		
	public static void pingSchedulerService(String targetServiceUrl) {
		
		try {
			//String InstantMailUrl = PropertyUtil.getPropertyValue("InstantMailUrl");
				URL url = new URL(targetServiceUrl);
			
		        URLConnection schedulerConnection = url.openConnection();
		        DataInputStream dis = new DataInputStream(schedulerConnection.getInputStream());
	            String inputLine;

	            while ((inputLine = dis.readLine()) != null) {
	                logger.info("Received message : "+ inputLine);
	            }
	            dis.close();
		        
		    } catch (MalformedURLException me) {
		        logger.info("MalformedURLException: " + me);
		        logger.error("Exception ::" , me);;
		    } catch (Exception e) {
		        logger.info("IOException: " + e);
		        logger.error("Exception ::" , e);
		    }
		
		
	}//pingInstantMailSender
	
	
	public static String getContactEmailStatus(Contacts contact, MailingList ml) {
		
		ContactParentalConsentDao contactParentalConsentDao = (ContactParentalConsentDao)SpringUtil.getBean("contactParentalConsentDao");
		
		ContactParentalConsent contactParentalConsent = contactParentalConsentDao.findByContactId(contact.getContactId());
		
		
		String status = "";
		
		boolean optInFlag = (contact.getEmailStatus() != null && contact.getEmailStatus().equals(Constants.CONT_STATUS_OPTIN_PENDING) ) ? true : false;
		
		boolean consentFlag = (contact.getEmailStatus() != null && contact.getEmailStatus().equals(Constants.CONT_STATUS_PARENTAL_PENDING) )? true : false;
		
		String optinMedium = contact.getOptinMedium();
		
		if(optInFlag ) {
			
			if(ml.isCheckParentalConsent() && optinMedium != null && optinMedium.startsWith(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM)) {
			
				if(contactParentalConsent != null && contactParentalConsent.getStatus().equals(Constants.CONT_PARENTAL_STATUS_PENDING_APPROVAL)) {
					
					status = Constants.CONT_STATUS_PARENTAL_PENDING;
					
					
				}
				else{
					status = Constants.CONT_STATUS_ACTIVE;
					
				}
				
				
				
			}//if parental is enabled
			else{
				
				status = Constants.CONT_STATUS_ACTIVE;
				
			}
			
		}
		if(consentFlag ) {
			
			
			
			if( optinMedium != null && 
					optinMedium.equals(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM) && 
					contactParentalConsent != null && 
					contactParentalConsent.getStatus().equals(Constants.CONT_PARENTAL_STATUS_PENDING_APPROVAL)) {
				
				status = Constants.CONT_STATUS_PARENTAL_PENDING;
				
				
			}
			else{
				status = Constants.CONT_STATUS_ACTIVE;
				
			}
			
			
			
		}
		
		
		return status;
	}
	

	public static List<String> couponGenarationCode(String inputURL , int param) {
		
		try {
			List<String> couponGenList = new ArrayList<String>();
			
			char[] base32 =  {
				    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 
				    'I', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 
				    'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 
				    'Z', '1', '2', '3', '4', '5', '6', '7'
			};
			
			String Md5result = MD5Algo(inputURL);
			if(Md5result == null) {
				//logger.info(" :: generated  MD5 algo is null");
				return null;
			}
			//logger.info("MD5 result string is ===>"+Md5result);
			 int lenthofInt = Md5result.length();
			// logger.info("lenthofInt ===>"+lenthofInt);
			 int subHexLenInt = lenthofInt / 8;
			// logger.info("subHexLenInt ===>"+subHexLenInt);
			 
			 int maxLength = 8;
			 
			 for(int i=0; i < subHexLenInt; i++) {
				 String subHexStr = Md5result.substring(i*8, (i*8)+maxLength);
			//	 logger.info("subHexStr ::"+subHexStr);
			//	 logger.info("Hex ::"+Long.parseLong(subHexStr, 16));
				 
				 long hexaValue = 0x3FFFFFFF & Long.parseLong(subHexStr, 16);
			//	 logger.info("hexaValue is::"+hexaValue);
				 
				 StringBuffer outSb = new StringBuffer();
				 
				 for (int j = 0; j < param; j++) {
				      long val = 0x0000001F & hexaValue;
				    
				      outSb.append(base32[(int)val]);
				      hexaValue = hexaValue >>> param-1;
				  } //inner for
				 
			//	 logger.info("== "+outSb);
				 if(outSb !=  null)
					 couponGenList.add(outSb.toString());
			 } // outer for
			 return couponGenList;
			 
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
		 
	} // couponGenarationCode
	
	/** find coupons based on coupon code
	 * calculate issued coupons based on coupon code and status as active 
	 * calculate redeemed coupons based on coupon code and status as redeemed
	 * calculate available count subtracting issued count from total quantity 
	 * @param coupCode
	 */
	/*public static boolean findCoupons(Coupons couponObj, CouponCodesDao couponCodesDao, CouponsDao couponsDao ){
		
		try {
			long redeemdCount = couponCodesDao.findRedeemdCoupCodeByCoup(couponObj.getCouponId(), Constants.COUP_CODE_STATUS_REDEEMED);
		   double totRevenue = couponCodesDao.findTotRevenue(couponObj.getCouponId());
		   logger.info("total revenue is "+totRevenue);
		
			//Redeemed
			couponObj.setRedeemed(redeemdCount);
			
			//Available
			//long totCount = couponObj.getTotalQty() != null ? couponObj.getTotalQty() :0;
			//long issuedCout = couponObj.getIssued() != null ? couponObj.getIssued() :0;
			//couponObj.setAvailable(totCount - issuedCout);
			
			// total revenue
			couponObj.setTotRevenue(totRevenue);
			
			couponsDao.saveOrUpdate(couponObj);
			return true;
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return false;
		}
	}//findCoupons
	
	*/
	
	public static void showPreviewNewEditor(Iframe sourceIframeId, String userName, String htmlStr) {
		
		htmlStr=htmlStr.replace("[url]", "javascript:void(0)");
		/*htmlStr=htmlStr.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\"");
		htmlStr=htmlStr.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\"");*/
		
		if(htmlStr.contains("href='")){
			htmlStr = htmlStr.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\"");
			
		}
		if(htmlStr.contains("href=\"")){
			htmlStr = htmlStr.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\"");
		}
		
		
		barcodeIndicativeMsg(htmlStr);
		
		//display preview with coupn bar code
		
		try{
			String imgPattern = "<img\\s+.*?((?:id\\s*=\\s*\\\"?CC_\\w\\\"?)).*?>";
			String idPattern = "<img .*?id\\s*?=\\\"?(.*?)\\\".*?>";
			String srcPattern = "<img .*?src\\s*?=\\\"?(.*?)\\\".*?>";
			String wPattern = "<img .*?width\\s*?=\\\"?(.*?)\\\".*?>";
			String hPattern = "<img .*?height\\s*?=\\\"?(.*?)\\\".*?>";
			String stylePattern = "<img .*?style\\s*?=\\\"?(.*?)\\\".*?>";
			
			Pattern pattern = Pattern.compile(imgPattern,Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(htmlStr);
			
			
			while(matcher.find()) {

				String imgtag = null;
				String srcAttr = null;
				String idAttr = null;
				String wAttr = null;
				String hAttr = null;
				String styleAttr = null;
				
					imgtag = matcher.group();
					
					Pattern srcp = Pattern.compile(srcPattern,Pattern.CASE_INSENSITIVE);
					Matcher srcm = srcp.matcher(imgtag);
					
					Pattern idp = Pattern.compile(idPattern, Pattern.CASE_INSENSITIVE);
					Matcher idm = idp.matcher(imgtag);
					
					Pattern widthp = Pattern.compile(wPattern, Pattern.CASE_INSENSITIVE);
					Matcher widthm = widthp.matcher(imgtag);
					
					Pattern heightp = Pattern.compile(hPattern, Pattern.CASE_INSENSITIVE);
					Matcher heightm = heightp.matcher(imgtag);
					
					Pattern stylep = Pattern.compile(stylePattern, Pattern.CASE_INSENSITIVE);
					Matcher stylem = stylep.matcher(imgtag);
					
					
					while(srcm.find()){
						srcAttr = srcm.group(1);
					}
					while(idm.find()){
						idAttr = idm.group(1);
					}
					while(widthm.find()){
						wAttr = widthm.group(1);
					}
					while(heightm.find()){
						hAttr = heightm.group(1);
					}
					while(stylem.find()){
						styleAttr = stylem.group(1);
					}
					
					
					//style="width: 120px; height: 80px;"
					if(wAttr == null || hAttr == null){
						
						while(stylem.find()){
							styleAttr = stylem.group(1);
							
							logger.info("styleAttr :"+styleAttr);
							String whAttr[] = styleAttr.split(";");
							//for width part
							String wstyle[] = whAttr[0].trim().split(":");
							wAttr = wstyle[1].replace("px", "").trim();

							//for height part
							String hstyle[] = whAttr[1].trim().split(":");
							hAttr = hstyle[1].replace("px", "").trim();
							
						}
					}
					
					wAttr = wAttr.trim();
					hAttr = hAttr.trim();
					if(styleAttr!=null && !styleAttr.isEmpty()) {
						styleAttr = styleAttr.trim();
					}
					
					
					String ccPhTokens[] = idAttr.split("_");
					
					String phStr = ccPhTokens[0]+"_"+ccPhTokens[1]+"_"+ccPhTokens[2];
					String COUPON_CODE_URL = "";
					
		
				
				int width = Integer.parseInt(wAttr.trim());
				int height = Integer.parseInt(hAttr.trim());
				
				BitMatrix bitMatrix = null;
				
				String barcodeFile = ccPhTokens[0]+"_"+ccPhTokens[1]+"_"+ccPhTokens[2]+"_"+ccPhTokens[3]+"_"+
										width+"_"+height;
				String ccPreviewUrl = null;
				String barcodeType = ccPhTokens[3].trim();
				String message = "Test:"+ccPhTokens[2];
				
				if(barcodeType.equals(Constants.COUP_BARCODE_QR)){
					
					bitMatrix = new QRCodeWriter().encode(message, BarcodeFormat.QR_CODE, width, height,null);
					String bcqrImg = userName+File.separator+
							"Preview"+File.separator+"QRCODE"+File.separator+barcodeFile+".png";
					
					COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcqrImg;
					
					ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcqrImg;
				}
				else if(barcodeType.equals(Constants.COUP_BARCODE_AZTEC)){
					
					bitMatrix = new AztecWriter().encode(message, BarcodeFormat.AZTEC, width, height);
					String bcazImg = userName+File.separator+
							"Preview"+File.separator+"AZTEC"+File.separator+barcodeFile+".png";
					
					COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcazImg;
					
					ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcazImg;
				}
				else if(barcodeType.equals(Constants.COUP_BARCODE_LINEAR)){
					
					bitMatrix = new Code128Writer().encode(message, BarcodeFormat.CODE_128, width, height,null);
					String bclnImg = userName+File.separator+
							"Preview"+File.separator+"LINEAR"+File.separator+barcodeFile+".png";
					
					COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bclnImg;
					
					ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bclnImg;
				}
				else if(barcodeType.equals(Constants.COUP_BARCODE_DATAMATRIX)){
					
					bitMatrix = new DataMatrixWriter().encode(message, BarcodeFormat.DATA_MATRIX, width, height,null);
					String bcdmImg = userName+File.separator+
							"Preview"+File.separator+"DATAMATRIX"+File.separator+barcodeFile+".png";
					
					COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcdmImg;
					
					ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcdmImg;
				}
				
				if(bitMatrix == null){
					return;
				}
				File myTemplateFile = new File(COUPON_CODE_URL);
				File parentDir = myTemplateFile.getParentFile();
				 if(!parentDir.exists()) {
						
						parentDir.mkdir();
					}

				
				if(!myTemplateFile.exists()) {
					
					MatrixToImageWriter.writeToStream(bitMatrix, "png", new FileOutputStream(
		            		new File(COUPON_CODE_URL)));	
				}
				
				String barcodeImgtag = null;
				if(styleAttr!=null && !styleAttr.isEmpty()) {
					barcodeImgtag = "<img id=\""+idAttr+"\" src=\""+ccPreviewUrl+"\" width=\""+wAttr+"\" height=\""+hAttr+"\" style=\""+styleAttr+"\"/>";
				}else {
					barcodeImgtag = "<img id=\""+idAttr+"\" src=\""+ccPreviewUrl+"\" width=\""+wAttr+"\" height=\""+hAttr+"\" />";
				}
				htmlStr = htmlStr.replace(imgtag, barcodeImgtag);
			}
		}catch(Exception e){
			logger.error("Exception ::" , e);
			return;
		}
		
		sourceIframeId.setSrc(null);
		sourceIframeId.invalidate();
		/**
		 * creates a html file and saves in user/MyTemplate directory
		 */
		String previewFilePathStr = 
			PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+userName+
			File.separator+"Preview"+File.separator+"preview.html";
		try {
			File myTemplateFile = new File(previewFilePathStr);
			File parentDir = myTemplateFile.getParentFile();
			 if(!parentDir.exists()) {
					
					parentDir.mkdir();
				}

			
			if(myTemplateFile.exists()) {
				
				myTemplateFile.delete();
			}
			
			
		//    myTemplateFile = new File(previewFilePathStr);
			myTemplateFile.createNewFile();	
			//TODO Have to copy image files if exists
		   BufferedWriter bw = new BufferedWriter(new FileWriter(previewFilePathStr));
			
		   String noCache="<meta http-equiv=\"cache-control\" content=\"max-age=0\" />" +
		   		"<meta http-equiv=\"cache-control\" content=\"no-cache\" />" +
		   		"<meta http-equiv=\"expires\" content=\"0\" />" +
		   		"<meta http-equiv=\"expires\" content=\"Tue, 01 Jan 1980 1:00:00 GMT\" />" +
		   	//	"<meta http-equiv=\"refresh\" content=\"1\" />" +
		   		"<meta http-equiv=\"pragma\" content=\"no-cache\" />";
		   
		   if(!htmlStr.trim().toLowerCase().startsWith("<html>")) {
			   //htmlStr = "<html><head>"+noCache+"</head><body>"+htmlStr+"</body></html>";
			   htmlStr = htmlStr;

		   }
		   logger.info("htmlStr"+htmlStr);
			bw.write(htmlStr);
			//bw.write("enabled=true");
			bw.flush();
			bw.close();
			
		//	myTemplateFile.renameTo(parentDir);
			
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ::" , e);
		}
		sourceIframeId.invalidate();
		sourceIframeId.setSrc(PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+
				"/"+userName+"/"+"Preview"+"/"+"preview.html");
		
		//Html html = (Html)previewWin$html;
		//html.setContent(htmlStr);
		//previewWin.setVisible(true);
		
	}
	
	
	
	//Previewfor bee
	public static void showPreview(Iframe sourceIframeId, String userName, String htmlStr) {
		String randomNum=RandomStringUtils.randomAlphanumeric(4);
		htmlStr=htmlStr.replace("[url]", "javascript:void(0)");
		/*htmlStr=htmlStr.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\"");
		htmlStr=htmlStr.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\"");*/
		
		if(htmlStr.contains("href='")){
			htmlStr = htmlStr.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\"");
			
		}
		if(htmlStr.contains("href=\"")){
			htmlStr = htmlStr.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\"");
		}
		
		
		barcodeIndicativeMsg(htmlStr);
		
		//display preview with coupn bar code
		
		try{
			String imgPattern = "<img\\s+.*?((?:id\\s*=\\s*\\\"?CC_\\w\\\"?)).*?>";
			String idPattern = "<img .*?id\\s*?=\\\"?(.*?)\\\".*?>";
			String srcPattern = "<img .*?src\\s*?=\\\"?(.*?)\\\".*?>";
			String wPattern = "<img .*?width\\s*?=\\\"?(.*?)\\\".*?>";
			String hPattern = "<img .*?height\\s*?=\\\"?(.*?)\\\".*?>";
			String stylePattern = "<img .*?style\\s*?=\\\"?(.*?)\\\".*?>";
			
			Pattern pattern = Pattern.compile(imgPattern,Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(htmlStr);
			
			
			while(matcher.find()) {

				String imgtag = null;
				String srcAttr = null;
				String idAttr = null;
				String wAttr = null;
				String hAttr = null;
				String styleAttr = null;
				
					imgtag = matcher.group();
					
					Pattern srcp = Pattern.compile(srcPattern,Pattern.CASE_INSENSITIVE);
					Matcher srcm = srcp.matcher(imgtag);
					
					Pattern idp = Pattern.compile(idPattern, Pattern.CASE_INSENSITIVE);
					Matcher idm = idp.matcher(imgtag);
					
					Pattern widthp = Pattern.compile(wPattern, Pattern.CASE_INSENSITIVE);
					Matcher widthm = widthp.matcher(imgtag);
					
					Pattern heightp = Pattern.compile(hPattern, Pattern.CASE_INSENSITIVE);
					Matcher heightm = heightp.matcher(imgtag);
					
					Pattern stylep = Pattern.compile(stylePattern, Pattern.CASE_INSENSITIVE);
					Matcher stylem = stylep.matcher(imgtag);
					
					
					while(srcm.find()){
						srcAttr = srcm.group(1);
					}
					while(idm.find()){
						idAttr = idm.group(1);
					}
					while(widthm.find()){
						wAttr = widthm.group(1);
					}
					while(heightm.find()){
						hAttr = heightm.group(1);
					}
					while(stylem.find()){
						styleAttr = stylem.group(1);
					}
					
					
					//style="width: 120px; height: 80px;"
					if(wAttr == null || hAttr == null){
						
						while(stylem.find()){
							styleAttr = stylem.group(1);
							
							logger.info("styleAttr :"+styleAttr);
							String whAttr[] = styleAttr.split(";");
							//for width part
							String wstyle[] = whAttr[0].trim().split(":");
							wAttr = wstyle[1].replace("px", "").trim();

							//for height part
							String hstyle[] = whAttr[1].trim().split(":");
							hAttr = hstyle[1].replace("px", "").trim();
							
						}
					}
					
					wAttr = wAttr.trim();
					hAttr = hAttr.trim();
					if(styleAttr!=null && !styleAttr.isEmpty()) {
						styleAttr = styleAttr.trim();
					}
					
					String ccPhTokens[] = idAttr.split("_");
					
					String phStr = ccPhTokens[0]+"_"+ccPhTokens[1]+"_"+ccPhTokens[2];
					String COUPON_CODE_URL = "";
					
		
				
				int width = Integer.parseInt(wAttr.trim());
				int height = Integer.parseInt(hAttr.trim());
				
				BitMatrix bitMatrix = null;
				
				String barcodeFile = ccPhTokens[0]+"_"+ccPhTokens[1]+"_"+ccPhTokens[2]+"_"+ccPhTokens[3]+"_"+
										width+"_"+height;
				String ccPreviewUrl = null;
				String barcodeType = ccPhTokens[3].trim();
				String message = "Test:"+ccPhTokens[2];
				
				if(barcodeType.equals(Constants.COUP_BARCODE_QR)){
					
					bitMatrix = new QRCodeWriter().encode(message, BarcodeFormat.QR_CODE, width, height,null);
					String bcqrImg = userName+File.separator+
							"Preview"+File.separator+"QRCODE"+File.separator+barcodeFile+".png";
					
					COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcqrImg;
					
					ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcqrImg;
				}
				else if(barcodeType.equals(Constants.COUP_BARCODE_AZTEC)){
					
					bitMatrix = new AztecWriter().encode(message, BarcodeFormat.AZTEC, width, height);
					String bcazImg = userName+File.separator+
							"Preview"+File.separator+"AZTEC"+File.separator+barcodeFile+".png";
					
					COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcazImg;
					
					ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcazImg;
				}
				else if(barcodeType.equals(Constants.COUP_BARCODE_LINEAR)){
					
					bitMatrix = new Code128Writer().encode(message, BarcodeFormat.CODE_128, width, height,null);
					String bclnImg = userName+File.separator+
							"Preview"+File.separator+"LINEAR"+File.separator+barcodeFile+".png";
					
					COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bclnImg;
					
					ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bclnImg;
				}
				else if(barcodeType.equals(Constants.COUP_BARCODE_DATAMATRIX)){
					
					bitMatrix = new DataMatrixWriter().encode(message, BarcodeFormat.DATA_MATRIX, width, height,null);
					String bcdmImg = userName+File.separator+
							"Preview"+File.separator+"DATAMATRIX"+File.separator+barcodeFile+".png";
					
					COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcdmImg;
					
					ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcdmImg;
				}
				
				if(bitMatrix == null){
					return;
				}
				File myTemplateFile = new File(COUPON_CODE_URL);
				File parentDir = myTemplateFile.getParentFile();
				 if(!parentDir.exists()) {
						
						parentDir.mkdir();
					}

				
				if(!myTemplateFile.exists()) {
					
					MatrixToImageWriter.writeToStream(bitMatrix, "png", new FileOutputStream(
		            		new File(COUPON_CODE_URL)));	
				}
				
				String barcodeImgtag = null;
				if(styleAttr!=null && !styleAttr.isEmpty()) {
					barcodeImgtag = "<img id=\""+idAttr+"\" src=\""+ccPreviewUrl+"\" width=\""+wAttr+"\" height=\""+hAttr+"\" style=\""+styleAttr+"\" />";
				}else {
					barcodeImgtag = "<img id=\""+idAttr+"\" src=\""+ccPreviewUrl+"\" width=\""+wAttr+"\" height=\""+hAttr+"\" />";
				}
				
				htmlStr = htmlStr.replace(imgtag, barcodeImgtag);
			}
		}catch(Exception e){
			logger.error("Exception ::" , e);
			return;
		}
		
		sourceIframeId.setSrc(null);
		sourceIframeId.invalidate();
		/**
		 * creates a html file and saves in user/MyTemplate directory
		 */
		String previewFilePathStr = 
			PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+userName+
			File.separator+"Preview"+File.separator+"preview" + randomNum + ".html";
		try {
			cleanUpOldPreview(new File(PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+userName+
					File.separator+"Preview"), "preview");
			//FileUtils.deleteQuietly(FileUtils.getFile(deletePrviousPreviewFilePathStr));
			File myTemplateFile = new File(previewFilePathStr);
			File parentDir = myTemplateFile.getParentFile();
			 if(!parentDir.exists()) {					
					parentDir.mkdir();
				}

			
			if(myTemplateFile.exists()) {
				
				myTemplateFile.delete();
			}
			
			
		//    myTemplateFile = new File(previewFilePathStr);
			myTemplateFile.createNewFile();	
			//TODO Have to copy image files if exists
		   BufferedWriter bw = new BufferedWriter(new FileWriter(previewFilePathStr));
			
		   String noCache="<meta http-equiv=\"cache-control\" content=\"max-age=0\" />" +
		   		"<meta http-equiv=\"cache-control\" content=\"no-cache\" />" +
		   		"<meta http-equiv=\"expires\" content=\"0\" />" +
		   		"<meta http-equiv=\"expires\" content=\"Tue, 01 Jan 1980 1:00:00 GMT\" />" +
		   	//	"<meta http-equiv=\"refresh\" content=\"1\" />" +
		   		"<meta http-equiv=\"pragma\" content=\"no-cache\" />";
		   
		   if(!htmlStr.trim().toLowerCase().startsWith("<html>")) {
			   htmlStr = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"+noCache+"</head><body>"+htmlStr+"</body></html>";
		   }
		   logger.info("htmlStr"+htmlStr);
			bw.write(htmlStr);
			//bw.write("enabled=true");
			bw.flush();
			bw.close();
			
		//	myTemplateFile.renameTo(parentDir);
			
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ::" , e);
		}
		sourceIframeId.invalidate();
		sourceIframeId.setSrc(PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+
				"/"+userName+"/"+"Preview"+"/"+"preview"+ randomNum +".html");
		
		//Html html = (Html)previewWin$html;
		//html.setContent(htmlStr);
		//previewWin.setVisible(true);
		
	}
	
	
//	private static final Pattern csvPattern = Pattern.compile("\"([^\"]*)\"|(?<=,|^)([^,]*)(?:,|$)");  
	private static Matcher matcher = null;
	public static final Pattern phonePattern = Pattern.compile("\\b(1?|91?|92?|971?)\\s*\\(?([0-9]{3})\\)?[-. *]*([0-9]{3})[-. ]*([0-9]{4})\\b");  
	/*public static void main(String[] args) {
		UserOrganization organization = new UserOrganization();
		organization.setMinNumberOfDigits(7);
		organization.setMaxNumberOfDigits(9);
		organization.setMobilePattern(false);
		System.out.println("TEst"+phoneParse("9715058936",organization));
	}*/
	public static String phoneParseForCreateUser(String csvLine, int min, int max, boolean mobilePattern) {
		// Created for validating Mobile Number in create user screen based on min and max number of digits..
		while(csvLine.startsWith("0")){
			csvLine =  csvLine.substring(1);
		}
		Pattern phonePattern = null;
		String poneMatch = null;
		if(mobilePattern){
			//For US we need to have pattern
			phonePattern = Pattern.compile("\\b(1?|91?|92?)\\s*\\(?([0-9]{3})\\)?[-. *]*([0-9]{3})[-. ]*([0-9]{4})\\b");
			Matcher matcher = phonePattern.matcher(csvLine);
			while (matcher.find()) {
				poneMatch = matcher.group(1)+matcher.group(2)+matcher.group(3)+matcher.group(4);

			}
			if( poneMatch != null ) {

				if( (poneMatch.startsWith("91") && poneMatch.startsWith("910000000000") ) ||
						(poneMatch.startsWith("92") && poneMatch.startsWith("920000000000") ) || 
						(poneMatch.startsWith("1") && poneMatch.trim().length() == 10 )   || 
						poneMatch.startsWith("0000000000")  || 
						poneMatch.startsWith("10000000000") ||
						(poneMatch.startsWith("9") && poneMatch.length() == 11	)) {
					//	System.out.println("Returning NULL ");
					return null;
				}
			}

			return poneMatch;
		}
		else{
			//default pattern \\b(1?|91?|92?|971?)[0-9]{7,9}\\b
			if(csvLine.startsWith("00") || csvLine.startsWith("0")  ) {
				String prefix = csvLine.startsWith("00") ? csvLine.substring(0,2)  : csvLine.substring(0,1); 
				csvLine = csvLine.substring(prefix.length());
			}

			if(csvLine.startsWith("971")||csvLine.startsWith("974")||csvLine.startsWith("965")||csvLine.startsWith("+971")||csvLine.startsWith("+974")||csvLine.startsWith("+965")) {
								
				
				//	phonePattern = Pattern.compile("\\b(971?|)[0-9]{"+userOrganization.getMinNumberOfDigits()+","+userOrganization.getMaxNumberOfDigits()+"}\\b");
				phonePattern = Pattern.compile("\\b(\\+?)(971?|974?|965?)\\s*\\(?([0-9]{1})\\)?[-. *]*([0-9]{"+(min-1)+","+(max-1)+"})\\b");
				Matcher matcher = phonePattern.matcher(csvLine);
				//String poneMatch = null;
				while (matcher.find()) { 
					poneMatch = matcher.group();

				}
				if(csvLine.length() == 11){
					phonePattern = Pattern.compile("\\b(\\+?)(971?|974?|965?)\\s*\\(?([0-9]{1})\\)?[-. *]*([0-9]{"+min+","+max+"})\\b");
					Matcher m= phonePattern.matcher(csvLine);
					//String poneMatch = null;
					while (m.find()) { 
						poneMatch = m.group();
					}
				}
				
				if( poneMatch != null ) {

					if((poneMatch.startsWith("971") && (poneMatch.startsWith("9710000000") || poneMatch.startsWith("97100000000") 
							|| poneMatch.startsWith("971000000000")) )||(poneMatch.startsWith("974") && (poneMatch.startsWith("9740000000") || poneMatch.startsWith("97400000000") 
									|| poneMatch.startsWith("974000000000")) )||(poneMatch.startsWith("965") && (poneMatch.startsWith("9650000000") || poneMatch.startsWith("96500000000") 
											|| poneMatch.startsWith("965000000000")) )) {
						//System.out.println("Returning NULL ");
						return null;
					}
				}
			}else if(!csvLine.startsWith("971")||!csvLine.startsWith("974")||!csvLine.startsWith("965")){
				
				phonePattern = Pattern.compile("\\b[0-9]{"+min+","+max+"}\\b");
				Matcher matcher = phonePattern.matcher(csvLine);
				//String poneMatch = null;
				while (matcher.find()) {

					poneMatch = matcher.group();
					//System.out.println(",........"+poneMatch);    
				}
			//	poneMatch = csvLine;
			}  
			return poneMatch;

		}

	}  // phoneParseForCreateuser 
	public static String phoneParse(String csvLine, UserOrganization userOrganization ) {
		try {
		if(userOrganization == null ){
			logger.info("User does not belong to any organization.", "color:red;","top");
			return null ;

		}
		if(!userOrganization.isRequireMobileValidation()) return csvLine;
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Long userId=usersDao.getOwnerofOrg(userOrganization.getUserOrgId());
			Users user=usersDao.findByUserId(userId);
			String userCountryCarrier=user.getCountryCarrier().toString();
	//	logger.debug("Validating Moblie Number"+csvLine +"      UserOranization.... "+userOrganization.getOrganizationName());
		csvLine= csvLine.replaceAll("[- ()]", "");//APP-117
		while(csvLine.startsWith("0")){
			csvLine =  csvLine.substring(1);
		}
		Pattern phonePattern = null;
		String poneMatch = null;
		if(userOrganization.isMobilePattern()){
			//For US we need to have pattern
			phonePattern = Pattern.compile("\\b(1?|91?|92?)\\s*\\(?([0-9]{3})\\)?[-. *]*([0-9]{3})[-. ]*([0-9]{4})\\b");
			Matcher matcher = phonePattern.matcher(csvLine);
			while (matcher.find()) {
				poneMatch = matcher.group(1)+matcher.group(2)+matcher.group(3)+matcher.group(4);

			}
			if( poneMatch != null ) {

				if( (poneMatch.startsWith("91") && poneMatch.startsWith("910000000000") ) ||
						(poneMatch.startsWith("92") && poneMatch.startsWith("920000000000") ) || 
						(poneMatch.startsWith("1") && poneMatch.trim().length() == 10 )   || 
						poneMatch.startsWith("0000000000")  || 
						poneMatch.startsWith("10000000000") ||
						(poneMatch.startsWith("9") && poneMatch.length() == 11	)) {
					//	System.out.println("Returning NULL ");
					return null;
				}
			}

			return poneMatch;
		}
		else{
			//default pattern \\b(1?|91?|92?|971?)[0-9]{7,9}\\b
			if(csvLine.startsWith("00") || csvLine.startsWith("0")  ) {
				String prefix = csvLine.startsWith("00") ? csvLine.substring(0,2)  : csvLine.substring(0,1); 
				csvLine = csvLine.substring(prefix.length());
			}
			//Here Code is changed for including countries Qatar, Kuwait

			/*//if(csvLine.startsWith("971")) {
			//	phonePattern = Pattern.compile("\\b(971?|)[0-9]{"+userOrganization.getMinNumberOfDigits()+","+userOrganization.getMaxNumberOfDigits()+"}\\b");
			//	phonePattern = Pattern.compile("\\b(971)[0-9]{"+userOrganization.getMinNumberOfDigits()+","+userOrganization.getMaxNumberOfDigits()+"}\\b");
				phonePattern = Pattern.compile("\\b(971?|9?74?|965)\\s*\\(?([0-9]{1})\\)?[-. *]*([0-9]{"+userOrganization.getMinNumberOfDigits()+","+userOrganization.getMaxNumberOfDigits()+"}\\b");
				Matcher matcher = phonePattern.matcher(csvLine);
				//String poneMatch = null;
				while (matcher.find()) {

					poneMatch = matcher.group();
					//       System.out.println(",........"+poneMatch);    
				}
				if( poneMatch != null ) {

					if((poneMatch.startsWith("971") && (poneMatch.startsWith("9710000000") || poneMatch.startsWith("97100000000") 
							|| poneMatch.startsWith("971000000000")) )) {
						//System.out.println("Returning NULL ");
						return null;
					}
				}
			}else if(!(csvLine.startsWith("971"))){
				
				phonePattern = Pattern.compile("^\\d{9}$");
				Matcher matcher = phonePattern.matcher(csvLine);
				//String poneMatch = null;
				while (matcher.find()) {

					poneMatch = matcher.group();
					//       System.out.println(",........"+poneMatch);    
				}
			//	poneMatch = csvLine;
			}

			return poneMatch;


		}

	}  // phoneParse
*/	
			//if(csvLine.startsWith("971")||csvLine.startsWith("974")||csvLine.startsWith("965")||csvLine.startsWith("+971")||csvLine.startsWith("+974")||csvLine.startsWith("+965")) {
			if((csvLine.startsWith(userCountryCarrier)||csvLine.startsWith("+"+userCountryCarrier))) {
							
				//phonePattern = Pattern.compile("\\b(\\+?)(971?|974?|965?)\\s*\\(?([0-9]{1})\\)?[-. *]*([0-9]{"+(userOrganization.getMinNumberOfDigits()-1)+","+(userOrganization.getMaxNumberOfDigits()-1)+"})\\b");
				phonePattern = Pattern.compile("\\b(\\+?)("+userCountryCarrier+"?)\\s*\\(?([0-9]{1})\\)?[-. *]*([0-9]{"+(userOrganization.getMinNumberOfDigits()-1)+","+(userOrganization.getMaxNumberOfDigits()-1)+"})\\b");
				Matcher matcher = phonePattern.matcher(csvLine);
				//String poneMatch = null;
				while (matcher.find()) { 
					poneMatch = matcher.group();

				}
				
				if(csvLine.length() == 11){
					//phonePattern = Pattern.compile("\\b(\\+?)(971?|974?|965?)\\s*\\(?([0-9]{1})\\)?[-. *]*([0-9]{"+userOrganization.getMinNumberOfDigits()+","+userOrganization.getMaxNumberOfDigits()+"})\\b");
					phonePattern = Pattern.compile("\\b(\\+?)("+userCountryCarrier+"?)\\s*\\(?([0-9]{1})\\)?[-. *]*([0-9]{"+userOrganization.getMinNumberOfDigits()+","+userOrganization.getMaxNumberOfDigits()+"})\\b");
					Matcher m= phonePattern.matcher(csvLine);
					//String poneMatch = null;
					while (m.find()) { 
						poneMatch = m.group();
					}
				}
				
				if( poneMatch != null ) {

					if((poneMatch.startsWith(userCountryCarrier) && (poneMatch.startsWith(userCountryCarrier+"0000000") 
							|| poneMatch.startsWith(userCountryCarrier+"00000000") 
							|| poneMatch.startsWith(userCountryCarrier+"000000000")) )) {
						return null;
					}
				}
			}else if(!csvLine.startsWith(userCountryCarrier)){
				
				phonePattern = Pattern.compile("\\b[0-9]{"+userOrganization.getMinNumberOfDigits()+","+userOrganization.getMaxNumberOfDigits()+"}\\b");
				Matcher matcher = phonePattern.matcher(csvLine);
				//String poneMatch = null;
				while (matcher.find()) {

					poneMatch = matcher.group();
					//System.out.println(",........"+poneMatch);    
				}
			//	poneMatch = csvLine;
			}  

		}

		logger.info("phone parse returned phone number >>>> "+poneMatch);
		return poneMatch;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception in phone parse ",e);
			return null;
		} 
	}  // phoneParse
	public static String validateCouponDimensions(String content){
		
		try{
			String imgPattern = "<img\\s+.*?((?:id\\s*=\\s*\\\"?CC_\\w\\\"?)).*?>";
			String idPattern = "<img .*?id\\s*?=\\\"?(.*?)\\\".*?>";
			String srcPattern = "<img .*?src\\s*?=\\\"?(.*?)\\\".*?>";
			String wPattern = "<img .*?width\\s*?=\\\"?(.*?)\\\".*?>";
			String hPattern = "<img .*?height\\s*?=\\\"?(.*?)\\\".*?>";
			String stylePattern = "<img .*?style\\s*?=\\\"?(.*?)\\\".*?>";
			
			Pattern pattern = Pattern.compile(imgPattern,Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(content);
			
			
			while(matcher.find()) {

				String imgtag = null;
				String srcAttr = null;
				String idAttr = null;
				String wAttr = null;
				String hAttr = null;
				String styleAttr = null;
				
					imgtag = matcher.group();
					
					Pattern srcp = Pattern.compile(srcPattern,Pattern.CASE_INSENSITIVE);
					Matcher srcm = srcp.matcher(imgtag);
					
					Pattern idp = Pattern.compile(idPattern, Pattern.CASE_INSENSITIVE);
					Matcher idm = idp.matcher(imgtag);
					
					Pattern widthp = Pattern.compile(wPattern, Pattern.CASE_INSENSITIVE);
					Matcher widthm = widthp.matcher(imgtag);
					
					Pattern heightp = Pattern.compile(hPattern, Pattern.CASE_INSENSITIVE);
					Matcher heightm = heightp.matcher(imgtag);
					
					Pattern stylep = Pattern.compile(stylePattern, Pattern.CASE_INSENSITIVE);
					Matcher stylem = stylep.matcher(imgtag);
					
					
					while(srcm.find()){
						srcAttr = srcm.group(1);
					}
					while(idm.find()){
						idAttr = idm.group(1);
					}
					while(widthm.find()){
						wAttr = widthm.group(1);
					}
					while(heightm.find()){
						hAttr = heightm.group(1);
					}
					//style="width: 120px; height: 80px;"
					if(wAttr == null || hAttr == null){
						
						while(stylem.find()){
							styleAttr = stylem.group(1);
							
							logger.info("styleAttr :"+styleAttr);
							String whAttr[] = styleAttr.split(";");
							//for width part
							String wstyle[] = whAttr[0].trim().split(":");
							wAttr = wstyle[1].replace("px", "").trim();

							//for height part
							String hstyle[] = whAttr[1].trim().split(":");
							hAttr = hstyle[1].replace("px", "").trim();
							
						}
					}
					
					
					String ccPhTokens[] = idAttr.split("_");
					
					//String phStr = ccPhTokens[0]+"_"+ccPhTokens[1]+"_"+ccPhTokens[2];
					//String COUPON_CODE_URL = "";
					
				logger.info(" wAttr_width : "+wAttr+" hAttr_height: "+hAttr);
				String wAttr2 = wAttr.replace("\"", "");
				String hAttr2 = hAttr.replace("\"", "");
				if((wAttr2.trim()).endsWith("auto")) wAttr2="200";
				if((hAttr2.trim()).equalsIgnoreCase("auto")) hAttr2="50";
				int width = Integer.parseInt(wAttr2.trim());
				int height = Integer.parseInt(hAttr2.trim());
				
				logger.info(" width : "+wAttr+" height: "+hAttr);
				
				String validMsg = "Incorrect aspect ratio detected for bar-code image - "+ ccPhTokens[2]+
						"\n\n To ensure that bar-code can be scanned correctly,"+ 
						" \n please note that linear bar-codes is usually in"+ 
						"\n 4:1 or 3:1 aspect ratio, QR & Aztec codes are square,"+ 
						"\n while Data Matrix code can be square or rectangle.";
				
				if(Constants.barcodeSquareTypes.contains(ccPhTokens[3].trim()) && width != height){
				
					try {
						
						if(ccPhTokens[3].trim().equals("DM") && width >= height){
							
							return null;
							
						}
						/*int confirm = Messagebox.show("Wrong aspect ratio detected for barcode\n -- "+ccPhTokens[2]+
								"\n\nNote: Linear barcodes are generally in 4:1 or 3:1 aspect ratio while Data Matrix, Aztec and QR codes are always square.", 
								"Information", Messagebox.OK | Messagebox.CANCEL, Messagebox.INFORMATION);*/
						int confirm = Messagebox.show(validMsg, "Information", Messagebox.OK | Messagebox.CANCEL, Messagebox.INFORMATION);
						
						if(confirm != Messagebox.OK) {
							
								return ccPhTokens[2];
						}
					}catch(Exception e){
						logger.error("Exception ::" , e);
					}
						
						
				}
				else if (ccPhTokens[3].trim().equals("LN") && !(width/height ==4 || width/height == 3)){
					
					try {
						
						/*int confirm = Messagebox.show("Incorrect aspect ratio detected for bar-code image -- "+ccPhTokens[2]+
								"\n\nNote: Linear barcodes are generally in 4:1 or 3:1 aspect ratio \n while Data Matrix, Aztec and QR codes are always square.", 
								"Information", Messagebox.OK | Messagebox.CANCEL, Messagebox.INFORMATION);*/
						
						int confirm = Messagebox.show(validMsg, "Information", Messagebox.OK | Messagebox.CANCEL, Messagebox.INFORMATION);
						
						if(confirm != Messagebox.OK) {
							
								return ccPhTokens[2];	
						}
					}catch(Exception e){
						logger.error("Exception ::" , e);
					}
					
					
				}
			}
			}catch(Exception e){
				logger.error("Exception ::" , e);
				return "Barcode";
			}
		
		/*Set<String> phSet = getPhSet(content);
		
		for(String ph : phSet){
			if(ph.startsWith("CC_")){
				String[] phStr = ph.split("_");
				
				if(phStr.length > 3){
							
					try{
						int width = Integer.parseInt(phStr[4].trim());
						int height = Integer.parseInt(phStr[5].trim());
						
						if(Constants.barcodeSquareTypes.contains(phStr[3].trim()) && width != height){
							
								if(Messagebox.show("Wrong aspect ratio detected for barcode\n coupon placeholder -- "+ph+
										"\n\nNote: Linear barcodes are generally in 4:1 or 3:1 aspect ratio while Data Matrix, Aztec and QR codes are always square.", "Confirm" ,Messagebox.OK | Messagebox.CANCEL, 
										Messagebox.INFORMATION) == Messagebox.CANCEL){
									return ph;
								}
							
						}
						else if (phStr[3].trim().equals("LN") && !(width/height ==4 || width/height == 3)){
							
							if(Messagebox.show("Wrong aspect ratio detected for barcode\n coupon placeholder -- "+ph+
									"\n\nNote: Linear barcodes are generally in 4:1 or 3:1 aspect ratio while Data Matrix, Aztec and QR codes are always square.", "Confirm" ,Messagebox.OK | Messagebox.CANCEL, 
									Messagebox.INFORMATION) == Messagebox.CANCEL){
								return ph;
							}
						}
					}catch(Exception e){
						return ph;
					}
				}
			}
		}*/
		return null;
	}
	
	public static String validatePh(String content, Users user){
	
	try{
	Set<String> phSet = getPhSet(content);
	Set<String> uniqueCCSet = new HashSet<String>();
	for(String ph : phSet){
		if(ph.startsWith("DATE_")){
			String[] phStr = ph.split("_");
			if(phStr.length == 2 && (phStr[1].equals("Today") || phStr[1].equals("Tomorrow"))){
				continue;
			}
			else if(phStr.length == 3 ){
				try{
				int date = Integer.parseInt(phStr[1].trim());
				continue;
				}
				catch(Exception e){
					return ph;
				}
			}//else if
		}//if
		
		else if(ph.startsWith("CC_")){
			
			String[] phStr = ph.split("_");
			if(phStr.length > 3){
				return ph;
			}
			
			Long couponId = null;
			try{
			couponId = Long.parseLong(phStr[1]);
			}catch(Exception e){
				return ph;
			}
			
			if(couponId != null){
				
				CouponsDao couponsDao= (CouponsDao)SpringUtil.getBean("couponsDao");
				Long orgId = user.getUserOrganization().getUserOrgId();
				Coupons coupons = couponsDao.findCouponsById(couponId);
				Boolean couponExit = couponsDao.isExistCoupon(couponId, coupons.getCouponName(), orgId);
				if(!couponExit){
					logger.info("code is not exist test..."+ph);
					return ph;
				}
			}
		}else  if(ph.equalsIgnoreCase("GEN_updatePreferenceLink")){
			
			if(!user.getSubscriptionEnable()){
				
				logger.info("This user doesn't have user subscription settings." );
				return ph;
				
			}
	
			
			
			
		}
		
		
		
	}//for
	}catch(Exception e){
		logger.error("Exception ::" , e);
	}
	return null;
	
   }
	
	/**
	 * 
	 * @param content
	 * @param user
	 * @return
	 * 
	 * validates coupon and barcode expired or paused status and throws a message to user.
	 * 
	 */
	public static String validateCCPh(String content, Users user){
		
		try{
		Set<String> phSet = getPhSet(content);
		Set<String> ccPhSet = getBarcodePhset(content);
		if(ccPhSet != null){
			phSet.addAll(ccPhSet);
		}
		String couponIdStr = "";
				
		logger.info("coupon + barcode ph set: "+phSet);
		for(String ph : phSet){
			if(ph.startsWith("CC_")){
				
				String[] phStr = ph.split("_");
				
				couponIdStr += couponIdStr.trim().length() <= 0 ? phStr[1] : ","+phStr[1];
			}
			
		}//for
		
		if(couponIdStr.trim().length() == 0) return null;
		CouponsDao couponsDao= (CouponsDao)SpringUtil.getBean("couponsDao");
		Long orgId = user.getUserOrganization().getUserOrgId();
		
		List<Coupons> inValidCoupList = couponsDao.isExpireOrPauseCoupList(couponIdStr, orgId);
	
		if(inValidCoupList == null || inValidCoupList.size() <= 0){
			return null;
		}
			
		String inValidCoupNames = "";
		if(inValidCoupList != null && inValidCoupList.size() >0) {
			for (Coupons coupons : inValidCoupList) {
				inValidCoupNames += inValidCoupNames.trim().length() >0 ? ","+coupons.getCouponName() : coupons.getCouponName();
			}
			MessageUtil.setMessage(	"The discount code "+inValidCoupNames+" used in this campaign has either expired or in paused" + 
			"\n status. Please change the status of this discount code.",
					"color:red", "TOP");
			return "invalid";
		}
		
		
		}catch(Exception e){
			logger.error("Exception ::" , e);
		}
		return null;
		
	   }
	
	/**
	 * Fetches barcode placeholders
	 * @param content
	 * @return
	 */
	public static Set<String> getBarcodePhset(String content){
		
		try{
			//String imgPattern = "<img\\s+.*?((?:id\\s*=\\s*\\\"?CC_\\w\\\"?)).*?>";
			String imgPattern = "<img\\s+((?:id\\s*=\\s*\\\"?CC_\\w\\\"?)).*?>";
			String imgPattern1 = "<img\\s+((?:id\\s*=\\s*\\\"?&quot;CC_\\w\\\"?)).*?>";
			String idPattern = "<img .*?id\\s*?=\\\"?(.*?)\\\".*?>";
						
			Set<String> barcodePhSet = new HashSet<String>();
			
			Pattern pattern = Pattern.compile(imgPattern,Pattern.CASE_INSENSITIVE);
			Pattern pattern1 = Pattern.compile(imgPattern1,Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(content);
			Matcher matcher1 = pattern1.matcher(content);

			
			Boolean match =  matcher.find();
			Boolean match1 =  matcher1.find();

			while(match || match1) {
			
			
			//while(matcher.find()) {

				String imgtag = null;
				String idAttr = null;
				
				if(match){
					imgtag = matcher.group();
					match = matcher.find();
					//System.out.println("Img........"+imgtag);
				}	else if(match1){
					imgtag = matcher1.group();
					imgtag = imgtag.replace("&quot;", "");
					match1 = matcher1.find();
					//System.out.println("Imgtag........"+imgtag);
				}
					logger.info("Imgtag........"+imgtag);
					//imgtag = matcher.group();
					
					Pattern idp = Pattern.compile(idPattern, Pattern.CASE_INSENSITIVE);
					Matcher idm = idp.matcher(imgtag);
					
					while(idm.find()){
						idAttr = idm.group(1);
					}
					
					String ccPhTokens[] = idAttr.split("_");
					
					String phStr = ccPhTokens[0]+"_"+ccPhTokens[1]+"_"+ccPhTokens[2];
					barcodePhSet.add(phStr);
				
			}
			//logger.info("barcodePhSet......" + barcodePhSet);
			return barcodePhSet;
			
			}catch(Exception e){
				logger.error("***Exception in getting barcode placeholders*** ", e);
			}
				
		return null;
	}
	
	
	
	
  public static Set<String> getPhSet(String content) {
	
	content = content.replace("|^", "[").replace("^|", "]");
	String cfpattern = "\\[([^\\[]*?)\\]";
	Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
	Matcher m = r.matcher(content);

	String ph = null;
	Set<String> subjectSymbolSet = new HashSet<String>();

	try {
		while(m.find()) {

			ph = m.group(1); //.toUpperCase()
			//logger.info("Ph holder :" + ph);

			if(ph.startsWith("DATE_")){
				subjectSymbolSet.add(ph);
			}
			else if(ph.startsWith("CC_")){
				subjectSymbolSet.add(ph);
			}else if(ph.equalsIgnoreCase("GEN_updatePreferenceLink")){
				
				subjectSymbolSet.add(ph);
			}
			
		} // while
		
	} catch (Exception e) {
		logger.error("Exception while getting the symbol place holders ", e);
	}

	logger.info("symbol PH  Set : "+ subjectSymbolSet);

	return subjectSymbolSet;
  }

//************************************ Search Filters Start ********************************  
	public static Map<String, ArrayList<Object>> getLBFilterMap(Listbox filterLB, boolean clearFlag) {
		
		Listbox tempLB;
		Textbox tempTB;
		Intbox tempIntBox;
		Doublebox tempDoubleBox;
		Toolbarbutton tempTButton;
		
		MyDatebox tempMyDB;
		Map<String, ArrayList<Object>> filtersMap = new HashMap<String, ArrayList<Object>>();

		Collection<Component> hedderList = filterLB.getHeads();

		for (Component eachHeader : hedderList) {
			if(!(eachHeader instanceof Auxhead)) continue;
			List<Component> childList = eachHeader.getChildren();
			
			for (int i=0; i<childList.size(); i++ ) {
				Component eachAuxheader = childList.get(i);
				
				if(!(eachAuxheader instanceof Auxheader)) continue;
				List<Component> auxChildList = eachAuxheader.getChildren();
				if(auxChildList.size()<2) continue;
				
				//String filterToken="";
				ArrayList<Object> filterValArr = new ArrayList<Object>();
				
				String filterOpt="";
				SearchFilterEnum sfEnum = null;
				Field filterColField=null;
				
				for (int j=0; j<auxChildList.size(); j++ ) {
					Component eachComp = auxChildList.get(j);

					String tempVal=null;
					
					if(eachComp instanceof Toolbarbutton) {
						
						if(clearFlag) continue;//tempLB.setSelectedIndex(0); // For Clear selection
						
						tempTButton = (Toolbarbutton)eachComp;
						
						if(tempTButton.hasAttribute(LBFilterEventListener.FILTER_ENUM)) {
							sfEnum = (SearchFilterEnum)tempTButton.getAttribute(LBFilterEventListener.FILTER_ENUM);
							filterOpt = sfEnum.getTooltip();
							filterColField = ((Field)tempTButton.getAttribute("colName"));
							//if(filterColField!=null) filterColName = filterColField.getName();
							
							tempVal=null;
						}
					}
					else if(eachComp instanceof Listbox) {
						tempLB = (Listbox)eachComp;
						if(clearFlag) tempLB.setSelectedIndex(0); // For Clear selection
						
						tempVal = tempLB.getSelectedItem().getLabel().trim();
						if(j==0) {
							filterOpt = tempVal; // Search Option
							
						//	filterColField = ((Field)tempLB.getAttribute("colName"));
						//	if(filterColField!=null) filterColName = filterColField.getName();
							
						//	tempVal=null;
						//	continue;
						}
						
						if(!tempVal.isEmpty()) filterValArr.add(tempVal);
					}
					else if(eachComp instanceof Textbox) {
						tempTB = (Textbox)eachComp;
						if(clearFlag) {
							tempTB.setValue(""); // For Clear selection
						}
						else {
							tempVal = tempTB.getValue().trim();
							if(!tempVal.isEmpty()) filterValArr.add(tempVal); 
						}
					}
					else if(eachComp instanceof Intbox) {
						tempIntBox = (Intbox)eachComp;
						if(clearFlag) {
							tempIntBox.setValue(null); // For Clear selection
						}
						else {
							if(tempIntBox.getValue()!=null) filterValArr.add(tempIntBox.getValue()); 
						}
					}
					else if(eachComp instanceof Doublebox) {
						tempDoubleBox = (Doublebox)eachComp;
						if(clearFlag) {
							tempDoubleBox.setValue(null); // For Clear selection
						}
						else {
							if(tempDoubleBox.getValue()!=null) filterValArr.add(tempDoubleBox.getValue());
						}
					}
					else if(eachComp instanceof MyDatebox) {
						tempMyDB = (MyDatebox)eachComp;
						if(clearFlag) {
							tempMyDB.setText(null); // For Clear selection
						}
						else {
							if(tempMyDB.getText()!=null && !tempMyDB.getText().trim().isEmpty())
								filterValArr.add(tempMyDB.getClientValue());
						}
					}
					
				} // for

				if(filterValArr.size()>0 && sfEnum!=null) {
					filterValArr.add(0, sfEnum);
					filterValArr.add(1, filterColField);
					filtersMap.put(filterColField.getName(), filterValArr);
				}
				
			} // inner for
		} // outner for
		
		logger.debug("Return filtersMap="+filtersMap );
		
		return filtersMap;
		
	} // filterUserUR


	public static  void refreshModel1(LBFilterEventListener myLb, int activePage, boolean _needsTotalSizeUpdate) {
		
		//int _pageSize=myLb.getPaging().getPageSize();// 20;
		int _pageSize=0;
		if(myLb.getPaging() != null)
			_pageSize=myLb.getPaging().getPageSize();
		
		  if(myLb.getPaging1() == null) { Paging pagingTemp = myLb.getPaging();
		  pagingTemp.setId("myPaging1Id"); myLb.setPaging1(pagingTemp);
		  _pageSize=myLb.getPaging().getPageSize(); }
		 
		
		Map<String, ArrayList<Object>> filtersMap = Utility.getLBFilterMap(myLb.getFilterLB(), false);
		
		String filterQry = Utility.getFilterQuery(filtersMap, myLb.getQryPrefix());
		
		String sortQry = myLb.getSortQry();
		
		if(sortQry==null || sortQry.trim().isEmpty()){
			if(myLb.getFilterLB().getAttribute("defaultOrderBy")!=null && 
					!((String)(myLb.getFilterLB().getAttribute("defaultOrderBy"))).trim().isEmpty() ) {
				sortQry =" ORDER BY "+((String)(myLb.getFilterLB().getAttribute("defaultOrderBy"))).trim(); 
				sortQry += myLb.getFilterLB().getAttribute("defaultOrderByOrder")!=null && 
						!((String)(myLb.getFilterLB().getAttribute("defaultOrderByOrder"))).trim().isEmpty() ?" "+((String)(myLb.getFilterLB().getAttribute("defaultOrderByOrder"))).trim():" DESC";
			}else
			sortQry="";
		}
		PagingListModel model = new PagingListModel(activePage, _pageSize,  
				myLb.getQuery()+filterQry+sortQry, myLb.getCountQuery()+filterQry);
		
			
		myLb.getFilterLB().setAttribute("filteredQuery",myLb.getQuery()+filterQry);
		
		if(myLb.getFilterLB().getAttribute("eventSource") == null){
			myLb.getFilterLB().setAttribute("eventSource", "Filter");
		}
		model.setMultiple(true);
				
		if(_needsTotalSizeUpdate) {
			int _totalSize = model.getTotalSize();
			myLb.getPaging().setTotalSize(_totalSize);
			myLb.getPaging().setActivePage(activePage);
			myLb.getPaging1().setTotalSize(_totalSize);
			myLb.getPaging1().setActivePage(activePage);
			 
			_needsTotalSizeUpdate = false;
		}
		
		myLb.getFilterLB().setModel(model);
	}
	
	public static  void refreshModel(LBFilterEventListener myLb, int activePage, boolean _needsTotalSizeUpdate) {
		
		//int _pageSize=myLb.getPaging().getPageSize();// 20;
		int _pageSize=0;
		if(myLb.getPaging() != null)
			_pageSize=myLb.getPaging().getPageSize();
		
		Map<String, ArrayList<Object>> filtersMap = Utility.getLBFilterMap(myLb.getFilterLB(), false);
		
		String filterQry = Utility.getFilterQuery(filtersMap, myLb.getQryPrefix());
		
		String sortQry = myLb.getSortQry();
		
		if(sortQry==null || sortQry.trim().isEmpty()){
			if(myLb.getFilterLB().getAttribute("defaultOrderBy")!=null && 
					!((String)(myLb.getFilterLB().getAttribute("defaultOrderBy"))).trim().isEmpty() ) {
				sortQry =" ORDER BY "+((String)(myLb.getFilterLB().getAttribute("defaultOrderBy"))).trim(); 
				sortQry += myLb.getFilterLB().getAttribute("defaultOrderByOrder")!=null && 
						!((String)(myLb.getFilterLB().getAttribute("defaultOrderByOrder"))).trim().isEmpty() ?" "+((String)(myLb.getFilterLB().getAttribute("defaultOrderByOrder"))).trim():" DESC";
			}else
			sortQry="";
		}
		PagingListModel model = new PagingListModel(activePage, _pageSize,  
				myLb.getQuery()+filterQry+sortQry, myLb.getCountQuery()+filterQry);
		
			
		myLb.getFilterLB().setAttribute("filteredQuery",myLb.getQuery()+filterQry);
		
		if(myLb.getFilterLB().getAttribute("eventSource") == null){
			myLb.getFilterLB().setAttribute("eventSource", "Filter");
		}
		model.setMultiple(true);
				
		if(_needsTotalSizeUpdate) {
			int _totalSize = model.getTotalSize();
			myLb.getPaging().setTotalSize(_totalSize);
			myLb.getPaging().setActivePage(activePage);
			_needsTotalSizeUpdate = false;
		}
		
		myLb.getFilterLB().setModel(model);
	}
	
public static  void refreshGridModel(LBFilterEventListener myLb, int activePage, boolean _needsTotalSizeUpdate) {
		
	//int _pageSize=myLb.getPaging().getPageSize();// 20;
		int _pageSize=0;
		if(myLb.getPaging() != null)
			_pageSize=myLb.getPaging().getPageSize();
		
	
		String sortQry = myLb.getSortQry();
		
		if(sortQry==null || sortQry.trim().isEmpty()){
			if(myLb.getFilterGd().getAttribute("defaultOrderBy")!=null && 
					!((String)(myLb.getFilterGd().getAttribute("defaultOrderBy"))).trim().isEmpty() ) {
				sortQry =" ORDER BY "+((String)(myLb.getFilterGd().getAttribute("defaultOrderBy"))).trim(); 
				sortQry += myLb.getFilterGd().getAttribute("defaultOrderByOrder")!=null && 
						!((String)(myLb.getFilterGd().getAttribute("defaultOrderByOrder"))).trim().isEmpty() ?" "+((String)(myLb.getFilterGd().getAttribute("defaultOrderByOrder"))).trim():" DESC";
			}else
			sortQry="";
		}
		
		PagingListModel model = new PagingListModel(activePage, _pageSize,  
				myLb.getQuery()+sortQry, myLb.getCountQuery());
		
			
		myLb.getFilterGd().setAttribute("filteredQuery",myLb.getQuery());
		
		if(myLb.getFilterGd().getAttribute("eventSource") == null){
			myLb.getFilterGd().setAttribute("eventSource", "Filter");
		}
		model.setMultiple(true);
				
		if(_needsTotalSizeUpdate) {
			int _totalSize = model.getTotalSize();
			myLb.getPaging().setTotalSize(_totalSize);
			myLb.getPaging().setActivePage(activePage);
			_needsTotalSizeUpdate = false;
		}
		
		myLb.getFilterGd().setModel(model);
	}
	
	public static String getFilterQuery(Map<String, ArrayList<Object>> filtersMap, String qryPrefix) {
		String retStr="";
		try {
			if(filtersMap==null || filtersMap.isEmpty()) return retStr;
			
			ArrayList<Object> tempArr = null;
			Field tempField=null;
			String searchVal=null;
			SearchFilterEnum sfEnum;
			
			if(qryPrefix==null || qryPrefix.trim().isEmpty()) qryPrefix="";
			else qryPrefix = qryPrefix+".";
			
			Set<String> keySet = filtersMap.keySet();
			
			String dbColumn = "";
			for (String key : keySet) {
				tempArr = filtersMap.get(key);
				sfEnum = (SearchFilterEnum)tempArr.get(0);
				tempField = (Field)tempArr.get(1);
				searchVal = tempArr.get(2).toString();
				
				searchVal = StringEscapeUtils.escapeSql(searchVal);
				
				dbColumn = " "+qryPrefix+key+" ";
				if(tempField.getType().isAssignableFrom(Calendar.class)) {
					dbColumn = " DATE ("+dbColumn+") ";
				}
				//TODO need to consider Field type
				if(sfEnum.name().equals(SearchFilterEnum.CS.name()))		retStr += " AND "+dbColumn+" LIKE '%"+searchVal+"%' ";
				else if(sfEnum.name().equals(SearchFilterEnum.SW.name())) 	retStr += " AND "+dbColumn+" LIKE '"+searchVal+"%' ";
				else if(sfEnum.name().equals(SearchFilterEnum.EW.name())) 	retStr += " AND "+dbColumn+" LIKE '%"+searchVal+"' ";
				else if(sfEnum.name().equals(SearchFilterEnum.EQ.name()))	retStr += " AND "+dbColumn+"='"+searchVal+"' ";
				else if(sfEnum.name().equals(SearchFilterEnum.NE.name()))	retStr += " AND "+dbColumn+"!='"+searchVal+"' ";
				else if(sfEnum.name().equals(SearchFilterEnum.GT.name()))	retStr += " AND "+dbColumn+">'"+searchVal+"' ";
				else if(sfEnum.name().equals(SearchFilterEnum.LT.name()))	retStr += " AND "+dbColumn+"<'"+searchVal+"' ";
				else if(sfEnum.name().equals(SearchFilterEnum.GE.name()))	retStr += " AND "+dbColumn+">='"+searchVal+"' ";
				else if(sfEnum.name().equals(SearchFilterEnum.LE.name()))	retStr += " AND "+dbColumn+"<='"+searchVal+"' ";
				
			} // for
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		
		return retStr;
	} //
	
	
	public static void filterListboxByListitems(Listbox filterLB, boolean clearFlag) {
		
		Listbox tempLB;
		Textbox tempTB;
		Intbox tempIntBox;
		Doublebox tempDoubleBox;
		MyDatebox tempMyDB;
		Toolbarbutton tempTButton;
		
		Map<Integer, ArrayList<Object>> filtersMap = new HashMap<Integer, ArrayList<Object>>();

		Collection<Component> hedderList = filterLB.getHeads();

		for (Component eachHeader : hedderList) {
			if(!(eachHeader instanceof Auxhead)) continue;
			List<Component> childList = eachHeader.getChildren();
			
			for (int i=0; i<childList.size(); i++ ) {
				Component eachAuxheader = childList.get(i);
				
				if(!(eachAuxheader instanceof Auxheader)) continue;
				List<Component> auxChildList = eachAuxheader.getChildren();
				if(auxChildList.size()<2) continue;
				
				//String filterToken="";
				ArrayList<Object> filterValArr = new ArrayList<Object>();
				
				String filterOpt="";
				SearchFilterEnum sfEnum = null;
				
				for (int j=0; j<auxChildList.size(); j++ ) {
					Component eachComp = auxChildList.get(j);

					String tempVal=null;
					
					if(eachComp instanceof Toolbarbutton) {
						
						if(clearFlag) continue;//tempLB.setSelectedIndex(0); // For Clear selection
						
						tempTButton = (Toolbarbutton)eachComp;
						
						if(tempTButton.hasAttribute(LBFilterEventListener.FILTER_ENUM)) {
							sfEnum = (SearchFilterEnum)tempTButton.getAttribute(LBFilterEventListener.FILTER_ENUM);
							filterOpt = sfEnum.getTooltip();
							tempVal=null;
						}
					}
					else if(eachComp instanceof Listbox) {
						tempLB = (Listbox)eachComp;
						if(clearFlag) {
							tempLB.setSelectedIndex(0); // For Clear selection
						}
						else {
							tempVal = tempLB.getSelectedItem().getLabel().trim();
							if(!tempVal.isEmpty()) filterValArr.add(tempVal);
						}
					}
					else if(eachComp instanceof Textbox) {
						tempTB = (Textbox)eachComp;
						if(clearFlag) {
							tempTB.setValue(""); // For Clear selection
						}
						else {
							tempVal = tempTB.getValue().trim();
							if(!tempVal.isEmpty()) filterValArr.add(tempVal); 
						}
					}
					else if(eachComp instanceof Intbox) {
						tempIntBox = (Intbox)eachComp;
						if(clearFlag) {
							tempIntBox.setValue(null); // For Clear selection
						}
						else {
							if(tempIntBox.getValue()!=null) filterValArr.add(tempIntBox.getValue()); 
						}
					}
					else if(eachComp instanceof Doublebox) {
						tempDoubleBox = (Doublebox)eachComp;
						if(clearFlag) {
							tempDoubleBox.setValue(null); // For Clear selection
						}
						else {
							if(tempDoubleBox.getValue()!=null) filterValArr.add(tempDoubleBox.getValue());
						}
					}
					else if(eachComp instanceof MyDatebox) {
						tempMyDB = (MyDatebox)eachComp;
						if(clearFlag) {
							tempMyDB.setText(null); // For Clear selection
						}
						else {
							if(tempMyDB.getText()!=null && !tempMyDB.getText().trim().isEmpty())
								filterValArr.add(tempMyDB.getClientValue());
						}
					}
					
				} // for

				if(filterValArr.size() > 0 && sfEnum!=null) {
					filterValArr.add(0, sfEnum);
					filtersMap.put(i, filterValArr);
				}
				
			} // inner for
		} // outner for
		
		String filterShowGranted = (String)filterLB.getAttribute(Constants.FILTER_SHOW_GRANTED);
		logger.debug("filtersMap="+filtersMap+" ShowGranted="+filterShowGranted);
		
		List<Listitem> liList = filterLB.getItems();
		boolean enableFlag;
		ArrayList<Object> keyList = null;
		//String tempArr[] = null;
		for (Listitem eachLi : liList) {
			
			if(filterShowGranted!=null && filterShowGranted.equalsIgnoreCase("true") && !eachLi.isSelected()) {
				eachLi.setVisible(false);
				continue;
			}
				
			List<Component> compList = eachLi.getChildren();
			enableFlag = true;
			Listcell tempLc=null;
			String cellVal=null;
			
			for (int i = 0; i < compList.size(); i++) {
				keyList = filtersMap.get(i);
				if(keyList==null || keyList.size() < 2) continue;

				tempLc = (Listcell)compList.get(i);
				
				if(tempLc.getChildren().size()<=0) {
					cellVal = tempLc.getLabel().trim();
				}
				else {
					Component cellComp = tempLc.getFirstChild();
					if(cellComp instanceof Label) {
						cellVal = ((Label)cellComp).getValue();
					}
					else if(cellComp instanceof A) {
						cellVal = ((A)cellComp).getLabel();
					}
					else if(cellComp instanceof Textbox) {
						cellVal = ((Textbox)cellComp).getValue();
					}
				}
				
				if(keyList.get(1) instanceof Calendar) {
				    try {
				    	Calendar keyCal = (Calendar)keyList.get(1);
				    	Calendar valCal = Calendar.getInstance();
				    	
				    	SimpleDateFormat sdf = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STDATE);
						valCal.setTime(sdf.parse(cellVal));
						valCal.set(Calendar.HOUR, 0);
						valCal.set(Calendar.MINUTE, 0);
						valCal.set(Calendar.SECOND, 0);
						valCal.set(Calendar.MILLISECOND, 0);
						
						enableFlag = isFilterMatch(keyCal, valCal, (SearchFilterEnum)keyList.get(0));
					} catch (Exception e) {
						logger.error("Exception ::" , e);
					}
				}
				else if(keyList.get(1) instanceof Intbox) {
					logger.info("int box====");
				}
				else {
					enableFlag = isFilterMatch(keyList.get(1).toString(), cellVal, (SearchFilterEnum)keyList.get(0));
				}
				
				if(enableFlag==false) break;
			} // i
			
			eachLi.setVisible(enableFlag);
		}
		
	} // filterUserUR
	

		
	
	public static void filterGridByRows(Grid filterGrid, boolean clearFlag) {
		
		Listbox tempLB;
		Textbox tempTB;
		MyDatebox tempMyDB;
		Map<Integer, ArrayList<Object>> filtersMap = new HashMap<Integer, ArrayList<Object>>();

		Collection<Component> hedderList = filterGrid.getHeads();

		for (Component eachHeader : hedderList) {
			if(!(eachHeader instanceof Auxhead)) continue;
			List<Component> childList = eachHeader.getChildren();
			
			for (int i=0; i<childList.size(); i++ ) {
				Component eachAuxheader = childList.get(i);
				
				if(!(eachAuxheader instanceof Auxheader)) continue;
				List<Component> auxChildList = eachAuxheader.getChildren();
				if(auxChildList.size()<2) continue;
				
				//String filterToken="";
				ArrayList<Object> filterValArr = new ArrayList<Object>();
				
				String filterOpt="";
				
				for (int j=0; j<auxChildList.size(); j++ ) {
					Component eachComp = auxChildList.get(j);

					String tempVal=null;
					
					if(eachComp instanceof Listbox) {
						tempLB = (Listbox)eachComp;
						if(clearFlag && j!=0) tempLB.setSelectedIndex(0); // For Clear selection
						
						tempVal = tempLB.getSelectedItem().getLabel().trim();
						if(j==0) {
							filterOpt = tempVal; // Search Option
							tempVal=null;
							continue;
						}
						
						if(!tempVal.isEmpty()) filterValArr.add(tempVal);
					}
					else if(eachComp instanceof Textbox) {
						tempTB = (Textbox)eachComp;
						if(clearFlag) {
							tempTB.setValue(""); // For Clear selection
						}
						else {
							tempVal = tempTB.getValue().trim();
							if(!tempVal.isEmpty()) filterValArr.add(tempVal); 
						}
					}
					else if(eachComp instanceof MyDatebox) {
						tempMyDB = (MyDatebox)eachComp;
						if(clearFlag) {
							tempMyDB.setText(null); // For Clear selection
						}
						else {
							if(tempMyDB.getText()!=null && !tempMyDB.getText().trim().isEmpty())
								filterValArr.add(tempMyDB.getClientValue());
						}
					}
					
				} // for

				if(filterValArr.size()>0) {
					filterValArr.add(0, filterOpt);
					filtersMap.put(i, filterValArr);
				}
				
			} // inner for
		} // outner for
		
		logger.debug("filtersMap="+filtersMap);
		
		Rows rows = filterGrid.getRows();
		
		List<Component> rowList = rows.getChildren();
		
		//logger.info("rowList="+rowList);
		boolean enableFlag;
		ArrayList<Object> keyList = null;
		//String tempArr[] = null;
		for (Component eachComp : rowList) {
			
			//logger.info("eachComp="+eachComp);
			
			List<Component> compList = eachComp.getChildren();
			enableFlag = true;
			Component tempComp=null;
			String cellVal=null;
			
			for (int i = 0; i < compList.size(); i++) {
				
				//logger.info("compList.get(i)="+compList.get(i));
				
				keyList = filtersMap.get(i);
				if(keyList==null || keyList.size() < 2) continue;

				tempComp = compList.get(i);
				
				if(tempComp.getChildren().size()<=0) {
					cellVal = ((Label)tempComp).getValue().trim();
				}
				else {
					Component cellComp = tempComp.getFirstChild();
					if(cellComp instanceof Label) {
						cellVal = ((Label)cellComp).getValue();
					}
					else if(cellComp instanceof A) {
						cellVal = ((A)cellComp).getLabel();
					}
					else if(cellComp instanceof Textbox) {
						cellVal = ((Textbox)cellComp).getValue();
					}
				}
				
				if(keyList.get(1) instanceof Calendar) {
				    try {
				    	Calendar keyCal = (Calendar)keyList.get(1);
				    	Calendar valCal = Calendar.getInstance();
				    	
				    	SimpleDateFormat sdf = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STDATE);
						valCal.setTime(sdf.parse(cellVal));
						valCal.set(Calendar.HOUR, 0);
						valCal.set(Calendar.MINUTE, 0);
						valCal.set(Calendar.SECOND, 0);
						valCal.set(Calendar.MILLISECOND, 0);
						
						//TODO need to modify the search Enum
						enableFlag = isFilterMatch(keyCal, valCal, SearchFilterEnum.findByTooltip(keyList.get(0).toString()));
					} catch (Exception e) {
						logger.error("Exception ::" , e);
					}
				}
				else {
					//TODO need to modify the search Enum
					enableFlag = isFilterMatch(keyList.get(1).toString(), cellVal, SearchFilterEnum.findByTooltip(keyList.get(0).toString()));
				}
				
				if(enableFlag==false) break;
			} // i
			
			eachComp.setVisible(enableFlag);
		}
		
	} // filterUserUR
	

	public static boolean isFilterMatch(String key, String value, SearchFilterEnum opt) {
		//logger.debug("Key="+key+" , Val="+value+" ,Opt="+opt);
		boolean retFlag=false;
		try {
			if(key.trim().isEmpty()) return true;
			if (value==null || value.trim().isEmpty()) return false;
			
			if(opt.name().equals(SearchFilterEnum.CS.name())) {
				return value.toLowerCase().contains(key.toLowerCase());
			}
			else if(opt.name().equals(SearchFilterEnum.SW.name())) {
				return value.toLowerCase().startsWith(key.toLowerCase());
			}
			else if(opt.name().equals(SearchFilterEnum.EW.name())) {
				return value.toLowerCase().endsWith(key.toLowerCase());
			}
			else if(opt.name().equals(SearchFilterEnum.EQ.name())) {
				return value.toLowerCase().equals(key.toLowerCase());
			}
			else if(opt.name().equals(SearchFilterEnum.NE.name())) {
				return !value.toLowerCase().equals(key.toLowerCase());
			}
			else if(opt.name().equals(SearchFilterEnum.GT.name())) {
				return Double.parseDouble(value.trim()) > Double.parseDouble(key.trim());
			}
			else if(opt.name().equals(SearchFilterEnum.GE.name())) {
				return Double.parseDouble(value.trim()) >= Double.parseDouble(key.trim());
			}
			else if(opt.name().equals(SearchFilterEnum.LT.name())) {
				return Double.parseDouble(value.trim()) < Double.parseDouble(key.trim());
			}
			else if(opt.name().equals(SearchFilterEnum.LE.name())) {
				return Double.parseDouble(value.trim()) <= Double.parseDouble(key.trim());
			}
		
		} catch (Exception e) {
		}
		
		return retFlag;
	}
	
	public static boolean isFilterMatch(Calendar key, Calendar value, SearchFilterEnum opt) {
		//logger.debug("Key="+key+" , Val="+value+" ,Opt="+opt);
		boolean retFlag=false;
		try {
			if (key==null) return true;
			if (value==null) return false;
			
			if(opt.name().equals(SearchFilterEnum.EQ.name())) {
				return key.compareTo(value)==0;
			}
			else if(opt.name().equals(SearchFilterEnum.NE.name())) {
				return key.compareTo(value)!=0;
			}
			else if(opt.name().equals(SearchFilterEnum.GT.name())) {
				return value.after(key); 
			}
			else if(opt.name().equals(SearchFilterEnum.LT.name())) {
				return value.before(key);
			}
			else if(opt.name().equals(SearchFilterEnum.GE.name())) {
				return key.compareTo(value)==0 || value.after(key); 
			}
			else if(opt.name().equals(SearchFilterEnum.LE.name())) {
				return key.compareTo(value)==0 || value.before(key);
			}
		
		} catch (Exception e) {
		}
		
		return retFlag;
	}
	
//************************************ Search Filters End ********************************

	public  static TreeMap<String, List<String>> getPriorityMap(long userId,String mappingType, POSMappingDao posMappingDao) {
	  
	  try {
//		  logger.info("UserId is ::"+userId+" ::MappingType is ::"+mappingType);
		  TreeMap<String, List<String>> prioMap = new TreeMap<String, List<String>>();
		  
		  if(posMappingDao == null) {
			  
			  posMappingDao= (POSMappingDao)SpringUtil.getBean("posMappingDao");
		  }
				  
		  List<POSMapping> posmapList = posMappingDao.getPriorityMapByUserId(userId , "'"+mappingType+"'");
		  
		  if(posmapList == null || posmapList.size()==0) return null;
		  
		  for (POSMapping posMapObj : posmapList) {
			  	
			  String dataTypeStr = posMapObj.getDataType().toLowerCase();
			  
			  if(dataTypeStr.startsWith("date")) {
					dataTypeStr = "string"; 
			  }
			  
			
			  String priorStr = posMapObj.getCustomFieldName()+"|"+dataTypeStr;
			  
			  
			  List<String> valList = prioMap.get(""+posMapObj.getUniquePriority());
			  if(valList==null){
				  valList = new ArrayList<String>();
			  }
			  
			 if(!valList.contains(priorStr)) {
						valList.add(priorStr);
			}
			  
			 prioMap.put(""+posMapObj.getUniquePriority(), valList);
			 
			 
		  }
		  
		  return prioMap;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
		return null;
	}
	  
  } //getPriorityMap
  
  
  
  
  
  public static Contacts mergeContacts(Contacts inputContact, Contacts dbContact) {

      try {
		if(dbContact == null){
		      return inputContact;
		  }


		  if(!(inputContact.getEmailId()==null) && !(inputContact.getEmailId().trim().isEmpty())){
		      dbContact.setEmailId(inputContact.getEmailId());
		  }
		  if(!(inputContact.getFirstName()== null) &&!(inputContact.getFirstName().trim().isEmpty())){
		      dbContact.setFirstName(inputContact.getFirstName());
		  }
		  if(!(inputContact.getLastName()==null) &&!(inputContact.getLastName().trim().isEmpty())){
		      dbContact.setLastName(inputContact.getLastName());
		  }
		  if(!(inputContact.getAddressOne()==null) &&!(inputContact.getAddressOne().trim().isEmpty())){
		      dbContact.setAddressOne(inputContact.getAddressOne());
		  }
		  if(!(inputContact.getAddressTwo()==null) &&!(inputContact.getAddressTwo().trim().isEmpty())){
		      dbContact.setAddressTwo(inputContact.getAddressTwo());
		  }
		  if(!(inputContact.getCity()==null) &&!(inputContact.getCity().trim().isEmpty())){
		      dbContact.setCity(inputContact.getCity());
		  }
		  if(!(inputContact.getState()==null) &&!(inputContact.getState().trim().isEmpty())){
		      dbContact.setState(inputContact.getState());
		  }
		  if(!(inputContact.getCountry()==null) &&!(inputContact.getCountry().isEmpty())){
		      dbContact.setCountry(inputContact.getCountry());
		  }
		  if(!(inputContact.getUdf1()==null) &&!(inputContact.getUdf1().trim().isEmpty())){
		      dbContact.setUdf1(inputContact.getUdf1());
		  }
		  if(!(inputContact.getUdf2()==null) &&!(inputContact.getUdf2().trim().isEmpty())){
		      dbContact.setUdf2(inputContact.getUdf2());
		  }
		  if(!(inputContact.getUdf3()==null) &&!(inputContact.getUdf3().trim().isEmpty())){
		      dbContact.setUdf3(inputContact.getUdf3());
		  }
		  if(!(inputContact.getUdf4()==null) &&!(inputContact.getUdf4().trim().isEmpty())){
		      dbContact.setUdf4(inputContact.getUdf4());
		  }
		  if(!(inputContact.getUdf5()==null) &&!(inputContact.getUdf5().trim().isEmpty())){
		      dbContact.setUdf5(inputContact.getUdf5());
		  }
		  if(!(inputContact.getUdf6()==null) &&!(inputContact.getUdf6().trim().isEmpty())){
		      dbContact.setUdf6(inputContact.getUdf6());
		  }
		  if(!(inputContact.getUdf7()==null) &&!(inputContact.getUdf7().trim().isEmpty())){
		      dbContact.setUdf7(inputContact.getUdf7());
		  }
		  if(!(inputContact.getUdf8()==null) &&!(inputContact.getUdf8().trim().isEmpty())){
		      dbContact.setUdf8(inputContact.getUdf8());
		  }
		  if(!(inputContact.getUdf9()==null) &&!(inputContact.getUdf9().trim().isEmpty())){
		      dbContact.setUdf9(inputContact.getUdf9());
		  }
		  if(!(inputContact.getUdf10()==null) &&!(inputContact.getUdf10().trim().isEmpty())){
		      dbContact.setUdf10(inputContact.getUdf10());
		  }
		  if(!(inputContact.getUdf11()==null) &&!(inputContact.getUdf11().trim().isEmpty())){
		      dbContact.setUdf11(inputContact.getUdf11());
		  }
		  if(!(inputContact.getUdf12()==null) &&!(inputContact.getUdf12().trim().isEmpty())){
		      dbContact.setUdf12(inputContact.getUdf12());
		  }
		  if(!(inputContact.getUdf13()==null) &&!(inputContact.getUdf13().trim().isEmpty())){
		      dbContact.setUdf13(inputContact.getUdf13());
		  }
		  if(!(inputContact.getUdf14()==null) &&!(inputContact.getUdf14().trim().isEmpty())){
		      dbContact.setUdf14(inputContact.getUdf14());
		  }
		  if(!(inputContact.getUdf15()==null) &&!(inputContact.getUdf15().trim().isEmpty())){
		      dbContact.setUdf15(inputContact.getUdf15());
		  }
		  if(!(inputContact.getGender()==null) &&!(inputContact.getGender().trim().isEmpty())){
		      dbContact.setGender(inputContact.getGender());
		  }
		  if(!(inputContact.getBirthDay()==null)){
		      dbContact.setBirthDay(inputContact.getBirthDay());
		  }
		  if(!(inputContact.getAnniversary()==null)){
		      dbContact.setAnniversary(inputContact.getAnniversary());
		  }
		  if(!(inputContact.getZip()==null) &&!(inputContact.getZip().trim().isEmpty())){
		      dbContact.setZip(inputContact.getZip());
		  }
		  if(!(inputContact.getMobilePhone()==null) &&!(inputContact.getMobilePhone().trim().isEmpty())){
		      dbContact.setMobilePhone(inputContact.getMobilePhone());
		  }
		  if(!(inputContact.getExternalId()==null) &&!(inputContact.getExternalId().trim().isEmpty())){
		      dbContact.setExternalId(inputContact.getExternalId());
		  }
		  if(!(inputContact.getHomeStore()==null) &&!(inputContact.getHomeStore().trim().isEmpty())){
		      dbContact.setHomeStore(inputContact.getHomeStore());
		  }
		  if(!(inputContact.getHomePhone()==null) &&!(inputContact.getHomePhone().trim().isEmpty())){
		      dbContact.setHomePhone(inputContact.getHomePhone());
		  }
		  logger.info("inputContact.getSubsidiaryNumber()==null"+inputContact.getSubsidiaryNumber()==null);
		  if(!(inputContact.getSubsidiaryNumber()==null) &&!(inputContact.getSubsidiaryNumber().trim().isEmpty())){
		      dbContact.setSubsidiaryNumber((inputContact.getSubsidiaryNumber()));//APP-2327
		  }
		  return dbContact;
	} catch (Exception e) {
		logger.error("Exception ::" , e);
		return dbContact;
	}
  }

  public static Contacts mergeContactsFromWebForm(Contacts inputContact, Contacts dbContact) {

      try {
		if(dbContact == null){
		      return inputContact;
		  }


		  if((dbContact.getEmailId() == null || dbContact.getEmailId().isEmpty()) && !(inputContact.getEmailId()==null) && !(inputContact.getEmailId().trim().isEmpty())){
		      dbContact.setEmailId(inputContact.getEmailId());
		  }
		  if((dbContact.getFirstName() == null || dbContact.getFirstName().isEmpty()) && !(inputContact.getFirstName()== null) &&!(inputContact.getFirstName().trim().isEmpty())){
		      dbContact.setFirstName(inputContact.getFirstName());
		  }
		  if((dbContact.getLastName() == null || dbContact.getLastName().isEmpty()) && !(inputContact.getLastName()==null) &&!(inputContact.getLastName().trim().isEmpty())){
		      dbContact.setLastName(inputContact.getLastName());
		  }
		  if((dbContact.getAddressOne() == null || dbContact.getAddressOne().isEmpty()) && !(inputContact.getAddressOne()==null) &&!(inputContact.getAddressOne().trim().isEmpty())){
		      dbContact.setAddressOne(inputContact.getAddressOne());
		  }
		  if((dbContact.getAddressTwo() == null || dbContact.getAddressTwo().isEmpty()) && !(inputContact.getAddressTwo()==null) &&!(inputContact.getAddressTwo().trim().isEmpty())){
		      dbContact.setAddressTwo(inputContact.getAddressTwo());
		  }
		  if((dbContact.getCity() == null || dbContact.getCity().isEmpty()) && !(inputContact.getCity()==null) &&!(inputContact.getCity().trim().isEmpty())){
		      dbContact.setCity(inputContact.getCity());
		  }
		  if((dbContact.getState() == null || dbContact.getState().isEmpty()) && !(inputContact.getState()==null) &&!(inputContact.getState().trim().isEmpty())){
		      dbContact.setState(inputContact.getState());
		  }
		  if((dbContact.getCountry() == null || dbContact.getCountry().isEmpty()) && !(inputContact.getCountry()==null) &&!(inputContact.getCountry().isEmpty())){
		      dbContact.setCountry(inputContact.getCountry());
		  }
		  if((dbContact.getUdf1() == null || dbContact.getUdf1().isEmpty()) && !(inputContact.getUdf1()==null) &&!(inputContact.getUdf1().trim().isEmpty())){
		      dbContact.setUdf1(inputContact.getUdf1());
		  }
		  if((dbContact.getUdf2() == null || dbContact.getUdf2().isEmpty()) && !(inputContact.getUdf2()==null) &&!(inputContact.getUdf2().trim().isEmpty())){
		      dbContact.setUdf2(inputContact.getUdf2());
		  }
		  if((dbContact.getUdf3() == null || dbContact.getUdf3().isEmpty()) && !(inputContact.getUdf3()==null) &&!(inputContact.getUdf3().trim().isEmpty())){
		      dbContact.setUdf3(inputContact.getUdf3());
		  }
		  if((dbContact.getUdf4() == null || dbContact.getUdf4().isEmpty()) && !(inputContact.getUdf4()==null) &&!(inputContact.getUdf4().trim().isEmpty())){
		      dbContact.setUdf4(inputContact.getUdf4());
		  }
		  if((dbContact.getUdf5() == null || dbContact.getUdf5().isEmpty()) && !(inputContact.getUdf5()==null) &&!(inputContact.getUdf5().trim().isEmpty())){
		      dbContact.setUdf5(inputContact.getUdf5());
		  }
		  if((dbContact.getUdf6() == null || dbContact.getUdf6().isEmpty()) && !(inputContact.getUdf6()==null) &&!(inputContact.getUdf6().trim().isEmpty())){
		      dbContact.setUdf6(inputContact.getUdf6());
		  }
		  if((dbContact.getUdf7() == null || dbContact.getUdf7().isEmpty()) && !(inputContact.getUdf7()==null) &&!(inputContact.getUdf7().trim().isEmpty())){
		      dbContact.setUdf7(inputContact.getUdf7());
		  }
		  if((dbContact.getUdf8() == null || dbContact.getUdf8().isEmpty()) && !(inputContact.getUdf8()==null) &&!(inputContact.getUdf8().trim().isEmpty())){
		      dbContact.setUdf8(inputContact.getUdf8());
		  }
		  if((dbContact.getUdf9() == null || dbContact.getUdf9().isEmpty()) && !(inputContact.getUdf9()==null) &&!(inputContact.getUdf9().trim().isEmpty())){
		      dbContact.setUdf9(inputContact.getUdf9());
		  }
		  if((dbContact.getUdf10() == null || dbContact.getUdf10().isEmpty()) && !(inputContact.getUdf10()==null) &&!(inputContact.getUdf10().trim().isEmpty())){
		      dbContact.setUdf10(inputContact.getUdf10());
		  }
		  if((dbContact.getUdf11() == null || dbContact.getUdf11().isEmpty()) && !(inputContact.getUdf11()==null) &&!(inputContact.getUdf11().trim().isEmpty())){
		      dbContact.setUdf11(inputContact.getUdf11());
		  }
		  if((dbContact.getUdf12() == null || dbContact.getUdf12().isEmpty()) && !(inputContact.getUdf12()==null) &&!(inputContact.getUdf12().trim().isEmpty())){
		      dbContact.setUdf12(inputContact.getUdf12());
		  }
		  if((dbContact.getUdf13() == null || dbContact.getUdf13().isEmpty()) && !(inputContact.getUdf13()==null) &&!(inputContact.getUdf13().trim().isEmpty())){
		      dbContact.setUdf13(inputContact.getUdf13());
		  }
		  if((dbContact.getUdf14() == null || dbContact.getUdf14().isEmpty()) && !(inputContact.getUdf14()==null) &&!(inputContact.getUdf14().trim().isEmpty())){
		      dbContact.setUdf14(inputContact.getUdf14());
		  }
		  if((dbContact.getUdf15() == null || dbContact.getUdf15().isEmpty()) && !(inputContact.getUdf15()==null) &&!(inputContact.getUdf15().trim().isEmpty())){
		      dbContact.setUdf15(inputContact.getUdf15());
		  }
		  if((dbContact.getGender() == null || dbContact.getGender().isEmpty()) && !(inputContact.getGender()==null) &&!(inputContact.getGender().trim().isEmpty())){
		      dbContact.setGender(inputContact.getGender());
		  }
		  if(dbContact.getBirthDay() == null && !(inputContact.getBirthDay()==null)){
		      dbContact.setBirthDay(inputContact.getBirthDay());
		  }
		  if(dbContact.getAnniversary() == null && !(inputContact.getAnniversary()==null)){
		      dbContact.setAnniversary(inputContact.getAnniversary());
		  }
		  if((dbContact.getZip() == null || dbContact.getZip().isEmpty()) && !(inputContact.getZip()==null) &&!(inputContact.getZip().trim().isEmpty())){
		      dbContact.setZip(inputContact.getZip());
		  }
		  if((dbContact.getMobilePhone() == null || dbContact.getMobilePhone().isEmpty()) && !(inputContact.getMobilePhone()==null) &&!(inputContact.getMobilePhone().trim().isEmpty())){
		      dbContact.setMobilePhone(inputContact.getMobilePhone());
		  }
		  if((dbContact.getExternalId() == null || dbContact.getExternalId().isEmpty()) && !(inputContact.getExternalId()==null) &&!(inputContact.getExternalId().trim().isEmpty())){
		      dbContact.setExternalId(inputContact.getExternalId());
		  }
		  if((dbContact.getHomeStore() == null || dbContact.getHomeStore().isEmpty()) && !(inputContact.getHomeStore()==null) &&!(inputContact.getHomeStore().trim().isEmpty())){
		      dbContact.setHomeStore(inputContact.getHomeStore());
		  }
		  if((dbContact.getHomePhone() == null || dbContact.getHomePhone().isEmpty()) && !(inputContact.getHomePhone()==null) &&!(inputContact.getHomePhone().trim().isEmpty())){
		      dbContact.setHomePhone(inputContact.getHomePhone());
		  }


		  return dbContact;
	} catch (Exception e) {
		logger.error("Exception ::" , e);
		return dbContact;
	}
  }
  

  public static void setContactFieldsOnDeletion(Contacts contact){
	  
	   if(contact.getMlBits().longValue() == 0l){
	  //contact.setUsers(null);
	  //contact.setCreatedDate(null);
	  contact.setPurged(false);
	  contact.setEmailStatus(Constants.CONT_STATUS_PURGE_PENDING);
	  contact.setLastStatusChange(null);
	  contact.setLastMailDate(null);
	  contact.setOptin((byte)0);
	  contact.setSubscriptionType(null);
	  contact.setOptinMedium(null);
	  //contact.setHomeStore(null);
	  //contact.setLoyaltyCustomer((byte)0);
	  //TODO what to do with mobile optin
	  //contact.setLastSMSDate(null);
	  //contact.setMobileOptin(false);
	  }
	   
  } //setContactFieldsOnDeletion
  
  
  public static void setUserDefaultMapping(Long userId) {
	  try {
		  List<POSMapping>  contMapList = new ArrayList<POSMapping>();
		  POSMappingDao posMappingDao= (POSMappingDao)SpringUtil.getBean("posMappingDao");
		  POSMappingDaoForDML posMappingDaoForDML= (POSMappingDaoForDML)SpringUtil.getBean("posMappingDaoForDML");
		  Set<String> set = deafultContMap.keySet();
		  
		  
		  for (String genFieldLbl : set) {
				  POSMapping posMapping = new POSMapping();
				  
				  //POS Attr
				  posMapping.setPosAttribute(genFieldLbl);
		//				posMapping.setDisplayLabel(genFieldLbl);
				  String optCulFieldStr = "";
					  
				  optCulFieldStr = deafultContMap.get(genFieldLbl);
				  
				  //Display Label
				  if(optCulFieldStr.equals("Email")){
					  posMapping.setDisplayLabel("Email ID");
					  posMapping.setUniquePriority(2);
				  }
				  else if(optCulFieldStr.equals("Mobile")) {
					  posMapping.setDisplayLabel("Mobile Phone");
					  posMapping.setUniquePriority(3);
				  }
				  else {
					  posMapping.setDisplayLabel(optCulFieldStr);
				  }
				  
				  //CustField Name
				  posMapping.setCustomFieldName(deafultContMap.get(genFieldLbl));
				  
				  //Digital Receipt Attr
				  
				  String drKeyFieldStr = POSFieldsEnum.findByOCAttribute(optCulFieldStr).getDrKeyField();
				  String[] drKeyFieldArr = drKeyFieldStr.split(",");
				  posMapping.setDigitalReceiptAttribute(drKeyFieldArr[0]);
				  
				  
				  //DataTypes
				  if(genFieldLbl.equals("BirthDay") || genFieldLbl.equals("Anniversary")
						  || genFieldLbl.equals("CreatedDate")) {
					  posMapping.setDataType("Date(MM/dd/yyyy HH:mm:ss)");
					  posMapping.setDrDataType("Date(MM/dd/yyyy HH:mm:ss)");
				  }else {
					  posMapping.setDataType("String");
					  posMapping.setDrDataType("String");
				  }
				  
				  if(genFieldLbl.equals("CustomerID")) {
					  posMapping.setUniquePriority(1);
				  }
				  //Mapping Type
				  posMapping.setMappingType(Constants.POS_MAPPING_TYPE_CONTACTS);
				  
				  //Set user
				  posMapping.setUserId(userId);
				  contMapList.add(posMapping);
		  }
		  posMappingDaoForDML.saveByCollection(contMapList);
	  } catch (Exception e) {
		  // TODO Auto-generated catch block
		  logger.error("Exception ::" , e);
	  }
	  
  } //defaultGenFieldMapp
  
  public static long getMlsBit(Set<MailingList> mlSet) {
		long retVal=0l;
		try {
			Long userId=null;
			
			Iterator<MailingList> mlIt = mlSet.iterator();
			MailingList mailingList = null;
			
			while(mlIt.hasNext()) {
			
				mailingList = mlIt.next();
				
				if(userId==null) {
					userId = mailingList.getUsers().getUserId();
				}
				else if(userId.longValue() != mailingList.getUsers().getUserId().longValue()) {
					return -1; // Different user ids
				}
				
				retVal = retVal | mailingList.getMlBit();
			} // while
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return 0;
		}
		return retVal;
	}
  
  public static String getMultiUserQry(Set<MailingList> mlSet, long mlsbit) {
	  String retQry=null;
	  if(mlsbit != -1) return null;
	  
	  	String mlIds="";
	  	Iterator<MailingList> mlIt = mlSet.iterator();
		while(mlIt.hasNext()) {
			if(!mlIds.isEmpty()) mlIds +=",";
			mlIds += mlIt.next().getListId().longValue();
		} // 
		
	  retQry=" FROM Contacts c, MailingList ml WHERE c.users = ml.users " +
				" AND ml.listId IN ("+mlIds+") "+
 				" AND bitwise_and(c.mlBits, ml.mlBit)>0 ";
	  return retQry;
  }
  
  public static String getIdsStrFromList(List<Long> mlList) {
	  
	  if(mlList == null || mlList.size() == 0 ) return null;
	  
	  String idStr = "";
	  
	  for (Long id : mlList) {
		
		  if(id == null) continue;
		  
		  if(idStr.length() > 0) idStr += ",";
		  idStr += id.longValue() ;
		  
		  
		  
	}
	  return idStr;
	  
  }
  
 /* public static Set<String> getDigitalReceiptsAttr(){
	  
	  String drFile = PropertyUtil.getPropertyValue("templateParent");
	  
	  Set<String> drList = new HashSet<String>();
	  FileReader fileReader = null;
	try {
		fileReader = new FileReader(drFile+"/json_format_1");
	} catch (FileNotFoundException e1) {
		logger.error("Exception ::", e1);
	}
	if(fileReader == null) return null;
	  JSONObject jsonObject = (JSONObject)JSONValue.parse(fileReader);
		
	  JSONArray jsonArray = null;

	  try{
		 Set<Map.Entry<String, Object>> jsonKeyValue = jsonObject.entrySet();
		 for(Entry<String, Object> entry : jsonKeyValue){
			 String key = entry.getKey();
			 Object object = entry.getValue();
			 
			 if(object instanceof JSONArray){
				jsonArray = (JSONArray)object;
				Iterator<Object> objIter = jsonArray.iterator();
				while(objIter.hasNext()){
					Object obj = objIter.next();
					JSONObject jsnObj1 = (JSONObject)obj;
					 Set<Map.Entry<String, Object>> KeyValue = jsnObj1.entrySet();
					 for(Entry<String, Object> ent : KeyValue){
						 String key1 = ent.getKey();
						 drList.add(key+"::"+key1);
					 }
			    }
			 }
			 if(object instanceof JSONObject){
				 JSONObject jsnObj = (JSONObject)object;
				 Set<Map.Entry<String, Object>> KeyValue = jsnObj.entrySet();
				 for(Entry<String, Object> ent : KeyValue){
					 String key1 = ent.getKey();
					 drList.add(key+"::"+key1);
				 }
			 
			 }
			
	     }
		}catch(Exception e){
			 logger.error("Exception ::" , e);
			 return null;
		 }
	  
	  return drList;
	}*/
  
 public static Set<String> getDigitalReceiptsAttr(){
	  
	  String drFile = PropertyUtil.getPropertyValue("templateParent");
	  
	  Set<String> drList = new HashSet<String>();
	  FileReader fileReader = null;
	try {
		fileReader = new FileReader(drFile+"/json_format_live");
	} catch (FileNotFoundException e1) {
		logger.error("Exception ::", e1);
	}
	if(fileReader == null) return null;
	  JSONObject jsonMainObject = (JSONObject)JSONValue.parse(fileReader);
		
	  if(jsonMainObject != null) {
  		if(!jsonMainObject.containsKey("Items")) {
  			jsonMainObject = (JSONObject)jsonMainObject.get("Body");
  		}
  	} else {
  		logger.debug("*** Main Object is NUll ***");
  		//jsonMessage = "Unable to parse the JSON request .";
  		return null;
  	}
	  JSONArray jsonArray = null;

	  try{
		 Set<Map.Entry<String, Object>> jsonKeyValue = jsonMainObject.entrySet();
		 for(Entry<String, Object> entry : jsonKeyValue){
			 String key = entry.getKey();
			 Object object = entry.getValue();
			 
			 if(object instanceof JSONArray){
				jsonArray = (JSONArray)object;
				Iterator<Object> objIter = jsonArray.iterator();
				while(objIter.hasNext()){
					Object obj = objIter.next();
					JSONObject jsnObj1 = (JSONObject)obj;
					 Set<Map.Entry<String, Object>> KeyValue = jsnObj1.entrySet();
					 for(Entry<String, Object> ent : KeyValue){
						 String key1 = ent.getKey();
						 drList.add(key+"::"+key1);
					 }
			    }
			 }
			 if(object instanceof JSONObject){
				 JSONObject jsnObj = (JSONObject)object;
				 Set<Map.Entry<String, Object>> KeyValue = jsnObj.entrySet();
				 for(Entry<String, Object> ent : KeyValue){
					 String key1 = ent.getKey();
					 drList.add(key+"::"+key1);
				 }
			 
			 }
			
	     }
		}catch(Exception e){
			 logger.error("Exception ::" , e);
			 return null;
		 }
	  
	  return drList;
	}
  
 public static SkuFile mergeSkuFile(SkuFile inputSkuFile, SkuFile dbSkuFile) {

     
		 if(dbSkuFile == null){
		      return inputSkuFile;
		  }

		  if(!(inputSkuFile.getListPrice()==null)){
		      dbSkuFile.setListPrice(inputSkuFile.getListPrice());
		  }
		  if(!(inputSkuFile.getDescription()==null) && !(inputSkuFile.getDescription().trim().isEmpty())){
		      dbSkuFile.setDescription(inputSkuFile.getDescription());
		  }
		  if(!(inputSkuFile.getSku()==null) && !(inputSkuFile.getSku().trim().isEmpty())){
		      dbSkuFile.setSku(inputSkuFile.getSku());
		  }
		  if(!(inputSkuFile.getItemCategory()==null) && !(inputSkuFile.getItemCategory().trim().isEmpty())){
		      dbSkuFile.setItemCategory(inputSkuFile.getItemCategory());
		  }
		  if(!(inputSkuFile.getItemSid()==null) && !(inputSkuFile.getItemSid().trim().isEmpty())){
		      dbSkuFile.setItemSid(inputSkuFile.getItemSid());
		  }
		  if(!(inputSkuFile.getStoreNumber()==null) && !(inputSkuFile.getStoreNumber().trim().isEmpty())){
		      dbSkuFile.setStoreNumber(inputSkuFile.getStoreNumber());
		  }
		  
		  if(!(inputSkuFile.getClassCode()==null) && !(inputSkuFile.getClassCode().trim().isEmpty())){
		      dbSkuFile.setClassCode(inputSkuFile.getClassCode());
		  }
		  
		  if(!(inputSkuFile.getSubClassCode()==null) && !(inputSkuFile.getSubClassCode().trim().isEmpty())){
		      dbSkuFile.setSubClassCode(inputSkuFile.getSubClassCode());
		  }
		  
		  if(!(inputSkuFile.getDepartmentCode()==null) && !(inputSkuFile.getDepartmentCode().trim().isEmpty())){
		      dbSkuFile.setDepartmentCode(inputSkuFile.getDepartmentCode());
		  }
		  
		  if(!(inputSkuFile.getVendorCode()==null) && !(inputSkuFile.getVendorCode().trim().isEmpty())){
		      dbSkuFile.setVendorCode(inputSkuFile.getVendorCode());
		  }
		  
		  if(!(inputSkuFile.getDCS()==null) && !(inputSkuFile.getDCS().trim().isEmpty())){
		      dbSkuFile.setDCS(inputSkuFile.getDCS());
		  }
		  
		  if(!(inputSkuFile.getUdf1()==null) &&!(inputSkuFile.getUdf1().trim().isEmpty())){
			  dbSkuFile.setUdf1(inputSkuFile.getUdf1());
		  }
		  if(!(inputSkuFile.getUdf2()==null) &&!(inputSkuFile.getUdf2().trim().isEmpty())){
			  dbSkuFile.setUdf2(inputSkuFile.getUdf2());
		  }
		  if(!(inputSkuFile.getUdf3()==null) &&!(inputSkuFile.getUdf3().trim().isEmpty())){
			  dbSkuFile.setUdf3(inputSkuFile.getUdf3());
		  }
		  if(!(inputSkuFile.getUdf4()==null) &&!(inputSkuFile.getUdf4().trim().isEmpty())){
			  dbSkuFile.setUdf4(inputSkuFile.getUdf4());
		  }
		  if(!(inputSkuFile.getUdf5()==null) &&!(inputSkuFile.getUdf5().trim().isEmpty())){
			  dbSkuFile.setUdf5(inputSkuFile.getUdf5());
		  }
		  if(!(inputSkuFile.getUdf6()==null) &&!(inputSkuFile.getUdf6().trim().isEmpty())){
			  dbSkuFile.setUdf6(inputSkuFile.getUdf6());
		  }
		  if(!(inputSkuFile.getUdf7()==null) &&!(inputSkuFile.getUdf7().trim().isEmpty())){
			  dbSkuFile.setUdf7(inputSkuFile.getUdf7());
		  }
		  if(!(inputSkuFile.getUdf8()==null) &&!(inputSkuFile.getUdf8().trim().isEmpty())){
			  dbSkuFile.setUdf8(inputSkuFile.getUdf8());
		  }
		  if(!(inputSkuFile.getUdf9()==null) &&!(inputSkuFile.getUdf9().trim().isEmpty())){
			  dbSkuFile.setUdf9(inputSkuFile.getUdf9());
		  }
		  if(!(inputSkuFile.getUdf10()==null) &&!(inputSkuFile.getUdf10().trim().isEmpty())){
			  dbSkuFile.setUdf10(inputSkuFile.getUdf10());
		  }
		  if(!(inputSkuFile.getUdf11()==null) &&!(inputSkuFile.getUdf11().trim().isEmpty())){
			  dbSkuFile.setUdf11(inputSkuFile.getUdf11());
		  }
		  if(!(inputSkuFile.getUdf12()==null) &&!(inputSkuFile.getUdf12().trim().isEmpty())){
			  dbSkuFile.setUdf12(inputSkuFile.getUdf12());
		  }
		  if(!(inputSkuFile.getUdf13()==null) &&!(inputSkuFile.getUdf13().trim().isEmpty())){
			  dbSkuFile.setUdf13(inputSkuFile.getUdf13());
		  }
		  if(!(inputSkuFile.getUdf14()==null) &&!(inputSkuFile.getUdf14().trim().isEmpty())){
			  dbSkuFile.setUdf14(inputSkuFile.getUdf14());
		  }
		  if(!(inputSkuFile.getUdf15()==null) &&!(inputSkuFile.getUdf15().trim().isEmpty())){
			  dbSkuFile.setUdf15(inputSkuFile.getUdf15());
		  }
     
		  return dbSkuFile;
 }
 
	public static RetailProSalesCSV mergeSalesFile(RetailProSalesCSV inputSalesFile, RetailProSalesCSV dbSalesFile) {

		if (dbSalesFile == null) {	return inputSalesFile;	}

		if (!(inputSalesFile.getDocSid() == null)) {
			dbSalesFile.setDocSid(inputSalesFile.getDocSid());
		}
		if (!(inputSalesFile.getQuantity() == null)) {
			dbSalesFile.setQuantity(inputSalesFile.getQuantity());
		}
		if (!(inputSalesFile.getSalesDate() == null)) {
			dbSalesFile.setSalesDate(inputSalesFile.getSalesDate());
		}
		if (!(inputSalesFile.getSalesPrice() == null)) {
			dbSalesFile.setSalesPrice(inputSalesFile.getSalesPrice());
		}
		if (!(inputSalesFile.getTenderType() == null) && !(inputSalesFile.getTenderType().trim().isEmpty())) {
			dbSalesFile.setTenderType(inputSalesFile.getTenderType());
		}
		if (!(inputSalesFile.getSku() == null) && !(inputSalesFile.getSku().trim().isEmpty())) {
			dbSalesFile.setSku(inputSalesFile.getSku());
		}
		if (!(inputSalesFile.getCustomerId() == null) && !(inputSalesFile.getCustomerId().trim().isEmpty())) {
			dbSalesFile.setCustomerId(inputSalesFile.getCustomerId());
		}
		if (!(inputSalesFile.getPromoCode() == null) && !(inputSalesFile.getPromoCode().trim().isEmpty())) {
			dbSalesFile.setPromoCode(inputSalesFile.getPromoCode());
		}
		if (!(inputSalesFile.getTax() == null)) {
			dbSalesFile.setTax(inputSalesFile.getTax());
		}
		if (!(inputSalesFile.getRecieptNumber() == null)) {
			dbSalesFile.setRecieptNumber(inputSalesFile.getRecieptNumber());
		}
		if (!(inputSalesFile.getItemSid() == null) 	&& !(inputSalesFile.getItemSid().trim().isEmpty())) {
			dbSalesFile.setItemSid(inputSalesFile.getItemSid());
		}
		if (!(inputSalesFile.getStoreNumber() == null) 	&& !(inputSalesFile.getStoreNumber().trim().isEmpty())) {
			dbSalesFile.setStoreNumber(inputSalesFile.getStoreNumber());
		}

		return dbSalesFile;
	}
 
	
	public static boolean validateDate(String data, String dateFormat){
		boolean isValid = false;
		try{
			
			//String usdateFormat = PropertyUtil.getPropertyValue("customFiledDateFormat"); //mm/dd/yyyy
			if(data != null && dateFormat != null) {
				
				DateFormat format = new SimpleDateFormat(dateFormat);
//				format.setLenient(false);
				Date date = format.parse(data);
				isValid = true;
			}
		}catch(java.lang.NumberFormatException nfe){
			isValid = false;
		}catch (java.text.ParseException pe){
			isValid = false;
		}catch (IllegalArgumentException e) {
	    	isValid = false;
	    	logger.error("Exception ::" , e);
	    }catch(Exception e){
			isValid = false;
		}
		return isValid;
	} // validateDate
	
	
	// Validate pin
	
	public static boolean validateZipCode(String zip, String countryType){
		
		/*if(zip == null && zip.length() == 0) {
			
			return false;
		}*/
		
	//	if(zipValidateMap.containsKey(countryType)){
			
		logger.info("country type is =="+countryType);
		String zipRegexValue = zipValidateMap.get(countryType);
		String pattern = zipRegexValue;
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(zip);
		return m.matches();
		
	//	}else return true;
	}
	
	/*@SuppressWarnings("null")
	public static boolean validateZipCode(String zip, String countryType){
		logger.info("------ 1 ----------------");
		if(zip == null && zip.length() == 0) {
			
			return false;
		}
		//logger.info("Username is ===>>"+currentUser.getUserName());
		logger.info("country type is == "+countryType);
		
	//	Users users = null;
		if(countryType.equalsIgnoreCase("US")){
			logger.info("country type is US== "+countryType);
			String pattern = "^[0-9]{5}(?:-[0-9]{4})?$";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(zip);
			return m.matches();
			
		}
		else if(countryType.equalsIgnoreCase("India")){
			logger.info("country type is India== "+countryType);
			String pattern = "\\d{6}";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(zip);
			return m.matches();
		}
		else if(countryType.equalsIgnoreCase("Pakistan")){
			logger.info("country type is Pakistan== "+countryType);
			String pattern = "\\d{5}";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(zip);
			return m.matches();
		}
		else if(countryType.equalsIgnoreCase("Canada")){
			logger.info("country type is Canada== "+countryType);
			String pattern = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(zip);
			return m.matches();
		}
		else if(countryType.equalsIgnoreCase("UAE")){
			logger.info("country type is UAE== "+countryType);
			String pattern = "\\d{6}";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(zip);
			return m.matches();
		}
		
		String pattern = "\\d{6}(-\\d{5})?";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(zip);
		return m.matches();
		return false;
	}*/
	
	
	public static Set<String> getDRConTentPlaceHolder(String content) {
		
		content = content.replace("|^", "[").replace("^|", "]");
		
		String cfpattern = "\\[([^\\[]*?)\\]";
		Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(content);

		String ph = null;
		Set<String> subjectSymbolSet = new HashSet<String>();

		try {
			while(m.find()) {

				ph = m.group(1); //.toUpperCase()
				////logger.info("Ph holder :" + ph);

				if(ph.startsWith(Constants.DATE_PH_DATE_) || ph.startsWith("GEN_") || ph.startsWith("UDF_") || ph.startsWith("REC_")){
					subjectSymbolSet.add(ph);
				}
				
			} // while
			
			////logger.debug("+++ Exiting : "+ totalPhSet);
		} catch (Exception e) {
			//logger.error("Exception while getting the symbol place holders ", e);
		}

		//logger.info("symbol PH  Set : "+ subjectSymbolSet);

		return subjectSymbolSet;
	}
	
	
	public static void barcodeIndicativeMsg(String content){

		logger.debug("entered into barcode indicative message...");
		try{
			String imgPattern = "<img\\s+.*?((?:id\\s*=\\s*\\\"?CC_\\w\\\"?)).*?>";
			String idPattern = "<img .*?id\\s*?=\\\"?(.*?)\\\".*?>";
						
			Pattern pattern = Pattern.compile(imgPattern,Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(content);
						
			while(matcher.find()) {

				String imgtag = null;
				String idAttr = null;
				
					imgtag = matcher.group();
					
					Pattern idp = Pattern.compile(idPattern, Pattern.CASE_INSENSITIVE);
					Matcher idm = idp.matcher(imgtag);
					
					while(idm.find()){
						idAttr = idm.group(1);
					}
					logger.debug("idAttr: "+idAttr);
					String ccPhTokens[] = idAttr.split("_");
		
		
					logger.debug("entered into barcodeindicative message");
					if(ccPhTokens.length >3){
						MessageUtil.setMessage("Please note that bar-code image shown in \n preview/test emails is indicative only,\n for the purpose of dimensions representation \n and is not the  actual code.", "info");
						break;
					}
			}
		
		}
		catch(Exception e){
			logger.error("Exception in indicative barcode message ", e);
		}
	}
	
	
	public  static Map<Long,TreeMap<String, List<String>>>  getPriorityMapByUsersList(List<Users> userIdLst,
															String mappingType, POSMappingDao posMappingDao) {
        
        try {
            //logger.debug(" userIdLst is  ::"+userIdLst.size());
            Map<Long, TreeMap<String, List<String>>> usersTreMap = new HashMap<Long, TreeMap<String, List<String>>>();
           
            for (Users eachUser : userIdLst) {
               
                //logger.debug("UserId is ::"+eachUser.getUserId()+" ::MappingType is ::"+mappingType);
                TreeMap<String, List<String>> prioMap = new TreeMap<String, List<String>>();
                if(posMappingDao == null) {
                   
                    posMappingDao= (POSMappingDao)SpringUtil.getBean("posMappingDao");
                }
                       
                List<POSMapping> posmapList = posMappingDao.getPriorityMapByUserId(eachUser.getUserId() , "'"+mappingType+"'");
               
                if(posmapList == null || posmapList.size()==0)  continue;
               
                for (POSMapping posMapObj : posmapList) {
                       
                    String dataTypeStr = posMapObj.getDataType().toLowerCase();
                   
                    if(dataTypeStr.startsWith("date")) {
                          dataTypeStr = "string";
                    }
                   
                 
                    String priorStr = posMapObj.getCustomFieldName()+"|"+dataTypeStr;
                   
                   
                    List<String> valList = prioMap.get(""+posMapObj.getUniquePriority());
                    if(valList==null){
                        valList = new ArrayList<String>();
                    }
                   
                   if(!valList.contains(priorStr)) {
                              valList.add(priorStr);
                  }
                   
                   prioMap.put(""+posMapObj.getUniquePriority(), valList);
                   
                   
                }
//                logger.debug(eachUser.getUserId()+">>>> "+prioMap);
                usersTreMap.put(eachUser.getUserId(), prioMap);
            }
            return usersTreMap;
      } catch (Exception e) {
          // TODO Auto-generated catch block
        logger.error("Excpetion :: ", e);
          return null;
      }
       
    } //getPriorityMapByUsersList
    
	public static String encryptPassword(String userName,String password) {

		/*Md5PasswordEncoder md5 = new Md5PasswordEncoder();
    	String encryptedPwd = md5.encodePassword(password,userName);
    	return encryptedPwd;*/
		String encryptedPwd = BCrypt.hashpw(password, BCrypt.gensalt());   
	 	return encryptedPwd;
	}
	/*public static String encryptPasswordUsingBcryptHash(String userName,String password) {	
		//BCrypt md5 = new BCrypt();    	
		String encryptedPwd = BCrypt.hashpw(password, BCrypt.gensalt());   
	 	return encryptedPwd;	
	}*/
	public static Set<String> findCoupPlaceholders(String content) {
		
		String cfpattern = "\\[([^\\[]*?)\\]";
		
		Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(content);

		String ph = null;
		Set<String> totalPhSet = new HashSet<String>();

		try {
			while(m.find()) {
	
				ph = m.group(1); //.toUpperCase()
				//logger.info("Ph holder :" + ph);
	
				
				 if(ph.startsWith("CC_")) {
					 totalPhSet.add(ph);
				 }
			}
				
		}catch (Exception e) {
			// TODO: handle exception
			return totalPhSet;
		}
		
		return totalPhSet;
	}
	public static void ltyBreadCrumbFrom(int fromPageNo, boolean isSbtoOcUser) {
		try {
		Long prgmId = (Long) Sessions.getCurrent().getAttribute("programId");
		Div ltyNavigationDivId =(Div)Utility.getComponentById("ltyNavigationDivId");
		
		//disable navigation In Header part
		Div ltyPrgmFirstStepId=(Div) ltyNavigationDivId.getFellowIfAny("ltyPrgmFirstStepId");
		Div ltyPrgmSecondStepId=(Div)ltyNavigationDivId.getFellowIfAny("ltyPrgmSecondStepId");
		Div ltyPrgmThirdStepId=(Div)ltyNavigationDivId.getFellowIfAny("ltyPrgmThirdStepId");
		Div ltyPrgmFourthStepId=(Div)ltyNavigationDivId.getFellowIfAny("ltyPrgmFourthStepId");
		Div ltyPrgmFifthStepId=(Div)ltyNavigationDivId.getFellowIfAny("ltyPrgmFifthStepId");
		Div ltyPrgmSixthStepId=(Div)ltyNavigationDivId.getFellowIfAny("ltyPrgmSixthStepId");

		Div[] naviDivArr = {ltyPrgmFirstStepId, ltyPrgmSecondStepId, ltyPrgmThirdStepId,
				ltyPrgmFourthStepId, ltyPrgmFifthStepId, ltyPrgmSixthStepId};
		
		logger.info("----------------- ltyBreadCrumbFrom called with :"+fromPageNo);
		
		
		if(prgmId == null) {
			if(fromPageNo == 1) {
				naviDivArr[0].setClass("create_email_step_current");
				for (int i = 1; i < naviDivArr.length; i++) {
					naviDivArr[i].setClass("create_email_step_incomplete");
				}
			}
		}
		else {
			LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			LoyaltyProgram prgmObj = loyaltyProgramDao.findById(prgmId); 
			for (int i = 0; i < naviDivArr.length; i++) {
				if(i+1 == fromPageNo) { 
					naviDivArr[i].setSclass("create_email_step_current");
				}
				else {
					if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE) || prgmObj.getProgramType().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_TYPE_DYNAMIC)){
						if(i+1 == 3) {
							naviDivArr[i].setClass("disable_nonapplicable_step");
						}
						else {
							naviDivArr[i].setSclass("create_email_step_completed");
						}
					}
					else {
						naviDivArr[i].setSclass("create_email_step_completed");
					}
					if(isSbtoOcUser && i+1==4 ){
						//naviDivArr[i].setClass("disable_nonapplicable_step");
						naviDivArr[i].setClass("create_email_step_completed");
					}
				}
			}
		}
		
		ltyNavigationDivId.invalidate();
		ltyNavigationDivId.setVisible(true);
		} catch (Exception e) {
			logger.error("Exception ::",e);
		}
	}
	
	/**
	 * This method use to display decimal or currency value into US format with Comma Separated format (eg: $ 921,921.50) .
	 * @param numberToFormatted
	 * @return decimalFormat
	 */
	public static String getAmountInUSFormat(Object numberToFormatted) {
		logger.info("entered in getAmountInUSFormat");
		String usFormatValue ="$ ";
		Users currUser = GetUser.getUserObj();
		 String currSymbol = Utility.countryCurrencyMap.get(currUser.getCountryType());
		   if(currSymbol != null && !currSymbol.isEmpty()) usFormatValue = currSymbol+ " ";
		try {
			NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
			if (numberFormat instanceof DecimalFormat) {
				DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
				decimalFormat.applyPattern("#0.00");
				decimalFormat.setGroupingUsed(true);
				decimalFormat.setGroupingSize(3);

				usFormatValue = usFormatValue + decimalFormat.format(numberToFormatted);
			}//if

		} catch (Exception e) {
			usFormatValue = usFormatValue + numberToFormatted;
			logger.error("Exception While formatting the Number ",e);
		}
		logger.info("completed  getAmountInUSFormat");
		return usFormatValue;
	}//getAmountInUSFormat
	
	
	
	public static String getAmountInExport(Object numberToFormatted) {
		logger.info("entered in getAmountInUSFormat");
		String usFormatValue =" ";
		try {
			NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
			if (numberFormat instanceof DecimalFormat) {
				DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
				decimalFormat.applyPattern("#0.00");
				decimalFormat.setGroupingUsed(true);
				decimalFormat.setGroupingSize(3);

				usFormatValue = usFormatValue + decimalFormat.format(numberToFormatted);
			}//if

		} catch (Exception e) {
			usFormatValue = usFormatValue + numberToFormatted;
			logger.error("Exception While formatting the Number ",e);
		}
		logger.info("completed  getAmountInUSFormat");
		return usFormatValue;
	}//getAmountInUSFormat
	
	
	
	/*public static void main(String[] args) {
		System.out.println(maskNumber("1234567890"));
	}*/
	public static String maskNumber(String cardNumber) {
		 /*long starttime = System.currentTimeMillis();
	        int total = cardNumber.length();
	        int startlen=4,endlen = 4;
	        int masklen = total-(startlen + endlen) ;
	        String start = cardNumber.substring(0,startlen);
	        String end = cardNumber.substring(startlen+masklen, total);
	        String padded = StringUtils.rightPad(start, startlen+masklen,'X'); 
	        String masked = padded.concat(end);
	        long endtime = System.currentTimeMillis();
	        System.out.println("maskCCNumber:="+masked+" of :"+masked.length()+" size");
	        System.out.println("using Stringutils="+(endtime-starttime)+" millis");
	        return masked;*/
		
		 final String s = cardNumber.replaceAll("\\D", "");

		    final int start = 0;
		    final int end = s.length() - 4;
		    final String overlay = StringUtils.repeat("X", end - start);

		    return StringUtils.overlay(s, overlay, start, end);
	}
	
	public static String validateNumberValue(String num){
		String value = num;
		value = value.replaceAll("[^0-9]", "");
		value = value.replaceFirst("^0*", "");
		
		if(value.length() > 0)
			return value;
			else return null;
		
	}
	
	public static String validateDoubleValue(String num){

		String str = num;
		str = str.replaceFirst("^0*", "");
		String value = str.toString().replaceAll(",", "").trim();
		String pattern = "\\d*\\.?\\d*";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(value);
		boolean matches = m.matches();
		if(matches){
			return value;
		}else {
			return null;
		}

	}
public static String replaceTextBarcodeHeightWidth(String content){
		
		String replacedHtml=content;
		
		Document doc = Jsoup.parse(content);
		
		//suraj--encoding code for emojis and other characters
		doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        doc.outputSettings().prettyPrint(true);
        doc.outputSettings().charset("ISO-8859-1");
        
        
		//Elements bodyele = doc.getElementsByTag("body");
		//Element body = doc.body();
		Elements elements = doc.select("img[data-mce-src]");
		for (Element el : elements) {
			String id=el.id();
			//String heightSize=id.substring(id.lastIndexOf("_")+1);
			//String widthSize=(id.substring(0, id.lastIndexOf("_"))).substring((id.substring(0, id.lastIndexOf("_"))).lastIndexOf("_")+1);
			
			String[] dimensions=id.split("_");
			el.attr("width",dimensions[dimensions.length-2]);
			el.attr("height",dimensions[dimensions.length-1]);
		}
		replacedHtml=doc.toString();
		return replacedHtml;
	}

	public static String truncateUptoTwoDecimal(double doubleValue) {
		String value = String.valueOf(doubleValue);
		if (value != null) {
			String result = value;
			int decimalIndex = result.indexOf(".");
			if (decimalIndex != -1) {
				String decimalString = result.substring(decimalIndex + 1);
				if (decimalString.length() > 2) {
					result = value.substring(0, decimalIndex + 3);
				} else if (decimalString.length() == 1) {
					result = String.format("%.2f",Double.parseDouble(value));
				}
			}
			return result;
		}
		return null;
	}//changes 2.5.5.0
	public static String truncateUptoTwoDecimal(String doubleValue) {
		String value = String.valueOf(doubleValue);
		if (value != null) {
			String result = value;
			int decimalIndex = result.indexOf(".");
			if (decimalIndex != -1) {
				String decimalString = result.substring(decimalIndex + 1);
				if (decimalString.length() > 2) {
					result = value.substring(0, decimalIndex + 3);
				} else if (decimalString.length() == 1) {
					result = String.format("%.2f",Double.parseDouble(value));
				}
			}
			return result;
		}
		return null;
	}

	
	public static String truncateToInteger(String doubleValue) {
				if(doubleValue!=null && doubleValue.contains(".")){
					int decimalIndex = doubleValue.indexOf(".");
					String decimalString = doubleValue.substring(0,decimalIndex);
					return decimalString;
				}
		return doubleValue;			
	}
	
public static void sendDRToLtyFailureMail(String DocSID, String DocDate, String DocTime, Users user,String trxType,String reason) {
		
		try{
			logger.info("sending DRToLtyFailureMail");
			if(reason.contains("Error 111539:Suspended membership")) {
				logger.info("Returning as the membership is suspended member. ");
				return; //APP-4196
			}
			String type=Constants.EQ_TYPE_SUPPORT_ALERT;
			String supportMail = PropertyUtil.getPropertyValueFromDB(OCConstants.Mail_To_Support);
			String subject = PropertyUtil.getPropertyValueFromDB(OCConstants.DRToLtyFailureSubject);
			subject=subject.replace(OCConstants.USERNAME, user.getUserName());
			String message = PropertyUtil.getPropertyValueFromDB(OCConstants.DRToLtyFailureMessage);
			
			message = message.replace(OCConstants.USER_FIRST_NAME_PH, user.getFirstName());
			message = message.replace(OCConstants.TRANSACTION_TYPE,trxType);
			message = message.replace(OCConstants.REASON,reason);
			message = message.replace(OCConstants.DOCSID,DocSID!=null ? DocSID : "");
			message = message.replace(OCConstants.DOC_DATE,DocDate
					+" "+DocTime);
			message = message.replace(OCConstants.USER_ORGID,Utility.getOnlyOrgId(user.getUserName()));
			message = message.replace(OCConstants.USERNAME,user.getUserName());
			
			//EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)SpringUtil.getBean("emailQueueDaoForDML");
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			EmailQueue testEmailQueue = null;
			
			testEmailQueue = new EmailQueue(subject, message, type, "Active", supportMail, MyCalendar.getNewCalendar(), user);
			emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
			String InstantMailUrl = PropertyUtil.getPropertyValue("InstantMailUrl");
			//Send request to servlet which activates the simpleMailSender(which actually sends the instant mails)
			pingSchedulerService(InstantMailUrl);
		}
		catch(Exception e){
			logger.error("** Exception : ", e);
		}
		
	}

public static void sendDRToPromoRedeemFailureMail(String DocSID, String DocDate, String DocTime,Users user,String coupon,String reason) {
	
	try{
		logger.info("sending DRToPromoRedeemFailureMail");
		String type=Constants.EQ_TYPE_SUPPORT_ALERT;
		String supportMail = PropertyUtil.getPropertyValueFromDB(OCConstants.Mail_To_Support);
		String subject = PropertyUtil.getPropertyValueFromDB(OCConstants.DRToPromoRedeemFailureSubject);
		subject=subject.replace(OCConstants.USERNAME, user.getUserName());
		String message = PropertyUtil.getPropertyValueFromDB(OCConstants.DRToPromoRedeemFailureMessage);
		
		message = message.replace(OCConstants.USER_FIRST_NAME_PH, user.getFirstName());
		message = message.replace(OCConstants.COUPON,coupon);
		message = message.replace(OCConstants.REASON,reason);
		message = message.replace(OCConstants.DOCSID,DocSID);
		message = message.replace(OCConstants.DOC_DATE,DocDate
				+" "+DocTime);
		message = message.replace(OCConstants.USER_ORGID,Utility.getOnlyOrgId(user.getUserName()));
		message = message.replace(OCConstants.USERNAME,user.getUserName());
		
		//EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)SpringUtil.getBean("emailQueueDaoForDML");
		EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
		EmailQueue testEmailQueue = null;
		
		testEmailQueue = new EmailQueue(subject, message, type, "Active", supportMail, MyCalendar.getNewCalendar(), user);
		emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
		String InstantMailUrl = PropertyUtil.getPropertyValue("InstantMailUrl");
		//Send request to servlet which activates the simpleMailSender(which actually sends the instant mails)
		pingSchedulerService(InstantMailUrl);
	}
	catch(Exception e){
		logger.error("** Exception : ", e);
	}
	
}

public static JSONArray dynamicUrlforCustomRowsBeeEditor(Users currentUser, UserDesignedCustomRowsDao userDesignedCustomRowsDao, String type) {
	String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
	JSONArray array = null;
	try {
		array = new JSONArray();
		JSONObject item = new JSONObject();
		List<ApplicationProperties> defaultCustomRows  = userDesignedCustomRowsDao.getDefaultTemplate(type);
		List<String> userDesignedCustomRows  = userDesignedCustomRowsDao.findTemplatesFromUserId(currentUser.getUserId());
		if(defaultCustomRows!=null && !defaultCustomRows.isEmpty() && defaultCustomRows.size() > 0) {
			for (ApplicationProperties defaultRows : defaultCustomRows) {
				item.put("name", defaultRows.getKey());
				item.put("value", ""+appUrl+"savedRows.mqrm?name="+defaultRows.getKey()+"");
				array.add(item);
				item = new JSONObject();
			}
		}
		if(userDesignedCustomRows!=null && !userDesignedCustomRows.isEmpty() && userDesignedCustomRows.size() > 0) {
			for (String customRows : userDesignedCustomRows) {
				item.put("name", customRows);
				item.put("value", ""+appUrl+"userDesignedSavedRows.mqrm?name="+customRows+"&userId="+currentUser.getUserId()+"");
				array.add(item);
				item = new JSONObject();
			}
		}
		
		item.put("name", "promotion Bar_Code");
		item.put("value", ""+appUrl+"promotionBarCode.mqrm?userOrgId="+currentUser.getUserOrganization().getUserOrgId()+"&userName="+GetUser.getUserName()+"");
		array.add(item);
		item = new JSONObject();
		return array;
	} catch (Exception e) {
		e.printStackTrace();
		return null;
	}
}


public static boolean validateBarCodeImage(String html) {
	String imgtag = null;
	boolean flag = true;
	String imgPattern = "<img\\s+((?:id\\s*=\\s*\\\"?CC_\\w\\\"?)).*?>";
	Pattern pattern = Pattern.compile(imgPattern,Pattern.CASE_INSENSITIVE);
	
	Matcher matcher = pattern.matcher(html);
	try {
	while(matcher.find()) {
		String s = "src=\"";
			imgtag = matcher.group();
			int ix = imgtag.indexOf(s)+s.length();
			String imageSrcValue = imgtag.substring(ix, imgtag.indexOf("\"", ix+1));
			if(!imageSrcValue.contains(GetUser.getUserName())) {
				flag= false;
			}
	}
	//APP-2070 validation for bar-code image end
	return flag;
	}catch (Exception e) {
		logger.info("error occrured during bar code image validation :"+e);
		return true;
	}
}
//method for old generated preview file if we have any
public static void cleanUpOldPreview(File dir,String pattern) {
	File[] files = dir.listFiles();
	try {
		if (files != null) {
			for (final File file : files) {
				if (file.isDirectory()) {
					logger.info("nothing to do it is a directory......");
				} else if (file.getName().startsWith(pattern)) {
					FileUtils.deleteQuietly(file);
				}
			}
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
	}
}

public static String mergeTagsForPreviewAndTestMail(String htmlContent,String type){
	HashSet<String> couponCodePLaceHolder = getCustomFields(htmlContent);
	 if(couponCodePLaceHolder!=null && !couponCodePLaceHolder.isEmpty()) {
		 for (String ccPlaceHolder : couponCodePLaceHolder) {
			 	String[] ccName = ccPlaceHolder.split("_");
			 	if(ccName!= null && ccName.length > 0 && ccName[2]!=null) {
			 		if(type.equals("preview"))
			 			htmlContent=htmlContent.replace("|^"+ccPlaceHolder+"^|", ccName[2]);
			 		else if(type.equals("testMail"))
			 			htmlContent=htmlContent.replace("|^"+ccPlaceHolder+"^|", "Test-"+ccName[2]);
			 		else if(type.equals("previewReport"))
			 			htmlContent=htmlContent.replace("["+ccPlaceHolder+"]", ccName[2]);
			 	}
		}
	 }
	 return htmlContent;
}


public static HashSet<String> getCustomFields(String content) {
	content = content.replace("|^", "[").replace("^|", "]");
	String cfpattern = "\\[([^\\[]*?)\\]";
	Pattern r = Pattern.compile(cfpattern, Pattern.CASE_INSENSITIVE);
	Matcher m = r.matcher(content);
	String ph = null;
	HashSet<String> totalPhSet = new HashSet<String>();
	try {
		while (m.find()) {

			ph = m.group(1); // .toUpperCase()
			if (ph.startsWith("CC_")) {
				totalPhSet.add(ph);
			}
		}

	} catch (Exception e) {
		logger.error("Exception while getting the place holders ", e);
	}
	return totalPhSet;
}

public static boolean isSomethingWrong(Campaigns campaign, int currentPos) {
	try {
			CampaignsDao campaignsDao = (CampaignsDao)ServiceLocator.getInstance().getDAOByName("campaignsDao");
			Campaigns campaigns = campaignsDao.findByCampaignId(campaign.getCampaignId());
			if(campaigns == null) return false;
			//return currentPos < CampaignStepsEnum.valueOf(campaigns.getDraftStatus()).getPos();
			return (currentPos < CampaignStepsEnum.valueOf(campaigns.getDraftStatus()).getPos() || 
					CampaignStepsEnum.valueOf(campaign.getDraftStatus()).getPos() <  CampaignStepsEnum.valueOf(campaigns.getDraftStatus()).getPos());
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception", e);
	}
	return false;
}
public static void main(String[] args) {
	System.out.println(Utility.numberToWord(879));
}
public static String numberToWord(int number) {
    // variable to hold string representation of number 
	logger.debug("inside TotalInWords====");
    String words = "";
    String unitsArray[] = { "Zero", "One", "Two", "Three", "Four", "Five", "Six", 
                  "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve",
                  "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", 
                  "Eighteen", "Nineteen" };
    String tensArray[] = { "Zero", "Ten", "Twenty", "Thirty", "Forty", "Fifty",
                  "Sixty", "Seventy", "Eighty", "Ninety" };

if (number == 0) {
    return "Zero";
}
// add minus before conversion if the number is less than 0
if (number < 0) { 
       // convert the number to a string
       String numberStr = "" + number; 
       // remove minus before the number 
       numberStr = numberStr.substring(1); 
       // add minus before the number and convert the rest of number 
       return "Minus " + numberToWord(Integer.parseInt(numberStr)); 
    } 
    // check if number is divisible by 1 million
    if ((number / 1000000) > 0) {
   words += numberToWord(number / 1000000) + " Million ";
   number %= 1000000;
}
// check if number is divisible by 1 thousand
if ((number / 1000) > 0) {
    words += numberToWord(number / 1000) + " Thousand ";
    number %= 1000;
}
// check if number is divisible by 1 hundred
if ((number / 100) > 0) {
     words += numberToWord(number / 100) + " Hundred ";
     number %= 100;
}

if (number > 0) {
     // check if number is within teens
     if (number < 20) { 
                // fetch the appropriate value from unit array
                words += unitsArray[number];
         } else { 
            // fetch the appropriate value from tens array
            words += tensArray[number / 10]; 
            if ((number % 10) > 0) {
	    words += "-" + unitsArray[number % 10];
            }  
     }
      }
  return words;
}

public static void sendCampaignFailureAlertMailToSupport(Users user, String smsCampaignName,String emailCampaignName,String notificationCampaignName,
		Calendar scheduledDate,String errorStatus,String configuredCount,String notSubmittedCount,String subjectLine) throws BaseServiceException{
try {
	// TODO Auto-generated method stub
	
	/*Date date = scheduledDate.getTime();
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
	String strDate = dateFormat.format(date);*/ 
	
	EmailQueueDao emailQueueDao = null;
	EmailQueueDaoForDML emailQueueDaoForDML = null;
	try {
		emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName("emailQueueDao");
		emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("emailQueueDaoForDML");
	} catch (Exception e) {
		
		logger.error(e);
		throw new BaseServiceException("No dao(s) found in the context");
		
	}
	
	
	String supportMail = PropertyUtil.getPropertyValueFromDB(OCConstants.Mail_To_Support);
	String subject = PropertyUtil.getPropertyValueFromDB(OCConstants.CAMPAIGN_FAILED_TO_SEND_SUBJECT);
	String message ="";
	
	String userName= Utility.getOnlyUserName(user.getUserName());
	subject=subject.replace(OCConstants.ALERT_MESSAGE_PH_USERNAME, userName);
	if(!smsCampaignName.isEmpty()){
		message =PropertyUtil.getPropertyValueFromDB(OCConstants.SMS_CAMPAIGN_FAILED_TO_SEND_CONTENT);
		message=message.replace(OCConstants.CAMPAIGN_NAME, smsCampaignName);
	}
	if(emailCampaignName!=""){
		message=PropertyUtil.getPropertyValueFromDB(OCConstants.EMAIL_CAMPAIGN_FAILED_TO_SEND_CONTENT);
		message=message.replace(OCConstants.CAMPAIGN_NAME, emailCampaignName);
		message=message.replace(OCConstants.SUBJECT_LINE,subjectLine);
	}
	if(notificationCampaignName!="") {
		message=PropertyUtil.getPropertyValueFromDB(OCConstants.NOTIFICATION_CAMPAIGN_FAILED_TO_SEND_CONTENT);
		message=message.replace(OCConstants.CAMPAIGN_NAME, notificationCampaignName);
		message=message.replace(OCConstants.SUBJECT_LINE,subjectLine);
	}
	
	
	if(errorStatus!=""){
	message=message.replace(OCConstants.ERROR_STATUS, errorStatus);
	}
	
	message=message.replace(OCConstants.SCHEDULED_DATE, MyCalendar.calendarToString(scheduledDate, MyCalendar.FORMAT_DATETIME_STYEAR));
	
	if(notSubmittedCount!=""){
	message=message.replace(OCConstants.CONFIGURED_COUNT,configuredCount);
	message=message.replace(OCConstants.NOT_SUBMITTED_COUNT,notSubmittedCount);
	}
	message=message.replace(OCConstants.USER_FNAME,user.getFirstName());
	message=message.replace(OCConstants.ALERT_MESSAGE_PH_USERNAME, userName);
	message = message.replace(OCConstants.USER_ORGID, Utility.getOnlyOrgId(user.getUserName()));
	
	
	EmailQueue emailQueueSupport = new EmailQueue(subject, message, Constants.EQ_TYPE_SUPPORT_ALERT,
			Constants.EQ_STATUS_ACTIVE, supportMail, Calendar.getInstance());
	
	emailQueueDaoForDML.saveOrUpdate(emailQueueSupport);
	
	
} catch (Exception e) {
	// TODO Auto-generated catch block
	logger.error("Error while sending a mail to support", e);
	throw new BaseServiceException("Error while sending a mail to support");
}

}
}
