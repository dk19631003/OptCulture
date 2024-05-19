package com.optculture.shared.entities.communication.email;

import com.optculture.shared.entities.org.User;

import jakarta.persistence.*;
@Entity
@Table(name = "campaign_report")
public class CampaignReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cr_id")
    private java.lang.Long crId;

    @Column(name = "campaign_name", length = 100)
    private String campaignName;

    @Column(name = "sent_date")
    private java.util.Calendar sentDate;

    @Column(name = "content")
    private String content;

    @Column(name = "subject", length = 200)
    private String subject;

    @Column(name = "sent")
    private long sent;

    @Column(name = "opens")
    private int opens;

    @Column(name = "clicks")
    private int clicks;

    @Column(name = "unsubscribes")
    private int unsubscribes;

    @Column(name = "bounces")
    private int bounces;

    @Column(name = "spams")
    private int spams;

    @Column(name = "status", length = 100)
    private String status;

    @Column(name = "source_type", length = 100)
    private String sourceType;

    @Column(name = "configured")
    private long configured;

    @Column(name = "place_holders_str", length = 1024)
    private String placeHoldersStr;

    @Column(name = "preference_count")
    private int preferenceCount;

    @Column(name = "suppressed")
    private int suppressed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

/*
TODO: Convert Set
<set name="domains" table="campaign_domains" lazy="false">
			<key column="cr_id" />
			<element type="string" column="domain" />
		</set>
*/
/*
TODO: Convert Set
<set name="urls" table="campaign_urls" lazy="false">
			<key column="cr_id" />
			<element type="string" column="url" />
		</set>
*/
}
