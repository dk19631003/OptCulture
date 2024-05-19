package org.mq.optculture.model.DR;

import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.digitalReceipt.SendDRResponseInfo;

public class DigitalReceiptResponse extends BaseResponseObject{

	private SendDRResponseInfo RESPONSEINFO;

	public SendDRResponseInfo getRESPONSEINFO() {
		return RESPONSEINFO;
	}

	public void setRESPONSEINFO(SendDRResponseInfo rESPONSEINFO) {
		RESPONSEINFO = rESPONSEINFO;
	}
	
}
