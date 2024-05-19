package org.mq.optculture.sales.json;


import java.util.List;

public class DRBody {
	
	private DRCash Cash;
	private DRFCCheck FCCheck;
	private DRCheck Check;
	private CreditCardDR creditCards;
	private DRCOD COD;
	private StoreCreditDR clsStoreCredit;
	private PaymentsDR clsPayments;
	private GiftDR clsGiftCertificate;
	private GiftCardDR clsGiftCard ;
	//private Items items;
	
	

	private List<DRItem> Items;
	//private List<DRCreditCard> CreditCard;
	private DRReceipt Receipt;
	private DRUserDetails UserDetails; 
	
	// Added after New DR Schema
	private DRDeposit Deposit;
	private DRFC FC;
	
	private DebitCardDR clsDebitcard;
	private FCDR clsFCDR; 
	private DRCharge Charge;
	
	// added after 3 rarely used tenders
	//private PaymentsDR clsPayments;
	private DRTravelerCheck TravelerCheck;
	//private DRFCCheck FCCheck;

	
	
	
	public DRCash getCash() {
		return Cash;
	}
	
	public void setCash(DRCash cash) {
		Cash = cash;
	}
	public CreditCardDR getCreditCards() {
		return creditCards;
	}
	public FCDR getClsFCDR() {
		return clsFCDR;
	}
	public void setClsFCDR(FCDR clsFCDR) {
		this.clsFCDR = clsFCDR;
	}
	public void setCreditCards(CreditCardDR creditCards) {
		this.creditCards = creditCards;
	}
	public DRCOD getCOD() {
		return COD;
	}
	
	public void setCOD(DRCOD cOD) {
		COD = cOD;
	}
	public StoreCreditDR getClsStoreCredit() {
		return clsStoreCredit;
	}
	public void setClsStoreCredit(StoreCreditDR clsStoreCredit) {
		this.clsStoreCredit = clsStoreCredit;
	}
	public PaymentsDR getClsPayments() {
		return clsPayments;
	}
	public void setClsPayments(PaymentsDR clsPayments) {
		this.clsPayments = clsPayments;
	}
	public GiftDR getClsGiftCertificate() {
		return clsGiftCertificate;
	}
	public void setClsGiftCertificate(GiftDR clsGiftCertificate) {
		this.clsGiftCertificate = clsGiftCertificate;
	}
	public GiftCardDR getClsGiftCard() {
		return clsGiftCard;
	}
	public void setClsGiftCard(GiftCardDR clsGiftCard) {
		this.clsGiftCard = clsGiftCard;
	}
	
	
	public List<DRItem> getItems() {
		return Items;
	}

	public void setItems(List<DRItem> items) {
		Items = items;
	}

	public DRReceipt getReceipt() {
		return Receipt;
	}
	
	public void setReceipt(DRReceipt receipt) {
		Receipt = receipt;
	}
	public DRUserDetails getUserDetails() {
		return UserDetails;
	}
	
	public void setUserDetails(DRUserDetails userDetails) {
		UserDetails = userDetails;
	}
	public DRDeposit getDeposit() {
		return Deposit;
	}
	
	public void setDeposit(DRDeposit deposit) {
		Deposit = deposit;
	}
	
	
	public DRCheck getCheck() {
		return Check;
	}
	
	
	public void setCheck(DRCheck check) {
		Check = check;
	}
	
	public DebitCardDR getClsDebitcard() {
		return clsDebitcard;
	}
	
	public void setClsDebitcard(DebitCardDR clsDebitcard) {
		this.clsDebitcard = clsDebitcard;
	}
	
	public DRCharge getCharge() {
		return Charge;
	}
	
	public void setCharge(DRCharge charge) {
		Charge = charge;
	}
	
	public DRTravelerCheck getTravelerCheck() {
		return TravelerCheck;
	}
	
	public void setTravelerCheck(DRTravelerCheck travelerCheck) {
		TravelerCheck = travelerCheck;
	}
	public DRFCCheck getFCCheck() {
		return FCCheck;
	}
	
	public void setFCCheck(DRFCCheck fCCheck) {
		FCCheck = fCCheck;
	}
	
	public DRFC getFC() {
		return FC;
	}
	
	public void setFC(DRFC fC) {
		FC = fC;
	}
	
}
