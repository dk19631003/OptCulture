package org.mq.captiway.scheduler;

import java.util.HashMap;
import java.util.Map;


public enum POSFieldsEnum {
	
		
	
	//pojo reflection Field, pos attribute(CSV), OC attribute, DBColumn, DR key field, phAttribute ,Datatype
	
	firstName(1,"FirstName", "FirstName", "First Name", "first_name", "Receipt::BillToFName","firstName", "String"),
	lastName(2, "LastName" , "LastName" , "Last Name", "last_name", "Receipt::BillToLName", "lastName", "String"),
	addressOne(3, "AddressOne", "Street" , "Street", "address_one", "Receipt::BillToAddr1", "addressOne", "String"),
	addressTwo(4, "AddressTwo", "AddressTwo" , "Address Two", "address_two", "", "addressTwo", "String"),
	city(5,"City", "City" , "City", "city", "Receipt::BillToAddr2", "city", "String"),
	state(6,"State", "State" , "State", "state", "Receipt::BillToAddr3", "state", "String"),
	country(7,"Country", "Country" , "Country", "country", "", "country", "String"),
	zip(8,"Zip", "ZIP" , "ZIP", "zip", "Receipt::BillToZip", "pin", "String" ),
	gender(9,"Gender","Gender" , "Gender", "gender", "", "gender", "String"  ),
	emailId(10,"EmailId", "Email" , "Email", "email_id", "Receipt::BillToEMail", "email", "String" ),
	externalId(11, "ExternalId", "CustomerID" , "Customer ID", "external_id", "Receipt::BillToCustSID", "", "String"),
	//addressUnitId(12, "AddressUnitId", "AddressUnitID" , "Addressunit ID", "String"),
	mobilePhone(13,"MobilePhone","MobilePhone" , "Mobile", "mobile_phone", "Receipt::BillToPhone1,Receipt::BillToPhone2", "phone", "String" ),
	birthDay(14,"BirthDay", "BirthDay" , "BirthDay", "birth_day", "", "birthday", "Date"),
	anniversary(15,"Anniversary", "Anniversary" , "Anniversary", "anniversary", "", "anniversary", "Date" ),
	homeStore(16,"HomeStore", "Store" , "Home Store", "home_store", "Receipt::Store", "", "String" ),
	subsidiaryNumber(16,"SubsidiaryNumber", "Subsidiary Number" , "Subsidiary Number", "subsidiary_number", "Receipt::SubsidiaryNumber", "", "String" ),
	//pojo reflection Field, pos attribute(CSV), OC attribute, DBColumn, DR key field, phAttribute ,Datatype
	udf1(17, "Udf1", null, "UDF1", "udf1", "", "UDF1", "String" ),
	udf2(18, "Udf2", null, "UDF2", "udf2", "", "UDF2", "String"),
	udf3(19, "Udf3", null, "UDF3", "udf3", "", "UDF3", "String"),
	udf4(20, "Udf4", null, "UDF4", "udf4", "", "UDF4", "String"),
	udf5(21, "Udf5", null, "UDF5", "udf5", "", "UDF5", "String"),
	udf6(22, "Udf6", null, "UDF6", "udf6", "", "UDF6", "String"),
	udf7(23, "Udf7", null, "UDF7", "udf7", "", "UDF7", "String"),
	udf8(24, "Udf8", null, "UDF8", "udf8", "", "UDF8", "String"),
	udf9(25, "Udf9", null, "UDF9", "udf9", "", "UDF9", "String"),
	udf10(26, "Udf10", null, "UDF10", "udf10",  "", "UDF10", "String"),
	udf11(27, "Udf11", null, "UDF11", "udf11",  "", "UDF11","String"),
	udf12(28, "Udf12", null, "UDF12", "udf12",  "", "UDF12", "String"),
	udf13(29, "Udf13", null, "UDF13", "udf13", "", 	"UDF13", "String"),
	udf14(30, "Udf14", null, "UDF14", "udf14", "", 	"UDF14", "String"),
	udf15(31, "Udf15", null, "UDF15", "udf15", "", 	"UDF15", "String"),
	
	//pojo reflection Field, pos attribute(CSV), OC attribute, DBColumn, DR key field, phAttribute ,Datatype
	//sales and sku fields
	recieptNumber(32, "RecieptNumber", "RecieptNumber", "Receipt Number", "reciept_number", "Receipt::InvcNum", "", "String"),
	salesDate(33, "SalesDate", "SalesDate", "Sale Date",  "sales_date", "Receipt::DocDate", "", "Calendar"),
	quantity(34, "Quantity", "Quantity", "Quantity", "quantity", "Items::Qty", "", "Double"),
	salesPrice(35, "SalesPrice", "SalesPrice", "Sale Price", "sales_price", "Items::InvcItemPrc", "", "Double"),
	tax(36, "Tax", "Tax", "Tax", "tax", "Items::ExtTotalTax", "", "Double"),
	promoCode(37, "PromoCode", "PromoCode", "Promo Code", "promo_code", "Receipt::TrackingNum", "", "String"),
	storeNumber(38, "StoreNumber", "StoreNumber", "Store Number", "store_number", "Receipt::Store", "", "String"),
	sku(39, "Sku", "Sku", "SKU", "sku", "Items::DCS,Items::ItemLookup,Items::UPC,Items::ALU", "", "String"),
	tenderType(40, "TenderType", "TenderType", "Tender Type", "tender_type", "Receipt::Tender", "", "String"),
	itemSid(41,"ItemSid", "ItemSid", "Item Sid", "item_sid", "Items::ItemSID", "", "String"),
	description(42, "Description", "Description", "Description", "description", "Items::Desc1,Items::Desc2", "", "String"),
	listPrice(43, "ListPrice", "ListPrice", "List Price", "list_price", "Items::InvcItemPrc", "", "Double"),
	itemCategory(44, "ItemCategory", "ItemCategory", "Item Category", "item_category", "Items::DCSName,Items::DCS", "", "String"),
	docSid(45, "DocSid","Doc_Sid", "Doc Sid", "doc_sid", "Receipt::DocSID", "", "String"),
	customerId(46, "CustomerId", "CustomerID", "Customer ID", "customer_id", "Receipt::BillToCustSID", "", "String"),
	createdDate(47,"CreatedDate", "CreatedDate" , "Created Date", "created_date", "", "createdDate", "String" ),
	/*discount(53, "Discount","Discount", "Discount", "Discount", "Receipt::Discount", "", "Double"),*/ // added in 2.4.6
	discount(53, "Discount","Discount", "Discount", "Discount", "Items::DocItemDisc", "", "Double"),
	SBSNumber(16,"SubsidiaryNumber", "Subsidiary Number" , "Subsidiary Number", "subsidiary_number", "Items::SubsidiaryNumber", "", "String" ),
	
	
	
	//pojo reflection Field, pos attribute(CSV), OC attribute, DBColumn, DR key field, phAttribute ,Datatype
	vendorCode(48,"VendorCode", "VC" , "Vendor", "vendor_code", "VendorCode", "", "String" ),
	departMentCode(49,"DepartmentCode", "D_Code" , "Department", "department_code", "DepartmentCode", "", "String" ),
	departMentName(49,"DepartmentName", "D_Code" , "Department", "department_code", "DepartmentCode", "", "String" ),
	classCode(50,"ClassCode", "C_Code" , "Class", "class_code", "ItemClass", "", "String" ),
	Sclass(51,"SubClassCode", "S_Code" , "Subclass", "subclass_code", "ItemSubClass", "", "String" ),
	dcs(52,"DCS", "DCS" , "DCS", "dcs", "DCS", "", "String" ),
	;
	

    //private static final Map<String, POSFieldsEnum> ocAttrMap = new HashMap<String, POSFieldsEnum>();
    //private static final Map<String, POSFieldsEnum> ocTodbAttrMap = new HashMap<String, POSFieldsEnum>();
   /* 
    static {
        for (POSFieldsEnum posEnum : POSFieldsEnum.values())
        	ocAttrMap.put(posEnum.getOcAttr(), posEnum);
        	//ocTodbAttrMap.put(posEnum.getOcAttr(), )
    }

    private POSFieldsEnum(String ocAttr) {
        this.ocAttr = ocAttr;
    }
	
    public static POSFieldsEnum get(String ocattr) { 
        return ocAttrMap.get(ocattr); 
    }*/
	
	
	private long fieldId;
	private String pojoField;
	private String posAttr;
	private String ocAttr;
	private String dbColumn;
	private String drKeyField;
	private String phStrField;
	private String dataType;
	
	

	private POSFieldsEnum(long fieldId, String pojoField, String posAttr,
			String ocAttr, String dbColumn, String drKeyField, String phStrField ,String dataType) {
		this.fieldId = fieldId;
		this.pojoField = pojoField;
		this.posAttr = posAttr;
		this.ocAttr = ocAttr;
		this.dbColumn = dbColumn;
		this.drKeyField = drKeyField;
		this.phStrField = phStrField;
		this.dataType = dataType;
	}
	
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	public String getPojoField() {
		return pojoField;
	}
	public void setPojoField(String pojoField) {
		this.pojoField = pojoField;
	}
	public String getPosAttr() {
		return posAttr;
	}
	public void setPosAttr(String posAttr) {
		this.posAttr = posAttr;
	}
	
	
	public String getOcAttr() {
		return ocAttr;
	}

	public void setOcAttr(String ocAttr) {
		this.ocAttr = ocAttr;
	}

	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public String getDbColumn() {
		return dbColumn;
	}

	public void setDbColumn(String dbColumn) {
		this.dbColumn = dbColumn;
	}
	
	
	public String getDrKeyField() {
		return drKeyField;
	}

	public void setDrKeyField(String drKeyField) {
		this.drKeyField = drKeyField;
	}
	
	

	public String getPhStrField() {
		return phStrField;
	}

	public void setPhStrField(String phStrField) {
		this.phStrField = phStrField;
	}

	public static POSFieldsEnum findByOCAttribute(String ocStr) {
		POSFieldsEnum sfEnums[] = values();
		for (POSFieldsEnum eachEnum : sfEnums) {
			if(eachEnum.getOcAttr().equals(ocStr)) return eachEnum;
		}
		return null;
	}

	public static POSFieldsEnum findByPOSAttribute(String posStr) {
		POSFieldsEnum sfEnums[] = values();
		for (POSFieldsEnum eachEnum : sfEnums) {
			if(eachEnum.getOcAttr().equals(posStr)) return eachEnum;
		}
		return null;
	}
	public static POSFieldsEnum findByPhAttribute(String phStr) {
		POSFieldsEnum sfEnums[] = values();
		for (POSFieldsEnum eachEnum : sfEnums) {
			if(eachEnum.getPhStrField().equals(phStr)) return eachEnum;
		}
		return null;
	}


}
