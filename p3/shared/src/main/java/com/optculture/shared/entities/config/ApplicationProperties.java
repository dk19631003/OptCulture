package com.optculture.shared.entities.config;

import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "application_properties")
public class ApplicationProperties {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "p_id")
    private java.lang.Long pId;

    @Column(name = "props_key")
    private String key;

    @Column(name = "props_value")
    private String value;

}
