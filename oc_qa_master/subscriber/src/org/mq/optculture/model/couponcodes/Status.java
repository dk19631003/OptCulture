package org.mq.optculture.model.couponcodes;
import javax.xml.bind.annotation.XmlElement;
public class Status {

	private String social_login;
	private String scratched;
	private String scratched_date;
	private String claimed;
	private String claimed_date;
	private String claimed_type;
	private String validated;
	private String validated_date;
	
	public String getSocial_login() {
		return social_login;
	}
	@XmlElement(name ="social_login")
	public void setSocial_login(String social_login) {
		this.social_login = social_login;
	}
	public String getScratched() {
		return scratched;
	}
	@XmlElement(name ="scratched")
	public void setScratched(String scratched) {
		this.scratched = scratched;
	}
	public String getScratched_date() {
		return scratched_date;
	}
	@XmlElement(name ="scratched_date")
	public void setScratched_date(String scratched_date) {
		this.scratched_date = scratched_date;
	}
	public String getClaimed() {
		return claimed;
	}
	@XmlElement(name ="claimed")
	public void setClaimed(String claimed) {
		this.claimed = claimed;
	}
	public String getClaimed_date() {
		return claimed_date;
	}
	@XmlElement(name ="claimed_date")
	public void setClaimed_date(String claimed_date) {
		this.claimed_date = claimed_date;
	}
	public String getClaimed_type() {
		return claimed_type;
	}
	@XmlElement(name ="claimed_type")
	public void setClaimed_type(String claimed_type) {
		this.claimed_type = claimed_type;
	}
	public String getValidated() {
		return validated;
	}
	@XmlElement(name ="validated")
	public void setValidated(String validated) {
		this.validated = validated;
	}
	public String getValidated_date() {
		return validated_date;
	}
	@XmlElement(name ="validated_date")
	public void setValidated_date(String validated_date) {
		this.validated_date = validated_date;
	}
	
	
}
