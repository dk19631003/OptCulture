package com.optculture.shared.entities.communication.email;

import jakarta.persistence.*;
@Entity
@Table(name = "user_vmta")
public class UserVmta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "vmta_id")
    private java.lang.Long vmtaId;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "email_type", length = 60)
    private String emailType;

    @Column(name = "enabled")
    private boolean enabled;

}
