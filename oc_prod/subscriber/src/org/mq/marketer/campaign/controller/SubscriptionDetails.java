package org.mq.marketer.campaign.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.service.MQService;
import org.mq.marketer.campaign.dao.MQSRequestDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.zkplus.spring.SpringUtil;


public class SubscriptionDetails {
	
	private HashMap<String, String> subscriptionValidityMap = null;
	private HashMap<String, String> availableUsageLimitMap = null;
	MQService mqs;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public String userName = null;
	public Users user = null;
	public boolean useMQS =  false;
	public String custNo = "";
	public SubscriptionDetails(){
		MQSRequestDao mqsRequestDao = (MQSRequestDao)SpringUtil.getBean("mqsRequestDao");
		this.mqs = new MQService(mqsRequestDao);
		this.user = GetUser.getUserObj();
		this.userName = GetUser.getUserName();	
		this.custNo = user.getMqsId(); 
		this.useMQS = useMQS();
	}
	

	public void getMQSSubscriptionValidity(){
		logger.debug("Customer Number : " + custNo);
		subscriptionValidityMap = mqs.getSubscriptionValidity(custNo);
	}
	
	public void getMQSAvailableUsageLimit(){
		logger.debug("Customer Number : " + custNo);
		availableUsageLimitMap = mqs.getAvailableUsageLimit(custNo);
	}
	
	public boolean useMQS(){
		String useMQS = PropertyUtil.getPropertyValueFromDB("useMQS");
		if(useMQS!=null){
			if(useMQS.equalsIgnoreCase("true"))
				return true;
			else
				return false;
		}
		return false;
	}
	
	
	/**
	 * returns the contract status of the user by getting the details from getSubscriptionValidity web service of MQS
	 */
	public String checkActiveStatus(){
		if(useMQS){
			String status = null;
			if(subscriptionValidityMap==null){
				getMQSSubscriptionValidity();
			}
			if(subscriptionValidityMap!=null){
				if(subscriptionValidityMap.size()>0){
					status = subscriptionValidityMap.get("CONTRACTSTATUS");
				}
			}
			return status;
		}
		return "Active";
	}
	
	/**
	 * returns number of validity days remained of a user by getting the details from getSubscriptionValidity web service of MQS
	 * default returns 0
	 */
	public long checkDateCount(){
		if(useMQS){
			long days = 0l;
			try {
				if(subscriptionValidityMap == null){
					getMQSSubscriptionValidity();
				}
				String endDateStr = subscriptionValidityMap.get("ENDDATE");
				if(endDateStr!=null){
					DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
					Date endDate = format.parse(endDateStr);
					days = Utility.DateDiff(endDate, new Date());
				}
			} catch (ParseException e) {
				logger.error("* Exception : Problem while getting the days remaining - " + e);
			}
			return days;
		}
		return 10;
	}
	
	public int getEmailStatus(Calendar myCalendar){
		int balance = 0;
		if(useMQS){
			long days = checkDateCount();
			if(days<=0){
				return -1;
			}
			if(availableUsageLimitMap==null){	
				getMQSAvailableUsageLimit();
			}
			if(availableUsageLimitMap!=null){	
				if(availableUsageLimitMap.size()>0){	
					String balStr = availableUsageLimitMap.get("AVAILABLEBALANCE");  
					if(balStr!=null){
						try{
							balance = Integer.parseInt(balStr);
							logger.debug(userName + " Availabel usage limit : " + balance);
						}catch (Exception e) {
							logger.error("*Exception : unable to convert string(" + balStr + ") to long - " + e);
						}
					}
				}
			}
			return balance;
		} 
		else {
			if(user.getEmailCount() == null || user.getPackageExpiryDate() == null || user.getPackageExpiryDate().before(myCalendar))
				return -1;
			
			return user.getEmailCount() - ( user.getUsedEmailCount() == null ? 0 : user.getUsedEmailCount());
		}
	}
	
	
	public int getSMSStatus(Calendar myCalendar) {
		int balance = 0;
		if(useMQS){
			long days = checkDateCount();
			if(days<=0){
				return -1;
			}
			if(availableUsageLimitMap==null){	
				getMQSAvailableUsageLimit();
			}
			if(availableUsageLimitMap!=null){	
				if(availableUsageLimitMap.size()>0){	
					String balStr = availableUsageLimitMap.get("AVAILABLEBALANCE");  
					if(balStr!=null){
						try{
							balance = Integer.parseInt(balStr);
							logger.debug(userName + " Availabel usage limit : " + balance);
						}catch (Exception e) {
							logger.error("*Exception : unable to convert string(" + balStr + ") to long - " + e);
						}
					}
				}
			}
			return balance;
		} 
		else {
			if(user.getSmsCount() == null || user.getPackageExpiryDate() == null || user.getPackageExpiryDate().before(myCalendar))
				return -1;
			
			return user.getSmsCount() - ( user.getUsedSmsCount() == null ? 0 : user.getUsedSmsCount());
		}
	}

	public boolean processOrder(String itemName, String count){
		if(useMQS){
			try {
				if(availableUsageLimitMap==null){	
					getMQSAvailableUsageLimit();
				}
				if(availableUsageLimitMap!=null){	
					String serviceName = availableUsageLimitMap.get("SERVICENAME");
					if(serviceName!=null){
						HashMap<String, String> poMap = mqs.processOrder(custNo, serviceName, itemName, count);
						if(poMap.get("ERRORNO").equalsIgnoreCase("0")){
							logger.debug("Update Email count is successful for " + itemName);
						}else
							logger.error("Update Email count is failed for " + itemName);
					}
				}
				return true;
			} catch (Exception e) {
				logger.error(" ** Exception : problem while doing processOrder "+ e + " **");
				return false;
			}
		}
		return true;
	}
	
}
