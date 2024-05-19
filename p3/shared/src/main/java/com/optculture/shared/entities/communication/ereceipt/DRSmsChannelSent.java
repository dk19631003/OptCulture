package com.optculture.shared.entities.communication.ereceipt;

import java.util.Calendar;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dr_sms_channel_sent")
public class DRSmsChannelSent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "contact_id")
    private Long contactId;

    @Column(name = "mobile", length = 60)
    private String mobile;

    @Column(name = "email", length = 60)
    private String email;

    @Column(name = "name", length = 60)
    private String name;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "sent_date")
    private Calendar sentDate;

    @Column(name = "sent_count")
    private Integer sentCount;

    @Column(name = "opens")
    private Integer opens;

    @Column(name = "clicks")
    private Integer clicks;

    @Column(name = "drjson_obj_Id")
    private Long drJsonObjId;

    @Column(name = "doc_sid", length = 60)
    private String docSid;

    @Column(name = "store_number")
    private String storeNumber;

    @Column(name = "sbs_no")
    private String sbsNumber;

    @Column(name = "ack_id")
    private String ackId;

    @Column(name = "dlr_status")
    private String dlrStatus;

    @Column(name = "org_id")
    private Long orgId;

    @Column(name = "zone_id")
    private Long zoneId;

    @Column(name = "html_content")
    private String htmlContent;

    @Column(name = "original_short_code")
    private String originalShortCode;

    @Column(name = "short_url")
    private String shortUrl;

    @Column(name = "original_url")
    private String originalUrl;

    @Column(name = "generated_short_code")
    private String generatedShortCode;

    @Column(name = "receipt_no")
    private String receiptNo;

    @Column(name = "transaction_time")
    private String transactionTime;

    @Column(name = "channel")
    private String channel;

}
