package com.optculture.shared.entities.system;

import jakarta.persistence.*;
@Entity
@Table(name = "form_mapping")
public class FormMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "url")
    private String URL;

    @Column(name = "input_field_mapping")
    private String inputFieldMapping;

    @Column(name = "form_name")
    private String formName;

    @Column(name = "form_mapping_name")
    private String formMappingName;

    @Column(name = "form_type")
    private String formType;

    @Column(name = "active_since")
    private java.util.Calendar activeSince;

    @Column(name = "consent_cust_template_id")
    private java.lang.Long consentCustTemplateId;

    @Column(name = "check_parental_email")
    private boolean checkParentalEmail;

    @Column(name = "list_id")
    private java.lang.Long listId;

    @Column(name = "send_email_to_existing_contact")
    private boolean sendEmailToExistingContact;

    @Column(name = "html_redirect_URL")
    private String htmlRedirectURL;

    @Column(name = "enable")
    private boolean enable;

    @Column(name = "submit_field_name")
    private String submitFieldName;

    @Column(name = "html_redirect_failure_URL")
    private String htmlRedirectFailureURL;

    @Column(name = "html_redirect_db_failure_URL")
    private String htmlRedirectDbFailureURL;

    @Column(name = "html_redirect_parental_URL")
    private String htmlRedirectParentalURL;

    @Column(name = "loyalty_program_id")
    private java.lang.Long loyaltyProgramId;

    @Column(name = "loyalty_cardset_id")
    private java.lang.Long loyaltyCardsetId;

    @Column(name = "auto_select_card")
    private char autoSelectCard;

    @Column(name = "enable_parental_consent")
    private char enableParentalConsent;

    @Column(name = "enable_feedback_mail")
    private char checkFeedbackFormEmail;

    @Column(name = "enable_feedback_sms")
    private char checkFeedbackFormSms;

    @Column(name = "feedback_mail_custtemplateid")
    private java.lang.Long feedBackMailCustTemplateId;

    @Column(name = "feedback_sms_templateid")
    private java.lang.Long feedBackSmsTemplateId;

    @Column(name = "issuereward_ischecked")
    private char issueRewardIschecked;

    @Column(name = "issuereward_type")
    private String issueRewardType;

    @Column(name = "issuereward_value")
    private String issueRewardValue;

    @Column(name = "enable_simplesignup_mail")
    private char checkSimpleSignUpForEmail;

    @Column(name = "enable_simplesignup_sms")
    private char checkSimpleSignUpFormSms;

    @Column(name = "simplesignup_mail_custtemplateid")
    private java.lang.Long simpleSignUpCustTemplateId;

    @Column(name = "simplesignup_sms_templateid")
    private java.lang.Long simpleSignUpSmsTemplateId;

    @Column(name = "do_issue_points")
    private char doIssuePoints;

    @Column(name = "webhook")
    private boolean webHook;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.optculture.shared.entities.org.User users;

}
