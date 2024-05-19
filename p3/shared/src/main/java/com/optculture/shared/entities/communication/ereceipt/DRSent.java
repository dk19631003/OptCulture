package com.optculture.shared.entities.communication.ereceipt;

import jakarta.persistence.*;
@Entity
@Table(name = "dr_sent")
public class DRSent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "contact_id")
    private java.lang.Long contactId;

    @Column(name = "email_id", length = 60)
    private String emailId;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "sent_date")
    private java.util.Calendar sentDate;

    @Column(name = "opens")
    private int opens;

    @Column(name = "clicks")
    private int clicks;

    @Column(name = "phValStr")
    private String phValStr;

    @Column(name = "subject", length = 200)
    private String subject;

    @Column(name = "message", length = 60)
    private String message;

    @Column(name = "bounced")
    private int bounced;

    @Column(name = "spam")
    private int spam;

    @Column(name = "drjson_obj_Id")
    private java.lang.Long drJsonObjId;

    @Column(name = "template_name", length = 60)
    private String templateName;

    @Column(name = "html_content")
    private String htmlStr;

    @Column(name = "doc_sid", length = 60)
    private String docSid;

    @Column(name = "sent_count")
    private int sentCount;

    @Column(name = "store_number")
    private String storeNumber;

    @Column(name = "SBS_no")
    private String sbsNumber;

    @Column(name = "selected_template_id")
    private java.lang.Long myTemplateId;

    @Column(name = "zone_Id")
    private java.lang.Long zoneId;

}
