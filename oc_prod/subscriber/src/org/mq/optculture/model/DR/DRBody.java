package org.mq.optculture.model.DR;

import java.util.List;

import org.mq.optculture.model.DR.tender.COD;
import org.mq.optculture.model.DR.tender.Cash;
import org.mq.optculture.model.DR.tender.Charge;
import org.mq.optculture.model.DR.tender.Deposit;
import org.mq.optculture.model.DR.tender.Check;
import org.mq.optculture.model.DR.tender.DebitCard;
import org.mq.optculture.model.DR.tender.CreditCard;
import org.mq.optculture.model.DR.tender.FCCheck;
import org.mq.optculture.model.DR.tender.FC;
import org.mq.optculture.model.DR.tender.Gift;
import org.mq.optculture.model.DR.tender.GiftCard;
import org.mq.optculture.model.DR.tender.Payments;
import org.mq.optculture.model.DR.tender.StoreCredit;
import org.mq.optculture.model.DR.tender.TravellerCheck;
import org.mq.optculture.model.DR.tender.CustomTender;
import org.mq.optculture.model.digitalReceipt.Promotions;

public class DRBody {
private List<DRItem> Items;
private Receipt Receipt;

private Cash Cash;
private Charge Charge;
private List<Check> Check;
private COD COD;
private List<CreditCard> CreditCard;
private List<DebitCard> DebitCard;
private Deposit Deposit;
private List<FC> FC;
private List<FCCheck> FCCheck;
private List<Gift> Gift;
private List<GiftCard> GiftCard;
private List<Payments> Payments;
private List<StoreCredit> StoreCredit;
private List<TravellerCheck> TravelerCheck;
private List<CustomTender> CustomTender;
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
public Receipt getReceipt() {
	return Receipt;
}
public void setReceipt(Receipt receipt) {
	Receipt = receipt;
}
public Cash getCash() {
	return Cash;
}
public void setCash(Cash cash) {
	Cash = cash;
}
public Charge getCharge() {
	return Charge;
}
public void setCharge(Charge charge) {
	Charge = charge;
}
public List<Check> getCheck() {
	return Check;
}
public void setCheck(List<Check> check) {
	Check = check;
}
public COD getCOD() {
	return COD;
}
public void setCOD(COD cOD) {
	COD = cOD;
}
public List<CreditCard> getCreditCard() {
	return CreditCard;
}
public void setCreditCard(List<CreditCard> creditCard) {
	CreditCard = creditCard;
}
public List<DebitCard> getDebitCard() {
	return DebitCard;
}
public void setDebitCard(List<DebitCard> debitCard) {
	DebitCard = debitCard;
}
public Deposit getDeposit() {
	return Deposit;
}
public void setDeposit(Deposit deposit) {
	Deposit = deposit;
}
public List<FC> getFC() {
	return FC;
}
public void setFC(List<FC> fC) {
	FC = fC;
}
public List<FCCheck> getFCCheck() {
	return FCCheck;
}
public void setFCCheck(List<FCCheck> fCCheck) {
	FCCheck = fCCheck;
}
public List<Gift> getGift() {
	return Gift;
}
public void setGift(List<Gift> gift) {
	Gift = gift;
}
public List<GiftCard> getGiftCard() {
	return GiftCard;
}
public void setGiftCard(List<GiftCard> giftCard) {
	GiftCard = giftCard;
}
public List<Payments> getPayments() {
	return Payments;
}
public void setPayments(List<Payments> payments) {
	Payments = payments;
}
public List<StoreCredit> getStoreCredit() {
	return StoreCredit;
}
public void setStoreCredit(List<StoreCredit> storeCredit) {
	StoreCredit = storeCredit;
}
public List<TravellerCheck> getTravelerCheck() {
	return TravelerCheck;
}
public void setTravelerCheck(List<TravellerCheck> travelerCheck) {
	TravelerCheck = travelerCheck;
}
public List<CustomTender> getCustomTender() {
	return CustomTender;
}
public void setCustomTender(List<CustomTender> customTender) {
	CustomTender = customTender;
}

}
