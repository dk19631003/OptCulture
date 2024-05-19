package org.mq.captiway.scheduler.services;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.Utility;

	public class SMSCountryApi {

		private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
		public SMSCountryApi() {

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
		public static String sendSingleSMS(String mobilenumber, String message, String sid, String mtype) throws Exception {
			
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
			
			if(mtype.equalsIgnoreCase("L")) {
				message = Utility.stringToHex(message);
			}
			
			postData += "User=" + URLEncoder.encode(User,"UTF-8") + "&passwd=" + passwd + "&mobilenumber=" + 
				mobilenumber + "&message=" + URLEncoder.encode(message,"UTF-8") + "&sid=" + sid + "&mtype=" + mtype + "&DR=" + DR;
			
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

			if(logger.isDebugEnabled()) logger.debug(retval);
			if(logger.isDebugEnabled()) logger.debug("msgcontent is ======>"+message);
			return retval;
			
		} // sendSMS
		
		
		
		

} // class

