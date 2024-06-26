package org.mq.captiway.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
			// process the request URL string devide it into valid tokens  
			
			this.isRunning = true;
			
			if(externalSMTPEventsQueue.getQueueSize() > 0) {
				
				int count = 0;
				String[] paramArr = null;
				//String requestUrlStr  = null;
				//Object jsonRootObject;
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
				//int initQSize = externalSMTPEventsQueue.getQueueSize();
				//logger.debug("size ::"+initQSize);
				while(( jsonRootArrayStr = externalSMTPEventsQueue.getObjFromQueue()) != null ) {
					
					try {
						jsonRootArray = new JSONArray(jsonRootArrayStr.toString());
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						logger.error("Exception ::::", e1);
					}
					
					/*String str= new String("fuj hg");
				
					org.json.JSONArray testJson = new org.json.JSONArray(str)*/
					//[{"timestamp":1383722885,"sg_event_id":"LuB3d4DLTIOH9yryfMZqAg","Email":"CampaignSchedule","smtp-id":"<105593389.1.1383723073733.JavaMail.magna@testserver>","email":"abc.xyz@ghi.com","reason":"550 5.1.1 Recipient address rejected: There's no recipient by that address here. ","status":"5.1.1","event":"bounce","userId":"67","type":"bounce","ServerName":"http:\/\/test.optculture.com","crId":"4311"}]

					//logger.info("JSON Root Array obj is ::"+jsonRootArray);
					try {
						//logger.debug("requestUrl ::"+requestUrlStr);
						++count;
						
						for(int i = 0; i < jsonRootArray.length() ; i++) {
					
							//http://localhost:8080/Scheduler/sendGridEventHandler.mqrm?event=deferred&crId=23&sentId=123&userId=2
							//reference to the above example request url, we need to get the individual params to be processed.
							
							JSONObject jsonObj = (JSONObject) jsonRootArray.get(i);
							
							eventType = (String) jsonObj.get(Constants.URL_PARAM_EVENT);
//							sentId = Long.parseLong((String) jsonObj.get(Constants.URL_PARAM_SENTID));
							Object userIdObj = jsonObj.get(Constants.URL_PARAM_USERID);
							userId = userIdObj instanceof  String ? Long.parseLong((String) userIdObj) : 
								(userIdObj instanceof Integer ? new Long((Integer) userIdObj) : null);
							//userId = Long.parseLong((String) jsonObj.get(Constants.URL_PARAM_USERID));
							
							serverName = (String) jsonObj.get(Constants.URL_PARAM_SERVERNAME);
							email = (String) jsonObj.get(Constants.URL_PARAM_EMAIL);
							if(jsonObj.has(Constants.URL_PARAM_STATUS))
							statusCode = jsonObj.has(Constants.URL_PARAM_STATUS) ? (String) jsonObj.get(Constants.URL_PARAM_STATUS) : "";
							type = jsonObj.has(Constants.URL_PARAM_TYPE) ? (String) jsonObj.get(Constants.URL_PARAM_TYPE) : ""; 
							reason = jsonObj.has(Constants.URL_PARAM_REASON) ? (String) jsonObj.get(Constants.URL_PARAM_REASON) : "";
							
							logger.info("event type::::::::: "+eventType+"   email id >>>> "+email);
							//Added Logger 2.3.11
							if(eventType!=null && statusCode != null  && 
									Constants.EXTERNAL_SMTP_EVENTS_TYPE_BOUNCE.equalsIgnoreCase(eventType) 
									&& statusCode.startsWith("5") ){
								logger.debug("Checking for bounce eventType :: "+eventType +" : userId :: "+userId+" :serverName :: "+serverName+" : email :: "+email+" : type :: "+type+" : reason :: "+reason);
							} 
							
							
							//For DIGITAL-RCPT bounces update
							//if(jsonObj.containsKey(Constants.URL_PARAM_EMAIL_TYPE)){
							if(jsonObj.has(Constants.URL_PARAM_EMAIL_TYPE)){
								
								emailType = (String) jsonObj.get(Constants.URL_PARAM_EMAIL_TYPE);
								sentId = (String)jsonObj.get(Constants.URL_PARAM_SENTID);
								if(emailType != null) {
									
									try {
										
											
											//logger.info("DR ::"+emailType);
											
											if(serverName != null && serverName.contains("localhost:") ) {
												
												continue;
												
											}
										
											if(serverName != null && !serverName.equalsIgnoreCase(PropertyUtil.getPropertyValue("schedulerIp"))) {
												

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
												
												
												/*if(logger.isErrorEnabled()) logger.error("Exception : Differenct server, Redirecting to server :" + serverName);
												
												//messageHeader =  "{\"unique_args\": {\"userId\": \""+ user.getUserId() +"\" ,\"EmailType\" : \""+Constants.EQ_TYPE_DIGITALRECIEPT +"\",\"sentId\" : \""+drSent.getId()+"\" ,\"ServerName\": \""+ serverName +"\" }}";
												
												String redirecturl = serverName + "/Scheduler/sendGridEventHandler.mqrm";
												String postData = "?sentId="+ sentId + "&userId="+ userId+"&EmailType="+emailType
												+ "&event="+ eventType + "&ServerName="+ serverName +
												(email != null && !email.isEmpty() ? "&email="+email : "");
												//logger.debug(" ****>>> Redirecting to : "+ redirecturl+postData );
												//open a connection to the new server(need to optimize it)
												try{
													
													URL url = new URL(redirecturl+postData);
													
													HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
													//logger.debug("CON OPENED ? "+urlconnection.getURL());
													urlconnection.setRequestMethod("POST");
													urlconnection.setRequestProperty("Content-Type","text/html");
													urlconnection.setDoOutput(true);
													OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
													out.write(postData);
													out.flush();
													out.close();
													BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
													in.close();
													urlconnection.disconnect();
										        
											    } catch (MalformedURLException me) {
											        logger.error("MalformedURLException: " , me);
											        continue;
											    } catch (Exception e) {
											    	logger.error("IOException: " , e);
											    	continue;
											    }*/
												continue;
												
											}
											
											//logger.info(" updating DRSent "+sentId+" eventType ::"+eventType );
											if( emailType.equals(Constants.EQ_TYPE_DIGITALRECIEPT) || emailType.equals(Constants.COUP_GENT_CAMPAIGN_TYPE_SINGLE_EMAIL)){
											updateDeliveryStatus(eventType, emailType, sentId,email,userId, statusCode,reason);
											continue;
										}
									} catch (Exception e) {
										// TODO Auto-generated catch block
										logger.error("got exception",e);
										continue;
									}
									
								}
								
								
								
								
								
								
								
							}
							
							
							//******************************
								
								
								
								
							//crId = Long.parseLong((String) jsonObj.get(Constants.URL_PARAM_CRID));
							Object crIdObj = jsonObj.get(Constants.URL_PARAM_CRID);
							crId = crIdObj instanceof  String ? Long.parseLong((String) crIdObj) : 
								(crIdObj instanceof Integer ? new Long((Integer) crIdObj) : null);
								
							if(eventType == null  || crId == null || 
									email == null || userId == null) {
								logger.error("** Exception : Required parameter in query string not found.");
								continue;
							}
							
							 if( !eventType.equalsIgnoreCase(Constants.EXTERNAL_SMTP_EVENTS_TYPE_DROPPED) && 
									 !(eventType.equalsIgnoreCase(Constants.EXTERNAL_SMTP_EVENTS_TYPE_BOUNCE)) &&
									 !(eventType.equalsIgnoreCase(Constants.EXTERNAL_SMTP_EVENTS_TYPE_SPAMREPORT)) ) {
								 logger.error("Exception : event is not found :" + serverName);
								 continue;
							 }
							
							if(serverName==null || serverName.trim().isEmpty()) {
								if(logger.isErrorEnabled()) logger.error("Exception : ServerName is not found :" + serverName);
								continue;
							}
							
							if(serverName != null && serverName.contains("localhost:") ) {
								
								continue;
								
							}
							
							// Ignore if the server is not app.
							
							if(!serverName.equalsIgnoreCase(PropertyUtil.getPropertyValue("schedulerIp"))) {
								
								if(logger.isErrorEnabled()) logger.error("Exception : Differenct server, Redirecting to server :" + serverName);
								
								JSONArray redirectingArr = new JSONArray();
								//redirectingArr.add(jsonObj);
								redirectingArr.put(jsonObj);
								String redirecturl = serverName + "/Scheduler/sendGridEventHandler.mqrm";
								try {
									PostMethod post = new PostMethod(redirecturl);
										
									post.addRequestHeader("Content-Type", "application/json");
								     
									
									StringRequestEntity requestEntity = new StringRequestEntity(
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
								
								
								
								
								
								
								
								
								
								
								
							/*	
								String postData = "?crId="+ crId + "&userId="+ userId + "&event=" + eventType + "&ServerName="+ serverName +
													(email != null && !email.isEmpty() ? "&email="+ email : "")+
													(statusCode != null && !statusCode.isEmpty() ? "&status="+ statusCode : "") +
													(type != null && !type.isEmpty() ? "&type="+ type : "") +
													(reason != null && !reason.isEmpty() ? "&reason=" +reason : "");
													
								logger.debug(" ****>>> Redirecting to : "+ redirecturl+postData );
								//open a connection to the new server(need to optimize it)
								try{
									
									URL url = new URL(redirecturl+postData);
									
									HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
									//logger.debug("CON OPENED ? "+urlconnection.getURL());
									urlconnection.setRequestMethod("POST");
									urlconnection.setRequestProperty("Content-Type","text/html");
									
									BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
									in.close();
									urlconnection.disconnect();
						        
							    } catch (MalformedURLException me) {
							        logger.error("MalformedURLException: " , me);
							        continue;
							    } catch (Exception e) {
							    	logger.error("IOException: " , e);
							        continue;
							    }*/
								continue;
								
							}
							
							//create an events object and insert it into DB
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
							
						}//for
					
						
						
						if(count >= 2000){//to allow only 2000 to be pushed into DB
							
							break;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						if(logger.isErrorEnabled()) logger.error("Exception : ",e);
						continue;
						
					}

					
				}//while
				
				
				
				
				
				
				
				/*while((  requestUrlStr = externalSMTPEventsQueue.getObjFromQueue()) != null ) {
					
					try {
						logger.debug("requestUrl ::"+requestUrlStr);
						if(requestUrlStr.length() > 0) {
							
							++count ;
							
							//http://localhost:8080/Scheduler/sendGridEventHandler.mqrm?event=deferred&crId=23&sentId=123&userId=2
							//reference to the above example request url, we need to get the individual params to be processed.
							
							
							requestUrlStr = requestUrlStr.substring(requestUrlStr.indexOf(Constants.URL_TOKEN_QUESTIONMARK)+1);
							if(requestUrlStr.indexOf(Constants.URL_TOKEN_AMBERSENT ) != -1 ) {
								
								paramArr = requestUrlStr.split(Constants.URL_TOKEN_AMBERSENT);
								if(paramArr != null && paramArr.length > 0) {
									String[] paramKeyValArr = null;
									for (String param : paramArr) {
										if(param.indexOf(Constants.URL_TOKEN_EQUALTO) != -1) {
											
											paramKeyValArr = param.split(Constants.URL_TOKEN_EQUALTO);
											if(paramKeyValArr[0].equalsIgnoreCase(Constants.URL_PARAM_EVENT)) {
												
												try {
													eventType = paramKeyValArr[1];
												} catch (Exception e) {
													// TODO Auto-generated catch block
													continue;
												}
												
											}else if(paramKeyValArr[0].equalsIgnoreCase(Constants.URL_PARAM_SENTID)) {
												
												try {
													sentId = Long.parseLong(paramKeyValArr[1]);
												} catch (Exception e) {
													// TODO Auto-generated catch block
													continue; 
												}
												
											}else if(paramKeyValArr[0].equalsIgnoreCase(Constants.URL_PARAM_USERID)) {
												
												try {
													userId = Long.parseLong(paramKeyValArr[1]);
												} catch (Exception e) {
													// TODO Auto-generated catch block
													continue; 
												}
												
											}else if(paramKeyValArr[0].equalsIgnoreCase(Constants.URL_PARAM_CRID)) {
												
												try {
													crId = Long.parseLong(paramKeyValArr[1]);
												} catch (Exception e) {
													// TODO Auto-generated catch block
													continue; 
												}
												
											}else if(paramKeyValArr[0].equalsIgnoreCase(Constants.URL_PARAM_SERVERNAME)) {
												
												try {
													serverName = paramKeyValArr[1];
												} catch (Exception e) {
													// TODO Auto-generated catch block
													continue; 
												}
												
											}else if(paramKeyValArr[0].equalsIgnoreCase(Constants.URL_PARAM_EMAIL)) {
												
												try {
													email = paramKeyValArr[1];
												} catch (Exception e) {
													// TODO Auto-generated catch block
													continue; 
												}
											
											}else if(paramKeyValArr[0].equalsIgnoreCase(Constants.URL_PARAM_STATUS)) {
												
												try {
													statusCode = paramKeyValArr[1];
												} catch (Exception e) {
													// TODO Auto-generated catch block
													continue; 
												}
												
											}else if(paramKeyValArr[0].equalsIgnoreCase(Constants.URL_PARAM_TYPE)) {
												
												try {
													type = paramKeyValArr[1];
												} catch (Exception e) {
													// TODO Auto-generated catch block
													continue; 
												}
												
											}else if(paramKeyValArr[0].equalsIgnoreCase(Constants.URL_PARAM_REASON)) {
												
												try {
													reason = paramKeyValArr[1];
												} catch (Exception e) {
													// TODO Auto-generated catch block
													continue; 
												}
												
											}
											
										}
										
									}//for
									
									
									
								}//if
								
							}//if
							
							if(eventType == null  || crId == null || 
									email == null || userId == null) {
								logger.info("** Exception : Required parameter in query string not found.");
								continue;
							}
							
							 if( !eventType.equalsIgnoreCase(Constants.EXTERNAL_SMTP_EVENTS_TYPE_DROPPED) && 
									 !(eventType.equalsIgnoreCase(Constants.EXTERNAL_SMTP_EVENTS_TYPE_BOUNCE)) &&
									 !(eventType.equalsIgnoreCase(Constants.EXTERNAL_SMTP_EVENTS_TYPE_SPAMREPORT)) ) {
								 logger.error("Exception : event is not found :" + serverName);
								 continue;
							 }
							
							if(serverName==null || serverName.trim().isEmpty()) {
								if(logger.isErrorEnabled()) logger.error("Exception : ServerName is not found :" + serverName);
								continue;
							}
							
							if(serverName != null && serverName.contains("localhost:") ) {
								
								continue;
								
							}
							
							// Ignore if the server is not app.
							
							if(!serverName.equalsIgnoreCase(PropertyUtil.getPropertyValue("schedulerIp"))) {
								
								if(logger.isErrorEnabled()) logger.error("Exception : Differenct server, Redirecting to server :" + serverName);
								
								String redirecturl = serverName + "/Scheduler/sendGridEventHandler.mqrm";
								String postData = "?crId="+ crId + "&userId="+ userId + "&event=" + eventType + "&ServerName="+ serverName +
													(email != null && !email.isEmpty() ? "&email="+ email : "")+
													(statusCode != null && !statusCode.isEmpty() ? "&status="+ statusCode : "") +
													(type != null && !type.isEmpty() ? "&type="+ type : "") +
													(reason != null && !reason.isEmpty() ? "&reason=" +reason : "");
													
								logger.debug(" ****>>> Redirecting to : "+ redirecturl+postData );
								//open a connection to the new server(need to optimize it)
								try{
									
									URL url = new URL(redirecturl+postData);
									
									HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
									//logger.debug("CON OPENED ? "+urlconnection.getURL());
									urlconnection.setRequestMethod("POST");
									urlconnection.setRequestProperty("Content-Type","text/html");
									
									BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
									in.close();
									urlconnection.disconnect();
						        
							    } catch (MalformedURLException me) {
							        logger.error("MalformedURLException: " , me);
							        continue;
							    } catch (Exception e) {
							    	logger.error("IOException: " , e);
							        continue;
							    }
								continue;
								
							}
							
							//create an events object and insert it into DB
							ExternalSMTPEvents newEvent = new ExternalSMTPEvents(serverName, eventType, sentId, userId, crId, Calendar.getInstance(), email);
							newEvent.setStatusCode(statusCode);//TODO statusCode
							newEvent.setType(type);
							newEvent.setReason(reason);
							eventsToBeProcessed.add(newEvent);
							
							logger.debug("eventsList size ::"+eventsToBeProcessed.size());
							
							if(eventsToBeProcessed.size() >= 2000 ) {
								
								logger.debug("saving events to DB......");
								externalSMTPEventsDao.saveByCollection(eventsToBeProcessed);
								eventsToBeProcessed.clear();
							}//
							
						}//if
						
						if(count >= 2000){//to allow only 2000 to be pushed into DB
							
							break;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						
						continue;
						
					}

					
				}//while
*/				
				
				if(eventsToBeProcessed.size() > 0 ) {
					
					logger.debug("saving events to DB......");
					//externalSMTPEventsDao.saveByCollection(eventsToBeProcessed);
					externalSMTPEventsDaoForDML.saveByCollection(eventsToBeProcessed);
					eventsToBeProcessed.clear();
				}//
			}//if
			if(campaignReportEventsQueue.getQueueSize() > 0) { //it will not execute
				
				int count = 0;
				String[] paramArr = null;
				String requestUrlStr  = null;
				Long userId = null;
				Long sentId = null;
				String requestedAction = null;
				String url = null;
				Long cId = null;
				Long unSubId = null;
				String userAgent = null;
				
				int initQSize = campaignReportEventsQueue.getQueueSize();
				
				while((  requestUrlStr = campaignReportEventsQueue.getObjFromQueue()) != null ) {
					
					//logger.debug("requestUrl ::"+requestUrlStr);
					if(requestUrlStr.length() > 0) {
						
						++count ;
						
												//http://localhost:8080/Scheduler/sendGridEventHandler.mqrm?event=deferred&crId=23&sentId=123&userId=2
						//reference to the above example request url, we need to get the individual params to be processed.
						
						
						requestUrlStr = requestUrlStr.substring(requestUrlStr.indexOf(Constants.URL_TOKEN_QUESTIONMARK)+1);
						if(requestUrlStr.indexOf(Constants.URL_TOKEN_AMBERSENT ) != -1 ) {
							
							paramArr = requestUrlStr.split(Constants.URL_TOKEN_AMBERSENT);
							if(paramArr != null && paramArr.length > 0) {
								String[] paramKeyValArr = null;
								for (String param : paramArr) {
									if(param.indexOf(Constants.URL_TOKEN_EQUALTO) != -1) {
										
										paramKeyValArr = param.split(Constants.URL_TOKEN_EQUALTO);
										if(paramKeyValArr[0].equalsIgnoreCase(Constants.URL_PARAM_REQUESTED_ACTION)) {
											
											requestedAction = paramKeyValArr[1];
											
										}else if(paramKeyValArr[0].equalsIgnoreCase(Constants.URL_PARAM_SENTID)) {
											
											sentId = Long.parseLong(paramKeyValArr[1]);
											
										}else if(paramKeyValArr[0].equalsIgnoreCase(Constants.URL_PARAM_USERID)) {
											
											userId = Long.parseLong(paramKeyValArr[1]);
											
										}else if(paramKeyValArr[0].equalsIgnoreCase(Constants.URL_PARAM_CID)) {
											
											cId = Long.parseLong(paramKeyValArr[1]);
											
										}else if(paramKeyValArr[0].equalsIgnoreCase(Constants.URL_PARAM_URL)) {
											
											url = paramKeyValArr[1];
											
										}else if(paramKeyValArr[0].equalsIgnoreCase(Constants.URL_PARAM_UNSUBID)) {
											
											unSubId = Long.parseLong(paramKeyValArr[1]);
											
										}else if(paramKeyValArr[0].equalsIgnoreCase(Constants.QS_USERAGENT)) {
											
											userAgent = paramKeyValArr[1];
											
										}
										
									}
									
								}//for
								
							}//if params arr
							
						}//if contains &
						
						
						if(requestedAction == null ) {
			 				//logger.error("** Exception : Required parameter in query string not found.");
							continue;
						}
						
						//create an events object and insert it into DB
						
						/*
						 * ExternalSMTPEvents(String requestedAction,
						 * Long sentId, Long userId, Long cId, Long unSubId, String url, Calendar requestTime) {
						 */
						CampaignReportEvents newEvent = new CampaignReportEvents(requestedAction, sentId, userId,
								cId, unSubId, url, Calendar.getInstance(), userAgent );
						repEventsToBeProcessed.add(newEvent);
						
						
						if(repEventsToBeProcessed.size() >= 2000 ) {
							
							//logger.debug("saving events to DB......");
							//campaignReportEventsDao.saveByCollection(repEventsToBeProcessed);
							campaignReportEventsDaoForDML.saveByCollection(repEventsToBeProcessed);

							repEventsToBeProcessed.clear();
							
						}//if
						
				
					}
					
					
					if(count >= 2000){
						
						break;
					}

			}//while
			
				
				if(repEventsToBeProcessed.size() > 0 ) {
					
					//logger.debug("saving events to DB......");
					//campaignReportEventsDao.saveByCollection(repEventsToBeProcessed);
					campaignReportEventsDaoForDML.saveByCollection(repEventsToBeProcessed);

					repEventsToBeProcessed.clear();
				}//
				
			}
			
			
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
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			SuppressedContactsDao  suppressedContactsDao =(SuppressedContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SUPPRESSEDCONTACTS_DAO);
			SuppressedContactsDaoForDML  suppressedContactsDaoForDML =(SuppressedContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SUPPRESSEDCONTACTS_DAO_FOR_DML);
			SuppressedContacts suppressedContacts= null;
			String type = null;
			String conStatus = null;
			boolean addToSupp = false;
			if(eventType.equalsIgnoreCase(Constants.EXTERNAL_SMTP_EVENTS_TYPE_BOUNCE) && statusCode.startsWith("5") ){
				
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
				
				suppressedContacts = new SuppressedContacts();
				suppressedContacts.setEmail(emailId);
				suppressedContacts.setType(type);
				Users user = usersDao.findByUserId(userId);
				
				if(user != null)suppressedContacts.setUser(user);
				suppressedContacts.setSuppressedtime(Calendar.getInstance());
				suppressedContacts.setReason(reason);
				suppressedContactsDaoForDML.saveOrUpdate(suppressedContacts);
				
			}
			if(emailType.equals(Constants.EQ_TYPE_DIGITALRECIEPT))
				//update = drSentDao.updateStaus(sentId,eventType);
				update = drSentDaoForDML.updateStaus(sentId,eventType);
			else if(emailType.equals(Constants.COUP_GENT_CAMPAIGN_TYPE_SINGLE_EMAIL))
				//update = emailQueueDao.updateDeliveryStatus(sentId,eventType);
			update = emailQueueDaoForDML.updateDeliveryStatus(sentId,eventType);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("got exception**",e);
			return 0;
		}
		return update;
		
	}
	
	
}
