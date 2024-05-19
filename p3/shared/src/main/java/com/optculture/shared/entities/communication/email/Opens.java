package com.optculture.shared.entities.communication.email;

import jakarta.persistence.*;
@Entity
@Table(name = "opens")
public class Opens {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "open_id")
    private java.lang.Long openId;

    @Column(name = "sent_id")
    private java.lang.Long sentId;

    @Column(name = "open_date")
    private java.util.Calendar openDate;

    @Column(name = "email_client")
    private java.lang.Long emailClient;

    @Column(name = "os_Family")
    private java.lang.Long osFamily;

    @Column(name = "ua_Family")
    private java.lang.Long uaFamily;

}
