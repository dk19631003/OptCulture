package org.mq.marketer.campaign.beans;

import java.util.Calendar;
import java.util.List;
/**
 * 
 * @author Adarsh Kumar G
 * It handles data of Referral program settings.
 */
public class ReferralcodesRedeemed {

	private Long referralId;
	
	private Long referralcodeid;
	
	public Long getReferralcodeid() {
		return referralcodeid;
	}
	public void setReferralcodeid(Long referralcodeid) {
		this.referralcodeid = referralcodeid;
	}

	private String referredName;
	
	private String docSID;
	
	public String getDocSID() {
		return docSID;
	}
	public void setDocSID(String docSID) {
		this.docSID = docSID;
	}

	private String refcode;
	
	private String  refcodestatus;

	public String getRefcodestatus() {
		return refcodestatus;
	}
	public void setRefcodestatus(String refcodestatus) {
		this.refcodestatus = refcodestatus;
	}

	private Long referredcid;
	
	public Long getReferredcid() {
		return referredcid;
	}
	public void setReferredcid(Long referredcid) {
		this.referredcid = referredcid;
	}

	private Long refereecid;
	

	public Long getRefereecid() {
		return refereecid;
	}
	public void setRefereecid(Long refereecid) {
		this.refereecid = refereecid;
	}
	public String getRefcode() {
		return refcode;
	}
	public void setRefcode(String refcode) {
		this.refcode = refcode;
	}

	private Calendar redeemedDate;

	private Long userId;
	private Long  orgId;
	
	
	
	
	public Calendar getRedeemedDate() {
		return redeemedDate;
	}
	public void setRedeemedDate(Calendar redeemedDate) {
		this.redeemedDate = redeemedDate;
	}


	public String getReferredName() {
		return referredName;
	}
	public void setReferredName(String referredName) {
		this.referredName = referredName;
	}

	
	
	
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	
	public Long getReferralId() {
		return referralId;
	}
	public void setReferralId(Long referralId) {
		this.referralId = referralId;
	}

	
	private Double totRevenue;

	public Double getTotRevenue() {
		return totRevenue;
	}
	public void setTotRevenue(Double totRevenue) {
		this.totRevenue = totRevenue;
	}
	public ReferralcodesRedeemed() {
	
	}

	
	

	
}



