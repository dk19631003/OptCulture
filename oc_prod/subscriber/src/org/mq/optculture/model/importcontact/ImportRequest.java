package org.mq.optculture.model.importcontact;

import org.mq.optculture.model.BaseRequestObject;

public class ImportRequest extends BaseRequestObject {

	private Header header;
	private String importType;
	private Lookup lookup;
	private Report report;
	private User user;

	public String getImportType() {
		return importType;
	}

	public void setImportType(String importType) {
		this.importType = importType;
	}

	public Lookup getLookup() {
		return lookup;
	}

	public void setLookup(Lookup lookup) {
		this.lookup = lookup;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

}
