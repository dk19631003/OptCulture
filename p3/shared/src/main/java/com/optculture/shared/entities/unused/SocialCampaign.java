package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "social_campaigns")
public class SocialCampaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campaign_id")
    private java.lang.Long campaignId;

    @Column(name = "campaign_name")
    private java.lang.String campaignName;

    @Column(name = "description")
    private java.lang.String description;

    @Column(name = "providers")
    private java.lang.Byte providers;

    @Column(name = "fbPageIds")
    private java.lang.String fbPageIds;

    @Column(name = "twitterContent")
    private java.lang.String twitterContent;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "last_modified_date")
    private java.util.Calendar lastModifiedDate;

    @Column(name = "campaign_status")
    private java.lang.String campaignStatus;

/*
TODO: Convert Set
<set name="socialCampSchedules" table="social_campaign_schedules" inverse="true" lazy="false" fetch="select">
            <key>
                <column name="campaign_id" not-null="true" />
            </key>
            <one-to-many class="org.mq.marketer.campaign.beans.SocialCampaignSchedule" />
        </set>
*/
}
