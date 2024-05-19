
package org.mq.marketer.campaign.beans;

import java.util.Calendar;
import java.util.List;
/**
 * 
 * @author Adarsh Kumar G
 * It handles data of Referral program settings.
 */
public class RewardReferraltype {

	
	private String rewardonReferraltype;
	private String rewardonReferralVC;
	private String rewardonReferralValue;
	private String  rewardonReferralRepeats;
	
	
	/*public int getRewardonReferralRepeats() {
		return rewardonReferralRepeats;
	}
	public void setRewardonReferralRepeats(int rewardonReferralRepeats) {
		this.rewardonReferralRepeats = rewardonReferralRepeats;
	}*/


	public String getRewardonReferralRepeats() {
		return rewardonReferralRepeats;
	}
	public void setRewardonReferralRepeats(String rewardonReferralRepeats) {
		this.rewardonReferralRepeats = rewardonReferralRepeats;
	}


	private Long refId;
	private Long referralid;
	private ReferralProgram referralprogram;
	private Long milestoneLevel;
	


	
	
	
	public Long getMilestoneLevel() {
		return milestoneLevel;
	}
	public void setMilestoneLevel(Long milestoneLevel) {
		this.milestoneLevel = milestoneLevel;
	}
	public ReferralProgram getReferralprogram() {
		return referralprogram;
	}
	public void setReferralprogram(ReferralProgram referralprogram) {
		this.referralprogram = referralprogram;
	}
	public Long getReferralid() {
		return referralid;
	}
	public void setReferralid(Long referralid) {
		this.referralid = referralid;
	}
	public String getRewardonReferraltype() {
		return rewardonReferraltype;
	}
	public void setRewardonReferraltype(String rewardonReferraltype) {
		this.rewardonReferraltype = rewardonReferraltype;
	}

		
	public Long getRefId() {
		return refId;
	}
	public void setRefId(Long refId) {
		this.refId = refId;
	}
	public String getRewardonReferralVC() {
		return rewardonReferralVC;
	}
	public void setRewardonReferralVC(String rewardonReferralVC) {
		this.rewardonReferralVC = rewardonReferralVC;
	}
	public String getRewardonReferralValue() {
		return rewardonReferralValue;
	}
	public void setRewardonReferralValue(String rewardonReferralValue) {
		this.rewardonReferralValue = rewardonReferralValue;
	}
	

	public RewardReferraltype() {
	
	}

	
	

	
}



