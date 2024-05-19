package org.mq.optculture.model.loyalty;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlElement;

public class CustomerInfo {
	private String CUSTOMERID;
	private String CUSTOMERTYPE;
	private String FIRSTNAME;
	private String MIDDLENAME;
	private String LASTNAME;
	private String PHONE;
	private String PHONEPREF;
	private String EMAIL;
	private String EMAILPREF;
	private String ADDRESS1;
	private String ADDRESS2;
	private String CITY;
	private String STATE;
	private String POSTAL;
	private String COUNTRY;
	private String MAILPREF;
	private String BIRTHDAY;
	private String ANNIVERSARY;
	private String GENDER;
	private String CREATEDDATE; //  is created for setting created_date while implementing bulk enrollment utility
	public String getCREATEDDATE() {
		return CREATEDDATE;
	}
	public void setCREATEDDATE(String cREATEDDATE) {
		CREATEDDATE = cREATEDDATE;
	}
	public String getCUSTOMERID() {
		return CUSTOMERID;
	}
	@XmlElement(name = "CUSTOMERID")
	public void setCUSTOMERID(String cUSTOMERID) {
		CUSTOMERID = cUSTOMERID;
	}
	public String getCUSTOMERTYPE() {
		return CUSTOMERTYPE;
	}
	@XmlElement(name = "CUSTOMERTYPE")
	public void setCUSTOMERTYPE(String cUSTOMERTYPE) {
		CUSTOMERTYPE = cUSTOMERTYPE;
	}
	public String getFIRSTNAME() {
		return FIRSTNAME;
	}
	@XmlElement(name = "FIRSTNAME")
	public void setFIRSTNAME(String fIRSTNAME) {
		FIRSTNAME = fIRSTNAME;
	}
	public String getMIDDLENAME() {
		return MIDDLENAME;
	}
	@XmlElement(name = "MIDDLENAME")
	public void setMIDDLENAME(String mIDDLENAME) {
		MIDDLENAME = mIDDLENAME;
	}
	public String getLASTNAME() {
		return LASTNAME;
	}
	@XmlElement(name = "LASTNAME")
	public void setLASTNAME(String lASTNAME) {
		LASTNAME = lASTNAME;
	}
	public String getPHONE() {
		return PHONE;
	}
	@XmlElement(name = "PHONE")
	public void setPHONE(String pHONE) {
		PHONE = pHONE;
	}
	public String getPHONEPREF() {
		return PHONEPREF;
	}
	@XmlElement(name = "PHONEPREF")
	public void setPHONEPREF(String pHONEPREF) {
		PHONEPREF = pHONEPREF;
	}
	public String getEMAIL() {
		return EMAIL;
	}
	@XmlElement(name = "EMAIL")
	public void setEMAIL(String eMAIL) {
		EMAIL = eMAIL;
	}
	public String getEMAILPREF() {
		return EMAILPREF;
	}
	@XmlElement(name = "EMAILPREF")
	public void setEMAILPREF(String eMAILPREF) {
		EMAILPREF = eMAILPREF;
	}
	public String getADDRESS1() {
		return ADDRESS1;
	}
	@XmlElement(name = "ADDRESS1")
	public void setADDRESS1(String aDDRESS1) {
		ADDRESS1 = aDDRESS1;
	}
	public String getADDRESS2() {
		return ADDRESS2;
	}
	@XmlElement(name = "ADDRESS2")
	public void setADDRESS2(String aDDRESS2) {
		ADDRESS2 = aDDRESS2;
	}
	public String getCITY() {
		return CITY;
	}
	@XmlElement(name = "CITY")
	public void setCITY(String cITY) {
		CITY = cITY;
	}
	public String getSTATE() {
		return STATE;
	}
	@XmlElement(name = "STATE")
	public void setSTATE(String sTATE) {
		STATE = sTATE;
	}
	public String getPOSTAL() {
		return POSTAL;
	}
	@XmlElement(name = "POSTAL")
	public void setPOSTAL(String pOSTAL) {
		POSTAL = pOSTAL;
	}
	public String getCOUNTRY() {
		return COUNTRY;
	}
	@XmlElement(name = "COUNTRY")
	public void setCOUNTRY(String cOUNTRY) {
		COUNTRY = cOUNTRY;
	}
	public String getMAILPREF() {
		return MAILPREF;
	}
	@XmlElement(name = "MAILPREF")
	public void setMAILPREF(String mAILPREF) {
		MAILPREF = mAILPREF;
	}
	public String getBIRTHDAY() {
		return BIRTHDAY;
	}
	@XmlElement(name = "BIRTHDAY")
	public void setBIRTHDAY(String bIRTHDAY) {
		BIRTHDAY = bIRTHDAY;
	}
	public String getANNIVERSARY() {
		return ANNIVERSARY;
	}
	@XmlElement(name = "ANNIVERSARY")
	public void setANNIVERSARY(String aNNIVERSARY) {
		ANNIVERSARY = aNNIVERSARY;
	}
	public String getGENDER() {
		return GENDER;
	}
	@XmlElement(name = "GENDER")
	public void setGENDER(String gENDER) {
		GENDER = gENDER;
	}
}
