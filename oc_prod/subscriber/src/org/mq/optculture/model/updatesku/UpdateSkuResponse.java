package org.mq.optculture.model.updatesku;

import org.mq.optculture.model.HeaderInfo;
import org.mq.optculture.model.StatusInfo;
import org.mq.optculture.model.UserDetails;

public class UpdateSkuResponse {
	private HeaderInfo HEADERINFO;
	private StatusInfo STATUSINFO;
	private UserDetails USERDETAILS;
	private SkuInfo SKUINFO;
	public HeaderInfo getHEADERINFO() {
		return HEADERINFO;
	}
	public void setHEADERINFO(HeaderInfo hEADERINFO) {
		HEADERINFO = hEADERINFO;
	}
	public UserDetails getUSERDETAILS() {
		return USERDETAILS;
	}
	public void setUSERDETAILS(UserDetails uSERDETAILS) {
		USERDETAILS = uSERDETAILS;
	}
	public SkuInfo getSKUINFO() {
		return SKUINFO;
	}
	public void setSKUINFO(SkuInfo sKUINFO) {
		SKUINFO = sKUINFO;
	}
	public StatusInfo getSTATUSINFO() {
		return STATUSINFO;
	}
	public void setSTATUSINFO(StatusInfo sTATUSINFO) {
		STATUSINFO = sTATUSINFO;
	}
}
