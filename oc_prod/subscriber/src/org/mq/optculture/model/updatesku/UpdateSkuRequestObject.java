package org.mq.optculture.model.updatesku;

import javax.xml.bind.annotation.XmlElement;

import org.mq.optculture.model.BaseRequestObject;

public class UpdateSkuRequestObject extends BaseRequestObject {
	private UpdateSkuRequest UPDATESKUREQUEST;

	public UpdateSkuRequestObject() {
	}

	public UpdateSkuRequestObject(UpdateSkuRequest uPDATESKUREQUEST) {
		UPDATESKUREQUEST = uPDATESKUREQUEST;
	}

	public UpdateSkuRequest getUPDATESKUREQUEST() {
		return UPDATESKUREQUEST;
	}

	@XmlElement(name="UPDATESKUREQUEST")
	public void setUPDATESKUREQUEST(UpdateSkuRequest uPDATESKUREQUEST) {
		UPDATESKUREQUEST = uPDATESKUREQUEST;
	}

}
