package org.mq.optculture.model;

import java.util.List;

public class DLRRequests extends BaseRequestObject{
	
	private List<SynapseDLRRequestObject> dlrRequests;
	
	public List<SynapseDLRRequestObject> getDlrRequests() {
		return dlrRequests;
	}

	public void setDlrRequests(List<SynapseDLRRequestObject> dlrRequests) {
		this.dlrRequests = dlrRequests;
	}
}
