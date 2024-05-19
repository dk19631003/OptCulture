package org.mq.optculture.model;

import com.google.zxing.common.BitMatrix;

public class UrlShortCodeResponseObject extends BaseResponseObject{

	private String redirectTo;
	private BitMatrix bitMatrix;
	private String imageFormat;
	//private String ErrorResponse;
	
	public String getRedirectTo() {
		return redirectTo;
	}
	public void setRedirectTo(String redirectTo) {
		this.redirectTo = redirectTo;
	}
	public BitMatrix getBitMatrix() {
		return bitMatrix;
	}
	public void setBitMatrix(BitMatrix bitMatrix) {
		this.bitMatrix = bitMatrix;
	}
	public String getImageFormat() {
		return imageFormat;
	}
	public void setImageFormat(String imageFormat) {
		this.imageFormat = imageFormat;
	}
	/*public String getErrorResponse() {
		return ErrorResponse;
	}
	public void setErrorResponse(String errorResponse) {
		ErrorResponse = errorResponse;
	}*/
	
}
