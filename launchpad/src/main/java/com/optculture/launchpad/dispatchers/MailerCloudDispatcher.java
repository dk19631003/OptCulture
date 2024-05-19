package com.optculture.launchpad.dispatchers;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;

import com.optculture.shared.entities.communication.ChannelSetting;

import jakarta.mail.internet.MimeMessage;


@Component("MailerCloud")
public class MailerCloudDispatcher extends AbstractEmailSenderDispatcher {
	
	private String commGateway;
	
	@Value(value=("${environment}"))
	String environment;
	
	@Override
	void setAdditionalMsgProps(ChannelSetting channelSetting, Properties props) {
		return;
	}

	@Override
	MimeMessage prepareMessageHeader(MimeMessage message, String commReportId, String sentId, 
			 String userId,String source, String toEmail) {
		try {	
		JSONObject mailerCloudArgs =new JSONObject();
        JSONObject metadata = new JSONObject();
        
        	mailerCloudArgs.put("crId", commReportId);
        	mailerCloudArgs.put("userId",userId);
        	mailerCloudArgs.put("sentId", sentId);
        	mailerCloudArgs.put("source", source);
        	mailerCloudArgs.put("serverName", environment);
        	mailerCloudArgs.put("CampaignSource", commGateway);
        	
        	metadata.put("metadata",mailerCloudArgs);
        	        	
        	message.addHeader("X-MSYS-API", metadata.toString());
        	
        	
		}catch(Exception e) {
			logger.error("Exception while preparing mailercloud header : ",e);
		}
		return message;
	}
}
