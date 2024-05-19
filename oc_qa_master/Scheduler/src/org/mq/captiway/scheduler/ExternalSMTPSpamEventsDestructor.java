package org.mq.captiway.scheduler;

import java.util.TimerTask;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.campaign.ExternalSMTPSpamEventsQueue;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.PropertyUtil;

public class ExternalSMTPSpamEventsDestructor extends TimerTask {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);


	/*private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		// TODO Auto-generated method stub
		this.applicationContext = applicationContext;
	}*/
	
	private ExternalSMTPSpamEventsQueue externalSMTPSpamEventsQueue ;
	
	
	public ExternalSMTPSpamEventsQueue getExternalSMTPSpamEventsQueue() {
		
		return externalSMTPSpamEventsQueue;
	}

	public void setExternalSMTPSpamEventsQueue(ExternalSMTPSpamEventsQueue externalSMTPSpamEventsQueue) {
		
		this.externalSMTPSpamEventsQueue = externalSMTPSpamEventsQueue;
		
	}
	 public static String SMTP_AUTH_USER;
	 public static String SMTP_AUTH_PWD;
	
	 private volatile boolean isRunning;
	 public boolean isRunning() {
	 	if(logger.isDebugEnabled()) logger.debug("isRunning ::"+isRunning);
	 	return isRunning;
	 }
	
	

	@Override
	public void run() {
		
		try {
			SMTP_AUTH_USER = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_MULTIMAIL_USER_ID);
			SMTP_AUTH_PWD = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_MULTIMAIL_USER_PWD);
			this.isRunning = true;
			if(externalSMTPSpamEventsQueue.getQueueSize() > 0) {
				
				String emailId = null;

				while((emailId = externalSMTPSpamEventsQueue.getObjFromQueue()) != null ) {
			
					
					try {
						//logger.info("======================isRunning for email id::     "+emailId);
						PostMethod post = new PostMethod("https://sendgrid.com/api/spamreports.delete.json");
										
					      post.addParameter("api_user", SMTP_AUTH_USER);
					      post.addParameter("api_key", SMTP_AUTH_PWD);
					      post.addParameter("email", emailId);
			
					      HttpClient httpClient = new HttpClient();
					      httpClient.executeMethod(post);
					      String responseStr = StringEscapeUtils.unescapeHtml(post.getResponseBodyAsString());
			
						// logger.info("response is======>"+responseStr);
						// JSONObject responseObj = new JSONObject(responseStr);
						// logger.info("Response is :: " +responseObj.getString("message"));
					
					}catch(Exception e) {
						logger.error("Exception while deleting contact.");
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception while deleting spam reports at SendGrid ", e);
			
		}finally{
			
			isRunning = false;
		}
	}
}