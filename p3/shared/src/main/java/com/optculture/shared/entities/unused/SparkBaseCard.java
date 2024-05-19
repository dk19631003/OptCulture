package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "sparkbase_cards")
public class SparkBaseCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sparkbase_card_id")
    private java.lang.Long sparkBaseCard_id;

    @Column(name = "card_id")
    private String cardId;

    @Column(name = "card_pin")
    private String cardPin;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "from_source")
    private String fromSource;

    @Column(name = "status")
    private String status;

    @Column(name = "activation_date")
    private java.util.Calendar activationDate;

    @Column(name = "comments")
    private String comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sparkbase_location_id")
    private com.optculture.shared.entities.unused.SparkBaseLocationDetails sparkBaseLocationId;

}
