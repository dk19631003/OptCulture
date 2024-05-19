package com.optculture.shared.entities.transactions.logs;

import jakarta.persistence.*;
@Entity
@Table(name = "pos_file_logs")
public class POSFileLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pos_log_id")
    private java.lang.Long posLogId;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "fetched_time")
    private java.util.Calendar fetchedTime;

}
