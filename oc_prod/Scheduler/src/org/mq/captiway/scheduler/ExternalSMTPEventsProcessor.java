package org.mq.captiway.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mq.captiway.scheduler.beans.CampaignReportEvents;
import org.mq.captiway.scheduler.beans.ExternalSMTPEvents;
import org.mq.captiway.scheduler.beans.SuppressedContacts;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.campaign.CampaignReportEventsQueue;
import org.mq.captiway.scheduler.campaign.ExternalSMTPEventsQueue;
import org.mq.captiway.scheduler.dao.CampaignReportEventsDao;
import org.mq.captiway.scheduler.dao.CampaignReportEventsDaoForDML;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.DRSentDao;
import org.mq.captiway.scheduler.dao.DRSentDaoForDML;
import org.mq.captiway.scheduler.dao.EmailQueueDao;
import org.mq.captiway.scheduler.dao.EmailQueueDaoForDML;
import org.mq.captiway.scheduler.dao.ExternalSMTPEventsDao;
import org.mq.captiway.scheduler.dao.ExternalSMTPEventsDaoForDML;
import org.mq.captiway.scheduler.dao.SuppressedContactsDao;
import org.mq.captiway.scheduler.dao.SuppressedContactsDaoForDML;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ExternalSMTPEventsProcessor extends TimerTask  implements ApplicationContextAware  {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);


private ApplicationContext applicationContext;
public void setApplicationContext(ApplicationContext applicationContext) {
	
	this.applicationContext = applicationContext;
	
}
	
private ExternalSMTPEventsQueue externalSMTPEventsQueue ;
	
	
public ExternalSMTPEventsQueue getExternalSMTPEventsQueue() {
	
	return externalSMTPEventsQueue;
}

public void setExternalSMTPEventsQueue(	ExternalSMTPEventsQueue externalSMTPEventsQueue) {
	
	this.externalSMTPEventsQueue = externalSMTPEventsQueue;
	
}

private CampaignReportEventsQueue campaignReportEventsQueue;

public CampaignReportEventsQueue getCampaignReportEventsQueue() {
	return campaignReportEventsQueue;
}

public void setCampaignReportEventsQueue(
		CampaignReportEventsQueue campaignReportEventsQueue) {
	this.campaignReportEventsQueue = campaignReportEventsQueue;
}

private ExternalSMTPEventsDao externalSMTPEventsDao;

public ExternalSMTPEventsDao getExternalSMTPEventsDao() {
	return externalSMTPEventsDao;
}

public void setExternalSMTPEventsDao(ExternalSMTPEventsDao externalSMTPEventsDao) {
	this.externalSMTPEventsDao = externalSMTPEventsDao;
}

private ExternalSMTPEventsDaoForDML externalSMTPEventsDaoForDML;


public ExternalSMTPEventsDaoForDML getExternalSMTPEventsDaoForDML() {
	return externalSMTPEventsDaoForDML;
}

public void setExternalSMTPEventsDaoForDML(
		ExternalSMTPEventsDaoForDML externalSMTPEventsDaoForDML) {
	this.externalSMTPEventsDaoForDML = externalSMTPEventsDaoForDML;
}

private CampaignReportEventsDao campaignReportEventsDao;



public CampaignReportEventsDao getCampaignReportEventsDao() {
	return campaignReportEventsDao;
}

public void setCampaignReportEventsDao(
		CampaignReportEventsDao campaignReportEventsDao) {
	this.campaignReportEventsDao = campaignReportEventsDao;
}


private CampaignReportEventsDaoForDML campaignReportEventsDaoForDML;


public CampaignReportEventsDaoForDML getCampaignReportEventsDaoForDML() {
	return campaignReportEventsDaoForDML;
}

public void setCampaignReportEventsDaoForDML(
		CampaignReportEventsDaoForDML campaignReportEventsDaoForDML) {
	this.campaignReportEventsDaoForDML = campaignReportEventsDaoForDML;
}

private DRSentDao drSentDao;
private DRSentDaoForDML drSentDaoForDML;
public DRSentDaoForDML getDrSentDaoForDML() {
	return drSentDaoForDML;
}

public void setDrSentDaoForDML(DRSentDaoForDML drSentDaoForDML) {
	this.drSentDaoForDML = drSentDaoForDML;
}

public DRSentDao getDrSentDao() {
	return drSentDao;
}

public void setDrSentDao(DRSentDao drSentDao) {
	this.drSentDao = drSentDao;
}

private EmailQueueDao emailQueueDao;
private EmailQueueDaoForDML emailQueueDaoForDML;

public EmailQueueDaoForDML getEmailQueueDaoForDML() {
	return emailQueueDaoForDML;
}

public void setEmailQueueDaoForDML(EmailQueueDaoForDML emailQueueDaoForDML) {
	this.emailQueueDaoForDML = emailQueueDaoForDML;
}

public EmailQueueDao getEmailQueueDao() {
	return emailQueueDao;
}

public void setEmailQueueDao(EmailQueueDao emailQueueDao) {
	this.emailQueueDao = emailQueueDao;
}

private ContactsDao contactsDao;

public ContactsDao getContactsDao() {
	return contactsDao;
}

public void setContactsDao(ContactsDao contactsDao) {
	this.contactsDao = contactsDao;
}

private ContactsDaoForDML contactsDaoForDML;
public ContactsDaoForDML getContactsDaoForDML() {
	return contactsDaoForDML;
}

public void setContactsDaoForDML(ContactsDaoForDML contactsDaoForDML) {
	this.contactsDaoForDML = contactsDaoForDML;
}

/* private UsersDao usersDao;

public UsersDao getUsersDao() {
	return usersDao;
}

public void setUsersDao(UsersDao usersDao) {
	this.usersDao = usersDao;
}

private SuppressedContactsDao suppressedContactsDao;

public SuppressedContactsDao getSuppressedContactsDao() {
	return suppressedContactsDao;
}

public void setSuppressedContactsDao(SuppressedContactsDao suppressedContactsDao) {
	this.suppressedContactsDao = suppressedContactsDao;
}
*/
private volatile boolean isRunning;
public boolean isRunning() {
	if(logger.isDebugEnabled()) logger.debug("isRunning ::"+isRunning);
	return isRunning;
}

List<ExternalSMTPEvents> eventsToBeProcessed = new ArrayList<ExternalSMTPEvents>();
List<CampaignReportEvents> repEventsToBeProcessed = new ArrayList<CampaignReportEvents>();
	@Override
	public void run() {
		
		try {
			if(logger.isInfoEnabled()) logger.info("------- just entered ExternalSMTPEventsProcessor to push events to DB -------");
			
			this.isRunning = true;
			
			if(externalSMTPEventsQueue.getQueueSize() > 0) {
				
				int count = 0;
				String[] paramArr = null;
				JSONArray jsonRootArray = null;
				String jsonRootArrayStr = null;
				Long userId = null;
				Long crId = null;
				String sentId = null;
				String eventType = null;
				String serverName = null;
				String email = null;
				String statusCode = null;
				String type = null;
				String reason = null;
				String emailType = null;
				 final String SESType = "SES";
				int retryCount = 0;

				Map<String,String> responseMap = new HashMap<String,String>();
				while(( jsonRootArrayStr = externalSMTPEventsQueue.getObjFromQueue()) != null ) {
					
					try {
						jsonRootArray = new JSONArray(jsonRootArrayStr.toString());
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						logger.error("Exception ::::", e1);
					}
					try {
						++count;
						
						for(int i = 0; i < jsonRootArray.length() ; i++) {
					
							
							JSONObject jsonObj = (JSONObject) jsonRootArray.get(i);
						//	String fromSource =   (jsonObj.get("rcpt_meta").get("CampaignSource") ;
							String csSource = Constants.STRING_NILL;
							 
							
							if(jsonObj.has("msys"))
							{
								JSONObject innerJsonObj = (JSONObject)((JSONObject)jsonObj.get("msys")).get("message_event");
									csSource = (String)((JSONObject)innerJsonObj.get("rcpt_meta")).get("CampaignSource");

							}
								if(jsonObj.has("rcpt_meta")) {
										 csSource = (String)((JSONObject)jsonObj.get("rcpt_meta")).get("CampaignSource");
					//	String spreport = (String)csSource.get("CampaignSource"); 
										 Object retries = jsonObj.get("num_retries");
											retryCount = retries instanceof  String ? Integer.parseInt((String) retries) : 
												(retries instanceof Integer) ? (Integer) retries : 0;
									//	 String retries = (String)jsonObj.get("num_retries");
										 reason = (String)jsonObj.get("reason");
										 if(retryCount > 0 && reason.contains("over quota")) {
											 logger.info("Here condition for over quote, received for 2nd time.");
											 continue; // if full it retires over more time. we don't process such. 
										 }
						}
					
							if(jsonObj.has(Constants.URL_PARAM_SG_EMAIL)||jsonObj.has(Constants.URL_PARAM_EMAIL_TYPE) || 
									jsonObj.has(Constants.URL_PARAM_EQ_EMAIL)) {
								//write sendgrid call here.
								logger.info("In sendgrid block");
								responseMap  = prepareSendGridReportObj(jsonObj);
								if(responseMap ==  null) {
									//related to error
									continue;
								}
								if(responseMap.containsKey("EQID")){
									//related to single email.
									continue;
								}
								if(!responseMap.get("source").equalsIgnoreCase("CampaignSchedule")) {
							//		logger.info("Response object is for digitalreceipt"+ responseMap);
									continue;
								}
								else {
								//	logger.info("Response object is not null"+ responseMap);

								eventType = responseMap.get("eventType");
								userId = Long.parseLong(responseMap.get("userId"));
								crId = Long.parseLong(responseMap.get("crId"));								
								email = responseMap.get("email");
								type = responseMap.get("type");
								statusCode = responseMap.get("status");
								reason = responseMap.get("reason");
								serverName = responseMap.get("serverName");
								}
								logger.info("Completed sendgrid delivery object made");
							}
							else if(jsonObj.has(Constants.SES_REPORT_TYPE)) {
								// write ses call here.
								responseMap = prepareAmazonSESReportObj(jsonObj);
							}
							//else if(be specific instead genralizing else){
							else if(csSource.equalsIgnoreCase(Constants.SMTP_SPARKPOST) || csSource.equalsIgnoreCase(SESType) ){
								// write sparkpost call here.
								logger.info("In the sparkpost block");
								responseMap = prepareSparkPostReportObj(jsonObj);
								if(responseMap ==  null) {
									continue;
								}
								if(!responseMap.get("source").equalsIgnoreCase("CampaignSchedule")){
									continue;
								}
								else {
								eventType = responseMap.get("event");
								userId = Long.parseLong(responseMap.get("userId"));
								crId = Long.parseLong(responseMap.get("crId"));
								email = responseMap.get("email");
								type = responseMap.get("type");
								serverName = responseMap.get("serverName");
								statusCode = responseMap.get("statusCode");
								reason = responseMap.get("reason");
								}

								
							}
														
							logger.info("server name like : "+serverName+"for email Id "+email);
									
							
							//create an events object and insert it into DB
							logger.info("creating the object for the events.."+email+" size of "+eventsToBeProcessed.size());
							ExternalSMTPEvents newEvent = new ExternalSMTPEvents(serverName, eventType,  userId, crId, Calendar.getInstance(), email);
							newEvent.setStatusCode(statusCode);//TODO statusCode
							newEvent.setType(type);
							newEvent.setReason(reason);
							eventsToBeProcessed.add(newEvent);
							
							logger.debug("eventsList size ::"+eventsToBeProcessed.size());
							if(eventsToBeProcessed.size() >= 200 ) {
								
								logger.debug("saving events to DB......");
								//externalSMTPEventsDao.saveByCollection(eventsToBeProcessed);
								externalSMTPEventsDaoForDML.saveByCollection(eventsToBeProcessed);

								eventsToBeProcessed.clear();
							}//
												
						
						if(count >= 2000)//to allow only 2000 to be pushed into DB
							break;
						} // for 
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						if(logger.isErrorEnabled()) logger.error("Exception : ",e);
						continue;
						
					}

					
				}//while
								
				if(eventsToBeProcessed.size() > 0 ) {
					
					logger.debug("saving events to DB......");
					//externalSMTPEventsDao.saveByCollection(eventsToBeProcessed);
					externalSMTPEventsDaoForDML.saveByCollection(eventsToBeProcessed);
					eventsToBeProcessed.clear();
				}//
			}//if
			
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			logger.error("Exception while deleting spam reports at SendGrid ", e);
			
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception in saving" , e);
		}
		
		finally{
			
			isRunning = false;
			
		}
		
		
		
	}
		
		public Map<String, String> prepareSparkPostReportObj(JSONObject jsonObj) {
// TODO Auto-generated method stub
			//    	"msys": { #jsonObj.get(0);
			//        "rcpt_to": "priyab@optculture.com", #jsonObj.get(0).get(message_event).get(rcpt_to);
			//        "type": "delivery", #jsonObj.get(0).get(message_event).get(type);
			//        "sending_ip": "156.70.121.139", #jsonObj.get(0).get(message_event).get(sending_ip);
			//			need to get the other parameters
			// [crId, userId, SentId,event type: get from the header][reason, status: get from the response] - to update in DB.
			
			logger.info("....Entered in sparkpost updation method .....");
			Map<String,String> resMap = new HashMap<String,String>();

			try {
				String email = Constants.STRING_NILL;
				String event = Constants.STRING_NILL;
				String crId = Constants.STRING_NILL;
				String userId = Constants.STRING_NILL;
				String sentId = Constants.STRING_NILL;
				String statusCode = Constants.STRING_NILL;
				String reason = Constants.STRING_NILL;
				String source = Constants.STRING_NILL;
				String serverName = Constants.STRING_NILL;
				String type = Constants.STRING_NILL;
				JSONObject customTags = null;
				String schedulerWebhookIp = PropertyUtil.getPropertyValue("schedulerWebhookIp") != null ? PropertyUtil.getPropertyValue("schedulerWebhookIp") : Constants.STRING_NILL;
				
			JSONObject jsonObj1 = (JSONObject)jsonObj.get("msys");
			if(jsonObj1.has("message_event")) {
				JSONObject emailObj = (JSONObject) jsonObj1.get("message_event");
				 email = (String)emailObj.get("rcpt_to");
				logger.info("email :>>>>>> "+email);
				resMap.put("email",email);
				
				 event = (String)emailObj.get("type");
				 if(emailObj.has("bounce_class")) {
					 type = (String)emailObj.get("bounce_class");
					 resMap.put("type", type);
				 }
				 
				if(event.equalsIgnoreCase("delivery")) {
					 resMap.put("event",Constants.EXTERNAL_SMTP_EVENTS_TYPE_DELIVERED);

				}/*else if(event.equalsIgnoreCase("delay")) {
					 resMap.put("event",Constants.CS_STATUS_BOUNCED);

				}*/else {
					resMap.put("event",event);
				}
					logger.info("event : "+event);
					if(emailObj.has("error_code")) {
				 statusCode = (String)emailObj.get("error_code");
					resMap.put("statusCode", statusCode);
					logger.info("statusCode : "+statusCode);
					}
					
					if(emailObj.has("raw_reason")) {
					 reason = (String)emailObj.get("raw_reason");
					resMap.put("reason", reason);
					logger.info("Reason : "+reason);
					}

			// not required for campaigns
		//	resMap.put("eventType", eventType);
		//	resMap.put("userId", userId);
	       /* "rcpt_meta": {
		          "source": "CampaignSchedule",
		          "sentId": "40466",
		          "crId": "78882",
		          "userId": "1076"
		          "serverName" :"http://qcapp.optculture.com"
		        },
*/
			if(emailObj.has("rcpt_meta")) {
				 customTags = emailObj.getJSONObject("rcpt_meta");
				if(customTags.has("source")) {
					source = (String) customTags.get("source");
					resMap.put("source", source);
					logger.info("source : "+source);

				}
				if(customTags.has("sentId")) {
					sentId = (String)customTags.get("sentId");
					resMap.put("sentId", sentId);
					logger.info("sentId : "+sentId);

				}
				if(customTags.has("crId")) {
					crId = (String) customTags.get("crId");
					resMap.put("crId", crId);
					logger.info("crId : "+crId);

				}
				if(customTags.has("userId")) {
					userId = (String)customTags.get("userId");
					resMap.put("userId",userId);
					logger.info("userId : "+userId);

				}
				if(customTags.has("serverName")) {
					serverName = (String)customTags.get("serverName");
					resMap.put("serverName",serverName);
					logger.info("serVerName : "+serverName);

				}

			}
			}
			if(!jsonObj.has("msys")) {
			reason = (String)jsonObj.get("raw_reason");
			resMap.put("reason", reason);
			statusCode = (String) jsonObj.get("error_code");
			resMap.put("statusCode", statusCode);
			email = ( String)jsonObj.get("rcpt_to");
			resMap.put("email", email);
			if(jsonObj.has("bounce_class")) {
				type = (String) jsonObj.get("bounce_class");
				resMap.put("type", type);
			}
			if(jsonObj.has("rcpt_meta")){
				
				if(customTags.has("source")) {
					source = (String) customTags.get("source");
					resMap.put("source", source);
					logger.info("source : "+source);

				}
				if(customTags.has("sentId")) {
					sentId = (String)customTags.get("sentId");
					resMap.put("sentId", sentId);
					logger.info("sentId : "+sentId);

				}
				if(customTags.has("crId")) {
					crId = (String) customTags.get("crId");
					resMap.put("crId", crId);
					logger.info("crId : "+crId);

				}
				if(customTags.has("userId")) {
					userId = (String)customTags.get("userId");
					resMap.put("userId",userId);
					logger.info("userId : "+userId);

				}
				if(customTags.has("serverName")) {
					serverName = (String)customTags.get("serverName");
					resMap.put("serverName",serverName);
					logger.info("serVerName : "+serverName);

				}

				
			}
			}
			if((serverName != null && !serverName.equalsIgnoreCase(PropertyUtil.getPropertyValue("schedulerIp")) 
				&& !serverName.equalsIgnoreCase(PropertyUtil.getPropertyValue("subscriberIp")))
					
				&& (!schedulerWebhookIp.isEmpty() && !serverName.equalsIgnoreCase(schedulerWebhookIp))) { //as ip is returned instead of domain(Digital receipt)
				

				if(logger.isErrorEnabled()) logger.error("Exception : Differenct server, Redirecting to server :" + serverName);
				
				JSONArray redirectingArr = new JSONArray();
				//redirectingArr.add(jsonObj);
				redirectingArr.put(jsonObj);
				String redirecturl = serverName + "/Scheduler/sendGridEventHandler.mqrm";
				try {
					PostMethod post = new PostMethod(redirecturl);
						
					//post.addRequestHeader("Content-Type", "application/json");
				     
					
					StringRequestEntity requestEntity = new StringRequestEntity(
							//redirectingArr.toJSONString(),
							redirectingArr.toString(),
						    "application/json",
						    "UTF-8");

					post.setRequestEntity(requestEntity);
					
				     HttpClient httpClient = new HttpClient();
				     int status = httpClient.executeMethod(post);
				    if(status != -1) {
				    	
				    	post.releaseConnection();
				    	
				    }
				
				}catch(Exception e) {
					logger.error("Exception while deleting contact.");
				}
				return null;
			}
			

			if(source != null  && source.isEmpty()) {
			if( source.equals(Constants.EQ_TYPE_DIGITALRECIEPT) || source.equals(Constants.COUP_GENT_CAMPAIGN_TYPE_SINGLE_EMAIL)){
				
				logger.info("logger for the email : "+email+"for sentID may9th : "+sentId);
				Long currentUserId = Long.parseLong(userId);
				updateDeliveryStatus(event, source, sentId,email,currentUserId, statusCode,reason);
			
				logger.info("Returning null as this part is skipped in the flow , Previously continue keyword is used here");
				return null;
		}
			}
			
		
			}
			catch(Exception e) {
				logger.debug("Error occurred in sparkpost object"+e);
			}
			
			
			
return resMap;
}

	public Map<String, String> prepareAmazonSESReportObj(JSONObject jsonObj) {
// TODO Auto-generated method stub
		Map<String,String> resMap = new HashMap<String,String>();
		try {
			String notificationType = (String)jsonObj.get("notificationType");
			String userId = Constants.STRING_NILL;
			String crId = Constants.STRING_NILL;
			String sentId = Constants.STRING_NILL;
		//	String serverName = Constants.STRING_NILL;
			String name = Constants.STRING_NILL;
			String value = Constants.STRING_NILL;
			String email = Constants.STRING_NILL;
			String emailType = Constants.STRING_NILL;
			String reason = Constants.STRING_NILL;
			String statusCode = Constants.STRING_NILL;
		//	jsonRootArray = new JSONArray(jsonRootArrayStr.toString());

		/*	JSONArray emailList = new JSONArray(jsonObj.getJSONArray("destination"));
			JSONObject recpObj = (JSONObject) emailList.get(0);
			String email = recpObj.toString(); */
			
			// of delivered no reason for it. 
			JSONObject mailObj = (JSONObject) jsonObj.get("mail");
			statusCode = (String)mailObj.get("smtpResponse");
			
			JSONArray header = new JSONArray(jsonObj.getJSONArray("headers"));
			for(int i= 0;i<header.length();i++) {
				JSONObject json = header.getJSONObject(i);
				 name = (String) json.get("name");
				 if(name.equalsIgnoreCase("To")) {
					 value = (String) json.get("value");
					 email = value;
					 resMap.put("email",value );
				 }else if(name.equalsIgnoreCase("X-OPT-CAMPAIGN-REPORT-ID")) {
					 value = (String) json.get("value");
					 crId = value;
					 resMap.put("crId", value);
				 }
				 else if(name.equalsIgnoreCase("X-OPT-SENTID")) {
					 value = (String) json.get("value");
					 sentId = value;
					 resMap.put("sentId", value);
				 }
				 else if(name.equalsIgnoreCase("X-OPT-USERID")) {
					 value = (String) json.get("value");
					 userId = value;
					 resMap.put("userId", value);
				 }
				 else if(name.equalsIgnoreCase("X-OPT-SOURCE")) {
					 value = (String) json.get("value");
					 emailType = value;
					 resMap.put("emailType", value);
				 }
			}
	if(emailType != null && !emailType.isEmpty()) {
				
				try {
					
					logger.info("emailType ::"+emailType);
						//logger.info("DR ::"+emailType);
									
				logger.info(" updating email for the  email :"+email+"senderId"+sentId+" eventType ::"+notificationType );
				
				if( emailType.equals(Constants.EQ_TYPE_DIGITALRECIEPT) || emailType.equals(Constants.COUP_GENT_CAMPAIGN_TYPE_SINGLE_EMAIL)){
					
					logger.info("logger for the email : "+email+"for sentID may9th : "+sentId);
				
					updateDeliveryStatus(notificationType, emailType, sentId,email,Long.parseLong(userId), statusCode,reason);
				
					logger.info("Returning null as this part is skipped in the flow , Previously continue keyword is used here");
				
					return null;
			}
				
				

				if(notificationType == null  || crId == null || 
						email == null || userId == null) {
					logger.error("** Exception : Required parameter in query string not found.");
					logger.debug("separated flow, Returning null as the previously continue keyword used here");
					return null;
				}	
		}catch(Exception e) {
			logger.error("Exception caused here : "+e);
		}
	} // if emailtype
	
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
	}
		
return resMap;
}

	
public Map<String,String> prepareSendGridReportObj(JSONObject jsonObj) {
		
	Map<String,String> resMap = new HashMap<String,String>();

	try {
		logger.info("Entered in the prepare sendgrid flow >>>>> "+jsonObj);
		
		String	statusCode = Constants.STRING_NILL;
		String emailType = Constants.STRING_NILL; 
		String sentId  = Constants.STRING_NILL;
		String eventType = (String) (jsonObj.get(Constants.URL_PARAM_EVENT));
		String	reason = Constants.STRING_NILL;
		String	type = Constants.STRING_NILL;
		String schedulerWebhookIp = PropertyUtil.getPropertyValue("schedulerWebhookIp") != null ? PropertyUtil.getPropertyValue("schedulerWebhookIp") : Constants.STRING_NILL;
		resMap.put("eventType", eventType);
		Object crIdObj = null;
		String crIdStr = Constants.STRING_NILL;
		Long userId = 0L;
		
		logger.debug("EventType : "+eventType);

		if(jsonObj.has(Constants.URL_PARAM_EMAIL_TYPE)) {
			
			emailType  = (String) jsonObj.get(Constants.URL_PARAM_EMAIL_TYPE);
			resMap.put("source", emailType);

		}
		if(jsonObj.has(Constants.URL_PARAM_SG_EMAIL)) {
			
			emailType = (String) jsonObj.get(Constants.URL_PARAM_SG_EMAIL);
			resMap.put("source", emailType);
		}
		
		Object userIdObj = jsonObj.get(Constants.URL_PARAM_USERID);
		userId = userIdObj instanceof  String ? Long.parseLong((String) userIdObj) : 
			(userIdObj instanceof Integer ? new Long((Integer) userIdObj) : null);
		

		
		logger.debug("UserId : "+userId);
		
		resMap.put("userId", userId+"");
		String serverName = (String) jsonObj.get(Constants.URL_PARAM_SERVERNAME);
		resMap.put("serverName", serverName);
		logger.info("server name : "+serverName);

		String email = (String) jsonObj.get(Constants.URL_PARAM_EMAIL);
		resMap.put("email", email);
		logger.debug("email : "+email);
		
		if(jsonObj.has(Constants.URL_PARAM_EQ_EMAIL)) { // single email
			sentId = jsonObj.has(Constants.URL_PARAM_EQ_EMAIL) ? (String) jsonObj.get(Constants.URL_PARAM_EQ_EMAIL) : "";
			resMap.put("EQID", sentId);
			emailType = Constants.COUP_GENT_CAMPAIGN_TYPE_SINGLE_EMAIL;
			logger.info("EQID  : "+sentId);

		}

		
		if(jsonObj.has(Constants.URL_PARAM_STATUS)) {
			statusCode = jsonObj.has(Constants.URL_PARAM_STATUS) ? (String) jsonObj.get(Constants.URL_PARAM_STATUS) : "";
			resMap.put("status", statusCode);
			logger.info("status  : "+statusCode);

		}
		
		
		if(jsonObj.has(Constants.URL_PARAM_SENTID)){
		
			 sentId = jsonObj.has(Constants.URL_PARAM_SENTID) ?(String) jsonObj.get(Constants.URL_PARAM_SENTID): "";
			 
			 resMap.put("sentId",sentId);
		
		
		
		}if(jsonObj.has(Constants.URL_PARAM_CRID)) {
			
			
			
			/*Long crId = crIdObj instanceof  String ? Long.parseLong((String) crIdObj) : 
				(crIdObj instanceof Integer ? new Long((Integer) crIdObj) : null);*/
			
			//crId = Long.parseLong((String) jsonObj.get(Constants.URL_PARAM_CRID));
				crIdObj = jsonObj.get(Constants.URL_PARAM_CRID);
				 Long crId = crIdObj instanceof  String ? Long.parseLong((String) crIdObj) : 
					(crIdObj instanceof Integer ? new Long((Integer) crIdObj) : null);

			 crIdStr = crId.toString();
			
			resMap.put("crId",crIdStr);
			
			logger.info("CrID inserted : "+crId);

		}
		type = jsonObj.has(Constants.URL_PARAM_TYPE) ? (String) jsonObj.get(Constants.URL_PARAM_TYPE) : ""; 
		
		resMap.put("type", type);
	
		logger.info("type  : "+type);
		
		reason = jsonObj.has(Constants.URL_PARAM_REASON) ? (String) jsonObj.get(Constants.URL_PARAM_REASON) : "";
	
		resMap.put("reason", reason);
	
	logger.info("reason: "+reason);

	
		statusCode = jsonObj.has(Constants.URL_PARAM_STATUS) ? (String) jsonObj.get(Constants.URL_PARAM_STATUS) : "";
		resMap.put("statusCode", statusCode);
		logger.info("Status code: "+statusCode);

		//Added Logger 2.3.11
		if(eventType!=null && statusCode != null  && 
				Constants.EXTERNAL_SMTP_EVENTS_TYPE_BOUNCE.equalsIgnoreCase(eventType) 
				&& statusCode.startsWith("5") ){
			logger.debug("Checking for bounce eventType :: "+eventType +" : userId :: "+userId+" :serverName :: "+serverName+" : email :: "+email+" : type :: "+type+" : reason :: "+reason);
		} 

		
		logger.info("server name like : "+serverName+"for email Id "+email);

		logger.info("event type::::::::: "+eventType+"   email id >>>> "+email);
		
		
					
		//		logger.info(" updating email for the  email :"+email+"senderId"+sentId+" eventType ::"+eventType );
	/*	if((serverName != null && !serverName.equalsIgnoreCase(PropertyUtil.getPropertyValue("schedulerIp"))
					 && !serverName.equalsIgnoreCase(PropertyUtil.getPropertyValue("subscriberIp")))
				&& (!schedulerWebhookIp.isEmpty() && !serverName.equalsIgnoreCase(schedulerWebhookIp))) { //as ip is returned instead of domain (Digital receipts)
			

			if(logger.isErrorEnabled()) logger.error("Exception : Differenct server, Redirecting to server :" + serverName);
			
			JSONArray redirectingArr = new JSONArray();
			//redirectingArr.add(jsonObj);
			redirectingArr.put(jsonObj);
			String redirecturl = serverName + "/Scheduler/sendGridEventHandler.mqrm";
			try {
				PostMethod post = new PostMethod(redirecturl);
					
				//post.addRequestHeader("Content-Type", "application/json");
			     
				
				StringRequestEntity requestEntity = new StringRequestEntity(
						//redirectingArr.toJSONString(),
						redirectingArr.toString(),
					    "application/json",
					    "UTF-8");

				post.setRequestEntity(requestEntity);
				
			     HttpClient httpClient = new HttpClient();
			     int status = httpClient.executeMethod(post);
			    if(status != -1) {
			    	
			    	post.releaseConnection();
			    	
			    }
			
			}catch(Exception e) {
				logger.error("Exception while deleting contact.");
			}
			return null;
		}
*/		

				
				if( emailType.equals(Constants.EQ_TYPE_DIGITALRECIEPT) || emailType.equals(Constants.COUP_GENT_CAMPAIGN_TYPE_SINGLE_EMAIL)){
					
					logger.info("logger for the email : "+email+"for sentID may9th : "+sentId);
				
					updateDeliveryStatus(eventType, emailType, sentId+"",email,userId, statusCode,reason);
				
					logger.info("Returning null as this part is skipped in the flow , Previously continue keyword is used here");
				
					return null;
			}

			

				String userIdStr = userId+"";

				if(eventType == null  || crIdStr == null || 
						email == null || userIdStr == null) {
					logger.error("** Exception : Required parameter in query string not found.");
					logger.debug("separated flow, Returning null as the previously continue keyword used here");
					return null;
				}
				
				 if( !eventType.equalsIgnoreCase(Constants.EXTERNAL_SMTP_EVENTS_TYPE_DROPPED) && 
						 !(eventType.equalsIgnoreCase(Constants.EXTERNAL_SMTP_EVENTS_TYPE_BOUNCE)) &&
						 !(eventType.equalsIgnoreCase(Constants.EXTERNAL_SMTP_EVENTS_TYPE_SPAMREPORT)) //{
						 && !(eventType.equalsIgnoreCase(Constants.EXTERNAL_SMTP_EVENTS_TYPE_DELIVERED))) {
					 logger.error("Exception : event is not found :" + serverName);
					 logger.debug("Previosly continue keyword is used here, Returning null");
					 return null;
				 }
				
				if(serverName==null || serverName.trim().isEmpty()) {
					if(logger.isErrorEnabled()) logger.error("Exception : ServerName is not found :" + serverName);
					 logger.debug("Previosly continue keyword is used here, Returning null");
					 return null;
				}
				
				if(serverName != null && serverName.contains("localhost:") ) {
					 logger.debug("Previosly continue keyword is used here, Returning null");
					return null;
					
				}
										

	}
	catch(Exception e) {
		logger.error("Exception in prepareSendgridReportObj"+e);
	}
	logger.info("returning the result object : "+resMap.get("eventType"));
		return resMap;
	}
	
	 /**
     * Updates the CampaignSent and CampaignReport tables for the specified type
     * @param campaignSent - CampaignSent Object
     * @param type - Specifies for which type to update the report 
     * 			(Opens, Clicks, Bounces, Unsubscribes ...)
     * @return
     */
  /*  public int updateCampaignSent(CampaignSent campaignSent, String type) {
        if(logger.isDebugEnabled()) logger.debug("-- Just Entered --");    
        
        int updateCount = 0;
		if(campaignSent == null) {
			logger.warn("CampiagnSent Object is null");
			return updateCount;
		}
		try {
			updateCount = campaignSentDao.updateCampaignSent(campaignSent.getSentId(), type);
			if(updateCount == 0 ) return updateCount;
		} catch (Exception e) {
			logger.error("Exception : Problem while updating the "+ type +" in CampaignSent \n", e);
		}
		
		
		
        if(logger.isDebugEnabled()) logger.debug("-- Exit --"+updateCount);
        return updateCount;
    
    }*/
    
  /*  public int updateCampaignReport(CampaignSent campaignSent, String type) {
    	
		try {
			Long  crId = campaignSentDao.getCrIdBySentId(campaignSent.getSentId());
			logger.debug("Updating Campaign report , CrId Is : " + crId);		
			int resp = campaignReportDao.updateCampaignReport(crId, campaignSent.getSentId(), type);
			logger.debug("Saved to Campaign Report : "+ resp);
			return resp;
		} catch (Exception e) {
			logger.error("Exception : Problem while updating the "+ type +" in CampaignReport \n", e);
			return 0;
		}
    	
    }
    
    public void addToSuppressedContacts(Long userId, String emailId,String type) {
		try {
			SuppressedContacts suppressedContact = new SuppressedContacts();
			Users users = usersDao.find(userId); 
			suppressedContact.setUser(users);
			suppressedContact.setEmail(emailId);
			suppressedContact.setType(type);
			suppressedContactsDao.saveOrUpdate(suppressedContact);
			logger.debug("Added successfully to suppress contacts .");
		} catch (Exception e) {
			logger.error("**Exception : while adding contact to suppress Contacts list :", e);
		}
	}
	*/
	public int updateDeliveryStatus(String eventType,String emailType, String sentIdStr,String emailId, Long userId ,String statusCode ,String reason){
		
		logger.info("entered update delivery status.. understanding the flow for the event processor :"+emailId +" userID : "+userId);

		
		
		int update = 0;
		try {
			if(eventType == null || sentIdStr == null){
				logger.info("No even or sent ID");
				return 0;  
			}
			
			long sentId;
			try {
				sentId = Long.parseLong(sentIdStr);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				logger.error("got NUmberFormat exception**",e);
				return 0;
			}
			logger.info("understanding the flow for the event processor :"+emailId +" userID : "+userId);
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			SuppressedContactsDao  suppressedContactsDao =(SuppressedContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SUPPRESSEDCONTACTS_DAO);
			SuppressedContactsDaoForDML  suppressedContactsDaoForDML =(SuppressedContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SUPPRESSEDCONTACTS_DAO_FOR_DML);
			SuppressedContacts suppressedContacts= null;
			String type = null;
			String conStatus = null;
			boolean addToSupp = false;
			
			/*if(emailType.equals(Constants.EQ_TYPE_DIGITALRECIEPT))
				//update = drSentDao.updateStaus(sentId,eventType);
				update = drSentDaoForDML.updateStaus(sentId,eventType);
			else if(emailType.equals(Constants.COUP_GENT_CAMPAIGN_TYPE_SINGLE_EMAIL))
				//update = emailQueueDao.updateDeliveryStatus(sentId,eventType);
				emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
				update = emailQueueDaoForDML.updateDeliveryStatus(sentId,eventType);*/
			if(emailType.equals(Constants.EQ_TYPE_DIGITALRECIEPT)){
				//update = drSentDao.updateStaus(sentId,eventType);
				if(eventType.equals(Constants.EXTERNAL_SMTP_EVENTS_TYPE_DELIVERED)){
					update = drSentDaoForDML.updateStaus(sentId,"Success");
				}
				else {
					update = drSentDaoForDML.updateStaus(sentId,eventType);
				}
			}
			else if(emailType.equals(Constants.COUP_GENT_CAMPAIGN_TYPE_SINGLE_EMAIL)){
				//update = emailQueueDao.updateDeliveryStatus(sentId,eventType);
				emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
				if(eventType.equals(Constants.EXTERNAL_SMTP_EVENTS_TYPE_DELIVERED)){
					update = emailQueueDaoForDML.updateDeliveryStatus(sentId,"Success");
				}
				else {
					update = emailQueueDaoForDML.updateDeliveryStatus(sentId,eventType);
				}
			}
			if(eventType.equalsIgnoreCase(Constants.EXTERNAL_SMTP_EVENTS_TYPE_BOUNCE)  ){
				
				//contactsDao.updateEmailStatusByUserId(emailId, userId, Constants.CONT_STATUS_BOUNCED, Constants.CONT_STATUS_ACTIVE);
				type = Constants.SUPP_TYPE_BOUNCED;
				conStatus = Constants.CONT_STATUS_BOUNCED;
				addToSupp = true;
				
			}else if(eventType.equalsIgnoreCase(Constants.EXTERNAL_SMTP_EVENTS_TYPE_SPAMREPORT)){
				
				type = Constants.CS_STATUS_SPAMMED;
				conStatus = Constants.CONT_STATUS_REPORT_AS_SPAM;
				addToSupp = true; 
				
			} 
			
			if(addToSupp) {
				contactsDaoForDML.updateEmailStatusByUserId(emailId, userId, conStatus, Constants.CONT_STATUS_ACTIVE);
				
				try {
					suppressedContacts = new SuppressedContacts();
					suppressedContacts.setEmail(emailId);
					suppressedContacts.setType(type);
					Users user = usersDao.findByUserId(userId);
					
					if(user != null)suppressedContacts.setUser(user);
					suppressedContacts.setSuppressedtime(Calendar.getInstance());
					suppressedContacts.setReason(reason);
					suppressedContactsDaoForDML.saveOrUpdate(suppressedContacts);
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("got exception**",e);
			return 0;
		}
		return update;
		
	}
	
	
}
