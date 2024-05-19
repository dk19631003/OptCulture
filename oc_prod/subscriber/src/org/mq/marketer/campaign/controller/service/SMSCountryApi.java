package org.mq.marketer.campaign.controller.service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;

	public class SMSCountryApi {
		
		private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

		public SMSCountryApi() {

		}
		
		
		public static String sendSingleSMS(String mobilenumber, 
				String message, String sid, String mtype) throws Exception {
			return 	sendSingleSMS(mobilenumber, message, sid, mtype, true);
		}
		
		
		/*	
	    1	Create a URL. 
		2	Retrieve the URLConnection object. 
		3	Set output capability on the URLConnection. 
		4	Open a connection to the resource. 
		5	Get an output stream from the connection. 
		6	Write to the output stream. 
		7	Close the output stream.
		*/
		public static String sendSingleSMS(String mobilenumber, 
				String message, String sid, String mtype, boolean convertFlag) throws Exception {
			
			String postData="";
			String retval = "";

			//give all Parameters In String 
			String User ="captiway";
			String passwd = "Delight2Win";
			
/*			String mobilenumber = "Mno1,Mno2,,,,Mnon"; 
			String message = "SMS MEssage";
			String sid = "Sender_Id";
			String mtype = "N";
*/			
			String DR = "Y";		

			if(convertFlag && mtype.equalsIgnoreCase("L")) {
				message = Utility.stringToHex(message);
			}
			
			postData += "User=" + URLEncoder.encode(User,"UTF-8") + "&passwd=" + passwd + "&mobilenumber=" + 
				mobilenumber + "&message=" + URLEncoder.encode(message,"UTF-8") + "&sid=" + sid + "&mtype=" + mtype + "&DR=" + DR;
			
			logger.info("postData:: "+postData);
			
			URL url = new URL("http://sms.captiway.com/WebserviceSMS.aspX");
			
			HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();

			// If You Are Behind The Proxy Server Set IP And PORT else Comment Below 4 Lines
			//Properties sysProps = System.getProperties();
			//sysProps.put("proxySet", "true");
			//sysProps.put("proxyHost", "Proxy Ip");
			//sysProps.put("proxyPort", "PORT");

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
				retval += decodedString;
			}
			in.close();

			logger.info(retval);
			return retval;
			
		} // sendSMS
		
		
		
		
		/*public static String[] HexToOriginalString(String str) {
			int size = str.length()/4;
			String[] charTokensArr = new String[size];
			String buffer = new String();
			int j = 0;
			int k =0;
			char c ;
			for(int i=0; i<str.length(); i+=4){
				
				charTokensArr[j] = str.substring(i, i+4);
				++j;
			}*/
			/* for(int i=0; i<charTokensArr.length; i++){
				    k= Integer.parseInt(charTokensArr[i],16);
				    logger.info("Decimal:="+ k);
				    c = (char)k;
				    buffer = buffer+c;
			 }
			return buffer;
			*/
			/*return charTokensArr;
			
		}*/
	/*public static void main(String[] args) throws IOException{
			    BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
			    StringBuffer buffer = new StringBuffer();
			    logger.info("Enter the hexa number:");
			    String str= bf.readLine();
			    String[] charTokenArr = HexToOriginalString(str);
			    for(int i=0; i<charTokenArr.length; i++){
			    int k= Integer.parseInt(charTokenArr[i],16);
			    logger.info("Decimal:="+ k);
			    char c = (char)k;
			    buffer.append(c);
			   
			  }
		   logger.info("string buffer is====>"+buffer);
		 }*/
		 
} // class

