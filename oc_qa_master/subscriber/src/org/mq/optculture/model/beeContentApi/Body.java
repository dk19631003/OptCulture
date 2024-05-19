package org.mq.optculture.model.beeContentApi;

public class Body {

	private String page_size;
	private String page_orientation;
	private String content_type;
	private String url;
	private String filename;
	
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
	public String getContent_type() {
		return content_type;
	}
	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
}


/*
{"statusCode":200,"body":{"page_size":"Full","page_orientation":"portrait",
"content_type":"application/pdf",
"url":"https://pro-bee-beepro-pdf.s3.amazonaws.com/public/pdf/A3clJLncJB.pdf","filename":"A3clJLncJB.pdf"}}
*/