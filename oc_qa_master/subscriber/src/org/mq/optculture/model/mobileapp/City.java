package org.mq.optculture.model.mobileapp;

import java.util.List;

public class City {
	private String cityName;
	private List<Locality> locality;

	public List<Locality> getLocality() {
		return locality;
	}

	public void setLocality(List<Locality> locality) {
		this.locality = locality;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	
}
