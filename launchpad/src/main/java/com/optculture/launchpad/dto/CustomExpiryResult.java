package com.optculture.launchpad.dto;

import org.springframework.stereotype.Service;

import lombok.Data;
import lombok.NoArgsConstructor;
@Service
@Data
@NoArgsConstructor
public class CustomExpiryResult {
	private String membershipNumber;
	private Long aggExpPoints;
	private Double aggExpAmt;
	
	public CustomExpiryResult(String membershipNumber,Long aggExpPoints,Double aggExpAmt) {
		this.membershipNumber = membershipNumber;
		this.aggExpPoints = aggExpPoints;
		this.aggExpAmt = aggExpAmt;
	}

}
