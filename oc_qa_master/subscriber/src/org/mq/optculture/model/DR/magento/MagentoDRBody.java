package org.mq.optculture.model.DR.magento;

import java.util.List;

public class MagentoDRBody {
	private MagentoOrderDetails orderdetails;
	private List<MagentoOrderItems> orderitems;
	private MagentoCustomerDetails customerdetails;
	//private String Membership;
	public MagentoOrderDetails getOrderdetails() {
		return orderdetails;
	}
	public void setOrderdetails(MagentoOrderDetails orderdetails) {
		this.orderdetails = orderdetails;
	}
	public List<MagentoOrderItems> getOrderitems() {
		return orderitems;
	}
	public void setOrderitems(List<MagentoOrderItems> orderitems) {
		this.orderitems = orderitems;
	}
	public MagentoCustomerDetails getCustomerdetails() {
		return customerdetails;
	}
	public void setCustomerdetails(MagentoCustomerDetails customerdetails) {
		this.customerdetails = customerdetails;
	}
	
}
