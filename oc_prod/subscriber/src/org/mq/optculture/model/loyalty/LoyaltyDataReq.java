package org.mq.optculture.model.loyalty;

public class LoyaltyDataReq {
	private HeaderInfo HEADERINFO;
	private StoreLocationInfo STORELOCATIONINFO;
	private LoyaltyInfo LOYALTYINFO;
	private UserInfo USERDETAILS;
	
	public HeaderInfo getHEADERINFO() {
		return HEADERINFO;
	}
	public void setHEADERINFO(HeaderInfo hEADERINFO) {
		HEADERINFO = hEADERINFO;
	}
	public StoreLocationInfo getSTORELOCATIONINFO() {
		return STORELOCATIONINFO;
	}
	public void setSTORELOCATIONINFO(StoreLocationInfo sTORELOCATIONINFO) {
		STORELOCATIONINFO = sTORELOCATIONINFO;
	}
	public LoyaltyInfo getLOYALTYINFO() {
		return LOYALTYINFO;
	}
	public void setLOYALTYINFO(LoyaltyInfo lOYALTYINFO) {
		LOYALTYINFO = lOYALTYINFO;
	}
	public UserInfo getUSERDETAILS() {
		return USERDETAILS;
	}
	public void setUSERDETAILS(UserInfo uSERDETAILS) {
		USERDETAILS = uSERDETAILS;
	}
}
