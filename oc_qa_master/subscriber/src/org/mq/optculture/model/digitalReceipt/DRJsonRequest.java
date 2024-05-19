package org.mq.optculture.model.digitalReceipt;

public class DRJsonRequest {

	private DRHead Head;
	//private DRBody Body;
	private DigitalReceiptBody Body;
	
	public DRHead getHead() {
		return Head;
	}
	public void setHead(DRHead head) {
		Head = head;
	}
	/*public DRBody getBody() {
		return Body;
	}
	public void setBody(DRBody body) {
		Body = body;
	}*/
	public DigitalReceiptBody getBody() {
		return Body;
	}
	public void setBody(DigitalReceiptBody body) {
		Body = body;
	}
	
}
