package com.optculture.shared.entities.communication.email;

import java.util.Calendar;

import com.optculture.shared.entities.org.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "external_SMTP_events")
public class ExternalSMTPEvents {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_id")
	private Long eventId;
	@Column(name = "event_source")
	private String eventSource;
	@Column(name = "event_type")
	private String eventType;
	@Column(name = "sent_id")
	private Long sentId;
	@Column(name = "user_id")
	private Long userId;
	@Column(name = "cr_id")
	private Long crId;
	@Column(name = "request_time")
	private Calendar requestTime;
	@Column(name = "email_id")
	private String emailId;
	@Column(name = "status_code")
	private String statusCode;
	@Column(name = "type")
	private String type;
	@Column(name = "reason")
	private String reason;

	//new ExternalSMTPEvents(serverName, eventType, userId, crId,
	//	LocalDateTime.now(), email);
	
//	ExternalSMTPEvents newEvent = new ExternalSMTPEvents(serverName, eventType,  userId, crId, Calendar.getInstance(), emailStr);

	public ExternalSMTPEvents(String eventSource, String eventType, Long user, Long crId,
			Calendar requestTime, String emailId) {

		this.eventSource = eventSource;
		this.eventType = eventType;
		this.userId = user;
		this.crId = crId;
		this.requestTime = requestTime;
		this.emailId = emailId;

	}

	

}
