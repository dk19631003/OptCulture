package org.mq.optculture.model.opySync;

import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.loyalty.Status;

public class OptSyncResponse extends BaseResponseObject {
	
	private Status STATUS;
	public Status getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(Status sTATUS) {
		STATUS = sTATUS;
	}
	public OptSyncResponse(){
		
		
	}
	public OptSyncResponse(Status sTATUS){
		
		STATUS= sTATUS;
	}

	

}
