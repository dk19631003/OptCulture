package com.optculture.shared.entities.communication.email;

import jakarta.persistence.*;
@Entity
@Table(name = "clicks")
public class Clicks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "click_id")
    private java.lang.Long clickId;

    @Column(name = "cr_id")
    private java.lang.Long crId;

    @Column(name = "sent_id")
    private java.lang.Long sentId;

    @Column(name = "click_Url", length = 255)
    private java.lang.String clickUrl;

    @Column(name = "click_date")
    private java.util.Calendar clickDate;

}
