package org.mq.optculture.model.mobileapp;

import java.util.List;

import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.ocloyalty.Status;

public class PurchaseHistoryResponse extends BaseResponseObject{

	private ResponseHeader header;
	private List<MatchedCustomers> matchedCustomers;
	//private ResponseReport report;
	private Status status;
	
	public ResponseHeader getHeader() {
		return header;
	}
	public void setHeader(ResponseHeader header) {
		this.header = header;
	}
	public List<MatchedCustomers> getMatchedCustomers() {
		return matchedCustomers;
	}
	public void setMatchedCustomers(List<MatchedCustomers> matchedCustomers) {
		this.matchedCustomers = matchedCustomers;
	}
	/*public ResponseReport getReport() {
		return report;
	}
	public void setReport(ResponseReport report) {
		this.report = report;
	}*/
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	
	
	
}
