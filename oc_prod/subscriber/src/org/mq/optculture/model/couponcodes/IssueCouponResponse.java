package org.mq.optculture.model.couponcodes;

import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.couponcodes.StatusInfo;

public class IssueCouponResponse extends BaseResponseObject {
	
	private StatusInfo  status;
	public IssueCouponResponse() {
	}
	public IssueCouponResponse(StatusInfo status) {
		this.status = status;
	}
	public StatusInfo getStatus() {
		return status;
	}
	public void setStatus(StatusInfo status) {
		this.status = status;
	}	

}
