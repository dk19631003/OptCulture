package com.optculture.shared.entities.communication.email;

import jakarta.persistence.*;
@Entity
@Table(name = "unsubscribes")
public class Unsubscribes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long unsubscribeId;

    @Column(name = "email_id", length = 60)
    private String emailId;

    @Column(name = "reason")
    private String reason;

    @Column(name = "date")
    private java.util.Calendar date;

    @Column(name = "unsub_categories_Weight")
    private java.lang.Short unsubcategoriesWeight;

    @Column(name = "user_id")
    private java.lang.Long userId;

}
