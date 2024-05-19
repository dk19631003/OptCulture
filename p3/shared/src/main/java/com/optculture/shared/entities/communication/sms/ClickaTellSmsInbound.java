package com.optculture.shared.entities.communication.sms;

import jakarta.persistence.*;
@Entity
@Table(name = "sms_inbounds")
public class ClickaTellSmsInbound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inbound_msg_id")
    private java.lang.Long inboundMsgId;

    @Column(name = "text_content")
    private String text;

    @Column(name = "used_keywords")
    private String usedKeyWords;

    @Column(name = "mo_from")
    private String moFrom;

    @Column(name = "mo_to")
    private String moTo;

    @Column(name = "inbound_time")
    private java.util.Calendar timeStamp;

    @Column(name = "org_id")
    private java.lang.Long orgId;

    @Column(name = "auto_response", length = 1000)
    private String autoResponse;

    @Column(name = "msg_id", length = 100)
    private String msgID;

    @Column(name = "delivery_status", length = 50)
    private String deliveryStatus;

    @Column(name = "delivered_time")
    private java.util.Calendar deliveredTime;

}
