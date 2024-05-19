package org.mq.optculture.business.helper.DRToLty;

import java.util.List;

import org.mq.optculture.model.DR.tender.CreditCard;
import org.mq.optculture.model.DR.tender.CustomTender;
import org.mq.optculture.model.DR.tender.COD;

public class DRToLoyaltyExtractionReceipt {

	private String DocDate;
	private String InvcNum;
	private String DocTime;
	
	private String DocSID;
	private String Store;
	private String SubsidiaryNumber;
	private String Total;
	private String Subtotal;
	private List<CreditCard> listOfCreditCards ;
	private COD COD;
	private String InvcHdrNotes;
	private String ECOMOrderNo;
	private String InvcComment1;
	private String InvcComment2;
	private String RefDocSID;
	private String RefReceipt;
	private String refStore;
	private String refSbs;
	private String ReceiptType;
	private String Discount;
	private String BillToInfo1;
	private List<CustomTender> CustomTender;
	private String DiscountReasonName;
	
	public String getDiscountReasonName() {
		return DiscountReasonName;
	}
	public void setDiscountReasonName(String discountReasonName) {
		DiscountReasonName = discountReasonName;
	}
	public DRToLoyaltyExtractionReceipt(String docDate, String invcNum, String docTime, String docSID, String store,
			String subsidiaryNumber, String total, String subtotal, List<CreditCard> listOfCreditCards,
			org.mq.optculture.model.DR.tender.COD cOD,String invcHdrNotes,String invcComment1,String invcComment2,
			String eCOMOrderNo,String refDocSID,String refReceipt, String refStore, String refSbs,String discount) {
		super();
		DocDate = docDate;
		InvcNum = invcNum;
		DocTime = docTime;
		DocSID = docSID;
		Store = store;
		SubsidiaryNumber = subsidiaryNumber;
		Total = total;
		Subtotal = subtotal;
		this.listOfCreditCards = listOfCreditCards;
		COD = cOD;
		InvcHdrNotes=invcHdrNotes;
		InvcComment1=invcComment1;
		InvcComment2=invcComment2;
		ECOMOrderNo=eCOMOrderNo;
		RefDocSID=refDocSID;
		RefReceipt=refReceipt;
		this.refStore = refStore;
		this.refSbs = refSbs;
		this.Discount=discount;
	}
	public String getECOMOrderNo() {
		return ECOMOrderNo;
	}
	public void setECOMOrderNo(String eCOMOrderNo) {
		ECOMOrderNo = eCOMOrderNo;
	}
	public String getDocDate() {
		return DocDate;
	}
	public void setDocDate(String docDate) {
		DocDate = docDate;
	}
	public String getInvcNum() {
		return InvcNum;
	}
	public void setInvcNum(String invcNum) {
		InvcNum = invcNum;
	}
	public String getDocTime() {
		return DocTime;
	}
	public void setDocTime(String docTime) {
		DocTime = docTime;
	}
	public String getDocSID() {
		return DocSID;
	}
	public void setDocSID(String docSID) {
		DocSID = docSID;
	}
	public String getStore() {
		return Store;
	}
	public void setStore(String store) {
		Store = store;
	}
	public String getSubsidiaryNumber() {
		return SubsidiaryNumber;
	}
	public void setSubsidiaryNumber(String subsidiaryNumber) {
		SubsidiaryNumber = subsidiaryNumber;
	}
	public String getTotal() {
		return Total;
	}
	public void setTotal(String total) {
		Total = total;
	}
	public List<CreditCard> getListOfCreditCards() {
		return listOfCreditCards;
	}
	public void setListOfCreditCards(List<CreditCard> listOfCreditCards) {
		this.listOfCreditCards = listOfCreditCards;
	}
	public COD getCOD() {
		return COD;
	}
	public void setCOD(COD cOD) {
		COD = cOD;
	}
	public String getSubtotal() {
		return Subtotal;
	}
	public void setSubtotal(String subtotal) {
		Subtotal = subtotal;
	}
	public String getInvcHdrNotes() {
		return InvcHdrNotes;
	}
	public void setInvcHdrNotes(String invcHdrNotes) {
		InvcHdrNotes = invcHdrNotes;
	}
	public String getInvcComment1() {
		return InvcComment1;
	}
	public void setInvcComment1(String invcComment1) {
		InvcComment1 = invcComment1;
	}
	public String getInvcComment2() {
		return InvcComment2;
	}
	public void setInvcComment2(String invcComment2) {
		InvcComment2 = invcComment2;
	}
	public String getRefDocSID() {
		return RefDocSID;
	}
	public void setRefDocSID(String refDocSID) {
		RefDocSID = refDocSID;
	}
	public String getRefReceipt() {
		return RefReceipt;
	}
	public void setRefReceipt(String refReceipt) {
		RefReceipt = refReceipt;
	}
	public String getReceiptType() {
		return ReceiptType;
	}
	public void setReceiptType(String receiptType) {
		ReceiptType = receiptType;
	}
	public String getRefStore() {
		return refStore;
	}
	public void setRefStore(String refStore) {
		this.refStore = refStore;
	}
	public String getRefSbs() {
		return refSbs;
	}
	public void setRefSbs(String refSbs) {
		this.refSbs = refSbs;
	}
	public String getDiscount() {
		return Discount;
	}
	public void setDiscount(String discount) {
		Discount = discount;
	}
	public String getBillToInfo1() {
		return BillToInfo1;
	}
	public void setBillToInfo1(String billToInfo1) {
		BillToInfo1 = billToInfo1;
	}
	public List<CustomTender> getCustomTender() {
		return CustomTender;
	}
	public void setCustomTender(List<CustomTender> customTender) {
		CustomTender = customTender;
	}
	
	
}
