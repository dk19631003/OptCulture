package com.optculture.launchpad.dispatchers;

import com.optculture.shared.entities.communication.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.optculture.shared.entities.contact.Contact;

@Component("Mock SMS")
public class MockSmsDispatcher implements ChannelDispatcher{
	Logger logger = LoggerFactory.getLogger(MockSmsDispatcher.class);

    @Override
    public void dispatch(String msg, Contact contactobj, ChannelAccount ocChannelAccount, ChannelSetting channelSetting, UserChannelSetting usechannelSetting, Communication communicationObj, CustomCommunication in) {
    	
    	logger.info("Entering SmsDispatcher.dispatch ....");
    	logger.info("contact number ...."+( contactobj).getMobilePhone());
    	logger.info("contactId is :"+( contactobj).getContactId());
    	logger.info("final msg content is :"+msg);

   
    	try {
            // Pause for 0.2 second (200 milliseconds)
            Thread.sleep(200);
        } catch (InterruptedException e) {
        		
        	logger.error("Thread sleep interrupted",e);       
        	
        }
    	logger.info("finished Mock SMS dispatcher");
    	//validating gateway connections- check connection()
    	
    	//checking credits and postpaid balance 
    	
    	//Creating request structure for sending.
    	
    	//sending Communication
    	
    	//close connection.
    	

    	
    }
	
}
