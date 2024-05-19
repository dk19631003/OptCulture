package com.optculture.shared.entities.GiftCard;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "gift_cards_history", indexes = {
        @Index(name="TRXTYPE_INDEX", columnList = "transaction_type"),
        @Index(name="USER_INDEX", columnList = "user_id"),
        @Index(name = "RECPT_INDEX", columnList = "receipt_number"),
        @Index(name = "SALESID_INDEX", columnList = "sales_id"),
        @Index(name = "STORENUM_INDEX", columnList = "store_number"),
        @Index(name = "CARDIDUSER_INDEX", columnList = "user_id,gift_card_id"),
        @Index(name = "CARDNUMUSER_INDEX", columnList = "user_id,gift_card_number")
})
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GiftCardsHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private java.lang.Long historyId;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "gift_card_id")
    private java.lang.Long giftCardId;

    @Column(name="gift_program_id")
    private java.lang.Long giftProgramId;

    @Column(name = "gift_card_number")
    private String giftCardNumber;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "amount_type")
    private String amountType;

    @Column(name = "entered_amount")
    private java.lang.Double enteredAmount;

    @Column(name = "gift_difference")
    private java.lang.Double giftDifference;

    @Column(name = "gift_balance")
    private java.lang.Double giftBalance;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "item_info")
    private String itemInfo;

    @Column(name = "gifted_to_mobile")
    private java.lang.Long giftedToMobile;

    @Column(name = "gifted_to_email")
    private String giftedToEmail;

    @Column(name = "receipt_number")
    private String receiptNumber;

    @Column(name = "sales_id")
    private java.lang.Long salesId;

    @Column(name = "store_number")
    private java.lang.Long storeNumber;
}
