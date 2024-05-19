package org.mq.marketer.campaign.beans;

public class POSMapping implements java.io.Serializable{
	
	private Long posId;
	private String customFieldName;
	private String displayLabel;
	private String defaultPhValue;
	private String defaultPhValueSet;
	private String dataType;
	private String drDataType;
	private String mappingType;
	private String posAttribute;
	private String digitalReceiptAttribute;
	private String optionalValues;
	private Integer uniquePriority;
	private Integer uniqueInAcrossFiles;
	
	private Long userId;
	
	public POSMapping() {}
	
	
	public POSMapping(String customFieldName, String displayLabel,String defaultPhValue, String dataType, String mappingType,String posAttribute,Long  userId) {
		this.customFieldName = customFieldName;
		this.displayLabel = displayLabel;
		this.defaultPhValue=defaultPhValue;
		this.dataType = dataType;
		this.mappingType = mappingType;
		this.posAttribute = posAttribute;
		this.userId = userId;
	}


	public String getOptionalValues() {
		return optionalValues;
	}


	public void setOptionalValues(String optionalValues) {
		this.optionalValues = optionalValues;
	}


	public Integer getUniquePriority() {
		return uniquePriority;
	}


	public void setUniquePriority(Integer uniquePriority) {
		this.uniquePriority = uniquePriority;
	}


	
	
	public Integer getUniqueInAcrossFiles() {
		return uniqueInAcrossFiles;
	}


	public void setUniqueInAcrossFiles(Integer uniqueInAcrossFiles) {
		this.uniqueInAcrossFiles = uniqueInAcrossFiles;
	}


	public Long getPosId() {
		return posId;
	}


	public void setPosId(Long posId) {
		this.posId = posId;
	}


	public String getCustomFieldName() {
		return customFieldName;
	}


	public void setCustomFieldName(String customFieldName) {
		this.customFieldName = customFieldName;
	}


	public String getDisplayLabel() {
		return displayLabel;
	}


	public void setDisplayLabel(String displayLabel) {
		this.displayLabel = displayLabel;
	}
	
	

	public void setDefaultPhValue(String defaultPhValue) {
		this.defaultPhValue = defaultPhValue;
	}
	
	public String getDefaultPhValue() {
		return defaultPhValue;
	}


	public String getDefaultPhValueSet() {
		return defaultPhValueSet;
	}


	public void setDefaultPhValueSet(String defaultPhValueSet) {
		this.defaultPhValueSet = defaultPhValueSet;
	}


	public String getDataType() {
		return dataType;
	}


	public void setDataType(String dataType) {
		this.dataType = dataType;
	}


	public String getMappingType() {
		return mappingType;
	}


	public void setMappingType(String mappingType) {
		this.mappingType = mappingType;
	}


	public String getPosAttribute() {
		return posAttribute;
	}


	public void setPosAttribute(String posAttribute) {
		this.posAttribute = posAttribute;
	}


	public Long getUserId() {
		return userId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public String getDigitalReceiptAttribute() {
		return digitalReceiptAttribute;
	}


	public void setDigitalReceiptAttribute(String digitalReceiptAttribute) {
		this.digitalReceiptAttribute = digitalReceiptAttribute;
	}


	public String getDrDataType() {
		return drDataType;
	}


	public void setDrDataType(String drDataType) {
		this.drDataType = drDataType;
	}

	
	
}
