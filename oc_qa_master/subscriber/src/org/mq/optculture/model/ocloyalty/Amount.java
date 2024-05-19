package org.mq.optculture.model.ocloyalty;

import javax.xml.bind.annotation.XmlElement;

public class Amount {

	private String type;		//Reward or gift
	private String enteredValue;
	private String valueCode;
	private String receiptAmount;
	private String requestedType ;
	private String returnedAmount;
	private String ignoreIssuance;
	private String purchaseValue;
	
	
	public String getPurchaseValue() {
		return purchaseValue;
	}
	public void setPurchaseValue(String purchaseValue) {
		this.purchaseValue = purchaseValue;
	}
	public String getIgnoreIssuance() {
		return ignoreIssuance;
	}
	public void setIgnoreIssuance(String ignoreIssuance) {
		this.ignoreIssuance = ignoreIssuance;
	}
	public String getReturnedAmount() {
		return returnedAmount;
	}
	public void setReturnedAmount(String returnedAmount) {
		this.returnedAmount = returnedAmount;
	}
	public Amount() {
	}
	public String getEnteredValue() {
		return enteredValue;
	}
	@XmlElement(name = "enteredValue")	
	public void setEnteredValue(String enteredValue) {
		this.enteredValue = enteredValue;
	}
	public String getValueCode() {
		return valueCode;
	}
	@XmlElement(name = "valueCode")	
	public void setValueCode(String valueCode) {
		this.valueCode = valueCode;
	}
	public String getType() {
		return type;
	}
	@XmlElement(name = "type")	
	public void setType(String type) {
		this.type = type;
	}
	public String getReceiptAmount() {
		return receiptAmount;
	}
	@XmlElement(name = "receiptAmount")
	public void setReceiptAmount(String receiptAmount) {
		this.receiptAmount = receiptAmount;
	}
	public String getRequestedType() {
		return requestedType;
	}
	public void setRequestedType(String requestedType) {
		this.requestedType = requestedType;
	}
}
