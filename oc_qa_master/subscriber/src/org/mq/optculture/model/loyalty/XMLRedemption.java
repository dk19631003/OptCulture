package org.mq.optculture.model.loyalty;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "LOYALTYREDEMPTIONREQS")
public class XMLRedemption {

	private List<LoyaltyRedemptionRequestObject> redemptionRequest;
	
	public List<LoyaltyRedemptionRequestObject> getRedemptionRequest() {
		return redemptionRequest;
	}
	@XmlElement(name = "LOYALTYREDEMPTIONREQ")
	public void setRedemptionRequest(List<LoyaltyRedemptionRequestObject> redemptionRequest) {
		this.redemptionRequest = redemptionRequest;
	}
		
}
