package org.mq.optculture.model.DR;

import javax.xml.bind.annotation.XmlElement;

public class DRItem {


	private String DCS;
	private String VendorCode;
	private String VendorName;
	
	private String ItemCategory;
	private String DepartmentCode;
	private String DepartmentName;
	private String ItemClass;
	private String ItemClassName;
	private String ItemSubClass;
	private String ItemSubClassName;

	private String Desc1;
	private String Desc2;
	private String Attr;
	private String Size;
	private String UPC;
	private String ALU;
	private String UDF0;
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
	
	private String BarCode;
	
	private String TaxCd;
	private String TaxPrc;
	private String Tax;
	private String Qty;
	private String InvcItemPrc;
	private String DocItemDisc;
	private String DCSName;
	private String ExtPrc;
	private String DocItemDiscLvl;
	private String DocItemOrigPrc;
	private String DocItemPrc;
	private String DocItemOrigTax;
	private String Clerk;
	private String ItemLookup;
	private String PrcLvl;
	private String ItemSID;
	private String DocItemDiscAmt;
	private String FC1Prc;
	private String StyleSID;
	private String Desc3;
	private String Desc4;
	private String InvnAUX0;
	private String InvnAUX1;
	private String InvnAUX2;
	private String InvnAUX3;
	private String InvnAUX4;
	private String InvnAUX5;
	private String InvnAUX6;
	private String InvnAUX7;
	private String InvnUDFStr;
	private String InvnUDFDate;
	private String DocItemRndDisc;
	private String ExtOrigPrc;
	private String TotalTax;
	private String Tax2Prc;
	private String Tax2;
	private String Tax2Cd;
	private String Tax1Cd;
	private String TaxArea2;
	private String SeqNum;
	private String ExtTax;
	private String ExtTax2;
	private String ExtTotalTax;
	private String DiscountPercentWithTax;
	private String DiscountAmountWithTax;
	private String FCOriginalPrice;
	private String FCOriginalPriceWithTax;
	private String FCOriginalExtendedPrice;
	private String FCOriginalExtendedPriceWithTax;
	private String FCPrice;
	private String FCPriceWithTax;
	private String FCExtendedPrice;
	private String FCExtendedPriceWithTax;
	private String SerialNum;
	private String ItemNote1;
	private String ItemNote2;
	private String ItemNote3;
	private String ItemNote4;
	private String ItemNote5;
	private String ItemNote6;
	private String ItemNote7;
	private String ItemNote8;
	private String ItemNote9;
	private String ItemNote10;
	private String ItemLine;
	private String NetPrice;
	private String ReturnReason;
	private String VoidReason;
	private String DiscountReason;
	private String TaxAmount;
	
	private String RefStoreCode;
	private String RefSubsidiaryNumber;
	private String RefReceipt;
	private String RefDocSID;
	
	
	
	public DRItem(String dCS, String vendorCode, String vendorName, String itemCategory, String departmentCode,
			String departmentName, String itemClass, String itemClassName, String itemSubClass, String itemSubClassName,
			String desc1, String desc2, String attr, String size, String uPC, String aLU, String uDF0, String uDF1,
			String uDF2, String uDF3, String taxCd, String taxPrc, String tax, String qty, String invcItemPrc,
			String docItemDisc, String dCSName, String extPrc, String docItemDiscLvl, String docItemOrigPrc,
			String docItemPrc, String docItemOrigTax, String clerk, String itemLookup, String prcLvl, String itemSID,
			String docItemDiscAmt, String fC1Prc, String styleSID, String desc3, String desc4, String invnAUX0,
			String invnAUX1, String invnAUX2, String invnAUX3, String invnAUX4, String invnAUX5, String invnAUX6,
			String invnAUX7, String invnUDFStr, String invnUDFDate, String docItemRndDisc, String extOrigPrc,
			String totalTax, String tax2Prc, String tax2, String tax2Cd, String tax1Cd, String taxArea2, String seqNum,
			String extTax, String extTax2, String extTotalTax, String discountPercentWithTax,
			String discountAmountWithTax, String fCOriginalPrice, String fCOriginalPriceWithTax,
			String fCOriginalExtendedPrice, String fCOriginalExtendedPriceWithTax, String fCPrice,
			String fCPriceWithTax, String fCExtendedPrice, String fCExtendedPriceWithTax, String serialNum,
			String itemNote1, String itemNote2, String itemNote3,String itemNote4,String itemNote5,
			String itemNote6,String itemNote7,String itemNote8,String itemNote9,String itemNote10,
			String itemLine, String netPrice, String returnReason,
			String voidReason, String discountReason, String taxAmount, String refStoreCode, String refSubsidiaryNumber,
			String refReceipt, String refDocSID) {
		DCS = dCS;
		VendorCode = vendorCode;
		VendorName = vendorName;
		ItemCategory = itemCategory;
		DepartmentCode = departmentCode;
		DepartmentName = departmentName;
		ItemClass = itemClass;
		ItemClassName = itemClassName;
		ItemSubClass = itemSubClass;
		ItemSubClassName = itemSubClassName;
		Desc1 = desc1;
		Desc2 = desc2;
		Attr = attr;
		Size = size;
		UPC = uPC;
		ALU = aLU;
		UDF0 = uDF0;
		UDF1 = uDF1;
		UDF2 = uDF2;
		UDF3 = uDF3;
		TaxCd = taxCd;
		TaxPrc = taxPrc;
		Tax = tax;
		Qty = qty;
		InvcItemPrc = invcItemPrc;
		DocItemDisc = docItemDisc;
		DCSName = dCSName;
		ExtPrc = extPrc;
		DocItemDiscLvl = docItemDiscLvl;
		DocItemOrigPrc = docItemOrigPrc;
		DocItemPrc = docItemPrc;
		DocItemOrigTax = docItemOrigTax;
		Clerk = clerk;
		ItemLookup = itemLookup;
		PrcLvl = prcLvl;
		ItemSID = itemSID;
		DocItemDiscAmt = docItemDiscAmt;
		FC1Prc = fC1Prc;
		StyleSID = styleSID;
		Desc3 = desc3;
		Desc4 = desc4;
		InvnAUX0 = invnAUX0;
		InvnAUX1 = invnAUX1;
		InvnAUX2 = invnAUX2;
		InvnAUX3 = invnAUX3;
		InvnAUX4 = invnAUX4;
		InvnAUX5 = invnAUX5;
		InvnAUX6 = invnAUX6;
		InvnAUX7 = invnAUX7;
		InvnUDFStr = invnUDFStr;
		InvnUDFDate = invnUDFDate;
		DocItemRndDisc = docItemRndDisc;
		ExtOrigPrc = extOrigPrc;
		TotalTax = totalTax;
		Tax2Prc = tax2Prc;
		Tax2 = tax2;
		Tax2Cd = tax2Cd;
		Tax1Cd = tax1Cd;
		TaxArea2 = taxArea2;
		SeqNum = seqNum;
		ExtTax = extTax;
		ExtTax2 = extTax2;
		ExtTotalTax = extTotalTax;
		DiscountPercentWithTax = discountPercentWithTax;
		DiscountAmountWithTax = discountAmountWithTax;
		FCOriginalPrice = fCOriginalPrice;
		FCOriginalPriceWithTax = fCOriginalPriceWithTax;
		FCOriginalExtendedPrice = fCOriginalExtendedPrice;
		FCOriginalExtendedPriceWithTax = fCOriginalExtendedPriceWithTax;
		FCPrice = fCPrice;
		FCPriceWithTax = fCPriceWithTax;
		FCExtendedPrice = fCExtendedPrice;
		FCExtendedPriceWithTax = fCExtendedPriceWithTax;
		SerialNum = serialNum;
		ItemNote1 = itemNote1;
		ItemNote2 = itemNote2;
		ItemNote3 = itemNote3;
		ItemNote3 = itemNote4;
		ItemNote3 = itemNote5;
		ItemNote3 = itemNote6;
		ItemNote3 = itemNote7;
		ItemNote3 = itemNote8;
		ItemNote3 = itemNote9;
		ItemNote3 = itemNote10;
		ItemLine = itemLine;
		NetPrice = netPrice;
		ReturnReason = returnReason;
		VoidReason = voidReason;
		DiscountReason = discountReason;
		TaxAmount = taxAmount;
		RefStoreCode = refStoreCode;
		RefSubsidiaryNumber = refSubsidiaryNumber;
		RefReceipt = refReceipt;
		RefDocSID = refDocSID;
	}
	
	
	public DRItem(){
		
		
	}
	
	public String getDCS() {
		return DCS;
	}
	@XmlElement(name = "DCS")
	public void setDCS(String dCS) {
		this.DCS = dCS;
	}
	public String getVendorCode() {
		return VendorCode;
	}
	@XmlElement(name = "VendorCode")
	public void setVendorCode(String vendorCode) {
		VendorCode = vendorCode;
	}
	
	public String getVendorName() {
		return VendorName;
	}
	@XmlElement(name = "VendorName")
	public void setVendorName(String vendorName) {
		VendorName = vendorName;
	}

	public String getItemCategory() {
		return ItemCategory;
	}
	@XmlElement(name = "ItemCategory")
	public void setItemCategory(String itemCategory) {
		ItemCategory = itemCategory;
	}

	public String getDepartmentCode() {
		return DepartmentCode;
	}
	@XmlElement(name = "DepartmentCode")
	public void setDepartmentCode(String departmentCode) {
		DepartmentCode = departmentCode;
	}

	public String getDepartmentName() {
		return DepartmentName;
	}
	@XmlElement(name = "DepartmentName")
	public void setDepartmentName(String departmentName) {
		DepartmentName = departmentName;
	}

	public String getItemClass() {
		return ItemClass;
	}
	@XmlElement(name = "ItemClass")
	public void setItemClass(String itemClass) {
		ItemClass = itemClass;
	}

	public String getItemClassName() {
		return ItemClassName;
	}
	@XmlElement(name = "ItemClassName")
	public void setItemClassName(String itemClassName) {
		ItemClassName = itemClassName;
	}

	public String getItemSubClass() {
		return ItemSubClass;
	}
	@XmlElement(name = "ItemSubClass")
	public void setItemSubClass(String itemSubClass) {
		ItemSubClass = itemSubClass;
	}

	public String getItemSubClassName() {
		return ItemSubClassName;
	}
	@XmlElement(name = "ItemSubClassName")
	public void setItemSubClassName(String itemSubClassName) {
		ItemSubClassName = itemSubClassName;
	}
	
	public String getDesc1() {
		return Desc1;
	}
	@XmlElement(name = "Desc1")
	public void setDesc1(String desc1) {
		Desc1 = desc1;
	}
	public String getDesc2() {
		return Desc2;
	}
	@XmlElement(name = "Desc2")
	public void setDesc2(String desc2) {
		Desc2 = desc2;
	}
	public String getAttr() {
		return Attr;
	}
	@XmlElement(name = "Attr")
	public void setAttr(String attr) {
		Attr = attr;
	}
	public String getSize() {
		return Size;
	}
	@XmlElement(name = "Size")
	public void setSize(String size) {
		Size = size;
	}
	public String getUPC() {
		return UPC;
	}
	@XmlElement(name = "UPC")
	public void setUPC(String uPC) {
		UPC = uPC;
	}
	public String getALU() {
		return ALU;
	}
	@XmlElement(name = "ALU")
	public void setALU(String aLU) {
		ALU = aLU;
	}
	public String getUDF0() {
		return UDF0;
	}
	@XmlElement(name = "UDF0")
	public void setUDF0(String uDF0) {
		UDF0 = uDF0;
	}
	public String getUDF1() {
		return UDF1;
	}
	@XmlElement(name = "UDF1")
	public void setUDF1(String uDF1) {
		UDF1 = uDF1;
	}
	public String getUDF2() {
		return UDF2;
	}
	@XmlElement(name = "UDF2")
	public void setUDF2(String uDF2) {
		UDF2 = uDF2;
	}
	public String getUDF3() {
		return UDF3;
	}
	@XmlElement(name = "UDF3")
	public void setUDF3(String uDF3) {
		UDF3 = uDF3;
	}
	public String getUDF4() {
		return UDF4;
	}


	public void setUDF4(String uDF4) {
		UDF4 = uDF4;
	}


	public String getUDF5() {
		return UDF5;
	}


	public void setUDF5(String uDF5) {
		UDF5 = uDF5;
	}


	public String getUDF6() {
		return UDF6;
	}


	public void setUDF6(String uDF6) {
		UDF6 = uDF6;
	}


	public String getUDF7() {
		return UDF7;
	}


	public void setUDF7(String uDF7) {
		UDF7 = uDF7;
	}


	public String getUDF8() {
		return UDF8;
	}


	public void setUDF8(String uDF8) {
		UDF8 = uDF8;
	}


	public String getUDF9() {
		return UDF9;
	}


	public void setUDF9(String uDF9) {
		UDF9 = uDF9;
	}


	public String getUDF10() {
		return UDF10;
	}


	public void setUDF10(String uDF10) {
		UDF10 = uDF10;
	}


	public String getUDF11() {
		return UDF11;
	}


	public void setUDF11(String uDF11) {
		UDF11 = uDF11;
	}


	public String getUDF12() {
		return UDF12;
	}


	public void setUDF12(String uDF12) {
		UDF12 = uDF12;
	}


	public String getUDF13() {
		return UDF13;
	}


	public void setUDF13(String uDF13) {
		UDF13 = uDF13;
	}


	public String getUDF14() {
		return UDF14;
	}


	public void setUDF14(String uDF14) {
		UDF14 = uDF14;
	}


	public String getUDF15() {
		return UDF15;
	}


	public void setUDF15(String uDF15) {
		UDF15 = uDF15;
	}


	public String getBarCode() {
		return BarCode;
	}


	public void setBarCode(String barCode) {
		BarCode = barCode;
	}


	public String getTaxCd() {
		return TaxCd;
	}
	@XmlElement(name = "TaxCd")
	public void setTaxCd(String taxCd) {
		TaxCd = taxCd;
	}
	public String getTaxPrc() {
		return TaxPrc;
	}
	@XmlElement(name = "TaxPrc")
	public void setTaxPrc(String taxPrc) {
		TaxPrc = taxPrc;
	}
	public String getTax() {
		return Tax;
	}
	@XmlElement(name = "Tax")
	public void setTax(String tax) {
		Tax = tax;
	}
	public String getQty() {
		return Qty;
	}
	@XmlElement(name = "Qty")
	public void setQty(String qty) {
		Qty = qty;
	}
	public String getInvcItemPrc() {
		return InvcItemPrc;
	}
	@XmlElement(name = "InvcItemPrc")
	public void setInvcItemPrc(String invcItemPrc) {
		InvcItemPrc = invcItemPrc;
	}
	public String getDocItemDisc() {
		return DocItemDisc;
	}
	@XmlElement(name = "DocItemDisc")
	public void setDocItemDisc(String docItemDisc) {
		DocItemDisc = docItemDisc;
	}
	public String getDCSName() {
		return DCSName;
	}
	@XmlElement(name = "DCSName")
	public void setDCSName(String dCSName) {
		DCSName = dCSName;
	}
	public String getExtPrc() {
		return ExtPrc;
	}
	@XmlElement(name = "ExtPrc")
	public void setExtPrc(String extPrc) {
		ExtPrc = extPrc;
	}
	public String getDocItemDiscLvl() {
		return DocItemDiscLvl;
	}
	@XmlElement(name = "DocItemDiscLvl")
	public void setDocItemDiscLvl(String docItemDiscLvl) {
		DocItemDiscLvl = docItemDiscLvl;
	}
	public String getDocItemOrigPrc() {
		return DocItemOrigPrc;
	}
	@XmlElement(name = "DocItemOrigPrc")
	public void setDocItemOrigPrc(String docItemOrigPrc) {
		DocItemOrigPrc = docItemOrigPrc;
	}
	public String getDocItemOrigTax() {
		return DocItemOrigTax;
	}
	@XmlElement(name = "DocItemOrigTax")
	public void setDocItemOrigTax(String docItemOrigTax) {
		DocItemOrigTax = docItemOrigTax;
	}
	public String getClerk() {
		return Clerk;
	}
	@XmlElement(name = "Clerk")
	public void setClerk(String clerk) {
		Clerk = clerk;
	}
	public String getItemLookup() {
		return ItemLookup;
	}
	@XmlElement(name = "ItemLookup")
	public void setItemLookup(String itemLookup) {
		ItemLookup = itemLookup;
	}
	public String getPrcLvl() {
		return PrcLvl;
	}
	@XmlElement(name = "PrcLvl")
	public void setPrcLvl(String prcLvl) {
		PrcLvl = prcLvl;
	}
	public String getItemSID() {
		return ItemSID;
	}
	@XmlElement(name = "ItemSID")
	public void setItemSID(String itemSID) {
		ItemSID = itemSID;
	}
	public String getDocItemDiscAmt() {
		return DocItemDiscAmt;
	}
	@XmlElement(name = "DocItemDiscAmt")
	public void setDocItemDiscAmt(String docItemDiscAmt) {
		DocItemDiscAmt = docItemDiscAmt;
	}
	public String getFC1Prc() {
		return FC1Prc;
	}
	@XmlElement(name = "FC1Prc")
	public void setFC1Prc(String fC1Prc) {
		FC1Prc = fC1Prc;
	}
	public String getStyleSID() {
		return StyleSID;
	}
	@XmlElement(name = "StyleSID")
	public void setStyleSID(String styleSID) {
		StyleSID = styleSID;
	}
	public String getDesc3() {
		return Desc3;
	}
	@XmlElement(name = "Desc3")
	public void setDesc3(String desc3) {
		Desc3 = desc3;
	}
	public String getDesc4() {
		return Desc4;
	}
	@XmlElement(name = "Desc4")
	public void setDesc4(String desc4) {
		Desc4 = desc4;
	}
	public String getInvnAUX0() {
		return InvnAUX0;
	}
	@XmlElement(name = "InvnAUX0")
	public void setInvnAUX0(String invnAUX0) {
		InvnAUX0 = invnAUX0;
	}
	public String getInvnAUX1() {
		return InvnAUX1;
	}
	@XmlElement(name = "InvnAUX1")
	public void setInvnAUX1(String invnAUX1) {
		InvnAUX1 = invnAUX1;
	}
	public String getInvnAUX2() {
		return InvnAUX2;
	}
	@XmlElement(name = "InvnAUX2")
	public void setInvnAUX2(String invnAUX2) {
		InvnAUX2 = invnAUX2;
	}
	public String getInvnAUX3() {
		return InvnAUX3;
	}
	@XmlElement(name = "InvnAUX3")
	public void setInvnAUX3(String invnAUX3) {
		InvnAUX3 = invnAUX3;
	}
	public String getInvnAUX4() {
		return InvnAUX4;
	}
	@XmlElement(name = "InvnAUX4")
	public void setInvnAUX4(String invnAUX4) {
		InvnAUX4 = invnAUX4;
	}
	public String getInvnAUX5() {
		return InvnAUX5;
	}
	@XmlElement(name = "InvnAUX5")
	public void setInvnAUX5(String invnAUX5) {
		InvnAUX5 = invnAUX5;
	}
	public String getInvnAUX6() {
		return InvnAUX6;
	}
	@XmlElement(name = "InvnAUX6")
	public void setInvnAUX6(String invnAUX6) {
		InvnAUX6 = invnAUX6;
	}
	public String getInvnAUX7() {
		return InvnAUX7;
	}
	@XmlElement(name = "InvnAUX7")
	public void setInvnAUX7(String invnAUX7) {
		InvnAUX7 = invnAUX7;
	}
	public String getInvnUDFStr() {
		return InvnUDFStr;
	}
	@XmlElement(name = "InvnUDFStr")
	public void setInvnUDFStr(String invnUDFStr) {
		InvnUDFStr = invnUDFStr;
	}
	public String getInvnUDFDate() {
		return InvnUDFDate;
	}
	@XmlElement(name = "InvnUDFDate")
	public void setInvnUDFDate(String invnUDFDate) {
		InvnUDFDate = invnUDFDate;
	}
	public String getDocItemRndDisc() {
		return DocItemRndDisc;
	}
	@XmlElement(name = "DocItemRndDisc")
	public void setDocItemRndDisc(String docItemRndDisc) {
		DocItemRndDisc = docItemRndDisc;
	}
	public String getExtOrigPrc() {
		return ExtOrigPrc;
	}
	@XmlElement(name = "ExtOrigPrc")
	public void setExtOrigPrc(String extOrigPrc) {
		ExtOrigPrc = extOrigPrc;
	}
	public String getTotalTax() {
		return TotalTax;
	}
	@XmlElement(name = "TotalTax")
	public void setTotalTax(String totalTax) {
		TotalTax = totalTax;
	}
	public String getTax2Prc() {
		return Tax2Prc;
	}
	@XmlElement(name = "Tax2Prc")
	public void setTax2Prc(String tax2Prc) {
		Tax2Prc = tax2Prc;
	}
	public String getTax2() {
		return Tax2;
	}
	@XmlElement(name = "Tax2")
	public void setTax2(String tax2) {
		Tax2 = tax2;
	}
	public String getTax2Cd() {
		return Tax2Cd;
	}
	@XmlElement(name = "Tax2Cd")
	public void setTax2Cd(String tax2Cd) {
		Tax2Cd = tax2Cd;
	}
	public String getTax1Cd() {
		return Tax1Cd;
	}
	@XmlElement(name = "Tax1Cd")
	public void setTax1Cd(String tax1Cd) {
		Tax1Cd = tax1Cd;
	}
	public String getTaxArea2() {
		return TaxArea2;
	}
	@XmlElement(name = "TaxArea2")
	public void setTaxArea2(String taxArea2) {
		TaxArea2 = taxArea2;
	}
	public String getSeqNum() {
		return SeqNum;
	}
	@XmlElement(name = "SeqNum")
	public void setSeqNum(String seqNum) {
		SeqNum = seqNum;
	}
	public String getExtTax() {
		return ExtTax;
	}
	@XmlElement(name = "ExtTax")
	public void setExtTax(String extTax) {
		ExtTax = extTax;
	}
	public String getExtTax2() {
		return ExtTax2;
	}
	@XmlElement(name = "ExtTax2")
	public void setExtTax2(String extTax2) {
		ExtTax2 = extTax2;
	}
	public String getExtTotalTax() {
		return ExtTotalTax;
	}
	@XmlElement(name = "ExtTotalTax")
	public void setExtTotalTax(String extTotalTax) {
		ExtTotalTax = extTotalTax;
	}
	public String getDiscountPercentWithTax() {
		return DiscountPercentWithTax;
	}
	@XmlElement(name = "DiscountPercentWithTax")
	public void setDiscountPercentWithTax(String discountPercentWithTax) {
		DiscountPercentWithTax = discountPercentWithTax;
	}
	public String getDiscountAmountWithTax() {
		return DiscountAmountWithTax;
	}
	@XmlElement(name = "DiscountAmountWithTax")
	public void setDiscountAmountWithTax(String discountAmountWithTax) {
		DiscountAmountWithTax = discountAmountWithTax;
	}
	public String getFCOriginalPrice() {
		return FCOriginalPrice;
	}
	@XmlElement(name = "FCOriginalPrice")
	public void setFCOriginalPrice(String fCOriginalPrice) {
		FCOriginalPrice = fCOriginalPrice;
	}
	public String getFCOriginalPriceWithTax() {
		return FCOriginalPriceWithTax;
	}
	@XmlElement(name = "FCOriginalPriceWithTax")
	public void setFCOriginalPriceWithTax(String fCOriginalPriceWithTax) {
		FCOriginalPriceWithTax = fCOriginalPriceWithTax;
	}
	public String getFCOriginalExtendedPrice() {
		return FCOriginalExtendedPrice;
	}
	@XmlElement(name = "FCOriginalExtendedPrice")
	public void setFCOriginalExtendedPrice(String fCOriginalExtendedPrice) {
		FCOriginalExtendedPrice = fCOriginalExtendedPrice;
	}
	public String getFCOriginalExtendedPriceWithTax() {
		return FCOriginalExtendedPriceWithTax;
	}
	@XmlElement(name = "FCOriginalExtendedPriceWithTax")
	public void setFCOriginalExtendedPriceWithTax(
			String fCOriginalExtendedPriceWithTax) {
		FCOriginalExtendedPriceWithTax = fCOriginalExtendedPriceWithTax;
	}
	public String getFCPrice() {
		return FCPrice;
	}
	@XmlElement(name = "FCPrice")
	public void setFCPrice(String fCPrice) {
		FCPrice = fCPrice;
	}
	public String getFCPriceWithTax() {
		return FCPriceWithTax;
	}
	@XmlElement(name = "FCPriceWithTax")
	public void setFCPriceWithTax(String fCPriceWithTax) {
		FCPriceWithTax = fCPriceWithTax;
	}
	public String getFCExtendedPrice() {
		return FCExtendedPrice;
	}
	@XmlElement(name = "FCExtendedPrice")
	public void setFCExtendedPrice(String fCExtendedPrice) {
		FCExtendedPrice = fCExtendedPrice;
	}
	public String getFCExtendedPriceWithTax() {
		return FCExtendedPriceWithTax;
	}
	@XmlElement(name = "FCExtendedPriceWithTax")
	public void setFCExtendedPriceWithTax(String fCExtendedPriceWithTax) {
		FCExtendedPriceWithTax = fCExtendedPriceWithTax;
	}
	public String getSerialNum() {
		return SerialNum;
	}
	@XmlElement(name = "SerialNum")
	public void setSerialNum(String serialNum) {
		SerialNum = serialNum;
	}

	public String getItemNote1() {
		return ItemNote1;
	}

	@XmlElement(name = "ItemNote1")
	public void setItemNote1(String itemNote1) {
		ItemNote1 = itemNote1;
	}

	public String getItemNote2() {
		return ItemNote2;
	}

	@XmlElement(name = "ItemNote2")
	public void setItemNote2(String itemNote2) {
		ItemNote2 = itemNote2;
	}

	public String getItemNote3() {
		return ItemNote3;
	}

	@XmlElement(name = "ItemNote3")
	public void setItemNote3(String itemNote3) {
		ItemNote3 = itemNote3;
	}

	public String getItemNote4() {
		return ItemNote4;
	}


	public void setItemNote4(String itemNote4) {
		ItemNote4 = itemNote4;
	}


	public String getItemNote5() {
		return ItemNote5;
	}


	public void setItemNote5(String itemNote5) {
		ItemNote5 = itemNote5;
	}


	public String getItemNote6() {
		return ItemNote6;
	}


	public void setItemNote6(String itemNote6) {
		ItemNote6 = itemNote6;
	}


	public String getItemNote7() {
		return ItemNote7;
	}


	public void setItemNote7(String itemNote7) {
		ItemNote7 = itemNote7;
	}


	public String getItemNote8() {
		return ItemNote8;
	}


	public void setItemNote8(String itemNote8) {
		ItemNote8 = itemNote8;
	}


	public String getItemNote9() {
		return ItemNote9;
	}


	public void setItemNote9(String itemNote9) {
		ItemNote9 = itemNote9;
	}


	public String getItemNote10() {
		return ItemNote10;
	}


	public void setItemNote10(String itemNote10) {
		ItemNote10 = itemNote10;
	}


	public String getItemLine() {
		return ItemLine;
	}

	@XmlElement(name = "ItemLine")
	public void setItemLine(String itemLine) {
		ItemLine = itemLine;
	}

	public String getNetPrice() {
		return NetPrice;
	}


	@XmlElement(name = "NetPrice")
	public void setNetPrice(String netPrice) {
		NetPrice = netPrice;
	}

	public String getReturnReason() {
		return ReturnReason;
	}
	@XmlElement(name = "ReturnReason")
	public void setReturnReason(String returnReason) {
		ReturnReason = returnReason;
	}
	public String getVoidReason() {
		return VoidReason;
	}
	@XmlElement(name = "VoidReason")
	public void setVoidReason(String voidReason) {
		VoidReason = voidReason;
	}
	
	public String getDiscountReason() {
		return DiscountReason;
	}
	@XmlElement(name = "DiscountReason")
	public void setDiscountReason(String discountReason) {
		DiscountReason = discountReason;
	}
	public String getTaxAmount() {
		return TaxAmount;
	}


	@XmlElement(name = "TaxAmount")
	public void setTaxAmount(String taxAmount) {
		TaxAmount = taxAmount;
	}
	
	public String getRefStoreCode() {
		return RefStoreCode;
	}
	@XmlElement(name = "RefStoreCode")
	public void setRefStoreCode(String refStoreCode) {
		this.RefStoreCode = refStoreCode;
	}
	public String getRefSubsidiaryNumber() {
		return RefSubsidiaryNumber;
	}
	@XmlElement(name = "RefSubsidiaryNumber")
	public void setRefSubsidiaryNumber(String refSubsidiaryNumber) {
		RefSubsidiaryNumber = refSubsidiaryNumber;
	}
	public String getRefReceipt() {
		return RefReceipt;
	}
	@XmlElement(name = "RefReceipt")
	public void setRefReceipt(String refReceipt) {
		this.RefReceipt = refReceipt;
	}

	public String getDocItemPrc() {
		return DocItemPrc;
	}
	@XmlElement(name = "DocItemPrc")
	public void setDocItemPrc(String docItemPrc) {
		DocItemPrc = docItemPrc;
	}

	public String getRefDocSID() {
		return RefDocSID;
	}
	@XmlElement(name = "RefDocSID")
	public void setRefDocSID(String refDocSID) {
		RefDocSID = refDocSID;
	}

}
