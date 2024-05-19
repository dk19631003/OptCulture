package com.optculture.shared.entities.communication.email;

import jakarta.persistence.*;
@Entity
@Table(name = "domain_status")
public class DomainStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "domain_id")
    private java.lang.Long domainId;

    @Column(name = "domain")
    private java.lang.String domain;

    @Column(name = "status")
    private java.lang.String status;

}
