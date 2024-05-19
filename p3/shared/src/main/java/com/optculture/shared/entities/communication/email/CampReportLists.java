package com.optculture.shared.entities.communication.email;

import jakarta.persistence.*;
@Entity
@Table(name = "campreports_lists")
public class CampReportLists {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cr_list_id")
    private java.lang.Long campRepListId;

    @Column(name = "list_name")
    private String listsName;

    @Column(name = "cr_id")
    private long campaignReportId;

    @Column(name = "segment_query")
    private String segmentQuery;

}
