package com.optculture.shared.entities.communication.email;

import jakarta.persistence.*;
@Entity
@Table(name = "user_external_smtp_settings")
public class UserExternalSMTPSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "external_SMTP", length = 256)
    private String externalSMTP;

    @Column(name = "username", length = 64)
    private String userName;

    @Column(name = "password", length = 256)
    private String password;

    @Column(name = "emailId", length = 60)
    private String emailId;

    @Column(name = "Created_date")
    private java.util.Calendar createdDate;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "email_count")
    private java.lang.Integer emailCount;

    @Column(name = "used_email_count")
    private java.lang.Integer usedEmailCount;

    @Column(name = "is_free_user")
    private boolean freeUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.optculture.shared.entities.org.User users;

}
