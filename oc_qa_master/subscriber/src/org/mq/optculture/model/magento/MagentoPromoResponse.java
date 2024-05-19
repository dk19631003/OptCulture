package org.mq.optculture.model.magento;

import org.mq.optculture.model.BaseResponseObject;

public class MagentoPromoResponse extends BaseResponseObject{

	private PromoCodeResponse promoCodeResponse;
	private StatusInfo statusInfo;
	private HeaderInfo headerInfo;
	
	public StatusInfo getStatusInfo() {
		return statusInfo;
	}
	public void setStatusInfo(StatusInfo statusInfo) {
		this.statusInfo = statusInfo;
	}
	public HeaderInfo getHeaderInfo() {
		return headerInfo;
	}
	public void setHeaderInfo(HeaderInfo headerInfo) {
		this.headerInfo = headerInfo;
	}
	public PromoCodeResponse getPromoCodeResponse() {
		return promoCodeResponse;
	}
	public void setPromoCodeResponse(PromoCodeResponse promoCodeResponse) {
		this.promoCodeResponse = promoCodeResponse;
	}

}
