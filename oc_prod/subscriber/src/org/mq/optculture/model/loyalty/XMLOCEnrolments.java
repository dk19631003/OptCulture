package org.mq.optculture.model.loyalty;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.mq.optculture.model.ocloyalty.LoyaltyEnrollRequest;

@XmlRootElement(name = "OCLOYALTY_ENROLLMENTREQS")
public class XMLOCEnrolments {

	private List<LoyaltyEnrollRequest> enrollRequest;

	public List<LoyaltyEnrollRequest> getEnrollRequest() {
		return enrollRequest;
	}
	@XmlElement(name = "OCLoyaltyEnrollReq")
	public void setEnrollRequest(List<LoyaltyEnrollRequest> enrollRequest) {
		this.enrollRequest = enrollRequest;
	}
	
	
}
