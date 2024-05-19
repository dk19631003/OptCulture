package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "customer_sales_updated_data")
public class CustomerSalesUpdatedData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aggr_id")
    private java.lang.Long aggrId;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "tot_purchase_amt")
    private double totPurchaseAmt;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "basket_size")
    private double basketSize;

    @Column(name = "tot_invoice")
    private int totInvoice;

    @Column(name = "tot_visits")
    private int totVisits;

    @Column(name = "first_purchase_date")
    private java.util.Calendar firstPurchaseDate;

    @Column(name = "last_purchase_date")
    private java.util.Calendar lastPurchaseDate;

}
