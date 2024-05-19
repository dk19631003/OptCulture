package org.mq.optculture.model.DR.heartland;

import java.util.ArrayList;

public class HeartlandDetails {
	private ArrayList<DetailResults> results;
    private String total;
    private String pages;
	public ArrayList<DetailResults> getResults() {
		return results;
	}
	public void setResults(ArrayList<DetailResults> results) {
		this.results = results;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getPages() {
		return pages;
	}
	public void setPages(String pages) {
		this.pages = pages;
	}

}
