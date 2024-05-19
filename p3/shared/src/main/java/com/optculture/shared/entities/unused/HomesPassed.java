package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "homespassed")
public class HomesPassed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hp_id")
    private java.lang.Long hpId;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "list_id")
    private java.lang.Long listId;

    @Column(name = "address_unit_id")
    private java.lang.Long addressUnitId;

    @Column(name = "status", length = 255)
    private String status;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "last_status_change")
    private java.util.Calendar lastStatusChange;

    @Column(name = "last_mail_date")
    private java.util.Calendar lastMailDate;

    @Column(name = "address_one", length = 100)
    private String addressOne;

    @Column(name = "address_Two", length = 100)
    private String addressTwo;

    @Column(name = "street", length = 100)
    private String street;

    @Column(name = "area", length = 100)
    private String area;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "district", length = 100)
    private String district;

    @Column(name = "zip")
    private String zip;

    @Column(name = "state", length = 50)
    private String state;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "udf1", length = 30)
    private String udf1;

    @Column(name = "udf2", length = 30)
    private String udf2;

    @Column(name = "udf3", length = 30)
    private String udf3;

    @Column(name = "udf4", length = 30)
    private String udf4;

    @Column(name = "udf5", length = 30)
    private String udf5;

    @Column(name = "udf6", length = 30)
    private String udf6;

    @Column(name = "udf7", length = 30)
    private String udf7;

    @Column(name = "udf8", length = 30)
    private String udf8;

    @Column(name = "udf9", length = 30)
    private String udf9;

    @Column(name = "udf10", length = 30)
    private String udf10;

    @Column(name = "udf11", length = 30)
    private String udf11;

    @Column(name = "udf12", length = 30)
    private String udf12;

    @Column(name = "udf13", length = 30)
    private String udf13;

    @Column(name = "udf14", length = 30)
    private String udf14;

    @Column(name = "udf15", length = 30)
    private String udf15;

}
