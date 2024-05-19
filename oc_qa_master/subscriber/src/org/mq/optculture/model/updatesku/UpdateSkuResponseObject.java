package org.mq.optculture.model.updatesku;

import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.HeaderInfo;
import org.mq.optculture.model.StatusInfo;
import org.mq.optculture.model.UserDetails;

public class UpdateSkuResponseObject extends BaseResponseObject {
	
	private UpdateSkuResponse UPDATESKURESPONSE;

	public UpdateSkuResponseObject() {
	}

	public UpdateSkuResponseObject(UpdateSkuResponse uPDATESKURESPONSE) {
		UPDATESKURESPONSE = uPDATESKURESPONSE;
	}

	public UpdateSkuResponse getUPDATESKURESPONSE() {
		return UPDATESKURESPONSE;
	}

	public void setUPDATESKURESPONSE(UpdateSkuResponse uPDATESKURESPONSE) {
		UPDATESKURESPONSE = uPDATESKURESPONSE;
	}
	

}
