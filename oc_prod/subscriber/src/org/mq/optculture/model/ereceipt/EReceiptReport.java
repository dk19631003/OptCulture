package org.mq.optculture.model.ereceipt;

import java.util.List;

public class EReceiptReport {

	private List<EReceipt> transactions;
	private Report report;
	private EReceiptStatus status;
	
	public List<EReceipt> getTransactions() {
		return transactions;
	}
	public void setTransactions(List<EReceipt> transactions) {
		this.transactions = transactions;
	}
	public Report getReport() {
		return report;
	}
	public void setReport(Report report) {
		this.report = report;
	}
	public EReceiptStatus getStatus() {
		return status;
	}
	public void setStatus(EReceiptStatus status) {
		this.status = status;
	}
	
}
