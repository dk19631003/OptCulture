package org.mq.marketer.campaign.controller.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;

public class MVaayooHTTPSample {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private String userId;
	private String accountPwd;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAccountPwd() {
		return accountPwd;
	}

	public void setAccountPwd(String accountPwd) {
		this.accountPwd = accountPwd;
	}
	
	public MVaayooHTTPSample() {}
	
	/*public MVaayooHTTPSample(boolean isTransactional) {
		
		if(isTransactional) {
			
			userId = "amith.lulla@optculture.com";
			accountPwd = "amithl";
		}else {
			
			userId = "mallika.naidu@optculture.com";
			accountPwd = "mallika";
			
		}
		
		
		
	}*/
	public MVaayooHTTPSample(String userID, String pwd) {
		
			
			userId = userID;
			accountPwd = pwd;
			
		
		
		
	}
	
	
	public static void main(String[] args) {
		
		try {
			/*
			 * http://api.mVaayoo.com/mvaayooapi/MessageCompose?user=amith.lulla@optculture.com:amithl&senderID=OPTCLT&receipientno=9052346000&dcs=0&msgtxt=This is Test message&state=4 
			 */
			
			String postData = "";
			
			postData += "user=amith.lulla@optculture.com:amithl&senderID=OPTCLT&receipientno=9052221449,9490927928&dcs=0&msgtxt=Thanks for shopping with Tresmode&state=4";
			
			URL url = new URL("http://api.mVaayoo.com/mvaayooapi/MessageCompose");
			
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
			logger.error("Exception ::" , e);
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ::" , e);
		}
		
		
		
		
		
	}
	
	public String test(String content, String mobile, String senderId) {

		
		String response = "";
		try {
			/*
			 * http://api.mVaayoo.com/mvaayooapi/MessageCompose?user=amith.lulla@optculture.com:amithl&senderID=OPTCLT&receipientno=9052346000&dcs=0&msgtxt=This is Test message&state=4 
			 */
			String data = URLEncoder.encode(content, "UTF-8"); 
			
			String urlData = "user="+getUserId()+":"+getAccountPwd()+ (senderId != null ? "&senderID="+senderId : "")+"&dcs=0&msgtxt="+data+"&state=4&receipientno=";
			String postData = "";
			if(mobile.contains(Constants.DELIMETER_COMMA)) {
				String[] mobileTokenArr = mobile.split(Constants.DELIMETER_COMMA);
				String mobileStr = Constants.STRING_NILL;
				for (String mobileToken : mobileTokenArr) {
					
					if(!mobileStr.isEmpty()) mobileStr += Constants.DELIMETER_COMMA;
					
					mobileStr += mobileToken;
					
					if((urlData+mobileStr).length() <= 1024) {
						
						postData = urlData + mobileStr;
						continue;
					}
					
					
					response = sendToGateWay(postData);
					mobileStr = mobileToken;
					
				}//for
				
				if(!postData.isEmpty()) {
					
					response = sendToGateWay(postData);
				}
				
			}//if
			else {
				
				postData =  urlData + mobile;
				response = sendToGateWay(postData);
				
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		return response;
	}
	
	public String sendToGateWay(String postData){
		String response = "";
		try{
			URL url = new URL("http://api.mVaayoo.com/mvaayooapi/MessageCompose");
			
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
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ::" , e);
		}
		return response;
		
	}
	
	
}
