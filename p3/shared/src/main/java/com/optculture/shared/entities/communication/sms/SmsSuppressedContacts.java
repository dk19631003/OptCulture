package com.optculture.shared.entities.communication.sms;

import jakarta.persistence.*;
@Entity
@Table(name = "sms_suppressed_contacts")
public class SmsSuppressedContacts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "mobile", length = 100)
    private String mobile;

    @Column(name = "type")
    private String type;

    @Column(name = "source")
    private String source;

    @Column(name = "suppressed_time")
    private java.util.Calendar suppressedtime;

    @Column(name = "reason")
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.optculture.shared.entities.org.User user;

}
