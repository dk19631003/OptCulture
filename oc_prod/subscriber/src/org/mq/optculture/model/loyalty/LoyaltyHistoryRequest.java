package org.mq.optculture.model.loyalty;


public class LoyaltyHistoryRequest {
	private static final long serialVersionUID = 5190524497983077963L;
	
	private HeaderInfo HEADERINFO;
	private StoreLocationInfo STORELOCATIONINFO;
	private LoyaltyHistoryInfo LOYALTYINFO;
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
	public LoyaltyHistoryInfo getLOYALTYINFO() {
		return LOYALTYINFO;
	}
	public void setLOYALTYINFO(LoyaltyHistoryInfo lOYALTYINFO) {
		LOYALTYINFO = lOYALTYINFO;
	}
	public UserInfo getUSERDETAILS() {
		return USERDETAILS;
	}
	public void setUSERDETAILS(UserInfo uSERDETAILS) {
		USERDETAILS = uSERDETAILS;
	}
}
