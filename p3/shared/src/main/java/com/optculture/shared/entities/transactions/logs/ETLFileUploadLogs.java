package com.optculture.shared.entities.transactions.logs;

import jakarta.persistence.*;
@Entity
@Table(name = "etl_fileupload_logs")
public class ETLFileUploadLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private java.lang.Long logId;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "upload_time")
    private java.util.Calendar uploadTime;

    @Column(name = "record_count")
    private int recordCount;

    @Column(name = "file_status")
    private String fileStatus;

    @Column(name = "comments")
    private String comments;

    @Column(name = "processedFilePath")
    private String processedFilePath;

    @Column(name = "receiptDetailsPath")
    private String receiptDetailsPath;

}
