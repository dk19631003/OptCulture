package org.mq.optculture.model.updatesku;

import javax.xml.bind.annotation.XmlElement;

public class SkuInfo {
	private String ITEMSID;
	private String ITEMCATEGORY;
	private String STORENUMBER;
	private String SKU;
	private String DESCRIPTION;
	private String LISTPRICE;
	private String DCS;
	private String CLASSCODE;
	private String SUBCLASSCODE;
	private String DEPARTMENTCODE;
	private String VENDORCODE;
	private String UDF1;
	private String UDF2;
	private String UDF3;
	private String UDF4;
	private String UDF5;
	private String UDF6;
	private String UDF7;
	private String UDF8;
	private String UDF9;
	private String UDF10;
	private String UDF11;
	private String UDF12;
	private String UDF13;
	private String UDF14;
	private String UDF15;
	public SkuInfo() {
	}
	public SkuInfo(String iTEMSID, String sTORENUMBER, String sKU,
			String dESCRIPTION, String lISTPRICE, String dCS, String cLASSCODE,
			String sUBCLASSCODE, String dEPARTMENTCODE, String vENDORCODE,
			String uDF1, String uDF2, String uDF3, String uDF4, String uDF5,
			String uDF6, String uDF7, String uDF8, String uDF9, String uDF10,
			String uDF11, String uDF12, String uDF13, String uDF14,
			String uDF15, String iTEMCATEGORY) {
		ITEMSID = iTEMSID;
		STORENUMBER = sTORENUMBER;
		SKU = sKU;
		DESCRIPTION = dESCRIPTION;
		LISTPRICE = lISTPRICE;
		DCS = dCS;
		CLASSCODE = cLASSCODE;
		SUBCLASSCODE = sUBCLASSCODE;
		DEPARTMENTCODE = dEPARTMENTCODE;
		VENDORCODE = vENDORCODE;
		UDF1 = uDF1;
		UDF2 = uDF2;
		UDF3 = uDF3;
		UDF4 = uDF4;
		UDF5 = uDF5;
		UDF6 = uDF6;
		UDF7 = uDF7;
		UDF8 = uDF8;
		UDF9 = uDF9;
		UDF10 = uDF10;
		UDF11 = uDF11;
		UDF12 = uDF12;
		UDF13 = uDF13;
		UDF14 = uDF14;
		UDF15 = uDF15;
		ITEMCATEGORY = iTEMCATEGORY;
	}

	public String getITEMSID() {
		return ITEMSID;
	}
	@XmlElement(name="ITEMSID")
	public void setITEM_SID(String iTEMSID) {
		ITEMSID = iTEMSID;
	}
	public String getSTORENUMBER() {
		return STORENUMBER;
	}
	@XmlElement(name="STORENUMBER")
	public void setSTORENUMBER(String sTORENUMBER) {
		STORENUMBER = sTORENUMBER;
	}
	public String getSKU() {
		return SKU;
	}
	@XmlElement(name="SKU")
	public void setSKU(String sKU) {
		SKU = sKU;
	}
	public String getDESCRIPTION() {
		return DESCRIPTION;
	}
	@XmlElement(name="DESCRIPTION")
	public void setDESCRIPTION(String dESCRIPTION) {
		DESCRIPTION = dESCRIPTION;
	}
	public String getLISTPRICE() {
		return LISTPRICE;
	}
	@XmlElement(name="LISTPRICE")
	public void setLISTPRICE(String lISTPRICE) {
		LISTPRICE = lISTPRICE;
	}
	public String getDCS() {
		return DCS;
	}
	@XmlElement(name="DCS")
	public void setDCS(String dCS) {
		DCS = dCS;
	}
	
	public String getDEPARTMENTCODE() {
		return DEPARTMENTCODE;
	}
	@XmlElement(name="DEPARTMENTCODE")
	public void setDEPARTMENTCODE(String dEPARTMENTCODE) {
		DEPARTMENTCODE = dEPARTMENTCODE;
	}
	
	public String getUDF1() {
		return UDF1;
	}
	@XmlElement(name="UDF1")
	public void setUDF1(String uDF1) {
		UDF1 = uDF1;
	}
	public String getUDF2() {
		return UDF2;
	}
	@XmlElement(name="UDF2")
	public void setUDF2(String uDF2) {
		UDF2 = uDF2;
	}
	public String getUDF3() {
		return UDF3;
	}
	@XmlElement(name="UDF3")
	public void setUDF3(String uDF3) {
		UDF3 = uDF3;
	}
	public String getUDF4() {
		return UDF4;
	}
	@XmlElement(name="UDF4")
	public void setUDF4(String uDF4) {
		UDF4 = uDF4;
	}
	public String getUDF5() {
		return UDF5;
	}
	@XmlElement(name="UDF5")
	public void setUDF5(String uDF5) {
		UDF5 = uDF5;
	}
	public String getUDF6() {
		return UDF6;
	}
	@XmlElement(name="UDF6")
	public void setUDF6(String uDF6) {
		UDF6 = uDF6;
	}
	public String getUDF7() {
		return UDF7;
	}
	@XmlElement(name="UDF7")
	public void setUDF7(String uDF7) {
		UDF7 = uDF7;
	}
	public String getUDF8() {
		return UDF8;
	}
	@XmlElement(name="UDF8")
	public void setUDF8(String uDF8) {
		UDF8 = uDF8;
	}
	public String getUDF9() {
		return UDF9;
	}
	@XmlElement(name="UDF9")
	public void setUDF9(String uDF9) {
		UDF9 = uDF9;
	}
	public String getUDF10() {
		return UDF10;
	}
	@XmlElement(name="UDF10")
	public void setUDF10(String uDF10) {
		UDF10 = uDF10;
	}
	public String getUDF11() {
		return UDF11;
	}
	@XmlElement(name="UDF11")
	public void setUDF11(String uDF11) {
		UDF11 = uDF11;
	}
	public String getUDF12() {
		return UDF12;
	}
	@XmlElement(name="UDF12")
	public void setUDF12(String uDF12) {
		UDF12 = uDF12;
	}
	public String getUDF13() {
		return UDF13;
	}
	@XmlElement(name="UDF13")
	public void setUDF13(String uDF13) {
		UDF13 = uDF13;
	}
	public String getUDF14() {
		return UDF14;
	}
	@XmlElement(name="UDF14")
	public void setUDF14(String uDF14) {
		UDF14 = uDF14;
	}
	public String getUDF15() {
		return UDF15;
	}
	@XmlElement(name="UDF15")
	public void setUDF15(String uDF15) {
		UDF15 = uDF15;
	}
	public String getITEMCATEGORY() {
		return ITEMCATEGORY;
	}
	@XmlElement(name="ITEMCATEGORY")
	public void setITEMCATEGORY(String iTEMCATEGORY) {
		ITEMCATEGORY = iTEMCATEGORY;
	}
	public String getCLASSCODE() {
		return CLASSCODE;
	}
	@XmlElement(name="CLASSCODE")
	public void setCLASSCODE(String cLASSCODE) {
		CLASSCODE = cLASSCODE;
	}
	public String getSUBCLASSCODE() {
		return SUBCLASSCODE;
	}
	@XmlElement(name="SUBCLASSCODE")
	public void setSUBCLASSCODE(String sUBCLASSCODE) {
		SUBCLASSCODE = sUBCLASSCODE;
	}
	public String getVENDORCODE() {
		return VENDORCODE;
	}
	@XmlElement(name="VENDORCODE")
	public void setVENDORCODE(String vENDORCODE) {
		VENDORCODE = vENDORCODE;
	}
}
