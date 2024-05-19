/**
 * 
 */
package org.mq.marketer.campaign.controller.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.ServiceLocator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author vinod.bokare
 *
 */
public class InfobipSMSGateway {


	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private OCSMSGateway ocsmsGateway;
	private String userID ;
	private String pwd ;
	private final String urlStr = "http://api.infobip.com/api/v3/sendsms/plain?";
	private final String queryString  = "user=<USERNAME>&pass=<PWD>&sender=<SENDERID>&SMSText=<MESSAGE>&concat=<CONCAT>&GSM=<TO>";
	/**
	 * 
	 */
	public InfobipSMSGateway() {
		// TODO Auto-generated constructor stub
	}

	public InfobipSMSGateway(OCSMSGateway ocsmsGateway) {
		this.ocsmsGateway = ocsmsGateway;
	}


	

	
	
	public String sendOverHTTP(String UserID, String Password ,String content, String mobile,String senderId) {
		logger.debug(">>>>>>> Started InfobipSMSGateway :: sendOverHTTP <<<<<<< ");
		try {
			/*
			 * http://api.mVaayoo.com/mvaayooapi/MessageCompose?user=amith.lulla@optculture.com:amithl&senderID=OPTCLT&receipientno=9052346000&dcs=0&msgtxt=This is Test message&state=4 
			 */
			
			String postData = "";
			String data = URLEncoder.encode(content, "UTF-8"); 
			
			postData += this.queryString.replace("<USERNAME>", UserID ).replace("<PWD>",  URLEncoder.encode(Password, "UTF-8"))
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
			
			/**
			 * Prepare the response by processing the XML
			 */
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(response.getBytes("utf-8"))));;//dBuilder.parse(response);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("result");
			String msgId =Constants.STRING_NILL;
			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				//	System.out.println("\nCurrent Element :" + nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;


					msgId = eElement.getElementsByTagName("messageid").item(0).getTextContent();

				}
			}
			logger.info("msgId : " + msgId);
			
			logger.debug(">>>>>>> Started InfobipSMSGateway :: sendOverHTTP <<<<<<< ");
			return msgId;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::",e);
			return null;
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::",e);
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::",e);
			return null;
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception :::",e);
			return null;
		}
		
		
	}

	/**
	 * 
	 * @param senderId
	 * @param content
	 * @param mobile
	 */
	public void sendTestSMS(String UserId, String Password,String senderId, String content, String mobile) {
		logger.debug(">>>>>>> Started InfobipSMSGateway :: sendTestSMS <<<<<<< ");
		
		try {
			/*
			 * http://api.mVaayoo.com/mvaayooapi/MessageCompose?user=amith.lulla@optculture.com:amithl&senderID=OPTCLT&receipientno=9052346000&dcs=0&msgtxt=This is Test message&state=4 
			 */
			
			String postData = "";
			String data = URLEncoder.encode(content, "UTF-8"); 
			
			postData += this.queryString.replace("<USERNAME>", UserId ).replace("<PWD>",  URLEncoder.encode(Password, "UTF-8"))
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
		
		logger.debug(">>>>>>> Completed InfobipSMSGateway :: sendTestSMS <<<<<<< ");
	}

	/*public static void main(String[] args) {
		System.out.println("1");
		OCSMSGateway gateway = new OCSMSGateway();;
		gateway.setUserId("risservices");
		gateway.setPwd("Test2368");
		gateway.setPostpaidBalURL("http://api2.infobip.com/api/command");
		InfobipSMSGateway gateway2 = new InfobipSMSGateway();
		gateway2.getBalance(1, gateway);
		gateway2.sendTestSMS("risservices", "Test2368", "Infobip", "Hi vinod test sms from local", "918143958243");
	}*/
	/**
	 * Checks balance with the provider 
	 * @param totalCount
	 * @param ocsmsGateway
	 * @return true/false
	 */
	public boolean getBalance(int totalCount, OCSMSGateway ocsmsGateway) {
	logger.debug(">>>>>>> Started InfobipSMSGateway :: getBalance <<<<<<< ");
		String targetUrl = ocsmsGateway.getPostpaidBalURL()+"?username="+ocsmsGateway.getUserId().trim()+"&password="+ocsmsGateway.getPwd().trim()+"&cmd=CREDITS";
		logger.info("InfoBip Target Url ......"+targetUrl+"\t totalCount"+totalCount);
		//System.out.println("InfoBip Target Url ......"+targetUrl);
		try {
			URL url = new URL(targetUrl);
			HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();

			urlconnection.setRequestMethod("GET");
			urlconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			urlconnection.setDoOutput(true);

			BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));

			String decodedString = Constants.STRING_NILL;
			String response = Constants.STRING_NILL;
			while ((decodedString = in.readLine()) != null) {
				response += decodedString;
			}
			in.close();

			if(response == null) return false;

			double balance = 0.0;

			try{
				balance = Double.parseDouble(response);
			//	logger.info("AED Avaliable "+balance);
				/*balance = balance*(0.065 * 153846);
			//	logger.info("AED Avaliable After Conversion "+balance);*/
				balance = balance - (totalCount * 0.065);
			}
			catch(NumberFormatException e){
				logger.error("Infobip Error while processing balance ....",e);
			}
			boolean canProceed = true;

			if(balance <=  0.0){
				canProceed = false;
			}//if
			logger.debug("InfoBip Balance -----"+balance +"\t canProceed..:"+canProceed);

			if( !canProceed ) {

				try {

					String message = PropertyUtil.getPropertyValueFromDB(Constants.SMS_LOW_CREDITS_WARN_TEXT);
					String emailId = PropertyUtil.getPropertyValueFromDB("SupportEmailId");

					EmailQueue emailQueue = new EmailQueue("Ran out of SMS Credits-Infobip", message, 
				 			Constants.EQ_TYPE_LOW_SMS_CREDITS, "Active", emailId, Calendar.getInstance());
					EmailQueueDao emailQueueDao = null;
					EmailQueueDaoForDML emailQueueDaoForDML=null;
					try {
						emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName("emailQueueDao");
						emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("emailQueueDaoForDML");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						throw new BaseServiceException("InfoBip Exception while getting emailQueueDao");
					}

					//emailQueueDao.saveOrUpdate(emailQueue);
					emailQueueDaoForDML.saveOrUpdate(emailQueue);
				} catch(Exception e) {
					logger.error("InfoBip exception while saving email queue object");
					return canProceed;
				}//catch

			}//if
			logger.debug(">>>>>>> Completed InfobipSMSGateway :: getBalance <<<<<<< ");
			return canProceed;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error("InfoBip Exception", e);
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			logger.error("InfoBip Exception", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("InfoBip Exception", e);
		}
		logger.debug(">>>>>>> Completed InfobipSMSGateway :: getBalance <<<<<<< ");
		return false;
	}//getBalance
}//EOF
