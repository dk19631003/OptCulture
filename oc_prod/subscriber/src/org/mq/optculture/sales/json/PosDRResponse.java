package org.mq.optculture.sales.json;

import org.mq.optculture.model.BaseResponseObject;

public class PosDRResponse extends BaseResponseObject{
	
	private static final long serialVersionUID = 1L;
	private ResponseInfo responseInfo;

	public ResponseInfo getResponseInfo() {
		return responseInfo;
	}

	public void setResponseInfo(ResponseInfo responseInfo) {
		this.responseInfo = responseInfo;
	}
	
}
