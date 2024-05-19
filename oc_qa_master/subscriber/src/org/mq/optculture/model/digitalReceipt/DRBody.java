package org.mq.optculture.model.digitalReceipt;


import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class DRBody {
	
	private Items items;
	//private CreditCardDR creditCards;

	//private List<DRItem> Items;
	private CreditCardDR creditCards;
	//private List<DRCreditCard> CreditCard;
	private DRCash Cash;
	private DRReceipt Receipt;
	private DRUserDetails UserDetails; 
	
	// Added after New DR Schema
	private DRDeposit Deposit;
	private DRFC FC;
	private DRCOD COD;
	private DRCheck Check;
	private GiftDR clsGiftCertificate;
	private GiftCardDR clsGiftCard ;
	private DebitCardDR clsDebitcard;
	private StoreCreditDR clsStoreCredit;
	private DRCharge Charge;
	
	// added after 3 rarely used tenders
	private PaymentsDR clsPayments;
	private DRTravelerCheck TravelerCheck;
	private DRFCCheck FCCheck;
	
	public Items getItems() {
		return items;
	}
	@XmlElement(name = "Items")
	public void setItems(Items items) {
		this.items = items;
	}
	
	/*public List<DRItem> getItems() {
		return Items;
	}
	public void setItems(List<DRItem> items) {
		Items = items;
	}
	public List<DRCreditCard> getCreditCard() {
		return CreditCard;
	}
	public void setCreditCard(List<DRCreditCard> creditCard) {
		CreditCard = creditCard;
	}*/
	public CreditCardDR getCreditCards() {
		return creditCards;
	}
	@XmlElement(name = "CreditCard")
	public void setCreditCards(CreditCardDR creditCards) {
		this.creditCards = creditCards;
	}
	
	public DRCash getCash() {
		return Cash;
	}
	@XmlElement(name = "Cash")
	public void setCash(DRCash cash) {
		Cash = cash;
	}

	public DRReceipt getReceipt() {
		return Receipt;
	}
	@XmlElement(name = "Receipt")
	public void setReceipt(DRReceipt receipt) {
		Receipt = receipt;
	}
	public DRUserDetails getUserDetails() {
		return UserDetails;
	}
	@XmlElement(name = "UserDetails")
	public void setUserDetails(DRUserDetails userDetails) {
		UserDetails = userDetails;
	}
	public DRDeposit getDeposit() {
		return Deposit;
	}
	@XmlElement(name = "Deposit")
	public void setDeposit(DRDeposit deposit) {
		Deposit = deposit;
	}
	public DRFC getFC() {
		return FC;
	}
	@XmlElement(name = "FC")
	public void setFC(DRFC fC) {
		FC = fC;
	}
	public DRCOD getCOD() {
		return COD;
	}
	@XmlElement(name = "COD")
	public void setCOD(DRCOD cOD) {
		COD = cOD;
	}
	public DRCheck getCheck() {
		return Check;
	}
	@XmlElement(name = "Check")
	public void setCheck(DRCheck check) {
		Check = check;
	}
	public GiftDR getClsGiftCertificate() {
		return clsGiftCertificate;
	}
	@XmlElement(name = "Gift")
	public void setClsGiftCertificate(GiftDR clsGiftCertificate) {
		this.clsGiftCertificate = clsGiftCertificate;
	}
	public GiftCardDR getClsGiftCard() {
		return clsGiftCard;
	}
	@XmlElement(name = "GiftCard")
	public void setClsGiftCard(GiftCardDR clsGiftCard) {
		this.clsGiftCard = clsGiftCard;
	}
	public DebitCardDR getClsDebitcard() {
		return clsDebitcard;
	}
	@XmlElement(name = "DebitCard")
	public void setClsDebitcard(DebitCardDR clsDebitcard) {
		this.clsDebitcard = clsDebitcard;
	}
	public StoreCreditDR getClsStoreCredit() {
		return clsStoreCredit;
	}
	@XmlElement(name = "StoreCredit")
	public void setClsStoreCredit(StoreCreditDR clsStoreCredit) {
		this.clsStoreCredit = clsStoreCredit;
	}
	public DRCharge getCharge() {
		return Charge;
	}
	@XmlElement(name = "Charge")
	public void setCharge(DRCharge charge) {
		Charge = charge;
	}
	public PaymentsDR getClsPayments() {
		return clsPayments;
	}
	@XmlElement(name = "Payments")
	public void setClsPayments(PaymentsDR clsPayments) {
		this.clsPayments = clsPayments;
	}
	public DRTravelerCheck getTravelerCheck() {
		return TravelerCheck;
	}
	@XmlElement(name = "TravelerCheck")
	public void setTravelerCheck(DRTravelerCheck travelerCheck) {
		TravelerCheck = travelerCheck;
	}
	public DRFCCheck getFCCheck() {
		return FCCheck;
	}
	@XmlElement(name = "FCCheck")
	public void setFCCheck(DRFCCheck fCCheck) {
		FCCheck = fCCheck;
	}
	
}
