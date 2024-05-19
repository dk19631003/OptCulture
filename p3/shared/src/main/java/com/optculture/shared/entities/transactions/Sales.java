package com.optculture.shared.entities.transactions;

import java.time.LocalDateTime;
import java.util.Calendar;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "retail_pro_sales")
public class Sales {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sales_id")
	private Long salesId;

	@Column(name = "list_id")
	private Long listId;

	@Column(name = "external_id")
	private Long externalId;

	@Column(name = "customer_id")
	private String customerId;

	@Column(name = "reciept_number")
	private String receiptNumber;

	@Column(name = "sales_date")
	private LocalDateTime salesDate;

	@Column(name = "quantity")
	private Double quantity;

	@Column(name = "sales_price")
	private Double salesPrice;

	@Column(name = "tax")
	private Double tax;

	@Column(name = "promo_code")
	private String promoCode;

	@Column(name = "store_number")
	private String storeNumber;

	@Column(name = "subsidiary_number")
	private String subsidiaryNumber;

	@Column(name = "sku")
	private String sku;

	@Column(name = "tender_type")
	private String tenderType;

	@Column(name = "udf1")
	private String udf1;

	@Column(name = "udf2")
	private String udf2;

	@Column(name = "udf3")
	private String udf3;

	@Column(name = "udf4")
	private String udf4;

	@Column(name = "udf5")
	private String udf5;

	@Column(name = "udf6")
	private String udf6;

	@Column(name = "udf7")
	private String udf7;

	@Column(name = "udf8")
	private String udf8;

	@Column(name = "udf9")
	private String udf9;

	@Column(name = "udf10")
	private String udf10;

	@Column(name = "udf11")
	private String udf11;

	@Column(name = "udf12")
	private String udf12;

	@Column(name = "udf13")
	private String udf13;

	@Column(name = "udf14")
	private String udf14;

	@Column(name = "udf15")
	private String udf15;

	@Column(name = "cid")
	private Long contactId;

	@ManyToOne
	@JoinColumn(name = "inventory_id")
	private Sku skuInventory;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "item_sid")
	private String itemSid;

	@Column(name = "doc_sid")
	private String docSid;

	@Column(name = "discount")
	private Double discount;
	
	public static Double getSalesNetAmount(Sales salesRecord) {
		return salesRecord.getSalesPrice() + salesRecord.getTax() - salesRecord.getDiscount();
	}

}
