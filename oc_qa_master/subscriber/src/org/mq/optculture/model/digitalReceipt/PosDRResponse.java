package org.mq.optculture.model.digitalReceipt;

import org.mq.optculture.model.BaseResponseObject;

public class PosDRResponse extends BaseResponseObject{
	
	private ResponseInfo responseInfo;

	public ResponseInfo getResponseInfo() {
		return responseInfo;
	}

	public void setResponseInfo(ResponseInfo responseInfo) {
		this.responseInfo = responseInfo;
	}
	
}
