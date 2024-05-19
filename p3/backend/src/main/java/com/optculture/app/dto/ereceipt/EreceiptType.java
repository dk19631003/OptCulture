package com.optculture.app.dto.ereceipt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EreceiptType {
	private Loyalty loyalty;
	private Customer customer;
	private Branding branding;
	private Receipt receipt;
	private LineItem[] lineItem;
	private Tax tax;
	private TermsAndConditions termsAndConditions;
	private Tender tender;
	private Organization organization;
	private StoreDetails storeDetails;
	private Offers[] offersArray;
	private String components;
	private String customerToken;
	private String[] npsOptions;
	private boolean receiptFeedbackAvailable;

	public EreceiptType(Receipt receipt, LineItem[] lineItems, Tax tax, Tender tender) {
		this.receipt=receipt;
		this.lineItem=lineItems;
		this.tax=tax;
		this.tender=tender;
	}
}
