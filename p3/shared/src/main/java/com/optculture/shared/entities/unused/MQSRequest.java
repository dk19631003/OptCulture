package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "mqs_request")
public class MQSRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "ref_number")
    private String refNumber;

    @Column(name = "service", length = 100)
    private String service;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "date")
    private java.util.Calendar date;

    @Column(name = "reqestXML")
    private String reqestXML;

    @Column(name = "responseXML")
    private String responseXML;

}
