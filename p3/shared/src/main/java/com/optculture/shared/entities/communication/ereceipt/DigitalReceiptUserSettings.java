package com.optculture.shared.entities.communication.ereceipt;

import jakarta.persistence.*;
@Entity
@Table(name = "digital_receipt_user_settings")
public class DigitalReceiptUserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "template_json_settings", length = 50)
    private String templateJsonSettings;

    @Column(name = "selected_template_name")
    private String selectedTemplateName;

    @Column(name = "subject", length = 200)
    private String subject;

    @Column(name = "from_name")
    private String fromName;

    @Column(name = "from_email")
    private String fromEmail;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "personalize_to")
    private boolean personalizeTo;

    @Column(name = "to_name", length = 60)
    private String toName;

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

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "sms_enabled")
    private boolean smsEnabled;

    @Column(name = "message_content")
    private String messageContent;

    @Column(name = "include_tax")
    private boolean includeTax;

    @Column(name = "include_fee")
    private boolean includeFee;

    @Column(name = "include_shipping")
    private boolean includeShipping;

    @Column(name = "global_discount")
    private boolean includeGlobalDiscount;

    @Column(name = "include_dynamic_from_name")
    private boolean includeDynamicFrmName;

    @Column(name = "include_dynamic_from_email")
    private boolean includeDynamicFrmEmail;

    @Column(name = "total_amount")
    private boolean includeTotalAmount;

    @Column(name = "selected_template_id")
    private java.lang.Long myTemplateId;

    @Column(name = "zone_id", length = 50)
    private java.lang.Long zoneId;

    @Column(name = "setting_enable")
    private boolean settingEnable;

    @Column(name = "include_dynamic_reply_to_email")
    private boolean includeDynamicReplyToEmail;

    @Column(name = "reply_To_Email")
    private String replyToEmail;

    @Column(name = "date_format")
    private String dateFormat;

    @Column(name = "credit_note_enabled")
    private boolean creditNoteEnabled;

    @Column(name = "CN_template_id")
    private java.lang.Long CNTemplateId;

}
