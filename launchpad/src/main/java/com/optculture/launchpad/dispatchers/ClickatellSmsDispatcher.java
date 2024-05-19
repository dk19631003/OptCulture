package com.optculture.launchpad.dispatchers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;


import com.optculture.launchpad.dispatchers.SynapseSMSList.MessageParams;
import com.optculture.launchpad.repositories.ScheduleRepository;
import com.optculture.shared.entities.communication.*;
import com.optculture.shared.entities.contact.Contact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("Clickatell")
public class ClickatellSmsDispatcher implements ChannelDispatcher {

    
	static Logger logger = LoggerFactory.getLogger(ClickatellSmsDispatcher.class);

	ScheduleRepository schrepo;
    
    public ClickatellSmsDispatcher(ScheduleRepository schrepo) {
		super();
		this.schrepo = schrepo;
	}

    List<MessageParams> textlist = null;
	List<String> smsStructureList;
	int smsCount;
	LinkedHashSet<String> sentIdsSet;

	public ClickatellSmsDispatcher() {
		smsStructureList = new ArrayList<>();
		smsCount = 0;
		sentIdsSet = new LinkedHashSet<>();
	}


    private final static String singleSmsStructure = "<clickAPI>" +
			"<sendMsg>" +
			"<api_id><APIID></api_id>" +
			/*"<user>optculture10</user>" +
			"<password>optculture10</password>"+
			"<from>888555</from>" +
			"<to>18322321356</to>" +
			"<text>test msg</text>" +
			"<climsgid>A;=;12323</climsgid>"+*/
			"<user><USERID></user>" +
			"<password><PWD></password>" +
			"<from><SENDERID></from>" +
			"<mo>1</mo>"+
			"<to><VAL_TO></to>" +
			"<text><VAL_TXT></text>" +
			"<callback>3</callback>"+
			"</sendMsg>" +
			"</clickAPI>";
    
   
    
       
	@Override
	public void dispatch(String msgContent, Contact contactobj, ChannelAccount channelAccount, ChannelSetting channelSetting, UserChannelSetting usechannelSetting, Communication communicationObj, CustomCommunication in)
			throws IOException {
	
		logger.trace("entering ClickaTell SMS dispatcher");

    	// need to add Escaping HtmlUtils
		
    	
		String to =contactobj.getMobilePhone();
        String userId= channelAccount.getAccountName();
        String pwd= channelAccount.getAccountPwd();
        String apiID= channelAccount.getApiKey();
        String text=msgContent;
        String senderID=usechannelSetting.getSenderId();
        
        if(to != null && to.length()==10 && !to.startsWith("1") ) {
			to = "1"+to;
		}
        try {	
		String sendContent = singleSmsStructure.replace("<USERID>", userId).replace("<PWD>",pwd).replace("<APIID>", apiID).
				replace("<VAL_TO>", to).replace("<VAL_TXT>", text).replace("<SENDERID>", senderID); 
		
			String respoString = sendSMS(sendContent);
			
			logger.info("response is======>{}",respoString);
			
			
			
        } catch (Exception e) {
        	logger.error("Exception caught {}: ",e.getMessage());
        }
	
        
	
    	
    }
	
	/**
	 * this method sends the SMS to NETCORE Gateway 
	 * @param tempData
	 */
	public static String sendSMS(String tempData) {
		String response = "";
		try {
			//append the  request parameter UserRequest along with the values to the url
			//capture the response as in Xml format which can be parsed further and get the GUID and required things further
			String postData = "";
			
			postData += "data="+URLEncoder.encode(tempData, "UTF-8");
			logger.info("Data to be sent is=====>{}",postData);
			
			URL url = new URL("http://api.clickatell.com/xml/xml");
			
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
			logger.info("response is======>{}",response);
			
			/***parse and update the status based on the initial response****/
			
	
		}catch(Exception e) {
			
				logger.error("Exception caught in catch block while sending sms from clickatell: {} " , e.getMessage());
			
		}
		return response;
	}//sendSMS
	
	

}
