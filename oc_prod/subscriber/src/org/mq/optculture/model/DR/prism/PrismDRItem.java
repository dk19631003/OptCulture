package org.mq.optculture.model.DR.prism;
public class PrismDRItem {

	
	private String item_pos;//itemline
	private String alu;
	private String original_price;
	private String price;
	private String tax_percent;
	private String tax_amount;
	private String quantity;
	private String item_description1;
	private String item_description2;
	private String item_description3;
	private String item_description4;
	private String discount_perc;
	private String discount_amt;
	private String discount_reason;
	private String note1;
	private String note2;
	private String note3;
	private String note4;
	private String note5;
	private String note6;
	private String note7;
	private String note8;
	private String note9;
	private String note10;
	private String employee1_login_name;
	private String so_number;
	private String serial_number;
	private String ref_order_doc_sid;
	private String ref_sale_sid;
	private String fulfill_store_sbs_no;
	private String fulfill_store_no;
	private String orig_store_number;//ref store
	private String orig_subsidiary_number;//ref sbs
	private String orig_document_number;//ref receipt
	private String attribute;
	private String netPrice;
	private String udf_string01;
	private String vendor_code;
	private String price_lvl;
	private String package_sequence_number;
	private String return_reason;
	private String item_type; // 2- return
	public String getReturn_reason() {
		return return_reason;
	}
	public void setReturn_reason(String return_reason) {
		this.return_reason = return_reason;
	}
	public String getPackage_sequence_number() {
		return package_sequence_number;
	}
	public void setPackage_sequence_number(String package_sequence_number) {
		this.package_sequence_number = package_sequence_number;
	}
	private String invn_sbs_item_sid;
	private String invn_item_uid;
	public String getInvn_sbs_item_sid() {
		return invn_sbs_item_sid;
	}
	public void setInvn_sbs_item_sid(String invn_sbs_item_sid) {
		this.invn_sbs_item_sid = invn_sbs_item_sid;
	}
	public String getPrice_lvl() {
		return price_lvl;
	}
	public void setPrice_lvl(String price_lvl) {
		this.price_lvl = price_lvl;
	}
	public String getVendor_code() {
		return vendor_code;
	}
	public void setVendor_code(String vendor_code) {
		this.vendor_code = vendor_code;
	}
	public String getUdf_string01() {
		return udf_string01;
	}
	public void setUdf_string01(String udf_string01) {
		this.udf_string01 = udf_string01;
	}
	public String getUdf_string02() {
		return udf_string02;
	}
	public void setUdf_string02(String udf_string02) {
		this.udf_string02 = udf_string02;
	}
	public String getUdf_string03() {
		return udf_string03;
	}
	public void setUdf_string03(String udf_string03) {
		this.udf_string03 = udf_string03;
	}
	public String getUdf_string04() {
		return udf_string04;
	}
	public void setUdf_string04(String udf_string04) {
		this.udf_string04 = udf_string04;
	}
	public String getUdf_string05() {
		return udf_string05;
	}
	public void setUdf_string05(String udf_string05) {
		this.udf_string05 = udf_string05;
	}
	public String getScan_upc() {
		return scan_upc;
	}
	public void setScan_upc(String scan_upc) {
		this.scan_upc = scan_upc;
	}
	private String udf_string02;
	private String udf_string03;
	private String udf_string04;
	private String udf_string05;
	private String scan_upc;
	public String getNetPrice() {
		return netPrice;
	}
	public void setNetPrice(String netPrice) {
		this.netPrice = netPrice;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public String getItem_lookup() {
		return item_lookup;
	}
	public void setItem_lookup(String item_lookup) {
		this.item_lookup = item_lookup;
	}
	public String getItem_size() {
		return item_size;
	}
	public void setItem_size(String item_size) {
		this.item_size = item_size;
	}
	public String getDcs_code() {
		return dcs_code;
	}
	public void setDcs_code(String dcs_code) {
		this.dcs_code = dcs_code;
	}
	private String item_lookup;
	private String item_size;
	private String dcs_code;
	public String getItem_pos() {
		return item_pos;
	}
	public void setItem_pos(String item_pos) {
		this.item_pos = item_pos;
	}
	public String getAlu() {
		return alu;
	}
	public void setAlu(String alu) {
		this.alu = alu;
	}
	public String getOriginal_price() {
		return original_price;
	}
	public void setOriginal_price(String original_price) {
		this.original_price = original_price;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getTax_percent() {
		return tax_percent;
	}
	public void setTax_percent(String tax_percent) {
		this.tax_percent = tax_percent;
	}
	public String getTax_amount() {
		return tax_amount;
	}
	public void setTax_amount(String tax_amount) {
		this.tax_amount = tax_amount;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getItem_description1() {
		return item_description1;
	}
	public void setItem_description1(String item_description1) {
		this.item_description1 = item_description1;
	}
	public String getItem_description2() {
		return item_description2;
	}
	public void setItem_description2(String item_description2) {
		this.item_description2 = item_description2;
	}
	public String getItem_description3() {
		return item_description3;
	}
	public void setItem_description3(String item_description3) {
		this.item_description3 = item_description3;
	}
	public String getItem_description4() {
		return item_description4;
	}
	public void setItem_description4(String item_description4) {
		this.item_description4 = item_description4;
	}
	public String getDiscount_perc() {
		return discount_perc;
	}
	public void setDiscount_perc(String discount_perc) {
		this.discount_perc = discount_perc;
	}
	public String getDiscount_amt() {
		return discount_amt;
	}
	public void setDiscount_amt(String discount_amt) {
		this.discount_amt = discount_amt;
	}
	public String getDiscount_reason() {
		return discount_reason;
	}
	public void setDiscount_reason(String discount_reason) {
		this.discount_reason = discount_reason;
	}
	public String getNote1() {
		return note1;
	}
	public void setNote1(String note1) {
		this.note1 = note1;
	}
	public String getNote2() {
		return note2;
	}
	public void setNote2(String note2) {
		this.note2 = note2;
	}
	public String getNote3() {
		return note3;
	}
	public void setNote3(String note3) {
		this.note3 = note3;
	}
	public String getNote4() {
		return note4;
	}
	public void setNote4(String note4) {
		this.note4 = note4;
	}
	public String getNote5() {
		return note5;
	}
	public void setNote5(String note5) {
		this.note5 = note5;
	}
	public String getNote6() {
		return note6;
	}
	public void setNote6(String note6) {
		this.note6 = note6;
	}
	public String getNote7() {
		return note7;
	}
	public void setNote7(String note7) {
		this.note7 = note7;
	}
	public String getNote8() {
		return note8;
	}
	public void setNote8(String note8) {
		this.note8 = note8;
	}
	public String getNote9() {
		return note9;
	}
	public void setNote9(String note9) {
		this.note9 = note9;
	}
	public String getNote10() {
		return note10;
	}
	public void setNote10(String note10) {
		this.note10 = note10;
	}
	public String getSerial_number() {
		return serial_number;
	}
	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
	}
	public String getRef_order_doc_sid() {
		return ref_order_doc_sid;
	}
	public void setRef_order_doc_sid(String ref_order_doc_sid) {
		this.ref_order_doc_sid = ref_order_doc_sid;
	}
	public String getFulfill_store_sbs_no() {
		return fulfill_store_sbs_no;
	}
	public void setFulfill_store_sbs_no(String fulfill_store_sbs_no) {
		this.fulfill_store_sbs_no = fulfill_store_sbs_no;
	}
	public String getFulfill_store_no() {
		return fulfill_store_no;
	}
	public void setFulfill_store_no(String fulfill_store_no) {
		this.fulfill_store_no = fulfill_store_no;
	}
	public String getOrig_store_number() {
		return orig_store_number;
	}
	public void setOrig_store_number(String orig_store_number) {
		this.orig_store_number = orig_store_number;
	}
	public String getOrig_subsidiary_number() {
		return orig_subsidiary_number;
	}
	public void setOrig_subsidiary_number(String orig_subsidiary_number) {
		this.orig_subsidiary_number = orig_subsidiary_number;
	}
	public String getOrig_document_number() {
		return orig_document_number;
	}
	public void setOrig_document_number(String orig_document_number) {
		this.orig_document_number = orig_document_number;
	}
	public String getSo_number() {
		return so_number;
	}
	public void setSo_number(String so_number) {
		this.so_number = so_number;
	}
	public String getEmployee1_login_name() {
		return employee1_login_name;
	}
	public void setEmployee1_login_name(String employee1_login_name) {
		this.employee1_login_name = employee1_login_name;
	}
	public String getItem_type() {
		return item_type;
	}
	public void setItem_type(String item_type) {
		this.item_type = item_type;
	}
	public String getInvn_item_uid() {
		return invn_item_uid;
	}
	public void setInvn_item_uid(String invn_item_uid) {
		this.invn_item_uid = invn_item_uid;
	}
	public String getRef_sale_sid() {
		return ref_sale_sid;
	}
	public void setRef_sale_sid(String ref_sale_sid) {
		this.ref_sale_sid = ref_sale_sid;
	}
	
	private String ref_sale_doc_sid;
	private String ref_sale_doc_no;
	public String getRef_sale_doc_sid() {
		return ref_sale_doc_sid;
	}
	public void setRef_sale_doc_sid(String ref_sale_doc_sid) {
		this.ref_sale_doc_sid = ref_sale_doc_sid;
	}
	public String getRef_sale_doc_no() {
		return ref_sale_doc_no;
	}
	public void setRef_sale_doc_no(String ref_sale_doc_no) {
		this.ref_sale_doc_no = ref_sale_doc_no;
	}
	
	
}
