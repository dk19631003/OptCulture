package org.mq.marketer.campaign.beans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import java.util.Calendar;

public class SkuFile implements java.io.Serializable {

	private Long skuId;
	private Long listId;
	private String storeNumber;
	private String sku;
	private String description;
	private Double listPrice;
	private String itemCategory;
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
	
	private Long userId;
	private String itemSid;
	private String vendorCode;
	private String departmentCode;
	private String classCode;
	private String subClassCode;
	private String DCS;
	private String subsidiaryNumber;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private Calendar CreatedDate;
	private Calendar ModifiedDate;

	
	public Calendar getCreatedDate() {
		return CreatedDate;
	}

	public void setCreatedDate(Calendar createdDate) {
		CreatedDate = createdDate;
	}

	public Calendar getModifiedDate() {
		return ModifiedDate;
	}

	public void setModifiedDate(Calendar modifiedDate) {
		ModifiedDate = modifiedDate;
	}



	public String getSubsidiaryNumber() {
		return subsidiaryNumber;
	}

	public void setSubsidiaryNumber(String subsidiaryNumber) {
		this.subsidiaryNumber = subsidiaryNumber;
	}

	
	
	
	//private Set<SKUCustFiledData> skuUdfSet;
	
	public SkuFile() {}
	
	public SkuFile(String storeNumber, String sku, String description, Double listPrice, String itemCategory,Long listId,
			String udf1, String udf2, String udf3, String udf4, String udf5, String udf6, String udf7, String udf8, 
			String udf9 , String udf10, String udf11, String udf12,	String udf13, String udf14, String udf15)  {
		this.storeNumber = storeNumber;
		this.sku = sku;
		this.description = description;
		this.listPrice = listPrice;
		this.itemCategory = itemCategory;
		this.listId = listId;
		this.udf1 = udf1;
		this.udf2 = udf2;
		this.udf3 = udf3;
		this.udf4 = udf4;
		this.udf5 = udf5;
		this.udf6 = udf6;
		this.udf7 = udf7;
		this.udf8 =udf8;
		this.udf9 = udf9;
		this.udf10 = udf10;
		this.udf11=udf11;
		this.udf12=udf12;
		this.udf13=udf13;
		this.udf14=udf14;
		this.udf15=udf15;
		
	}

	public Long getSkuId() {
		return skuId;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}

	public Long getListId() {
		return listId;
	}

	public void setListId(Long listId) {
		this.listId = listId;
	}

	public String getStoreNumber() {
		return storeNumber;
	}

	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getListPrice() {
		return listPrice;
	}

	public void setListPrice(Double listPrice) {
		this.listPrice = listPrice;
	}

	public String getItemCategory() {
		return itemCategory;
	}

	public void setItemCategory(String itemCategory) {
		this.itemCategory = itemCategory;
	}

	
	public String getUdf1() {
		return udf1;
	}

	public void setUdf1(String udf1) {
		this.udf1 = udf1;
	}

	public String getUdf2() {
		return udf2;
	}

	public void setUdf2(String udf2) {
		this.udf2 = udf2;
	}

	public String getUdf3() {
		return udf3;
	}

	public void setUdf3(String udf3) {
		this.udf3 = udf3;
	}

	public String getUdf4() {
		return udf4;
	}

	public void setUdf4(String udf4) {
		this.udf4 = udf4;
	}

	public String getUdf5() {
		return udf5;
	}

	public void setUdf5(String udf5) {
		this.udf5 = udf5;
	}

	public String getUdf6() {
		return udf6;
	}

	public void setUdf6(String udf6) {
		this.udf6 = udf6;
	}

	public String getUdf7() {
		return udf7;
	}

	public void setUdf7(String udf7) {
		this.udf7 = udf7;
	}

	public String getUdf8() {
		return udf8;
	}

	public void setUdf8(String udf8) {
		this.udf8 = udf8;
	}

	public String getUdf9() {
		return udf9;
	}

	public void setUdf9(String udf9) {
		this.udf9 = udf9;
	}

	public String getUdf10() {
		return udf10;
	}

	public void setUdf10(String udf10) {
		this.udf10 = udf10;
	}

	public String getUdf11() {
		return udf11;
	}

	public void setUdf11(String udf11) {
		this.udf11 = udf11;
	}

	public String getUdf12() {
		return udf12;
	}

	public void setUdf12(String udf12) {
		this.udf12 = udf12;
	}

	public String getUdf13() {
		return udf13;
	}

	public void setUdf13(String udf13) {
		this.udf13 = udf13;
	}

	public String getUdf14() {
		return udf14;
	}

	public void setUdf14(String udf14) {
		this.udf14 = udf14;
	}

	public String getUdf15() {
		return udf15;
	}

	public void setUdf15(String udf15) {
		this.udf15 = udf15;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getItemSid() {
		return itemSid;
	}

	public void setItemSid(String itemSid) {
		this.itemSid = itemSid;
	}

	public String getVendorCode() {
		return vendorCode;
	}

	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		if(departmentCode!=null && departmentCode.length()>255 ) {
			logger.info("DEPARTMENT_CODE :"+departmentCode);
			departmentCode = departmentCode.substring(0, 255);
			logger.info("DEPARTMENT_CODE IS GREATER THAN 255 char hence trimmed upto 255 chars :"+departmentCode);
		}
		this.departmentCode = departmentCode;
	}

	public String getClassCode() {
		return classCode;
	}

	public void setClassCode(String classCode) {
		if(classCode!=null && classCode.length()>255 ) {
			logger.info("CLASS_CODE :"+classCode);
			classCode = classCode.substring(0, 255);
			logger.info("CLASS_CODE IS GREATER THAN 255 char hence trimmed upto 255 chars :"+classCode);
		}
		this.classCode = classCode;
	}

	public String getSubClassCode() {
		return subClassCode;
	}

	public void setSubClassCode(String subClassCode) {
		if(subClassCode!= null && subClassCode.length()>255 ) {
			logger.info("SUBCLASS_CODE :"+subClassCode);
			subClassCode = subClassCode.substring(0, 255);
			logger.info("SUBCLASS_CODE IS GREATER THAN 255 char hence trimmed upto 255 chars :"+subClassCode);
		}
	    this.subClassCode = subClassCode;
	}

	public String getDCS() {
		return DCS;
	}

	public void setDCS(String dCS) {
		DCS = dCS;
	}

	/*public Set<SKUCustFiledData> getSkuUdfSet() {
		return skuUdfSet;
	}

	public void setSkuUdfSet(Set<SKUCustFiledData> skuUdfSet) {
		this.skuUdfSet = skuUdfSet;
	}*/
	
	
	
}
