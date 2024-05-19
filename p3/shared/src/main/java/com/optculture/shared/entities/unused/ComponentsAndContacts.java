package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "components_contacts")
public class ComponentsAndContacts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cc_id")
    private java.lang.Long ccId;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "contact_id")
    private java.lang.Long contactId;

    @Column(name = "component_id")
    private java.lang.Long componentId;

    @Column(name = "program_id")
    private java.lang.Long programId;

    @Column(name = "stage")
    private int stage;

    @Column(name = "component_win_id")
    private String componentWinId;

    @Column(name = "path")
    private String path;

    @Column(name = "status")
    private String status;

    @Column(name = "activity_date")
    private java.util.Calendar activityDate;

}
