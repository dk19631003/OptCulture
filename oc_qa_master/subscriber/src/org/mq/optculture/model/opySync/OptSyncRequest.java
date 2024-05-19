package org.mq.optculture.model.opySync;


public class OptSyncRequest {
	private Head HEAD;
	private Body BODY;
	private UserDetails USERDETAILS;
	
	public Head getHEAD() {
		return HEAD;
	}
	public void setHEAD(Head hEAD) {
		HEAD = hEAD;
	}
	public Body getBODY() {
		return BODY;
	}
	public void setBODY(Body bODY) {
		BODY = bODY;
	}
	public UserDetails getUSERDETAILS() {
		return USERDETAILS;
	}
	public void setUSERDETAILS(UserDetails uSERDETAILS) {
		USERDETAILS = uSERDETAILS;
	}
	
	
	
}
