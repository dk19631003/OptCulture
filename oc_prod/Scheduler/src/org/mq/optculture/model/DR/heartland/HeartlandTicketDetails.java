package org.mq.optculture.model.DR.heartland;

import java.util.ArrayList;

public class HeartlandTicketDetails {
	
	private String id;
    private TicketMetadata metadata;
    private String type;
    private String customer_id;
    private String source_location_id;
    private String sales_rep;
    private String created_at;
    private String updated_at;
    private String private_id;
    private String station_id;
    private Object parent_transaction_id; 
    private boolean recalculate;
    private String status;
    private Object order_id;
    private String total;
    private String created_by_user_id;
    private TicketCustom custom;
    private String local_created_at;
    private String local_updated_at;
    private Object metadata_private;
    private String updated_by_user_id;
    private String completed_at;
    private String local_completed_at;
    private Object coupon_id;
    private ArrayList<Object> sales_rep_ids;
    private String balance;
    private String billing_address_id;
    private String change;
    private boolean completed;
    private String customer_name;
    private boolean readOnly;
    private String total_item_qty;
    private String total_paid;
    private boolean voided;
    private boolean signatures_required;
    private String original_subtotal;
    private String total_discounts;
    private String value_of_all_added_item_discount_lines;
    private boolean paymentException;
    private String search_vector;
    private TicketPromotionState promotion_state;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public TicketMetadata getMetadata() {
		return metadata;
	}
	public void setMetadata(TicketMetadata metadata) {
		this.metadata = metadata;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}
	public String getSource_location_id() {
		return source_location_id;
	}
	public void setSource_location_id(String source_location_id) {
		this.source_location_id = source_location_id;
	}
	public String getSales_rep() {
		return sales_rep;
	}
	public void setSales_rep(String sales_rep) {
		this.sales_rep = sales_rep;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	public String getPrivate_id() {
		return private_id;
	}
	public void setPrivate_id(String private_id) {
		this.private_id = private_id;
	}
	public String getStation_id() {
		return station_id;
	}
	public void setStation_id(String station_id) {
		this.station_id = station_id;
	}
	public Object getParent_transaction_id() {
		return parent_transaction_id;
	}
	public void setParent_transaction_id(Object parent_transaction_id) {
		this.parent_transaction_id = parent_transaction_id;
	}
	public boolean isRecalculate() {
		return recalculate;
	}
	public void setRecalculate(boolean recalculate) {
		this.recalculate = recalculate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Object getOrder_id() {
		return order_id;
	}
	public void setOrder_id(Object order_id) {
		this.order_id = order_id;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getCreated_by_user_id() {
		return created_by_user_id;
	}
	public void setCreated_by_user_id(String created_by_user_id) {
		this.created_by_user_id = created_by_user_id;
	}
	public TicketCustom getCustom() {
		return custom;
	}
	public void setCustom(TicketCustom custom) {
		this.custom = custom;
	}
	public String getLocal_created_at() {
		return local_created_at;
	}
	public void setLocal_created_at(String local_created_at) {
		this.local_created_at = local_created_at;
	}
	public String getLocal_updated_at() {
		return local_updated_at;
	}
	public void setLocal_updated_at(String local_updated_at) {
		this.local_updated_at = local_updated_at;
	}
	public Object getMetadata_private() {
		return metadata_private;
	}
	public void setMetadata_private(Object metadata_private) {
		this.metadata_private = metadata_private;
	}
	public String getUpdated_by_user_id() {
		return updated_by_user_id;
	}
	public void setUpdated_by_user_id(String updated_by_user_id) {
		this.updated_by_user_id = updated_by_user_id;
	}
	public String getCompleted_at() {
		return completed_at;
	}
	public void setCompleted_at(String completed_at) {
		this.completed_at = completed_at;
	}
	public String getLocal_completed_at() {
		return local_completed_at;
	}
	public void setLocal_completed_at(String local_completed_at) {
		this.local_completed_at = local_completed_at;
	}
	public Object getCoupon_id() {
		return coupon_id;
	}
	public void setCoupon_id(Object coupon_id) {
		this.coupon_id = coupon_id;
	}
	public ArrayList<Object> getSales_rep_ids() {
		return sales_rep_ids;
	}
	public void setSales_rep_ids(ArrayList<Object> sales_rep_ids) {
		this.sales_rep_ids = sales_rep_ids;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getBilling_address_id() {
		return billing_address_id;
	}
	public void setBilling_address_id(String billing_address_id) {
		this.billing_address_id = billing_address_id;
	}
	public String getChange() {
		return change;
	}
	public void setChange(String change) {
		this.change = change;
	}
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	public String getCustomer_name() {
		return customer_name;
	}
	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public String getTotal_item_qty() {
		return total_item_qty;
	}
	public void setTotal_item_qty(String total_item_qty) {
		this.total_item_qty = total_item_qty;
	}
	public String getTotal_paid() {
		return total_paid;
	}
	public void setTotal_paid(String total_paid) {
		this.total_paid = total_paid;
	}
	public boolean isVoided() {
		return voided;
	}
	public void setVoided(boolean voided) {
		this.voided = voided;
	}
	public boolean isSignatures_required() {
		return signatures_required;
	}
	public void setSignatures_required(boolean signatures_required) {
		this.signatures_required = signatures_required;
	}
	public String getOriginal_subtotal() {
		return original_subtotal;
	}
	public void setOriginal_subtotal(String original_subtotal) {
		this.original_subtotal = original_subtotal;
	}
	public String getTotal_discounts() {
		return total_discounts;
	}
	public void setTotal_discounts(String total_discounts) {
		this.total_discounts = total_discounts;
	}
	public String getValue_of_all_added_item_discount_lines() {
		return value_of_all_added_item_discount_lines;
	}
	public void setValue_of_all_added_item_discount_lines(String value_of_all_added_item_discount_lines) {
		this.value_of_all_added_item_discount_lines = value_of_all_added_item_discount_lines;
	}
	public boolean isPaymentException() {
		return paymentException;
	}
	public void setPaymentException(boolean paymentException) {
		this.paymentException = paymentException;
	}
	public String getSearch_vector() {
		return search_vector;
	}
	public void setSearch_vector(String search_vector) {
		this.search_vector = search_vector;
	}
	public TicketPromotionState getPromotion_state() {
		return promotion_state;
	}
	public void setPromotion_state(TicketPromotionState promotion_state) {
		this.promotion_state = promotion_state;
	}
    
	

}
