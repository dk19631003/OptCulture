package org.mq.optculture.model.ocloyalty;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Discounts {

	private String appliedPromotion;
	private List<Promotion> promotions;
	
	public Discounts() {
	}

	public String getAppliedPromotion() {
		return appliedPromotion;
	}
	@XmlElement(name = "appliedPromotion")
	public void setAppliedPromotion(String appliedPromotion) {
		this.appliedPromotion = appliedPromotion;
	}

	public List<Promotion> getPromotions() {
		return promotions;
	}
	@XmlElement(name = "promotions")
	public void setPromotions(List<Promotion> promotions) {
		this.promotions = promotions;
	}
	
	
}
