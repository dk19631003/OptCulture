package org.mq.optculture.model;

import org.mq.optculture.model.opySync.OptSyncResponse;

public class OptSyncDataResponseObject  extends BaseResponseObject {
	
	private OptSyncResponse OPTSYNCRESPONSE;
	
	public OptSyncDataResponseObject(){
		
	}

	public OptSyncResponse getOPTSYNCRESPONSE() {
		return OPTSYNCRESPONSE;
	}

	public void setOPTSYNCRESPONSE(OptSyncResponse oPTSYNCRESPONSE) {
		OPTSYNCRESPONSE = oPTSYNCRESPONSE;
	}
	


	
	
}
