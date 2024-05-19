package org.mq.optculture.model.couponcodes;

import javax.xml.bind.annotation.XmlElement;

import org.mq.marketer.campaign.beans.Coupons;
import org.mq.optculture.model.BaseRequestObject;


public class IssueCouponRequest extends BaseRequestObject{
	
	private String id;
	private String datetime;
	private String livemode;
	private String type;
	private String coupon_code;
	private String session;
	private String campaign;
	private String coupon_url;
	private Coupon coupon;
	private Status status;
	private Customer customer;
	
	public IssueCouponRequest() {
	}

	public IssueCouponRequest(String id, String datetime, String livemode, String type, String coupon_code,
			String session, String campaign, String coupon_url, Coupon coupon, Status status, Customer customer) {
		super();
		this.id = id;
		this.datetime = datetime;
		this.livemode = livemode;
		this.type = type;
		this.coupon_code = coupon_code;
		this.session = session;
		this.campaign = campaign;
		this.coupon_url = coupon_url;
		this.coupon = coupon;
		this.status = status;
		this.customer = customer;
	}

	public String getId() {
		return id;
	}
	@XmlElement(name = "id")
	public void setId(String id) {
		this.id = id;
	}

	public String getDatetime() {
		return datetime;
	}
	@XmlElement(name = "datetime")
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getLivemode() {
		return livemode;
	}
	@XmlElement(name = "livemode")
	public void setLivemode(String livemode) {
		this.livemode = livemode;
	}

	public String getType() {
		return type;
	}
	@XmlElement(name = "type")
	public void setType(String type) {
		this.type = type;
	}

	public String getCoupon_code() {
		return coupon_code;
	}
	@XmlElement(name = "coupon_code")
	public void setCoupon_code(String coupon_code) {
		this.coupon_code = coupon_code;
	}

	public String getSession() {
		return session;
	}
	@XmlElement(name = "session")
	public void setSession(String session) {
		this.session = session;
	}

	public String getCampaign() {
		return campaign;
	}
	@XmlElement(name = "campaign")
	public void setCampaign(String campaign) {
		this.campaign = campaign;
	}

	public String getCoupon_url() {
		return coupon_url;
	}
	@XmlElement(name = "coupon_url")
	public void setCoupon_url(String coupon_url) {
		this.coupon_url = coupon_url;
	}

	public Coupon getCoupon() {
		return coupon;
	}
	@XmlElement(name = "coupon")
	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
	}

	public Status getStatus() {
		return status;
	}
	@XmlElement(name = "status")
	public void setStatus(Status status) {
		this.status = status;
	}

	public Customer getCustomer() {
		return customer;
	}
	@XmlElement(name = "customer")
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
}
