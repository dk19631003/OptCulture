package com.optculture.launchpad.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

	private String companyAddress ;
	// Cancating all this will give company address.
	private String addressOne;
	private String addressTwo;
	private String city;
	private String state;
	private String country;
	private String pinCode;
	private String phone;
	
	private String getCompanyAddress() {
		companyAddress = "";
		if(this.addressOne != null && !this.addressOne.isEmpty()) {
			this.companyAddress = addressOne;
		}if(this.addressTwo != null && !this.addressTwo.isEmpty()) {
		this.companyAddress = this.companyAddress +","+addressTwo;
		}if(this.city != null && !this.city.isEmpty()) {
			this.companyAddress = this.companyAddress+","+city;
		}if(this.state != null  && !this.state.isEmpty()) {
			this.companyAddress = this.companyAddress+","+state;
		}
		if(this.country != null && !this.country.isEmpty()) {
			this.companyAddress = this.companyAddress+","+country;
		}
		if(this.pinCode != null && !this.pinCode.isEmpty()) {
			this.companyAddress = this.companyAddress+","+pinCode;
		}
		if(this.phone != null && !this.phone.isEmpty()) {
			this.companyAddress = this.companyAddress+"|"+phone;
		}
		return this.companyAddress;
	}
	
}
