package org.mq.optculture.model.mobileapp;

import org.mq.optculture.model.ocloyalty.Status;

public class WebPortalBrandingResponse {
	private String orgId;
	private String username;
	/*
	private String password;
	private String token;*/
	private String coverimage;
	private String logoimage;
	private String bannerImage;
	private String themecolor;
	private String balanceCardThemeColor;
	private String balanceCardTextColor;
	private Status status;
	private String customerName;
	private String bannerName;
	private String fontName;
	private String fontURL;
	private String tabName;
	private String tabImage;
	private String token;
	private String contactsList;
	private String email;
	private String mobile;
	private Long loginType;
	private Long cardSettings;
	private String referralImage;
	private String loyaltyImage;
	
	
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getContactsList() {
		
		return contactsList;
	}
	public void setContactsList(String contactsList) {
		this.contactsList = contactsList;
	}
	public String getCurrencySymbol() {
		return currencySymbol;
	}
	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}
	private String currencySymbol;
	private PrerequisiteInfo prerequisiteInfo;
	
	public PrerequisiteInfo getPrerequisiteInfo() {
		return prerequisiteInfo;
	}
	public void setPrerequisiteInfo(PrerequisiteInfo prerequisiteInfo) {
		this.prerequisiteInfo = prerequisiteInfo;
	}
	public WebPortalBrandingResponse() {}
	public WebPortalBrandingResponse(String username, 
			String logo,String theme,String coverImage,String custName,
			String bannerName, String currencySymbol, String contactsList) {
		
		this.username=username;
		this.currencySymbol = currencySymbol;
		this.contactsList = contactsList;
		/*this.orgId=orgId;
		this.token=token;
		this.password=pwd;*/
		this.logoimage=logo;
		this.themecolor=theme;
		this.coverimage=coverImage;
		this.customerName=custName;
		this.bannerName=bannerName;
	}
	public String getBannerName() {
		return bannerName;
	}
	public void setBannerName(String bannerName) {
		this.bannerName = bannerName;
	}
	
	public String getCoverimage() {
		return coverimage;
	}
	public void setCoverimage(String coverimage) {
		this.coverimage = coverimage;
	}
	public String getLogoimage() {
		return logoimage;
	}
	public void setLogoimage(String logoimage) {
		this.logoimage = logoimage;
	}
	public String getBannerImage() {
		return bannerImage;
	}
	public void setBannerImage(String bannerImage) {
		this.bannerImage = bannerImage;
	}
	public String getThemecolor() {
		return themecolor;
	}
	public void setThemecolor(String themecolor) {
		this.themecolor = themecolor;
	}
	public String getBalanceCardThemeColor() {
		return balanceCardThemeColor;
	}
	public void setBalanceCardThemeColor(String balanceCardThemeColor) {
		this.balanceCardThemeColor = balanceCardThemeColor;
	}
	public String getBalanceCardTextColor() {
		return balanceCardTextColor;
	}
	public void setBalanceCardTextColor(String balanceCardTextColor) {
		this.balanceCardTextColor = balanceCardTextColor;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
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
	public String getTabName() {
		return tabName;
	}
	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	public String getTabImage() {
		return tabImage;
	}
	public void setTabImage(String tabImage) {
		this.tabImage = tabImage;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Long getLoginType() {
		return loginType;
	}
	public void setLoginType(Long loginType) {
		this.loginType = loginType;
	}
	public Long getCardSettings() {
		return cardSettings;
	}
	public void setCardSettings(Long cardSettings) {
		this.cardSettings = cardSettings;
	}
	public String getReferralImage() {
		return referralImage;
	}
	public void setReferralImage(String referralImage) {
		this.referralImage = referralImage;
	}
	public String getLoyaltyImage() {
		return loyaltyImage;
	}
	public void setLoyaltyImage(String loyaltyImage) {
		this.loyaltyImage = loyaltyImage;
	}
	
	
}
