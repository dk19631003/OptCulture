package org.mq.optculture.model.beefileapi;

public class FininalResponse {

	private String status;
	private DataPojo data;

	public DataPojo getData() {
		return data;
	}

	public void setData(DataPojo data) {
		this.data = data;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
