package com.optculture.shared.entities.transactions;

import jakarta.persistence.*;
@Entity
@Table(name = "purge_queue")
public class PurgeQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "list_id")
    private java.lang.Long listId;

    @Column(name = "status")
    private String status;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "purged_date")
    private java.util.Calendar purgedDate;

}
