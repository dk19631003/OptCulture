package com.optculture.app.dto.ereceipt;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
	private String firstName;
	private String lastName;
	private String mobilePhone;
	private String emailId;
	private String addressOne;
	private String addressTwo;
	private String city;
	private String state;
	private String country;
	private String pinCode;
	private LocalDateTime birthDay;
	private LocalDateTime anniversary;
	private String gender;
	private Byte optin;
	private String udf11; // Birth Month
	private Boolean dobEnabled;
}
