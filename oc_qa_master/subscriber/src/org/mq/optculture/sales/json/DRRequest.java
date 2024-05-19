package  org.mq.optculture.sales.json;

import java.util.List;

public class DRRequest {

	private List<DRItem> Items;
	private List<DRCreditCard> CreditCard;
	private DRCash Cash;
	private DRReceipt Receipt;
	private DRUserDetails UserDetails; 
	// added after New DR Schema
	private DRDeposit Deposit;
	private DRCOD COD;
	private DRFC FC;
	private DRCheck Check;
	private List<DRGift> Gift;
	private List<DRGiftCard> GiftCard;
	private List<DRDebitCard> DebitCard;
	private List<DRStoreCredit> StoreCredit;
	private DRCharge Charge;
	
	// Added after 3 rarely user tenders
	
	private List<DRPayments> Payments;
	private DRTravelerCheck TravelerCheck;
	private DRFCCheck FCCheck;
	
	public DRRequest() {
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
	public DRCOD getCOD() {
		return COD;
	}
	public void setCod(DRCOD cod) {
		COD = cod;
	}
	public DRFC getFc() {
		return FC;
	}
	public void setFc(DRFC fc) {
		FC = fc;
	}
	public DRCheck getCheck() {
		return Check;
	}
	public void setCheck(DRCheck check) {
		Check = check;
	}
	public List<DRGift> getGift() {
		return Gift;
	}
	public void setGift(List<DRGift> gift) {
		Gift = gift;
	}
	public List<DRGiftCard> getGiftCard() {
		return GiftCard;
	}
	public void setGiftCard(List<DRGiftCard> giftCard) {
		GiftCard = giftCard;
	}
	public List<DRDebitCard> getDebitCard() {
		return DebitCard;
	}
	public void setDebitCard(List<DRDebitCard> debitCard) {
		DebitCard = debitCard;
	}
	public List<DRStoreCredit> getStoreCredit() {
		return StoreCredit;
	}
	public void setStoreCredit(List<DRStoreCredit> storeCredit) {
		StoreCredit = storeCredit;
	}
	public DRCharge getCharge() {
		return Charge;
	}
	public void setCharge(DRCharge charge) {
		Charge = charge;
	}
	
	public List<DRPayments> getPayments() {
		return Payments;
	}
	public void setPayments(List<DRPayments> payments) {
		Payments = payments;
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
