package org.mq.optculture.utils;

public enum WooCommerceDREnum {
	
			Items(1,null,"orderitems",null,null, false, false,"Items"),
			//item_pos(1,Items,null,"item_pos","#Item.LineNumber#", false, false,"ItemLine"),
			//total(1,Items,null,"total","#Item.Total#", false, true,"ExtPrc"),
			//alu(1,Items,null,"alu","#Item.ALU#", false, false,"ALU"),
			name(1,Items,null,"name","#Item.DCS#", false, false,"DCS"),
			//item_size(1,Items,null,"item_size","#Item.Size#", false, false,"Size"),
			//attribute(1,Items,null,"attribute","#Item.Attr#", false, false,"Attr"),
			price(1,Items,null,"price","#Item.UnitPrice#",false, true,"InvcItemPrc"),
			//original_price_Dec3(1,Items,null,"original_price","#Item.UnitPrice_DEC3#", true, true,"InvcItemPrc"),
			//price(1,Items,null,"price",null, false, true,"DocItemPrc"),
			//netPrice(1,Items,null,"netPrice","#Item.NetPrice#", false, true,"NetPrice"),
			//netPrice_DEC3(1,Items,null,"netPrice","#Item.NetPrice_DEC3#", true, true, "NetPrice"),
			//tax_percent(1,Items,null,"tax_percent","#Item.TaxPrc#", false, true,"TaxPrc"),
			//tax_percent_DEC3(1,Items,null,"tax_percent","#Item.TaxPrc_DEC3#", true, true,"TaxPrc"),
			//total_tax(1,Items,null,"total_tax","#Item.TaxAmount#",false, true,"DocItemOrigTax"),
			//tax_amount_DEC3(1,Items,null,"tax_amount","#Item.TaxAmount_DEC3#",true, true,"DocItemOrigTax"),
			quantity(1,Items,null,"quantity","#Item.Quantity#", false, false,"Qty"),
			//description(1,Items,null,"description","#Item.Description1#", false, false, "Desc1"),
			//item_description2(1,Items,null,"item_description2","#Item.Description2#", false, false,"Desc2"),
			//item_description3(1,Items,null,"item_description3","#Item.Desc3#", false, false,"Desc3"),
			//item_description4(1,Items,null,"item_description4","#Item.Description4#", false, false,"Desc4"),
			//discount_percent(1,Items,null,"discount_percent","#Item.DiscountPercent#", false, true, "DocItemDisc"),
			//discount_perc_DEC3(1,Items,null,"discount_perc","#Item.DiscountPercent_DEC3#", true, true,"DocItemDisc"),
			//discount_amount(1,Items,null,"discount_amountt","#Item.Discount#", false, true,"DocItemDiscAmt"),
			//discount_reason(1,Items,null,"discount_reason","#Item.DiscountReason#", false, false,"DiscountReason"),
			//note1(1,Items,null,"note1","#Item.Note1#", false, false,"ItemNote1"),
			//note2(1,Items,null,"note2","#Item.Note2#", false, false,"ItemNote2"),
			//note3(1,Items,null,"note3","#Item.Note3#", false, false,"ItemNote3"),
			//note4(1,Items,null,"note4",null, false, false),
			//note5(1,Items,null,"note5",null, false, false),
			//note6(1,Items,null,"note6",null, false, false),
			//note7(1,Items,null,"note7",null, false, false),
			//note8(1,Items,null,"note8",null, false, false),
			//note9(1,Items,null,"note9",null, false, false),
			//note10(1,Items,null,"note10",null, false, false),
			product_id(1,Items,null,"product_id","#Item.SerialNumber#", false, false,"SerialNum"),
			//ref_order_doc_sid(1,Items,null,"ref_order_doc_sid","#Item.RefDocSID#", false, false,"RefDocSID"),
			//fulfill_store_sbs_no(1,Items,null,"fulfill_store_sbs_no",null, false, false),
			store_id(1,Items,null,"store_id",null, false, false),
			//orig_store_number(1,Items,null,"orig_store_number","#Item.RefStoreCode#", false, false,"RefStoreCode"),//ref store
			//orig_subsidiary_number(1,Items,null,"orig_subsidiary_number","#Item.RefSubsidiaryNumber#", false, false,"RefSubsidiaryNumber"),//ref sbs
			//order_id(1,Items,null,"order_id","#Item.RefReceipt#", false, false,"RefReceipt"),
			//vendor_code(1,Items,null,"vendor_code","#Item.Vendor#", false, false,"VendorCode"),
			//scan_upc(1,Items,null,"scan_upc","#Item.UPC#", false, false,"UPC"),
			
			//receipt placeholders
			DRDocument(1,null,"orderdetails",null,null, false, false,"Receipt"),
			//tracking_number(1,DRDocument,null,"tracking_number","#Receipt.TrackingNum_LU#", false, false,"TrackingNum"),
			//order_id(1,DRDocument,null,"entity_id","#Receipt.ECOMOrderNo#", false, false,"ECOMOrderNo"),
			created_at(1,DRDocument,null,"created_at","#Receipt.Date#", false, false,"DocDate"),
			order_id(1,DRDocument,null,"order_id","#Receipt.Number#", false, false,"InvcNum"),
			//employee1_full_name(1,DRDocument,null,"employee1_full_name","#Receipt.SalesPerson#", false, false,"SalesPerson"),
			//bt_employee_id(1,DRDocument,null,"bt_employee_id","#Receipt.Associate#", false, false,"EmployeeID"),
			//workstation_number(1,DRDocument,null,"workstation_number","#Receipt.WS#", false, false,"Workstation"),
			total_line_items_quantity(1,DRDocument,null,"total_line_items_quantity","#Receipt.InvcTotQty#", false, false,"InvcTotalQty"),
			subtotal(1,DRDocument,null,"subtotal","#Receipt.Subtotal#", false, true,"Subtotal"),
			//base_subtotal(1,DRDocument,null,"base_subtotal","#Receipt.Subtotal_DEC3#", true, true,"Subtotal"),
			total_tax(1,DRDocument,null,"total_tax","#Receipt.TaxAmount#", false, true, "TaxAmount"),
			//transaction_total_tax_amt_DEC3(1,DRDocument,null,"transaction_total_tax_amt","#Receipt.TaxAmount_DEC3#", true, true,"TaxAmount"),
			total(1,DRDocument,null,"total","#Receipt.Amount#", false, true, "Total"),
			//transaction_total_amt_DEC3(1,DRDocument,null,"transaction_total_amt","#Receipt.Amount_DEC3#", true, true,"Total"),
			//discount_amount(1,DRDocument,null,"total_discount_amt","#Receipt.Discount#", false, true,"Discount"),
			//base_discount_amount(1,DRDocument,null,"base_discount_amt","#Receipt.Discount_DEC3#", true, true,"Discount"),
			//transaction_total_round_amt(1,DRDocument,null,"transaction_total_amt","#Receipt.InvcTotalRoundAmt#", false, true,"InvcTotalRoundAmt"),
			//transaction_total_round_amt_DEC3(1,DRDocument,null,"transaction_total_amt","#Receipt.InvcTotalRoundAmt_DEC3#", true, true,"InvcTotalRoundAmt"),
			
			//rounded_due_amt(1,DRDocument,null,"rounded_due_amt","#Receipt.InvcRoundAmt#", false, true,"InvcRoundAmt"),
			//rounded_due_amt_DEC3(1,DRDocument,null,"rounded_due_amt","#Receipt.InvcRoundAmt_DEC3#", true, true,"InvcRoundAmt"),
			code(1,DRDocument,null,"code","#Receipt.Coupon#", false, false,"CouponId"),
			//comments(1,DRDocument,null,"comments","#Receipt.InvcComment1#", false, false,"Comment1"),
			//comment2(1,DRDocument,null,"comment2","#Receipt.InvcComment2#", false, false,"Comment2"),
			increment_id(1,DRDocument,null,"increment_id","#Receipt.ID#", false, false,"DocSID"),
			phone(1,DRDocument,null,"phone","#Receipt.Mobile_Phone_No#", false, false,"Mobile_Phone_No"),
			//store_name(1,DRDocument,null,"store_name","#Store.Name#", false, false,"StoreName"),
			//store_address_line1(1,DRDocument,null,"store_address_line1","#Store.Heading1#", false, false,"StoreHeading1"),
			//store_address_line2(1,DRDocument,null,"store_address_line2","#Store.Heading2#", false, false,"StoreHeading2"),
			//store_address_line3(1,DRDocument,null,"store_address_line3","#Store.Heading3#", false, false,"StoreHeading3"),
			//store_address_line4(1,DRDocument,null,"store_address_line4","#Store.Heading4#", false, false,"StoreHeading4"),
			//store_address_line5(1,DRDocument,null,"store_address_line5","#Store.Heading5#", false, false,"StoreHeading5"),
			//store_phone1(1,DRDocument,null,"store_phone1","#Store.Phone1#", false, false,"StorePhoneNumber"),
			//store_address_zip(1,DRDocument,null,"store_address_zip","#Store.Heading6#", false, false,"StoreHeading6"),
			//cashier_name(1,DRDocument,null,"cashier_name","#Store.Cashier#", false, false,"Cashier"),
			//subsidiary_number(1,DRDocument,null,"subsidiary_number","#Store.SbsNo#", false, false, "SubsidiaryNumber"),
			//store_code(1,DRDocument,null,"store_code","#Store.StoreCode#", false, false,"StoreCode"),
			//fiscal_document_number(1,DRDocument,null,"fiscal_document_number","#Store.FiscalCode#", false, false,"FiscalCode"),
			customer_id(1,DRDocument,null,"customer_id","#BillTo.CustNumber#", false, false,"BillToCustNumber"),
			first_name(1,DRDocument,null,"customer_first_name","#BillTo.Name#", false, false,"BillToFName");
			
			


			
			private long fieldId;
			private WooCommerceDREnum parent;
			private String rootElementName;
			private String elementName;
			private String hashTag;
			private boolean decimal;
			private boolean number;
			private boolean checkVisibility;
			private String OptDRJsonEleName;
			
			

			public String getOptDRJsonEleName() {
				return OptDRJsonEleName;
			}

			public void setOptDRJsonEleName(String optDRJsonEleName) {
				OptDRJsonEleName = optDRJsonEleName;
			}

			private WooCommerceDREnum(long fieldId, WooCommerceDREnum parent, String rootElementName, String elementName,String hashTag, boolean decimal, boolean number) {
				this.fieldId = fieldId;
				this.parent = parent;
				this.rootElementName = rootElementName;
				this.elementName = elementName;
				this.hashTag = hashTag;
				this.decimal = decimal;
				this.number = number;
				
			}
			
			private WooCommerceDREnum(long fieldId, WooCommerceDREnum parent, String rootElementName, String elementName,String hashTag, boolean decimal, boolean number, boolean checkVisibility) {
				this.fieldId = fieldId;
				this.parent = parent;
				this.rootElementName = rootElementName;
				this.elementName = elementName;
				this.hashTag = hashTag;
				this.decimal = decimal;
				this.number = number;
				this.checkVisibility = checkVisibility;
				
			}
			private WooCommerceDREnum(long fieldId, WooCommerceDREnum parent, String rootElementName, String elementName,String hashTag, boolean decimal, boolean number, String OptDRJsonEleName) {
				this.fieldId = fieldId;
				this.parent = parent;
				this.rootElementName = rootElementName;
				this.elementName = elementName;
				this.hashTag = hashTag;
				this.decimal = decimal;
				this.number = number;
				this.OptDRJsonEleName = OptDRJsonEleName;
				
			}


			public long getFieldId() {
				return fieldId;
			}



			public void setFieldId(long fieldId) {
				this.fieldId = fieldId;
			}



			public WooCommerceDREnum getParent() {
				return parent;
			}



			public void setParent(WooCommerceDREnum parent) {
				this.parent = parent;
			}



			public String getRootElementName() {
				return rootElementName;
			}



			public void setRootElementName(String rootElementName) {
				this.rootElementName = rootElementName;
			}



			public String getElementName() {
				return elementName;
			}



			public void setElementName(String elementName) {
				this.elementName = elementName;
			}



			public String getHashTag() {
				return hashTag;
			}



			public void setHashTag(String hashTag) {
				this.hashTag = hashTag;
			}



			public boolean isDecimal() {
				return decimal;
			}



			public void setDecimal(boolean decimal) {
				this.decimal = decimal;
			}
			
			
		public static WooCommerceDREnum getEnumsByHashTag(String hashTag) {
				
				
			WooCommerceDREnum[] childEnum = WooCommerceDREnum.values();
				for (WooCommerceDREnum wooCommerceDREnum : childEnum) {
					//logger.info("parentEnum "+parentEnum+" segmentEnum "+segmentEnum);
					if(wooCommerceDREnum.getHashTag() == null ) continue;
					if(wooCommerceDREnum.getHashTag().equalsIgnoreCase(hashTag)) {
						
						return wooCommerceDREnum;
						
					}//if
					
				}//for
				
				return null;
				
				
			}



		public boolean isNumber() {
			return number;
		}



		public void setNumber(boolean number) {
			this.number = number;
		}

		public boolean isCheckVisibility() {
			return checkVisibility;
		}

		public void setCheckVisibility(boolean checkVisibility) {
			this.checkVisibility = checkVisibility;
		}

}
