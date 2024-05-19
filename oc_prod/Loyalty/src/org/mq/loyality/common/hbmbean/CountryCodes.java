package org.mq.loyality.common.hbmbean;

public class CountryCodes {
	
	
	private long countryCodesId;
	public long getCountryCodesId() {
		return countryCodesId;
	}
	public void setCountryCodesId(long countryCodesId) {
		this.countryCodesId = countryCodesId;
	}
	private String countryName;
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	private String callingCode;
	public String getCallingCode() {
		return callingCode;
	}
	public void setCallingCode(String callingCode) {
		this.callingCode = callingCode;
	}
	public String getGeoCode() {
		return geoCode;
	}
	public void setGeoCode(String geoCode) {
		this.geoCode = geoCode;
	}
	private String geoCode;

}
