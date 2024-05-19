package org.mq.optculture.model.digitalReceipt;

public class SendDRResponseInfo {
	
	private SendDRStatus STATUS;
	private String DISPLAYTEMPLATE;
	private String CARDNUMBER;
	private String POINTSBALANCE;
	private String CURRENCYBALANCE; //reddemableCurr  //sivaram
	private String CHECKREWARD;

	
	public String getCHECKREWARD() {
		return CHECKREWARD;
	}

	public void setCHECKREWARD(String cHECKREWARD) {
		CHECKREWARD = cHECKREWARD;
	}

	public String getPOINTSBALANCE() {
		return POINTSBALANCE;
	}

	public void setPOINTSBALANCE(String pOINTSBALANCE) {
		POINTSBALANCE = pOINTSBALANCE;
	}

	public String getCURRENCYBALANCE() {
		return CURRENCYBALANCE;
	}

	public void setCURRENCYBALANCE(String cURRENCYBALANCE) {
		CURRENCYBALANCE = cURRENCYBALANCE;
	}

	public String getDISPLAYTEMPLATE() {
		return DISPLAYTEMPLATE;
	}

	public void setDISPLAYTEMPLATE(String dISPLAYTEMPLATE) {
		DISPLAYTEMPLATE = dISPLAYTEMPLATE;
	}

	public SendDRResponseInfo(SendDRStatus status) {
		this.STATUS = status;
	}

	public SendDRStatus getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(SendDRStatus sTATUS) {
		STATUS = sTATUS;
	}

	public String getCARDNUMBER() {
		return CARDNUMBER;
	}

	public void setCARDNUMBER(String cARDNUMBER) {
		CARDNUMBER = cARDNUMBER;
	}

	
	
}
