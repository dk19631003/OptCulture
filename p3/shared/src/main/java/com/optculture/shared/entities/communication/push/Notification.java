package com.optculture.shared.entities.communication.push;

import jakarta.persistence.*;
@Entity
@Table(name = "Notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private java.lang.Long notificationId;

    @Column(name = "notification_campaign_name", length = 100)
    private String notificationName;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "draft_status", length = 30)
    private String draftStatus;

    @Column(name = "notification_sender_id", length = 30)
    private String notificationSenderId;

    @Column(name = "header")
    private String header;

    @Column(name = "logo_Image_Url")
    private String logoImageUrl;

    @Column(name = "banner_Image_Url")
    private String bannerImageUrl;

    @Column(name = "redirect_Url")
    private String redirectUrl;

    @Column(name = "notification_content")
    private String notificationContent;

    @Column(name = "schedule_type", length = 30)
    private String scheduleType;

    @Column(name = "list_type")
    private String listType;

    @Column(name = "category")
    private java.lang.Long category;

    @Column(name = "user_id")
    private java.lang.Long userId;

/*
TODO: Convert Set
<set name="mailingLists" table="mlists_notification" lazy="false">
			<key column="notification_id" />
			<many-to-many class="org.mq.marketer.campaign.beans.MailingList" column="list_id" />
		</set>
*/
}
