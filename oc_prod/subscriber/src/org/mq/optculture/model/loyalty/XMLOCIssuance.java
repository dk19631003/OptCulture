package org.mq.optculture.model.loyalty;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceRequest;

@XmlRootElement(name = "OCLOYALTY_LOYALTYISSUANCEREQS")
public class XMLOCIssuance {

	private List<LoyaltyIssuanceRequest> issuanceRequest;
	
	public List<LoyaltyIssuanceRequest> getIssuanceRequest() {
		return issuanceRequest;
	}
	@XmlElement(name = "OCLoyaltyIssuanceReq")
	public void setIssuanceRequest(List<LoyaltyIssuanceRequest> issuanceRequest) {
		this.issuanceRequest = issuanceRequest;
	}
		
}
