package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "generate_report_setting")
public class GenerateReportSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "file_format")
    private String fileFormat;

    @Column(name = "org_id")
    private java.lang.Long orgId;

    @Column(name = "account_id")
    private java.lang.Long accountId;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "type")
    private String type;

    @Column(name = "enable")
    private boolean enable;

    @Column(name = "generate_at")
    private java.util.Date generateAt;

    @Column(name = "created_on")
    private java.util.Calendar createdOn;

    @Column(name = "created_by")
    private java.lang.Long createdBy;

    @Column(name = "freequency")
    private String freequency;

    @Column(name = "modified_on")
    private java.util.Calendar modifiedOn;

    @Column(name = "modified_by")
    private java.lang.Long modifiedBy;

    @Column(name = "last_generated_on")
    private java.util.Calendar lastGeneratedOn;

    @Column(name = "last_generated_file")
    private String lastGeneratedFile;

    @Column(name = "host")
    private String host;

    @Column(name = "port")
    private java.lang.Integer port;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "targetDir")
    private String targetDir;

    @Column(name = "selected_timezone")
    private String selectedTimeZone;

    @Column(name = "timezone_name")
    private String timeZoneName;

}
