package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "user_agent_json")
public class UserAgentJSON {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ua_json_id")
    private long uajsonId;

    @Column(name = "user_agent_str", length = 700)
    private String userAgentStr;

    @Column(name = "json_str", length = 1000)
    private String jsonStr;

}
