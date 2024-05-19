package org.mq.optculture.model.ocloyalty;

import javax.xml.bind.annotation.XmlElement;

public class LoyaltyTransferMembershipJsonRequest {


	private RequestHeader header;
	private MembershipRequest membership;
	private TransferSource transferSource;
	
	private LoyaltyUser user;
	
	public LoyaltyTransferMembershipJsonRequest() {
	}

	public RequestHeader getHeader() {
		return header;
	}
	
	public void setHeader(RequestHeader header) {
		this.header = header;
	}
		
	

	public LoyaltyUser getUser() {
		return user;
	}
	public void setUser(LoyaltyUser user) {
		this.user = user;
	}

	public MembershipRequest getMembership() {
		return membership;
	}
	public void setMembership(MembershipRequest membership) {
		this.membership = membership;
	}
	
	public TransferSource getTransferSource() {
		return transferSource;
	}
	public void setTransferSource(TransferSource transferSource) {
		this.transferSource = transferSource;
	}
	
	
	
}
