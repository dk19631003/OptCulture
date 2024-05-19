package org.mq.optculture.model.mobileapp;

import java.util.List;

public class Locality {
	
	private String localityName;
	private List<Stores> stores;

	public List<Stores> getStores() {
		return stores;
	}

	public void setStores(List<Stores> stores) {
		this.stores = stores;
	}

	public String getLocalityName() {
		return localityName;
	}

	public void setLocalityName(String localityName) {
		this.localityName = localityName;
	}

}
