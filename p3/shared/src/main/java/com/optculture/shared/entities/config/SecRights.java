package com.optculture.shared.entities.config;

import jakarta.persistence.*;
@Entity
@Table(name = "sec_rights")
public class SecRights {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "right_id")
    private java.lang.Long right_id;

    @Column(name = "type", length = 100)
    private String type;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "version", length = 50)
    private String version;

}
