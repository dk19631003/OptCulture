package org.mq.optculture.model.ocloyalty;

import java.util.List;

import org.mq.optculture.model.BaseResponseObject;

public class LoyaltyDataResponse extends BaseResponseObject {
	
	private static final long serialVersionUID = -5244271853400515308L;
	
	private ResponseHeader header;
	private List<MatchedCustomerReport> matchedCustomers;
	private LoyaltyReportResponse report;
	private Status status;
	
	public ResponseHeader getHeader() {
		return header;
	}
	public void setHeader(ResponseHeader header) {
		this.header = header;
	}
	public List<MatchedCustomerReport> getMatchedCustomers() {
		return matchedCustomers;
	}
	public void setMatchedCustomers(List<MatchedCustomerReport> matchedCustomers) {
		this.matchedCustomers = matchedCustomers;
	}
	public LoyaltyReportResponse getReport() {
		return report;
	}
	public void setReport(LoyaltyReportResponse report) {
		this.report = report;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	
}
