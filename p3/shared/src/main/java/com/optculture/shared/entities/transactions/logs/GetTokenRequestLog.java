package com.optculture.shared.entities.transactions.logs;

import jakarta.persistence.*;
@Entity
@Table(name = "get_token_request_log")
public class GetTokenRequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "json_request")
    private String jsonRequest;

    @Column(name = "json_response")
    private String jsonResponse;

    @Column(name = "request_date")
    private java.util.Calendar requestDate;

}
