package com.optculture.launchpad.dispatchers;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.optculture.shared.entities.communication.ChannelSetting;

import jakarta.mail.internet.MimeMessage;

@Component("AmazonSES")
class AmazonSESDispatcher extends AbstractEmailSenderDispatcher{

	String commGateway;
	@Value(value=("${environment}"))
	String environment;
	@Override
	void setAdditionalMsgProps(ChannelSetting channelSetting, Properties props) {
		return;
	}

	@Override
	MimeMessage prepareMessageHeader(MimeMessage message,String commReportId,String sentId, String userId,
			String source, String toEmail) {
		try {
			message.addHeader("X-OPT-CAMPAIGN-REPORT-ID",commReportId);
			message.addHeader("X-OPT-SOURCE",source);
			message.addHeader("X-OPT-USERID",userId);
			message.addHeader("X-OPT-SENTID",sentId);
			message.addHeader("X-OPT-CAMPAIGN-SOURCE",commGateway);

		 }catch(Exception e) {
			 logger.error("Exception while preparing header",e);
		 }
		return message;
	}

	

}
