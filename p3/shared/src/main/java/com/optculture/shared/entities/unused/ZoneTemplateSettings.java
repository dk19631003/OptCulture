package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "zone_template_settings")
public class ZoneTemplateSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private java.lang.Long id;

    @Column(name = "zone_id")
    private java.lang.Long zoneId;

    @Column(name = "channel")
    private String channel;

    @Column(name = "sender_or_from")
    private String senderORfrom;

    @Column(name = "auto_comm_type", length = 30)
    private String autoCommType;

    @Column(name = "template_id")
    private String templateId;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.optculture.shared.entities.org.User userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    private com.optculture.shared.entities.org.UserOrganization orgId;

}
