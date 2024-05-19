package com.optculture.shared.entities.transactions.logs;

import jakarta.persistence.*;
@Entity
@Table(name = "promo_trx_log")
public class PromoTrxLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "json_request")
    private String jsonRequest;

    @Column(name = "json_response")
    private String jsonResponse;

    @Column(name = "type")
    private String type;

    @Column(name = "mode")
    private String mode;

    @Column(name = "pcflag")
    private boolean pcFlag;

    @Column(name = "request_date")
    private java.util.Calendar requestDate;

    @Column(name = "user_detail")
    private String userDetail;

    @Column(name = "doc_sid")
    private String docSID;

    @Column(name = "store_number")
    private String storeNumber;

    @Column(name = "customer_id")
    private String customerId;

}
