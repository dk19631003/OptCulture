package com.optculture.launchpad.dispatchers;
import com.optculture.shared.entities.communication.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.optculture.shared.entities.contact.Contact;

@Component("Email")
public class MockEmailDispatcher implements ChannelDispatcher{

	Logger logger = LoggerFactory.getLogger(MockEmailDispatcher.class);

    
    @Override
    public void dispatch(String msg, Contact contactobj, ChannelAccount ocChannelAccount, ChannelSetting channelSetting, UserChannelSetting usechannelSetting, Communication communicationObj, CustomCommunication in) {
    	
    	logger.info("Entering EmailDispatcher.dispatch ....");
    	logger.info("email id ...."+( contactobj).getEmailId());
    	
    	logger.info("sending msg is ="+msg);
    	
    	logger.info("channel api key ="+ocChannelAccount.getApiKey());
    	
    	logger.info("end point  ="+channelSetting.getEndPoint());
    	
    	//validating gateway connections- check connection()
    	
    	//Creating request structure for sending.
    	
    	//sending Communication
    	
    	//close connection.
    	

    	
    }
}
