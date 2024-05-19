package com.optculture.shared.entities.system;

import jakarta.persistence.*;
@Entity
@Table(name = "reset_password_token")
public class ResetPasswordToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private java.lang.Long tokenId;

    @Column(name = "token_value")
    private String tokenValue;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "status")
    private String status;

}
