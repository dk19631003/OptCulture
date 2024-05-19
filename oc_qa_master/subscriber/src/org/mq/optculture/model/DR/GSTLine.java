package org.mq.optculture.model.DR;

public class GSTLine {
	public GSTLine(){}
	public GSTLine(String taxableAmt, String taxDescription, String iGSTRate, String iGSTAmt, String cGSTRate,
			String cGSTAmt, String sGSTRate, String sGSTAmt, String cESSRate, String cESSAmt) {
		super();
		TaxableAmt = taxableAmt;
		TaxDescription = taxDescription;
		IGSTRate = iGSTRate;
		IGSTAmt = iGSTAmt;
		CGSTRate = cGSTRate;
		CGSTAmt = cGSTAmt;
		SGSTRate = sGSTRate;
		SGSTAmt = sGSTAmt;
		CESSRate = cESSRate;
		CESSAmt = cESSAmt;
	}
	private String TotalTaxableAmt;
	public String getTotalTaxableAmt() {
		return TotalTaxableAmt;
	}
	public void setTotalTaxableAmt(String totalTaxableAmt) {
		TotalTaxableAmt = totalTaxableAmt;
	}
	public String getTotalCESSAmt() {
		return TotalCESSAmt;
	}
	public void setTotalCESSAmt(String totalCESSAmt) {
		TotalCESSAmt = totalCESSAmt;
	}
	public String getTotalCGSTAmt() {
		return TotalCGSTAmt;
	}
	public void setTotalCGSTAmt(String totalCGSTAmt) {
		TotalCGSTAmt = totalCGSTAmt;
	}
	public String getTotalSGSTAmt() {
		return TotalSGSTAmt;
	}
	public void setTotalSGSTAmt(String totalSGSTAmt) {
		TotalSGSTAmt = totalSGSTAmt;
	}
	public String getTotalIGSTAmt() {
		return TotalIGSTAmt;
	}
	public void setTotalIGSTAmt(String totalIGSTAmt) {
		TotalIGSTAmt = totalIGSTAmt;
	}
	private String TotalCESSAmt;
	private String TotalCGSTAmt;
	private String TotalSGSTAmt;
	private String TotalIGSTAmt;
	private String TaxableAmt;
	public String getTaxableAmt() {
		return TaxableAmt;
	}
	public void setTaxableAmt(String taxableAmt) {
		TaxableAmt = taxableAmt;
	}
	public String getTaxDescription() {
		return TaxDescription;
	}
	public void setTaxDescription(String taxDescription) {
		TaxDescription = taxDescription;
	}
	public String getIGSTRate() {
		return IGSTRate;
	}
	public void setIGSTRate(String iGSTRate) {
		IGSTRate = iGSTRate;
	}
	public String getIGSTAmt() {
		return IGSTAmt;
	}
	public void setIGSTAmt(String iGSTAmt) {
		IGSTAmt = iGSTAmt;
	}
	public String getCGSTRate() {
		return CGSTRate;
	}
	public void setCGSTRate(String cGSTRate) {
		CGSTRate = cGSTRate;
	}
	public String getCGSTAmt() {
		return CGSTAmt;
	}
	public void setCGSTAmt(String cGSTAmt) {
		CGSTAmt = cGSTAmt;
	}
	public String getSGSTRate() {
		return SGSTRate;
	}
	public void setSGSTRate(String sGSTRate) {
		SGSTRate = sGSTRate;
	}
	public String getSGSTAmt() {
		return SGSTAmt;
	}
	public void setSGSTAmt(String sGSTAmt) {
		SGSTAmt = sGSTAmt;
	}
	public String getCESSRate() {
		return CESSRate;
	}
	public void setCESSRate(String cESSRate) {
		CESSRate = cESSRate;
	}
	public String getCESSAmt() {
		return CESSAmt;
	}
	public void setCESSAmt(String cESSAmt) {
		CESSAmt = cESSAmt;
	}
	private String TaxDescription;
	private String IGSTRate; 
	private String IGSTAmt; 
	private String CGSTRate; 
	private String CGSTAmt; 
	private String SGSTRate; 
	private String SGSTAmt; 
	private String CESSRate; 
	private String CESSAmt;
}
