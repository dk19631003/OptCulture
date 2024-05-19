package com.optculture.launchpad.dto;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunicationJSONDump {
	
	private String from;
	private String fromName;
	private String replyEmail;
	private String subject;
	private String replyToName;
	

}
