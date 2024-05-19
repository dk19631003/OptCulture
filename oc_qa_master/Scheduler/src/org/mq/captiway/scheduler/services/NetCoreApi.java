package org.mq.captiway.scheduler.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.SMSCampaignSent;
import org.mq.captiway.scheduler.beans.SMSDeliveryReport;
import org.mq.captiway.scheduler.dao.SMSDeliveryReportDao;
import org.mq.captiway.scheduler.dao.SMSDeliveryReportDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class NetCoreApi {

	/*public static final String NETCORE_ERROR_MOBILENUMINVALID = "Mobile number invalid";
	public static final String NETCORE_ERROR_MSGTOUSERREJECTED_NDNC_STATUS_UNAVAILABLE = "NDNC status is unavailable";
	public static final String NETCORE_ERROR_MSGTOUSERREJECTED_NDNC_STATUS_REGISTERED = "User registered with NDNC";
	public static final String NETCORE_ERROR_MSGTOUSERREJECTED_NDNC_STATUS_UNKNOWN = "NDNC status unknown";
	public static final String NETCORE_ERROR_EMPTY_FEEDID_OR_MOBNUM = "FeedId or mobile number is empty";
	public static final String NETCORE_SUCCESS = "success";
	*/
	
	//public static final Map<String,String> errorCodesMap = new HashMap<String, String>();
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	/*static {
		//TODO need to get username,password,feedid from DB
		errorCodesMap.put("108",NETCORE_ERROR_MOBILENUMINVALID);
		errorCodesMap.put("131",NETCORE_ERROR_MSGTOUSERREJECTED_NDNC_STATUS_UNAVAILABLE);
		errorCodesMap.put("132",NETCORE_ERROR_MSGTOUSERREJECTED_NDNC_STATUS_REGISTERED);
		errorCodesMap.put("136",NETCORE_ERROR_MSGTOUSERREJECTED_NDNC_STATUS_UNKNOWN);
		errorCodesMap.put("-1",NETCORE_ERROR_EMPTY_FEEDID_OR_MOBNUM);
		errorCodesMap.put("success",NETCORE_SUCCESS);
		
		
	}*/
	//private Long smsCampRepId;
	//private List<String> smsMsgsList;
	private List<String> smsStructureList;
	private int smsCount;
	
	public NetCoreApi() {
		//smsMsgsList = new ArrayList<String>();
		smsStructureList = new ArrayList<String>();
		smsCount = 0;
	}
	
	private SMSDeliveryReportDao smsDeliveryReportDao;
	private SMSDeliveryReportDaoForDML smsDeliveryReportDaoForDML;
	
	public SMSDeliveryReportDao getSmsDeliveryReportDao() {
		return smsDeliveryReportDao;
	}

	public void setSmsDeliveryReportDao(SMSDeliveryReportDao smsDeliveryReportDao) {
		this.smsDeliveryReportDao = smsDeliveryReportDao;
	}
	
	public SMSDeliveryReportDaoForDML getSmsDeliveryReportDaoForDML() {
		return smsDeliveryReportDaoForDML;
	}

	public void setSmsDeliveryReportDaoForDML(SMSDeliveryReportDaoForDML smsDeliveryReportDaoForDML) {
		this.smsDeliveryReportDaoForDML = smsDeliveryReportDaoForDML;
	}
	
	
	/**this is example format to send an SMS to NetCore Api
	 * <!DOCTYPE REQ SYSTEM
	'http://bulkpush.mytoday.com//BulkSms/BulkSmsV1.00.dtd'>
	<REQ>
		<VER>1.0</VER>
		<USER>
		    <USERNAME>99670xxxxx</USERNAME>
		    <PASSWORD>abcde</PASSWORD>
	     </USER>
	     <ACCOUNT>
	    	<ID>38272</ID>
	    </ACCOUNT>
	    <MESSAGE>
	        <TEXT>hello hi</TEXT>
	        <TYPE>0</TYPE>
	        <MID>1</MID><!--we can set it as with SMSCampaignSentId--!>
	        <SMS FROM='' TO='919900840069' INDEX ='1'(this is the sequence number of the message unique to each contact)></SMS>
	    </MESSAGE>
	    <MESSAGE>
	         <TEXT>hello</TEXT>
	         <TYPE>0</TYPE>
	         <MID>2</MID>
	         <SMS FROM='' TO='919900840069' INDEX ='1'></SMS>
	    </MESSAGE>
    </REQ>

	 */
	private final String data = "<!DOCTYPE REQ SYSTEM 'http://bulkpush.mytoday.com//BulkSms/BulkSmsV1.00.dtd'>";
	private final String requestStructure = "<REQ> \n <VER>1.0</VER> \n <USER> \n <USERNAME><VAL_USER></USERNAME> \n"+
											"<PASSWORD><VAL_PWD></PASSWORD> \n </USER> \n <ACCOUNT> \n	<ID><VAL_ID></ID> \n " +
											"</ACCOUNT> \n"+"<MESSAGESTRUCTURE> \n"+"</REQ>";
	private final String messageStructure = "<MESSAGE> \n <TAG><VAL_TAG></TAG> \n <TEXT><VAL_TXT></TEXT> \n <TYPE><VAL_TYPE></TYPE> \n <MID><VAL_MID></MID> \n"+
											
											"<SMS FROM='<VAL_FROM>' TO='<VAL_TO>' INDEX ='<VAL_INDEX>'></SMS></MESSAGE>";
	//private final String smsStructure = "<SMS FROM='<VAL_FROM>' TO='<VAL_TO>' INDEX ='<VAL_INDEX>'></SMS></MESSAGE> \n";
	
	//private String messages;
	//private List<String> smsStructureList;//will be utilized for actual contact values replacement in the smsStructure
	
	
	/**
	 * 
	 */
	
	public List<String> prepareData(String messageContent, String msgType, String sentId,
			String fromMobNum, String toMobNum, String msgSeq, Long smsCampRepId, boolean limit, int size, String senderId) throws Exception {
		smsCount++;
		//this.smsCampRepId = smsCampRepId;
		List<String> responseList = null;
		
		//******* need to prepare the data into netCore understandable format(into an XML format)********
		//send a request to send SMS
		String response = "";
		
		msgType = "1";//for text message//y here hard coded like this means for other apis N or L are the message types.
		
		String tempMsg = messageStructure.replace("<VAL_TXT>", messageContent).replace("<VAL_TYPE>", msgType).
						replace("<VAL_MID>", sentId).replace("<VAL_FROM>", senderId).
						replace("<VAL_TAG>", ""+"0000000"+smsCampRepId).
						replace("<VAL_TO>", toMobNum).replace("<VAL_INDEX>", msgSeq);
		/*String tempsms = smsStructure.replace("<VAL_FROM>", fromMobNum).
							replace("<VAL_TO>", toMobNum).replace("<VAL_INDEX>", msgSeq);*/

			smsStructureList.add(tempMsg);
			
			/*String tempReq = requestStructure.replace("<VAL_USER>", "9848495956").replace("<VAL_PWD>", "amgjm").
							replace("<VAL_ID>", "308138").replace("<MESSAGESTRUCTURE>", tempMsg);*/
			
			String tempReq = "";
			String tempData ="";
			//logger.debug("the data is====>"+tempData);
			if((smsCount == 100 && limit) || (smsCount == size && !limit)){
				
				//**** need to prepare the msgStructure*****
				
				tempReq = requestStructure.replace("<VAL_USER>", "9848495956").replace("<VAL_PWD>", "amgjm").
							replace("<VAL_ID>", "308138");
				String tempMsgStr = "";
				for (String msgStructure : smsStructureList) {
					tempMsgStr += msgStructure;
				}
				tempData = data+tempReq.replace("<MESSAGESTRUCTURE>", tempMsgStr);
				
				if(logger.isDebugEnabled()) logger.debug(" finally the tempdata is=====>"+tempData);
				
				responseList = sendSMS(tempData);
				smsStructureList.clear();
				smsCount = 0;
				//smsStructureList.clear();
			}
			return responseList;
		
		
	}
	
	/**
	 * this method sends the request to NetCore Gateway to fetch the reports
	 * @param smsDeliveryReport
	 * @return
	 */
	public List<String> fetchReports(SMSDeliveryReport smsDeliveryReport) {
		
		String quickResponseContent = smsDeliveryReport.getRequestId();
		String sentdate = MyCalendar.calendarToString(smsDeliveryReport.getReqGeneratedDate(), MyCalendar.FORMAT_DATEONLY1);
		//String reqId = parse(quickResponseContent);
		String postData = "";
		String response = "";
		
		/**this is the exmaple url along with the required initial request parameters**/
		//http://stats.mytoday.com/dlr_api?feedid=308138&date=2011-06-03&format=xml
		
		try {
			if(logger.isDebugEnabled()) logger.debug("sent date is===>"+sentdate);
			
			/*postData += "feedid="+URLEncoder.encode("308138", "UTF-8")+"&date="+URLEncoder.encode(sentdate, "UTF-8")+
			"&format="+URLEncoder.encode("xml", "UTF-8");*/
			
			postData += "feedid="+"308138"+"&date="+sentdate+"&format="+"xml";
			if(logger.isDebugEnabled()) logger.debug("post data is====>"+postData);
			URL url = new URL("http://stats.mytoday.com/dlr_api?"+postData);
			HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
			
			urlconnection.setRequestMethod("GET");
			urlconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			urlconnection.setDoOutput(true);

			/*OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
			out.write(postData);
			out.flush();
			out.close();
			*/
			BufferedReader in = new BufferedReader(new InputStreamReader(urlconnection.getInputStream()));
			
			String decodedString;
			while ((decodedString = in.readLine()) != null) {
				response += decodedString;
			}
			in.close();
			if(logger.isDebugEnabled()) logger.debug("fetch request response is reswponse in step 1 is======>"+response);
			
			String transcId = ((Element)(DocumentBuilderFactory.newInstance().newDocumentBuilder().
					parse(new ByteArrayInputStream(response.getBytes()))).getDocumentElement()).getTextContent();
			
			if(logger.isDebugEnabled()) logger.debug("given transaction id in step 1 is=====>"+transcId);
			Thread.sleep(60000);
			response = "";
			postData = "";
			postData += "feedid=308138&date="+sentdate+"&format=xml&dtxnid="+transcId;
			url = new URL("http://stats.mytoday.com/dlr_api?"+postData);
			urlconnection = (HttpURLConnection) url.openConnection();
			
			urlconnection.setRequestMethod("GET");
			urlconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			urlconnection.setDoOutput(true);

			/*out = new OutputStreamWriter(urlconnection.getOutputStream());
			out.write(postData);
			out.flush();
			out.close();*/
			
			in = new BufferedReader(new InputStreamReader(urlconnection.getInputStream()));
			
			
			while ((decodedString = in.readLine()) != null) {
				response += decodedString;
			}
			in.close();
			if(logger.isDebugEnabled()) logger.debug("fetch request response is reswponse in step 2 is======>"+response); 
			
			String flag =  ((Element)(DocumentBuilderFactory.newInstance().newDocumentBuilder().
							parse(new ByteArrayInputStream(response.getBytes()))).getDocumentElement()).getTextContent();
			if(flag.equalsIgnoreCase("DONE")){
				response = "";
				postData = "";
				postData += "feedid=308138&date="+sentdate+"&format=xml&dtxnid="+transcId+"&ack=1";
				url = new URL("http://stats.mytoday.com/dlr_api?"+postData);
				urlconnection = (HttpURLConnection) url.openConnection();
				
				urlconnection.setRequestMethod("GET");
				urlconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				urlconnection.setDoOutput(true);

				/*out = new OutputStreamWriter(urlconnection.getOutputStream());
				out.write(postData);
				out.flush();
				out.close();*/
				
				in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
				
				
				while ((decodedString = in.readLine()) != null) {
					response += decodedString;
				}
				smsDeliveryReport.setStatus("Done");
				
			}else{
				
				smsDeliveryReport.setStatus("Active");
			}
			if(logger.isDebugEnabled()) logger.debug("response in step 3 is====>"+response);
			
			//TODO need to extend the design
			List<String> responseContent  = parseForReports(response);
			
			
			//smsDeliveryReportDao.saveOrUpdate(smsDeliveryReport);
			smsDeliveryReportDaoForDML.saveOrUpdate(smsDeliveryReport);
			return responseContent;
			
		} catch (MalformedURLException e) {
			logger.error("Exception",e);
			return null;
			
		}catch (UnsupportedEncodingException e) {
			logger.error("Exception",e);
			return null;
			
		} catch (ProtocolException e) {
			logger.error("Exception",e);
			return null;
			
		} catch (IOException e) {
			logger.debug("Exception",e);
			return null;
			
		}catch (ParserConfigurationException e) {
			logger.debug("Exception",e);
			return null;
		} catch (SAXException e) {
			logger.debug("Exception",e);
			return null;
			
		}catch (DOMException e) {
			logger.debug("Exception",e);
			return null;
			
		}catch (Exception e) {
			logger.debug("Exception",e);
			return null;
			
		}
	}//fetchReports
	
	/**
	 * this method used to parse the response given by NtCore when requested for the reports
	 * @param response
	 * @return
	 */
	public List<String> parseForReports(String response) {
		//TODO need to parse the response and update the status for each smsCampaignsent
		
		/** this is the example format how the response for fetch request will be....
		 * <REQ>
			<MODE>1</MODE>
			<VER>1.0</VER>
			−
			<ACCOUNT>
			<ID/>
			</ACCOUNT>
			−
			<MESSAGE TEXT="hai this is a test msg for reports" REQUESTID="336439372" TRANSACTIONID="696534097" SENDERID="DEMOCAPT" TAG="null" MESSAGEID="-2" NOOFSMS="1">
			<MOBILE ID="1" SENTTIME="2011-06-10 09:21:50" DELIVEREDTIME="2011-06-10 10:13:00" STATUS="Undelivered" REASONCODE="1314" GATEWAYID="403" URL="" INDEXID="-2">919194927928</MOBILE>
			</MESSAGE>
			</REQ>
		 */
		//******need to find the error messages for the error codes*******
		
		Node node = null;
		Element element = null;
		String statusmsg = "";
		String reportId = "";
		String reqId = "";
		String mobile = "";
		NodeList childNodeList = null;
		List<String> responseContent = new ArrayList<String>();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(response.getBytes()));
			doc.getDocumentElement().normalize();
			
			
			/*Element docElement = doc.getDocumentElement();
			if(logger.isDebugEnabled()) logger.debug("the document element is====>"+docElement.toString());*/
			
			NodeList nodeLst = doc.getElementsByTagName("MESSAGE");
			
			for(int i=0; i<nodeLst.getLength(); i++){
				node = nodeLst.item(i);
				element = (Element)node;
				reportId = element.getAttribute("TAG");
				reqId = element.getAttribute("REQUESTID");
				if(element.hasChildNodes()) {
					childNodeList = node.getChildNodes();
					for(int j=0; j<childNodeList.getLength(); j++) {
						node = childNodeList.item(j);
						if(node.getNodeName().equalsIgnoreCase("MOBILE")) {
							element = (Element)node;
							mobile = element.getTextContent();
							statusmsg = element.getAttribute("STATUS");
							
							responseContent.add(reportId+"|"+statusmsg+"|"+mobile+"|"+reqId);
						}
					}//for
					
					
				}//if
				
				
			}//for
			
			return responseContent;
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			logger.debug("Exception",e);
			return null;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			logger.debug("Exception",e);
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.debug("Exception",e);
			return null;
		}
		
		
		
		
	}//parseForReports
	
	
	/**//**if no errors the response format will be as follows
	 * <!DOCTYPE RESULT SYSTEM
	'http://bulkpush.mytoday.com/BulkSms/BulkSmsRespV1.00.dtd'>
	<RESULT REQID ='7'>
	        <MID SUBMITDATE='2008-06-05 09:16:46' ID='1' TAG = '' TID =
	'11'>
	</MID>
	        <MID SUBMITDATE='2008-06-05 09:16:46' ID='2' TAG = '' TID =
	'12'>
	        </MID>
	</RESULT>
	 *//*
	
	*//**if error occured the response format will be as follows
	 * <!DOCTYPE RESULT SYSTEM
		'http://bulkpush.mytoday.com/BulkSms/BulkSmsRespV1.00.dtd'>
		<RESULT>
		<REQUEST-ERROR>
		     <ERROR>
		           <CODE>104<CODE>
		           <DESC> Credit not sufficient </DESC>
		     </ERROR>
		</REQUEST-ERROR>
		</RESULT>
	 * <RESULT REQID ='3020'>
	       <MID SUBMITDATE='2008-08-07 11:57:46' ID='1' TAG = 'null'>
	               <ERROR>
	                        <CODE>120</CODE>
	                        <DESC>International sms feature not
									enabled</DESC>
	               </ERROR>
	       </MID>
	       <MID SUBMITDATE='2008-08-07 11:57:46' ID='2' TAG = 'null' TID
				= '553336'></MID>
	       <MID SUBMITDATE='2008-08-07 11:57:46' ID='3' TAG = 'null'>
	               <ERROR>
	                        <CODE>106</CODE>
	                        <DESC>Message Empty</DESC>
	               </ERROR>
	       </MID>
	       </RESULT>

	 */
	
	
	
	/**
	 * this method actually sends the SMS to netCore Gateway
	 * @param tempData
	 * @return
	 */
	public  List<String> sendSMS(String tempData) {
		
		try {
			//TODO need to open a url connection
			//append the  request parameter UserRequest along with the values to the url
			//capture the response as in Xml format which can be parsed further and get the request id and  other required things further
			
			HashMap<String, String> responseDataHashmap = null;
			String postData = "";
			String response = "";
			
			postData += "UserRequest="+URLEncoder.encode(tempData, "UTF-8");
			if(logger.isDebugEnabled()) logger.debug("postData is=====>"+postData);
			
			URL url = new URL("http://bulkpush.mytoday.com/BulkSms/SendSms");
			
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
				response += decodedString;//response content
			}
			in.close();
			if(logger.isDebugEnabled()) logger.debug("response is======>"+response);
			
			/***parse and update the status based on the initial response****/
			
			List<String> responseList = parseInitialResponse(response);
			//saveRequest(response,smsCampRepId);
			return responseList;
			
			
			
		} catch (UnsupportedEncodingException e) {
			logger.debug("Exception",e);
			return null;
			
		} catch (MalformedURLException e) {
			logger.debug("Exception",e);
			return null;
			
		} catch (ProtocolException e) {
			logger.debug("Exception",e);
			return null;
			
		} catch (IOException e) {
			logger.debug("Exception",e);
			return null;
			
		}
	}//sendSMS

	
	
	/**
	 * this method parse the response given by netCore after immediate sending
	 * @param response
	 * @return
	 */
	public List<String> parseInitialResponse(String response) {
		List<String> responseContentList = new ArrayList<String>();
		try {
			
			Node node = null;
			Node childNode = null;
			Element element = null;
			String reqid = "";
			String sentId = "";
			//String seq = "";
			String desc = "";
			NodeList childNodeList = null;
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(response.getBytes()));
			doc.getDocumentElement().normalize();
			reqid = ((Element)doc.getDocumentElement()).getAttribute("REQID");//request id specific to each sending
			
			
			NodeList nodeLst = doc.getElementsByTagName("MID");//given to each <MESSAGE> tag
			if(logger.isDebugEnabled()) logger.debug("the number of MID tags are===>"+nodeLst.getLength());
			for(int i=0; i<nodeLst.getLength(); i++) {
				
				
				node = nodeLst.item(i);
				element = (Element)node;
				//guid = element.getAttribute("GUID");
				sentId = element.getAttribute("ID");//sent id in our SMSCampaignSent
				
				if(logger.isDebugEnabled()) logger.debug("sent id is===>"+sentId+"node is====>"+node.toString());
				if(element.hasChildNodes()) {
					if(!element.getTextContent().trim().equalsIgnoreCase("")) {
						desc = element.getTextContent();
						desc = desc.replace(desc.substring(0, 3), "").trim();//to remove 3 digit ERROR codes
						
						if(desc.startsWith("Mobile number") && desc.endsWith("invalid")) {// to remove the invalid mobile number in description
							
							desc = "Invalid mobile number";
						}
						
						responseContentList.add(sentId+"|"+desc+"|"+reqid);
						
						/*desc = desc.replace(desc.substring(0, 3), "");//to remove 3 digit ERROR codes
						
						if(desc.contains("Invalid mobile number")) {// to remove the invalid mobile number in description
							
							desc = desc.replace(desc.substring(desc.indexOf("-",desc.indexOf("Invalid mobile number")), desc.length()), "");
						}
						
						responseContentList.add(sentId+"|"+desc+"|"+reqid);*/
						
					}else {// if no error occured
						
						responseContentList.add(sentId+"|"+"Status Pending"+"|"+reqid);
					}
				}//if
				
				
			
			}//for
			if(logger.isDebugEnabled()) logger.debug("the response list is====>"+responseContentList);
			
			return responseContentList;
			
		}catch (UnsupportedEncodingException e) {
			logger.debug("Exception",e);
			return null;
		} catch (MalformedURLException e) {
			logger.debug("Exception",e);
			return null;
		} catch (IOException e) {
			logger.debug("Exception",e);
			return null;
		} catch(Exception e){
			logger.debug("Exception",e);
			return null;
		}
		
	}//parseInitialResponse
	
		
	
	/**
	 * this method parse the respone for the given requestid(currently not utilizing)
	 * @param xmlStr
	 * @return
	 */
	public String parse(String xmlStr) {
		String reqId = "";
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(xmlStr.getBytes()));
			doc.getDocumentElement().normalize();
			
			
			Element docElement = doc.getDocumentElement();
			if(logger.isDebugEnabled()) logger.debug("the document element is====>"+docElement.toString());
			
			 reqId = docElement.getAttribute("REQID");
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
		}
		
		return reqId;
		
	}
	
	/*public static void main(String args[]) throws Exception {
	NetCoreApi obj = new NetCoreApi();
	//String response = obj.prepareData("hai", "N", "123", "919848495956", "919490927928", "1");
	String response = "<!DOCTYPE RESULT SYSTEM 'http://bulkpush.mytoday.com/BulkSms/BulkSmsRespV1.00.dtd'><RESULT REQID ='332211828'>	<MID SUBMITDATE='2011-06-03 18:18:11' ID='87' TAG = 'null' TID = '686885590'>	</MID></RESULT>";
	//String response = "<!DOCTYPE RESULT SYSTEM 'http://bulkpush.mytoday.com/BulkSms/BulkSmsRespV1.00.dtd'><RESULT REQID ='331177299'>	<MID SUBMITDATE='2011-06-02 13:19:46' ID='123' TAG = 'null' TID = '683648130'>  </MID></RESULT>";
	String reqId = obj.parse(response);
	if(logger.isDebugEnabled()) logger.debug("status message is====>"+reqId);
	
	SMSDeliveryReport smsDeliveryreport = new SMSDeliveryReport();
	smsDeliveryreport.setReqGeneratedDate(Calendar.getInstance());
	if(logger.isDebugEnabled()) logger.debug(smsDeliveryreport.getReqGeneratedDate());
	if(logger.isDebugEnabled()) logger.debug("the generated date is====>"+MyCalendar.calendarToString(smsDeliveryreport.getReqGeneratedDate(), MyCalendar.FORMAT_DATEONLY1));
	*/
	/*try {
		
		Node node = null;
		Element element = null;
		String guid = "";
		String sentId = "";
		//String seq = "";
		String code = "";
		NodeList childNodeList = null;
		
		Map<String, String> responseDataHashmap = new HashMap<String, String>(); 
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(response.getBytes()));
		doc.getDocumentElement().normalize();
		
		
		NodeList nodeLst = doc.getElementsByTagName("MID");
		
		for(int i=0; i<nodeLst.getLength(); i++) {
			node = nodeLst.item(i);
			element = (Element)node;
			//guid = element.getAttribute("GUID");
			sentId = element.getAttribute("ID");
			if(logger.isDebugEnabled()) logger.debug("sent id is===>"+sentId+"node is====>"+node.toString());
			if(logger.isDebugEnabled()) logger.debug("text content is====>"+node.getTextContent());
			if(node.hasChildNodes()) {
				childNodeList = node.getChildNodes();
				if(logger.isDebugEnabled()) logger.debug("the child node size is====>"+childNodeList.getLength());
				for(int j=0; j<childNodeList.getLength(); j++ ) {
					node = childNodeList.item(j);
					if(node.getNodeType() == (short)3) {
						responseDataHashmap.put(sentId, "success");
						continue;
					}
					if(logger.isDebugEnabled()) logger.debug("node is====>"+node.toString());
					element = (Element)node;
					code = element.getFirstChild().getTextContent();
					//code = element.getAttribute("CODE");
					//fldsInfoHashMap.put(,code );
					responseDataHashmap.put(sentId, code);
				}//for		
			
			
			
			}//if
			else {
				code = "success";
				responseDataHashmap.put(sentId, code);
			}
			
			if(logger.isDebugEnabled()) logger.debug("responseDataHashmap is===>"+responseDataHashmap);
		
		}//for
		
	}catch(Exception e) {
		logger.error("Exception ::::" , e);
	}
}*/

	
	
}//class
