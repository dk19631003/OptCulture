package org.mq.optculture.model.couponcodes;

import java.util.List;

import org.mq.optculture.model.loyalty.Balances;
import org.mq.optculture.model.loyalty.OTPRedeemLimit;

public class LoyaltyInfo {

	private String CUSTOMERID;
	private String CARDNUMBER;
    private String CARDPIN;
    private String CUSTOMERNAME;
    private String REDEEMABLEAMOUNT;
    private String POINTSEARNED;
    private String CURRENCYEARNED;
    private String CHECKREWARDS;
    private String POINTSREDEEMED;
    private String TIERNAME;

    
	public String getTIERNAME() {
		return TIERNAME;
	}

	public void setTIERNAME(String tIERNAME) {
		TIERNAME = tIERNAME;
	}

	public String getPOINTSREDEEMED() {
		return POINTSREDEEMED;
	}

	public void setPOINTSREDEEMED(String pOINTSREDEEMED) {
		POINTSREDEEMED = pOINTSREDEEMED;
	}

	public String getCHECKREWARDS() {
		return CHECKREWARDS;
	}

	public void setCHECKREWARDS(String cHECKREWARDS) {
		CHECKREWARDS = cHECKREWARDS;
	}
    
    public String getCURRENCYEARNED() {
		return CURRENCYEARNED;
	}
	public void setCURRENCYEARNED(String cURRENCYEARNED) {
		CURRENCYEARNED = cURRENCYEARNED;
	}
    private String OTPENABLED;
    private String LIFETIMEPOINTS;
    private List<Balances> BALANCES;
    private String ISSUANCEDESC;
    private String REDEEMDESC;
    private String EXCLREDMPNOTE;
    private String EMAIL;
    private String ALLOWREDEMPTION;
	
   	public String getALLOWREDEMPTION() {
   		return ALLOWREDEMPTION;
   	}
   	public void setALLOWREDEMPTION(String aLLOWREDEMPTION) {
   		ALLOWREDEMPTION = aLLOWREDEMPTION;
   	}

private List<String> EXPIRYINFO;
	
    public List<String> getEXPIRYINFO() {
		return EXPIRYINFO;
	}
	public void setEXPIRYINFO(List<String> eXPIRYINFO) {
		EXPIRYINFO = eXPIRYINFO;
	}
    public String getEMAIL() {
		return EMAIL;
	}
	public void setEMAIL(String eMAIL) {
		EMAIL = eMAIL;
	}
	public String getPHONE() {
		return PHONE;
	}
	public void setPHONE(String pHONE) {
		PHONE = pHONE;
	}
	private String PHONE;
   /* Earn 1 point for every $ spent
    Earn 2 points for every $10 spent
    Earn 10 points for every AED 10 spent
    Get AED 1 cashback for every AED 10 
    Get AED 1 cashback for every AED 10   

    RedeemDesc
    100 points get you $10 
    (blank if cashback)  */
	
	public String getEXCLREDMPNOTE() {
		return EXCLREDMPNOTE;
	}
	public void setEXCLREDMPNOTE(String eXCLREDMPNOTE) {
		EXCLREDMPNOTE = eXCLREDMPNOTE;
	}
	private List<OTPRedeemLimit> OTPREDEEMLIMIT;
	private String DISPLAYTEMPLATE;
	
	
	
	
    public String getCUSTOMERID() {
		return CUSTOMERID;
	}
	public void setCUSTOMERID(String cUSTOMERID) {
		CUSTOMERID = cUSTOMERID;
	}
	public String getCARDNUMBER() {
		return CARDNUMBER;
	}
	public void setCARDNUMBER(String cARDNUMBER) {
		CARDNUMBER = cARDNUMBER;
	}
	public String getCARDPIN() {
		return CARDPIN;
	}
	public void setCARDPIN(String cARDPIN) {
		CARDPIN = cARDPIN;
	}
	public String getREDEEMABLEAMOUNT() {
		return REDEEMABLEAMOUNT;
	}
	public void setREDEEMABLEAMOUNT(String rEDEEMABLEAMOUNT) {
		REDEEMABLEAMOUNT = rEDEEMABLEAMOUNT;
	}
	public String getOTPENABLED() {
		return OTPENABLED;
	}
	public void setOTPENABLED(String oTPENABLED) {
		OTPENABLED = oTPENABLED;
	}
	public List<OTPRedeemLimit> getOTPREDEEMLIMIT() {
		return OTPREDEEMLIMIT;
	}
	public void setOTPREDEEMLIMIT(List<OTPRedeemLimit> oTPREDEEMLIMIT) {
		OTPREDEEMLIMIT = oTPREDEEMLIMIT;
	}
	
	public List<Balances> getBALANCES() {
		return BALANCES;
	}
	public void setBALANCES(List<Balances> bALANCES) {
		BALANCES = bALANCES;
	}
	public String getDISPLAYTEMPLATE() {
		return DISPLAYTEMPLATE;
	}
	public void setDISPLAYTEMPLATE(String dISPLAYTEMPLATE) {
		DISPLAYTEMPLATE = dISPLAYTEMPLATE;
	}
	public String getLIFETIMEPOINTS() {
		return LIFETIMEPOINTS;
	}
	public void setLIFETIMEPOINTS(String lIFETIMEPOINTS) {
		LIFETIMEPOINTS = lIFETIMEPOINTS;
	}
	public String getPOINTSEARNED() {
		return POINTSEARNED;
	}
	public void setPOINTSEARNED(String pOINTSEARNED) {
		POINTSEARNED = pOINTSEARNED;
	}
	public String getISSUANCEDESC() {
		return ISSUANCEDESC;
	}
	public void setISSUANCEDESC(String iSSUANCEDESC) {
		ISSUANCEDESC = iSSUANCEDESC;
	}
	public String getREDEEMDESC() {
		return REDEEMDESC;
	}
	
	public void setREDEEMDESC(String rEDEEMDESC) {
		REDEEMDESC = rEDEEMDESC;
	}
	public String getCUSTOMERNAME() {
		return CUSTOMERNAME;
	}
	public void setCUSTOMERNAME(String cUSTOMERNAME) {
		CUSTOMERNAME = cUSTOMERNAME;
	}

}
