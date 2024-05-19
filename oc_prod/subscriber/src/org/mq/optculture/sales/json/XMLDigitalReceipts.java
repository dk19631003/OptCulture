package org.mq.optculture.sales.json;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DigitalReceipt_Requests")
public class XMLDigitalReceipts {

	private List<DRBody> drrequest;

	public List<DRBody> getDrrequest() {
		return drrequest;
	}
	@XmlElement(name = "DigitalReceipts")
	public void setDrrequest(List<DRBody> drrequest) {
		this.drrequest = drrequest;
	}
	
}
