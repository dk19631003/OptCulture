package org.mq.optculture.model.DR;

import org.mq.optculture.model.BaseResponseObject;

public class DigitalReceiptResponse extends BaseResponseObject{

	private SendDRResponseInfo RESPONSEINFO;

	public SendDRResponseInfo getRESPONSEINFO() {
		return RESPONSEINFO;
	}

	public void setRESPONSEINFO(SendDRResponseInfo rESPONSEINFO) {
		RESPONSEINFO = rESPONSEINFO;
	}
	
}
