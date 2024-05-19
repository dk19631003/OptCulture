package org.mq.optculture.model.ocloyalty;

import javax.xml.bind.annotation.XmlElement;

public class RequestHeader {

	private String requestId;
	private String requestType;
	private String requestDate;
	private String pcFlag;
	private String subsidiaryNumber;
	private String storeNumber;
	private String employeeId;
	private String terminalId;
    private String docSID;
    private String receiptNumber;
   
	private String sourceType;
	
	public String getSourceType() {
		return sourceType;
	}

	@XmlElement(name = "sourceType")
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}


	public RequestHeader() {
	}

	
	public RequestHeader(String requestId, String requestDate, String pcFlag, String subsidiaryNumber,
			String storeNumber, String employeeId, String terminalId, String docSID, String sourceType, String receiptNumber) {
		
		this.requestId = requestId;
		this.requestDate = requestDate;
		this.pcFlag = pcFlag;
		this.subsidiaryNumber = subsidiaryNumber;
		this.storeNumber =storeNumber;
		this.employeeId = employeeId;
		this.terminalId = terminalId;
		this.docSID = docSID;
		this.sourceType = sourceType;
		this.receiptNumber = receiptNumber;
		
	}
	
	public String getRequestId() {
		return requestId;
	}
	@XmlElement(name = "requestId")
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getPcFlag() {
		return pcFlag;
	}
	@XmlElement(name = "pcFlag")
	public void setPcFlag(String pcFlag) {
		this.pcFlag = pcFlag;
	}
	
	public String getSubsidiaryNumber() {
		return subsidiaryNumber;
	}
	@XmlElement(name = "subsidiaryNumber")
	public void setSubsidiaryNumber(String subsidiaryNumber) {
		this.subsidiaryNumber = subsidiaryNumber;
	}

	public String getStoreNumber() {
		return storeNumber;
	}
	@XmlElement(name = "storeNumber")
	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}

	public String getEmployeeId() {
		return employeeId;
	}
	@XmlElement(name = "employeeId")
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public String getTerminalId() {
		return terminalId;
	}
	@XmlElement(name = "terminalId")
	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}
	
	public String getRequestDate() {
		return requestDate;
	}
	@XmlElement(name = "requestDate")
	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
	}
	
	

	public String getDocSID() {
		return docSID;
	}
	@XmlElement(name = "docSID")
	public void setDocSID(String docSID) {
		this.docSID = docSID;
	}
	
	 public String getReceiptNumber() {
		return receiptNumber;
	}
	 @XmlElement(name = "receiptNumber")
	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}

	public String getRequestType() {
		return requestType;
	}

	@XmlElement(name = "requestType")
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

}
