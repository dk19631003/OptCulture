package org.mq.optculture.model.digitalReceipt;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Items {
	
	private List<DRItem> Items;
	
	public List<DRItem> getItems() {
		return Items;
	}
	@XmlElement(name = "Item")
	public void setItems(List<DRItem> items) {
		this.Items = items;
	}

	private String ItemCode;
	private String ItemDiscount;
	private String Quantity;

	public String getItemCode() {
		return ItemCode;
	}
	public void setItemCode(String itemCode) {
		ItemCode = itemCode;
	}
	public String getItemDiscount() {
		return ItemDiscount;
	}
	public void setItemDiscount(String itemDiscount) {
		ItemDiscount = itemDiscount;
	}
	public String getQuantity() {
		return Quantity;
	}
	public void setQuantity(String quantity) {
		Quantity = quantity;
	}

}
