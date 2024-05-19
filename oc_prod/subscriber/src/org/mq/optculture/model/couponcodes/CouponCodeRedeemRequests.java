package org.mq.optculture.model.couponcodes;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="COUPONCODEREDEEM_REQUESTS")
public class CouponCodeRedeemRequests {
	
//	private List<CouponCodeRedeemReq> couponCodeReqList = new ArrayList<CouponCodeRedeemReq>();
	
	
	private List<CouponCodeRedeemReq> couponCodeRedeemReqList;
	public CouponCodeRedeemRequests(){}

	public List<CouponCodeRedeemReq> getCouponCodeRedeemReqList() {
		return couponCodeRedeemReqList;
	}
	
	@XmlElement(name="COUPONCODEREDEEMREQ")
	public void setCouponCodeRedeemReqList(List<CouponCodeRedeemReq> couponCodeRedeemReqList) {
		this.couponCodeRedeemReqList = couponCodeRedeemReqList;
	}

	
	
	
	
}
