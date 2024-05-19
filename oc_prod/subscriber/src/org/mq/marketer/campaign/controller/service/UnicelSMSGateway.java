package org.mq.marketer.campaign.controller.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.SMSStatusCodes;

public class UnicelSMSGateway {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private String userID ;
	private String pwd ;
	private final String urlStr = "http://api.unicel.in/SendSMS/sendmsg.php";
	private final String queryString = "uname=<USERNAME>&pass=<PWD>&send=<SENDERID>&dest=<TO>&msg=<MESSAGE>&concat=<CONCAT>";
	public static Set<String> deliveredStatusSet ;
	public static Set<String> suppressedStatusSet;
	public static Map<String, String> unicellDlrMap ;
	public UnicelSMSGateway() {}
	
	public UnicelSMSGateway(String userID, String pwd) {
		
		this.userID = userID;
		this.pwd = pwd;
		
		
	}
	
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		
		this.pwd = pwd;
	}

	
	
	static {
		
		unicellDlrMap = new HashMap<String, String>();
		
		unicellDlrMap.put("-01",SMSStatusCodes.SMPP_DLR_STATUS_UNKNOWN ); 
		unicellDlrMap.put("000", SMSStatusCodes.SMPP_DLR_STATUS_QUEUED);
		unicellDlrMap.put("001", SMSStatusCodes.SMPP_DLR_STATUS_DELIVERED);
		unicellDlrMap.put("002", SMSStatusCodes.SMPP_DLR_STATUS_FAILED);
		unicellDlrMap.put("004",SMSStatusCodes.SMPP_DLR_STATUS_NDNC_FAILED );
		unicellDlrMap.put("005", SMSStatusCodes.SMPP_DLR_STATUS_BLACKLIST );
		unicellDlrMap.put("007", SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER);
		unicellDlrMap.put("006", SMSStatusCodes.SMPP_DLR_STATUS_WHITELIST);
		unicellDlrMap.put("008", SMSStatusCodes.SMPP_DLR_STATUS_PREPAID_REJECTED);
		unicellDlrMap.put("009", SMSStatusCodes.SMPP_DLR_STATUS_NIGHT_PROMO_BLOCKED);
		unicellDlrMap.put("031",SMSStatusCodes.SMPP_DLR_STATUS_ROAMING_REJECTED );
		unicellDlrMap.put("032", SMSStatusCodes.SMPP_DLR_STATUS_MS_OUTOFMEMORY);
		unicellDlrMap.put("033", SMSStatusCodes.SMPP_DLR_STATUS_NETWORK_FAILURE);
		unicellDlrMap.put("034", SMSStatusCodes.SMPP_DLR_STATUS_PROTOCOL_FAILURE);
		unicellDlrMap.put("035", SMSStatusCodes.SMPP_DLR_STATUS_PROTOCOL_FAILURE);
		unicellDlrMap.put("036", SMSStatusCodes.SMPP_DLR_STATUS_PROVIDER_FAILURE);
		unicellDlrMap.put("037", SMSStatusCodes.SMPP_DLR_STATUS_TOOMANY_MSGS);
		unicellDlrMap.put("044", SMSStatusCodes.SMPP_DLR_STATUS_PROMO_BLOCKED);
		
		//Unicel provided intermediate status
		unicellDlrMap.put("110", SMSStatusCodes.SMPP_DLR_STATUS_PENDING_ABSENT_SUBSCRIBER);
		unicellDlrMap.put("120", SMSStatusCodes.SMPP_DLR_STATUS_PENDING_OUTOFMEMORY);
		unicellDlrMap.put("130", SMSStatusCodes.SMPP_DLR_STATUS_PENDING_NW_FAIL);
		unicellDlrMap.put("140", SMSStatusCodes.SMPP_DLR_STATUS_PENDING_NW_TIMEOUT);
		unicellDlrMap.put("150", SMSStatusCodes.SMPP_DLR_STATUS_PENDING_SMS_TIMEOUT);
		unicellDlrMap.put("160", SMSStatusCodes.SMPP_DLR_STATUS_PENDING_HANDSET_BUSY);
		unicellDlrMap.put("190", "User Abort");
		unicellDlrMap.put("200", "Node not reachable");
		unicellDlrMap.put("210", "User Abort");
		
		deliveredStatusSet = new HashSet<String>();
		deliveredStatusSet.add(SMSStatusCodes.SMPP_DLR_STATUS_DELIVERED);
		deliveredStatusSet.add(SMSStatusCodes.SMPP_DLR_STATUS_QUEUED);
		deliveredStatusSet.add(SMSStatusCodes.SMPP_DLR_STATUS_TOOMANY_MSGS);
		deliveredStatusSet.add(SMSStatusCodes.SMPP_DLR_STATUS_PENDING_ABSENT_SUBSCRIBER);
		deliveredStatusSet.add(SMSStatusCodes.SMPP_DLR_STATUS_PENDING_OUTOFMEMORY);
		deliveredStatusSet.add(SMSStatusCodes.SMPP_DLR_STATUS_PENDING_NW_FAIL);
		deliveredStatusSet.add(SMSStatusCodes.SMPP_DLR_STATUS_PENDING_NW_TIMEOUT);
		deliveredStatusSet.add(SMSStatusCodes.SMPP_DLR_STATUS_PENDING_SMS_TIMEOUT);
		deliveredStatusSet.add(SMSStatusCodes.SMPP_DLR_STATUS_PENDING_HANDSET_BUSY);
		
		suppressedStatusSet = new HashSet<String>();
		suppressedStatusSet.add(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER);
		suppressedStatusSet.add(SMSStatusCodes.SMPP_DLR_STATUS_BLACKLIST);
		
		
	}

	public void test(String content, String mobile, String senderId ) {

		
		try {
			/*
			 * http://api.mVaayoo.com/mvaayooapi/MessageCompose?user=amith.lulla@optculture.com:amithl&senderID=OPTCLT&receipientno=9052346000&dcs=0&msgtxt=This is Test message&state=4 
			 */
			
			String postData = "";
			String data = URLEncoder.encode(content, "UTF-8"); 
			
			postData += this.queryString.replace("<USERNAME>", getUserID() ).replace("<PWD>",  URLEncoder.encode(getPwd(), "UTF-8"))
					.replace("<SENDERID>", senderId).replace("<TO>", mobile)
					.replace("<MESSAGE>", data).replace("<CONCAT>", content.trim().length() <= 160 ? "0" : "1");
	
			
			logger.debug("postData======>"+postData);
			
			URL url = new URL(this.urlStr);
			
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
			String response = "";
			while ((decodedString = in.readLine()) != null) {
				response += decodedString;
			}
			in.close();
			logger.debug("response is======>"+response);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::",e);
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::",e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::",e);
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception :::",e);
		}
		
		
	}
	
	
	public String sendOverHTTP(String content, String mobile, String senderId) {
		
		try {
			/*
			 * http://api.mVaayoo.com/mvaayooapi/MessageCompose?user=amith.lulla@optculture.com:amithl&senderID=OPTCLT&receipientno=9052346000&dcs=0&msgtxt=This is Test message&state=4 
			 */
			String postData = "";
			String data = URLEncoder.encode(content, "UTF-8"); 
			
			postData += this.queryString.replace("<USERNAME>", getUserID()).replace("<PWD>", URLEncoder.encode(getPwd(), "UTF-8"))
				     .replace("<SENDERID>", senderId).replace("<TO>", mobile)
				     .replace("<MESSAGE>", data).replace("<CONCAT>", content.trim().length() <= 160 ? "0" : "1");
	
			logger.debug("postData======>"+postData);
			
			URL url = new URL(this.urlStr);
			
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
			String response = "";
			while ((decodedString = in.readLine()) != null) {
				response += decodedString;
			}
			in.close();
			logger.debug("response is======>"+response);
			
			String msgID = response;
			return msgID;
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::", e);
			return null;
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::", e);
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::", e);
			return null;
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception :::", e);
			return null;
		}
		
	}
	
}