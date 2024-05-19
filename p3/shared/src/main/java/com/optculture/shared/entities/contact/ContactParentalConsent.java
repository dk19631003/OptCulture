package com.optculture.shared.entities.contact;

import jakarta.persistence.*;
@Entity
@Table(name = "contact_parental_consent")
public class ContactParentalConsent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parental_id")
    private java.lang.Long parentalId;

    @Column(name = "email_id", length = 60)
    private String email;

    @Column(name = "contact_email_id", length = 60)
    private String contactEmail;

    @Column(name = "contact_id")
    private java.lang.Long contactId;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "sent_date")
    private java.util.Calendar sentDate;

    @Column(name = "child_first_name")
    private String childFirstName;

    @Column(name = "child_dob")
    private java.util.Calendar childDOB;

    @Column(name = "status", length = 50)
    private String status;

}
