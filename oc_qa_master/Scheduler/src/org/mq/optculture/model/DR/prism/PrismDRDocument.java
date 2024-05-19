package org.mq.optculture.model.DR.prism;
import java.util.List;

public class PrismDRDocument {

	private String store_name;
	private String store_number;
	private String store_code;
	private String original_store_number;//store number 0
	private String original_store_code;//storecode000
	private String store_address_line1;//store heading 1-5
	private String store_address_line2;
	private String store_address_line3;
	private String store_address_line4;
	private String store_address_line5;
	private String store_address_zip; //address line 6 
	private String store_phone1;
	private String store_phone2;
	private String cashier_name;//cashier
	private String sid;//docsid
	private String serial_number;//Serial NO
	private String so_number;
	private String eft_invoice_number;//invcnum
	private String doc_tender_type;//tender-split or 0
	private String bt_cuid;//bill to cust id
	private String bt_id;//billtocust no
	private String bt_first_name;
	private String bt_last_name;
	private String bt_primary_phone_no;
	private String bt_email;
	private String bt_address_line1;
	private String bt_address_line2;
	private String bt_address_line3;
	private String bt_address_line4;
	private String bt_address_line5;
	private String bt_address_line6;
	private String bt_postal_code;
	private String transaction_subtotal_with_tax;
	private String bt_company_name;
	private String comment1;
	private String comment2;
	private String bt_info;//associate
	private String invoice_posted_date;
	private String order_tracking_number;//Tracking_num_LU
	private String fiscal_document_number;
	private String subsidiary_number;
	private String workstation_number;
	private String sold_qty;//invctotQty
	private String sale_subtotal;//total before vat
	private String sale_total_amt;//total after vat
	private String sale_total_tax_amt;//tax
	private String rounded_due_amt;//round value
	private List<String> coupons;
	private String notes_order;
	private String total_discount_amt;
	
	private String employee1_id;
	private String employee1_login_name;
	private String order_fee_amt1;
	private String order_shipping_amt;
	
	
	public String getEmployee1_id() {
		return employee1_id;
	}
	public void setEmployee1_id(String employee1_id) {
		this.employee1_id = employee1_id;
	}
	private List<PrismDRItem> items;
	private List<PrismDRTender> tenders;
	
	
	public String getStore_name() {
		return store_name;
	}
	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}
	public String getStore_number() {
		return store_number;
	}
	public void setStore_number(String store_number) {
		this.store_number = store_number;
	}
	public String getStore_code() {
		return store_code;
	}
	public void setStore_code(String store_code) {
		this.store_code = store_code;
	}
	public String getOriginal_store_number() {
		return original_store_number;
	}
	public void setOriginal_store_number(String original_store_number) {
		this.original_store_number = original_store_number;
	}
	public String getOriginal_store_code() {
		return original_store_code;
	}
	public void setOriginal_store_code(String original_store_code) {
		this.original_store_code = original_store_code;
	}
	public String getStore_address_line1() {
		return store_address_line1;
	}
	public void setStore_address_line1(String store_address_line1) {
		this.store_address_line1 = store_address_line1;
	}
	public String getStore_address_line2() {
		return store_address_line2;
	}
	public void setStore_address_line2(String store_address_line2) {
		this.store_address_line2 = store_address_line2;
	}
	public String getStore_address_line3() {
		return store_address_line3;
	}
	public void setStore_address_line3(String store_address_line3) {
		this.store_address_line3 = store_address_line3;
	}
	public String getStore_address_line4() {
		return store_address_line4;
	}
	public void setStore_address_line4(String store_address_line4) {
		this.store_address_line4 = store_address_line4;
	}
	public String getStore_address_line5() {
		return store_address_line5;
	}
	public void setStore_address_line5(String store_address_line5) {
		this.store_address_line5 = store_address_line5;
	}
	public String getStore_address_zip() {
		return store_address_zip;
	}
	public void setStore_address_zip(String store_address_zip) {
		this.store_address_zip = store_address_zip;
	}
	public String getStore_phone1() {
		return store_phone1;
	}
	public void setStore_phone1(String store_phone1) {
		this.store_phone1 = store_phone1;
	}
	public String getStore_phone2() {
		return store_phone2;
	}
	public void setStore_phone2(String store_phone2) {
		this.store_phone2 = store_phone2;
	}
	public String getCashier_name() {
		return cashier_name;
	}
	public void setCashier_name(String cashier_name) {
		this.cashier_name = cashier_name;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getSerial_number() {
		return serial_number;
	}
	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
	}
	public String getSo_number() {
		return so_number;
	}
	public void setSo_number(String so_number) {
		this.so_number = so_number;
	}
	public String getEft_invoice_number() {
		return eft_invoice_number;
	}
	public void setEft_invoice_number(String eft_invoice_number) {
		this.eft_invoice_number = eft_invoice_number;
	}
	public String getDoc_tender_type() {
		return doc_tender_type;
	}
	public void setDoc_tender_type(String doc_tender_type) {
		this.doc_tender_type = doc_tender_type;
	}
	public String getBt_cuid() {
		return bt_cuid;
	}
	public void setBt_cuid(String bt_cuid) {
		this.bt_cuid = bt_cuid;
	}
	public String getBt_id() {
		return bt_id;
	}
	public void setBt_id(String bt_id) {
		this.bt_id = bt_id;
	}
	public String getBt_first_name() {
		return bt_first_name;
	}
	public void setBt_first_name(String bt_first_name) {
		this.bt_first_name = bt_first_name;
	}
	public String getBt_last_name() {
		return bt_last_name;
	}
	public void setBt_last_name(String bt_last_name) {
		this.bt_last_name = bt_last_name;
	}
	public String getBt_primary_phone_no() {
		return bt_primary_phone_no;
	}
	public void setBt_primary_phone_no(String bt_primary_phone_no) {
		this.bt_primary_phone_no = bt_primary_phone_no;
	}
	public String getBt_email() {
		return bt_email;
	}
	public void setBt_email(String bt_email) {
		this.bt_email = bt_email;
	}
	public String getBt_address_line1() {
		return bt_address_line1;
	}
	public void setBt_address_line1(String bt_address_line1) {
		this.bt_address_line1 = bt_address_line1;
	}
	public String getBt_address_line2() {
		return bt_address_line2;
	}
	public void setBt_address_line2(String bt_address_line2) {
		this.bt_address_line2 = bt_address_line2;
	}
	public String getBt_address_line3() {
		return bt_address_line3;
	}
	public void setBt_address_line3(String bt_address_line3) {
		this.bt_address_line3 = bt_address_line3;
	}
	public String getBt_address_line4() {
		return bt_address_line4;
	}
	public void setBt_address_line4(String bt_address_line4) {
		this.bt_address_line4 = bt_address_line4;
	}
	public String getBt_address_line5() {
		return bt_address_line5;
	}
	public void setBt_address_line5(String bt_address_line5) {
		this.bt_address_line5 = bt_address_line5;
	}
	public String getBt_address_line6() {
		return bt_address_line6;
	}
	public void setBt_address_line6(String bt_address_line6) {
		this.bt_address_line6 = bt_address_line6;
	}
	public String getBt_postal_code() {
		return bt_postal_code;
	}
	public void setBt_postal_code(String bt_postal_code) {
		this.bt_postal_code = bt_postal_code;
	}
	public String getTransaction_subtotal_with_tax() {
		return transaction_subtotal_with_tax;
	}
	public void setTransaction_subtotal_with_tax(String transaction_subtotal_with_tax) {
		this.transaction_subtotal_with_tax = transaction_subtotal_with_tax;
	}
	
	public String getBt_company_name() {
		return bt_company_name;
	}
	public void setBt_company_name(String bt_company_name) {
		this.bt_company_name = bt_company_name;
	}
	public List<PrismDRItem> getItems() {
		return items;
	}
	public void setItems(List<PrismDRItem> items) {
		this.items = items;
	}
	public List<PrismDRTender> getTenders() {
		return tenders;
	}
	public void setTenders(List<PrismDRTender> tenders) {
		this.tenders = tenders;
	}
	public String getComment1() {
		return comment1;
	}
	public void setComment1(String comment1) {
		this.comment1 = comment1;
	}
	public String getComment2() {
		return comment2;
	}
	public void setComment2(String comment2) {
		this.comment2 = comment2;
	}
	public String getBt_info() {
		return bt_info;
	}
	public void setBt_info(String bt_info) {
		this.bt_info = bt_info;
	}
	public String getInvoice_posted_date() {
		return invoice_posted_date;
	}
	public void setInvoice_posted_date(String invoice_posted_date) {
		this.invoice_posted_date = invoice_posted_date;
	}
	public String getOrder_tracking_number() {
		return order_tracking_number;
	}
	public void setOrder_tracking_number(String order_tracking_number) {
		this.order_tracking_number = order_tracking_number;
	}
	public String getFiscal_document_number() {
		return fiscal_document_number;
	}
	public void setFiscal_document_number(String fiscal_document_number) {
		this.fiscal_document_number = fiscal_document_number;
	}
	public String getSubsidiary_number() {
		return subsidiary_number;
	}
	public void setSubsidiary_number(String subsidiary_number) {
		this.subsidiary_number = subsidiary_number;
	}
	public String getWorkstation_number() {
		return workstation_number;
	}
	public void setWorkstation_number(String workstation_number) {
		this.workstation_number = workstation_number;
	}
	public String getSold_qty() {
		return sold_qty;
	}
	public void setSold_qty(String sold_qty) {
		this.sold_qty = sold_qty;
	}
	public String getSale_subtotal() {
		return sale_subtotal;
	}
	public void setSale_subtotal(String sale_subtotal) {
		this.sale_subtotal = sale_subtotal;
	}
	public String getSale_total_amt() {
		return sale_total_amt;
	}
	public void setSale_total_amt(String sale_total_amt) {
		this.sale_total_amt = sale_total_amt;
	}
	public String getSale_total_tax_amt() {
		return sale_total_tax_amt;
	}
	public void setSale_total_tax_amt(String sale_total_tax_amt) {
		this.sale_total_tax_amt = sale_total_tax_amt;
	}
	public String getRounded_due_amt() {
		return rounded_due_amt;
	}
	public void setRounded_due_amt(String rounded_due_amt) {
		this.rounded_due_amt = rounded_due_amt;
	}
	public List<String> getCoupons() {
		return coupons;
	}
	public void setCoupons(List<String> coupons) {
		this.coupons = coupons;
	}
	public String getNotes_order() {
		return notes_order;
	}
	public void setNotes_order(String notes_order) {
		this.notes_order = notes_order;
	}
	public String getTotal_discount_amt() {
		return total_discount_amt;
	}
	public void setTotal_discount_amt(String total_discount_amt) {
		this.total_discount_amt = total_discount_amt;
	}
	public String getEmployee1_login_name() {
		return employee1_login_name;
	}
	public void setEmployee1_login_name(String employee1_login_name) {
		this.employee1_login_name = employee1_login_name;
	}
	public String getOrder_fee_amt1() {
		return order_fee_amt1;
	}
	public void setOrder_fee_amt1(String order_fee_amt1) {
		this.order_fee_amt1 = order_fee_amt1;
	}
	public String getOrder_shipping_amt() {
		return order_shipping_amt;
	}
	public void setOrder_shipping_amt(String order_shipping_amt) {
		this.order_shipping_amt = order_shipping_amt;
	}
	

	
}
