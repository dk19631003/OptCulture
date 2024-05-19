package com.optculture.shared.entities.communication.push;

import jakarta.persistence.*;
@Entity
@Table(name = "notification_campreports_lists")
public class NotificationCampReportLists {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_cr_list_id")
    private java.lang.Long notificationCampRepListId;

    @Column(name = "list_name")
    private String listsName;

    @Column(name = "notification_cr_id")
    private long notificationCampaignReportId;

    @Column(name = "segment_query")
    private String segmentQuery;

}
