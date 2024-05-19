package com.optculture.shared.entities.communication.sms;

import jakarta.persistence.*;
@Entity
@Table(name = "org_sms_keywords")
public class OrgSmskeywords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "keyword_id")
    private java.lang.Long keywordId;

    @Column(name = "keyword")
    private String keyword;

    @Column(name = "auto_response")
    private String autoResponse;

    @Column(name = "status")
    private String status;

    @Column(name = "valid_upto")
    private java.util.Calendar validUpto;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "start_from")
    private java.util.Calendar startFrom;

    @Column(name = "org_id")
    private java.lang.Long orgId;

    @Column(name = "sender_id")
    private String senderId;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "modified_by")
    private java.lang.Long modifiedBy;

    @Column(name = "short_code", length = 15)
    private String shortCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.optculture.shared.entities.org.User user;

}
