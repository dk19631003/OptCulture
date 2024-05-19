package org.mq.optculture.model.loyalty;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ENROLLMENTREQS")
public class XMLEnrolments {

	private List<LoyaltyEnrollRequestObject> enrollRequest;

	public List<LoyaltyEnrollRequestObject> getEnrollRequest() {
		return enrollRequest;
	}
	@XmlElement(name = "ENROLLMENTREQ")
	public void setEnrollRequest(List<LoyaltyEnrollRequestObject> enrollRequest) {
		this.enrollRequest = enrollRequest;
	}
	
	
}
