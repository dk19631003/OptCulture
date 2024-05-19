package com.optculture.shared.entities.unused;

import jakarta.persistence.*;

@Entity
@Table(name = "email_clients")
public class EmailClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long emailClientId;

    @Column(name = "email_client", length = 50)
    private String emailClientType;

    @Column(name = "user_agent", length = 50)
    private String userAgent;

    @Column(name = "ua_disp_value", length = 50)
    private String uaDispValue;

    @Column(name = "user_agent_str")
    private String userAgentStr;

}
