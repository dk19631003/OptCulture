package org.mq.optculture.model.campaign;

public class EmailPdfResponseObject {
	String pdfName;
	String pdfFilePath;
	String htmlFilePath;
	public String getPdfName() {
		return pdfName;
	}
	public void setPdfName(String pdfName) {
		this.pdfName = pdfName;
	}
	public String getPdfFilePath() {
		return pdfFilePath;
	}
	public void setPdfFilePath(String pdfFilePath) {
		this.pdfFilePath = pdfFilePath;
	}
	public String getHtmlFilePath() {
		return htmlFilePath;
	}
	public void setHtmlFilePath(String htmlFilePath) {
		this.htmlFilePath = htmlFilePath;
	}
	
}
