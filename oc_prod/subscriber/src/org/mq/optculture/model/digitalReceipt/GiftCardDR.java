package org.mq.optculture.model.digitalReceipt;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class GiftCardDR {
	private List<DRGiftCard> clsGiftCard;

	public List<DRGiftCard> getClsGiftCard() {
		return clsGiftCard;
	}
	@XmlElement(name = "clsGiftCard")
	public void setClsGiftCard(List<DRGiftCard> clsGiftCard) {
		this.clsGiftCard = clsGiftCard;
	}

}
