package org.mq.optculture.model.digitalReceipt;

import org.mq.optculture.model.BaseResponseObject;

public class SendDRResponse extends BaseResponseObject{
	
	private SendDRResponseInfo RESPONSEINFO;

	public SendDRResponseInfo getRESPONSEINFO() {
		return RESPONSEINFO;
	}

	public void setRESPONSEINFO(SendDRResponseInfo rESPONSEINFO) {
		RESPONSEINFO = rESPONSEINFO;
	}

	
}
