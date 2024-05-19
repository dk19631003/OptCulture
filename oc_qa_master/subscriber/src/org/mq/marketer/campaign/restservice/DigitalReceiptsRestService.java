package org.mq.marketer.campaign.restservice;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.net.QuotedPrintableCodec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.DRSent;
import org.mq.marketer.campaign.beans.DigitalReceiptMyTemplate;
import org.mq.marketer.campaign.beans.DigitalReceiptUserSettings;
import org.mq.marketer.campaign.beans.DigitalReceiptsJSON;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.PrepareFinalHtml;
import org.mq.marketer.campaign.controller.service.DigitalReceiptSender;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.DRSentDao;
import org.mq.marketer.campaign.dao.DigitalReceiptMyTemplatesDao;
import org.mq.marketer.campaign.dao.DigitalReceiptUserSettingsDao;
import org.mq.marketer.campaign.dao.DigitalReceiptsJSONDao;
import org.mq.marketer.campaign.dao.DigitalReceiptsJSONDaoForDML;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MessagesDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.ReplacePlaceholderFromStrConetnt;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;


/*
 *   Accepts a JSON string and returns a JSON string .
 *   
 *   {
 *     replyJSON {
 *   		"replyStatus" : { "status" : "pass/fail" }
 *      	"replyMessage" : { "message" : "pass/fail" }
 *      	"returnParameters" : { "Allparams" : "values"  }
 *      }
 *   }
 *   
 */
public class DigitalReceiptsRestService extends AbstractController {
	
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public DigitalReceiptsRestService() {
		
	}
	
	private UsersDao usersDao;
	private EmailQueueDao emailQueueDao;
	private DigitalReceiptMyTemplatesDao digitalReceiptMyTemplatesDao;
	private MailingListDao mailingListDao;
	private ContactsDao contactsDao;
	private DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao;
	private DigitalReceiptUserSettings digitalReceiptUserSettings;
	private MessagesDao messagesDao;
	private DRSentDao drSentDao;
	private DigitalReceiptsJSONDao digitalReceiptsJSONDao;
	private DigitalReceiptsJSONDaoForDML digitalReceiptsJSONDaoForDML;
	public DigitalReceiptsJSONDaoForDML getDigitalReceiptsJSONDaoForDML() {
		return digitalReceiptsJSONDaoForDML;
	}

	public void setDigitalReceiptsJSONDaoForDML(
			DigitalReceiptsJSONDaoForDML digitalReceiptsJSONDaoForDML) {
		this.digitalReceiptsJSONDaoForDML = digitalReceiptsJSONDaoForDML;
	}

	private OrganizationStoresDao organizationStoresDao;
	private RetailProSalesDao retailProSalesDao;
	private ContactsLoyaltyDao contactsLoyaltyDao;	
	private POSMappingDao posMappingDao;
	
	public DigitalReceiptsJSONDao getDigitalReceiptsJSONDao() {
		return digitalReceiptsJSONDao;
	}

	public void setDigitalReceiptsJSONDao(
			DigitalReceiptsJSONDao digitalReceiptsJSONDao) {
		this.digitalReceiptsJSONDao = digitalReceiptsJSONDao;
	}

	public MessagesDao getMessagesDao() {
		return messagesDao;
	}

	public void setMessagesDao(MessagesDao messagesDao) {
		this.messagesDao = messagesDao;
	}

	public DigitalReceiptUserSettingsDao getDigitalReceiptUserSettingsDao() {
		return digitalReceiptUserSettingsDao;
	}

	public void setDigitalReceiptUserSettingsDao(
			DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao) {
		this.digitalReceiptUserSettingsDao = digitalReceiptUserSettingsDao;
	}

	public UsersDao getUsersDao() {
		return usersDao;
	}

	public void setUsersDao(UsersDao usersDao) {
		this.usersDao = usersDao;
	}

	public EmailQueueDao getEmailQueueDao() {
		return emailQueueDao;
	}

	public void setEmailQueueDao(EmailQueueDao emailQueueDao) {
		this.emailQueueDao = emailQueueDao;
	}

	public DigitalReceiptMyTemplatesDao getDigitalReceiptMyTemplatesDao() {
		return digitalReceiptMyTemplatesDao;
	}

	public void setDigitalReceiptMyTemplatesDao(
			DigitalReceiptMyTemplatesDao digitalReceiptMyTemplatesDao) {
		this.digitalReceiptMyTemplatesDao = digitalReceiptMyTemplatesDao;
	}
	
	
	
	public ContactsDao getContactsDao() {
		return contactsDao;
	}

	public void setContactsDao(ContactsDao contactsDao) {
		this.contactsDao = contactsDao;
	}
	
	public MailingListDao getMailingListDao() {
		return mailingListDao;
	}

	public void setMailingListDao(MailingListDao mailingListDao) {
		this.mailingListDao = mailingListDao;
	}
	
	
	public OrganizationStoresDao getOrganizationStoresDao() {
		return organizationStoresDao;
	}

	public void setOrganizationStoresDao(
			OrganizationStoresDao organizationStoresDao) {
		this.organizationStoresDao = organizationStoresDao;
	}

	public RetailProSalesDao getRetailProSalesDao() {
		return retailProSalesDao;
	}

	public void setRetailProSalesDao(RetailProSalesDao retailProSalesDao) {
		this.retailProSalesDao = retailProSalesDao;
	}
	
	public ContactsLoyaltyDao getContactsLoyaltyDao() {
		return contactsLoyaltyDao;
	}

	public void setContactsLoyaltyDao(ContactsLoyaltyDao contactsLoyaltyDao) {
		this.contactsLoyaltyDao = contactsLoyaltyDao;
	}
	public POSMappingDao getPosMappingDao() {
		return posMappingDao;
	}

	public void setPosMappingDao(POSMappingDao posMappingDao) {
		this.posMappingDao = posMappingDao;
	}

	public DRSentDao getDrSentDao() {
		return drSentDao;
	}

	public void setDrSentDao(DRSentDao drSentDao) {
		this.drSentDao = drSentDao;
	}
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		PrintWriter responseWriter = response.getWriter();
		String jsonMessage = "";
		byte statusFlag = 0;
		int errorCode = 0;
		response.setContentType("application/json");
		
		String extPrcWithTaxRelated ="";
		
	    try {
	    	
	    	//logger.debug("*************** JUST ENTERED ***********************");
	    	boolean isUserFound = false;
	    	
	    	String userFullDetails = "";
	    	
	    	if( (request.getParameter("userName") != null && request.getParameter("userName").length() > 1) &&
	    		(request.getParameter("userOrg") != null &&  request.getParameter("userOrg").length() > 1) ) {	
	    		isUserFound = true;
	    		userFullDetails =  request.getParameter("userName") + Constants.USER_AND_ORG_SEPARATOR +
	    				request.getParameter("userOrg");
	    	} else {
	    		isUserFound = false;
	    		/*errorCode = 300001;
	    		jsonMessage ="Given User name or Organization details not found .";
	    	    logger.debug("Required user name and Organization fields are not valid ... returning ");
	    	    return null;*/
	    	}
	       
	    	if(request.getParameter("jsonValue") == null ||  request.getParameter("jsonValue").length() < 1 ) {
	    		errorCode = 300002;
	    		jsonMessage = "JSON value is not valid."; 
	    		logger.debug("json value is null , returning.");
	    		return null;
	    	}
	    	
	    	
	    	String str = request.getParameter("jsonValue");
	    	byte by[] = QuotedPrintableCodec.decodeQuotedPrintable(( str ).getBytes());
	    	ByteArrayInputStream byteArrStrStream  = new ByteArrayInputStream( by);
	    	
	    	BufferedReader br = new BufferedReader(new InputStreamReader(byteArrStrStream));
	    	
	    	String tempStr = "";
	    	StringBuffer sb = new StringBuffer();
	    	while(  (tempStr = br.readLine()) != null ) {
	    		
	    		//logger.debug(" JSON VALUE IS  " + jsonStr);
	    		sb.append(tempStr);
	    	}
	    	
	    	String jsonStr = sb.toString();
	    	
	    	JSONObject headerObject = null;
	    	JSONObject jsonMainObj = null;
	    	//logger.debug(" Requst Query String : "+  Utility.decodeUrl(jsonStr));
	    	if(jsonStr != null && jsonStr.length() > 1) {
	    		
	    		logger.debug("jsonStr :>>>>>>>" + jsonStr);
	    		jsonMainObj = (JSONObject)JSONValue.parse(jsonStr);
	    		//logger.debug(" JSON VALUE TO STR : " + JSONValue.toJSONString(jsonMainObj));
	    		
	    	} else {
	    		logger.debug("UNable to creeate main obj");
	    		return null;
	    	}
	    	
	    	if(jsonMainObj != null) {
	    		if(jsonMainObj.containsKey("Head")) {
	    			headerObject = (JSONObject)((JSONObject)(jsonMainObj.get("Head"))).get("user");
	    		}
	    	}
	    	
	    	if(!isUserFound){
	    		if(headerObject.get("userName") != null && headerObject.get("userName").toString().trim().length() > 1 && 
	    				headerObject.get("organizationId") != null && headerObject.get("organizationId").toString().trim().length() > 1){
	    			isUserFound = true;
		    		userFullDetails =  headerObject.get("userName").toString().trim() + Constants.USER_AND_ORG_SEPARATOR +
		    				headerObject.get("organizationId").toString().trim();
	    			
	    		}else{
	    			errorCode = 300001;
		    		jsonMessage ="Given User name or Organization details not found .";
		    	    logger.debug("Required user name and Organization fields are not valid ... returning ");
		    	    return null;
	    		}
	    	}
	    	

	    	/*if(request.getParameter("userId") == null ||  request.getParameter("userId").length() < 1 ) {
	    		
	    		logger.debug("User Id is null .");
	    		userId = 249l;
	    		//return null;
	    	} else {
	    	
	    	    try {
	    	    	userId = Long.parseLong(request.getParameter("userId"));
	    	     } catch(NumberFormatException e) {
	    	    	logger.debug("User Id is not proper .. Number Format Exception , returning.");
	    	    	return null;
	    	     }
	    	} */   
	    	
	    	
	    	/*if(userId == null) {
	    		
	    		logger.debug("user Id is null ... returning");
	    		return null;
	    	}*/
	    	
	    	// Get User Obj by Id.
			Users user = usersDao.findByUsername(userFullDetails);
			
			if(user == null) {
				errorCode = 100011;
				jsonMessage ="Given User name or Organization details not found .";  // Organization
				logger.debug("******** User Object not Found ... returing ");
				return null;
			}
			
			if(!user.isEnabled() || user.getPackageExpiryDate().before(Calendar.getInstance())){
				
				errorCode = 100012;
				jsonMessage ="User account is either deactivated or expired.";  // Organization
				logger.debug("******** User Object not Found ... returing ");
				return null;
				
				
			}
			
			
	    	/*ByteArrayInputStream byteArrStrStream  = new ByteArrayInputStream( QuotedPrintableCodec.decodeQuotedPrintable( (request.getParameter("jsonValue") ).getBytes()));
	    	
	    	BufferedReader br = new BufferedReader(new InputStreamReader(byteArrStrStream));
	    	
	    	String tempStr = "";
	    	StringBuffer sb = new StringBuffer();
	    	while(  (tempStr = br.readLine()) != null ) {
	    		
	    		//logger.debug(" JSON VALUE IS  " + jsonStr);
	    		sb.append(tempStr);
	    	}
	    	
	    	String jsonStr = sb.toString();*/
	    	
	    	//started logging json request 
	    	DigitalReceiptsJSON digitalReceiptsJSON = new DigitalReceiptsJSON();
	    	
	    	digitalReceiptsJSON.setJsonStr(jsonStr);
	    	digitalReceiptsJSON.setStatus(Constants.DR_JSON_PROCESS_STATUS_NEW);
	    	digitalReceiptsJSON.setUserId(user.getUserId());
	    	digitalReceiptsJSON.setCreatedDate(Calendar.getInstance());
	    	digitalReceiptsJSON.setMode(OCConstants.DR_ONLINE_MODE);
	    	String docSid = null;
			digitalReceiptsJSON.setDocSid(docSid);
	    	
	    	//digitalReceiptsJSONDao.saveOrUpdate(digitalReceiptsJSON);
	    	digitalReceiptsJSONDaoForDML.saveOrUpdate(digitalReceiptsJSON);
	    	//completed logging json request
	    	
	    	
	    	String userJSONSettings = PropertyUtil.getPropertyValueFromDB("DigitalReceiptSetting");
	    	
	    	//String userJSONSettings = "#Store.Logo#:http://optculture.com/wp-content/themes/optculture/images/logo.png ^|^#Store.WWW#:Receipt.Store^|^#Store.Number#:Receipt.Store^|^#Store.Name#:Receipt.StoreHeading1^|^#Store.Phone#:Receipt.StorePhone^|^#Store.Email#:Receipt.StoreHeading2^|^#Store.Street#:Receipt.StoreHeading3^|^#Store.City#:Receipt.StoreHeading4^|^#Store.State#:Receipt.StoreHeading5^|^#Store.Zip#:Receipt.StoreHeading6^|^#Store.Cashier#:Receipt.Cashier^|^#Receipt.ID#:Receipt.DocSID^|^#Receipt.Number#:Receipt.InvcNum^|^#Receipt.Date#:Receipt.DocDate_$$_Receipt.DocTime^|^#Receipt.PaymentMethod#:Receipt.Tender^|^#BillTo.Name#:Receipt.BillToFullName^|^#BillTo.Email#:Receipt.BillToEMail^|^#BillTo.Street#:Receipt.BillToAddr1^|^#BillTo.City#:Receipt.BillToAddr2^|^#BillTo.State#:Receipt.BillToAddr3^|^#BillTo.Zip#:Receipt.BillToZip^|^#ShipTo.Name#:Receipt.ShipToFullName^|^#ShipTo.Street#:Receipt.ShipToAddr1^|^#ShipTo.City#:Receipt.ShipToAddr2^|^#ShipTo.State#:Receipt.ShipToAddr3^|^#ShipTo.Zip#:Receipt.ShipToZip^|^#Item.Description#:Items.Desc1_$$_Items.Attr_$$_Items.Size^|^#Item.Quantity#:Items.Qty^|^#Item.UnitPrice#:Items.InvcItemPrc^|^#Item.Total#:Items.ExtPrc^|^#Receipt.Amount#:Receipt.Total^|^#Receipt.Message#:Receipt.InvcComment1^|^#Receipt.Message2#:Receipt.InvcComment2^|^#Receipt.Footer#:Receipt.TotalSavings";
	    		    	
	    	if(userJSONSettings == null) {
				errorCode = 300003;
	    		jsonMessage ="Given JSON is not found .";
	    		logger.debug("userJSONSettings is null ... returning");
	    		return null;
	    	}
	    	
	    	
	    	boolean enabled = digitalReceiptUserSettingsDao.findIsUserEnabled(user.getUserId());
	    	if(!enabled) {
	    		errorCode = 300004;
	    		jsonMessage ="Can not send receipt : user is not enabled with sending ";  // Organization
				logger.info("******** not enabled ... returing ");
				return null;
	    		
	    		
	    	}
	    	
	    	
	    //	JSONObject jsonMainObj = null;
	    	//logger.debug(" Requst Query String : "+  Utility.decodeUrl(jsonStr));
	    	/*if(jsonStr != null && jsonStr.length() > 1) {
	    		
	    		logger.debug("jsonStr :>>>>>>>" + jsonStr);
	    		jsonMainObj = (JSONObject)JSONValue.parse(jsonStr);
	    		//logger.debug(" JSON VALUE TO STR : " + JSONValue.toJSONString(jsonMainObj));
	    		
	    	} else {
	    		logger.debug("UNable to creeate main obj");
	    		return null;
	    	}*/
	    	
	    	if(jsonMainObj != null) {
	    		if(!jsonMainObj.containsKey("Items")) {
	    			jsonMainObj = (JSONObject)jsonMainObj.get("Body");
	    		}
	    	} else {
	    		errorCode = 300005;
	    		logger.debug("*** Main Object is NUll ***");
	    		jsonMessage = "Unable to parse the JSON request .";
	    		return null;
	    	}
	    	
	    	/*JSONObject jsonObject = (JSONObject)jsonMainObj.get("Items");
	    	
	    	logger.debug("arr 1 :" +jsonObject.get("UDF0"));
	    	logger.debug("arr 2 :" +jsonObject.get("UDF2"));*/
	    	
	    	
	    	// GET User Configured REQUIRED TEMPLATE .
	    	String templateContentStr = "";
	    	//logger.debug(">>>>>>>>>>>>>>"+ digitalReceiptMyTemplatesDao);
	    	String selectedTemplate = digitalReceiptUserSettingsDao.findUserSelectedTemplate(user.getUserId());
	    	//long selTemplateId=digitalReceiptUserSettingsDao.findTemplateByName(user.getUserId(),selectedTemplate);
	    	//logger.debug("Template Settings : "+ selectedTemplate);
	    	String selTemplateName=selectedTemplate;
	    	
	    	if(selectedTemplate == null) {
	    		
	    		logger.debug("Digital reciept template is not found .. assigning default template ********");
	    		selectedTemplate = "SYSTEM:Corporate_template";
    			//return null;
	    	}
	    	
	    	if(selectedTemplate.indexOf("MY_TEMPLATE:") != -1) {
	    		
	    		selectedTemplate = selectedTemplate.substring(12);
	    		//logger.debug("Template Name is : "+ selectedTemplate);
	    		DigitalReceiptMyTemplate digitalReceiptMyTemplates = digitalReceiptMyTemplatesDao.findByUserNameAndTemplateName(user.getUserId(), selectedTemplate);
	    		
	    		if(digitalReceiptMyTemplates == null) {
	    			logger.debug("Configured digital reciept template is not found ********");
	    			errorCode = 300006;
	    			jsonMessage = "Configured Custom digital reciept template is not found"; 
	    			return null;
	    		}
	    		
	    		templateContentStr = digitalReceiptMyTemplates.getContent();
	    		
	    	} else if (selectedTemplate.indexOf("SYSTEM:") != -1) {
	    		
	    		selectedTemplate = selectedTemplate.substring(7);
	    		//logger.debug("Template Name is : "+ selectedTemplate);
	    		String digiReciptsDir = PropertyUtil.getPropertyValue("DigitalRecieptsDirectory");
	    		File templateFile = new File(digiReciptsDir + "/" + selectedTemplate + "/index.html");
	    		//logger.debug("File is :" + templateFile);

	    		if(!templateFile.exists()) {
	    			errorCode = 300007;
	    			jsonMessage ="Templates dir path is incorrect";
	    			return null;
	    		}
	    		
	    		FileReader fr = new FileReader(templateFile);
	    		
	    		BufferedReader br2 = new BufferedReader(fr);
	    		
	    		String line = "";
	    		StringBuffer sb2 = new StringBuffer();
	    		while((line = br2.readLine())!= null) {
	    			
	    			sb2.append(line);
	    		}
	    		templateContentStr = sb2.toString();
	    	}
	    	
	    	//logger.debug("Template Content is : "+ templateContentStr);
	    	
	    	// Configure admin setting to a MAP : format #templatePH# : JSONKey String 
	    	//logger.debug("**** userJSONSettings "+ userJSONSettings);
	    	String[] settingFieldsArr = userJSONSettings.split(Pattern.quote("^|^"));
	    	
	    	Map<String, String> userJSONSettingHM = new HashMap<String, String>();
	    	String[] tokensStr;
	    	
	    	//logger.debug("*********** settingFieldsArr " + settingFieldsArr.length);
	    	
	    	 // Get Individual mapping and add them to our MAP ,#templatePH# : JSONKey
	    	for(int i=0;i< settingFieldsArr.length;i++) {
	    		
	    		tokensStr = settingFieldsArr[i].split(":");
	    		userJSONSettingHM.put(tokensStr[0], tokensStr[1]);
	    	}	
	    	
	   // 	logger.debug("******** Map userJSONSettingHM :" + userJSONSettingHM);
	    	
	    	// Get Values form Application properties
	    	String paymentSetPlaceHolders = PropertyUtil.getPropertyValue("DRPaymentLoopPlaceHolders"); 
			String ItemsSetPlaceHolders = PropertyUtil.getPropertyValue("DRItemLoopPlaceHolders");
	    	
	    	String[] paymentSetPlaceHoldersArr = paymentSetPlaceHolders.split(",");
			String[] ItemsSetPlaceHoldersArr = ItemsSetPlaceHolders.split(",");
			
			
			//create string buffer for storing place holders in template
			
			
			// Step 1 : Replace Loopable elements .
		//	logger.debug("** ItemsSetPlaceHoldersArr length : " + ItemsSetPlaceHoldersArr.length);
			
			JSONObject receiptJSONObj = (JSONObject)jsonMainObj.get("Receipt");
				if(receiptJSONObj == null) {
					logger.debug("**** Exception : Receipt Object Not found ***");
					return null;
				}
			
				
				if(templateContentStr.indexOf(ItemsSetPlaceHoldersArr[0]) != -1 && templateContentStr.indexOf(ItemsSetPlaceHoldersArr[5]) != -1) 
				{
					String loopBlockOne = templateContentStr.substring(templateContentStr.indexOf(ItemsSetPlaceHoldersArr[0]) + ItemsSetPlaceHoldersArr[0].length(),
							templateContentStr.indexOf(ItemsSetPlaceHoldersArr[5]));
					logger.debug("Items HTML is :"+ loopBlockOne);
					
					
					
					extPrcWithTaxRelated = loopBlockOne;
				//	String temploopbackContent = loopBlockOne; 
					
					String replacedStr = replaceLoopBlock(loopBlockOne,userJSONSettingHM,jsonMainObj);
					
					
					 //DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00"); 
					 // Print only those from below fields which have value. 
					 //'0' or empty value are considered as fields having no value.
					 //'0.00' though will be treated as field having a value. 
					
					// Double totTax = (Double) (receiptJSONObj.get("TotalTax") != null &&receiptJSONObj.get("TotalTax").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(receiptJSONObj.get("TotalTax").toString()));
					// Double subTotal = (Double) (receiptJSONObj.get("Subtotal").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(receiptJSONObj.get("Subtotal").toString()));
					// Double shipping = (Double) (receiptJSONObj.get("Shipping").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(receiptJSONObj.get("Shipping").toString()));
					// Double fee = (Double) (receiptJSONObj.get("Fee").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(receiptJSONObj.get("Fee").toString()));
					 //Double total = (Double) (receiptJSONObj.get("Total").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(receiptJSONObj.get("Total").toString()));
					 
					//List<Double> amtValues = new ArrayList<Double>();
					Map<String, Double> mescilleneousAmtMap = new LinkedHashMap<String, Double>();
					
					Object subTotalJsonObj = receiptJSONObj.get("Subtotal");
					if(subTotalJsonObj != null && !subTotalJsonObj.toString().trim().isEmpty() && !subTotalJsonObj.toString().trim().equals("0") ){
						
						Double subTotal = Double.parseDouble(subTotalJsonObj.toString().trim());
						mescilleneousAmtMap.put("Sub Total", subTotal);
						//amtValues.add(subTotal);
						
					}
					
					 Object totTaxJsonObj = receiptJSONObj.get("TotalTax");
					 if(totTaxJsonObj != null && !totTaxJsonObj.toString().trim().isEmpty() && !totTaxJsonObj.toString().trim().equals("0")){
						 
						 Double totTax = Double.parseDouble(totTaxJsonObj.toString().trim());
						// amtValues.add(totTax);
						 mescilleneousAmtMap.put("Tax", totTax);

					 }
					 
					 
					 Object shippingJsonObj = receiptJSONObj.get("Shipping");
					 if(shippingJsonObj != null && !shippingJsonObj.toString().trim().isEmpty() && !shippingJsonObj.toString().trim().equals("0")){
						 
						 Double shipping = Double.parseDouble(shippingJsonObj.toString().trim());
						 mescilleneousAmtMap.put("Shipping", shipping);
						// amtValues.add(shipping);

					 }
					 
					 Object feeJsonObj = receiptJSONObj.get("Fee");
					 String helperKey="";
					 if(feeJsonObj != null && !feeJsonObj.toString().trim().isEmpty() && !feeJsonObj.toString().trim().equals("0")){
						 
						 Object feeType = ((JSONObject)jsonMainObj.get("Receipt")).get("FeeType");
						 Double fee = Double.parseDouble(feeJsonObj.toString().trim());
						 helperKey =  ((feeType !=null && !feeType.toString().trim().isEmpty() ) 
								 ? feeType.toString().trim() : Constants.STRING_NILL)+" Fee";
						/* mescilleneousAmtMap.put(((feeType !=null && !feeType.toString().trim().isEmpty() ) 
								 ? feeType.toString().trim() : Constants.STRING_NILL)+" Fee", fee); */
						 mescilleneousAmtMap.put(helperKey, fee);		 
						 
						 
						 		 
						 //amtValues.add(fee);

					 }
					 Object discountJsonObj = receiptJSONObj.get("Discount");
					 if(discountJsonObj != null && !discountJsonObj.toString().trim().isEmpty() && !discountJsonObj.toString().trim().equals("0")){
						 
						 Double discount = Double.parseDouble(discountJsonObj.toString().trim());
						 mescilleneousAmtMap.put("Rcpt. Discount", discount); 
						 //amtValues.add(total);
						 
					 }
					 
					 Object totalJsonObj = receiptJSONObj.get("Total");
					 if(totalJsonObj != null && !totalJsonObj.toString().trim().isEmpty() && !totalJsonObj.toString().trim().equals("0")){
						 
						 Double total = Double.parseDouble(totalJsonObj.toString().trim());
						 mescilleneousAmtMap.put("Total", total); 
						 //amtValues.add(total);

					 }
					 
					 Object totalSavingJsonObj = receiptJSONObj.get("TotalSavings");
					 if(totalSavingJsonObj != null && !totalSavingJsonObj.toString().trim().isEmpty() && !totalSavingJsonObj.toString().trim().equals("0")){
						 
						 Double totalSaving = Double.parseDouble(totalSavingJsonObj.toString().trim());
						 mescilleneousAmtMap.put("Total Savings", totalSaving); 
						 //amtValues.add(total);

					 }
					 //added in 2.4.6
                     /*
					 Object discountJsonObj = receiptJSONObj.get("Discount");
					 if(discountJsonObj != null && !discountJsonObj.toString().trim().isEmpty() && !discountJsonObj.toString().trim().equals("0")){
						 
						 Double discount = Double.parseDouble(discountJsonObj.toString().trim());
						 mescilleneousAmtMap.put("Discount", discount); 
						 //amtValues.add(total);

					 }*/
					 
					 
					logger.debug("mescilleneousAmtMap size::"+mescilleneousAmtMap.size());
					 /*amtValues.add(subTotal);
					 amtValues.add(totTax);
					 amtValues.add(shipping);
					 amtValues.add(fee);
					 amtValues.add(total);*/
					/*
					if(receiptJSONObj == null) {
						logger.debug("**** Exception : Receipt Object Not found ***");
						return null;
					}
					*/
					
					/**
					 *  Removed after Faye's Request(With out Items and SONumber also We'l send DR)
					 */		
					
					/*if((receiptJSONObj.get("SONumber") == null || receiptJSONObj.get("SONumber").toString().isEmpty() )  && replacedStr == null) {
						errorCode = 300008;
						jsonMessage="unable to find proper mappings.";
						logger.debug("**** Exception : Error occured while replacing the item default values ... ");
						return null;
					}*/
					
						//logger.debug("Items extra fields are getting added....");
						// Add extras prices like tax shipping etc
					 
					 // Commented after default values in DR
				//	if(replacedStr != null) {
					 /*logger.debug("loopBlockOne ::"+loopBlockOne );
					 String [] itemArray = getItemsArray(loopBlockOne);
					 for (String itemHolder : itemArray) {
						 logger.debug("itemHolder ::"+itemHolder);
					}
					 if(true) return null;*/
					 if(replacedStr == null) replacedStr=Constants.STRING_NILL;
					 
					// for (int i = 0; i < itemArray.length; i++) {
					 
					 String itemHolder = "";
					// Set<String> DR_PLACEHOLDERS_LIST = Constants.DR_PLACEHOLDERS_LIST;
					 Set<String> mescelleneousAmtKeySet = mescilleneousAmtMap.keySet();
					// int amountIndex = 0;
					 DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(user.getUserId());
					 boolean tax = digitalReceiptUserSettings.isIncludeTax();
					 boolean fee = digitalReceiptUserSettings.isIncludeFee();
					 boolean shipping = digitalReceiptUserSettings.isIncludeShipping();
					 boolean globalDisc = digitalReceiptUserSettings.isIncludeGlobalDiscount();
					 
					 
					 itemHolder = replaceItemArrayNew(loopBlockOne, Constants.STRING_NILL, 0.0 ); 
					 replacedStr += itemHolder;
					 
					 for (String ItemPHTag : mescelleneousAmtKeySet) {
						 
						 if(ItemPHTag.equalsIgnoreCase("Tax")){
							 if(tax){ 
								 	itemHolder = replaceItemArrayNew(loopBlockOne, ItemPHTag, mescilleneousAmtMap.get(ItemPHTag) ); 
								 	replacedStr += itemHolder;
							     }
						 } else if(ItemPHTag.equalsIgnoreCase(helperKey)){ // i.e. for fee
							 if(fee){
								 itemHolder = replaceItemArrayNew(loopBlockOne, ItemPHTag, mescilleneousAmtMap.get(ItemPHTag) ); 
								 replacedStr += itemHolder;
							 }
						 } else if(ItemPHTag.equalsIgnoreCase("Shipping")){
							 if(shipping){
								 itemHolder = replaceItemArrayNew(loopBlockOne, ItemPHTag, mescilleneousAmtMap.get(ItemPHTag) ); 
								 replacedStr += itemHolder;
							 }
						 }else if(ItemPHTag.equalsIgnoreCase("Rcpt. Discount")){
							 if(globalDisc){
								 itemHolder = replaceItemArrayNew(loopBlockOne, ItemPHTag, mescilleneousAmtMap.get(ItemPHTag) ); 
								 replacedStr += itemHolder;
							 }
						 }else if(ItemPHTag.equalsIgnoreCase("Sub Total")){
							 if(tax || fee || shipping || globalDisc){
								 itemHolder = replaceItemArrayNew(loopBlockOne, ItemPHTag, mescilleneousAmtMap.get(ItemPHTag) ); 
								 replacedStr += itemHolder;
							 }
						 }
						 else{
							 itemHolder = replaceItemArrayNew(loopBlockOne, ItemPHTag, mescilleneousAmtMap.get(ItemPHTag) ); 
							 replacedStr += itemHolder;
						 }
						 
						 //itemHolder = replaceItemArray(loopBlockOne, ItemPHTag, mescilleneousAmtMap.get(ItemPHTag) ); 
						 
						 //loopblock one string.
						 //replacedStr += itemHolder;
						// amountIndex++;
						 
					}
						
						// replacedStr += itemHolder;
						/*for (int j = 0; j < itemArray.length; j++) {
							itemHolder = itemHolder.replace(itemArray[i], "&nbsp;");
							
							
						}*/
						
						
			          /*  itemHolder = itemHolder.replace("#Item.Description#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Attr#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Size#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Description1#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Description2#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.SerialNumber#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Quantity#", "&nbsp;");
			            
			            itemHolder = itemHolder.replace("#Item.ALU#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Lookup#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Discount#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.DiscountPercent#", "&nbsp;");*/
			            
			            
			           /* itemHolder = itemHolder.replace("#Item.UnitPrice#", "<strong> Sub Total </strong>");
			           // itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + receiptJSONObj.get("Subtotal") + "</strong>");
			            itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + decimalFormat.format(subTotal) + "</strong>");
			            replacedStr += itemHolder;
	*/
			            // Tax
			          /*  itemHolder = loopBlockOne;
			            itemHolder = itemHolder.replace("#Item.Description#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Description1#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Description2#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Attr#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Size#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.SerialNumber#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Quantity#", "&nbsp;");
			            
			            itemHolder = itemHolder.replace("#Item.ALU#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Lookup#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Discount#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.DiscountPercent#", "&nbsp;");
			            
			            itemHolder = itemHolder.replace("#Item.UnitPrice#", "Tax");
			          //  itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + receiptJSONObj.get("TotalTax") + "</strong>");
			            itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + decimalFormat.format(totTax) + "</strong>");
			            replacedStr += itemHolder;*/
			            
			            // Shipping
			           /* if (((JSONObject)jsonMainObj.get("Receipt")).get("Shipping") != null) 
			            {
			                itemHolder = loopBlockOne;
			                itemHolder = itemHolder.replace("#Item.Description#", "&nbsp;");
			                itemHolder = itemHolder.replace("#Item.Attr#", "&nbsp;");
			                itemHolder = itemHolder.replace("#Item.Size#", "&nbsp;");
			                itemHolder = itemHolder.replace("#Item.SerialNumber#", "&nbsp;");
			                itemHolder = itemHolder.replace("#Item.Description1#", "&nbsp;");
			                itemHolder = itemHolder.replace("#Item.Description2#", "&nbsp;");
			                itemHolder = itemHolder.replace("#Item.Quantity#", "&nbsp;");
			                
			                itemHolder = itemHolder.replace("#Item.ALU#", "&nbsp;");
				            itemHolder = itemHolder.replace("#Item.Lookup#", "&nbsp;");
				            itemHolder = itemHolder.replace("#Item.Discount#", "&nbsp;");
				            itemHolder = itemHolder.replace("#Item.DiscountPercent#", "&nbsp;");
				            
			                itemHolder = itemHolder.replace("#Item.UnitPrice#", "Shipping");
			              //  itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + ((JSONObject)jsonMainObj.get("Receipt")).get("Shipping")  + "</strong>");
			                itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + decimalFormat.format(shipping)  + "</strong>");
			                replacedStr += itemHolder;
			            }*/
	
			            // Fee
			           /* if (((JSONObject)jsonMainObj.get("Receipt")).get("Fee") != null) {
			                itemHolder = loopBlockOne;
			                itemHolder = itemHolder.replace("#Item.Description#", "&nbsp;");
			                itemHolder = itemHolder.replace("#Item.Attr#", "&nbsp;");
			                itemHolder = itemHolder.replace("#Item.Size#", "&nbsp;");
			                itemHolder = itemHolder.replace("#Item.Description1#", "&nbsp;");
			                itemHolder = itemHolder.replace("#Item.Description2#", "&nbsp;");
			                itemHolder = itemHolder.replace("#Item.Quantity#", "&nbsp;");
			                
			                itemHolder = itemHolder.replace("#Item.ALU#", "&nbsp;");
				            itemHolder = itemHolder.replace("#Item.Lookup#", "&nbsp;");
				            itemHolder = itemHolder.replace("#Item.Discount#", "&nbsp;");
				            itemHolder = itemHolder.replace("#Item.DiscountPercent#", "&nbsp;");
				            
			                itemHolder = itemHolder.replace("#Item.SerialNumber#", "&nbsp;");
			                itemHolder = itemHolder.replace("#Item.UnitPrice#", "Fee -" + ((JSONObject)jsonMainObj.get("Receipt")).get("FeeType"));
			              //  itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + ((JSONObject)jsonMainObj.get("Receipt")).get("Fee") + "</strong>");
			                itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + decimalFormat.format(fee) + "</strong>");
			                replacedStr += itemHolder;
			            }*/
			            
			            // Total
			           /* itemHolder = loopBlockOne;
			            itemHolder = itemHolder.replace("#Item.Description#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Attr#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Size#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Description1#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Description2#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Quantity#", "&nbsp;");
			            
			            itemHolder = itemHolder.replace("#Item.ALU#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Lookup#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.Discount#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.DiscountPercent#", "&nbsp;");
			            
			            itemHolder = itemHolder.replace("#Item.SerialNumber#", "&nbsp;");
			            itemHolder = itemHolder.replace("#Item.UnitPrice#", "<strong> Total </strong>");
			          //  itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + ((JSONObject)jsonMainObj.get("Receipt")).get("Total") + "</strong>");
			            itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + decimalFormat.format(total) + "</strong>");
			            replacedStr += itemHolder;
					*/
					
						
						templateContentStr = templateContentStr.replace(loopBlockOne, replacedStr);
						logger.debug("content is prepared "+templateContentStr.isEmpty());
						// Commented after default values in DR
					/*} else { 
						System.out.println("enterd in no items ");
						templateContentStr = templateContentStr.replace("#Item.Description#", "&nbsp;").
			           replace("#Item.Attr#", "&nbsp;").
			           replace("#Item.Description1#", "&nbsp;")
			           .replace("#Item.Description2#", "&nbsp;")
			           .replace("#Item.Quantity#", "&nbsp;")
			           .replace("#Item.Size#", "&nbsp;")
			            .replace("#Item.SerialNumber#", "&nbsp;")
			            
			           .replace("#Item.ALU#", "&nbsp;")
			           .replace("#Item.Lookup#", "&nbsp;")
			            .replace("#Item.Discount#", "&nbsp;")
			           .replace("#Item.DiscountPercent#", "&nbsp;")
			            
			           .replace("#Item.UnitPrice#", "<strong> Sub Total </strong>")
			         //  .replace("#Item.Total#", "<strong>" + receiptJSONObj.get("Subtotal") + "</strong>");
			           .replace("#Item.Total#", "<strong>" + decimalFormat.format(subTotal) + "</strong>");
						
						
						
					}*/
				// }// for
				}
				
			
			// Now replace payments PH
			/*logger.debug("** paymentSetPlaceHoldersArr : " + paymentSetPlaceHoldersArr.length);
			logger.debug("--0--"+ templateContentStr );
			logger.debug("--1--"+ paymentSetPlaceHoldersArr[0]);
			logger.debug("--2--"+ templateContentStr.indexOf(paymentSetPlaceHoldersArr[0]));
			logger.debug("--3--"+ paymentSetPlaceHoldersArr[4]);
			logger.debug("--4--"+ templateContentStr.indexOf(paymentSetPlaceHoldersArr[4]));*/
				//logger.debug("--4--");
			
			if(templateContentStr.indexOf(paymentSetPlaceHoldersArr[0]) != -1 && templateContentStr.indexOf(paymentSetPlaceHoldersArr[4]) != -1) 
			{
				
					String loopBlockTwo = templateContentStr.substring(templateContentStr.indexOf(paymentSetPlaceHoldersArr[0]) + paymentSetPlaceHoldersArr[0].length(),
							templateContentStr.indexOf(paymentSetPlaceHoldersArr[4]));
					
				//	logger.debug("Payment HTML is :"+ loopBlockTwo);
				
					//String replacedStr2 = replaceLoopBlock(loopBlockTwo,userJSONSettingHM,jsonMainObj);
					//String replacedStr2 = "";
					
					//logger.debug("Payment block after replace is : "+ replacedStr2);
					/*if(replacedStr2 == null) {
						logger.debug("**** Exception : Error occured while replacing the PAYMENT default values ... ");
						return null;
					}*/
					
					// Add extra fields to template
				//	logger.debug("PAYMENT extra fields are getting added....");
				//	JSONObject receiptJSONObj = (JSONObject)jsonMainObj.get("Receipt");
					/*
					if(receiptJSONObj == null) {
						logger.debug("**** Exception : Receipt Object Not found ***");
						return null;
					}*/
					
					String paymentParts ="";
					 DecimalFormat deciFormat = new DecimalFormat("#,###,##0.00"); 
				   if (jsonMainObj.get("Cash") != null)   {
					   JSONObject cashObj =(JSONObject)jsonMainObj.get("Cash");
					   Double cashVal = (Double) (cashObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(cashObj.get("Amount").toString()));
					   
					   //String cashVal = deciFormat.format(cashObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(cashObj.get("Amount")).toString();
		                paymentParts += GetMergedPaymentPart(loopBlockTwo,"","Cash", deciFormat.format(cashVal)) ;
		            }
				 
		            if (jsonMainObj.get("StoreCredit") != null)   {
		            	// Added after new DR Schema
		            	JSONArray storeCreditCardArr = (JSONArray)jsonMainObj.get("StoreCredit");
		            	
		            	for (Object object : storeCreditCardArr) {
							
		            		JSONObject tempStoreCreditObj = (JSONObject)object;
		            		  Double storeCreditVal = (Double) (tempStoreCreditObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(tempStoreCreditObj.get("Amount").toString()));
		            	//	String storeCreditVal = deciFormat.format(tempStoreCreditObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(tempStoreCreditObj.get("Amount")).toString();
		            		 paymentParts += GetMergedPaymentPart(loopBlockTwo,"","Store Credit",deciFormat.format(storeCreditVal) );
						}
		            	
		            	
		            	/*JSONObject tempStoreCreditObj = null;
		            	try {
		            		tempStoreCreditObj = (JSONObject)jsonMainObj.get("StoreCredit");
		            	} catch(ClassCastException e) {
		            		tempStoreCreditObj = (JSONObject)((JSONArray)jsonMainObj.get("StoreCredit")).get(0);
		            	}	
		                paymentParts += GetMergedPaymentPart(loopBlockTwo,"","SC",
		                		deciFormat.format(tempStoreCreditObj.get("Amount").toString()) );*/
		            }
		            
		            
		            if (jsonMainObj.get("CreditCard") != null)
		            {
		            	JSONArray creditCardArr = (JSONArray)jsonMainObj.get("CreditCard");
		            	
		            	for (Object object : creditCardArr) {
							
		            		JSONObject tempObj = (JSONObject)object;
		            		 Double creditCardVal = (Double) (tempObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(tempObj.get("Amount").toString()));
		            		//String creditCardVal = deciFormat.format(tempObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(tempObj.get("Amount")).toString() ;
		            		 
		            		 // Added after Payement.Number implementation
		            		 String ccNum = tempObj.get("Number").toString();
		            		 
			            		String creditCardNum = Constants.STRING_NILL;
								if(!ccNum.isEmpty() && ccNum.length() >= 4){
									creditCardNum =maskCardNumber(ccNum);
								}
								else if(!ccNum.isEmpty()){
									creditCardNum = ccNum.toString();
								}
		            		paymentParts += GetMergedPaymentPart(loopBlockTwo,	creditCardNum ,"Credit Card - "+tempObj.get("Type").toString(),	deciFormat.format(creditCardVal) );
						}
		            }	
		                
		            
		            if (jsonMainObj.get("DebitCard") != null)    {
		            	
		            	// Added after new DR Schema
		            	JSONArray debitCreditCardArr = (JSONArray)jsonMainObj.get("DebitCard");
		            	
		            	for (Object object : debitCreditCardArr) {
							
		            		JSONObject tempDebitCreditObj = (JSONObject)object;
		            		
		            		 Double debitVal = (Double) (tempDebitCreditObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(tempDebitCreditObj.get("Amount").toString()));
		            		//String debitVal = deciFormat.format(tempDebitCreditObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(tempDebitCreditObj.get("Amount")).toString();
		            		
		            		 // Added after Payement.Number implementation
		            		 String debitNum = tempDebitCreditObj.get("Number").toString();
		            		 
			            		String debitCardNum = Constants.STRING_NILL;
								if(!debitNum.isEmpty() && debitNum.length() >= 4){
									debitCardNum =maskCardNumber(debitNum);	
								}else if(!debitNum.isEmpty()){
									debitCardNum=debitNum.toString();
								}
		            		 
		            		 
		            		 paymentParts += GetMergedPaymentPart(loopBlockTwo,debitCardNum,"Debit Card",	deciFormat.format(debitVal) );
						}
		            	
		            	/*
		            	JSONObject tempDebitCreditObj = null;
		            	try {
		            		tempDebitCreditObj = (JSONObject)jsonMainObj.get("DebitCard");
		            	} catch(ClassCastException e) {
		            		tempDebitCreditObj = (JSONObject)((JSONArray)jsonMainObj.get("DebitCard")).get(0);
		            	}
		            	
		                paymentParts += GetMergedPaymentPart(loopBlockTwo,
		                		tempDebitCreditObj.get("Number").toString(),
		                		"DEBIT",
		                		deciFormat.format(tempDebitCreditObj.get("Amount").toString())  );*/
		            }
		            
		            if (jsonMainObj.get("Gift") != null)
		            {
		            	
		            	// Added after new DR Schema
		            	JSONArray giftArr = (JSONArray)jsonMainObj.get("Gift");
		            	
		            	for (Object object : giftArr) {
							
		            		JSONObject tempGiftObj = (JSONObject)object;
		            		 Double giftVal = (Double) (tempGiftObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(tempGiftObj.get("Amount").toString()));
		            		//String giftVal = deciFormat.format(tempGiftObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00")  : deciFormat.format(tempGiftObj.get("Amount")).toString();
		            		
		            		 // Added after Payement.Number implementation
		            		 String giftNum = tempGiftObj.get("GiftNum").toString();
		            		 
		            		
		            		 
		            		 paymentParts += GetMergedPaymentPart(loopBlockTwo, giftNum ,"Gift Certificate", deciFormat.format(giftVal));
						}
		            	
		            	
		            	/*JSONObject tempGiftObj = null;
		            	try {
		            		tempGiftObj = (JSONObject)jsonMainObj.get("GiftCertificate");
		            	} catch(ClassCastException e) {
		            		tempGiftObj = (JSONObject)((JSONArray)jsonMainObj.get("GiftCertificate")).get(0);
		            	}
		            	
		                paymentParts += GetMergedPaymentPart(loopBlockTwo,
		                    "",
		                    "GiftCertificate",
		                    deciFormat.format(tempGiftObj.get("Amount").toString()) );*/
		            }
		            
		            if (jsonMainObj.get("GiftCard") != null) {
		            	
		            	// Added after new DR Schema
		            	JSONArray giftArr = (JSONArray)jsonMainObj.get("GiftCard");
		            	
		            	for (Object object : giftArr) {
							
		            		JSONObject tempGiftCardObj = (JSONObject)object;
		            		 Double giftCrdVal = (Double) (tempGiftCardObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(tempGiftCardObj.get("Amount").toString()));
		            		//String giftCrdVal = deciFormat.format(tempGiftCardObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(tempGiftCardObj.get("Amount")).toString();
		            		

		            		 // Added after Payement.Number implementation
		            		 String gcNum = tempGiftCardObj.get("Number").toString();
		            		 
			            		String giftCardNum = Constants.STRING_NILL;
								if(!gcNum.isEmpty() && gcNum.length() >= 4 ){
									giftCardNum =maskCardNumber(gcNum);
								}
								else if(!gcNum.isEmpty()){
									giftCardNum =gcNum.toString();
								}
		            		 
		            		 
		            		 
		            		 paymentParts += GetMergedPaymentPart(loopBlockTwo, giftCardNum ,"Gift Card",deciFormat.format(giftCrdVal));
						}
		            	
		            	/*JSONObject tempGiftCardObj = null;
		            	try {
		            		tempGiftCardObj = (JSONObject)jsonMainObj.get("GiftCard");
		            	} catch(ClassCastException e) {
		            		tempGiftCardObj = (JSONObject)((JSONArray)jsonMainObj.get("GiftCard")).get(0);
		            	}
		            	
		                paymentParts += GetMergedPaymentPart(loopBlockTwo,"","GC",
		                		deciFormat.format(tempGiftCardObj.get("Amount").toString()) );*/
		                
		            }
		            
		            if (jsonMainObj.get("Charge") != null) {
		            	// Added after new DR Schema
		            	JSONObject chargeObj = (JSONObject)jsonMainObj.get("Charge");
		            	 Double chargeVal = (Double) (chargeObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(chargeObj.get("Amount").toString()));
		            	//String chargeVal = deciFormat.format(chargeObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(chargeObj.get("Amount")).toString();
		            	 paymentParts += GetMergedPaymentPart(loopBlockTwo,"","Charge",	deciFormat.format(chargeVal ));
		            }
		            
		            if (jsonMainObj.get("COD") != null) {
		            	// Added after new DR Schema

		            	JSONObject CODObj = (JSONObject)jsonMainObj.get("COD");
		            	 Double codVal = (Double) (CODObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(CODObj.get("Amount").toString()));
		            	//String codVal = deciFormat.format(CODObj.get("Amount")).toString() .isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(CODObj.get("Amount")).toString() ;
		            	 paymentParts += GetMergedPaymentPart(loopBlockTwo,"","COD ",deciFormat.format(codVal));
		            }
		            
		            if (jsonMainObj.get("Deposit") != null){
		            	// Added after new DR Schema
		            	JSONObject depositObj = (JSONObject)jsonMainObj.get("Deposit");
		            	 Double depositVal = (Double) (depositObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(depositObj.get("Amount").toString()));
		            	//String depositVal = deciFormat.format(depositObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(depositObj.get("Amount")).toString();
		            	 paymentParts += GetMergedPaymentPart(loopBlockTwo,"","Deposit", deciFormat.format(depositVal));
		            }
		            
		            if (jsonMainObj.get("Check") != null) {
		            	
		            	JSONObject checkObj = (JSONObject)jsonMainObj.get("Check");
		            	 Double checkVal = (Double) (checkObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(checkObj.get("Amount").toString()));
		            	//String checkVal = deciFormat.format(checkObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(checkObj.get("Amount")).toString();
		            
		            	 // Added after Payement.Number implementation
	            		 String checkNum = checkObj.get("Number").toString();
		            	 
		                paymentParts += GetMergedPaymentPart(loopBlockTwo, checkNum ,"Check" ,deciFormat.format(checkVal));
		            }
		            
		            if (jsonMainObj.get("FC") != null) {
		            	// Added after new DR Schema
		            	JSONObject FCObj = (JSONObject)jsonMainObj.get("FC");
		            	 Double fcVal = (Double) (FCObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(FCObj.get("Amount").toString()));
		            	//String fcVal = deciFormat.format(FCObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00") : deciFormat.format(FCObj.get("Amount")).toString();
		            	 paymentParts += GetMergedPaymentPart(loopBlockTwo,"","Foreign Currency",	deciFormat.format(fcVal) );
		            }
		            
		        	
		            // Added another 3 tender types(rarely used)
		            
		            if (jsonMainObj.get("Payments") != null) {
		            	
		            	JSONArray paymentsArr = (JSONArray)jsonMainObj.get("Payments");
		            	
		            	for (Object object : paymentsArr) {
							
		            		JSONObject tempPaymentObj = (JSONObject)object;
		            		 Double paymentVal = (Double) (tempPaymentObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(tempPaymentObj.get("Amount").toString()));
		            		//String paymentVal = deciFormat.format(tempPaymentObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00"): deciFormat.format(tempPaymentObj.get("Amount")).toString();
		            		 paymentParts += GetMergedPaymentPart(loopBlockTwo,"","Payments", deciFormat.format(paymentVal ));
						}
		            	
		                
		            }
		            
		            if (jsonMainObj.get("TravelerCheck") != null) {
		            	JSONObject travelerCheckObj = (JSONObject)jsonMainObj.get("TravelerCheck");
		            	 Double travelerCheckVal = (Double) (travelerCheckObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(travelerCheckObj.get("Amount").toString()));
		            	//String travelerCheckVal = deciFormat.format(travelerCheckObj.get("Amount")).toString().isEmpty() ? Double.parseDouble("0.00"): deciFormat.format(travelerCheckObj.get("Amount")).toString(); 
		            	
		            	 // Added after Payement.Number implementation
	            		 String travCheckNum = travelerCheckObj.get("Number").toString();
		            	 
		            	 paymentParts += GetMergedPaymentPart(loopBlockTwo, travCheckNum ,"Traveler Check",deciFormat.format(travelerCheckVal));
		            }
		            
		            if (jsonMainObj.get("FCCheck") != null) {

		            	JSONObject FCCheckObj = (JSONObject)jsonMainObj.get("FCCheck");
		            	 Double fcCheckval = (Double) (FCCheckObj.get("Amount").toString().isEmpty()  ? Double.parseDouble("0.00") : Double.parseDouble(FCCheckObj.get("Amount").toString()));
		            	//String fcCheckval =  deciFormat.format(FCCheckObj.get("Amount")).toString().isEmpty() ?  Double.parseDouble("0.00") :  deciFormat.format(FCCheckObj.get("Amount")).toString();
		            	
		            	 // Added after Payement.Number implementation
	            		 String fcCheckNum = FCCheckObj.get("Number").toString();
		            	 
		            	 paymentParts += GetMergedPaymentPart(loopBlockTwo, fcCheckNum ,"FC Check",deciFormat.format(fcCheckval));
		            }
					
		     //       logger.debug("** After process paymentParts val :  " + paymentParts);
		           
		            
					if(paymentParts != null) {
						
						templateContentStr = templateContentStr.replace(loopBlockTwo, paymentParts);
					}
			}
			
			//logger.debug("--5--");
			
			// Remove ##START## ##END## PH from the template
			templateContentStr = templateContentStr.replace(paymentSetPlaceHoldersArr[0], "").replace(paymentSetPlaceHoldersArr[4],"");
			templateContentStr = templateContentStr.replace(ItemsSetPlaceHoldersArr[0], "").replace(ItemsSetPlaceHoldersArr[5],"");
			
		//	logger.debug("After processing LOOP Contents : "+ templateContentStr);
			
			// Replace All individual place holders place Holders
		  	Set<String> set = userJSONSettingHM.keySet();
	    	String jsonKey;
	    	String[] jsonPathArr;
	    	String jsonKeyValue = "";
	    	 DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");
			 Double amount = (Double) (receiptJSONObj.get("Total").toString().isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(receiptJSONObj.get("Total").toString()));
			 templateContentStr = templateContentStr.replace(Constants.DR_RECEIPT_AMOUNT, (String)decimalFormat.format(amount));
	    	Double totalSaving = (Double)(receiptJSONObj.get("TotalSavings").
	    			toString().isEmpty() ? Double.parseDouble("0.00") : Double.parseDouble(receiptJSONObj.get("TotalSavings").toString()));
	    	templateContentStr = templateContentStr.replace(Constants.DR_RECEIPT_FOOTER, (String)decimalFormat.format(totalSaving)).
	    			replace(Constants.DR_RECEIPT_TOTALSAVING, (String)decimalFormat.format(totalSaving));
	    	
			for (String phStr : set) {
				
				jsonKeyValue = Constants.STRING_NILL;
	    		JSONObject parentObject = null;
	    		JSONArray parentArrayObject = null;
			
		    		if(templateContentStr.indexOf(phStr) != -1) {			// PH exists in template
		    			
		    			jsonKey = userJSONSettingHM.get(phStr);
		    	//		logger.debug("*******<< jsonKey : "+ jsonKey);
		    			
		    			if(jsonKey.indexOf("_$$_") != -1) {
		    				
		    		//		logger.debug("contains dollar .....");
		    				
		    				String[] jsonKeytokens = jsonKey.split(Pattern.quote("_$$_"));

		    				for(int j=0;j<jsonKeytokens.length;j++) {
		    					
					               // . path exists in json key 
			    				
			    				jsonPathArr = jsonKeytokens[j].split(Pattern.quote("."));
			    				//logger.debug("*******<< jsonKey : "+ jsonPathArr[0] + " ::: " + jsonPathArr[1]);
			    				
			    				try {
			    					
			    					//logger.debug("SINGLE OBJ CREATED...");
			    					parentObject = (JSONObject)jsonMainObj.get(jsonPathArr[0]);
			    				} catch(ClassCastException e) {
			    					
			    				//	logger.debug("ARRAY OBJ CREATED...");
			    					parentArrayObject = (JSONArray)jsonMainObj.get(jsonPathArr[0]);
			    				//	logger.debug("--Array---"+ parentArrayObject);
			    					parentObject  = (JSONObject)parentArrayObject.get(0);
			    				//	logger.debug("--First element in array---"+ parentObject);
			    					//logger.debug("--Second element in array---"+ (JSONObject)parentArrayObject.get(1));
			    				}	
			    			
			    				if(parentObject != null) {
			    					
			    					try {
			    						
			    						if(parentObject.get(jsonPathArr[1]) != null) {  
			    							
			    							jsonKeyValue += (String)parentObject.get(jsonPathArr[1]) + " ";
			    						}	
			    					} catch(ClassCastException e) {
			    						try {
			    							jsonKeyValue += ((Double)parentObject.get(jsonPathArr[1]))+ " ";
			    						} catch(ClassCastException f) { 
			    							jsonKeyValue += ((Long)parentObject.get(jsonPathArr[1])) + " ";
			    						}	
			    					} 
			    					
			    					
			    				} else {
			    					jsonKeyValue = Constants.STRING_NILL;
			    					
			    		//			logger.debug("** Parent JSON is null *****");
			    				} 
			    			
		    				}
		    				
		    				//logger.debug("*** Replacing " + phStr + " with " + jsonKeyValue);
	    					//templateContentStr = templateContentStr.replaceAll(phStr, jsonKeyValue);
		    				templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
		    				
		    			} else if(jsonKey.contains(".")) {			               // . path exists in json key 
		    				
		    				jsonPathArr = jsonKey.split(Pattern.quote("."));
		    				//logger.debug("*******<< jsonKey : "+ jsonPathArr[0] + " ::: " + jsonPathArr[1]);
		    				
		    				try {
		    					
		    					//logger.debug("SINGLE OBJ CREATED...");
		    					parentObject = (JSONObject)jsonMainObj.get(jsonPathArr[0]);
		    				} catch(ClassCastException e) {
		    					
		    					//logger.debug("ARRAY OBJ CREATED...");
		    					parentArrayObject = (JSONArray)jsonMainObj.get(jsonPathArr[0]);
		    					//logger.debug("--Array---"+ parentArrayObject);
		    					parentObject  = (JSONObject)parentArrayObject.get(0);
		    				//	logger.debug("--First element in array---"+ parentObject);
		    					//logger.debug("--Second element in array---"+ (JSONObject)parentArrayObject.get(1));
		    				}	
		    			
		    				if(parentObject != null) {
		    					
		    					try {
		    						
		    						if(parentObject.get(jsonPathArr[1]) != null) {  
		    							
		    							jsonKeyValue = (String)parentObject.get(jsonPathArr[1]);
		    						}	
		    					} catch(ClassCastException e) {
		    						try {
		    							jsonKeyValue = ((Double)parentObject.get(jsonPathArr[1])).toString();
		    						} catch(ClassCastException f) { 
		    							jsonKeyValue = ((Long)parentObject.get(jsonPathArr[1])).toString();
		    						}	
		    					} 
		    					
		    				//	logger.debug("*** Replacing " + phStr + " with " + jsonKeyValue);
		    				} else {
		    					jsonKeyValue = Constants.STRING_NILL;
		    				//	logger.debug("** Parent JSON is null *****");
		    				} 
		    				
		    				//templateContentStr = templateContentStr.replaceAll(phStr, jsonKeyValue);
		    				templateContentStr = templateContentStr.replace(phStr, jsonKeyValue);
		    				
		    			}  
		    		}
		    } // for 
	    	 
			
			
			
			//templateContentStr = replaceExisting_ExtPrcWithTax_IfAny(extPrcWithTaxRelated, jsonMainObj, templateContentStr);
			
			templateContentStr = replaceExisting_ExtPrcWithTax_IfAny(jsonMainObj, templateContentStr);
			//logger.debug("--6--");
		//	logger.debug("After processing INDIVIDUAL Contents : "+ templateContentStr);
			
			// remove all no-value place holders form template
			String digiRcptDefPHStr = PropertyUtil.getPropertyValue("DRPlaceHolders");
			if(digiRcptDefPHStr != null) {
				String[] defPHArr = digiRcptDefPHStr.split(Pattern.quote(","));
				for(int i=0;i<defPHArr.length;i++) {
					templateContentStr = templateContentStr.replaceAll(defPHArr[i],"");
				}
				
				//logger.debug("After Removing no-value Place holder(#PH#) values.. Content is :" + templateContentStr );
			}
			
			// Add domain name to images urls 	
			String appUrl = PropertyUtil.getPropertyValue("subscriberIp");			
			templateContentStr = templateContentStr.replaceAll("/subscriber/SystemData/digital-templates/",  appUrl +"/subscriber/SystemData/digital-templates/" );
			
			// Get email from json request..
			JSONObject receiptObj = (JSONObject)jsonMainObj.get("Receipt");
			String emailId = (String)receiptObj.get("BillToEMail");
			
			if(emailId == null || emailId.trim().length() == 0 ) {
				errorCode = 300009;
				jsonMessage = "Bill-To-EmailId is null in json request";
				logger.debug("**** Bill-To-EmailId is null is json request returning ...");
				return null;
			}else if(!Utility.validateEmail(emailId)){
				errorCode = 300010;
				jsonMessage = "Bill-To-EmailId is invalid ";
				logger.debug("**** Bill-To-EmailId is invalid is json request returning ...");
				return null;
			}
			
			/*templateContentStr = templateContentStr.replaceAll(Pattern.quote("|^"), "[").replaceAll(Pattern.quote("^|"), "]");
		//	logger.debug("---->>>>>>>"+ templateContentStr);
			// Get Contact from email ID and replace html Place holders .
			String tempStr2 = replaceContactsPHValues(templateContentStr,emailId, user);
			
			if(tempStr2 != null) {
				
				templateContentStr =  tempStr2;
			} 
			
			
		//	logger.debug("1001 : after process : "+ templateContentStr);
			// remove empty PH 
		//	logger.debug(">>>>>>>>>>>>>> phStr ==="+ phStr);
			if(phStr.trim().length() > 0) {
				String[] temp = phStr.split(",");
				String Str = "";
				for (String strings : temp) {
					
					Str = "[GEN_"+strings + "]";
					//logger.debug("-->>"+templateContentStr.indexOf(Str));
					templateContentStr = templateContentStr.replace(Str, "");
					logger.debug("Replacing " + Str + " with " + "''");
				}
			//	logger.debug("1004 : after process : "+ templateContentStr);
			}*/
			/*//need to set all the user settings
			DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(user.getUserId());
			
			
			
			if(digitalReceiptUserSettings != null) {
				//subject
				if(digitalReceiptUserSettings.getSubject() != null ) {
					subject =  digitalReceiptUserSettings.getSubject() ;
				}
				
				//fromName
				
				
				
			}
			*/
			
			
			//preparing html content which handles clicks
			String htmlContent = PrepareFinalHtml .prepareStuff(templateContentStr,"");
			
			// Add to Email Queue
			String subject = "Your Purchase Receipt";
	    	
			/*DigitalReceiptSeneder digitalReceiptSeneder = new DigitalReceiptSeneder(subject, templateContentStr,
					emailId,user,usersDao,drSentDao,messagesDao,digitalReceiptUserSettingsDao);
			 */
			
	    	DigitalReceiptSender digitalReceiptSeneder = new DigitalReceiptSender(subject, htmlContent,
	    											emailId,user,usersDao,messagesDao,digitalReceiptUserSettingsDao,
	    											contactsDao, organizationStoresDao, contactsLoyaltyDao, retailProSalesDao, posMappingDao, jsonMainObj, drSentDao,digitalReceiptsJSON.getDrjsonId(),selTemplateName);
	    	
	    	digitalReceiptSeneder.start();
	    	
	    	statusFlag = 1;
	    	errorCode = 0;
	    	jsonMessage = "Digital receipt submitted successfully . Mail would be sent in a moment.";
	    	
	    	
	    	
	    	//Utility.pingInstantMailSender();
	    	/*if(!emailQueueScheduler.isRunning()) {
	    		emailQueueScheduler.run();
	    	}
	    	*/
	    	logger.debug("*************** EXITING***********************");
	    		    	
	    	return null;
	    } catch(Exception e) {
	    	logger.error("Exception ::",e);
	    //	e.printStackTrace();
	    	//logger.error("Exception :::",e);
	    	return null;
	    } finally {
	    	
	    	//jsonMessage
	    	
	    	JSONObject rootObject = new JSONObject();
	    	JSONObject replyObject = new JSONObject();
	    	JSONObject returnParams = new JSONObject();
	    
	    	Enumeration<String> reqMap = request.getParameterNames();
	    	
	    	while(reqMap.hasMoreElements()) {
	    		
	    		String ele = reqMap.nextElement();
	    		
	    		if(ele.equals("submit") || ele.equals("userName") || ele.equals("userOrg") || ele.equals("jsonValue")) {
	    			continue;
	    		} 
	    		   		
	    		returnParams.put(ele, request.getParameter(ele));
	    		
	    		logger.debug(">>>"+ ele  + "  :  " +request.getParameter(ele) );
	    	} // while
	    	
	    	JSONObject status = new JSONObject();
	    	if(returnParams.size() > 0) {
	    		status.put("RETURNPARAMETERS", returnParams);
	    	}
	    
	    	replyObject.put("STATUS", statusFlag == 1 ? "Success" : "Failure");
	    	replyObject.put("MESSAGE", jsonMessage);
	    	replyObject.put("ERRORCODE", errorCode);
	    	
	    	status.put("STATUS",replyObject);
	    	
	    	rootObject.put("RESPONSEINFO", status);
	    	
	    	responseWriter.println(rootObject.toJSONString());
	    	responseWriter.flush();
            responseWriter.close();
	    }
		
	}
	
	private String phStr;
	
	private String replaceContactsPHValues(String templateContentStr, String emailId, Users user) {
		try {
			
			phStr = getPlaceHolders(templateContentStr);
			
		//	logger.debug("phStr >>>>>>>>>>>>>>>>> "+ phStr);
			
			if(phStr.trim().length() < 1) {
				
		//		logger.debug(" *** No place holders exist ... returning ..");
				return null;
			}
			
			MailingList mailingList = mailingListDao.findListTypeMailingList(Constants.MAILINGLIST_TYPE_POS,user.getUserId());
			
			if(mailingList == null) {
				
				logger.debug("No POS list exist for the user .. returning ");
				return null;
			}
			
			Long contactId = contactsDao.getContactIdByEmailIdAndMlist(mailingList, emailId);
			
			if(contactId == null) {
				
				logger.debug("Contact Id not found ... returning");
				return null;
			}
			
			Contacts contact = contactsDao.findById(contactId);
			
			if(contact == null) {
				logger.debug("No Contact Object found .. returning.");
				return null;
			}
			
			//Class contactClass = contact.getClass();
			Class strArg[] = new Class[]{String.class};
			Class longArg[] = new Class[]{Long.class};
			Class intArg[] = new Class[]{Integer.TYPE};
			Class calArg[] = new Class[]{Calendar.class};
			
			String[] fieldArr = phStr.split(",");
			String tempStr = null;
			Calendar tempCal = null;
			Long tempLong = null;
			int tempInt = 0;
			for (String contField : fieldArr) {
			    logger.debug("--1---");
				tempStr = null;
				tempInt = 0; tempLong = null;
				tempCal = null;
				
				if(contField.equals("email")) {
					
					logger.debug("--2---");
					tempStr = contact.getEmailId();
				}	
				else if (contField.equals("firstName")) {
					
					tempStr = contact.getFirstName();
				}
				else if (contField.equals("lastName")) {
					
					//Contacts.class.getMethod("setLastName",strArg);
					tempStr = contact.getLastName();
				}
				else if (contField.equals("addressOne")) {
					
					tempStr = contact.getAddressOne();
				}
				else if (contField.equals("addressTwo")) {
					
					//Contacts.class.getMethod("setAddressTwo",strArg);
					tempStr = contact.getAddressTwo();
				}
				else if (contField.equals("city")) {
					
					//Contacts.class.getMethod("setCity",strArg);
					tempStr = contact.getCity();
				}
				else if (contField.equals("state")) {
					
					//Contacts.class.getMethod("setState",strArg);
					tempStr = contact.getState();
				}
				else if (contField.equals("country")) {
					
					//Contacts.class.getMethod("setCountry",strArg);
					tempStr = contact.getCountry();
				}
				else if (contField.equals("pin") || contField.equals("zip")){
					
					//Contacts.class.getMethod("setPin",intArg);
//					tempInt = contact.getPin();
					tempStr = contact.getZip();
				}
				else if (contField.equals("phone")|| contField.equals("MobilePhone")) {
					
					//Contacts.class.getMethod("setPhone",longArg);
					tempStr = contact.getMobilePhone();
				}
				else if (contField.equals("gender")) {
					
					//Contacts.class.getMethod("setGender",strArg);
					tempStr = contact.getGender();
				}
				else if (contField.equals("birthDay")) {
					
					//Contacts.class.getMethod("setBirthDay",calArg);
					tempCal = contact.getBirthDay();
				}
				else if (contField.equals("anniversary")) {
					
					//Contacts.class.getMethod("setAnniversary",calArg);
					tempCal = contact.getAnniversary();
				}
				//logger.debug("templates before :>>> "+ templateContentStr);
				String tempStrVal = "[GEN_"+ contField+"]";
				
				if(tempStr != null) {
					logger.debug("--3---");
					templateContentStr =templateContentStr.replaceAll(Pattern.quote(tempStrVal), tempStr);
				} 
				else if(tempInt != 0) {
					templateContentStr =templateContentStr.replaceAll(Pattern.quote(tempStrVal), tempInt+"");
				} 
				else if(tempCal != null) {
					templateContentStr =templateContentStr.replaceAll(Pattern.quote(tempStrVal), MyCalendar.calendarToString(tempCal,
							MyCalendar.FORMAT_DATEONLY));
				} else if (tempLong != null) {
					templateContentStr =templateContentStr.replaceAll(Pattern.quote(tempStrVal), tempLong+"");
				} 
				
			}
		//	logger.debug("templates after :>>> "+ templateContentStr);
			
			return templateContentStr;
			
			
		} catch(Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
	
	private String replaceExisting_ExtPrcWithTax_IfAny(JSONObject jsonMainObj, String templateContentStr){
		
		
		try{


			
			if(templateContentStr == null){
				return templateContentStr;
			}
			
			JSONArray jsonArr = null;

			jsonArr  = (JSONArray)jsonMainObj.get("Items");

			if(jsonArr == null){
				return templateContentStr;
			}






			String extPrcWithTax_ExtPrc_JsonKey = "";
			String extPrcWithTax_Tax_JsonKey = "";

			for(int i = 0; i < jsonArr.size(); i++)
			{



				JSONObject jsonObj = (JSONObject)jsonArr.get(i);

				if(templateContentStr.contains("#Item.extPrcWithTax#")){




					extPrcWithTax_ExtPrc_JsonKey = "Items.ExtPrc";

					extPrcWithTax_Tax_JsonKey = "Items.Tax";

					String[] extPrcWithTax_ExtPrc_JsonKeyValArray = extPrcWithTax_ExtPrc_JsonKey.split(Pattern.quote("."));

					String[] extPrcWithTax_Tax_JsonKeyValArray = extPrcWithTax_Tax_JsonKey.split(Pattern.quote("."));


					Double valueExtPrcFromJson = (Double) (jsonObj.get(extPrcWithTax_ExtPrc_JsonKeyValArray[1]).toString().isEmpty()  ? 
							Double.parseDouble("0.00") : Double.parseDouble(jsonObj.get(extPrcWithTax_ExtPrc_JsonKeyValArray[1]).toString()));


					Double valueTaxFromJson = (Double) (jsonObj.get(extPrcWithTax_Tax_JsonKeyValArray[1]).toString().isEmpty()  ? 
							Double.parseDouble("0.00") : Double.parseDouble(jsonObj.get(extPrcWithTax_Tax_JsonKeyValArray[1]).toString()));



					Double sumOfExtPrcAndTax = (valueExtPrcFromJson + valueTaxFromJson);
					sumOfExtPrcAndTax = Math.round(sumOfExtPrcAndTax*100.0)/100.0;

					logger.debug("sumOfExtPrcAndTax >>>>>>>>> "+sumOfExtPrcAndTax);


					DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");
					templateContentStr = templateContentStr.replace("#Item.extPrcWithTax#", decimalFormat.format(sumOfExtPrcAndTax));



				}






			}


			
			
			//templateContentStr = templateContentStr.replaceAll("#Item.extPrcWithTax#", "");
			
			
			
			
			
		}catch(Exception ex){
			logger.error("Exception >>> ",ex);
			return templateContentStr;
		}
		return templateContentStr;
				
				
				
				
			
				
	}
	
	private String getPlaceHolders(String content) {
		try {
		//	logger.debug("+++++++ Just Entered +++++"+ content);
			String cfpattern =  "\\[(GEN_[a-z0-9]*?)]"; //[([GEN_][a-z0-9A-Z]*?)\\]
			Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
			Matcher m = r.matcher(content);

			String ph = null;
			//totalPhSet = new HashSet<String>();
			String cfNameListStr = "";

			try {
				while(m.find()) {

					ph = m.group(1); //.toUpperCase()
					logger.info("Ph holder :" + ph);

					if(ph.startsWith("GEN_")) {
						//totalPhSet.add(ph);
						cfNameListStr = cfNameListStr + (cfNameListStr.equals("") ?  "" : Constants.DELIMETER_COMMA)
								 + ph.substring(4,ph.length());
					}
					/*else if(ph.startsWith("CF_")) {
						//totalPhSet.add(ph);
						cfNameListStr = cfNameListStr + (cfNameListStr.equals("") ?  "" : Constants.DELIMETER_COMMA)
												+ "'" + ph.substring(3) + "'";
					} // if(ph.startsWith("CF_"))
*/			} // while
				
				//logger.debug("+++ Exiting : "+ totalPhSet);
			} catch (Exception e) {
				logger.error("Exception while getting the place holders ", e);
			}

			//logger.info("CF PH cfNameListStr :" + cfNameListStr);

			return cfNameListStr;
		} catch (Exception e) {
			return "";
		}
	}
	
	private String GetMergedPaymentPart(String paymentPart, String number, String type, String Amount )
    {
        String paymentPartStr = paymentPart.replace("#Payment.Amount#",  Amount )
                .replace("#Payment.Number#", number)
                .replace("#Payment.Type#", type);
        return paymentPartStr;
    }
	
	
	private String replaceLoopBlock(String loopBlock, Map<String,String> userJSONSettingHM,JSONObject jsonMainObj) {
		try {
			
			Map<String,String> extPrcWithTaxForItemsMap = new HashMap<String, String>();
			String finalHtmlBlockVal = "";
			Matcher matcher = Pattern.compile("(#\\w+).(\\w+#)").matcher(loopBlock);
			
			//JSONObject jsonObj = null;

			JSONArray jsonArr = null;
			
			 DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00"); 
			
			
			if(matcher.find()) {
				
		//		logger.debug("--PLACE HOLDERS FOUND --"+ matcher.group(0));
				//logger.debug("--1--"+ userJSONSettingHM.get(matcher.group(0)));
				
				if(userJSONSettingHM.get(matcher.group(0)) == null) {
		//			
					logger.debug("*** Place holder value is not found in HashMap ***");
					return null;
				}
				
				String[] arrayElement = userJSONSettingHM.get(matcher.group(0)).split(Pattern.quote("."));
				
				// Items from Items.billTomail
				logger.debug("arrayElement[0]" + arrayElement[0]);
				jsonArr  = (JSONArray)jsonMainObj.get(arrayElement[0]);
				
			} else {
				
				logger.debug("Pattern not found");
				return null;
			}
			
			matcher.reset();
			
			if(jsonArr == null) {
				return null;
			}
			
		//		logger.debug("Array size :" + jsonArr.size());
			
			for(int i = 0; i < jsonArr.size(); i++)   // Loop all array elements
			{	
				
				JSONObject jsonObj = (JSONObject)jsonArr.get(i);
				String tempStr = loopBlock;
				//logger.debug(" Current jsonobj object from the jsonarray is :"+ jsonObj);
			
				while (matcher.find()) {			// Loop all fields
					String val = "";
					String extPrcWithTax_ExtPrc_JsonKey = "";
					String extPrcWithTax_Tax_JsonKey = "";
					boolean processExtPrcWithTax = false;
					
					
					
					String placeHolderStr = matcher.group(0);
					//logger.debug(" Found place holders : " +placeHolderStr);
					try{
						if("#Item.extPrcWithTax#".equals(placeHolderStr)){
							
							extPrcWithTax_ExtPrc_JsonKey = "Items.ExtPrc";
							
							extPrcWithTax_Tax_JsonKey = "Items.Tax";
							
							String[] extPrcWithTax_ExtPrc_JsonKeyValArray = extPrcWithTax_ExtPrc_JsonKey.split(Pattern.quote("."));
							
							String[] extPrcWithTax_Tax_JsonKeyValArray = extPrcWithTax_Tax_JsonKey.split(Pattern.quote("."));
							
							
							Double valueExtPrcFromJson = (Double) (jsonObj.get(extPrcWithTax_ExtPrc_JsonKeyValArray[1]).toString().isEmpty()  ? 
									 Double.parseDouble("0.00") : Double.parseDouble(jsonObj.get(extPrcWithTax_ExtPrc_JsonKeyValArray[1]).toString()));
							
							
							Double valueTaxFromJson = (Double) (jsonObj.get(extPrcWithTax_Tax_JsonKeyValArray[1]).toString().isEmpty()  ? 
									 Double.parseDouble("0.00") : Double.parseDouble(jsonObj.get(extPrcWithTax_Tax_JsonKeyValArray[1]).toString()));
							
							
							
							Double sumOfExtPrcAndTax = (valueExtPrcFromJson + valueTaxFromJson);
							
							sumOfExtPrcAndTax = Math.round(sumOfExtPrcAndTax*100.0)/100.0;
							
							logger.debug("sumOfExtPrcAndTax >>>>>>>>> "+sumOfExtPrcAndTax);
							
							
							
							
							
							extPrcWithTaxForItemsMap.put("#Item.extPrcWithTax#", decimalFormat.format(sumOfExtPrcAndTax));
							
							processExtPrcWithTax = true;
							
						}
					}catch(Exception e){
						logger.error("Exception >>> ",e);
					}
					
					
					String jsonKey = userJSONSettingHM.get(placeHolderStr);
					String[] jsonValArr = null;
					if(jsonKey != null) { 
						
					//logger.debug("*** JSON key path  is : " + jsonKey);
					// if contains _$$_ multi valued keys .
					if(jsonKey.indexOf("_$$_") != -1) {
						
						//logger.debug("******************** Multi values in json Key exist ...");
						//logger.debug("*** json Val is : "+ jsonKey);
						
						String[] jsonKeytokens = jsonKey.split(Pattern.quote("_$$_"));
						
						for(int j=0;j<jsonKeytokens.length;j++) {
							
							//logger.debug("Individual key is  : "+ jsonKeytokens[j]);
							jsonValArr = jsonKeytokens[j].split(Pattern.quote("."));
							//logger.debug("key is "+ jsonValArr[1]);
							
							if(jsonObj.get(jsonValArr[1]) == null) {
								
								logger.debug("**** Excepted value not found in the json object ***");
								continue;
							}
							
//							logger.debug("***^^ replace template with object value  : "+ jsonObj.get(jsonValArr[1])+ " key is "+ jsonValArr[1]);
							
							try {
								
								val += (String)jsonObj.get(jsonValArr[1]) + " ";
							} catch(ClassCastException e) {
								try {
									val += ((Long)jsonObj.get(jsonValArr[1])).toString() + " ";
								} catch(ClassCastException f) {
									val += ((Double)jsonObj.get(jsonValArr[1])).toString() + " ";
								}	
							} 
						}
						//logger.debug("Value(1) is : " + val);
						tempStr = tempStr.replace(placeHolderStr,val);
					} else {	
					
						//logger.debug("<<<< json Val is : "+ jsonKey);
						

						jsonValArr = jsonKey.split(Pattern.quote("."));
		//				logger.debug("***$$ replace template with object value  : "+ jsonObj.get(jsonValArr[1])+" key is "+ jsonKey);
						
						if(jsonObj.get(jsonValArr[1]) == null) {
							logger.debug("**** Excepted value not found in the json object ***");
							continue;
						}
						
						
						try {
							//val = (String)jsonObj.get(jsonValArr[1]);
							if(jsonKey.equals(Constants.DR_ITEM_INVC_ITEM_PRC)|| 
									jsonKey.equals(Constants.DR_ITEM_EXTPRC) ||	
									jsonKey.equals(Constants.DR_ITEM_DOCI_TEM_DISC_AMT)||
									jsonKey.equals(Constants.DR_ITEM_DOC_ITEM_DISC) ||
									jsonKey.equals(Constants.DR_ITEM_TAX) ||
									jsonKey.equals(Constants.DR_ITEM_DOC_ITEM_ORG_PRC) ||
									jsonKey.equals(Constants.DR_ITEM_DOC_ITEM_EXT_ORG_PRC)){
								 Double value = (Double) (jsonObj.get(jsonValArr[1]).toString().isEmpty()  ? 
										 Double.parseDouble("0.00") : Double.parseDouble(jsonObj.get(jsonValArr[1]).toString()));
								
								 val =(String)decimalFormat.format(value);
								 logger.debug("jsonKey ========="+jsonKey+" &&& Value ====="+val);
								 if(jsonKey.equals(Constants.DR_ITEM_DOC_ITEM_DISC) && val.equals("0.00")){
									 
									 val = Constants.STRING_NILL;
								 }
								
								 
							}else{
								val = (String)jsonObj.get(jsonValArr[1]);
							}
						} catch(ClassCastException e) {
							try  {
								val = ((Long)jsonObj.get(jsonValArr[1])).toString();
							} catch(ClassCastException f) {
								val = ((Double)jsonObj.get(jsonValArr[1])).toString();
							}
						}
						
						//logger.debug("Value(2) is : " + val);
						tempStr = tempStr.replace(matcher.group(0),val);
						logger.debug("tempStr 1 >>> "+tempStr);
					}  
				  } // if
					try{

						if(processExtPrcWithTax){
							tempStr = tempStr.replace("#Item.extPrcWithTax#",extPrcWithTaxForItemsMap.get("#Item.extPrcWithTax#"));
							extPrcWithTaxForItemsMap.clear();

							logger.debug("tempStr extPrc related >>> "+tempStr);
						}
						
					}catch(Exception e){
						logger.error("Exception >>>>>>>> ",e);
					}
					
				}
				
				logger.debug("tempStr >>> "+tempStr);
				matcher.reset();
				
				finalHtmlBlockVal = finalHtmlBlockVal + tempStr; 
				
			}
			
			//logger.debug("final block to replace is "+ finalHtmlBlockVal);
			return finalHtmlBlockVal;
		} catch(Exception e) {
			
			logger.error("Exception ::" , e);
			return null;
		} 
	} 
	
	// Added for any card Masking
	public static String maskCardNumber(String ccnum){
	        int total = ccnum.length();
	        int endlen = 4;
	        int masklen = total-( endlen) ;
	        StringBuffer maskedbuf = new StringBuffer(ccnum.substring(0,0));
	        for(int i=0;i<masklen;i++) {
	            maskedbuf.append('X');
	        }
	        maskedbuf.append(ccnum.substring(masklen, total));
	        String masked = maskedbuf.toString();
	        return masked;
    }
	
	public String[] getItemsArray(String itemHtml){
		/*String text = "<tr><td>#Item.Description1#</td><td>#Item.Quantity#</td>"
						+ "<td>#Item.Discount#</td><td>#Item.DiscountPercent#</td>"
						+ "<td>#Item.UnitPrice#</td><td>#Item.Total#</td></tr>";
		*/
		 String pattern = "<td.*?>([^<]+)</td.*?>";
		   Pattern p = Pattern.compile(pattern);
	        Matcher m = p.matcher(itemHtml);
		
				
		 String resultStr =Constants.STRING_NILL;
        while (m.find()) {
        	       	
        	String itemValue = m.group(1);
        	if(! resultStr.isEmpty())	resultStr+=Constants.DELIMETER_COMMA;
        	resultStr += itemValue;
        	
        }
      String [] itemsArray = resultStr.split(Constants.DELIMETER_COMMA);
	 // logger.debug("itemsArray size ::"+itemsArray.length+ resultStr);
      return itemsArray;  
	}
	
	public String replaceItemArray(String loopBlockOne, String ItemPHTag, Double amtVal) {
	
		String itemHolder = loopBlockOne;
		try {
			//int itemHolderIndex = 0;
			 String [] itemArray = getItemsArray(loopBlockOne);
			 DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");
			int rightAlignIndex = (itemArray.length-2);
			for (int itemHolderIndex = 0; itemHolderIndex<itemArray.length ; itemHolderIndex++) {
				if(itemHolderIndex == rightAlignIndex) {
					//logger.debug("in if ::"+ItemPHTag + itemHolderIndex+ itemArray[itemHolderIndex] + itemArray[itemHolderIndex+1]);
					try {
						String str = itemArray[itemHolderIndex];
						//String substr = str.substring(0,str.length()-1);
						String originalitemSubStr = itemHolder.substring(0, (itemHolder.indexOf(str)+str.length()))+"</td>";
						String itemSubStr = originalitemSubStr.replace(str, "<strong> "+ItemPHTag+" </strong>");
						//itemHolder = itemHolder.replace(str, "<strong> "+ItemPHTag+" </strong>");
						itemHolder = itemHolder.replace(originalitemSubStr, itemSubStr);
						//logger.debug("itemHolder 1::"+itemHolder);
						itemHolder = itemHolder.replace(itemArray[itemHolderIndex+1], "<div align='right'><strong>"+ decimalFormat.format(amtVal)+"</strong></div>");
						//logger.debug("itemHolder2 ::"+itemHolder);
						
						
						try{
							itemHolder = itemHolder.replace("text-align:left", "text-align:right");
						}catch(Exception e){
							logger.error("Exception >>>>>>>>>>>>> ",e);
						}
						
						//logger.debug("prepared  itemHolder ::"+itemHolder);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error(e);
					}
					break;
					
				}else{
					//logger.debug("in else ::"+itemHolderIndex);
					
					String str = itemArray[itemHolderIndex];
					//String substr = str.substring(0,str.length()-1);
					String originalitemSubStr = itemHolder.substring(0, (itemHolder.indexOf(str)+str.length()))+"</td>";
					String itemSubStr = originalitemSubStr.replace(str, "&nbsp;");
					//itemHolder = itemHolder.replace(str, "<strong> "+ItemPHTag+" </strong>");
					itemHolder = itemHolder.replace(originalitemSubStr, itemSubStr);
					//logger.debug("itemHolder 1::"+itemHolder);
					//itemHolder = itemHolder.replace(itemArray[itemHolderIndex+1], "<strong>"+ decimalFormat.format(amtVal)+"</strong>");
					//logger.debug("itemHolder2 ::"+itemHolder);
					
					//itemHolder = itemHolder.replace(itemArray[itemHolderIndex], "&nbsp;");
				}
					//itemHolderIndex ++;
			}
			logger.debug("itemHolder3 ::"+itemHolder);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		return itemHolder;
	}
	
	
private String replaceItemArrayNew(String loopBlockOne, String ItemPHTag, Double amtVal){
		
		
		String itemHolder = loopBlockOne;
		String preparedString="";
		try{
			final String COLSPAN = "COLSPAN";
			final String LABEL = "LABEL";
			final String VALUE = "VALUE";
			final String CELLSPACING = "CELLSPACING";
			final String BENEATH_ITEMS_TR_STRUCTURE = 
					"<tr>"+ 
							"<td colspan="+COLSPAN+">" +
							"<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\""+CELLSPACING+"\">" +
							"<tr>" +
							"<td width=\"50%\"> </td>" +
							"<td width=\"20%\" style=\"text-align:left;\">"+LABEL+"</td>" +
							"<td width=\"30%\" style=\"text-align:right;\">"+VALUE+"</td>"+
							"</tr>"+
							"</table>"+
							"</td>"+
							"</tr>";


			

			
			
			String [] itemArray = getItemsArray(loopBlockOne);
			logger.info("itemArray   >>>>>>>>>>>>>>>>>>>>>>> "+Arrays.toString(itemArray) );
			DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");
			
			
			
			
			if(ItemPHTag.trim().length() == 0){
				preparedString = BENEATH_ITEMS_TR_STRUCTURE.replace(COLSPAN, (itemArray.length)+"").
						replace(LABEL, "").replace(VALUE, "").replace(CELLSPACING, "7");
				
				logger.info("line spacing >>> "+preparedString);
				return preparedString;
			}
			
			
			preparedString = BENEATH_ITEMS_TR_STRUCTURE.replace(COLSPAN, (itemArray.length)+"").
					replace(LABEL, "<strong>"+ItemPHTag+"</strong>").replace(VALUE, "<strong>"+ decimalFormat.format(amtVal)+"</strong>")
					.replace(CELLSPACING, "0");
			
			
			
			
			logger.info("preparedString >>> "+preparedString);
			

		}catch(Exception e){
			logger.error("Exception >>> ",e);
			return itemHolder;
		}
		
		
		
		
		
		return preparedString;
		
	}
	
	
	
}
