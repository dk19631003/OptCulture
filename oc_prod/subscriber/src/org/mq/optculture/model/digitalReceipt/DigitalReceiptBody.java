package org.mq.optculture.model.digitalReceipt;

import java.util.List;

public class DigitalReceiptBody {
	
	//Body structure written to match App DR json
	
	private List<DRItem> Items;
	private List<DRCreditCard> CreditCard;
	private DRCash Cash;
	private DRReceipt Receipt;
	private DRUserDetails UserDetails; 
	private DRDeposit Deposit;
	private DRFC FC;
	private DRCOD COD;
	private DRCheck Check;
	private GiftDR clsGiftCertificate;
	private GiftCardDR clsGiftCard ;
	private DebitCardDR clsDebitcard;
	private StoreCreditDR clsStoreCredit;
	private DRCharge Charge;
	private PaymentsDR clsPayments;
	private DRTravelerCheck TravelerCheck;
	private DRFCCheck FCCheck;
	
	private List<Promotions> Promotions;
	
	public List<Promotions> getPromotions() {
		return Promotions;
	}
	public void setPromotions(List<Promotions> promotions) {
		Promotions = promotions;
	}
	public List<DRItem> getItems() {
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
	}
	public DRCash getCash() {
		return Cash;
	}
	public void setCash(DRCash cash) {
		Cash = cash;
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
	public DRFC getFC() {
		return FC;
	}
	public void setFC(DRFC fC) {
		FC = fC;
	}
	public DRCOD getCOD() {
		return COD;
	}
	public void setCOD(DRCOD cOD) {
		COD = cOD;
	}
	public DRCheck getCheck() {
		return Check;
	}
	public void setCheck(DRCheck check) {
		Check = check;
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
	public DebitCardDR getClsDebitcard() {
		return clsDebitcard;
	}
	public void setClsDebitcard(DebitCardDR clsDebitcard) {
		this.clsDebitcard = clsDebitcard;
	}
	public StoreCreditDR getClsStoreCredit() {
		return clsStoreCredit;
	}
	public void setClsStoreCredit(StoreCreditDR clsStoreCredit) {
		this.clsStoreCredit = clsStoreCredit;
	}
	public DRCharge getCharge() {
		return Charge;
	}
	public void setCharge(DRCharge charge) {
		Charge = charge;
	}
	public PaymentsDR getClsPayments() {
		return clsPayments;
	}
	public void setClsPayments(PaymentsDR clsPayments) {
		this.clsPayments = clsPayments;
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

}
