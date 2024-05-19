package com.optculture.shared.entities.system;

import jakarta.persistence.*;
@Entity
@Table(name = "customfield_data")
public class CustomFieldData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "c_id")
    private java.lang.Long cId;

    @Column(name = "cust_1", length = 50)
    private String cust1;

    @Column(name = "cust_2", length = 50)
    private String cust2;

    @Column(name = "cust_3", length = 50)
    private String cust3;

    @Column(name = "cust_4", length = 50)
    private String cust4;

    @Column(name = "cust_5", length = 50)
    private String cust5;

    @Column(name = "cust_6", length = 50)
    private String cust6;

    @Column(name = "cust_7", length = 50)
    private String cust7;

    @Column(name = "cust_8", length = 50)
    private String cust8;

    @Column(name = "cust_9", length = 50)
    private String cust9;

    @Column(name = "cust_10", length = 50)
    private String cust10;

    @Column(name = "cust_11", length = 50)
    private String cust11;

    @Column(name = "cust_12", length = 50)
    private String cust12;

    @Column(name = "cust_13", length = 50)
    private String cust13;

    @Column(name = "cust_14", length = 50)
    private String cust14;

    @Column(name = "cust_15", length = 50)
    private String cust15;

    @Column(name = "cust_16", length = 50)
    private String cust16;

    @Column(name = "cust_17", length = 50)
    private String cust17;

    @Column(name = "cust_18", length = 50)
    private String cust18;

    @Column(name = "cust_19", length = 50)
    private String cust19;

    @Column(name = "cust_20", length = 50)
    private String cust20;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private com.optculture.shared.entities.contact.Contact contact;

}
