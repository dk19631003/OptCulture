package com.optculture.shared.entities.system;

import jakarta.persistence.*;
@Entity
@Table(name = "opt_sync_data")
public class UpdateOptSyncData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "opt_sync_id")
    private java.lang.Long optSyncId;

    @Column(name = "opt_sync_name", length = 60)
    private String optSyncName;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "plugin_status", length = 16)
    private String pluginStatus;

    @Column(name = "opt_sync_hit_time")
    private java.util.Calendar optSyncHitTime;

    @Column(name = "opt_sync_modified_time")
    private java.util.Calendar optSyncModifiedTime;

    @Column(name = "count")
    private java.lang.Integer count;

    @Column(name = "email_id", length = 512)
    private String emailId;

    @Column(name = "enabled_opt_sync_flag", length = 16)
    private String enabledOptSyncFlag;

    @Column(name = "org_Id")
    private java.lang.Long orgId;

    @Column(name = "on_alerts_by")
    private String onAlertsBy;

    @Column(name = "down_alert_sent_ime")
    private java.util.Calendar downAlertSentTime;

}
