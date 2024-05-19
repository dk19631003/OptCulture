package org.mq.marketer.campaign.beans;
/**
 * This objects represents loyalty_settings persistent data in java object format
 * @author vinod.bokare
 *
 */
public class LoyaltySettings implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//Instance Variables 
	private Long ltySettingId;
	private Long userId;
	private Long userOrgId;
	private String urlStr;
	private String colorCode;
	private String path;
	private String loyaltyType;
	private Long orgOwner;
	private String bannerPath;
	private String homePageImagePath;
	private String homePageColorCode;
	private String bannerName;
	private String balanceCardColorCode;
	private String balanceCardTextColorCode;
	private String fontName;
	private String fontURL;
	private String tabName;
	private String tabImagePath;
	private String isActive;
	private String email;
	private String mobile;
	
	
	private boolean includeFirstname;
	private boolean includeLastname;
	private boolean includeMobilenumber;
	private boolean includeEmailaddress;
	private String smartEreceiptJsonConfig;
	private Long loginType;
	private Long cardSettings;
	private String referralImage;
	private String loyaltyImage;
	
	
	
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
	public String getSmartEreceiptJsonConfig() {
		return smartEreceiptJsonConfig;
	}
	public void setSmartEreceiptJsonConfig(String smartEreceiptJsonConfig) {
		this.smartEreceiptJsonConfig = smartEreceiptJsonConfig;
	}
	
	
	private boolean includeStreet;
	public boolean isIncludeStreet() {
		return includeStreet;
	}
	public void setIncludeStreet(boolean includeStreet) {
		this.includeStreet = includeStreet;
	}
	public boolean isIncludeCity() {
		return includeCity;
	}
	public void setIncludeCity(boolean includeCity) {
		this.includeCity = includeCity;
	}
	public boolean isIncludeState() {
		return includeState;
	}
	public void setIncludeState(boolean includeState) {
		this.includeState = includeState;
	}
	public boolean isIncludePostalCode() {
		return includePostalCode;
	}
	public void setIncludePostalCode(boolean includePostalCode) {
		this.includePostalCode = includePostalCode;
	}
	public boolean isIncludeCountry() {
		return includeCountry;
	}
	public void setIncludeCountry(boolean includeCountry) {
		this.includeCountry = includeCountry;
	}
	public boolean isIncludeBirthday() {
		return includeBirthday;
	}
	public void setIncludeBirthday(boolean includeBirthday) {
		this.includeBirthday = includeBirthday;
	}
	public boolean isIncludeAnniversary() {
		return includeAnniversary;
	}
	public void setIncludeAnniversary(boolean includeAnniversary) {
		this.includeAnniversary = includeAnniversary;
	}
	public boolean isIncludeGender() {
		return includeGender;
	}
	public void setIncludeGender(boolean includeGender) {
		this.includeGender = includeGender;
	}
	private boolean includeCity;
	private boolean includeState;
	private boolean includePostalCode;
	
	
	private boolean includeCountry;
	private boolean includeBirthday;
	private boolean includeAnniversary;
	private boolean includeGender;
	
	
	
	
	public boolean isIncludeFirstname() {
		return includeFirstname;
	}
	public void setIncludeFirstname(boolean includeFirstname) {
		this.includeFirstname = includeFirstname;
	}
	public boolean isIncludeLastname() {
		return includeLastname;
	}
	public void setIncludeLastname(boolean includeLastname) {
		this.includeLastname = includeLastname;
	}
	public boolean isIncludeMobilenumber() {
		return includeMobilenumber;
	}
	public void setIncludeMobilenumber(boolean includeMobilenumber) {
		this.includeMobilenumber = includeMobilenumber;
	}
	public boolean isIncludeEmailaddress() {
		return includeEmailaddress;
	}
	public void setIncludeEmailaddress(boolean includeEmailaddress) {
		this.includeEmailaddress = includeEmailaddress;
	}
	
	
	
	
	
	
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
	public String getTabName() {
		return tabName;
	}
	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	public String getTabImagePath() {
		return tabImagePath;
	}
	public void setTabImagePath(String tabImagePath) {
		this.tabImagePath = tabImagePath;
	}
	//Default Constructor. 
	public LoyaltySettings(){
		
	}
	/**
	 * @return the ltySettingId
	 */
	public Long getLtySettingId() {
		return ltySettingId;
	}
	/**
	 * @param ltySettingId the ltySettingId to set
	 */
	public void setLtySettingId(Long ltySettingId) {
		this.ltySettingId = ltySettingId;
	}
	
	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	/**
	 * @return the userOrgId
	 */
	public Long getUserOrgId() {
		return userOrgId;
	}
	/**
	 * @param userOrgId the userOrgId to set
	 */
	public void setUserOrgId(Long userOrgId) {
		this.userOrgId = userOrgId;
	}
	/**
	 * @return the urlStr
	 */
	public String getUrlStr() {
		return urlStr;
	}
	/**
	 * @param urlStr the urlStr to set
	 */
	public void setUrlStr(String urlStr) {
		this.urlStr = urlStr;
	}
	/**
	 * @return the colorCode
	 */
	public String getColorCode() {
		return colorCode;
	}
	/**
	 * @param colorCode the colorCode to set
	 */
	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	public String getBannerPath() {
		return bannerPath;
	}
	public void setBannerPath(String bannerPath) {
		this.bannerPath = bannerPath;
	}
	/**
	 * @return the loyaltyType
	 */
	public String getLoyaltyType() {
		return loyaltyType;
	}
	/**
	 * @param loyaltyType the loyaltyType to set
	 */
	public void setLoyaltyType(String loyaltyType) {
		this.loyaltyType = loyaltyType;
	}
	public Long getOrgOwner() {
		return orgOwner;
	}
	public void setOrgOwner(Long orgOwner) {
		this.orgOwner = orgOwner;
	}
	public String getHomePageImagePath() {
		return homePageImagePath;
	}
	public void setHomePageImagePath(String homePageImagePath) {
		this.homePageImagePath = homePageImagePath;
	}
	public String getHomePageColorCode() {
		return homePageColorCode;
	}
	public void setHomePageColorCode(String homePageColorCode) {
		this.homePageColorCode = homePageColorCode;
	}
	public String getBannerName() {
		return bannerName;
	}
	public void setBannerName(String bannerName) {
		this.bannerName = bannerName;
	}
	public String getBalanceCardColorCode() {
		return balanceCardColorCode;
	}
	public void setBalanceCardColorCode(String balanceCardColorCode) {
		this.balanceCardColorCode = balanceCardColorCode;
	}
	public String getBalanceCardTextColorCode() {
		return balanceCardTextColorCode;
	}
	public void setBalanceCardTextColorCode(String balanceCardTextColorCode) {
		this.balanceCardTextColorCode = balanceCardTextColorCode;
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
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
}//EOF
