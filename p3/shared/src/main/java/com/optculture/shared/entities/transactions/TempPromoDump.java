package com.optculture.shared.entities.transactions;

import jakarta.persistence.*;
@Entity
@Table(name = "temp_promo_dump")
public class TempPromoDump {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "item_sid")
    private String itemSid;

    @Column(name = "discount")
    private double discount;

    @Column(name = "owner_id")
    private java.lang.Long ownerId;

}
