package org.mq.optculture.model.DR.orion;

public class OrionMasterCard {

	private String CURRENCY_CODE ;
	private String TENDER_AMOUNT ;
	private String SOTENDER_AMOUNT ;
	private String RTENDER_AMOUNT ;

	public String getCURRENCY_CODE() {
		return CURRENCY_CODE;
	}
	public void setCURRENCY_CODE(String cURRENCY_CODE) {
		CURRENCY_CODE = cURRENCY_CODE;
	}
	public String getTENDER_AMOUNT() {
		return TENDER_AMOUNT;
	}
	public void setTENDER_AMOUNT(String tENDER_AMOUNT) {
		TENDER_AMOUNT = tENDER_AMOUNT;
	}
	public String getSOTENDER_AMOUNT() {
		return SOTENDER_AMOUNT;
	}
	public void setSOTENDER_AMOUNT(String sOTENDER_AMOUNT) {
		SOTENDER_AMOUNT = sOTENDER_AMOUNT;
	}
	public String getRTENDER_AMOUNT() {
		return RTENDER_AMOUNT;
	}
	public void setRTENDER_AMOUNT(String rTENDER_AMOUNT) {
		RTENDER_AMOUNT = rTENDER_AMOUNT;
	}

}
