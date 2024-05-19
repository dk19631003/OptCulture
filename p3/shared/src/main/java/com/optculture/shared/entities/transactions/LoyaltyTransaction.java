package com.optculture.shared.entities.transactions;

import jakarta.persistence.*;
@Entity
@Table(name = "loyalty_transaction")
public class LoyaltyTransaction {

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

    @Column(name = "status")
    private String status;

    @Column(name = "request_status")
    private String requestStatus;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "user_detail")
    private String userDetail;

    @Column(name = "doc_sid")
    private String docSID;

    @Column(name = "store_number")
    private String storeNumber;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "service_type")
    private String serviceType;

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "terminal_id")
    private String terminalId;

    @Column(name = "loyalty_service_type")
    private String loyaltyServiceType;

}
