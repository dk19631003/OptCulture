package com.optculture.shared.entities.transactions.logs;

import jakarta.persistence.*;
@Entity
@Table(name = "user_activities")
public class UserActivities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "activity", length = 256)
    private String activity;

    @Column(name = "date")
    private java.util.Calendar date;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "module", length = 256)
    private String module;

}
