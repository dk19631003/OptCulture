package com.optculture.shared.entities.config;

import jakarta.persistence.*;
@Entity
@Table(name = "character_codes")
public class CharacterCodes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cc_id")
    private java.lang.Long ccId;

    @Column(name = "spc_character")
    private String charcater;

    @Column(name = "code")
    private String code;

}
