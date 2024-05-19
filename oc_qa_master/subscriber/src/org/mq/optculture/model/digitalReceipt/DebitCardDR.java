package org.mq.optculture.model.digitalReceipt;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class DebitCardDR {
	private List<DRDebitCard> clsDebitcard;

	public List<DRDebitCard> getClsDebitcard() {
		return clsDebitcard;
	}
	@XmlElement(name = "clsDebitcard")
	public void setClsDebitcard(List<DRDebitCard> clsDebitcard) {
		this.clsDebitcard = clsDebitcard;
	}

}
