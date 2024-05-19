package org.mq.optculture.model.loyalty;

import java.util.List;

public class LoyaltyDataResponse {
	private StoreLocationInfo STORELOCATIONINFO;
	private HeaderInfo HEADERINFO;
	private List<ContactsLoyaltyDetails> CUSTOMERINFO;
	private LoyaltyBasicInfo LOYALTYBASICINFO;
	private Status STATUS;
	
	public StoreLocationInfo getSTORELOCATIONINFO() {
		return STORELOCATIONINFO;
	}
	public void setSTORELOCATIONINFO(StoreLocationInfo sTORELOCATIONINFO) {
		STORELOCATIONINFO = sTORELOCATIONINFO;
	}
	public HeaderInfo getHEADERINFO() {
		return HEADERINFO;
	}
	public void setHEADERINFO(HeaderInfo hEADERINFO) {
		HEADERINFO = hEADERINFO;
	}
	public List<ContactsLoyaltyDetails> getCUSTOMERINFO() {
		return CUSTOMERINFO;
	}
	public void setCUSTOMERINFO(List<ContactsLoyaltyDetails> cUSTOMERINFO) {
		CUSTOMERINFO = cUSTOMERINFO;
	}
	public LoyaltyBasicInfo getLOYALTYBASICINFO() {
		return LOYALTYBASICINFO;
	}
	public void setLOYALTYBASICINFO(LoyaltyBasicInfo lOYALTYBASICINFO) {
		LOYALTYBASICINFO = lOYALTYBASICINFO;
	}
	public Status getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(Status sTATUS) {
		STATUS = sTATUS;
	}
}
