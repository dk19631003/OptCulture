package org.mq.optculture.model.DR.prism;

import java.util.List;

import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.DR.DROptCultureDetails;

public class PrismBasedDRRequest extends BaseRequestObject {
	
	private PrismDRBody Body;
	private PrismDRHead Head;

	private String fileName;
	private DROptCultureDetails OptcultureDetails;

	
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

			
	
	public PrismDRBody getBody() {
		return Body;
	}
	
	public void setBody(PrismDRBody body) {
		Body = body;
	}

	public PrismDRHead getHead() {
		return Head;
	}

	public void setHead(PrismDRHead head) {
		Head = head;
	}

	public DROptCultureDetails getOptcultureDetails() {
		return OptcultureDetails;
	}

	public void setOptcultureDetails(DROptCultureDetails optcultureDetails) {
		OptcultureDetails = optcultureDetails;
	}
	
	
	
	
}
