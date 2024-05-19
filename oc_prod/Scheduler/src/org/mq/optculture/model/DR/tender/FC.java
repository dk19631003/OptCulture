package org.mq.optculture.model.DR.tender;

import javax.xml.bind.annotation.XmlElement;

public class FC {

	private String Amount;
	private String FCName;
	private String Take;
	private String Give;
	private String BaseTaken;
	
	public FC(){}
	
	

	public FC(String amount, String fCName, String take, String give, String baseTaken) {
		Amount = amount;
		FCName = fCName;
		Take = take;
		Give = give;
		BaseTaken = baseTaken;
	}



	public String getAmount() {
		return Amount;
	}
	@XmlElement(name = "Amount")
	public void setAmount(String amount) {
		Amount = amount;
	}
	public String getFCName() {
		return FCName;
	}
	@XmlElement(name = "FCName")
	public void setFCName(String fCName) {
		FCName = fCName;
	}
	public String getTake() {
		return Take;
	}
	@XmlElement(name = "Take")
	public void setTake(String take) {
		Take = take;
	}
	public String getGive() {
		return Give;
	}
	@XmlElement(name = "Give")
	public void setGive(String give) {
		Give = give;
	}
	public String getBaseTaken() {
		return BaseTaken;
	}
	@XmlElement(name = "BaseTaken")
	public void setBaseTaken(String baseTaken) {
		BaseTaken = baseTaken;
	}

	

}
