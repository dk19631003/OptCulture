package org.mq.optculture.model.DR.heartland;

public class HeartlandDRBody {
	private HeartlandTicketDetails ticket;
	private HeartlandCustomerDetails customer;
	private HeartlandPaymentDetails payments;
	private HeartlandDetails details;
	public HeartlandTicketDetails getTicket() {
		return ticket;
	}
	public void setTicket(HeartlandTicketDetails ticket) {
		this.ticket = ticket;
	}
	public HeartlandCustomerDetails getCustomer() {
		return customer;
	}
	public void setCustomer(HeartlandCustomerDetails customer) {
		this.customer = customer;
	}
	public HeartlandPaymentDetails getPayments() {
		return payments;
	}
	public void setPayments(HeartlandPaymentDetails payments) {
		this.payments = payments;
	}
	public HeartlandDetails getDetails() {
		return details;
	}
	public void setDetails(HeartlandDetails details) {
		this.details = details;
	}
	
}