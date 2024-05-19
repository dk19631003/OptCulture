package com.optculture.launchpad.dispatchers;

import java.util.Properties;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.optculture.shared.entities.communication.ChannelSetting;

import jakarta.mail.internet.MimeMessage;


@Component("SendGrid")
class SendGridDispatcher extends AbstractEmailSenderDispatcher{

	String commGateway;
	@Value(value=("${environment}"))
	String environment;
	@Override
	void setAdditionalMsgProps(ChannelSetting channelSetting, Properties props) {
		return;
	}
/*
 * created a header for unique args and sentIds and crIds 
 */
	@Override
	MimeMessage prepareMessageHeader(MimeMessage message, String commReportId, String sentId, 
		 String userId, String source,String toEmail) {
		try {
			
			JSONObject sendGridHeader = new JSONObject();
			JSONObject sendGridArgs ;

			sendGridArgs = new JSONObject();
			sendGridArgs.put("Email", source);
			sendGridArgs.put("crId",commReportId );
			sendGridArgs.put("userId", userId);
			sendGridArgs.put("serverName",environment );
			sendGridArgs.put("sentId",sentId);
			sendGridArgs.put("CampaignSource",commGateway);
			sendGridArgs.put("email",toEmail);

			
			sendGridHeader.put("unique_args", sendGridArgs);
			
			message.addHeader("X-SMTPAPI",sendGridHeader.toString());
			

		 }catch(Exception e) {
			 logger.error("Exception while preparing header",e);
		 }
		return message;
	}
}
