package com.optculture.shared.entities.communication;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "communication")
@Data
public class Communication {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "communication_id")
    private java.lang.Long communicationId;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "sender_id", length = 30)
    private String senderId;

    @Column(name = "channel_type", length = 30)
    private String channelType;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "template_id")
    private java.lang.Long templateId;

    @Column(name = "message_content", columnDefinition = "MEDIUMTEXT")
    private String messageContent;

    //we are not using lists so giving segment_id directly
    @Column(name = "segment_id")
    private String segmentId;

    @Column(name = "attribute", columnDefinition = "JSON")
    private String attributes;

    @Column(name = "media_url", length = 255)
    private String mediaUrl;

	@Column(name = "schedule_type",length = 50)
	private  String scheduleType;

	@Column(name = "frequency_type",length = 10)
	private  String frequencyType;

	@Column(name = "start_date")
	private  LocalDateTime startDate;

	@Column(name = "end_date")
	private  LocalDateTime endDate;

	@Column(name = "schedule_time")
	private LocalTime scheduleTime;

    @Column(name = "placeholder_mappings")
    private  String placeholderMappings;
    public String getMessageContent() {
        return this.messageContent == null ? "" : this.messageContent;
    }
    
    @Column(name = "json_content", columnDefinition = "JSON")
    private String jsonContent;


}
