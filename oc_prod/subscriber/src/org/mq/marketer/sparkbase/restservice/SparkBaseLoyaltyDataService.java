package org.mq.marketer.sparkbase.restservice;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SparkBaseLocationDetails;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.contacts.CustomFieldValidator;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.SparkBaseCardDao;
import org.mq.marketer.campaign.dao.SparkBaseLocationDetailsDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class SparkBaseLoyaltyDataService extends AbstractController{

	
	private SparkBaseLocationDetailsDao sparkBaseLocationDetailsDao;
	private MailingListDao mailingListDao;
	private ContactsDao contactsDao;
	private UsersDao usersDao;
	private ContactsLoyaltyDao contactsLoyaltyDao;
	private SparkBaseCardDao sparkBaseCardDao;
	private EmailQueueDao emailQueueDao;
	
	 
	
	private String msg = null;
	private int errorCode = -1;
	
	
	
	
	public SparkBaseLoyaltyDataService() {
		// TODO Auto-generated constructor stub
	}
	
	public SparkBaseLocationDetailsDao getSparkBaseLocationDetailsDao() {
		return sparkBaseLocationDetailsDao;
	}

	public void setSparkBaseLocationDetailsDao(
			SparkBaseLocationDetailsDao sparkBaseLocationDetailsDao) {
		this.sparkBaseLocationDetailsDao = sparkBaseLocationDetailsDao;
	}

	public MailingListDao getMailingListDao() {
		return mailingListDao;
	}

	public void setMailingListDao(MailingListDao mailingListDao) {
		this.mailingListDao = mailingListDao;
	}

	public ContactsDao getContactsDao() {
		return contactsDao;
	}

	public void setContactsDao(ContactsDao contactsDao) {
		this.contactsDao = contactsDao;
	}

	public UsersDao getUsersDao() {
		return usersDao;
	}

	public void setUsersDao(UsersDao usersDao) {
		this.usersDao = usersDao;
	}

	public ContactsLoyaltyDao getContactsLoyaltyDao() {
		return contactsLoyaltyDao;
	}

	public void setContactsLoyaltyDao(ContactsLoyaltyDao contactsLoyaltyDao) {
		this.contactsLoyaltyDao = contactsLoyaltyDao;
	}

	public SparkBaseCardDao getSparkBaseCardDao() {
		return sparkBaseCardDao;
	}

	public void setSparkBaseCardDao(SparkBaseCardDao sparkBaseCardDao) {
		this.sparkBaseCardDao = sparkBaseCardDao;
	}

	public EmailQueueDao getEmailQueueDao() {
		return emailQueueDao;
	}

	public void setEmailQueueDao(EmailQueueDao emailQueueDao) {
		this.emailQueueDao = emailQueueDao;
	}

	
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		//Contacts contact = null; 
		JSONObject jsonLoc = null;
		JSONObject jsonHeader = null;
		//JSONObject jsonReqId = null;
		String requestId = null;
		String jsonLocId = null;
		JSONObject jsonLoyaltydetails = null;
		JSONObject jsonResponseObject = new JSONObject();
		JSONArray  jsonCustmorInfoArr = new JSONArray();
		String userName = null;
		String userOrg = null;
		String userToken = null;
		//String passwrd = null;
		boolean getOnlyCount = false;
		long count = 0;
		String startDate = null;
		String endDate = null;
		String loyaltyEnrolledSource  = null;
		
		try {
			
			// Print all parameters
			Enumeration<String> enumerator = request.getParameterNames();
			logger.info("Printing all req parametes :");
			
			while(enumerator.hasMoreElements()) {
				
				String reqParaName = enumerator.nextElement();
				logger.info(" QUERY PARAMETERS  >>> : ");
				logger.debug("parameters  : " + reqParaName);
				logger.info(" Value : " +request.getParameter(reqParaName));
			}
			
			// Check if jsonVal parameter is there
			JSONObject jsonRootObject = null;
			
			if(request.getParameter("jsonVal") != null && request.getParameter("jsonVal").length() > 0) {
				
				String jsonValStr = request.getParameter("jsonVal");
				jsonRootObject = (JSONObject)JSONValue.parse(jsonValStr);
				if(jsonRootObject == null) {
					
					logger.info("Error : Invalid json Object .. Returning. ****");
					msg = "Error : Invalid json Object .. Returning. ****";
					errorCode = 1201;
					return null;
				}
			} else {
			
			// stream Test
			  	InputStream is = request.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				char[] chr = new char[1024];
				int bytesRead = 0;
				StringBuilder sb = new StringBuilder();
				
				while ((bytesRead = br.read(chr)) > 0) {
			         sb.append(chr, 0, bytesRead);
			    }
				logger.info("REst body value is "+ sb.toString());
				try {
					
					jsonRootObject = (JSONObject)JSONValue.parse(sb.toString());
				} catch(Exception e) {
					
					logger.info("Error : Invalid json Object .. Returning. ****");
					msg = "Error : Invalid json Object .. Returning. ****";
					errorCode = 1001;
					return null;
				}		
			}
			
			if(jsonRootObject == null) {
				
				logger.info("Error : Unable to parse the json.. Returning. ****");
				msg = "Error : Unable to parse the json.. Returning. ****";
				errorCode = 1002;
				return null;
			}
			
			
			JSONObject jsonMainObject =  (JSONObject)jsonRootObject.get("LOYALTYDATAREQ");
			
			if(jsonMainObject == null) {
				
				logger.info("Error : unable to find the Loyalty data Req Location in JSON ****");
				msg = "Error : Unable to find the LoyaltydataReq Location in JSON ";
				errorCode = 1003;
				return null;
			}
			
			jsonHeader = (JSONObject)jsonMainObject.get("HEADERINFO");
			
			if(jsonHeader == null) {
				
				logger.info("Error : unable to find the HearderInfo in JSON ****");
				msg = "Error : unable to find the Header info in JSON";
				errorCode = 1004;
				return null;
			}
			
			requestId = jsonHeader.get("REQUESTID").toString();
			
			if(requestId == null || requestId.trim().length() == 0) {
				
				logger.info("Error : Request ID of HearderInfo is empty in JSON ****");
				msg = "Error : Request ID of Header info in JSON should not be empty.";
				errorCode = 1004;
				return null;
				
				
			}
			
			jsonLoc = (JSONObject)jsonMainObject.get("STORELOCATIONINFO");
			
			logger.info("System Loc is "+ jsonLoc);
			
			if(jsonLoc == null) {
				
				logger.info("Error : unable to find the StoreLocationInfo in JSON ****");
				msg = "Error : unable to find the Store Location info in JSON.";
				errorCode = 1004;
				return null;
			}
			//user may request data for store wise or all stores
			
			jsonLocId = jsonLoc.get("STORELOCATIONID").toString();
			
			
			logger.info("System Loc is "+ jsonLocId);
			
			if(jsonLocId == null  || jsonLocId.trim().equals("")) {
				
				logger.info("Error : find the  spark based StoreLocationID as empty in JSON ****");
				msg = "Error : Store Location ID in JSON should not be empty.";
				errorCode = 1004;
				return null;
			}
			
			
			JSONObject userJSONObj = (JSONObject)jsonMainObject.get("USERDETAILS");
			
			logger.info("User Details is "+ userJSONObj);
			
			if(userJSONObj == null) {
				
				logger.info("Error : unable to find the User Details in JSON ****");
				msg = "Error : unable to find the User Details in JSON";
				errorCode = 1011;
				return null;
			}
			
			userToken = userJSONObj.get("TOKEN") == null ? null : userJSONObj.get("TOKEN").toString();
			
			
			if(userToken == null) {
				
				logger.info("Error : User Token cannot be empty.");
				msg = "Error :  User Token cannot be empty.";
				errorCode = 1012;
				return null;
			}
			
			
			userName = userJSONObj.get("USERNAME").toString();
			userOrg = userJSONObj.get("ORGID").toString();
			
			//passwrd = userJSONObj.get("PASSWORD").toString();
			
			if(userName == null || userOrg == null) {
				
				logger.info("Error : Username or organisation cannot be empty.");
				msg = "Error :  Username or organisation cannot be empty.";
				errorCode = 1012;
				return null;
			}
			
			Users user = usersDao.findByToken(userName+ Constants.USER_AND_ORG_SEPARATOR +userOrg, userToken );
			if(user == null) {
				
				logger.info("Unable to find the user Obj");
				msg = "Error : Unable to find the User Details";
				errorCode = 1005;
				return null;
			}
			SparkBaseLocationDetails sparkBaseLoc = null;
			//SparkBaseLocationDetails sparkBaseLoc = sparkBaseLocationDetailsDao.findSparkBaseLocByUserNameAndPass(userName, pass, jsonLocId.toString());
			sparkBaseLoc = sparkBaseLocationDetailsDao.findBylocationId(user.getUserOrganization().getUserOrgId(), jsonLocId);
			
			if(sparkBaseLoc == null) {
				
				logger.info("Error : No SparkBaseDetails Found with the given credentials. ****");
				msg = "Error : No SparkBase Details Found with the given credentials.";
				errorCode = 1006;
				return null;
			}
			
			MailingList mlList = mailingListDao.findPOSMailingList(user);
			if(mlList == null) {
				
				logger.info("Unable to find the user POS ml List");
				msg = "Error : Unable to find the user POS  List.";
				errorCode = 1007;
				return null;
			}
			
			jsonLoyaltydetails = (JSONObject)jsonMainObject.get("LOYALTYINFO");
			
			if(jsonLoyaltydetails == null) {
				
				logger.info("Error : unable to find the  required Loyalty details in JSON ****");
				msg = "Error : unable to find the Loyalty Details in JSON";
				errorCode = 1004;
				return null;
			}
			
			String getOnlyCountStr = jsonLoyaltydetails.get("GETONLYCOUNT").toString().trim();
			getOnlyCount = (getOnlyCountStr == null || getOnlyCountStr.equals("") ||
									getOnlyCountStr.toLowerCase().equals("n") || getOnlyCountStr.toLowerCase().equals("no")  ) ? false : true;
			
			
			loyaltyEnrolledSource = jsonLoyaltydetails.get("LOYALTYTYPE").toString().trim();
			loyaltyEnrolledSource = loyaltyEnrolledSource == null ? "All" : loyaltyEnrolledSource;//need to confirm about default val here 
			
			
			startDate = jsonLoyaltydetails.get("STARTDATE").toString().trim();
			endDate = jsonLoyaltydetails.get("ENDDATE").toString().trim();
			
			if(!CustomFieldValidator.validateDate(startDate, "date", MyCalendar.FORMAT_DATETIME_STYEAR)) {
				
				
				logger.info("Error : unable to fetch the requested data,got wrong start date in JSON ****");
				msg = "Error : start date provided wrongly in JSON";
				errorCode = 1004;
				return null;
				
				
				
			}//if start date is not correct
			
			if(!CustomFieldValidator.validateDate(endDate, "Date", MyCalendar.FORMAT_DATETIME_STYEAR)) {
				
				logger.info("Error : unable to fetch the requested data,got wrong end date in JSON ****");
				msg = "Error : end date provided wrongly in JSON";
				errorCode = 1004;
				return null;
				
				
				
			}//if end date is not correct
			
			
			count = contactsLoyaltyDao.findLoyaltyCountByDates(user.getUserId(), startDate, endDate, sparkBaseLoc.getLocationId(), loyaltyEnrolledSource);
			if(!getOnlyCount) {
				
				//need to fetch all the customers info for 
			String subQry ="";
			if(!loyaltyEnrolledSource.toLowerCase().equals("all")) {
				
				subQry = " AND contactLoyaltyType='"+loyaltyEnrolledSource+"'";
			}
			
			 String qry = " FROM ContactsLoyalty WHERE userId= "+user.getUserId()+" AND created_date BETWEEN '"
					+startDate+"' AND '"+endDate+"' AND  locationId='"+sparkBaseLoc.getLocationId()+"'"+subQry;

			 logger.info("qry ::"+qry);
			 
			List<ContactsLoyalty> loyaltyContactsList = null;
			int strtIndex = 0;
			int size = 100;
			JSONObject customerInfo = null;
			JSONObject customer = null;
			Contacts contact = null;
			
			do {
				
				loyaltyContactsList = contactsLoyaltyDao.findLoyaltyContactsByDates(qry, strtIndex, size );
				logger.info("got size::"+loyaltyContactsList.size());
				
				for (ContactsLoyalty contactsLoyalty : loyaltyContactsList) {
					
					contact = contactsLoyalty.getContact();
					
					
						
						// Return Created User Object.
						customerInfo = new JSONObject();
						customer = new JSONObject();
						
						customerInfo.put("CUSTOMERID", contactsLoyalty.getCustomerId() == null ? "" : contactsLoyalty.getCustomerId());
						if(contact != null) {
							customerInfo.put("CUSTOMERTYPE", "");
							customerInfo.put("FIRSTNAME", contact.getFirstName() != null ? contact.getFirstName() : ""); 
							customerInfo.put("MIDDLENAME","");
							customerInfo.put("LASTNAME",contact.getLastName() != null ? contact.getLastName() : ""); 
							customerInfo.put("ADDRESS1",contact.getAddressOne() != null ? contact.getAddressOne() : ""); 
							customerInfo.put("ADDRESS2",contact.getAddressTwo() != null ? contact.getAddressTwo() : ""); 
							customerInfo.put("CITY",contact.getCity() != null ? contact.getState() : ""); 
							customerInfo.put("STATE",contact.getState() != null ? contact.getState() : ""); 
//							customerInfo.put("POSTAL",contact.getPin() != 0 ? contact.getPin() : ""); 
							customerInfo.put("POSTAL",contact.getZip() != null ? contact.getZip() : ""); 
							customerInfo.put("COUNTRY",contact.getCountry() != null ? contact.getCountry() : ""); 
							customerInfo.put("MAILPREF",""); 
//							customerInfo.put("PHONE",contact.getPhone() != null ? contact.getPhone() : ""); 
							customerInfo.put("PHONE",contact.getMobilePhone() != null ? contact.getMobilePhone() : ""); 
							customerInfo.put("ISMOBILE",""); 
							customerInfo.put("PHONEPREF",""); 
							customerInfo.put("EMAIL",contact.getEmailId() != null ? contact.getEmailId() : ""); 
							customerInfo.put("EMAILPREF",""); 
							customerInfo.put("BIRTHDAY",contact.getBirthDay() != null ? MyCalendar.calendarToString(contact.getBirthDay(), MyCalendar.FORMAT_DATETIME_STYEAR) : ""); 
							customerInfo.put("ANNIVERSARY",contact.getAnniversary() != null ? MyCalendar.calendarToString(contact.getAnniversary(), MyCalendar.FORMAT_DATETIME_STYEAR) : ""); 
							customerInfo.put("GENDER",contact.getGender() != null ? contact.getGender() : "");
							
						}
						customerInfo.put("CARDNUMBER", contactsLoyalty.getCardNumber());
						customerInfo.put("CARDTYPE", contactsLoyalty.getCardType() == null ? "" : contactsLoyalty.getCardType());
						customerInfo.put("CARDPIN", contactsLoyalty.getCardPin() != null ? contactsLoyalty.getCardPin() : "");
						customerInfo.put("TOTALLOYALTYEARNED", contactsLoyalty.getTotalLoyaltyEarned() == null ? "" : contactsLoyalty.getTotalLoyaltyEarned());
						customerInfo.put("TOTALLOYALTYREDEMPTION", contactsLoyalty.getTotalLoyaltyRedemption() == null ? "" : contactsLoyalty.getTotalLoyaltyRedemption());
						customerInfo.put("TOTALGIFTCARDAMOUNT", contactsLoyalty.getTotalGiftcardAmount() == null ? "" : contactsLoyalty.getTotalGiftcardAmount());
						customerInfo.put("TOTALGIFTCARDREDEMPTION", contactsLoyalty.getTotalGiftcardRedemption() == null ? "" : contactsLoyalty.getTotalGiftcardRedemption());
						customerInfo.put("GIFTCARDBALANCE", contactsLoyalty.getGiftcardBalance() == null ? "" : contactsLoyalty.getGiftcardBalance());
						customerInfo.put("LOYALTYENROLLEDDATE", contactsLoyalty.getCreatedDate() == null ? "" : MyCalendar.calendarToString(contactsLoyalty.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR));
						customer.put("CUSTOMER", customerInfo);
						jsonCustmorInfoArr.add(customer); 
						
					
					
					
					
				}//for
				
				strtIndex += size;
				loyaltyContactsList.clear();
				
			}while(loyaltyContactsList.size() == size);
			
			}
			errorCode = 0;
			msg = " Number of Customers fetched :: "+count;
			
		} catch(Exception e) {
			
			logger.error("Exception ::::", e);
			msg = "Error : Server error occured.";
			errorCode = 1000;
			
		} finally {
			
			
			JSONObject headerDetails = new JSONObject();
			headerDetails.put("REQUESTID", requestId);
			jsonResponseObject.put("HEADERINFO", headerDetails);
			
			
			JSONObject locationDetails = new JSONObject();
			locationDetails.put("STORELOCATIONID", jsonLocId == null ? "" : jsonLoc.get("STORELOCATIONID").toString());
			
			jsonResponseObject.put("STORELOCATIONINFO", locationDetails);
			
			JSONObject replyObject = new JSONObject();
			replyObject.put("STATUS", errorCode == 0 ? "Success" : "Failure");
			replyObject.put("MESSAGE", msg);
			replyObject.put("ERRORCODE", errorCode);
			
			jsonResponseObject.put("STATUS",replyObject);
			
			JSONObject loyaltyJsonObj = new JSONObject();
			loyaltyJsonObj.put("LOYALTYTYPE", loyaltyEnrolledSource != null ? loyaltyEnrolledSource : "" );
			loyaltyJsonObj.put("TOTALCOUNT", count);
			loyaltyJsonObj.put("STARTDATE", startDate != null ? startDate : "" );
			loyaltyJsonObj.put("ENDDATE", endDate != null ? endDate : "" );
			jsonResponseObject.put("LOYALTYBASICINFO", loyaltyJsonObj);
			
			if(!getOnlyCount) {
				jsonResponseObject.put("CUSTOMERINFO",jsonCustmorInfoArr);
			}
			
			JSONObject rootObject = new JSONObject();
			rootObject.put("LOYALTYDATARESPONSE", jsonResponseObject);
			
			
			logger.info("Response json object : "+ rootObject);
			pw.write(rootObject.toJSONString());
			
			pw.flush();
			pw.close();
			
		}
		
		return null;
	}
	
	
	
}
