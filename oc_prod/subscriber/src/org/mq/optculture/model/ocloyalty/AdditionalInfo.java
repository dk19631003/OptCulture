package org.mq.optculture.model.ocloyalty;

import java.util.List;

public class AdditionalInfo {

	private String otpEnabled;
    private List<OTPRedeemLimit> otpRedeemLimit;
	private String pointsEquivalentCurrency;
	private String totalRedeemableCurrency;
	private String companyLogo;
	private String bannerImage;
	private String fontName;
	private String fontURL;
	
	public AdditionalInfo() {
	}
	
	public String getOtpEnabled() {
		return otpEnabled;
	}
	public void setOtpEnabled(String otpEnabled) {
		this.otpEnabled = otpEnabled;
	}
	
	public List<OTPRedeemLimit> getOtpRedeemLimit() {
		return otpRedeemLimit;
	}

	public void setOtpRedeemLimit(List<OTPRedeemLimit> otpRedeemLimit) {
		this.otpRedeemLimit = otpRedeemLimit;
	}
	
	public String getPointsEquivalentCurrency() {
		return pointsEquivalentCurrency;
	}
	public void setPointsEquivalentCurrency(String pointsEquivalentCurrency) {
		this.pointsEquivalentCurrency = pointsEquivalentCurrency;
	}
	public String getTotalRedeemableCurrency() {
		return totalRedeemableCurrency;
	}
	public void setTotalRedeemableCurrency(String totalRedeemableCurrency) {
		this.totalRedeemableCurrency = totalRedeemableCurrency;
	}

	public String getCompanyLogo() {
		return companyLogo;
	}

	public void setCompanyLogo(String companyLogo) {
		this.companyLogo = companyLogo;
	}

	public String getBannerImage() {
		return bannerImage;
	}

	public void setBannerImage(String bannerImage) {
		this.bannerImage = bannerImage;
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public String getFontURL() {
		return fontURL;
	}

	public void setFontURL(String fontURL) {
		this.fontURL = fontURL;
	}

	private String lifeTimePurchaseValue;
	public String getLifeTimePurchaseValue() {
		return lifeTimePurchaseValue;
	}

	public void setLifeTimePurchaseValue(String lifeTimePurchaseValue) {
		this.lifeTimePurchaseValue = lifeTimePurchaseValue;
	}

	}
