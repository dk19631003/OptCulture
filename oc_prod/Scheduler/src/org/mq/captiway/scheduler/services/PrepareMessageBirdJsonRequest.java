package org.mq.captiway.scheduler.services;

import java.util.List;

public class PrepareMessageBirdJsonRequest {

	private List<MessageBirdTextList> messages;

	public List<MessageBirdTextList> getMessages() {
		return messages;
	}

	public void setMessages(List<MessageBirdTextList> messages) {
		this.messages = messages;
	}


	public  PrepareMessageBirdJsonRequest() {
		
	}
	
}
