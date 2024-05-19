package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
// @Entity
// @Table(name = "UserConnection")
public class UserConnection {

    @Column(name = "rank")
    private java.lang.Long rank;

    @Column(name = "displayName", length = 255)
    private java.lang.String displayName;

    @Column(name = "imageUrl", length = 255)
    private java.lang.String imageUrl;

    @Column(name = "profileUrl", length = 255)
    private java.lang.String profileUrl;

    @Column(name = "accessToken", length = 255)
    private java.lang.String accessToken;

    @Column(name = "secret", length = 255)
    private java.lang.String secret;

    @Column(name = "refreshToken", length = 255)
    private java.lang.String refreshToken;

    @Column(name = "expireTime")
    private java.lang.Long expireTime;

}
