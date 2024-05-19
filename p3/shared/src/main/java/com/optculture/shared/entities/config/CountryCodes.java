package com.optculture.shared.entities.config;

import jakarta.persistence.*;
@Entity
@Table(name = "country_codes")
public class CountryCodes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "country_codes_id")
    private java.lang.Long countryCodesId;

    @Column(name = "country_name", length = 100)
    private String countryName;

    @Column(name = "calling_code", length = 100)
    private String callingCode;

    @Column(name = "geo_code", length = 100)
    private String geoCode;

}
