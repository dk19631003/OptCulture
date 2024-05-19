package com.optculture.app.dto.ereceipt;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;

import com.optculture.shared.entities.transactions.Sales;
import com.optculture.shared.entities.transactions.Sku;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineItem {

	private Sku skuInventory;
	private Double quantity;
	private Double discount;
	private Double tax;
	private Double netAmount;
	private String itemPromoDisc;
	private String itemSid;
	

	public static LineItem[] of(List<Sales> salesRecords) {
		
		ModelMapper modelMapper = new ModelMapper();

		List<LineItem> listOfItems = new ArrayList<>();

		for (Sales salesRecord : salesRecords) {
			LineItem lineItem = modelMapper.map(salesRecord, LineItem.class);
			lineItem.setNetAmount(Sales.getSalesNetAmount(salesRecord));
			lineItem.getSkuInventory().setListPrice(salesRecord.getSalesPrice());
			
			listOfItems.add(lineItem);
		}

		return listOfItems.toArray(new LineItem[0]);
	}
}
