package org.mq.marketer.campaign.controller.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.DRSMSSent;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.SMSCampaignSentDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.EquenceAutoSMSResponseList;
import org.mq.optculture.model.EquenceDLRResponseObject;
import org.mq.optculture.model.EquenceSingleSMSResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollRequest;
import org.mq.optculture.model.ocloyalty.RequestHeader;
import org.mq.optculture.model.ocloyalty.SkuDetails;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;

public class EquenceSMSGateway {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private String userID ;
	private String pwd ;
	private String senderId;
	private String peId;
	private OCSMSGateway ocsmsGateway;
	private final String urlStr = "https://api.equence.in/pushsms";
	
	
	public EquenceSMSGateway() {}
	
	public EquenceSMSGateway(OCSMSGateway ocsmsGateway,String userID,String pwd,String senderId,String peId) {
		
		this.ocsmsGateway = ocsmsGateway;
		this.userID = userID;
		this.pwd = pwd;
		this.senderId=senderId;
		this.peId=peId;
		try {
			ServiceLocator serviceLocator = ServiceLocator.getInstance();
			/*smsCampaignSentDao = (SMSCampaignSentDao)serviceLocator.getDAOByName("smsCampaignSentDao");
			smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML )serviceLocator.getDAOForDMLByName("smsCampaignSentDaoForDML");
			smsCampaignReportDao = (SMSCampaignReportDao)serviceLocator.getDAOByName("smsCampaignReportDao");
			smsCampaignReportDaoForDML = (SMSCampaignReportDaoForDML)serviceLocator.getDAOForDMLByName("smsCampaignReportDaoForDML");
			contactsDao = (ContactsDao)serviceLocator.getDAOByName("contactsDao");
			contactsDaoForDML  = (ContactsDaoForDML)serviceLocator.getDAOForDMLByName("contactsDaoForDML");*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception while getting the dao ", e);
		}
	}
	
	public EquenceSMSGateway(String userID, String pwd, String senderId,String peId) {
		
		this.userID = userID;
		this.pwd = pwd;
		this.senderId=senderId;
		this.peId = peId;
		
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
	
	//String content="Hello harshi"; 
	

	/*public void testSingle(String content, String mobile, String senderId ) {

			try{
				
				String postData = "";
				String data = URLEncoder.encode(content, "UTF-8"); 
				
				postData += this.queryString.replace("<USERNAME>", getUserID()).replace("<PWD>", URLEncoder.encode(getPwd(), "UTF-8"))
					     .replace("<SENDERID>", senderId).replace("<TO>", mobile)
					     .replace("<MESSAGE>", data).replace("<CONCAT>", content.trim().length() <= 160 ? "0" : "1");
				
				logger.info("started sending single sms to equence");
					 DefaultHttpClient httpClient = new DefaultHttpClient();
					 HttpPost postRequest = new HttpPost("https://api.equence.in/pushsms");
					 StringEntity input = new StringEntity(postData);
					 input.setContentType("application/json");
					 postRequest.setEntity(input);

					 HttpResponse response = httpClient.execute(postRequest);

					 if (response.getStatusLine().getStatusCode() != 201) {
					 throw new RuntimeException("Failed : HTTP error code : "
					 + response.getStatusLine().getStatusCode());
					 }

					 BufferedReader br = new BufferedReader(
					                         new InputStreamReader((response.getEntity().getContent())));

					 String output;
					 logger.info("finished sending single sms to equence");
					 while ((output = br.readLine()) != null) {
					 }

					 httpClient.getConnectionManager().shutdown();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				logger.error("Exception :::", e);
				
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				logger.error("Exception :::", e);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Exception :::", e);
				
			}catch (Exception e) {
				// TODO: handle exception
				logger.error("Exception :::", e);
			}
			//logger.debug("response is======>"+output);
	}*/
		
	public String sendTestSMS(String content, String mobile,String templateRegisteredId, Long rowId) {
		

		
		try{
			
			List<EquenceTextList> textLst = new ArrayList<EquenceTextList>();
			EquenceTextList equenceTextList = new EquenceTextList();
			equenceTextList.setTo(mobile);
			equenceTextList.setText(content);
			textLst.add(equenceTextList);
				Gson gson = new Gson();
				PrepareEquenceJsonRequest pj=new PrepareEquenceJsonRequest();
				pj.setUsername(userID);
				pj.setPassword(pwd);
				pj.setFrom(senderId);
				pj.setPeId(peId);
				pj.setTmplId(templateRegisteredId);
				pj.setTextlist(textLst);
				pj.setMSGID(rowId!=null ? OCConstants.AutoSMSPrefix+rowId :"");
				
				List<PrepareEquenceSingleJsonRequest> Equlist=	PrepareEquenceSingleJsonRequest.convert(pj);
			
				logger.info("new requestlist is "+Equlist);

				
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost postRequest = new HttpPost(urlStr);
				
				for (PrepareEquenceSingleJsonRequest msg:Equlist) {
					
				logger.info("entering for loop");

				String request = gson.toJson(msg);
				
				logger.info("requestJson--->"+request);
				
			//	DefaultHttpClient httpClient = new DefaultHttpClient();
			//	HttpPost postRequest = new HttpPost("https://api.equence.in/pushsms");
				StringEntity input = new StringEntity(request);
				input.setContentType("application/json");
				postRequest.setEntity(input);
				
				HttpResponse response = httpClient.execute(postRequest);
				
				/*if (response.getStatusLine().getStatusCode() != 201) {
					 throw new RuntimeException("Failed : HTTP error code : "
					 + response.getStatusLine().getStatusCode());
					 }*/
				
				BufferedReader br = new BufferedReader(
						new InputStreamReader((response.getEntity().getContent())));
				
				String resp="";
				String output="";
				logger.info("Output from Server .... \n");
				while ((output = br.readLine()) != null) {
					if(output!=null)resp=resp+output;
					logger.info("output---"+resp);
					}
				/*logger.info("resp .... \n"+resp);
				resp.substring(resp.indexOf('['),resp.indexOf(']'));
				resp= resp.substring((resp.indexOf('[')+1),resp.indexOf(']'));
				logger.info("resp .... \n"+resp);
				parseInitialResponse(resp);*/
				httpClient.getConnectionManager().shutdown();
				
				String mrId = parseInitialAutoSMSResponse(resp);
				return mrId;
				}		
		} catch (MalformedURLException e) {
			
			logger.error("Exception",e);
		} catch (IOException e) {
			
			logger.error("Exception",e);
			
		}
		return null;
	}	
	
	public String sendDRSMS(String content, String mobile,String templateRegisteredId,DRSMSSent drSmsSent) {
		

		
		try{
			content = content.replace("|^", "[").replace("^|", "]");
			
			logger.info("Before content: "+content);
			
			content = replaceBarcodePhWithDummyCode(content);
			
			List<EquenceTextList> textLst = new ArrayList<EquenceTextList>();
			EquenceTextList equenceTextList = new EquenceTextList();
			equenceTextList.setTo(mobile);
			equenceTextList.setText(content);
			textLst.add(equenceTextList);
				Gson gson = new Gson();
				PrepareEquenceJsonRequest pj=new PrepareEquenceJsonRequest();
				pj.setUsername(userID);
				pj.setPassword(pwd);
				pj.setFrom(senderId);
				pj.setPeId(peId);
				pj.setTmplId(templateRegisteredId);
				pj.setTextlist(textLst);
				pj.setMSGID(drSmsSent!=null ? OCConstants.DRSMSPrefix+drSmsSent.getId() :"");
				
				String request = gson.toJson(pj);
				logger.info("requestJson--->"+request);
				
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost postRequest = new HttpPost("https://api.equence.in/pushsms");
				StringEntity input = new StringEntity(request);
				input.setContentType("application/json");
				postRequest.setEntity(input);
				
				HttpResponse response = httpClient.execute(postRequest);
				
				/*if (response.getStatusLine().getStatusCode() != 201) {
					 throw new RuntimeException("Failed : HTTP error code : "
					 + response.getStatusLine().getStatusCode());
					 }*/
				
				BufferedReader br = new BufferedReader(
						new InputStreamReader((response.getEntity().getContent())));
				
				String resp="";
				String output="";
				logger.info("Output from Server .... \n");
				while ((output = br.readLine()) != null) {
					if(output!=null)resp=resp+output;
					logger.info("output---"+resp);
					}
				/*logger.info("resp .... \n"+resp);
				resp.substring(resp.indexOf('['),resp.indexOf(']'));
				resp= resp.substring((resp.indexOf('[')+1),resp.indexOf(']'));
				logger.info("resp .... \n"+resp);
				parseInitialResponse(resp);*/
				httpClient.getConnectionManager().shutdown();
				
				String mrId = parseInitialAutoSMSResponse(resp);
				return mrId;
			
		} catch (MalformedURLException e) {
			
			logger.error("Exception",e);
		} catch (IOException e) {
			
			logger.error("Exception",e);
			
		}
		return null;
	}
	
	public String parseInitialAutoSMSResponse(String response) {
		logger.info("auto sms response--"+response);
		try {
			Gson gson1 = new Gson();
			EquenceSingleSMSResponse eqResponse =new EquenceSingleSMSResponse();
			List<EquenceAutoSMSResponseList> responseList =null;
			eqResponse = gson1.fromJson(response,EquenceSingleSMSResponse.class);
			responseList=eqResponse.getResponse();
			logger.debug("=======responseList ======"+responseList);
			
			String mrId="";
			String msgId="";
			for (EquenceAutoSMSResponseList respLst : responseList ){
				mrId=respLst.getMrId();
				if(msgId.equalsIgnoreCase(Constants.STRING_NILL)) msgId=mrId;
				logger.info("mrId== "+respLst.getMrId());
			}
			
			return msgId;
		} catch(Exception e){
			logger.error("Exception",e);
			return null;
		}
		
	}//parseInitialAutoSMSResponse
	
		/*public void parseInitialResponse(String response) {
			List<String> responseContentList = new ArrayList<String>();
			try {
				//String jsonValue = OptCultureUtils.getParameterJsonValue(response);
				Gson gson = new Gson();
				SampleEqResponse eqResponse =null;
				eqResponse = gson.fromJson(response,SampleEqResponse.class);
				
				logger.debug("=======ending the service ======"+eqResponse);
				
				String responseJson = gson.toJson(eqResponse);
				eqResponse.setJsonValue(responseJson);
				logger.info("Response = "+responseJson);
				logger.info("mrid==="+eqResponse.getMrId());
				
				
			} catch(Exception e){
				logger.info("Exception",e);
			}
			
		}//parseInitialResponse
*/	/*public String sendOverHTTP(String content, String mobile, String senderId) {
		
		try{
		//String queryString = new String("{\"username\":\"<USERNAME>\",\"password\":\"<PWD>\",\"to\":\"<TO>\",\"from\":\"<SENDERID>\",\"text\":\"<MESSAGE>\",\"concat\":\"<CONCAT>\"}");
		String postData = "";
		//String content="Hello harshi";
		String data = URLEncoder.encode(content, "UTF-8"); 
		//String user_id="optcult_pr";
		//String pwd="-MP59_lg";
		
		postData += this.queryString.replace("<USERNAME>", getUserID()).replace("<PWD>", URLEncoder.encode(getPwd(), "UTF-8"))
			     .replace("<SENDERID>", senderId).replace("<TO>", mobile)
			     .replace("<MESSAGE>", data).replace("<CONCAT>", content.trim().length() <= 160 ? "0" : "1");
		
		System.out.println("postData======>"+postData);
		
			 DefaultHttpClient httpClient = new DefaultHttpClient();
			 HttpPost postRequest = new HttpPost("https://api.equence.in/pushsms");
			 StringEntity input = new StringEntity(postData);
			 input.setContentType("application/json");
			 postRequest.setEntity(input);

			 HttpResponse response = httpClient.execute(postRequest);

			 if (response.getStatusLine().getStatusCode() != 201) {
			 throw new RuntimeException("Failed : HTTP error code : "
			 + response.getStatusLine().getStatusCode());
			 }

			 BufferedReader br = new BufferedReader(
			                         new InputStreamReader((response.getEntity().getContent())));

			 String output;
			 System.out.println("Output from Server .... \n");
			 while ((output = br.readLine()) != null) {
			 System.out.println(output);
			 }

			 httpClient.getConnectionManager().shutdown();
			   } catch (MalformedURLException e) {

			 e.printStackTrace();
			   } catch (IOException e) {

			 e.printStackTrace();

			   }
		return null;
	}
*/	
	public class PrepareEquenceJsonRequest {
		/*{
				"username":"****",
				"password":"****",
				"from":"SMSTST",
				"textlist":[                                                                                 
				                   {"to":"9206774674","text":"hi test message1"},
				                   {"to":"8895801942","text":"hi test message2"},
				                   {"to":"7377069728","text":"hi test message3"}]
	      }*/
		private String username; 
		private String password;
		private String from;
		private String peId;
		private String tmplId;
		private List<EquenceTextList> textlist; 
		private String MSGID;
		
		public PrepareEquenceJsonRequest(){
			//Default Constructor
		}
		
		public String getUsername() {
			return username;
		}
		
		public void setUsername(String username) {
			this.username = username;
		}
		
		public String getPassword() {
			return password;
		}
		
		public void setPassword(String password) {
			this.password = password;
		}
		
		public String getFrom() {
			return from;
		}
		
		public void setFrom(String from) {
			this.from = from;
		}
		
		public List<EquenceTextList> getTextlist() {
			return textlist;
		}
	
		public void setTextlist(List<EquenceTextList> textlist) {
			this.textlist = textlist;
		}

		public String getPeId() {
			return peId;
		}

		public void setPeId(String peId) {
			this.peId = peId;
		}

		public String getTmplId() {
			return tmplId;
		}

		public void setTmplId(String tmplId) {
			this.tmplId = tmplId;
		}
		public String getMSGID() {
			return MSGID;
		}

		public void setMSGID(String mSGID) {
			MSGID = mSGID;
		}
	}
	
	public class PrepareEquenceJsonResponse {

		/*{
		"username":"****",
		"password":"****",
		"from":"SMSTST",
		"textlist":[                                                                                 
		                   {"to":"9206774674","text":"hi test message1"},
		                   {"to":"8895801942","text":"hi test message2"},
		                   {"to":"7377069728","text":"hi test message3"}]
	}*/
		private String username; 
		private String password;
		private String from;
		private String peId;
		private List<EquenceTextList> textlist; 
		
		public PrepareEquenceJsonResponse(){
		//Default Constructor
		}
		
		public String getUsername() {
		return username;
		}
		
		public void setUsername(String username) {
		this.username = username;
		}
		
		public String getPassword() {
		return password;
		}
		
		public void setPassword(String password) {
		this.password = password;
		}
		
		public String getFrom() {
		return from;
		}
		
		public void setFrom(String from) {
		this.from = from;
		}
		
		public List<EquenceTextList> getTextlist() {
		return textlist;
		}
		
		public void setTextlist(List<EquenceTextList> textlist) {
		this.textlist = textlist;
		}

		public String getPeId() {
			return peId;
		}

		public void setPeId(String peId) {
			this.peId = peId;
		}
		}

	/*public class SampleEqResponse extends BaseRequestObject {

		
		private String mrId; 
		private String id;
		private String seg;
		private String destination;
		private String dispatch;
		private String segment;
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getSeg() {
			return seg;
		}

		public void setSeg(String seg) {
			this.seg = seg;
		}

		public String getDestination() {
			return destination;
		}

		public void setDestination(String destination) {
			this.destination = destination;
		}

		public String getDispatch() {
			return dispatch;
		}

		public void setDispatch(String dispatch) {
			this.dispatch = dispatch;
		}

		public String getSegment() {
			return segment;
		}

		public void setSegment(String segment) {
			this.segment = segment;
		}
		
		public SampleEqResponse(){
		//Default Constructor
		}

		public String getMrId() {
			return mrId;
		}

		public void setMrId(String mrId) {
			this.mrId = mrId;
		}
		
				}
*/	
	public class EquenceTextList {

		private String to;
		private String text;
		
		public String getTo() {
			return to;
		}
		
		public void setTo(String to) {
			this.to = to;
		}
		
		public String getText() {
			return text;
		}
		
		public void setText(String text) {
			this.text = text;
		}
	}
			
	public void prepareData(String userID, String pwd, String senderID, String messageContent, String toMobNum,String templateRegisteredId) throws Exception {
	messageContent = messageContent.replace("|^", "[").replace("^|", "]");
	
	logger.info("Before test messageContent: "+messageContent);
	
	messageContent = replaceBarcodePhWithDummyCode(messageContent);//we can shift this method to other place
	
		String[] mobileArray =  toMobNum.split(",");
		for(String oneMobile : mobileArray){
			oneMobile = oneMobile.trim();
			sendTestSMS(messageContent,oneMobile,templateRegisteredId, null);
		}
    }
	
	/*public void testPost(String user_id,String pwd,String senderId,String toMobNum,String content) throws Exception{
		String queryString = new String("{\"username\":\"<USERNAME>\",\"password\":\"<PWD>\",\"to\":\"<TO>\",\"from\":\"<SENDERID>\",\"text\":\"<MESSAGE>\",\"concat\":\"<CONCAT>\"}");
		String postData = "";
		String data = URLEncoder.encode(content, "UTF-8"); 
		
		postData += queryString.replace("<USERNAME>", user_id ).replace("<PWD>",  URLEncoder.encode(pwd, "UTF-8"))
				.replace("<SENDERID>", "OPTCLT").replace("<TO>", "123,456")
				.replace("<MESSAGE>", data).replace("<CONCAT>", content.trim().length() <= 160 ? "0" : "1");
		
		System.out.println("postData======>"+postData);
		
		 try{
			 DefaultHttpClient httpClient = new DefaultHttpClient();
			 HttpPost postRequest = new HttpPost("https://api.equence.in/pushsms");
			 StringEntity input = new StringEntity(postData);
			 input.setContentType("application/json");
			 postRequest.setEntity(input);

			 HttpResponse response = httpClient.execute(postRequest);

			 if (response.getStatusLine().getStatusCode() != 201) {
			 throw new RuntimeException("Failed : HTTP error code : "
			 + response.getStatusLine().getStatusCode());
			 }

			 BufferedReader br = new BufferedReader(
			                         new InputStreamReader((response.getEntity().getContent())));

			 String output;
			 System.out.println("Output from Server .... \n");
			 while ((output = br.readLine()) != null) {
			 System.out.println(output);
			 }

			 httpClient.getConnectionManager().shutdown();
			   } catch (MalformedURLException e) {

			 e.printStackTrace();
			   } catch (IOException e) {

			 e.printStackTrace();

			   }
	}*/
	private String replaceBarcodePhWithDummyCode(String smsMsg){
		
		try{
			CouponsDao couponsDao = null;
			Set<String> coupPhSet = Utility.findCoupPlaceholders(smsMsg);
		
			if(coupPhSet != null  && coupPhSet.size() > 0) {
			
				couponsDao = (CouponsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
				
				Iterator<String> iter = coupPhSet.iterator();
				String couponPh = null;
				Coupons coupon = null;
				
				String appShortUrl = PropertyUtil.getPropertyValue(Constants.APP_SHORTNER_URL).trim();
				
				while(iter.hasNext()){
					couponPh = iter.next();
					logger.debug("sms cc ph: "+couponPh);
					String searchBarcodePhStr = appShortUrl+"["+couponPh+"]";
					
					if(smsMsg.contains(searchBarcodePhStr)){
						String[] ccPhTokens = couponPh.split("_");
						String testBarcode = "Sample";		
						//coupon = couponsDao.findCouponsByIdAndName(Long.parseLong(ccPhTokens[1].trim()),ccPhTokens[2].trim());
						coupon = couponsDao.findCouponsById(Long.parseLong(ccPhTokens[1].trim()));
						if(coupon == null){
							continue;
						}
						else{
							logger.debug("coupon: "+coupon+" "+coupon.getBarcodeType()+" ");
							String barcodeStr = null;
							if(coupon.getBarcodeType().trim().equals("LN")){
								barcodeStr = "L"+testBarcode;
							}
							else if(coupon.getBarcodeType().trim().equals("QR")){
								barcodeStr = "Q"+testBarcode;
							}
							else if(coupon.getBarcodeType().trim().equals("DM")){
								barcodeStr = "D"+testBarcode;
							}
							else if(coupon.getBarcodeType().trim().equals("AZ")){
								barcodeStr = "A"+testBarcode;
							}
							
							String barcodeLink  = appShortUrl+barcodeStr;
							smsMsg = smsMsg.replace(searchBarcodePhStr, barcodeLink);
							
							logger.info("searchBarcodePhStr :"+ searchBarcodePhStr);
							logger.info("Test sms barcodeLink: "+barcodeLink);
						}//else
					}//if
					
				}//while
			}//if
		}catch(Exception e){
			logger.error("Exception in replacing barcode placeholder", e);
		}
		return smsMsg;
	}
	/*public static void main(String[] args) throws Exception{
				
			PrepareEquenceJsonRequest responseJson = null;
				Gson gson = new Gson();
				EquenceSMSGateway eq=new EquenceSMSGateway();
				PrepareEquenceJsonRequest pj=eq.new PrepareEquenceJsonRequest();
				pj.setUsername("optcult_otr");
				pj.setPassword("_JC64_ta");
				pj.setFrom("OPTCLT");
				
				String[] mobilenumb={"9550808880"};
				System.out.println("mobilenumb length "+mobilenumb.length);
				String content="hai";
				//String[] textlist = {"9550808880","1","200","1","300","3"};
				List<EquenceTextList> toText = new ArrayList<EquenceTextList>();
				for(int i=0;i<mobilenumb.length;i++){
					EquenceTextList tl = eq.new EquenceTextList();
					System.out.println("mobile num "+mobilenumb[i]);
					tl.setTo(mobilenumb[i]);
					tl.setText(content);
					toText.add(tl);
				}
				pj.setTextlist(toText);
				
				
				//responseJson = gson.toJson(pj);
				String request = gson.toJson(pj);
				responseJson = gson.fromJson(request,PrepareEquenceJsonRequest.class);
				System.out.println("requestJson--->"+request);
				System.out.println("responseJson--->"+responseJson);
				//String queryString = new String("{\"username\":\"<USERNAME>\",\"password\":\"<PWD>\",\"to\":\"<TO>\",\"from\":\"<SENDERID>\",\"text\":\"<MESSAGE>\",\"concat\":\"<CONCAT>\"}");
				//String postData = "";
				 try{
					 DefaultHttpClient httpClient = new DefaultHttpClient();
					 HttpPost postRequest = new HttpPost("https://api.equence.in/pushsms");
					 StringEntity input = new StringEntity(gson.toJson(responseJson));
					 input.setContentType("application/json");
					 postRequest.setEntity(input);

					 HttpResponse response = httpClient.execute(postRequest);

					 if (response.getStatusLine().getStatusCode() != 201) {
					 throw new RuntimeException("Failed : HTTP error code : "
					 + response.getStatusLine().getStatusCode());
					 }

					 BufferedReader br = new BufferedReader(
					                         new InputStreamReader((response.getEntity().getContent())));

					 String output;
					 System.out.println("Output from Server .... \n");
					 while ((output = br.readLine()) != null) {
					 System.out.println(output);
					 }

					 httpClient.getConnectionManager().shutdown();
					   } catch (MalformedURLException e) {

					 e.printStackTrace();
					   } catch (IOException e) {

					 e.printStackTrace();

					   }
	}*/
	
	
	/*public static void main(String[] args) throws Exception{
		//post method
		
		String queryString = new String("{\"username\":\"<USERNAME>\",\"password\":\"<PWD>\",\"to\":\"<TO>\",\"from\":\"<SENDERID>\",\"text\":\"<MESSAGE>\",\"concat\":\"<CONCAT>\"}");
		String postData = "";
		String content="Hello harshi";
		String data = URLEncoder.encode(content, "UTF-8"); 
		String user_id="optcult_pr";
		String pwd="-MP59_lg";
		
		postData += queryString.replace("<USERNAME>", user_id ).replace("<PWD>",  URLEncoder.encode(pwd, "UTF-8"))
				.replace("<SENDERID>", "OPTCLT").replace("<TO>", "123,456")
				.replace("<MESSAGE>", data).replace("<CONCAT>", content.trim().length() <= 160 ? "0" : "1");
		
		System.out.println("postData======>"+postData);
		
		 try{
			 DefaultHttpClient httpClient = new DefaultHttpClient();
			 HttpPost postRequest = new HttpPost("https://api.equence.in/pushsms");
			 StringEntity input = new StringEntity(postData);
			 input.setContentType("application/json");
			 postRequest.setEntity(input);

			 HttpResponse response = httpClient.execute(postRequest);

			 if (response.getStatusLine().getStatusCode() != 201) {
			 throw new RuntimeException("Failed : HTTP error code : "
			 + response.getStatusLine().getStatusCode());
			 }

			 BufferedReader br = new BufferedReader(
			                         new InputStreamReader((response.getEntity().getContent())));

			 String output;
			 System.out.println("Output from Server .... \n");
			 while ((output = br.readLine()) != null) {
			 System.out.println(output);
			 }

			 httpClient.getConnectionManager().shutdown();
			   } catch (MalformedURLException e) {

			 e.printStackTrace();
			   } catch (IOException e) {

			 e.printStackTrace();

			   }
	}
*/	
}
