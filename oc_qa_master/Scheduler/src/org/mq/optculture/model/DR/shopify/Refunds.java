package org.mq.optculture.model.DR.shopify;

import java.util.List;

public class Refunds {
	private List<RefundItems> refund_line_items;

	public List<RefundItems> getRefund_line_items() {
		return refund_line_items;
	}

	public void setRefund_line_items(List<RefundItems> refund_line_items) {
		this.refund_line_items = refund_line_items;
	}
}
