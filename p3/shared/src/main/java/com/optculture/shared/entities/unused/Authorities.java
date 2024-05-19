package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "authorities")
public class Authorities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authority_id")
    private java.lang.Long authorityId;

    @Column(name = "authority", length = 50)
    private String authority;

    @Column(name = "username", length = 20)
    private String username;

}
