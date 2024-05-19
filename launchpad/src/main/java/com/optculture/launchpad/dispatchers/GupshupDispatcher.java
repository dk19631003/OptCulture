package com.optculture.launchpad.dispatchers;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.optculture.shared.entities.communication.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.optculture.launchpad.repositories.CommunicationTemplateRepository;
import com.optculture.shared.entities.contact.Contact;
//import com.optculture.shared.events.CommunicationEventHandler;
import com.optculture.launchpad.configs.CommunicationEventHandler;
import com.optculture.shared.util.Constants;


@Component("GUPSHUP")
public class GupshupDispatcher implements ChannelDispatcher {

	@Autowired
	CommunicationEventHandler communicationEventHandler;

	Logger logger = LoggerFactory.getLogger(GupshupDispatcher.class);

	@Autowired
	CommunicationTemplateRepository communicationTemplateRepository ;

	@Override
	public void dispatch(String msgContent, Contact contactobj, ChannelAccount channelAccount,
			ChannelSetting channelSetting, UserChannelSetting userChannelSetting, Communication communicationObj, CustomCommunication in)
					throws IOException {

		try {

			logger.debug("----------< Entered Gupshup Dispatcher >----------");

			//			String mobile = Utility.phoneParse(mobileNumber.trim(), user.getUserOrganization());
			//			String countryCarrier = user.getCountryCarrier() + Constants.STRING_NILL;
			//			if (mobile.startsWith(countryCarrier) && mobile.length() > user.getUserOrganization().getMinNumberOfDigits()) {
			//
			//				mobile = mobile.substring(countryCarrier.length());// needed for CM
			//
			//			}

			//String mobile = contactobj.getMobilePhone();

			//prepare the URI to process the GET request
			URI uri = generateFinalURI(msgContent, contactobj, channelAccount, channelSetting, userChannelSetting, communicationObj, in);
			
			if(uri == null) return;
			
			logger.debug("Final URL is ::\n"+uri);

			HttpClient httpClient = HttpClients.createDefault();

			HttpGet httpGet = new HttpGet(uri);

			HttpResponse response = httpClient.execute(httpGet);

			int statusCode = response.getStatusLine().getStatusCode();
			logger.debug("Response Status Code: " + statusCode);

			String responseBody = EntityUtils.toString(response.getEntity());
			logger.debug("Response Body: " + responseBody);

			String status = null;
			status = (statusCode == 200) ? "Sent" : "UnSent";

			//create the event object and Publish to rabbitMQ
			CommunicationEvent communicationEvent = null;
			communicationEvent = new CommunicationEvent(in.getCommReportId(),in.getCampId(),contactobj.getMobilePhone(),status,LocalDateTime.now(),
					Constants.STRING_NILL,communicationObj.getChannelType(),contactobj.getUserId(),contactobj.getContactId());

			communicationEventHandler.createCommunicationEvent(communicationEvent);



		} catch (Exception e) {
			logger.error("Exception :: ", e);
		}


	}//dispatch

	public URI generateFinalURI(String msgContent, Contact contactobj, ChannelAccount channelAccount,
			ChannelSetting channelSetting, UserChannelSetting userChannelSetting, Communication communicationObj, CustomCommunication in)
					throws IOException {

		try {

			String apiEndPoint = channelSetting.getEndPoint(); //https://media.smsgupshup.com/GatewayAPI/rest
			String userid = channelAccount.getAccountName();//2000210942
			String pwd = channelAccount.getAccountPwd();

			CommunicationTemplate communicationTemplate = null;
			String footer = null;
			String msg_type = null;
			String header_text = null;
			String media_url = null;

			communicationTemplate = communicationTemplateRepository.findByTemplateId(communicationObj.getTemplateId());

			if(communicationTemplate != null) {
				footer = communicationTemplate.getFooter();
				msg_type = communicationTemplate.getMsgType();

				if(msg_type!=null && msg_type.equals("TEXT")) {
					header_text = communicationTemplate.getHeaderText();//if TEXT then keep static i.e. template level

				}else {//IMAGE/VIDEO/DOCUMENT
					media_url = communicationObj.getMediaUrl();//url at communication level
				}

			}else {
				logger.debug("No Template Configured for the Communication");
				return null;
			}

			URI uri = new URIBuilder(apiEndPoint)
					.addParameter("userid", userid)
					.addParameter("password", pwd)
					.addParameter("send_to", contactobj.getMobilePhone())
					.addParameter("v", "1.1")
					.addParameter("format", "json")
					.addParameter("msg_type", msg_type)//TEXT,IMAGE,VIDEO,DOCUMENT
					.addParameter("isTemplate", "true")
					.addParameter("auth_scheme", "PLAIN")
					.addParameter("wa_template_json", "{\"components\":[]}")//if 2 static btn URLs then must
					.addParameter("extra", 	in.getCommReportId() + Constants.DELIMETER_DOUBLECOLON + 
											in.getCampId() + Constants.DELIMETER_DOUBLECOLON + 
											contactobj.getContactId() + Constants.DELIMETER_DOUBLECOLON + 
											contactobj.getUserId())//pass cr_id::camp_id::cid::user_id to track delivery reports
					.build();

			if(msg_type!=null && msg_type.equals("TEXT")) {
				uri = new URIBuilder(uri)
						.addParameter("msg", msgContent)
						.addParameter("method", "SENDMESSAGE")
						.build();

			}else{//IMAGE,DOCUMENT,VIDEO
				uri = new URIBuilder(uri)
						.addParameter("caption", msgContent)
						.addParameter("method", "SENDMEDIAMESSAGE")
						.addParameter("media_url", media_url)
						.build();
			}

			if(header_text!=null && !header_text.isEmpty()) {
				uri = new URIBuilder(uri)
						.addParameter("header", header_text)
						.build();
			}

			if(footer!=null && !footer.isEmpty()) {
				uri = new URIBuilder(uri)
						.addParameter("footer", footer)
						.build();
			}

			return uri;

		} catch (Exception e) {
			logger.error("Exception :: ", e);
			return null;
		}


	}//dispatch

	@Override
	public String beforeMailMerge(Contact contact, Communication communication, ChannelAccount channelAccount,
								  UserChannelSetting userChannelSettingObj, CustomCommunication in) {

		//here {{1}},{{2}} in the message content will be replaced with corresponding placeholder mapping
		//if placeholder_mapping = ${contact.firstName ! 'N/A'}||${contact.lastName ! 'N/A'}, 
		//then {{1}} will be replaced with ${contact.firstName ! 'N/A'}, and {{2}} with ${contact.lastName ! 'N/A'}

		String placeholderMappings = communication.getPlaceholderMappings();//${coupon.CC_13232}||${contact.firstName ! 'N/A'}
		String msgContent = communication.getMessageContent();

		if(placeholderMappings != null && !placeholderMappings.isEmpty()){

			String placholders[] = placeholderMappings.split("\\|\\|",-1);

			Pattern pattern = Pattern.compile("\\{\\{([\\d]+)}}");// Regex to match variables like {{1}}, {{2}}, etc.
			Matcher m = pattern.matcher(msgContent);

			while (m.find()) {

				String ph = m.group(0); //{{1}},{{2}} ..
				//String phPositon = ph.replace("{{","").replace("}}","");// 1,2 ...
				String phPositon = m.group(1);// 1,2 ...

				int index=Integer.parseInt(phPositon)-1;//get index
				if(!placholders[index].isEmpty())
					msgContent = msgContent.replace(ph, placholders[index].trim());
			}//while
		}//if

		return msgContent;
	}

}
