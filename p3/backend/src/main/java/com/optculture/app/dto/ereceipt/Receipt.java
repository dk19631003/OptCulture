package com.optculture.app.dto.ereceipt;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;

import com.optculture.shared.entities.transactions.Sales;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {
	private LocalDateTime salesDate;
	private String receiptNumber;
	private Double totalDiscount;
	private Double subTotal;
	private Double total;
	private String storeNumber;
	private Double invcTotalQty;
	private String docSid;
	private Double totalTax;
	private String downloadReceiptLink;

	public static Receipt of(List<Sales> salesRecords, String downloadReceiptLink) {

		ModelMapper modelMapper = new ModelMapper();
		Receipt receipt = modelMapper.map(salesRecords.get(0), Receipt.class);

		Double totalDiscount = 0.0;
		Double totalAmount = 0.0;
		Double totalTax = 0.0;
		Double totalQty = 0.0;

		for (Sales salesRecord : salesRecords) {
			totalAmount   += salesRecord.getSalesPrice() * salesRecord.getQuantity();
			totalDiscount += salesRecord.getDiscount();
			totalQty += salesRecord.getQuantity();
			totalTax += salesRecord.getTax();
		}
		receipt.setTotalDiscount(totalDiscount);
		receipt.setTotal(totalAmount - totalDiscount);
		receipt.setSubTotal(totalAmount - totalTax);
		receipt.setTotalTax(totalTax);
		receipt.setInvcTotalQty(totalQty);
		receipt.setDownloadReceiptLink(downloadReceiptLink);

		return receipt;
	}
}
