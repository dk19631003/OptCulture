package com.optculture.shared.entities.config;

import jakarta.persistence.*;
@Entity
@Table(name = "user_pos_ftp_settings")
public class UserPosFtpSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_ftp_id")
    private java.lang.Long userFTPId;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "host_address")
    private String hostAddress;

    @Column(name = "directory_path")
    private String directoryPath;

    @Column(name = "ftp_user_name")
    private String ftpUserName;

    @Column(name = "ftp_password")
    private String ftpPassword;

    @Column(name = "file_format")
    private String fileFormat;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "last_fetched_time")
    private java.util.Calendar lastFetchedTime;

    @Column(name = "scheduled_freq_in_minutes")
    private long scheduledFreqInMintues;

    @Column(name = "schedule_type")
    private String scheduleType;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "check_process_period")
    private int checkProcessPeriod;

    @Column(name = "alert_email_address")
    private String alertEmailAddress;

    @Column(name = "check_alert")
    private boolean checkAlert;

}
