package com.optculture.launchpad.dispatchers;

import java.util.List;

public class SynapseSMSList {
	
	private List<MessageParams> messageParams;
	
	public List<MessageParams> getMobileNumbers() {
		return messageParams ;
	}

	public void setMobileNumbers(List<MessageParams> messageParams) {
		this.messageParams = messageParams;
	}

	public class MessageParams{
		
		private String mobileNumber;
		
		public String getMobileNumber() {
			return mobileNumber;
		}
		public void setMobileNumber(String mobileNumber) {
			this.mobileNumber = mobileNumber;
		}
	}
}
