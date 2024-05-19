package com.optculture.shared.entities.communication;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tracking_url")
@Data
public class TrackingUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "url_id")
    private java.lang.Long urlId;

    @Column(name = "type", length = 10)
    private String typeOfUrl;

    @Column(name = "cr_id")
    private java.lang.Long crId;

    @Column(name = "original_url", length = 1000)
    private String originalUrl;

    @Column(name = "short_url", length = 500)
    private String shortUrl;

}
