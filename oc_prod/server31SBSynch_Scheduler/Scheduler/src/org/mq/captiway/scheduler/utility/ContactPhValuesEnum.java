package org.mq.captiway.scheduler.utility;

import java.util.List;

public enum ContactPhValuesEnum {

	
	firstName(1,"firstName", "FirstName"),
	lastName(2, "lastName" , "LastName" ),
	addressOne(3, "addressOne", "AddressOne"),
	addressTwo(4, "addressTwo", "AddressTwo" ),
	city(5,"city", "City" ),
	state(6,"state", "State" ),
	country(7,"country", "Country" ),
	zip(8,"pin", "Pin" ),
	gender(9,"gender","Gender" ),
	emailId(10,"email", "Email"),
	email(10,"emailId", "Email"),
	
	//addressUnitId(12, "AddressUnitId", "AddressUnitID" , "Addressunit ID", "String"),
	mobilePhone(13,"phone","Phone"),
	birthDay(14,"birthday", "Birthday"),
	anniversary(15,"anniversary", "Anniversary"),
	createdDate(36,"createdDate", "CreatedDate"),
	storeName(16,"storeName", "StoreName"),
	storeManager(17,"storeManager", "StoreManager"),
	storePhone(18,"storePhone", "StorePhone"),
	storeEmail(19,"storeEmail", "StoreEmail"),
	storeStreet(20,"storeStreet", "StoreStreet"),
	storeCity(21,"storeCity", "StoreCity"),
	storeState(22,"storeState", "StoreState"),
	
	storeZip(23,"storeZip", "StoreZip"),
	
	 lastPurchaseStoreAddress(24, "lastPurchaseStoreAddress" , "LastPurchaseStoreAddress"),
	 lastPurchaseDate(25, "lastPurchaseDate","LastPurchaseDate"),
	 loyaltyPointsBalance(26, "loyaltyPointsBalance", "LoyaltyPointsBalance"),
	 loyaltygiftcardBalance(27,"loyaltygiftcardBalance", "LoyaltygiftcardBalance"),
	 loyaltyCardNumber(28, "loyaltyCardNumber", "LoyaltyCardNumber"),
	 loyaltyCardPin(29, "loyaltyCardPin", "LoyaltyCardPin"),
	 loyaltyRefreshedOn(30, "loyaltyRefreshedOn" , "LoyaltyRefreshedOn"),
	 unsubscribeLink(31, "unsubscribeLink", "UnsubscribeLink"),
	 webPageVersionLink(32, "webPageVersionLink" ,"WebPageVersionLink"),
	 ContactHomeStore(33, "ContactHomeStore" ,"ContactHomeStore" ),
	 ContactLastPurchasedStore(34, "ContactLastPurchasedStore", "ContactLastPurchasedStore"),
	 forwardToFriendLink(35, "forwardToFriendLink", "forwardToFriendLink"),
	
	//pojo reflection Field, pos attribute(CSV), OC attribute, DBColumn, DR key field, phAttribute ,Datatype
	udf1(17, "UDF1", "Udf1"),
	udf2(18, "UDF2", "Udf2"),
	udf3(19, "UDF3", "Udf3"),
	udf4(20,  "UDF4", "Udf4"),
	udf5(21,  "UDF5", "Udf5"),
	udf6(22,  "UDF6", "Udf6"),
	udf7(23, "UDF7", "Udf7"),
	udf8(24,  "UDF8", "Udf8"),
	udf9(25, "UDF9", "Udf9"),
	udf10(26, "UDF10", "Udf10"),
	udf11(27,  "UDF11", "Udf11"),
	udf12(28, "UDF12", "Udf12"),
	udf13(29, "UDF13", "Udf13"),
	udf14(30,  "UDF14", "Udf14"),
	udf15(31,  "UDF15", "Udf15");
	
	
	/*
	 * private String email;
	private String firstName;
	private String lastName;
	private String addressOne;
	private String addressTwo;;
	private String city;
	private String state;
	private String country;
	private String pin;
	private String phone;
	private String gender;
	private String birthday;
	private String anniversary;
	private String storeName;
	private String storeManager;
	private String storePhone;
	private String storeEmail;
	private String storeStreet;
	private String storeCity;
	private String storeState;
	private String storeZip;
	private String lastPurchaseStoreAddress;
	private String lastPurchaseDate;
	private String loyaltyPointsBalance;
	private String loyaltygiftcardBalance;
	private String loyaltyCardNumber;
	private String loyaltyCardPin;
	private String loyaltyRefreshedOn;
	private String unsubscribeLink;
	private String webPageVersionLink;
	private String ContactHomeStore;
	private String ContactLastPurchasedStore;
	
	//UDF placeholders
	private String udf1;
	private String udf2;
	private String udf3;
	private String udf4;
	private String udf5;
	private String udf6;
	private String udf7;
	private String udf8;
	private String udf9;
	private String udf10;
	private String udf11;
	private String udf12;
	private String udf13;
	private String udf14;
	private String udf15;
	
	 */
	
	
	
	
	private int serealCode;
	public int getSerealCode() {
		return serealCode;
	}


	public void setSerealCode(int serealCode) {
		this.serealCode = serealCode;
	}


	public String getField() {
		return field;
	}


	public void setField(String field) {
		this.field = field;
	}


	public String getGetterMethod() {
		return getterMethod;
	}


	public void setGetterMethod(String getterMethod) {
		this.getterMethod = getterMethod;
	}


	private String field;
	private String getterMethod;
	
	
	private ContactPhValuesEnum(int serealCode, String filed, String getterMethod)  {
		this.serealCode = serealCode;
		this.field = filed;
		this.getterMethod = getterMethod;
	}
	
	
	public static ContactPhValuesEnum getEnumByPlaceHOlder(String placeHolder) {
		
		ContactPhValuesEnum[] list = ContactPhValuesEnum.values();
		for (ContactPhValuesEnum contactPhValuesEnum : list) {
			
			if(! contactPhValuesEnum.getField().equalsIgnoreCase(placeHolder)) continue;
			
			return contactPhValuesEnum;
			
			
		}
		
		
		return null;
		
	}
	
	
}
