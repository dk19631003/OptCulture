package org.mq.optculture.business.helper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.useradmin.BillingDetailsController;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.optculture.utils.ServiceLocator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class UserHelper {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private UsersDao usersDao;
	private UsersDaoForDML usersDaoForDML;
	


	
	public  void createBillingProfile (Users user) {
		
		
		
			try {
				usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName("usersDao");
				usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("usersDaoForDML");




		if(user.getCIMProfileId() != null) {
						
						MessageUtil.setMessage("User already has CIM account.", "color:blue;");
						return ;
						
		}//
		
		
		String createCutomerProfile = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
												"<createCustomerProfileRequest xmlns=\"AnetApi/xml/v1/schema/AnetApiSchema.xsd\">" +
												"<merchantAuthentication>" +
												"<name>"+BillingDetailsController.CIMMerchantLoginName+"</name>" +
												"<transactionKey>"+BillingDetailsController.CIMMerchantTransactionKey+"</transactionKey>" +
												"</merchantAuthentication>" +
												"<profile>" +
												"<merchantCustomerId>"+user.getUserId().longValue()+"</merchantCustomerId>" +
												"<description>"+user.getUserName()+"</description>" +
												"<email>"+user.getEmailId()+"</email>" +
												/*"<paymentProfiles>"+
												"<billTo>" +
												"<firstName>" +user.getFirstName() +"</firstName>"+
												"<lastName>" +user.getLastName() +"</lastName>"+
												"<company>" +user.getCompanyName()+"</company>"+
												"<address>" +user.getAddressOne()+"</address>" +
												"<city>" +user.getCity()+"</city>" +
												"<state>" +user.getState()+"</state>" +
												"<zip>" +user.getPinCode()+"</zip>" +
												"<country>" +user.getCountry()+"</country>" +
												"<phoneNumber>" +user.getPhone()+"</phoneNumber>" +
												"</billTo>"+
												"</paymentProfiles>"+*/
												"</profile>" +
												"</createCustomerProfileRequest>";
		
		
		
		
		try {
						URL url = new URL("https://api.authorize.net/xml/v1/request.api");
						
						HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
						
						urlconnection.setRequestMethod("POST");
						urlconnection.setRequestProperty("Content-Type","application/xml");
						urlconnection.setDoOutput(true);
		
						OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
						out.write(createCutomerProfile);
						out.flush();
						out.close();
						
						BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
						
						String decodedString;
						
						String response = "";
						while ((decodedString = in.readLine()) != null) {
							response += decodedString;
						}
						
						logger.debug("response =============>"+response);
		String value = parseForSuccessResponse(response);
		
		if(!value.contains(Constants.ADDR_COL_DELIMETER) ) {
						
				MessageUtil.setMessage("CIM profile for user : '"+user.getUserName()+"' created successfully.", "color:blue;");
				user.setCIMProfileId(value);
				//usersDao.saveOrUpdate(user);
				usersDaoForDML.saveOrUpdate(user);
				
		}else {
				
				MessageUtil.setMessage("Poblem while creating CIM profile for user : '"+user.getUserName()+"' " +
						"\n errorcode :"+value.split(Constants.ADDR_COL_DELIMETER)[0] +" \n message :"+value.split(Constants.ADDR_COL_DELIMETER)[1] , "color:red;");
				
				
		}
						
		} catch (MalformedURLException e) {
			logger.error("Exception ",e);
		} catch (ProtocolException e) {
			logger.error("Exception ",e);
		} catch (IOException e) {
			logger.error("Exception ",e);
		}catch (Exception e) {
			// TODO: handle exception
			MessageUtil.setMessage("Poblem while creating CIM profile for user : '"+user.getUserName()+"' " , "color:red;");
						
		}
		} catch (Exception e) {
			logger.error("Exception ",e);
		}
		
		
	
		
	}
	
	
public String parseForSuccessResponse(String response) {
		
		/**
		 * ﻿<?xml version="1.0" encoding="utf-8"?>
		 * <createCustomerProfileResponse xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema"
		 *  xmlns="AnetApi/xml/v1/schema/AnetApiSchema.xsd">
		 *  <messages>
		 *  <resultCode>Ok</resultCode>
		 *  <message><code>I00001</code>
		 *  <text>Successful.</text></message></messages>
		 *  <customerProfileId>80244827</customerProfileId>
		 *  <customerPaymentProfileIdList /><customerShippingAddressIdList /><validationDirectResponseList /></createCustomerProfileResponse>
		 */

		
		Node node = null;
		Node childNode = null;
		Element element = null;
		
		String code = "";
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(response.getBytes()));
			doc.getDocumentElement().normalize();
			
			Element docElement = doc.getDocumentElement();
			//<resultCode>Ok</resultCode>

			NodeList nodeLst = doc.getElementsByTagName("messages");//given to each <MESSAGE> tag
			for(int i=0; i<nodeLst.getLength(); i++) {
				
				
				node = nodeLst.item(i);
				element = (Element)node;
				//guid = element.getAttribute("GUID");
				//sentId = element.getAttribute("ID");//sent id in our SMSCampaignSent
				
				//logger.info("sent id is===>"+sentId+"node is====>"+node.toString());
				
				if(element.hasChildNodes()) {
					
					
					NodeList childNodeList = element.getChildNodes();
					for(int j=0; j<childNodeList.getLength(); j++) {
						
						
						childNode = childNodeList.item(j);
						//logger.debug("my childNode is=====>"+childNode.getNodeName());
						
						if(childNode.getNodeName().equals("resultCode")) {
							
							if( childNode.getTextContent().trim().equalsIgnoreCase("OK")){
								NodeList profileNodes = doc.getElementsByTagName("customerProfileId");
								if(profileNodes != null && profileNodes.getLength() > 0) {
									for(int p=0; p<profileNodes.getLength(); p++) {
										
										
										node = profileNodes.item(p);
										element = (Element)node;
										
										return element.getTextContent();
										
									}
								}
							}
						}
				
						if(childNode.getNodeName().equals("message")) { //it is error
							
							return (childNode.getFirstChild().getTextContent() +Constants.ADDR_COL_DELIMETER+childNode.getLastChild().getTextContent());
							
						}
							
							
					}
				}
					
			}		
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ",e);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ",e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ",e);
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ",e);
		}
		
		return code;
		
		
	}//parseForSuccessResponse
}
