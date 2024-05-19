package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "social_account_page_settings")
public class SocialAccountPageSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "user_id", length = 255)
    private java.lang.String userId;

    @Column(name = "profile_page_name", length = 255)
    private java.lang.String profilePageName;

    @Column(name = "profile_page_id", length = 255)
    private java.lang.String profilePageId;

    @Column(name = "profile_page_type", length = 255)
    private java.lang.String profilePageType;

}
