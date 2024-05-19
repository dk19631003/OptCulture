package org.mq.optculture.model.genesys;

import java.util.List;

import org.mq.optculture.model.BaseResponseObject;

public class GetPageURLResponse extends BaseResponseObject {
	
	private String status;
	private List<Data> data;
	private String error;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<Data> getData() {
		return data;
	}
	public void setData(List<Data> data) {
		this.data = data;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	
	
}
