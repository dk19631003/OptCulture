package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "upload_report")
public class UploadReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long uploadReportId;

    @Column(name = "filename", length = 250)
    private String fileName;

    @Column(name = "uploaded_to", length = 50)
    private String uploadedTo;

    @Column(name = "module", length = 50)
    private String module;

    @Column(name = "date")
    private java.util.Calendar date;

    @Column(name = "report")
    private String report;

    @Column(name = "status", length = 30)
    private String status;

}
