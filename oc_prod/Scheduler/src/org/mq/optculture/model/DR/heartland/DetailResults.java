package org.mq.optculture.model.DR.heartland;

import java.util.ArrayList;

public class DetailResults {
	
	private String id;
    private String type;
    private String sales_transaction_id;
    private String description;
    private String value;
    private String created_at;
    private String updated_at;
    private String qty;
    private String adjusted_unit_price;
    private String unit_cost;
    private String original_unit_price;
    private String item_id;
    private Object action_id;
    private Object shipping_line_id;
    private Object item_line_id;
    private Object sales_transaction_line_id;
    private Object address_id;
    private Object shipping_method_id;
    private boolean manuallyAdded;
    private DetailReasonCodes reason_codes;
    private Object lookup_id;
    private Object customer_id;
    private Object loyalty_points;
    
    public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSales_transaction_id() {
		return sales_transaction_id;
	}
	public void setSales_transaction_id(String sales_transaction_id) {
		this.sales_transaction_id = sales_transaction_id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
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
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public String getAdjusted_unit_price() {
		return adjusted_unit_price;
	}
	public void setAdjusted_unit_price(String adjusted_unit_price) {
		this.adjusted_unit_price = adjusted_unit_price;
	}
	public String getUnit_cost() {
		return unit_cost;
	}
	public void setUnit_cost(String unit_cost) {
		this.unit_cost = unit_cost;
	}
	public String getOriginal_unit_price() {
		return original_unit_price;
	}
	public void setOriginal_unit_price(String original_unit_price) {
		this.original_unit_price = original_unit_price;
	}
	public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
	public Object getAction_id() {
		return action_id;
	}
	public void setAction_id(Object action_id) {
		this.action_id = action_id;
	}
	public Object getShipping_line_id() {
		return shipping_line_id;
	}
	public void setShipping_line_id(Object shipping_line_id) {
		this.shipping_line_id = shipping_line_id;
	}
	public Object getItem_line_id() {
		return item_line_id;
	}
	public void setItem_line_id(Object item_line_id) {
		this.item_line_id = item_line_id;
	}
	public Object getSales_transaction_line_id() {
		return sales_transaction_line_id;
	}
	public void setSales_transaction_line_id(Object sales_transaction_line_id) {
		this.sales_transaction_line_id = sales_transaction_line_id;
	}
	public Object getAddress_id() {
		return address_id;
	}
	public void setAddress_id(Object address_id) {
		this.address_id = address_id;
	}
	public Object getShipping_method_id() {
		return shipping_method_id;
	}
	public void setShipping_method_id(Object shipping_method_id) {
		this.shipping_method_id = shipping_method_id;
	}
	public boolean isManuallyAdded() {
		return manuallyAdded;
	}
	public void setManuallyAdded(boolean manuallyAdded) {
		this.manuallyAdded = manuallyAdded;
	}
	public DetailReasonCodes getReason_codes() {
		return reason_codes;
	}
	public void setReason_codes(DetailReasonCodes reason_codes) {
		this.reason_codes = reason_codes;
	}
	public Object getLookup_id() {
		return lookup_id;
	}
	public void setLookup_id(Object lookup_id) {
		this.lookup_id = lookup_id;
	}
	public Object getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(Object customer_id) {
		this.customer_id = customer_id;
	}
	public Object getLoyalty_points() {
		return loyalty_points;
	}
	public void setLoyalty_points(Object loyalty_points) {
		this.loyalty_points = loyalty_points;
	}
	public Object getOrder_line_id() {
		return order_line_id;
	}
	public void setOrder_line_id(Object order_line_id) {
		this.order_line_id = order_line_id;
	}
	public Object getAddress_revision_id() {
		return address_revision_id;
	}
	public void setAddress_revision_id(Object address_revision_id) {
		this.address_revision_id = address_revision_id;
	}
	public Object getOrder_discount_id() {
		return order_discount_id;
	}
	public void setOrder_discount_id(Object order_discount_id) {
		this.order_discount_id = order_discount_id;
	}
	public Object getGift_card_id() {
		return gift_card_id;
	}
	public void setGift_card_id(Object gift_card_id) {
		this.gift_card_id = gift_card_id;
	}
	public Object getTax_rule_id() {
		return tax_rule_id;
	}
	public void setTax_rule_id(Object tax_rule_id) {
		this.tax_rule_id = tax_rule_id;
	}
	public String getDiscount_value() {
		return discount_value;
	}
	public void setDiscount_value(String discount_value) {
		this.discount_value = discount_value;
	}
	public String getCurrent_unit_price() {
		return current_unit_price;
	}
	public void setCurrent_unit_price(String current_unit_price) {
		this.current_unit_price = current_unit_price;
	}
	public Object getMetadata_private() {
		return metadata_private;
	}
	public void setMetadata_private(Object metadata_private) {
		this.metadata_private = metadata_private;
	}
	public boolean isPermanent() {
		return permanent;
	}
	public void setPermanent(boolean permanent) {
		this.permanent = permanent;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public Object getSource_type() {
		return source_type;
	}
	public void setSource_type(Object source_type) {
		this.source_type = source_type;
	}
	public Object getSource_id() {
		return source_id;
	}
	public void setSource_id(Object source_id) {
		this.source_id = source_id;
	}
	public String getUpdated_by_user_id() {
		return updated_by_user_id;
	}
	public void setUpdated_by_user_id(String updated_by_user_id) {
		this.updated_by_user_id = updated_by_user_id;
	}
	public String getCreated_by_user_id() {
		return created_by_user_id;
	}
	public void setCreated_by_user_id(String created_by_user_id) {
		this.created_by_user_id = created_by_user_id;
	}
	public ArrayList<Object> getSales_rep_ids() {
		return sales_rep_ids;
	}
	public void setSales_rep_ids(ArrayList<Object> sales_rep_ids) {
		this.sales_rep_ids = sales_rep_ids;
	}
	public DetailCustom getCustom() {
		return custom;
	}
	public void setCustom(DetailCustom custom) {
		this.custom = custom;
	}
	public Object getGiftCardBalanceLoaded() {
		return giftCardBalanceLoaded;
	}
	public void setGiftCardBalanceLoaded(Object giftCardBalanceLoaded) {
		this.giftCardBalanceLoaded = giftCardBalanceLoaded;
	}
	public ArrayList<Object> getAllowable_custom_field_keys() {
		return allowable_custom_field_keys;
	}
	public void setAllowable_custom_field_keys(ArrayList<Object> allowable_custom_field_keys) {
		this.allowable_custom_field_keys = allowable_custom_field_keys;
	}
	public String getPrice_list_id() {
		return price_list_id;
	}
	public void setPrice_list_id(String price_list_id) {
		this.price_list_id = price_list_id;
	}
	public Object getDeposit_item_tracking_number_id() {
		return deposit_item_tracking_number_id;
	}
	public void setDeposit_item_tracking_number_id(Object deposit_item_tracking_number_id) {
		this.deposit_item_tracking_number_id = deposit_item_tracking_number_id;
	}
	public DetailPromptFor getPrompt_for() {
		return prompt_for;
	}
	public void setPrompt_for(DetailPromptFor prompt_for) {
		this.prompt_for = prompt_for;
	}
	public Object getPrivate_id() {
		return private_id;
	}
	public void setPrivate_id(Object private_id) {
		this.private_id = private_id;
	}
	public String getUnit_price() {
		return unit_price;
	}
	public void setUnit_price(String unit_price) {
		this.unit_price = unit_price;
	}
	public DetailItemCustom getItem_custom() {
		return item_custom;
	}
	public void setItem_custom(DetailItemCustom item_custom) {
		this.item_custom = item_custom;
	}
	public Object getRemoval_reason() {
		return removal_reason;
	}
	public void setRemoval_reason(Object removal_reason) {
		this.removal_reason = removal_reason;
	}
	public ArrayList<Object> getPrice_adjustments() {
		return price_adjustments;
	}
	public void setPrice_adjustments(ArrayList<Object> price_adjustments) {
		this.price_adjustments = price_adjustments;
	}
	public ArrayList<Object> getAdd_on_item_lines() {
		return add_on_item_lines;
	}
	public void setAdd_on_item_lines(ArrayList<Object> add_on_item_lines) {
		this.add_on_item_lines = add_on_item_lines;
	}
	public String getFinal_unit_price() {
		return final_unit_price;
	}
	public void setFinal_unit_price(String final_unit_price) {
		this.final_unit_price = final_unit_price;
	}
	public Object getImage_thumbnail_url() {
		return image_thumbnail_url;
	}
	public void setImage_thumbnail_url(Object image_thumbnail_url) {
		this.image_thumbnail_url = image_thumbnail_url;
	}
	public Object getImage_url() {
		return image_url;
	}
	public void setImage_url(Object image_url) {
		this.image_url = image_url;
	}
	public Object getItem_metadata_private() {
		return item_metadata_private;
	}
	public void setItem_metadata_private(Object item_metadata_private) {
		this.item_metadata_private = item_metadata_private;
	}
	public String getItem_public_id() {
		return item_public_id;
	}
	public void setItem_public_id(String item_public_id) {
		this.item_public_id = item_public_id;
	}
	public String getOriginal_value() {
		return original_value;
	}
	public void setOriginal_value(String original_value) {
		this.original_value = original_value;
	}
	public ArrayList<Object> getPossible_add_ons() {
		return possible_add_ons;
	}
	public void setPossible_add_ons(ArrayList<Object> possible_add_ons) {
		this.possible_add_ons = possible_add_ons;
	}
	public Object getSales_rep() {
		return sales_rep;
	}
	public void setSales_rep(Object sales_rep) {
		this.sales_rep = sales_rep;
	}
	private Object order_line_id;
    private Object address_revision_id;
    private Object order_discount_id;
    private Object gift_card_id;
    private Object tax_rule_id;
    private String discount_value;
    private String current_unit_price;
    private Object metadata_private;
    private boolean permanent;
    private boolean deleted;
    private Object source_type;
    private Object source_id;
    private String updated_by_user_id;
    private String created_by_user_id;
    private ArrayList<Object> sales_rep_ids;
    private DetailCustom custom;
    private Object giftCardBalanceLoaded;
    private ArrayList<Object> allowable_custom_field_keys;
    private String price_list_id;
    private Object deposit_item_tracking_number_id;
    private DetailPromptFor prompt_for;
    private Object private_id;
    private String unit_price;
    private DetailItemCustom item_custom;
    private Object removal_reason;
    private ArrayList<Object> price_adjustments;
    private ArrayList<Object> add_on_item_lines;
    private String final_unit_price;
    private Object image_thumbnail_url;
    private Object image_url;
    private Object item_metadata_private;
    private String item_public_id;
    private String original_value;
    private ArrayList<Object> possible_add_ons;
    private Object sales_rep;
    
	
}
