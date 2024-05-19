package com.optculture.shared.entities.communication;

import java.time.LocalDateTime;
import java.util.Calendar;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "communication_report")
@Data
public class CommunicationReport {

  
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cr_id")
    private java.lang.Long crId;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    @Column(name = "content",columnDefinition="MEDIUMTEXT")
    private String content;

    @Column(name = "configured")
    private Long configured;

    @Column(name = "suppressed_count")
    private Long suppressedCount;

    @Column(name = "sent")
    private Long sent;

    @Column(name = "opens")
    private Integer opens;

    @Column(name = "clicks")
    private Integer clicks;

    @Column(name = "unsubscribes")
    private Integer unsubscribes;

    @Column(name = "bounces")
    private Integer bounces;
    //added for preference_count & spams in reports
    @Column(name= "preference_count",columnDefinition="Integer default 0")
    private Integer preferenceCount;
    
    @Column(name = "spams")
    private Integer spams;

    @Column(name = "status", length = 100)
    private String status;

    @Column(name = "source_type")
    private String sourceType;

    @Column(name = "credits_count")
    private Integer creditsCount;

    @Column(name = "user_id")
    private java.lang.Long userId;
    
    @Column(name="placeholder_used",columnDefinition="MEDIUMTEXT")
    private String placeHolders;
    
    @Column(name = "segments_used")
    private String segmentId;
    
    //required for creating object for commReport in submitter
    public CommunicationReport(Long user,String campaignOrTriggerName,String content,LocalDateTime sentDate, Long sent, Integer opens, Integer clicks, Integer unsubscribes,
			Integer bounces,Integer spams,String status,String sourceType) { 		
	    this.userId = user;
		this.name = campaignOrTriggerName;
	    this.content = content;
	    this.sent = sent;
	    this.sentDate = sentDate;
	    this.opens = opens;
	    this.clicks = clicks;
	    this.unsubscribes = unsubscribes;
	    this.bounces = bounces;
	    this.spams = spams;
	    this.status = status;
	    this.sourceType = sourceType;
	}

}
