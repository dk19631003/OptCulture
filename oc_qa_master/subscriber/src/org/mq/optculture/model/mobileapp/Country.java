package org.mq.optculture.model.mobileapp;

import java.util.List;

public class Country {
	private String countryName;
	private List<City> city;
	
	
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public List<City> getCity() {
		return city;
	}
	public void setCity(List<City> city) {
		this.city = city;
	}
	

}
