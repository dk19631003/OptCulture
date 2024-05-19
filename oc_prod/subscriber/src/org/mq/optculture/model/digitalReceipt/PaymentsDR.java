package org.mq.optculture.model.digitalReceipt;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class PaymentsDR {
	private List<DRPayments> clsPayments;

	public List<DRPayments> getClsPayments() {
		return clsPayments;
	}
	@XmlElement(name = "clsPayments")
	public void setClsPayments(List<DRPayments> clsPayments) {
		this.clsPayments = clsPayments;
	}

}
