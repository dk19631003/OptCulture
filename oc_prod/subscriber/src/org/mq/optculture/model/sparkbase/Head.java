package org.mq.optculture.model.sparkbase;

public class Head {

	private String responseCode;
	private String responseMessage;
	private String responseStatus;
	
	public Head() {
	}
	public Head(String rESPONSECODE, String rESPONSEMESSAGE, String rESPONSESTATUS) {
		responseCode = rESPONSECODE;
		responseMessage = rESPONSEMESSAGE;
		responseStatus = rESPONSESTATUS;
	}
	public String getRESPONSECODE() {
		return responseCode;
	}
	public void setRESPONSECODE(String rESPONSECODE) {
		responseCode = rESPONSECODE;
	}
	public String getRESPONSEMESSAGE() {
		return responseMessage;
	}
	public void setRESPONSEMESSAGE(String rESPONSEMESSAGE) {
		responseMessage = rESPONSEMESSAGE;
	}
	public String getRESPONSESTATUS() {
		return responseStatus;
	}
	public void setRESPONSESTATUS(String rESPONSESTATUS) {
		responseStatus = rESPONSESTATUS;
	}
	
}
