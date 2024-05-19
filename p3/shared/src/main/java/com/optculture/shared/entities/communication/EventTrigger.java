package com.optculture.shared.entities.communication;

import jakarta.persistence.*;

@Entity
@Table(name = "event_trigger")
public class EventTrigger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "trigger_name")
    private String triggerName;

    @Column(name = "campaign_id")
    private java.lang.Long campaignId;

    @Column(name = "trigger_type")
    private String triggerType;

    @Column(name = "minutes_offset")
    private java.lang.Long minutesOffset;

    @Column(name = "event_field")
    private String eventField;

    @Column(name = "data_type")
    private String dataType;

    @Column(name = "status")
    private String status;

    @Column(name = "trigger_modified_date")
    private java.util.Calendar triggerModifiedDate;

    @Column(name = "trigger_created_date")
    private java.util.Calendar triggerCreatedDate;

    @Column(name = "last_sent_date")
    private java.util.Calendar lastSentDate;

    @Column(name = "selected_campaign_from_name")
    private String selectedCampaignFromName;

    @Column(name = "selected_campaign_reply_email")
    private String selectedCampaignReplyEmail;

    @Column(name = "selected_campaign_from_email")
    private String selectedCampaignFromEmail;

    @Column(name = "options_flag")
    private java.lang.Long optionsFlag;

    @Column(name = "sms_id")
    private java.lang.Long smsId;

    @Column(name = "trigger_query")
    private String triggerQuery;

    @Column(name = "tr_bits")
    private java.lang.Long trBits;

    @Column(name = "tr_type")
    private java.lang.Integer trType;

    @Column(name = "tr_bit_flag_offtime")
    private java.lang.Long trBitFlagOffTime;

    @Column(name = "input_str")
    private String inputStr;

    @Column(name = "last_fetched_time")
    private java.util.Calendar lastFetchedTime;

    @Column(name = "target_days_flag")
    private String targetDaysFlag;

    @Column(name = "target_time")
    private java.util.Date targetTime;

    @Column(name = "camp_category")
    private java.lang.Long campCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.optculture.shared.entities.org.User users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "add_to_list")
    private com.optculture.shared.entities.contact.MailingList addTriggerContactsToMl;

    /*
     * TODO: Convert Set
     * <set name="mailingLists" table="mlists_trigger" cascade="save-update">
     * <key column="id" />
     * <many-to-many class="org.mq.marketer.campaign.beans.MailingList"
     * column="list_id" />
     * </set>
     */
}
