package org.mq.optculture.controller.loyalty;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.general.Constants;
import org.zkoss.zkplus.spring.SpringUtil;

public enum SpecialRewardEnum {


	 
	//*************************Special Rule ATTRIBUTES ***************************************
	
	    ITEM_PURCHASE_ATTRIBUTE(0, 1, "Item Attribute", "[#PurchasedItem#]", "string", null, null, null,"",""),
	   
	        ITEM_PURCHASE_VENDOR_CODE(105, 0, "Vendor Code", "Vendor", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","vendorCode"),
	        ITEM_PURCHASE_DEPT_CODE(106, 0, "Dept. Code", "Department", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","departmentCode"),
	        ITEM_PURCHASE_ITEM_CATEGORY(105, 0, "Item Category", "Item Category", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","itemCategory"),
	        ITEM_PURCHASE_DCS(106, 0, "DCS", "DCS", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","DCS"),
	        ITEM_PURCHASE_CLASS(105, 0, "Class", "Class", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","itemClass"),
	        ITEM_PURCHASE_SUB_CLASS(106, 0, "Sub-Class", "Subclass", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","itemSubClass"),
	        ITEM_PURCHASE_SKU(106, 0, "SKU", "SKU", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","skuNumber"),
	        ITEM_PURCHASE_SKUID(107, 0, "SKUID", "SKUID", "json", ITEM_PURCHASE_ATTRIBUTE, "number", null, "is", "items","","skuId"),
		ITEM_PURCHASE_LISTID(108, 0, "LISTID", "LISTID", "json", ITEM_PURCHASE_ATTRIBUTE, "number", null, "is", "items","","listId"),
		ITEM_PURCHASE_STORE_NUMBER(109, 0, "Store Number", "Store Number", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","storeNumber"),
		ITEM_PURCHASE_DESCRIPTION(110, 0, "Description", "Description", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","description"),
		//ITEM_PURCHASE_LIST_PRICE(111, 0, "List Price", "List Price", "json", ITEM_PURCHASE_ATTRIBUTE, "double", null, "is", "items","","listPrice"),
		ITEM_PURCHASE_UDF1(112, 0, "UDF1", "UDF1", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","udf1"),
		ITEM_PURCHASE_UDF2(113, 0, "UDF2", "UDF2", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","udf2"),
		ITEM_PURCHASE_UDF3(114, 0, "UDF3", "UDF3", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","udf3"),
		ITEM_PURCHASE_UDF4(115, 0, "UDF4", "UDF4", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","udf4"),
		ITEM_PURCHASE_UDF5(116, 0, "UDF5", "UDF5", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","udf5"),
		ITEM_PURCHASE_UDF6(117, 0, "UDF6", "UDF6", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","udf6"),
		ITEM_PURCHASE_UDF7(118, 0, "UDF7", "UDF7", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","udf7"),
		ITEM_PURCHASE_UDF8(119, 0, "UDF8", "UDF8", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","udf8"),
		ITEM_PURCHASE_UDF9(120, 0, "UDF9", "UDF9", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","udf9"),
		ITEM_PURCHASE_UDF10(121, 0, "UDF10", "UDF10", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","udf10"),
		ITEM_PURCHASE_UDF11(122, 0, "UDF11", "UDF11", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","udf11"),
		ITEM_PURCHASE_UDF12(123, 0, "UDF12", "UDF12", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","udf12"),
		ITEM_PURCHASE_UDF13(124, 0, "UDF13", "UDF13", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","udf13"),
		ITEM_PURCHASE_UDF14(125, 0, "UDF14", "UDF14", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","udf14"),
		ITEM_PURCHASE_UDF15(126, 0, "UDF15", "UDF15", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","udf15"),
		ITEM_PURCHASE_USER_ID(126, 0, "User Id", "User Id", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","userId"),
		//ITEM_PURCHASE_ITEM_SID(127, 0, "Item Sid", "Item Sid", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","itemSid"),
		ITEM_PURCHASE_CLASS_CODE(128, 0, "Class Code", "Class Code", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","classCode"),
		ITEM_PURCHASE_SUB_CLASS_CODE(129, 0, "Sub Class Code", "Sub Class Code", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","subClassCode"),
		ITEM_PURCHASE_SUBSIDIARY_NUMBER(130, 0, "Subsidiary Number", "Subsidiary Number", "json", ITEM_PURCHASE_ATTRIBUTE, "text", null, "is", "items","","subsidiaryNumber"),
        
	      ITEM_PURCHASE_QUANTIY(0, 4, "Item Quantity", "[#ItemFactor#]", "string", null, null, null,null,"","",""),
	           ITEM_PURCHASE_IN_DISCOUNT(201, 0, "only non-discounted unit(s)", "exclude discount", "string", ITEM_PURCHASE_QUANTIY, "text", null, "is", "","E",""),
	           ITEM_PURCHASE_EX_DISCOUNT(202, 0, "include discounted unit(s)", "include discount", "string", ITEM_PURCHASE_QUANTIY, "text", null, "is", "","I",""),
	   		
	             ITEM_PURCHASE_SINGLE_IN_DIS(201, 0, "in a single purchase", "in a single purchase", "string", ITEM_PURCHASE_IN_DISCOUNT, null, null, "is", "","S",""),
	             ITEM_PURCHASE_MULTIPLE_IN_DIS(202, 0, "across multiple purchases", "across multiple purchases", "string", ITEM_PURCHASE_IN_DISCOUNT, null, null, "is", "","M",""),
	             ITEM_PURCHASE_SINGLE_EX_DIS(203, 0, "in a single purchase", "in a single purchase", "string", ITEM_PURCHASE_EX_DISCOUNT, null, null, "is", "","S",""),
	             ITEM_PURCHASE_MULTIPLE_EX_DIS(204, 0, "across multiple purchases", "across multiple purchases", "string", ITEM_PURCHASE_EX_DISCOUNT, null, null, "is", "","M",""),
	   		RECEIPT_TOTAL_ATTRIBUTE(8, 1, "Minimum Receipt Total", "[#Minimum Receipt Total#]", "string", null, null,null,"total_purchase_amt","total_purchase_amt"),
	        		RECEIPT_TOTAL_EQUALS(101, 0, "", "", "number", RECEIPT_TOTAL_ATTRIBUTE, "text", null, "=", ""),
	        		
		
		VISITS(1, 1, "Visits","[#Visit#]",   "String", null, null, null, "",""),	
			VISITS_REPEATE(101, 0, "For every cycle of", "For every cycle of", "string", VISITS, "text", null, "is", "","R",""),
			VISITS_FIRST_TIME(102, 0, "For only first cycle of", "For only first cycle of", "string", VISITS, "text", null, "is","", "F",""),
						
		PURCHASE_DATE(2, 1, "Purchase Day/Time","[#PuchaseDate#]", "date", null, null, null, "", ""),				
			TIME_OF_THE_DAY(301,0, "Time of the day", "Time of the day", "time", PURCHASE_DATE, "23", "1", "is", "","TOD",""),
			DAY_OF_THE_WEEK(302,0, "Day of the week", "Day of the week", "date", PURCHASE_DATE, "7", "7", "is", "","DOW",""),
			DAY_OF_THE_MONTH(303,0, "Day of the month", "Day of the month", "date", PURCHASE_DATE, "31", "1", "is", "","DOM",""),
			SPECIAL_DAY(304,0, "Special day", "Special day", "date", PURCHASE_DATE, "date", null, "is", "","Day",""),
			SHOPPING_DAYS_ARE(304,0, "Shopping days are", "Shopping days are", "date", PURCHASE_DATE, "date", "date", "","", "Dates",""),
		
		PURCHASE_IN_STORE(3, 1, "Purchase in Store","[#PurchaseStore#]", "String", null, null, null, "", "","","storeNumber"),
			STORE(101,0, "Select Store", "Select Store", "json",PURCHASE_IN_STORE , null, null, "is", ""),
	
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
	private String dispLabel;
	private String item;
	private String type;
	private SpecialRewardEnum parentEnum;
	private String type1;
	private String type2;
	private String token;
	private String columnName;
	private String columnValue;
	private String selectFieldName;
	private String innerQryColumnName;
	private String jsonAttribute;
	
	
	
	public String getJsonAttribute() {
		return jsonAttribute;
	}

	public void setJsonAttribute(String jsonAttribute) {
		this.jsonAttribute = jsonAttribute;
	}

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
	
	private SpecialRewardEnum(int code, int categoryCode, String dispLabel,String item , String type, SpecialRewardEnum parentEnum, 
				String type1, String type2, String token, String columnName  ) {
		
		this(code, categoryCode, dispLabel,item, type, parentEnum, type1, type2, token, columnName ,null,null );

	}
	
	private SpecialRewardEnum(int code, int categoryCode, String dispLabel, String item, String type, SpecialRewardEnum parentEnum, 
				String type1, String type2, String token, String columnName , String columnValue,String jsonAttribute ) {
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
		this.jsonAttribute=jsonAttribute;
		
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

	public void setParentEnum(SpecialRewardEnum parentEnum) {
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
	
	public SpecialRewardEnum getParentEnum() {
		return parentEnum;
	}

	public String getType1() {
		return type1;
	}

	public String getType2() {
		return type2;
	}

	private SpecialRewardEnum() {
		
	}
	
	public String getToken() {
		return token;
	}

	public String getColumnName() {
		return columnName;
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
	
	public List<SpecialRewardEnum> getChidrenByParentEnum(SpecialRewardEnum parentEnum) {
		List<SpecialRewardEnum> childEnumList = new ArrayList<SpecialRewardEnum>();
		SpecialRewardEnum[] childEnum = SpecialRewardEnum.values();
		for (SpecialRewardEnum segmentEnum : childEnum) {
			if(segmentEnum.getParentEnum() == null) continue;
			if(segmentEnum.getParentEnum().name().equals(parentEnum.name())) {
				childEnumList.add(segmentEnum);
			}
		}
		
		return childEnumList;
		
	}
	public List<SpecialRewardEnum> getChidrenByParentEnumForPurchaseItem(SpecialRewardEnum parentEnum) {
		Users users = GetUser.getUserObj();
		POSMappingDao posMappingDao= (POSMappingDao)SpringUtil.getBean("posMappingDao");
		List<POSMapping> posList=	posMappingDao.findByTypeForSKU(Constants.POS_MAPPING_TYPE_SKU,users.getUserId());
		List<SpecialRewardEnum> childEnumList = new ArrayList<SpecialRewardEnum>();
		SpecialRewardEnum[] childEnum = SpecialRewardEnum.values();
		for (SpecialRewardEnum segmentSpecialRules : childEnum) {
			if(segmentSpecialRules.getParentEnum() == null) continue;
			if(segmentSpecialRules.getParentEnum().name().equals(parentEnum.name())) {
			posList.forEach(mapping->{
				if(mapping.getCustomFieldName().equals(segmentSpecialRules.getItem()))
						childEnumList.add(segmentSpecialRules);
						
			});
			}
		}
		return childEnumList;
	}
	
	public List<String> getChidrenByParentEnumForPurchaseItemForString(SpecialRewardEnum parentEnum) {
		Users users = GetUser.getUserObj();
		POSMappingDao posMappingDao= (POSMappingDao)SpringUtil.getBean("posMappingDao");
		List<POSMapping> posList=	posMappingDao.findByTypeForSKU(Constants.POS_MAPPING_TYPE_SKU,users.getUserId());
		List<String> postAttributeList = new ArrayList<String>();
		SpecialRewardEnum[] childEnum = SpecialRewardEnum.values();
		for (SpecialRewardEnum segmentSpecialRules : childEnum) {
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
	
	
	
	
	
	public static List<SpecialRewardEnum> getEnumsByCategoryCode(int categoryCode) {
		
		List<SpecialRewardEnum> enumList = new ArrayList<SpecialRewardEnum>();
		
		SpecialRewardEnum[] childEnum = SpecialRewardEnum.values();
		for (SpecialRewardEnum SegmentSpecialRules : childEnum) {
			//logger.info("parentEnum "+parentEnum+" SegmentSpecialRules "+SegmentSpecialRules);
			
			if(SegmentSpecialRules.getCategoryCode() == categoryCode) {
				
				enumList.add(SegmentSpecialRules);
				
			}//if
			
		}//for
		
		
		return enumList;
		
		
	}
	
	
	
	public  SpecialRewardEnum createAndReturnNewSegmentEnum(int code, 
			int categoryCode, String dispLabel, String type, SpecialRewardEnum parentEnum,
			String type1, String type2, String token, String columnName  )  {
		
		
		try {
			SpecialRewardEnum newSegmentEnum = (SpecialRewardEnum)this.clone(); 
			newSegmentEnum.setCategoryCode(2);
			
			
			
			
			
			return newSegmentEnum;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}
		
	}
	
	
	
	public static SpecialRewardEnum getUDFEnumByColumn(String customfieldname, int category) {
		
		
		
		
		SpecialRewardEnum[] childEnum = SpecialRewardEnum.values();
		for (SpecialRewardEnum SegmentSpecialRules : childEnum) {
			//logger.info("parentEnum "+parentEnum+" SegmentSpecialRules "+SegmentSpecialRules);
			
			if(SegmentSpecialRules.getCategoryCode() == category && SegmentSpecialRules.getColumnName().endsWith(customfieldname.toLowerCase())) {
				
				return SegmentSpecialRules;
				
			}//if
			
		}//for
		
		
		return null;
		
		
		
	}
	
	
	
	/*public static findSelectFieldNameByColName(String colName) {
		
		SegmentSpecialRules[] childEnum = SegmentSpecialRules.values();
		
		for (SegmentSpecialRules SegmentSpecialRules : childEnum) {
			//logger.info("parentEnum "+parentEnum+" SegmentSpecialRules "+SegmentSpecialRules);
			
			if(SegmentSpecialRules.getColumnName().equalsIgnoreCase(customfieldname) ) {
				
				return SegmentSpecialRules;
				
			}//if
			
		}//for
		
		
		return null;
		
		
	}*/
	
	
public static SpecialRewardEnum getEnumByColumn(String customfieldname) {
		
		SpecialRewardEnum[] childEnum = SpecialRewardEnum.values();
		
		
		for (SpecialRewardEnum SegmentSpecialRules : childEnum) {
			//logger.info("parentEnum "+parentEnum+" SegmentSpecialRules "+SegmentSpecialRules);
			if(SegmentSpecialRules.getColumnName() == null || SegmentSpecialRules.getColumnName().isEmpty()) continue;
			if(SegmentSpecialRules.getColumnName().equalsIgnoreCase(customfieldname) ) {
				
				return SegmentSpecialRules;
				
			}//if
			
		}//for
		
		
		return null;
		
		
		
	}

public static SpecialRewardEnum getEnumByItem(String item) {
	
	SpecialRewardEnum[] childEnum = SpecialRewardEnum.values();
	
	
	for (SpecialRewardEnum SegmentSpecialRules : childEnum) {
		//logger.info("parentEnum "+parentEnum+" SegmentSpecialRules "+SegmentSpecialRules);
		if(SegmentSpecialRules.getItem() == null || SegmentSpecialRules.getItem().isEmpty()) continue;
		if(SegmentSpecialRules.getItem().equalsIgnoreCase(item) ) {
			
			return SegmentSpecialRules;
			
		}//if
		
	}//for
	
	
	return null;
	
	
	
}
	
public static SpecialRewardEnum getEnumByColumn(String columnName, String value) {
	
	SpecialRewardEnum[] childEnum = SpecialRewardEnum.values();
	
	
	for (SpecialRewardEnum SegmentSpecialRules : childEnum) {
		//logger.info("parentEnum "+parentEnum+" SegmentSpecialRules "+SegmentSpecialRules);
		if(SegmentSpecialRules.getColumnName() == null || SegmentSpecialRules.getColumnName().isEmpty()) continue;
		if(SegmentSpecialRules.getColumnName().equalsIgnoreCase(columnName)) {
			
			if(SegmentSpecialRules.getColumnValue() != null 
					&& !SegmentSpecialRules.getColumnValue().isEmpty() 
					&& SegmentSpecialRules.getColumnValue().equals(value)) {
				
				return SegmentSpecialRules;
				
			}//if
			
		}//if
		
	}//for
	
	
	return null;
	
	
	
}

	
	
	

}
