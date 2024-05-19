package com.optculture.shared.entities.communication;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomCommunication implements Serializable{
	
	private static final long serialVersionUID = 1L;
	// have to send reportId and sent Id to update.
	private Long campId;
	private Long contactId;
	private Long commReportId;
	private Long commSentId;
	//private String channelType;


}
