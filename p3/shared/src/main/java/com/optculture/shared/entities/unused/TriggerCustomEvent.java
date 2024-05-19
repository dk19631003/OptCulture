package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "trigger_custom_events")
public class TriggerCustomEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "event_date")
    private java.util.Calendar eventDate;

    @Column(name = "user_name")
    private String userName;

}
