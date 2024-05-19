package com.optculture.shared.entities.transactions;

import jakarta.persistence.*;
@Entity
@Table(name = "async_loyalty_trx")
public class AsyncLoyaltyTrx {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "trx_type", length = 30)
    private String trxType;

    @Column(name = "created_time")
    private java.util.Calendar createdTime;

    @Column(name = "processed_time")
    private java.util.Calendar processedTime;

    @Column(name = "status", length = 10)
    private String status;

    @Column(name = "status_code", length = 500)
    private String statusCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trx_id")
    private LoyaltyTransaction loyaltyTransaction;

}
