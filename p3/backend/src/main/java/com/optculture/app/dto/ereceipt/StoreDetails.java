package com.optculture.app.dto.ereceipt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreDetails {
	private String homeStoreId;
	private String storeName;
	private String storeManagerName;
	private String storeBrand;
	private String website;
	private String locality;
	private String city;
	private String state;
	private String country;
	private String zipCode;
	private String emailId;
	private String googleMapLink;
	private String mobileNo;
}
