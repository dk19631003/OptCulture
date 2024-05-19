package com.optculture.shared.entities.loyalty;

import jakarta.persistence.*;
@Entity
@Table(name = "loyalty_card_set")
public class LoyaltyCardSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_set_id")
    private java.lang.Long cardSetId;

    @Column(name = "card_set_name", length = 60)
    private String cardSetName;

    @Column(name = "quantity")
    private java.lang.Long quantity;

    @Column(name = "generation_type", length = 60)
    private String generationType;

    @Column(name = "status", length = 60)
    private String status;

    @Column(name = "program_id")
    private java.lang.Long programId;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "created_by", length = 60)
    private String createdBy;

    @Column(name = "modified_by", length = 60)
    private String modifiedBy;

    @Column(name = "migrated_flag")
    private char migratedFlag;

    @Column(name = "card_set_type", length = 60)
    private String cardSetType;

    @Column(name = "linked_tier_level")
    private java.lang.Integer linkedTierLevel;

    @Column(name = "card_generation_type", length = 60)
    private String cardGenerationType;

}
