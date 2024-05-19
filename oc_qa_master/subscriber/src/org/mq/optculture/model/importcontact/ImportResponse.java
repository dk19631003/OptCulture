package org.mq.optculture.model.importcontact;

import java.util.List;

import org.mq.optculture.model.BaseResponseObject;

public class ImportResponse extends BaseResponseObject {

	private Header header;
	private List<Customer> matchedCustomers;
	private Report report;
	private Status status;

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}


	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<Customer> getMatchedCustomers() {
		return matchedCustomers;
	}

	public void setMatchedCustomers(List<Customer> matchedCustomers) {
		this.matchedCustomers = matchedCustomers;
	}

}
