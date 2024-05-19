package  org.mq.captiway.scheduler.beans;
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
	
	
}//EOF
