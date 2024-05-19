package com.optculture.shared.entities.config;

import jakarta.persistence.*;
@Entity
@Table(name = "country_receiving_numbers")
public class CountryReceivingNumbers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recv_num_id")
    private java.lang.Long recvNumId;

    @Column(name = "country")
    private String country;

    @Column(name = "receiving_number")
    private String receivingNumber;

    @Column(name = "recv_num_type")
    private String recvNumType;

    @Column(name = "gateway")
    private java.lang.Long gatewayId;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "created_by")
    private java.lang.Long createdBy;

}
