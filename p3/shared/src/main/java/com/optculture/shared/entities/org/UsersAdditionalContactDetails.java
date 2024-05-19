package com.optculture.shared.entities.org;

import jakarta.persistence.*;
@Entity
@Table(name = "users_additional_contact_details")
public class UsersAdditionalContactDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "additional_contact_id")
    private java.lang.Long additionalContactId;

    @Column(name = "name")
    private String name;

    @Column(name = "number")
    private String number;

    @Column(name = "email")
    private String email;

    @Column(name = "position")
    private String position;

    @Column(name = "priority_level")
    private java.lang.Integer priorityLevel;

    @Column(name = "user_id")
    private java.lang.Long userId;

}
