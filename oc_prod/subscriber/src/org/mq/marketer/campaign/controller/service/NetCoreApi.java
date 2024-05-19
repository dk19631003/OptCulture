package org.mq.marketer.campaign.controller.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;

/**
 * 
 * @author proumya
 *
 */
public class NetCoreApi {

	/*public static final String NETCORE_ERROR_MOBILENUMINVALID = "Mobile number invalid";
	public static final String NETCORE_ERROR_MSGTOUSERREJECTED_NDNC_STATUS_UNAVAILABLE = "NDNC status is unavailable";
	public static final String NETCORE_ERROR_MSGTOUSERREJECTED_NDNC_STATUS_REGISTERED = "User registered with NDNC";
	public static final String NETCORE_ERROR_MSGTOUSERREJECTED_NDNC_STATUS_UNKNOWN = "NDNC status unknown";
	public static final String NETCORE_ERROR_EMPTY_FEEDID_OR_MOBNUM = "FeedId or mobile number is empty";
	public static final String NETCORE_SUCCESS = "success";*/
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	/*public static final Map<String,String> errorCodesMap = new HashMap<String, String>();
	
	static {
		//TODO need to get username,password,feedid from DB
		errorCodesMap.put("108",NETCORE_ERROR_MOBILENUMINVALID);
		errorCodesMap.put("131",NETCORE_ERROR_MSGTOUSERREJECTED_NDNC_STATUS_UNAVAILABLE);
		errorCodesMap.put("132",NETCORE_ERROR_MSGTOUSERREJECTED_NDNC_STATUS_REGISTERED);
		errorCodesMap.put("136",NETCORE_ERROR_MSGTOUSERREJECTED_NDNC_STATUS_UNKNOWN);
		errorCodesMap.put("-1",NETCORE_ERROR_EMPTY_FEEDID_OR_MOBNUM);
		errorCodesMap.put("success",NETCORE_SUCCESS);
		
		
	}*/
	
	public NetCoreApi() {
		
		
	}
	
	/**this is example format to send an SMS to NetCore Api
	 * <!DOCTYPE REQ SYSTEM
	'http://bulkpush.mytoday.com//BulkSms/BulkSmsV1.00.dtd'>
	<REQ>
		<VER>1.0</VER>
		<USER>
		    <USERNAME>99670xxxxx</USERNAME>
		    <PASSWORD>abcde</PASSWORD>
	     </USER>
	     <ACCOUNT>
	    	<ID>38272</ID>
	    </ACCOUNT>
	    <MESSAGE>
	        <TEXT>hello hi</TEXT>
	        <TYPE>0</TYPE>
	        <MID>1</MID><!--we can set it as with SMSCampaignSentId--!>
	        <SMS FROM='' TO='919900840069' INDEX ='1'(this is the sequence number of the message unique to each contact)></SMS>
	    </MESSAGE>
	    <MESSAGE>
	         <TEXT>hello</TEXT>
	         <TYPE>0</TYPE>
	         <MID>2</MID>
	         <SMS FROM='' TO='919900840069' INDEX ='1'></SMS>
	    </MESSAGE>
    </REQ>

	 */
	private final String data = "<!DOCTYPE REQ SYSTEM 'http://bulkpush.mytoday.com//BulkSms/BulkSmsV1.00.dtd'>";
	
	private final String requestStructure = "<REQ> \n <VER>1.0</VER> \n <USER> \n <USERNAME><VAL_USER></USERNAME> \n"+
											"<PASSWORD><VAL_PWD></PASSWORD> \n </USER> \n <ACCOUNT> \n	<ID><VAL_ID></ID> \n " +
											"</ACCOUNT> \n"+"<MESSAGESTRUCTURE> \n"+"</REQ>";
	
	private final String messageStructure = "<MESSAGE> \n <TEXT><VAL_TXT></TEXT> \n <TYPE><VAL_TYPE></TYPE> \n <MID><VAL_MID></MID> \n"+
									         "<SMSSTRUCTURE> </MESSAGE> \n";
	
	private final String smsStructure = "<SMS FROM='<VAL_FROM>' TO='<VAL_TO>' INDEX ='<VAL_INDEX>'></SMS> \n";
	
	/**
	 * this method prepares XmlDocument to sent the SMS content, and sends it to NETCORE gateway
	 * @param messageContent
	 * @param msgType
	 * @param fromMobNum
	 * @param toMobNum
	 * @param msgSeq
	 * @throws Exception
	 */
	public void prepareData(String messageContent, String msgType,
			String fromMobNum, String toMobNum, String msgSeq, String senderId) throws Exception {
		
		//********* need to prepare the data into netCore understandable format(into an XML format)*********
		//send a request to send SMS
		//now from mobile number is not required
		String response = "";
		List<String> smsStructureList = new ArrayList<String>();
		msgType = "1";//for text message//y here hard coded like this means for other apis N or L are the message types.
		String sms = "";
		String mobile[] = null;
		String mobileNum = "";
		
		if(toMobNum.contains(",")){
			//******need to split the mobile numbers and send to each individual contact seperately********
			mobile = toMobNum.split(",");
			for (int i = 0; i < mobile.length; i++) {
				sms = smsStructure.replace("<VAL_TO>", mobile[i]).replace("<VAL_INDEX>",""+i).replace("<VAL_FROM>", senderId);
				smsStructureList.add(sms);
			}//for
			
		} else {
			sms = smsStructure.replace("<VAL_TO>", toMobNum).replace("<VAL_INDEX>",msgSeq).replace("<VAL_FROM>", senderId);
			smsStructureList.add(sms);
		}//else
		
		sms = "";
		
		for(String smsStruc : smsStructureList) {
			
			sms += smsStruc;
			
		}//for
		
		//prepare the complete xml document
		String tempMsg = messageStructure.replace("<VAL_TXT>", messageContent).replace("<VAL_TYPE>", msgType).
						replace("<VAL_MID>", "47").replace("<SMSSTRUCTURE>", sms);
		
		String tempReq = requestStructure.replace("<VAL_USER>", "9848495956").replace("<VAL_PWD>", "amgjm").
						replace("<VAL_ID>", "308138").replace("<MESSAGESTRUCTURE>", tempMsg);
		
		String tempData = data+tempReq;
		logger.debug("the data in XML format is====>"+tempData);
		String encode = URLEncoder.encode(tempData, "UTF-8");
		logger.debug("the encoded data is====>"+encode);
		
		sendSMS(tempData);
		
		
		
	}//prepareData
	
	/**
	 * this method sends the SMS to NETCORE Gateway 
	 * @param tempData
	 */
	public static void sendSMS(String tempData) {
		try {
			//TODO need to open a url connection
			//append the  request parameter UserRequest along with the values to the url
			//capture the response as in Xml format which can be parsed further and get the GUID and required things further
			String postData = "";
			String response = "";
			
			postData += "UserRequest="+URLEncoder.encode(tempData, "UTF-8");
			logger.debug("Data to be sent is=====>"+postData);
			
			URL url = new URL("http://bulkpush.mytoday.com/BulkSms/SendSms");
			
			HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
			
			urlconnection.setRequestMethod("POST");
			urlconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			urlconnection.setDoOutput(true);

			OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
			out.write(postData);
			out.flush();
			out.close();
			
			BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
			
			String decodedString;
			while ((decodedString = in.readLine()) != null) {
				response += decodedString;
			}
			in.close();
			logger.debug("response is======>"+response);
	
		}catch(Exception e) {
			
			logger.error("Exception while sendind the SMS",e);
		}
	}//sendSMS
	
	
}