package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "share_social_network_link")
public class ShareSocialNetworkLinks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "sent_id")
    private long sentId;

    @Column(name = "cr_id")
    private long crId;

    @Column(name = "source_type")
    private String sourceType;

    @Column(name = "shared_time")
    private java.util.Calendar sharedTime;

}
