package com.optculture.shared.entities.communication;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "communication_templates",uniqueConstraints = {@UniqueConstraint(columnNames = {"sender_id", "template_registered_id"})})
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommunicationTemplate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private java.lang.Long id;

	@Column(name = "template_name", length = 50)
	private String templateName;

	@Column(name = "channel_type", length = 50)
	private String channelType;//email/sms/wa

	@Column(name = "msg_content",columnDefinition = "MEDIUMTEXT")
	private String msgContent;

	@Column(name = "json_content",columnDefinition = "JSONTEXT")
	private String jsonContent;

	@Column(name = "provider", length = 50)
	private String provider;

	@Column(name = "sender_id", length = 50)
	private String senderId;

	@Column(name = "header_text", length = 250)
	private String headerText;

	@Column(name = "msg_type", length = 50)
	private String msgType;

	@Column(name = "footer", length = 250)
	private String footer;

	@Column(name = "template_type", length = 50)
	private String templateType;
	
	@Column(name = "predefined_template_type", length = 50)
	private String preDefinedTemplateType;//Carousel, Limited Time Offer, Copy Coupon Code - Newly Added

	@Column(name = "template_registered_id", length = 50)
	private String templateRegisteredId;

	@Column(name = "status", length = 50)
	private String status;

	@Column(name = "user_id")
	private java.lang.Long userId;

	@Column(name = "org_id")
	private java.lang.Long orgId;

	@Column(name = "folder_name", length = 50)
	private String folderName;

	@Column(name = "parent_dir", length = 50)
	private String parentDir;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "modified_date")
	private LocalDateTime modifiedDate;

	@Column(name = "created_by")
	private java.lang.Long createdBy;

	@Column(name = "modified_by")
	private java.lang.Long modifiedBy;

}
