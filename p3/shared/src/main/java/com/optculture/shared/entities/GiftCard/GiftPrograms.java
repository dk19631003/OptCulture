package com.optculture.shared.entities.GiftCard;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "gift_programs", indexes = {
        @Index(name="USER_INDEX", columnList = "user_id"),
        @Index(name="REDEMTYPE_INDEX", columnList = "redemption_type")
})
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GiftPrograms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gift_program_id")
    private java.lang.Long giftProgramId;

    @Column(name = "gift_program_name")
    private String giftProgramName;

    @Column(name = "program_status")
    private String programStatus;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Column(name = "expiry_in_months")
    private java.lang.Long expiryInMonths;

    @Column(name = "redemption_type")
    private String redemptionType;

    @Column(name = "pin_length")
    private java.lang.Long pinLength;

    @Column(name = "card_type")
    private String cardType;

    // Getters and Setters
}
