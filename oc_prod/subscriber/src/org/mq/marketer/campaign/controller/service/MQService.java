package org.mq.marketer.campaign.controller.service;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.MQSRequest;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.MQSRequestDao;
import org.mq.marketer.campaign.dao.MQSRequestDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.XMLReader;

public class MQService {

	Properties mqsProps = null;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	MQSRequestDao mqsRequestDao;
	MQSRequestDaoForDML mqsRequestDaoForDML;
	
	public MQService(MQSRequestDao mqsRequestDao) {
		loadProperties();
		this.mqsRequestDao = mqsRequestDao;
	}
	
	public void loadProperties(){
		try {
			mqsProps = new Properties();
			URL url = MQService.class.getClassLoader().getResource("mqservice.properties");
			mqsProps.load(url.openStream());
			logger.debug("Loading the mqs properties file successful");
		} catch (FileNotFoundException fnfe) {
			logger.error("*Exception : Properties file not found " + fnfe + " *");
		} catch (IOException ioe) {
			logger.error("*Exception : unable to load the properties file " + ioe + " *");
		} catch (Exception e) {
			logger.error("*Exception : Problem while loading the properties file " + e + " *");
		}
	}
	
	public HashMap<String, String> getSubscriptionValidity(String customerNo){
		String referenceNum = "" + (new Date()).getTime();
		String requestXML = "<![CDATA[<REQUESTINFO><KEY_NAMEVALUE><KEY_NAME>CUSTOMERNO</KEY_NAME><KEY_VALUE>" + customerNo + "</KEY_VALUE></KEY_NAMEVALUE></REQUESTINFO>]]>";
		logger.debug("Reguest XML : " + requestXML);
		//String encoded = StringEscapeUtils.escapeHtml(requestXML);
		String soapBody = "<tem:GetSubscriptionValidity>"
			+ "<tem:SubscriptionvalidityXML>" + requestXML + "</tem:SubscriptionvalidityXML>"
			+ "<tem:ReferenceNo>" + referenceNum + "</tem:ReferenceNo>"
			+ "</tem:GetSubscriptionValidity>";
		HashMap<String, String> respMap = doRequest(soapBody,referenceNum,"GetSubscriptionValidity");
		logger.debug("getSubscriptionValidity Resonce : " + respMap);
		return respMap;
	}
	
	public HashMap<String, String> getAvailableUsageLimit(String customerNo){
		String referenceNum = "" + (new Date()).getTime();
		String requestXML = "<![CDATA[<REQUESTINFO><KEY_NAMEVALUE><KEY_NAME>CUSTOMERNO</KEY_NAME><KEY_VALUE>" + customerNo + "</KEY_VALUE></KEY_NAMEVALUE><AVAILABLEUSAGELIMIT><SERVICEID></SERVICEID></AVAILABLEUSAGELIMIT></REQUESTINFO>]]>";
		logger.debug("Reguest XML : " + requestXML);
		//String encoded = StringEscapeUtils.escapeHtml(requestXML);
		String soapBody = "<tem:AvailableUsageLimit>"
			+ " <tem:AvailableUsageLimitXML>" + requestXML + "</tem:AvailableUsageLimitXML>"
			+ " <tem:ReferenceNo>" + referenceNum + "</tem:ReferenceNo>"
			+ "</tem:AvailableUsageLimit>";
		HashMap<String, String> respMap = doRequest(soapBody,referenceNum,"AvailableUsageLimit");
		logger.debug("getAvailableUsageLimit Resonce : " + respMap);
		return respMap;
	}

	public HashMap<String, String> processOrder(String customerNo,String serviceType,String itemName, String itemPrice){
		String referenceNum = "" + (new Date()).getTime();
		Date startDate = new Date();
		startDate.setDate(startDate.getDate()+1);
		Date endDate = startDate;
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String startTime = format.format(startDate);
		String endTime = format.format(endDate);
		String requestXML = "<![CDATA[<REQUESTINFO><KEY_NAMEVALUE><KEY_NAME>CUSTOMERNO</KEY_NAME><KEY_VALUE>" + customerNo + "</KEY_VALUE></KEY_NAMEVALUE><PROCESSORDER><SERVICETYPE>" + serviceType + "</SERVICETYPE><ISDEFINED>N</ISDEFINED><ISPROVISIONABLE>N</ISPROVISIONABLE><ITEMNAME>" + itemName + "</ITEMNAME><ITEMPRICE>" + itemPrice + "</ITEMPRICE><STARTTIME>" + startTime + "</STARTTIME><ENDTIME>" + endTime + "</ENDTIME></PROCESSORDER></REQUESTINFO>]]>";
		logger.debug("Reguest XML : " + requestXML);
		//String encoded = StringEscapeUtils.escapeHtml(requestXML);
		String soapBody = "<tem:ProcessOrder>"
			+ " <tem:ProcessORderXML>" + requestXML + "</tem:ProcessORderXML>"
			+ " <tem:ReferenceNo>" + referenceNum + "</tem:ReferenceNo>"
			+ "</tem:ProcessOrder>";
		HashMap<String, String> respMap = doRequest(soapBody,referenceNum,"ProcessOrder");
		logger.debug("processOrder Resonce : " + respMap);
		return respMap;
	}
	
	
	
	/**
	 * @param soapBody
	 * @param referenceNum
	 * @param service
	 * @return
	 */
	public HashMap<String, String> doRequest(String soapBody,String referenceNum, String service){
		String strURL = mqsProps.getProperty("MQSUrl").trim();//"http://ksatish/mqservice/MQService.asmx";
        String userId = mqsProps.getProperty("UserId").trim();//"SYSADMIN";
        String password = mqsProps.getProperty("Password").trim();// "SYSADMIN";
        String externalPartyName = mqsProps.getProperty("ExternalPartyName").trim();// "CAPTIWAY";
        HashMap<String, String> hashmapMessages = new HashMap<String, String>();
        PostMethod post = new PostMethod(strURL);
        //logger.debug("Request : " + encoded);
		 try {
			String request = "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:tem=\"http://tempuri.org/\">"
				 + "   <soapenv:Header>"
				 + "      <tem:MQUserNameToken>"
				 + "         <tem:User_id>" + userId + "</tem:User_id>"
				 + "         <tem:Password>" + password + "</tem:Password>"
				 + "         <tem:ExternalPartyName>" + externalPartyName + "</tem:ExternalPartyName>"
				 + "      </tem:MQUserNameToken>"
				 + "   </soapenv:Header>"
				 + "   <soapenv:Body>"
				 + soapBody
				 + "   </soapenv:Body>"
				 + "</soapenv:Envelope>";
			//logger.debug("Request : " + request);
			    post.setRequestEntity(new InputStreamRequestEntity(new ByteArrayInputStream(request.getBytes()), request.length()));
			    post.setRequestHeader("Content-type", "text/xml; charset=UTF-8");
			    HttpClient httpClient = new HttpClient();
			    try {
			        int result = httpClient.executeMethod(post);
			        //String response = post.getResponseBodyAsString();
			        String response = StringEscapeUtils.unescapeHtml(post.getResponseBodyAsString());
			        
			        if(response.indexOf("<RESPONSEINFO>")>0){
			        	String mqRes = response.substring(response.indexOf("<RESPONSEINFO>"), response.indexOf("</RESPONSEINFO>") + ("</RESPONSEINFO>").length());
			        	hashmapMessages = XMLReader.read(mqRes,"RESPONSEINFO");
			        }
			        String status = hashmapMessages.get("MESSAGE");
			        if(!status.equalsIgnoreCase("success"))
			        	status = status + "-" + hashmapMessages.get("ERRORNO");
			        MQSRequest mQSRequest = new MQSRequest(referenceNum, service, status, MyCalendar.getNewCalendar(),request,response);
			        try {
			        	mqsRequestDaoForDML.saveOrUpdate(mQSRequest);
			        	logger.debug("MQSRequest is stored with RefNum : " + referenceNum);
			        } catch (Exception e) {
			        	logger.error("** Exception : Problem while storing the MQS request with RefNum : " +referenceNum + " - " + e +" **");
			        }
			    }catch (Exception e) {
					logger.error("** Exception : " + e +" **");
				} finally {
			        post.releaseConnection();
			    }
		} catch (Exception e) {
			logger.error("** Exception : " + e +" **");
		}
		return hashmapMessages;
	}
}
