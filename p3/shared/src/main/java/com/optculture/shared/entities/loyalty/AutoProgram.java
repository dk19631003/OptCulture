package com.optculture.shared.entities.loyalty;

import jakarta.persistence.*;
@Entity
@Table(name = "auto_program")
public class AutoProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "program_id")
    private java.lang.Long programId;

    @Column(name = "program_name")
    private String programName;

    @Column(name = "description")
    private String description;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "status")
    private String status;

    @Column(name = "category")
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.optculture.shared.entities.org.User user;

}
