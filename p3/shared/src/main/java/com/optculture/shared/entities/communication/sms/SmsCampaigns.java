package com.optculture.shared.entities.communication.sms;

import jakarta.persistence.*;
@Entity
@Table(name = "Sms_campaigns")
public class SmsCampaigns {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sms_campaign_id")
    private java.lang.Long smsCampaignId;

    @Column(name = "sms_campaign_name", length = 100)
    private String smsCampaignName;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "draft_status", length = 30)
    private String draftStatus;

    @Column(name = "sms_sender_id", length = 30)
    private String senderId;

    @Column(name = "message_content")
    private String messageContent;

    @Column(name = "schedule_type", length = 30)
    private String scheduleType;

    @Column(name = "list_type")
    private String listType;

    @Column(name = "message_type")
    private String messageType;

    @Column(name = "message_size_option")
    private java.lang.Byte messageSizeOption;

    @Column(name = "enable_entire_list")
    private boolean enableEntireList;

    @Column(name = "category")
    private java.lang.Long category;

    @Column(name = "template_registered_id")
    private String templateRegisteredId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.optculture.shared.entities.org.User users;

/*
TODO: Convert Set
<set name="mailingLists" table="mlists_sms_campaigns" lazy="false">
			<key column="sms_campaign_id" />
			<many-to-many class="org.mq.marketer.campaign.beans.MailingList" column="list_id" />
		</set>
*/
}
