package com.optculture.shared.entities.transactions.logs;

import jakarta.persistence.*;
@Entity
@Table(name = "audit_trails")
public class AuditTrails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "ip")
    private String IP;

    @Column(name = "status")
    private String status;

    @Column(name = "date")
    private java.util.Calendar date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.optculture.shared.entities.org.User users;

}
