package org.mq.optculture.model.DR.shopify;

import java.util.List;

public class ShopifyDRBody {
	
	private ShopifyReceiptDetails Receipt;
	private String RECEIPTNUMBER;
	private String RECEIPTAMOUNT;
	private String CARDTYPE;
	private String CUSTOMERID;
	private String USERNAME;
	private String ORGANISATION;
	private String TOKEN;
	private String STORELOCATIONID;
	private String CARDNUMBER;
	private String REQUESTID;
	//private List<ShopifyItems> Items;
	
	
	/*public List<ShopifyItems> getItems() {
		return Items;
	}
	public void setItems(List<ShopifyItems> items) {
		Items = items;
	}*/
	public ShopifyReceiptDetails getReceipt() {
		return Receipt;
	}
	public void setReceipt(ShopifyReceiptDetails receipt) {
		Receipt = receipt;
	}
	public String getRECEIPTNUMBER() {
		return RECEIPTNUMBER;
	}
	public void setRECEIPTNUMBER(String rECEIPTNUMBER) {
		RECEIPTNUMBER = rECEIPTNUMBER;
	}
	public String getRECEIPTAMOUNT() {
		return RECEIPTAMOUNT;
	}
	public void setRECEIPTAMOUNT(String rECEIPTAMOUNT) {
		RECEIPTAMOUNT = rECEIPTAMOUNT;
	}
	public String getCARDTYPE() {
		return CARDTYPE;
	}
	public void setCARDTYPE(String cARDTYPE) {
		CARDTYPE = cARDTYPE;
	}
	public String getCUSTOMERID() {
		return CUSTOMERID;
	}
	public void setCUSTOMERID(String cUSTOMERID) {
		CUSTOMERID = cUSTOMERID;
	}
	public String getUSERNAME() {
		return USERNAME;
	}
	public void setUSERNAME(String uSERNAME) {
		USERNAME = uSERNAME;
	}
	public String getORGANISATION() {
		return ORGANISATION;
	}
	public void setORGANISATION(String oRGANISATION) {
		ORGANISATION = oRGANISATION;
	}
	public String getTOKEN() {
		return TOKEN;
	}
	public void setTOKEN(String tOKEN) {
		TOKEN = tOKEN;
	}
	public String getSTORELOCATIONID() {
		return STORELOCATIONID;
	}
	public void setSTORELOCATIONID(String sTORELOCATIONID) {
		STORELOCATIONID = sTORELOCATIONID;
	}
	public String getCARDNUMBER() {
		return CARDNUMBER;
	}
	public void setCARDNUMBER(String cARDNUMBER) {
		CARDNUMBER = cARDNUMBER;
	}
	public String getREQUESTID() {
		return REQUESTID;
	}
	public void setREQUESTID(String rEQUESTID) {
		REQUESTID = rEQUESTID;
	}
	
		
}
