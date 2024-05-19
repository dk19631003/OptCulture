package org.mq.optculture.model.updatesku;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="UPDATESKUREQUESTS")
public class UpdateSkuRequests {

	private List<UpdateSkuRequest> UPDATESKUREQUEST;

	public UpdateSkuRequests() {
	}

	public UpdateSkuRequests(List<UpdateSkuRequest> uPDATESKUREQUEST) {
		UPDATESKUREQUEST = uPDATESKUREQUEST;
	}

	public List<UpdateSkuRequest> getUPDATESKUREQUEST() {
		return UPDATESKUREQUEST;
	}

	@XmlElement(name="UPDATESKUREQUEST")
	public void setUPDATESKUREQUEST(List<UpdateSkuRequest> uPDATESKUREQUEST) {
		UPDATESKUREQUEST = uPDATESKUREQUEST;
	}
	
}
