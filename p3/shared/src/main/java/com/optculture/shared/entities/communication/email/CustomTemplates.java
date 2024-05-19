package com.optculture.shared.entities.communication.email;

import jakarta.persistence.*;

@Entity
@Table(name = "custom_templates")
public class CustomTemplates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id")
    private java.lang.Long templateId;

    @Column(name = "template_name", length = 255)
    private java.lang.String templateName;

    @Column(name = "html_text")
    private String htmlText;

    @Column(name = "iframe_link")
    private java.lang.String iframeLink;

    @Column(name = "type")
    private java.lang.String type;

    @Column(name = "selected_form")
    private java.lang.String selectedForm;

    @Column(name = "subject", length = 200)
    private String subject;

    @Column(name = "editor_type", length = 200)
    private String editorType;

    @Column(name = "personalize_to")
    private boolean personalizeTo;

    @Column(name = "to_name", length = 100)
    private String toName;

    @Column(name = "from_name", length = 60)
    private String fromName;

    @Column(name = "from_email", length = 100)
    private String fromEmail;

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

    @Column(name = "include_org")
    private boolean includeOrg;

    @Column(name = "include_org_unit")
    private boolean includeOrgUnit;

    @Column(name = "addr_type", length = 30)
    private String addrType;

    @Column(name = "address", length = 1000)
    private String addressStr;

    @Column(name = "include_before_str", length = 200)
    private String includeBeforeStr;

    @Column(name = "myTemplate_Id")
    private java.lang.Long myTemplateId;

    @Column(name = "reply_to_email", length = 100)
    private String replyToEmail;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "customize_footer")
    private boolean customizeFooter;

    @Column(name = "downloadpdf")
    private boolean downloadPdf;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.optculture.shared.entities.org.User userId;

}
