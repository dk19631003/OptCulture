package com.optculture.shared.entities.loyalty;

import jakarta.persistence.*;
@Entity
@Table(name = "loyalty_cards")
public class LoyaltyCards {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private java.lang.Long cardId;

    @Column(name = "program_id")
    private java.lang.Long programId;

    @Column(name = "card_set_id")
    private java.lang.Long cardSetId;

    @Column(name = "card_number", length = 60)
    private String cardNumber;

    @Column(name = "card_pin", length = 60)
    private String cardPin;

    @Column(name = "org_id")
    private java.lang.Long orgId;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "status", length = 60)
    private String status;

    @Column(name = "activation_date")
    private java.util.Calendar activationDate;

    @Column(name = "registered_flag")
    private char registeredFlag;

    @Column(name = "membership_id")
    private java.lang.Long membershipId;

}
