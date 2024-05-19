package com.optculture.shared.entities.communication.email;

import jakarta.persistence.*;
@Entity
@Table(name = "auto_email_clicks")
public class AutoEmailClicks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "click_id")
    private java.lang.Long clickId;

    @Column(name = "eq_id")
    private java.lang.Long eqId;

    @Column(name = "click_Url", length = 255)
    private java.lang.String clickUrl;

    @Column(name = "click_count")
    private int clickCount;

}
