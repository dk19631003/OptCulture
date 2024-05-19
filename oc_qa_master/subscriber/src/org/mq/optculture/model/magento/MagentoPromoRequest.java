/**
 * 
 */
package org.mq.optculture.model.magento;

import java.util.List;

import org.mq.optculture.model.BaseRequestObject;


/**
 * @author abheeshna.nalla
 *
 */

public class MagentoPromoRequest extends BaseRequestObject{
	
	private CouponCodeInfo couponCodeInfo;
	private HeaderInfo headerInfo;
	private List<PurchasedItems> purchasedItems;
	private UserDetails userDetails;
	
	
	public MagentoPromoRequest(CouponCodeInfo couponCodeInfo,
			HeaderInfo headerInfo, List<PurchasedItems> purchasedItems,
			UserDetails userDetails) {
		this.couponCodeInfo = couponCodeInfo;
		this.headerInfo = headerInfo;
		this.purchasedItems = purchasedItems;
		this.userDetails = userDetails;
	}
	public UserDetails getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(UserDetails userDetails) {
		this.userDetails = userDetails;
	}
	public HeaderInfo getHeaderInfo() {
		return headerInfo;
	}
	public void setHeaderInfo(HeaderInfo headerInfo) {
		this.headerInfo = headerInfo;
	}
	public List<PurchasedItems> getPurchasedItems() {
		return purchasedItems;
	}
	public void setPurchasedItems(List<PurchasedItems> purchasedItems) {
		this.purchasedItems = purchasedItems;
	}
	public CouponCodeInfo getCouponCodeInfo() {
		return couponCodeInfo;
	}
	public void setCouponCodeInfo(CouponCodeInfo couponCodeInfo) {
		this.couponCodeInfo = couponCodeInfo;
	}
	

}
