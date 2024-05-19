package com.optculture.shared.entities.contact;

import jakarta.persistence.*;
@Entity
@Table(name = "mailing_lists")
public class MailingList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_id")
    private java.lang.Long listId;

    @Column(name = "list_name", length = 50)
    private String listName;

    @Column(name = "description", length = 128)
    private String description;

    @Column(name = "Created_date")
    private java.util.Calendar createdDate;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "list_type")
    private String listType;

    @Column(name = "last_statusChangeDate")
    private java.util.Calendar lastStatusChangeDate;

    @Column(name = "last_modifiedDate")
    private java.util.Calendar lastModifiedDate;

    @Column(name = "cust_field")
    private boolean custField;

    @Column(name = "custom_template_id")
    private java.lang.Long custTemplateId;

    @Column(name = "consent_template_id")
    private java.lang.Long consentCutomTempId;

    @Column(name = "loyalty_template_id")
    private java.lang.Long loyaltyCutomTempId;

    @Column(name = "welcome_template_id")
    private java.lang.Long welcomeCustTempId;

    @Column(name = "check_double_optin")
    private boolean checkDoubleOptin;

    @Column(name = "check_parental_consent")
    private boolean checkParentalConsent;

    @Column(name = "check_loyalty_optin")
    private boolean checkLoyaltyOptin;

    @Column(name = "check_welcome_msg")
    private boolean checkWelcomeMsg;

    @Column(name = "consent")
    private boolean consent;

    @Column(name = "mlbit")
    private java.lang.Long mlBit;

    @Column(name = "list_size")
    private java.lang.Long listSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.optculture.shared.entities.org.User users;

/*
TODO: Convert Set
<set name="campaigns" table="mlists_campaigns" cascade="save-update" lazy="false" inverse="true">
			<key column="list_id" />
			<many-to-many class="org.mq.marketer.campaign.beans.Campaigns" column="campaign_id" />
		</set>
*/
/*
TODO: Convert Set
<set name="smsCampaigns" table="mlists_sms_campaigns" cascade="save-update" lazy="false" inverse="true">
			<key column="list_id" />
			<many-to-many class="org.mq.marketer.campaign.beans.SmsCampaigns" column="sms_campaign_id" />
		</set>
*/
/*
TODO: Convert Set
<set name="sharedToDomain" table="mlists_domains">
			<key column="list_id" />
			<many-to-many class="org.mq.marketer.campaign.beans.UsersDomains" column="domain_id" />
		</set>
*/
/*
TODO: Convert Set
<set name="triggersSet" table="mlists_trigger" cascade="save-update">
			<key column="list_id" />
			<many-to-many class="org.mq.marketer.campaign.beans.EventTrigger" column="id" />
		</set>
*/
}
