package org.mq.optculture.model.digitalReceipt;

import javax.xml.bind.annotation.XmlElement;

public class DRPayments {
	private String Amount;
	private String PaymentDate;
	private String Remark;
	
	public String getAmount() {
		return Amount;
	}
	@XmlElement(name = "Amount")
	public void setAmount(String amount) {
		Amount = amount;
	}
	public String getPaymentDate() {
		return PaymentDate;
	}
	@XmlElement(name = "PaymentDate")
	public void setPaymentDate(String paymentDate) {
		PaymentDate = paymentDate;
	}
	public String getRemark() {
		return Remark;
	}
	@XmlElement(name = "Remark")
	public void setRemark(String remark) {
		Remark = remark;
	}
	

}
