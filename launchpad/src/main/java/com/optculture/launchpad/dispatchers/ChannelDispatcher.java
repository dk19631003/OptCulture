package com.optculture.launchpad.dispatchers;

import com.optculture.shared.entities.communication.*;
import com.optculture.shared.entities.contact.Contact;

public interface ChannelDispatcher {

	void dispatch(String finalContent, Contact contactobj, ChannelAccount channelAccount, ChannelSetting channelSetting, UserChannelSetting usechannelSetting, Communication communicationObj, CustomCommunication in) throws Exception;
	
	//adding hooks
	default String beforeMailMerge(Contact contact, Communication communication, ChannelAccount channelAccount, UserChannelSetting userChannelSettingObj, CustomCommunication in) {
		return null;
	}//if any dispatcher needs, that can implement this method

	default void afterMailMerge(){}//As of now not using

}
