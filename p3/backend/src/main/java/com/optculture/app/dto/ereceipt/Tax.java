package com.optculture.app.dto.ereceipt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tax {
	private String tax;
	private Double totalTax;
	private String hsnCode;
	private String taxDescription;
	private String iGstRate;
	private String iGstAmt;
	private String cGstRate;
	private String cGstAmt;
	private String sGstRate;
	private String sGstAmt;
	private String cessRate;
	private String cessAmt;
}
