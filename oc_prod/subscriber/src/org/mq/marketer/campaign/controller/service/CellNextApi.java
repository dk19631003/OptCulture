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
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CellNextApi {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public static final String CELLNEXT_ERROR_DESTNOTNUMERIC = "destination number not numeric";
	public static final String CELLNEXT_ERROR_TEXTEMPTY = "SMS text is empty";
	public static final String CELLNEXT_ERROR_DESTNUMEMPTY = "Destination number empty";
	public static final String CELLNEXT_ERROR_MSGFAIL = "Message failed";
	public static final String CELLNEXT_SUCCESS = "Message delivered successfully";
	public static final String CELLNEXT_ERROR_INVALIDMSG = "Invalid message. Submit failed";
	
	
	private String userName,password;
	//private String userSMSTool,response;
	
	
	public static final Map<String, String> errorcodesMap = new HashMap<String, String>();
	
	static{
		//TODO need to get the userId and password for each SMS API from DB
		errorcodesMap.put("8449", CELLNEXT_ERROR_MSGFAIL);
		errorcodesMap.put("8448", CELLNEXT_SUCCESS);
		errorcodesMap.put("28673", CELLNEXT_ERROR_DESTNOTNUMERIC);
		errorcodesMap.put("28674", CELLNEXT_ERROR_DESTNUMEMPTY);
		errorcodesMap.put("28679", CELLNEXT_ERROR_TEXTEMPTY);
		errorcodesMap.put("28681", CELLNEXT_ERROR_INVALIDMSG);
		
		
	}
	
	/**this is the example XML format to send a SMS to cellnext API
	 * <?xml version="1.0" encoding="ISO-8859-1"?>
	<!DOCTYPE MESSAGE SYSTEM "http://127.0.0.1/psms/dtd/message.dtd" >
	<MESSAGE>
	     <USER USERNAME="mycompany" PASSWORD="mycompany"/>
	     <SMS UDH="0" CODING="1" TEXT="hey this is a real test" PROPERTY="" ID="1" DLR="0" VALIDITY="0">
	            <ADDRESS FROM="9812345678" TO="919812345678" SEQ="1" TAG="some client side random data" />
	            <ADDRESS FROM="9812345678" TO="mycompany" SEQ="2" />
	            <ADDRESS FROM="CELLNEXT"     TO="919812345678" SEQ="3" />
	     </SMS>
	     <SMS  UDH="0" CODING="1" TEXT="hey this is a real test" PROPERTY="" ID="2" SEND_ON="2007-10-15 20:10:10 +0530">
	            <ADDRESS FROM="9812345678" TO="919812345678" SEQ="1" />
	            <ADDRESS FROM="9812345678" TO="919812345678" SEQ="2" />
	            <ADDRESS FROM="CELLNEXT"     TO="919812345678" SEQ="3" />
	            <ADDRESS FROM="9812345678" TO="919812345678" SEQ="4" />
	            <ADDRESS FROM="CELLNEXT"     TO="919812345678" SEQ="5" />
	            <ADDRESS FROM="CELLNEXT"     TO="919812345678" SEQ="6" />
	     </SMS>
	</MESSAGE>
	 */
	
	//for preparing the content for cellnext requestparameter 'data' as a value for it
	private final String data = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"+
					"<!DOCTYPE MESSAGE SYSTEM \"http://127.0.0.1/psms/dtd/message.dtd\">\n";

	
	
	private final String messageStructure = "<MESSAGE> \n"+"<USER USERNAME=\"democlnxtaggxml11\""+" PASSWORD=\"aggr28cnxt11\"/> \n"+
										"<SMSStructure>"+
										"</MESSAGE>";//for preparing root element <MESSAGE>
	
	private final String SMSs = "<SMS UDH=\"<VAL_UDH>\" CODING=\"<VAL_CODING>\" TEXT=\"<VAL_TEXT>\" " +
							"PROPERTY=\"<VAL_PROPERTY>\" ID=\"<VAL_ID>\" DLR=\"<VAL_DLR>\" VALIDITY=\"<VAL_VALIDITY>\"> \n" + 
							"<ADDRESS FROM=\"<VAL_FROM>\" TO=\"<VAL_TO>\" SEQ=\"<VAL_SEQ>\"/> \n" +
							"</SMS>\n";//for preparing <SMS> tags for each sms to be sent
	
	
	public CellNextApi() {
		
	}
	
	/**used to prepare the data into CellNext understandable format
	 * 
	 * @param messageContent
	 * @param msgType is coding to represent normal msg or unicode msg
	 * @param sentId
	 * @param fromMobNum
	 * @param toMobNum
	 * @param msgSeq
	 * @return
	 */
	public  void prepareData(String messageContent, String msgType, 
						String fromMobNum, String toMobNum, String msgSeq) {
		
		//TODO need to convert the request into the CellNext understandable format
		//and send a requset to send SMS
		
		if(msgType.equalsIgnoreCase("L")) {
			logger.info("-----just entered-----"+messageContent);
			messageContent = Utility.stringToHex(messageContent);
			
		}
		
		//(smsCampaign.getMessageType() == 1) ? "N" : "L";
		msgType = (msgType.equalsIgnoreCase("N")) ? "1" :"2";
		
		
		String tempSMS = SMSs.replace("<VAL_UDH>", "0").replace("<VAL_CODING>", msgType).replace("<VAL_TEXT>", messageContent).
					replace("<VAL_PROPERTY>", "").replace("<VAL_ID>", "47").replace("<VAL_DLR>", "0").
					replace("<VAL_VALIDITY>", "").replace("<VAL_FROM>", fromMobNum).replace("<VAL_TO>", toMobNum).
					replace("<VAL_SEQ>", msgSeq);
		
		String tempMessageStructure = messageStructure.replace("<SMSStructure>", tempSMS);
		
		String tempData = data + tempMessageStructure;
		
		logger.info("data is=====>"+tempData);
		sendSMS(tempData, "send");
		//logger.info("responseDataMap is=====>"+responseDataMap);
		//String statusMessage = errorcodesMap.get(responseDataMap.get(sentId));
		//logger.info("statusMessage is====>"+statusMessage);
		
		/*if(statusMessage== null){
			statusMessage = "Failure";
		}
		
		return statusMessage;*/
	}

	
	//private CaptiwayToSMSApiGateway captiwayToSMSApiGateway;
	
	
	/**used to send the SMS to Cellnext server
	 *  @param data
	 *  @param action
	 *  @return
	 */
	public static void sendSMS(String data, String action) {
		
		try {
			//TODO need to open a url connection
			//append the two request parameters along with the values to the url
			//capture the response as in Xml format which can be parsed further and get the GUID and required things further
			Map<String, String> responseDataHashmap = null;
			String postData = "";
			String response = "";
			
			postData += "data="+URLEncoder.encode(data, "UTF-8")+"&action="+"send";
			logger.info("postData is=====>"+postData);
			
			URL url = new URL("http://8.6.95.137/psms/servlet/psms.Eservice2");
			
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
				response += decodedString;
			}
			in.close();
			logger.info("reswponse is======>"+response);
			
			//responseDataHashmap = parse(response);
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
		
		
	}
	
	/**
	 * used to parse the given response coming from CellNext
	 * @param response
	 * @return
	 */
	public static Map<String, String> parse(String response) {
		Map<String, String> responseDataHashmap = null;
		//TODO need to parse and call the appropriate method in CaptiwayToSMSApiGateway
		/*the example respone after sending an SMS will be like this
		 * 
		 * <MESSAGEACK>
			<GUID GUID="kb5pj010213721f4610148cdd3DEMOCLNXTA" SUBMITDATE="2011-05-25 19:01:02" ID="1"/>
			−
			<GUID GUID="kb5pj010214121f461014ch2ghDEMOCLNXTA" SUBMITDATE="2011-05-25 19:01:02" ID="2">
			<ERROR SEQ="2" CODE="28674"/>
			</GUID>
			</MESSAGEACK>
		 */
		
		try {
			
			Node node = null;
			Element element = null;
			String guid = "";
			String sentId = "";
			String seq = "";
			String code = "";
			NodeList childNodeList = null;
			
			responseDataHashmap = new HashMap<String, String>(); 
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(response.getBytes()));
			doc.getDocumentElement().normalize();
			NodeList nodeLst = doc.getElementsByTagName("GUID");
			for(int i=0; i<nodeLst.getLength(); i++) {
				
				node = nodeLst.item(i);
				element = (Element)node;
				guid = element.getAttribute("GUID");
				sentId = element.getAttribute("ID");
				if(node.hasChildNodes()) {
					childNodeList = node.getChildNodes();
					for(int j=0; j<childNodeList.getLength(); j++ ) {
						node = childNodeList.item(j);
						element = (Element)node;
						seq = element.getAttribute("SEQ");
						code = element.getAttribute("CODE");
						//fldsInfoHashMap.put(,code );
						responseDataHashmap.put(sentId, code);
						
					}//for
					
					
				}//if
				else {
					code="8448";
					responseDataHashmap.put(sentId, code);
				}//else
				
			}//for
			
			return responseDataHashmap;
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		} catch (IOException e) {
			logger.error("Exception ::" , e);
			return null;
		}
		
	}
	
	/*public static void main(String args[]) {
		String response = "<MESSAGEACK>	<GUID GUID=\"kb5pj010213721f4610148cdd3DEMOCLNXTA\" SUBMITDATE=\"2011-05-25 19:01:02\" ID=\"1\"/>"+
		
		"<GUID GUID=\"kb5pj010214121f461014ch2ghDEMOCLNXTA\" SUBMITDATE=\"2011-05-25 19:01:02\" ID=\"2\">"+
		"<ERROR SEQ=\"2\" CODE=\"28674\"/>"+"</GUID></MESSAGEACK>";
		
		
		try {
			
			Node node = null;
			Element element = null;
			String guid,id,seq,code;
			NodeList childNodeList = null;
			
			HashMap<String, String> fldsInfoHashMap = new HashMap<String, String>(); 
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(response.getBytes()));
			doc.getDocumentElement().normalize();
			NodeList nodeLst = doc.getElementsByTagName("GUID");
			for(int i=0; i<nodeLst.getLength(); i++) {
				
				node = nodeLst.item(i);
				element = (Element)node;
				guid = element.getAttribute("GUID");
				id = element.getAttribute("id");
				logger.info("the values are====>"+i+"---> "+guid+" "+id);
				if(node.hasChildNodes()) {
					childNodeList = node.getChildNodes();
					for(int j=0; j<childNodeList.getLength(); j++ ) {
						node = childNodeList.item(j);
						element = (Element)node;
						seq = element.getAttribute("SEQ");
						code = element.getAttribute("CODE");
						logger.info("values in inner for loop are====>"+j+"---->"+seq+" "+code);
						//fldsInfoHashMap.put(,code );
						
						
						
					}
					
					
				}
				defVal = element.getAttribute("value");
				type = element.getAttribute("type");
				fldsInfoHashMap.put(""+i, name+"|"+id+"|"+defVal+"|"+type);
				
				
				
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch(IOException e){
			logger.error("Exception ::", e);
		}
		
		
	}*/
	
	public static void main(String ags[]) throws Exception {
		
		String encode = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"+
						"<!DOCTYPE STATUSREQUEST SYSTEM \"http://127.0.0.1/psms/dtd/requeststatus.dtd\" >"+
						"<STATUSREQUEST>"+
						        "<USER USERNAME=\" democlnxtaggxml11\" PASSWORD=\" aggr28cnxt11\"/>"+
						        "<GUID GUID=\"kb62f334343611f461014dxc1wDEMOCLNXTA\">"+
						                "<STATUS SEQ=\"1\" />"+ 
						        "</GUID>"+
						"</STATUSREQUEST>";

		encode = URLEncoder.encode(encode, "UTF-8");
		logger.info("the encode string is====>"+encode);
		
	}
	
}
