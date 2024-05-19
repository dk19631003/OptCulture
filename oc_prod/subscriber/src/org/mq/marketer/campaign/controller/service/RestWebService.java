package org.mq.marketer.campaign.controller.service;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;


public final class  RestWebService {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	/**
	 * @param args
	 */
	
	private static final String restfullURL =  "https://sendgrid.com/apiv2/"; //PropertyUtil.getPropertyValue("serverRestfulURL");  //
	
	public static String requestService(String serviceName, String queryData) {
		
		try {

			String urlStr =  restfullURL + serviceName;
			
            logger.debug("Service URL  = "+urlStr + queryData);			
            logger.debug("Inp XML Data = "+queryData );			
	           
			URL url = new URL(urlStr + queryData);
			
			HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();

			logger.info("URL :"+ urlconnection);
			urlconnection.setRequestMethod("POST");
		
			urlconnection.setRequestProperty("Content-Type","application/xml");
			/*urlconnection.setRequestProperty("USERNAME", "SYSADMIN");
			urlconnection.setRequestProperty("PASSWORD", "SYSADMIN");
			urlconnection.setRequestProperty("EXTERNALPARTY", "MQS");*/
			
			urlconnection.setDoOutput(true);
			urlconnection.setRequestProperty("Content-Length", ""+ queryData.getBytes().length);
	           
			urlconnection.setDoInput(true);
			
			/*DataOutputStream out = new DataOutputStream(urlconnection.getOutputStream()); 
			out.writeBytes(queryData);
			out.flush();
			out.close();*/
			
			InputStream io = urlconnection.getInputStream();
			
			BufferedReader in = new BufferedReader(	new InputStreamReader(io));
			
			String response="";
			String decodedString;
			while ((decodedString = in.readLine()) != null) {
				response += decodedString;
			}
			in.close();
			
			logger.debug("Response ="+response);
			return response;
		} catch (Exception e) {
			logger.error(" ** Exception:: Failed to get the Response: ", e);
			return null;
		}
	}
	
	
	public static void main(String[] args) {
		String tempStr = "?api_user=captiway&api_key=captiway123&username=newcustomer@example.com&" +
				"website=example.com&" +
				"password=samplepassword&confirm_password=samplepassword&" +
				"first_name=fname&last_name=lname&address=555%20anystreet&" +
				"city=any%20city&state=CA&zip=91234&email=newcustomer@example.com&" +
				"country=US&phone=555-555";
		String retrieveAllUsers = "?api_user=captiway&api_key=captiway123&task=get";
		//String retStr = requestService("reseller.add.json", tempStr);
		String retStr = requestService("customer.profile.json", retrieveAllUsers);
		logger.info("Resp: "+retStr);
	}

	
	public static String httpGet(String urlStr) throws IOException {
		  URL url = new URL(urlStr);
		  HttpURLConnection conn =
		      (HttpURLConnection) url.openConnection();

		  if (conn.getResponseCode() != 200) {
		    throw new IOException(conn.getResponseMessage());
		  }

		  // Buffer the result into a string
		  BufferedReader rd = new BufferedReader(
		      new InputStreamReader(conn.getInputStream()));
		  StringBuilder sb = new StringBuilder();
		  String line;
		  while ((line = rd.readLine()) != null) {
		    sb.append(line);
		  }
		  rd.close();

		  conn.disconnect();
		  return sb.toString();
		}
	
	
	public static String httpPost(String urlStr, String[] paramName,
			String[] paramVal) throws Exception {
			  URL url = new URL(urlStr);
			  HttpURLConnection conn =
			      (HttpURLConnection) url.openConnection();
			  conn.setRequestMethod("POST");
			  conn.setDoOutput(true);
			  conn.setDoInput(true);
			  conn.setUseCaches(false);
			  conn.setAllowUserInteraction(false);
			  conn.setRequestProperty("Content-Type",
			      "application/x-www-form-urlencoded");

			  // Create the form content
			  OutputStream out = conn.getOutputStream();
			  Writer writer = new OutputStreamWriter(out, "UTF-8");
			  for (int i = 0; i < paramName.length; i++) {
			    writer.write(paramName[i]);
			    writer.write("=");
			    writer.write(URLEncoder.encode(paramVal[i], "UTF-8"));
			    writer.write("&");
			  }
			  writer.close();
			  out.close();

			  if (conn.getResponseCode() != 200) {
			    throw new IOException(conn.getResponseMessage());
			  }

			  // Buffer the result into a string
			  BufferedReader rd = new BufferedReader(
			      new InputStreamReader(conn.getInputStream()));
			  StringBuilder sb = new StringBuilder();
			  String line;
			  while ((line = rd.readLine()) != null) {
			    sb.append(line);
			  }
			  rd.close();

			  conn.disconnect();
			  return sb.toString();
			}
	
}

