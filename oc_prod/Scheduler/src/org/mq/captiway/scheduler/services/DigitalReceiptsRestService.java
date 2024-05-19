package org.mq.captiway.scheduler.services;

import java.io.BufferedReader;



import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Request;
import org.apache.commons.codec.net.QuotedPrintableCodec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.captiway.scheduler.EmailQueueScheduler;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.DigitalReceiptMyTemplate;
import org.mq.captiway.scheduler.beans.DigitalReceiptUserSettings;
import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.MyTemplates;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.DigitalReceiptMyTemplatesDao;
import org.mq.captiway.scheduler.dao.DigitalReceiptUserSettingsDao;
import org.mq.captiway.scheduler.dao.EmailQueueDao;
import org.mq.captiway.scheduler.dao.EmailQueueDaoForDML;
import org.mq.captiway.scheduler.dao.MailingListDao;
import org.mq.captiway.scheduler.dao.MyTemplatesDao;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.Utility;
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
	
	
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	public DigitalReceiptsRestService() {
		
	}
	
	UsersDao usersDao;
	EmailQueueDao emailQueueDao;
	EmailQueueDaoForDML emailQueueDaoForDML;
	public EmailQueueDaoForDML getEmailQueueDaoForDML() {
		return emailQueueDaoForDML;
	}

	public void setEmailQueueDaoForDML(EmailQueueDaoForDML emailQueueDaoForDML) {
		this.emailQueueDaoForDML = emailQueueDaoForDML;
	}

	DigitalReceiptMyTemplatesDao digitalReceiptMyTemplatesDao;
	EmailQueueScheduler emailQueueScheduler;
	MailingListDao mailingListDao;
	ContactsDao contactsDao;
	DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao;
	DigitalReceiptUserSettings digitalReceiptUserSettings;
	
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
	
	
	public EmailQueueScheduler getEmailQueueScheduler() {
		return emailQueueScheduler;
	}

	public void setEmailQueueScheduler(EmailQueueScheduler emailQueueScheduler) {
		this.emailQueueScheduler = emailQueueScheduler;
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

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		PrintWriter responseWriter = response.getWriter();
		String jsonMessage = "";
		byte statusFlag = 0;
		response.setContentType("application/json");
		
	    try {
	    	
	    	//if(logger.isDebugEnabled()) logger.debug("*************** JUST ENTERED ***********************");
	    	
	    	String userFullDetails = "";
	    	
	    	if( (request.getParameter("userName") != null && request.getParameter("userName").length() > 1) &&
	    		(request.getParameter("userOrg") != null &&  request.getParameter("userOrg").length() > 1) ) {	
	    			
	    		userFullDetails =  request.getParameter("userName") + Constants.USER_AND_ORG_SEPARATOR +
	    				request.getParameter("userOrg");
	    	} else {
	    		jsonMessage ="Given User name or Organization details not found .";
	    	    if(logger.isDebugEnabled()) logger.debug("Required user name and Organization fields are not valid ... returning ");
	    	    return null;
	    	}
	    	
	    	
	    	if(request.getParameter("jsonValue") == null ||  request.getParameter("jsonValue").length() < 1 ) {
	    		jsonMessage = "JSON value is not valid."; 
	    		if(logger.isDebugEnabled()) logger.debug("json value is null , returning.");
	    		return null;
	    	}
	    	

	    	/*if(request.getParameter("userId") == null ||  request.getParameter("userId").length() < 1 ) {
	    		
	    		if(logger.isDebugEnabled()) logger.debug("User Id is null .");
	    		userId = 249l;
	    		//return null;
	    	} else {
	    	
	    	    try {
	    	    	userId = Long.parseLong(request.getParameter("userId"));
	    	     } catch(NumberFormatException e) {
	    	    	if(logger.isDebugEnabled()) logger.debug("User Id is not proper .. Number Format Exception , returning.");
	    	    	return null;
	    	     }
	    	} */   
	    	
	    	String userJSONSettings = PropertyUtil.getPropertyValueFromDB("DigitalReceiptSetting");
	    	
	    	//String userJSONSettings = "#Store.Logo#:http://optculture.com/wp-content/themes/optculture/images/logo.png ^|^#Store.WWW#:Receipt.Store^|^#Store.Number#:Receipt.Store^|^#Store.Name#:Receipt.StoreHeading1^|^#Store.Phone#:Receipt.StorePhone^|^#Store.Email#:Receipt.StoreHeading2^|^#Store.Street#:Receipt.StoreHeading3^|^#Store.City#:Receipt.StoreHeading4^|^#Store.State#:Receipt.StoreHeading5^|^#Store.Zip#:Receipt.StoreHeading6^|^#Store.Cashier#:Receipt.Cashier^|^#Receipt.ID#:Receipt.DocSID^|^#Receipt.Number#:Receipt.InvcNum^|^#Receipt.Date#:Receipt.DocDate_$$_Receipt.DocTime^|^#Receipt.PaymentMethod#:Receipt.Tender^|^#BillTo.Name#:Receipt.BillToFullName^|^#BillTo.Email#:Receipt.BillToEMail^|^#BillTo.Street#:Receipt.BillToAddr1^|^#BillTo.City#:Receipt.BillToAddr2^|^#BillTo.State#:Receipt.BillToAddr3^|^#BillTo.Zip#:Receipt.BillToZip^|^#ShipTo.Name#:Receipt.ShipToFullName^|^#ShipTo.Street#:Receipt.ShipToAddr1^|^#ShipTo.City#:Receipt.ShipToAddr2^|^#ShipTo.State#:Receipt.ShipToAddr3^|^#ShipTo.Zip#:Receipt.ShipToZip^|^#Item.Description#:Items.Desc1_$$_Items.Attr_$$_Items.Size^|^#Item.Quantity#:Items.Qty^|^#Item.UnitPrice#:Items.InvcItemPrc^|^#Item.Total#:Items.ExtPrc^|^#Receipt.Amount#:Receipt.Total^|^#Receipt.Message#:Receipt.InvcComment1^|^#Receipt.Message2#:Receipt.InvcComment2^|^#Receipt.Footer#:Receipt.TotalSavings";
	    		    	
	    	if(userJSONSettings == null) {
	    		jsonMessage ="Given JSON is not found .";
	    		if(logger.isDebugEnabled()) logger.debug("userJSONSettings is null ... returning");
	    		return null;
	    	}
	    	
	    	/*if(userId == null) {
	    		
	    		if(logger.isDebugEnabled()) logger.debug("user Id is null ... returning");
	    		return null;
	    	}*/
	    	
	    	// Get User Obj by Id.
			Users user = usersDao.findByUsername(userFullDetails);
			
			if(user == null) {
				jsonMessage ="Given User name or Organization details not found .";  // Organization
				if(logger.isDebugEnabled()) logger.debug("******** User Object not Found ... returing ");
				return null;
			}
			
			
	    	boolean enabled = digitalReceiptUserSettingsDao.findIsUserEnabled(user.getUserId());
	    	if(!enabled) {
	    		
	    		jsonMessage ="Can not send receipt : user is not enabled with sending ";  // Organization
				if(logger.isDebugEnabled()) logger.debug("******** not enabled ... returing ");
				return null;
	    		
	    		
	    	}
	    	
			
			
	    	ByteArrayInputStream byteArrStrStream  = new ByteArrayInputStream( QuotedPrintableCodec.decodeQuotedPrintable( (request.getParameter("jsonValue") ).getBytes()));
	    	
	    	BufferedReader br = new BufferedReader(new InputStreamReader(byteArrStrStream));
	    	
	    	String tempStr = "";
	    	StringBuffer sb = new StringBuffer();
	    	while(  (tempStr = br.readLine()) != null ) {
	    		
	    		//if(logger.isDebugEnabled()) logger.debug(" JSON VALUE IS  " + jsonStr);
	    		sb.append(tempStr);
	    	}
	    	
	    	String jsonStr = sb.toString();
	    	
	    	JSONObject jsonMainObj = null;
	    	//if(logger.isDebugEnabled()) logger.debug(" Requst Query String : "+  Utility.decodeUrl(jsonStr));
	    	if(jsonStr != null && jsonStr.length() > 1) {
	    		
	    		logger.debug("jsonStr :>>>>>>>" + jsonStr);
	    		jsonMainObj = (JSONObject)JSONValue.parse(jsonStr);
	    		//if(logger.isDebugEnabled()) logger.debug(" JSON VALUE TO STR : " + JSONValue.toJSONString(jsonMainObj));
	    		
	    	} else {
	    		if(logger.isDebugEnabled()) logger.debug("UNable to creeate main obj");
	    		return null;
	    	}
	    	
	    	if(jsonMainObj != null) {
	    		if(!jsonMainObj.containsKey("Items")) {
	    			jsonMainObj = (JSONObject)jsonMainObj.get("Body");
	    		}
	    	} else {
	    		if(logger.isDebugEnabled()) logger.debug("*** Main Object is NUll ***");
	    		jsonMessage = "Unable to parse the JSON request .";
	    		return null;
	    	}
	    	
	    	/*JSONObject jsonObject = (JSONObject)jsonMainObj.get("Items");
	    	
	    	if(logger.isDebugEnabled()) logger.debug("arr 1 :" +jsonObject.get("UDF0"));
	    	if(logger.isDebugEnabled()) logger.debug("arr 2 :" +jsonObject.get("UDF2"));*/
	    	
	    	
	    	// GET User Configured REQUIRED TEMPLATE .
	    	String templateContentStr = "";
	    	//if(logger.isDebugEnabled()) logger.debug(">>>>>>>>>>>>>>"+ digitalReceiptMyTemplatesDao);
	    	String selectedTemplate = digitalReceiptUserSettingsDao.findUserSelectedTemplate(user.getUserId());
	    	//if(logger.isDebugEnabled()) logger.debug("Template Settings : "+ selectedTemplate);
	    	
	    	if(selectedTemplate == null) {
	    		
	    		if(logger.isDebugEnabled()) logger.debug("Digital reciept template is not found .. assigning default template ********");
	    		selectedTemplate = "SYSTEM:Corporate_template";
    			//return null;
	    	}
	    	
	    	if(selectedTemplate.indexOf("MY_TEMPLATE:") != -1) {
	    		
	    		selectedTemplate = selectedTemplate.substring(12);
	    		//if(logger.isDebugEnabled()) logger.debug("Template Name is : "+ selectedTemplate);
	    		DigitalReceiptMyTemplate digitalReceiptMyTemplates = digitalReceiptMyTemplatesDao.findByUserNameAndTemplateName(user.getUserId(), selectedTemplate);
	    		
	    		if(digitalReceiptMyTemplates == null) {
	    			if(logger.isDebugEnabled()) logger.debug("Configured digital reciept template is not found ********");
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
	    	
	    	if(logger.isDebugEnabled()) logger.debug("******** Map userJSONSettingHM :" + userJSONSettingHM);
	    	
	    	// Get Values form Application properties
	    	String paymentSetPlaceHolders = PropertyUtil.getPropertyValue("DRPaymentLoopPlaceHolders"); 
			String ItemsSetPlaceHolders = PropertyUtil.getPropertyValue("DRItemLoopPlaceHolders");
	    	
	    	String[] paymentSetPlaceHoldersArr = paymentSetPlaceHolders.split(",");
			String[] ItemsSetPlaceHoldersArr = ItemsSetPlaceHolders.split(",");
			
			
			// Step 1 : Replace Loopable elements .
			if(logger.isDebugEnabled()) logger.debug("** ItemsSetPlaceHoldersArr length : " + ItemsSetPlaceHoldersArr.length);
			
			if(templateContentStr.indexOf(ItemsSetPlaceHoldersArr[0]) != -1 && templateContentStr.indexOf(ItemsSetPlaceHoldersArr[5]) != -1) 
			{
				String loopBlockOne = templateContentStr.substring(templateContentStr.indexOf(ItemsSetPlaceHoldersArr[0]) + ItemsSetPlaceHoldersArr[0].length(),
						templateContentStr.indexOf(ItemsSetPlaceHoldersArr[5]));
				if(logger.isDebugEnabled()) logger.debug("Items HTML is :"+ loopBlockOne);
				
				String temploopbackContent = loopBlockOne; 
				
				String replacedStr = replaceLoopBlock(loopBlockOne,userJSONSettingHM,jsonMainObj);
				
				if(replacedStr == null) {
					jsonMessage="unable to find proper mappings.";
					if(logger.isDebugEnabled()) logger.debug("**** Exception : Error occured while replacing the item default values ... ");
					return null;
				}
				
					//if(logger.isDebugEnabled()) logger.debug("Items extra fields are getting added....");
					// Add extras prices like tax shipping etc
					JSONObject receiptJSONObj = (JSONObject)jsonMainObj.get("Receipt");
					
					if(receiptJSONObj == null) {
						if(logger.isDebugEnabled()) logger.debug("**** Exception : Receipt Object Not found ***");
						return null;
					}
					
					String itemHolder = "";
					 itemHolder = loopBlockOne;
		            itemHolder = itemHolder.replace("#Item.Description#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Attr#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Description1#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Description2#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Quantity#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.UnitPrice#", "<strong> Sub Total </strong>");
		            itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + receiptJSONObj.get("Subtotal") + "</strong>");
		            replacedStr += itemHolder;

		            // Tax
		            itemHolder = loopBlockOne;
		            itemHolder = itemHolder.replace("#Item.Description#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Description1#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Description2#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Attr#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Quantity#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.UnitPrice#", "Tax");
		            itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + receiptJSONObj.get("TotalTax") + "</strong>");
		            replacedStr += itemHolder;
		            
		            // Shipping
		            if (((JSONObject)jsonMainObj.get("Receipt")).get("Shipping") != null) 
		            {
		                itemHolder = loopBlockOne;
		                itemHolder = itemHolder.replace("#Item.Description#", "&nbsp;");
		                itemHolder = itemHolder.replace("#Item.Attr#", "&nbsp;");
		                itemHolder = itemHolder.replace("#Item.Description1#", "&nbsp;");
		                itemHolder = itemHolder.replace("#Item.Description2#", "&nbsp;");
		                itemHolder = itemHolder.replace("#Item.Quantity#", "&nbsp;");
		                itemHolder = itemHolder.replace("#Item.UnitPrice#", "Shipping");
		                itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + ((JSONObject)jsonMainObj.get("Receipt")).get("Shipping")  + "</strong>");
		                replacedStr += itemHolder;
		            }

		            // Fee
		            if (((JSONObject)jsonMainObj.get("Receipt")).get("Fee") != null) {
		                itemHolder = loopBlockOne;
		                itemHolder = itemHolder.replace("#Item.Description#", "&nbsp;");
		                itemHolder = itemHolder.replace("#Item.Attr#", "&nbsp;");
		                itemHolder = itemHolder.replace("#Item.Description1#", "&nbsp;");
		                itemHolder = itemHolder.replace("#Item.Description2#", "&nbsp;");
		                itemHolder = itemHolder.replace("#Item.Quantity#", "&nbsp;");
		                itemHolder = itemHolder.replace("#Item.UnitPrice#", "Fees-" + ((JSONObject)jsonMainObj.get("Receipt")).get("FeeType"));
		                itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + ((JSONObject)jsonMainObj.get("Receipt")).get("Fee") + "</strong>");
		                replacedStr += itemHolder;
		            }
		            
		            // Total
		            itemHolder = loopBlockOne;
		            itemHolder = itemHolder.replace("#Item.Description#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Attr#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Description1#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Description2#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.Quantity#", "&nbsp;");
		            itemHolder = itemHolder.replace("#Item.UnitPrice#", "<strong> Total </strong>");
		            itemHolder = itemHolder.replace("#Item.Total#", "<strong>" + ((JSONObject)jsonMainObj.get("Receipt")).get("Total") + "</strong>");
		            replacedStr += itemHolder;
				
				if(replacedStr != null) {
					
					templateContentStr = templateContentStr.replace(loopBlockOne, replacedStr);
				}
			}
			
			// Now replace payments PH
			/*if(logger.isDebugEnabled()) logger.debug("** paymentSetPlaceHoldersArr : " + paymentSetPlaceHoldersArr.length);
			if(logger.isDebugEnabled()) logger.debug("--0--"+ templateContentStr );
			if(logger.isDebugEnabled()) logger.debug("--1--"+ paymentSetPlaceHoldersArr[0]);
			if(logger.isDebugEnabled()) logger.debug("--2--"+ templateContentStr.indexOf(paymentSetPlaceHoldersArr[0]));
			if(logger.isDebugEnabled()) logger.debug("--3--"+ paymentSetPlaceHoldersArr[4]);
			if(logger.isDebugEnabled()) logger.debug("--4--"+ templateContentStr.indexOf(paymentSetPlaceHoldersArr[4]));*/
			
			
			if(templateContentStr.indexOf(paymentSetPlaceHoldersArr[0]) != -1 && templateContentStr.indexOf(paymentSetPlaceHoldersArr[4]) != -1) 
			{
				
					String loopBlockTwo = templateContentStr.substring(templateContentStr.indexOf(paymentSetPlaceHoldersArr[0]) + paymentSetPlaceHoldersArr[0].length(),
							templateContentStr.indexOf(paymentSetPlaceHoldersArr[4]));
					
					if(logger.isDebugEnabled()) logger.debug("Payment HTML is :"+ loopBlockTwo);
				
					//String replacedStr2 = replaceLoopBlock(loopBlockTwo,userJSONSettingHM,jsonMainObj);
					//String replacedStr2 = "";
					
					//if(logger.isDebugEnabled()) logger.debug("Payment block after replace is : "+ replacedStr2);
					/*if(replacedStr2 == null) {
						if(logger.isDebugEnabled()) logger.debug("**** Exception : Error occured while replacing the PAYMENT default values ... ");
						return null;
					}*/
					
					// Add extra fields to template
					if(logger.isDebugEnabled()) logger.debug("PAYMENT extra fields are getting added....");
					JSONObject receiptJSONObj = (JSONObject)jsonMainObj.get("Receipt");
					
					if(receiptJSONObj == null) {
						if(logger.isDebugEnabled()) logger.debug("**** Exception : Receipt Object Not found ***");
						return null;
					}
					
					String paymentParts ="";
					
				   if (jsonMainObj.get("Cash") != null)
		            {
		                paymentParts += GetMergedPaymentPart(loopBlockTwo,"","CASH",
		                		Double.parseDouble((((JSONObject)jsonMainObj.get("Cash")).get("Amount")).toString()) );
		            }
				 
		            if (jsonMainObj.get("StoreCredit") != null)
		            {
		            	JSONObject tempStoreCreditObj = null;
		            	try {
		            		tempStoreCreditObj = (JSONObject)jsonMainObj.get("StoreCredit");
		            	} catch(ClassCastException e) {
		            		tempStoreCreditObj = (JSONObject)((JSONArray)jsonMainObj.get("StoreCredit")).get(0);
		            	}	
		                paymentParts += GetMergedPaymentPart(loopBlockTwo,"","SC",
		                		Double.parseDouble(tempStoreCreditObj.get("Amount").toString()) );
		            }
		            
		            
		            if (jsonMainObj.get("CreditCard") != null)
		            {
		            	JSONArray creditCardArr = (JSONArray)jsonMainObj.get("CreditCard");
		            	
		            	for (Object object : creditCardArr) {
							
		            		JSONObject tempObj = (JSONObject)object;
		            		paymentParts += GetMergedPaymentPart(loopBlockTwo,
		            				tempObj.get("Number").toString(),
		                    		tempObj.get("Type").toString(),
		                    		Double.parseDouble(tempObj.get("Amount").toString()) );
						}
		            }	
		                
		            
		            if (jsonMainObj.get("DebitCard") != null)
		            {
		            	
		            	
		            	JSONObject tempDebitCreditObj = null;
		            	try {
		            		tempDebitCreditObj = (JSONObject)jsonMainObj.get("DebitCard");
		            	} catch(ClassCastException e) {
		            		tempDebitCreditObj = (JSONObject)((JSONArray)jsonMainObj.get("DebitCard")).get(0);
		            	}
		            	
		                paymentParts += GetMergedPaymentPart(loopBlockTwo,
		                		tempDebitCreditObj.get("Number").toString(),
		                		"DEBIT",
		                		Double.parseDouble(tempDebitCreditObj.get("Amount").toString())  );
		            }
		            
		            if (jsonMainObj.get("Gift") != null)
		            {
		            	
		            	JSONObject tempGiftObj = null;
		            	try {
		            		tempGiftObj = (JSONObject)jsonMainObj.get("GiftCertificate");
		            	} catch(ClassCastException e) {
		            		tempGiftObj = (JSONObject)((JSONArray)jsonMainObj.get("GiftCertificate")).get(0);
		            	}
		            	
		                paymentParts += GetMergedPaymentPart(loopBlockTwo,
		                    "",
		                    "GiftCertificate",
		                    Double.parseDouble(tempGiftObj.get("Amount").toString()) );
		            }
		            
		            if (jsonMainObj.get("GiftCard") != null)
		            {
		            	JSONObject tempGiftCardObj = null;
		            	try {
		            		tempGiftCardObj = (JSONObject)jsonMainObj.get("GiftCard");
		            	} catch(ClassCastException e) {
		            		tempGiftCardObj = (JSONObject)((JSONArray)jsonMainObj.get("GiftCard")).get(0);
		            	}
		            	
		                paymentParts += GetMergedPaymentPart(loopBlockTwo,"","GC",
		                		Double.parseDouble(tempGiftCardObj.get("Amount").toString()) );
		                
		            }
		            
		            if (jsonMainObj.get("Charge") != null)
		            {
		            }
		            
		            if (jsonMainObj.get("COD") != null)
		            {
		            }
		            
		            if (jsonMainObj.get("Deposit") != null)
		            {
		            }
		            
		            if (jsonMainObj.get("Check") != null)
		            {
		            	JSONObject checkObj = (JSONObject)jsonMainObj.get("Check");
		            
		                paymentParts += GetMergedPaymentPart(loopBlockTwo,
		                    "","Check" + "-" + checkObj.get("Number"),
		                    Double.parseDouble(checkObj.get("Amount").toString()) );
		            }
		            
		            if (jsonMainObj.get("FC") != null)
		            {
		            }
					
		            if(logger.isDebugEnabled()) logger.debug("** After process paymentParts val :  " + paymentParts);
		           
		            
					if(paymentParts != null) {
						
						templateContentStr = templateContentStr.replace(loopBlockTwo, paymentParts);
					}
			}
			
			
			
			// Remove ##START## ##END## PH from the template
			templateContentStr = templateContentStr.replace(paymentSetPlaceHoldersArr[0], "").replace(paymentSetPlaceHoldersArr[4],"");
			templateContentStr = templateContentStr.replace(ItemsSetPlaceHoldersArr[0], "").replace(ItemsSetPlaceHoldersArr[5],"");
			
			if(logger.isDebugEnabled()) logger.debug("After processing LOOP Contents : "+ templateContentStr);
			
			// Replace All individual place holders place Holders
		  	Set<String> set = userJSONSettingHM.keySet();
	    	String jsonKey;
	    	String[] jsonPathArr;
	    	String jsonKeyValue = "";
	    	
			for (String phStr : set) {
				
				jsonKeyValue = "";
	    		JSONObject parentObject = null;
	    		JSONArray parentArrayObject = null;
			
		    		if(templateContentStr.indexOf(phStr) != -1) {			// PH exists in template
		    			
		    			jsonKey = userJSONSettingHM.get(phStr);
		    			if(logger.isDebugEnabled()) logger.debug("*******<< jsonKey : "+ jsonKey);
		    			
		    			if(jsonKey.indexOf("_$$_") != -1) {
		    				
		    				if(logger.isDebugEnabled()) logger.debug("contains dollar .....");
		    				
		    				String[] jsonKeytokens = jsonKey.split(Pattern.quote("_$$_"));

		    				for(int j=0;j<jsonKeytokens.length;j++) {
		    					
					               // . path exists in json key 
			    				
			    				jsonPathArr = jsonKeytokens[j].split(Pattern.quote("."));
			    				//if(logger.isDebugEnabled()) logger.debug("*******<< jsonKey : "+ jsonPathArr[0] + " ::: " + jsonPathArr[1]);
			    				
			    				try {
			    					
			    					if(logger.isDebugEnabled()) logger.debug("SINGLE OBJ CREATED...");
			    					parentObject = (JSONObject)jsonMainObj.get(jsonPathArr[0]);
			    				} catch(ClassCastException e) {
			    					
			    					if(logger.isDebugEnabled()) logger.debug("ARRAY OBJ CREATED...");
			    					parentArrayObject = (JSONArray)jsonMainObj.get(jsonPathArr[0]);
			    					if(logger.isDebugEnabled()) logger.debug("--Array---"+ parentArrayObject);
			    					parentObject  = (JSONObject)parentArrayObject.get(0);
			    					if(logger.isDebugEnabled()) logger.debug("--First element in array---"+ parentObject);
			    					//if(logger.isDebugEnabled()) logger.debug("--Second element in array---"+ (JSONObject)parentArrayObject.get(1));
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
			    					
			    					if(logger.isDebugEnabled()) logger.debug("** Parent JSON is null *****");
			    				} 
			    			
		    				}
		    				
		    				if(logger.isDebugEnabled()) logger.debug("*** Replacing " + phStr + " with " + jsonKeyValue);
	    					templateContentStr = templateContentStr.replaceAll(phStr, jsonKeyValue);
		    				
		    			} else if(jsonKey.contains(".")) {			               // . path exists in json key 
		    				
		    				jsonPathArr = jsonKey.split(Pattern.quote("."));
		    				//if(logger.isDebugEnabled()) logger.debug("*******<< jsonKey : "+ jsonPathArr[0] + " ::: " + jsonPathArr[1]);
		    				
		    				try {
		    					
		    					if(logger.isDebugEnabled()) logger.debug("SINGLE OBJ CREATED...");
		    					parentObject = (JSONObject)jsonMainObj.get(jsonPathArr[0]);
		    				} catch(ClassCastException e) {
		    					
		    					if(logger.isDebugEnabled()) logger.debug("ARRAY OBJ CREATED...");
		    					parentArrayObject = (JSONArray)jsonMainObj.get(jsonPathArr[0]);
		    					if(logger.isDebugEnabled()) logger.debug("--Array---"+ parentArrayObject);
		    					parentObject  = (JSONObject)parentArrayObject.get(0);
		    					if(logger.isDebugEnabled()) logger.debug("--First element in array---"+ parentObject);
		    					//if(logger.isDebugEnabled()) logger.debug("--Second element in array---"+ (JSONObject)parentArrayObject.get(1));
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
		    					
		    					if(logger.isDebugEnabled()) logger.debug("*** Replacing " + phStr + " with " + jsonKeyValue);
		    					templateContentStr = templateContentStr.replaceAll(phStr, jsonKeyValue);
		    				} else {
		    					
		    					if(logger.isDebugEnabled()) logger.debug("** Parent JSON is null *****");
		    				} 
		    				
		    				
		    			}  
		    		}
		    } // for 
	    	 
			
			if(logger.isDebugEnabled()) logger.debug("After processing INDIVIDUAL Contents : "+ templateContentStr);
			
			// remove all no-value place holders form template
			String digiRcptDefPHStr = PropertyUtil.getPropertyValue("DRPlaceHolders");
			if(digiRcptDefPHStr != null) {
				String[] defPHArr = digiRcptDefPHStr.split(Pattern.quote(","));
				for(int i=0;i<defPHArr.length;i++) {
					templateContentStr = templateContentStr.replaceAll(defPHArr[i],"");
				}
				
				if(logger.isDebugEnabled()) logger.debug("After Removing no-value Place holder(#PH#) values.. Content is :" + templateContentStr );
			}
			
			// Add domain name to images urls 	
			String appUrl = PropertyUtil.getPropertyValue("subscriberIp");			
			templateContentStr = templateContentStr.replaceAll("/subscriber/SystemData/digital-templates/",  appUrl +"/subscriber/SystemData/digital-templates/" );
			
			// Get email from json request..
			JSONObject receiptObj = (JSONObject)jsonMainObj.get("Receipt");
			String emailId = (String)receiptObj.get("BillToEMail");
			
			if(emailId == null) {
				jsonMessage = "Bill-To-EmailId is null in json request";
				if(logger.isDebugEnabled()) logger.debug("**** Bill-To-EmailId is null is json request returning ...");
				return null;
			}
			
			templateContentStr = templateContentStr.replaceAll(Pattern.quote("|^"), "[").replaceAll(Pattern.quote("^|"), "]");
			if(logger.isDebugEnabled()) logger.debug("---->>>>>>>"+ templateContentStr);
			// Get Contact from email ID and replace html Place holders .
			String tempStr2 = replaceContactsPHValues(templateContentStr,emailId, user);
			
			if(tempStr2 != null) {
				
				templateContentStr =  tempStr2;
			} 
			
			
			if(logger.isDebugEnabled()) logger.debug("1001 : after process : "+ templateContentStr);
			// remove empty PH 
			if(logger.isDebugEnabled()) logger.debug(">>>>>>>>>>>>>> phStr ==="+ phStr);
			if(phStr.trim().length() > 0) {
				String[] temp = phStr.split(",");
				String Str = "";
				for (String strings : temp) {
					
					Str = "[GEN_"+strings + "]";
					//if(logger.isDebugEnabled()) logger.debug("-->>"+templateContentStr.indexOf(Str));
					templateContentStr = templateContentStr.replace(Str, "");
					if(logger.isDebugEnabled()) logger.debug("Replacing " + Str + " with " + "''");
				}
				if(logger.isDebugEnabled()) logger.debug("1004 : after process : "+ templateContentStr);
			}
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
			
			
			// Add to Email Queue
			String subject = "Your Purchase Receipt";
	    	EmailQueue emailQueue = new EmailQueue(subject, templateContentStr,"DigitalReciept","Active", emailId,new Date(),user);
	    	//emailQueueDao.saveOrUpdate(emailQueue);
	    	emailQueueDaoForDML.saveOrUpdate(emailQueue);
	    	
	    	statusFlag = 1;
	    	jsonMessage = "Digital receipt submitted successfully . Mail would be sent in a moment .";
	    	
	    	
	    	
	    	//Utility.pingInstantMailSender();
	    	if(!emailQueueScheduler.isRunning()) {
	    		emailQueueScheduler.run();
	    	}
	    	
	    	if(logger.isDebugEnabled()) logger.debug("*************** EXITING***********************");
	    		    	
	    	return null;
	    } catch(Exception e) {
	    	logger.error("Exception ::::" , e);
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
	    		
	    		if(logger.isDebugEnabled()) logger.debug(">>>"+ ele  + "  :  " +request.getParameter(ele) );
	    	} // while
	    	
	    	JSONObject status = new JSONObject();
	    	if(returnParams.size() > 0) {
	    		status.put("RETURNPARAMETERS", returnParams);
	    	}
	    
	    	replyObject.put("STATUS", statusFlag == 1 ? "Success" : "Failure");
	    	replyObject.put("MESSAGE", jsonMessage);
	    	replyObject.put("ERRORCODE", 0);
	    	
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
			
			if(logger.isDebugEnabled()) logger.debug("phStr >>>>>>>>>>>>>>>>> "+ phStr);
			
			if(phStr.trim().length() < 1) {
				
				if(logger.isDebugEnabled()) logger.debug(" *** No place holders exist ... returning ..");
				return null;
			}
			
			MailingList mailingList = mailingListDao.findListTypeMailingList(Constants.MAILINGLIST_TYPE_POS,user.getUserId());
			
			if(mailingList == null) {
				
				if(logger.isDebugEnabled()) logger.debug("No POS list exist for the user .. returning ");
				return null;
			}
			
			Long contactId = contactsDao.getContactIdByEmailIdAndMlist(mailingList, emailId);
			
			if(contactId == null) {
				
				if(logger.isDebugEnabled()) logger.debug("Contact Id not found ... returning");
				return null;
			}
			
			Contacts contact = contactsDao.findById(contactId);
			
			if(contact == null) {
				if(logger.isDebugEnabled()) logger.debug("No Contact Object found .. returning.");
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
			    if(logger.isDebugEnabled()) logger.debug("--1---");
				tempStr = null;
				tempInt = 0; tempLong = null;
				tempCal = null;
				
				if(contField.equals("email")) {
					
					if(logger.isDebugEnabled()) logger.debug("--2---");
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
				//if(logger.isDebugEnabled()) logger.debug("templates before :>>> "+ templateContentStr);
				String tempStrVal = "[GEN_"+ contField+"]";
				
				if(tempStr != null) {
					if(logger.isDebugEnabled()) logger.debug("--3---");
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
			if(logger.isDebugEnabled()) logger.debug("templates after :>>> "+ templateContentStr);
			
			return templateContentStr;
			
			
		} catch(Exception e) {
			logger.error("Exception ::::" , e);
			return null;
		}
	}
	
	private String getPlaceHolders(String content) {
		try {
			if(logger.isDebugEnabled()) logger.debug("+++++++ Just Entered +++++"+ content);
			String cfpattern =  "\\[(GEN_[a-z0-9]*?)]"; //[([GEN_][a-z0-9A-Z]*?)\\]
			Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
			Matcher m = r.matcher(content);

			String ph = null;
			//totalPhSet = new HashSet<String>();
			String cfNameListStr = "";

			try {
				while(m.find()) {

					ph = m.group(1); //.toUpperCase()
					if(logger.isInfoEnabled()) logger.info("Ph holder :" + ph);

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
				
				//if(logger.isDebugEnabled()) logger.debug("+++ Exiting : "+ totalPhSet);
			} catch (Exception e) {
				if(logger.isErrorEnabled()) logger.error("Exception while getting the place holders ", e);
			}

			if(logger.isInfoEnabled()) logger.info("CF PH cfNameListStr :" + cfNameListStr);

			return cfNameListStr;
		} catch (Exception e) {
			return "";
		}
	}
	
	private String GetMergedPaymentPart(String paymentPart, String number, String type, Double Amount )
    {
        String paymentPartStr = paymentPart.replace("#Payment.Amount#", "<strong>" +  Amount + "</strong>")
                .replace("#Payment.Number#", number)
                .replace("#Payment.Type#", type);
        return paymentPartStr;
    }
	
	
	private String replaceLoopBlock(String loopBlock, Map<String,String> userJSONSettingHM,JSONObject jsonMainObj) {
		try {
			String finalHtmlBlockVal = "";
			Matcher matcher = Pattern.compile("(#\\w+).(\\w+#)").matcher(loopBlock);
			
			//JSONObject jsonObj = null;

			JSONArray jsonArr = null;
			
			if(matcher.find()) {
				
				if(logger.isDebugEnabled()) logger.debug("--PLACE HOLDERS FOUND --"+ matcher.group(0));
				//if(logger.isDebugEnabled()) logger.debug("--1--"+ userJSONSettingHM.get(matcher.group(0)));
				
				if(userJSONSettingHM.get(matcher.group(0)) == null) {
					
					if(logger.isDebugEnabled()) logger.debug("*** Place holder value is not found in HashMap ***");
					return null;
				}
				
				String[] arrayElement = userJSONSettingHM.get(matcher.group(0)).split(Pattern.quote("."));
				
				// Items from Items.billTomail
				if(logger.isDebugEnabled()) logger.debug("arrayElement[0]" + arrayElement[0]);
				jsonArr  = (JSONArray)jsonMainObj.get(arrayElement[0]);
				
			} else {
				
				if(logger.isDebugEnabled()) logger.debug("Pattern not found");
				return null;
			}
			
			matcher.reset();
			
			if(jsonArr == null) {
				return null;
			}
			
				if(logger.isDebugEnabled()) logger.debug("Array size :" + jsonArr.size());
			
			for(int i = 0; i < jsonArr.size(); i++)   // Loop all array elements
			{	
				
				JSONObject jsonObj = (JSONObject)jsonArr.get(i);
				String tempStr = loopBlock;
				//if(logger.isDebugEnabled()) logger.debug(" Current jsonobj object from the jsonarray is :"+ jsonObj);
			
				while (matcher.find()) {			// Loop all fields
					String val = "";
					
					String placeHolderStr = matcher.group(0);
					//if(logger.isDebugEnabled()) logger.debug(" Found place holders : " +placeHolderStr);
					String jsonKey = userJSONSettingHM.get(placeHolderStr);
					String[] jsonValArr = null;
					if(jsonKey != null) { 
						
					//if(logger.isDebugEnabled()) logger.debug("*** JSON key path  is : " + jsonKey);
					// if contains _$$_ multi valued keys .
					if(jsonKey.indexOf("_$$_") != -1) {
						
						//if(logger.isDebugEnabled()) logger.debug("******************** Multi values in json Key exist ...");
						//if(logger.isDebugEnabled()) logger.debug("*** json Val is : "+ jsonKey);
						
						String[] jsonKeytokens = jsonKey.split(Pattern.quote("_$$_"));
						
						for(int j=0;j<jsonKeytokens.length;j++) {
							
							//if(logger.isDebugEnabled()) logger.debug("Individual key is  : "+ jsonKeytokens[j]);
							jsonValArr = jsonKeytokens[j].split(Pattern.quote("."));
							//if(logger.isDebugEnabled()) logger.debug("key is "+ jsonValArr[1]);
							
							if(jsonObj.get(jsonValArr[1]) == null) {
								
								if(logger.isDebugEnabled()) logger.debug("**** Excepted value not found in the json object ***");
								continue;
							}
							
							if(logger.isDebugEnabled()) logger.debug("***^^ replace template with object value  : "+ jsonObj.get(jsonValArr[1])+ " key is "+ jsonValArr[1]);
							
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
						//if(logger.isDebugEnabled()) logger.debug("Value(1) is : " + val);
						tempStr = tempStr.replace(placeHolderStr,val);
					} else {	
					
						//if(logger.isDebugEnabled()) logger.debug("<<<< json Val is : "+ jsonKey);
						jsonValArr = jsonKey.split(Pattern.quote("."));
						if(logger.isDebugEnabled()) logger.debug("***$$ replace template with object value  : "+ jsonObj.get(jsonValArr[1])+" key is "+ jsonKey);
						
						if(jsonObj.get(jsonValArr[1]) == null) {
							if(logger.isDebugEnabled()) logger.debug("**** Excepted value not found in the json object ***");
							continue;
						}
						
						try {
							val = (String)jsonObj.get(jsonValArr[1]);
						} catch(ClassCastException e) {
							try  {
								val = ((Long)jsonObj.get(jsonValArr[1])).toString();
							} catch(ClassCastException f) {
								val = ((Double)jsonObj.get(jsonValArr[1])).toString();
							}
						}
						
						//if(logger.isDebugEnabled()) logger.debug("Value(2) is : " + val);
						tempStr = tempStr.replace(matcher.group(0),val);
					}  
				  } // if
					
					
				}
				
				matcher.reset();
				
				finalHtmlBlockVal = finalHtmlBlockVal + tempStr; 
				
			}
			
			//if(logger.isDebugEnabled()) logger.debug("final block to replace is "+ finalHtmlBlockVal);
			return finalHtmlBlockVal;
		} catch(Exception e) {
			
			logger.error("Exception ::::" , e);
			return null;
		} 
	} 

}
