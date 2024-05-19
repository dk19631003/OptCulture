package org.mq.marketer.campaign.controller.contacts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.general.Constants;
import org.zkoss.zkplus.spring.SpringUtil;

public enum CouponsEnum {

	 
	//************************PURCHASE ATTRIBUTES*****************************************************************************************
	RECEIPT_TOTAL_ATTRIBUTE(0, 2, "Minimum Receipt Total", "Minimum Receipt Total", "string", null, null, null,"","total_purchase_amt","total_purchase_amt"),
		RECEIPT_TOTAL_EQUALS(101, 0, "Equals", "Equals", "number", RECEIPT_TOTAL_ATTRIBUTE, "double", null, "=", ""),
	 ITEM_PURCHASE_ATTRIBUTE(1, 2, "Item Attribute", "Item Attribute", "string", null, null, null,"","",""),
	 ITEM_PURCHASE_VENDOR_CODE(105, 0, "Vendor", "Vendor Code", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null,null,"vendor_code","vendor_code"),
     	PURCHASE_VENDOR_CODE_EQUALS(101, 0, "Equals", "Equals", "String", ITEM_PURCHASE_VENDOR_CODE, "text", null, "is", "",false),
     	PURCHASE_VENDOR_CODE_ONE_OF(3, 0, "One of", "One of", "String", ITEM_PURCHASE_VENDOR_CODE, "text", null, "is in", "",true),
     ITEM_PURCHASE_DEPT_CODE(106, 0, "Department", "Department", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "department_code","department_code"),
     	PURCHASE_DEPT_CODE_EQUALS(101, 0, "Equals", "Equals", "String", ITEM_PURCHASE_DEPT_CODE, "text", null, "is", "",false),
     	PURCHASE_DEPT_CODE_ONE_OF(3, 0, "One of", "One of", "String", ITEM_PURCHASE_DEPT_CODE, "text", null, "is in", "",true),
     ITEM_PURCHASE_ITEM_CATEGORY(105, 0, "Item Category", "Item Category", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "item_category","item_category"),
     	PURCHASE_ITEM_CATEGORY_EQUALS(101, 0, "Equals", "Equals", "String", ITEM_PURCHASE_ITEM_CATEGORY, "text", null, "is", "",false),
     	PURCHASE_ITEM_CATEGORY_ONE_OF(3, 0, "One of", "One of", "String", ITEM_PURCHASE_ITEM_CATEGORY, "text", null, "is in", "",true),
     ITEM_PURCHASE_DCS(106, 0, "DCS", "DCS", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "dcs","dcs"),
     	PURCHASE_DCS_EQUALS(101, 0, "Equals", "Equals", "String", ITEM_PURCHASE_DCS, "text", null, "is", "",false),
     	PURCHASE_DCS_ONE_OF(3, 0, "One of", "One of", "String", ITEM_PURCHASE_DCS, "text", null, "is in", "",true),
     ITEM_PURCHASE_CLASS(105, 0, "Class", "Class", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "class_code","item_class"),
     	PURCHASE_CLASS_EQUALS(101, 0, "Equals", "Equals", "String", ITEM_PURCHASE_CLASS, "text", null, "is", "",false),
     	PURCHASE_CLASS_ONE_OF(3, 0, "One of", "One of", "String", ITEM_PURCHASE_CLASS, "text", null, "is in", "",true),
     ITEM_PURCHASE_SUB_CLASS(106, 0, "Subclass", "Subclass", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "subclass_code","subclass_code"),
     	PURCHASE_SUB_CLASS_CODE_EQUALS(101, 0, "Equals", "Equals", "String", ITEM_PURCHASE_SUB_CLASS, "text", null, "is", "",false),
     	PURCHASE_SUB_CLASS_ONE_OF(3, 0, "One of", "One of", "String", ITEM_PURCHASE_SUB_CLASS, "text", null, "is in", "",true),
     ITEM_PURCHASE_SKU(106, 0, "SKU", "SKU", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "sku","sku"),
     	PURCHASE_SKU_EQUALS(101, 0, "Equals", "Equals", "String", ITEM_PURCHASE_SKU, "text", null, "is", "",false),
     	PURCHASE_SKU_ONE_OF(3, 0, "One of", "One of", "String", ITEM_PURCHASE_SKU, "text", null, "is in", "",true),
	ITEM_PURCHASE_DESCRIPTION(110, 0, "Description", "Description", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "description","description"),
		PURCHASE_DESCRIPTION_EQUALS(101, 0, "Equals", "Equals", "String", ITEM_PURCHASE_DESCRIPTION, "text", null, "is", "",true),
		//PURCHASE_DESCRIPTION_ONE_OF(3, 0, "One of", "One of", "String", ITEM_PURCHASE_DESCRIPTION, "text", null, "is in", ""),
		
		ITEM_PURCHASE_UDF1(112, 0, "UDF1", "UDF1", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "udf1","udf1"),
		ITEM_PURCHASE_UDF1_EQUALS(101, 0, "Equals", "Equals", "String", ITEM_PURCHASE_UDF1, "text", null, "is", "",true),
		ITEM_PURCHASE_UDF1_ONE_OF(3, 0, "One of", "One of", "String", ITEM_PURCHASE_UDF1, "text", null, "is in", "",true),
		ITEM_PURCHASE_UDF2(113, 0, "UDF2", "UDF2", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "udf2","udf2"),
		ITEM_PURCHASE_UDF2_EQUALS(101, 0, "Equals", "Equals", "String", ITEM_PURCHASE_UDF2, "text", null, "is", "",true),
		ITEM_PURCHASE_UDF2_ONE_OF(3, 0, "One of", "One of", "String", ITEM_PURCHASE_UDF2, "text", null, "is in", "",true),
		ITEM_PURCHASE_UDF3(114, 0, "UDF3", "UDF3", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "udf3","udf3"),
		ITEM_PURCHASE_UDF3_EQUALS(101, 0, "Equals", "Equals", "String", ITEM_PURCHASE_UDF3, "text", null, "is", "",true),
		ITEM_PURCHASE_UDF3_ONE_OF(3, 0, "One of", "One of", "String", ITEM_PURCHASE_UDF3, "text", null, "is in", "",true),
		ITEM_PURCHASE_UDF4(115, 0, "UDF4", "UDF4", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "udf4","udf4"),
		ITEM_PURCHASE_UDF4_EQUALS(101, 0, "Equals", "Equals", "String", ITEM_PURCHASE_UDF4, "text", null, "is", "",true),
		ITEM_PURCHASE_UDF4_ONE_OF(3, 0, "One of", "One of", "String", ITEM_PURCHASE_UDF4, "text", null, "is in", "",true),
		ITEM_PURCHASE_UDF5(116, 0, "UDF5", "UDF5", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "udf5","udf5"),
		ITEM_PURCHASE_UDF5_EQUALS(101, 0, "Equals", "Equals", "String", ITEM_PURCHASE_UDF5, "text", null, "is", "",true),
		ITEM_PURCHASE_UDF5_ONE_OF(3, 0, "One of", "One of", "String", ITEM_PURCHASE_UDF5, "text", null, "is in", "",true),
		ITEM_PURCHASE_UDF6(117, 0, "UDF6", "UDF6", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "udf6","udf6"),
		ITEM_PURCHASE_UDF6_EQUALS(101, 0, "Equals", "Equals", "String", ITEM_PURCHASE_UDF6, "text", null, "is", "",true),
		ITEM_PURCHASE_UDF6_ONE_OF(3, 0, "One of", "One of", "String", ITEM_PURCHASE_UDF6, "text", null, "is in", "",true),
		ITEM_PURCHASE_UDF7(118, 0, "UDF7", "UDF7", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "udf7","udf7"),
		ITEM_PURCHASE_UDF7_EQUALS(101, 0, "Equals", "Equals", "String", ITEM_PURCHASE_UDF7, "text", null, "is", "",true),
		ITEM_PURCHASE_UDF7_ONE_OF(3, 0, "One of", "One of", "String", ITEM_PURCHASE_UDF7, "text", null, "is in", "",true),
		ITEM_PURCHASE_UDF8(119, 0, "UDF8", "UDF8", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "udf8","udf8"),
		ITEM_PURCHASE_UDF8_EQUALS(101, 0, "Equals", "Equals", "String", ITEM_PURCHASE_UDF8, "text", null, "is", "",true),
		ITEM_PURCHASE_UDF8_ONE_OF(3, 0, "One of", "One of", "String", ITEM_PURCHASE_UDF8, "text", null, "is in", "",true),
		ITEM_PURCHASE_UDF9(120, 0, "UDF9", "UDF9", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "udf9","udf9"),
		ITEM_PURCHASE_UDF9_EQUALS(101, 0, "Equals", "Equals", "String", ITEM_PURCHASE_UDF9, "text", null, "is", "",true),
		ITEM_PURCHASE_UDF9_ONE_OF(3, 0, "One of", "One of", "String", ITEM_PURCHASE_UDF9, "text", null, "is in", "",true),
		ITEM_PURCHASE_UDF10(121, 0, "UDF10", "UDF10", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "udf10","udf10"),
		ITEM_PURCHASE_UDF10_EQUALS(101, 0, "Equals", "Equals", "String", ITEM_PURCHASE_UDF10, "text", null, "is", "",true),
		ITEM_PURCHASE_UDF10_ONE_OF(3, 0, "One of", "One of", "String", ITEM_PURCHASE_UDF10, "text", null, "is in", "",true),
		ITEM_PURCHASE_UDF11(122, 0, "UDF11", "UDF11", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "udf11","udf11"),
		ITEM_PURCHASE_UDF11_EQUALS(101, 0, "Equals", "Equals", "String", ITEM_PURCHASE_UDF11, "text", null, "is", "",true),
		ITEM_PURCHASE_UDF11_ONE_OF(3, 0, "One of", "One of", "String", ITEM_PURCHASE_UDF11, "text", null, "is in", "",true),
		ITEM_PURCHASE_UDF12(123, 0, "UDF12", "UDF12", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "udf12","udf12"),
		ITEM_PURCHASE_UDF12_EQUALS(101, 0, "Equals", "Equals", "String", ITEM_PURCHASE_UDF12, "text", null, "is", "",true),
		ITEM_PURCHASE_UDF12_ONE_OF(3, 0, "One of", "One of", "String", ITEM_PURCHASE_UDF12, "text", null, "is in", "",true),
		ITEM_PURCHASE_UDF13(124, 0, "UDF13", "UDF13", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "udf12","udf13"),
		ITEM_PURCHASE_UDF13_EQUALS(101, 0, "Equals", "Equals", "String", ITEM_PURCHASE_UDF13, "text", null, "is", "",true),
		ITEM_PURCHASE_UDF13_ONE_OF(3, 0, "One of", "One of", "String", ITEM_PURCHASE_UDF13, "text", null, "is in", "",true),
		ITEM_PURCHASE_UDF14(125, 0, "UDF14", "UDF14", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "udf14","udf14"),
		ITEM_PURCHASE_UDF14_EQUALS(101, 0, "Equals", "Equals", "String", ITEM_PURCHASE_UDF14, "text", null, "is", "",true),
		ITEM_PURCHASE_UDF14_ONE_OF(3, 0, "One of", "One of", "String", ITEM_PURCHASE_UDF14, "text", null, "is in", "",true),
		ITEM_PURCHASE_UDF15(126, 0, "UDF15", "UDF15", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "udf15","udf15"),
		ITEM_PURCHASE_UDF15_EQUALS(101, 0, "Equals", "Equals", "String", ITEM_PURCHASE_UDF15, "text", null, "is", "",true),
		ITEM_PURCHASE_UDF15_ONE_OF(3, 0, "One of", "One of", "String", ITEM_PURCHASE_UDF15, "text", null, "is in", "",true),
		
	ITEM_PURCHASE_ITEM_PRICE(0, 2, "Item Price", "Item Price", "String", null,null, null, "is", "item_price","item_price"),
		PURCHASE_ITEM_PRICE_GREATER_OR_EQUALS(101, 0, "Greater than or equal to", "Greater than or equal to", "number", ITEM_PURCHASE_ITEM_PRICE, "double", null, ">=", ""),
		PURCHASE_ITEM_PRICE_LESSER_OR_EQUALS(101, 0, "Less than or equal to", "Less than or equal to", "number", ITEM_PURCHASE_ITEM_PRICE, "double", null, "<=", ""),
	
	//ITEM_PURCHASE_USER_ID(126, 0, "User Id", "User Id", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","userId"),
	//ITEM_PURCHASE_ITEM_SID(127, 0, "Item Sid", "Item Sid", "String", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","itemSid"),
 
   /*ITEM_PURCHASE_QUANTIY(0, 4, "Item Quantity", "Item Quantity", "string", null, null, null,null,"quantity","quantity"),
        ITEM_PURCHASE_IN_DISCOUNT(201, 0, "only non-discounted unit(s)", "exclude discount", "string", ITEM_PURCHASE_QUANTIY, "text", null, "is","quantity",""),
        ITEM_PURCHASE_EX_DISCOUNT(202, 0, "include discounted unit(s)", "include discount", "string", ITEM_PURCHASE_QUANTIY, "text", null, "is","quantity",""),*/
		
			
	
		//***************************************************************************************************
		//***************************DISCOUNT ATTRIBUTES**************************************************
		
		
	DISCOUNT_QUANTITY_PER_ITEM(0, 4, "Item Quantity to discount", "Item Quantity to discount", "String",  null,null, null,null, "quantity" ,"quantity"),
	
		DISCOUNT_QUANTITY_PER_ITEM_ALL(204, 0, "All", "All", "number", DISCOUNT_QUANTITY_PER_ITEM, null, null, "ALL", "quantity",null),
	
		DISCOUNT_QUANTITY_PER_ITEM_MAXIMUM(204, 0, "Maximum", "Maximum", "number", DISCOUNT_QUANTITY_PER_ITEM, "double", null, "LTE", "quantity",""),
		
		DISCOUNT_QUANTITY_PER_ITEM_EQUALS(204, 0, "Equals", "Equals", "number", DISCOUNT_QUANTITY_PER_ITEM, "double", null, "ET","quantity",""),
	
		DISCOUNT_QUANTITY_PER_ITEM_MINIMUM(200, 0, "Minimum", "Minimum", "number", DISCOUNT_QUANTITY_PER_ITEM,	"double", null, "GTE", "quantity",""),
	
	
	DISCOUNT_ELIGIBLE_ITEMS(101, 3, "Eligible Items", "Eligible Items", "String", null, null, null, null ,"no_of_eligible_items"),
	
		DISCOUNT_ELIGIBLE_ITEMS_ALL(204, 0, "all items", "all items", "String", DISCOUNT_ELIGIBLE_ITEMS, null, null, "ALLEI", null),
	
		DISCOUNT_ELIGIBLE_ITEMS_HIGHEST(204, 0, "highest priced items", "highest priced items", "String", DISCOUNT_ELIGIBLE_ITEMS, null, null, "HPIWD", null),

		DISCOUNT_ELIGIBLE_ITEMS_HIGHEST_NON_DISCOUNTED(200, 0, "highest priced non discounted items", "highest priced non discounted items", "String", DISCOUNT_ELIGIBLE_ITEMS,	null, null, "HPIWOD", null),
		
		DISCOUNT_ELIGIBLE_ITEMS_LOWEST(204, 0, "lowest priced items", "lowest priced items", "String", DISCOUNT_ELIGIBLE_ITEMS, null, null, "LPIWD", null),

		DISCOUNT_ELIGIBLE_ITEMS_LOWEST_NON_DISCOUNTED(200, 0, "lowest priced non discounted items", "lowest priced non discounted items", "String", DISCOUNT_ELIGIBLE_ITEMS,	null, null, "LPIWOD", null),
	
	DISCOUNT_TYPE(101, 3, "Discount Type", "Discount Type", "number", null, "double", null, null ,"discount_type","discount_type" ),
	
		DISCOUNT_TYPE_PERCENTAGE(204, 0, "Percentage", "Percentage", "number", DISCOUNT_TYPE, "double", null, "Percentage", null),
	
		DISCOUNT_TYPE_CURRENCY(204, 0, "Currency", "Currency", "number", DISCOUNT_TYPE, "double", null, "Value", null),
	

	PURCHASE_IN_TIER(4, 1, "Purchase in Tier","[#PurchaseTier#]", "String", null, null, null, "", ""),
		LOYALTY_PROGRAM(101,0, "Select Program", "Select Program", "String",PURCHASE_IN_TIER ,null, null, "is", ""),
		LOYALTY_PROGRAM_TIER(101,0, "Select Tier", "Select Tier", "json",LOYALTY_PROGRAM , null, null, "is", ""),
		
	PURCHASE_IN_CARD_SET(5, 1, "Purchase in Card-Set","[#PurchaseCardSet#]",  "String", null, null, null, "", ""),
	    LOYALTY_PROGRAM_CARDSET(101,0, "Select Program", "Select Program", "String",PURCHASE_IN_CARD_SET ,null, null, "is", ""),
	    LOYALTY_PROGRAM_CARDSET_CARD_SET(102,0, "Select Card-set", "Select Card-set", "json",LOYALTY_PROGRAM_CARDSET , null, null, "is", ""),


	;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private int code;
	private int categoryCode;
	private boolean isVisible ;
	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}


	private String dispLabel;
	private String item;
	private String type;
	private CouponsEnum parentEnum;
	private String type1;
	private String type2;
	private String token;
	private String columnName;
	private String columnValue;
	private String selectFieldName;
	private String innerQryColumnName;
	private boolean allowCommas;
	
	public String getInnerQryColumnName() {
		return innerQryColumnName;
	}

	public void setInnerQryColumnName(String innerQryColumnName) {
		this.innerQryColumnName = innerQryColumnName;
	}

	public String getSelectFieldName() {
		return selectFieldName;
	}

	public void setSelectFieldName(String selectFieldName) {
		this.selectFieldName = selectFieldName;
	}


	private boolean isMappingField;
	private String posCustomFieldLbl;
	private String ignoreYearToken;
	

	public String getIgnoreYearToken() {
		return ignoreYearToken;
	}

	public void setIgnoreYearToken(String ignoreYearToken) {
		this.ignoreYearToken = ignoreYearToken;
	}

	private CouponsEnum(int code, int categoryCode, String dispLabel,String item , String type, CouponsEnum parentEnum, 
				String type1, String type2, String token, String columnName  ) {
		
		this(code, categoryCode, dispLabel,item, type, parentEnum, type1, type2, token, columnName ,null );

	}
	private CouponsEnum(int code, int categoryCode, String dispLabel,String item , String type, CouponsEnum parentEnum, 
			String type1, String type2, String token, String columnName,boolean allowCommas) {
		this.code = code;
		this.categoryCode = categoryCode;
		this.dispLabel = dispLabel;
		this.item = item;
		this.type = type;
		this.parentEnum = parentEnum;
		this.type1 = type1;
		this.type2 = type2;
		this.token = token;
		this.columnName = columnName;
		this.allowCommas = allowCommas;	

	}
	private CouponsEnum(int code, int categoryCode, String dispLabel,boolean isvisible, String item , String type, CouponsEnum parentEnum, 
			String type1, String type2, String token, String columnName  ) {
	
		this.code = code;
		this.categoryCode = categoryCode;
		this.dispLabel = dispLabel;
		this.type = type;
		this.parentEnum = parentEnum;
		this.type1 = type1;
		this.type2 = type2;
		this.token = token;
		this.columnName = columnName;
		this.item = item;
		this.isVisible = isvisible;
		

}
	private CouponsEnum(int code, int categoryCode, String dispLabel, String item, String type, CouponsEnum parentEnum, 
				String type1, String type2, String token, String columnName , String columnValue ) {
		this.code = code;
		this.categoryCode = categoryCode;
		this.dispLabel = dispLabel;
		this.type = type;
		this.parentEnum = parentEnum;
		this.type1 = type1;
		this.type2 = type2;
		this.token = token;
		this.columnName = columnName;
		this.columnValue = columnValue;
		this.item = item;
		
	}
	
	private CouponsEnum(int code, int categoryCode, String dispLabel, String item, boolean isMappingFiled, String posCustomFieldLbl, String type, CouponsEnum parentEnum, 
			String type1, String type2, String token, String columnName  ) {
		
		this(code, categoryCode, dispLabel, item, isMappingFiled, posCustomFieldLbl, type, parentEnum, type1, type2, token, columnName ,null );
	

	}
	
	
	
	private CouponsEnum(int code, int categoryCode, String dispLabel, String item, boolean isMappingFiled, String posCustomFieldLbl, String type, CouponsEnum parentEnum, 
			String type1, String type2, String token, String columnName , String columnValue ) {
	this.code = code;
	this.categoryCode = categoryCode;
	this.dispLabel = dispLabel;
	this.type = type;
	this.parentEnum = parentEnum;
	this.type1 = type1;
	this.type2 = type2;
	this.token = token;
	this.columnName = columnName;
	this.columnValue = columnValue;
	this.isMappingField = isMappingFiled;
	this.posCustomFieldLbl = posCustomFieldLbl;
	this.item = item;
	
	}
	//PURCHASE_TOTAL_PURCHASE_VALUE_AVG(7, 0, "Avg", "number", PURCHASE_TOTAL_PURCHASE_VALUE, null, null, null,"", "new_avg", "", ",avg((sal.sales_price*sal.quantity)+sal.tax) as new_avg"),
	private CouponsEnum(int code, int categoryCode, String dispLabel, String item, String type, CouponsEnum parentEnum, 
			String type1, String type2, String token, String columnName , String columnValue, String selectFieldName, String innerQryColumnName ) {
	this.code = code;
	this.categoryCode = categoryCode;
	this.dispLabel = dispLabel;
	this.type = type;
	this.parentEnum = parentEnum;
	this.type1 = type1;
	this.type2 = type2;
	this.token = token;
	this.columnName = columnName;
	this.columnValue = columnValue;
	this.selectFieldName = selectFieldName;
	this.item = item;
	this.innerQryColumnName = innerQryColumnName;
	
	}
	
	private CouponsEnum(int code, int categoryCode, String dispLabel, String item, String type, CouponsEnum parentEnum, 
			String type1, String type2, String token, String columnName , String columnValue, String selectFieldName  ) {
	this.code = code;
	this.categoryCode = categoryCode;
	this.dispLabel = dispLabel;
	this.type = type;
	this.parentEnum = parentEnum;
	this.type1 = type1;
	this.type2 = type2;
	this.token = token;
	this.columnName = columnName;
	this.columnValue = columnValue;
	this.selectFieldName = selectFieldName;
	this.item = item;
	
	}
	
	public void setCode(int code) {
		this.code = code;
	}

	public void setCategoryCode(int categoryCode) {
		this.categoryCode = categoryCode;
	}

	public void setDispLabel(String dispLabel) {
		this.dispLabel = dispLabel;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setParentEnum(CouponsEnum parentEnum) {
		this.parentEnum = parentEnum;
	}

	public void setType1(String type1) {
		this.type1 = type1;
	}

	public void setType2(String type2) {
		this.type2 = type2;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}





	
	
	
	public String getColumnValue() {
		return columnValue;
	}

	public void setColumnValue(String columnValue) {
		this.columnValue = columnValue;
	}

	
	public int getCode() {
		return code;
	}

	public String getDispLabel() {
		return dispLabel;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}
	
	public String getType() {
		return type;
	}

	
		
	public CouponsEnum getParentEnum() {
		return parentEnum;
	}

	public String getType1() {
		return type1;
	}

	public String getType2() {
		return type2;
	}

	private CouponsEnum() {
		
	}
	
	public String getToken() {
		return token;
	}

	public String getColumnName() {
		return columnName;
	}

	public boolean isAllowCommas() {
		return allowCommas;
	}

	public void setAllowCommas(boolean allowCommas) {
		this.allowCommas = allowCommas;
	}

	public int getCategoryCode() {
		return categoryCode;
	}
	
	public String getPosCustomFieldLbl() {
		return posCustomFieldLbl;
	}

	public void setPosCustomFieldLbl(String posCustomFieldLbl) {
		this.posCustomFieldLbl = posCustomFieldLbl;
	}

	public boolean isMappingField() {
		return isMappingField;
	}
	
	public void setMappingField(boolean isMappingField) {
		this.isMappingField = isMappingField;
	}
	
	public List<CouponsEnum> getChidrenByParentEnum(CouponsEnum parentEnum) {
		List<CouponsEnum> childEnumList = new ArrayList<CouponsEnum>();
		CouponsEnum[] childEnum = CouponsEnum.values();
		for (CouponsEnum couponsEnum : childEnum) {
			//logger.info("parentEnum "+parentEnum+" couponsEnum "+couponsEnum);
			
			if(couponsEnum.getParentEnum() == null) continue;
			
			if(couponsEnum.getParentEnum().name().equals(parentEnum.name())) {
				childEnumList.add(couponsEnum);
			}
			
		}
		
		return childEnumList;
		
		
		
	}
	
	
	public static List<CouponsEnum> getEnumsByCategoryCode(int categoryCode) {
		
		List<CouponsEnum> enumList = new ArrayList<CouponsEnum>();
		
		CouponsEnum[] childEnum = CouponsEnum.values();
		for (CouponsEnum couponsEnum : childEnum) {
			//logger.info("parentEnum "+parentEnum+" couponsEnum "+couponsEnum);
			
			if(couponsEnum.getCategoryCode() == categoryCode) {
				
				enumList.add(couponsEnum);
				
			}//if
			
		}//for
		
		
		return enumList;
		
		
	}
	public List<String> getChidrenByParentEnumForPurchaseItemForString(CouponsEnum parentEnum) {
		Users users = GetUser.getUserObj();
		POSMappingDao posMappingDao= (POSMappingDao)SpringUtil.getBean("posMappingDao");
		List<POSMapping> posList=	posMappingDao.findByTypeForSKU(Constants.POS_MAPPING_TYPE_SKU,users.getUserId());
		List<String> postAttributeList = new ArrayList<String>();
		CouponsEnum[] childEnum = CouponsEnum.values();
		for (CouponsEnum segmentSpecialRules : childEnum) {
			if(segmentSpecialRules.getParentEnum() == null) continue;
			if(segmentSpecialRules.getParentEnum().name().equals(parentEnum.name())) {
			posList.forEach(mapping->{
				if(mapping.getCustomFieldName().equals(segmentSpecialRules.getItem()))
					postAttributeList.add(mapping.getDisplayLabel());
						
			});
			}
		}
		return postAttributeList;
	}
	public List<CouponsEnum> getChidrenByParentEnumForPurchaseItem(CouponsEnum couponsEnum) {
		Users users = GetUser.getUserObj();
		POSMappingDao posMappingDao= (POSMappingDao)SpringUtil.getBean("posMappingDao");
		List<POSMapping> posList=	posMappingDao.findByTypeForSKU(Constants.POS_MAPPING_TYPE_SKU,users.getUserId());
		List<CouponsEnum> childEnumList = new ArrayList<CouponsEnum>();
		CouponsEnum[] childEnum = CouponsEnum.values();
		for (CouponsEnum segmentSpecialRules : childEnum) {
			if(segmentSpecialRules.getParentEnum() == null) continue;
			if(segmentSpecialRules.getParentEnum().name().equals(couponsEnum.name())) {
			posList.forEach(mapping->{
				if(mapping.getCustomFieldName().equals(segmentSpecialRules.getItem()))
						childEnumList.add(segmentSpecialRules);
						
			});
			}
		}
		return childEnumList;
	}
	
	public  CouponsEnum createAndReturnNewCouponsEnum(int code, 
			int categoryCode, String dispLabel, String type, CouponsEnum parentEnum,
			String type1, String type2, String token, String columnName  )  {
		
		
		try {
			CouponsEnum newCouponsEnum = (CouponsEnum)this.clone(); 
			newCouponsEnum.setCategoryCode(2);
			
			
			
			
			
			return newCouponsEnum;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}
		
	}
	
	
	
	public static CouponsEnum getUDFEnumByColumn(String customfieldname, int category) {
		
		
		
		
		CouponsEnum[] childEnum = CouponsEnum.values();
		for (CouponsEnum couponsEnum : childEnum) {
			//logger.info("parentEnum "+parentEnum+" couponsEnum "+couponsEnum);
			
			if(couponsEnum.getCategoryCode() == category && couponsEnum.getColumnName().endsWith(customfieldname.toLowerCase())) {
				
				return couponsEnum;
				
			}//if
			
		}//for
		
		
		return null;
		
		
		
	}
	
	
public static CouponsEnum getEnumByColumn(String customfieldname) {
		
	CouponsEnum[] childEnum = CouponsEnum.values();
		
		
		for (CouponsEnum couponsEnum : childEnum) {
			//logger.info("parentEnum "+parentEnum+" couponsEnum "+couponsEnum);
			if(couponsEnum.getColumnName() == null || couponsEnum.getColumnName().isEmpty()) continue;
			if(couponsEnum.getColumnName().equalsIgnoreCase(customfieldname) ) {
				
				return couponsEnum;
				
			}//if
			
		}//for
		
		
		return null;
		
		
		
	}

public static CouponsEnum getEnumByItem(String item) {
	
	CouponsEnum[] childEnum = CouponsEnum.values();
	
	
	for (CouponsEnum couponsEnum : childEnum) {
		//logger.info("parentEnum "+parentEnum+" couponsEnum "+couponsEnum);
		if(couponsEnum.getItem() == null || couponsEnum.getItem().isEmpty()) continue;
		if(couponsEnum.getItem().equalsIgnoreCase(item) ) {
			
			return couponsEnum;
			
		}//if
		
	}//for
	
	
	return null;
	
	
	
}
	
public static CouponsEnum getEnumByColumn(String columnName, String value) {
	
	CouponsEnum[] childEnum = CouponsEnum.values();
	
	
	for (CouponsEnum couponsEnum : childEnum) {
		//logger.info("parentEnum "+parentEnum+" couponsEnum "+couponsEnum);
		if(couponsEnum.getColumnName() == null || couponsEnum.getColumnName().isEmpty()) continue;
		if(couponsEnum.getColumnName().equalsIgnoreCase(columnName)) {
			
			if(couponsEnum.getColumnValue() != null 
					&& !couponsEnum.getColumnValue().isEmpty() 
					&& couponsEnum.getColumnValue().equals(value)) {
				
				return couponsEnum;
				
			}//if
			
		}//if
		
	}//for
	
	
	return null;
	
	
	
}

	
	
	


}
