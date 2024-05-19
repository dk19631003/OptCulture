package org.mq.optculture.model.couponcodes;

import java.util.List;

public class ReferralCodeEnqResponse {

	private ReferralInfo REFERRALINFO;
	private StatusInfo STATUSINFO;

	public ReferralCodeEnqResponse(ReferralInfo rEFERRALINFO, StatusInfo sTATUSINFO) {
		super();
		REFERRALINFO = rEFERRALINFO;
		STATUSINFO = sTATUSINFO;
	}
	public ReferralInfo getREFERRALINFO() {
		return REFERRALINFO;
	}
	public void setREFERRALINFO(ReferralInfo rEFERRALINFO) {
		REFERRALINFO = rEFERRALINFO;
	}
	public StatusInfo getSTATUSINFO() {
		return STATUSINFO;
	}
	public void setSTATUSINFO(StatusInfo sTATUSINFO) {
		STATUSINFO = sTATUSINFO;
	}
}
