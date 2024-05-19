package org.mq.loyality.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UnicelSMSGateway {

	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);
	private String userID ;
	private String pwd ;
	private final String urlStr = "http://api.unicel.in/SendSMS/sendmsg.php";
	private final String queryString = "uname=<USERNAME>&pass=<PWD>&send=<SENDERID>&dest=<TO>&msg=<MESSAGE>&concat=<CONCAT>";
	
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
	/*public static void main(String[] args) {
		UnicelSMSGateway obj = new UnicelSMSGateway("magtrans","8&6hyg%6");
		obj.sendOverHTTP("OptCulture: Your loyalty program xyz is running low on inventory cards " +
				"(6 available). Add card-sets to continue enrolling into this program.", "8008265090", "OPTCLT");
	}*/
	
}