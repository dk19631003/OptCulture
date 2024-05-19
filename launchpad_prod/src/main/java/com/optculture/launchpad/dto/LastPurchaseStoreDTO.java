package com.optculture.launchpad.dto;
/*
 * All these columns are related to contact's last purchase. 
 */
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LastPurchaseStoreDTO {
	// from the stores table.
	private String storeName;
	private String storeManager;
	private String storeAddress;
	private String storePhone;
	private String storeEmail;
	private String storeStreet;
	private String storeCity;
	private String storeState;
	private String storeZip;
	// can get from the sales object.
	private String salesDate;
	private String salesPrice;
	
}