package com.optculture.shared.entities.GiftCard;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "gift_cards_expiry", indexes = {
        @Index(name = "HISTORYID_INDEX", columnList = "history_id"),
        @Index(name = "USERID_INDEX", columnList = "user_id"),
        @Index(name = "USERIDCARDID_INDEX", columnList = "user_id, gift_card_id"),
        @Index(name = "CARDNUMUSER_INDEX", columnList = "user_id, gift_card_number")
})
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GiftCardsExpiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expiry_id")
    private java.lang.Long expiryId;

    @Column(name = "history_id")
    private java.lang.Long historyId;

    @Column(name = "gift_card_id")
    private java.lang.Long giftCardId;

    @Column(name="gift_program_id")
    private java.lang.Long giftProgramId;

    @Column(name = "gift_card_number")
    private String giftCardNumber;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "expiry_amount")
    private java.lang.Double expiryAmount;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name="created_date")
    private LocalDateTime createdDate;
}
