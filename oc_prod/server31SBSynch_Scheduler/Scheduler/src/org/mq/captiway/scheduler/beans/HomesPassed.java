package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

public class HomesPassed {
	private Long userId;
	private Calendar createdDate;
	private Calendar lastMailDate;
	private Calendar modifiedDate;
	private String addressOne;
	private String addressTwo;
	private String street;
	private String area;
	private String city;
	
	private String district;
	
	private String state;
	private String zip;
	
	private String country;
	private Long hpId;
	private String status;
	private Calendar lastStatusChange;
	private Long listId;
	
	
	
	private String udf1;
	private String udf2;
	private String udf3;
	private String udf4;
	private String udf5;
	private String udf6;
	private String udf7;
	private String udf8;
	private String udf9;
	private String udf10;
	private String udf11;
	private String udf12;
	private String udf13;
	private String udf14;
	private String udf15;
	
	private Long addressUnitId;
	
	public HomesPassed() {}
	
	public HomesPassed(long listId,Calendar createdDate) {
		this.listId = listId;
		this.createdDate = createdDate;
	}
	
	
	public HomesPassed(Long userId,Calendar createdDate) {
		this.userId = userId;
		this.createdDate = createdDate;
	}
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getHpId() {
		return hpId;
	}
	public void setHpId(Long hpId) {
		this.hpId = hpId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	public Calendar getLastMailDate() {
		return lastMailDate;
	}
	public void setLastMailDate(Calendar lastMailDate) {
		this.lastMailDate = lastMailDate;
	}
	public String getAddressOne() {
		return addressOne;
	}
	public void setAddressOne(String addressOne) {
		this.addressOne = addressOne;
	}
	public String getAddressTwo() {
		return addressTwo;
	}
	public void setAddressTwo(String addressTwo) {
		this.addressTwo = addressTwo;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getUdf1() {
		return udf1;
	}
	public void setUdf1(String udf1) {
		this.udf1 = udf1;
	}
	public String getUdf2() {
		return udf2;
	}
	public void setUdf2(String udf2) {
		this.udf2 = udf2;
	}
	public String getUdf3() {
		return udf3;
	}
	public void setUdf3(String udf3) {
		this.udf3 = udf3;
	}
	public String getUdf4() {
		return udf4;
	}
	public void setUdf4(String udf4) {
		this.udf4 = udf4;
	}
	public String getUdf5() {
		return udf5;
	}
	public void setUdf5(String udf5) {
		this.udf5 = udf5;
	}
	public String getUdf6() {
		return udf6;
	}
	public void setUdf6(String udf6) {
		this.udf6 = udf6;
	}
	public String getUdf7() {
		return udf7;
	}
	public void setUdf7(String udf7) {
		this.udf7 = udf7;
	}
	public String getUdf8() {
		return udf8;
	}
	public void setUdf8(String udf8) {
		this.udf8 = udf8;
	}
	public String getUdf9() {
		return udf9;
	}
	public void setUdf9(String udf9) {
		this.udf9 = udf9;
	}
	public String getUdf10() {
		return udf10;
	}
	public void setUdf10(String udf10) {
		this.udf10 = udf10;
	}
	public String getUdf11() {
		return udf11;
	}
	public void setUdf11(String udf11) {
		this.udf11 = udf11;
	}
	public String getUdf12() {
		return udf12;
	}
	public void setUdf12(String udf12) {
		this.udf12 = udf12;
	}
	public String getUdf13() {
		return udf13;
	}
	public void setUdf13(String udf13) {
		this.udf13 = udf13;
	}
	public String getUdf14() {
		return udf14;
	}
	public void setUdf14(String udf14) {
		this.udf14 = udf14;
	}
	public String getUdf15() {
		return udf15;
	}
	public void setUdf15(String udf15) {
		this.udf15 = udf15;
	}
	
	public Long getListId() {
		return listId;
	}
	public void setListId(Long listId) {
		this.listId = listId;
	}
	public Calendar getLastStatusChange() {
		return lastStatusChange;
	}
	public void setLastStatusChange(Calendar lastStatusChange) {
		this.lastStatusChange = lastStatusChange;
	}
	public Long getAddressUnitId() {
		return addressUnitId;
	}
	public void setAddressUnitId(Long addressUnitId) {
		this.addressUnitId = addressUnitId;
	}

	public Calendar getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Calendar modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	
	
	
	
	
	

}