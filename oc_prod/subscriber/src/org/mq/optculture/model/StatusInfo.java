package org.mq.optculture.model;

public class StatusInfo {
	
	
		private String  errorCode;
		private String message;
		private String status;
		
		public StatusInfo(String errorCode, String message, String status) {
			this.errorCode = errorCode;
			this.message = message;
			this.status = status;
		}

		public String getErrorCode() {
			return errorCode;
		}
		public void setErrorCode(String errorCode) {
			this.errorCode = errorCode;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		
		
		
		//added for new json format, will be removed in future

	private String ERRORCODE;
	private String MESSAGE;
	private String STATUS;
	
	public StatusInfo() {
	}
	/*public StatusInfo(String eRRORCODE, String mESSAGE, String sTATUS) {
		ERRORCODE = eRRORCODE;
		MESSAGE = mESSAGE;
		STATUS = sTATUS;
	}*/
	public String getERRORCODE() {
		return ERRORCODE;
	}
	public void setERRORCODE(String eRRORCODE) {
		ERRORCODE = eRRORCODE;
	}
	public String getMESSAGE() {
		return MESSAGE;
	}
	public void setMESSAGE(String mESSAGE) {
		MESSAGE = mESSAGE;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	


}
