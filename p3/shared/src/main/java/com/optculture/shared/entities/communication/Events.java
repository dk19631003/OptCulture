package com.optculture.shared.entities.communication;

import jakarta.persistence.*;
@Entity
@Table(name = "events")
public class Events {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long eventId;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "event_title")
    private String eventTitle;

    @Column(name = "event_status")
    private String eventStatus;

    @Column(name = "event_start_date")
    private java.util.Calendar eventStartDate;

    @Column(name = "event_end_date")
    private java.util.Calendar eventEndDate;

    @Column(name = "subtitle", length = 500)
    private String subtitle;

    @Column(name = "description")
    private String description;

    @Column(name = "address_line1", length = 200)
    private String addressLine1;

    @Column(name = "address_line2", length = 200)
    private String addressLine2;

    @Column(name = "state")
    private String state;

    @Column(name = "zip_code")
    private java.lang.Long zipCode;

    @Column(name = "store")
    private String store;

    @Column(name = "city")
    private String city;

    @Column(name = "is_one_day")
    private boolean isOneDay;

    @Column(name = "event_create_date")
    private java.util.Calendar eventCreateDate;

    @Column(name = "dir_id")
    private String dirId;

    @Column(name = "left_lable")
    private String leftLable;

    @Column(name = "left_lable_url")
    private String leftLableURL;

    @Column(name = "right_lable")
    private String rightLable;

    @Column(name = "right_lable_url")
    private String rightLableURL;

    @Column(name = "event_start_date_api")
    private String eventStartDateAPI;

    @Column(name = "event_end_date_api")
    private String eventEndDateAPI;

}
