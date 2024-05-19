package com.optculture.shared.entities.transactions.logs;

import jakarta.persistence.*;
@Entity
@Table(name = "lty_settings_activity_logs")
public class LtySettingsActivityLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private java.lang.Long logId;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "program_id")
    private java.lang.Long programId;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "log_type")
    private String logType;

    @Column(name = "log_details")
    private String logDetails;

    @Column(name = "send_email_flag")
    private char sendEmailFlag;

    @Column(name = "last_email_sent_date")
    private java.util.Calendar lastEmailSentDate;

}
