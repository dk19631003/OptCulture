package com.optculture.shared.entities.system;

import jakarta.persistence.*;
@Entity
@Table(name = "export_file_details")
public class ExportFileDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "export_file_id")
    private java.lang.Long exportFileId;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "org_id")
    private long orgId;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "status")
    private String status;

    @Column(name = "created_time")
    private java.util.Calendar createdTime;

    @Column(name = "deleted_time")
    private java.util.Calendar deletedTime;

}
