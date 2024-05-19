package org.mq.optculture.model.DR.prism;
import java.util.List;

public class PrismDRBody {

	private String store_name;
	private String store_number;
	private String store_code;
	private String original_store_number;//store number 0
	private String orig_subsidiary_number;
	private String original_store_code;//storecode000
	private String store_address_line1;//store heading 1-5
	private String store_address_line2;
	private String store_address_line3;
	private String store_address_line4;
	private String store_address_line5;
	private String store_address_zip; //address line 6 
	private String store_phone1;
	private String store_phone2;
	private String ref_sale_sid;
	private String cashier_name;//cashier
	private String sid;//docsid
	private String serial_number;//Serial NO
	private String so_number;
	private String eft_invoice_number;//invcnum
	private String doc_tender_type;//tender-split or 0
	private String tender_name;
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
	private String orig_document_number;
	private String bt_postal_code;
	private String st_address_line1;
	private String st_address_line2;
	private String st_address_line3;
	private String st_address_line4;
	private String st_address_line5;
	private String st_address_line6;
	private String st_company_name;
	
	private String st_postal_code;
	private String st_first_name;
	private String st_last_name;
	private String bt_employee_id;
	private String order_shipping_percentage;
	private String fee_type1;
	private String bt_info1;
	private String bt_udf1;
	private String bt_udf2;
	private String bt_udf3;
	private String bt_udf4;
	private String bt_udf5;
	private String bt_udf6;
	private String bt_udf7;
	private String bt_udf8;
	private String st_email;
	private String used_discount_type;
	private String customer_dob;
	private String customer_nationality;

	
	public String getCustomer_dob() {
		return customer_dob;
	}
	public void setCustomer_dob(String customer_dob) {
		this.customer_dob = customer_dob;
	}
	
	public String getCustomer_nationality() {
		return customer_nationality;
	}
	public void setCustomer_nationality(String customer_nationality) {
		this.customer_nationality = customer_nationality;
	}
	
	public String getUsed_discount_type() {
		return used_discount_type;
	}
	public void setUsed_discount_type(String used_discount_type) {
		this.used_discount_type = used_discount_type;
	}
	public String getSt_email() {
		return st_email;
	}
	public void setSt_email(String st_email) {
		this.st_email = st_email;
	}
	public String getBt_udf1() {
		return bt_udf1;
	}
	public void setBt_udf1(String bt_udf1) {
		this.bt_udf1 = bt_udf1;
	}
	public String getBt_udf2() {
		return bt_udf2;
	}
	public void setBt_udf2(String bt_udf2) {
		this.bt_udf2 = bt_udf2;
	}
	public String getBt_udf3() {
		return bt_udf3;
	}
	public void setBt_udf3(String bt_udf3) {
		this.bt_udf3 = bt_udf3;
	}
	public String getBt_udf4() {
		return bt_udf4;
	}
	public void setBt_udf4(String bt_udf4) {
		this.bt_udf4 = bt_udf4;
	}
	public String getBt_udf5() {
		return bt_udf5;
	}
	public void setBt_udf5(String bt_udf5) {
		this.bt_udf5 = bt_udf5;
	}
	public String getBt_udf6() {
		return bt_udf6;
	}
	public void setBt_udf6(String bt_udf6) {
		this.bt_udf6 = bt_udf6;
	}
	public String getBt_udf7() {
		return bt_udf7;
	}
	public void setBt_udf7(String bt_udf7) {
		this.bt_udf7 = bt_udf7;
	}
	public String getBt_udf8() {
		return bt_udf8;
	}
	public void setBt_udf8(String bt_udf8) {
		this.bt_udf8 = bt_udf8;
	}
	
	public String getBt_info1() {
		return bt_info1;
	}
	public void setBt_info1(String bt_info1) {
		this.bt_info1 = bt_info1;
	}
	public String getBt_info2() {
		return bt_info2;
	}
	public void setBt_info2(String bt_info2) {
		this.bt_info2 = bt_info2;
	}
	private String bt_info2;
	public String getFee_type1() {
		return fee_type1;
	}
	public void setFee_type1(String fee_type1) {
		this.fee_type1 = fee_type1;
	}
	public String getFee_tax_perc1() {
		return fee_tax_perc1;
	}
	public void setFee_tax_perc1(String fee_tax_perc1) {
		this.fee_tax_perc1 = fee_tax_perc1;
	}
	private String fee_tax_perc1;
	
	public String getOrder_shipping_percentage() {
		return order_shipping_percentage;
	}
	public void setOrder_shipping_percentage(String order_shipping_percentage) {
		this.order_shipping_percentage = order_shipping_percentage;
	}
	private String transaction_subtotal_with_tax;
	private String bt_company_name;
	private String given_amt;
	private String comment1;
	private String comment2;
	private String bt_info;//associate
	private String invoice_posted_date;
	private String tracking_number;//Tracking_num_LU
	private String tax_area_name;
	public String getTax_area_name() {
		return tax_area_name;
	}
	public void setTax_area_name(String tax_area_name) {
		this.tax_area_name = tax_area_name;
	}
	public String getTracking_number() {
		return tracking_number;
	}
	public void setTracking_number(String tracking_number) {
		this.tracking_number = tracking_number;
	}
	private String fiscal_document_number;
	private String subsidiary_number;
	private String workstation_number;
	private String sold_qty;//invctotQty
	private String return_qty;
	/*private String sale_subtotal;//total before vat
	private String sale_total_amt;//total after vat
	private String sale_total_tax_amt;//tax
*/	
	
	
	
	
	
	public String getReturn_qty() {
		return return_qty;
	}
	public void setReturn_qty(String return_qty) {
		this.return_qty = return_qty;
	}
	private String rounded_due_amt;//round value
	private List<PrismDRCoupon> coupons;
	private String notes_order;
	private String notes_general;
	private String transaction_subtotal;//total before vat
	public String getTransaction_subtotal() {
		return transaction_subtotal;
	}
	public void setTransaction_subtotal(String transaction_subtotal) {
		this.transaction_subtotal = transaction_subtotal;
	}
	public String getTransaction_total_amt() {
		return transaction_total_amt;
	}
	public void setTransaction_total_amt(String transaction_total_amt) {
		this.transaction_total_amt = transaction_total_amt;
	}
	public String getTransaction_total_tax_amt() {
		return transaction_total_tax_amt;
	}
	public void setTransaction_total_tax_amt(String transaction_total_tax_amt) {
		this.transaction_total_tax_amt = transaction_total_tax_amt;
	}
	private String transaction_total_amt;//total after vat
	private String transaction_total_tax_amt;
	private String tax_rebate_percent;
	private String total_item_count;
	public String getTotal_item_count() {
		return total_item_count;
	}
	public void setTotal_item_count(String total_item_count) {
		this.total_item_count = total_item_count;
	}
	public String getTax_rebate_percent() {
		return tax_rebate_percent;
	}
	public void setTax_rebate_percent(String tax_rebate_percent) {
		this.tax_rebate_percent = tax_rebate_percent;
	}
	public String getTax_rebate_amt() {
		return tax_rebate_amt;
	}
	public void setTax_rebate_amt(String tax_rebate_amt) {
		this.tax_rebate_amt = tax_rebate_amt;
	}
	private String tax_rebate_amt;
	
	private String has_sale;
	public String getHas_sale() {
		return has_sale;
	}
	public void setHas_sale(String has_sale) {
		this.has_sale = has_sale;
	}
	public String getHas_return() {
		return has_return;
	}
	public void setHas_return(String has_return) {
		this.has_return = has_return;
	}
	private String has_return;
	
	public String getNotes_general() {
		return notes_general;
	}
	public void setNotes_general(String notes_general) {
		this.notes_general = notes_general;
	}
	private String total_discount_amt;
	private String document_number;
	private String created_datetime;
	
private String discount_reason_name;
	
	public String getDiscount_reason_name() {
		return discount_reason_name;
	}
	public void setDiscount_reason_name(String discount_reason_name) {
		this.discount_reason_name = discount_reason_name;
	}
	public String getCreated_datetime() {
		return created_datetime;
	}
	public void setCreated_datetime(String created_datetime) {
		this.created_datetime = created_datetime;
	}
	public String getDocument_number() {
		return document_number;
	}
	public void setDocument_number(String document_number) {
		this.document_number = document_number;
	}
	private String employee1_id;
	private String employee1_login_name;
	private String order_fee_amt1;
	private String order_shipping_amt;
	private String employee1_full_name;
	
	
	
	public String getEmployee1_full_name() {
		return employee1_full_name;
	}
	public void setEmployee1_full_name(String employee1_full_name) {
		this.employee1_full_name = employee1_full_name;
	}
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
	/*public String getSale_subtotal() {
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
	}*/
	public String getRounded_due_amt() {
		return rounded_due_amt;
	}
	public void setRounded_due_amt(String rounded_due_amt) {
		this.rounded_due_amt = rounded_due_amt;
	}
	public List<PrismDRCoupon> getCoupons() {
		return coupons;
	}
	public void setCoupons(List<PrismDRCoupon> coupons) {
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
	
	public String getBt_employee_id() {
		return bt_employee_id;
	}
	public void setBt_employee_id(String bt_employee_id) {
		this.bt_employee_id = bt_employee_id;
	}
	public String getTender_name() {
		return tender_name;
	}
	public void setTender_name(String tender_name) {
		this.tender_name = tender_name;
	}
	public String getSt_address_line1() {
		return st_address_line1;
	}
	public void setSt_address_line1(String st_address_line1) {
		this.st_address_line1 = st_address_line1;
	}
	public String getSt_address_line2() {
		return st_address_line2;
	}
	public void setSt_address_line2(String st_address_line2) {
		this.st_address_line2 = st_address_line2;
	}
	public String getSt_address_line3() {
		return st_address_line3;
	}
	public void setSt_address_line3(String st_address_line3) {
		this.st_address_line3 = st_address_line3;
	}
	public String getSt_address_line4() {
		return st_address_line4;
	}
	public void setSt_address_line4(String st_address_line4) {
		this.st_address_line4 = st_address_line4;
	}
	public String getSt_address_line5() {
		return st_address_line5;
	}
	public void setSt_address_line5(String st_address_line5) {
		this.st_address_line5 = st_address_line5;
	}
	public String getSt_address_line6() {
		return st_address_line6;
	}
	public void setSt_address_line6(String st_address_line6) {
		this.st_address_line6 = st_address_line6;
	}
	public String getSt_postal_code() {
		return st_postal_code;
	}
	public void setSt_postal_code(String st_postal_code) {
		this.st_postal_code = st_postal_code;
	}
	public String getSt_first_name() {
		return st_first_name;
	}
	public void setSt_first_name(String st_first_name) {
		this.st_first_name = st_first_name;
	}
	public String getSt_last_name() {
		return st_last_name;
	}
	public void setSt_last_name(String st_last_name) {
		this.st_last_name = st_last_name;
	}
	public String getSt_company_name() {
		return st_company_name;
	}
	public void setSt_company_name(String st_company_name) {
		this.st_company_name = st_company_name;
	}
	public String getGiven_amt() {
		return given_amt;
	}
	public void setGiven_amt(String given_amt) {
		this.given_amt = given_amt;
	}
	public String getOrig_document_number() {
		return orig_document_number;
	}
	public void setOrig_document_number(String orig_document_number) {
		this.orig_document_number = orig_document_number;
	}
	public String getOrig_subsidiary_number() {
		return orig_subsidiary_number;
	}
	public void setOrig_subsidiary_number(String orig_subsidiary_number) {
		this.orig_subsidiary_number = orig_subsidiary_number;
	}
	public String getRef_sale_sid() {
		return ref_sale_sid;
	}
	public void setRef_sale_sid(String ref_sale_sid) {
		this.ref_sale_sid = ref_sale_sid;
	}
	
}
