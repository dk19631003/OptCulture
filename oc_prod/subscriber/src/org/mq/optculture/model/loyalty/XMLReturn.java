package org.mq.optculture.model.loyalty;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.mq.optculture.model.ocloyalty.LoyaltyOfflineReturnTransactionRequest;

@XmlRootElement(name = "OCLOYALTY_LOYALTYRETURNREQS")
public class XMLReturn {

	private List<LoyaltyOfflineReturnTransactionRequest> OCLoayaltyReturnReq;

	public List<LoyaltyOfflineReturnTransactionRequest> getOCLoayaltyReturnReq() {
		return OCLoayaltyReturnReq;
	}

	@XmlElement(name = "OCLoayaltyReturnReq")
	public void setOCLoayaltyReturnReq(
			List<LoyaltyOfflineReturnTransactionRequest> OCLoayaltyReturnReq) {
		this.OCLoayaltyReturnReq = OCLoayaltyReturnReq;
	}

	

}
