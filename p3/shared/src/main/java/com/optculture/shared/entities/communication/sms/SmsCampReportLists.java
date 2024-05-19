package com.optculture.shared.entities.communication.sms;

import jakarta.persistence.*;
@Entity
@Table(name = "sms_campreports_lists")
public class SmsCampReportLists {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sms_cr_list_id")
    private java.lang.Long smsCampRepListId;

    @Column(name = "list_name")
    private String listsName;

    @Column(name = "sms_cr_id")
    private long smsCampaignReportId;

    @Column(name = "segment_query")
    private String segmentQuery;

}
