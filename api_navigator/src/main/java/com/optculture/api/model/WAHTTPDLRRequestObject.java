package com.optculture.api.model;

import lombok.Data;

@Data
public class WAHTTPDLRRequestObject extends BaseRequestObject{
	
	private static final long serialVersionUID = -3551878764009000029L;

	
	private String messageID;
	private String mobileNumber;
	private String status;
	private Long crId;
	private Long campId;
	private Long userId;
	private Long contactId;

}
