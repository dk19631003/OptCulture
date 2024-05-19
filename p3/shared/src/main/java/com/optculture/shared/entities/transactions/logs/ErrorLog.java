package com.optculture.shared.entities.transactions.logs;

import jakarta.persistence.*;
@Entity
@Table(name = "captiway_logs")
public class ErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long refId;

    @Column(name = "message")
    private String message;

    @Column(name = "source", length = 100)
    private String source;

    @Column(name = "date")
    private java.util.Calendar date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.optculture.shared.entities.org.User users;

}
