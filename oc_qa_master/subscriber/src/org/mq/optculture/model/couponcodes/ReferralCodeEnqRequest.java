package org.mq.optculture.model.couponcodes;

public class ReferralCodeEnqRequest {

	private ReferralCodeInfo REFERRALCODEINFO;
	private UserDetails USERDETAILS;


	public ReferralCodeEnqRequest(ReferralCodeInfo rEFERRALCODEINFO, UserDetails uSERDETAILS) {
		super();
		REFERRALCODEINFO = rEFERRALCODEINFO;
		USERDETAILS = uSERDETAILS;
	}

	public ReferralCodeInfo getREFERRALCODEINFO() {
		return REFERRALCODEINFO;
	}

	public void setREFERRALCODEINFO(ReferralCodeInfo rEFERRALCODEINFO) {
		REFERRALCODEINFO = rEFERRALCODEINFO;
	}

	public UserDetails getUSERDETAILS() {
		return USERDETAILS;
	}

	public void setUSERDETAILS(UserDetails uSERDETAILS) {
		USERDETAILS = uSERDETAILS;
	}


	
}
