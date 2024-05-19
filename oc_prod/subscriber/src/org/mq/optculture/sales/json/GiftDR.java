package org.mq.optculture.sales.json;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class GiftDR {
	private List<DRGift> clsGiftCertificate;

	public List<DRGift> getClsGiftCertificate() {
		return clsGiftCertificate;
	}
	@XmlElement(name = "clsGiftCertificate")
	public void setClsGiftCertificate(List<DRGift> clsGiftCertificate) {
		this.clsGiftCertificate = clsGiftCertificate;
	}
}
