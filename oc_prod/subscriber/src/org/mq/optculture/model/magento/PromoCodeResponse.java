package org.mq.optculture.model.magento;

import java.util.List;

public class PromoCodeResponse {


	private List<PromoCodeDiscountInfo> promoCodeDiscountInfo;

	public PromoCodeResponse() {
	}

	public PromoCodeResponse(List<PromoCodeDiscountInfo> promoCodeDiscountInfo) {
		this.promoCodeDiscountInfo = promoCodeDiscountInfo;
	}

	public List<PromoCodeDiscountInfo> getPromoCodeDiscountInfo() {
		return promoCodeDiscountInfo;
	}

	public void setPromoCodeDiscountInfo(
			List<PromoCodeDiscountInfo> promoCodeDiscountInfo) {
		this.promoCodeDiscountInfo = promoCodeDiscountInfo;
	}
	

	
}
