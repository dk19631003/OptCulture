package com.optculture.shared.entities.communication;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@RequiredArgsConstructor
@Table(name = "url_short_code_mapping")
public class UrlShortCodeMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "short_code_id")
    private java.lang.Long shortCodeId;

    @Column(name = "short_code",unique = true)
    private String shortCode;

    @Column(name = "url_content")
    private String urlContent;

    @Column(name = "url_type")
    private String urlType;

    @Column(name = "user_id")
    private java.lang.Long userId;

}
