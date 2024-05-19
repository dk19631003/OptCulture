package org.mq.optculture.business.mobileapp;

import java.util.Set;

public class Filter {
	private Set<String> brand;
	private Set<String> subsidiaryName;
	public Set<String> getBrand() {
		return brand;
	}
	public void setBrand(Set<String> brandSet) {
		this.brand = brandSet;
	}
	public Set<String> getSubsidiaryName() {
		return subsidiaryName;
	}
	public void setSubsidiaryName(Set<String> subsidiaryName) {
		this.subsidiaryName = subsidiaryName;
	}
}
