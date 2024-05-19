package org.mq.optculture.model.DR;

import java.util.List;

public class DROptCultureDetails {
	private String MembershipNumber;
	private String Email;
	private String Phone;
	private OCLoyaltyRedeem LoyaltyRedeem;
	private String LoyaltyRedeemReversal; //Used for Redemption Reversal in Returns
	private List<OCPromotions> Promotions;
	public String getMembershipNumber() {
		return MembershipNumber;
	}
	public void setMembershipNumber(String membershipNumber) {
		MembershipNumber = membershipNumber;
	}
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	public String getPhone() {
		return Phone;
	}
	public void setPhone(String phone) {
		Phone = phone;
	}
	public OCLoyaltyRedeem getLoyaltyRedeem() {
		return LoyaltyRedeem;
	}
	public void setLoyaltyRedeem(OCLoyaltyRedeem loyaltyRedeem) {
		LoyaltyRedeem = loyaltyRedeem;
	}
	public String getLoyaltyRedeemReversal() {
		return LoyaltyRedeemReversal;
	}
	public void setLoyaltyRedeemReversal(String creditRedeemedAmount) {
		LoyaltyRedeemReversal = creditRedeemedAmount;
	}
	public List<OCPromotions> getPromotions() {
		return Promotions;
	}
	public void setPromotions(List<OCPromotions> promotions) {
		Promotions = promotions;
	}
}
