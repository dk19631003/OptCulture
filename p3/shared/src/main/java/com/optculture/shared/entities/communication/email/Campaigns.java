package com.optculture.shared.entities.communication.email;

import jakarta.persistence.*;

@Entity
@Table(name = "campaigns")
public class Campaigns {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campaign_id")
    private java.lang.Long campaignId;

    @Column(name = "campaign_name", length = 100)
    private String campaignName;

    @Column(name = "label", length = 50)
    private String label;

    @Column(name = "subject", length = 200)
    private String subject;

    @Column(name = "personalize_to")
    private boolean personalizeTo;

    @Column(name = "to_name", length = 60)
    private String toName;

    @Column(name = "from_name", length = 60)
    private String fromName;

    @Column(name = "from_email", length = 60)
    private String fromEmail;

    @Column(name = "reply_email", length = 60)
    private String replyEmail;

    @Column(name = "company_name", length = 100)
    private String companyName;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "draft_status", length = 30)
    private String draftStatus;

    @Column(name = "editor_type", length = 30)
    private String editorType;

    @Column(name = "html_text")
    private String htmlText;

    @Column(name = "final_html")
    private String finalHtmlText;

    @Column(name = "is_prepared")
    private boolean prepared;

    @Column(name = "text_message")
    private String textMessage;

    @Column(name = "coupon_flag")
    private boolean couponFlag;

    @Column(name = "schedule_type", length = 30)
    private String scheduleType;

    @Column(name = "web_link_flag")
    private boolean webLinkFlag;

    @Column(name = "web_link_text")
    private String webLinkText;

    @Column(name = "web_link_urlText")
    private String webLinkUrlText;

    @Column(name = "permission_remainder_flag")
    private boolean permissionRemainderFlag;

    @Column(name = "permission_remainder_text", length = 255)
    private String permissionRemainderText;

    @Column(name = "address_flag")
    private boolean addressFlag;

    @Column(name = "address", length = 1000)
    private String addressStr;

    @Column(name = "content_type", length = 30)
    private String contentType;

    @Column(name = "addrs_type", length = 30)
    private String addrsType;

    @Column(name = "lists_type")
    private String listsType;

    @Column(name = "place_holders_type", length = 30)
    private String placeHoldersType;

    @Column(name = "category_weight")
    private short categoryWeight;

    @Column(name = "include_before_str", length = 200)
    private String includeBeforeStr;

    @Column(name = "include_org")
    private boolean includeOrg;

    @Column(name = "include_org_unit")
    private boolean includeOrgUnit;

    @Column(name = "googleanalytics")
    private boolean googleAnalytics;

    @Column(name = "googleanalytics_camptitle", length = 128)
    private String googleAnalyticsCampTitle;

    @Column(name = "categories", length = 200)
    private java.lang.Long categories;

    @Column(name = "json_content", length = 16777215)
    private String jsonContent;

    @Column(name = "customize_footer")
    private boolean customizeFooter;

    @Column(name = "status_changed_on")
    private java.util.Calendar statusChangedOn;

    @Column(name = "downloadpdf")
    private boolean downloadPdf;

    @Column(name = "alignment_Flag")
    private String alignmentFlag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.optculture.shared.entities.org.User users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private com.optculture.shared.entities.config.SystemTemplates template;

    /*
     * TODO: Convert Set
     * <set name="mailingLists" table="mlists_campaigns" lazy="false">
     * <key column="campaign_id" />
     * <many-to-many class="org.mq.marketer.campaign.beans.MailingList"
     * column="list_id" />
     * </set>
     */
}
