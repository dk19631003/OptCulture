package org.mq.marketer.campaign.controller.useradmin;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

public class BillingDetailsController extends GenericForwardComposer implements EventListener{
	
	private Users user;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Toolbarbutton profileIframeWin$profileIframeWinCloseTbBtnId;
	private Window  profileIframeWin;
	private Include profileIframeWin$myJspIncId;
	
	public static final String CIMMerchantLoginName = PropertyUtil.getPropertyValueFromDB(Constants.CIM_MERCHANT_LOGIN_NAME);
	public static final String CIMMerchantTransactionKey = PropertyUtil.getPropertyValueFromDB(Constants.CIM_MERCHANT_TRANSACTION_KEY);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		user = GetUser.getUserObj();
		 if(user.getCIMProfileId() != null) {
			 
	    	renderTheBillingAccount();
	    	
		 }
		    
		 profileIframeWin$profileIframeWinCloseTbBtnId.addEventListener("onClick", this);
		 
		 String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
	     	PageUtil.setHeader("Billing Details","",style,true);
	        
			
		
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
		
		Object obj = event.getTarget();
		if(obj instanceof Image) {
			
			Image img = (Image)obj;
			
			String type = (String)img.getAttribute("type");
			
			if(type.equals("cardDelete")) {
				int confirm = Messagebox.show("Confirm to delete the selected card.", "Confirm",
						Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm == Messagebox.OK) {
					try {
						Row row = (Row)img.getParent();
						String customerPaymentProfileId = row.getValue();

						String requestToDelete = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
												"<deleteCustomerPaymentProfileRequest xmlns=\"AnetApi/xml/v1/schema/AnetApiSchema.xsd\">" +
												"<merchantAuthentication>" +
												"<name>"+CIMMerchantLoginName+"</name>" +
												"<transactionKey>"+CIMMerchantTransactionKey+"</transactionKey>" +
												"</merchantAuthentication>" +
												"<customerProfileId>"+user.getCIMProfileId()+"</customerProfileId>" +
												"<customerPaymentProfileId>"+customerPaymentProfileId+"</customerPaymentProfileId>" +
												"</deleteCustomerPaymentProfileRequest>";
						
						URL url = new URL("https://api.authorize.net/xml/v1/request.api");
						
						HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
						
						urlconnection.setRequestMethod("POST");
						urlconnection.setRequestProperty("Content-Type","application/xml");
						urlconnection.setDoOutput(true);

						OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
						out.write(requestToDelete);
						out.flush();
						out.close();
						
						BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
						
						String decodedString;
						
						String response = "";
						
						while ((decodedString = in.readLine()) != null) {
							response += decodedString;
						}
						
						logger.debug("response is=============>"+response);
						
						if(parseForSuccessResponse(response)) {
							
							
							MessageUtil.setMessage("Card details deleted successfully.", "color:blue;");
							cardRowsId.removeChild(row);
						}
						else{
							
							MessageUtil.setMessage("Problem while deleting the Card details .", "color:red;");
							
						}
						
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ",e);
						MessageUtil.setMessage("Problem while deleting the Card", "color:red;");
						return;
						
					}
					
					
					
				
				}
				
				
			}
			
			
			
		}//if
		else if(obj instanceof Toolbarbutton) {
			
			Toolbarbutton tb = (Toolbarbutton)obj;
			if(tb.getId().equals(profileIframeWin$profileIframeWinCloseTbBtnId.getId())) {
				profileIframeWin.setVisible(false);
				event.stopPropagation();
				
				if(cardRowsId.getChildren() != null && cardRowsId.getChildren().size() > 0) {
					
					Components.removeAllChildren(cardRowsId);
					
				}//if
				
				
				if(user.getCIMProfileId() != null) {
					
					renderTheBillingAccount();
					
				}
			}
		}
		
		
	}
	
	

		public void onClick$requestForPopupAnchId() {
			
			//need to check whether the profile is there or not
			String CIMProfileID = user.getCIMProfileId();
			if(CIMProfileID == null) {
				
				MessageUtil.setMessage("No Billing profile exist for your account.\n Please contact / Email to "+
						PropertyUtil.getPropertyValueFromDB("SupportEmailId")+" to enable billing account.", "color:red;");
				return;
				
				
			}//if
			
			int confirm = Messagebox.show("Confirm to add payment profile.", "Confirm",
							Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			
			if(confirm == Messagebox.OK) {
				try {
					
					
					
					String token = getToken(user.getCIMProfileId());
					//logger.debug("token ====>"+token); -- APP-3914
					if(token == null) {
						
						MessageUtil.setMessage("Problem to process your request.", "color:red;");
						return;
						
					}//if
					
					/*HttpSession nativaSession = (HttpSession)session.getNativeSession();
					nativaSession.setAttribute("AUToken", token);*/
					profileIframeWin$myJspIncId.setSrc(null);
					profileIframeWin$myJspIncId.setSrc("/authorize_payment.jsp");
					
					profileIframeWin.doHighlighted();
					
					Clients.evalJavaScript("insertToken('"+token+"');");
					
					
				}catch (Exception e) {
					// TODO: handle exception
					
				}
			
			}
			
			
			
		}//onClick$requestForPopupAnchId
	
		public String getToken(String customerProfileID) {
			
			try {
				String tokenRequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
						"<getHostedProfilePageRequest xmlns=\"AnetApi/xml/v1/schema/AnetApiSchema.xsd\">" +
						"<merchantAuthentication>" +
						"<name>"+CIMMerchantLoginName+"</name>" +
						"<transactionKey>"+CIMMerchantTransactionKey+"</transactionKey>" +
						"</merchantAuthentication>" +
						"<customerProfileId>"+customerProfileID+"</customerProfileId>" +
						"</getHostedProfilePageRequest>";
				
			/*	
				String tokenRequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
										"<getHostedProfilePageRequest xmlns=\"AnetApi/xml/v1/schema/AnetApiSchema.xsd\">" +
										"<merchantAuthentication>" +
										"<name>6pd2KXR997</name>" +
										"<transactionKey>487L2j49Zb6xFmbA</transactionKey>" +
										"</merchantAuthentication>" +
										"<customerProfileId>80123060</customerProfileId>" +
										"</getHostedProfilePageRequest>";

			*/	
				URL url = new URL("https://api.authorize.net/xml/v1/request.api");
				
				logger.debug("tokenRe "+tokenRequest);
				
				HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
				
				urlconnection.setRequestMethod("POST");
				urlconnection.setRequestProperty("Content-Type","application/xml");
				urlconnection.setDoOutput(true);

				OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
				out.write(tokenRequest);
				out.flush();
				out.close();
				
				BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
				
				String decodedString;
				
				String response = "";
				while ((decodedString = in.readLine()) != null) {
					response += decodedString;
				}
				
				logger.debug("response for token ===>"+response);
				
				try {
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document doc = db.parse(new ByteArrayInputStream(response.getBytes()));
					doc.getDocumentElement().normalize();
					NodeList nodeLst = doc.getElementsByTagName("token");//given to each <MESSAGE> tag
					boolean found = false;
					for(int i=0; i<nodeLst.getLength(); i++) {
						
						
						Node node = nodeLst.item(i);
						Element element = (Element)node;
						return element.getTextContent();
					}
				} catch (DOMException e) {
					// TODO Auto-generated catch block
					logger.error("Exception ",e);
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					logger.error("Exception ",e);
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					logger.error("Exception ",e);
				}
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ",e);
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ",e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ",e);
			}
			
			return null;
			
			
		}

	
public boolean parseForSuccessResponse(String response) {
		
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
								return true;
							}
							else{
								return false;
							}
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
		
		return false;
		
		
		
	
		
		
		
		
		
	}//parseForSuccessResponse
	
	
	
public void renderTheBillingAccount() {
		
		try {
			
			String requestForPaymentProfile = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
												"<getCustomerProfileRequest xmlns=\"AnetApi/xml/v1/schema/AnetApiSchema.xsd\">" +
												"<merchantAuthentication>" +
												"<name>"+CIMMerchantLoginName+"</name>" +
												"<transactionKey>"+CIMMerchantTransactionKey+"</transactionKey>" +
												"</merchantAuthentication>" +
												"<customerProfileId>"+user.getCIMProfileId()+"</customerProfileId>" +
												"</getCustomerProfileRequest>";
			logger.debug("---just entered----"+requestForPaymentProfile);
			
			URL url = new URL("https://api.authorize.net/xml/v1/request.api");
			
			HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
			
			urlconnection.setRequestMethod("POST");
			urlconnection.setRequestProperty("Content-Type","application/xml");
			urlconnection.setDoOutput(true);

			OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
			out.write(requestForPaymentProfile);
			out.flush();
			out.close();
			
			BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
			
			String decodedString;
			
			String response = "";
			
			while ((decodedString = in.readLine()) != null) {
				response += decodedString;
			}
			
			logger.debug("response is=============>"+response);
			
			parseForRenderPaymentProfile(response);
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ",e);
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ",e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ",e);
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ",e);
		}
		
		
	}
	
	private Rows cardRowsId;
	public void parseForRenderPaymentProfile(String response) {
		
		/**
		 * ?xml version="1.0" encoding="utf-8"?>
		 * <getCustomerProfileResponse xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns="AnetApi/xml/v1/schema/AnetApiSchema.xsd">
		 * <messages><resultCode>Ok</resultCode><message><code>I00001</code><text>Successful.</text>
		 * </message></messages><profile><merchantCustomerId>amith__org__MQ</merchantCustomerId>
		 * <description>CIM account for user-amith__org__MQ</description><email>amith.lulla@magnaquest.com</email>
		 * <customerProfileId>80245066</customerProfileId>
		 * <paymentProfiles>
		 * <billTo><firstName>Amith</firstName><lastName>Lulla</lastName><company /><address /><city /><state /><zip /><country /><phoneNumber /><faxNumber /></billTo>
		 * <customerPaymentProfileId>75491520</customerPaymentProfileId>
		 * <payment><creditCard><cardNumber>XXXX1028</cardNumber><expirationDate>XXXX</expirationDate></creditCard></payment></paymentProfiles></profile></getCustomerProfileResponse>
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
			NodeList paymentnodeLst = doc.getElementsByTagName("paymentProfiles");
			if(paymentnodeLst == null || paymentnodeLst.getLength() == 0) return;
			
			
			for(int i=0; i<paymentnodeLst.getLength(); i++) {
				
				Row row = new Row();
				row.setParent(cardRowsId);
				node = paymentnodeLst.item(i);
				element = (Element)node;
				if(element.hasChildNodes()) {
					
					Node billToNode = element.getFirstChild();
					if(billToNode.getNodeName().equals("billTo")) {
						
						
						Element billToElement = (Element)billToNode;
						String cardholderName  = billToElement.getFirstChild().getTextContent()+" "+billToElement.getChildNodes().item(1).getTextContent();
						row.appendChild(new Label(cardholderName));
						
						
					}//if
					else {
						
						row.appendChild(new Label("--"));
					}
					
					row.setValue(element.getElementsByTagName("customerPaymentProfileId").item(0).getTextContent());
					
					Node paymentNode = element.getElementsByTagName("payment").item(0);
					Element paymentElement = (Element)paymentNode;
					
					Node cardNode = paymentElement.getElementsByTagName("creditCard").item(0);
					Element cardElement = (Element)cardNode;
					
					row.appendChild(new Label(cardElement.getFirstChild().getTextContent()));
					
					
					Image delImg = new Image("/img/action_delete.gif");
					delImg.setTooltiptext("Delete");
					delImg.setStyle("cursor:pointer;");
					delImg.addEventListener("onClick", this);
					delImg.setAttribute("type", "cardDelete");
					delImg.setParent(row);
					
					
				}//if
				
				
				
			}
			
			
			
			/*NodeList nodeLst = doc.getElementsByTagName("billTo");//given to each <MESSAGE> tag
			boolean found = false;
			if(nodeLst != null && nodeLst.getLength() > 0) {
				
				for(int i=0; i<nodeLst.getLength(); i++) {
					if(!found ) {
					node = nodeLst.item(i);
					element = (Element)node;
					String cardholderName  = element.getFirstChild().getTextContent()+" "+element.getChildNodes().item(1).getTextContent();
					row.appendChild(new Label(cardholderName));
					found = true;
					}
					
				}
				
			}else {
				
				row.appendChild(new Label("--"));
			}
			
			
			nodeLst = doc.getElementsByTagName("cardNumber");
			found = false;
			if(nodeLst != null && nodeLst.getLength() > 0) {
				for(int i=0; i<nodeLst.getLength(); i++) {
				
					if(!found ) {
						node = nodeLst.item(i);
						element = (Element)node;
						row.appendChild(new Label(element.getTextContent()));
						found = true;
					}
				}
				//guid = element.getAttribute("GUID");
				//sentId = element.getAttribute("ID");//sent id in our SMSCampaignSent
				
				//logger.info("sent id is===>"+sentId+"node is====>"+node.toString());
				
			}
			
			nodeLst = doc.getElementsByTagName("customerPaymentProfileId");
			found = false;
			if(nodeLst != null && nodeLst.getLength() > 0) {
				for(int i=0; i<nodeLst.getLength(); i++) {
				
					if(!found ) {
						node = nodeLst.item(i);
						element = (Element)node;
						row.setValue(element.getTextContent());
						found = true;
					}
				}
				//guid = element.getAttribute("GUID");
				//sentId = element.getAttribute("ID");//sent id in our SMSCampaignSent
				
				//logger.info("sent id is===>"+sentId+"node is====>"+node.toString());
				
			}
			*/
			
			
			
			
			
						
		
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ",e);
		}
		
		
		
		
		
	}//parseForRenderPaymentProfile
	
	
	
	
}
