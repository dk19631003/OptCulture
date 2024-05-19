package org.mq.optculture.model.DR;

import javax.xml.bind.annotation.XmlElement;

public class Receipt {


	public Receipt(String invcHdrNotes, String tax, String taxPrc, String discPrc, String discount, String invcComment1,
			String invcComment2, String taxArea, String taxRebatePrc, String taxRebate, String itemsCount,
			String shipping, String fee, String shippingPrc, String feeType, String feeTaxPrc, String taxOnFeeShip,
			String invcRoundAmt, String invcTotalRoundAmt, String invcTotalOfLineDisc, String subtotal, String total,
			String invcHdrRcptStatus, String invcHdrRcptType, String cashier, String docDate, String invcNum,
			String clerk, String billToCustNumber, String billToCustCompany, String billToFName, String billToLName,
			String billToTitle, String billToAddr1, String billToAddr2, String billToAddr3, String billToAddr4,
			String billToAddr5, String billToAddr6, String billToZip, String billToInfo1, String billToInfo2,
			String serialNo, String shipToCustNumber, String shipToCustCompany, String shipToFName, String shipToLName,
			String shipToTitle, String shipToAddr1, String shipToAddr2, String shipToAddr3, String shipToAddr4,
			String shipToAddr5, String shipToAddr6, String shipToZip, String shipToInfo1, String shipToInfo2,
			String billToCustSID, String shipToCustSID, String billToFullName, String shipToFullName,
			String workstation, String docTime, String sONumber, String sOType, String flag1, String flag2,
			String flag3, String billToPhone1, String billToPhone2, String shipToPhone1, String shipToPhone2,
			String sOCustPONumber, String storeHeading1, String storeHeading2, String storeHeading3, String tender,
			String store, String trackingNum, String credLim, String invcTotalQty, String totalFC1,
			String storeHeading4, String storeHeading5, String storeHeading6, String prcLvl, String extOrigPrc,
			String taxArea2, String totalTax, String tax2, String tax2Prc, String chargeDueDate, String shipToAux12,
			String shipToAux11, String shipToAux10, String shipToAux9, String shipToAux8, String shipToAux7,
			String shipToAux6, String shipToAux5, String shipToAux4, String shipToAux3, String shipToAux2,
			String shipToAux1, String billToAux12, String billToAux11, String billToAux10, String billToAux9,
			String billToAux8, String billToAux7, String billToAux6, String billToAux5, String billToAux4,
			String billToAux3, String billToAux2, String billToAux1, String billToUDF8, String billToUDF7,
			String billToUDF6, String billToUDF5, String billToUDF4, String billToUDF3, String billToUDF2,
			String shipToUDF8, String shipToUDF7, String shipToUDF6, String shipToUDF5, String shipToUDF4,
			String shipToUDF3, String shipToUDF2, String billToUDF1, String shipToUDF1, String billToEMail,
			String shipToEMail, String refDocSID, String docSID, String shippingName, String tendered, String change,
			String totalSavings, String giftCardTotal, String customerCopy, String country, String countryCode,
			String storeName, String storePhoneNumber, String companyname, String companyName2, String subsidiaryNumber,
			String storeCode, String currency, String taxAmount, String salesPerson, String refReceipt,
			String refStoreCode, String refSubsidiaryNumber, String taxMethod, String eCOMOrderNo, String storePhone2,
			String couponId, String comment1, String comment2, String fiscalDocID, String fiscalCode,
			String al_extract_date, String employeeID, String mobile_Phone_No) {
		InvcHdrNotes = invcHdrNotes;
		Tax = tax;
		TaxPrc = taxPrc;
		DiscPrc = discPrc;
		Discount = discount;
		InvcComment1 = invcComment1;
		InvcComment2 = invcComment2;
		TaxArea = taxArea;
		TaxRebatePrc = taxRebatePrc;
		TaxRebate = taxRebate;
		ItemsCount = itemsCount;
		Shipping = shipping;
		Fee = fee;
		ShippingPrc = shippingPrc;
		FeeType = feeType;
		FeeTaxPrc = feeTaxPrc;
		TaxOnFeeShip = taxOnFeeShip;
		InvcRoundAmt = invcRoundAmt;
		InvcTotalRoundAmt = invcTotalRoundAmt;
		InvcTotalOfLineDisc = invcTotalOfLineDisc;
		Subtotal = subtotal;
		Total = total;
		InvcHdrRcptStatus = invcHdrRcptStatus;
		InvcHdrRcptType = invcHdrRcptType;
		Cashier = cashier;
		DocDate = docDate;
		InvcNum = invcNum;
		Clerk = clerk;
		BillToCustNumber = billToCustNumber;
		BillToCustCompany = billToCustCompany;
		BillToFName = billToFName;
		BillToLName = billToLName;
		BillToTitle = billToTitle;
		BillToAddr1 = billToAddr1;
		BillToAddr2 = billToAddr2;
		BillToAddr3 = billToAddr3;
		BillToAddr4 = billToAddr4;
		BillToAddr5 = billToAddr5;
		BillToAddr6 = billToAddr6;
		BillToZip = billToZip;
		BillToInfo1 = billToInfo1;
		BillToInfo2 = billToInfo2;
		SerialNo = serialNo;
		ShipToCustNumber = shipToCustNumber;
		ShipToCustCompany = shipToCustCompany;
		ShipToFName = shipToFName;
		ShipToLName = shipToLName;
		ShipToTitle = shipToTitle;
		ShipToAddr1 = shipToAddr1;
		ShipToAddr2 = shipToAddr2;
		ShipToAddr3 = shipToAddr3;
		ShipToAddr4 = shipToAddr4;
		ShipToAddr5 = shipToAddr5;
		ShipToAddr6 = shipToAddr6;
		ShipToZip = shipToZip;
		ShipToInfo1 = shipToInfo1;
		ShipToInfo2 = shipToInfo2;
		BillToCustSID = billToCustSID;
		ShipToCustSID = shipToCustSID;
		BillToFullName = billToFullName;
		ShipToFullName = shipToFullName;
		Workstation = workstation;
		DocTime = docTime;
		SONumber = sONumber;
		SOType = sOType;
		Flag1 = flag1;
		Flag2 = flag2;
		Flag3 = flag3;
		BillToPhone1 = billToPhone1;
		BillToPhone2 = billToPhone2;
		ShipToPhone1 = shipToPhone1;
		ShipToPhone2 = shipToPhone2;
		SOCustPONumber = sOCustPONumber;
		StoreHeading1 = storeHeading1;
		StoreHeading2 = storeHeading2;
		StoreHeading3 = storeHeading3;
		Tender = tender;
		Store = store;
		TrackingNum = trackingNum;
		CredLim = credLim;
		InvcTotalQty = invcTotalQty;
		TotalFC1 = totalFC1;
		StoreHeading4 = storeHeading4;
		StoreHeading5 = storeHeading5;
		StoreHeading6 = storeHeading6;
		PrcLvl = prcLvl;
		ExtOrigPrc = extOrigPrc;
		TaxArea2 = taxArea2;
		TotalTax = totalTax;
		Tax2 = tax2;
		Tax2Prc = tax2Prc;
		ChargeDueDate = chargeDueDate;
		ShipToAux12 = shipToAux12;
		ShipToAux11 = shipToAux11;
		ShipToAux10 = shipToAux10;
		ShipToAux9 = shipToAux9;
		ShipToAux8 = shipToAux8;
		ShipToAux7 = shipToAux7;
		ShipToAux6 = shipToAux6;
		ShipToAux5 = shipToAux5;
		ShipToAux4 = shipToAux4;
		ShipToAux3 = shipToAux3;
		ShipToAux2 = shipToAux2;
		ShipToAux1 = shipToAux1;
		BillToAux12 = billToAux12;
		BillToAux11 = billToAux11;
		BillToAux10 = billToAux10;
		BillToAux9 = billToAux9;
		BillToAux8 = billToAux8;
		BillToAux7 = billToAux7;
		BillToAux6 = billToAux6;
		BillToAux5 = billToAux5;
		BillToAux4 = billToAux4;
		BillToAux3 = billToAux3;
		BillToAux2 = billToAux2;
		BillToAux1 = billToAux1;
		BillToUDF8 = billToUDF8;
		BillToUDF7 = billToUDF7;
		BillToUDF6 = billToUDF6;
		BillToUDF5 = billToUDF5;
		BillToUDF4 = billToUDF4;
		BillToUDF3 = billToUDF3;
		BillToUDF2 = billToUDF2;
		ShipToUDF8 = shipToUDF8;
		ShipToUDF7 = shipToUDF7;
		ShipToUDF6 = shipToUDF6;
		ShipToUDF5 = shipToUDF5;
		ShipToUDF4 = shipToUDF4;
		ShipToUDF3 = shipToUDF3;
		ShipToUDF2 = shipToUDF2;
		BillToUDF1 = billToUDF1;
		ShipToUDF1 = shipToUDF1;
		BillToEMail = billToEMail;
		ShipToEMail = shipToEMail;
		RefDocSID = refDocSID;
		DocSID = docSID;
		ShippingName = shippingName;
		Tendered = tendered;
		Change = change;
		TotalSavings = totalSavings;
		GiftCardTotal = giftCardTotal;
		CustomerCopy = customerCopy;
		Country = country;
		CountryCode = countryCode;
		StoreName = storeName;
		StorePhoneNumber = storePhoneNumber;
		Companyname = companyname;
		CompanyName = companyName2;
		SubsidiaryNumber = subsidiaryNumber;
		StoreCode = storeCode;
		Currency = currency;
		TaxAmount = taxAmount;
		SalesPerson = salesPerson;
		RefReceipt = refReceipt;
		RefStoreCode = refStoreCode;
		RefSubsidiaryNumber = refSubsidiaryNumber;
		TaxMethod = taxMethod;
		ECOMOrderNo = eCOMOrderNo;
		StorePhone2 = storePhone2;
		CouponId = couponId;
		Comment1 = comment1;
		Comment2 = comment2;
		FiscalDocID = fiscalDocID;
		FiscalCode = fiscalCode;
		this.al_extract_date = al_extract_date;
		EmployeeID = employeeID;
		Mobile_Phone_No = mobile_Phone_No;
	}
	private String InvcHdrNotes;
	private String Tax;
	private String TaxPrc;
	private String DiscPrc;
	private String Discount;
	private String InvcComment1;
	private String InvcComment2;
	private String TaxArea;
	private String TaxRebatePrc;
	private String TaxRebate;
	private String ItemsCount;
	private String Shipping;
	private String Fee;
	private String ShippingPrc;
	private String FeeType;
	private String FeeTaxPrc;
	private String TaxOnFeeShip;
	private String InvcRoundAmt;
	private String InvcTotalRoundAmt;
	private String InvcTotalOfLineDisc;
	private String Subtotal;
	private String Total;
	private String InvcHdrRcptStatus;
	private String InvcHdrRcptType;
	private String Cashier;
	private String DocDate;
	private String InvcNum;
	private String Clerk;
	private String BillToCustNumber;
	private String BillToCustCompany;
	private String BillToFName;
	private String BillToLName;
	private String BillToTitle;
	private String BillToAddr1;
	private String BillToAddr2;
	private String BillToAddr3;
	private String BillToAddr4;
	private String BillToAddr5;
	private String BillToAddr6;
	private String BillToZip;
	private String BillToInfo1;
	private String BillToInfo2;
	private String SerialNo;
	
	private String ShipToCustNumber;
	private String ShipToCustCompany;
	private String ShipToFName;
	private String ShipToLName;
	private String ShipToTitle;
	private String ShipToAddr1;
	private String ShipToAddr2;
	private String ShipToAddr3;
	private String ShipToAddr4;
	private String ShipToAddr5;
	private String ShipToAddr6;
	private String ShipToZip;
	private String ShipToInfo1;
	private String ShipToInfo2;
	private String BillToCustSID;
	private String ShipToCustSID;
	private String BillToFullName;
	private String ShipToFullName;
	private String Workstation;
	private String DocTime;
	private String SONumber;
	private String SOType;
	private String Flag1;
	private String Flag2;
	private String Flag3;
	private String BillToPhone1;
	private String BillToPhone2;
	private String ShipToPhone1;
	private String ShipToPhone2;
	private String SOCustPONumber;
	private String StoreHeading1;
	private String StoreHeading2;
	private String StoreHeading3;
	private String Tender;
	private String Store;
	private String TrackingNum;
	private String CredLim;
	private String InvcTotalQty;
	private String TotalFC1;
	private String StoreHeading4;
	private String StoreHeading5;
	private String StoreHeading6;
	private String PrcLvl;
	private String ExtOrigPrc;
	private String TaxArea2;
	private String TotalTax;
	private String Tax2;
	private String Tax2Prc;
	private String ChargeDueDate;
	private String ShipToAux12;
	private String ShipToAux11;
	private String ShipToAux10;
	private String ShipToAux9;
	private String ShipToAux8;
	private String ShipToAux7;
	private String ShipToAux6;
	private String ShipToAux5;
	private String ShipToAux4;
	private String ShipToAux3;
	private String ShipToAux2;
	private String ShipToAux1;
	private String BillToAux12;
	private String BillToAux11;
	private String BillToAux10;
	private String BillToAux9;
	private String BillToAux8;
	private String BillToAux7;
	private String BillToAux6;
	private String BillToAux5;
	private String BillToAux4;
	private String BillToAux3;
	private String BillToAux2;
	private String BillToAux1;
	private String BillToUDF8;
	private String BillToUDF7;
	private String BillToUDF6;
	private String BillToUDF5;
	private String BillToUDF4;
	private String BillToUDF3;
	private String BillToUDF2;
	private String ShipToUDF8;
	private String ShipToUDF7;
	private String ShipToUDF6;
	private String ShipToUDF5;
	private String ShipToUDF4;
	private String ShipToUDF3;
	private String ShipToUDF2;
	private String BillToUDF1;
	private String ShipToUDF1;
	private String BillToEMail;
	private String ShipToEMail;
	private String RefDocSID;
	private String DocSID;
	private String ShippingName;
	private String Tendered;
	private String Change;
	private String TotalSavings;
	private String GiftCardTotal;
	
	
	private String CustomerCopy;
	private String Country;
	private String CountryCode;
	private String StoreName;
	private String StorePhoneNumber;
	private String Companyname;
	private String CompanyName;
	private String SubsidiaryNumber;
	private String StoreCode;
	private String Currency;
	private String TaxAmount;
	private String SalesPerson;
	private String RefReceipt;
	
	private String RefStoreCode;
	private String RefSubsidiaryNumber;
	private String TaxMethod;
	private String ECOMOrderNo;
	private String StorePhone2;
	private String CouponId;
	private String Comment1;
	private String Comment2;
	private String FiscalDocID;
	private String FiscalCode;
	private String al_extract_date;
	private String EmployeeID;
	private String Mobile_Phone_No;
	private String ReceiptType;
	
	
	//APP-4052
		private String DOB;
		private String Nationality;

		
		
		public String getDOB() {
			return DOB;
		}
		public void setDOB(String dOB) {
			DOB = dOB;
		}
		public String getNationality() {
			return Nationality;
		}
		public void setNationality(String nationality) {
			Nationality = nationality;
		}
		
	
	public String getReceiptType() {
		return ReceiptType;
	}
	public void setReceiptType(String receiptType) {
		ReceiptType = receiptType;
	}
	public String getSerialNo() {
		return SerialNo;
	}
	@XmlElement(name = "SerialNo")
	public void setSerialNo(String serialNo) {
		SerialNo = serialNo;
	}
	
	public String getInvcHdrNotes() {
		return InvcHdrNotes;
	}
	@XmlElement(name = "InvcHdrNotes")
	public void setInvcHdrNotes(String invcHdrNotes) {
		InvcHdrNotes = invcHdrNotes;
	}
	public String getTax() {
		return Tax;
	}
	@XmlElement(name = "Tax")
	public void setTax(String tax) {
		Tax = tax;
	}
	public String getTaxPrc() {
		return TaxPrc;
	}
	@XmlElement(name = "TaxPrc")
	public void setTaxPrc(String taxPrc) {
		TaxPrc = taxPrc;
	}
	public String getDiscPrc() {
		return DiscPrc;
	}
	@XmlElement(name = "DiscPrc")
	public void setDiscPrc(String discPrc) {
		DiscPrc = discPrc;
	}
	public String getDiscount() {
		return Discount;
	}
	@XmlElement(name = "Discount")
	public void setDiscount(String discount) {
		Discount = discount;
	}
	public String getInvcComment1() {
		return InvcComment1;
	}
	@XmlElement(name = "InvcComment1")
	public void setInvcComment1(String invcComment1) {
		InvcComment1 = invcComment1;
	}
	public String getInvcComment2() {
		return InvcComment2;
	}
	@XmlElement(name = "InvcComment2")
	public void setInvcComment2(String invcComment2) {
		InvcComment2 = invcComment2;
	}
	public String getTaxArea() {
		return TaxArea;
	}
	@XmlElement(name = "TaxArea")
	public void setTaxArea(String taxArea) {
		TaxArea = taxArea;
	}
	public String getTaxRebatePrc() {
		return TaxRebatePrc;
	}
	@XmlElement(name = "TaxRebatePrc")
	public void setTaxRebatePrc(String taxRebatePrc) {
		TaxRebatePrc = taxRebatePrc;
	}
	public String getTaxRebate() {
		return TaxRebate;
	}
	@XmlElement(name = "TaxRebate")
	public void setTaxRebate(String taxRebate) {
		TaxRebate = taxRebate;
	}
	public String getItemsCount() {
		return ItemsCount;
	}
	@XmlElement(name = "ItemsCount")
	public void setItemsCount(String itemsCount) {
		ItemsCount = itemsCount;
	}
	public String getShipping() {
		return Shipping;
	}
	@XmlElement(name = "Shipping")
	public void setShipping(String shipping) {
		Shipping = shipping;
	}
	public String getFee() {
		return Fee;
	}
	@XmlElement(name = "Fee")
	public void setFee(String fee) {
		Fee = fee;
	}
	public String getShippingPrc() {
		return ShippingPrc;
	}
	@XmlElement(name = "ShippingPrc")
	public void setShippingPrc(String shippingPrc) {
		ShippingPrc = shippingPrc;
	}
	public String getFeeType() {
		return FeeType;
	}
	@XmlElement(name = "FeeType")
	public void setFeeType(String feeType) {
		FeeType = feeType;
	}
	public String getFeeTaxPrc() {
		return FeeTaxPrc;
	}
	@XmlElement(name = "FeeTaxPrc")
	public void setFeeTaxPrc(String feeTaxPrc) {
		FeeTaxPrc = feeTaxPrc;
	}
	public String getTaxOnFeeShip() {
		return TaxOnFeeShip;
	}
	@XmlElement(name = "TaxOnFeeShip")
	public void setTaxOnFeeShip(String taxOnFeeShip) {
		TaxOnFeeShip = taxOnFeeShip;
	}
	public String getInvcRoundAmt() {
		return InvcRoundAmt;
	}
	@XmlElement(name = "InvcRoundAmt")
	public void setInvcRoundAmt(String invcRoundAmt) {
		InvcRoundAmt = invcRoundAmt;
	}
	public String getInvcTotalRoundAmt() {
		return InvcTotalRoundAmt;
	}
	@XmlElement(name = "InvcTotalRoundAmt")
	public void setInvcTotalRoundAmt(String invcTotalRoundAmt) {
		InvcTotalRoundAmt = invcTotalRoundAmt;
	}
	public String getInvcTotalOfLineDisc() {
		return InvcTotalOfLineDisc;
	}
	@XmlElement(name = "InvcTotalOfLineDisc")
	public void setInvcTotalOfLineDisc(String invcTotalOfLineDisc) {
		InvcTotalOfLineDisc = invcTotalOfLineDisc;
	}
	public String getSubtotal() {
		return Subtotal;
	}
	@XmlElement(name = "Subtotal")
	public void setSubtotal(String subtotal) {
		Subtotal = subtotal;
	}
	public String getTotal() {
		return Total;
	}
	@XmlElement(name = "Total")
	public void setTotal(String total) {
		Total = total;
	}
	public String getInvcHdrRcptStatus() {
		return InvcHdrRcptStatus;
	}
	@XmlElement(name = "InvcHdrRcptStatus")
	public void setInvcHdrRcptStatus(String invcHdrRcptStatus) {
		InvcHdrRcptStatus = invcHdrRcptStatus;
	}
	public String getInvcHdrRcptType() {
		return InvcHdrRcptType;
	}
	@XmlElement(name = "InvcHdrRcptType")
	public void setInvcHdrRcptType(String invcHdrRcptType) {
		InvcHdrRcptType = invcHdrRcptType;
	}
	public String getCashier() {
		return Cashier;
	}
	@XmlElement(name = "Cashier")
	public void setCashier(String cashier) {
		Cashier = cashier;
	}
	public String getDocDate() {
		return DocDate;
	}
	@XmlElement(name = "DocDate")
	public void setDocDate(String docDate) {
		DocDate = docDate;
	}
	public String getInvcNum() {
		return InvcNum;
	}
	@XmlElement(name = "InvcNum")
	public void setInvcNum(String invcNum) {
		InvcNum = invcNum;
	}
	public String getClerk() {
		return Clerk;
	}
	@XmlElement(name = "Clerk")
	public void setClerk(String clerk) {
		Clerk = clerk;
	}
	public String getBillToCustNumber() {
		return BillToCustNumber;
	}
	@XmlElement(name = "BillToCustNumber")
	public void setBillToCustNumber(String billToCustNumber) {
		BillToCustNumber = billToCustNumber;
	}
	public String getBillToCustCompany() {
		return BillToCustCompany;
	}
	@XmlElement(name = "BillToCustCompany")
	public void setBillToCustCompany(String billToCustCompany) {
		BillToCustCompany = billToCustCompany;
	}
	public String getBillToFName() {
		return BillToFName;
	}
	@XmlElement(name = "BillToFName")
	public void setBillToFName(String billToFName) {
		BillToFName = billToFName;
	}
	public String getBillToLName() {
		return BillToLName;
	}
	@XmlElement(name = "BillToLName")
	public void setBillToLName(String billToLName) {
		BillToLName = billToLName;
	}
	public String getBillToTitle() {
		return BillToTitle;
	}
	@XmlElement(name = "BillToTitle")
	public void setBillToTitle(String billToTitle) {
		BillToTitle = billToTitle;
	}
	public String getBillToAddr1() {
		return BillToAddr1;
	}
	@XmlElement(name = "BillToAddr1")
	public void setBillToAddr1(String billToAddr1) {
		BillToAddr1 = billToAddr1;
	}
	public String getBillToAddr2() {
		return BillToAddr2;
	}
	@XmlElement(name = "BillToAddr2")
	public void setBillToAddr2(String billToAddr2) {
		BillToAddr2 = billToAddr2;
	}
	public String getBillToAddr3() {
		return BillToAddr3;
	}
	@XmlElement(name = "BillToAddr3")
	public void setBillToAddr3(String billToAddr3) {
		BillToAddr3 = billToAddr3;
	}
	public String getBillToZip() {
		return BillToZip;
	}
	@XmlElement(name = "BillToZip")
	public void setBillToZip(String billToZip) {
		BillToZip = billToZip;
	}
	public String getBillToInfo1() {
		return BillToInfo1;
	}
	@XmlElement(name = "BillToInfo1")
	public void setBillToInfo1(String billToInfo1) {
		BillToInfo1 = billToInfo1;
	}
	public String getBillToInfo2() {
		return BillToInfo2;
	}
	@XmlElement(name = "BillToInfo2")
	public void setBillToInfo2(String billToInfo2) {
		BillToInfo2 = billToInfo2;
	}
	public String getShipToCustNumber() {
		return ShipToCustNumber;
	}
	@XmlElement(name = "ShipToCustNumber")
	public void setShipToCustNumber(String shipToCustNumber) {
		ShipToCustNumber = shipToCustNumber;
	}
	public String getShipToCustCompany() {
		return ShipToCustCompany;
	}
	@XmlElement(name = "ShipToCustCompany")
	public void setShipToCustCompany(String shipToCustCompany) {
		ShipToCustCompany = shipToCustCompany;
	}
	public String getShipToFName() {
		return ShipToFName;
	}
	@XmlElement(name = "ShipToFName")
	public void setShipToFName(String shipToFName) {
		ShipToFName = shipToFName;
	}
	public String getShipToLName() {
		return ShipToLName;
	}
	@XmlElement(name = "ShipToLName")
	public void setShipToLName(String shipToLName) {
		ShipToLName = shipToLName;
	}
	public String getShipToTitle() {
		return ShipToTitle;
	}
	@XmlElement(name = "ShipToTitle")
	public void setShipToTitle(String shipToTitle) {
		ShipToTitle = shipToTitle;
	}
	public String getShipToAddr1() {
		return ShipToAddr1;
	}
	@XmlElement(name = "ShipToAddr1")
	public void setShipToAddr1(String shipToAddr1) {
		ShipToAddr1 = shipToAddr1;
	}
	public String getShipToAddr2() {
		return ShipToAddr2;
	}
	@XmlElement(name = "ShipToAddr2")
	public void setShipToAddr2(String shipToAddr2) {
		ShipToAddr2 = shipToAddr2;
	}
	public String getShipToAddr3() {
		return ShipToAddr3;
	}
	@XmlElement(name = "ShipToAddr3")
	public void setShipToAddr3(String shipToAddr3) {
		ShipToAddr3 = shipToAddr3;
	}
	public String getShipToZip() {
		return ShipToZip;
	}
	@XmlElement(name = "ShipToZip")
	public void setShipToZip(String shipToZip) {
		ShipToZip = shipToZip;
	}
	public String getShipToInfo1() {
		return ShipToInfo1;
	}
	@XmlElement(name = "ShipToInfo1")
	public void setShipToInfo1(String shipToInfo1) {
		ShipToInfo1 = shipToInfo1;
	}
	public String getShipToInfo2() {
		return ShipToInfo2;
	}
	@XmlElement(name = "ShipToInfo2")
	public void setShipToInfo2(String shipToInfo2) {
		ShipToInfo2 = shipToInfo2;
	}
	public String getBillToCustSID() {
		return BillToCustSID;
	}
	@XmlElement(name = "BillToCustSID")
	public void setBillToCustSID(String billToCustSID) {
		BillToCustSID = billToCustSID;
	}
	public String getShipToCustSID() {
		return ShipToCustSID;
	}
	@XmlElement(name = "ShipToCustSID")
	public void setShipToCustSID(String shipToCustSID) {
		ShipToCustSID = shipToCustSID;
	}
	public String getBillToFullName() {
		return BillToFullName;
	}
	@XmlElement(name = "BillToFullName")
	public void setBillToFullName(String billToFullName) {
		BillToFullName = billToFullName;
	}
	public String getShipToFullName() {
		return ShipToFullName;
	}
	@XmlElement(name = "ShipToFullName")
	public void setShipToFullName(String shipToFullName) {
		ShipToFullName = shipToFullName;
	}
	public String getWorkstation() {
		return Workstation;
	}
	@XmlElement(name = "Workstation")
	public void setWorkstation(String workstation) {
		Workstation = workstation;
	}
	public String getDocTime() {
		return DocTime;
	}
	@XmlElement(name = "DocTime")
	public void setDocTime(String docTime) {
		DocTime = docTime;
	}
	public String getSONumber() {
		return SONumber;
	}
	@XmlElement(name = "SONumber")
	public void setSONumber(String sONumber) {
		SONumber = sONumber;
	}
	public String getSOType() {
		return SOType;
	}
	@XmlElement(name = "SOType")
	public void setSOType(String sOType) {
		SOType = sOType;
	}
	public String getFlag1() {
		return Flag1;
	}
	@XmlElement(name = "Flag1")
	public void setFlag1(String flag1) {
		Flag1 = flag1;
	}
	public String getFlag2() {
		return Flag2;
	}
	@XmlElement(name = "Flag2")
	public void setFlag2(String flag2) {
		Flag2 = flag2;
	}
	public String getFlag3() {
		return Flag3;
	}
	@XmlElement(name = "Flag3")
	public void setFlag3(String flag3) {
		Flag3 = flag3;
	}
	public String getBillToPhone1() {
		return BillToPhone1;
	}
	@XmlElement(name = "BillToPhone1")
	public void setBillToPhone1(String billToPhone1) {
		BillToPhone1 = billToPhone1;
	}
	public String getBillToPhone2() {
		return BillToPhone2;
	}
	@XmlElement(name = "BillToPhone2")
	public void setBillToPhone2(String billToPhone2) {
		BillToPhone2 = billToPhone2;
	}
	public String getShipToPhone1() {
		return ShipToPhone1;
	}
	@XmlElement(name = "ShipToPhone1")
	public void setShipToPhone1(String shipToPhone1) {
		ShipToPhone1 = shipToPhone1;
	}
	public String getShipToPhone2() {
		return ShipToPhone2;
	}
	@XmlElement(name = "ShipToPhone2")
	public void setShipToPhone2(String shipToPhone2) {
		ShipToPhone2 = shipToPhone2;
	}
	public String getSOCustPONumber() {
		return SOCustPONumber;
	}
	@XmlElement(name = "SOCustPONumber")
	public void setSOCustPONumber(String sOCustPONumber) {
		SOCustPONumber = sOCustPONumber;
	}
	public String getStoreHeading1() {
		return StoreHeading1;
	}
	@XmlElement(name = "StoreHeading1")
	public void setStoreHeading1(String storeHeading1) {
		StoreHeading1 = storeHeading1;
	}
	public String getStoreHeading2() {
		return StoreHeading2;
	}
	@XmlElement(name = "StoreHeading2")
	public void setStoreHeading2(String storeHeading2) {
		StoreHeading2 = storeHeading2;
	}
	public String getStoreHeading3() {
		return StoreHeading3;
	}
	@XmlElement(name = "StoreHeading3")
	public void setStoreHeading3(String storeHeading3) {
		StoreHeading3 = storeHeading3;
	}
	public String getTender() {
		return Tender;
	}
	@XmlElement(name = "Tender")
	public void setTender(String tender) {
		Tender = tender;
	}
	public String getStore() {
		return Store;
	}
	@XmlElement(name = "Store")
	public void setStore(String store) {
		Store = store;
	}
	public String getTrackingNum() {
		return TrackingNum;
	}
	@XmlElement(name = "TrackingNum")
	public void setTrackingNum(String trackingNum) {
		TrackingNum = trackingNum;
	}
	public String getCredLim() {
		return CredLim;
	}
	@XmlElement(name = "CredLim")
	public void setCredLim(String credLim) {
		CredLim = credLim;
	}
	public String getInvcTotalQty() {
		return InvcTotalQty;
	}
	@XmlElement(name = "InvcTotalQty")
	public void setInvcTotalQty(String invcTotalQty) {
		InvcTotalQty = invcTotalQty;
	}
	public String getTotalFC1() {
		return TotalFC1;
	}
	@XmlElement(name = "TotalFC1")
	public void setTotalFC1(String totalFC1) {
		TotalFC1 = totalFC1;
	}
	public String getStoreHeading4() {
		return StoreHeading4;
	}
	@XmlElement(name = "StoreHeading4")
	public void setStoreHeading4(String storeHeading4) {
		StoreHeading4 = storeHeading4;
	}
	public String getStoreHeading5() {
		return StoreHeading5;
	}
	@XmlElement(name = "StoreHeading5")
	public void setStoreHeading5(String storeHeading5) {
		StoreHeading5 = storeHeading5;
	}
	public String getStoreHeading6() {
		return StoreHeading6;
	}
	@XmlElement(name = "StoreHeading6")
	public void setStoreHeading6(String storeHeading6) {
		StoreHeading6 = storeHeading6;
	}
	public String getPrcLvl() {
		return PrcLvl;
	}
	@XmlElement(name = "PrcLvl")
	public void setPrcLvl(String prcLvl) {
		PrcLvl = prcLvl;
	}
	public String getExtOrigPrc() {
		return ExtOrigPrc;
	}
	@XmlElement(name = "ExtOrigPrc")
	public void setExtOrigPrc(String extOrigPrc) {
		ExtOrigPrc = extOrigPrc;
	}
	public String getTaxArea2() {
		return TaxArea2;
	}
	@XmlElement(name = "TaxArea2")
	public void setTaxArea2(String taxArea2) {
		TaxArea2 = taxArea2;
	}
	public String getTotalTax() {
		return TotalTax;
	}
	@XmlElement(name = "TotalTax")
	public void setTotalTax(String totalTax) {
		TotalTax = totalTax;
	}
	public String getTax2() {
		return Tax2;
	}
	@XmlElement(name = "Tax2")
	public void setTax2(String tax2) {
		Tax2 = tax2;
	}
	public String getTax2Prc() {
		return Tax2Prc;
	}
	@XmlElement(name = "Tax2Prc")
	public void setTax2Prc(String tax2Prc) {
		Tax2Prc = tax2Prc;
	}
	public String getChargeDueDate() {
		return ChargeDueDate;
	}
	@XmlElement(name = "ChargeDueDate")
	public void setChargeDueDate(String chargeDueDate) {
		ChargeDueDate = chargeDueDate;
	}
	public String getShipToAux12() {
		return ShipToAux12;
	}
	@XmlElement(name = "ShipToAux12")
	public void setShipToAux12(String shipToAux12) {
		ShipToAux12 = shipToAux12;
	}
	public String getShipToAux11() {
		return ShipToAux11;
	}
	@XmlElement(name = "ShipToAux11")
	public void setShipToAux11(String shipToAux11) {
		ShipToAux11 = shipToAux11;
	}
	public String getShipToAux10() {
		return ShipToAux10;
	}
	@XmlElement(name = "ShipToAux10")
	public void setShipToAux10(String shipToAux10) {
		ShipToAux10 = shipToAux10;
	}
	public String getShipToAux9() {
		return ShipToAux9;
	}
	@XmlElement(name = "ShipToAux9")
	public void setShipToAux9(String shipToAux9) {
		ShipToAux9 = shipToAux9;
	}
	public String getShipToAux8() {
		return ShipToAux8;
	}
	@XmlElement(name = "ShipToAux8")
	public void setShipToAux8(String shipToAux8) {
		ShipToAux8 = shipToAux8;
	}
	public String getShipToAux7() {
		return ShipToAux7;
	}
	@XmlElement(name = "ShipToAux7")
	public void setShipToAux7(String shipToAux7) {
		ShipToAux7 = shipToAux7;
	}
	public String getShipToAux6() {
		return ShipToAux6;
	}
	@XmlElement(name = "ShipToAux6")
	public void setShipToAux6(String shipToAux6) {
		ShipToAux6 = shipToAux6;
	}
	public String getShipToAux5() {
		return ShipToAux5;
	}
	@XmlElement(name = "ShipToAux5")
	public void setShipToAux5(String shipToAux5) {
		ShipToAux5 = shipToAux5;
	}
	public String getShipToAux4() {
		return ShipToAux4;
	}
	@XmlElement(name = "ShipToAux4")
	public void setShipToAux4(String shipToAux4) {
		ShipToAux4 = shipToAux4;
	}
	public String getShipToAux3() {
		return ShipToAux3;
	}
	@XmlElement(name = "ShipToAux3")
	public void setShipToAux3(String shipToAux3) {
		ShipToAux3 = shipToAux3;
	}
	public String getShipToAux2() {
		return ShipToAux2;
	}
	@XmlElement(name = "ShipToAux2")
	public void setShipToAux2(String shipToAux2) {
		ShipToAux2 = shipToAux2;
	}
	public String getShipToAux1() {
		return ShipToAux1;
	}
	@XmlElement(name = "ShipToAux1")
	public void setShipToAux1(String shipToAux1) {
		ShipToAux1 = shipToAux1;
	}
	public String getBillToAux12() {
		return BillToAux12;
	}
	@XmlElement(name = "BillToAux12")
	public void setBillToAux12(String billToAux12) {
		BillToAux12 = billToAux12;
	}
	public String getBillToAux11() {
		return BillToAux11;
	}
	@XmlElement(name = "BillToAux11")
	public void setBillToAux11(String billToAux11) {
		BillToAux11 = billToAux11;
	}
	public String getBillToAux10() {
		return BillToAux10;
	}
	@XmlElement(name = "BillToAux10")
	public void setBillToAux10(String billToAux10) {
		BillToAux10 = billToAux10;
	}
	public String getBillToAux9() {
		return BillToAux9;
	}
	@XmlElement(name = "BillToAux9")
	public void setBillToAux9(String billToAux9) {
		BillToAux9 = billToAux9;
	}
	public String getBillToAux8() {
		return BillToAux8;
	}
	@XmlElement(name = "BillToAux8")
	public void setBillToAux8(String billToAux8) {
		BillToAux8 = billToAux8;
	}
	public String getBillToAux7() {
		return BillToAux7;
	}
	@XmlElement(name = "BillToAux7")
	public void setBillToAux7(String billToAux7) {
		BillToAux7 = billToAux7;
	}
	public String getBillToAux6() {
		return BillToAux6;
	}
	@XmlElement(name = "BillToAux6")
	public void setBillToAux6(String billToAux6) {
		BillToAux6 = billToAux6;
	}
	public String getBillToAux5() {
		return BillToAux5;
	}
	@XmlElement(name = "BillToAux5")
	public void setBillToAux5(String billToAux5) {
		BillToAux5 = billToAux5;
	}
	public String getBillToAux4() {
		return BillToAux4;
	}
	@XmlElement(name = "BillToAux4")
	public void setBillToAux4(String billToAux4) {
		BillToAux4 = billToAux4;
	}
	public String getBillToAux3() {
		return BillToAux3;
	}
	@XmlElement(name = "BillToAux3")
	public void setBillToAux3(String billToAux3) {
		BillToAux3 = billToAux3;
	}
	public String getBillToAux2() {
		return BillToAux2;
	}
	@XmlElement(name = "BillToAux2")
	public void setBillToAux2(String billToAux2) {
		BillToAux2 = billToAux2;
	}
	public String getBillToAux1() {
		return BillToAux1;
	}
	@XmlElement(name = "BillToAux1")
	public void setBillToAux1(String billToAux1) {
		BillToAux1 = billToAux1;
	}
	public String getBillToUDF8() {
		return BillToUDF8;
	}
	@XmlElement(name = "BillToUDF8")
	public void setBillToUDF8(String billToUDF8) {
		BillToUDF8 = billToUDF8;
	}
	public String getBillToUDF7() {
		return BillToUDF7;
	}
	@XmlElement(name = "BillToUDF7")
	public void setBillToUDF7(String billToUDF7) {
		BillToUDF7 = billToUDF7;
	}
	public String getBillToUDF6() {
		return BillToUDF6;
	}
	@XmlElement(name = "BillToUDF6")
	public void setBillToUDF6(String billToUDF6) {
		BillToUDF6 = billToUDF6;
	}
	public String getBillToUDF5() {
		return BillToUDF5;
	}
	@XmlElement(name = "BillToUDF5")
	public void setBillToUDF5(String billToUDF5) {
		BillToUDF5 = billToUDF5;
	}
	public String getBillToUDF4() {
		return BillToUDF4;
	}
	@XmlElement(name = "BillToUDF4")
	public void setBillToUDF4(String billToUDF4) {
		BillToUDF4 = billToUDF4;
	}
	public String getBillToUDF3() {
		return BillToUDF3;
	}
	@XmlElement(name = "BillToUDF3")
	public void setBillToUDF3(String billToUDF3) {
		BillToUDF3 = billToUDF3;
	}
	public String getBillToUDF2() {
		return BillToUDF2;
	}
	@XmlElement(name = "BillToUDF2")
	public void setBillToUDF2(String billToUDF2) {
		BillToUDF2 = billToUDF2;
	}
	public String getShipToUDF8() {
		return ShipToUDF8;
	}
	@XmlElement(name = "ShipToUDF8")
	public void setShipToUDF8(String shipToUDF8) {
		ShipToUDF8 = shipToUDF8;
	}
	public String getShipToUDF7() {
		return ShipToUDF7;
	}
	@XmlElement(name = "ShipToUDF7")
	public void setShipToUDF7(String shipToUDF7) {
		ShipToUDF7 = shipToUDF7;
	}
	public String getShipToUDF6() {
		return ShipToUDF6;
	}
	@XmlElement(name = "ShipToUDF6")
	public void setShipToUDF6(String shipToUDF6) {
		ShipToUDF6 = shipToUDF6;
	}
	public String getShipToUDF5() {
		return ShipToUDF5;
	}
	@XmlElement(name = "ShipToUDF5")
	public void setShipToUDF5(String shipToUDF5) {
		ShipToUDF5 = shipToUDF5;
	}
	public String getShipToUDF4() {
		return ShipToUDF4;
	}
	@XmlElement(name = "ShipToUDF4")
	public void setShipToUDF4(String shipToUDF4) {
		ShipToUDF4 = shipToUDF4;
	}
	public String getShipToUDF3() {
		return ShipToUDF3;
	}
	@XmlElement(name = "ShipToUDF3")
	public void setShipToUDF3(String shipToUDF3) {
		ShipToUDF3 = shipToUDF3;
	}
	public String getShipToUDF2() {
		return ShipToUDF2;
	}
	@XmlElement(name = "ShipToUDF2")
	public void setShipToUDF2(String shipToUDF2) {
		ShipToUDF2 = shipToUDF2;
	}
	public String getBillToUDF1() {
		return BillToUDF1;
	}
	@XmlElement(name = "BillToUDF1")
	public void setBillToUDF1(String billToUDF1) {
		BillToUDF1 = billToUDF1;
	}
	public String getShipToUDF1() {
		return ShipToUDF1;
	}
	@XmlElement(name = "ShipToUDF1")
	public void setShipToUDF1(String shipToUDF1) {
		ShipToUDF1 = shipToUDF1;
	}
	public String getBillToEMail() {
		return BillToEMail;
	}
	@XmlElement(name = "BillToEMail")
	public void setBillToEMail(String billToEMail) {
		BillToEMail = billToEMail;
	}
	public String getShipToEMail() {
		return ShipToEMail;
	}
	@XmlElement(name = "ShipToEMail")
	public void setShipToEMail(String shipToEMail) {
		ShipToEMail = shipToEMail;
	}
	public String getRefDocSID() {
		return RefDocSID;
	}
	@XmlElement(name = "RefDocSID")
	public void setRefDocSID(String refDocSID) {
		RefDocSID = refDocSID;
	}
	public String getDocSID() {
		return DocSID;
	}
	@XmlElement(name = "DocSID")
	public void setDocSID(String docSID) {
		DocSID = docSID;
	}
	public String getShippingName() {
		return ShippingName;
	}
	@XmlElement(name = "ShippingName")
	public void setShippingName(String shippingName) {
		ShippingName = shippingName;
	}
	public String getTendered() {
		return Tendered;
	}
	@XmlElement(name = "Tendered")
	public void setTendered(String tendered) {
		Tendered = tendered;
	}
	public String getChange() {
		return Change;
	}
	@XmlElement(name = "Change")
	public void setChange(String change) {
		Change = change;
	}
	public String getTotalSavings() {
		return TotalSavings;
	}
	@XmlElement(name = "TotalSavings")
	public void setTotalSavings(String totalSavings) {
		TotalSavings = totalSavings;
	}
	public String getGiftCardTotal() {
		return GiftCardTotal;
	}
	@XmlElement(name = "GiftCardTotal")
	public void setGiftCardTotal(String giftCardTotal) {
		GiftCardTotal = giftCardTotal;
	}
	
	public String getBillToAddr4() {
		return BillToAddr4;
	}
	
	@XmlElement(name = "BillToAddr4")
	public void setBillToAddr4(String billToAddr4) {
		BillToAddr4 = billToAddr4;
	}
	public String getBillToAddr5() {
		return BillToAddr5;
	}
	@XmlElement(name = "BillToAddr5")
	public void setBillToAddr5(String billToAddr5) {
		BillToAddr5 = billToAddr5;
	}
	public String getBillToAddr6() {
		return BillToAddr6;
	}
	@XmlElement(name = "BillToAddr6")
	public void setBillToAddr6(String billToAddr6) {
		BillToAddr6 = billToAddr6;
	}
	public String getShipToAddr4() {
		return ShipToAddr4;
	}
	@XmlElement(name = "ShipToAddr4")
	public void setShipToAddr4(String shipToAddr4) {
		ShipToAddr4 = shipToAddr4;
	}
	public String getShipToAddr5() {
		return ShipToAddr5;
	}
	@XmlElement(name = "ShipToAddr5")
	public void setShipToAddr5(String shipToAddr5) {
		ShipToAddr5 = shipToAddr5;
	}
	public String getShipToAddr6() {
		return ShipToAddr6;
	}
	@XmlElement(name = "ShipToAddr6")
	public void setShipToAddr6(String shipToAddr6) {
		ShipToAddr6 = shipToAddr6;
	}
	
	public String getCustomerCopy() {
		return CustomerCopy;
	}
	@XmlElement(name = "CustomerCopy")
	public void setCustomerCopy(String customerCopy) {
		CustomerCopy = customerCopy;
	}
	
	
	public String getCountry() {
		return Country;
	}
	@XmlElement(name = "Country")
	public void setCountry(String country) {
		Country = country;
	}
	public String getCountryCode() {
		return CountryCode;
	}
	@XmlElement(name = "CountryCode")
	public void setCountryCode(String countryCode) {
		CountryCode = countryCode;
	}
	public String getStoreName() {
		return StoreName;
	}
	@XmlElement(name = "StoreName")
	public void setStoreName(String storeName) {
		StoreName = storeName;
	}
	public String getStorePhoneNumber() {
		return StorePhoneNumber;
	}
	@XmlElement(name = "StorePhoneNumber")
	public void setStorePhoneNumber(String storePhoneNumber) {
		StorePhoneNumber = storePhoneNumber;
	}
	public String getCompanyname() {
		return Companyname;
	}
	@XmlElement(name = "Companyname")
	public void setCompanyname(String companyname) {
		Companyname = companyname;
	}
	public String getCompanyName() {
		return CompanyName;
	}
	@XmlElement(name = "CompanyName")
	public void setCompanyName(String companyName) {
		CompanyName = companyName;
	}
	public String getSubsidiaryNumber() {
		return SubsidiaryNumber;
	}
	@XmlElement(name = "SubsidiaryNumber")
	public void setSubsidiaryNumber(String subsidiaryNumber) {
		SubsidiaryNumber = subsidiaryNumber;
	}
	public String getStoreCode() {
		return StoreCode;
	}
	@XmlElement(name = "StoreCode")
	public void setStoreCode(String storeCode) {
		StoreCode = storeCode;
	}
	public String getCurrency() {
		return Currency;
	}
	@XmlElement(name = "Currency")
	public void setCurrency(String currency) {
		Currency = currency;
	}

	public String getComment1() {
		return Comment1;
	}
	@XmlElement(name = "Comment1")
	public void setComment1(String comment1) {
		Comment1 = comment1;
	}
	public String getComment2() {
		return Comment2;
	}
	@XmlElement(name = "Comment2")
	public void setComment2(String comment2) {
		Comment2 = comment2;
	}
	
	
	public String getCouponId() {
		return CouponId;
	}
	@XmlElement(name = "CouponId")
	public void setCouponId(String couponId) {
		CouponId = couponId;
	}
	public String getStorePhone2() {
		return StorePhone2;
	}
	@XmlElement(name = "StorePhone2")
	public void setStorePhone2(String storePhone2) {
		StorePhone2 = storePhone2;
	}
	
	
	public String getECOMOrderNo() {
		return ECOMOrderNo;
	}
	@XmlElement(name = "ECOMOrderNo")
	public void setECOMOrderNo(String eCOMOrderNo) {
		ECOMOrderNo = eCOMOrderNo;
	}
	
	
	public String getSalesPerson() {
		return SalesPerson;
	}
	@XmlElement(name = "SalesPerson")
	public void setSalesPerson(String salesPerson) {
		this.SalesPerson = salesPerson;
	}
	public String getTaxAmount() {
		return TaxAmount;
	}
	@XmlElement(name = "TaxAmount")
	public void setTaxAmount(String taxAmount) {
		this.TaxAmount = taxAmount;
	}
	public String getFiscalDocID() {
		return FiscalDocID;
	}
	@XmlElement(name = "FiscalDocID")
	public void setFiscalDocID(String fiscalDocID) {
		FiscalDocID = fiscalDocID;
	}
	public String getFiscalCode() {
		return FiscalCode;
	}
	@XmlElement(name = "FiscalCode")
	public void setFiscalCode(String fiscalCode) {
		FiscalCode = fiscalCode;
	}
	
	
	
	/*public String getAL_Extract_Date() {
		return AL_Extract_Date;
	}
	@XmlElement(name = "AL_Extract_Date")
	public void setAL_Extract_Date(String aL_Extract_Date) {
		AL_Extract_Date = aL_Extract_Date;
	}*/
	
	public String getEmployeeID() {
		return EmployeeID;
	}
	@XmlElement(name = "EmployeeID")
	public void setEmployeeID(String employeeID) {
		EmployeeID = employeeID;
	}
	public String getMobile_Phone_No() {
		return Mobile_Phone_No;
	}
	@XmlElement(name = "Mobile_Phone_No")
	public void setMobile_Phone_No(String mobile_Phone_No) {
		Mobile_Phone_No = mobile_Phone_No;
	}
	public String getAl_extract_date() {
		return al_extract_date;
	}
	@XmlElement(name = "al_extract_date")
	public void setAl_extract_date(String al_extract_date) {
		this.al_extract_date = al_extract_date;
	}
	
	public String getRefReceipt() {
		return RefReceipt;
	}
	public void setRefReceipt(String refReceipt) {
		RefReceipt = refReceipt;
	}
	public String getRefStoreCode() {
		return RefStoreCode;
	}
	public void setRefStoreCode(String refStoreCode) {
		RefStoreCode = refStoreCode;
	}
	public String getRefSubsidiaryNumber() {
		return RefSubsidiaryNumber;
	}
	public void setRefSubsidiaryNumber(String refSubsidiaryNumber) {
		RefSubsidiaryNumber = refSubsidiaryNumber;
	}
	public String getTaxMethod() {
		return TaxMethod;
	}
	public void setTaxMethod(String taxMethod) {
		TaxMethod = taxMethod;
	}
	
	

}
