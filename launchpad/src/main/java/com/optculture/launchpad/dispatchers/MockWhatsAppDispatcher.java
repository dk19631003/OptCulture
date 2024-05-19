package com.optculture.launchpad.dispatchers;

import com.optculture.shared.entities.communication.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.optculture.shared.entities.contact.Contact;

@Component("WhatsApp")
public class MockWhatsAppDispatcher implements ChannelDispatcher{

	Logger logger = LoggerFactory.getLogger(MockWhatsAppDispatcher.class);
    @Override
    public void dispatch(String msg, Contact contactobj, ChannelAccount ocChannelAccount, ChannelSetting channelSetting, UserChannelSetting usechannelSetting, Communication communicationObj, CustomCommunication in) {
     
    	
    	logger.debug("<<<<<<<<<  Entered into WhatsApp Dispatcher >>>>>>>>");
    	
    	logger.debug("contact number ...."+( contactobj).getMobilePhone());
    	
    	logger.debug("sending msg is ="+msg);
    	
    	logger.debug("channel api key ="+ocChannelAccount.getApiKey());
    	
    	logger.debug("end point  ="+channelSetting.getEndPoint());
    	
    	//validating gateway connections- check connection()
    	
    	//Creating request structure for sending.
    	
    	//sending Communication
    	
    	//close connection.
    	

    	
    }
	
}
