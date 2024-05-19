package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "sparkbase_transaction")
public class SparkBaseTransactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "location_id")
    private String locationId;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "card_id")
    private String cardId;

    @Column(name = "type")
    private String type;

    @Column(name = "amount_entered")
    private double amountEntered;

    @Column(name = "processed_time")
    private java.util.Calendar processedTime;

    @Column(name = "difference")
    private double difference;

    @Column(name = "loyalty_balance")
    private double loyaltyBalance;

    @Column(name = "giftcard_balance")
    private double giftcardBalance;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "status")
    private String status;

    @Column(name = "contact_id")
    private long contactId;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "server_time")
    private java.util.Calendar serverTime;

    @Column(name = "trigger_ids")
    private String triggerIds;

    @Column(name = "store_number")
    private String storeNumber;

}
