package com.optculture.shared.entities.communication;

import java.io.Serializable;
import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommunicationEvent  implements Serializable{

	private long crId;
	private long campaignId;
	private String recipient;
	private String eventType;
	private LocalDateTime eventDate;
	
	private String apiMsgId;
	private String channelType;
	private long userId;
	private long contactId;


	public CommunicationEvent(String recipient, String eventType, LocalDateTime eventDate) {
		super();
		this.recipient = recipient;
		this.eventType = eventType;
		this.eventDate = eventDate;
	}
}
