package com.optculture.shared.entities.communication.ereceipt;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dr_sms_sent")
public class DRSmsSent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "contact_id")
    private java.lang.Long contactId;

    @Column(name = "mobile", length = 60)
    private String mobile;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "sent_date")
    private java.util.Calendar sentDate;

    @Column(name = "opens")
    private Integer opens;

    @Column(name = "clicks")
    private Integer clicks;

    @Column(name = "message", length = 60)
    private String message;

    @Column(name = "drjson_obj_Id")
    private java.lang.Long drJsonObjId;

    @Column(name = "doc_sid", length = 60)
    private String docSid;

    @Column(name = "sent_count")
    private Integer sentCount;

    @Column(name = "store_number")
    private String storeNumber;

    @Column(name = "SBS_no")
    private String sbsNumber;

    @Column(name = "zone_Id")
    private java.lang.Long zoneId;

    @Column(name = "html_content")
    private String htmlStr;

    @Column(name = "original_short_code")
    private String originalShortCode;

    @Column(name = "short_url")
    private String shortUrl;

    @Column(name = "original_url")
    private String originalUrl;

    @Column(name = "generated_short_code")
    private String generatedShortCode;

    @Column(name = "message_id", length = 100)
    private String messageId;

    @Column(name = "dlr_status")
    private String dlrStatus;

    @Column(name = "sent_on_WA")
    private Boolean sentOnWA;


}
