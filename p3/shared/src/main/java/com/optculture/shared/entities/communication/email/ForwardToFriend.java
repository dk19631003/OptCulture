package com.optculture.shared.entities.communication.email;

import jakarta.persistence.*;
@Entity
@Table(name = "farward_to_friend")
public class ForwardToFriend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "farward_friend_id")
    private java.lang.Long farwardFriendId;

    @Column(name = "cid")
    private java.lang.Long contactId;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "cr_id")
    private java.lang.Long crId;

    @Column(name = "email", length = 60)
    private String email;

    @Column(name = "referer", length = 30)
    private String referer;

    @Column(name = "sent_id")
    private java.lang.Long sentId;

    @Column(name = "cust_msg", length = 60)
    private String custMsg;

    @Column(name = "to_email_id", length = 60)
    private String toEmailId;

    @Column(name = "to_full_name", length = 60)
    private String toFullName;

    @Column(name = "sent_date")
    private java.util.Calendar sentDate;

    @Column(name = "opens")
    private int opens;

    @Column(name = "clicks")
    private int clicks;

    @Column(name = "captcha", length = 60)
    private String captcha;

    @Column(name = "status", length = 60)
    private String status;

}
