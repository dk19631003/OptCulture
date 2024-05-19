package org.mq.captiway.scheduler.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.mq.captiway.scheduler.WARecipientProvider;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.UserWAConfigs;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.beans.WACampaign;
import org.mq.captiway.scheduler.beans.WACampaignReport;
import org.mq.captiway.scheduler.beans.WACampaignSent;
import org.mq.captiway.scheduler.dao.UserWAConfigsDao;
import org.mq.captiway.scheduler.dao.WACampaignSentDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.ReplacePlaceHolders;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.springframework.context.ApplicationContext;

public class WAMultiThreadedSubmission extends Thread {


	private WARecipientProvider provider;
	private WACampaign waCampaign;
	private String textContent;
	private String templateProvider;
	private String apiEndPoint;
	private String accessToken;
	private String fromID;//channel ID
	private Set<String> totalPhSet;
	private WACampaignReport waCampaignReport;
	private ApplicationContext context;

	private UserWAConfigsDao userWAConfigsDao;
	private WACampaignSentDaoForDML waCampaignSentDaoForDML ;


	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER); 
	private int tempCount = 0;
	ReplacePlaceHolders replacePlaceHolders  = null;


	public WAMultiThreadedSubmission() {}

	public WAMultiThreadedSubmission(WARecipientProvider provider, Users user, WACampaign waCampaign,
			String textContent, String templateProvider, WACampaignReport waCampaignReport, Set<String> totalPhSet,
			ApplicationContext context,ReplacePlaceHolders replacePlaceHolders ) { 

		this.provider = provider;
		this.waCampaign = waCampaign;
		this.textContent = textContent;
		this.templateProvider = templateProvider;
		this.waCampaignReport = waCampaignReport;
		this.totalPhSet = totalPhSet;
		this.tempCount = 0;
		this.context = context;
		this.replacePlaceHolders = replacePlaceHolders;
	}


	public void run()  {

		Users user = waCampaign.getUsers();
		//String waCampaignName = waCampaign.getWaCampaignName();
		//String templateRegisteredId = waCampaign.getWaTemplateId();


		WACampaignSent waCampaignSent = null;
		String sentId = "";


		userWAConfigsDao = (UserWAConfigsDao)context.getBean("userWAConfigsDao");
		waCampaignSentDaoForDML = (WACampaignSentDaoForDML)context.getBean("waCampaignSentDaoForDML");


		boolean placeHolders = false;
		if(totalPhSet != null && totalPhSet.size() > 0) {
			placeHolders = true;
		}else{
			placeHolders = false;
		}

		UserWAConfigs userWAConfigs = null;
		try {
			userWAConfigs = userWAConfigsDao.findWAConfigsByProvider(user.getUserId().longValue(),this.templateProvider);
		} catch (Exception e) {
			logger.error("Exception ::",e);
		}
		
		if(userWAConfigs == null || userWAConfigs.getWaAPIEndPoint()==null || userWAConfigs.getWaAPIEndPoint().isEmpty()
								 || userWAConfigs.getAccessToken()==null || userWAConfigs.getAccessToken().isEmpty()
								 || userWAConfigs.getFromId()==null || userWAConfigs.getFromId().isEmpty() ) {
			logger.error("userWAConfigs is missed for the user :"+user.getUserName());
			return ;
		}
			
		this.apiEndPoint = userWAConfigs.getWaAPIEndPoint();
		this.accessToken = userWAConfigs.getAccessToken();
		this.fromID = userWAConfigs.getFromId();//channel ID

		Contacts contact = provider.getNext();//Getting the 10000 contacts from wa_tempcontacts
		if(contact == null ) {
			if(logger.isInfoEnabled()) logger.info("--------- No Recipients to send WA, hence WA Sender Thread exiting -------"+ Thread.currentThread().getName());

			return;
		}
		while(contact != null) {
			String mobileNumber = contact.getMobilePhone();


			waCampaignSent = (WACampaignSent)contact.getTempObj();
			if(waCampaignSent == null ) {
				if(logger.isInfoEnabled()) logger.info("--------- No sent obj is attached to this contact, hence WA Sender Thread exiting -------"+ Thread.currentThread().getName());
				return;
			}

			Long waCampRepId = waCampaignReport.getWaCrId();
			sentId = ""+waCampaignSent.getSentId();

			/*int lengthOfSentId = sentId.length();  
			for(int j=lengthOfSentId; j<8; j++){
			
				sentId = "0"+sentId;
			
			}*/

			if(logger.isInfoEnabled()) logger.info("sent id is=====>"+sentId+" in Thread :: "+Thread.currentThread().getName());
			if(logger.isInfoEnabled()) logger.info(mobileNumber+"  , "+contact.getEmailId());

			//contact.setTempObj(waCampaignSent.getSentId());

			//logger.info("contains placeHolders "+placeHolders);
			/**
			 * if WA contains place holders we submit individually
			 */
			if(placeHolders) { // if and else both are same for now as no replaceMethod developed
				
				logger.debug("contains placeHolders");

				String msgContentToBeSent = textContent;

				if(totalPhSet != null && totalPhSet.size() >0) {
		    		
					logger.debug("<<<<<<<<<<<<<<<calling replace place holders >>>>>>>>>>>>>>>>"+totalPhSet.size());
					
					msgContentToBeSent = replacePlaceHolders.getWAPlaceHolders(contact, totalPhSet, Constants.COUP_GENT_CAMPAIGN_TYPE_WA, 
							mobileNumber,user, waCampaignSent.getSentId(), provider, msgContentToBeSent);
		    		
		    	}
				
				//send the whatsapp msg
				sendWAOverHttp(msgContentToBeSent, mobileNumber, user, contact, waCampaignSent);
				
			} 
			else { // else follow group submission

				logger.debug("no placeholders");

				String msgContentToBeSent = textContent;
				
				//send the whatsapp msg
				sendWAOverHttp(msgContentToBeSent, mobileNumber, user, contact, waCampaignSent);
				

			} // else

			contact = provider.getNext();
			if(contact == null ) {
				if(logger.isInfoEnabled()) logger.info("--------- No more Recipients to send WA, hence WA Sender Thread exiting -------"+ Thread.currentThread().getName());

				break;
			}

		}

	}//run
	
	public void sendWAOverHttp(String msgContentToBeSent, String mobileNumber, Users user, Contacts contact, WACampaignSent waCampaignSent) {
		
		String mobile = Utility.phoneParse(mobileNumber.trim(), user.getUserOrganization());
		String countryCarrier = user.getCountryCarrier() + Constants.STRING_NILL;
		if (mobile.startsWith(countryCarrier) && mobile.length() > user.getUserOrganization().getMinNumberOfDigits()) {

			mobile = mobile.substring(countryCarrier.length());// country code stripped off as 91 already there in template

		}

		try {

			String messageTemplateReplaced = msgContentToBeSent.replace("[mobile]", mobile)
					.replace("[WAUserID]", this.fromID)
					.replace("[Name]", contact.getFirstName()!=null ? contact.getFirstName() : "Customer");
			
			try {
				if (this.apiEndPoint.equalsIgnoreCase(Constants.EndPoint_MsgBird)) { //only for MB,not for CM
					// append reportUrl to get sent campaign status
					JSONParser parser = new JSONParser();
					JSONObject jsonObj = (JSONObject) parser.parse(messageTemplateReplaced);
					jsonObj.put("reportUrl", PropertyUtil.getPropertyValue("WADLRURL")); // https://localhost:8080/Scheduler/updateWADLR.mqrm?sentId=[sentId];
					messageTemplateReplaced = jsonObj.toString().replace("[sentId]",
							waCampaignSent.getSentId().toString());
					/*
					{
						   "to": "+91XXXX",
						   "reportUrl" : "http://localhost:8080/Scheduler/updateWADLR.mqrm?sentId=[sentId]",
						   ...
					}
					*/
				}
				else if (this.apiEndPoint.equalsIgnoreCase(Constants.EndPoint_CM)) { //for CM
					// append reference to get delivery reports
					JSONParser parser = new JSONParser();
					JSONObject jsonMainObj = (JSONObject) parser.parse(messageTemplateReplaced);
					
					JSONObject jsonMsgObj = (JSONObject)jsonMainObj.get("messages");
					jsonMsgObj.put("reference", waCampaignSent.getSentId().toString()); // pass the sentID here to track the delivery reports
					
					jsonMainObj.replace("messages", jsonMsgObj);
					messageTemplateReplaced = jsonMainObj.toString();
					/*
					{
					    "messages": {
					        "reference":"sent_id",
					        ....
						}
					}
					*/
				}
				
			} catch (Exception e) {
				logger.error("Exception while appending reportUrl:",e);
			}
			
			logger.debug("message =="+messageTemplateReplaced);

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(this.apiEndPoint);
			
			StringEntity input = new StringEntity(messageTemplateReplaced,"UTF-8");
			
			postRequest.setEntity(input);
			postRequest.setHeader("Authorization", this.accessToken); //MB //fetching from userWAConfigs
			postRequest.setHeader("Content-Type", "application/json");
			postRequest.setHeader("apikey", this.accessToken);	//CM
			
			HttpResponse response = httpClient.execute(postRequest);
			//logger.info("mobile response ::"+response);
			
			int  statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 202 && statusCode !=200) {	//MB=202 CM=200
				logger.error("Failed : HTTP error code : " + statusCode);
				waCampaignSentDaoForDML.updateStatusBySentId(Constants.WA_STATUS_NotSubmitted, mobile, waCampaignSent.getSentId());//not submitted to msgBird
			}else {
				logger.info("Success : HTTP Success code : " + statusCode);
				waCampaignSentDaoForDML.updateStatusBySentId(Constants.WA_STATUS_Submitted, mobile, waCampaignSent.getSentId());//submitted to msgBird
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
			String output;
			String api_msg_id = "";
			StringBuffer totalOutput = new StringBuffer();
			try {
				while ((output = br.readLine()) != null) {
					totalOutput.append(output).append("::").append("statusCode:"+statusCode);
					JSONParser parser = new JSONParser();
					JSONObject jsonObj = (JSONObject) parser.parse(output);
					api_msg_id = (jsonObj!=null && jsonObj.get("id")!=null) ? jsonObj.get("id").toString() : ""; //only MB
				}
			} catch (Exception e) {
				
			}
			logger.info("mobile response ::"+totalOutput.toString());
			logger.info("API message ID : "+api_msg_id);
			
			if(api_msg_id!=null && !api_msg_id.isEmpty())
				waCampaignSentDaoForDML.updateApiMsgId(waCampaignSent.getSentId().toString(), waCampaignReport.getWaCrId(), api_msg_id);

		} catch (UnsupportedEncodingException e) {
			logger.error("Exception ",e);
		} catch (ClientProtocolException e) {
			logger.error("Exception ",e);
		} catch (IOException e) {
			logger.error("Exception ",e);
		}catch (Exception e) {
			logger.error("Exception ",e);
		}

		
	}//sendWAOverHttp



}
