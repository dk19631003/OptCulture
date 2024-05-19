package org.mq.optculture.utils;

import java.util.ArrayList;
import java.util.List;

import org.mq.marketer.campaign.controller.contacts.SegmentEnum;

public enum PrismDREnum {

	
	Items(1,null,"items",null,null, false, false,"Items"),
	item_pos(1,Items,null,"item_pos","#Item.LineNumber#", false, false,"ItemLine"),
	item_lookup(1,Items,null,"item_lookup","#Item.Lookup#", false, false,"ItemLookup"),
	item_total(1,Items,null,"netPrice","#Item.Total#", false, true,"ExtPrc"),
	alu(1,Items,null,"alu","#Item.ALU#", false, false,"ALU"),
	dcs_code(1,Items,null,"dcs_code","#Item.DCS#", false, false,"DCS"),
	dcs_name(1,Items,null,"dcs_code","#Item.DCSName#", false, false,"DCSName"),
	item_size(1,Items,null,"item_size","#Item.Size#", false, false,"Size"),
	attribute(1,Items,null,"attribute","#Item.Attr#", false, false,"Attr"),
	original_price(1,Items,null,"original_price","#Item.UnitPrice#",false, true,"InvcItemPrc"),
	original_price_Dec3(1,Items,null,"original_price","#Item.UnitPrice_DEC3#", true, true,"InvcItemPrc"),
	doc_original_price(1,Items,null,"original_price","#Item.OriginalPrice#",false, true,"DocItemOrigPrc"),
	doc_original_price_Dec3(1,Items,null,"original_price","#Item.OriginalPrice_DEC3#", true, true,"DocItemOrigPrc"),
	//clerk(1,Items,null,"clerk","#Item.Associate#", false, false,"Clerk"),
	
	price(1,Items,null,"price","#Item.Price#", false, true,"DocItemPrc"),
	netPrice(1,Items,null,"netPrice","#Item.NetPrice#", false, true,"NetPrice"),
	netPrice_DEC3(1,Items,null,"netPrice","#Item.NetPrice_DEC3#", true, true, "NetPrice"),
	extPrice(1,Items,null,"netPrice","#Item.ExtPrice#", false, true,"NetPrice"),
	extOrigPrice(1,Items,null,"netPrice","#Item.ExtOriginalPrice#", false, true,"NetPrice"),
	extPrice_DEC3(1,Items,null,"netPrice","#Item.ExtPrice_DEC3#", true, true, "NetPrice"),
	tax_percent(1,Items,null,"tax_percent","#Item.TaxPrc#", false, true,"TaxPrc"),
	tax_percent_DEC3(1,Items,null,"tax_percent","#Item.TaxPrc_DEC3#", true, true,"TaxPrc"),
	tax_amount(1,Items,null,"tax_amount","#Item.TaxAmount#",false, true,"DocItemOrigTax"),
	tax_amount_DEC3(1,Items,null,"tax_amount","#Item.TaxAmount_DEC3#",true, true,"DocItemOrigTax"),
	quantity(1,Items,null,"quantity","#Item.Quantity#", false, false,"Qty"),
	item_description1(1,Items,null,"item_description1","#Item.Description1#", false, false, "Desc1"),
	item_description2(1,Items,null,"item_description2","#Item.Description2#", false, false,"Desc2"),
	item_description3(1,Items,null,"item_description3","#Item.Desc3#", false, false,"Desc3"),
	item_description4(1,Items,null,"item_description4","#Item.Description4#", false, false,"Desc4"),
	discount_perc(1,Items,null,"discount_perc","#Item.DiscountPercent#", false, true, "DocItemDisc"),
	discount_perc_DEC3(1,Items,null,"discount_perc","#Item.DiscountPercent_DEC3#", true, true,"DocItemDisc"),
	discount_amt(1,Items,null,"discount_amt","#Item.Discount#", false, true,"DocItemDiscAmt"),
	disc_amt(1,Items,null,"discount_amt","#Item.DiscountAmt#", false, true,"DocItemDiscAmt"),
	
	
	discount_reason(1,Items,null,"discount_reason","#Item.DiscountReason#", false, false,"DiscountReason"),
	note1(1,Items,null,"note1","#Item.Note1#", false, false,"ItemNote1"),
	note2(1,Items,null,"note2","#Item.Note2#", false, false,"ItemNote2"),
	note3(1,Items,null,"note3","#Item.Note3#", false, false,"ItemNote3"),
	note4(1,Items,null,"note4",null, false, false,"ItemNote4"),
	note5(1,Items,null,"note5",null, false, false, "ItemNote5"),
	note6(1,Items,null,"note6",null, false, false, "ItemNote6"),
	note7(1,Items,null,"note7",null, false, false, "ItemNote7"),
	note8(1,Items,null,"note8",null, false, false, "ItemNote8"),
	note9(1,Items,null,"note9",null, false, false, "ItemNote9"),
	note10(1,Items,null,"note10",null, false, false, "ItemNote10"),
	employee1_login_name(1,Items,null,"employee1_login_name","#Item.Associate#", false, false,"Clerk"),
	serial_number(1,Items,null,"serial_number","#Item.SerialNumber#", false, false,"SerialNum"),
	ref_order_doc_sid(1,Items,null,"ref_order_doc_sid","#Item.RefDocSID#", false, false,"RefDocSID"),
	fulfill_store_sbs_no(1,Items,null,"fulfill_store_sbs_no",null, false, false),
	fulfill_store_no(1,Items,null,"fulfill_store_no",null, false, false),
	orig_store_number(1,Items,null,"orig_store_number","#Item.RefStoreCode#", false, false,"RefStoreCode"),//ref store
	orig_subsidiary_number(1,Items,null,"orig_subsidiary_number","#Item.RefSubsidiaryNumber#", false, false,"RefSubsidiaryNumber"),//ref sbs
	orig_document_number(1,Items,null,"orig_document_number","#Item.RefReceipt#", false, false,"RefReceipt"),//ref receipt
	vendor_code(1,Items,null,"vendor_code","#Item.Vendor#", false, false,"VendorCode"),
	scan_upc(1,Items,null,"scan_upc","#Item.UPC#", false, false,"UPC"),
	so_number(1,Items,null,"so_number","#Receipt.SONumber#", false, false,"SONumber"),
	//receipt placeholders
	DRDocument(1,null,"drDocument",null,null, false, false,"Receipt"),
	tracking_number(1,DRDocument,null,"tracking_number","#Receipt.TrackingNum_LU#", false, false,"TrackingNum"),
	notes_general(1,DRDocument,null,"notes_general","#Receipt.ECOMOrderNo#", false, false,"ECOMOrderNo"),
	created_datetime(1,DRDocument,null,"created_datetime","#Receipt.Date#", false, false,"DocDate"),
	document_number(1,DRDocument,null,"document_number","#Receipt.Number#", false, false,"InvcNum"),
	employee1_full_name(1,DRDocument,null,"employee1_full_name","#Receipt.SalesPerson#", false, false,"SalesPerson"),
	bt_employee_id(1,DRDocument,null,"bt_employee_id","#Receipt.Associate#", false, false,"EmployeeID"),
	workstation_number(1,DRDocument,null,"workstation_number","#Receipt.WS#", false, false,"Workstation"),
	sold_qty(1,DRDocument,null,"sold_qty","#Receipt.InvcTotQty#", false, false,"InvcTotalQty"),
	transaction_subtotal(1,DRDocument,null,"transaction_subtotal","#Receipt.Subtotal#", false, true,"Subtotal"),
	transaction_subtotal_DEC3(1,DRDocument,null,"transaction_subtotal","#Receipt.Subtotal_DEC3#", true, true,"Subtotal"),
	transaction_total_tax_amt(1,DRDocument,null,"transaction_total_tax_amt","#Receipt.TaxAmount#", false, true, "TotalTax"),
	transaction_total_with_total_tax_amt(1,DRDocument,null,"transaction_total_tax_amt","#Receipt.TotWithTaxAmount#", false, true, "TotalTax"),
	transaction_total_tax_amt_DEC3(1,DRDocument,null,"transaction_total_tax_amt","#Receipt.TaxAmount_DEC3#", true, true,"TotalTax"),
	transaction_total_amt(1,DRDocument,null,"transaction_total_amt","#Receipt.Amount#", false, true, "Total"),
	transaction_total_amt_DEC3(1,DRDocument,null,"transaction_total_amt","#Receipt.Amount_DEC3#", true, true,"Total"),
	total_discount_amt(1,DRDocument,null,"total_discount_amt","#Receipt.Discount#", false, true,"Discount"),
	
	total_discount_amt_DEC3(1,DRDocument,null,"total_discount_amt","#Receipt.Discount_DEC3#", true, true,"Discount"),
	transaction_total_round_amt(1,DRDocument,null,"transaction_total_amt","#Receipt.InvcTotalRoundAmt#", false, true,"InvcTotalRoundAmt"),
	transaction_total_round_amt_DEC3(1,DRDocument,null,"transaction_total_amt","#Receipt.InvcTotalRoundAmt_DEC3#", true, true,"InvcTotalRoundAmt"),
	
	rounded_due_amt(1,DRDocument,null,"rounded_due_amt","#Receipt.InvcRoundAmt#", false, true,"InvcRoundAmt"),
	rounded_due_amt_DEC3(1,DRDocument,null,"rounded_due_amt","#Receipt.InvcRoundAmt_DEC3#", true, true,"InvcRoundAmt"),
	coupons(1,DRDocument,null,"coupons","#Receipt.Coupon#", false, false,"CouponId"),
	comment1(1,DRDocument,null,"comment1","#Receipt.InvcComment1#", false, false,"Comment1"),
	comment2(1,DRDocument,null,"comment2","#Receipt.InvcComment2#", false, false,"Comment2"),
	sid(1,DRDocument,null,"sid","#Receipt.ID#", false, false,"DocSID"),
	bt_primary_phone_no(1,DRDocument,null,"bt_primary_phone_no","#Receipt.Mobile_Phone_No#", false, false,"Mobile_Phone_No"),
	store_name(1,DRDocument,null,"store_name","#Store.Name#", false, false,"StoreName"),
	store_address_line1(1,DRDocument,null,"store_address_line1","#Store.Heading1#", false, false,"StoreHeading1"),
	store_address_line2(1,DRDocument,null,"store_address_line2","#Store.Heading2#", false, false,"StoreHeading2"),
	store_address_line3(1,DRDocument,null,"store_address_line3","#Store.Heading3#", false, false,"StoreHeading3"),
	store_address_line4(1,DRDocument,null,"store_address_line4","#Store.Heading4#", false, false,"StoreHeading4"),
	store_address_line5(1,DRDocument,null,"store_address_line5","#Store.Heading5#", false, false,"StoreHeading5"),
	store_phone1(1,DRDocument,null,"store_phone1","#Store.Phone1#", false, false,"StorePhoneNumber"),
	store_address_zip(1,DRDocument,null,"store_address_zip","#Store.Heading6#", false, false,"StoreHeading6"),
	cashier_name(1,DRDocument,null,"cashier_name","#Store.Cashier#", false, false,"Cashier"),
	subsidiary_number(1,DRDocument,null,"subsidiary_number","#Store.SbsNo#", false, false, "SubsidiaryNumber"),
	store_code(1,DRDocument,null,"store_code","#Store.StoreCode#", false, false,"StoreCode"),
	fiscal_document_number(1,DRDocument,null,"fiscal_document_number","#Store.FiscalCode#", false, false,"FiscalCode"),
	bt_id(1,DRDocument,null,"bt_id","#BillTo.CustNumber#", false, false,"BillToCustNumber"),
	bt_cuid(1,DRDocument,null,"bt_cuid","#BillTo.CustID#", false, false,"BillToCustSID"),
	bt_first_name(1,DRDocument,null,"bt_first_name","#BillTo.Name#", false, false,"BillToFName"),
	bt_company_name(1,DRDocument,null,"bt_company_name","#BillTo.BillToCustCompany#", false, false,"BillToCustCompany"),
	st_company_name(1,DRDocument,null,"st_company_name","#BillTo.ShipToCustCompany#", false, false,"ShipToCustCompany"),
	bt_address_line1(1,DRDocument,null,"bt_address_line1","#BillTo.Addr1#", false, false,"BillToAddr1"),
	bt_address_line2(1,DRDocument,null,"bt_address_line2","#BillTo.Addr2#", false, false,"BillToAddr2"),
	bt_address_line3(1,DRDocument,null,"bt_address_line3","#BillTo.Addr3#", false, false,"BillToAddr3"),
	bt_address_line4(1,DRDocument,null,"bt_address_line4","#BillTo.Addr4#", false, false,"BillToAddr4"),
	bt_address_line5(1,DRDocument,null,"bt_address_line5","#BillTo.Addr5#", false, false,"BillToAddr5"),
	bt_address_line6(1,DRDocument,null,"bt_address_line6","#BillTo.Addr6#", false, false,"BillToAddr6"),
	st_address_line1(1,DRDocument,null,"st_address_line1","#ShipTo.Addr1#", false, false,"ShipToAddr1"),
	st_address_line2(1,DRDocument,null,"st_address_line2","#ShipTo.Addr2#", false, false,"ShipToAddr2"),
	st_address_line3(1,DRDocument,null,"st_address_line3","#ShipTo.Addr3#", false, false,"ShipToAddr3"),
	st_address_line4(1,DRDocument,null,"st_address_line4","#ShipTo.Addr4#", false, false,"ShipToAddr4"),
	st_address_line5(1,DRDocument,null,"st_address_line5","#ShipTo.Addr5#", false, false,"ShipToAddr5"),
	st_address_line6(1,DRDocument,null,"st_address_line6","#ShipTo.Addr6#", false, false,"ShipToAddr6"),
	bt_postal_code(1,DRDocument,null,"bt_postal_code","#BillTo.Zip#", false, false,"BillToZip"),
	st_postal_code(1,DRDocument,null,"st_postal_code","#ShipTo.Zip#", false, false,"ShipToZip"),
	bt_info1(1,DRDocument,null,"bt_info1","#BillTo.Info1#", false, false,"BillToInfo1"),
	bt_email(1,DRDocument,null,"bt_email","#BillTo.Email#", false, false,"BillToEmail"),
	tender_name(1,DRDocument,null,"tender_name","#Receipt.PaymentMethod#", false, false,"Tender"),
	tenderd(1,DRDocument,null,"doc_tender_type","#Receipt.Tendered#", false, false,"Tendered"),
	change(1,DRDocument,null,"given_amt","#Receipt.Change#", false, false,"Change"),
	TotalSavings(1,DRDocument,null,"total_discount_amt","#Receipt.TotalSavings#", false, false,"TotalSavings"),
	st_first_name(1,DRDocument,null,"st_first_name","#ShipTo.Name#", false, false,"ShipToFName");
	
	private long fieldId;
	private PrismDREnum parent;
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

	private PrismDREnum(long fieldId, PrismDREnum parent, String rootElementName, String elementName,String hashTag, boolean decimal, boolean number) {
		this.fieldId = fieldId;
		this.parent = parent;
		this.rootElementName = rootElementName;
		this.elementName = elementName;
		this.hashTag = hashTag;
		this.decimal = decimal;
		this.number = number;
		
	}
	
	private PrismDREnum(long fieldId, PrismDREnum parent, String rootElementName, String elementName,String hashTag, boolean decimal, boolean number, boolean checkVisibility) {
		this.fieldId = fieldId;
		this.parent = parent;
		this.rootElementName = rootElementName;
		this.elementName = elementName;
		this.hashTag = hashTag;
		this.decimal = decimal;
		this.number = number;
		this.checkVisibility = checkVisibility;
		
	}
	private PrismDREnum(long fieldId, PrismDREnum parent, String rootElementName, String elementName,String hashTag, boolean decimal, boolean number, String OptDRJsonEleName) {
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



	public PrismDREnum getParent() {
		return parent;
	}



	public void setParent(PrismDREnum parent) {
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
	
	
public static PrismDREnum getEnumsByHashTag(String hashTag) {
		
		
		PrismDREnum[] childEnum = PrismDREnum.values();
		for (PrismDREnum prismDREnum : childEnum) {
			//logger.info("parentEnum "+parentEnum+" segmentEnum "+segmentEnum);
			if(prismDREnum.getHashTag() == null ) continue;
			if(prismDREnum.getHashTag().equalsIgnoreCase(hashTag)) {
				
				return prismDREnum;
				
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
