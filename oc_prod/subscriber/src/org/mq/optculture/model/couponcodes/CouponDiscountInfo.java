package org.mq.optculture.model.couponcodes;

import java.util.List;

import com.google.gson.Gson;
public class CouponDiscountInfo {

	private String COUPONNAME;
	private String COUPONCODE;
	private String COUPONTYPE;
	private String VALIDFROM;
	private String VALIDTO;
	private String DISCOUNTCRITERIA;
	private String EXCLUDEDISCOUNTEDITEMS;
	private String ACCUMULATEDISCOUNT;
	
	private String ELIGIBILITY;
	
	private String LOYALTYPOINTS;
	private List<DiscountInfo> DISCOUNTINFO;
	
	private String LOYALTYVALUECODE;
	private String DESCRIPTION;
	private String APPLYATTRIBUTES;
	
	private String NUDGEPROMOCODE;
	private String NUDGEDESCRIPTION;
	private String APPLIEDCOUPONS;
	private String APPLYDEFAULT;
	private String OTPENABLED;
	public String getOTPENABLED() {
		return OTPENABLED;
	}
	public void setOTPENABLED(String oTPENABLED) {
		OTPENABLED = oTPENABLED;
	}
	public String getAPPLYDEFAULT() {
		return APPLYDEFAULT;
	}
	public void setAPPLYDEFAULT(String aPPLYDEFAULT) {
		APPLYDEFAULT = aPPLYDEFAULT;
	}
	private String SHIPPINGFEE;
	public String getSHIPPINGFEE() {
		return SHIPPINGFEE;
	}
	public void setSHIPPINGFEE(String sHIPPINGFEE) {
		SHIPPINGFEE = sHIPPINGFEE;
	}
	public String getSHIPPINGFEETYPE() {
		return SHIPPINGFEETYPE;
	}
	public void setSHIPPINGFEETYPE(String sHIPPINGFEETYPE) {
		SHIPPINGFEETYPE = sHIPPINGFEETYPE;
	}
	private String SHIPPINGFEETYPE;
	
		
	public String getDESCRIPTION() {
		return DESCRIPTION;
	}
	public void setDESCRIPTION(String dESCRIPTION) {
		DESCRIPTION = dESCRIPTION;
	}
	public CouponDiscountInfo() {
	}
	public CouponDiscountInfo(String COUPONNAME, String COUPONCODE,
			String COUPONTYPE, String ACCUMULATEDISCOUNT, String VALIDFROM, String VALIDTO,
			String DISCOUNTCRITERIA, String LOYALTYPOINTS,
			List<DiscountInfo> DISCOUNTINFO) {
		this.COUPONNAME = COUPONNAME;
		this.COUPONCODE = COUPONCODE;
		this.COUPONTYPE = COUPONTYPE;
		this.VALIDFROM = VALIDFROM;
		this.VALIDTO = VALIDTO;
		this.DISCOUNTCRITERIA = DISCOUNTCRITERIA;
		this.LOYALTYPOINTS = LOYALTYPOINTS;
		this.DISCOUNTINFO = DISCOUNTINFO;
		this.ACCUMULATEDISCOUNT = ACCUMULATEDISCOUNT;
	}
	
	public String getLOYALTYVALUECODE() {
		return LOYALTYVALUECODE;
	}
	public void setLOYALTYVALUECODE(String lOYALTYVALUECODE) {
		LOYALTYVALUECODE = lOYALTYVALUECODE;
	}
	
	public String getCOUPONNAME() {
		return COUPONNAME;
	}
	public void setCOUPONNAME(String cOUPONNAME) {
		COUPONNAME = cOUPONNAME;
	}
	public String getCOUPONCODE() {
		return COUPONCODE;
	}
	public void setCOUPONCODE(String cOUPONCODE) {
		COUPONCODE = cOUPONCODE;
	}
	public String getCOUPONTYPE() {
		return COUPONTYPE;
	}
	public void setCOUPONTYPE(String cOUPONTYPE) {
		COUPONTYPE = cOUPONTYPE;
	}
	public String getVALIDFROM() {
		return VALIDFROM;
	}
	public void setVALIDFROM(String vALIDFROM) {
		VALIDFROM = vALIDFROM;
	}
	public String getVALIDTO() {
		return VALIDTO;
	}
	public void setVALIDTO(String vALIDTO) {
		VALIDTO = vALIDTO;
	}
	public String getDISCOUNTCRITERIA() {
		return DISCOUNTCRITERIA;
	}
	public void setDISCOUNTCRITERIA(String dISCOUNTCRITERIA) {
		DISCOUNTCRITERIA = dISCOUNTCRITERIA;
	}
	public String getLOYALTYPOINTS() {
		return LOYALTYPOINTS;
	}
	public void setLOYALTYPOINTS(String lOYALTYPOINTS) {
		LOYALTYPOINTS = lOYALTYPOINTS;
	}
	public List<DiscountInfo> getDISCOUNTINFO() {
		return DISCOUNTINFO;
	}
	public void setDISCOUNTINFO(List<DiscountInfo> dISCOUNTINFO) {
		DISCOUNTINFO = dISCOUNTINFO;
	}
	
	public String getEXCLUDEDISCOUNTEDITEMS() {
		return EXCLUDEDISCOUNTEDITEMS;
	}
	public void setEXCLUDEDISCOUNTEDITEMS(String eXCLUDEDISCOUNTEDITEMS) {
		EXCLUDEDISCOUNTEDITEMS = eXCLUDEDISCOUNTEDITEMS;
	}
	
	public String getACCUMULATEDISCOUNT() {
		return ACCUMULATEDISCOUNT;
	}
	public void setACCUMULATEDISCOUNT(String aCCUMULATEDISCOUNT) {
		ACCUMULATEDISCOUNT = aCCUMULATEDISCOUNT;
	}
	public String getELIGIBILITY() {
		return ELIGIBILITY;
	}
	public void setELIGIBILITY(String iTEMELIGIBILITY) {
		ELIGIBILITY = iTEMELIGIBILITY;
	}
	
	
	public static void main(String[] args) {
		
		CouponDiscountInfo couponDiscountInfo = new CouponDiscountInfo();
		couponDiscountInfo.setCOUPONCODE("");
		couponDiscountInfo.setCOUPONNAME("");
		couponDiscountInfo.setCOUPONTYPE("");
		couponDiscountInfo.setVALIDFROM("");
		couponDiscountInfo.setVALIDTO("");
		couponDiscountInfo.setDISCOUNTCRITERIA("");
		couponDiscountInfo.setEXCLUDEDISCOUNTEDITEMS("");
		couponDiscountInfo.setACCUMULATEDISCOUNT("");
		couponDiscountInfo.setELIGIBILITY("");
		couponDiscountInfo.setLOYALTYPOINTS("");
		
		Gson gson = new Gson();
		System.out.println(gson.toJson(couponDiscountInfo));
	}
	public String getAPPLYATTRIBUTES() {
		return APPLYATTRIBUTES;
	}
	public void setAPPLYATTRIBUTES(String aPPLYATTRIBUTE) {
		APPLYATTRIBUTES = aPPLYATTRIBUTE;
	}
	public String getNUDGEPROMOCODE() {
		return NUDGEPROMOCODE;
	}
	public void setNUDGEPROMOCODE(String nUDGEPROMOCODE) {
		NUDGEPROMOCODE = nUDGEPROMOCODE;
	}
	public String getNUDGEDESCRIPTION() {
		return NUDGEDESCRIPTION;
	}
	public void setNUDGEDESCRIPTION(String nUDGEDESCRIPTION) {
		NUDGEDESCRIPTION = nUDGEDESCRIPTION;
	}
	public String getAPPLIEDCOUPONS() {
		return APPLIEDCOUPONS;
	}
	public void setAPPLIEDCOUPONS(String aPPLIEDCOUPONS) {
		APPLIEDCOUPONS = aPPLIEDCOUPONS;
	}
}
