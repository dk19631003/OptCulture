package org.mq.optculture.model.beeContentApi;

public class HtmlToPdf {
	private String  html;
	private String file_type;
	private String page_size;
	private String page_orientation;
	
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
	public String getFile_type() {
		return file_type;
	}
	public void setFile_type(String file_type) {
		this.file_type = file_type;
	}
	public String getPage_size() {
		return page_size;
	}
	public void setPage_size(String page_size) {
		this.page_size = page_size;
	}
	public String getPage_orientation() {
		return page_orientation;
	}
	public void setPage_orientation(String page_orientation) {
		this.page_orientation = page_orientation;
	}

}


/*
"html":"a","file_type":"pdf","page_size":"full","page_orientation":"portrait"}
*/