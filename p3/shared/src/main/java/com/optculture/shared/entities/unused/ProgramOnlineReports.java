package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "program_online_reports")
public class ProgramOnlineReports {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prog_rep_id")
    private java.lang.Long progRepId;

    @Column(name = "activity_date")
    private java.util.Date activityDate;

    @Column(name = "component_win_id")
    private String componentWinId;

    @Column(name = "program_id")
    private java.lang.Long programId;

    @Column(name = "component_id")
    private java.lang.Long componentId;

    @Column(name = "contact_id")
    private java.lang.Long contactId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comp_contacts_id")
    private com.optculture.shared.entities.unused.ComponentsAndContacts compContactsId;

}
