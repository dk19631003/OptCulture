package org.mq.optculture.model.loyalty;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "LOYALTYISSUANCEREQS")
public class XMLIssuance {

	private List<LoyaltyIssuanceRequestObject> issuanceRequest;
	
	public List<LoyaltyIssuanceRequestObject> getIssuanceRequest() {
		return issuanceRequest;
	}
	@XmlElement(name = "LOYALTYISSUANCEREQ")
	public void setIssuanceRequest(List<LoyaltyIssuanceRequestObject> issuanceRequest) {
		this.issuanceRequest = issuanceRequest;
	}
		
}
